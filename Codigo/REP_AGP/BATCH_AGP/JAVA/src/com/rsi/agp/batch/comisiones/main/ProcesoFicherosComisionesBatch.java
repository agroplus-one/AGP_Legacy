package com.rsi.agp.batch.comisiones.main;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.batch.comisiones.ProcesamientoFicherosComisiones;
import com.rsi.agp.batch.comisiones.ProcesamientoFicherosImpagados;
import com.rsi.agp.batch.comisiones.ProcesamientoFicherosRecibosEmitidos;
import com.rsi.agp.batch.comisiones.ProcesamientoFicherosReglamento;
import com.rsi.agp.batch.comisiones.util.ConfigBuzonInfovia;
import com.rsi.agp.batch.comisiones.util.ImportacionConnectionPool;

public class ProcesoFicherosComisionesBatch {

	private static final Log logger = LogFactory.getLog(ProcesoFicherosComisionesBatch.class);

	ImportacionConnectionPool icp = null;

	public void doProcesamiento() throws SQLException {
		procesarFicheros();
	}

	public void procesarFicheros() throws SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		String dirBase = ConfigBuzonInfovia.getProperty("directorioLocal");
		String dirComisiones = dirBase + ConfigBuzonInfovia.getProperty("comisiones") + sdf.format(date);
		String dirImpagados = dirBase + ConfigBuzonInfovia.getProperty("impagados") + sdf.format(date);
		String dirEmitidos = dirBase + ConfigBuzonInfovia.getProperty("recibos") + sdf.format(date);
		String dirReglamento = dirBase + ConfigBuzonInfovia.getProperty("reglamento") + sdf.format(date);
		icp = new ImportacionConnectionPool();
		logger.info("Procesamiento de ficheros");
		logger.info("Procesando directorio de Comisiones. " + dirComisiones);
		procesarComisiones(dirComisiones);
		logger.info("Procesando directorio de Impagados. " + dirImpagados);
		procesarImpagados(dirImpagados);
		logger.info("Procesando directorio de Emitidos. " + dirEmitidos);
		procesarEmitidos(dirEmitidos);
		logger.info("Procesando directorio de Reglamento. " + dirReglamento);
		procesarReglamento(dirReglamento);
	}

	private void procesarReglamento(String dirRreglamento) throws SQLException {

		ProcesamientoFicherosReglamento pfr = new ProcesamientoFicherosReglamento(icp);
		File directorio = new File(dirRreglamento);
		File[] ficheros = directorio.listFiles(new ZipFileFilter());
		if (ficheros != null) {
			for (File file : ficheros) {
				logger.info("procesarReglamento -> fichero :" + file.getName());
				try {
					pfr.procesarFicheroReglamento(file);
				} catch (Exception e) {
					e.printStackTrace();
					updateEnHistorico(file.getName(), false);
				}
				updateEnHistorico(file.getName(), true);
			}
		} else {
			logger.info("La ruta indicada no contiene archivos de Reglamento");
		}
	}

	private void procesarEmitidos(String dirEmitidos) throws SQLException {
		ProcesamientoFicherosRecibosEmitidos pfr = new ProcesamientoFicherosRecibosEmitidos(icp);
		File directorio = new File(dirEmitidos);
		File[] ficheros = directorio.listFiles(new ZipFileFilter());
		if (ficheros != null) {
			for (File file : ficheros) {
				logger.info("procesarEmitidos -> fichero :" + file.getName());
				try {
					pfr.procesarFicheroReciboEmitido(file);
				} catch (Exception e) {
					e.printStackTrace();
					updateEnHistorico(file.getName(), false);
				}
				updateEnHistorico(file.getName(), true);
				moverABackup(file);
			}
		} else {
			logger.info("La ruta indicada no contiene archivos de Emitidos");
		}
	}

	private void procesarImpagados(String dirImpagados) throws SQLException {
		ProcesamientoFicherosImpagados pfr = new ProcesamientoFicherosImpagados(icp);
		File directorio = new File(dirImpagados);
		File[] ficheros = directorio.listFiles(new ZipFileFilter());
		if (ficheros != null) {
			for (File file : ficheros) {
				logger.info("procesarImpagados -> fichero :" + file.getName());
				try {
					pfr.procesarFicheroImpagado(file);
				} catch (Exception e) {
					e.printStackTrace();
					updateEnHistorico(file.getName(), false);
				}
				updateEnHistorico(file.getName(), true);
				moverABackup(file);
			}
		} else {
			logger.info("La ruta indicada no contiene archivos de Impagados");
		}
	}

	private void procesarComisiones(String dirComisiones) throws SQLException {
		ProcesamientoFicherosComisiones pfr = new ProcesamientoFicherosComisiones(icp);
		File directorio = new File(dirComisiones);
		File[] ficheros = directorio.listFiles(new ZipFileFilter());
		File file = null;
		if (ficheros != null) {
			for (int i = 0; i < ficheros.length; i++) {
				file = ficheros[i];
				logger.info("Procesando " + file.getName());
				try {
					pfr.procesarFicheroComisiones(file);
				} catch (Exception e) {
					e.printStackTrace();
					updateEnHistorico(file.getName(), false);
				}
				updateEnHistorico(file.getName(), true);
			}
		} else {
			logger.info("La ruta indicada no contiene archivos de Comisiones.");
		}

	}

	private void updateEnHistorico(String filename, boolean correcto) throws SQLException {

		String sqlQuery = "update o02agpe0.TB_COMS_HIST_FICHS_IMPORTS set correcto='" + correcto
				+ "' where nombrefichero='" + filename.substring(0, filename.length() - 4) + "'";
		logger.info(sqlQuery);
		icp.executeQueryInsert(sqlQuery);
	}

	private class ZipFileFilter implements java.io.FileFilter {
		public boolean accept(File f) {
			String name = f.getName().toLowerCase();
			return name.endsWith("zip");
		}
	}

	private void moverABackup(File filename) {
		String dirFinal = ConfigBuzonInfovia.getProperty("backupdir");
		String base = ConfigBuzonInfovia.getProperty("directorioLocal");
		boolean result = filename.renameTo(new File(base + dirFinal + filename.getName()));
		logger.info(base + dirFinal + filename.getName() + " --> " + result);
	}
}
