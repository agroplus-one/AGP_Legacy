package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.service.impl.InformesImpagadosService;

public class InformesImpagadosFilter extends GenericoFilter implements
		IGenericoFilter {
	
	private Boolean esMayorIgual2015;

	public Boolean getEsMayorIgual2015() {
		return esMayorIgual2015;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : this.filters) {
			 // Listado de grupo de entidades
    		if (InformesImpagadosService.CAMPO_LISTADOGRUPOENT.equals( filter.getProperty())) {
    			List<BigDecimal> l=(List<BigDecimal>) filter.getValue();
    			ArrayList<Long> listEnt = new ArrayList<Long>();
    			for (BigDecimal value : l) {
     				listEnt.add(value.longValue());
     			}
    			criteria.add(Restrictions.in("codentidad", listEnt));	    			
    		}else {
    			//La propiedad esMayorIgual2015 no es u n campo de base de datos por lo que no construimos el criteria con él
        		// necesitamos la propiedad para saber a que hbm redirigimos la consulta
        		if (filter.getProperty().toString().compareTo("esMayorIgual2015")!=0){
        			this.buildCriteria(criteria, filter.getProperty(), filter.getValue(),filter.getTipo());
        		}
    		}    	
    }

        return criteria;
	}
	
	public void execute() {
		for (Filter filter : this.filters) {
			 // Listado de grupo de entidades
			if (filter.getProperty().equalsIgnoreCase("esMayorIgual2015")) {
	   			this.esMayorIgual2015 = (Boolean) filter.getValue();
			}
   		}
	}
	
	
}
