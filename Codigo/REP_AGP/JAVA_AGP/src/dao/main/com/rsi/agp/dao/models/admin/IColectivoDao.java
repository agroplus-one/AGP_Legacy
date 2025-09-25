package com.rsi.agp.dao.models.admin;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.admin.impl.Colectivo2Filtro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;




@SuppressWarnings("rawtypes")
public interface IColectivoDao extends GenericDao {
	public List<Colectivo> getColectivosGrupoEntidad(Colectivo colectivoBean, List<BigDecimal> entidades,
			boolean addFiltroBaja, List<BigDecimal> planesFiltroInicial) throws DAOException;
	public Map<String, Object> getMapaColectivo(Colectivo colectivoBean) throws DAOException;
	public boolean existeOficina(Oficina oficina) throws DAOException;
	public PaginatedListImpl<Colectivo> getPaginatedListColectivosGrupoEntidad(Colectivo colectivoBean, PageProperties pageProperties, Colectivo2Filtro colectivo2Filtro) throws DAOException;
	public List<Colectivo> getAll()throws DAOException;
	public List<Colectivo> getColectivos(Long id, String referencia, String dc);
	public Linea getLineaColectivo(BigDecimal codLinea, BigDecimal codPlan);
	public Colectivo activarColectivo(Long id)throws DAOException;
	public ArrayList<BigDecimal> getPlanesFiltroInicial()throws DAOException;
	String getDcColectivo(String idColectivo);
}
