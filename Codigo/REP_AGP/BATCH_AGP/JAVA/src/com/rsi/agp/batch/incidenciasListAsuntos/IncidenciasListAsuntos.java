package com.rsi.agp.batch.incidenciasListAsuntos;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import es.agroseguro.listaAsuntos.ListaAsuntosDocument;
/* Pet. 57627 ** MODIF TAM (29.10.2019) */
import es.agroseguro.listaMotivosAnulacionRescision.ListaMotivosDocument;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ContratacionSCImpresionModificacion;


public class IncidenciasListAsuntos {
	private static final Logger logger = Logger.getLogger(IncidenciasListAsuntos.class);

	
	// ************ //
	// *** MAIN *** //
	// ************ //	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##---------------------------------------------------------##");
			logger.debug("## INICIO BATCH RECUPERAR LISTA DE ASUNTOS y MOTIVOS  V.02 ##");
			logger.debug("##---------------------------------------------------------##");
			doWork();
			logger.debug("## FIN BATCH RECUPERAR LISTA DE ASUNTOS y MOTIVOS ## ");
			System.exit(0);
		} catch (Throwable e) {
			
			logger.error("Error en el proceso de Carga Lista de Asuntos y Motivos",e);
			System.exit(1);
		}
	}
	
	// ************** //
	// *** DOWORK *** //
	// ************** //	
	private static void doWork() throws Exception {
		Session session     = null;
		
		session = getSessionFactory().openSession();

		ListaAsuntosDocument incidListRes = null;
		ListaMotivosDocument ListMotivRes = null;
			
		try{
			// Crea el objeto para las llamadas al SW
			ContratacionSCImpresionModificacion objWS = IncidenciasListAsuntosWS.getObjWSList();
		    
			try {
				logger.debug("Recuperamos la lista de Asuntos");
				incidListRes = IncidenciasListAsuntosWS.getListaAsuntos(session, objWS);
				logger.debug("RESPUESTA WS: "+ incidListRes.toString());
				
				logger.debug("Recuperamos la lista de Motivos");
				ListMotivRes = IncidenciasListAsuntosWS.getListaMotivos(session, objWS);
				logger.debug("RESPUESTA WS: "+ ListMotivRes.toString());
			} 
			catch (Exception ex) {
				
				logger.debug("Error en la llamada al SW de Asuntos y Motivos");
				
			}		    
			
		} catch (Throwable ex) {
			logger.error("Error inesperado en la ejecucion del WS que devuelve la lista de Asuntos y Motivos", ex);
			throw new Exception();
		} finally {
			if (session != null) {
				//session.close();
			}
		} 
		// si el WS ha retornado correctamente la lista de Asuntos.
		if (incidListRes !=null) {
			
			// Borramos aquellos registros de la tabla de tb_inc_asuntos que no tengan su correspondencia en la tabla tb_inc_incidencias
			BBDDIncidListaAsuntos.borrarAsuntos(session);
			
			//Actualizamos el estado de aquellos registros de la tabla tb_inc_asuntos que si tienen su correspondencia en la tabla tb_inc_incidencias
			// con el valor ACTIVO = 0
			BBDDIncidListaAsuntos.actualizaEstadoAsuntos(session, IncidenciasListAsuntosConstants.EST_INC_NO_ACTIVO);
			
			//Actualizamos la lista de Inc. Asuntos con los valores obtenidos de la llamada al WS.
			BBDDIncidListaAsuntos.tratamientoListIncAsuntos(incidListRes, session);
			
		}
		
		// si el WS ha retornado correctamente la lista de Motivos.
		if (ListMotivRes !=null) {
					
			// Borramos aquellos registros de la tabla de TB_MOTIVOS que no tengan su correspondencia en la tabla tb_inc_incidencias
			BBDDIncidListaAsuntos.borrarMotivos(session);
			
			//Actualizamos el estado de aquellos registros de la tabla TB_MOTIVOS que si tienen su correspondencia en la tabla tb_inc_incidencias
			// con el valor ACTIVO = 0
			BBDDIncidListaAsuntos.actualizaEstadoMotivos(session, IncidenciasListAsuntosConstants.EST_INC_NO_ACTIVO);
			
			//Actualizamos la lista de Motivos con los valores obtenidos de la llamada al WS.
			BBDDIncidListaAsuntos.tratamientoListMotivos(ListMotivRes, session);
			
		}
	}
	
	
	private static SessionFactory getSessionFactory() {
			SessionFactory factory;
			try {
				Configuration cfg = new Configuration();
				cfg.configure();

				factory = cfg.buildSessionFactory();
				logger.error("Salimos del getSessionFactory");

			} catch (Throwable ex) {
				logger.error("Error al crear el objeto SessionFactory." + ex);
				throw new ExceptionInInitializerError(ex);
			}
			return factory;
	}
		
	
	protected static class IncidListAsuntosBean<codigo> {

		private String codigo;
		private String descripcion;

		public IncidListAsuntosBean(final String codigo, final String descripcion) {

			this.codigo = codigo;
			this.descripcion = descripcion;
		}

		public String getCodigo() {
			return codigo;
		}

		public String getDescripcion() {
			return descripcion;
		}
		
		public void setCodigo(final String codigo) {
			this.codigo = codigo;
		}
		
		public void setDescripcion(final String descripcion) {
			this.descripcion = descripcion;
		}

		@Override
		public String toString() {
			StringBuffer listInc = new StringBuffer();
			listInc.append(this.codigo).append("/").append(this.descripcion);
			return listInc.toString();
		}
	}

	
}