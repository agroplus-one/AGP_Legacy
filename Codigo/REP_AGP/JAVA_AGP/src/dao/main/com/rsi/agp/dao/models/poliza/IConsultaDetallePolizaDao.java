package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("rawtypes")
public interface IConsultaDetallePolizaDao extends  GenericDao {
	
	public EnvioAgroseguro getXMLCalculoImportes(Poliza polizaBean) throws DAOException;
}