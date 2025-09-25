package com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.agroseguro.seguroagrario.contratacion.reduccioncapital package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PolizaReduccionCapital_QNAME = new QName("http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital", "PolizaReduccionCapital");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.agroseguro.seguroagrario.contratacion.reduccioncapital
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PolizaReduccionCapital }
     * 
     */
    public PolizaReduccionCapital createPolizaReduccionCapital() {
        return new PolizaReduccionCapital();
    }

    /**
     * Create an instance of {@link ParcelaReducida }
     * 
     */
    public ParcelaReducida createParcelaReducida() {
        return new ParcelaReducida();
    }

    /**
     * Create an instance of {@link ObjetosAsegurados }
     * 
     */
    public ObjetosAsegurados createObjetosAsegurados() {
        return new ObjetosAsegurados();
    }

    /**
     * Create an instance of {@link CapitalesAsegurados }
     * 
     */
    public CapitalesAsegurados createCapitalesAsegurados() {
        return new CapitalesAsegurados();
    }

    /**
     * Create an instance of {@link CapitalAsegurado }
     * 
     */
    public CapitalAsegurado createCapitalAsegurado() {
        return new CapitalAsegurado();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolizaReduccionCapital }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agroseguro.es/SeguroAgrario/Contratacion/ReduccionCapital", name = "PolizaReduccionCapital")
    public JAXBElement<PolizaReduccionCapital> createPolizaReduccionCapital(PolizaReduccionCapital value) {
        return new JAXBElement<PolizaReduccionCapital>(_PolizaReduccionCapital_QNAME, PolizaReduccionCapital.class, null, value);
    }

}
