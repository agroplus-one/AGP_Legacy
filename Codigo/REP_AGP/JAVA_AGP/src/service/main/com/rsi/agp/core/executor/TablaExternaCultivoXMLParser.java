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
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.TablaExternaCultivo;
import com.rsi.agp.dao.tables.cpl.TablaExternaCultivoId;

public class TablaExternaCultivoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\TablaExternaEleccionCultivo.xml";
		args[1] = "D:\\borrar\\TablaExternaEleccionCultivo.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TablaExternaCultivoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TablaExternaCultivoXMLParser parser = new TablaExternaCultivoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Tabla Externa Cultivo " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Tabla Externa Cultivo " + e.getMessage());
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
		TablaExternaCultivo registro = (TablaExternaCultivo)reg;
		
		String sql = "";
		
		sql += registro.getId().getId() + ";" + registro.getId().getLineaseguroid() + ";";
		
		if (registro.getModulo() != null){
			sql += StringUtils.nullToString(registro.getCultivo().getId().getCodcultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getConceptoPpalModulo() != null){
			sql += StringUtils.nullToString(registro.getConceptoPpalModulo().getCodconceptoppalmod()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getRiesgoCubierto() != null){
			sql += StringUtils.nullToString(registro.getRiesgoCubierto().getId().getCodriesgocubierto()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getRiesgoelegido()) + ";";
		
		if (registro.getCultivo() != null){
			sql += StringUtils.nullToString(registro.getCultivo().getId().getCodcultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		TablaExternaCultivo registro;
		if (actual == null){
			registro = new TablaExternaCultivo();
		}
		else{
			registro = (TablaExternaCultivo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			TablaExternaCultivoId idActual = new TablaExternaCultivoId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo m = new Modulo();
				m.getId().setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).trim());
				registro.setModulo(m);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				ConceptoPpalModulo cpm = new ConceptoPpalModulo();
				cpm.setCodconceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpm);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")){
				registro.setRiesgoelegido(new Character(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				Cultivo cult = new Cultivo();
				cult.getId().setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				registro.setCultivo(cult);
			}
			
		}
		return registro;
	}
}
