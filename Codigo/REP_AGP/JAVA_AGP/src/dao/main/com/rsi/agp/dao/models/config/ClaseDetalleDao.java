package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClaseDetalleFilter;
import com.rsi.agp.core.jmesa.sort.ClaseDetalleSort;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.admin.ClaseDetalleItem;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Variedad;

@SuppressWarnings("unchecked")
public class ClaseDetalleDao extends BaseDaoHibernate implements IClaseDetalleDao {

	private final String TODOS_99999 = "99999";
	private final BigDecimal TODOS_999 = BigDecimal.valueOf(999);
	private final BigDecimal TODOS_99 = BigDecimal.valueOf(99);
	private final Character TODOS_9 = '9';
	
	@Override
	public int getConsultaClaseDetalleCountWithFilter(final ClaseDetalleFilter filter) {
		logger.debug("init - [ClaseDetalleDao] getConsultaClaseDetalleCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(ClaseDetalle.class);

						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount())
								.uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger.debug("end - [ClaseDetalleDao] getConsultaClaseDetalleCountWithFilter");
		return count.intValue();
		 
	}

	@Override
	@SuppressWarnings("all")
	public Collection<ClaseDetalle> getClaseDetalleWithFilterAndSort(
			final ClaseDetalleFilter filter,final ClaseDetalleSort sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger
					.debug("init - [ClaseDetalleDao] getClaseDetalleWithFilterAndSort");
			Collection<ClaseDetalle> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(ClaseDetalle.class);
							// Alias
							criteria.createAlias("cicloCultivo","ciclo", CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("sistemaCultivo","sist", CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("tipoCapital","tCap", CriteriaSpecification.LEFT_JOIN);
							criteria.createAlias("tipoPlantacion","tPlant", CriteriaSpecification.LEFT_JOIN);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							
							List<ClaseDetalle> lista = criteria.list();
							List<ClaseDetalleItem> listaItem = new ArrayList<ClaseDetalleItem>();
							for (ClaseDetalle clDet : lista){
								listaItem.add(new ClaseDetalleItem(clDet));
							}
							return listaItem;
						}
					});
			logger.debug("end - [ClaseDetalleDao] getClaseDetalleWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public Cultivo getCultivo(final Long lineaseguroid, final BigDecimal codcultivo){
		
		Cultivo cultivo = (Cultivo) getHibernateTemplate().execute(
				new HibernateCallback() {
					
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(Cultivo.class);
						criteria.add(Restrictions.eq ("id.lineaseguroid",lineaseguroid));
						criteria.add(Restrictions.eq ("id.codcultivo",codcultivo));
						
						List<Cultivo> lista = criteria.list();
						
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return cultivo;
	}

	@Override
	public Modulo getModulo(final Long lineaseguroid, final String codmodulo) {
		
		Modulo modulo = (Modulo) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(Modulo.class);
						criteria.add(Restrictions.eq ("id.lineaseguroid",lineaseguroid));
						criteria.add(Restrictions.eq ("id.codmodulo",codmodulo));
						
						List<Modulo> lista = criteria.list();
						
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return modulo;
	}

	@Override
	public Variedad getVariedad(final Long lineaseguroid, final BigDecimal codcultivo,final BigDecimal codvariedad) {
		
		Variedad variedad = (Variedad) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(Variedad.class);
						criteria.add(Restrictions.eq ("id.lineaseguroid",lineaseguroid));
						criteria.add(Restrictions.eq ("id.codcultivo",codcultivo));
						criteria.add(Restrictions.eq ("id.codvariedad",codvariedad));
						
						
						List<Variedad> lista = criteria.list();
						
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return variedad;
	}

	@Override
	public CicloCultivo getCicloCultivo(final BigDecimal cicloCultivo) {
		CicloCultivo obj = (CicloCultivo) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(CicloCultivo.class);
						criteria.add(Restrictions.eq ("codciclocultivo",cicloCultivo));
						List<CicloCultivo> lista = criteria.list();
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return obj;
	}

	@Override
	public SistemaCultivo getSistemaCultivo(final BigDecimal sistemaCultivo) {
		SistemaCultivo obj = (SistemaCultivo) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(SistemaCultivo.class);
						criteria.add(Restrictions.eq ("codsistemacultivo",sistemaCultivo));
						List<SistemaCultivo> lista = criteria.list();
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return obj;
	}
	
	@Override
	public TipoCapital getTipoCapital(final BigDecimal tCapital) {
		TipoCapital obj = (TipoCapital) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(TipoCapital.class);
						criteria.add(Restrictions.eq ("codtipocapital",tCapital));
						List<TipoCapital> lista = criteria.list();
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return obj;
	}
	
	@Override
	public TipoPlantacion getTipoPlantacion(final BigDecimal tPlantacion) {
		TipoPlantacion obj = (TipoPlantacion) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						Criteria criteria = session.createCriteria(TipoPlantacion.class);
						criteria.add(Restrictions.eq ("codtipoplantacion",tPlantacion));
						List<TipoPlantacion> lista = criteria.list();
						return (lista.size() == 0 ? null : lista.get(0));
					}
				});
	
