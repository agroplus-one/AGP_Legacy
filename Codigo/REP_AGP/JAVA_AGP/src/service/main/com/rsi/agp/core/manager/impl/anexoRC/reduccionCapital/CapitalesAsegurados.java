package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapitalesAsegurados complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapitalesAsegurados">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CapitalAsegurado" type="{http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital}CapitalAsegurado" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapitalesAsegurados", propOrder = {
    "capitalAsegurado"
})
public class CapitalesAsegurados {

    @XmlElement(name = "CapitalAsegurado", required = true)
    protected List<CapitalAsegurado> capitalAsegurado;

    /**
     * Gets the value of the capitalAsegurado property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the capitalAsegurado property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCapitalAsegurado().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CapitalAsegurado }
     * 
     * 
     */
    public List<CapitalAsegurado> getCapitalAsegurado() {
        if (capitalAsegurado == null) {
            capitalAsegurado = new ArrayList<CapitalAsegurado>();
        }
        return this.capitalAsegurado;
    }

	public void setCapitalAsegurado(List<CapitalAsegurado> capitalAsegurado) {
		this.capitalAsegurado = capitalAsegurado;
	}

}
