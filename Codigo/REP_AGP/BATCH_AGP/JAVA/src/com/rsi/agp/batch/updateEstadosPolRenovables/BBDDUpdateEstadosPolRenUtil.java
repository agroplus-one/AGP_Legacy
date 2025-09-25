package com.rsi.agp.batch.updateEstadosPolRenovables;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;

public final class BBDDUpdateEstadosPolRenUtil {
	
	private BBDDUpdateEstadosPolRenUtil() {
	}
	
	private static final Logger logger = Logger.getLogger(UpdateEstadosPolRenovables.class);
	
	private static final String ER_FIELD_STR = "estadoRenovacionAgroseguro";
	private static final String ER_COD_FIELD_STR = "estadoRenovacionAgroseguro.codigo";
	
	// Recupera el listado de polizas renovables a partir del estadoAgroseguro y estadoAgroplus pasados como parametros
	@SuppressWarnings("unchecked")
	protected static List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> getPolizasRenovablesEstados(
			final Session session, final Integer estAgSeguro, final List<Long> lstEstadosAgSeguro,
			final List<Long> lstEstadosAgPlus, int planActual) throws BusinessException {
		String estadosAgroseguro = "";
		List<com.rsi.agp.dao.tables.renovables.PolizaRenovable> lstRenovBBDD;
		Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.renovables.PolizaRenovable.class);		
		crit.createAlias(ER_FIELD_STR, "estadoRenovacionAgroseguro");
		
		if (lstEstadosAgSeguro != null) {
			crit.add(Restrictions.in(ER_COD_FIELD_STR, lstEstadosAgSeguro));
			estadosAgroseguro = lstEstadosAgSeguro.toString();
		}else {
			crit.add(Restrictions.eq("estadoRenovacionAgroseguro.codigo",estAgSeguro.longValue()));
			estadosAgroseguro = estAgSeguro.toString();
		}
		
		crit.add(Restrictions.eq("plan",Long.parseLong(planActual+"")));
		
