package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class CamposPermitidosFilter implements CriteriaCommand {
	
	private List<Filter> filters = new ArrayList<Filter >();
	private final Log  logger = LogFactory.getLog(getClass());	
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
			try {
        		// descripcion
        		if (property.equals("descripcion")){
        			criteria.add(Restrictions.eq("descripcion", value.toString()));
        		}
        		// TABLA ORIGEN
        		else if (property.equals("vistaCampo.tablaOrigen")){
        			//criteria.add(Restrictions.eq("vis.id", new BigDecimal(value.toString())));
        		}
        		// CAMPO
        		else if (property.equals("vistaCampo.nombre")){
        			criteria.add(Restrictions.eq("visC.nombre", value.toString()));
        		}
        		// TIPO
        		else if (property.equals("vistaCampo.vistaCampoTipo.idtipo")){
        			criteria.add(Restrictions.eq("visTipo.idtipo", new BigDecimal(value.toString())));
        		}
        		//Visible
        		else if (property.equals("vistaCampo.visible")){
        			criteria.add(Restrictions.eq("visC.visible", new BigDecimal(value.toString())));
        		}
			}
			catch (Exception e) {
				logger.error("CamposPermitidosFilter - "+e.getMessage());
			}     	
        }
    }
	
	public String getSqlInnerJoin(String tablaOrigen){
		String sqlInnerJoin = "select count (*) from tb_mtoinf_campos_permitidos cp ";
		boolean vCampo = false;
		if (tablaOrigen != null && !"".equals(tablaOrigen)){ 
			sqlInnerJoin += " inner join tb_mtoinf_vistas_campos vc on vc.id = cp.idcampo" ;
			sqlInnerJoin += " inner join tb_mtoinf_vistas vis on vis.id = vc.idvista" ;
			vCampo = true;
		}
		for (Filter filter : filters) {
			if (!vCampo && filter.getProperty().contains("vistaCampo.")){
				sqlInnerJoin += " inner join tb_mtoinf_vistas_campos vc on vc.id = cp.idcampo" ;
				vCampo = true;
			}
			
		}
		return sqlInnerJoin;
	}
	
	public String getSqlWhere(){
		String sqlWhere = " WHERE 1 = 1";
		for (Filter filter : filters) {	
			if (filter.getValue() != null) {
				try {
	        		// descripcion
	        		if (filter.getProperty().equals("descripcion")){
	        			sqlWhere += " AND cp.descripcion = '" + filter.getValue() + "'";
	        		}
	        		// TABLA ORIGEN
	        		else if (filter.getProperty().equals("vistaCampo.tablaOrigen")){
	        			//sqlWhere += " AND vis.id = " + filter.getValue();
	        		}
	        		// CAMPO
	        		else if (filter.getProperty().equals("vistaCampo.nombre")){
	        			sqlWhere += " AND vc.nombre = '" + filter.getValue() + "'";
	        		}
	        		// TIPO
	        		else if (filter.getProperty().equals("vistaCampo.vistaCampoTipo.idtipo")){
	        			sqlWhere += " AND vc.tipo = '" + filter.getValue() + "'";
	        		}else if (filter.getProperty().equals("vistaCampo.visible")){
	        			sqlWhere += " AND vc.visible = " + filter.getValue() + "";
	        		}
				}catch (Exception e) {
					logger.error("CamposPermitidosFilter - "+e.getMessage());
				}
			}	
        }
		return sqlWhere;
	}
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
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
