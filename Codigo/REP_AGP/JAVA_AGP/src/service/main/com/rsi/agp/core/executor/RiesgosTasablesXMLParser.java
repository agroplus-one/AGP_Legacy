package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.RiesgoTarificable;
import com.rsi.agp.dao.tables.cpl.RiesgoTasable;
import com.rsi.agp.dao.tables.cpl.RiesgoTasableId;

public class RiesgosTasablesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\RiesgosTasables.xml";
		args[1] = "D:\\borrar\\RiesgosTasables.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RiesgosTasablesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RiesgosTasablesXMLParser parser = new RiesgosTasablesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de riesgos tasables " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de riesgos tasables " + e.getMessage());
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
		RiesgoTasable registro = (RiesgoTasable)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodriesgotasable() + ";";
		if (registro.getRiesgoTarificable() != null){
			sql += registro.getRiesgoTarificable().getId().getCodriesgotarificable() + ";";
		}
		else{
			sql += ";";
		}
		sql += registro.getId().getCodmodulo() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RiesgoTasable registro;
		if (actual == null){
			registro = new RiesgoTasable();
		}
		else{
			registro = (RiesgoTasable) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RiesgoTasableId idActual = new RiesgoTasableId();
			
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRTas").trim()).equals("")){
				idActual.setCodriesgotasable(parser.getAttributeValue(null, "codRTas").trim());
			}
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRTar").trim()).equals("")){
				RiesgoTarificable rt = new RiesgoTarificable();
				rt.getId().setCodriesgotarificable(parser.getAttributeValue(null, "codRTar").trim());
				registro.setRiesgoTarificable(rt);
			}
			
		}
		return registro;
	}
}
