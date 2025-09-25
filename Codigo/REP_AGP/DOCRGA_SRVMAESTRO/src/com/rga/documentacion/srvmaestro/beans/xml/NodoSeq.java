package com.rga.documentacion.srvmaestro.beans.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "rdf:Seq")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodoSeq {

	@XmlElement(name= "rdf:li")
	private List<NodoRdfLi> rdfLi;
}
