package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.PrimaRiesgo;
import com.rsi.agp.dao.tables.cpl.PrimaRiesgoId;

public class PrimasRiesgoXMLParser extends GenericXMLParser {
	
	BigDecimal plan;
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\PrimasRiesgo.xml";
		args[1] = "D:\\borrar\\PrimasRiesgo.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ PrimasRiesgoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PrimasRiesgoXMLParser parser = new PrimasRiesgoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de máscara de primas de riesgo " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de máscara de primas de riesgo " + e.getMessage());
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
		PrimaRiesgo registro = (PrimaRiesgo)reg;
		String sql = "";
		sql += registro.getId().getCodplan() + ";" + registro.getId().getCodgrupotasa() + ";";
		sql += registro.getId().getCodriesgotarificable() + ";" + registro.getId().getCodprovincia() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += StringUtils.nullToString(registro.getTasaprimacomercial()) + ";";
		sql += registro.getId().getCodcomarca()+ ";";
		sql += registro.getTasacombaseriesgo();
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PrimaRiesgo registro;
		if (actual == null){
			registro = new PrimaRiesgo();
		}
		else{
			registro = (PrimaRiesgo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PrimaRiesgoId idPrima = new PrimaRiesgoId();
			idPrima.setCodplan(plan);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				idPrima.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRTar")).equals("")){
				idPrima.setCodriesgotarificable(parser.getAttributeValue(null, "codRTar"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				idPrima.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				idPrima.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				idPrima.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				idPrima.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				idPrima.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				idPrima.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idPrima.setSubtermino(new Character('-'));
			}
			else{
				idPrima.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			registro.setId(idPrima);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaPCom")).equals("")){
				registro.setTasaprimacomercial(new BigDecimal(parser.getAttributeValue(null, "tasaPCom")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaComBasRie")).equals("")){
				registro.setTasacombaseriesgo(new BigDecimal(parser.getAttributeValue(null, "tasaComBasRie")));
			}
		}
		else if (GenericXMLParser.TAG_CONDICIONADO_PLAN.equals(tag)){
			plan = new BigDecimal(parser.getAttributeValue(null, "plan"));
		}
		return registro;
	}
}
