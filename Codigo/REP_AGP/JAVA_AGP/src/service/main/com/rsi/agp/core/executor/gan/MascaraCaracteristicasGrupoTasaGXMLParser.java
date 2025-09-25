package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.MascaraGrupoTasasG;
import com.rsi.agp.dao.tables.cpl.gan.MascaraGrupoTasasGId;

public class MascaraCaracteristicasGrupoTasaGXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		/*
		//TEMPORAL
		args = new String[4];
		args[0] = "D:\\borrar\\MascaraCaracteristicasGrupoTasa.xml";
		args[1] = "D:\\borrar\\MascaraCaracteristicasGrupoTasa.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		//FIN TEMPORAL
		*/
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ MascaraCaracteristicasGrupoTasaGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MascaraCaracteristicasGrupoTasaGXMLParser parser = new MascaraCaracteristicasGrupoTasaGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de MascaraCaracteristicasGrupoTasaG " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de MascaraCaracteristicasGrupoTasaG " + e.getMessage());
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
		MascaraGrupoTasasG registro = (MascaraGrupoTasasG)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodtipocapital() + ";" + registro.getId().getCodespecie() + ";" + registro.getId().getCodregimen() + ";";
		sql += registro.getId().getCodgruporaza() + ";" + registro.getId().getCodtipoanimal() + ";";
		sql += registro.getId().getCodconcepto() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MascaraGrupoTasasG registro;
		if (actual == null){
			registro = new MascaraGrupoTasasG();
		}
		else{
			registro = (MascaraGrupoTasasG) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			MascaraGrupoTasasGId idMasc = new MascaraGrupoTasasGId();
			idMasc.setLineaseguroid(lineaseguroid);
	
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("0")){
				idMasc.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
			}
			else{
				idMasc.setCodmodulo("99999");
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("0")){
				idMasc.setCodtipocapital(new Long(parser.getAttributeValue(null, "tipCptal")));
			}
			else{
				idMasc.setCodtipocapital(new Long("999"));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("0")){
				idMasc.setCodespecie(new Long(parser.getAttributeValue(null, "esp")));
			}
			else{
				idMasc.setCodespecie(new Long("999"));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("0")){
				idMasc.setCodregimen(new Long(parser.getAttributeValue(null, "reg")));
			}
			else{
				idMasc.setCodregimen(new Long("999"));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("0")){
				idMasc.setCodgruporaza(new Long(parser.getAttributeValue(null, "grupRaza")));
			}
			else{
				idMasc.setCodgruporaza(new Long("999"));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("0")){
				idMasc.setCodtipoanimal(new Long(parser.getAttributeValue(null, "tipAnim")));
			}
			else{
				idMasc.setCodtipoanimal(new Long("999"));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				idMasc.setCodconcepto(new Long(parser.getAttributeValue(null, "codCpto")));
			}
			else{
				idMasc.setCodtipoanimal(new Long("0"));
			}			
			registro.setId(idMasc);
		}
		return registro;
	}
}
