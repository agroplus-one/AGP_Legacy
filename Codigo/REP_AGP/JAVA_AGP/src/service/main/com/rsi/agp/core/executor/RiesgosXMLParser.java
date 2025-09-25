package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Riesgo;
import com.rsi.agp.dao.tables.cgen.RiesgoId;

public class RiesgosXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\Riesgos.xml";
		args[1] = "D:\\borrar\\Riesgos.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RiesgosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RiesgosXMLParser parser = new RiesgosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de riesgos " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de riesgos " + e.getMessage());
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
		Riesgo registro = (Riesgo)reg;
		String sql = "";
		sql += registro.getId().getCodgruposeguro() + ";";
		sql += registro.getId().getCodriesgo() + ";" + StringUtils.nullToString(registro.getDesriesgo()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Riesgo registro;
		if (actual == null){
			registro = new Riesgo();
		}
		else{
			registro = (Riesgo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RiesgoId idActual = new RiesgoId();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoGrupoSeguro").trim()).equals("")){
				idActual.setCodgruposeguro(parser.getAttributeValue(null, "codigoGrupoSeguro").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRies").trim()).equals("")){
				idActual.setCodriesgo(parser.getAttributeValue(null, "codRies").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()).equals("")){
				registro.setDesriesgo(parser.getAttributeValue(null, "desc").trim());
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
