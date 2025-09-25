package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.org.OrganizadorInformacionId;

public class OrganizadorInformacionXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(OrganizadorInformacionXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\OrganizadorInformacion.xml";
		args[1] = "D:\\borrar\\OrganizadorInformacion.csv";
		args[2] = "181";
		args[3] = "564564564";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + OrganizadorInformacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			OrganizadorInformacionXMLParser parser = new OrganizadorInformacionXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Organizador Informacion " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Organizador Informacion " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		OrganizadorInformacion registro = (OrganizadorInformacion)reg;
		
		String sql = "";
		
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodubicacion() + ";";
		sql += registro.getId().getCodconcepto() + ";" + registro.getId().getCoduso() + ";";
		sql += StringUtils.nullToString(registro.getAclaraciondeducible()) + ";" + /*fecha_baja*/ ";";
		sql += StringUtils.nullToString(registro.getVariable()) + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		OrganizadorInformacion registro;
		if (actual == null){
			registro = new OrganizadorInformacion();
		}
		else{
			registro = (OrganizadorInformacion) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			OrganizadorInformacionId idActual = new OrganizadorInformacionId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoUbicacion")).equals("")){
				idActual.setCodubicacion(new BigDecimal(parser.getAttributeValue(null, "codigoUbicacion")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoConcepto")).equals("")){
				idActual.setCodconcepto(new BigDecimal(parser.getAttributeValue(null, "codigoConcepto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codigoUso")).equals("")){
				idActual.setCoduso(new BigDecimal(parser.getAttributeValue(null, "codigoUso")));
			}
			
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aclaracionDeducible")).equals("")){
				registro.setAclaraciondeducible(parser.getAttributeValue(null, "aclaracionDeducible"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "variable")).equals("")){
				registro.setVariable(new Character(parser.getAttributeValue(null, "variable").charAt(0)));
			}
		}
		return registro;
	}
}
