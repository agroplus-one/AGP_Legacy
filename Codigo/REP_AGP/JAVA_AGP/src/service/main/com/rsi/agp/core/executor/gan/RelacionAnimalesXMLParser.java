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
import com.rsi.agp.dao.tables.cpl.gan.RelacionAnimales;
import com.rsi.agp.dao.tables.cpl.gan.RelacionAnimalesId;

public class RelacionAnimalesXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		
		/*args = new String[4];
		args[0] = "D:\\borrar\\RelacionAnimales.xml";
		args[1] = "D:\\borrar\\RelacionAnimales.csv";
		args[2] = "1045";
		args[3] = new Date().toString();*/
		
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RelacionAnimalesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			RelacionAnimalesXMLParser parser = new RelacionAnimalesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de RelacionAnimales " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de RelacionAnimales " + e.getMessage());
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
		RelacionAnimales registro = (RelacionAnimales)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getModulo().getId().getCodmodulo() 
				+ ";" + registro.getTipoCapitalConGrupoNegocio().getCodtipocapital() + ";";
		
		sql += registro.getEspecie().getId().getCodespecie() + ";" + registro.getRegimenManejo().getId().getCodRegimen() 
				+ ";" + registro.getGruposRazas().getId().getCodGrupoRaza() + ";";
		
		
		if (null != registro.getTipoAnimalBase())
			sql += registro.getTipoAnimalBase() + ";";
		else
			sql += ";";
		if (null != registro.getTipoAnimalBaseD())
			sql += registro.getTipoAnimalBaseD() + ";";
		else
			sql += ";";
		
		if (null != registro.getTipoAnimalRel())
			sql += registro.getTipoAnimalRel() + ";";
		else
			sql += ";";
		if (null != registro.getTipoAnimalRelD())
			sql += registro.getTipoAnimalRelD() + ";";
		else
			sql += ";";
		sql+= registro.getTipoGanaderia() + ";";
		if (null != registro.getPctMinRelBase())
			sql+= registro.getPctMinRelBase() + ";";
		else
			sql+= ";";
		if (null != registro.getNumAniRelMin())
			sql += registro.getNumAniRelMin()+ ";";
		else
			sql+= ";";
		
		sql += registro.getId().getId() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		RelacionAnimales registro;
		if (actual == null){
			registro = new RelacionAnimales();
		}
		else{
			registro = (RelacionAnimales) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RelacionAnimalesId relId = new RelacionAnimalesId();
			relId.setLineaseguroid(lineaseguroid);
			relId.setId(new Long(id));
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				registro.getModulo().getId().setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				registro.getTipoCapitalConGrupoNegocio().setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				registro.getEspecie().getId().setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				registro.getRegimenManejo().getId().setCodRegimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				registro.getGruposRazas().getId().setCodGrupoRaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimB")).equals("")){
				registro.setTipoAnimalBase(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimB").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimBD")).equals("")){
				registro.setTipoAnimalBaseD(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimBD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimRel")).equals("")){
				registro.setTipoAnimalRel(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimRel").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimRelD")).equals("")){
				registro.setTipoAnimalRelD(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnimRelD").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tGanad")).equals("")){
				registro.setTipoGanaderia(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tGanad").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "pMinRel")).equals("")){
				BigDecimal l = new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "pMinRel").trim()));
				registro.setPctMinRelBase(l.longValue());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nAnimRelMin")).equals("")){
				registro.setNumAniRelMin(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "nAnimRelMin").trim())));
			}
			registro.setId(relId);
		}
		return registro;
	}
}
