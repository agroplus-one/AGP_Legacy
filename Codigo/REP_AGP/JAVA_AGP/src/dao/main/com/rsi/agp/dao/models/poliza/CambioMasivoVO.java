package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CambioMasivoVO {

	
	private String parelas_cm;
	private Long polizaId;
	private List<Long> listaParcelas;
	private BigDecimal maxProduccion;
	private BigDecimal minProduccion;
	
	//ubicacion
	private String provincia_cm;
	private String comarca_cm;
	private String termino_cm;
	private String subtermino_cm;
	//sigpac
	private String  provSig_cm;
	private String termSig_cm;
	private String  agrSig_cm;
	private String zonaSig_cm;
	private String polSig_cm;
	private String parcSig_cm;
	private String recSig_cm;
	// cultivo y variedad
	private String varieda_cm;
	private String cultivo_cm;
	// produccion,superficie y precio 
	private String increne_ha_cm;
	private String increme_parcela_cm;
	private String inc_unidades_cm;
	private String superficie_cm;
	private String precio_cm;
	//datos variables
	private String destino_cm;
	private String tplantacion;
	private String sistcultivo;
	private String codtipomarcoplantac_cm;
	private String codpracticacultural_cm;
	private String fechaFinGarantia_cm;
	private String fechaSiembra;
	private String edad_cm;
	private String incEdad_cm;
	private String unidades_cm;
	private String sistproduccion_cm;
	
	 
	
	public CambioMasivoVO(){
		listaParcelas = new ArrayList<Long>();	
	}
	
	public String getVarieda_cm() {
		return varieda_cm;
	}
	public void setVarieda_cm(String varieda_cm) {
		this.varieda_cm = varieda_cm;
	}
	public String getCultivo_cm() {
		return cultivo_cm;
	}
	public void setCultivo_cm(String cultivo_cm) {
		this.cultivo_cm = cultivo_cm;
	}
	
	public String getParelas_cm() {
		return parelas_cm;
	}
	public void setParelas_cm(String parelas_cm) {
		this.parelas_cm = parelas_cm;
	}
	public Long getPolizaId() {
		return polizaId;
	}
	public void setPolizaId(Long polizaId) {
		this.polizaId = polizaId;
	}
	public List<Long> getListaParcelas() {
		return listaParcelas;
	}
	public void setListaParcelas(List<Long> listaParcelas) {
		this.listaParcelas = listaParcelas;
	}

	public String getIncrene_ha_cm() {
		return increne_ha_cm;
	}

	public void setIncrene_ha_cm(String increne_ha_cm) {
		this.increne_ha_cm = increne_ha_cm;
	}

	public String getIncreme_parcela_cm() {
		return increme_parcela_cm;
	}

	public void setIncreme_parcela_cm(String increme_parcela_cm) {
		this.increme_parcela_cm = increme_parcela_cm;
	}

	public BigDecimal getMaxProduccion() {
		return maxProduccion;
	}

	public void setMaxProduccion(BigDecimal maxProduccion) {
		this.maxProduccion = maxProduccion;
	}

	public BigDecimal getMinProduccion() {
		return minProduccion;
	}

	public void setMinProduccion(BigDecimal minProduccion) {
		this.minProduccion = minProduccion;
	}

	public String getSuperficie_cm() {
		return superficie_cm;
	}

	public void setSuperficie_cm(String superficie_cm) {
		this.superficie_cm = superficie_cm;
	}

	public String getFechaSiembra() {
		return fechaSiembra;
	}

	public void setFechaSiembra(String fechaSiembra) {
		this.fechaSiembra = fechaSiembra;
	}

	public String getDestino_cm() {
		return destino_cm;
	}

	public void setDestino_cm(String destino_cm) {
		this.destino_cm = destino_cm;
	}

	public String getTplantacion() {
		return tplantacion;
	}

	public void setTplantacion(String tplantacion) {
		this.tplantacion = tplantacion;
	}

	public String getProvSig_cm() {
		return provSig_cm;
	}

	public void setProvSig_cm(String provSig_cm) {
		this.provSig_cm = provSig_cm;
	}

	public String getTermSig_cm() {
		return termSig_cm;
	}

	public void setTermSig_cm(String termSig_cm) {
		this.termSig_cm = termSig_cm;
	}

	public String getAgrSig_cm() {
		return agrSig_cm;
	}

	public void setAgrSig_cm(String agrSig_cm) {
		this.agrSig_cm = agrSig_cm;
	}

	public String getZonaSig_cm() {
		return zonaSig_cm;
	}

	public void setZonaSig_cm(String zonaSig_cm) {
		this.zonaSig_cm = zonaSig_cm;
	}

	public String getPolSig_cm() {
		return polSig_cm;
	}

	public void setPolSig_cm(String polSig_cm) {
		this.polSig_cm = polSig_cm;
	}

	public String getParcSig_cm() {
		return parcSig_cm;
	}

	public void setParcSig_cm(String parcSig_cm) {
		this.parcSig_cm = parcSig_cm;
	}

	public String getRecSig_cm() {
		return recSig_cm;
	}

	public void setRecSig_cm(String recSig_cm) {
		this.recSig_cm = recSig_cm;
	}

	public String getProvincia_cm() {
		return provincia_cm;
	}

	public void setProvincia_cm(String provincia_cm) {
		this.provincia_cm = provincia_cm;
	}

	public String getComarca_cm() {
		return comarca_cm;
	}

	public void setComarca_cm(String comarca_cm) {
		this.comarca_cm = comarca_cm;
	}

	public String getTermino_cm() {
		return termino_cm;
	}

	public void setTermino_cm(String termino_cm) {
		this.termino_cm = termino_cm;
	}

	public String getSubtermino_cm() {
		return subtermino_cm;
	}

	public void setSubtermino_cm(String subtermino_cm) {
		this.subtermino_cm = subtermino_cm;
	}

	public String getInc_unidades_cm() {
		return inc_unidades_cm;
	}

	public void setInc_unidades_cm(String inc_unidades_cm) {
		this.inc_unidades_cm = inc_unidades_cm;
	}

	public String getPrecio_cm() {
		return precio_cm;
	}

	public void setPrecio_cm(String precio_cm) {
		this.precio_cm = precio_cm;
	}

	public String getSistcultivo() {
		return sistcultivo;
	}

	public void setSistcultivo(String sistcultivo) {
		this.sistcultivo = sistcultivo;
	}

	public String getCodtipomarcoplantac_cm() {
		return codtipomarcoplantac_cm;
	}

	public void setCodtipomarcoplantac_cm(String codtipomarcoplantac_cm) {
		this.codtipomarcoplantac_cm = codtipomarcoplantac_cm;
	}

	public String getCodpracticacultural_cm() {
		return codpracticacultural_cm;
	}

	public void setCodpracticacultural_cm(String codpracticacultural_cm) {
		this.codpracticacultural_cm = codpracticacultural_cm;
	}

	public String getFechaFinGarantia_cm() {
		return fechaFinGarantia_cm;
	}

	public void setFechaFinGarantia_cm(String fechaFinGarantia_cm) {
		this.fechaFinGarantia_cm = fechaFinGarantia_cm;
	}

	public String getEdad_cm() {
		return edad_cm;
	}

	public void setEdad_cm(String edad_cm) {
		this.edad_cm = edad_cm;
	}

	public String getIncEdad_cm() {
		return incEdad_cm;
	}

	public void setIncEdad_cm(String incEdad_cm) {
		this.incEdad_cm = incEdad_cm;
	}

	public String getUnidades_cm() {
		return unidades_cm;
	}

	public void setUnidades_cm(String unidades_cm) {
		this.unidades_cm = unidades_cm;
	}

	public String getSistproduccion_cm() {
		return sistproduccion_cm;
	}

	public void setSistproduccion_cm(String sistproduccion_cm) {
		this.sistproduccion_cm = sistproduccion_cm;
	}

	

	
}
