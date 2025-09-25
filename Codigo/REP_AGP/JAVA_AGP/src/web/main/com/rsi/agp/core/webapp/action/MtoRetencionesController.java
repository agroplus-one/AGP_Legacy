package com.rsi.agp.core.webapp.action;

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
import org.jmesa.limit.Limit;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoRetencionesDao;
import com.rsi.agp.core.jmesa.service.IMtoRetencionesService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
//import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.comisiones.Retencion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;

public class MtoRetencionesController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(MtoRetencionesController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IMtoRetencionesService mtoRetencionesService;
	private String successView;
	
	private final static String VACIO = "";
	
	public ModelAndView doConsulta (HttpServletRequest request, 
			HttpServletResponse response,Retencion retencionBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		String perfil = "";
		String html = null;
		ModelAndView mv = new ModelAndView(successView);
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
				
		String origenLlamada = request.getParameter("origenLlamada");
		if (null==origenLlamada) {
			origenLlamada = (String) request.getAttribute("origenLlamada");
		}else if ("incidenciasComisiones".equalsIgnoreCase(origenLlamada) || 
				"incidenciasComisionesUnificadas".equalsIgnoreCase(origenLlamada)) {
			// ** REVISAR **
			//this.llamadaImportaciones(request,  retencionBean);
		}
		
		if (StringUtils.nullToString(request.getParameter("limpiarFiltro")).equals("false")){
			Retencion filtroRetencion = (Retencion) request.getSession().getAttribute("filtroRetencion");
			if (filtroRetencion != null) {
				retencionBean = filtroRetencion;
			}
		}
		// Carga el grupo de entidades asociadas al usuario si es de perfil 5
		List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
		parametros.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
					
		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		/*
		if (new Integer(perfil).intValue() == Constants.COD_PERFIL_1) {
			descuentosBean.getSubentidadMediadora().getEntidad().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
			descuentosBean.getSubentidadMediadora().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
		}
		*/
		try{
			//if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				
				html = mtoRetencionesService.getTablaRetenciones(request, response, retencionBean, origenLlamada,grupoEntidades);
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
						request.setAttribute("consultaRetenciones", html);
				}
			//}
			parametros.put("origenLlamada", origenLlamada);
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
			
			mv = new ModelAndView(successView, "retencionBean", retencionBean);
			mv.addAllObjects(parametros);
		
		}catch (Exception e){
			logger.error("Error en doConsulta de MtoRetenciones", e);
		}
		return mv;
	}
		
	public ModelAndView doAlta (HttpServletRequest request, 
			HttpServletResponse response,Retencion retencionBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			//retencionBean.setId(null);
			parametros = mtoRetencionesService.validaAltaModificacion(retencionBean) ;
			if (parametros.get("alerta")== null) {
				mtoRetencionesService.guardaRegistro(retencionBean);
				parametros.put("mensaje",bundle.getString("mensaje.alta.OK"));
			}else {
				retencionBean = new Retencion();
			}
			request.setAttribute("origenLlamada", "doAlta");	
		
		} catch (Exception e) {
			logger.error("Error en doAlta de MtoRetenciones", e);
			parametros.put("alerta",bundle.getString("mensaje.alta.generico.KO"));
			//request.setAttribute("origenLlamada", "errorAlta");	
			retencionBean = new Retencion();
			doConsulta(request, response, retencionBean).addAllObjects(parametros);
		}
		return doConsulta(request, response, retencionBean).addAllObjects(parametros);
	}
	
	public ModelAndView doEdita (HttpServletRequest request, 
			HttpServletResponse response,Retencion retencionBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			//parametros = mtoRetencionesService.validaAltaModificacion(retencionBean) ;
			if (parametros.get("alerta")== null) {
				mtoRetencionesService.guardaRegistro(retencionBean);
				parametros.put("mensaje",bundle.getString("mensaje.modificacion.OK"));
			}
			request.setAttribute("origenLlamada", "doEdita");	
			
			
		} catch (Exception e) {
			logger.error("Error en doEdita de MtoRetenciones", e);
			parametros.put("alerta",bundle.getString("mensaje.modificacion.KO"));				
			doConsulta(request, response, retencionBean).addAllObjects(parametros);
		}
		return doConsulta(request, response, retencionBean).addAllObjects(parametros);
	}
	
	
	
	public ModelAndView doBorrar (HttpServletRequest request, 
			HttpServletResponse response,Retencion retencionBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		String idBorrar = StringUtils.nullToString(request.getParameter("idBorrar"));
		String idRetencion = StringUtils.nullToString(request.getParameter("idRetencion"));
		Retencion retencion = new Retencion();
		if (!idBorrar.equals("")){
			retencion.setAnyo(new Integer(idBorrar));
		}
		if (!idRetencion.equals("")){
			retencion.setRetencion(new BigDecimal(idRetencion));
		}
		try {
			mtoRetencionesService.borraRegistro(retencion);
			parametros.put("mensaje",bundle.getString("mensaje.baja.OK"));
			
			request.setAttribute("origenLlamada", "doBorrar");	
		
		} catch (Exception e) {
			logger.error("Error en doBorrar de MtoRetenciones", e);
			parametros.put("alerta",bundle.getString("mensaje.baja.KO"));
			doConsulta(request, response, retencionBean).addAllObjects(parametros);
		}
		
		
		return doConsulta(request, response, retencionBean).addAllObjects(parametros);
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setMtoRetencionesService(IMtoRetencionesService mtoRetencionesService) {
		this.mtoRetencionesService = mtoRetencionesService;
	}
}