package com.rga.documentacion.srvmaestro.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rdf:Description")
public class NodoDescripcion {

	@XmlElement(name = "xmp:firma")
	private NodoFirma nodoFirma;

	@XmlElement(name = "xmp:documento")
	private NodoDocumento nodoDocumento;

	@XmlElement(name = "xmp:id_firma")
	private HojaFirma hojaFirma;
	
	@XmlElement(name = "xmp:nombre")
	private HojaNombre nombre;
	
	@XmlElement(name = "xmp:nif")
	private HojaNif nif;
	
	@XmlElement(name = "xmp:id_interno")
	private HojaIdInterno idInterno;
	
	@XmlElement(name = "xmp:cod_tipo_persona")
	private HojaCodigoTipoPersona codTipoPersona;
		
	@XmlElement(name = "xmp:cod_ofcna_alta_pe")
	private HojaCodigoOficinaAlta codOfcnaAltaPe;
	
	@XmlElement(name = "xmp:cod_rl_pe_ac")
	private HojaCodigoRelacPersona codRlPersPe;
	
	@XmlElement(name = "xmp:num_orden_rl_ac")
	private HojaCodigoRelacOrden numRlOrden;
	
	@XmlElement(name = "xmp:cod_tipo_de")
	private HojaCodigoTp codTpDe;
	 
	@XmlElement(name = "xmp:pos_X")
	private HojaPosicionX posX;

	@XmlElement(name = "xmp:pos_Y")
	private HojaPosicionY posY;

	@XmlElement(name = "xmp:pos_YV2")
	private HojaPosicionYV2 posYV2;

	@XmlElement(name = "xmp:pagina")
	private HojaPagina pagina;

	@XmlElement(name = "xmp:firma_Alto")
	private HojaFirmaAlto firmaAlto;

	@XmlElement(name = "xmp:firma_Ancho")
	private HojaFirmaAncho firmaAncho;

	@XmlElement(name = "xmp:tipo_emision")
	private HojaTipoEmision tipoEmision;

	@XmlElement(name = "xmp:nombre_documento")
	private HojaNombreDocumento nombreDocumento;

	@XmlElement(name = "xmp:identificacion_emision")
	private HojaIdentificacionEmision identificacionEmision;

	@XmlElement(name = "xmp:formulario")
	private HojaFormulario formulario;

	@XmlElement(name = "xmp:tipo_doc")
	private HojaTipoDoc tipoDoc;

}
