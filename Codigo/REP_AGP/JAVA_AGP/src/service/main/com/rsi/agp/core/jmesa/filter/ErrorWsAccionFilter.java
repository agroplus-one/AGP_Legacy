package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.webapp.util.StringUtils;

public class ErrorWsAccionFilter implements CriteriaCommand{

	private static final String OCULTAR = "ocultar";
	private static final String SERVICIO = "servicio";
	private static final String ENTIDAD_CODENTIDAD = "entidad.codentidad";
	private static final String ERROR_WS_DESCRIPCION = "errorWs.descripcion";
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
	private boolean filtroForzar = false;
	
	@Override
	public Criteria execute(Criteria criteria) {
		boolean hayCodError = false;
		for (Filter filter : filters) {
			if (filter.getProperty().equals("errorWs.id.coderror")){
				hayCodError = true;
			} else if (filter.getProperty().equals("codErrorPerfiles")) {
				this.filtroForzar = true;
			}
		}
		for (Filter filter : filters) {
			if (hayCodError && (filter.getProperty().equals(ERROR_WS_DESCRIPCION)
						||filter.getProperty().equals("errorWs.errorWsTipo.descripcion"))){
				logger.info("No añadimos el filtro");
			}else{
				buildCriteria(criteria, filter.getProperty(), filter.getValue());
			}
            
        }

        return criteria;
	}
	
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null || ENTIDAD_CODENTIDAD.equals(property)) {        	       	       	
        	Class<?> tipo;        	
        	try {
        		Class<?> c = Class.forName("com.rsi.agp.dao.tables.commons.ErrorWsAccion");
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
	        		// Plan
	        		if ("linea.codplan".equals(property) ){
	        			
	        			Disjunction disjunction = Restrictions.disjunction();
	        			disjunction.add(Restrictions.isNull("lin.codplan"));
	        			disjunction.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));

	        			criteria.add(disjunction);
	        			
	        		}
	        		// Linea
	        		else if ("linea.codlinea".equals(property)){
	        			
	        			Disjunction disjunction = Restrictions.disjunction();
	        			disjunction.add(Restrictions.isNull("lin.codlinea"));
	        			disjunction.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));

	        			criteria.add(disjunction);

	        		}
	        		/* Pet. 63481 ** MODIF TAM (13/05/2021) ** Inicio */
	        		//catalogo
	        		else if ("errorWs.id.catalogo".equals(property) && value != null){
	        			criteria.add(Restrictions.eq("error.id.catalogo", value.toString()));
	        		}
	        		/* Pet. 63481 ** MODIF TAM (13/05/2021) ** Fin */
	        		// Coderror
	        		else if ("errorWs.id.coderror".equals(property) && value != null){
	        			criteria.add(Restrictions.eq("error.id.coderror", new BigDecimal(value.toString())));
	        		}
	        		// Descripcion
	        		else if (ERROR_WS_DESCRIPCION.equals(property)){
	        			criteria.add(Restrictions.ilike("error.descripcion", "%"+value.toString()+"%"));
	        		}
	        		// Descripcion_Tipo
	        		else if ("errorWs.errorWsTipo.codigo".equals(property)){
	        			criteria.add(Restrictions.ilike("ErrTipo.codigo", "%"+value.toString()+"%"));
	        		}
	        		// Servicio
	        		else if (SERVICIO.equals(property)){
	        			criteria.add(Restrictions.eq(SERVICIO, value.toString()));
	        		}
	        		// Ocultar
	        		else if (OCULTAR.equals(property)){
	        			criteria.add(Restrictions.eq(OCULTAR, value.toString()));
	        		}
	        		//Entidad
	        		else if (ENTIDAD_CODENTIDAD.equals(property)) {
	        			if(value != null) {
	        				criteria.add(Restrictions.eq(ENTIDAD_CODENTIDAD, new BigDecimal(value.toString())));
	        			}
	        			else {
	        				criteria.add(Restrictions.isNull(ENTIDAD_CODENTIDAD));
	        			}
	        		}
	        		// Forzar
					else if ("codErrorPerfiles".equals(property)){
						if(value.toString().startsWith("[") && value.toString().endsWith("]")) {
							String val = value.toString().replace("[", "").replace("]", "").replace(" ", "");
							if (!StringUtils.isNullOrEmpty(val)) {
								criteria.add(Restrictions.in("errorPerfil.perfil.id", StringUtils.asListBigDecimal(val, ",")));
							}
						} else {
							criteria.add(
									Restrictions.in("errorPerfil.perfil.id", StringUtils.asListBigDecimal(value.toString(), ",")));
						}
	        		}
	        	}
			}catch (Exception e) {
				logger.error("ErrorWSAccionFilter - "+e.getMessage());
			}     	
        }
    }
	
	/** DAA 13/02/2013 Añade el filtro de ErrorWs para la lista de Ids.
	 * 
	 * @param ErrorWsAccionFilter
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere= "WHERE 1=1 AND E.CODERROR = C.CODERROR " +
						 "AND C.CATALOGO = E.CATALOGO " +
						 "AND C.TIPOERROR = T.CODIGO " +
						 "AND E.LINEASEGUROID = L.LINEASEGUROID (+)" + 
						 "AND E.ID = EP.IDERRORACCION (+)";
		try {
			sqlWhere = addSqlFilters(sqlWhere);
		}catch (Exception e) {
			logger.error("ClaseDetalleFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
		}  
		return sqlWhere;
	}

	/**
	 * Añade los filtros necesarios a la clausula where de la consulta
	 * @param sqlWhere - clausuala where preformada
	 * @return sqlWhere con los filtros requeridos
	 */
	private String addSqlFilters(String sqlWhere) {
		for (Filter filter : filters) {
			String property = filter.getProperty(); 
			
			//Servicio
			if (SERVICIO.equals(property)){
				sqlWhere += " AND E.SERVICIO = '"+ filter.getValue()+"'";
			}
			
			/* Pet. 63481 ** MODIF TAM (13/05/2021) ** Inicio */
			// Catálogo
			if ("errorWs.id.catalogo".equals(property)){
				sqlWhere += " AND C.CATALOGO = '"+ filter.getValue()+"'";
			}
			/* Pet. 63481 ** MODIF TAM (13/05/2021) ** Fin */
			
			//Descripcion Tipo
			if ("errorWs.errorWsTipo.codigo".equals(property)){
				sqlWhere += " AND T.CODIGO = '" + filter.getValue()+"'";
			}
			//Descripcion Error
			if (ERROR_WS_DESCRIPCION.equals(property)){
				sqlWhere += " AND C.DESCRIPCION = '" +  filter.getValue()+"'";
			}
			//CodLinea
			if ("linea.codlinea".equals(property)){
				sqlWhere += " AND L.CODLINEA (+) = " + filter.getValue();
			}
			//CodPlan
			if ("linea.codplan".equals(property)){
				sqlWhere += " AND L.CODPLAN (+) = " + filter.getValue();
			}
			//CodError 
			if ("errorWs.id.coderror".equals(property)){
				sqlWhere += " AND C.CODERROR = " + filter.getValue();
			}
			//Ocultar
			if (OCULTAR.equals(property)){
				sqlWhere += " AND E.OCULTAR = '" + filter.getValue()+"'";
			}
			//Entidad
			if (ENTIDAD_CODENTIDAD.equals(property)){
				sqlWhere += " AND E.CODENTIDAD = " + filter.getValue();
			}
			//Forzar
			if ("codErrorPerfiles".equals(property)){
				String val = filter.getValue().toString().replace("[", "").replace("]", "").replace(" ", "");
				if (!StringUtils.isNullOrEmpty(val)) {
					sqlWhere += " AND EP.IDPERFIL IN (" + val + ")";
				}
			}
			
		}
		return sqlWhere;
	}
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }
	
	public boolean isFiltroForzar() {
		return filtroForzar;
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
