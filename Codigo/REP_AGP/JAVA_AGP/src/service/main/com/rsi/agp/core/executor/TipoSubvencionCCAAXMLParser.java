package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionCCAA;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class TipoSubvencionCCAAXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\TiposSubvencionesCCAA.xml";
		args[1] = "D:\\borrar\\TiposSubvencionesCCAA.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TipoSubvencionCCAAXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TipoSubvencionCCAAXMLParser parser = new TipoSubvencionCCAAXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Tipos Subvenciones CCAA " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Tipos Subvenciones CCAA " + e.getMessage());
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
		TipoSubvencionCCAA registro = (TipoSubvencionCCAA)reg;
		String sql = "";
		sql += registro.getCodtiposubvccaa() + ";" + StringUtils.nullToString(registro.getDestiposubvccaa()) + ";";
		sql += StringUtils.nullToString(registro.getDeclarable()) + ";" + StringUtils.nullToString(registro.getNiveldeclaracion()) + ";";
		sql += StringUtils.nullToString(registro.getNiveldependencia()) + ";" + StringUtils.nullToString(registro.getAplicable()) + ";";
		if (registro.getDiccionarioDatos() != null){
			sql += StringUtils.nullToString(registro.getDiccionarioDatos().getCodconcepto()) + ";";
		}
		else{
			sql += ";";
		}
		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		TipoSubvencionCCAA registro;
		if (actual == null){
			registro = new TipoSubvencionCCAA();
		}
		else{
			registro = (TipoSubvencionCCAA) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipSubv")).equals("")){
				registro.setCodtiposubvccaa(new BigDecimal(parser.getAttributeValue(null, "tipSubv")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDestiposubvccaa(parser.getAttributeValue(null, "desc").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "decl")).equals("")){
				registro.setDeclarable(new Character(parser.getAttributeValue(null, "decl").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivDecl")).equals("")){
				registro.setNiveldeclaracion(new BigDecimal(parser.getAttributeValue(null, "nivDecl")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivDep")).equals("")){
				registro.setNiveldependencia(new Character(parser.getAttributeValue(null, "nivDep").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "apli")).equals("")){
				registro.setAplicable(new Character(parser.getAttributeValue(null, "apli").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				DiccionarioDatos dd = new DiccionarioDatos();
				dd.setCodconcepto(new BigDecimal(parser.getAttributeValue(null, "codCpto")));
				registro.setDiccionarioDatos(dd);
			}
		}
		return registro;
	}
}
