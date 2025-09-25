package com.rsi.agp.batch.cargaPolizasRenovables;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosListaPolizasRenovables;


public class CargaPolizasRenovablesAUX {
	private static final Logger logger = Logger.getLogger(CargaPolizasRenovablesAUX.class);

	
	// ************ //
	// *** MAIN *** //
	// ************ //	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##-------------------------------------------------------##");
			logger.debug("## INICIO BATCH AUXILIAR DE CARGA DE RENOVABLES ## ");
			logger.debug("##-------------------------------------------------------##");
			doWork();
			logger.debug("## FIN BATCH AUXILIAR DE CARGA DE RENOVABLES ## ");
			System.exit(0);
		} catch (Throwable e) {
			
			logger.error("Error en el proceso de Carga Auxiliar de Renovables",e);
			System.exit(1);
		}
	}
	
	// ************** //
	// *** DOWORK *** //
	// ************** //	
	private static void doWork() throws Exception {
		SessionFactory factory;
		Session session     = null;
		factory = getSessionFactory();
		session = factory.openSession();

		try{
			
			RecuperarRegistrosTabAux(session);
			
		} catch (Throwable ex) {		
			logger.error("# Error inesperado en la ejecucion Auxiliar la Carga Polizas Renovables",ex);
			throw new Exception();
		} finally {
			if (session != null) {
				session.close();
			}
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
	
	public static void RecuperarRegistrosTabAux(Session session){		
		logger.debug("INICIO ficheroProcesado");	
		
		int cont = 0;
		
		try {
			String sql = "select t.idpoliza, t.codplan, t.codlinea, t.referencia, t.codmodulo" +
				         " from tb_renovables_aux t where t.xml is null ";
			
			List<?> res = session.createSQLQuery(sql).list();
			
			if (res != null && res.size()>0) {
				// TATIANA : De momento comentamos esto para lanzar pruebas sobre 1 sola poliza.
				for (int i=0;i<res.size();i++){
				
					cont++;

					Object[] registro = (Object[]) res.get(i);
					BigDecimal idPoliza = (BigDecimal)registro[0];
					BigDecimal codPlan = (BigDecimal)registro[1];
					BigDecimal codLinea = (BigDecimal)registro[2];
					String referencia = (String)registro[3];
					String codModulo = (String)registro[4];
					
					Thread.sleep(2000);
					try {
						
						getListAuxPolRenov(session, idPoliza, codPlan, codLinea, referencia, codModulo, cont);
						
					}catch (Exception ex) {
						logger.debug("Error en la llamada al SW");
					}		    
				}
			}

			logger.debug("Elementos obtenidos de la consulta: " + res.size());
			
		} catch (Exception e) {
			logger.debug("Error en ficheroProcesado: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug(" ** ============================================== **");
		logger.debug(" ** FIN DE LA CARGA DE TABLA AUXILIAR RENOVACIONES **");
		logger.debug(" ** VALOR DE REGISTROS TOTALES ACTUALIZADOS: " +cont + "**");
		logger.debug(" ** ============================================== **");
		
	}
	
	public static void ActualizaXMLRegistrosTabAux(Session session ,final Renovacion polizaRen, BigDecimal idPoliza, BigDecimal codPlan, BigDecimal codLinea, String referencia, int cont ){		
		logger.debug("INICIO ActualizaXMLRegistrosTabAux");	
		
		String xml = polizaRen.xmlText();
		
		logger.debug("Valor del xml:"+xml);		

		try {
			String stringQuery = "update tb_renovables_aux t" +
				         " set t.xml= :xml" + 
				         " where t.idpoliza= :idPoliza" +
				         "  and t.codplan= :codPlan" +
				         "  and t.codlinea= :codLinea" +
				         "  and t.referencia= :referencia" ;
			
			logger.debug("Valor del update:"+stringQuery);
						
			Query query = session.createSQLQuery(stringQuery);
		    
			query.setString("xml", xml);
			query.setBigDecimal("idPoliza", idPoliza);
			query.setBigDecimal("codPlan", codPlan);
			query.setBigDecimal("codLinea", codLinea);
			query.setString("referencia", referencia);
			
			query.executeUpdate();

			logger.debug("REGISTRO ACTUALIZADO (referencia: " + referencia + ") nº:" + cont++);
		    
			
		}catch (Exception ex) {
			logger.debug("Error en la actualización (UPDATE):", ex);
		}		    
	}

	protected static void getListAuxPolRenov(Session session, final BigDecimal idPoliza, final BigDecimal plan, final BigDecimal linea, final String referencia, final String modulo, final int cont)	throws Exception {
		URL wsdlLocation = null;
		String url = "";
		
		if (!WSUtils.isProxyFixed())
			WSUtils.setProxy();
	
		try {
			File ficherowsdl = new File(WSUtils.getBundleProp("contratacionRenovacionesWS.wsdl"));
			url = ficherowsdl.getAbsolutePath();
			wsdlLocation = new URL("file:" + url);
		} catch (MalformedURLException ex) {
			throw new Exception("Imposible recuperar el WSDL de Gastos de una poliza Renovable. Revise la Ruta: "+ url, ex);
		} catch (NullPointerException ex) {
			throw new Exception("Imposible obtener el WSDL de Gastos de una poliza Renovable. Revise la Ruta: "+ wsdlLocation.toString(), ex);
		}	
		// PARAMETROS WS
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort 	  = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService  = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsService: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			ContratacionRenovaciones contratRen = (ContratacionRenovaciones) srv.getPort(portName, ContratacionRenovaciones.class);
			WSUtils.addSecurityHeader(contratRen);
			ParametrosListaPolizasRenovablesAgroplus paramListPolReq = new ParametrosListaPolizasRenovablesAgroplus();
	
			// datos fijos
			ObjectFactory obj = new ObjectFactory();
			JAXBElement<String> ref = obj.createParametrosListaPolizasRenovablesReferencia(referencia); 
			
			paramListPolReq.setPlan(plan.intValue());
			paramListPolReq.setLinea(linea.intValue());
			paramListPolReq.setReferencia(ref);			
			
			logger.debug("## CALL WS - LISTA POL REN - PLAN: "+plan.toString() +" LINEA: "+linea.toString() + " REFERENCIA: " + ref + "##");
			Renovacion[] renArr = null;
			
				try {		 
					//Creamos el campo Blob para asignarselo al FicheroUnificado
					
					renArr = getArrayRenovaciones(contratRen, paramListPolReq);
					
					if (renArr != null) {
						for (Renovacion renov : renArr) {
							ActualizaXMLRegistrosTabAux(session, renov, idPoliza, plan, linea, referencia, cont);
						}
					}
					logger.debug("## TOTAL POLIZAS A TRATAR: "+renArr.length+" PLAN: "+plan.toString() +" LINEA: "+linea.toString() +  "##");							
				} catch  (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
					logger.error("Error en WS en agrException: "+ex.getMessage().toString());
					List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
					String errores ="";
					for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error: lstErrores){
						errores = errores + error.getMensaje().toString()+".";
						logger.error("# Error en agrException: "+error.getMensaje().toString());
						break;
					}
				}
			
			
		} catch  (Exception ex) {		
			logger.error("# Error global en WS Exception: "+ex.getMessage().toString(),ex);
		}	
	}
				
	/**
	 * Devuelve la fechaDesde recibida como parï¿½metro formateada para la llamada al SW
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaDesde (int ano,int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(ano, mes, dia, DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionDesde(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaDesde para la llamada al SW", e);
		}
		
		return fecFormateada;
	}
				
	/**
	 * Devuelve la fechaHasta recibida como parï¿½metro formateada para la llamada al SW
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaHasta (int ano,int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(ano, mes, dia, DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionHasta(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaHasta para la llamada al SW", e);
		}
		
		return fecFormateada;
	}
	
	/**
	 * Realiza la llamada al SW y devuelve el array de objetos Renovacion obtenido
	 * @param contratRen
	 * @param paramListPolReq
	 * @return
	 * @throws AgrException
	 * @throws XmlException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Renovacion[] getArrayRenovaciones (ContratacionRenovaciones contratRen, ParametrosListaPolizasRenovables paramListPolReq) throws AgrException, UnsupportedEncodingException, XmlException {
		return ListaPolizasRenovablesDocument.Factory.parse(
				new String (contratRen.listaPolizasRenovables(paramListPolReq).getPolizasRenovables().getValue(), "UTF-8")).getListaPolizasRenovables().getRenovacionArray();
	}


}