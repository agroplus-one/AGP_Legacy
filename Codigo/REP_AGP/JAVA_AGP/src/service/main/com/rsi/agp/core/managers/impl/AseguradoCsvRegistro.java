package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.rsi.agp.core.util.Validaciones;

public class AseguradoCsvRegistro {

	//Indices de los campos
	private final int C_ENTIDAD=0;
	private final int C_TIPOIDENTIFICACION=1;
	private final int C_IDENTIFICACION=2; 
	private final int C_NOMBRE=3;
	private final int C_APELLIDO1=4;
	private final int C_APELLIDO2=5;
	private final int C_RAZONSOCIAL=6;
	private final int C_USUARIO=7;
	private final int C_TIPOVIA=8;
	private final int C_DOMICILIO=9;
	private final int C_NUMERO=10;
	private final int C_PISO=11;
	private final int C_BLOQUE=12;
	private final int C_ESCALERA=13;
	private final int C_CODPROVINCIA=14;
	private final int C_CODLOCALIDAD=15;
	private final int C_CODSUBLOCALIDAD=16;
	private final int C_CODPOSTAL=17;
	private final int C_TELEFONOFIJO=18;
	private final int C_TELEFONOMOVIL=19;
	private final int C_EMAIL=20;
	private final int C_NUMSEGSOCIAL=21;
	private final int C_REGIMENSEGSOCIAL=22;
	private final int C_ATP=23;
	private final int C_JOVENAGRICULTOR=24;
	private final int C_ibanPrima1=25;
	private final int C_ibanPrima2=26;
	private final int C_ibanPrima3=27;
	private final int C_ibanPrima4=28;
	private final int C_ibanPrima5=29;
	private final int C_ibanPrima6=30;
	private final int C_ibanSiniestro1=31;
	private final int C_ibanSiniestro2=32;
	private final int C_ibanSiniestro3=33;
	private final int C_ibanSiniestro4=34;
	private final int C_ibanSiniestro5=35;
	private final int C_ibanSiniestro6=36;
	private final int C_DEST_DOMICILACION=37;
	private final int C_TIT_CUENTA=38;
	//Nombre de los campos -----------------------------------------------
	
	// *****************************************************************
	
	//Campos
	private String entidad;
	private String tipoIdentificacion;
	private String identificacion; 
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String razonSocial;
	private String usuario;
	private String tipoVia;
	private String domicilio;
	private String numero;
	private String piso;
	private String bloque;
	private String escalera;
	private String codProvincia;
	private String codLocalidad;
	private String cosSublocalidad;
	private String codPostal;
	private String telefonoFijo;
	private String telefonoMovil;
	private String email;
	private String numSeguridadSoc;
	private String regSeguridadSoc;
	private String atp;
	private String jovenAgricultor;
	private String ibanPrima1;
	private String ibanPrima2;
	private String ibanPrima3;
	private String ibanPrima4;
	private String ibanPrima5;
	private String ibanPrima6;
	private String ibanSiniestro1;
	private String ibanSiniestro2;
	private String ibanSiniestro3;
	private String ibanSiniestro4;
	private String ibanSiniestro5;
	private String ibanSiniestro6;
	private String destDomiciliacion;
	private String titCuenta;
	
	// Otros campos que no vienen en el fichero
	
	private BigDecimal entidadMediadora;
	private BigDecimal subEntidadMediadora;
	private BigDecimal codEntidad;
	private BigDecimal codOficina;
	
	private String discriminante;
	private Character revisado;
	
	// **************************************************************
	
	//Otras variables --------------------------------------------------
	private Boolean esRegistroValido;
	
