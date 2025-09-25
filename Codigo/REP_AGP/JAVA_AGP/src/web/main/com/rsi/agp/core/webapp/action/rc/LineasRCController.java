package com.rsi.agp.core.webapp.action.rc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ILineasRCService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.LineasRC;

public class LineasRCController extends MultiActionController {

	private static final Log LOGGER = LogFactory.getLog(LineasRCController.class);

	private ILineasRCService lineasRCService;
	private String successView;

	public ModelAndView doConsulta(final HttpServletRequest request,
			final HttpServletResponse response,
			final LineasRC lineasRC) {

		LOGGER.debug("init - LineasRCController");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String html = null;
		String origenLlamada = request.getParameter("origenLlamada");

		try {
			
			cargaParametrosComunes(request, parametros);

			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				
				logger.debug("Comienza la busqueda de líneas de Responsabilidad Civil");

				html = this.lineasRCService.getTablaLineasRC(request, response,
						lineasRC, origenLlamada);

				if (html == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("listaLineasRC", html);
				}
			}

			parametros.put("origenLlamada", origenLlamada);

			String mensaje = request.getParameter("mensaje") == null ? (String) request
					.getAttribute("mensaje") : request.getParameter("mensaje");
			String alerta = request.getParameter("alerta") == null ? (String) request
					.getAttribute("alerta") : request.getParameter("alerta");
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
		} catch (Exception e) {

			logger.error("Error en doConsulta de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "lineasRC", lineasRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doAlta(final HttpServletRequest request,
			final HttpServletResponse response,
			final LineasRC lineasRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String origenLlamada = request.getParameter("origenLlamada");

		try {
			
			cargaParametrosComunes(request, parametros);
			
			parametros.put("origenLlamada", origenLlamada);

			String errorMsg = this.lineasRCService.validateLineaRC(lineasRC);

			if (errorMsg != null) {

				request.setAttribute("alerta", errorMsg);
				
				return doConsulta(request, response, lineasRC);

			} else {

				LineasRC resultBean = this.lineasRCService.grabarLineaRC(lineasRC);

				request.setAttribute("mensaje", "Línea para RC grabada correctamente.");
				
				return doConsulta(request, response, resultBean);
			}
		} catch (Exception e) {

			logger.error("Error en doAlta de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "lineasRC", lineasRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doModificar(final HttpServletRequest request,
			final HttpServletResponse response,
			final LineasRC lineasRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String origenLlamada = request.getParameter("origenLlamada");

		try {

			cargaParametrosComunes(request, parametros);
			
			parametros.put("origenLlamada", origenLlamada);

			String errorMsg = this.lineasRCService.validateLineaRC(lineasRC);

			if (errorMsg != null && !"".equals(errorMsg)) {

				request.setAttribute("alerta", errorMsg);
				
				return doConsulta(request, response, lineasRC);

			} else {

				LineasRC resultBean = this.lineasRCService.modificarLineaRC(lineasRC);

				request.setAttribute("mensaje", "Línea para RC modificada correctamente.");
				
				return doConsulta(request, response, resultBean);
			}
		} catch (Exception e) {

			logger.error("Error en doModificar de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "lineasRC", lineasRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doBorrar(final HttpServletRequest request,
			final HttpServletResponse response,
			final LineasRC lineasRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);

			this.lineasRCService.borrarLineaRC(lineasRC.getId());
			
			request.setAttribute("mensaje", "Línea para RC borrada correctamente.");

			return doConsulta(request, response, lineasRC);

		} catch (Exception e) {

			logger.error("Error en doBorrar de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "lineasRC", lineasRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doReplicar(final HttpServletRequest request,
			final HttpServletResponse response,
			final LineasRC lineasRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);
			
			BigDecimal planDest = new BigDecimal (request.getParameter("planreplica"));
			BigDecimal lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			
			String errorMsg = this.lineasRCService.validateReplicaLineaRC(planDest, lineaDest);

			if (errorMsg != null && !"".equals(errorMsg)) {

				parametros.put("alerta", errorMsg);

			} else {

				this.lineasRCService.replicaLineaRC(lineasRC.getLinea()
						.getCodplan(), lineasRC.getLinea().getCodlinea(),
						planDest, lineaDest);
				
				request.setAttribute("mensaje", "Línea para RC replicada correctamente.");
				
				lineasRC.getLinea().setCodplan(planDest);
				lineasRC.getLinea().setCodlinea(lineaDest);
				
				return doConsulta(request, response, lineasRC);
			}			
		} catch (Exception e) {

			logger.error("Error en doReplicar de LineasRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "lineasRC", lineasRC);
		mv.addAllObjects(parametros);

		return mv;
	}

	private void cargaParametrosComunes(final HttpServletRequest request,
			final Map<String, Object> parametros) throws BusinessException {

		// ESPECIES PARA RC (PARA COMBO)
		Collection<EspeciesRC> especiesRC = this.lineasRCService
				.getEspeciesRC();
		parametros.put("listaEspeciesRC", especiesRC);
	}

	public void setLineasRCService(final ILineasRCService lineasRCService) {
		this.lineasRCService = lineasRCService;
	}

	public void setSuccessView(final String successView) {
		this.successView = successView;
	}
}