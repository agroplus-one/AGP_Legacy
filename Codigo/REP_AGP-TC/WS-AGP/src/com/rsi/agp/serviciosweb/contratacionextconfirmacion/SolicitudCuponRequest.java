package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import java.math.BigDecimal;

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
 *         &lt;element name="plan" type="{http://www.w3.org/2001/XMLSchema}bigdecimal"/>
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "plan", "referencia" })
@XmlRootElement(name = "solicitudCuponRequest")
public class SolicitudCuponRequest {

	@XmlElement(required = true)
	protected BigDecimal plan;

	@XmlElement(required = true)
	protected String referencia;

	/**
	 * Gets the value of the plan property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getPlan() {
		return plan;
	}

	/**
	 * Sets the value of the plan property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setPlan(BigDecimal value) {
		this.plan = value;
	}

	/**
	 * Gets the value of the referencia property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReferencia() {
		return referencia;
	}

	/**
	 * Sets the value of the referencia property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReferencia(String value) {
		this.referencia = value;
	}
}
