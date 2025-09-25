package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.TablaBonus;
import com.rsi.agp.dao.tables.cpl.TablaBonusId;

public class TablaBonusXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\TablaExternaBonus.xml";
		args[1] = "D:\\borrar\\TablaExternaBonus.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TablaBonusXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TablaBonusXMLParser parser = new TablaBonusXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Tabla Bonus " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Tabla Bonus " + e.getMessage());
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
		TablaBonus registro = (TablaBonus)reg;
		
		String sql = "";
		
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodpctfranquiciaeleg() + ";";
		sql += registro.getId().getCodhistorialasegurado() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		TablaBonus registro;
		if (actual == null){
			registro = new TablaBonus();
		}
		else{
			registro = (TablaBonus) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			TablaBonusId idActual = new TablaBonusId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq")).equals("")){
				idActual.setCodpctfranquiciaeleg(new BigDecimal(parser.getAttributeValue(null, "franq")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "histAseg")).equals("")){
				idActual.setCodhistorialasegurado(new BigDecimal(parser.getAttributeValue(null, "histAseg")));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
