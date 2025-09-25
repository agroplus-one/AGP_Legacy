package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.MedidaAsociadaAseguradoG;
import com.rsi.agp.dao.tables.cpl.gan.MedidaAsociadaAseguradoGId;

public class MedidaAsociadaAseguradoGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
//		args = new String[4];
//		args[0] = "D:\\borrar\\SIN_ARCHIVO.xml";
//		args[1] = "D:\\borrar\\SIN_ARCHIVO.csv";
//		args[2] = "1045";
//		args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidaAsociadaAseguradoGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidaAsociadaAseguradoGXMLParser parser = new MedidaAsociadaAseguradoGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de MedidaAsociadaAseguradoG" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de MedidaAsociadaAseguradoG" + e.getMessage());
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
		MedidaAsociadaAseguradoG registro = (MedidaAsociadaAseguradoG)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getId().getCodmodulo() + ";";	
			sql += registro.getId().getGrupoMedida() + ";";
			sql += registro.getId().getGrupoNegocio() + ";";			
			sql += registro.getId().getCodespecie() + ";";
			sql += registro.getId().getCodtipomedida() + ";";
			if (registro.getPctmedida() != null)
				sql += registro.getPctmedida() + ";";
			if (registro.getCoefmedida() != null)
				sql += registro.getCoefmedida() + ";";
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		MedidaAsociadaAseguradoG registro;
		if (actual == null){
			registro = new MedidaAsociadaAseguradoG();
		}
		else{
			registro = (MedidaAsociadaAseguradoG) actual;
		}
		MedidaAsociadaAseguradoGId idActual = new MedidaAsociadaAseguradoGId();
		if (this.getTagPrincipal().equals(tag)){			
			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupMed")).equals("")){
				idActual.setGrupoMedida(StringUtils.nullToString(parser.getAttributeValue(null, "grupMed").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupNeg")).equals("")){
				idActual.setGrupoNegocio(new Character(parser.getAttributeValue(null, "grupNeg").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				idActual.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMed")).equals("")){
				idActual.setCodtipomedida(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipMed").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "med")).equals("")){
				registro.setPctmedida(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "med").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "coefMed")).equals("")){
				registro.setCoefmedida(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "coefMed").trim())));
			}
	}	
		registro.setId(idActual);
		return registro;
}
}
