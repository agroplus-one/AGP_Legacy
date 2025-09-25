package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesST;
import com.rsi.agp.dao.tables.cpl.RelEspeciesSCEspeciesSTId;

public class RelEspeciesSCEspeciesSTXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(RelEspeciesSCEspeciesSTXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\RelacionEspeciesSeguroCrecienteEspeciesSeguroTradicional.xml";
		args[1] = "D:\\borrar\\RelacionEspeciesSeguroCrecienteEspeciesSeguroTradicional.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RelEspeciesSCEspeciesSTXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RelEspeciesSCEspeciesSTXMLParser parser = new RelEspeciesSCEspeciesSTXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de relacion de especies SC y especies ST " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de relacion de especies SC y especies ST " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		RelEspeciesSCEspeciesST registro = (RelEspeciesSCEspeciesST)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getCodcultivo() + ";" + registro.getId().getCodvariedad() + ";";
		sql += registro.getId().getCodplan() + ";" + registro.getId().getCodlinea() + ";";
		sql += registro.getId().getCodcultivost() + ";" + registro.getId().getCodvariedadst() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RelEspeciesSCEspeciesST registro;
		if (actual == null){
			registro = new RelEspeciesSCEspeciesST();
		}
		else{
			registro = (RelEspeciesSCEspeciesST) actual;
		}
		RelEspeciesSCEspeciesSTId idActual;
		if (this.getTagPrincipal().equals(tag)){
			if (registro.getId().getLineaseguroid() != null || registro.getId().getCodplan() != null){
				idActual = registro.getId();
			}
			else{
				idActual = new RelEspeciesSCEspeciesSTId();
			}
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul").trim()).equals("")){
				idActual.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var").trim()).equals("")){
				idActual.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var").trim()));
			}
			
			registro.setId(idActual);
		}
		else if (GenericXMLParser.TAG_DATOS_TRADICIONALES.equals(tag)){
			if (registro.getId().getLineaseguroid() != null || registro.getId().getCodplan() != null){
				idActual = registro.getId();
			}
			else{
				idActual = new RelEspeciesSCEspeciesSTId();
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "plan").trim()).equals("")){
				idActual.setCodplan(new BigDecimal(parser.getAttributeValue(null, "plan").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "linea").trim()).equals("")){
				idActual.setCodlinea(new BigDecimal(parser.getAttributeValue(null, "linea").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul").trim()).equals("")){
				idActual.setCodcultivost(new BigDecimal(parser.getAttributeValue(null, "cul").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var").trim()).equals("")){
				idActual.setCodvariedadst(new BigDecimal(parser.getAttributeValue(null, "var").trim()));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
