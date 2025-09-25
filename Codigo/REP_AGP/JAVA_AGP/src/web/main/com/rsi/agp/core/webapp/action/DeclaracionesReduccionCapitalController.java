package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CargaPolizaActualizadaDelCuponException;
import com.rsi.agp.core.exception.CargaPolizaFromCopyOrPolizaException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.manager.impl.anexoRC.IAnexoReduccionCapitalManager;
import com.rsi.agp.core.manager.impl.anexoRC.confirmacion.IConfirmacionRCManager;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.ISolicitudReduccionCapManager;
import com.rsi.agp.core.managers.impl.DeclaracionesReduccionCapitalManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.CuponPrevio;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.CuponPrevioRC;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;

public class DeclaracionesReduccionCapitalController extends BaseMultiActionController {

	private static final String REDUCCION_CAPITAL = "reduccionCapital";
	private static final String MENSAJE_ERROR_GENERAL = "mensaje.error.general";
	private static final String MENSAJE = "mensaje";
	private static final String REDUCCION_CAPITAL_BEAN = "reduccionCapitalBean";
	private static final String MENSAJE_ALTA_KO = "mensaje.alta.KO";
	private static final String VIENE_DE_LISTADO_RED_CAP = "vieneDeListadoRedCap";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ALERTA = "alerta";
	private static final String MODO_LECTURA = "modoLectura";
	//P0079361
	private static final String S = "S";
	private IAnexoReduccionCapitalManager anexoReduccionCapitalManager;
	private static final String WEB_INF = "/WEB-INF/";
	private IConfirmacionRCManager confirmacionRCManager;
	private static final String MSJ = "mensaje";
	private static final String ERRORES = "errores";
	//P0079361
	
