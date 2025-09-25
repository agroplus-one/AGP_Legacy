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
import com.rsi.agp.dao.tables.cgen.Organismo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.SistemaReduccionSubvCCAA;
import com.rsi.agp.dao.tables.cpl.SistemaReduccionSubvCCAAId;

public class SistemasReduccionSubvencionesCCAAXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.xml";
		args[1] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + SistemasReduccionSubvencionesCCAAXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			SistemasReduccionSubvencionesCCAAXMLParser parser = new SistemasReduccionSubvencionesCCAAXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Sistemas Reduccion Subvenciones CCAA " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Sistemas Reduccion Subvenciones CCAA " + e.getMessage());
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
		SistemaReduccionSubvCCAA registro = (SistemaReduccionSubvCCAA)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		if (registro.getOrganismo() != null){
			sql += StringUtils.nullToString(registro.getOrganismo().getCodorganismo()) + ";";
		}
		if (registro.getModulo() != null){
			sql += registro.getModulo().getId().getCodmodulo() + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getCodsistemareduccion()) + ";";
		sql += StringUtils.nullToString(registro.getPctsubvmax()) + ";";
		sql += StringUtils.nullToString(registro.getImpmaxsubv()) + ";";
		
		if (registro.getCultivo() != null){
			sql += registro.getCultivo().getId().getCodcultivo() + ";";
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
		
		SistemaReduccionSubvCCAA registro;
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		
		if (actual == null){
			registro = new SistemaReduccionSubvCCAA();
		}
		else{
			registro = (SistemaReduccionSubvCCAA) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			SistemaReduccionSubvCCAAId idActual = new SistemaReduccionSubvCCAAId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOrg")).equals("")){
				Organismo org = new Organismo();
				org.setCodorganismo(new Character(parser.getAttributeValue(null, "codOrg").trim().charAt(0)));
				registro.setOrganismo(org);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));
				registro.setModulo(modulo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				Cultivo cultivo = new Cultivo();
				cultivo.getId().setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				registro.setCultivo(cultivo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sistRed")).equals("")){
				registro.setCodsistemareduccion(new BigDecimal(parser.getAttributeValue(null, "sistRed")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "subvMax")).equals("")){
				registro.setPctsubvmax(new BigDecimal(parser.getAttributeValue(null, "subvMax")));
			}

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "limSubv")).equals("")){
				registro.setImpmaxsubv(new BigDecimal(parser.getAttributeValue(null, "limSubv")));
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
