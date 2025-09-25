package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParcelaReducida complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParcelaReducida">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CapitalesAsegurados" type="{http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital}CapitalesAsegurados"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hoja" use="required" type="{http://www.agroseguro.es/Tipos}Parcela_Hoja" />
 *       &lt;attribute name="numero" use="required" type="{http://www.agroseguro.es/Tipos}Parcela_Numero" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParcelaReducida", propOrder = {
    "capitalesAsegurados"
})
public class ParcelaReducida {

    @XmlElement(name = "CapitalesAsegurados", required = true)
    protected CapitalesAsegurados capitalesAsegurados;
    @XmlAttribute(required = true)
    protected int hoja;
    @XmlAttribute(required = true)
    protected int numero;

    /**
     * Gets the value of the capitalesAsegurados property.
     * 
     * @return
     *     possible object is
     *     {@link CapitalesAsegurados }
     *     
     */
    public CapitalesAsegurados getCapitalesAsegurados() {
        return capitalesAsegurados;
    }

    /**
     * Sets the value of the capitalesAsegurados property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapitalesAsegurados }
     *     
     */
    public void setCapitalesAsegurados(CapitalesAsegurados value) {
        this.capitalesAsegurados = value;
    }

    /**
     * Gets the value of the hoja property.
     * 
     */
    public int getHoja() {
        return hoja;
    }

    /**
     * Sets the value of the hoja property.
     * 
     */
    public void setHoja(int value) {
        this.hoja = value;
    }

    /**
     * Gets the value of the numero property.
     * 
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Sets the value of the numero property.
     * 
     */
    public void setNumero(int value) {
        this.numero = value;
    }

}
