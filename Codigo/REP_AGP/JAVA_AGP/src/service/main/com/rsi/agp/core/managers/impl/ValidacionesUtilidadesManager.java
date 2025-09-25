package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IValidacionesUtilidadesManager;
import com.rsi.agp.dao.models.poliza.IValidacionesUtilidadesDao;

public class ValidacionesUtilidadesManager implements IValidacionesUtilidadesManager{
	
	// Caracter por el que se separan los ids de poliza
	private final String CHAR_SEPARADOR_IDS = ";";
	
	private Log logger = LogFactory.getLog(ValidacionesUtilidadesManager.class);
	private IValidacionesUtilidadesDao validacionesUtilidadesDao;

	@Override
	public boolean validarPolizasBorradoMasivo(String ids) {
		
		// Se trata la cadena 'ids' para convertirla en un listado de ids
		List<String> lista = new ArrayList<String>();
		try {
			// Obtiene el listado de ids de poliza a partir de la cadena separa por ';'
			lista = limpiarVacios (Arrays.asList (ids.split(CHAR_SEPARADOR_IDS)));	
		}
		catch (Exception e) {
			log ("validarPolizasBorradoMasivo", "Ocurrio un error al obtener el listado de ids de poliza");
			logger.error("Ocurrio un error al obtener el listado de ids de poliza", e);
		}
		
		// Si la lista se ha rellenado correctamente seguimos con la validacion
		if (lista.size() > 0) {
			try {
				//Comprueba si alguna pÛliza seleccionada se encuientra en estados diferentes a
				//pendiente de validaciÛn o grabaciÛn provisional
				return ((validacionesUtilidadesDao.getCountPlzBorradoMasivo (lista) > 0) ? false : true);
			} catch (DAOException e) {
				log ("validarPolizasBorradoMasivo", "Ocurrio algun error al obtener el numero de polizas que no pueden ser borradas");
				logger.error("Ocurrio algun error al obtener el numero de polizas que no pueden ser borradas", e);
			}
		}
		
		// Si llega hasta aqui ha ocurrido algun error, no se permite el borrado
		return false;
	}
	
	@Override
	public String validarPolizasCambioOficinaMultiple(String ids, boolean perfil0) {
		// Se trata la cadena 'ids' para convertirla en un listado de ids
		List<String> lista = new ArrayList<String>();
		try {
			// Obtiene el listado de ids de poliza a partir de la cadena separa por ';'
			lista = limpiarVacios (Arrays.asList (ids.split(CHAR_SEPARADOR_IDS)));	
		}
		catch (Exception e) {
			log ("validarPolizasCambioOficinaMultiple", "Ocurrio un error al obtener el listado de ids de poliza");
			logger.error("Ocurrio un error al obtener el listado de ids de poliza", e);
		}
		
		// Si la lista se ha rellenado correctamente seguimos con la validacion
		if (lista.size() > 0) {
			
			try {
				if (!perfil0 && validacionesUtilidadesDao.hayPolizasAnuladas(lista)) {
					return "false2";
				} else {
					// Llama al dao para obtener la entidad de las polizas pasadas como parametro para hacer el cambio
					// Si hay mas de una, el cambio masivo no se puede realizar
					return validacionesUtilidadesDao.getEntidadCambioOficinaMasivo(lista);
				}
			} catch (DAOException e) {
				log ("validarPolizasCambioOficinaMultiple", "Ocurrio algun error al validar las polizas");
				logger.error("Ocurrio algun error al validar las polizas", e);
			}
		}
		
		// Si llega hasta aqui ha ocurrido algun error, no se permite el borrado
		return "false";
	}

	/**
	 * Elimina los elementos vacios que contenga la lista
	 * @param listaIni
	 */
	public List<String> limpiarVacios (List<String> listaIni) {
		
		List<String> listaFin = new ArrayList<String>();
		
		// Recorre los elementos de la lista y copia en la nueva lista los que no sean vacios
		for (String string : listaIni) {
			if (!"".equals(string)) listaFin.add(string);
		}	
	
		return listaFin;
	}
	
	/**
	 * Escribe en el log indicando la clase y el m√©todo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("ValidacionesUtilidadesManager." + method + " - " + msg);
	}

	
	/**
	 * Setter del DAO para Spring
	 * @param validacionesUtilidadesDao
	 */
	public void setValidacionesUtilidadesDao(IValidacionesUtilidadesDao validacionesUtilidadesDao) {
		this.validacionesUtilidadesDao = validacionesUtilidadesDao;
	}

}
