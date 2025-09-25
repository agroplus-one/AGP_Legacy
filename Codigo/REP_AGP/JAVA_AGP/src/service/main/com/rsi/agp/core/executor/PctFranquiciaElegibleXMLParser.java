package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;

public class PctFranquiciaElegibleXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(PctFranquiciaElegibleXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\PctFranquiciaElegible.xml";
		args[1] = "D:\\borrar\\PctFranquiciaElegible.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PctFranquiciaElegibleXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PctFranquiciaElegibleXMLParser parser = new PctFranquiciaElegibleXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Porcentaje de franquicia elegible " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Porcentaje de franquicia elegible " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		PctFranquiciaElegible registro = (PctFranquiciaElegible)reg;
		String sql = "";
		sql += registro.getCodpctfranquiciaeleg() + ";" + StringUtils.nullToString(registro.getValor()) + ";";
		sql += StringUtils.nullToString(registro.getDespctfranquiciaeleg()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PctFranquiciaElegible registro;
		if (actual == null){
			registro = new PctFranquiciaElegible();
		}
		else{
			registro = (PctFranquiciaElegible) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq")).equals("")){
				registro.setCodpctfranquiciaeleg(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "franq"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setValor(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valor"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDespctfranquiciaeleg(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
		}
		return registro;
	}
}
