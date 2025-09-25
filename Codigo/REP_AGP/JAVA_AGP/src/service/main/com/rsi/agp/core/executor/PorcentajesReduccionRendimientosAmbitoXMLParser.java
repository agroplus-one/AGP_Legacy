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
import com.rsi.agp.dao.tables.cpl.ReduccionRdtoAmbito;
import com.rsi.agp.dao.tables.cpl.ReduccionRdtoAmbitoId;

public class PorcentajesReduccionRendimientosAmbitoXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(PorcentajesReduccionRendimientosAmbitoXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\borrar\\PorcentajesReduccionRendimientosAmbito.xml";
		args[1] = "D:\\borrar\\PorcentajesReduccionRendimientosAmbito.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ PorcentajesReduccionRendimientosAmbitoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PorcentajesReduccionRendimientosAmbitoXMLParser parser = new PorcentajesReduccionRendimientosAmbitoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de Porcentajes Reduccion Rendimientos Ambito " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de Porcentajes Reduccion Rendimientos Ambito " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		ReduccionRdtoAmbito registro = (ReduccionRdtoAmbito)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodreducrdto() + ";" + registro.getId().getCodprovincia() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += StringUtils.nullToString(registro.getPctreduccion()) + ";" + registro.getId().getCodcomarca() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		ReduccionRdtoAmbito registro;
		if (actual == null){
			registro = new ReduccionRdtoAmbito();
		}
		else{
			registro = (ReduccionRdtoAmbito) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			ReduccionRdtoAmbitoId idActual = new ReduccionRdtoAmbitoId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRedRdto")).equals("")){
				idActual.setCodreducrdto(new BigDecimal(parser.getAttributeValue(null, "codRedRdto")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				idActual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				idActual.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				idActual.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				idActual.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				idActual.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				idActual.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idActual.setSubtermino(new Character('-'));
			}
			else{
				idActual.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			registro.setId(idActual);

			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reduc")).equals("")){
				registro.setPctreduccion(new BigDecimal(parser.getAttributeValue(null, "reduc")));
			}
			
		}
		return registro;
	}
}
