package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.org.RelUsoTablaCondicionado;
import com.rsi.agp.dao.tables.org.RelUsoTablaCondicionadoId;

public class RelacionUsosTablasCondicionadoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\RelacionUsosTablasCondicionado.xml";
		args[1] = "D:\\borrar\\RelacionUsosTablasCondicionado.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RelacionUsosTablasCondicionadoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RelacionUsosTablasCondicionadoXMLParser parser = new RelacionUsosTablasCondicionadoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Relacion Usos Tablas Condicionado " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Relacion Usos Tablas Condicionado " + e.getMessage());
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
		RelUsoTablaCondicionado registro = (RelUsoTablaCondicionado)reg;
		
		String sql = "";
		
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCoduso() + ";";
		sql += registro.getId().getNumtabla() + ";" + /*fecha_baja*/ ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		RelUsoTablaCondicionado registro;
		if (actual == null){
			registro = new RelUsoTablaCondicionado();
		}
		else{
			registro = (RelUsoTablaCondicionado) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RelUsoTablaCondicionadoId idActual = new RelUsoTablaCondicionadoId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoUso")).equals("")){
				idActual.setCoduso(new BigDecimal(parser.getAttributeValue(null, "codigoUso")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numeroTabla")).equals("")){
				idActual.setNumtabla(new BigDecimal(parser.getAttributeValue(null, "numeroTabla")));
			}
			
			registro.setId(idActual);
			
		}
		return registro;
	}
}
