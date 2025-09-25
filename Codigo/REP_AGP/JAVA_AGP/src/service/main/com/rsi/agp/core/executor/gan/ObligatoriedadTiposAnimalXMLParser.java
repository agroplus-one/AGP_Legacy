package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.ObligatoriedadTiposAnimal;
import com.rsi.agp.dao.tables.cpl.gan.ObligatoriedadTiposAnimalId;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;

public class ObligatoriedadTiposAnimalXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		
		//args = new String[4];
		//args[0] = "D:\\borrar\\ObligatoriedadTiposAnimal.xml";
		//args[1] = "D:\\borrar\\ObligatoriedadTiposAnimal.csv";
		//args[2] = "1045";
		//args[3] = "d/MM/yyyy";
		
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + ObligatoriedadTiposAnimalXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			ObligatoriedadTiposAnimalXMLParser parser = new ObligatoriedadTiposAnimalXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de ObligTipoAnimalGanado " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de ObligTipoAnimalGanado " + e.getMessage());
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
		ObligatoriedadTiposAnimal registro = (ObligatoriedadTiposAnimal)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" +
			   registro.getModulo().getId().getCodmodulo() + ";" +
			   registro.getTipoCapitalConGrupoNegocio().getCodtipocapital() + ";" + 
			   registro.getEspecie().getId().getCodespecie() + ";" +
			   registro.getRegimenManejo().getId().getCodRegimen() + ";" + 
			   registro.getGruposRazas().getId().getCodGrupoRaza() + ";" +
			   registro.getTiposAnimalGanado().getId().getCodTipoAnimal()+ ";";
		
		if (null != registro.getObligatoriedad()) sql += registro.getObligatoriedad() + ";";
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		ObligatoriedadTiposAnimal registro;
		if (actual == null){
			registro = new ObligatoriedadTiposAnimal();
		}
		else{
			registro = (ObligatoriedadTiposAnimal) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			ObligatoriedadTiposAnimalId obligTiposAni = new ObligatoriedadTiposAnimalId();
			obligTiposAni.setLineaseguroid(lineaseguroid);
			/*if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){				
				obligTiposAni.setCodTipoAnimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim"))));
			}*/
			
			registro.setId(obligTiposAni);
			RegimenManejo regMan = new RegimenManejo();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				regMan.getId().setCodRegimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			registro.setRegimenManejo(regMan);
			
			GruposRazas gruRaza = new GruposRazas();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				gruRaza.getId().setCodGrupoRaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			registro.setGruposRazas(gruRaza);
			
			Especie especie = new Especie();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				especie.getId().setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			registro.setEspecie(especie);
			
			Modulo modulo = new Modulo();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				modulo.getId().setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()));
			}
			registro.setModulo(modulo);
			
			TipoCapitalConGrupoNegocio tipCap = new TipoCapitalConGrupoNegocio();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				tipCap.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			registro.setTipoCapitalConGrupoNegocio(tipCap);
			
			
			TiposAnimalGanado tipAni = new TiposAnimalGanado();
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				tipAni.getId().setCodTipoAnimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			registro.setTiposAnimalGanado(tipAni);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "obl")).equals("")){
				registro.setObligatoriedad(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "obl").trim())));
			}
			
					
		}
		return registro;
	}
}
