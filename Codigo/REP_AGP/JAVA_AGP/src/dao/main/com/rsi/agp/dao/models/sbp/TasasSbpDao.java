package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.TasasSbpFilter;
import com.rsi.agp.core.jmesa.sort.TasasSbpSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

public class TasasSbpDao extends BaseDaoHibernate implements ITasasSbpDao {
	
	@Override
	public int getTasasSbpCountWithFilter(final TasasSbpFilter filter) {
		logger.debug("init - [TasasSbpDao] getTasasSbpCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(TasasSbp.class);
                // Alias
				criteria.createAlias("linea", "linea");
                // Filtro
                criteria = filter.execute(criteria);
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [TasasSbpDao] getTasasSbpCountWithFilter");
        return count.intValue();
	}

	@Override
	public Collection<TasasSbp> getTasasSbpWithFilterAndSort(final TasasSbpFilter filter, final TasasSbpSort sort, final int rowStart, 
															 final int rowEnd) throws BusinessException {
		try{
			logger.debug("init - [TasasSbpDao] getTasasSbpWithFilterAndSort");
				List<TasasSbp> applications = (List<TasasSbp>) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria criteria = session.createCriteria(TasasSbp.class);     
	                // Alias
					criteria.createAlias("linea", "linea");
	                // Filtro
	                criteria = filter.execute(criteria);
	                // Ordenación
	                criteria = sort.execute(criteria);
	                // Primer registro
	                criteria.setFirstResult(rowStart);
	                // Número máximo de registros a mostrar
	                criteria.setMaxResults(rowEnd - rowStart);
	                // Devuelve el listado de tasas
	                return criteria.list();
	            }
	        });
			logger.debug("end - [TasasSbpDao] getTasasSbpWithFilterAndSort");
	        return applications;
			}catch (Exception e) {
				throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
			}
	}
	
	public String replicar(BigDecimal origen, BigDecimal destino) throws DAOException {
		
		int resultado=0;
		
		try {
			String procedimiento = "PQ_REPLICAR.replicarTasasSbp (LINEASEGUROID_DESTINO IN NUMBER, LINEASEGUROID_ORIGEN IN NUMBER, P_RESULT OUT NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("LINEASEGUROID_DESTINO", destino);
			parametros.put("LINEASEGUROID_ORIGEN", origen);
			parametros.put("P_RESULT", resultado);
		
			parametros = databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			return parametros.get("P_RESULT").toString();
			
		} catch (Exception e) {
			logger.error("Error al replicar TasasSbp ",e);
			throw new DAOException("Error al replicar TasasSbp ", e);
		}        
		
	}

	public boolean numRegDestinoIgualNumRegOrigen(Long lineaSeguroIdDestino, Long lineaSeguroIdOrigen) {
		
		boolean numregIguales = true;
		Session session = obtenerSession();
		
		String sql = "select count(*) from TB_SBP_TASAS t where t.lineaseguroid in ("+
					lineaSeguroIdDestino+","+lineaSeguroIdOrigen+ ") group by t.lineaseguroid";
		List list = session.createSQLQuery(sql).list();
		
		BigDecimal reg1 = new BigDecimal(list.get(0).toString());
		BigDecimal reg2 = new BigDecimal(list.get(1).toString());
		
		if(reg1.compareTo(reg2)!= 0)
			numregIguales = false;
		
		return numregIguales;
	}
	
	/** DAA 27/04/2013 Carga las tasas en bbdd a partir de un fichero
	 * 
	 */
	public void volcarTasasSbpFromFichero() throws BusinessException {
		
		int resultado=0;
		
		try {
			String procedimiento = "PQ_IMPORTACION_SBP.PR_INSERTAR_TASAS_SBP (P_RESULT OUT NUMBER)";
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("P_RESULT", resultado);
			
			parametros = databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			
			logger.debug("end - [TasasSbpDao] volcarTasasSbpFromFichero - Registros copiados: "+parametros.get("P_RESULT").toString());
			return;
			
		} catch (BusinessException e) {
			logger.error("Error al ejecutar el PL de volcado de las TasasSbp ",e);
			throw new BusinessException("Error al ejecutar el PL de volcado de las TasasSbp ", e);
		}
		
	}
}
