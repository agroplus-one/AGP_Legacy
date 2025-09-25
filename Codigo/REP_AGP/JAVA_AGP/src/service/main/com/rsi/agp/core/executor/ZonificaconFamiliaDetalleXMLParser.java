package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ZonificacionFamiliaDetalle;
import com.rsi.agp.dao.tables.cgen.ZonificacionFamiliaDetalleId;

public class ZonificaconFamiliaDetalleXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\LineasFamiliasZonificaciones.xml";
		args[1] = "D:\\borrar\\LineasFamiliasZonificaciones.csv";
		args[2] = "1";
		args[3] = "DD/MM/YYYY";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ZonificaconFamiliaDetalleXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			ZonificaconFamiliaDetalleXMLParser parser = new ZonificaconFamiliaDetalleXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Organismos " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Organismos " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			//e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		ZonificacionFamiliaDetalle registro = (ZonificacionFamiliaDetalle)reg;
		String sql = "";
		sql += registro.getId().getCodfamilia() + ";" + StringUtils.nullToString(registro.getId().getCodlinea()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		ZonificacionFamiliaDetalle registro;
		if (actual == null){
			registro = new ZonificacionFamiliaDetalle();
		}
		else{
			registro = (ZonificacionFamiliaDetalle) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			ZonificacionFamiliaDetalleId idActual = new ZonificacionFamiliaDetalleId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "familia")).equals("")){
				idActual.setCodfamilia(StringUtils.nullToString(parser.getAttributeValue(null, "familia")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "linea")).equals("")){
				idActual.setCodlinea(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "linea").trim())));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
