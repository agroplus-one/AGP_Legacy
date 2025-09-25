package com.rsi.agp.core.jmesa.service.impl.utilidades;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rsi.agp.core.jmesa.dao.impl.IImportacionPolRenovableDao;
import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.core.jmesa.service.utilidades.IAltaPolizaRenovableService;
import com.rsi.agp.dao.models.comisiones.IPolizasPctComisionesDao;
import com.rsi.agp.dao.tables.renovables.BonificacionRecargoRen;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.CosteGrupoNegocioRen;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.FraccionamientoRen;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.GastosRenovacionAplicados;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableEstadoEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableHistoricoEstados;

import es.agroseguro.estadoRenovacion.GrupoNegocioPrimas;
import es.agroseguro.estadoRenovacion.Renovacion;


public class AltaPolizaRenovableService implements IAltaPolizaRenovableService {

	private IImportacionPolRenovableDao importacionPolRenovableDao;
	private static final Log logger = LogFactory.getLog(AltaPolizaRenovableService.class);

	/***** ---------- INICIO TATIANA (23.04.2021) --------- *****/
	@SuppressWarnings("unused")
	public ColectivosRenovacion ValidatePolizaRenColectivo(final Renovacion polizaRen, final boolean batch, final Session session)
			throws Exception {
		
		boolean isGrabado = false;
		
		String refColectivo = polizaRen.getColectivo();
		BigDecimal codPlan = new BigDecimal(polizaRen.getPlan());
		BigDecimal codLinea = new BigDecimal(polizaRen.getLinea());
		String refPoliza = polizaRen.getReferencia();
		
		ColectivosRenovacion colHbm = new ColectivosRenovacion();
		
		if (batch){
			
			logger.info("Entramos por batch");
			colHbm = importacionPolRenovableDao.obtenerColectivoRen(refColectivo);
			
			if (colHbm == null) {
				logger.info("colectivo nulo");
				
				ColectivosRenovacion colNew = new ColectivosRenovacion();
				colNew.setReferencia(polizaRen.getColectivo());
				colNew.setCodentidad(new Long(9999));
				colNew.setCodentidadmed(new Long(9999));
				colNew.setCodsubentmed(new Long(0));
				colNew.setDc(new Character('1'));
				colNew.setCodlinea(Long.parseLong(Integer.toString(polizaRen.getLinea())));
				
				// RQ.09
				ColectivosRenovacion colAnt = importacionPolRenovableDao.obtenerColectivoPlanAnt(refColectivo, codPlan, codLinea);
				if (colAnt != null) {
					colNew.setCodentidad(colAnt.getCodentidad());
					colNew.setCodentidadmed(colAnt.getCodentidadmed());
					colNew.setCodsubentmed(colAnt.getCodsubentmed());
				}
				
				Transaction transCol = session.beginTransaction();
				transCol = session.beginTransaction();
				session.saveOrUpdate(colNew);
				transCol.commit();
					
				return colNew;
				
			} 
		}else {
			
			logger.info("Entramos por online");
			/* Cambio de Alcance Nº2 ** P0063482 ** MODIF TAM (08.06.2021) ** Inicio */
			if (refColectivo == null) {
				logger.info("Entramos por Referencia del colectivo nulo, valor de referencia Poliza:"+refPoliza);
				
				/*1º Buscamos la referencia del colectivo de las polizas Renovables de planes Anteriores*/
				ColectivosRenovacion colHbm1 = importacionPolRenovableDao.obtenerColectivoRenovPlanAnt(refPoliza, codPlan, codLinea);
				
				if (colHbm1 != null) {
					String refColectivoRen1 = colHbm1.getReferencia();
					colHbm = importacionPolRenovableDao.obtenerColectivoRen(refColectivoRen1);
					return colHbm;
				}else {
					/*2º Si no se ha encontrado para los planes anteriores, buscamos la referencia del colectivo de la póliza Principal */
					ColectivosRenovacion colHbm2 = importacionPolRenovableDao.obtenerColectivoPolPpal(refPoliza, codPlan, codLinea);
					
					/* Si se ha encontrado el colectivo de la principal*/
					if (colHbm2 != null) {
						/* Buscamos el colectivo devuelto en la tabla de colectivos renovables:*/
						String refColectivoRen = colHbm2.getReferencia();
						
						ColectivosRenovacion colHbm3 = importacionPolRenovableDao.obtenerColectivoRen(refColectivoRen);

						if (colHbm3 != null) {
							return colHbm3;
						}else {
							/* Guardamos el colectivo del plan anterior */
							isGrabado = importacionPolRenovableDao.guardarColectivoRen(colHbm2);	
							return colHbm;
						}
					}else {
						/* Si no se encuentran colectivos de planes anteriores ni de la póliza principal, se retorna nulo y se cancela el alta avisando del error al usuario*/
						return null;	
					}
				}
				
			}else {
				/* Cambio de Alcance Nº2 ** P0063482 ** MODIF TAM (08.06.2021) ** Fin */	
				logger.info("Entramos por Referencia del colectivo no nulo");
				logger.info("AltaPolizaRenovableService (ValidatePolizaRenColectivo), Valor de Colectivo(): " + polizaRen.getColectivo());
		
				colHbm = importacionPolRenovableDao.obtenerColectivoRen(refColectivo);
				
				if (colHbm == null) {
					logger.info("colectivo nulo");
					
					/* Obtenemos el colectivo de planes anteriores en caso de no existir */
					colHbm = importacionPolRenovableDao.obtenerColectivoRenovPlanAnt(refPoliza, codPlan, codLinea);
					
					if (colHbm != null) {
						String refColectivoRen1 = colHbm.getReferencia();
						colHbm = importacionPolRenovableDao.obtenerColectivoRen(refColectivoRen1);
						return colHbm;
					}else {
						/* Si no se encuentran colectivos de planes anteriores, se retorna nulo y se cancela el alta avisando del error al usuario*/
						return null;
					}
				
				}
			}
		}
	
		return colHbm;

	}
	
