package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;

public class TerminosXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\Terminos.xml";
		args[1] = "D:\\borrar\\Terminos.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TerminosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TerminosXMLParser parser = new TerminosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Terminos " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Terminos " + e.getMessage());
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
		Termino registro = (Termino)reg;
		String sql = "";
		sql += registro.getId().getCodprovincia() + ";" + registro.getId().getCodtermino() + ";";
		sql += registro.getId().getSubtermino() + ";" + registro.getId().getCodcomarca() + ";" + registro.getNomtermino() + ";";
		sql += StringUtils.nullToString(registro.getCodpostal()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Termino registro;
		if (actual == null){
			registro = new Termino();
		}
		else{
			registro = (Termino) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			TerminoId idActual = new TerminoId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("")){
				idActual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("")){
				idActual.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("")){
				idActual.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idActual.setSubtermino(new Character(' '));
			}
			else{
				idActual.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			registro.setId(idActual);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "denom")).equals("")){
				registro.setNomtermino(parser.getAttributeValue(null, "denom"));
			}
		}
		return registro;
	}
}
