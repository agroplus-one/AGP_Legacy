package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.poliza.Poliza;


@SuppressWarnings("rawtypes")
public interface ICaracteristicaExplotacionDao extends GenericDao {
	
	public boolean aplicaCaractExplotacion(final Long lineaseguroid)throws DAOException; 
	public void deleteCaractExplotacion(final Long idpoliza)throws DAOException;
	public boolean aplicaYBorraCaractExplocion(Poliza poliza) throws DAOException;
	public boolean validarCaractExplotacion(Poliza poliza) throws DAOException;
	
	public CaracteristicaExplotacion getCaracteristicaExplotacion(int codCaracteristicaExplotacion) throws DAOException;
}
