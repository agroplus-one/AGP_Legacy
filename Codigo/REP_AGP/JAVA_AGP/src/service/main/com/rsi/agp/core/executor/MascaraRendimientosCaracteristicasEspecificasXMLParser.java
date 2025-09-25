package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.MascaraRendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.MascaraRendimientoCaracteristicaEspecificaId;

public class MascaraRendimientosCaracteristicasEspecificasXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\MascaraRendimientosCaracteristicasEspecificas.xml";
		args[1] = "D:\\borrar\\MascaraRendimientosCaracteristicasEspecificas.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ MascaraRendimientosCaracteristicasEspecificasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MascaraRendimientosCaracteristicasEspecificasXMLParser parser = new MascaraRendimientosCaracteristicasEspecificasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de máscara de rendimientos de características específicas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de máscara de rendimientos de características específicas " + e.getMessage());
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
		MascaraRendimientoCaracteristicaEspecifica registro = (MascaraRendimientoCaracteristicaEspecifica)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodcultivo() + ";" + registro.getId().getCodvariedad() + ";" + registro.getId().getCodprovincia() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += registro.getId().getCodconcepto() + ";" + registro.getId().getCodcomarca();
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MascaraRendimientoCaracteristicaEspecifica registro;
		if (actual == null){
			registro = new MascaraRendimientoCaracteristicaEspecifica();
		}
		else{
			registro = (MascaraRendimientoCaracteristicaEspecifica) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			MascaraRendimientoCaracteristicaEspecificaId idMasc = new MascaraRendimientoCaracteristicaEspecificaId();
			idMasc.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idMasc.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				idMasc.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				idMasc.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				idMasc.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				idMasc.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				idMasc.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				idMasc.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				idMasc.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				idMasc.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idMasc.setSubtermino(new Character('-'));
			}
			else{
				idMasc.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				idMasc.setCodconcepto(new BigDecimal(parser.getAttributeValue(null, "codCpto")));
			}
			else{
				idMasc.setCodconcepto(new BigDecimal("0"));
			}
			
			registro.setId(idMasc);
		}
		return registro;
	}
}
