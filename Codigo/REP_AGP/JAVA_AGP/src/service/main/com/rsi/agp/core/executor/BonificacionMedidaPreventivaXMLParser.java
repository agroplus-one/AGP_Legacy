package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.BonificacionMedidaPreventiva;
import com.rsi.agp.dao.tables.cpl.BonificacionMedidaPreventivaId;

public class BonificacionMedidaPreventivaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\BonificacionMedidaPreventiva.xml";
		args[1] = "D:\\borrar\\BonificacionMedidaPreventiva.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + BonificacionMedidaPreventivaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			BonificacionMedidaPreventivaXMLParser parser = new BonificacionMedidaPreventivaXMLParser();
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
		BonificacionMedidaPreventiva registro = (BonificacionMedidaPreventiva)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getCodmedidapreventiva() + ";" + StringUtils.nullToString(registro.getPctbonificacion()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		BonificacionMedidaPreventiva registro;
		if (actual == null){
			registro = new BonificacionMedidaPreventiva();
		}
		else{
			registro = (BonificacionMedidaPreventiva) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			BonificacionMedidaPreventivaId idActual = new BonificacionMedidaPreventivaId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod")).trim());
			}

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "medPrev")).equals("")){
				idActual.setCodmedidapreventiva(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "medPrev"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "bon")).equals("")){
				registro.setPctbonificacion(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "bon").trim())));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
