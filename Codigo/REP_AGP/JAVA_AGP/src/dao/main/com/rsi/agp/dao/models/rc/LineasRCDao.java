package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.LineasRCFilter;
import com.rsi.agp.core.jmesa.sort.LineasRCSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.rc.EspeciesRC;
import com.rsi.agp.dao.tables.rc.LineasRC;

public class LineasRCDao extends BaseDaoHibernate implements ILineasRCDao {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<EspeciesRC> getEspeciesRC() throws DAOException {

		try {

			logger.debug("init - [LineasRCDao] getEspeciesRC");

			return this.findAll(EspeciesRC.class);

		} catch (Exception e) {

			logger.error("Error: getEspeciesRC : " + e);
			throw new DAOException(
					"getTiposDocumento : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public String getlistaIdsTodos(final LineasRCFilter consultaFilter)
			throws DAOException {

		String listaids = "";

		try {

			logger.debug("init - [LineasRCDao] getlistaIdsTodos");

			Session session = obtenerSession();

			String sql = "SELECT LRC.ID FROM TB_RC_LINEAS LRC, TB_LINEAS L "
					+ consultaFilter.getSqlWhere();

			@SuppressWarnings("unchecked")
			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			for (int i = 0; i < lista.size(); i++) {
				listaids += lista.get(i).toString() + ",";
			}

			return listaids;

		} catch (Exception e) {

			logger.error("Error: getlistaIdsTodos : " + e);
			throw new DAOException(
					"getlistaIdsTodos : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public int getLineasRCCountWithFilter(final LineasRCFilter filter)
			throws DAOException {

		logger.debug("init - [LineasRCDao] getLineasRCCountWithFilter");

		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Criteria criteria = session
								.createCriteria(LineasRC.class);
						criteria.createAlias("linea", "lineaseguro");
						criteria.createAlias("especiesRC", "especiesRC");
						criteria = filter.execute(criteria);

						return criteria.setProjection(Projections.rowCount())
								.uniqueResult();
					}
				});

		logger.debug("end - [LineasRCDao] getLineasRCCountWithFilter");

		return count.intValue();
	}

	@Override
	public Collection<LineasRC> getLineasRCWithFilterAndSort(
			final LineasRCFilter filter, final LineasRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException {

		try {

			logger.debug("init - [LineasRCDao] getLineasRCWithFilterAndSort");

			@SuppressWarnings("unchecked")
			List<LineasRC> applications = (List<LineasRC>) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(LineasRC.class);
							criteria.createAlias("linea", "lineaseguro");
							criteria.createAlias("especiesRC", "especiesRC");
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<LineasRC> lista = criteria.list();
							return lista;
						}
					});

			logger.debug("end - [LineasRCDao] getLineasRCWithFilterAndSort");

			return applications;

		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public void replicaLineaRC(final BigDecimal planOrig,
			final BigDecimal lineaOrig, final BigDecimal planDest,
			final BigDecimal lineaDest) throws DAOException {

		try {
			// Procedimiento de réplica
			String procedimiento = "PQ_REPLICAR.replicarLineasRC (CODPLAN_ORIGEN IN NUMBER, CODLINEA_ORIGEN IN NUMBER, CODPLAN_DESTINO IN NUMBER, CODLINEA_DESTINO IN NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(4); // parámetros PL
			parametros.put("CODPLAN_ORIGEN", planOrig);
			parametros.put("CODLINEA_ORIGEN", lineaOrig);
			parametros.put("CODPLAN_DESTINO", planDest);
			parametros.put("CODLINEA_DESTINO", lineaDest);

			this.databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL

		} catch (Exception e) {

			logger.error("Error al replicar líneas para RC", e);
			throw new DAOException("Error al replicar líneas para RC", e);
		}
	}
}