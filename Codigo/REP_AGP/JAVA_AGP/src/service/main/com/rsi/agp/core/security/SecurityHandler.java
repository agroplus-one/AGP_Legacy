package com.rsi.agp.core.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Log logger = LogFactory.getLog(SecurityHandler.class);

	public SecurityHandler() {
		super();
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public void close(MessageContext context) {
		//EMTPY METHOD
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		boolean result = false;
		try {
			addSecurityHeader(context);
			result = true;
		} catch (SOAPException e) {
			logger.error("Excepcion : SecurityHandler - handleMessage", e);
		}
		return result;
	}

	private void addSecurityHeader(SOAPMessageContext context) throws SOAPException {
		SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
		envelope.addNamespaceDeclaration("wsu",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

		SOAPHeader header;
		if (envelope.getHeader() != null)
			header = envelope.getHeader();
		else
			header = envelope.addHeader();
		String namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		SOAPElement security = header.addChildElement("Security", "oas", namespace);
		security.addNamespaceDeclaration("", namespace);

		SOAPElement usernameTokenElement = security
				.addChildElement(envelope.createName("UsernameToken", "oas", namespace));
		usernameTokenElement.addNamespaceDeclaration("", namespace);

		//test seguridad invocacion INI
		//SOAPElement usernameElement = usernameTokenElement.addChildElement(envelope.createName("Username"));
		//SOAPElement passwordElement = usernameTokenElement.addChildElement(envelope.createName("Password"));
		SOAPElement usernameElement = usernameTokenElement.addChildElement(envelope.createName("Username", "oas", namespace));
		SOAPElement passwordElement = usernameTokenElement.addChildElement(envelope.createName("Password", "oas", namespace));
		//test seguridad invocacion FIN
		
		ResourceBundle rb = ResourceBundle.getBundle("webservices");
		usernameElement.setValue(rb.getString("security.user"));
		passwordElement.setValue(rb.getString("security.password"));
		logger.debug("addSecurityHeader - user='" + rb.getString("security.user") + "', pass='"
				+ rb.getString("security.password") + "'");
		
		//test seguridad invocacion INI
		try {
			SOAPMessage message = context.getMessage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        message.writeTo(baos);
	        String soapMessage = new String(baos.toByteArray(), "UTF-8");
	        
	        System.out.println("SOAP Content[addSecurityHeader]:");
	
	        System.out.println(soapMessage);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		//test seguridad invocacion FIN
	}

}
