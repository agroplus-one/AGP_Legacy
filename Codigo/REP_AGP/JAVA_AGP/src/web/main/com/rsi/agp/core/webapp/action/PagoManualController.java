/**
 * 
 */
package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IPagoManualService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinasZona;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Zona;

/**
 * @author U029769
 *
 */
public class PagoManualController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(PagoManualController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IPagoManualService pagoManualService;
	private String successView;

	/**
	 * Realiza la consulta de oficinas que se ajustan al filtro de busqueda
	 * 
	 * @param request
	 * @param response
	 * @param oficinaBean
	 *            Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Oficina oficinaBean) {
		logger.debug("PagoManualController - doConsulta [INIT]");

		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		final Map<String, Object> parameters = new HashMap<String, Object>();
		String html = null;

		parameters.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));
		Oficina oficinaBusqueda = (Oficina) oficinaBean;
		
		/* Pet. 63701 ** MODIF TAM (30.07.2021) ** Inicio (Defecto 4)*/
		String zonaSelStr = request.getParameter("zonaSel");
		
		if (!StringUtils.nullToString(zonaSelStr).equals("")) {
			String zonasSel[] = zonaSelStr.split(",");

			if (zonasSel.length > 0) {
				Set<OficinasZona> ofZon= new HashSet<OficinasZona>();
				
				for (int i = 0; i < zonasSel.length; i++) {
					String zonaStr = zonasSel[i];
					String zona[] = zonaStr.split("-");
					OficinasZona oficinasZonaNew = new OficinasZona();
					BigDecimal codEntidad = new BigDecimal(zona[0]);
					BigDecimal codZona = new BigDecimal(zona[1]);
					oficinasZonaNew.getId().setCodentidad(codEntidad);
					oficinasZonaNew.getId().setCodzona(codZona);
					
					ofZon.add(oficinasZonaNew);
					oficinaBusqueda.setOficinasZona(ofZon);
				}
			}
		} 

		String origenLlamada = request.getParameter("origenLlamada");
		if (StringUtils.nullToString(origenLlamada).equals("")) {
			origenLlamada = (String) request.getAttribute("origenLlamada");
		}
		try {

			/* Pet. 63701 ** MODIF TAM (22.06.2021) ** Inicio */
			if (oficinaBean.getId().getCodentidad() != null) {
				parameters.put("listaZonasEnt",
						pagoManualService.obtenerListaZonas(oficinaBean.getId().getCodentidad()));
			} else {
				parameters.put("listaZonasEnt", pagoManualService.obtenerListaZonas(null));
			}

			if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				logger.debug("Comienza la busqueda de Oficinas");
				
				html = pagoManualService.getTablaOficinasPagoManual(request, response, oficinaBusqueda, origenLlamada);
				if (html == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este atributo
						request.setAttribute("consultaOficinasPagoManual", html);
				}
			}

			parameters.put("origenLlamada", origenLlamada);
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parameters.put("alerta", alerta);
			}
			if (mensaje != null) {
				parameters.put("mensaje", mensaje);
			}
			parameters.put("perfil", perfil);
			
			/* GDLD-63701 ** MODIF TAM (24.08.2021) ** Defecto 5 ** Inicio */
			parameters.put("zonaSel", zonaSelStr);
			
			
			/* GDLD-63701 ** MODIF TAM (27.08.2021) * Defecto 9 * Inicio*/
			if (origenLlamada == "doAlta") {
				String zonaSelStrAlta = request.getParameter("zonaSelAlta");
				parameters.put("zonaSelAlta", zonaSelStrAlta);
			}
				
			

			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redireccion y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);
			mv = new ModelAndView(successView, "oficinaBean", oficinaBean);
			mv.addAllObjects(parameters);

			logger.debug("PagoManualController - doConsulta [END]");

			return mv;

		} catch (Exception e) {
			logger.error("Excepcion : PagoManualController - doConsulta", e);
		}
		return null;
	}

	/**
	 * 
	 * 07/05/2014 U029769
	 * 
	 * @param request
	 * @param response
	 * @param oficinaBean
	 * @return
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, Oficina oficinaBean) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		List<Zona> zonaListSel = new ArrayList<Zona>();

		try {

			/* Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */
			String zonaSelStr = request.getParameter("zonaSelModif");

			if (!StringUtils.nullToString(zonaSelStr).equals("")) {
				String zonasSel[] = zonaSelStr.split(",");

				if (zonasSel.length > 0) {
					for (int i = 0; i < zonasSel.length; i++) {
						String zonaStr = zonasSel[i];
						String zona[] = zonaStr.split("-");
						Zona zonaNew = new Zona();
						BigDecimal codEntidad = new BigDecimal(zona[0]);
						BigDecimal codZona = new BigDecimal(zona[1]);
						zonaNew.getId().setCodentidad(codEntidad);
						zonaNew.getId().setCodzona(codZona);

						zonaListSel.add(zonaNew);
					}
				}
			} else {
				zonaListSel = null;
			}

			/* Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */

			if (!StringUtils.nullToString(oficinaBean.getId().getCodentidad()).equals("")
					&& !StringUtils.nullToString(oficinaBean.getId().getCodoficina()).equals("")
					&& !StringUtils.nullToString(oficinaBean.getPagoManual()).equals("")) {

				// DNF 4/12/2018
				if (oficinaBean.getIdgrupo() == null) {
					oficinaBean.setIdgrupo(BigDecimal.ZERO);
				}

				parameters = pagoManualService.editaOficina(oficinaBean, zonaListSel);
			} else {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			}
			parameters.put("showModificar", "true");
			request.setAttribute("origenLlamada", "doEditar");
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado en la edicion de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);
		}
		return mv;
	}

	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, Oficina oficinaBean) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;

		try {
			if (!StringUtils.nullToString(oficinaBean.getId().getCodentidad()).equals("")
					&& !StringUtils.nullToString(oficinaBean.getId().getCodoficina()).equals("")) {
				parameters = pagoManualService.borraOficina(oficinaBean);

			} else {
				parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.KO"));
			}
			request.setAttribute("origenLlamada", "doBorrar");
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado al borrar de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.borrar.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);
		}
		return mv;
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Oficina oficinaBean) {

		logger.debug("PagoManualController - doAlta [INIT]");

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;

		List<Zona> zonaListSel = new ArrayList<Zona>();

		/* Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */
		String zonaSelStr = request.getParameter("zonaSelAlta");

		if (!StringUtils.nullToString(zonaSelStr).equals("")) {
			String zonasSel[] = zonaSelStr.split(",");

			if (zonasSel.length > 0) {
				for (int i = 0; i < zonasSel.length; i++) {
					String zonaStr = zonasSel[i];
					String zona[] = zonaStr.split("-");
					Zona zonaNew = new Zona();
					BigDecimal codEntidad = new BigDecimal(zona[0]);
					BigDecimal codZona = new BigDecimal(zona[1]);
					zonaNew.getId().setCodentidad(codEntidad);
					zonaNew.getId().setCodzona(codZona);

					zonaListSel.add(zonaNew);
				}
			}
		} else {
			zonaListSel = null;
		}

		/* Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */

		try {
			if (!StringUtils.nullToString(oficinaBean.getId().getCodentidad()).equals("")
					&& !StringUtils.nullToString(oficinaBean.getId().getCodoficina()).equals("")
					&& !StringUtils.nullToString(oficinaBean.getPagoManual()).equals("")) {

				parameters = pagoManualService.altaOficina(oficinaBean, zonaListSel);

			} else {
				parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			}

			/* P0063701 ** MODIF TAM (25.08/2021) ** Defecto 6 ** Inicio */ 
			request.setAttribute("origenLlamada", "doAlta");
			
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado en el alta de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, oficinaBean).addAllObjects(parameters);
		}

		logger.debug("PagoManualController - doAlta [END]");

		return mv;
	}

	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, Oficina oficinaBean) {

		logger.debug("init - PagoManualController - doCambioMasivo");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		String esAdiccion_cm = StringUtils.nullToString(request.getParameter("adiccionMasiva"));
		/* Pet. 63701 ** MODIF TAM (05.07.2021) */
		/*
		 * Se incluyen las zonas en el cambio Masivo. Se sustituyen las zonas que tenga
		 * asignadas por las seleccionadas en el cambio masivo
		 */

		String listaZonasMarcadas = StringUtils.nullToString(request.getParameter("zonaSelcm"));
		List<Zona> zonaListSel = new ArrayList<Zona>();

		if (!StringUtils.nullToString(listaZonasMarcadas).equals("")) {
			String zonasSel[] = listaZonasMarcadas.split(",");

			if (zonasSel.length > 0) {
				for (int i = 0; i < zonasSel.length; i++) {
					String zonaStr = zonasSel[i];
					String zona[] = zonaStr.split("-");
					Zona zonaNew = new Zona();
					BigDecimal codEntidad = new BigDecimal(zona[0]);
					BigDecimal codZona = new BigDecimal(zona[1]);
					zonaNew.getId().setCodentidad(codEntidad);
					zonaNew.getId().setCodzona(codZona);

					zonaListSel.add(zonaNew);
				}
			}
		} else {
			zonaListSel = null;
		}

		try {
			if (esAdiccion_cm.equals("S")) {
				parameters = pagoManualService.adiccionMasiva(listaIdsMarcados_cm, oficinaBean, zonaListSel);
			} else {
				parameters = pagoManualService.cambioMasivo(listaIdsMarcados_cm, oficinaBean, zonaListSel);
			}

		} catch (Exception e) {
			logger.debug("Error inesperado en el Cambio Masivo de oficinas (PagoManualController) ", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));

		}
		Oficina cambioMasivoOficinaBean = pagoManualService.getCambioMasivoBeanFromLimit(
				(Limit) request.getSession().getAttribute("consultaOficinasPagoManual_LIMIT"));

		mv = doConsulta(request, response, cambioMasivoOficinaBean).addAllObjects(parameters);

		logger.debug("end - PagoManualController - doCambioMasivo");
		return mv;
	}

	/**
	 * DAA 28/05/2013 Obtenemos la lista de Entidades
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */

	public void doObtenerListaZonasEntidad(HttpServletRequest request, HttpServletResponse response,
			Oficina oficinaBean) throws Exception {
		logger.debug("PagoManualcontroller - doObtenerListaZonasEntidad [INIT]");
		JSONObject objeto = new JSONObject();

		try {

			String entidad = StringUtils.nullToString(request.getParameter("codent"));
			BigDecimal ent = new BigDecimal(entidad);

			List<Zona> ListZonas = pagoManualService.obtenerListaZonas(ent);
			objeto.put("listaZonasCM", ListZonas);

		} catch (Exception excepcion) {
			logger.error("Error al obtener la lista de Zonas por Entidad", excepcion);
			throw new Exception("Error al obtener la lista de Zonas por Entidad", excepcion);

		}
		getWriterJSON(response, objeto);
		logger.debug("PagoManualcontroller - doObtenerListaZonasEntidad [END]");

	}

	public ModelAndView doObtenerZonasEntidad(HttpServletRequest request, HttpServletResponse response,
			Oficina oficinaBean) {
		logger.debug("init - PagoManualController - doObtenerZonasEntidad");

		final Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;

		BigDecimal codEntidad = oficinaBean.getId().getCodentidad();

		try {
			/* Pet. 63701 ** MODIF TAM (22.06.2021) ** Inicio */
			parameters.put("listaZonasEnt", pagoManualService.obtenerListaZonas(codEntidad));
		} catch (Exception e) {
			logger.debug("Error inesperado en el Cambio Masivo de oficinas (PagoManualController) ", e);
			parameters.put("alerta", bundle.getString("mensaje.oficinaPagoManual.edicion.KO"));

		}

		mv = new ModelAndView(successView, "oficinaBean", oficinaBean);
		mv.addAllObjects(parameters);

		return mv;
	}
	
	/**
	 * Exporta el listado de oficinas de pago manual a Excel.
	 *
	 * @param request  El objeto HttpServletRequest que contiene los atributos de la solicitud.
	 * @param response El objeto HttpServletResponse que contiene los atributos de la respuesta.
	 * @return Un objeto ModelAndView que redirige al método 'doInformeListado' si el listado no está vacío, 
	 *         de lo contrario, retorna null.
	 */
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
	    List<Oficina> items;
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = pagoManualService.getAllFilteredAndSorted();

	        // Si hay registros, preparar los datos para la exportación a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoPagosManuales");
	            request.setAttribute("jasperPath", "informeJasper.listadoPagosManuales");

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

	public void setPagoManualService(IPagoManualService pagoManualService) {
		this.pagoManualService = pagoManualService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
