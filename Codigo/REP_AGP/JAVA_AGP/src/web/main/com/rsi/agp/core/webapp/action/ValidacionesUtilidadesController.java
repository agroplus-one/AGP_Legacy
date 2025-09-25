package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.IValidacionesUtilidadesManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.WebServicesManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

public class ValidacionesUtilidadesController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(ValidacionesUtilidadesController.class);	
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private IValidacionesUtilidadesManager validacionesUtilidadesManager;
	private IConsultaSbpManager ConsultaSbpManager;
	private PolizaManager polizaManager;
	private WebServicesManager webServicesManager;
	
	/**
	 * Comprueba que todas las polizas cuyos ids se pasan por parámetro se pueden borrar.
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("all")
	public void doAjaxCheckBorradoMultiple (HttpServletRequest request, HttpServletResponse response) {
		
		log ("doAjaxCheckBorradoMultiple", "inicio");
		
		// Se obtiene el listado de ids de poliza a comprobar
		String listIdPolizas = request.getParameter("idsRowsChecked");
		log ("doAjaxCheckBorradoMultiple", "Lista ids de poliza a validar: " + listIdPolizas);
		
		// Hace la llamada al manager para comprobar si las polizas se pueden borrar
		log ("doAjaxCheckBorradoMultiple", "Llamada a la validacion");
		boolean borrar = validacionesUtilidadesManager.validarPolizasBorradoMasivo(listIdPolizas);
		
		// Se escribe en el response el resultado de la validacion
		response.setCharacterEncoding("UTF-8");
		log ("doAjaxCheckBorradoMultiple", "Las polizas " + ((borrar) ? "" : "no ") + "se pueden borrar.");
		try {
			response.getWriter().write(new Boolean(borrar).toString());
		} catch (IOException e) {
			log ("doAjaxCheckBorradoMultiple", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);
		}
		
		log ("doAjaxCheckBorradoMultiple", "fin");
					
	}
	
	// DAA 11/06/2012
	/**
	 * Comprueba que todas las polizas cuyos ids se pasan por parámetro se pueden cambiar de oficina.
	 * @param request
	 * @param response
	 */
	public void doAjaxCheckCambioOficinaMultiple (HttpServletRequest request, HttpServletResponse response) {
		log ("doAjaxCheckCambioOficinaMultiple", "inicio");
		
		// Se obtiene el listado de ids de poliza a comprobar
		String listIdPolizas = request.getParameter("idsRowsChecked");
		log ("doAjaxCheckCambioOficinaMultiple", "Lista ids de poliza a validar: " + listIdPolizas);
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		// Hace la llamada al manager para comprobar si las polizas se pueden cambiar de oficina
		log ("doAjaxCheckCambioOficinaMultiple", "Llamada a la validacion");
		String cambiar = validacionesUtilidadesManager.validarPolizasCambioOficinaMultiple(listIdPolizas,
				Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil()));
		
		//Se escribe en el response el resultado de la validacion
		response.setCharacterEncoding("UTF-8");
		log ("doAjaxCheckCambioOficinaMultiple", "Las polizas " + ((cambiar.equals("false")) ? "no" : " ") + "se pueden cambiar de oficina.");
		try {
			response.getWriter().write(cambiar);
		} catch (IOException e) {
			log ("doAjaxCheckCambioOficinaMultiple", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);
		}
		
		log ("doAjaxCheckCambioOficinaMultiple", "fin");
					
	}
	
	
	// MPM - 21/06/2012
	// Se mueven los métodos de validacion de sobreprecio a este controlador desde el de consulta de polizas para sobreprecio
	// para evitar errores en el paso a definitiva con usuarios de perfil 4
	/**
	 * Comprueba si la poliza cuyo id se envia desde la jsp tiene polizas de sobreprecio asociadas
	 * @param request
	 * @param response
	 */
	public void do_Ajax_check_Cpl_Sbp(HttpServletRequest request, HttpServletResponse response) {
		boolean tieneCpl_Sbp = false;
		try{
			// Carga la informacion de la poliza correspondiente al id enviado desde la jsp
		    Poliza poliza = polizaManager.getPoliza(Long.parseLong(StringUtils.nullToString(request.getParameter("idPoliza"))));

		    // Solo se realiza la comprobacion si la poliza es principal
		    if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia())) 
		    	tieneCpl_Sbp = ConsultaSbpManager.getListaPolizasSbp(poliza,false);		    
		    
		    //Se escribe en el response el resultado de la validacion
		    log ("do_Ajax_check_Cpl_Sbp", "tieneCpl_sbp = " + tieneCpl_Sbp);
			response.setCharacterEncoding("UTF-8");			
			response.getWriter().write(new Boolean(tieneCpl_Sbp).toString());
		}
		catch(IOException e){
			log ("do_Ajax_check_Cpl_Sbp", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(NumberFormatException e){
			log ("do_Ajax_check_Cpl_Sbp", "Ocurrio un error al pasar a numerico el id de poliza.");
			logger.error("Ocurrio un error al pasar a numerico el id de poliza.", e);			
    	}
		catch(Exception e){
			log ("do_Ajax_check_Cpl_Sbp", "Ocurrio un error inesperado.");
			logger.error("Ocurrio un error inesperado.", e);			
    	}
	}
	
	
	
	public void do_Ajax_muestraDatosAval(HttpServletRequest request, HttpServletResponse response) {
		boolean esFinanciada = false;
		try{
			// Carga la informacion de la poliza correspondiente al id enviado desde la jsp
		    Poliza poliza = polizaManager.getPoliza(Long.parseLong(StringUtils.nullToString(request.getParameter("idPoliza"))));

		    // Solo se realiza la comprobacion si la poliza es principal
		    if (poliza.getDistribucionCoste2015s()!=null){
		    	for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
		    		if(null!= dc.getRecargoaval()){
		    			esFinanciada=true;
		    		}
		    	}
		    }
		    
		    //Se escribe en el response el resultado de la validacion
		    log ("do_Ajax_muestraDatosAval", "tieneFinanciacion = " + esFinanciada);
			response.setCharacterEncoding("UTF-8");			
			response.getWriter().write(new Boolean(esFinanciada).toString());
		}
		catch(IOException e){
			log ("do_Ajax_muestraDatosAval", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(NumberFormatException e){
			log ("do_Ajax_muestraDatosAval", "Ocurrio un error al pasar a numerico el id de poliza.");
			logger.error("Ocurrio un error al pasar a numerico el id de poliza.", e);			
    	}
		catch(Exception e){
			log ("do_Ajax_muestraDatosAval", "Ocurrio un error inesperado.");
			logger.error("Ocurrio un error inesperado.", e);			
    	}
	}
	
	/**
	 * Comprueba si alguna de las polizas cuyos ids se envia desde la jsp tienen polizas de sobreprecio asociadas
	 * @param request
	 * @param response
	 */
	public void do_Ajax_check_Multiple_Cpl_Sbp(HttpServletRequest request, HttpServletResponse response) {
    	
		// Obtiene la lista de ids separados por ';'
		String listIdPolizas = request.getParameter("idsRowsChecked");
    	
		// Convierte la lista de ids en un listado de polizas
    	List<Poliza> listPolizas = new ArrayList<Poliza>();
		try {
			listPolizas = ConsultaSbpManager.getListObjPolFromString(listIdPolizas);
		} catch (Exception e) {
			log ("do_Ajax_check_Multiple_Cpl_Sbp", "Ocurrio un error al conviertir la lista de ids en un listado de polizas.");
			logger.error("Ocurrio un error al conviertir la lista de ids en un listado de polizas.", e);			
		}
    
    	// Recorre las polizas y comprueba si alguna tiene polizas de sobreprecio asociadas    	
    	boolean tieneCpl_Sbp = false;
		try{
			for (Poliza poliza: listPolizas){
				// Realiza la comprobacion si la poliza es principal
				if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia()))
					tieneCpl_Sbp = ConsultaSbpManager.getListaPolizasSbp(poliza,false);
			    
				// Si tiene polizas de sobreprecio sale del bucle
				if (tieneCpl_Sbp) break;
			}
			
			//Se escribe en el response el resultado de la validacion
			log ("do_Ajax_check_Multiple_Cpl_Sbp", "tieneCpl_sbp = " + tieneCpl_Sbp);
			response.setCharacterEncoding("UTF-8");			
			response.getWriter().write(new Boolean(tieneCpl_Sbp).toString());
		}
		catch(IOException e){
			log ("do_Ajax_check_Multiple_Cpl_Sbp", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(Exception e){
			log ("do_Ajax_check_Multiple_Cpl_Sbp", "Ocurrio un error inesperado.");
			logger.error("Ocurrio un error inesperado.", e);			
    	}
	}
	
	// MPM - 21/06/2012 - FIN
	
	
	/**
	 * Comprueba si alguna de las polizas cuyos ids se envia desde la jsp estan financiadas
	 * @param request
	 * @param response
	 */
	public void do_Ajax_check_Multiple_Financiadas(HttpServletRequest request, HttpServletResponse response) {
    	
		// Obtiene la lista de ids separados por ';'
		String listIdPolizas = request.getParameter("idsRowsChecked");
    	
		// Convierte la lista de ids en un listado de polizas
    	List<Poliza> listPolizas = new ArrayList<Poliza>();
		try {
			listPolizas = ConsultaSbpManager.getListObjPolFromString(listIdPolizas);
		} catch (Exception e) {
			log ("do_Ajax_check_Multiple_Financiadas", "Ocurrio un error al conviertir la lista de ids en un listado de polizas.");
			logger.error("Ocurrio un error al convertir la lista de ids en un listado de polizas.", e);			
		}
    
    	// Recorre las polizas y comprueba si alguna esta financiada   	
    	boolean tieneFinanciadas = false;
    	Integer numFinanciadas = 0;
    	String esSaecaUnica = "";
		try{
			for(Poliza poliza: listPolizas){
				if(poliza.getDistribucionCoste2015s()!=null && poliza.getDistribucionCoste2015s().size()>0){
					//Sólo habrá uno en principio
					if(poliza.getDistribucionCoste2015s().iterator().next().getOpcionFracc()!=null){
						numFinanciadas = numFinanciadas + 1;
					}
				}
			}
			// Si tiene mas de una poliza con financiacion SAECA seleccionadas
			if (numFinanciadas > 1){
				tieneFinanciadas = true;
			}
			// Si tiene mas de una poliza seleccionada y una tiene financiacion SAECA
			if (numFinanciadas == 1){
				if(listPolizas.size() != 1){
					tieneFinanciadas = true;
				}
				else{
					esSaecaUnica = "esSaecaUnica";
				}
			}
			
			//Se escribe en el response el resultado de la validacion
			log ("do_Ajax_check_Multiple_Financiadas", "tieneFinanciadas = " + tieneFinanciadas);
			response.setCharacterEncoding("UTF-8");			
			response.getWriter().write(new Boolean(tieneFinanciadas).toString());
			response.getWriter().write("_" + esSaecaUnica);
		}
		catch(IOException e){
			log ("do_Ajax_check_Multiple_Financiadas", "Ocurrio un error al escribir el resultado de la validacion.");
			logger.error("Ocurrio un error al escribir el resultado de la validacion.", e);			
    	}
		catch(Exception e){
			log ("do_Ajax_check_Multiple_Financiadas", "Ocurrio un error inesperado.");
			logger.error("Ocurrio un error inesperado.", e);			
    	}
	}

	public void doCargarListaCondicionesFraccAjax(HttpServletRequest request, HttpServletResponse response){
		
		logger.debug("init - doCargarListaCondicionesFraccAjax");
		
		List<String> listaCondicionesFraccionamiento = new ArrayList<String>();
		String idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
		String codModulo = StringUtils.nullToString(request.getParameter("codModulo"));
		
		JSONObject resultado = new JSONObject();
		
		try {
			
			Poliza pol = polizaManager.getPoliza(new Long(idPoliza));
			if (!codModulo.equals("")) {
				List<CondicionesFraccionamiento> auxListaCondicionesFracc = webServicesManager.getCondicionesFraccionamiento(pol.getLinea().getLineaseguroid(), codModulo);				
				
				if(auxListaCondicionesFracc!=null){
					Iterator<CondicionesFraccionamiento> it = auxListaCondicionesFracc.iterator();
					while(it.hasNext()){
						listaCondicionesFraccionamiento.add(it.next().getId().getPeriodoFracc().toString());
					}
				}
				
			}
			resultado.put("listaCondicionesFracc", listaCondicionesFraccionamiento);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error al doCargarListaCondicionesFraccAjax: " + be.getMessage());			
		}
		getWriterJSON(response, resultado);
	}
	
	/**
	 * Escribe en el log indicando la clase y el método.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("ValidacionesUtilidadesController." + method + " - " + msg);
	}

	/**
	 * Setter del Manager para Spring
	 * @param validacionesUtilidadesManager
	 */
	public void setValidacionesUtilidadesManager(IValidacionesUtilidadesManager validacionesUtilidadesManager) {
		this.validacionesUtilidadesManager = validacionesUtilidadesManager;
	}

	/**
	 * Setter del Manager para Spring
	 * @param consultaSbpManager
	 */
	public void setConsultaSbpManager(IConsultaSbpManager consultaSbpManager) {
		ConsultaSbpManager = consultaSbpManager;
	}

	/**
	 * Setter del Manager para Spring
	 * @param polizaManager
	 */
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
}