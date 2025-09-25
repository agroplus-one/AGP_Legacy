package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public class PrimaMinimaSbpFilter implements CriteriaCommand{

	private List<Filter> filters = new ArrayList<Filter>();
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
        	       	       	
        	//Para comprobar que el valor tiene formato de fecha
        	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
        	Matcher dateMatcher = datePattern.matcher(value+"");
        	
        	Class<?> tipo;
        	
        	try {
				Class<?> c = Class.forName("com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp");
				Field field = c.getDeclaredField(property);
				tipo = field.getType();
			} catch (ClassNotFoundException e) {
				logger.debug ("No se encontró la clase");
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (SecurityException e) {
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (NoSuchFieldException e) {
				//El campo no existe. Se trata del filtro por actor (la propiedad está dentro de otro objeto)
				//En este caso, la clase será Long
				//tipo = Long.class;
				tipo = String.class;
			}
			try {
				if (dateMatcher.find()) {
	        		//La propiedad es de tipo fecha
	    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
	    					Integer.parseInt(dateMatcher.group(2))-1, 
	    					Integer.parseInt(dateMatcher.group(1)));
	    			
	    			Date fechaMas24 = new Date();
        			GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
        			fechaEnvioGrMas24.setTime(gc.getTime());
        			fechaEnvioGrMas24.add(Calendar.HOUR,24);
        			fechaMas24 = fechaEnvioGrMas24.getTime();
        			criteria.add(Restrictions.ge(property, gc.getTime()));
        			criteria.add(Restrictions.ge(property, gc.getTime()));
        			criteria.add(Restrictions.lt(property, fechaMas24));
	        	}
	        	else if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        	else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
	        	else{
	        		// Plan
	        		if (property.equals("linea.codplan")){
	        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (property.equals("linea.codlinea")){
	        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
	        		}
	        		// Prima Mínima
	        		else if (property.equals("primaMinima")){
	        			criteria.add(Restrictions.eq("primaMinima", new BigDecimal(value.toString())));
	        		}
        	}
			}catch (Exception e) {
				logger.error("ConsultaPolizaSbpFilter - "+e.getMessage());
			}     	
        }
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
