package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.IFicheroReglamentoService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;

public class FicheroReglamentoController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(FicheroReglamentoController.class);
	private IFicheroReglamentoService ficheroReglamentoService;
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
			HttpServletResponse response, ReglamentoProduccionEmitida reglamentoProduccionEmitidaBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		Long idFichero = new Long(request.getParameter("idFichero"));
		reglamentoProduccionEmitidaBean.setFichero(new Fichero());
		reglamentoProduccionEmitidaBean.getFichero().setId(idFichero);
		
		String html = ficheroReglamentoService.getTablaReglamento(request,
					response, reglamentoProduccionEmitidaBean, origenLlamada);
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
					request.setAttribute("listadoReglamento", html);
				}
			reglamentoProduccionEmitidaBean.setFichero(new Fichero());
			if (!StringUtils.nullToString(request.getParameter("nombreFichero")).equals("")){
				reglamentoProduccionEmitidaBean.getFichero().setNombrefichero(request.getParameter("nombreFichero"));
			}
			if (!StringUtils.nullToString(request.getParameter("fase")).equals("")){
				reglamentoProduccionEmitidaBean.getFichero().setFase(new Fase());
				reglamentoProduccionEmitidaBean.getFichero().getFase().setFase(request.getParameter("fase"));
			}
			if (!StringUtils.nullToString(request.getParameter("estado")).equals("")){
				parameters.put("estado", request.getParameter("estado"));
			}
			parameters.put("idFichero", idFichero.toString());
		
		return new ModelAndView(successView, "reglamentoProduccionEmitidaBean",
				reglamentoProduccionEmitidaBean).addAllObjects(parameters);

	}

	
	/**
	 * @param request
	 * @param response
	 * @param reglamentoProduccionEmitidaBean
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitida reglamentoProduccionEmitidaBean) {
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("tipo", "R");
		ModelAndView mv= new ModelAndView("redirect:/importacionComisiones.html").addObject(
				"method", "doConsulta").addAllObjects(parameters);
		return mv;
		
	}

	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}


	public void setFicheroReglamentoService(
			IFicheroReglamentoService ficheroReglamentoService) {
		this.ficheroReglamentoService = ficheroReglamentoService;
	}

	

}

