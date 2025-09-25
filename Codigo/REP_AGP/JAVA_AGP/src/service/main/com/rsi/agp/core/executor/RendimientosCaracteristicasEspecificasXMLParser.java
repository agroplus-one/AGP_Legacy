package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.cpl.CodigoDenominacionOrigen;
import com.rsi.agp.dao.tables.cpl.CodigoDenominacionOrigenId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.RendimientoCaracteristicaEspecificaId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class RendimientosCaracteristicasEspecificasXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\RendimientosCaracteristicasEspecificas.xml";
		args[1] = "D:\\borrar\\RendimientosCaracteristicasEspecificas.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RendimientosCaracteristicasEspecificasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RendimientosCaracteristicasEspecificasXMLParser parser = new RendimientosCaracteristicasEspecificasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Rendimientos de características específicas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Rendimientos de características específicas " + e.getMessage());
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
		RendimientoCaracteristicaEspecifica registro = (RendimientoCaracteristicaEspecifica)reg;
		String sql = "";
		
		sql += registro.getId().getId() + ";";
		sql += registro.getId().getLineaseguroid() + ";";
		if (registro.getModulo() != null){
			sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo().trim()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getVariedad() != null){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()) + ";";
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()) + ";";
		}
		else{
			sql += ";";
			sql += ";";
		}
		
		if (registro.getCodigoDenominacionOrigen() != null){
			sql += StringUtils.nullToString(registro.getCodigoDenominacionOrigen().getId().getCoddenomorigen()) + ";";
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
		
		if (registro.getSistemaConduccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaConduccion().getCodsistemaconduccion()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		sql += StringUtils.nullToString(registro.getLimiteinfrdto()) + ";";
		sql += StringUtils.nullToString(registro.getLimitesuprdto()) + "";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RendimientoCaracteristicaEspecifica registro;
		if (actual == null){
			registro = new RendimientoCaracteristicaEspecifica();
		}
		else{
			registro = (RendimientoCaracteristicaEspecifica) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RendimientoCaracteristicaEspecificaId idRdto = new RendimientoCaracteristicaEspecificaId();
			idRdto.setId(new BigDecimal(id));
			idRdto.setLineaseguroid(lineaseguroid);
			
			registro.setId(idRdto);
			
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codDO")).equals("")){
				CodigoDenominacionOrigen codDO = new CodigoDenominacionOrigen();
				CodigoDenominacionOrigenId idDo = new CodigoDenominacionOrigenId();
				idDo.setCoddenomorigen(new BigDecimal(parser.getAttributeValue(null, "codDO")));
				codDO.setId(idDo);
				registro.setCodigoDenominacionOrigen(codDO);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "praCult")).equals("")){
				PracticaCultural pc = new PracticaCultural();
				pc.setCodpracticacultural(new BigDecimal(parser.getAttributeValue(null, "praCult")));
				registro.setPracticaCultural(pc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCond")).equals("")){
				SistemaConduccion sc = new SistemaConduccion();
				sc.setCodsistemaconduccion(new BigDecimal(parser.getAttributeValue(null, "sisCond")));
				registro.setSistemaConduccion(sc);
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "limInfRdto")).equals("")){
				registro.setLimiteinfrdto(new BigDecimal(parser.getAttributeValue(null, "limInfRdto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "limSupRdto")).equals("")){
				registro.setLimitesuprdto(new BigDecimal(parser.getAttributeValue(null, "limSupRdto")));
			}
		}
		return registro;
	}
}
