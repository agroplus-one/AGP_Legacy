package com.rga.documentacion.srvmaestro.beans.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cod_rl_pe_ac")
public class HojaCodigoRelacPersona {

	@XmlValue
	private String value;
}
