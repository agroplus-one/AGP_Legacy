package com.rsi.agp.batch.renovables;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

public final class BBDDPolRenUtil {
	
	private static final Logger logger = Logger.getLogger(GastosRenovables.class);

	// Metodo que actualiza el historico 
	public static void actualizarHistorico(final Long id, int estadoAGROSEGURO, int estadoAGROPLUS, Character grupoNegocio, final Session session) {
		
		logger.debug("Insercion en la tabla de hist�rico de polizas renovables para el id: " + id + ", estado_agroseguro: " + estadoAGROSEGURO +
					 ", estado_agroplus: " + estadoAGROPLUS + ", grupoNegocio: " + grupoNegocio);
		
		try {
			String strHis = "insert into o02agpe0.tb_plz_renov_hist_estados values"
					+ "(o02agpe0.sq_plz_renov_hist_estados.nextval,"
					+ id
					+ ","
					+ estadoAGROSEGURO
					+ ","
					+ estadoAGROPLUS
					+ ",sysdate,'BATCH',null,null,null,null,null,'"
					+ grupoNegocio + "')";
			session.createSQLQuery(strHis).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al insertar en la tabla de hist�rico de polizas renovables", e);
		}
	}

	// Metodo que actualiza el estado y la descripcion del error en el registro de Gastos Renovacion correspondiente 
	public static void actualizaEstadoById(final Long id, final int estadoAGROPLUS,final String descError, final Session session) {
		
		logger.debug("Actualizacion de la tabla de gastos de renovacion para el id: " + id + ", estado_agroplus: " + estadoAGROPLUS + ", descError: " + descError);
		
		try {
			String strE = " update o02agpe0.tb_gastos_renovacion set estado_agroplus = "
					+ estadoAGROPLUS
					+ ", DESC_ERROR_ENVIO = '"
					+ descError
					+ "' where ID =" + id + "";
			session.createSQLQuery(strE).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al actualizar de la tabla de gastos de renovacion", e);
		}
	}
	
	// Metodo para actualizar los gastos de renovacion con estado "gastos asignados"  a "pendiente de confirmar".
	public static void actualizaEstadoPolRenPendientes(final Session session) {
		
		try {
			String strQ = " update o02agpe0.tb_gastos_renovacion set estado_agroplus = "
					+ GastosRenovablesConstants.EST_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR
					+ " where estado_agroplus = "
					+ GastosRenovablesConstants.EST_AGPLUS_GASTOS_ASIGNADOS
					+ "";
			session.createSQLQuery(strQ).executeUpdate();
		} catch (Exception e) {
			logger.error("Error al actualizar los gastos de renovaci�n con estado 'gastos asignados'  a 'pendiente de confirmar'", e);
		}
		
	}
	
	// Metodo para obtener los datos de la poliza renovable asociada al gasto de renovacion correspondiente al id pasado como parametro
	public static PolizaRenovable getDatosPlzRenovable (final Long id, final Session session) {
		
		logger.debug("Obtiene los datos de la p�liza renovable asociada al gasto de renovaci�n con id: " + id);
		
		PolizaRenovable plzRen = new PolizaRenovable();
		
		try {
			String strQ = "select pr.id, pr.referencia, pr.estado_agroseguro, pr.plan, pr.linea, pr.dc "
					+ "from o02agpe0.tb_polizas_renovables pr, o02agpe0.tb_gastos_renovacion gr "
					+ "where gr.id = "
					+ id
					+ " and gr.idpolizarenovable = pr.id";
			Object[] res = (Object[]) session.createSQLQuery(strQ)
					.uniqueResult();
			plzRen.setId(((BigDecimal) res[0]).longValue());
			plzRen.setReferencia((String) res[1]);
			EstadoRenovacionAgroseguro era = new EstadoRenovacionAgroseguro(
					((BigDecimal) res[2]).longValue(), null);
			plzRen.setEstadoRenovacionAgroseguro(era);
			plzRen.setPlan(((BigDecimal) res[3]).longValue());
			plzRen.setLinea(((BigDecimal) res[4]).longValue());
			plzRen.setDc(((String) res[1]).charAt(0));
		} 
		catch (Exception e) {
			logger.error("Error al Obtiene los datos de la p�liza renovable asociada al gasto de renovaci�n", e);
		}
		
		return plzRen;
	}
	
	/**
	 * Obtiene el listado de registros de gastos de renovacion que estan en estado de Agroplus 'Enviada Pdte. Confirmar'
	 * @param session
	 * @return
	 */
	public static List<com.rsi.agp.dao.tables.renovables.GastosRenovacion> getGastosRenovacionPtes(Session session) {

		Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.renovables.GastosRenovacion.class);
		crit.createAlias("estadoRenovacionAgroplus","estadoRenovacionAgroplus");
		crit.add(Restrictions.eq("estadoRenovacionAgroplus.codigo",((Integer) GastosRenovablesConstants.EST_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR).longValue()));
		
		return (List<com.rsi.agp.dao.tables.renovables.GastosRenovacion>) crit.list();
		
	}
}
