/**
 * 
 */
package com.rsi.agp.core.webapp.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.dao.IMtoZonasDao;
import com.rsi.agp.core.jmesa.dao.impl.MtoZonasDao;
import com.rsi.agp.core.jmesa.service.IMtoZonasService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Zona;

/**
 * @author U028975 (T-Systems) GDLD-63701 - Mantenimiento de Zonas
 */
public class MtoZonasController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(MtoZonasController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IMtoZonasService mtoZonasService;
	private String successView;
	

	/**
	 * Realiza la consulta de Zona que se ajustan al filtro de busqueda U028975
	 * (T-Systems)
	 * 
	 * @param request
	 * @param response
	 * @param zonaBean
	 * @return
	 */

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, Zona zonaBean) {
		logger.debug("init - MtoZonasController");

		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final String perfil = usuario.getPerfil().substring(4);
		final Map<String, Object> parameters = new HashMap<String, Object>();

		String html = null;

		parameters.put("perfil", perfil);
		parameters.put("grupoEntidades",
				StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false));

		String origenLlamada = request.getParameter("origenLlamada");
		if (StringUtils.nullToString(origenLlamada).equals("")) {
			origenLlamada = (String) request.getAttribute("origenLlamada");
		}

		try {
			logger.debug("Comienza la busqueda de Zonas");

			html = mtoZonasService.getTablaZonas(request, response, zonaBean, origenLlamada);
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
					request.setAttribute("consultaZonas", html);
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

			parameters.put("esExterno", usuario.getExterno());

			if (zonaBean.getId().getCodentidad() != null) {
				String nombEntidad = mtoZonasService.getNombEntidad(zonaBean.getId().getCodentidad());
				parameters.put("desc_entidad", nombEntidad);
			}

			// -----------------------------------------------------------------
			// -- Se crea el objeto que contiene la redireccion y se devuelve --
			// -----------------------------------------------------------------
			ModelAndView mv = new ModelAndView(successView);

			mv = new ModelAndView(successView, "zonaBean", zonaBean);
			mv.addAllObjects(parameters);

			logger.debug("end - MtoZonasController");

			return mv;

		} catch (Exception e) {
			logger.error("Excepcion : MtoZonasController - doConsulta", e);
		}
		return null;
	}

	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, Zona zonaBean) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {
			if (!StringUtils.nullToString(zonaBean.getId().getCodentidad()).equals("")
					&& !StringUtils.nullToString(zonaBean.getId().getCodzona()).equals("")) {
				parameters = mtoZonasService.borraZona(zonaBean);
			} else {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.KO"));
			}
			request.setAttribute("origenLlamada", "doBorrar");
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado al borrar la zona", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.borrar.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);
		}
		return mv;
	}

	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, Zona zonaBean) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {

			if (estanRellenosCampos(zonaBean)) {
				parameters = mtoZonasService.editaZona(zonaBean, request);
			} else {
				parameters.put("alerta", bundle.getString("mensaje.mtoZonas.edicion.KO"));
			}
			parameters.put("showModificar", "true");
			request.setAttribute("origenLlamada", "doEditar");

			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.edicion.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado en la edicion Zonas", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoZonas.edicion.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);
		}
		return mv;
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, Zona zonaBean) {

		logger.debug("MtoZonasController - doAlta [INIT]");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {

			if (estanRellenosCampos(zonaBean)) {
				parameters = mtoZonasService.altaZona(zonaBean);
			} else {
				parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			}
			parameters.put("showModificar", "true");
			parameters.put("id.codentidad", zonaBean.getId().getCodentidad());
			parameters.put("id.codzona", zonaBean.getId().getCodzona());
			request.setAttribute("origenLlamada", "doAlta");
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);

		} catch (Exception e) {
			logger.debug("Error inesperado en la edicion de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.alta.generico.KO"));
			mv = doConsulta(request, response, zonaBean).addAllObjects(parameters);
		}
		logger.debug("MtoZonasController - doAlta [END]");
		return mv;
	}

	private boolean estanRellenosCampos(Zona zonaBean) {

		if (!StringUtils.nullToString(zonaBean.getId().getCodentidad()).equals("")
				&& !StringUtils.nullToString(zonaBean.getId().getCodzona()).equals("")
				&& !StringUtils.nullToString(zonaBean.getNomzona()).equals("")) {
			return true;
		}
		return false;
	}
	
	/*
	 * 
	 */
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
	    List<Zona> items;
	    try {
	        // Obtener todos los registros filtrados y ordenados
	        items = mtoZonasService.getAllFilteredAndSorted();

	        // Si hay registros, preparar los datos para la exportación a Excel
	        if (items.size() != 0) {
	            request.setAttribute("listado", items);
	            request.setAttribute("nombreInforme", "ListadoZonas");
	            request.setAttribute("jasperPath", "informeJasper.listadoZonas");

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

	public void setMtoZonasService(IMtoZonasService mtoZonasService) {
		this.mtoZonasService = mtoZonasService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
