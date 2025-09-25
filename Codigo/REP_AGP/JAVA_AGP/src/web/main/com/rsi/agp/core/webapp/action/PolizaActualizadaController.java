package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.IPolizaActualizadaService;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PolizaActualizadaController extends BaseMultiActionController{

	private Log logger = LogFactory.getLog(PolizaActualizadaController.class);
	private IPolizaActualizadaService polizaActualizadaService;
	private String successView;

	/**
	 * Realiza la consulta de polizas actualizadas que se ajustan al filtro de búsqueda
	 * 
	 * @param request
	 * @param response
	 *         Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean) {
		logger.debug("init - PolizaActualizadaController " + polizaBean.getAsegurado().getNifcif());
    	
    	// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
    	final String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parámetros que se pasarán a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	// Variable que almacena el codigo de la tabla de polizas
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	if(null!=origenLlamada) {
	    	if(origenLlamada.equals("paginacion")) {
	    		if(null!=polizaBean.getLinea()) {
	    			polizaBean.getLinea().setCodplan(new BigDecimal(request.getParameter("codplan")));
	    			polizaBean.getLinea().setCodlinea(new BigDecimal(request.getParameter("codlinea")));    			
	    		}
	    		if (null!= polizaBean.getAsegurado()) {
	    			polizaBean.getAsegurado().setNifcif(request.getParameter("nifasegurado"));
	    		}
	    	}
    	}
    	
    	// Carga de entidad asociada al usuario para filtrar por ella por defecto
		//cargarEntidad(request, usuario, perfil, polizaBusqueda);    	
		
    	// ---------------------------------------------------------------------------------
    	// -- Búsqueda de polizas para sobreprecio y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------
    	logger.debug("Comienza la búsqueda de polizas");
    	html = polizaActualizadaService.getTablaPolizas(request, response, polizaBean, origenLlamada);
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
					return null;
				} catch (IOException e) {
					logger.error("IOExcepcion : PolizaActualizadaController - doConsulta", e);
				}
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("polizas", html);
			}
		}
    	
		// ----------------------------------
    	// -- Carga del mapa de parámetros --
        // ----------------------------------
		
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
		parameters.put("idpoliza", polizaBean.getIdpoliza());
		parameters.put("descargarCopy", request.getParameter("descargarCopy"));
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView, "polizaBean", polizaBean);
       	mv.addAllObjects(parameters);
       	
       	logger.debug("end - PolizaActualizadaController");
    	
    	return mv;
    }

	public void setPolizaActualizadaService(
			IPolizaActualizadaService polizaActualizadaService) {
		this.polizaActualizadaService = polizaActualizadaService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
}
