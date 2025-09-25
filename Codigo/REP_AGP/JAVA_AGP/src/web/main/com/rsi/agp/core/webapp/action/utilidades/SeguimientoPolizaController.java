package com.rsi.agp.core.webapp.action.utilidades;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ISeguimientoPolizaManager;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaBean;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class SeguimientoPolizaController extends MultiActionController{
	
	private ISeguimientoPolizaManager seguimientoPolizaManager;
	private String realPath;
	private Usuario usuario;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	/**
	 * Metodo doSeguimiento que llama al metodo getInfoPoliza de SeguimientoPolizaManager.java
	 * y carga los datos en la vista seguimientoPoliza.jsp.
	 * @author DANUNEZ
	 * @since 17/01/2019
	 * @throws DAOException
	 * @param request
	 * @param response
	 * @return ModelAndView que realiza la redireccion
	 */
	public ModelAndView doSeguimiento(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug ("SeguimientoPolizaController - doSeguimiento [INIT]");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;
		
		Poliza poliza = new Poliza();
		SeguimientoPolizaBean seguimientoPolizaBean = new SeguimientoPolizaBean();
		Long idPoliza = null;
		
		
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpoliza")))) {
			idPoliza = Long.parseLong(request.getParameter("idpoliza"));
		} else {
			idPoliza = (Long) request.getAttribute("idpoliza");
		}
		
		usuario = (Usuario) request.getSession().getAttribute("usuario");
		realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			
			poliza = seguimientoPolizaManager.getPoliza(idPoliza);
			
			seguimientoPolizaBean = seguimientoPolizaManager.getInfoPoliza(poliza, usuario.getCodusuario(), realPath);			
			
			parameters.put("objetoPoliza", poliza);
			parameters.put("polizaBean", seguimientoPolizaBean);
			parameters.put("idpoliza", idPoliza);
			parameters.put("method", request.getParameter("method"));
			
			
			
			if (usuario.getPerfil().equals(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
				parameters.put("isPerfilAdministrador",true);
			}else{
				parameters.put("isPerfilAdministrador",false);
			}
			
			resultado = new ModelAndView("moduloUtilidades/seguimientoPoliza", "datos", parameters).addAllObjects(parameters);	
			
		} catch (DAOException e) { 
			logger.error("Excepcion : SeguimientoPolizaController - doSeguimiento", e);
		}
		logger.debug ("SeguimientoPolizaController - doSeguimiento [END]");
		return resultado;	
	}
	
	/*Creacion de un nuevo metodo ‘doActualizar’ que llame al metodo ‘actualizarPoliza’ de ‘SeguimientoPolizaManager’ y muestre un mensaje por pantalla con el resultado de la operacion*/

	public ModelAndView doActualizar(HttpServletRequest request, HttpServletResponse response, Poliza polizaBean){

		logger.debug ("SegumimientoPolizaController - doActualizar [INIT]");
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;
		
		SeguimientoPolizaBean seguimientoPolizaBean = new SeguimientoPolizaBean();
		usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		Long idPoliza = null;
		if (!"".equals(StringUtils.nullToString(request.getParameter("idpoliza")))) {
			idPoliza = Long.parseLong(request.getParameter("idpoliza"));
		}
		try {
			
			Poliza poliza = seguimientoPolizaManager.getPoliza(idPoliza);
			
			logger.debug ("Antes de obtener el los datos del Seguimiento de la poliza");
			seguimientoPolizaBean = seguimientoPolizaManager.getInfoPoliza(poliza, usuario.getCodusuario(), realPath);
			
			logger.debug ("Antes de actualizar la poliza");
			seguimientoPolizaManager.actualizarPoliza(idPoliza, seguimientoPolizaBean, usuario.getCodusuario());
			
			//FALTA QUE MUESTRE UN MENSAJE POR PANTALLA DEL RESULTADO DE LA OPERACION
			parameters.put("mensaje",bundle.getString("mensaje.actualiza.OK"));
			
			resultado = doSeguimiento(request, response).addAllObjects(parameters);
			
		}catch(DAOException e) { 
			logger.error("Excepcion : SeguimientoPolizaController - doActualizar", e);
			parameters.put("mensaje",bundle.getString("mensaje.actualiza.generico.KO"));
			resultado = doSeguimiento(request, response).addAllObjects(parameters);
		}
		
		logger.debug ("SegumimientoPolizaController - doActualizar [END]");
		return resultado;
	}
	
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		return new ModelAndView(new RedirectView("utilidadesPoliza.html")).addAllObjects(parametros);
	}
	
	public ModelAndView doDescargarXmlCostes(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		
		logger.debug("SeguimientoPolizaController - doDescargarXmlCostes [INIT]");
		
		logger.debug("POLIZA1: " + request.getParameter("idpoliza"));
		logger.debug("POLIZA2: " + request.getAttribute("idpoliza"));
		logger.debug("POLIZA3: " + request.getParameter("idPoliza"));
		logger.debug("POLIZA4: " + request.getAttribute("idPoliza"));

		String idPoliza = StringUtils.isNullOrEmpty(request.getParameter("idpoliza")) ? request.getParameter("idPoliza") : request.getParameter("idpoliza");
		logger.debug("Valor de idPoliza:"+idPoliza);

		String nombreFichero = null;
		ServletOutputStream out = null;
		ModelAndView mv = null;
		realPath = this.getServletContext().getRealPath("/WEB-INF/");
		
		try {
			logger.debug("Antes de obtener el xml de Costes");
			String xmlCostes = this.seguimientoPolizaManager.getDistribucionCostes(Long.valueOf(idPoliza), realPath);
			
			if (xmlCostes != null) {
				
				response.setContentType("text/xml");
				nombreFichero = "XMLCostes_" + idPoliza + ".xml";
				
				response.setHeader("Content-Disposition","attachment; filename=" + nombreFichero);
				response.setHeader("cache-control", "no-cache");
				byte[] fileBytes = xmlCostes.getBytes();
				out = response.getOutputStream();
				out.write(fileBytes);
				out.flush();			
			}			
		} catch (Exception e) {
			logger.error("Error en doDescargarXmlCostes de SeguimientoPolizaController", e);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("alerta", e.getMessage());
			request.setAttribute("idpoliza", idPoliza);
			mv = doSeguimiento(request, response).addAllObjects(parameters);
		} finally {
			if (out != null)
				out.close();
		}		
		logger.debug("SeguimientoPolizaController - doDescargarXmlCostes [END]");
		return mv;
	}	

	//Setters para Spring
	public ISeguimientoPolizaManager getSeguimientoPolizaManager() {
		return seguimientoPolizaManager;
	}

	public void setSeguimientoPolizaManager(
			ISeguimientoPolizaManager seguimientoPolizaManager) {
		this.seguimientoPolizaManager = seguimientoPolizaManager;
	}
}
