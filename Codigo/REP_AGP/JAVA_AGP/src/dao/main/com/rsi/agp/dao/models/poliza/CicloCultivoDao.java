/**
 * 
 */
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;


/**
 * @author U029769
 *
 */
public class CicloCultivoDao extends BaseDaoHibernate implements ICicloCultivoDao{
	
	
	@Override
	public boolean existeCicloCultivo(BigDecimal codcicloCultivo) {
		
		try {
			Criteria criteria = obtenerSession().createCriteria(CicloCultivo.class);
			criteria.add(Restrictions.eq ("codciclocultivo",codcicloCultivo));
			
			List<CicloCultivo> lista = criteria.list();
			return (lista != null && lista.size()>0);
		} 
		catch (Exception e) {
			logger.error("Error al comprobar el ciclo de cultivo " + codcicloCultivo, e);
		} 
		
		return false;
	}


}
