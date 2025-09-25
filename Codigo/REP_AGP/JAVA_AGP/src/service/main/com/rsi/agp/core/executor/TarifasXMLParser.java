package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.Tarifa;
import com.rsi.agp.dao.tables.cpl.TarifaId;

public class TarifasXMLParser extends GenericXMLParser {
	
	BigDecimal plan;
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Tarifas.xml";
		args[1] = "D:\\borrar\\Tarifas.csv";
		args[2] = "202";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TarifasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TarifasXMLParser parser = new TarifasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de tarifas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de tarifas " + e.getMessage());
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
		Tarifa registro = (Tarifa)reg;
		String sql = "";
		sql += registro.getId().getCodplan() + ";" + registro.getId().getCodgrupotasa() + ";";
		sql += registro.getId().getCodprovincia() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += StringUtils.nullToString(registro.getPctreaseguroconsorcio()) + ";";
		sql += StringUtils.nullToString(registro.getPctrecargoconsorcio()) + ";";
		sql += StringUtils.nullToString(registro.getTasacoste()) + ";";
		sql += StringUtils.nullToString(registro.getTasaboe()) + ";";
		sql += registro.getId().getCodcomarca() + ";";
		sql += StringUtils.nullToString(registro.getTasacomercialbase()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Tarifa registro;
		if (actual == null){
			registro = new Tarifa();
		}
		else{
			registro = (Tarifa) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			TarifaId idTarifa = new TarifaId();
			idTarifa.setCodplan(plan);
			idTarifa.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			idTarifa.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			idTarifa.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			idTarifa.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idTarifa.setSubtermino(new Character('-'));
			}
			else{
				idTarifa.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			registro.setId(idTarifa);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "raseCons")).trim().equals("")){
				registro.setPctreaseguroconsorcio(new BigDecimal(parser.getAttributeValue(null, "raseCons")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "rcgoCons")).trim().equals("")){
				registro.setPctrecargoconsorcio(new BigDecimal(parser.getAttributeValue(null, "rcgoCons")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaCoste")).trim().equals("")){
				registro.setTasacoste(new BigDecimal(parser.getAttributeValue(null, "tasaCoste")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaBOE")).trim().equals("")){
				registro.setTasaboe(new BigDecimal(parser.getAttributeValue(null, "tasaBOE")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaComBas")).trim().equals("")){
				registro.setTasacomercialbase(new BigDecimal(parser.getAttributeValue(null, "tasaComBas")));
			}
		}
		else if (GenericXMLParser.TAG_CONDICIONADO_PLAN.equals(tag)){
			plan = new BigDecimal(parser.getAttributeValue(null, "plan"));
		}
		
		return registro;
	}
}
