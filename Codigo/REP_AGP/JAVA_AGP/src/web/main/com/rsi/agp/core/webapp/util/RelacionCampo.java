
package com.rsi.agp.core.webapp.util;

public class RelacionCampo{
	String uso            = null;
	String ubicacion      = null;
	String campoSc        = null;
	String factor         = null;
	String tipo           = null;
	String procesoCalculo = null;
	
	public RelacionCampo(String uso, String ubicacion, String campoSc,      
	                     String factor, String tipo, String procesoCalculo){
		
		this.uso            = uso;
		this.ubicacion      = ubicacion;
		this.campoSc        = campoSc;
		this.factor         = factor;
		this.tipo           = tipo;
		this.procesoCalculo = procesoCalculo;
		
	}
	
	public String getUso(){
		return this.uso;
		
	}
	public String getUbicacion(){
		return this.ubicacion;
		
	}
	public String getCampoSc(){
		return this.campoSc;
		
	}
	public String getFactor(){
		return this.factor;
		
	}
	public String getTipo(){
		return this.tipo ;
		
	}
	public String getProcesoCalculo(){
		return this.procesoCalculo;
		
	}
}