package com.rsi.agp.dao.models;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.UnzipException;

public interface IPlsqlDao {
	public void ejecutaPLInsercionXML(String tablas, int tipoImportacion, String ruta, String plan, String linea, String fichTablas, String tablasErrorTransformacion) throws BusinessException;
	public void createBackupImportacion(String tablas);
	public void executeStatementErrorImportacion(int tipoImportacion, String error, String plan, String linea) throws BusinessException;
	public void descomprimeZIP(String fileName, String path) throws UnzipException;
	public void descomprimeZIP(String fileName, String path, String xmlFile) throws UnzipException;
	public void ejecutaConfiguradorPantallasAuto (int lineaSeguroId) throws BusinessException;
}
