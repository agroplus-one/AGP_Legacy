package com.rsi.agp.core.jmesa.filter;

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

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;

public class ListadoPolizaSbpFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private final Log logger = LogFactory.getLog(getClass());

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		criteria.add(Restrictions.ne("estadoPpal.idestado", Constants.ESTADO_POLIZA_BAJA));
		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, Object value) {
		if (value != null) {
			// Para comprobar que el valor tiene formato de fecha
			Pattern datePattern = Pattern
					.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
			Matcher dateMatcher = datePattern.matcher(value + "");
			try {
	        	if (dateMatcher.find()){
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
        			criteria.add(Restrictions.lt(property, fechaMas24));
	        	}
	        	else{
		        		if (property.equals("polizaPpal.colectivo.tomador.id.codentidad")){
		        			criteria.add(Restrictions.eq("tom.id.codentidad", new BigDecimal(value.toString())));
		        		}else if(property.equals("listaGrupoEntidades")){
		        			criteria.add(Restrictions.in("tom.id.codentidad", (List<?>)value));
		        		}else if (property.equals("polizaPpal.oficina")){
		        			criteria.add(Restrictions.in("polPpal.oficina", CriteriaUtils.getCodigosOficina(value.toString())));
		        		}else if (property.equals("usuarioProvisional")){
		        			criteria.add(Restrictions.eq("usuarioProvisional", value.toString()));
		        		}else if (property.equals("polizaPpal.linea.codplan")){
		        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
		        		}else if (property.equals("polizaPpal.linea.codlinea")){
		        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
		        		}else if (property.equals("polizaPpal.clase")){
		        			criteria.add(Restrictions.eq("polPpal.clase", new BigDecimal(value.toString())));
		        		}else if (property.equals("polizaPpal.colectivo.idcolectivo")){
		        			criteria.add(Restrictions.eq("col.idcolectivo", value.toString()));
		        		}else if (property.equals("polizaPpal.colectivo.dccolectivo")){
		        			criteria.add(Restrictions.eq("col.dc", value.toString()));
		        		}else if (property.equals("estadoPlzSbp.idestado")){
		        			criteria.add(Restrictions.eq("estadoSbp.idestado", new BigDecimal(value.toString())));
		        		}else if (property.equals("polizaPpal.codmodulo")){
		        			criteria.add(Restrictions.eq("polPpal.codmodulo", value.toString()));
		        		}else if (property.equals("polizaPpal.estadoPoliza.idestado")){
		        				criteria.add(Restrictions.eq("estadoPpal.idestado", new BigDecimal(value.toString())));
		        		}else if (property.equals("polizaCpl.estadoPoliza.idestado")){
		        			if (!value.toString().equals("9")){	        				
		        				criteria.add(Restrictions.eq("estadoCpl.idestado", new BigDecimal(value.toString())));
		        			}
		        		}else if (property.equals("incSbpComp")){
		        			criteria.add(Restrictions.eq("incSbpComp", value.toString().charAt(0)));
		        		}else if (property.equals("polizaPpal.asegurado.nifcif")){
		        			criteria.add(Restrictions.eq("aseg.nifcif", value.toString()));
		        		}else if (property.equals("detalle")){
		        		}else if (property.equals("id")){
		        			criteria.add(Restrictions.eq("id",Long.valueOf(value.toString())));
		        		}else if (property.equals("referencia")){
		        			criteria.add(Restrictions.eq("referencia", value.toString()));
		        		}else if (property.equals("tipoEnvio.descripcion")){
		        			criteria.add(Restrictions.eq("tipoEnvio.descripcion",value.toString()));
		        		}
		        		else if (property.equals("errorPlzSbp.errorSbp.iderror")){
		        			criteria.add(Restrictions.eq(property, value).ignoreCase());
		        		} else if (property.equals("refPlzOmega")){
		        			criteria.add(Restrictions.eq("refPlzOmega", new BigDecimal(value.toString())));
		        		}
		        		else if(property.equals("listaGrupoOficinas")){
		        			criteria.add(Restrictions.in("polPpal.oficina",  CriteriaUtils.getCodigosListaOficina((List<?>)value)));
		        		}
		        		//DNF 29/11/2018 establecemos el filtro para el nuevo campo nsolicitud
		        		else if(property.equals(/*"polizaPpal.idpoliza"*/ "nSolicitud")){
		        			criteria.add(Restrictions.eq(/*"polizaPpal.idpoliza"*/ "id", Long.valueOf(value.toString())));
		        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
			        	}else if(property.equals("polizaPpal.colectivo.subentidadMediadora.id.codentidad")){
		        			criteria.add(Restrictions.eq("esMed.id.codentidad", new BigDecimal(value.toString())));
			        	}else if(property.equals("polizaPpal.colectivo.subentidadMediadora.id.codsubentidad")){
		        			criteria.add(Restrictions.eq("esMed.id.codsubentidad", new BigDecimal(value.toString())));
			        	}else if(property.equals("polizaPpal.usuario.delegacion")){
			        		criteria.add(Restrictions.eq("usu.delegacion",new BigDecimal(value.toString())));
			        	/**
			        	* P0073325 -RQ.10, RQ.11 y RQ.12
			        	*/
			        	}else if (property.equals("gedDocPolizaSbp.canalFirma.idCanal")){
		        			criteria.add(Restrictions.eq(property, Long.valueOf(value.toString())));
		        		}else if (property.equals("gedDocPolizaSbp.docFirmada")){
		        			criteria.add(Restrictions.eq(property, value.toString().charAt(0)));
		        		}
	        		}/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
	        		
			}catch (Exception e) {
				logger.error("ListadoPolizaSbpFilter - "+e.getMessage());
			}

		}
	}

	public static class Filter {
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

	public List<Filter> getFilters() {
		return filters;
	}

}