	@Override
	public boolean populateAndValidatePolizaRen(List<PolizaRenBean> lstRes, PolizaRenovable polizaHbm,
			Renovacion polizaRen, Session session, StringBuilder polizasOK,
			IPolizasPctComisionesDao polizasPctComisionesDao,  boolean batch, String codUsuario)
			throws Exception {

		boolean polizaOK = true;
		
		logger.info("AltaPolizaRenovableService(populateAndValidatePolizaRen) ");
		logger.info("- Referencia:"+ polizaRen.getReferencia() + "de la línea: "+polizaRen.getLinea() +" del plan: "+polizaRen.getPlan());

		Long codlinea = Long.parseLong(Integer.toString(polizaRen.getLinea()));
		Long plan = Long.parseLong(Integer.toString(polizaRen.getPlan()));

		BigDecimal linea = new BigDecimal(codlinea);
		
		Long entMed = polizaHbm.getColectivoRenovacion().getCodentidadmed();
		Long subentMed = polizaHbm.getColectivoRenovacion().getCodsubentmed();
		Date fechaRenovacion = polizaRen.getFechaRenovacion().getTime();
		logger.info("BBDDCargaPolRenUtil-populateAndValidatePolizaRen, Valor de fechaRenovacion:" + fechaRenovacion);

		// Obtenemos los parametros de comisiones específicas o generales, para la línea
		// de la póliza Renovable que estamos tratando
		// para luego enviarlos a la asignación de Gastos.

		Map<String, BigDecimal[]> mapLineaGrPctsFinal = importacionPolRenovableDao.getParamsComis(session, linea, plan,
				entMed, subentMed, fechaRenovacion, batch);
		/*
		 * ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S
		 * Mediadora * Fin
		 */

		// Linea
		polizaHbm.setLinea(Long.parseLong(Integer.toString(polizaRen.getLinea())));

		// Plan
		polizaHbm.setPlan(Long.parseLong(Integer.toString(polizaRen.getPlan())));

		// Referencia
		polizaHbm.setReferencia(polizaRen.getReferencia());
		char dc = Integer.toString(polizaRen.getDigitoControl()).charAt(0);
		polizaHbm.setDc(dc);

		// Asegurado
		polizaHbm.setNifAsegurado(polizaRen.getNifAsegurado());

		// Estados Poliza
		EstadoRenovacionAgroplus estadoAgroplus = importacionPolRenovableDao.getEstadoPolRenAgroplus(session, batch);

		EstadoRenovacionAgroseguro estadoAgroSeguro = importacionPolRenovableDao
				.getEstadoPolRenAgroseguro(Long.parseLong(Integer.toString(polizaRen.getEstado())), session, batch);

		polizaHbm.setEstadoRenovacionAgroseguro(estadoAgroSeguro);

		// Fecha Carga
		Date dat = new Date();
		polizaHbm.setFechaCarga(dat);

		// Estado de envio de IBAN a Agroseguro - Por defecto a '1 - No'
		PolizaRenovableEstadoEnvioIBAN estadoEnvioIBAN = new PolizaRenovableEstadoEnvioIBAN(1L, "No");
		// PolizaRenovableEstadoEnvioIBAN estadoEnvioIBAN =
		// BBDDEstadosRenUtil.getEstadoEnvioIBAN(1L,session); // new
		// PolizaRenovableEstadoEnvioIBAN(1L, "No");
		polizaHbm.setPolizaRenovableEstadoEnvioIBAN(estadoEnvioIBAN);

		// OPCIONALES

		// Tomador
		if (polizaRen.getNifTomador() != null)
			polizaHbm.setNifTomador(polizaRen.getNifTomador());

		// Medidador
		if (polizaRen.getMediador() != null)
			polizaHbm.setMediador(polizaRen.getMediador());

		// Fecha renovacion
		if (polizaRen.getFechaRenovacion() != null)
			polizaHbm.setFechaRenovacion(polizaRen.getFechaRenovacion().getTime());

		// Fecha comunicacion
		if (polizaRen.getFechaComunicacion() != null)
			polizaHbm.setFechaComunicacion(polizaRen.getFechaComunicacion().getTime());

		// Coste Total Tomador
		if (polizaRen.getCosteTotalTomador() != null)
			polizaHbm.setCosteTotalTomador(polizaRen.getCosteTotalTomador());

		// Coste Total Tomador Anterior
		if (polizaRen.getCosteTotalTomadorAnterior() != null) {
			logger.info("CosteTotalTomadorAnterior: " + polizaRen.getCosteTotalTomadorAnterior());
			polizaHbm.setCosteTotalTomadorAnterior(polizaRen.getCosteTotalTomadorAnterior());
		}

		// numeroRecibo
		if (polizaRen.getNumeroRecibo() != 0) {
			polizaHbm.setNumRecibo(new BigDecimal(polizaRen.getNumeroRecibo()));
		}

		// Fecha domiciliacion
		if (polizaRen.getFechaDomiciliacion() != null)
			polizaHbm.setFechaDomiciliacion(polizaRen.getFechaDomiciliacion().getTime());

		// importeDomiciliar
		if (polizaRen.getImporteDomiciliar() != null)
			polizaHbm.setImporteDomiciliar(polizaRen.getImporteDomiciliar());

		// IBAN
		if (polizaRen.getIBAN() != null)
			polizaHbm.setIban(polizaRen.getIBAN());

		// destinatarioDomiciliacion
		if (polizaRen.getDestinatarioDomiciliacion() != null)
			polizaHbm.setDestinoDomiciliacion(polizaRen.getDestinatarioDomiciliacion().charAt(0));

		// Forma de pago
		if (polizaRen.getForma() != null) {
			polizaHbm.setForma(polizaRen.getForma().charAt(0));
		}

		if (polizaRen.getDomiciliado() != null) {
			polizaHbm.setDomiciliado(polizaRen.getDomiciliado().charAt(0));
		}

		try {

			// Gastos
			List<Character> listGN = populateGastos(polizaHbm, mapLineaGrPctsFinal, polizaRen, estadoAgroplus, batch);
			
			if (listGN == null && !batch) {
				return false;
			}

			// Gastos Aplicados
			Long linId = importacionPolRenovableDao.getLineaseguroIdfromPlanLinea(session, polizaHbm.getPlan(),
					polizaHbm.getLinea(), batch);

			logger.info("Antes de populateGastosAplicados, Valor de entMed:"
					+ polizaHbm.getColectivoRenovacion().getCodentidadmed());
			logger.info("Antes de populateGastosAplicados, Valor de SubentMed:"
					+ polizaHbm.getColectivoRenovacion().getCodsubentmed());
			populateGastosAplicados(polizaHbm, polizaRen, polizasPctComisionesDao, linId);

			// CosteGrupoNegocio
			populateCosteGrupoNegocio(polizaHbm, polizaRen);

			// Guardamos el XML de la Poliza Renovable
			importacionPolRenovableDao.guardaXml(polizaHbm, polizaRen.xmlText(), session, batch);

			// Insertamos en el historico de estados para cada poliza Ren
			importacionPolRenovableDao.actualizarHistorico(polizaHbm, estadoAgroSeguro, estadoAgroplus, listGN, session,
					batch, codUsuario);
			
			// POLIZA INSERTADA OK
			if (batch) {
				
				Transaction trans = session.beginTransaction();
				
				PolizaRenBean polOK = new PolizaRenBean();
				polOK.setLinea(polizaHbm.getLinea().toString());
				polOK.setPlan(polizaHbm.getPlan().toString());
				polOK.setReferencia(polizaHbm.getReferencia());
				polOK.setDescripcion("OK");
				polizasOK.append(polizaHbm.getReferencia() + ",");
				
				session.saveOrUpdate(polizaHbm);

				trans.commit();
				lstRes.add(polOK);

			}else {
				importacionPolRenovableDao.guardarPolizaRen(polizaHbm, session, batch);
			}	
			

			if (polizaRen.getFraccionamiento() != null) {
				populateFraccionamiento(polizaHbm, polizaRen);
				
				if (batch) {
					logger.info("Guardamos póliza batch");
					Transaction trans2 = session.beginTransaction();
					
					session.saveOrUpdate(polizaHbm);
					trans2.commit();
				}else {	
					importacionPolRenovableDao.guardarPolizaRen(polizaHbm, session, batch);
				}
					
					
			}
		} catch (Exception ex) {
			polizaOK = false;
			logger.error("# Se ha producido un error al guardar los dtos de la poliza renovable", ex);
		}

		return polizaOK;
	}

