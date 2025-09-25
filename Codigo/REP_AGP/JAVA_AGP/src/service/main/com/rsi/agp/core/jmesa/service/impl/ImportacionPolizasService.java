package com.rsi.agp.core.jmesa.service.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IImportacionPolizasService;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.poliza.IDistribucionCosteDAO;
import com.rsi.agp.dao.models.poliza.IImportacionPolizasExtDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

import es.agroseguro.contratacion.ObjetosAsegurados;
import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;
import es.agroseguro.contratacion.costePoliza.CostePoliza;
import es.agroseguro.contratacion.costePoliza.Financiacion;
import es.agroseguro.contratacion.datosVariables.DatosVariables;
import es.agroseguro.contratacion.explotacion.Coordenadas;
import es.agroseguro.contratacion.explotacion.ExplotacionDocument;

public class ImportacionPolizasService implements IImportacionPolizasService {

	private IImportacionPolizasExtDao importacionPolizasExtDao;
	private IDocumentacionGedManager documentacionGedManager;
	private IDistribucionCosteDAO distribucionCosteDAO;
	private IPolizasPctComisionesManager polizasPctComisionesManager;

	private static final Log LOGGER = LogFactory.getLog(ImportacionPolizasService.class);

	static Map<String, BigDecimal> colFilaModulo = new HashMap<String, BigDecimal>();
	static Map<String, String> colDescripciones = new HashMap<String, String>();

	/***
	 * Metodo para obtener la poliza principal agricola poblada con todos los datos
	 * 
	 * @param polizaHbm
	 * @param poliza
	 * @param idEnvio
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Boolean populateAndValidatePoliza(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session, final Boolean isBatch, final String codUsuario) throws Exception {

		LOGGER.debug("ImportacionPolizasService - populateAndValidatePoliza - Principal Agricola [INIT]");
		LOGGER.debug("Entramos por batch:" + isBatch);

		Boolean resultadoImportacion = false;
		
		try {
			Long secuencia = importacionPolizasExtDao.getSecuenciaComparativa(session, isBatch);

			if (isBatch) {
				polizaHbm.setIdenvio(BigDecimal.valueOf(idEnvio.longValue()));
			}

			Date fechaFirma = poliza.getFechaFirmaSeguro().getTime();
			polizaHbm.setFechaenvio(fechaFirma);

			LOGGER.debug("populateAndValidatePoliza - fechaFirma: " + fechaFirma.toString());

			// LINEA
			polizaHbm.setLinea(importacionPolizasExtDao.getLineaSeguroBBDD(BigDecimal.valueOf(poliza.getLinea()),
					BigDecimal.valueOf(poliza.getPlan()), session, isBatch));
			LOGGER.debug("linea: " + polizaHbm.getLinea().getLineaseguroid());

			// COLECTIVO
			BigDecimal codPlan = new BigDecimal(poliza.getPlan());
			Colectivo colectivoHbm = importacionPolizasExtDao.getColectivoBBDDonline(poliza.getColectivo(), session,
					codPlan, isBatch);
			polizaHbm.setColectivo(colectivoHbm);
			LOGGER.debug("colectivo: " + colectivoHbm.getIdcolectivo());

			// E-S MEDIADORA - obtenida del campo 'codigoInterno de la situacion
			// actualizada'
			SubentidadMediadora sm = getESMediadora(poliza.getPlan(), colectivoHbm, session, isBatch);

			// ASEGURADO
			Asegurado aseguradoHbm = importacionPolizasExtDao.getAseguradoBBDD(poliza.getAsegurado(),
					colectivoHbm.getTomador().getEntidad().getCodentidad(), session, sm, isBatch);
			polizaHbm.setAsegurado(aseguradoHbm);
			polizaHbm.setDiscriminante(aseguradoHbm.getDiscriminante());
			LOGGER.debug("asegurado: " + polizaHbm.getAsegurado().getNifcif());

			// USUARIO
			Usuario usuario = importacionPolizasExtDao.getUsuarioPolizaBBDD(session, isBatch, codUsuario,
					sm.getId().getCodentidad(), sm.getId().getCodsubentidad());
			polizaHbm.setUsuario(usuario);
			LOGGER.debug("usuario: " + usuario.getCodusuario());

			// ESTADO POLIZA
			polizaHbm.setEstadoPoliza(importacionPolizasExtDao.getEstadoPolizaBBDD(session, isBatch));
			LOGGER.debug("...estado");

			// DATOS DE PAGO
			polizaHbm.setEstadoPagoAgp(importacionPolizasExtDao.getEstadoPagoBBDD(session, isBatch));
			LOGGER.debug("...estado de pago");

			// Datos fijos o de importacion directa desde la situacion actual
			polizaHbm.setExterna(1);
			polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
			polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
			polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
			polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
			polizaHbm.setPacCargada(Constants.CHARACTER_N);
			polizaHbm.setReferencia(poliza.getReferencia());
			polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
			polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
			polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId().getCodoficina().toString());

			LOGGER.debug("...datos fijos y guardamos la poliza");
			importacionPolizasExtDao.saveOrUpdateEntity(polizaHbm, session, isBatch);
			LOGGER.debug("Poliza guardada");		

			// SUBVENCIONES
			populateSubvenciones(polizaHbm, poliza.getSubvencionesDeclaradas(), session, isBatch);
			LOGGER.debug("Asignamos subvenciones");

			// COBERTURAS
			populateCoberturas(polizaHbm, poliza.getCobertura(), session, true, isBatch, secuencia);
			LOGGER.debug("...coberturas");
			// PARCELAS
			populateParcelasPpal(polizaHbm, poliza.getObjetosAsegurados(), poliza.getCobertura(), session, isBatch);
			asignarParcelasInstalaciones(polizaHbm.getParcelas());
			LOGGER.debug("...parcelas");

			// Sumatorio de superficies parcela
			BigDecimal totalsuperficie = BigDecimal.valueOf(0);
			Iterator<Parcela> itParcelas = polizaHbm.getParcelas().iterator();
			while (itParcelas.hasNext()) {
				Parcela parcela = itParcelas.next();
				Iterator<CapitalAsegurado> itCapitales = parcela.getCapitalAsegurados().iterator();
				while (itCapitales.hasNext()) {
					CapitalAsegurado capitalAseg = itCapitales.next();
					totalsuperficie = totalsuperficie.add(capitalAseg.getSuperficie());
				}
			}
			polizaHbm.setTotalsuperficie(totalsuperficie);
			LOGGER.debug("...superficies");

			// COSTES
			populateCostes(polizaHbm, poliza.getCostePoliza(), session, isBatch, secuencia);
			LOGGER.debug("...costes1-batch");

			// PAGOS
			populatePagos(polizaHbm, poliza.getPago(),
					poliza.getCuentaCobroSiniestros() == null ? "" : poliza.getCuentaCobroSiniestros().getIban(),
					session, isBatch);
			LOGGER.debug("...pagos");

			// FRACCIONAMIENTO
			populateFraccionamiento(polizaHbm, poliza.getPago(), session, isBatch);
			LOGGER.debug("...fraccionamiento");

			// COMISIONES
			es.agroseguro.iTipos.Gastos gastos[] = poliza.getEntidad().getGastosArray();
			populateComisiones(polizaHbm, poliza.getPlan(), poliza.getLinea(), poliza.getPago().getFecha(), gastos, sm, session, isBatch);
			LOGGER.debug("...comisiones");

			// SOCIOS
			populateSocios(polizaHbm, poliza.getRelacionSocios(), session, isBatch);
			LOGGER.debug("...socios");

			// ESTADOS PAGO Y ESTADOS POLIZA
			LOGGER.debug("Antes de actualizarHistEstado");
			if (poliza.getPago().getFecha() != null) {
				LOGGER.debug("Entramos con Fecha de Pago");
				importacionPolizasExtDao.actualizaHistEstado(polizaHbm, poliza.getPago().getFecha().getTime(), session,
						usuario.getCodusuario(), isBatch);
			} else {
				LOGGER.debug("Entramos con Fecha FirmaSeguro");
				importacionPolizasExtDao.actualizaHistEstado(polizaHbm, poliza.getFechaFirmaSeguro().getTime(), session,
						usuario.getCodusuario(), isBatch);
			}
			LOGGER.debug("Estado en el Hco. actualizado");
			// XML de situacion actual
			importacionPolizasExtDao.guardaSituacionActual(polizaHbm, poliza.xmlText(), session);
			LOGGER.debug("Situacion actual guardada");

			importacionPolizasExtDao.saveOrUpdateEntity(polizaHbm, session, isBatch);
			
			// ESC-32955
			if (isBatch) {
				documentacionGedManager.saveNewGedDocPolizaBatch(polizaHbm.getIdpoliza(), usuario.getCodusuario());
			}
			else {
				documentacionGedManager.saveNewGedDocPoliza(polizaHbm.getIdpoliza(), usuario.getCodusuario());
			}
			resultadoImportacion = true;
			LOGGER.debug("Poliza guardada");

		} catch (Exception ex) {

			/* Borramos la poliza si se ha producido un error */
			borrarPoliza(polizaHbm, isBatch, session);

