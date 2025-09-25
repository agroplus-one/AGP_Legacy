package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;
import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.EspecieId;
import com.rsi.agp.dao.tables.cpl.gan.MedidaG;
import com.rsi.agp.dao.tables.cpl.gan.MedidaGId;


public class MedidaGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\MedidasGanado.xml";
		args[1] = "D:\\borrar\\MedidasGanado.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		*/
		if (args.length != 4) {
			System.out.println("Usage: java " + MedidaGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			MedidaGXMLParser parser = new MedidaGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de MedidaG" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de MedidaG" + e.getMessage());
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
		MedidaG registro = (MedidaG)reg;
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			if (registro.getModulo() != null)
				sql += registro.getModulo().getId().getCodmodulo() + ";";
			else
				sql += ";";
			sql += registro.getNifcif() + ";";
			if (registro.getGruposNegocio() != null)
				sql += registro.getGruposNegocio().getGrupoNegocio() + ";";
			else{
				sql += ";";
			}
			if (registro.getespecie() != null)
				sql += registro.getespecie().getId().getCodespecie() + ";";
			else{
				sql += ";";
			}
			if (registro.getespecieDeducible() != null)
				sql += registro.getespecieDeducible().getId().getCodespecie() + ";";
			else{
				sql += ";";
			}
			sql += registro.getCodtipomedida() + ";";
			
			if (registro.getPctmedida() != null)
				sql += registro.getPctmedida() + ";";
			else{
				sql += ";";
			}
			if (registro.getCoefmedida() != null)
				sql += registro.getCoefmedida() + ";";
			else{
				sql += ";";
			}
			sql += StringUtils.nullToString(registro.getId().getId()) + ";";
			return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		MedidaG registro;
		if (actual == null){
			registro = new MedidaG();
		}
		else{
			registro = (MedidaG) actual;
		}
		MedidaGId idActual = new MedidaGId();
		if (this.getTagPrincipal().equals(tag)){					
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				Modulo modd = new Modulo();
				ModuloId moddId = new ModuloId();
				moddId.setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
				modd.setId(moddId);
				registro.setModulo(modd);
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).equals("")){
				registro.setNifcif(StringUtils.nullToString(parser.getAttributeValue(null, "nif").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupNeg")).equals("")){
				GruposNegocio grupNeg = new GruposNegocio();
				grupNeg.setGrupoNegocio(new Character(parser.getAttributeValue(null, "grupNeg").charAt(0)));
				registro.setGruposNegocio(grupNeg);
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				Especie espe = new Especie();
				EspecieId espId = new EspecieId();
				espId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
				espe.setId(espId);
				registro.setespecie(espe);
			}			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "espD")).equals("")){
				Especie espeD = new Especie();
				EspecieId espId = new EspecieId();
				espId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "espD").trim())));
				espeD.setId(espId);
				registro.setespecieDeducible(espeD);
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMed")).equals("")){
				registro.setCodtipomedida(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipMed").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "med")).equals("")){
				registro.setPctmedida(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "med").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "coefMed")).equals("")){
				registro.setCoefmedida(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "coefMed").trim())));
			}
	}	
		registro.setId(idActual);
		return registro;
}
}
