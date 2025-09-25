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
import com.rsi.agp.core.webapp.util.StringUtils;



public class PagoManualFilter implements CriteriaCommand {

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
	 * 06/05/2014 U029769
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		if (value != null) {
			try {			    
				// id.codentidad
				if (property.equals("id.codentidad") && value != null){
					criteria.add(Restrictions.eq("id.codentidad", new BigDecimal(value.toString())));
				}
				// id.codoficina
				else if (property.equals("id.codoficina") && value != null){
					criteria.add(Restrictions.eq("id.codoficina", new BigDecimal(value.toString())));
				}
				// pagoManual
				else if (property.equals("pagoManual") && value != null){
					criteria.add(Restrictions.eq("pagoManual", new BigDecimal(value.toString())));
				} 
				//nom oficina
				else if (property.equals("nomoficina") && value != null){
					Criterion ilike = Restrictions.like("nomoficina", value.toString().trim(), MatchMode.START);
					criteria.add(Restrictions.disjunction().add(ilike));
				} 
				//cod Zona
				else if (property.equals("codZona") && value != null){
					String[] strArr = value.toString().split(",");
					List<BigDecimal> codZonas = new ArrayList<BigDecimal>(strArr.length);
					for (String str : strArr) {
						if (!StringUtils.isNullOrEmpty(str)) {
							codZonas.add(new BigDecimal(str.substring(str.lastIndexOf('-')+1)));
						}
					}
					criteria.createAlias("oficinasZona", "oficinasZona");
					criteria.add(Restrictions.in("oficinasZona.id.codzona", codZonas.toArray(new BigDecimal[] {})));
				}
			}
			catch (Exception e) {
				logger.error("PagoManualFilter - "+ e.getMessage());
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

	public String getSqlWhere() {
		String sqlWhere= "";
		try {
			
			boolean zona = false;
			/* P0063701 ** MODIF TAM (26.08.2021) ** Defecto 11 ** MODIF TAM */
			/* Si se incluye la propiedad de la zona, se añade la relación con dicha tabla */
			for (Filter filter : filters) {
				String property = filter.getProperty();
				// codZona
				if (property.equals("codZona")){
					zona = true;
					sqlWhere += " , tb_oficinas_zonas oz ";
				}	
			}
			
			if (zona == true) {
				sqlWhere+= "WHERE oz.codoficina = o.codoficina and oz.codentidad = o.codentidad ";
			}else {
				sqlWhere+= "WHERE 1=1";
			}
			/* P0063701 ** MODIF TAM (26.08.2021) ** Defecto 11 ** MODIF TAM */
			
			for (Filter filter : filters) {
				String property = filter.getProperty();
				
				// codoficina
				if (property.equals("id.codoficina")){
					sqlWhere += " AND o.codoficina = '"+ filter.getValue()+"'";
				}
				// codentidad
				if (property.equals("id.codentidad")){
					sqlWhere += " AND o.codentidad = '" + filter.getValue()+"'";
				}
				// pagoManual
				if (property.equals("pagoManual")){
					sqlWhere += " AND o.pago_manual = '" +  filter.getValue()+"'";
				}
				// codZona
				if (property.equals("codZona")){
					StringBuffer sb = new StringBuffer();
					String[] strArr = filter.getValue().toString().split(",");
					for (String str : strArr) {
						if (!StringUtils.isNullOrEmpty(str)) {
							sb.append(str.substring(str.lastIndexOf('-')+1));
							sb.append(',');
						}
					}
					if (sb.length() != 0) {
						sb.setCharAt(sb.length()-1, ' ');
					}
					sqlWhere += " AND oz.codzona IN (" + sb.toString() +")";
				}				
			}
		}catch (Exception e) {
			logger.error("PagoManualFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
		}  
		return sqlWhere;
	}
}

