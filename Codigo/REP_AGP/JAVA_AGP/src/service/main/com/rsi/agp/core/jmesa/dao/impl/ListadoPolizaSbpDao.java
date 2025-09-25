package com.rsi.agp.core.jmesa.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IListadoPolizaSbpDao;
import com.rsi.agp.core.jmesa.filter.ListadoPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ListadoPolizaSbpSort;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;


public class ListadoPolizaSbpDao extends HibernateDaoSupport implements IListadoPolizaSbpDao{
	
	/**
	 * método que obtiene los items que cumplen el filtro
	 * @param filter
	 * @param ListadoPolizaSbpSort
	 * @param rowStart
	 * @param rowEnd
	 * @param filtrarDetalle -> recibe el valor del filtrado por detalle
	 * @return Collection<PolizaSbp>
	 * @throws BusinessException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<PolizaSbp> getListadoPolizasSbpWithFilterAndSort(
			final ListadoPolizaSbpFilter filter, final ListadoPolizaSbpSort sort, final int rowStart, final int rowEnd, final String filtrarDetalle) throws BusinessException {
		try{
			List<PolizaSbp> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
            	
                Criteria criteria = session.createCriteria(PolizaSbp.class);
                //alias
                criteria.createAlias("polizaPpal","polPpal", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.linea","lin", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.asegurado","aseg", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.colectivo","col", CriteriaSpecification.LEFT_JOIN);
        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
        		criteria.createAlias("col.subentidadMediadora", "esMed", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.usuario","usu", CriteriaSpecification.LEFT_JOIN);
        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
//        		criteria.createAlias("polPpal.estadoPoliza","estadoPoliza", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("col.tomador","tom", CriteriaSpecification.LEFT_JOIN);
        		//criteria.createAlias("polizaPpal.usuario","usu", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("estadoPlzSbp","estadoSbp", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.estadoPoliza","estadoPpal", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polizaCpl","polCpl", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("polCpl.estadoPoliza","estadoCpl", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("tipoEnvio","tipoEnvio", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("errorPlzSbp","errorPlzSbp", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("errorPlzSbp.errorSbp","errorPlzSbp.errorSbp", CriteriaSpecification.LEFT_JOIN);
				// añadimos esta restriccion para que no saque las polizas en estado simulacion
				criteria.add(Restrictions.not(Restrictions.eq("estadoSbp.idestado", ConstantsSbp.ESTADO_SIMULACION)));
				/**
				* P0073325 -RQ.10, RQ.11 y RQ.12
				*/
				criteria.createAlias("gedDocPolizaSbp","gedDocPolizaSbp", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("gedDocPolizaSbp.canalFirma","gedDocPolizaSbp.canalFirma", CriteriaSpecification.LEFT_JOIN);
				
				/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
				criteria.add(Restrictions.ne("aseg.isBloqueado", Integer.valueOf(1)));
				/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Fin */

        		criteria = filter.execute(criteria);
        		
                
                criteria = sort.execute(criteria);
                if (rowStart != -1 && rowEnd != -1) {
			        // Primer registro
			        criteria.setFirstResult(rowStart);
			        // N�mero m�ximo de registros a mostrar
			        criteria.setMaxResults(rowEnd - rowStart);
			    }
                return criteria.list();
            }
        });
		logger.debug("end - [ListadoPolizaSbpDao] getListadoPolizasSbpWithFilterAndSort");
        return applications;
		}catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos: " + e.getMessage());
		}
	}
	
	/**
	 * método que obtiene el número de filas que cumplen el filtro
	 * @param filter
	 * @return total de filas
	 * @param filtrarDetalle -> recibe el valor del filtrado por detalle
	 */
	public int getListadoPolizaSbpCountWithFilter(final ListadoPolizaSbpFilter filter, final String filtrarDetalle) {
		logger.debug("init - [ListadoPolizaSbpDao] getListadoPolizaSbpCountWithFilter");
        Integer count = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	
                Criteria criteria = session.createCriteria(PolizaSbp.class);
                /****/
              //alias
                criteria.createAlias("polizaPpal","polPpal", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.linea","lin", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.asegurado","aseg", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.colectivo","col", CriteriaSpecification.LEFT_JOIN);
        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
        		criteria.createAlias("col.subentidadMediadora", "esMed", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.usuario","usu", CriteriaSpecification.LEFT_JOIN);
        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
        		criteria.createAlias("col.tomador","tom", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("estadoPlzSbp","estadoSbp", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polPpal.estadoPoliza","estadoPpal", CriteriaSpecification.LEFT_JOIN);
        		criteria.createAlias("polizaCpl","polCpl", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("polCpl.estadoPoliza","estadoCpl", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("tipoEnvio","tipoEnvio", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("errorPlzSbp","errorPlzSbp", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("errorPlzSbp.errorSbp","errorPlzSbp.errorSbp", CriteriaSpecification.LEFT_JOIN);
				/**
				* P0073325 -RQ.10, RQ.11 y RQ.12
				*/
				criteria.createAlias("gedDocPolizaSbp","gedDocPolizaSbp", CriteriaSpecification.LEFT_JOIN);
				criteria.createAlias("gedDocPolizaSbp.canalFirma","gedDocPolizaSbp.canalFirma", CriteriaSpecification.LEFT_JOIN);
        		criteria = filter.execute(criteria);
                
        		// añadimos esta restriccion para que no saque las polizas en estado simulacion
				criteria.add(Restrictions.not(Restrictions.eq("estadoSbp.idestado", ConstantsSbp.ESTADO_SIMULACION)));
				
				/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Inicio */
				criteria.add(Restrictions.ne("aseg.isBloqueado", Integer.valueOf(1)));
				/* Pet. ESC-12906 ** MODIF TAM (19.01.2021) ** Fin */
				
                criteria.setProjection(Projections.rowCount()).uniqueResult();
                return criteria.uniqueResult();
            }
        });
        logger.debug("end - [ListadoPolizaSbpDao] getListadoPolizaSbpCountWithFilter");
        return count.intValue();
    }
	
}
