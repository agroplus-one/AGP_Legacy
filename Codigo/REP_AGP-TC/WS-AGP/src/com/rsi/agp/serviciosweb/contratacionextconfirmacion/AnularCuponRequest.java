package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idCupon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "idCupon" })
@XmlRootElement(name = "anularCuponRequest")
public class AnularCuponRequest {

	@XmlElement(required = true)
	protected String idCupon;

	/**
	 * Gets the value of the idCupon property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdCupon() {
		return idCupon;
	}

	/**
	 * Sets the value of the idCupon property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdCupon(String value) {
		this.idCupon = value;
	}
}
