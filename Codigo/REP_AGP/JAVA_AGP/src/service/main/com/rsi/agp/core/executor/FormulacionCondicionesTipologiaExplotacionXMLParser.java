package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.FormulacionCondicionesTipologiaExplotacion;
import com.rsi.agp.dao.tables.cpl.FormulacionCondicionesTipologiaExplotacionId;

public class FormulacionCondicionesTipologiaExplotacionXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\FormulacionCondicionesTipologiaExplotacion.xml";
		args[1] = "D:\\borrar\\FormulacionCondicionesTipologiaExplotacion.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + FormulacionCondicionesTipologiaExplotacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FormulacionCondicionesTipologiaExplotacionXMLParser parser = new FormulacionCondicionesTipologiaExplotacionXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Fechas de recolección " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Fechas de recolección " + e.getMessage());
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
		FormulacionCondicionesTipologiaExplotacion registro = (FormulacionCondicionesTipologiaExplotacion)reg;
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodndtipologia() + ";" + registro.getId().getCodatributo() + ";";
		sql += StringUtils.nullToString(registro.getValorattdesde()) + ";";
		sql += StringUtils.nullToString(registro.getValoratthasta()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		FormulacionCondicionesTipologiaExplotacion registro;
		if (actual == null){
			registro = new FormulacionCondicionesTipologiaExplotacion();
		}
		else{
			registro = (FormulacionCondicionesTipologiaExplotacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			FormulacionCondicionesTipologiaExplotacionId idActual = new FormulacionCondicionesTipologiaExplotacionId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "condTipol")).equals("")){
				idActual.setCodndtipologia(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "condTipol"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codAtri")).equals("")){
				idActual.setCodatributo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codAtri"))));
			}
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valAtriD")).equals("")){
				registro.setValorattdesde(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valAtriD"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valAtriH")).equals("")){
				registro.setValoratthasta(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valAtriH"))));
			}
		}
		return registro;
	}
}
