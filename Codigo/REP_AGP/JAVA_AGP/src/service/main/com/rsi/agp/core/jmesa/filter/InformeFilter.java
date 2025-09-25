package com.rsi.agp.core.jmesa.filter;

import static com.rsi.agp.core.util.Constants.PERFIL_USUARIO_SEMIADMINISTRADOR;
import static com.rsi.agp.core.util.Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_ENTIDADES_NO;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_ENTIDADES_SI;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_PERFIL;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_TODOS;
import static com.rsi.agp.core.util.ConstantsInf.COD_VISIBILIDAD_USUARIOS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

public class InformeFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter >();
	
	// Indican si se ha filtrado por algun campo en concreto
	private boolean isFilterVsb = false;
	private boolean isFilterVsbEnt = false;
	private boolean isPerfil1 = false;
	private boolean isPerfil5 = false;
	
	// Indican si se ha creado el alias de un campo
	private boolean isAliasUsuario = false;
	private boolean isAliasEntidad = false;
	
	// Indica si la entidad tiene acceso restringido a usuarios y perfiles concretos al apartado de generacion
	private boolean isEntidadRestringida = false;
	// Indica si el usuario pertenece al perfil 0
	private boolean isPerfil0 = false;
	// Lista de codigos de entidad correspondiente al grupo al que pertenece el usuario
	private List<BigDecimal> listaEntidadesUsuario = new ArrayList<BigDecimal>();
	
	// Constantes para identificar los filtros de busqueda
	public static final String CAMPO_FILTRO_NOMBRE = "nombre";
	public static final String CAMPO_FILTRO_TITULO1 = "titulo1";
	public static final String CAMPO_FILTRO_TITULO2 = "titulo2";
	public static final String CAMPO_FILTRO_TITULO3 = "titulo3";
	public static final String CAMPO_FILTRO_VISIBILIDAD = "visibilidad";
	public static final String CAMPO_FILTRO_VISIBILIDAD_ENT = "visibilidadEnt";
	public static final String CAMPO_FILTRO_PERFIL = "perfil";
	public static final String CAMPO_FILTRO_CUENTA = "cuenta";
	public static final String CAMPO_FILTRO_PERFIL1_CODPERFIL = "perfil1.codperfil";
	public static final String CAMPO_FILTRO_PERFIL1_CODENTIDAD = "perfil1.codentidad";
	public static final String CAMPO_FILTRO_PERFIL5_ENTIDADES = "perfil5.entidades";
	public static final String CAMPO_FILTRO_PERFIL2_OFICINAS = "perfil2.oficinas";
	public static final String CAMPO_FILTRO_USUARIO = "usuario.codusuario";
	public static final String CAMPO_FILTRO_OCULTO = "oculto";
		
	private String valorFiltroPerfil = "";
	Disjunction or = Restrictions.disjunction();
	
	public Criteria execute(Criteria criteria) {
		
		boolean esPerfil0; 
		if (isPerfil0){ // USUARIOS PERFIL 0
			esPerfil0 = true;
		}else{  //USUARIOS PERFIL 1 y 5
			esPerfil0 = false;
		}
		// Construye el Criteria
		for (Filter filter : filters) {
	           buildCriteria(criteria, filter.getProperty(), filter.getValue(), esPerfil0);
	    }
		
		if (!valorFiltroPerfil.equals("")){
        	or.add(Restrictions.eq(CAMPO_FILTRO_PERFIL, new BigDecimal(valorFiltroPerfil.toString())));
        }
        	
		valorFiltroPerfil = "";
		return criteria;
	}
	
	/**
	 * Anhade al Criteria la condicion IN mas los codigos introducidos en las lupas si estos se han informado
	 * @param criteria
	 * @param cadenaCodigosLupas
	 * @return
	 */
	public Criteria executeCriteriaListaCodigos (Criteria criteria, String cadenaCodigosLupas) {
		boolean addCriteria = false;
		if (isPerfil0){ // USUARIOS PERFIL 0
			// Campos para incluir como IN
			// Si se ha buscado por alguno de ellos y la lista de codigos no esta vacia
			if ((isFilterVsb || isFilterVsbEnt) && (!StringUtils.isNullOrEmpty(cadenaCodigosLupas))) {
				// Obtiene la lista de codigos por los que hay que buscar 
				List<String> lstCodUsuarios = (isFilterVsb) ? (Arrays.asList(cadenaCodigosLupas.split("#"))) : new ArrayList<String>();
				List<BigDecimal> lstCodEntidades = (isFilterVsbEnt) ? StringUtils.asListBigDecimal(cadenaCodigosLupas, "#") : new ArrayList<BigDecimal>();
				
				// Si la lista tiene elementos
				if (lstCodUsuarios.size()>0 || lstCodEntidades.size()>0) {
					criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
					// Crea el alias e incluye los codigos dependiendo de si la lista es de codigos de usuario o de entidad
					criteria.createAlias(isFilterVsb ? "usuarios" : "entidades", "tbin");
					criteria.add(Restrictions.in("tbin." + (isFilterVsb ? "codusuario" : "codentidad"), isFilterVsb ? lstCodUsuarios : lstCodEntidades));
				}
			}
		}else{
			
			if (!StringUtils.isNullOrEmpty(cadenaCodigosLupas)) {
				List<BigDecimal> lstCodEntidades = StringUtils.asListBigDecimal(cadenaCodigosLupas, "#");
				
				if (lstCodEntidades.size()>0) {
					//criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
					criteria.createAlias("entidades", "tbin");
					or.add(Restrictions.in("tbin.codentidad", lstCodEntidades));
					criteria.add(or);
					addCriteria = true;
				}
			}
		}
		if (!addCriteria){
			criteria.add(or);
		}
		return criteria;
	}

	
	private void buildCriteria(Criteria criteria, String property, Object value, boolean esPerfil0) {
        if (value != null) {
    		// Nombre
    		if (property.equals(CAMPO_FILTRO_NOMBRE)){
    			criteria.add(Restrictions.like(CAMPO_FILTRO_NOMBRE, "%"+value.toString()+"%"));
    		}
    		// titulo1
    		else if (property.equals(CAMPO_FILTRO_TITULO1)){
    			criteria.add(Restrictions.like(CAMPO_FILTRO_TITULO1, "%"+value.toString()+"%"));
    		}
    		// titulo2
    		else if (property.equals(CAMPO_FILTRO_TITULO2)){
    			criteria.add(Restrictions.like(CAMPO_FILTRO_TITULO2, "%"+value.toString()+"%"));
    		}
    		// titulo3
    		else if (property.equals(CAMPO_FILTRO_TITULO3)){
    			criteria.add(Restrictions.like(CAMPO_FILTRO_TITULO3, "%"+value.toString()+"%"));
    		}
    		// visibilidad
    		else if (property.equals(CAMPO_FILTRO_VISIBILIDAD)){
    			if (esPerfil0)
    				criteria.add(Restrictions.eq(CAMPO_FILTRO_VISIBILIDAD, new BigDecimal(value.toString())));
    		}
    		// visibilidad de entidades
    		else if (property.equals(CAMPO_FILTRO_VISIBILIDAD_ENT)){
    			if (esPerfil0)
    				criteria.add(Restrictions.eq(CAMPO_FILTRO_VISIBILIDAD_ENT, new BigDecimal(value.toString())));
    		}
    		// perfil
    		else if (property.equals(CAMPO_FILTRO_PERFIL)){
    			if (esPerfil0)
    				criteria.add(Restrictions.eq(CAMPO_FILTRO_PERFIL, new BigDecimal(value.toString())));
    			else{
    				valorFiltroPerfil = value.toString();
    			}
    		}
    		// Cuenta
    		else if (property.equals(CAMPO_FILTRO_CUENTA)){
    			criteria.add(Restrictions.eq(CAMPO_FILTRO_CUENTA, new BigDecimal(value.toString())));
    		}
    		// --
    		// Filtros implicitos para usuarios de perfil 1
    		// --
    		// Perfil del usuario que creo el informe
    		else if (property.equals(CAMPO_FILTRO_PERFIL1_CODPERFIL)){
    			//Crea el alias de Usuario si no esta creado ya
    			createAliasUsuario(criteria);
    			criteria.add(Restrictions.eq("usu.tipousuario", new BigDecimal(value.toString())));
    		}
    		// Entidad del usuario que creo el informe
    		else if (property.equals(CAMPO_FILTRO_PERFIL1_CODENTIDAD)){
    			//Crea los alias de Usuario y Entidad si no estan creados ya
    			createAliasUsuarioEntidad(criteria);
    			criteria.add(Restrictions.eq("ent.codentidad", new BigDecimal(value.toString())));
    		}
    		// --
    		// Filtros implicitos para usuarios de perfil 5
    		// --
    		// Entidad del usuario que creo el informe
    		else if (property.equals(CAMPO_FILTRO_PERFIL5_ENTIDADES)){
    			//Crea los alias de Usuario y Entidad si no estan creados ya
    			createAliasUsuarioEntidad(criteria);
    			criteria.add(Restrictions.in("ent.codentidad", (List<?>) value)); 
    		
    		}else if (property.equals(CAMPO_FILTRO_USUARIO)){
    			criteria.add(Restrictions.eq("usuario.codusuario", value.toString()));
    			
    		}
    		else if (property.equals(CAMPO_FILTRO_OCULTO)){
    			criteria.add(Restrictions.eq(CAMPO_FILTRO_OCULTO, new BigDecimal(value.toString())));
    		}	
        }
    }

	/**
	 * Crea los alias de Usuario y Entidad si estan creados ya
	 * @param criteria
	 */
	private void createAliasUsuarioEntidad(Criteria criteria) {
		createAliasUsuario(criteria);
		// Se crea el alias de Entidad si no se ha hecho anteriormente
		if (!isAliasEntidad) {
			criteria.createAlias("usu.oficina", "ofi");
			criteria.createAlias("ofi.entidad", "ent");
			isAliasEntidad = true;
		}
	}

	/**
	 * Crea el alias de Usuario si no esta creado ya
	 * @param criteria
	 */
	private void createAliasUsuario(Criteria criteria) {
		// Se crea el alias de Usuario si no se ha hecho anteriormente
		if (!isAliasUsuario) {
			criteria.createAlias("usuario", "usu");
			isAliasUsuario = true;
		}
	}
	
	/**
	 * Anhade a la consulta del count los join necesarios dependiendo de los filtros introducidos en el mantenimiento de informes
	 * @param cadenaCodigosLupas
	 * @return
	 */
	public String getSqlInnerJoin(String cadenaCodigosLupas){
		StringBuilder sqlInnerJoin = new StringBuilder ("select count (*) from tb_mtoinf_informes I");
		
		// Si se ha filtrado por visibilidad de usuarios o entidades
		if (cadenaCodigosLupas != null && !cadenaCodigosLupas.equals("")){
			sqlInnerJoin.append ((isFilterVsb) ? " inner join Tb_mtoinf_informes_usuario U on U.IDINFORME = I.ID ":
							(isFilterVsbEnt) ? " inner join Tb_mtoinf_informes_entidades E on E.IDINFORME = I.ID " : "");
		}
		
		// Si el usuario conectado es perfil 1 o 5 se anhade el join con la tablas de usuarios
		if (isPerfil1 || isPerfil5) {
			sqlInnerJoin.append (" inner join tb_usuarios USU on USU.CODUSUARIO = I.PROPIETARIO ");
			sqlInnerJoin.append (" inner join Tb_mtoinf_informes_entidades E on E.IDINFORME = I.ID ");
		}
		
		sqlInnerJoin.append (" WHERE 1 = 1");
		
		return sqlInnerJoin.toString();
	}
	
	/**
	 * Anhade a la consulta del count los join necesarios dependiendo del perfil del usuario para la pantalla de generacion de informes
	 * @param cadenaCodigosLupas
	 * @return
	 */
	public String getSqlLeftJoinGen (){
		
		StringBuilder sqlJoin = new StringBuilder ("");
		
		// Si el usuario no es perfil 0, se incluyen los join necesarios
		if (!this.isPerfil0){
			sqlJoin.append("select DISTINCT I.ID ");
			sqlJoin.append("from tb_mtoinf_informes I ");
			sqlJoin.append("left join tb_mtoinf_informes_entidades E on E.idinforme = I.id ");
			sqlJoin.append("left join tb_mtoinf_informes_usuario U on U.idinforme = I.id ");
			sqlJoin.append("left join tb_usuarios USU on USU.CODUSUARIO = I.PROPIETARIO ");
			sqlJoin.append("WHERE ");
		}
		// Si es perfil 0 no hace falta join, ya que hay que contar todos los registros de la tabla de informes
		else{
			sqlJoin.append("select count (*) from tb_mtoinf_informes I WHERE 1 = 1 ");
		}
		
		return sqlJoin.toString();
	}
	
	/**
	 * Crea la clausula where para la el count del mantenimiento de informes
	 * @return
	 */
	public String getSqlWhere(String cadenaCodigosLupas){
		
		StringBuilder sqlWhere = new StringBuilder("");
		
		if (isPerfil0){ // USUARIOS PERFIL 0
			// Campos fijos del filtro
			for (Filter filter : filters) {	
				if (filter.getValue() != null) {
					// Nombre
	        		if (filter.getProperty().equals(CAMPO_FILTRO_NOMBRE)) sqlWhere.append(" AND I.NOMBRE LIKE '%" + filter.getValue() + "%'");
	        		// titulo1
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO1)) sqlWhere.append(" AND I.TITULO1 LIKE '%" + filter.getValue() + "%'");
	        		// titulo2
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO2)) sqlWhere.append(" AND I.TITULO2 LIKE '%" + filter.getValue() + "%'");
	        		// titulo3
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO3)) sqlWhere.append(" AND I.TITULO3 LIKE '%" + filter.getValue() + "%'");
	        		// visibilidad
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_VISIBILIDAD)) sqlWhere.append(" AND I.VISIBILIDAD = " + filter.getValue());
	        		// visibilidad de entidades
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_VISIBILIDAD_ENT)) sqlWhere.append(" AND I.VISIBILIDADENT = " + filter.getValue());
	        		// oculto
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_OCULTO)) sqlWhere.append(" AND I.OCULTO = " + filter.getValue());
	        		// perfil
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL)) sqlWhere.append(" AND I.PERFIL = " + filter.getValue());
	        		// cuenta
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_CUENTA)) sqlWhere.append(" AND I.CUENTA = " + filter.getValue());
	        		// Campos para busquedas de usuarios con perfil 1
	        		// Perfil de usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL1_CODPERFIL)) sqlWhere.append(" AND USU.TIPOUSUARIO = " + filter.getValue());
	        		// Entidad de usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL1_CODENTIDAD)) sqlWhere.append(" AND USU.CODENTIDAD = " + filter.getValue());
	        		// Campos para busquedas de usuarios con perfil 5
	        		// Grupo de entidades del usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL5_ENTIDADES)){
	        			sqlWhere.append(" AND USU.CODENTIDAD IN " + StringUtils.toValoresSeparadosXComas((List<?>)filter.getValue(), false)); 
	        		}else if (filter.getProperty().equals(CAMPO_FILTRO_USUARIO)){
	        			sqlWhere.append(" AND I.PROPIETARIO = '" + filter.getValue() +"'");
	        		}
				}	
	        }
			
			// Campos para incluir como IN
			// Si se ha buscado por alguno de ellos y la lista de codigos no esta vacia
			if ((isFilterVsb || isFilterVsbEnt) && (!StringUtils.isNullOrEmpty(cadenaCodigosLupas))) { 
				sqlWhere.append(" AND " + (isFilterVsb ? "U.CODUSUARIO" : "E.CODENTIDAD") + 
						 " IN " + StringUtils.toValoresSeparadosXComas(cadenaCodigosLupas.split("#"), true));
			}
			return sqlWhere.toString();
			
			
		}else { // USUARIOS PERFIL 1 y 5
			
			// Campos fijos del filtro
			for (Filter filter : filters) {	
				if (filter.getValue() != null) {
					// Nombre
	        		if (filter.getProperty().equals(CAMPO_FILTRO_NOMBRE)) 
	        			sqlWhere.append(" AND I.NOMBRE LIKE '%" + filter.getValue() + "%'");
	        		// titulo1
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO1)) 
	        			sqlWhere.append(" AND I.TITULO1 LIKE '%" + filter.getValue() + "%'");
	        		// titulo2
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO2)) 
	        			sqlWhere.append(" AND I.TITULO2 LIKE '%" + filter.getValue() + "%'");
	        		// titulo3
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_TITULO3)) 
	        			sqlWhere.append(" AND I.TITULO3 LIKE '%" + filter.getValue() + "%'");
	        		// oculto
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_OCULTO)) 
	        			sqlWhere.append(" AND I.OCULTO = " + filter.getValue());
	        		// perfil
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL)) 
	        			//sqlWhere.append(" AND I.PERFIL = " + filter.getValue());
	        			valorFiltroPerfil = filter.getValue().toString();
	        		// cuenta
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_CUENTA)) 
	        			sqlWhere.append(" AND I.CUENTA = " + filter.getValue());
	        		// Campos para busquedas de usuarios con perfil 1
	        		// Perfil de usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL1_CODPERFIL)) 
	        			sqlWhere.append(" AND USU.TIPOUSUARIO = " + filter.getValue());
	        		// Entidad de usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL1_CODENTIDAD)) 
	        			sqlWhere.append(" AND USU.CODENTIDAD = " + filter.getValue());
	        		// Campos para busquedas de usuarios con perfil 5
	        		// Grupo de entidades del usuario
	        		else if (filter.getProperty().equals(CAMPO_FILTRO_PERFIL5_ENTIDADES)){
	        			sqlWhere.append(" AND USU.CODENTIDAD IN " + 
	        						StringUtils.toValoresSeparadosXComas((List<?>)filter.getValue(), false)); 
	        		}else if (filter.getProperty().equals(CAMPO_FILTRO_USUARIO)){
	        			sqlWhere.append(" AND I.PROPIETARIO = '" + filter.getValue() +"'");
	        		}
				}	
	        }
		}
		int i=0;
		
		if (!valorFiltroPerfil.equals("")){
			
			sqlWhere.append(" AND (I.PERFIL = " + valorFiltroPerfil);
			i++; 
		}
		if (isFilterVsbEnt) {
			if (!StringUtils.isNullOrEmpty(cadenaCodigosLupas)){ // si viene relleno es con la lista de entidades
				
				if (i==1){ // ha entrado por el if anterior
					sqlWhere.append(" OR ");
					sqlWhere.append(" E.CODENTIDAD IN " + StringUtils.toValoresSeparadosXComas(cadenaCodigosLupas.split("#"), true));
					sqlWhere.append(" ) ");
					i++; // i=2
				}else {
					sqlWhere.append(" AND E.CODENTIDAD IN " + StringUtils.toValoresSeparadosXComas(cadenaCodigosLupas.split("#"), true));
				}
			
			}
		}
		if (i==1){
			sqlWhere.append(" ) ");
		}
		
		return sqlWhere.toString();
	}
	
	/**
	 * Crea la clausula where para la el count de la generacion de informes
	 * @param usuario
	 * @return
	 */
	public String getSqlWhereGen (final Usuario usuario) {
		
		StringBuilder sqlWhere = new StringBuilder("");
		
		// Si es perfil 0 no se incluyen las condiciones de visibilidad ni de perfil
		if (!isPerfil0) {
			// Filtros para obtener los informes disponibles en base a la visibilidad de cada uno
			cargarFiltrosVisibilidad(sqlWhere, usuario);
			// Filtros para obtener los informes disponibles en base al perfil del usuario (solo para perfiles 1 y 5)
			cargarFiltrosPerfil (sqlWhere, usuario);
		}		
		// Anhade los filtros de busqueda incluidos en el formulario
		sqlWhere.append(getSqlWhere(null));
		
		return sqlWhere.toString();
	}
	
	/**
	 * Anhade en sqlWhere los filtros para obtener los informes en base al perfil del usuario
	 * @param sqlWhere
	 * @param usuario
	 */
	private void cargarFiltrosPerfil (StringBuilder sqlWhere, final Usuario usuario) {
		
		// Si el usuario es perfil 1 internto se filtran los informes por este perfil y por la entidad del usuario
		if (PERFIL_USUARIO_SERVICIOS_CENTRALES.equals (usuario.getPerfil()) && !usuario.isUsuarioExterno()) {
			sqlWhere.append(" or (usu.tipousuario= ").append(usuario.getTipousuario()).append(" and usu.codentidad= ")
					.append(usuario.getOficina().getEntidad().getCodentidad()).append(")");
		}
		else if (PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())) {
			sqlWhere.append(" or (USU.CODENTIDAD IN ").append(StringUtils.toValoresSeparadosXComas(this.listaEntidadesUsuario, false)).append(")");
		}
		
		sqlWhere.append(")");
	}
	
	/**
	 * Anhade en sqlWhere los filtros para obtener los informes en base a la visibilidad de cada uno
	 * @param sqlWhere
	 * @param usuario
	 */
	private void cargarFiltrosVisibilidad (StringBuilder sqlWhere, final Usuario usuario) {
		// Inicio del grupo de condiciones de visibilidad
		sqlWhere.append(" ((");
		
		// Estas dos condiciones se incluiran en la consulta si la entidad del usuario no tiene restringido el acceso al generador
		// a usuarios y perfiles concretos
		if (!isEntidadRestringida()) {
			// Condicion para obtener los informes que tengan visibilidad 'Todos' (sin Entidad)
			sqlWhere.append("(i.visibilidad = ").append(COD_VISIBILIDAD_TODOS).append(" and (i.visibilidadent is null or i.visibilidadent = ").append(COD_VISIBILIDAD_ENTIDADES_NO +")) OR ");
			// Condicion para obtener los informes que tengan visibilidad 'Todos' y 'Entidad'
			sqlWhere.append("(i.visibilidad = ").append(COD_VISIBILIDAD_TODOS).
					 append(" and i.visibilidadent = ").append(COD_VISIBILIDAD_ENTIDADES_SI).append(" and e.codentidad = ").append(usuario.getOficina().getEntidad().getCodentidad()).append(") OR ");
			// Condicion para obtener los informes que tengan visibilidad 'Perfil' y 'Entidad'
			sqlWhere.append("(i.visibilidad = ").append(COD_VISIBILIDAD_PERFIL).append(" and i.perfil = ").append(usuario.getTipousuario())
					.append(" and i.visibilidadent = ").append(COD_VISIBILIDAD_ENTIDADES_SI).append(" and e.codentidad = ").append(usuario.getOficina().getEntidad().getCodentidad()).append(") OR ");
			// Condicion para obtener los informes que tengan visibilidad unicamente de 'Entidad'
			sqlWhere.append("(i.visibilidad is null and i.visibilidadent = ").append(COD_VISIBILIDAD_ENTIDADES_SI).append(" and e.codentidad = ")
							  .append(usuario.getOficina().getEntidad().getCodentidad()).append(") OR ");
		}
		
		// Condicion para obtener los informes que tengan visibilidad 'Perfil' unicamente - Usuarios externos
		BigDecimal tipoUsuarioExt = null;
		if (usuario.isUsuarioExterno()) {
			tipoUsuarioExt = Constants.PERFIL_USUARIO_SERVICIOS_CENTRALES.equals(usuario.getPerfil()) ? Constants.PERFIL_1_EXT_INFORMES :
										Constants.PERFIL_3_EXT_INFORMES;
		}

		// Condicion para obtener los informes que tengan visibilidad 'Perfil' unicamente
		sqlWhere.append("(i.visibilidad = ").append(COD_VISIBILIDAD_PERFIL).append(" and i.perfil = ").
				 append(tipoUsuarioExt != null ? tipoUsuarioExt : usuario.getTipousuario()).append(" and i.visibilidadent is null").append(") OR ");
		
		// Condicion para obtener los informes que tengan visibilidad 'Usuario'
		sqlWhere.append("(i.visibilidad = ").append(COD_VISIBILIDAD_USUARIOS).append(" and u.codusuario = '").append(usuario.getCodusuario()).append("')");
		
		// Fin del grupo de condiciones de visibilidad
		sqlWhere.append(")");
	}
	
	/**
	 * Anhade el filtro de busqueda al listado de filtros
	 * @param property
	 * @param value
	 */
	public void addFilter(String property, Object value) {
        
		filters.add(new Filter(property, value));
        
        // Comprueba si se esta filtrando por visibilidad de usuarios para actualizar el flag correspondiente
        if ((CAMPO_FILTRO_VISIBILIDAD.equals(property) && (new Integer (COD_VISIBILIDAD_USUARIOS).toString()).equals(value))) {
        	this.isFilterVsb =true;
        }
        // Comprueba si se esta filtrando por visibilidad de entidades para actualizar el flag correspondiente
        if (CAMPO_FILTRO_VISIBILIDAD_ENT.equals(property)) {
        	this.isFilterVsbEnt = true;
        }
        // Comprueba si se va a filtrar por las condiciones del perfil 1
        if (CAMPO_FILTRO_PERFIL1_CODPERFIL.equals(property)) {
        	this.isPerfil1 = true;
        }
        // Comprueba si se va a filtrar por las condiciones del perfil 5
        if (CAMPO_FILTRO_PERFIL5_ENTIDADES.equals(property)) {
        	this.isPerfil5 = true;
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

	public boolean isEntidadRestringida() {
		return isEntidadRestringida;
	}

	public void setEntidadRestringida(boolean isEntidadRestringida) {
		this.isEntidadRestringida = isEntidadRestringida;
	}

	public boolean isPerfil0() {
		return isPerfil0;
	}

	public void setPerfil0(boolean isPerfil0) {
		this.isPerfil0 = isPerfil0;
	}

	public List<BigDecimal> getListaEntidadesUsuario() {
		return listaEntidadesUsuario;
	}

	public void setListaEntidadesUsuario(List<BigDecimal> listaEntidadesUsuario) {
		this.listaEntidadesUsuario = listaEntidadesUsuario;
	}

	

}
