package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Banco;
import com.rsi.agp.dao.tables.cgen.BancoId;
import com.rsi.agp.dao.tables.commons.Provincia;

public class BancosXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Bancos.xml";
		args[1] = "D:\\borrar\\Bancos.csv";
		args[2] = "null";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ BancosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			BancosXMLParser parser = new BancosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Bancos " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Bancos " + e.getMessage());
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
		Banco registro = (Banco)reg;
		String sql = "";
		sql += StringUtils.nullToString(registro.getId().getCodbanco()) + ";" + StringUtils.nullToString(registro.getId().getCodoficina()) + ";";
		sql += StringUtils.nullToString(registro.getId().getNumcuenta()) + ";" + StringUtils.nullToString(registro.getDigcontrol()) + ";";
		sql += StringUtils.nullToString(registro.getNombanco()) + ";" + StringUtils.nullToString(registro.getDombanco()) + ";";
		sql += StringUtils.nullToString(registro.getProvincia().getCodprovincia()) + ";" + StringUtils.nullToString(registro.getCpbanco()) + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Banco registro;
		if (actual == null){
			registro = new Banco();
		}
		else{
			registro = (Banco) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			BancoId idActual = new BancoId();

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codBan")).equals("")){
				idActual.setCodbanco(StringUtils.nullToString(parser.getAttributeValue(null, "codBan")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codOfi")).equals("")){
				idActual.setCodoficina(StringUtils.nullToString(parser.getAttributeValue(null, "codOfi")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numCuen")).equals("")){
				idActual.setNumcuenta(StringUtils.nullToString(parser.getAttributeValue(null, "numCuen")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dc")).equals("")){
				registro.setDigcontrol(StringUtils.nullToString(parser.getAttributeValue(null, "dc").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nom")).equals("")){
				registro.setNombanco(StringUtils.nullToString(parser.getAttributeValue(null, "nom").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dir")).equals("")){
				registro.setDombanco(StringUtils.nullToString(parser.getAttributeValue(null, "dir").trim()));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				Provincia prov = new Provincia();
				prov.setCodprovincia(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "prov"))));
				registro.setProvincia(prov);
			}
			else{
				Provincia prov = new Provincia();
				prov.setCodprovincia(new BigDecimal(99));
				registro.setProvincia(prov);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cp")).equals("")){
				registro.setCpbanco(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "cp").trim())));
			}
			
			registro.setId(idActual);
		}
		
		return registro;
	}
}
