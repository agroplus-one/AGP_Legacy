package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.org.NaturalezaAlternativa;
import com.rsi.agp.dao.tables.org.NaturalezaAlternativaId;

public class NaturalezaAlternativaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\NaturalezaAlternativa.xml";
		args[1] = "D:\\borrar\\NaturalezaAlternativa.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + NaturalezaAlternativaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			NaturalezaAlternativaXMLParser parser = new NaturalezaAlternativaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Formularios " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Formularios " + e.getMessage());
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
		NaturalezaAlternativa registro = (NaturalezaAlternativa)reg;
		String sql = "";
		sql += registro.getId().getCodconcepto() + ";" + registro.getId().getIddocumento() + ";";
		sql += registro.getId().getCodtiponaturaleza() + ";" + StringUtils.nullToString(registro.getLongitud()) + ";";
		sql += registro.getDecimales() + ";" + /*fecha_baja*/ ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		NaturalezaAlternativa registro;
		if (actual == null){
			registro = new NaturalezaAlternativa();
		}
		else{
			registro = (NaturalezaAlternativa) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			NaturalezaAlternativaId idActual = new NaturalezaAlternativaId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoConcepto")).equals("")){
				idActual.setCodconcepto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codigoConcepto"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "idDocumento")).equals("")){
				idActual.setIddocumento(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "idDocumento"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipoNaturaleza")).equals("")){
				idActual.setCodtiponaturaleza(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "tipoNaturaleza"))));
			}
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "longitud")).equals("")){
				registro.setLongitud(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "longitud"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "decimales")).equals("")){
				registro.setDecimales(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "decimales"))));
			}
		}
		return registro;
	}
}
