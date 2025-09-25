package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;


import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.SistemasReduccionSubvenciones;
import com.rsi.agp.dao.tables.cpl.SistemasReduccionSubvencionesId;

public class SistemasReduccionSubvencionesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.xml";
		args[1] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + SistemasReduccionSubvencionesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			SistemasReduccionSubvencionesXMLParser parser = new SistemasReduccionSubvencionesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Sistemas Reduccion Subvenciones " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Sistemas Reduccion Subvenciones " + e.getMessage());
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
		SistemasReduccionSubvenciones registro = (SistemasReduccionSubvenciones)reg;
		
		String sql = "";
		
		//Taty(11.06.2019)
		//sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getId() + ";" ;
		
		sql += StringUtils.nullToString(registro.getSistemaRedSubv()) + ";";
		sql += StringUtils.nullToString(registro.getDescripcion()) + ";";
	
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		SistemasReduccionSubvenciones registro;
		
		if (actual == null){
			registro = new SistemasReduccionSubvenciones();
		}
		else{
			registro = (SistemasReduccionSubvenciones) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			SistemasReduccionSubvencionesId idActual = new SistemasReduccionSubvencionesId();
			idActual.setId(new BigDecimal(id));
			// Taty (11.06.2019)
			//idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			//Taty (11.06.2019)
			//if (!StringUtils.nullToString(parser.getAttributeValue(null, "sistemaRedSubv")).equals("")){
			//	registro.setSistemaRedSubv(new BigDecimal(parser.getAttributeValue(null, "sistemaRedSubv")));
			//}
			
			//if (!StringUtils.nullToString(parser.getAttributeValue(null, "descripcion")).equals("")){
			//	registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "descripcion").trim()));
			//}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sistRed")).equals("")){
				registro.setSistemaRedSubv(new BigDecimal(parser.getAttributeValue(null, "sistRed")));
			}
				
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}

			
		}
		return registro;
	}
}

