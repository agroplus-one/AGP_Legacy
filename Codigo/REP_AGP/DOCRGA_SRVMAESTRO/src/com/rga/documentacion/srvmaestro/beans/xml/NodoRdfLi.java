package com.rga.documentacion.srvmaestro.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "rdf:li")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodoRdfLi {

	@XmlElement(name = "rdf:Description")
	private NodoDescripcion descripcion;
}
