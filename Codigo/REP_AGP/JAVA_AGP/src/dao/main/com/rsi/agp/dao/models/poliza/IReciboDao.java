package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.recibos.Recibo;

public interface IReciboDao extends GenericDao {
	
	public Recibo getRecibo(Integer codRecibo, String codPlan, Integer codLinea, String refColectivo) throws DAOException;
	public boolean existeReciboPoliza(Integer codrecibo, Character tipoRefPoliza, String refPoliza) throws DAOException;
}
