package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.VistaPolizasRCFilter;
import com.rsi.agp.core.jmesa.sort.VistaPolizasRCSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.rc.ErroresRC;
import com.rsi.agp.dao.tables.rc.EstadosRC;
import com.rsi.agp.dao.tables.rc.PermisosPerfilRC;
import com.rsi.agp.dao.tables.rc.VistaPolizasRC;

@SuppressWarnings("unchecked")
public class ListadoPolizasRCDao extends BaseDaoHibernate implements IListadoPolizasRCDao {
	
	private static final String ID_COD_OFICINA = "id.codoficina";
	private static final String ID_COD_ENTIDAD = "id.codentidad";
	private static final String COD_ENTIDAD2 = "codentidad";
	private static final String NOM_OFICINA = "nomoficina";
	private static final String NOM_ENTIDAD = "nomentidad";
	private static final String[] ESTADOS_VALIDOS = {"P.", "D.", "E.P.", "E.E.", "E.", "A."};
	private static final BigDecimal ESTADO_RC_NO_MOSTRAR = new BigDecimal("0");
	private static final String ID_POLIZA = "idpoliza";
	private static final String ID_ESTADO_RC = "id";
	private static final String ABREVIATURA = "abreviatura";
	
	private static final Log LOGGER = LogFactory.getLog(ListadoPolizasRCDao.class);

	@Override
	public int getVistaPolizasRCCountWithFilter(final VistaPolizasRCFilter filter)
			throws DAOException {
		LOGGER.debug("Obteniendo - ListadoPolizasRCDao.getDatosRCCountWithFilter");
		try {
			return (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(VistaPolizasRC.class);
							criteria = filter.execute(criteria);
							criteria = criteria.setProjection(Projections.rowCount());
							Integer integer = (Integer)criteria.uniqueResult();
							return integer;
						}
					});
		} catch (DataAccessException e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	
	@Override
	public List<VistaPolizasRC> getVistaPolizasRCWithFilterAndSort(
			final VistaPolizasRCFilter filter, final VistaPolizasRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException {
		LOGGER.debug("Obteniendo - ListadoPolizasRCDao.getDatosRCWithFilterAndSort");
		try {
			return (List<VistaPolizasRC>) this.getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(VistaPolizasRC.class);
							criteria = filter.execute(criteria);
							criteria = sort.execute(criteria);
							criteria.setFirstResult(rowStart);
							criteria.setMaxResults(rowEnd - rowStart);
							return criteria.list();
						}
					});
		} catch (DataAccessException e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		}
	}

	@Override
	public String getlistaIdsTodos(final VistaPolizasRCFilter filter)
			throws DAOException {
		StringBuilder stringIds = new StringBuilder();
		List<BigDecimal> listaIds = (List<BigDecimal>) this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Criteria criteria = session.createCriteria(VistaPolizasRC.class);
				criteria = filter.execute(criteria);
				return (List<BigDecimal>)criteria.setProjection(Projections.property(ID_POLIZA)).list();
			}
		});
		for (BigDecimal id : listaIds) {
			stringIds.append(id.toString()).append(",");
		}
		return stringIds.toString();
	}

	@Override
	public List<EstadosRC> getEstadosRC() throws DAOException{
		return (List<EstadosRC>) this.obtenerSession()
				.createCriteria(EstadosRC.class)
				.add(Restrictions.not(Restrictions.eq(ID_ESTADO_RC, ESTADO_RC_NO_MOSTRAR))).list();
	}

	@Override
	public List<EstadoPoliza> getEstadoPoliza() throws DAOException{	
		return (List<EstadoPoliza>) this.obtenerSession()
				.createCriteria(EstadoPoliza.class)
				.add(Restrictions.in(ABREVIATURA, ESTADOS_VALIDOS)).list();
	}

	@Override
	public List<ErroresRC> getErroresRC() throws DAOException{
		return (List<ErroresRC>) this.obtenerSession().createCriteria(ErroresRC.class).list();
	}


	@Override
	public String getNombreEntidad(BigDecimal codEntidad) {
		return (String)this.obtenerSession()
			.createCriteria(Entidad.class)
			.add(Restrictions.eq(COD_ENTIDAD2, codEntidad))
			.setProjection(Projections.property(NOM_ENTIDAD)).uniqueResult();
	}


	@Override
	public String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) {
		return (String)this.obtenerSession()
				.createCriteria(Oficina.class)
				.add(Restrictions.eq(ID_COD_ENTIDAD, codEntidad))
				.add(Restrictions.eq(ID_COD_OFICINA, codOficina))
				.setProjection(Projections.property(NOM_OFICINA)).uniqueResult();
	}


	@Override
	public PermisosPerfilRC getPermisosRC(BigDecimal perfilUsuario) {
		return (PermisosPerfilRC)this.obtenerSession()
				.createCriteria(PermisosPerfilRC.class)
				.add(Restrictions.eq("codperfil", perfilUsuario))
				.uniqueResult();
	}
}
