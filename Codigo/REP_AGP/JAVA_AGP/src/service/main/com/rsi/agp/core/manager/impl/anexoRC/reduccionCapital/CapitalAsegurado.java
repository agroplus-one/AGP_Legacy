package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapitalAsegurado complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapitalAsegurado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="tipo" type="{http://www.agroseguro.es/Tipos}TipoCapital" default="0" />
 *       &lt;attribute name="produccionTrasReduccion" use="required" type="{http://www.agroseguro.es/Tipos}ProduccionKG" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapitalAsegurado")
public class CapitalAsegurado {

    @XmlAttribute
    protected Integer tipo;
    @XmlAttribute(required = true)
    protected int produccionTrasReduccion;

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getTipo() {
        if (tipo == null) {
            return  0;
        } else {
            return tipo;
        }
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTipo(Integer value) {
        this.tipo = value;
    }

    /**
     * Gets the value of the produccionTrasReduccion property.
     * 
     */
    public int getProduccionTrasReduccion() {
        return produccionTrasReduccion;
    }

    /**
     * Sets the value of the produccionTrasReduccion property.
     * 
     */
    public void setProduccionTrasReduccion(int value) {
        this.produccionTrasReduccion = value;
    }

}
