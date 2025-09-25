package com.rsi.agp.core.plsql;

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PlsqlExecutorService {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private IPlsqlService plsqlExecutor;
	private Executor executor;
	
	private int tipoImportacion;
	private String plan;
	private String linea;
	private String tablas;
	private String fichTablas;
	private String classpath;
	
	public PlsqlExecutorService() {
		
	}

	/**
	 * Constructor
	 * @param plsqlExecutor
	 * @param executor
	 */
	public PlsqlExecutorService(IPlsqlService plsqlExecutor, Executor executor){
		this.plsqlExecutor = plsqlExecutor;
		this.executor = executor;
	}

    /**
	 * Starts executing the Service. This call is asynchronous, so won't
	 * block.
	 * 
	 * @throws RejectedExecutionException
	 *             if the execution of plsqlexecutor is not accepted
	 *             (concurrent/shutdown).
	 */
	public void start(){
		executor.execute(new Task());
    }

	public int getTipoImportacion() {
		return tipoImportacion;
	}

	public void setTipoImportacion(int tipoImportacion) {
		this.tipoImportacion = tipoImportacion;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getTablas() {
		return tablas;
	}

	public void setTablas(String tablas) {
		this.tablas = tablas;
	}

	/**
	 * Clase para lanzar el proceso asíncrono
	 * @author U028783
	 *
	 */
    class Task implements Runnable{
        public void run(){
            try{
            	plsqlExecutor.setTipoImportacion(tipoImportacion);
            	plsqlExecutor.setPlan(plan);
            	plsqlExecutor.setLinea(linea);
            	plsqlExecutor.setTablas(tablas);
            	plsqlExecutor.setFichTablas(fichTablas);
            	plsqlExecutor.setClasspath(classpath);
            	
            	plsqlExecutor.doTask();
            }
            catch(Exception ex){
            	logger.info("Error durante la ejecucion del proceso asincrono.", ex);
            }
        }
    }

	public String getFichTablas() {
		return fichTablas;
	}

	public void setFichTablas(String fichTablas) {
		this.fichTablas = fichTablas;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

}
