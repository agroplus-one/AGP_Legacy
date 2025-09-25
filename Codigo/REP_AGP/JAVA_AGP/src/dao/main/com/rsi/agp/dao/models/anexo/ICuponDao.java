package com.rsi.agp.dao.models.anexo;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.HistoricoCupon;

@SuppressWarnings("rawtypes")
public interface ICuponDao extends GenericDao {
	
	public Cupon saveCupon (final Cupon cupon) throws DAOException;
	public HistoricoCupon saveHistoricoCupon (final HistoricoCupon historico) throws DAOException;
	
	/**
	 * Elimina todos los registros de cupón cuyo idCupon coincida con el parámetro
	 * @param idCupon
	 * @throws DAOException
	 */
	public void borrarCupon (final String idCupon) throws DAOException;
	
	public boolean esPolizaGanado(final String idCupon) throws DAOException;
}
