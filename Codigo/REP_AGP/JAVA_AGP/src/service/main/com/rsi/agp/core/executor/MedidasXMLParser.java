package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.MedidaId;

public class MedidasXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Medidas.xml";
		args[1] = "D:\\borrar\\Medidas.csv";
		args[2] = "181";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidasXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidasXMLParser parser = new MedidasXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de medidas " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de medidas " + e.getMessage());
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
		Medida registro = (Medida)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getNifasegurado() + ";";
		
		if (registro.getTipomedidaclub() != null){
			sql += registro.getTipomedidaclub() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getPctbonifrecargo() != null){
			sql += registro.getPctbonifrecargo() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getAplicacionrdto() != null){
			sql += registro.getAplicacionrdto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCoefrdtomaxaseg() != null){
			sql += registro.getCoefrdtomaxaseg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTablardto() != null){
			sql += registro.getTablardto();
		}
		else{
			sql += ";";
		}
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		Medida registro;
		if (actual == null){
			registro = new Medida();
		}
		else{
			registro = (Medida) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			MedidaId idMed = new MedidaId();
			idMed.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).trim().equals("")){
				idMed.setNifasegurado(parser.getAttributeValue(null, "nif"));
			}
			else{
				idMed.setNifasegurado("-");
			}
			
			registro.setId(idMed);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMedClub")).equals("")){
				registro.setTipomedidaclub(new Character(parser.getAttributeValue(null, "tipMedClub").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "bonRec")).equals("")){
				registro.setPctbonifrecargo(new BigDecimal(parser.getAttributeValue(null, "bonRec")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "apRdtos")).equals("")){
				registro.setAplicacionrdto(new Character(parser.getAttributeValue(null, "apRdtos").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "coefRdtosMaxAseg")).equals("")){
				registro.setCoefrdtomaxaseg(new BigDecimal(parser.getAttributeValue(null, "coefRdtosMaxAseg")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tabRdtos")).equals("")){
				registro.setTablardto(new BigDecimal(parser.getAttributeValue(null, "tabRdtos")));
			}
		}
		return registro;
	}
}
