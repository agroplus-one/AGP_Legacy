package com.rsi.agp.dao.models.config;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.LobHandler;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.pac.FormPacCargasBean;
import com.rsi.agp.dao.tables.pac.PacAsegurados;
import com.rsi.agp.dao.tables.pac.PacCargas;
import com.rsi.agp.dao.tables.pac.PacParcelas;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;

@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
public class CargaPACDao extends BaseDaoHibernate implements ICargaPACDao{
	
	private static final Log logger = LogFactory.getLog(CargaPACDao.class);
	private DataSource dataSource;
	private LobHandler lobHandler;
	private ILineaDao lineaDao;
	
	public void saveDatosCargaPAC (List<PacParcelas> listPacAsegParcelas, Reader contenidoFicheroPAC) throws DAOException {
		
	    Session session = obtenerSession();
				
		try {

			for(PacParcelas pacParcelas : listPacAsegParcelas){
			
				//Si existe algun AsegParcela con Entidad, plan, linea, Num_identificacion Asegurado, Provincia, Municipio, Submunicipio, 
				//Comarca, Cultivo, Variedad, Poligono, Parcela que coincida con pacAsegParcelas, 
				//el anterior se elimina de la base de datos y se inserta el nuevo
				
				Criteria criteria = session.createCriteria(PacParcelas.class);
				criteria.add(Restrictions.eq("codprovincia", pacParcelas.getProvincia()));
				criteria.add(Restrictions.eq("codcomarca", pacParcelas.getComarca()));
				criteria.add(Restrictions.eq("codtermino", pacParcelas.getTermino()));
				criteria.add(Restrictions.eq("subtermino", pacParcelas.getSubtermino()));
				criteria.add(Restrictions.eq("poligono", pacParcelas.getPoligono()));
				criteria.add(Restrictions.eq("parcela", pacParcelas.getParcela()));
				criteria.add(Restrictions.eq("cultivo", pacParcelas.getCultivo()));
				criteria.add(Restrictions.eq("variedad", pacParcelas.getVariedad()));
				
				criteria.createAlias("pacAseg", "pacAseg");
				
				criteria.add(Restrictions.eq("pacAseg.nifcif", pacParcelas.getPacAsegurados().getNifAsegurado()));

				criteria.createAlias("pacAseg.pacCargas", "car");
				criteria.add(Restrictions.eq("car.codEntidad", pacParcelas.getPacAsegurados().getPacCargas().getEntidad()));
				criteria.add(Restrictions.eq("car.codlinea", pacParcelas.getPacAsegurados().getPacCargas().getLinea()));
				criteria.add(Restrictions.eq("car.codplan", pacParcelas.getPacAsegurados().getPacCargas().getPlan()));
				
				List<PacParcelas> listaAsegParcelasExistentes = criteria.list();
				
				for(PacParcelas pacAsegParcelasAux : listaAsegParcelasExistentes)
					session.delete(pacAsegParcelasAux);
				
				session.saveOrUpdate(pacParcelas);
			
			}
			
			PacCargas pacCargas = listPacAsegParcelas.get(0).getPacAsegurados().getPacCargas();

			PreparedStatement ps = session.connection().prepareStatement("UPDATE TB_PAC_CARGAS SET CONTENIDO_FICHERO = ? WHERE ID = "+pacCargas.getId());
			lobHandler.getLobCreator().setClobAsCharacterStream(ps, 1, contenidoFicheroPAC, 80000);
			ps.executeUpdate();
			
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error salvando los datos de carga de la PAC", ex);	
		}
		
	}
	
	/**
	 * Método para obtener el contenido del clob en el que se guarda el fichero de PAC.
	 * @param idCargaPAC Identificador de la carga.
	 * @return Reader apuntando al contenido del clob.
	 */
	public Reader getContenidoArchivoCargaPAC(Long idCargaPAC) throws DAOException {
	
		Session session = obtenerSession();
		Reader reader = null;
				
		try {
		    PreparedStatement ps = session.connection().prepareStatement("SELECT FICHERO FROM TB_PAC_CARGAS_FICHERO WHERE ID_CARGA = "+idCargaPAC);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				reader = lobHandler.getClobAsCharacterStream(rs, 1);
			rs.close();
			
			if (reader == null)
				throw new DAOException("No se han encontrado datos para el idCargaPAC "+idCargaPAC);
			return reader;
		}
		catch(Exception ex){
			throw new DAOException("Se ha producido un error leyendo el archivo de carga de la PAC", ex);	
		}
		
	}
	
