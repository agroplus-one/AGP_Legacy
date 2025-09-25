package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ErrorWsAccionFilter;
import com.rsi.agp.core.jmesa.sort.ErrorWsAccionSort;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.commons.ErrorWsIdFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.commons.ErrorPerfiles;
import com.rsi.agp.dao.tables.commons.ErrorWs;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.ErrorWsTipo;
import com.rsi.agp.dao.tables.commons.Perfil;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ErrorWsAccionDao extends BaseDaoHibernate implements IErrorWsAccionDao {
	
	private static final String ERROR_WS = "errorWs";
	private static final String LINEA = "linea";

	@Override
	public int getConsultaErrorWsCountWithFilter(final ErrorWsAccionFilter filter, final boolean vieneDeCambioMasivo,final Long idErrorWs) {
		logger.debug("init - [ErrorWSAccionDao] getConsultaErrorWsCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(ErrorWsAccion.class);
						// Alias
						criteria.createAlias(LINEA, "lin", Criteria.LEFT_JOIN);              
						criteria.createAlias(ERROR_WS, "error");
						criteria.createAlias("errorWs.errorWsTipo", "ErrTipo");
						criteria.createAlias("errorWs.id", "errorId");
						criteria.createAlias("codErrorPerfiles", "errorPerfil", Criteria.LEFT_JOIN);
						// Filtro
						if (vieneDeCambioMasivo){
							criteria.add(Restrictions.ne("id", idErrorWs));
						}
						
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.countDistinct("id")).uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger.debug("end - [ErrorWSAccionDao] getConsultaErrorWSCountWithFilter");
		return count.intValue();	
	}
		
	@SuppressWarnings("unchecked")
	public List<ErrorWs> getErrores(ErrorWsIdFiltro errorWsidFiltro){
		 
		List<ErrorWs> result = (List<ErrorWs>) this.getObjects(errorWsidFiltro);
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public Collection<ErrorWsAccion> getErrorWsAccionWithFilterAndSort(final ErrorWsAccionFilter filter,
			final ErrorWsAccionSort sort, final int rowStart, final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [ErrorWsAccionDao] getErrorWsAccionWithFilterAndSort");
			List<ErrorWsAccion> applications = (List) getHibernateTemplate()
					.execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							List<?> result;
							Criteria criteria = session
									.createCriteria(ErrorWsAccion.class);
							
							// Alias
							criteria.createAlias(LINEA, "lin", Criteria.LEFT_JOIN);
							criteria.createAlias(ERROR_WS, "error");
							criteria.createAlias("errorWs.errorWsTipo", "ErrTipo");
							criteria.createAlias("errorWs.id", "errorId");
							criteria.createAlias("codErrorPerfiles", "errorPerfil", Criteria.LEFT_JOIN);
							criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							List<?> aux = criteria.list();
							return Arrays.asList(Arrays.copyOfRange(aux.toArray(), rowStart, rowEnd));
						}
					});
			logger.debug("end - [ErrorWsAccionDao] getErrorWsAccionWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}
	
	/** Obtiene toda la lista de Errores WS
	 * @return listerrores
	 */
	@SuppressWarnings("unchecked")
	public List<ErrorWs> getTodosErrores() {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ErrorWs.class);
		criteria.addOrder(Order.asc("coderror"));
		List<ErrorWs> listerrores = (List<ErrorWs>) criteria.list();			
		return listerrores;	
	}

	@Override
	public void replicar(BigDecimal origen, BigDecimal destino, String servicioOrig, String servicioDest) throws DAOException {
		
		try {
			String procedimiento = "PQ_REPLICAR.replicarErroresWs (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, SERVICIO_ORIGEN IN VARCHAR2, SERVICIO_DESTINO IN VARCHAR2)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("LINEASEGUROID_ORIGEN", origen);
			parametros.put("SERVICIO_ORIGEN", servicioOrig);
			parametros.put("SERVICIO_DESTINO", servicioDest);
		
			databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
		} catch (Exception e) {
			logger.error("Error al replicar errorew webservice",e);
			throw new DAOException("Error al replicar errorew webservice", e);
		}        	
	}
	
	/**
	 * La lista de errores que recibe como parámetro solo debe de ser de errores de rechazo;
	 */
	public boolean hayErroresRechazoMostrarPerfilCero(List<String> errores, Long lineaseguroid, String servicio){
		boolean res = true;
		try {
			Session session = obtenerSession(); 
			
			String codErrores = StringUtils.toValoresSeparadosXComas(errores,false);
			String sql = "";
			logger.debug("ErrorWSAccionDao.hayErrores --> codErrores = " + codErrores);
			for (String codErr:errores) {
				sql = "SELECT COUNT(*) FROM TB_COD_ERRORES_WS_ACCION WHERE SERVICIO='"+ servicio + "' AND LINEASEGUROID = " 
						 + lineaseguroid + " AND CODERROR IN " + codErr + " and ocultar='N' and Perfil=0" ;
				int numErrores = ((BigDecimal) session.createSQLQuery(sql).uniqueResult()).intValue();
				if (numErrores <1) {
					logger.debug("codError " + codErr +" NO encontrado. No muestro boton forzar a definitiva");
					res = false;
					break;
				}else {
					logger.debug("codError " + codErr +" encontrado");
				}
			}		
		} catch (Exception e) {
			logger.error("Error obtener los datos",e);
		}  	
		return res;
	}
		
	@SuppressWarnings("unchecked")
	public Long getErrorWS(ErrorWsAccion errorWs) {
		List<ErrorWsAccion> listerrores;
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ErrorWsAccion.class);
		criteria.createAlias(LINEA, LINEA);
		criteria.createAlias(ERROR_WS, ERROR_WS);
        if (null != errorWs.getLinea()){
        	if (null != errorWs.getLinea().getCodlinea()){
        		criteria.add(Restrictions.eq("linea.codlinea",errorWs.getLinea().getCodlinea()));
        	} else {
				criteria.add(Restrictions.isNull("linea.codlinea"));
			}
        	if (null != errorWs.getLinea().getCodplan()){
        		criteria.add(Restrictions.eq("linea.codplan",errorWs.getLinea().getCodplan()));
        	} else {
				criteria.add(Restrictions.isNull("linea.codplan"));
			}
        }
        if (null != errorWs.getErrorWs()){
        	if (null != errorWs.getErrorWs().getId().getCoderror()){
        		criteria.add(Restrictions.eq("errorWs.id.coderror", errorWs.getErrorWs().getId().getCoderror()));
        	}
        	if (null != errorWs.getErrorWs().getId().getCatalogo()){
        		criteria.add(Restrictions.eq("errorWs.id.catalogo", errorWs.getErrorWs().getId().getCatalogo()));
        	}
        }
		if (null != errorWs.getServicio()){
			criteria.add(Restrictions.eq("servicio", errorWs.getServicio()));
		}
		if (null != errorWs.getEntidad()) {
			if(null != errorWs.getEntidad().getCodentidad()) {
				criteria.add(Restrictions.eq("entidad.codentidad", errorWs.getEntidad().getCodentidad()));
			}
			else {
				criteria.add(Restrictions.isNull("entidad.codentidad"));
			}
		}
		
		listerrores = (List<ErrorWsAccion>) criteria.list();
		session.flush();
		
		if(listerrores.size()>0){
			session.evict((ErrorWsAccion) listerrores.get(0));
			session.clear();
			ErrorWsAccion w =(ErrorWsAccion) listerrores.get(0); 
			return w.getId();
						
		}	
		return null;			
	}
	
	/** DAA 06/02/2013  Metodo para recuperar un String con todos los Ids de ErrorWs segun el filtro
	 * 
	 * @param claseDetalleBusqueda
	 * @return listaids
	 */
	public String getlistaIdsTodos(ErrorWsAccionFilter consultaFilter) {
		String listaids="";
		Session session = obtenerSession();
		String sql = "SELECT DISTINCT E.ID FROM TB_COD_ERRORES_WS_ACCION E, " +
								      "TB_COD_ERRORES_WS C, " +
								      "TB_COD_ERRORES_WS_TIPOS T, " +
								      "TB_LINEAS L, " +
								      "TB_COD_ERRORES_PERFILES EP " +
								      consultaFilter.getSqlWhere();
		@SuppressWarnings("rawtypes")
		List lista = session.createSQLQuery(sql).list();		
		for(int i=0;i<lista.size();i++){
			listaids += lista.get(i)+",";
		}
		return listaids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ErrorWsTipo> obtenerListaTiposWsError() throws DAOException {

		Session session = obtenerSession();
		List<ErrorWsTipo> listaErrorWsTipo = (List<ErrorWsTipo>) session
				.createCriteria(ErrorWsTipo.class).list();
		return listaErrorWsTipo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Perfil> obtenerListaPerfiles() throws DAOException {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Perfil.class);				
		criteria.add(Restrictions.ne("id", BigDecimal.ZERO));
		criteria.addOrder(Order.asc("descripcion"));
		return (List<Perfil>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ErrorPerfiles> obtenerPerfiles(ErrorWsAccion accion) {
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(ErrorPerfiles.class)
				.add(Restrictions.eq("errorWsAccion.id", accion.getId()));
		return (List<ErrorPerfiles>) criteria.list();
	}
}