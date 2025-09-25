package com.rsi.agp.batch.incidenciasListAsuntos;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import es.agroseguro.listaAsuntos.ListaAsuntosDocument;
import es.agroseguro.listaMotivosAnulacionRescision.ListaMotivosDocument;

public final class BBDDIncidListaAsuntos {
	
	private static final Logger logger = Logger.getLogger(IncidenciasListAsuntos.class);
	
	public static void tratamientoListIncAsuntos(
			final ListaAsuntosDocument listIncWS,
			final Session session){
		
		int index = 1;
		int reg_update = 0;
		int reg_insert = 0;
		
		logger.debug("Entramos a tratar la lista de incidencias devuelta por el WS");
		int total_reg = listIncWS.getListaAsuntos().sizeOfAsuntoArray();
				
		// Recorremos la lista de asuntos devuelta por el Ws.
		for (index=0; index<total_reg; index++) {

			String codigo_ws = (listIncWS.getListaAsuntos().getAsuntoArray(index).getCodigo()).trim();
			String descr_ws = (listIncWS.getListaAsuntos().getAsuntoArray(index).getDescriptivo()).trim();
			
			String asunInc = null;
			
			try{
			   String strSelect = "SELECT codasunto "
			   						+ "FROM o02agpe0.tb_inc_asuntos "
			   						+ "WHERE codasunto = '" + codigo_ws + "' "
							     	+ "AND catalogo = 'P'"; 
			   
				asunInc = (String) session.createSQLQuery(strSelect).uniqueResult();
				
   			   // Si al hacer la select recupera un valor, quiere decir que existe en la tabla
			   // por lo tanto solo habrá que updatear
			   
			   //if(resultado!=null && resultado.size()>0){
			   // si el registro ya existe (codasunto) en la tabla tb_inc_asuntos, se updatea la tabla para actualizar el 
			   if (asunInc != null){
				   BBDDIncidListaAsuntos.actualizaEstadoAsuntosList(session, IncidenciasListAsuntosConstants.EST_INC_ACTIVO, codigo_ws);
				   reg_update = reg_update + 1;
			   }else{
			   //Si el registro no existe (codasunto) en la tabla tb_inc_asuntos se inserta un nuevo registro con el valor
			   // activo = 1
				   
				   String strInsert ="INSERT into o02agpe0.tb_inc_asuntos"
				   					+ " VALUES ("
				   						+ " '" + codigo_ws  + "',"
				   						+ " '" + descr_ws + "'," 
				   						+ " '" + IncidenciasListAsuntosConstants.EST_INC_ACTIVO + "',"
				   						+ " 'P'"
			   						+ ")";
				   
				   session.createSQLQuery(strInsert).executeUpdate();
				   reg_insert = reg_insert + 1;
			   }
			    
			}catch (Exception e){
				logger.error("Error al Actualizar la lista de Asuntos ", e);
			}
		}
		
		logger.debug("############################################################");
		logger.debug("## ESTADISTICAS: LISTADO DE INC. ASUNTOS (BATCH)          ##");
		logger.debug("## -------------------------------------------------------##");
		logger.debug("## Asuntos recibidos del Web Service:     "+ total_reg  +"##");
		logger.debug("## Asuntos Actualizados             :     "+ reg_update +"##");
		logger.debug("## Asuntos Insertados               :     "+ reg_insert +"##");
		logger.debug("############################################################");		
	}

	// Metodo que borra los asuntos que no estén reflejados en la tabla de Incidencias 
	public static void borrarAsuntos (final Session session) { 	
		/* No se debe borrar el Asunto genérico */
		logger.debug("Borramos asuntos sin el genérico");
		try {
			String strDelete = "DELETE"
								+ " FROM o02agpe0.tb_inc_asuntos a"
								+ " WHERE a.codasunto not in ("
									+ " SELECT DISTINCT i.codasunto"
									+ " FROM o02agpe0.tb_inc_incidencias i"
								+ ")"
								+ " AND a.codasunto not in ("
									+ " SELECT hi.codasunto"
									+ " FROM o02agpe0.tb_inc_incidencias_hist hi"
								+ ")"
								+ " AND a.codasunto not in ('SINASU')"
								+ " AND a.catalogo = 'P'";          
					
			session.createSQLQuery(strDelete).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al borrar los asuntos que no tengan correspondencia con la tabla incidencias", e);
		}
	}

	// Metodo que actualiza el estado y la descripcion del error en el registro de Gastos Renovacion correspondiente 
	public static void actualizaEstadoAsuntos(final Session session, final int  ACTIVO) {
		
		try {
			String strUpd = "UPDATE o02agpe0.tb_inc_asuntos"
							+ " SET activo = " + ACTIVO 
							+ " WHERE"
							+ " codasunto IN ("
								+ " SELECT DISTINCT codasunto "
								+ " FROM o02agpe0.tb_inc_incidencias"
							+ ")"
							+ " AND catalogo = 'P'";						   

			session.createSQLQuery(strUpd).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al actualizar de la tabla de Asuntos de incidencias", e);
		}
	}
	
