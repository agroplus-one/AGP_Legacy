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
import com.rsi.agp.core.jmesa.filter.MtoImpuestoSbpFilter;
import com.rsi.agp.core.jmesa.sort.MtoImpuestoSbpSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;


public class MtoImpuestoSbpDao extends BaseDaoHibernate implements IMtoImpuestoSbpDao {
	
	@Override
	public int getConsultaMtoImpuestoSbpCountWithFilter(final MtoImpuestoSbpFilter filter) {
		logger.debug("init - [MtoImpuestoSbpDao] getConsultaMtoImpuestoSbpCountWithFilter");
		
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(MtoImpuestoSbp.class);
						//Alias
						criteria.createAlias("impuestoSbp", "impuestoSbp");
						criteria.createAlias("baseSbp", "baseSbp");
						// Filtro
						criteria = filter.execute(criteria);
						criteria.setProjection(Projections.rowCount()).uniqueResult();
						return criteria.uniqueResult();
					}
				});
		logger.debug("end - [MtoImpuestoSbpDao] getConsultaMtoImpuestoSbpCountWithFilter");
		return count.intValue();
		
	}

	@Override
	@SuppressWarnings("all")
	public Collection<MtoImpuestoSbp> getMtoImpuestoSbpWithFilterAndSort(
			final MtoImpuestoSbpFilter filter,final MtoImpuestoSbpSort sort,final int rowStart,
			final int rowEnd) throws BusinessException {
		try {
			logger.debug("init - [MtoImpuestoSbpDao] getMtoImpuestoSbpWithFilterAndSort");
			List<MtoImpuestoSbp> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {

						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Criteria criteria = session.createCriteria(MtoImpuestoSbp.class);
							//Alias
							criteria.createAlias("impuestoSbp", "impuestoSbp");
							criteria.createAlias("baseSbp", "baseSbp");
							// Filtro
							criteria = filter.execute(criteria);
							// Ordenación
							criteria = sort.execute(criteria);
							// Primer registro
							criteria.setFirstResult(rowStart);
							// Número máximo de registros a mostrar
							criteria.setMaxResults(rowEnd - rowStart);
							List<MtoImpuestoSbp> lista = criteria.list();
							return lista;
						}
					});
			logger.debug("end - [MtoImpuestoSbpDao] getMtoImpuestoSbpWithFilterAndSort");
			return applications;
		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}

	@Override
	public String replicar(BigDecimal origen, BigDecimal destino) throws DAOException {
		
		int resultado=0;
		
		try {			
			String procedimiento = "PQ_REPLICAR.replicarMtoImpuestoSbp (PLAN_DESTINO IN NUMBER, PLAN_ORIGEN IN NUMBER, P_RESULT OUT NUMBER)";
			
			Map<String, Object> parametros = new HashMap<String, Object>(); // parámetros PL
			parametros.put("PLAN_DESTINO", destino);
			parametros.put("PLAN_ORIGEN", origen);
			parametros.put("P_RESULT", resultado);
		
			parametros = databaseManager.executeStoreProc(procedimiento, parametros); // ejecutamos PL
			return parametros.get("P_RESULT").toString();
			
		} catch (Exception e) {
			logger.error("Error al replicar MtoImpuestoSbp ",e);
			throw new DAOException("Error al replicar MtoImpuestoSbp ", e);
		}        
		
	}

	@Override
	public List getImpuestoSbpWithFilter(MtoImpuestoSbp mtoImpuestoSbp) {

		Session session = obtenerSession();
		String sql = "select * from tb_sbp_impuestos i where 1=1 ";
		if(mtoImpuestoSbp.getImpuestoSbp().getCodigo()!= null){
			sql = sql + "and i.codigo = '"+mtoImpuestoSbp.getImpuestoSbp().getCodigo()+"' ";
		}
		if(mtoImpuestoSbp.getImpuestoSbp().getDescripcion()!=null){
			sql = sql + "and i.descripcion like '%"+mtoImpuestoSbp.getImpuestoSbp().getDescripcion()+"%' ";
		}
		logger.debug("[MtoImpuestoSbpDao] getImpuestoSbpWithFilter: "+sql);
		List list = session.createSQLQuery(sql).list();
		return list;		
	}
	
	public List getBaseSbpWithFilter(MtoImpuestoSbp mtoImpuestoSbp) {

		Session session = obtenerSession();
		String sql = "select * from tb_sbp_bases b where 1=1 ";
		if(mtoImpuestoSbp.getBaseSbp().getBase()!= null){
			sql = sql + "and b.base ='"+mtoImpuestoSbp.getBaseSbp().getBase()+"' ";
		}
		logger.debug("[MtoImpuestoSbpDao] getBaseSbpWithFilter: "+sql);
		List list = session.createSQLQuery(sql).list();
		return list;		
	}

	@Override
	public int numRegistrosIguales(MtoImpuestoSbp mtoImpuestoSbp) {
		Session session = obtenerSession();
		String sql= "select count(*) from tb_sbp_mto_impuestos m where 1=1";
		if(mtoImpuestoSbp.getId()!=null){
			//si viene el id estoy editando por lo que tengo que buscar si existe un registro igual con distinto id
			sql = sql + " and m.id <> "+mtoImpuestoSbp.getId();
		}
		
		sql = sql +	" and m.codplan = "+mtoImpuestoSbp.getCodplan()+
			" and m.idimpuesto = "+mtoImpuestoSbp.getImpuestoSbp().getId()+
			" and m.idbase = "+mtoImpuestoSbp.getBaseSbp().getId();
		
		logger.debug("[MtoImpuestoSbpDao] numRegistrosIguales: "+sql);
		int num = ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
		return num;
	}
	
}
