package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class VariedadesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\Variedades.xml";
		args[1] = "D:\\borrar\\Variedades.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + VariedadesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			VariedadesXMLParser parser = new VariedadesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de variedades " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de variedades " + e.getMessage());
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
		Variedad registro = (Variedad)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodcultivo() + ";";
		sql += registro.getId().getCodvariedad() + ";" + StringUtils.nullToString(registro.getDesvariedad()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Variedad registro;
		if (actual == null){
			registro = new Variedad();
		}
		else{
			registro = (Variedad) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			VariedadId idActual = new VariedadId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul").trim()).equals("")){
				idActual.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var").trim()).equals("")){
				idActual.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()).equals("")){
				registro.setDesvariedad(parser.getAttributeValue(null, "desc").trim());
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
