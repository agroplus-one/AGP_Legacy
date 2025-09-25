package com.rsi.agp.dao.tables.anexo;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.tables.cgen.Destino;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Variedad;

public class AnexoModSWCambioMasivo {
	
	private Long idPoliza;
	private Long idAnexoMod;
	
	
	private List<Long> listParcelas;
	private String idCupon;
	
	//ubicacion
	
	private Termino termino_cm;
	private Character subtermino_cm;
	
	//sigpac
	private String  provSig_cm;
	private String termSig_cm;
	private String  agrSig_cm;
	private String zonaSig_cm;
	private String polSig_cm;
	private String parcSig_cm;
	private String recSig_cm;
	// cultivo y variedad
	private Cultivo cultivo;
	private Variedad variedad;
	// produccion,superficie y precio 
	private BigDecimal increHa;
	private BigDecimal increParcela;
	private BigDecimal inc_unidades_cm;
	private BigDecimal superficie;
	private BigDecimal precio_cm;
	//datos variables
	private Destino destino;
	private TipoPlantacion tipoPlantacion;
	private SistemaCultivo sistemaCultivo;
	private String codtipomarcoplantac_cm;
	private BigDecimal codpracticacultural_cm;
	private String fechaFinGarantia_cm;
	private String fechaSiembra;
	private String edad_cm;
	private String incEdad_cm;
	private String unidades_cm;
	private SistemaProduccion sistemaProduccion;
	
	
	public AnexoModSWCambioMasivo() {
		cultivo = new Cultivo();
		variedad = new Variedad();
		destino = new Destino();
		tipoPlantacion = new TipoPlantacion();
		sistemaCultivo = new SistemaCultivo();
		termino_cm = new Termino();
		destino = new Destino();
		tipoPlantacion = new TipoPlantacion ();
		sistemaCultivo = new SistemaCultivo();
		sistemaProduccion= new SistemaProduccion();
	}
	
	public Cultivo getCultivo() {
		return cultivo;
	}
	public void setCultivo(Cultivo cultivo) {
		this.cultivo = cultivo;
	}
	public Variedad getVariedad() {
		return variedad;
	}
	public void setVariedad(Variedad variedad) {
		this.variedad = variedad;
	}
	
	public BigDecimal getIncreHa() {
		return increHa;
	}
	public void setIncreHa(BigDecimal increHa) {
		this.increHa = increHa;
	}
	public BigDecimal getIncreParcela() {
		return increParcela;
	}
	public void setIncreParcela(BigDecimal increParcela) {
		this.increParcela = increParcela;
	}
	public BigDecimal getSuperficie() {
		return superficie;
	}
	public void setSuperficie(BigDecimal superficie) {
		this.superficie = superficie;
	}
	public String getFechaSiembra() {
		return fechaSiembra;
	}
	public void setFechaSiembra(String fechaSiembra) {
		this.fechaSiembra = fechaSiembra;
	}
	public Destino getDestino() {
		return destino;
	}
	public void setDestino(Destino destino) {
		this.destino = destino;
	}
	public TipoPlantacion getTipoPlantacion() {
		return tipoPlantacion;
	}
	public void setTipoPlantacion(TipoPlantacion tipoPlantacion) {
		this.tipoPlantacion = tipoPlantacion;
	}
	public SistemaCultivo getSistemaCultivo() {
		return sistemaCultivo;
	}
	public void setSistemaCultivo(SistemaCultivo sistemaCultivo) {
		this.sistemaCultivo = sistemaCultivo;
	}
	public Long getIdPoliza() {
		return idPoliza;
	}
	public void setIdPoliza(Long idPoliza) {
		this.idPoliza = idPoliza;
	}

	public List<Long> getListParcelas() {
		return listParcelas;
	}

	public void setListParcelas(List<Long> listParcelas) {
		this.listParcelas = listParcelas;
	}

	public Long getIdAnexoMod() {
		return idAnexoMod;
	}

	public void setIdAnexoMod(Long idAnexoMod) {
		this.idAnexoMod = idAnexoMod;
	}

	public String getIdCupon() {
		return idCupon;
	}

	public void setIdCupon(String idCupon) {
		this.idCupon = idCupon;
	}

	public Termino getTermino_cm() {
		return termino_cm;
	}

	public void setTermino_cm(Termino termino_cm) {
		this.termino_cm = termino_cm;
	}

	public Character getSubtermino_cm() {
		return subtermino_cm;
	}

	public void setSubtermino_cm(Character subtermino_cm) {
		this.subtermino_cm = subtermino_cm;
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

	public BigDecimal getInc_unidades_cm() {
		return inc_unidades_cm;
	}

	public void setInc_unidades_cm(BigDecimal inc_unidades_cm) {
		this.inc_unidades_cm = inc_unidades_cm;
	}

	public BigDecimal getPrecio_cm() {
		return precio_cm;
	}

	public void setPrecio_cm(BigDecimal precio_cm) {
		this.precio_cm = precio_cm;
	}

	public String getCodtipomarcoplantac_cm() {
		return codtipomarcoplantac_cm;
	}

	public void setCodtipomarcoplantac_cm(String codtipomarcoplantac_cm) {
		this.codtipomarcoplantac_cm = codtipomarcoplantac_cm;
	}

	public BigDecimal getCodpracticacultural_cm() {
		return codpracticacultural_cm;
	}

	public void setCodpracticacultural_cm(BigDecimal codpracticacultural_cm) {
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

	public SistemaProduccion getSistemaProduccion() {
		return sistemaProduccion;
	}

	public void setSistemaProduccion(SistemaProduccion sistemaProduccion) {
		this.sistemaProduccion = sistemaProduccion;
	}

}
