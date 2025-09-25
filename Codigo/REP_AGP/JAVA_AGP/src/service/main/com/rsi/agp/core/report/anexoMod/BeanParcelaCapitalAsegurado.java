package com.rsi.agp.core.report.anexoMod;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.Variedad;

import es.agroseguro.iTipos.IdentificacionCatastral;
import es.agroseguro.iTipos.SIGPAC;

public class BeanParcelaCapitalAsegurado implements Comparable<BeanParcelaCapitalAsegurado> {
	
	//Datos de la parcela
	private String nombre;
	private Integer hoja;
	private Integer numero;
	
	//Datos de la parcela: Identificacion Catastral
	private IdentificacionCatastral idCatastral;
	
	//Datos de la parcela: SIGPAC
	private SIGPAC idSigpac;
	
	private Termino ubicacion;
	private Variedad variedad;
	
	//Datos del capital asegurado
	private TipoCapital tipoCapital;
	private BigDecimal superficie;
	private BigDecimal precio;
	private Integer produccion;
	private Integer produccionComplementaria;
	private List<DatoVariable> datosVariables; //En el valor poner codigo y descripcion o valor segun el caso.
	
	public BeanParcelaCapitalAsegurado(){
		super();
		
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getHoja() {
		return hoja;
	}
	public void setHoja(Integer hoja) {
		this.hoja = hoja;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public IdentificacionCatastral getIdCatastral() {
		return idCatastral;
	}
	public void setIdCatastral(IdentificacionCatastral idCatastral) {
		this.idCatastral = idCatastral;
	}
	public SIGPAC getIdSigpac() {
		return idSigpac;
	}
	public void setIdSigpac(SIGPAC idSigpac) {
		this.idSigpac = idSigpac;
	}
	public Termino getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(Termino ubicacion) {
		this.ubicacion = ubicacion;
	}
	public Variedad getVariedad() {
		return variedad;
	}
	public void setVariedad(Variedad variedad) {
		this.variedad = variedad;
	}
	public TipoCapital getTipoCapital() {
		return tipoCapital;
	}
	public void setTipoCapital(TipoCapital tipoCapital) {
		this.tipoCapital = tipoCapital;
	}
	public BigDecimal getSuperficie() {
		return superficie;
	}
	public void setSuperficie(BigDecimal superficie) {
		this.superficie = superficie;
	}
	public BigDecimal getPrecio() {
		return precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
	public Integer getProduccion() {
		return produccion;
	}
	public void setProduccion(Integer produccion) {
		this.produccion = produccion;
	}
	public List<DatoVariable> getDatosVariables() {
		return datosVariables;
	}
	public void setDatosVariables(List<DatoVariable> datosVariables) {
		this.datosVariables = datosVariables;
	}

	@Override
	public int compareTo(BeanParcelaCapitalAsegurado o) {
		int compareUbicacion = this.getUbicacion().compareTo(o.getUbicacion());
		if (compareUbicacion == 0) {
			if (this.getHoja() < o.getHoja())
				return -1;
			else if (this.getHoja() > o.getHoja())
				return 1;
			else{
				if (this.getNumero() < o.getNumero())
					return -1;
				else if (this.getNumero() > o.getNumero())
					return 1;
			}
			return 0;
		} else {
			return compareUbicacion;
		}		
	}

	public Integer getProduccionComplementaria() {
		return produccionComplementaria;
	}

	public void setProduccionComplementaria(Integer produccionComplementaria) {
		this.produccionComplementaria = produccionComplementaria;
	}	
}