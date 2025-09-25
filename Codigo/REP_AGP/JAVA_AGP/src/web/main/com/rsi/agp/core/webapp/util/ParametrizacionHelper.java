package com.rsi.agp.core.webapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ParametrizacionHelper implements Comparable<ParametrizacionHelper>{

	String porComisionMax="";
	String porAdmin="";
	String porAdq="";
	String codGrupoNegocio="";
	String descGrupoNecio="";
	String porEnt="";
	String porESMed="";
	String porcentajeRecargo="";
	String tipoDctoRecargo="";
	String pctEsMedAjustado="";
	String pctEntidadAjustado="";
	
	public String getPorComisionMax() {
		return porComisionMax;
	}
	public void setPorComisionMax(String porComisionMax) {
		this.porComisionMax = porComisionMax;
	}
	public String getPorAdmin() {
		return porAdmin;
	}
	public void setPorAdmin(String porAdmin) {
		this.porAdmin = porAdmin;
	}
	public String getPorAdq() {
		return porAdq;
	}
	public void setPorAdq(String porAdq) {
		this.porAdq = porAdq;
	}
	public String getCodGrupoNegocio() {
		return codGrupoNegocio;
	}
	public void setCodGrupoNegocio(String codGrupoNegocio) {
		this.codGrupoNegocio = codGrupoNegocio;
	}
	public String getDescGrupoNecio() {
		return descGrupoNecio;
	}
	public void setDescGrupoNecio(String descGrupoNecio) {
		this.descGrupoNecio = descGrupoNecio;
	}
	public String getPorEnt() {
		return porEnt;
	}
	public void setPorEnt(String porEnt) {
		this.porEnt = porEnt;
	}
	public String getPorESMed() {
		return porESMed;
	}
	public void setPorESMed(String porESMed) {
		this.porESMed = porESMed;
	}
	public String getPorcentajeRecargo() {
		return porcentajeRecargo;
	}
	public void setPorcentajeRecargo(String porcentajeRecargo) {
		this.porcentajeRecargo = porcentajeRecargo;
	}
	@Override
	public int compareTo(ParametrizacionHelper arg0) {
		return this.descGrupoNecio.compareTo(arg0.getDescGrupoNecio());
	}
	public String getTipoDctoRecargo() {
		return tipoDctoRecargo;
	}
	public void setTipoDctoRecargo(String tipoDctoRecargo) {
		this.tipoDctoRecargo = tipoDctoRecargo;
	}
	public String getPctEsMedAjustado() {
		pctEsMedAjustado="";
		BigDecimal bPctESMed=null;
		BigDecimal bPctComisionMax=null;
		BigDecimal bPctDescRec=null;
		BigDecimal bCien=new BigDecimal(100);
		BigDecimal resOperacion=new BigDecimal(0.0);
		BigDecimal resOperacion2=new BigDecimal(0.0);
		BigDecimal resOperacion3=new BigDecimal(0.0);
		BigDecimal res=new BigDecimal(0.0);
		//BigDecimal db = new BigDecimal(d).setScale(12, BigDecimal.ROUND_HALF_UP);
		try {
			res.setScale(2, RoundingMode.HALF_UP);
			bPctESMed=new BigDecimal(this.getPorESMed().replace(",", "."));
			bPctComisionMax=new BigDecimal(this.getPorComisionMax().replace(",", "."));
			resOperacion=bPctESMed.multiply(bPctComisionMax).divide(bCien);
			
			if (this.getTipoDctoRecargo().equals("0")){//Descuento
				if(!this.getPorEnt().isEmpty()){
					bPctDescRec=new BigDecimal(this.getPorcentajeRecargo().replace(",", "."));					
					resOperacion2=resOperacion.multiply(bPctDescRec).divide(bCien);
					resOperacion3=resOperacion.subtract(resOperacion2);
					res= resOperacion3.setScale(2, RoundingMode.HALF_UP);
				}
			}else if(this.getTipoDctoRecargo().equals("1")){//Recargo
				bPctDescRec=new BigDecimal(this.getPorcentajeRecargo().replace(",", "."));					
				resOperacion2=resOperacion.multiply(bPctDescRec).divide(bCien);
				resOperacion3=resOperacion.add(resOperacion2);
				res= resOperacion3.setScale(2, RoundingMode.HALF_UP);
			}else{
				res= resOperacion.setScale(2, RoundingMode.HALF_UP);
			}
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
			bPctEnt=new BigDecimal(this.getPorEnt().replace(",", "."));
			bPctComisionMax=new BigDecimal(this.getPorComisionMax().replace(",", "."));
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
