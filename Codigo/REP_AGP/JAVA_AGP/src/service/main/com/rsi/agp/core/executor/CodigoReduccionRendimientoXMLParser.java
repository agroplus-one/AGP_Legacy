package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CodigoReduccionRendimiento;

public class CodigoReduccionRendimientoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\CodigosReduccionRendimiento.xml";
		args[1] = "D:\\borrar\\CodigosReduccionRendimiento.csv";
		args[2] = "181";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CodigoReduccionRendimientoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CodigoReduccionRendimientoXMLParser parser = new CodigoReduccionRendimientoXMLParser();
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
		CodigoReduccionRendimiento registro = (CodigoReduccionRendimiento)reg;
		String sql = "";
		sql += registro.getCodreducrdto() + ";";
		sql += StringUtils.nullToString(registro.getDesreducrdto()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		CodigoReduccionRendimiento registro;
		if (actual == null){
			registro = new CodigoReduccionRendimiento();
		}
		else{
			registro = (CodigoReduccionRendimiento) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRedRdto")).equals("")){
				registro.setCodreducrdto(new BigDecimal(parser.getAttributeValue(null, "codRedRdto").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDesreducrdto(parser.getAttributeValue(null, "desc").trim());
			}
		}
		return registro;
	}
}
