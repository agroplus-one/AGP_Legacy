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
import com.rsi.agp.core.jmesa.service.IFicheroComisionApliService;
import com.rsi.agp.core.managers.impl.FaseManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;

public class FicheroComisionApliController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(FicheroComisionApliController.class);
	private IFicheroComisionApliService ficheroComisionApliService;
	private String successView;
	private FaseManager faseManager;

	/**
	 * Realiza la consulta de los datos de comisiones aplicacion
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, ComisionAplicacion comisionApliBean) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
				
		if (comisionApliBean.getId()== null){
			Long idComisiones = new Long(request.getParameter("idComision"));
			comisionApliBean.setComision(new Comision());
			comisionApliBean.getComision().setId(idComisiones);
			parameters.put("idComision", idComisiones.toString());
		}else{
			parameters.put("idComision", comisionApliBean.getComision().getId().toString());
		}
		
		Long idFichero = new Long (request.getParameter("idFichero"));
		
		String html = ficheroComisionApliService.getTablaComisionesApli(request,
					response, comisionApliBean, origenLlamada);
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
					request.setAttribute("listadoComisionApli", html);
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
			
			parameters.put("idFichero", idFichero.toString());
			parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
			parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
			parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
			parameters.put("muestraDiv",StringUtils.nullToString( request.getParameter("muestraDiv")));
			parameters.put("marcaCondiComisionesApli", StringUtils.nullToString(request.getParameter("marcaCondiComisionesApli")));
			parameters.put("marcaCondiComisiones", StringUtils.nullToString(request.getParameter("marcaCondiComisiones")));
		return new ModelAndView(successView, "comisionApliBean",
				comisionApliBean).addAllObjects(parameters);

	}

	/**
	 * @param request
	 * @param response
	 * @param comisionApliBean
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			ComisionAplicacion comisionApliBean) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("idFichero", StringUtils.nullToString(request.getParameter("idFichero")));
		parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
		parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
		parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
		
		return new ModelAndView("redirect:/ficheroComision.run").addObject(
				"method", "doConsulta").addAllObjects(parameters);
	
		
	}
	

	public ModelAndView doDetalle(HttpServletRequest request,
			HttpServletResponse response,
			ComisionAplicacion comisionApliBean) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		String marcaCondiComisionesApli = "";
		String marcaCondiComisiones = "";
		
		Long idComision = new Long(request.getParameter("idComision"));
		Long idComisionApli = new Long(request.getParameter("idComisionApli"));
		parameters.put("origenLlamada", "detalleComisionApli");
		parameters.put("muestraDiv", "true");
		try {
			comisionApliBean = ficheroComisionApliService.getDatosComisiones(idComisionApli);
			
			marcaCondiComisionesApli = ficheroComisionApliService.getMarcaCondComisionesApli(idComisionApli);
			parameters.put("marcaCondiComisionesApli", marcaCondiComisionesApli);
			
			marcaCondiComisiones = ficheroComisionApliService.getMarcaCondComisiones(idComision);
			parameters.put("marcaCondiComisiones", marcaCondiComisiones);
			return doConsulta(request, response, comisionApliBean).addAllObjects(parameters);
		
		} catch (BusinessException e) {
			logger.error("error al obtener los datos");
			return doConsulta(request, response, comisionApliBean);
		}
		
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setFicheroComisionApliService(
			IFicheroComisionApliService ficheroComisionApliService) {
		this.ficheroComisionApliService = ficheroComisionApliService;
	}

	public void setFaseManager(FaseManager faseManager) {
		this.faseManager = faseManager;
	}
}