package com.rsi.agp.core.security;

import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

	public SecurityHandler() {
		super();
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext context) {
		// EMPTY METHOD
	}

	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	public boolean handleMessage(SOAPMessageContext context) {
		boolean result = false;
		try {
			addSecurityHeader(context);
			result = true;
		} catch (SOAPException e) {
			e.printStackTrace();
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

		SOAPElement usernameElement = usernameTokenElement.addChildElement(envelope.createName("Username"));
		SOAPElement passwordElement = usernameTokenElement.addChildElement(envelope.createName("Password"));

		ResourceBundle rb = ResourceBundle.getBundle("webservices");
		usernameElement.setValue(rb.getString("security.user"));
		passwordElement.setValue(rb.getString("security.password"));

	}

}
