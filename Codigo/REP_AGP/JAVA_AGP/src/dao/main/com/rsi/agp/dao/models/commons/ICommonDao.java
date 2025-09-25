package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("rawtypes")
public interface ICommonDao extends GenericDao {

	public List getPlanes() throws BusinessException;
	public List getLineas(BigDecimal codPlan) throws BusinessException;
	public List getLineaseguroid(BigDecimal codPlan, BigDecimal codLinea);
	public Linea getPlanLinea(Long lineaSeguroId) throws BusinessException; 
	public List getTiposImportacion () throws BusinessException;
	public List getLineasNoPlan () throws BusinessException;
	public int getNumInstalaciones(Long idParcela) throws DAOException;
	public int getMaxIdZonificacionSIGPAC() throws DAOException;
}