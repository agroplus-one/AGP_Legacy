/**
 * 
 */
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

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.MtoComisionesRenovFilter;
import com.rsi.agp.core.jmesa.service.IMtoComisionesRenovService;
import com.rsi.agp.core.jmesa.ui.CustomToolbar;
import com.rsi.agp.core.jmesa.ui.CustomView;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.ComisionesRenov;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;


/**
 * @author   U028975 (Tatiana, T-Systems)
 * Petición: 57624 (Mantenimiento de Comisioens en Renovables por E-S Mediadora)
 * Fecha:    (Enero/Febrero.2019)
 */
public class MtoComisionesRenovController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoComisionesRenovController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IMtoComisionesRenovService mtoComisionesRenovService;
	private String successView;
	
	/**
	 * Realiza la consulta de Comisiones Renovables por E-SMediadora
	 * @param request
	 * @param response
	 * @param comisionesRenovBean
	 * @return
	 */
	/* CONSULTA */
	public ModelAndView doConsulta(HttpServletRequest request, 
								   HttpServletResponse response, 
								   ComisionesRenov comisionesRenovBean) {
		logger.debug("init - MtoComisionesRenovController -doConsulta");
		if ("false".equals(request.getParameter("ajax")) && request.getParameter("export") != null) {
			return doExportToExcel(request);
		}
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		String html = null;
		ModelAndView mv = new ModelAndView(successView);
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		final String perfil = usuario.getPerfil().substring(4);
		parametros.put("perfil", perfil);
		parametros.put("externo",usuario.getExterno());
		
		
		/* cargamos la lista de Grupos de Negocio */
		List <GruposNegocio> gruposNegocio = new ArrayList<GruposNegocio>();
    	gruposNegocio = mtoComisionesRenovService.getGruposNegocio();
    	parametros.put("gruposNegocio", gruposNegocio);

    	BigDecimal codPlan = null;
		
		// Guardo el valor del plan del filtro de búsqueda
    	if (comisionesRenovBean.getCodplan() !=null){
    		codPlan = comisionesRenovBean.getCodplan();	
    	}
    	
    	String origenLlamadaLimp = request.getParameter("origenLlamadaLimp");
		
    	String origenLlamada = (String) request.getAttribute("origenLlamada");
    	
		if (StringUtils.nullToString(origenLlamada).equals("")) {
			 origenLlamada = request.getParameter("origenLlamada");
		}
		try{
			logger.debug("Comienza la busqueda de ComisionesRenov");
			if (StringUtils.nullToString(origenLlamada).equals("")) {
				if (!StringUtils.nullToString(origenLlamada).equals("")){
					origenLlamada = origenLlamadaLimp;
				}else{
					origenLlamada = "menuGeneral";
				}
				
			}
			
			html = mtoComisionesRenovService.getTablaComisionesRenov(request, response, comisionesRenovBean, origenLlamada, gruposNegocio);
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
					request.setAttribute("consultaComisionesRenov", html);
			}
			parametros.put("origenLlamada", origenLlamada);
			String mensaje = request.getParameter("mensaje");
			String alerta = request.getParameter("alerta");
			
			if (alerta != null) {
				parametros.put("alerta", alerta);
			}
			if (mensaje != null) {
				parametros.put("mensaje", mensaje);
			}
				
			//Obtenemos la lista de Entidades
			String grupoEntidades = "";
			if (!StringUtils.nullToString(usuario.getListaCodEntidadesGrupo()).equals("")){
				grupoEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false);
			}
			parametros.put("grupoEntidades", grupoEntidades);
			
			// Guardo el valor del plan del filtro de búsqueda
	    	if (codPlan !=null){
	    		comisionesRenovBean.setCodplan(codPlan);	
	    	}
				
			mv = new ModelAndView(successView, "comisionesRenovBean", comisionesRenovBean);
			mv.addAllObjects(parametros);
				
			logger.debug("end - MtoComisionesRenovController");
		
			return mv;
		
		}catch (Exception e){
			logger.error("Excepcion : MtoComisionesRenovController - doConsulta", e);
		}
		return null;
	}
	
	/* ALTA DE NUEVO REGISTRO */
	public ModelAndView doAlta(HttpServletRequest request,HttpServletResponse response,ComisionesRenov comisRenovBean) {
		
		logger.debug("doAlta - MtoComisionesRenovController - inicio");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		BigDecimal codPlan_aux = comisRenovBean.getCodplan();
		
		comisRenovBean.setUsuarioModif(usuario.getCodusuario());
		try {
			comisRenovBean.setId(null);
			parametros = mtoComisionesRenovService.validaAltaModificacion(comisRenovBean) ;
			ArrayList<Integer> errorComisiones = new ArrayList<Integer>();
			
			if (parametros.get("alerta")== null) {
				
				errorComisiones = mtoComisionesRenovService.guardaComisRenov(comisRenovBean, usuario, 0);
				
				if (errorComisiones.size()> 0){
					// Carga los mensajes de error producidos en el proceso de alta
					ArrayList<String> errores = cargarErrores(usuario, parametros, errorComisiones, true);
				
					if (errores.size() > 0){
						parametros.put("alerta2", errores);
					}
				}else{				
					parametros.put("mensaje",bundle.getString("mensaje.alta.OK"));
				}
			}
			
			request.setAttribute("origenLlamada", "doAlta");	
		} catch (Exception e) {
			logger.error("Error en doAlta de MtoDescuentos", e);
			parametros.put("alerta",bundle.getString("mensaje.alta.generico.KO"));
			request.setAttribute("origenLlamada", "errorAlta");	
			doConsulta(request, response, comisRenovBean).addAllObjects(parametros);
		}
		comisRenovBean.setCodplan(codPlan_aux);
		return doConsulta(request, response, comisRenovBean).addAllObjects(parametros);
	}
	
	/* BORRADO DE REGISTRO */
	public ModelAndView doBorrar(HttpServletRequest request,HttpServletResponse response,ComisionesRenov comisionesRenovBean) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv =null;
		ComisionesRenov comisRenovBean = null;
		/* Guardamos los datos del filtro de búsqueda */
		
		try{
			if (!StringUtils.nullToString(comisionesRenovBean.getCodplan()).equals("") && !StringUtils.nullToString(comisionesRenovBean.getCodlinea()).equals("")){
				parameters = mtoComisionesRenovService.borraComisionRenov(comisionesRenovBean);
				
			}else {
				parameters.put("alerta", bundle.getString("mensaje.mtoComisionesRenov.borrar.KO"));
			}
			request.setAttribute("origenLlamada", "doBorrar");
			comisRenovBean = mtoComisionesRenovService.cargarFiltroBusqueda(request, comisionesRenovBean);
			mv = doConsulta(request, response, comisRenovBean).addAllObjects(parameters);
		
		}catch (BusinessException e) {
			logger.error("Se ha producido un error: " + e.getMessage());
			parameters.put("alerta", bundle.getString("mensaje.mtoComisionesRenov.borrar.KO"));
			comisRenovBean = mtoComisionesRenovService.cargarFiltroBusqueda(request, comisionesRenovBean);
			mv = doConsulta(request, response, comisionesRenovBean).addAllObjects(parameters);
			
		}catch (Exception e){
			logger.debug("Error inesperado al borrar de oficinas pago manual", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoComisionesRenov.borrar.KO"));
			comisRenovBean = mtoComisionesRenovService.cargarFiltroBusqueda(request, comisionesRenovBean);			
			mv = doConsulta(request, response, comisionesRenovBean).addAllObjects(parameters);
		}
		return mv;
	}
	
	/* MODIFICACION DE UN  REGISTRO */
	public ModelAndView doModificar(HttpServletRequest request,HttpServletResponse response,ComisionesRenov comisRenovBean) {
		
		logger.debug("doModificar - MtoComisionesRenovController - inicio");
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		BigDecimal codPlan_aux = comisRenovBean.getCodplan();

		try {
			ArrayList<Integer> errorComisiones = new ArrayList<Integer>();
			
			parametros = mtoComisionesRenovService.validaAltaModificacion(comisRenovBean) ;
			
			if (parametros.get("alerta")== null) {
				comisRenovBean.setUsuarioModif(usuario.getCodusuario());
				
				errorComisiones = mtoComisionesRenovService.guardaComisRenov(comisRenovBean, usuario, 1);
				
				if (errorComisiones.size()> 0){
					// Carga los mensajes de error producidos en el proceso de alta
					ArrayList<String> errores = cargarErrores(usuario, parametros, errorComisiones, false);
				
					if (errores.size() > 0){
						parametros.put("alerta2", errores);
					}
				}else{				
					parametros.put("mensaje",bundle.getString("mensaje.modificacion.OK"));
				}
			}
			request.setAttribute("origenLlamada", "doModificar");
			parametros.put("origenLlamada", "doModificar");
			
			
		} catch (Exception e) {
			logger.error("Error en doModificar de MtoComisionesRenov", e);
			parametros.put("alerta",bundle.getString("mensaje.modificacion.KO"));
			doConsulta(request, response, comisRenovBean).addAllObjects(parametros);
		}
		
		comisRenovBean.setCodplan(codPlan_aux);
		return 		doConsulta(request, response, comisRenovBean).addAllObjects(parametros);

	}
	
	/**
	 * Realiza la copia de todos los descuentos  de un plan/línea a otro
	 * @param request
	 * @param response
	 * @param descuentosBean
	 * @return
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, ComisionesRenov comisRenovBean) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		BigDecimal lineaDest = null;
		BigDecimal planOrig = null;
		BigDecimal lineaOrig = null;
		
		logger.debug("doReplicar - MtoComisionesRenovController - inicio");
		
		try {
			// Obtiene el plan/línea destino
			
			planOrig = new BigDecimal (request.getParameter("plan_orig"));
			lineaOrig = new BigDecimal (request.getParameter("linea_orig"));
			
			// Obtiene el plan/línea destino y/o servicio
			planDest = new BigDecimal (request.getParameter("plan_dest"));
			lineaDest = new BigDecimal (request.getParameter("linea_dest"));
			
			/*BigDecimal planOrig = comisRenovBean.getCodplan();
			BigDecimal lineaOrig = comisRenovBean.getCodlinea();
			
			planDest = new BigDecimal (request.getParameter("planreplica"));
			lineaDest = new BigDecimal (request.getParameter("lineareplica"));*/
			
			final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			
			// Llamada al método que realiza la réplica
			logger.debug("Replicar las comisiones del plan/linea " + planOrig + "/" + lineaOrig + " al" + planDest + "/" + lineaDest);
			parameters = mtoComisionesRenovService.replicar(planOrig, lineaOrig, planDest, lineaDest, usuario.getCodusuario());
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar las Comisionesa otro plan/linea", e);
			parameters.put("alerta", bundle.getString("mensaje.mtoComisionesRenov.replica.KO"));
		}
		
		// Redirección
		// Si ha ocurrido algún error, se vuelve a la pantalla con el filtro de búsqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/línea destino
		if (!parameters.containsKey("alerta")) {
			comisRenovBean = new ComisionesRenov();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			comisRenovBean.setCodlinea(lineaDest);
			comisRenovBean.setCodplan(planDest);
		}
		
		return doConsulta(request, response, comisRenovBean).addAllObjects(parameters);
	}
	
	/**
	 * Devuelve la lista de mensajes correspondiente a los errores producidos en el proceso de alta o modificacion del colectivo
	 * @param usuario
	 * @param parameters
	 * @param errorColectivo
	 * @return
	 */
	private ArrayList<String> cargarErrores(final Usuario usuario,final Map<String, Object> parameters, ArrayList<Integer> errorComisiones, boolean alta) {
		
		ArrayList<String> errores = new ArrayList<String>();
		for (Integer valor: errorComisiones){
			switch (valor.intValue()){
				case 0: parameters.put("mensaje", bundle.getString("mensaje." + (alta ? "alta" : "modificacion") + ".OK"));
						break;
				case 2: 
						if (!usuario.getPerfil().equalsIgnoreCase(bundle.getString("usuario.perfil") + "-0")) {
							errores.add(bundle.getString("mensaje.mtoComisionesRenov.entidad.KO"));
						}
						break;
				case 4: errores.add(bundle.getString("mensaje.mtoComisionesRenov.planLinea.inexistente.KO"));
						break;
				case 5: errores.add(bundle.getString("mensaje.mtoComisionesRenov.planLinea.inactivo.KO"));
						break;
				case 9:	errores.add(bundle.getString("mensaje.comisiones.distMed.validacion.grupoNegocio"));
						break;	
				case 10: errores.add(bundle.getString("mensaje.comisiones.distMed.validacion.modulo"));
						break;
				case 13: errores.add(bundle.getString("mensaje.mtoComisionesRenov.subentidad.inexistente.KO"));
						break;
				case 20: errores.add(bundle.getString("mensaje.mtoComisionesRenov.generico.KO"));
						break;
				default:
						break;
			}
		}
		return errores;
	}

	public ModelAndView doExportToExcel(HttpServletRequest request) {
	    List<ComisionesRenov> items;
	    Limit limit = (Limit) request.getSession().getAttribute("consultaComisionesRenov_LIMIT");
		// Obtener todos los registros filtrados y ordenados
		items = mtoComisionesRenovService.getComisionesRenovList(limit);

		// Si hay registros, preparar los datos para la exportación a Excel
		if (items.size() != 0) {
		    request.setAttribute("listado", items);
		    request.setAttribute("nombreInforme", "ListadoComisionesRenov");
		    request.setAttribute("jasperPath", "informeJasper.listadoMtoComisionesRenov");

		    // Redirigir a la vista de exportación a Excel
		    return new ModelAndView("forward:/informes.html?method=doInformeListado");
		}

	    // Si no hay registros o se produce un error, devolver null
	    return null;
	}

	public void setMtoComisionesRenovService(IMtoComisionesRenovService mtoComisionesRenovService){
		this.mtoComisionesRenovService = mtoComisionesRenovService;
	}
	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	
	
	
	
	
}
