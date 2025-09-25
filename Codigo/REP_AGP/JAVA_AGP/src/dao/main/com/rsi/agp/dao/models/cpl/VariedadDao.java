package com.rsi.agp.dao.models.cpl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.Variedad;

public class VariedadDao extends BaseDaoHibernate implements IVariedadDao {
	
	/**
	 * Método para obtener los datos de una variedad
	 * @param lineaseguroid Identificador de plan/línea
	 * @param codcultivo Identificador del cultivo
	 * @param codvariedad Identificador de la variedad
	 * @return Variedad con todos los datos
	 */
	public Variedad getVariedad(Long lineaseguroid, BigDecimal codcultivo, BigDecimal codvariedad) throws DAOException{
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(Variedad.class);

			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codcultivo", codcultivo));
			criteria.add(Restrictions.eq("id.codvariedad", codvariedad));
			
			List<Variedad> variedades = criteria.list();
			if (variedades.size() > 0)
				return variedades.get(0);

		} catch (Exception ex) {
			logger.error("Error al obtener los datos de la variedad " +  codcultivo + ", " + codvariedad, ex);
			throw new DAOException(
					"Error al obtener los datos de la variedad " +  codcultivo + ", " + codvariedad,
					ex);
		} finally {
		}
		return null;
	}
     
}
