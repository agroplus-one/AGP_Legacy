package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObjetosAsegurados complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjetosAsegurados">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Parcela" type="{http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital}ParcelaReducida" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjetosAsegurados", propOrder = {
    "parcela"
})
public class ObjetosAsegurados {

    @XmlElement(name = "Parcela", required = true)
    protected List<ParcelaReducida> parcela;

    /**
     * Gets the value of the parcela property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parcela property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParcela().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParcelaReducida }
     * 
     * 
     */
    public List<ParcelaReducida> getParcela() {
        if (parcela == null) {
            parcela = new ArrayList<ParcelaReducida>();
        }
        return this.parcela;
    }

	public void setParcela(List<ParcelaReducida> parcela) {
		this.parcela = parcela;
	}

}
