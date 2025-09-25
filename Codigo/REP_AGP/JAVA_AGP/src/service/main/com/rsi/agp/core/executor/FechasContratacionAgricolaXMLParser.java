package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProteccion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.FechaContratacionAgricola;
import com.rsi.agp.dao.tables.cpl.FechaContratacionAgricolaId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class FechasContratacionAgricolaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\FechasContratacionAgricola.xml";
		args[1] = "D:\\borrar\\FechasContratacionAgricola.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + FechasContratacionAgricolaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FechasContratacionAgricolaXMLParser parser = new FechasContratacionAgricolaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Fechas de contratación " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Fechas de contratación " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			//e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		FechaContratacionAgricola registro = (FechaContratacionAgricola)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		
		sql += registro.getId().getId() + ";";
		sql += registro.getId().getLineaseguroid() + ";";
		if (registro.getModulo() != null){
			sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo().trim()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getVariedad() != null && !StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()).equals("")){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()) + ";";
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()) + ";";
		}
		else{
			sql += ";";
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		
		if (registro.getFeciniciocontrata() != null){
			sql += sdf.format(registro.getFeciniciocontrata()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getFecfincontrata() != null){
			sql += sdf.format(registro.getFecfincontrata()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaProteccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaProteccion().getCodsistemaproteccion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getUltimodiapago() != null){
			sql += sdf.format(registro.getUltimodiapago()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getRiesgoCubierto() != null){
			sql += StringUtils.nullToString(registro.getRiesgoCubierto().getId().getCodriesgocubierto()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getRiesgocubiertoelegible()) + ";";
		
		if (registro.getConceptoPpalModulo() != null){
			sql += StringUtils.nullToString(registro.getConceptoPpalModulo().getCodconceptoppalmod()) + ";";
		}
		else{
			sql += ";";
		}

		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		
		if (registro.getCicloCultivo() != null){
			sql += StringUtils.nullToString(registro.getCicloCultivo().getCodciclocultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoPlantacion() != null){
			sql += StringUtils.nullToString(registro.getTipoPlantacion().getCodtipoplantacion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoCapital() != null){
			sql += StringUtils.nullToString(registro.getTipoCapital().getCodtipocapital()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaCultivo() != null){
			sql += StringUtils.nullToString(registro.getSistemaCultivo().getCodsistemacultivo()) + ";";
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
		
		FechaContratacionAgricola registro;
		if (actual == null){
			registro = new FechaContratacionAgricola();
		}
		else{
			registro = (FechaContratacionAgricola) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			FechaContratacionAgricolaId idActual = new FechaContratacionAgricolaId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));
				registro.setModulo(modulo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				Variedad var = new Variedad();
				VariedadId varId = new VariedadId();
				varId.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				varId.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
				varId.setLineaseguroid(lineaseguroid);
				var.setId(varId);
				registro.setVariedad(var);
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecICont")).equals("")){
				try {
					registro.setFeciniciocontrata(sdf2.parse(parser.getAttributeValue(null, "fecICont")));
				} catch (ParseException e) {
					registro.setFeciniciocontrata(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecFCont")).equals("")){
				try {
					registro.setFecfincontrata(sdf2.parse(parser.getAttributeValue(null, "fecFCont")));
				} catch (ParseException e) {
					registro.setFecfincontrata(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisProt")).equals("")){
				SistemaProteccion sp = new SistemaProteccion();
				sp.setCodsistemaproteccion(new BigDecimal(parser.getAttributeValue(null, "sisProt")));
				registro.setSistemaProteccion(sp);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "uDPago")).equals("")){
				try {
					registro.setUltimodiapago(sdf2.parse(parser.getAttributeValue(null, "uDPago")));
				} catch (ParseException e) {
					registro.setUltimodiapago(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				RiesgoCubiertoId rcId = new RiesgoCubiertoId();
				rcId.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				rc.setId(rcId);
				registro.setRiesgoCubierto(rc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				registro.setRiesgocubiertoelegible(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				ConceptoPpalModulo cpMod = new ConceptoPpalModulo();
				cpMod.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpMod);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "ciCul")).equals("")){
				CicloCultivo cc = new CicloCultivo();
				cc.setCodciclocultivo(new BigDecimal(parser.getAttributeValue(null, "ciCul")));
				registro.setCicloCultivo(cc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipPlant")).equals("")){
				TipoPlantacion tp = new TipoPlantacion();
				tp.setCodtipoplantacion(new BigDecimal(parser.getAttributeValue(null, "tipPlant")));
				registro.setTipoPlantacion(tp);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				TipoCapital tc = new TipoCapital();
				tc.setCodtipocapital(new BigDecimal(parser.getAttributeValue(null, "tipCptal")));
				registro.setTipoCapital(tc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCult")).equals("")){
				SistemaCultivo sc = new SistemaCultivo();
				sc.setCodsistemacultivo(new BigDecimal(parser.getAttributeValue(null, "sisCult")));
				registro.setSistemaCultivo(sc);
			}
			
		}
		return registro;
	}
}
