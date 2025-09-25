package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.HistoricoCuponRC;

@SuppressWarnings("rawtypes")
public interface ICuponDao extends GenericDao {
	
	public CuponRC saveCupon (final CuponRC cupon) throws DAOException;
	public HistoricoCuponRC saveHistoricoCupon (final HistoricoCuponRC historico) throws DAOException;
	
	/**
	 * Elimina todos los registros de cupón cuyo idCupon coincida con el parámetro
	 * @param idCupon
	 * @throws DAOException
	 */
	public void borrarCupon (final String idCupon) throws DAOException;
	
	public CuponRC obtenerCupon (final String idCupon) throws DAOException;
}
