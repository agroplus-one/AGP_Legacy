package com.rsi.agp.dao.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.Usuario;

@SuppressWarnings("rawtypes")
public class BaseDaoHibernate extends HibernateDaoSupport implements GenericDao {

	protected IDatabaseManager databaseManager;
	
	protected Session obtenerSession(){
		Session session = this.getSessionFactory().getCurrentSession();
		session.setFlushMode(FlushMode.COMMIT);
		
		return session;
	}
	
	public Transaction beginTransaction (){
		return this.obtenerSession().beginTransaction();
	}
	
	
	public Object saveOrUpdate(Object entity) throws DAOException {
	    
		Session session = obtenerSession(); 
		try{
		    
			session.saveOrUpdate(entity);
			return entity;
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		}
		finally{
		}
	}
	
	public void saveOrUpdateList(List listaEntidades) throws DAOException{
		
		Session session = obtenerSession(); 
				
		try{
		    
			for(Object entidad:listaEntidades)
				session.saveOrUpdate(entidad);
			
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el guardado de una de las entidades", ex);
		}
		finally{
		}
	}
	
	public void delete(Class clazz, Serializable id) throws DAOException {
	  
		Session session = obtenerSession(); 
				
		try{
		    
			Object entidad = session.get(clazz, id);
			session.delete(entidad);
			
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el borrado de la entidad", ex);
		}
		finally{
		}
	}
	
	public void delete(Object entidad) throws DAOException {
		Session session = obtenerSession(); 
				
		try{
			session.delete(entidad);
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el borrado de la entidad", ex);
		}
		finally{
		}
	}
	
	
	public void deleteAll(Collection lista) throws DAOException {
		  
		try{
		    
			this.getHibernateTemplate().deleteAll(lista);
			
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el borrado de las entidades", ex);
		}
		finally{
		}
	}
	
	public Object get(Class clazz, Serializable id) throws DAOException {
	   
		Session session = obtenerSession(); 
		
		try{
		    return session.get(clazz, id);
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso de la entidad", ex);
		}
		finally{
		}
	}
	
	
	
	/**
	 * Elimina un objeto de la BD (DELETE)
	 * @param clazz --> tipo entidad 
	 * @param id --> PK en la BD
	 */
	public void removeObject(Class clazz, Serializable id) {
	    
		Session session = obtenerSession(); 
		
		try{
			
		    session.delete(session.get(clazz, id));
		}
		catch(Exception ex){
		}
		finally{
		}
	}

	/**
	 * Obtiene un objeto de la BD (GET)
	 * @param clazz --> tipo entidad 
	 * @param id --> PK en la BD
	 */
	public Object getObject(Class clazz, Serializable id) {
	    
		Session session = obtenerSession(); 
		Object object = null;
				
		try{
		    object = session.get(clazz, id);
		}
		catch(Exception ex){
		}
		finally{
		}
		return object;
	}

	
	public Object getObject(Class clazz, String property, Object value) {
		    
			Session session = obtenerSession(); 
			Object object = null;
			java.util.List lista=null; 
					
			try{
				org.hibernate.Criteria criteria=session.createCriteria(clazz).add(Restrictions.eq(property, value));
			    lista = criteria.list();			    
			    if(null!=lista && lista.size()>0) {
			    	object = lista.get(0);
			    }	
			    // C�digo para convertir el proxy del objeto en el objeto
				// Hibernate est� utilizando su m�todo load en lugar del get. Load devuelve un  proxy del objeto.
			    if (HibernateProxy.class.isAssignableFrom(object.getClass())) {
				      Hibernate.initialize(object);
				      HibernateProxy proxy=(HibernateProxy)object;
				      object= proxy.getHibernateLazyInitializer().getImplementation();
				      Object obj=clazz.newInstance();
				      obj=object;
				      return obj;
				}else {
					return object;
				}
			    
			}
			catch(Exception ex){				
			}
			finally{
			}
			return object;
	}

	
	public List getObjectsBySQLQuery(String sqlQuery) {
	    
		Session session = obtenerSession(); 
		
		try{
		    return session.createSQLQuery(sqlQuery).list();
		}
		catch(Exception ex){
		}
		finally{
		}
		return null;
	}
	
