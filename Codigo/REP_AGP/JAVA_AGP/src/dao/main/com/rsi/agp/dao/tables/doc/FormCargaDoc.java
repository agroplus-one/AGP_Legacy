package com.rsi.agp.dao.tables.doc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.dao.tables.poliza.Linea;

public class FormCargaDoc implements Serializable {

	private static final long serialVersionUID = -1898085272119781752L;

	private MultipartFile file = null;
	private DocAgroseguroTipo docAgroseguroTipo;
	private BigDecimal codplan;
	private BigDecimal codlinea;
	private String descripcion;
	private BigDecimal codentidad;
	private Date fechavalidez;
	private String codusuario;
	private Date fecha;
	private List<String> listaPerfiles = new ArrayList<String>();
	
	private String nomlinea;
	private String nombreEntidad;	

	
	public FormCargaDoc() {
		this.docAgroseguroTipo = new DocAgroseguroTipo();
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public DocAgroseguroTipo getDocAgroseguroTipo() {
		return docAgroseguroTipo;
	}

	public void setDocAgroseguroTipo(DocAgroseguroTipo docAgroseguroTipo) {
		this.docAgroseguroTipo = docAgroseguroTipo;
	}

	/* Pet. 79014 ** MODIF TAM (25/03/2022) ** Inicio */
	public BigDecimal getCodplan() {
		return this.codplan;
	}
	public void setCodplan(BigDecimal codplan) {
		this.codplan = codplan;
	}

	
	public BigDecimal getCodlinea() {
		return this.codlinea;
	}

	public void setCodlinea(BigDecimal codlinea) {
		this.codlinea = codlinea;
	}
	
	public BigDecimal getCodentidad() {
		return this.codentidad;
	}

	public void setCodentidad(BigDecimal codentidad) {
		this.codentidad = codentidad;
	}
	
	public Date getFechavalidez() {
		return this.fechavalidez;
	}

	public void setFechavalidez(Date fechavalidez) {
		this.fechavalidez = fechavalidez;
	}

	public String getCodusuario() {
		return this.codusuario;
	}

	public void setCodusuario(String codusuario) {
		this.codusuario = codusuario;
	}
	
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public List<String> getListaPerfiles() {
		return listaPerfiles;
	}

	public void setListaPerfiles(List<String> listaPerfiles) {
		this.listaPerfiles = listaPerfiles;
	}
	
	public String getNomlinea() {
		return nomlinea;
	}

	public void setNomlinea(String nomlinea) {
		this.nomlinea = nomlinea;
	}
	
	public String getNombreEntidad() {
		return nombreEntidad;
	}

	public void setNombreEntidad(String nombreEntidad) {
		this.nombreEntidad = nombreEntidad;
	}
	/* Pet. 79014 ** MODIF TAM (25/03/2022) ** Inicio */


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}