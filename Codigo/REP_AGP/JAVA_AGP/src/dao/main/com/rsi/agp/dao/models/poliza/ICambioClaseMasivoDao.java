/**
 * 
 */
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

/**
 * @author U029769
 */
@SuppressWarnings("rawtypes")
public interface ICambioClaseMasivoDao extends GenericDao {

	BigDecimal getNumPolizasNoEnvCorr(List<String> listaIds) throws DAOException;

	void actualizaClasePoliza(List<Long> idsPolizas, BigDecimal idclase) throws DAOException;
	
	Long getLineaSeguroIdFromPoliza(Long idLineaSeguro) throws DAOException;
}
