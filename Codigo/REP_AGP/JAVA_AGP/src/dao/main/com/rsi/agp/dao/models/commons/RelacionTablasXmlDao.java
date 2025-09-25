/*
 **************************************************************************************************
 *
 *  CREACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034  25-06-2010  Ernesto Laura	 DAO que repre
 *
  **************************************************************************************************
 */

package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.admin.TomadorDao;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.commons.RelacionTablaXml;


public class RelacionTablasXmlDao extends BaseDaoHibernate implements IRelacionTablasDao{	
	
	private static final Log LOGGER = LogFactory.getLog(RelacionTablasXmlDao.class);
	
	/** DAA 17/09/2013
	 *  Metodo para obtener una lista de codigos de tablas de condicionado segun el tipo
	 * @throws DAOException 
	 */
	public List<BigDecimal> getLstCodtablacondicionado(String tipoSc) throws DAOException {
		
		Session sesion = obtenerSession();
		try {
			Criteria criteria = sesion.createCriteria(RelacionTablaXml.class);
			
			if (tipoSc!= null && !("").equals(tipoSc)){		
				criteria.add(Restrictions.eq("tiposc", tipoSc));			
			}
			criteria.setProjection(Projections.property("numtabla"));
			
			return criteria.list();
		
		} catch (Exception e) {
			LOGGER.info("Error al obtener el listado de codigos de tablas del condicionado", e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}	
}
