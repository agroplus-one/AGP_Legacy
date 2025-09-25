package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.CondicionesComisiones;
import com.rsi.agp.dao.tables.cpl.CondicionesComisionesId;

public class CondicionesParticularesAplicadasComisionesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\CondicionesParticularesAplicadasComisiones.xml";
		args[1] = "D:\\borrar\\CondicionesParticularesAplicadasComisiones.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ CondicionesParticularesAplicadasComisionesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CondicionesParticularesAplicadasComisionesXMLParser parser = new CondicionesParticularesAplicadasComisionesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de máscara de límites de rendimiento " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de máscara de límites de rendimiento " + e.getMessage());
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
		CondicionesComisiones registro = (CondicionesComisiones)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodgrupotasa() + ";" + registro.getId().getCodprovincia() + ";" + registro.getId().getCodcomarca() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += registro.getId().getCondparticular() + ";" + registro.getId().getPctcomisindiv() + ";";
		sql += registro.getId().getPctcomisaplic() + ";" + registro.getId().getCodcultivo() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		CondicionesComisiones registro;
		if (actual == null){
			registro = new CondicionesComisiones();
		}
		else{
			registro = (CondicionesComisiones) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			CondicionesComisionesId idActual = new CondicionesComisionesId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				idActual.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				idActual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				idActual.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				idActual.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				idActual.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				idActual.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				idActual.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idActual.setSubtermino(new Character('-'));
			}
			else{
				idActual.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "condPart")).equals("")){
				idActual.setCondparticular(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "condPart"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "comIndv")).equals("")){
				idActual.setPctcomisindiv(new BigDecimal(parser.getAttributeValue(null, "comIndv")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "comAplic")).equals("")){
				idActual.setPctcomisaplic(new BigDecimal(parser.getAttributeValue(null, "comAplic")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				idActual.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
