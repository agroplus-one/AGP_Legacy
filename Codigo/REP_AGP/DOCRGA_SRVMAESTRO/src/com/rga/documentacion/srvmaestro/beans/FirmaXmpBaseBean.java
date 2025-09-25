package com.rga.documentacion.srvmaestro.beans;

import lombok.Data;

@Data
@SuppressWarnings("unused")
/**
 * Objeto donde se almacenan los metadatos de la firma
 * @author U021059
 *
 */
public class FirmaXmpBaseBean {

	private Integer posicionX;
	
	private Integer posicionY;
	
	private Float posicionYV2;
	
	private Integer pagina;
	
	private Integer alto;
	
	private Integer ancho;
	
	private String nombre;
	
	private String nif;
	
	private String codigoPersona;
	
	private String idPersonaInterno;
	
	private String entidadAltaPersona;	 
	
	private String codRlPersPe;
	
	private String numRlOrden;
	
	private String codTpDe;
}
