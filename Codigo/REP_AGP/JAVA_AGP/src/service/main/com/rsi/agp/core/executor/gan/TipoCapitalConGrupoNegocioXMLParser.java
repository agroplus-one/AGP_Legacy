package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.cpl.MedidaId;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class TipoCapitalConGrupoNegocioXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\TipoCapitalConGrupoNegocio.xml";
		args[1] = "D:\\borrar\\TipoCapitalConGrupoNegocio.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
				
		if (args.length != 4) {
			System.out.println("Usage: java " + TipoCapitalConGrupoNegocioXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			TipoCapitalConGrupoNegocioXMLParser parser = new TipoCapitalConGrupoNegocioXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de TipoCapitalConGrupoNegocio " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de TipoCapitalConGrupoNegocio " + e.getMessage());
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
		TipoCapitalConGrupoNegocio registro = (TipoCapitalConGrupoNegocio)reg;
		String sql = "";
		sql += registro.getCodtipocapital() + ";" + StringUtils.nullToString(registro.getDestipocapital()) + ";";
		sql += registro.getDiccionarioDatos().getCodconcepto() + ";" + StringUtils.nullToString(registro.getGruposNegocio().getGrupoNegocio()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		TipoCapitalConGrupoNegocio registro;
		if (actual == null){
			registro = new TipoCapitalConGrupoNegocio();
		}
		else{
			registro = (TipoCapitalConGrupoNegocio) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GruposNegocio grupoNegocio = new GruposNegocio();
			DiccionarioDatos dic = new DiccionarioDatos();
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){				
				registro.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal"))));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupNeg")).equals("")){
				grupoNegocio.setGrupoNegocio(new Character(parser.getAttributeValue(null, "grupNeg").charAt(0)));
				registro.setGruposNegocio(grupoNegocio);
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).equals("")){
				registro.setDestipocapital(StringUtils.nullToString(parser.getAttributeValue(null, "desc").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				dic.setCodconcepto(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto"))));
				registro.setDiccionarioDatos(dic);
			}
		}
		return registro;
	}
}
