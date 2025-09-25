package com.rsi.agp.batch.updateEstadosPolRenovables;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.util.WSUtils;

import es.agroseguro.estadoRenovacion.Renovacion;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovables;
import es.agroseguro.listaPolizasRenovables.ListaPolizasRenovablesDocument;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ContratacionRenovaciones_Service;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ListaPolizasRenovablesResponse;
import es.agroseguro.serviciosweb.contratacionrenovaciones.ObjectFactory;

public final class UpdateEstadosPolRenovablesWS {
	private static final Logger logger = Logger.getLogger(UpdateEstadosPolRenovablesWS.class);
	
	private UpdateEstadosPolRenovablesWS() {
	}
	
	protected static ContratacionRenovaciones getObjetoWs()	throws Exception {
		ContratacionRenovaciones contratRen = null;
		
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
		
		String wsLocation = WSUtils.getBundleProp("contratacionRenovacionesWS.location");
		String wsPort 	  = WSUtils.getBundleProp("contratacionRenovacionesWS.port");
		String wsService  = WSUtils.getBundleProp("contratacionRenovacionesWS.service");
		QName serviceName = new QName(wsLocation, wsService);
		QName portName    = new QName(wsLocation, wsPort);
		
		// LOGS PARAMETROS WS
		logger.debug("wsdlLocation: " + wsdlLocation.toString());
		logger.debug("wsLocation: " + wsLocation.toString());
		logger.debug("wsPort: " + wsPort.toString());
		logger.debug("wsServicee: " + wsService.toString());
		
		try {
			ContratacionRenovaciones_Service srv = new ContratacionRenovaciones_Service(wsdlLocation, serviceName);
			contratRen = (ContratacionRenovaciones) srv.getPort(portName, ContratacionRenovaciones.class);
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		return contratRen;
	}
	
	protected static List<Renovacion> getListPolizasRenovablesWS(final Long planActual, final List<BigDecimal> lstLineasSW,
			final Session session, final String estCallWs, final ContratacionRenovaciones objWs, final boolean conFechas,
			final Boolean modificarDias) throws Exception {

		List<Renovacion> lstRenovGlobal = new ArrayList<Renovacion>();
		int contPolizas = 0;
		int contPolizasTemp = 0;
		int f;
		int ds;
		int diasTemp = 0;
		Base64Binary res = null;
		Integer reintentos = null;
		Integer divisionMes = null;
		ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_plz_renovables");
		
		String strReintentos  = bundle.getString("reintentos");
		String strDivisionMes = bundle.getString("divisionMes");
		
		if (!strReintentos.equals(""))
			reintentos = Integer.parseInt(strReintentos);
		//logger.debug("# Reintentos por timeOut: "+reintentos);
		if (!strDivisionMes.equals("")){
			divisionMes = Integer.parseInt(strDivisionMes);
			diasTemp = 28/divisionMes;
		}else{
			logger.debug("# sin parametro divisionMes #");
			System.exit(0);
		}
		
		logger.debug("# divisionMes: "+divisionMes);
		try {
			WSUtils.addSecurityHeader(objWs);
			ParametrosListaPolizasRenovablesAgroplus paramListPolReq = new ParametrosListaPolizasRenovablesAgroplus();

			paramListPolReq.setPlan(planActual.intValue());
			List<JAXBElement<List<BigInteger>>> lstEstados = new ArrayList<JAXBElement<List<BigInteger>>>();
			List<BigInteger> lstBig = new ArrayList<BigInteger>();
			lstBig.add(new BigInteger(estCallWs));
			for (BigDecimal linea: lstLineasSW) {
				// recorremos la lista de lineas y llamamos por cada una de ellas al SW de ListaPolizasRenovables
				
				Calendar cDesde = Calendar.getInstance();
				Calendar cHasta = Calendar.getInstance();
				Calendar cHastaT = Calendar.getInstance();

				Calendar cIni   = Calendar.getInstance();
				Calendar cFinal = Calendar.getInstance();
				  
				/* ESC-7428 ** MODIF TAM (21.10.2019)*/
				/* Logs */

				  String dia, mes, annio;
					
				  dia = Integer.toString(cDesde.get(Calendar.DATE));
				  mes = Integer.toString(cDesde.get(Calendar.MONTH));
				  annio = Integer.toString(cDesde.get(Calendar.YEAR));
				  logger.debug("Valor de cDesde (Inicial):" + dia + "/" + mes +"/" + annio);   
			   
			      dia = Integer.toString(cHasta.get(Calendar.DATE));
			      mes = Integer.toString(cHasta.get(Calendar.MONTH));
			      annio = Integer.toString(cHasta.get(Calendar.YEAR));
			      logger.debug("Valor de cHasta (Inicial):" + dia + "/" + mes +"/" + annio);
				
			      dia = Integer.toString(cHastaT.get(Calendar.DATE));
			      mes = Integer.toString(cHastaT.get(Calendar.MONTH));
			      annio = Integer.toString(cHastaT.get(Calendar.YEAR));
			      logger.debug("Valor de cHastaT (Inicial):" + dia + "/" + mes +"/" + annio);
			      
			      dia = Integer.toString(cIni.get(Calendar.DATE));
			      mes = Integer.toString(cIni.get(Calendar.MONTH));
			      annio = Integer.toString(cIni.get(Calendar.YEAR));
			      logger.debug("Valor de cIni (Inicial):" + dia + "/" + mes +"/" + annio);

			      dia = Integer.toString(cFinal.get(Calendar.DATE));
			      mes = Integer.toString(cFinal.get(Calendar.MONTH));
			      annio = Integer.toString(cFinal.get(Calendar.YEAR));
			      logger.debug("Valor de cFinal (Inicial):" + dia + "/" + mes +"/" + annio);  
				  
			      /* FIN ESC-7428 ** MODIF TAM (21.10.2019)*/
				  
				boolean primerCiclo    = true;
				boolean primerCicloFin = true;
				boolean salirPrimerciclo = true;
				Long meses = new Long(0);
				String fecInicio = "";
				if (conFechas) {
					try{
						fecInicio = BBDDUpdateEstadosPolRenUtil.getFechaInicioUpdatePolRenovables(session);
					} catch (Exception ex) {
						logger.error("# Se ha producido un error al obtener la fecha de Inicio, ",ex);
					}
					// MODIF TAM (29.11.2018) ESC-4581 ** Inicio //
					// Modificamos la fecha para obtenerla de un nuevo paramaetro de la tabla tb_config_agp
					// ya que de esta forma podremos cambiar el valor de la fecha de inicio desde BBDD.
					
					//cDesde.set(2015, Calendar.MAY, 1); // poner 2015 mayo
					//meses = getDiffDates(cDesde.getTime(), cHasta.getTime(), 1);
					//logger.debug("## Meses entre JUNIO 2015 AL ACTUAL: " + meses+" ##");
					//cHasta.set(2015, Calendar.JUNE, 1); //poner 2015 junio
					//cHastaT.set(2015, Calendar.JUNE, 1); //poner 2015 junio
					logger.debug("Despues de haber recuperado la fecha de inicio:"+fecInicio);
					
					int diaInicio = Integer.parseInt(fecInicio.substring(0,2));
					int mesInicio = Integer.parseInt(fecInicio.substring(3,5));
					int annoInicio = Integer.parseInt(fecInicio.substring(6,10));
					
					int mesInicioDesde = mesInicio - 1;
					
					cDesde.set(annoInicio, mesInicioDesde, diaInicio);
					meses = getDiffDates(cDesde.getTime(), cHasta.getTime(), 1);
					cHasta.set(annoInicio, mesInicio, diaInicio); //poner 2018 Junio
					cHastaT.set(annoInicio, mesInicio, diaInicio); //poner 2018 Junio
					// MODIF TAM (29.11.2018) ESC-4581 ** Fin //
					
					// ESC-6635 ** Se amplia el num de meses a 4 //
					//meses = meses +3; // para que haga 2 meses mas a partir de la fecha actual
					meses = meses +4; // para que haga 2 meses mas a partir de la fecha actual
					logger.debug("## Num de Meses:"+ meses);
					// ESC-6635 ** Se amplia el num de meses a 4 * Fin//
					
				}else {
					meses = new Long(1);
				}
				//meses = new Long(3); // SOLO PARA PRUEBAS!!!!!!!!
				logger.debug("## PLAN: "+planActual.toString() +" LINEA: "+linea.toString()+" ##");
				for(f=0;f<meses;f++) {				
					//if (lstRenovGlobal.size()>0)
					//   break;
					boolean primeraDia = true;
					boolean salirMes = false;
					if (salirPrimerciclo){
						cHastaT.add(Calendar.DATE, cIni.get(Calendar.DAY_OF_MONTH)-1);
					}else{						
						cHastaT.set(cHasta.get(Calendar.YEAR), cHasta.get(Calendar.MONTH)-1, cFinal.get(Calendar.DAY_OF_MONTH));
					}
					
					for(ds=1;ds<divisionMes+1;ds++) {
						//logger.debug("# ds : "+ds+ " #");
						lstEstados.clear();
						JAXBElement<XMLGregorianCalendar> fecDesde = null;
						JAXBElement<XMLGregorianCalendar> fecHasta = null;
						StringBuffer msg = new StringBuffer();
						contPolizasTemp = 0;
						if (conFechas) {					
							if (primerCiclo) {
								fecDesde = getFechaDesde(cDesde.get(Calendar.YEAR),cHasta.get(Calendar.MONTH)+1,1); // PRIMERA VEZ
								primerCiclo = false;
							}else {
								if (primeraDia){
									primeraDia = false;
								}else{
									cIni.add(Calendar.DATE,diasTemp);
								}
								fecDesde = getFechaDesde(cHastaT.get(Calendar.YEAR),cHastaT.get(Calendar.MONTH)+1,cHastaT.get(Calendar.DAY_OF_MONTH));
								//logger.debug("# "+fecDesde.getValue()+ " #");
							}					
							if (fecDesde != null) {
								//logger.debug("## Fecha renovacion Desde: " + fecDesde.getValue()+" ##");
								paramListPolReq.setFechaRenovacionDesde(fecDesde);
							}					
							if (f == meses-1) {
								if (modificarDias){ // casos  -8- PRECARTERA_PRECALCULADA se busca hasta 90 dias siguientes a la fecha actual
									cHastaT.add(Calendar.DATE, 30);
									cFinal.add(Calendar.DATE, 30); 
								}else{  // resto de casos -> +83
									cHastaT.add(Calendar.DATE, 23);
									cFinal.add(Calendar.DATE, 23);
								}
								fecHasta = getFechaHasta(cHastaT.get(Calendar.YEAR),cHastaT.get(Calendar.MONTH)+1,cHastaT.get(Calendar.DAY_OF_MONTH));
								salirMes = true;
							}else {
								if (primerCicloFin){
									fecHasta = getFechaHasta(cHasta.get(Calendar.YEAR),cHasta.get(Calendar.MONTH)+1,cFinal.get(Calendar.DAY_OF_MONTH));
									primerCicloFin = false;
								}else{
									if (ds == divisionMes){
										 /* ESC-7428 ** MODIF TAM (21.10.2019) 
										  * La fecha Hasta no se calcula correctamente los 3 primeros dias del mes, ya que al sumar los
										  * 28 dias no pasa de mes y por tanto no se recalcula correctamente.
										  */
										  dia = Integer.toString(cHastaT.get(Calendar.DATE));
									      mes = Integer.toString(cHastaT.get(Calendar.MONTH));
									      annio = Integer.toString(cHastaT.get(Calendar.YEAR));
									      logger.debug("Valor de cHastaT (1.1):" + dia + "/" + mes +"/" + annio);  
										
									    int dia_aux = cHastaT.get(Calendar.DATE);
							  		      
							  		    if (dia_aux <= 3)  {
							  		       int diasTemp_aux = diasTemp + 3;  
							  		       cHastaT.add(Calendar.DATE,diasTemp_aux);  
							  		       logger.debug("Anhadimos: " + diasTemp_aux + " dias a la fecha cHastaT");
							  		    }else if (dia_aux > 3) {
							  		       cHastaT.add(Calendar.DATE,diasTemp);
							  		       logger.debug("Anhadimos: " + diasTemp + " dias a la fecha cHastaT");
							  		    }
							  		    
										  dia = Integer.toString(cHastaT.get(Calendar.DATE));
									      mes = Integer.toString(cHastaT.get(Calendar.MONTH));
									      annio = Integer.toString(cHastaT.get(Calendar.YEAR));
									      logger.debug("Valor de cHastaT (1.1):" + dia + "/" + mes +"/" + annio);  

							  		    /* ESC-7428 ** MODIF TAM (21.10.2019) Fin */
							  		    
										fecHasta = getFechaHasta(cHastaT.get(Calendar.YEAR),cHastaT.get(Calendar.MONTH)+1,cFinal.get(Calendar.DAY_OF_MONTH));
										logger.debug("Valor de fecHasta:" +fecHasta.getValue().toString());
									}else{
										cHastaT.add(Calendar.DATE,diasTemp);
										int mm = cHastaT.get(Calendar.MONTH);
										if (mm == 0)
											fecHasta = getFechaHasta(cHastaT.get(Calendar.YEAR),cHastaT.get(Calendar.MONTH)+1,cHastaT.get(Calendar.DAY_OF_MONTH));
										else
											fecHasta = getFechaHasta(cHastaT.get(Calendar.YEAR),cHastaT.get(Calendar.MONTH)+1,cHastaT.get(Calendar.DAY_OF_MONTH));										
									}
									//logger.debug("# "+fecHasta.getValue()+ " #");
								}
							}						
							if (fecHasta != null) {
								//logger.debug("## Fecha renovacion Hasta: " + fecHasta.getValue()+" ##");	
								paramListPolReq.setFechaRenovacionHasta(fecHasta);
							}
						}
						paramListPolReq.setLinea(Integer.parseInt(linea.toString()));
						ObjectFactory fact = new ObjectFactory();
						JAXBElement<List<BigInteger>>  bb = fact.createParametrosListaPolizasRenovablesListaEstados(lstBig);
						lstEstados.add(bb);
						paramListPolReq.setListaEstados(lstEstados);

						msg.append("## CALL WS PLAN: "+paramListPolReq.getPlan() +" LINEA: "+paramListPolReq.getLinea());
						if (conFechas) {
							msg.append(" est: "+paramListPolReq.getListaEstados().get(0).getValue()+" fDesde: "+paramListPolReq.getFechaRenovacionDesde().getValue().toString()+" fHasta: "+paramListPolReq.getFechaRenovacionHasta().getValue().toString()+" ##");			
						}else {
							msg.append(" est: "+paramListPolReq.getListaEstados().get(0).getValue()+" sin fechas ##");
						}
						
						logger.debug(msg.toString());
						ListaPolizasRenovablesResponse wsResp = null;
						int vuelta = 0;
						for (int ciclo=0;ciclo<reintentos;ciclo++) {
							vuelta++;
							try {
								wsResp = objWs.listaPolizasRenovables(paramListPolReq);
								logger.debug("# ciclo "+vuelta+"/"+reintentos);
								res = wsResp.getPolizasRenovables(); // Llamada al WS
								logger.debug("# ciclo "+vuelta+"/"+reintentos +" OK");
								byte[]arrayAcuse = res.getValue();
								String acuse = new String (arrayAcuse, "UTF-8");
								ListaPolizasRenovablesDocument pp = ListaPolizasRenovablesDocument.Factory.parse(acuse);
							    ListaPolizasRenovables ppRen = pp.getListaPolizasRenovables();
								Renovacion[] renArr = ppRen.getRenovacionArray();
								for (Renovacion renov : renArr) {
									lstRenovGlobal.add(renov);
									contPolizas++;
									contPolizasTemp++;
								}
								logger.debug(" Polizas encontradas -> "+contPolizasTemp+ "");
								//System.exit(0); // 	QUITAR!!!!!!
								break;
							}catch  (es.agroseguro.serviciosweb.contratacionrenovaciones.AgrException ex) {
								List<es.agroseguro.serviciosweb.contratacionrenovaciones.Error> lstErrores = ex.getFaultInfo().getError();
								String errores ="";
								for (es.agroseguro.serviciosweb.contratacionrenovaciones.Error error: lstErrores){
									errores = errores + error.getMensaje().toString()+".";
									logger.debug("# AgrException: "+error.getMensaje().toString());
								}
								break;
							}catch  (Exception e) {
								if (e instanceof javax.xml.ws.WebServiceException) {
									Throwable cause = e; 
								    if ((cause = cause.getCause()) != null){
								        if(cause instanceof ConnectException){
											logger.debug("# ConnectException: "+e.getMessage().toString());
											logger.debug("# Reintentando conexion "+vuelta+"/"+reintentos+"...");
								        }else { 
								        	logger.debug("# otro tipo de Exception: "+e.getMessage().toString());
								        	throw new Exception(e.getMessage().toString(), e);
								        }
								    }else { 
							        	logger.debug("# Error exc. tipo: "+e.getMessage().toString());
							        	throw new Exception(e.getMessage().toString(), e);
							        }
								}else {
									logger.debug("# Error exception tipo: "+e.getClass().toString());
									throw new Exception(e.getMessage().toString(), e);
								}
							}
						} // fin bucle ciclo
						if (salirPrimerciclo){
							salirPrimerciclo = false;
							break;
						}
						if (salirMes){
							salirMes = false;
							break;
						}
					 } // fin bucle dias
					 cDesde.add(Calendar.MONTH, 1);
					 cHasta.add(Calendar.MONTH, 1);
				} // fin bucle meses
			} // fin bucle lstLineasSW
			logger.debug("## TOTAL POLIZAS SW con estAgSeguro: "+estCallWs+" PLAN: " +planActual +" Y LINEAS: "+lstLineasSW+" -> "+contPolizas+ " ##");
		}catch  (Exception ex) {
			logger.debug("# Error en WSn Exception: "+ex.getMessage().toString());
			throw new Exception(ex.getMessage().toString(), ex);
		}
		
		return lstRenovGlobal;
	}
	
	/**
	 * Devuelve la fechaDesde recibida como parametro formateada para la llamada al SW
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaDesde (int anho,int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			if ((mes == 2) && (dia >28)){
				 if(anho % 4 == 0 && anho % 100 != 0 || anho % 400 == 0){
					 dia = 29;				 
				 }else{
					 dia = 28;
				 }
			}else if (dia >30 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)){
				dia = 30;
			}
			
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(anho, mes, dia, DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionDesde(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaDesde para la llamada al SW", e);
		}
		return fecFormateada;
	}
	
	/**
	 * Devuelve la fechaHasta recibida como parametro formateada para la llamada al SW
	 * @param fecha
	 * @return
	 */
	public static JAXBElement<XMLGregorianCalendar> getFechaHasta (int anho,int mes, int dia) {
		JAXBElement<XMLGregorianCalendar> fecFormateada = null;
		try {
			if ((mes == 2) && (dia >28)){
				 if(anho % 4 == 0 && anho % 100 != 0 || anho % 400 == 0){
					 dia = 29;				 
				 }else{
					 dia = 28;
				 }
			}else if (dia >30 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)){
				dia = 30;
			}
			
			XMLGregorianCalendar fechaHCal = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(anho, mes, dia, DatatypeConstants.FIELD_UNDEFINED);
			fecFormateada = new ObjectFactory().createParametrosListaPolizasRenovablesFechaRenovacionHasta(fechaHCal);
		} catch (Exception e) {
			logger.debug("Error al obtener la fechaHasta para la llamada al SW", e);
		}
		return fecFormateada;
	}
	
