package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.EntidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.admin.TipoMediador;
import com.rsi.agp.dao.tables.admin.TipoMediadorAgro;
import com.rsi.agp.dao.tables.admin.TipoMediadorEntMed;
import com.rsi.agp.dao.tables.comisiones.InformeMediadores;
import com.rsi.agp.dao.tables.config.ConfigAgp;
import com.rsi.agp.dao.tables.poliza.Linea;

public class SubentidadMediadoraDao extends BaseDaoHibernate implements ISubentidadMediadoraDao {
 
	private static final Log logger = LogFactory.getLog(SubentidadMediadoraDao.class);
	private static final String PLANES_COLECTIVOS = "PLANES_COLECTIVOS";

	@SuppressWarnings("unchecked")
	@Override
	public List<SubentidadMediadora> listSubentidadesGrupoEntidad(SubentidadMediadora subentidadMediadora) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.addOrder(Order.asc("id.codentidad"));		
			
			if(FiltroUtils.noEstaVacio(subentidadMediadora.getEntidad())){
				if (FiltroUtils.noEstaVacio(subentidadMediadora.getEntidad().getCodentidad())) {			
					criteria.add(Restrictions.eq("entidad.codentidad", subentidadMediadora.getEntidad().getCodentidad()));
				}	
			}
			
