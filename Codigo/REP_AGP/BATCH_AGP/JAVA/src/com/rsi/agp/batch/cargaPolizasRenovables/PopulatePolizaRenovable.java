package com.rsi.agp.batch.cargaPolizasRenovables;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.renovables.BonificacionRecargoRen;
import com.rsi.agp.dao.tables.renovables.CosteGrupoNegocioRen;
import com.rsi.agp.dao.tables.renovables.FraccionamientoRen;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.GastosRenovacionAplicados;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.estadoRenovacion.GrupoNegocioPrimas;
import es.agroseguro.estadoRenovacion.Renovacion;


public final class PopulatePolizaRenovable {
	
	private PopulatePolizaRenovable() {
	}
	private static final Logger logger = Logger.getLogger(PopulatePolizaRenovable.class);
		
	
	public static void populateGastos(final PolizaRenovable polizaHbm, final Renovacion polWs) throws Exception {
		Set<GastosRenovacion> gasRen = new HashSet<GastosRenovacion>();
		GastosRenovacion gas = new GastosRenovacion();
		for (int i = 0; i < polWs.getGastosArray().length; i++) {
			if (polWs.getGastosArray(i).getAdministracion() != null) {
				gas.setAdministracion(polWs.getGastosArray(i).getAdministracion());
			}
			if (polWs.getGastosArray(i).getAdquisicion() != null) {
				gas.setAdquisicion(polWs.getGastosArray(i).getAdquisicion());
			}
			if (polWs.getGastosArray(i).getComisionMediador() != null) {
				gas.setComisionMediador(polWs.getGastosArray(i).getComisionMediador());
			}
			if (polWs.getGastosArray(i).getGrupoNegocio() != null
					&& !polWs.getGastosArray(i).getGrupoNegocio().toString().equals("")) {
				gas.setGrupoNegocio(polWs.getGastosArray(i).getGrupoNegocio().charAt(0));
			}
			gas.setPolizaRenovable(polizaHbm);
			gasRen.add(gas);
		}
		if (gasRen.size() > 0) {
			polizaHbm.setGastosRenovacions(gasRen);
			logger.debug(" Gastos insertados..");
		}
	}
	
