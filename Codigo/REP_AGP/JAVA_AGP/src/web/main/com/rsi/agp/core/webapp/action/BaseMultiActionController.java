package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.SecurityLayer;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.commons.Usuario;

public class BaseMultiActionController extends MultiActionController {
	
	private static final Log LOGGER = LogFactory.getLog(BaseMultiActionController.class); 

	protected ModelAndView handleRequestInternal(HttpServletRequest request,HttpServletResponse response) throws Exception {

		SecurityLayer._assert(request);
		
		return super.handleRequestInternal(request, response);

	}
	
	/**
	 * @return Obtiene la ruta de los .wsdl para las llamadas a los SW
	 */
	protected String getRealPath () {
		try {
			return this.getServletContext().getRealPath("/WEB-INF/");
		} 
		catch (Exception e) {
			logger.error("Ha ocurrido un error al obtener la ruta de los .wsdl", e);
			return "";
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
	
	protected void getWriterJSON(HttpServletResponse response, String resultado){		
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(resultado);
		} catch (IOException e) {			
			logger.warn("Fallo al escribir la lista en el contexto", e);
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
	
	/**
	 * Método que añade una clave y valor a un objeto JSONObject controlando sus excepciones
	 * @param json
	 * @param key
	 * @param value
	 */
	protected void putJSON (JSONObject json, String key, String value) {
		
		try {
			json.put(key, value);
		} catch (Exception e) {
			logger.error("Ocurrió un error al insertar la clave y valor en el objeto json", e);
		}
		
	}
	
	protected Map<String, Object> getParametrosEntidadUsuarioPorPerfil(Usuario usuario){
		Map<String, Object> parameters = new HashMap<String, Object>();
		String lstCodEntidades = "";
		String entMed = "";
		String subEntMed = "";
		String nomEntidad = "";
		String perfil = usuario.getPerfil();
		
		if ((Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES).equals(perfil)){
			lstCodEntidades = usuario.getOficina().getId().getCodentidad().toString();
			entMed = usuario.getSubentidadMediadora().getId().getCodentidad().toString();
			subEntMed = usuario.getSubentidadMediadora().getId().getCodsubentidad().toString();
			nomEntidad = usuario.getSubentidadMediadora().getEntidad().getNomentidad();
		}else{
			if((Constants.PERFIL_USUARIO_SEMIADMINISTRADOR).equals(perfil)){
				lstCodEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false, false);
			}
		}
		
		parameters.put("grupoEntidades", lstCodEntidades);
		parameters.put("entMed", entMed);
		parameters.put("subEntMed", subEntMed);
		parameters.put("externo", usuario.getExterno());
		parameters.put("perfil", perfil.substring(4));
		parameters.put("nomEntidad", nomEntidad);
		
		return parameters;
	}
	
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. 
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacÃ­as
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
    
}
