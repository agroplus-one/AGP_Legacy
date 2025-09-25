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
import com.rsi.agp.dao.tables.cgen.ConceptosDeducibles;
import com.rsi.agp.dao.tables.cpl.gan.RelValConceptXConceptDeduciblesG;
import com.rsi.agp.dao.tables.cpl.gan.RelValConceptXConceptDeduciblesGId;


public class RelValConceptXConceptDeduciblesGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\RelacionValoresConceptosDeducibles.xml";
		args[1] = "D:\\borrar\\RelacionValoresConceptosDeducibles.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RelValConceptXConceptDeduciblesGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RelValConceptXConceptDeduciblesGXMLParser parser = new RelValConceptXConceptDeduciblesGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de RelValConceptXConceptDeduciblesG" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de RelValConceptXConceptDeduciblesG" + e.getMessage());
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
		RelValConceptXConceptDeduciblesG registro = (RelValConceptXConceptDeduciblesG)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getConceptosDeducibles().getId().getCodcptoded() + ";";	
			sql += registro.getValconceptoded() + ";";
			sql += registro.getId().getNumagrupacion() + ";";	
			sql += registro.getConceptosDeducibles().getId().getCodcpto() + ";";
			sql += registro.getValconcepto() + ";";
						
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		RelValConceptXConceptDeduciblesG registro;
		if (actual == null){
			registro = new RelValConceptXConceptDeduciblesG();
		}
		else{
			registro = (RelValConceptXConceptDeduciblesG) actual;
		}
		RelValConceptXConceptDeduciblesGId idActual = new RelValConceptXConceptDeduciblesGId();
		if (this.getTagPrincipal().equals(tag)){			
			idActual.setLineaseguroid(lineaseguroid);
			ConceptosDeducibles concDed = new ConceptosDeducibles();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCptoD")).equals("")){
				concDed.getId().setCodcptoded(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCptoD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCptoD")).equals("")){
				registro.setValconceptoded(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "valCptoD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numAgrup")).equals("")){
				idActual.setNumagrupacion(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "numAgrup").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				concDed.getId().setCodcpto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCpto")).equals("")){
				registro.setValconcepto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "valCpto").trim())));
			}
			registro.setConceptosDeducibles(concDed);
	}	
		registro.setId(idActual);
		
		return registro;
}
}
