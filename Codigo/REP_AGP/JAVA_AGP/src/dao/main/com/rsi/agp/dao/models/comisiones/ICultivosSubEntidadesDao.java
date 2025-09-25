package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("rawtypes")
public interface ICultivosSubEntidadesDao extends GenericDao {

	public List<CultivosSubentidades> listCultivosSubentidades(CultivosSubentidades cultivosSubentidadesBean, boolean pctGeneral) throws DAOException;

	public Linea getLineaseguroId(BigDecimal codlinea, BigDecimal codplan) throws DAOException;

	public SubentidadMediadora getSubentidadMediadora(SubentidadMediadora subentidadMediadora) throws DAOException;

	public Integer existeRegistro(CultivosSubentidades cultivosSubentidadesBean) throws DAOException;

	public BigDecimal getPlanActual() throws DAOException;

	public ArrayList<CultivosSubentidadesHistorico> consultaHistorico(Long id) throws DAOException;

	public BigDecimal getLineaActual() throws DAOException;
}
