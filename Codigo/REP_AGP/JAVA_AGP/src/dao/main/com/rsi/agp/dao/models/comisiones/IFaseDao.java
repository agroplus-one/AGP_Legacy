package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fase;

@SuppressWarnings("rawtypes")
public interface IFaseDao extends GenericDao{

	public Fase isExistFase(String fase, BigDecimal plan) throws DAOException;
	public void saveFaseFichero(Fase fase) throws DAOException;
	public int borrarReferenciasCierre(Cierre cierre) throws DAOException;
	public BigDecimal obtenerPlanByIdFichero(Long idFichero) throws DAOException;
}
