package com.rsi.agp.core.webapp.action.utilidades;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.utilidades.IAnexoModificacionUtilidadesService;
import com.rsi.agp.core.managers.impl.AnexoModificacionManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.commons.Usuario;

public class AnexoModificacionUtilidadesController extends MultiActionController{
	
	private Log logger = LogFactory.getLog(AnexoModificacionUtilidadesController.class);
	private static final String OPERACION = "operacion";
	private IAnexoModificacionUtilidadesService anexoModificacionUtilidadesService;
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private AnexoModificacionManager anexoModificacionManager;
	private String successView;
	
	
	/**
	 * Realiza la consulta de Anexo de modificacion que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param campoCalculadoBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que contiene la redireccion a la pagina listado de anexo de modificacion
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response,
				AnexoModificacion anexoModificacionBean) {
		
		log ("doConsulta", "inicio");
				
    	// Map para guardar los parametros que se pasaran a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	// Comprueba si la llamada se ha hecho desde el menu lateral
    	boolean deMenuLateral = request.getParameter("origenLlamada") != null;
    	// Comprueba si es la primera busqueda que se realiza
    	String primeraBusqueda = request.getParameter("primeraBusqueda");
    	// Comprueba si se esta volviendo de alguna pagina de acciones de Anexos de modificacion
    	boolean volver = request.getParameter("volver") != null;
    	
    	String accion = StringUtils.nullToString(request.getParameter(OPERACION));
    	
    	// Si vuelve, se intenta cargar el filtro de busqueda que haya en sesion
    	if (volver) {
    		if (request.getSession().getAttribute("anexoModificacionBean") != null)
    			anexoModificacionBean = (AnexoModificacion) request.getSession().getAttribute("anexoModificacionBean");
    		
		}
    	// -----------------------------------------------------------------
    	// -- Busqueda de Anexos de mod. que se ajsutan al filtro de busqueda --
        // -----------------------------------------------------------------
    	
    	// Carga el grupo de entidades asociadas al usuario si es de perfil 5
    	parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
    	// Carga el grupo de entidades asociadas al usuario si es de perfil 5
    	parameters.put("grupoOficinas", StringUtils.toValoresSeparadosXComas (usuario.getListaCodOficinasGrupo(), false, false));
    	parameters.put("capitalAseguradoBean", new CapitalAsegurado());    	
    	parameters.put("vieneDeListadoAnexosMod", "true");
    	
		if ("imprimirInforme".equals(accion)){				
			try {
				return doExportToExcel(request);
			} catch (Exception e) {
				logger.error("Error al gererar el informe de utilidades de pólizas", e);
			}
    	// Si la llamada se ha hecho desde el menu lateral no se realiza la busqueda
		} else if (!deMenuLateral || volver) {
    		
    		// Guarda el objeto de busqueda en sesion
    		request.getSession().setAttribute("anexoModificacionBean", anexoModificacionBean);
    		
	    	log("doConsulta", "Comienza la busqueda de Anexos de Modificacion");
	    	String html = anexoModificacionUtilidadesService.getTablaAnexoModificacion(request, response, anexoModificacionBean, primeraBusqueda, usuario.getListaCodEntidadesGrupo(),usuario.getListaCodOficinasGrupo());
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
					request.setAttribute("listadoAnexoModificacion", html);
				}
			}
    	}
    	
    	log ("doConsulta", "Carga los datos necesarios para la jsp y el filtro de busqueda");
    	AnexoModificacion anexoModificacionBusq=  cargarDatosFiltroBusqueda (parameters, request, volver, anexoModificacionBean, deMenuLateral);  
    	
    	log ("doConsulta", "Crea la redireccion a jsp");
    	ModelAndView mv = new ModelAndView(successView);
    	mv = new ModelAndView(successView, "anexoModificacionBean", anexoModificacionBusq);
       	
    	String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
    	mv.addAllObjects(parameters);
		
		log ("doConsulta", "fin");
		
		return mv;
	}
	
	
	public void doCambiarIBAN(final HttpServletRequest request, final HttpServletResponse response) {
		
		JSONObject params = new JSONObject();
		
		try{
			String nuevoIBAN = request.getParameter("nuevoIBAN");
			String idAnexoStr = request.getParameter("idAnexo");
			Long idAnexo = new Long(idAnexoStr);
			
			/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
			String nuevoIBAN2 = request.getParameter("nuevoIBAN2");
			
			AnexoModificacion anexoModificacion = anexoModificacionManager.obtenerAnexoModificacionById(idAnexo);
			if (!StringUtils.isNullOrEmpty(nuevoIBAN)) {
				anexoModificacion.setIbanAsegModificado(nuevoIBAN);
				anexoModificacion.setEsIbanAsegModificado(1);
			}
					
			
			if (!StringUtils.isNullOrEmpty(nuevoIBAN2)) {
				anexoModificacion.setIban2AsegModificado(nuevoIBAN2);
				anexoModificacion.setEsIban2AsegModificado(1);
			}
			/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */

			
			anexoModificacionManager.guardarAnexoModificacion(anexoModificacion);
			
