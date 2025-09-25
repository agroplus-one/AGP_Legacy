/**
 * 
 */
package com.rsi.agp.core.webapp.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015;

/**
 * @author T-Systems
 *
 *         Encapsula los datos necesarios para la pantalla de Importes
 */
public class FluxCondensatorObject implements Serializable {

	private static final long serialVersionUID = 6753746513719462303L;

	private static final String DEFAULT_TEXT = "N/D";

	private String conceptoPpalMod = DEFAULT_TEXT;
	private String idModulo = DEFAULT_TEXT;
	private String descModulo = DEFAULT_TEXT;
	private String riesgoCubierto = DEFAULT_TEXT;
	private String concepto = DEFAULT_TEXT;
	private String valor = DEFAULT_TEXT;
	private String primaComercial = DEFAULT_TEXT;
	private String primaNeta = DEFAULT_TEXT;
	private BigDecimal primaNetaB;
	private String costeNeto = DEFAULT_TEXT;
	private String bonifAsegurado = DEFAULT_TEXT;
	private String pctBonifAsegurado = DEFAULT_TEXT;
	private String recargoAsegurado = DEFAULT_TEXT;
	private String pctRecargoAsegurado = DEFAULT_TEXT;
	private String pctMedidaPreventiva = DEFAULT_TEXT;
	private String bonifMedidaPreventiva = DEFAULT_TEXT;
	private String pctDescContColectiva = DEFAULT_TEXT;
	private String descuentoContColectiva = DEFAULT_TEXT;
	private String reaseguroConsorcio = DEFAULT_TEXT;
	private String recargoConsorcio = DEFAULT_TEXT;
	private String totalENESA = DEFAULT_TEXT;
	private String importeTomador = DEFAULT_TEXT;
	private String admiteComplementario = DEFAULT_TEXT;
	private String consorcioReaseguro = DEFAULT_TEXT;
	private String consorcioRecargo = DEFAULT_TEXT;
	private String comparativaCompleta = DEFAULT_TEXT;
	private String totalProduccion = DEFAULT_TEXT;
	private String idEnvioComp = DEFAULT_TEXT;
	private String reciboPrima = DEFAULT_TEXT;
	private String costeTomador = DEFAULT_TEXT;
	private String recargoAval = DEFAULT_TEXT;
	private String recargoFraccionamiento = DEFAULT_TEXT;
	private String comMediadorE = DEFAULT_TEXT;
	private String comMediadorE_S = DEFAULT_TEXT;
	private String comparativaSeleccionada = null; // Para guardar el identificador de la comparativa
	private transient Map<String, String> subvEnesa = new HashMap<String, String>();
	private transient Map<String, String> subvCCAA = new HashMap<String, String>();
	private transient Map<String, String> boniRecargo1 = new HashMap<String, String>();
	private transient Set<DistCosteParcela2015> distCosteParcela2015s = new HashSet<DistCosteParcela2015>(0);
	private Boolean muestraBotonFinanciar;
	private Integer opcionFracc = null;
	private BigDecimal valorOpcionFracc = null;
	private boolean esFraccAgr = false;
	private BigDecimal pctRecargoFraccAgr = new BigDecimal(0);
	private String importePagoFraccAgr = null;
	private String importePagoFracc = null;
	private String periodoFracc = null;
	private String totalCosteTomador = null;

	// para el popup de financiacion
	String pctMinFinanSobreCosteTomador = null;
	// *********************************************************

	public Boolean getMuestraBotonFinanciar() {
		return muestraBotonFinanciar;
	}

	public void setMuestraBotonFinanciar(Boolean muestraBotonFinanciar) {
		this.muestraBotonFinanciar = muestraBotonFinanciar;
	}

	/**
	 * @return the conceptoPpalMod
	 */
	public String getConceptoPpalMod() {
		return conceptoPpalMod;
	}

	/**
	 * @param conceptoPpalMod
	 *            the conceptoPpalMod to set
	 */
	public void setConceptoPpalMod(String conceptoPpalMod) {
		this.conceptoPpalMod = conceptoPpalMod;
	}

	/**
	 * @return the idModulo
	 */
	public String getIdModulo() {
		return idModulo;
	}

