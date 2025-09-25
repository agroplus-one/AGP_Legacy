package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
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



public class CPMTipoCapitalFilter implements CriteriaCommand{

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
	private boolean incluirNulos = false;
	private boolean sistCultivoIncluido = false;
	private boolean ffgIncluido = false;
	private boolean cicloCulIncluido = false;
	
	@Override
	public Criteria execute(Criteria criteria) {
		
		// A�ade al criteria los filtros informados en el formulario
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }
		
		// Si en la lista de filtros se encuentra la clave 'incluirNulos'
		if (incluirNulos) { 
			// Llama al m�todo que para cada clave opcional no incluida en el filtro a�ade la condici�n de que sea nula
			buildCriteriaNulos (criteria);
		}

		return criteria;
	}
	
	
	/**
	 * Comprueba si los campos no obligatorios del formulario (Sistema de cultivo y Fecha de fin de garantias) estan incluidos en el filtro.
	 * Si no lo estan, se anade la condicion de que sean nulos en el criteria
	 * @param criteria
	 * @param filtros
	 */
	private void buildCriteriaNulos(Criteria criteria) { 
		if (!sistCultivoIncluido) criteria.add(Restrictions.isNull("sistemaCultivo.codsistemacultivo"));
		if (!ffgIncluido) criteria.add(Restrictions.isNull("fechafingarantia"));
		if (!cicloCulIncluido) criteria.add(Restrictions.isNull("cicloCultivo.codciclocultivo"));
	}

	@SuppressWarnings("rawtypes")
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	
        	//Para comprobar que el valor tiene formato de fecha
        	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
        	Matcher dateMatcher = datePattern.matcher(value+"");
        	
        	Class tipo;
        	
        	try {
				Class c = Class.forName("com.rsi.agp.dao.tables.cpm.CPMTipoCapital");
				Field field = c.getDeclaredField(property);
				tipo = field.getType();
			} catch (ClassNotFoundException e) {
				logger.debug ("No se encontr� la clase");
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
	    			criteria.add(Restrictions.eq(property, gc.getTime()));
	        	}
	        	else if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        	else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
	        	else{
	        		// Plan
	        		if (property.equals("cultivo.linea.codplan") ){
	        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (property.equals("cultivo.linea.codlinea")){
	        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
	        		}
	        		// Codmodulo
	        		else if (property.equals("modulo")){
	        			criteria.add(Restrictions.eq("modulo", value.toString()));
	        		}
	        		// Codconcepto ppal mod
	        		else if (property.equals("conceptoPpalModulo.codconceptoppalmod") ){
	        			criteria.add(Restrictions.eq("conceptoPpalModulo.codconceptoppalmod", new BigDecimal(value.toString())));
	        		}
	        		// codtipoCapital
	        		else if (property.equals("tipoCapital.codtipocapital") ){
	        			criteria.add(Restrictions.eq("tipoCapital.codtipocapital", new BigDecimal(value.toString())));
	        		}
	        		// codcultivo
	        		else if (property.equals("cultivo.id.codcultivo") ){
	        			criteria.add(Restrictions.eq("cultivo.id.codcultivo", new BigDecimal(value.toString())));
	        		}
	        		// codsistemacultivo
	        		else if (property.equals("sistemaCultivo.codsistemacultivo") ){
	        			criteria.add(Restrictions.eq("sistemaCultivo.codsistemacultivo", new BigDecimal(value.toString())));
	        			sistCultivoIncluido = true;
	        		}
	        		// fechafingarantia
	        		else if (property.equals("fechafingarantia") ){
	        			criteria.add(Restrictions.eq("fechafingarantia", value));
	        			ffgIncluido = true;
	        		}
	        		// incluirNulos
	        		else if (property.equals("incluirNulos") ){
	        			incluirNulos = true;
	        		}
	        		// codCicloCultivo
	        		else if (property.equals("cicloCultivo.codciclocultivo") ){
	        			criteria.add(Restrictions.eq("cicloCultivo.codciclocultivo", new BigDecimal(value.toString())));
	        			cicloCulIncluido = true;
	        		}
        	}
			}catch (Exception e) {
				logger.error("Ocurri� un error al montar el filtro para CPMTipoCapital. ", e);
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
