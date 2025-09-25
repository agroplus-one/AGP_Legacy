package com.rsi.agp.core.webapp.action.utilidades;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.managers.impl.InformesDetalleManager;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;

public class InformesDetalleController extends BaseMultiActionController{
	
	private static final Log logger = LogFactory.getLog(InformesDetalleController.class);
	
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
	
	private InformesDetalleManager informesDetalleManager;
	
	/**
	 * @param request { tipo: 'poliza' | 'situacion_actualizada', idPoliza}
	 * @param response
	 * @return
	 */
	public ModelAndView doInformesDetalle(HttpServletRequest request, HttpServletResponse response, Object bean) throws Exception  {
		
		logger.debug("InformesDetalleController - doInformesDetalle # Init");
		
		Map<String, Object> parametros = new HashMap<String, Object>();		
		ModelAndView mv = null;
		
		try {
			
			String tipo = request.getParameter("tipo"); 
			String idPoliza = request.getParameter("idPoliza"); 
			
			if (StringUtils.isNullOrEmpty(idPoliza) || StringUtils.isNullOrEmpty(tipo)) {
				parametros.put("alerta", "No se han recibido todos los parámetros de entrada");
			} else if (!tipo.equals("poliza") && !tipo.equals("situacion_actualizada")) {
				parametros.put("alerta", "El parámetro [tipo] no es válido");
			} else {
				HSSFWorkbook wb;
				if (tipo.equals("situacion_actualizada")) {
					String realPath = this.getServletContext().getRealPath("/WEB-INF/");
					wb = informesDetalleManager.informesDetalleSitAct(Long.valueOf(idPoliza), realPath);
					response.setHeader(CONTENT_DISPOSITION, "attachment; filename=informesDetalleSitAct_" + idPoliza + ".xls");
				} else {
					wb = informesDetalleManager.informesDetallePoliza(Long.valueOf(idPoliza));
					response.setHeader(CONTENT_DISPOSITION, "attachment; filename=informesDetallePoliza_" + idPoliza + ".xls");
				}
				
				response.setContentType(APPLICATION_VND_MS_EXCEL);
				wb.write(response.getOutputStream());
				response.getOutputStream().flush();				
			}			
		} catch (Exception e) {
			parametros.put("alerta", e.getMessage());
			mv = new ModelAndView(new RedirectView("utilidadesPoliza.html"));
			mv.addAllObjects(parametros);
		}
		
		logger.debug("InformesDetalleController - doInformesDetalle # END");

		return mv;	
	}

	public void setInformesDetalleManager(InformesDetalleManager informesDetalleManager) {
		this.informesDetalleManager = informesDetalleManager;
	}
}