	/**
	 * @param idModulo
	 *            the idModulo to set
	 */
	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}

	/**
	 * @return the descModulo
	 */
	public String getDescModulo() {
		return descModulo;
	}

	/**
	 * @param descModulo
	 *            the descModulo to set
	 */
	public void setDescModulo(String descModulo) {
		this.descModulo = descModulo;
	}

	/**
	 * @return the riesgoCubierto
	 */
	public String getRiesgoCubierto() {
		return riesgoCubierto;
	}

	/**
	 * @param riesgoCubierto
	 *            the riesgoCubierto to set
	 */
	public void setRiesgoCubierto(String riesgoCubierto) {
		this.riesgoCubierto = riesgoCubierto;
	}

	/**
	 * @return the concepto
	 */
	public String getConcepto() {
		return concepto;
	}

	/**
	 * @param tipoFranquicia
	 *            the tipoFranquicia to set
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor
	 *            the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return the primaComercial
	 */
	public String getPrimaComercial() {
		return primaComercial;
	}

	/**
	 * @param primaComercial
	 *            the primaComercial to set
	 */
	public void setPrimaComercial(String primaComercial) {
		this.primaComercial = primaComercial;
	}

	/**
	 * @return the primaNeta
	 */
	public String getPrimaNeta() {
		return primaNeta;
	}

	/**
	 * @param primaNeta
	 *            the primaNeta to set
	 */
	public void setPrimaNeta(String primaNeta) {
		this.primaNeta = primaNeta;
	}

	/**
	 * @return the costeNeto
	 */
	public String getCosteNeto() {
		return costeNeto;
	}

	/**
	 * @param costeNeto
	 *            the costeNeto to set
	 */
	public void setCosteNeto(String costeNeto) {
		this.costeNeto = costeNeto;
	}

	/**
	 * @return the bonifAsegurado
	 */
	public String getBonifAsegurado() {
		return bonifAsegurado;
	}

	/**
	 * @param bonifAsegurado
	 *            the bonifAsegurado to set
	 */
	public void setBonifAsegurado(String bonifAsegurado) {
		this.bonifAsegurado = bonifAsegurado;
	}

	/**
	 * @return the pctBonifAsegurado
	 */
	public String getPctBonifAsegurado() {
		return pctBonifAsegurado;
	}

	/**
	 * @param pctBonifAsegurado
	 *            the pctBonifAsegurado to set
	 */
	public void setPctBonifAsegurado(String pctBonifAsegurado) {
		this.pctBonifAsegurado = pctBonifAsegurado;
	}

	/**
	 * @return the recargoAsegurado
	 */
	public String getRecargoAsegurado() {
		return recargoAsegurado;
	}

	/**
	 * @param recargoAsegurado
	 *            the recargoAsegurado to set
	 */
	public void setRecargoAsegurado(String recargoAsegurado) {
		this.recargoAsegurado = recargoAsegurado;
	}

	/**
	 * @return the pctRecargoAsegurado
	 */
	public String getPctRecargoAsegurado() {
		return pctRecargoAsegurado;
	}

	/**
	 * @param pctRecargoAsegurado
	 *            the pctRecargoAsegurado to set
	 */
	public void setPctRecargoAsegurado(String pctRecargoAsegurado) {
		this.pctRecargoAsegurado = pctRecargoAsegurado;
	}

	/**
	 * @return the pctMedidaPreventiva
	 */
	public String getPctMedidaPreventiva() {
		return pctMedidaPreventiva;
	}

	/**
	 * @param pctMedidaPreventiva
	 *            the pctMedidaPreventiva to set
	 */
	public void setPctMedidaPreventiva(String pctMedidaPreventiva) {
		this.pctMedidaPreventiva = pctMedidaPreventiva;
	}

	/**
	 * @return the pctDescContColectiva
	 */
	public String getPctDescContColectiva() {
		return pctDescContColectiva;
	}

	/**
	 * @param pctDescContColectiva
	 *            the pctDescContColectiva to set
	 */
	public void setPctDescContColectiva(String pctDescContColectiva) {
		this.pctDescContColectiva = pctDescContColectiva;
	}

	/**
	 * @return the bonifMedidaPreventiva
	 */
	public String getBonifMedidaPreventiva() {
		return bonifMedidaPreventiva;
	}

	/**
	 * @param bonifMedidaPreventiva
	 *            the bonifMedidaPreventiva to set
	 */
	public void setBonifMedidaPreventiva(String bonifMedidaPreventiva) {
		this.bonifMedidaPreventiva = bonifMedidaPreventiva;
	}

	/**
	 * @return the descuentoContColectiva
	 */
	public String getDescuentoContColectiva() {
		return descuentoContColectiva;
	}

	/**
	 * @param descuentoContColectiva
	 *            the descuentoContColectiva to set
	 */
	public void setDescuentoContColectiva(String descuentoContColectiva) {
		this.descuentoContColectiva = descuentoContColectiva;
	}

	/**
	 * @return the reaseguroConsorcio
	 */
	public String getReaseguroConsorcio() {
		return reaseguroConsorcio;
	}

	/**
	 * @param reaseguroConsorcio
	 *            the reaseguroConsorcio to set
	 */
	public void setReaseguroConsorcio(String reaseguroConsorcio) {
		this.reaseguroConsorcio = reaseguroConsorcio;
	}

	/**
	 * @return the recargoConsorcio
	 */
	public String getRecargoConsorcio() {
		return recargoConsorcio;
	}

	/**
	 * @param recargoConsorcio
	 *            the recargoConsorcio to set
	 */
	public void setRecargoConsorcio(String recargoConsorcio) {
		this.recargoConsorcio = recargoConsorcio;
	}

	/**
	 * @return the totalENESA
	 */
	public String getTotalENESA() {
		return totalENESA;
	}

	/**
	 * @param totalENESA
	 *            the totalENESA to set
	 */
	public void setTotalENESA(String totalENESA) {
		this.totalENESA = totalENESA;
	}

	/**
	 * @return the importeTomador
	 */
	public String getImporteTomador() {
		return importeTomador;
	}

	/**
	 * @param importeTomador
	 *            the importeTomador to set
	 */
	public void setImporteTomador(String importeTomador) {
		this.importeTomador = importeTomador;
	}

	/**
	 * @return the admiteComplementario
	 */
	public String getAdmiteComplementario() {
		return admiteComplementario;
	}

	/**
	 * @param admiteComplementario
	 *            the admiteComplementario to set
	 */
	public void setAdmiteComplementario(String admiteComplementario) {
		this.admiteComplementario = admiteComplementario;
	}

	/**
	 * @return the consorcioReaseguro
	 */
	public String getConsorcioReaseguro() {
		return consorcioReaseguro;
	}

	/**
	 * @param consorcioReaseguro
	 *            the consorcioReaseguro to set
	 */
	public void setConsorcioReaseguro(String consorcioReaseguro) {
		this.consorcioReaseguro = consorcioReaseguro;
	}

	/**
	 * @return the consorcioRecargo
	 */
	public String getConsorcioRecargo() {
		return consorcioRecargo;
	}

	/**
	 * @param consorcioRecargo
	 *            the consorcioRecargo to set
	 */
	public void setConsorcioRecargo(String consorcioRecargo) {
		this.consorcioRecargo = consorcioRecargo;
	}

	/**
	 * @return the subvEnesa
	 */
	public Map<String, String> getSubvEnesa() {
		return subvEnesa;
	}

	/**
	 * @return the subvCCAA
	 */
	public Map<String, String> getSubvCCAA() {
		return subvCCAA;
	}

	/**
	 * 
	 * @param descripcion
	 * @param importe
	 */
	public void addSubEnesa(String descripcion, String importe) {
		subvEnesa.put(descripcion, importe);
	}

	/**
	 * 
	 * @param descripcion
	 * @param importe
	 */
	public void addSubCCAA(String descripcion, String importe) {
		subvCCAA.put(descripcion, importe);
	}

	/**
	 * @return the comparativaSeleccionada
	 */
	public String getComparativaSeleccionada() {
		return comparativaSeleccionada;
	}

	/**
	 * @param comparativaSeleccionada
	 *            the comparativaSeleccionada to set
	 */
	public void setComparativaSeleccionada(String comparativaSeleccionada) {
		this.comparativaSeleccionada = comparativaSeleccionada;
	}

	public String getComparativaCompleta() {
		return comparativaCompleta;
	}

	public void setComparativaCompleta(String comparativaCompleta) {
		this.comparativaCompleta = comparativaCompleta;
	}

	public String getTotalProduccion() {
		return totalProduccion;
	}

	public void setTotalProduccion(String totalProduccion) {
		this.totalProduccion = totalProduccion;
	}

	public String getIdEnvioComp() {
		return idEnvioComp;
	}

	public void setIdEnvioComp(String idEnvioComp) {
		this.idEnvioComp = idEnvioComp;
	}

	@Override
	public String toString() {
		String cadena = "";
		cadena += "; conceptoPpalMod = " + this.conceptoPpalMod;
		cadena += "; idModulo = " + idModulo;
		cadena += "; descModulo = " + descModulo;
		cadena += "; riesgoCubierto = " + riesgoCubierto;
		cadena += "; concepto = " + concepto;
		cadena += "; valor = " + valor;
		cadena += "; primaComercial = " + primaComercial;
		cadena += "; primaNeta = " + primaNeta;
		cadena += "; costeNeto = " + costeNeto;
		cadena += "; bonifAsegurado = " + bonifAsegurado;
		cadena += "; pctBonifAsegurado = " + pctBonifAsegurado;
		cadena += "; recargoAsegurado = " + recargoAsegurado;
		cadena += "; pctRecargoAsegurado = " + pctRecargoAsegurado;
		cadena += "; pctMedidaPreventiva = " + pctMedidaPreventiva;
		cadena += "; bonifMedidaPreventiva = " + bonifMedidaPreventiva;
		cadena += "; pctDescContColectiva = " + pctDescContColectiva;
		cadena += "; descuentoContColectiva = " + descuentoContColectiva;
		cadena += "; reaseguroConsorcio = " + reaseguroConsorcio;
		cadena += "; recargoConsorcio = " + recargoConsorcio;
		cadena += "; totalENESA = " + totalENESA;
		cadena += "; importeTomador = " + importeTomador;
		cadena += "; admiteComplementario = " + admiteComplementario;
		cadena += "; consorcioReaseguro = " + consorcioReaseguro;
		cadena += "; consorcioRecargo = " + consorcioRecargo;
		cadena += "; comparativaCompleta = " + comparativaCompleta;
		cadena += "; totalProduccion = " + totalProduccion;
		cadena += "; idEnvioComp = " + idEnvioComp;
		cadena += "; comparativaSeleccionada = " + comparativaSeleccionada;

		return cadena;
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

	public void setSubvEnesa(Map<String, String> subvEnesa) {
		this.subvEnesa = subvEnesa;
	}

	public void setSubvCCAA(Map<String, String> subvCCAA) {
		this.subvCCAA = subvCCAA;
	}

	public void setCosteTomador(String costeTomador) {
		this.costeTomador = costeTomador;
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

	public Map<String, String> getBoniRecargo1() {
		return boniRecargo1;
	}

	public void addBoniRecargo1(String descripcion, String importe) {
		boniRecargo1.put(descripcion, importe);
	}

	public Set<DistCosteParcela2015> getDistCosteParcela2015s() {
		return distCosteParcela2015s;
	}

	public void setDistCosteParcela2015s(Set<DistCosteParcela2015> distCosteParcela2015s) {
		this.distCosteParcela2015s = distCosteParcela2015s;
	}

	public BigDecimal getPrimaNetaB() {
		return primaNetaB;
	}

	public void setPrimaNetaB(BigDecimal primaNetaB) {
		this.primaNetaB = primaNetaB;
	}

	public Integer getOpcionFracc() {
		return opcionFracc;
	}

	public void setOpcionFracc(Integer opcionFracc) {
		this.opcionFracc = opcionFracc;
	}

	public BigDecimal getValorOpcionFracc() {
		return valorOpcionFracc;
	}

	public void setValorOpcionFracc(BigDecimal valorOpcionFracc) {
		this.valorOpcionFracc = valorOpcionFracc;
	}

	public boolean isEsFraccAgr() {
		return esFraccAgr;
	}

	public void setEsFraccAgr(boolean esFraccAgr) {
		this.esFraccAgr = esFraccAgr;
	}

	public BigDecimal getPctRecargoFraccAgr() {
		return pctRecargoFraccAgr;
	}

	public void setPctRecargoFraccAgr(BigDecimal pctRecargoFraccAgr) {
		this.pctRecargoFraccAgr = pctRecargoFraccAgr;
	}

	public String getImportePagoFraccAgr() {
		return importePagoFraccAgr;
	}

	public void setImportePagoFraccAgr(String importePagoFraccAgr) {
		this.importePagoFraccAgr = importePagoFraccAgr;
	}

	public String getImportePagoFracc() {
		return importePagoFracc;
	}

	public void setImportePagoFracc(String importePagoFracc) {
		this.importePagoFracc = importePagoFracc;
	}

	public String getPeriodoFracc() {
		return periodoFracc;
	}

	public void setPeriodoFracc(String periodoFracc) {
		this.periodoFracc = periodoFracc;
	}

	public String getTotalCosteTomador() {
		return totalCosteTomador;
	}

	public void setTotalCosteTomador(String totalCosteTomador) {
		this.totalCosteTomador = totalCosteTomador;
	}

	public String getPctMinFinanSobreCosteTomador() {
		return pctMinFinanSobreCosteTomador;
	}

	public void setPctMinFinanSobreCosteTomador(String pctMinFinanSobreCosteTomador) {
		this.pctMinFinanSobreCosteTomador = pctMinFinanSobreCosteTomador;
	}
}