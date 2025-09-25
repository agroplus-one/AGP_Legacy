package com.rsi.agp.dao.models;

import java.util.Map;

import org.hibernate.SessionFactory;

import com.rsi.agp.core.exception.BusinessException;

public interface IDatabaseManager {

	public static final String DB_TYPE_NUMBER = "NUMBER";
	public static final String DB_TYPE_VARCHAR2 = "VARCHAR2";
	public static final String DB_TYPE_VARCHAR = "VARCHAR";
	public static final String DB_TYPE_DATE = "DATE";
	
	public static final String PARAMETER_IN = "IN";
	public static final String PARAMETER_OUT = "OUT";

	public static final String FUNCTION_RESULT = "RESULT";

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory);
	
	public void createBackupImportacion(final String tablas);
	
	public Map<String, Object> executeStoreProc(String spName, Map<String,Object> inParameters) throws BusinessException;
	public int executeStatementErrorImportacion(String error, int tipoImportacion, Long lineaseguroid) throws BusinessException;

}