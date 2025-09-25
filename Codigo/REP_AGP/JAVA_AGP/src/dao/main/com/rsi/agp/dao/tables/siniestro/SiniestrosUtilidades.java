package com.rsi.agp.dao.tables.siniestro;

import java.math.BigDecimal;
import java.util.Date;

public class SiniestrosUtilidades implements java.io.Serializable {

	private static final long serialVersionUID = 5453495444283224703L;

	private BigDecimal id;
	private BigDecimal idpoliza;
	private BigDecimal codentidad;
	private String nombreEntidad;
	private String oficina;
	private String nombreOficina;
	private BigDecimal codplan;
	private BigDecimal codlinea;
	private String referencia;
	private BigDecimal dc;
	private String nifcif;
	private String nombre;
	private Date fenvpol;
	private String numsiniestro;
	private Date focurr;
	private Date ffirma;
	private BigDecimal idestado;
	private String descestado;
	private Date fenv;
	private String codriesgo;
	private String desriesgo;
	private String codgruposeguro;
	private String nomlinea;
	private String nomentidad;
	private String codusuario;
	private String delegacion;
	private BigDecimal entmediadora;
	private BigDecimal subentmediadora;
	private BigDecimal numerosiniestro;
	private Integer serie;
	/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Inicio */
	private Date fbaja;

	public SiniestrosUtilidades() {
		this.id = new BigDecimal(0);

	}

	public SiniestrosUtilidades(BigDecimal id) {
		this.id = id;
	}

	public String getNomlinea() {
		return nomlinea;
	}

