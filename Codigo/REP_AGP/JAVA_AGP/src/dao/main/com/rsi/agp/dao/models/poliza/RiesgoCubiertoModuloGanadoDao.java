package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;

public class RiesgoCubiertoModuloGanadoDao extends BaseDaoHibernate implements IRiesgoCubiertoModuloGanadoDao {

	
	public List<RiesgoCubiertoModuloGanado> getRiesgosCubiertosModuloGanado(ModuloId id){
		Session session = obtenerSession();
		List<RiesgoCubiertoModuloGanado> lista = new ArrayList<RiesgoCubiertoModuloGanado>();
		
		try{
			Criteria criteria =	session.createCriteria(RiesgoCubiertoModuloGanado.class);
			criteria.add(Restrictions.eq("modulo.id.lineaseguroid", id.getLineaseguroid()));
			criteria.add(Restrictions.eq("modulo.id.codmodulo", id.getCodmodulo()));
			criteria.add(
				Restrictions.disjunction().add(
					Restrictions.isNull("niveleccion")
				).add(
					Restrictions.ne("niveleccion", "D")
				)
			);
			
			lista = criteria.list();
		
		} catch (Exception e) {
			logger.error("Error al obtener la lista de RCM de ganado", e);
		}
		
		return lista;
	}
	
	
	/**
	 * Se cruzan las tablas de RCM y características del módulo para el plan/línea y módulo indicados y se busca si hay
	 * RCM elegible o CM con tipo E, lo que indica que hay comparativas elegibles
	 * @param codModulo
	 * @param lineaseguroid
	 * @return Booleano indicando si para el plan/línea y módulo hay comparativas elegibles
	 */
	public boolean hayComparativasElegibles (String codModulo, Long lineaseguroid) {
		
		Session session = obtenerSession();
		
		try {
			int numElegibles = ((BigDecimal) session.createSQLQuery("SELECT COUNT(*) " +
								   "FROM TB_SC_C_RIESGO_CBRTO_MOD_G RCMG, TB_SC_C_CARACT_MODULO CM " +
								   "WHERE RCMG.LINEASEGUROID = CM.LINEASEGUROID " +
								   "AND RCMG.CODMODULO = CM.CODMODULO " +
								   "AND RCMG.FILAMODULO = CM.FILAMODULO " + 
								   "AND (RCMG.ELEGIBLE = 'S' OR CM.TIPOVALOR='E') " +
								   "AND RCMG.LINEASEGUROID = " + lineaseguroid + " " +
								   "AND RCMG.CODMODULO = '" + codModulo + "'").uniqueResult()).intValue();
			
			return (numElegibles > 0);
			
		} catch (Exception e) {
			logger.error("Error al comprobar si el módulo tiene comparativas elegibles", e);
		}
		
		return false;
	}
	
}
