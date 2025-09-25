package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.IncompatibilidadContratacion;
import com.rsi.agp.dao.tables.cpl.IncompatibilidadContratacionId;

public class IncompatibilidadesContratacionXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
//		
//		//TEMPORAL
//		args = new String[4];
//		args[0] = "D:\\borrar\\IncompatibilidadesContratacion.xml";
//		args[1] = "D:\\borrar\\IncompatibilidadesContratacion.csv";
//		args[2] = "181";
//		args[3] = "d/MM/yyyy";
//		//FIN TEMPORAL
//		
		if (args.length != 4) {
			System.out.println("Usage: java " + IncompatibilidadesContratacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			IncompatibilidadesContratacionXMLParser parser = new IncompatibilidadesContratacionXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Incompatibilidades Contratacion " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Incompatibilidades Contratacion " + e.getMessage());
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
		IncompatibilidadContratacion registro = (IncompatibilidadContratacion)reg;
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getCodmodulo().trim() + ";";
		sql += registro.getCodcultivo() + ";";
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		sql += StringUtils.nullToString(registro.getCodvariedad()) + ";";
		sql += StringUtils.nullToString(registro.getLineaseguroidinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodmoduloinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodcultivoinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodprovinciainc()) + ";";
		sql += StringUtils.nullToString(registro.getCodterminoinc()) + ";";
		sql += StringUtils.nullToString(registro.getSubterminoinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodvariedadinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		sql += StringUtils.nullToString(registro.getCodcomarcainc()) + ";";
		sql += StringUtils.nullToString(registro.getCodplaninc()) + ";";
		sql += StringUtils.nullToString(registro.getCodlineainc()) + ";";
		sql += registro.getId().getId()+ ";";
		sql += StringUtils.nullToString(registro.getCodconceptoppalmod()) + ";";
		sql += StringUtils.nullToString(registro.getCodriesgocubierto()) + ";";
		sql += StringUtils.nullToString(registro.getRiesgocubiertoelegible()) + ";";
		sql += StringUtils.nullToString(registro.getCodconceptoppalmodinc()) + ";";
		sql += StringUtils.nullToString(registro.getCodriesgocubiertoinc()) + ";";
		sql += StringUtils.nullToString(registro.getRiesgocubiertoelegibleinc());
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		IncompatibilidadContratacion registro;
		if (actual == null){
			registro = new IncompatibilidadContratacion();
		}
		else{
			registro = (IncompatibilidadContratacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			IncompatibilidadContratacionId idActual = new IncompatibilidadContratacionId();
			idActual.setLineaseguroid(lineaseguroid);
			idActual.setId(new BigDecimal(id));
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				registro.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				registro.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				registro.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				registro.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				registro.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				registro.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				registro.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				registro.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				registro.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				registro.setSubtermino(new Character('-'));
			}
			else{
				registro.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}			
			//este dato habrá que calcularlo durante el proceso de inserción en la tabla del condicionado
			registro.setLineaseguroidinc(null);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCodconceptoppalmod(new Long(parser.getAttributeValue(null, "cPMod").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				registro.setCodriesgocubierto(new Long(parser.getAttributeValue(null, "codRCub").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				registro.setRiesgocubiertoelegible(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
		}
		else if (GenericXMLParser.TAG_DATOS_INCOMPATIBILIDAD.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "plan")).equals("")){
				registro.setCodplaninc(new BigDecimal(parser.getAttributeValue(null, "plan")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "linea")).equals("")){
				registro.setCodlineainc(new BigDecimal(parser.getAttributeValue(null, "linea")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				registro.setCodmoduloinc(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				registro.setCodcultivoinc(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				registro.setCodvariedadinc(new BigDecimal(parser.getAttributeValue(null, "var")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				registro.setCodprovinciainc(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				registro.setCodprovinciainc(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				registro.setCodcomarcainc(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				registro.setCodcomarcainc(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				registro.setCodterminoinc(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				registro.setCodterminoinc(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				registro.setSubterminoinc(new Character('-'));
			}
			else{
				registro.setSubterminoinc(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCodconceptoppalmodinc(new Long(parser.getAttributeValue(null, "cPMod").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				registro.setCodriesgocubiertoinc(new Long(parser.getAttributeValue(null, "codRCub").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				registro.setRiesgocubiertoelegibleinc(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
		}
		
		return registro;
	}
}
