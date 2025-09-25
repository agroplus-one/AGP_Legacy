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
import com.rsi.agp.dao.tables.cpl.gan.VinculacionFilasModuloG;
import com.rsi.agp.dao.tables.cpl.gan.VinculacionFilasModuloGId;

public class VinculacionFilasModuloGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		//args = new String[4];
		//args[0] = "D:\\borrar\\VinculacionesFilasModulo.xml";
		//args[1] = "D:\\borrar\\VinculacionesFilasModulo.csv";
		//args[2] = "1045";
		//args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + VinculacionFilasModuloGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			VinculacionFilasModuloGXMLParser parser = new VinculacionFilasModuloGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de VinculacionFilasModuloG" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de VinculacionFilasModuloG" + e.getMessage());
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
		System.out.println("pintando sql");
		VinculacionFilasModuloG registro = (VinculacionFilasModuloG)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getId().getCodmodulo() + ";";	
			sql += registro.getId().getFilamodulo() + ";";
			sql += registro.getId().getValriesgocubeleg() + ";";
			sql += registro.getId().getNumagrupacion() + ";";
			sql += registro.getId().getFilamodvinculada() + ";";
			sql += registro.getId().getRiesgocubelegvin() + ";";		
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		System.out.println("rellenando objeto");
		VinculacionFilasModuloG registro;
		if (actual == null){
			registro = new VinculacionFilasModuloG();
		}
		else{
			registro = (VinculacionFilasModuloG) actual;
		}
		VinculacionFilasModuloGId idActual = new VinculacionFilasModuloGId();
		if (this.getTagPrincipal().equals(tag)){			
			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fMod")).equals("")){
				idActual.setFilamodulo(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "fMod").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				idActual.setValriesgocubeleg(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numAgrup")).equals("")){
				idActual.setNumagrupacion(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "numAgrup").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fModVinc")).equals("")){
				idActual.setFilamodvinculada(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "fModVinc").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoElegVinc")).equals("")){
				idActual.setRiesgocubelegvin(new Character(parser.getAttributeValue(null, "riesgCbtoElegVinc").charAt(0)));
			}

	}	
		registro.setId(idActual);
		return registro;
}
}
