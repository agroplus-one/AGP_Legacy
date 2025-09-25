package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IFicheroImpagadosService;
import com.rsi.agp.core.managers.impl.FaseManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.impagados.ReciboImpagado;

public class FicheroImpagadosController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(FicheroImpagadosController.class);
	private IFicheroImpagadosService ficheroImpagadosService;
	private String successView;
	private FaseManager faseManager;

	/**
	 * Realiza la consulta de ficheros de comisiones Impagados
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response,
			ReciboImpagado reciboImpagadoBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = request.getParameter("origenLlamada");
		
		reciboImpagadoBean.setFichero(new Fichero());
		reciboImpagadoBean.getFichero().setId(new Long(request.getParameter("idFichero")));
		logger.info("doConsulta - Comienza la búsqueda fichero de impagados");
		
		Long idFichero = new Long (request.getParameter("idFichero"));
		String html = ficheroImpagadosService.getTablaImpagados(
				request, response, reciboImpagadoBean, origenLlamada);
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
				// Pasa a la jsp el código de la tabla a través de este atributo
				request.setAttribute("listadoFicheroImpagados", html);
		}
		
		BigDecimal plan = null;
		try {
			plan = faseManager.obtenerPlanByIdFichero(idFichero);
		} catch (DAOException e) {
			logger.error("Error:" + e);
		}
		
		String tipoPlan = "";
		if(plan!=null){
			if(plan.intValue()<2015){
				tipoPlan = "2014-";
			}else{
				tipoPlan = "2015+";
			}
		}
		parameters.put("tipoPlan", tipoPlan);
		
		reciboImpagadoBean.setFichero(new Fichero());
		if (!StringUtils.nullToString(request.getParameter("nombreFichero")).equals("")){
			reciboImpagadoBean.getFichero().setNombrefichero(request.getParameter("nombreFichero"));
		}
		if (!StringUtils.nullToString(request.getParameter("fase")).equals("")){
			reciboImpagadoBean.getFichero().setFase(new Fase());
			reciboImpagadoBean.getFichero().getFase().setFase(request.getParameter("fase"));
		}
		if (!StringUtils.nullToString(request.getParameter("estado")).equals("")){
			parameters.put("estado", request.getParameter("estado"));
		}
		parameters.put("idFichero", request.getParameter("idFichero"));
		parameters.put("muestraDiv",StringUtils.nullToString( request.getParameter("muestraDiv")));
		return new ModelAndView(successView, "reciboImpagadoBean",
				reciboImpagadoBean).addAllObjects(parameters);
	}
	
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			ReciboImpagado reciboImpagadoBean) {
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("tipo", "I");
		ModelAndView mv= new ModelAndView("redirect:/importacionComisiones.html").addObject(
				"method", "doConsulta").addAllObjects(parameters);
		return mv;
		
	}
	public ModelAndView doDetalle(HttpServletRequest request,
			HttpServletResponse response,
			ReciboImpagado reciboImpagadoBean) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		Long idImpagado = new Long(request.getParameter("idImpagado"));
		
		parameters.put("idFichero", StringUtils.nullToString(request.getParameter("idFichero")));
		parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
		parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
		parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
		parameters.put("origenLlamada", "detalleImpagados");
		parameters.put("muestraDiv", "true");
		try {
			reciboImpagadoBean = ficheroImpagadosService.getDatosImpagados(idImpagado);
			return doConsulta(request, response, reciboImpagadoBean).addAllObjects(parameters);
		
		} catch (BusinessException e) {
			logger.error("error al obtener los datos");
			return doConsulta(request, response, reciboImpagadoBean);
		}
		
	}
	
	public void setFicheroImpagadosService(
			IFicheroImpagadosService ficheroImpagadosService) {
		this.ficheroImpagadosService = ficheroImpagadosService;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setFaseManager(FaseManager faseManager) {
		this.faseManager = faseManager;
	}	
}