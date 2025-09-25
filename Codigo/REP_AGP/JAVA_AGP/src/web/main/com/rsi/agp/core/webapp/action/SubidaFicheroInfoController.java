package com.rsi.agp.core.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;

public class SubidaFicheroInfoController extends BaseSimpleController{
	private final static Log logger = LogFactory.getLog(SubidaFicheroInfoController.class);
	
    public SubidaFicheroInfoController() {
        setCommandClass(Object.class);
    }
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
				
		JSONObject jsonResp = new JSONObject();
		try {
			HttpSession session = request.getSession();
						
			if(session.getAttribute("progressStatus") != null && session.getAttribute("progress") != null){
				if(session.getAttribute("progressStatus").equals("DONE")){
					session.removeAttribute("progress");
					session.removeAttribute("progressStatus");
					
					jsonResp.put("progress", 100);
					jsonResp.put("result", "DONE");
					
				}
				else{
					
					jsonResp.put("progress", session.getAttribute("progress"));
					jsonResp.put("result", session.getAttribute("progressStatus"));
					
				}
				
				if (session.getAttribute("texto") != null && !"".equals(session.getAttribute("texto"))) {
					jsonResp.put("texto", session.getAttribute("texto"));
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e);
			jsonResp.put("result", "error");
		} finally {
			response.getWriter().print(jsonResp);
            response.flushBuffer();
		}
		
		return null;
	}
	
}
