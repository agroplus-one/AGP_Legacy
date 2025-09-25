/*
 **************************************************************************************************
 *
 *  CREACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034              Antonio Serrano   Controlador para las peticiones via Ajax
 *
 **************************************************************************************************
 */

package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.AjaxManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class AjaxController extends BaseSimpleController implements Controller {

	private static final Log LOGGER = LogFactory.getLog(AjaxController.class);
	private AjaxManager ajaxManager;
	private PolizaManager polizaManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public AjaxController() {
		setCommandClass(String.class);
		setCommandName("string");
	}

	@Override
	public ModelAndView handle(final HttpServletRequest request,
			final HttpServletResponse response, final Object object,
			final BindException exception) {

		final String operacion = request.getParameter("operacion");

		if (operacion.equalsIgnoreCase("ajax_getLineas")) {
			ajax_getLineas(request, response);
		} else if ("ajax_getTabla".equalsIgnoreCase(operacion)) {
			ajax_getData(request, response);
		} else if ("ajax_getNumInstalaciones".equalsIgnoreCase(operacion)) {
			ajax_getNumInstalaciones(request, response);
		} else if ("ajax_getServerTime".equalsIgnoreCase(operacion)) {
			ajax_getServerTime(request, response);
		} else if ("ajax_getLstTotalProdParcelas".equalsIgnoreCase(operacion)) {
			ajax_getLstTotalProdParcelas(request, response);
		} else if ("ajax_getTablasPendientes".equalsIgnoreCase(operacion)) {
			ajax_getTablasPendientes(request, response);
		}

		return null;
	}

	/**
	 * DAA 29/01/13 Convierte a un JSON la lista de tablas que faltan para
	 * completar el proceso de Activacion de la linea
	 * 
	 * @param request
	 * @param response
	 */
	private void ajax_getTablasPendientes(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String lineaSeguroId = StringUtils.nullToString(request
					.getParameter("lineaseguroId"));
			JSONArray list = new JSONArray();
			List<String> listTablas = ajaxManager.getTablasPendientes(new BigDecimal(
					lineaSeguroId));

			for (int i = 0; i < listTablas.size(); i++) {
				list.put(listTablas.get(i));
			}

			getWriterJSON(response, list);
		} catch (Exception excepcion) {
			logger.error(
					"Error al recuperar las tablas que faltan para completar el proceso de Activacion",
					excepcion);
		}

	}

	/**
	 * Metodo para obtener un listado de objetos para mostrar en una lupa
	 * 
	 * @param request
	 *            Peticion
	 * @param response
	 *            Respuesta
	 *
	 */
	private void ajax_getData(HttpServletRequest request,
			HttpServletResponse response) {
		String objeto = request.getParameter("objeto");
		Integer posicion = new Integer(request.getParameter("posicion"));
		String[] filtro = request.getParameterValues("filtro");
		String[] campoFiltro = request.getParameterValues("campoFiltro");
		String[] tipoFiltro = request.getParameterValues("tipoFiltro");
		String[] campoListado = request.getParameterValues("campoListado");
		String[] valorDepende = request.getParameterValues("valorDepende");
		String[] tipoBeanDepende = request.getParameterValues("tipoDepende");
		String[] campoBeanDepende = request.getParameterValues("campoBeanDepende");
		String[] campoBeanDistinct = request.getParameterValues("campoBeanDistinct");
		String[] campoBeanIsNull = request.getParameterValues("campoBeanIsNull");
		String[] campoLeftJoin = request.getParameterValues("campoLeftJoin");
		String[] campoRestrictions = request.getParameterValues("campoRestriccion");
		String[] valorRestrictions= request.getParameterValues("valorRestriccion");
		String[] operadorRestrictions= request.getParameterValues("operadorRestriccion");
		String[] tipoValorRestrictions= request.getParameterValues("tipoValorRestriccion");
		String[] valorDependeGenerico= request.getParameterValues("valorDependeGenerico");
		String[] valorDependeOrdenGenerico= request.getParameterValues("valorDependeOrdenGenerico");
		String acumularResultados= request.getParameter("acumularResultado");
		
	
		String objetoActual = request.getParameter("objetoActual");
		String campoOrden = request.getParameter("campoOrden");
		String tipoOrden = request.getParameter("tipoOrden");
		String direccion = request.getParameter("direccion");

		LOGGER.debug("Llamada al metodo generico para obtener el listado de "
				+ objeto);
		JSONObject contenedor = this.ajaxManager.getData(objeto, posicion,
				filtro, campoFiltro, tipoFiltro, campoListado, valorDepende,
				campoBeanDepende, tipoBeanDepende, objetoActual, campoOrden,
				tipoOrden, direccion, campoBeanDistinct,campoBeanIsNull, 
				campoLeftJoin, campoRestrictions, valorRestrictions, 
				operadorRestrictions,tipoValorRestrictions,
				valorDependeGenerico, valorDependeOrdenGenerico,acumularResultados);

		getWriterJSON(response, contenedor);
	}

	
	private void ajax_getLstTotalProdParcelas(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String idPoliza = StringUtils.nullToString(request
					.getParameter("idPoliza"));
			Poliza polizaBean = ajaxManager.getPoliza(Long.parseLong(idPoliza));
			List<List<String>> lstParcelasPopUp = new ArrayList<List<String>>();
			lstParcelasPopUp = polizaManager.getTotalProdParcelas(polizaBean);
			// pintar la tabla
			String tabla = "";
			tabla = "<div class='panelInformacion_content'>";
			tabla += "<div id='panelInformacion' class='panelInformacion'>";
			tabla += "<table align='center'>";
			int countFila = 0;
			int countCol = 0;
			Iterator<List<String>> it = lstParcelasPopUp.iterator();
			while (it.hasNext()) {
				List<String> linea = it.next();
				countCol = 0;
				tabla += "<tr>";
				for (int i = 0; i < linea.size(); i++) {
					String elemento = linea.get(i);
					if (countFila == 0) {
						tabla += "<td class='literal' style='font-weight: bold;'><u>"
								+ elemento + "</u></td>";
					} else {
						if (countFila % 2 == 0) {
							if (countFila != 0 && countCol > 3) {
								if (countFila != 0
										&& countCol == (linea.size() - 1)) {
									tabla += "<td class='literal' style='text-align:right' style='color: #0033FF;'>"
											+ elemento + "</td>";
								} else {
									tabla += "<td class='literal' style='text-align:right' style='color: green;'>"
											+ elemento + "</td>";
								}
							} else {
								tabla += "<td class='literal' style='text-align:right' right;'>"
										+ elemento + "</td>";
							}
						} else {
							if (countFila != 0 && countCol > 3) {
								if (countFila != 0
										&& countCol == (linea.size() - 1)) {
									tabla += "<td class='literal' style='text-align:right' style='color: #0033FF;background-color:#F3F3F3;'>"
											+ elemento + "</td>";
								} else {
									tabla += "<td class='literal' style='text-align:right' style='color: green;background-color:#F3F3F3;'>"
											+ elemento + "</td>";
								}
							} else {
								tabla += "<td class='literal' style='text-align:right' style='background-color:#F3F3F3;'>"
										+ elemento + "</td>";
							}
						}
					}
					countCol++;
				}
				tabla += "</tr>";
				countFila++;
			}
			tabla += "</table>";
			tabla += "</div>";
			tabla += "<div style='margin-top: 15px'><a class='bot' href='javascript:cerrarPopUpTotalProd()' title='Cerrar'>Cerrar</a>";
			tabla += "</div>";
			tabla += "</div>";

			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new String(tabla.getBytes(), "UTF-8"));

		} catch (Exception excepcion) {
			logger.error("Error en el metodo ajax_getLstTotalProdParcelas",
					excepcion);
		}
	}

	private void ajax_getServerTime(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			Calendar calendario = Calendar.getInstance();

			int hora = calendario.get(Calendar.HOUR_OF_DAY);
			int minutos = calendario.get(Calendar.MINUTE);
			int segundos = calendario.get(Calendar.SECOND);

			String serverTime = hora + ":" + minutos + ":" + segundos;

			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(serverTime);
		} catch (Exception excepcion) {
			logger.error("Error en el metodo ajax_getLstTotalProdParcelas",
					excepcion);
		}
	}

	@SuppressWarnings("unchecked")
	private void ajax_getLineas(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String plan = StringUtils
					.nullToString(request.getParameter("Plan"));
			JSONObject element = null;
			JSONArray list = new JSONArray();
			List<Linea> listLineas = ajaxManager
					.getLineas(new BigDecimal(plan));

			for (Linea linea : listLineas) {
				element = new JSONObject();
				element.put("value", linea.getLineaseguroid());
				element.put("nodeText",
						linea.getCodlinea() + " - " + linea.getNomlinea());
				list.put(element);
			}

			HttpSession session = request.getSession();
			session.setAttribute("listalineas", listLineas); // metemos en
																// sesion la
																// lista de
																// lineas

			getWriterJSON(response, list);
		} catch (Exception excepcion) {
			logger.error("Excepcion : AjaxController - ajax_getLineas", excepcion);
		}
	}

	private final void ajax_getNumInstalaciones(
			final HttpServletRequest request, HttpServletResponse response) {
		try {
			String idParcela = request.getParameter("idParcela");

			Long num = Long.valueOf(ajaxManager.getNumInstalaciones(new Long(
					idParcela)));

			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(num.toString());
		} catch (Exception excepcion) {
			logger.error("Excepcion : AjaxController - ajax_getNumInstalaciones", excepcion);
		}
	}

	public void setAjaxManager(AjaxManager ajaxManager) {
		this.ajaxManager = ajaxManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
}
