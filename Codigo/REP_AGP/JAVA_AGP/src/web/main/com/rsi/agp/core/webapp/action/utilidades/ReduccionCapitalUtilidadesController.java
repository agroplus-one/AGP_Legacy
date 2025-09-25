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
import com.rsi.agp.core.jmesa.service.utilidades.IReduccionCapitalUtilidadesService;
import com.rsi.agp.core.manager.impl.anexoRC.IAnexoReduccionCapitalManager;
import com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa.Causas;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.DeclaracionesReduccionCapitalManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.reduccionCap.Estado;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapitalUtilidades;

public class ReduccionCapitalUtilidadesController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(ReduccionCapitalUtilidadesController.class);
	private IReduccionCapitalUtilidadesService reduccionCapitalUtilidadesService;
	private DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager;
	private String successView;
	//P0079361
	private IAnexoReduccionCapitalManager anexoReduccionCapitalManager;
	private static final String WEB_INF = "/WEB-INF/";
	
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	//P0079361
	
	/**
	 * Realiza la consulta de Red. Capital que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param campoCalculadoBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que contiene la redireccion a la pagina listado de Red. Capital
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, ReduccionCapitalUtilidades reduccionCapitalUtilidadesBean) {
		
		log ("doConsulta", "inicio");
				
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	// Comprueba si la llamada se ha hecho desde el menu lateral
    	boolean deMenuLateral = request.getParameter("origenLlamada") != null;
    	// Comprueba si es la primera busqueda que se realiza
    	String primeraBusqueda = request.getParameter("primeraBusqueda");
    	// Comprueba si se esta volviendo de alguna pagina de acciones de Red. Capital
    	boolean volver = request.getParameter("volver") != null;
    	
    	// Si vuelve, se intenta cargar el filtro de busqueda que haya en sesion
    	if (volver) {
    		if (request.getSession().getAttribute("reduccionCapitalUtilidadesBean") != null)
    			reduccionCapitalUtilidadesBean = (ReduccionCapitalUtilidades) request.getSession().getAttribute("reduccionCapitalUtilidadesBean");
    	}else {
    		request.getSession().removeAttribute("reduccionCapitalUtilidadesBean");
    		reduccionCapitalUtilidadesBean.setEstadoCuponId((String) request.getParameter("ftpNumCupon"));
    		reduccionCapitalUtilidadesBean.setFdaniosNY((String) request.getParameter("fechadanioId"));    		
    		reduccionCapitalUtilidadesBean.setFdaniosHastaNY((String) request.getParameter("fechadanioIdHasta"));
    		reduccionCapitalUtilidadesBean.setFenvNY((String) request.getParameter("fechaEnvioId"));
    		reduccionCapitalUtilidadesBean.setFenvHastaNY((String) request.getParameter("fechaEnvioIdHasta"));
    		reduccionCapitalUtilidadesBean.setFenvpolHastaNY((String) request.getParameter("fechaEnvioPolIdHasta"));
    		reduccionCapitalUtilidadesBean.setFenvpolNY((String) request.getParameter("fechaEnvioPolId"));
    	}
    	
    	// -----------------------------------------------------------------
    	// -- Busqueda de Red. Capital que se ajsutan al filtro de busqueda --
        // -----------------------------------------------------------------  
    	// Carga el grupo de entidades asociadas al usuario si es de perfil
    	List <BigDecimal> listaGrupoEntidades = usuario.getListaCodEntidadesGrupo();
    	List <BigDecimal> listaGrupoOficinas = usuario.getListaCodOficinasGrupo();
    	parameters.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(listaGrupoEntidades, false, false));
    	parameters.put("grupoOficinas",StringUtils.toValoresSeparadosXComas(listaGrupoOficinas, false, false));
    	// Si la llamada se ha hecho desde el menu lateral no se realiza la busqueda
    	if (!deMenuLateral || volver) {
    		
    		// Guarda el objeto de busqueda en sesion
    		request.getSession().setAttribute("reduccionCapitalUtilidadesBean", reduccionCapitalUtilidadesBean);
    		
	    	log("doConsulta", "Comienza la busqueda de ReduccionCapital"); 
	    	String html = reduccionCapitalUtilidadesService.getTablaReduccionCapital(request, response, reduccionCapitalUtilidadesBean, primeraBusqueda, listaGrupoEntidades,listaGrupoOficinas);
	//    	request.getSession().setAttribute("reduccionCapitalUtilidadesBean", reduccionCapitalUtilidadesBean);
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
					request.setAttribute("listadoReduccionCapital", html);
				}
			}
    	}
    	
    					
    	// -----------------------------------------------------------
    	// -- Carga los datos necesarios para la jsp y el filtro de busqueda --
        // -----------------------------------------------------------
    	log ("doConsulta", "Carga los datos necesarios para la jsp y el filtro de busqueda");
    	ReduccionCapitalUtilidades reduccionCapitalBusq=  cargarDatosFiltroBusqueda (parameters, request, volver, reduccionCapitalUtilidadesBean, deMenuLateral);  
    	
    	// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	log ("doConsulta", "Crea la redireccion a jsp");
    	ModelAndView mv = new ModelAndView(successView);
    	mv = new ModelAndView(successView, "reduccionCapitalUtilidadesBean", reduccionCapitalBusq).addObject("reduccionCapitalBean", new ReduccionCapital());
       	mv.addAllObjects(parameters);
		
		log ("doConsulta", "fin");
		
		return mv;
	}
	
	
	
	/**
	 * Carga los parametros que se reenvo­an a la jsp
	 * @param parameters
	 * @param request
	 */
	private ReduccionCapitalUtilidades cargarDatosFiltroBusqueda (Map<String, Object> parameters, HttpServletRequest request, boolean volver,
											ReduccionCapitalUtilidades reduccionCapitalBean, boolean deMenuLateral) {
		//P0079361
		//TODO comprobar si lo que se pide esta hecho o hay que modificar algo con la respuesta de la nueva version del servicio
		// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	
    	// Si viene del menu lateral se crea el objeto
    	if (deMenuLateral) reduccionCapitalBean = new ReduccionCapitalUtilidades();
    	
    	// Tratamiento del perfil
    	if (null != perfil && !"".equalsIgnoreCase(perfil))
		{
			// Si no se ha cargado la poliza de sesion se incluyen filtros por defecto en el objeto de busqueda
			if (!volver && deMenuLateral) 
			{
				switch (new Integer(perfil).intValue())
				{
					// MPM - 26/10/2012 - El perfil 2 ve las mismas polizas que el 1
					case Constants.COD_PERFIL_1: case Constants.COD_PERFIL_2: 
						reduccionCapitalBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						reduccionCapitalBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
						reduccionCapitalBean.setNombreOficina(usuario.getOficina().getNomoficina());
						reduccionCapitalBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						break;
					case Constants.COD_PERFIL_3:	
						reduccionCapitalBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						reduccionCapitalBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
						reduccionCapitalBean.setNombreOficina(usuario.getOficina().getNomoficina());
						reduccionCapitalBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
						break;				
					case Constants.COD_PERFIL_4:
						reduccionCapitalBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
						reduccionCapitalBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
						reduccionCapitalBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
						reduccionCapitalBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
						reduccionCapitalBean.setCodusuario(usuario.getCodusuario());
						break;	
					default:
						break;
				}
			}
		}
    	//campos por defecro en caso de usuario externo
        if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
        	if (usuario.getTipousuario().compareTo(Constants.PERFIL_1)==0) {
        		reduccionCapitalBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		reduccionCapitalBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		reduccionCapitalBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
        		reduccionCapitalBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        	}else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3)==0) {
        		reduccionCapitalBean.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		reduccionCapitalBean.setNombreEntidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		reduccionCapitalBean.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad());
        		reduccionCapitalBean.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        		reduccionCapitalBean.setOficina(usuario.getOficina().getId().getCodoficina().toString());
        		reduccionCapitalBean.setNombreOficina(usuario.getOficina().getNomoficina());
        		reduccionCapitalBean.setDelegacion(usuario.getDelegacion().toString());
        	}
		}	
    	// Obtiene el listado de estados posibles para los Red. Capital
    	List<Estado> estados = new ArrayList<Estado>();
    	try {
			estados = declaracionesReduccionCapitalManager.getEstadosReduccionCapital();
		} catch (BusinessException e) {
			log ("cargarDatosFiltroBusqueda", "Ocurrio un error al obtener la lista de estados de Red. Capital", e);
		}
		
		// Obtiene el listado de riesgos posibles
    	
    	//P0079361 Cambiar para obtener el listado de riesgos de servicio SOAP
		//List<Riesgo> riesgos = reduccionCapitalUtilidadesService.getRiesgos();
    	String realPath = this.getServletContext().getRealPath(
				WEB_INF);
    	List<Riesgo> riesgos = new ArrayList<Riesgo>();
    	
    	//Causas causas = null;
		try {
			riesgos = anexoReduccionCapitalManager.obtenerAyudaCausaRC(realPath);
		} catch (Exception e) {
			logger.error("Error en cargarDatosFiltroBusqueda al cargar el listado de riesgos posibles", e);
		}
    	//grupo seguro>cod y desc
		//P0079361 Cambiar para obtener el listado de riesgos de servicio SOAP
    	
		//P0079361 listado estado CUPON RC mismo origen que AM
		List<EstadoCupon> estadosCupon = new ArrayList<com.rsi.agp.dao.tables.anexo.EstadoCupon>();
		try {
			estadosCupon = declaracionesModificacionPolizaManager.getEstadosCupon();    
		} catch (Exception e) {
			logger.error("Error en cargarDatosFiltroBusqueda al cargar el listado de estados de Cupon", e);
		}
		parameters.put("estadosCupon", estadosCupon);
		//P0079361 listado estado CUPON RC  mismo origen que AM
		
    	// Anhade el perfil del usuario a los parametros
    	parameters.put("perfil", perfil);
    	// Anhade el listado de estados de los Red. Capital a los parametros
    	parameters.put("estados", estados);
    	// Anhade el listado de riesgos posibles a los parametros
    	//P0079361
    	//parameters.put("riesgos", riesgos);
    	parameters.put("riesgos", riesgos);
    	//P0079361
    	// Anhade el parametro que indica si la llamada se ha hecho desde el menu lateral
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
    	// Anhade el parametro que indica si hay que mostrar alguna alerta en la pantalla
    	parameters.put("alerta", request.getParameter("alerta"));
    	// Anhade el parametro que indica si hay que mostrar algun mensaje en la pantalla
    	parameters.put("mensaje", request.getParameter("mensaje"));
    	parameters.put("externo",usuario.getExterno());
    	parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
    	parameters.put("grupoOficinas", StringUtils.toValoresSeparadosXComas (usuario.getListaCodOficinasGrupo(), false, false));
    	
    	//puede que falte mandar aqui la relacion de los valores para el jsp de listadoReduccionCapital de numero y estado de cupon
    	//o opciones para el desplegable retornadas por un servicio para filtara por id,ftp o todas
    	
    	return reduccionCapitalBean;
    	//P0079361
	}
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("ReduccionCapitalUtilidadesController." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("ReduccionCapitalUtilidadesController." + method + " - " + msg, e);
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
	 * @param ReduccionCapitalUtilidadesService
	 */
	public void setReduccionCapitalUtilidadesService(IReduccionCapitalUtilidadesService ReduccionCapitalUtilidadesService) {
		this.reduccionCapitalUtilidadesService = ReduccionCapitalUtilidadesService;
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
	 * @param ReduccionCapitalManager
	 */
	public void setReduccionCapitalManager(DeclaracionesReduccionCapitalManager reduccionCapitalManager) {
		this.declaracionesReduccionCapitalManager = reduccionCapitalManager;
	}

	public void setDeclaracionesReduccionCapitalManager(
			DeclaracionesReduccionCapitalManager declaracionesReduccionCapitalManager) {
		this.declaracionesReduccionCapitalManager = declaracionesReduccionCapitalManager;
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
	    List<ReduccionCapitalUtilidades> items;
	    
	    //P0079361
	    String estadoCuponRC = (String) request.getParameter("estadoCuponRC");
		String tipoEnvioRC = (String) request.getParameter("tipoEnvioRC");
		String fEEnvio = (String) request.getParameter("fEEnvio");
		String fEEnvioHasta = (String) request.getParameter("fEEnvioHasta");
		String fEdanio = (String) request.getParameter("fEdanio");
		String fEdanioHasta = (String) request.getParameter("fEdanioHasta");
		String fEEnvioPol = (String) request.getParameter("fEEnvioPol");
		String fEEnvioPolHasta = (String) request.getParameter("fEEnvioPolHasta");
		
		logger.debug("[DATOS FECHAS ] estadoCuponRC: " + estadoCuponRC +" / tipoEnvioRC: " + tipoEnvioRC + " / fEEnvio: " + fEEnvio + " / fEEnvioHasta: "
				+ fEEnvioHasta + " / fEdanio: " + fEdanio + " / fEdanioHasta: " + fEdanioHasta + " / fEEnvioPol: " + fEEnvioPol  + " / fEEnvioPolHasta: " + fEEnvioPolHasta);
		
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = reduccionCapitalUtilidadesService.getAllFilteredAndSorted(estadoCuponRC,tipoEnvioRC,fEEnvio,fEEnvioHasta,fEdanio,fEdanioHasta,fEEnvioPol,fEEnvioPolHasta);

	      //P0079361
	        
	        // Si hay registros, preparar los datos para la exportación a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoReduccionesCapital");
	            request.setAttribute("jasperPath", "informeJasper.listadoReduccionesCapital");

	            // Redirigir a la vista de exportación a Excel
	            return new ModelAndView("forward:/informes.html?method=doInformeListado");
	        }
	    } catch (BusinessException e) {
	        // Registrar el error si no se pudieron obtener los registros filtrados y ordenados
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
	    }

	    // Si no hay registros o se produce un error, devolver null
	    return null;
	}

	//P0079361
	public IAnexoReduccionCapitalManager getAnexoReduccionCapitalManager() {
		return anexoReduccionCapitalManager;
	}

	public void setAnexoReduccionCapitalManager(IAnexoReduccionCapitalManager anexoReduccionCapitalManager) {
		this.anexoReduccionCapitalManager = anexoReduccionCapitalManager;
	}
	

	public DeclaracionesModificacionPolizaManager getDeclaracionesModificacionPolizaManager() {
		return declaracionesModificacionPolizaManager;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}
	//P0079361
}
