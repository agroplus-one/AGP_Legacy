package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;

public class RiesgosCubiertosXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\RiesgosCubiertos.xml";
		args[1] = "D:\\borrar\\RiesgosCubiertos.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RiesgosCubiertosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RiesgosCubiertosXMLParser parser = new RiesgosCubiertosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de riesgos cubiertos " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de riesgos cubiertos " + e.getMessage());
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
		RiesgoCubierto registro = (RiesgoCubierto)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getCodriesgocubierto() + ";" + StringUtils.nullToString(registro.getDesriesgocubierto()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RiesgoCubierto registro;
		if (actual == null){
			registro = new RiesgoCubierto();
		}
		else{
			registro = (RiesgoCubierto) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RiesgoCubiertoId idActual = new RiesgoCubiertoId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub").trim()).equals("")){
				idActual.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()).equals("")){
				registro.setDesriesgocubierto(parser.getAttributeValue(null, "desc").trim());
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
