package com.rsi.agp.core.plsql;

public interface IPlsqlService {
	
	public void setTipoImportacion(int tipoImportacion);
	public void setPlan(String plan);
	public void setLinea(String linea);
	public void setTablas(String tablas);
	public void setFichTablas(String fichTablas);
	public void setClasspath(String classpath);

	public void doTask() throws Exception;
}

