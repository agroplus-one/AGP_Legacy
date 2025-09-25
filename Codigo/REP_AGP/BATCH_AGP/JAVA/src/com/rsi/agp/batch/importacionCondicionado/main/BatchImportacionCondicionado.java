package com.rsi.agp.batch.importacionCondicionado.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.rsi.agp.batch.importacionCondicionado.util.ConfigImportacionCondicionado;
import com.rsi.agp.batch.importacionCondicionado.util.ImportacionCondicionadoConnectionPool;
import com.rsi.agp.core.managers.impl.PantallasConfigurablesManager;
import com.rsi.agp.core.plsql.PlsqlServiceImpl;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.commons.CommonDao;
import com.rsi.agp.dao.models.config.PantallasConfigurablesDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.log.HistImportacionesDao;
import com.rsi.agp.dao.models.plsql.PlsqlDao;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;
import com.rsi.agp.dao.tables.cargas.CargasTablas;

public class BatchImportacionCondicionado {
	
	private static final Logger logger = Logger.getLogger(BatchImportacionCondicionado.class);
	static ImportacionCondicionadoConnectionPool ccp=null;
	private static PlsqlServiceImpl plsqlExecutor = new PlsqlServiceImpl();
	
	public static void main(String[] args) {
		
		try {
			BasicConfigurator.configure();
			logger.info("INICIO batch Importacion Condicionados");
			doWork();
			logger.info("FIN Batch Importacion Condicionados");
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error en el proceso batch de Importacion de condicionados", e);
			System.exit(1);
		}
		
	}

