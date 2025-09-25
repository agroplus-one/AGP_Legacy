package com.rsi.agp.vo;

import com.rsi.agp.core.webapp.util.StringUtils;

public class ItemVistaVO {

	private int codigo;
	private String descripcion;
	private String fecha;
	private String fecha1;
	private String fecha2;

	public ItemVistaVO() {
	}

	public ItemVistaVO(int codigo, String descripcion, String fecha, String fechaIniCont, String fechaFinCont) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.fecha1 = fechaIniCont;
		this.fecha2 = fechaFinCont;
	}

	public String getFecha1() {
		return fecha1;
	}

	public void setFecha1(String fechaIniCont) {
		this.fecha1 = fechaIniCont;
	}

	public String getFecha2() {
		return fecha2;
	}

	public void setFecha2(String fechaFinCont) {
		this.fecha2 = fechaFinCont;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	@Override
	public boolean equals(Object obj) {
		if ((this == obj))
			return true;
		if ((obj == null))
			return false;
		if (!(obj instanceof ItemVistaVO))
			return false;

		boolean iguales = false;
		ItemVistaVO elemento = (ItemVistaVO) obj;
		if (this.codigo == elemento.getCodigo()
				&& StringUtils.nullToString(this.descripcion)
						.equals(StringUtils.nullToString(elemento.getDescripcion()))
				&& StringUtils.nullToString(this.fecha).equals(StringUtils.nullToString(elemento.getFecha()))
				&& StringUtils.nullToString(this.fecha1).equals(StringUtils.nullToString(elemento.getFecha1()))
				&& StringUtils.nullToString(this.fecha2).equals(StringUtils.nullToString(elemento.getFecha2())))
			iguales = true;
		return iguales;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}