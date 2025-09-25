package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.ICambioModuloDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.contratacion.PolizaDocument;

public class CambioModuloManager {

	private static final Log LOGGER = LogFactory.getLog(CambioModuloManager.class);

	private ICambioModuloDao cambioModuloDao;

	private static final BigDecimal[] estadosPermitidos = new BigDecimal[] { Constants.ESTADO_POLIZA_DEFINITIVA,
			Constants.ESTADO_POLIZA_EMITIDA, Constants.ESTADO_POLIZA_RESCINDIDA, Constants.ESTADO_POLIZA_ANULADA };

	public String cambioModulo(final Long idpoliza, final String realPath) throws BusinessException {
		LOGGER.debug("[cambioModulo] init");
		Poliza poliza;
		String moduloDestino = "";
		try {
			poliza = (Poliza) this.cambioModuloDao.get(Poliza.class, idpoliza);
			if (poliza == null) {
				throw new BusinessException("La p\u00F3liza con identificador " + idpoliza + " no existe");
			} else {
				if (ArrayUtils.contains(estadosPermitidos, poliza.getEstadoPoliza().getIdestado())) {
					if (poliza.getLinea().getCodplan().longValue() < 2015) {
						throw new BusinessException(
								"Operaci\u00F3n no permitida para p\u00F3lizas de planes anteriores al 2015");
					} else {
						boolean esGanado = poliza.getLinea().isLineaGanado();
						try {
							PolizaActualizadaResponse resp = new SWAnexoModificacionHelper()
									.getPolizaActualizadaUnificado(poliza.getReferencia(),
											poliza.getLinea().getCodplan(), realPath, esGanado);
							PolizaDocument polizaDocument;
							if (esGanado) {
								polizaDocument = resp.getPolizaGanado();
							} else {
								if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia())) {
									polizaDocument = resp.getPolizaPrincipalUnif();
								} else {
									polizaDocument = resp.getPolizaComplementariaUnif();
								}
							}
							if (polizaDocument == null) {
								throw new BusinessException(
										"El servicio web no ha devuelto la situaci\u00F3n actualizada");
							} else {
								es.agroseguro.contratacion.Poliza xmlPol = polizaDocument.getPoliza();
								moduloDestino = xmlPol.getCobertura().getModulo().trim();
								if (moduloDestino.equals(poliza.getCodmodulo())) {
									throw new BusinessException(
											"La situaci\u00F3n actualizada tiene el mismo m\u00F3dulo que la p\u00F3liza");
								} else {
									this.cambioModuloDao.cambiarModulo(poliza, xmlPol);
								}
							}
						} catch (Exception e) {
							throw new BusinessException(e.getMessage());
						}
					}
				} else {
					throw new BusinessException(
							"La p\u00F3liza no est\u00E1 en un estado que permita el cambio de m\u00F3dulo");
				}
			}
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage());
		}
		LOGGER.debug("[cambioModulo] end");
		return moduloDestino;
	}

	public void setCambioModuloDao(ICambioModuloDao cambioModuloDao) {
		this.cambioModuloDao = cambioModuloDao;
	}
}