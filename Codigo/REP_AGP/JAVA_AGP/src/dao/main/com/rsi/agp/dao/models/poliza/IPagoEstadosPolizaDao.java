package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;

public interface IPagoEstadosPolizaDao extends GenericDao{
	/**
	 * Obtiene todos los estados de pago de poliza
	 * 05/08/2013 U029769
	 * @return
	 * @throws DAOException
	 */
	List<EstadosPoliza> getEstadosPagoPoliza() throws DAOException;

}
