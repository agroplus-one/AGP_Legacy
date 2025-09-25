package com.rsi.agp.core.webapp.action.mtoinf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoCamposPermitidosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.Vista;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

public class MtoCamposPermitidosController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoCamposPermitidosController.class);
	private IMtoCamposPermitidosService mtoCamposPermitidosService; 
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * Realiza la consulta de campos permitidos que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param campoPermitidoBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de campos permitidos
	 * @throws Exception 
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CamposPermitidos campoPermitidoBean) throws Exception {
		// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
    	String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parámetros que se pasarán a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	String html = null;
    	String tablaOrigen ="";
    	String descripcion ="";
    	String origenLlamada = request.getParameter("origenLlamada");
    	if ("".equals(origenLlamada)){
    		origenLlamada = (String)request.getAttribute("origenLlamada");
    	}
    	
    	if (!"menuGeneral".equals(origenLlamada)){
    		// Obtiene el filtro de busqueda correspondiente a la tabla origen
        	tablaOrigen = request.getParameter("tablaOrigen");
        	if (campoPermitidoBean.getVistaCampo() != null && campoPermitidoBean.getVistaCampo().getVista() !=null &&
        			campoPermitidoBean.getVistaCampo().getVista().getId() != null)
        			tablaOrigen = campoPermitidoBean.getVistaCampo().getVista().getId().toString();
        	
        	// Obtiene el filtro de busqueda correspondiente a la descripcion
        	descripcion = request.getParameter("descripcion");
        	if (campoPermitidoBean.getDescripcion() != null && !"".equals(campoPermitidoBean.getDescripcion())) {
        		descripcion = campoPermitidoBean.getDescripcion();
        	}
    	}
    	
    	String grupoEntidades = "";
    	perfil = usuario.getPerfil().substring(4);
    	parameters.put("perfil", perfil);

    	CamposPermitidos campPermBusqueda = (CamposPermitidos) campoPermitidoBean;
    	
    	// ---------------------------------------------------------------------------------
    	// -- Busqueda de Campos Permitidos y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	List<Vista> lstVistas = mtoCamposPermitidosService.getListadoVistas();
    	html = mtoCamposPermitidosService.getTablaCamposPermitidos(request, response, campPermBusqueda, origenLlamada, tablaOrigen, lstVistas, descripcion);
		if (html == null) {
			return null; // an export
		} else {
			String ajax = request.getParameter("ajax");
			// Llamada desde ajax
			if (ajax != null && ajax.equals("true")) {
				byte[] contents = html.getBytes("UTF-8");
				response.getOutputStream().write(contents);
				return null;
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("mtoCamposPermitidos", html);
			}
		}
    	
    	
    	// --------------------------------------
    	// -- Carga la lista de tipos de campo --
    	// --------------------------------------
    	parameters.put("listaTiposDato", mtoCamposPermitidosService.getListaTiposCampo());
    	parameters.put("tipoNumerico", ConstantsInf.CAMPO_TIPO_NUMERICO);
    	parameters.put("tipoFecha", ConstantsInf.CAMPO_TIPO_FECHA);
    	parameters.put("tipoTexto", ConstantsInf.CAMPO_TIPO_TEXTO);
    	
    	
		// ----------------------------------
    	// -- Carga del mapa de parámetros --
        // ----------------------------------
    	if ("menuGeneral".equals(origenLlamada)){
    		campPermBusqueda = new CamposPermitidos();
    		campoPermitidoBean = new CamposPermitidos();
    	}else{
    		if (campoPermitidoBean.getId() !=null){
				parameters.put("idCampoPermitido", campoPermitidoBean.getId().toString());
			}
			if (campoPermitidoBean.getVistaCampo()!= null && campoPermitidoBean.getVistaCampo().getId()!= null){
				parameters.put("idVistaCampo", campoPermitidoBean.getVistaCampo().getId());
			}
			if (campoPermitidoBean.getVistaCampo().getVista()!= null && campoPermitidoBean.getVistaCampo().getVista().getId()!= null){
				parameters.put("idVista", campoPermitidoBean.getVistaCampo().getVista().getId());
			}else if (tablaOrigen != null && !tablaOrigen.equals("")){
				parameters.put("idVista",tablaOrigen);
			}
    	}
    	 
    	parameters.put("lstVistas", lstVistas);
		parameters.put("campoPermitido", campoPermitidoBean);
		parameters.put("grupoEntidades", grupoEntidades);
		parameters.put("origenLlamada", origenLlamada);
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
		parameters.put("perfil", perfil);
		logger.debug("Establecemos perfil");
    	
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
       	mv = new ModelAndView(successView, "campoPermitidoBean", campPermBusqueda);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - MtoCamposPermitidosController - doConsulta");
    	
    	return mv; 
	}
	
	/**
	 * Realiza el alta del campo permitido
	 * @param request
	 * @param response
	 * @param campoPermitidoBean Objeto que encapsula el campo permitido a dar de alta
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de campos permitidos
	 * @throws Exception 
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, CamposPermitidos campoPermitidoBean) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try{
			
			VistaCampo visC = new VistaCampo();
			String idVistaCampo = StringUtils.nullToString(request.getParameter("idVistaCampo"));
			String idVista = StringUtils.nullToString(request.getParameter("idVista"));
			
			if (!idVistaCampo.equals("")){
				// añadimos la vistaCampo al Campo permitido
				visC = mtoCamposPermitidosService.getVistaCampo(Long.valueOf(idVistaCampo));
				campoPermitidoBean.setVistaCampo(visC);
			}
			
			if (!idVista.equals("")){
				Vista vis = new Vista();
				vis.setId(new BigDecimal(idVista));
				campoPermitidoBean.getVistaCampo().setVista(vis);
			}
			if (campoPermitidoBean != null){
				parameters = mtoCamposPermitidosService.altaCampoPermitido(campoPermitidoBean);
			}
			campoPermitidoBean = new CamposPermitidos();
			parameters.put("campoPermitidoBean", campoPermitidoBean);
			request.setAttribute("origenLlamada", "menuGeneral");
			return doConsulta(request, response, campoPermitidoBean).addAllObjects(parameters);
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_ALTA_KO));
			request.setAttribute("origenLlamada", "menuGeneral");
			return doConsulta(request, response, campoPermitidoBean).addAllObjects(parameters);  
		}
	}
	
	/**
	 * Realiza la modificacion del campo permitido
	 * @param request
	 * @param response
	 * @param campoPermitidoBean Objeto que encapsula el campo permitido a modificar
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de campos permitidos
	 * @throws Exception 
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, CamposPermitidos campoPermitidoBean) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		VistaCampo visC = new VistaCampo();
		String idVistaCampo = StringUtils.nullToString(request.getParameter("idVistaCampo"));
		String idVista = StringUtils.nullToString(request.getParameter("idVista"));
		
		try {
			if (!StringUtils.nullToString(request.getParameter("idCampoPermitido")).equals("")){
				
				if (!idVistaCampo.equals("")){
					// añadimos la vistaCampo al Campo permitido
					visC = mtoCamposPermitidosService.getVistaCampo(Long.valueOf(idVistaCampo));
					campoPermitidoBean.setVistaCampo(visC);
				}
				
				if (!idVista.equals("")){
					Vista vis = new Vista();
					vis.setId(new BigDecimal(idVista));
					campoPermitidoBean.getVistaCampo().setVista(vis);
				}
				campoPermitidoBean.setId(Long.parseLong(request.getParameter("idCampoPermitido")));
				
				parameters.put("idCampoPermitido", campoPermitidoBean.getId());
				parameters = mtoCamposPermitidosService.updateCampoPermitido(campoPermitidoBean);
				campoPermitidoBean = new CamposPermitidos();
				parameters.put("campoPermitidoBean", campoPermitidoBean);
			} else{
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_MODIF_KO));
			}
			request.setAttribute("origenLlamada", "menuGeneral");
			
			return doConsulta(request, response, campoPermitidoBean).addAllObjects(parameters);
		} catch (BusinessException e) {
			logger.error("Se ha producido un error al editar el Campo Permitido: " + e.getMessage());
			parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_MODIF_KO));
			request.setAttribute("origenLlamada", "menuGeneral");
			campoPermitidoBean = new CamposPermitidos();
			return doConsulta(request, response, campoPermitidoBean).addAllObjects(parameters);
		}
	}
	
	/**
	 * Realiza la baja del campo permitido
	 * @param request
	 * @param response
	 * @param campoPermitidoBean Objeto que encapsula el campo permitido a dar de baja
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de campos permitidos
	 * @throws Exception 
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, CamposPermitidos campoPermitidoBean) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Se obtiene el objeto almacenado en sesion que contiene el filtro de busqueda para hacer la redireccion tras borrar el registro 
		CamposPermitidos objFiltro = (request.getSession().getAttribute("filtroCampoPermitidos") != null) ?
									 (CamposPermitidos)request.getSession().getAttribute("filtroCampoPermitidos") : new CamposPermitidos();
			
		try {										 
			String idCampoPermitido = (String) request.getParameter("idCampoPermitido");
			campoPermitidoBean = mtoCamposPermitidosService.getCampoPermitido(Long.parseLong(idCampoPermitido));
			if (campoPermitidoBean != null){
				mtoCamposPermitidosService.bajaCampoPermitido(campoPermitidoBean);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_CAMPOPERMITIDO_BAJA_OK));
			}else{
				logger.error("El CampoPermitido con id: " + idCampoPermitido + " no existe en BBDD");
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_BAJA_KO));
			}
			parameters.put("campoPermitidoBean", objFiltro);
			parameters.put("origenLlamada", "borrar");
			String descripcion = (String) request.getParameter("descripcion");
			parameters.put("descripcion", descripcion);
			parameters.put("tablaOrigen", (String) request.getParameter("tOrigen"));
			return new ModelAndView("redirect://mtoCamposPermitidos.run").addAllObjects(parameters);
		}
		
		catch (BusinessException e) {
			logger.error("Se ha producido un error al dar de baja el Campo permitido. " + e.getMessage());
			if (e.getMessage().equals("Dependencias")){
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_BAJA_DEPENDENCIAS_KO));		
			}else{
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_CAMPOPERMITIDO_BAJA_KO));
			}
			return doConsulta(request, response, objFiltro).addAllObjects(parameters);  	
		}
	}
	
	
	/**
	 * Setter del Service para Spring
	 * @param mtoCamposPermitidosService
	 */
	public void setMtoCamposPermitidosService(IMtoCamposPermitidosService mtoCamposPermitidosService) {
		this.mtoCamposPermitidosService = mtoCamposPermitidosService;
	}
	
	/**
	 * Setter de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
