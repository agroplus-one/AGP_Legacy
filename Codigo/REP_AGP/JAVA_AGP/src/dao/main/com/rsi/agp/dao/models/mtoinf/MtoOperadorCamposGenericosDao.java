package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.OperadorCampoGenericoFilter;
import com.rsi.agp.core.jmesa.sort.OperadorCampoGenericoSort;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.DatoInformes;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfOperadores;

public class MtoOperadorCamposGenericosDao extends BaseDaoHibernate implements IMtoOperadorCamposGenericosDao { 

	@Override
	public int getOpGenericoCountWithFilter(final OperadorCampoGenericoFilter filter) {
		
		logger.debug("[MtoOperadorCamposGenericosDao] getOpGenericoCountWithFilter");

		// Ejecuta la sentencia y devuelve el count
		return ( (BigDecimal) obtenerSession().createSQLQuery(filter.getSqlCount()).list().get(0) ).intValue();
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<VistaMtoinfOperadores> getOpGenericoWithFilterAndSort(
			final OperadorCampoGenericoFilter filter, final OperadorCampoGenericoSort sort, final int rowStart,	final int rowEnd) throws BusinessException {
	try{
		
		List<VistaMtoinfOperadores> applications = (List) getHibernateTemplate().execute(new HibernateCallback() {
        public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Criteria criteria = session.createCriteria(VistaMtoinfOperadores.class);
    		// Filtro
            criteria = filter.execute(criteria);
            // Ordenación
            criteria = sort.execute(criteria);
            // Primer registro
            criteria.setFirstResult(rowStart);
            // Número máximo de registros a mostrar
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
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
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
			//throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		return lstDatoInformes;
	}
	
	@SuppressWarnings("unchecked")
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