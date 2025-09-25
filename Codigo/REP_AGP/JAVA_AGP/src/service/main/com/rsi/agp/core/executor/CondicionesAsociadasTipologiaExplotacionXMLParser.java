package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.CondicionesAsociadasTipologiaExplotacion;
import com.rsi.agp.dao.tables.cpl.CondicionesAsociadasTipologiaExplotacionId;

public class CondicionesAsociadasTipologiaExplotacionXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\CondicionesAsociadasTipologiaExplotacion.xml";
		args[1] = "D:\\borrar\\CondicionesAsociadasTipologiaExplotacion.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CondicionesAsociadasTipologiaExplotacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CondicionesAsociadasTipologiaExplotacionXMLParser parser = new CondicionesAsociadasTipologiaExplotacionXMLParser();
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
		CondicionesAsociadasTipologiaExplotacion registro = (CondicionesAsociadasTipologiaExplotacion)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodcaractexplotacion() + ";";
		sql += registro.getId().getCodndtipologia() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		CondicionesAsociadasTipologiaExplotacion registro;
		if (actual == null){
			registro = new CondicionesAsociadasTipologiaExplotacion();
		}
		else{
			registro = (CondicionesAsociadasTipologiaExplotacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			CondicionesAsociadasTipologiaExplotacionId idActual = new CondicionesAsociadasTipologiaExplotacionId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "carExpl").trim()).equals("")){
				idActual.setCodcaractexplotacion(new BigDecimal(parser.getAttributeValue(null, "carExpl").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "condTipol").trim()).equals("")){
				idActual.setCodndtipologia(new BigDecimal(parser.getAttributeValue(null, "condTipol").trim()));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
