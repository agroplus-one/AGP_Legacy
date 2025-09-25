package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.SiniestrosUtilidadesService;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Entidad;

public class SiniestrosFilter implements CriteriaCommand {
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());
	private boolean filtroPlan = false;
	
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }
		
		// Si no se ha filtrado por plan, se introduce el filtro por defecto para este campo
		if (!filtroPlan) buildCondicionPlan (criteria);

        return criteria;
	}
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }	
	
	/**
	 * Anade el filtro por defecto para este campo (ano actual y ano pasado)
	 * @param criteria
	 */
	private void buildCondicionPlan (Criteria criteria) {
		
		// Obtiene el ano actual y el pasado
		GregorianCalendar gc = new GregorianCalendar();		
		BigDecimal anyoActual = new BigDecimal (gc.get(Calendar.YEAR));
		gc.add(Calendar.YEAR, -1);
		BigDecimal anyoPasado = new BigDecimal (gc.get(Calendar.YEAR));

		// Anade los anos a la condicion de busqueda
		criteria.add(Restrictions.in(SiniestrosUtilidadesService.CAMPO_PLAN,
				new BigDecimal[] { anyoActual, anyoPasado }));
		
		log ("buildCondicionPlan", "Anadidos el ano actual y el pasado al filtro de plan");
	}
	
	
	/**
	 * Carga el objeto Criteria con las condiciones de búsqueda referentes al filtro indicado por 'property,valor'
	 * @param criteria
	 * @param property
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		//Para comprobar que el valor tiene formato de fecha
    	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
    	Matcher dateMatcher = datePattern.matcher(value+"");
		
		if (value != null) {
        	
			try {			    
				
				if (dateMatcher.find()) {
					
					//La propiedad es de tipo fecha
	    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
	    					Integer.parseInt(dateMatcher.group(2))-1, 
	    					Integer.parseInt(dateMatcher.group(1)));
	    			Date dat = new Date(gc.getTimeInMillis());
	    			Date maxDate = new Date(dat.getTime() + TimeUnit.DAYS.toMillis(1));        			
        			
        			criteria.add(Restrictions.between(property, dat, maxDate));	
				}
				else {
				
	        		// Entidad
	        		if (SiniestrosUtilidadesService.CAMPO_ENTIDAD.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'entidad = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_ENTIDAD, new BigDecimal(value.toString())));
	        		}
	        		// Oficina
	        		else if (SiniestrosUtilidadesService.CAMPO_OFICINA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'oficina = "+CriteriaUtils.getCodigosOficina(value.toString())+ "'");
	        			criteria.add(Restrictions.in(SiniestrosUtilidadesService.CAMPO_OFICINA,CriteriaUtils.getCodigosOficina(value.toString())));
	        		}
	        		// Usuario
	        		else if (SiniestrosUtilidadesService.CAMPO_USUARIO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'usuario = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_USUARIO, value.toString()));
	        		}
	        		//delegacion
	        		else if (SiniestrosUtilidadesService.CAMPO_DELEGACION.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'delegacion = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_DELEGACION, value.toString()));
	        		}
	        		//entidad mediadora
	        		else if (SiniestrosUtilidadesService.CAMPO_ENTMEDIADORA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'entidad mediadora = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_ENTMEDIADORA, new BigDecimal(value.toString())));
	        		}
	        		//subentidad mediadora
	        		else if (SiniestrosUtilidadesService.CAMPO_SUBENTMEDIADORA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'subentidad mediadora = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_SUBENTMEDIADORA,new BigDecimal (value.toString())));
	        		}
	        		// Plan
	        		else if (SiniestrosUtilidadesService.CAMPO_PLAN.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'plan = " + value.toString() + "'");
	        			filtroPlan = true;
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_PLAN, new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (SiniestrosUtilidadesService.CAMPO_LINEA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'linea = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_LINEA, new BigDecimal(value.toString())));
	        		}
	        		// Poliza
	        		else if (SiniestrosUtilidadesService.CAMPO_POLIZA.equals(property)) {
	        			
	        			// Si la referencia de poliza tiene guion, se parte la cadena para buscar por referencia y dc
	        			if (value.toString().indexOf("-") > -1) {
	        				String ref = value.toString().split("-")[0];
	        				String dc = value.toString().split("-")[1];
	        				// Establece los valores en el bean.
	        				log ("buildCriteria", "Anade el filtro 'poliza = " + ref + "'");
	        				criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_POLIZA, ref));
	        				log ("buildCriteria", "Anade el filtro 'dc = " + dc + "'");
	        				criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_DC, new BigDecimal (dc)));
	        			}
	        			// Si no, solo se busca por el campo referencia
	        			else {
	        				log ("buildCriteria", "Anade el filtro 'poliza = " + value.toString() + "'");
	        				criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_POLIZA, value.toString()));
	        			}
	        		}
	        		// Dc
	        		else if (SiniestrosUtilidadesService.CAMPO_DC.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'dc = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_DC, value.toString()));
	        		}
	        		// CIF/NIF
	        		else if (SiniestrosUtilidadesService.CAMPO_NIF.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'nif = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_NIF, value.toString()));
	        		}
	        		// Asegurado
	        		else if (SiniestrosUtilidadesService.CAMPO_NOMBRE.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'asegurado = " + value.toString() + "'");
	        			//criteria.add(Restrictions.sqlRestriction("upper(nombre||' '||apellido1||' '||apellido2) like upper('%"+value.toString()+"%')"));
	        			criteria.add(Restrictions.sqlRestriction(" upper(NOMBRE) like upper('%"+value.toString()+"%')"));
	        			
	        		}
	        		// Riesgo
	        		else if (SiniestrosUtilidadesService.CAMPO_CODRIESGO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'codriesgo = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_CODRIESGO, value.toString()));
	        		}
	        		else if (SiniestrosUtilidadesService.CAMPO_NUMEROSINIESTRO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'numeroSiniestro = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_NUMEROSINIESTRO, new BigDecimal (value.toString())));
	        		}
	        		// Estado
	        		else if (SiniestrosUtilidadesService.CAMPO_IDESTADO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'idestado = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(SiniestrosUtilidadesService.CAMPO_IDESTADO, new BigDecimal (value.toString()) ));
	        		}
	        		if(SiniestrosUtilidadesService.CAMPO_LISTADOGRUPOOFI.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'listaGrupoOficinas= " + value.toString() + "'");
	        			criteria.add(Restrictions.in("oficina", CriteriaUtils.getCodigosListaOficina((List<BigDecimal>) value)));
	        		}
	        		// Listado de grupo de entidades
	        		if (SiniestrosUtilidadesService.CAMPO_LISTADOGRUPOENT.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'listaGrupoEntidades = " + value.toString() + "'");
	        			criteria.add(Restrictions.in("codentidad", (List<BigDecimal>) value));
	        		}
				}
			}
			
			catch (Exception e) {				
				log ("buildCriteria", "Ocurrio un error al anadir el filtro", e);
			}
        	
        	
        	
        }
	}
	
	public String getSqlInnerJoin(){
		//JANV 04/04/2106
		//Se ha modificado la vista siniestros utilidades por lo tanto el count no funciona.
		//se realiza el count desde la vista tb, no mediante joins.
		String sqlInnerJoin = "select count (*) from VW_SINIESTROS_UTILIDADES ";
		return sqlInnerJoin;
	}
	
	@SuppressWarnings("unchecked")
	public String getSqlWhere(){
		//JANV 04/04/2106
		//Se ha modificado la vista siniestros utilidades por lo tanto el count no funciona.
		//se realiza el count desde la vista tb, no mediante joins.
		//se eliminan los identificadores de tablas ya que todos los campos pertenecenecen a la misma (la vista).
		StringBuilder sqlWhere = new StringBuilder();		
		boolean filtroPlan = false;
		for (Filter filter : filters) {	
			if (filter.getValue() != null) {
				try {
						// entidad
		        		if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_ENTIDAD)){
		        			sqlWhere.append(" AND codentidad = '" + filter.getValue() + "'");
		        		}
		        		// oficina
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_OFICINA)){
							sqlWhere.append(" AND oficina in  "
									+ StringUtils.toValoresSeparadosXComas(
											CriteriaUtils.getCodigosOficina(filter
													.getValue().toString()), false,
											true));
		        		}
		        		// usuario
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_USUARIO)){
		        			sqlWhere.append(" AND codusuario = '" + filter.getValue() + "'");
		        		}
		        		// delegacion usu
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_DELEGACION)){
		        			sqlWhere.append(" AND delegacion = '" + filter.getValue() + "'");
		        		}
		        		// entidad mediadora
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_ENTMEDIADORA)){
		        			sqlWhere.append(" AND ENTMEDIADORA = '" + filter.getValue() + "'");
		        		}
		        		// subentidad mediadora
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_SUBENTMEDIADORA)){
		        			sqlWhere.append(" AND SUBENTMEDIADORA = '" + filter.getValue() + "'");
		        		}
		        		//linea
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_PLAN)){
		        			sqlWhere.append(" AND codplan = '" + filter.getValue() + "'");
		        			filtroPlan = true;
		        		}
		        		//linea
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_LINEA)){
		        			sqlWhere.append(" AND codlinea = '" + filter.getValue() + "'");
		        		}
		        		//poliza
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_POLIZA)){
		        			// Si la referencia de poliza tiene guion, se parte la cadena para buscar por referencia y dc
		        			if (filter.getValue().toString().indexOf("-") > -1) {
		        				String ref = filter.getValue().toString().split("-")[0];
		        				String dc = filter.getValue().toString().split("-")[1];
		        				// Establece los valores en el bean.
		        				sqlWhere.append("  AND referencia ='"  + ref+"'");
		        				sqlWhere.append("  AND dc ="  + dc);
		        			}
		        			// Si no, solo se busca por el campo referencia
		        			else {
		        				sqlWhere.append("  AND referencia ='"  + filter.getValue()+"'");
		        			}
		        		}
		        		// nif
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_NIF)){
		        			sqlWhere.append(" AND nifcif = '" + filter.getValue()+"'");
		        		}
		        		// codriesgo
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_CODRIESGO)){
		        			sqlWhere.append(" AND codriesgo = " + filter.getValue());
		        		}
		        		// idestado
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_IDESTADO)){
		        			sqlWhere.append(" AND idestado= " + filter.getValue());
		        		}
		        		//fecha ocurrencia
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_FEC_OCURRENCIA)){
		        			sqlWhere.append(" AND to_char(focurr, 'DD/MM/YYYY')= '" + filter.getValue()+"'");
		        		}
		        		// fecha firma
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_FEC_FIRMA)){
		        			sqlWhere.append(" AND to_char(ffirma, 'DD/MM/YYYY')= '" + filter.getValue()+"'");
		        		}
		        		//fecha envio
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO)){		        			
		        			sqlWhere.append(" and to_char(fenv, 'DD/MM/YYYY') = '"+filter.getValue()+ "'");
		        		}
		        		//fecha envio pol
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_FEC_ENVIO_POLIZA)){
		        			sqlWhere.append(" AND to_char(fenvpol, 'DD/MM/YYYY') = '" + filter.getValue()+"'");
		        		}
		        		//nombre 
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_NOMBRE)){
		        			//DAA 06/11/2012
		        			sqlWhere.append(" AND (upper(NOMBRE) like upper('%"+filter.getValue()+"%'))");
		        			
		        		}
		        		// grupo de entidades
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_LISTADOGRUPOENT)){
		        			sqlWhere.append(" AND codentidad IN " + StringUtils.toValoresSeparadosXComas((List<Entidad>)filter.getValue(), false));
		        		}
		        		// grupo de oficinas
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_LISTADOGRUPOOFI)){
							sqlWhere.append(" AND oficina IN "
									+ StringUtils.toValoresSeparadosXComas(
											CriteriaUtils
													.getCodigosListaOficina((List<BigDecimal>) filter
															.getValue()), false,
											true));
		        		}
		        		
		        		//numAviso 
		        		else if (filter.getProperty().equals(SiniestrosUtilidadesService.CAMPO_NUMEROSINIESTRO)){
		        			sqlWhere.append(" AND numerosiniestro= " + filter.getValue());
		        		}
		        		
		        }catch (Exception e) {
					logger.error("CamposPermitidosFilter - "+e.getMessage());
				}
			}	
        }
		
		// Si no se ha filtrado por plan, se introduce el filtro por defecto para este campo
		if (!filtroPlan) {
			// Obtiene el ano actual y el pasado
			GregorianCalendar gc = new GregorianCalendar();
			BigDecimal anyoActual = new BigDecimal(gc.get(Calendar.YEAR));
			gc.add(Calendar.YEAR, -1);
			BigDecimal anyoPasado = new BigDecimal(gc.get(Calendar.YEAR));

			// Anade los anos a la condicion de busqueda
			sqlWhere.append(" and codplan in (" + anyoActual + ", " + anyoPasado + ")");
			log ("buildCondicionPlan", "Anadidos el ano actual y el pasado al filtro de plan");
		}
		
		return sqlWhere.toString();
	}
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("SiniestrosFilter." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("SiniestrosFilter." + method + " - " + msg, e);
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