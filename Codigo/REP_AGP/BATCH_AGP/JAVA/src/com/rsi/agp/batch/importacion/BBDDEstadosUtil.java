package com.rsi.agp.batch.importacion;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.managers.impl.EleccionFormaPagoManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.pagos.EstadosPago;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;
import com.rsi.agp.dao.tables.pagos.HistoricoPoliza;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.HistoricoEstados;
import com.rsi.agp.dao.tables.poliza.Poliza;

public final class BBDDEstadosUtil {

	private static final Log logger = LogFactory.getLog(EleccionFormaPagoManager.class);
	
	private BBDDEstadosUtil() {
	}

	// Método que inserta los registros del ciclo de vida de la póliza
	// Uno para el estado de la póliza y otro para el estado del pago
	protected static void actualizaHistEstado(final Poliza polizaHbm,
			final Date fechaPago, final Session session) {
		
		logger.debug ("Dentro de actualizaHistEstado [INIT]");

		HistoricoEstados histEstadoHbm;
		HistoricoPoliza histPolizaHbm;

		histEstadoHbm = new HistoricoEstados();

		histEstadoHbm.setCodusuario(ImportacionConstants.USUARIO_IMPORTACION);
		histEstadoHbm.setEstado(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR);
		histEstadoHbm.setFecha(fechaPago);
		histEstadoHbm
				.setId((BigDecimal) session
						.createSQLQuery(
								"SELECT o02agpe0.sq_polizas_historico_estados.NEXTVAL FROM DUAL")
						.uniqueResult());
		histEstadoHbm.setPoliza(polizaHbm);

		session.saveOrUpdate(histEstadoHbm);
		
		histEstadoHbm = new HistoricoEstados();

		histEstadoHbm.setCodusuario(ImportacionConstants.USUARIO_IMPORTACION);
		histEstadoHbm.setEstado(Constants.ESTADO_POLIZA_DEFINITIVA);
		histEstadoHbm.setFecha(polizaHbm.getFechaenvio());
		histEstadoHbm
				.setId((BigDecimal) session
						.createSQLQuery(
								"SELECT o02agpe0.sq_polizas_historico_estados.NEXTVAL FROM DUAL")
						.uniqueResult());
		histEstadoHbm.setPoliza(polizaHbm);

		session.saveOrUpdate(histEstadoHbm);

		histPolizaHbm = new HistoricoPoliza();

		histPolizaHbm.setId((BigDecimal) session.createSQLQuery(
				"SELECT o02agpe0.sq_pago_historico_plz.NEXTVAL FROM DUAL")
				.uniqueResult());
		histPolizaHbm.setCodusuario(ImportacionConstants.USUARIO_IMPORTACION);
		histPolizaHbm.setEstadosPago((EstadosPago) session.get(
				EstadosPago.class,
				BigDecimal.valueOf(Constants.PAGO_CORRECTO.longValue())));
		histPolizaHbm.setEstadosPoliza((EstadosPoliza) session.get(
				EstadosPoliza.class,
				BigDecimal.valueOf(Constants.POLIZA_PAGADA.longValue())));
		histPolizaHbm.setFecha(fechaPago);
		histPolizaHbm.setPoliza(polizaHbm);

		session.saveOrUpdate(histPolizaHbm);
		
		logger.debug ("Dentro de actualizaHistEstado [END]");
	}

	// M�todo que valida que el estado de la p�liza asociado a la misma por
	// configuraci�n
	// est� presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la p�liza en la importaci�n
	protected static EstadoPoliza getEstadoPolizaBBDD(final Session session)
			throws Exception {

		EstadoPoliza estadoHbm;

		estadoHbm = (EstadoPoliza) session.get(EstadoPoliza.class,
				Constants.ESTADO_POLIZA_DEFINITIVA);

		if (estadoHbm == null) {
			throw new Exception(
					"No se encuentra el estado de la p�liza. Revise los datos: idEstado "
							+ Constants.ESTADO_POLIZA_DEFINITIVA);
		}

		return estadoHbm;
	}

	// M�todo que valida que el estado del pago asociado a la p�liza por
	// configuraci�n
	// est� presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la p�liza en la importaci�n
	protected static EstadoPagoAgp getEstadoPagoBBDD(final Session session)
			throws Exception {

		EstadoPagoAgp estadoPagoHbm;

		estadoPagoHbm = (EstadoPagoAgp) session.get(EstadoPagoAgp.class,
				Constants.POLIZA_PAGADA);

		if (estadoPagoHbm == null) {
			throw new Exception(
					"No se encuentra el estado del pago. Revise los datos: idPagoAgp "
							+ Constants.POLIZA_PAGADA);
		}

		return estadoPagoHbm;
	}

	// M�todo que actualiza el estado de la importaci�n a FALLO
	protected static void actualizaEstadoKOImportacion(final Long id,
			final String message, final Boolean isPrincipal,
			final Session session) {

		actualizaEstadoImportacion(id, message,
				ImportacionConstants.ESTADO_IMPORTACION_KO, isPrincipal,
				session);
	}

	// M�todo que actualiza el estado de la importaci�n a CORRECTO
	protected static void actualizaEstadoOKImportacion(final Long id,
			final Boolean isPrincipal, final Session session) {

		actualizaEstadoImportacion(id, "P�liza importada correctamente.",
				ImportacionConstants.ESTADO_IMPORTACION_OK, isPrincipal,
				session);
	}

	// M�todo que actualiza el estado de la importaci�n
	private static void actualizaEstadoImportacion(final Long id,
			final String message, final int estado, final Boolean isPrincipal,
			final Session session) {

		com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt registro = (com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt) session
				.get(com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt.class,
						id);
		registro.setEstado(estado);
		registro.setDetalle(message);
		// Si el par�metro isPrincipal llega a nulo
		// significa que el fallo se ha producido antes de recibir
		// la situaci�n actual, as� que marcamos el registro como No Available
		registro.setTipoRef(isPrincipal == null ? "N" : (isPrincipal ? String
				.valueOf(Constants.MODULO_POLIZA_PRINCIPAL) : String
				.valueOf(Constants.MODULO_POLIZA_COMPLEMENTARIO)));
		registro.setFecImportacion(new Date());

		session.saveOrUpdate(registro);
	}
}
