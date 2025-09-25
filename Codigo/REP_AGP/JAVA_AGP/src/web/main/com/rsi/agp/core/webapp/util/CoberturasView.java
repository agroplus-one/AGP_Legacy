package com.rsi.agp.core.webapp.util;

import java.util.ArrayList;

public class CoberturasView 
{
	private String plan;
	private String lineaAndDesc;
	private ArrayList<Modulos> modulos;
	
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getLineaAndDesc() {
		return lineaAndDesc;
	}
	public void setLineaAndDesc(String lineaAndDesc) {
		this.lineaAndDesc = lineaAndDesc;
	}
	public ArrayList<Modulos> getModulos() {
		return modulos;
	}
	public void setModulos(ArrayList<Modulos> modulos) {
		this.modulos = modulos;
	}
}
