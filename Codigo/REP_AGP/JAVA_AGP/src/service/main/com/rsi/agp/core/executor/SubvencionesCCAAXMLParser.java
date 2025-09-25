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
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cgen.TipoRendimiento;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAAId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class SubvencionesCCAAXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\SubvencionesCCAA.xml";
		args[1] = "D:\\borrar\\SubvencionesCCAA.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + SubvencionesCCAAXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			SubvencionesCCAAXMLParser parser = new SubvencionesCCAAXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Subvenciones CCAA " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Subvenciones CCAA " + e.getMessage());
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
		SubvencionCCAA registro = (SubvencionCCAA)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		sql += registro.getOrganismo().getCodorganismo() + ";" + registro.getModulo().getId().getCodmodulo() + ";";
		sql += registro.getTipoRendimiento().getCodtipordto() + ";" + registro.getVariedad().getId().getCodcultivo() + ";";
		sql += registro.getVariedad().getId().getCodvariedad() + ";" + registro.getCodprovincia() + ";";
		sql += registro.getCodcomarca() + ";" + registro.getCodtermino() + ";" + registro.getSubtermino() + ";";
		sql += registro.getTipoSubvencionCCAA().getCodtiposubvccaa() + ";";
		
		if (registro.getGarantizado() != null){
			sql += StringUtils.nullToString(registro.getGarantizado().getCodgarantizado()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getTasacoste()) + ";";
		sql += StringUtils.nullToString(registro.getCodbasecalculosubv()) + ";";
		sql += StringUtils.nullToString(registro.getDatoasocbase()) + ";";
		sql += StringUtils.nullToString(registro.getPctsubvindividual()) + ";";
		sql += StringUtils.nullToString(registro.getPctsubvcolectivo()) + ";";
		
		if (registro.getDiccionarioDatos() != null){
			sql += StringUtils.nullToString(registro.getDiccionarioDatos().getCodconcepto()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getValorconcepto()) + ";";
		
		if (registro.getFechaentradavigorhasta() != null){
			sql += sdf.format(registro.getFechaentradavigorhasta()) + ";";
		}
		else{
			sql += ";";
		}
		
		
		if (registro.getFechaDesde() != null){
			sql += sdf.format(registro.getFechaDesde()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getFechaHasta() != null){
			sql += sdf.format(registro.getFechaHasta()) + ";";
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
		
		SubvencionCCAA registro;
		if (actual == null){
			registro = new SubvencionCCAA();
		}
		else{
			registro = (SubvencionCCAA) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			SubvencionCCAAId idActual = new SubvencionCCAAId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).equals("")){
				Organismo o = new Organismo();
				o.setCodorganismo(new Character(parser.getAttributeValue(null, "codOrg").charAt(0)));
				registro.setOrganismo(o);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));
				registro.setModulo(modulo);
			}
			
			TipoRendimiento tr = new TipoRendimiento();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipRdto")).equals("")){
				tr.setCodtipordto(new BigDecimal(parser.getAttributeValue(null, "tipRdto")));
				registro.setTipoRendimiento(tr);
			}
			else{
				tr.setCodtipordto(new BigDecimal(-9));
				registro.setTipoRendimiento(tr);
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipSubv")).equals("")){
				TipoSubvencionCCAA tsccaa = new TipoSubvencionCCAA();
				tsccaa.setCodtiposubvccaa(new BigDecimal(parser.getAttributeValue(null, "tipSubv")));
				registro.setTipoSubvencionCCAA(tsccaa);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "garant")).equals("")){
				Garantizado garantizado = new Garantizado();
				garantizado.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "garant")));
				registro.setGarantizado(garantizado);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tasaCoste")).equals("")){
				registro.setTasacoste(new BigDecimal(parser.getAttributeValue(null, "tasaCoste")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codBasCal")).equals("")){
				registro.setCodbasecalculosubv(new Character(parser.getAttributeValue(null, "codBasCal").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "datAsoBasCal")).equals("")){
				registro.setDatoasocbase(new BigDecimal(parser.getAttributeValue(null, "datAsoBasCal")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "subvSegInd")).equals("")){
				registro.setPctsubvindividual(new BigDecimal(parser.getAttributeValue(null, "subvSegInd")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "subvSegCol")).equals("")){
				registro.setPctsubvcolectivo(new BigDecimal(parser.getAttributeValue(null, "subvSegCol")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				DiccionarioDatos dd = new DiccionarioDatos();
				dd.setCodconcepto(new BigDecimal(parser.getAttributeValue(null, "codCpto")));
				registro.setDiccionarioDatos(dd);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCpto")).equals("")){
				registro.setValorconcepto(new BigDecimal(parser.getAttributeValue(null, "valCpto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecEntVigH")).equals("")){
				try {
					registro.setFechaentradavigorhasta(sdf2.parse(parser.getAttributeValue(null, "fecEntVigH")));
				} catch (ParseException e) {
					registro.setFechaentradavigorhasta(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fechaDesde")).equals("")){
				try {
					registro.setFechaDesde(sdf2.parse(parser.getAttributeValue(null, "fechaDesde")));
				} catch (ParseException e) {
					registro.setFechaDesde(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fechaHasta")).equals("")){
				try {
					registro.setFechaHasta(sdf2.parse(parser.getAttributeValue(null, "fechaHasta")));
				} catch (ParseException e) {
					registro.setFechaHasta(null);
				}
			}
			
		}
		return registro;
	}
}
