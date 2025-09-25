package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.DanioCubierto;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;
import com.rsi.agp.dao.tables.cpl.GrupoTasaRiesgo;
import com.rsi.agp.dao.tables.cpl.GrupoTasaRiesgoId;
import com.rsi.agp.dao.tables.cpl.GrupoTasas;
import com.rsi.agp.dao.tables.cpl.GrupoTasasId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

public class CaracteristicasGrupoTasaRiesgoXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\CaracteristicasGrupoTasa.xml";
		args[1] = "D:\\borrar\\CaracteristicasGrupoTasa.csv";
		args[2] = "181";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CaracteristicasGrupoTasaRiesgoXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CaracteristicasGrupoTasaRiesgoXMLParser parser = new CaracteristicasGrupoTasaRiesgoXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_CARACTERISTICA_GRUPO_TASA);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Caracteristicas Grupo Tasa " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Caracteristicas Grupo Tasa " + e.getMessage());
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
		GrupoTasaRiesgo registro = (GrupoTasaRiesgo)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		if (registro.getRiesgoCubierto() != null){
			String sql = "";
			sql += registro.getId().getLineaseguroid() + ";";
			sql += registro.getGrupoTasas().getId().getCodmodulo().trim() + ";";
			sql += registro.getGrupoTasas().getId().getCodcultivo() + ";";
			sql += registro.getGrupoTasas().getId().getCodvariedad() + ";";
			sql += registro.getGrupoTasas().getId().getCodgrupotasa() + ";";
			
			if (registro.getTipoFranquicia() != null){
				sql += registro.getTipoFranquicia().getCodtipofranquicia() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getPctFranquiciaElegible() != null){
				sql += registro.getPctFranquiciaElegible().getCodpctfranquiciaeleg() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getMinimoIndemnizableElegible() != null){
				sql += registro.getMinimoIndemnizableElegible().getPctminindem() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getDanioCubierto() != null){
				sql += registro.getDanioCubierto().getCoddaniocubierto() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCalculoIndemnizacion() != null){
				sql += registro.getCalculoIndemnizacion().getCodcalculo() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getGarantizado() != null){
				sql += registro.getGarantizado().getCodgarantizado() + ";";
			}
			else{
				sql += ";";
			}
			if (registro.getCapitalAseguradoElegible() != null){
				sql += registro.getCapitalAseguradoElegible().getPctcapitalaseg() + ";";
			}
			else{
				sql += ";";
			}
			
			sql += StringUtils.nullToString(registro.getDuracionmaxgarantdias()) + ";";
			sql += StringUtils.nullToString(registro.getDiasiniciogarantias()) + ";";
			sql += StringUtils.nullToString(registro.getEstfenfingarantias()) + ";";
			sql += StringUtils.nullToString(registro.getEstfeniniciogarantias()) + ";";
			
			if (registro.getFecfingarantias() != null){
				sql += sdf.format(registro.getFecfingarantias()) + ";";
			}
			else{
				sql += ";";
			}
			
			if (registro.getFeciniciogarantias() != null){
				sql += sdf.format(registro.getFeciniciogarantias()) + ";";
			}
			else{
				sql += ";";
			}
			
			sql += StringUtils.nullToString(registro.getDuracionmaxgarantmeses()) + ";";
			sql += StringUtils.nullToString(registro.getMesesiniciogarantias()) + ";";
			sql += StringUtils.nullToString(registro.getRiesgocubiertoeleg()) + ";";
			sql += StringUtils.nullToString(registro.getConceptoppalmod()) + ";";
			
			if (registro.getRiesgoCubierto() != null){
				sql += registro.getRiesgoCubierto().getId().getCodriesgocubierto() + ";";
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
		
		GrupoTasaRiesgo registro;
		if (actual == null){
			registro = new GrupoTasaRiesgo();
		}
		else{
			registro = (GrupoTasaRiesgo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GrupoTasaRiesgoId idActual = new GrupoTasaRiesgoId();
			idActual.setId(id+"");
			idActual.setLineaseguroid(lineaseguroid);
			
			registro.setId(idActual);
			
			GrupoTasas gt = new GrupoTasas();
			GrupoTasasId gtId = new GrupoTasasId();
			gtId.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				gtId.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				gtId.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				gtId.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				gtId.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			}
			
			gt.setId(gtId);
			registro.setGrupoTasas(gt);
		}
		else if (GenericXMLParser.TAG_TIPO_FRANQUICIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoFranquicia tf = new TipoFranquicia();
				tf.setCodtipofranquicia(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				registro.setTipoFranquicia(tf);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_FRANQUICIA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				PctFranquiciaElegible pfe = new PctFranquiciaElegible();
				pfe.setCodpctfranquiciaeleg(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setPctFranquiciaElegible(pfe);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_MINIMO_INDEMNIZABLE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				MinimoIndemnizableElegible mie = new MinimoIndemnizableElegible();
				mie.setPctminindem(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setMinimoIndemnizableElegible(mie);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_DANIO_CUBIERTO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				DanioCubierto dc = new DanioCubierto();
				dc.setCoddaniocubierto(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				registro.setDanioCubierto(dc);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_CALCULO_INDEMNIZACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				CalculoIndemnizacion ca = new CalculoIndemnizacion();
				ca.setCodcalculo(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setCalculoIndemnizacion(ca);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_GARANTIZADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				Garantizado garant = new Garantizado();
				garant.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setGarantizado(garant);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_CAPITAL_ASEGURADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				CapitalAseguradoElegible cae = new CapitalAseguradoElegible();
				cae.setPctcapitalaseg(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setCapitalAseguradoElegible(cae);
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_DURACION_MAX_GARANTIA_DIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setDuracionmaxgarantdias(new BigDecimal(parser.getAttributeValue(null, "valor")));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_DIAS_INICIO_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setDiasiniciogarantias(new BigDecimal(parser.getAttributeValue(null, "valor")));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_ESTADO_FENOLOGICO_FIN_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setEstfenfingarantias(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_ESTADO_FENOLOGICO_INICIO_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setEstfeniniciogarantias(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_FECHA_FIN_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				
				try {
					registro.setFecfingarantias(sdf2.parse(parser.getAttributeValue(null, "valor")));
				} catch (ParseException e) {
					registro.setFecfingarantias(null);
				}
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_FECHA_INICIO_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				
				try {
					registro.setFeciniciogarantias(sdf2.parse(parser.getAttributeValue(null, "valor")));
				} catch (ParseException e) {
					registro.setFeciniciogarantias(null);
				}
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_DURACION_MAX_GARANTIAS_MESES.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setDuracionmaxgarantmeses(new BigDecimal(parser.getAttributeValue(null, "valor")));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_MES_INICIO_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setMesesiniciogarantias(new BigDecimal(parser.getAttributeValue(null, "valor")));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		else if (GenericXMLParser.TAG_RIESGO_CUBIERTO_ELEGIDO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setRiesgocubiertoeleg(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				
				registro.setConceptoppalmod(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
				
				RiesgoCubierto rc = new RiesgoCubierto();
				rc.getId().setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				registro.setRiesgoCubierto(rc);
			}
		}
		
		return registro;
	}
}
