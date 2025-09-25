package com.rsi.agp.batch.importacion;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public class BBDDPolizaUtil {

	private static final Logger logger = Logger.getLogger(BBDDPolizaUtil.class);

	private BBDDPolizaUtil() {
	}

	// Puebla y valida el contenido del objeto Hibernate encargado de la
	// importacion de la poliza principal
	
	protected static void populateAndValidatePolizaPpal(final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session) throws Exception {
		logger.info("populateAndValidatePolizaPpal - Agricola");
		
		
		// Datos relativos al envio
		polizaHbm.setIdenvio(BigDecimal.valueOf(idEnvio.longValue()));
		/* ESC-10886 ** MODIF TAM (28.09.2020) ** Pasamos la fecha de la firma por indicaci�n de RGA */
		///polizaHbm.setFechaenvio(getFechaEnvio(idEnvio, session));
				
		Date fechaFirma = poliza.getFechaFirmaSeguro().getTime();
		polizaHbm.setFechaenvio(fechaFirma);
		logger.info("**@@** Valor de fechaFirma:"+fechaFirma.toString());
		logger.info("Asigna datos relativos al envio");
		
		// Linea de Seguro
		polizaHbm.setLinea(getLineaSeguroBBDD(BigDecimal.valueOf(poliza.getLinea()), BigDecimal.valueOf(poliza.getPlan()), session));
		logger.info("... linea");
		
		// E-S Mediadora - obtenida del campo 'codigoInterno de la situacion actualizada'
		SubentidadMediadora sm = getESMediadora(poliza.getEntidad());

		// Colectivo
		Colectivo colectivoHbm = getColectivoBBDD(poliza.getColectivo(), session, sm);
		polizaHbm.setColectivo(colectivoHbm);
		logger.info("... colectivo");
		
		// Asegurado
		Asegurado aseguradoHbm = getAseguradoBBDD(poliza.getAsegurado(), colectivoHbm.getTomador().getEntidad().getCodentidad(), session, sm);
		polizaHbm.setAsegurado(aseguradoHbm);
		polizaHbm.setDiscriminante(aseguradoHbm.getDiscriminante());
		logger.info("... asegurado");
		
		// Usuario
		polizaHbm.setUsuario(getUsuarioPolizaBBDD(session));
		logger.info("... usuario");
		
		// Estado Poliza
		polizaHbm.setEstadoPoliza(BBDDEstadosUtil.getEstadoPolizaBBDD(session));
		logger.info("...estado");
		
		// Datos de pago
		polizaHbm.setEstadoPagoAgp(BBDDEstadosUtil.getEstadoPagoBBDD(session));
		logger.info("...estado de pago");
		
		// Datos fijos o de importacion directa desde la situacion actual
		polizaHbm.setExterna(ImportacionConstants.POLIZA_EXTERNA);
		polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_PRINCIPAL);
		polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
		polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
		polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
		polizaHbm.setPacCargada(Constants.CHARACTER_N);
		polizaHbm.setReferencia(poliza.getReferencia());
		polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
		polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
		polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId()
				.getCodoficina().toString());
		logger.info("...datos fijos y guardamos la poliza");
		session.saveOrUpdate(polizaHbm);
		logger.info("Poliza guardada");
		try {
			// Subvenciones
			BBDDSubvencionesUtil.populateSubvenciones(polizaHbm,
					poliza.getSubvencionesDeclaradas(), session);
			logger.info("Asignamos subvenciones");
			// Coberturas
			BBDDCoberturasUtil.populateCoberturas(polizaHbm,
					poliza.getCobertura(), session, true);
			logger.info("...coberturas");
			// Parcelas
			BBDDParcelasUtil.populateParcelasPpal(polizaHbm,
					poliza.getObjetosAsegurados(),poliza.getCobertura(), session);
			
			BBDDParcelasUtil.asignarParcelasInstalaciones(polizaHbm
					.getParcelas());
			logger.info("...parcelas");
			// Sumatorio de superficies parcela
			BigDecimal totalsuperficie = BigDecimal.valueOf(0);
			Iterator<Parcela> itParcelas = polizaHbm.getParcelas().iterator();
			while (itParcelas.hasNext()) {
				Parcela parcela = itParcelas.next();
				Iterator<CapitalAsegurado> itCapitales = parcela
						.getCapitalAsegurados().iterator();
				while (itCapitales.hasNext()) {
					CapitalAsegurado capitalAseg = itCapitales.next();
					totalsuperficie = totalsuperficie.add(capitalAseg
							.getSuperficie());
				}
			}
			polizaHbm.setTotalsuperficie(totalsuperficie);
			logger.info("...superficies");
			
			BBDDImportesUtil.populateCostes(polizaHbm, poliza.getCostePoliza(), session);
			logger.info("...costes1");
			BBDDImportesUtil.populatePagos(polizaHbm, poliza.getPago(),
					poliza.getCuentaCobroSiniestros() == null ? "" : poliza.getCuentaCobroSiniestros().getIban(),
					session);
			logger.info("...pagos");
			BBDDImportesUtil.populateFraccionamiento(polizaHbm, poliza.getPago(), session);
			logger.info("...fraccionamiento");
			es.agroseguro.iTipos.Gastos gastos = poliza.getEntidad().getGastosArray(0);
			BBDDImportesUtil.populateComisiones(polizaHbm, poliza.getPlan(), poliza.getLinea(),
					poliza.getPago().getFecha(), gastos.getAdministracion(), gastos.getAdquisicion(),
					gastos.getComisionMediador(), sm, session, new Character('1'));
			
			logger.info("...comisiones");
						
			// Socios
			BBDDSociosUtil.populateSocios(polizaHbm,
					poliza.getRelacionSocios(), session);
			logger.info("...socios");
			// Estados pago y estados poliza
			logger.debug ("Antes de actualizarHistEstado");
			if (poliza.getPago().getFecha() != null) {
				logger.debug("Entramos con Fecha de Pago");
				BBDDEstadosUtil.actualizaHistEstado(polizaHbm, poliza.getPago().getFecha().getTime(), session);
				
			}else {
				logger.debug("Entramos con Fecha FirmaSeguro");
				BBDDEstadosUtil.actualizaHistEstado(polizaHbm, poliza.getFechaFirmaSeguro().getTime(), session);
			}
			logger.info("Estado en el Hco. actualizado");
			// XML de situacion actual
			guardaSituacionActual(polizaHbm, poliza.xmlText(), session);
			logger.info("Situacion actual guardada");
			
			session.saveOrUpdate(polizaHbm);
			logger.info("Poliza guardada");
		} catch (Exception ex) {
			session.delete(polizaHbm);
			logger.error("Error en BBDDPolizaUtil.populateAndValidatePolizaPpal. ",ex);
			throw ex;
		}
	}

	private static SubentidadMediadora getESMediadora(es.agroseguro.contratacion.Entidad entidad) {
		SubentidadMediadora sm = null;
		// Obtener el codigo interno
		String codigoInterno = entidad.getCodigoInterno();
		
		if (!StringUtils.isNullOrEmpty(codigoInterno)) {
			// Entidad mediadora
			BigDecimal entMed = toBigDecimal (codigoInterno.trim().substring(0, 4));
			// Subentidad mediadora
			BigDecimal subEntMed = toBigDecimal (codigoInterno.trim().substring(4));
			
			logger.debug("entMed: " + entMed + " - subEntMed:" + subEntMed);
			
			SubentidadMediadoraId smId = new SubentidadMediadoraId(entMed, subEntMed);
			sm = new SubentidadMediadora();
			sm.setId(smId);
		}
		return sm;
	}

	// Puebla y valida el contenido del objeto Hibernate encargado de la
	// importacion de la poliza complementaria
	protected static void populateAndValidatePolizaComp(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.Poliza poliza,
			final Long idEnvio, final Session session) throws Exception {
		logger.info("populateAndValidatePolizaComp");
		// Datos relativos al envio
		polizaHbm.setIdenvio(BigDecimal.valueOf(idEnvio.longValue()));
		
		
		/* ESC-12354 y ESC-13277 ** MODIF TAM (06.04.2020) ** Inicio - Pasamos la fecha de la firma  */
		/// polizaHbm.setFechaenvio(getFechaEnvio(idEnvio, session));		
		Date fechaFirma = poliza.getFechaFirmaSeguro().getTime();
		polizaHbm.setFechaenvio(fechaFirma);
		logger.info("**@@** Valor de fechaFirma en Cpl:"+fechaFirma.toString());
		/* ESC-12354 ** MODIF TAM (28.09.2020) ** Fin  */
		
		logger.info("Asigna datos relativos al envio");
		// Linea de Seguro
		polizaHbm.setLinea(getLineaSeguroBBDD(
				BigDecimal.valueOf(poliza.getLinea()),
				BigDecimal.valueOf(poliza.getPlan()), session));
		logger.info("...linea");
		// Poliza principal
		Poliza polizaPpal = getPolizaPpalBBDD(
				polizaHbm.getLinea().getCodplan(), polizaHbm.getLinea()
						.getCodlinea(), poliza.getReferencia(), poliza
						.getCobertura().getModulo().trim(), session);
		if (polizaPpal == null) {
			throw new Exception(
					"No se encuentra la poliza Complementaria. Revise los datos: plan "
							+ polizaHbm.getLinea().getCodplan() + ", linea "
							+ polizaHbm.getLinea().getCodlinea()
							+ ", referencia " + poliza.getReferencia()
							+ ", modulo complementario "
							+ poliza.getCobertura().getModulo().trim());
		}
		polizaHbm.setPolizaPpal(polizaPpal);
		polizaHbm.setClase(polizaPpal.getClase());
		logger.info("...poliza Complementaria");
		
		// E-S Mediadora - obtenida del campo 'codigoInterno de la situacion actualizada'
		SubentidadMediadora sm = getESMediadora(poliza.getEntidad());
		logger.info("...ES Mediadora");
		
		// Colectivo
		Colectivo colectivoHbm = getColectivoBBDD(poliza.getColectivo(), session, sm);
		polizaHbm.setColectivo(colectivoHbm);
		logger.info("...colectivo");
		
		// Asegurado
		Asegurado aseguradoHbm = getAseguradoBBDD(poliza.getAsegurado(),
				colectivoHbm.getTomador().getEntidad().getCodentidad(), session, sm);
		logger.info("...asegurado");
		
		polizaHbm.setAsegurado(aseguradoHbm);
		polizaHbm.setDiscriminante(aseguradoHbm.getDiscriminante());
		
		// Usuario
		polizaHbm.setUsuario(getUsuarioPolizaBBDD(session));
		logger.info("...usuario");
		
		// Estado Poliza
		polizaHbm.setEstadoPoliza(BBDDEstadosUtil.getEstadoPolizaBBDD(session));
		logger.info("...estado de la poliza");
		
		// Datos de pago
		polizaHbm.setEstadoPagoAgp(BBDDEstadosUtil.getEstadoPagoBBDD(session));
		logger.info("...estado de pago");
		
		// Datos fijos o de importacion directa desde la situacion actual
		polizaHbm.setExterna(ImportacionConstants.POLIZA_EXTERNA);
		polizaHbm.setTipoReferencia(Constants.MODULO_POLIZA_COMPLEMENTARIO);
		polizaHbm.setTienesiniestros(Constants.CHARACTER_N);
		polizaHbm.setTieneanexomp(Constants.CHARACTER_N);
		polizaHbm.setTieneanexorc(Constants.CHARACTER_N);
		polizaHbm.setPacCargada(Constants.CHARACTER_N);
		polizaHbm.setReferencia(poliza.getReferencia());
		polizaHbm.setDc(BigDecimal.valueOf(poliza.getDigitoControl()));
		polizaHbm.setCodmodulo(poliza.getCobertura().getModulo().trim());
		polizaHbm.setOficina(polizaHbm.getUsuario().getOficina().getId()
				.getCodoficina().toString());
		logger.info("...datos fijos");
		session.saveOrUpdate(polizaHbm);
		logger.info("Poliza guardada");
		try {

			// Coberturas
			BBDDCoberturasUtil.populateCoberturas(polizaHbm,
					poliza.getCobertura(), session, false);
			logger.info("...coberturas");
			// Parcelas
			BBDDParcelasUtil.populateParcelasComp(polizaHbm,
					poliza.getObjetosAsegurados(), session);
			BBDDParcelasUtil.asignarParcelasInstalaciones(polizaHbm
					.getParcelas());
			logger.info("...parcelas");
			// Sumatorio de superficies parcela
			BigDecimal totalsuperficie = BigDecimal.valueOf(0);
			Iterator<Parcela> itParcelas = polizaHbm.getParcelas().iterator();
			while (itParcelas.hasNext()) {
				Parcela parcela = itParcelas.next();
				Iterator<CapitalAsegurado> itCapitales = parcela
						.getCapitalAsegurados().iterator();
				while (itCapitales.hasNext()) {
					CapitalAsegurado capitalAseg = itCapitales.next();
					totalsuperficie = totalsuperficie.add(capitalAseg
							.getSuperficie());
				}
			}
			polizaHbm.setTotalsuperficie(totalsuperficie);
			logger.info("...superficie");
			
			String cccSiniestros = null;
			Set<PagoPoliza> pagoPolizas = polizaPpal.getPagoPolizas();
			if (pagoPolizas != null && !pagoPolizas.isEmpty()) {
				PagoPoliza pp = pagoPolizas.iterator().next();
				cccSiniestros = pp.getIban2() + pp.getCccbanco2();
			}
			
			BBDDImportesUtil.populateCostes(polizaHbm, poliza.getCostePoliza(), session);
			logger.info("...distribucion de costes (populateCostes1)");
			BBDDImportesUtil.populatePagos(polizaHbm, poliza.getPago(), cccSiniestros, session);
			logger.info("...pagos");
				
			es.agroseguro.iTipos.Gastos gastos = poliza.getEntidad().getGastosArray(0);
			BBDDImportesUtil.populateComisiones(polizaHbm, poliza.getPlan(), poliza.getLinea(),
					poliza.getPago().getFecha(), gastos.getAdministracion(), gastos.getAdquisicion(),
					gastos.getComisionMediador(), sm, session, new Character('1'));
			
			// Estados pago y estados poliza
			BBDDEstadosUtil.actualizaHistEstado(polizaHbm,
					poliza.getPago().getFecha() == null ? new Date() : poliza.getPago().getFecha().getTime(), session);
			logger.info("Actualizado estado en Hco.");
			// XML de situacion actual
			guardaSituacionActual(polizaHbm, poliza.xmlText(), session);
			logger.info("Situacion actual de la poliza guardada");
			
			//P0063699 - Suplementos de sobreprecio por contrataci�n de complementarias
			actualizaSbp(polizaPpal.getIdpoliza(), session);
			
		} catch (Exception ex) {
			session.delete(polizaHbm);
			throw ex;
		}
	}
	
	//P0063699 - Suplementos de sobreprecio por contrataci�n de complementarias
	private static void actualizaSbp(final Long idPoliza, final Session session) throws DAOException {
		Long idSbp = null;
		try {
			logger.debug("init - hayPolizaSbp: idpolizaPpal= " + idPoliza);
			PolizaSbp polizaSbp = (PolizaSbp) session.createCriteria(PolizaSbp.class)
					.createAlias("polizaPpal", "polizaPpal").createAlias("estadoPlzSbp", "estadoPlzSbp")
					.createAlias("tipoEnvio", "tipoEnvio").add(Restrictions.eq("tipoEnvio.id", BigDecimal.ONE))
					.add(Restrictions.eq("polizaPpal.idpoliza", idPoliza))
					.add(Restrictions.eq("estadoPlzSbp.idestado", ConstantsSbp.ESTADO_ENVIADA_CORRECTA)).uniqueResult();
			if (polizaSbp != null) {
				idSbp = polizaSbp.getId();
				if (idSbp != null) {
					Query sql = session
							.createSQLQuery("UPDATE o02agpe0.TB_SBP_POLIZAS SET GEN_SPL_CPL = 'S' WHERE ID = " + idSbp);
					sql.executeUpdate();
				}
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error en el acceso a la BBDD: " + ex.getMessage());
			throw new DAOException("Se ha producido un error en el acceso a la BBDD", ex);
		}
	}	
	
	// Metodo que valida que el colectivo recibido en la situacion actual
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	private static Colectivo getColectivoBBDD(final es.agroseguro.contratacion.Colectivo colectivo,
			final Session session, final SubentidadMediadora sm) throws Exception {
		
		logger.debug("Dentro de getColectivoBBDD");

		Colectivo colectivoHbm;
		String referencia;
		String dc;
		String msgError = null;		

		referencia = colectivo.getReferencia();
		dc = String.valueOf(colectivo.getDigitoControl());		

		Criteria crit = session.createCriteria(Colectivo.class)
				.add(Restrictions.eq("idcolectivo", referencia))
				.add(Restrictions.eq("dc", dc));
		
		// Si la ES Mediadora viene rellena se incluye en la consulta
		if (sm != null) crit.add(Restrictions.eq("subentidadMediadora", sm));
		
		

		colectivoHbm = (Colectivo) crit.uniqueResult();

		if (colectivoHbm == null) {
			msgError="No se encuentra el colectivo. Revise los datos: idcolectivo "
					+ referencia + ", dc " + dc;
			
			if (sm != null && null!=sm.getId() && null!=sm.getId().getCodentidad())
				msgError=msgError + ", entMed " + sm.getId().getCodentidad().toString();
			
			if (sm != null && null!=sm.getId() && null!=sm.getId().getCodsubentidad())
				msgError=msgError + ", subEntMed " + sm.getId().getCodsubentidad().toString();
			
			
			throw new Exception(msgError);
		}

		return colectivoHbm;
	}

	public static Colectivo getColectivoBBDD(final String referencia, final int dc,
			final Session session) throws Exception {

		Colectivo colectivoHbm;
		
		Criteria crit = session.createCriteria(Colectivo.class)
				.add(Restrictions.eq("idcolectivo", referencia))
				.add(Restrictions.eq("dc", Integer.toString(dc)));

		colectivoHbm = (Colectivo) crit.uniqueResult();

		if (colectivoHbm == null) {
			throw new Exception(
					"No se encuentra el colectivo. Revise los datos: idcolectivo "
							+ referencia + ", dc " + dc);
		}

		return colectivoHbm;
	}
	
		
	// Metodo que valida que el asegurado recibido en la situacion actual
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	private static Asegurado getAseguradoBBDD(final es.agroseguro.contratacion.Asegurado asegurado,
			final BigDecimal codentidad, final Session session, final SubentidadMediadora sm) throws Exception {

		Asegurado aseguradoHbm;
		String nif;
		String msgError;
		
		nif = String.format("%9s", asegurado.getNif()).replace(' ', '0');

		Criteria crit = session.createCriteria(Asegurado.class)
				.createAlias("entidad", "entidad")
				.add(Restrictions.eq("nifcif", nif))
				.add(Restrictions.eq("entidad.codentidad", codentidad));
		
		// Si se ha informado la ES Mediadora
		if (sm != null) {
			crit.createAlias("usuario", "usuario");
			crit.createAlias("usuario.subentidadMediadora", "usuario.subentidadMediadora");
			crit.add(Restrictions.eq("usuario.subentidadMediadora.id.codentidad", sm.getId().getCodentidad()));
			crit.add(Restrictions.eq("usuario.subentidadMediadora.id.codsubentidad", sm.getId().getCodsubentidad()));
		}

		aseguradoHbm = (Asegurado) crit.uniqueResult();

		if (aseguradoHbm == null) {
			msgError= "No se encuentra el asegurado. Revise los datos: nifcif "	+ nif + ", codentidad " + codentidad;
			
			if (sm != null && null!=sm.getId() && null!=sm.getId().getCodentidad())
				msgError=msgError + ", entMed " + sm.getId().getCodentidad().toString();
			
			if (sm != null && null!=sm.getId() && null!=sm.getId().getCodsubentidad())
				msgError=msgError + ", subEntMed " + sm.getId().getCodsubentidad().toString();
			
			
			throw new Exception(msgError);
		}

		return aseguradoHbm;
	}

	// Metodo que valida que el usuario asociado a la poliza por configuracion
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	
	public static Asegurado getAseguradoBBDD(final String  nif,
			final BigDecimal codentidad, final Session session)
			throws Exception {

		Asegurado aseguradoHbm;
		String nifAseg=nif;	

		nifAseg = String.format("%9s",nif.replace(' ', '0'));

		Criteria crit = session.createCriteria(Asegurado.class)
				.createAlias("entidad", "entidad")
				.add(Restrictions.eq("nifcif", nifAseg))
				.add(Restrictions.eq("entidad.codentidad", codentidad));

		aseguradoHbm = (Asegurado) crit.uniqueResult();

		if (aseguradoHbm == null) {
			throw new Exception(
					"No se encuentra el asegurado. Revise los datos: nifcif "
							+ nifAseg + ", codentidad " + codentidad);
		}

		return aseguradoHbm;
	}
	
	
	protected static Usuario getUsuarioPolizaBBDD(final Session session)
			throws Exception {

		Usuario usuarioHbm;

		usuarioHbm = (Usuario) session.get(Usuario.class,
				ImportacionConstants.USUARIO_IMPORTACION);

		if (usuarioHbm == null) {
			throw new Exception(
					"No se encuentra el usuario. Revise los datos: codusuario "
							+ ImportacionConstants.USUARIO_IMPORTACION);
		}

		return usuarioHbm;
	}

	// Metodo que valida que la linea de seguro recibida en la situacion actual
	// este presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
	public static Linea getLineaSeguroBBDD(final BigDecimal codlinea,
			final BigDecimal codplan, final Session session) throws Exception {

		Linea lineaHbm;

		Criteria crit = session.createCriteria(Linea.class)
				.add(Restrictions.eq("codlinea", codlinea))
				.add(Restrictions.eq("codplan", codplan))
				.add(Restrictions.eq("activo", "SI"));

		lineaHbm = (Linea) crit.uniqueResult();

		if (lineaHbm == null) {
			throw new Exception(
					"No se encuentra la linea de seguro. Revise los datos: codlinea "
							+ codlinea + ", codplan " + codplan);
		}

		return lineaHbm;
	}

	// Metodo que valida que la poliza principal correspondiente a la situacion
	// actual este presente en la BBDD de Agroplus y devuelve el objeto
	// Hibernate necesario para las referencias externas de la poliza en la
	// importacion
	protected static Poliza getPolizaPpalBBDD(final BigDecimal plan,
			final BigDecimal linea, final String referencia,
			final String moduloComp, final Session session) throws Exception {

		Criteria crit;
		Poliza polizaHbm;
		String moduloPpal;

		crit = session.createCriteria(Modulo.class)
				.createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan", plan))
				.add(Restrictions.eq("linea.codlinea", linea))
				.add(Restrictions.eq("id.codmodulo", moduloComp));

		moduloPpal = ((Modulo) crit.uniqueResult()).getCodmoduloasoc();

		if (moduloPpal == null) {
			throw new Exception(
					"No se encuentra el modulo asociado. Revise los datos: plan "
							+ plan + ", linea " + linea
							+ ", modulo complementario " + moduloComp);
		}

		crit = session.createCriteria(Poliza.class)
				.createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan", plan))
				.add(Restrictions.eq("linea.codlinea", linea))
				.add(Restrictions.eq("referencia", referencia))
				.add(Restrictions.eq("codmodulo", moduloPpal.trim()));

		polizaHbm = (Poliza) crit.uniqueResult();

		return polizaHbm;
	}

	private static void guardaSituacionActual(final Poliza polizaHbm,
			final String xmlText, final Session session) {

		Clob clob;
		Reader reader = null;

		try {

			clob = Hibernate.createClob(xmlText);
			reader = clob.getCharacterStream();
			polizaHbm.setXmlacusecontratacion(Hibernate.createClob(reader,
					(int) clob.length()));

		} catch (SQLException e) {

			logger.error("Error al guardar el XML de la situacion actual.", e);

		} finally {
			try {
				//if (reader != null)
				//	reader.close();
			} catch (Exception ex) {
				// Exception free code
			}
		}
	}
	
	private static BigDecimal toBigDecimal (String numero) {
		try {
			return new BigDecimal (numero.trim());
		} catch (Exception e) {
			return null;
		}
	}
}
