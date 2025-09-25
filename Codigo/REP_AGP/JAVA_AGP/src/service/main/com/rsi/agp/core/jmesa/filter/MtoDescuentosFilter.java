package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.service.impl.MtoDescuentosService;
//import com.rsi.agp.core.jmesa.service.impl.utilidades.SiniestrosUtilidadesService;


public class MtoDescuentosFilter  implements CriteriaCommand {

	// Constantes
	private static final String FECHA_BAJA_CAMBIO_MASIVO = "fechaBajaCambioMasivo";
	private static final String FECHA_BAJA = "fechaBaja";
	private static final String LINEA_CODLINEA = "linea.codlinea";
	private static final String LINEA_CODPLAN = "linea.codplan";
	private static final String VER_COMISIONES = "verComisiones";
	private static final String PERMITIR_RECARGO = "permitirRecargo";
	private static final String PCT_DESC_MAX = "pctDescMax";
	private static final String DELEGACION = "delegacion";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String OFICINA_ID_CODOFICINA = "oficina.id.codoficina";
	private static final String SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD = "subentidadMediadora.entidad.codentidad";
	
	private List<Filter> filters = new ArrayList<Filter>();
	private final Log  logger = LogFactory.getLog(getClass());	
		
	
	public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }

	@Override
	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
            buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return criteria;
	}
	
	public String getSqlWhere() {
		/*String sqlWhere= "WHERE 1=1 AND D.LINEASEGUROID = L.LINEASEGUROID AND D.CODENT = O.CODENTIDAD " +
				"AND D.CODOFICINA = O.CODOFICINA AND D.CODENTMED = E.CODENTIDAD AND D.CODSUBENTMED = E.CODSUBENTIDAD";*/
		/* "SELECT D.ID FROM TB_COMS_DESCUENTOS D , TB_SUBENTIDADES_MEDIADORAS S, TB_ENTIDADES E, TB_LINEAS L " */
		
		String sqlWhere= "WHERE 1=1 AND D.LINEASEGUROID = L.LINEASEGUROID AND D.CODENTMED = S.CODENTIDAD AND " +
				"D.CODSUBENTMED = S.CODSUBENTIDAD AND S.CODENTIDADNOMEDIADORA = E.CODENTIDAD";
		
		   try {
			for (Filter filter : filters) {
				String property = filter.getProperty(); 
				// codentidad
			    if (property.equals(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD)){
			    	sqlWhere += " AND D.CODENT = '"+ filter.getValue()+"'";
					
				} 
				// codoficina
				else if (property.equals(OFICINA_ID_CODOFICINA)){
					sqlWhere += " AND D.CODOFICINA = '"+ filter.getValue()+"'";
				} 
			    
			 // esMediadora
				else if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD)){
					sqlWhere += " AND D.CODENTMED = '"+ filter.getValue()+"'";
				}
				// codsubentidad
				else if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD)){
					sqlWhere += " AND D.CODSUBENTMED = '"+ filter.getValue()+"'";
				} 
				// delegacion
				else if (property.equals(DELEGACION)){
					sqlWhere += " AND D.DELEGACION = '"+ filter.getValue()+"'";
				}
				// externo
				else if (property.equals(PCT_DESC_MAX)){
					sqlWhere += " AND D.PCT_DESC_MAX = "+ filter.getValue();
				} 
			    //Permitir recargo			    
				else if (property.equals(PERMITIR_RECARGO)){
					sqlWhere += " AND D.PERMITIR_RECARGO = '"+ filter.getValue()+"'";		
				} 
			    //Ver comisiones		    
				else if (property.equals(VER_COMISIONES)){
					sqlWhere += " AND D.VER_COMISIONES = '"+ filter.getValue()+"'";					
				}	
			    //Plan
				else if (property.equals(LINEA_CODPLAN)){
					sqlWhere += " AND L.CODPLAN = '"+ filter.getValue()+"'";					
				}
			    //Línea
				else if (property.equals(LINEA_CODLINEA)){
					sqlWhere += " AND L.CODLINEA = '"+ filter.getValue()+"'";					
				}
			    //Fecha de baja
				else if (property.equals(FECHA_BAJA)){
					if(null!= filter.getValue()) {
						sqlWhere += " AND to_char(D.FECHA_BAJA, 'DD/MM/YYYY')= '" + filter.getValue()+ "'";							
					}		
				}
			    //Fecha de baja para cambio masivo (1)
				else if(property.equals(FECHA_BAJA_CAMBIO_MASIVO)) {
					sqlWhere += " AND D.FECHA_BAJA is null";		
				}
			}
		}catch (Exception e) {
			logger.error("MtoDescuentosFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
		}  
		return sqlWhere;
	}

	
	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro indicado por 'property,valor'
	 * U029769
	 * @param criteria
	 * @param property
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		if (value != null) {
			try {			    
				
				// codentidad
			    if (property.equals(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD) && value != null){
					criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, new BigDecimal(value.toString())));
				} 
				// codoficina
				else if (property.equals(OFICINA_ID_CODOFICINA) && value != null){
					criteria.add(Restrictions.eq(OFICINA_ID_CODOFICINA,  new BigDecimal(value.toString())));
				} 
				// esMediadora
				else if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD) && value != null){
					criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD, new BigDecimal(value.toString())));
				}
				// codsubentidad
				else if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD) && value != null){
					criteria.add(Restrictions.eq(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD, new BigDecimal(value.toString())));
				} 
				// delegacion
				else if (property.equals(DELEGACION) && value != null){
					criteria.add(Restrictions.eq(DELEGACION, new BigDecimal(value.toString())));
				}
				// externo
				else if (property.equals(PCT_DESC_MAX) && value != null){
					criteria.add(Restrictions.eq(PCT_DESC_MAX, new BigDecimal(value.toString())));
				} 
			    //Permitir recargo			    
				else if (property.equals(PERMITIR_RECARGO) && value != null){
					Integer recargo;
					recargo = new Integer(value.toString());//(Integer)value;
					criteria.add(Restrictions.eq(PERMITIR_RECARGO, recargo));					
				} 
			    //Ver comisiones		    
				else if (property.equals(VER_COMISIONES) && value != null){
					Integer verComis;
					verComis = new Integer(value.toString());//(Integer)value;
					criteria.add(Restrictions.eq(VER_COMISIONES, verComis));					
				}	
			    //Plan
				else if (property.equals(LINEA_CODPLAN) && value != null){
					BigDecimal codplan;
					codplan = new BigDecimal(value.toString());//(Integer)value;
					criteria.add(Restrictions.eq(LINEA_CODPLAN, codplan));					
				}
			    //Línea
				else if (property.equals(LINEA_CODLINEA) && value != null){
					BigDecimal codlinea;
					codlinea = new BigDecimal(value.toString());//(Integer)value;
					criteria.add(Restrictions.eq(LINEA_CODLINEA, codlinea));					
				}
			    // Listado de grupo de entidades
        		if (MtoDescuentosService.CAMPO_LISTADOGRUPOENT.equals(property)) {
        			criteria.add(Restrictions.in(SUBENTIDAD_MEDIADORA_ENTIDAD_CODENTIDAD, (List<BigDecimal>) value));
        		}
        		        		
			}catch (Exception e) {
				logger.error("MtoUsuariosFilter - "+ e.getMessage());
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
