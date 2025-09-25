package com.rsi.agp.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class ParserXML {

	private static final String XML_VERSION = "1.0";
	private static final String XML_ENCODING = "ISO-8859-1";// "UTF-8";

	private static final Log logger = LogFactory.getLog(ParserXML.class);

	protected Document documento = null;

	public ParserXML(String xml) throws Exception {
		this.documento = parserDoc(xml, false);
	}

	public ParserXML(String xml, boolean hayDTD) throws Exception {
		this.documento = parserDoc(xml, hayDTD);
	}

	public ParserXML(Document doc) {
		this.documento = doc;

	}

	public String parserDoc(Document doc) throws Exception {
		StringWriter strWriter = null;
		XMLSerializer probeMsgSerializer = null;
		OutputFormat outFormat = null;
		String xmlStr = null;

		probeMsgSerializer = new XMLSerializer();
		strWriter = new StringWriter();
		outFormat = new OutputFormat();

		// Setup format settings
		outFormat.setEncoding(XML_ENCODING);
		outFormat.setVersion(XML_VERSION);
		outFormat.setIndenting(true);
		outFormat.setIndent(4);

		// Define a Writer
		probeMsgSerializer.setOutputCharStream(strWriter);

		// Apply the format settings
		probeMsgSerializer.setOutputFormat(outFormat);

		// Serialize XML Document
		probeMsgSerializer.serialize(doc);

		xmlStr = strWriter.toString();
		strWriter.close();

		return xmlStr;

	}

	public String parserDoc() {
		StringWriter strWriter = null;
		XMLSerializer probeMsgSerializer = null;
		OutputFormat outFormat = null;
		String xmlStr = null;

		probeMsgSerializer = new XMLSerializer();
		strWriter = new StringWriter();
		outFormat = new OutputFormat();

		// Setup format settings
		outFormat.setEncoding(XML_ENCODING);
		outFormat.setVersion(XML_VERSION);
		outFormat.setIndenting(true);
		outFormat.setIndent(4);

		// Define a Writer
		probeMsgSerializer.setOutputCharStream(strWriter);

		// Apply the format settings
		probeMsgSerializer.setOutputFormat(outFormat);
		try {
			// Serialize XML Document
			probeMsgSerializer.serialize(documento);

			xmlStr = strWriter.toString();
			strWriter.close();
		} catch (IOException io) {
			logger.error("Excepcion : ParserXML - parserDoc", io);
		}

		return xmlStr;

	}

	public String parserDoc(String dtd) {
		StringWriter strWriter = null;
		XMLSerializer probeMsgSerializer = null;
		OutputFormat outFormat = null;
		String xmlStr = null;

		probeMsgSerializer = new XMLSerializer();
		strWriter = new StringWriter();
		outFormat = new OutputFormat();

		// Setup format settings
		outFormat.setEncoding(XML_ENCODING);
		outFormat.setVersion(XML_VERSION);
		outFormat.setIndenting(true);
		outFormat.setIndent(4);
		outFormat.setDoctype(null, dtd);

		// Define a Writer
		probeMsgSerializer.setOutputCharStream(strWriter);

		// Apply the format settings
		probeMsgSerializer.setOutputFormat(outFormat);
		try {
			// Serialize XML Document
			probeMsgSerializer.serialize(documento);

			xmlStr = strWriter.toString();
			strWriter.close();
		} catch (IOException io) {
			logger.error("Excepcion : ParserXML - parserDoc", io);
		}

		return xmlStr;

	}

	public Document parserDoc(String doc, boolean hayDTD) throws Exception {

		Document documento = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			StringReader sr = new StringReader(doc);
			org.xml.sax.InputSource is = new org.xml.sax.InputSource(sr);
			documento = factory.newDocumentBuilder().parse(is);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return documento;
	}

	public Document getDocumento() {
		return documento;
	}

	public void setDocumento(Document documento) {
		this.documento = documento;
	}

	public String getString(String Tag) throws ArrayIndexOutOfBoundsException {
		String resultado = null;
		if (documento.getElementsByTagName(Tag).getLength() > 0) {
			if (((Node) documento.getElementsByTagName(Tag).item(0)).getChildNodes().getLength() > 0) {
				resultado = ((Node) documento.getElementsByTagName(Tag).item(0)).getChildNodes().item(0).getNodeValue();

			}
		}
		return resultado;
	}

	public String getString(String Tag, int index) throws ArrayIndexOutOfBoundsException {

		String resultado = null;
		if (documento.getElementsByTagName(Tag).getLength() > 0) {
			if (((Node) documento.getElementsByTagName(Tag).item(index)).getChildNodes().getLength() > 0) {
				resultado = ((Node) documento.getElementsByTagName(Tag).item(index)).getChildNodes().item(0)
						.getNodeValue();
			}
		}
		return resultado;
	}

	public String getAttributeTag(String Tag, String attribute) throws ArrayIndexOutOfBoundsException {
		String resultado = null;
		if (documento.getElementsByTagName(Tag).getLength() > 0) {
			if (((Node) documento.getElementsByTagName(Tag).item(0)).hasAttributes()) {
				Node node = (Node) documento.getElementsByTagName(Tag).item(0);
				for (int i = 0; i < node.getAttributes().getLength(); i++) {
					String atributos_nombre = node.getAttributes().item(i).getNodeName();
					if (atributos_nombre.equalsIgnoreCase(attribute))
						resultado = node.getAttributes().item(i).getNodeValue();
				}
			}
		}
		return resultado;
	}

	public String getAttributeTag(String Tag, String attribute, int index) throws ArrayIndexOutOfBoundsException {
		String resultado = null;
		if (documento.getElementsByTagName(Tag).getLength() > 0) {
			if (((Node) documento.getElementsByTagName(Tag).item(index)).hasAttributes()) {
				Node node = (Node) documento.getElementsByTagName(Tag).item(index);
				for (int i = 0; i < node.getAttributes().getLength(); i++) {
					String atributos_nombre = node.getAttributes().item(i).getNodeName();
					if (atributos_nombre.equalsIgnoreCase(attribute))
						resultado = node.getAttributes().item(i).getNodeValue();
				}
			}
		}
		return resultado;
	}

	public int getIndice(String tag, String attb, String valorId) {
		int contador = countTag(tag);
		int indice = -1;
		for (int i = 0; i < contador; i++) {
			indice = i;
			if (valorId.equalsIgnoreCase(getAttributeTag(tag, attb, i)))
				break;
		}
		return indice;
	}

	/*
	 * public String getCampo(String tag, String attb,String valorId){ int contador
	 * = countTag(tag); int indice = -1; for (int i=0;i<contador; i++){ indice = i;
	 * if (valorId.equalsIgnoreCase(getAttributeTag(tag,attb,i))) break; } return
	 * getString(tag,indice); }
	 */

	public int countTag(String Tag) {
		return documento.getElementsByTagName(Tag).getLength();
	}

	public boolean existeTag(String tag) {
		if (countTag(tag) > 0)
			return true;
		else
			return false;
	}

	/**
	 * Indica si existe un tag par un determinado indice.
	 * 
	 * @param tag
	 *            String
	 * @param index
	 *            int
	 * @return boolean
	 */
	public boolean existeTag(String tag, int index) {
		int count = countTag(tag);
		return (count > 0) && (index <= count);
	}

	public Document getXml(String Tag, int index) throws ArrayIndexOutOfBoundsException, Exception {

		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		try {
			df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			df.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		Document resultado = df.newDocumentBuilder().newDocument();

		if (documento.getElementsByTagName(Tag).getLength() > 0) {
			if (((Node) documento.getElementsByTagName(Tag).item(index)).getChildNodes().getLength() > 0) {
				Node nodo = documento.getElementsByTagName(Tag).item(index).getChildNodes().item(0).getParentNode();
				NodeList list = nodo.getChildNodes();
				Node aux = resultado.importNode(nodo, false);
				resultado.appendChild(aux);
				for (int j = 0; j < list.getLength(); j++) {
					// Get element
					Node nodoAux = (Node) list.item(j);
					if (!nodoAux.getNodeName().equals("#text")) {
						Node aux2 = resultado.importNode(nodoAux, true);
						aux.appendChild(aux2);
					}
				}
			}
		}
		return resultado;
	}

	class ManejadorErrores extends DefaultHandler {

		public void error(SAXParseException exc) throws SAXException {
			throw new SAXException("**Error**\n" + "  Linea:    " + exc.getLineNumber() + "\n" + "  Columna:    "
					+ exc.getColumnNumber() + "\n" + "  URI:     " + exc.getSystemId() + "\n" + "  Mensaje: "
					+ exc.getMessage());

		}

		public void fatalError(SAXParseException exc) throws SAXException {

			throw new SAXException("**Error Fatal**\n" + "  Linea:    " + exc.getLineNumber() + "\n"
					+ "  Columna:    " + exc.getColumnNumber() + "\n" + "  URI:     " + exc.getSystemId() + "\n"
					+ "  Mensaje: " + exc.getMessage());
		}

	}
	// ************************************ METODOS A�ADIDOS
	// **********************************
	// ************************************ CMG **********************************

}
