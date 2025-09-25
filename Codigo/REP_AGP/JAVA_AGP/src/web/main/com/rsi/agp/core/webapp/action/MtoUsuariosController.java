/**
 * 
 */
package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IMtoUsuariosService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

/**
 * @author U029769
 *
 */
public class MtoUsuariosController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoUsuariosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IMtoUsuariosService mtoUsuariosService;
	private String successView;
	
	/**
	 * Realiza la consulta de usuarios que se ajustan al filtro de busqueda
	 * 08/05/2014 U029769
	 * @param request
	 * @param response
	 * @param usuarioBean
	 * @return
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Usuario usuarioBean) {
		logger.debug("init - MtoUsuariosController");
		
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		final Map<String, Object> parameters = new HashMap<String, Object>();
		String html = null;
		List<String> listTiposUsuarios = new ArrayList<String>();
		
		parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
		Usuario usuarioBusqueda = (Usuario) usuarioBean;

		String origenLlamada = request.getParameter("origenLlamada");
		if (StringUtils.nullToString(origenLlamada).equals("")) {
			origenLlamada = (String) request.getAttribute("origenLlamada");
		}
		try{
			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				logger.debug("Comienza la busqueda de Usuarios");
				
				logger.debug("Email busqueda -->: " + usuarioBusqueda.getEmail());
				
				html = mtoUsuariosService.getTablaUsuarios(request, response, usuarioBusqueda, origenLlamada);
				if (html == null) {
					return null; 
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este atributo
						request.setAttribute("consultaUsuarios", html);
				}
			}
				
				parameters.put("origenLlamada", origenLlamada);
				String mensaje = request.getParameter("mensaje");
				String alerta = request.getParameter("alerta");
				if (alerta != null) {
					parameters.put("alerta", alerta);
				}
				if (mensaje != null) {
					parameters.put("mensaje", mensaje);
				}
				parameters.put("perfil", perfil);
				
				
				parameters.put("esExterno", usuario.getExterno());
				
				// -----------------------------------------------------------------
				// -- Se crea el objeto que contiene la redireccion y se devuelve --
				// -----------------------------------------------------------------
				ModelAndView mv = new ModelAndView(successView);
				if(usuarioBean.getFechaLimite() == null && origenLlamada.equals("menuGeneral")) {				
					usuarioBean.setFechaLimite(new Date());
				}
				mv = new ModelAndView(successView, "usuarioBean", usuarioBean);
				mv.addAllObjects(parameters);
		
				logger.debug("end - MtoUsuariosController");
		
				return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : MtoUsuariosController - doConsulta", e);
		}
		return null;
	}
	
	public ModelAndView doBorrar(HttpServletRequest request,HttpServletResponse response,Usuario usuarioBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			if (!StringUtils.nullToString(usuarioBean.getCodusuario()).equals("")){
				parameters = mtoUsuariosService.borraUsuario(usuarioBean);
				
			}else {
				parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.borrar.KO"));
			}
			request.setAttribute("origenLlamada", "doBorrar");
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.borrar.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado al borrar de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.borrar.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
		}
		return mv;       	
	}
	
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response,Usuario usuarioBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{

			if (estanRellenosCampos(usuarioBean)){
				parameters = mtoUsuariosService.editaUsuario(usuarioBean,request);
			}else{
				parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.edicion.KO"));
			}
			parameters.put("showModificar", "true");
			request.setAttribute("origenLlamada", "doEditar");
			
			parameters.put("codusuInicial", usuarioBean.getCodusuario());
			parameters.put("perfilIni",usuarioBean.getPerfil());
			parameters.put("externo",usuarioBean.getColectivo());
			parameters.put("externoIni", usuarioBean.getTipousuario());
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
			
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.edicion.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
		}
		return mv;       	   
	}
	
	

	public ModelAndView doAlta(HttpServletRequest request,HttpServletResponse response,Usuario usuarioBean) {
		
		
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		try{
			
			if (estanRellenosCampos(usuarioBean)){ 
				
				parameters = mtoUsuariosService.altaUsuario(usuarioBean);
			}else{
				parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			}
			parameters.put("showModificar", "true");
			parameters.put("codusuInicial", usuarioBean.getCodusuario());
			request.setAttribute("origenLlamada", "doAlta");
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
			
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la edicion de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, usuarioBean).addAllObjects(parameters);
		}
		return mv;       	   
	}
	
	/** Cambio masivo de usuarios
	 * 
	 * @param request
	 * @param response
	 * @param errorWsAccionBean
	 * @return mv
	 */
	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, Usuario usuarioBean) {
		logger.debug("init - MtoUsuariosController - doCambioMasivo");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		
		try{
			parameters = mtoUsuariosService.cambioMasivo(listaIdsMarcados_cm, usuarioBean);
		
		}catch (Exception e){
			logger.debug("Error inesperado en el Cambio Masivo de Usuario", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.edicion.KO"));
		
		}	
		// Recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y no perder el filtro al volver
		Usuario usuarioCMBean = mtoUsuariosService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("consultaUsuarios_LIMIT"));
		request.setAttribute("origenLlamada", "cambioMasivo");
		mv = doConsulta(request, response, usuarioCMBean).addAllObjects(parameters);
				
		logger.debug("end - MtoUsuariosController - doCambioMasivo");
		return mv;
	}
	
	/** Cambio masivo de incrementar en un año la fecha limite de los usuarios
	 * 
	 * @param request
	 * @param response
	 * @param usuarioBean
	 * @return mv
	 */
	public ModelAndView doIncrementarFecha(HttpServletRequest request, HttpServletResponse response, Usuario usuarioBean) {
		logger.debug("init - MtoUsuariosController - doIncrementarFecha");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		
		String listaIdsMarcados_ifecha = StringUtils.nullToString(request.getParameter("listaIdsMarcados_ifecha"));
		
		try {
			parameters = mtoUsuariosService.incrementarFecha(listaIdsMarcados_ifecha, usuarioBean);
		
		}catch (Exception e) {
			logger.debug("Error inesperado en el Cambio Masivo de Usuario", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoUsuario.edicion.KO"));
		
		}	
		// Recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y no perder el filtro al volver
		Usuario usuarioCMBean = mtoUsuariosService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("consultaUsuarios_LIMIT"));
		request.setAttribute("origenLlamada", "incrementarFecha");
		mv = doConsulta(request, response, usuarioCMBean).addAllObjects(parameters);
				
		logger.debug("end - MtoUsuariosController - doIncrementarFecha");
		return mv;
	}
	
	private boolean estanRellenosCampos(Usuario usuarioBean) {
		
		if (!StringUtils.nullToString(usuarioBean.getCodusuario()).equals("") && 
				!StringUtils.nullToString(usuarioBean.getTipousuario()).equals("")&&
				!StringUtils.nullToString(usuarioBean.getOficina().getId().getCodentidad()).equals("")&&
				!StringUtils.nullToString(usuarioBean.getOficina().getId().getCodoficina()).equals("")&&
				!StringUtils.nullToString(usuarioBean.getSubentidadMediadora().getId().getCodentidad()).equals("")&&
				!StringUtils.nullToString(usuarioBean.getSubentidadMediadora().getId().getCodsubentidad()).equals("")&&
				!StringUtils.nullToString(usuarioBean.getDelegacion()).equals("") &&
				!StringUtils.nullToString(usuarioBean.getCargaPac()).equals("") &&
				!StringUtils.nullToString(usuarioBean.getFinanciar()).equals("")
				) {
			
			return true;
		}
		return false;
	}
	public void setMtoUsuariosService(IMtoUsuariosService mtoUsuariosService) {
		this.mtoUsuariosService = mtoUsuariosService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	
	
	
	
	
}
