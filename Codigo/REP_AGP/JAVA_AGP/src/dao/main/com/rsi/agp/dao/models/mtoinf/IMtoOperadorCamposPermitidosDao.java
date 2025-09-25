package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.Operador;

@SuppressWarnings("rawtypes")
public interface IMtoOperadorCamposPermitidosDao extends GenericDao {

	public String checkCampo_OperadorExists(String idVistaCampo, BigDecimal idOperador, String idOpCampoPermitido)
			throws DAOException;

	public List<Operador> getListaOperadores(Long idCampoPermitido) throws DAOException;

	public boolean existeCondicionCamPerm(String idOpCamPer) throws DAOException;
}