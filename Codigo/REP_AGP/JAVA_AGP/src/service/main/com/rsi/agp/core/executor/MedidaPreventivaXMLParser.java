package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.MedidaPreventiva;

public class MedidaPreventivaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\MedidaPreventiva.xml";
		args[1] = "D:\\borrar\\MedidaPreventiva.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidaPreventivaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidaPreventivaXMLParser parser = new MedidaPreventivaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Medidas preventivas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Medidas preventivas " + e.getMessage());
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
		MedidaPreventiva registro = (MedidaPreventiva)reg;
		String sql = "";
		sql += registro.getCodmedidapreventiva() + ";" + registro.getCodriesgotarificable() + ";" + registro.getDesmedidapreventiva() + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MedidaPreventiva registro;
		if (actual == null){
			registro = new MedidaPreventiva();
		}
		else{
			registro = (MedidaPreventiva) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "medPrev")).equals("")){
				registro.setCodmedidapreventiva(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "medPrev"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRTar")).equals("")){
				registro.setCodriesgotarificable(StringUtils.nullToString(parser.getAttributeValue(null, "codRTar")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDesmedidapreventiva(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
		}
		return registro;
	}
}
