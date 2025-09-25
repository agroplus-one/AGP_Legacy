package com.rsi.agp.core.webapp.action.mtoinf;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoEntidadesAccesoRestringidoService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public class MtoEntidadesAccesoRestringidoController extends BaseMultiActionController {
	
	private IMtoEntidadesAccesoRestringidoService mtoEntidadesAccesoRestringidoService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		
		logger.debug("init - MtoEntidadesAccesoRestringidoController - doConsulta");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		// ---------------------------------------------------
    	// --  Busqueda de entidades con acceso restringido --
        // ---------------------------------------------------
    	logger.debug("Comienza la busqueda de entidades con acceso restringido");    	
    	
    	String html = mtoEntidadesAccesoRestringidoService.getTablaEntidadesAccesoRestringido(request, response, entidadAccesoRestringido);
        
		if (html == null) {
			return null; // an export
		} else {
			String ajax = request.getParameter("ajax");
			// Llamada desde ajax
			if (ajax != null && ajax.equals("true")) {
				try {
					byte[] contents = html.getBytes("UTF-8");
					response.getOutputStream().write(contents);
				} catch (Exception e) {
					logger.error("Ocurrio un error al escribir la salida en el response", e);
				}

				return null;
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("entidadAccesoRestringidoListado", html);
			}
		}
		
		// Se anhade como parametros los codigos que indican el nivel de acceso
		parametros.put("codDenegado", ConstantsInf.ACCESO_DENEGADO);
		parametros.put("codPermitido", ConstantsInf.ACCESO_PERMITIDO);
		parametros.put("codPermitidoConcreto", ConstantsInf.ACCESO_PERMITIDO_USU_PER_CONCRETOS);
		// Anhade el parametro que indica si la llamada se ha hecho desde el menu lateral
		parametros.put("origenLlamada", request.getParameter("origenLlamada"));
		
		logger.debug("fin - MtoEntidadesAccesoRestringidoController - doConsulta");
		
		return new ModelAndView (successView, "entidadAccesoRestringido", entidadAccesoRestringido).addAllObjects(parametros);
	}
	
	/**
	 * Da de alta el registro indicado en 'entidadAccesoRestringido'
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		
		logger.debug("init - MtoEntidadesAccesoRestringidoController - doAlta");
		
		Map<String, Object> parametros = mtoEntidadesAccesoRestringidoService.altaEntidadAccesoRestringido(entidadAccesoRestringido);
		
		logger.debug("fin - MtoEntidadesAccesoRestringidoController - doAlta");
		
		return doConsulta(request, response, new EntidadAccesoRestringido()).addAllObjects(parametros);
	}
	
	/**
	 * Da de baja el registro indicado en 'entidadAccesoRestringido'
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		
		logger.debug("init - MtoEntidadesAccesoRestringidoController - doBaja");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		// A partir del id, carga el objeto correspondiente
		EntidadAccesoRestringido e = mtoEntidadesAccesoRestringidoService.getEntidadAccesoRestringido(entidadAccesoRestringido.getId());
		
		// Si el objeto se ha cargado correctamente
		if (e != null) {
			// Llamada al metodo que da de baja el registro
			boolean resultado = mtoEntidadesAccesoRestringidoService.bajaEntidadAccesoRestringido(e);
			
			// Dependiendo del resultado se mostrara un mensaje
			if (resultado) parametros.put("mensaje", bundle.getObject(ConstantsInf.MSG_ENT_ACCESO_RESTRINGIDO_BAJA_OK));
			else parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_BAJA_KO));
		}
		else {
			logger.debug("Ocurrio un error al cargar el objeto entidad con acceso restringido");
			parametros.put("alerta", bundle.getObject(ConstantsInf.ALERTA_ENT_ACCESO_RESTRINGIDO_BAJA_KO));
		}
		
		logger.debug("fin - MtoEntidadesAccesoRestringidoController - doBaja");
		
		return doConsulta(request, response, new EntidadAccesoRestringido()).addAllObjects(parametros);
	}
	
	/**
	 * Modifica el registro indicado en 'entidadAccesoRestringido'
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido) {
		
		logger.debug("init - MtoEntidadesAccesoRestringidoController - doEdita");
		
		Map<String, Object> parametros = mtoEntidadesAccesoRestringidoService.editaEntidadAccesoRestringido(entidadAccesoRestringido);
		
		logger.debug("fin - MtoEntidadesAccesoRestringidoController - doEdita");
		
		return doConsulta(request, response, new EntidadAccesoRestringido()).addAllObjects(parametros);
	}
	
	/**
	 * Setter de propiedad para Spring
	 * @param mtoEntidadesAccesoRestringidoService
	 */
	public void setMtoEntidadesAccesoRestringidoService(IMtoEntidadesAccesoRestringidoService mtoEntidadesAccesoRestringidoService) {
		this.mtoEntidadesAccesoRestringidoService = mtoEntidadesAccesoRestringidoService;
	}

	/**
	 * Setter de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
}
