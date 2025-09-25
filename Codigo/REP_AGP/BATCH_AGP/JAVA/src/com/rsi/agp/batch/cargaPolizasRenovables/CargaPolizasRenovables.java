package com.rsi.agp.batch.cargaPolizasRenovables;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.models.poliza.LineaDao;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.poliza.LineaGrupoNegocio;

/* Pet. 63482 ** MODIF TAM (04/05/2021) **/
import com.rsi.agp.core.jmesa.service.impl.PolizaRenBean;
import com.rsi.agp.core.jmesa.service.impl.utilidades.AltaPolizaRenovableService;
import com.rsi.agp.core.jmesa.dao.impl.ImportacionPolRenovableDao;

public class CargaPolizasRenovables {

	private static final Logger logger = Logger.getLogger(CargaPolizasRenovables.class);
	private static PolizasPctComisionesDao polizasPctComisionesDao;
	private static LineaDao lineaDao;
	
	/* Pet. 63482 ** MODIF TAM (04/05/2021) ** Inicio */
	private static AltaPolizaRenovableService altaPolizaRenovableService;
	private static ImportacionPolRenovableDao importacionPolRenovableDao;
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##--------------------------------------------##");
			logger.debug("  INICIO Batch CARGA POLIZAS RENOVABLES V.2.1  ");
			logger.debug("##--------------------------------------------##");
			doWork();
			logger.debug("FIN Batch Carga Polizas Renovables");
			System.exit(0);
		} catch (Throwable e) {
			
			logger.error("Error en el proceso de Carga Polizas Renovables",e);
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	private static void doWork() throws Exception {
		List<BigDecimal> lstLineasS = new ArrayList<BigDecimal>();
		List<String> lstLineasSplan = new ArrayList<String>();
		List<BigDecimal> lstLineasSW = new ArrayList<BigDecimal>();
		List<String> lstLineasSWplan = new ArrayList<String>();
		BigDecimal pctAdmTemp = null;
		BigDecimal pctAdqTemp = null;
		boolean tieneGrGenerico = false;
		boolean informado = false;
		SessionFactory factory;
		Session session     = null;
		Transaction trans   = null;
		Map<String, BigDecimal[]> mapLineaGrPcts = new HashMap<String, BigDecimal[]>();
		/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Inicio */
		/* Ya no obtenemos squi los Porcentajes de las comisiones generales, los obtendremos por cada póliza */
		/*Map<String, BigDecimal[]> mapLineaGrPctsFinal = new HashMap<String, BigDecimal[]>();*/
		/* ESC-7188 **  Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Fin */
		Map<String,List<Character>> mapGrLineas = new HashMap<String,List<Character>>();
		Map<String,List<Character>> mapGrNoEcontrados = new HashMap<String,List<Character>>();
		List<PolizaRenBean> lstRes = new ArrayList<PolizaRenBean>();
		try {
			factory = getSessionFactory();
			session = factory.openSession();
			
			logger.debug("# Inicio del doWork #");

			Integer reintentos = 1;
			ResourceBundle bundle = ResourceBundle.getBundle("agp_carga_plz_renovables");
			String strReintentos = bundle.getString("reintentos");
			if (!strReintentos.equals(""))
				reintentos = Integer.parseInt(strReintentos);
			logger.debug("# reintentos llamada WS: "+ reintentos + " #");
			// 1 - Buscamos el plan actual
			Calendar c2 = new GregorianCalendar();
			int planActual = c2.get(Calendar.YEAR);
			
			// Recorremos plan actual y anterior
			for(int anio=planActual;anio>planActual-2;anio--) { 
				logger.debug("## ANO "+ anio + " ##");
				// inicializamos listas y mapas
				lstLineasS.clear();
				lstLineasSW.clear();
				mapGrLineas.clear();
				mapLineaGrPcts.clear();
				/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Inicio */
				/*mapLineaGrPctsFinal.clear();*/
				
				// 2 - Recogemos las lineas con grupo seguro "G01"			
										
				lstLineasS = BBDDCargaPolRenUtil.getLineasGanado(session,anio);
				//SOLO PARA PRUEBAS !!!!!!!!!! 
				//lstLineasS.add(new BigDecimal(415));
				//lstLineasS.add(new BigDecimal(401));
				logger.debug("## Lista lineas ano "+ anio +" con grupo seguro G01: "+lstLineasS.toString()+" ##");
				
				for (BigDecimal ll: lstLineasS) {
					lstLineasSplan.add(anio +"-"+ll.toString());
				}
				
				if (lstLineasS != null && lstLineasS.size()>0) {
					// 2.1 - Creamos un mapa con los grupos de negocio que aplican a cada una de las lineas(tabla TB_LINEAS_GRUPO_NEGOCIO)
					List<LineaGrupoNegocio> lstLineasGrNg = BBDDCargaPolRenUtil.getGruposNegocioPorLinea(session,anio);
					
					for (LineaGrupoNegocio linGr:lstLineasGrNg){
						// SOLO PARA PRUEBAS !!!!!!!!!! 
						//if (linGr.getLinea().getCodlinea().compareTo(new BigDecimal(401)) == 0 || linGr.getLinea().getCodlinea().compareTo(new BigDecimal(415)) == 0){
							if (!mapGrLineas.containsKey(linGr.getLinea().getCodlinea()+"_"+anio)){
								List<Character> lstGruposN = new ArrayList<Character>(); 
								mapGrLineas.put(linGr.getLinea().getCodlinea()+"_"+anio,lstGruposN);
								lstGruposN.add(linGr.getId().getGrupoNegocio());
								
							}else{
								List<Character> lstGr = mapGrLineas.get(linGr.getLinea().getCodlinea()+"_"+anio);
								if (!lstGr.contains(linGr.getId().getGrupoNegocio())){
									lstGr.add(linGr.getId().getGrupoNegocio());
								}
							}
						//}
					}	

					// Recorro las lineas:
					for (BigDecimal linea: lstLineasS) {
						tieneGrGenerico = false;
						informado 	    = true;						
						logger.debug("## comprobando linea "+ linea +" ano: "+anio+" ##");
						
						//3 - Recogemos aquellas lineas que se ha definido el parametro general de comisiones
						List<CultivosEntidades> lstParam = BBDDCargaPolRenUtil.getParamsGen(session,linea,anio);
						// 3.1 Montamos un mapa con las comisiones por linea y grupo de negocio 
						for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param: lstParam) {
							if (param.getLinea() != null && param.getLinea().getCodlinea() != null) {
								// Guardamos por cada linea las comisiones en un mapa
								BigDecimal[] pctComs;
								pctComs=new BigDecimal[2];
								pctComs[0]= param.getPctadministracion();
								pctComs[1]= param.getPctadquisicion();
								// indexo tambien el grupo negocio en la key del mapa: linea + grupoNegocio
								mapLineaGrPcts.put(param.getLinea().getCodlinea().toString()+"_"+param.getGrupoNegocio().getGrupoNegocio(), pctComs);
							}
						}						

						// 3.2 Compruebo si existe el grupo generico para esa linea en nuestro  mantenimiento de parametros generales
						for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param: lstParam) {
							if(param.getLinea().getCodlinea().compareTo(linea) == 0){// misma linea
								if (param.getGrupoNegocio().getGrupoNegocio().equals('9')){
									logger.debug("## linea "+ linea +" con grupoNegocio 9 ##");
									pctAdmTemp = param.getPctadministracion();
									pctAdqTemp = param.getPctadquisicion();
									tieneGrGenerico = true;
								}	
							}
						}
						
						// 3.3 recogemos los grupos de esa linea
						List<Character> lstGr = mapGrLineas.get(linea+"_"+anio);
						
						if (lstGr != null){
							if (tieneGrGenerico){
								// 3.4.1 SI GENERICO: accedemos al mapa de grupos por linea y creamos las comisiones para todos los grupos excepto el generico
								logger.debug("## metemos todos los grupos "+lstGr.toString()+" para linea "+ linea +" plan "+anio+ " ##");
								for (Character gr:lstGr){
									if (!gr.equals('9')){
										BigDecimal[] pctComs;
										pctComs=new BigDecimal[2];
										pctComs[0]= pctAdmTemp;
										pctComs[1]= pctAdqTemp;
										/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Inicio */
										/*mapLineaGrPctsFinal.put(linea.toString()+"_"+gr, pctComs);*/
									}
								}
								
								// inserto en la lista la linea para llamar al WS
								if (!lstLineasSW.contains(linea)){
									lstLineasSW.add(linea);
								}
							}else {//no es generico
	
								// 3.4.2 NO GENERICO: 
								
								//compruebo que todos los grupos de la linea tengon informados datos de comisiones
								List<Character> lstGrNoEncontrados = new ArrayList<Character>();
								for (Character gr:lstGr){
									if (!gr.equals('9')){
										BigDecimal[] pctComsEncontrados =mapLineaGrPcts.get(linea.toString()+"_"+gr);
										if (null == pctComsEncontrados){
											logger.debug("## la linea "+ linea +" plan: "+anio+" NO tiene parametros para el grupoNegocio "+gr+" en el mantenimiento de parametros generales ##");
											lstGrNoEncontrados.add(gr);
											informado = false;
										}
									}
								}
								
								// para el correo resumen almacenamos en una lista los grupos no encontrados en parametros generales como key -> linea_plan
								if (lstGrNoEncontrados.size()>0){
									mapGrNoEcontrados.put(linea+"_"+anio, lstGrNoEncontrados);
								}
								
								
								if (informado){
									for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param: lstParam) {
										if(param.getLinea().getCodlinea().compareTo(linea) == 0){// misma linea	
											BigDecimal[] pctComs;
											pctComs=new BigDecimal[2];
											pctComs[0]= param.getPctadministracion();
											pctComs[1]= param.getPctadquisicion();
											
											// indexo en el mapa las comisiones como key: linea_GrupoNegocio
											/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Inicio */
											/*mapLineaGrPctsFinal.put(param.getLinea().getCodlinea().toString()+"_"+param.getGrupoNegocio().getGrupoNegocio(), pctComs);*/
										}
									}
									//  inserto en la lista la linea para llamar al WS
									if (!lstLineasSW.contains(linea)){
										lstLineasSW.add(linea);
									}
								}// fin if informado
							}
						
						}else{// fin if lstGr
							// Este resultado no deberia contemplarse si el condicioniado esta bien cargado(grupos correctos en TB_LINEAS_GRUPO_NEGOCIO para ese plan, linea)
							logger.debug("## la linea "+ linea +" plan: "+anio+" no tiene grupos Negocio a aplicar en TB_LINEAS_GRUPO_NEGOCIO ##");
						}
					}
					
					logger.debug("## Lineas del ano "+ anio +" para llamar al SW: "+lstLineasSW.toString()+" ##");
					
					for (BigDecimal ll: lstLineasSW) {
						lstLineasSWplan.add(anio +"-"+ll.toString());
					}
					
					// 4 - Recuperamos la lista de polizas renovables y su insercion en BBDD
					if (lstLineasSW.size()>0){
						
						/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Inicio */
						//lstRes = CargaPolizasRenovablesWS.getListPolizasRenovables(lstRes,new Long(anio),lstLineasSW,session,mapLineaGrPctsFinal,reintentos,polizasPctComisionesDao);
						lstRes = CargaPolizasRenovablesWS.getListPolizasRenovables(lstRes,new Long(anio),lstLineasSW,session,reintentos,polizasPctComisionesDao, altaPolizaRenovableService);
						/* ESC-7188 ** Modif TAM (05.09.2019) * Incidencia de Comisiones por E-S Mediadora * Fin */
					}else{
						logger.debug("## NO existen lineas correctas del año "+ anio +" para llamar al SW ##");
					}
				}
			
			}
			
			// 5 - Llamamos al método que realiza el Envío correo  por lista de distribución
			generateAndSendMail(lstRes,lstLineasS,lstLineasSW, session,planActual,lstLineasSplan,lstLineasSWplan,mapGrNoEcontrados);
			
		} catch (Throwable ex) {		
			logger.error("# Error inesperado en la ejecucion la Carga Polizas Renovables",ex);
			if (trans != null && trans.isActive()) {
				trans.rollback();
			}
			throw new Exception();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private static SessionFactory getSessionFactory() {
		SessionFactory sessionFactory;
		try {
			Configuration cfg = new Configuration();
			cfg.configure();

			sessionFactory = cfg.buildSessionFactory();
			
			//Creamos los managers y DAOS necesarios
			polizasPctComisionesDao = new PolizasPctComisionesDao();
			lineaDao = new LineaDao();
			sessionFactory.getCurrentSession().beginTransaction();
			
			//abrimos la sesion
	    	
	    	polizasPctComisionesDao.setLineaDao(lineaDao);
	    	lineaDao.setSessionFactory(sessionFactory);
			polizasPctComisionesDao.setSessionFactory(sessionFactory);
			
			/* Pet. 63482 ** MODIF TAM (04.05.2021) ** Inicio */
			importacionPolRenovableDao = new ImportacionPolRenovableDao();
			importacionPolRenovableDao.setSessionFactory(sessionFactory);
			
			altaPolizaRenovableService = new AltaPolizaRenovableService();
			altaPolizaRenovableService.setImportacionPolRenovableDao(importacionPolRenovableDao);
			
		} catch (Throwable ex) {
			
			logger.error("# Error al crear el objeto SessionFactory.", ex);
			throw new ExceptionInInitializerError(ex);
		}
		return sessionFactory;
	}
	
	/**
	 * 	Mï¿½todo que realiza el Envï¿½o correo  por lista de distribuciï¿½n
	 * @param lstRes 		lista de pï¿½lizas procesadas
	 * @param lstLineas  	lista de lï¿½neas con gruposeguro G01
	 * @param lstLineasSW 	lista de lï¿½neas con gruposeguro G01 que tienen porcentajes de comisiones
	 * @param session
	 * @param planActual
	 */
	private static void generateAndSendMail(
			final List<PolizaRenBean> lstRes, final List<BigDecimal> lstLineas,final List<BigDecimal> lstLineasSW, final Session session,
			final int planActual,final List<String> lstLineasSplan,final List<String> lstLineasSWplan,Map<String,List<Character>> mapGrNoEcontrados) {
		String grupo;
		String asunto;
		StringBuffer msg;
		int contPolizas = 1;
		int totalPolizasOK = 0;
		Boolean polNoCreadas = true;
		Map<String,List<PolizaRenBean>> mapLinPol= new HashMap<String,List<PolizaRenBean>>();

		List<PolizaRenBean> lstNoCreadas = new ArrayList<PolizaRenBean>();
		List<Integer> lstAnios = new ArrayList<Integer>();
		
		// ejemplo correo

		/*
		PolizaRenBean polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("E2244556");
		polOK.setDescripcion("OK");
		lstRes.add(polOK);
		
		polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("H6677889");
		polOK.setDescripcion("OK");
		lstRes.add(polOK);
				
		polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("K7788998");
		polOK.setDescripcion("OK");
		lstRes.add(polOK);
				
		polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("D77667755");
		polOK.setDescripcion("OK");
		lstRes.add(polOK);
		
		polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("S6633447");
		polOK.setDescripcion("- S6633447: error 22");
		lstRes.add(polOK);
		
		polOK = new PolizaRenBean();
		polOK.setLinea("415");
		polOK.setPlan("2015");
		polOK.setReferencia("U3344556");
		polOK.setDescripcion("- U3344556: error 33");
		lstRes.add(polOK);
		 */		

		lstAnios.add(planActual);
		lstAnios.add(planActual-1);
		
		Collections.sort(lstAnios);
		Collections.reverse(lstAnios);
		
		lstLineasSW.clear();
		for (String liW: lstLineasSWplan) {
			BigDecimal linnW = new BigDecimal( liW.subSequence(5, 8).toString());
			if (!lstLineasSW.contains(linnW))
				lstLineasSW.add(linnW);
		}
		
		lstLineas.clear();
		for (String li: lstLineasSplan) {
			BigDecimal linn = new BigDecimal( li.subSequence(5, 8).toString());
			if (!lstLineas.contains(linn))
				lstLineas.add(linn);
		}
		
		//Recorremos aï¿½os
		for (Integer pp: lstAnios) {
			// Recorremos lineas
			for (BigDecimal li: lstLineasSW) {			
				//creamos el mapa
				List<PolizaRenBean> lstP = new ArrayList<PolizaRenBean>();
				mapLinPol.put(pp.toString() + "-" + li.toString(), lstP);
						
				for (PolizaRenBean pol: lstRes) {
					if (pol.getLinea() != null && pol.getPlan().equals(pp.toString()) && pol.getLinea().equals(li.toString()) && pol.getDescripcion().equals("OK")){
						//aï¿½adimos la pol al mapa segun plan y lï¿½nea
						totalPolizasOK++;
						lstP.add(pol);
					}
					if (!pol.getDescripcion().equals("OK")){
						if (!lstNoCreadas.contains(pol)){
							lstNoCreadas.add(pol);
						}
					}
					// nuevo mapa key				
				}
			}
		}
		
		ResourceBundle bundle = ResourceBundle.getBundle("agp_carga_plz_renovables");
		
		grupo = bundle.getString("mail.grupo");
		asunto = bundle.getString("mail.asunto")+ " " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		msg = new StringBuffer();
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		//if (lstLineasSW.size()> 0) {
			msg.append(bundle.getString("mail.totalPolizas"));
			msg.append(" ");
			msg.append(totalPolizasOK);
			msg.append(System.getProperty("line.separator"));
			
			// Montamos una lista de lï¿½neas ï¿½nica
			 List<BigDecimal> lstLinFinal = new ArrayList<BigDecimal>();
			 for (BigDecimal linea: lstLineas) {
				 lstLinFinal.add(linea);
			 }
			 for (BigDecimal lineaSW: lstLineasSW) {
				 if (!lstLinFinal.contains(lineaSW))
					 lstLinFinal.add(lineaSW);
			 }
			 
			Collections.sort(lstLinFinal);
			
			for (Integer pp: lstAnios) {
				for (BigDecimal linea: lstLinFinal) {
					if (lstLineasSW.contains(linea) && lstLineasSWplan.contains(pp+"-"+linea)){
						List<PolizaRenBean> lstPolOK = mapLinPol.get(pp + "-" + linea.toString());
						msg.append("    Polizas del plan "+ pp +", línea "+ linea+": "+lstPolOK.size());
						msg.append(System.getProperty("line.separator"));
					}else {
						if (lstLineasSplan.contains(pp + "-" + linea.toString())) {
							msg.append("    Polizas del plan "+ pp +", linea "+ linea+": "+bundle.getString("mail.noPctComisiones"));
							if (mapGrNoEcontrados.containsKey(linea+"_"+pp)){// falta algun grupo que no esta en el mantenimiento de param generales y si en TB_LINEAS_GRUPO_NEGOCIO
								List<Character> lstGrN = mapGrNoEcontrados.get(linea+"_"+pp);
								Collections.sort(lstGrN);
								String grupos = "";
								boolean primera = true;
								for (Character gr:lstGrN){
									if (primera){
										grupos = gr.toString();
										primera = false;
									}else{
										grupos = grupos + ","+gr.toString();
									}
								}
								msg.append("para los grupos de negocio: "+grupos);
							}
							msg.append(System.getProperty("line.separator"));
						}
					}
					
				}
			}
			
			for (PolizaRenBean polRen: lstRes) {
				if (null != polRen.getDescripcion() && !polRen.getDescripcion().equals("OK")) {
					if (contPolizas<500) {
						if (polNoCreadas){
							polNoCreadas = false;
							msg.append("        ");
							msg.append(bundle.getString("mail.polizasKO"));
						}		
						msg.append(System.getProperty("line.separator"));	
						msg.append("            ");
						msg.append(polRen.getDescripcion());
					}
					contPolizas++;
				}
			}
			if (contPolizas>499) {
				msg.append(System.getProperty("line.separator"));	
				msg.append("            ");
				msg.append("[...]");
			}
//		} else {
//			msg.append(bundle.getString("mail.noPolizas"));
//		}
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		msg.append(bundle.getString("mail.footer"));
		msg.append(System.getProperty("line.separator"));
		logger.debug("Mensaje a mandar: "+ asunto.toString());
		logger.debug(msg.toString());
		
		Query query = session.createSQLQuery("CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreo(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", grupo).setParameter("asunto", asunto)
				.setParameter("mensaje", msg.toString());

		query.executeUpdate();
	}
	
	
}