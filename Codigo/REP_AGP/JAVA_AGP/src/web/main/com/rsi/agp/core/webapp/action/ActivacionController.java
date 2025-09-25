/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  16/07/2010  Ernesto Laura     Controlador de las paginas relacionadas con 
* 											activacion de planes
*
 **************************************************************************************************
*/
package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.ActivacionLineasManager;
import com.rsi.agp.core.managers.impl.AjaxManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.log.HistImportaciones;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ActivacionController extends BaseSimpleController implements Controller  {
	
	private static final Log LOGGER = LogFactory.getLog(ActivacionController.class);
	
	private ActivacionLineasManager activacionLineasManager;	
	private AjaxManager ajaxManager;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public ActivacionController() {
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		ModelAndView mv = null;				
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		String accion = StringUtils.nullToString(request.getParameter("operacion"));
	    String [] opt    = accion.split(";");
	    accion = opt[0];
	    if (accion.equals("")){
	    	accion="consultar";
	    }
	    if (accion.equals("consultar")) {
	    	mv = handleConsulta(request, response, command, errors);
	    }	   
	    else if (accion.equals("selecccionar")) {
	    	mv = consultarDatosPL(request);
	    }
	    else if (accion.equals("activar")) {
	    	mv = activarPL(request);
	    }
	    else if (accion.equals("bloquear")) {
	    	mv = bloquearPL(request);
	    }
	    else if (accion.equals("detalleTablas")) {
	    	mv = detalleTablas(request);
	    }
	    else if (accion.equals("coberturas")){
	    	mv = detalleCoberturas(request);
	    }
	    else if (accion.equals("limpiar")){
	    	mv = handleCargaDatosIniciales(request, response, command, errors);
	    }
	    /*else if(accion.equalsIgnoreCase("ajax_modulos")){
				String codmodulo = request.getParameter("codmodulo");
				String idlinea = request.getParameter("idlinea");
				String idtabla = request.getParameter("idtabla");
				
				JSONObject modulo = polizaManager.getCoberturasModulo(codmodulo, idtabla, Long.parseLong(idlinea),false);
				getWriterJSON(response, modulo); 
	    }*/else if(accion.equals("volverClase")){
	    	// DAA 25/01/2013    
	    	parameters.put("vieneDeCicloPoliza", true);
			parameters.put("origenLlamada", "cicloPoliza");
	    	mv = new ModelAndView("redirect:/cargaClase.run", parameters);
	    }
	    //comento esto porque nunca va a entrar por aqui
	   /* else
	    {
	    	//Para todo lo demás...
	    	mv = handleCargaDatosIniciales(request, response, command, errors);
	    }*/
	    
		return mv;
	}
	
	private ModelAndView handleConsulta (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		parameters = activacionLineasManager.consulta(request);
		return new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
	}
	
	private ModelAndView consultarDatosPL (HttpServletRequest request) throws Exception
	{
		HistImportaciones hist = activacionLineasManager.consultaFilaPL (new Long(request.getParameter("ROW")));
		
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		parameters = activacionLineasManager.cargaDatosBasicosActivacion(request, false, null, usuario.getTipousuario());
		parameters.put("RowHistoricoImp", hist);
		parameters.put("coberturason", "SI");
		Linea linea = ajaxManager.getPlanLinea(hist.getLinea().getLineaseguroid());
		parameters.put("tipoMenu", "menuGeneral");
    	parameters.put("PlanLinea", linea);
		return new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
	}
	
	private ModelAndView activarPL (HttpServletRequest request) throws Exception{
		
		String idHistorico = StringUtils.nullToString(request.getParameter("lineaSeguroSelect"));
		String fechaActivacion = StringUtils.nullToString(request.getParameter("fechaActiv"));
		//DAA 29/01/13 forzarActivar
		Boolean forzarActivar = Boolean.valueOf(StringUtils.nullToString(request.getParameter("forzarActivar")));
		Map<String, Object> parameters2 = activacionLineasManager.activarPL(idHistorico, fechaActivacion, forzarActivar);
		
		parameters = activacionLineasManager.consulta(request);
		Integer errores = (Integer)parameters2.get("error");
		ArrayList<String> erroresWeb = new ArrayList<String>();
		
		if (errores.intValue() != 0){
			//resultado -1: Si alguna de las tablas importadas no tiene estado 'Importado'
			//resultado -2: Si existe alguna tabla del condicionado para este P/L que no tiene registros
			//resultado -3: Si no estan configuradas las pantallas configurables
			//resultado -4: Si alguna de las pantallas configurables no tiene datos variables configurados
			switch (errores.intValue()){
				case 1: parameters.put("mensaje", bundle.getString("mensaje.activavion.planLinea.OK"));
						break;
				case -1: erroresWeb.add(bundle.getString("mensaje.activacion.importacion.KO"));
						break;
				case -2: erroresWeb.add(bundle.getString("mensaje.activacion.registros.KO"));
				 		break;
				case -3: erroresWeb.add(bundle.getString("mensaje.activacion.pantallasConfigurables.KO"));
				 		break;
				case -4: erroresWeb.add(bundle.getString("mensaje.activacion.camposConfigurados.KO"));
				 		break;
				default:
						break;
			}			
			parameters.put("alerta2", erroresWeb);
		}
		else{
			parameters.put("mensaje", bundle.getString("mensaje.activavion.planLinea.OK"));
		}
		
		parameters.put("tipoMenu", "menuGeneral");
		return new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
	}
	
	private ModelAndView bloquearPL (HttpServletRequest request) throws Exception
	{
		String idHistorico = StringUtils.nullToString(request.getParameter("ROW"));
		parameters = activacionLineasManager.consulta(request);
		parameters.put("tipoMenu", "menuGeneral");
		String mensaje = activacionLineasManager.bloquearPL (idHistorico);
		parameters.put("mensaje",mensaje);
		return new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
	}
	
	private ModelAndView detalleTablas (HttpServletRequest request) throws Exception
	{
		String idLinea = StringUtils.nullToString(request.getParameter("ROW"));
		parameters = activacionLineasManager.consultaDetalleActivacion(idLinea);
		parameters.put("comeFrom", "activacion");
		parameters.put("ROW", idLinea);
		parameters.put("tipoMenu", "menuGeneral");
		return new ModelAndView("moduloTaller/activacion/detalleTablas", "detHistorico", parameters);
	}
	
	private ModelAndView detalleCoberturas (HttpServletRequest request) throws Exception {
		
		ModelAndView mv;
		
		String lineaSeguroId = StringUtils.nullToString(request.getParameter("lineaSeguroSelect"));
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		try {
			if (!StringUtils.isNullOrEmpty(lineaSeguroId) && activacionLineasManager.mostrarCoberturas(Long.valueOf(lineaSeguroId))) {
				parameters = activacionLineasManager.dameListaModulos(new Long(lineaSeguroId), "coberturas", usuario.getTipousuario());
				parameters.put("tipoMenu", "menuTaller");
				parameters.put("esGanado", request.getParameter("esGanado"));			
				mv = new ModelAndView("moduloTaller/activacion/detalleCoberturas", "detCoberturas", parameters);
			} else {
				parameters.put("alerta2", "No hay coberturas que mostrar.");
				mv = new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
			}
		} catch (Exception ex) {
			LOGGER.error(ex);
			parameters.put("alerta2", ex.getMessage());
			mv = new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
		}
		return mv;
	}
	
	private ModelAndView handleCargaDatosIniciales (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		
		parameters = activacionLineasManager.cargaDatosBasicosActivacion(request, true, null, usuario.getTipousuario());
		return new ModelAndView("moduloTaller/activacion/activacionlineas", "activacion", parameters);
	}
	
	public void setActivacionLineasManager(
			ActivacionLineasManager activacionLineasManager) {
		this.activacionLineasManager = activacionLineasManager;
	}

	public void setAjaxManager(AjaxManager ajaxManager) {
		this.ajaxManager = ajaxManager;
	}
	
}
