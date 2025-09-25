package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.EspecieId;

public class EspecieXMLParser extends GenericXMLParser {

	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\Especie.xml";
		args[1] = "D:\\borrar\\Especie.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + EspecieXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			EspecieXMLParser parser = new EspecieXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);

			System.out.println("arg 0: " + args[0]);
			System.out.println("arg 1: " + args[1]);
			System.out.println("arg 2long: " + new Long(args[2]));
			System.out.println("arg 3: " + args[3]);
			
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Especie " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Especie " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		Especie registro = (Especie)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodespecie() + ";";
		if (registro.getDescripcion() != null)
			sql += registro.getDescripcion() + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Especie registro;
		if (actual == null){
			registro = new Especie();
		}
		else{
			registro = (Especie) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			EspecieId especieId = new EspecieId();
			especieId.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){				
				especieId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp"))));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
			registro.setId(especieId);
		}
		return registro;
	}
}
