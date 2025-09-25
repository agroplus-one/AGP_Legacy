package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;

public interface ICondicionesFraccionamientoDao extends IGenericoDao{
	public List<CondicionesFraccionamiento> listCondicionesFraccionamiento(CondicionesFraccionamiento condicionesFraccionamiento) throws DAOException;
	public List<CondicionesFraccionamiento> getAll()throws DAOException ;
}
