package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoComisionesRenovFilter;
import com.rsi.agp.core.jmesa.sort.MtoComisionesRenovSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.ComisionesRenov;
import com.rsi.agp.dao.tables.poliza.Linea;

/**
 * @author   U028975 (Tatiana, T-Systems)
 * Petición: 57624 (Mantenimiento de Comisioens en Renovables por E-S Mediadora)
 * Fecha:    (Enero/Febrero.2019)
 **/

@SuppressWarnings("rawtypes")
public interface IMtoComisionesRenovDao extends GenericDao {
	                            
	public List<ComisionesRenov> getComisRenovParaCalculo(final ComisionesRenov predicate) throws DAOException;
	
	Collection<ComisionesRenov> getComisRenovWithFilterAndSort(final MtoComisionesRenovFilter filter,
			final MtoComisionesRenovSort sort, final int rowStart, final int rowEnd) throws BusinessException;

	int getComisionesRenovCountWithFilter(final MtoComisionesRenovFilter filter);
	
	List<ComisionesRenov> listTodasComisionesRenov(ComisionesRenov comisionesRenov) throws DAOException;
	
    boolean existeComisionesRenov(ComisionesRenov comisionesRenovBean, boolean valEntidad)throws DAOException, Exception;
    
    public boolean validarRangoImporte (ComisionesRenov comisionesRenovBean, boolean valEntidad)throws DAOException, Exception;
    
	public BigDecimal getMaxIdComisRenov();
	
	public Linea getLinea(BigDecimal codLinea, BigDecimal codPlan);
	
	public Integer validarEntidad(BigDecimal entidadMed, BigDecimal subentidadMed ,BigDecimal codEntidad) throws DAOException ;
	
	public String getDescLinea(BigDecimal codLinea, BigDecimal codPlan);
	
	public void replicarComisRenov (BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest, String codUsuario) throws DAOException;
	
	public String getNombEntidad(BigDecimal codEntidad);
	
}
