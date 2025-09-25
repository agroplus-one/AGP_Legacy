package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

@SuppressWarnings("rawtypes")
public interface IMtoOperadorCamposCalculadosDao extends GenericDao {

	public List<OperadorCamposCalculados> getListaOperadores(BigDecimal idCampoCalculado) throws DAOException;

	public String checkCampo_OperadorExists(String idCampoCalc, BigDecimal idOperador, String idOpCalculado)
			throws DAOException;

	public boolean existeCondicionCamCalc(String idOpCalculado) throws DAOException;

	public boolean existeCamCalcEnInforme(String idCampoCalc) throws DAOException;
}