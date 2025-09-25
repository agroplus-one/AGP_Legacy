package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;

public class RiesgoCubiertoModuloDao extends BaseDaoHibernate implements IRiesgoCubiertoModuloDao {

	
	@SuppressWarnings("unchecked")
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo){
		logger.debug("init - [RiesgoCubiertoModuloDao] getRiesgosCubiertosModulo");
		
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		
		try{
			
			Criteria criteria =	session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][RiesgoCubiertoModuloDao][getRiesgosCubiertosModulo]Error lectura BD", e);
		}
		
		logger.debug("end - [RiesgoCubiertoModuloDao] getRiesgosCubiertosModulo");
		return lista;
	}
	
	public String getDescRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal codRiesgoCub) 
			throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getRiesgosCubiertosModulo");
		
		Session session = obtenerSession();
		try{
			
			 String sql = "select c.desriesgocubierto from TB_SC_C_RIESGOS_CUBIERTOS c where c.LINEASEGUROID = "+ lineaseguroid +
					 " and c.CODMODULO = " + codmodulo + " and c.CODRIESGOCUBIERTO = " + codRiesgoCub;
	       
			 SQLQuery query = session.createSQLQuery(sql);
			 
			 return query.uniqueResult().toString();	
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del Riesgo cubierto elegido: getRiesgoCubiertosModulo()", e);
			throw e;
		}	
	}
	
	public String getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,BigDecimal codRiesgoCub) throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getfilaRiesgoCubModulo");
		Session session = obtenerSession();
		try{			
			 String sql = "select c.filamodulo from tb_sc_c_riesgo_cbrto_mod_g c where c.LINEASEGUROID = "+ lineaseguroid +
					 " and c.CODMODULO = " + codmodulo + " and c.CODCONCEPTOPPALMOD = " + cPmodulo + " and c.CODRIESGOCUBIERTO = " + codRiesgoCub;	       
			 SQLQuery query = session.createSQLQuery(sql);			 
			 return query.uniqueResult().toString();
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del Riesgo cubierto elegido: getRiesgoCubiertosModulo()", e);
			throw e;
		}		
	}
	
	public String getDescCalcIndem(int valor) throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getDescCalcIndem");		
		Session session = obtenerSession();
		try{		
			 String sql = "select c.descalculo from tb_sc_c_calc_indemnizacion c where c.codcalculo = "+ valor;	       
			 SQLQuery query = session.createSQLQuery(sql);			 
			 return query.uniqueResult().toString();	
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del CalcIndemnizacion elegido: getDescCalcIndem()", e);
			throw e;
		}
	}
	
	public String getDescMinIndem(int valor) throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getDescMinIndem");
		Session session = obtenerSession();
		try{			
			 String sql = "select c.descalculo from tb_sc_c_min_indem_eleg c where c.codcalculo = "+ valor;
			 SQLQuery query = session.createSQLQuery(sql);			 
			 return query.uniqueResult().toString();	
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del minimo indemnizable elegido: getDescMinIndem()", e);
			throw e;
		}
	}
	
	public String getDescDatoVarRiesgo(BigDecimal cpto) throws Exception{
		logger.debug("init - [RiesgoCubiertoModuloDao] getDescDatoVarRiesgo");
		Session session = obtenerSession();
		try{			
			 String sql = "select c.nomconcepto from tb_sc_dd_dic_datos c where c.codconcepto = "+ cpto;
			 SQLQuery query = session.createSQLQuery(sql);			 
			 return query.uniqueResult().toString();	
		} catch (Exception e) {
			logger.fatal("Error al obtener la descripcion del dato variable del riesgo: getDescDatoVarRiesgo()", e);
			throw e;
		}
	}
	
	
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModuloPoliza(ModuloId id){
		return getRiesgosCubiertosModulo (id, false);
	}
	
	
	/**
	 * Devuelve la lista de RCM asociados al módulo pasado como parámetro, elegibles o no 
	 * @param id Módulo asociado a los RCm
	 * @param elegible Indica si se filtra por RCM elegibles
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo (ModuloId id, boolean elegible){
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		
		try{
			Criteria criteria =	session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq("modulo.id.lineaseguroid", id.getLineaseguroid()));
			criteria.add(Restrictions.eq("modulo.id.codmodulo", id.getCodmodulo()));
			criteria.add(
				Restrictions.disjunction().add(
					Restrictions.isNull("niveleccion")
				).add(
					Restrictions.ne("niveleccion", "D")
				)
			);
			
			// Si se indica en el parámetro, se listan sólo los RCM elegibles
			if (elegible) criteria.add(Restrictions.eq("elegible", 'E'));
			
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("Error al obtener la lista de RCM para polizas", e);
		}
		
		return lista;
	}
	
	
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	/**
	 * Devuelve la lista de RCM asociados al módulo pasado como parámetro, elegibles 
	 * @param id Módulo asociado a los RCm
	 * @param elegible Indica si se filtra por RCM elegibles
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiesgoCubiertoModulo> getRiesgosCubModuloCalcRendimiento_old(Long lineaseguroid, String codmodulo) throws Exception{
		Session session = obtenerSession();
		
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		
		logger.debug("init - [RiesgoCubiertoModuloDao] getRiesgosCubModuloCalcRendimiento");
		
		try{			
			
			String sql = "select c.* from tb_sc_c_riesgo_cbrto_mod c where c.LINEASEGUROID = "+ lineaseguroid +
					 " and c.CODMODULO = '" + codmodulo + "' and c.elegible = 'S' and c.niveleccion = 'C' ";	      
			
			 SQLQuery query = session.createSQLQuery(sql);
			 
			 lista = query.list();
			 
		} catch (Exception e) {
			logger.fatal("Error al obtener la lista de RCM para poliza", e);
			throw e;
		}
		
		return lista;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<RiesgoCubiertoModulo> getRiesgosCubModuloCalcRendimiento(Long lineaseguroid, String codmodulo){
		logger.debug("init - [RiesgoCubiertoModuloDao] getRiesgosCubModuloCalcRendimiento");
		
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		Character elegible = 'S';
		Character niveleccion = 'C';
		
		
		try{
			
			Criteria criteria =	session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			criteria.add(Restrictions.eq("elegible", elegible));
			criteria.add(Restrictions.eq("niveleccion", niveleccion));
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][RiesgoCubiertoModuloDao][getRiesgosCubModuloCalcRendimiento]Error lectura BD", e);
		}
		
		logger.debug("end - [DatosParcelaFLDao] getRiesgosCubModuloCalcRendimiento");
		return lista;
	}
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Fin */
	
}
