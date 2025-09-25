package com.rsi.agp.batch.envioCuentasRenovables;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rsi.agp.core.exception.BusinessException;

import es.agroseguro.contratacion.seguroRenovable.gastos.Cuenta;

public final class BBDDEnvioCuentasRenovables {

	private BBDDEnvioCuentasRenovables() {
	}

	public static final String PARAMETER_IN = "IN";
	public static final String PARAMETER_OUT = "OUT";
	public static final String FUNCTION_RESULT = "RESULT";
	public static final String DB_TYPE_NUMBER = "NUMBER";
	public static final String DB_TYPE_VARCHAR2 = "VARCHAR2";
	public static final String DB_TYPE_VARCHAR = "VARCHAR";
	public static final String DB_TYPE_DATE = "DATE";

	private static final Logger logger = Logger.getLogger(BBDDEnvioCuentasRenovables.class);

	// Metodo que actualiza el historico de cambio de estado de envio IBAN en la
	// poliza renovable
	public static void actualizarHistorico(final Long id, int estadoIBAN, final Session session) {
		String strHis = " insert into o02agpe0.tb_plz_renov_env_iban_hist_est values(o02agpe0.sq_plz_renov_env_iban_hist_est.nextval,"
				+ id + "," + estadoIBAN + ",'BATCH'," + " sysdate)";
		logger.debug("ACTUALIZACION HISTORICO: " + strHis);
		Query qHis = session.createSQLQuery(strHis);
		qHis.executeUpdate();
	}

	// Metodo que actualiza el estado en la poliza renovable
	public static void actualizaEstadoById(final Long id, final int estadoAGROPLUS, final String descError,
			final Session session) {
		String strE = " update o02agpe0.tb_polizas_renovables set estado_agroplus = " + estadoAGROPLUS
				+ ", DESC_ERROR_ENVIO = '" + descError + "' where ID =" + id + "";
		logger.debug("ACTUALIZACION DE ESTADO " + strE);
		Query query = session.createSQLQuery(strE);
		query.executeUpdate();

	}

	// Metodo que atualiza las polizas renovables con estado IBAN 2-Preprado a 3 -
	// Enviado
	public static void actualizaEstadoEnvioIBAN(final Session session, int codPlan, int estadoIBAN) {
		String strQ = " update o02agpe0.tb_polizas_renovables set estado_envio_iban_agro = " + estadoIBAN + ", "
				+ "fecha_envio_iban_agro=SYSDATE " + "where plan =" + codPlan + " and estado_envio_iban_agro = "
				+ EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_PREPARADO;
		logger.debug(
				"actualiza las polizas renovables con estado IBAN 2-Preprado a 3 - Enviado y su fecha de envio a Agroseguro: "
						+ strQ);
		Query query = session.createSQLQuery(strQ);
		query.executeUpdate();
	}
	
	// Metodo que atualiza las polizas renovables con estado IBAN 3-Enviado a 2 -
	// Enviado
	public static void rollbackEstadoEnvioIBAN(final Session session, int codPlan, int estadoIBAN) {
		String strQ = " update o02agpe0.tb_polizas_renovables set estado_envio_iban_agro = " + estadoIBAN + ", "
				+ "fecha_envio_iban_agro=SYSDATE " + "where plan =" + codPlan + " and estado_envio_iban_agro = "
				+ EnvioCuentasRenovablesConstants.ES_POL_REN_ENVIO_IBAN_ENVIADO;
		logger.debug(
				"actualiza las polizas renovables con estado IBAN 3-Enviado a 2-Preparado - Enviado y su fecha de envio a Agroseguro: "
						+ strQ);
		Query query = session.createSQLQuery(strQ);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public static String recogerCuentaAsegurado(final Session session, String idPolRen, String codLinea) {
		String iban = "";
		String sql = " select dat.iban||dat.ccc as IBAN  from o02agpe0.tb_polizas_renovables ren,o02agpe0.tb_colectivos_renovacion col, o02agpe0.tb_asegurados ase,"
				+ " o02agpe0.tb_usuarios usu,o02agpe0.tb_datos_asegurados dat  where 1=1  and ren.idcolectivo = col.id  and ren.nif_asegurado = ase.nifcif "
				+ " and col.codentidad = ase.codentidad  and col.codentidadmed = usu.entmediadora and col.codsubentmed = usu.subentmediadora "
				+ " and ase.codusuario = usu.codusuario and ase.id = dat.idasegurado and ren.id=" + idPolRen
				+ " and dat.codlinea=" + codLinea;
		List<Object> resultado = session.createSQLQuery(sql).list();
		if (resultado != null && !resultado.isEmpty()) {
			iban = (String) resultado.get(0);
		}
		return iban;
	}

	// ESC-8663 ** MODIF TAM (13/02/2020) ** Inicio //
	// Recuperamos el destinatario del asegurado.
	@SuppressWarnings("unchecked")
	public static Cuenta recogerDestinatarioAseg(final Session session, String idPolRen, String codLinea) {

		logger.debug("Dentro de recogerDestinatarioAseg, con línea:" + codLinea);
		Cuenta cuenta = Cuenta.Factory.newInstance();

		String sql = " select dat.dest_domiciliacion as DEST, dat.titular_cuenta as TIT from o02agpe0.tb_polizas_renovables ren,o02agpe0.tb_colectivos_renovacion col, o02agpe0.tb_asegurados ase,"
				+ " o02agpe0.tb_usuarios usu,o02agpe0.tb_datos_asegurados dat  where 1=1  and ren.idcolectivo = col.id  and ren.nif_asegurado = ase.nifcif "
				+ " and col.codentidad = ase.codentidad  and col.codentidadmed = usu.entmediadora and col.codsubentmed = usu.subentmediadora "
				+ " and ase.codusuario = usu.codusuario and ase.id = dat.idasegurado and ren.id=" + idPolRen
				+ " and dat.codlinea=" + codLinea;

		logger.debug("Recuperamos el destinatario y titular de los datos de asegurado: " + sql);

		List<Object> resultado = session.createSQLQuery(sql).list();
		Object[] params = null;

		if (resultado != null && !resultado.isEmpty()) {

			params = (Object[]) resultado.get(0);
			String destinatario = ((String) params[0]);
			String titular = ((String) params[1]);

			cuenta.setDestinatario(destinatario);
			cuenta.setTitular(titular);
		}
		return cuenta;
	}
	// ESC-8663 ** MODIF TAM (13/02/2020) ** Fin /

	// Metodo que devuelve el nombre del fichero
	@SuppressWarnings("deprecation")
	public static String getNombreFichero(String codPlan, Session session) throws BusinessException, SQLException {
		try (Connection conexion = session.connection();
				CallableStatement sentencia = conexion
						.prepareCall("{?=call o02agpe0.PQ_GENERA_ENVIOS_AGROSEGURO.get_next_name_file( ?, ? )}")) {
			sentencia.registerOutParameter(1, Types.VARCHAR);
			sentencia.setString(2, codPlan);
			sentencia.setString(3, "GS");
			sentencia.executeQuery();
			return sentencia.getString(1);
		}
	}
}