	// **************************************************************
	public AseguradoCsvRegistro(CsvReader registro) throws Exception{
		try {
			this.entidad = 				registro.get(C_ENTIDAD);
			this.tipoIdentificacion = 	registro.get(C_TIPOIDENTIFICACION).toUpperCase();
			this.identificacion =		registro.get(C_IDENTIFICACION); 
			this.nombre =				registro.get(C_NOMBRE).toUpperCase();
			this.apellido1 =			registro.get(C_APELLIDO1).toUpperCase();
			this.apellido2 =			registro.get(C_APELLIDO2).toUpperCase();
			this.razonSocial =			registro.get(C_RAZONSOCIAL).toUpperCase();
			this.usuario =				registro.get(C_USUARIO).toUpperCase();
			this.tipoVia =				registro.get(C_TIPOVIA).toUpperCase();
			this.domicilio =			registro.get(C_DOMICILIO).toUpperCase();
			this.numero =				registro.get(C_NUMERO);
			this.piso =					registro.get(C_PISO);
			this.bloque =				registro.get(C_BLOQUE).toUpperCase();
			this.escalera =				registro.get(C_ESCALERA).toUpperCase();
			this.codProvincia =			registro.get(C_CODPROVINCIA);
			this.codLocalidad =			registro.get(C_CODLOCALIDAD);
			this.cosSublocalidad =		registro.get(C_CODSUBLOCALIDAD);
			this.codPostal =			registro.get(C_CODPOSTAL);
			this.telefonoFijo =			registro.get(C_TELEFONOFIJO);
			this.telefonoMovil =		registro.get(C_TELEFONOMOVIL);
			this.email =				registro.get(C_EMAIL);
			this.numSeguridadSoc =		registro.get(C_NUMSEGSOCIAL);
			this.regSeguridadSoc =		registro.get(C_REGIMENSEGSOCIAL);
			this.atp =					registro.get(C_ATP).toUpperCase();
			this.jovenAgricultor =		registro.get(C_JOVENAGRICULTOR).toUpperCase();
			this.ibanPrima1 =			registro.get(C_ibanPrima1).toUpperCase();
			this.ibanPrima2 =			registro.get(C_ibanPrima2).toUpperCase();
			this.ibanPrima3 =			registro.get(C_ibanPrima3).toUpperCase();
			this.ibanPrima4 =			registro.get(C_ibanPrima4).toUpperCase();
			this.ibanPrima5 =			registro.get(C_ibanPrima5).toUpperCase();
			this.ibanPrima6 =			registro.get(C_ibanPrima6).toUpperCase();
			this.ibanSiniestro1 =		registro.get(C_ibanSiniestro1).toUpperCase();
			this.ibanSiniestro2 =		registro.get(C_ibanSiniestro2).toUpperCase();
			this.ibanSiniestro3 =		registro.get(C_ibanSiniestro3).toUpperCase();
			this.ibanSiniestro4 =		registro.get(C_ibanSiniestro4).toUpperCase();
			this.ibanSiniestro5 =		registro.get(C_ibanSiniestro5).toUpperCase();
			this.ibanSiniestro6 =		registro.get(C_ibanSiniestro6).toUpperCase();
			this.destDomiciliacion =	registro.get(C_DEST_DOMICILACION).toUpperCase();
			this.titCuenta =			registro.get(C_TIT_CUENTA).toUpperCase();
			
		} catch (Exception e) {
			throw new Exception(e);
		}
		
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTipoVia() {
		return tipoVia;
	}

	public void setTipoVia(String tipoVia) {
		this.tipoVia = tipoVia;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getBloque() {
		return bloque;
	}

	public void setBloque(String bloque) {
		this.bloque = bloque;
	}

	public String getEscalera() {
		return escalera;
	}

	public void setEscalera(String escalera) {
		this.escalera = escalera;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getCodLocalidad() {
		return codLocalidad;
	}

	public void setCodLocalidad(String codLocalidad) {
		this.codLocalidad = codLocalidad;
	}

	public String getCosSublocalidad() {
		return cosSublocalidad;
	}

	public void setCosSublocalidad(String cosSublocalidad) {
		this.cosSublocalidad = cosSublocalidad;
	}

	public String getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public String getTelefonoMovil() {
		return telefonoMovil;
	}

	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumSeguridadSoc() {
		return numSeguridadSoc;
	}

	public void setNumSeguridadSoc(String numSeguridadSoc) {
		this.numSeguridadSoc = numSeguridadSoc;
	}

	public String getRegSeguridadSoc() {
		return regSeguridadSoc;
	}

	public void setRegSeguridadSoc(String regSeguridadSoc) {
		this.regSeguridadSoc = regSeguridadSoc;
	}

	public String getAtp() {
		return atp;
	}

	public void setAtp(String atp) {
		this.atp = atp;
	}

	public String getJovenAgricultor() {
		return jovenAgricultor;
	}

	public void setJovenAgricultor(String jovenAgricultor) {
		this.jovenAgricultor = jovenAgricultor;
	}

	public String getIbanPrima1() {
		return ibanPrima1;
	}

	public void setIbanPrima1(String ibanPrima1) {
		this.ibanPrima1 = ibanPrima1;
	}

	public String getIbanPrima2() {
		return ibanPrima2;
	}

	public void setIbanPrima2(String ibanPrima2) {
		this.ibanPrima2 = ibanPrima2;
	}

	public String getIbanPrima3() {
		return ibanPrima3;
	}

	public void setIbanPrima3(String ibanPrima3) {
		this.ibanPrima3 = ibanPrima3;
	}

	public String getIbanPrima4() {
		return ibanPrima4;
	}

	public void setIbanPrima4(String ibanPrima4) {
		this.ibanPrima4 = ibanPrima4;
	}

	public String getIbanPrima5() {
		return ibanPrima5;
	}

	public void setIbanPrima5(String ibanPrima5) {
		this.ibanPrima5 = ibanPrima5;
	}

	public String getIbanPrima6() {
		return ibanPrima6;
	}

	public void setIbanPrima6(String ibanPrima6) {
		this.ibanPrima6 = ibanPrima6;
	}

	public String getIbanSiniestro1() {
		return ibanSiniestro1;
	}

	public void setIbanSiniestro1(String ibanSiniestro1) {
		this.ibanSiniestro1 = ibanSiniestro1;
	}

	public String getIbanSiniestro2() {
		return ibanSiniestro2;
	}

	public void setIbanSiniestro2(String ibanSiniestro2) {
		this.ibanSiniestro2 = ibanSiniestro2;
	}

	public String getIbanSiniestro3() {
		return ibanSiniestro3;
	}

	public void setIbanSiniestro3(String ibanSiniestro3) {
		this.ibanSiniestro3 = ibanSiniestro3;
	}

	public String getIbanSiniestro4() {
		return ibanSiniestro4;
	}

	public void setIbanSiniestro4(String ibanSiniestro4) {
		this.ibanSiniestro4 = ibanSiniestro4;
	}

	public String getIbanSiniestro5() {
		return ibanSiniestro5;
	}

	public void setIbanSiniestro5(String ibanSiniestro5) {
		this.ibanSiniestro5 = ibanSiniestro5;
	}

	public String getIbanSiniestro6() {
		return ibanSiniestro6;
	}

	public void setIbanSiniestro6(String ibanSiniestro6) {
		this.ibanSiniestro6 = ibanSiniestro6;
	}

	public String getDestDomiciliacion() {
		return destDomiciliacion;
	}

	public void setDestDomiciliacion(String destDomiciliacion) {
		this.destDomiciliacion = destDomiciliacion;
	}

	public String getTitCuenta() {
		return titCuenta;
	}

	public void setTitCuenta(String titCuenta) {
		this.titCuenta = titCuenta;
	}

	public Boolean getEsRegistroValido() {
		return esRegistroValido;
	}

	public void setEsRegistroValido(Boolean esRegistroValido) {
		this.esRegistroValido = esRegistroValido;
	}
	
	public ArrayList<String> isValidCampo(String valorCampo, String nombreCampo, boolean obligatorio, boolean numerico, int longitud, int longitudMax, String valoresFijos){
		ArrayList<String> mensajes = new ArrayList<String>();
		
		if(obligatorio){
			
			if(!Validaciones.isValidObligatorio(valorCampo)){
				mensajes.add("El campo " + nombreCampo + " es obligatorio y no contiene valor.");
				//msgValid.put(this.getIdentificacion(),"El campo " + nombreCampo + " es obligatorio y no contiene valor.");
			}
		}			
		
		if( numerico && !valorCampo.isEmpty()){
			
			if(!Validaciones.isNumeric(valorCampo)){
				mensajes.add("El campo " + nombreCampo + " contiene caracteres no num&eacutericos");
				//msgValid.put(this.getIdentificacion(),"El campo " + nombreCampo + " contiene caracteres no numéricos");
			}			
		}
		
		if(longitud>0 && !valorCampo.isEmpty()){
			
			if(!Validaciones.isValidLongitud(valorCampo, longitud)){
				mensajes.add("El campo " + nombreCampo + " no tiene la longitud correcta.");
				//msgValid.put(this.getIdentificacion(),"El campo " + nombreCampo + " no tiene la longitud correcta.");
			}		
		}
			
		
		if( longitudMax>0 && !valorCampo.isEmpty()){
			
			if(!Validaciones.isValidLongitudMax(valorCampo, nombreCampo, longitudMax)){
				mensajes.add("El campo " + nombreCampo + " no tiene la longitud correcta.");
				//msgValid.put(this.getIdentificacion(),"El campo " + nombreCampo + " no tiene la longitud correcta.");
			}	
		}
			
		
		if(null!=valoresFijos && !valorCampo.isEmpty()){
			
			if(!Validaciones.isValidValorFijo(valorCampo, nombreCampo, valoresFijos)){
				mensajes.add("El campo " + nombreCampo + " no tiene un valor esperado.");
				//msgValid.put(this.getIdentificacion(),"El campo " + nombreCampo + " no tiene un valor esperado.");
			}	
		}
			
		this.esRegistroValido=(mensajes.size()>0);
		return mensajes;
	}

	public BigDecimal getEntidadMediadora() {
		return entidadMediadora;
	}

	public void setEntidadMediadora(BigDecimal entidadMediadora) {
		this.entidadMediadora = entidadMediadora;
	}

	public BigDecimal getSubEntidadMediadora() {
		return subEntidadMediadora;
	}

	public void setSubEntidadMediadora(BigDecimal subEntidadMediadora) {
		this.subEntidadMediadora = subEntidadMediadora;
	}

	public String getDiscriminante() {
		return discriminante;
	}

	public void setDiscriminante(String discriminante) {
		this.discriminante = discriminante;
	}

	public Character getRevisado() {
		return revisado;
	}

	public void setRevisado(Character revisado) {
		this.revisado = revisado;
	}

	public BigDecimal getCodEntidad() {
		return codEntidad;
	}

	public void setCodEntidad(BigDecimal codEntidad) {
		this.codEntidad = codEntidad;
	}

	public BigDecimal getCodOficina() {
		return codOficina;
	}

	public void setCodOficina(BigDecimal codOficina) {
		this.codOficina = codOficina;
	}

	



		
}
