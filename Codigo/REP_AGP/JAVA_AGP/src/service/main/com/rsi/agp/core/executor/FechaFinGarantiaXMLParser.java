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
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantiaId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class FechaFinGarantiaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\FechasFinGarantias.xml";
		args[1] = "D:\\borrar\\FechasFinGarantias.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + FechaFinGarantiaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FechaFinGarantiaXMLParser parser = new FechaFinGarantiaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Fechas fin garantías " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Fechas fin garantías " + e.getMessage());
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
		FechaFinGarantia registro = (FechaFinGarantia)reg;
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
		
		sql += StringUtils.nullToString(registro.getCodconceptoppalmod()) + ";";
		sql += StringUtils.nullToString(registro.getCodriesgocubierto()) + ";";
		
		if (registro.getVariedad() != null && !StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()).equals("")){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()) + ";";
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()) + ";";
		}
		else{
			sql += ";";
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		
		if (registro.getTipoCapital() != null){
			sql += StringUtils.nullToString(registro.getTipoCapital().getCodtipocapital()) + ";";
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
		
		if (registro.getFgarantdesde() != null){
			sql += sdf.format(registro.getFgarantdesde()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getEstadoFenologicoByFkFecGarantEstFenD() != null){
			sql += sdf.format(registro.getEstadoFenologicoByFkFecGarantEstFenD().getId().getCodestadofenologico()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getNummesesdesde()) + ";";
		sql += StringUtils.nullToString(registro.getNumdiasdesde()) + ";";
		
		if (registro.getFgaranthasta() != null){
			sql += sdf.format(registro.getFgaranthasta()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getEstadoFenologicoByFkFecGarantEstFenH() != null){
			sql += sdf.format(registro.getEstadoFenologicoByFkFecGarantEstFenH().getId().getCodestadofenologico()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getNummeseshasta()) + ";";
		sql += StringUtils.nullToString(registro.getNumdiashasta()) + ";";
		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		
		FechaFinGarantia registro;
		if (actual == null){
			registro = new FechaFinGarantia();
		}
		else{
			registro = (FechaFinGarantia) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			FechaFinGarantiaId idActual = new FechaFinGarantiaId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));
				registro.setModulo(modulo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				registro.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				TipoCapital tc = new TipoCapital();
				tc.setCodtipocapital(new BigDecimal(parser.getAttributeValue(null, "tipCptal")));
				registro.setTipoCapital(tc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "praCult")).equals("")){
				PracticaCultural pc = new PracticaCultural();
				pc.setCodpracticacultural(new BigDecimal(parser.getAttributeValue(null, "praCult")));
				registro.setPracticaCultural(pc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecD")).equals("")){
				try {
					registro.setFgarantdesde(sdf2.parse(parser.getAttributeValue(null, "fecD")));
				} catch (ParseException e) {
					registro.setFgarantdesde(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "estFenD")).equals("")){
				EstadoFenologico ef = new EstadoFenologico();
				ef.getId().setCodestadofenologico(new Character(parser.getAttributeValue(null, "estFenD").charAt(0)));
				registro.setEstadoFenologicoByFkFecGarantEstFenD(ef);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mesesD")).equals("")){
				registro.setNummesesdesde(new BigDecimal(parser.getAttributeValue(null, "mesesD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "diasD")).equals("")){
				registro.setNumdiasdesde(new BigDecimal(parser.getAttributeValue(null, "diasD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecH")).equals("")){
				try {
					registro.setFgaranthasta(sdf2.parse(parser.getAttributeValue(null, "fecH")));
				} catch (ParseException e) {
					registro.setFgarantdesde(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "estFenH")).equals("")){
				EstadoFenologico ef = new EstadoFenologico();
				ef.getId().setCodestadofenologico(new Character(parser.getAttributeValue(null, "estFenH").charAt(0)));
				registro.setEstadoFenologicoByFkFecGarantEstFenH(ef);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mesesH")).equals("")){
				registro.setNummeseshasta(new BigDecimal(parser.getAttributeValue(null, "mesesH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "diasH")).equals("")){
				registro.setNumdiashasta(new BigDecimal(parser.getAttributeValue(null, "diasH")));
			}
		}
		return registro;
	}
}