			LOGGER.error("Error en ImportacionPolizasService. ", ex);
			resultadoImportacion = false;
			throw new BusinessException(ex.getMessage());
		}

		return resultadoImportacion;
	}

	/**
	 * Metodo para obtener la poliza complemantaria agricola poblada con todos los
	 * datos
	 * 
	 * @param polizaHbm
	 * @param poliza
	 * @param idEnvio
	 * @param session
	 * @throws Exception
	 */
	public Boolean populateAndValidatePolizaComp(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session, final Boolean isBatch, final String codUsuario) throws Exception {

		Boolean resultadoImportacionCompl = false;
		LOGGER.debug("populateAndValidatePolizaComp - Agricola");
		
		try {
			// Datos relativos al envio
			if (isBatch) {
				polizaHbm.setIdenvio(BigDecimal.valueOf(idEnvio.longValue()));
			}

			/** ESC-15143 ** MODIF TAM (15.10.2021) ** Inicio **/
			Date fechaPago = poliza.getPago().getFecha() == null ? null : poliza.getPago().getFecha().getTime();

			if (fechaPago == null) {
				Date fechaFirma = poliza.getFechaFirmaSeguro().getTime();
				polizaHbm.setFechaenvio(fechaFirma);
				polizaHbm.setFechaPago(fechaFirma);
				LOGGER.debug("**@@** Valor de fechaFirma en Cpl: " + fechaFirma.toString());
			} else {
				polizaHbm.setFechaenvio(fechaPago);
				polizaHbm.setFechaPago(fechaPago);
			}
			/****/

			LOGGER.debug("Asigna datos relativos al envio");

			Long secuencia = importacionPolizasExtDao.getSecuenciaComparativa(session, isBatch);

			// LINEA
			polizaHbm.setLinea(importacionPolizasExtDao.getLineaSeguroBBDD(BigDecimal.valueOf(poliza.getLinea()),
					BigDecimal.valueOf(poliza.getPlan()), session, isBatch));
			LOGGER.debug("...linea");

			// POLIZA PRINCIPAL ******** SOLO PARA COMPLEMENTARIAS
			Poliza polizaPpal = importacionPolizasExtDao.getPolizaPpalBBDD(polizaHbm.getLinea().getCodplan(),
					polizaHbm.getLinea().getCodlinea(), poliza.getReferencia(),
					poliza.getCobertura().getModulo().trim(), session, isBatch);
			if (polizaPpal == null) {
				throw new Exception("No se encuentra la poliza Principal de la complementaria. Revise los datos: plan "
						+ polizaHbm.getLinea().getCodplan() + ", linea " + polizaHbm.getLinea().getCodlinea()
						+ ", referencia " + poliza.getReferencia() + ", modulo complementario "
						+ poliza.getCobertura().getModulo().trim());
			}
			polizaHbm.setPolizaPpal(polizaPpal);
			polizaHbm.setClase(polizaPpal.getClase());
			LOGGER.debug("...poliza Complementaria");

			// COLECTIVO
			BigDecimal codPlan = new BigDecimal(poliza.getPlan());
			Colectivo colectivoHbm = importacionPolizasExtDao.getColectivoBBDDonline(poliza.getColectivo(), session,
					codPlan, isBatch);
			polizaHbm.setColectivo(colectivoHbm);
			LOGGER.debug("...colectivo");

			// E-S MEDIADORA - obtenida del campo 'codigoInterno de la situacion
			// actualizada'
			SubentidadMediadora sm = getESMediadora(poliza.getPlan(), colectivoHbm, session, isBatch);
			LOGGER.debug("...ES Mediadora");

			// ASEGURADO
			Asegurado aseguradoHbm = importacionPolizasExtDao.getAseguradoBBDD(poliza.getAsegurado(),
					colectivoHbm.getTomador().getEntidad().getCodentidad(), session, sm, isBatch);
			LOGGER.debug("...asegurado");
			polizaHbm.setAsegurado(aseguradoHbm);
			polizaHbm.setDiscriminante(aseguradoHbm.getDiscriminante());

			// USUARIO
			Usuario usuario = importacionPolizasExtDao.getUsuarioPolizaBBDD(session, isBatch, codUsuario,
					sm.getId().getCodentidad(), sm.getId().getCodsubentidad());
			polizaHbm.setUsuario(usuario);
			LOGGER.debug("usuario: " + usuario.getCodusuario());

			// ESTADO POLIZA
			polizaHbm.setEstadoPoliza(importacionPolizasExtDao.getEstadoPolizaBBDD(session, isBatch));
			LOGGER.debug("...estado de la poliza");

			// DATOS DE PAGO
			polizaHbm.setEstadoPagoAgp(importacionPolizasExtDao.getEstadoPagoBBDD(session, isBatch));
			LOGGER.debug("...estado de pago");

			// Datos fijos o de importacion directa desde la situacion actual
			polizaHbm.setExterna(1);
			polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_COMPLEMENTARIO);
			polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
			polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
			polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
			polizaHbm.setPacCargada(Constants.CHARACTER_N);
			polizaHbm.setReferencia(poliza.getReferencia());
			polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
			polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
			polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId().getCodoficina().toString());
			LOGGER.debug("...datos fijos");
			// session.saveOrUpdate(polizaHbm);
			importacionPolizasExtDao.saveOrUpdateEntity(polizaHbm, session, isBatch);
			LOGGER.debug("Poliza guardada");

			// COBERTURAS
			populateCoberturas(polizaHbm, poliza.getCobertura(), session, false, isBatch, secuencia);
			LOGGER.debug("...coberturas");

			// PARCELAS
			populateParcelasComp(polizaHbm, poliza.getObjetosAsegurados(), session, isBatch);
			asignarParcelasInstalaciones(polizaHbm.getParcelas());
			LOGGER.debug("...parcelas");

			// Sumatorio de superficies parcela
			BigDecimal totalsuperficie = BigDecimal.valueOf(0);
			Iterator<Parcela> itParcelas = polizaHbm.getParcelas().iterator();
			while (itParcelas.hasNext()) {
				Parcela parcela = itParcelas.next();
				Iterator<CapitalAsegurado> itCapitales = parcela.getCapitalAsegurados().iterator();
				while (itCapitales.hasNext()) {
					CapitalAsegurado capitalAseg = itCapitales.next();
					totalsuperficie = totalsuperficie.add(capitalAseg.getSuperficie());
				}
			}
			polizaHbm.setTotalsuperficie(totalsuperficie);
			LOGGER.debug("...superficie");

			String cccSiniestros = null;
			Set<PagoPoliza> pagoPolizas = polizaPpal.getPagoPolizas();
			if (pagoPolizas != null && !pagoPolizas.isEmpty()) {
				PagoPoliza pp = pagoPolizas.iterator().next();
				cccSiniestros = pp.getIban2() + pp.getCccbanco2();
			}

			populateCostes(polizaHbm, poliza.getCostePoliza(), session, isBatch, secuencia);
			LOGGER.debug("...distribucion de costes (populateCostes1)");
			populatePagos(polizaHbm, poliza.getPago(), cccSiniestros, session, isBatch);
			LOGGER.debug("...pagos");

			// COMISIONES
			es.agroseguro.iTipos.Gastos gastos[] = poliza.getEntidad().getGastosArray();
			populateComisiones(polizaHbm, poliza.getPlan(), poliza.getLinea(), poliza.getPago().getFecha(), gastos, sm, session, isBatch);
			LOGGER.debug("...comisiones");

			// ESTADOS PAGO Y ESTADOS POLIZA
			importacionPolizasExtDao.actualizaHistEstado(polizaHbm,
					poliza.getPago().getFecha() == null ? new Date() : poliza.getPago().getFecha().getTime(), session,
							usuario.getCodusuario(), isBatch);
			LOGGER.debug("Actualizado estado en Hco.");
			// XML de situacion actual
			importacionPolizasExtDao.guardaSituacionActual(polizaHbm, poliza.xmlText(), session);
			LOGGER.debug("Situacion actual de la poliza guardada");

			// P0063699 - Suplementos de sobreprecio por contratacion de complementarias
			importacionPolizasExtDao.actualizaSbp(polizaPpal.getIdpoliza(), session, isBatch);
			
			// ESC-32955
			if (isBatch) {
				documentacionGedManager.saveNewGedDocPolizaBatch(polizaHbm.getIdpoliza(), usuario.getCodusuario());
			}
			else {
				documentacionGedManager.saveNewGedDocPoliza(polizaHbm.getIdpoliza(), usuario.getCodusuario());
			}
			resultadoImportacionCompl = true;

		} catch (Exception ex) {
			importacionPolizasExtDao.deleteEntity(polizaHbm, session, isBatch);
			resultadoImportacionCompl = false;
			throw new BusinessException(ex.getMessage());
		}

		return resultadoImportacionCompl;
	}

	/**
	 * Metodo para obtener la poliza principal de ganado poblada con todos los datos
	 * 
	 * @param polizaHbm
	 * @param poliza
	 * @param idEnvio
	 * @param session
	 * @throws Exception
	 */
	public Boolean populateAndValidatePolizaGanado(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza, final Long idEnvio, final Session session,
			final Boolean isBatch, final String codUsuario) throws Exception {

		Boolean resultadoImportacionGanado = false;
		LOGGER.debug("populateAndValidatePolizaGanado");

		try {

			Long secuencia = importacionPolizasExtDao.getSecuenciaComparativa(session, isBatch);

			/*
			 * Se asigna la fecha de pago en la fecha de env�o y en su defecto la fecha de
			 * la firma
			 */
			Date fechaPago = poliza.getPago().getFecha() == null ? null : poliza.getPago().getFecha().getTime();

			if (fechaPago == null) {
				Date fechaFirma = poliza.getFechaFirmaSeguro().getTime();
				polizaHbm.setFechaenvio(fechaFirma);
				polizaHbm.setFechaPago(fechaFirma);
				LOGGER.debug("**@@** Valor de fechaFirma en Ganado: " + fechaFirma.toString());
			} else {
				polizaHbm.setFechaenvio(fechaPago);
				polizaHbm.setFechaPago(fechaPago);
				LOGGER.debug("**@@** Valor de fechaPago en Ganado: " + fechaPago.toString());
			}

			// LINEA
			Linea linea = importacionPolizasExtDao.getLineaSeguroBBDD(new BigDecimal(poliza.getLinea()),
					new BigDecimal(poliza.getPlan()), session, isBatch);
			LOGGER.debug("Asigna datos de la linea");
			polizaHbm.setLinea(linea);

			// COLECTIVO
			BigDecimal codPlan = new BigDecimal(poliza.getPlan());
			com.rsi.agp.dao.tables.admin.Colectivo col = new com.rsi.agp.dao.tables.admin.Colectivo();
			if (!isBatch) {
				col = importacionPolizasExtDao.getColectivoBBDDonline(poliza.getColectivo(), session, codPlan, isBatch);
				polizaHbm.setColectivo(col);
				LOGGER.debug("... del colectivo online");
			} else {
				col = importacionPolizasExtDao.getColectivoBBDDGan(poliza.getColectivo().getReferencia(),
						poliza.getColectivo().getDigitoControl(), session);
				polizaHbm.setColectivo(col);
				LOGGER.debug("... del colectivo batch");
			}
			
			// E-S MEDIADORA - obtenida del campo 'codigoInterno de la situacion
			// actualizada'
			SubentidadMediadora sm = getESMediadora(poliza.getPlan(), col, session, isBatch);
			LOGGER.debug("...ES Mediadora");

			// ASEGURADO
			if (isBatch) {
				com.rsi.agp.dao.tables.admin.Asegurado aseg = importacionPolizasExtDao.getAseguradoBBDDGanado(
						poliza.getAsegurado().getNif(), col.getTomador().getEntidad().getCodentidad(), session,
						isBatch);

				polizaHbm.setAsegurado(aseg);
				polizaHbm.setDiscriminante(aseg.getDiscriminante());
				LOGGER.debug("...del asegurado (batch)");
			} else {
				com.rsi.agp.dao.tables.admin.Asegurado aseg = importacionPolizasExtDao.getAseguradoBBDDGanadoOnline(
						poliza.getAsegurado().getNif(), col.getTomador().getEntidad().getCodentidad(), session,
						isBatch);
				polizaHbm.setAsegurado(aseg);
				polizaHbm.setDiscriminante(aseg.getDiscriminante());
				LOGGER.debug("...del asegurado (online)");
			}

			// USUARIO
			Usuario usu = importacionPolizasExtDao.getUsuarioPolizaBBDD(session, isBatch, codUsuario,
					sm.getId().getCodentidad(), sm.getId().getCodsubentidad());
			polizaHbm.setUsuario(usu);

			// IMPORTE
			if (poliza.getCostePoliza() != null) {
				CostePoliza costePoliza = poliza.getCostePoliza();
				if (costePoliza.getTotalCosteTomador() != null)
					polizaHbm.setImporte(costePoliza.getTotalCosteTomador());
			}
			LOGGER.debug("...del importe");
			// ESTADO POLIZA
			if (isBatch) {
				polizaHbm.setEstadoPoliza(importacionPolizasExtDao.getEstadoPolizaBBDD(session, isBatch));
			} else {
				String refPoliza = poliza.getReferencia();
				polizaHbm.setEstadoPoliza(
						importacionPolizasExtDao.getEstadoPolizaBBDDGanado(session, isBatch, refPoliza));
			}

			LOGGER.debug("...del estado");

			// Datos fijos o de importacion directa desde la situacion actual
			polizaHbm.setExterna(1);
			polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
			polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
			polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
			polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
			polizaHbm.setPacCargada(Constants.CHARACTER_N);
			polizaHbm.setReferencia(poliza.getReferencia());
			polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
			polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
			polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId().getCodoficina().toString());
			LOGGER.debug("...datos fijos");

			// DATOS DE PAGO
			populatePagoGanado(polizaHbm, poliza.getPago(), poliza.getCuentaCobroSiniestros(),
					poliza.getFechaFirmaSeguro(), isBatch);

			polizaHbm.setEstadoPagoAgp(importacionPolizasExtDao.getEstadoPagoBBDD(session, isBatch));
			LOGGER.debug("...datos de pago");

			importacionPolizasExtDao.saveOrUpdateEntity(polizaHbm, session, isBatch);

			// COBERTURAS
			populateCoberturasGanado(polizaHbm, poliza.getCobertura(), session, true, isBatch, secuencia);
			LOGGER.debug("...coberturas Ganado");

			// EXPLOTACIONES
			populateExplotaciones(polizaHbm, poliza, session, linea.getLineaseguroid(), isBatch);
			LOGGER.debug("...explotaciones");
			// actualizamos el historico de estados de la poliza

			// DISTRIBUCION DE COSTES
			guardaDistribucionCoste(polizaHbm, poliza, session, isBatch, secuencia);
			LOGGER.debug("...distribucion de costes");

			// COMISIONES
			LOGGER.debug("Entramos por online - Comisiones para Ganado");			
            es.agroseguro.iTipos.Gastos gastos[] = poliza.getEntidad().getGastosArray();
			populateComisiones(polizaHbm, poliza.getPlan(), poliza.getLinea(), poliza.getPago().getFecha(), gastos, sm, session, isBatch);
			LOGGER.debug("...comisiones");

			importacionPolizasExtDao.actualizarHistoricoPoliza(polizaHbm, session, isBatch);
			LOGGER.debug("...Historico de estados");
			
			// ESC-32955
			if (isBatch) {
				documentacionGedManager.saveNewGedDocPolizaBatch(polizaHbm.getIdpoliza(), usu.getCodusuario());
			}
			else {
				documentacionGedManager.saveNewGedDocPoliza(polizaHbm.getIdpoliza(), usu.getCodusuario());
			}
			
			resultadoImportacionGanado = true;

		} catch (Exception ex) {
			importacionPolizasExtDao.deleteEntity(polizaHbm, session, isBatch);
			resultadoImportacionGanado = false;
			throw new BusinessException(ex.getMessage());
		}

		return resultadoImportacionGanado;
	}

	public SubentidadMediadora getESMediadora(int codplan, Colectivo colectivo, Session session, Boolean isBatch) {
		SubentidadMediadora sm = null;

		try {
			// Obtenemos la Entidad y Subentidad Mediadora del colectivo:
			if (colectivo != null) {
				// obtener subentidad Mediadora
				Object[] busqueda = importacionPolizasExtDao.obtenerSubEntColectivo(colectivo, codplan, session,
						isBatch);

				if (busqueda != null) {
					BigDecimal entMed = (BigDecimal) busqueda[0];
					BigDecimal subEntMed = (BigDecimal) busqueda[1];

					LOGGER.debug("entMed: " + entMed + " - subEntMed:" + subEntMed);

					SubentidadMediadoraId smId = new SubentidadMediadoraId(entMed, subEntMed);

					sm = new SubentidadMediadora();
					sm.setId(smId);
				}

			}
		} catch (Exception e) {
			return null;
		}
		return sm;
	}

	public SubentidadMediadora getESMediadora_old(es.agroseguro.contratacion.Entidad entidad) {
		SubentidadMediadora sm = null;
		// Obtener el codigo interno
		String codigoInterno = entidad.getCodigoInterno();
		if (!StringUtils.isNullOrEmpty(codigoInterno)) {
			// Entidad mediadora
			BigDecimal entMed = toBigDecimal(codigoInterno.trim().substring(0, 4));
			// Subentidad mediadora
			BigDecimal subEntMed = toBigDecimal(codigoInterno.trim().substring(4));
			LOGGER.debug("entMed: " + entMed + " - subEntMed:" + subEntMed);

			SubentidadMediadoraId smId = new SubentidadMediadoraId(entMed, subEntMed);

			sm = new SubentidadMediadora();
			sm.setId(smId);
		}
		return sm;
	}

	public BigDecimal toBigDecimal(String numero) {
		try {
			return new BigDecimal(numero.trim());
		} catch (Exception e) {
			return null;
		}
	}

	// SUBVENCIONES
	public void populateSubvenciones(final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.SubvencionesDeclaradas subvsDeclaradas,
			final Session session, final Boolean isBatch) throws Exception {

		Set<SubAseguradoENESA> subAseguradoENESAs;
		Set<SubAseguradoCCAA> subAseguradoCCAAs;
		SubAseguradoENESA subAsegEnesaHbm;
		SubAseguradoCCAA subAsegCCAAHbm;
		SubvencionEnesa subvEnesaHbm;
		SubvencionCCAA subvCCAAHbm;

		if (subvsDeclaradas != null) {

			es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradasArr = subvsDeclaradas
					.getSubvencionDeclaradaArray();

			subAseguradoENESAs = new HashSet<SubAseguradoENESA>();
			subAseguradoCCAAs = new HashSet<SubAseguradoCCAA>();

			for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvDeclaradas : subvsDeclaradasArr) {

				subvEnesaHbm = importacionPolizasExtDao.getSubvEnesaBBDD(BigDecimal.valueOf(subvDeclaradas.getTipo()),
						polizaHbm.getLinea().getLineaseguroid(), polizaHbm.getCodmodulo(), session, isBatch);
				// Si la subvencion es de ENESA
				if (subvEnesaHbm != null) {
					subAsegEnesaHbm = new SubAseguradoENESA();
					subAsegEnesaHbm.setAsegurado(polizaHbm.getAsegurado());
					subAsegEnesaHbm.setPoliza(polizaHbm);
					subAsegEnesaHbm.setSubvencionEnesa(subvEnesaHbm);
					importacionPolizasExtDao.saveOrUpdateEntity(subAsegEnesaHbm, session, isBatch);
					subAseguradoENESAs.add(subAsegEnesaHbm);
				}
				// Si la subvencion es de CCAA
				else {
					// subvCCAAHbm = getSubvCCAABBDD(
					subvCCAAHbm = importacionPolizasExtDao.getSubvCCAABBDD(BigDecimal.valueOf(subvDeclaradas.getTipo()),
							polizaHbm.getLinea().getLineaseguroid(), polizaHbm.getCodmodulo(), session, isBatch);
					if (subvCCAAHbm != null) {
						subAsegCCAAHbm = new SubAseguradoCCAA();
						subAsegCCAAHbm.setAsegurado(polizaHbm.getAsegurado());
						subAsegCCAAHbm.setPoliza(polizaHbm);
						subAsegCCAAHbm.setSubvencionCCAA(subvCCAAHbm);
						importacionPolizasExtDao.saveOrUpdateEntity(subAsegCCAAHbm, session, isBatch);
						subAseguradoCCAAs.add(subAsegCCAAHbm);
					}
				}
			}
			/* Tatiana (28.05.2021) */
			if (isBatch) {
				if (!subAseguradoENESAs.isEmpty())
					polizaHbm.setSubAseguradoENESAs(subAseguradoENESAs);
				if (!subAseguradoCCAAs.isEmpty())
					polizaHbm.setSubAseguradoCCAAs(subAseguradoCCAAs);

			}
		}
	}

	// COBERTURAS
	public void populateCoberturas(final Poliza polizaHbm, final es.agroseguro.contratacion.Cobertura cobertura,
			final Session session, final boolean isPrincipal, final Boolean isBatch, final Long secuencia)
			throws DAOException {
		Set<ModuloPoliza> modulosPolHbm;

		Set<ComparativaPoliza> comparativasPolHbm;
		ModuloPoliza moduloPolHbm;
		ComparativaPoliza comparativaPolHbm;

		modulosPolHbm = new HashSet<ModuloPoliza>();
		moduloPolHbm = new ModuloPoliza();

		moduloPolHbm.setId(new ModuloPolizaId(polizaHbm.getIdpoliza(), polizaHbm.getLinea().getLineaseguroid(),
				polizaHbm.getCodmodulo(), secuencia.longValue()));
		moduloPolHbm.setPoliza(polizaHbm);

		importacionPolizasExtDao.saveOrUpdateEntity(moduloPolHbm, session, isBatch);
		modulosPolHbm.add(moduloPolHbm);

		if (isBatch) {
			polizaHbm.setModuloPolizas(modulosPolHbm);
		}

		if (isPrincipal) {

			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = cobertura.getDatosVariables();
			comparativasPolHbm = new HashSet<ComparativaPoliza>();
			// GARANTIZADO
			if (datosVariables != null && datosVariables.getGarantArray() != null
					&& datosVariables.getGarantArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.Garantizado g : datosVariables.getGarantArray()) {
					String descValor = importacionPolizasExtDao.getDescValorCodGarantizado(session, g.getValor(),
							colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(g.getCPMod()),
							BigDecimal.valueOf(g.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(g.getCPMod()),
									BigDecimal.valueOf(g.getCodRCub()), session, colFilaModulo, isBatch),
							BigDecimal.valueOf(g.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// CALCULO INDEMNIZACION
			if (datosVariables != null && datosVariables.getCalcIndemArray() != null
					&& datosVariables.getCalcIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c : datosVariables
						.getCalcIndemArray()) {
					String descValor = importacionPolizasExtDao.getDescValorCalculoIndemnizacion(session, c.getValor(),
							colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(c.getCPMod()),
							BigDecimal.valueOf(c.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(c.getCPMod()),
									BigDecimal.valueOf(c.getCodRCub()), session, colFilaModulo, isBatch),
							BigDecimal.valueOf(c.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// % FRANQUICIA
			if (datosVariables != null && datosVariables.getFranqArray() != null
					&& datosVariables.getFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia pf : datosVariables
						.getFranqArray()) {
					String descValor = importacionPolizasExtDao.getDescValorPctFranquiciaElegible(session,
							pf.getValor(), colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pf.getCPMod()),
							BigDecimal.valueOf(pf.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(pf.getCPMod()),
									BigDecimal.valueOf(pf.getCodRCub()), session, colFilaModulo, isBatch),
							BigDecimal.valueOf(pf.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// MINIMO INDEMNIZABLE
			if (datosVariables != null && datosVariables.getMinIndemArray() != null
					&& datosVariables.getMinIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : datosVariables
						.getMinIndemArray()) {
					String descValor = importacionPolizasExtDao.getDescValorMinimoIndemnizableElegible(session,
							pmi.getValor(), colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pmi.getCPMod()),
							BigDecimal.valueOf(pmi.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(pmi.getCPMod()),
									BigDecimal.valueOf(pmi.getCodRCub()), session, colFilaModulo, isBatch),
							BigDecimal.valueOf(pmi.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			// TIPO FRANQUICIA
			if (datosVariables != null && datosVariables.getTipFranqArray() != null
					&& datosVariables.getTipFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tf : datosVariables.getTipFranqArray()) {
					String descValor = importacionPolizasExtDao.getDescValoTipoFranquicia(session, tf.getValor(),
							colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(tf.getCPMod()),
							BigDecimal.valueOf(tf.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(tf.getCPMod()),
									BigDecimal.valueOf(tf.getCodRCub()), session, colFilaModulo, isBatch),
							new BigDecimal(tf.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}
			// % CAPITAL ASEGURADO
			if (datosVariables != null && datosVariables.getCapAsegArray() != null
					&& datosVariables.getCapAsegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : datosVariables
						.getCapAsegArray()) {
					String descValor = importacionPolizasExtDao.getDescValoCapitalAseguradoElegible(session,
							pca.getValor(), colDescripciones, isBatch);
					comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pca.getCPMod()),
							BigDecimal.valueOf(pca.getCodRCub()),
							BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO),
							(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
									polizaHbm.getCodmodulo(), BigDecimal.valueOf(pca.getCPMod()),
									BigDecimal.valueOf(pca.getCodRCub()), session, colFilaModulo, isBatch),
							BigDecimal.valueOf(pca.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

					importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
					comparativasPolHbm.add(comparativaPolHbm);
				}
			}

			if (datosVariables != null) {
				comparativasPolHbm.addAll(getComparativasRiesgCubEleg(polizaHbm, datosVariables.getRiesgCbtoElegArray(),
						session, secuencia, isBatch));
			} else {
				comparativasPolHbm.addAll(getComparativasRiesgCubEleg(polizaHbm, null, session, secuencia, isBatch));

			}
			/* Tatiana 28.05.2021 */
			if (isBatch) {
				polizaHbm.setComparativaPolizas(comparativasPolHbm);
			}
		} else {

			// Las polizas complementarias no traen datos de coberturas. Son los
			// mismos que la poliza principal.
		}

	}

	/*****/
	// COBERTURAS
	public void populateCoberturasGanado(final Poliza polizaHbm, final es.agroseguro.contratacion.Cobertura cobertura,
			final Session session, final boolean isPrincipal, final Boolean isBatch, final Long secuencia)
			throws DAOException {

		Set<ComparativaPoliza> comparativasPolHbm;

		LOGGER.debug("Dentro de populateCoberturasGanado ");
		LOGGER.debug("Valor de isBatch:" + isBatch);

		Long lineaseguroId = polizaHbm.getLinea().getLineaseguroid();

		/****/
		/** MODULO */
		Set<ModuloPoliza> modulosPolHbm;
		ModuloPoliza moduloPolHbm;
		modulosPolHbm = new HashSet<ModuloPoliza>();
		ModuloPolizaId modId = new ModuloPolizaId();
		modId.setCodmodulo(polizaHbm.getCodmodulo());
		modId.setLineaseguroid(lineaseguroId);
		modId.setIdpoliza(polizaHbm.getIdpoliza());

		modId.setNumComparativa(secuencia.longValue());
		moduloPolHbm = new ModuloPoliza(modId, polizaHbm, null, 1);

		modulosPolHbm.add(moduloPolHbm);

		importacionPolizasExtDao.saveOrUpdateEntity(moduloPolHbm, session, isBatch);

		/* Tatiana 28.05.2021 */
		if (isBatch) {
			polizaHbm.setModuloPolizas(modulosPolHbm);
		}

		if (isPrincipal) {

			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = cobertura.getDatosVariables();
			comparativasPolHbm = new HashSet<ComparativaPoliza>();

			comparativasPolHbm.addAll(getComparativasRiesgCubElegGanado(polizaHbm, datosVariables, session,
					secuencia, isBatch, lineaseguroId));
			
			if (isBatch) {
				polizaHbm.setComparativaPolizas(comparativasPolHbm);
			}
		}
	}

	public ComparativaPoliza generarComparativaPoliza(final Poliza polizaHbm, final BigDecimal cpm,
			final BigDecimal rCub, final BigDecimal codConcepto, final BigDecimal filaModulo, final BigDecimal valor,
			final String descValor, final BigDecimal filaComparativa, final Long idComparativa) {

		ComparativaPoliza comparativaPolHbm = new ComparativaPoliza();
		ComparativaPolizaId id = new ComparativaPolizaId();

		id.setFilamodulo(filaModulo);
		id.setCodconcepto(codConcepto);
		id.setCodconceptoppalmod(cpm);
		id.setCodriesgocubierto(rCub);
		id.setCodmodulo(polizaHbm.getCodmodulo());
		id.setCodvalor(valor);
		id.setIdpoliza(polizaHbm.getIdpoliza());
		id.setLineaseguroid(polizaHbm.getLinea().getLineaseguroid());
		id.setFilacomparativa(filaComparativa);
		id.setIdComparativa(idComparativa);
		comparativaPolHbm.setDescvalor(descValor);
		comparativaPolHbm.setId(id);

		comparativaPolHbm.setPoliza(polizaHbm);

		return comparativaPolHbm;
	}

	/* Pet. 63482 ** MODIF TAM (14.06.2021) * Fin */
	private ComparativaPoliza generarComparativaPolizaGanado(final BigDecimal cpm, final BigDecimal rCub,
			final int codConcepto, final BigDecimal filaModulo, final String descvalor, final BigDecimal codValor,
			final Poliza polizaHbm, final Long idComparativa) {

		ComparativaPoliza cp = new ComparativaPoliza();
		ComparativaPolizaId id = new ComparativaPolizaId();

		RiesgoCubiertoModuloGanado riesgoGanado = new RiesgoCubiertoModuloGanado();

		id.setFilamodulo(filaModulo);
		id.setCodconcepto(BigDecimal.valueOf(codConcepto));
		id.setCodconceptoppalmod(cpm);
		id.setCodriesgocubierto(rCub);

		id.setCodmodulo(polizaHbm.getCodmodulo());
		id.setCodvalor(codValor);

		id.setIdpoliza(polizaHbm.getIdpoliza());
		id.setLineaseguroid(polizaHbm.getLinea().getLineaseguroid());
		id.setFilacomparativa(new BigDecimal(1));
		id.setIdComparativa(idComparativa);

		Modulo moduloGan = new Modulo();
		ConceptoPpalModulo conceptoPpalModuloGan = new ConceptoPpalModulo();

		moduloGan.setCodmoduloasoc(polizaHbm.getCodmodulo());
		conceptoPpalModuloGan.setCodconceptoppalmod(cpm);

		riesgoGanado.setConceptoPpalModulo(conceptoPpalModuloGan);
		riesgoGanado.setModulo(moduloGan);
		cp.setRiesgoCubiertoModuloGanado(riesgoGanado);

		cp.setDescvalor(descvalor);
		cp.setId(id);

		cp.setPoliza(polizaHbm);

		return cp;
	}
	/* Pet. 63482 ** MODIF TAM (14.06.2021) * Fin */

	// PARCELAS
	public void populateParcelasPpal(final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados,
			final es.agroseguro.contratacion.Cobertura cobertura, final Session session, final Boolean isBatch)
			throws Exception {
		Set<Parcela> parcelasHbm;
		Set<CapitalAsegurado> capitalesAseguradosHbm;
		Parcela parcelaHbm;
		CapitalAsegurado capitalAseguradoHbm;
		TipoCapital tipoCapitalHbm;
		CapAsegRelModulo capAsegRelMod;
		Set<CapAsegRelModulo> capitalesAsegRelMod;

		parcelasHbm = new HashSet<Parcela>();

		Node node = objetosAsegurados.getDomNode().getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocument = null;
				try {
					parcelaDocument = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
				} catch (XmlException e) {
					LOGGER.error("Error al parsear una parcela.", e);

				}
				if (parcelaDocument != null) {
					es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela = parcelaDocument.getParcela();
					parcelaHbm = new Parcela();

					Variedad variedadHbm = importacionPolizasExtDao.getVariedad(parcela, polizaHbm, session, isBatch);
					parcelaHbm.setVariedad(variedadHbm);

					Termino terminoHbm = importacionPolizasExtDao.getTermino(parcela, polizaHbm, session, isBatch);
					parcelaHbm.setTermino(terminoHbm);

					if (parcela.getSIGPAC() != null) {
						parcelaHbm.setCodprovsigpac(BigDecimal.valueOf(parcela.getSIGPAC().getProvincia()));
						parcelaHbm.setCodtermsigpac(BigDecimal.valueOf(parcela.getSIGPAC().getTermino()));
						parcelaHbm.setAgrsigpac(BigDecimal.valueOf(parcela.getSIGPAC().getAgregado()));
						parcelaHbm.setZonasigpac(BigDecimal.valueOf(parcela.getSIGPAC().getZona()));
						parcelaHbm.setPoligonosigpac(BigDecimal.valueOf(parcela.getSIGPAC().getPoligono()));
						parcelaHbm.setParcelasigpac(BigDecimal.valueOf(parcela.getSIGPAC().getParcela()));
						parcelaHbm.setRecintosigpac(BigDecimal.valueOf(parcela.getSIGPAC().getRecinto()));
					}

					parcelaHbm.setNomparcela(parcela.getNombre());
					parcelaHbm.setCodcultivo(variedadHbm.getCultivo().getId().getCodcultivo());
					parcelaHbm.setCodvariedad(variedadHbm.getId().getCodvariedad());
					parcelaHbm.setHoja(parcela.getHoja());
					parcelaHbm.setNumero(parcela.getNumero());
					parcelaHbm.setTipoparcela(Constants.TIPO_PARCELA_PARCELA);
					parcelaHbm.setAltaencomplementario(Constants.CHARACTER_N);

					parcelaHbm.setPoliza(polizaHbm);

					importacionPolizasExtDao.saveOrUpdateEntity(parcelaHbm, session, isBatch);

					try {

						es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalAseguradoArr = parcela.getCosecha()
								.getCapitalesAsegurados().getCapitalAseguradoArray();
						capitalesAseguradosHbm = new HashSet<CapitalAsegurado>(capitalAseguradoArr.length);

						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado : capitalAseguradoArr) {

							if (capitalAsegurado.getTipo() >= 100) {
								parcelaHbm.setTipoparcela(Constants.TIPO_PARCELA_INSTALACION);
							}

							capitalAseguradoHbm = new CapitalAsegurado();
							capitalAseguradoHbm.setPrecio(capitalAsegurado.getPrecio());
							capitalAseguradoHbm.setProduccion(BigDecimal.valueOf(capitalAsegurado.getProduccion()));
							capitalAseguradoHbm.setSuperficie(capitalAsegurado.getSuperficie());

							tipoCapitalHbm = importacionPolizasExtDao.getTipoCapital(capitalAsegurado, session,
									isBatch);

							capitalAseguradoHbm.setTipoCapital(tipoCapitalHbm);
							capitalAseguradoHbm.setAltaencomplementario(Constants.CHARACTER_N);

							capitalAseguradoHbm.setParcela(parcelaHbm);
							// session.saveOrUpdate(capitalAseguradoHbm);
							importacionPolizasExtDao.saveOrUpdateEntity(capitalAseguradoHbm, session, isBatch);

							try {
								populateDatosVariables(capitalAseguradoHbm, capitalAsegurado.getDatosVariables(),
										polizaHbm.getLinea().getLineaseguroid(), session, isBatch);
								// Anhadimos los datos variables genericos que vienen en la cobertura
								populateDatosVariables(capitalAseguradoHbm, cobertura.getDatosVariables(),
										polizaHbm.getLinea().getLineaseguroid(), session, isBatch);

								capitalesAsegRelMod = new HashSet<CapAsegRelModulo>();
								capAsegRelMod = new CapAsegRelModulo();

								capAsegRelMod.setCodmodulo(polizaHbm.getCodmodulo());
								capAsegRelMod.setPrecio(capitalAsegurado.getPrecio());
								capAsegRelMod.setProduccion(BigDecimal.valueOf(capitalAsegurado.getProduccion()));
								capAsegRelMod.setCapitalAsegurado(capitalAseguradoHbm);

								capitalesAsegRelMod.add(capAsegRelMod);

								importacionPolizasExtDao.saveOrUpdateEntity(capAsegRelMod, session, isBatch);
								capitalAseguradoHbm.setCapAsegRelModulos(capitalesAsegRelMod);
							} catch (Exception ex) {

								importacionPolizasExtDao.deleteEntity(capitalAseguradoHbm, session, isBatch);
								importacionPolizasExtDao.deleteEntity(parcelaHbm, session, isBatch);
								throw ex;
							}
							capitalesAseguradosHbm.add(capitalAseguradoHbm);
						}

						parcelaHbm.setCapitalAsegurados(capitalesAseguradosHbm);
						parcelasHbm.add(parcelaHbm);
					} catch (Exception ex) {

						importacionPolizasExtDao.deleteEntity(parcelaHbm, session, isBatch);
						throw ex;
					}
				}
			}
			node = node.getNextSibling();
		}

		if (isBatch) {
			polizaHbm.setParcelas(parcelasHbm);
		}

	}

	public void populateDatosVariables(final CapitalAsegurado capitalAseguradoHbm,
			final es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, final Long lineaseguroid,
			final Session session, final Boolean isBatch) throws Exception {

		Set<DatoVariableParcela> datoVariableParcelasHbm;
		DatoVariableParcela datoVariableParcelaHbm;
		Method method;

		if (datosVariables != null) {

			datoVariableParcelasHbm = new HashSet<DatoVariableParcela>();
			List<DiccionarioDatos> dicDatosHbmArr = importacionPolizasExtDao
					.getDiccionarioDatosVariablesParcela(lineaseguroid, session, isBatch);

			for (DiccionarioDatos dicDatosHbm : dicDatosHbmArr) {

				try {
					method = datosVariables.getClass().getMethod("get" + dicDatosHbm.getEtiquetaxml());
					// Es de tipo simple... unicamente nos interesa el valor
					Object datoVar = method.invoke(datosVariables);

					if (datoVar != null) {
						datoVariableParcelaHbm = new DatoVariableParcela();

						datoVariableParcelaHbm.setDiccionarioDatos(dicDatosHbm);

						Object valor = datoVar.getClass().getMethod("getValor").invoke(datoVar);
						if (valor instanceof XmlCalendar) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							datoVariableParcelaHbm.setValor(
									sdf.format(((XmlCalendar) datoVar.getClass().getMethod("getValor").invoke(datoVar))
											.getTime()));
						} else {
							datoVariableParcelaHbm
									.setValor(datoVar.getClass().getMethod("getValor").invoke(datoVar).toString());
						}
						datoVariableParcelaHbm.setCapitalAsegurado(capitalAseguradoHbm);

						importacionPolizasExtDao.saveOrUpdateEntity(datoVariableParcelaHbm, session, isBatch);
						datoVariableParcelasHbm.add(datoVariableParcelaHbm);
					}
				} catch (NoSuchMethodException ex) {
					// Es de tipo complejo... multiples ocurrencias y nos
					// interesa el valor, el concepto principal y el riesgo
					// cubierto
					try {
						method = datosVariables.getClass().getMethod("get" + dicDatosHbm.getEtiquetaxml() + "Array");
						Object[] datosVar = (Object[]) method.invoke(datosVariables);

						for (Object datoVar : datosVar) {
							if (datoVar != null) {
								datoVariableParcelaHbm = new DatoVariableParcela();

								datoVariableParcelaHbm.setCodconceptoppalmod(Integer
										.valueOf(datoVar.getClass().getMethod("getCPMod").invoke(datoVar).toString()));
								datoVariableParcelaHbm.setCodriesgocubierto(Integer.valueOf(
										datoVar.getClass().getMethod("getCodRCub").invoke(datoVar).toString()));
								datoVariableParcelaHbm.setDiccionarioDatos(dicDatosHbm);
								Object valor = datoVar.getClass().getMethod("getValor").invoke(datoVar);
								if (valor instanceof XmlCalendar) {
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									datoVariableParcelaHbm.setValor(sdf.format(
											((XmlCalendar) datoVar.getClass().getMethod("getValor").invoke(datoVar))
													.getTime()));
								} else {
									/* ESC-14981 ** MODIF TAM (31/08/2021) */
									String val = datoVar.getClass().getMethod("getValor").invoke(datoVar).toString();
									if (val.equals("N")) {
										datoVariableParcelaHbm.setValor("-2");
									}else if (val.equals("S")) {
										datoVariableParcelaHbm.setValor("-1");
									}else {
										datoVariableParcelaHbm.setValor(
											datoVar.getClass().getMethod("getValor").invoke(datoVar).toString());
									}	
									/* ESC-14981 ** MODIF TAM (31/08/2021) Fin */
								}
								datoVariableParcelaHbm.setCapitalAsegurado(capitalAseguradoHbm);

								importacionPolizasExtDao.saveOrUpdateEntity(datoVariableParcelaHbm, session, isBatch);
								datoVariableParcelasHbm.add(datoVariableParcelaHbm);
							}
						}
					} catch (NoSuchMethodException nsmex) {
						// El dato variable no pertenece a la parcela
						// Do nothing
						LOGGER.debug("El campo variable " + dicDatosHbm.getEtiquetaxml() + " no es de ambito parcela.");
					}
				}
			}
			if (null == capitalAseguradoHbm.getDatoVariableParcelas()
					|| capitalAseguradoHbm.getDatoVariableParcelas().size() == 0) {
				capitalAseguradoHbm.setDatoVariableParcelas(datoVariableParcelasHbm);
			} else {
				capitalAseguradoHbm.getDatoVariableParcelas().addAll(datoVariableParcelasHbm);
			}

		}
	}

	public void asignarParcelasInstalaciones(final Set<Parcela> parcelas) {

		Iterator<Parcela> it;

		it = parcelas.iterator();
		while (it.hasNext()) {

			Parcela parcelaHbm = it.next();
			if (parcelaHbm.getTipoparcela() == Constants.TIPO_PARCELA_INSTALACION) {

				// Es parcela de instalaciones, buscamos sus parcelas
				// asociadas
				for (Parcela parcelaHbmAux : parcelas) {

					// Se compara por Ubicacion (SIGPAC o Ident. Catastral),
					// cultivo y variedad
					int comparation = new Comparator<Parcela>() {

						@Override
						public int compare(final Parcela arg0, final Parcela arg1) {

							int result = -1;

							// Comparamos el cultivo/variedad
							if (arg0.getCodcultivo().equals(arg1.getCodcultivo())
									&& arg0.getCodvariedad().equals(arg1.getCodvariedad())) {
								result = 0;
							}
							// Si aplica, comparamos la ubicacion
							if (result == 0) {
								boolean isSigPac = "".equals(arg0.getPoligono() == null ? "" : arg0.getPoligono())
										&& "".equals(arg0.getParcela() == null ? "" : arg0.getParcela());

								if (isSigPac) {
									if (arg0.getCodprovsigpac().equals(arg1.getCodprovsigpac())
											&& arg0.getCodtermsigpac().equals(arg1.getCodtermsigpac())
											&& arg0.getAgrsigpac().equals(arg1.getAgrsigpac())
											&& arg0.getZonasigpac().equals(arg1.getZonasigpac())
											&& arg0.getPoligonosigpac().equals(arg1.getPoligonosigpac())
											&& arg0.getParcelasigpac().equals(arg1.getParcelasigpac())
											&& arg0.getRecintosigpac().equals(arg1.getRecintosigpac())) {
										result = 0;
									}
								} else {
									if (arg0.getPoligono().equals(arg1.getPoligono())
											&& arg0.getParcela().equals(arg1.getParcela())) {
										result = 0;
									}
								}
							}

							return result;
						}
					}.compare(parcelaHbm, parcelaHbmAux);

					// Si son la misma parcela se asigna la instalacion
					if (comparation == 0) {
						parcelaHbmAux.setIdparcelaestructura(parcelaHbm.getIdparcela());
					}
				}
			}
		}
	}

	// DISTRIBUCION DE COSTES
	public void populateCostes(final Poliza polizaHbm,
			final es.agroseguro.contratacion.costePoliza.CostePoliza costePoliza, final Session session,
			final Boolean isBatch, final Long idComparativa) throws Exception {

		Set<com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015> distribucionCostes2015 = new HashSet<com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015>(
				1);
		com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015 dcHbm = new com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015();

		if (polizaHbm.getModuloPolizas() != null && !polizaHbm.getModuloPolizas().isEmpty()) {
			Iterator<ModuloPoliza> polizaHbmModuloPolizaIterator = polizaHbm.getModuloPolizas().iterator();
			if (polizaHbmModuloPolizaIterator.hasNext()) {
				ModuloPoliza modPoliza = polizaHbmModuloPolizaIterator.next();
				if (modPoliza.getId().getNumComparativa() != null) {
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
					if (costeGrupoNeg != null) {
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
						if (financiacion != null && financiacion.getRecargoAval() != null) {
							dcHbm.setRecargoaval(financiacion.getRecargoAval());
						}
						if (financiacion != null && financiacion.getRecargoFraccionamiento() != null) {
							dcHbm.setRecargofraccionamiento(financiacion.getRecargoFraccionamiento());
						}
						// Bonificaciones y recargos
						dcHbm = cargaBonifRecargUnificado(dcHbm, costeGrupoNeg);
						// Carga las subvenciones de CCAA y ENESA en el objeto de la distribucion de
						// costes
						dcHbm = cargarSubvencionesDC(dcHbm, costeGrupoNeg);
						break;
					}
				}
			}
		}

		dcHbm.setIdcomparativa(new BigDecimal(idComparativa));
		dcHbm.setFilacomparativa(BigDecimal.valueOf(1));

		distribucionCostes2015.add(dcHbm);
		/* Tatiana 28.05.2021 */
		if (isBatch) {
			polizaHbm.setDistribucionCoste2015s(distribucionCostes2015);
		}

		importacionPolizasExtDao.saveOrUpdateEntity(dcHbm, session, isBatch);
	}

	public DistribucionCoste2015 cargaBonifRecargUnificado(DistribucionCoste2015 dc,
			CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.BonificacionRecargo[] boniRecargo = costeGrupoNegocio
				.getBonificacionRecargoArray();
		if (boniRecargo != null) {
			// Bucle para anadir las distribuciones de coste de las
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

	public DistribucionCoste2015 cargarSubvencionesDC(final DistribucionCoste2015 dc,
			final CosteGrupoNegocio costeGrupoNegocio) {
		es.agroseguro.contratacion.costePoliza.SubvencionCCAA[] subCCAA = costeGrupoNegocio.getSubvencionCCAAArray();
		es.agroseguro.contratacion.costePoliza.SubvencionEnesa[] subEnesa = costeGrupoNegocio.getSubvencionEnesaArray();
		// Subvenciones CCAA
		if (subCCAA != null) {
			for (int i = 0; i < subCCAA.length; i++) {
				// Bucle para anadir las distribuciones de coste de las
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

	// PAGOS
	public void populatePagos(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pago,
			final String cccSiniestros, final Session session, final Boolean isBatch) throws Exception {

		LOGGER.debug("** Dentro de populatePagos");
		Set<PagoPoliza> pagoPolizas;
		PagoPoliza pagoHbm;
		String cccbanco = "";
		Date fecha;
		Character formapago = 'C';
		Character destDomiciliacion = null;
		String titularCuenta = "";
		BigDecimal importe;

		pagoPolizas = new HashSet<PagoPoliza>(1);
		pagoHbm = new PagoPoliza();

		if (pago.getCuenta() != null && !StringUtils.isNullOrEmpty(pago.getCuenta().getIban())) {
			cccbanco = pago.getCuenta().getIban();
		}
		if (!StringUtils.isNullOrEmpty(pago.getForma())) {
			formapago = pago.getForma().toString().charAt(0);
		}
		if (pago.getCuenta() != null && !StringUtils.isNullOrEmpty(pago.getCuenta().getDestinatario())) {
			destDomiciliacion = pago.getCuenta().getDestinatario().charAt(0);
		}
		if (pago.getCuenta() != null && !StringUtils.isNullOrEmpty(pago.getCuenta().getTitular())) {
			titularCuenta = pago.getCuenta().getTitular();
		}		

		fecha = pago.getFecha() == null ? null : pago.getFecha().getTime();		
		importe = pago.getImporte();

		pagoHbm.setCccbanco(StringUtils.isNullOrEmpty(cccbanco) ? "" : cccbanco.substring(4));
		LOGGER.debug("Valor de banco:" + pagoHbm.getCccbanco());
		pagoHbm.setCccbanco2(StringUtils.isNullOrEmpty(cccSiniestros) ? "" : cccSiniestros.substring(4));
		pagoHbm.setFecha(fecha);
		pagoHbm.setFormapago(formapago);
		pagoHbm.setDestinatarioDomiciliacion(destDomiciliacion);
		pagoHbm.setTitularCuenta(titularCuenta);
		pagoHbm.setImporte(importe);
		pagoHbm.setTipoPago(new BigDecimal(0));

		if (!StringUtils.isNullOrEmpty(cccbanco)) {
			pagoHbm.setIban(cccbanco.substring(0, 4));
			LOGGER.debug("Valor de iban:" + pagoHbm.getIban());
		} else {
			pagoHbm.setIban("ES  ");
		}

		if (!StringUtils.isNullOrEmpty(cccSiniestros)) {
			pagoHbm.setIban2(cccSiniestros.substring(0, 4));
		}

		/* Defecto Num 12 (18.06.2021) ** Inicio */
		if (pago.getDomiciliado() != null && pago.getDomiciliado().equals("T")) {
			pagoHbm.setEnvioIbanAgro('S');
			pagoHbm.setTipoPago(new BigDecimal(2));
		}
		/* Defecto Num 12 (18.06.2021) ** Fin */

		pagoHbm.setPoliza(polizaHbm);
		importacionPolizasExtDao.saveOrUpdateEntity(pagoHbm, session, isBatch);

		pagoPolizas.add(pagoHbm);

		if (isBatch) {
			polizaHbm.setPagoPolizas(pagoPolizas);
		}

	}

	// FRACCIONAMIENTO DE PAGO
	public void populateFraccionamiento(final Poliza polizaHbm, es.agroseguro.contratacion.Pago pago,
			final Session session, final Boolean isBatch) throws Exception {
		if (null != pago && null != pago.getFraccionamiento()) {
			if (null != pago.getFraccionamiento().getAval()) {
				com.rsi.agp.dao.tables.poliza.DatosAval datosAval = new com.rsi.agp.dao.tables.poliza.DatosAval();
				// datosAval.setIdpoliza(polizaHbm.getIdpoliza());
				datosAval.setImporteAval(pago.getFraccionamiento().getAval().getImporte());
				datosAval.setNumeroAval(new BigDecimal(pago.getFraccionamiento().getAval().getNumero()));
				polizaHbm.setDatosAval(datosAval);
				datosAval.setPoliza(polizaHbm);
				importacionPolizasExtDao.saveOrUpdateEntity(datosAval, session, isBatch);
			}
			com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015 dc = (DistribucionCoste2015) polizaHbm
					.getDistribucionCoste2015s().iterator().next();
			dc.setImportePagoFracc(pago.getImporte());
			dc.setPeriodoFracc(pago.getFraccionamiento().getPeriodo());
			dc.setPoliza(polizaHbm);
			importacionPolizasExtDao.saveOrUpdateEntity(dc, session, isBatch);

			if (isBatch) {
				polizaHbm.getDistribucionCoste2015s().clear();
				polizaHbm.getDistribucionCoste2015s().add(dc);
			}

		}
	}

	// COMISIONES
	public void populateComisiones(final Poliza polizaHbm, int plan, int linea, Calendar fechaEfecto,
			es.agroseguro.iTipos.Gastos gastos[], SubentidadMediadora sm, final Session session, Boolean isBatch)
			throws Exception {

		CultivosSubentidades coms;
		Character grupoNeg;
		PolizaPctComisiones polPctCom = null;
		Set<DistribucionCoste2015> sDistCostes;
		BigDecimal primaNeta = new BigDecimal(0.0);
		Map<String, String> impCom = new HashMap<String, String>(3);

		// Sin los datos de la ES Mediadora no se pueden buscar las comisiones
		if (sm == null) {
			throw new Exception(
					"No se encuentran las comisiones ya que no se ha obtenido la ES Mediadora de la poliza");
		}

		// Obtenemos las comisiones
		coms = importacionPolizasExtDao.getComisionesSubentidades(plan, linea, fechaEfecto, sm.getId().getCodentidad(),
				sm.getId().getCodsubentidad(), session, isBatch);

		if (null != coms) {
			// Obtenemos la distribucion de costes de la poliza
			if (isBatch) {
				sDistCostes = polizaHbm.getDistribucionCoste2015s();
			} else {
				// Recuperamos la distribucion de coste para actualizar las comisiones
				List<DistribucionCoste2015> lDistCostes = distribucionCosteDAO
						.getDistribucionCoste2015ByIdPoliza(polizaHbm.getIdpoliza());

				sDistCostes = new HashSet<DistribucionCoste2015>(lDistCostes.size());

				for (DistribucionCoste2015 disCost : lDistCostes) {
					sDistCostes.add(disCost);
				}
			}

			for (es.agroseguro.iTipos.Gastos gasto : gastos) {
				polPctCom = new PolizaPctComisiones();
				grupoNeg = gasto.getGrupoNegocio().charAt(0);

				polPctCom.setPoliza(polizaHbm);
				polPctCom.setPctadministracion(gasto.getAdministracion());
				polPctCom.setPctadquisicion(gasto.getAdquisicion());
				polPctCom.setPctcommax(gasto.getComisionMediador());
				polPctCom.setPctentidad(coms.getPctentidad());
				polPctCom.setPctesmediadora(coms.getPctmediador());
				polPctCom.setGrupoNegocio(grupoNeg);

				importacionPolizasExtDao.saveOrUpdateEntity(polPctCom, session, isBatch);

				for (DistribucionCoste2015 disCost : sDistCostes) {
					// Calculamos las comisiones del grupo de negocio correspondiente
					if (grupoNeg.equals(disCost.getGrupoNegocio())) {
						primaNeta = disCost.getPrimacomercialneta();

						// Obtenemos la comisión mediadora y comesión subentidad mediadora
						impCom = polizasPctComisionesManager.obtenerDesgloseComisiones(polPctCom, primaNeta, null);

						if (impCom != null && !impCom.isEmpty()) {
							disCost.setImpComsEntidad(new BigDecimal(impCom.get("comTotalE")));
							disCost.setImpComsESMed(new BigDecimal(impCom.get("comTotalE_S")));
						} else {
							LOGGER.debug("populateComisiones: el campo impCom es nulo o vacio");
						}

						break;
					}
				}

				if (isBatch) {
					polizaHbm.setPolizaPctComisiones(polPctCom);
				}
			}
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String fecEfecto = df.format(fechaEfecto == null ? new Date() : fechaEfecto.getTime());

			if (!isBatch) {
				borrarPoliza(polizaHbm, isBatch, session);
			}

			throw new Exception(polizaHbm.getReferencia() + ": No se encuentran las comisiones del plan " + plan
					+ ", linea " + linea + ", fecha de efecto " + fecEfecto + ", entMed " + sm.getId().getCodentidad()
					+ ", subEntMed " + sm.getId().getCodsubentidad());
		}
	}
	
	// SOCIOS
	public void populateSocios(final Poliza polizaHbm,
			final es.agroseguro.contratacion.declaracionSubvenciones.RelacionSocios relacionSocios,
			final Session session, final Boolean isBatch) throws Exception {

		LOGGER.debug("**@@**Dentro de populateSocios");

		if (relacionSocios != null) {

			PolizaSocio socioPolizaHbm;
			Socio socioHbm;
			SubvencionSocio subSocioHbm;

			Long idAsegurado = polizaHbm.getAsegurado().getId();
			LOGGER.debug("**@@** Valor de idAsegurado" + idAsegurado);

			/* Incluimos como filtro el id del asegurado */
			es.agroseguro.contratacion.declaracionSubvenciones.Socio[] socios = relacionSocios.getSocioArray();
			for (es.agroseguro.contratacion.declaracionSubvenciones.Socio socio : socios) {
				LOGGER.debug("**@@** Valor de nif-socio: " + socio.getNif());

				socioHbm = importacionPolizasExtDao.getSocio(socio, idAsegurado, session, isBatch);

				if (socioHbm == null) {
					socioHbm = new Socio();

					socioHbm.setAsegurado(polizaHbm.getAsegurado());

					socioHbm.setId(new SocioId(socio.getNif(), polizaHbm.getAsegurado().getId()));

					if (socio.getRazonSocial() != null) {
						socioHbm.setRazonsocial(socio.getRazonSocial().getRazonSocial());
						socioHbm.setTipoidentificacion("CIF");
					} else {
						socioHbm.setNombre(socio.getNombreApellidos().getNombre());
						socioHbm.setApellido1(socio.getNombreApellidos().getApellido1());
						socioHbm.setApellido2(socio.getNombreApellidos().getApellido2());
						socioHbm.setTipoidentificacion("NIF");
					}
					if (socio.getSubvencionesDeclaradas() != null
							&& socio.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
						socioHbm.setAtp("SI");
						socioHbm.setNumsegsocial(socio.getSubvencionesDeclaradas().getSeguridadSocial().getProvincia()
								+ "" + socio.getSubvencionesDeclaradas().getSeguridadSocial().getNumero()
								+ socio.getSubvencionesDeclaradas().getSeguridadSocial().getCodigo());
						socioHbm.setRegimensegsocial(BigDecimal
								.valueOf(socio.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen()));
					} else {
						socioHbm.setAtp("NO");
					}

					importacionPolizasExtDao.saveOrUpdateEntity(socioHbm, session, isBatch);
				}

				// Asociamos el socio a la poliza
				socioPolizaHbm = new PolizaSocio();
				socioPolizaHbm.setSocio(socioHbm);
				socioPolizaHbm.setOrden(BigDecimal.valueOf(socio.getNumero()));
				socioPolizaHbm.setPoliza(polizaHbm);

				importacionPolizasExtDao.saveOrUpdateEntity(socioPolizaHbm, session, isBatch);
				if (socio.getSubvencionesDeclaradas() != null) {
					es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada[] subvsDeclaradaSocio = socio
							.getSubvencionesDeclaradas().getSubvencionDeclaradaArray();
					for (es.agroseguro.contratacion.declaracionSubvenciones.SubvencionDeclarada subvDeclaradaSocio : subvsDeclaradaSocio) {
						subSocioHbm = new SubvencionSocio();
						subSocioHbm.setSocio(socioHbm);
						subSocioHbm.setSubvencionEnesa(importacionPolizasExtDao.getSubvEnesaBBDD(
								BigDecimal.valueOf(subvDeclaradaSocio.getTipo()),
								polizaHbm.getLinea().getLineaseguroid(), polizaHbm.getCodmodulo(), session, isBatch));

						if (Constants.SUBVENCION_JOVEN_HOMBRE
								.equals(BigDecimal.valueOf(subvDeclaradaSocio.getTipo()))) {
							socioHbm.setJovenagricultor('H');
						} else if (Constants.SUBVENCION_JOVEN_MUJER
								.equals(BigDecimal.valueOf(subvDeclaradaSocio.getTipo()))) {
							socioHbm.setJovenagricultor('M');
						} else if (Constants.SUBVENCION20.equals(BigDecimal.valueOf(subvDeclaradaSocio.getTipo()))) {
							socioHbm.setAtp("SI");
							socioHbm.setNumsegsocial(
									socio.getSubvencionesDeclaradas().getSeguridadSocial().getProvincia() + ""
											+ socio.getSubvencionesDeclaradas().getSeguridadSocial().getNumero()
											+ socio.getSubvencionesDeclaradas().getSeguridadSocial().getCodigo());
							socioHbm.setRegimensegsocial(BigDecimal
									.valueOf(socio.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen()));
						}

						subSocioHbm.setPoliza(polizaHbm);

						importacionPolizasExtDao.saveOrUpdateEntity(subSocioHbm, session, isBatch);

						if (isBatch) {
							polizaHbm.getSubvencionSocios().add(subSocioHbm);
						}
					}
				}

				if (isBatch) {
					polizaHbm.getPolizaSocios().add(socioPolizaHbm);
				}

			}
		}
	}

	// PARCELAS COMPLEMENTARIAS
	// Metodo que transforma las parcelas de la situacion actual de la poliza
	// complementaria en el formato esperado por el modelo de datos de Agroplus
	// y puebla el objeto Hibernate encargado de la importacion
	public void populateParcelasComp(final Poliza polizaHbm,
			final es.agroseguro.contratacion.ObjetosAsegurados objetosAsegurados, final Session session,
			final Boolean isBatch) throws Exception {

		Set<Parcela> parcelasHbm;
		Parcela parcelaHbm;
		Set<CapitalAsegurado> capitalesAseguradosHbm;
		CapitalAsegurado capAsegHbm;
		es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcelaComp;
		BigDecimal precioPpal;
		BigDecimal produccionPpal;
		Boolean isAltaEnComp;

		parcelasHbm = new HashSet<Parcela>();

		// Recorremos las parcelas de la poliza principal
		Set<Parcela> parcelasPolPpal = polizaHbm.getPolizaPpal().getParcelas();
		for (Parcela parcelaPpal : parcelasPolPpal) {

			isAltaEnComp = Boolean.FALSE;
			parcelaComp = null;
			precioPpal = null;
			produccionPpal = null;

			// Inicamente para parcelas NO de instalaciones
			if (Constants.TIPO_PARCELA_PARCELA.equals(parcelaPpal.getTipoparcela())) {

				Node node = objetosAsegurados.getDomNode().getFirstChild();
				while (node != null) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						es.agroseguro.contratacion.parcela.ParcelaDocument parcelaDocument = null;
						try {
							parcelaDocument = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(node);
						} catch (XmlException e) {
							LOGGER.error("Error al parsear una parcela.", e);

						}
						if (parcelaDocument != null) {
							es.agroseguro.contratacion.parcela.ParcelaDocument.Parcela parcela = parcelaDocument
									.getParcela();
							if (parcelaPpal.getHoja().equals(Integer.valueOf(parcela.getHoja()))
									&& parcelaPpal.getNumero().equals(Integer.valueOf(parcela.getNumero()))) {
								isAltaEnComp = Boolean.TRUE;
								parcelaComp = parcela;
							}
						}
					}
					node = node.getNextSibling();
				}

				// Recorremos los capitales asegurados
				Set<CapitalAsegurado> capitalesAsegPpal = parcelaPpal.getCapitalAsegurados();
				capitalesAseguradosHbm = new HashSet<CapitalAsegurado>(capitalesAsegPpal.size());
				for (CapitalAsegurado capAsegPpal : capitalesAsegPpal) {

					// unicamente para tipos de capital de produccion
					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PRODUCCION)
							.equals(capAsegPpal.getTipoCapital().getCodconcepto())) {

						Set<CapAsegRelModulo> capAsegRelModulosPpal = capAsegPpal.getCapAsegRelModulos();
						for (CapAsegRelModulo capAsegRelMod : capAsegRelModulosPpal) {

							if (polizaHbm.getPolizaPpal().getCodmodulo().equals(capAsegRelMod.getCodmodulo())) {
								precioPpal = capAsegRelMod.getPrecio();
								produccionPpal = capAsegRelMod.getProduccion();
								break;
							}
						}

						if (precioPpal == null || produccionPpal == null) {
							throw new Exception(
									"No se ha podido obtener precio y produccion de la parcela de la poliza principal.");
						}

						capAsegHbm = new CapitalAsegurado();

						capAsegHbm.setPrecio(precioPpal);
						capAsegHbm.setProduccion(produccionPpal);
						capAsegHbm.setSuperficie(capAsegPpal.getSuperficie());
						capAsegHbm.setTipoCapital(capAsegPpal.getTipoCapital());
						capAsegHbm
								.setAltaencomplementario(isAltaEnComp ? Constants.CHARACTER_S : Constants.CHARACTER_N);
						if (parcelaComp != null) {

							es.agroseguro.contratacion.parcela.CapitalAsegurado[] capitalAseguradoArr = parcelaComp
									.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray();
							for (es.agroseguro.contratacion.parcela.CapitalAsegurado capitalAsegurado : capitalAseguradoArr) {

								if (capAsegPpal.getTipoCapital().getCodtipocapital()
										.equals(BigDecimal.valueOf(capitalAsegurado.getTipo()))) {

									capAsegHbm.setIncrementoproduccion(
											BigDecimal.valueOf(capitalAsegurado.getProduccion()));
								}
							}
						}
						capitalesAseguradosHbm.add(capAsegHbm);
					}
				}
				// Se ha encontrado al menos un capital asegurado de produccion
				// en la parcela principal. Debemos crear la complementaria
				if (!capitalesAseguradosHbm.isEmpty()) {
					parcelaHbm = new Parcela();

					parcelaHbm.setVariedad(parcelaPpal.getVariedad());
					parcelaHbm.setTermino(parcelaPpal.getTermino());
					parcelaHbm.setPoligono(parcelaPpal.getPoligono());
					parcelaHbm.setParcela(parcelaPpal.getParcela());
					parcelaHbm.setCodprovsigpac(parcelaPpal.getCodprovsigpac());
					parcelaHbm.setCodtermsigpac(parcelaPpal.getCodtermsigpac());
					parcelaHbm.setAgrsigpac(parcelaPpal.getAgrsigpac());
					parcelaHbm.setZonasigpac(parcelaPpal.getZonasigpac());
					parcelaHbm.setPoligonosigpac(parcelaPpal.getPoligonosigpac());
					parcelaHbm.setParcelasigpac(parcelaPpal.getParcelasigpac());
					parcelaHbm.setRecintosigpac(parcelaPpal.getRecintosigpac());
					parcelaHbm.setNomparcela(parcelaPpal.getNomparcela());
					parcelaHbm.setCodcultivo(parcelaPpal.getCodcultivo());
					parcelaHbm.setCodvariedad(parcelaPpal.getCodvariedad());
					parcelaHbm.setHoja(parcelaPpal.getHoja());
					parcelaHbm.setNumero(parcelaPpal.getNumero());
					parcelaHbm.setTipoparcela(Constants.TIPO_PARCELA_PARCELA);
					parcelaHbm.setPoliza(polizaHbm);
					parcelaHbm.setAltaencomplementario(isAltaEnComp ? Constants.CHARACTER_S : Constants.CHARACTER_N);

					importacionPolizasExtDao.saveOrUpdateEntity(parcelaHbm, session, isBatch);

					for (CapitalAsegurado capAsegHbmAux : capitalesAseguradosHbm) {
						capAsegHbmAux.setParcela(parcelaHbm);

						importacionPolizasExtDao.saveOrUpdateEntity(capAsegHbmAux, session, isBatch);
					}
					parcelaHbm.setCapitalAsegurados(capitalesAseguradosHbm);

					parcelasHbm.add(parcelaHbm);
				}
			}
		}

		if (isBatch) {
			polizaHbm.setParcelas(parcelasHbm);
		}

	}

	// GANADO
	public void populatePagoGanado(final Poliza polizaHbm, final es.agroseguro.contratacion.Pago pagoAct,
			final es.agroseguro.contratacion.CuentaCobroSiniestros ccsAct, final Calendar fechaFirmaSeguro,
			final boolean isBatch) {

		PagoPoliza pago = new PagoPoliza();
		pago.setPoliza(polizaHbm);
		pago.setFormapago(pagoAct.getForma() != null ? pagoAct.getForma().charAt(0) : 'C');
		if (isBatch) {
			pago.setFecha(pagoAct.getFecha() != null ? pagoAct.getFecha().getTime() : null);
		} else {
			/*
			 * Se asigna la fecha de pago en la fecha de env�o y en su defecto la fecha de
			 * la firma
			 */
			Date fechaPago = pagoAct.getFecha() != null ? pagoAct.getFecha().getTime() : null;
			if (fechaPago == null) {
				Date fechaFirma = fechaFirmaSeguro.getTime();
				pago.setFecha(fechaFirma);
				LOGGER.debug("**@@** Asignamos a la fecha de PAgo la fecha de la firma: " + fechaFirma.toString());
			} else {
				pago.setFecha(pagoAct.getFecha() != null ? pagoAct.getFecha().getTime() : null);
			}
		}
		pago.setImporte(pagoAct.getImporte());

		// Si vienen informados los datos del IBAN para el pago de prima
		if (pagoAct.getCuenta() != null && !StringUtils.isNullOrEmpty(pagoAct.getCuenta().getIban())) {
			pago.setIban(pagoAct.getCuenta().getIban().substring(0, 4));
			pago.setCccbanco(pagoAct.getCuenta().getIban().substring(4));
		}
		// Si no vienen informados se inserta el IBAN a 'ES' ya que es un dato
		// obligatorio en la BBDD
		else {
			pago.setIban("ES  ");
		}

		if (ccsAct != null && !StringUtils.isNullOrEmpty(ccsAct.getIban())) {
			pago.setIban2(ccsAct.getIban().substring(0, 4));
			pago.setCccbanco2(ccsAct.getIban().substring(4));
		}

		pago.setDomiciliado(pagoAct.getDomiciliado() != null ? pagoAct.getDomiciliado().charAt(0) : null);
		pago.setTipoPago(new BigDecimal(0));

		/* Defecto Num 12 (18.06.2021) ** Inicio */
		if (pagoAct.getDomiciliado().equals("T")) {
			pago.setEnvioIbanAgro('S');
			pago.setTipoPago(new BigDecimal(2));
		}
		/* Defecto Num 12 (18.06.2021) ** Fin */

		Set<PagoPoliza> pagosPoliza = new HashSet<PagoPoliza>();
		pagosPoliza.add(pago);
		polizaHbm.setPagoPolizas(pagosPoliza);

		LOGGER.debug("MPM - Informacion del pago insertada");
	}

	public void populateModulo(final es.agroseguro.contratacion.Poliza poliza, final Session session,
			final Poliza polizaHbm, Long lineaSeguroId, final Boolean isBatch) {
		try {

			Set<ModuloPoliza> modulosPolHbm;
			ModuloPoliza moduloPolHbm;
			modulosPolHbm = new HashSet<ModuloPoliza>();
			ModuloPolizaId modId = new ModuloPolizaId();
			modId.setCodmodulo(poliza.getCobertura().getModulo().trim());
			modId.setLineaseguroid(lineaSeguroId);
			modId.setIdpoliza(polizaHbm.getIdpoliza());
			Long secuencia = importacionPolizasExtDao.getSecuenciaComparativa(session, isBatch);
			modId.setNumComparativa(secuencia.longValue());
			moduloPolHbm = new ModuloPoliza(modId, polizaHbm, null, 1);

			modulosPolHbm.add(moduloPolHbm);

			importacionPolizasExtDao.saveOrUpdateEntity(moduloPolHbm, session, isBatch);

		} catch (Exception ex) {
			LOGGER.error("## Error al crear el modulo de la poliza : " + poliza.getReferencia() + " LINEA: "
					+ poliza.getLinea() + " PLAN: " + poliza.getPlan() + " ## ", ex);
		}
	}

	// Metodoque transforma las explotaciones de la llamada a WS
	// principal en el formato esperado por el modelo de datos de Agroplus y
	// puebla el objeto Hibernate encargado de la importacion

	public void guardaDistribucionCoste(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza pp,
			final Session session, final Boolean isBatch, final Long idComparativa) throws Exception {

		Set<DistribucionCoste2015> distribucionCoste2015s = new HashSet<DistribucionCoste2015>(0);
		DistribucionCoste2015 dc = new DistribucionCoste2015();

		try {
			es.agroseguro.contratacion.costePoliza.Financiacion financiacion = null;
			if (pp.getCostePoliza() != null) {
				CostePoliza costePoliza = pp.getCostePoliza();
				if (costePoliza != null) {
					CosteGrupoNegocio[] costeGrupoNegocioArray = costePoliza.getCosteGrupoNegocioArray();
					if (costeGrupoNegocioArray != null) {
						financiacion = costePoliza.getFinanciacion();
						for (CosteGrupoNegocio costeGrupoNeg : costeGrupoNegocioArray) {
							if (costeGrupoNeg != null) {
								// Rellena el objeto DistribucionCoste2015
								String codModulo = polizaHbm.getCodmodulo();
								BigDecimal filaComparativa = new BigDecimal(1);
								dc = crearDC2015Unificada(polizaHbm, codModulo, filaComparativa,
										BigDecimal.valueOf(idComparativa), costePoliza, costeGrupoNeg, financiacion);

								// Carga las subvenciones de CCAA y ENESA en el objeto de la distribucion de
								// costes
								dc = cargarSubvencionesDC(dc, costeGrupoNeg);
								// Bonificaciones y recargos
								dc = cargaBonifRecargUnificado(dc, costeGrupoNeg);
								distribucionCoste2015s.add(dc);

								importacionPolizasExtDao.saveOrUpdateEntity(dc, session, isBatch);
							}
						}

					}
				}
			}

		} catch (Exception ex) {
			LOGGER.error("# Ha ocurrido algun error al guardar la distribucion de costes", ex);
		}
	}

	private DistribucionCoste2015 crearDC2015Unificada(Poliza polHbm, String codModulo, BigDecimal filaComparativa,
			BigDecimal idComparativa, CostePoliza costePoliza, CosteGrupoNegocio costeGrupoNegocio,
			Financiacion financiacion) throws DAOException {

		DistribucionCoste2015 dc;
		dc = new DistribucionCoste2015();
		dc.setPoliza(polHbm);
		dc.setCodmodulo(codModulo);
		dc.setFilacomparativa(filaComparativa);
		dc.setIdcomparativa(idComparativa);
		dc.setCostetomador(costeGrupoNegocio.getCosteTomador());
		dc.setPrimacomercial(costeGrupoNegocio.getPrimaComercial());
		dc.setPrimacomercialneta(costeGrupoNegocio.getPrimaComercialNeta());
		dc.setRecargoconsorcio(costeGrupoNegocio.getRecargoConsorcio());
		dc.setReciboprima(costeGrupoNegocio.getReciboPrima());
		dc.setTotalcostetomador(costePoliza.getTotalCosteTomador());
		dc.setGrupoNegocio(costeGrupoNegocio.getGrupoNegocio().charAt(0));
		if (financiacion != null && financiacion.getRecargoAval() != null) {
			dc.setRecargoaval(financiacion.getRecargoAval());
		}
		if (financiacion != null && financiacion.getRecargoFraccionamiento() != null) {
			dc.setRecargofraccionamiento(financiacion.getRecargoFraccionamiento());
		}
		return dc;
	}

	// Metodo que transforma las explotaciones de la llamada a WS
	// principal en el formato esperado por el modelo de datos de Agroplus y
	// puebla el objeto Hibernate encargado de la importacion
	public Explotacion populateExplotaciones(final Poliza polizaHbm, final es.agroseguro.contratacion.Poliza poliza,
			final Session session, final Long lineaId, final Boolean isBatch) throws Exception {

		boolean primera = true;
		Map<String, Explotacion> mapaExplotacion = new HashMap<String, Explotacion>();
		try {
			ObjetosAsegurados objectosAsegurados = poliza.getObjetosAsegurados();
			Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla = getCodConceptoEtiquetaTablaExplotaciones(lineaId,
					session, isBatch);
			Node node = objectosAsegurados.getDomNode().getFirstChild();

			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {

					es.agroseguro.contratacion.explotacion.ExplotacionDocument explotacionDocumento = null;
					try {
						explotacionDocumento = es.agroseguro.contratacion.explotacion.ExplotacionDocument.Factory
								.parse(node);
					} catch (XmlException e) {
						LOGGER.error("Error al parsear una explotacion.", e);

					}
					if (explotacionDocumento != null) {
						es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion = explotacionDocumento
								.getExplotacion();
						Explotacion explotacionBean = new Explotacion();

						if (explotacion.getNumero() != 0) {
							explotacionBean.setNumero(explotacion.getNumero());
						}
						if (explotacion.getRega() != null) {
							explotacionBean.setRega(explotacion.getRega());
						}
						if (explotacion.getSigla() != null) {
							explotacionBean.setSigla(explotacion.getSigla());
						} else {
							explotacionBean.setSigla("");
						}
						if (explotacion.getSubexplotacion() != 0) {
							explotacionBean.setSubexplotacion(explotacion.getSubexplotacion());
						}
						explotacionBean.setEspecie(Long.parseLong(Integer.toString(explotacion.getEspecie())));
						explotacionBean.setRegimen(Long.parseLong(Integer.toString(explotacion.getRegimen())));

						// Coordenadas
						agregarCoordenadas(explotacion, explotacionBean);

						// ambito (Termino)
						Termino termino = importacionPolizasExtDao.obtenerTermino(session, explotacion, isBatch);

						if (termino != null) {
							LOGGER.debug(new StringBuilder("#").append(termino.getId().getSubtermino()).append("#"));
							explotacionBean.setTermino(termino);

							Set<GrupoRaza> grupoRazaSet = new HashSet<GrupoRaza>(0);

							DatosVariables datosVariablesExplotacion = explotacion.getDatosVariables();
							String codigoModulo = poliza.getCobertura().getModulo().trim();

							es.agroseguro.contratacion.explotacion.GrupoRaza[] grupoRazaArray = explotacion
									.getGrupoRazaArray();
							for (es.agroseguro.contratacion.explotacion.GrupoRaza grupoRaza : grupoRazaArray) {

								DatosVariables datosVariablesRaza = grupoRaza.getDatosVariables();
								Long codigoGrupoRaza = new Long(grupoRaza.getGrupoRaza());
								es.agroseguro.contratacion.explotacion.CapitalAsegurado[] capitalAseguradoArray = grupoRaza
										.getCapitalAseguradoArray();

								for (es.agroseguro.contratacion.explotacion.CapitalAsegurado capitalAsegurado : capitalAseguradoArray) {

									DatosVariables datosVariablesCapitalAsegurado = capitalAsegurado
											.getDatosVariables();
									BigDecimal codigoTipoCapitalAsegurado = new BigDecimal(capitalAsegurado.getTipo());
									es.agroseguro.contratacion.explotacion.Animales[] animalesArray = capitalAsegurado
											.getAnimalesArray();

									for (es.agroseguro.contratacion.explotacion.Animales animal : animalesArray) {
										// Declaro la variables necesarias
										GrupoRaza grupoRazaBean = new GrupoRaza();
										PrecioAnimalesModulo precioAnimalesModulo = new PrecioAnimalesModulo();
										Set<PrecioAnimalesModulo> precioAnimalesSet = new HashSet<PrecioAnimalesModulo>();
										Set<DatosVariable> datosVariablesSet = new HashSet<DatosVariable>();

										// Recupero los datos variables del animal
										DatosVariables datosVariablesAnimal = animal.getDatosVariables();

										// Establezco el grupo de raza, precio y codigo de modulo de los animales
										precioAnimalesModulo.setGrupoRaza(grupoRazaBean);
										precioAnimalesModulo.setPrecio(animal.getPrecio());
										precioAnimalesModulo.setCodmodulo(codigoModulo);
										precioAnimalesSet.add(precioAnimalesModulo);

										// Añado todos los datos variables
										datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesAnimal,
												grupoRazaBean, session, auxEtiquetaTabla);
										datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesCapitalAsegurado,
												grupoRazaBean, session, auxEtiquetaTabla);
										datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesRaza,
												grupoRazaBean, session, auxEtiquetaTabla);
										datosVariablesSet = addDatVar(datosVariablesSet, datosVariablesExplotacion,
												grupoRazaBean, session, auxEtiquetaTabla);

										// Relleno todos los campos de grupoRazaBean
										grupoRazaBean.setNumanimales(new Long(animal.getNumero()));
										grupoRazaBean.setCodtipoanimal(new Long(animal.getTipo()));
										grupoRazaBean.setPrecioAnimalesModulos(precioAnimalesSet);
										grupoRazaBean.setExplotacion(explotacionBean);
										grupoRazaBean.setCodtipocapital(codigoTipoCapitalAsegurado);
										grupoRazaBean.setCodgruporaza(codigoGrupoRaza);
										grupoRazaBean.setDatosVariables(datosVariablesSet);

										// Finalmente añado el animal al conjunto de animales
										grupoRazaSet.add(grupoRazaBean);
									}
								}
							}

							explotacionBean.setGrupoRazas(grupoRazaSet);
							explotacionBean.setPoliza(polizaHbm);

							/* ESC-15182 ** MODIF TAM (20.09.2021) ** Inicio */

							Set<ExplotacionCobertura> explotacionCobertura = populateExplotacionesCoberturas(polizaHbm,
									poliza, session, lineaId, isBatch, explotacionBean);

							if (explotacionCobertura != null) {
								explotacionBean.setExplotacionCoberturas(explotacionCobertura);
							}
							/* ESC-15182 ** MODIF TAM (20.09.2021) ** Fin */

							importacionPolizasExtDao.saveOrUpdateEntity(explotacionBean, session, isBatch);

							if (primera) {
								primera = false;
								mapaExplotacion.put("inicial", explotacionBean);
							} else {
								Explotacion expTemp = (Explotacion) mapaExplotacion.get("inicial");
								if (expTemp != null && expTemp.getNumero() != null
										&& explotacionBean.getNumero() != null) {
									if (explotacionBean.getNumero().compareTo(expTemp.getNumero()) == -1) {
										mapaExplotacion.put("inicial", explotacionBean);
									}
								}
							}

						} else { // no existe termino
							LOGGER.debug("# Termino no encontrado #");
						}
					} // fin Node.ELEMENT_NODE
				} // fin cc != null
				node = node.getNextSibling();
			} // FIN explotacion

			if (!primera) {
				return (Explotacion) mapaExplotacion.get("inicial");
			} else {
				return null;
			}
		} catch (Exception ex) {

			LOGGER.debug("# ImportacionPolizasService - Ha ocurrido algun error. Rollback de la transaccion", ex);

			return null;
		}

	}

	/*ESC-15182 ** MODIF TAM (20.09.2021) ** Inicio */

	public Set<ExplotacionCobertura> populateExplotacionesCoberturas(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza, final Session session, final Long lineaId,
			final Boolean isBatch, final Explotacion explotacionBean) throws Exception {

		Long idPoliza = polizaHbm.getIdpoliza();

		Poliza polizaAgr = new Poliza(idPoliza);
		
		if (poliza.getCobertura() != null) {
			polizaAgr.setCodmodulo(poliza.getCobertura().getModulo());
		}

		Node currNode = poliza.getObjetosAsegurados().getDomNode().getFirstChild();

		while (currNode != null) {
			if (currNode.getNodeType() == Node.ELEMENT_NODE) {
				ExplotacionDocument xmlExplotacion = null;
				try {
					xmlExplotacion = ExplotacionDocument.Factory.parse(currNode);

					es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacionXML = xmlExplotacion
							.getExplotacion();

					Set<ExplotacionCobertura> coberturas = explotacionXML.getDatosVariables() != null
							? getExplotacionesCoberturas(explotacionXML.getDatosVariables().getRiesgCbtoElegArray(),
									polizaHbm, lineaId)
							: null;

					if (coberturas != null) {
						for (ExplotacionCobertura exp : coberturas) {
							exp.setExplotacion(explotacionBean);
						}
					}
					return coberturas;

				} catch (XmlException e) {
					LOGGER.debug(
							"# ImportacionPolizasService (pupulateExplotacionesCoberturas) - Error al parsear una explotacion.",
							e);
				}
			}
		}
		return null;

	}

	public Set<ExplotacionCobertura> getExplotacionesCoberturas(
			es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido riesgos[], final Poliza polizaHbm,
			final Long lineaId) throws DAOException {
		Set<ExplotacionCobertura> coberturas = null;

		String modulo = polizaHbm.getCodmodulo();

		if (null != riesgos && riesgos.length > 0) {
			coberturas = new HashSet<ExplotacionCobertura>(riesgos.length - 1);
			for (int i = 0; i < riesgos.length; i++) {
				ExplotacionCobertura cob = new ExplotacionCobertura();
				cob.setRiesgoCubierto((short) riesgos[i].getCodRCub());
				cob.setCpm((short) riesgos[i].getCPMod());
				cob.setElegida(riesgos[i].getValor().toCharArray()[0]);
				cob.setElegible('S');
				/* obtenemos el valor de fila */
				short fila = importacionPolizasExtDao.getFilaExplotacionCobertura(lineaId, modulo,
						riesgos[i].getCPMod(), riesgos[i].getCodRCub());

				/* Obtenemos la descripcion de cpm */
				String cpmDescripcion = importacionPolizasExtDao.getDescripcionConceptoPpalMod(riesgos[i].getCPMod());

				/* Obtenemos la descripcion de RC */
				String rcDescripcion = importacionPolizasExtDao.getDescripcionRiesgoCubierto(lineaId, modulo,
						riesgos[i].getCodRCub());
				cob.setFila(fila);

				if (null == cpmDescripcion)
					cpmDescripcion = new String("");
				cob.setCpmDescripcion(cpmDescripcion);

				if (null == rcDescripcion)
					rcDescripcion = new String("");
				cob.setRcDescripcion(rcDescripcion);

				cob.setCodmodulo(modulo);

				coberturas.add(cob);
			}
		}
		return coberturas;
	}

	/* ESC-15182 ** MODIF TAM (20.09.2021) ** Fin */

	public void agregarCoordenadas(es.agroseguro.contratacion.explotacion.ExplotacionDocument.Explotacion explotacion,
			Explotacion explotacionBean) {
		Coordenadas coord = explotacion.getCoordenadas();
		if (coord != null) {
			if (coord.getLatitud() != 0) {
				explotacionBean.setLatitud(coord.getLatitud());
			}
			if (coord.getLongitud() != 0) {
				explotacionBean.setLongitud(coord.getLongitud());
			}
		}
	}

	public Set<DatosVariable> addDatVar(final Set<DatosVariable> datosVariables, final DatosVariables datt,
			final GrupoRaza grupRBean, Session sessionWW, Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla) {
		NodeList childList2 = null;
		try {
			if (datt != null) {
				childList2 = datt.getDomNode().getChildNodes();
				for (int j = 0; j < childList2.getLength(); j++) {
					Node node2 = childList2.item(j);
					String datNombre = node2.getLocalName();
					String strCodConcepto = getCodconcepto(datNombre, auxEtiquetaTabla);
					if (strCodConcepto != null && !strCodConcepto.equals("")) {
						Integer codConcepto = Integer.parseInt(strCodConcepto);
						if (codConcepto != null && codConcepto.compareTo(0) != 0) {
							DatosVariable datBean = new DatosVariable();
							datBean.setCodconcepto(codConcepto);
							String val = node2.getAttributes().getNamedItem("valor").getNodeValue();
							datBean.setValor(val);
							datBean.setGrupoRaza(grupRBean);
							if (!datosVariables.contains(datBean))
								datosVariables.add(datBean);
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("# Error al procesar el dato variable:", ex);
			return datosVariables;
		}
		return datosVariables;
	}

	public String getCodconcepto(final String nombre, final Map<String, RelacionEtiquetaTabla> auxEtiquetaTabla) {
		String codConcepto = "";
		RelacionEtiquetaTabla rel = auxEtiquetaTabla.get(nombre);
		if (rel != null) {
			codConcepto = rel.getcodConcepto();
		}
		return codConcepto;
	}

	public Map<String, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(final Long lineaseguroid,
			final Session session, final Boolean isBatch) {

		List<Object> busqueda = importacionPolizasExtDao.getCodConceptoEtiquetaTablaExplotacionesBBDD(lineaseguroid,
				session, isBatch);
		// Recorro la lista y voy rellenando el mapa
		Map<String, RelacionEtiquetaTabla> resultado = new HashMap<String, RelacionEtiquetaTabla>();
		for (Object elem : busqueda) {
			Object[] elemento = (Object[]) elem;
			RelacionEtiquetaTabla ret = new RelacionEtiquetaTabla(nullToString(elemento[1]), // etiqueta
					nullToString(elemento[3]), // tabla
					nullToString(elemento[0])); // codConcepto
			resultado.put(nullToString(elemento[2]), ret);
		}
		return resultado;
	}

	public String nullToString(Object cad) {
		try {
			if (cad == null || cad.equals("null"))
				cad = "";
			return cad.toString();
		} catch (Exception e) {
			return "";
		}

	}

	public List<ComparativaPoliza> getComparativasRiesgCubEleg(final Poliza polizaHbm,
			final es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] riesgCbtoElegArray,
			final Session session, final Long secuencia, Boolean isBatch) throws DAOException {

		LOGGER.debug("ImportacionPolizasService - getComparativasRiesgCubEleg - init");
		
		LOGGER.debug("Array riesgos cubiertos total: " + (riesgCbtoElegArray == null ? 0 : riesgCbtoElegArray.length));
		
		List<ComparativaPoliza> comparativasRiesgCubEleg;
		List<RiesgoCubiertoModulo> rCubMobList;
		ComparativaPoliza comparativaPolHbm;
		BigDecimal codConcepto;
		BigDecimal valor;
		String descValor;
		BigDecimal filaComparativa;

		final BigDecimal[] lineasEspeciales = new BigDecimal[] { BigDecimal.valueOf(301) };

		comparativasRiesgCubEleg = new ArrayList<ComparativaPoliza>();

		rCubMobList = importacionPolizasExtDao.getListaRiesgoCubiertoModulo(polizaHbm, session, isBatch);
		
		LOGGER.debug("ListaRiesgoCubiertoModulo total: " + rCubMobList.size());

		for (RiesgoCubiertoModulo rcmodHbm : rCubMobList) {

			if (riesgCbtoElegArray != null && riesgCbtoElegArray.length > 0) {

				codConcepto = BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "N";
				filaComparativa = BigDecimal.valueOf(0);

				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : riesgCbtoElegArray) {

					if (rcmodHbm.getRiesgoCubierto().getId().getCodriesgocubierto()
							.equals(BigDecimal.valueOf(rce.getCodRCub()))
							&& rcmodHbm.getConceptoPpalModulo().getCodconceptoppalmod()
									.equals(BigDecimal.valueOf(rce.getCPMod()))) {
						descValor = rce.getValor();
						valor = new BigDecimal(
								"S".equals(rce.getValor()) ? Constants.RIESGO_ELEGIDO_SI : Constants.RIESGO_ELEGIDO_NO);
						if (!Arrays.asList(lineasEspeciales).contains(polizaHbm.getLinea().getCodlinea())) {
							filaComparativa = "S".equals(rce.getValor()) ? BigDecimal.valueOf(1)
									: BigDecimal.valueOf(2);
						}
						break;
					}
				}
			} else {

				codConcepto = BigDecimal.valueOf(0);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "";
				filaComparativa = Arrays.asList(lineasEspeciales).contains(polizaHbm.getLinea().getCodlinea())
						? BigDecimal.valueOf(0)
						: BigDecimal.valueOf(2);
			}

			comparativaPolHbm = generarComparativaPoliza(polizaHbm,
					rcmodHbm.getConceptoPpalModulo().getCodconceptoppalmod(),
					rcmodHbm.getRiesgoCubierto().getId().getCodriesgocubierto(), codConcepto,
					rcmodHbm.getId().getFilamodulo(), valor, descValor, filaComparativa, secuencia);
			
			importacionPolizasExtDao.saveOrUpdateEntity(comparativaPolHbm, session, isBatch);
			comparativasRiesgCubEleg.add(comparativaPolHbm);
		}

		// Ajuste de fila comparativa para lineas especiales
		if (!comparativasRiesgCubEleg.isEmpty()
				&& Arrays.asList(lineasEspeciales).contains(polizaHbm.getLinea().getCodlinea())) {

			// Ordenamos por fila del modulo
			Collections.sort(comparativasRiesgCubEleg, new Comparator<ComparativaPoliza>() {
				@Override
				public int compare(final ComparativaPoliza arg0, final ComparativaPoliza arg1) {
					return arg0.getId().getFilamodulo().compareTo(arg1.getId().getFilamodulo());
				}
			});

			if (BigDecimal.valueOf(301).equals(polizaHbm.getLinea().getCodlinea())) {
				// LS301 tiene fila 7 y fila 12
				// Filacomp 1: ambos elegidos
				// Filacomp 2: elegida fila 7
				// Filacomp 3: elegida fila 12
				// Filacomp 4: ninguno elegido
				final ComparativaPoliza compFila7 = comparativasRiesgCubEleg.get(0);
				final ComparativaPoliza compFila12 = comparativasRiesgCubEleg.get(1);
				if (compFila7.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(4);
					compFila7.getId().setCodconcepto(BigDecimal.valueOf(0));
					compFila12.getId().setCodconcepto(BigDecimal.valueOf(0));
				} else if (compFila7.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(3);
				} else if (compFila7.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(2);
				} else if (compFila7.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12.getId().getCodvalor().equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(1);
				} else {
					filaComparativa = BigDecimal.valueOf(0);
				}
				compFila7.getId().setFilacomparativa(filaComparativa);
				compFila12.getId().setFilacomparativa(filaComparativa);
			}
		}
		
		LOGGER.debug("Se han anyadido " + comparativasRiesgCubEleg + " comparativas") ;
		LOGGER.debug("ImportacionPolizasService - getComparativasRiesgCubEleg - end");

		return comparativasRiesgCubEleg;
	}

	/* Pet. 63482 ** MODIF TAM (14.06.2021) * Inicio */
	public List<ComparativaPoliza> getComparativasRiesgCubElegGanado(Poliza polizaHbm, DatosVariables dvs,
			Session session, Long secuencia, Boolean isBatch, final Long lineaseguroid) throws DAOException {

		LOGGER.debug("Dentro de getComparativasRiesgCubElegGanado:" + isBatch);
		List<ComparativaPoliza> comparativasRiesgCubEleg = new ArrayList<ComparativaPoliza>();
		// Obtiene una lista ComparativaPoliza a partir de la situacion actualizada de la poliza

		// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA (31) Y A
		// LA UBICACION DE COBERTURAS (18)
		Filter oiFilter = new Filter() {
			@Override
			public Criteria getCriteria(final Session sesion) {
				Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
				criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
				criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA));
				criteria.add(Restrictions.in("id.codubicacion",
						new Object[] { OrganizadorInfoConstants.UBICACION_COBERTURA_DV }));
				return criteria;
			}
		};

		List<OrganizadorInformacion> oiList = importacionPolizasExtDao.obtenerlistOrgInformacion(oiFilter, isBatch,
				session);

		// RIESGO CUBIERTO ELEGIDO
		try {
			LOGGER.debug("Valor de dvs:" + dvs);
			if (dvs != null) {
				LOGGER.debug("Entramos, se han recuperado Datos Variables ");
				if (dvs.getRiesgCbtoElegArray() != null && dvs.getRiesgCbtoElegArray().length > 0) {
					for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : dvs
							.getRiesgCbtoElegArray()) {

						BigDecimal sFila = importacionPolizasExtDao.getfilaRiesgoCubModulo(
								polizaHbm.getLinea().getLineaseguroid(), polizaHbm.getCodmodulo(),
								new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub()));

						ComparativaPoliza comp = generarComparativaPolizaGanado(new BigDecimal(rce.getCPMod()),
								new BigDecimal(rce.getCodRCub()), ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO,
								sFila, rce.getValor(),
								"S".equals(rce.getValor()) ? new BigDecimal(Constants.RIESGO_ELEGIDO_SI)
										: new BigDecimal(Constants.RIESGO_ELEGIDO_NO),
								polizaHbm, secuencia);

						importacionPolizasExtDao.saveOrUpdateEntity(comp, session, isBatch);
						comparativasRiesgCubEleg.add(comp);

					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener los riesgos cubiertos elegibles de la cobertura.", e);
		}

		try {

			for (OrganizadorInformacion oi : oiList) {
				Method method = dvs.getClass().getMethod("get" + oi.getDiccionarioDatos().getEtiquetaxml() + "Array");
				Class<?> dvClass = dvs.getClass().getMethod("addNew" + oi.getDiccionarioDatos().getEtiquetaxml())
						.getReturnType();
				Object[] result = (Object[]) method.invoke(dvs);
				for (Object obj : result) {
					BigDecimal valor = new BigDecimal("" + dvClass.getMethod("getValor").invoke(obj));
					BigDecimal cpMod = new BigDecimal("" + dvClass.getMethod("getCPMod").invoke(obj));
					BigDecimal rCub = new BigDecimal("" + dvClass.getMethod("getCodRCub").invoke(obj));
					String des = "";
					// GARANTIZADO
					if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO))) {
						des = importacionPolizasExtDao.getDesGarantizado(valor);
					} // CALCULO INDEMNIZACION
					else if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION))) {
						des = importacionPolizasExtDao.getDesCalcIndem(valor);
					} // % FRANQUICIA
					else if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA))) {
						des = importacionPolizasExtDao.getDesPctFranquicia(valor);
					} // MINIMO INDEMNIZABLE
					else if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE))) {
						des = importacionPolizasExtDao.getDesMinIndem(valor);
					} // TIPO FRANQUICIA
					else if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA))) {
						des = importacionPolizasExtDao.getDesTipoFranqIndem(valor.toString());
					} // % CAPITAL ASEGURADO
					else if (oi.getDiccionarioDatos().getCodconcepto()
							.equals(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO))) {
						des = importacionPolizasExtDao.getDesCapitalAseg(valor);
					}

					BigDecimal sFila = importacionPolizasExtDao.getfilaRiesgoCubModulo(
							polizaHbm.getLinea().getLineaseguroid(), polizaHbm.getCodmodulo(), cpMod, rCub);

					ComparativaPoliza comp = (generarComparativaPolizaGanado(cpMod, rCub,
							oi.getId().getCodconcepto().intValue(), sFila, des, valor, polizaHbm, secuencia));

					importacionPolizasExtDao.saveOrUpdateEntity(comp, session, isBatch);
					comparativasRiesgCubEleg.add(comp);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener los datos variables de la cobertura.", e);
		}

		return comparativasRiesgCubEleg;
	}

	public void borrarPoliza(Poliza polizaHbm, Boolean isBatch, final Session session) throws DAOException {
		try {

			LOGGER.debug("ImportacionPolizasService - BorrarPoliza [INIT]");

			importacionPolizasExtDao.deleteEntity(polizaHbm, session, isBatch);

		} catch (Exception e) {
			LOGGER.error("Error al obtener los datos variables de la cobertura.", e);
		}

	}

	/* Pet. 63482 ** MODIF TAM (14.06.2021) * Fin */

	public static class RelacionEtiquetaTabla {

		private String etiqueta;
		private String tabla;
		private String codConcepto;

		public RelacionEtiquetaTabla() {
		}

		public RelacionEtiquetaTabla(String etiqueta, String tabla, String codConcepto) {
			this.etiqueta = etiqueta;
			this.tabla = tabla;
			this.codConcepto = codConcepto;
		}

		public String getEtiqueta() {
			return etiqueta;
		}

		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}

		public String getTabla() {
			return tabla;
		}

		public void setTabla(String tabla) {
			this.tabla = tabla;
		}

		public String getcodConcepto() {
			return codConcepto;
		}

		public void setCodConcepto(String codConcepto) {
			this.codConcepto = codConcepto;
		}

	}
	
	@Override
	public List<ComparativaPoliza> getComparativas(Poliza polizaHbm, Session session, Long secuencia, final es.agroseguro.contratacion.Cobertura cobertura,
			boolean isBatch) {
		
		LOGGER.debug("ImportacionPolizasService - getComparativas - init");

		List<ComparativaPoliza> comparativasPolHbm = new ArrayList<ComparativaPoliza>();
		ComparativaPoliza comparativaPolHbm;
		
		es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = cobertura.getDatosVariables();
		
		// GARANTIZADO
		if (datosVariables != null && datosVariables.getGarantArray() != null
				&& datosVariables.getGarantArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.Garantizado g : datosVariables.getGarantArray()) {
				String descValor = importacionPolizasExtDao.getDescValorCodGarantizado(session, g.getValor(),
						colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(g.getCPMod()),
						BigDecimal.valueOf(g.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(g.getCPMod()),
								BigDecimal.valueOf(g.getCodRCub()), session, colFilaModulo, isBatch),
						BigDecimal.valueOf(g.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}

		// CALCULO INDEMNIZACION
		if (datosVariables != null && datosVariables.getCalcIndemArray() != null
				&& datosVariables.getCalcIndemArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c : datosVariables
					.getCalcIndemArray()) {
				String descValor = importacionPolizasExtDao.getDescValorCalculoIndemnizacion(session, c.getValor(),
						colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(c.getCPMod()),
						BigDecimal.valueOf(c.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(c.getCPMod()),
								BigDecimal.valueOf(c.getCodRCub()), session, colFilaModulo, isBatch),
						BigDecimal.valueOf(c.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}

		// % FRANQUICIA
		if (datosVariables != null && datosVariables.getFranqArray() != null
				&& datosVariables.getFranqArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia pf : datosVariables
					.getFranqArray()) {
				String descValor = importacionPolizasExtDao.getDescValorPctFranquiciaElegible(session,
						pf.getValor(), colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pf.getCPMod()),
						BigDecimal.valueOf(pf.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(pf.getCPMod()),
								BigDecimal.valueOf(pf.getCodRCub()), session, colFilaModulo, isBatch),
						BigDecimal.valueOf(pf.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}

		// MINIMO INDEMNIZABLE
		if (datosVariables != null && datosVariables.getMinIndemArray() != null
				&& datosVariables.getMinIndemArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : datosVariables
					.getMinIndemArray()) {
				String descValor = importacionPolizasExtDao.getDescValorMinimoIndemnizableElegible(session,
						pmi.getValor(), colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pmi.getCPMod()),
						BigDecimal.valueOf(pmi.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(pmi.getCPMod()),
								BigDecimal.valueOf(pmi.getCodRCub()), session, colFilaModulo, isBatch),
						BigDecimal.valueOf(pmi.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}

		// TIPO FRANQUICIA
		if (datosVariables != null && datosVariables.getTipFranqArray() != null
				&& datosVariables.getTipFranqArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tf : datosVariables.getTipFranqArray()) {
				String descValor = importacionPolizasExtDao.getDescValoTipoFranquicia(session, tf.getValor(),
						colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(tf.getCPMod()),
						BigDecimal.valueOf(tf.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(tf.getCPMod()),
								BigDecimal.valueOf(tf.getCodRCub()), session, colFilaModulo, isBatch),
						new BigDecimal(tf.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}
		// % CAPITAL ASEGURADO
		if (datosVariables != null && datosVariables.getCapAsegArray() != null
				&& datosVariables.getCapAsegArray().length > 0) {
			for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : datosVariables
					.getCapAsegArray()) {
				String descValor = importacionPolizasExtDao.getDescValoCapitalAseguradoElegible(session,
						pca.getValor(), colDescripciones, isBatch);
				comparativaPolHbm = generarComparativaPoliza(polizaHbm, BigDecimal.valueOf(pca.getCPMod()),
						BigDecimal.valueOf(pca.getCodRCub()),
						BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO),
						(BigDecimal) importacionPolizasExtDao.getFilaModulo(polizaHbm.getLinea().getLineaseguroid(),
								polizaHbm.getCodmodulo(), BigDecimal.valueOf(pca.getCPMod()),
								BigDecimal.valueOf(pca.getCodRCub()), session, colFilaModulo, isBatch),
						BigDecimal.valueOf(pca.getValor()), descValor, BigDecimal.valueOf(1), secuencia);

				comparativasPolHbm.add(comparativaPolHbm);
			}
		}
					
		LOGGER.debug("ImportacionPolizasService - getComparativas - end");

		return comparativasPolHbm;
	}

	public void setImportacionPolizasExtDao(IImportacionPolizasExtDao importacionPolizasExtDao) {
		this.importacionPolizasExtDao = importacionPolizasExtDao;
	}
	
	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}

	public void setDistribucionCosteDAO(IDistribucionCosteDAO distribucionCosteDAO) {
		this.distribucionCosteDAO = distribucionCosteDAO;
	}
	
	public void setPolizasPctComisionesManager(IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}
}