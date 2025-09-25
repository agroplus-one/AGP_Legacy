package com.rsi.agp.core.jmesa.filter.gan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.ConstantsRC;

public class ImpuestosRCFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private static final Log LOGGER = LogFactory.getLog(ImpuestosRCFilter.class);

	public void addFilter(final String property, final Object value) {
		filters.add(new Filter(property, value));
	}

	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return criteria;
	}

	private void buildCriteria(final Criteria criteria, final String property, final Object value) {
		if (!ObjectUtils.equals(value, "")) {
			try {
				if (property.equals(ConstantsRC.PLAN_VALUE)) {
					if(value instanceof String){
						criteria.add(Restrictions.eq(ConstantsRC.PLAN_VALUE, new BigDecimal((String) value)));
					}
					if(value instanceof BigDecimal){
						criteria.add(Restrictions.eq(ConstantsRC.PLAN_VALUE, (BigDecimal) value));
					}
				}
				if (property.equals(ConstantsRC.BASE_VALUE)) {
					criteria.add(Restrictions.eq(ConstantsRC.BASE_VALUE, (String) value));
				}
				if (property.equals(ConstantsRC.COD_IMPUESTO_VALUE)) {
					criteria.add(Restrictions.eq(ConstantsRC.COD_IMPUESTO_VALUE, (String) value));
				}
				if (property.equals(ConstantsRC.NOM_IMPUESTO_VALUE)) {
					criteria.add(Restrictions.eq(ConstantsRC.NOM_IMPUESTO_VALUE, (String) value));
				}
				if (property.equals(ConstantsRC.VALOR_VALUE)) {
					criteria.add(Restrictions.eq(ConstantsRC.VALOR_VALUE, (BigDecimal) value));
				}
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("ImpuestosRCFilter - ").append(e.getMessage()).toString());
			}
		}
	}

	private static class Filter {
		private final String property;
		private final Object value;

		public Filter(String clave, Object valor) {
			super();
			this.property = clave;
			this.value = valor;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}

	}

}
