package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.TipificacionRecibos;
import com.rsi.agp.dao.tables.recibos.ReciboPoliza;

public interface IReciboPolizaDao extends GenericDao {

	public List<ReciboPoliza> list(ReciboPoliza reciboPoliza) throws DAOException;

	List<TipificacionRecibos> getListTipificacionRecibos()throws DAOException;
	
}
