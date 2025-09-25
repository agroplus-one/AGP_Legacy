package com.rsi.agp.core.jmesa.dao.impl.ganado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IExplotacionDAO;
import com.rsi.agp.core.jmesa.filter.ExplotacionesFilter;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

public class ExplotacionDAO extends BaseDaoHibernate implements IExplotacionDAO {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort,
			final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [ExplotacionDAO] getWithFilterAndSort");
			List<Serializable> explotaciones = (List<Serializable>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(final Session session)
								throws HibernateException, SQLException {
							final ExplotacionesFilter expFilter = (ExplotacionesFilter) filter;
							expFilter.execute();
							Criteria criteria = session
									.createCriteria(Explotacion.class);
							// Filtro
							criteria = expFilter.execute(criteria);
							// Ordenacion
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Numero maximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							final List<Explotacion> lista = criteria.list();
							HashMap<String, List<String>> grupoRazasCols;
							Set<GrupoRaza> grupoRazas;

							for (Explotacion explotacion : lista) {
								grupoRazasCols = explotacion
										.getGrupoRazasCols();
								grupoRazas = explotacion.getGrupoRazas();
								for (GrupoRaza grupo : grupoRazas) {
									grupoRazasCols
											.get(Explotacion.GR_RAZA)
											.add(grupo.getCodgruporaza()
													+ "-"
													+ grupo.getNomgruporaza());
									grupoRazasCols
											.get(Explotacion.GR_TIPOCAPITAL)
											.add(grupo.getCodtipocapital()
													+ "-"
													+ grupo.getNomtipocapital());
									grupoRazasCols
											.get(Explotacion.GR_TIPOANIMAL)
											.add(grupo.getCodtipoanimal()
													+ "-"
													+ grupo.getNomtipoanimal());
									grupoRazasCols.get(Explotacion.GR_NUMERO)
											.add("" + grupo.getNumanimales());
									BigDecimal precioMax = new BigDecimal(0);
									for (PrecioAnimalesModulo precioAnimalModulo : grupo
											.getPrecioAnimalesModulos()) {
										precioMax = precioMax
												.max(precioAnimalModulo
														.getPrecio());
									}
									grupoRazasCols.get(Explotacion.GR_PRECIO)
											.add(NumberUtils.formatear(
													precioMax, 4));
								}
								explotacion.setGrupoRazasCols(grupoRazasCols);
							}
							return lista;
						}
					});
			logger.debug("end - [ExplotacionDAO] getWithFilterAndSort");
			return explotaciones;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	public int getCountWithFilter(final CriteriaCommand filter) {
		logger.debug("init - [ExplotacionDAO] getCountWithFilter");
		final ExplotacionesFilter expFilter = (ExplotacionesFilter) filter;
		expFilter.execute();
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(Explotacion.class);
						criteria = expFilter.execute(criteria);
						return criteria.setProjection(Projections.rowCount())
								.uniqueResult();
					}
				});
		logger.debug("end - [ExplotacionDAO] getCountWithFilter");
		return count.intValue();
	}

	@Override
	public List<Long> obtenerExplotacionesConVariosGruposRaza(final Long idPoliza) throws DAOException {
		
		Session session = obtenerSession();
		List<Long> listaIdsExplotacion = null;
		
		try {
			
			Query query = session.createSQLQuery("select exp.id id from tb_explotaciones exp, " +
												"tb_grupo_raza_explotacion gr where exp.idpoliza = " + idPoliza + " and " +
												"gr.idexplotacion = exp.id " +
												"group by exp.id " +
												"having count(*)>1").addScalar("id", Hibernate.LONG);

			listaIdsExplotacion = query.list();
		
		} catch (Exception e) {
			logger.error("Se ha producido un error durante el acceso a la base de datos ", e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		return listaIdsExplotacion;
	}
}