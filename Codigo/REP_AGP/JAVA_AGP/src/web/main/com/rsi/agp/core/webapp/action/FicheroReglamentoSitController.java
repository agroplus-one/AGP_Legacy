package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.IFicheroReglamentoSitService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public class FicheroReglamentoSitController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(FicheroReglamentoController.class);
	private IFicheroReglamentoSitService ficheroReglamentoSitService;
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
			HttpServletResponse response, ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSitBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		
		Long idReglamento = new Long(request.getParameter("idReglamento"));
		reglamentoProduccionEmitidaSitBean.setId(idReglamento);
		
		Long idFichero = new Long (request.getParameter("idFichero"));
		
		
		String html = ficheroReglamentoSitService.getTablaReglamentoSit(request,
					response, reglamentoProduccionEmitidaSitBean, origenLlamada);
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
					request.setAttribute("listadoReglamentoSit", html);
				}
			
			parameters.put("idReglamento", idReglamento.toString());
			parameters.put("idFichero", idFichero.toString());
			parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
			parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
			parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
			
		return new ModelAndView(successView, "reglamentoProduccionEmitidaBean",
				reglamentoProduccionEmitidaSitBean).addAllObjects(parameters);

	}

	
	/**
	 * @param request
	 * @param response
	 * @param reglamentoProduccionEmitidaSitBean
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			ReglamentoProduccionEmitidaSituacion reglamentoProduccionEmitidaSitBean) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("idFichero", StringUtils.nullToString(request.getParameter("idFichero")));
		parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
		parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
		parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
		
		return new ModelAndView("redirect:/ficheroReglamento.run").addObject(
				"method", "doConsulta").addAllObjects(parameters);
	
		
	}

	
	public void setSuccessView(String successView) {
		this.successView = successView;
	}


	public void setFicheroReglamentoSitService(
			IFicheroReglamentoSitService ficheroReglamentoSitService) {
		this.ficheroReglamentoSitService = ficheroReglamentoSitService;
	}

}
