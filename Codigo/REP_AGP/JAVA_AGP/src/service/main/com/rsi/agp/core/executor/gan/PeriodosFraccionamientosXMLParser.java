package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.gan.PeriodosFraccionamientos;
import com.rsi.agp.dao.tables.cpl.gan.PeriodosFraccionamientosId;

/**
 * Parser tabla 0450: Periodos y Fraccionamiento
 */
public class PeriodosFraccionamientosXMLParser extends GenericXMLParser {

	public static void main(String[] args){	
			/*args = new String[4];
			args[0] = "D:\\borrar\\PeriodosFraccionamiento.xml";
			args[1] = "D:\\borrar\\PeriodosFraccionamiento.csv";
			args[2] = "1001";
			args[3] = "null";*/
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PeriodosFraccionamientosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PeriodosFraccionamientosXMLParser parser = new PeriodosFraccionamientosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero de Periodos y Fraccionamiento " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Periodos y Fraccionamiento " + e.getMessage());
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
		PeriodosFraccionamientos registro = (PeriodosFraccionamientos)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo()) + ";";
		sql += StringUtils.nullToString(registro.getId().getFraccionamiento()) + ";";
		sql += StringUtils.nullToString(registro.getId().getPlazoDomiciliciacion()) + ";";
		sql += StringUtils.nullToString(registro.getPctFracc()) + ";";
		sql += StringUtils.nullToString(registro.getNumDiasReclamaPago()) + ";";
		
		return sql;
	}
	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		PeriodosFraccionamientos registro;
		PeriodosFraccionamientosId idActual;
		
		if (actual == null){
			registro = new PeriodosFraccionamientos();
			idActual = new PeriodosFraccionamientosId();
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
		}
		else{
			registro = (PeriodosFraccionamientos) actual;
			idActual = registro.getId();
		}
		
		if (this.getTagPrincipal().equals(tag)){
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "perFra")).trim().equals("")){
				idActual.setFraccionamiento(new BigDecimal(parser.getAttributeValue(null, "perFra")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numPlazo")).trim().equals("")){
				idActual.setPlazoDomiciliciacion(new BigDecimal(parser.getAttributeValue(null, "numPlazo")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "porFra")).trim().equals("")){
				registro.setPctFracc(new BigDecimal(parser.getAttributeValue(null, "porFra")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numDiasReclamaPago")).trim().equals("")){
				registro.setNumDiasReclamaPago(new BigDecimal(parser.getAttributeValue(null, "numDiasReclamaPago")));
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
