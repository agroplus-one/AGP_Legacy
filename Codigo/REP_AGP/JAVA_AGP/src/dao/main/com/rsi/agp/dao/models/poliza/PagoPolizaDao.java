package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.CriteriaUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.Banco;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class PagoPolizaDao extends BaseDaoHibernate implements IPagoPolizaDao {

	@Override
	public boolean existeBancoDestino(String bancoDestino) throws Exception {
		
		Integer count =0;
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(Banco.class); 
			criteria.add(Restrictions.in("id.codbanco",CriteriaUtils.getCodigosOficina(bancoDestino)));
			count = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
			if (count>0) {
				return true;
			}
			return false;
		}catch (Exception e) {
			logger.error ("existeBancoDestino - Se ha producido un error durante el acceso a la base de datos"+ e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}	
			
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject validaFormaPago(String idpoliza) throws Exception {
		JSONObject params =new JSONObject();
		boolean validaTipoPago = false;
		boolean validaCuentaEntidad = true;
		boolean validaPagoManual = false;
		
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(PagoPoliza.class); 
			criteria.add(Restrictions.eq("poliza.idpoliza", new Long(idpoliza)));
			List<PagoPoliza> pagosPs = criteria.list();
			if (pagosPs!= null && pagosPs.size()>0) {
				PagoPoliza pp = pagosPs.get(0);
				if (pp.getTipoPago()!= null) {
					// se comprueba que el campo tipoPago sea 0 o 1
					if (pp.getTipoPago().compareTo(Constants.PAGO_MANUAL) ==0||
							pp.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA)==0) {
						validaTipoPago = true;
						
						// se comprueba que en caso de que sea cargo_en_cuenta la cuenta pertenece a la entidad
						if (pp.getTipoPago().compareTo(Constants.CARGO_EN_CUENTA)==0) {
							
							BigDecimal entidadCuenta = null;
							
							if(pp.getCccbanco()!=null){
								entidadCuenta = new BigDecimal(pp.getCccbanco().substring(0,4));
								Criteria criteria2 = session.createCriteria(Poliza.class);
								criteria2.add(Restrictions.eq("idpoliza", new Long(idpoliza)));
								Poliza pol = (Poliza) criteria2.uniqueResult();
								
								SubentidadMediadora subentidadMed = pol.getColectivo().getSubentidadMediadora();
								Set<Entidad> colEntidadesCargoCuenta = subentidadMed.getEntidadesCargoCuenta();
								
								Iterator<Entidad> it = colEntidadesCargoCuenta.iterator();
								
								boolean encontrado = false;
								
								while(it.hasNext() && !encontrado){
									Entidad ent = it.next();
									
									if(ent.getCodentidad().compareTo(entidadCuenta)==0){
										encontrado = true;
									}
								}
								validaCuentaEntidad = encontrado;
							}
							validaPagoManual = true;
							
						}else {
							if (pp.getBanco() != null && pp.getFecha() != null
									&& pp.getImporte() != null
									&& pp.getImportePago() != null
									&& pp.getIban() != null
									&& !"".equals(pp.getIban())
									&& pp.getCccbanco() != null
									&& !"".equals(pp.getCccbanco())) {
								validaPagoManual = true;
							} else {
								validaPagoManual = false;
							}
							validaCuentaEntidad = true;							
						}
					}
				}
			}
			if (!validaTipoPago) {
				params.put("mensaje","errorTipoPago");
			}
			else if (!validaCuentaEntidad) {
				params.put("mensaje","errorCuenta");
			}
			else if (!validaPagoManual) {
				params.put("mensaje","errorManual");
			}
		}catch (Exception e) {
			logger.error ("existeBancoDestino - Se ha producido un error durante el acceso a la base de datos"+ e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}
		return params;	
	}
	

	@Override
	public boolean guardaDatosCuenta(Long idpoliza, Character envioIBANAgr)throws Exception {
		try {
			Session session = obtenerSession();
			String sql = " update TB_PAGOS_POLIZA set tipo_pago="+Constants.CARGO_EN_CUENTA+",banco = null,envio_iban_agro = '"+envioIBANAgr+"' where idpoliza="+idpoliza;
			
			session.createSQLQuery(sql).executeUpdate();
			
			String sql2 = "update TB_POLIZAS set pagada="+Constants.POLIZA_NO_PAGADA +" where idpoliza="+idpoliza; 
					
			session.createSQLQuery(sql2).executeUpdate();
			
		}catch (Exception e) {
			logger.error ("guardaDatosCuenta - Se ha producido un error durante el acceso a la base de datos"+ e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}	
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean polizaEsFinanciada(Long idpoliza)throws Exception {
		
			boolean esFinanciada = false;
			Session session = obtenerSession();
			String sql = " select decode(((select count(*) from o02agpe0.tb_polizas po"
					   +" inner join o02agpe0.TB_LINEAS L on po.LINEASEGUROID = L.LINEASEGUROID"
					   +" inner join o02agpe0.TB_POLIZAS_RENOVABLES pr on pr.referencia = po.referencia and pr.plan = l.codplan"
					   +" where pr.coste_total_tomador != pr.importe_domiciliar and po.IDPOLIZA = "+idpoliza+")"
					   +" +(select count(*)"
					   +" from o02agpe0.tb_distribucion_costes_2015 dc"
					   +" where (dc.importe_pago_fracc is not null or"
					   +" dc.importe_pago_fracc_agr is not null)"
					   +" and dc.idpoliza = "+idpoliza+")), 0, 'N', 'S')"
					   +" from dual";

			logger.debug(sql);		
			List resultado = session.createSQLQuery(sql).list();
			if(null!=resultado && resultado.size()>0){
				String res = (String) resultado.get(0);
				if (res.equals("S"))
					return true;
			}
			return esFinanciada;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public PagoPoliza getFormaPago(Long idpoliza) throws Exception {
		List<PagoPoliza> pp = new ArrayList<PagoPoliza>();
		try {
			Session session = obtenerSession();
			Criteria criteria = session.createCriteria(PagoPoliza.class); 
			criteria.add(Restrictions.eq("poliza.idpoliza", idpoliza));
			pp = criteria.list();
			
			if (pp.size()>0) 
				return pp.get(0);
				
			
		}catch (Exception e) {
			logger.error ("getFormaPago - Se ha producido un error durante el acceso a la base de datos"+ e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}	
		return null;
	}

	// Pet. 54046 ** MODIF TAM (29.06.2018) ** Inicio//
	// A�adimos un nuevo m�todo para obtener el indicador que nos informe
	// si las polizas agricolas permiten el env�o 
	@SuppressWarnings("rawtypes")
	public boolean polizaAgrPermiteEnvioIban(BigDecimal codPlan_pol, BigDecimal codLinea_pol)throws Exception {
		
		boolean permiteEnvioIban = false;
		Session session = obtenerSession();
		
		String sql_EnvioIban =
				"select * from "
				+" (select l.codplan, l.codlinea, min(f.feciniciocontrata) FIC " 
                +" from o02agpe0.tb_sc_c_fec_contrat_agr f, "
                +"      o02agpe0.tb_lineas l"
                +" where f.lineaseguroid = l.lineaseguroid "
                +" and l.codplan >= 2018"
                +" group by l.codplan, l.codlinea)"
                +"where FIC >= to_date('01/06/2018', 'DD/MM/YYYY')"
                +"and CODPLAN = "+ codPlan_pol +" and CODLINEA = " +codLinea_pol ;
		
		//!! ATENCI�N !! PARA PRUEBAS EN DESARROLLO.... HE CAMBIADO LAS FECHAS PARA QUE ME 
		//COJA L�NEAS DEL 2016 Y PODER HACER PRUEBAS.....
		/*String sql_EnvioIban_pruebas =
				"select * from "
				+" (select l.codplan, l.codlinea, min(f.feciniciocontrata) FIC " 
                +" from o02agpe0.tb_sc_c_fec_contrat_agr f, "
                +"      o02agpe0.tb_lineas l"
                +" where f.lineaseguroid = l.lineaseguroid "
                +" and l.codplan >= 2015"
                //+" and l.codplan >= 2018"
                +" group by l.codplan, l.codlinea)"
                +"where FIC >= to_date('01/06/2015', 'DD/MM/YYYY')"
                //+"where FIC >= to_date('01/12/2016', 'DD/MM/YYYY')"
                //+"where FIC >= to_date('01/06/2018', 'DD/MM/YYYY')"
                +"and CODPLAN = "+ codPlan_pol +" and CODLINEA = " +codLinea_pol ;*/
				
		logger.debug(sql_EnvioIban);		
		List resultado = session.createSQLQuery(sql_EnvioIban).list();
		if(null!=resultado && resultado.size()>0){
			return true;
		}
		return permiteEnvioIban;
	}
	// Pet. 54046 ** MODIF TAM (29.06.2018) ** Fin//

	@Override
	public void updateIbanbyPoliza(String listIdPolizasMod, String iban, String cccbanco,
			String destinatarioDomiciliacion, String titularCuenta, String iban2, String cccbanco2) throws Exception {
		try {
			Session session = obtenerSession();

			String sql = " update TB_PAGOS_POLIZA set cccbanco='" + cccbanco + "', iban = '" + iban
					+ "', dest_domiciliacion ='" + destinatarioDomiciliacion + "', cccbanco2='" + cccbanco2
					+ "', iban2 = '" + iban2 + "'";
			if (destinatarioDomiciliacion.equals("O"))
				sql = sql + ", titular_cuenta = '" + titularCuenta + "'";
			else
				sql = sql + ", titular_cuenta = ''";
			sql = sql + " where idpoliza in (" + listIdPolizasMod + ")";
			logger.debug("PagoPolizaDao - updateIbanbyPoliza: sql: " + sql);
			session.createSQLQuery(sql).executeUpdate();

		} catch (Exception e) {
			logger.error(
					"updateIbanbyPoliza - Se ha producido un error al actualizar los datos de pago de las polizas" + e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos" + e.getMessage());
		}

	}


	@SuppressWarnings("rawtypes")
	@Override
	public BigDecimal getPctMinimoFinanciacion(BigDecimal codPlan, BigDecimal codLinea, String codModulo) throws Exception {
		Session sesion = obtenerSession();
		BigDecimal resultado = null;
		List res=null;
		try {
			// B�squeda espec�fica
			res= sesion.createSQLQuery (getSqlPctMinimoFinanciacion (codPlan, codLinea, codModulo)).list();
			
			if(null!=res && res.size()>0) resultado = ((BigDecimal)res.get(0));
			// Si no se han encontrado datos, se busca por el plan/l�nea espec�ficos y el m�dulo gen�rico
			else {
				res= sesion.createSQLQuery (getSqlPctMinimoFinanciacion (codPlan, codLinea, Constants.TODOS_MODULOS)).list();
				
				if(null!=res && res.size()>0) resultado = ((BigDecimal)res.get(0));
				// Si no se han encontrado datos, se busca por el plan espec�fico y l�nea y m�dulo gen�ricos
				else {
					res= sesion.createSQLQuery (getSqlPctMinimoFinanciacion (codPlan, Constants.CODLINEA_GENERICA, Constants.TODOS_MODULOS)).list();
					
					if(null!=res && res.size()>0) resultado = ((BigDecimal)res.get(0));
					// Si no se han encontrado datos, se realiza la b�squeda gen�rica
					else {
						res= sesion.createSQLQuery (getSqlPctMinimoFinanciacion (new BigDecimal(9999), Constants.CODLINEA_GENERICA, Constants.TODOS_MODULOS)).list();
					}
					
					if(null!=res && res.size()>0) resultado = ((BigDecimal)res.get(0));
				}
			}
						
		} catch (Exception e) {
			throw e;
		}
		
		return resultado;
	}

	
	/**
	 * Devuelve la consulta para obtener el porcentaje m�nimo de financiaci�n para el plan, l�nea y m�dulo especificados
	 * @param codPlan
	 * @param codLinea
	 * @param codModulo
	 * @return
	 */
	private String getSqlPctMinimoFinanciacion (BigDecimal codPlan, BigDecimal codLinea, String codModulo) {
		String sql = "select pctminimo from O02AGPE0.TB_FINANCIACION_PCT fin " +
					"where fin.CODPLAN=" + codPlan + " and fin.CODLINEA=" +
					codLinea + " and fin.CODMODULO='" + codModulo + "'";
		
		logger.debug("getSqlPctMinimoFinanciacion - " + sql);
		
		return sql;
	}


	@Override
	public JSONObject validaEntidadPermitida(String idpoliza, String entidad) throws Exception {
	JSONObject params =new JSONObject();
	try {
		Session session = obtenerSession();
		
			Criteria criteria2 = session.createCriteria(Poliza.class);
			criteria2.add(Restrictions.eq("idpoliza", new Long(idpoliza)));
			Poliza pol = (Poliza) criteria2.uniqueResult();
			SubentidadMediadora subentidadMed = pol.getColectivo().getSubentidadMediadora();
			Set<Entidad> colEntidadesCargoCuenta = subentidadMed.getEntidadesCargoCuenta();
			Iterator<Entidad> it = colEntidadesCargoCuenta.iterator();
			boolean encontrado = false;
			while(it.hasNext() && !encontrado){
				Entidad ent = it.next();
				
				logger.debug("validaEntidadPermitida - ent.getCodentidad(): " + ent.getCodentidad());
				logger.debug("validaEntidadPermitida - entidad: : " + entidad);
				
				if(ent.getCodentidad().compareTo(new BigDecimal(entidad))==0){
					encontrado = true;
				}
				
				logger.debug("validaEntidadPermitida - encontrado: " + encontrado);
			}
			
			// MPM - 04/04/2017
			// Modificaci�n para que siempre devuelva el objeto JSON y no de problemas al parsear la respuesta en IE8
			params.put("mensaje", encontrado ? "OK" : "errorCuenta");
			
	}catch (Exception e) {
		logger.error ("validaEntidadPermitida - Se ha producido un error durante el acceso a la base de datos"+ e);
		throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
	}
	return params;	
	}
	

	/**
	 * Devuelve la consulta para saber si la linea inicia la contratacion a partir del 01/02/2021
	 */
	public boolean lineaContratacion2019(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan) throws Exception {
		
		return this.lineaContratacionFecha(codPlan_pol, codLinea_pol, isLineaGan, "01/03/2019");
	}
	
	/**
	 * Devuelve la consulta para saber si la linea inicia la contratacion a partir del 01/02/2021
	 */
	public boolean lineaContratacion2021(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan) throws Exception {
		
		return this.lineaContratacionFecha(codPlan_pol, codLinea_pol, isLineaGan, "01/02/2021");
	}
	
	@SuppressWarnings("rawtypes")
	private boolean lineaContratacionFecha(BigDecimal codPlan_pol, BigDecimal codLinea_pol, boolean isLineaGan,
			String fecha) throws Exception {
		Session session = obtenerSession();
		String sqlContratacion = "";
		String anhoStr = fecha.substring(fecha.lastIndexOf('/') + 1);
		if (isLineaGan) {
			sqlContratacion = "select * from (select l.codplan, l.codlinea, min(fg.fec_contrat_ini) FIC"
					+ " from o02agpe0.tb_sc_c_fec_contrat_g fg, o02agpe0.tb_lineas l"
					+ " where fg.lineaseguroid = l.lineaseguroid and l.codplan >= " + anhoStr + " group by l.codplan, l.codlinea)"
					+ " where FIC >= to_date('"	+ fecha + "', 'DD/MM/YYYY') and CODPLAN = " + codPlan_pol + " and CODLINEA = " + codLinea_pol;
		} else {
			sqlContratacion = "select * from (select l.codplan, l.codlinea, min(f.feciniciocontrata) FIC"
					+ " from o02agpe0.tb_sc_c_fec_contrat_agr f, o02agpe0.tb_lineas l"
					+ " where f.lineaseguroid = l.lineaseguroid  and l.codplan >= " + anhoStr + " group by l.codplan, l.codlinea)"
					+ " where FIC >= to_date('"	+ fecha + "', 'DD/MM/YYYY') and CODPLAN = " + codPlan_pol + " and CODLINEA = " + codLinea_pol;
		}
		logger.debug(sqlContratacion);
		List resultado = session.createSQLQuery(sqlContratacion).list();
		return null != resultado && resultado.size() > 0;
	}
}