	private static void doWork() {
		List <CargasFicheros> listFicheros = new ArrayList<CargasFicheros>();
		List <CargasTablas> listTablas = new ArrayList<CargasTablas>();
		CargasFicheros cf = new CargasFicheros();
		CargasTablas ct = new CargasTablas();
		ccp = new ImportacionCondicionadoConnectionPool();
		String tablas = "";
    	String ficheros = "";
    	Long idcond = null ;
    	int estadoCarga;
    	logger.info("Cargamos la configuracion necesaria");
    	getConfiguracion();
		
    	logger.info("Recuperamos las cargas en estado CERRADO de bbdd");
    	List <Long> listIds = ccp.getCargasCerradas();
		
		logger.info("Cargas obtenidas de base de datos: " + listIds.size() + ". Recorremos los ids y recuperamos los ficheros.");
		try{
			for (int i=0; i<listIds.size();i++){
				idcond = listIds.get(i);
				logger.info("idCondicionado: " +idcond);
				
				listFicheros = ccp.getFicherosbyID(idcond);
				logger.info("Por cada condicionado obtenemos los ficheros.idFichero a imporar:" + idcond);
				
				for(int x =0;x<listFicheros.size();x++){
					
					cf = listFicheros.get(x);
					
					//Copiamos el fichero de "/aplicaciones/AGP_AGROPLUS/INTERFACES/cargas_batch" a "/aplicaciones/AGP_AGROPLUS/INTERFACES/"
					copiarFicheroCondicionado(cf.getFichero());
					//Fin de la copia de ficheros.
					
					listTablas = ccp.getTablas(cf.getId());
					
					for (int z=0; z<listTablas.size();z++){
						ct = listTablas.get(z);
						tablas += ct.getNumtabla() + ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.SEPARADOR);
						ficheros += cf.getFichero() + ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.SEPARADOR);
						//Si es la tabla 8: Características del grupo de tasas, añadimos también la combinación 8R
						if (ct.getNumtabla().equals(new BigDecimal(8)) || ct.getNumtabla().equals(new BigDecimal(406))){
							tablas += ct.getNumtabla() + "R" + ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.SEPARADOR);
							ficheros += cf.getFichero() + ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.SEPARADOR);
						}
					}
					logger.info("Establecemos las propiedades necesarias para lanzar el proceso de importación");
					logger.info("idCondicionado:" + listIds.get(i) + " idFichero:" + listFicheros.get(x).getFichero()+ " tablas:" + tablas);
		        	plsqlExecutor.setTablas(tablas);
		        	plsqlExecutor.setClasspath(ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.CLASSPATH));
		        	plsqlExecutor.setFichTablas(ficheros);
		        	plsqlExecutor.setLinea(StringUtils.nullToString(cf.getLinea()));
		        	plsqlExecutor.setPlan(StringUtils.nullToString(cf.getPlan()));
		        	plsqlExecutor.setTipoImportacion(getTipo(cf.getTipo().intValue()));
		        	plsqlExecutor.doTask();
		        	tablas = "";
		        	ficheros = "";
				}
				logger.info("actualizamos la fecha de carga y el estado en bbdd");
				estadoCarga = 1; //Cargado
				ccp.updateFechayEstado(idcond,estadoCarga);
			}
		}catch(Exception e ){
			logger.error("ERROR EN LA CARGA DE FICHEROS DEL CONDICIONADO: " + idcond, e);
			logger.info("actualizamos la fecha de carga y el estado en bbdd");
			estadoCarga = 4; //Error
			ccp.updateFechayEstado(idcond,estadoCarga);
			System.exit(1);
		}
	}

	/**
	 * Método para copiar los ficheros del condicionado de la ruta a la que se suben desde el mantenimiento a la ruta
	 * donde se ejecutarán para la carga.
	 * @param fichero
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void copiarFicheroCondicionado(String fichero) throws IOException {
		try {
			File ficheroTxtOrigen = new File(
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.RUTA_ORIGEN) + fichero
							+ ".TXT");
			File ficheroZipOrigen = new File(
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.RUTA_ORIGEN) + fichero
							+ ".ZIP");
			File ficheroTxtDestino = new File(
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.RUTA_CARGA) + fichero
							+ ".TXT");
			File ficheroZipDestino = new File(
					ConfigImportacionCondicionado.getProperty(ConfigImportacionCondicionado.RUTA_CARGA) + fichero
							+ ".ZIP");
			byte[] buf = new byte[1024];
			int len;
			try (InputStream inTxt = new FileInputStream(ficheroTxtOrigen);
					OutputStream outTxt = new FileOutputStream(ficheroTxtDestino)) {
				while ((len = inTxt.read(buf)) > 0) {
					outTxt.write(buf, 0, len);
				}
			}
			buf = new byte[1024];
			try (InputStream inZip = new FileInputStream(ficheroZipOrigen);
					OutputStream outZip = new FileOutputStream(ficheroZipDestino)) {
				while ((len = inZip.read(buf)) > 0) {
					outZip.write(buf, 0, len);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Metodo que devuelve el tipo correcto para la importacion en funcion del tipo que hay en bbdd
	 * @param tipo
	 * @return
	 */
	private static int getTipo(int tipo) {
		switch (tipo) {
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			default:
				return 0;
		}
	}

	private static void getConfiguracion() {
		//Creamos los managers y DAOS necesarios
		DatabaseManager databaseManager = new DatabaseManager();
		PlsqlDao plsqlDao = new PlsqlDao();
		HistImportacionesDao histImportacionesDao = new HistImportacionesDao();
		CommonDao commonDao = new CommonDao();
		
		PantallasConfigurablesManager pantallasConfigurablesManager = new PantallasConfigurablesManager();
		PantallasConfigurablesDao pantallasConfigurablesDao = new PantallasConfigurablesDao();
		
    	//Configuracion hibernate
		Configuration cfg = new Configuration();
		//cargamos la sesion
		org.hibernate.SessionFactory sessionFactory =  cfg.configure().buildSessionFactory();
		
		//Configuracion de hibernate para el esquema o02agpe1:		
		/* PRODUCCION */
		Configuration cfg1 = new Configuration()
		.setProperty("hibernate.connection.url", "jdbc:oracle:oci:@SGR_P")
	    .setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
		.setProperty("default_schema", "O02AGPE1")
		.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
    	
    	//abrimos la sesion
    	sessionFactory.getCurrentSession().beginTransaction();
    	
    	//Asignamos la session de hibernate a los Daos y managers
    	databaseManager.setSessionFactory(sessionFactory);
    	databaseManager.setSessionFactoryImportacion(cfg1.buildSessionFactory());
    	plsqlDao.setDatabaseManager(databaseManager);
    	histImportacionesDao.setSessionFactory(sessionFactory);
    	commonDao.setSessionFactory(sessionFactory);
    	pantallasConfigurablesDao.setSessionFactory(sessionFactory);
    	pantallasConfigurablesManager.setPantallasConfigurablesDao(pantallasConfigurablesDao);
    	pantallasConfigurablesManager.setCommonDao(commonDao);
    	
    	plsqlExecutor.setPlsqlDao(plsqlDao);
    	plsqlExecutor.setHistImportacionesDao(histImportacionesDao);
    	plsqlExecutor.setCommonDao(commonDao);
    	plsqlExecutor.setPantallasConfigurablesManager(pantallasConfigurablesManager);
    }
}
