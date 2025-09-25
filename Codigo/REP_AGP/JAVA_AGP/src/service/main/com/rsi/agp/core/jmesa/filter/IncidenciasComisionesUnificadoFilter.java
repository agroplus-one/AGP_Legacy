package com.rsi.agp.core.jmesa.filter;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class IncidenciasComisionesUnificadoFilter extends GenericoFilter
		implements IGenericoFilter {
 
	@Override
	public Criteria execute(final Criteria criteria) {
		for (Filter filter : this.filters) {			
			if("mensaje".equals(filter.getProperty())){
				String val = (String) filter.getValue();
				criteria.add(Restrictions.ilike("mensaje", val, MatchMode.ANYWHERE));	
			}else {
				this.buildCriteria(criteria, filter.getProperty(),
					filter.getValue(), filter.getTipo());
			}
		}
		return criteria;
	}
	
	public String getSqlWhere() {
		String sqlWhere = "WHERE INC.LINEASEGUROID = L.LINEASEGUROID ";
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				// FICHERO
				if (property.equals("ficheroUnificado.id")) {
					sqlWhere += " AND INC.ID_FICHERO_UNIF = " + filter.getValue();
				}
				// PLAN
				if (property.equals("linea.codplan")) {
					sqlWhere += " AND L.CODPLAN = " + filter.getValue();
				}
				// LINEA
				if (property.equals("linea.codlinea")) {
					sqlWhere += " AND L.CODLINEA = " + filter.getValue();
				}
				// COLECTIVO
				if (property.equals("idcolectivo")) {
					sqlWhere += " AND INC.IDCOLECTIVO = " + filter.getValue();
				}
				// SUBENTIDAD MEDIADORA
				if (property.equals("subentidad")) {
					sqlWhere += " AND INC.SUBENTIDAD = '" + filter.getValue() + "'";
				}
				// OFICINA
				if (property.equals("oficina")) {
					sqlWhere += " AND INC.OFICINA = '" + filter.getValue() + "'";
				}
				// FASE
				if (property.equals("fase")) {
					sqlWhere += " AND INC.FASE = " + filter.getValue();
				}
				// POLIZA
				if (property.equals("refpoliza")) {
					sqlWhere += " AND INC.REFPOLIZA = '" + filter.getValue() + "'";
				}
				// ESTADO
				if (property.equals("estado")) {
					sqlWhere += " AND INC.ESTADO = '" + filter.getValue() + "'";
				}
				// ES_MED_COLECTIVO
				if (property.equals("esMedColectivo")) {
					sqlWhere += " AND INC.ES_MED_COLECTIVO = '" + filter.getValue() + "'";
				}
				// MENSAJE
				if (property.equals("mensaje")) {
					sqlWhere += " AND INC.MENSAJE LIKE '%" + filter.getValue() + "%'";
				}
			}
		} catch (Exception e) {
			logger.error("DatosRCFilter - Error al recuperar la lista de todos los ids -"
					+ e.getMessage());
		}
		return sqlWhere;
	}
}
