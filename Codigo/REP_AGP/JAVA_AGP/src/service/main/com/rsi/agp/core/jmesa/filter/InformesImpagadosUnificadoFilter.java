package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.InformesComisionesUnificadoService;

public class InformesImpagadosUnificadoFilter extends GenericoFilter {
	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : this.filters) {
			
			if (InformesComisionesUnificadoService.LISTA_ENTIDADES_USUARIO.equals( filter.getProperty())) {
				List<BigDecimal> l=(List<BigDecimal>) filter.getValue();
				ArrayList<BigDecimal> listEnt = new ArrayList<BigDecimal>();
				for (BigDecimal value : l) {
	 				listEnt.add(value);
	 			}
				criteria.add(Restrictions.in("codentidad", listEnt));
			}else {
				this.buildCriteria(criteria, filter.getProperty(),
						filter.getValue(), filter.getTipo());
			}
			
		}
		

		
		return criteria;
	}
}
