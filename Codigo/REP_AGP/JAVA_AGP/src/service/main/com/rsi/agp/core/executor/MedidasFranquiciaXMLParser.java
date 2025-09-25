package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.MedidaFranquicia;
import com.rsi.agp.dao.tables.cpl.MedidaFranquiciaId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

public class MedidasFranquiciaXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\MedidasFranquicia.xml";
		args[1] = "D:\\borrar\\MedidasFranquicia.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidasFranquiciaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidasFranquiciaXMLParser parser = new MedidasFranquiciaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Medidas Franquicia " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Medidas Franquicia " + e.getMessage());
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
		MedidaFranquicia registro = (MedidaFranquicia)reg;
		String sql = "";
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		if (registro.getModulo() != null){
			sql += registro.getModulo().getId().getCodmodulo() + ";";
		}
		else{
			sql += ";";
		}
		sql += StringUtils.nullToString(registro.getNifcif()) + ";";
		
		if (registro.getCultivo() != null) {
			sql += registro.getCultivo().getId().getCodcultivo() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getConceptoPpalModulo() != null) {
			sql += registro.getConceptoPpalModulo().getCodconceptoppalmod() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getRiesgoCubierto() != null) {
			sql += registro.getRiesgoCubierto().getId().getCodriesgocubierto() + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getRiesgoelegido()) + ";";
		sql += StringUtils.nullToString(registro.getPctfranquicia()) + ";";
		
		sql += StringUtils.nullToString(registro.getProvincia()) + ";";
		sql += StringUtils.nullToString(registro.getComarca()) + ";";
		sql += StringUtils.nullToString(registro.getTerminoMunicipal()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		MedidaFranquicia registro;
		if (actual == null){
			registro = new MedidaFranquicia();
		}
		else{
			registro = (MedidaFranquicia) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			MedidaFranquiciaId idActual = new MedidaFranquiciaId();
			
			idActual.setLineaseguroid(lineaseguroid);
			idActual.setId(new BigDecimal(id));
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				Modulo m = new Modulo();
				m.getId().setCodmodulo(parser.getAttributeValue(null, "mod").trim());
				registro.setModulo(m);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif").trim()).equals("")){
				registro.setNifcif(parser.getAttributeValue(null, "nif"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul").trim()).equals("")){
				Cultivo c = new Cultivo();
				c.getId().setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				registro.setCultivo(c);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod").trim()).equals("")){
				ConceptoPpalModulo cpm = new ConceptoPpalModulo();
				cpm.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpm);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub").trim()).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg").trim()).equals("")){
				registro.setRiesgoelegido(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq").trim()).equals("")){
				registro.setPctfranquicia(new BigDecimal(parser.getAttributeValue(null, "franq")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov").trim()).equals("")){
				registro.setProvincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com").trim()).equals("")){
				registro.setComarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term").trim()).equals("")){
				registro.setTerminoMunicipal(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sterm").trim()).equals("")){
				registro.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
		}
		return registro;
	}
}
