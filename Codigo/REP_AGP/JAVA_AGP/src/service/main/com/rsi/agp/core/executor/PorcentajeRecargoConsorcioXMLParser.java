package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.PctRecargoConsorcio;
import com.rsi.agp.dao.tables.cpl.PctRecargoConsorcioId;


public class PorcentajeRecargoConsorcioXMLParser extends GenericXMLParser {
	BigDecimal plan;
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\PctRecargoConsorcio.xml";
		args[1] = "D:\\borrar\\PctRecargoConsorcio.csv";
		args[2] = "202";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PorcentajeRecargoConsorcioXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PorcentajeRecargoConsorcioXMLParser parser = new PorcentajeRecargoConsorcioXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de PctRecargoConsorcio " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de PctRecargoConsorcio " + e.getMessage());
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
		PctRecargoConsorcio registro = (PctRecargoConsorcio)reg;
		String sql = "";
		sql += registro.getId().getPlan() + ";" + registro.getId().getGrupoTasa() + ";";
		sql += StringUtils.nullToString(registro.getRecargoConsorcio()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PctRecargoConsorcio registro;
		if (actual == null){
			registro = new PctRecargoConsorcio();
		}
		else{
			registro = (PctRecargoConsorcio) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PctRecargoConsorcioId prcId = new PctRecargoConsorcioId();
			
			prcId.setGrupoTasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			prcId.setPlan(plan);
			
			registro.setId(prcId);
			registro.setRecargoConsorcio(new BigDecimal(parser.getAttributeValue(null, "rcgoCons")));
			
		}else if (GenericXMLParser.TAG_CONDICIONADO_PLAN.equals(tag)){
			plan = new BigDecimal(parser.getAttributeValue(null, "plan"));
		}
		return registro;
	}
	
}