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
import com.rsi.agp.dao.tables.cgen.ProvinciaOrganismo;
import com.rsi.agp.dao.tables.cgen.ProvinciaOrganismoId;

public class ProvinciasOrganismoXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(ProvinciasOrganismoXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\ProvinciasOrganismos.xml";
		args[1] = "D:\\borrar\\ProvinciasOrganismos.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ProvinciasOrganismoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			ProvinciasOrganismoXMLParser parser = new ProvinciasOrganismoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Provincias Organismos " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Provincias Organismos " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		ProvinciaOrganismo registro = (ProvinciaOrganismo)reg;
		String sql = "";
		sql += registro.getId().getCodorganismo() + ";" + registro.getId().getCodprovincia() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		ProvinciaOrganismo registro;
		if (actual == null){
			registro = new ProvinciaOrganismo();
		}
		else{
			registro = (ProvinciaOrganismo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			ProvinciaOrganismoId idActual = new ProvinciaOrganismoId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOrg").trim()).equals("")){
				idActual.setCodorganismo(new Character(parser.getAttributeValue(null, "codOrg").trim().charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "provOrg").trim()).equals("")){
				idActual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "provOrg").trim()));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
