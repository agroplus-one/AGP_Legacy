package com.rsi.agp.core.webapp.action;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.service.IGetTablaService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.comisiones.impagados.InformeImpagados;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesImpagadosController extends BaseMultiActionController {
	// Declaraciones
	private IGetTablaService informesImpagadosService;
	private IGenericoDao informesImpagadosDao;

	// -------------------------------------------------------------
	// Métodos set
	public void setInformesImpagadosService(
			IGetTablaService informesImpagadosService) {
		this.informesImpagadosService = informesImpagadosService;
	}

	public void setInformesImpagadosDao(IGenericoDao informesImpagadosDao) {
		this.informesImpagadosDao = informesImpagadosDao;
	}

	// ----------------------------------------------------------------

	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, InformeImpagados informeImpagadosBean) {

		ModelAndView mv = null;

		Usuario usuario = (Usuario) request.getSession()
				.getAttribute("usuario");
		/*
		 * Recoge los parámettros de lista de entidades y E-S según el perfil y
		 * guarda también éste
		 */		
		Map<String, Object> parameters = this.getParametrosEntidadUsuarioPorPerfil(usuario);
		asignaEntidadesPorperfil(usuario, informeImpagadosBean);
		
		String origenLlamada = request.getParameter("origenLlamada");
		String mayorIgual2015 = request.getParameter("mayorIgual2015");

		parameters.put("origenLlamada", origenLlamada);
		parameters.put("mayorIgual2015", mayorIgual2015);
		
		if (null!=mayorIgual2015 && mayorIgual2015.equalsIgnoreCase("true")){
			informeImpagadosBean.setEsMayorIgual2015(true);
		}else if (null!=mayorIgual2015 && mayorIgual2015.equalsIgnoreCase("false")) {
			informeImpagadosBean.setEsMayorIgual2015(false);
		}
		
		try {
			if (null == origenLlamada
					|| !origenLlamada
							.equalsIgnoreCase(Constants.ORIGEN_LLAMADA_MENU_GENERAL)) {
				String tablaHTML = getTablaHtml(request, response,
						informeImpagadosBean, usuario, origenLlamada);

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
						request.setAttribute("consultaImpagados", tablaHTML);
				}

				mv = new ModelAndView("moduloComisiones/informesImpagados",
						"informeImpagadosBean", informeImpagadosBean);
				mv.addAllObjects(parameters);
			} else {
				mv = new ModelAndView("moduloComisiones/informesImpagados",
						"informeImpagadosBean", informeImpagadosBean)
						.addAllObjects(parameters);
			}

		} catch (Exception e) {
			logger.error("Error en doConsulta de InformesImpagados", e);
		}

		return mv;
	}

	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			InformeImpagados informeImpagadosBean, Usuario usuario,
			String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario
				.getListaCodEntidadesGrupo();
		
		String tabla = informesImpagadosService.getTabla(request, response,
				informeImpagadosBean, origenLlamada, listaGrupoEntidades,
				informesImpagadosDao);
		return tabla;
	}
	
	//Procedimiento para asignarle los valores al bean. La lista de entidades y resto de
	//parametros se asignan en el método del multiaction getParametrosEntidadUsuarioPorPerfil
	private void asignaEntidadesPorperfil(Usuario usuario, InformeImpagados infImp) {
		String perfil = usuario.getPerfil();
		if ((Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES).equals(perfil)){
			if (usuario.getExterno().compareTo(new BigDecimal("1"))==0) {
				infImp.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad().longValue());
				infImp.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad().longValue());
				infImp.setCodentidad(usuario.getOficina().getEntidad().getCodentidad().longValue());
				infImp.setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
			}else {
				infImp.setCodentidad(usuario.getOficina().getEntidad().getCodentidad().longValue());
				infImp.setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
			}			
		}
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
		
		List<Serializable> items;
		try {
			items = informesImpagadosService.getAllFilteredAndSorted(informesImpagadosDao);
			if (items.size() != 0) {
				
				request.setAttribute("listado", items);
				request.setAttribute("nombreInforme", "ListadoImpagados");
				request.setAttribute("jasperPath", "informeJasper.listadoImpagados");
				
				return new ModelAndView("forward:/informes.html?method=doInformeListado");
			}
		} catch (BusinessException e) {
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
		}
				
		return null;
	}
}
