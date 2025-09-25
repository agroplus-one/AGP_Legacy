package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ICargasCondicionadoService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cargas.CargasCondicionado;

public class CargasCondicionadoController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(CargasCondicionadoController.class);
	private ICargasCondicionadoService cargasCondicionadoService;
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	/**
	 * Realiza la consulta de cargas de condicionado
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, CargasCondicionado cargasCondicionadoBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");

		logger.info("doConsulta - Comienza la búsqueda de cargas de condicionado");
		String html = cargasCondicionadoService.getTablaCargasCondicionado(request, response, cargasCondicionadoBean, origenLlamada);
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
					logger.error("Error al pintar la tabla", e);
				} catch (IOException e) {
					logger.error("Error al pintar la tabla", e);
				}

				return null;
			} else
				// Pasa a la jsp el código de la tabla a través de este atributo
				request.setAttribute("listadoCargasCondicionado", html);
		}
		if (!StringUtils.nullToString(request.getAttribute("mensaje")).equals("")) {
			parameters.put("mensaje", (String) request.getAttribute("mensaje"));
		}
		if (!StringUtils.nullToString(request.getAttribute("alerta")).equals("")) {
			parameters.put("alerta", (String) request.getAttribute("alerta"));
		}
		return new ModelAndView(successView, "cargasCondicionadoBean", cargasCondicionadoBean).addAllObjects(parameters);
	}
	/**
	 * Borra una carga de condicionado
	 * @param request
	 * @param response
	 * @param cargasCondicionadoBean
	 * @return
	 */
	public ModelAndView doBorrarCondicionado(HttpServletRequest request, HttpServletResponse response, CargasCondicionado cargasCondicionadoBean) {
		ModelAndView mv = null;
		String idCondicionado = request.getParameter("idCondicionado");
		try {
			cargasCondicionadoService.borraCondicionado(Long.parseLong(idCondicionado));
		} catch (BusinessException e) {
			logger.error("Error al borrar el condicionado ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_BORRADO_CONDICIONADO_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		} catch (Exception e) {
			logger.error("Error al borrar el fichero ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_BORRADO_CONDICIONADO_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		}
		request.setAttribute("mensaje", bundle.getObject(Constants.MENSAJE_BORRADO_CONDICIONADO_OK));
		mv = doConsulta(request, response, new CargasCondicionado());

		return mv;

	}
	
	/**
	 * Edita una carga de condicionado
	 * @param request
	 * @param response
	 * @param cargasCondicionadoBean
	 * @return
	 */
	public ModelAndView doEditarCondicionado(HttpServletRequest request, HttpServletResponse response, CargasCondicionado cargasCondicionadoBean) {
		ModelAndView mv = null;
		String idCondicionado = request.getParameter("idCondicionado");
		request.getSession().setAttribute("idCondicionado", Long.parseLong(idCondicionado));
		try {
			// Cambiamos el estado a "abierta"
			cargasCondicionadoService.cambiaEstadoCarga(Long.parseLong(idCondicionado), Constants.ESTADO_CARGA_ABIERTA);
		} catch (BusinessException e) {
			logger.error("Error al editar la carga del condicionado ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_EDITA_CONDICIONADO_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		} catch (Exception e) {
			logger.error("Error al editar la carga del condicionado ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_EDITA_CONDICIONADO_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		}
		mv = new ModelAndView("redirect:/cargasFicheros.run").addObject("method", "doConsulta");
		return mv;
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param cargasCondicionadoBean
	 * @return
	 */
	public ModelAndView doConsultarCondicionado(HttpServletRequest request, HttpServletResponse response, CargasCondicionado cargasCondicionadoBean) {
		ModelAndView mv = null;
		HashMap<String, String> parameters = new HashMap<String, String>();
		String idCondicionado = request.getParameter("idCondicionado");
		request.getSession().setAttribute("idCondicionado", Long.parseLong(idCondicionado));
		
		parameters.put("origenLlamada", "consulta");
		mv = new ModelAndView("redirect:/cargasFicheros.run").addObject("method", "doConsulta").addAllObjects(parameters);
		return mv;
	}
	
	/**
	 * Cierra una carga de condicionado
	 * @param request
	 * @param response
	 * @param cargasCondicionadoBean
	 * @return
	 */
	public ModelAndView doCerrarCarga(HttpServletRequest request, HttpServletResponse response, CargasCondicionado cargasCondicionadoBean) {
		ModelAndView mv = null;
		String idCondicionado = request.getParameter("idCondicionado");
		try {
			cargasCondicionadoService.cambiaEstadoCarga(Long.parseLong(idCondicionado), Constants.ESTADO_CARGA_CERRADA);
		} catch (BusinessException e) {
			logger.error("Error al cerrar la carga del condicionado ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_CARGA_CERRRADA_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		} catch (Exception e) {
			logger.error("Error al cerrar la carga del condicionado ", e);
			request.setAttribute("alerta", bundle.getObject(Constants.ALERTA_CARGA_CERRRADA_KO));
			mv = doConsulta(request, response, new CargasCondicionado());
		}
		request.setAttribute("mensaje", bundle.getObject(Constants.MENSAJE_CARGA_CERRRADA_OK));
		mv = doConsulta(request, response, new CargasCondicionado());

		return mv;
	}
	
	/* Inyeccion de Spring */
	public void setCargasCondicionadoService(ICargasCondicionadoService cargasCondicionadoService) {
		this.cargasCondicionadoService = cargasCondicionadoService;
	}
	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
