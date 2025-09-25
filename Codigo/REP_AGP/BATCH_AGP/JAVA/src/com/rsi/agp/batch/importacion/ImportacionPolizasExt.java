package com.rsi.agp.batch.importacion;

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
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.batch.common.ImportacionConstants;
import com.rsi.agp.core.exception.BusinessException;
/* Pet. 63482 ** MODIF TAM (28/05/2021) **/
import com.rsi.agp.core.jmesa.service.impl.ImportacionPolizasService;
import com.rsi.agp.core.managers.ged.impl.DocumentacionGedManager;
import com.rsi.agp.core.managers.impl.PolizasPctComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.comisiones.PolizasPctComisionesDao;
import com.rsi.agp.dao.models.ged.DocumentacionGedDao;
import com.rsi.agp.dao.models.poliza.DistribucionCosteDao;
import com.rsi.agp.dao.models.poliza.ImportacionPolizasExtDao;
import com.rsi.agp.dao.models.poliza.LineaDao;
import com.rsi.agp.dao.tables.batch.comunicacionesExternas.ErrorComunicacionExterna;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ImportacionPolizasExt { 

	private static final Logger logger = Logger
			.getLogger(ImportacionPolizasExt.class);
	
	private static final String CONT_BATCH_ENVIO_POL_EXTERNAS  = "CONT_BATCH_ENVIO_POL_EXTERNAS";
	
	/* Pet. 63482 ** MODIF TAM (28/05/2021) ** Inicio */
	private static ImportacionPolizasService importacionPolizasService;
	private static ImportacionPolizasExtDao importacionPolizasExtDao;
	
	//ESC-25609
	private static DistribucionCosteDao distribucionCosteDAO;
	private static PolizasPctComisionesManager polizasPctComisionesManager;
	private static PolizasPctComisionesDao polizasPctComisionesDao;
	private static LineaDao lineaDao;
	
	//P0073325
	private static DocumentacionGedManager documentacionGedManager;
	private static DocumentacionGedDao documentacionGedDao;
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.info("*************************************************");
			logger.info("INICIO batch Importacion de polizas externas v1.1");
			logger.info("*************************************************");
			doWork();
			logger.info("FIN Batch Importacion de polizas externas");
			System.exit(0);
		} catch (Exception e) { 
			logger.error(
					"Error en el proceso batch de Importacion de polizas externas",
					e);
			System.exit(1);
		}
	}

	@SuppressWarnings("unchecked")
	private static void doWork() {
		List<com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt> pendientes;
		SessionFactory factory; 
		es.agroseguro.contratacion.Poliza sitActual;
		Session session = null;
		Transaction trans = null;
		Boolean isPrincipal = null;

		List<PolizaOKBean> refPolizasOK = null;
		List<PolizaKOBean> refPolizasKO = null;
		String codModulo;
		String tipoRef;
		try {
			factory = getSessionFactory();
			session = factory.openSession();
												  
			Criteria crit = session.createCriteria(com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt.class);
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.eq("estado",ImportacionConstants.ESTADO_IMPORTACION_PDTE));
			or.add(Restrictions.eq("estado",ImportacionConstants.ESTADO_IMPORTACION_KO));
			crit.add(or);

			pendientes = (List<com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt>) crit.list();

			refPolizasOK = new ArrayList<PolizaOKBean>(pendientes.size());
			refPolizasKO = new ArrayList<PolizaKOBean>(pendientes.size());

			if (pendientes.size() == 0) {
				logger.info("No hay polizas para importar.");
			} else {

				for (com.rsi.agp.dao.tables.importacion.ImportacionPolizasExt pendiente : pendientes) {
					tipoRef = pendiente.getTipoRef();
					isPrincipal = tipoRef.trim().compareTo(new String("P")) == 0;
					codModulo = "N/A";

					logger.debug("Tratando poliza " + pendiente.getReferencia());
					logger.debug("Poliza en estado de importacion "
							+ (ImportacionConstants.ESTADO_IMPORTACION_PDTE == pendiente
									.getEstado() ? "Pendiente" : "Erronea"));
					
					Linea linea =BBDDPolizaUtil.getLineaSeguroBBDD(new BigDecimal(pendiente.getLinea()), 
							new BigDecimal(pendiente.getPlan()), session);
					boolean esGanado = (linea.getEsLineaGanadoCount() > 0);
					trans = session.beginTransaction();
					
					logger.debug("Valor de tipoRef: " +tipoRef +" y valor de isPrincipal: "+isPrincipal);
					

					try {
						sitActual = ImportacionPolizasExtWS.getSituacionActualizada(pendiente.getReferencia(), 
								pendiente.getPlan(), pendiente.getLinea(), tipoRef, session, esGanado);
						
						es.agroseguro.contratacion.Poliza polizaAct=(es.agroseguro.contratacion.Poliza) sitActual;
						codModulo = polizaAct.getCobertura().getModulo().trim();
						pendiente.setCodmodulo(codModulo);
						ImportacionPolizasExtBBDD.importaPolizaExt(polizaAct, pendiente.getIdEnvio(), session,
								isPrincipal, esGanado, importacionPolizasService);
						
						
						BBDDEstadosUtil.actualizaEstadoOKImportacion(pendiente.getId(), isPrincipal, session);

						refPolizasOK.add(new PolizaOKBean(pendiente.getPlan().toString(), pendiente.getLinea().toString(),
								pendiente.getReferencia(), codModulo));
					} catch (BusinessException ex) {
						logger.error(ex.getMessage(), ex);
						String errorMsg = StringUtils.isNullOrEmpty(ex.getMessage()) ? ex.getClass().getName()
								: ex.getMessage();
						// Si el error es que ya se encuentra en el sistema, hay que quitarle el estado 3 y ponerlo a 2.
						if (errorMsg.toLowerCase().indexOf("ya se encuentra presente".toLowerCase()) != -1 ) {
							
							// Se actualizan como importadas OK
							BBDDEstadosUtil.actualizaEstadoOKImportacion(pendiente.getId(), isPrincipal, session);
							refPolizasOK.add(new PolizaOKBean(pendiente.getPlan().toString(), pendiente.getLinea().toString(),
									pendiente.getReferencia(), codModulo));
						}
						else {
							
							BBDDEstadosUtil.actualizaEstadoKOImportacion(pendiente.getId(), errorMsg, isPrincipal, session);

							refPolizasKO.add(new PolizaKOBean(pendiente.getPlan()
											.toString(), pendiente.getLinea()
											.toString(), pendiente.getReferencia(),
											pendiente.getCodmodulo(), errorMsg));
						}
						
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						String errorMsg = ex.getMessage() != null && !"".equals(ex.getMessage()) ? ex.getMessage()
								: ex.getClass().getName();

						BBDDEstadosUtil.actualizaEstadoKOImportacion(pendiente.getId(), errorMsg, isPrincipal, session);
						refPolizasKO
								.add(new PolizaKOBean(pendiente.getPlan().toString(), pendiente.getLinea().toString(),
										pendiente.getReferencia(), pendiente.getCodmodulo(), errorMsg));
					}
					
					if (trans.isActive()&& !trans.wasCommitted()){
						trans.commit();
					}	

					logger.debug("Fin de poliza " + pendiente.getReferencia());
				}

				logger.info("Polizas importadas correctamente: "
						+ refPolizasOK.size());
				logger.info("Polizas sin importar por error:   "
						+ refPolizasKO.size());

			}
		} catch (Throwable ex) {
			logger.error("Error inesperado en la ejecucion de la importacion",
					ex);
			if (trans != null && trans.isActive()) {
				trans.rollback();
			}
		} finally {
			generateAndSendMail(refPolizasOK, refPolizasKO, session);
			
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
			sessionFactory.getCurrentSession().beginTransaction();
			
			//Creamos los managers y DAOS necesarios
			/* Pet. 63482 ** MODIF TAM (04.05.2021) ** Inicio */
			importacionPolizasExtDao = new ImportacionPolizasExtDao();
			importacionPolizasExtDao.setSessionFactory(sessionFactory);
			
			importacionPolizasService = new ImportacionPolizasService();
			importacionPolizasService.setImportacionPolizasExtDao(importacionPolizasExtDao);
			
			//ESC-25609
			lineaDao = new LineaDao();
			lineaDao.setSessionFactory(sessionFactory);
			
			polizasPctComisionesDao = new PolizasPctComisionesDao();
			polizasPctComisionesDao.setSessionFactory(sessionFactory);
			polizasPctComisionesDao.setLineaDao(lineaDao);
			
			distribucionCosteDAO = new DistribucionCosteDao();
			distribucionCosteDAO.setSessionFactory(sessionFactory);
			
			polizasPctComisionesManager = new PolizasPctComisionesManager();
			polizasPctComisionesManager.setPolizasPctComisionesDao(polizasPctComisionesDao);
			
			importacionPolizasService.setDistribucionCosteDAO(distribucionCosteDAO);
			importacionPolizasService.setPolizasPctComisionesManager(polizasPctComisionesManager);
			
			//P0073325
			documentacionGedDao = new DocumentacionGedDao();
			documentacionGedDao.setSessionFactory(sessionFactory);
			
			documentacionGedManager = new DocumentacionGedManager();
			documentacionGedManager.setDocumentacionGedDao(documentacionGedDao);
			
			importacionPolizasService.setDocumentacionGedManager(documentacionGedManager);
			
		} catch (Throwable ex) {
			logger.error("Error al crear el objeto SessionFactory." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		return sessionFactory;
	}

	private static void generateAndSendMail(final List<PolizaOKBean> refPolizasOK,
			final List<PolizaKOBean> refPolizasKO, final Session session) {

		String grupo;
		String asunto;
		StringBuffer msg;
		String cabeceraValidacion;
		String cabeceraTotalPolizas;
		String cabeceraImportaciones;
		String polizasExtValidadas;
		
		
		ResourceBundle bundle = ResourceBundle.getBundle("agp_plz_externas");

		grupo = bundle.getString("mail.grupo");

		asunto = bundle.getString("mail.asunto")
				+ new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		msg = new StringBuffer();

		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		
		cabeceraValidacion=bundle.getString("mail.cabeceraValidaciones");
		msg.append(cabeceraValidacion);
		msg.append("\t ");
		msg.append(System.getProperty("line.separator"));
		
		polizasExtValidadas = getPolizasExtValidadas();
		cabeceraTotalPolizas=bundle.getString("mail.cabeceraTotalPolizasValidadas") + polizasExtValidadas;
		msg.append(cabeceraTotalPolizas);
		msg.append("\t ");
		msg.append(System.getProperty("line.separator"));
		
		escribeErroresComunicacion(msg);
		msg.append("\t ");
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		cabeceraImportaciones= bundle.getString("mail.cabeceraImportaciones");
		msg.append(cabeceraImportaciones);
		msg.append("\t ");
		msg.append(System.getProperty("line.separator"));
		
		if (refPolizasOK != null && refPolizasKO != null && (refPolizasOK.size() + refPolizasKO.size() > 0)) {

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
			msg.append(System.getProperty("line.separator"));
			msg.append(bundle.getString("mail.polizasOK"));
			msg.append(" ");
			msg.append(refPolizasOK.size());
			Collections.sort(refPolizasOK, comparator);
			for (PolizaOKBean polizaOK : refPolizasOK) {
				msg.append(System.getProperty("line.separator"));
				msg.append("\t- ");
				msg.append(polizaOK.toString());
			}
			msg.append(System.getProperty("line.separator"));
			msg.append(System.getProperty("line.separator"));
			msg.append(bundle.getString("mail.polizasKO"));
			msg.append(" ");
			msg.append(refPolizasKO.size());
			Collections.sort(refPolizasKO, comparator);
			for (PolizaOKBean polizaKO : refPolizasKO) {
				msg.append(System.getProperty("line.separator"));
				msg.append("\t- ");
				msg.append(polizaKO.toString());
			}
		} else {

			msg.append(bundle.getString("mail.noPolizas"));
		}
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		msg.append(bundle.getString("mail.footer"));
		msg.append(System.getProperty("line.separator"));
		
		// Escribe el texto del correo en el log
		logger.debug("Mensaje a enviar:");
		logger.debug(msg);
		 
		Query query = session
				.createSQLQuery(
						"CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreoGrande(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", grupo).setParameter("asunto", asunto)
				.setParameter("mensaje", msg.toString());

		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	private static List<ErrorComunicacionExterna>getErrores(){
		List<ErrorComunicacionExterna> errores = null;
		SessionFactory factory;		
		Session session = null;
		
		try {
			factory = getSessionFactory();
			session = factory.openSession();
			Criteria crit = session.createCriteria(ErrorComunicacionExterna.class);			
			errores = (List<ErrorComunicacionExterna>) crit.list();			
		} catch (Throwable ex) {
			logger.error("Error seleccionando los errores de comunicaciones externas",ex);
		
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return errores;

	} 
	
	private static String getPolizasExtValidadas() {
		SessionFactory factory;		
		Session session = null;
		String res=null;
		try {
			
			factory = getSessionFactory();
			session = factory.openSession();
			String sql="SELECT AGP_VALOR FROM O02AGPE0.TB_CONFIG_AGP WHERE  AGP_NEMO='" + CONT_BATCH_ENVIO_POL_EXTERNAS +"'";
			res = (String) session.createSQLQuery(sql).uniqueResult();
			
		} catch (Exception e) {
			logger.error("Error seleccionando el numero de polizas validadas en el proceso de envio a agroseguro.", e);
		} finally {
			if (session != null) {
				logger.debug("Cerrando session en Finally..");
				session.close();
				logger.debug("Session cerrada en Finally");
			}
		}
		return res;
	}
	
	private static void escribeErroresComunicacion(StringBuffer msg){
		List<ErrorComunicacionExterna> errores=getErrores();
		
		ResourceBundle bundle = ResourceBundle.getBundle("agp_plz_externas");
		String cabeceraNoValidas=bundle.getString("mail.cabeceraPolizasNoValidas") ;
		
		if(null!=errores && errores.size()>0){
			msg.append(cabeceraNoValidas + errores.size());
			msg.append(System.getProperty("line.separator"));
			
			for (ErrorComunicacionExterna error: errores) {
				msg.append(error.toString());
				msg.append(System.getProperty("line.separator"));
			}
		}else{
			msg.append(cabeceraNoValidas + " 0");
		}
			
	} 
	
	protected static class PolizaOKBean {

		private String plan;
		private String linea;
		private String referencia;
		private String modulo;

		public PolizaOKBean(final String plan, final String linea,
				final String referencia, final String modulo) {

			this.plan = plan;
			this.linea = linea;
			this.referencia = referencia;
			this.modulo = modulo;
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
			sb.append(this.plan);
			sb.append(" ");
			sb.append(this.linea);
			sb.append(" ");
			/* MODIF TAM ESC-4828 ** 10.01.2019 ** Inicio */
			if (this.modulo != null){
				//logger.info("**@@** Valor de modulo No Nulo");
				sb.append(this.modulo.length() < 2 ? this.modulo + ' '
						: this.modulo);
			}else{
				logger.info("**@@** Valor de modulo Nulo");
				this.modulo = " ";
				sb.append(this.modulo.length() < 2 ? this.modulo + ' '
					: this.modulo);
		    }
			//logger.info("**@@** Valor de modulo:-"+this.modulo+"-");
			/* MODIF TAM ESC-4828 ** 10.01.2019 ** Fin */
			
			sb.append(" ");
			sb.append(this.referencia);
			return sb.toString();
		}
	}

	protected static class PolizaKOBean extends PolizaOKBean {

		private String descripcion;

		public PolizaKOBean(final String plan, final String linea,
				final String referencia, final String modulo,
				final String descripcion) {

			super(plan, linea, referencia, modulo);
			this.descripcion = descripcion;
		}

		public String getDescripcion() {

			return descripcion;
		}

		@Override
		public String toString() {

			StringBuffer sb = new StringBuffer(super.toString());
			sb.append(" ");
			sb.append(this.descripcion);
			return sb.toString();
		}
	}
}