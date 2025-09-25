package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.rc.RegimenRC;

@SuppressWarnings("rawtypes")
public interface IPolizasRCDao extends GenericDao {
	PolizasRC obtenerPolizaRC(Long polizaId) throws DAOException;
	
	List<EspeciesRC> obtenerEspecieRC(Long lineaSeguroId, BigDecimal codEspecie, 
			BigDecimal regimen, BigDecimal codTipoCapital) throws DAOException;
	
	List<RegimenRC> obtenerRegimenRC(Long lineaSeguroId, BigDecimal codsubentidad, 
			BigDecimal codEntidad, String codEspecieRC) throws DAOException;
	
	Boolean grupoRazaTieneRegimen(Long lineaSeguroId, Long codEspecie, Long codRegimen, BigDecimal codTipoCapital) throws DAOException;
}
