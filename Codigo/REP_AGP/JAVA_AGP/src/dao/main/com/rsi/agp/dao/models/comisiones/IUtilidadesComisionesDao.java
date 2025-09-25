package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao.PorcentajesAplicar;
import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;

@SuppressWarnings("rawtypes")
public interface IUtilidadesComisionesDao extends GenericDao {

	public BigDecimal[] obtenerPorcentajesComision(String refPoliza, Date fechaEmision)
			throws DAOException;

	public boolean validarArrayPorcentajesComision(BigDecimal[] arrayPorcentajes);

	public PorcentajesAplicar obtenerPorcentajesComision(String refPoliza, Character tipoRef,
			Long codPlan, Character grupoNegocio,
			Map<String, ColectivosRenovacion> colColectivos,
			Map<String, PorcentajesAplicar> colPorcentajesAplicar,
			Map<String, BigDecimal> colPorcentajeMaximo,
			Map<String, Linea> colLineas, AplicacionUnificado aplicacion,
			Date fechaEmision) throws DAOException;

	public boolean validarPorcentajesComision(PorcentajesAplicar porcentajes);

	public PorcentajesAplicar obtenerPorcentajesComisionUnif17(
			String refPoliza, Character tipoRef, Long codPlan,
			Character grupoNegocio, Date fechaEmision) throws DAOException;
}