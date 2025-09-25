package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.DanioCubierto;

public class DanioCubiertoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\DanioCubierto.xml";
		args[1] = "D:\\borrar\\DanioCubierto.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ DanioCubiertoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			DanioCubiertoXMLParser parser = new DanioCubiertoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Danio Cubierto " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Danio Cubierto " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		DanioCubierto registro = (DanioCubierto)reg;
		String sql = "";
		sql += registro.getCoddaniocubierto() + ";" + StringUtils.nullToString(registro.getDesdaniocubierto()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		DanioCubierto registro;
		if (actual == null){
			registro = new DanioCubierto();
		}
		else{
			registro = (DanioCubierto) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codDnCub")).equals("")){
				registro.setCoddaniocubierto(new Character(StringUtils.nullToString(parser.getAttributeValue(null, "codDnCub")).charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desDnCub")).equals("")){
				registro.setDesdaniocubierto(StringUtils.nullToString(parser.getAttributeValue(null, "desDnCub").trim()));
			}
		}
		return registro;
	}
}