	public List getObjects(Filter filter) { 
		
		Session session = obtenerSession(); 
		
		return filter.getCriteria(session).list();
	}


	public List getObjects(Class clazz, String property, Object value) {
		
		Session session = obtenerSession(); 
		
		if (property == null || value == null)
			return session.createCriteria(clazz).list();
		else{
			return session.createCriteria(clazz).add(Restrictions.eq(property, value)).list();
		}
				
	}	


	public Integer getNumObjects(Class clazz) {
		
		Session session = obtenerSession(); 
		
		return Integer.parseInt(session.createCriteria(
				clazz).setProjection(Projections.rowCount()).uniqueResult().toString());
	}


	public Integer getNumObjects(Filter filter) {
		Session session = obtenerSession(); 
		
		return Integer.parseInt(filter.getCriteria(
				session).setProjection(
				Projections.rowCount()).uniqueResult().toString());
	}
	
	public List findAll (Class clase, String orden) throws DAOException {
		
		Session session = obtenerSession(); 
		
		try{
		
			Criteria criteria = session.createCriteria(clase);
			if(orden!=null)
				criteria=criteria.addOrder(Order.asc(orden).ignoreCase());
			return criteria.list();

		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}finally {
		}
	}

	public List findAll (Class clase) throws DAOException {
		return findAll(clase,null);
	}
	
	public List findFiltered (Class clase, String parametro, Object valor) throws DAOException {
		return findFiltered(clase,new String[]{parametro}, new Object[]{valor},null);
	}
	
	public List findFiltered(Object bean, String[] campos, String orden) throws DAOException {

		Session session = obtenerSession(); 	
		try{
		
			Criteria criteria = session.createCriteria(bean.getClass());
		
			for(String campo:campos){
				boolean esCampoTexto = campo.endsWith("%");
				
				if(esCampoTexto)
					 campo = campo.substring(0,campo.length()-1);
				
				Object value = HTMLUtils.getProperty(bean, campo);
				
				if(value != null){
				
					if(esCampoTexto)
						criteria.add(Restrictions.ilike(campo, value.toString(), MatchMode.ANYWHERE));
					else 
						criteria.add(Restrictions.eq(campo, value));
				}
			}
			
			if(orden != null)
				criteria.addOrder(Order.asc(orden));
			
			return criteria.list();
		
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}finally {
		}
		
	}
	public List findFiltered(Object bean, String[] campos) throws DAOException {
	
		return findFiltered(bean, campos, null);
	}
	
	public List findFiltered (Class clase, String [] parametros, Object [] valores, String orden) throws DAOException {

		Session session = obtenerSession(); 		
		
		try{		
			
			Criteria criteria = session.createCriteria(clase);
			
			for(int i=0;i<parametros.length;i++){
				if(valores[i] != null)
					if(!(valores[i] instanceof String) || !((String)valores[i]).isEmpty())
						criteria = criteria.add(Restrictions.eq(parametros[i], valores[i]));
			}
			
			if(orden != null)
				criteria = criteria.addOrder(Order.asc(orden).ignoreCase());
			
			return criteria.list();
		
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}finally {
		}
	}

	public List findFiltered (Class clase, String parametro, Object valor, String orden) throws DAOException {
		return findFiltered(clase,new String[]{parametro}, new Object[]{valor}, orden);
	}
	
