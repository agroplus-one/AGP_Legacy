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
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModuloId;

public class RiesgosCubiertosModuloXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(RiesgosCubiertosModuloXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\RiesgosCubiertosModulo.xml";
		args[1] = "D:\\borrar\\RiesgosCubiertosModulo.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ RiesgosCubiertosModuloXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RiesgosCubiertosModuloXMLParser parser = new RiesgosCubiertosModuloXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Riesgos cubiertos módulo " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Riesgos cubiertos módulo " + e.getMessage());
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
		RiesgoCubiertoModulo registro = (RiesgoCubiertoModulo)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getFilamodulo() + ";" + registro.getConceptoPpalModulo().getCodconceptoppalmod() + ";";
		sql += registro.getRiesgoCubierto().getId().getCodriesgocubierto() + ";" + registro.getElegible() + ";";
		sql += StringUtils.nullToString(registro.getNiveleccion()) + ";" + StringUtils.nullToString(registro.getFichvinculacionexterna()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RiesgoCubiertoModulo registro;
		if (actual == null){
			registro = new RiesgoCubiertoModulo();
		}
		else{
			registro = (RiesgoCubiertoModulo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RiesgoCubiertoModuloId idActual = new RiesgoCubiertoModuloId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fMod")).equals("")){
				idActual.setFilamodulo(new BigDecimal(parser.getAttributeValue(null, "fMod")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				ConceptoPpalModulo cpm = new ConceptoPpalModulo();
				cpm.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpm);
			}
			else{
				//para evitar excepciones
				registro.setConceptoPpalModulo(new ConceptoPpalModulo());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				RiesgoCubiertoId rcId = new RiesgoCubiertoId();
				rcId.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				rc.setId(rcId);
				registro.setRiesgoCubierto(rc);
			}
			else{
				//para evitar excepciones
				registro.setRiesgoCubierto(new RiesgoCubierto());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "eleg")).equals("")){
				registro.setElegible(new Character(parser.getAttributeValue(null, "eleg").trim().charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivElec")).trim().equals("")){
				registro.setNiveleccion(new Character(parser.getAttributeValue(null, "nivElec").trim().charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numTab")).equals("")){
				registro.setFichvinculacionexterna(new BigDecimal(parser.getAttributeValue(null, "numTab").trim()));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
