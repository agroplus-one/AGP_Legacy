package com.rsi.agp.vo;

// Idem 
public class CampoPantallaConfigurableVO {
	
	private String nombre;
	private int idpantallaconfigurable;
	private int idseccion;
	private String etiqueta; // label del elemento
	private int tamanio; // tamaño, si lo necesitase
	private int idtipo; // id Combo, date, imputText, etc
	private String descripcion_tipo; // descripcion
	private int x; // coordenada x
	private int y; // coordenada y
	private int idorigendedatos;
	private int ubicacion_codigo;
	private String ubicacion_descripcion;
	private String tabla_asociada;
	private String multiple;
	private String descripcion;
	private int ancho;
	private int alto;
	private String mostrar;
	private String mostrarCarga;
	private String deshabilitado;
	private int idPantalla;
	private int codUso;
	private int codConcepto;
	private int lineaSeguroId;
	private int codTipoNaturaleza;
	private String desTipoNaturaleza;
	private int decimales;
	private String valorCargaPac;
	private ItemVO[] valores;

	public CampoPantallaConfigurableVO(String nombre, int idpantallaconfigurable, int idseccion, String etiqueta,
			int tamanio, int idtipo, String descripcion_tipo, int x, int y, int idorigendedatos, int ubicacion_codigo,
			String ubicacion_descripcion, String tabla_asociada, String multiple, String descripcion, int ancho, int alto,
			String mostrar, String mostrarCarga, String deshabilitado, int idPantalla, int codUso, int codConcepto,
			int lineaSeguroId, int codTipoNaturaleza, String desTipoNaturaleza, int decimales, String valorCargaPac, ItemVO[] valores) {

		this.nombre = nombre;
		this.idpantallaconfigurable = idpantallaconfigurable;
		this.idseccion = idseccion;
		this.etiqueta = etiqueta;
		this.tamanio = tamanio;
		this.idtipo = idtipo;
		this.descripcion_tipo = descripcion_tipo;
		this.x = x;
		this.y = y;
		this.idorigendedatos = idorigendedatos;
		this.ubicacion_codigo = ubicacion_codigo;
		this.ubicacion_descripcion = ubicacion_descripcion;
		this.tabla_asociada = tabla_asociada;
		this.multiple = multiple;
		this.descripcion = descripcion;
		this.ancho = ancho;
		this.alto = alto;
		this.mostrar = mostrar;
		this.mostrarCarga = mostrarCarga;
		this.deshabilitado = deshabilitado;
		this.idPantalla = idPantalla;
		this.codUso = codUso;
		this.codConcepto = codConcepto;
		this.lineaSeguroId = lineaSeguroId;
		this.codTipoNaturaleza = codTipoNaturaleza;
		this.desTipoNaturaleza = desTipoNaturaleza;
		this.decimales = decimales;
		this.valorCargaPac = valorCargaPac;
		this.valores = valores;
	}

	public CampoPantallaConfigurableVO() {
		this.nombre = new String();
		this.etiqueta = new String();
		this.descripcion_tipo = new String();
		this.ubicacion_descripcion = new String();
		this.tabla_asociada = new String();
		this.multiple = new String();
		this.descripcion = new String();
		this.mostrar = new String();
		this.mostrarCarga = new String();
		this.deshabilitado = new String();
		this.valorCargaPac = new String();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getIdpantallaconfigurable() {
		return idpantallaconfigurable;
	}

	public void setIdpantallaconfigurable(int idpantallaconfigurable) {
		this.idpantallaconfigurable = idpantallaconfigurable;
	}

	public int getIdseccion() {
		return idseccion;
	}

	public void setIdseccion(int idseccion) {
		this.idseccion = idseccion;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public int getTamanio() {
		return tamanio;
	}

	public void setTamanio(int tamanio) {
		this.tamanio = tamanio;
	}

	public int getIdtipo() {
		return idtipo;
	}

	public void setIdtipo(int idtipo) {
		this.idtipo = idtipo;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getIdorigendedatos() {
		return idorigendedatos;
	}

	public void setIdorigendedatos(int idorigendedatos) {
		this.idorigendedatos = idorigendedatos;
	}

	public int getUbicacion_codigo() {
		return ubicacion_codigo;
	}

	public void setUbicacion_codigo(int ubicacion_codigo) {
		this.ubicacion_codigo = ubicacion_codigo;
	}

	public String getUbicacion_descripcion() {
		return ubicacion_descripcion;
	}

	public void setUbicacion_descripcion(String ubicacion_descripcion) {
		this.ubicacion_descripcion = ubicacion_descripcion;
	}

	public String getTabla_asociada() {
		return tabla_asociada;
	}

	public void setTabla_asociada(String tabla_asociada) {
		this.tabla_asociada = tabla_asociada;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public String getDescripcion_tipo() {
		return descripcion_tipo;
	}

	public void setDescripcion_tipo(String descripcion_tipo) {
		this.descripcion_tipo = descripcion_tipo;
	}

	public String getMostrar() {
		return mostrar;
	}

	public void setMostrar(String mostrar) {
		this.mostrar = mostrar;
	}

	public String getMostrarCarga() {
		return mostrarCarga;
	}

	public void setMostrarCarga(String mostrarCarga) {
		this.mostrarCarga = mostrarCarga;
	}

	public String getDeshabilitado() {
		return deshabilitado;
	}

	public void setDeshabilitado(String deshabilitado) {
		this.deshabilitado = deshabilitado;
	}

	public int getIdPantalla() {
		return idPantalla;
	}

	public void setIdPantalla(int idPantalla) {
		this.idPantalla = idPantalla;
	}

	public int getCodUso() {
		return codUso;
	}

	public void setCodUso(int codUso) {
		this.codUso = codUso;
	}

	public int getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(int codConcepto) {
		this.codConcepto = codConcepto;
	}

	public int getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(int lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}

	public int getCodTipoNaturaleza() {
		return codTipoNaturaleza;
	}

	public void setCodTipoNaturaleza(int codTipoNaturaleza) {
		this.codTipoNaturaleza = codTipoNaturaleza;
	}

	public int getDecimales() {
		return decimales;
	}

	public void setDecimales(int decimales) {
		this.decimales = decimales;
	}

	public String getValorCargaPac() {
		return valorCargaPac;
	}

	public void setValorCargaPac(String valorCargaPac) {
		this.valorCargaPac = valorCargaPac;
	}
	
	public ItemVO[] getValores() {
		return valores;
	}

	public void setValores(ItemVO[] valores) {
		this.valores = valores;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getDesTipoNaturaleza() {
		return desTipoNaturaleza;
	}

	public void setDesTipoNaturaleza(String desTipoNaturaleza) {
		this.desTipoNaturaleza = desTipoNaturaleza;
	}	
}
