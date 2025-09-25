package com.rsi.agp.core.managers.ged.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GedHeaderHandlerResolver implements HandlerResolver {

	private static final Log LOGGER = LogFactory.getLog(GedHeaderHandlerResolver.class);

	private final Map<String, String> configParams;

	public GedHeaderHandlerResolver(final Map<String, String> configParams) {

		super();
		this.configParams = configParams;
	}

	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo) {

		List<Handler> handlerChain = new ArrayList<Handler>();

		GedHeaderHandler hh = new GedHeaderHandler(this.configParams);

		handlerChain.add(hh);

		return handlerChain;
	}

	public class GedHeaderHandler implements SOAPHandler<SOAPMessageContext> {

		final String codSecUser;
		final String codSecTrans;
		final String codSecEnt;
		final String codTerminal;
		final String codSecIp;
		final String codApl;
		final String codCanal;
		final String codCorrelationId;

		public GedHeaderHandler(final Map<String, String> configParams) {

			super();
			this.codSecUser = configParams.get("CODSecUser");
			this.codSecTrans = configParams.get("CODSecTrans");
			this.codSecEnt = configParams.get("CODSecEnt");
			this.codTerminal = configParams.get("CODTerminal");
			this.codSecIp = configParams.get("CODSecIp");
			this.codApl = configParams.get("CODApl");
			this.codCanal = configParams.get("CODCanal");
			this.codCorrelationId = configParams.get("CODCorrelationId");
		}

		public boolean handleMessage(SOAPMessageContext smc) {

			Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

			if (outboundProperty.booleanValue()) {

				try {

					SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();

					SOAPHeader header = envelope.getHeader();
					
					if (header == null) {
						
						header = envelope.addHeader();
					}

					SOAPElement rsiHeader = header.addChildElement("RSI_Header", "sec",
							"http://www.ruralserviciosinformaticos.com/XSD/SecurityHeader/");

					SOAPElement codSecUser = rsiHeader.addChildElement("CODSecUser", "sec");
					codSecUser.addTextNode(this.codSecUser == null ? "" : this.codSecUser);

					SOAPElement codSecTrans = rsiHeader.addChildElement("CODSecTrans", "sec");
					codSecTrans.addTextNode(this.codSecTrans == null ? "" : this.codSecTrans);

					SOAPElement codSecEnt = rsiHeader.addChildElement("CODSecEnt", "sec");
					codSecEnt.addTextNode(this.codSecEnt == null ? "" : this.codSecEnt);

					SOAPElement codTerminal = rsiHeader.addChildElement("CODTerminal", "sec");
					codTerminal.addTextNode(this.codTerminal == null ? "" : this.codTerminal);

					SOAPElement codSecIp = rsiHeader.addChildElement("CODSecIp", "sec");
					codSecIp.addTextNode(this.codSecIp == null ? "" : this.codSecIp);

					SOAPElement codApl = rsiHeader.addChildElement("CODApl", "sec");
					codApl.addTextNode(this.codApl == null ? "" : this.codApl);

					SOAPElement codCanal = rsiHeader.addChildElement("CODCanal", "sec");
					codCanal.addTextNode(this.codCanal == null ? "" : this.codCanal);

					SOAPElement codCorrelationId = rsiHeader.addChildElement("CODCorrelationId", "sec");
					codCorrelationId.addTextNode(this.codCorrelationId == null ? "" : this.codCorrelationId);
				} catch (SOAPException e) {

					LOGGER.error("Error al montar las cabeceras del mensaje: ", e);
				}
			}

			return outboundProperty;

		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Set getHeaders() {

			// throw new UnsupportedOperationException("Not supported yet.");
			return null;
		}

		public boolean handleFault(SOAPMessageContext context) {

			// throw new UnsupportedOperationException("Not supported yet.");
			return true;
		}

		public void close(MessageContext context) {

			// throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}