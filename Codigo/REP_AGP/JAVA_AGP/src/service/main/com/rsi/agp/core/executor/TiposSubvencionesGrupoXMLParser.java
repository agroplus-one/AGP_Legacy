package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupoId;

public class TiposSubvencionesGrupoXMLParser extends GenericXMLParser {
	
	BigDecimal plan;
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\TiposSubvencionesGrupo.xml";
		args[1] = "D:\\borrar\\TiposSubvencionesGrupo.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + TiposSubvencionesGrupoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TiposSubvencionesGrupoXMLParser parser = new TiposSubvencionesGrupoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Tipos Subvenciones Grupo " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Tipos Subvenciones Grupo " + e.getMessage());
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
		SubvencionesGrupo registro = (SubvencionesGrupo)reg;
		String sql = "";
		sql += registro.getId().getPlan() + ";" + registro.getId().getGruposubv() + ";";
		sql += registro.getId().getCodtiposubv() + ";";
		sql += StringUtils.nullToString(registro.getPrioridadaplica()) + ";";
		
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		SubvencionesGrupo registro;
		if (actual == null){
			registro = new SubvencionesGrupo();
		}
		else{
			registro = (SubvencionesGrupo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			SubvencionesGrupoId idActual = new SubvencionesGrupoId();
			idActual.setPlan(plan);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "gruSubv")).equals("")){
				idActual.setGruposubv(new BigDecimal(parser.getAttributeValue(null, "gruSubv")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipSubv")).equals("")){
				idActual.setCodtiposubv(new BigDecimal(parser.getAttributeValue(null, "tipSubv")));
			}
			
			registro.setId(idActual);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prio")).equals("")){
				registro.setPrioridadaplica(new BigDecimal(parser.getAttributeValue(null, "prio")));
			}
		}
		else if (GenericXMLParser.TAG_CONDICIONADO_PLAN.equals(tag)){
			plan = new BigDecimal(parser.getAttributeValue(null, "plan"));
		}
		return registro;
	}
}
