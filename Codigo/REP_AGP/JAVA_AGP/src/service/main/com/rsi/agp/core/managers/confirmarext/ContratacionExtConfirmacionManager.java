package com.rsi.agp.core.managers.confirmarext;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.w3._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.ConfirmacionServiceException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.PolizaActualizadaRCResponse;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ObjetosAsegurados;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ParcelaReducida;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.ISolicitudReduccionCapManager;
import com.rsi.agp.core.managers.IContratacionExtConfirmacionManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.confirmarext.ConfirmarExtException.AgrWSException;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.poliza.util.PolizaUtils;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.anexo.ICuponDao;
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IEnviosSWSolicitudDao;
import com.rsi.agp.dao.models.anexo.ISubvencionesDeclarablesModificacionPolizaDao;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.commons.ITerminoDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.contratacionext.IAuditoriaConfirmacionExtDao;
import com.rsi.agp.dao.models.contratacionext.ICorreduriaExternaDao;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaAnexoDao;
import com.rsi.agp.dao.models.poliza.IImportacionPolizasExtDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.IReduccionCapitalDao;
import com.rsi.agp.dao.models.poliza.ITipoCapitalDao;
import com.rsi.agp.dao.models.poliza.ganado.ICargaExplotacionesDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionAnexoDao;
import com.rsi.agp.dao.models.sbp.ISimulacionSbpDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.anexo.AnexoModBonifRecargos;
import com.rsi.agp.dao.tables.anexo.AnexoModBonifRecargosId;
import com.rsi.agp.dao.tables.anexo.AnexoModDistribucionCostes;
import com.rsi.agp.dao.tables.anexo.AnexoModDistribucionCostesId;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvCCAA;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvCCAAId;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvEnesa;
import com.rsi.agp.dao.tables.anexo.AnexoModSubvEnesaId;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.EnviosSWConfirmacion;
import com.rsi.agp.dao.tables.anexo.EnviosSWSolicitud;
import com.rsi.agp.dao.tables.anexo.EnviosSWXML;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.importacion.AuditoriaAnexosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalcAnxExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaCalculoExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaConfirmacionExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaSiniestrosExt;
import com.rsi.agp.dao.tables.importacion.AuditoriaValidarExt;
import com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVarExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRazaAnexo;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModuloAnexo;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWConfirmacionRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWXMLRC;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalBonifRecargos;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalBonifRecargosId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostes;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalDistribucionCostesId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvCCAA;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvCCAAId;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvEnesa;
import com.rsi.agp.dao.tables.reduccionCap.RedCapitalSubvEnesaId;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.acuseRecibo.Error;
import es.agroseguro.contratacion.costePoliza.BonificacionRecargo;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.costePoliza.SubvencionCCAA;
import es.agroseguro.contratacion.costePoliza.SubvencionEnesa;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada;
import es.agroseguro.contratacion.explotacion.Animales;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;
import es.agroseguro.iTipos.Gastos;
import es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument;
import es.agroseguro.seguroAgrario.siniestros.SiniestroDocument;

public class ContratacionExtConfirmacionManager implements IContratacionExtConfirmacionManager {

	private static final int COD_ERROR_INESPERADO = -1;
	private static final int COD_KO_RGA = 0;
	private static final int COD_OK_RGA_KO_AGROSEGURO = 1;
	private static final int COD_OK_TODO = 2;

	private static final String ETIQUETA_ACUSE_RECIBO_REFERENCIA = "referencia";
	private static final String COD_COMPLEMENTARIA_SEGURO_CRECIENTE = "SD";
	private static final String COD_TIPO_DOCUMENTO_COMPLEMENTARIO = "C";
	private static final String COD_TIPO_DOCUMENTO_PRINCIPAL = "P";
	
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	private IImportacionPolizasExtDao importacionPolizasExtDao;
	private ICorreduriaExternaDao correduriaExternaDao;
	private IAuditoriaConfirmacionExtDao auditoriaConfirmacionExtDao;
	private IAnexoModificacionDao anexoModificacionDao;
	private ICuponDao cuponDao;
	private IPolizaDao polizaDao;
	private IEnviosSWSolicitudDao enviosSWSolicitudDao;
	private ITipoCapitalDao tipoCapitalDao;
	private ITerminoDao terminoDao;
	private ICargaExplotacionesDao cargaExplotacionesDao;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private ISimulacionSbpDao simulacionSbpDao;
	private ISimulacionSbpManager simulacionSbpManager;
	private ISolicitudModificacionManager solicitudModificacionManager;
	// P0079313 [BEGIN] --> Necesario para que no falle el server
	private IDatosParcelaAnexoDao datosParcelaAnexoDao;
	private IDatosExplotacionAnexoDao datosExplotacionAnexoDao;
	private ISubvencionesDeclarablesModificacionPolizaDao subvencionesDeclarablesModificacionPolizaDao;
	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao;
	// P0079313 [END]
	// P0079361 [BEGIN]
	private ISolicitudReduccionCapManager solicitudReduccionCapManager;
	//private IHistoricoEstadosManager historicoEstadosManager;
	private IReduccionCapitalDao reduccionCapitalDao;
	private com.rsi.agp.dao.models.rc.ICuponDao cuponRCDao;
	private com.rsi.agp.dao.models.rc.IEnviosSWSolicitudDao enviosSWRCSolicitudDao;
	// P0079361 [END]
		
	private static final Log logger = LogFactory.getLog(ContratacionExtConfirmacionManager.class);

