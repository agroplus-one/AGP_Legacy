package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IMtoZonasDao;
import com.rsi.agp.core.jmesa.filter.MtoZonasFilter;
import com.rsi.agp.core.jmesa.sort.MtoZonasSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Zona;

/**
 * @author U029769
 *
 */
public class MtoZonasDao extends BaseDaoHibernate implements IMtoZonasDao {

	/**
	 * MÃ©todo que comprueba si ya existe una zona dada de alta para esa misma
	 * entidad
	 */
	public boolean existeZona(Zona zonaBean) throws DAOException {

		try {
			Session session = obtenerSession();

			BigDecimal codEntidad = zonaBean.getId().getCodentidad();
			BigDecimal codZona = zonaBean.getId().getCodzona();

			String sql = "select count(*) from o02agpe0.tb_zonas_entidad zon where zon.codentidad = " + codEntidad
					+ " and zon.codzona =" + codZona;

			int num = ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();

			if (num > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error en existeZona (MtoZonasDao)", e);
			throw new DAOException("Se ha producido un error en existeZona", e);
		}

		return false;
	}

	@Override
	public void borrarZona(Zona zonaBean) throws DAOException {

		Session session = obtenerSession();
		try {
			// Eliminamos la zona
			Query queryDelete = session
					.createSQLQuery("delete from o02agpe0.tb_zonas_entidad "
							+ " where codentidad = :codentidad and codzona = :codzona")
					.setBigDecimal("codentidad", zonaBean.getId().getCodentidad())
					.setBigDecimal("codzona", zonaBean.getId().getCodzona());
			queryDelete.executeUpdate();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al eliminar la zona", ex);
		}

		logger.debug("Fin borrarZona()");
	}

	/**
	 * Metodo que comprueba si ya existe una zona dada de alta para esa misma
	 * entidad
	 */
	public void modificarZona(Zona zonaBean, BigDecimal codEntIni, BigDecimal codZonaIni) throws DAOException {

		try {
			Session session = obtenerSession();

			// Modificamos la zona
			String sql = "update o02agpe0.tb_zonas_entidad zon set zon.codentidad = " + zonaBean.getId().getCodentidad()
					+ ", zon.codzona = " + zonaBean.getId().getCodzona() + ", zon.nomzona = '" + zonaBean.getNomzona()
					+ "' where zon.codentidad = " + codEntIni + " and zon.codzona = " + codZonaIni;

			logger.debug("Valor de sql:" + sql);

			session.createSQLQuery(sql).executeUpdate();

		} catch (Exception ex) {
			throw new DAOException("Se ha producido un error al eliminar la zona", ex);
		}

		logger.debug("Fin borrarZona()");

	}

	@Override
	public boolean esZonaConOficina(Zona zonaBean) throws DAOException {

		try {
			Session session = obtenerSession();

			BigDecimal codEntidad = zonaBean.getId().getCodentidad();
			BigDecimal codZona = zonaBean.getId().getCodzona();

			String sql = " select count(*) from o02agpe0.tb_oficinas_zonas ofi " + " where ofi.codentidad = "
					+ codEntidad + " and ofi.codzona = " + codZona;

			int num = ((BigDecimal) session.createSQLQuery(sql).list().get(0)).intValue();

			if (num > 0) {
				return true;
			}
			return false;

		} catch (Exception e) {
			logger.error("Se ha producido un error en esZonaConOficina", e);
			throw new DAOException("Se ha producido un error en esZonaConOficina", e);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Zona> getZonasWithFilterAndSort(final MtoZonasFilter filter, final MtoZonasSort sort,
			final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [MtoZonasDao] getZonasWithFilterAndSort");
			
			logger.debug("rowstart: " + rowStart);
			logger.debug("rowend: " + rowEnd);
			
			List<Zona> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
				

				public Object doInHibernate(final Session session) throws HibernateException, SQLException {
					
					Criteria criteria = session.createCriteria(Zona.class);
					// Filtro
					criteria = filter.execute(criteria);
					
					if (sort.getSorts().size()==0) {
						// ESC-17873 - busqueda inicial
						criteria.addOrder(Order.asc("id.codentidad"));
						criteria.addOrder(Order.asc("id.codzona"));
					}
					else {
						// Ordenacion
						criteria = sort.execute(criteria);
					}
					if (rowStart!=-1 && rowEnd !=-1) {
						// Primer registro
						criteria.setFirstResult(rowStart);
						// Numero maximo de registros a mostrar
						criteria.setMaxResults(rowEnd - rowStart);
					}
					final List<Zona> lista = criteria.list();
					
					for (int i = 0; i < lista.size(); i++) {
			            logger.debug("Entidad:" + lista.get(i).getId().getCodentidad() + "   -   CodZona:" + lista.get(i).getId().getCodzona() + "   -    NomZona:" + lista.get(i).getNomzona());
			        }
					
					return lista;
				}
			});
			logger.debug("end - [MtoZonasDao] getZonasWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	/**
	 * Método para recuperar un String con todos los Ids de usurios segun el filtro
	 * 
	 * @param claseDetalleBusqueda
	 * @return listaids
	 */
	public String getlistaIdsTodos(MtoZonasFilter consultaFilter) {
		String listaids = "";
		Session session = obtenerSession();
		String sql = "SELECT CODENTIDAD ||'-'|| CODZONA FROM o02agpe0.TB_ZONAS_ENTIDAD " + consultaFilter.getSqlWhere();
		List lista = session.createSQLQuery(sql).list();

		for (int i = 0; i < lista.size(); i++) {
			listaids += lista.get(i) + ",";
		}
		return listaids;
	}

	@Override
	public int getZonasCountWithFilter(final MtoZonasFilter filter) {
		logger.debug("init - [MtoZonasDao] getZonasCountWithFilter");

		Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				Criteria criteria = session.createCriteria(Zona.class);
				criteria = filter.execute(criteria);

				return criteria.setProjection(Projections.rowCount()).uniqueResult();
			}
		});
		logger.debug("end - [MtoZonasDao] getZonasCountWithFilter");
		return count.intValue();
	}

	/**
	 * obtiene la descripción de la Entidad opr el codEntidad
	 * 
	 * @param codEntidad
	 * 
	 */
	public String getNombEntidad(BigDecimal codEntidad) {
		Session sesion = obtenerSession();

		List<Entidad> lstEntidad = new ArrayList<Entidad>();
		Entidad entidad = null;
		String nombEntidad = "";

		Criteria criteria = sesion.createCriteria(Entidad.class);
		criteria.add(Restrictions.eq("codentidad", codEntidad));

		lstEntidad = criteria.list();

		if (lstEntidad != null) {
			if (lstEntidad.size() > 0) {
				entidad = lstEntidad.get(0);
				nombEntidad = entidad.getNomentidad();
			}
		}

		return nombEntidad;
	}

}
