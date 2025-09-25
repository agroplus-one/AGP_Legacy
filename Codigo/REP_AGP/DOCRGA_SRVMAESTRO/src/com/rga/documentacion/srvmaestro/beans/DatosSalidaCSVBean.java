package com.rga.documentacion.srvmaestro.beans;

import java.util.List;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class DatosSalidaCSVBean {

	private String destino;

	private String impresion;

	private String codigoPostal;

	private String welcomePack;

	private String welcomePackProducto;

	private String tarjetas;
	
	private List<Object> ListaDatosTarjetasAsegurado;
	
	// P0026771: Unificación PDFs Sepa
	private String operacion;
	
	/*M0002137 - Nuevos datos para PersonaCartaCertificada*/
	private String idExternoTomador;
	private String nombreTomador;
	private String direccionTomador;
	private String codigoPostalTomador;
	private String provinciaTomador;
	private String poblacionTomador;
}
