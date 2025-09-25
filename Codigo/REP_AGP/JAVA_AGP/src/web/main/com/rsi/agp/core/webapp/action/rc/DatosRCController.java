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
import com.rsi.agp.core.jmesa.service.IDatosRCService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.rc.DatosRC;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;
import com.rsi.agp.dao.tables.rc.SumaAseguradaRC;

public class DatosRCController extends MultiActionController {

	private static final Log LOGGER = LogFactory
			.getLog(DatosRCController.class);

	private IDatosRCService datosRCService;
	private String successView;

	public ModelAndView doConsulta(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC) {

		LOGGER.debug("init - DatosRCController");

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String html = null;
		String origenLlamada = request.getParameter("origenLlamada");

		try {

			cargaParametrosComunes(request, parametros);

			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {

				logger.debug("Comienza la busqueda de ...");

				html = this.datosRCService.getTablaDatosRC(request, response,
						datosRC, origenLlamada);

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
						request.setAttribute("listaDatosRC", html);
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

			logger.error("Error en doConsulta de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}

	public ModelAndView doAlta(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		String origenLlamada = request.getParameter("origenLlamada");

		try {

			cargaParametrosComunes(request, parametros);

			parametros.put("origenLlamada", origenLlamada);

			String errorMsg = this.datosRCService.validateDatosRC(datosRC);

			if (errorMsg != null && !"".equals(errorMsg)) {

				request.setAttribute("alerta", errorMsg);
				
				return doConsulta(request, response, datosRC);

			} else {

				DatosRC resultBean = this.datosRCService.grabarDatosRC(datosRC);

				request.setAttribute("mensaje",
						"Datos para RC grabados correctamente.");

				return doConsulta(request, response, resultBean);
			}
		} catch (Exception e) {

			logger.error("Error en doAlta de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}

	public ModelAndView doModificar(final HttpServletRequest request,
			final HttpServletResponse response, final DatosRC datosRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		String origenLlamada = request.getParameter("origenLlamada");

		try {

			cargaParametrosComunes(request, parametros);

			parametros.put("origenLlamada", origenLlamada);

			String errorMsg = this.datosRCService.validateDatosRC(datosRC);

			if (errorMsg != null && !"".equals(errorMsg)) {

				request.setAttribute("alerta", errorMsg);
				
				return doConsulta(request, response, datosRC);

			} else {

				DatosRC resultBean = this.datosRCService.modificarDatosRC(datosRC);

				request.setAttribute("mensaje",
						"Datos para RC modificados correctamente.");

				return doConsulta(request, response, resultBean);
			}
		} catch (Exception e) {

			logger.error("Error en doModificar de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doBorrar(final HttpServletRequest request,
			final HttpServletResponse response,
			final DatosRC datosRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);

			this.datosRCService.borrarDatosRC(datosRC.getId());
			
			request.setAttribute("mensaje", "Dato para RC borrados correctamente.");

			return doConsulta(request, response, datosRC);

		} catch (Exception e) {

			logger.error("Error en doBorrar de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doReplicar(final HttpServletRequest request,
			final HttpServletResponse response,
			final DatosRC datosRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);
			
			BigDecimal planDest = new BigDecimal (request.getParameter("planreplica"));
			BigDecimal lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			
			String errorMsg = this.datosRCService.validateReplicaDatosRC(planDest, lineaDest);

			if (errorMsg != null && !"".equals(errorMsg)) {

				parametros.put("alerta", errorMsg);

			} else {

				this.datosRCService.replicaDatosRC(datosRC.getLinea()
						.getCodplan(), datosRC.getLinea().getCodlinea(),
						planDest, lineaDest);
				
				request.setAttribute("mensaje", "Datos para RC replicados correctamente.");
				
				datosRC.getLinea().setCodplan(planDest);
				datosRC.getLinea().setCodlinea(lineaDest);
				
				return doConsulta(request, response, datosRC);
			}			
		} catch (Exception e) {

			logger.error("Error en doReplicar de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	
	public ModelAndView doCambioMasivo(final HttpServletRequest request,
			final HttpServletResponse response,
			final DatosRC datosRC) {

		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();

		try {

			cargaParametrosComunes(request, parametros);
			
			BigDecimal tasaCM = StringUtils.isNullOrEmpty(request
					.getParameter("tasaCM")) ? null : new BigDecimal(
					request.getParameter("tasaCM"));
			BigDecimal franquiciaCM = StringUtils.isNullOrEmpty(request
					.getParameter("franquiciaCM")) ? null : new BigDecimal(
					request.getParameter("franquiciaCM"));
			BigDecimal primaMinimaCM = StringUtils.isNullOrEmpty(request
					.getParameter("primaMinimaCM")) ? null : new BigDecimal(
					request.getParameter("primaMinimaCM"));
			
			String listaIdsMarcados = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
			String[] idsMarcadosStrArr = listaIdsMarcados.split(",");
			Long[] idsMarcadosLngArr = new Long[idsMarcadosStrArr.length];
			for (int i = 0; i < idsMarcadosStrArr.length; i++) {
				idsMarcadosLngArr[i] = Long.valueOf(idsMarcadosStrArr[i]);
			}
			
			this.datosRCService.cambioMasivoDatosRC(idsMarcadosStrArr, tasaCM,
					franquiciaCM, primaMinimaCM);
			
			return doConsulta(request, response, datosRC);
						
		} catch (Exception e) {

			logger.error("Error en doCambioMasivo de DatosRCController", e);
			parametros
					.put("alerta",
							"Error no esperado. Por favor, contacte con su administrador.");
		}

		mv = new ModelAndView(successView, "datosRC", datosRC);
		mv.addAllObjects(parametros);

		return mv;
	}
	

	private void cargaParametrosComunes(final HttpServletRequest request,
			final Map<String, Object> parametros) throws BusinessException {

		// ESPECIES PARA RC (PARA COMBO)
		Collection<EspeciesRC> especiesRC = this.datosRCService.getEspeciesRC();
		parametros.put("listaEspeciesRC", especiesRC);

		// REGIMENES PARA RC (PARA COMBO)
		Collection<RegimenRC> regimenesRC = this.datosRCService
				.getRegimenesRC();
		parametros.put("listaRegimenesRC", regimenesRC);

		// SUMAS ASEGURADAS PARA RC (PARA COMBO)
		Collection<SumaAseguradaRC> sumasAseguradasRC = this.datosRCService
				.getSumasAseguradasRC();
		parametros.put("listaSumasAseguradasRC", sumasAseguradasRC);
	}

	public void setDatosRCService(final IDatosRCService datosRCService) {
		this.datosRCService = datosRCService;
	}

	public void setSuccessView(final String successView) {
		this.successView = successView;
	}
}