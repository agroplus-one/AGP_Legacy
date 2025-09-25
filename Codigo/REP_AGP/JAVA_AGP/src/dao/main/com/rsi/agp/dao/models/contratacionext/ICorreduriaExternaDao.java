package com.rsi.agp.dao.models.contratacionext;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.CorreduriaExterna;

@SuppressWarnings("rawtypes")
public interface ICorreduriaExternaDao extends GenericDao {

	public CorreduriaExterna getCorreduria(final String codInterno) throws DAOException;

	public void guardarCupon(final BigDecimal plan, final String referencia, final String idCupon) throws DAOException;

	public void anularCupon(final String idCupon) throws DAOException;

	public String getCorreduriaPoliza(final BigDecimal plan, final String referencia) throws DAOException;
	
	public String getCorreduriaCupon(final String idCupon) throws DAOException;
	
	public List<Colectivo> getColectivo(final String referencia, final int dc) throws DAOException;
}