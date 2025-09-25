package com.rsi.agp.core.jmesa.filter;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class GenericoFilter implements IGenericoFilter {
	
	protected List<Filter> filters = new ArrayList<Filter>();
	
	protected final Log logger = LogFactory.getLog(getClass());			
	
	public void addFilter(String property, Object value) {
		String tipo = value.getClass().getSimpleName();
		if (tipo.compareTo("Date")==0){
			filters.add(new Filter(property,  new SimpleDateFormat("dd/MM/yyyy").format(value), tipo));
		}else {
			filters.add(new Filter(property, value, tipo));	
		}
    }
	
	public void addFilter(String property, Object value, String operador) {
		
		String tipo = value.getClass().getSimpleName();
		if (tipo.compareTo("Date")==0){
			filters.add(new Filter(property,  new SimpleDateFormat("dd/MM/yyyy").format(value), tipo, operador));
		}else {
			filters.add(new Filter(property, value, tipo, operador));	
		}
    }
	
	@Override
	public void clear() {
		if(null!=filters) {
			filters.clear();
		}		
	}
	
	
	public static class Filter {
        private final String property;
        private final Object value;
        private final String tipo;
        private final String operador;
        
        
        public Filter(String property, Object value, String tipo) {
            this.property = property;
            this.value = value;
            this.tipo=tipo;
            this.operador=null;
        }
        
        /**
         * 
         * @param property
         * @param value
         * @param tipo
         * @param operador Restriction
         */
        public Filter(String property, Object value, String tipo, String operador) {
            this.property = property;
            this.value = value;
            this.tipo=tipo;
            this.operador=operador;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }
        
        public String getTipo() {
        	return tipo;
        }
        
        public String getOperador(){
        	return operador;
        }
    }

	@Override
	public Criteria execute(Criteria criteria) {
		return null;
	}

	@Override
	public void buildCriteria(Criteria criteria, String property, Object value, String tipo) {
			if (value != null) {
				try {	
					if(tipo.compareTo("Long")==0) {
						criteria.add(Restrictions.eq(property, Long.parseLong(value.toString())));
					}else if(tipo.compareTo("BigDecimal")==0) {
						criteria.add(Restrictions.eq(property, new BigDecimal(value.toString())));
					}else if (tipo.compareTo("Date")==0) {
						//Date fecha=(Date)value;
						//criteria.add(Restrictions.eq(property, fecha));	
						//criteria.add(Restrictions.eq(property, value));
						Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
			        	Matcher dateMatcher = datePattern.matcher(value+"");
						if (dateMatcher.find()) {
			        		//La propiedad es de tipo fecha
			    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
			    					Integer.parseInt(dateMatcher.group(2))-1, 
			    					Integer.parseInt(dateMatcher.group(1)));
			    			criteria.add(Restrictions.eq(property, gc.getTime()));
			        	}
			        }else {
						criteria.add(Restrictions.eq(property, value));
						//SimpleExpression x =new SimpleExpression();
					} 
					
				}catch(Exception e){
					logger.error("GenericoFilter - "+ e.getMessage());
				}
			}
	}
		

	public void buildCriteria(Criteria criteria, String property, Object value, String tipo, String operador) {
		if(operador==null){
			this.buildCriteria(criteria, property, value, tipo);
		}else{
			if (value != null) {
				try {	
					if(tipo.compareTo("Long")==0) {						
						crearCriteriaOperador(criteria,property, Long.parseLong(value.toString()),operador);	
					}else if(tipo.compareTo("BigDecimal")==0) {
						crearCriteriaOperador(criteria,property, new BigDecimal(value.toString()),operador);		
					}else if (tipo.compareTo("Date")==0) {
						Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
			        	Matcher dateMatcher = datePattern.matcher(value+"");
						if (dateMatcher.find()) {
			        		//La propiedad es de tipo fecha
			    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
			    					Integer.parseInt(dateMatcher.group(2))-1, 
			    					Integer.parseInt(dateMatcher.group(1)));
			    			crearCriteriaOperador(criteria,property, gc.getTime(),operador);		
			        	}
			        }else {
			        	crearCriteriaOperador(criteria,property, value,operador);						
					} 
					
				}catch(Exception e){
					logger.error("GenericoFilter - "+ e.getMessage());
				}
			}
		}		
	}

	private void crearCriteriaOperador(Criteria criteria, String property, Object value, String operador){
		try {
			if(operador.compareTo("gt")==0) {//mayor que
				criteria.add(Restrictions.gt(property, value));
			}
			if(operador.compareTo("ge")==0) {//mayor o igual
				criteria.add(Restrictions.ge(property, value));
			}
			if(operador.compareTo("lt")==0) {//menor que 
				criteria.add(Restrictions.lt(property, value));
			}
			if(operador.compareTo("le")==0) {//menor o igual 
				criteria.add(Restrictions.le(property, value));
			}
			if(operador.compareTo("eq")==0) {//igual
				criteria.add(Restrictions.eq(property, value));
			}
			if(operador.compareTo("ne")==0) {//diferente que
				criteria.add(Restrictions.ne(property, value));
			}
			//OJO, ir implementando las que se necesiten

		} catch (Exception e) {
			logger.error("GenericoFilter - crearCriteriaOperador - "+ e.getMessage());
		}
				
	}



}
