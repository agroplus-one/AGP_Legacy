package com.rsi.agp.dao.models.inc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.IncidenciasAgroFilter;
import com.rsi.agp.core.jmesa.sort.IncidenciasSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Linea;

public class IncidenciasAgroDao extends BaseDaoHibernate implements IIncidenciasAgroDao {

	private static final Log logger = LogFactory.getLog(IncidenciasAgroDao.class);

	private static final String QUERY_DC_POLIZA = "SELECT P.DC FROM O02AGPE0.TB_POLIZAS P INNER JOIN O02AGPE0.TB_LINEAS L ON P.LINEASEGUROID = L.LINEASEGUROID WHERE P.REFERENCIA = :referencia AND L.CODPLAN = :codPlan AND L.CODLINEA = :codLinea AND P.TIPOREF = :tipoRef";

	private static final String ID_COD_OFICINA = "id.codoficina";
	private static final String ID_COD_ENTIDAD = "id.codentidad";
	private static final String COD_ENTIDAD = "codentidad";
	private static final String COD_LINEA = "codlinea";
	private static final String NOM_LINEA = "nomlinea";
	private static final String NOM_OFICINA = "nomoficina";
	private static final String NOM_ENTIDAD = "nomentidad";

	private static final String CODESTADO = "codestado";
	private static final String COD_ESTADO_BORRADA = "B";

