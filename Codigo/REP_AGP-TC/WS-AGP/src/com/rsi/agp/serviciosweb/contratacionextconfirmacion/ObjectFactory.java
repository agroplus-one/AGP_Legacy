
package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.rsi.agp.serviciosweb.contratacionextconfirmacion package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.rsi.agp.serviciosweb.contratacionextconfirmacion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConfirmarRequest }
     * 
     */
    public ConfirmarRequest createConfirmarRequest() {
        return new ConfirmarRequest();
    }

    /**
     * Create an instance of {@link ConfirmarResponse }
     * 
     */
    public ConfirmarResponse createConfirmarResponse() {
        return new ConfirmarResponse();
    }

    /**
     * Create an instance of {@link AgpFallo }
     * 
     */
    public AgpFallo createAgpFallo() {
        return new AgpFallo();
    }

    /**
     * Create an instance of {@link Error }
     * 
     */
    public Error createError() {
        return new Error();
    }

}
