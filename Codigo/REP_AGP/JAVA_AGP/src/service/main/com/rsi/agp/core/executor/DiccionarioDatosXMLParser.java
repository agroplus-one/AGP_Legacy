package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.org.TipoNaturaleza;

public class DiccionarioDatosXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\DiccionarioDatos.xml";
		args[1] = "D:\\borrar\\DiccionarioDatos.csv";
		args[2] = "1234";
		args[3] = "DD/MM/YYYY";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + DiccionarioDatosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			DiccionarioDatosXMLParser parser = new DiccionarioDatosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de diccionario de datos " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de diccionario de datos " + e.getMessage());
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
		DiccionarioDatos registro = (DiccionarioDatos)reg;
		String sql = "";
		sql += registro.getCodconcepto() + ";" + registro.getNomconcepto() + ";";
		sql += StringUtils.nullToString(registro.getDesconcepto()).replaceAll("[\n\r]"," ").replaceAll(";", ".") + ";";
		sql += StringUtils.nullToString(registro.getLongitud()) + ";" + StringUtils.nullToString(registro.getDecimales()) + ";";
		sql += StringUtils.nullToString(registro.getTipoNaturaleza().getCodtiponaturaleza()) + ";";
		sql += StringUtils.nullToString(registro.getDeducible()) + ";" + StringUtils.nullToString(registro.getNumtabla()) + ";";
		sql += StringUtils.nullToString(registro.getEtiquetaxml()) + ";" + /*fecha_baja*/ ";" /*+ registro.getGrupoFactores()*/ + ";";
		sql += StringUtils.nullToString(registro.getMultiple()) + ";" + StringUtils.nullToString(registro.getDepriesgocbrto()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		DiccionarioDatos registro;
		if (actual == null){
			registro = new DiccionarioDatos();
		}
		else{
			registro = (DiccionarioDatos) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoConcepto")).equals("")){
				registro.setCodconcepto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codigoConcepto"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nombre")).equals("")){
				registro.setNomconcepto(StringUtils.nullToString(parser.getAttributeValue(null, "nombre").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "descripcion")).equals("")){
				registro.setDesconcepto(StringUtils.nullToString(parser.getAttributeValue(null, "descripcion").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "longitud")).equals("")){
				registro.setLongitud(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "longitud"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "decimales")).equals("")){
				registro.setDecimales(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "decimales"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipoNaturaleza")).equals("")){
				TipoNaturaleza tipoNaturaleza = new TipoNaturaleza();
				tipoNaturaleza.setCodtiponaturaleza(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "tipoNaturaleza"))));
				registro.setTipoNaturaleza(tipoNaturaleza);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "deducible")).equals("")){
				registro.setDeducible(StringUtils.nullToString(parser.getAttributeValue(null, "deducible")).charAt(0));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numeroTabla")).equals("")){
				registro.setNumtabla(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "numeroTabla"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "etiquetaXML")).equals("")){
				registro.setEtiquetaxml(StringUtils.nullToString(parser.getAttributeValue(null, "etiquetaXML")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "multiple")).equals("")){
				registro.setMultiple(StringUtils.nullToString(parser.getAttributeValue(null, "multiple")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dependeRiesgo")).equals("")){
				registro.setDepriesgocbrto(StringUtils.nullToString(parser.getAttributeValue(null, "dependeRiesgo")).charAt(0));
			}
		}
		return registro;
	}
}