	public void guardaXml(final PolizaRenovable polizaHbm, final String xmlText, final Session session) {

		Clob clob;
		Reader reader = null;

		try {

			clob = Hibernate.createClob(xmlText);
			reader = clob.getCharacterStream();
			polizaHbm.setXml(Hibernate.createClob(reader, (int) clob.length()));

		} catch (SQLException e) {

			logger.error("# Error al guardar el XML de la Poliza Renovable.", e);

		} finally {
			try {
				// if (reader != null)
				// reader.close();
			} catch (Exception ex) {
				// Exception free code
			}
		}
	}

	protected static List<Character> populateGastos(final PolizaRenovable polizaHbm,
			final Map<String, BigDecimal[]> mapLineaGrPctsFinal, final Renovacion pRen,
			EstadoRenovacionAgroplus estadoAgroplus, boolean batch) throws Exception {
		logger.info(" # Insertamos gastos en la poliza: " + pRen.getReferencia() + ".. #");
		List<Character> lstGruposN = new ArrayList<Character>();
		boolean revisarGrN = false;
		// compruebo los grupos de negocio del objeto renovacion en los arrays de
		// Gastos, Gastos Aplicados y CostesGrupoNegocio

		// compruebo GruposN de gastos en la poliza WS
		for (int i = 0; i < pRen.getGastosArray().length; i++) {
			if (pRen.getGastosArray(i).getGrupoNegocio() != null
					&& !pRen.getGastosArray(i).getGrupoNegocio().toString().equals("")) {
				lstGruposN.add(pRen.getGastosArray(i).getGrupoNegocio().charAt(0));
			}
		}
		if (lstGruposN.isEmpty()) {
			// compruebo GruposN de gastoAplicados en la poliza WS
			for (int i = 0; i < pRen.getGastosAplicadosArray().length; i++) {
				if (pRen.getGastosAplicadosArray(i).getGrupoNegocio() != null
						&& !pRen.getGastosAplicadosArray(i).getGrupoNegocio().toString().equals("")) {
					lstGruposN.add(pRen.getGastosAplicadosArray(i).getGrupoNegocio().charAt(0));
				}
			}
		}
		if (lstGruposN.isEmpty()) {
			// compruebo GruposN de CostesGrupoNegocio en la poliza WS
			for (int i = 0; i < pRen.getCosteGrupoNegocioArray().length; i++) {
				if (pRen.getCosteGrupoNegocioArray(i).getGrupoNegocio() != null
						&& !pRen.getCosteGrupoNegocioArray(i).getGrupoNegocio().toString().equals("")) {
					lstGruposN.add(pRen.getCosteGrupoNegocioArray(i).getGrupoNegocio().charAt(0));
				}
			}
		}
		if (lstGruposN.size() > 0) { // tenemos gruposN en la poliza WS
			Collections.sort(lstGruposN);
			revisarGrN = true;
			logger.info(" La poliza WS tiene los siguientes grupos: " + lstGruposN.toString()
					+ " en gastos, gastos aplicados o costesGrupoNegocio");
		} else { // sin grupos en la poliza WS, inserto todos los del mapa
			revisarGrN = false;
		}
		if (revisarGrN)
			logger.info(" Se insertan los gastos de los grupos: " + lstGruposN.toString()
					+ " con las comisiones del mapa..");
		else
			logger.info(" Se insertan los gastos con todos los grupos de negocio del mapa..");

		Set<GastosRenovacion> setGasRen = new HashSet<GastosRenovacion>();
		List<String> lstKeys = new ArrayList<String>();
		logger.info("**@@** Antes de asignar los gastos(for)");
		logger.info("**@@** Valor de  mapLineaGrPctsFinal.size" + mapLineaGrPctsFinal.size());
		logger.info("**@@** Valor de  mapLineaGrPctsFinal.entrySet()" + mapLineaGrPctsFinal.entrySet());
		for (Map.Entry<String, BigDecimal[]> entry : mapLineaGrPctsFinal.entrySet()) {
			logger.info("**@@** clave = " + entry.getKey());
			String[] arrKey = entry.getKey().split("_");
			if (arrKey[0].equals(polizaHbm.getLinea().toString())) {
				lstKeys.add(entry.getKey());
			}
		}

		for (String key : lstKeys) {
			String[] arrKey = key.split("_");
			BigDecimal[] pctComs = (BigDecimal[]) mapLineaGrPctsFinal
					.get(polizaHbm.getLinea().toString() + "_" + arrKey[1].charAt(0));
			logger.info("**@@** Valor de pctComs: " + pctComs);
			logger.info("**@@** Valor de revisarGrN: " + revisarGrN);
			if (pctComs != null && pctComs.length > 0) {
				// si revisarGrN es false se insertan todos los grupos del mapa, si no solo se
				// insertan los que hayan sido informados en la poliza Ws(de gastos Aplicados o
				// CostesGrupoNegocio
				if (!revisarGrN || lstGruposN.contains(arrKey[1].charAt(0))) {
					GastosRenovacion gs = new GastosRenovacion();
					// se insertan los datos de comisiones
					gs.setAdministracion((null != pctComs[0]) ? pctComs[0] : null);
					gs.setAdquisicion((null != pctComs[1]) ? pctComs[1] : null);
					// se inserta el grupo correspondiente
					gs.setGrupoNegocio(arrKey[1].charAt(0));
					if (!lstGruposN.contains(arrKey[1].charAt(0)))
						lstGruposN.add(arrKey[1].charAt(0));
					// se inserta el estado pendiente asignar gastos..
					gs.setEstadoRenovacionAgroplus(estadoAgroplus);
					gs.setPolizaRenovable(polizaHbm);
					setGasRen.add(gs);
					logger.info("gr " + gs.getGrupoNegocio() + " adm: " + gs.getAdministracion() + " adq: "
							+ gs.getAdquisicion() + " insertado.");
				} else {
					logger.info("gr " + arrKey[1].charAt(0) + " del mapa descartado.");
				}
			} else {
				logger.info("el mapa no tiene comisiones para el gr " + arrKey[1].charAt(0) + " ");
			}
		}
		if (setGasRen.size() > 0) {
			polizaHbm.setGastosRenovacions(setGasRen);
			Collections.sort(lstGruposN);
			if (revisarGrN)
				logger.info(
						" Gastos de los grupos " + lstGruposN.toString() + " insertados con las comisiones del mapa..");
			else
				logger.info(" Gastos de los grupos " + lstGruposN.toString()
						+ " insertados (todos los grupos de negocio del mapa)");
		} else {
			logger.info(" Set de gastosrenovacion vacio, NO se insertan gastos");
			/* Pet. 63482 */
			if (!batch) {
				return null;
			}
			
		}
		// }

		return lstGruposN;
	}

