package com.rsi.agp.core.webapp.action;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.ISolicitudReduccionCapManager;
import com.rsi.agp.core.exception.CargaPolizaActualizadaDelCuponException;
import com.rsi.agp.core.exception.CargaPolizaFromCopyOrPolizaException;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.CuponPrevio;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class DeclaracionesModificacionPolizaController extends BaseMultiActionController {

	private static final String MODULO_UTILIDADES_ERRORES_CONTRATACION = "/moduloUtilidades/erroresContratacion";
	private static final String ERR_LENGTH = "errLength";
	private static final String OPERACION = "operacion";
	private static final String MODO_LECTURA = "modoLectura";
	private static final String ID_ANEXO = "idAnexo";
	private static final String ANEXO_MODIFICACION = "anexoModificacion";
	private static final String ID_POLIZA = "idPoliza";
	private static final String REDIRECT_ANEXO_MODIFICACION_UTILIDADES_RUN = "redirect:/anexoModificacionUtilidades.run";
	private static final String VOLVER = "volver";
	private static final String VIENE_DE_LISTADO_ANEXOS_MOD = "vieneDeListadoAnexosMod";
	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	private static final String WEB_INF = "/WEB-INF/";
	private static final String USUARIO = "usuario";
	private static final Log logger = LogFactory.getLog(DeclaracionesModificacionPolizaController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private ISolicitudModificacionManager solicitudModificacionManager;
	
	private CoberturasModificacionPolizaController coberturasModificacionPolizaController;
	
	/**
	 * BAJA
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {

		Map<String, Object> parametros = new HashMap<String, Object>();
		Poliza poliza = null;
		List<AnexoModificacion> listaAnexosMod = null;
		String msgAnulacionCupon = null;
		final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		
		try{
			anexoModificacion = declaracionesModificacionPolizaManager.getAnexoModifById(anexoModificacion.getId());
			
			// Eliminamos el anexo
			declaracionesModificacionPolizaManager.eliminarDeclaracionModificacionPoliza(anexoModificacion.getId());
			
			// Si el anexo se dio de alta por medio de SW y el cupon no esta caducado se anula
			if (Constants.ANEXO_MODIF_TIPO_ENVIO_SW.equals(anexoModificacion.getTipoEnvio()) &&
				!Constants.AM_CUPON_ESTADO_CADUCADO.equals(anexoModificacion.getCupon().getEstadoCupon().getId())) {
				msgAnulacionCupon = solicitudModificacionManager.anularCupon(anexoModificacion.getCupon().getId(),
																			 anexoModificacion.getCupon().getIdcupon(), 
																			 this.getServletContext().getRealPath(WEB_INF),
																			 usuario != null ? usuario.getCodusuario() : "");
			}
				
			//Comprobamos si en la poliza hay mas anexoModificacions, si no hay modificamos el campo tieneanexomp de polizas a N
			poliza = declaracionesModificacionPolizaManager.getPoliza(new Long(anexoModificacion.getPoliza().getIdpoliza()));
			listaAnexosMod = declaracionesModificacionPolizaManager.buscarAnexosPoliza(poliza.getIdpoliza());
			if (listaAnexosMod.size() == 0 ){ // no tiene anexos, actualizamos la poliza
				poliza.setTieneanexomp(new Character('N'));
				declaracionesModificacionPolizaManager.updateAnexoModificacionPoliza(poliza);
			}
			parametros.put(MENSAJE, bundle.getString("mensaje.baja.OK") + ((msgAnulacionCupon != null) ? ". " + msgAnulacionCupon : ""));
			
			// Se asigna un objeto Cupon nuevo para que no falle en el listado
			anexoModificacion.setCupon(new Cupon());
			
		}catch(BusinessException be){
			logger.error("Se ha producido un error durante el borrado de un siniestro", be);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
		}catch(Exception be){
			logger.error("Se ha producido un error inesperado durante el borrado de un siniestro", be);
			parametros.put(ALERTA, bundle.getString("mensaje.baja.KO"));
		}
		//TMR . Utilidades anexos de modificacion 30-08-2012
		String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
		if ("true".equals(vieneDeListadoAnexosMod)) {
			
			parametros.put(VOLVER, true);
			
			return new ModelAndView(REDIRECT_ANEXO_MODIFICACION_UTILIDADES_RUN).addAllObjects(parametros);
		}
		
		return doConsulta(request, response, anexoModificacion).addAllObjects(parametros); 
	}
	
	/**
	 * CONSULTA
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String alerta   = StringUtils.nullToString(request.getAttribute(ALERTA));
		String mensaje  = StringUtils.nullToString(request.getAttribute(MENSAJE));
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getAttribute(VIENE_DE_LISTADO_ANEXOS_MOD));
		if("".equals(alerta))
			alerta = StringUtils.nullToString(request.getParameter(ALERTA));
		if ("".equals(mensaje))
			mensaje = StringUtils.nullToString(request.getParameter(MENSAJE));
		
		try {
			if (idPoliza != null && !"".equals(idPoliza)) {
				anexoModificacion.getPoliza().setIdpoliza(Long.parseLong(idPoliza));
			} else {
				idPoliza = anexoModificacion.getPoliza().getIdpoliza().toString();				
			}
			
			parametros.put(ID_POLIZA, idPoliza);
			Poliza poliza = declaracionesModificacionPolizaManager.getPoliza(Long.parseLong(idPoliza));
			parametros.put("poliza", poliza);
			anexoModificacion.setPoliza(poliza); //Para Mejora Acuse Recibo 
			
			if(!"".equals(alerta)) {
				parametros.put(ALERTA, alerta); 
			}else if (!"".equals(mensaje)) {
				parametros.put(MENSAJE, mensaje); 
			}
		
			// Recuperamos los anexos de modificacion de la poliza
			List<AnexoModificacion> listAnexosModificacion = declaracionesModificacionPolizaManager.listByIdPoliza(anexoModificacion.getPoliza().getIdpoliza());
		
			parametros.put("listAnexosModificacion", listAnexosModificacion);
			
			final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
			parametros.put("perfil", usuario.getTipousuario());
			//volvemos a utilidades
			if (vieneDeListadoAnexosMod.equals("true")) {
				parametros.put(VOLVER,true);
				return new ModelAndView(REDIRECT_ANEXO_MODIFICACION_UTILIDADES_RUN).addAllObjects(parametros);
			}
			
		} catch(BusinessException be) {
			logger.error("Se ha producido un error durante la consulta de anexos", be);
			parametros.put(ALERTA, bundle.getString("mensaje.error.general"));
		} catch(Exception be) {
			logger.error("Se ha producido un error inesperado durante la consulta de anexos", be);
			parametros.put(ALERTA, bundle.getString("mensaje.error.general"));
		}
		if (anexoModificacion.getCupon() != null && anexoModificacion.getCupon().getCuponPrevio() == null){
			anexoModificacion.getCupon().setCuponPrevio(new CuponPrevio());
		}
		else if (anexoModificacion.getCupon() == null){
			anexoModificacion.setCupon(new Cupon());
		}
		return new ModelAndView("/moduloUtilidades/modificacionesPoliza/declaracionesAnexoModificacion", ANEXO_MODIFICACION, anexoModificacion).addAllObjects(parametros);
	}
	
	public ModelAndView doPasarDefinitiva(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) {

		Long idAnexoModificacion = anexoModificacion.getId();
		Map<String, Object> parametros = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
		
		try {
			//DAA 31/05/2013 Guardamos usuario y fecha de alta
			declaracionesModificacionPolizaManager.pasarDefinitiva(idAnexoModificacion, usuario.getCodusuario());
			parametros.put(MENSAJE, bundle.getString("mensaje.modificacion.definitivo.OK"));
		} catch (BusinessException e) {
			logger.error("Se ha producido un error inesperado al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString("mensaje.modificacion.definitivo.KO"));
		} catch (DAOException e) {
			logger.error("Se ha producido un error inesperado al pasar a definitivo el anexo", e);
			parametros.put(ALERTA, bundle.getString("mensaje.modificacion.definitivo.KO"));
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("XML invalido, no cumple el esquema ModificacionSeguroAgrario ",e );
			parametros.put(ALERTA, bundle.getString("mensaje.modificacion.definitivo.validacion.KO")+ " "+e.getMessage());
		}
		String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
		if ("true".equals(vieneDeListadoAnexosMod)) {
			parametros.put(VOLVER, true);
			return new ModelAndView(REDIRECT_ANEXO_MODIFICACION_UTILIDADES_RUN).addAllObjects(parametros);
		}else{
			return (doConsulta(request, response, anexoModificacion)).addAllObjects(parametros);
		}
	   
	} 
	
	/**
	 * VISUALIZACION
	 */
	public ModelAndView doVisualiza(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String idAnexo = StringUtils.nullToString(request.getParameter("id"));
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String vieneDeListadoAnexosMod = request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD);
		
		parametros.put(ID_ANEXO, idAnexo);
		parametros.put(ID_POLIZA, idPoliza);
		parametros.put("tipoModo", "modoVisualizacion");
		parametros.put(MODO_LECTURA, "true");
		parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);

		//return new ModelAndView(new RedirectView("coberturasModificacionPoliza.html"),"anexoModificacion", anexoModificacion).addAllObjects(parametros); //al doConsulta
		return this.coberturasModificacionPolizaController.doContinua(request, response, anexoModificacion).addAllObjects(parametros);
		//return this.coberturasModificacionPolizaController.doConsulta(request, response, anexoModificacion).addAllObjects(parametros);
	}

	/**
	 * Edicion y alta de un A.M de una poliza PPAL
	 * TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico (historicoEstadosManager.insertaEstado) 
	 * @author U029769 28/06/2013
	 * @param request
	 * @param response
	 * @param anexoModificacion
	 * @return ModelAndView
	 * @throws Exception
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String idAnexo  = StringUtils.nullToString(request.getParameter("id"));
		String idPoliza = StringUtils.nullToString(request.getParameter(ID_POLIZA));
		String realPath = this.getServletContext().getRealPath(WEB_INF);
		Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO); 
		boolean isCaducado = false;
		try {
			// Tanto si edita como si es un alta el estado es provisional
			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
			if(idAnexo != null && !"".equals(idAnexo)){
				// -- TIENE IDANEXO -- Edicion
				// Almacena el nuevo cupon solicitado por si se esta haciendo una renovacion de un cupon caducado
				String nuevoCupon = anexoModificacion.getCupon().getIdcupon();
				
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
				}
			} else { 
				// -- NO TIENE IDANEXO -- Alta
				anexoModificacion.getPoliza().setIdpoliza(new Long(idPoliza));
				
				// Alta de anexo por cupon
				if (anexoModificacion.getCupon() != null && 
						!StringUtils.nullToString(anexoModificacion.getCupon().getIdcupon()).equals("")){
					anexoModificacion.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_SW);
				}
				// Alta de anexo por ftp
				else {
					//Establezco los valores fijos en el anexo
					anexoModificacion.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP);
					anexoModificacion.setCupon(null);
				}

				anexoModificacion.setFechaAlta(new Date());
				anexoModificacion.setUsuarioAlta(usuario.getCodusuario());
				
				//Guardo el anexo
				anexoModificacion = declaracionesModificacionPolizaManager.altaAnexoModificacion(anexoModificacion, 
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
					/*** 12/01/21 PET.63485.FIII DNF coberturas, cambio el flujo de la pantalla*/
					//return this.coberturasModificacionPolizaController.doConsulta(request, response, anexoModificacion).addAllObjects(parametros);
					return this.coberturasModificacionPolizaController.doContinua(request, response, anexoModificacion).addAllObjects(parametros);
					/*** fin 12/01/21 PET.63485.FIII DNF coberturas, cambio el flujo de la pantalla*/
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
	}
	
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("recogerPolizaSesion", "true");
		parametros.put(OPERACION, VOLVER);
		return new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parametros);
	}
	
	public ModelAndView doImprimir(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception{
		
		return new ModelAndView("redirect:/informes.html").addObject(ID_ANEXO, anexoModificacion.getId()).addObject("method", "doInformeAnexoModificacion");
	
	}
	
	/**
	 * VER RECIBO
	 */
    public ModelAndView doVerRecibo(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception{
	    logger.debug("init - operacion ver Acuse Recibo");
		Map<String, Object> parametros = new HashMap<String, Object>();
		es.agroseguro.acuseRecibo.Error[] errores = null;
		Long idAnexo = null;
		AnexoModificacion anexoMod= null;
		Long idPoliza = null;
		String refPoliza = null;
		BigDecimal linea = null;
		BigDecimal plan = null;
		BigDecimal idEnvio = null;
		ModelAndView mv = null;
		
		try {
			 if (!StringUtils.nullToString(request.getParameter("id")).equals("")){
			 
			 	idAnexo = Long.valueOf((request.getParameter("id")));
			 	logger.debug("idAnexo:  "+ idAnexo + " idPoliza: " + anexoModificacion.getPoliza().getIdpoliza());
			 
			 	String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter(VIENE_DE_LISTADO_ANEXOS_MOD));
			 	parametros.put(VIENE_DE_LISTADO_ANEXOS_MOD, vieneDeListadoAnexosMod);
			 	
			 	anexoMod = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
				//.getAnexoModifById(Long.parseLong(idAnexo));
		
				if (anexoMod != null) {
					if (anexoMod.getComunicaciones() != null && anexoMod.getComunicaciones().getIdenvio() != null) {
						idEnvio = anexoMod.getComunicaciones().getIdenvio();
					}
					idPoliza = anexoMod.getPoliza().getIdpoliza();
					refPoliza = anexoMod.getPoliza().getReferencia();
					logger.debug("idPoliza:  "+ idPoliza + " refPoliza: " + refPoliza + " idEnvio: "+ idEnvio );
			
					if (idEnvio == null || refPoliza == null) {
						
						parametros.put(ID_POLIZA, idPoliza);
						parametros.put(ERR_LENGTH, 0);
						parametros.put(OPERACION, ANEXO_MODIFICACION);
						
						mv = new ModelAndView(MODULO_UTILIDADES_ERRORES_CONTRATACION,ANEXO_MODIFICACION, anexoModificacion).addAllObjects(parametros);
					} else {
						
						linea = anexoMod.getPoliza().getLinea().getCodlinea();
						plan = anexoMod.getPoliza().getLinea().getCodplan();
					
						//Se obtiene un array con los errores	
						errores = 	declaracionesModificacionPolizaManager.getFicheroContenido(idEnvio, refPoliza, linea, plan);						
						logger.debug("listado de errores - Size :  "+ errores.length );
						
						if (errores.length == 0) {
							parametros.put(ERR_LENGTH, 0);
							parametros.put(ID_POLIZA, idPoliza);
							parametros.put(OPERACION, ANEXO_MODIFICACION);
						} else {
						 parametros.put(ID_POLIZA, idPoliza);
						 parametros.put("errores", errores);
						 parametros.put(ERR_LENGTH, errores.length);
						 parametros.put(OPERACION, ANEXO_MODIFICACION);
						}
						
						mv = new ModelAndView(MODULO_UTILIDADES_ERRORES_CONTRATACION,ANEXO_MODIFICACION, anexoModificacion).addAllObjects(parametros);
					}
					
				}else {
					parametros.put(ID_POLIZA, anexoModificacion.getPoliza().getIdpoliza());
					mv =  doConsulta(request, response, anexoModificacion).addAllObjects(parametros); 
				} 
			}else {
				parametros.put(ID_POLIZA, anexoModificacion.getPoliza().getIdpoliza());
				mv =  doConsulta(request, response, anexoModificacion).addAllObjects(parametros); 
			} 
			
		} catch (BusinessException be) {
			logger.error("Se ha producido un error al recuperar los documentos de Acuse de Recibo: " + be.getMessage());
			parametros.put(ID_POLIZA, idPoliza);
			parametros.put(ALERTA, bundle.getString("mensaje.acuseRecibo.KO"));
			return new ModelAndView(MODULO_UTILIDADES_ERRORES_CONTRATACION,ANEXO_MODIFICACION, anexoModificacion).addAllObjects(parametros);
			 
		}
		logger.debug("end - doVerRecibo"); 
		return mv;
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
	public ModelAndView doComprobarAlta(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexoModificacion) throws Exception{
		
		try{
			// Recoge los par᭥tros enviados en la llamada AJAX
			BigDecimal codLinea = NumberUtils.formatToNumber(request.getParameter("codLinea"));
			BigDecimal codPlan = NumberUtils.formatToNumber(request.getParameter("codPlan"));
			BigDecimal idPoliza = NumberUtils.formatToNumber (request.getParameter(ID_POLIZA));
			BigDecimal idEstadoPlz = NumberUtils.formatToNumber(request.getParameter("idEstadoPlz"));
			
			// Si todos los par᭥tros estᮠinformados y contienen un num鲩co se realizan las validaciones previas; en caso contrario se devuelve el error correspondiente
 			String validaAltaAnexo = (codLinea == null || codPlan == null || idPoliza == null || idEstadoPlz == null) ? "error" : 
									 this.declaracionesModificacionPolizaManager.comprobarAltaAnexo(codLinea, codPlan, idPoliza, idEstadoPlz);
			 
			JSONObject objeto = new JSONObject();
			objeto.put("objeto",validaAltaAnexo);
			getWriterJSON(response, objeto);
    	}
    	catch(Exception excepcion){
    		logger.error("Error al comprobar el plan/l&iacute;nea de la p&oacute;liza asociada",excepcion);
    	}
		
		return null;
	
	}
	
	/**
	 * Metodo para comprobar que el am por cupon caducado indicado por el id de anexo y poliza pasados en la request 
	 * es editable solicitando un nuevo cupon o no
	 * @param request
	 * @param response
	 */
	public void isEditableAMCuponCaducado (HttpServletRequest request, HttpServletResponse response) {
		
		// Obtiene los id de anexo y poliza
		Long idAnexo = NumberUtils.parseLong(request.getParameter(ID_ANEXO));
		Long idPoliza = NumberUtils.parseLong(request.getParameter(ID_POLIZA));
		BigDecimal resultado = new BigDecimal (-1);
		
		// Si se han obtenido correctamente los ids se comprueba si el anexo es editable
		if (idAnexo != null && idPoliza != null) {
			resultado = declaracionesModificacionPolizaManager.isEditableAMCuponCaducado(idPoliza, idAnexo);
		}
		
		// Se escribe el resultado en la respuesta
		JSONObject objeto = new JSONObject();
		putJSON(objeto, "isEditableAMCuponCaducado", resultado.toString());
		getWriterJSON(response, objeto);
		
	}

    public void setDeclaracionesModificacionPolizaManager(DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public ISolicitudModificacionManager getSolicitudModificacionManager() {
		return solicitudModificacionManager;
	}

	public void setSolicitudModificacionManager(
			ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setCoberturasModificacionPolizaController(
			CoberturasModificacionPolizaController coberturasModificacionPolizaController) {
		this.coberturasModificacionPolizaController = coberturasModificacionPolizaController;
	}
}