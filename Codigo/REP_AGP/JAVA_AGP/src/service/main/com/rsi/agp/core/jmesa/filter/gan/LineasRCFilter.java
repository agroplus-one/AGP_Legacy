package com.rsi.agp.core.jmesa.filter.gan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class LineasRCFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());

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

	private void buildCriteria(final Criteria criteria, final String property,
			final Object value) {
		if (value != null) {
			try {
				// PLAN
				if (property.equals("linea.codplan") && value != null) {
					criteria.add(Restrictions.eq("lineaseguro.codplan",
							new BigDecimal((String) value)));
				}
				// LINEA
				else if (property.equals("linea.codlinea") && value != null) {
					criteria.add(Restrictions.eq("lineaseguro.codlinea",
							new BigDecimal((String) value)));
				}
				// ESPECIE
				else if (property.equals("codespecie") && value != null) {
					criteria.add(Restrictions.eq("codespecie", new BigDecimal(
							(String) value)));
				}
				// REGIMEN
				else if (property.equals("codregimen") && value != null) {
					criteria.add(Restrictions.eq("codregimen", new BigDecimal(
							(String) value)));
				}
				// TIPO CAPITAL
				else if (property.equals("codtipocapital") && value != null) {
					criteria.add(Restrictions.eq("codtipocapital",
							new BigDecimal((String) value)));
				}
				// ESPECIE PARA RC
				else if (property.equals("especiesRC.codespecie")
						&& value != null) {
					criteria.add(Restrictions
							.eq("especiesRC.codespecie", value));
				}
			} catch (Exception e) {
				logger.error("LineasRCFilter - " + e.getMessage());
			}
		}
	}

	public String getSqlWhere() {
		String sqlWhere = "WHERE LRC.LINEASEGUROID = L.LINEASEGUROID";
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				// PLAN
				if (property.equals("linea.codplan")) {
					sqlWhere += " AND L.CODPLAN = " + filter.getValue();
				}
				// LINEA
				if (property.equals("linea.codlinea")) {
					sqlWhere += " AND L.CODLINEA = " + filter.getValue();
				}
				// ESPECIE
				if (property.equals("codespecie")) {
					sqlWhere += " AND LRC.CODESPECIE = " + filter.getValue();
				}
				// REGIMEN
				if (property.equals("codregimen")) {
					sqlWhere += " AND LRC.CODREGIMEN = " + filter.getValue();
				}
				// TIPO CAPITAL
				if (property.equals("codtipocapital")) {
					sqlWhere += " AND LRC.CODTIPOCAPITAL = "
							+ filter.getValue();
				}
				// ESPECIE PARA RC
				if (property.equals("especiesRC.codespecie")) {
					sqlWhere += " AND LRC.CODESPECIE_RC = '" + filter.getValue() + "'";
				}
			}
		} catch (Exception e) {
			logger.error("LineasRCFilter - Error al recuperar la lista de todos los ids -"
					+ e.getMessage());
		}
		return sqlWhere;
	}

	private static class Filter {
		private final String property;
		private final Object value;

		public Filter(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}
	}
}
