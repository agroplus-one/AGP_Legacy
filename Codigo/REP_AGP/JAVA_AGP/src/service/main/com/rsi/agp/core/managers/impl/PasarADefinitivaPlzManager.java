package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IPasarADefinitivaPlzManager;
import com.rsi.agp.core.managers.impl.sbp.SimulacionSbpManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsRC;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.util.DateUtil;
import com.rsi.agp.core.util.ErrorAcuseDefMultiple;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.core.ws.ResultadoWS;
import com.rsi.agp.dao.filters.poliza.SeleccionPolizaFiltro;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.models.config.IErrorWsAccionDao;
import com.rsi.agp.dao.models.poliza.IModuloPolizaDao;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.models.poliza.IPolizaComplementariaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.models.ref.IReferenciaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.acuseRecibo.Error;

public class PasarADefinitivaPlzManager implements IPasarADefinitivaPlzManager{
	
	private static final String VALIDACIONES_PREVIAS = "validacionesPrevias";
	private static final String ACTUALIZAR_SBP = "actualizarSbp";
	private static final String REAL_PATH = "realPath";
	private static final String ASIGNAR_REFERENCIA_POLIZA = "asignarReferenciaPoliza";
	private static final String PASO_A_DEFINITIVA = "pasoADefinitiva";
	private static final String RESULTADO = "resultado";
	private static final String ACUSE_PPAL = "acusePpal";
	private static final String MENSAJE_ALTA_DEFINITIVA_KO = "mensaje.alta.definitiva.KO";
	private static final String ALERTA = "alerta";
	private static final String DO_PASAR_A_DEFINITIVA_MULTIPLE = "doPasarADefinitivaMultiple";
	private static final String USUARIO = "usuario";
	private static final String DO_PASAR_A_DEFINITIVA = "doPasarADefinitiva";
	
	private Log logger = LogFactory.getLog(PasarADefinitivaPlzManager.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	final ResourceBundle bundleSbp = ResourceBundle.getBundle("agp_sbp");

	private SeleccionPolizaManager seleccionPolizaManager;
	private IncompatibilidadClaseManager incompatibilidadClaseManager;
	private PolizaManager polizaManager;
	private SimulacionSbpManager simulacionSbpManager;
	private CaracteristicaExplotacionManager caracteristicaExplotacionManager;
	private ComparativaManager comparativaManager;
	private IReferenciaDao referenciaDao;
	private IPolizaDao polizaDao;
	private IModuloPolizaDao moduloPolizaDao;
	private ISeleccionPolizaDao seleccionPolizaDao;
	private ClaseManager claseManager;
	private ParametrizacionManager parametrizacionManager;
	private IErrorWsAccionDao errorWsAccionDao;
	private WebServicesManager webServicesManager;
	private WebServicesCplManager webServicesCplManager;
	@SuppressWarnings("unused")
	private IPolizaComplementariaDao polizaComplementariaDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private IPolizasPctComisionesDao polizasPctComisionesDao;
	protected IPagoPolizaDao pagoPolizaDao;
	private EleccionFormaPagoManager eleccionFormaPagoManager;
	private PolizaRCManager polizaRCManager;
	
	// Hora y minuto limite para el paso a definitiva en el dia
	public static final int HORA = 16;
	public static final int MINUTO = 40;
	
	@Override
	public Map<String, Object> doPasarADefinitiva(Long idPoliza, Map<String, Object> parametros, HttpServletRequest request) {
		
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "inicio");
		Usuario usuario = (Usuario)request.getSession().getAttribute(PasarADefinitivaPlzManager.USUARIO);
		// Mapa que almacena los errores producidos en el proceso de paso a definitiva
		Map<String, Object> errores = new HashMap<String, Object>();
		String codTerminal = (String) request.getSession().getAttribute("codTerminal");
		String idInternoPe = request.getParameter("idInternoPe");
		boolean debeFirmar = !StringUtils.isNullOrEmpty(codTerminal) && !StringUtils.isNullOrEmpty(idInternoPe);
		
		
		// -------------------------------------------
		// -- CARGA DE BD DE LOS DATOS DE LA POLIZA --
		// -------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Carga de bd la poliza con id = " + idPoliza);
		Poliza p = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
		
		String docFirmada = (String) parametros.get("docFirmada");
		log (PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "DOCFIRMADA: " + docFirmada);
		
		/* Comprobacion estado Enviada Correcta [ESC - 5876] */
		log (PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Comprobamos si el estado no es Enviada correcta para poder contiuar con el paso a definitiva");

		BigDecimal idEstadoPoliza = p.getEstadoPoliza().getIdestado();
		if (idEstadoPoliza != null && ConstantsSbp.ENVIADA_CORRECTA.equals(idEstadoPoliza)) {
			log (PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "El estado ya esta en Enviada correcta");
			return errores;
		}
		/******************************************************/
		
		// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
		// recuperamos el valor de swConfirmacion
		boolean swConfirm = (Boolean) parametros.get("swConfirmacion");
		
		// Redireccion y envio de parametros a jsp
		log ("doPasarADefinitivaPlz","@@**@@-Valor de swConfirm recibido:"+swConfirm);
		// Pet. 22208 ** MODIF TAM (02.03.2018) ** fin	//		
		
		
		//Guardamos los datos del aval en caso de que sea una poliza financiada
		//Si viene del popup
		if(!StringUtils.nullToString(request.getParameter("numeroAval")).equals("")){
			String numeroAval = request.getParameter("numeroAval");
			String importeAval = request.getParameter("importeAval");
			
			DatosAval datosAval = null;
			
			if(p.getDatosAval()!=null){
				datosAval = p.getDatosAval();
			}else{
				datosAval = new DatosAval();
				datosAval.setPoliza(p);
			}
			
			datosAval.setNumeroAval(new BigDecimal(numeroAval));
			datosAval.setImporteAval(new BigDecimal(importeAval));

			try{
				polizaManager.AddDatosAval(datosAval);
				p.setDatosAval(datosAval);
			}catch(DAOException e){
				errores.put(Constants.KEY_ALERTA, "Error inesperado al guardar los datos del aval");
				logger.error("Error inesperado al guardar los datos del aval de la poliza con id = " + idPoliza);
			}
			
			for(com.rsi.agp.dao.tables.poliza.PagoPoliza pp : p.getPagoPolizas()){
				pp.setFormapago('F');					
			}
		}

		
		// -----------------------------------------------
		// -- VALIDACIONES PREVIAS AL PASO A DEFINITIVA --
		// -----------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Se realizan las validaciones previas al paso a definitiva");								 

		// Llama al metodo de validaciones previas indicando que compruebe las fechas de contratacion
		validacionesPrevias(p, errores, parametros, true, usuario);
		// Si hay algun error en las validaciones no se continua el proceso
		
		if (errores.size() > 0) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "No se ha superado las validaciones previas por lo que no se continua el paso a definitiva");
			if (p.getReferencia() == null) {
				asignarReferenciaPoliza (p, errores);
			}
			else if (p.getReferencia().isEmpty()) {
				asignarReferenciaPoliza (p, errores);
			}
			return errores;
		}
		
		// ------------------------------------------------------------------------------------------------------------------
		// -- LLAMADA AL METODO QUE REALIZA EL PASO A DEFINITIVA DEPENDIENDO DE SI LA POLIZA ES PRINCIPAL O COMPLEMENTARIA --
		// ------------------------------------------------------------------------------------------------------------------
		if (!debeFirmar) {
			if(!Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(p.getTipoReferencia())){
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// pasamos como nuevo parametro el boolean con el valor de Sw Confirmacion
				pasarADefinitivaPpal(parametros, p, errores, swConfirm);
			} else {
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// pasamos como nuevo parametro el boolean con el valor de Sw Confirmacion
				pasarADefinitivaCpl (parametros, p.getIdpoliza(), errores, swConfirm);
			}
		}
		else if (Constants.CHARACTER_S.equals(docFirmada)) {
			if(!Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(p.getTipoReferencia())){
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// pasamos como nuevo parametro el boolean con el valor de Sw Confirmacion
				pasarADefinitivaPpal(parametros, p, errores, swConfirm);
			} else {
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// pasamos como nuevo parametro el boolean con el valor de Sw Confirmacion
				pasarADefinitivaCpl (parametros, p.getIdpoliza(), errores, swConfirm);
			}
		} 
				
		// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
		// Solo llamamos a la generacion del mensaje si no venimos pro swConfirmacion
		if (swConfirm == false){
  		   // --------------------------------------------------------------------------------
		   // -- GENERACION DE MENSAJE DE AVISO DEPENDIENDO DE LA HORA DE PASO A DEFINITIVA --
		   // --------------------------------------------------------------------------------
		   generacionMsgAviso(errores);
		}
		
		// RC DE GANADO
		if (p.getLinea().isLineaGanado()) {
			
		
			try { 
				PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(idPoliza);
				
				if (polizaRC != null) {
					
					this.polizaRCManager.cambiaEstadoPolizaRC(polizaRC,
							ConstantsRC.ESTADO_RC_DEFINITIVA,
							usuario.getCodusuario());	
				}
			} catch (BusinessException e) {
				
				errores.put(Constants.KEY_ALERTA, "Error inesperado al pasar a definitiva la poliza de RC de Ganado");
			}
		}
		
