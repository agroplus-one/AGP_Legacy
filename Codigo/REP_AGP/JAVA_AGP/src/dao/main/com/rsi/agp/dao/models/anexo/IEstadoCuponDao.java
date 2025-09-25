package com.rsi.agp.dao.models.anexo;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;

@SuppressWarnings("rawtypes")
public interface IEstadoCuponDao extends GenericDao {
	
	List<EstadoCupon> getListaEstadoCupon ()throws DAOException;
	EstadoCupon getEstadoCupon(Long idEstado) throws DAOException;
	
}
