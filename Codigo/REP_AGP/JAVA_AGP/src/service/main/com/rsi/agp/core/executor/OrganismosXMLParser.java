package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Organismo;

public class OrganismosXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(OrganismosXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Organismos.xml";
		args[1] = "D:\\borrar\\Organismos.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ OrganismosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			OrganismosXMLParser parser = new OrganismosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Organismos " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Organismos " + e.getMessage());
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
		Organismo registro = (Organismo)reg;
		String sql = "";
		sql += registro.getCodorganismo() + ";" + StringUtils.nullToString(registro.getDesorganismo()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Organismo registro;
		if (actual == null){
			registro = new Organismo();
		}
		else{
			registro = (Organismo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).equals("")){
				registro.setCodorganismo(new Character(StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDesorganismo(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
		}
		return registro;
	}
}
