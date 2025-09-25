package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamiento;
import com.rsi.agp.dao.tables.cpl.CondicionesFraccionamientoId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;


/**
 * Parser tabla 0145: Condiciones fraccionamiento
 */
public class CondicionesFraccionamientoXMLParser extends GenericXMLParser {
	
public static void main(String[] args){
	
//		args = new String[4];
//		args[0] = "D:\\borrar\\CondicionesFraccionamiento.xml";
//		args[1] = "D:\\borrar\\CondicionesFraccionamiento.csv";
//		args[2] = "1001";
//		args[3] = "null";
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CondicionesFraccionamientoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CondicionesFraccionamientoXMLParser parser = new CondicionesFraccionamientoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero de Condiciones de Fraccionamiento " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Condiciones de Fraccionamiento " + e.getMessage());
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
		CondicionesFraccionamiento registro = (CondicionesFraccionamiento)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += StringUtils.nullToString(registro.getId().getPeriodoFracc()) + ";";
		sql += StringUtils.nullToString(registro.getPctRecAval()) + ";";
		sql += StringUtils.nullToString(registro.getPctRecFracc()) + ";";
		sql += StringUtils.nullToString(registro.getImpMinRecAval()) + ";";
		sql += StringUtils.nullToString(registro.getId().getCodmodulo()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		CondicionesFraccionamiento registro;
		CondicionesFraccionamientoId idActual;
		
		if (actual == null){
			registro = new CondicionesFraccionamiento();
			idActual = new CondicionesFraccionamientoId();
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
		}
		else{
			registro = (CondicionesFraccionamiento) actual;
			idActual = registro.getId();
		}
		
		if (this.getTagPrincipal().equals(tag)){
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "perFra")).trim().equals("")){
				idActual.setPeriodoFracc(new BigDecimal(parser.getAttributeValue(null, "perFra")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "recAva")).trim().equals("")){
				registro.setPctRecAval(new BigDecimal(parser.getAttributeValue(null, "recAva")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "recFra")).trim().equals("")){
				registro.setPctRecFracc(new BigDecimal(parser.getAttributeValue(null, "recFra")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "impMinRecAva")).trim().equals("")){
				registro.setImpMinRecAval(new BigDecimal(parser.getAttributeValue(null, "impMinRecAva")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).trim().equals("")){
				Modulo modulo = new Modulo();
				ModuloId moduloId = new ModuloId();
				String codModulo = StringUtils.nullToString(parser.getAttributeValue(null, "mod")).trim();
				moduloId.setCodmodulo(codModulo);
				moduloId.setLineaseguroid(lineaseguroid);
				modulo.setId(moduloId);
				registro.setModulo(modulo);
				idActual.setCodmodulo(codModulo);
			}
		}
		
		return registro;
	}
}