	return obj;
	}

	@Override
	public List<BigDecimal> getClaseDetallePorClaseModulo(final Long lineaseguroid, final BigDecimal idClase,final String codmodulo) {
		
		logger.debug("ClaseDetalleDao.getClaseDetallePorClaseModulo - lineaseguroid:" + lineaseguroid +", idClase:" 
					 + idClase + ", codmodulo:" + codmodulo);
		
		List<BigDecimal> listado = (List<BigDecimal>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public List<BigDecimal> doInHibernate(Session session)
							throws HibernateException, SQLException {
								Criteria criteria = session.createCriteria(ClaseDetalle.class);
								criteria.createAlias("clase","clase");
								criteria.createAlias("clase.linea","linea");
								criteria.add(Restrictions.eq ("linea.lineaseguroid", lineaseguroid));
								criteria.add(Restrictions.eq ("clase.clase", idClase));
								criteria.add(Restrictions.eq ("codmodulo", codmodulo));
								criteria.setProjection(Projections.distinct(Projections.property("tipoCapital.codtipocapital")));
								return criteria.list();
					}
				});
		
		return listado;
	}

	@Override
	public boolean existeClaseDetalle(ClaseDetalle claseDetalle) {
		Session session = this.obtenerSession();
		Criteria criteria = session.createCriteria(ClaseDetalle.class);
		if (null != claseDetalle.getCodmodulo())
			criteria.add(Restrictions.eq("codmodulo",claseDetalle.getCodmodulo()));
		
		if (null != claseDetalle.getCicloCultivo()){
			if (null != claseDetalle.getCicloCultivo().getCodciclocultivo()){
				criteria.createAlias("cicloCultivo", "cicloCultivo");
				criteria.add(Restrictions.eq("cicloCultivo.codciclocultivo", claseDetalle.getCicloCultivo().getCodciclocultivo()));
			}
		}
		if (null != claseDetalle.getSistemaCultivo()){
			if (null != claseDetalle.getSistemaCultivo().getCodsistemacultivo()){
				criteria.createAlias("sistemaCultivo", "sistemaCultivo");
				criteria.add(Restrictions.eq("sistemaCultivo.codsistemacultivo", claseDetalle.getSistemaCultivo().getCodsistemacultivo()));
			}
		}
		if (null != claseDetalle.getCultivo()){
			if (null != claseDetalle.getCultivo().getId())
				if (null != claseDetalle.getCultivo().getId().getCodcultivo()){
					criteria.createAlias("cultivo", "cultivo");
					criteria.add(Restrictions.eq("cultivo.id.codcultivo", claseDetalle.getCultivo().getId().getCodcultivo()));
				}
		}
		if (null != claseDetalle.getVariedad()){
			if (null != claseDetalle.getVariedad().getId())
				if (null != claseDetalle.getVariedad().getId().getCodvariedad()){
					criteria.createAlias("variedad", "variedad");
					criteria.add(Restrictions.eq("variedad.id.codvariedad", claseDetalle.getVariedad().getId().getCodvariedad()));
				}
		}
		if (null != claseDetalle.getCodprovincia())
			criteria.add(Restrictions.eq("codprovincia", claseDetalle.getCodprovincia()));
		
		if (null != claseDetalle.getCodcomarca())
			criteria.add(Restrictions.eq("codcomarca", claseDetalle.getCodcomarca()));
		
		if (null != claseDetalle.getCodtermino())
			criteria.add(Restrictions.eq("codtermino", claseDetalle.getCodtermino()));
		
		if (null != claseDetalle.getSubtermino())
			criteria.add(Restrictions.eq("subtermino", claseDetalle.getSubtermino()));
		
		if (null != claseDetalle.getTipoCapital()){
			if (null != claseDetalle.getTipoCapital().getCodtipocapital()){
				criteria.createAlias("tipoCapital", "tipoCapital");
				criteria.add(Restrictions.eq("tipoCapital.codtipocapital", claseDetalle.getTipoCapital().getCodtipocapital()));
			}
		}		
		
		if (null != claseDetalle.getTipoPlantacion()){
			if (null != claseDetalle.getTipoPlantacion().getCodtipoplantacion()){
				criteria.createAlias("tipoPlantacion", "tipoPlantacion");
				criteria.add(Restrictions.eq("tipoPlantacion.codtipoplantacion", claseDetalle.getTipoPlantacion().getCodtipoplantacion()));
			}
		}		
		if (null != claseDetalle.getClase()){ 
			if (null != claseDetalle.getClase().getLinea()){
				if (null != claseDetalle.getClase().getLinea().getLineaseguroid()){
					criteria.createAlias("clase", "clase");
					criteria.createAlias("clase.linea", "linea");
					criteria.add(Restrictions.eq("linea.lineaseguroid", claseDetalle.getClase().getLinea().getLineaseguroid()));
					// MPM - Se añade el filtro por idclase, ya que si no se considera que el detalle está repetido cuando 
					// pertenece a otra clase
					criteria.add(Restrictions.eq("clase.id", claseDetalle.getClase().getId()));
				}
			}
		}
		if (null != claseDetalle.getId()){
			criteria.add(Restrictions.ne("id", claseDetalle.getId()));
		}
		List<ClaseDetalle> list =criteria.list();
		if (list.size()>0){
			return true;
		}
		return false;
	}

	/** DAA 06/02/2013  Metodo para recuperar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param claseDetalleBusqueda
	 * @return listaids
	 */
	@SuppressWarnings("rawtypes")
	public String getlistaIdsTodos(ClaseDetalleFilter consultaFilter) {
		String listaids="";
		Session session = obtenerSession();
		String sql = "SELECT D.ID FROM TB_CLASE_DETALLE D " +consultaFilter.getSqlWhere();
		List lista = session.createSQLQuery(sql).list();
		
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		return listaids;
	}

	@Override
	public BigDecimal getClaseSitAct(final es.agroseguro.contratacion.Poliza sitAct, final Long lineaseguroid) throws DAOException {
		BigDecimal clase;
		logger.debug("[getClaseSitAct] init");
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(ClaseDetalle.class);
			criteria.createAlias("clase","clase");
			criteria.createAlias("clase.linea","linea");
			criteria.add(Restrictions.eq ("linea.lineaseguroid", lineaseguroid));
			Conjunction c = Restrictions.conjunction();
			c.add(Restrictions.disjunction().add(Restrictions.eq("codmodulo", sitAct.getCobertura().getModulo().trim()))
					.add(Restrictions.eq("codmodulo", TODOS_99999)));
			es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela = null;
			Node node = sitAct.getObjetosAsegurados().getDomNode().getFirstChild();
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocument = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory
							.parse(node);
					if (parcelaDocument != null) {
						parcela = parcelaDocument.getParcela();
						break;
					}
				}
			}
			if (parcela != null) {
				if (FiltroUtils.noEstaVacio(parcela.getCosecha().getCultivo())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("variedad.id.codcultivo",
									BigDecimal.valueOf(parcela.getCosecha().getCultivo())))
							.add(Restrictions.eq("variedad.id.codcultivo", TODOS_999)));
				}
				if (FiltroUtils.noEstaVacio(parcela.getCosecha().getVariedad())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("variedad.id.codvariedad",
									BigDecimal.valueOf(parcela.getCosecha().getVariedad())))
							.add(Restrictions.eq("variedad.id.codvariedad", TODOS_999)));
				}
				if (FiltroUtils.noEstaVacio(parcela.getUbicacion().getProvincia())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("codprovincia",
									BigDecimal.valueOf(parcela.getUbicacion().getProvincia())))
							.add(Restrictions.eq("codprovincia", TODOS_99)));
				}
				if (FiltroUtils.noEstaVacio(parcela.getUbicacion().getComarca())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("codcomarca", BigDecimal.valueOf(parcela.getUbicacion().getComarca())))
							.add(Restrictions.eq("codcomarca", TODOS_99)));
				}
				if (FiltroUtils.noEstaVacio(parcela.getUbicacion().getTermino())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("codtermino", BigDecimal.valueOf(parcela.getUbicacion().getTermino())))
							.add(Restrictions.eq("codtermino", TODOS_999)));
				}
				if (FiltroUtils.noEstaVacio(parcela.getUbicacion().getSubtermino())) {
					c.add(Restrictions.disjunction()
							.add(Restrictions.eq("subtermino", parcela.getUbicacion().getSubtermino().charAt(0)))
							.add(Restrictions.eq("subtermino", TODOS_9)));
				}
				criteria.add(c);
				List<ClaseDetalle> auxList = criteria.list();
				logger.debug("[getClaseSitAct] end");
				clase = auxList.isEmpty() ? null : auxList.get(0).getClase().getClase();
			} else {
				clase = null;
			}
			return clase;
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][dentroDeAmbitoAsegurable] error lectura BD", e);
		}
	}
}
