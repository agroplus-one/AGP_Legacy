package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * @author U028975 (T-Systems) GDLD-63701 - Mantenimiento de Zonas
 */
public class MtoZonasFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		return criteria;
	}

	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro
	 * indicado por 'property,valor' 16/06/2021 U028975 (T-Systems)
	 * 
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {

		if (value != null) {
			try {
				// codEntidad
				if (property.equals("id.codentidad") && value != null) {
					criteria.add(Restrictions.eq("id.codentidad", new BigDecimal((String)value)));
				}
				// codZona
				if (property.equals("id.codzona") && value != null) {
					criteria.add(Restrictions.eq("id.codzona", new BigDecimal((String)value)));
				}
				// Nombre Zona
				else if (property.equals("nomzona") && value != null) {
					Criterion ilike = Restrictions.like("nomzona", value.toString().trim(), MatchMode.START);
					criteria.add(Restrictions.disjunction().add(ilike));
				}
			} catch (Exception e) {
				logger.error("MtoZonasFilter - " + e.getMessage());
			}
		}
	}

	/**
	 * Añade el filtro de Usuario para la lista de Ids.
	 * 
	 * @param MtoUsuariosFilter
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere = "WHERE 1=1 ";
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();

				// codEntidad
				if (property.equals("id.codEntidad")) {
					sqlWhere += " AND codentidad = '" + filter.getValue() + "'";
				}
				// codEntidad
				if (property.equals("id.codzona")) {
					sqlWhere += " AND codzona = '" + filter.getValue() + "'";
				}

				// nombre usu
				if (property.equals("nombzona")) {
					sqlWhere += " AND nombzona like '" + filter.getValue() + "%'";
				}
			}
		} catch (Exception e) {
			logger.error("ClaseDetalleFilter - Error al recuperar la lista de todos los ids -" + e.getMessage());
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
