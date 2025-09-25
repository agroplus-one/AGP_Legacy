package com.rsi.agp.core.webapp.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rsi.agp.core.util.VistaImportesGNComparator;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteParcela2015;

public class VistaImportes implements Serializable {

	private static final long serialVersionUID = 2684998061944812001L;

	final String defaultText = "N/D";
	String conceptoPpalMod = defaultText;
	String idModulo = defaultText;
	String descModulo = defaultText;
	String reaseguroConsorcio = defaultText;
	// Este campo debería desaparecer. Lo dejamos mientras las pólizas de agrario no
	// sean en formato unificado
	String importeTomador = defaultText;
	// *******************************************************************************************************
	String totalCosteTomador = null;
	String idEnvioComp = defaultText;
	Boolean muestraBotonFinanciar;
	boolean esFraccAgr = false;
	String importePagoFraccAgr = null;
	String importePagoFracc = null;
	String periodoFracc = null;
	String admiteComplementario = defaultText;
	String comparativaSeleccionada = null; // Para guardar el identificador de la comparativa
	String comparativaCompleta = defaultText;
	String totalProduccion = defaultText;
	String comMediadorE = defaultText;
	String comMediadorE_S = defaultText;
	Set<DistCosteParcela2015> distCosteParcela2015s = new HashSet<DistCosteParcela2015>(0);
	Integer opcionFracc = null;
	BigDecimal valorOpcionFracc = null;
	BigDecimal pctRecargoFraccAgr = new BigDecimal(0);

	List<VistaImportesPorGrupoNegocio> vistaImportesPorGrupoNegocio = new ArrayList<VistaImportesPorGrupoNegocio>();

	// para el popup de financiacion
	String pctMinFinanSobreCosteTomador = null;
	// *********************************************************

	String riesgoCubierto = defaultText;
	String totalENESA = defaultText;
	String concepto = defaultText;
	String valor = defaultText;
	BigDecimal primaNetaB;

	// para la distribución de costes
	String totalCosteTomadorAFinanciar = null;

	public VistaImportes() {
		super();
	}

	public Boolean getMuestraBotonFinanciar() {
		return muestraBotonFinanciar;
	}

	public void setMuestraBotonFinanciar(Boolean muestraBotonFinanciar) {
		this.muestraBotonFinanciar = muestraBotonFinanciar;
	}

	public String getConceptoPpalMod() {
		return conceptoPpalMod;
	}

	public void setConceptoPpalMod(String conceptoPpalMod) {
		this.conceptoPpalMod = conceptoPpalMod;
	}

	public String getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}

	public String getDescModulo() {
		return descModulo;
	}

	public void setDescModulo(String descModulo) {
		this.descModulo = descModulo;
	}

	public String getRiesgoCubierto() {
		return riesgoCubierto;
	}

	public void setRiesgoCubierto(String riesgoCubierto) {
		this.riesgoCubierto = riesgoCubierto;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getReaseguroConsorcio() {
		return reaseguroConsorcio;
	}

	public void setReaseguroConsorcio(String reaseguroConsorcio) {
		this.reaseguroConsorcio = reaseguroConsorcio;
	}

	public String getTotalENESA() {
		return totalENESA;
	}

	public void setTotalENESA(String totalENESA) {
		this.totalENESA = totalENESA;
	}

	public String getImporteTomador() {
		return importeTomador;
	}

	public void setImporteTomador(String importeTomador) {
		this.importeTomador = importeTomador;
	}

	public String getAdmiteComplementario() {
		return admiteComplementario;
	}

	public void setAdmiteComplementario(String admiteComplementario) {
		this.admiteComplementario = admiteComplementario;
	}

	public String getComparativaSeleccionada() {
		return comparativaSeleccionada;
	}

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
		// cadena += "; costeNeto = " + costeNeto;
		cadena += "; reaseguroConsorcio = " + reaseguroConsorcio;
		cadena += "; totalENESA = " + totalENESA;
		cadena += "; importeTomador = " + importeTomador;
		cadena += "; admiteComplementario = " + admiteComplementario;
		cadena += "; comparativaCompleta = " + comparativaCompleta;
		cadena += "; totalProduccion = " + totalProduccion;
		cadena += "; idEnvioComp = " + idEnvioComp;
		cadena += "; comparativaSeleccionada = " + comparativaSeleccionada;

		return cadena;
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

	public List<VistaImportesPorGrupoNegocio> getVistaImportesPorGrupoNegocio() {
		if (vistaImportesPorGrupoNegocio != null)
			Collections.sort(vistaImportesPorGrupoNegocio, new VistaImportesGNComparator());
		return vistaImportesPorGrupoNegocio;
	}

	public void setVistaImportesPorGrupoNegocio(List<VistaImportesPorGrupoNegocio> vistaImportesPorGrupoNegocio) {
		this.vistaImportesPorGrupoNegocio = vistaImportesPorGrupoNegocio;
	}

	public String getTotalCosteTomadorAFinanciar() {
		return totalCosteTomadorAFinanciar;
	}

	public void setTotalCosteTomadorAFinanciar(String totalCosteTomadorAFinanciar) {
		this.totalCosteTomadorAFinanciar = totalCosteTomadorAFinanciar;
	}
}