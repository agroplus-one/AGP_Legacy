package com.rsi.agp.batch.procesarPolizasRenovables;

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
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.models.param.ParametrizacionDao;
import com.rsi.agp.dao.models.poliza.LineaDao;
import com.rsi.agp.dao.models.poliza.PolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;


public class ProcesarPolizasRenovables {

	private static final Logger logger = Logger.getLogger(ProcesarPolizasRenovables.class);
	
	private static PolizasPctComisionesManager polizasPctComisionesManager ;
	private static PolizasPctComisionesDao polizasPctComisionesDao;
	private static LineaDao lineaDao;
	private static PolizaDao polizaDao;
	private static ParametrizacionDao parametrizacionDao;
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##-----------------------------------------------##");
			logger.debug ("INICIO Batch PROCESAMIENTO POLIZAS RENOVABLES V.01");
			logger.debug("##-----------------------------------------------##");
			ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_plz_renovables");
			String work = bundle.getString("work");
			
			if(work != null && "1".equals(work)){
				doWorkPctComisiones();
			}else{
				doWork();				 
			}
			
			logger.debug("##--------------------------------------##");
			logger.debug ("FIN Batch Procesamiento Polizas Renovables");
			logger.debug("##--------------------------------------##");
			System.exit(0);
		} catch (Throwable e) {
			logger.error("# Error en el Procesamiento de Polizas Renovables",e);
			System.exit(1);
		}
	}
	
	private static void doWorkPctComisiones()throws Exception {
		SessionFactory factory;
		Session session     = null;
		Transaction trans   = null;
		Calendar calFechaRenovacion=null;
		try {
			factory = getSessionFactory();
			session = factory.openSession();
			// Seleccion de polizas renovables que no tienen un registro asociado de porcentajes de comisiones
			
			List<Object[]>ren= BBDDProcesarPolRenUtil.getPolizasRenovablesSinPctComisiones(session);
			if (null!= ren && ren.size()>0){
				logger.debug("Polizas renovables sin registro de porcentaje de comisiones: " + ren.size());
				trans = session.beginTransaction();
				int i=0;
				for (Object[] elemento : ren){
					BigDecimal idRenovable= (BigDecimal)elemento[0];
					Date fechaRenovacion=(Date)elemento[1];				
					String referencia=(String) elemento[2];
					BigDecimal lineaseguroId= (BigDecimal)elemento[3];
					logger.debug(" # idRenovable: "+idRenovable+ " fechaRenovacion: " +fechaRenovacion+ " referencia: "+ referencia + " lineaseguroId: "+lineaseguroId+" #");
					GastosRenovacion gastos = BBDDProcesarPolRenUtil.getGastosRenovacion(session, idRenovable.longValue());
					if(null!=gastos && null!=gastos.getAdministracion() 
							&& null!=gastos.getAdquisicion() && null!=gastos.getComisionMediador()){
						 calFechaRenovacion = Calendar.getInstance();
						 calFechaRenovacion.setTime(fechaRenovacion);
						 Poliza poliza=BBDDProcesarPolRenUtil.getPoliza(session, referencia,lineaseguroId.longValue());
						try {
							ProcesarPolizasRenovablesWS.populateComisionesProcesar(poliza, poliza.getLinea().getCodplan().intValue(), 
									poliza.getLinea().getCodlinea().intValue(),calFechaRenovacion,
									gastos.getAdministracion(), gastos.getAdquisicion(),gastos.getComisionMediador(), 
									poliza.getColectivo().getSubentidadMediadora(), session, gastos.getGrupoNegocio());
						} catch (Exception e) {
							try {
									logger.debug("No se encuentran las comisiones del plan " + poliza.getLinea().getCodplan().toString() + ", linea " 
											+ poliza.getLinea().getCodlinea().intValue() + ", fecha de efecto " + calFechaRenovacion.getTime().toString()
								+ ", entMed " + poliza.getColectivo().getSubentidadMediadora().getId().getCodentidad() + 
								", subEntMed " + poliza.getColectivo().getSubentidadMediadora().getId().getCodsubentidad());
							} catch (Exception e2) {								
								logger.debug("No se encuentran las comisiones de la poliza " + referencia);
							}
							
						}
						 
						 //para pruebas
						 //trans.commit();
						 //session.close();
					}else{
						logger.debug("Polizas renovables sin registro de gastos: " + referencia);
					}
					if(i==10){
						break;
					}
					i+=1;
				}
				if (trans.isActive()&& !trans.wasCommitted()){
					trans.commit();
				}
			}else{
				logger.debug(" No hay polizas renovables sin registro de porcentaje de comisiones");
			}
			
			
		}  catch (Exception ex){
			logger.error("# Error inesperado en la ejecucion del Procesamiento de Polizas Renovables doWorkPctComisiones - ",ex);
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
	
	@SuppressWarnings("unchecked")
	private static void doWork() throws Exception {
		List<com.rsi.agp.dao.tables.comisiones.CultivosEntidades> lstParam = new ArrayList<com.rsi.agp.dao.tables.comisiones.CultivosEntidades>();
		List<BigDecimal> lstLineasS = new ArrayList<BigDecimal>();
		List<String> lstLineasSplan = new ArrayList<String>();
		List<BigDecimal> lstLineasSW = new ArrayList<BigDecimal>();
		List<String> lstLineasSWplan = new ArrayList<String>();
		SessionFactory factory;
		Session session     = null;
		List<PolizaRenBean> lstRes = new ArrayList<PolizaRenBean>();
		try {
			factory = getSessionFactory();
			session = factory.openSession();
			
			// 1 - Buscamos el plan actual
			Calendar c2 = new GregorianCalendar();
			int planActual = c2.get(Calendar.YEAR);
			
			String estados = BBDDProcesarPolRenUtil.getEstadosPolRenovables(session);
			logger.debug("## estados "+ estados + " ##");
			//int anio = 2017;
			for(int anio=planActual;anio>planActual-2;anio--) { 
				logger.debug("## A�O "+ anio + " ##");
				
				lstLineasS.clear();
				lstLineasSW.clear();
				
				// 2 - Recogemos las lineas con grupo seguro "G01"		
				lstLineasS = BBDDProcesarPolRenUtil.getLineasGanado(session,anio);
				logger.debug("## Lista l�neas a�o "+ anio +" con grupo seguro G01: "+lstLineasS.toString()+" ## ");
				
				for (BigDecimal ll: lstLineasS) {
					lstLineasSplan.add(anio +"-"+ll.toString());
				}
				
				if (lstLineasS != null && lstLineasS.size()>0) {
					// 3 - Recogemos aquellas lineas que se ha definido el parametro general de comisiones		
					Criteria crit2 = session.createCriteria(com.rsi.agp.dao.tables.comisiones.CultivosEntidades.class);
					crit2.createAlias("linea","linea");
					crit2.add(Restrictions.eq("linea.codplan",new BigDecimal(anio)));
					crit2.add(Restrictions.in("linea.codlinea",lstLineasS));
					crit2.add(Restrictions.isNotNull("pctadministracion"));
					crit2.add(Restrictions.isNotNull("pctadquisicion"));

					lstParam = (List<com.rsi.agp.dao.tables.comisiones.CultivosEntidades>) crit2.list();				
					for (com.rsi.agp.dao.tables.comisiones.CultivosEntidades param: lstParam) {
						if (param.getLinea() != null && param.getLinea().getCodlinea() != null) {
							if (!lstLineasSW.contains(param.getLinea().getCodlinea()))
								lstLineasSW.add(param.getLinea().getCodlinea());						
						}
					}
					
					for (BigDecimal ll: lstLineasSW) {
						lstLineasSWplan.add(anio +"-"+ll.toString());
					}
					
					// 4 - Recuperamos la lista de polizas renovables
					if (lstLineasSW.size()>0)
						lstRes = ProcesarPolizasRenovablesWS.getListPolizasRenovables(lstRes,new Long(anio),lstLineasSW,estados,session,polizasPctComisionesManager,polizasPctComisionesDao, polizaDao, parametrizacionDao);
				}				
			}
			// 5 - 	Llamamos al metodo que realiza el Envio correo  por lista de distribucion
			generateAndSendMail(lstRes,lstLineasS,lstLineasSW, session,planActual,lstLineasSplan,lstLineasSWplan);
			
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
			polizasPctComisionesManager = new PolizasPctComisionesManager();
			
			polizasPctComisionesDao = new PolizasPctComisionesDao();
			lineaDao = new LineaDao();
			polizaDao = new PolizaDao();
			parametrizacionDao = new ParametrizacionDao();
			//abrimos la sesion
	    	sessionFactory.getCurrentSession().beginTransaction();
			polizasPctComisionesManager.setPolizasPctComisionesDao(polizasPctComisionesDao);
			polizasPctComisionesDao.setLineaDao(lineaDao);
			lineaDao.setSessionFactory(sessionFactory);
			polizasPctComisionesDao.setSessionFactory(sessionFactory);
			polizaDao.setSessionFactory(sessionFactory);
			parametrizacionDao.setSessionFactory(sessionFactory);
		} catch (Throwable ex) {
			
			logger.error("# Error al crear el objeto SessionFactory.", ex);
			throw new ExceptionInInitializerError(ex);
		}
		return sessionFactory;
	}
	
	/**
	 * M�todo que realiza el Env�o correo  por lista de distribuci�n
	 * @param lstRes 		lista de p�lizas procesadas
	 * @param lstLineas  	lista de l�neas con gruposeguro G01
	 * @param lstLineasSW 	lista de l�neas con gruposeguro G01 que tienen porcentajes de comisiones
	 * @param session
	 * @param planActual
	 */
	private static void generateAndSendMail(
			final List<PolizaRenBean> lstRes, final List<BigDecimal> lstLineas,final List<BigDecimal> lstLineasSW, final Session session,
			final int planActual,final List<String> lstLineasSplan,final List<String> lstLineasSWplan) {
		String grupo;
		String asunto;
		StringBuffer msg;
		int contPolizas = 1;
		int totalPolizasOK = 0;
		Boolean polNoCreadas = true;
		StringBuilder polizasOK = new StringBuilder();
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
					
		//Recorremos años
	    for (Integer pp: lstAnios) {
		    //bucle lineas
			for (BigDecimal li: lstLineasSW) {				
				//creamos el mapa
				List<PolizaRenBean> lstP = new ArrayList<PolizaRenBean>();
				mapLinPol.put(pp.toString() + "-" + li.toString(), lstP);	
				for (PolizaRenBean pol: lstRes) {
					if (pol.getLinea() != null && pol.getPlan().equals(pp.toString()) && pol.getLinea().equals(li.toString())){
						//añadimos la pol al mapa segun linea
						if (null != pol.getDescripcion() && pol.getDescripcion().equals("OK")){
							if (pol.getReferencia() != null)
								//logger.debug(pol.getReferencia());
								polizasOK.append(pol.getReferencia()+",");
							totalPolizasOK++;
							lstP.add(pol);
						}		
					}
					// Creamos la lista de p�lizas erroneas evitando duplicaciones
					if (null != pol.getDescripcion() && !pol.getDescripcion().equals("OK")){
						if (!lstNoCreadas.contains(pol)){
							lstNoCreadas.add(pol);
						}
					}
					// nuevo mapa key
				}
				
			}
		
	    }	
	    if (polizasOK.length()>0){
			logger.debug("#### REFERENCIAS INSERTADAS: "+polizasOK.toString() +" ####");
		}	
		ResourceBundle bundle = ResourceBundle.getBundle("agp_procesar_plz_renovables");
		
		grupo = bundle.getString("mail.grupo");
		asunto = bundle.getString("mail.asunto")+ " " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		msg = new StringBuffer();
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		if (lstLineasSW.size()> 0) {
			msg.append(bundle.getString("mail.totalPolizas"));
			msg.append(" ");
			msg.append(totalPolizasOK);
			msg.append(System.getProperty("line.separator"));
			
			// Montamos una lista de lineas unica
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
						msg.append("    Polizas del plan "+ pp +", linea "+ linea+": "+lstPolOK.size());
						msg.append(System.getProperty("line.separator"));
					}else {
						if (lstLineasSplan.contains(pp + "-" + linea.toString())) {
							msg.append("    Polizas del plan "+ pp +", linea "+ linea+": "+bundle.getString("mail.noPctComisiones"));
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
		} else {
			msg.append(bundle.getString("mail.noPolizas"));
		}
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		msg.append(bundle.getString("mail.footer"));
		msg.append(System.getProperty("line.separator"));
		logger.debug("Mensaje a mandar: "+ asunto.toString());
		logger.debug(msg.toString());

		Query query = session.createSQLQuery("CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreoGrande(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", grupo).setParameter("asunto", asunto)
				.setParameter("mensaje", msg.toString());
		query.executeUpdate();
	}

	protected static class PolizaRenBean {

		private String plan;
		private String linea;
		private String referencia;
		private String modulo;
		private String idColectivo;
		private String nifAsegurado;
		private String descripcion;
		private String estado;
		
		public PolizaRenBean(final String plan, final String linea,
				final String referencia, final String modulo, final String descripcion,final String estado) {

			this.plan = plan;
			this.linea = linea;
			this.referencia = referencia;
			this.modulo = modulo;
			this.descripcion = descripcion;
			this.estado = estado;
		}

		
		public String getEstado() {
			return estado;
		}


		public void setEstado(String estado) {
			this.estado = estado;
		}


		public void setPlan(String plan) {
			this.plan = plan;
		}


		public void setLinea(String linea) {
			this.linea = linea;
		}


		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}


		public void setModulo(String modulo) {
			this.modulo = modulo;
		}


		public PolizaRenBean() {
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
		
		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		
		public String getIdColectivo() {
			return idColectivo;
		}


		public void setIdColectivo(String idColectivo) {
			this.idColectivo = idColectivo;
		}


		public String getNifAsegurado() {
			return nifAsegurado;
		}


		public void setNifAsegurado(String nifAsegurado) {
			this.nifAsegurado = nifAsegurado;
		}


		@Override
		public String toString() {

			StringBuffer sb = new StringBuffer();
			sb.append(this.plan);
			sb.append(" ");
			sb.append(this.linea);
			sb.append(" ");
			sb.append(this.modulo.length() < 2 ? this.modulo + ' ': this.modulo);
			sb.append(" ");
			sb.append(this.referencia);
			sb.append(" ");
			sb.append(this.idColectivo);
			sb.append(" ");
			sb.append(this.nifAsegurado);
			sb.append(" ");
			sb.append(this.descripcion);
			sb.append(" ");
			sb.append(this.estado);
			return sb.toString();
		}
	}

	
}