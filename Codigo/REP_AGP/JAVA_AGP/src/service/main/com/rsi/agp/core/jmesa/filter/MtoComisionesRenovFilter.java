package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * @author   U028975 (Tatiana, T-Systems)
 * Petición: 57624 (Mantenimiento de Comisioens en Renovables por E-S Mediadora)
 * Fecha:    (Enero/Febrero.2019)
 */

public class MtoComisionesRenovFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
		
	
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
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro indicado por 'property,valor'
	 * 08/05/2014 U029769
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		if (value != null) {
			try {			    
				// codPlan
				if (property.equals("codplan") && value != null){
					if (value == "0"){
						criteria.add(Restrictions.between("codplan", "0", "9999"));
					}else{
						criteria.add(Restrictions.eq("codplan", new BigDecimal(value.toString())));
					}
				}
				// codLinea
				else if (property.equals("codlinea") && value != null){
					criteria.add(Restrictions.eq("codlinea", new BigDecimal(value.toString())));
				}
				// entidad
				else if (property.equals("codentidad") && value != null){
					criteria.add(Restrictions.eq("codentidad", new BigDecimal(value.toString())));
				}
				//Entidad Mediadora
				else if (property.equals("codentmed") && value != null){
					criteria.add(Restrictions.eq("codentmed", new BigDecimal(value.toString())));
				}
				//SubEntidad Mediadora
				else if (property.equals("codsubmed") && value != null){
					criteria.add(Restrictions.eq("codsubmed", new BigDecimal(value.toString())));
				}
				//Grupo Negocio
				else if (property.equals("idgrupo") && value != null){
					criteria.add(Restrictions.eq("idgrupo", value.toString()));
				}
				//Modulo
				else if (property.equals("codmodulo") && value != null){
					criteria.add(Restrictions.eq("codmodulo", value.toString()));
				}
				//Referencia Importe
				else if (property.equals("refimporte") && value != null){
					criteria.add(Restrictions.eq("refimporte", value.toString()));
				}
				//Importe Desde
				else if (property.equals("impDesde") && value != null){
					criteria.add(Restrictions.eq("impDesde", new BigDecimal(value.toString())));
				}
				//Importe Hasta
				else if (property.equals("impHasta") && value != null){
					criteria.add(Restrictions.eq("impHasta", new BigDecimal(value.toString())));
				}
				//Comision
				else if (property.equals("comision") && value != null){
					criteria.add(Restrictions.eq("comision", new BigDecimal(value.toString())));
				}

			}catch (Exception e) {
				logger.error("MtoComisionesRenovFilter - "+ e.getMessage());
			}
		}
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

