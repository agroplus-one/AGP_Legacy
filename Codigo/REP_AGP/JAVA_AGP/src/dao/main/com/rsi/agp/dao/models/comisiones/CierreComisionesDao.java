package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CierreFilter;
import com.rsi.agp.core.jmesa.sort.CierreSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.comisiones.Cierre;
import com.rsi.agp.dao.tables.comisiones.Fase;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.ReportCierre;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;


public class CierreComisionesDao extends BaseDaoHibernate implements ICierreComisionesDao {
	
	private static final Log logger = LogFactory.getLog(CierreComisionesDao.class);
	private IFaseDao faseDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Fase> listFasesSinCerrar(Date fechaCierre) throws DAOException {
		logger.debug("init - listFasesSinCerrar");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Fase.class);
			
			criteria.add(Restrictions.isNull("cierre"));
			criteria.add(Restrictions.le("fechaemision", fechaCierre));
			logger.debug("end - listFasesSinCerrar");
			return (List<Fase>)criteria.list();
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FaseUnificado> listFasesUnifSinCerrar(Date fechaCierre,Long idCierre)
			throws DAOException {
		logger.debug("init - listFasesUnifSinCerrar");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(FaseUnificado.class);
			if (idCierre != null){
				Disjunction or = Restrictions.disjunction();
				or.add(Restrictions.isNull("cierre"));
				or.add(Restrictions.eq("cierre.id", idCierre));
				criteria.add(or);
			}else{
				criteria.add(Restrictions.isNull("cierre"));
			}
						
			logger.debug("end - listFasesUnifSinCerrar");
			return (List<FaseUnificado>)criteria.list();
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Fichero> getFasesSinCierre() throws DAOException {
		logger.debug("init - getFasesSinCierre");
		Session session = obtenerSession();
		
		try {
			
			// Fases de ficheros de comisiones antiguos
			Criteria criteria = session.createCriteria(Fichero.class);
			criteria.createAlias("fase", "fase");
			criteria.add(Restrictions.isNull("fase.cierre"));
			criteria.addOrder(Order.asc("fase.fase"));
			criteria.addOrder(Order.asc("tipofichero"));
			
			List<Fichero> ficheros = criteria.list();
			
			// Fases de ficheros de comisiones 2015
			String sql = " select nombre_fichero,fase," +
						 " REPLACE(REPLACE(tipo_Fichero, 'C', '1'),'I','2')," +
						 " fa.fecha_emision_recibo" +
					     " from TB_COMS_UNIF_FICHERO f inner join TB_COMS_UNIF_FASE fa on f.ID=fa.IDFICHERO " +
					     " where fa.IDCIERRE is null ";
			List<Object> list = session.createSQLQuery(sql).list();
			for (int i=0; i<list.size(); i++){
				Fichero f = new Fichero ();
				Object[] o = (Object[]) list.get(i);
				f.setNombrefichero((String) o[0]);
				f.setFase(new Fase());
				f.getFase().setFase(((BigDecimal) o[1]).toString());
				f.getFase().setFechaemision((Date) o[3]);
				f.setTipofichero(o[2].toString().charAt(0));				
				ficheros.add(f);
			}
			
			logger.debug("end - getFasesSinCierre");
			return ficheros;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
		
	}

	@Override
	public Integer periodoCerrado(Date fechacierre) throws DAOException {
		logger.debug("init - periodoCerrado");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Fase.class);
			criteria.createAlias("cierre", "cierre");
			
			criteria.add(Restrictions.eq("cierre.fechacierre", fechacierre));			
			criteria.setProjection(Projections.rowCount());
			
			logger.debug("end - periodoCerrado");
			return (Integer)criteria.uniqueResult();
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Fichero> ficherosNoAceptados(Date fecha) throws DAOException {
		logger.debug("init - ficherosNoAceptados");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Fichero.class);
			criteria.createAlias("fase","fase");
			
			Disjunction d = Restrictions.disjunction();
			d.add( Restrictions.disjunction()
						  .add(Restrictions.ge("fase.fechaemision", fecha))
						  .add(Restrictions.isNull("fase.fechaemision")));
			
			criteria.add(Restrictions.isNull("fase.cierre"));
			criteria.add(Restrictions.isNull("fechaaceptacion"));
			
			logger.debug("end - ficherosNoAceptados");
			return criteria.list();
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}	
	@SuppressWarnings("unchecked")
	@Override
	public List<FaseUnificado> ficherosUnificadosNoAceptados(Date fecha) throws DAOException {
		logger.debug("init - ficherosUnificadosNoAceptados");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(FaseUnificado.class);
			criteria.createAlias("fichero","fichero");
			
			criteria.add(Restrictions.isNull("cierre"));
			criteria.add(Restrictions.isNull("fichero.fechaAceptacion"));
			
			logger.debug("init - ficherosUnificadosNoAceptados");
			return criteria.list();
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}	

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportCierre> getListaInformesGenerados(final Long idCierre) throws DAOException {
		logger.debug("init - getListaInformesGenerados");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(ReportCierre.class);
			criteria.createAlias("cierre", "cierre");
			
			criteria.add(Restrictions.eq("cierre.id", idCierre));
			
			logger.debug("end - getListaInformesGenerados");
			return criteria.list();
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}	

	@Override
	public ReportCierre getContenidoInforme(Long idInforme) throws DAOException {
		Session session = obtenerSession();
		ReportCierre ficheroCierre = null;
		try {
			Criteria criteria = session.createCriteria(ReportCierre.class);
			criteria.add(Restrictions.eq("id", idInforme));
			
			if ( criteria.list().size() > 0)
			{				
				ficheroCierre = (ReportCierre)criteria.list().get(0);
			}			
			return ficheroCierre;	
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error al obtener los datos del informe " + idInforme, ex);	
		}		
		
	}
	
	@Override
	public ReportCierre getInformeById(Long idInforme) throws DAOException {
		logger.debug("init - getInformeById");
		Session session = obtenerSession();
		ReportCierre fichero = new ReportCierre();
		try {
			
			Criteria criteria = session.createCriteria(ReportCierre.class);
			criteria.add(Restrictions.eq("id", idInforme));			
			if (criteria.list().size() > 0)
			{
				fichero = (ReportCierre)criteria.list().get(0);
			}
			logger.debug("end - getInformeById");
			return fichero;
			
		}catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<Fichero> getFicheroByIdCierre(Long idCierre) throws DAOException {
		logger.debug("init - ficherosNoAceptados");
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(Fichero.class);
			criteria.createAlias("fase","fase");
			criteria.createAlias("fase.cierre","cierre");
			criteria.add(Restrictions.eq("cierre.id", idCierre));		
			
			logger.debug("init - ficherosNoAceptados");
			return criteria.list();
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Cierre getCierreByFecha(Date fechaCierre) throws DAOException {
		logger.debug("init - getCierreByFecha");
		Session session = obtenerSession();
		Cierre cierre = null;
		List aux = new ArrayList();
		try {
			
			Criteria criteria = session.createCriteria(Cierre.class);
			
			criteria.add(Restrictions.eq("fechacierre", fechaCierre));		
			criteria.addOrder(Order.desc("id"));
			aux = criteria.list();
			if (aux.size() > 0){
				cierre = (Cierre) aux.get(0);
			}
			
			logger.debug("fin - getCierreByFecha");
			
			
			return cierre;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}	

	public int getCierreCountWithFilter(final CierreFilter filter) {
		logger
		.debug("init - [CierreComisionesDao] getCierreCountWithFilter");
		Integer count = (Integer) getHibernateTemplate().execute(
		new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Criteria criteria = session
						.createCriteria(Cierre.class);
				// Filtro
				criteria = filter.execute(criteria);
				criteria.setProjection(Projections.rowCount())
						.uniqueResult();
				return criteria.uniqueResult();
			}
		});
		logger.debug("end - [CierreComisionesDao] getCierreCountWithFilter");
		return count.intValue();
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Cierre> getCierreWithFilterAndSort(final CierreFilter filter,
			final CierreSort sort, final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger
					.debug("init - [CierreComisionesDao] getCierreWithFilterAndSort");
			List<Cierre> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session
									.createCriteria(Cierre.class);
							// Filtro
							criteria = filter.execute(criteria);
							// OrdenaciÃ³n
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// NÃºmero mÃ¡ximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							return criteria.list();
						}
					});
			logger
					.debug("end - [CierreComisionesDao] getCierreWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error durante el acceso a la base de datos"
							+ e.getMessage());
		}
	}


	/**
	 * Obtiene el id del cierre más reciente
	 */
	@SuppressWarnings("rawtypes")
	public Long obtenerIdCierreMasReciente(){
		
		Long idCierre = null;
		logger.debug("init - [CierreComisionesDao] obtenerIdCierreMasReciente");
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Cierre.class);
		criteria.addOrder(Order.desc("fechacierre"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		List listaAux = criteria.list();
		
		if(listaAux!=null && listaAux.size()>0){
			idCierre = ((Cierre)listaAux.get(0)).getId();
		}
		logger.debug("end - [CierreComisionesDao] obtenerIdCierreMasReciente");
		return idCierre;
	}
	
	/**
	 * Borra un determinado cierre dado su id. No borra las fases. Sólo las desreferencia.
	 * @param idCierre
	 * @throws DAOException
	 */
	public void borrarCierrePorId(Cierre cierre) throws DAOException{
		
		faseDao.borrarReferenciasCierre(cierre);
		this.delete(Cierre.class, cierre.getId());
	}
	
	/**
	 * Borra los informes de comisiones dado un idCierre.
	 * No se hace distinción entre tablas antiguas y 2015+, ya que un mismo cierre puede haber escrito en ambas tablas.
	 * @param idCierre
	 * @return Operación realizada con éxito
	 */
	public boolean borrarInformesComisionesByIdCierre(Long idCierre) {
		
		String sql = null;
		Session session = obtenerSession();
		boolean borradoExito = true;
		
		try{
			sql = "DELETE TB_COMS_INFORMES_COMISIONES WHERE IDCIERRE = " + idCierre;
			session.createSQLQuery(sql).executeUpdate();
		}catch (Exception excepcion) {
			logger.error("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_COMISIONES ", excepcion);
			borradoExito = false;
		}

		try{
			sql = "DELETE TB_COMS_INFORMES_COMS_2015 WHERE IDCIERRE = " + idCierre;
			session.createSQLQuery(sql).executeUpdate();
		}catch (Exception excepcion) {
			logger.error("Se ha producido un error al borrar los datos de la tabla TB_COMS_INFORMES_COMS_2015 ", excepcion);
			borradoExito = false;
		}
		
		return borradoExito;
	}
	
	/**
	 * Borra los resgistros de RgaUnifMediadores por mes y año de cierre
	 * @param cierre
	 * @throws Exception 
	 */
	public void borrarRgaUnifMediadores(final Date fechaCierre) throws Exception{
		String sql = null;
		Session session = obtenerSession();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaCierre);
		int mes  = 1 + cal.get(Calendar.MONTH);
		int anio = cal.get(Calendar.YEAR);
		try{
			sql = "DELETE TB_RGA_UNIF_MEDIADORES WHERE MES = "+ mes + " AND ANYO = " + anio;
			session.createSQLQuery(sql).executeUpdate();
		}catch (Exception ex) {
			logger.error("Se ha producido un error al borrar los datos de la tabla TB_RGA_UNIF_MEDIADORES, MES = "+ mes + " AND ANYO = " + anio, ex);
			throw new Exception(ex);
		}
	}
	
	/**
	 *  Metodo para que cada vez que se importe un fichero comisiones, guarde los datos en la tabla TB_COMS_INFORMES_COMISINOES. 
	 * @throws DAOException 
	 */
	public void actualizarInformesFicheroComisiones(Long id) throws DAOException {
		
		try {
			String procedimiento = "PQ_INFORMES_COMISIONES.UPDATE_INF_FICHERO_COMISIONES (IDCIERRE IN NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parametros PL
			parametros.put("IDCIERRE", id);
		     
			 databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			 //throw new DAOException("Error al actualizar la Tabla Informes Comisiones para el Fichero Comisiones ", new Exception());
		} catch (Exception e) {
			logger.error("Error al actualizar la Tabla Informes comisiones para el Fichero Comisiones",e);
			throw new DAOException("Error al actualizar la Tabla Informes Comisiones para el Fichero Comisiones", e);
		}  
		
	}

	public void setFaseDao(IFaseDao faseDao) {
		this.faseDao = faseDao;
	}

	
	/**
	 * Mira en la tabla tb_coms_unif_fase si existe una fase con algun fichero
	 * de comisiones asociado
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FaseUnificado existeComisiomesUnif(String fase, BigDecimal codplan, boolean mirarFecha) throws DAOException {
		
		logger.debug("init - getFaseComisiomesUnif");
		Session session = obtenerSession();
		List<FaseUnificado> aux = new ArrayList<FaseUnificado>();
		try {
			
			Criteria criteria = session.createCriteria(FaseUnificado.class);
			criteria.createAlias("fichero", "fichero");
			criteria.add(Restrictions.eq("fase", Integer.valueOf(fase)));	
			criteria.add(Restrictions.in("fichero.tipoFichero",	new Character[] { 'C', 'U' }));
			criteria.add(Restrictions.eq("plan", codplan.intValue()));
			
			if (mirarFecha)
				criteria.add(Restrictions.isNotNull("fichero.fechaAceptacion"));
			
			aux = criteria.list();
			if (aux.size() > 0){
				return aux.get(0);
			}
			
			logger.debug("fin - getFaseComisiomesUnif");			
			
			return null;
			
		} catch (Exception e) {
			logger.error("Se ha producido un error en la BBDD:" + e.getMessage());
			throw new DAOException("Se ha producido un error en la BBDD",e);
		}
	}


}