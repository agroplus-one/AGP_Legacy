package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.cgen.RiesgoId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;

@SuppressWarnings("unchecked")
public class RiesgosDao extends BaseDaoHibernate implements IRiesgosDao{
 
	
	private static final String SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS = "Se ha producido un error durante el acceso a la base de datos";

	@Override
	public List<BigDecimal> getRiesgosCubModulo(Long lineaseguroid, String codmodulo, Character elegible) throws DAOException {
		
		Session session = obtenerSession();
		try {
			
			Criteria criteria = session.createCriteria(RiesgoCubiertoModulo.class);
			
			criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			criteria.add(Restrictions.eq("elegible", elegible));
			
			criteria.setProjection(Projections.distinct(Projections.property("riesgoCubierto.id.codriesgocubierto")));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return criteria.list();
			
		} catch (Exception e) {
			
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
			
		}finally{
		}
	}
	public List<Riesgo> getRiesgosConTasables() throws DAOException {
		
		Session session = obtenerSession();
		try {
			
			String query = "SELECT R.CODRIESGO, R.DESRIESGO, R.CODGRUPOSEGURO " +
						   "FROM TB_SC_C_RIESGOS R WHERE R.CODRIESGO IN (SELECT DISTINCT (RT.CODRIESGOTASABLE) " +
						   "FROM TB_SC_C_RIESGOS_TASABLES RT) ORDER BY R.CODRIESGO";
			
			List<Riesgo> listaRiesgos =  convertirARiesgos (session.createSQLQuery(query).list());
			
			return listaRiesgos;
			
		} catch (Exception e) {
			
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
			
		}
	}
	
	/**
	 * Convierte la lista de objetos en una lista de riesgos
	 * @param resultado
	 * @return
	 */
	private List<Riesgo> convertirARiesgos (List<Object[]> resultado) {
		
		List<Riesgo> listaRiesgos = new ArrayList<Riesgo>();
		
		for (Object[] riesgo : resultado) {
			RiesgoId ri = new RiesgoId ();
			ri.setCodriesgo((String)riesgo[0]);
			ri.setCodgruposeguro((String)riesgo[2]);
			
			Riesgo r = new Riesgo ();
			r.setDesriesgo((String)riesgo[1]);
			r.setId(ri);
			
			listaRiesgos.add(r);
		}
		
		return listaRiesgos;
	}

	@Override
	public List<BigDecimal> getRiesgosElegidosFiltrados(Poliza poliza)throws DAOException {
		Session session = obtenerSession();
		try {
				List<Long> idspar = new ArrayList<Long>();
				for (Parcela p: poliza.getParcelas()) {
					idspar.add(p.getIdparcela());
				}
				String sql = " select distinct CODRIESGOCUBIERTO "+
						 	  "  from o02agpe0.TB_SC_C_RIESGO_CBRTO_MOD "+
						 	  " where LINEASEGUROID = "+poliza.getLinea().getLineaseguroid()+
						 	  " and CODMODULO = '" +poliza.getCodmodulo()+"'"+
						 	  " and ELEGIBLE = 'S'"+
						 	  " and (CODRIESGOCUBIERTO  in (select distinct CODRIESGOCUBIERTO "+
				              " from o02agpe0.TB_COMPARATIVAS_POLIZA  "+
				              " where IDPOLIZA = "+ poliza.getIdpoliza()+
				              " and CODMODULO = '" +poliza.getCodmodulo()+"'"+
				              " and LINEASEGUROID ="+poliza.getLinea().getLineaseguroid()+ ")"+
						 	  " OR CODRIESGOCUBIERTO  in(select distinct cp.CODRIESGOCUBIERTO  "+
				              " from o02agpe0.tb_parcelas_coberturas cp "+
				              " inner join o02agpe0.tb_parcelas p ON cp.idparcela = p.idparcela "+
				              " where p.idpoliza=" + poliza.getIdpoliza()+
						 	  " and cp.codvalor= -1 ))";
				 
				 logger.info("sql " + sql);
				 
				 return session.createSQLQuery(sql).list();
		} catch (Exception e) {
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<Riesgo> getRiesgos(List<BigDecimal> listadoRiesgos, String modulo, Long lineaSeguroid, BigDecimal codLinea)throws DAOException {
		Session session = obtenerSession();
		List<Riesgo> listRiesgos = new ArrayList<Riesgo>();
		try {
				
				String sql = " select ri.CODGRUPOSEGURO, ri.CODRIESGO, ri.DESRIESGO" + 
						"  from o02agpe0.TB_SC_C_RIESGOS ri, o02agpe0.TB_SC_C_LINEAS linn" +
						" where ri.codgruposeguro = linn.codgruposeguro" +
						" and linn.codlinea = " + codLinea +
						" and ri.CODRIESGO in" + 
						"       (select distinct CODRIESGOTASABLE" + 
						"          from o02agpe0.TB_SC_C_RIESGOS_TASABLES" + 
						"         where LINEASEGUROID = " + lineaSeguroid +
						"           and CODMODULO = '" + modulo + "'"+
						"           and CODRIESGOTARIFICABLE in" + 
						"               (select distinct CODRIESGOTARIFICABLE" + 
						"                  from o02agpe0.TB_SC_C_RIESGOS_TARIFICABLES" + 
						"                 where CODRIESGOTARIFICABLE in" + 
						"                       (select distinct CODRIESGOTARIFICABLE" + 
						"                          from o02agpe0.TB_SC_C_REL_CBRTOS_TARIF" + 
						"                         where CODRIESGOCUBIERTO in " + StringUtils.toValoresSeparadosXComas(listadoRiesgos, false) +
						"                           and CODMODULO = '" + modulo + "'" +
						"                           and LINEASEGUROID = " +lineaSeguroid + ")" + 
						"                   and LINEASEGUROID = " + lineaSeguroid + ")" +
						"         )" + 
						" order by ri.CODRIESGO asc ";
				 
				 logger.debug("sql " + sql);
				 List  resultado = session.createSQLQuery(sql).list();
				 if (resultado.size()>0){
						// Se copian los riesgos obtenidos a una lista de Riesgos
						for (int i = 0; i < resultado.size(); i++) {
							Object[] registro = (Object[]) resultado.get(i);
							Riesgo aux = new Riesgo();
							if (registro[0] != null) {
								aux.getGrupoSeguro().setCodgruposeguro((String) registro[0]);
								aux.getId().setCodgruposeguro((String) registro[0]);
							}
							if (registro[1] != null) {
								aux.getId().setCodriesgo((String) registro[1]);
							}
							if (registro[2] != null) {
								aux.setDesriesgo((String) registro[2]);
							}
							listRiesgos.add(aux);
						}
				  }
				 return listRiesgos;
				 
		} catch (Exception e) {
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
		}
	}
	
	

	@Override
	public List<Object[]> getRiesgosReduccionCapital(Long lineaseguroid)throws DAOException {
		Session session = obtenerSession();
		try {
			
			Query sql = session.createSQLQuery("select r.codriesgo, r.desriesgo from tb_lineas l," +
										              "tb_sc_c_lineas ls, " +
										              "tb_sc_c_grupo_seguro g, " +
										              "tb_sc_c_riesgos r " +
												"where l.codlinea = ls.codlinea " +
												"and ls.codgruposeguro = g.codgruposeguro " +
												"and g.codgruposeguro = r.codgruposeguro " +
												"and l.lineaseguroid = :linea")
													.setLong("linea", lineaseguroid);
			
			return sql.list();
			
		} catch (Exception e) {
			
			throw new DAOException(SE_HA_PRODUCIDO_UN_ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, e);
			
		}
	}

	
}
