package com.rsi.agp.dao.models.cpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModuloId;

public class ConceptoCubiertoModuloDao extends BaseDaoHibernate implements
		IConceptoCubiertoModuloDao {

	@Override
	public List<ConceptoCubiertoModulo> getConceptosCubiertosModulo(ConceptoCubiertoModuloId id) {
		Session session = obtenerSession();
		List<ConceptoCubiertoModulo> lista = new ArrayList<ConceptoCubiertoModulo>();
		try{
			
			Criteria criteria =	session.createCriteria(ConceptoCubiertoModulo.class);
			criteria.add(Restrictions.eq("id", id));
			criteria.addOrder(Order.asc("id.columnamodulo"));
			lista = criteria.list();
		} 
		catch (Exception e) {
			logger.fatal("Error al obtener el listado de conceptos cubiertos del módulo", e);
		}
		
		return lista;
	}

}
