package com.rsi.agp.core.webapp.action.rc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.jmesa.service.IImpuestosRCService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;

public class ImpuestosRCController extends MultiActionController {
	
	private static final String ERROR_TOTAL = "Error no esperado. Por favor, contacte con su administrador.";

	private static final Log LOGGER = LogFactory.getLog(ImpuestosRCController.class);
	
	private IImpuestosRCService impuestosRCService;
	private String successView;

	public ModelAndView doConsulta(final HttpServletRequest req,
			final HttpServletResponse res, ImpuestosRC impuestos) {
		
		LOGGER.debug("init - ImpuestosRCController.doConsulta");
		Map<String, Object> params = new HashMap<String, Object>();
		String origenLlamada = req.getParameter("origenLlamada");

		try {
			if (!StringUtils.equals(origenLlamada, "menuGeneral")) {
				String html = this.impuestosRCService.getTablaImpuestos(req, impuestos, origenLlamada);
				if (html == null) {
					return null;
				} else {
					String ajax = req.getParameter("ajax");
					if (StringUtils.equals(ajax, "true")) {
						byte[] contents = html.getBytes(Constants.DEFAULT_ENCODING);
						res.getOutputStream().write(contents);
						return null;
					} else {
						req.setAttribute("listaImpuestosRC", html);
					}
				}
			}
			params.put("origenLlamada", origenLlamada);
			String mensaje = req.getParameter("mensaje") == null ? (String) req
					.getAttribute("mensaje") : req.getParameter("mensaje");
			String alerta = req.getParameter("alerta") == null ? (String) req
					.getAttribute("alerta") : req.getParameter("alerta");
			if (alerta != null) {
				params.put("alerta", alerta);
			}
			if (mensaje != null) {
				params.put("mensaje", mensaje);
			}
		} catch (Exception e) {
			LOGGER.error("Error en ImpuestosRCController.doConsulta", e);
			params.put("alerta", ERROR_TOTAL);
		}
		ModelAndView mv = new ModelAndView(successView, "impuestosRC", impuestos).addAllObjects(params);
		return mv;
	}

	public ModelAndView doAlta(final HttpServletRequest req,
			final HttpServletResponse res, ImpuestosRC impuestos) {

		Map<String, Object> params = new HashMap<String, Object>();
		String origenLlamada = req.getParameter("origenLlamada");
		try {
			params.put("origenLlamada", origenLlamada);			
			String errorMsg = this.impuestosRCService.validarImpuestosRC(impuestos);
			if (!StringUtils.equals(errorMsg, "")) {
				req.setAttribute("alerta", errorMsg);
				return doConsulta(req, res, impuestos);
			} else {
				ImpuestosRC impuestosBean = this.impuestosRCService.guardarImpuesto(impuestos);
				req.setAttribute("mensaje", "Impuesto para RC grabados correctamente.");
				return doConsulta(req, res, impuestosBean);
			}
		} catch (Exception e) {
			LOGGER.error("Error en ImpuestosRCController.doAlta", e);
			params.put("alerta", ERROR_TOTAL);
		}
		ModelAndView mv = new ModelAndView(successView, "impuestosRC", impuestos).addAllObjects(params);
		return mv;
	}

	public ModelAndView doModificar(final HttpServletRequest req,
			final HttpServletResponse res, ImpuestosRC impuestos) {
		Map<String, Object> params = new HashMap<String, Object>();
		String origenLlamada = req.getParameter("origenLlamada");
		try {
			params.put("origenLlamada", origenLlamada);
			String errorMsg = this.impuestosRCService.validarImpuestosRC(impuestos);
			if (!errorMsg.equals("")) {
				req.setAttribute("alerta", errorMsg);
				return doConsulta(req, res, impuestos);
			} else {
				ImpuestosRC resultBean = this.impuestosRCService.modificarImpuesto(impuestos);
				req.setAttribute("mensaje", "Impuesto RC modificado correctamente.");
				return doConsulta(req, res, resultBean);
			}
		} catch (Exception e) {
			LOGGER.error("Error en ImpuestosRCController.doModificar", e);
			params.put("alerta",	ERROR_TOTAL);
		}
		return new ModelAndView(successView, "impuestosRC", impuestos).addAllObjects(params);
	}

	public ModelAndView doEliminar(final HttpServletRequest req,
			final HttpServletResponse res, ImpuestosRC impuestos) {
		Map<String, Object> params = new HashMap<String, Object>();
		try {			
			this.impuestosRCService.borrarImpuesto(impuestos.getId());
			req.setAttribute("mensaje", "Impuesto RC borrado correctamente.");
			return doConsulta(req, res, impuestos);
		} catch (Exception e) {
			LOGGER.error("Error en ImpuestosRCController.doBorrar", e);
			params.put("alerta", ERROR_TOTAL);
		}
		ModelAndView mv = new ModelAndView(successView, "impuestosRC", impuestos).addAllObjects(params);
		return mv;
	}

	public ModelAndView doReplicar(final HttpServletRequest req,
			final HttpServletResponse res, ImpuestosRC impuestos) {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			if(impuestos.getCodPlan() != null){
				BigDecimal planDest = new BigDecimal(req.getParameter("planreplica"));
				BigDecimal planOrig = impuestos.getCodPlan();
				String errorMsg = this.impuestosRCService.validarImpuestosRCReplica(planDest);
				if (!errorMsg.equals("")) {
					params.put("alerta", errorMsg);
				} else {
					this.impuestosRCService.replicarImpuestosRC(planOrig, planDest);
					req.setAttribute("mensaje", "Impuestos para RC replicados correctamente.");
					impuestos.setCodPlan(planDest);
					return doConsulta(req, res, impuestos);
				}
			} else {
				params.put("alerta", "El plan de origen no está definido.");
			}
		} catch (Exception e) {
			LOGGER.error("Error en ImpuestosRCController.doReplicar", e);
			params.put("alerta", ERROR_TOTAL);
		}
		ModelAndView mv = new ModelAndView(successView, "impuestosRC", impuestos).addAllObjects(params);
		return mv;
	}
	
	public void setImpuestosRCService(IImpuestosRCService impuestosRCService) {
		this.impuestosRCService = impuestosRCService;
	}
	
	public void setSuccessView(String successView){
		this.successView = successView;
	}
}