	// Metodo que actualiza el historico
	public static void actualizarHistorico(final PolizaRenovable polizaHbm, EstadoRenovacionAgroseguro estadoAgroSeguro,
			EstadoRenovacionAgroplus estadoAgroplus, List<Character> listGN, final Session session) {

		if (listGN == null || listGN.isEmpty()) {
			logger.info("--actualizarHistorico: La lista de GN es vacia");
			return;
		}

		// Insertamos registro en el historico de estados
		Set<PolizaRenovableHistoricoEstados> historico = new HashSet<PolizaRenovableHistoricoEstados>();

		for (Character gn : listGN) {
			PolizaRenovableHistoricoEstados hist = new PolizaRenovableHistoricoEstados();
			hist.setEstadoRenovacionAgroplus(estadoAgroplus);
			hist.setEstadoRenovacionAgroseguro(estadoAgroSeguro);
			hist.setFecha(new Date());
			hist.setUsuario("BATCH");
			hist.setPolizaRenovable(polizaHbm);
			hist.setGrupoNegocio(gn);
			historico.add(hist);

			logger.info("## Historico de la poliza " + polizaHbm.getReferencia() + " con GN: " + gn + " insertado ##");
		}

		polizaHbm.setPolizaRenovableHistoricoEstadoses(historico);
	}

	// Metodo que devuelve una lista de lineasGrupoNegocio

	/***** ---------- FIN TATIANA (23.04.2021) ---------- *****/

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
			logger.info(" Gastos insertados..");
		}

	}

	public static void populateGastosAplicados(final PolizaRenovable polizaHbm, final Renovacion polizaRen,
			final IPolizasPctComisionesDao polizasPctComisionesDao, final Long linId) throws Exception {
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
			logger.info(" Gastos aplicados insertados..");
		}

	}

	public static void populateCosteGrupoNegocio(final PolizaRenovable polizaHbm, final Renovacion polizaRen)
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
				logger.info(" PrimaComercialBaseNeta: "
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
			logger.info(" Coste Grupo Negocio insertado..");
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
			logger.info(" BonificacionRecargo insertada..");
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
			logger.info(" Fraccionamiento insertado..");
		}

	}

	public void setImportacionPolRenovableDao(IImportacionPolRenovableDao importacionPolRenovableDao) {
		this.importacionPolRenovableDao = importacionPolRenovableDao;
	}

}
