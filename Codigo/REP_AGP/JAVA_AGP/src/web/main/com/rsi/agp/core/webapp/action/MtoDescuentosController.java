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
import com.rsi.agp.core.jmesa.service.IMtoDescuentosService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;

public class MtoDescuentosController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(MtoDescuentosController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IMtoDescuentosService mtoDescuentosService;
	private String successView;
	
	private final static String VACIO = "";
	
	public ModelAndView doConsulta (HttpServletRequest request, 
			HttpServletResponse response,Descuentos descuentosBean) {
		
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
			this.llamadaImportaciones(request,  descuentosBean);
		}
		
		if (StringUtils.nullToString(request.getParameter("limpiarFiltro")).equals("false")){
			Descuentos filtroDescuentos = (Descuentos) request.getSession().getAttribute("filtroDescuentos");
			if (filtroDescuentos != null) {
				descuentosBean = filtroDescuentos;
			}
		}
		// Carga el grupo de entidades asociadas al usuario si es de perfil 5
		List<BigDecimal> grupoEntidades = usuario.getListaCodEntidadesGrupo();
		parametros.put("grupoEntidades",StringUtils.toValoresSeparadosXComas(grupoEntidades, false, false));
					
		perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		if (new Integer(perfil).intValue() == Constants.COD_PERFIL_1) {
			descuentosBean.getSubentidadMediadora().getEntidad().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
			descuentosBean.getSubentidadMediadora().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
		}
		try{
			//if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
				
				html = mtoDescuentosService.getTablaDescuentos(request, response, descuentosBean, origenLlamada,grupoEntidades);
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
						request.setAttribute("consultaDescuentos", html);
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
			
			mv = new ModelAndView(successView, "descuentosBean", descuentosBean);
			mv.addAllObjects(parametros);
		
		}catch (Exception e){
			logger.error("Error en doConsulta de MtoDescuentos", e);
		}
		return mv;
	}
	
	
	private void llamadaImportaciones(HttpServletRequest request, 
			Descuentos descuentosBean) {
		
		String[] temp;
		String delimiter = "-";
		String subEntidad=null;
		String codEnMediadora=null;
		String codSubEntMediadora=null;
		String sucursal="0";
		Linea linea = new Linea();
		
		subEntidad = request.getParameter("subentidad");
		temp = subEntidad.split(delimiter);
		
		 for(int i =0; i < temp.length ; i++) {
			if(i==0) codEnMediadora =temp[i] ;
			if(i==1) codSubEntMediadora=temp[i];		
		 }
		sucursal=request.getParameter("codOficina");
	
		if(null !=codEnMediadora && StringUtils.isNumeric(codEnMediadora)) {
			//descuentosBean.getSubentidadMediadora().getEntidad().setCodentidad(new BigDecimal(codEnMediadora));
			descuentosBean.getSubentidadMediadora().getId().setCodentidad(new BigDecimal(codEnMediadora));
			//subentidadMediadora.id.codentidad
			//subentidadMediadora.entidad.codentidad
		}
		
		if (null != codSubEntMediadora && StringUtils.isNumeric(codSubEntMediadora)) {
			descuentosBean.getSubentidadMediadora().getId().setCodsubentidad(new BigDecimal (codSubEntMediadora));
		}
		
		if (null != sucursal && StringUtils.isNumeric(sucursal)) {
			//if (null!= descuentosBean.getOficina() ) {
				descuentosBean.getOficina().getId().setCodoficina(new BigDecimal(sucursal.trim()));
		}	
		
		
		
		String lin =request.getParameter("codLineaCom");
		String plan=request.getParameter("planLineaCom");
		if (lin !=null) 	
			linea.setCodlinea(new BigDecimal(lin));				
		if (plan !=null) 
			linea.setCodplan(new BigDecimal(plan));				
		descuentosBean.setLinea(linea);	
		
		/*String lin =request.getParameter("codLineaCom");
		String plan=request.getParameter("planLineaCom");
		
		if (lin !=null) {
			linea.setCodlinea(new BigDecimal(lin));
			linea.setCodplan(new BigDecimal(plan));		
			descuentosBean.setLinea(linea);
			descuentosBean.getLinea().setCodlinea(new BigDecimal(lin));
		}*/		
	}
	
	
	public ModelAndView doAlta (HttpServletRequest request, 
			HttpServletResponse response,Descuentos descuentosBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			descuentosBean.setId(null);
			parametros = mtoDescuentosService.validaAltaModificacion(descuentosBean) ;
			if (parametros.get("alerta")== null) {
				if(null == descuentosBean.getOficina().getId().getCodoficina()) {
					descuentosBean.getOficina().getId().setCodoficina(Constants.SIN_OFICINA);
				}
				mtoDescuentosService.guardaRegistro(descuentosBean);
				mtoDescuentosService.guardaHistorico (descuentosBean,Constants.ALTA_DESCUENTO,usuario.getCodusuario());
				parametros.put("mensaje",bundle.getString("mensaje.alta.OK"));
			}
			request.setAttribute("origenLlamada", "doAlta");	
		
		} catch (Exception e) {
			logger.error("Error en doAlta de MtoDescuentos", e);
			parametros.put("alerta",bundle.getString("mensaje.alta.generico.KO"));
			request.setAttribute("origenLlamada", "errorAlta");	
			doConsulta(request, response, descuentosBean).addAllObjects(parametros);
		}
		return doConsulta(request, response, descuentosBean).addAllObjects(parametros);
	}
	
	public ModelAndView doEdita (HttpServletRequest request, 
			HttpServletResponse response,Descuentos descuentosBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			parametros = mtoDescuentosService.validaAltaModificacion(descuentosBean) ;
			if (parametros.get("alerta")== null) {
				mtoDescuentosService.guardaRegistro(descuentosBean);
				mtoDescuentosService.guardaHistorico (descuentosBean,Constants.MOD_DESCUENTO,usuario.getCodusuario());
				parametros.put("mensaje",bundle.getString("mensaje.modificacion.OK"));
			}
			request.setAttribute("origenLlamada", "doEdita");	
			
			
		} catch (Exception e) {
			logger.error("Error en doEdita de MtoDescuentos", e);
			parametros.put("alerta",bundle.getString("mensaje.modificacion.KO"));				
			doConsulta(request, response, descuentosBean).addAllObjects(parametros);
		}
		return doConsulta(request, response, descuentosBean).addAllObjects(parametros);
	}
	
	public ModelAndView doBorrar (HttpServletRequest request, 
			HttpServletResponse response,Descuentos descuentosBean) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try {
			mtoDescuentosService.borraRegistro(descuentosBean);
			mtoDescuentosService.guardaHistorico (descuentosBean,Constants.BAJA_DESCUENTO,usuario.getCodusuario());
			parametros.put("mensaje",bundle.getString("mensaje.baja.OK"));
			
			request.setAttribute("origenLlamada", "doBorrar");	
		
		} catch (Exception e) {
			logger.error("Error en doBorrar de MtoDescuentos", e);
			parametros.put("alerta",bundle.getString("mensaje.baja.KO"));
			doConsulta(request, response, descuentosBean).addAllObjects(parametros);
		}
		
		
		return doConsulta(request, response, descuentosBean).addAllObjects(parametros);
	}
	
	public ModelAndView doConsultarHistorico (HttpServletRequest request, HttpServletResponse response,
			Descuentos descuentosBean){
		logger.debug("init - doConsultarHistorico");
		Map<String, Object> parametros = new HashMap<String, Object>();
		ModelAndView mv = null;
		ArrayList<DescuentosHistorico> listadoHistorico = new ArrayList<DescuentosHistorico>();
		try {
			request.getSession().setAttribute("filtroDescuentos", descuentosBean);
			
		    listadoHistorico = mtoDescuentosService.consultaHistorico (descuentosBean.getId());
			parametros.put("listadoHistorico", listadoHistorico);			
			
			parametros.put("entidadH",StringUtils.nullToString(request.getParameter("entidadH")));
			
			parametros.put("entmediadoraH",StringUtils.nullToString(request.getParameter("entmediadoraH")));
			parametros.put("subentmediadoraH",StringUtils.nullToString(request.getParameter("subentmediadoraH")));
			parametros.put("nomEntidadH",StringUtils.nullToString(request.getParameter("nomEntidadH")));
			parametros.put("oficinaH",formatOficina(StringUtils.nullToString(request.getParameter("oficinaH"))));
			parametros.put("nomOficinaH",StringUtils.nullToString(request.getParameter("nomOficinaH")));
			parametros.put("delegacionH",!VACIO.equals(StringUtils.nullToString(request.getParameter("delegacionH")))?request.getParameter("delegacionH"):"Todas");
			parametros.put("pctDescMaxH",StringUtils.nullToString(request.getParameter("pctDescMaxH")));
			parametros.put("planH", StringUtils.nullToString(request.getParameter("planH")));
			parametros.put("lineaH", StringUtils.nullToString(request.getParameter("lineaH")));
			parametros.put("desc_lineaH", StringUtils.nullToString(request.getParameter("nomlineaH")));
			parametros.put("permitirRecargoH", StringUtils.nullToString(request.getParameter("permitirRecargoH")));
			parametros.put("verComisionesH", StringUtils.nullToString(request.getParameter("verComisionesH")));	
			
			int pr = Integer.parseInt(request.getParameter("permitirRecargoH"));
			parametros.put("permitirRecargoTxt", getPermitirRecargoTxt(pr));
			int vc = Integer.parseInt(request.getParameter("verComisionesH"));
			parametros.put("verComisionesTxt", this.getVerComisionesTxt(vc));	
			
			
			

			
			
		}catch (Exception be) {
			logger.error("Se ha producido un error al consultar el historico ",be);
			parametros.put("alerta", bundle.getString("mensaje.comisiones.KO"));
			mv = doConsulta(request, response, new Descuentos());
			return mv.addAllObjects(parametros);
		}
		logger.debug("end - doConsultarHistorico");
		return mv = new ModelAndView("moduloComisiones/mtoDescuentosHistorico",
				"descuentosBean", descuentosBean).addAllObjects(parametros);
	}
	
	/**
	 * Realiza la copia de todos los descuentos  de un plan/línea a otro
	 * @param request
	 * @param response
	 * @param descuentosBean
	 * @return
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, Descuentos descuentosBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		BigDecimal lineaDest = null;
		BigDecimal entidadReplica=null;
		try {
			// Obtiene el plan/línea destino
			BigDecimal planOrig = descuentosBean.getLinea().getCodplan();
			BigDecimal lineaOrig = descuentosBean.getLinea().getCodlinea();
			planDest = new BigDecimal (request.getParameter("planreplica"));
			lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			entidadReplica=new BigDecimal(request.getParameter("entidadreplica"));
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			// Llamada al método que realiza la réplica
			logger.debug("Replicar los descuentos del plan/linea " + planOrig + "/" + lineaOrig + " al" + planDest + "/" + lineaDest);
			parameters = mtoDescuentosService.replicar(planOrig, lineaOrig, planDest, lineaDest, usuario.getCodusuario(), entidadReplica);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar los descuentos a otro plan/linea", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoDescuentos.replica.KO"));
		}
		
		// Redirección
		// Si ha ocurrido algún error, se vuelve a la pantalla con el filtro de búsqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/línea destino
		if (!parameters.containsKey("alerta")) {
			descuentosBean = new Descuentos ();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			descuentosBean.setLinea(linea);
			descuentosBean.getSubentidadMediadora().getEntidad().setCodentidad(entidadReplica);
		}
		
		return doConsulta(request, response, descuentosBean).addAllObjects(parameters);
	}

	/** 19/02/2015 Cambio masivo de errorWs		 * 
	 * @param request
	 * @param response
	 * @param 
	 * @return mv
	 */
	public ModelAndView doCambioMasivo(HttpServletRequest request, HttpServletResponse response, Descuentos descuentosBean) {
		logger.debug("init - MtoDescuentosController - doCambioMasivo");
		Map<String, String> parameters = new HashMap<String, String>();
		ModelAndView mv = null;
		String listaIdsMarcados_cm = StringUtils.nullToString(request.getParameter("listaIdsMarcados_cm"));
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try{
			parameters = mtoDescuentosService.cambioMasivo(listaIdsMarcados_cm, descuentosBean, usuario); 
			parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
		}catch (DAOException e) {
			logger.debug("Error inesperado en el Cambio Masivo de Descuentos ", e);
			parameters.put("alerta", bundle.getString("mensaje.modificacion.KO"));			
		}catch (Exception e){
			logger.debug("Error inesperado en el Cambio Masivo de Descuentos ", e);
			parameters.put("alerta", bundle.getString("mensaje.modificacion.KO"));
		
		}	
		//DAA 05/03/2013 recuperamos el filtro de sesion para pasarlo al nuevo Bean del Cambio Masivo y 
		//no perder el filtro al volver
		Descuentos cm_descuentosBean = mtoDescuentosService.getCambioMasivoBeanFromLimit((Limit) request.getSession().getAttribute("consultaDescuentos_LIMIT"));
		request.setAttribute("origenLlamada", "cambioMasivo");
		mv = doConsulta(request, response, cm_descuentosBean).addAllObjects(parameters);
		
		logger.debug("end - MtoDescuentosController - doCambioMasivo");
		return mv;
	}
	
	
		public String getVerComisionesTxt(int verComisiones) {
			String res="";
			switch (verComisiones) {
				case Constants.VER_COMISIONES_NO:
					res= Constants.VER_COMISIONES_NO_TXT;
					break;
				case Constants.VER_COMISIONES_ENTIDAD:
					res= Constants.VER_COMISIONES_ENTIDAD_TXT;
					break;
				case Constants.VER_COMISIONES_ENTIDAD_MEDIADORA:
					res= Constants.VER_COMISIONES_ENTIDAD_MEDIADORA_TXT;
					break;
				case Constants.VER_COMISIONES_TODAS:
					res= Constants.VER_COMISIONES_TODAS_TXT;
					break;
				default:
					break;
			}
			return res;
		}
		 
	    public String getPermitirRecargoTxt(int permitirRecargo) {
	    	String res;
			if(permitirRecargo == Constants.PERMITIR_RECARGO_NO) {
				res = Constants.PERMITIR_RECARGO_NO_TXT ;
			}else {
				res =Constants.PERMITIR_RECARGO_SI_TXT;
			}
				return res;		
	    }
	    
	
	private String formatOficina(String codOficina) {
		if (codOficina.length()<4 ) {
			while (codOficina.length()<4) {
				codOficina= "0" + codOficina;
			}
		}
		return codOficina;
	}
	
	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setMtoDescuentosService(IMtoDescuentosService mtoDescuentosService) {
		this.mtoDescuentosService = mtoDescuentosService;
	}
}