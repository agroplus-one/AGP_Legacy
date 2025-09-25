package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.BonificacionPorRenovacion;
import com.rsi.agp.dao.tables.cpl.BonificacionPorRenovacionId;
import com.rsi.agp.dao.tables.cpl.Modulo;

public class BonificacionPorRenovacionXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\BonificacionPorRenovacion.xml";
		args[1] = "D:\\borrar\\BonificacionPorRenovacion.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ BonificacionPorRenovacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			BonificacionPorRenovacionXMLParser parser = new BonificacionPorRenovacionXMLParser();
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
		BonificacionPorRenovacion registro = (BonificacionPorRenovacion)reg;
		String sql = "";
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		sql += registro.getModulo().getId().getCodmodulo() + ";";
		sql += StringUtils.nullToString(registro.getNifcif()) + ";" + StringUtils.nullToString(registro.getPctbonificacion()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		BonificacionPorRenovacion registro;
		if (actual == null){
			registro = new BonificacionPorRenovacion();
		}
		else{
			registro = (BonificacionPorRenovacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			BonificacionPorRenovacionId idActual = new BonificacionPorRenovacionId();
			
			idActual.setLineaseguroid(lineaseguroid);
			idActual.setId(new BigDecimal(id));
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo m = new Modulo();
				m.getId().setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod")).trim());
				m.getId().setLineaseguroid(lineaseguroid);
				registro.setModulo(m);
			}

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).equals("")){
				registro.setNifcif(StringUtils.nullToString(parser.getAttributeValue(null, "nif")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "bon")).equals("")){
				registro.setPctbonificacion(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "bon").trim())));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
