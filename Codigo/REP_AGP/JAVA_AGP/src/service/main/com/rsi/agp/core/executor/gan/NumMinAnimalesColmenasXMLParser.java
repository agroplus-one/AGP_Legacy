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
import com.rsi.agp.dao.tables.cpl.gan.NumMinAnimalesColmenas;
import com.rsi.agp.dao.tables.cpl.gan.NumMinAnimalesColmenasId;


public class NumMinAnimalesColmenasXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		
//		args = new String[4];
//		args[0] = "D:\\DOCUMENTACION\\SIGPEs\\8346\\67292429\\NumeroMinimoAnimalesColmenas.xml";
//		args[1] = "D:\\DOCUMENTACION\\SIGPEs\\8346\\67292429\\NumeroMinimoAnimalesColmenas.csv";
//		args[2] = "7363";
//		args[3] = new Date().toString();
		
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + NumMinAnimalesColmenasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			NumMinAnimalesColmenasXMLParser parser = new NumMinAnimalesColmenasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de NumMinAnimalesColmenas " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de NumMinAnimalesColmenas " + e.getMessage());
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
		NumMinAnimalesColmenas registro = (NumMinAnimalesColmenas)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";" + registro.getId().getCodtipocapital() + ";";
		sql += registro.getId().getCodespecie() + ";" + registro.getId().getCodregimen() + ";" + registro.getId().getCodgruporaza() + ";";
		if (null != registro.getId().getCodtipoanimal())
			sql += registro.getId().getCodtipoanimal() + ";";
		else
			sql += ";";
		if (null != registro.getCodtipoanimald())
			sql += registro.getCodtipoanimald() + ";";
		else
			sql += ";";
		if (null != registro.getNumAniMin())
			sql += registro.getNumAniMin() + ";";
		else
			sql += ";";
		if (null != registro.getNumColmenasMin())
			sql += registro.getNumColmenasMin() + ";";
		else
			sql += ";";
		if (null != registro.getValMinProd())
			sql += registro.getValMinProd() + ";";
		else
			sql += ";";
		if (null != registro.getNivelAplicacion())
			sql += registro.getNivelAplicacion() + ";";
		else
			sql += ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		NumMinAnimalesColmenas registro;
		if (actual == null){
			registro = new NumMinAnimalesColmenas();
		}
		else{
			registro = (NumMinAnimalesColmenas) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			NumMinAnimalesColmenasId numMinId = new NumMinAnimalesColmenasId();
			numMinId.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				numMinId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				numMinId.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				numMinId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				numMinId.setCodregimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				numMinId.setCodgruporaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				numMinId.setCodtipoanimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimD")).equals("")){
				registro.setCodtipoanimald(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nAnimMin")).equals("")){
				registro.setNumAniMin(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "nAnimMin").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nColMin")).equals("")){
				registro.setNumColmenasMin(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "nColMin").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valMinProd")).equals("")){
				registro.setValMinProd(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valMinProd").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivApl")).equals("")){
				registro.setNivelAplicacion(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "nivApl").trim())));
			}
			registro.setId(numMinId);
		}
		return registro;
	}
}
