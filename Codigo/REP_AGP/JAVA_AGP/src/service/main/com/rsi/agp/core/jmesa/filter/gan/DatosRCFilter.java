package com.rsi.agp.core.jmesa.filter.gan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class DatosRCFilter implements CriteriaCommand {

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
				// ENTIDAD MEDIADORA
				else if (property.equals("subentidadMediadora.id.codentidad")
						&& value != null) {
					criteria.add(Restrictions.eq(
							"subentidadMediadora.id.codentidad",
							new BigDecimal((String) value)));
				}
				// SUBENTIDAD MEDIADORA
				else if (property
						.equals("subentidadMediadora.id.codsubentidad")
						&& value != null) {
					criteria.add(Restrictions.eq(
							"subentidadMediadora.id.codsubentidad",
							new BigDecimal((String) value)));
				}
				// ESPECIE PARA RC
				else if (property.equals("especiesRC.codespecie")
						&& value != null) {
					criteria.add(Restrictions
							.eq("especiesRC.codespecie", value));
				}
				// REGIMEN PARA RC
				else if (property.equals("regimenRC.codregimen")
						&& value != null) {
					criteria.add(Restrictions.eq("regimenRC.codregimen",
							new BigDecimal((String) value)));
				}
				// SUMA ASEGURADA
				else if (property.equals("sumaAseguradaRC.codsuma")
						&& value != null) {
					criteria.add(Restrictions.eq("sumaAseguradaRC.codsuma",
							new BigDecimal((String) value)));
				}
				// TASA
				else if (property.equals("tasa") && value != null) {
					criteria.add(Restrictions.eq("tasa", new BigDecimal(
							(String) value)));
				}
				// FRANQUICIA
				else if (property.equals("franquicia") && value != null) {
					criteria.add(Restrictions.eq("franquicia", new BigDecimal(
							(String) value)));
				}
				// PRIMA MINIMA
				else if (property.equals("primaMinima") && value != null) {
					criteria.add(Restrictions.eq("primaMinima", new BigDecimal(
							(String) value)));
				}
			} catch (Exception e) {
				logger.error("DatosRCFilter - " + e.getMessage());
			}
		}
	}

	public String getSqlWhere() {
		String sqlWhere = "WHERE DRC.LINEASEGUROID = L.LINEASEGUROID AND E.CODENTIDAD (+) = DRC.CODENT_MED AND E.CODSUBENTIDAD (+) =  DRC.CODSUBENT_MED";
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
				// ENTIDAD MEDIADORA
				if (property.equals("subentidadMediadora.id.codentidad")) {
					sqlWhere += " AND E.CODENTIDAD = " + filter.getValue();
				}
				// SUBENTIDAD MEDIADORA
				if (property.equals("subentidadMediadora.id.codsubentidad")) {
					sqlWhere += " AND E.CODSUBENTIDAD = " + filter.getValue();
				}
				// ESPECIE PARA RC
				if (property.equals("especiesRC.codespecie")) {
					sqlWhere += " AND DRC.CODESPECIE_RC = '"
							+ filter.getValue() + "'";
				}
				// REGIMEN PARA RC
				if (property.equals("regimenRC.codregimen")) {
					sqlWhere += " AND DRC.CODREGIMEN_RC = " + filter.getValue();
				}
				// SUMA ASEGURADA
				if (property.equals("sumaAseguradaRC.codsuma")) {
					sqlWhere += " AND DRC.CODSUMA_RC = " + filter.getValue();
				}
				// TASA
				if (property.equals("tasa")) {
					 sqlWhere += " AND DRC.TASA = TO_NUMBER('"
							+ filter.getValue() + "', '999999D99')";
				}
				// FRANQUICIA
				if (property.equals("franquicia")) {
					sqlWhere += " AND DRC.FRANQUICIA = " + filter.getValue();
				}
				// PRIMA MINIMA
				if (property.equals("primaMinima")) {
					sqlWhere += " AND DRC.PRIMA_MINIMA = " + filter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error("DatosRCFilter - Error al recuperar la lista de todos los ids -"
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