	public Object saveOrUpdateFacturacion(Object entity, Usuario usuario) throws DAOException {
		
		Session session = obtenerSession(); 
		try{
			session.saveOrUpdate(entity);
			this.executeFacturacion(usuario,"A");
			return entity;
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el guardado de la entidad", ex);
		}
		finally{
		}
	}
	//Facturacion
	public void deleteFacturacion(Object entidad,Usuario usuario) throws DAOException {
		Session session = obtenerSession(); 
				
		try{
			session.delete(entidad);
			this.executeFacturacion(usuario,"A");
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el borrado de la entidad", ex);
		}
		finally{
		}
	}
	public void removeObjectFacturacion(Class clazz, Serializable id,Usuario usuario) {
	    
		Session session = obtenerSession(); 
		
		try{
			session.delete(session.get(clazz, id));
			this.executeFacturacion(usuario,"A");
		}
		catch(Exception ex){
		}
		finally{
		}
	}
	public void callFacturacion(Usuario usuario,String tipo) throws DAOException{
		
		try{
			this.executeFacturacion(usuario, tipo);
		
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el guardado de la entidad de facturacion", ex);
		}
		finally{
		}
	}
	
	/**
	 * metodo que llama al pl de facturacion 
	 * @param usuario
	 * @param C => Consultas
			  A => Actualizaciones
			  I => Impresiones
	 */
	private void executeFacturacion (Usuario usuario,String tipo){
		String procedure = "o02agpe0.pq_facturacion.pr_facturacion(P_CODENTIDAD IN VARCHAR2,P_CODOFICINA IN VARCHAR2, P_CODUSUARIO IN VARCHAR2, P_TIPO IN VARCHAR2)";			
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {	
			
			if (usuario!= null){
				String codEntidad = usuario.getOficina().getId().getCodentidad().toString();
				String codOficina = usuario.getOficina().getId().getCodoficina().toString();
				String codUsu = usuario.getCodusuario();
				parametros.put("P_CODENTIDAD",codEntidad );
				parametros.put("P_CODOFICINA", codOficina);
				parametros.put("P_CODUSUARIO", codUsu);
				parametros.put("P_TIPO", tipo);
				logger.debug("llamada al PL: pq_facturacion.pr_facturacion("+codEntidad+","+codOficina+","+codUsu+","+tipo+")");
			
				databaseManager.executeStoreProc(procedure, parametros);
			}
		} catch (Exception e) {
			logger.error("error en la llamada al PL llamada al PL: pq_facturacion.pr_facturacion()",e);
		}
	}

	//FIN FACTURACION
	
	/**
	 * Elimina el objeto de la sesion de hibernate para recargarlo
	 */
	public void evict(Object entity){
		Session session = obtenerSession();
		session.evict(entity);
	}
	public IDatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	public void setDatabaseManager(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	
	public void creaRestrictionsCriteria(Criteria criteria, String campoFiltro,
			String restriccion, Object valor) {
		//OJO, ir implementando las que se necesiten
		
		if(restriccion.compareTo("gt")==0) {//mayor que
			criteria.add(Restrictions.gt(campoFiltro, valor));
		}
		if(restriccion.compareTo("ge")==0) {//mayor o igual
			criteria.add(Restrictions.ge(campoFiltro, valor));
		}
		if(restriccion.compareTo("lt")==0) {//menor que 
			criteria.add(Restrictions.lt(campoFiltro, valor));
		}
		if(restriccion.compareTo("le")==0) {//menor o igual 
			criteria.add(Restrictions.le(campoFiltro, valor));
		}
		if(restriccion.compareTo("eq")==0) {//igual
			criteria.add(Restrictions.eq(campoFiltro, valor));
		}
		if(restriccion.compareTo("ne")==0) {//diferente que
			criteria.add(Restrictions.ne(campoFiltro, valor));
		}		
	}
	
	public void creaRestrictionsCriteriaIN(Criteria criteria,String campoFiltro, String restriccion, Object[] valor) {
		//para restricciones de coleccion de objetos como in
		if(restriccion.compareTo("in")==0) {//igual
			criteria.add(Restrictions.in(campoFiltro, valor));
		}
	}
	
	public BigDecimal  getSecuencia(String nombreSecuencia) throws DAOException {
		try {
	
		Session session = obtenerSession();	
		String sql = "select o02agpe0."+nombreSecuencia+".nextval from dual";
		BigDecimal secuencia = (BigDecimal)session.createSQLQuery(sql).uniqueResult();
		return secuencia;
		}catch (Exception e) {
			throw new DAOException("Error al crear la secuencia "+nombreSecuencia, e);
		}
	}	
}