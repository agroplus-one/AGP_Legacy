package com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Causas complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Causas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Causa" type="{http://www.agroseguro.es/SeguroAgrario/Contratacion/AyudaCausa}Causa" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Causas", propOrder = {
    "causa"
})
public class Causas {

    @XmlElement(name = "Causa", required = true)
    protected List<Causa> causa;

    /**
     * Gets the value of the causa property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the causa property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCausa().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Causa }
     * 
     * 
     */
    public List<Causa> getCausa() {
        if (causa == null) {
            causa = new ArrayList<Causa>();
        }
        return this.causa;
    }

}
