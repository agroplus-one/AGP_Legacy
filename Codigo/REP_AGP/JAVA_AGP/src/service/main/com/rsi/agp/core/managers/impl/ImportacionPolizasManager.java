package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.service.IImportacionPolizasService;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IImportacionPolizasExtDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ImportacionPolizasManager implements IManager {
	private static final Log LOGGER = LogFactory.getLog(ImportacionComisionesManager.class);

	private IPolizaDao polizaDao;
	private IImportacionPolizasService importacionPolizasService;
	private IImportacionPolizasExtDao importacionPolizasExtDao;

	public HashMap<String, Object> iniciarImportacion(Integer plan, String referencia, String usuario,
			Character tipoRefPoliza, String realPath) throws BusinessException {

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		LOGGER.debug("ImportacionPolizasManager - iniciarImportacion [INIT]");

		Boolean resultadoImport = false;
		Boolean existePol = false;

		BigDecimal codLinea = null;
		BigDecimal codPlan = null;
		String nifAsegurado = "";
		String colectivo = "";

		try {

			/* Tatiana (24.05.2021) ** Inicio */
			/*
			 * 1º: Hay que consultar si la póliza ya existe en BBDD, en cuyo caso se muestra
			 * mensaje de error y no se continua
			 */
			existePol = importacionPolizasExtDao.existePolizaHbm(plan, referencia, tipoRefPoliza);

			if (existePol) {
				LOGGER.debug("Error la póliza ya existe");
				parameters.put("alert", "Error la póliza ya existe");
				parameters.put("resultado", "KO");
				return parameters;
			}

			PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
			respuesta = new SWAnexoModificacionHelper().getPolizaActualizada(referencia, new BigDecimal(plan),
					realPath);

			es.agroseguro.contratacion.Poliza polizaActualizada = null;

			if (tipoRefPoliza == 'P') {
				LOGGER.debug("**** Entramos por Polizas PRINCIPALES");
				if (respuesta.getPolizaGanado() != null) {
					polizaActualizada = respuesta.getPolizaGanado().getPoliza();
					LOGGER.debug("polizaActualizada Ppal (Ganado): " + polizaActualizada);
				} else if (respuesta.getPolizaPrincipalUnif() != null) {
					polizaActualizada = respuesta.getPolizaPrincipalUnif().getPoliza();
					LOGGER.debug("polizaActualizada Ppal (Agrícola): " + polizaActualizada);
				}

			} else {
				LOGGER.debug("**** Entramos por Polizas COMPLEMENTARIAS");
				if (respuesta.getPolizaComplementariaUnif() == null && tipoRefPoliza.equals('C')) {
					parameters.put("alert",
							"Atención, No se han encontrado datos para el tipoReferencia Complementaria");
					parameters.put("resultado", "KO");
					return parameters;
				} else if (respuesta.getPolizaComplementariaUnif() != null) {
					polizaActualizada = respuesta.getPolizaComplementariaUnif().getPoliza();
					LOGGER.debug("polizaActualizada Complementaria (Agrícola): " + polizaActualizada);
				}
			}

			/* Tatiana (24.05.2021) ** Fin */

			Poliza polizaHbm = new Poliza();
			Boolean isBatch = false;

			codLinea = new BigDecimal(polizaActualizada.getLinea());
			codPlan = new BigDecimal(polizaActualizada.getPlan());
			nifAsegurado = polizaActualizada.getAsegurado().getNif();
			colectivo = polizaActualizada.getColectivo().getReferencia();

			// Guardamos la uditoria en la tabla TB_SW_CONS_CONTRATACION
			LOGGER.debug("ImportacionPolizasManager - Antes de guardarAuditoriaImportacionPoliza");
			importacionPolizasExtDao.guardarAuditoriaImportacionPoliza(isBatch, new BigDecimal(plan), referencia,
					polizaActualizada.toString(), usuario);
			LOGGER.debug("ImportacionPolizasManager - Despues de guardarAuditoriaImportacionPoliza");

			Session session = null;
			Colectivo col = importacionPolizasExtDao.getColectivoBBDDonline(polizaActualizada.getColectivo(), session,
					codPlan, isBatch);

			if (col == null) {
				LOGGER.info("Atención, No se ha encontrado el colectivo");
				parameters.put("alert", "Atención, colectivo inexistente");
				parameters.put("resultado", "KO");
				return parameters;
			}

			/* RGA solicita que no se valide el usuario correo del 08/06/2021 */

			Linea linea = importacionPolizasExtDao.getLineaSeguroBBDD(codLinea, codPlan, null, isBatch);

			boolean esGanado = (linea.getEsLineaGanadoCount() > 0);

			LOGGER.debug("Continuamos con el alta, usuario y colectivo de la misma Entidad");

			if (tipoRefPoliza == 'P') {

				if (esGanado) {
					LOGGER.debug("*** Poliza Principal de Ganado ***");

					/*
					 * En este caso y solo para el proceso online pasaomos en la variable idEnvio el
					 * valor del estado de la poliza
					 */
					resultadoImport = importacionPolizasService.populateAndValidatePolizaGanado(polizaHbm,
							polizaActualizada, null, /* session */ null, isBatch, usuario);
				} else {
					LOGGER.debug("*** Poliza Principal de Agricola ***");
					resultadoImport = importacionPolizasService.populateAndValidatePoliza(polizaHbm, polizaActualizada,
							null, null, isBatch, usuario);
				}
			} else {
				/* De momento lo comentamos */

				LOGGER.debug("*** Poliza Complementaria ***");
				if (polizaActualizada != null) {
					resultadoImport = importacionPolizasService.populateAndValidatePolizaComp(polizaHbm,
							polizaActualizada, null, /* session */ null, isBatch, usuario);
				} else {
					parameters.put("alert",
							"TipoRef Complementaria, no se han recuperado datos para la complementaria");
					parameters.put("resultado", "KO");
					return parameters;
				}

			}

		} catch (Exception e) {

			LOGGER.debug("Error: " + StringUtils.stack2string(e));

			if ((StringUtils.stack2string(e).indexOf("colectivo")) > 0) {
				LOGGER.info("Atención, colectivo inexistente");
				parameters.put("alert", "Atención, Colectivo inexistente: " + colectivo);
			} else if ((StringUtils.stack2string(e).indexOf("segurado")) > 0) {
				LOGGER.info("Atención,  No se encuentera el Asegurado");
				parameters.put("alert", "Atención, No se encuentra el Asegurado: " + nifAsegurado);
			} else if ((StringUtils.stack2string(e).indexOf("comisiones")) > 0) {
				LOGGER.info("Atención,  No se encuentran las comisiones del plan: " + codPlan + " y Linea:" + codLinea);
				parameters.put("alert",
						"No se encuentran las comisiones del plan: " + codPlan + " y Linea:" + codLinea);
			} else if ((StringUtils.stack2string(e).indexOf("AgrException")) > 0) {
				LOGGER.info(
						"Se ha producido un AgrException en la llamada al llamar al servicio web de consulta de contratación");
				parameters.put("alert", "La contratación solicitada no existe");
			} else if ((StringUtils.stack2string(e).indexOf("servicio web")) > 0) {
				LOGGER.info("Error inesperado al llamar al servicio web de consulta de contratación");
				parameters.put("alert", "Error inesperado al llamar al servicio web de consulta de contratación");
			} else if ((StringUtils.stack2string(e).indexOf("modulo asociado")) > 0) {
				LOGGER.info("No se encuentra el modulo asociado. Revise los datos. Modulo: " + tipoRefPoliza);
				parameters.put("alert",
						"No se encuentra el modulo asociado. Revise los datos. Modulo: " + tipoRefPoliza);
			} else if ((StringUtils.stack2string(e).indexOf("No se encuentra la poliza Principal")) > 0) {
				LOGGER.info("No se encuentra el modulo asociado. Revise los datos. Modulo: " + tipoRefPoliza);
				parameters.put("alert", "No se encuentra la pól. Principal de la complementaria. Revise los datos.");

			} else {
				LOGGER.info("Atención, Atención, Se ha producido un error en el Alta de la póliza");
				parameters.put("alert", "Atención, Se ha producido un error en el Alta de la póliza ");
				parameters.put("resultado", "KO");
			}

			e.printStackTrace();
		}

		LOGGER.debug("fin - iniciarImportacion");
		if (!resultadoImport) {
			String alerta = (String) parameters.get("alert");
			if (alerta.equals("")) {
				parameters.put("alert", "Atención, Se ha producido un error en el proceso de Alta");
			}

		} else {
			parameters.put("resultado", "OK");
		}
		LOGGER.debug("ImportacionPolizasManager - iniciarImportacion [END]");
		return parameters;

	}

	/* DNF 19/04/2021 */
	public Poliza getPolizaByPlanReferencia(BigDecimal plan, String refPoliza, Character tipoRefPoliza)
			throws BusinessException {
		try {

			Poliza poliza = (Poliza) polizaDao.getPolizaByRefPlanTipoRef(refPoliza, plan, tipoRefPoliza);

			return poliza;

		} catch (DAOException daoe) {
			throw new BusinessException("Error durante el acceso a la base de datos", daoe);
		}
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setImportacionPolizasService(IImportacionPolizasService importacionPolizasService) {
		this.importacionPolizasService = importacionPolizasService;
	}

	public void setImportacionPolizasExtDao(IImportacionPolizasExtDao importacionPolizasExtDao) {
		this.importacionPolizasExtDao = importacionPolizasExtDao;
	}
}
