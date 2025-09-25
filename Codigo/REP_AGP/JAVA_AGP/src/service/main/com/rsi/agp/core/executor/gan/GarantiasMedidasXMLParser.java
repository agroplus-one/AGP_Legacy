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
import com.rsi.agp.dao.tables.cpl.gan.GarantiasMedidas;
import com.rsi.agp.dao.tables.cpl.gan.GarantiasMedidasId;


/**
 * Parser tabla 0448: Garantias aplicables a medidas
 */

public class GarantiasMedidasXMLParser  extends GenericXMLParser {

	public static void main(String[] args){	
			/*args = new String[4];
			args[0] = "D:\\borrar\\GarantiasAplicablesMedidas.xml";
			args[1] = "D:\\borrar\\GarantiasAplicablesMedidas.csv";
			args[2] = "1001";
			args[3] = "null";*/
		
		if (args.length != 4) {
			System.out.println("Usage: java " + GarantiasMedidasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			GarantiasMedidasXMLParser parser = new GarantiasMedidasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero de Garantias aplicables a medidas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Garantias aplicables a medidas " + e.getMessage());
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
	GarantiasMedidas registro = (GarantiasMedidas)reg;
	String sql = "";
	sql += registro.getId().getLineaseguroid() + ";";
	sql += StringUtils.nullToString(registro.getId().getCodmodulo()) + ";";
	sql += StringUtils.nullToString(registro.getId().getCppalMod()) + ";";
	sql += StringUtils.nullToString(registro.getId().getCodRiesgoCub()) + ";";
	
	
	
	return sql;
}

@Override
protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
	
	GarantiasMedidas registro;
	GarantiasMedidasId idActual;
	
	if (actual == null){
		registro = new GarantiasMedidas();
		idActual = new GarantiasMedidasId();
		idActual.setLineaseguroid(lineaseguroid);
		registro.setId(idActual);
	}
	else{
		registro = (GarantiasMedidas) actual;
		idActual = registro.getId();
	}
	
	if (this.getTagPrincipal().equals(tag)){
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).trim().equals("")){
			idActual.setCppalMod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
		}
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).trim().equals("")){
			idActual.setCodRiesgoCub(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
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
