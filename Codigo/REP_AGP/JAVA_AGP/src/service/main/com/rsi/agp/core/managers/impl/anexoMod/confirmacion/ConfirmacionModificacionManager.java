package com.rsi.agp.core.managers.impl.anexoMod.confirmacion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.exception.ValidacionServiceException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.WebServicesCplManager;
import com.rsi.agp.core.managers.impl.WebServicesPplManager;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaCplTranformer;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaGanadoTranformer;
import com.rsi.agp.core.managers.impl.anexoMod.util.PolizaActualizadaTranformer;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.anexo.IEnviosSWConfirmacionDao;
import com.rsi.agp.dao.models.anexo.IEstadoCuponDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModSWValidacion;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.anexo.EnviosSWXML;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;

public class ConfirmacionModificacionManager implements IConfirmacionModificacionManager {
	
	/*** SONAR Q ** MODIF TAM(16.12.2021) ***/
	/** - Se ha eliminado todo el código comentado
	 ** - Se crean metodos nuevos para descargar de ifs/fors
	 ** - Se crean constantes locales nuevas
	 **/

	private ISolicitudModificacionManager solicitudModificacionManager;
	private IAnexoModificacionDao anexoModificacionDao;
	private WebServicesPplManager webServicesPplManager;
	private WebServicesCplManager webServicesCplManager;
	private IEnviosSWConfirmacionDao enviosSWConfirmacionDao;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private IEstadoCuponDao estadoCuponDao;
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private ParametrizacionManager parametrizacionManager;
	private Parametro parametro;
	private IPolizaDao polizaDao;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Inicio **/
	private static final String MSJ = "mensaje"; 
	private static final String ANX_REF = "El Anexo de Modificacion con referencia ";
	private static final String ERR_ACT_XML =  "Error al actualizar el xml del anexo";
	
	/** CONSTANTES SONAR Q ** MODIF TAM (16.12.2021) ** Fin **/
	

	private static final Log logger = LogFactory.getLog(ConfirmacionModificacionManager.class);

	public AcuseRecibo generarPolizaActualizada(final AnexoModificacion am, final String realPath,
			final String codUsuario, final boolean hayCambiosDatosAsegurado) {

		boolean esPolizaGanado = am.getPoliza().getLinea().isLineaGanado();

		// Obtiene el anexo de BD para cargar el xml a enviar al servicio
		AnexoModificacion anexo = this.getAnexoByIdCupon(am.getCupon().getId());

		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/*
		 * Por los desarrollos de esta peticion tanto las polizas agricolas como las de
		 * ganado iran por el mismo end-point y con formato Unificado
		 */

		// Obtiene el objeto Poliza correspondiente al xml de la situacion actualizada
		es.agroseguro.contratacion.PolizaDocument p = (es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
				.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon());

		Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
		try {
			listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(am);
		} catch (BusinessException e1) {
			logger.error("Error al obtener la lista de datos variables dependientes del riesgo y cpm", e1);
		}
		List<BigDecimal> listaCPM = new ArrayList<BigDecimal>();
		try {
			logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
					+ am.getPoliza().getIdpoliza() + ", idAnexo: " + am.getId() + ", codModulo: "
					+ am.getPoliza().getCodmodulo());
			listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(am.getPoliza().getIdpoliza(), am.getId(),
					am.getPoliza().getCodmodulo());
		} catch (Exception e1) {
			logger.error("Error al obtener la lista de CPM permitidos", e1);
		}

		AcuseRecibo validarPplSituacionAct = null;
		try {
			/* Pet. 57626 ** MODIF TAM (12.06.2020) */
			/* Utilizamos la misma funcion tanto para ganado como para agricolas */
			// Modifica la situacion actualizada de la poliza con los cambios del
			// anexo de modificacion
			if (esPolizaGanado) {
				logger.debug("Es poliza de ganado");
				PolizaActualizadaGanadoTranformer.generarPolizaSituacionFinalCompleta(p.getPoliza(), am,
						listaDatosVariables, listaCPM, anexoModificacionDao, hayCambiosDatosAsegurado);
				logger.debug("Despues de transformar");
			} else {
				logger.debug("Es poliza de Agricola");
				PolizaActualizadaTranformer.generarPolizaSituacionFinalCompletaAgri(p.getPoliza(), am,
						listaDatosVariables, listaCPM, hayCambiosDatosAsegurado);
			}
			/*
			 * Quitamos de la poliza los datos variables de coberturas que pertenecen a
			 * parcela
			 */
			// MPM - Si en el anexo se han modificado las coberturas se envian las del anexo
			if (am.getCoberturas() == null || am.getCoberturas().isEmpty()) {
				logger.debug("Sin coberturas");
				p = borraDatosVariablesDeCoberturas(p, am.getPoliza().getLinea().getLineaseguroid(), esPolizaGanado);
			}
			// Se actualiza el el objeto del anexo de modificacion con el xml de la poliza
			// actualizada
			actualizarXMLAnexo(p, am);

			validarPplSituacionAct = webServicesPplManager.validarPplSituacionAct(p, anexo, realPath);

		} catch (BusinessException e) {
			logger.error("Error al validar la situacion actualizada de la poliza principal", e);
		} catch (ValidacionPolizaException e) {
			logger.error("Error al generarPolizaSituacionFinalCompleta", e);
		} catch (Exception e) {
			logger.error("Error inesperado al generar la poliza actualizada", e);
		} finally {
			if (p != null)
				registrarComunicacionSW(codUsuario, am, p,
						validarPplSituacionAct == null ? "[ERROR]" : validarPplSituacionAct.xmlText());
		}

