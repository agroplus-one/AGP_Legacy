package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.MedidasAsociadasHistorialAsegurado;
import com.rsi.agp.dao.tables.cpl.MedidasAsociadasHistorialAseguradoId;

public class MedidasAsociadasHistorialAseguradoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\MedidasAsociadasHistorialAsegurado.xml";
		args[1] = "D:\\borrar\\MedidasAsociadasHistorialAsegurado.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidasAsociadasHistorialAseguradoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidasAsociadasHistorialAseguradoXMLParser parser = new MedidasAsociadasHistorialAseguradoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Medidas Asociadas Historial Asegurado " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Medidas Asociadas Historial Asegurado " + e.getMessage());
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
		MedidasAsociadasHistorialAsegurado registro = (MedidasAsociadasHistorialAsegurado)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getGrupomedida() + ";" + StringUtils.nullToString(registro.getPctbonifrecargo()) + ";";
		sql += registro.getId().getHistaseg() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MedidasAsociadasHistorialAsegurado registro;
		if (actual == null){
			registro = new MedidasAsociadasHistorialAsegurado();
		}
		else{
			registro = (MedidasAsociadasHistorialAsegurado) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			MedidasAsociadasHistorialAseguradoId idActual = new MedidasAsociadasHistorialAseguradoId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupMed").trim()).equals("")){
				idActual.setGrupomedida(parser.getAttributeValue(null, "grupMed"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "histAseg").trim()).equals("")){
				idActual.setHistaseg(new BigDecimal(parser.getAttributeValue(null, "histAseg")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "bon").trim()).equals("")){
				registro.setPctbonifrecargo(new BigDecimal(parser.getAttributeValue(null, "bon")));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
