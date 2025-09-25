package com.rga.documentacion.srvmaestro.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "rdf:Description")
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

	@XmlAttribute(name = "rdf:about")
	private String about = "";

	@XmlAttribute(name = "xmlns:xmp")
	private String schema = "http://ns.adobe.com/xap/1.0/";

	@XmlElement(name = "xmp:Elements")
	private NodoElement nodoElemento; 
}