	@Override
	public int getIncidenciasAgroCountWithFilter(final IncidenciasAgroFilter filter, final Date fechaEnvioDesde,
			final Date fechaEnvioHasta) throws DAOException {

		logger.debug("init - [IncidenciasAgroDao] getIncidenciasAgroCountWithFilter");

		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				Criteria criteria = session.createCriteria(VistaIncidenciasAgro.class);

				criteria = crearCriteriaFechas(fechaEnvioDesde, fechaEnvioHasta, criteria);

				logger.debug("Valor del criteria:" + criteria.toString());

				criteria = filter.execute(criteria);
				logger.debug("Valor del criteria(2):" + criteria.toString());

				return criteria.setProjection(Projections.rowCount()).uniqueResult();
			}
		});

		logger.debug("numIncidencias: " + count);

		logger.debug("end - [IncidenciasAgroDao] getIncidenciasAgroCountWithFilter");

		return count.intValue();
	}

	@Override
	public Collection<VistaIncidenciasAgro> getIncidenciasAgroWithFilterAndSort(final IncidenciasAgroFilter filter,
			final IncidenciasSort sort, final int rowStart, final int rowEnd, final Date fechaEnvioDesde,
			final Date fechaEnvioHasta) throws DAOException {

		try {

			logger.debug("init - [IncidenciasAgroDao] getIncidenciasAgroWithFilterAndSort");

			@SuppressWarnings("unchecked")
			List<VistaIncidenciasAgro> incidencias = (List<VistaIncidenciasAgro>) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(final Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(VistaIncidenciasAgro.class);

							criteria = crearCriteriaFechas(fechaEnvioDesde, fechaEnvioHasta, criteria);

							// Filtro
							criteria = filter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							if (rowStart != -1 && rowEnd != -1) {
						        // Primer registro
						        criteria.setFirstResult(rowStart);
						        // Número máximo de registros a mostrar
						        criteria.setMaxResults(rowEnd - rowStart);
						    }
							final List<VistaIncidenciasAgro> lista = criteria.list();
							return lista;
						}
					});

			logger.debug("incidencias resultantes: " + incidencias.size());

			logger.debug("end - [IncidenciasAgroDao] getIncidenciasAgroWithFilterAndSort");

			return incidencias;

		} catch (Exception e) {
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	private Criteria crearCriteriaFechas(final Date fechaEnvioDesde, final Date fechaEnvioHasta, Criteria criteria) {

		if (fechaEnvioDesde != null) {
			criteria.add(Restrictions.ge("fecha", fechaEnvioDesde));
		}

		if (fechaEnvioHasta != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fechaEnvioHasta);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			criteria.add(Restrictions.lt("fecha", calendar.getTime()));
		}
		return criteria;
	}

	@Override
	public String getlistaIdsTodos(final IncidenciasAgroFilter consultaFilter, final Date fechaEnvioDesde,
			final Date fechaEnvioHasta) throws DAOException {

		String listaids = "";

		try {

			logger.debug("init - [IncidenciasAgroDao] getlistaIdsTodos");

			Session session = obtenerSession();

			String sql = "SELECT V.IDINCIDENCIA FROM VW_INC_INCIDENCIAS_AGRO V " + consultaFilter.getSqlWhere();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (fechaEnvioDesde != null) {
				sql += " AND fecha >= TO_DATE('" + sdf.format(fechaEnvioDesde) + "', 'DD/MM/YYYY')";
			}

			if (fechaEnvioHasta != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(fechaEnvioHasta);
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				sql += " AND fecha < TO_DATE('" + sdf.format(calendar.getTime()) + "', 'DD/MM/YYYY')";
			}

			logger.debug(sql);

			@SuppressWarnings("unchecked")
			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			for (int i = 0; i < lista.size(); i++) {
				listaids += lista.get(i).toString() + ",";
			}

			return listaids;

		} catch (Exception e) {

			logger.error("Error: getlistaIdsTodos : " + e);
			throw new DAOException("getlistaIdsTodos : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<EstadosInc> getEstadosInc() throws DAOException {

		Session session = obtenerSession();

		try {

			Criteria criteria = session.createCriteria(EstadosInc.class);
			criteria.add(Restrictions.ne(CODESTADO, COD_ESTADO_BORRADA));

			return criteria.list();

		} catch (Exception e) {

			logger.error("Error: getEstadosInc : " + e);
			throw new DAOException("getTiposDocumento : Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public String getNombreEntidad(BigDecimal codEntidad) {
		return (String) this.obtenerSession().createCriteria(Entidad.class)
				.add(Restrictions.eq(COD_ENTIDAD, codEntidad)).setProjection(Projections.property(NOM_ENTIDAD))
				.uniqueResult();
	}

	@Override
	public String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) {
		return (String) this.obtenerSession().createCriteria(Oficina.class)
				.add(Restrictions.eq(ID_COD_ENTIDAD, codEntidad)).add(Restrictions.eq(ID_COD_OFICINA, codOficina))
				.setProjection(Projections.property(NOM_OFICINA)).uniqueResult();
	}

	@Override
	public String getNombreLinea(BigDecimal codLinea) throws DAOException {
		return (String) this.obtenerSession().createCriteria(Linea.class).add(Restrictions.eq(COD_LINEA, codLinea))
				.setProjection(Projections.distinct(Projections.property(NOM_LINEA))).uniqueResult();
	}

	public BigDecimal getDCPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea) {
		SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_DC_POLIZA);
		query.setString("referencia", referencia);
		query.setBigDecimal("codPlan", codPlan);
		;
		query.setBigDecimal("codLinea", codLinea);
		query.setCharacter("tipoRef", tipoRef);
		BigDecimal uniqueResult = (BigDecimal) query.uniqueResult();
		return uniqueResult;
	}

	@SuppressWarnings("unchecked")
	public boolean getEstadoAsegurado(BigDecimal plan, BigDecimal linea, String refPoliza, String nifcif) {

		boolean aseguradoBloqueado = false;

		logger.debug("init - [IncidenciasAgroDao] getEstadoAsegurado");
		String sql = "";
		BigDecimal cero = new BigDecimal(0);

		Session session = obtenerSession();

		if (!nifcif.equals("")) {
			logger.debug("Buscamos Estado por nifcif");
			/* Buscamos asesgurado por nif */
			sql = " SELECT BLQ.ID_ASEGURADO FROM O02AGPE0.TB_BLOQUEOS_ASEGURADOS BLQ " + " WHERE  BLQ.NIFCIF = '"
					+ nifcif + "'" + " AND BLQ.IDESTADO_ASEG ='B' ";

			logger.debug("Consulta en tabla bloqueos Asegurado por nifcif: " + sql);

			List<BigDecimal> lista = session.createSQLQuery(sql).list();

			if (lista.size() > 0) {
				logger.debug("Asegurado bloqueado, retornamos true");
				return true;
			} else {
				logger.debug("Asegurado NO bloqueado, retornamos false");
				return false;
			}

		} else {
			logger.debug("Buscamos Estado por Referencia de poliza");
			if (!refPoliza.equals(null) && plan.compareTo(cero) != 0) {
				/*
				 * Busdamos el idAsegurado de la poliza para la poliza principal, ya que la
				 * complementaria, en caso de tenerla, tiene el mismo asegurado.
				 */
				String QUERY_POL = " SELECT PO.IDASEGURADO FROM O02AGPE0.TB_POLIZAS PO "
						+ " INNER JOIN O02AGPE0.TB_LINEAS L " + " ON PO.LINEASEGUROID = L.LINEASEGUROID "
						+ " WHERE PO.REFERENCIA = :referencia AND L.CODPLAN = :plan " + " AND PO.TIPOREF ='P'";

				SQLQuery query = this.obtenerSession().createSQLQuery(QUERY_POL);
				query.setString("referencia", refPoliza);
				query.setBigDecimal("plan", plan);

				BigDecimal idAsegurado = (BigDecimal) query.uniqueResult();
				
				if (null!=idAsegurado) {
					
					if (idAsegurado.compareTo(cero) != 0) {
						sql = " SELECT BLQ.ID_ASEGURADO FROM O02AGPE0.TB_BLOQUEOS_ASEGURADOS BLQ "
								+ " WHERE  BLQ.ID_ASEGURADO = " + idAsegurado + "  AND BLQ.IDESTADO_ASEG = 'B'";

						logger.debug("Consulta en tabla bloqueos por idAsegurado: " + sql);

						List<BigDecimal> lista = session.createSQLQuery(sql).list();

						if (lista.size() > 0) {
							logger.debug("Asegurado bloqueado, retornamos true");
							return true;
						} else {
							logger.debug("Asegurado NO bloqueado, retornamos false");
							return false;
						}
					} else {
						logger.debug("No se ha recuperado id del Asegurado");
					}
				}
			}
			/* buscamos por el asegurado de la poliza */

		}
		return aseguradoBloqueado;
	}

}
