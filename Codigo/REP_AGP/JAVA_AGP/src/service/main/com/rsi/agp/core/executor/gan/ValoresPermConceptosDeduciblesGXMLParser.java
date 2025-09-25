package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.ValoresPermConceptosDeduciblesG;
import com.rsi.agp.dao.tables.cpl.gan.ValoresPermConceptosDeduciblesGId;


public class ValoresPermConceptosDeduciblesGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
//		args = new String[4];
//		args[0] = "D:\\borrar\\sin_archivo.xml";
//		args[1] = "D:\\borrar\\sin_archivo.csv";
//		args[2] = "1045";
//		args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ValoresPermConceptosDeduciblesGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			ValoresPermConceptosDeduciblesGXMLParser parser = new ValoresPermConceptosDeduciblesGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de ValoresPermConceptosDeduciblesG" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de ValoresPermConceptosDeduciblesG" + e.getMessage());
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
		ValoresPermConceptosDeduciblesG registro = (ValoresPermConceptosDeduciblesG)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getId().getCodconceptoded() + ";";	
			sql += registro.getId().getValconceptoded() + ";";
			if (registro.getDescripcion() != null){
				sql += registro.getDescripcion() + ";";
			}
			else{
				sql += ";";
			}
			
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		ValoresPermConceptosDeduciblesG registro;
		if (actual == null){
			registro = new ValoresPermConceptosDeduciblesG();
		}
		else{
			registro = (ValoresPermConceptosDeduciblesG) actual;
		}
		ValoresPermConceptosDeduciblesGId idActual = new ValoresPermConceptosDeduciblesGId();
		if (this.getTagPrincipal().equals(tag)){			
			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCptoD")).equals("")){
				idActual.setCodconceptoded(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCptoD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCptoD")).equals("")){
				idActual.setValconceptoded(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "valCptoD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDescripcion(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
	}	
		registro.setId(idActual);
		return registro;
}
}
