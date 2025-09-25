package com.rsi.agp.core.webapp.action.mtoinf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadorCamposGenericosService;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.mtoinf.OperadorCampoGenerico;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public class MtoOperadorCamposGenericosController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(MtoOperadorCamposGenericosController.class);
	private IMtoOperadorCamposGenericosService mtoOperadorCamposGenericosService;
	private String successView;
	
	/**
	 * Realiza la consulta de operadores de campos de informe que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param informeBean Objeto que encapsula el filtro de búsqueda
	 * @return ModelAndView que contiene la redireccion a la página de mantenimiento de operadores de campos de informe
	 * @throws Exception 
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, OperadorCampoGenerico operadorCamposGenerico) throws Exception {
		// Obtiene el usuario de la sesion y su sesion
    	final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
    	String idOperador = "";
    	String tablaOrigen = "";
    	String campo = "";
    	String perfil = usuario.getPerfil().substring(4);
    	// Map para guardar los parámetros que se pasarán a la jsp
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	String html = null;
    	String origenLlamada = request.getParameter("origenLlamada");
    	if ("".equals(origenLlamada)){
    		origenLlamada = (String)request.getAttribute("origenLlamada");
    	}
    	perfil = usuario.getPerfil().substring(4);
    	parameters.put("perfil", perfil);
    	
    	OperadorCampoGenerico opGenericoBusqueda = (OperadorCampoGenerico) operadorCamposGenerico;
    	// ----------------------------------
    	// -- Carga del mapa de parámetros --
        // ----------------------------------
    	List<Vista> lstVistas = mtoOperadorCamposGenericosService.getListadoVistas();
    	html = mtoOperadorCamposGenericosService.getTablaOperadorCampos(request, response, opGenericoBusqueda, origenLlamada); 
        
		if (html == null) {
			return null; // an export
		} else {
			String ajax = request.getParameter("ajax");
			// Llamada desde ajax
			if (ajax != null && ajax.equals("true")) {
				byte[] contents = html.getBytes("UTF-8");
				response.getOutputStream().write(contents);
				return null;
			} else {
				// Pasa a la jsp el codigo de la tabla a traves de este atributo
				request.setAttribute("opGenerico", html);
			}
		}
		
		if ("menuGeneral".equals(origenLlamada)){
			parameters.put("idVista", "1");
		}
    	parameters.put("lstVistas", lstVistas);
    	String isCalcOPermMain3 = request.getParameter("isCalcOPermMain3");
    	parameters.put("isCalcOPermMain3", isCalcOPermMain3);
    	String idcampoCalc = request.getParameter("idcampoCalc");
    	parameters.put("idcampoCalc", idcampoCalc);
    	parameters.put("idOpCamposPermitido", request.getParameter("idOpCampoPermitido"));
    	parameters.put("idOpCalculado", request.getParameter("idOpCalculado"));
    	
    	// -----------------------------------
    	// -- Carga de la lista de Operadores --
        // -----------------------------------
    	parameters.put("listaOperadores", mtoOperadorCamposGenericosService.getListaOperadores());
    	
		parameters.put("origenLlamada", origenLlamada);
		parameters.put("tablaOrigenStr", tablaOrigen);
		parameters.put("campoStr", campo);
		parameters.put("operadorStr", idOperador);
		
		OperadorCamposPermitido opCampoPerm = new OperadorCamposPermitido();
		parameters.put("operadorCamposPermitidosBean", opCampoPerm);
		
		OperadorCamposCalculados opCampoCalc = new OperadorCamposCalculados();
		parameters.put("operadorCamposCalculadosBean", opCampoCalc);
		
		String idVistaC = request.getParameter("idVistaC");
		parameters.put("idVistaC", idVistaC);
		String mensaje = request.getParameter("mensaje");
		String alerta = request.getParameter("alerta");
        if (alerta!=null){
        	parameters.put("alerta", alerta);
        }
        if (mensaje !=null){
			parameters.put("mensaje", mensaje);
		}
		parameters.put("perfil", perfil);
		logger.debug("Establecemos perfil");
    	
		// -----------------------------------------------------------------
    	// -- Se crea el objeto que contiene la redireccion y se devuelve --
        // -----------------------------------------------------------------
    	ModelAndView mv = new ModelAndView(successView);
       	mv = new ModelAndView(successView, "operadorCamposGenerico", opGenericoBusqueda);
       	mv.addAllObjects(parameters);
       	logger.debug("end - MtoCamposPermitidosController - doConsulta");
    	return mv; 
	}
	
	/**
	 * Setter del Service para Spring
	 * @param mtoOperadorCamposGenericosService
	 */
	public void setMtoOperadorCamposGenericosService(IMtoOperadorCamposGenericosService mtoOperadorCamposGenericosService) {
		this.mtoOperadorCamposGenericosService = mtoOperadorCamposGenericosService;
	}

	/**
	 * Setter de propiedad para Spring
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	
}