	private static final Log logger = LogFactory.getLog(DeclaracionesReduccionCapitalController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager;
	private ParcelasReduccionCapitalController parcelasReduccionCapitalController;
	private String origen = "";
	private final static String VACIO = "";
	
	private ISolicitudReduccionCapManager solicitudReduccionCapManager;
	
	protected void initBinder(HttpServletRequest request,ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	 /**
     * Metodo para comprobar si el plan/linea del anexo que intentamos dar de alta es el ultimo activo o no para
     * solicitar confirmacion al usuario.
     * @param request
     * @param response
     * @param anexoModificacion
     * @return
     * @throws Exception
     */
	public ModelAndView doComprobarAlta(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		
		Long idPoliza = null;
		Poliza poliza = null;
		
		if (reduccionCapital == null) {
			idPoliza = new Long(request.getParameter(ID_POLIZA));			
		} else {		
			if (reduccionCapital.getPoliza() != null && reduccionCapital.getPoliza().getIdpoliza() != null) {
				idPoliza = reduccionCapital.getPoliza().getIdpoliza();	
			} else {
				idPoliza = new Long(request.getParameter(ID_POLIZA));				
			}					
		}
		
		poliza = declaracionesReduccionCapitalManager.getPoliza(idPoliza);
		
		Character c = poliza.getTieneanexorc();
		JSONObject objeto = new JSONObject();
		
		if (c == 'S') {
			Set<ReduccionCapital> anexosRC = poliza.getAnexoRCs();
			
			for (ReduccionCapital rc : anexosRC) {
				if (rc.getCupon() != null && !(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals( rc.getCupon().getEstadoCupon().getId()) || 
						Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals( rc.getCupon().getEstadoCupon().getId()))) {
					
					objeto.put("objeto", "tieneAnexo");
					getWriterJSON(response, objeto);
					return null;
				}
			}
			
		}
		
		objeto.put("objeto", "noTieneAnexo");
		getWriterJSON(response, objeto);
		return null;
	}
	
	/**
	 * Metodo para comprobar que el am por cupon caducado indicado por el id de anexo y poliza pasados en la request 
	 * es editable solicitando un nuevo cupon o no
	 * @param request
	 * @param response
	 */
	public void isEditableRCCuponCaducado(HttpServletRequest request, HttpServletResponse response) {
		
		// Obtiene los id de anexo y poliza
		Long idAnexo = NumberUtils.parseLong(request.getParameter("idAnexo"));
		Long idPoliza = NumberUtils.parseLong(request.getParameter(ID_POLIZA));
		BigDecimal resultado = new BigDecimal (-1);
		
		// Si se han obtenido correctamente los ids se comprueba si el anexo es editable
		if (idAnexo != null && idPoliza != null) {
			resultado = declaracionesReduccionCapitalManager.isEditableRCCuponCaducado(idPoliza, idAnexo);
		}
		
		// Se escribe el resultado en la respuesta
		JSONObject objeto = new JSONObject();
		putJSON(objeto, "isEditableRCCuponCaducado", resultado.toString());
		getWriterJSON(response, objeto);
		
	}

	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{

		logger.debug("DeclaracionesRCController - INIT");
		//Long idPoliza = null;
		Poliza poliza = null;
		String origen = null;	
		
		String idAnexo  = StringUtils.nullToString(request.getParameter("id"));
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String idCupon = StringUtils.nullToString(request.getParameter("idCupon"));
		logger.debug("ANEXO: " + idAnexo);
		logger.debug("POLIZA: " + idPoliza);
		logger.debug("CUPON: " + idCupon);
		
		if (idPoliza.isEmpty()) idPoliza = StringUtils.nullToString(request.getParameter("idPoliza2"));
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		
		if (idAnexo == null || idAnexo.isEmpty()) {
			idCupon = "";
		}
		
		logger.debug("IDANEXO: " + idAnexo);
		logger.debug("IDPOLIZA: " + idPoliza);
		logger.debug("IDCUPON: " + idCupon);
		
		List<Object[]> listaRiesgos = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Estado estado = new Estado(Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR);
		
		
		
		boolean modoLectura = (FiltroUtils.noEstaVacio(request.getParameter(MODO_LECTURA))) &&
						  "true".equals(request.getParameter(MODO_LECTURA)) ? true : false;
		
		
		String mensajeAlerta = StringUtils.nullToString(request.getParameter(ALERTA));
		if (!mensajeAlerta.equals("")) {
			parametros.put(ALERTA, mensajeAlerta);
		}
		
		/*if (reduccionCapital == null) {
			idPoliza = new Long(request.getParameter(ID_POLIZA));			
		} else {		
			if (reduccionCapital.getPoliza() != null && reduccionCapital.getPoliza().getIdpoliza() != null) {
				idPoliza = reduccionCapital.getPoliza().getIdpoliza();	
			} else {
				idPoliza = new Long(request.getParameter(ID_POLIZA));				
			}					
		}*/
		
		boolean isCaducado = false;
		
		
		try {
			//Se obtiene la poliza 
			System.out.println(reduccionCapital.getCupon().getIdcupon() + "-" + reduccionCapital.getCupon().getId());
			poliza = declaracionesReduccionCapitalManager.getPoliza(Long.parseLong(idPoliza));
			parametros.put("poliza", poliza);
			
			if (!idCupon.isEmpty()) {
				
				CuponRC c = declaracionesReduccionCapitalManager.devuelveNuevoCupon(idCupon);
				reduccionCapital.setCupon(c);
				//reduccionCapital.setCupon(declaracionesReduccionCapitalManager.devuelveNuevoCupon(idCupon));
			}
			
			if((idAnexo != null && !"".equals(idAnexo)) && declaracionesReduccionCapitalManager.buscarReduccionCapital(Long.parseLong(idAnexo))!=null && declaracionesReduccionCapitalManager.buscarReduccionCapital(Long.parseLong(idAnexo)).getCupon()!=null &&declaracionesReduccionCapitalManager.buscarReduccionCapital(Long.parseLong(idAnexo)).getCupon().getId() != null){ // Edición
				
				logger.debug("ENTRA POR EDICIÓN");
				String nuevoCupon = "";
				Long idCuponNuevo = null;
				
				if (reduccionCapital.getCupon() != null) {
					nuevoCupon = reduccionCapital.getCupon().getIdcupon();
					idCuponNuevo = reduccionCapital.getCupon().getId();
				}
					
				
				reduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionCapital(Long.parseLong(idAnexo));				
				Short idEstado = reduccionCapital.getEstado().getIdestado();
				
				if(idEstado.equals(Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO) || 
						idEstado.equals(Constants.REDUCCION_CAPITAL_ESTADO_RECIBIDO_CORRECTO)) { 
					// si no es editable
					parametros.put(MODO_LECTURA, "true");
				}
				else{
					// Editable
					parametros.put(MODO_LECTURA, "false");
					
					// Se comprueba si se esta haciendo una renovacion de un cupon caducado o una edicion normal
					boolean anexoCaducado = false;
					
					if (reduccionCapital.getCupon() != null)
						anexoCaducado = Constants.AM_CUPON_ESTADO_CADUCADO.equals(reduccionCapital.getCupon().getEstadoCupon().getId());
					else {
						anexoCaducado = true;
					}
			
					//String idAnexoCaducado  = StringUtils.nullToString(request.getParameter("idAnexoCaducado"));
					
					// Renovacion de cupon
					if(anexoCaducado) {
						// Se elimina el registro del cupon pedido para evitar duplicados, ya que se va a actualizar el cupon antiguo
						logger.debug("NUEVO CUPON: " + nuevoCupon);
						if (nuevoCupon != null && !nuevoCupon.isEmpty())
							declaracionesReduccionCapitalManager.borrarCupon(nuevoCupon);
						
						// Actualiza los antiguos datos del cupon con el nuevo
						CuponRC cupon = new CuponRC();
						
						if (reduccionCapital.getCupon() != null) 
							cupon = reduccionCapital.getCupon();
						
						if (cupon.getId() == null) {
							cupon.setId(idCuponNuevo);
						}
						
						if (cupon.getReferencia() == null) {
							cupon.setReferencia(poliza.getReferencia());
						}
						
						cupon.setIdcupon(nuevoCupon);
						cupon.setFecha(new Date());
						
						EstadoCuponRC ec = new EstadoCuponRC();
						ec.setId(Constants.AM_CUPON_ESTADO_ABIERTO);
						cupon.setEstadoCupon(ec);
						
						reduccionCapital.setCupon(cupon);
					}
					// Edicion normal
					else if (reduccionCapital.getCupon() != null && reduccionCapital.getCupon().getId() != null){
						// Si el anexo es de SW, no se hace el cambio de estado 
						// se comprueba si esta caducado, si es asi se cambia el estado a caducado
						estado = null;
						isCaducado = declaracionesReduccionCapitalManager.isAnexoCaducado(reduccionCapital);
						if (isCaducado) {							
							reduccionCapital.getCupon().setEstadoCupon(declaracionesReduccionCapitalManager.getEstadoCupon());
						}
					}
					
					declaracionesReduccionCapitalManager.guardarReduccionCapital(reduccionCapital, estado, usuario);
					
					
					//anexoModificacion = declaracionesModificacionPolizaManager.saveAnexoModificacion(anexoModificacion,usuario.getCodusuario(),estado,false);
				}
				//P0079361
		    	listaRiesgos = anexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC(realPath);
				parametros.put("listaRiesgos", listaRiesgos);	
				//P0079361
			} else { // Alta
				logger.debug("ENTRA POR ALTA.");
				reduccionCapital.getPoliza().setIdpoliza(new Long(idPoliza));
				
				reduccionCapital.setFechaAlta(new Date());
				reduccionCapital.setUsuarioAlta(usuario.getCodusuario());
				
				logger.debug("CUPON: " + reduccionCapital.getCupon().getId() + " - " + reduccionCapital.getCupon().getIdcupon());
				 
				if(!modoLectura) {
					declaracionesReduccionCapitalManager.altaAnexoReduccionCap(reduccionCapital, realPath, usuario, estado, true);
					
					//Cuando damos de alta un anexo rellenamos el campo tieneanexorc a 'S'
					poliza.setTieneanexorc('S');
					declaracionesReduccionCapitalManager.updateTieneReduccionCapPoliza(poliza);
				}
				//P0079361
				//listaRiesgos = declaracionesReduccionCapitalManager.getRiesgos(poliza);
		    	listaRiesgos = anexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC(realPath);
				parametros.put("listaRiesgos", listaRiesgos);	
				//P0079361
			}
			
			
			
			//Lista con los Riesgos para la lista desplegable de la pantalla
			
			
			
			String vieneDeListadoRedCap = request.getParameter(VIENE_DE_LISTADO_RED_CAP);
			parametros.put(VIENE_DE_LISTADO_RED_CAP, vieneDeListadoRedCap);
			
			// Si no se ha incluido ya el parametro 'modoLectura' 
			if (!parametros.containsKey(MODO_LECTURA)) {
				parametros.put(MODO_LECTURA, modoLectura);
			}
		} catch (BusinessException be) {
			logger.error("Se ha producido un error", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ALTA_KO));
			throw new Exception();
		} catch (DAOException be) {
			logger.error("Se ha producido un error", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ALTA_KO));
			throw new Exception();
		} catch (Exception be) {
			logger.error("Se ha producido un error", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ALTA_KO));
			throw new Exception();
		}
		
