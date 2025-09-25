package com.rga.documentacion.srvmaestro.beans;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class OutputBean {

	private String codigoRetorno;
	
	private String codigoError;

	private String textoError;

	private String rutaFichero;

	private String fichero;

	private String indicadorTableta;

	private String idioma;

	private String stream;

	private DatosSalidaCSVBean datosCSV;
	
	//P22250 Indicador firma diferida
	private String indicadorFirmaDiferida;
	
	//25366 Indicador de soporte duradero
	private String indicadorSoporteDuradero;	
	
	//P24996 C�digo formulario tableta
	private String formulariotableta;
	
	//P24996 C�dgio documento tableta
	private String codigoDocumentoTableta;
	
	//P24996 Referencia un�voca del documento. Equivaldr� al campo IDARCHIV de las tablas de GED Seguros.
	private String referenciaDocumento;
	
	//P24996 Clase documental /Negocio donde se guarda en GED Seguros para que se pueda consultar posteriormente.
	private String negocio;
	
	//P24996 Ruta origen donde se disponibilizar� el fichero para la firma en tableta
	private String rutaFicheroTabletaOrigen;
	
	//P24996 Ruta destino donde se disponibilizar� el fichero para la firma en tableta
	private String rutaFicheroTabletaDestino;
	
	// N�mero de p�ginas del documento
	private int numPaginas;
}
