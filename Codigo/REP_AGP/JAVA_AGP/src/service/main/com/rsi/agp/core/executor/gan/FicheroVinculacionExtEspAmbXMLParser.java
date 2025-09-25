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
import com.rsi.agp.dao.tables.cpl.gan.FicheroVinculacionExtEspAmbito;
import com.rsi.agp.dao.tables.cpl.gan.FicheroVinculacionExtEspAmbitoId;

public class FicheroVinculacionExtEspAmbXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
//		args = new String[4];
//		args[0] = "D:\\borrar\\sin_archivo.xml";
//		args[1] = "D:\\borrar\\sin_archivo.csv";
//		args[2] = "1045";
//		args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + FicheroVinculacionExtEspAmbXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FicheroVinculacionExtEspAmbXMLParser parser = new FicheroVinculacionExtEspAmbXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_CARACTERISTICA_GRUPO_TASA);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de FicheroVinculacionExtEspAmb" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de FicheroVinculacionExtEspAmb" + e.getMessage());
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
		FicheroVinculacionExtEspAmbito registro = (FicheroVinculacionExtEspAmbito)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getId().getCodmodulo() + ";";	
			sql += registro.getId().getCodtipocapital() + ";";
			sql += registro.getId().getCodespecie() + ";";
			sql += registro.getId().getCodregimen() + ";";	
			sql += registro.getId().getCodgruporaza() + ";";
			sql += registro.getId().getCodtipoanimal() + ";";
			sql += registro.getId().getCodprovincia() + ";";
			sql += registro.getId().getCodcomarca() + ";";
			sql += registro.getId().getCodtermino() + ";";
			sql += registro.getId().getSubtermino() + ";";
			sql += registro.getId().getConceptoppalmod() + ";";
			sql += registro.getId().getCodriesgocubierto() + ";";
			sql += registro.getId().getCodriesgocubierto() + ";";
		
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		System.out.println("rellenando objeto");
		FicheroVinculacionExtEspAmbito registro;
		if (actual == null){
			registro = new FicheroVinculacionExtEspAmbito();
		}
		else{
			registro = (FicheroVinculacionExtEspAmbito) actual;
		}
		FicheroVinculacionExtEspAmbitoId idActual = new FicheroVinculacionExtEspAmbitoId();
		if (this.getTagPrincipal().equals(tag)){			
			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				idActual.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				idActual.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				idActual.setCodregimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				idActual.setCodgruporaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				idActual.setCodtipoanimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("")){
				idActual.setCodprovincia(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "prov").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("")){
				idActual.setCodcomarca(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "com").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("")){
				idActual.setCodtermino(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "term").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals("")){
				idActual.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				idActual.setConceptoppalmod(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "cPMod").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				idActual.setCodriesgocubierto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codRCub").trim())));
			}

	}	
		registro.setId(idActual);
		return registro;
}
}
