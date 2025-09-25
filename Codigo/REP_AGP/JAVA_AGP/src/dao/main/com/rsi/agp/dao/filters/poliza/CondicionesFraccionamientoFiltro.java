package com.rsi.agp.dao.filters.poliza;

import com.rsi.agp.core.jmesa.filter.GenericoFilter;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;

import java.math.BigDecimal;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class CondicionesFraccionamientoFiltro extends GenericoFilter implements IGenericoFilter{
	
	private final static String ID = "lineaseguroid";
	private final static String PERIODOFRAC = "periodoFracc";
	private final static String CODPLAN = "codplan";
	private final static String CODLINEA= "codlinea";
	private final static String PCTRECAVAL = "pctRecAval";
	private final static String PCTRECFRACC = "pctRecFracc";
	private final static String IMPMINRECAVAL = "impMinRecAval";
	
	@Override
	public Criteria execute(Criteria criteria) {

		for (Filter filter : this.filters) {

			if(ID.equals(filter.getProperty())){
				Long val = (Long) filter.getValue();
				criteria.add(Restrictions.eq("id.lineaseguroid", val));
			}
			if(PERIODOFRAC.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("id.periodoFracc", val));
			}
			if(CODPLAN.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("linea.codplan", val));
			}
			if(CODLINEA.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("linea.codlinea", val));
			}
			if(PCTRECAVAL.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("pctRecAval", val));
			}
			if(PCTRECFRACC.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("pctRecFracc", val));
			}
			if(IMPMINRECAVAL.equals(filter.getProperty())){
				BigDecimal val = (BigDecimal) filter.getValue();
				criteria.add(Restrictions.eq("impMinRecAval", val));
			}
    }

        return criteria;
	}
	
	public void execute() {
		//EMPTY METHOD
	}
}