package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.rsi.agp.batch.bbdd.Conexion;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;

public final class BBDDImportesUtil {

	private static final Logger logger= Logger.getLogger(BBDDImportesUtil.class);
	
	private BBDDImportesUtil() {
	}

	// Metodo que transforma el pago de la situacion actual
	// en el formato esperado por el modelo de datos de Agroplus y puebla el
	// objeto Hibernate encargado de la importacion
	protected static void populatePagos(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pago,
			final String cccSiniestros, final Session session) throws Exception {

		logger.debug ("** Dentro de populatePagos");
		Set<PagoPoliza> pagoPolizas;
		PagoPoliza pagoHbm;
		String cccbanco = "";
		Date fecha;
		Character formapago = 'C';
		BigDecimal importe;
		
		pagoPolizas = new HashSet<PagoPoliza>(1);
		pagoHbm = new PagoPoliza();
		
		if (pago.getCuenta() != null && !StringUtils.isNullOrEmpty(pago.getCuenta().getIban())) {
			cccbanco = pago.getCuenta().getIban();
		}
			
		fecha = pago.getFecha() == null ? null : pago.getFecha().getTime();
		if(!StringUtils.isNullOrEmpty(pago.getForma()))
			formapago = pago.getForma().toString().charAt(0);			
		importe = pago.getImporte();
		
		pagoHbm.setCccbanco(StringUtils.isNullOrEmpty(cccbanco) ? "" : cccbanco.substring(4));
		logger.debug("Valorde banco:"+pagoHbm.getCccbanco());
		pagoHbm.setCccbanco2(StringUtils.isNullOrEmpty(cccSiniestros) ? "" : cccSiniestros.substring(4));
		pagoHbm.setFecha(fecha);
		pagoHbm.setFormapago(formapago);
		pagoHbm.setImporte(importe);
		pagoHbm.setTipoPago(new BigDecimal(0));
		
		
		if (!StringUtils.isNullOrEmpty(cccbanco)) {			
			pagoHbm.setIban(cccbanco.substring(0, 4));
			logger.debug("Valor de iban:"+pagoHbm.getIban());
		}else {
			pagoHbm.setIban("ES");
		}
		
		if (!StringUtils.isNullOrEmpty(cccSiniestros)) {			
			pagoHbm.setIban2(cccSiniestros.substring(0, 4));
		}		

		pagoHbm.setPoliza(polizaHbm);
		session.saveOrUpdate(pagoHbm);

		pagoPolizas.add(pagoHbm);
		polizaHbm.setPagoPolizas(pagoPolizas);
	}

		
	// ***************DISTRIBUCION DE COSTES **********************************
	protected static void populateCostes(final Poliza polizaHbm,
			final es.agroseguro.contratacion.costePoliza.CostePoliza costePoliza, final Session session)
			throws Exception {

		Set<com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015> distribucionCostes2015 = new HashSet<com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015>(1);
		com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015 dcHbm= new com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015();
		
		if(polizaHbm.getModuloPolizas() != null && !polizaHbm.getModuloPolizas().isEmpty()) {
			Iterator<ModuloPoliza> polizaHbmModuloPolizaIterator = polizaHbm.getModuloPolizas().iterator();
			if (polizaHbmModuloPolizaIterator.hasNext()) {
				ModuloPoliza modPoliza = polizaHbmModuloPolizaIterator.next();
				if(modPoliza.getId().getNumComparativa() != null){
					dcHbm.setIdcomparativa(new BigDecimal(modPoliza.getId().getNumComparativa()));
				}
			}
		}
		
		es.agroseguro.contratacion.costePoliza.Financiacion financiacion = null;
		if (costePoliza != null) {
			CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
			if (costeGrupoNegocioArray != null) {
				financiacion = costePoliza.getFinanciacion();
				for (CosteGrupoNegocio costeGrupoNeg : costeGrupoNegocioArray) {
					if ( costeGrupoNeg != null) {
						dcHbm.setCodmodulo(polizaHbm.getCodmodulo());
						dcHbm.setCostetomador(costeGrupoNeg.getCosteTomador());
						dcHbm.setPoliza(polizaHbm);
						dcHbm.setPrimacomercial(costeGrupoNeg.getPrimaComercial());
						dcHbm.setPrimacomercialneta(costeGrupoNeg.getPrimaComercialNeta());						
						dcHbm.setRecargoconsorcio(costeGrupoNeg.getRecargoConsorcio());					
						dcHbm.setReciboprima(costeGrupoNeg.getReciboPrima());
						dcHbm.setTotalcostetomador(costePoliza.getTotalCosteTomador());
						polizaHbm.setImporte(costePoliza.getTotalCosteTomador());
						dcHbm.setGrupoNegocio(costeGrupoNeg.getGrupoNegocio().charAt(0));
						if (financiacion != null && financiacion.getRecargoAval()!= null) {
							dcHbm.setRecargoaval(financiacion.getRecargoAval());
						}
						if (financiacion != null && financiacion.getRecargoFraccionamiento()!= null) {
							dcHbm.setRecargofraccionamiento(financiacion.getRecargoFraccionamiento());
						}
						// Bonificaciones y recargos
						dcHbm = cargaBonifRecargUnificado(dcHbm, costeGrupoNeg);
						// Carga las subvenciones de CCAA y ENESA en el objeto de la distribucion de costes
						dcHbm = cargarSubvencionesDC(dcHbm, costeGrupoNeg);
						break;
					}
				}
			}
		}		
		distribucionCostes2015.add(dcHbm);
		polizaHbm.setDistribucionCoste2015s(distribucionCostes2015);
		session.saveOrUpdate(dcHbm);
	}
	