	public static void populateGastosAplicados(PolizaRenovable polizaHbm, final Renovacion polizaRen,
			final PolizasPctComisionesDao polizasPctComisionesDao, final Long linId) throws Exception {
		Set<GastosRenovacionAplicados> gasRenApl = new HashSet<GastosRenovacionAplicados>();

		for (int i = 0; i < polizaRen.getGastosAplicadosArray().length; i++) {
			GastosRenovacionAplicados gasApl = new GastosRenovacionAplicados();

			if (polizaRen.getGastosAplicadosArray(i).getGrupoNegocio() != null
					&& !polizaRen.getGastosAplicadosArray(i).getGrupoNegocio().toString().equals("")) {
				gasApl.setGrupoNegocio(polizaRen.getGastosAplicadosArray(i).getGrupoNegocio().charAt(0));
			}
			if (polizaRen.getGastosAplicadosArray(i).getAdministracion() != null) {
				gasApl.setAdministracion(polizaRen.getGastosAplicadosArray(i).getAdministracion());
			}
			if (polizaRen.getGastosAplicadosArray(i).getImporteAdministracion() != null) {
				gasApl.setImporteAdministracion(polizaRen.getGastosAplicadosArray(i).getImporteAdministracion());
			}
			if (polizaRen.getGastosAplicadosArray(i).getAdquisicion() != null) {
				gasApl.setAdquisicion(polizaRen.getGastosAplicadosArray(i).getAdquisicion());
			}
			if (polizaRen.getGastosAplicadosArray(i).getImporteAdquisicion() != null) {
				gasApl.setImporteAdquisicion(polizaRen.getGastosAplicadosArray(i).getImporteAdquisicion());
			}
			if (polizaRen.getGastosAplicadosArray(i).getComisionMediador() != null) {
				gasApl.setComisionMediador(polizaRen.getGastosAplicadosArray(i).getComisionMediador());
			}
			if (polizaRen.getGastosAplicadosArray(i).getImporteComisionMediador() != null) {
				gasApl.setImporteComisionMediador(polizaRen.getGastosAplicadosArray(i).getImporteComisionMediador());
			}

			// calculamos los porcentajes aplicados Entidad y E-S Medidadora
			/* recogemos los datos del mto de comisiones por E-S Mediadora */
			if (gasApl.getComisionMediador() != null) {

				Object[] comisionesESMed = polizasPctComisionesDao.getComisionesESMed(linId,
						new BigDecimal(polizaHbm.getColectivoRenovacion().getCodentidadmed()),
						new BigDecimal(polizaHbm.getColectivoRenovacion().getCodsubentmed()),
						new BigDecimal(polizaHbm.getLinea()), new BigDecimal(polizaHbm.getPlan()), null);

				/*
				 * Object[] comisionesESMed = polizasPctComisionesDao.getComisionesESMed (new
				 * Long(6263), new BigDecimal(3190), new BigDecimal(0), new BigDecimal(415),new
				 * BigDecimal(2016),null);
				 */

				// CALCULAMOS LOS PORCENTAJES A PARTIR DEL TOTAL: COMISIONMEDIADOR
				if (comisionesESMed != null) {
					BigDecimal pctEntidad = (BigDecimal) comisionesESMed[0];
					BigDecimal pctEsMediadora = (BigDecimal) comisionesESMed[1];
					BigDecimal comAplEntidad = gasApl.getComisionMediador()
							.multiply(pctEntidad.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal comAplEsMed = gasApl.getComisionMediador()
							.multiply(pctEsMediadora.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
					gasApl.setComisionAplEntidad(comAplEntidad);
					gasApl.setComisionAplEsMed(comAplEsMed);
				}
			}
			gasApl.setPolizaRenovable(polizaHbm);
			gasRenApl.add(gasApl);
		}
		if (gasRenApl.size() > 0) {
			polizaHbm.setGastosRenovacionAplicados(gasRenApl);
			logger.debug(" Gastos aplicados insertados..");
		}

	}
		
	public static void populateCosteGrupoNegocio(PolizaRenovable polizaHbm, final Renovacion polizaRen)
			throws Exception {
		Set<CosteGrupoNegocioRen> costeGNRen = new HashSet<CosteGrupoNegocioRen>();
		for (int i = 0; i < polizaRen.getCosteGrupoNegocioArray().length; i++) {
			CosteGrupoNegocioRen costeGN = new CosteGrupoNegocioRen();
			if (polizaRen.getCosteGrupoNegocioArray(i).getGrupoNegocio() != null
					&& !polizaRen.getCosteGrupoNegocioArray(i).getGrupoNegocio().toString().equals("")) {
				costeGN.setGrupoNegocio(polizaRen.getCosteGrupoNegocioArray(i).getGrupoNegocio().charAt(0));
			}
			if (polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercial() != null) {
				costeGN.setPrimaComercial(polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercial());
			}
			if (polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercialNeta() != null) {
				costeGN.setPrimaComercialNeta(polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercialNeta());
			}
			if (polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercialBaseNeta() != null) {
				logger.debug(" PrimaComercialBaseNeta: "
						+ polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercialBaseNeta());
				costeGN.setPrimaComercialBaseNeta(polizaRen.getCosteGrupoNegocioArray(i).getPrimaComercialBaseNeta());
			}

			// bonificacionRecargo
			populateBonificacionRecargo(costeGN, polizaRen.getCosteGrupoNegocioArray(i));

			costeGN.setPolizaRenovable(polizaHbm);
			costeGNRen.add(costeGN);
		}
		if (costeGNRen.size() > 0) {
			polizaHbm.setCostesGrupoNegocioRen(costeGNRen);
			logger.debug(" Coste Grupo Negocio insertado..");
		}
	}
		
	public static void populateBonificacionRecargo(final CosteGrupoNegocioRen costeGN,
			final GrupoNegocioPrimas gNegPrimas) throws Exception {
		Set<BonificacionRecargoRen> bonificacionRecargoRens = new HashSet<BonificacionRecargoRen>();
		for (int i = 0; i < gNegPrimas.getBonificacionRecargoArray().length; i++) {
			BonificacionRecargoRen bonRec = new BonificacionRecargoRen();
			Long codigo = Long.parseLong(Integer.toString(gNegPrimas.getBonificacionRecargoArray(i).getCodigo()));
			bonRec.setCodigo(codigo);

			if (gNegPrimas.getBonificacionRecargoArray(i).getImporte() != null) {
				bonRec.setImporte(gNegPrimas.getBonificacionRecargoArray(i).getImporte());
			}
			bonRec.setCosteGrupoNegocioRen(costeGN);
			bonificacionRecargoRens.add(bonRec);
		}
		if (bonificacionRecargoRens.size() > 0) {
			costeGN.setBonificacionRecargoRens(bonificacionRecargoRens);
			logger.debug(" BonificacionRecargo insertada..");
		}
	}
		
	public static void populateFraccionamiento(final PolizaRenovable polizaHbm, final Renovacion polizaRen)
			throws Exception {
		if (polizaRen.getFraccionamiento() != null) {
			FraccionamientoRen fr = new FraccionamientoRen();
			fr.setIdPolizaRenovable(polizaHbm.getId());
			Long periodo = Long.parseLong(Integer.toString(polizaRen.getFraccionamiento().getPeriodo()));
			fr.setPeriodo(periodo);
			if (polizaRen.getFraccionamiento().getAval() != null) {
				es.agroseguro.estadoRenovacion.Aval aval = polizaRen.getFraccionamiento().getAval();
				Long numero = Long.parseLong(Integer.toString(aval.getNumero()));
				fr.setNumeroAval(numero);
				if (aval.getImporte() != null) {
					fr.setImporteAval(aval.getImporte());
				}
			}
			polizaHbm.setFraccionamientoRen(fr);
			logger.debug(" Fraccionamiento insertado..");
		}
			
			// pruebas fracc
//			FraccionamientoRen fr = new FraccionamientoRen();
//			fr.setIdPolizaRenovable(polizaHbm.getId());
//			fr.setPeriodo(new Long(2));
//			fr.setImporteAval(new BigDecimal(3.3));
//			fr.setNumeroAval(new Long(4));
//			polizaHbm.setFraccionamientoRen(fr);
//			logger.debug(" Fraccionamiento PRUEBA insertado..");
			// FIN pruebas fracc
	}
		
	// M�todo que se encarga de resetear los datos de gastos, gastos aplicados,
	// coste gurpo negocio y fraccionamiento
	// pas�ndole el id de la p�liza renovable
	public static void borrarDatosPolizaRenovable(final Long idPolizaRen, final Session session) throws Exception {
		try {
			logger.debug(" Init - borrarDatosPolizaRenovable");
			String str = "";
			Query query = null;
			// los gastos no se modifican: sigpe 8798 13/03/17
			/*
			 * str =
			 * " delete o02agpe0.tb_gastos_renovacion gas where gas.idpolizarenovable="
			 * +idPolizaRen; //logger.debug(str); Query query = session.createSQLQuery(str);
			 * query.executeUpdate();
			 */
			str = " delete o02agpe0.tb_gastos_renovacion_aplicados gasApl where gasApl.idpolizarenovable="
					+ idPolizaRen;
			logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();

			str = " delete o02agpe0.tb_coste_grupo_negocio_ren ren where ren.idpolizarenovable=" + idPolizaRen;
			// logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();

			str = " delete o02agpe0.tb_fraccionamiento_ren fra where fra.idpolizarenovable=" + idPolizaRen;
			// logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();
			logger.debug("fin borrarDatosPolizaRenovable");
		} catch (Exception e) {
			logger.error("## ERROR en borrado datos poliza renovable:" + idPolizaRen + "##  ", e);
			throw e;
		}
	}
		
		
	public static void borrarExplotacionesByIdPoliza(final Long idPoliza, final Session session) throws Exception {
		try {
			String str = "";
			Query query = null;
			str = " delete o02agpe0.tb_explotaciones exp where exp.idpoliza=" + idPoliza;
			logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("## ERROR en borrado de las explotaciones de la poliza:" + idPoliza + "##  ", e);
			throw e;
		}
	}

	public static void borrarPagosByIdPoliza(final Long idPoliza, final Session session) throws Exception {
		try {
			String str = "";
			Query query = null;
			str = " delete o02agpe0.tb_pagos_poliza pag where pag.idpoliza=" + idPoliza;
			logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("## ERROR en borrado de los pagos de la poliza:" + idPoliza + "##  ", e);
			throw e;
		}
	}

	public static void borrarPctComisionesByIdPoliza(final Long idPoliza, final Session session) throws Exception {
		try {
			String str = "";
			Query query = null;
			str = " delete o02agpe0.tb_polizas_pct_comisiones pag where pag.idpoliza=" + idPoliza;
			logger.debug(str);
			query = session.createSQLQuery(str);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("## ERROR en borrado de pctComisiones de la poliza:" + idPoliza + "##  ", e);
			throw e;
		}
	}
		
	public static void guardaPagoPoliza(final PagoPoliza pago, final Session session) throws Exception {
		try {

			Query query = null;
			String fecha = "";
			StringBuilder qBuilder = new StringBuilder();
			if (pago.getFecha() != null) {
				Date fec = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				fecha = sdf.format(fec);
			}

			qBuilder.append("insert into o02agpe0.tb_pagos_poliza values(o02agpe0.sq_pagos_poliza.nextval,"
					+ pago.getPoliza().getIdpoliza() + "," + "'" + pago.getFormapago());
			if (pago.getFecha() != null) {
				qBuilder.append("',to_date('" + fecha + "','DD/MM/YYYY'),");
			} else {
				qBuilder.append("',null,");
			}
			qBuilder.append(pago.getImporte() + ",'" + pago.getCccbanco() + "',null,null,null," + pago.getTipoPago()
					+ ",'" + pago.getIban() + "'," + pago.getBanco());

			if (pago.getDomiciliado() != null) {
				qBuilder.append(",'" + pago.getDomiciliado() + "',");
			} else {
				qBuilder.append("',null,");
			}
			qBuilder.append("null,null,null,");
			qBuilder.append(pago.getImportePago());
			qBuilder.append(",null,null)");
			logger.debug(qBuilder.toString());
			query = session.createSQLQuery(qBuilder.toString());
			query.executeUpdate();
		} catch (Exception e) {
			logger.error(
					"## ERROR en la inserci�n de pagos p�liza con idPoliza:" + pago.getPoliza().getIdpoliza() + " ##  ",
					e);
			throw e;
		}
	}

	// Pet. 54046 ** MODIF TAM (18.09.2018) ** Inicio */
	// Updateamos los datos del pago en caso de que el valor de domiciliado sea 'S'
	public static void actualizaPagosPoliza(final PagoPoliza pago, final Session session) throws Exception {
		try {

			Query query = null;
			StringBuilder qBuilderUpdte = new StringBuilder();

			qBuilderUpdte.append(
					"update o02agpe0.tb_pagos_poliza p" + " set p.titular_cuenta = " + "'" + pago.getTitularCuenta()
							+ "', " + " p.dest_domiciliacion = " + "'" + pago.getDestinatarioDomiciliacion() + "', "
							+ " p.envio_iban_agro = " + "'" + pago.getEnvioIbanAgro() + "', " + " p.cccbanco = " + "'"
							+ pago.getCccbanco() + "', " + " p.iban = " + "'" + pago.getIban() + "', "
							+ " p.cccbanco2 = " + "'" + pago.getCccbanco2() + "', " + " p.iban2 = " + "'"
							+ pago.getIban2() + "' " + " where p.idpoliza = " + pago.getPoliza().getIdpoliza());

			logger.debug(qBuilderUpdte.toString());
			query = session.createSQLQuery(qBuilderUpdte.toString());
			query.executeUpdate();
		} catch (Exception e) {
			logger.error(
					"## ERROR en el update de pagos p�liza con idPoliza:" + pago.getPoliza().getIdpoliza() + " ##  ",
					e);
			throw e;
		}
	}
	
	public static List<?> polizasSinPagos(final Session session){
		try {
			logger.debug(">>Prueba obtener polzias sin pagos");
			
			String sql = "select p.idpoliza, p.referencia, l.codplan, l.codlinea from o02agpe0.tb_polizas p, o02agpe0.tb_lineas l "
					+ "where p.lineaseguroid = l.lineaseguroid and l.codplan  = 2023 and p.idestado in (8, 14, 16) "
					+ "and p.idpoliza not in (select idpoliza from o02agpe0.TB_PAGOS_POLIZA)";

			logger.debug(sql);
			
			List<?> res = session.createSQLQuery(sql).list();
			logger.debug(">>>>Se han obtenido "+res.size()+" obtener polzias sin pagos");
			
			return res;
		} catch (Exception e) {
			logger.error("## >>Error obtener polzias sin pagos");
			return null;
		}
	}
	
	public static List<?> polizasRenovables(final Session session){
		try {		
			String sql = "select p.idpoliza, p.referencia, l.codlinea, l.codplan, dc.primacomercialneta, nvl(g.comision_apl_entidad,0), nvl(g.comision_apl_es_med,0), dc.grupo_negocio, r.id "
						+ "from o02agpe0.tb_polizas p, o02agpe0.tb_polizas_renovables r, o02agpe0.tb_lineas l, o02agpe0.tb_distribucion_costes_2015 dc, o02agpe0.tb_gastos_renovacion_aplicados g "
						+ "where p.lineaseguroid = l.lineaseguroid "
						+ "and p.idpoliza = dc.idpoliza "
						+ "and p.referencia = r.referencia and l.codlinea = r.linea and l.codplan = r.plan "
						+ "and r.id = g.idpolizarenovable "
						+ "and dc.grupo_negocio = g.grupo_negocio "
						+ "and p.idestado = 14 "
						+ "and l.codplan = 2023 and p.idpoliza < 60538427 ";
			
			logger.debug(">>> sql:"+sql);
			
			List<?> res = session.createSQLQuery(sql).list();
			logger.debug(">>> Se han obtenido "+res.size()+" obtener polzias sin pagos");
			
			return res;
		} catch (Exception e) {
			logger.error(">>> Error obtener polzias renovables no emitidas", e);
			return null;
		}
	}
	
	public static void updateComisionPolRenov(final BigDecimal idPoliza, final String grupoNeg,
			final BigDecimal renvPrimaComercialNeta, final BigDecimal impComMed, final BigDecimal impComES,
			final Session session) {
		try {
			logger.debug(">>> Actualizamos las comisiones renovables: idPoliza=" + idPoliza + " grupoNeg=" + grupoNeg
					+ " renvPrimaComercialNeta=" + renvPrimaComercialNeta + " impComMed=" + impComMed + " impComES="
					+ impComES);
			Query query = null;
			StringBuilder sbUpdate = new StringBuilder();
			sbUpdate.append("update o02agpe0.tb_distribucion_costes_2015 set primacomercialneta = ")
					.append(renvPrimaComercialNeta).append(", imp_cmsn_entidad = ").append(impComMed)
					.append(", imp_cmsn_esmed = ").append(impComES).append(" where idpoliza = ").append(idPoliza)
					.append(" and grupo_negocio = ").append(grupoNeg);

			logger.debug(">>> sbUpdate:" + sbUpdate.toString());
			query = session.createSQLQuery(sbUpdate.toString());
			query.executeUpdate();
		} catch (Exception e) {
			logger.error(">>> ERROR en el updateComisionPolRenov con idPoliza:" + idPoliza, e);
		}
	}
}
