/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  21/06/2010  Ernesto Laura     Controlador de las paginas relacionadas con 
* 											importaciones y sus historicos      
*
 **************************************************************************************************
*/
package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.RejectedExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.ImportacionManager;
import com.rsi.agp.core.managers.impl.PlsqlManager;
import com.rsi.agp.core.plsql.PlsqlExecutorService;
import com.rsi.agp.core.webapp.util.StringUtils;

public class ImportacionesController extends BaseSimpleController implements Controller {
	//Atributos para lanzar las cargas
	private PlsqlExecutorService plsqlExecutionService;
	private PlsqlManager plsqlManager;
	
	//Atributos para la consulta del histórico
	private ImportacionManager importacionManager;
	
	public ImportacionesController() {
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {	
		ModelAndView mv = null;				

		String accion = StringUtils.nullToString(request.getParameter("operacion"));
		String datosSesion = "0";
		
	    String [] opt    = accion.split(";");
	    accion = opt[0];
	    if (opt.length>1)
	    	datosSesion = opt[1];
	    
	    if (accion.equalsIgnoreCase("importarTodo")){
			mv = handleImportar (request, response, command, errors, ImportacionManager.IMPORTACION_COMPLETA);
		}
		else if (accion.equalsIgnoreCase("importarOrganizador")){
			mv = handleImportar (request, response, command, errors, ImportacionManager.IMPORTACION_ORGANIZADOR);
		} 
		else if (accion.equalsIgnoreCase("importarCondGeneral")){
			mv = handleImportar (request, response, command, errors, ImportacionManager.IMPORTACION_COND_GENERAL);
		} 
		else if (accion.equalsIgnoreCase("importarCondPL")){
			mv = handleImportar (request, response, command, errors, ImportacionManager.IMPORTACION_PLAN_LINEA);
		}
		else if (accion.equalsIgnoreCase("historico")){
			mv = handleCargaDatosBasicosHistorico(request, response, command, errors);				
		}
		else if (accion.equalsIgnoreCase("consulta")){
			mv = handleConsultaHistorico(request, response, command, errors, datosSesion);					
		}
		else if (accion.equalsIgnoreCase("detalleTablas")){
			mv = handleConsultaDetalleHistorico(request, response, command, errors);
		}
		else if (accion.equalsIgnoreCase("limpiar"))
		{
			mv = handleCargaDatosBasicosHistorico(request, response, command, errors);
		}
		else if (accion.equalsIgnoreCase("volver"))
		{
			String vienesDe = StringUtils.nullToString(request.getParameter("comeFrom"));
			if ("historico".equalsIgnoreCase(vienesDe))
			{
				mv = handleConsultaHistorico(request, response, command, errors, datosSesion);
			}
			else
			{
				mv = new ModelAndView("moduloTaller/activacion/activacionlineas");
			}
		}
		else {
			//Menu
			Map<String, Object> parameters = importacionManager.cargaDatosBasicosImportaciones();
			//Guardo la relacion fichero->tablas en sesion
			request.getSession().setAttribute("fichTablas", parameters.get("fichTablas"));
			mv = new ModelAndView("moduloTaller/importaciones/importacion", "importaciones", parameters);
		}
		
		return mv;
	}
	
	private ModelAndView handleImportar (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors, int tipoImportacion) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;
		
		String classpath = ResourceBundle.getBundle("agp_importacion").getString("classpath");//this.getServletContext().getRealPath("/WEB-INF/lib");
		
        try{
    		//en tablas tendremos nombre del fichero y número de tabla separados por - y , (fichero1-tabla1,fichero2-tabla2,...)
        	String[] tablasFicheros = StringUtils.nullToString(request.getParameter("tablas")).split(
        			ResourceBundle.getBundle("agp_importacion").getString("importacion.separador"));
        	
        	String tablas = "";
        	String ficheros = "";
        	
        	for (String tabFich: tablasFicheros){
        		if (!StringUtils.nullToString(tabFich).equals("")){
	        		String[] tabFichSep = tabFich.split("-");
	        		ficheros += tabFichSep[0] + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        		tablas += tabFichSep[1] + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        		if (tabFichSep[1].equals("8")){
	        			//En caso de estar tratando la tabla de CaracteristicasGrupoTasas, también incluyo la que depende del riesgo
	        			ficheros += tabFichSep[0] + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        			tablas += "8R" + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        		}else if  (tabFichSep[1].equals("406")){
	        			//En caso de estar tratando la tabla de CaracteristicasGrupoTasas, también incluyo la que depende del riesgo G
	        			ficheros += tabFichSep[0] + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        			tablas += "406R" + ResourceBundle.getBundle("agp_importacion").getString("importacion.separador");
	        		}
        		}
        	}
        	logger.debug("Controlador de importaciones - Tablas: "+tablas+" - Ficheros: "+ficheros);

        	//Validamos el plan/línea introducido por el usuario. Debe coindicir en todos los ficheros seleccionados.
    		boolean correcto = this.importacionManager.validarPlanLinea(StringUtils.nullToString(request.getParameter("plan")),
    				StringUtils.nullToString(request.getParameter("linea")), 
    				StringUtils.nullToString(request.getParameter("ruta")), 
    				ficheros.split(ResourceBundle.getBundle("agp_importacion").getString("importacion.separador")), 
    				tipoImportacion);
    		
    		logger.debug("Validacion de los ficheros realizada. Resultado: " + correcto);
    		
    		if (correcto){
	    		logger.info("Starting PlsqlExecutionService...");
	        	logger.debug("ruta: " + StringUtils.nullToString(request.getParameter("ruta")));
	        	logger.debug("plan: " + StringUtils.nullToString(request.getParameter("plan")));
	        	logger.debug("linea: " + StringUtils.nullToString(request.getParameter("linea")));
	        	logger.debug("tablas: " + StringUtils.nullToString(request.getParameter("tablas")));
	        	
	        	//Establecemos las propiedades necesarias para lanzar el proceso de importación
	        	this.plsqlExecutionService.setPlan(StringUtils.nullToString(request.getParameter("plan")));
	        	this.plsqlExecutionService.setLinea(StringUtils.nullToString(request.getParameter("linea")));        	
	        	this.plsqlExecutionService.setTipoImportacion(tipoImportacion);
	        	this.plsqlExecutionService.setClasspath(classpath);
	        	
	        	//el último separador se quita en PlsqlServiceImpl
	        	this.plsqlExecutionService.setTablas(tablas);
	        	this.plsqlExecutionService.setFichTablas(ficheros);
	    	
	        	this.plsqlExecutionService.start();
	        	parameters = plsqlManager.cargaDatosEjecucionOK();
	        	mv = new ModelAndView("moduloTaller/importaciones/resultadosImportacion", "importaciones", parameters);
    		}
    		else{
    			logger.debug("Los ficheros no corresponden con el plan/línea introducido");
    			parameters = importacionManager.cargaDatosBasicosImportaciones();
    			//Guardo la relacion fichero->tablas en sesion
    			request.getSession().setAttribute("fichTablas", parameters.get("fichTablas"));
    			mv = new ModelAndView("moduloTaller/importaciones/importacion", "importaciones", parameters)
    					.addObject("alerta", ResourceBundle.getBundle("agp_importacion").getString("importacion.plan_linea.KO"));
    		}
            return mv;
        }
        catch(RejectedExecutionException ex){
           	parameters = plsqlManager.cargaDatosTodaviaEnEjecucion();
        	mv = new ModelAndView("moduloTaller/importaciones/resultadosImportacion", "importaciones", parameters);
            return mv;
        }
        catch(Exception ex){
        	logger.error("Error al iniciar la importacion", ex);
           	parameters = plsqlManager.cargaDatosError();
        	mv = new ModelAndView("moduloTaller/importaciones/resultadosImportacion", "importaciones", parameters);
            return mv;
        }
	}
	
