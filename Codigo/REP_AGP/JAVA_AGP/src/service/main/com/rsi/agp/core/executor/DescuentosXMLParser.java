package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.Descuento;
import com.rsi.agp.dao.tables.cpl.DescuentoId;

public class DescuentosXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Descuentos.xml";
		args[1] = "D:\\borrar\\Descuentos.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + DescuentosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			DescuentosXMLParser parser = new DescuentosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de cultivos " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de cultivos " + e.getMessage());
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
		Descuento registro = (Descuento)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += StringUtils.nullToString(registro.getPctdtocontrcolect()) + ";";
		sql += StringUtils.nullToString(registro.getPctdtoventanillaindiv()) + ";";
		sql += StringUtils.nullToString(registro.getPctdtoventanillacolect()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Descuento registro;
		if (actual == null){
			registro = new Descuento();
		}
		else{
			registro = (Descuento) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			DescuentoId idActual = new DescuentoId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dtoContCol")).equals("")){
				registro.setPctdtocontrcolect(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "dtoContCol"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dtoVentInd")).equals("")){
				registro.setPctdtoventanillaindiv(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "dtoVentInd"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dtoVentCol")).equals("")){
				registro.setPctdtoventanillacolect(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "dtoVentCol"))));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
