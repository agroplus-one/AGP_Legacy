
package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.w3._2005._05.xmlmime.Base64Binary;

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
 *       	&lt;element name="idCupon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;element name="revisionAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="polizaPpal" type="{http://www.w3.org/2005/05/xmlmime}base64Binary"/>
 *         &lt;element name="polizaComp" type="{http://www.w3.org/2005/05/xmlmime}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "idCupon", "revisionAdmin", "polizaPpal", "polizaComp" })
@XmlRootElement(name = "confirmarCuponRequest")
public class ConfirmarCuponRequest {

	@XmlElement(required = true)
	protected String idCupon;
	@XmlElement(required = true)
	protected Boolean revisionAdmin;
	@XmlElement(required = false)
	protected Base64Binary polizaPpal;
	@XmlElement(required = false)
	protected Base64Binary polizaComp;

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

	/**
	 * Gets the value of the revisionAdmin property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public Boolean getRevisionAdmin() {
		return revisionAdmin;
	}

	/**
	 * Sets the value of the revisionAdmin property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setRevisionAdmin(Boolean value) {
		this.revisionAdmin = value;
	}

	/**
	 * Gets the value of the polizaPpal property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getPolizaPpal() {
		return polizaPpal;
	}

	/**
	 * Sets the value of the polizaPpal property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setPolizaPpal(Base64Binary value) {
		this.polizaPpal = value;
	}

	/**
	 * Gets the value of the polizaComp property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getPolizaComp() {
		return polizaComp;
	}

	/**
	 * Sets the value of the polizaComp property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setPolizaComp(Base64Binary value) {
		this.polizaComp = value;
	}
}
