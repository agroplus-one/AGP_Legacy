package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.IFasesCierreComisionesService;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.unificado.FasesCerradas;


public class FasesCierreComisionesController extends BaseMultiActionController{
	
	private IFasesCierreComisionesService fasesCierreComisionesService;
	private String successView;
	
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, FasesCerradas faseBean)throws Exception{
		
		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		String html = fasesCierreComisionesService.getTablaFasesCierre(request,
					response, faseBean, origenLlamada);
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
					request.setAttribute("listadoFasesCierre", html);
				}
		return new ModelAndView(successView, "faseBean",
				faseBean).addAllObjects(parameters);
		
	}
	
	public ModelAndView doDetalle(HttpServletRequest request,
			HttpServletResponse response,
			FasesCerradas fase) throws Exception {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		String idCierre = StringUtils.nullToString(request.getParameter("idCierre"));
		
		try {
			if (!idCierre.isEmpty()){
				
				fase.setCierre(new Long(idCierre));
			}
			return doConsulta(request, response, fase).addAllObjects(parameters);
		
		} catch (BusinessException e) {
			logger.error("error al obtener los datos");
			return doConsulta(request, response, fase);
		}
		
	}

	public void setFasesCierreComisionesService(
			IFasesCierreComisionesService fasesCierreComisionesService) {
		this.fasesCierreComisionesService = fasesCierreComisionesService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
	

}
