package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;


import com.rsi.agp.dao.tables.cpl.GrupoVarietales;
import com.rsi.agp.dao.tables.cpl.GrupoVarietalesId;


public class GruposVarietalesXMLParser extends GenericXMLParser {
	
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\GruposVarietales.xml";
		args[1] = "D:\\borrar\\GruposVarietales.csv";
		args[2] = "202";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + GruposVarietalesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			GruposVarietalesXMLParser parser = new GruposVarietalesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de GrupoVarietales " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de GrupoVarietales " + e.getMessage());
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
		GrupoVarietales registro = (GrupoVarietales)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getGrupovarietal() + ";";
		sql += registro.getId().getCodcultivo() + ";" + registro.getId().getCodvariedad() + ";";
		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		GrupoVarietales registro;
		if (actual == null){
			registro = new GrupoVarietales();
		}
		else{
			registro = (GrupoVarietales) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			
			GrupoVarietalesId gv = new GrupoVarietalesId();
			gv.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			gv.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
			gv.setGrupovarietal(new BigDecimal(parser.getAttributeValue(null, "gruVar")));
			gv.setLineaseguroid(lineaseguroid);
			
			registro.setId(gv);
		}
		return registro;
	}

}