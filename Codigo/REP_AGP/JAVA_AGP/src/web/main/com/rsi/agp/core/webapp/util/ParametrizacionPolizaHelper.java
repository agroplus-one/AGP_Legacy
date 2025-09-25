package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class ParametrizacionPolizaHelper implements Comparable<ParametrizacionPolizaHelper>{

	String id = "";
	String estado = "";
	String tipo = "";
	String plan = "";
	String pctComMax = "";
	String pctAdm = "";
	String pctAdq = "";
	String linsegId = "";
	String pctEnt = "";
	String pctESMed = "";
	String descuentoRecargoTipo = "";;
	String descuentoRecargoPct = "";
	String descuentoElegidoPct = "";
	Character grupoNegocio;
	String descGrupoNegocio="";
	String pctEsMedAjustado="";
	String pctEntidadAjustado="";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getPctComMax() {
		return pctComMax;
	}
	public void setPctComMax(String pctComMax) {
		this.pctComMax = pctComMax;
	}
	public String getPctAdm() {
		return pctAdm;
	}
	public void setPctAdm(String pctAdm) {
		this.pctAdm = pctAdm;
	}
	public String getPctAdq() {
		return pctAdq;
	}
	public void setPctAdq(String pctAdq) {
		this.pctAdq = pctAdq;
	}
	public String getLinsegId() {
		return linsegId;
	}
	public void setLinsegId(String linsegId) {
		this.linsegId = linsegId;
	}
	public String getPctEnt() {
		return pctEnt;
	}
	public void setPctEnt(String pctEnt) {
		this.pctEnt = pctEnt;
	}
	public String getPctESMed() {
		return pctESMed;
	}
	public void setPctESMed(String pctESMed) {
		this.pctESMed = pctESMed;
	}
	public String getDescuentoRecargoTipo() {
		return descuentoRecargoTipo;
	}
	public void setDescuentoRecargoTipo(String descuentoRecargoTipo) {
		this.descuentoRecargoTipo = descuentoRecargoTipo;
	}
	public String getDescuentoRecargoPct() {
		return descuentoRecargoPct;
	}
	public void setDescuentoRecargoPct(String descuentoRecargoPct) {
		this.descuentoRecargoPct = descuentoRecargoPct;
	}
	public String getDescuentoElegidoPct() {
		return descuentoElegidoPct;
	}
	public void setDescuentoElegidoPct(String descuentoElegidoPct) {
		this.descuentoElegidoPct = descuentoElegidoPct;
	}
	public Character getGrupoNegocio() {
		return grupoNegocio;
	}
	public void setGrupoNegocio(Character grupoNegocio) {
		this.grupoNegocio = grupoNegocio;
	}
	public String getDescGrupoNegocio() {
		return descGrupoNegocio;
	}
	public void setDescGrupoNegocio(String descGrupoNegocio) {
		this.descGrupoNegocio = descGrupoNegocio;
	}
	@Override
	public int compareTo(ParametrizacionPolizaHelper arg0) {
		return this.descGrupoNegocio.compareTo(arg0.getDescGrupoNegocio());
	}
	
	public String getPctEsMedAjustado() {
		pctEsMedAjustado="";
		BigDecimal bPctESMed=null;
		BigDecimal bPctComisionMax=null;
		BigDecimal bCien=new BigDecimal(100);
		BigDecimal resOperacion=new BigDecimal(0.0);
		BigDecimal res=new BigDecimal(0.0);
		
		try {
			res.setScale(2, RoundingMode.HALF_UP);
			bPctESMed=new BigDecimal(this.getPctESMed().replace(",", "."));
			bPctComisionMax=new BigDecimal(this.getPctComMax().replace(",", "."));
			resOperacion=bPctESMed.multiply(bPctComisionMax).divide(bCien);
			//Ajustamos Comisión del colectivo
			res= resOperacion.setScale(2, RoundingMode.HALF_UP);
			//********************************************************
			//Ajustamos el descuento o recargo elegido
			if(!this.getDescuentoElegidoPct().isEmpty()){//Si es un descuento viene un valor negativo. Si es recargo positivo
				BigDecimal bPctElegido=new BigDecimal(this.getDescuentoElegidoPct().replace(",","."));
				BigDecimal resPctElegido=resOperacion.multiply(bPctElegido).divide(bCien);				
				BigDecimal resPctElegido2=res.add(resPctElegido);
				res=new BigDecimal(resPctElegido2.setScale(2, RoundingMode.HALF_UP).toString());
			}
			
			
			
			
			//********************************************************
			
			this.pctEsMedAjustado=res.toString();
		} catch (Exception e) {
			return "";
		}
		
		return pctEsMedAjustado;
	}
	public void setPctEsMedAjustado(String pctEsMedAjustado) {
		this.pctEsMedAjustado = pctEsMedAjustado;
	}	
	
	public String getPctEntidadAjustado() {
		BigDecimal bPctEnt=null;
		BigDecimal bPctComisionMax=null;
		BigDecimal bCien=new BigDecimal(100);
		BigDecimal resOperacion=new BigDecimal(0.0);
		BigDecimal res=new BigDecimal(0.0);
		try {
			bPctEnt=new BigDecimal(this.getPctEnt().replace(",", "."));
			bPctComisionMax=new BigDecimal(this.getPctComMax().replace(",", "."));
			resOperacion=bPctEnt.multiply(bPctComisionMax).divide(bCien);
			res= resOperacion.setScale(2, RoundingMode.HALF_UP);
			this.pctEntidadAjustado=res.toString();
		} catch (Exception e) {
			return "";
		}
		
		return this.pctEntidadAjustado;
	}
	public void setPctEntidadAjustado(String pctEntidadAjustado) {
		this.pctEntidadAjustado = pctEntidadAjustado;
	}

}