package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.cesp.impl.PeriodoGarantiaFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.models.cesp.ICondicionesEspecialesDao;
import com.rsi.agp.dao.models.commons.ICommonDao;
import com.rsi.agp.dao.tables.cesp.PeriodoGarantiaCe;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("unchecked")
public class PeriodoGarantiaManager implements IManager {

	private static final Log LOG = LogFactory.getLog(PeriodoGarantiaManager.class);
	private ICondicionesEspecialesDao dao;
	private ICommonDao commonDao;

	public final List<PeriodoGarantiaCe> consultaPeriodoGarantia(final PeriodoGarantiaCe periodoGarantiaCe) {
		final PeriodoGarantiaFiltro filtro = new PeriodoGarantiaFiltro(periodoGarantiaCe);
		return dao.getObjects(filtro);
	}

	public final void guardaPeriodoGarantia(final PeriodoGarantiaCe periodoGarantiaCe) {
		try {
			dao.saveOrUpdate(periodoGarantiaCe);
		} catch (DAOException e) {
			LOG.error("Se ha producido un error al guardar Periodo de garantia",e);
		}
	}

	public final void bajaPeriodoGarantia(final PeriodoGarantiaCe periodoGarantiaCe) {
		dao.removeObject(PeriodoGarantiaCe.class, periodoGarantiaCe.getId());
	}

	public final List<ConceptoPpalModulo> getAllConceptoPpal() {
		return dao.getAllConceptoPpal();
	}

	public final List<EstadoFenologico> getAllEstadoFenologico() {
		return dao.getAllEstadoFenologico();
	}
	
	@SuppressWarnings("rawtypes")
	public final List getAllLineas() {
		List listaPlan = new ArrayList();
		try {
			listaPlan = commonDao.getLineasNoPlan();
		} catch(BusinessException e) {
			LOG.error("No se ha podido recuperar la lista de planes", e);
		}
		return listaPlan;
	}

	public final List<Object[]> getCultivosByCodLinea(String codLinea) {
		return dao.getCultivosByCodLinea(codLinea);
	}
	
	public final List<Object[]> getEstadosFenologicosByCodCultivo(final BigDecimal codcultivo) {
		return dao.getEstadosFenologicosByCodCultivo(codcultivo);
	}
	

	public final List<RiesgoCubierto> getRiesgosCubiertos(final Long idLinea, final String codModulo) {
		return dao.getRiesgosCubiertos(idLinea, codModulo);
	}

	public final void setCondicionesEspecialesDao(final ICondicionesEspecialesDao condicionesEspecialesDao) {
		this.dao = condicionesEspecialesDao;
	}

	public final void setCommonDao(final ICommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public final List<Linea> getLineaByPlan(final BigDecimal idPlan) {
		return dao.getLineaByPlan(idPlan);
	}

	public final PeriodoGarantiaCe getPeriodoGarantiaCe(Long periodoGarantiaCeId) {
		return (PeriodoGarantiaCe) dao.getObject(PeriodoGarantiaCe.class, periodoGarantiaCeId);
	}

	public final List<Modulo> getModulos(final Long idLinea) {
		final ModuloFiltro filter = new ModuloFiltro(idLinea);
		return dao.getObjects(filter);
	}
}
