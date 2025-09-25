package com.rsi.agp.core.jmesa.filter.gan;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.ConstantsRC;

public class VistaPolizasRCFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private static final Log LOGGER = LogFactory.getLog(VistaPolizasRCFilter.class);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void addFilter(final String property, final Object value) {
		filters.add(new Filter(property, value));
	}
	
	@Override
	public Criteria execute(Criteria criteria) {
		for(Filter filter : filters){
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return criteria;
	}
	
	private void buildCriteria(final Criteria criteria, final String property,
			final Object value) {
		if(!ObjectUtils.equals(value, "")){
			try {
	        	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
	        	Matcher dateMatcher = datePattern.matcher(value.toString());
	        	if(dateMatcher.find()){
	        		//La propiedad es de tipo fecha
	    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
	    					Integer.parseInt(dateMatcher.group(2))-1, 
	    					Integer.parseInt(dateMatcher.group(1)));
        			GregorianCalendar fechaMas24 = new GregorianCalendar();
        			fechaMas24.setTime(gc.getTime());
        			fechaMas24.add(Calendar.HOUR,24);
        			criteria.add(Restrictions.ge(property, gc.getTime()));
        			criteria.add(Restrictions.lt(property, fechaMas24.getTime()));
	        	} else {
					if(property.equals(ConstantsRC.ENTIDAD_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.ENTIDAD_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.OFICINA_VAL)){
						String codOficina = String.format("%04d", Integer.parseInt((String)value));
						criteria.add(Restrictions.eq(ConstantsRC.OFICINA_VAL, codOficina));
						return;
					}
					if(property.equals(ConstantsRC.USUARIO_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.USUARIO_VAL, (String)value));
						return;
					}
					if(property.equals(ConstantsRC.PLAN_POLIZA_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.PLAN_POLIZA_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.LINEA_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.LINEA_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.REF_COL_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.REF_COL_VAL, (String)value));
						return;
					}
					if(property.equals(ConstantsRC.REF_POLIZA_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.REF_POLIZA_VAL, (String)value));
						return;
					}
					if(property.equals(ConstantsRC.NIFCIF_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.NIFCIF_VAL, (String)value));
						return;
					}
					if(property.equals(ConstantsRC.CLASE_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.CLASE_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.MODULO_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.MODULO_VAL, (String)value));
						return;
					}
					if(property.equals(ConstantsRC.ESTADO_POL_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.ESTADO_POL_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.SUMA_ASEGURADA_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.SUMA_ASEGURADA_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.IMPORTE_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.IMPORTE_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.ESTADO_RC_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.ESTADO_RC_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.REF_OMEGA_VAL)){
						criteria.add(Restrictions.eq(ConstantsRC.REF_OMEGA_VAL, new BigDecimal((String)value)));
						return;
					}
					if(property.equals(ConstantsRC.N_SOLICITUD_VAL)){
						criteria.add(Restrictions.eq("idpoliza", new BigDecimal((String)value)));
						return;
					}
	        	}
			} catch (Exception e) {
				LOGGER.error("VistaPolizasRCFilter - ", e);
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
