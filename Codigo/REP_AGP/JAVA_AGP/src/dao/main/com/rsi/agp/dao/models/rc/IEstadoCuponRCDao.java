package com.rsi.agp.dao.models.rc;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;

@SuppressWarnings("rawtypes")
public interface IEstadoCuponRCDao extends GenericDao {
	
	//List<EstadoCupon> getListaEstadoCupon ()throws DAOException;
	EstadoCuponRC getEstadoCupon(Long idEstado) throws DAOException;
	
}
