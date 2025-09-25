package com.rsi.agp.dao.tables.pac;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class FormPacCargasBean implements Serializable{
	
	private MultipartFile file = null;
	private String nombreFichero = null;
	private BigDecimal plan;
	private BigDecimal entMed;
	private BigDecimal subentMed;
	
	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}

	public BigDecimal getPlan() {
		return plan;
	}

	public void setPlan(BigDecimal plan) {
		this.plan = plan;
	}


	public BigDecimal getEntMed() {
		return entMed;
	}

	public void setEntMed(BigDecimal entMed) {
		this.entMed = entMed;
	}

	public BigDecimal getSubentMed() {
		return subentMed;
	}

	public void setSubentMed(BigDecimal subentMed) {
		this.subentMed = subentMed;
	}

	

}