		return validarPplSituacionAct;
	}

	private es.agroseguro.contratacion.PolizaDocument borraDatosVariablesDeCoberturas(
			final es.agroseguro.contratacion.PolizaDocument p, final Long lineaSeguroId, final boolean esPolizaGanado)
			throws BusinessException {

		/* Pet. 57626 ** MODIF TAM (15.06.2020) ** Inicio */
		
		/* Por los desarrollos de esta peticion tanto pol. de Ganado como las de
		 * Agricola van por el mismo end-point y van con Formato Unificado */

		Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta = null;
		Class<?> clase;
		ArrayList<RiesgoCubiertoElegido> lstRCEA = new ArrayList<RiesgoCubiertoElegido>();

		if (esPolizaGanado) {
			clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
			es.agroseguro.contratacion.datosVariables.DatosVariables datt = ((es.agroseguro.contratacion.PolizaDocument) p)
					.getPoliza().getCobertura().getDatosVariables();
			if (datt == null)
				return p;
			
			/* SONAR Q */
			lstRCEA = obtenerArrRiegosFinal(p, lstRCEA);
			/* SONAR Q FIN */
			
			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] arrRiegosFinal = lstRCEA
					.toArray(new RiesgoCubiertoElegido[lstRCEA.size()]);

			datt.setRiesgCbtoElegArray(arrRiegosFinal);

		} else {
			clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = ((es.agroseguro.contratacion.PolizaDocument) p)
					.getPoliza().getCobertura().getDatosVariables();

			if (datosVariables == null)
				return p;
			
			/* SONAR Q */
			tratarAgricola(clase, datosVariables, dvCodConceptoEtiqueta, lineaSeguroId);
			/* SONAR Q FIN */
		}
		return p;
	}

	public AcuseRecibo generarPolizaActualizadaCpl(AnexoModificacion am, String realPath, final String codUsuario, final boolean hayCambiosDatosAsegurado) {

		/* Pet. 57626 ** MODIF TAM (18.06.2020) ** Inicio */
		/*
		 * Por el desarrollo de esta peticion tanto polizas Agricolas como de Ganado van
		 * por formato Unificado
		 */

		// Obtiene el objeto Poliza correspondiente al xml de la situacion actualizada
		es.agroseguro.contratacion.Poliza p = ((es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
				.getPolizaActualizadaCplFromCupon(am.getCupon().getIdcupon())).getPoliza();

		// Modifica la situacion actualizada de la poliza con los cambios del anexo de
		// modificacion
		PolizaActualizadaCplTranformer.generarPolizaSituacionFinalCompleta(p, am);

		// Se actualiza el el objeto del anexo de modificacion con el xml de la poliza
		// actualizada
		actualizarXMLAnexoCpl(p, am);

		AcuseRecibo validarSituacionAct = null;
		try {
			validarSituacionAct = webServicesCplManager.validarSituacionActualizada(p, am, realPath);

		} catch (BusinessException e) {
			logger.error("Error al validar la situacion actualizada de la poliza complementaria", e);
		} finally {
			if (p != null)
				registrarComunicacionSW(codUsuario, am, p,
						validarSituacionAct == null ? "[ERROR]" : validarSituacionAct.xmlText());
		}

		return validarSituacionAct;
	}

	/**
	 * Carga el objeto Anexo asociado al id de cupon pasado como parametro
	 * 
	 * @param id
	 * @return
	 */
	public AnexoModificacion getAnexoByIdCupon(Long id) {
		// Carga el anexo de bbdd
		AnexoModificacion am = new AnexoModificacion();
		try {
			am = anexoModificacionDao.getAnexoByIdCupon(id);
		} catch (DAOException e) {
			logger.error("Error al obtener el AM", e);
		} catch (Exception e) {
			logger.error("Error inesperado al obtener el AM", e);
		}
		return am;
	}

	public Map<String, String> confirmarAnexo(AnexoModificacion am, boolean indRevAdm, String codUsuario,
			String realPath) {

		// Obtiene el anexo de BD para cargar el xml a enviar al servicio
		AnexoModificacion anexo = this.getAnexoByIdCupon(am.getCupon().getId());

		// Comprueba si el anexo es de principal o de complementaria
		boolean amPpal = !(Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(anexo.getPoliza().getTipoReferencia()));

		// Llama al SW para confirmar el AM
		AcuseRecibo acuse = null;
		String errorSW = null;
		Map<String, String> mensajes = new HashMap<String, String>();

		/* Pet. 57626 ** MODIF TAM (16.06.2020) *** Inicio */
		/* Por los desarrollos de esta peticion tanto las polizas de ganado como las de
		 * agricolas van ahora con Formato Unificado. */

		try {
			if (anexo.getPoliza().getLinea().isLineaGanado()) {
				acuse = new SWAnexoModificacionHelper().getConfirmacionModificacionUnificada(
						anexo.getCupon().getIdcupon(), indRevAdm, amPpal ? (anexo.getXml()) : null, realPath);
			} else {
				acuse = new SWAnexoModificacionHelper().getConfirmacionModificacion(anexo.getCupon().getIdcupon(),
						indRevAdm, amPpal ? (anexo.getXml()) : null, amPpal ? null : (anexo.getXml()), realPath);
			}

			// Genera el mensaje de aviso que aparecera en pantalla con el resultado de la
			// confirmacion y actualiza los estados del anexo y cupon
			mensajes = generarMsgConfirmacion(indRevAdm, anexo, acuse);
		} catch (AgrException e) {
			logger.error("El SW ha devuelto un error, el anexo no ha sido confirmado.", e);
			// Devuelve el error contenido en el AgrException
			errorSW = getMsgError(e, anexo);
			mensajes.put("alerta", errorSW);

		} catch (Exception e) {
			logger.error("Ha ocurrido un error inesperado", e);
		}

		try {
			anexoModificacionDao.actualizar(anexo);
		} catch (DAOException e) {
			logger.error("Error al actualizar el am", e);
		}

		// actualizamos el historico de estados de anexo
		historicoEstadosManager.insertaEstado(Tabla.ANEXO_MOD, anexo.getId(), codUsuario,
				anexo.getEstado().getIdestado());

		// Se guarda la comunicacion con el SW en la tabla correspondiente
		insertarEnviosSWConfirmacion(anexo, indRevAdm, codUsuario, anexo.getXml(),
				acuse != null ? (acuse.toString()) : errorSW);

		// Se devuelve el resultado de la llamada al SW
		return mensajes;
	}

	/**
	 * Obtiene el acuse de recibo del ultimo intento de confirmacion del anexo
	 * indicado
	 * 
	 * @param Id
	 *            del anexo del que se quiere obtener el acuse
	 */
	public Map<String, Object> getAcuseConfirmacion(Long idAnexo) {

		Map<String, Object> mapa = new HashMap<String, Object>();

		// Obtiene el clob de la ultimo intento de confirmacion del anexo
		Clob acuse = null;
		try {
			acuse = anexoModificacionDao.getAcuseConfirmacion(idAnexo);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener el ultimo acuse de recibo del anexo " + idAnexo, e);
		}

		// Si no se ha podido obtener el acuse se devuelve el error
		if (acuse == null) {
			mapa.put(MSJ, "Ha ocurrido un error al obtener el ultimo acuse de recibo del anexo");
			return mapa;
		}

		// Se convierte el clob en un objecto de acuse de recibo
		AcuseRecibo acuseRecibo = null;
		try {
			acuseRecibo = AcuseReciboDocument.Factory
					.parse(WSUtils.convertClob2String(acuse).replace("xml-fragment", "acus:AcuseRecibo"))
					.getAcuseRecibo();
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al genera el objeto AcuseReciboDocument", e);
		}

		// Si no se ha podido parsear el acuse a un objeto AcuseReciboDocument es porque
		// el acuse es una cadena
		if (acuseRecibo == null) {
			mapa.put(MSJ, WSUtils.convertClob2String(acuse));
			return mapa;
		}

		// Si se ha podido parsear correctamente, se anhade la lista de errores al mapa
		// y se devuelve
		@SuppressWarnings("rawtypes")
		List listaErrores = new ArrayList();
		if (acuseRecibo.getDocumentoArray() != null && acuseRecibo.getDocumentoArray().length > 0) {
			listaErrores = Arrays.asList(acuseRecibo.getDocumentoArray(0).getErrorArray());
		}

		String mensaje = "";
		if (listaErrores.isEmpty()) {
			DatosAsociados da = acuseRecibo.getDocumentoArray(0).getDatosAsociados();
			if (da != null) {
				mensaje = generarMsgConfDatosAsociados(da);
				mapa.put(MSJ, mensaje);
			}

		}

		mapa.put("errores", listaErrores);

		return mapa;
	}

	@Override
	public Cupon obtenerCuponByIdCupon(Long idCupon) {
		Cupon cupon = null;
		if (idCupon != null) {
			try {
				cupon = (Cupon) estadoCuponDao.get(Cupon.class, idCupon);
			} catch (DAOException e) {
				logger.error("Error al intentar obtener el cupon [" + idCupon + "]");
			}
		}
		return cupon;
	}

	/**
	 * Genera el mensaje de aceptacion a partir de acuse de recibo y sus datos
	 * asociados
	 */
	private String generarMsgConfDatosAsociados(DatosAsociados da) {
		String mensaje = "";
		String estado = "";
		String referencia = "";
		if (da.getDomNode() != null && da.getDomNode().getAttributes() != null) {
			estado = da.getDomNode().getAttributes().getNamedItem("estado").getNodeValue().toString();
			referencia = da.getDomNode().getAttributes().getNamedItem("referencia").getNodeValue().toString();

		}
		if (estado != null && referencia != null) {
			if (estado.equals("A"))
				mensaje = ANX_REF + referencia + " ha sido aceptado";
			else if (estado.equals("E"))
				mensaje = ANX_REF + referencia
						+ " ha sido aceptado pendiente de revision administrativa";
		}
		return mensaje;
	}

	/**
	 * Genera el mensaje de error a partir de acuse de recibo y sus datos asociados
	 * 
	 * @param da
	 * @return
	 */
	private String generarMsgErrorConfDatosAsociados(DatosAsociados da) {
		String mensaje = "";
		String incidencia = "";
		String referencia = "";

		if (da.getDomNode() != null && da.getDomNode().getAttributes() != null) {
			incidencia = da.getDomNode().getAttributes().getNamedItem("descriptivoIncidencia").getNodeValue()
					.toString();
			referencia = da.getDomNode().getAttributes().getNamedItem("referencia").getNodeValue().toString();

			mensaje = ANX_REF + referencia
					+ " ha sido rechazado debido a la incidencia '" + incidencia + "'";
		}

		return mensaje;
	}

	/**
	 * Genera el mensaje de aviso a partir del acuse y actualiza los estados del
	 * anexo y del cupon
	 * 
	 * @param indRevAdm
	 * @param anexo
	 * @param acuse
	 * @return
	 * @throws DAOException
	 */
	private Map<String, String> generarMsgConfirmacion(boolean indRevAdm, AnexoModificacion anexo, AcuseRecibo acuse)
			throws DAOException {

		String mensaje = "";
		String alerta = "";
		Map<String, String> mensajes = new HashMap<String, String>();
		final String MSG_CUPON_OK = getStrProperties("mensaje.anexo.cupon.confirmado.ok");
		final String MSG_CUPON_RECHAZADO = getStrProperties("mensaje.anexo.cupon.confirmado.rechazo");
		final String MSG_CUPON_TRAMITE = getStrProperties("mensaje.anexo.cupon.confirmado.tramite");
		final String MSG_CUPON_OK_REVADM = getStrProperties("mensaje.anexo.cupon.confirmado.revAdm");

		if (acuse != null && acuse.getDocumentoArray() != null) {
			Documento documento = acuse.getDocumentoArray(0);

			/* SONAR Q */
			// Estado del acuse de recibo
			int estadoAcuse = obtenerestadoAcuse(documento);
			/* SONAR Q FIN */
			logger.debug("[ESC-28168] estadoAcuse: " + estadoAcuse);
			// -- --
			// -- Modificacion aceptada y aplicada --
			// -- --
			if (estadoAcuse == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
				logger.debug("[ESC-28168] ACUSE_RECIBO_ESTADO_CORRECTO");
				// Actualiza el estado del cupon
				EstadoCupon estadoCupon = new EstadoCupon();
				estadoCupon.setId(indRevAdm ? Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE
						: Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO);
				anexo.getCupon().setEstadoCupon(estadoCuponDao.getEstadoCupon(estadoCupon.getId()));

				// Actualiza el estado del anexo
				Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_CORRECTO);
				estado.setDescestado(Constants.ANEXO_MODIF_ESTADO_CORRECTO_DESC);
				anexo.setEstado(estado);

				// sobrePrecio-2014 Actualizamos el campo revisar_sbp= S
				anexo.setRevisarSbp(Constants.CHARACTER_S);

				// Actualiza el mensaje con el texto definido para la confirmacion correcta
				mensaje = MSG_CUPON_OK;
			} else if (estadoAcuse == Constants.ACUSE_RECIBO_ESTADO_ACEPTADO_PDTE_REV_ADM) {
				logger.debug("[ESC-28168] ACUSE_RECIBO_ESTADO_ACEPTADO_PDTE_REV_ADM");
				// -- --
				// -- Modificacion aceptada pendiente de revision administrativa --
				// -- --
				// Actualiza el estado del cupon
				EstadoCupon estadoCupon = new EstadoCupon();
				estadoCupon.setId(Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE);
				anexo.getCupon().setEstadoCupon(estadoCuponDao.getEstadoCupon(estadoCupon.getId()));

				// Actualiza el estado del anexo
				Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_CORRECTO);
				estado.setDescestado(Constants.ANEXO_MODIF_ESTADO_CORRECTO_DESC);
				anexo.setEstado(estado);

				anexo.setRevisarSbp(null);

				// Actualiza el mensaje con el texto definido para la confirmacion correcta
				mensaje = MSG_CUPON_OK_REVADM;
			} else {
				logger.debug("[ESC-28168] ACUSE_RECIBO_ESTADO_RECHAZADO");
				// -- --
				// -- Modificacion rechazada --
				// -- --
				boolean isRechazado = false;
				
				/* SONAR Q */
				HashMap<String, Object> datos = obtenerAlerta(documento, alerta, isRechazado); 
				isRechazado = (Boolean) datos.get("isRechazado");
				alerta = (String) datos.get("alerta");
				logger.debug("[ESC-28168] isRechazado: " + isRechazado);
				logger.debug("[ESC-28168] alerta: " + alerta);
				/* SONAR Q INICIO */

				// Actualiza el estado del cupon
				EstadoCupon estadoCupon = new EstadoCupon();
				estadoCupon.setId(isRechazado ? Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO
						: Constants.AM_CUPON_ESTADO_ERROR_TRAMITE);
				anexo.getCupon().setEstadoCupon(estadoCuponDao.getEstadoCupon(estadoCupon.getId()));

				// Actualiza el estado del anexo
				Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_ERROR);
				estado.setDescestado(Constants.ANEXO_MODIF_ESTADO_ERROR_DESC);
				anexo.setEstado(estado);

				anexo.setRevisarSbp(null);

				// Actualiza el mensaje con el texto definido para el tipo de error
				alerta = (isRechazado ? MSG_CUPON_RECHAZADO : MSG_CUPON_TRAMITE) + alerta;
			}
			mensajes.put(MSJ, mensaje);
			mensajes.put("alerta", alerta);
		}
		return mensajes;
	}

	private void insertarEnviosSWConfirmacion(AnexoModificacion am, boolean indRevAdm, String codusuario,
			Clob xmlPoliza, String xmlAcuse) {

		EnviosSWConfirmacion envio = new EnviosSWConfirmacion();
		envio.setCodusuario(codusuario);
		envio.setFecha(new Date());
		envio.setIdcupon(new BigDecimal(am.getCupon().getId()));
		envio.setIndRevAdm(indRevAdm ? 'S' : 'N');
		
		logger.debug("XML");
		for (Cobertura c: am.getCoberturas()) {
			logger.debug(c.getCodconcepto() + " - " + c.getCodvalor());
		}
		envio.setAnexoModificacion(am);

		// XML envio
		EnviosSWXML objXmlPoliza = new EnviosSWXML();
		objXmlPoliza.setXml(xmlPoliza);

		// XML acuse
		EnviosSWXML objXmlAcuse = new EnviosSWXML();
		if (xmlAcuse != null)
			objXmlAcuse.setXml(Hibernate.createClob(xmlAcuse));

		envio.setEnviosSWXMLByIdxmlPoliza(objXmlPoliza);
		envio.setEnviosSWXMLByIdxmlAcuse(objXmlAcuse);

		try {
			enviosSWConfirmacionDao.guardarConfirmacion(envio);
		} catch (DAOException e) {
			logger.error("Ha occurido un error al guardar el registro de envio de confirmacion", e);
		} catch (Exception e) {
			logger.error("Ha occurido un error inesperado al guardar el registro de envio de confirmacion", e);
		}

	}

	/**
	 * Actualiza el xml del registro del anexo con los datos de la situacion
	 * actualizada de la poliza mas los cambios en el anexo
	 * 
	 * @param p
	 * @param am
	 */
	private void actualizarXMLAnexo(XmlObject p, AnexoModificacion am) {
		am.setXml(Hibernate.createClob(p.toString()));
		try {
			anexoModificacionDao.actualizar(am);
		} catch (DAOException e1) {
			logger.error(ERR_ACT_XML, e1);
		} catch (Exception e2) {
			logger.error(ERR_ACT_XML, e2);
		}
	}

	/**
	 * Actualiza el xml del registro del anexo de complementaria con los datos de la
	 * situacion actualizada de la poliza mas los cambios en el anexo
	 * 
	 * @param p
	 *            Poliza Actualizada de Agroseguro
	 * @param am
	 *            Anexo de modificacion
	 */
	private void actualizarXMLAnexoCpl(es.agroseguro.contratacion.Poliza p, AnexoModificacion am) {
		am.setXml(Hibernate.createClob(p.toString()));
		try {
			anexoModificacionDao.actualizar(am);
		} catch (DAOException e1) {
			logger.error(ERR_ACT_XML, e1);
		} catch (Exception e2) {
			logger.error(ERR_ACT_XML, e2);
		}
	}

	/**
	 * Devuelve el error contenido en el AgrException y actualiza los estados del
	 * anexo y del cupon
	 * 
	 * @param e
	 * @return
	 */
	private String getMsgError(AgrException e, AnexoModificacion anexo) {

		String errorSW = getStrProperties("mensaje.anexo.cupon.confirmado.error");
		try {
			List<Error> listaErrores = e.getFaultInfo().getError();
			for (Error error : listaErrores) {
				errorSW += error.getMensaje();
			}

			// Actualiza el estado del cupon
			EstadoCupon estadoCupon = new EstadoCupon();
			estadoCupon.setId(Constants.AM_CUPON_ESTADO_ERROR);

			anexo.getCupon().setEstadoCupon(estadoCuponDao.getEstadoCupon(estadoCupon.getId()));

			// Actualiza el estado del anexo
			Estado estado = new Estado();
			estado.setIdestado(Constants.ANEXO_MODIF_ESTADO_ERROR);
			estado.setDescestado(Constants.ANEXO_MODIF_ESTADO_ERROR_DESC);
			anexo.setEstado(estado);

		} catch (DAOException e1) {
			logger.error(ERR_ACT_XML, e1);
		} catch (Exception e2) {
			logger.error(ERR_ACT_XML, e2);
		}
		return errorSW;
	}

	/**
	 * Devuelve la cadena del properties asociada a la clave indicada por parametro
	 * 
	 * @param key
	 * @return
	 */
	private String getStrProperties(String key) {
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			logger.error("Error al obtener la clave " + key + " del properties", e);
		}
		return "";
	}

	/**
	 * AMG 06/03/2014 Eliminamos los errores que no queremos que aparezcan en el
	 * listado
	 * 
	 * @param acuseRecibo
	 *            acuse de recibo.
	 * @param webServiceToCall
	 *            Indica el tipo de servicio al que se llama
	 * @param polizaDao
	 * @param lineaseguroid
	 * @return ar
	 */
	public AcuseRecibo limpiaErroresWsAnexo(AcuseRecibo ar, String webServiceToCall, BigDecimal codPlan,
			BigDecimal codLinea, BigDecimal codEntida) {
		parametro = parametrizacionManager.getParametro();
		WSUtils.limpiaErroresWs(ar, webServiceToCall, parametro, polizaDao, codPlan, codLinea, codEntida,
				Constants.ANEXO_MODIFICACION);
		return ar;
	}

	public boolean checkPerfil34(final String perfil, final BigDecimal forzarRevisionAM) {

		boolean result = Boolean.FALSE;

		// Si el usuario es perfil 3 (interno o externo) o perfil 4 y su E-S Mediadora
		// esta configurada para que no se fuerce la revision administrativa
		if ((Constants.PERFIL_USUARIO_OFICINA.equals(perfil) || Constants.PERFIL_USUARIO_OTROS.equals(perfil))
				&& Constants.VALOR_0.equals(forzarRevisionAM)) {
			// Se devuelve true para indicar a la pantalla que no se puede mostrar el boton
			// de forzar
			result = Boolean.TRUE;
		}

		return result;
	}

	private void registrarComunicacionSW(final String codUsuario, AnexoModificacion am, XmlObject p,
			String XMLRespuesta) {
		try {
			AnexoModSWValidacion anexo = new AnexoModSWValidacion();
			anexo.setFecha(new Date());
			anexo.setCupon(am.getCupon().getIdcupon());
			anexo.setCodusuario(codUsuario);
			anexo.setAnexoModificacion(am);
			String xml = p.toString();
			// No se recupero el XML...
			if (xml == null)
				throw new ValidacionServiceException("No se ha podido obtener el XML de la Poliza");
			// Modifica el xml para que lo acepte el servicio de validacion
			final String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			xml = cabecera + xml.replace("xml-fragment", "ns2:Poliza");
			anexo.setXmlEnvio(Hibernate.createClob(xml));
			anexo.setXmlRespuesta(Hibernate.createClob(XMLRespuesta));
			polizaDao.saveOrUpdate(anexo);
		} catch (DAOException e) {
			logger.error("Error inesperado al guardar respuesta servicio web de Validacion", e);
		}

	}

	/* P0078691 ** MODIF TAM (14.12.2021) ** Inicio */
	/**
	 * Validar caracteristica explotacion
	 */
	public boolean validarCaractExplotacion(Poliza poliza, boolean isGanado) throws DAOException {

		boolean isValid = false;

		try {
			/* Si es poliza Principal de Agrícola */
			if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia())) {
				if (!isGanado) {
					/* Si la línea tiene caracteristicas de la explotacion */
					for (ComparativaPoliza comparativaPoliza : poliza.getComparativaPolizas()) {
						if (comparativaPoliza.getId().getCodconcepto().toString().equals("106")) {
							isValid = true;
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("[CaracteristicaExplotacionDao] validarCaractExplotacion().", ex);
		}

		return isValid;
	}

	public BigDecimal calcularCaractExplotacionAnx(final AnexoModificacion am, final String realPath,
			final String codUsuario, final Poliza poliza, boolean isGanado) throws BusinessException, DAOException {
		BigDecimal calcExpl = null;

		boolean aplicaCaractExpl = false;
		aplicaCaractExpl = this.validarCaractExplotacion(poliza, isGanado);

		if (aplicaCaractExpl) {

			logger.debug("**@@** ConfirmacionModificacionManager - calcularCaractExplotacionAnx [INIT]");
			boolean esPolizaGanado = am.getPoliza().getLinea().isLineaGanado();

			// Obtiene el objeto Poliza correspondiente al xml de la situacion actualizada
			es.agroseguro.contratacion.PolizaDocument p = (es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
					.getPolizaActualizadaFromCupon(am.getCupon().getIdcupon());

			Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
			try {
				listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(am);
			} catch (BusinessException e1) {
				logger.error("Error al obtener la lista de datos variables dependientes del riesgo y cpm", e1);
			}

			List<BigDecimal> listaCPM = new ArrayList<BigDecimal>();
			try {
				logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
						+ am.getPoliza().getIdpoliza() + ", idAnexo: " + am.getId() + ", codModulo: "
						+ am.getPoliza().getCodmodulo());
				listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(am.getPoliza().getIdpoliza(), am.getId(),
						am.getPoliza().getCodmodulo());
			} catch (Exception e1) {
				logger.error("Error al obtener la lista de CPM permitidos", e1);
			}

			try {

				logger.debug("** Poliza de Agricola");
				PolizaActualizadaTranformer.generarPolizaSituacionFinalCompletaAgri(p.getPoliza(), am,
						listaDatosVariables, listaCPM, false);

				 /* Quitamos de la poliza los datos variables de coberturas que pertenecen a
				  * parcela */

				// MPM - Si en el anexo se han modificado las coberturas se envian las del anexo
				if (am.getCoberturas() == null || am.getCoberturas().isEmpty()) {
					logger.debug("Sin coberturas");
					p = borraDatosVariablesDeCoberturas(p, am.getPoliza().getLinea().getLineaseguroid(),
							esPolizaGanado);
				}
				// Se actualiza el el objeto del anexo de modificacion con el xml de la poliza
				// actualizada
				String xml = p.toString();

				Poliza pol = polizaDao.getPolizaById(am.getPoliza().getIdpoliza());

				logger.debug(
						"**@@** ConfirmacionModificacionManager - Antes de llamar al calcularCaractExplotacionAnx");
				calcExpl = webServicesPplManager.calcularCaractExplotacionAnx(xml, realPath, pol);

				logger.debug("**@@** ConfirmacionModificacionManager - Valor obtenido:" + calcExpl);
				if (calcExpl != null) {
					anexoModificacionDao.guardarCaractExplAnx(am.getId(), calcExpl);
				}

			} catch (BusinessException be) {
				logger.error(
						"Se ha producido un error al guardar la caracterï¿½stica de explotaciï¿½n: " + be.getMessage());
				throw be;

			} catch (Exception e) {
				logger.error("Se ha producido un error al generar el XML de la poliza: " + e.getMessage());
				throw new BusinessException("Se ha producido un error al generar el XML de la poliza", e);
			}
		}

		return calcExpl;

	}
	/* P0078691 ** MODIF TAM (14.12.2021) ** Fin */
	
	/** SONAR Q ** MODIF TAM (17.12.2021) ** Inicio **/
	private static ArrayList<RiesgoCubiertoElegido> obtenerArrRiegosFinal(es.agroseguro.contratacion.PolizaDocument p, ArrayList<RiesgoCubiertoElegido> lstRCEA){
		es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] arrRiegos = ((es.agroseguro.contratacion.PolizaDocument) p)
				.getPoliza().getCobertura().getDatosVariables().getRiesgCbtoElegArray();
		// eliminamos los riesgos que tengan valor a "N"
		for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido ar : arrRiegos) {
			logger.debug("CPMod:" + ar.getCPMod() + " CodRCub:" + ar.getCodRCub() + " Valor:" + ar.getValor());
			if (ar.getValor().toString().equals("S")) {
				logger.debug("anhadimos concepto --> " + ar.getCPMod() + " con valor a S");
				RiesgoCubiertoElegido rCubEleg = RiesgoCubiertoElegido.Factory.newInstance();
				rCubEleg.setCodRCub(ar.getCodRCub());
				rCubEleg.setCPMod(ar.getCPMod());
				rCubEleg.setValor(ar.getValor());
				lstRCEA.add(rCubEleg);
			}
		}
		return lstRCEA;
	}
	

	private static Method obtenerMethodRemove(Object objeto, String valor, Class<?> clase,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, String etiqueta) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	
		Method methodRemove = null;
		if (valor.equals("A")){	
			if (objeto != null) {
				methodRemove = clase.getMethod("unset" + etiqueta);
				methodRemove.invoke(datosVariables);
				logger.debug("Concepto " + etiqueta + " eliminado");
			}
			
		}else if (valor.equals("B")) {
			if (objeto != null) {
				methodRemove = clase.getMethod("unset" + etiqueta + "Array");
				methodRemove.invoke(datosVariables);
				logger.debug("Concepto " + etiqueta + " eliminado");
			}
			
		}
		return methodRemove;
	}
	

	private void tratarAgricola(Class<?> clase,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, 
			Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta, Long lineaSeguroId) throws BusinessException {

		// Mapa auxiliar con los codigos de concepto de los datos variables y sus
		// etiquetas y tablas asociadas.
		dvCodConceptoEtiqueta = diccionarioDatosDao.getCodConceptoEtiquetaTablaCoberturas(lineaSeguroId);
		String valor;
		for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
			logger.debug("Eliminando concepto --> " + dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta());
			Method method;
			String etiqueta = dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta();
			try {
				
				method = clase.getMethod("get" + etiqueta);
				Object objeto = method.invoke(datosVariables);
				/* SONAR Q */
				valor = "A";
				obtenerMethodRemove(objeto, valor, clase, datosVariables, etiqueta);
				/* SONAR Q */
			} catch (NoSuchMethodException e) {
				try {
					method = clase.getMethod("get" + etiqueta + "Array");
					Object objeto = method.invoke(datosVariables);
					/* SONAR Q */
					valor = "B";
					obtenerMethodRemove(objeto, valor, clase, datosVariables, etiqueta);
					/* SONAR Q */
				} catch (NoSuchMethodException e1) {
					logger.debug("Concepto no presente en datos variables --> " + etiqueta);
				} catch (Exception ex) {
					logger.error(
							"Error inesperado al borrar datos variables de las coberturas de la poliza desde el xml",
							e);
					throw new BusinessException(ex);
				}
			} catch (Exception e) {
				logger.error(
						"Error inesperado al borrar datos variables de las coberturas de la poliza desde el xml",
						e);
				throw new BusinessException(e);
			}
		}
	}
	

	private HashMap<String, Object> obtenerAlerta (Documento documento, String alerta, boolean isRechazado){ 
	
		HashMap<String, Object> datos = new HashMap<String, Object>();
		
		// Si hay lista de errores
		if (documento.getErrorArray().length > 0) {
			for (es.agroseguro.acuseRecibo.Error error : documento.getErrorArray()) {
				if (error.getTipo() == Constants.ACUSE_RECIBO_ERROR_RECHAZO)
					isRechazado = true;
				alerta += error.getDescripcion() + ".";
	
			}
		}
		// Si no hay lista de errores pero si datos asociados
		else if (documento.getDatosAsociados() != null) {
			isRechazado = true;
			alerta = generarMsgErrorConfDatosAsociados(documento.getDatosAsociados());
		}
		
		datos.put("alerta", alerta);
		datos.put("isRechazado", isRechazado);
		
		return datos;
		
	}

	// Estado del acuse de recibo
	private static int  obtenerestadoAcuse(Documento documento) {
		int estadoAcuse = (documento != null) ? documento.getEstado() : -1;
		return estadoAcuse;
	}	
	/** SONAR Q ** MODIF TAM (17.12.2021) ** Fin **/

	// SETTERS PARA SPRING
	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setWebServicesPplManager(WebServicesPplManager webServicesPplManager) {
		this.webServicesPplManager = webServicesPplManager;
	}

	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}

	public void setEnviosSWConfirmacionDao(IEnviosSWConfirmacionDao enviosSWConfirmacionDao) {
		this.enviosSWConfirmacionDao = enviosSWConfirmacionDao;
	}

	public void setWebServicesCplManager(WebServicesCplManager webServicesCplManager) {
		this.webServicesCplManager = webServicesCplManager;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setEstadoCuponDao(IEstadoCuponDao estadoCuponDao) {
		this.estadoCuponDao = estadoCuponDao;
	}

	public void setParametrizacionManager(ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setHistoricoEstadosManager(IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

}