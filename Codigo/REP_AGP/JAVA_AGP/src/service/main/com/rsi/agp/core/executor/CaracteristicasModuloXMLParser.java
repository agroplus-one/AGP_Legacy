package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModuloId;

public class CaracteristicasModuloXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\CaracteristicasModulo.xml";
		args[1] = "D:\\borrar\\CaracteristicasModulo.csv";
		args[2] = "181";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ CaracteristicasModuloXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CaracteristicasModuloXMLParser parser = new CaracteristicasModuloXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Características del módulo " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Características del módulo " + e.getMessage());
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
		CaracteristicaModulo registro = (CaracteristicaModulo)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + SEPARADOR + registro.getId().getCodmodulo() + SEPARADOR;
		sql += registro.getId().getFilamodulo() + SEPARADOR + registro.getId().getColumnamodulo() + SEPARADOR;
		sql += StringUtils.nullToString(registro.getDatovinculado()) + SEPARADOR + StringUtils.nullToString(registro.getTipovalor()) + SEPARADOR;
		sql += StringUtils.nullToString(reemplazarSeparador(registro.getObservaciones())) + SEPARADOR + StringUtils.nullToString(registro.getNiveleleccion()) + SEPARADOR;
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		CaracteristicaModulo registro;
		if (actual == null){
			registro = new CaracteristicaModulo();
		}
		else{
			registro = (CaracteristicaModulo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			CaracteristicaModuloId idActual = new CaracteristicaModuloId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fMod")).equals("")){
				idActual.setFilamodulo(new BigDecimal(parser.getAttributeValue(null, "fMod")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cMod")).equals("")){
				idActual.setColumnamodulo(new BigDecimal(parser.getAttributeValue(null, "cMod")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "datVinc")).trim().equals("")){
				registro.setDatovinculado(new Character(parser.getAttributeValue(null, "datVinc").trim().charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipVal")).trim().equals("")){
				registro.setTipovalor(new Character(parser.getAttributeValue(null, "tipVal").trim().charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "obsv")).equals("")){
				registro.setObservaciones(parser.getAttributeValue(null, "obsv").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivElec")).trim().equals("")){
				registro.setNiveleleccion(new Character(parser.getAttributeValue(null, "nivElec").trim().charAt(0)));
			}
			
			registro.setId(idActual);
		}
		return registro;
	}
}
