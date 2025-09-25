package com.rsi.agp.core.managers.impl.anexoMod.impresion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.IEnviosSWImpresionDao;
import com.rsi.agp.dao.models.inc.IIncidenciasAgroDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.EnviosSWImpresion;
import com.rsi.agp.dao.tables.anexo.EnviosSWXML;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.AsuntosIncId;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.Motivos;

import es.agroseguro.relacionIncidencias.Incidencia;
import es.agroseguro.relacionIncidencias.RelacionIncidencias;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error;

public class ImpresionIncidenciasModManager implements IImpresionIncidenciasModManager {

	private static final Character CATALOGO_POLIZA = 'P';
	private static final String ERROR_GENERICO_KEY = "errorGenerico";
	private static final String ERROR_AGR_EXCEPTION_KEY = "errorAgrException";
	private static final String MSG_EXCEPTION = "Ocurrio un error inesperado en la llamada al SW de +"
			+ "ContratacionSCImpresionModificacion -SolicitudImpresionModificacion";
	private static final String MSG_AGR_EXCEPTION = "Ocurrio un error en la llamada al SW de +"
			+ "ContratacionSCImpresionModificacion - SolicitudImpresionModificacion";

	private static final Log logger = LogFactory.getLog(ImpresionIncidenciasModManager.class);

	private IEnviosSWImpresionDao enviosSWImpresionDao;
	private IIncidenciasAgroDao incidenciasAgroDao;
	private IPolizaDao polizaDao;

	public Map<String, Object> solicitarRelacionIncidencias(String referencia, BigDecimal plan, String realPath,
			String codUsuario) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			RelacionIncidenciasResponse solicitudImpresionModificacion = new SWImpresionModificacionHelper()
					.getSolicitudImpresionModificacion(referencia, plan, realPath);

