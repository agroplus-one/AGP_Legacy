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
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModulo;
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModuloId;

public class FactoresIncluidosConceptoPpalModXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\FactoresIncluidosConceptoPrincipalModulo.xml";
		args[1] = "D:\\borrar\\FactoresIncluidosConceptoPrincipalModulo.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + FactoresIncluidosConceptoPpalModXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FactoresIncluidosConceptoPpalModXMLParser parser = new FactoresIncluidosConceptoPpalModXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de FactoresIncluidosConceptoPpalMod " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de FactoresIncluidosConceptoPpalMod " + e.getMessage());
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
		FactoresIncluidosConceptoPrincipalModulo registro = (FactoresIncluidosConceptoPrincipalModulo)reg;
		String sql = "";
		sql += registro.getId().getCpm() + ";" + registro.getId().getCodcpto() + ";" + registro.getId().getValorCpto() + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		FactoresIncluidosConceptoPrincipalModulo registro;
		if (actual == null){
			registro = new FactoresIncluidosConceptoPrincipalModulo();
		}
		else{
			registro = (FactoresIncluidosConceptoPrincipalModulo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			FactoresIncluidosConceptoPrincipalModuloId factoresId = new FactoresIncluidosConceptoPrincipalModuloId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCpto")).equals("")){				
				factoresId.setValorCpto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "valCpto"))));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){				
				factoresId.setCodcpto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto"))));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){				
				factoresId.setCpm(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "cPMod"))));
			}		
			registro.setId(factoresId);
		}
		return registro;
	}
}
