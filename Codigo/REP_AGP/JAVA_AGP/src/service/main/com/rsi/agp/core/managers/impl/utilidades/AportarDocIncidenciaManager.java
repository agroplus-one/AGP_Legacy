package com.rsi.agp.core.managers.impl.utilidades;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.SWImpresionModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.inc.IAportarDocIncidenciaDao;
import com.rsi.agp.dao.models.inc.IIncidenciasAgroDao;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.AsuntosIncId;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.DocumentacionIncForm;
import com.rsi.agp.dao.tables.inc.DocumentosInc;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.IncidenciasHist;
import com.rsi.agp.dao.tables.inc.LlamadaWSInc;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.inc.TiposDocInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.acuseRecibo.Error;
import es.agroseguro.relacionincidencias.Incidencia;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AnioIncidencia;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionIncidenciaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.EnvioDocumentacionNuevaIncidenciaRequest;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PlanLineaNif;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PlanReferenciaTipo;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class AportarDocIncidenciaManager {

	private static final Character CATALOGO_POLIZA = 'P';
	protected final Log logger = LogFactory.getLog(AportarDocIncidenciaManager.class);

	private static final BigDecimal COD_ESTADO_ACTIVO = new BigDecimal("1");
	private static final BigDecimal COD_ESTADO_ERRONEO = new BigDecimal("0");
	private static final String SW_INC = "envioDocumentacionIncidencia";
	private static final String SW_NUEVA_INC = "envioDocumentacionNuevaIncidencia";
	private static final Character ESTADO_BORRADOR = 'B';
	private static final Character REF_POLIZA = 'P';
	private static final String ENVIO_POLIZA = "p";
	private static final String ENVIO_INCIDENCIA = "i";
	private static final String ENVIO_ANEXO = "am";
	private static final String ENVIO_ASEGURADO = "aseg";

	private IAportarDocIncidenciaDao aportarDocIncidenciaDao;
	private SeleccionPolizaManager seleccionPolizaManager;
	/* Pet. 67219 ** MODIF TAM (27.01.2021) ** Inicio */
	private IIncidenciasAgroDao incidenciasAgroDao;

	public DocumentacionIncForm generarDocumentacionIncForm(Long idIncidencia) throws BusinessException {
		DocumentacionIncForm docIncForm = new DocumentacionIncForm();
		docIncForm.setIncidencias(this.getIncidencia(idIncidencia));
		return docIncForm;
	}

	public Long guardarDocumento(DocumentacionIncForm form) throws BusinessException, IOException {
		Long idIncidencia = form.getIncidencias().getIdincidencia();
		logger.debug("**AportarDocIncidenciaManager-guardarDocumento");

		try {
			if (idIncidencia == null) {
				form = this.guardarIncidencia(form);
				idIncidencia = form.getIncidencias().getIdincidencia();
			}
			String nombreArchivo = form.getFile().getOriginalFilename();
			String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1, nombreArchivo.length());
			logger.debug("**AportarDocIncidenciaManager-guardarDocumento. Valor de extension(1):*" + extension + "*");
			extension = extension.toLowerCase();
			logger.debug("**AportarDocIncidenciaManager-guardarDocumento. Valor de extension(2):*" + extension + "*");
			if (this.comprobarExtensionArchivo(extension)) {
				logger.debug("**AportarDocIncidenciaManager-guardarDocumento. Antes de generarDocumentoInc*");
				DocumentosInc documentosInc = generarDocumentoInc(form, nombreArchivo, extension);
				this.aportarDocIncidenciaDao.saveOrUpdate(documentosInc);
			}
		} catch (DAOException e) {
			throw new BusinessException();
		}
		return idIncidencia;
	}

	private DocumentacionIncForm guardarIncidencia(DocumentacionIncForm form) throws BusinessException {
		try {
			Incidencias incidencia = new Incidencias();

			BigDecimal lineaPoliza = form.getIncidencias().getCodlinea();
			String referencia = form.getIncidencias().getReferencia();
			Character tipoReferencia = form.getIncidencias().getTiporef();
			BigDecimal codPlan = form.getIncidencias().getCodplan();
			String nifCif = form.getIncidencias().getNifaseg();

			if (referencia != null) {
				lineaPoliza = this.aportarDocIncidenciaDao.getLineaPoliza(referencia, codPlan, tipoReferencia);
				nifCif = this.aportarDocIncidenciaDao.getNifCifPoliza(referencia, tipoReferencia, codPlan);
				if (!StringUtils.isNullOrEmpty(referencia)) {
					BigDecimal dc = this.aportarDocIncidenciaDao.getDCPoliza(referencia, tipoReferencia, codPlan,
							lineaPoliza);
					incidencia.setDc(dc);
				}
			} else {
				tipoReferencia = new Character(' ');
			}

			// MODIF TAM (27.09.2018) ** Inicio //
			// comprobamos que la línea y el nif no sean nulos//
			if (lineaPoliza == null) {
				lineaPoliza = new BigDecimal("0");
			}
			if (StringUtils.isNullOrEmpty(nifCif)) {
				nifCif = "  ";
			}
			// MODIF TAM (27.09.2018) ** Fin //

			incidencia.setTiporef(tipoReferencia);
			incidencia.setReferencia(referencia);
			incidencia.setNifaseg(nifCif);
			incidencia.setReferencia(referencia);
			incidencia.setCodplan(codPlan);
			incidencia.setCodlinea(lineaPoliza);

			incidencia.setTipoalta(form.getIncidencias().getTipoalta());
			incidencia.setObservaciones(form.getIncidencias().getObservaciones());

			String codAsunto = form.getIncidencias().getAsuntosInc().getId().getCodasunto();
			AsuntosInc asunto = this.getAsunto(codAsunto);
					//(AsuntosInc) this.aportarDocIncidenciaDao.get(AsuntosInc.class, codAsunto);
			incidencia.setAsuntosInc(asunto);
			asunto.getIncidenciases().add(incidencia);

			EstadosInc estadoBorrador = (EstadosInc) this.aportarDocIncidenciaDao.getObject(EstadosInc.class,
					ESTADO_BORRADOR);
			incidencia.setEstadosInc(estadoBorrador);
			estadoBorrador.getIncidenciases().add(incidencia);

			DocsAfectadosInc docsAfectadosInc = new DocsAfectadosInc();
			docsAfectadosInc.setCoddocafectado(REF_POLIZA);
			incidencia.setDocsAfectadosInc(docsAfectadosInc);
			docsAfectadosInc.getIncidenciases().add(incidencia);

			incidencia.setFechaestado(new Date());
			incidencia.setFechaestadoagro(new Date());
			/* Pet. 57627 ** MODIF TAM (14.11.2019) */
			/* Damos de alta la incidencia con el codmotivo nulo */
			int codmotivo = 0;
			Motivos motivo = (Motivos) this.aportarDocIncidenciaDao.get(Motivos.class, codmotivo);
			incidencia.setmotivos(motivo);
			incidencia.setTipoinc('I');
			/* Pet. 57627 ** MODIF TAM (14.11.2019) Fin */

			incidencia.setCodestado(Constants.ESTADO_INC_LIMBO);

			incidencia = (Incidencias) this.aportarDocIncidenciaDao.saveOrUpdate(incidencia);

			form.setIncidencias(incidencia);

			return form;
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	private DocumentosInc generarDocumentoInc(DocumentacionIncForm form, String nombreArchivo, String extension)
			throws DAOException, IOException {
		String observaciones_aux = form.getIncidencias().getObservaciones();
		Incidencias incidencia = this.getIncidencia(form.getIncidencias().getIdincidencia());
		logger.debug("**AportarDocIncidenciaManager-generarDocumento. Antes de getExtension");
		TiposDocInc TiposDocInc = this.aportarDocIncidenciaDao.getExtension(extension);
		DocumentosInc documentosInc = new DocumentosInc();
		incidencia.setObservaciones(observaciones_aux);
		incidencia.setTipoalta(form.getIncidencias().getTipoalta());
		/* MODIF TAM (02.10.2018) ** Inicio */
		/* Guardamos el valor del asunto insertado por pantalla */
		incidencia.setAsuntosInc(form.getIncidencias().getAsuntosInc());

		documentosInc.setIncidencias(incidencia);
		documentosInc.setTiposDocInc(TiposDocInc);
		logger.debug("**AportarDocIncidenciaManager-generarDocumento. Antes de generarNombreValido");
		documentosInc.setNombre(generarNombreValido(nombreArchivo));
		logger.debug("**AportarDocIncidenciaManager-generarDocumento. Antes de createBlob");
		documentosInc.setFichero(Hibernate.createBlob(form.getFile().getInputStream()));
		logger.debug("**AportarDocIncidenciaManager-generarDocumento. Antes de añadir");
		incidencia.getDocumentosIncs().add(documentosInc);
		logger.debug("**AportarDocIncidenciaManager-generarDocumento. Despues de añadir");
		return documentosInc;
	}

	private static String generarNombreValido(String nombreConExtension) {
		String nombre = nombreConExtension.substring(0, nombreConExtension.lastIndexOf('.') + 1);
		return nombre.length() < 50 ? nombre : nombre.substring(0, 49);
	}

	private Boolean comprobarExtensionArchivo(String extensionArchivo) throws DAOException {
		List<TiposDocInc> extensionesValidas = this.aportarDocIncidenciaDao.getExtensionesFicherosValidas();
		boolean valido = false;
		for (TiposDocInc tipos : extensionesValidas) {
			if (tipos.getExtension().equals(extensionArchivo)) {
				valido = true;
				break;
			}
		}
		return valido;
	}

	/* MODIF TAM (29.08.2018) ** Inicio **/
	/*
	 * Comprobamos si la línea de la poliza que estamos tratando es de ganado o no
	 */
	private Boolean comprobarIsLineaGanado(String tipoEnvio, DocumentacionIncForm form, BigDecimal lineaPoliza)
			throws DAOException {

		Boolean isLinGanado = false;

		if (ENVIO_POLIZA.equals(tipoEnvio)) {
			String referencia = form.getIncidencias().getReferencia();
			BigDecimal codPlan = form.getIncidencias().getCodplan();
			Character tipoReferencia = form.getIncidencias().getTiporef();

			if (referencia != null) {
				if (!StringUtils.isNullOrEmpty(referencia) && lineaPoliza != null) {
					// Nos declaramos una nueva función en el Dao.java para recuperar el id de la
					// poliza con la referencia codplan, tipo referencia linea y referencia
					BigDecimal idPol = this.aportarDocIncidenciaDao.getIdPoliza(referencia, tipoReferencia, codPlan,
							lineaPoliza);
					Long idPoliza;
					idPoliza = idPol.longValue();

					// luego obtenemos el objeto polizaBean
					Poliza p = seleccionPolizaManager.getPolizaById(idPoliza);
					// Y comprobamos si es una línea de ganado.
					if (p.getLinea().isLineaGanado()) {
						isLinGanado = true;
					}
				}
			}
		}
		return isLinGanado;
	}

	public List<DocumentosInc> obtenerDocumentos(Long idIncidencia) throws BusinessException {
		try {
			return this.aportarDocIncidenciaDao.getDocumentos(idIncidencia);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	public void borrarIncidencia(Long idIncidencia) throws BusinessException {
		try {
			this.aportarDocIncidenciaDao.delete(Incidencias.class, idIncidencia);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	private void guardarHistorico(Incidencias inc, String codUsuario) throws DAOException {
		IncidenciasHist hist = new IncidenciasHist();
		hist.setAnhoincidencia(inc.getAnhoincidencia());
		hist.setAsuntosInc(inc.getAsuntosInc());
		hist.setCodestado(inc.getCodestado());
		hist.setCodlinea(inc.getCodlinea());
		hist.setCodplan(inc.getCodplan());
		hist.setDc(inc.getDc());
		hist.setDocsAfectadosInc(inc.getDocsAfectadosInc());
		hist.setEstadosInc(inc.getEstadosInc());
		hist.setFechaestado(inc.getFechaestado());
		hist.setFechaestadoagro(inc.getFechaestadoagro());
		hist.setIdenvio(inc.getIdenvio());
		hist.setNifaseg(inc.getNifaseg());
		hist.setNumdocumentos(inc.getNumdocumentos());
		hist.setNumincidencia(inc.getNumincidencia());
		hist.setObservaciones(inc.getObservaciones());
		hist.setReferencia(inc.getReferencia());
		hist.setTimestamp(new Date());
		hist.setTiporef(inc.getTiporef());
		hist.setUsuario(codUsuario);

		inc.getIncidenciasHists().add(hist);
		hist.setIncidencias(inc);
		/* Pet. 57627 ** MODIF TAM (14.11.2019) */
		hist.setmotivos(inc.getmotivos());

		this.aportarDocIncidenciaDao.saveOrUpdate(hist);
	}

	public List<TiposDocInc> obtenerExtensionesValidas() throws BusinessException {
		try {
			return this.aportarDocIncidenciaDao.getExtensionesFicherosValidas();
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	public List<AsuntosInc> obtenerAsuntos() throws BusinessException {
		try {
			return this.aportarDocIncidenciaDao.getAsuntos();
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	public List<Motivos> obtenerMotivos() throws BusinessException {
		try {
			return this.aportarDocIncidenciaDao.getMotivos();
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	public void eliminarDocumento(Long idDocumento) throws BusinessException {
		try {
			this.aportarDocIncidenciaDao.delete(DocumentosInc.class, idDocumento);
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	public Long altaIncidencia() throws BusinessException {
		try {
			Incidencias incidencia = new Incidencias();
			incidencia.setCodestado(Constants.ESTADO_INC_LIMBO);
			incidencia = (Incidencias) this.aportarDocIncidenciaDao.saveOrUpdate(incidencia);
			incidencia.setFechaestado(new Date());
			return incidencia.getIdincidencia();
		} catch (DAOException e) {
			throw new BusinessException();
		}
	}

	private List<Base64Binary> generarDocumentosEnvioAgroseguro(Set<DocumentosInc> documentos) throws Exception {
		List<Base64Binary> base64DocList = new ArrayList<Base64Binary>();
		for (DocumentosInc doc : documentos) {
			Base64Binary base64Doc = new Base64Binary();
			Blob fichero = doc.getFichero();
			byte[] fileBytes = fichero.getBytes(1, Integer.parseInt(String.valueOf(fichero.length())));
			base64Doc.setContentType(doc.getTiposDocInc().getMimeType());
			base64Doc.setValue(fileBytes);
			base64DocList.add(base64Doc);
		}
		return base64DocList;
	}

	public Map<String, Object> enviarDocAgroseguro(String realPath, DocumentacionIncForm form, String tipoBusqueda,
			String codUsuario) throws Exception {
		Long idIncidencia = form.getIncidencias().getIdincidencia();
		Incidencias incidencia = (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
		Map<String, Object> parametros = new HashMap<String, Object>();
		AcuseReciboDocument acuseReciboDocument = null;
		EnvioDocumentacionIncidenciaRequest wsReq = null;
		BigDecimal lineaInc = incidencia.getCodlinea();

		try {

			List<Base64Binary> listaDocumentos = this.generarDocumentosEnvioAgroseguro(incidencia.getDocumentosIncs());
			wsReq = this.crearWSRequestDocIncidencia(tipoBusqueda, lineaInc, listaDocumentos, form);
			acuseReciboDocument = new SWImpresionModificacionHelper().envioDocumentacionIncidencia(realPath, wsReq);
			String Error = " ";
			this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);
			parametros = this.erroresAcuseRecibo(acuseReciboDocument.getAcuseRecibo());
			Incidencia incidenciaAgroseguro = this.obtenerNuevosDatosIncidencia(acuseReciboDocument.getAcuseRecibo());

			if (incidenciaAgroseguro != null) {
				actualizarIncidenciaBD(idIncidencia, incidenciaAgroseguro);
			} else {
				/* MODIF TAM (27.08.2018) - Inicio */
				/*
				 * Si venimos de Aportar Documentación, la incidencia no es nueva y el acuse de
				 * recibo del WS no recibe Datos Asociados, por lo tanto si tenemos la
				 * incidencia informada, actualizamos unicament el estado de la misma.
				 */
				if (incidencia != null) {
					Long idIncidencia_aux = incidencia.getIdincidencia();

					Incidencias incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class,
							idIncidencia_aux);
					incidenciaBD.setCodestado(COD_ESTADO_ACTIVO);

					/* Resolución incidencia MODIF TAM (17.09.2018) * Inicio */
					/*
					 * En este caso como el estado Agroplus no viene informado en el acuse de
					 * Recibo, se ha especificado por parte de RGA /* que el valor que se debe
					 * asignar es directamente el estado "En revisión Admin." "E"
					 */
					Character codEstado = 'E';

					EstadosInc estadoInc = (EstadosInc) this.aportarDocIncidenciaDao.getObject(EstadosInc.class,
							codEstado);
					incidenciaBD.setEstadosInc(estadoInc);
					estadoInc.getIncidenciases().add(incidenciaBD);
					/* Resolución incidencia MODIF TAM (17.09.2018) * Fin */

					/* Pet. 57627 ** MODIF TAM (14.11.2019) */
					/* Damos de alta la incidencia con el codmotivo nulo */
					int codmotivo = 0;
					Motivos motivo = (Motivos) this.aportarDocIncidenciaDao.get(Motivos.class, codmotivo);
					incidencia.setmotivos(motivo);
					incidencia.setTipoinc('I');
					/* Pet. 57627 ** MODIF TAM (14.11.2019) Fin */

					incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
					/* MODIF TAM (27.08.2018) - Fin */

				}

			}
		} catch (AgrException e) {
			String Error = procesarAgrException(e);

			/* Guardamos el XML con el error retornado */
			this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);
			/* Actualizamos el estado de la incidencia como "Enviada Errónea" */
			this.actualizarIncidenciaBDError(idIncidencia);
			/* Guardamos registro en la tabla de histórico */
			this.guardarHistorico(incidencia, codUsuario);
			throw e;
		}

		return parametros;
	}

	private static String procesarAgrException(AgrException e) {
		StringBuilder msg = new StringBuilder();
		if (e.getFaultInfo() != null && e.getFaultInfo().getError() != null) {
			List<es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error> errores = e.getFaultInfo()
					.getError();
			for (es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error error : errores) {
				msg.append(error.getMensaje()).append("\n");
			}
		}
		return msg.toString();
	}

	public Map<String, Object> enviarDocAgroseguroNuevo(String realPath, DocumentacionIncForm form, String tipoBusqueda,
			String codUsuario) throws Exception {
		SWImpresionModificacionHelper servicioWeb = new SWImpresionModificacionHelper();
		Long idIncidencia = form.getIncidencias().getIdincidencia();
		Incidencias incidencia = (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);

		BigDecimal lineaInc = incidencia.getCodlinea();

		List<Base64Binary> listaDocumentos = this.generarDocumentosEnvioAgroseguro(incidencia.getDocumentosIncs());
		Map<String, Object> parametros = new HashMap<String, Object>();
		AcuseReciboDocument acuseReciboDocument = null;
		if (form.getIncidencias().getNumincidencia() != null
				&& form.getIncidencias().getNumincidencia() != BigDecimal.ZERO) {
			EnvioDocumentacionIncidenciaRequest wsReq = null;
			try {
				wsReq = this.crearWSRequestDocIncidencia(tipoBusqueda, lineaInc, listaDocumentos, form);
				acuseReciboDocument = servicioWeb.envioDocumentacionIncidencia(realPath, wsReq);
				String Error = "";
				this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);

			} catch (AgrException e) {
				String Error = procesarAgrException(e);

				/* Guardamos el XML con el error retornado */
				this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);
				/* Actualizamos el estado de la incidencia como "Enviada Errónea" */
				this.actualizarIncidenciaBDError(idIncidencia);
				/* Guardamos registro en la tabla de histórico */
				this.guardarHistorico(incidencia, codUsuario);
				throw e;
			}
		} else {
			EnvioDocumentacionNuevaIncidenciaRequest wsReq = null;
			try {
				wsReq = this.crearWSRequestDocNuevaIncidencia(tipoBusqueda, lineaInc, listaDocumentos, form);
				acuseReciboDocument = servicioWeb.envioDocumentacionNuevaIncidencia(realPath, wsReq);
				String Error = " ";
				this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);
			} catch (AgrException e) {
				String Error = procesarAgrException(e);

				/* Guardamos el XML con el error retornado */
				this.guardarXmlIncidencia(wsReq, acuseReciboDocument, codUsuario, incidencia, Error);
				/* Actualizamos el estado de la incidencia como "Enviada Errónea" */
				this.actualizarIncidenciaBDError(idIncidencia);
				/* Guardamos registro en la tabla de histórico */
				this.guardarHistorico(incidencia, codUsuario);
				throw e;
			}

		}

		Map<String, Object> erroresAcuseRecibo = this.erroresAcuseRecibo(acuseReciboDocument.getAcuseRecibo());
		parametros.putAll(erroresAcuseRecibo);

		Incidencia incidenciaAgroseguro = this.obtenerNuevosDatosIncidencia(acuseReciboDocument.getAcuseRecibo());

		if (incidenciaAgroseguro != null) {
			incidencia = this.actualizarIncidenciaBD(idIncidencia, incidenciaAgroseguro);
		}
		this.guardarHistorico(incidencia, codUsuario);
		return parametros;
	}

	private void guardarXmlIncidencia(Object request, AcuseReciboDocument acuseReciboDocument, String codUsuario,
			Incidencias incidencia, String Error) throws DAOException {
		String xmlEnvio = "";
		String swUtilizado = "";
		if (request instanceof EnvioDocumentacionNuevaIncidenciaRequest) {
			xmlEnvio = xmlEnvioIncidenciaNueva((EnvioDocumentacionNuevaIncidenciaRequest) request);
			swUtilizado = SW_NUEVA_INC;
		} else {
			xmlEnvio = xmlEnvioIncidencia((EnvioDocumentacionIncidenciaRequest) request);
			swUtilizado = SW_INC;
		}
		LlamadaWSInc llamadaWS = new LlamadaWSInc();
		llamadaWS.setTimestamp(new Date());
		llamadaWS.setSwutilizado(swUtilizado);
		llamadaWS.setXmlenviado(Hibernate.createClob(xmlEnvio));
		/* MODIF TAM (04.10.2018) ** Inicio */
		if (acuseReciboDocument != null) {
			llamadaWS.setXmlrecibido(Hibernate.createClob(acuseReciboDocument.toString()));
		} else {

			llamadaWS.setXmlrecibido(Hibernate.createClob(Error));
		}

		llamadaWS.setUsuario(codUsuario);
		llamadaWS.setIncidencias(incidencia);
		this.aportarDocIncidenciaDao.saveOrUpdate(llamadaWS);
	}

	private Incidencias actualizarIncidenciaBD(Long idIncidencia, Incidencia incidenciaAgroseguro) throws DAOException {

		logger.debug("***INIT actualizarIncidenciaBD...");
		Incidencias incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class,
				idIncidencia);

		Character codEstado = incidenciaAgroseguro.getEstado().charAt(0);

		EstadosInc estadoInc = (EstadosInc) this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
		incidenciaBD.setEstadosInc(estadoInc);
		estadoInc.getIncidenciases().add(incidenciaBD);

		incidenciaBD.setAnhoincidencia(new BigDecimal(incidenciaAgroseguro.getAnio()));
		incidenciaBD.setNumincidencia(new BigDecimal(incidenciaAgroseguro.getNumero()));
		incidenciaBD.setCodestado(COD_ESTADO_ACTIVO);

		/* Pet. 57627 ** MODIF TAM (14.11.2019) */
		/* Damos de alta la incidencia con el codmotivo nulo */
		int codmotivo = 0;
		Motivos motivo = (Motivos) this.aportarDocIncidenciaDao.get(Motivos.class, codmotivo);
		incidenciaBD.setmotivos(motivo);
		incidenciaBD.setTipoinc('I');
		/* Pet. 57627 ** MODIF TAM (14.11.2019) Fin */
		logger.debug("**AportarDocIncidenciaManager-actualizarIncidenciaBD: ");
		logger.debug("Referencia: " + incidenciaBD.getReferencia() + ", Plan: " + incidenciaBD.getCodplan()
				+ ", TipoRef: " + incidenciaBD.getTiporef());

		/* PRUEBAS PARA TEST DNF 19/02/2020 ESC-8399 */
		logger.debug("PRUEBA actualizarIncidenciaBD: ");
		VistaIncidenciasAgro via = this.aportarDocIncidenciaDao.getPlanRefTipoRefFromIncidenciaById(idIncidencia);
		logger.debug("referencia: " + via.getReferencia());
		logger.debug("plan: " + via.getCodplan());
		logger.debug("tiporef: " + via.getTiporef());
		/* FIN PRUEBAS PARA TEST DNF 19/02/2020 ESC-8399 */

		incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);

		logger.debug("***plan de la incidenciBD:" + incidenciaBD.getCodplan());

		return incidenciaBD;
	}

	private Incidencias actualizarIncidenciaBDError(Long idIncidencia) throws DAOException {
		Incidencias incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class,
				idIncidencia);

		incidenciaBD.setCodestado(COD_ESTADO_ERRONEO);
		BigDecimal anhoincidencia = BigDecimal.ZERO;

		if (incidenciaBD.getAnhoincidencia() == null) {
			incidenciaBD.setAnhoincidencia(anhoincidencia);
		}
		if (incidenciaBD.getNumincidencia() == null) {
			incidenciaBD.setNumincidencia(anhoincidencia);
		}

		/* Actualizamos el estadoAgroseguro con el valor "Rechazada" */
		Character codEstado = 'R';
		EstadosInc estadoInc = (EstadosInc) this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
		incidenciaBD.setEstadosInc(estadoInc);
		estadoInc.getIncidenciases().add(incidenciaBD);

		/* Pet. 57627 ** MODIF TAM (14.11.2019) */
		/* Damos de alta la incidencia con el codmotivo nulo */
		int codmotivo = 0;
		Motivos motivo = (Motivos) this.aportarDocIncidenciaDao.get(Motivos.class, codmotivo);
		incidenciaBD.setmotivos(motivo);
		incidenciaBD.setTipoinc('I');
		/* Pet. 57627 ** MODIF TAM (14.11.2019) Fin */

		incidenciaBD = (Incidencias) this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
		return incidenciaBD;
	}

	private Incidencia obtenerNuevosDatosIncidencia(AcuseRecibo acuseRecibo) throws XmlException {
		Incidencia incidencia = null;
		Documento[] documentos = acuseRecibo.getDocumentoArray();
		for (Documento doc : documentos) {
			DatosAsociados datosAsociados = doc.getDatosAsociados();
			if (datosAsociados != null) {
				Node currNode = datosAsociados.getDomNode().getFirstChild();
				if (currNode.getNodeType() == Node.ELEMENT_NODE) {
					// HAGO ESTA "GUARRADA" YA QUE, POR ALGUN RAZÓN QUE NO ENTIENDO, LA FORMA CHACHI
					// NO SÉ POR QUÉ NO FUNCIONA
					// incidencia = Incidencia.Factory.parse(currNode);
					Element element = (Element) currNode;
					int anioInc = Integer.parseInt(element.getAttribute("anio"));
					BigInteger numeroInc = new BigInteger(element.getAttribute("numero"));
					String estadoInc = element.getAttribute("estado");
					incidencia = new Incidencia();
					incidencia.setAnio(anioInc);
					incidencia.setNumero(numeroInc);
					incidencia.setEstado(estadoInc);
				}
			}
		}
		return incidencia;
	}

	private Map<String, Object> erroresAcuseRecibo(AcuseRecibo acuseRecibo) throws XmlException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		Documento[] documentos = acuseRecibo.getDocumentoArray();
		StringBuilder alerta = new StringBuilder();
		for (Documento doc : documentos) {
			Error[] errores = doc.getErrorArray();
			for (Error error : errores) {
				String descripcion = error.getDescripcion();
				String codigo = error.getCodigo();
				StringBuilder sb = new StringBuilder(codigo).append(" - ").append(descripcion);
				logger.error(sb.toString());
				alerta.append(descripcion).append("\n");
			}
		}
		if (alerta.length() > 0) {
			parametros.put("alerta", alerta.toString());
		}
		return parametros;
	}

	private EnvioDocumentacionNuevaIncidenciaRequest crearWSRequestDocNuevaIncidencia(String tipoEnvio,
			BigDecimal lineaInc, List<Base64Binary> listaDocumentos, DocumentacionIncForm form) {
		EnvioDocumentacionNuevaIncidenciaRequest wsReq = new EnvioDocumentacionNuevaIncidenciaRequest();
		wsReq.getDocumento().addAll(listaDocumentos);
		wsReq.setCodigoAsunto(form.getIncidencias().getAsuntosInc().getId().getCodasunto());

		/* MODIF TAM (29.08.2018) ** Inicio */
		/* Comprobamos si es una línea de Ganado */
		Boolean isLineaGanado = false;

		try {
			isLineaGanado = comprobarIsLineaGanado(tipoEnvio, form, lineaInc);
		} catch (DAOException e) {
			logger.error("Ocurrio un error al comprobar si la poliza es de la línea de Ganado", e);
		}
		/* MODIF TAM (29.08.2018) ** Inicio */

		if (ENVIO_ASEGURADO.equals(tipoEnvio)) {
			PlanLineaNif planLineaNif = new PlanLineaNif();
			planLineaNif.setLinea(form.getIncidencias().getCodlinea().intValue());
			planLineaNif.setNif(form.getIncidencias().getNifaseg());
			planLineaNif.setPlan(form.getIncidencias().getCodplan().intValue());
			wsReq.setPlanLineaNif(planLineaNif);
		} else if (ENVIO_POLIZA.equals(tipoEnvio)) {
			PlanReferenciaTipo planReferenciaTipo = new PlanReferenciaTipo();
			planReferenciaTipo.setPlan(form.getIncidencias().getCodplan().intValue());
			planReferenciaTipo.setReferencia(form.getIncidencias().getReferencia());
			// MODIF TAM (28.08.2018) ** Inicio ///
			// Hay que incluir una validación para las polizas que son de ganado
			// ya que el tipo de referencia siempre debe ser 'P'
			if (!isLineaGanado) {
				if (REF_POLIZA.equals(form.getIncidencias().getTiporef())) {
					planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.P);
				} else {
					planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.C);
				}
			} else {
				planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.P);
			}

			wsReq.setPlanReferenciaTipo(planReferenciaTipo);
		}
		wsReq.setObservaciones(form.getIncidencias().getObservaciones());
		return wsReq;
	}

	private EnvioDocumentacionIncidenciaRequest crearWSRequestDocIncidencia(String tipoEnvio, BigDecimal lineaInc,
			List<Base64Binary> listaDocumentos, DocumentacionIncForm form) {

		EnvioDocumentacionIncidenciaRequest wsReq = new EnvioDocumentacionIncidenciaRequest();
		wsReq.getDocumento().addAll(listaDocumentos);
		wsReq.setObservaciones(form.getIncidencias().getObservaciones());

		/* MODIF TAM (29.08.2018) ** Inicio */
		/* Comprobamos si es una línea de Ganado */
		Boolean isLineaGanado = false;

		try {
			isLineaGanado = comprobarIsLineaGanado(tipoEnvio, form, lineaInc);
		} catch (DAOException e) {
			logger.error("Ocurrio un error al comprobar si la poliza es de la línea de Ganado", e);
		}
		/* MODIF TAM (29.08.2018) ** Inicio */

		if (ENVIO_POLIZA.equals(tipoEnvio)) {
			PlanReferenciaTipo planReferenciaTipo = new PlanReferenciaTipo();
			planReferenciaTipo.setPlan(form.getIncidencias().getCodplan().intValue());
			planReferenciaTipo.setReferencia(form.getIncidencias().getReferencia());

			// MODIF TAM (28.08.2018) ** Inicio ///
			// Hay que incluir una validación para las polizas que son de ganado
			// ya que el tipo de referencia siempre debe ser 'P'
			if (!isLineaGanado) {
				if (REF_POLIZA.equals(form.getIncidencias().getTiporef())) {
					planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.P);
				} else {
					planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.C);
				}
			} else {
				planReferenciaTipo.setTipoReferencia(PolizaReferenciaTipo.P);
			}

			wsReq.setPlanReferenciaTipo(planReferenciaTipo);
		} else if (ENVIO_INCIDENCIA.equals(tipoEnvio)) {
			AnioIncidencia anioIncidencia = new AnioIncidencia();
			anioIncidencia.setAnio(form.getIncidencias().getAnhoincidencia().intValue());
			anioIncidencia.setNumero(new BigInteger(form.getIncidencias().getNumincidencia().toPlainString()));
			wsReq.setAnioIncidencia(anioIncidencia);
		} else if (ENVIO_ANEXO.equals(tipoEnvio)) {
			wsReq.setCuponModificacion(form.getIncidencias().getIdenvio());
		} else if (ENVIO_ASEGURADO.equals(tipoEnvio)) {
			PlanLineaNif planLineaNif = new PlanLineaNif();
			planLineaNif.setLinea(form.getIncidencias().getCodlinea().intValue());
			planLineaNif.setNif(form.getIncidencias().getNifaseg());
			planLineaNif.setPlan(form.getIncidencias().getCodplan().intValue());
			wsReq.setPlanLineaNif(planLineaNif);
		}
		wsReq.setObservaciones(form.getIncidencias().getObservaciones());
		return wsReq;
	}

	private static String xmlEnvioIncidenciaNueva(EnvioDocumentacionNuevaIncidenciaRequest req) {
		StringBuilder sb = new StringBuilder(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosweb/ContratacionSCImpresionModificacion/\" xmlns:xm=\"http://www.w3.org/2005/05/xmlmime\"><soapenv:Body>");
		sb.append("<con:envioDocumentacionNuevaIncidenciaRequest>");
		if (req.getPlanReferenciaTipo() != null) {
			sb.append(nodoPlanReferenciaTipo(req.getPlanReferenciaTipo()));
		} else if (req.getPlanLineaNif() != null) {
			sb.append(nodoPlanLineaNif(req.getPlanLineaNif()));
		}
		sb.append(nodoDocumento(req.getDocumento()));
		if (req.getObservaciones() != null) {
			sb.append("<con:observaciones>").append(req.getObservaciones()).append("</con:observaciones>");
		}
		sb.append("<con:codigoAsunto>").append(req.getCodigoAsunto()).append("</con:codigoAsunto>");
		sb.append("</con:envioDocumentacionNuevaIncidenciaRequest>");
		sb.append("</soapenv:Body></soapenv:Envelope>");
		;

		return sb.toString();
	}

	private static String xmlEnvioIncidencia(EnvioDocumentacionIncidenciaRequest req) {
		StringBuilder sb = new StringBuilder(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosweb/ContratacionSCImpresionModificacion/\" xmlns:xm=\"http://www.w3.org/2005/05/xmlmime\"><soapenv:Body>");
		sb.append("<con:envioDocumentacionIncidenciaRequest>");
		if (req.getPlanReferenciaTipo() != null) {
			sb.append(nodoPlanReferenciaTipo(req.getPlanReferenciaTipo()));
		} else if (req.getPlanLineaNif() != null) {
			sb.append(nodoPlanLineaNif(req.getPlanLineaNif()));
		} else if (req.getAnioIncidencia() != null) {
			sb.append("<con:anioIncidencia>");
			sb.append("<con:anio>").append(req.getAnioIncidencia().getAnio()).append("</con:anio>");
			sb.append("<con:numero>").append(req.getAnioIncidencia().getNumero()).append("</con:numero>");
			sb.append("</con:anioIncidencia>");
		} else if (req.getCuponModificacion() != null) {
			// Resolución Incidencia (25.09.2018) ** Inicio //
			// En este caso enviamos el valor del cupon
			// sb.append("<con:cuponModificacion>").append(req.getPlanReferenciaTipo().getPlan()).append("</con:cuponModificacion>");
			sb.append("<con:cuponModificacion>").append(req.getCuponModificacion()).append("</con:cuponModificacion>");
			// Resolución Incidencia (25.09.2018) ** Fin //
		}
		sb.append(nodoDocumento(req.getDocumento()));
		if (req.getObservaciones() != null) {
			sb.append("<con:observaciones>").append(req.getObservaciones()).append("</con:observaciones>");
		}
		sb.append("</con:envioDocumentacionIncidenciaRequest>");
		sb.append("</soapenv:Body></soapenv:Envelope>");

		return sb.toString();
	}

	private static StringBuilder nodoPlanReferenciaTipo(PlanReferenciaTipo planReferenciaTipo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<con:planReferenciaTipo>");
		sb.append("<con:plan>").append(planReferenciaTipo.getPlan()).append("</con:plan>");
		sb.append("<con:referencia>").append(planReferenciaTipo.getReferencia()).append("</con:referencia>");
		sb.append("<con:tipoReferencia>").append(planReferenciaTipo.getTipoReferencia())
				.append("</con:tipoReferencia>");
		sb.append("</con:planReferenciaTipo>");
		return sb;
	}

	private static StringBuilder nodoPlanLineaNif(PlanLineaNif planLineaNif) {
		StringBuilder sb = new StringBuilder();
		sb.append("<con:planLineaNif>");
		sb.append("<con:plan>").append(planLineaNif.getPlan()).append("</con:plan>");
		sb.append("<con:linea>").append(planLineaNif.getLinea()).append("</con:linea>");
		sb.append("<con:nif>").append(planLineaNif.getNif()).append("</con:nif>");
		sb.append("</con:planLineaNif>");
		return sb;
	}

	private static StringBuilder nodoDocumento(List<Base64Binary> documentos) {
		StringBuilder sb = new StringBuilder();
		for (Base64Binary doc : documentos) {
			sb.append("<con:documento xm:contentType=\"").append(doc.getContentType()).append("\">")
					.append(doc.getValue()).append("</con:documento>");
		}
		return sb;
	}

	/* Pet. 67219 ** MODIF TAM (27.01.2021) ** Inicio */
	/* Creamos un nuevo método para comprobar si el ASegurado está bloqueado */
	public boolean consultaAseguradoBloqueado(BigDecimal plan, BigDecimal linea, String referencia, String nifcif) {

		return this.incidenciasAgroDao.getEstadoAsegurado(plan, linea, referencia, nifcif);
	}

	public void setIncidenciasAgroDao(IIncidenciasAgroDao incidenciasAgroDao) {
		this.incidenciasAgroDao = incidenciasAgroDao;
	}

	/* Pet. 67219 ** MODIF TAM (27.01.2021) ** Fin */

	public AsuntosInc getAsunto(String codAsunto) {
		return (AsuntosInc) this.aportarDocIncidenciaDao.getObject(AsuntosInc.class, 
				new AsuntosIncId(codAsunto, CATALOGO_POLIZA));
	}

	public Motivos getMotivo(int codMotivo) {
		return (Motivos) this.aportarDocIncidenciaDao.getObject(Motivos.class, codMotivo);
	}

	public DocumentosInc obtenerDocumento(Long idDocumento) {
		return (DocumentosInc) this.aportarDocIncidenciaDao.getObject(DocumentosInc.class, idDocumento);
	}

	public Incidencias getIncidencia(Long idIncidencia) {
		return (Incidencias) this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
	}

	public void setAportarDocIncidenciaDao(IAportarDocIncidenciaDao aportarDocIncidenciaDao) {
		this.aportarDocIncidenciaDao = aportarDocIncidenciaDao;
	}

	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void getSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

}