	/**
	 * Calcula la diferencia entre dos fechas. Devuelve el resultado en dias, meses o anhos segun sea el valor del parametro 'tipo'
	 * @param fechaInicio Fecha inicial
	 * @param fechaFin Fecha final
	 * @param tipo 0=TotalAnhos; 1=TotalMeses; 2=TotalDias; 3=MesesDelAnio; 4=DiasDelMes
	 * @return numero de dias, meses o anhos de diferencia
	 */
	public static long getDiffDates(Date fechaInicio, Date fechaFin, int tipo) {
		// Fecha inicio
 		Calendar calendarInicio = Calendar.getInstance();
		calendarInicio.setTime(fechaInicio);
		int diaInicio = calendarInicio.get(Calendar.DAY_OF_MONTH);
		int mesInicio = calendarInicio.get(Calendar.MONTH) + 1; // 0 Enero, 11 Diciembre
		int anioInicio = calendarInicio.get(Calendar.YEAR);
	 
		// Fecha fin
		Calendar calendarFin = Calendar.getInstance();
		calendarFin.setTime(fechaFin);
		int diaFin = calendarFin.get(Calendar.DAY_OF_MONTH);
		int mesFin = calendarFin.get(Calendar.MONTH) + 1; // 0 Enero, 11 Diciembre
		int anioFin = calendarFin.get(Calendar.YEAR);
	 
		int anios = 0;
		int mesesPorAnio = 0;
		int diasPorMes = 0;
		int diasTipoMes = 0;
	 
		//
		// Calculo de dias del mes
		//
		if (mesInicio == 2) {
			// Febrero
			if ((anioFin % 4 == 0) && ((anioFin % 100 != 0) || (anioFin % 400 == 0))) {
				// Bisiesto
				diasTipoMes = 29;
			} else {
				// No bisiesto
				diasTipoMes = 28;
			}
		} else if (mesInicio <= 7) {
			// De Enero a Julio los meses pares tienen 30 y los impares 31
			if (mesInicio % 2 == 0) {
				diasTipoMes = 30;
			} else {
				diasTipoMes = 31;
			}
		} else if (mesInicio > 7) {
			// De Julio a Diciembre los meses pares tienen 31 y los impares 30
			if (mesInicio % 2 == 0) {
				diasTipoMes = 31;
			} else {
				diasTipoMes = 30;
			}
		}
	 
	 
		//
		// Calculo de diferencia de anho, mes y dia
		//
		if ((anioInicio > anioFin) || (anioInicio == anioFin && mesInicio > mesFin)
				|| (anioInicio == anioFin && mesInicio == mesFin && diaInicio > diaFin)) {
			// La fecha de inicio es posterior a la fecha fin
			// System.out.println("La fecha de inicio ha de ser anterior a la fecha fin");
			return -1;
		} else {
			if (mesInicio <= mesFin) {
				anios = anioFin - anioInicio;
				if (diaInicio <= diaFin) {
					mesesPorAnio = mesFin - mesInicio;
					diasPorMes = diaFin - diaInicio;
				} else {
					if (mesFin == mesInicio) {
						anios = anios - 1;
					}
					mesesPorAnio = (mesFin - mesInicio - 1 + 12) % 12;
					diasPorMes = diasTipoMes - (diaInicio - diaFin);
				}
			} else {
				anios = anioFin - anioInicio - 1;
				System.out.println(anios);
				if (diaInicio > diaFin) {
					mesesPorAnio = mesFin - mesInicio - 1 + 12;
					diasPorMes = diasTipoMes - (diaInicio - diaFin);
				} else {
					mesesPorAnio = mesFin - mesInicio + 12;
					diasPorMes = diaFin - diaInicio;
				}
			}
		}
		//System.out.println("Han transcurrido " + anios + " Anhos, " + mesesPorAnio + " Meses y " + diasPorMes + " Dias.");		
	 
		//
		// Totales
		//
		long returnValue = -1;
	 
		switch (tipo) {
			case 0:
				// Total Anhos
				returnValue = anios;
				// System.out.println("Total anhos: " + returnValue + " Anhos.");
				break;
	 
			case 1:
				// Total Meses
				returnValue = anios * 12 + mesesPorAnio;
				// System.out.println("Total meses: " + returnValue + " Meses.");
				break;
	 
			case 2:
				// Total Dias (se calcula a partir de los milisegundos por dia)
				long millsecsPerDay = 86400000; // Milisegundos al dia
				returnValue = (fechaFin.getTime() - fechaInicio.getTime()) / millsecsPerDay;
				// System.out.println("Total dias: " + returnValue + " Dias.");
				break;
	 
			case 3:
				// Meses del anho
				returnValue = mesesPorAnio;
				// System.out.println("Meses del anho: " + returnValue);
				break;
	 
			case 4:
				// Dias del mes
				returnValue = diasPorMes;
				// System.out.println("Dias del mes: " + returnValue);
				break;
	 
			default:
				break;
		}
	 
		return returnValue;
	}
	
}
