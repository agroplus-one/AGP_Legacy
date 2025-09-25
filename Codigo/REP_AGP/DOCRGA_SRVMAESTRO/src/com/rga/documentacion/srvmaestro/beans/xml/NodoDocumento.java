package com.rga.documentacion.srvmaestro.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "xmp:documento")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodoDocumento {

	@XmlElement(name = "rdf:Seq")
	private NodoSeq nodeSeq;
}
