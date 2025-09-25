/**
 * 
 */
package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.HistoricoEstados;

/**
 * @author U029769 27/06/2013
 */
public class HistoricoEstadosDao extends BaseDaoHibernate implements IHistoricoEstadosDao {

	private static final Log LOG = LogFactory.getLog(HistoricoEstadosDao.class);

	private Session session;

	/**
	 * @author U029769 27/06/2013
	 * @param nomTabla
	 * @param secuencia
	 * @param idObjeto
	 * @param usuario
	 * @param idEstado
	 */
	@Override
	public void insertaEstado(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final BigDecimal idEstado) {

		LOG.debug("Llamada al procedimiento o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado(" + nomTabla + ","
				+ secuencia + "," + idObjeto + "," + codUsuario + "," + idEstado + ")");

		session = obtenerSession();

		if (session != null) {
			session.doWork(new Work() {
				public void execute(final Connection connection) throws SQLException {
					try (CallableStatement call = connection
							.prepareCall("{ call o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado(?,?,?,?,?) }")) {
						call.setString(1, nomTabla);
						call.setString(2, secuencia);
						call.setString(3, codUsuario);
						call.setLong(4, idObjeto);
						call.setBigDecimal(5, idEstado);
						call.execute();
					}
				}
			});
		}
		LOG.debug("FIN de la llamada al PL PQ_HISTORICO_ESTADOS.pr_insertar_estado");
	}

	public void insertaEstadoAnexo(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final BigDecimal idEstado, final Character estadoAgro) {

		LOG.debug("Llamada al procedimiento o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado_anexo(" + nomTabla + ","
				+ secuencia + "," + idObjeto + "," + codUsuario + "," + idEstado + "," + estadoAgro + ")");

		session = obtenerSession();

		if (session != null) {
			session.doWork(new Work() {
				public void execute(final Connection connection) throws SQLException {
					try (CallableStatement call = connection.prepareCall(
							"{ call o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado_anexo(?,?,?,?,?,?) }")) {
						call.setString(1, nomTabla);
						call.setString(2, secuencia);
						call.setString(3, codUsuario);
						call.setLong(4, idObjeto);
						call.setBigDecimal(5, idEstado);
						call.setString(6, estadoAgro != null ? estadoAgro.toString() : "");
						call.execute();
					}
				}
			});
		}
		LOG.debug("FIN de la llamada al PL PQ_HISTORICO_ESTADOS.pr_insertar_estado_anexo");
	}

	public void insertaEstadoPoliza(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final BigDecimal idEstado, final BigDecimal tipoPago, final Date fPrimerPago,
			final BigDecimal pctPrimerPago, final Date fSegundoPago, final BigDecimal pctSegundoPago) {

		final Session session = obtenerSession();
		if (session != null) {
			session.doWork(new Work() {
				public void execute(final Connection connection) throws SQLException {

					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String strFecPrimerPago = "";
					String strFecSegundoPago = "";

					if (fPrimerPago != null) {
						strFecPrimerPago = df.format(fPrimerPago);
					}

					if (fSegundoPago != null) {
						strFecSegundoPago = df.format(fSegundoPago);
					}
					LOG.debug("Llamada al procedimiento o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado_poliza("
							+ nomTabla + "," + secuencia + "," + idObjeto + "," + codUsuario + "," + idEstado + ","
							+ tipoPago + " fPrimerPagoF: " + strFecPrimerPago + " fSegundoPagoF: " + strFecSegundoPago
							+ " pctPrimerPago: " + pctPrimerPago + " pctSegundoPago: " + pctSegundoPago);
					try (CallableStatement call = connection.prepareCall(
							"{ call o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado_poliza(?,?,?,?,?,?,?,?,?,?) }")) {
						call.setString(1, nomTabla);
						call.setString(2, secuencia);
						call.setString(3, codUsuario);
						call.setLong(4, idObjeto);
						call.setBigDecimal(5, idEstado);
						call.setBigDecimal(6, tipoPago);
						call.setString(7, strFecPrimerPago);
						call.setBigDecimal(8, pctPrimerPago);
						call.setString(9, strFecSegundoPago);
						call.setBigDecimal(10, pctSegundoPago);
						call.execute();
					}
				}
			});
		}
		LOG.debug("FIN de la llamada al PL PQ_HISTORICO_ESTADOS.pr_insertar_estado_poliza");
	}

	/**
	 * @author U029769 23/07/2013
	 * @param nomTabla
	 * @param secuencia
	 * @param idObjeto
	 * @param codUsuario
	 * @param idEstado
	 */
	public void insertaEstadoDatosAseg(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final String idEstado) {

		LOG.debug("Llamada al procedimiento o02agpe0." + "PQ_HISTORICO_ESTADOS.pr_insertar_estado(" + nomTabla + ","
				+ secuencia + "," + idObjeto + "," + codUsuario + "," + idEstado + ")");

		session = obtenerSession();

		if (session != null) {
			session.doWork(new Work() {
				public void execute(final Connection connection) throws SQLException {
					try (CallableStatement call = connection.prepareCall(
							"{ call o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_estado_datos_aseg(?,?,?,?,?) }")) {
						call.setString(1, nomTabla);
						call.setString(2, secuencia);
						call.setString(3, codUsuario);
						call.setLong(4, idObjeto);
						call.setString(5, idEstado);
						call.execute();
					}
				}
			});
		}
		LOG.debug("FIN de la llamada al PL PQ_HISTORICO_ESTADOS.pr_insertar_estado");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean esNuevaContratacion(Long idPoliza) throws DAOException {

		session = obtenerSession();
		List<HistoricoEstados> resultado = new ArrayList<HistoricoEstados>();

		try {
			Criteria criteria = session.createCriteria(HistoricoEstados.class).createAlias("poliza", "poliza")
					.add(Restrictions.eq("poliza.idpoliza", idPoliza))
					.add(Restrictions.eq("estado", new BigDecimal(8)));

			resultado = criteria.list();
		} catch (Exception ex) {
			LOG.error("Error al obtener los datos del termino " + idPoliza, ex);
			throw new DAOException("Error al obtener los datos del termino " + idPoliza, ex);
		}

		return resultado != null && resultado.size() != 0 ? true : false;
	}

}
