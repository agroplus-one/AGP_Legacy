package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.GGESubentidades;

@SuppressWarnings("rawtypes")
public interface IGGESubEntidadesDao extends GenericDao {
	public List<GGESubentidades> getListGGESubentidades(GGESubentidades GGESubentidadesBean) throws DAOException;
	public Integer existeRegistro(GGESubentidades geeSubentidadesBean) throws DAOException;
	public SubentidadMediadora getSubentidadMediadora(SubentidadMediadora subentidadMediadora) throws DAOException;
	public boolean existePlan(String planDestino) throws DAOException;
	public BigDecimal getPctSectorAgricola(Long planDestino)throws DAOException;
}
