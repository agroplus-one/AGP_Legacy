package com.rsi.agp.core.manager.impl.anexoRC;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa.Causas;
import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.SolicitudReduccionCapResponse;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.cgen.RiesgoId;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class AnexoReduccionCapitalManager implements IAnexoReduccionCapitalManager {

	private static final Log logger = LogFactory.getLog(AnexoReduccionCapitalManager.class);

	private SWAnexoRCHelper swAnexoRCHelper;
	private ParametrizacionManager parametrizacionManager;

	public List<Riesgo> obtenerAyudaCausaRC(String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.obtenerAyudaCausaRC");

		Causas retorno = null;

		List<Riesgo> listaAnteriorCambio = new ArrayList<Riesgo>();
		try {
			retorno = this.swAnexoRCHelper.getAyudaCausaRC(realPath);

			// POJO to current data strucutre
			for (int i = 0; i < retorno.getCausa().size(); i++) {
				Riesgo riesgoAnteriorCambio = new Riesgo();
				riesgoAnteriorCambio.setDesriesgo(retorno.getCausa().get(i).getDescripcion());

				RiesgoId riesgoIdAnteriorCambio = new RiesgoId();
				riesgoIdAnteriorCambio.setCodriesgo(retorno.getCausa().get(i).getCodigoCausa());

				riesgoAnteriorCambio.setId(riesgoIdAnteriorCambio);

				listaAnteriorCambio.add(riesgoAnteriorCambio);
			}

			logger.debug(retorno);

		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.obtenerAyudaCausaRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.obtenerAyudaCausaRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.obtenerAyudaCausaRC");

		return listaAnteriorCambio;
	}

	public List<Object[]> obtenerAyudaCausaDeclaracionRC(String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC");

		Causas retorno = null;

		List<Object[]> listaAnteriorCambio = new ArrayList<Object[]>();
		try {
			retorno = this.swAnexoRCHelper.getAyudaCausaRC(realPath);

			// POJO to current data strucutre
			for (int i = 0; i < retorno.getCausa().size(); i++) {
				Object[] objToAdd = new Object[2];

				objToAdd[0] = retorno.getCausa().get(i).getCodigoCausa();
				objToAdd[1] = retorno.getCausa().get(i).getDescripcion();

				listaAnteriorCambio.add(objToAdd);
			}

			logger.debug(retorno);

		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.obtenerAyudaCausaDeclaracionRC");

		return listaAnteriorCambio;
	}

	public PolizaActualizadaRCResponse consultarContratacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.consultarContratacionRC");

		PolizaActualizadaRCResponse retorno = null;

		try {
			retorno = this.swAnexoRCHelper.consultarContratacionRC(referencia, plan, realPath);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.consultarContratacionRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.consultarContratacionRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.consultarContratacionRC");

		return retorno;
	}

	public SolicitudReduccionCapResponse solicitudModificacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws AgrException, Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.solicitudModificacionRC");

		SolicitudReduccionCapResponse retorno = null;

		try {
			retorno = this.swAnexoRCHelper.solicitudModificacionRC(referencia, plan, realPath);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.solicitudModificacionRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.solicitudModificacionRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.solicitudModificacionRC");

		return retorno;
	}

	public AcuseRecibo envioModificacionRC(final String idCupon, final boolean revAdministrativa, final Clob xmlPpal,
			final Clob xmlCpl, final String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.envioModificacionRC");

		AcuseRecibo retorno = null;

		try {
			retorno = this.swAnexoRCHelper.envioModificacionRC(idCupon, revAdministrativa, xmlPpal, xmlCpl, realPath);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.envioModificacionRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.envioModificacionRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.envioModificacionRC");

		return retorno;
	}

	public String anulacionCuponRC(final String idCupon, final String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.anulacionCuponRC");

		String retorno = Constants.STR_EMPTY;

		try {
			retorno = this.swAnexoRCHelper.anulacionCuponRC(idCupon, realPath);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.anulacionCuponRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.anulacionCuponRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.anulacionCuponRC");

		return retorno;
	}

	public AcuseRecibo validacionModificacionRC(String xmlpoliza,final String idCupon, final String realPath) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.validacionModificacionRC");

		AcuseRecibo retorno = null;

		try {
			retorno = this.swAnexoRCHelper.validacionModificacionRC(xmlpoliza, idCupon, realPath);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.validacionModificacionRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.validacionModificacionRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.validacionModificacionRC");

		return retorno;
	}

	public Map<String, Object> calculoModificacionCuponActivoRC(final String realPath, final String cupon,
			final Base64Binary xml) throws Exception {
		logger.debug("INIT AnexoReduccionCapitalManager.calculoModificacionCuponActivoRC");

		Map<String, Object> retorno = null;

		try {
			boolean calcularSituacionActual = parametrizacionManager.getParametro().getCalculoSitActSwCalculoAm()
					.intValue() == 1;

			retorno = this.swAnexoRCHelper.calculoModificacionCuponActivoRC(realPath, cupon, calcularSituacionActual,
					xml);
		} catch (AgrException e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.calculoModificacionCuponActivoRC. ", e);
		} catch (Exception e) {
			logger.error("########## - Error en AnexoReduccionCapitalManager.calculoModificacionCuponActivoRC. ", e);
			throw e;
		}

		logger.debug("END AnexoReduccionCapitalManager.calculoModificacionCuponActivoRC");

		return retorno;
	}

	public SWAnexoRCHelper getSwAnexoRCHelper() {
		return swAnexoRCHelper;
	}

	public void setSwAnexoRCHelper(SWAnexoRCHelper servAnexoRCHelper) {
		this.swAnexoRCHelper = servAnexoRCHelper;
	}

	public ParametrizacionManager getParametrizacionManager() {
		return parametrizacionManager;
	}

	public void setParametrizacionManager(ParametrizacionManager parametrizacionManager) {
		this.parametrizacionManager = parametrizacionManager;
	}
}
