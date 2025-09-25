package com.rsi.agp.vo;

public class ItemSubvencionVO {

	private String descripcion;
	private Long codigo;

	public ItemSubvencionVO() {
		super();
	}

	public ItemSubvencionVO(String descripcion, Long codigo) {
		super();
		this.descripcion = descripcion;
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
}
