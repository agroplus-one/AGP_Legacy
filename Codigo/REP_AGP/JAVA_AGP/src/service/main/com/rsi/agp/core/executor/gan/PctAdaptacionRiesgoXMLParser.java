package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.PctAdaptacionRiesgo;
import com.rsi.agp.dao.tables.cpl.gan.PctAdaptacionRiesgoId;

public class PctAdaptacionRiesgoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		
//		args = new String[4];
//		args[0] = "D:\\borrar\\PorcentajesAdaptacionRiesgo.xml";
//		args[1] = "D:\\borrar\\PorcentajesAdaptacionRiesgo.csv";
//		args[2] = "1045";
//		args[3] = new Date().toString();
		
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PctAdaptacionRiesgoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PctAdaptacionRiesgoXMLParser parser = new PctAdaptacionRiesgoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de PctAdaptacionRiesgo " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de PctAdaptacionRiesgo " + e.getMessage());
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
		PctAdaptacionRiesgo registro = (PctAdaptacionRiesgo)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";" + registro.getId().getCodconcepto() + ";";
		sql += registro.getId().getValconcepto() + ";" + registro.getId().getCodtipocapital() + ";" + registro.getId().getCodespecie() + ";";
		sql += registro.getId().getCodregimen() + ";" + registro.getId().getCodgruporaza() + ";" + registro.getId().getCodtipoanimal() + ";";
		sql += registro.getId().getCodprovincia()+ ";" + registro.getId().getCodcomarca() + ";" + registro.getId().getCodtermino() + ";";
		sql += registro.getId().getSubtermino()+ ";" + registro.getId().getCodriesgotarif() + ";";
		
		if (registro.getBonifRecargo() != null){
			sql += registro.getBonifRecargo() + ";";
		}
		else{
			sql += ";";
		}		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PctAdaptacionRiesgo registro;
		if (actual == null){
			registro = new PctAdaptacionRiesgo();
		}
		else{
			registro = (PctAdaptacionRiesgo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PctAdaptacionRiesgoId pctadpRiesgoId = new PctAdaptacionRiesgoId();
			pctadpRiesgoId.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				pctadpRiesgoId.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				pctadpRiesgoId.setCodconcepto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valCpto")).equals("")){
				pctadpRiesgoId.setValconcepto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "valCpto").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				pctadpRiesgoId.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				pctadpRiesgoId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				pctadpRiesgoId.setCodregimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				pctadpRiesgoId.setCodgruporaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				pctadpRiesgoId.setCodtipoanimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("")){
				pctadpRiesgoId.setCodprovincia(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "prov").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("")){
				pctadpRiesgoId.setCodcomarca(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "com").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("")){
				pctadpRiesgoId.setCodtermino(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "term").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals("")){
				pctadpRiesgoId.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRTar")).equals("")){				
				pctadpRiesgoId.setCodriesgotarif(StringUtils.nullToString(parser.getAttributeValue(null, "codRTar")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "bon")).equals("")){				
				registro.setBonifRecargo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "bon").trim())));
			}
			registro.setId(pctadpRiesgoId);

		}
		return registro;
	}
}
