package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;


import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.BasesCalculoSubvenciones;
import com.rsi.agp.dao.tables.cpl.BasesCalculoSubvencionesId;

public class BasesCalculoSubvencionesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.xml";
		args[1] = "D:\\borrar\\SistemasReduccionSubvencionesCCAA.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + BasesCalculoSubvencionesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			BasesCalculoSubvencionesXMLParser parser = new BasesCalculoSubvencionesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Bases Calculo Subvenciones " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Bases Calculo Subvenciones " + e.getMessage());
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
		BasesCalculoSubvenciones registro = (BasesCalculoSubvenciones)reg;
		
		String sql = "";
		// Taty (11.06.2019)
		//sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getId() + ";";
		sql += StringUtils.nullToString(registro.getCodbaseCalcSubvenciones()) + ";";
		sql += StringUtils.nullToString(registro.getDescripcion()) + ";";
		sql += StringUtils.nullToString(registro.getReqValProLim()) + ";";
	
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		BasesCalculoSubvenciones registro;
		
		if (actual == null){
			registro = new BasesCalculoSubvenciones();
		}
		else{
			registro = (BasesCalculoSubvenciones) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			BasesCalculoSubvencionesId idActual = new BasesCalculoSubvencionesId();
			idActual.setId(new BigDecimal(id));
			// Taty (11.06.2019)
			//idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			// Taty (11.06.2019)
			//if (!StringUtils.nullToString(parser.getAttributeValue(null, "codbaseCalcSubvenciones")).equals("")){
			//	registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "codbaseCalcSubvenciones").trim()));
			//}
			
			//if (!StringUtils.nullToString(parser.getAttributeValue(null, "descripcion")).equals("")){
			//	registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "descripcion").trim()));
			//}
			
			//if (!StringUtils.nullToString(parser.getAttributeValue(null, "reqValProLim")).equals("")){
			//	registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "reqValProLim").trim()));
			//}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codBasCal")).equals("")){
				registro.setCodbaseCalcSubvenciones(StringUtils.nullToString(parser.getAttributeValue(null, "codBasCal").trim()).charAt(0));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reqVPL")).equals("")){
				registro.setReqValProLim(StringUtils.nullToString(parser.getAttributeValue(null, "reqVPL").trim()).charAt(0));
			}

			
		}
		return registro;
	}
}
