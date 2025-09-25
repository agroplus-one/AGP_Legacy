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
import com.rsi.agp.dao.tables.cpl.gan.PctValorMaxExplotacion;
import com.rsi.agp.dao.tables.cpl.gan.PctValorMaxExplotacionId;

public class PctValorMaxExplotacionXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		
//		args = new String[4];
//		args[0] = "D:\\borrar\\xx.xml";
//		args[1] = "D:\\borrar\\xxo.csv";
//		args[2] = "1045";
//		args[3] = new Date().toString();
		
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PctValorMaxExplotacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PctValorMaxExplotacionXMLParser parser = new PctValorMaxExplotacionXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de PctValorMaxExplotacion " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de PctValorMaxExplotacion " + e.getMessage());
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
		PctValorMaxExplotacion registro = (PctValorMaxExplotacion)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";" + registro.getId().getCodtipocapital() + ";";
		sql += registro.getId().getCodespecie() + ";" + registro.getId().getCodregimen() + ";" + registro.getId().getCodgruporaza() + ";";
		
		if (null != registro.getCodtipoanimal())
			sql += registro.getCodtipoanimal() + ";";
		else
			sql += ";";
		if (null != registro.getCodtipoanimald())
			sql += registro.getCodtipoanimald() + ";";
		else
			sql += ";";
		if (null != registro.getValProdMaxExpl())
			sql += registro.getValProdMaxExpl() + ";";
		else
			sql += ";";
		if (null != registro.getNumAnimExcep())
			sql += registro.getNumAnimExcep() + ";";
		else
			sql += ";";
		if (null != registro.getNivelAplicacion())
			sql += registro.getNivelAplicacion() + ";";
		else
			sql += ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PctValorMaxExplotacion registro;
		if (actual == null){
			registro = new PctValorMaxExplotacion();
		}
		else{
			registro = (PctValorMaxExplotacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PctValorMaxExplotacionId pctValorId = new PctValorMaxExplotacionId();
			pctValorId.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				pctValorId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				pctValorId.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				pctValorId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				pctValorId.setCodregimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				pctValorId.setCodgruporaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				registro.setCodtipoanimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimD")).equals("")){
				registro.setCodtipoanimald(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valProdMaxExpl")).equals("")){
				registro.setValProdMaxExpl(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "valProdMaxExpl").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numAnimExcep")).equals("")){
				registro.setNumAnimExcep(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "numAnimExcep").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivApl")).equals("")){
				registro.setNivelAplicacion(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "nivApl").trim())));
			}
			registro.setId(pctValorId);
		}
		return registro;
	}
}
