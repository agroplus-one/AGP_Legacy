package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.ImpuestosRCFilter;
import com.rsi.agp.core.jmesa.sort.ImpuestosRCSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.rc.ImpuestosRC;

public class ImpuestosRCDao extends BaseDaoHibernate implements IImpuestosRCDao {

	private static final String ID_IMPUESTO_RC = "id";
	private static final String IMPUESTO_SBP = "impuestoSbp";
	private static final String BASE_SBP = "baseSbp";
	private static final String PROCEDIMIENTO = "PQ_REPLICAR.replicarImpuestosRC(CODPLAN_ORIGEN IN NUMBER, CODPLAN_DESTINO IN NUMBER)";
	private static final String CODPLAN_ORIGEN_PLSQL = "CODPLAN_ORIGEN";
	private static final String CODPLAN_DESTINO_PLSQL = "CODPLAN_DESTINO";

	private static final Log LOGGER = LogFactory.getLog(ImpuestosRCDao.class);

	@Override
	public int getImpuestosRCCountWithFilter(final ImpuestosRCFilter filter)
			throws DAOException {

		LOGGER.debug("inicio - ImpuestosRCDao.getDatosRCCountWithFilter");
		try {
			Integer cuenta = (Integer) this.getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(ImpuestosRC.class);
							criteria.createAlias(BASE_SBP, BASE_SBP);
							criteria.createAlias(IMPUESTO_SBP, IMPUESTO_SBP);
							criteria = filter.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			LOGGER.debug("final - ImpuestosRCDao.getDatosRCCountWithFilter");
			return cuenta.intValue();
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public List<ImpuestosRC> getImpuestosRCWithFilterAndSort(
			final ImpuestosRCFilter filter, final ImpuestosRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException {
		try {
			LOGGER.debug("inicio - ImpuestosRCDao.getDatosRCWithFilterAndSort");
			
			@SuppressWarnings("unchecked")
			List<ImpuestosRC> listaImpuestos = (List<ImpuestosRC>) this
					.getHibernateTemplate().execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(ImpuestosRC.class);
							criteria.createAlias(BASE_SBP, BASE_SBP);
							criteria.createAlias(IMPUESTO_SBP, IMPUESTO_SBP);
							// filtro
							criteria = filter.execute(criteria);
							//ordenacion
							criteria = sort.execute(criteria);
							// numero de registros devueltos
							criteria.setMaxResults(rowEnd - rowStart);
							List<ImpuestosRC> lista = criteria.list();
							return lista;
						}
					});
			LOGGER.debug("final - ImpuestosRCDao.getDatosRCWithFilterAndSort");
			return listaImpuestos;
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public void replicarImpuestosRC(final BigDecimal planOrig, final BigDecimal planDest)
			throws DAOException {
		try {
			Map<String, Object> params = new HashMap<String, Object>() {
				private static final long serialVersionUID = -251370221206855234L;
				{
					put(CODPLAN_ORIGEN_PLSQL, planOrig);
					put(CODPLAN_DESTINO_PLSQL, planDest);
				}
			};
			this.databaseManager.executeStoreProc(PROCEDIMIENTO, params);

		} catch (Exception e) {
			throw new DAOException("Error al replicar impuestos para RC", e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public StringBuilder getlistaIdsTodos(final ImpuestosRCFilter filter){
		List<Long> listaIds = (List<Long>)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(ImpuestosRC.class);
				criteria.createAlias(BASE_SBP, BASE_SBP);
				criteria.createAlias(IMPUESTO_SBP, IMPUESTO_SBP);
				criteria = filter.execute(criteria);
				return criteria.setProjection(Projections.property(ID_IMPUESTO_RC)).list();
			}
		});
		StringBuilder stringIds = new StringBuilder();
		for (Long id : listaIds) {
			stringIds.append(id.toString()).append(",");
		}
		return stringIds;
	}

	@Override
	public boolean existeImpuestoRC(final Long ImpuestoRCId)
			throws DAOException {
		try {
			ImpuestosRC impuesto = (ImpuestosRC) this.get(ImpuestosRC.class, ImpuestoRCId);
			if (impuesto != null) {
				return true;
			}
			return false;
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error durante el acceso a la base de datos", e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ImpuestosRC> getImpuestosRC(final BigDecimal plan)
			throws DAOException {
		List<ImpuestosRC> datos = null;
		try {			
			Criteria criteria = this.getSession().createCriteria(ImpuestosRC.class);
			criteria.add(Restrictions.eq("codPlan", plan));
			datos = criteria.list();
			return datos;
		} catch (Exception e) {
			logger.error("Error al obtener impuestos para RC", e);
			throw new DAOException("Error al obtener impuestos para RC", e);
		}
	}
}
