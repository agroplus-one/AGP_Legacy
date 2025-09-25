package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.MaterialEstructura;

public class MaterialEstructuraXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\MaterialEstructura.xml";
		args[1] = "D:\\borrar\\MaterialEstructura.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MaterialEstructuraXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MaterialEstructuraXMLParser parser = new MaterialEstructuraXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Material Estructura " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Material Estructura " + e.getMessage());
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
		MaterialEstructura registro = (MaterialEstructura)reg;
		String sql = "";
		sql += registro.getCodmaterialestructura() + ";" + StringUtils.nullToString(registro.getDescripcion()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MaterialEstructura registro;
		if (actual == null){
			registro = new MaterialEstructura();
		}
		else{
			registro = (MaterialEstructura) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "matEstr")).equals("")){
				registro.setCodmaterialestructura(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codOrg"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
		}
		return registro;
	}
}
