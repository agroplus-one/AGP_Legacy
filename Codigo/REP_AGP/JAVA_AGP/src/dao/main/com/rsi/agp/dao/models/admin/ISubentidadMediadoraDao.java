package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.TipoMediador;
import com.rsi.agp.dao.tables.admin.TipoMediadorAgro;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;

@SuppressWarnings("rawtypes")
public interface ISubentidadMediadoraDao extends GenericDao {

	public List<SubentidadMediadora> listSubentidadesGrupoEntidad(SubentidadMediadora subentidadMediadora)
			throws DAOException;

	public Integer existeRegistro(SubentidadMediadora subentidadMediadoraBean, boolean addFiltroFechaBaja,
			BigDecimal codEntidad) throws DAOException;

	public Integer existeRegistro(BigDecimal CodentidadEM, BigDecimal CodsubentidadEM, boolean addFiltroFechaBaja,
			BigDecimal codEntidad) throws DAOException;

	public List<TipoMediador> getListTiposMediador() throws DAOException;

	public List<InformeMediadores> listInformeMediadoresBySubent(SubentidadMediadora subentidadMediadoraBean)
			throws DAOException;

	public List<SubentidadMediadora> getAll() throws DAOException;

	public List<EntidadMediadora> getAllEntMediadoras() throws DAOException;

	boolean esSubEntValidaParaEntMed(BigDecimal codentidad, BigDecimal codsubentidad) throws DAOException;

	public TipoMediador getTipoMediadorRGA(BigDecimal codEnt, BigDecimal codSubEntMed);

	public TipoMediadorAgro getTipoMediadorAgro(BigDecimal codEnt, BigDecimal codSubEntMed);

	public boolean isSubentidadMedBaja(BigDecimal codentidad, BigDecimal codsubentidad) throws DAOException;
	
	public String[] getColectivosUltPlanes(final BigDecimal codentidad, final BigDecimal codsubentidad) throws DAOException;
}
