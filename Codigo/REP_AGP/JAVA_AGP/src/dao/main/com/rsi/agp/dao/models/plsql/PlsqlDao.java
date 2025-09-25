package com.rsi.agp.dao.models.plsql;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.UnzipException;
import com.rsi.agp.dao.models.IDatabaseManager;
import com.rsi.agp.dao.models.IPlsqlDao;

public class PlsqlDao implements IPlsqlDao {
	
	// Constantes
	private static final String CON_PARAMETROS = "Con parametros:";
	private static final String ERROR_AL_DESCOMPRIMIR_EL_FICHERO = "Error al descomprimir el fichero ";
	private static final String LLAMADA_AL_PROCEDIMIENTO = "Llamada al procedimiento ";
	
	private final Log logger = LogFactory.getLog(getClass());
	private IDatabaseManager databaseManager;

	@Override
	public void descomprimeZIP(String fileName, String path) throws UnzipException {
		logger.info("Inicio de la descompresion del fichero " + fileName + " en la ruta " + path);
		String procedure = "o02agpe0.PQ_UTLZIP.UNCOMPRESSFILE (P_IN_FILE IN VARCHAR2, RUTA IN VARCHAR2)";
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_IN_FILE", fileName);
		inParameters.put("RUTA", path);		
		
		logger.info(LLAMADA_AL_PROCEDIMIENTO + procedure);
		try {
			this.databaseManager.executeStoreProc(procedure, inParameters);
		} catch (Exception e) {
			logger.error(ERROR_AL_DESCOMPRIMIR_EL_FICHERO + fileName, e);
			throw new UnzipException(ERROR_AL_DESCOMPRIMIR_EL_FICHERO + fileName + ": " + e.getMessage());
		}
		
		logger.info("Fin de la descompresion del fichero.");
	}
	
	@Override
	public void descomprimeZIP(String fileName, String path, String xmlFile) throws UnzipException {
		logger.info("Inicio de la descompresion del fichero " + fileName + " en la ruta " + path);
		String procedure = "o02agpe0.PQ_UTLZIP.UNCOMPRESSFILE (P_IN_FILE IN VARCHAR2, RUTA IN VARCHAR2, FICHERO IN VARCHAR2)";
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_IN_FILE", fileName);
		inParameters.put("RUTA", path);
		inParameters.put("FICHERO", xmlFile);
		
		logger.info(LLAMADA_AL_PROCEDIMIENTO + procedure);
		logger.info("P_IN_FILE " + fileName);
		logger.info("RUTA " + path);
		logger.info("FICHERO " + xmlFile);
		try {
			this.databaseManager.executeStoreProc(procedure, inParameters);
		} catch (Exception e) {
			logger.error(ERROR_AL_DESCOMPRIMIR_EL_FICHERO + fileName, e);
			throw new UnzipException(ERROR_AL_DESCOMPRIMIR_EL_FICHERO + fileName + ": " + e.getMessage());
		}
		
		logger.info("Fin de la descompresion del fichero.");
	}

	@Override
	public void ejecutaPLInsercionXML(String tablas, int tipoImportacion,
			String ruta, String plan, String linea, String fichTablas, String tablasErrorTransformacion) throws BusinessException {
		
		logger.info("Inicio de la carga de ficheros xml en la base de datos");
		String procedure = "o02agpe0.PQ_IMPORTACION_CSV.PR_CARGAXMLS (P_TABLAS IN VARCHAR2, P_TIPOIMPORTACION IN NUMBER, P_PLAN IN NUMBER, " +
				"P_LINEA IN NUMBER, P_RUTA IN VARCHAR2, P_FICHEROS IN VARCHAR2, P_TABLAS_ERROR IN VARCHAR2)";
		
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("P_TABLAS", tablas);
		inParameters.put("P_TIPOIMPORTACION", tipoImportacion);
		inParameters.put("P_PLAN", plan);
		inParameters.put("P_LINEA", linea);
		inParameters.put("P_RUTA", ruta);
		inParameters.put("P_FICHEROS", fichTablas);
		inParameters.put("P_TABLAS_ERROR", tablasErrorTransformacion);
		
		logger.info(LLAMADA_AL_PROCEDIMIENTO + procedure);
		logger.info(CON_PARAMETROS);
		logger.info('\t'+"Tablas: "+tablas);
		logger.info('\t'+"Tipo importacion: "+tipoImportacion);
		logger.info('\t'+"Plan: "+plan);
		logger.info('\t'+"Linea: "+linea);
		logger.info('\t'+"Ruta: "+ruta);
		logger.info('\t'+"Fichero tablas: "+fichTablas);
		logger.info('\t'+"Tablas error: "+tablasErrorTransformacion);
		
		this.databaseManager.executeStoreProc(procedure, inParameters);
		
		logger.info("Fin de la carga de ficheros xml en la base de datos.");
	}
	
	public void createBackupImportacion(String tablas){
		GregorianCalendar gcIni = new GregorianCalendar();
		this.databaseManager.createBackupImportacion(tablas);
		GregorianCalendar gcFin = new GregorianCalendar();
		logger.info("Tiempo de creacion del backup de la importacion: " + (gcFin.getTimeInMillis() - gcIni.getTimeInMillis()) + ".");
	}
	 
	public void ejecutaConfiguradorPantallasAuto (int lineaSeguroId) throws BusinessException
	{
		String procedure = "o02agpe0.PQ_IMPORTACION_CSV.PR_CREAPANTALLACONFIGURABLE (LINEASEGUROPARAM IN NUMBER)";
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("LINEASEGUROPARAM", lineaSeguroId);
		logger.info(LLAMADA_AL_PROCEDIMIENTO + procedure);
		logger.info(CON_PARAMETROS);
		logger.info("LineaSeguroId: "+lineaSeguroId);
		this.databaseManager.executeStoreProc(procedure, inParameters);		
	}
	
	public void setDatabaseManager(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public void executeStatementErrorImportacion(int tipoImportacion, String error, String plan, String linea)
			throws BusinessException {
		
		logger.info("Se produjo el siguiente error al descomprimir un fichero zip: " + error);
		String procedure = "o02agpe0.PQ_IMPORTACION_CSV.PR_INSERTAERROR (tipoImportacion IN NUMBER, plan IN NUMBER, linea IN NUMBER, error IN VARCHAR2)";
		Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("TIPOIMPORTACION", tipoImportacion);
		inParameters.put("PLAN", plan);
		inParameters.put("LINEA", linea);
		inParameters.put("ERROR", error);
		
		logger.info(LLAMADA_AL_PROCEDIMIENTO + procedure);
		logger.info(CON_PARAMETROS);
		logger.info('\t'+"Tipo importacion: "+tipoImportacion);
		logger.info('\t'+"Plan: "+plan);
		logger.info('\t'+"Linea: "+linea);
		logger.info('\t'+"Error: "+error);
		
		this.databaseManager.executeStoreProc(procedure, inParameters);
		
		logger.info("Fin de la inserción del error en la base de datos.");
	}
	
}
