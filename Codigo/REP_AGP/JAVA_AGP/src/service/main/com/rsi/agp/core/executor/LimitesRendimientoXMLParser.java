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
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.LimiteRendimientoId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class LimitesRendimientoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\LimitesRendimiento.xml";
		args[1] = "D:\\borrar\\LimitesRendimiento.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + LimitesRendimientoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			LimitesRendimientoXMLParser parser = new LimitesRendimientoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de límites de rendimiento " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de límites de rendimiento " + e.getMessage());
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
		LimiteRendimiento registro = (LimiteRendimiento)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		
		sql += registro.getId().getId() + ";";
		sql += registro.getId().getLineaseguroid() + ";";
		if (registro.getModulo() != null && !StringUtils.nullToString(registro.getModulo().getId().getCodmodulo()).equals("")){
			sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo().trim()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getVariedad() != null && !StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()).equals("")){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getVariedad() != null && !StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()).equals("")){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCodprovincia() != null){
			sql += registro.getCodprovincia() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCodtermino() != null){
			sql += registro.getCodtermino() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSubtermino() != null){
			sql += registro.getSubtermino() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCodcomarca() != null){
			sql += registro.getCodcomarca() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCodtipomarcoplantac() != null){
			sql += registro.getCodtipomarcoplantac() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getEdaddesde() != null){
			sql += registro.getEdaddesde() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getEdadhasta() != null){
			sql += registro.getEdadhasta() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getLimiteinfrdto() != null){
			sql += registro.getLimiteinfrdto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getLimitesuprdto() != null){
			sql += registro.getLimitesuprdto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCaracteristicaExplotacion() != null){
			sql += StringUtils.nullToString(registro.getCaracteristicaExplotacion().getCodcaractexplotacion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getDensidaddesde() != null){
			sql += registro.getDensidaddesde() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getDensidadhasta() != null){
			sql += registro.getDensidadhasta() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getFrecoldesde() != null){
			sql += sdf.format(registro.getFrecoldesde()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getFrecolhasta() != null){
			sql += sdf.format(registro.getFrecolhasta()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getConceptoPpalModulo() != null){
			sql += StringUtils.nullToString(registro.getConceptoPpalModulo().getCodconceptoppalmod()) + ";";
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
		
		if (registro.getGarantizado() != null){
			sql += StringUtils.nullToString(registro.getGarantizado().getCodgarantizado()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getNumudsdesde() != null){
			sql += StringUtils.nullToString(registro.getNumudsdesde()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getNumudshasta() != null){
			sql += StringUtils.nullToString(registro.getNumudshasta()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getNumaniospoda() != null){
			sql += StringUtils.nullToString(registro.getNumaniospoda()) + ";";
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
		
		if (registro.getSistemaProduccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaProduccion().getCodsistemaproduccion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaConduccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaConduccion().getCodsistemaconduccion()) + ";";
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
		
		if (registro.getPracticaCultural() != null){
			sql += StringUtils.nullToString(registro.getPracticaCultural().getCodpracticacultural()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTablardtos() != null){
			sql += registro.getTablardtos() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getApprdto() != null){
			sql += registro.getApprdto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getRcubeleg() != null){
			sql += registro.getRcubeleg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getPctaplrdto() != null){
			sql += registro.getPctaplrdto() + ";";
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
		
		LimiteRendimiento registro;
		if (actual == null){
			registro = new LimiteRendimiento();
		}
		else{
			registro = (LimiteRendimiento) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			LimiteRendimientoId idLim = new LimiteRendimientoId();
			idLim.setId(new BigDecimal(id));
			idLim.setLineaseguroid(lineaseguroid);
			
			registro.setId(idLim);
			
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tabRdtos")).equals("")){
				registro.setTablardtos(new BigDecimal(parser.getAttributeValue(null, "tabRdtos")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMcoPlant")).equals("")){
				registro.setCodtipomarcoplantac(new BigDecimal(parser.getAttributeValue(null, "tipMcoPlant")));
			}
			else{
				registro.setCodtipomarcoplantac(new BigDecimal("-9"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "edadD")).equals("")){
				registro.setEdaddesde(new BigDecimal(parser.getAttributeValue(null, "edadD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "edadH")).equals("")){
				registro.setEdadhasta(new BigDecimal(parser.getAttributeValue(null, "edadH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "limInfRdto")).equals("")){
				registro.setLimiteinfrdto(new BigDecimal(parser.getAttributeValue(null, "limInfRdto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "limSupRdto")).equals("")){
				registro.setLimitesuprdto(new BigDecimal(parser.getAttributeValue(null, "limSupRdto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "carExpl")).equals("")){
				CaracteristicaExplotacion ca = new CaracteristicaExplotacion();
				ca.setCodcaractexplotacion(new BigDecimal(parser.getAttributeValue(null, "carExpl")));
				registro.setCaracteristicaExplotacion(ca);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "densD")).equals("")){
				registro.setDensidaddesde(new BigDecimal(parser.getAttributeValue(null, "densD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "densH")).equals("")){
				registro.setDensidadhasta(new BigDecimal(parser.getAttributeValue(null, "densH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecRecD")).equals("")){
				try {
					registro.setFrecoldesde(sdf2.parse(parser.getAttributeValue(null, "fecRecD")));
				} catch (ParseException e) {
					registro.setFrecoldesde(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecRecH")).equals("")){
				try {
					registro.setFrecolhasta(sdf2.parse(parser.getAttributeValue(null, "fecRecH")));
				} catch (ParseException e) {
					registro.setFrecolhasta(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				ConceptoPpalModulo cpMod = new ConceptoPpalModulo();
				cpMod.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpMod);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				RiesgoCubiertoId rcId = new RiesgoCubiertoId();
				rcId.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "garant")).equals("")){
				Garantizado garantizado = new Garantizado();
				garantizado.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "garant")));
				registro.setGarantizado(garantizado);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numUnidD")).equals("")){
				registro.setNumudsdesde(new BigDecimal(parser.getAttributeValue(null, "numUnidD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numUnidH")).equals("")){
				registro.setNumudshasta(new BigDecimal(parser.getAttributeValue(null, "numUnidH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nadp")).equals("")){
				registro.setNumaniospoda(new BigDecimal(parser.getAttributeValue(null, "nadp")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCult")).equals("")){
				SistemaCultivo sc = new SistemaCultivo();
				sc.setCodsistemacultivo(new BigDecimal(parser.getAttributeValue(null, "sisCult")));
				registro.setSistemaCultivo(sc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisProd")).equals("")){
				SistemaProduccion sp = new SistemaProduccion();
				sp.setCodsistemaproduccion(new BigDecimal(parser.getAttributeValue(null, "sisProd")));
				registro.setSistemaProduccion(sp);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCond")).equals("")){
				SistemaConduccion sc = new SistemaConduccion();
				sc.setCodsistemaconduccion(new BigDecimal(parser.getAttributeValue(null, "sisCond")));
				registro.setSistemaConduccion(sc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipPlant")).equals("")){
				TipoPlantacion tp = new TipoPlantacion();
				tp.setCodtipoplantacion(new BigDecimal(parser.getAttributeValue(null, "tipPlant")));
				registro.setTipoPlantacion(tp);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "praCult")).equals("")){
				PracticaCultural pc = new PracticaCultural();
				pc.setCodpracticacultural(new BigDecimal(parser.getAttributeValue(null, "praCult")));
				registro.setPracticaCultural(pc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aplRdto")).equals("")){
				registro.setApprdto(new Character(parser.getAttributeValue(null, "aplRdto").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				registro.setRcubeleg(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "porAplRdto")).equals("")){
				registro.setPctaplrdto(new BigDecimal(parser.getAttributeValue(null, "porAplRdto")));
			}
		}
		return registro;
	}
}
