/**
 * 
 */
package com.rsi.agp.core.webapp.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.rsi.agp.core.managers.IRevisionComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

/**
 * @author U028982
 * 
 */
public class RevisionComisionesController extends BaseMultiActionController {

	private static final Log LOOGER = LogFactory.getLog(RevisionComisionesController.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private IRevisionComisionesManager revisionComisionesManager;
	
	
	/**
	 * Cambia los parametros de comisiones de una póliza
	 * @author U028982
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	public ModelAndView doRestaurarParams(HttpServletRequest request, HttpServletResponse response) {
		
		LOOGER.debug("Init - doRestaurarParams");
		
		ModelAndView mv = null;
		Map<String, String> parameters = new HashMap<String, String>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String numeroPoliza 			= StringUtils.nullToString(request.getParameter("numeroPoliza"));
		String comMaxP				= null;
		String pctadministracionP 	= null;
		String pctadquisicionP 		= null;
		String pctEntidadP 			= null;
		String pctESMedP 			= null;
		String grupoNegocio			= null;
		try {			
			int nPol=1;
			if(!numeroPoliza.equals("")){
				nPol=Integer.parseInt(numeroPoliza);
			}
			for(int x=0;x<nPol;x++){		
					 comMaxP				= StringUtils.nullToString(request.getParameter("comMaxP"+(x+1)));
					 pctadministracionP 	= StringUtils.nullToString(request.getParameter("pctadministracionP"+(x+1)));
					 pctadquisicionP 		= StringUtils.nullToString(request.getParameter("pctadquisicionP"+(x+1)));
					 pctEntidadP 			= StringUtils.nullToString(request.getParameter("pctEntidadP"+(x+1)));
					 pctESMedP 				= StringUtils.nullToString(request.getParameter("pctESMedP"+(x+1)));
					 grupoNegocio 			= StringUtils.nullToString(request.getParameter("grupoNegocio"+(x+1)));
				
				
				String idPlz = StringUtils.nullToString(request.getParameter("idPlz"));
				parameters = revisionComisionesManager.cambiaParametrosComisiones(usuario, comMaxP, pctadministracionP, pctadquisicionP,
																					pctEntidadP, pctESMedP, idPlz, grupoNegocio);
			}
		
		} catch (Exception e) {
			LOOGER.info("Se ha producido un error al modificar los parametros de comisiones: " + e.getMessage());
			parameters.put("alerta",bundle.getString("mensaje.comisiones.parametros.modificacion.KO"));
		}
		//parametros para la redirección
		parameters.put("recogerPolizaSesion","true");
		parameters.put("operacion","consultar");
		
		mv = new ModelAndView(new RedirectView("utilidadesPoliza.html"));
		mv.addAllObjects(parameters);
		LOOGER.debug("End - doRestaurarParams");
			
		return mv;
	}

	public void setRevisionComisionesManager(
			IRevisionComisionesManager revisionComisionesManager) {
		this.revisionComisionesManager = revisionComisionesManager;
	}
}
