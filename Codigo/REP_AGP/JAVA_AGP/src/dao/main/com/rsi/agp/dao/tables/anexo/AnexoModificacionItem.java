package com.rsi.agp.dao.tables.anexo;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class AnexoModificacionItem implements java.io.Serializable {

	private static final long serialVersionUID = -4762329528201569117L;

	private Long id;
	private Estado estado;
	private EstadosInc estadoAgroseguro;
	private Comunicaciones comunicaciones;
	private Poliza poliza;
	private Long idcopy;
	private Date fechafirmadoc;
	private String asunto;
	private String nomaseg;
	private String apel1aseg;
	private String apel2aseg;
	private String razsocaseg;
	private String calleaseg;
	private String numaseg;
	private BigDecimal codprovincia;
	private BigDecimal codlocalidad;
	private String codposaseg;
	private String pisoaseg;
	private String bloqueaseg;
	private String escaseg;
	private String telffijoaseg;
	private String telfmovilaseg;
	private String email;
	private String numsegsocial;
	private String regimensegsocial;
	private String codmodulo;
	private transient Clob xml;
	private String nomlocalidad;
	private String tipoEnvio;
	private Cupon cupon;
	private Date fechaSeguimiento;
	private Set<Parcela> parcelas = new HashSet<Parcela>(0);
	private Set<SubvDeclarada> subvDeclaradas = new HashSet<SubvDeclarada>(0);
	private Set<Cobertura> coberturas = new HashSet<Cobertura>(0);
	private Date fechaEnvioAnexo;

	public AnexoModificacionItem() {
		estado = new Estado();
		poliza = new Poliza();
		cupon = new Cupon();
		comunicaciones = new Comunicaciones();
		estadoAgroseguro = new EstadosInc();
	}

	public AnexoModificacionItem(AnexoModificacion am) {
		this.id = am.getId();
		this.estado = am.getEstado();
		if (am.getEstadoAgroseguro() == null) {
			this.estadoAgroseguro = new EstadosInc();
		} else {
			this.estadoAgroseguro = am.getEstadoAgroseguro();
		}
		if (am.getComunicaciones() == null) {
			this.comunicaciones = new Comunicaciones();
		} else {
			this.comunicaciones = am.getComunicaciones();
		}
		this.poliza = am.getPoliza();
		this.idcopy = am.getIdcopy();
		this.fechafirmadoc = am.getFechafirmadoc();
		this.asunto = am.getAsunto();
		this.nomaseg = am.getNomaseg();
		this.apel1aseg = am.getApel1aseg();
		this.apel2aseg = am.getApel2aseg();
		this.razsocaseg = am.getRazsocaseg();
		this.calleaseg = am.getCalleaseg();
		this.numaseg = am.getNumaseg();
		this.codprovincia = am.getCodprovincia();
		this.codlocalidad = am.getCodlocalidad();
		this.codposaseg = am.getCodposaseg();
		this.pisoaseg = am.getPisoaseg();
		this.bloqueaseg = am.getBloqueaseg();
		this.escaseg = am.getEscaseg();
		this.telffijoaseg = am.getTelffijoaseg();
		this.telfmovilaseg = am.getTelfmovilaseg();
		this.email = am.getEmail();
		this.numsegsocial = am.getNumsegsocial();
		this.regimensegsocial = am.getRegimensegsocial();
		this.codmodulo = am.getCodmodulo();
		this.xml = am.getXml();
		this.nomlocalidad = am.getNomlocalidad();
		this.parcelas = am.getParcelas();
		this.subvDeclaradas = am.getSubvDeclaradas();
		this.coberturas = am.getCoberturas();
		this.tipoEnvio = am.getTipoEnvio();
		this.cupon = am.getCupon();
		this.fechaSeguimiento = am.getFechaSeguimiento();
		this.fechaEnvioAnexo = am.getFechaEnvioAnexo();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Estado getEstado() {
		return this.estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Comunicaciones getComunicaciones() {
		return this.comunicaciones;
	}

	public void setComunicaciones(Comunicaciones comunicaciones) {
		this.comunicaciones = comunicaciones;
	}

	public Poliza getPoliza() {
		return this.poliza;
	}

	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}

	public Long getIdcopy() {
		return this.idcopy;
	}

	public void setIdcopy(Long idcopy) {
		this.idcopy = idcopy;
	}

	public Date getFechafirmadoc() {
		return this.fechafirmadoc;
	}

	public void setFechafirmadoc(Date fechafirmadoc) {
		this.fechafirmadoc = fechafirmadoc;
	}

	public String getAsunto() {
		return this.asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getNomaseg() {
		return this.nomaseg;
	}

	public void setNomaseg(String nomaseg) {
		this.nomaseg = nomaseg;
	}

	public String getApel1aseg() {
		return this.apel1aseg;
	}

	public void setApel1aseg(String apel1aseg) {
		this.apel1aseg = apel1aseg;
	}

	public String getApel2aseg() {
		return this.apel2aseg;
	}

	public void setApel2aseg(String apel2aseg) {
		this.apel2aseg = apel2aseg;
	}

	public String getRazsocaseg() {
		return this.razsocaseg;
	}

	public void setRazsocaseg(String razsocaseg) {
		this.razsocaseg = razsocaseg;
	}

	public String getCalleaseg() {
		return this.calleaseg;
	}

	public void setCalleaseg(String calleaseg) {
		this.calleaseg = calleaseg;
	}

	public String getNumaseg() {
		return this.numaseg;
	}

	public void setNumaseg(String numaseg) {
		this.numaseg = numaseg;
	}

	public BigDecimal getCodprovincia() {
		return this.codprovincia;
	}

	public void setCodprovincia(BigDecimal codprovincia) {
		this.codprovincia = codprovincia;
	}

	public BigDecimal getCodlocalidad() {
		return this.codlocalidad;
	}

	public void setCodlocalidad(BigDecimal codlocalidad) {
		this.codlocalidad = codlocalidad;
	}

	public String getCodposaseg() {
		return this.codposaseg;
	}

	public void setCodposaseg(String codposaseg) {
		this.codposaseg = codposaseg;
	}

	public String getCodposasegstr() {
		if (codposaseg == null)
			return "";
		StringBuilder codPostalStr = new StringBuilder(codposaseg);
		while (codPostalStr.length() < 5) {
			codPostalStr.insert(0, "0");
		}
		return codPostalStr.toString();
	}

	public void setCodposasegstr(String codpostalstr) {
		if (codpostalstr != null && !codpostalstr.isEmpty())
			this.codposaseg = codpostalstr;
	}

	public String getPisoaseg() {
		return this.pisoaseg;
	}

	public void setPisoaseg(String pisoaseg) {
		this.pisoaseg = pisoaseg;
	}

	public String getBloqueaseg() {
		return this.bloqueaseg;
	}

	public void setBloqueaseg(String bloqueaseg) {
		this.bloqueaseg = bloqueaseg;
	}

	public String getEscaseg() {
		return this.escaseg;
	}

	public void setEscaseg(String escaseg) {
		this.escaseg = escaseg;
	}

	public String getTelffijoaseg() {
		return this.telffijoaseg;
	}

	public void setTelffijoaseg(String telffijoaseg) {
		this.telffijoaseg = telffijoaseg;
	}

	public String getTelfmovilaseg() {
		return this.telfmovilaseg;
	}

	public void setTelfmovilaseg(String telfmovilaseg) {
		this.telfmovilaseg = telfmovilaseg;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumsegsocial() {
		return this.numsegsocial;
	}

	public void setNumsegsocial(String numsegsocial) {
		this.numsegsocial = numsegsocial;
	}

	public String getRegimensegsocial() {
		return this.regimensegsocial;
	}

	public void setRegimensegsocial(String regimensegsocial) {
		this.regimensegsocial = regimensegsocial;
	}

	public String getCodmodulo() {
		return this.codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}

	public Clob getXml() {
		return this.xml;
	}

	public void setXml(Clob xml) {
		this.xml = xml;
	}

	public String getNomlocalidad() {
		return this.nomlocalidad;
	}

	public void setNomlocalidad(String nomlocalidad) {
		this.nomlocalidad = nomlocalidad;
	}

	public Set<Parcela> getParcelas() {
		return this.parcelas;
	}

	public void setParcelas(Set<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public Set<SubvDeclarada> getSubvDeclaradas() {
		return this.subvDeclaradas;
	}

	public void setSubvDeclaradas(Set<SubvDeclarada> subvDeclaradas) {
		this.subvDeclaradas = subvDeclaradas;
	}

	public Set<Cobertura> getCoberturas() {
		return this.coberturas;
	}

	public void setCoberturas(Set<Cobertura> coberturas) {
		this.coberturas = coberturas;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public Cupon getCupon() {
		return cupon;
	}

	public void setCupon(Cupon cupon) {
		this.cupon = cupon;
	}

	public EstadosInc getEstadoAgroseguro() {
		return estadoAgroseguro;
	}

	public void setEstadoAgroseguro(EstadosInc estadoAgroseguro) {
		this.estadoAgroseguro = estadoAgroseguro;
	}

	public Date getFechaSeguimiento() {
		return fechaSeguimiento;
	}

	public void setFechaSeguimiento(Date fechaSeguimiento) {
		this.fechaSeguimiento = fechaSeguimiento;
	}

	public Date getFechaEnvioAnexo() {
		return fechaEnvioAnexo;
	}

	public void setFechaEnvioAnexo(Date fechaEnvioAnexo) {
		this.fechaEnvioAnexo = fechaEnvioAnexo;
	}
}
