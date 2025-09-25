package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.ComisionAgraria;
import com.rsi.agp.dao.tables.cpl.ComisionAgrariaId;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;

public class ComisionesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Comisiones.xml";
		args[1] = "D:\\borrar\\Comisiones.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ComisionesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			ComisionesXMLParser parser = new ComisionesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Codigos de denominación de origen " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Codigos de denominación de origen " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		ComisionAgraria registro = (ComisionAgraria)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getModulo().getId().getCodmodulo() + ";";
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		sql += StringUtils.nullToString(registro.getCodgrupotasa()) + ";";
		sql += StringUtils.nullToString(registro.getGastosindiv()) + ";";
		sql += StringUtils.nullToString(registro.getGastoscol()) + ";";
		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		
		if (registro.getCultivo() != null){
			sql += StringUtils.nullToString(registro.getCultivo().getId().getCodcultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getId().getId()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		ComisionAgraria registro;
		if (actual == null){
			registro = new ComisionAgraria();
		}
		else{
			registro = (ComisionAgraria) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			ComisionAgrariaId idActual = new ComisionAgrariaId();
			idActual.setLineaseguroid(lineaseguroid);
			idActual.setId(new BigDecimal(id));
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				Modulo m = new Modulo();
				m.getId().setCodmodulo(parser.getAttributeValue(null, "mod").trim());
				registro.setModulo(m);
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				registro.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "gsExtEntInd")).equals("")){
				registro.setGastosindiv(new BigDecimal(parser.getAttributeValue(null, "gsExtEntInd")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "gsExtEntCol")).equals("")){
				registro.setGastoscol(new BigDecimal(parser.getAttributeValue(null, "gsExtEntCol")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				Cultivo cultivo = new Cultivo();
				cultivo.getId().setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				registro.setCultivo(cultivo);
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
