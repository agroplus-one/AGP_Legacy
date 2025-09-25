package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ComparativaSitActDao extends BaseDaoHibernate implements IComparativaSitActDao {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * Devuelve la descripción del Garantizado asociado al código indicado por parámetro
	 */
	public String getDesGarantizado (BigDecimal codGarantizado) {
		
		List<Garantizado> objects = this.getObjects(Garantizado.class, "codgarantizado", codGarantizado);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDesgarantizado();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la descripción del CalculoIndemnizacion asociado al código indicado por parámetro
	 */
	public String getDesCalcIndem (BigDecimal codcalculo) {
		
		List<CalculoIndemnizacion> objects = this.getObjects(CalculoIndemnizacion.class, "codcalculo", codcalculo);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDescalculo();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la descripción del PctFranquiciaElegible asociado al código indicado por parámetro
	 */
	public String getDesPctFranquicia (BigDecimal codpctfranquiciaeleg) {
		
		List<PctFranquiciaElegible> objects = this.getObjects(PctFranquiciaElegible.class, "codpctfranquiciaeleg", codpctfranquiciaeleg);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDespctfranquiciaeleg();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la descripción del MinimoIndemnizableElegible asociado al código indicado por parámetro
	 */
	public String getDesMinIndem (BigDecimal pctminindem) {
		
		List<MinimoIndemnizableElegible> objects = this.getObjects(MinimoIndemnizableElegible.class, "pctminindem", pctminindem);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDesminindem();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la descripción del TipoFranquicia asociado al código indicado por parámetro
	 */
	public String getDesTipoFranqIndem (String codtipofranquicia) {
		
		List<TipoFranquicia> objects = this.getObjects(TipoFranquicia.class, "codtipofranquicia", codtipofranquicia);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDestipofranquicia();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la descripción del CapitalAseguradoElegible asociado al código indicado por parámetro
	 */
	public String getDesCapitalAseg (BigDecimal pctcapitalaseg) {
		
		List<CapitalAseguradoElegible> objects = this.getObjects(CapitalAseguradoElegible.class, "pctcapitalaseg", pctcapitalaseg);
		
		if (objects != null && objects.size()>0) {
			return objects.get(0).getDescapitalaseg();
		}
		
		return "";
	}
	
	/**
	 * Devuelve la FilaModulo asociada a la linea, modulo y concepto cubierto elegible indicado en los parámetros
	 */
	public BigDecimal getFilaModulo (Long lineaseguroid, String codModulo, int codconcepto,
			BigDecimal codConceptopplaMod, BigDecimal codRiesgoCub) {
		
		try {
			Session session = obtenerSession();
			String sql = "select cc.filamodulo " +
						 " from tb_sc_c_caract_modulo      cc, " +
						 " tb_sc_c_concepto_cbrto_mod cm, " +
						 " tb_sc_c_riesgo_cbrto_mod rcm " +
						 " where cc.lineaseguroid = " + lineaseguroid + " " +
						 " and cc.codmodulo = '" + codModulo + "' " +
						 " and cc.tipovalor = 'E' " +
						 " and cc.lineaseguroid = cm.lineaseguroid " +
						 " and cc.codmodulo = cm.codmodulo " +
						 " and cc.columnamodulo = cm.columnamodulo " +
						 " and rcm.lineaseguroid = cc.lineaseguroid " +
						 " and rcm.filamodulo = cc.filamodulo " +
						 " and rcm.codmodulo = cc.codmodulo " +
						 " and rcm.codconceptoppalmod = "+ codConceptopplaMod +
						 " and rcm.codriesgocubierto = " + codRiesgoCub +
						 " and cm.codconceptocbrtomod = " + codconcepto;
			
			return ((BigDecimal)session.createSQLQuery(sql).list().get(0));
        
        }catch (Exception e) {
			logger.error("Error al obtener la filamodulo", e);
		}
		
		return new BigDecimal(-1);
	}
	
	/**
	 * Devuelve la FilaModulo asociada a la linea, modulo y concepto cubierto elegible indicado en los parámetros
	 */	
	public BigDecimal getFilaModuloGanado (Long lineaseguroid, String codModulo,
			BigDecimal codConceptopplaMod, BigDecimal codRiesgoCub) {
		try {
			Session session = obtenerSession();
			String sql = "select cc.filamodulo " +
						 " from " +
						 " o02agpe0.tb_sc_c_caract_modulo      cc, " +
						 " o02agpe0.tb_sc_c_concepto_cbrto_mod cm, " +
						 " o02agpe0.tb_sc_c_riesgo_cbrto_mod_g rcm " +
						 " where cc.lineaseguroid = " + lineaseguroid + " " +
						 " and cc.codmodulo = '" + codModulo + "' " +
						 " and cc.tipovalor = 'E' " +
						 " and cc.lineaseguroid = cm.lineaseguroid " +
						 " and cc.codmodulo = cm.codmodulo " +
						 " and cc.columnamodulo = cm.columnamodulo " +
						 " and rcm.lineaseguroid = cc.lineaseguroid " +
						 " and rcm.filamodulo = cc.filamodulo " +
						 " and rcm.codmodulo = cc.codmodulo " +
						 " and rcm.codconceptoppalmod = "+ codConceptopplaMod +
						 " and rcm.codriesgocubierto = " + codRiesgoCub +
						 " and cm.codconceptocbrtomod  in ( select cm.codconceptocbrtomod" +
						 " from o02agpe0.tb_sc_c_concepto_cbrto_mod cm where cm.lineaseguroid=" + lineaseguroid + " " +
						 " and cm.codmodulo= '" + codModulo + "')" ;
			List lista=session.createSQLQuery(sql).list();
			if(lista!=null && lista.size()>0){
				return ((BigDecimal)session.createSQLQuery(sql).list().get(0));
			}
        logger.debug("no se ha encontrado filaModulo para lineaseguroid = " + lineaseguroid +" codmodulo ="  + codModulo +" codconceptoppalmod = "+ codConceptopplaMod +" codriesgocubierto = " + codRiesgoCub );
        }catch (Exception e) {
			logger.error("Error al obtener la filamodulo", e);
		}
		
		return new BigDecimal(-1);
	}
	

}
