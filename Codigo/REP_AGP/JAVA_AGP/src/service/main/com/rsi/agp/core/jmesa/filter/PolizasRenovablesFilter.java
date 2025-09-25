package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.PolizasRenovablesService;

/**
 * @author U029769
 *
 */
public class PolizasRenovablesFilter implements CriteriaCommand {

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
				// entidad
				if (property.equals("codentidad") && value != null){
					criteria.add(Restrictions.eq("codentidad", Long.valueOf(value.toString())));
					
				}
				// entidad mediadora
				if (property.equals("codentidadmed") && value != null){
					criteria.add(Restrictions.eq("codentidadmed", new BigDecimal(value.toString())));
					
				}
				// subentidad mediadora
				if (property.equals("codsubentmed") && value != null){
					criteria.add(Restrictions.eq("codsubentmed", new BigDecimal(value.toString())));
					
				}
				// tomador
				if (property.equals("nifTomador") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("nifTomador", value.toString()));
				}
				// plan
				else if (property.equals("plan") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("plan", new BigDecimal(value.toString())));
				} 
				// linea
				else if (property.equals("linea") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("linea", new BigDecimal(value.toString())));
				} 
				// poliza
				else if (property.equals("referencia") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("referencia", value.toString()));
				} 
				// colectivo
				else if (property.equals("refcol") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("refcol", value.toString()));
				} 
				// nifAsegurado
				else if (property.equals("nifAsegurado") && value != null && !value.equals("")){
					criteria.add(Restrictions.eq("nifAsegurado", value.toString()));
				} 
				// estados agroPlus
//				else if (property.equals("estadoRenovacionAgroplus.codigo") && value != null ){
//					criteria.add(Restrictions.eq("estadoRenovacionAgroplus.codigo", Long.valueOf(value.toString())));
//				} 
				// estados agroSeguro
				else if (property.equals("estagroseguro") && value != null ){
					criteria.add(Restrictions.eq("estagroseguro", new BigDecimal(value.toString())));
				
				}
				// estados envío IBAN
				else if (property.equals("estadoIban") && value != null ){
					criteria.add(Restrictions.eq("estadoIban", new BigDecimal(value.toString())));
				} 
				// Listado de grupo de entidades
        		if (PolizasRenovablesService.CAMPO_LISTADOGRUPOENT.equals(property)) {
        			criteria.add(Restrictions.in("codentidad", (List<Long>) value));
        		}
        		
       
        		
//        		// GRUPO NEGOCIO
//				else if (property.equals("gastosRenovacions.grupoNegocio") && value != null ){
//					criteria.add(Restrictions.eq("gastosRenovacions.grupoNegocio", value.toString().charAt(0)));
//				}
			}
			catch (Exception e) {
				logger.error("PolizasRenovablesFilter - "+ e.getMessage());
			}
		}
	}
	
	/** Añade el filtro de Usuario para la lista de Ids.
	 * 
	 * @param MtoUsuariosFilter
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere= " WHERE 1=1 ";//AND ren.idcolectivo = colren.id AND ren.id = gastos.idpolizarenovable(+)";
		
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				// codentidad
			    if (property.equals("codentidad") && filter != null && !filter.getValue().equals("")){
			    	sqlWhere += " AND CODENTIDAD = '"+ filter.getValue()+"'";
			    }
				// entidad mediadora
			    if (property.equals("codentidadmed") && filter != null && !filter.getValue().equals("")){
			    	sqlWhere += " AND CODENTIDADMED = '"+ filter.getValue()+"'";
			    }
				// subentidad mediadora
			    if (property.equals("codsubentmed") && filter != null && !filter.getValue().equals("")){
			    	sqlWhere += " AND CODSUBENTMED = '"+ filter.getValue()+"'";
			    }
			    // referencia colectivo
			    if (property.equals("refcol") && filter != null && !filter.getValue().equals("")){
			    	sqlWhere += " AND refcol = '"+ filter.getValue()+"'";
			    }
				// tomador
				if (property.equals("nifTomador")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.nifTomador = '"+ filter.getValue()+"'";
				}
				// plan
				if (property.equals("plan")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.PLAN = '" +  filter.getValue()+"'";
				}
				// linea
				if (property.equals("linea")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.LINEA = " + filter.getValue();
				}
				// referencia Poliza
				if (property.equals("referencia")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.REFERENCIA = '" + filter.getValue()+"'";
				}
				// nifAsegurado
				if (property.equals("nifAsegurado")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.nifAsegurado = '" + filter.getValue()+"'";
				}
				// estado AgroPlus
				if (property.equals("estagroplus")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.estagroplus = " + filter.getValue();
				}
				// estado AgroSeguro
				if (property.equals("estagroseguro")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.estagroseguro = " + filter.getValue();
				}
				// estado Envío IBAN
				if (property.equals("estadoIban")&& filter != null && !filter.getValue().equals("")){
					sqlWhere += " AND ren.estadoIban = " + filter.getValue();
				}
				
				
				
				
			}
		}catch (Exception e) {
			logger.error("polizasrenovablesFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
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