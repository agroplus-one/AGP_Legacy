package com.rsi.agp.core.managers.impl;

import java.util.HashMap;
import java.util.ResourceBundle;

import com.rsi.agp.core.plsql.PlsqlExecutorService;

public class PlsqlManager {
	
	private PlsqlExecutorService plsqlExecutionService;
	
	public PlsqlManager() {
		
	}
	
	public PlsqlManager(PlsqlExecutorService plsqlExecutionService){
		this.plsqlExecutionService = plsqlExecutionService;
	}
	
	public PlsqlExecutorService getPlsqlExecutionService() {
		return plsqlExecutionService;
	}

	public void setPlsqlExecutionService(PlsqlExecutorService plsqlExecutionService) {
		this.plsqlExecutionService = plsqlExecutionService;
	}

	/**
	 * Ejecucion del PL ok
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> cargaDatosEjecucionOK() throws Exception
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("result", ResourceBundle.getBundle("agp_importacion").getString("importacion.msgOk"));
		return parameters;
	}
	/**
	 * PL todavia en Ejecucion
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> cargaDatosTodaviaEnEjecucion() throws Exception
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("result", ResourceBundle.getBundle("agp_importacion").getString("importacion.msgDuring"));
		return parameters;		
	}
	/**
	 * PL Error
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> cargaDatosError() throws Exception
	{
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("result", ResourceBundle.getBundle("agp_importacion").getString("importacion.msgError"));
		return parameters;		
	}

}
