/**
 * ----------------------------------------------------------------------------
 * Autor:       Miguel Granadino 
 * Fecha:       08/02/2012
 * Version:     null
 * Descripcion: null
 * Proyecto:    Agroplus
 * 
 * ----------------------------------------------------------------------------
 */
package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.models.poliza.IIncompatibilidadClaseDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

/**
 * MANAGER
 * Clase para el control de incimpatibilidad por clase en polizas
 * tb_incompatibilidad_clases
 */
public class IncompatibilidadClaseManager implements IManager {
	
	/* CONSTANTS
	 ------------------------------------------------------------------------ */
	private static final Log logger = LogFactory.getLog(PolizaManager.class);
	
	/* VARIABLES
	 ------------------------------------------------------------------------ */
	private IIncompatibilidadClaseDao incompatibilidadClaseDao;
	
	/* METODOS PUBLICOS
	 ------------------------------------------------------------------------ */
	/**
	 * Comprueba si una lista de polizas son compatibles con ellas mismas
	 * y las que hay en la BBDD a nivel de clase consultando tb_incompatibilidad_clases
	 * @param recibe como parametro una lista Polizas y devuelve el resultado de
	 * la validacion
	 */
	public String isCompatible(List<Poliza> listPolizas) throws BusinessException
	{
		StringBuilder table = new StringBuilder();
		
		try {
			boolean result = incompatibilidadClaseDao.isCompatible(listPolizas);
			
			if(result){
				table = null;
			}else{
				table.append("<div style='color:black;border:1px solid #DD3C10;font-size:12px;text-align:center;");
		    	table.append("font-size:12px;line-height:20px;background-color:#FFEBE8;'>");
		    	table.append("Imposible pasar a definitiva. Incompatibilidad con la clase/modulo/linea de otra poliza");
		    	table.append("</div>");
			}
		}catch(Exception ex){
			logger.error(ex);
			throw new BusinessException("[IncompatibilidadClaseManager] isCompatible() - error ", ex);
		}
		
		return  (table != null) ? table.toString() : "";
		
	}
	
	/**
	 * Comprueba si una poliza es compatible,
	 * consultando tb_incompatibilidad_clases
	 * Devuelve
	 */
	public boolean isCompatible(Poliza poliza) throws BusinessException
	{
		boolean isCompatible = false;
		try {
			isCompatible = incompatibilidadClaseDao.isCompatible(poliza);
		}catch(Exception ex){
			logger.error(ex);
			throw new BusinessException("[IncompatibilidadClaseManager] isCompatible() - error ", ex);
		}		
		return isCompatible;	
	}

	/* SETTERS MANAGERS FOR SPRING IOC
	 ------------------------------------------------------------------------ */
	public void setIncompatibilidadClaseDao(IIncompatibilidadClaseDao incompatibilidadClaseDao){
		this.incompatibilidadClaseDao = incompatibilidadClaseDao;
	}
}
