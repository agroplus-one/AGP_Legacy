package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import com.rsi.agp.dao.filters.cesp.ConceptoPpalFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;

public class ConceptoPpalDao extends BaseDaoHibernate implements IConceptoPpalDao {

	@Override
	public boolean existeConceptoPpal(BigDecimal codConceptoPpal) {
		
		try {
			ConceptoPpalFiltro cpf = new ConceptoPpalFiltro(codConceptoPpal);
			Criteria criteria = cpf.getCriteria(this.obtenerSession());
			criteria.setProjection(Projections.rowCount()).uniqueResult();
			Integer contador = (Integer) criteria.uniqueResult();
			
			return (contador > 0);
			
		} catch (Exception e) {
			logger.error("Ocurrio un error al comprobar si existe el concepto ppal del modulo", e);
		}
		
		return false;
	}
	
	public ConceptoPpalModulo getConceptoPpal(BigDecimal codConceptoPpal) throws Exception{
		
		try {
			ConceptoPpalFiltro cpf = new ConceptoPpalFiltro(codConceptoPpal);
			Criteria criteria = cpf.getCriteria(this.obtenerSession());
			
			ConceptoPpalModulo conceptoPpalModulo = (ConceptoPpalModulo) criteria.uniqueResult();
			
			return conceptoPpalModulo;
			
		} catch (Exception e) {
			logger.error("Ocurrio un error al comprobar si existe el concepto ppal del modulo", e);
			throw e;
		}
	}

}
