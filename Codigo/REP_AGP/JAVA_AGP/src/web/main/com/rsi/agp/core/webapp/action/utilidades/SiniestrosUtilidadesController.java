package com.rsi.agp.core.webapp.action.utilidades;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.utilidades.ISiniestrosUtilidadesService;
import com.rsi.agp.core.managers.impl.SiniestrosManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestrosUtilidades;

public class SiniestrosUtilidadesController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(SiniestrosUtilidadesController.class);
	private ISiniestrosUtilidadesService siniestrosUtilidadesService;
	private SiniestrosManager siniestrosManager;
	private String successView;
	
	/**
	 * Realiza la consulta de siniestros que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param campoCalculadoBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que contiene la redireccion a la pagina listado de siniestros
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, SiniestrosUtilidades siniestroBean) {
		
		log ("doConsulta", "inicio");
				
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	
    	// Comprueba si la llamada se ha hecho desde el menu lateral
    	String origenLlamada=null;
    	boolean deMenuLateral=false;
    	if(null!=request.getParameter("origenLlamada"))
    		origenLlamada=request.getParameter("origenLlamada");
    	if(null!=origenLlamada && origenLlamada.equals("menuGeneral"))
    		deMenuLateral=true;
    	
    	//parameters.put("origenLlamada", "listadoSiniestros");
    	
    	// Comprueba si es la primera busqueda que se realiza
    	String primeraBusqueda = request.getParameter("primeraBusqueda");
    	// Comprueba si se esta volviendo de alguna pagina de acciones de siniestro
    	boolean volver = request.getParameter("volver") != null;
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	
    	// Si vuelve, se intenta cargar el filtro de busqueda que haya en sesion
    	if (volver) {
    		if (request.getSession().getAttribute("siniestroUtilidadesBean") != null)
    			siniestroBean = (SiniestrosUtilidades) request.getSession().getAttribute("siniestroUtilidadesBean");
    	}
    	
    	// Carga el grupo de entidades asociadas al usuario si es de perfil 5
		List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
    	parameters.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
    	// Carga el grupo de oficinas asociadas al usuario si es de perfil 2
    			List<BigDecimal> grupoOficinas = usuario.getListaCodOficinasGrupo();
    	    	parameters.put("grupoOficinas",StringUtils.toValoresSeparadosXComas(grupoOficinas, false, false));
    	// -----------------------------------------------------------------
    	// -- Busqueda de siniestros que se ajsutan al filtro de busqueda --
        // -----------------------------------------------------------------  
    	// Si la llamada se ha hecho desde el menu lateral no se realiza la busqueda
    	if (!deMenuLateral || volver) {
    		
    		// Guarda el objeto de busqueda en sesion
    		request.getSession().setAttribute("siniestroUtilidadesBean", siniestroBean);
    		
	    	log("doConsulta", "Comienza la busqueda de siniestros");
	    	String html = siniestrosUtilidadesService.getTablaSiniestros(request, response, siniestroBean, primeraBusqueda, grupoEntidades,grupoOficinas);
	        if (html == null) {
	            return null; // an export
	        } else {
	            String ajax = request.getParameter("ajax");
	            // Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents;
					try {
						contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
					} catch (UnsupportedEncodingException e) {
						log("doConsulta", "Ocurrio un error por encoding no soportado", e);
					} catch (IOException e) {
						log("doConsulta", "Ocurrio un error por al escribir en el response", e);
					}

					return null;
				} else {
					// Pasa a la jsp el codigo de la tabla a traves de este atributo
					request.setAttribute("listadoSiniestros", html);
				}
			}
    	}
    	
    					
    	// -----------------------------------------------------------
    	// -- Carga los datos necesarios para la jsp y el filtro de busqueda --
        // -----------------------------------------------------------
    	log ("doConsulta", "Carga los datos necesarios para la jsp y el filtro de busqueda");
    	SiniestrosUtilidades siniestroBusq=  cargarDatosFiltroBusqueda (parameters, request, volver, siniestroBean, deMenuLateral);  
    	
    	// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	log ("doConsulta", "Crea la redireccion a jsp");
    	ModelAndView mv = new ModelAndView(successView);
    	mv = new ModelAndView(successView, "siniestroBean", siniestroBusq);
       	mv.addAllObjects(parameters);
		
		log ("doConsulta", "fin");
		
		return mv;
	}
	
	
	
	/**
	 * Carga los parametros que se reenvian a la jsp
	 * @param parameters
	 * @param request
	 */
	private SiniestrosUtilidades cargarDatosFiltroBusqueda (Map<String, Object> parameters, HttpServletRequest request, boolean volver,
											SiniestrosUtilidades siniestroBean, boolean deMenuLateral) {
		
		// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	
    	// Si viene del menu lateral se crea el objeto
    	if (deMenuLateral) siniestroBean = new SiniestrosUtilidades();
    	// Tratamiento del perfil
    	if (null != perfil && !"".equalsIgnoreCase(perfil))
		{
			
			if (!volver && deMenuLateral ) 
			{
				
		    	
				switch (new Integer(perfil).intValue())
				{
					// MPM - 26/10/2012 - El perfil 2 ve las mismas polizas que el 1
					case Constants.COD_PERFIL_1: case Constants.COD_PERFIL_2: 
							siniestroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							siniestroBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							siniestroBean.setNombreOficina(usuario.getOficina().getNomoficina());
							siniestroBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
							break;
					case Constants.COD_PERFIL_3:	
							siniestroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							siniestroBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							siniestroBean.setNombreOficina(usuario.getOficina().getNomoficina());
							siniestroBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
							break;				
					case Constants.COD_PERFIL_4:
							siniestroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							siniestroBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							siniestroBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
							siniestroBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
							siniestroBean.setCodusuario(usuario.getCodusuario());
							break;		
					default:
							break;
				}
			}
		}
    	
    	//campos por defecto en caso de usuario externo
        if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
        	if (usuario.getTipousuario().compareTo(Constants.PERFIL_1)==0) {
        		siniestroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		siniestroBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		siniestroBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
        		siniestroBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        	}else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3)==0) {
        		siniestroBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		siniestroBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		siniestroBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
        		siniestroBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        		siniestroBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
        		siniestroBean.setNombreOficina(usuario.getOficina().getNomoficina());
        		siniestroBean.setDelegacion(usuario.getDelegacion().toString());
        	}
		}	
    	
    	
    	// Obtiene el listado de estados posibles para los siniestros
    	List<EstadoSiniestro> estados = new ArrayList<EstadoSiniestro>();
    	try {
			estados = siniestrosManager.getEstadosSiniestro();
		} catch (BusinessException e) {
			log ("cargarDatosFiltroBusqueda", "Ocurrio un error al obtener la lista de estados de siniestros", e);
		}
		
		// Obtiene el listado de riesgos posibles
		List<Riesgo> riesgos = siniestrosUtilidadesService.getRiesgos();
    	
    	// Anade el perfil del usuario a los parametros
    	parameters.put("perfil", perfil);
    	// Anade el listado de estados de los siniestros a los parametros
    	parameters.put("estados", estados);
    	// Anade el listado de riesgos posibles a los parametros
    	parameters.put("riesgos", riesgos);
    	// Anade el parametro que indica si la llamada se ha hecho desde el menu lateral
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
    	// Anade el parametro que indica si hay que mostrar alguna alerta en la pantalla
    	parameters.put("alerta", request.getParameter("alerta"));
    	// Anade el parametro que indica si hay que mostrar algun mensaje en la pantalla
    	parameters.put("mensaje", request.getParameter("mensaje"));
    	parameters.put("externo",usuario.getExterno());
    	parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
    	parameters.put("grupoOficinas", StringUtils.toValoresSeparadosXComas (usuario.getListaCodOficinasGrupo(), false, false));
    	
    	return siniestroBean;
	}
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("SiniestrosUtilidadesController." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("SiniestrosUtilidadesController." + method + " - " + msg, e);
	}
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automaticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacias
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
	
	/**
	 * Setter del Service para Spring
	 * @param siniestrosUtilidadesService
	 */
	public void setSiniestrosUtilidadesService(ISiniestrosUtilidadesService siniestrosUtilidadesService) {
		this.siniestrosUtilidadesService = siniestrosUtilidadesService;
	}
	
	/**
	 * Setter de la propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	/**
	 * Setter del Manager para Spring
	 * @param siniestrosManager
	 */
	public void setSiniestrosManager(SiniestrosManager siniestrosManager) {
		this.siniestrosManager = siniestrosManager;
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
		
		List<SiniestrosUtilidades> items;
		
		try {
			items = siniestrosUtilidadesService.getAllFilteredAndSorted();
			if (items.size() != 0) {
				
				request.setAttribute("listado", items);
				request.setAttribute("nombreInforme", "ListadoSiniestros");
				request.setAttribute("jasperPath", "informeJasper.listadoSiniestros");
				
				return new ModelAndView("forward:/informes.html?method=doInformeListado");
			}
		} catch (BusinessException e) {
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
		}
				
		return null;
	}
}
