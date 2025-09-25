/****************************************************************************
 *
 * CREACION
 * --------
 *
 * REFERENCIA: P0000014796
 * FECHA: 03/02/2010
 * AUTOR: Oscar Navarro
 * DESCRIPCION: Controlador base 
 *
 ****************************************************************************/
package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.rsi.agp.core.util.SecurityLayer;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
/**
 * Implementation of <strong>SimpleFormController</strong> that contains convenience methods for subclasses. For
 * example, getting the current user and saving messages/errors. This class is intended to be a base class for all Form
 * controllers.
 */

public class BaseSimpleController extends AbstractCommandController {
	
	private static final Log LOGGER = LogFactory.getLog(BaseSimpleController.class); 
	
	@Override
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
	    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	protected ModelAndView handle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, BindException arg3) throws Exception {
		return null;
	}

	//metodo por el que pasan todos los controller antes de ejecutarse
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,HttpServletResponse response) throws Exception {

		SecurityLayer._assert(request);
		
		return super.handleRequestInternal(request, response);

	}
	

	protected boolean checkLogged(HttpServletRequest request) {
		if(request.getSession().getAttribute("usuario")!=null){ 
			return true;
		}else{
		    return false;
		}
	}
	
	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONArray listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			LOGGER.warn("Fallo al escribir la lista en el contexto", e);
		}
	}
	
	/**
	 * Metodo que va a ser invocado desde cada controller para estribir una lista JSON en su response
	 * @param response --> objeto response en el que se va a escribir la lista
	 * @param listaJSON --> la lista JSON que tiene que ser escrita
	 */
	protected void getWriterJSON(HttpServletResponse response, JSONObject listaJSON){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(listaJSON.toString());
		} catch (IOException e) {			
			LOGGER.warn("Fallo al escribir la lista en el contexto", e);
		}
	}
}