	public void setNomlinea(String nomlinea) {
		this.nomlinea = nomlinea;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getIdpoliza() {
		return this.idpoliza;
	}

	public void setIdpoliza(BigDecimal idpoliza) {
		this.idpoliza = idpoliza;
	}

	public BigDecimal getCodentidad() {
		return this.codentidad;
	}

	public void setCodentidad(BigDecimal codentidad) {
		this.codentidad = codentidad;
	}

	public String getOficina() {
		return this.oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

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

	public String getReferencia() {
		return this.referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public BigDecimal getDc() {
		return this.dc;
	}

	public void setDc(BigDecimal dc) {
		this.dc = dc;
	}

	public String getNifcif() {
		return this.nifcif;
	}

	public void setNifcif(String nifcif) {
		this.nifcif = nifcif;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Date getFenvpol() {
		return this.fenvpol;
	}

	public void setFenvpol(Date fenvpol) {
		this.fenvpol = fenvpol;
	}

	public String getNumsiniestro() {
		return this.numsiniestro;
	}

	public void setNumsiniestro(String numsiniestro) {
		this.numsiniestro = numsiniestro;
	}

	public Date getFocurr() {
		return this.focurr;
	}

	public void setFocurr(Date focurr) {
		this.focurr = focurr;
	}

	public Date getFfirma() {
		return this.ffirma;
	}

	public void setFfirma(Date ffirma) {
		this.ffirma = ffirma;
	}

	public BigDecimal getIdestado() {
		return this.idestado;
	}

	public void setIdestado(BigDecimal idestado) {
		this.idestado = idestado;
	}

	public String getDescestado() {
		return this.descestado;
	}

	public void setDescestado(String descestado) {
		this.descestado = descestado;
	}

	public Date getFenv() {
		return this.fenv;
	}

	public void setFenv(Date fenv) {
		this.fenv = fenv;
	}

	public String getCodriesgo() {
		return this.codriesgo;
	}

	public void setCodriesgo(String codriesgo) {
		this.codriesgo = codriesgo;
	}

	public String getDesriesgo() {
		return this.desriesgo;
	}

	public void setDesriesgo(String desriesgo) {
		this.desriesgo = desriesgo;
	}

	/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Inicio */
	public Date getFbaja() {
		return this.fbaja;
	}

	public void setFbaja(Date fbaja) {
		this.fbaja = fbaja;
	}
	/* Pet. 63473 ** MODIF TAM (30/11/2021) ** Fin */

	public boolean equals(Object other) {
		if ((other == null))
			return false;
		if (this.getClass() != other.getClass())
		    return false;

		SiniestrosUtilidades castOther = (SiniestrosUtilidades) other;

		return ((this.getId() == castOther.getId())
				|| (this.getId() != null && castOther.getId() != null && this.getId().equals(castOther.getId())))
				&& ((this.getIdpoliza() == castOther.getIdpoliza()) || (this.getIdpoliza() != null
						&& castOther.getIdpoliza() != null && this.getIdpoliza().equals(castOther.getIdpoliza())))
				&& ((this.getCodentidad() == castOther.getCodentidad()) || (this.getCodentidad() != null
						&& castOther.getCodentidad() != null && this.getCodentidad().equals(castOther.getCodentidad())))
				&& ((this.getOficina() == castOther.getOficina()) || (this.getOficina() != null
						&& castOther.getOficina() != null && this.getOficina().equals(castOther.getOficina())))
				&& ((this.getCodplan() == castOther.getCodplan()) || (this.getCodplan() != null
						&& castOther.getCodplan() != null && this.getCodplan().equals(castOther.getCodplan())))
				&& ((this.getCodlinea() == castOther.getCodlinea()) || (this.getCodlinea() != null
						&& castOther.getCodlinea() != null && this.getCodlinea().equals(castOther.getCodlinea())))
				&& ((this.getReferencia() != null && castOther.getReferencia() != null
						&& this.getReferencia().equals(castOther.getReferencia())))
				&& ((this.getDc() == castOther.getDc()) || (this.getDc() != null && castOther.getDc() != null
						&& this.getDc().equals(castOther.getDc())))
				&& ((this.getNifcif() != null && castOther.getNifcif() != null
						&& this.getNifcif().equals(castOther.getNifcif())))
				&& ((this.getNombre() != null && castOther.getNombre() != null
						&& this.getNombre().equals(castOther.getNombre())))
				&& ((this.getFenvpol() == castOther.getFenvpol()) || (this.getFenvpol() != null
						&& castOther.getFenvpol() != null && this.getFenvpol().equals(castOther.getFenvpol())))
				&& ((this.getNumsiniestro() != null && castOther.getNumsiniestro() != null
						&& this.getNumsiniestro().equals(castOther.getNumsiniestro())))
				&& ((this.getFocurr() == castOther.getFocurr()) || (this.getFocurr() != null
						&& castOther.getFocurr() != null && this.getFocurr().equals(castOther.getFocurr())))
				&& ((this.getFfirma() == castOther.getFfirma()) || (this.getFfirma() != null
						&& castOther.getFfirma() != null && this.getFfirma().equals(castOther.getFfirma())))
				&& ((this.getIdestado() == castOther.getIdestado()) || (this.getIdestado() != null
						&& castOther.getIdestado() != null && this.getIdestado().equals(castOther.getIdestado())))
				&& ((this.getDescestado() != null && castOther.getDescestado() != null
						&& this.getDescestado().equals(castOther.getDescestado())))
				&& ((this.getFenv() == castOther.getFenv()) || (this.getFenv() != null && castOther.getFenv() != null
						&& this.getFenv().equals(castOther.getFenv())))
				&& ((this.getCodriesgo() != null && castOther.getCodriesgo() != null
						&& this.getCodriesgo().equals(castOther.getCodriesgo())))
				&& ((this.getDesriesgo() != null && castOther.getDesriesgo() != null
						&& this.getDesriesgo().equals(castOther.getDesriesgo())))
				&& ((this.getCodgruposeguro() != null && castOther.getCodgruposeguro() != null
						&& this.getCodgruposeguro().equals(castOther.getCodgruposeguro())))
				&& ((this.getDelegacion() != null && castOther.getDelegacion() != null
						&& this.getDelegacion().equals(castOther.getDelegacion())))
				&& ((this.getEntmediadora() != null && castOther.getEntmediadora() != null
						&& this.getEntmediadora().equals(castOther.getEntmediadora())))
				&& ((this.getSubentmediadora() != null && castOther.getSubentmediadora() != null
						&& this.getSubentmediadora().equals(castOther.getSubentmediadora())))
				&& ((this.getFbaja() != null && castOther.getFbaja() != null
						&& this.getFbaja().equals(castOther.getFbaja())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getId() == null ? 0 : this.getId().hashCode());
		result = 37 * result + (getIdpoliza() == null ? 0 : this.getIdpoliza().hashCode());
		result = 37 * result + (getCodentidad() == null ? 0 : this.getCodentidad().hashCode());
		result = 37 * result + (getOficina() == null ? 0 : this.getOficina().hashCode());
		result = 37 * result + (getCodplan() == null ? 0 : this.getCodplan().hashCode());
		result = 37 * result + (getCodlinea() == null ? 0 : this.getCodlinea().hashCode());
		result = 37 * result + (getReferencia() == null ? 0 : this.getReferencia().hashCode());
		result = 37 * result + (getDc() == null ? 0 : this.getDc().hashCode());
		result = 37 * result + (getNifcif() == null ? 0 : this.getNifcif().hashCode());
		result = 37 * result + (getNombre() == null ? 0 : this.getNombre().hashCode());
		result = 37 * result + (getFenvpol() == null ? 0 : this.getFenvpol().hashCode());
		result = 37 * result + (getNumsiniestro() == null ? 0 : this.getNumsiniestro().hashCode());
		result = 37 * result + (getFocurr() == null ? 0 : this.getFocurr().hashCode());
		result = 37 * result + (getFfirma() == null ? 0 : this.getFfirma().hashCode());
		result = 37 * result + (getIdestado() == null ? 0 : this.getIdestado().hashCode());
		result = 37 * result + (getDescestado() == null ? 0 : this.getDescestado().hashCode());
		result = 37 * result + (getFenv() == null ? 0 : this.getFenv().hashCode());
		result = 37 * result + (getCodriesgo() == null ? 0 : this.getCodriesgo().hashCode());
		result = 37 * result + (getDesriesgo() == null ? 0 : this.getDesriesgo().hashCode());
		result = 37 * result + (getCodgruposeguro() == null ? 0 : this.getCodgruposeguro().hashCode());
		result = 37 * result + (getDelegacion() == null ? 0 : this.getDelegacion().hashCode());
		result = 37 * result + (getEntmediadora() == null ? 0 : this.getEntmediadora().hashCode());
		result = 37 * result + (getSubentmediadora() == null ? 0 : this.getSubentmediadora().hashCode());
		result = 37 * result + (getFbaja() == null ? 0 : this.getFbaja().hashCode());
		return result;
	}

	public String getNomentidad() {
		return nomentidad;
	}

	public void setNomentidad(String nomentidad) {
		this.nomentidad = nomentidad;
	}

	public String getCodusuario() {
		return codusuario;
	}

	public void setCodusuario(String codusuario) {
		this.codusuario = codusuario;
	}

	public String getDelegacion() {
		return delegacion;
	}

	public void setDelegacion(String delegacion) {
		this.delegacion = delegacion;
	}

	public BigDecimal getEntmediadora() {
		return entmediadora;
	}

	public void setEntmediadora(BigDecimal entmediadora) {
		this.entmediadora = entmediadora;
	}

	public BigDecimal getSubentmediadora() {
		return subentmediadora;
	}

	public void setSubentmediadora(BigDecimal subentmediadora) {
		this.subentmediadora = subentmediadora;
	}

	public String getNombreEntidad() {
		return nombreEntidad;
	}

	public void setNombreEntidad(String nombreEntidad) {
		this.nombreEntidad = nombreEntidad;
	}

	public String getNombreOficina() {
		return nombreOficina;
	}

	public void setNombreOficina(String nombreOficina) {
		this.nombreOficina = nombreOficina;
	}

	public BigDecimal getNumerosiniestro() {
		return numerosiniestro;
	}

	public void setNumerosiniestro(BigDecimal numerosiniestro) {
		this.numerosiniestro = numerosiniestro;
	}

	public Integer getSerie() {
		return serie;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public String getCodgruposeguro() {
		return codgruposeguro;
	}

	public void setCodgruposeguro(String codgruposeguro) {
		this.codgruposeguro = codgruposeguro;
	}
}