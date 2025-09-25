package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanadoId;

public class RiesgoCubiertoModuloGanadoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\RiesgosCubiertosModulo.xml";
		args[1] = "D:\\borrar\\RiesgosCubiertosModulo.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RiesgoCubiertoModuloGanadoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RiesgoCubiertoModuloGanadoXMLParser parser = new RiesgoCubiertoModuloGanadoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de RiesgoCubiertoModuloGanado " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de RiesgoCubiertoModuloGanado " + e.getMessage());
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
		RiesgoCubiertoModuloGanado registro = (RiesgoCubiertoModuloGanado)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";" + registro.getId().getFilamodulo() + ";";		
		sql += registro.getConceptoPpalModulo().getCodconceptoppalmod() + ";";		
		sql += registro.getRiesgoCubierto().getId().getCodriesgocubierto() + ";" + registro.getElegible() + ";";
		sql += registro.getNiveleccion() + ";" + registro.getTipogarantiamodulo() + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RiesgoCubiertoModuloGanado registro;
		if (actual == null){
			registro = new RiesgoCubiertoModuloGanado();
		}
		else{
			registro = (RiesgoCubiertoModuloGanado) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RiesgoCubiertoModuloGanadoId riesgoId = new RiesgoCubiertoModuloGanadoId();
			ConceptoPpalModulo cPpalMod = new ConceptoPpalModulo();
			RiesgoCubierto ri = new RiesgoCubierto(); 
			
			riesgoId.setLineaseguroid(lineaseguroid);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				riesgoId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fMod")).equals("")){
				riesgoId.setFilamodulo(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "fMod").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				cPpalMod.setCodconceptoppalmod(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "cPMod").trim())));
			}
			registro.setConceptoPpalModulo(cPpalMod);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				ri.getId().setCodriesgocubierto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codRCub").trim())));
			}
			registro.setRiesgoCubierto(ri);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "eleg")).equals("")){
				registro.setElegible(new Character(parser.getAttributeValue(null, "eleg").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivElec")).equals("")){
				registro.setNiveleccion(new Character((parser.getAttributeValue(null, "nivElec")).charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipGarant")).equals("")){
				registro.setTipogarantiamodulo(new Character(parser.getAttributeValue(null, "tipGarant").charAt(0)));
			}

			registro.setId(riesgoId);

		}
		return registro;
	}
}
