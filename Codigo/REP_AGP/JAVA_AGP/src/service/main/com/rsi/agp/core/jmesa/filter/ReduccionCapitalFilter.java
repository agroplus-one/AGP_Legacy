package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.utilidades.ReduccionCapitalUtilidadesService;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.util.WSUtils;

public class ReduccionCapitalFilter implements CriteriaCommand {
	
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
		
		// Anade los anos a la condicion de búsqueda
		criteria.add(Restrictions.disjunction().add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_PLAN, anyoActual))
											   .add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_PLAN, anyoPasado)));
		
		log ("buildCondicionPlan", "Anadidos el ano actual y el pasado al filtro de plan");
	}
	
	
	/**
	 * Carga el objeto Criteria con las condiciones de búsqueda referentes al filtro indicado por 'property,valor'
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		//Para comprobar que el valor tiene formato de fecha
    	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
    	Matcher dateMatcher = datePattern.matcher(value+"");
		
		if (value != null) {
        	
			try {			    
				
				if (dateMatcher.find()) {
					
					//P0079361
					logger.debug("El campo para la construccion del criteria es de tipo Date");
					/*
					
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
        			criteria.add(Restrictions.lt(property, fechaMas24));*/
					
					/*GregorianCalendar gcDatoInforForm = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
	    					Integer.parseInt(dateMatcher.group(2))-1, 
	    					Integer.parseInt(dateMatcher.group(1)));
					
        			//ORDENADO
        			if(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS_HASTA.equals(property)) {
						criteria.add(Restrictions.le(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS, gcDatoInforForm.getTime()));
					}
        			
        			if(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA.equals(property)) {
        				criteria.add(Restrictions.ge(property, gcDatoInforForm.getTime()));
					}
        			
        			if(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO.equals(property)) {
        				criteria.add(Restrictions.ge(property, gcDatoInforForm.getTime()));
					}
					
					if(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA_HASTA.equals(property)) {
						criteria.add(Restrictions.le(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_POLIZA, gcDatoInforForm.getTime()));
					}
					
					if(ReduccionCapitalUtilidadesService.CAMPO_FEC_DANIOS.equals(property)) {
	        			criteria.add(Restrictions.ge(property, gcDatoInforForm.getTime()));
					}
						
					if(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO_HASTA.equals(property)) {
						criteria.add(Restrictions.le(ReduccionCapitalUtilidadesService.CAMPO_FEC_ENVIO, gcDatoInforForm.getTime()));
					}*/
					//ORDENADO	
        			//P0079361
					
				}
				else {
				
	        		// Entidad
	        		if (ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'entidad = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD, new BigDecimal(value.toString())));
	        		}
	        		// Oficina
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_OFICINA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'oficina = " + CriteriaUtils.getCodigosOficina(value.toString()) + "'");
	        			criteria.add(Restrictions.in(ReduccionCapitalUtilidadesService.CAMPO_OFICINA,CriteriaUtils.getCodigosOficina(value.toString())));
	        		}
	        		// Usuario
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_USUARIO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'usuario = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_USUARIO, value.toString()));
	        		}
	        		//delegacion
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_DELEGACION.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'delegacion = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_DELEGACION, value.toString()));
	        		}
	        		//entidad mediadora
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_ENTMEDIADORA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'entidad mediadora = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_ENTMEDIADORA, new BigDecimal(value.toString())));
	        		}
	        		//subentidad mediadora
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_SUBENTMEDIADORA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'subentidad mediadora = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_SUBENTMEDIADORA,new BigDecimal (value.toString())));
	        		}
	        		// Plan
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_PLAN.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'plan = " + value.toString() + "'");
	        			filtroPlan = true;
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_PLAN, new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_LINEA.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'linea = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_LINEA, new BigDecimal(value.toString())));
	        		}
	        		// Poliza
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_POLIZA.equals(property)) {
	        			
	        			// Si la referencia de poliza tiene guion, se parte la cadena para buscar por referencia y dc
	        			if (value.toString().indexOf("-") > -1) {
	        				String ref = value.toString().split("-")[0];
	        				String dc = value.toString().split("-")[1];
	        				// Establece los valores en el bean.
	        				log ("buildCriteria", "Anade el filtro 'poliza = " + ref + "'");
	        				criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_POLIZA, value.toString()));
	        				log ("buildCriteria", "Anade el filtro 'dc = " + dc + "'");
	        				criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_DC, new BigDecimal (dc)));
	        			}
	        			// Si no, solo se busca por el campo referencia
	        			else {
	        				log ("buildCriteria", "Anade el filtro 'poliza = " + value.toString() + "'");
	        				criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_POLIZA, value.toString()));
	        			}
	        		}
	        		// Dc
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_DC.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'dc = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_DC, value.toString()));
	        		}
	        		// CIF/NIF
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_NIF.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'nif = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_NIF, value.toString()));
	        		}
	        		// Asegurado
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_NOMBRE.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'asegurado = " + value.toString() + "'");
	        			criteria.add(Restrictions.sqlRestriction("nombre like upper('%"+value.toString()+"%')"));
	        			
	        		}
	        		// Riesgo
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_CODRIESGO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'codriesgo = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_CODRIESGO, value.toString()));
	        		}
	        		// Estado
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_IDESTADO.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'idestado = " + value.toString() + "'");
	        			criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_IDESTADO, new BigDecimal (value.toString()) ));
	        		}        		
	        		// Listado de grupo de entidades
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_LISTADOGRUPOENT.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'listaGrupoEntidades = " + value.toString() + "'");
	        			criteria.add(Restrictions.in(ReduccionCapitalUtilidadesService.CAMPO_ENTIDAD, (List<?>) value));
	        		}
	        		// Listado de grupo de oficinas
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_LISTADOGRUPOOFI.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'listaGrupoOficinas = " + value.toString() + "'");
	        			criteria.add(Restrictions.in(ReduccionCapitalUtilidadesService.CAMPO_OFICINA, CriteriaUtils.getCodigosListaOficina((List<?>) value)));
	        		}	
	        		//TIPO ENVIO ID
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_NUMEROCUPON.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'idcupon = " + value.toString() + "'");
	        			if(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP.equals(value.toString().trim().toUpperCase())) {
	        				criteria.add(Restrictions.isNull(ReduccionCapitalUtilidadesService.CAMPO_NUMEROCUPON));
	        			}else {
	        				criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_NUMEROCUPON, value.toString()));	        				
	        			}
	        		}	
	        		//ESTADO CUPON ID
	        		else if (ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON.equals(property)) {
	        			log ("buildCriteria", "Anade el filtro 'estado = " + value.toString() + "'");
	        			//String idRedCapEstado = WSUtils.obtenerCodEstadoCuponByNumber(value.toString());
	        			//ojo al trim por CHAR(25) en bbdd
	        			//criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, value.toString()+"%"));
	        			/*if(!value.toString().isEmpty() && Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S.equals(value.toString())) {
	        				criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S+"%"));
	        			} else if(!value.toString().isEmpty() && Constants.AM_CUPON_ESTADO_ERROR_TRAMITE_S.equals(value.toString())) {
	        				criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_ERROR_TRAMITE_S+"%"));
	        			} else  */
	        			if (!value.toString().isEmpty() && Constants.AM_CUPON_ESTADO_ERROR_S.equals(value.toString())){
	        				criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON,Constants.AM_CUPON_ESTADO_ERROR_S+"%"));
	        				criteria.add(Restrictions.not(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO_S+"%")));
	        				criteria.add(Restrictions.not(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_ERROR_TR_S+"%")));
	        			} else if(!value.toString().isEmpty() && value.toString().contains(Constants.AM_CUPON_ESTADO_ERROR_TR_S)) {
	        				criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_ERROR_TR_S+"%"));
	        			} else if(!value.toString().isEmpty() && value.toString().contains(Constants.AM_CUPON_ESTADO_CONFIRMADO_TR_S)) {
	        				criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, Constants.AM_CUPON_ESTADO_CONFIRMADO_TR_S+"%"));
						}else{
							criteria.add(Restrictions.like(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, value.toString()+"%"));

						}
	        			//criteria.add(Restrictions.eq(ReduccionCapitalUtilidadesService.CAMPO_ESTADOCUPON, value.toString()));
	        		}
				}
			}
			
			catch (Exception e) {				
				log ("buildCriteria", "Ocurrio un error al anadir el filtro", e);
			}
        	
        	
        	
        }
	}
	
	
	/**
	 * Escribe en el log indicando la clase y el metodo.
	 * @param method
	 * @param msg
	 */
	private void log (String method, String msg) {
		logger.debug("ReduccionCapitalFilter." + method + " - " + msg);
	}
	
	/**
	 * Escribe en el log indicando la clase, el metodo y la excepcion.
	 * @param method
	 * @param msg
	 * @param e
	 */
	private void log (String method, String msg, Throwable e) {
		logger.error("ReduccionCapitalFilter." + method + " - " + msg, e);
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