	private ModelAndView handleCargaDatosBasicosHistorico (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		if (request.getSession().getAttribute("filtroHistorico") != null)
		{
			request.getSession().removeAttribute("filtroHistorico");
			request.getSession().removeAttribute("listalineas");
		}
		
		Map<String, Object> parameters = importacionManager.cargaDatosBasicosHistorico();
		return new ModelAndView("moduloTaller/importaciones/historico", "historico", parameters);
	}
	
	private ModelAndView handleConsultaHistorico (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors, String datosSesion) throws Exception
	{
		HashMap<String, Object> filtroConsulta = new HashMap<String, Object>();
		String plan = StringUtils.nullToString(request.getParameter("sl_planes"));
		String lineaSeguro = StringUtils.nullToString(request.getParameter("sl_lineas"));
		lineaSeguro = lineaSeguro.substring(lineaSeguro.lastIndexOf(";")+1);
		String tiposc = StringUtils.nullToString(request.getParameter("sl_tipo"));
		String estado = StringUtils.nullToString(request.getParameter("sl_estado"));
		String fxDesde = StringUtils.nullToString(request.getParameter("fechaIni"));
		String fxHasta = StringUtils.nullToString(request.getParameter("fechaFin"));
		filtroConsulta.put("plan", plan);
		filtroConsulta.put("linea", lineaSeguro);
		filtroConsulta.put("tiposc", tiposc);
		filtroConsulta.put("estado", estado);
		filtroConsulta.put("fechaD", fxDesde);
		filtroConsulta.put("fechaH", fxHasta);		

		if (StringUtils.nullToString(request.getParameter("nuevo")).equals("1"))
			request.getSession().setAttribute("filtroHistorico", null);
		
		Map<String, Object> parameters = importacionManager.consultaHistorico(request, plan, lineaSeguro, tiposc, estado, fxDesde, fxHasta);
		parameters.put("filtro", filtroConsulta);
		return new ModelAndView("moduloTaller/importaciones/historico", "historico", parameters);
	}
	
	private ModelAndView handleConsultaDetalleHistorico (HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		String idHistorico = request.getParameter("seleccionado");
		
		Map<String, Object> parameters = importacionManager.consultaDetalleHistorico(idHistorico);
		parameters.put("comeFrom", "historico");
		parameters.put("id", idHistorico);
		return new ModelAndView("moduloTaller/importaciones/detallehistorico", "detHistorico", parameters);		
	}	

	public void setImportacionManager(ImportacionManager importacionManager) {
		this.importacionManager = importacionManager;
	}

	public void setPlsqlExecutionService(PlsqlExecutorService plsqlExecutionService) {
		this.plsqlExecutionService = plsqlExecutionService;
	}

	public void setPlsqlManager(PlsqlManager plsqlManager) {
		this.plsqlManager = plsqlManager;
	}	
}
