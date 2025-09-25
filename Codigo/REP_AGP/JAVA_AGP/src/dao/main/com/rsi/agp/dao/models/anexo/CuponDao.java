package com.rsi.agp.dao.models.anexo;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.HistoricoCupon;

public class CuponDao extends BaseDaoHibernate implements ICuponDao {

	@Override
	public Cupon saveCupon(final Cupon cupon) throws DAOException {
		try {
			return (Cupon) this.saveOrUpdate(cupon);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al guardar el cupon", e);
			throw new DAOException("Ha ocurrido un error al guardar el cupon");
		}
	}

	@Override
	public HistoricoCupon saveHistoricoCupon(final HistoricoCupon historico)
			throws DAOException {
		try {
			this.saveOrUpdate(historico);
			return historico;
		} catch (DAOException exc) {
			logger.error(
					"Ha ocurrido un error al guardar el historico del cupon",
					exc);
			throw new DAOException(
					"Ha ocurrido un error al guardar el historico del cupon");
		}
	}

	@Override
	public void borrarCupon(final String idCupon) throws DAOException {
		try {
			@SuppressWarnings("unchecked")
			List<Cupon> lista = (List<Cupon>) this.getObjects(Cupon.class,
					"idcupon", idCupon);
			for (Cupon cupon : lista) {
				this.delete(cupon);
			}
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al borrar el cupon con id "
					+ idCupon, e);
			throw new DAOException(
					"Ha ocurrido un error al guardar el historico del cupon");
		}
	}

	@Override
	public boolean esPolizaGanado(final String idCupon) throws DAOException {
		boolean result = false;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(AnexoModificacion.class);
			criteria.createAlias("cupon", "cp");
			criteria.createAlias("poliza", "pol");
			criteria.createAlias("pol.linea", "ln");
			criteria.add(Restrictions.eq("cp.idcupon", idCupon));
			criteria.setProjection(Projections.property("ln.esLineaGanadoCount"));
			result = Long.valueOf(1).equals(criteria.uniqueResult());
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error en esPolizaGanado", e);
		}
		return result;
	}
}
