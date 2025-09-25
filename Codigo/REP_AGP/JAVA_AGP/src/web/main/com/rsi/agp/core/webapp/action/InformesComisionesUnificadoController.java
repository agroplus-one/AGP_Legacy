package com.rsi.agp.core.webapp.action;

import java.io.Serializable;
import java.math.BigDecimal;
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
import com.rsi.agp.core.jmesa.service.IInformesComisionesUnificadoService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.informes.InformeComisionesUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformesComisionesUnificadoController extends
		BaseMultiActionController {
	
	private static final Log logger = LogFactory.getLog(IncidenciasComisionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IInformesComisionesUnificadoService informesComisionesUnificadoService;
	private IGenericoDao informesComisionesUnificadoDao;
	

	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, InformeComisionesUnificado informe)
			throws Exception {
		logger.debug("init - doConsulta");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		try {		
			String origenLlamada = request.getParameter("origenLlamada");
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			parametros=this.getParametrosEntidadUsuarioPorPerfil(usuario);	
			asignaEntidadesPorperfil(usuario, informe, parametros);
			//asignaNulosEmpty(fichero);
			//String descripcionEstadoFichero =this.getDescripcionEstadoFichero(fichero.getFicheroUnificado().getEstado());
			
			if(StringUtils.nullToString(origenLlamada).compareTo(Constants.ORIGEN_LLAMADA_MENU_GENERAL)!=0) {		
				parametros.put("origenLlamada", origenLlamada);
				String tablaHTML = getTablaHtml(request, response, informe, usuario, origenLlamada);
	
					if (tablaHTML == null) {
						return null;
					} else {
						String ajax = request.getParameter("ajax");
						if (ajax != null && ajax.equals("true")) {
							byte[] contents = tablaHTML.getBytes("UTF-8");	
							//String x= new String(contents, "UTF-8");
							response.getOutputStream().write(contents);
							return null;
						} else
							// Pasa a la jsp el codigo de la tabla a traves de este
							// atributo
							request.setAttribute("consultaInformeComisionesUnificado", tablaHTML);
					}
					mv = new ModelAndView("moduloComisionesUnificado/InformeComisionesUnificado", "informeBean", informe).addAllObjects(parametros);
			}else {
				parametros.put("origenLlamada", Constants.ORIGEN_LLAMADA_CONSULTAR);
				mv = new ModelAndView("moduloComisionesUnificado/InformeComisionesUnificado",
						"informeBean", informe).addAllObjects(parametros);
			}				
		
		} catch (Exception be) {
			logger.error("Se ha producido un error", be);
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			//mv = doConsulta(request, response, new FicheroUnificado());
		}
		return mv.addAllObjects(parametros);
	}

	//Procedimiento para asignarle los valores al bean. La lista de entidades y resto de
		//parametros se asignan en el método del multiaction getParametrosEntidadUsuarioPorPerfil
		private void asignaEntidadesPorperfil(Usuario usuario, InformeComisionesUnificado informe,Map <String, Object> parametros) {
			String perfil = usuario.getPerfil();
			if ((Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES).equals(perfil)){
				if (usuario.getExterno().compareTo(new BigDecimal("1"))==0) {
					
					informe.setEntmediadora(usuario.getSubentidadMediadora().getId().getCodentidad().intValue());
					informe.setSubentmediadora(usuario.getSubentidadMediadora().getId().getCodsubentidad().intValue());
					informe.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
					informe.setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
				}else {
					informe.setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
					informe.setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
				}
			}			
		}
	
	private String getTablaHtml(HttpServletRequest request,
			HttpServletResponse response,
			InformeComisionesUnificado informe, Usuario usuario,
			String origenLlamada) {

		List<BigDecimal> listaGrupoEntidades = usuario
				.getListaCodEntidadesGrupo();
		
		String tabla = informesComisionesUnificadoService.getTabla(request, response,
				informe, origenLlamada, listaGrupoEntidades,
				informesComisionesUnificadoDao,usuario);
		return tabla;
	}

	public void setInformesComisionesUnificadoService(
			IInformesComisionesUnificadoService informesComisionesUnificadoService) {
		this.informesComisionesUnificadoService = informesComisionesUnificadoService;
	}

	public void setInformesComisionesUnificadoDao(
			IGenericoDao informesComisionesUnificadoDao) {
		this.informesComisionesUnificadoDao = informesComisionesUnificadoDao;
	}
	
	public ModelAndView doExportToExcel(HttpServletRequest request, HttpServletResponse response) {
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		List<Serializable> items;
		try {
			items = informesComisionesUnificadoService.getAllFilteredAndSorted(informesComisionesUnificadoDao);
			if (items.size() != 0) {
				
				request.setAttribute("listado", items);
				request.setAttribute("nombreInforme", "ListadoComisionesUnificado");
				request.setAttribute("jasperPath", "informeJasper.listadoComisionesUnificado");
				request.setAttribute("PERFIL", usuario.getPerfil().substring(4));
				request.setAttribute("EXTERNO", usuario.isUsuarioExterno());
				
				return new ModelAndView("forward:/informes.html?method=doInformeListadoComisiones");
			}
		} catch (BusinessException e) {
	        logger.error("Error al obtener todos los registros filtrados y ordenados", e);
		}
				
		return null;
	}
	
}
