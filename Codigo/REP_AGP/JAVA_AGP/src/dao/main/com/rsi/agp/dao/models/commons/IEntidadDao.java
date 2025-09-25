package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Oficina;

@SuppressWarnings("rawtypes")
public interface IEntidadDao extends GenericDao{
	
	public List<BigDecimal> getListaEntidadesGrupo(String idGrupo) throws DAOException;
	public boolean isCRM(BigDecimal codentidad);
	public boolean isCRAlmendralejo(BigDecimal codentidad);
	public String getEntidadesGrupoCRM() throws DAOException;
	String getIdGrupoEntidad(BigDecimal codEntidadUsuario) throws DAOException;
	boolean existeEntidad (BigDecimal codentidad)throws DAOException;
	boolean existeEntidadOficina(BigDecimal codentidad, BigDecimal codoficina)throws DAOException;
	Oficina getOficina(BigDecimal codentidad, BigDecimal codoficina) throws DAOException;
	public List<Entidad> obtenerListaEntidadesByArrayCodEntidad(List<BigDecimal> listaCodEntidad) throws DAOException;
	public String getIdGrupoOficina(BigDecimal codEntidad,BigDecimal codOficina) throws DAOException;
	public List<BigDecimal> getListaOficinasGrupo(BigDecimal codZona, BigDecimal codEntidad) throws DAOException;

}