	public List<Long> existeParcelasPACAsegurado(BigDecimal codlinea, BigDecimal codplan, String cifnifAsegurado, BigDecimal codentidad,
											  BigDecimal codentidadMed, BigDecimal codsubentidadMed) throws DAOException {
		
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(PacAsegurados.class);

			criteria.createAlias("pacCargas", "carga"); 
			
			criteria.add(Restrictions.eq("carga.linea", codlinea.intValue()));
			criteria.add(Restrictions.eq("carga.plan", codplan.intValue()));
			criteria.add(Restrictions.eq("carga.entidad", codentidad));
			criteria.add(Restrictions.eq("carga.entMed", codentidadMed.intValue()));
			criteria.add(Restrictions.eq("carga.subentMed", codsubentidadMed.intValue()));
			criteria.add(Restrictions.eq("nifAsegurado", cifnifAsegurado));
			
			criteria.setProjection(Projections.property("id"));
			
			return (criteria.list());
			
		}catch(Exception ex){
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);	
		}
	}
	
	public List <PacCargas> listarCargas(PacCargas pacCarga) throws DAOException{

		Session session = obtenerSession();
		
		try{
			
			Criteria criteria =	session.createCriteria(PacCargas.class);
			
			// Entidad 
			if(pacCarga.getEntidad() != null) {
				criteria.add(Restrictions.eq("entidad", pacCarga.getEntidad()));
			}
			// Si no se ha filtrado por 'Entidad' y el usuario es perfil 5, se filtra por todas las entidades del grupo
			else if (pacCarga.getGrupoEntidades() != null && !pacCarga.getGrupoEntidades().isEmpty()){
				criteria.add(Restrictions.in("entidad", pacCarga.getGrupoEntidades()));
			}
			
			// Entidad mediadora
			if(pacCarga.getEntMed() != null)
				criteria.add(Restrictions.eq("entMed", pacCarga.getEntMed()));
			
			// Entidad mediadora
			if(pacCarga.getSubentMed() != null)
				criteria.add(Restrictions.eq("subentMed", pacCarga.getSubentMed()));
			
			// Plan
			if(pacCarga.getPlan() != null)
				criteria.add(Restrictions.eq("plan", pacCarga.getPlan()));
			
			// Línea
			if(pacCarga.getLinea() != null)
				criteria.add(Restrictions.eq("linea", pacCarga.getLinea()));
			
			// Fichero PAC
			if(pacCarga.getNombreFichero() != null && pacCarga.getNombreFichero().length()>0)
				criteria.add(Restrictions.ilike("nombreFichero", pacCarga.getNombreFichero().trim(), MatchMode.ANYWHERE));
			
			return criteria.list();
			
		}catch(Exception ex){
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);	
		}
		finally{
		}
	}
	
	public List <RelEspeciesSCEspeciesST>  buscarST (BigDecimal lineaSeguroId,BigDecimal codLinea,BigDecimal codPlan, BigDecimal codCultST, BigDecimal codVarST)  throws DAOException{
		Session session = obtenerSession();
		
		try {
			Criteria criteria = session.createCriteria(RelEspeciesSCEspeciesST.class);
			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
			criteria.add(Restrictions.eq("id.codlinea", codLinea));
			criteria.add(Restrictions.eq("id.codplan", codPlan));
			criteria.add(Restrictions.eq("id.codcultivost", codCultST));
			criteria.add(Restrictions.eq("id.codvariedadst", codVarST));
			
			return  criteria.list();
			
		} catch (Exception ex) {
			logger.error(ex); 
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
		
	}
	
	public List<PacParcelas> filtrarPacProvComTerSubterm (Map<String,Object> filtro, List<PacParcelas> listParcelasPac)throws DAOException {
		logger.debug("--- Init filtrarPacProvComTerSubterm" );
		List<PacParcelas> 	filtrado   = new ArrayList<PacParcelas>(0);
		ArrayList<String> 		listProv   = new ArrayList<String>();
		ArrayList<String> 		listCom    = new ArrayList<String>(0);
		ArrayList<String> 		listTerm   = new ArrayList<String>(0);
		ArrayList<Character> 	listSubTer = new ArrayList<Character>(0);

		try {
			if (!filtro.isEmpty()) {
				for( Iterator<String> recorrofiltro = filtro.keySet().iterator();  recorrofiltro.hasNext();) { 
		            String propiedad = recorrofiltro.next();
		            if (propiedad == "subtermino") { 
		            	ArrayList<Character> valorSubTer = (ArrayList<Character>)filtro.get(propiedad);
		            	listSubTer  = valorSubTer ; 
		            }else{
						ArrayList<String> valor = (ArrayList<String>)filtro.get(propiedad);
			            if (propiedad == "codprovincia") { listProv  = valor ;} 
			            if (propiedad == "codcomarca") { listCom  = valor ;} 
			            if (propiedad == "codtermino") { listTerm  = valor ;} 
		            }
				}
		            
				for(PacParcelas pacParcela : listParcelasPac){
					Boolean  existe	= true;
					
					if (listProv.size() != 0){// compruebo si provincia esta dentro le listProv
						if (listProv.indexOf(pacParcela.getProvincia().toString()) < 0) {existe = false;}
					}
					if (listCom.size() != 0 && existe){
						if (listCom.indexOf(pacParcela.getComarca().toString()) < 0) {existe = false;}
					}
					if (listTerm.size() != 0 && existe){
						if (listTerm.indexOf(pacParcela.getTermino().toString()) < 0) {existe = false;}
					}
					if (listSubTer.size() != 0 && existe){
						if (listSubTer.indexOf(pacParcela.getSubtermino()) < 0) {existe = false;}
					}
					
					if (existe){ filtrado.add(pacParcela);}
	
				}
				
			} else {filtrado = listParcelasPac;}
			logger.debug("--- Exit filtrarPacProvComTerSubterm" );	
			return filtrado;
			
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}
	
    // ---------------------------------------------
    //     [INICIO] Listados filtrados por clase
    // ---------------------------------------------
    
    public List getParcelasPac(String nifcif, BigDecimal codlinea, BigDecimal codplan, Long claseId, BigDecimal codentidad) throws DAOException{
    	List<BigDecimal> provincias  = new ArrayList<BigDecimal>();
    	List<BigDecimal> comarcas    = new ArrayList<BigDecimal>();
    	List<BigDecimal> terminos    = new ArrayList<BigDecimal>();
    	List<Character> subterminos  = new ArrayList<Character>();
    	//List<BigDecimal> cultivos    = new ArrayList<BigDecimal>();
    	//List<BigDecimal> variedades  = new ArrayList<BigDecimal>();
    	
    	Long lineaseguroid = null;
    	
    	try{
    		
    		lineaseguroid = this.lineaDao.getLineaSeguroId(codlinea, codplan);
    		
    		// elementos por los que filtro
    		provincias  = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codprovincia");             // provincias validas
    		comarcas    = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codcomarca");               // comarcas      "
    		terminos    = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.codtermino");               // terminos      "
    		subterminos = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.subtermino");               // subterminos   "
    		
    		//cultivos    = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.cultivo.id.codcultivo");    // cultivos      "
    		//variedades  = getCampoClaseDetalle(null, lineaseguroid, "claseDetalle.variedad.id.codvariedad");  // variedades    "
    		
    		Session session = obtenerSession();
    		Criteria criteria = getCriteriaParcelasPac(session, provincias, comarcas, terminos, subterminos, null, 
    				null, nifcif, codlinea, codplan, codentidad);

    		return criteria.list();


    	}catch(Exception ex){
    		logger.error("Se ha producido un error durante el acceso a la base de datos: ",ex);
    		throw new DAOException("[ERROR] en CargaPACDao.java mÃ©todo getParcelasPac]");
    	}
    }
    
    public Criteria getCriteriaParcelasPac(Session session, List provincias, List comarcas, List terminos, List subterminos, 
    		List cultivos, List variedades, String nifcif, BigDecimal codlinea, BigDecimal codplan, BigDecimal codentidad){
    	
    	String cad="";
    	Criteria criteria = session.createCriteria(PacParcelas.class);
    	criteria.createAlias("pacAseg", "paseg");
    	criteria.createAlias("pacAseg.pacCargas", "papc");
    	
    	
    	if(nifcif != null && !"".equals(nifcif)){
    	    criteria.add(Restrictions.eq("paseg.nifcif", nifcif));
    	}
    	
    	if(codlinea != null){
		    criteria.add(Restrictions.eq("papc.codlinea", codlinea));
    	}
    	
    	if(codplan != null){
		    criteria.add(Restrictions.eq("papc.codplan", codplan));
    	}
    	
    	if (codentidad != null){
    		criteria.add(Restrictions.eq("papc.codEntidad", codentidad));
    	}
    	
    	// provincias
    	if(provincias != null && !provincias.contains(new BigDecimal("99"))){
			if(provincias.size() > 0){
				provincias.add(new BigDecimal("99"));
			}
			//cambiar la lista de BigDecimal a String
			// Rellenar por la izquierda con '0' hasta el maximo de caracteres por campo
			List<String> listProv = new ArrayList<String>();
			Iterator iter = provincias.iterator();
			while (iter.hasNext()){
				cad= (iter.next().toString());
				Integer temp = new Integer(cad);
				listProv.add((String.format("%02d",temp.intValue())));
			}
			criteria.add(Restrictions.in("codprovincia", listProv));
		}
    	
    	// comarcas
    	if(comarcas != null && !comarcas.contains(new BigDecimal("99"))){
			if(comarcas.size() > 0){
				comarcas.add(new BigDecimal("99"));
			}
			//cambiar la lista de BigDecimal a String
			List<String> listCom = new ArrayList<String>();
			Iterator iter = comarcas.iterator();
			while (iter.hasNext()){
				cad= (iter.next().toString());
				Integer temp = new Integer(cad);
				listCom.add((String.format("%02d",temp.intValue())));
			}
			criteria.add(Restrictions.in("codcomarca", listCom));
		}
    	
    	// teminos
    	if(terminos != null && !terminos.contains(new BigDecimal("999"))){
			if(terminos.size() > 0){
				terminos.add(new BigDecimal("999"));
			}
			//cambiar la lista de BigDecimal a String
			List<String> listTerm = new ArrayList<String>();
			Iterator iter = terminos.iterator();
			while (iter.hasNext()){
				cad= (iter.next().toString());
				Integer temp = new Integer(cad);
				listTerm.add((String.format("%03d",temp.intValue())));
			}
			criteria.add(Restrictions.in("codtermino", listTerm));
		}
    	
    	// subterminos
    	if(subterminos != null && !subterminos.contains(new Character('9'))){
			if(subterminos.size() > 0){
				subterminos.add(new Character('9'));
			}
			criteria.add(Restrictions.in("subtermino", subterminos));
		}
        
    	// cultivos
    	if(cultivos != null && !cultivos.contains(new BigDecimal("999"))){
			if(cultivos.size() > 0){
				cultivos.add(new BigDecimal("999"));
			}
			//cambiar la lista de BigDecimal a String
			List<String> listCult = new ArrayList<String>();
			Iterator iter = cultivos.iterator();
			while (iter.hasNext()){
				cad= (iter.next().toString());
				Integer temp = new Integer(cad);
				listCult.add((String.format("%03d",temp.intValue())));
			}
			criteria.add(Restrictions.in("cultivo", listCult));
		}
    	// variedades
    	if(variedades != null && !variedades.contains(new BigDecimal("999"))){
			if(cultivos.size() > 0){
				variedades.add(new BigDecimal("999"));
			}
			//cambiar la lista de BigDecimal a String
			List<String> listVar = new ArrayList<String>();
			Iterator iter = variedades.iterator();
			while (iter.hasNext()){
				cad= (iter.next().toString());
				Integer temp = new Integer(cad);
				listVar.add((String.format("%03d",temp.intValue())));
			}
			criteria.add(Restrictions.in("variedad", listVar));
		}
    	return criteria;
    }
    
    
    /**
     * METODO GENERICO 
     * @param campo
     * @param clase
     */
    public List getCampoClaseDetalle(Long clase, String campo){
		Session session = obtenerSession();
		String query = "SELECT distinct "+campo+" FROM ClaseDetalle claseDetalle " +
                       "WHERE claseDetalle.clase.id =:clase_";
		Query hql = session.createQuery(query);
		hql.setParameter("clase_", clase);
		return hql.list();
	}
    
    /**
     * METODO GENERICO 
     * @param campo
     * @param clase
     * @param linea seguro id
     */
    public List getCampoClaseDetalle(Long clase, Long lineaseguroid, String campo){
		Session session = obtenerSession();
		String query;
		if(clase != null){
			query = "SELECT distinct "+campo+" FROM ClaseDetalle claseDetalle " +
                       	   "WHERE claseDetalle.clase.id =:clase_ AND claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
		}else{
			query = "SELECT distinct "+campo+" FROM ClaseDetalle claseDetalle " +
        	   "WHERE claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
		}

		Query hql = session.createQuery(query);
		if(clase != null){
			hql.setParameter("clase_", clase);
		}
		hql.setParameter("lineaseguroid_", lineaseguroid);
		return hql.list();
	}
    
    public List getlstSisCultClaseDetalle(BigDecimal clase, Long lineaseguroid, String campo){
		Session session = obtenerSession();
		Criteria criteria = session.createCriteria(Clase.class);
		criteria.add(Restrictions.eq("linea.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("clase", clase));
		Clase claseAux = (Clase )criteria.uniqueResult();
		
		// MPM 26-09 - Se controla que la clase no sea nula
		String query = "SELECT distinct "+campo+" FROM ClaseDetalle claseDetalle " +
                       "WHERE " + (claseAux != null && claseAux.getId()!=null ? " claseDetalle.clase.id =:clase_ AND " : "") 
                       + " claseDetalle.clase.linea.lineaseguroid =:lineaseguroid_";
		
		Query hql = session.createQuery(query);		
		if (claseAux != null && claseAux.getId()!=null) hql.setParameter("clase_", claseAux.getId());
		hql.setParameter("lineaseguroid_", lineaseguroid);
		
		return hql.list();
	}
    
    // ---------------------------------------------
    //     [FIN] Listados filtrados por clase
    // ---------------------------------------------
    
    public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException{
    	Session session = obtenerSession();
    	try {
    	session.save(nDat);
    	} catch (Exception ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("[ERROR] al acceder a la BBDD.",ex);
		}
    }
    
    public void actualizaCLOBPacCargas(List<PacParcelas> listPacAsegParcelas, final String clob) throws DAOException {
    	logger.info("init - actualizaCLOBPacCargas");
    	try {	
			PacCargas pacCargas = listPacAsegParcelas.get(0).getPacAsegurados().getPacCargas();
			if (!StringUtils.nullToString(clob).equals("")) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				jdbcTemplate.update(
					"UPDATE TB_PAC_CARGAS SET CONTENIDO_FICHERO=? WHERE ID="
					+ pacCargas.getId(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							lobHandler.getLobCreator().setClobAsString(ps, 1,
									clob);
						}
					});
			}
    	}catch(Exception ex){
    		logger.error("Se ha producido un error durante el acceso a la base de datos ",ex);
    		throw new DAOException("Se ha producido un error en acceso a la BBDD", ex);	
    	}
    	logger.info("end - actualizaCLOBPacCargas");
    }
    
    
    public String executeStoreProcCargarPAC(String fichero, String codUsuario, FormPacCargasBean form) throws DAOException {
    	
		Session session = null;

		try{
			session = obtenerSession();

			String query = "call PQ_CARGAS.pr_carga_pac (?,?,?,?,?,?)";
			CallableStatement statement = session.connection().prepareCall("{" + query + "}");
			statement.setString(1, fichero);			
			statement.setString(2, codUsuario);
			statement.setBigDecimal(3, form.getPlan());
			statement.setBigDecimal(4, form.getEntMed());
			statement.setBigDecimal(5, form.getSubentMed());
			statement.registerOutParameter(6, Types.VARCHAR);

			logger.info("[executeStoreProcCargarPAC] Llamada al PL: " + query + " con los parametros " + 
					fichero + ", " + codUsuario + ", " + form.getPlan() + ", " + form.getEntMed() + ", " + form.getSubentMed());

			statement.execute();
			
			String resultado = statement.getString(6);
			logger.info("[executeStoreProcCargarPAC] resultado carga PAC: '" + StringUtils.nullToString(resultado) + "'");
			
			return StringUtils.nullToString(resultado);
			
		}catch(Exception ex){
			logger.error("[executeStoreProcCargarPAC]Se produjo el siguiente error al ejecutar el procedimiento '" + ex.getMessage());
			throw new DAOException(ex);
		}
	}
    
    /**
     * Llama al procedimiento almacenado encargado de copiar las parcelas de PAC asocidas a la lista de ids de asegurados de PAC indicada como parámetro
     * en la póliza asociada al id 'idPoliza'
     * @param idPoliza
     * @param idClase
     * @param listaIdPacAseg
     * @param listaDVDefecto
     * @return
     * @throws DAOException
     */
    public String cargaParcelasPolizaDesdePAC(Long idPoliza, Long idClase, String listaIdPacAseg, String listaDVDefecto ) throws DAOException {
    	
    	Session session = null;

		try{
			session = obtenerSession();

			String query = "call PQ_CARGA_PARCELAS_PAC.cargaParcelasPolizaDesdePAC (?,?,?,?,?)";
			CallableStatement statement = session.connection().prepareCall("{" + query + "}");
			statement.setLong(1, idPoliza);			
			statement.setLong(2, idClase);
			statement.setString(3, listaIdPacAseg);
			statement.setString(4, listaDVDefecto);			
			statement.registerOutParameter(5, Types.VARCHAR);

			logger.info("[cargaParcelasPolizaDesdePAC] Llamada al PL: " + query + " con los parametros " + 
					idPoliza + ", " + idClase + ", '" + listaIdPacAseg + "', '" + listaDVDefecto + "' ");

			statement.execute();
			
			String resultado = statement.getString(5);
			logger.info("[cargaParcelasPolizaDesdePAC] Resultado carga PAC: '" + StringUtils.nullToString(resultado) + "'");
			
			return (StringUtils.nullToString(resultado));
				
		}catch(Exception ex){
			logger.error("[cargaParcelasPolizaDesdePAC] Se produjo el siguiente error al ejecutar el procedimiento '", ex);
			throw new DAOException(ex);
		}
    	
    }
    
    
    
    public boolean existeESMedEntUsuario (BigDecimal entMed, BigDecimal subentMed, List<BigDecimal> listaEntidades) {
    	Session session = this.obtenerSession();
    	
		String sql= "SELECT COUNT(*) from TB_SUBENTIDADES_MEDIADORAS sm where sm.codentidad=" + entMed + " and " +
					"sm.codsubentidad=" + subentMed + " and " +
					"sm.codentidadnomediadora in " + StringUtils.toValoresSeparadosXComas(listaEntidades, false, true);
		
		List list = session.createSQLQuery(sql).list();
		
		return (( (BigDecimal)list.get(0) ).intValue() > 0) ? true : false;
    }
    
    
    
    public boolean existeArchivoCargado(final String filename){
    	Session session = this.obtenerSession();
    	
		String sql= "select count(*) from TB_PAC_CARGAS c where UPPER(NOMBRE_FICHERO) = '" + filename.toUpperCase() + "'";
		List list = session.createSQLQuery(sql).list();
		
		return (( (BigDecimal)list.get(0) ).intValue() > 0) ? true : false;
    }
    
    public void dropCargaPAC(BigDecimal idCargaPac) throws DAOException{
    	Session session = obtenerSession();
		try{
			String sql = "DELETE TB_PAC_CARGAS WHERE ID = " + idCargaPac;
			session.createSQLQuery(sql).executeUpdate();
		}
		catch(Exception excepcion){
			logger.error("Error al borrar la PAC por id " + idCargaPac, excepcion);
			throw new DAOException("Error al borrar la PAC por id " + idCargaPac, excepcion);
		}
    }
    
    public Map<BigDecimal, BigDecimal> getDatosVarPantalla (Long lineaSeguroId) throws DAOException {
    	
    	List<BigDecimal> list = new ArrayList<BigDecimal>();
    	Map<BigDecimal, BigDecimal> codConceptos = new HashMap<BigDecimal, BigDecimal>();
    	try {
    		Session session = this.obtenerSession();
    		
    		String sql = " select distinct codconcepto from tb_pantallas_configurables p, tb_configuracion_campos c " +
    				     " where p.idpantallaconfigurable = c.idpantallaconfigurable " + 
    					 " and c.lineaseguroid = "+ lineaSeguroId+ " and p.idpantalla = 7";
    		
    		list = session.createSQLQuery(sql).list();
    		for (int i=0;i<list.size();i++) {
    			codConceptos.put(list.get(i), list.get(i));
    		}
    	
    	}catch(Exception excepcion){
			logger.error("Error al recuperar los datos variables", excepcion);
			throw new DAOException("Error al recuperar los datos variables", excepcion);
		}
    	return codConceptos;
	}
    
    
    @Override
	public List<ModuloPoliza> getModulosPoliza(Long idpoliza, Long lineaseguroid)
			throws DAOException {
    	Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ModuloPoliza.class);
			criteria.add(Restrictions.eq("id.idpoliza", idpoliza));			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));			
			
			return criteria.list();
		}
		catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos",ex);
		}
	}
    
    public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	

}
