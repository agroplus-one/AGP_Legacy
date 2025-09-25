package com.rsi.agp.core.managers.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.ref.ReferenciaColectivoFiltro;
import com.rsi.agp.dao.models.ref.IReferenciaColectivoDao;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;

public class ReferenciaColectivoManager implements IManager {

	private IReferenciaColectivoDao referenciaColectivoDao;
	private static final Log LOG = LogFactory.getLog(ReferenciaColectivoManager.class);
	
	@SuppressWarnings("unchecked")
	public final List<ColectivoReferencia> getReferencias(final Date fechaIni, final Date fechaFin) {
		final ReferenciaColectivoFiltro filter = new ReferenciaColectivoFiltro(fechaIni, fechaFin);
		return referenciaColectivoDao.getObjects(filter);
	}
	@SuppressWarnings("unchecked")
	public final List<ColectivoReferencia> getReferenciasRango(final String referenciaIni,final String referenciaFin) {
		final ReferenciaColectivoFiltro filter = new ReferenciaColectivoFiltro(referenciaIni, referenciaFin);
		List prueba = referenciaColectivoDao.getObjects(filter);
		return prueba;
	}

	@SuppressWarnings("unchecked")
	public final List<ColectivoReferencia> getReferenciasDisponibles() {
		final ReferenciaColectivoFiltro filter = new ReferenciaColectivoFiltro(false);
		return referenciaColectivoDao.getObjects(filter);
	}

	@SuppressWarnings("unchecked")
	public final Integer getNumRefLibres() {
		final ReferenciaColectivoFiltro filter = new ReferenciaColectivoFiltro(true);
		final Integer resultado = referenciaColectivoDao.getNumObjects(filter);
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public final String getUltimaRef() {
		final String resultado = referenciaColectivoDao.getUltimaRef();
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public final ColectivoReferencia getRefColectivoByRef(final String referencia) {
		ColectivoReferencia resultado = null;
		final ReferenciaColectivoFiltro filter = new ReferenciaColectivoFiltro(referencia);
		final List<ColectivoReferencia> listaReferencias = referenciaColectivoDao.getObjects(filter);
		if (listaReferencias.size() > 0) {
			resultado = listaReferencias.get(0);
		}
		return resultado;
	}

	public final void addReferencias(final List<ColectivoReferencia> listRefNuevas) {
		for (ColectivoReferencia referencia : listRefNuevas) {
			try {
				referenciaColectivoDao.saveOrUpdate(referencia);
			} catch (DAOException e) {
				LOG.error("Se ha producido un error al guardar las referencias",e);
			}
		}
	}

	public final void setreferenciaColectivoDao(final IReferenciaColectivoDao referenciaColectivoDao) {
		this.referenciaColectivoDao = referenciaColectivoDao;
	}

}

