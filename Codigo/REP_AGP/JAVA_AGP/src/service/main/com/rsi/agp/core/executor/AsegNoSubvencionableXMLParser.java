package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.AseguradoNoSubvencionable;
import com.rsi.agp.dao.tables.cgen.AseguradoNoSubvencionableId;

public class AsegNoSubvencionableXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\AseguradosNoSubvencionables.xml";
		args[1] = "D:\\borrar\\AseguradosNoSubvencionables.csv";
		args[2] = "0";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + AsegNoSubvencionableXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			AsegNoSubvencionableXMLParser parser = new AsegNoSubvencionableXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Asegurados no Subvencionables " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Asegurados no Subvencionables " + e.getMessage());
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
		AseguradoNoSubvencionable registro = (AseguradoNoSubvencionable)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		String sql = "";
		sql += registro.getId().getCodorganismo() + ";" + registro.getId().getNifasegurado() + ";";
		sql += StringUtils.nullToString(registro.getApellido1aseg()) + ";" + StringUtils.nullToString(registro.getApellido2aseg()) + ";";
		sql += StringUtils.nullToString(registro.getNombreaseg()) + ";" + StringUtils.nullToString(registro.getRazonsocialaseg()) + ";";
		if (registro.getId().getFecefectosentencia() != null){
			sql += sdf.format(registro.getId().getFecefectosentencia()) + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getId().getFecfinsentencia() != null){
			sql += sdf.format(registro.getId().getFecfinsentencia()) + ";";
		}
		else{
			sql += ";";
		}
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		AseguradoNoSubvencionable registro;
		if (actual == null){
			registro = new AseguradoNoSubvencionable();
		}
		else{
			registro = (AseguradoNoSubvencionable) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
		
			AseguradoNoSubvencionableId idActual = new AseguradoNoSubvencionableId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).equals("")){
				idActual.setCodorganismo(new Character(StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).equals("")){
				idActual.setNifasegurado(StringUtils.nullToString(parser.getAttributeValue(null, "nif")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecEfectSent")).equals("")){
				try {
					idActual.setFecefectosentencia(sdf2.parse(parser.getAttributeValue(null, "fecEfectSent")));
				} catch (ParseException e) {
					idActual.setFecefectosentencia(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecFinSent")).equals("")){
				try {
					idActual.setFecfinsentencia(sdf2.parse(parser.getAttributeValue(null, "fecFinSent")));
				} catch (ParseException e) {
					idActual.setFecfinsentencia(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "ap1")).equals("")){
				registro.setApellido1aseg(StringUtils.nullToString(parser.getAttributeValue(null, "ap1").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "ap2")).equals("")){
				registro.setApellido2aseg(StringUtils.nullToString(parser.getAttributeValue(null, "ap2").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nom")).equals("")){
				registro.setNombreaseg(StringUtils.nullToString(parser.getAttributeValue(null, "nom").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "razSoc")).equals("")){
				registro.setRazonsocialaseg(StringUtils.nullToString(parser.getAttributeValue(null, "razSoc").trim()));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
