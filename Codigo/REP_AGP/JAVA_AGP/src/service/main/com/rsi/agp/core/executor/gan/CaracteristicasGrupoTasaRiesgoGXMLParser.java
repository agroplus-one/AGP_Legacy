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
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasaRiesgoG;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasaRiesgoGId;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasasG;
import com.rsi.agp.dao.tables.cpl.gan.GrupoTasasGId;


public class CaracteristicasGrupoTasaRiesgoGXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		/*
		//TEMPORAL
		args = new String[4];
		args[0] = "D:\\borrar\\CaracteristicasGrupoTasaRiesgo.xml";
		args[1] = "D:\\borrar\\CaracteristicasGrupoTasaRiesgo.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";
		//FIN TEMPORAL
		*/
		if (args.length != 4) {
			System.out.println("Usage: java " + CaracteristicasGrupoTasaRiesgoGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CaracteristicasGrupoTasaRiesgoGXMLParser parser = new CaracteristicasGrupoTasaRiesgoGXMLParser();
			parser.setTagPrincipal(TAG_CARACTERISTICA_GRUPO_TASA);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Caracteristicas Grupo Tasa G" + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Caracteristicas Grupo Tasa G" + e.getMessage());
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
		//System.out.println("pintando sql");
		GrupoTasaRiesgoG registro = (GrupoTasaRiesgoG)reg;
		if (registro.getRiesgoCubierto() != null){
			//System.out.println("ENTRANDOO sql");
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getGrupoTasasG().getId().getCodmodulo() + ";";	
			sql += registro.getGrupoTasasG().getId().getCodtipocapital() + ";";	
			sql += registro.getGrupoTasasG().getId().getCodespecie() + ";";	
			sql += registro.getGrupoTasasG().getId().getCodregimen() + ";";	
			sql += registro.getGrupoTasasG().getId().getCodgruporaza() + ";";
			sql += registro.getGrupoTasasG().getId().getCodtipoanimal() + ";";
		    sql += registro.getGrupoTasasG().getId().getCodgrupotasa() + ";";
			
			if (registro.getCpmcalcindmniz() != null){
				sql += registro.getCpmcalcindmniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRccalcindemniz() != null){
				sql += registro.getRccalcindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValcalcindemniz() != null){
				sql += registro.getValcalcindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmminimoindemniz() != null){
				sql += registro.getCpmminimoindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRcminindemniz() != null){
				sql += registro.getRcminindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValminindemniz() != null){
				sql += registro.getValminindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmtipofranquicia() != null){
				sql += registro.getCpmtipofranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRctipofranquicia() != null){
				sql += registro.getRctipofranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValtipofranquicia() != null){
				sql += registro.getValtipofranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmpctfranquicia() != null){
				sql += registro.getCpmpctfranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRcpctfranquicia() != null){
				sql += registro.getRcpctfranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValpctfranquicia() != null){
				sql += registro.getValpctfranquicia() + ";";
			}
			else{
				sql += ";";
			}
			
			if (registro.getCpmgarantizado() != null){
				sql += registro.getCpmgarantizado() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRcgarantizado() != null){
				sql += registro.getRcgarantizado() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValgarantizado() != null){
				sql += registro.getValgarantizado() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmlimindemniz() != null){
				sql += registro.getCpmlimindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRclimindemniz() != null){
				sql += registro.getRclimindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getVallimindemniz() != null){
				sql += registro.getVallimindemniz() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmperiodogarant() != null){
				sql += registro.getCpmperiodogarant() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRcperiodogarant() != null){
				sql += registro.getRcperiodogarant() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValperiodogarant() != null){
				sql += registro.getValperiodogarant() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCpmriesgocubeleg() != null){
				sql += registro.getCpmriesgocubeleg() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getRcriesgocubeleg() != null){
				sql += registro.getRcriesgocubeleg() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getValriesgocubeleg() != null){
				sql += registro.getValriesgocubeleg() + ";";
			}
			else{
				sql += ";";
			}
		
			sql += StringUtils.nullToString(registro.getId().getId()) + ";";
	
			return sql;
		}
		return "";
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		//System.out.println("rellenando objeto");
		GrupoTasaRiesgoG registro;
		if (actual == null){
			registro = new GrupoTasaRiesgoG();
		}
		else{
			registro = (GrupoTasaRiesgoG) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GrupoTasaRiesgoGId idActual = new GrupoTasaRiesgoGId();
			idActual.setId(id+"");
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			GrupoTasasG gt = new GrupoTasasG();
			GrupoTasasGId gtId = new GrupoTasasGId();
			gtId.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")){
				gtId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				
				gtId.setCodtipocapital(new Long(parser.getAttributeValue(null, "tipCptal")));
		
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")){
	
				gtId.setCodespecie(new Long(parser.getAttributeValue(null, "esp")));
	
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "reg")).equals("")){
		
				gtId.setCodregimen(new Long(parser.getAttributeValue(null, "reg")));

			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupRaza")).equals("")){
			
				gtId.setCodgruporaza(new Long(parser.getAttributeValue(null, "grupRaza")));

			}		
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipAnim")).equals("")){
	
				gtId.setCodtipoanimal(new Long(parser.getAttributeValue(null, "tipAnim")));
		
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				gtId.setCodgrupotasa(new Long(parser.getAttributeValue(null, "grupoTasa")));
			}
			gt.setId(gtId);
			registro.setGrupoTasasG(gt);
		}
		else if (GenericXMLParser.TAG_CALCULO_INDEMNIZACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmcalcindmniz(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRccalcindemniz(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValcalcindemniz(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_MINIMO_INDEMNIZABLE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmminimoindemniz(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRcminindemniz(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValminindemniz(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_TIPO_FRANQUICIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmtipofranquicia(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRctipofranquicia(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValtipofranquicia(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_FRANQUICIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmpctfranquicia(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRcpctfranquicia(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValpctfranquicia(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_GARANTIZADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmgarantizado(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRcgarantizado(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValgarantizado(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_LIMITE_INDEMNIZACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmlimindemniz(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRclimindemniz(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setVallimindemniz(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_PERIODO_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmperiodogarant(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRcperiodogarant(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValperiodogarant(new Long(parser.getAttributeValue(null, "valor")));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_RIESGO_CUBIERTO_ELEGIDO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCpmriesgocubeleg(new Long(parser.getAttributeValue(null, "cPMod")));
				registro.setRcriesgocubeleg(new Long(parser.getAttributeValue(null, "codRCub")));
				registro.setValriesgocubeleg(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}		
		return registro;
	}
}
