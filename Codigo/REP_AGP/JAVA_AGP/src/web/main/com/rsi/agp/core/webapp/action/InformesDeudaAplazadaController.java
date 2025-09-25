package com.rsi.agp.core.webapp.action;

import static com.rsi.agp.core.webapp.util.StringUtils.toValoresSeparadosXComas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.impl.IInformesDeudaAplazadaDao;
import com.rsi.agp.core.jmesa.service.IInformesDeudaAplazadaService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeDeudaAplazadaUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesDeudaAplazadaController extends BaseMultiActionController {

	private static final Log logger = LogFactory.getLog(InformesDeudaAplazadaController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IInformesDeudaAplazadaService informesDeudaAplazadaService;
	private IInformesDeudaAplazadaDao informesDeudaAplazadaDao;

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response,
			InformeDeudaAplazadaUnificado infDeudaBean) throws Exception {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		String perfil = "";
		String nomEntidad = "";
		String nomLinea = "";
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		/*
		 * Recoge los parámettros de lista de entidades y E-S según el perfil y guarda
		 * también éste
		 */
		Map<String, Object> paramsUsuario = this.getParametrosEntidadUsuarioPorPerfil(usuario);
		// asignaEntidadesPorperfil(usuario, infDeudaBean);

		String origenLlamada = request.getParameter("origenLlamada");

		InformeDeudaAplazadaUnificado infDeudaAplazada = null;
		Long idDeuda = null;

		// PERFILES
		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		parametros.put("externo", paramsUsuario.get("externo"));
		if (new Integer(perfil).intValue() == Constants.COD_PERFIL_1) {
			infDeudaBean.setCodentidad(
					Integer.parseInt(usuario.getSubentidadMediadora().getEntidad().getCodentidad().toString()));
			nomEntidad = usuario.getSubentidadMediadora().getEntidad().getNomentidad();
			if (usuario.isUsuarioExterno()) {
				infDeudaBean.setEntmediadora(Integer.parseInt(paramsUsuario.get("entMed").toString()));
				infDeudaBean.setSubentmediadora(Integer.parseInt(paramsUsuario.get("subEntMed").toString()));
			}
		} else if (new Integer(perfil).intValue() == Constants.COD_PERFIL_5) {
			// Si el usuario es perfil 5 se pasa a la jsp la lista de entidades
			// pertenecientes a su grupo de entidades
			parametros.put("grupoEntidades",
					Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())
							? toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false)
							: "");
		}
		if (new Integer(perfil).intValue() != Constants.COD_PERFIL_1 && infDeudaBean.getCodentidad() != null) {

			nomEntidad = informesDeudaAplazadaService.getNombreEntidad(infDeudaBean.getCodentidad(),
					informesDeudaAplazadaDao);
			parametros.put("nomEntidad", nomEntidad);
		}

		// FIN PERFILES

		if (infDeudaBean.getLinea() != null) {
			nomLinea = informesDeudaAplazadaService.getNombreLinea(infDeudaBean.getLinea(), informesDeudaAplazadaDao);
			parametros.put("nomEntidad", nomEntidad);
			parametros.put("nomLinea", nomLinea);
		}

		try {
			if (null == origenLlamada || !origenLlamada.equalsIgnoreCase(Constants.ORIGEN_LLAMADA_MENU_GENERAL)) {
				String tablaHTML = "";
				List<InformeDeudaAplazadaUnificado> lstItems = new ArrayList<InformeDeudaAplazadaUnificado>();
				TableFacade tableFacade = new TableFacade("consultaDeudaAplazada", request);
				Limit limit = tableFacade.getLimit();
				if (limit.isExported()) {
					lstItems = getListado(request, response, infDeudaBean, usuario, origenLlamada);

					// llamada a informe Excel
					request.setAttribute("lstItems", lstItems);
					parametros.put("isExterno", nomLinea);
					mv = new ModelAndView("forward:/informes.html?method=doInformeConsultaDeudaAplazada");
					return mv;
				} else {

					tablaHTML = getTablaHtml(request, response, infDeudaBean, usuario, origenLlamada);
				}
				if (tablaHTML == null) {
					return null;
				} else {
					String ajax = request.getParameter("ajax");
					if (ajax != null && ajax.equals("true")) {
						byte[] contents = tablaHTML.getBytes("UTF-8");
						response.getOutputStream().write(contents);
						return null;
					} else
						// Pasa a la jsp el codigo de la tabla a traves de este
						// atributo
						request.setAttribute("consultaDeudaAplazada", tablaHTML);
				}

				mv = new ModelAndView("moduloComisiones/informesDeudaAplazada", "infDeudaBean", infDeudaBean);
				mv.addAllObjects(parametros);
			} else {
				mv = new ModelAndView("moduloComisiones/informesDeudaAplazada", "infDeudaBean", infDeudaBean)
						.addAllObjects(parametros);
			}

		} catch (Exception e) {
			logger.error("Error en doConsulta de InformesDeudaAplazada", e);
		}

		return mv;
	}

	private String getTablaHtml(HttpServletRequest request, HttpServletResponse response,
			InformeDeudaAplazadaUnificado infDeudaBean, Usuario usuario, String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario.getListaCodEntidadesGrupo();
		String perfil = usuario.getPerfil().substring(4);
		String tabla = informesDeudaAplazadaService.getTabla(request, response, infDeudaBean, origenLlamada,
				listaGrupoEntidades, perfil, usuario.isUsuarioExterno(), informesDeudaAplazadaDao);
		return tabla;
	}

	private List<InformeDeudaAplazadaUnificado> getListado(HttpServletRequest request, HttpServletResponse response,
			InformeDeudaAplazadaUnificado infDeudaBean, Usuario usuario, String origenLlamada) {
		List<InformeDeudaAplazadaUnificado> lstItems = new ArrayList<InformeDeudaAplazadaUnificado>();
		List<BigDecimal> listaGrupoEntidades = usuario.getListaCodEntidadesGrupo();
		String perfil = usuario.getPerfil().substring(4);
		lstItems = informesDeudaAplazadaService.getListado(request, response, infDeudaBean, origenLlamada,
				listaGrupoEntidades, perfil, usuario.isUsuarioExterno(), informesDeudaAplazadaDao);
		return lstItems;
	}

	public void setInformesDeudaAplazadaService(IInformesDeudaAplazadaService informesDeudaAplazadaService) {
		this.informesDeudaAplazadaService = informesDeudaAplazadaService;
	}

	public void setInformesDeudaAplazadaDao(IInformesDeudaAplazadaDao informesDeudaAplazadaDao) {
		this.informesDeudaAplazadaDao = informesDeudaAplazadaDao;
	}

	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {

		List<Serializable> items;
		try {
			items = informesDeudaAplazadaService.getAllFilteredAndSorted(informesDeudaAplazadaDao);
			if (items.size() != 0) {

				request.setAttribute("listado", items);
				request.setAttribute("nombreInforme", "ListadoDeudaAplazada");
				request.setAttribute("jasperPath", "informeJasper.listadoDeudaAplazada");

				return new ModelAndView("forward:/informes.html?method=doInformeListado");
			}
		} catch (BusinessException e) {
			logger.error("Error al obtener todos los registros filtrados y ordenados", e);
		}

		return null;
	}

}