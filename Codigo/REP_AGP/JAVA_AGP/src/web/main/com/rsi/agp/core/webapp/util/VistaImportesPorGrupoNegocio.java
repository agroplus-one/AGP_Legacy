package com.rsi.agp.core.webapp.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VistaImportesPorGrupoNegocio implements Serializable {

	private static final long serialVersionUID = -2560158446108471576L;

	private static final String DEFAULT_TEXT = "N/D";
	// Id
	private String codGrupoNeg = "";
	private String descGrupNeg = "";
	// Cuadro de Importes
	private String primaComercial = DEFAULT_TEXT;
	private String primaNeta = DEFAULT_TEXT;
	private String reciboPrima = DEFAULT_TEXT;
	private String costeTomador = DEFAULT_TEXT;
	private String recargoConsorcio = DEFAULT_TEXT;
	private String costeNeto = DEFAULT_TEXT;

	// CUADRO BONIFICACIÓN/RECARGO
	private String bonifAsegurado = DEFAULT_TEXT;
	private String pctBonifAsegurado = DEFAULT_TEXT;
	private String recargoAsegurado = DEFAULT_TEXT;
	private String pctRecargoAsegurado = DEFAULT_TEXT;
	private String bonifMedidaPreventiva = DEFAULT_TEXT;
	private String pctMedidaPreventiva = DEFAULT_TEXT;
	private String descuentoContColectiva = DEFAULT_TEXT;
	private String pctDescContColectiva = DEFAULT_TEXT;
	private transient Map<String, String> boniRecargo1 = new HashMap<String, String>();
	private String recargoFraccionamiento = DEFAULT_TEXT;
	private String recargoAval = DEFAULT_TEXT;
	private String consorcioReaseguro = DEFAULT_TEXT;
	private String consorcioRecargo = DEFAULT_TEXT;

	// CUADRO SUBVENCIÓN ENESA
	private transient Map<String, String> subvEnesa = new HashMap<String, String>();

	// CUADRO SUBVENCIÓN CCAA
	private  transient Map<String, String> subvCCAA = new HashMap<String, String>();

	// COMISIONES
	String comMediadorE = DEFAULT_TEXT;
	String comMediadorE_S = DEFAULT_TEXT;
	String totalComisiones = DEFAULT_TEXT;
	String totalMediadorE_S = DEFAULT_TEXT;
	String totalMediadorE = DEFAULT_TEXT;
	// Operaciones
	BigDecimal primaNetaB;

	public VistaImportesPorGrupoNegocio() {
		super();
	}

	public String getPrimaComercial() {
		return primaComercial;
	}

	public void setPrimaComercial(String primaComercial) {
		this.primaComercial = primaComercial;
	}

	public String getPrimaNeta() {
		return primaNeta;
	}

	public void setPrimaNeta(String primaNeta) {
		this.primaNeta = primaNeta;
	}

	public String getRecargoConsorcio() {
		return recargoConsorcio;
	}

	public void setRecargoConsorcio(String recargoConsorcio) {
		this.recargoConsorcio = recargoConsorcio;
	}

	public String getReciboPrima() {
		return reciboPrima;
	}

	public void setReciboPrima(String reciboPrima) {
		this.reciboPrima = reciboPrima;
	}

	public String getCosteTomador() {
		return costeTomador;
	}

	public void setCosteTomador(String costeTomador) {
		this.costeTomador = costeTomador;
	}

	public String getBonifAsegurado() {
		return bonifAsegurado;
	}

	public void setBonifAsegurado(String bonifAsegurado) {
		this.bonifAsegurado = bonifAsegurado;
	}

	public String getPctBonifAsegurado() {
		return pctBonifAsegurado;
	}

	public void setPctBonifAsegurado(String pctBonifAsegurado) {
		this.pctBonifAsegurado = pctBonifAsegurado;
	}

	public String getRecargoAsegurado() {
		return recargoAsegurado;
	}

	public void setRecargoAsegurado(String recargoAsegurado) {
		this.recargoAsegurado = recargoAsegurado;
	}

	public String getPctRecargoAsegurado() {
		return pctRecargoAsegurado;
	}

	public void setPctRecargoAsegurado(String pctRecargoAsegurado) {
		this.pctRecargoAsegurado = pctRecargoAsegurado;
	}

	public String getPctMedidaPreventiva() {
		return pctMedidaPreventiva;
	}

	public void setPctMedidaPreventiva(String pctMedidaPreventiva) {
		this.pctMedidaPreventiva = pctMedidaPreventiva;
	}

	public String getPctDescContColectiva() {
		return pctDescContColectiva;
	}

	public void setPctDescContColectiva(String pctDescContColectiva) {
		this.pctDescContColectiva = pctDescContColectiva;
	}

	public String getBonifMedidaPreventiva() {
		return bonifMedidaPreventiva;
	}

	public void setBonifMedidaPreventiva(String bonifMedidaPreventiva) {
		this.bonifMedidaPreventiva = bonifMedidaPreventiva;
	}

	public String getDescuentoContColectiva() {
		return descuentoContColectiva;
	}

	public void setDescuentoContColectiva(String descuentoContColectiva) {
		this.descuentoContColectiva = descuentoContColectiva;
	}

	public String getConsorcioReaseguro() {
		return consorcioReaseguro;
	}

	public void setConsorcioReaseguro(String consorcioReaseguro) {
		this.consorcioReaseguro = consorcioReaseguro;
	}

	public String getConsorcioRecargo() {
		return consorcioRecargo;
	}

	public void setConsorcioRecargo(String consorcioRecargo) {
		this.consorcioRecargo = consorcioRecargo;
	}

	public String getRecargoAval() {
		return recargoAval;
	}

	public void setRecargoAval(String recargoAval) {
		this.recargoAval = recargoAval;
	}

	public String getRecargoFraccionamiento() {
		return recargoFraccionamiento;
	}

	public void setRecargoFraccionamiento(String recargoFraccionamiento) {
		this.recargoFraccionamiento = recargoFraccionamiento;
	}

	public Map<String, String> getBoniRecargo1() {
		return boniRecargo1;
	}

	public void addBoniRecargo1(String descripcion, String importe) {
		boniRecargo1.put(descripcion, importe);
	}

	public Map<String, String> getSubvEnesa() {
		return subvEnesa;
	}

	public Map<String, String> getSubvCCAA() {
		return subvCCAA;
	}

	public void addSubEnesa(String descripcion, String importe) {
		subvEnesa.put(descripcion, importe);
	}

	public void addSubCCAA(String descripcion, String importe) {
		subvCCAA.put(descripcion, importe);
	}

	public String getCodGrupoNeg() {
		return codGrupoNeg;
	}

	public void setCodGrupoNeg(String codGrupoNeg) {
		this.codGrupoNeg = codGrupoNeg;
	}

	public String getDescGrupNeg() {
		return descGrupNeg;
	}

	public void setDescGrupNeg(String descGrupNeg) {
		this.descGrupNeg = descGrupNeg;
	}

	public String getComMediadorE() {
		return comMediadorE;
	}

	public void setComMediadorE(String comMediadorE) {
		this.comMediadorE = comMediadorE;
	}

	public String getComMediadorE_S() {
		return comMediadorE_S;
	}

	public void setComMediadorE_S(String comMediadorE_S) {
		this.comMediadorE_S = comMediadorE_S;
	}

	public String getTotalComisiones() {
		return totalComisiones;
	}

	public void setTotalComisiones(String totalComisiones) {
		this.totalComisiones = totalComisiones;
	}

	public BigDecimal getPrimaNetaB() {
		return primaNetaB;
	}

	public void setPrimaNetaB(BigDecimal primaNetaB) {
		this.primaNetaB = primaNetaB;
	}

	public String getCosteNeto() {
		return costeNeto;
	}

	public void setCosteNeto(String costeNeto) {
		this.costeNeto = costeNeto;
	}

	public String getTotalMediadorE_S() {
		return totalMediadorE_S;
	}

	public void setTotalMediadorE_S(String totalMediadorE_S) {
		this.totalMediadorE_S = totalMediadorE_S;
	}

	public String getTotalMediadorE() {
		return totalMediadorE;
	}

	public void setTotalMediadorE(String totalMediadorE) {
		this.totalMediadorE = totalMediadorE;
	}

}