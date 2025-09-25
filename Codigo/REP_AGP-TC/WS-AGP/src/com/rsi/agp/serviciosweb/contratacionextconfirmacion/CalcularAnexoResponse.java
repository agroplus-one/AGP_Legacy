
package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.w3._2005._05.xmlmime.Base64Binary;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="acuseRecibo" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="calculoModificacion" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="calculoOriginal" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="diferenciasCoste" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="errors" type="{http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/}Error" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codigo",
    "mensaje",
    "acuseRecibo",
    "calculoModificacion",
    "calculoOriginal",
    "diferenciasCoste",
    "errors"
})
@XmlRootElement(name = "calcularResponse")
public class CalcularAnexoResponse {

    @XmlElement(required = true)
    protected String codigo;
    @XmlElement(required = true)
    protected String mensaje;
    @XmlMimeType("text/xml")
    protected Base64Binary acuseRecibo;
    protected Base64Binary calculoModificacion;
    protected Base64Binary calculoOriginal;
    protected Base64Binary diferenciasCoste;
    protected List<Error> errors;

    /**
     * Gets the value of the codigo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Sets the value of the codigo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
    }

    /**
     * Gets the value of the mensaje property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Sets the value of the mensaje property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensaje(String value) {
        this.mensaje = value;
    }
    
    /**
     * Gets the value of the acuseRecibo property.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getAcuseRecibo() {
        return acuseRecibo;
    }

    /**
     * Sets the value of the acuseRecibo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setAcuseRecibo(Base64Binary value) {
        this.acuseRecibo = value;
    }

    /**
     * Gets the value of the calculoModificacion property.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getCalculoModificacion() {
        return calculoModificacion;
    }

    /**
     * Sets the value of the calculoModificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setCalculoModificacion(Base64Binary value) {
        this.calculoModificacion = value;
    }
    
    /**
     * Gets the value of the calculoOriginal property.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getCalculoOriginal() {
        return calculoOriginal;
    }

    /**
     * Sets the value of the calculoOriginal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setCalculoOriginal(Base64Binary value) {
        this.calculoOriginal = value;
    }
    
    
    /**
     * Gets the value of the diferenciasCoste property.
     * 
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *     
     */
    public Base64Binary getDiferenciasCoste() {
        return diferenciasCoste;
    }

    /**
     * Sets the value of the diferenciasCoste property.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *     
     */
    public void setDiferenciasCoste(Base64Binary value) {
        this.diferenciasCoste = value;
    }


    /**
     * Gets the value of the errors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Error }
     * 
     * 
     */
    public List<Error> getErrors() {
        if (errors == null) {
            errors = new ArrayList<Error>();
        }
        return this.errors;
    }

}
