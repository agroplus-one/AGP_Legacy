package com.rsi.agp.core.jmesa.filter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

/**
 * @author U029769
 *
 */
public class MtoUsuariosFilter implements CriteriaCommand {

	private static final String FECHA_LIMITE = "fechaLimite";
	private static final String IMP_MAX_FINANCIACION = "impMaxFinanciacion";
	private static final String IMP_MIN_FINANCIACION = "impMinFinanciacion";
	private static final String FINANCIAR = "financiar";
	private static final String CARGA_PAC = "cargaPac";
	private static final String EXTERNO = "externo";
	private static final String DELEGACION = "delegacion";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD = "subentidadMediadora.id.codsubentidad";
	private static final String SUBENTIDAD_MEDIADORA_ID_CODENTIDAD = "subentidadMediadora.id.codentidad";
	private static final String OFICINA_ID_CODOFICINA = "oficina.id.codoficina";
	private static final String OFICINA_ID_CODENTIDAD = "oficina.id.codentidad";
	private static final String TIPOUSUARIO = "tipousuario";
	private static final String NOMBREUSU = "nombreusu";
	private static final String CODUSUARIO = "codusuario";
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
	/**
	 * Carga el objeto Criteria con las condiciones de busqueda referentes al filtro indicado por 'property,valor'
	 * 08/05/2014 U029769
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
		
		if (value != null) {
			try {			    
				// codUsuario
				if (property.equals(CODUSUARIO) && value != null){
					criteria.add(Restrictions.eq(CODUSUARIO, value.toString()));
				}
				// nombre usu
				else if (property.equals(NOMBREUSU) && value != null){
					Criterion ilike = Restrictions.like(NOMBREUSU, value.toString().trim(), MatchMode.START);
					criteria.add(Restrictions.disjunction().add(ilike));
					
					//criteria.add(Restrictions.sqlRestriction("nombreusu like upper('%"+value.toString()+"%')"));
					//criteria.add(Restrictions.like("nombreusu", "%"+value.toString()+"%"));
				}
				// TIPOUSUARIO
				else if (property.equals(TIPOUSUARIO) && value != null && !value.equals("")){
					criteria.add(Restrictions.eq(TIPOUSUARIO, new BigDecimal(value.toString())));
				} 
				// codentidad
				else if (property.equals(OFICINA_ID_CODENTIDAD) && value != null){
					criteria.add(Restrictions.eq(OFICINA_ID_CODENTIDAD, new BigDecimal(value.toString())));
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
				else if (property.equals(EXTERNO) && value != null){
					criteria.add(Restrictions.eq(EXTERNO, new BigDecimal(value.toString())));
				} 
				// carga pac
				else if (property.equals(CARGA_PAC) && value != null){
					criteria.add(Restrictions.eq(CARGA_PAC, new BigDecimal(value.toString())));
				}
				// e-mail
				else if (property.equals("email") && value != null){
					Criterion ilike = Restrictions.like("email", value.toString().trim(), MatchMode.START);
					criteria.add(Restrictions.disjunction().add(ilike));
				}
				// financiar
				else if (property.equals(FINANCIAR) && value != null) {
					criteria.add(Restrictions.eq(FINANCIAR, new BigDecimal(value.toString())));
				}
				// importe minimo
				else if (property.equals(IMP_MIN_FINANCIACION) && value != null) {
					criteria.add(Restrictions.eq(IMP_MIN_FINANCIACION, new BigDecimal(value.toString())));
				}
				// importe maximo
				else if (property.equals(IMP_MAX_FINANCIACION) && value != null) {
					criteria.add(Restrictions.eq(IMP_MAX_FINANCIACION, new BigDecimal(value.toString())));
				}
				// fecha limite
				else if (property.equals(FECHA_LIMITE) && value != null) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					Date fechaLimite = df.parse(value.toString());
					criteria.add(Restrictions.eq(FECHA_LIMITE, fechaLimite));
				}
			}
			catch (Exception e) {
				logger.error("MtoUsuariosFilter - "+ e.getMessage());
			}
		}
	}
	
	/** Añade el filtro de Usuario para la lista de Ids.
	 * 
	 * @param MtoUsuariosFilter
	 * @return sqlWhere
	 */
	public String getSqlWhere() {
		String sqlWhere= "WHERE 1=1 ";
		try {
			for (Filter filter : filters) {
				String property = filter.getProperty();
				
				// codUsuario
				if (property.equals(CODUSUARIO)){
					sqlWhere += " AND U.codusuario = '"+ filter.getValue()+"'";
				}
				// nombre usu
				if (property.equals(NOMBREUSU)){
					sqlWhere += " AND U.nombreusu like '" + filter.getValue()+"%'";
				}
				// TIPOUSUARIO
				if (property.equals(TIPOUSUARIO)){
					sqlWhere += " AND U.tipousuario = '" +  filter.getValue()+"'";
				}
				// codentidad
				if (property.equals(OFICINA_ID_CODENTIDAD)){
					sqlWhere += " AND U.codentidad = " + filter.getValue();
				}
				// codoficina
				if (property.equals(OFICINA_ID_CODOFICINA)){
					sqlWhere += " AND U.codoficina = " + filter.getValue();
				}
				// entMediadora 
				if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODENTIDAD)){
					sqlWhere += " AND U.entmediadora = " + filter.getValue();
				}
				// codsubentidad
				if (property.equals(SUBENTIDAD_MEDIADORA_ID_CODSUBENTIDAD)){
					sqlWhere += " AND U.subentmediadora = '" + filter.getValue()+"'";
				}
				// delegacion
				if (property.equals(DELEGACION)){
					sqlWhere += " AND U.delegacion = " + filter.getValue();
				}
				// externo
				if (property.equals(EXTERNO)){
					sqlWhere += " AND U.externo = " + filter.getValue();
				}
				else if (property.equals(CARGA_PAC)){
					sqlWhere += " AND U.CARGA_PAC = " + filter.getValue();
				}
				// e-mail
				if (property.equals("email")){
					sqlWhere += " AND U.email like '" + filter.getValue()+"%'";
				}
				// financiar
				if (property.equals(FINANCIAR) && filter.getValue() != null) {
					sqlWhere += " AND U.FINANCIAR = " + filter.getValue();
				}
				// importe minimo
				if (property.equals(IMP_MIN_FINANCIACION) && filter.getValue() != null) {
					sqlWhere += " AND U.IMP_MIN_FINANCIACION = " + filter.getValue();
				}
				// importe maximo
				if (property.equals(IMP_MAX_FINANCIACION) && filter.getValue() != null) {
					sqlWhere += " AND U.IMP_MAX_FINANCIACION = " + filter.getValue();
				}
				// fecha limite
				if (property.equals(FECHA_LIMITE) && filter.getValue() != null) {
					sqlWhere += " AND U.FECHA_LIMITE = TO_DATE('" + filter.getValue()+"', 'dd/MM/yyyy')";
				}
			}
		}catch (Exception e) {
			logger.error("ClaseDetalleFilter - Error al recuperar la lista de todos los ids -"+e.getMessage());
		}  
		return sqlWhere;
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