		if (isCaducado) {
			parametros.put("idPoliza", idPoliza);
			request.setAttribute(MENSAJE, bundle.getString("mensaje.anexo.cupon.caducado"));
			return doConsulta(request, response, reduccionCapital).addAllObjects(parametros);
		}
				
		return new ModelAndView("/moduloUtilidades/reduccionCapital/datosReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
	} 
	
	/*public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String idRC  = StringUtils.nullToString(request.getParameter("id"));
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
		boolean isCaducado = false;
		try {
			// Tanto si edita como si es un alta el estado es provisional
			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
			// Editar
			if(idAnexo != null && !"".equals(idAnexo)){
				// -- TIENE IDANEXO -- Edicion
				// Almacena el nuevo cupon solicitado por si se esta haciendo una renovacion de un cupon caducado
				/*String nuevoCupon = anexoModificacion.getCupon().getIdcupon();
				
				anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(Long.parseLong(idAnexo));				
				BigDecimal idEstado = anexoModificacion.getEstado().getIdestado();
				if(idEstado.equals(Constants.ANEXO_MODIF_ESTADO_ENVIADO) || 
						idEstado.equals(Constants.ANEXO_MODIF_ESTADO_CORRECTO)) { 
					// si no es editable
					parametros.put(MODO_LECTURA, "true");
				}
				else{
					// Editable
					parametros.put(MODO_LECTURA, "false");
					
					// Se comprueba si se esta haciendo una renovacion de un cupon caducado o una edicion normal
					String idAnexoCaducado  = StringUtils.nullToString(request.getParameter("idAnexoCaducado"));
					
					// Renovacion de cupon
					if(!"".equals(idAnexoCaducado)) {
						// Se elimina el registro del cupon pedido para evitar duplicados, ya que se va a actualizar el cupon antiguo
						declaracionesModificacionPolizaManager.borrarCupon(nuevoCupon);
						
						// Actualiza los antiguos datos del cupon con el nuevo
						Cupon cupon = anexoModificacion.getCupon();
						cupon.setIdcupon(nuevoCupon);
						cupon.setFecha(new Date());
						
						EstadoCupon ec = new EstadoCupon();
						ec.setId(Constants.AM_CUPON_ESTADO_ABIERTO);
						cupon.setEstadoCupon(ec);
						
						anexoModificacion.setCupon(cupon);
					}
					// Edicion normal
					else if (anexoModificacion.getCupon() != null && anexoModificacion.getCupon().getId() != null){
						// Si el anexo es de SW, no se hace el cambio de estado 
						// se comprueba si esta caducado, si es asi se cambia el estado a caducado
						estado = null;
						isCaducado = declaracionesModificacionPolizaManager.isAnexoCaducado(anexoModificacion);
						if (isCaducado) {							
							anexoModificacion.getCupon().setEstadoCupon(declaracionesModificacionPolizaManager.getEstadoCupon());
						}
					}
					
					anexoModificacion = declaracionesModificacionPolizaManager.saveAnexoModificacion(anexoModificacion,usuario.getCodusuario(),estado,false);
				}* /
			} else { 
				// -- NO TIENE IDANEXO -- Alta
				reduccionCapital.getPoliza().setIdpoliza(new Long(idPoliza));
				
				

				reduccionCapital.setFechaAlta(new Date());
				reduccionCapital.setUsuarioAlta(usuario.getCodusuario());
				
				//Guardo el anexo
				reduccionCapital = declaracionesModificacionPolizaManager.altaAnexoModificacion(anexoModificacion, 
						realPath, usuario.getCodusuario(), estado, true);
				
				if(anexoModificacion.getId() ==  null) {
					// La poliza ya tiene un anexo en estado Borrador o Definitivo
					request.setAttribute(ALERTA,  bundle.getString("mensaje.modificacion.estado.borradorODefinitivo.KO"));
					return doConsulta(request, response, anexoModificacion).addAllObjects(parametros); 
				} else {
					idAnexo = anexoModificacion.getId().toString();
				}
			}
			String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
			parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			parametros.put(ID_POLIZA, idPoliza);
			
			//return new ModelAndView(new RedirectView("coberturasModificacionPoliza.html"),"anexoModificacion", anexoModificacion).addAllObjects(parametros); //al doEdita
			if (!isCaducado) {
				parametros.put(ID_ANEXO, idAnexo);
				if(null!=anexoModificacion.getPoliza() 
					&& null!=anexoModificacion.getPoliza().getTipoReferencia()
					&& !anexoModificacion.getPoliza().getTipoReferencia().toString().equals("C")) {
					/*** 12/01/21 PET.63485.FIII DNF coberturas, cambio el flujo de la pantalla* /
					//return this.coberturasModificacionPolizaController.doConsulta(request, response, anexoModificacion).addAllObjects(parametros);
					return this.coberturasModificacionPolizaController.doContinua(request, response, anexoModificacion).addAllObjects(parametros);
					/*** fin 12/01/21 PET.63485.FIII DNF coberturas, cambio el flujo de la pantalla* /
				}				
					
				if (null != anexoModificacion.getPoliza() && null != anexoModificacion.getPoliza().getTipoReferencia()
						&& anexoModificacion.getPoliza().getTipoReferencia().toString().equals("C")) {
					return new ModelAndView("redirect:/declaracionesModificacionPolizaComplementaria.html")
							.addAllObjects(parametros);
				}
			}else {
				// Se eliminar el cupon del anexo de modificacion para que no de problemas en el alta de AM por FTP al volver a la pantalla
				anexoModificacion.setCupon(null);
				request.setAttribute(MENSAJE, bundle.getString("mensaje.anexo.cupon.caducado"));
			}

		} catch (BusinessException be) {
			logger.error("Se ha producido un error en el alta del anexo..", be);
			parametros.put(ALERTA, bundle.getString("mensaje.alta.generico.KO"));
			// si no esta creado el anexo y tiene cupon, lo anulamos
			if((idAnexo == null || "".equals(idAnexo)) && anexoModificacion.getCupon() != null ){
				// anulamos cupon
				String msgAnulacionCupon = null;
				if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(anexoModificacion.getTipoEnvio()) &&
						!Constants.AM_CUPON_ESTADO_CADUCADO.equals(anexoModificacion.getCupon().getEstadoCupon().getId())) {
						msgAnulacionCupon = solicitudModificacionManager.anularCupon(anexoModificacion.getCupon().getId(),
																					 anexoModificacion.getCupon().getIdcupon(), 
																					 this.getServletContext().getRealPath(WEB_INF),
																					 usuario != null ? usuario.getCodusuario() : "");
				}
				parametros.put(MENSAJE, msgAnulacionCupon);
			}	
		} catch (CargaPolizaActualizadaDelCuponException e) {
			logger.error("Se ha producido un error al cargar los datos de la situaci&oacute;n actualizada del cupon", e);
			parametros.put(ALERTA, bundle.getString("mensaje.alta.KO.Carga.situacionActualizadaCupon.KO"));
			// si no esta creado el anexo y tiene cupon, lo anulamos
			if((idAnexo == null || "".equals(idAnexo)) && anexoModificacion.getCupon() != null ){
				// anulamos cupon
				String msgAnulacionCupon = null;
				if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(anexoModificacion.getTipoEnvio()) &&
						!Constants.AM_CUPON_ESTADO_CADUCADO.equals(anexoModificacion.getCupon().getEstadoCupon().getId())) {
						msgAnulacionCupon = solicitudModificacionManager.anularCupon(anexoModificacion.getCupon().getId(),
																					 anexoModificacion.getCupon().getIdcupon(), 
																					 this.getServletContext().getRealPath(WEB_INF),
																					 usuario != null ? usuario.getCodusuario() : "");
				}
				parametros.put(MENSAJE, msgAnulacionCupon);
			}	
		}catch (CargaPolizaFromCopyOrPolizaException e) { 
			logger.error("Se ha producido un error al cargar los datos de la copy de la poliza", e);
			parametros.put(ALERTA, bundle.getString("mensaje.alta.KO.Carga.CopyOrPoliza.KO"));	
		}catch (ValidacionAnexoModificacionException e) { 
			logger.error("Se ha producido un error al validar el xml del anexo de modificaci&oacute;n");
			parametros.put(ALERTA, bundle.getString("mensaje.alta.generico.KO")+ " " + e.getMessage());
			return doConsulta(request, response, anexoModificacion).addAllObjects(parametros);
		}
		
		return doConsulta(request, response, anexoModificacion).addAllObjects(parametros);
	}*/

	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{

		Long idReduccionCapital = reduccionCapital.getId();
		Map<String, Object> parametros = new HashMap<String, Object>();
		Poliza poliza = null;
		List<ReduccionCapital> listaReduccionCapital = null;
		String msgAnulacionCupon = null;
		
		reduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionCapital(idReduccionCapital);
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			if(declaracionesReduccionCapitalManager.tieneEstado(idReduccionCapital,Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR) ||
     		   declaracionesReduccionCapitalManager.tieneEstado(idReduccionCapital,Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO) ||
     		  declaracionesReduccionCapitalManager.tieneEstado(idReduccionCapital,Constants.REDUCCION_CAPITAL_ESTADO_DEFINITIVO)){
				
				System.out.println(reduccionCapital.getCupon());
				System.out.print(reduccionCapital.getCupon().getEstadoCupon().getId());
				
				
				if (reduccionCapital.getCupon() != null && !Constants.AM_CUPON_ESTADO_CADUCADO.equals(reduccionCapital.getCupon().getEstadoCupon().getId())) {
					msgAnulacionCupon = solicitudReduccionCapManager.anularCupon(reduccionCapital.getCupon().getId(),
							reduccionCapital.getCupon().getIdcupon(), this.getServletContext().getRealPath(WEB_INF),
																				 usuario != null ? usuario.getCodusuario() : "");
				}
				
                //Realizamos la baja de la reduccion capital
				declaracionesReduccionCapitalManager.eliminarReduccionCapital(reduccionCapital);
				//Comprobamos si en la poliza hay mas anexoModificacions, si no hay modificamos el campo tieneanexomp de polizas a N
				poliza = declaracionesReduccionCapitalManager.getPoliza(new Long(reduccionCapital.getPoliza().getIdpoliza()));
				listaReduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionCapitalPoliza(reduccionCapital);
				if (listaReduccionCapital.size() == 0 ){ // no tiene anexos, actualizamos la poliza
					poliza.setTieneanexorc(new Character('N'));
					declaracionesReduccionCapitalManager.updateTieneReduccionCapPoliza(poliza);
				}
				//Mensaje de baja correcta
				parametros.put(MENSAJE, bundle.getString("mensaje.baja.OK"));		
				origen = "doBaja";
			} else {
				//Mensaje de error: no se puede dar de baja un registro ya enviado a Agroseguro
				parametros.put(ALERTA, bundle.getString("mensaje.baja.enviado.KO"));	
			}	
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante el borrado de una reduccion de capital", be);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
			throw new Exception();
		} catch (Exception be) {
			logger.error("Se ha producido un error durante el borrado de una reduccion de capital", be);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
			throw new Exception();
		}
		String vieneDeListadoRedCap = request.getParameter(VIENE_DE_LISTADO_RED_CAP);
		if ("true".equals(vieneDeListadoRedCap)) {
			
			parametros.put("volver", true);
			return new ModelAndView("redirect:/utilidadesReduccionCapital.run").addAllObjects(parametros);
		}
		
