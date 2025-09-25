package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.ICambioClaseMasivoManager;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.models.poliza.ICambioClaseMasivoDao;
import com.rsi.agp.dao.tables.admin.Clase;

/**
 * @author U029769
 */
public class CambioClaseMasivoManager implements ICambioClaseMasivoManager {
	
	private static final Log LOGGER = LogFactory.getLog(CambioMasivoPolizasManager.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private ICambioClaseMasivoDao cambioClaseMasivoDao;
	private IClaseDao claseDao;
	
	/**
	 * Cambia la clase a las polizas
	 * @author U029769 12/06/2013
	 * @param clase
	 * @param listaIds
	 * @return Map<String, String>
	 * @throws DAOException 
	 */
	@Override
	public Map<String, String> cambiaClase(String clase, String listaIds) throws DAOException {
		
		List<Long> idsPolizas = new ArrayList<Long>();
		int countKO =0;
		Map<String, String> params = new HashMap<String, String>();
		try {
			String[] idPol = listaIds.split(",");
			List<Long> listaAuxCadenas = new ArrayList<Long>();
			boolean validacion = false;
			boolean validacionAux = false;
			for (int i = 0 ; i < idPol.length ; i++) {
				Long idLineaSeguro = cambioClaseMasivoDao.getLineaSeguroIdFromPoliza(Long.valueOf(idPol[i]));
				//validamos que la clase exista para la linea de la poliza				
				if(listaAuxCadenas.size() > 0) {
					// Entramos por aquí la segunda vez en adelante, comprobando el contenido de la variable: "listaAuxCadenas" 
					// para evitar validar casuísticas repetidas
					if(!listaAuxCadenas.contains(idLineaSeguro)) {
						validacion = validaLineaClase(idLineaSeguro,clase);
						validacionAux = true;
					}
				}
				else {
					// La primera vez entramos por aquí, ya que la variable: "listaAuxCadenas" todavía no tiene elementos
					validacion = validaLineaClase(idLineaSeguro,clase);
					validacionAux = true;
				}
					if(validacionAux) {
						if(validacion) {
							listaAuxCadenas.add(idLineaSeguro);
							idsPolizas.add(Long.valueOf(idPol[i]));
						}else {
							listaAuxCadenas.add(idLineaSeguro);
							countKO++;
						}
						validacionAux = false;
					}
					else {
						if(validacion) {
							idsPolizas.add(Long.valueOf(idPol[i]));
						}
						else {
							countKO++;
						}
					}
			}			
			if (idsPolizas.size()>0){
				cambioClaseMasivoDao.actualizaClasePoliza (idsPolizas,new BigDecimal(clase));
				params.put("mensaje", bundle.getString("mensaje.clase.masivo.OK"));
			}
			if (countKO > 0){
				params.put("alerta", bundle.getString("mensaje.clase.masivo.info"));
			}
			
			
			return params;
		} catch (DAOException e) {
			LOGGER.info("Se ha producido un error al cambiar la clase de la poliza: " + e.getMessage());
			throw new DAOException("Se ha producido un error al cambiar la clase de la poliza:", e);
		}	
	}
	
	/**
	 * valida si existe una clase dada una lineaSeguroId
	 * @author U029769 12/06/2013
	 * @param lineaseguroid
	 * @param clase
	 * @throws BusinessException 
	 */
	private boolean validaLineaClase(Long lineaseguroid, String clase){
	
		Clase c = claseDao.getClase(lineaseguroid, new BigDecimal(clase));
		if (c != null){
			return true;
		}
		return false;
	}

	/**
	 * @author U029769 12/06/2013
	 * @param cambioClaseMasivoDao
	 */
	public void setCambioClaseMasivoDao(ICambioClaseMasivoDao cambioClaseMasivoDao) {
		this.cambioClaseMasivoDao = cambioClaseMasivoDao;
	}
	/**
	 * @author U029769 12/06/2013
	 * @param claseDao
	 */
	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}
}
