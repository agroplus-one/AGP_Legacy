package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;
import com.rsi.agp.dao.tables.poliza.Linea;

public class CultivosSubEntidadesDao extends BaseDaoHibernate implements ICultivosSubEntidadesDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CultivosSubentidades> listCultivosSubentidades(CultivosSubentidades cultivosSubentidadesBean, boolean pctGeneral) throws DAOException {
		logger.debug("init - listCultivosSubentidades");
		List<CultivosSubentidades> result;
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(CultivosSubentidades.class);
			criteria.createAlias("linea", "linea");
			criteria.addOrder(Order.asc("subentidadMediadora.id.codentidad"));
			criteria.addOrder(Order.asc("subentidadMediadora.id.codsubentidad"));
			criteria.addOrder(Order.desc("linea.codplan"));
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getSubentidadMediadora())){
				if(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodentidad()!= null){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodentidad()));
				}
				if(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()!= null){
					criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad()));
				}
			}
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea())){
				if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea().getCodlinea())){
					criteria.add(Restrictions.eq("linea.codlinea", cultivosSubentidadesBean.getLinea().getCodlinea()));
				}
				if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea().getCodplan())){
					criteria.add(Restrictions.eq("linea.codplan", cultivosSubentidadesBean.getLinea().getCodplan()));
				}
			}
			if(!StringUtils.nullToString(cultivosSubentidadesBean.getPctmediador()).equals("")){
				criteria.add(Restrictions.eq("pctmediador", cultivosSubentidadesBean.getPctmediador()));
			}
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getId())){
				criteria.add(Restrictions.eq("id", cultivosSubentidadesBean.getId()));
			}
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getFecEfecto())){
				criteria.add(Restrictions.eq("fecEfecto", cultivosSubentidadesBean.getFecEfecto()));
			}
			result = criteria.list();
			if (pctGeneral) {
				BigDecimal pctgeneral;
				BigDecimal pctentidadCalculado;
				BigDecimal pctmediadorCalculado;
				for (CultivosSubentidades cse : result) {
					pctgeneral = getPctGeneral(cse);
					pctentidadCalculado = NumberUtils.calcularPorcentajes(cse.getPctentidad(), pctgeneral);
					pctmediadorCalculado = pctgeneral.subtract(pctentidadCalculado);
					cse.setPctentidadCalculado(pctentidadCalculado);
					cse.setPctmediadorCalculado(pctmediadorCalculado);
				}
			}
			logger.debug("end - listCultivosSubentidades");
			return result;
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	} 
	
	@Override
	public Linea getLineaseguroId(BigDecimal codlinea, BigDecimal codplan) throws DAOException {
		logger.debug("init - getLineaseguroId");
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(Linea.class);
			criteria.add(Restrictions.eq("codlinea", codlinea));
			criteria.add(Restrictions.eq("codplan", codplan));
			
			logger.debug("end -  getLineaseguroId");
			return (Linea)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public SubentidadMediadora getSubentidadMediadora(SubentidadMediadora subentidadMediadora) throws DAOException {
		logger.debug("init - getSubentidadMediadora");
		Session session = obtenerSession();
		try {
			Criteria criteria =	session.createCriteria(SubentidadMediadora.class);
			criteria.add(Restrictions.eq("id.codentidad", subentidadMediadora.getId().getCodentidad()));
			criteria.add(Restrictions.eq("id.codsubentidad", subentidadMediadora.getId().getCodsubentidad()));
			
			logger.debug("end -  getSubentidadMediadora");
			return (SubentidadMediadora)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public Integer existeRegistro(CultivosSubentidades cultivosSubentidadesBean)throws DAOException {
		logger.debug("init - existeRegistro");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosSubentidades.class);
			criteria.createAlias("linea", "linea");
			
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea())){
				if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea().getCodlinea()))
					criteria.add(Restrictions.eq("linea.codlinea", cultivosSubentidadesBean.getLinea().getCodlinea()));
				if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getLinea().getCodplan()))
					criteria.add(Restrictions.eq("linea.codplan", cultivosSubentidadesBean.getLinea().getCodplan()));
			}
			
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getSubentidadMediadora())){
				if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getSubentidadMediadora().getId())){
					if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodentidad())){
						criteria.add(
								Restrictions.eq("subentidadMediadora.id.codentidad", 
										cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodentidad())
						);
					}
					if(cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad() != null){
						criteria.add(
								Restrictions.eq("subentidadMediadora.id.codsubentidad", 
										cultivosSubentidadesBean.getSubentidadMediadora().getId().getCodsubentidad())
						);
					}
				}
				
			}
			
			if(FiltroUtils.noEstaVacio(cultivosSubentidadesBean.getId()))
				criteria.add(Restrictions.ne("id", cultivosSubentidadesBean.getId()));
			
			criteria.add(Restrictions.isNull("fecBaja"));
			criteria.setProjection(Projections.rowCount());
			
			logger.debug("end - existeRegistro");
			return (Integer)criteria.uniqueResult();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	private BigDecimal getPctGeneral(CultivosSubentidades cse)throws DAOException {
		logger.debug("init - getPctGeneral");
		BigDecimal result = BigDecimal.ZERO;
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.addOrder(Order.desc("fechamodificacion"));
			criteria.addOrder(Order.desc("id"));
			
			criteria.add(Restrictions.eq("linea.lineaseguroid", cse.getLinea().getLineaseguroid()));
			
			criteria.setProjection(Projections.property("pctgeneralentidad"));
			
			criteria.setFirstResult(0);
			criteria.setMaxResults(1);
			
			result = (BigDecimal) criteria.uniqueResult();
			
			//Si no se encuentra ningun dato, se obtienen de los datos genericos de la linea 999
			if (result == null){
				criteria = session.createCriteria(CultivosEntidades.class);
				criteria.createAlias("linea", "linea");
				
				criteria.addOrder(Order.desc("fechamodificacion"));
				criteria.addOrder(Order.desc("id"));
				
				criteria.add(Restrictions.eq("linea.codlinea", new BigDecimal("999")));
				criteria.add(Restrictions.eq("linea.codplan", cse.getLinea().getCodplan()));
				
				criteria.setProjection(Projections.property("pctgeneralentidad"));
				
				criteria.setFirstResult(0);
				criteria.setMaxResults(1);
				
				result = (BigDecimal) criteria.uniqueResult();
				
				if (result == null) {
					result = BigDecimal.ZERO;
				}
			} 			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage()); 
		}
		return result;
	}

	@Override
	public BigDecimal getPlanActual() throws DAOException {
		logger.debug("init - getPlanActual");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias("linea", "linea");			
			criteria.addOrder(Order.desc("linea.codplan"));				
			
			criteria.setProjection(Projections.property("linea.codplan"));			
			
			return (BigDecimal) criteria.list().get(0);
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CultivosSubentidadesHistorico> consultaHistorico(
			Long id) throws DAOException {
		logger.debug("init - consultaHistorico");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosSubentidadesHistorico.class);
			criteria.createAlias("cultivosSubentidades","cultivosSubentidades");
			criteria.addOrder(Order.asc("fechamodificacion"));				
			
			criteria.add(Restrictions.eq("cultivosSubentidades.id", id));
			
			
			return (ArrayList<CultivosSubentidadesHistorico>) criteria.list();
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

	@Override
	public BigDecimal getLineaActual() throws DAOException {
		logger.debug("init - getPlanActual");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(CultivosEntidades.class);
			criteria.createAlias("linea", "linea");			
			criteria.addOrder(Order.desc("linea.codlinea"));				
			
			criteria.setProjection(Projections.property("linea.codlinea"));			
			
			return (BigDecimal) criteria.list().get(0);
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
		}
	}

}
	

