package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.InformesDeudaAplazadaService;
import com.rsi.agp.core.jmesa.service.impl.InformesImpagadosService;

	public class InformesDeudaAplazadaFilter extends GenericoFilter 
	{
		@Override
		public Criteria execute(final Criteria criteria) {
			for (Filter filter : this.filters) {
				 // Listado de grupo de entidades
	    		if (InformesDeudaAplazadaService.GRUPO_ENT.equals( filter.getProperty())) {
	    			List<BigDecimal> l=(List<BigDecimal>) filter.getValue();
	    			ArrayList<Integer> listEnt = new ArrayList<Integer>();
	    			for (BigDecimal value : l) {
	     				listEnt.add(value.intValue());
	     			}
	    			criteria.add(Restrictions.in(InformesDeudaAplazadaService.ENTIDAD, listEnt));	    			
	    		}else {
	    			
	    		
	    			this.buildCriteria(criteria, filter.getProperty(),
						filter.getValue(), filter.getTipo());
	    		}
			}
			return criteria;
	}
	
}
