package com.rsi.agp.dao.models.poliza;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Linea;


@SuppressWarnings({"unchecked", "rawtypes"})
public class LineaDao extends BaseDaoHibernate implements ILineaDao {
	
	/**
	 * obtiene todas las lineas
	 */
	public List<Linea> getAll()throws DAOException {
		Session sesion = obtenerSession();
		try {
			
			Criteria criteria = sesion.createCriteria(Linea.class);
			
			return criteria.list();
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	/**
	 * obtiene la linea seguro id segun codlinea y codplan
	 * @param codLinea
	 * @param codPlan
	 */
	public Long getLineaSeguroId(BigDecimal codLinea, BigDecimal codPlan)throws DAOException {

		Long result = null;
    	LineasFiltro filtro = new LineasFiltro(codPlan, codLinea);
    	List<Linea> idLineaList = this.getObjects(filtro);
    	
    	try{
    	
	    	if(idLineaList.size() > 0){
	    	    result = idLineaList.get(0).getLineaseguroid();
	    	}else{
	    		result = null;
	    	}
    	
    	}catch(Exception ex){
    		logger.info("Se ha producido un error al recuperar la lineaseguroid: " + ex.getMessage());
			throw new DAOException("Se ha producido un error al recuperar la lineaseguroid", ex);
    	}
		
		return result;
	}

	/**
	 * obtiene la linea seguro id segun codlinea y codplan
	 * @param codLinea
	 * @param codPlan
	 */
	public Linea getLinea(BigDecimal codLinea, BigDecimal codPlan)throws DAOException {

		Linea result = null;
    	LineasFiltro filtro = new LineasFiltro(codPlan, codLinea);
    	List<Linea> idLineaList = this.getObjects(filtro);
    	
    	try{
    	
	    	if(idLineaList.size() > 0){
	    	    result = idLineaList.get(0);
	    	}else{
	    		result = null;
	    	}
    	}catch(Exception ex){
    		logger.info("Se ha producido un error al recuperar la linea: " + ex.getMessage());
			throw new DAOException("Se ha producido un error al recuperar la linea", ex);
    	}
		
		return result;
	}

	public boolean existeLinea(BigDecimal codplan, BigDecimal codlinea) {
		
		List<Linea> lstLinea = new ArrayList<Linea>();
		Session session = obtenerSession();
		boolean existeLinea = false;
		
		try{
				Criteria criteria = session.createCriteria(Linea.class);
				// Alias
				// criteria.createAlias("linea", "linea");
				// criteria.createAlias("cultivo", "cultivo");
				criteria.add(Restrictions.eq("codplan", codplan));
				criteria.add(Restrictions.eq("codlinea", codlinea));
				
				lstLinea = criteria.list();
				
				if (!lstLinea.isEmpty()){
					existeLinea = true;
				}
			
		} catch (Exception ex) {
			logger.error("[SobreprecioDao] lstSobreprecio - Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return existeLinea;
	}		


	public boolean existePlan(BigDecimal codplan) throws DAOException   {
		
		boolean existePlan = false;
		try{
			Session session = obtenerSession();
			
			String sql= "select distinct(codplan) from TB_LINEAS WHERE CODPLAN = " + codplan;
			List list = session.createSQLQuery(sql).list();
			int numElem = ( (BigDecimal)list.get(0) ).intValue();
			
			if (numElem > 0)
				existePlan = true;
			
		} catch (Exception ex) {
			logger.error("Se ha producido un error en la BBDD: " + ex.getMessage());
		}
		return existePlan;
	}		

	/** 
	 * DAA 30/10/2013
	 * Metodo para insertar en la tabla de lineas la linea generica para el plan indicado 
	 */
	public Linea insertaLineaGenerica(BigDecimal codplan) {
	
		Session session = obtenerSession();
		Linea linea = new Linea();
		
		try{
			linea.setCodlinea(Constants.CODLINEA_GENERICA);
			linea.setCodplan(codplan);
			linea.setNomlinea("Todas las lineas");
			linea.setEstado("INVALIDO");
			linea.setActivo("NO");
			linea.setMaxpolizasppal(new BigDecimal(1));
			linea.setDiccionarioDatos(null);
			
			logger.debug("Insertamos la nueva línea en la base de datos");
			session.saveOrUpdate(linea);
			
		} catch (HibernateException e) {
			logger.error("Error al crear la linea ", e);
		}
		return linea;
	}
	

	@Override
	public boolean noExisteLineaMayorPlanActivo(BigDecimal codLinea, BigDecimal codPlan) throws DAOException {
		
		Session session = obtenerSession();
		try{
			String sql= "select * from tb_lineas where activo = 'SI' and codplan > "+codPlan+" and codlinea = "+codLinea;
			logger.debug("comprobarAltaAnexo - " + sql);
			List list = session.createSQLQuery(sql).list();
			
			// si hay registros no puede dar de alta ya que hay un plan linea mas nuevo
			return !(list.size()>0);
		}
		catch(Exception excepcion){
			logger.error("Error al comprobar el plan/linea de la poliza asociada",excepcion);
			throw new DAOException("EError al comprobar el plan/linea de la poliza asociada",excepcion);
		}
		
	}
	
	@Override
	public boolean esLineaGanado(final Long lineaseguroId) throws DAOException {
		boolean result = false;
		try {
			Linea linea = (Linea) this.get(Linea.class, lineaseguroId);
			result = Long.valueOf(1).equals(linea.getEsLineaGanadoCount());
		} catch (Exception e) {
			throw new DAOException("Se ha producido un error en esLineaGanado", e);
		}
		return result;
	}	
	
	@Override
	public Linea getLinea(String lineaseguroid) {
		Linea linea = (Linea) this.getObject(Linea.class, Long.parseLong(lineaseguroid));
		return linea;
	}
	
}
