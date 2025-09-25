package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasasG;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasasGId;

public class CaracteristicasGrupoTasaGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\CaracteristicasGrupoTasa.xml";
		args[1] = "D:\\borrar\\CaracteristicasGrupoTasa.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";
	    */
	    //FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CaracteristicasGrupoTasaGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CaracteristicasGrupoTasaGXMLParser parser = new CaracteristicasGrupoTasaGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_CARACTERISTICA_GRUPO_TASA);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Caracteristicas Grupo Tasa G " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Caracteristicas Grupo Tasa G " + e.getMessage());
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
		GrupoTasasG registro = (GrupoTasasG)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodtipocapital() + ";";
		sql += registro.getId().getCodespecie() + ";";
		sql += registro.getId().getCodregimen() + ";";
		sql += registro.getId().getCodgruporaza() + ";";
		sql += registro.getId().getCodtipoanimal() + ";";
		sql += registro.getId().getCodgrupotasa() + ";";
		if (registro.getCodcalifsaneamiento() != null){
			sql += registro.getCodcalifsaneamiento() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodcalifsaneamientod() != null){
			sql += registro.getCodcalifsaneamientod() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodcalifsanitaria() != null){
			sql += registro.getCodcalifsanitaria() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodcalifsanitariad() != null){
			sql += registro.getCodcalifsanitariad() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodsistemalmacen() != null){
			sql += registro.getCodsistemalmacen() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodautorizespecial() != null){
			sql += registro.getCodautorizespecial() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodexcepcontratexpl() != null){
			sql += registro.getCodexcepcontratexpl() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodexecpcontratpol() != null){
			sql += registro.getCodexecpcontratpol() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCoddestino() != null){
			sql += registro.getCoddestino() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodduracionperprod() != null){
			sql += registro.getCodduracionperprod() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodtipoasegurado() != null){
			sql += registro.getCodtipoasegurado() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCodtipoganaderia() != null){
			sql += registro.getCodtipoganaderia() + ";";
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
		
		GrupoTasasG registro;
		if (actual == null){
			registro = new GrupoTasasG();
		}
		else{
			registro = (GrupoTasasG) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GrupoTasasGId idActual = new GrupoTasasGId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				idActual.setCodtipocapital(new Long(parser.getAttributeValue(null, "tipCptal")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				idActual.setCodespecie(new Long(parser.getAttributeValue(null, "esp")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				idActual.setCodregimen(new Long(parser.getAttributeValue(null, "reg")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				idActual.setCodgruporaza(new Long(parser.getAttributeValue(null, "grupRaza")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				idActual.setCodtipoanimal(new Long(parser.getAttributeValue(null, "tipAnim")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				idActual.setCodgrupotasa(new Long(parser.getAttributeValue(null, "grupoTasa")));
			}
			registro.setId(idActual);
		}
		else if (GenericXMLParser.TAG_CALIFICACION_SANEAMIENTO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodcalifsaneamiento(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_CALIFICACION_SANEAM_DEDUCIBLE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodcalifsaneamientod(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_CALIFICACION_SANITARIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodcalifsanitaria(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_CALIFICACION_SANIT_DEDUCIBLE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodcalifsanitariad(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_SISTEMA_ALMACENAMIENTO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodsistemalmacen(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_AUTORIZACION_ESPECIAL.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodautorizespecial(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
			}
		}
		else if (GenericXMLParser.TAG_EXCEPCION_CONTRAT_EXPLOTACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodexcepcontratexpl(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_EXCEPCION_CONTRAT_POLIZA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodexecpcontratpol(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_DESTINO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCoddestino(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_DURACION_PERIODO_PRODUCTIVO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodduracionperprod(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_TIPO_ASEGURADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodtipoasegurado(new Long(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_TIPO_GANADERIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodtipoganaderia(new Long(parser.getAttributeValue(null, "valor")));
			}
		}

		return registro;
	}
}