		return doConsulta(request, response, reduccionCapital).addAllObjects(parametros); // enviar una reduccion de capital nueva con el idpoliza
	}
	/**
	 * Alta de una Reduccion de capital
	 * TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico (historicoEstadosManager.insertaEstado) 
	 * @author U029769 28/06/2013
	 * @param request
	 * @param response
	 * @param reduccionCapital
	 * @return ModelAndView
	 * @throws Exception
	 */
	public ModelAndView doGuarda(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();		
		ModelAndView mv = null;
		com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado ca = new com.rsi.agp.dao.tables.reduccionCap.CapitalAsegurado();
		
		Date fechaDanios = reduccionCapital.getFechadanios();
		String codigoRiesgo = reduccionCapital.getCodmotivoriesgo();
		String motivo = reduccionCapital.getMotivo();
		
		
		reduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionCapital(reduccionCapital.getId());
		
		reduccionCapital.setFechadanios(fechaDanios);
		reduccionCapital.setCodmotivoriesgo(codigoRiesgo);
		reduccionCapital.setMotivo(motivo);
		
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
			Poliza poliza = this.declaracionesReduccionCapitalManager.getPoliza(reduccionCapital.getPoliza().getIdpoliza());
			
			if(reduccionCapital.getEstado().getIdestado() != null && (
			   reduccionCapital.getEstado().getIdestado().intValue() == Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR.intValue() ||
			   reduccionCapital.getEstado().getIdestado().intValue() == Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO.intValue())){
				
				
				
				if (!StringUtils.nullToString(poliza.getReferencia()).equals("")){
				
					Estado estado = null;
					if(StringUtils.nullToString(reduccionCapital.getUsuarioAlta()).equals("") && 
							("").equals(StringUtils.nullToString(reduccionCapital.getFechaAlta()))){
						estado = new Estado(Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR);
					}
					
					//Realizamos el alta de la reduccion de capital
					declaracionesReduccionCapitalManager.guardarReduccionCapital(reduccionCapital, estado, usuario);
					//Cuando damos de alta un anexo rellenamos el campo tieneanexomp a 'S'
					poliza.setTieneanexorc('S');
					declaracionesReduccionCapitalManager.updateTieneReduccionCapPoliza(poliza);
				}
			}
			//DAA 31/08/2012
			String vieneDeListadoRedCap = request.getParameter(VIENE_DE_LISTADO_RED_CAP);
			parametros.put(VIENE_DE_LISTADO_RED_CAP, vieneDeListadoRedCap);
			
			parametros.put(ID_POLIZA, reduccionCapital.getPoliza().getIdpoliza());
			parametros.put("idReduccionCapital", reduccionCapital.getId());
			
			ca.getParcela().getReduccionCapital().getPoliza().setIdpoliza(poliza.getIdpoliza());
			ca.getParcela().getReduccionCapital().getPoliza().setReferencia(poliza.getReferencia());
			ca.getParcela().getReduccionCapital().getPoliza().setLinea(poliza.getLinea());
			ca.getParcela().getReduccionCapital().setId(reduccionCapital.getId());
			
			if (declaracionesReduccionCapitalManager.isRCconParcelas(reduccionCapital.getId())) {
				return parcelasReduccionCapitalController.doConsulta(request, response, ca).addAllObjects(parametros);
			}
			
			return parcelasReduccionCapitalController.doCargaParcelas(request, response,ca).addAllObjects(parametros);
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error durante el alta de una reduccion de capital", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ALTA_KO));
			
			mv = new ModelAndView("/moduloUtilidades/reduccionCapital/datosReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
		} catch (Exception be) {
			logger.error("Se ha producido un error durante el alta de una reduccion de capital", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ALTA_KO));
			
			mv = new ModelAndView("/moduloUtilidades/reduccionCapital/datosReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
		}
		
		return mv; //Enviar idPoliza 	
 	
	}
	
	/**
	 * Metodo de entrada por defecto a este controlador. Carga la informacion necesaria para mostrar la pantalla de Declaraciones de 
	 * Reducciones de Capital. Redirige a: 
	 * - el JSP de Declaraciones de Reduccion de Capital: si existe alguna Declaracion.
	 * - el controlador del alta de reducciones de capital (en esta misma clase): si no existe ninguna Declaracion de Reduccion de Capital.
	 * @param request Datos recibidos de la pantalla anterior
	 * @param response 
	 * @param reduccionCapital Bean con la informacion relativa a una Reduccion de Capital
	 * @return Una redireccion al siguiente paso del workflow, con toda la informacion que necesita.
	 * @throws Exception en caso de error
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception {

		List<ReduccionCapital> listaReduccionCapital = null;
		List<Riesgo> listaRiesgos = null;
		List<Estado> listaEstados = null;
		Poliza poliza = null;			
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView redireccion = null;			
		
		String mensajeAlerta = request.getParameter(ALERTA);
		if (!VACIO.equals(StringUtils.nullToString(mensajeAlerta))) {
			parametros.put(ALERTA, mensajeAlerta);
		}

		String idPoliza = request.getParameter(ID_POLIZA);
		
		if(idPoliza != null && !Constants.STR_EMPTY.equals(idPoliza)) {
			reduccionCapital.setPoliza(new Poliza());
			
			reduccionCapital.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
		} else {
			//Si no hay poliza informada en la request, es que se esta llamando a doConsulta desde
			//otro metodo de esta clase, luego la poliza ya esta informada en los parametros de entrada
			idPoliza = reduccionCapital.getPoliza().getIdpoliza().toString();
		}
		parametros.put(ID_POLIZA, idPoliza);
		
		try {
			//Se tratan de recuperar las reducciones de capital
			listaReduccionCapital = declaracionesReduccionCapitalManager.buscarReduccionesCapital(reduccionCapital);
			for (ReduccionCapital rcIter : listaReduccionCapital) {
				logger.debug("DeclaracionesReduccionCapitalController "+rcIter.toDebugString());
			}
			parametros.put("listaReduccionCapital", listaReduccionCapital);

		} catch(BusinessException be){
			logger.error("Se ha producido un error durante la consulta de Declaraciones de Reduccion de Capital ", be);
			parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));			
			throw new Exception();
		}

		if (reduccionCapital.getCupon() != null && reduccionCapital.getCupon().getCuponPrevio() == null){
			reduccionCapital.getCupon().setCuponPrevio(new CuponPrevioRC());
		}
		else if (reduccionCapital.getCupon() == null){
			reduccionCapital.setCupon(new CuponRC());
		}
			if (listaReduccionCapital != null && !listaReduccionCapital.isEmpty()) {
			//Existen reducciones de capital, luego se muestra la pantalla de consulta
		
			try{
				
				recuperarInformacionPantalla (reduccionCapital,idPoliza,poliza, 
						listaRiesgos,listaEstados,parametros);
				
				

				if (request.getParameter(MENSAJE) != null) {
					parametros.put(MENSAJE, request.getParameter(MENSAJE));
				}
				if (listaReduccionCapital.size() > 0){
					int countProvYDefinitiva = 0;
					for (ReduccionCapital rc: listaReduccionCapital){ 
						if (rc.getEstado().getIdestado().equals(Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR)
							|| rc.getEstado().getIdestado().equals(Constants.REDUCCION_CAPITAL_ESTADO_DEFINITIVO)){
							countProvYDefinitiva++;
							if (countProvYDefinitiva >0){
								parametros.put("altaKO", "true");
								break;
							}
						}
					}
				}
			}catch(BusinessException be){
				logger.error("Se ha producido un error durante la consulta de Declaraciones de Reduccion de  Capital", be);
				parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
				throw new Exception();
			}
		
			redireccion = new ModelAndView("/moduloUtilidades/reduccionCapital/declaracionesReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital)
							.addAllObjects(parametros);

		} else if ((listaReduccionCapital == null || (listaReduccionCapital != null && listaReduccionCapital.isEmpty())) && origen.equals("doBaja")) {
			//La poliza no tiene reducciones de capital porque se acaba de borrar la última
			
			try{									
				
				recuperarInformacionPantalla (
						reduccionCapital,
						idPoliza,
						poliza, 
						listaRiesgos, 
						listaEstados, 
						parametros);
						
				origen = "";
				
			}catch(BusinessException be){
				logger.error("Se ha producido un error despues del borrado de la última reduccion de capital", be);
				parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
				throw new Exception();
			}
			
			redireccion = new ModelAndView("/moduloUtilidades/reduccionCapital/declaracionesReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital)
							.addAllObjects(parametros);
			
		} else {					
			//No hay reducciones de capital, luego se reenvia directamente a la pantalla de alta	
			parametros.put("method", "doEdita");
			//P0079361
			try{									
				
				recuperarInformacionPantalla (
						reduccionCapital,
						idPoliza,
						poliza, 
						listaRiesgos, 
						listaEstados, 
						parametros);
						
				origen = "";
				
			}catch(BusinessException be){
				logger.error("Se ha producido un error despues del borrado de la última reduccion de capital", be);
				parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
				throw new Exception();
			}
			//redireccion = new ModelAndView("redirect:/declaracionesReduccionCapital.html", REDUCCION_CAPITAL, reduccionCapital).addAllObjects(parametros);
			redireccion = new ModelAndView("/moduloUtilidades/reduccionCapital/declaracionesReduccionCapital", REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);			
			//dudas en este cambio
			//P0079361
		} 	
		
		return redireccion;

	}
	
	public ModelAndView doPasarDefinitiva(HttpServletRequest request,HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		Long idReduccionCapital = reduccionCapital.getId();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		if(idReduccionCapital != null){ 
			//DAA 04/06/2013 Guardamos usuario y fecha de paso a def
			declaracionesReduccionCapitalManager.pasarDefinitiva(new Long(idReduccionCapital),usuario);
			parametros.put(MENSAJE, bundle.getString("mensaje.reduccionCapital.alta.definitiva.OK"));
		}else{
			parametros.put(MENSAJE, bundle.getString("mensaje.reduccionCapital.alta.definitiva.KO"));
		}
		
		String vieneDeListadoRedCap = request.getParameter(VIENE_DE_LISTADO_RED_CAP);
		if ("true".equals(vieneDeListadoRedCap)) {
			parametros.put("volver", true);
			return new ModelAndView("redirect:/utilidadesReduccionCapital.run").addAllObjects(parametros);
		}else{
			return doConsulta(request, response, reduccionCapital).addAllObjects(parametros);
		}
	}
	
	private void recuperarInformacionPantalla (
			ReduccionCapital reduccionCapital,
			String idPoliza,
			Poliza poliza, 
			List<Riesgo> listaRiesgos, 
			List<Estado> listaEstados, 
			Map<String, Object> parametros) throws BusinessException
	{
		Comunicaciones comunicaciones = null;
		
		try {
			//Se recupera la informacion de la poliza
			poliza = declaracionesReduccionCapitalManager.getPoliza(new Long(idPoliza));
			parametros.put("poliza", poliza);
	
			reduccionCapital.setPoliza(poliza);
						
			//Se obtienen las comunicaciones de la poliza
			if (poliza.getIdenvio() != null && (comunicaciones = declaracionesReduccionCapitalManager.getComunicaciones(poliza.getIdenvio())) != null) {								
				parametros.put("comunicaciones", comunicaciones);
			}				
					
			listaEstados = declaracionesReduccionCapitalManager.getEstadosReduccionCapital();
			parametros.put("listaEstados", listaEstados);
	
			//Lista con los Riesgos para la lista desplegable de la pantalla
			//P0079361 
			//falta cambiar para obtener del servicio de ayudaCausa?
			/*listaRiesgos = declaracionesReduccionCapitalManager.getRiesgos(poliza);
			parametros.put("listaRiesgos", listaRiesgos);	*/	
			String realPath = this.getServletContext().getRealPath(
					WEB_INF);
			listaRiesgos = new ArrayList<Riesgo>();
	    	
			try {
				listaRiesgos = anexoReduccionCapitalManager.obtenerAyudaCausaRC(realPath);
			} catch (Exception e) {
				logger.error("Error en cargarDatosFiltroBusqueda al cargar el listado de riesgos posibles", e);
			}
			
			//Array para las descripciones de los riesgos en el datagrid