			params.put("cambioIBANValido", "true");
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
		}
		catch(Exception e){
			logger.error("doCambiarIBAN - Ocurrio un error inesperado.", e);
			try {
				params.put("cambioIBANValido", "false");
			} catch (JSONException e1) {}
		    response.setCharacterEncoding("UTF-8");
		    this.getWriterJSON(response, params);
    	}
	}




	//M�TODOS PRIVADOS
	
	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONObject listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			logger.warn("Fallo al escribir la lista en el contexto", e);
		}
	}
	
	/**
	 * Carga los parametros que se van a enviar a la jsp al redireccionar
	 * @param parameters
	 * @param request
	 * @param plzDeSesion
	 * @param anexoModificacionBean
	 * @param deMenuLateral
	 * @return
	 */
	private AnexoModificacion cargarDatosFiltroBusqueda(Map<String, Object> parameters, HttpServletRequest request,	boolean volver, AnexoModificacion anexoModificacionBean,
			boolean deMenuLateral) {
		
		// Obtiene el usuario de la sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	
    	// Si viene del menu lateral se crea el objeto
    	if (deMenuLateral) anexoModificacionBean = new AnexoModificacion();
    	
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
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							anexoModificacionBean.getPoliza().setOficina(usuario.getOficina().getId().getCodoficina().toString());
							anexoModificacionBean.getPoliza().setNombreOfi(usuario.getOficina().getNomoficina());
							break;
					case Constants.COD_PERFIL_3:	
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							anexoModificacionBean.getPoliza().setOficina(usuario.getOficina().getId().getCodoficina().toString());
							anexoModificacionBean.getPoliza().setNombreOfi(usuario.getOficina().getNomoficina());
							break;				
					case Constants.COD_PERFIL_4:
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							anexoModificacionBean.getPoliza().getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
							anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
							anexoModificacionBean.getPoliza().setUsuario(usuario);
							break;
					default:
							break;
				}
			}
		} 
    	//campos por defecro en caso de usuario externo
        if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
        	if (usuario.getTipousuario().compareTo(Constants.PERFIL_1)==0) {
        		anexoModificacionBean.getPoliza().getColectivo().getTomador().getId().
        			setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		anexoModificacionBean.getPoliza().getColectivo().getTomador().getEntidad().
        			setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		
        		anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().
        			setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
        		
        		anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().
        			setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        	}else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3)==0) {
        		
        		anexoModificacionBean.getPoliza().getColectivo().getTomador().getId().
    				setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		
        		anexoModificacionBean.getPoliza().getColectivo().getTomador().getEntidad().
    				setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		
        		anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().
    				setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
    		
        		anexoModificacionBean.getPoliza().getColectivo().getSubentidadMediadora().getId().
    				setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        		
        		anexoModificacionBean.getPoliza().setOficina(usuario.getOficina().getId().getCodoficina().toString());
        		anexoModificacionBean.getPoliza().setNombreOfi(usuario.getOficina().getNomoficina());
        		anexoModificacionBean.getPoliza().getUsuario().setDelegacion(usuario.getDelegacion());
        	}
		}	
    		
    	// Obtiene el listado de estados posibles para Anexos Modificacion
    	List<com.rsi.agp.dao.tables.anexo.Estado> estados = new ArrayList<com.rsi.agp.dao.tables.anexo.Estado>();
    	try {
			estados = declaracionesModificacionPolizaManager.getEstadosAnexosModificacion();
		} catch (BusinessException e) {
			log ("cargarDatosFiltroBusqueda", "Ocurrio un error al obtener la lista de estados de Red. Capital", e);
		}
    	
    	// Obtiene el listado de estados posibles para el cupón
    	List<EstadoCupon> estadosCupon = declaracionesModificacionPolizaManager.getEstadosCupon();    
		
		// Anade el perfil del usuario a los parametros
    	parameters.put("perfil", perfil);
    	// Anade el listado de estados de los AM a los parametros
    	parameters.put("estados", estados);
    	// Anade el listado de estados del cupón a los parámetros
    	parameters.put("estadosCupon", estadosCupon);
    	// Anade el parametro que indica si la llamada se ha hecho desde el menu lateral
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
    	// Anade el parametro que indica si hay que mostrar alguna alerta en la pantalla
    	parameters.put("alerta", request.getParameter("alerta"));
    	// Anade el parametro que indica si hay que mostrar algun mensaje en la pantalla
    	parameters.put("mensaje", request.getParameter("mensaje"));
    	parameters.put("externo",usuario.getExterno());
    	parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
    	
    	return anexoModificacionBean;
	}


	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("AnexosModificacionUtilidadesController." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("AnexosModificacionUtilidadesController." + method + " - " + msg, e);
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
	 * Setter de la propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setanexoModificacionUtilidadesService(
			IAnexoModificacionUtilidadesService anexoModificacionUtilidadesService) {
		this.anexoModificacionUtilidadesService = anexoModificacionUtilidadesService;
	}

	public void setDeclaracionesModificacionPolizaManager(
			DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}


	public void setAnexoModificacionManager(AnexoModificacionManager anexoModificacionManager) {
		this.anexoModificacionManager = anexoModificacionManager;
	}

	public ModelAndView doExportToExcel(HttpServletRequest request) {
	    List<AnexoModificacion> items;
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = anexoModificacionUtilidadesService.getAllFilteredAndSorted();

	        // Si hay registros, preparar los datos para la exportaci�n a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoAnexosModificacion");
	            request.setAttribute("jasperPath", "informeJasper.listadoAnexosModificacion");

	            // Redirigir a la vista de exportaci�n a Excel
	            return new ModelAndView("forward:/informes.html?method=doInformeListado");
	        }
	    } catch (BusinessException e) {
	        // Registrar el error si no se pudieron obtener los registros filtrados y ordenados
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
	    }

	    // Si no hay registros o se produce un error, devolver null
	    return null;
	}

	
}
