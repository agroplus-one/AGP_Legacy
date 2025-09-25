package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.IFicheroDeudaAplazadaService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FormFicheroComisionesBean;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DetalleAbonoPoliza;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.commons.Usuario;

public class FicheroDeudaAplazadaController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(FicheroDeudaAplazadaController.class);
	private IFicheroDeudaAplazadaService ficheroDeudaAplazadaService;
	private String successView;
	
	/**
	 * Realiza la consulta de los datos de deuda aplazada
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 * Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, FicheroMult ficheroMultBean) {

		ModelAndView mv = null;
		
		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Se incluye el filtro de busqueda en la sesion
		request.getSession().setAttribute("ficheroMultBean", ficheroMultBean);

		String html = ficheroDeudaAplazadaService.getTablaDeudaAplazada(request,
					response, ficheroMultBean, origenLlamada);
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
					// Pasa a la jsp el codigo de la tabla a traves de este
					// atributo
					request.setAttribute("listadoDeudaAplazada", html);
				}
			
		
		//return new ModelAndView(successView, "ficheroMultBean", ficheroMultBean).addAllObjects(parameters);
		FormFicheroComisionesBean ffcb = new FormFicheroComisionesBean();
		mv = new ModelAndView("moduloComisiones/importacionComisiones", "ffcb", ffcb).addAllObjects(parameters);
		return mv;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param emitido
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			DetalleAbonoPoliza deudaAplazada) {
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("tipo", "D");
		ModelAndView mv= new ModelAndView("redirect:/importacionComisiones.html").addObject(
				"method", "doConsulta").addAllObjects(parameters);
		return mv;
		
	}

	

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setFicheroDeudaAplazadaService(IFicheroDeudaAplazadaService ficheroDeudaAplazadaService) {
		this.ficheroDeudaAplazadaService = ficheroDeudaAplazadaService;
	}
}