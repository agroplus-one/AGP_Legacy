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
import com.rsi.agp.dao.tables.cpl.gan.ImporteMinimoRecargoAval;
import com.rsi.agp.dao.tables.cpl.gan.ImporteMinimoRecargoAvalId;

/**
 * Parser tabla 0447: Importe Minimo Recargo Aval
 */
public class ImporteMinimoRecargoAvalXMLParser  extends GenericXMLParser {
	
	public static void main(String[] args){	
			//args = new String[4];
			/*args[0] = "D:\\borrar\\ImporteMinimoRecargoAval.xml";
			args[1] = "D:\\borrar\\ImporteMinimoRecargoAval.csv";
			args[2] = "1001";
			args[3] = "null";*/
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ImporteMinimoRecargoAvalXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			//System.out.println("38 - Creamos el objeto ");
			ImporteMinimoRecargoAvalXMLParser parser = new ImporteMinimoRecargoAvalXMLParser();
			//System.out.println("40 - Asignamos Tag principal");
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
//			System.out.println("42 -" + args[0]);
//			System.out.println("43 -" + args[1]);
//			System.out.println("44 -" + args[2]);
//			System.out.println("45 -" + args[3]);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
//			System.out.println("47 - procesarFichero");
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero de Importe Minimo Recargo Aval " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Importe Minimo Recargo Aval " + e.getMessage());
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
		ImporteMinimoRecargoAval registro = (ImporteMinimoRecargoAval)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += StringUtils.nullToString(registro.getId().getCodmodulo()) + ";";
		sql += StringUtils.nullToString(registro.getId().getImpAvalDesde()) + ";";
		sql += StringUtils.nullToString(registro.getId().getImpAvalHasta()) + ";";
		sql += StringUtils.nullToString(registro.getImpMinRecAval()) + ";";
		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		ImporteMinimoRecargoAval registro;
		ImporteMinimoRecargoAvalId idActual;
		
		if (actual == null){
			registro = new ImporteMinimoRecargoAval();
			idActual = new ImporteMinimoRecargoAvalId();
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
		}
		else{
			registro = (ImporteMinimoRecargoAval) actual;
			idActual = registro.getId();
		}
		
		if (this.getTagPrincipal().equals(tag)){
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "impAvalD")).trim().equals("")){
				idActual.setImpAvalDesde(new BigDecimal(parser.getAttributeValue(null, "impAvalD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "impAvalH")).trim().equals("")){
				idActual.setImpAvalHasta(new BigDecimal(parser.getAttributeValue(null, "impAvalH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "impMinRecargoAval")).trim().equals("")){
				registro.setImpMinRecAval(new BigDecimal(parser.getAttributeValue(null, "impMinRecargoAval")));
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
