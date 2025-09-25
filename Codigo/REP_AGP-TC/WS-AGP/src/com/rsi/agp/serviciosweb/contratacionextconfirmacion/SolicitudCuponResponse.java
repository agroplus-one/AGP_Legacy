
package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
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
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="poliza" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="polizaComp" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="estadoContratacion" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="cuponModificacion" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="polizaRC" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="polizaCompRC" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codigo", "mensaje", "poliza", "polizaComp", "estadoContratacion",
		"cuponModificacion", "polizaRC", "polizaCompRC" })
@XmlRootElement(name = "confirmarResponse")
public class SolicitudCuponResponse {

	@XmlElement(required = true)
	protected String codigo;
	@XmlElement(required = true)
	protected String mensaje;
	@XmlMimeType("text/xml")
	protected Base64Binary poliza;
	@XmlMimeType("text/xml")
	protected Base64Binary polizaComp;
	@XmlMimeType("text/xml")
	protected Base64Binary estadoContratacion;
	@XmlMimeType("text/xml")
	protected Base64Binary cuponModificacion;
	@XmlMimeType("text/xml")
	protected Base64Binary polizaRC;
	@XmlMimeType("text/xml")
	protected Base64Binary polizaCompRC;

	/**
	 * Gets the value of the codigo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Sets the value of the codigo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodigo(String value) {
		this.codigo = value;
	}

	/**
	 * Gets the value of the mensaje property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Sets the value of the mensaje property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMensaje(String value) {
		this.mensaje = value;
	}

	/**
	 * Gets the value of the poliza property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getPoliza() {
		return poliza;
	}

	/**
	 * Sets the value of the poliza property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setPoliza(Base64Binary value) {
		this.poliza = value;
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

	/**
	 * Gets the value of the estadoContratacion property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getEstadoContratacion() {
		return estadoContratacion;
	}

	/**
	 * Sets the value of the estadoContratacion property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setEstadoContratacion(Base64Binary value) {
		this.estadoContratacion = value;
	}

	/**
	 * Gets the value of the cuponModificacion property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getCuponModificacion() {
		return cuponModificacion;
	}

	/**
	 * Sets the value of the cuponModificacion property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setCuponModificacion(Base64Binary value) {
		this.cuponModificacion = value;
	}

	/**
	 * Gets the value of the polizaRC property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getPolizaRC() {
		return polizaRC;
	}

	/**
	 * Sets the value of the polizaRC property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setPolizaRC(Base64Binary value) {
		this.polizaRC = value;
	}

	/**
	 * Gets the value of the polizaCompRC property.
	 * 
	 * @return possible object is {@link Base64Binary }
	 * 
	 */
	public Base64Binary getPolizaCompRC() {
		return polizaCompRC;
	}

	/**
	 * Sets the value of the polizaCompRC property.
	 * 
	 * @param value
	 *            allowed object is {@link Base64Binary }
	 * 
	 */
	public void setPolizaCompRC(Base64Binary value) {
		this.polizaCompRC = value;
	}
}
