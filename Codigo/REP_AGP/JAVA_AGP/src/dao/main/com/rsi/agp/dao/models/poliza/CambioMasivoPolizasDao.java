package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class CambioMasivoPolizasDao extends BaseDaoHibernate implements ICambioMasivoPolizasDao {

	private static final Log LOG = LogFactory.getLog(CambioMasivoPolizasDao.class);
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getNumPolizasNoDefitivas(List<String> listaIds) throws DAOException {
		Session sesion = obtenerSession();
		try {			
			String sqlQuery = "SELECT COUNT(*) FROM TB_POLIZAS P WHERE P.IDPOLIZA IN " + StringUtils.toValoresSeparadosXComas(listaIds,false) + 
			" AND P.IDESTADO != " + Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA;			
			// Lanza la consulta y devuelve el valor del count
			List<BigDecimal> listaEntidades = sesion.createSQLQuery (sqlQuery).list();
			return listaEntidades.get(0);
		} 
		catch (Exception ex) {
			logger.error("Se ha producido un error durante el acceso a la base de datos: ", ex);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", ex);
		}
	}

	/**
	 * Inserta en TB_PAGO_HISTORICO_PLZ un registro por cada poliza
	 * 06/08/2013 U029769
	 * @param fechapago
	 * @param listaIds
	 * @param marcar_desmarcar
	 * @param codUsuario
	 */
	public void pagoMasivo(final String fechapago, String listaIds, 
				final String marcar_desmarcar, final String codUsuario)throws DAOException { 
		try {
			final Session session = this.obtenerSession();
			final String[] ids = listaIds.split(",");			
			//Actualizamos el campo "PAGADA" en las polizas
			String sqlQuery = "UPDATE TB_POLIZAS P SET P.PAGADA = " + 
					((StringUtils.nullToString(marcar_desmarcar).equals("S")) ? Constants.POLIZA_PAGADA : Constants.POLIZA_NO_PAGADA) + 
					" WHERE P.IDPOLIZA IN " + StringUtils.toValoresSeparadosXComas(ids, false);					
			session.createSQLQuery (sqlQuery).executeUpdate();			
			//Actualizamos el historico del estado.
			if (session != null) {
				session.doWork(new Work() {
					public void execute(final Connection connection) throws SQLException {						
						LOG.info("por cada poliza insertamos un registro en TB_PAGO_HISTORICO_PLZ");
						for (int i=0;i<ids.length;i++) {							
							try (CallableStatement call = connection.prepareCall("{ call o02agpe0.PQ_HISTORICO_ESTADOS.pr_insertar_pago_poliza(?,?,?,?,?) }")) {
								call.setString(1, ids[i]);
								if (StringUtils.nullToString(marcar_desmarcar).equals("S")){
									call.setLong(2, Constants.POLIZA_PAGADA);
								}else{
									call.setLong(2, Constants.POLIZA_NO_PAGADA);
								}
								call.setBigDecimal(3, Constants.PAGO_NO_PAGADA_MANUAL);
								call.setString(4, codUsuario);
								call.setString(5, fechapago);
								
								LOG.debug("Llamada al procedimiento o02agpe0." +
										"PQ_HISTORICO_ESTADOS.pr_insertar_pago_poliza(" + ids[i] + ","+Constants.POLIZA_PAGADA+"," +
										Constants.PAGO_NO_PAGADA_MANUAL+","+codUsuario+","+fechapago+")");
								
								call.execute();
							}
						}
					}
				});	
			}
			LOG.info("FIN de la llamada al PL PQ_HISTORICO_ESTADOS.pr_insertar_pago_poliza");
		} catch (Exception e) {
			LOG.error("Se ha producido un error al efectuar el pago masivo",e);
			throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
	}	
}