//			JSONArray arrayDatos = new JSONArray();
//			JSONObject objeto = null;
//			
//			for(Object obj : listaRiesgos){
//				objeto = new JSONObject();
//				Object[] aux = (Object[]) obj;
//				objeto.put(aux[0].toString(),aux[1].toString());
//				arrayDatos.put(objeto);
//			}
			JSONArray jsonArray = new JSONArray();

	        for (Riesgo riesgo : listaRiesgos) {
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.put(riesgo.getId().getCodriesgo(), riesgo.getDesriesgo());
	            
	            jsonArray.put(jsonObject);
	        }
	        //P0079361
	        
			parametros.put("listaRiesgosDesc", jsonArray);
			
		} catch(BusinessException be) {
			throw new BusinessException();
		} catch (JSONException e) {
			logger.error("Se ha producido un error al generar el JSON con las descripciones de los riesgos", e);
		}
	
	}

	/**
	 * Metodo que gestiona la pulsacion del boton Volver en la pantalla de Datos de Reducciones de Capital, es decir, la primera pantalla 
	 * de Alta del proceso de Reduccion de Capital. Redirige a: 
	 * - al metodo por defecto del controlador de Reducciones de Capital: si existe alguna Reduccion de Capital para la poliza en cuestion
	 * - al listado de polizas por el que se entro al proceso de Reduccion de Capital. 
	 * @param request Datos recibidos de la pantalla anterior
	 * @param response 
	 * @param reduccionCapital Bean con la informacion relativa a una Reduccion de Capital
	 * @return Una redireccion al siguiente paso del workflow, con toda la informacion que necesita.
	 * @throws Exception en caso de error
	 */
	public ModelAndView doVolver(
			HttpServletRequest request, 
			HttpServletResponse response, 
			ReduccionCapital reduccionCapital) throws Exception {
				
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView redireccion = null;
		Long idPoliza = reduccionCapital.getPoliza().getIdpoliza();
		
		String vieneDeListadoRedCap = request.getParameter(VIENE_DE_LISTADO_RED_CAP);
		
		if("true".equals(vieneDeListadoRedCap)){
			parametros.put("volver", true);
			//vuelve al listado de red. Capital en el menu utilidades - redcapital.
			redireccion = new ModelAndView(new RedirectView("utilidadesReduccionCapital.run")).addAllObjects(parametros);

		}else{
		
			if (idPoliza != null) {
				
				try {
	
					if (declaracionesReduccionCapitalManager.tieneReduccionesCapital(idPoliza)) {
						//Vuelve a la pantalla de reducciones de capital
						redireccion = new ModelAndView("redirect:declaracionesReduccionCapital.html", REDUCCION_CAPITAL, reduccionCapital);
						
						
						parametros.put(ID_POLIZA, idPoliza);												
						
					} else {
						//Vuelve a la pantalla de polizas, puesto que no existia ninguna reduccion de capital
						parametros.put("recogerPolizaSesion", "true");
						redireccion = new ModelAndView("redirect:utilidadesPoliza.html");															
					}
					
				} catch(BusinessException be){
					logger.error("Se ha producido un error durante la consulta de Declaraciones de Reduccion de Capital", be);
					parametros.put(ALERTA, bundle.getString(MENSAJE_ERROR_GENERAL));
					throw new Exception();
				}
			}
		}
		
		return (redireccion != null) ? redireccion.addAllObjects(parametros) : new ModelAndView("redirect:utilidadesPoliza.html");
		
	}
	
	public ModelAndView doVerRecibo(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		logger.debug("init - doVerRecibo");
		Map<String, Object> parametros = new HashMap<String, Object>();
		es.agroseguro.acuseRecibo.Error[] errores = null;
		ReduccionCapital redCap = null;
		Long idRedCap = null;
		Long idPoliza = null;
		String refPoliza = null;
		BigDecimal linea = null;
		BigDecimal plan = null;
		BigDecimal idEnvio = null;
		ModelAndView mv = null;
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		try {
			 idRedCap = Long.valueOf((request.getParameter("id")));
			
			 logger.debug("idRedCap:  "+ idRedCap + " idPoliza: " + reduccionCapital.getPoliza().getIdpoliza());
			
			 redCap = declaracionesReduccionCapitalManager.buscarReduccionCapital(new Long (idRedCap));
				 
			 if (redCap != null) {
				 //no aplica
				/*if (redCap.getComunicaciones() != null && redCap.getComunicaciones().getIdenvio() != null) {
					idEnvio = redCap.getComunicaciones().getIdenvio();
				}*/

				idPoliza = redCap.getPoliza().getIdpoliza();
				refPoliza = redCap.getPoliza().getReferencia();
				logger.debug("idPoliza:  "+ idPoliza + " refPoliza: " + refPoliza + " idEnvio: "+ idEnvio );
				
				// AMG 31102012 Se indica a la jsp si viene del listado de Reduccion Capital de utilidades
				boolean fromUtilidades	= (FiltroUtils.noEstaVacio(request.getParameter("fromUtilidades"))) &&
				  						  "true".equals(request.getParameter("fromUtilidades")) ? true : false;
				parametros.put("fromUtilidades", fromUtilidades);
				// fin AMG 31102012
				
				//no aplicaria ??
				//idenvio no se informara al no trabajar con ftp y el fichero contenido sera inexistente ??
				
				if (//idEnvio == null || 
						refPoliza == null) {
					
				
					parametros.put(ID_POLIZA, idPoliza);
					parametros.put("errLength", 0);
					parametros.put("operacion", REDUCCION_CAPITAL);
					
					mv = new ModelAndView("/moduloUtilidades/erroresContratacion",REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
				
				} else {
					linea = redCap.getPoliza().getLinea().getCodlinea();
					plan = redCap.getPoliza().getLinea().getCodplan();
					logger.debug("plan:  "+ plan + " linea: " + linea );
					
					//Se debe cambiar esto por la parte de DeclaracionesReduccionCapitalManager.getFicheroContenido	para transformar el xml almacenado en el texto que muestra esta pantalla??
					//errores = 	declaracionesReduccionCapitalManager.getFicheroContenido(idEnvio,refPoliza, linea, plan);
					respuesta = confirmacionRCManager.getAcuseConfirmacion(idRedCap);
					//Se debe cambiar esto por la parte de DeclaracionesReduccionCapitalManager.getFicheroContenido	para transformar el xml almacenado en el texto que muestra esta pantalla??
					
					logger.debug("listado de errores - Size :  "+ respuesta.size() );
					
					if (respuesta.isEmpty()) {
						parametros.put(MSJ, "No se ha podido recuperar el acuse de recibo del anexo");
						parametros.put("errLength", 0);
						parametros.put(ID_POLIZA, idPoliza);
						parametros.put("operacion", REDUCCION_CAPITAL);
					} else {
						if (respuesta.containsKey(MSJ)) {
							parametros.put(MSJ, respuesta.get(MSJ));
							
							parametros.put(ID_POLIZA, idPoliza);
							parametros.put("operacion", REDUCCION_CAPITAL);
							
						} else if (respuesta.containsKey(ERRORES)) {
							parametros.put(ID_POLIZA, idPoliza);
							
							@SuppressWarnings("unchecked")
							List<Error> listaErrores = (List<Error>) respuesta.get(ERRORES);
							
							parametros.put(ERRORES, listaErrores);
							parametros.put("errLength", listaErrores.size());
							parametros.put("operacion", REDUCCION_CAPITAL);
						}
					}
					
					/*if (respuesta.size() == 0) {
							parametros.put("errLength", 0);
							parametros.put(ID_POLIZA, idPoliza);
							parametros.put("operacion", REDUCCION_CAPITAL);
					} else {
							parametros.put(ID_POLIZA, idPoliza);
							parametros.put("errores", errores);
							parametros.put("errLength", errores.length);
							parametros.put("operacion", REDUCCION_CAPITAL);
					}*/
			 
					mv = new ModelAndView("/moduloUtilidades/erroresContratacion",REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
		
				}
				
			}else {
				parametros.put(ID_POLIZA, reduccionCapital.getPoliza().getIdpoliza());
				mv = doConsulta(request, response, reduccionCapital).addAllObjects(parametros);
			} 
			
		}catch (BusinessException be) {
			logger.error("Se ha producido un error al recuperar los documentos de Acuse de Recibo: " + be.getMessage());
			parametros.put(ID_POLIZA, idPoliza);
			parametros.put(ALERTA, bundle.getString("mensaje.acuseRecibo.KO"));
			return new ModelAndView("/moduloUtilidades/erroresContratacion",REDUCCION_CAPITAL_BEAN, reduccionCapital).addAllObjects(parametros);
		
		}
				
		return mv;
		
	}

	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, ReduccionCapital reduccionCapital) throws Exception{
		
		return new ModelAndView("redirect:/informes.html").addObject("idReduccionCapital", reduccionCapital.getId()).addObject("method", "doInformeReduccionCapital");
	} 

	public void setDeclaracionesReduccionCapitalManager(DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager) {
		this.declaracionesReduccionCapitalManager = declaracionesReduccionCapitalManager;
	}

	public DeclaracionesReduccionCapitalManager getDeclaracionesReduccionCapitalManager() {
		return declaracionesReduccionCapitalManager;
	}

	public void setParcelasReduccionCapitalController(
			ParcelasReduccionCapitalController parcelasReduccionCapitalController) {
		this.parcelasReduccionCapitalController = parcelasReduccionCapitalController;
	}
	
	//P0079361
	public IAnexoReduccionCapitalManager getAnexoReduccionCapitalManager() {
		return anexoReduccionCapitalManager;
	}

	public void setAnexoReduccionCapitalManager(IAnexoReduccionCapitalManager anexoReduccionCapitalManager) {
		this.anexoReduccionCapitalManager = anexoReduccionCapitalManager;
	}

	public void setSolicitudReduccionCapManager(ISolicitudReduccionCapManager solicitudReduccionCapManager) {
		this.solicitudReduccionCapManager = solicitudReduccionCapManager;
	}
	
	public IConfirmacionRCManager getConfirmacionRCManager() {
		return confirmacionRCManager;
	}

	public void setConfirmacionRCManager(IConfirmacionRCManager confirmacionRCManager) {
		this.confirmacionRCManager = confirmacionRCManager;
	}
	//P0079361
}