			// Inserta la comunicacion con el SW de impresion de incidencias de Modificacion
			// en la tabla correspondiente
			insertarEnviosSWSolicitud(referencia, plan, codUsuario, solicitudImpresionModificacion, null, null);
			// Convierte la respuesta en un bean con los campos necesarios para enviar a la
			// pantalla
			parameters = impresionModificacionToBean(solicitudImpresionModificacion);

		} catch (AgrException e) {
			logger.error(MSG_AGR_EXCEPTION, e);
			parameters.put(ERROR_AGR_EXCEPTION_KEY, getMsgAgrException(e));
			return parameters;

		} catch (Exception e) {
			logger.error(MSG_EXCEPTION, e);
			parameters.put(ERROR_GENERICO_KEY, ERROR_GENERICO_KEY);
			return parameters;
		}

		return parameters;
	}

	public Map<String, Object> solicitarRelacionIncidencias(String nifcif, BigDecimal plan, BigDecimal linea,
			String realPath, String codUsuario) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {

			RelacionIncidenciasResponse solicitudImpresionModificacion = new SWImpresionModificacionHelper()
					.getSolicitudImpresionModificacion(nifcif, plan, linea, realPath);

			this.insertarEnviosSWSolicitud(null, plan, codUsuario, solicitudImpresionModificacion, nifcif, linea);
			parameters = this.impresionModificacionToBean(solicitudImpresionModificacion);

		} catch (AgrException e) {
			logger.error(MSG_AGR_EXCEPTION, e);
			parameters.put(ERROR_AGR_EXCEPTION_KEY, getMsgAgrException(e));
			return parameters;
		} catch (Exception e) {
			logger.error(MSG_EXCEPTION, e);
			parameters.put(ERROR_GENERICO_KEY, ERROR_GENERICO_KEY);
			return parameters;
		}

		return parameters;
	}

	/**
	 * Realiza la llamada al sw ContratacionSCImpresionModificacion -
	 * imprimirPdfIncidencia
	 * 
	 * @param realPath
	 * @param idCupon
	 * @param anio
	 * @param numero
	 * @return Map<String,Object> con el resultado
	 */
	public Map<String, Object> imprimirPdfIncidencia(String realPath, String idCupon, String anio, String numero) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {

			byte[] pdf = new SWImpresionModificacionHelper().getImprimirPdfIncidencia(idCupon, realPath, anio, numero);
			parameters.put("docPdf", pdf);

		} catch (AgrException e) {
			logger.error("Ocurrio un error en la llamada al SW de "
					+ "ContratacionSCImpresionModificacion - imprimirPdfIncidencia", e);
			parameters.put(ERROR_AGR_EXCEPTION_KEY, getMsgAgrException(e));
			return parameters;

		} catch (Exception e) {
			logger.error("Ocurrio un error inesperado en la llamada al SW de "
					+ "ContratacionSCImpresionModificacion - imprimirPdfIncidencia", e);
			parameters.put(ERROR_GENERICO_KEY, ERROR_GENERICO_KEY);
			return parameters;
		}
		return parameters;
	}

	/**
	 * Devuelve un mapa que contiene la lista de incidencias devuelva por el SW para
	 * la pÃ³liza indicada
	 * 
	 * @param respuesta
	 *            Objeto que encapsula la respuesta del SW
	 * @return
	 */
	private Map<String, Object> impresionModificacionToBean(RelacionIncidenciasResponse respuesta) {

		List<ImpresionIncidenciasModBean> listaIncidencias = new ArrayList<ImpresionIncidenciasModBean>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		RelacionIncidencias rp = respuesta.getRelacionIncidencias().getRelacionIncidencias();
		for (int i = 0; i < rp.getIncidenciaArray().length; i++) {
			Incidencia incidencia = rp.getIncidenciaArray(i);

			ImpresionIncidenciasModBean bean = new ImpresionIncidenciasModBean();
			bean.setAnio(incidencia.getAnio());
			bean.setNumeroInc(incidencia.getNumero());
			bean.setAsunto(incidencia.getAsunto());
			bean.setDescEstado(incidencia.getDescriptivoEstado());
			bean.setEstado(incidencia.getEstado());
			bean.setFechaEstado(incidencia.getFechaEstado().getTime());
			bean.setDescDocAfectado(incidencia.getDescriptivoDocumentoAfectado());
			bean.setCodigoDocAfectado(incidencia.getCodigoDocumentoAfectado());

			bean.setTipoPoliza(incidencia.getTipoPoliza() != null ? incidencia.getTipoPoliza().toString() : "P");

			bean.setIdEnvio(incidencia.getIdEnvio());
			bean.setNumDocumentos(incidencia.getNumeroDocumentos());
			bean.setReferencia(incidencia.getReferencia());
			bean.setCodAsunto(incidencia.getCodigoAsunto());

			listaIncidencias.add(bean);

		}
		parameters.put("listaIncidencias", listaIncidencias);
		return parameters;
	}

	/**
	 * Devuelve una cadena con los errores devueltos en una AgrException
	 * 
	 * @param exc
	 * @return
	 */
	private String getMsgAgrException(AgrException exc) {
		String msg = "";

		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				msg += error.getMensaje() + ". ";
			}
		}

		return msg;

	}

	private void insertarEnviosSWSolicitud(String referencia, BigDecimal plan, String codUsuario,
			RelacionIncidenciasResponse respuesta, String nifcif, BigDecimal linea) {

		// XML de RelacionIncidenciasPolizas
		EnviosSWXML enviosImpresion = new EnviosSWXML();

		enviosImpresion
				.setXml(Hibernate.createClob(respuesta.getRelacionIncidencias().getRelacionIncidencias().toString()));

		EnviosSWImpresion enviosSWImpresion = new EnviosSWImpresion();
		enviosSWImpresion.setCodusuario(codUsuario);
		enviosSWImpresion.setFecha(new Date());
		enviosSWImpresion.setPlan(plan);
		enviosSWImpresion.setReferencia(referencia);
		enviosSWImpresion.setEnviosSWXML(enviosImpresion);
		enviosSWImpresion.setNifcif(nifcif);
		enviosSWImpresion.setLinea(linea);

		try {
			enviosSWImpresionDao.saveOrUpdate(enviosSWImpresion);
		} catch (DAOException e) {
			logger.error(
					"Error al insertar el registro de la comunicacion con el SW de Impresion de incidencias de Modificacion",
					e);
		}
	}

	public Map<String, Object> guardarIncidencia(Incidencias incidenciaVista, AsuntosInc asuntoVista,
			DocsAfectadosInc docAfectadosVista) throws BusinessException {

		Map<String, Object> parametros = new HashMap<String, Object>();
		try {

			DatosAdiccionalesPoliza datosAdiccionalesPoliza = this.obtenerDatosAdiccionalesPoliza(
					incidenciaVista.getReferencia(), incidenciaVista.getTiporef(), incidenciaVista.getCodplan());

			// * MODIF TAM (18.09.2018) - Resolución Incidencias RGA - Inicio */
			String referenciaPoliza = incidenciaVista.getReferencia();
			Character tipoReferencia = incidenciaVista.getTiporef();
			BigDecimal plan = incidenciaVista.getCodplan();

			BigDecimal linea = this.polizaDao.obtenerLineaPoliza(referenciaPoliza, tipoReferencia, plan);

			if (referenciaPoliza != null) {
				if (!StringUtils.isNullOrEmpty(referenciaPoliza)) {
					BigDecimal dc = this.incidenciasAgroDao.getDCPoliza(referenciaPoliza, tipoReferencia, plan, linea);
					incidenciaVista.setDc(dc);
				}
			}
			// * MODIF TAM (18.09.2018) - Resolución Incidencias RGA - Fin */

			incidenciaVista.setCodlinea(datosAdiccionalesPoliza.getLinea());
			incidenciaVista.setCodplan(datosAdiccionalesPoliza.getPlan());
			incidenciaVista.setNifaseg(datosAdiccionalesPoliza.getNifCif());

			AsuntosInc asuntoBD = this.obtenerAsuntoBD(asuntoVista);
			incidenciaVista.setAsuntosInc(asuntoBD);
			asuntoBD.getIncidenciases().add(incidenciaVista);

			DocsAfectadosInc docAfectadosDB = this.ObtenerDocAfectadosDB(docAfectadosVista);
			incidenciaVista.setDocsAfectadosInc(docAfectadosDB);
			docAfectadosDB.getIncidenciases().add(incidenciaVista);

			EstadosInc estadoBorrador = (EstadosInc) this.incidenciasAgroDao.getObject(EstadosInc.class, 'B');
			estadoBorrador.getIncidenciases().add(incidenciaVista);
			incidenciaVista.setEstadosInc(estadoBorrador);

			/* Pet. 57627 ** MODIF TAM (14.11.2019) */
			/* Damos de alta la incidencia con el codmotivo nulo */
			Motivos motivoVista = new Motivos();
			motivoVista.setcodmotivo(0);
			Motivos motivoBD = this.obtenerMotivosBD(motivoVista);
			incidenciaVista.setmotivos(motivoBD);
			incidenciaVista.setTipoinc('I');
			/* Pet. 57627 ** MODIF TAM (14.11.2019) Fin */

			Incidencias incidencia = (Incidencias) this.incidenciasAgroDao.saveOrUpdate(incidenciaVista);
			parametros.put("incidenciaId", incidencia.getIdincidencia());
		} catch (DAOException e) {
			logger.error("No se ha guardado la incidencia", e);
			parametros.put("alerta", "No se ha guardado la incidencia");
			throw new BusinessException();
		}
		return parametros;
	}

	private AsuntosInc obtenerAsuntoBD(AsuntosInc asuntoVista) throws DAOException {
		AsuntosInc asuntoBD = (AsuntosInc) this.incidenciasAgroDao.getObject(AsuntosInc.class,
				new AsuntosIncId(asuntoVista.getId().getCodasunto(), CATALOGO_POLIZA));
		if (asuntoBD == null) {
			this.incidenciasAgroDao.saveOrUpdate(asuntoVista);
			asuntoBD = asuntoVista;
		}
		return asuntoBD;
	}

	/* Pet. 57627 ** MODIF TAM (14.11.2019) */
	private Motivos obtenerMotivosBD(Motivos motivoVista) throws DAOException {
		Motivos motivoBD = (Motivos) this.incidenciasAgroDao.getObject(Motivos.class, motivoVista.getCodmotivo());
		if (motivoBD == null) {
			this.incidenciasAgroDao.saveOrUpdate(motivoVista);
			motivoBD = motivoVista;
		}
		return motivoBD;
	}
	/* Pet. 57627 ** MODIF TAM (14.11.2019) */

	private DatosAdiccionalesPoliza obtenerDatosAdiccionalesPoliza(String referenciaPoliza, Character tipoReferencia,
			BigDecimal codPlan) throws DAOException {
		logger.debug("**@@**ImpresionIncidenciasModManager - obtenerDatosAdicionalesPoliza");
		logger.debug("**@@**Dentro de obtenerDatosAdicionalesPoliza - Valor de codPlan:" + codPlan);

		BigDecimal linea = this.polizaDao.obtenerLineaPoliza(referenciaPoliza, tipoReferencia, codPlan);
		// BigDecimal plan = this.polizaDao.obtenerPlanPoliza(referenciaPoliza,
		// tipoReferencia);
		BigDecimal plan = codPlan;
		String nifCif = this.polizaDao.obtenerNifCifDesdeReferenciaPoliza(referenciaPoliza, tipoReferencia, codPlan);

		DatosAdiccionalesPoliza datosAdiccionalesPoliza = new DatosAdiccionalesPoliza(linea, plan, nifCif);
		return datosAdiccionalesPoliza;
	}

	private class DatosAdiccionalesPoliza {
		private BigDecimal linea;
		private BigDecimal plan;
		private String nifCif;

		public DatosAdiccionalesPoliza(BigDecimal linea, BigDecimal plan, String nifCif) {
			super();
			this.linea = linea;
			this.plan = plan;
			this.nifCif = nifCif;
		}

		public BigDecimal getLinea() {
			return linea;
		}

		public BigDecimal getPlan() {
			return plan;
		}

		public String getNifCif() {
			return nifCif;
		}
	}

	public boolean consultaAseguradoBloqueado(BigDecimal plan, BigDecimal linea, String referencia, String nifcif) {

		return this.incidenciasAgroDao.getEstadoAsegurado(plan, linea, referencia, nifcif);
	}

	private DocsAfectadosInc ObtenerDocAfectadosDB(DocsAfectadosInc docAfectadosVista) {
		return (DocsAfectadosInc) this.incidenciasAgroDao.getObject(DocsAfectadosInc.class,
				docAfectadosVista.getCoddocafectado());
	}

	public void setEnviosSWImpresionDao(IEnviosSWImpresionDao enviosSWImpresionDao) {
		this.enviosSWImpresionDao = enviosSWImpresionDao;
	}

	public void setIncidenciasAgroDao(IIncidenciasAgroDao incidenciasAgroDao) {
		this.incidenciasAgroDao = incidenciasAgroDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
}
