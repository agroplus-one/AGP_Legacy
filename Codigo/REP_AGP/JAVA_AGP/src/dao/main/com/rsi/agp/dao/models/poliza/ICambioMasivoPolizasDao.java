package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface ICambioMasivoPolizasDao extends GenericDao {
	
	/** DAA 29/05/2013
	 *  Metodo para obtener cuantas polizas de una lista tienen estado distinto de definitivo
	 * @param listaIds
	 * @return BigDecimal numPolizas
	 * @throws DAOException 
	 */
	BigDecimal getNumPolizasNoDefitivas(List<String> listaIds) throws DAOException;
	
	
	void pagoMasivo(String fechapago, String listaIds,
			String marcar_desmarcar, String codUsuario) throws DAOException;
	

}
