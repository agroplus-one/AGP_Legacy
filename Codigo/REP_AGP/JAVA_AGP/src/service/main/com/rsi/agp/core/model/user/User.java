package com.rsi.agp.core.model.user;


public class User {

	private static final Object ADMINISTRATOR = "AGP-1";
	private static final Object RSI = "AGP-0";
	
	private String idUsuario;
	private String idEntidad;
	private String entidad;
	private String nombre;
	private String apellidos;
	private String perfil;
	private String idOficina;
	private String usuarioEncriptado;
	private String email;
	private String telefono;
	private String oficina;
	private String terminal;
	private String ipUsuario;
	/* Pet. 63701 ** MODIF TAM (17.06.2021) */
	private String codzona; 

	public User() {
	}
	
	public User(String idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(String idEntidad) {
		this.idEntidad = idEntidad;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getIdOficina() {
		return idOficina;
	}

	public void setIdOficina(String idOficina) {
		this.idOficina = idOficina;
	}

	public boolean isRSI() {
		if(this.getPerfil().equals(User.RSI))
			return true;
		else
			return false;
	}

	public boolean isAdmin() {
		if(this.getPerfil().equals(User.ADMINISTRATOR))
			return true;
		else
			return false;
	}

	public String getUsuarioEncriptado() {
		return usuarioEncriptado;
	}

	public void setUsuarioEncriptado(String usuarioEncriptado) {
		this.usuarioEncriptado = usuarioEncriptado;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getIpUsuario() {
	    return this.ipUsuario;
	}

	public void setIpUsuario(String ipUsuario) {
	    this.ipUsuario = ipUsuario;
	}
	
	/* Pet. 63701 ** MODIF TAM (17.06.2021) */
	public String getCodzona() {
	    return this.codzona;
	}

	public void setCodzona(String codzona) {
	    this.codzona = codzona;
	}
	/* Pet. 63701 ** MODIF TAM (17.06.2021) Fin */
}
