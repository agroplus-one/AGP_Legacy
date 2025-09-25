package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.IFicheroComisionService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;


public class FicheroComisionController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(FicheroComisionController.class);
	private IFicheroComisionService ficheroComisionService;
	private String successView;
	
	/**
	 * Realiza la consulta de los datos de reglamento
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, Comision comisionBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		Long idFichero = new Long(request.getParameter("idFichero"));
		comisionBean.setFichero(new Fichero());
		comisionBean.getFichero().setId(idFichero);
		
		
		String html = ficheroComisionService.getTablaComisiones(request,
					response, comisionBean, origenLlamada);
			if (html == null) {
				return null; // an export
			} else {
				String ajax = request.getParameter("ajax");
				// Llamada desde ajax
				if (ajax != null && ajax.equals("true")) {
					byte[] contents;
					try {
						contents = html.getBytes("UTF-8");
						response.getOutputStream().write(contents);
					} catch (UnsupportedEncodingException e) {
						logger.error("Error:" + e);
					} catch (IOException e) {
						logger.error("Error:" + e);
					}

					return null;
				} else
					// Pasa a la jsp el código de la tabla a través de este
					// atributo
					request.setAttribute("listadoComision", html);
				}
			comisionBean.setFichero(new Fichero());
			if (!StringUtils.nullToString(request.getParameter("nombreFichero")).equals("")){
				comisionBean.getFichero().setNombrefichero(request.getParameter("nombreFichero"));
			}
			if (!StringUtils.nullToString(request.getParameter("fase")).equals("")){
				comisionBean.getFichero().setFase(new Fase());
				comisionBean.getFichero().getFase().setFase(request.getParameter("fase"));
			}
			if (!StringUtils.nullToString(request.getParameter("estado")).equals("")){
				parameters.put("estado", request.getParameter("estado"));
			}
			parameters.put("idFichero", idFichero.toString());
		
		return new ModelAndView(successView, "comisionBean",
				comisionBean).addAllObjects(parameters);

	}
	
	/**
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			Comision comision) {
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("tipo", "C");
		ModelAndView mv= new ModelAndView("redirect:/importacionComisiones.html").addObject(
				"method", "doConsulta").addAllObjects(parameters);
		return mv;
		
	}

	public void setFicheroComisionService(
			IFicheroComisionService ficheroComisionService) {
		this.ficheroComisionService = ficheroComisionService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
