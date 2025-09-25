package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;

public class SistemaCultivoDao extends BaseDaoHibernate implements ISistemaCultivoDao {

	@Override
	public boolean existeSistemaCultivo(BigDecimal codSistCult) {
		
		try {
			Criteria criteria = obtenerSession().createCriteria(SistemaCultivo.class);
			criteria.add(Restrictions.eq ("codsistemacultivo",codSistCult));
			
			List<SistemaCultivo> lista = criteria.list();
			return (lista != null && lista.size()>0);
		} 
		catch (Exception e) {
			logger.error("Error al comprobar el sistema de cultivo " + codSistCult, e);
		} 
		
		return false;
	}

	
}
