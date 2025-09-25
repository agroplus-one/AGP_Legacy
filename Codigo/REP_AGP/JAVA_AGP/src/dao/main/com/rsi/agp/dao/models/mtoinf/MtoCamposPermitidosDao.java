package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CamposPermitidosFilter;
import com.rsi.agp.core.jmesa.sort.CamposPermitidosSort;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;

public class MtoCamposPermitidosDao extends BaseDaoHibernate implements	IMtoCamposPermitidosDao {

	@Override
	public int getCalcPermCountWithFilter(final CamposPermitidosFilter filter, final String tablaOrigen, final String descripcion) {
		
		logger.debug("init - [MtoCamposPermitidosDao] getCamposPermitidosCountWithFilter");
		Session session = obtenerSession();
		String sql = filter.getSqlInnerJoin(tablaOrigen);
		sql += filter.getSqlWhere();
		
		// Si se ha filtrado por tabla origen se añade la condicion
		if (tablaOrigen != null && !"".equals(tablaOrigen)){
			sql += " AND vis.id = " + tablaOrigen;
		}
		
		if (descripcion != null && !descripcion.equals("")){
			sql += " AND ( upper(cp.descripcion) like upper('%"+descripcion+"%'))";
        }
		
		logger.debug("[MtoCamposPermitidosDao] getCamposPermitidosCountWithFilter. Se ejecuta la consulta: " + sql);
		logger.debug("end - [MtoCamposPermitidosDao] getCamposPermitidosCountWithFilter");
		
		return ( (BigDecimal)session.createSQLQuery(sql).list().get(0) ).intValue();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<CamposPermitidos> getCalcPermWithFilterAndSort(
			final CamposPermitidosFilter filter, final CamposPermitidosSort sort, final int rowStart,
			final int rowEnd, final String tablaOrigen, final String descripcion) throws BusinessException {
	try{
		logger.debug("init - [MtoCamposPermitidosDao] getCalcPermWithFilterAndSort");
			List<CamposPermitidos> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CamposPermitidos.class);
                criteria.createAlias("vistaCampo","visC", CriteriaSpecification.LEFT_JOIN);
                criteria.createAlias("vistaCampo.vista","vis", CriteriaSpecification.LEFT_JOIN);
                criteria.createAlias("vistaCampo.vistaCampoTipo","visTipo", CriteriaSpecification.LEFT_JOIN);
                
                logger.info("tablaOrigen= "+tablaOrigen);
        		
                if (tablaOrigen != null && !"".equals(tablaOrigen)) {
        			criteria.add(Restrictions.eq("vis.id", new BigDecimal(tablaOrigen)));
                }

        		// Filtro
                criteria = filter.execute(criteria);
                if (descripcion != null && !descripcion.equals("")){
                	criteria.add(Restrictions.sqlRestriction("upper(descripcion) like upper('%"+descripcion+"%')"));
                }
                
                // Ordenacion
                criteria = sort.execute(criteria);
                // Primer registro
                criteria.setFirstResult(rowStart);
                // Numero maximo de registros a mostrar
                criteria.setMaxResults(rowEnd - rowStart);
                // Devuelve el listado
                return criteria.list();
            }
        });
		logger.debug("end - [MtoCamposPermitidosDao] getCalcPermWithFilterAndSort");
        return applications;
		}catch (Exception e) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
	}
	
	@Override
	public List<CamposPermitidos> getListaCamposPermitidos() throws DAOException {
	
		List<CamposPermitidos> listaCamposPermitidos; 
		try {
			
			listaCamposPermitidos = findAll(CamposPermitidos.class);
			return listaCamposPermitidos;
			
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
	
	@Override
	public List<CamposPermitidos> getListaCamposPermitidosParaOperador(){
		List<CamposPermitidos> lstCamposPermitidos = new ArrayList<CamposPermitidos>(); 
		try {

		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(CamposPermitidos.class);
		criteria.createAlias("vistaCampo", "vc");
		criteria.createAlias("vistaCampo.vista", "vi");
		criteria.createAlias("vistaCampo.vistaCampoTipo", "vct");
		criteria.addOrder(Order.asc("vi.nombre"));
		criteria.add(Restrictions.eq("vct.idtipo", new BigDecimal(ConstantsInf.CAMPO_TIPO_NUMERICO)));
		lstCamposPermitidos = criteria.list();
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
			//throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		return lstCamposPermitidos;
	}
	
	
	
	@Override
	public List<DatoInformes> getListaDatosInformes(Long idCampoPermitido){
		List<DatoInformes> lstDatoInformes = new ArrayList<DatoInformes>(); 
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(DatoInformes.class);
			criteria.createAlias("camposPermitidos", "cp");
			criteria.add(Restrictions.eq("cp.id", idCampoPermitido));
			lstDatoInformes = criteria.list();
			return lstDatoInformes;
		} catch (Exception ex) {
			logger.info("Se ha producido un error durante el acceso a la base de datos: " + ex.getMessage());
		}
		return lstDatoInformes;
	}
	
	@Override
	public boolean checkCampPermExists(BigDecimal idCampoPermitido) throws DAOException {
		List<CamposPermitidos> lstCamposPermitidos = new ArrayList<CamposPermitidos>();
		Session session = obtenerSession();
		boolean CamPermExists = false;
	
		try {			
				Criteria criteria = session.createCriteria(CamposPermitidos.class);
				criteria.createAlias("vistaCampo", "visC");
				criteria.add(Restrictions.eq("visC.id", idCampoPermitido));
				lstCamposPermitidos = criteria.list();
				 
				 if (!lstCamposPermitidos.isEmpty()) {	
					 CamposPermitidos camPer = lstCamposPermitidos.get(0);
					 if (camPer.getId() != null) { CamPermExists = true; }
				 }		
			
		} catch (Exception e) {			
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);		
		}
	
		return CamPermExists;	
	}
	
}
