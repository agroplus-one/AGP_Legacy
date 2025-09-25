package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import es.agroseguro.tipos.MotivoReduccionCapital;


/**
 * <p>Java class for PolizaReduccionCapital complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolizaReduccionCapital">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Motivo" type="{http://www.agroseguro.es/Tipos}Motivo_ReduccionCapital"/>
 *         &lt;element name="ObjetosAsegurados" type="{http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital}ObjetosAsegurados"/>
 *       &lt;/sequence>
 *       &lt;attribute name="plan" use="required" type="{http://www.agroseguro.es/Tipos}Plan" />
 *       &lt;attribute name="referencia" use="required" type="{http://www.agroseguro.es/Tipos}Poliza_Referencia_Creciente" />
 *       &lt;attribute name="modulo" use="required" type="{http://www.agroseguro.es/Tipos}Cobertura_Modulo" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolizaReduccionCapital", propOrder = {
    "motivo",
    "objetosAsegurados"
})
public class PolizaReduccionCapital {

    @XmlElement(name = "Motivo", required = true)
    protected MotivoReduccionCapital motivo;
    @XmlElement(name = "ObjetosAsegurados", required = true)
    protected ObjetosAsegurados objetosAsegurados;
    @XmlAttribute(required = true)
    protected int plan;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String referencia;
    @XmlAttribute(required = true)
    protected String modulo;

    /**
     * Gets the value of the motivo property.
     * 
     * @return
     *     possible object is
     *     {@link MotivoReduccionCapital }
     *     
     */
    public MotivoReduccionCapital getMotivo() {
        return motivo;
    }

    /**
     * Sets the value of the motivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MotivoReduccionCapital }
     *     
     */
    public void setMotivo(MotivoReduccionCapital value) {
        this.motivo = value;
    }

    /**
     * Gets the value of the objetosAsegurados property.
     * 
     * @return
     *     possible object is
     *     {@link ObjetosAsegurados }
     *     
     */
    public ObjetosAsegurados getObjetosAsegurados() {
        return objetosAsegurados;
    }

    /**
     * Sets the value of the objetosAsegurados property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjetosAsegurados }
     *     
     */
    public void setObjetosAsegurados(ObjetosAsegurados value) {
        this.objetosAsegurados = value;
    }

    /**
     * Gets the value of the plan property.
     * 
     */
    public int getPlan() {
        return plan;
    }

    /**
     * Sets the value of the plan property.
     * 
     */
    public void setPlan(int value) {
        this.plan = value;
    }

    /**
     * Gets the value of the referencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Sets the value of the referencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencia(String value) {
        this.referencia = value;
    }

    /**
     * Gets the value of the modulo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * Sets the value of the modulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModulo(String value) {
        this.modulo = value;
    }

	@Override
	public String toString() {
		Date fecha = this.getMotivo().getFechaOcurrencia().toGregorianCalendar().getTime();
        // Definir el formato deseado dd-MM-yyyy
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        String fechaFormateada = formatter.format(fecha);
		String cabecera = "<ns2:PolizaReduccionCapital xmlns:ns2=\"http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital\" modulo=\""
	+this.modulo+"\""+ " plan=\""+this.plan+"\" referencia=\""+this.referencia+"\">";
		String motivo= "<Motivo causaDanio=\""+this.getMotivo().getCausaDanio()+"\" fechaOcurrencia=\""+fechaFormateada+"\" />";
		String objetosAsegurados= "<ObjetosAsegurados>";

			for(ParcelaReducida parc : this.getObjetosAsegurados().getParcela()) {
				objetosAsegurados+= "<Parcela hoja=\""+parc.hoja+"\" numero=\""+parc.numero+"\"><CapitalesAsegurados>";
				for(CapitalAsegurado capAsegurado : parc.getCapitalesAsegurados().getCapitalAsegurado()) {
					objetosAsegurados+= "<CapitalAsegurado produccionTrasReduccion=\""+capAsegurado.getProduccionTrasReduccion()
					+"\" tipo=\""+capAsegurado.getTipo()+"\" />";
				}
				objetosAsegurados+="</CapitalesAsegurados></Parcela>";
			}
			
			objetosAsegurados+="</ObjetosAsegurados>";
		String cierre ="</ns2:PolizaReduccionCapital>";
		return cabecera + motivo + objetosAsegurados + cierre;
	}

}
