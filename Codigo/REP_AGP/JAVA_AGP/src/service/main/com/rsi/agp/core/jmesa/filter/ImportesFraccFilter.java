package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.ImportesFraccService;

public class ImportesFraccFilter extends GenericoFilter implements IGenericoFilter{
	
	@Override
	public Criteria execute(Criteria criteria) {
		
		for (Filter filter : this.filters) { 

			if("linea.codplan".equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("linea.codplan", val));
			}
			if("linea.codlinea".equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("linea.codlinea", val));
			}
			if("subentidadMediadora.id.codentidad".equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad", val));
			}
			if("subentidadMediadora.id.codsubentidad".equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad", val));
			}
			if(ImportesFraccService.IMPORTE.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("importe", val));
			}
			if(ImportesFraccService.TIPO.equals(filter.getProperty())){
				Integer val = (Integer) filter.getValue();
				criteria.add(Restrictions.eq("tipo", val));
			}
			if(ImportesFraccService.PCTRECARGO.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("pctRecargo", val));
			}
    }

        return criteria;
	}
	
	public void execute() {
		//EMPTY METHOD
	}
}
