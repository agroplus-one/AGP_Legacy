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
import com.rsi.agp.dao.tables.cpl.gan.MedidasFranquiciaGanado;
import com.rsi.agp.dao.tables.cpl.gan.MedidasFranquiciaGanadoId;


/**
 * Parser tabla 0449: Medidas de Franquicia Ganado
 */
public class MedidasFranquiciaGanadoXMLParser extends GenericXMLParser {

	public static void main(String[] args){	
			/*args = new String[4];
			args[0] = "D:\\borrar\\MedidasFranquiciaGanado.xml";
			args[1] = "D:\\borrar\\MedidasFranquiciaGanado.csv";
			args[2] = "1001";
			args[3] = "null";*/
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidasFranquiciaGanadoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidasFranquiciaGanadoXMLParser parser = new MedidasFranquiciaGanadoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero de Medidas de Franquicia Ganado " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Medidas de Franquicia Ganado " + e.getMessage());
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
	MedidasFranquiciaGanado registro = (MedidasFranquiciaGanado)reg;
	String sql = "";
	sql += registro.getId().getLineaseguroid() + ";";
	sql += StringUtils.nullToString(registro.getId().getCodmodulo()) + ";";
	sql += StringUtils.nullToString(registro.getId().getNifcif()) + ";";
	sql += StringUtils.nullToString(registro.getId().getCppalMod()) + ";";
	sql += StringUtils.nullToString(registro.getId().getCodRiesgoCub()) + ";";
	sql += StringUtils.nullToString(registro.getId().getCodRiesgoCbtoEle()) + ";";
	sql += StringUtils.nullToString(registro.getId().getPctFranq()) + ";";
	
	
	
	return sql;
}

@Override
protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
	
	MedidasFranquiciaGanado registro;
	MedidasFranquiciaGanadoId idActual;
	
	if (actual == null){
		registro = new MedidasFranquiciaGanado();
		idActual = new MedidasFranquiciaGanadoId();
		idActual.setLineaseguroid(lineaseguroid);
		registro.setId(idActual);
	}
	else{
		registro = (MedidasFranquiciaGanado) actual;
		idActual = registro.getId();
	}
	
	if (this.getTagPrincipal().equals(tag)){
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).trim().equals("")){
			idActual.setNifcif(parser.getAttributeValue(null, "nif"));
		}
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).trim().equals("")){
			idActual.setCppalMod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
		}
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).trim().equals("")){
			idActual.setCodRiesgoCub(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
		}
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).trim().equals("")){
			idActual.setCodRiesgoCbtoEle(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0));
		}
		
		if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq")).trim().equals("")){
			idActual.setPctFranq(new BigDecimal(parser.getAttributeValue(null, "franq")));
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
