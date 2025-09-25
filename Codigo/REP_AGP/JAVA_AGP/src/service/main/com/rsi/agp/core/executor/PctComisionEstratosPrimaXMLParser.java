package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.PctComisionEstratosPrima;
import com.rsi.agp.dao.tables.cpl.PctComisionEstratosPrimaId;

public class PctComisionEstratosPrimaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\PorcentajeComisionEstratosPrima.xml";
		args[1] = "D:\\borrar\\PorcentajeComisionEstratosPrima.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PctComisionEstratosPrimaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PctComisionEstratosPrimaXMLParser parser = new PctComisionEstratosPrimaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Porcentaje Comision Estratos Prima " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Porcentaje Comision Estratos Prima " + e.getMessage());
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
		PctComisionEstratosPrima registro = (PctComisionEstratosPrima)reg;
		String sql = "";
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		sql += StringUtils.nullToString(registro.getPrimabasedesde()) + ";";
		sql += StringUtils.nullToString(registro.getPrimabasehasta()) + ";" + StringUtils.nullToString(registro.getPctaplicar()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		PctComisionEstratosPrima registro;
		if (actual == null){
			registro = new PctComisionEstratosPrima();
		}
		else{
			registro = (PctComisionEstratosPrima) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PctComisionEstratosPrimaId idActual = new PctComisionEstratosPrimaId();
			
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "primaBaseComD")).equals("")){
				registro.setPrimabasedesde(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "primaBaseComD"))));
			}

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "primaBaseComH")).equals("")){
				registro.setPrimabasehasta(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "primaBaseComH"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aplicComGas")).equals("")){
				registro.setPctaplicar(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "aplicComGas"))));
			}
		}
		return registro;
	}
}
