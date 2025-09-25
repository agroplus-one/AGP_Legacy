package com.rsi.agp.dao.models.commons;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.SigpacVO;

@SuppressWarnings("rawtypes")
public interface ISigpacDao extends GenericDao {

	public LocalCultVarVO getLocalCultVar(SigpacVO sigpacVO, Long claseId) throws DAOException;

	public LocalCultVarVO getLocalFromTerminosSigpacAgro(SigpacVO sigpacVO, Long claseId);
}