		lstRenovBBDD = (List<com.rsi.agp.dao.tables.renovables.PolizaRenovable>) crit.list();
		logger.debug("## BBDD estAgSeguro: " + estadosAgroseguro + " plan: "+planActual+" SIZE: " +lstRenovBBDD.size());
		return lstRenovBBDD;
	}
			
	// Recupera la poliza renovable a partir de la referencia 
	protected static com.rsi.agp.dao.tables.renovables.PolizaRenovable getPolizaRenovable(final Session session,
			final String referencia,final Integer estAgSeguro, final List<Long> lstEstadosAgSeguro,int planActual) {
		
		com.rsi.agp.dao.tables.renovables.PolizaRenovable polRenBBDD = null;
		try{
		
			Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.renovables.PolizaRenovable.class);
			
			crit.add(Restrictions.eq("referencia",referencia));
	
			crit.createAlias(ER_FIELD_STR, "estadoRenovacionAgroseguro");
			if (lstEstadosAgSeguro != null) {
				crit.add(Restrictions.in(ER_COD_FIELD_STR,lstEstadosAgSeguro));
			}else
				crit.add(Restrictions.ne(ER_COD_FIELD_STR,estAgSeguro.longValue()));		
			
			crit.add(Restrictions.eq("plan",Long.parseLong(planActual+"")));
			polRenBBDD = (com.rsi.agp.dao.tables.renovables.PolizaRenovable) crit.uniqueResult();
		
		} catch(Exception e ) {
			logger.error("## ERROR en getPolizaRenovable ##  ",e);
		}
		return polRenBBDD;
	}
	
	// Metodo que actualiza el histórico de las polizas renovables
	public static void actualizarHistorico(final Long id, int estadoAGROSEGURO, Long estadoAGROPLUS, 
			final BigDecimal costeTomador, final BigDecimal importeDomiciliar, final Session session, final Character gr) throws BusinessException {
		try{	
			String strHis = "insert into o02agpe0.tb_plz_renov_hist_estados values"+
			"(o02agpe0.sq_plz_renov_hist_estados.nextval,"+id+","+estadoAGROSEGURO+","+estadoAGROPLUS+",sysdate,'BATCH',null,null,null,"+costeTomador+","+importeDomiciliar+","+gr+")";
			logger.debug(strHis);
			Query qHis = session.createSQLQuery(strHis);
			qHis.executeUpdate();
			logger.debug("ACT HIS POL REN: " + id );
		}catch(Exception e){
			logger.error("## ERROR en actualizarHistorico ##  ",e);
			throw new BusinessException(e);
		}
	}

	// Metodo que actualiza el estado en la poliza renovable
	public static void actualizaEstadoPolRenById(final Long id, final int estadoAgroseguro,final String descError, final Session session) throws BusinessException {
		try{
			String strE = " update o02agpe0.tb_polizas_renovables set estado_agroseguro = "+estadoAgroseguro+" where ID ="+id+"";	
			//logger.debug("ACTUALIZACION DE ESTADO POL REN" + id );
			Query query = session.createSQLQuery(strE);
			query.executeUpdate();
			logger.debug("ACT POL REN: " + id );
		}catch(Exception e ){
			logger.error("## ERROR en actualizaEstadoPolRenById ##  ",e);
			throw new BusinessException(e);
		}
		
	}

	// Metodo que actualiza datos en la poliza 
	public static void actualizaDatosPolizaById(final com.rsi.agp.dao.tables.renovables.PolizaRenovable polBBDD,
			final int estadoAgroPLusPol, final String descError, final Session session, int planActual,
			boolean actFechaModifPlz) throws BusinessException {
		String referencia  = polBBDD.getReferencia();
		String codLinea    = polBBDD.getLinea().toString();
		try{
			BigDecimal importe = polBBDD.getCosteTotalTomador(); 
			String strEPol = "update o02agpe0.tb_polizas a set idestado = " + estadoAgroPLusPol + ", importe = " + importe;
			if(actFechaModifPlz){		
				strEPol +=", fecha_modificacion = sysdate ";
				logger.debug("Se actualiza la fecha de modificacion de poliza: " + referencia + " plan:  " + planActual+ " linea: " + codLinea);
			}
			
			strEPol += " where referencia ='" + referencia + "'" +
					" and a.lineaseguroid = (select b.lineaseguroid from o02agpe0.tb_lineas b where b.codplan=" + planActual + " and b.codLinea =" + codLinea + ")";	
			
			logger.debug(strEPol);
			Query query = session.createSQLQuery(strEPol);
			query.executeUpdate();
			logger.debug("ACT POL: " + referencia + "  plan: " + planActual + " linea:  " + codLinea);
		}catch(Exception e ){
			logger.error("## ERROR en actualizaEstadoPolizaById renovable##  ",e);
			throw new BusinessException(e);
		}
		
		try{
			BigDecimal importeDom = polBBDD.getImporteDomiciliar(); 
			String strEPol2 = "update o02agpe0.tb_pagos_poliza pag set pag.importe = "+importeDom+ " where pag.idpoliza = (select pol.idpoliza from o02agpe0.tb_polizas pol where pol.referencia ='"+referencia+"'" +
					" and pol.lineaseguroid = (select b.lineaseguroid from o02agpe0.tb_lineas b where b.codplan="+planActual+" and b.codLinea ="+codLinea+"))";	
			
			logger.debug(strEPol2);
			Query query = session.createSQLQuery(strEPol2);
			query.executeUpdate();
			logger.debug("ACT POL pagos: " + referencia + " plan: "+planActual + "linea: "+codLinea+ " importe: "+ importeDom);
		}catch(Exception e ){
			logger.error("## ERROR en actualizaEstadoPolizaById - importeADomiciliar - renovable##  ",e);
			throw new BusinessException(e);
		}

		try{
			String strEPol3 = "insert into o02agpe0.tb_polizas_historico_estados values (o02agpe0.sq_polizas_historico_estados.nextval, (select idpoliza from o02agpe0.tb_polizas p inner join o02agpe0.tb_lineas l on l.codplan = "
					+ planActual + " and l.codlinea = " + codLinea
					+ " and l.lineaseguroid = p.lineaseguroid where p.referencia = '" + referencia
					+ "'), '@BATCH', sysdate, " + estadoAgroPLusPol + ", null, null, null, null, null)";	
			
			logger.debug(strEPol3);
			Query query = session.createSQLQuery(strEPol3);
			query.executeUpdate();			
		}catch(Exception e ){
			logger.error("## ERROR en actualizaEstadoPolizaById - historico estados - renovable##  ",e);
			throw new BusinessException(e);
		}
	}
	
	// Metodo que actualiza el coste tomador de la poliza
		public static void actualizaCosteTomadorPoliza(final com.rsi.agp.dao.tables.renovables.PolizaRenovable polBBDD, final Session session) throws BusinessException {
			String referencia       = polBBDD.getReferencia();
			String codLinea         = polBBDD.getLinea().toString();
			String codPlan          = polBBDD.getPlan().toString();
			BigDecimal costeTomador = polBBDD.getCosteTotalTomador();
			try{
				String strE = " update o02agpe0.tb_distribucion_costes_2015 set costetomador = "+costeTomador+", totalcostetomador = "+costeTomador+" where idpoliza =(select pol.idpoliza "+	
				"from o02agpe0.tb_polizas pol where pol.referencia ='"+referencia+"'" +" and  pol.lineaseguroid = (select b.lineaseguroid from o02agpe0.tb_lineas b"+
				" where b.codplan="+codPlan+" and b.codLinea = "+codLinea+"))";					
			    logger.debug("update coste tomador: "+strE);
				Query query = session.createSQLQuery(strE);
				query.executeUpdate();

			}catch(Exception e ){
				logger.error("## ERROR en actualizaCosteTomadorPoliza ##  ",e);
				throw new BusinessException(e);
			}
			
		}
	

	// Metodo que devuelve el plan actual
	public static int getPlanActual(final Session session) throws BusinessException {
		try{
			String strPlanActual = "select max(codplan) from o02agpe0.tb_lineas lin ,o02agpe0.tb_sc_c_lineas linn where lin.codlinea = linn.codlinea and linn.codgruposeguro='G01'";
			BigDecimal plan = (BigDecimal) session.createSQLQuery(strPlanActual).uniqueResult();
			logger.debug(" PLAN ACTUAL: " + plan );
			return plan.intValue();
		}catch(Exception e ){
			logger.error("## ERROR en getPlanActual ##  ",e);
			throw new BusinessException(e);
		}
	}
	
	// MODIF TAM (03.12.2018) ESC-4581 ** Inicio //
	// Metodo que devuelve la fecha Inicio de Búsqueda de polizas Renovables
	public static String getFechaInicioUpdatePolRenovables(final Session session) {
		String fec = "";
		try {				
			String queryFec = "select agp_valor from o02agpe0.Tb_Config_Agp where agp_nemo='FECHA_INICIO_RENOVABLES'";
			fec = (String) session.createSQLQuery(queryFec).uniqueResult();
			logger.debug(" ## FECHA INICIO: " + fec + " ## ");
			if (fec != null)
				return fec;
			else 
				return null;
			} catch (Exception ex) {
				logger.debug(" Error al recoger la fecha Inicio Renovables : ",ex );
				return null;
			}
	}
	// MODIF TAM (03.12.2018) ESC-4581 ** Fin //
		
}
