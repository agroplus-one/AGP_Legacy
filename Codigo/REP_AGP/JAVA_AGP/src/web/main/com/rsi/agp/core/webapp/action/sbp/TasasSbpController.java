package com.rsi.agp.core.webapp.action.sbp;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.ITasasSbpService;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.action.BaseMultiActionController;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

public class TasasSbpController extends BaseMultiActionController{
	
	private Log logger = LogFactory.getLog(TasasSbpController.class);	
	private ITasasSbpService tasasSbpService;		
	private String successView;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	
	/**
	 * Realiza la consulta de tasas que se ajustan al filtro de busqueda
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula el filtro de busqueda 
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{				
				
		log("doConsulta", "init");
		final Map<String, Object> parameters = new HashMap<String, Object>();
		// Variables obtenidas del formulario
		String origenLlamada = request.getParameter("origenLlamada");
		
    	// ---------------------------------------------------------------------------------
    	// -- Busqueda de tasas de sobreprecio y generacion de la tabla de presentacion --
        // ---------------------------------------------------------------------------------    
		if (!StringUtils.nullToString(origenLlamada).equals("menuGeneral")) {
	    	log("doConsulta", "Comienza la busqueda de tasas de sobreprecio");
	    	String html = tasasSbpService.getTablaTasasSbp(request, response, tasaSbpBean, origenLlamada);
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
	    			request.setAttribute("tasasSbp", html);
	             }
	        }
		}
		// Anhade el parametro que indica si la llamada se ha hecho desde el menu lateral
    	parameters.put("origenLlamada", request.getParameter("origenLlamada"));
		
		return new ModelAndView(successView, "tasaSbpBean", tasaSbpBean).addAllObjects(parameters);       	       	       	       
	}
	
	/**
	 * Limpia el filtro de busqueda y realiza la consulta de todos los registros de tasas
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula el filtro de busqueda
	 * @return ModelAndView que realiza la redireccion 
	 * @throws Exception
	 */
	public ModelAndView doLimpiar(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{				
		Map<String, Object> parameters = new HashMap<String, Object>();
		return doConsulta(request, response, tasaSbpBean).addAllObjects(parameters);       	       	       	       
	}
	
	/**
	 * Da de alta un registro de tasa
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula la informacion de la tasa a dar de alta
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{				
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, String> errores = new HashMap<String, String>();
		
		// Llama al manager para hacer el alta de la tasa y carga la alerta correspondiente
		errores = tasasSbpService.altaTasaSbp(tasaSbpBean);
			
		if (errores.size()==0) parametros.put("mensaje", bundle.getObject("mensaje.altaTasa.ok"));
		else {
			// Pasar los errores a la jsp
			// Si el plan/linea indicado no existe
			if (errores.containsKey(ConstantsSbp.ERROR_LINEASEGUROID_NO_EXISTE)) 
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.lineaseguroid.noexiste"));
			// Si la provincia indicada no existe
			else if (errores.containsKey(ConstantsSbp.ERROR_PROVINCIA_NO_EXISTE))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.provincia.noexiste"));
			// Si ya existe una tasa para el plan/linea y provincia indicados
			else if (errores.containsKey(ConstantsSbp.ERROR_TASA_YA_EXISTE))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.tasa.existe"));
			//Si el codCultivo no es valido
			else if (errores.containsKey(ConstantsSbp.ERROR_SOBREPRECIO_CULTIVO_KO))
				parametros.put("alerta", bundle.getObject("alerta.Sobreprecio.ko.cultivo.noExiste"));
			// Si ha ocurrido un error generico
			else if (errores.containsKey(ConstantsSbp.ERROR_GENERAL))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko"));
				
		}
		parametros.put("showModificar", "true");
		
		return doConsulta(request, response, tasaSbpBean).addAllObjects(parametros);       	       	       	       
	}
	
	/**
	 * Edita un registro de tasa
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula la informacion de la tasa a editar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doEditar(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{				
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		Map<String, String> errores = new HashMap<String, String>();
		
		// Llama al manager para hacer el alta de la tasa y carga la alerta correspondiente
		errores = tasasSbpService.updateTasaSbp(tasaSbpBean);
			
		if (errores.size()==0) parametros.put("mensaje", bundle.getObject("mensaje.updateTasa.ok"));
		else {
			// Pasar los errores a la jsp
			// Si el plan/linea indicado no existe
			if (errores.containsKey(ConstantsSbp.ERROR_LINEASEGUROID_NO_EXISTE)) 
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.lineaseguroid.noexiste"));
			// Si la provincia indicada no existe
			else if (errores.containsKey(ConstantsSbp.ERROR_PROVINCIA_NO_EXISTE))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.provincia.noexiste"));
			// Si ya existe una tasa para el plan/linea y provincia indicados
			else if (errores.containsKey(ConstantsSbp.ERROR_TASA_YA_EXISTE))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko.tasa.existe"));
			// Si ha ocurrido un error generico
			else if (errores.containsKey(ConstantsSbp.ERROR_GENERAL))
				parametros.put("alerta", bundle.getObject("alerta.Tasa.ko"));
				
		}
		parametros.put("showModificar", "true");
		return doConsulta(request, response, tasaSbpBean).addAllObjects(parametros);       	       	       	       
	}
	
	/**
	 * Borra un registro de tasa
	 * @param request
	 * @param response
	 * @param tasaSbpBean Objeto que encapsula la informacion de la tasa a borrar
	 * @return ModelAndView que realiza la redireccion
	 * @throws Exception
	 */
	public ModelAndView doBorrar(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Se comprueba si el id de la tasa de sbp está cargado en el bean
		if (tasaSbpBean != null && tasaSbpBean.getId() != null) {
			// Llama al manager para hacer la baja de la tasa y carga la alerta correspondiente
			if (tasasSbpService.bajaTasaSbp(tasaSbpBean)) parameters.put("mensaje", bundle.getObject("mensaje.borrarTasa.ok"));
			else parameters.put("alerta", bundle.getObject("alerta.borrarTasa.ko"));
		}
		else {
			parameters.put("alerta", bundle.getObject("alerta.borrarTasa.ko"));
		}
		
		// Redirige a la consulta
		return doConsulta(request, response, tasaSbpBean).addAllObjects(parameters);       	       	       	       
	}	
	
	
	/**
	 * Realiza la copia de todos las TasasSbp de un plan/linea a otro
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 * @return
	 * @throws Exception 
	 */
	public ModelAndView doReplicar(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		BigDecimal planDest = null;
		BigDecimal lineaDest = null;
		
		try {
			// Obtiene el plan/linea destino
			BigDecimal planOrig = tasaSbpBean.getLinea().getCodplan();
			BigDecimal lineaOrig = tasaSbpBean.getLinea().getCodlinea();
			planDest = new BigDecimal (request.getParameter("planreplica"));
			lineaDest = new BigDecimal (request.getParameter("lineareplica"));
			
			// Llamada al metodo que realiza la replica
			logger.debug("Replicar las tasas Sbp del plan/linea " + planOrig + "/" + lineaOrig + " al" + planDest + "/" + lineaDest);
			parameters = tasasSbpService.replicar(planOrig, lineaOrig, planDest, lineaDest);
		}
		catch (Exception e) {
			logger.debug("Error inesperado al replicar las tasas de Sbp a otro plan/linea", e);
			parameters.put("alerta", bundle.getString("mensaje.replicarTasa.KO"));
		}
		
		// Redireccion
		// Si ha ocurrido algun error, se vuelve a la pantalla con el filtro de busqueda anterior. Si el proceso ha finalizado correctamente,
		// se vuelve a la pantalla filtrando por el plan/linea destino
		if (!parameters.containsKey("alerta")) {
			tasaSbpBean = new TasasSbp ();
			Linea linea = new Linea ();
			linea.setCodlinea(lineaDest);
			linea.setCodplan(planDest);
			tasaSbpBean.setLinea(linea);
		}
		
		return doConsulta(request, response, tasaSbpBean).addAllObjects(parameters);
	}
	
	
	/** DAA 27/04/2013 Carga las tasas en bbdd a partir de un fichero
	 * @param request
	 * @param response
	 * @param tasaSbpBean
	 * @return
	 * @throws Exception 
	 */
	public ModelAndView doImportar(HttpServletRequest request, HttpServletResponse response, TasasSbp tasaSbpBean) throws Exception{
		
		logger.info("init - doImportar");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView mv = null;

		try{
			logger.debug("Nombre Fichero: " + tasaSbpBean.getFile().getOriginalFilename());
			tasasSbpService.subeFicheroTasas(tasaSbpBean.getFile(), request);
			parameters.put("mensaje", bundle.getString("mensaje.importarTasas.OK"));

		}catch (IOException ioe) {
			logger.error("Error al tratar el archivo entrada/salida - tasasSbpService.subeFicheroTasas ", ioe);
			parameters.put("alerta", bundle.getString("mensaje.importarTasas.fichero.KO"));
		
		}catch (BusinessException be) {
			logger.error("Error al volcar el archivo en la BBDD - tasasSbpDao.volcarTasasSbpFromFichero ", be);
			parameters.put("alerta", bundle.getString("mensaje.importarTasas.bbdd.KO"));
			
		}
		mv = doConsulta(request, response, tasaSbpBean).addAllObjects(parameters);
		return mv;
	}
	
	
	private void log (String method, String msg) {
		logger.debug("TasasSbpController." + method + " - " + msg);
	}
	
	/**
	 * Setter de propiedad
	 * @param tasasSbpService
	 */
	public void setTasasSbpService(ITasasSbpService tasasSbpService) {
		this.tasasSbpService = tasasSbpService;
	}

	/**
	 * Setter de propiedad
	 * @param successView
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
