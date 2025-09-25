package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService;
import com.rsi.agp.dao.tables.poliza.Poliza;

public final class ImportacionPolizasExtBBDD {
	
	private static final Logger logger = Logger
			.getLogger(ImportacionPolizasExtBBDD.class);

	private ImportacionPolizasExtBBDD() {
	} 
	
	protected static void importaPolizaExt(final es.agroseguro.contratacion.Poliza polizaActual, final Long idEnvio,
			final Session session, final boolean isPrincipal, final boolean esGanado, final ImportacionPolizasService importacionPolizasService) throws Exception {

		boolean existePoliza = false;
		Poliza polizaHbm = null;

		logger.debug("** ImportacionPolizasExtBBDD - Dentro de importaPolizaExt [INIT]");

		existePoliza = existePolizaHbm(session, polizaActual.getPlan(), polizaActual.getLinea(), polizaActual.getReferencia(), isPrincipal);
		if (existePoliza) {
			throw new BusinessException(
					"La poliza ya se encuentra presente en el sistema. Revise los datos: codPlan "
							+ polizaActual.getPlan() + ", referencia "
							+ polizaActual.getReferencia());
		} else {
			logger.debug("La poliza " + polizaActual.getReferencia() + " / "
					+ polizaActual.getCobertura().getModulo().trim() + " no se encuentra en el sistema. La creamos.");
			polizaHbm = new Poliza();

			if (isPrincipal) {
				if (esGanado) {
					logger.debug(" ** Poliza Principal de Ganado");
					importacionPolizasService.populateAndValidatePolizaGanado(polizaHbm, polizaActual, idEnvio, session, true, "");
				} else {
					logger.debug(" ** Poliza Principal de Agricola");
					importacionPolizasService.populateAndValidatePoliza(polizaHbm, polizaActual, idEnvio, session, true, "");
				}
			} else {
				logger.debug(" ** Poliza Complementaria");
				importacionPolizasService.populateAndValidatePolizaComp(polizaHbm, polizaActual, idEnvio, session, true, "");
			}			
		}
	}

	private static boolean existePolizaHbm(final Session session, int codplan, int codLinea, String referencia, boolean isPrincipal){
		Poliza polizaHbm=null;
		String tipoReferencia = (isPrincipal) ? "P" : "C";
		Criteria crit = session.createCriteria(Poliza.class)
				.createAlias("linea", "linea")
				.add(Restrictions.eq("linea.codplan",BigDecimal.valueOf(codplan)))
				.add(Restrictions.eq("linea.codlinea", BigDecimal.valueOf(codLinea)))
				.add(Restrictions.eq("referencia",referencia))
				.add(Restrictions.eq("tipoReferencia", tipoReferencia));
		
		logger.debug("plan: " + codplan + " - referencia: " + referencia + " - tipoReferencia: " + tipoReferencia);

		polizaHbm = (Poliza) crit.uniqueResult();
		
		return (null!=polizaHbm);
	}
}