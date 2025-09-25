package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("rawtypes")
public interface ICambioModuloDao extends GenericDao {

	public void cambiarModulo(final Poliza poliza, final es.agroseguro.contratacion.Poliza sitAct) throws DAOException;
}