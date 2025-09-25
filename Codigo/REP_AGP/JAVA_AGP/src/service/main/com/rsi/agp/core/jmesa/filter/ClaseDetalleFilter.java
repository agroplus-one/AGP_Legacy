package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;


public class ClaseDetalleFilter implements CriteriaCommand{

	// Constantes
	private static final String TIPO_PLANTACION_CODTIPOPLANTACION = "tipoPlantacion.codtipoplantacion";
	private static final String TIPO_CAPITAL_CODTIPOCAPITAL = "tipoCapital.codtipocapital";
	private static final String SUBTERMINO = "subtermino";
	private static final String CODTERMINO = "codtermino";
	private static final String CODCOMARCA = "codcomarca";
	private static final String CODPROVINCIA = "codprovincia";
	private static final String VARIEDAD_ID_CODVARIEDAD = "variedad.id.codvariedad";
	private static final String CULTIVO_ID_CODCULTIVO = "cultivo.id.codcultivo";
	private static final String SISTEMA_CULTIVO_CODSISTEMACULTIVO = "sistemaCultivo.codsistemacultivo";
	private static final String CICLO_CULTIVO_CODCICLOCULTIVO = "cicloCultivo.codciclocultivo";
	private static final String CODMODULO = "codmodulo";
	private static final String CLASE_ID = "clase.id";
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
	
	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	@SuppressWarnings("rawtypes")
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
        	Class tipo;
        	
        	try {
				Class c = Class.forName("com.rsi.agp.dao.tables.admin.ClaseDetalle");
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
				if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        	else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
	        	else{
	        		// Id
	        		if (property.equals(CLASE_ID) ){
	        			criteria.add(Restrictions.eq(CLASE_ID,new Long(value.toString())));
	        		}
	        		// Modulo
	        		if (property.equals(CODMODULO) ){
	        			criteria.add(Restrictions.eq(CODMODULO, value.toString()));
	        		}
	        		// Ciclo Cultivo
	        		else if (property.equals(CICLO_CULTIVO_CODCICLOCULTIVO)){
	        			criteria.add(Restrictions.eq(CICLO_CULTIVO_CODCICLOCULTIVO, new BigDecimal(value.toString())));
	        		}
	        		// Sistema Cultivo
	        		else if (property.equals(SISTEMA_CULTIVO_CODSISTEMACULTIVO)){
	        			criteria.add(Restrictions.eq(SISTEMA_CULTIVO_CODSISTEMACULTIVO, new BigDecimal(value.toString())));
	        		}
	        		// Cultivo
	        		else if (property.equals(CULTIVO_ID_CODCULTIVO)){
	        			criteria.add(Restrictions.eq(CULTIVO_ID_CODCULTIVO, new BigDecimal(value.toString())));
	        		}
	        		// Variedad
	        		else if (property.equals(VARIEDAD_ID_CODVARIEDAD)){
	        			criteria.add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, new BigDecimal(value.toString())));
	        		}
	        		// Provincia
	        		else if (property.equals(CODPROVINCIA)){
	        			criteria.add(Restrictions.eq(CODPROVINCIA, new BigDecimal(value.toString())));
	        		}
	        		// Comarca
	        		else if (property.equals(CODCOMARCA)){
	        			criteria.add(Restrictions.eq(CODCOMARCA, new BigDecimal(value.toString())));
	        		}
	        		// Termino
	        		else if (property.equals(CODTERMINO)){
	        			criteria.add(Restrictions.eq(CODTERMINO, new BigDecimal(value.toString())));
	        		}
	        		// SubTermino
	        		else if (property.equals(SUBTERMINO)){
	        			criteria.add(Restrictions.eq(SUBTERMINO, value.toString()));
	        		}
	        		
	        		// Tipo Capital
	        		else if (property.equals(TIPO_CAPITAL_CODTIPOCAPITAL)){
	        			criteria.add(Restrictions.eq(TIPO_CAPITAL_CODTIPOCAPITAL, new BigDecimal(value.toString())));
	        		}
	        		// Tipo Plantación
	        		else if (property.equals(TIPO_PLANTACION_CODTIPOPLANTACION)){
	        			criteria.add(Restrictions.eq(TIPO_PLANTACION_CODTIPOPLANTACION, new BigDecimal(value.toString())));
	        		}
        	}
			}catch (Exception e) {
				logger.error("ClaseDetalleFilter - "+e.getMessage());
			}     	
        }
    }
	
	/** DAA 06/02/2013 Añade el filtro de clase de talle para la lista de Ids.
	 * 
	 * @param claseDetalleBusqueda
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere= "WHERE 1=1";
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty(); 
				
				//Id
				if (property.equals(CLASE_ID)){
					sqlWhere += " AND D.IDCLASE = "+ filter.getValue();
				}
				//Modulo
				if (property.equals(CODMODULO)){
					sqlWhere += " AND D.CODMODULO = '" + filter.getValue()+"'";
				}
				//Ciclo cultivo
				if (property.equals(CICLO_CULTIVO_CODCICLOCULTIVO)){
					sqlWhere += " AND D.CODCICLOCULTIVO = " +  filter.getValue();
				}
				//Sist cultivo
				if (property.equals(SISTEMA_CULTIVO_CODSISTEMACULTIVO)){
					sqlWhere += " AND D.CODSISTEMACULTIVO = " + filter.getValue();
				}
				//Cultivo
				if (property.equals(CULTIVO_ID_CODCULTIVO)){
					sqlWhere += " AND D.CODCULTIVO = " + filter.getValue();
				}
				//Variedad 
				if (property.equals(VARIEDAD_ID_CODVARIEDAD)){
					sqlWhere += " AND D.CODVARIEDAD = " + filter.getValue();
				}
				//Tipo Capital
				if (property.equals(TIPO_CAPITAL_CODTIPOCAPITAL)){
					sqlWhere += " AND D.CODTIPOCAPITAL = " + filter.getValue();
				}
				//Provincia
				if (property.equals(CODPROVINCIA)){
					sqlWhere += " AND D.CODPROVINCIA = " + filter.getValue();
				}
				//Comarca
				if (property.equals(CODCOMARCA)){
					sqlWhere += " AND D.CODCOMARCA = " + filter.getValue();
				}
				//Termino
				if (property.equals(CODTERMINO)){
					sqlWhere += " AND D.CODTERMINO = " + filter.getValue();
				}
				//Subtermino
				if (property.equals(SUBTERMINO)){
					sqlWhere += " AND D.SUBTERMINO = '" + filter.getValue()+"'";
				}
				//Tipo plnatacion
				if (property.equals(TIPO_PLANTACION_CODTIPOPLANTACION)){
					sqlWhere += " AND D.CODTIPOPLANTACION = " + filter.getValue();
				}
			}
		}catch (Exception e) {
			logger.error("ClaseDetalleFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
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
