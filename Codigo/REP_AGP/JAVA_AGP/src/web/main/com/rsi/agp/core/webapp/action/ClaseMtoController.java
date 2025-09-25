package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IClaseMtoService;
import com.rsi.agp.core.managers.impl.ActivacionLineasManager;
import com.rsi.agp.core.managers.impl.ClaseManager;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ClaseMtoController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(ClaseMtoController.class);
	private IClaseMtoService claseMtoService;
	private ClaseManager claseManager;
	private ActivacionLineasManager activacionLineasManager;
	
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta de clases que se ajustan al filtro de búsqueda
	 * 
	 * @param request
	 * @param response
	 * @param claseMtoBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Clase claseMtoBean) {
		logger.debug("init - ClaseMtoController");
		
		// Obtiene el usuario de la sesión y su sesión
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		// Map para guardar los parámetros que se pasarán a la jsp
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variable que almacena el código de la tabla de pólizas
		String html = null;
		String origenLlamada = request.getParameter("origenLlamada");
		String descripcion = request.getParameter("descripcion");
		
		//DAA 25/01/2013
		if (("cicloPoliza").equals(origenLlamada)){
			claseMtoBean.setLinea(usuario.getColectivo().getLinea());
			parameters.put("vieneDeCicloPoliza", true);
			parameters.put("origenLlamada", "cicloPoliza");
			
		}
		
		Clase claseMtoBusqueda = (Clase) claseMtoBean;
		request.getSession().removeAttribute("claseDetalleBusqueda");
		// ---------------------------------------------------------------------------------
		// -- Búsqueda de clases y generación de la tabla de
		// presentación --
		// ---------------------------------------------------------------------------------
		logger.debug("Comienza la búsqueda de Clases");
		
		try{

			html = claseMtoService.getTablaClaseMto(request, response, claseMtoBusqueda, origenLlamada,descripcion);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
					return null;
				} else
					// Pasa a la jsp el código de la tabla a través de este atributo
					request.setAttribute("consultaClaseMto", html);
			}
				
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parameters.put("alerta", alerta);
			}
			if (mensaje != null) {
				parameters.put("mensaje", mensaje);
			}
			parameters.put("perfil", perfil);
			parameters.put("claseDetalleBean", new ClaseDetalle());
			parameters.put("claseDetalleGanadoBean", new ClaseDetalleGanado());
			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redirección y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "claseMtoBean", claseMtoBean);
			mv.addAllObjects(parameters);
	
			logger.debug("end - ClaseMtoController");
	
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : ClaseMtoController - doConsulta", e);
		}
		return null;
	}

	/**
	 * Da de alta una Clase
	 * 
	 * @param request
	 * @param response
	 * @param claseMtoBean
	 *            Objeto que encapsula la información de la clase a dar de
	 *            alta
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Clase claseMtoBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			// Si el objeto Clase no está cargado no se puede dar de alta
			if (claseMtoBean!=null){
				parameters = claseMtoService.altaClaseMto(claseMtoBean);
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.alta.KO"));
			}
		}
		catch (Exception e){
			logger.debug("Error inesperado en el alta de la clase", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.alta.KO"));
		}
		
		// Si el mapa de parámetros está vacío indica que el alta ha finalizado correctamente, se incluye el mensaje informativo
		if (parameters.isEmpty()) parameters.put("mensaje", bundle.getString("mensaje.clase.alta.OK"));
		
		// Redirecciona al listado
		return doConsulta(request, response, claseMtoBean).addAllObjects(parameters);   
	}

	/**
	 * Edita una clase
	 * 
	 * @param request
	 * @param response
	 * @param claseMtoBean  Objeto que encapsula la información la clase a editar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request,HttpServletResponse response, Clase claseMtoBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try{
			if (claseMtoBean != null && claseMtoBean.getId() != null){
				parameters = claseMtoService.editaClaseMto(claseMtoBean);
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
			}
		}
		catch (Exception e){
			logger.debug("Error inesperado en la edición de la clase", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
		}
		
		if (parameters.isEmpty()) parameters.put("mensaje", bundle.getString("mensaje.clase.edicion.OK"));
		
		return doConsulta(request, response, claseMtoBean).addAllObjects(parameters);     	   
	}

	/**
	 * Borra una clase
	 * 
	 * @param request
	 * @param response
	 * @param claseMtoBean
	 *            Objeto que encapsula la información de la clase a borrar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, Clase claseMtoBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try{
			if (!StringUtils.nullToString(claseMtoBean.getId()).equals("")){
				
				parameters = claseMtoService.bajaClaseMto(claseMtoBean);
				claseMtoBean.setId(null);
				
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.borrado.KO"));
			}
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.borrado.KO"));
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la baja de la clase", e);
			parameters.put("alerta", bundle.getString("mensaje.clase.borrado.KO"));
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);
		}
		return mv;     
	}
	/**
	 * Replica todas las clases y sus ClasesDetalle para un lineaseguroid
	 * 
	 * @param request
	 * @param response
	 * @param claseMtoBean
	 *            Objeto que encapsula la información de la clase a borrar
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	
	public ModelAndView doReplicar(HttpServletRequest request,HttpServletResponse response, Clase claseMtoBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		
		try{	
			//comprobamos que exista el lineaseguroid
			boolean existe = claseMtoService.existeLineaseguroid(claseMtoBean);
			if(existe){
				String lineaDestinoReplica = request.getParameter("lineareplica");
				String planDestinoReplica = request.getParameter("planreplica");
				
				parameters = claseMtoService.replicaPlanLineaClaseMto(claseMtoBean,planDestinoReplica, lineaDestinoReplica );
				
			}else{
				parameters.put("alerta", bundle.getString("mensaje.clase.alta.lineaseguroid.KO"));
			}
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);	
				
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.clase.alta.lineaseguroid.KO"));
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado en la replica de la clase", e);
			//parameters.put("alerta", bundle.getString("mensaje.clase.edicion.KO"));
			mv = doConsulta(request, response, claseMtoBean).addAllObjects(parameters);
		}
		
		return mv;       	   
	}
	
	// DAA 25/01/2013    
	public ModelAndView doCarga(HttpServletRequest request,	HttpServletResponse response, Clase claseMtoBean) throws Exception {
		logger.debug("init - doCarga");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		claseMtoBean.setLinea(usuario.getColectivo().getLinea());
		parameters = claseManager.cargaClase(request,claseMtoBean.getId());
		parameters.put("vieneDeCicloPoliza", true);
		parameters.put("origenLlamada", "cicloPoliza");

		mv = this.doConsulta(request, response, claseMtoBean);
		
		logger.debug("end - doCarga");
		mv.addAllObjects(parameters);
		return mv;
	}
	
	// DAA 25/01/2013    
	public ModelAndView doCoberturas(HttpServletRequest request,HttpServletResponse response, Clase claseBean) throws Exception {
		logger.debug("init - doCoberturas");
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		Long idlineaClase = usuario.getColectivo().getLinea().getLineaseguroid();

		parameters = activacionLineasManager.dameListaModulos(idlineaClase, "clase", usuario.getTipousuario());
		parameters.put("tipoMenu", "menuGeneral");
		
		logger.debug("end - doCoberturas");
		return new ModelAndView("moduloTaller/activacion/detalleCoberturas", "detCoberturas", parameters);

	}

	/**
	 * Setter de propiedad
	 * 
	 * @param claseMtoService
	 */
	public void setClaseMtoService(
			IClaseMtoService claseMtoService) {
		this.claseMtoService = claseMtoService;
	}

	/**
	 * Setter de propiedad
	 * 
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}
	
	public void setActivacionLineasManager(
			ActivacionLineasManager activacionLineasManager) {
		this.activacionLineasManager = activacionLineasManager;
	}
}
