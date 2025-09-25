package com.rsi.agp.dao.tables.comisiones.unificado.informes.cierre;

import java.math.BigDecimal;
import java.sql.Date;

public class InformeColaboradores2015 {
	
	 private Long id;
     private Integer idcierre;
     private String CSB;
     private String nomSubentidad;
     private String email;
     private String email2;
     private BigDecimal PCN;
     private BigDecimal importe;
     private BigDecimal retencion;
     private BigDecimal liquidar;
     private String mes;
     private String IBAN;
     
     public InformeColaboradores2015() {
    	 
     }

     public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getIdcierre() {
		return idcierre;
	}
	public void setIdcierre(Integer idcierre) {
		this.idcierre = idcierre;	
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail2() {
		return email2;
	}
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public BigDecimal getPCN() {
		return PCN;
	}
	public void setPCN(BigDecimal pCN) {
		PCN = pCN;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	
	public String getNomSubentidad() {
		return nomSubentidad;
	}
	public void setNomSubentidad(String nomSubentidad) {
		this.nomSubentidad = nomSubentidad;
	}
	public BigDecimal getRetencion() {
		return retencion;
	}
	public void setRetencion(BigDecimal retencion) {
		this.retencion = retencion;
	}
	public BigDecimal getLiquidar() {
		return liquidar;
	}
	public void setLiquidar(BigDecimal liquidar) {
		this.liquidar = liquidar;
	}
	public String getIBAN() {
		return IBAN;
	}
	public void setIBAN(String iBAN) {
		IBAN = iBAN;
	}

	public String getCSB() {
		return CSB;
	}

	public void setCSB(String cSB) {
		CSB = cSB;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

}
