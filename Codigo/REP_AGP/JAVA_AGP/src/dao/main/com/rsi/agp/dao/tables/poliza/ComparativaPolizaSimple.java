package com.rsi.agp.dao.tables.poliza;

public class ComparativaPolizaSimple implements java.io.Serializable {

	private long idpoliza;
	private long idanexo;
	private long lineaseguroid;
	private String codmodulo;
	private int filamodulo;
	private int cpm;
	private int rc;
	private int filacomparativa;
	private String descValor;
	private int concepto;
	private int valor;
	private Long numComparativa;
	
	public long getIdpoliza() {
		return idpoliza;
	}
	public void setIdpoliza(long idpoliza) {
		this.idpoliza = idpoliza;
	}
	public long getLineaseguroid() {
		return lineaseguroid;
	}
	public void setLineaseguroid(long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}
	public String getCodmodulo() {
		return codmodulo;
	}
	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}
	public int getFilamodulo() {
		return filamodulo;
	}
	public void setFilamodulo(int filamodulo) {
		this.filamodulo = filamodulo;
	}
	public int getCpm() {
		return cpm;
	}
	public void setCpm(int cpm) {
		this.cpm = cpm;
	}
	public int getRc() {
		return rc;
	}
	public void setRc(int rc) {
		this.rc = rc;
	}
	public int getFilacomparativa() {
		return filacomparativa;
	}
	public void setFilacomparativa(int filacomparativa) {
		this.filacomparativa = filacomparativa;
	}
	public String getDescValor() {
		return descValor;
	}
	public void setDescValor(String descValor) {
		this.descValor = descValor;
	}
	public int getConcepto() {
		return concepto;
	}
	public void setConcepto(int concepto) {
		this.concepto = concepto;
	}
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
	public long getIdanexo() {
		return idanexo;
	}
	public void setIdanexo(long idanexo) {
		this.idanexo = idanexo;
	}
	public Long getNumComparativa() {
		return numComparativa;
	}
	public void setNumComparativa(Long numComparativa) {
		this.numComparativa = numComparativa;
	}
	
	
}
