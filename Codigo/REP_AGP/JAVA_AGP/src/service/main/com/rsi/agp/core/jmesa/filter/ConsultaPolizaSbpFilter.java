package com.rsi.agp.core.jmesa.filter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.StringUtils;

public class ConsultaPolizaSbpFilter implements CriteriaCommand {
	
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
		criteria.add(Restrictions.ne("estadoPoliza.idestado",Constants.ESTADO_POLIZA_BAJA));
        return criteria;
	}
	
	/**
	 * Carga el objeto Criteria con las condiciones de b√∫squeda referentes al filtro indicado por 'property,valor'
	 * @param criteria
	 * @param property
	 * @param value
	 */
	private void buildCriteria(Criteria criteria, String property, Object value) {
        if (value != null) {
        	       	       	
        	//Para comprobar que el valor tiene formato de fecha
        	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
        	Matcher dateMatcher = datePattern.matcher(value+"");
        	
        	Class<?> tipo;
        	
        	try {
				Class<?> c = Class.forName("com.rsi.agp.dao.tables.poliza.Poliza");
				Field field = c.getDeclaredField(property);
				tipo = field.getType();
			} catch (ClassNotFoundException e) {
				logger.debug ("No se encontro la clase");
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (SecurityException e) {
				//Por defecto, pongo la clase String
				tipo = String.class;
			} catch (NoSuchFieldException e) {
				//El campo no existe. Se trata del filtro por actor (la propiedad est√° dentro de otro objeto)
				//En este caso, la clase ser√° Long
				//tipo = Long.class;
				tipo = String.class;
			}
			try {
				if (dateMatcher.find()) {
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
	        	else if (tipo.equals(Boolean.class)){
	        		criteria.add(Restrictions.eq(property, new BooleanType().fromStringValue(value+"")));
	        	}
	        	else if (tipo.equals(Long.class)){
	        		criteria.add(Restrictions.eq(property, new LongType().fromStringValue(value+"")));
	        	}
	        	else{
	        		// Entidad
	        		if (property.equals("colectivo.tomador.id.codentidad")){	        				        				        				        			
	        			criteria.add(Restrictions.eq("col.tomador.id.codentidad", new BigDecimal(value.toString())));
	        		}
	        		//GrupoEntidades
	        		else if(property.equals("listaGrupoEntidades")){
	        			criteria.add(Restrictions.in("col.tomador.id.codentidad", (List<?>)value));
	        		}	
	        		// Oficina
	        		else if (property.equals("oficina")){
	        			criteria.add(Restrictions.in("oficina", CriteriaUtils.getCodigosOficina(value.toString())));
	        		}
	        		else if(property.equals("listaGrupoOficina")){
	        			criteria.add(Restrictions.in("oficina", (List<?>)value));
	        		}
	        		// Usuario
	        		else if (property.equals("usuario.codusuario")){
	        			criteria.add(Restrictions.eq("usuario.codusuario", value.toString()));
	        		}
	        		// Plan
	        		else if (property.equals("linea.codplan")){
	        			criteria.add(Restrictions.eq("lin.codplan", new BigDecimal(value.toString())));
	        		}
	        		// Linea
	        		else if (property.equals("linea.codlinea")){
	        			criteria.add(Restrictions.eq("lin.codlinea", new BigDecimal(value.toString())));
	        		}
	        		// Clase
	        		else if (property.equals("clase")){
	        			criteria.add(Restrictions.eq("clase", new BigDecimal(value.toString())));
	        		}
	        		// Referencia de poliza
	        		else if (property.equals("referencia")){
	        			criteria.add(Restrictions.eq("referencia", value.toString()));
	        		}
	        		// Referencia colectivo
	        		else if (property.equals("colectivo.idcolectivo")){	        			
	        			criteria.add(Restrictions.eq("col.idcolectivo", value.toString()));
	        		}
	        		// DC colectivo
	        		else if (property.equals("colectivo.dccolectivo")){	        			
	        			criteria.add(Restrictions.eq("col.dc", value.toString()));
	        		}
	        		// Modulo
	        		else if (property.equals("codmodulo")){	        			
	        			criteria.add(Restrictions.eq("codmodulo", value.toString()));
	        		}
	        		// CIF/NIF asegurado
	        		else if (property.equals("asegurado.nifcif")){	        			
	        			criteria.add(Restrictions.eq("ase.nifcif", value.toString()));
	        		}
	        		// Nombre asegurado
	        		else if (property.equals("asegurado.nombre")){
	        			// filtrado directamente desde el Dao
	        		}
	        		// Estado de la poliza
	        		else if (property.equals("estadoPoliza.idestado")){	        			
	        			criteria.add(Restrictions.eq("est.idestado", new BigDecimal(value.toString())));
	        		}
	        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
	        		// Entidad Mediadora
	        		else if (property.equals("colectivo.subentidadMediadora.id.codentidad")){	        			
	        			criteria.add(Restrictions.eq("col.subentidadMediadora.id.codentidad", new BigDecimal(value.toString())));
	        		}
	        		// Subentidad Mediadora
	        		else if (property.equals("colectivo.subentidadMediadora.id.codsubentidad")){	        			
	        			criteria.add(Restrictions.eq("col.subentidadMediadora.id.codsubentidad", new BigDecimal(value.toString())));
	        		}
	        		// Delegacion
	        		else if (property.equals("usuario.delegacion")){	        			
	        			criteria.add(Restrictions.eq("usu.delegacion", new BigDecimal(value.toString())));
	        		}
	        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
	        	}
			}catch (Exception e) {
				logger.error("ConsultaPolizaSbpFilter - "+e.getMessage());
			}
        	
        	
        	
        }
        
    }
	
	public String getSqlInnerJoin(String nombreAseg){
		String sqlInnerJoin = "select count (*) from TB_POLIZAS P ";
		boolean col = false;
		boolean lin = false;
		boolean esMed = false;
		boolean usu = false;
		
		sqlInnerJoin += " inner join Tb_ASEGURADOS A on P.IDASEGURADO = A.ID ";
		
		for (Filter filter : filters) {
			if (!col){
				sqlInnerJoin += " inner join TB_COLECTIVOS C on P.IDCOLECTIVO = C.ID" ;
				col = true;
			}
			if (!lin && filter.getProperty().contains("linea.")){
				sqlInnerJoin += " inner join TB_LINEAS L on P.LINEASEGUROID = L.LINEASEGUROID ";
				lin = true;
			}
			if (!esMed && filter.getProperty().contains("subentidadMediadora") && col){
				sqlInnerJoin += " inner join TB_SUBENTIDADES_MEDIADORAS esMed on C.ENTMEDIADORA=esMed.CODENTIDAD and C.SUBENTMEDIADORA=esMed.CODSUBENTIDAD";
				esMed = true;
			}
			if (!usu && filter.getProperty().contains("delegacion") ){
				sqlInnerJoin += " inner join TB_USUARIOS usu on usu.CODUSUARIO=P.CODUSUARIO";
				usu = true;
			}
		}
		return sqlInnerJoin;
	}
	
	public String getSqlWhere(){
		String sqlWhere = " WHERE 1 = 1";
		for (Filter filter : filters) {	
			
			if (filter.getValue() != null) { 
	        	//Para comprobar que el valor tiene formato de fecha
	        	Pattern datePattern = Pattern.compile("(0[1-9]|[1-9]|1[0-9]|2[0-9]|3[0-1])/(0[1-9]|[1-9]|1[0-2])/(19|20\\d{2})");
	        	Matcher dateMatcher = datePattern.matcher(filter.getValue()+"");
				try {
					if (dateMatcher.find()) {
		        		//La propiedad es de tipo fecha
		    			GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(dateMatcher.group(3)), 
		    					Integer.parseInt(dateMatcher.group(2))-1, 
		    					Integer.parseInt(dateMatcher.group(1)));
		    			
		    			Date fechaMas24 = new Date();
	        			GregorianCalendar fechaEnvioGrMas24 = new GregorianCalendar();
	        			fechaEnvioGrMas24.setTime(gc.getTime());
	        			fechaEnvioGrMas24.add(Calendar.HOUR,24);
	        			fechaMas24 = fechaEnvioGrMas24.getTime();
	        			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	        			String FechaEnvioStr = "";
	        			String FechaMas24Str = "";
	        			FechaEnvioStr = df.format(gc.getTime());
	        			FechaMas24Str = df.format(fechaMas24);
	        			sqlWhere += " AND P.FECHAENVIO >= TO_DATE('" + FechaEnvioStr + "','DD/MM/YYYY')";
	        			sqlWhere += " AND P.FECHAENVIO < TO_DATE('" + FechaMas24Str+ "','DD/MM/YYYY')";
					}else{
		        		// Entidad
		        		if (filter.getProperty().equals("colectivo.tomador.id.codentidad")){	        				        				        				        			
		        			sqlWhere += " AND C.CODENTIDAD = " + filter.getValue();
		        		}
		        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
		        		// Entidad Mediadora
		        		if (filter.getProperty().equals("colectivo.subentidadMediadora.id.codentidad")){	        				        				        				        			
		        			sqlWhere += " AND esMed.CODENTIDAD = " + filter.getValue();

		        		}
		        		if (filter.getProperty().equals("colectivo.subentidadMediadora.id.codsubentidad")){	        				        				        				        			
		        			sqlWhere += " AND esMed.codsubentidad = " + filter.getValue();

		        		}
		        		if (filter.getProperty().equals("usuario.delegacion")){	        				        				        				        			
		        			sqlWhere += " AND usu.delegacion = " + filter.getValue();

		        		}
		        		/* Pet. 63473 ** MODIF TAM (20.12.2021) ** FIN */
		        		//GrupoEntidades
		        		else if (filter.getProperty().equals("listaGrupoEntidades")){	        				        				        				        			
		        			sqlWhere += " AND C.CODENTIDAD in (" + StringUtils.toValoresSeparadosXComas((List<?>)filter.getValue(),false,false) + ") ";
		        		}
		        		// Oficina
		        		else if (filter.getProperty().equals("oficina")){
		        			sqlWhere += " AND P.OFICINA in  " + StringUtils.toValoresSeparadosXComas(
		        						CriteriaUtils.getCodigosOficina(filter.getValue().toString()), false, true) ;
		        		}
		        		//GrupoOficina
		        		else if (filter.getProperty().equals("listaGrupoOficina")){	        				        				        				        			
		        			sqlWhere += " AND P.OFICINA in (" + StringUtils.toValoresSeparadosXComas((List<?>)filter.getValue(),false,false) + ") ";
		        		}
		        		// Usuario
		        		else if (filter.getProperty().equals("usuario.codusuario")){
		        			sqlWhere += " AND P.CODUSUARIO = '" + filter.getValue() + "'";
		        		}
		        		// Plan
		        		else if (filter.getProperty().equals("linea.codplan")){
		        			sqlWhere += " AND L.CODPLAN = '" + filter.getValue() + "'";
		        		}
		        		// Linea
		        		else if (filter.getProperty().equals("linea.codlinea")){
		        			sqlWhere += " AND L.CODLINEA = '" + filter.getValue() + "'";
		        		}
		        		// Clase
		        		else if (filter.getProperty().equals("clase")){
		        			sqlWhere += " AND P.CLASE = " + filter.getValue();
		        		}
		        		// Referencia de poliza
		        		else if (filter.getProperty().equals("referencia")){
		        			sqlWhere += " AND P.REFERENCIA = '" + filter.getValue() + "'";
		        		}
		        		// Referencia colectivo
		        		else if (filter.getProperty().equals("colectivo.idcolectivo")){
		        			sqlWhere += " AND C.IDCOLECTIVO = '" + filter.getValue() + "'";
		        		}
		        		// Modulo
		        		else if (filter.getProperty().equals("codmodulo")){	        			
		        			sqlWhere += " AND P.CODMODULO = '" + filter.getValue() + "'";
		        		}
		        		// CIF/NIF asegurado
		        		else if (filter.getProperty().equals("asegurado.nifcif")){
		        			sqlWhere += " AND A.NIFCIF = '" + filter.getValue() + "'";
		        		}
		        		// Nombre asegurado
		        		else if (filter.getProperty().equals("asegurado.nombre")){
		        			// filtrado directamente desde el Dao
		        		}
		        		// Estado de la poliza
		        		else if (filter.getProperty().equals("estadoPoliza.idestado")){	        			
		        			sqlWhere += " AND P.IDESTADO = '" + filter.getValue() + "'";
		        		}	        			        			        		
		        	}
					sqlWhere += " AND P.IDESTADO != " + Constants.ESTADO_POLIZA_BAJA ;
				}catch (Exception e) {
					logger.error("ConsultaPolizaSbpFilter - "+e.getMessage());
				}
			}	
        }
		
		/* Pet. 62719 ** MODIF TAM (21.01.2021) ** Inicio */
		/* Incluimos validaciÛn para no recuperar aquellos Asegurados que estÈn bloqueados */
        sqlWhere += " AND (P.IDASEGURADO NOT IN (SELECT BLOQA.ID_ASEGURADO FROM o02agpe0.TB_BLOQUEOS_ASEGURADOS BLOQA WHERE BLOQA.IDESTADO_ASEG = 'B'))";
        /* Pet. 62719 ** MODIF TAM (21.01.2021) ** Fin */
        
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
