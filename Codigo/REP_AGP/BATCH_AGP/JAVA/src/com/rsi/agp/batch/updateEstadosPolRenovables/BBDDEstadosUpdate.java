package com.rsi.agp.batch.updateEstadosPolRenovables;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.pagos.EstadosPago;
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;
import com.rsi.agp.dao.tables.pagos.HistoricoPoliza;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.HistoricoEstados;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

public final class BBDDEstadosUpdate {

	private BBDDEstadosUpdate() {
	}

	// Metodo que inserta los registros del ciclo de vida de la poliza
	// Uno para el estado de la poliza y otro para el estado del pago
	protected static void actualizaHistEstado(final Poliza polizaHbm,
			final Date fechaPago, final Session session) {

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
	}

	// Metodo que valida que el estado de la poliza asociado a la misma por
	// configuracion
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importaci�n
	protected static EstadoRenovacionAgroplus getEstadoPolRenAgroplus(final Session session)
			throws Exception {
		// insertamos el estado a la poliza renovable
		Integer estadoRenAgroplus = UPRConstants.ES_AGPLUS_PEND_ASIGNAR_GASTOS;
		EstadoRenovacionAgroplus estadoHbm;

		Criteria crit = session
				.createCriteria(EstadoRenovacionAgroplus.class)
				.add(Restrictions.eq("codigo",estadoRenAgroplus.longValue()));
		
		estadoHbm = (EstadoRenovacionAgroplus) crit.uniqueResult();

		if (estadoHbm == null) {
			throw new Exception(
					"No se encuentra el estado de la poliza renovable. Revise los datos: idEstado "
							+ UPRConstants.ES_AGPLUS_PEND_ASIGNAR_GASTOS);
		}

		return estadoHbm;
	}


	// Metodo que valida que el estado de la poliza asociado a la misma por
	// configuracion
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importaci�n
	protected static EstadoRenovacionAgroseguro getEstadoPolRenAgroseguro(final Long estAgroseguro, final Session session)
			throws Exception {
		EstadoRenovacionAgroseguro estadoHbm;
		Criteria crit = session
				.createCriteria(EstadoRenovacionAgroseguro.class)
				.add(Restrictions.eq("codigo",estAgroseguro));
		
		estadoHbm = (EstadoRenovacionAgroseguro) crit.uniqueResult();

		if (estadoHbm == null) {
			throw new Exception(
					"No se encuentra el estado de la poliza renovable. Revise los datos: idEstado "
							+ estAgroseguro);
		}
		return estadoHbm;
	}

	
	// Metodo que valida que el estado del pago asociado a la poliza por
	// configuracion
	// esta presente en la BBDD de Agroplus y devuelve el objeto Hibernate
	// necesario para las referencias externas de la poliza en la importacion
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

	// Metodo que actualiza el estado de la importacion a FALLO
	protected static void actualizaEstadoKOImportacion(final Long id,
			final String message, final Boolean isPrincipal,
			final Session session) {

		actualizaEstadoImportacion(id, message,
				ImportacionConstants.ESTADO_IMPORTACION_KO, isPrincipal,
				session);
	}

	// Metodo que actualiza el estado de la importacion a CORRECTO
	protected static void actualizaEstadoOKImportacion(final Long id,
			final Boolean isPrincipal, final Session session) {

		actualizaEstadoImportacion(id, "Poliza importada correctamente.",
				ImportacionConstants.ESTADO_IMPORTACION_OK, isPrincipal,
				session);
	}

	// Metodo que actualiza el estado de la importaci�n
	private static void actualizaEstadoImportacion(final Long id,
			final String message, final int estado, final Boolean isPrincipal,
			final Session session) {

		com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt registro = (com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt) session
				.get(com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt.class,
						id);
		registro.setEstado(estado);
		registro.setDetalle(message);
		// Si el parametro isPrincipal llega a nulo
		// significa que el fallo se ha producido antes de recibir
		// la situacion actual, asa que marcamos el registro como No Available
		registro.setTipoRef(isPrincipal == null ? "N" : (isPrincipal ? String
				.valueOf(Constants.MODULO_POLIZA_PRINCIPAL) : String
				.valueOf(Constants.MODULO_POLIZA_COMPLEMENTARIO)));
		registro.setFecImportacion(new Date());

		session.saveOrUpdate(registro);
	}
	
	
}
