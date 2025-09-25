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
import com.rsi.agp.dao.tables.cgen.AdaptacionRiesgo;
import com.rsi.agp.dao.tables.cgen.AdaptacionRiesgoId;
import com.rsi.agp.dao.tables.cgen.BancoId;
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModulo;
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModuloId;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;

public class AdaptacionRiesgoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\PorcentajesAdaptacionRiesgo.xml";
		args[1] = "D:\\borrar\\PorcentajesAdaptacionRiesgo.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + AdaptacionRiesgoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			AdaptacionRiesgoXMLParser parser = new AdaptacionRiesgoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de AdaptacionRiesgo " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de AdaptacionRiesgo " + e.getMessage());
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
		AdaptacionRiesgo registro = (AdaptacionRiesgo)reg;
		String sql = "";
		sql += registro.getId().getCodcpto() + ";" + registro.getId().getValorCpto() + ";" +
			   registro.getDescripcion() + ";" + registro.getTipoBonifRecargo() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		AdaptacionRiesgo registro;
		if (actual == null){
			registro = new AdaptacionRiesgo();
		}
		else{
			registro = (AdaptacionRiesgo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			AdaptacionRiesgoId adaptacionId = new AdaptacionRiesgoId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCpto")).equals("")){				
				adaptacionId.setValorCpto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valCpto"))));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){				
				adaptacionId.setCodcpto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto"))));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipBonRec")).equals("")){
				registro.setTipoBonifRecargo(new Character(parser.getAttributeValue(null, "tipBonRec").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}		
			registro.setId(adaptacionId);
		}
		return registro;
	}
}
