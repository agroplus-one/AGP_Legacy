
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
 *         &lt;element name="idCupon" type="{http://www.w3.org/2005/05/xmlmime}string"/>
 *         &lt;element name="tipoPoliza" type="{http://www.w3.org/2005/05/xmlmime}string"/>
 *         &lt;element name="calcularSituacionActual" type="{http://www.w3.org/2005/05/xmlmime}boolean"/>
 *         &lt;element name="modificacionPoliza" type="{http://www.w3.org/2005/05/xmlmime}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "idCupon", "tipoPoliza", "modificacionPoliza" })
@XmlRootElement(name = "calcularAnexoRequest")
public class CalcularAnexoRequest {

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
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCupon(String value) {
        this.idCupon = value;
    }
	
	@XmlElement(required = true)
	protected String tipoPoliza;
	/**
	 * Gets the value of the tipoPoliza property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoPoliza() {
		return tipoPoliza;
	}
	/**
     * Sets the value of the tipoPoliza property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoPoliza(String value) {
        this.tipoPoliza = value;
    }


	@XmlElement(required = true)
	protected Base64Binary modificacionPoliza;
	
	/**
     * Gets the value of the modificacionPoliza property.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getModificacionPoliza() {
        return modificacionPoliza;
    }
    /**
     * Sets the value of the modificacionPoliza property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setModificacionPoliza(Base64Binary value) {
        this.modificacionPoliza = value;
    }

	
	

}
