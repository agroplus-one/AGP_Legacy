package com.rsi.agp.batch.cargaComisiones;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.core.jmesa.dao.impl.ImportacionComisionesUnificadoDao;
import com.rsi.agp.core.managers.impl.CargaComisionesManager;
import com.rsi.agp.core.managers.impl.ParametrizacionManager;
import com.rsi.agp.core.managers.impl.ComisionesUnificadas.ImportacionComisionesUnificadoManager;
import com.rsi.agp.dao.models.comisiones.UtilidadesComisionesDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.param.ParametrizacionDao;

public class CargaComisiones {

	private static final Logger logger = Logger.getLogger(CargaComisiones.class);

	private static org.hibernate.SessionFactory sessionFactory ;
	
	private static CargaComisionesManager cargaComisionesManager;
	private static ParametrizacionManager parametrizacionManager;
	private static ParametrizacionDao parametrizacionDao;
	private static ImportacionComisionesUnificadoDao importacionComisionesUnificadoDao;
	private static UtilidadesComisionesDao utilidadesComisionesDao;
	private static ImportacionComisionesUnificadoManager importacionComisionesUnificadoManager;
	private static DatabaseManager databaseManager;
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.info("INICIO batch Carga Automatica Comisiones 2015+");
			doWork();
			logger.info("FIN batch Carga Automatica Comisiones 2015+");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en el proceso batch de Carga Automatica Comisiones 2015+", e);
			System.exit(1);
		} catch (Throwable e) {
			logger.error("Error en el proceso batch de Carga Automatica Comisiones 2015+", e);
			System.exit(1);
		}
	}

	private static void doWork() throws Throwable {

		try {
			logger.info(" Cargamos la configuracion necesaria ");
			getConfiguracion();
			
			sessionFactory.getCurrentSession().beginTransaction();
			
			int resultadoCarga = cargaComisionesManager.cargaFichero(null, null);
			
			if (resultadoCarga == CargaComisionesManager.FICHERO_NOT_FOUND) {
				
				Thread.sleep(Long.parseLong(parametrizacionManager.getConfigAgpValor("CARGA_COMS_REINTENTOS_MS")));
				
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				int minsNow = Calendar.getInstance().get(Calendar.MINUTE);
				if (sdf.parse(hourNow + ":" + minsNow).compareTo(
						sdf.parse(parametrizacionManager.getConfigAgpValor("CARGA_COMS_HORA_LIMITE"))) <= 0) {
					doWork();
				} else {
					logger.debug("No se encuentra el fichero de comisiones del mes y pasa de la hora limite");
				}
			} else if (resultadoCarga == CargaComisionesManager.FICHERO_CARGADO) {
				logger.debug("El fichero de comisiones del mes ya se ha cargado");
				//finalice OK y no haga nada mas
			} else if (resultadoCarga == CargaComisionesManager.ERROR) {
				logger.error("No se ha podido cargar el fichero");
			} else {
				logger.debug("Fichero importado correctamente");
				sessionFactory.getCurrentSession().getTransaction().commit();
				importacionComisionesUnificadoDao.validarFicheroComisiones(Long.valueOf(resultadoCarga), 'U');
				// finalice OK y genere el correo
				generateAndSendMail();
			}			
		} catch (Exception e) {
			logger.error("Error en doWork: " + e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error en doWork: " + e);
			throw e;
		} finally {
			if (sessionFactory.getCurrentSession().isOpen()) {
				sessionFactory.getCurrentSession().close();
			}		
		}
	}
	
	private static SessionFactory getConfiguracion() throws Throwable {
		try {
			//Configuracion hibernate
			Configuration cfg = new Configuration();
			//cargamos la sesion
			sessionFactory =  cfg.configure().buildSessionFactory();
			
			//Creamos los managers y DAOS necesarios
			cargaComisionesManager = new CargaComisionesManager();
			databaseManager = new DatabaseManager();
			databaseManager.setSessionFactory(sessionFactory);
			databaseManager.setSessionFactoryImportacion(sessionFactory);
			importacionComisionesUnificadoDao = new ImportacionComisionesUnificadoDao();
			importacionComisionesUnificadoDao.setSessionFactory(sessionFactory);
			importacionComisionesUnificadoDao.setDatabaseManager(databaseManager);
			utilidadesComisionesDao = new UtilidadesComisionesDao();
			utilidadesComisionesDao.setSessionFactory(sessionFactory);
			importacionComisionesUnificadoDao.setUtilidadesComisionesDao(utilidadesComisionesDao);
			cargaComisionesManager.setImportacionComisionesUnificadoDao(importacionComisionesUnificadoDao);
			importacionComisionesUnificadoManager = new ImportacionComisionesUnificadoManager();
			importacionComisionesUnificadoManager.setImportacionComisionesUnificadoDao(importacionComisionesUnificadoDao);
			cargaComisionesManager.setImportacionComisionesUnificadoManager(importacionComisionesUnificadoManager);			
			parametrizacionManager = new ParametrizacionManager();
			parametrizacionDao = new ParametrizacionDao();
			parametrizacionDao.setSessionFactory(sessionFactory);
			parametrizacionManager.setParametrizacionDao(parametrizacionDao);
		}catch (Throwable e) {
			logger.error("Error al cargar la configuracion " + e);
			throw e ;
		}
		return sessionFactory;
	}
	
	
	private static void generateAndSendMail() {

		String MES[] = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		
		int mes = Calendar.getInstance().get(Calendar.MONTH);
		String asunto = "Carga comisiones " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		StringBuilder msg = new StringBuilder();

		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));
		msg.append("Le informamos que el proceso de carga del fichero de comisiones de "
				+ MES[mes == 0 ? 11 : mes] + " ha finalizado y se ha cargado correctamente");
		msg.append(System.getProperty("line.separator"));
		msg.append(System.getProperty("line.separator"));

		logger.debug("Mensaje a mandar: " + asunto.toString());
		logger.debug(msg.toString());

		Query query = sessionFactory.getCurrentSession()
				.createSQLQuery("CALL o02agpe0.PQ_ENVIO_CORREOS.enviarCorreo(:grupo, :asunto, :mensaje)")
				.setParameter("grupo", "1").setParameter("asunto", asunto).setParameter("mensaje", msg.toString());

		query.executeUpdate();
	}
}