			if(FiltroUtils.noEstaVacio(subentidadMediadora.getId())){
				
				if (FiltroUtils.noEstaVacio(subentidadMediadora.getId().getCodentidad())) {
					criteria.add(Restrictions.eq("id.codentidad", subentidadMediadora.getId().getCodentidad()));
				}
				if (subentidadMediadora.getId().getCodsubentidad() !=  null) {
					criteria.add(Restrictions.eq("id.codsubentidad", subentidadMediadora.getId().getCodsubentidad()));
				}
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getTipoidentificacion())) {
				criteria.add(Restrictions.eq("tipoidentificacion", subentidadMediadora.getTipoidentificacion()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getNifcif())) {
				criteria.add(Restrictions.eq("nifcif", subentidadMediadora.getNifcif()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getNombre())) {
				criteria.add(Restrictions.eq("nombre", subentidadMediadora.getNombre()));
			}

			if (FiltroUtils.noEstaVacio(subentidadMediadora.getApellido1())) {
				criteria.add(Restrictions.eq("apellido1", subentidadMediadora.getApellido1()));
			}

			if (FiltroUtils.noEstaVacio(subentidadMediadora.getApellido2())) {
				criteria.add(Restrictions.eq("apellido2", subentidadMediadora.getApellido2()));
			}

			if (FiltroUtils.noEstaVacio(subentidadMediadora.getNomsubentidad())) {
				criteria.add(Restrictions.eq("nomsubentidad", subentidadMediadora.getNomsubentidad()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getPagodirecto())) {
				criteria.add(Restrictions.eq("pagodirecto", subentidadMediadora.getPagodirecto()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getCodpostal())) {
				criteria.add(Restrictions.eq("codpostal", subentidadMediadora.getCodpostal()));
			}
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getCargoCuenta())) {
				criteria.add(Restrictions.eq("cargoCuenta", subentidadMediadora.getCargoCuenta()));
			}
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getFechabaja())){
				criteria.add(Restrictions.like("fechabaja", subentidadMediadora.getFechabaja()));
			}
			// se saca todos los resultados cuya fecha de baja sea null
			//criteria.add(Restrictions.isNull("fechabaja"));
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getForzarRevisionAM())) {
				criteria.add(Restrictions.eq("forzarRevisionAM", subentidadMediadora.getForzarRevisionAM()));
			}	
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getCalcularRcGanado())) {
				criteria.add(Restrictions.eq("calcularRcGanado", subentidadMediadora.getCalcularRcGanado()));
			}	
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getSwConfirmacion())) {
				criteria.add(Restrictions.eq("swConfirmacion", subentidadMediadora.getSwConfirmacion()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getIndGastosAdq())) {
				criteria.add(Restrictions.eq("indGastosAdq", subentidadMediadora.getIndGastosAdq()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getEmail())) {
				criteria.add(Restrictions.eq("email", subentidadMediadora.getEmail()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getEmail2())) {
				criteria.add(Restrictions.eq("email2", subentidadMediadora.getEmail2()));
			}
			
			if (FiltroUtils.noEstaVacio(subentidadMediadora.getFirmaTableta())) {
				criteria.add(Restrictions.eq("firmaTableta", subentidadMediadora.getFirmaTableta()));
			}
			
			
			return criteria.list();
			
		} catch (Exception e) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}		
	}
	
	@Override
	public Integer existeRegistro(SubentidadMediadora subentidadMediadoraBean,boolean addFiltroFechaBaja,BigDecimal codEntidad) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.add(Restrictions.eq("id.codentidad", subentidadMediadoraBean.getId().getCodentidad()));
			criteria.add(Restrictions.eq("id.codsubentidad", subentidadMediadoraBean.getId().getCodsubentidad()));
			if(null!=codEntidad){
				criteria.add(Restrictions.eq("entidad.codentidad", codEntidad));
			}
			if (addFiltroFechaBaja) {
				criteria.add(Restrictions.isNull("fechabaja"));
			}
			criteria.setProjection(Projections.rowCount());
			
			return (Integer) criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}

	
	public Integer existeRegistro(BigDecimal CodentidadEM, BigDecimal CodsubentidadEM, 
			boolean addFiltroFechaBaja,BigDecimal codEntidad) throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.add(Restrictions.eq("id.codentidad", CodentidadEM));
			criteria.add(Restrictions.eq("id.codsubentidad", CodsubentidadEM));
			if(null!=codEntidad)
				criteria.add(Restrictions.eq("entidad.codentidad", codEntidad));
			
			if (addFiltroFechaBaja) {
				criteria.add(Restrictions.isNull("fechabaja"));
			}
			criteria.setProjection(Projections.rowCount());
			
			return (Integer) criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TipoMediador> getListTiposMediador() throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(TipoMediador.class);
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InformeMediadores> listInformeMediadoresBySubent(SubentidadMediadora subentidadMediadoraBean) throws DAOException {
		Session sesion = obtenerSession();		
		try {
			
			Criteria criteria = sesion.createCriteria(InformeMediadores.class);
			criteria.createAlias("subentidadMediadora", "subentidadMediadora");
			
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", subentidadMediadoraBean.getId().getCodentidad()));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", subentidadMediadoraBean.getId().getCodsubentidad()));			
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<SubentidadMediadora> getAll()throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);			
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<EntidadMediadora> getAllEntMediadoras()throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(EntidadMediadora.class);
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}	
	public boolean esSubEntValidaParaEntMed(BigDecimal codentidad,
			BigDecimal codsubentidad) throws DAOException {
		
		Integer count=0;
		try {
			Session sesion = obtenerSession();
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.add(Restrictions.eq("id.codentidad", codentidad));
			criteria.add(Restrictions.eq("id.codsubentidad", codsubentidad));
			
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}
	public boolean isSubentidadMedBaja(BigDecimal codentidad,
			BigDecimal codsubentidad) throws DAOException {
		
		Integer count=0;
		try {
			Session sesion = obtenerSession();
			Criteria criteria = sesion.createCriteria(SubentidadMediadora.class);
			
			criteria.add(Restrictions.eq("id.codentidad", codentidad));
			criteria.add(Restrictions.eq("id.codsubentidad", codsubentidad));
			criteria.add(Restrictions.isNull("fechabaja"));
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			
			if (count>0) {
				return true;
			}
			return false;
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}

	public TipoMediador getTipoMediadorRGA(BigDecimal codEnt, BigDecimal codSubEntMed) {
		
		return getTipoMediador(codEnt, codSubEntMed).getTipoMediador();
	}
	
	public TipoMediadorAgro getTipoMediadorAgro(BigDecimal codEnt, BigDecimal codSubEntMed) {
		
		return getTipoMediador(codEnt, codSubEntMed).getTipoMediadorAgro();
	}
	

	/**
	 * Devuelve el mediador asociado a la entidad y subentidad mediadora indicadas
	 */
	@SuppressWarnings("unchecked")
	private TipoMediadorEntMed getTipoMediador(BigDecimal codEnt, BigDecimal codSubEntMed) {
		Session sesion = obtenerSession();
		try {			
			// Primero se busca si hay registros para la subentidad indicada
			Criteria criteria = sesion.createCriteria(TipoMediadorEntMed.class);
			criteria.add(Restrictions.le("id.codentDesde", codEnt));
			criteria.add(Restrictions.ge("id.codentHasta", codEnt));
			criteria.add(Restrictions.eq("id.codsubent", codSubEntMed));
			
			List<TipoMediadorEntMed> lista1 = criteria.list();
			
			// Si no hay registro se busca por el código que indica todas las subentidades
			if (lista1 == null || lista1.isEmpty()) {
				criteria = sesion.createCriteria(TipoMediadorEntMed.class);
				criteria.add(Restrictions.le("id.codentDesde", codEnt));
				criteria.add(Restrictions.ge("id.codentHasta", codEnt));
				criteria.add(Restrictions.eq("id.codsubent", Constants.TODAS_SUBENTMED));
				
				lista1 = criteria.list();
			}
			
			// Si hay registro se devuelve
			if (lista1 != null && !lista1.isEmpty()) {
				return ((TipoMediadorEntMed)lista1.get(0));
			}
			
			
		} catch (Exception ex) {
			logger.error("Error al obtener el tipo de mediador asociado a la entidad y subentidad mediadora indicadas", ex);
		}
		
		// No se ha encontrado registros para los parametros indicados
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String[] getColectivosUltPlanes(final BigDecimal codentidad, final BigDecimal codsubentidad)
			throws DAOException {
		String[] result;
		Session sesion = obtenerSession();		
		Criteria criteria = sesion.createCriteria(Linea.class);
		criteria.setProjection(Projections.max("codplan"));
		BigDecimal maxPlan = (BigDecimal) criteria.uniqueResult();
		ConfigAgp configAgp = (ConfigAgp) this.getObject(ConfigAgp.class, "agpNemo", PLANES_COLECTIVOS);
		BigDecimal agpValor = new BigDecimal(configAgp.getAgpValor());
		criteria = sesion.createCriteria(Colectivo.class);
		criteria.createAlias("subentidadMediadora", "subentidadMediadora");
		criteria.createAlias("linea", "linea");
		criteria.add(Restrictions.eq("subentidadMediadora.id", new SubentidadMediadoraId(codentidad, codsubentidad)));
		criteria.add(Restrictions.between("linea.codplan", maxPlan.subtract(agpValor), maxPlan));
		criteria.setProjection(Projections.distinct(Projections.property("idcolectivo")));
		result = (String[]) criteria.list().toArray(new String[] {});
		return result;
	}
}