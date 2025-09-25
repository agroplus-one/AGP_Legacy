package com.rsi.agp.dao.models.log;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IHistImportacionesDao extends GenericDao {
	
	public int compruebaRegistrosPL(int lineaSeguroId, Boolean forzarActivar) throws BusinessException;

	public boolean mostrarCoberturas(final Long lineaseguroId) throws DAOException;
}
