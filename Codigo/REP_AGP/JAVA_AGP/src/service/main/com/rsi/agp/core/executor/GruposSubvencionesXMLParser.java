package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.GrupoSubvenciones;
import com.rsi.agp.dao.tables.cpl.GrupoSubvencionesId;

public class GruposSubvencionesXMLParser extends GenericXMLParser {
	
	BigDecimal plan;
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\GruposSubvenciones.xml";
		args[1] = "D:\\borrar\\GruposSubvenciones.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + GruposSubvencionesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			GruposSubvencionesXMLParser parser = new GruposSubvencionesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Grupos de subvenciones " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Grupos de subvenciones " + e.getMessage());
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
		GrupoSubvenciones registro = (GrupoSubvenciones)reg;
		String sql = "";
		sql += registro.getId().getPlan() + ";" + registro.getId().getGruposubv() + ";";
		sql += StringUtils.nullToString(registro.getDescripcion()) + ";";
		sql += registro.getCantidadsubvacumulables() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		GrupoSubvenciones registro;
		if (actual == null){
			registro = new GrupoSubvenciones();
		}
		else{
			registro = (GrupoSubvenciones) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GrupoSubvencionesId idActual = new GrupoSubvencionesId();
			idActual.setPlan(plan);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "gruSubv")).equals("")){
				idActual.setGruposubv(new BigDecimal(parser.getAttributeValue(null, "gruSubv")));
			}
			
			registro.setId(idActual);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desGru")).equals("")){
				registro.setDescripcion(parser.getAttributeValue(null, "desGru"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "canMaxSubvAcum")).equals("")){
				registro.setCantidadsubvacumulables(new BigDecimal(parser.getAttributeValue(null, "canMaxSubvAcum")));
			}
		}
		else if (GenericXMLParser.TAG_CONDICIONADO_PLAN.equals(tag)){
			plan = new BigDecimal(parser.getAttributeValue(null, "plan"));
		}
		return registro;
	}
}
