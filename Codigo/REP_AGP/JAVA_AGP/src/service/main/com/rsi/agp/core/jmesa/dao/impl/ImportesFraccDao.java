package com.rsi.agp.core.jmesa.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IImportesFraccDao;
import com.rsi.agp.core.jmesa.filter.ImportesFraccFilter;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;

public class ImportesFraccDao extends BaseDaoHibernate implements IImportesFraccDao{

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort, final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [ImportesFraccDao] getWithFilterAndSort");
			List<Serializable> informes =(List<Serializable>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session) throws HibernateException, SQLException {							
				final ImportesFraccFilter impFraccFilter= (ImportesFraccFilter)filter;	
				Criteria criteria = null;

				criteria = session.createCriteria(ImporteFraccionamiento.class);
				criteria.createAlias("linea", "linea");
				// Filtro
				criteria = impFraccFilter.execute(criteria);
				// Ordenacion
				criteria = sort.execute(criteria);
				// Primer registro
				criteria.setFirstResult(rowStart);
				// Numero maximo de registros a mostrar
				criteria.setMaxResults(rowEnd - rowStart);
				final List<ImporteFraccionamiento> lista = criteria.list();
				return lista;
							
			}
			});
			logger.debug("end - [ImportesFraccDao] getWithFilterAndSort");
			return informes;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public int getCountWithFilter(CriteriaCommand filter) {
		logger.debug("init - [ImportesFraccDao] getCountWithFilter");
		Integer count =0;
		try{
		final ImportesFraccFilter impFraccFilter = (ImportesFraccFilter)filter;	
		//impFraccFilter.execute();
		count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(ImporteFraccionamiento.class);
							criteria.createAlias("linea", "linea");
							criteria = impFraccFilter.execute(criteria);
							return criteria.setProjection(Projections.rowCount()).uniqueResult();
						}
					});
			logger.debug("end - [ImportesFraccDao] getCountWithFilter");
		}catch(Exception e){
				e.getMessage();
			}
			return count.intValue();
	}
	
//	public List<ImporteFraccionamiento> listImportesFraccionados(ImporteFraccionamiento importeFraccionamiento) throws DAOException {
//		Session sesion = obtenerSession();
//		try {
//			
//			Criteria criteria = sesion.createCriteria(ImporteFraccionamiento.class);
//			
//			criteria.addOrder(Order.asc("linea.codlinea"));		
//			
//			if(FiltroUtils.noEstaVacio(importeFraccionamiento.getLinea())){
//				if (FiltroUtils.noEstaVacio(importeFraccionamiento.getLinea().getCodplan())) {			
//					criteria.add(Restrictions.eq("linea.codplan", importeFraccionamiento.getLinea().getCodplan()));
//				}
//				if (FiltroUtils.noEstaVacio(importeFraccionamiento.getLinea().getCodlinea())) {			
//					criteria.add(Restrictions.eq("linea.codlinea", importeFraccionamiento.getLinea().getCodlinea()));
//				}
//			}
//			
//			if (FiltroUtils.noEstaVacio(importeFraccionamiento.getImporte())) {
//				criteria.add(Restrictions.eq("importe", importeFraccionamiento.getImporte()));
//			}
//			
//			if (FiltroUtils.noEstaVacio(importeFraccionamiento.getTipo())) {
//				criteria.add(Restrictions.eq("tipo", importeFraccionamiento.getTipo()));
//			}
//			
//			if (FiltroUtils.noEstaVacio(importeFraccionamiento.getPctRecargo())) {
//				criteria.add(Restrictions.eq("pctRecargo", importeFraccionamiento.getPctRecargo()));
//			}
//
//			return criteria.list();
//			
//		} catch (Exception e) {
//			logger.info("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
//			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
//		}		
//	}
//	
//	public List<ImporteFraccionamiento> getAll()throws DAOException {
//		Session sesion = obtenerSession();
//		try {
//			
//			Criteria criteria = sesion.createCriteria(ImporteFraccionamiento.class);			
//			
//			return criteria.list();
//			
//		} catch (Exception ex) {
//			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
//			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
//		}
//	}
	
//	public boolean existeImporteFracc (Linea linea){
//		try {		
//
//			BigDecimal resultado;
//			Session session = obtenerSession();
//			
//	 		String sql = "select count(*) from tb_importes_fraccionamiento d " + "where d.linea = " + linea ;
//			
//	 		List list = session.createSQLQuery(sql).list();
//			resultado = (BigDecimal) list.get(0);
//			
//			if ( resultado.intValue() == 0) 
//				return false;
//			else
//				return true;
//		
//		} catch (Exception e) {
//			return false;
//		}
//	}
	
//	/**
//	 * Obtiene el registro de importe de fraccionamiento SAECA para el plan/línea indicados.
//	 * @param lineaseguroid
//	 * @return
//	 */
//	public ImporteFraccionamiento getImporteFraccionamientoSAECA (Long lineaseguroid) {
//		
//		try {
//			Session sesion = obtenerSession();
//			
//			Criteria criteria = sesion.createCriteria(ImporteFraccionamiento.class);
//			criteria.createAlias("linea", "linea");
//			criteria.add(Restrictions.eq("tipo", Constants.FINANCIACION_SAECA));
//			criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
//			
//			List<ImporteFraccionamiento> list = criteria.list();
//			if (list != null && !list.isEmpty()) {
//				return list.get(0);
//			}
//		}
//		catch (Exception e) {
//			logger.error("Ha ocurrido un error al obtener el importe de fraccionamiento SAECA para el lineaseguroid " + lineaseguroid, e);
//		}
//		
//		return null;
//	}

	@SuppressWarnings("unchecked")
	public ImporteFraccionamiento obtenerImporteFraccionamiento(Long lineaSeguroId,SubentidadMediadora sm) {
		ImporteFraccionamiento impFrac=null;
		List<ImporteFraccionamiento> lista=null;
	//	lista = getObjects(ImporteFraccionamiento.class, "lineaseguroid", lineaSeguroId);
		String[] param = new String[3];
		Object[] valores= new Object[3];
		param[0]="linea.lineaseguroid";
		param[1]="subentidadMediadora.id.codentidad";
		param[2]="subentidadMediadora.id.codsubentidad";
		valores[0]=lineaSeguroId;
		valores[1]=sm.getId().getCodentidad();
		valores[2]=sm.getId().getCodsubentidad();
	
		try {
			lista=findFiltered(ImporteFraccionamiento.class, param, valores, null);
			if(null==lista || lista.size()==0){
				lista=obtenerImporteFraccParaLinea(ImporteFraccionamiento.class,lineaSeguroId);
			}
					
		} catch (DAOException e) {
			logger.error("Ha ocurrido un error al obtener el importe de fraccionamiento para el lineaseguroid " + lineaSeguroId + "y la subentidad mediadora " +sm.getId() , e);
		}
		if (lista != null && lista.size()>0) {
			impFrac =lista.get(0);
		}
		
		return impFrac;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ImporteFraccionamiento> obtenerImporteFraccParaLinea (Class clase,Long lineaSeguroId) {

		Session session = obtenerSession(); 		
		Criteria criteria=null;
		criteria = session.createCriteria(clase);
		criteria = criteria.add(Restrictions.eq("linea.lineaseguroid", lineaSeguroId));
		criteria=criteria.add(Restrictions.isNull("subentidadMediadora.id.codentidad"))	;
		criteria=criteria.add(Restrictions.isNull("subentidadMediadora.id.codsubentidad"))	;
		return criteria.list();
	}
}