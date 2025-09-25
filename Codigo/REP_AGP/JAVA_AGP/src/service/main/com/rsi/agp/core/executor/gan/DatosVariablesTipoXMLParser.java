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
import com.rsi.agp.dao.tables.cgen.BancoId;
import com.rsi.agp.dao.tables.cgen.ConceptosDeducibles;
import com.rsi.agp.dao.tables.cgen.ConceptosDeduciblesId;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneralId;
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModulo;
import com.rsi.agp.dao.tables.cgen.FactoresIncluidosConceptoPrincipalModuloId;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.cgen.TipoCapitalConGrupoNegocio;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.cpl.gan.DatosVariablesTipo;
import com.rsi.agp.dao.tables.cpl.gan.DatosVariablesTipoId;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.EspecieId;
import com.rsi.agp.dao.tables.cpl.gan.NumMinAnimalesColmenas;
import com.rsi.agp.dao.tables.cpl.gan.NumMinAnimalesColmenasId;
import com.rsi.agp.dao.tables.cpl.gan.PctAdaptacionRiesgo;
import com.rsi.agp.dao.tables.cpl.gan.PctAdaptacionRiesgoId;
import com.rsi.agp.dao.tables.cpl.gan.PctValorMaxExplotacion;
import com.rsi.agp.dao.tables.cpl.gan.PctValorMaxExplotacionId;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.RelacionAnimales;
import com.rsi.agp.dao.tables.cpl.gan.RelacionAnimalesId;

public class DatosVariablesTipoXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*
		args = new String[4];
		args[0] = "D:\\borrar\\DatosVariablesTipoCapitalEspecieAmbito.xml";
		args[1] = "D:\\borrar\\DatosVariablesTipoCapitalEspecieAmbito.csv";
		args[2] = "1045";
		args[3] = new Date().toString();
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + DatosVariablesTipoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			DatosVariablesTipoXMLParser parser = new DatosVariablesTipoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de DatosVariablesTipoCapitalEspecieAmbito " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de DatosVariablesTipoCapitalEspecieAmbito " + e.getMessage());
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
		DatosVariablesTipo registro = (DatosVariablesTipo)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodmodulo() + ";" + registro.getId().getCodtipocapital() + ";";
		sql += registro.getId().getCodespecie() + ";" + registro.getId().getCodregimen() + ";" + registro.getId().getCodgruporaza() + ";";
		sql += registro.getId().getCodtipoanimal() + ";" + registro.getId().getCodprovincia() + ";" + registro.getId().getCodcomarca() + ";";
		sql += registro.getId().getCodtermino()+ ";" + registro.getId().getSubtermino() + ";" + registro.getId().getCodconcepto() + ";";
		if (null != registro.getObligatoriedad())
			sql +=  registro.getObligatoriedad() + ";";
		else
			sql += ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		DatosVariablesTipo registro;
		if (actual == null){
			registro = new DatosVariablesTipo();
		}
		else{
			registro = (DatosVariablesTipo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			DatosVariablesTipoId datVarTipoId = new DatosVariablesTipoId();
			datVarTipoId.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				datVarTipoId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				datVarTipoId.setCodtipocapital(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
				datVarTipoId.setCodespecie(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "esp").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
				datVarTipoId.setCodregimen(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "reg").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
				datVarTipoId.setCodgruporaza(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
				datVarTipoId.setCodtipoanimal(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("")){
				datVarTipoId.setCodprovincia(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "prov").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("")){
				datVarTipoId.setCodcomarca(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "com").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("")){
				datVarTipoId.setCodtermino(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "term").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals("")){
				datVarTipoId.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codCpto")).equals("")){
				datVarTipoId.setCodconcepto(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "codCpto").trim())));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "obl")).equals("")){
				registro.setObligatoriedad(new Long(StringUtils.nullToString(parser.getAttributeValue(null, "obl").trim())));
			}
			registro.setId(datVarTipoId);
		}
		return registro;
	}
}
