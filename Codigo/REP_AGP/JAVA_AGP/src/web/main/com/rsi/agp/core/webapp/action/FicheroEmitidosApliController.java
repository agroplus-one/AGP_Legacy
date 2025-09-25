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
import com.rsi.agp.core.jmesa.service.IFicheroEmitidosApliService;
import com.rsi.agp.core.managers.impl.FaseManager;
import com.rsi.agp.core.webapp.util.StringUtils;

import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitido;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoAplicacion;

public class FicheroEmitidosApliController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(FicheroEmitidosApliController.class);
	private IFicheroEmitidosApliService ficheroEmitidosApliService;
	private String successView;
	private FaseManager faseManager;
	
	/**
	 * Realiza la consulta de los datos de emitidos aplicacion
	 * 
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 *            Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que realiza la redirección
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request,
			HttpServletResponse response, ReciboEmitidoAplicacion emitidoapli) {

		logger.info("doConsulta - init");
		HashMap<String, String> parameters = new HashMap<String, String>();
		String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
		
		if (emitidoapli.getId()== null){
			Long idemitido = new Long(request.getParameter("idEmitidos"));
			emitidoapli.setReciboEmitido(new ReciboEmitido());
			emitidoapli.getReciboEmitido().setId(idemitido);
			parameters.put("idEmitidos", idemitido.toString());
		}else{
			parameters.put("idEmitidos", emitidoapli.getReciboEmitido().getId().toString());
			parameters.put("idEmitidosApli", emitidoapli.getId().toString());
		}
		
		Long idFichero = new Long (request.getParameter("idFichero"));
		
		String html = ficheroEmitidosApliService.getTablaEmitidosApli(request,
					response, emitidoapli, origenLlamada);
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
					request.setAttribute("listadoEmitidosApli", html);
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
			
		
		return new ModelAndView(successView, "emitidosApliBean",
				emitidoapli).addAllObjects(parameters);

	}
	
	/**
	 * @param request
	 * @param response
	 * @param cargasFicherosBean
	 * @return
	 */
	public ModelAndView doVolver(HttpServletRequest request,
			HttpServletResponse response,
			ReciboEmitidoAplicacion emitidoApli) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put("idFichero", StringUtils.nullToString(request.getParameter("idFichero")));
		parameters.put("nombreFichero",StringUtils.nullToString(request.getParameter("nombreFichero")));
		parameters.put("estado",StringUtils.nullToString(request.getParameter("estado")));
		parameters.put("fase",StringUtils.nullToString(request.getParameter("fase")));
		
		return new ModelAndView("redirect:/ficheroEmitidos.run").addObject(
				"method", "doConsulta").addAllObjects(parameters);
	
		
	}

	
	public ModelAndView doDetalle(HttpServletRequest request,
			HttpServletResponse response,
			ReciboEmitidoAplicacion emitidoApli) {
		
		HashMap<String,String> parameters = new HashMap<String,String>();
		
		
		//Long idEmitidos = new Long(request.getParameter("idEmitidos"));
		Long idEmitidosApli = new Long(request.getParameter("idEmitidosApli"));
		parameters.put("origenLlamada", "detalleEmitidosApli");
		parameters.put("muestraDiv", "true");
		try {
			emitidoApli = ficheroEmitidosApliService.getDatosEmitidos(idEmitidosApli);
			
			return doConsulta(request, response, emitidoApli).addAllObjects(parameters);
		
		} catch (BusinessException e) {
			logger.error("error al obtener los datos");
			return doConsulta(request, response, emitidoApli);
		}
		
	}


	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public void setFicheroEmitidosApliService(
			IFicheroEmitidosApliService ficheroEmitidosApliService) {
		this.ficheroEmitidosApliService = ficheroEmitidosApliService;
	}
	
	public void setFaseManager(FaseManager faseManager) {
		this.faseManager = faseManager;
	}
}