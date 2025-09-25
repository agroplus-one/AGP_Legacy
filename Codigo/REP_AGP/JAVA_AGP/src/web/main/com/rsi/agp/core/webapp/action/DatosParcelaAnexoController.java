
/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Controlador para datosParcela.jsp
*
 **************************************************************************************************
*/

package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class DatosParcelaAnexoController extends BaseSimpleController implements Controller {
	
	private Map<String, Object> parameters = new HashMap<String, Object>();
	protected final Log logger = LogFactory.getLog(getClass());
	
	public DatosParcelaAnexoController()
	{
		setCommandClass(String.class);
		setCommandName("string");
	}

	public ModelAndView handle(HttpServletRequest request,HttpServletResponse response, Object object, BindException exception)throws Exception {
        ModelAndView mv = null;
	    
	    parameters.clear();
	    
		try {
			
		    String idpoliza = request.getParameter("idpoliza");
			String codplan = request.getParameter("codplan");
			String codlinea = request.getParameter("codlinea");
			String idpantalla = request.getParameter("idpantalla");
			String despantalla = request.getParameter("despantalla");
			String codModulo = request.getParameter("codModulo");
			String fechaInicioContratacion = request.getParameter("fechaInicioContratacion");
			
			parameters.put("idpoliza", idpoliza);
			parameters.put("codplan", codplan);
			parameters.put("codlinea", codlinea);
			parameters.put("idpantalla", idpantalla);
			parameters.put("despantalla", despantalla);
			parameters.put("codModulo", codModulo);
			parameters.put("fechaInicioContratacion", fechaInicioContratacion);

			mv = new ModelAndView("moduloPolizas/polizas/datosParcelaAnexo", "parametros", parameters);
		} catch (Exception excepcion){
			logger.error("Excepcion : DatosParcelaAnexoController - handle", excepcion);
		}
		return mv;
	}
}
