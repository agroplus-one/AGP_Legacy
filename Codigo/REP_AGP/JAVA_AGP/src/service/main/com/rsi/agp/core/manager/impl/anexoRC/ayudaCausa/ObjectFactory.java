package com.rsi.agp.core.manager.impl.anexoRC.ayudaCausa;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.agroseguro.seguroagrario.contratacion.ayudacausa package. 
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

    private final static QName _Causas_QNAME = new QName("http://www.agroseguro.es/SeguroAgrario/Contratacion/AyudaCausa", "Causas");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.agroseguro.seguroagrario.contratacion.ayudacausa
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Causa }
     * 
     */
    public Causa createCausa() {
        return new Causa();
    }

    /**
     * Create an instance of {@link Causas }
     * 
     */
    public Causas createCausas() {
        return new Causas();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Causas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agroseguro.es/SeguroAgrario/Contratacion/AyudaCausa", name = "Causas")
    public JAXBElement<Causas> createCausas(Causas value) {
        return new JAXBElement<Causas>(_Causas_QNAME, Causas.class, null, value);
    }

}