	private static DistribucionCoste2015 cargaBonifRecargUnificado(DistribucionCoste2015 dc,CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo = costeGrupoNegocio.getBonificacionRecargoArray();
		if (boniRecargo != null) {
			// Bucle para a�adir las distribuciones de coste de las
			// Bonificaciones Recargo
			for (int i = 0; i < boniRecargo.length; i++) {
				BonificacionRecargo2015 bon = new BonificacionRecargo2015();
				bon.setDistribucionCoste2015(dc);
				bon.setCodigo(new BigDecimal(boniRecargo[i].getCodigo()));
				bon.setImporte(boniRecargo[i].getImporte());
				dc.getBonificacionRecargo2015s().add(bon);
			}
		}
		return dc;
	}
	
	private static DistribucionCoste2015 cargarSubvencionesDC(final DistribucionCoste2015 dc,final CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA = costeGrupoNegocio.getSubvencionCCAAArray();
		es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa = costeGrupoNegocio.getSubvencionEnesaArray();
		// Subvenciones CCAA
		if (subCCAA != null) {
			for (int i = 0; i < subCCAA.length; i++) {
				// Bucle para a�adir las distribuciones de coste de las
				// subvenciones CCAA
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo(subCCAA[i].getCodigoOrganismo().charAt(0));
				subv.setImportesubv(subCCAA[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		// Subvenciones ENESA
		if (subEnesa != null) {
			
			for (int i = 0; i < subEnesa.length; i++) {
				DistCosteSubvencion2015 subv = new DistCosteSubvencion2015();
				subv.setDistribucionCoste2015(dc);
				subv.setCodorganismo('0');
				subv.setCodtiposubv(new BigDecimal(subEnesa[i].getTipo()));
				subv.setImportesubv(subEnesa[i].getImporte());
				dc.getDistCosteSubvencion2015s().add(subv);
			}
		}
		return dc;
	}	
	
// ***************FIN DISTRIBUCION DE COSTES **********************************

// ***************FRACCIONAMIENTO DE PAGO **********************************
	protected static void populateFraccionamiento(final Poliza polizaHbm,
			es.agroseguro.contratacion.Pago pago, final Session session) {
		if(null!= pago && null!=pago.getFraccionamiento()) {
			if(null!=pago.getFraccionamiento().getAval()) {
				com.rsi.agp.dao.tables.poliza.DatosAval datosAval= new com.rsi.agp.dao.tables.poliza.DatosAval();
				//datosAval.setIdpoliza(polizaHbm.getIdpoliza());
				datosAval.setImporteAval(pago.getFraccionamiento().getAval().getImporte());
				datosAval.setNumeroAval(new BigDecimal(pago.getFraccionamiento().getAval().getNumero()));
				polizaHbm.setDatosAval(datosAval);
				datosAval.setPoliza(polizaHbm);
				session.saveOrUpdate(datosAval);
			}			
			com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015 dc= (DistribucionCoste2015) polizaHbm.getDistribucionCoste2015s().iterator().next();
			dc.setImportePagoFracc(pago.getImporte());
			dc.setPeriodoFracc(pago.getFraccionamiento().getPeriodo());
			dc.setPoliza(polizaHbm);
			session.saveOrUpdate(dc);
			polizaHbm.getDistribucionCoste2015s().clear();
			polizaHbm.getDistribucionCoste2015s().add(dc);
		
		}
	} 
// ***************FIN FRACCIONAMIENTO DE PAGO **********************************	
	
// ***************COMISIONES ***************************************************

	public static PolizaPctComisiones populateComisiones(final Poliza polizaHbm,
			int plan, int linea, Calendar fechaEfecto, BigDecimal gastosAdmin, 
			BigDecimal gastosAdq, BigDecimal gastosComisionMed, 
			SubentidadMediadora sm, final Session session, Character grupoNegocio) throws Exception{ 
		
		// Si no se han obtenido los datos de la ES Mediadora no se pueden buscar las comisiones
		if (sm == null) {
			throw new Exception("No se encuentran las comisiones ya que no se ha obtenido la ES Mediadora de la poliza");
		}
		
		CultivosSubentidades coms=getComisionesSubentidades(plan, linea, fechaEfecto, sm.getId().getCodentidad(), sm.getId().getCodsubentidad());
		
		PolizaPctComisiones res=null;
		if(null!=coms){
			// BORRAR LA EXCEPCTION Y DESCOMENTAR LO SIGUIENTE
			/*
			if (polizaHbm.getReferencia().equals("122065W")){
				throw new Exception(
					"No se encuentran las comisiones del plan " + plan + ", linea " + linea + ", fecha de efecto " + fechaEfecto.toString()
					+ ", entMed " + sm.getId().getCodentidad() + ", subEntMed " + sm.getId().getCodsubentidad());
			}else {
			*/
			res=new PolizaPctComisiones();
			//res.setIdpoliza(polizaHbm.getIdpoliza());
			res.setPoliza(polizaHbm);
			//OJO llenar
			res.setPctadministracion(gastosAdmin);
			res.setPctadquisicion(gastosAdq);
			res.setPctcommax(gastosComisionMed);
			res.setPctentidad(coms.getPctentidad());
			res.setPctesmediadora(coms.getPctmediador());
			res.setGrupoNegocio(grupoNegocio);			
			session.saveOrUpdate(res);
			polizaHbm.setPolizaPctComisiones(res);
			
			//}
		}else{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String fecEfecto= df.format(fechaEfecto == null ? new Date() : fechaEfecto.getTime());
			
			throw new Exception(
					polizaHbm.getReferencia() +": No se encuentran las comisiones del plan " + plan + ", linea " + linea + ", fecha de efecto " + fecEfecto
					+ ", entMed " + sm.getId().getCodentidad() + ", subEntMed " + sm.getId().getCodsubentidad());
		}
		
		return res;
	} 
	
	private static CultivosSubentidades getComisionesSubentidades( int plan, int linea, Calendar fechaEfecto, BigDecimal entMed, BigDecimal subEntMed) throws Exception{
		logger.info("Seleccionamos las comisiones en la tabla de comisiones de subentidades por fecha de efecto. ");		
		CultivosSubentidades res=null;
		Conexion c = new Conexion();		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String fecEfecto= df.format(fechaEfecto == null ? new Date() : fechaEfecto.getTime());
		try {
			String sql ="select coms.PCTENTIDAD, coms.PCTMEDIADOR " +     
					"from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms " +
					"inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT " +
					"inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + 
					"where l.CODPLAN=" + plan + " and l.CODLINEA=" + linea + 
					" and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD=" + subEntMed +
					" and TO_DATE (sh.FEC_EFECTO,'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto + "','DD/MM/YYYY') "+ 
					"and ((coms.fec_baja is null or to_date(current_date,'dd/mm/yyy') < to_date(coms.fec_baja,'dd/mm/yyy')) and to_date(current_date,'dd/mm/yyy') >= trunc(sh.fec_efecto)) " +
					"order by sh.fec_efecto desc ,sh.fechamodificacion desc";
						
			logger.debug ("plan: " + plan + " - linea: " + linea + " - fecEfecto: " + fecEfecto + " - entMed: " + entMed + " - subEntMed: " + subEntMed);
			logger.debug (sql);
			
			List<Object> resultado = c.ejecutaQuery(sql, 2);
			if(resultado!=null && resultado.size()>0){
				BigDecimal pctent = (BigDecimal) ((Object[])resultado.get(0))[0];
				BigDecimal pctmed = (BigDecimal) ((Object[])resultado.get(0))[1] ;
				
				res = new CultivosSubentidades();
				res.setPctentidad(pctent);
				res.setPctmediador(pctmed);
			}else{
				String sql2 ="select coms.PCTENTIDAD, coms.PCTMEDIADOR " +     
						"from o02agpe0.TB_COMS_CULTIVOS_SUBENTIDADES coms " +
						"inner join O02AGPE0.TB_COMS_CULTIVOS_SUBS_HIST sh ON coms.id=sh.IDCOMISIONESSUBENT " +
						"inner join O02AGPE0.TB_LINEAS l on coms.LINEASEGUROID=l.LINEASEGUROID " + 
						"where l.CODPLAN=" + plan + " and l.CODLINEA=999" + 
						" and coms.CODENTIDAD=" + entMed + " and coms.CODSUBENTIDAD=" + subEntMed +
						" and TO_DATE (sh.FEC_EFECTO,'DD/MM/YYYY') <= TO_DATE ('" + fecEfecto + "','DD/MM/YYYY') "+ 
						"and ((coms.fec_baja is null or to_date(current_date,'dd/mm/yyy') < to_date(coms.fec_baja,'dd/mm/yyy')) and to_date(current_date,'dd/mm/yyy') >= trunc(sh.fec_efecto)) " +
						"order by sh.fec_efecto desc ,sh.fechamodificacion desc";
				logger.debug ("plan: " + plan + " - linea: 999 - fecEfecto: " + fecEfecto + " - entMed: " + entMed + " - subEntMed: " + subEntMed);
				logger.debug ("busqueda por linea generica. - " + sql2);
				List<Object> resultado2 = c.ejecutaQuery(sql2, 2);
				if(resultado2!=null && resultado2.size()>0){
					BigDecimal pctent = (BigDecimal) ((Object[])resultado2.get(0))[0];
					BigDecimal pctmed = (BigDecimal) ((Object[])resultado2.get(0))[1] ;
					
					res = new CultivosSubentidades();
					res.setPctentidad(pctent);
					res.setPctmediador(pctmed);
				}
			}
			
				
		} catch (Exception e) { 
			logger.error("Error seleccionando las comisiones de subentidades por fecha de efecto." ,e);
			throw(e);
		}
		return res;
	}

	
	
// ***************FIN DE COMISIONES ********************************************	
	
}
