package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.BonificacionRecargo;


public class BonificacionesRecargosXMLParser extends GenericXMLParser {
	
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\BonificacionRecargo.xml";
		args[1] = "D:\\borrar\\BonificacionRecargo.csv";
		args[2] = "202";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + BonificacionesRecargosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			BonificacionesRecargosXMLParser parser = new BonificacionesRecargosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de BonificacionRecargo " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de BonificacionRecargo " + e.getMessage());
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
		BonificacionRecargo registro = (BonificacionRecargo)reg;
		String sql = "";
		sql += registro.getCodBonRec() + ";" + StringUtils.nullToString(registro.getTipBonRec()) + ";";
		sql += StringUtils.nullToString(registro.getDescripcion()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		BonificacionRecargo registro;
		if (actual == null){
			registro = new BonificacionRecargo();
		}
		else{
			registro = (BonificacionRecargo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			registro.setCodBonRec(new Long(parser.getAttributeValue(null, "codBonRec")));
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipBonRec")).trim().equals("")){
				registro.setTipBonRec(new Character(parser.getAttributeValue(null, "tipBonRec").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).trim().equals("")){
				registro.setDescripcion(parser.getAttributeValue(null, "desc"));
			}
		}
		return registro;
	}

}