	/**
	 * Método principal de la clase para realizar la contratación definitiva de
	 * siniestros recibidos a través de un mediador
	 */
	public ConfirmarSiniestroExtBean doConfirmarSiniestro(final Base64Binary entrada, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		ConfirmarSiniestroExtBean confirmar = new ConfirmarSiniestroExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion;
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición confirmación siniestro mediador.");
		ConfirmarExtSiniestroBean siniestro = parseASiniestro(entrada);
		logger.debug("Siniestro procesado. Referencia " + siniestro.getReferencia() + " Linea " + siniestro.getLinea()
				+ " plan " + siniestro.getPlan());
		Base64Binary acuseRecibo = null;
		String codigoInternoEntidad = null;
		try {
			// 3. Validaciones
			codigoInternoEntidad = correduriaExternaDao.getCorreduriaPoliza(BigDecimal.valueOf(siniestro.getPlan()),
					siniestro.getReferencia());
			if (!StringUtils.isNullOrEmpty(codigoInternoEntidad)) {
				if (validarCorreduriaExterna(codigoInternoEntidad)) {
					// 4.1. Llamada al SW Confirmación
					acuseRecibo = ContratacionExtConfirmacionWS.confirmarSiniestro(entrada, realPath);
					confirmar.setAcuseRecibo(acuseRecibo);
					// 5. Resultado del servicio web;
					AcuseRecibo acuseReciboObj = procesarAcuseRecibo(acuseRecibo);
					Documento docAcuseRecibo = getAcuseRecibo(acuseReciboObj);
					Error[] errores = null;
					if (docAcuseRecibo.getEstado() != 1) {
						errores = docAcuseRecibo.getErrorArray();
					}
					if (errores != null && errores.length > 0) {
						List<ConfirmarSiniestroExtBean.AgrError> agrs = new ArrayList<ConfirmarSiniestroExtBean.AgrError>();
						codValidacion = COD_OK_RGA_KO_AGROSEGURO;
						for (Error error : errores) {
							ConfirmarSiniestroExtBean.AgrError agr = new ConfirmarSiniestroExtBean.AgrError();
							int codigo = -1;
							try {
								codigo = Integer.parseInt(error.getCodigo());
							} catch (NumberFormatException nfe) {
								logger.error("Error al parsear el codigo de error a int", nfe);
							}
							agr.setCodigo(codigo);
							agr.setMensaje(error.getDescripcion());
							agrs.add(agr);
						}
						confirmar.setAgrErrors(agrs.toArray(new ConfirmarSiniestroExtBean.AgrError[agrs.size()]));
						msgValidacion = "";
					} else {
						codValidacion = COD_OK_TODO;
						msgValidacion = "CONFIRMADO SINIESTRO";
					}
				} else {
					msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
				}
			} else {
				msgValidacion = "ERROR VALIDACION POLIZA NO EXISTE.";
				// AL NO TENER POLIZA NO PODEMOS OBTENER LA ENTIDAD DEL COLECTIVO
				codigoInternoEntidad = "XXXXXXXX";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (ConfirmacionServiceException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		// 5.3; Resultado; XML devuelto por SW Confirmación de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		confirmar.setCodigo(codValidacion);
		confirmar.setMensaje(msgValidacion);
		guardarAuditoriaSiniestroExt(codigoInternoEntidad, codValidacion, msgValidacion, entrada, acuseRecibo);
		logger.debug("----------------------------------------------------------------------");
		return confirmar;
	}

	/**
	 * Método principal de la clase para realizar la anulación de cupón de un
	 * mediador
	 */
	public AnularCuponExtBean doAnularCupon(final String idCupon, final String realPath)
			throws AgrWSException, Exception {
		AnularCuponExtBean anular = new AnularCuponExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion;
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición anulación cupón mediador. IdCupon " + idCupon);
		String codigoInternoEntidad = null;
		try {
			// 3. Validaciones
			codigoInternoEntidad = correduriaExternaDao.getCorreduriaCupon(idCupon);
			if (!StringUtils.isNullOrEmpty(codigoInternoEntidad)) {
				if (validarCorreduriaExterna(codigoInternoEntidad)) {
					// 4.1. Llamada al SW Anexos
					anular.setRespuesta(ContratacionExtConfirmacionWS.anularCupon(idCupon, realPath));
					// 5. Resultado del servicio web;
					if (StringUtils.isNullOrEmpty(idCupon)) {
						codValidacion = COD_OK_RGA_KO_AGROSEGURO;
						msgValidacion = idCupon;
					} else {
						codValidacion = COD_OK_TODO;
						msgValidacion = idCupon;
					}
				} else {
					msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
				}
			} else {
				msgValidacion = "ERROR VALIDACION POLIZA NO EXISTE O NO TIENE UN CUPON ACTIVO.";
				// AL NO TENER POLIZA NO PODEMOS OBTENER LA ENTIDAD DEL COLECTIVO
				codigoInternoEntidad = "XXXXXXXX";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		// 5.3; Resultado; XML devuelto por SW Confirmación de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		anular.setCodigo(codValidacion);
		anular.setMensaje(msgValidacion);
		long idAuditoria = guardarAuditoriaAnexosExt(codigoInternoEntidad, codValidacion, msgValidacion, null, null,
				idCupon, anular.getRespuesta(), null, null, null, null, AuditoriaAnexosExt.SERV_AC);
		if (!StringUtils.isNullOrEmpty(idCupon) && idAuditoria > 0) {
			correduriaExternaDao.anularCupon(idCupon);
		}
		logger.debug("----------------------------------------------------------------------");
		return anular;
	}

	/**
	 * Método principal de la clase para realizar la solicitud de cupón de un
	 * mediador
	 */
	public SolicitarCuponExtBean doSolicitarCupon(final BigDecimal plan, final String referencia, final String realPath)
			throws AgrWSException, Exception {
		SolicitarCuponExtBean solicitar = new SolicitarCuponExtBean();
		String idCupon = null;
		int codValidacion = COD_KO_RGA;
		String msgValidacion;
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición solicitud cupón mediador. Referencia " + referencia + " Plan " + plan);
		String codigoInternoEntidad = null;
		try {
			// 3. Validaciones
			codigoInternoEntidad = correduriaExternaDao.getCorreduriaPoliza(plan, referencia);
			if (!StringUtils.isNullOrEmpty(codigoInternoEntidad)) {
				if (validarCorreduriaExterna(codigoInternoEntidad)) {
					// 4.1. Llamada al SW Anexos
					Map<Integer, Base64Binary> response = ContratacionExtConfirmacionWS.solicitarCupon(plan, referencia,
							realPath);
					
					solicitar.setPoliza(response.get(ContratacionExtConfirmacionWS.MAP_POS_POLIZA));
					solicitar.setPolizaComp(response.get(ContratacionExtConfirmacionWS.MAP_POS_POLIZA_COMP));
					solicitar.setEstadoContratacion(response.get(ContratacionExtConfirmacionWS.MAP_POS_ESTADO_CONT));
					Base64Binary xmlCupon = response.get(ContratacionExtConfirmacionWS.MAP_POS_CUPON_MOD);
					solicitar.setCuponModificacion(response.get(ContratacionExtConfirmacionWS.MAP_POS_CUPON_MOD));
					solicitar.setPolizaRC(response.get(ContratacionExtConfirmacionWS.MAP_POS_POLIZA_RC));
					solicitar.setPolizaCompRC(response.get(ContratacionExtConfirmacionWS.MAP_POS_POLIZA_COMP_RC));;
					if (xmlCupon != null) {
						byte[] byteCupon = response.get(ContratacionExtConfirmacionWS.MAP_POS_CUPON_MOD).getValue();
						if (byteCupon != null && byteCupon.length > 0) {
							CuponModificacionDocument cuponModificacion = CuponModificacionDocument.Factory
									.parse(new StringReader(new String(byteCupon, Constants.DEFAULT_ENCODING)));
							idCupon = cuponModificacion.getCuponModificacion().getIdCupon();
						}
					}
					// 5. Resultado del servicio web;
					if (StringUtils.isNullOrEmpty(idCupon)) {
						codValidacion = COD_OK_RGA_KO_AGROSEGURO;
						msgValidacion = idCupon;
					} else {
						codValidacion = COD_OK_TODO;
						msgValidacion = idCupon;
					}
				} else {
					msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
				}
			} else {
				msgValidacion = "ERROR VALIDACION POLIZA NO EXISTE.";
				// AL NO TENER POLIZA NO PODEMOS OBTENER LA ENTIDAD DEL COLECTIVO
				codigoInternoEntidad = "XXXXXXXX";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		// 5.3; Resultado; XML devuelto por SW Confirmación de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		solicitar.setCodigo(codValidacion);
		solicitar.setMensaje(msgValidacion);
		long idAuditoria = guardarAuditoriaAnexosExt(codigoInternoEntidad, codValidacion, msgValidacion, referencia,
				plan, idCupon, null, solicitar.getPoliza(), solicitar.getPolizaComp(),
				solicitar.getEstadoContratacion(), solicitar.getCuponModificacion(), AuditoriaAnexosExt.SERV_SC);
		if (!StringUtils.isNullOrEmpty(idCupon) && idAuditoria > 0) {
			correduriaExternaDao.guardarCupon(plan, referencia, idCupon);
		}
		logger.debug("----------------------------------------------------------------------");
		return solicitar;
	}

	/**
	 * Método principal de la clase para realizar la contratación definitiva de
	 * pólizas recibidas a través de un mediador
	 */
	public ConfirmarCuponExtBean doConfirmarCupon(final String idCupon, final Boolean flgRevAdmin,
			final Base64Binary poliza, final Base64Binary polizaComp, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		ConfirmarCuponExtBean confirmar = new ConfirmarCuponExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion = null;
		String codigoInternoEntidad = null;
		boolean complementaria = false;
		Base64Binary acuseRecibo = null;
		Error[] errores = null;
		Boolean isAnexRedCap = Boolean.FALSE;
		String referencia = "";
		BigDecimal plan = null;
		Long estadoCupon = new Long(-1);
		String codUsuario = null;
		String strPolizaXML = "";
		
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición confirmación cupón mediador. IdCupon " + idCupon);
		
		try {
			// 3. Validaciones
			// Comprobar si existe poliza
			ConfirmarExtPolizaBean polizaObj = null;
			Poliza pol = new Poliza();
			PolizaReduccionCapital polizaRC = null;
			char tipoRef = ' ';

			// Comprobamos si se trata de una Reducciond e Capital
			if (poliza != null) {
				logger.debug("Comprobamos si se trata de una Reducciond e Capital" );
				strPolizaXML = new String(poliza.getValue(), Constants.DEFAULT_ENCODING);
				logger.debug("strPolizaXml: " + strPolizaXML);

				polizaRC = getAnexRedCap(strPolizaXML);
			}

			if (polizaRC == null) {
				logger.debug("Es un anexo de Modificacion" );
				if (poliza != null && polizaComp == null) {
					logger.debug("Obteniendo valores de poliza principal");
					polizaObj = parseAPoliza(poliza);
					tipoRef = 'P';
				} else if (poliza == null && polizaComp != null) {
					logger.debug("Obteniendo valores de poliza complementaria");
					polizaObj = parseAPoliza(polizaComp);
					tipoRef = 'C';
					complementaria = true;
				}

				referencia = polizaObj.getReferencia();
				plan = new BigDecimal(polizaObj.getPlan());
				
				logger.debug("Obteniendo la poliza con ref " + polizaObj.getReferencia());

				// Recuperamos la poliza
				pol = polizaDao.getPolizaByRefPlanLin(referencia, tipoRef, plan,
						BigDecimal.valueOf(polizaObj.getLinea()));
			} else {
				logger.debug("Es una anexo de Reduccion de Capital");
				// Anexo de Reduccion de Capital
				isAnexRedCap = Boolean.TRUE;
				// Solo esta permitido polizas principales
				tipoRef = 'P';
				referencia = polizaRC.getReferencia();
				plan = new BigDecimal(polizaRC.getPlan());
				
				logger.debug(
						"Obteniendo la poliza con ref " + polizaRC.getReferencia() + " y plan " + polizaRC.getPlan());

				// Recuperamos la poliza sin la línea ya que no la tenemos
				pol = polizaDao.getPolizaByReferenciaPlan(referencia, tipoRef, plan);
			}

			if (pol != null) {
				// Obtenemos el usuario 
				codUsuario = pol.getUsuario().getCodusuario();
				
				// Validaciones RGA
				codigoInternoEntidad = correduriaExternaDao.getCorreduriaCupon(idCupon);
				if (!StringUtils.isNullOrEmpty(codigoInternoEntidad)) {
					if (validarCorreduriaExterna(codigoInternoEntidad)) {
						// Llamada al SW de Confirmacion
						logger.debug("Llamando al SW de Confirmacion.....");
						acuseRecibo = ContratacionExtConfirmacionWS.confirmarCupon(idCupon, flgRevAdmin, poliza,
								polizaComp, realPath, isAnexRedCap);
						
						confirmar.setAcuseRecibo(acuseRecibo);
						// Resultado del servicio web;
						logger.debug("Procesando el resultado del SW de Confirmacion.....");
						AcuseRecibo acuseReciboObj = procesarAcuseRecibo(acuseRecibo);
						Documento docAcuseRecibo = getAcuseRecibo(acuseReciboObj);

						confirmar.setCodigo(docAcuseRecibo.getEstado());

						logger.debug("Estado del acuse: " + confirmar.getCodigo());
						if (confirmar.getCodigo() != -1) {
							msgValidacion = "CUPON " + idCupon;
							
							if (confirmar.getCodigo() == Constants.ACUSE_RECIBO_ESTADO_CORRECTO) {
								logger.debug("Acuse de Recibo Correcto.....");
								codValidacion = COD_OK_TODO;
								// 6-Confirmado-Trámite
								estadoCupon = (long) Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO;
							} else if (confirmar.getCodigo() == Constants.ACUSE_RECIBO_ESTADO_ACEPTADO_PDTE_REV_ADM) {
								logger.debug("Acuse de Recibo aceptado pdte de rev administrativa.....");
								codValidacion = COD_OK_TODO;
								// 7-Confirmado-Aplicado
								estadoCupon = (long) Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE;
							} else if (confirmar.getCodigo() == Constants.ACUSE_RECIBO_ESTADO_RECHAZADO) {
								logger.debug("Acuse de Recibo Rechazado.....");
								codValidacion = COD_OK_RGA_KO_AGROSEGURO;
								// 4-Error-Rechazado
								estadoCupon = (long) Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO;

								errores = docAcuseRecibo.getErrorArray();

								if (errores != null && errores.length > 0) {
									// Recuperamos los errores para mostrarlos
									List<ConfirmarCuponExtBean.AgrError> agrs = new ArrayList<ConfirmarCuponExtBean.AgrError>();
									for (Error error : errores) {
										ConfirmarCuponExtBean.AgrError agr = new ConfirmarCuponExtBean.AgrError();
										int codigo = -1;
										try {
											codigo = Integer.parseInt(error.getCodigo());
										} catch (NumberFormatException nfe) {
											logger.error("Error al parsear el codigo de error a int", nfe);
										}
										agr.setCodigo(codigo);
										agr.setMensaje(error.getDescripcion());
										agrs.add(agr);
									}
									confirmar.setAgrErrors(
											agrs.toArray(new ConfirmarCuponExtBean.AgrError[agrs.size()]));
								}
							}

							if (Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon)
									|| Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon)) {
								logger.debug("Anexo confirmado en Agroseguro.....");								
								guardarEnAgroplus(idCupon, referencia, codUsuario, estadoCupon, pol, complementaria,
										realPath, plan, isAnexRedCap, strPolizaXML, polizaRC, acuseReciboObj);
								logger.debug("Anexo creado en Agroplus.....");
							}
						}
					} else {
						msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
					}
				} else {
					msgValidacion = "ERROR NO TIENE UN CUPON ACTIVO.";
					// AL NO TENER POLIZA NO PODEMOS OBTENER LA ENTIDAD DEL COLECTIVO
					codigoInternoEntidad = "XXXXXXXX";
				}
			} else {
				if (isAnexRedCap) {
					msgValidacion = "La Póliza de la Reducción de capital no existe en AgroPlus. Para continuar con la carga, previamente cargue la Póliza manualmente en el sistema AgroPlus";
				} else {
					msgValidacion = "La Póliza del Anexo de modificación no existe en AgroPlus. Para continuar con la carga, previamente cargue la Póliza manualmente en el sistema AgroPlus";
				}

				codigoInternoEntidad = "XXXXXXXX";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (DAOException e) {
			// El RC se ha contratado pero ha fallado al crearlo en Agroplus
			logger.debug("Se ha producido un error al crear la RC en Agroplus.....");
			msgValidacion = msgValidacion + " contratado en Agroseguro pero se ha producido un error al crearlo en AgroPlus";
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}

		// 5.3; Resultado; XML devuelto por SW Confirmación de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO: " + codValidacion + " MENSAJE " + msgValidacion);
		confirmar.setCodigo(codValidacion);
		confirmar.setMensaje(msgValidacion);
		guardarAuditoriaAnexosExt(codigoInternoEntidad, codValidacion, msgValidacion, null, null, idCupon,
				confirmar.getAcuseRecibo(), poliza, polizaComp, null, null, AuditoriaAnexosExt.SERV_CA);
		logger.debug("----------------------------------------------------------------------");

		return confirmar;
	}

	/**
	 * Método principal de la clase para realizar la contratación definitiva de
	 * pólizas recibidas a través de un mediador
	 */
	public ConfirmarExtBean doConfirmar(final Base64Binary entrada, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		ConfirmarExtBean confirmar = new ConfirmarExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion;
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición contratación mediador.");
		ConfirmarExtPolizaBean poliza = parseAPoliza(entrada);
		logger.debug("Poliza procesada. Referencia " + poliza.getReferencia() + " Linea " + poliza.getLinea() + " plan "
				+ poliza.getPlan());
		Base64Binary acuseRecibo = null;
		String codigoInternoEntidad = poliza.getCodigoInternoEntidad();
		long idImportacion = 0;
		try {
			// 3. Validaciones
			if (validarCorreduriaExterna(codigoInternoEntidad)) {
				if (validarGastosYComisiones(poliza)) {
					// 4.1. Llamada al SW Confirmación
					acuseRecibo = ContratacionExtConfirmacionWS.confirmarPoliza(entrada, realPath);
					confirmar.setAcuseRecibo(acuseRecibo);
					// 5. Resultado del servicio web;
					AcuseRecibo acuseReciboObj = procesarAcuseRecibo(acuseRecibo);
					Documento docAcuseRecibo = getAcuseRecibo(acuseReciboObj);
					String referencia = tratarAcuseRecibo(docAcuseRecibo, docAcuseRecibo.getEstado());
					logger.debug("Referencia del acuse de la póliza; " + referencia);
					Error[] errores = null;
					if (referencia != null) {
						idImportacion = guardarEnImportacionPolizasExt(poliza, referencia, acuseReciboObj);
					} else {
						errores = docAcuseRecibo.getErrorArray();
					}
					if (errores != null && errores.length > 0) {
						List<ConfirmarExtBean.AgrError> agrs = new ArrayList<ConfirmarExtBean.AgrError>();
						codValidacion = COD_OK_RGA_KO_AGROSEGURO;
						for (Error error : errores) {
							ConfirmarExtBean.AgrError agr = new ConfirmarExtBean.AgrError();
							int codigo = -1;
							try {
								codigo = Integer.parseInt(error.getCodigo());
							} catch (NumberFormatException nfe) {
								logger.error("Error al parsear el codigo de error a int", nfe);
							}
							agr.setCodigo(codigo);
							agr.setMensaje(error.getDescripcion());
							agrs.add(agr);
						}
						confirmar.setAgrErrors(agrs.toArray(new ConfirmarExtBean.AgrError[agrs.size()]));
						msgValidacion = "";
					} else {
						codValidacion = COD_OK_TODO;
						msgValidacion = referencia;
					}
				} else {
					msgValidacion = "ERROR VALIDACION COMISIONES.";
				}
			} else {
				msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (ConfirmacionServiceException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		// 5.3; Resultado; XML devuelto por SW Confirmación de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		confirmar.setCodigo(codValidacion);
		confirmar.setMensaje(msgValidacion);
		long idAuditoria = guardarAuditoriaConfirmacionExt(codigoInternoEntidad, codValidacion, msgValidacion, entrada,
				acuseRecibo);
		if (idImportacion > 0 && idAuditoria > 0) {
			actualizarTablaImportacion(idImportacion, idAuditoria);
		}
		logger.debug("----------------------------------------------------------------------");
		return confirmar;
	}

	private void actualizarTablaImportacion(final long idImportacion, final long idAuditoria) {
		try {
			ImportacionPolizasExt importacion = new ImportacionPolizasExt();
			importacion.setIdEnvio(idAuditoria);
			importacion.setId(idImportacion);
			importacionPolizasExtDao.updateIdEnvio(importacion);
		} catch (Exception e) {
			logger.error("Excepción al intentar actualizar la tabla de importación; " + e.getMessage(), e);
		}
	}
	
	private long guardarAuditoriaSiniestroExt(final String codigoInterno, final int resultado, final String mensaje,
			final Base64Binary entrada, final Base64Binary acuseRecibo) {
		long idAuditoria = 0;
		try {
			AuditoriaSiniestrosExt auditoria = new AuditoriaSiniestrosExt();
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setEntrada(Hibernate.createClob(WSUtils.getStringResponse(entrada)));
			auditoria.setHoraLlamada(new Date());
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			if (acuseRecibo != null) {
				auditoria.setSalida(Hibernate.createClob(WSUtils.getStringResponse(acuseRecibo)));
			}
			auditoriaConfirmacionExtDao.saveAuditoriaSiniestro(auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}

	private long guardarAuditoriaConfirmacionExt(final String codigoInterno, final int resultado, final String mensaje,
			final Base64Binary entrada, final Base64Binary acuseRecibo) {
		long idAuditoria = 0;
		try {
			AuditoriaConfirmacionExt auditoria = new AuditoriaConfirmacionExt();
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setEntrada(Hibernate.createClob(WSUtils.getStringResponse(entrada)));
			auditoria.setHoraLlamada(new Date());
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			if (acuseRecibo != null) {
				auditoria.setSalida(Hibernate.createClob(WSUtils.getStringResponse(acuseRecibo)));
			}
			auditoriaConfirmacionExtDao.saveAuditoriaConfirmacion(auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}

	private long guardarAuditoriaAnexosExt(final String codigoInterno, final int resultado, final String mensaje,
			final String referencia, final BigDecimal plan, final String idCupon, final Base64Binary acuseRecibo,
			final Base64Binary poliza, final Base64Binary polizaComp, final Base64Binary estadoCont,
			final Base64Binary cuponModificacion, final String servicio) {
		long idAuditoria = 0;
		try {
			AuditoriaAnexosExt auditoria = new AuditoriaAnexosExt();
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setReferencia(referencia);
			auditoria.setPlan(plan);
			auditoria.setIdCupon(idCupon);
			auditoria.setHoraLlamada(new Date());
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			auditoria.setServicio(servicio);
			if (acuseRecibo != null) {
				auditoria.setAcuseRecibo(Hibernate.createClob(WSUtils.getStringResponse(acuseRecibo)));
			}
			if (poliza != null) {
				auditoria.setPoliza(Hibernate.createClob(WSUtils.getStringResponse(poliza)));
			}
			if (polizaComp != null) {
				auditoria.setPolizaComp(Hibernate.createClob(WSUtils.getStringResponse(polizaComp)));
			}
			if (estadoCont != null) {
				auditoria.setEstadoCont(Hibernate.createClob(WSUtils.getStringResponse(estadoCont)));
			}
			if (cuponModificacion != null) {
				auditoria.setCuponModificacion(Hibernate.createClob(WSUtils.getStringResponse(cuponModificacion)));
			}
			auditoriaConfirmacionExtDao.saveAuditoriaAnexo(auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}
	
	private long guardarAuditoriaCalcAnxExt(final String codigoInterno, 
											final String referencia, 
											final int plan, 
											final String tipoPoliza, 
											final String idCupon,  
											final int calcularSituacionActual, 
											final Base64Binary polizaModificacion, 
											final Base64Binary calculoModificacion, 
											final Base64Binary calculoOriginal,
											final Base64Binary diferenciasCoste,
											final int resultado,
											final String mensaje,
											final String servicio) {
		
		long idAuditoria = 0;
		
		try {
			
			AuditoriaCalcAnxExt auditoria = new AuditoriaCalcAnxExt();
			
			auditoria.setHoraLlamada(new Date());
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setReferencia(referencia);
			auditoria.setPlan(new BigDecimal(plan));
			auditoria.setTipoPoliza(tipoPoliza);
			auditoria.setIdCupon(idCupon);
			auditoria.setCalcularSituacionActual(new BigDecimal(calcularSituacionActual));
			
			if (polizaModificacion != null) {
				auditoria.setPolizaModificacion(Hibernate.createClob(WSUtils.getStringResponse(polizaModificacion)));
			}
			if (calculoModificacion != null) {
				auditoria.setCalculoModificacion(Hibernate.createClob(WSUtils.getStringResponse(calculoModificacion)));
			}
			if (calculoOriginal != null) {
				auditoria.setCalculoOriginal(Hibernate.createClob(WSUtils.getStringResponse(calculoOriginal)));
			}
			if (diferenciasCoste != null) {
				auditoria.setDiferenciasCoste(Hibernate.createClob(WSUtils.getStringResponse(diferenciasCoste)));
			}
			
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			auditoria.setServicio(servicio);
			auditoriaConfirmacionExtDao.saveAuditoriaCalculoAnexo(auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
			
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}
	
	
	/**
	 * Devuelve el objeto Poliza de contratacion de Agroseguro a partir del id
	 * de cupón indicado
	 * 
	 * @param referencia
	 * @param plan
	 * @param realPath
	 * @param codUsuario
	 * @param idCupon
	 * @return
	 */
	public XmlObject getPolizaActualizadaFromCuponAM(final String referencia, final BigDecimal plan,
			final String realPath, String codUsuario, String idCupon) {
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
		XmlObject poliza = null;
		boolean isPolizaGanado;
		try {
			isPolizaGanado = polizaDao.esPolizaGanado(referencia, plan);

			// LLamamos al servicio de consulta de la contratacion
			respuesta = new SWAnexoModificacionHelper().getPolizaActualizadaUnificada(referencia, plan, realPath,
					isPolizaGanado);

			// XML de la poliza principal
			EnviosSWXML enviosSWXMLPpal = new EnviosSWXML();
			if (isPolizaGanado) {
				enviosSWXMLPpal.setXml(Hibernate.createClob(respuesta.getPolizaGanado().toString()));
			} else {
				enviosSWXMLPpal.setXml(Hibernate.createClob(respuesta.getPolizaPrincipalUnif().toString()));
			}

			// XML del estado de la contratacion
			EnviosSWXML enviosSWXMLEstadoCont = new EnviosSWXML();
			enviosSWXMLEstadoCont.setXml(Hibernate.createClob(respuesta.getEstadoContratacion().toString()));

			// XML de la poliza complementaria
			EnviosSWXML enviosSWXMLCpl = null;
			if (respuesta.getPolizaComplementariaUnif() != null) {
				enviosSWXMLCpl = new EnviosSWXML();
				enviosSWXMLCpl.setXml(Hibernate.createClob(respuesta.getPolizaComplementariaUnif().toString()));
			}

			// cupón
			EnviosSWSolicitud enviosSWSolicitud = new EnviosSWSolicitud();
			enviosSWSolicitud.setCodusuario(codUsuario);
			enviosSWSolicitud.setFecha(new Date());
			enviosSWSolicitud.setPlan(plan);
			enviosSWSolicitud.setReferencia(referencia);
			enviosSWSolicitud.setEnviosSWXMLByIdxmlPpal(enviosSWXMLPpal);
			enviosSWSolicitud.setEnviosSWXMLByIdxmlEstadoContratacion(enviosSWXMLEstadoCont);
			enviosSWSolicitud.setEnviosSWXMLByIdxmlCpl(enviosSWXMLCpl);
			enviosSWSolicitud.setIdcupon(idCupon);

			try {
				enviosSWSolicitudDao.saveOrUpdate(enviosSWSolicitud);
			} catch (DAOException e) {
				logger.error("Error al insertar el registro de la comunicacion con el SW de Solicitud de Modificacion",
						e);
			}

			if (isPolizaGanado) {
				if (respuesta.getPolizaGanado() != null) {
					poliza = respuesta.getPolizaGanado();
				}
			} else {
				if (respuesta.getPolizaPrincipalUnif() != null) {
					poliza = respuesta.getPolizaPrincipalUnif();
				} else if (respuesta.getPolizaComplementariaUnif() != null) {
					poliza = respuesta.getPolizaComplementariaUnif();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return cargarPolizaFromXml(e.getEnviosSWXMLByIdxmlPpal().getXml());
		return poliza;
	}
	
	public PolizaActualizadaRCResponse getPolizaActualizadaFromCuponRC(final String referencia, final BigDecimal plan,
			final String realPath, String codUsuario, String idCupon) {
		PolizaActualizadaRCResponse respuesta = new PolizaActualizadaRCResponse();
		//XmlObject poliza = null;
		try {
			// LLamamos al servicio de consulta de la contratacion
			respuesta = new SWAnexoRCHelper().consultarContratacionRC(referencia, plan, realPath);

			// XML de la poliza principal
			EnviosSWXMLRC enviosSWXMLPpal = new EnviosSWXMLRC();
			enviosSWXMLPpal.setXml(Hibernate.createClob(respuesta.getPolizaPrincipalUnif().toString()));

			// XML del estado de la contratacion
			EnviosSWXMLRC enviosSWXMLEstadoCont = new EnviosSWXMLRC();
			enviosSWXMLEstadoCont.setXml(Hibernate.createClob(respuesta.getEstadoContratacion().toString()));

			// Cupon
			EnviosSWSolicitudRC enviosSWSolicitud = new EnviosSWSolicitudRC();

			enviosSWSolicitud.setCodusuario(codUsuario);
			enviosSWSolicitud.setFecha(new Date());
			enviosSWSolicitud.setPlan(plan);
			enviosSWSolicitud.setReferencia(referencia);
			enviosSWSolicitud.setEnviosSWXMLRCByIdxmlPpal(enviosSWXMLPpal);
			enviosSWSolicitud.setEnviosSWXMLRCByIdxmlEstadoContratacion(enviosSWXMLEstadoCont);
			enviosSWSolicitud.setIdcupon(idCupon);

			try {
				enviosSWRCSolicitudDao.saveOrUpdate(enviosSWSolicitud);
			} catch (DAOException e) {
				logger.error("Error al insertar el registro de la comunicacion con el SW de Solicitud de RC", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respuesta;
	}

	/**
	 * Transformación de Base64Binary a objeto Poliza
	 * 
	 * @param polizaB64
	 *            Base64Binary
	 * @return Poliza
	 * @throws Exception
	 */
	private static ConfirmarExtPolizaBean parseAPoliza(final Base64Binary polizaB64) throws Exception {
		ConfirmarExtPolizaBean polizaComun = new ConfirmarExtPolizaBean();
		XmlObject polizaXML = null;
		try {
			String strPolizaXML = new String(polizaB64.getValue(), Constants.DEFAULT_ENCODING);
			polizaXML = XmlObject.Factory.parse(strPolizaXML);
			if (polizaXML instanceof es.agroseguro.seguroAgrario.contratacion.PolizaDocument) {
				es.agroseguro.seguroAgrario.contratacion.Poliza poliza = ((es.agroseguro.seguroAgrario.contratacion.PolizaDocument) polizaXML).getPoliza();
				polizaComun.setLinea(poliza.getLinea());
				polizaComun.setReferencia(poliza.getReferencia());
				polizaComun.setFechaPago(poliza.getPago().getFecha());
				polizaComun.setFechaFirma(poliza.getFechaFirmaSeguro());
				polizaComun.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());
				polizaComun.setPlan(poliza.getPlan());
				ConfirmarExtGCBean comisiones = new ConfirmarExtGCBean();
				comisiones.setGastosAdmon(poliza.getEntidad().getGastos().getAdministracion());
				comisiones.setGastosAdquisicion(poliza.getEntidad().getGastos().getAdquisicion());
				comisiones.setComisionMediador(poliza.getEntidad().getGastos().getComisionMediador());
				polizaComun.getComisiones().put("", comisiones);				
			} else if (polizaXML instanceof es.agroseguro.contratacion.PolizaDocument) {
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaXML)
						.getPoliza();
				polizaComun.setLinea(poliza.getLinea());
				polizaComun.setReferencia(poliza.getReferencia());
				polizaComun.setFechaPago(poliza.getPago().getFecha());
				polizaComun.setFechaFirma(poliza.getFechaFirmaSeguro());
				polizaComun.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());
				polizaComun.setPlan(poliza.getPlan());
				Gastos[] gastos = poliza.getEntidad().getGastosArray();
				for (Gastos gasto : gastos) {
					ConfirmarExtGCBean comisiones = new ConfirmarExtGCBean();
					if (gasto.getAdministracion() != null) {
						comisiones.setGastosAdmon(gasto.getAdministracion());
					}
					if (gasto.getAdquisicion() != null) {
						comisiones.setGastosAdquisicion(gasto.getAdquisicion());
					}
					if (gasto.getComisionMediador() != null) {
						comisiones.setComisionMediador(gasto.getComisionMediador());
					}
					polizaComun.getComisiones().put(gasto.getGrupoNegocio(), comisiones);
				}
			}
		} catch (Exception e) {
			logger.error(
					"Error al transformar la poliza pasada en Base64Binary a objecto Poliza. ERROR; " + e.getMessage());
			throw e;
		}

		return polizaComun;
	}

	/**
	 * Transformación de Base64Binary a objeto Siniestro
	 * 
	 * @param siniestroB64
	 *            Base64Binary
	 * @return Siniestro
	 * @throws Exception
	 */
	private static ConfirmarExtSiniestroBean parseASiniestro(final Base64Binary siniestroB64) throws Exception {
		ConfirmarExtSiniestroBean siniestroComun = new ConfirmarExtSiniestroBean();
		XmlObject siniestroXML = null;
		try {
			String strSiniestroXML = new String(siniestroB64.getValue(), Constants.DEFAULT_ENCODING);
			siniestroXML = XmlObject.Factory.parse(strSiniestroXML);
			if (siniestroXML instanceof SiniestroDocument) {
				es.agroseguro.seguroAgrario.siniestros.Siniestro siniestro = ((SiniestroDocument) siniestroXML)
						.getSiniestro();
				siniestroComun.setLinea(siniestro.getPoliza().getLinea());
				siniestroComun.setPlan(siniestro.getPoliza().getPlan());
				siniestroComun.setReferencia(siniestro.getPoliza().getReferenciaPoliza());
			}
		} catch (Exception e) {
			logger.error("Error al transformar el siniestro pasado en Base64Binary a objecto Siniestro. ERROR; "
					+ e.getMessage());
			throw e;
		}

		return siniestroComun;
	}
	
	// Pet. 79361
	@SuppressWarnings("unchecked")
	private PolizaReduccionCapital getAnexRedCap (String xml) throws JAXBException {
		try {
			PolizaReduccionCapital polizaRC = new PolizaReduccionCapital();
			
			JAXBContext jaxbContext = JAXBContext.newInstance(polizaRC.getClass().getPackage().getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			StringReader reader = new StringReader(xml);
			JAXBElement<Object> jObj = null;

			jObj = (JAXBElement<Object>) unmarshaller.unmarshal(new InputSource(reader));
			
			polizaRC = (PolizaReduccionCapital) jObj.getValue();
			
			return polizaRC;
		} catch (Exception e) {
			return null;
		}		
	}	
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	/**
	 * Transformación de Base64Binary a objeto Poliza
	 * 
	 * @param polizaB64
	 *            Base64Binary
	 * @return Poliza
	 * @throws Exception
	 */
	private CalcularExtPolizaBean parseAPresupuesto(final Base64Binary polizaB64) throws Exception {
		CalcularExtPolizaBean polizaComun = new CalcularExtPolizaBean();
		logger.debug("Dentro de parseAPolizaCalc [INIT]");
		XmlObject polizaXML = null;
		try {
			String strPolizaXML = new String(polizaB64.getValue(), Constants.DEFAULT_ENCODING);
			logger.debug("strPolizaXml;" + strPolizaXML);
			polizaXML = XmlObject.Factory.parse(strPolizaXML);
			logger.debug("Valor de polizaXML;" + polizaXML.toString());
			if (polizaXML instanceof es.agroseguro.presupuestoContratacion.PolizaDocument) {
				logger.debug("Entramos en el PresupuestoContratacion - Formato UNIFICADO");
				es.agroseguro.presupuestoContratacion.Poliza poliza = ((es.agroseguro.presupuestoContratacion.PolizaDocument) polizaXML)
						.getPoliza();
				ConfirmarExtPolizaBean confirmarExtPoliza = new ConfirmarExtPolizaBean();
				confirmarExtPoliza.setLinea(poliza.getLinea());
				confirmarExtPoliza.setPlan(poliza.getPlan());
				confirmarExtPoliza.setFechaPago(poliza.getPago().getFecha());
				confirmarExtPoliza.setFechaFirma(poliza.getFechaFirmaSeguro());
				
				confirmarExtPoliza.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());
				
				Gastos[] gastos = poliza.getEntidad().getGastosArray();
				for (Gastos gasto : gastos) {
					ConfirmarExtGCBean comisiones = new ConfirmarExtGCBean();
					if (gasto.getAdministracion() != null) {
						comisiones.setGastosAdmon(gasto.getAdministracion());
					}
					if (gasto.getAdquisicion() != null) {
						comisiones.setGastosAdquisicion(gasto.getAdquisicion());
					}
					if (gasto.getComisionMediador() != null) {
						comisiones.setComisionMediador(gasto.getComisionMediador());
					}
					confirmarExtPoliza.getComisiones().put(gasto.getGrupoNegocio(), comisiones);
				}
				polizaComun.setConfirmarExtPoliza(confirmarExtPoliza);
				polizaComun.setIdColectivo(poliza.getColectivo().getReferencia());
				polizaComun.setDc(poliza.getColectivo().getDigitoControl());
			} else if (polizaXML instanceof es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument) {
				logger.debug("Entramos en el calculoSeguroAgrario - Formato NO UNIFICADO");
				es.agroseguro.seguroAgrario.calculoSeguroAgrario.Poliza poliza = ((es.agroseguro.seguroAgrario.calculoSeguroAgrario.PolizaDocument) polizaXML)
						.getPoliza();
				ConfirmarExtPolizaBean confirmarExtPoliza = new ConfirmarExtPolizaBean();
				confirmarExtPoliza.setLinea(poliza.getLinea());
				confirmarExtPoliza.setPlan(poliza.getPlan());
				confirmarExtPoliza.setFechaPago(poliza.getPago().getFecha());
				confirmarExtPoliza.setFechaFirma(poliza.getFechaFirmaSeguro());
				
				confirmarExtPoliza.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());
				
				
				ConfirmarExtGCBean comisiones = new ConfirmarExtGCBean();
				comisiones.setGastosAdmon(poliza.getEntidad().getGastos().getAdministracion());
				comisiones.setGastosAdquisicion(poliza.getEntidad().getGastos().getAdquisicion());
				comisiones.setComisionMediador(poliza.getEntidad().getGastos().getComisionMediador());
		
				confirmarExtPoliza.getComisiones().put("", comisiones);
				
				polizaComun.setConfirmarExtPoliza(confirmarExtPoliza);
				polizaComun.setIdColectivo(poliza.getColectivo().getReferencia());
				polizaComun.setDc(poliza.getColectivo().getDigitoControl());
				
			} else if (polizaXML instanceof es.agroseguro.contratacion.PolizaDocument) {
				logger.debug("Entramos en calculoAnexo");
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaXML).getPoliza();
				
				ConfirmarExtPolizaBean confirmarExtPoliza = new ConfirmarExtPolizaBean();
				
				confirmarExtPoliza.setPlan(poliza.getPlan());
				confirmarExtPoliza.setReferencia(poliza.getReferencia());
				confirmarExtPoliza.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());
				
				polizaComun.setConfirmarExtPoliza(confirmarExtPoliza);
				polizaComun.setIdColectivo(poliza.getColectivo().getReferencia());
				polizaComun.setDc(poliza.getColectivo().getDigitoControl());
			} else {
				logger.error("Error al transformar Presupuestopoliza pasada en Base64Binary a objecto Poliza.  ");
			}
		} catch (Exception e) {
			logger.error(
					"Error al transformar la poliza pasada en Base64Binary a objecto Poliza. ERROR; " + e.getMessage());
			throw e;
		}
		logger.debug("Dentro de parseAPolizaCalc [END]");
		return polizaComun;
	}
	
	/**
	 * Transformación de Base64Binary a objeto Poliza
	 * 
	 * @param polizaB64
	 * @return
	 * @throws Exception
	 */
	private CalcularExtPolizaBean parseAPolizaVal(final Base64Binary polizaB64) throws Exception {
		CalcularExtPolizaBean polizaComun = new CalcularExtPolizaBean();
		logger.debug("Dentro de parseAPolizaVal [INIT]");
		XmlObject polizaXML = null;
		try {
			String strPolizaXML = new String(polizaB64.getValue(), Constants.DEFAULT_ENCODING);
			logger.debug("strPolizaXml;" + strPolizaXML);
			polizaXML = XmlObject.Factory.parse(strPolizaXML);
			logger.debug("Valor de polizaXML;" + polizaXML.toString());

			ConfirmarExtPolizaBean confirmarExtPoliza = new ConfirmarExtPolizaBean();

			if (polizaXML instanceof es.agroseguro.contratacion.PolizaDocument) {
				logger.debug("Entramos en validar poliza");
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaXML)
						.getPoliza();

				confirmarExtPoliza.setLinea(poliza.getLinea());
				confirmarExtPoliza.setPlan(poliza.getPlan());
				confirmarExtPoliza.setFechaFirma(poliza.getFechaFirmaSeguro());
				confirmarExtPoliza.setCodigoInternoEntidad(poliza.getEntidad().getCodigoInterno());

				polizaComun.setConfirmarExtPoliza(confirmarExtPoliza);
				polizaComun.setIdColectivo(poliza.getColectivo().getReferencia());
				polizaComun.setDc(poliza.getColectivo().getDigitoControl());
			} else {
				logger.error("Error XML para validar.");
			}
		} catch (Exception e) {
			logger.error(
					"Error al transformar la poliza pasada en Base64Binary a objecto Poliza. ERROR; " + e.getMessage());
			throw e;
		}
		logger.debug("Dentro de parseAPolizaVal [END]");
		return polizaComun;
	}
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	private boolean validarColectivo(final String referencia, int dc) {
		boolean valido = false;
		try {
			List<Colectivo> colectivo = correduriaExternaDao.getColectivo(referencia, dc);

			if (colectivo.size() > 0) {
				valido = true;
			} else {
				logger.debug("El colectivo con referencia  " + referencia + " y dc; " + dc + " no corresponde con ninguna entidad");
			}
		} catch (DAOException dao) {
			logger.error(dao.getMessage());
		}
		return valido;
	}
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */

	private boolean validarCorreduriaExterna(final String codInterno) {
		boolean valido = false;
		try {
			CorreduriaExterna correduria = correduriaExternaDao.getCorreduria(codInterno);
			if (correduria != null) {
				valido = true;
			} else {
				logger.debug("El código interno " + codInterno + " no corresponde con ninguna correduria externa");
			}
		} catch (DAOException dao) {
			logger.error(dao.getMessage());
		}
		return valido;
	}

	private boolean validarGastosYComisiones(final ConfirmarExtPolizaBean poliza) throws Exception {
		boolean valido = false;
		try {
			String mensaje = "";
			logger.debug("Validación de gastos, comisión y gastos de adquisición y administración.");
			Map<String, ConfirmarExtGCBean> cgBeanMap = poliza.getComisiones();
			if (cgBeanMap == null || cgBeanMap.isEmpty()) {
				mensaje += " Sin gastos y/o comisiones.";
			} else {
				for (String gn : cgBeanMap.keySet()) {
					ConfirmarExtGCBean cgBean = cgBeanMap.get(gn);
					BigDecimal comisionMediador = cgBean.getComisionMediador();
					BigDecimal gastosAdmon = cgBean.getGastosAdmon();
					BigDecimal gastosAdquisicion = cgBean.getGastosAdquisicion();
					logger.debug("comisionMediador (" + gn + ") --> " + comisionMediador);
					logger.debug("gastosAdmon (" + gn + ") --> " + gastosAdmon);
					logger.debug("gastosAdquisicion (" + gn + ") --> " + gastosAdquisicion);
					if (comisionMediador == null || comisionMediador.compareTo(BigDecimal.ZERO) == 0)	 {
						mensaje = " % comision mediador incorrecto. ";
					}
					if (gastosAdmon != null && gastosAdmon.compareTo(BigDecimal.ZERO) >= 0 && gastosAdquisicion != null && gastosAdquisicion.compareTo(BigDecimal.ZERO) >= 0) {
						CultivosEntidadesHistorico comisionHco = getCultivosEntidadesHistorico(poliza, gn);
						if (comisionHco == null) {
							// "Comisiones erroneas. No existen en el Hco. ";
							mensaje += " Sin gastos en historico. ";
						} else {
							logger.debug("comisionHco.getPctadministracion() --> " + comisionHco.getPctadministracion());
							logger.debug("comisionHco.getPctadquisicion() --> " + comisionHco.getPctadquisicion());
							if (comisionHco.getPctadministracion().compareTo(gastosAdmon) != 0) {
								mensaje += " % de administracion incorrecto. ";								
							}
							if (comisionHco.getPctadquisicion().compareTo(gastosAdquisicion) != 0) {
								mensaje += " % de adquisicion incorrecto.";								
							}
						}
					} else {
						mensaje += " Poliza sin gastos.";
					}
					if (!StringUtils.isNullOrEmpty(mensaje)) {
						break;
					}
				}
			}
			if (StringUtils.isNullOrEmpty(mensaje)) {
				valido = true;
			} else {
				logger.debug(mensaje);
			}
		} catch (Exception e) {
			logger.error("Error en la validación de gastos, comisión y gastos de adquisición y administración. ERROR; "
					+ e.getMessage());
			throw e;
		}
		return valido;
	}

	private CultivosEntidadesHistorico getCultivosEntidadesHistorico(final ConfirmarExtPolizaBean poliza, final String gn)
			throws Exception {
		logger.debug("Obtención del historico del cultivo de entidades por fecha de efecto. ");
		int plan = poliza.getPlan();
		int linea = poliza.getLinea();
		Calendar fecha = poliza.getFechaPago() == null ? poliza.getFechaFirma() : poliza.getFechaPago();
		int entMed = Integer.parseInt(poliza.getCodigoInternoEntidad().substring(0, 4));
		int subEntMed = Integer.parseInt(poliza.getCodigoInternoEntidad().substring(4));
		CultivosEntidadesHistorico cultivo = null;
		try {
			cultivo = polizasPctComisionesDao.getUltimoHistoricoComision(plan, linea, entMed, subEntMed, fecha, gn);
			logger.debug("CultivosEntidadesHistorico Plan; " + plan + " - Linea;" + linea + " - Fecha Efecto; "
					+ fecha.toString());
			if (cultivo == null) {
				cultivo = polizasPctComisionesDao.getUltimoHistoricoComision(plan, 999, entMed, subEntMed, fecha, gn);
				// Si no hay resultados buscamos por linea generica
				logger.debug("CultivosEntidadesHistorico Plan; " + plan + " - Linea; 999" + " - Fecha Efecto; "
						+ fecha.toString());
			}
		} catch (Exception e) {
			logger.error("Error en la obtención del historico de comisiones por fecha de efecto.", e);
			throw (e);
		}
		return cultivo;
	}

	private AcuseRecibo getTransformarBinary64AAcuseRecibo(final Base64Binary respuesta) throws Exception {
		String acuseXML = new String(respuesta.getValue(), Constants.DEFAULT_ENCODING);
		AcuseReciboDocument acuseReciboDoc = AcuseReciboDocument.Factory.parse(new StringReader(acuseXML));
		return acuseReciboDoc.getAcuseRecibo();
	}

	private AcuseRecibo procesarAcuseRecibo(final Base64Binary b64AcuseRecibo) throws Exception {
		AcuseRecibo acuseRecibo = null;
		// 4.2.1. Poliza aceptada --> grabar en tablas para que proceso batch
		// importacion de polizas externas realice el alta de la póliza
		try {
			// 3. Tratar acuse de recibo
			if (b64AcuseRecibo != null) {
				acuseRecibo = getTransformarBinary64AAcuseRecibo(b64AcuseRecibo);
				logger.debug("Acuse de recibo; " + acuseRecibo.toString());
			}
		} catch (Exception e) {
			logger.error("Error al procesar el acuse de recibo. ERROR; " + e.getMessage());
			throw e;
		}
		return acuseRecibo;
	}

	private long guardarEnImportacionPolizasExt(final ConfirmarExtPolizaBean poliza, final String referencia,
			final AcuseRecibo acuseRecibo) throws Exception {
		String tipoReferencia = tratarTipoReferencia(acuseRecibo);
		ImportacionPolizasExt importacion = new ImportacionPolizasExt();
		importacion.setLinea(poliza.getLinea());
		importacion.setPlan(poliza.getPlan());
		importacion.setEstado(1); // 1.- Pdte. importacion
		importacion.setReferencia(referencia);
		importacion.setTipoRef(tipoReferencia);
		importacion.setFecImportacion(new Date());
		importacion.setDetalle("Contratación póliza externa");
		importacion.setIdEnvio(new Long(0));
		importacionPolizasExtDao.saveImportacionPolizaExt(importacion);
		return importacion.getId();
	}

	private static String tratarTipoReferencia(final AcuseRecibo acuseRecibo) throws Exception {
		String tipoRef = null;
		try {
			String tipoDocumento = acuseRecibo.getTipoDocumento();
			if (COD_COMPLEMENTARIA_SEGURO_CRECIENTE.equals(tipoDocumento)) {
				tipoRef = COD_TIPO_DOCUMENTO_COMPLEMENTARIO;
			} else {
				tipoRef = COD_TIPO_DOCUMENTO_PRINCIPAL;
			}
		} catch (Exception e) {
			logger.error("Error al tratar el tipo de documento. ERROR; " + e.getMessage());
			throw e;
		}
		return tipoRef;
	}	
	
	/**
	 * Método principal de la clase para realizar la validacion de las pólizas
	 * recibidas a través de un mediador
	 */
	public ValidarExtBean doValidar(final Base64Binary entrada, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		ValidarExtBean validar = new ValidarExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion = "";
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición VALIDAR [INIT].");
		logger.debug("Valor de entrada;" + entrada.toString());
		CalcularExtPolizaBean poliza = parseAPolizaVal(entrada);
		
		logger.debug("Poliza procesada. Referencia " + poliza.getConfirmarExtPoliza().getReferencia() + " Linea "
				+ poliza.getConfirmarExtPoliza().getLinea() + " plan " + poliza.getConfirmarExtPoliza().getPlan());
		
		Map<String, Base64Binary> acuseRecibo = null;
		String codigoInternoEntidad = poliza.getConfirmarExtPoliza().getCodigoInternoEntidad();
		
		try {
			// 3. Validaciones.
			if (validarCorreduriaExterna(codigoInternoEntidad)) {
				String idColectivo = poliza.getIdColectivo();
				int dc = poliza.getDc();
				if (validarColectivo(idColectivo, dc)) {
					// 4.1. Llamada al SW Validar

					acuseRecibo = ContratacionExtConfirmacionWS.validarPoliza(entrada, realPath);
					if (acuseRecibo != null && acuseRecibo.containsKey("acuse")) {
						validar.setAcuseRecibo(acuseRecibo.get("acuse"));

						// 5. Resultado del servicio web;
						// Obtenemos el acuse de Recibo
						AcuseRecibo acuseReciboObj = procesarAcuseRecibo(validar.getAcuseRecibo());
						Documento docAcuseRecibo = getAcuseRecibo(acuseReciboObj);
						Error[] errores = null;
						errores = docAcuseRecibo.getErrorArray();

						if (errores != null && errores.length > 0) {
							List<ValidarExtBean.AgrError> agrs = new ArrayList<ValidarExtBean.AgrError>();
							codValidacion = COD_OK_RGA_KO_AGROSEGURO;

							for (Error error : errores) {
								ValidarExtBean.AgrError agr = new ValidarExtBean.AgrError();
								int codigo = -1;
								try {
									codigo = Integer.parseInt(error.getCodigo());
								} catch (NumberFormatException nfe) {
									logger.error("Error al parsear el codigo de error a int", nfe);
								}
								agr.setCodigo(codigo);
								agr.setMensaje(error.getDescripcion());
								agrs.add(agr);
							}
							validar.setAgrErrors(agrs.toArray(new ValidarExtBean.AgrError[agrs.size()]));
							msgValidacion = "";
						} else {
							codValidacion = COD_OK_TODO;
						}
					}
				} else {
					msgValidacion = "ERROR VALIDACIONES COLECTIVO";
				}
			} else {
				msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (ConfirmacionServiceException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		
		// 5.3; Resultado; XML devuelto por SW de Cálculo de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		validar.setCodigo(codValidacion);
		validar.setMensaje(msgValidacion);
		
		long idAuditoria = guardarAuditoriaValidarExt(codigoInternoEntidad, codValidacion, msgValidacion, entrada,
				validar.getAcuseRecibo());
		
		logger.debug ("Valor de idAuditoria; "+idAuditoria);
		
		logger.debug("----------------------------------------------------------------------");
		return validar;
		
	}
	
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Inicio */
	/*
	 * Método principal de la clase para realizar la contratación definitiva de
	 * pólizas recibidas a través de un mediador
	 */
	public CalcularExtBean doCalcular(final Base64Binary entrada, final String realPath)
			throws ConfirmacionServiceException, ConfirmarExtException.AgrWSException, Exception {
		
		CalcularExtBean calcular = new CalcularExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion = "";
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición CALCULO [INIT].");
		logger.debug("Valor de entrada;"+entrada.toString());
		CalcularExtPolizaBean poliza = parseAPresupuesto(entrada);
		
		logger.debug("Poliza procesada. Referencia " + poliza.getConfirmarExtPoliza().getReferencia() + " Linea " + poliza.getConfirmarExtPoliza().getLinea() + " plan "
				+ poliza.getConfirmarExtPoliza().getPlan());
		
		Map<String, Base64Binary> acuseRecibo = null;
		String codigoInternoEntidad = poliza.getConfirmarExtPoliza().getCodigoInternoEntidad();
		
		try {
			// 3. Validaciones.
			if (validarCorreduriaExterna(codigoInternoEntidad)) {
				// De momento comentamos la validación de Gastos y Comisiones
				if (validarGastosYComisiones(poliza.getConfirmarExtPoliza())) {
					String idColectivo = poliza.getIdColectivo();
					int dc = poliza.getDc();
					if (validarColectivo(idColectivo, dc)) {
						// 4.1. Llamada al SW Calculo
						
						acuseRecibo = ContratacionExtConfirmacionWS.calcularPoliza(entrada, realPath);
						if (acuseRecibo != null && acuseRecibo.containsKey("acuse")) {
							calcular.setAcuseRecibo(acuseRecibo.get("acuse"));
							calcular.setCalculo(acuseRecibo.get("calculo"));

						  // 5. Resultado del servicio web;
						  // Obtenemos el acuse de Recibo
						  AcuseRecibo acuseReciboObj = procesarAcuseRecibo(calcular.getAcuseRecibo());
						  Documento docAcuseRecibo = getAcuseRecibo(acuseReciboObj);
						  Error[] errores = null;
						  errores = docAcuseRecibo.getErrorArray();
						  
						  if (errores != null && errores.length > 0) {
						     List<CalcularExtBean.AgrError> agrs = new ArrayList<CalcularExtBean.AgrError>();
							 codValidacion = COD_OK_RGA_KO_AGROSEGURO;
							
							 for (Error error : errores) {
								CalcularExtBean.AgrError agr = new CalcularExtBean.AgrError();
								int codigo = -1;
								try {
									codigo = Integer.parseInt(error.getCodigo());
								} catch (NumberFormatException nfe) {
									logger.error("Error al parsear el codigo de error a int", nfe);
								}
								agr.setCodigo(codigo);
								agr.setMensaje(error.getDescripcion());
								agrs.add(agr);
							 }
							 calcular.setAgrErrors(agrs.toArray(new CalcularExtBean.AgrError[agrs.size()]));
							 msgValidacion = "";
						  } else {
						     codValidacion = COD_OK_TODO;

						  }
					   }	  
					}else {
					   msgValidacion = "ERROR VALIDACIONES COLECTIVO";
					}
						
				} else {
					msgValidacion = "ERROR VALIDACION COMISIONES.";
				}
			} else {
				msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (ConfirmacionServiceException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		
		// 5.3; Resultado; XML devuelto por SW de Cálculo de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		calcular.setCodigo(codValidacion);
		calcular.setMensaje(msgValidacion);
		
		long idAuditoria = guardarAuditoriaCalculoExt(codigoInternoEntidad, codValidacion, msgValidacion, entrada,
				calcular.getAcuseRecibo(), calcular.getCalculo());
		
		logger.debug ("Valor de idAuditoria; "+idAuditoria);
		
		logger.debug("----------------------------------------------------------------------");
		return calcular;
	}
	
	private long guardarAuditoriaValidarExt(final String codigoInterno, final int resultado, final String mensaje,
			final Base64Binary entrada, final Base64Binary acuseRecibo) {
		long idAuditoria = 0;
		try {
			AuditoriaValidarExt auditoria = new AuditoriaValidarExt();
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setHoraLlamada(new Date());
			auditoria.setEntrada(Hibernate.createClob(WSUtils.getStringResponse(entrada)));
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			if (acuseRecibo != null) {
				auditoria.setAcuseRecibo(Hibernate.createClob(WSUtils.getStringResponse(acuseRecibo)));
			}
			
			
			auditoriaConfirmacionExtDao.saveAuditoriaValidar (auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}
	
	private long guardarAuditoriaCalculoExt(final String codigoInterno, final int resultado, final String mensaje,
			final Base64Binary entrada, final Base64Binary acuseRecibo, final Base64Binary calculo) {
		long idAuditoria = 0;
		try {
			AuditoriaCalculoExt auditoria = new AuditoriaCalculoExt();
			auditoria.setCodigoInterno(codigoInterno);
			auditoria.setHoraLlamada(new Date());
			auditoria.setEntrada(Hibernate.createClob(WSUtils.getStringResponse(entrada)));
			auditoria.setResultado(new BigDecimal(resultado));
			auditoria.setMensaje(mensaje);
			if (acuseRecibo != null) {
				auditoria.setAcuseRecibo(Hibernate.createClob(WSUtils.getStringResponse(acuseRecibo)));
			}
			if (calculo != null) {
				auditoria.setCalculo(Hibernate.createClob((WSUtils.getStringResponse(calculo))));
			}
			
			auditoriaConfirmacionExtDao.saveAuditoriaCalculo(auditoria);
			idAuditoria = auditoria.getId();
			logger.debug("Información guardada en la tabla de auditoría");
		} catch (Exception e) {
			logger.error("Excepción al intentar guardar información en la tabla de auditoría; " + e.getMessage(), e);
		}
		return idAuditoria;
	}
	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */

	/* Pet. 73328 ** MODIF TAM (16/03/2021) ** Fin */  

	private static String tratarAcuseRecibo(final Documento docAcuseRecibo, final int estado) {
		String referencia = null;
		if (docAcuseRecibo.getEstado() == 1) {
			DatosAsociados da = docAcuseRecibo.getDatosAsociados();
			referencia = da.getDomNode().getAttributes().getNamedItem(ETIQUETA_ACUSE_RECIBO_REFERENCIA).getNodeValue();
		}
		return referencia;
	}

	private static Documento getAcuseRecibo(final AcuseRecibo acuseRecibo) {
		Documento doc = null;
		if (acuseRecibo.getDocumentoArray(0) != null) {
			doc = acuseRecibo.getDocumentoArray(0);
		}
		return doc;
	}

	public void setPolizasPctComisionesDao(IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}

	public void setImportacionPolizasExtDao(IImportacionPolizasExtDao importacionPolizasExtDao) {
		this.importacionPolizasExtDao = importacionPolizasExtDao;
	}

	public void setCorreduriaExternaDao(ICorreduriaExternaDao correduriaExternaDao) {
		this.correduriaExternaDao = correduriaExternaDao;
	}

	public void setAuditoriaConfirmacionExtDao(IAuditoriaConfirmacionExtDao auditoriaConfirmacionExtDao) {
		this.auditoriaConfirmacionExtDao = auditoriaConfirmacionExtDao;
	}

	public void setCuponDao(ICuponDao cuponDao) {
		this.cuponDao = cuponDao;
	}
	
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public IAnexoModificacionDao getAnexoModificacionDao() {
		return anexoModificacionDao;
	}

	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}
	
	public IEnviosSWSolicitudDao getEnviosSWSolicitudDao() {
		return enviosSWSolicitudDao;
	}

	public void setEnviosSWSolicitudDao(IEnviosSWSolicitudDao enviosSWSolicitudDao) {
		this.enviosSWSolicitudDao = enviosSWSolicitudDao;
	}

	
	/**
	 * @param entrada
	 * @param realPath
	 * @return
	 * @throws Exception 
	 */
	public CalcularAnexoExtBean doCalcularAnexo(final String idCupon, final String tipoPoliza,
			final boolean calculoSituacionActual, final Base64Binary modificacionPoliza, final String realPath)
			throws Exception {
		
		CalcularAnexoExtBean calcularAnexo = new CalcularAnexoExtBean();
		int codValidacion = COD_KO_RGA;
		String msgValidacion = "";
		Boolean isAnexRedCap = Boolean.FALSE;
		
		logger.debug("----------------------------------------------------------------------");
		logger.debug("Recibida petición calcular anexo.");
		
		String strPolizaXML = new String(modificacionPoliza.getValue(), Constants.DEFAULT_ENCODING);
		logger.debug("strPolizaXml;" + strPolizaXML);
		
		PolizaReduccionCapital polizaRC = getAnexRedCap(strPolizaXML);
		CalcularExtPolizaBean poliza;
		Map<String, Base64Binary> respuesta = null;
		
		try {
			if (polizaRC == null) {
				poliza = parseAPresupuesto(modificacionPoliza);
				
				// 3. Validaciones
				if (!StringUtils.isNullOrEmpty(poliza.getConfirmarExtPoliza().getCodigoInternoEntidad())) {
					if (validarCorreduriaExterna(poliza.getConfirmarExtPoliza().getCodigoInternoEntidad())) {
						if (validarColectivo(poliza.getIdColectivo(), poliza.getDc())) {
							
							// 4.1. Llamada al SW Confirmación
							respuesta = ContratacionExtConfirmacionWS.calcularAnx(idCupon, tipoPoliza, calculoSituacionActual, modificacionPoliza, realPath, isAnexRedCap);
							if (respuesta != null) {
								calcularAnexo.setCalculoModificacion(respuesta.get("calculoModificacion"));
								calcularAnexo.setCalculoOriginal(respuesta.get("calculoOriginal"));
								calcularAnexo.setDiferenciasCoste(respuesta.get("diferenciasCoste"));
								codValidacion = COD_OK_TODO;
								msgValidacion = "CALCULO CORRECTO DE ANEXO EN AGROSEGURO";
							} else {
								codValidacion = COD_ERROR_INESPERADO;
								msgValidacion = "SE HA PRODUCIDO UN ERROR AL LLAMAR AL SW DE CALCULO";
							}
						} else {
							msgValidacion = "ERROR VALIDACION COLECTIVO.";
						}
					} else {
						msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
					}
				} else {
					msgValidacion = "ERROR VALIDACION POLIZA NO EXISTE.";
					// AL NO TENER POLIZA NO PODEMOS OBTENER LA ENTIDAD DEL COLECTIVO
				}
			} else {
				// Anexo de Reduccion de Capital
				isAnexRedCap = Boolean.TRUE;
				poliza = new CalcularExtPolizaBean();
				
				int plan = polizaRC.getPlan();
				String referencia = polizaRC.getReferencia();
				String codigoInternoEntidad = correduriaExternaDao.getCorreduriaPoliza(new BigDecimal(plan),
						referencia);

				// 3. Validaciones
				if (validarCorreduriaExterna(codigoInternoEntidad)) {
					ConfirmarExtPolizaBean confirmarExtPoliza = new ConfirmarExtPolizaBean();
					
					confirmarExtPoliza.setCodigoInternoEntidad(codigoInternoEntidad);
					confirmarExtPoliza.setReferencia(referencia);
					confirmarExtPoliza.setPlan(plan);
					
					poliza.setConfirmarExtPoliza(confirmarExtPoliza);

					// 4.1. Llamada al SW Confirmación
					respuesta = ContratacionExtConfirmacionWS.calcularAnx(idCupon, tipoPoliza, calculoSituacionActual,
							modificacionPoliza, realPath, isAnexRedCap);
					if (respuesta!=null) {
						calcularAnexo.setCalculoModificacion(respuesta.get("calculoModificacion"));
						calcularAnexo.setCalculoOriginal(respuesta.get("calculoOriginal"));
						calcularAnexo.setDiferenciasCoste(respuesta.get("diferenciasCoste"));
						codValidacion = COD_OK_TODO;
						msgValidacion = "CALCULO CORRECTO DE ANEXO EN AGROSEGURO";
					}
					else {
						codValidacion = COD_ERROR_INESPERADO;
						msgValidacion = "SE HA PRODUCIDO UN ERROR AL LLAMAR AL SW DE CALCULO";
					}
				} else {
					msgValidacion = "ERROR VALIDACION CORREDURIA EXTERNA.";
				}	
			}
		} catch (ConfirmarExtException.AgrWSException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (ConfirmacionServiceException e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		} catch (Exception e) {
			codValidacion = COD_ERROR_INESPERADO;
			msgValidacion = e.getMessage();
			throw e;
		}
		
		// 5.3; Resultado; XML devuelto por SW Calculo de anexos de Agroseguro
		// 6. Grabar en tabla de auditoría
		logger.debug("RESULTADO CODIGO; " + codValidacion + " MENSAJE " + msgValidacion);
		calcularAnexo.setCodigo(codValidacion);
		calcularAnexo.setMensaje(msgValidacion);
		String codigoInterno = poliza.getConfirmarExtPoliza().getCodigoInternoEntidad();
	    String referencia = poliza.getConfirmarExtPoliza().getReferencia();
	    int plan = poliza.getConfirmarExtPoliza().getPlan();
	    int calcularSituacionAct = (calculoSituacionActual) ? 1 : 0;
		guardarAuditoriaCalcAnxExt(codigoInterno, referencia, plan, tipoPoliza, idCupon, calcularSituacionAct, modificacionPoliza, calcularAnexo.getCalculoModificacion(), 
				calcularAnexo.getCalculoOriginal(), calcularAnexo.getDiferenciasCoste(), codValidacion, msgValidacion, AuditoriaCalcAnxExt.SERV_CL);
		logger.debug("----------------------------------------------------------------------");
				
		return calcularAnexo;
	}
	
	@Override
	public void guardarEnAgroplus(String idCupon, String referencia, String codUsuario, Long estadoCupon, Poliza poliza,
			boolean complementaria, String realPath, BigDecimal plan, Boolean isAnexRedCap, String entradaXML,
			PolizaReduccionCapital polizaRC, AcuseRecibo acuseReciboObj) throws DAOException {
		PolizaActualizadaRCResponse respuesta = null;
		XmlObject polizaXML = null;
		XmlObject polizaRcXML = null;
		
		if (isAnexRedCap) {
			// Se trata de un Anexo de Reduccion de Capital
			logger.debug("Guardamos en Agroplus la Reduccion de Capital.");
			
			// Guardamos el cupon
			// 79361: comento la llamada porque no hace falta (ademas q inserta en cupon historico y en entidades externas no aplica)
			//solicitudReduccionCapManager.saveCupon(null, idCupon, referencia, codUsuario, estadoCupon);
			
			// Obtenemos la situacion actualizada de la poliza llamando a la v2 del servicio
			respuesta = getPolizaActualizadaFromCuponRC(referencia, plan, realPath, codUsuario, idCupon);
			
			if (respuesta != null && respuesta.getPolizaPrincipalUnif() != null) {
				polizaXML = respuesta.getPolizaPrincipalUnif();
				polizaRcXML = respuesta.getPolizaPrincipalRC();
				
				// Obtenemos la Reduccion de Capital desde el xml
				ReduccionCapital redCap = getRedCapFromXml(idCupon, polizaRC, polizaXML, polizaRcXML, estadoCupon);
				
				// Guardamos la Reduccion de Capital
				reduccionCapitalDao.saveOrUpdate(redCap);
				
				// Guardamos los datos de envio
				saveXmlConfRC(redCap, codUsuario, entradaXML, entradaXML, acuseReciboObj);
			}
		} else {
			// Se trata de un Anexo de Modificacion
			logger.debug("Guardamos en Agroplus la Modificacion.");
			
			// Guardamos el cupon
			solicitudModificacionManager.saveCupon(null, idCupon, referencia, codUsuario, estadoCupon);
			
			// Obtenemos la situacion actualizada de la poliza llamando a la v1 del servicio
			polizaXML = getPolizaActualizadaFromCuponAM(referencia, plan, realPath, codUsuario, idCupon);
			
			// Obtenemos el anexo de Modificacion desde el xml
			AnexoModificacion anexo = getAnexoFromXml(idCupon, polizaXML, estadoCupon);
			
			// Guardamos los datos de envio
			saveXmlConfAM(anexo, poliza, codUsuario, complementaria);
		}
	}

	public void saveXmlConfAM(AnexoModificacion anexo, Poliza poliza, String codUsuario, boolean complementaria) {
		EnviosSWXML objXmlAnexo = new EnviosSWXML();
		objXmlAnexo.setXml(anexo.getXml());

		EnviosSWConfirmacion enviosSWConfirmacion = new EnviosSWConfirmacion();
		enviosSWConfirmacion.setEnviosSWXMLByIdxmlPoliza(objXmlAnexo);
		enviosSWConfirmacion.setAnexoModificacion(anexo);
		enviosSWConfirmacion.setEnviosSWXMLByIdxmlAcuse(objXmlAnexo);
		enviosSWConfirmacion.setCodusuario(codUsuario);
		enviosSWConfirmacion.setFecha(new Date());
		enviosSWConfirmacion.setIdcupon(new BigDecimal(anexo.getCupon().getId()));
		enviosSWConfirmacion.setIndRevAdm(new Character('S'));

		// comprobar si la poliza del anexo existe en la tabla de sobreprecios
		validarAnexoPolizaSbp(poliza, anexo, complementaria);
	}
	
	
	public void saveXmlConfRC(ReduccionCapital redCap, String codUsuario, String entradaXML, String strPolizaXML,
			AcuseRecibo acuse) {
		EnviosSWXMLRC objXmlRC = new EnviosSWXMLRC();		
		objXmlRC.setXml(Hibernate.createClob(strPolizaXML));
		
		EnviosSWXMLRC objXmlAcuse = new EnviosSWXMLRC();
		objXmlAcuse.setXml(Hibernate.createClob(acuse.toString()));

		EnviosSWConfirmacionRC enviosSWConfirmacion = new EnviosSWConfirmacionRC();
		enviosSWConfirmacion.setEnviosSWXMLRCByIdxmlPoliza(objXmlRC);
		enviosSWConfirmacion.setReduccionCapital(redCap);
		enviosSWConfirmacion.setEnviosSWXMLRCByIdxmlAcuse(objXmlAcuse);
		enviosSWConfirmacion.setCodusuario(codUsuario);
		enviosSWConfirmacion.setFecha(new Date());
		enviosSWConfirmacion.setIdcupon(new BigDecimal(redCap.getCupon().getId()));
		enviosSWConfirmacion.setIndRevAdm(new Character('S'));		

		try {
			enviosSWRCSolicitudDao.saveOrUpdate(enviosSWConfirmacion);
		} catch (DAOException e) {
			logger.error("Error al insertar el registro de la comunicacion con el SW de Confirmacion de RC", e);
		}
	}
	
	@SuppressWarnings({ "unlikely-arg-type" })
	@Override
	public AnexoModificacion getAnexoFromXml(String idCupon, XmlObject polizaXml,Long estado_cupon) {
		
		AnexoModificacion anexo = new AnexoModificacion();
		es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaXml).getPoliza();
		Poliza polizaOri = null;
		boolean isGanado = false;
		
        //EstadosInc
		EstadosInc estadoAgroseguro = null;
		try {
			estadoAgroseguro = (EstadosInc) anexoModificacionDao.get(EstadosInc.class,'A');
		} catch (DAOException e) {
			e.printStackTrace();
		}
		anexo.setEstadoAgroseguro(estadoAgroseguro);
		
		try {
			polizaOri = polizaDao.getPolizaByRefPlanLin(poliza.getReferencia(),'P', new BigDecimal(poliza.getPlan()), new BigDecimal(poliza.getLinea()));
			anexo.setPoliza(polizaOri);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		try {
			isGanado = polizaDao.esPolizaGanado(poliza.getReferencia(), new BigDecimal(poliza.getPlan()));
		} catch (DAOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//Cupon
		Cupon cupon = null;
		try {
			EstadoCupon estadoCupon = (EstadoCupon) cuponDao.get(EstadoCupon.class,new Long(6));
			
			cupon = new Cupon();
			cupon.setEstadoCupon(estadoCupon);
			cupon.setReferencia(polizaOri.getReferencia());
			cupon.setIdcupon(idCupon); //el recibido como entrada del SW
			cupon.setFecha(new Date());
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		try {
			cuponDao.saveCupon(cupon);
			anexo.setCupon(cupon);
		} catch (DAOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Comunicaciones
		anexo.setComunicaciones(null);
		
		//Estado 
		Estado estado = null;
		try {
			estado = (Estado) anexoModificacionDao.get(Estado.class,new BigDecimal(3));
		} catch (DAOException e) {
			e.printStackTrace();
		}
		anexo.setEstado(estado);
		
		//idcopy
		anexo.setIdcopy(null);
		
		//fechafirmadoc
		anexo.setFechafirmadoc(null);
         
		// Datos del asegurado
		if (poliza.getAsegurado() != null) {
			if (poliza.getAsegurado().getRazonSocial() != null
					&& !StringUtils.nullToString(poliza.getAsegurado().getRazonSocial().getRazonSocial()).equals("")) {
				anexo.setRazsocaseg(StringUtils.nullToString(poliza.getAsegurado().getRazonSocial().getRazonSocial()));
			} else {
				if (poliza.getAsegurado().getNombreApellidos() != null) {
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido1()).equals(""))
						anexo.setApel1aseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido1()));
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido2()).equals(""))
						anexo.setApel2aseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido2()));
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getNombre()).equals(""))
						anexo.setNomaseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getNombre()));
				}
			}
			if (poliza.getAsegurado().getDireccion() != null) {
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getVia()).equals(""))
					anexo.setCalleaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getVia()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getNumero()).equals(""))
					anexo.setNumaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getNumero()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getBloque()).equals(""))
					anexo.setBloqueaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getBloque()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getEscalera()).equals(""))
					anexo.setEscaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getEscalera()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getPiso()).equals(""))
					anexo.setPisoaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getPiso()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getProvincia()).equals(""))
					anexo.setCodprovincia(new BigDecimal(
							StringUtils.nullToString(poliza.getAsegurado().getDireccion().getProvincia())));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getLocalidad()).equals(""))
					anexo.setNomlocalidad(
							StringUtils.nullToString(poliza.getAsegurado().getDireccion().getLocalidad()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getCp()).equals(""))
					anexo.setCodposaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getCp()));
			}
			if (poliza.getAsegurado().getDatosContacto() != null) {
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getEmail()).equals(""))
					anexo.setEmail(StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getEmail()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoFijo()).equals(""))
					anexo.setTelffijoaseg(
							StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoFijo()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoMovil()).equals(""))
					anexo.setTelfmovilaseg(
							StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoMovil()));
			}
		}
		
		if (poliza.getSubvencionesDeclaradas() != null
				&& poliza.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
			anexo.setNumsegsocial(StringUtils
					.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getProvincia())
					+ StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getNumero())
					+ StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getCodigo()));
			anexo.setRegimensegsocial(
					StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen())
							+ "");
		}
	
		if(poliza.getCobertura() != null && poliza.getCobertura().getModulo() != null ) {
			anexo.setCodmodulo(StringUtils.nullToString(poliza.getCobertura().getModulo().trim()));
		}

		anexo.setXml(Hibernate.createClob(polizaXml.toString()));
		
		anexo.setTipoEnvio("SW");
		anexo.setFechaAlta(new Date());
		
		//TODO
		Usuario usuario = null;
		Session session = null;
		try {
			usuario = importacionPolizasExtDao.getUsuarioPolizaBBDD(session, true, "",
                    new BigDecimal(poliza.getEntidad().getCodigoInterno().substring(0,4)),
                    new BigDecimal(poliza.getEntidad().getCodigoInterno().substring(4)));
			
			if(usuario != null) {
				anexo.setUsuarioAlta(usuario.getCodusuario());
				anexo.setUsuarioDefinitiva(usuario.getCodusuario());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		anexo.setFechaBaja(null);
		anexo.setUsuarioBaja(null);
		anexo.setRevisarSbp(null);
		
		anexo.setFechaDefinitiva(new Date()); 
		
		if (poliza.getEntidad().getGastosArray() != null && poliza.getEntidad().getGastosArray().length > 0) {

			for (Gastos gastos : poliza.getEntidad().getGastosArray()) {
				// Si pertenece al G.N de Resto (1)
				if (Constants.GRUPO_NEGOCIO_VIDA.equals(gastos.getGrupoNegocio().charAt(0))) {
					anexo.setPctadministracionResto(gastos.getAdministracion());
					anexo.setPctadquisicionResto(gastos.getAdquisicion());
					anexo.setPctcomisionmediadorResto(gastos.getComisionMediador());
				}
				// Si pertenece al G.N de RyD (2)
				else if (Constants.GRUPO_NEGOCIO_RYD.equals(gastos.getGrupoNegocio().charAt(0))) {
					anexo.setPctadministracion(gastos.getAdministracion());
					anexo.setPctadquisicion(gastos.getAdquisicion());
					anexo.setPctcomisionmediador(gastos.getComisionMediador());
				}
			}
		}
	
		anexo.setFechaSeguimiento(null); 
		
		/* Pet. 70105 - Fase III (REQ.05) - MODIF TAM (02/03/2021) * Inicio */
		if (poliza.getPago() != null && poliza.getPago().getCuenta() != null) {
			if (anexo.getPoliza().getLinea().isLineaGanado()) {
				// Asegurado
				if ("A".equals(poliza.getPago().getCuenta().getDestinatario())) {
					anexo.setIbanAsegOriginal(poliza.getPago().getCuenta().getIban());
					anexo.setEsIbanAsegModificado(0);
				}
			} else {
				anexo.setIbanAsegOriginal(poliza.getPago().getCuenta().getIban());
				anexo.setEsIbanAsegModificado(0);
			}
		}

		if (poliza.getCuentaCobroSiniestros() != null) {
			anexo.setIban2AsegOriginal(poliza.getCuentaCobroSiniestros().getIban());
			anexo.setEsIban2AsegModificado(0);
		}
		
		if (null != poliza.getCobertura().getDatosVariables().getCarExpl()){
			anexo.setCodCaractExplotacion(new BigDecimal(poliza.getCobertura().getDatosVariables().getCarExpl().getValor()));
		}else {
			anexo.setCodCaractExplotacion(null);
		}
			
		if(poliza.getObjetosAsegurados() != null) {
			es.agroseguro.contratacion.ObjetosAsegurados objectosAsegurados = poliza.getObjetosAsegurados();
			
			HashSet<Parcela> parcelas = new HashSet<Parcela>();
			HashSet<ExplotacionAnexo> explotaciones = new HashSet<ExplotacionAnexo>();
			
			Node node = objectosAsegurados.getDomNode().getFirstChild();
			
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocumento = null;
					ExplotacionDocument explotacionDocumento = null;
					
					try {
						if(!isGanado) {
							parcelaDocumento = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);	
						}else {
							explotacionDocumento = ExplotacionDocument.Factory.parse(node);
						}
						
					} catch (XmlException e) {
						logger.error("Error al parsear.", e);
					}
					
					if(parcelaDocumento != null) {
						
						es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela par = parcelaDocumento.getParcela();
												
						Parcela parcela = new Parcela();
						parcela.setAnexoModificacion(anexo);
						parcela.setParcela(null);
						parcela.setIdcopyparcela(null);
						parcela.setHoja(new BigDecimal(par.getHoja()));
						parcela.setNumero(new BigDecimal(par.getNumero()));
						parcela.setNomparcela(par.getNombre());
						parcela.setCodprovincia(new BigDecimal(par.getUbicacion().getProvincia()));
						parcela.setCodcomarca(new BigDecimal(par.getUbicacion().getComarca()));
						parcela.setCodtermino(new BigDecimal(par.getUbicacion().getTermino()));
						if(!StringUtils.nullToString(par.getUbicacion().getSubtermino()).equals("")){
							parcela.setSubtermino(par.getUbicacion().getSubtermino().charAt(0));
						}else{
							parcela.setSubtermino(' ');
						}
						parcela.setCodcultivo(new BigDecimal(par.getCosecha().getCultivo()));
						parcela.setCodvariedad(new BigDecimal(par.getCosecha().getVariedad()));
						parcela.setPoligono(null);
						parcela.setParcela_1(null);
						parcela.setCodprovsigpac(new BigDecimal(par.getSIGPAC().getProvincia()));
						parcela.setCodtermsigpac(new BigDecimal(par.getSIGPAC().getTermino()));
						parcela.setAgrsigpac(new BigDecimal(par.getSIGPAC().getAgregado()));
						parcela.setZonasigpac(new BigDecimal(par.getSIGPAC().getZona()));
						parcela.setPoligonosigpac(new BigDecimal(par.getSIGPAC().getPoligono()));
						parcela.setParcelasigpac(new BigDecimal(par.getSIGPAC().getParcela()));
						parcela.setRecintosigpac(new BigDecimal(par.getSIGPAC().getRecinto()));
						parcela.setTipoparcela('P');
						
						parcela.setIdparcelaanxestructura(null);
						parcela.setAltaencomplementario('N');
						
						Character tipoModificacion = null; 

						List<com.rsi.agp.dao.tables.poliza.Parcela> parcelasPolizaOriginal = polizaDao.getlistParcelas(polizaOri.getIdpoliza());
						
						for(com.rsi.agp.dao.tables.poliza.Parcela parcelaOriginal : parcelasPolizaOriginal) {
								
							String hoja = StringUtils.nullToString(parcelaOriginal.getHoja());
							String numero = StringUtils.nullToString(parcelaOriginal.getNumero());
							
							boolean existe = false;
							
							if(hoja.equals(String.valueOf(par.getHoja())) && numero.equals(String.valueOf(par.getNumero()))) {
							{
								existe = true;
								boolean modificada = false;
								
								String nombreParcela = StringUtils.nullToString(par.getNombre());
								if(!nombreParcela.equals(StringUtils.nullToString(parcelaOriginal.getNomparcela()))){
									modificada = true;
								}else if(!new BigDecimal(par.getUbicacion().getProvincia()).equals(parcelaOriginal.getTermino().getProvincia().getCodprovincia())){
									modificada = true;
								}else if(!new BigDecimal(par.getUbicacion().getComarca()).equals(parcelaOriginal.getTermino().getComarca().getId().getCodcomarca())){
									modificada = true;
								}else if(!new BigDecimal(par.getUbicacion().getTermino()).equals(parcelaOriginal.getTermino().getId().getCodtermino())){
									modificada = true;
								}else if(!new Character(StringUtils.nullToString(par.getUbicacion().getSubtermino()).charAt(0)).equals(parcelaOriginal.getTermino().getId().getSubtermino())){
									modificada = true;
								}else if(!new BigDecimal(par.getCosecha().getCultivo()).equals(parcelaOriginal.getCodcultivo())){
									modificada = true;
								}else if(!new BigDecimal(par.getCosecha().getVariedad()).equals(parcelaOriginal.getCodvariedad())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getProvincia()).equals(parcelaOriginal.getCodprovsigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getTermino()).equals(parcelaOriginal.getCodtermsigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getAgregado()).equals(parcelaOriginal.getAgrsigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getZona()).equals(parcelaOriginal.getZonasigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getPoligono()).equals(parcelaOriginal.getPoligonosigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getParcela()).equals(parcelaOriginal.getParcelasigpac())){
									modificada = true;
								}else if(!new BigDecimal(par.getSIGPAC().getRecinto()).equals(parcelaOriginal.getRecintosigpac())){
									modificada = true;
								}
								
								if(modificada) {
									tipoModificacion = 'M';
								}
							}
							if(!existe) {
								tipoModificacion = 'A';
							}
						}
						parcela.setTipomodificacion(tipoModificacion); 
						
						CapitalAsegurado capital = new CapitalAsegurado();
						capital.setParcela(parcela);

						HashSet<CapitalAsegurado> capitalesAseguradosAnexo = new HashSet<CapitalAsegurado>();
						
						es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalesAsegurados = par.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(); 
										
						for(int i=0;i<capitalesAsegurados.length;i++) {
							es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado = capitalesAsegurados[i];
															
							TipoCapital tipoCapital = null;
							try {
								tipoCapital = (TipoCapital) tipoCapitalDao.get(TipoCapital.class,new BigDecimal(capitalAsegurado.getTipo()));
							} catch (DAOException e) {
								e.printStackTrace();
							}
							
							capital.setTipoCapital(tipoCapital);
							capital.setSuperficie(capitalAsegurado.getSuperficie());
							capital.setPrecio(capitalAsegurado.getPrecio());
							capital.setProduccion(new BigDecimal(capitalAsegurado.getProduccion()));
							capital.setAltaencomplementario('N');
							
							Character tipoModificacionCapital = null;
								
							List<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> capitalesPolizaOrigen;
							BigDecimal diferencia = null;
							try {
								capitalesPolizaOrigen = polizaDao.getListCapitalesAsegurados(polizaOri.getIdpoliza());
								
								boolean existeCapital = false;
								for(com.rsi.agp.dao.tables.poliza.CapitalAsegurado capitals : capitalesPolizaOrigen) {
									
									boolean modificada = false;
																		
									if(new BigDecimal(capitalAsegurado.getTipo()).equals(capitals.getTipoCapital().getCodtipocapital())) {
										
										existeCapital = true;
										
										if(!new BigDecimal(capitalAsegurado.getProduccion()).equals(capitals.getProduccion())) {
											modificada = true;
										}else if(capitalAsegurado.getPrecio().equals(capitals.getPrecio())) {
											modificada = true;
										}else if(capitalAsegurado.getSuperficie().equals(capitals.getSuperficie())) {
											modificada = true;
										}
										
										if(modificada) {
											tipoModificacionCapital = 'M';
											diferencia = new BigDecimal(capitalAsegurado.getProduccion()).subtract(capitals.getProduccion());
										}
									}											
								}
								if(!existeCapital) {
									tipoModificacionCapital = 'A';
								}
							} catch (DAOException e) {
								e.printStackTrace();
							}
							capital.setTipomodificacion(tipoModificacionCapital);
							capital.setIncrementoproduccion(diferencia);
							capital.setIncrementoproduccionanterior(new BigDecimal(0));
							capital.setValorincremento(null);
							capital.setTipoincremento(null);
							capital.setTipoRdto(null);
							
							DatosVariables capDatosVariables = capitalAsegurado.getDatosVariables();
							
							HashMap<Long,DatoVariableParcela> datosVariablesOrdenados = new HashMap<>();
							
							Set<CapitalDTSVariable> capitalesDts = getDatosVariablesParaAnexo(capDatosVariables,datosVariablesOrdenados,capital);
															
							capital.setCapitalDTSVariables(capitalesDts);
							
							capitalesAseguradosAnexo.add(capital);
						}						
						parcela.setCapitalAsegurados(capitalesAseguradosAnexo);
						parcelas.add(parcela);
						}
					} else {					
						if (explotacionDocumento != null) {
							ExplotacionAnexo explotacion = new ExplotacionAnexo();
							es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion exp = explotacionDocumento.getExplotacion();
							
							Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta = 
									diccionarioDatosDao.getCodConceptoEtiquetaTablaExplotaciones(polizaOri.getLinea().getLineaseguroid());
							
							explotacion.setAnexoModificacion(anexo);
							
							List<Explotacion> explotacionesPolizaOriginal = PolizaUtils.getExplotacionesPolizaFromPolizaActualizada(poliza, polizaOri.getIdpoliza(), dvCodConceptoEtiqueta);
							
							Character tipoModificacion = null;
							
							boolean existe = false;								
							for(Explotacion explotacionOriginal : explotacionesPolizaOriginal) {
								
								if(exp.getRega().equals(explotacionOriginal.getRega())) {
									existe = true;
									boolean modificada = false;
									
									if(!new BigDecimal(exp.getUbicacion().getProvincia()).equals(explotacionOriginal.getTermino().getProvincia().getCodprovincia())){
										modificada = true;
									}else if(!new BigDecimal(exp.getUbicacion().getComarca()).equals(explotacionOriginal.getTermino().getComarca().getId().getCodcomarca())){
										modificada = true;
									}else if(!new BigDecimal(exp.getUbicacion().getTermino()).equals(explotacionOriginal.getTermino().getId().getCodtermino())){
										modificada = true;
									}else if(!new Character(StringUtils.nullToString(exp.getUbicacion().getSubtermino()).charAt(0)).equals(explotacionOriginal.getTermino().getId().getSubtermino())){
										modificada = true;
									}else if(!new Integer(exp.getNumero()).equals(explotacionOriginal.getNumero())){
										modificada = true;
									}else if(!new Integer(exp.getSubexplotacion()).equals(explotacionOriginal.getSubexplotacion())){
										modificada = true;
									}else if(!new Integer(exp.getEspecie()).equals(explotacionOriginal.getEspecie())){
										modificada = true;
									}else if(!new Integer(exp.getSigla()).equals(explotacionOriginal.getSigla())){
										modificada = true;
									}else if(!new Integer(exp.getRegimen()).equals(explotacionOriginal.getRegimen())){
										modificada = true;
									}

									if(modificada) {
										tipoModificacion = 'M';
									}
								}
							}
							if(!existe) {
								tipoModificacion = 'A';
							}
							explotacion.setTipoModificacion(tipoModificacion);
							
							TerminoId terminoId = new TerminoId();
							terminoId.setCodprovincia(new BigDecimal(exp.getUbicacion().getProvincia()));
							terminoId.setCodcomarca(new BigDecimal(exp.getUbicacion().getComarca()));
							terminoId.setCodtermino(new BigDecimal(exp.getUbicacion().getTermino()));
							
							if(!StringUtils.nullToString(exp.getUbicacion().getSubtermino()).equals("")){
								terminoId.setSubtermino(exp.getUbicacion().getSubtermino().charAt(0));
							}else{
								terminoId.setSubtermino(' ');
							}
							
							Termino termino = null;
							try {
								termino = (Termino) terminoDao.get(Termino.class,terminoId);
							} catch (DAOException e) {
								e.printStackTrace();
							}
							
							explotacion.setTermino(termino);
														
							explotacion.setTipoModificacion(tipoModificacion);
							explotacion.setLatitud(null);
							explotacion.setLongitud(null);
							explotacion.setNumero(exp.getNumero());
							explotacion.setRega(exp.getRega());
							explotacion.setSigla(exp.getSigla());
							explotacion.setSubexplotacion(exp.getSubexplotacion());
							explotacion.setEspecie(new Long(exp.getEspecie()));
							explotacion.setNomespecie(null);
							explotacion.setRegimen(new Long(exp.getRegimen()));
							explotacion.setNomregimen(null);
																			
							HashSet<GrupoRazaAnexo> gruposRaza = new HashSet<GrupoRazaAnexo>();
							HashSet<ExplotacionCoberturaAnexo> explotacionCoberturasAnexo = new HashSet<ExplotacionCoberturaAnexo>();
													
							for(int i=0;i<exp.getGrupoRazaArray().length;i++) 
							{
								 es.agroseguro.contratacion.explotacion.GrupoRaza expGrupoRaza = exp.getGrupoRazaArray()[i];
								 es.agroseguro.contratacion.explotacion.CapitalAsegurado[] capitalAseguradoRaza = expGrupoRaza.getCapitalAseguradoArray();
								 							 
								 for(int j=0;j<capitalAseguradoRaza.length;j++) {
									 
									 es.agroseguro.contratacion.explotacion.CapitalAsegurado capital = capitalAseguradoRaza[i];
									 Animales[] animalesGrupoRaza = capital.getAnimalesArray();
									 
									 for(int k=0;k<animalesGrupoRaza.length;k++) {
										 //GrupoRazaAnexo
										
										GrupoRazaAnexo grupoRaza = new GrupoRazaAnexo();
										grupoRaza.setExplotacionAnexo(explotacion);
										grupoRaza.setCodgruporaza(new Long(expGrupoRaza.getGrupoRaza()));
										grupoRaza.setNomgruporaza(null);
										grupoRaza.setCodtipoanimal(new Long(capitalAseguradoRaza[k].getTipo()));
										grupoRaza.setNomtipocapital(null);
										grupoRaza.setNomtipoanimal(null);
										grupoRaza.setNumanimales(new Long(animalesGrupoRaza[k].getNumero()));
										
										HashSet<PrecioAnimalesModuloAnexo> preciosAnimales = new HashSet<>();
										
										//PrecioAnimalesModuloAnexo
										PrecioAnimalesModuloAnexo precioAnimales = new PrecioAnimalesModuloAnexo();
										precioAnimales.setGrupoRazaAnexo(grupoRaza);
										precioAnimales.setCodmodulo(poliza.getCobertura().getModulo());
										precioAnimales.setPrecio(animalesGrupoRaza[k].getPrecio());
										precioAnimales.setPrecioMin(null);
										precioAnimales.setPrecioMax(null);
										
										preciosAnimales.add(precioAnimales);
										
										grupoRaza.setPrecioAnimalesModuloAnexos(preciosAnimales);
										
										DatosVariables datVarCapitalAsegurado = capital.getDatosVariables();
										//Set<DatosVarExplotacionAnexo> datosVarExplotacionAnexos = getDatVarCapitalAsegurado(datVarCapitalAsegurado ,grupoRaza);
																				
										Set<DatosVarExplotacionAnexo> datosVarExplotacionAnexos = addDatosVariables(datVarCapitalAsegurado,dvCodConceptoEtiqueta,grupoRaza);
																			
										grupoRaza.setDatosVarExplotacionAnexos(datosVarExplotacionAnexos);
																			
										gruposRaza.add(grupoRaza);
									 }
								 }							
							}
							explotacionCoberturasAnexo = (HashSet<ExplotacionCoberturaAnexo>) getExplotacionesCoberturas(exp,explotacion);
							explotacion.setExplotacionCoberturasAnexo(explotacionCoberturasAnexo);
							explotacion.setGrupoRazaAnexos(gruposRaza);
						}
					}
				}
				node = node.getNextSibling();
			}
			anexo.setParcelas(parcelas);
			anexo.setExplotacionAnexos(explotaciones);
		}
		
		if(poliza.getSubvencionesDeclaradas() != null && poliza.getSubvencionesDeclaradas().getSubvencionDeclaradaArray() != null) {
			
			SubvencionDeclarada[] subvenciones = poliza.getSubvencionesDeclaradas().getSubvencionDeclaradaArray();
			HashSet<SubvDeclarada> subvDeclaradas = new HashSet<SubvDeclarada>();
			
			for(int i=0;i<subvenciones.length;i++) {
				SubvDeclarada subvencion =  new SubvDeclarada();
				subvencion.setAnexoModificacion(anexo);
				subvencion.setCodsubvencion(null);
				subvencion.setTipomodificacion(null);
				
				subvDeclaradas.add(subvencion);
			}
			anexo.setSubvDeclaradas(subvDeclaradas);
		}
		
		if(poliza.getCobertura() != null && poliza.getCobertura().getDatosVariables() != null) {
			
			DatosVariables datosVariables = poliza.getCobertura().getDatosVariables();
			
			HashSet<Cobertura> coberturas = (HashSet<Cobertura>) DeclaracionesModificacionPolizaManager.getCoberturasFromPolizaActualizada(datosVariables, anexo);
			
			anexo.setCoberturas(coberturas);
	
		}
		
		Set<AnexoModDistribucionCostes> anexoDistribucionesCostes  = new HashSet<AnexoModDistribucionCostes>(); 
		
		if(poliza.getCostePoliza() != null && poliza.getCostePoliza().getCosteGrupoNegocioArray() != null){
			
			CosteGrupoNegocio[] costesGrupoNegocio = poliza.getCostePoliza().getCosteGrupoNegocioArray();
			
			for(int i=0;i<costesGrupoNegocio.length;i++) {
				
				CosteGrupoNegocio costeGrupo = costesGrupoNegocio[i];
				
				//(tras la creación en BBDD del AnexoModificacion, guardar un registro por cada //Poliza/CostePoliza/CosteGrupoNegocio)
				AnexoModDistribucionCostes anex_distCostes = new AnexoModDistribucionCostes();
				
				//AnexoModDistribucionCostesId
				AnexoModDistribucionCostesId anex_distCostes_id = new AnexoModDistribucionCostesId();
				anex_distCostes_id.setIdanexo(anexo.getId());
				
				Integer tipoDc = 0;
				anex_distCostes_id.setTipoDc(tipoDc);
						
				anex_distCostes_id.setGrupoNegocio(costeGrupo.getGrupoNegocio().charAt(0));
				
				anex_distCostes.setId(anex_distCostes_id);
				anex_distCostes.setPrimaComercial(costeGrupo.getPrimaComercial());
				anex_distCostes.setPrimaComercialNeta(costeGrupo.getPrimaComercialNeta());
				anex_distCostes.setRecargoConsorcio(costeGrupo.getRecargoConsorcio());
				anex_distCostes.setReciboPrima(costeGrupo.getReciboPrima());
				anex_distCostes.setCosteTomador(costeGrupo.getCosteTomador());
				anex_distCostes.setTotalCosteTomador(costeGrupo.getCosteTomador());
				
				if(poliza.getCostePoliza().getFinanciacion() != null) {
					anex_distCostes.setRecargoAval(poliza.getCostePoliza().getFinanciacion().getRecargoAval());
					anex_distCostes.setRecargoFraccionamiento(poliza.getCostePoliza().getFinanciacion().getRecargoFraccionamiento());
				}
				
				HashSet<AnexoModBonifRecargos> bonifRecargos = new HashSet<AnexoModBonifRecargos>();
				
				for(int j=0;j<costeGrupo.getBonificacionRecargoArray().length;j++) {
					AnexoModBonifRecargos recargo = new AnexoModBonifRecargos();
					
					//AnexoModBonifRecargosId
					AnexoModBonifRecargosId recargoId = new AnexoModBonifRecargosId();
					recargoId.setIdanexo(anexo.getId());
					recargoId.setTipoDc(tipoDc);
					recargoId.setGrupoNegocio(costeGrupo.getGrupoNegocio().charAt(0));
					
					recargo.setId(recargoId);
					recargo.setAnexoModDistribucionCostes(recargo.getAnexoModDistribucionCostes());
					recargo.setImporte(recargo.getImporte());
					recargo.setDescripcion(null);
					
					bonifRecargos.add(recargo);
				}
				anex_distCostes.setAnexoModBonifRecargoses(bonifRecargos);
				
				//SubvencionEnesa
				if(costeGrupo.getSubvencionEnesaArray() != null) {
					SubvencionEnesa[] subvencionesEnesa = costeGrupo.getSubvencionEnesaArray();
					
					HashSet<AnexoModSubvEnesa> anexoModSubvEnesas = new HashSet<AnexoModSubvEnesa>();
					
					for(int j=0;j<subvencionesEnesa.length;j++) {
						
						AnexoModSubvEnesa anexSubEnesa = new AnexoModSubvEnesa();
						SubvencionEnesa subvencion = subvencionesEnesa[i];
						 
						AnexoModSubvEnesaId subEnesaId = new AnexoModSubvEnesaId();
						subEnesaId.setIdanexo(anexo.getId());
						subEnesaId.setTipoDc(tipoDc);
						subEnesaId.setTipoSubvencion(subvencion.getTipo());
						subEnesaId.setGrupoNegocio(costeGrupo.getGrupoNegocio().charAt(0));
						
						anexSubEnesa.setId(subEnesaId);
						anexSubEnesa.setAnexoModDistribucionCostes(anex_distCostes);
						anexSubEnesa.setImporte(subvencion.getImporte());
						anexSubEnesa.setDescripcion(null);
						
						anexoModSubvEnesas.add(anexSubEnesa);
					}
					
					anex_distCostes.setAnexoModSubvEnesas(anexoModSubvEnesas);
				}
				
				//AnexoModSubvCCAA
				if(costeGrupo.getSubvencionCCAAArray() != null) {
				
					SubvencionCCAA[] subvencionesCCAAA = costeGrupo.getSubvencionCCAAArray();
					HashSet<AnexoModSubvCCAA> anexoModSubvCCAAs = new HashSet<AnexoModSubvCCAA>();
					
					for(int j=0;j<subvencionesCCAAA.length;j++) {
						
						SubvencionCCAA subvencion = subvencionesCCAAA[i];
						AnexoModSubvCCAA subvencionCCAA = new AnexoModSubvCCAA();
						
						AnexoModSubvCCAAId subvCCAAId = new AnexoModSubvCCAAId();
						subvCCAAId.setIdanexo(anexo.getId());
						subvCCAAId.setCodOrganismo(subvencion.getCodigoOrganismo().charAt(0));
						subvCCAAId.setGrupoNegocio(costeGrupo.getGrupoNegocio().charAt(0));
						
						subvencionCCAA.setId(subvCCAAId);
						subvencionCCAA.setAnexoModDistribucionCostes(anex_distCostes);
						subvencionCCAA.setImporte(subvencion.getImporte());
						subvencionCCAA.setDescripcion(null);
												
						anexoModSubvCCAAs.add(subvencionCCAA);
					}
					
					anex_distCostes.setAnexoModSubvCCAAs(anexoModSubvCCAAs);
				}
				anexoDistribucionesCostes.add(anex_distCostes);
			}
		}	
		
		anexo.setAnexoModDistribucionCosteses(anexoDistribucionesCostes);
		
		//String asunto: después de poblar TODOS los demás campos del objeto, el valor de este campo lo dará el método XmlTransformerUtil.generarAsuntoAnexo(anexoModificacion, false)
		String asunto = XmlTransformerUtil.generarAsuntoAnexo(anexo, false);
		anexo.setAsunto(asunto);
				
		anexo = saveAnexo(anexo);
		
		return anexo;
	}
	
	/**
	 * Obtiene la Reduccion de capital desde el XML
	 * 
	 * @param idCupon
	 * @param polizaXml
	 * @param estado_cupon
	 * @return
	 * @throws DAOException 
	 */
	public ReduccionCapital getRedCapFromXml(String idCupon, PolizaReduccionCapital datosRC, XmlObject polizaXml,
			XmlObject polizaRcXML, Long estado_cupon) throws DAOException {
		ReduccionCapital redCap = new ReduccionCapital();
		es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) polizaXml).getPoliza();
		Poliza polizaOri = null;
		EstadosInc estadoAgroseguro = null;
		com.rsi.agp.dao.tables.reduccionCap.Estado estado = null;
		CuponRC cupon = null;
		EstadoCuponRC estadoCupon = null;
		Date d;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			// Poliza
			polizaOri = polizaDao.getPolizaByRefPlanLin(datosRC.getReferencia(), 'P', new BigDecimal(datosRC.getPlan()),
					new BigDecimal(poliza.getLinea()));
			redCap.setPoliza(polizaOri);

		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		// Estado (3-Enviado Correcto)
		try {
			estado = (com.rsi.agp.dao.tables.reduccionCap.Estado) reduccionCapitalDao
					.get(com.rsi.agp.dao.tables.reduccionCap.Estado.class, new Short("3"));
		} catch (DAOException e) {
			e.printStackTrace();
		}
			
		redCap.setEstado(estado);

		// Estado AGROSEGURO (A-Aceptada)
		try {
			estadoAgroseguro = (EstadosInc) reduccionCapitalDao.get(EstadosInc.class, 'A');		
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		redCap.setEstadoAgroseguro(estadoAgroseguro);

		// Cupon (6-Confirmado-Trámite)
		try {
			estadoCupon = (EstadoCuponRC) cuponDao.get(EstadoCuponRC.class, estado_cupon);

			cupon = new CuponRC();
			cupon.setEstadoCupon(estadoCupon);
			cupon.setReferencia(polizaOri.getReferencia());
			cupon.setIdcupon(idCupon); // el recibido como entrada del SW
			cupon.setFecha(new Date());

			cuponRCDao.saveCupon(cupon);
			redCap.setCupon(cupon);
		} catch (DAOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		redCap.setComunicaciones(null);
		redCap.setIdCopy(null);
		redCap.setIdenvio(null);
		redCap.setCodmotivoriesgo(datosRC.getMotivo().getCausaDanio());
		redCap.setXml(Hibernate.createClob(polizaRcXML.toString()));
		redCap.setNumAnexo(null);
		redCap.setCodModulo(datosRC.getModulo());
		// Fechas
		redCap.setFechaenvio(new Date());
		redCap.setFechaEnvioAnexo(new Date());
		redCap.setFechafirma(new Date());
		redCap.setFechaAlta(new Date());
		redCap.setFechaDefinitiva(new Date());
		
		// Fecha ocurrencia
		try {
			d = new Date();
			d = sdf.parse(datosRC.getMotivo().getFechaOcurrencia().toString());
			redCap.setFechadanios(d);
		} catch (ParseException e) {
			logger.error("Error al parsear la fecha de ocurrencia", e);
		}
		
		// Guardar anexo
		redCap = reduccionCapitalDao.saveReduccionCapital(redCap);
		
		// Parcelas
		if(poliza.getObjetosAsegurados() != null) {
			// Recuperamos las parcelas de la poliza de AgroPlus
			List<com.rsi.agp.dao.tables.poliza.Parcela> parcelasPolizaOriginal = polizaDao
					.getlistParcelas(polizaOri.getIdpoliza());
			
			Set<com.rsi.agp.dao.tables.reduccionCap.Parcela> parcelas = new HashSet<com.rsi.agp.dao.tables.reduccionCap.Parcela>();		
			// Recuperamos las parcelas de la consulta de la contratacion
			es.agroseguro.contratacion.ObjetosAsegurados objAsegPoliza = poliza.getObjetosAsegurados();
			// Recuperamos las parcelas de la reduccion de capital enviadas
			ObjetosAsegurados objAsegRC = datosRC.getObjetosAsegurados();
			
			Node node = objAsegPoliza.getDomNode().getFirstChild();
				
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocumento = null;
					
					try {
						parcelaDocumento = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
					} catch (XmlException e) {
						logger.error("Error al parsear.", e);
					}
					
					if(parcelaDocumento != null) {
						es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parPol = parcelaDocumento.getParcela();
						com.rsi.agp.dao.tables.reduccionCap.Parcela parcelaRC = new com.rsi.agp.dao.tables.reduccionCap.Parcela();
						
						//fix local entidades externas no lista parcelas en tabla
						Character altaAnexoVal = new Character('N');
						parcelaRC.setAltaenanexo(altaAnexoVal);
						//fix local entidades externas no lista parcelas en tabla
						
						parcelaRC.setReduccionCapital(redCap);
						parcelaRC.setIdparcelacopy(null);
						parcelaRC.setHoja(new BigDecimal(parPol.getHoja()));
						parcelaRC.setNumero(new BigDecimal(parPol.getNumero()));
						parcelaRC.setNomparcela(parPol.getNombre());
						parcelaRC.setCodcultivo(new BigDecimal(parPol.getCosecha().getCultivo()));
						parcelaRC.setCodvariedad(new BigDecimal(parPol.getCosecha().getVariedad()));
						parcelaRC.setCodprovincia(new BigDecimal(parPol.getUbicacion().getProvincia()));
						parcelaRC.setCodcomarca(new BigDecimal(parPol.getUbicacion().getComarca()));
						parcelaRC.setCodtermino(new BigDecimal(parPol.getUbicacion().getTermino()));
						if (!StringUtils.nullToString(parPol.getUbicacion().getSubtermino()).equals("")) {
							parcelaRC.setSubtermino(parPol.getUbicacion().getSubtermino().charAt(0));
						} else {
							parcelaRC.setSubtermino(' ');
						}		
						parcelaRC.setPoligono(null);
						parcelaRC.setParcela_1(null);
						parcelaRC.setCodprovsigpac(new BigDecimal(parPol.getSIGPAC().getProvincia()));
						parcelaRC.setCodtermsigpac(new BigDecimal(parPol.getSIGPAC().getTermino()));
						parcelaRC.setAgrsigpac(new BigDecimal(parPol.getSIGPAC().getAgregado()));
						parcelaRC.setZonasigpac(new BigDecimal(parPol.getSIGPAC().getZona()));
						parcelaRC.setPoligonosigpac(new BigDecimal(parPol.getSIGPAC().getPoligono()));
						parcelaRC.setParcelasigpac(new BigDecimal(parPol.getSIGPAC().getParcela()));
						parcelaRC.setRecintosigpac(new BigDecimal(parPol.getSIGPAC().getRecinto()));
						
						// Recorremos las parcelas de la poliza para asociar el idParcela de la poliza
						for(com.rsi.agp.dao.tables.poliza.Parcela parOriginal : parcelasPolizaOriginal) {
							if (parOriginal.getHoja() == parPol.getHoja()
									&& parOriginal.getNumero() == parPol.getNumero()) {
								parcelaRC.setParcela(parOriginal);
								break;
							}
						}					
						
						// Capitales Asegurados
						HashSet<com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado> capAseguradosRC = new HashSet<com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado>();
						com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado capital = new com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado();
						// Obtenemos los capitales de las parcelas de la consulta de la contratacion
						es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalesAsegurados = parPol.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(); 
						
						// Asociamos la parcelas
						capital.setParcela(parcelaRC);
						
						// Recorremos los capitales devueltos de la consulta de la contrtacion
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAsegCc : capitalesAsegurados) {
							// Obtenemos el Tipo de Capital								
							TipoCapital tipoCapital = null;
							try {
								tipoCapital = (TipoCapital) tipoCapitalDao.get(TipoCapital.class,
										new BigDecimal(capAsegCc.getTipo()));
							} catch (DAOException e) {
								e.printStackTrace();
							}
							
							capital.setCodtipocapital(tipoCapital.getCodtipocapital().shortValueExact());
							capital.setSuperficie(capAsegCc.getSuperficie());
							capital.setAltaenanexo(null);
							capital.setPrecio(capAsegCc.getPrecio());
							capital.setProd(new BigDecimal(capAsegCc.getProduccion()));
							capital.setPrecio(capAsegCc.getPrecio());
							
							// Como no tenemos la PolizaReducciones devuelta por el servicio de Consulta de
							// la Contratacion, sacamos la nueva produccion de el xml que nos mandan las
							// terceras entidades
							// Recorremos las parcelas de la reduccion de capital
							outerLoop:
							for (ParcelaReducida parRedCap : objAsegRC.getParcela()) {
								if (parRedCap.getHoja() == parPol.getHoja()
										&& parRedCap.getNumero() == parPol.getNumero()) {
									for (com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado capAsegRedCap : parRedCap
											.getCapitalesAsegurados().getCapitalAsegurado()) {
										if (capAsegRedCap.getTipo() == capAsegCc.getTipo()) {
											// Asignamos la produccion tras daños
											capital.setProdred(
													BigDecimal.valueOf(capAsegRedCap.getProduccionTrasReduccion()));
											break outerLoop;
										}
									}
								}
							}
							
							capAseguradosRC.add(capital);
						}						
						parcelaRC.setCapitalAsegurados(capAseguradosRC);
						parcelas.add(parcelaRC);
					} 
				}
				node = node.getNextSibling();
			}
			
			// Guardamos las parcelas
			reduccionCapitalDao.saveParcelas(parcelas);			
			redCap.setParcelas(parcelas);
		}
		
		// Distribucion de costes
		CostePoliza costePoliza = poliza.getCostePoliza();
		
		if (costePoliza != null && costePoliza.getCosteGrupoNegocioArray() != null && costePoliza.getCosteGrupoNegocioArray().length > 0) {
			Set<RedCapitalDistribucionCostes> distCostesRC = new HashSet<RedCapitalDistribucionCostes>(
					costePoliza.getCosteGrupoNegocioArray().length);
			RedCapitalDistribucionCostes distCosteRC = null;
			RedCapitalDistribucionCostesId distCosteRC_id = null;
			
			Character grupoNegocio;
			Integer tipoDc = 0;
			
			for (CosteGrupoNegocio costeGrupo : costePoliza.getCosteGrupoNegocioArray()) {
				grupoNegocio = costeGrupo.getGrupoNegocio().charAt(0);

				// Tras la creacion en BBDD de la Reduccion de Capital, guardar un registro por
				// cada Poliza/CostePoliza/CosteGrupoNegocio
				distCosteRC = new RedCapitalDistribucionCostes();

				// Id: RedCapitalDistribucionCostesId
				distCosteRC_id = new RedCapitalDistribucionCostesId();
				distCosteRC_id.setIdanexo(redCap.getId());
				distCosteRC_id.setTipoDc(tipoDc);
				distCosteRC_id.setGrupoNegocio(grupoNegocio);

				distCosteRC.setId(distCosteRC_id);
				distCosteRC.setPrimaComercial(costeGrupo.getPrimaComercial());
				distCosteRC.setPrimaComercialNeta(costeGrupo.getPrimaComercialNeta());
				distCosteRC.setRecargoConsorcio(costeGrupo.getRecargoConsorcio());
				distCosteRC.setReciboPrima(costeGrupo.getReciboPrima());
				distCosteRC.setCosteTomador(costeGrupo.getCosteTomador());
				distCosteRC.setTotalCosteTomador(poliza.getCostePoliza().getTotalCosteTomador());

				if (costePoliza.getFinanciacion() != null) {
					distCosteRC.setRecargoAval(costePoliza.getFinanciacion().getRecargoAval());
					distCosteRC.setRecargoFraccionamiento(costePoliza.getFinanciacion().getRecargoFraccionamiento());
				}

				// Bonificaciones/Recargos				
				if (costeGrupo.getBonificacionRecargoArray() != null
						&& costeGrupo.getBonificacionRecargoArray().length > 0) {
					Set<RedCapitalBonifRecargos> bonifRecargosRC = new HashSet<RedCapitalBonifRecargos>(0);
					RedCapitalBonifRecargos bonifRecRC = null;
					
					for (BonificacionRecargo bonifRec : costeGrupo.getBonificacionRecargoArray()) {
						bonifRecRC = new RedCapitalBonifRecargos(
								new RedCapitalBonifRecargosId(distCosteRC_id, bonifRec.getCodigo(), grupoNegocio),
								distCosteRC, bonifRec.getImporte());

						bonifRecargosRC.add(bonifRecRC);
					}

					distCosteRC.setRedCapitalBonifRecargos(bonifRecargosRC);
				}
				
				// Subvenciones Enesa
				if (costeGrupo.getSubvencionEnesaArray() != null && costeGrupo.getSubvencionEnesaArray().length > 0) {
					Set<RedCapitalSubvEnesa> redCapitalSubvEnesas = new HashSet<RedCapitalSubvEnesa>(
							costeGrupo.getSubvencionEnesaArray().length);
					RedCapitalSubvEnesa subvEnesaRC = new RedCapitalSubvEnesa();

					for (SubvencionEnesa subvEnesa : costeGrupo.getSubvencionEnesaArray()) {
						subvEnesaRC = new RedCapitalSubvEnesa(
								new RedCapitalSubvEnesaId(distCosteRC_id, subvEnesa.getTipo(), grupoNegocio),
								distCosteRC, subvEnesa.getImporte());
						redCapitalSubvEnesas.add(subvEnesaRC);
					}

					distCosteRC.setRedCapitalSubvEnesas(redCapitalSubvEnesas);
				}
				
				// Subvenciones CCAA
				if (costeGrupo.getSubvencionCCAAArray() != null && costeGrupo.getSubvencionCCAAArray().length > 0) {
					Set<RedCapitalSubvCCAA> redCapitalSubvCCAAs = new HashSet<RedCapitalSubvCCAA>(
							costeGrupo.getSubvencionCCAAArray().length);
					RedCapitalSubvCCAA subvCcaaRC = new RedCapitalSubvCCAA();
					
					for (SubvencionCCAA subvCCAA : costeGrupo.getSubvencionCCAAArray()) {
						subvCcaaRC = new RedCapitalSubvCCAA(new RedCapitalSubvCCAAId(distCosteRC_id,
								subvCCAA.getCodigoOrganismo().charAt(0), grupoNegocio), distCosteRC,
								subvCCAA.getImporte());
						redCapitalSubvCCAAs.add(subvCcaaRC);
					}
					
					distCosteRC.setRedCapitalSubvCCAAs(redCapitalSubvCCAAs);
				}				
				
				distCostesRC.add(distCosteRC);
			}
			
			// Guardamos la Distribución de Costes
			reduccionCapitalDao.saveDistCostes(distCostesRC);
			redCap.setRedCapitalDistribucionCostes(distCostesRC);
		}
		
		return redCap;
	}
	
	private Set<ExplotacionCoberturaAnexo> getExplotacionesCoberturas(
			es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion exp, ExplotacionAnexo explotacionAnexo) {
		
		es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido riesgos[] = exp.getDatosVariables().getRiesgCbtoElegArray();
		
		Set<ExplotacionCoberturaAnexo> coberturas =null;
		if (null!=riesgos && riesgos.length>0){
			coberturas = new HashSet<ExplotacionCoberturaAnexo>();
			for (int i = 0; i < riesgos.length; i++) {
				ExplotacionCoberturaAnexo cob = new ExplotacionCoberturaAnexo();		
				
				cob.setExplotacionAnexo(explotacionAnexo);
				cob.setCodmodulo(explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo());
				
				short fila = 0;
				try {
					fila = cargaExplotacionesDao.getFilaExplotacionCobertura(
							explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo(), riesgos[i].getCPMod(), riesgos[i].getCodRCub());
				} catch (DAOException e) {
					e.printStackTrace();
				}
				cob.setFila(fila);
				
				cob.setCpm((short) riesgos[i].getCPMod());
				
				String cpmDescripcion = null;
				try {
					cpmDescripcion = cargaExplotacionesDao.getDescripcionConceptoPpalMod(riesgos[i].getCPMod());
				} catch (DAOException e) {
					e.printStackTrace();
				}
				cob.setCpmDescripcion(cpmDescripcion);
				
				cob.setRiesgoCubierto((short) riesgos[i].getCodRCub());
				
				String rcDescripcion = null;
				try {
					rcDescripcion = cargaExplotacionesDao.getDescripcionRiesgoCubierto(explotacionAnexo.getAnexoModificacion().getPoliza().getLinea().getLineaseguroid(), 
							explotacionAnexo.getAnexoModificacion().getPoliza().getCodmodulo(), riesgos[i].getCodRCub());
				} catch (DAOException e) {
					e.printStackTrace();
				}
				cob.setRcDescripcion(rcDescripcion);
				
				cob.setElegida(riesgos[i].getValor().toCharArray()[0]);
				cob.setElegible(new Character('S'));
				cob.setExplotacionCoberturaVincAnexos(null);
				cob.setVinculada(null);
				cob.setTipoCobertura(null);
				cob.setDvCodConcepto(null);
				cob.setDvDescripcion(null);
				cob.setDvValor(null);
				cob.setDvValorDescripcion(null);
				cob.setDvElegido(null);
				cob.setDvColumna(null);
				
				coberturas.add(cob);
			}
		}
		return coberturas;
	}

	@SuppressWarnings("unlikely-arg-type")
	private static Set<CapitalDTSVariable> getDatosVariablesParaAnexo(
			final es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, HashMap<Long,DatoVariableParcela> datosVariablesOriginal, final CapitalAsegurado capitalAsegurado) {
		
		Set<CapitalDTSVariable>  CapitalesDTSVariable = new HashSet<CapitalDTSVariable>();
		if (datosVariables != null) {
			
			// CALCULO INDEMNIZACION
			if (null != datosVariables.getCalcIndemArray() && datosVariables.getCalcIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion calcIndem : datosVariables
						.getCalcIndemArray()) {
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION));
					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(calcIndem.getValor() + "");
					
					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(calcIndem.getCodRCub()).equals(calcIndem.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);
					
					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// GARANTIZADO
			if (null != datosVariables.getGarantArray() && datosVariables.getGarantArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.Garantizado garant : datosVariables.getGarantArray()) {
					
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));

					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(garant.getValor() + "");

					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(garant.getCodRCub()).equals(garant.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);

					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// % FRANQUICIA
			if (null != datosVariables.getFranqArray() && datosVariables.getFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia franq : datosVariables
						.getFranqArray()) {

					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));

					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(franq.getValor() + "");

					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(franq.getCodRCub()).equals(franq.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);

					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// % MINIMO INDEMNIZABLE
			if (null != datosVariables.getMinIndemArray() && datosVariables.getMinIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : datosVariables
						.getMinIndemArray()) {
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(minIndem.getValor() + "");

					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(minIndem.getCodRCub()).equals(minIndem.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);
					
					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// RIESGO CUBIERTO ELEGIDO
			if (null != datosVariables.getRiesgCbtoElegArray() && datosVariables.getRiesgCbtoElegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCub : datosVariables
						.getRiesgCbtoElegArray()) {
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					
					if( "S".equals(rCub.getValor()) || "-1".equals(rCub.getValor())) { 
						capitalDTSVariable.setValor(Constants.RIESGO_ELEGIDO_SI);
					} else { 
						capitalDTSVariable.setValor(Constants.RIESGO_ELEGIDO_NO);
					}
					
					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(rCub.getCodRCub()).equals(rCub.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);

					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// TIPO FRANQUICIA
			if (null != datosVariables.getTipFranqArray() && datosVariables.getTipFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tFranq : datosVariables
						.getTipFranqArray()) {
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA));
					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(tFranq.getValor() + "");

					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(tFranq.getCodRCub()).equals(tFranq.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);
					

					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}

			// % CAPITAL ASEGURADO
			if (null != datosVariables.getCapAsegArray() && datosVariables.getCapAsegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capAseg : datosVariables
						.getCapAsegArray()) {
					CapitalDTSVariable capitalDTSVariable = new CapitalDTSVariable();

					capitalDTSVariable.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO));
					capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
					capitalDTSVariable.setValor(capAseg.getValor() + "");

					Character tipoModificacion = null;
					if(!datosVariablesOriginal.containsValue(capitalAsegurado)) {
						if(datosVariablesOriginal.get(capAseg.getCodRCub()).equals(capAseg.getCodRCub())){
							tipoModificacion = 'M';
						}else {
							tipoModificacion = 'A';
						}
					}
					capitalDTSVariable.setTipomodificacion(tipoModificacion);
					
					CapitalesDTSVariable.add(capitalDTSVariable);
				}
			}
		}
		return CapitalesDTSVariable;
	}
	
	private static Set<DatosVarExplotacionAnexo> addDatosVariables(
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables,
			final Map<BigDecimal, RelacionEtiquetaTabla> dvCodConceptoEtiqueta,
			final GrupoRazaAnexo grupoRaza){
		
		HashSet<DatosVarExplotacionAnexo> datosVarExplotacionesAnexo = new HashSet<>();
		
		if (datosVariables != null && dvCodConceptoEtiqueta != null	&& !dvCodConceptoEtiqueta.isEmpty()) {
			// 1. Recorrer las claves de auxEtiquetaTabla
			for (BigDecimal codconcepto : dvCodConceptoEtiqueta.keySet()) {
				try {
					// 2. Buscar en los datos variables del capital asegurado el
					// valor correspondiente
					// primero obtengo el objeto que representa al dato variable
					Class<?> clase = es.agroseguro.contratacion.datosVariables.DatosVariables.class;
					Method method = clase.getMethod("get"
							+ dvCodConceptoEtiqueta.get(codconcepto).getEtiqueta());
					
					Object objeto = method.invoke(datosVariables);
					
					if (objeto != null) {
						// despuï¿½s obtengo el valor que tiene el objeto en el
						// dato variable.
						Class<?> claseValor = objeto.getClass();
						Method methodValor = claseValor.getMethod("getValor");
						Object valor = methodValor.invoke(objeto);
						// 3. asigno el valor al dato variable
						if (!StringUtils.nullToString(valor).equals("")) {
							DatosVarExplotacionAnexo datoVariable = new DatosVarExplotacionAnexo();
							datoVariable.setCodconcepto(codconcepto.intValue());
							if (valor instanceof XmlCalendar) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
								Date d = new Date();
								String fecha = "";
								try {
									d = sdf.parse(valor.toString());
									fecha = sdf2.format(d);
								} catch (ParseException e) {
									logger.error("Error al parsear la fecha en los datos variables", e);
								}
								datoVariable.setValor(fecha);
							} else {
								datoVariable.setValor(StringUtils.nullToString(valor));
							}
							if (grupoRaza != null) {
								datoVariable.setGrupoRazaAnexo(grupoRaza);
							}
							if (datosVarExplotacionesAnexo != null) {
								datosVarExplotacionesAnexo.add(datoVariable);
							}
						}
					}
				} catch (SecurityException e) {
					logger.debug("Error de seguridad " + e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.debug("El mï¿½todo no existe para esta clase "
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.debug("El mï¿½todo acepta los argumentos "
							+ e.getMessage());
				} catch (IllegalAccessException e) {
					logger.debug("Error " + e.getMessage());
				} catch (InvocationTargetException e) {
					logger.debug("Error " + e.getMessage());
				}
			}
		}
		return datosVarExplotacionesAnexo;
		
	}
	
	public ITipoCapitalDao getTipoCapitalDao() {
		return tipoCapitalDao;
	}

	public void setTipoCapitalDao(ITipoCapitalDao tipoCapitalDao) {
		this.tipoCapitalDao = tipoCapitalDao;
	}

	public ITerminoDao getTerminoDao() {
		return terminoDao;
	}

	public void setTerminoDao(ITerminoDao terminoDao) {
		this.terminoDao = terminoDao;
	}

	public ICargaExplotacionesDao getCargaExplotacionesDao() {
		return cargaExplotacionesDao;
	}

	public void setCargaExplotacionesDao(ICargaExplotacionesDao cargaExplotacionesDao) {
		this.cargaExplotacionesDao = cargaExplotacionesDao;
	}

	public IDiccionarioDatosDao getDiccionarioDatosDao() {
		return diccionarioDatosDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public IPolizasPctComisionesDao getPolizasPctComisionesDao() {
		return polizasPctComisionesDao;
	}

	public IImportacionPolizasExtDao getImportacionPolizasExtDao() {
		return importacionPolizasExtDao;
	}

	public ICorreduriaExternaDao getCorreduriaExternaDao() {
		return correduriaExternaDao;
	}

	public IAuditoriaConfirmacionExtDao getAuditoriaConfirmacionExtDao() {
		return auditoriaConfirmacionExtDao;
	}

	public ICuponDao getCuponDao() {
		return cuponDao;
	}

	public IPolizaDao getPolizaDao() {
		return polizaDao;
	}

	@Override
	public AnexoModificacion saveAnexo(AnexoModificacion anexo) {
		return anexo = anexoModificacionDao.saveAnexoModificacion(anexo);	
	}

	public ISimulacionSbpDao getSimulacionSbpDao() {
		return simulacionSbpDao;
	}

	public void setSimulacionSbpDao(ISimulacionSbpDao simulacionSbpDao) {
		this.simulacionSbpDao = simulacionSbpDao;
	}

	public ISimulacionSbpManager getSimulacionSbpManager() {
		return simulacionSbpManager;
	}

	public void setSimulacionSbpManager(ISimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void validarAnexoPolizaSbp(Poliza poliza,AnexoModificacion anexo,boolean complementaria) {
		
		PolizaSbp polizaSbp = null;
		Long idPolizaSbp = null;
		try {
			idPolizaSbp = simulacionSbpDao.getPolizaSbpId(poliza.getIdpoliza());
			if(idPolizaSbp != null) {
				polizaSbp = simulacionSbpManager.getPolizaSbp(idPolizaSbp);
				
				if(polizaSbp != null && ( "3".equals(polizaSbp.getEstadoPlzSbp()) || "4".equals(polizaSbp.getEstadoPlzSbp()) || "5".equals(polizaSbp.getEstadoPlzSbp()))) {
					//polizaSbp = (PolizaSbp) PolizaSbpDao
					anexo.setRevisarSbp(Constants.CHARACTER_S);
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	public ISolicitudModificacionManager getSolicitudModificacionManager() {
		return solicitudModificacionManager;
	}

	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}
	
	// P0079313 INICIO --> Necesario para que no falle el server
	public IDatosParcelaAnexoDao getDatosParcelaAnexoDao() {
		return datosParcelaAnexoDao;
	}

	public void setDatosParcelaAnexoDao(IDatosParcelaAnexoDao datosParcelaAnexoDao) {
		this.datosParcelaAnexoDao = datosParcelaAnexoDao;
	}
	
	public ISubvencionesDeclarablesModificacionPolizaDao getSubvencionesDeclarablesModificacionPolizaDao() {
		return subvencionesDeclarablesModificacionPolizaDao;
	}

	public void setSubvencionesDeclarablesModificacionPolizaDao(
			ISubvencionesDeclarablesModificacionPolizaDao subvencionesDeclarablesModificacionPolizaDao) {
		this.subvencionesDeclarablesModificacionPolizaDao = subvencionesDeclarablesModificacionPolizaDao;
	}

	public IDeclaracionModificacionPolizaDao getDeclaracionModificacionPolizaDao() {
		return declaracionModificacionPolizaDao;
	}

	public void setDeclaracionModificacionPolizaDao(IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) {
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao;
	}

	public IDatosExplotacionAnexoDao getDatosExplotacionAnexoDao() {
		return datosExplotacionAnexoDao;
	}

	public void setDatosExplotacionAnexoDao(IDatosExplotacionAnexoDao datosExplotacionAnexoDao) {
		this.datosExplotacionAnexoDao = datosExplotacionAnexoDao;
	}	
	// P0079313 FIN
	
	//P0079361 INICIO
	public ISolicitudReduccionCapManager getSolicitudReduccionCapManager() {
		return solicitudReduccionCapManager;
	}

	public void setSolicitudReduccionCapManager(ISolicitudReduccionCapManager solicitudReduccionCapManager) {
		this.solicitudReduccionCapManager = solicitudReduccionCapManager;
	}

	public IReduccionCapitalDao getReduccionCapitalDao() {
		return reduccionCapitalDao;
	}

	public void setReduccionCapitalDao(IReduccionCapitalDao reduccionCapitalDao) {
		this.reduccionCapitalDao = reduccionCapitalDao;
	}  

	public com.rsi.agp.dao.models.rc.ICuponDao getCuponRCDao() {
		return cuponRCDao;
	}

	public void setCuponRCDao(com.rsi.agp.dao.models.rc.ICuponDao cuponRCDao) {
		this.cuponRCDao = cuponRCDao;
	}

	public com.rsi.agp.dao.models.rc.IEnviosSWSolicitudDao getEnviosSWRCSolicitudDao() {
		return enviosSWRCSolicitudDao;
	}

	public void setEnviosSWRCSolicitudDao(com.rsi.agp.dao.models.rc.IEnviosSWSolicitudDao enviosSWRCSolicitudDao) {
		this.enviosSWRCSolicitudDao = enviosSWRCSolicitudDao;
	}
	//P0079361 FIN
}