package com.rsi.agp.core.executor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.xml.sax.InputSource;

/**
 * Clase para realizar la transformacion de los ficheros xml recibidos de agroseguro a los ficheros xml que se utilizan
 * para realizar la insercion en la base de datos.
 * Recibe como parametros el fichero xml de origen y el fichero xsl de transformacion y el resultado lo guarda en 
 * otro fichero xml cuyo nombre se indica como tercer parametro.
 * 
 * Esta clase debe ser llamada desde linea de comando indicando que la maquina virtual de java tiene al menos 768Mb.
 * @author U028783
 *
 */
public class XmlTransformer {
	
	static String fileName;

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: java "
					+ XmlTransformer.class.getName()
					+ " <XML that needs to be transformed>" + " <XSLT file>"
					+ " <Output file>");
			System.out.println("Optional parameters: "
					+ " <XSLT parameters>" + " <XSLT parameter values>");
			System.exit(1);
		}
		
		try {
			transform(args);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			System.exit(2);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (TransformerException e) {
			e.printStackTrace();
			System.exit(4);
		}
		
	}

	private static void transform(String[] args) throws XMLStreamException, FactoryConfigurationError, IOException, TransformerException {
		fileName = args[2].toString();
		InputSource xmlIn = new InputSource(new FileInputStream(args[0]));
		InputSource xsltIn = new InputSource(new FileInputStream(args[1]));
		FileOutputStream out = new FileOutputStream(args[2]);
		if (args.length == 3){
			transform(xmlIn, xsltIn, out);
		}
		else{
			//Nombres de los parametros separados por '|'
			String paramNames = args[3];
			//Valores para los parametros separados por '|'
			String paramValues = args[4];
			transform(xmlIn, xsltIn, out, paramNames.split("\\|"), paramValues.split("\\|"));
		}
	}

	public static void transform(InputSource xmlIn, InputSource xsltIn, OutputStream out) throws XMLStreamException, FactoryConfigurationError, IOException, TransformerException {
		
		javax.xml.transform.Result xmlResult = new javax.xml.transform.stax.StAXResult(
				XMLOutputFactory.newInstance().createXMLStreamWriter(
						new FileWriter(fileName)));

		javax.xml.transform.Source xsltSource = new javax.xml.transform.sax.SAXSource(xsltIn);
		javax.xml.transform.Source xmlSource = new javax.xml.transform.sax.SAXSource(xmlIn);

		try {
			// create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();

			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);

			trans.transform(xmlSource, xmlResult);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void transform(InputSource xmlIn, InputSource xsltIn,
			OutputStream out, String[] parameterNames, String[] parameterValues) throws XMLStreamException, FactoryConfigurationError, IOException, TransformerException {
		javax.xml.transform.Result xmlResult = new javax.xml.transform.stax.StAXResult(
				XMLOutputFactory.newInstance().createXMLStreamWriter(
						new FileWriter(fileName)));

		javax.xml.transform.Source xsltSource = new javax.xml.transform.sax.SAXSource(xsltIn);
		javax.xml.transform.Source xmlSource = new javax.xml.transform.sax.SAXSource(xmlIn);

		try {
			// create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();

			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);

			// recorro los parametros y los anhado al Transformer
			for (int i = 0; i < parameterNames.length; i++) {
				trans.setParameter(parameterNames[i], parameterValues[i]);
			}

			trans.transform(xmlSource, xmlResult);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}