		if (debeFirmar) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Redireccion a pantalla de errores y llamada automatica a componente de firma");
			errores.put("autoCompFirma", Boolean.TRUE);
		}
		
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "fin");		
		
		return errores;
	}
	
	@Override
	public Map<String, Object> doPasarADefinitivaMultiple(Map<String, Object> parametros, HttpServletRequest request) {
		
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "inicio");
		
		// Mapa que almacena los errores que se produzcan al pasar a definitiva las polizas
		Map<String, Object> errores = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario)request.getSession().getAttribute(PasarADefinitivaPlzManager.USUARIO);
		
		// ---------------------------------------------
		// -- CARGA DE BD DE LOS DATOS DE LAS POLIZAS --
		// ---------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Carga de bd las polizas");
		String listIdPolizas = (String)parametros.get("idsPoliza");
		List<Poliza> listPolizas = getListObjPolFromString(listIdPolizas);		
		if (listPolizas.isEmpty()) {
			// El listado es vacio si se ha producido algun error al cargar las polizas
			log (PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Ocurrio un error al cargar el listado de polizas de bd");
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return errores;
		}
		
		// -----------------------------------------------
		// -- VALIDACIONES PREVIAS AL PASO A DEFINITIVA --
		// -----------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Se realizan las validaciones previas al paso a definitiva multiple");		
		validacionesPreviasMultiple(listPolizas, errores, parametros, listIdPolizas, usuario);
		// Si se ha comprobado que se esta fuera de las fechas de contratacion no se continua el proceso
		// Si hay otros errores de validacion se mostraran en la pagina de resultados
		if (errores.size()>0 && errores.containsKey("popUpAmbiCont")) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "No se ha superado las validaciones previas por lo que no se continua el paso a definitiva");
			return errores;
		}
				
		// ------------------------------------------------------------------------------------------------------------------
		// -- LLAMADA AL METODO QUE REALIZA EL PASO A DEFINITIVA DEPENDIENDO DE SI LA POLIZA ES PRINCIPAL O COMPLEMENTARIA --
		// -- Y CARGA EL MAPA DE POLIZAS A REENVIAR A LA JSP															   --
		// ------------------------------------------------------------------------------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Recorre el listado de polizas y las pasa a definitiva");				
		pasarADefinitivaYCargarMapa(parametros, errores, listIdPolizas,	listPolizas,usuario);

		return errores;
	}
	
	

	/**
	 * Recorre el listado de polizas y pasa a definitiva las que no hayan generado un error en las validaciones previas
	 * @param parametros 
	 * @param errores
	 * @param listIdPolizas
	 * @param listPolizas
	 */
	@SuppressWarnings("unchecked")
	private void pasarADefinitivaYCargarMapa(Map<String, Object> parametros, Map<String, Object> errores, String listIdPolizas,
			List<Poliza> listPolizas,Usuario usuario) {
		
		// Mapa que se enviara a la jsp para informar del resultado del paso a definitiva de las polizas
		Map<String, Object> mapaPolizas = new HashMap<String, Object>();				
		
		// Variable que indica si alguna poliza ha generado error al pasar a definitiva
		boolean algunError = false; 
		// Variable que indica si alguna poliza ha pasado a definitiva correctamente
		boolean algunOK = false;
		// Cadena que almacena los ids de poliza ha generado error al pasar a definitiva - Se reenvia a la jsp para indicar
		// que polizas se enviaran al pulsar el boton de 'Enviar de nuevo'
		String idsPolizaKO = "";
		
		// Recorre el listado de polizas y pasa a definitiva las que no hayan dado error de validacion previa
		for (Poliza p : listPolizas) {
			
			Map<String, Object> errPD = new HashMap<String, Object>();
			
			// Si la poliza tiene un error de validacion previo no se pasa a definitiva
			if (hayErrorValidacionPrevio(errores, p)) {
				
				log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "La poliza con id = " + p.getIdpoliza() + " tiene errores de validacion previos, por lo que no se pasara a definitiva");
				
				// Se introduce en el mapa 'errorPD' el mensaje de error de validacion de la poliza
				errPD.put(PasarADefinitivaPlzManager.ALERTA, ((Map<String, Object>) errores.get(p.getIdpoliza().toString())).get(PasarADefinitivaPlzManager.ALERTA));
				
				
				// Se comprueba si el error de validacion es por el acuse de recibo del SW
				Map<String, Object> mapaErrores = (Map<String, Object>) errores.get(p.getIdpoliza().toString());
				
				if (mapaErrores.containsKey(PasarADefinitivaPlzManager.ACUSE_PPAL) || mapaErrores.containsKey(PasarADefinitivaPlzManager.RESULTADO)) {
					
					// Para polizas principales
					AcuseRecibo acuse = (AcuseRecibo) mapaErrores.get(PasarADefinitivaPlzManager.ACUSE_PPAL);
					if (acuse == null) {
						// Para polizas complementarias
						acuse = (AcuseRecibo) mapaErrores.get(PasarADefinitivaPlzManager.RESULTADO);
					}
					
					// Limpia los errores WS
					BigDecimal codLinea = p.getLinea().getCodlinea();
					BigDecimal codPlan = p.getLinea().getCodplan();
					BigDecimal codEntidad = p.getAsegurado().getEntidad().getCodentidad();
					WSUtils.limpiaErroresWs(acuse, Constants.WS_PASAR_DEFINITIVA, new Parametro (null, '1', null, null, null), this.errorWsAccionDao, codPlan, codLinea, codEntidad, Constants.PASAR_DEFINITIVA);
					
					logger.debug(acuse);
					for (int i = 0; i < acuse.getDocumentosRecibidos(); i++) {
						Documento documento = acuse.getDocumentoArray(i);
						
						if (documento.getErrorArray() != null && documento.getErrorArray().length > 0) {
							
							List<ErrorAcuseDefMultiple> listErroresAcuse = new ArrayList<ErrorAcuseDefMultiple>();
							
							for (Error error : documento.getErrorArray()) {
									listErroresAcuse.add(new ErrorAcuseDefMultiple (error));
							}
							
							p.setListErroresAcuse(listErroresAcuse);
						}
					}
					
				}
				
			}
			// La poliza no tiene errores previos, se pasa a definitiva
			else {	
				log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Pasa a definitiva de la poliza con id = " + p.getIdpoliza());						
					
				// Se llama un metodo u otro dependiendo si la poliza es principal o complementaria
				boolean esCpl = Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(p.getTipoReferencia());
				
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
				// pasamos como nuevo parametro el boolean con el valor de Sw Confirmacion
				boolean swConfirm = (Boolean) parametros.get("swConfirmacion");				
				pasarADefinitivaPpal(parametros, p, errores, swConfirm);
				
				// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio // 
				// anhadimos el envio del nuevo parametro para ambos casos
				if (!esCpl) {
					pasarADefinitivaPpal(parametros, p, errPD, swConfirm);
				} else {
					pasarADefinitivaCpl (parametros, p.getIdpoliza(), errPD, swConfirm);
				}
				
			}
			
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA_MULTIPLE, "Se carga el mapa de polizas a reenviar a la jsp");
			
			// Se errPd tiene algo, ha ocurrido algun error en el paso a definitiva de esa poliza												
			p.setErrorEnPasoADefinitiva(!errPD.isEmpty());
			// Si ha ocurrido algun error 
			if (!errPD.isEmpty()) {
				// Se pasa el mensaje a la pagina
				p.setMensaje((errPD.get(PasarADefinitivaPlzManager.ALERTA) != null ) ? errPD.get(PasarADefinitivaPlzManager.ALERTA).toString() : "");
				// Se indica a la jsp que muestre el boton de 'Enviar de nuevo'
				algunError = true;
				// Se añade el id de poliza a la cadena de ids de poliza erroneos
				idsPolizaKO += ";" + p.getIdpoliza();
				// DAA 13/05/2013
				// Si existe alerta de poliza incompatible y el perfil es 0
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR) &&
						StringUtils.nullToString(errPD.get(PasarADefinitivaPlzManager.ALERTA)).equals(bundle.getString("mensaje.alta.definitiva.IncompatibilidadClases"))){					
					AcuseRecibo acuseRecibo = null;
					acuseRecibo = (AcuseRecibo) ((Map<String, Object>) errores.get(p.getIdpoliza().toString())).get(PasarADefinitivaPlzManager.ACUSE_PPAL);
					if (null == acuseRecibo)
						acuseRecibo = (AcuseRecibo) ((Map<String, Object>) errores.get(p.getIdpoliza().toString())).get(PasarADefinitivaPlzManager.RESULTADO);
					
					p.setForzarPasoADef(muestraBotonPasoDef(p.getIdpoliza(), acuseRecibo,usuario));
				}
			}
			// Si el paso a definitiva ha sido correcto
			else {
				algunOK = true;
			}
			/* Si el error es por la validacion de acuse de recibo recuperamos los errores de validacion de 
			 * la poliza y comprobamos si el perfil del usuario puede forzar el paso*/
			if (StringUtils.nullToString(p.getMensaje()).equals(bundle.getString("mensaje.pasarDefinitiva.AcuseRecibo.KO"))){
				if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
					AcuseRecibo acuseRecibo = null;
					acuseRecibo = (AcuseRecibo) ((Map<String, Object>) errores.get(p.getIdpoliza().toString())).get(PasarADefinitivaPlzManager.ACUSE_PPAL);
					if (null == acuseRecibo)
						acuseRecibo = (AcuseRecibo) ((Map<String, Object>) errores.get(p.getIdpoliza().toString())).get(PasarADefinitivaPlzManager.RESULTADO);
					
					p.setForzarPasoADef(muestraBotonPasoDef(p.getIdpoliza(), acuseRecibo,usuario));
				}
			}
			// Inserta la poliza y su id en el mapa para pasarlo a la jsp
			mapaPolizas.put(p.getIdpoliza().toString(), p);
			
			// RC DE GANADO
			if (p.getLinea().isLineaGanado()) {
				
				try { 
					PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(p.getIdpoliza());
					
					if (polizaRC != null) {
						
						this.polizaRCManager.cambiaEstadoPolizaRC(polizaRC,
								ConstantsRC.ESTADO_RC_DEFINITIVA,
								usuario.getCodusuario());	
					}
				} catch (BusinessException e) {
					
					errores.put(Constants.KEY_ALERTA, "Error inesperado al pasar a definitiva la poliza de RC de Ganado");
				}
			}
		}
		
		// Si alguna poliza ha pasado a definitiva correctamente, se genera el aviso de la fecha de envio
		if (algunOK) errores.put("mensaje", generacionMsgAvisoMultiple());
		
		// Se indica que hay que redirigir a la pagina de resultado de grabacion multiple		
		errores.put("listIdPolizas", listIdPolizas);
		errores.put("resultadoGrabMult", "true");			
		errores.put("mapaPolizas", mapaPolizas);
		errores.put("algunError", algunError);		
		errores.put("idsPolizaKO", idsPolizaKO);
	}

	/**
	 * Comprueba si la poliza tiene errores de validacion previos al paso a definitiva
	 * @param errores
	 * @param p
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean hayErrorValidacionPrevio(Map<String, Object> errores, Poliza p) {
		
		// Comprueba si en el mapa de errores hay algun registro asociado al id de la poliza actual
		if (errores.get(p.getIdpoliza().toString()) != null) {
			try {
				return !((Map<String, Object>) errores.get(p.getIdpoliza().toString())).isEmpty();
			}
			catch (Exception e) {
				logger.error("Ocurrio un error al comprobar si hay errores de validacion previos para la poliza con id " + p.getIdpoliza().toString(), e);
				return false;
			}
		}
		
		// Si llega hasta aqui, la poliza no tiene errores de validacion previos
		return false;
	}

	
	/**
	 * Realiza las validaciones previas al paso a definitiva de las polizas seleccionadas
	 * @param listPolizas Listado de polizas que se quiere pasar a definitiva
	 * @param errores Mapa que contendra los errores generados en el proceso de paso a definitiva por cada poliza
	 * @param parametros Mapa de parametros enviados desde la jsp y el Controller
	 * @param listIdPolizas Listado de ids de poliza que se quiere pasar a definitiva
	 */
	private void validacionesPreviasMultiple (List<Poliza> listPolizas, Map<String, Object> errores, Map<String, Object> parametros, String listIdPolizas, Usuario usuario) {
		
		// Variables obtenidas de parametros		
		boolean pasoADefinitivaForzado = !"".equals((String) parametros.get("grFueraContratacion"));
		
		// Si se ha forzado el paso a definitiva no se realiza ninguna validacion
		if (pasoADefinitivaForzado) return;
		
		
		// Se recorre el listado de polizas y se comprueban las validaciones previas
		log("validacionesPreviasMultiple", "Comprueba si existen polizas con el mismo lineaseguroid, nif de asegurado y clase que alguna de las polizas que se va a pasar a definitiva");	
		for (Poliza p : listPolizas) {
			// Variable auxiliar que contendra el posible error de validacion de la poliza actual
			Map<String, Object> erroresAux = new HashMap<String, Object>();
			
			// Establece por defecto la forma de pago 'Cargo en cuenta' a la poliza
			if (setCargoCuentaDefecto(p)) {
				// Si se ha establecido la forma de pago correctamente se realizan las validaciones previas al paso a definitiva indicando 
				// que es un paso a definitiva multiple
				validacionesPrevias(p, erroresAux, parametros, false, usuario);
			}
			else {
				// Si ha ocurrido algun error al establecer la forma de pago por defecto, se almacena el error y no se continua con el 
				// paso a definitiva de la poliza actual
				erroresAux.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.errorFormaPagoDefecto"));
			}
			
			// En el mapa de errores general, se guarda el resultado de la validacion de la poliza indexado por su id
			errores.put(p.getIdpoliza().toString(), erroresAux);
		}
		
	}

	/**
	 * Establece como forma de pago el 'Cargo en cuenta' a la poliza recibida como parametro 
	 * @param p
	 * @return Booleano indicando si el proceso ha finalizado correctamente
	 */
	private boolean setCargoCuentaDefecto(Poliza pol) {
		try {
			PagoPoliza pagosPol;
			boolean cambioMasivoEstPol = true;
			pagosPol = (pol.getPagoPolizas() == null || pol.getPagoPolizas().size() == 0) ? new PagoPoliza() : pol.getPagoPolizas().iterator().next();
			pagosPol.setTipoPago(Constants.CARGO_EN_CUENTA);
			pagosPol.setFormapago(Constants.FORMA_PAGO_ALCONTADO);
			eleccionFormaPagoManager.guardarDatosFormaPago(pagosPol, pol, cambioMasivoEstPol);
			
		} catch (Exception e) {
			logger.error("Error al establecer por defecto la forma de pago 'Cargo en cuenta' a la poliza", e);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Devuelve un listado de objetos Poliza correspondiente a los ids de poliza separados por ; pasados por parametro
	 * @param listIdPolizas
	 * @return
	 */
	private List<Poliza> getListObjPolFromString(String listIdPolizas)
	{
		
		List<Poliza> listPolizas = new ArrayList<Poliza>();
		
		try {
			Poliza poliza;
			String[] listaFin = listIdPolizas.split(";");	
			
			for (int j=0; j<listaFin.length; j++){
				String id = listaFin[j];
				if (!id.equals("")){
					poliza = seleccionPolizaManager.getPolizaById(new Long(id));
					listPolizas.add(poliza);
				}
			}
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al cargar el listado de polizas de bd", e);
		}
		
		return listPolizas;
	}
	
	
	/**
	 * Realiza el paso a definitiva de la poliza complementaria
	 * @param parametros
	 * @param p
	 * @param errores
	 */
	private void pasarADefinitivaCpl(Map<String, Object> parametros, Long idPoliza,	Map<String, Object> errores, boolean swConfirmacion) {
		
		// Se recarga el objeto de poliza de bd para tener todos los cambios realizados
		log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "Se recarga el objeto de poliza de bd para tener todos los cambios realizados");
		Poliza p = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
		

		// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
		// El estado de la poliza debe ser diferente si venimos por "Paso a Definitiva" o si venimos del "Sw // Confirmacion"
		// EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA);
		
		// Actualiza el estado de la poliza
		EstadoPoliza estado;
		if (swConfirmacion == false){
		    estado = new EstadoPoliza(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA);
		}else{
			estado = new EstadoPoliza(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR);
		}

		p.setEstadoPoliza(estado);		
		
		if(swConfirmacion){
			p.setFechaenvio(new Date());
		}
		
		//DAA 31/05/2012
		Usuario usuario= (Usuario) parametros.get(PasarADefinitivaPlzManager.USUARIO);
		String codUsuario= usuario.getCodusuario();
		
		//TMR. Facturacion.Al pasar la cpl a definitiva facturamos
		Long ejec = seleccionPolizaManager.savePolizaFacturacion(p,usuario);
		
		//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
		
		
		Set<PagoPoliza> pagos = p.getPagoPolizas();
		BigDecimal tipoPago =  null;
		Date fechaPrimerPago = null;
		Date fechaSegundoPago = null;
		BigDecimal pctPrimerPago = null;
		BigDecimal pctSegundoPago = null;
		for (PagoPoliza pago:pagos){
			if (pago.getTipoPago() != null)
				tipoPago = pago.getTipoPago();
			if (pago.getFecha() !=  null)
				fechaPrimerPago = pago.getFecha();
			if (pago.getPctprimerpago() != null)
				pctPrimerPago = pago.getPctprimerpago();
			if (pago.getFechasegundopago() != null)
				fechaSegundoPago = pago.getFechasegundopago();			
			if (pago.getPctsegundopago() != null)
				pctSegundoPago = pago.getPctsegundopago();
			
		}
		
		
		// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
		// El estado de la poliza debe ser diferente si venimos por "Paso a Definitiva" o si venimos del "Sw //Confirmacion"
		//historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),codUsuario,Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA,tipoPago,fechaPrimerPago,
		//		pctPrimerPago,fechaSegundoPago,pctSegundoPago);
		if (swConfirmacion == false){
			historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),codUsuario,Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA,tipoPago,fechaPrimerPago,
							pctPrimerPago,fechaSegundoPago,pctSegundoPago);
		}else{
			historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),codUsuario,Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR,tipoPago,fechaPrimerPago,
							pctPrimerPago,fechaSegundoPago,pctSegundoPago);
		}
			
		
		// Si devuelve null ha habido algun error al guardar la poliza
		if (ejec == null) {
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return;
			
		}
				
		//Generamos el xml de poliza y lo guardamos en el campo 'XMLACUSECONTRATACION'
		try {
			polizaManager.grabarXmlDefinitivoCpl(p, null);
		} catch (Exception e) {
			logger.error("Ocurrio algun error al grabar el xml definitivo de la poliza complementaria", e);
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return;
		}
	}


	/**
	 * Realiza el paso a definitiva de la poliza principal
	 * @param parametros
	 * @param p
	 * @param errores
	 * @param boolean con el valor de SwConfirmacion
	 */
	private void pasarADefinitivaPpal(Map<String, Object> parametros, Poliza p,	Map<String, Object> errores, boolean swConfirmacion) {
		
		// ------------------------------------------------------------------------------------------------
		// -- ACCIONES PREVIAS AL PASO A DEFINITIVA DE LA POLIZA QUE SI CANCELAN EL PROCESO AL DAR ERROR --
		// ------------------------------------------------------------------------------------------------
		// Actualizacion de caracteristicas de explotacion
		actualizarCaracteristicasExplotacion(parametros, p, errores);
		
		if (errores.size()>0){
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Ocurrio un error al actualizar las caracteristicas de explotacion, por lo que no se continua el paso a definitiva");
			return;
		}
		
		// Asignar referencia a la poliza
		asignarReferenciaPoliza (p, errores);
		if (errores.size()>0) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Ocurrio un error al asignar una referencia a la poliza, por lo que no se continua el paso a definitiva");
			return;
		}
				
		// Generacion del XML de la poliza para el envio		
		generarXMLDefinitivo(p, errores);
		if (errores.size()>0) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Ocurrio un error al generar el XML de envio de la poliza, por lo que no se continua el paso a definitiva");
			return;
		}
		
		// ------------------------------------------------------------------------------------------------
		// -- ACCIONES PREVIAS AL PASO A DEFINITIVA DE LA POLIZA QUE NO CANCELAN EL PROCESO AL DAR ERROR --
		// ------------------------------------------------------------------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Acciones posteriores al paso a definitiva de la poliza");
		// Actualizacion de la poliza de sobreprecio en el caso de que tenga				
		boolean sbpBorrada = actualizarSbp(parametros, p);
		if (sbpBorrada){
			errores.put("mensaje2", bundleSbp.getString("mensaje.borrar.ok"));
		}
		// Borrado de polizas complementarias asociadas a la actual, en caso de que sea principal		
		actualizarPlzComplementarias(p);
		
		
		// ------------------------------------
		// -- PASO A DEFINITIVA DE LA POLIZA --
		// ------------------------------------
		log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Paso a definitiva de la poliza");
		Usuario usuario= (Usuario) parametros.get(PasarADefinitivaPlzManager.USUARIO);
		//TMR. Facturacion. Le pasamos el objeto usuario para luego usuarlo al facturar
		
		// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio **//
		// Pasamos por parametro el boolean para indicar si venimos por "Pasar a Definitiva" o por "SW Confirmaicon"
		pasoADefinitiva(p.getIdpoliza(), errores, usuario, swConfirmacion); 

		if (errores.size()>0) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Ocurrio un error al actualizar el estado de la poliza, por lo que no se continua el paso a definitiva");
			return;
		}
	}

	
	/**
	 * Asigna una referencia a la poliza que se pasa por parametro
	 * @param p
	 * @param errores
	 */
	private void asignarReferenciaPoliza (Poliza p, Map<String, Object> errores) {
		
		log (PasarADefinitivaPlzManager.ASIGNAR_REFERENCIA_POLIZA, "Asignar referencia a la poliza");
		
		// Si la poliza ya tiene referencia asignada previamente no se hace nada
		if (!StringUtils.isNullOrEmpty(p.getReferencia())) {
			log (PasarADefinitivaPlzManager.ASIGNAR_REFERENCIA_POLIZA, "La poliza ya tiene referencia asignada. No se asignara una nueva referencia.");
			return;
		}
				
		ReferenciaAgricola ra = null;
		
		try {
			// Obtiene la siguiente referencia agricola disponible
			ra = referenciaDao.getSiguienteReferencia();			
			log (PasarADefinitivaPlzManager.ASIGNAR_REFERENCIA_POLIZA,"Referencia obtenida: " + ra.getReferencia() + " - " + ra.getDc());			
			
			// Se asigna la referencia a la poliza y se actualizan sus datos
			p.setReferencia(ra.getReferencia());
			p.setDc(new BigDecimal (ra.getDc()));
			polizaDao.savePoliza(p);
			log (PasarADefinitivaPlzManager.ASIGNAR_REFERENCIA_POLIZA,"Poliza actualizada");						
			
		} catch (DAOException e) {									
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return;
		}
	}

	/**
	 * Generacion de mensaje de aviso dependiendo de la hora de paso a definitiva de la poliza
	 * @param errores
	 */
	private void generacionMsgAviso(Map<String, Object> errores) {
		
		log("generacionMsgAviso", "Comprobacion de plazos de la poliza");
		
		// Pet.22208 ** MODIF TAM (12.04.2018) - Si hay alguna alerta, no 
		// incluimos los mensajes de poliza pasada a definitiva
		if (errores.get(PasarADefinitivaPlzManager.ALERTA)== null){
		
  		   // Genera el aviso para el usuario que indica cuando se realizara el envio de la poliza
  		   // Si la hora actual es menos que las 16:40 se enviara en el dia y si no al dia siguiente
		   boolean avisoMan = DateUtil.horaActualMayor (HORA, MINUTO);
		
  		   // Envia los avisos a mostrar al usuario
		   errores.put("mensaje", bundle.getString("mensaje.poliza.change.state.definitiva"));
		   errores.put("mensaje1", bundle.getString("mensaje.poliza.change.state.definitiva.envio." + (avisoMan ? "man" : "hoy")));
		}
	}
	
		
	/**
	 * Genera el mensaje de aviso dependiendo de la hora de paso a definitiva de las polizas
	 * @return
	 */
	private String generacionMsgAvisoMultiple() {
		
		log("generacionMsgAvisoMultiple", "Comprobacion de plazos de la poliza");
		
		// Genera el aviso para el usuario que indica cuando se realizara el envio de la poliza
		// Si la hora actual es menos que las 16:40 se enviara en el dia y si no al dia siguiente
		boolean avisoMan = DateUtil.horaActualMayor (HORA, MINUTO);
		
		return bundle.getString("mensaje.poliza.change.state.definitiva.envio.mult." + (avisoMan ? "man" : "hoy"));
	}


	/**
	 * Generacion del XML de la poliza para el envio
	 * @param p
	 */
	public void generarXMLDefinitivo(Poliza p, Map<String, Object> errores) {
		
		log("generarXMLDefinitivo", "Generacion del XML de la poliza para el envio");				
		
		// GENERA XML Y LO GUARDAMOS (se guarda en xmlacusecontratacion)
		try {
			if(p.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL))
				polizaManager.grabarXmlDefinitivo(p);
			else
				polizaManager.grabarXmlDefinitivoCpl(p, null);
			
		} catch (ValidacionPolizaException e) {
			logger.error("Ocurrio un error al generar el XML de la poliza", e);
			errores.put(PasarADefinitivaPlzManager.ALERTA, e.getMessage());
		} catch (Exception e) {
			logger.error("Ocurrio un error al grabar el XML de la poliza", e);
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
		}
	}




	/**
	 * @param parametros
	 * @param p
	 */
	private void actualizarCaracteristicasExplotacion(Map<String, Object> parametros, Poliza p, Map<String, Object> errores) {
		// CARACTERISTICA EXPLOTACION (comprobamos si la lleva, mirando el obj. poliza)
		// si no la lleva la calculamos llamando WS de calculo	
		
		log("actualizarCaracteristicasExplotacion", "Actualizacion de caracteristicas de explotacion");
		
		try {
			boolean aplicaCaractExpl = caracteristicaExplotacionManager.aplicaYBorraCaractExplocion(p);			
			
			if(aplicaCaractExpl){
				
				boolean tieneCaractExplo = caracteristicaExplotacionManager.validarCaractExplotacion(p);
				
				if (!tieneCaractExplo) {				
					BigDecimal carExpl = caracteristicaExplotacionManager.calcularCaractExplotacion(p, (String)parametros.get(PasarADefinitivaPlzManager.REAL_PATH));
					List<ComparativaPoliza> listComparativasAux = p
							.getComparativaPolizas() != null ? Arrays.asList(p
							.getComparativaPolizas().toArray(
									new ComparativaPoliza[] {}))
							: new ArrayList<ComparativaPoliza>();
					ComparativaPoliza compP = comparativaManager.guardarComparatCaracExplot(listComparativasAux.get(0), p, carExpl);
					p.getComparativaPolizas().add(compP);
				}
			}
		} catch (Exception e) {
			log("actualizarCaracteristicasExplotacion", "Error al actualizar las caracteristicas de explotacion");
			logger.error(e);
			
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
		}
	}




	/**
	 * Borrado de polizas complementarias asociadas a la actual, en caso de que sea principal
	 * @param p
	 */
	private void actualizarPlzComplementarias(Poliza p) {
		
		log("actualizarPlzComplementarias", "Borrado de polizas complementarias asociadas a la actual, en caso de que sea principal");
		
		// SOLO PARA POLIZA PRINCIPAL:  
		// borramos todas las polizas complementarias de la principal al pasar a definitiva
		if(p.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
			
			  try {
					polizaDao.actualizarEstadoComplementaria(p);
					logger.debug("Se cambia el estado de las complementarias");
					
				}catch (Exception e) {
					logger.error("Se ha producido un error en cambia estado en las complementarias ", e);
					
				}
			
		}
	}




	/**
	 * Borra la poliza de sobreprecio asociada a la poliza actual en el caso de que exista
	 * @param parametros
	 * @param p
	 */
	private boolean actualizarSbp(Map<String, Object> parametros, Poliza p) {
		
		log(PasarADefinitivaPlzManager.ACTUALIZAR_SBP, "Actualizacion de la poliza de sobreprecio");
		boolean SbpBorrada = false;
		if ("true".equals(parametros.get(PasarADefinitivaPlzManager.ACTUALIZAR_SBP))){
			Set<PolizaSbp> lstPolSbp = new HashSet<PolizaSbp>();
			lstPolSbp = p.getPolizaPrincipal();
			if (lstPolSbp != null){
				for (PolizaSbp polSbp:lstPolSbp){
					try {
						 // borramos la Sbp
						simulacionSbpManager.bajaPolizaSbp(polSbp);
						log(PasarADefinitivaPlzManager.ACTUALIZAR_SBP,"idPolizaSbp borrada = " + polSbp.getId());
						//SbpBorrada = true;
					} catch (Exception e) {
						log(PasarADefinitivaPlzManager.ACTUALIZAR_SBP, "Error al actualizar la poliza de sobreprecio asociada");
						logger.error(e);
					}
				}
			}
		}
		return SbpBorrada;
	}

	/**
	 * Realiza el cambio de estado de la poliza
	 * @param usuarioDefinitiva 
	 * @param p
	 * @param swConfirmacion, para indicarnos por opcion se esta ejecutando.
	 */
	private Map<String, Object> pasoADefinitiva(Long idPoliza, Map<String, Object> errores, Usuario usuario, boolean swConfirmacion) {
		
		// Se recarga el objeto de poliza de bd para tener todos los cambios realizados
		log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "Se recarga el objeto de poliza de bd para tener todos los cambios realizados");
		log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA,"@@**@@ Valor del nuevo parametro swConfirmacion:"+swConfirmacion);
		
		Poliza p = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
		
		// Comprobacion del xml
		try {
			if (p.getXmlacusecontratacion()!= null && p.getXmlacusecontratacion().length()>0) {
				log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "Se ha actualizado el xml en el registro de la poliza correctamente");
			}
			else {
				log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "No se ha actualizado el xml en el registro de la poliza");
				// Indica que ha habido un error que para el proceso de paso a definitiva
				errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
				return errores;
				
			}
		} catch (SQLException e1) {
			logger.error("Ocurrio un error al obtener elxml de la poliza", e1);
			
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return errores;
		}
		
		// Recuperamos el modulo del set de modulos seleccionados 
		// (En estos momentos solo tenemos 1) y lo guardamos en codmodulo
		try {										
			p.setCodmodulo(((ModuloPoliza) moduloPolizaDao.getModuloPoliza (p.getIdpoliza(), p.getLinea().getLineaseguroid())).getId().getCodmodulo());
			log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "Asignado el modulo '" + p.getCodmodulo() + "' a la poliza.");
		} 
		catch (Exception e) {			
			logger.error("Ocurrio un error al obtener el modulo de la poliza", e);
			
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));
			return errores;
		}
		
		try {
			
			// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
			// El estado de la poliza debe ser diferente si venimos por "Paso a Definitiva" o si venimos del "Sw Confirmacion"
			//EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA);
			EstadoPoliza estado;
			
			if (swConfirmacion == false){
			    estado = new EstadoPoliza(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA);
			}else{
				estado = new EstadoPoliza(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR);
			}
			
			p.setEstadoPoliza(estado); // set estado poliza
			
			if(swConfirmacion){
				p.setFechaenvio(new Date());
			}
			//TMR .Facturacion. Al pasar a definitiva una poliza facturamos.
			seleccionPolizaManager.savePolizaFacturacion(p, usuario);	
			
			//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico
			Set<PagoPoliza> pagos = p.getPagoPolizas();
			BigDecimal tipoPago =  null;
			Date fechaPrimerPago = null;
			Date fechaSegundoPago = null;
			BigDecimal pctPrimerPago = null;
			BigDecimal pctSegundoPago = null;
			for (PagoPoliza pago:pagos){
				if (pago.getTipoPago() != null)
					tipoPago = pago.getTipoPago();
				if (pago.getFecha() !=  null)
					fechaPrimerPago = pago.getFecha();
				if (pago.getPctprimerpago() != null)
					pctPrimerPago = pago.getPctprimerpago();
				if (pago.getFechasegundopago() != null)
					fechaSegundoPago = pago.getFechasegundopago();			
				if (pago.getPctsegundopago() != null)
					pctSegundoPago = pago.getPctsegundopago();				
			}
			// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
			// El estado de la poliza debe ser diferente si venimos por "Paso a Definitiva" o si venimos del "Sw Confirmacion"
			//			historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA,tipoPago,fechaPrimerPago,
			//pctPrimerPago,fechaSegundoPago,pctSegundoPago);
			if (swConfirmacion == false){
			   historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA,tipoPago,fechaPrimerPago,
					pctPrimerPago,fechaSegundoPago,pctSegundoPago);
			}else{
			   historicoEstadosManager.insertaEstadoPoliza(p.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR ,tipoPago,fechaPrimerPago,
					pctPrimerPago,fechaSegundoPago,pctSegundoPago);
				
			}
			
			// P0073327 ACTUALIZAMOS EL % DE COMISION DE LA POLIZA CON EL UTILIZADO EN EL CALCULO
			this.polizasPctComisionesDao.updatePctComsMaxCalculada(p);
			
			seleccionPolizaDao.evict(p);
			
			log (PasarADefinitivaPlzManager.PASO_A_DEFINITIVA, "Actualizado el estado de la poliza.");
		}	
		catch (Exception e) {			
			logger.error("Ocurrio un error al actualizar el estado de la poliza", e);
			
			// Indica que ha habido un error que para el proceso de paso a definitiva
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString(PasarADefinitivaPlzManager.MENSAJE_ALTA_DEFINITIVA_KO));			
		}
		
		return errores;
	}

	

	/**
	 * Realiza las validaciones previas al paso a definitiva de la poliza
	 * @param p Poliza a validar
	 * @param errores Mapa donde se insertaran los errores de validacion que se produzcan
	 * @param parametros Mapa de parametros enviados desde formulario y del controlador
	 * @param isPasoSimple Sera true cuando se vaya a pasar a definitiva una sola poliza y false si el paso es multiple, 
	 * ya que en este caso no hay que validar los datos relativos a la forma de pago
	 */
	private void validacionesPrevias(Poliza p, final Map<String, Object> errores, Map<String, Object> parametros, boolean isPasoSimple, Usuario usuario) {
		

		// Comprueba si se ha forzado el paso a definitiva		
		boolean pasoADefinitivaForzado = !"".equals((String) parametros.get("grFueraContratacion"));
		
		
		// Si se ha forzado el paso a definitiva no se realiza ninguna validacion
		if (pasoADefinitivaForzado) {
			log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Se ha forzado el paso a definitiva. No se realizara ninguna validacion.");
			return;
		}		
		
		// Variable que indica si la poliza a pasar a definitiva es complementaria
		boolean esCpl = Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(p.getTipoReferencia());
		
		
		// Si la poliza es complementaria valida que la principal este contratada
		if (esCpl && !principalContratada(p)) {
			log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "La poliza principal asociada no esta contratada. No se continuara con las validaciones");
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.cpl.principalNoContratada"));
			return;
		}
		
		// Obtiene la clase asociada a la poliza
		Clase c = claseManager.getClase(p);		
		// Si la clase es nula se devuelve el error
		if (c == null) {
			log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "La clase asociada a la poliza es nula. No se continuara con las validaciones");
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.clase.KO"));
			return;
		}
		
		// Si existen polizas con el mismo lineaseguroid, nif de asegurado y clase que la que se va a pasar a definitiva no se continua
		// Si es complementaria no se hace esta validacion
		log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba si existen polizas con el mismo lineaseguroid, nif de asegurado y clase que la que se va a pasar a definitiva");
		if (!esCpl && existePolizaMismosDatos(p, c)) {
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.pasarDefinitiva.otroColectivo.KO"));
			return;
		}
		
		// Validacion de incompatibilidad por clase
		// Si es complementaria no se hace esta validacion
		log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba la incompatibilidad por clase");
		if (!esCpl && !isCompatible(p)) {
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.IncompatibilidadClases"));
			return;
		}
		
		// Comprobamos que la poliza tiene DISTRIBUCION DE COSTES
		log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba si la poliza tiene distribucion de costes");
		if (!polizaManager.checkDistribucionCosteSubv(p)){
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "La poliza no tiene distribucion de costes");
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.NoDistribucionCostes"));
			return;
		}
		
		// Comprobamos que la poliza tiene correctamente almacenados los datos de pago
		// Unicamente para el paso a definitiva de una sola poliza 
		if (isPasoSimple) {
			log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba si la poliza tiene correctos los datos de pago");				
			int resultado = polizaManager.checkDatosDePago(p);
			switch (resultado){
				case 1:
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.NoDatosDepago"));
					return;				
				case 2:
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.DatosDePago.importe.KO"));
					return;					
				case 3:
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.DatosDepago.pctprimerpago.KO"));
					return;					
				case 4:
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.DatosDePago.cccbanco.KO"));
					return;
				default:
					break;
			}
		}
				
		// Comprobacion de la parametrizacion de la poliza
		// Si es complementaria no se hace esta validacion
		log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba si la parametrizacion de la poliza es correcta");
		if (!esCpl && (!(Boolean)parametros.get("resultadoValidacion") || !polizaManager.validarParametrizacionPoliza(p, c))) {
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "La parametrizacion de la poliza no es correcta");
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.alta.definitiva.detail.KO"));
			return;
		}
	
		//AMG 31/10/2014
		// Comprobacion parametros de comisiones - Solo se comprueba si el plan >= 2015 y la poliza es principal
		if (p.isPlanMayorIgual2015() && !esCpl) {
		
			final Map<String, Object> erroresPct= compruebaPolizaPctComisiones(p, usuario);
			if(erroresPct!=null && erroresPct.size()>0){
				errores.putAll(erroresPct);
				return;
			}
		}

		log(PasarADefinitivaPlzManager.VALIDACIONES_PREVIAS, "Comprueba si hay errores en el acuse de recibo");
		if (erroresAcuseRecibo(p, errores, esCpl, parametros,usuario)) {
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.pasarDefinitiva.AcuseRecibo.KO"));
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Se han encontrado errores en el acuse de recibo");
			return;
				
		}
		
	}
	
		
	@SuppressWarnings("rawtypes")
	private final Map<String, Object> compruebaPolizaPctComisiones(final Poliza p, final Usuario usuario){
		Map<String, Object> errores=new HashMap<String, Object>();
		Object[] comisionesESMed =null;
		Descuentos descuentos =null;
		List res=null;
		if (p.getSetPolizaPctComisiones() != null && p.getSetPolizaPctComisiones().size()>0){
			try {
				for (Iterator iterator = p.getSetPolizaPctComisiones().iterator(); iterator.hasNext();) {
					// ## Comun a todos los grupos de negocio. Solo lo evaluamos una vez					
					if(null==res)res=polizasPctComisionesDao.getParamsGen (p.getLinea().getLineaseguroid(),
											p.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
											p.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
				
					if(res!=null && res.size()>0){
						if(null==comisionesESMed){							
							comisionesESMed = polizasPctComisionesDao.getComisionesESMed (p.getLinea().getLineaseguroid(),
									p.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
									p.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
									p.getLinea().getCodlinea(),p.getLinea().getCodplan(),null);							
						}
						if(comisionesESMed!=null){
							if(null==descuentos)descuentos = polizasPctComisionesDao.getDescuentos(
									p.getColectivo().getTomador().getId().getCodentidad(),
									usuario.getOficina().getId().getCodoficina().toString(),
									p.getColectivo().getSubentidadMediadora().getId().getCodentidad(),
									p.getColectivo().getSubentidadMediadora().getId().getCodsubentidad(),
									usuario.getDelegacion(),p.getLinea().getCodplan(),p.getLinea().getCodlinea());
					// ##--------------------------------------------------------------------------------		
							PolizaPctComisiones ppcTemp = (PolizaPctComisiones) iterator.next();
							PolizaPctComisiones ppc= getParamsGenPorGrupoNeg(res, ppcTemp.getGrupoNegocio(),descuentos,comisionesESMed);
							if(ppc!=null){
								// MPM - 28/4/2015
								// Se comprueba si el colectivo asociado a la poliza tiene descuento o recargo, ya que en ese caso
								// el % de la mediadora cambia con respecto al mantenimiento
								modificaPctESMedColectivo(p.getColectivo().gettipoDescRecarg(), p.getColectivo().getpctDescRecarg(), ppc);
								Map<String, Object> erroresVal=new HashMap<String, Object>();
								// SOLO VALIDAMOS SI NO SE ES PERFIL 0 O NO SE HAN REVISADO
								if (!(Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil()) && Long.valueOf(1).equals(ppcTemp.getEstado()))) {
									erroresVal = this.validarPolizaPctComisiones(ppcTemp,ppc,usuario);
								}
								if(erroresVal!=null && erroresVal.size()>0){
									errores.putAll(erroresVal);
								}
								if (errores.get(PasarADefinitivaPlzManager.ALERTA)!= null){
									return errores;
								}
							}else{
								errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("comisiones.alta.poliza.parametrosGenerales.KO"));
								log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Faltan datos de mantenimiento para el plan/linea");
								return errores;		
							}
							
						}else{
							errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("comisiones.alta.poliza.comisionesESMed.KO"));							
							log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Faltan datos de mantenimiento para la E-S Mediadora");
							return errores;	
						}
					}else{
						errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("comisiones.alta.poliza.parametrosGenerales.KO"));
						log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Faltan datos de mantenimiento para el plan/linea");
						return errores;		
					}
				}
			} catch (Exception e) {
				errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("comisiones.alta.poliza.parametrosGenerales.KO"));
				log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "Error al recuperar los datos de comisiones");
				return errores;
			}
		}else{
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.comisiones.poliza.definitiva.Porcentajes.KO"));
			log(PasarADefinitivaPlzManager.DO_PASAR_A_DEFINITIVA, "La poliza no tiene porcentajes de Comisiones");			
		}
		
		return errores;
	}

	@SuppressWarnings("rawtypes")
	private PolizaPctComisiones getParamsGenPorGrupoNeg(List comisionesParamGen, Character grupoNegocio, Descuentos descuentos, Object[] comisionesESMed){
		PolizaPctComisiones ppc  = null;
		for (int i = 0; i < comisionesParamGen.size(); i++) {
			Object[] paramsGen = (Object[]) comisionesParamGen.get(i);
			String sGrupoNegocio=(String)paramsGen[3];
			Character grNegocio=sGrupoNegocio.toCharArray()[0];
			if(grNegocio.equals(grupoNegocio)){
				ppc=new PolizaPctComisiones();
				ppc.setPctadministracion((BigDecimal) paramsGen[0]);
				ppc.setPctadquisicion((BigDecimal) paramsGen[1]);
				ppc.setPctcommax((BigDecimal) paramsGen[2]);
				String sGn=(String)paramsGen[3];
				if(sGn!=null)
					ppc.setGrupoNegocio(sGn.toCharArray()[0]);
			    if (descuentos != null)
			    	ppc.setPctdescmax(descuentos.getPctDescMax());
			    else
			    	ppc.setPctdescmax(null);
				ppc.setPctentidad((BigDecimal) comisionesESMed[0]);
				ppc.setPctesmediadora((BigDecimal) comisionesESMed[1]);
			}
		}
		if(ppc==null && !grupoNegocio.equals(new Character('9'))){
			ppc= getParamsGenPorGrupoNeg(comisionesParamGen, new Character('9'), descuentos, comisionesESMed);
		}
		return ppc;
	}
	
	/**
	 * Si el colectivo asociado a la poliza tiene descuento o recargo, se aplica este al % configurado en el mantenimiento de comisiones
	 * para que al comprobar con el mismo % de la poliza sea correcto
	 * @param tipoDescRecarg Indicador de descuento/recargo
	 * @param pctDescRecarg Porcentaje del descuento/recargo
	 */
	private void modificaPctESMedColectivo(Integer tipoDescRecarg, BigDecimal pctDescRecarg, PolizaPctComisiones ppc) {
		if  (tipoDescRecarg != null && pctDescRecarg != null){
			 BigDecimal pctEsMedNueva = null;
			 if (tipoDescRecarg.compareTo(new Integer(Constants.TIPO_DESC))==0) {
				// PCTESMEDIADORA nueva = (1 - (PCT_DESC_RECARG/100))* PCTESMEDIADORA
				 pctEsMedNueva = (new BigDecimal (1).subtract((pctDescRecarg.divide(new BigDecimal(100))))).multiply(ppc.getPctesmediadora()).setScale(2, BigDecimal.ROUND_HALF_UP);
			 } else if (tipoDescRecarg.compareTo(new Integer(Constants.TIPO_RECARG))==0) {
				// PCTESMEDIADORA nueva = (1 + (PCT_DESC_RECARG/100))* PCTESMEDIADORA
				 pctEsMedNueva = ((pctDescRecarg.divide(new BigDecimal(100))).add(new BigDecimal(1))).multiply(ppc.getPctesmediadora()).setScale(2, BigDecimal.ROUND_HALF_UP);
			 }			 
			 if (pctEsMedNueva != null)
				 ppc.setPctesmediadora(pctEsMedNueva);
		}
	}
	
	
	/**
	 *  Valida los porcentaje de comisiones de la poliza con los de la base de datos
	 * @param ppcPol -> porcentajes de comisiones de la poliza
	 * @param ppc -> porcentajes de comisiones de la bbdd
	 * @param errores -> mapa de errores
	 * @param Usuario -> usuario
	 */
	private Map<String, Object> validarPolizaPctComisiones(PolizaPctComisiones ppcPol, PolizaPctComisiones ppc, Usuario us){
		// revisamos % Comision maxima, % Entidad y/o % E-S Mediadora y los comparamos con los de la poliza
		Map<String, Object> errores= new HashMap<String, Object>();
		if ((ppcPol.getPctentidad().compareTo(ppc.getPctentidad())!=0) || (ppcPol.getPctesmediadora().compareTo(ppc.getPctesmediadora())!=0 )){
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.comisiones.poliza.definitiva.parametros.KO"));
			return errores;
		}
		// revisamos % Administracion y % Adquisicion y los comparamos con los de la poliza
		if ((ppcPol.getPctadministracion().compareTo(ppc.getPctadministracion())!=0) || (ppcPol.getPctadquisicion().compareTo(ppc.getPctadquisicion())!=0)){
			errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.comisiones.poliza.definitiva.datosMantenimiento.KO"));
			return errores;
		}
		
		// revisamos que el max. descuento permitido de la poliza coincide con del mantenimiento de Descuentos, 
		// y que el descuento elegido esta dentro del rango permitido
		// Para usuarios de perfil 0, 1 interno y 5 no se realiza la comprobacion
		if (ppcPol.getPctdescelegido() != null && ppcPol.getPctdescmax() != null){
			if (!us.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR) && !us.getPerfil().equals(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR) &&
					!(us.getPerfil().equals(Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES) && us.getExterno().compareTo(Constants.USUARIO_INTERNO)==0)
					)
			{
				if (ppcPol.getPctdescmax().compareTo(ppc.getPctdescmax())!= 0)
				{
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.comisiones.poliza.definitiva.descuentos.rango.poliza.KO"));
					return errores;
				}
				if (ppcPol.getPctdescelegido().compareTo(ppcPol.getPctdescmax())== 1)
					errores.put(PasarADefinitivaPlzManager.ALERTA, bundle.getString("mensaje.comisiones.poliza.definitiva.descuentos.rango.KO"));
			}
		}
		return errores;
	}
	
	
	
	
	/**
	 * Comprueba si la poliza complementaria tiene distribucion de costes
	 * @param polizaCpl
	 * @return
	 */
	public boolean checkDistribucionCosteSubvCpl (Poliza polizaCpl) {
		//TMR 02/04/2014
		log("checkDistribucionCosteSubvCpl", "Comprueba si la poliza complementaria tiene distribucion de costes");
		return polizaManager.checkDistribucionCosteSubvCpl(polizaCpl, polizaCpl.getPolizaPpal());
	}
	
	/**
	 * Comprueba si la poliza tiene errores en el acuse de recibo
	 * @param p
	 * @param pasoADefinitivaForzado
	 * @return
	 */
	private boolean erroresAcuseRecibo (Poliza p, final Map<String, Object> errores, boolean esCpl, Map<String, Object> parametros, Usuario usuario) {
		
		Map<String, String> cabeceraComparativaHTML = new LinkedHashMap<String, String>();
		ResultadoWS resultadoWS = null;
		Map<ComparativaPolizaId, ResultadoWS> acusePolizaHolder = new LinkedHashMap<ComparativaPolizaId, ResultadoWS>();
		
		// MPM - 20121212		
		// Se llama al servicio de validacion para comprobar los errores del acuse de recibo
		
		// Obtiene la comparativa de la poliza si es principal
		ComparativaPoliza cp = null;
		for (ComparativaPoliza comparativaPoliza : p.getComparativaPolizas()) {
			cp = comparativaPoliza;
			break;
		}	
		
		// Obtiene el registro de la tabla de parametros que indica el servicio al que se va a llamar
		Parametro parametro = parametrizacionManager.getParametro();
		
		// Inserta en la tabla de envios a Agroseguro el xml generado correspondiente a la poliza y retorna el idEnvio
		Long idEnvio = null;
		
		try {
			Map<Character, ComsPctCalculado> comsPctCalculado;
			//DAA 19/06/13 WS_PASAR_DEFINITIVA
			if (esCpl) {
				ModuloPoliza mp = null;
				Set<ModuloPoliza> mpLst = p.getModuloPolizas();
				for (ModuloPoliza aux : mpLst) {
					mp = aux;
					break;
				}
				comsPctCalculado = this.webServicesManager.getComsPctCalculadoComp(mp.getId().getNumComparativa());
				idEnvio = webServicesCplManager.generateAndSaveXMLPolizaCpl(p, Constants.WS_PASAR_DEFINITIVA, comsPctCalculado, null);
			} else {
				comsPctCalculado = this.webServicesManager.getComsPctCalculadoComp(cp.getId().getIdComparativa());
				idEnvio = webServicesManager.generateAndSaveXMLPoliza(p, cp, Constants.WS_PASAR_DEFINITIVA, true, comsPctCalculado);
			}								 
		}
		catch (Exception e) {
			logger.error("Error al generar el xml de la poliza e insertar en la tabla de envios", e);
		}
		
		// Llama al servicio de validacion, guarda la respuesta en registro de envios creado en el paso anterior
		// y devuelve el acuse de recibo
		AcuseRecibo acuseRecibo = null;
		try {
			if (idEnvio != null)
				acuseRecibo = (!esCpl) ? webServicesManager.validar(idEnvio, p.getIdpoliza(), (String) parametros.get(PasarADefinitivaPlzManager.REAL_PATH), parametro.getValidacion(), cp) :
										 webServicesCplManager.validarCpl(idEnvio, p.getIdpoliza(), (String) parametros.get(PasarADefinitivaPlzManager.REAL_PATH), parametro.getValidacion());			
		}
		catch (DAOException e) {
			logger.error("Error al validar la poliza", e);
		}
		catch (BusinessException e) {
			logger.error("Error al validar la poliza", e);
		}
		catch (Exception e) {
			logger.error("Error inesperado al validar la poliza", e);
		}
		
		// Si el acuse de recibo se ha recibido correctamente se comprueba si tiene errores
		if (acuseRecibo!= null){
			
			// Solo si la poliza es principal
			if (!esCpl) {
				String cpid = "" + cp.getId().getIdpoliza() + "" + cp.getId().getLineaseguroid() + ""
						+ cp.getId().getCodmodulo() + "" + cp.getId().getIdComparativa();
				resultadoWS = new ResultadoWS();
				resultadoWS.setAcuseRecibo(acuseRecibo);
				acusePolizaHolder.put(cp.getId(), resultadoWS);
				
				// Miramos los errores de validacion
				// UNIFICACION VALIDACION/CALCULO/PASO DEFINTIVA PARA MOSTRAR LAS COMPARATIVAS
				VistaImportes vistaImportes = webServicesManager.setDatosComparativa(cp, p.getLinea().isLineaGanado());
				webServicesManager.generarComparativa(idEnvio, p, cp, vistaImportes,(String) parametros.get(PasarADefinitivaPlzManager.REAL_PATH), usuario);

				cabeceraComparativaHTML.put(cpid, vistaImportes.getComparativaCompleta());
				cabeceraComparativaHTML.put(cpid + "modulo", vistaImportes.getIdModulo());
				cabeceraComparativaHTML.put(cpid + "descModulo", vistaImportes.getDescModulo());
			}
			
			BigDecimal codLinea = p.getLinea().getCodlinea();
			BigDecimal codPlan = p.getLinea().getCodplan();
			BigDecimal codEntidad = p.getAsegurado().getEntidad().getCodentidad();			
			WSUtils.limpiaErroresWs(acuseRecibo, Constants.WS_PASAR_DEFINITIVA, parametro, this.polizaDao, codPlan, codLinea, codEntidad, Constants.PASAR_DEFINITIVA);
			
			// Si tiene errores
			if (acuseRecibo.getDocumentoArray().length > 0 && acuseRecibo.getDocumentoArray(0).getErrorArray().length > 0) {								
				// Se indica en el objeto de errores que ha habido error en la validacion del acuse de recibo para que el 
				// controller realice la redireccion correspondiente dependiendo si la poliza es principal o complementaria
				
				if (!esCpl) { // Poliza principal
					errores.put("erroresAcuseRecibo", true);
					errores.put(PasarADefinitivaPlzManager.RESULTADO, acusePolizaHolder);
					errores.put("cabeceras", cabeceraComparativaHTML);
					errores.put("idpoliza",  p.getIdpoliza());
					errores.put(PasarADefinitivaPlzManager.ACUSE_PPAL, acuseRecibo);
				}
				else { // Poliza complementaria
					errores.put("erroresAcuseReciboCpl", true);
					errores.put(PasarADefinitivaPlzManager.RESULTADO, acuseRecibo);
				}
				
				return true;
			}
			
			
			// La poliza no tiene errores
			return false;
			
		}
		
		// Si llega hasta aqui ha habido errores en la validacion de la poliza
		return true;
	}
	
	/**
	 * Mejora 109. Angel .22/02/2012. Validar incompatibilidad por clase antes de pasar a definitiva
	 * Comprueba la incompatibilidad de clases
	 * @param p Poliza sobre la que se realiza la validacion
	 * @return
	 */
	private boolean isCompatible (Poliza p) {
		
		try {
			return incompatibilidadClaseManager.isCompatible(p);
		} catch (BusinessException e) {
			logger.error(e);			
		}
		
		// Si llega hasta aqui ha habido algun error en la comprobacion
		return false;
		
	}
	
	/**
	 * Mejora 120 . tamara 12/03/2012
	 * Comprueba si existen polizas con el mismo lineaseguroid, nif de asegurado y clase que la poliza pasada como parametro
	 * @param p Poliza sobre la que se realiza la validacion
	 * @return Si el numero de polizas con los mismos datos indicados es mayor que el numero maximo de polizas permitida por la clase
	 * del usuario propietario de la poliza.
	 */
	private boolean existePolizaMismosDatos (Poliza p, Clase c) {
		
		// Carga el objeto de busqueda con los datos de la poliza pasada como parametro
		Poliza polizaOtroColectivo = new Poliza();
		polizaOtroColectivo.setAsegurado(p.getAsegurado());
		polizaOtroColectivo.setColectivo(null);
		polizaOtroColectivo.setClase(p.getClase());
		polizaOtroColectivo.setLinea(p.getLinea());
		polizaOtroColectivo.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
		List<Poliza> lstPolizasOtroColectivo = new ArrayList<Poliza>();
		
		// Estados de poliza que se excluyen de la busqueda
		BigDecimal estadosPolizaNoIncluir [] = new BigDecimal [4];
		estadosPolizaNoIncluir[0]=  Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION; //pte. validacion
		estadosPolizaNoIncluir[1]= Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL; //Grb. provisional
		estadosPolizaNoIncluir[2]= Constants.ESTADO_POLIZA_ANULADA; //anulada
		estadosPolizaNoIncluir[3]= Constants.ESTADO_POLIZA_BAJA;// baja 
		
		// Colectivos que se excluyen de la busqueda de polizas - Se excluye el colectivo al que pertenece la poliza
		String colectivoPolizaNoIncluir [] = new String [1];
		colectivoPolizaNoIncluir[0]=  p.getColectivo().getIdcolectivo();
		
		// Llama al metodo que devuelve las polizas que se ajustan al filtro de busqueda
		lstPolizasOtroColectivo = getPolizasButEstados(polizaOtroColectivo, estadosPolizaNoIncluir, colectivoPolizaNoIncluir);
		
		// Devuelve si el numero de polizas obtenidas es mayora que el maximo de polizas permitido por la clase del usuario propietario
		return (lstPolizasOtroColectivo.size()> c.getMaxpolizas().intValue());								
		
	}
	
	/**
	 * Devuelve el listado de polizas que se ajustan al filtro de busqueda excluyendo las polizas que esten en algun estado de los
	 * indicados en 'estadosPolizaNoIncluir' o pertenezcan a algun colectivo indicado en 'colectivosPolizaNoIncluir'
	 * @param poliza
	 * @param estadosPolizaNoIncluir
	 * @param colectivosPolizaNoIncluir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Poliza> getPolizasButEstados(final Poliza poliza, BigDecimal estadosPolizaNoIncluir[], String colectivosPolizaNoIncluir[]) {
		final SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		filter.setEstadosPolizaNoIncluir(estadosPolizaNoIncluir);
		filter.setColectivosNoIncluir(colectivosPolizaNoIncluir);
		return seleccionPolizaDao.getObjects(filter);
	}
	
	@Override 
	public boolean muestraBotonPasoDef(Long idPoliza,AcuseRecibo acuseRecibo,Usuario usuario){
		boolean res = Boolean.FALSE;
		// P0021873 2.6 CODIGO OBSOLETO
		//List<String> errores = new ArrayList<String>();
		// Si el usuario tiene perfil 0 puede forzar el paso a definitiva
		if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)) {
			
			res = Boolean.TRUE;
			
		} else {
			
			Poliza p = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
			
			if (acuseRecibo!= null){
				
				// P0021873 2.6 NUEVA FUNCIONALIDAD
				BigDecimal tipoUsuario = usuario.getTipousuario();
				tipoUsuario = usuario.getExterno().equals(Constants.USUARIO_EXTERNO) ? tipoUsuario.add(Constants.NUMERO_DIEZ) : tipoUsuario;
				List<ErrorWsAccion> erroresWssAccionList = this.webServicesManager
						.getErroresWsAccion(p.getLinea().getCodplan(), p.getLinea()
								.getCodlinea(), p.getColectivo()
								.getSubentidadMediadora().getEntidad()
								.getCodentidad(), tipoUsuario, Constants.PASAR_DEFINITIVA);
				res = WSUtils.comprobarErroresPorPerfil(acuseRecibo, erroresWssAccionList);
			}
		}
		return res;
	}
	
	/**
	 * Devuelve un booleano indicando si la poliza principal asociada a la complementaria pasada como parametro esta en estado
	 * 'Enviada correcta'
	 * @param pCpl
	 * @return
	 */
	private boolean principalContratada (Poliza pCpl) {
		
		try {
			return (Constants.ESTADO_POLIZA_DEFINITIVA.equals(pCpl.getPolizaPpal().getEstadoPoliza().getIdestado()));
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al comprobar el estado de la principal asociada a la complementaria con id " + pCpl.getIdpoliza(), e);
		}
		
		return false;
	}
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("PasarADefinitivaPlzManager." + method + " - " + msg);
	}

	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setIncompatibilidadClaseManager(
			IncompatibilidadClaseManager incompatibilidadClaseManager) {
		this.incompatibilidadClaseManager = incompatibilidadClaseManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setSimulacionSbpManager(SimulacionSbpManager simulacionSbpManager) {
		this.simulacionSbpManager = simulacionSbpManager;
	}

	public void setCaracteristicaExplotacionManager(
			CaracteristicaExplotacionManager caracteristicaExplotacionManager) {
		this.caracteristicaExplotacionManager = caracteristicaExplotacionManager;
	}

	public void setComparativaManager(ComparativaManager comparativaManager) {
		this.comparativaManager = comparativaManager;
	}


	public void setReferenciaDao(IReferenciaDao referenciaDao) {
		this.referenciaDao = referenciaDao;
	}


	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}


	public void setModuloPolizaDao(IModuloPolizaDao moduloPolizaDao) {
		this.moduloPolizaDao = moduloPolizaDao;
	}

	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}

	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}

	public void setParametrizacionManager(
			ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}

	public void setErrorWsAccionDao(IErrorWsAccionDao errorWsAccionDao) {
		this.errorWsAccionDao = errorWsAccionDao;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setWebServicesCplManager(WebServicesCplManager webServicesCplManager) {
		this.webServicesCplManager = webServicesCplManager;
	}

	public void setPolizaComplementariaDao(
			IPolizaComplementariaDao polizaComplementariaDao) {
		this.polizaComplementariaDao = polizaComplementariaDao;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setPolizasPctComisionesDao(
			IPolizasPctComisionesDao polizasPctComisionesDao) {
		this.polizasPctComisionesDao = polizasPctComisionesDao;
	}

	public void setPagoPolizaDao(IPagoPolizaDao pagoPolizaDao) {
		this.pagoPolizaDao = pagoPolizaDao;
	}

	public void setEleccionFormaPagoManager(
			EleccionFormaPagoManager eleccionFormaPagoManager) {
		this.eleccionFormaPagoManager = eleccionFormaPagoManager;
	}

	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}
}
