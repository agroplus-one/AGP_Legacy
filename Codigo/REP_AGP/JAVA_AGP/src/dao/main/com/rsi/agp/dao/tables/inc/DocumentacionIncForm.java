package com.rsi.agp.dao.tables.inc;

import org.springframework.web.multipart.MultipartFile;

public class DocumentacionIncForm {

	private Long idDocumentacionInc;
	private MultipartFile file = null;
	private Incidencias incidencias;
	private String nombre;
	
	public DocumentacionIncForm() {
		super();
		this.incidencias = new Incidencias();
	}

	public Long getIdDocumentacionInc() {
		return idDocumentacionInc;
	}

	public void setIdDocumentacionInc(Long idDocumentacionInc) {
		this.idDocumentacionInc = idDocumentacionInc;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Incidencias getIncidencias() {
		return incidencias;
	}

	public void setIncidencias(Incidencias incidencias) {
		this.incidencias = incidencias;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