	// Metodo que actualiza el estado y la descripcion del error en el registro de Gastos Renovacion correspondiente 
	public static void actualizaEstadoAsuntosList(final Session session, final int  ACTIVO, final String ws_codigo) {
		
		try {
			String strUpdLst = "UPDATE o02agpe0.tb_inc_asuntos"
								+ " SET activo = " + ACTIVO
						        + " WHERE codasunto = '" + ws_codigo  + "'"
						        		+ " AND catalogo = 'P'";
    //					               " (SELECT codasunto" +
	//				                  " FROM o02agpe0.tb_inc_incidencias )";						   

			session.createSQLQuery(strUpdLst).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al actualizar el registro " + ws_codigo + "de la tabla de Asuntos de incidencias", e);
		}
	}
	
	
	/* Pet.57627 ** MODIF TAM (29.10.2019) */
	/* Implementamos las funciones necesarias para la gestión de lista de Motivos de las Anulaciones y Rescisiones de pólizas */
	public static void borrarMotivos (final Session session) { 	
		
		try {
			String strDelete = " DELETE " +
							   " FROM o02agpe0.TB_MOTIVOS a" +
							   " WHERE a.codmotivo not in " +
							         " (SELECT i.codmotivo" +
							         "   FROM o02agpe0.tb_inc_incidencias i)" +
							   " AND a.codmotivo not in (SELECT hi.codmotivo" +
							   " FROM o02agpe0.tb_inc_incidencias_hist hi)";    
					
			session.createSQLQuery(strDelete).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al borrar los motivos que no tengan correspondencia con la tabla incidencias", e);
		}
	}
	
	
	// Metodo que actualiza el estado y la descripcion del error en el registro de Gastos Renovacion correspondiente 
	public static void actualizaEstadoMotivos(final Session session, final int  ACTIVO) {
		
		try {
			String strUpd = " UPDATE o02agpe0.TB_MOTIVOS " +
						        " SET activo = " + ACTIVO +
						        " WHERE codmotivo in " +
					               " (SELECT codmotivo" +
					                  " FROM o02agpe0.tb_inc_incidencias )";						   

			session.createSQLQuery(strUpd).executeUpdate();
		} 
		catch (Exception e) {
			logger.error("Error al actualizar de la tabla de Motivos de Anulación y rescisión ", e);
		}
	}
	
	public static void tratamientoListMotivos(final ListaMotivosDocument listMotivosWS,	final Session session){
		
		int index = 1;
		int reg_update = 0;
		int reg_insert = 0;
		
		logger.debug("Entramos a tratar la lista de Motivos devuelta por el WS");
		int total_reg = listMotivosWS.getListaMotivos().sizeOfMotivoArray();
				
		// Recorremos la lista de asuntos devuelta por el Ws.
		for (index=0; index<total_reg; index++) {

			int codigo_ws = (listMotivosWS.getListaMotivos().getMotivoArray(index).getCodigo());
			
			String descr_ws = (listMotivosWS.getListaMotivos().getMotivoArray(index).getDescriptivo()).trim();
			
			BigDecimal motivoInc = new BigDecimal(0);
			
			try{
			   
				String strSelect ="SELECT codmotivo " +
				   			     " FROM o02agpe0.TB_MOTIVOS " +
							     " WHERE codmotivo = " + codigo_ws + "";
			   
				motivoInc = (BigDecimal) session.createSQLQuery(strSelect).uniqueResult();
				
   			   // Si al hacer la select recupera un valor, quiere decir que existe en la tabla
			   // por lo tanto solo habrá que updatear
			   
			   //if(resultado!=null && resultado.size()>0){
			   // si el registro ya existe (codasunto) en la tabla tb_inc_asuntos, se updatea la tabla para actualizar el 
			   if (motivoInc != null){
				   BBDDIncidListaAsuntos.actualizaEstadoMotivosList(session, IncidenciasListAsuntosConstants.EST_INC_ACTIVO, codigo_ws);
				   reg_update = reg_update + 1;
			   }else{
			   //Si el registro no existe (codasunto) en la tabla tb_inc_asuntos se inserta un nuevo registro con el valor
			   // activo = 1
				   logger.debug("Insertamos nuevo motivo");
				   String strInsert ="INSERT into o02agpe0.TB_MOTIVOS " +
						   			 "VALUES (" + codigo_ws  + ", '" +
						   			              descr_ws + "'" + ",'" +
						   			              IncidenciasListAsuntosConstants.EST_INC_ACTIVO +  
						   			        "')";
				   
				   session.createSQLQuery(strInsert).executeUpdate();
				   reg_insert = reg_insert + 1;
			   }
			    
			}catch (Exception e){
				logger.error("Error al Actualizar la lista de Motivos ", e);
			}
		}
		logger.debug("########################################################");
		logger.debug("## ESTADISTICAS: LISTADO DE MOTIVOS  (BATCH)          ##");
		logger.debug("## ---------------------------------------------------##");
		logger.debug("## Motivos recibidos del Web Serv :   "+ total_reg  +"##");
		logger.debug("## Motivos Actualizados           :   "+ reg_update +"##");
		logger.debug("## Motivos Insertados             :   "+ reg_insert +"##");
		logger.debug("########################################################");
		
	}
	
	// Metodo que actualiza el estado del motivo 
		public static void actualizaEstadoMotivosList(final Session session, final int ACTIVO, final int codigo_ws) {
			logger.debug("Updateamos motivo existente");
			try {
				String strUpdLst = " UPDATE o02agpe0.TB_MOTIVOS " +
							        " SET activo = " + ACTIVO +
							        " WHERE codmotivo = " + codigo_ws;

				session.createSQLQuery(strUpdLst).executeUpdate();
			} 
			catch (Exception e) {
				logger.error("Error al actualizar el registro " + codigo_ws + "de la tabla de Motivos de Anulacion/Rescision", e);
			}
		}
	
	/* Pet.57627 ** MODIF TAM (29.10.2019) FIN */
	
}
