package com.rsi.agp.batch.renovables;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.tipos.Gastos;

public class GastosRenovables {

	private static final Logger logger = Logger.getLogger(GastosRenovables.class);

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("#------------------------------------------#");
			logger.debug ("INICIO Batch Envio Gastos polizas renovables");
			logger.debug("#------------------------------------------#");
			doWork();
			logger.debug("#------------------------------------------#");
			logger.debug("FIN Batch Envio Gastos polizas renovables");
			logger.debug("#------------------------------------------#");
			System.exit(0);
		} catch (Throwable e) {
			logger.error("Error en el proceso de Envio Gastos polizas renovables",e);
			System.exit(1);
		}
	}

	private static void doWork() throws Exception {

		Session session     = null;
		
		try {
			session = getSessionFactory().openSession();
			
			// Actualiza los gastos de renovacion con estado "Gastos asignados"(2)  a "Pendiente de confirmar"(3).
			BBDDPolRenUtil.actualizaEstadoPolRenPendientes(session);
			
			// Carga el listado de registros de gastos de renovacion en estado "Pendiente de confirmar"(3)
			List<GastosRenovacion> listGastosRnv = BBDDPolRenUtil.getGastosRenovacionPtes(session);
			
			// Crea la listas para almacenar los registros enviados y erroneos, de cara al correo de resumen
			List<PolizaOKBean> refPolizasOK = new ArrayList<PolizaOKBean>();
			List<PolizaKOBean> refPolizasKO = new ArrayList<PolizaKOBean>();
			
			if (listGastosRnv.size() == 0) {
				logger.info("### NO HAY GASTOS DE RENOVACION PARA ENVIAR"+" ###");
			} else {
				logger.info("### GASTOS DE RENOVACION PARA ENVIAR :" + listGastosRnv.size()+" ###");
				
				// Crea el objeto para las llamadas al SW
				ContratacionRenovaciones objWs = GastosPolizaRenovableWS.getObjetoWs();
				
				// Obtiene el numero maximo de reintentos en la llamda a SW 
				Integer reintentos = getNumMaxReintentos();
				
				// Procesa los registros de gastos de renovacion obtenidos de BBDD
				for (GastosRenovacion gasRen : listGastosRnv) {
					
					Boolean resWS 	    = false;
					Boolean insertadoKO = false;
					
					// Probado con el gasto renovacion con id 1553
					
					// Obtiene los datos de la poliza renovable asociada al gasto de renovacion que se esta procesando
					PolizaRenovable plzRen = BBDDPolRenUtil.getDatosPlzRenovable(gasRen.getId(), session);
					
					logger.debug("#------------------------------------------#");
					logger.debug("TRATANDO EL GASTO DE RENOVACION: ID: "+gasRen.getId()+" REF: " + plzRen.getReferencia());
					
					// Insertas en el historico de la poliza renovable la modificacion del gasto de renovacion asociado
					BBDDPolRenUtil.actualizarHistorico(plzRen.getId(), plzRen.getEstadoRenovacionAgroseguro().getCodigo().intValue(), gasRen.getEstadoRenovacionAgroplus().getCodigo().intValue(), gasRen.getGrupoNegocio(), session);					
					
					// Se valida el registro de gastos de renovacion
					PolizaKOBean polValidaKO = isValidGastoRenovacion(gasRen, plzRen);									
							 
				    // Si el registro supera las validaciones llamamos al servicio web
					if (polValidaKO == null){ 
						try {
							resWS = GastosPolizaRenovableWS.getGastosPolizaRenovable(plzRen.getReferencia(),plzRen.getPlan(), gasRen, session, objWs, reintentos);
							logger.debug("RESPUESTA WS: "+ resWS.toString());
						} 
						catch (Exception ex) {
							
							logger.debug("Error en la llamada al SW");
							
							// Anade el gasto a la lista de erroneos
							PolizaKOBean polKo = new PolizaKOBean(plzRen.getPlan().toString(), plzRen.getLinea().toString(),
									 plzRen.getReferencia(), "", gasRen.getGrupoNegocio(), ex.getMessage().toString() );
							refPolizasKO.add(polKo);
							
							// Actualizamos el estado de la poliza a KO
							BBDDPolRenUtil.actualizaEstadoById(gasRen.getId(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA,ex.getMessage().toString(),session);
							
							// Actualizamos en historico a KO
							BBDDPolRenUtil.actualizarHistorico(plzRen.getId(),plzRen.getEstadoRenovacionAgroseguro().getCodigo().intValue(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA, gasRen.getGrupoNegocio(), session);
							
							insertadoKO = true;
							resWS = false;
						}
						 
						
						if (resWS) { // POLIZA OK
							
							logger.debug("Respuesta del SW: gasto enviado correcto");
							
							// Anade el gasto a la lista de correctos
							PolizaOKBean polOk = new PolizaKOBean(plzRen.getPlan().toString(), plzRen.getLinea().toString(), 
																  plzRen.getReferencia(), "", gasRen.getGrupoNegocio(), "ENVIO OK");
							refPolizasOK.add(polOk);
							
							// Actualizamos el estado de la poliza a OK
							BBDDPolRenUtil.actualizaEstadoById(gasRen.getId(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_CORRECTA,"",session);
							
							// Actualizamos en historico a OK
							BBDDPolRenUtil.actualizarHistorico(plzRen.getId(),plzRen.getEstadoRenovacionAgroseguro().getCodigo().intValue(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_CORRECTA, gasRen.getGrupoNegocio(), session);
							
						} 
						else {
							if (!insertadoKO) {
								logger.debug("Respuesta del SW: gasto enviado incorrecto");
								
								// Anade el gasto a la lista de erroneos
								PolizaKOBean polKo = new PolizaKOBean(plzRen.getPlan().toString(), plzRen.getLinea().toString(),
										 plzRen.getReferencia(), "", gasRen.getGrupoNegocio(), GastosRenovablesConstants.DESC_ENVIADA_ERRONEA);
								refPolizasKO.add(polKo);
								
								// Actualizamos el estado de la poliza a KO
								BBDDPolRenUtil.actualizaEstadoById(gasRen.getId(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA,GastosRenovablesConstants.DESC_ENVIADA_ERRONEA, session);
								
								// Actualizamos en historico a KO
								BBDDPolRenUtil.actualizarHistorico(plzRen.getId(),plzRen.getEstadoRenovacionAgroseguro().getCodigo().intValue(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA, gasRen.getGrupoNegocio(), session);
							}
						}

					} 
					// Si el registro no supera las validaciones
					else {  
						 
						 // Anade el gasto a la lista de erroneos
						 refPolizasKO.add(polValidaKO);
						 
						 // Actualizamos el estado del gasto de renovacion a KO
						 BBDDPolRenUtil.actualizaEstadoById(gasRen.getId(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA,GastosRenovablesConstants.DESC_ENVIADA_ERRONEA,session);
						
						 // Actualizamos en historico a KO
						 BBDDPolRenUtil.actualizarHistorico(plzRen.getId(),plzRen.getEstadoRenovacionAgroseguro().getCodigo().intValue(),GastosRenovablesConstants.EST_AGPLUS_ENVIADA_ERRONEA, gasRen.getGrupoNegocio(), session);
					 }	 
					
				}
				
				logger.info("### POLIZAS ENVIADAS CORRECTAMENTE: "+ refPolizasOK.size()+" ###");
				logger.info("### POLIZAS SIN ENVIAR POR ERROR: "+ refPolizasKO.size()+" ###");
			}

			generateAndSendMail(refPolizasOK, refPolizasKO, session);

		} catch (Throwable ex) {
			logger.error("Error inesperado en la ejecucion del envio de gastos de polizas renovables", ex);
			throw new Exception();
		} finally {
			if (session != null) {
				session.close();
			}
		} 
	}

	/**
	 * Obtiene del properties el numero maximo de reintentos de llamadas al SW para un mismo registro
	 * @return
	 */
	private static Integer getNumMaxReintentos() {
		
		Integer reintentos = 1;
		
		try {
			
			ResourceBundle bundle = ResourceBundle.getBundle("agp_gastos_renovacion");
			String strReintentos = bundle.getString("reintentos");
			if (!strReintentos.equals(""))	reintentos = Integer.parseInt(strReintentos);
		} 
		catch (Exception e) {
			logger.error("Error al obtener el numero maximo de reintentos", e);
		}
		
		logger.debug("Numero maximo de reintentos configurado a " + reintentos);
		return reintentos;
	}
	
	
	/**
	 * Comprueba si el registro de gastos de renovacion recibido como parametro superas las validaciones previas al envio al SW
	 * @param gasRen Registro de gastos de renovacion 
	 * @return Null si el registro pasa las validaciones y un objeto de tipo PolizaKOBean con los datos del error si no las pasa.
	 */
	private static PolizaKOBean isValidGastoRenovacion (GastosRenovacion gasRen, PolizaRenovable plzRen) {
		
		 boolean valPrimera  = false;
		 boolean valSegunda  = false;
		 
		 logger.debug("  Datos de gastos de renovacion: ref: " + getGastosLogs(gasRen));
		 BigDecimal zero         =  new BigDecimal(0);
		 BigDecimal comMediador  =  gasRen.getComisionMediador();
		 BigDecimal gastosAdq    =  gasRen.getAdquisicion() == null ? zero : gasRen.getAdquisicion();
		 BigDecimal gastosAdm    =  gasRen.getAdministracion() == null ? zero : gasRen.getAdministracion();
		 Character grupoNegocio  =  gasRen.getGrupoNegocio();
		 
		 // Primera validacion: que el gasto de renovacion tenga asignado comMediador >=0, y adquisicion y/o administracion >0)
		 if (null != comMediador && (comMediador.compareTo(zero) == 0 || comMediador.compareTo(zero) == 1)){
			 if ((gastosAdq.compareTo(zero) == 1) || (gastosAdm.compareTo(zero) == 1))
				 valPrimera = true;
		 }
		 
		 // Si supera la primera validacion realiza la segunda, que comprueba que la suma de % adm ,% adq y % comMed asociadas no superen el 90%
		 if (valPrimera) {
			BigDecimal sumaPcts = gastosAdq.add(gastosAdm).add(comMediador);						
			valSegunda = sumaPcts.compareTo(new BigDecimal(90))<1;
			logger.debug("  # Suma de comisiones (COMMED + ADQ + ADM): " + sumaPcts+" #");
		 }
		 
		 // Si ha superado las dos validaciones
		 if ((valPrimera) && (valSegunda)) {
			 logger.debug("El registro supera las validaciones");
			 // Devuelve el objeto PolizaKOBean a nulo para indicar que el gasto de renovacion es correcto
			 return null;
		 }
		 // Si no ha superado alguna de las validacions
		 else {
			 logger.debug("El registro NO supera las validaciones");
			 
			 PolizaKOBean polKo = new PolizaKOBean(plzRen.getPlan().toString(), plzRen.getLinea().toString(),
					 plzRen.getReferencia(), "", grupoNegocio, "No cumple las validaciones" );
			 if (!valPrimera){
				 logger.debug("## POLIZA " + plzRen.getReferencia() + " GASTOS INCOMPLETOS. ## ");
				 polKo.setDescripcion("Gastos incompletos");
			 }else {
				 logger.debug("## POLIZA " + plzRen.getReferencia() + " LA SUMA DE PORCENTAJES ES SUPERIOR A 90 ##");
				 polKo.setDescripcion("La suma de porcentajes es superior a 90");
			 }				
			 
			 return polKo;
		 }		
		
	}
	

	private static SessionFactory getSessionFactory() {
		SessionFactory factory;
		try {
			Configuration cfg = new Configuration();
			cfg.configure();

			factory = cfg.buildSessionFactory();

		} catch (Throwable ex) {
			logger.error("Error al crear el objeto SessionFactory." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		return factory;
	}
	
	
	/**
	 * Devuelve un String de gastos de renovacion de la poliza
	 */
	protected static String  getGastosLogs(com.rsi.agp.dao.tables.renovables.GastosRenovacion gRen){
		StringBuilder result = new StringBuilder("");
		if (gRen!= null) {
			result.append(gRen.getAdquisicion()!=null?"  Adq: " + gRen.getAdquisicion().toString():" Adq: NULL");
			result.append(gRen.getAdministracion()!=null?"  Adm: " + gRen.getAdministracion().toString():" Adm: NULL");
			result.append(gRen.getComisionMediador()!=null?"  comMed: " + gRen.getComisionMediador().toString():" comMed: NULL");
			result.append(gRen.getGrupoNegocio()!=null?"  GruN: " + gRen.getGrupoNegocio().toString():" GruN: NULL");
		}	
		return result.toString();
	}
	
	/**
	 * Devuelve un String de gastos de renovacion enviados a WS de la poliza
	 */
	protected static String  getGastosWSLogs(Gastos gas){
		StringBuilder result = new StringBuilder("");
		if (gas!= null) {
			result.append(gas.getAdquisicion()!=null?"  Adq: " + gas.getAdquisicion().toString():" Adq: NULL");
			result.append(gas.getAdministracion()!=null?"  Adm: " + gas.getAdministracion().toString():" Adm: NULL");
			result.append(gas.getComisionMediador()!=null?"  comMed: " + gas.getComisionMediador().toString():" comMed: NULL");
			result.append(gas.getGrupoNegocio()!=null?"  GruN: " + gas.getGrupoNegocio().toString():" GruN: NULL");
		}	
		return result.toString();
	}
	
	private static void generateAndSendMail(
			final List<PolizaOKBean> refPolizasOK,
			final List<PolizaKOBean> refPolizasKO, final Session session) {

		String grupo;
		String asunto;
		StringBuffer msg;
		int contPolizas = 1;
		/*
		mail.asunto=Resumen de gastos comunicados a Agroseguro el dia  
		mail.noPolizas=No hay polizas pendientes de importacion.
		mail.totalPolizas=Total de polizas marcadas para enviar:
		mail.polizasOK=Polizas enviadas correctamente:
		mail.polizasKO=Polizas erroneas:  
		mail.footer=Saludos cordiales. 
		*/
		ResourceBundle bundle = ResourceBundle.getBundle("agp_gastos_renovacion");

		grupo = bundle.getString("mail.grupo");

		asunto = bundle.getString("mail.asunto")
				+ new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		msg = new StringBuffer();

		msg.append(System.getProperty("line.separator"));
		if (refPolizasOK.size() + refPolizasKO.size() > 0) {

			Comparator<PolizaOKBean> comparator = new Comparator<PolizaOKBean>() {

				@Override
				public int compare(PolizaOKBean arg0, PolizaOKBean arg1) {

					int comparison = 0;
					comparison = arg0.getPlan().compareTo(arg1.getPlan());
					if (comparison == 0) {
						comparison = arg0.getLinea().compareTo(arg1.getLinea());
						if (comparison == 0) {
							comparison = arg0.getReferencia().compareTo(
									arg1.getReferencia());
						}
					}
					return comparison;
				}

			};

			msg.append(bundle.getString("mail.totalPolizas"));
			msg.append(" ");
			msg.append(refPolizasOK.size() + refPolizasKO.size());
			msg.append(System.getProperty("line.separator"));
			msg.append("    ");
			msg.append(bundle.getString("mail.polizasOK"));
			msg.append(" ");
			msg.append(refPolizasOK.size());
			/*
			Collections.sort(refPolizasOK, comparator);
			for (PolizaOKBean polizaOK : refPolizasOK) {
				msg.append(System.getProperty("line.separator"));
				msg.append("\t- ");
				msg.append(polizaOK.toString());
			}
			*/
			//msg.append(System.getProperty("line.separator"));
			msg.append(System.getProperty("line.separator"));
			msg.append("    ");
			msg.append(bundle.getString("mail.polizasKO"));
			msg.append(" ");
			msg.append(refPolizasKO.size());
			Collections.sort(refPolizasKO, comparator);
			for (PolizaKOBean polizaKO : refPolizasKO) {
				if (contPolizas<300) {
					msg.append(System.getProperty("line.separator"));
					msg.append("        ");
					msg.append(polizaKO.toString());
				}
				contPolizas++;
			}
			if (contPolizas>299) {
				msg.append(System.getProperty("line.separator"));	
				msg.append("            ");
				msg.append("[...]");
			}
		} else {
			msg.append(bundle.getString("mail.noPolizas"));
		}
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		msg.append(bundle.getString("mail.footer"));
		msg.append(System.getProperty("line.separator"));
		
		logger.info("Mensaje a mandar: "+ asunto.toString());
		logger.info(msg.toString());
		
		Query query = session
				.createSQLQuery(
						"CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreo(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", grupo).setParameter("asunto", asunto)
				.setParameter("mensaje", msg.toString());

		query.executeUpdate();
	}

	protected static class PolizaOKBean {

		private String plan;
		private String linea;
		private String referencia;
		private String modulo;
		private Character grupoNegocio;

		public PolizaOKBean(final String plan, final String linea,
				final String referencia, final String modulo, final Character grupoNegocio) {

			this.plan = plan;
			this.linea = linea;
			this.referencia = referencia;
			this.modulo = modulo;
			this.grupoNegocio = grupoNegocio;
		}

		public String getPlan() {
			return plan;
		}

		public String getLinea() {
			return linea;
		}

		public String getReferencia() {
			return referencia;
		}

		public String getModulo() {
			return modulo;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.plan).append("/").append(this.linea).append(" - ").append(this.referencia).append(" - GN: ").append(this.grupoNegocio);
			return sb.toString();
		}
	}

	protected static class PolizaKOBean extends PolizaOKBean {

		private String descripcion;

		public PolizaKOBean(final String plan, final String linea,
				final String referencia, final String modulo, final Character grupoNegocio,
				final String descripcion) {

			super(plan, linea, referencia, modulo, grupoNegocio);
			this.descripcion = descripcion;
		}

		public String getDescripcion() {

			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		@Override
		public String toString() {

			StringBuffer sb = new StringBuffer(super.toString());
			sb.append(": ");
			sb.append(this.descripcion);
			return sb.toString();
		}
	}
}