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
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSC;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizadoSCId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class AsegAutorizadosXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\AseguradosAutorizacionContratacion.xml";
		args[1] = "D:\\borrar\\AseguradosAutorizacionContratacion.csv";
		args[2] = "4963";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + AsegAutorizadosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			AsegAutorizadosXMLParser parser = new AsegAutorizadosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			System.out.println(args[0]);
			System.out.println(args[1]);
			System.out.println(args[2]);
			System.out.println(args[3]);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
			
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de asegurados autorizados " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de asegurados autorizados " + e.getMessage());
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
		AseguradoAutorizadoSC registro = (AseguradoAutorizadoSC)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getModulo().getId().getCodmodulo().trim() + ";";
		sql += registro.getNifasegurado().trim() + ";";
		
		
		
		if (null != registro.getVariedad() && null != registro.getVariedad().getId() && null != registro.getVariedad().getId().getCodcultivo()) {
			sql += registro.getVariedad().getId().getCodcultivo() +";";
		}else {
			sql +="999;";
		}
		if (null != registro.getVariedad() && null != registro.getVariedad().getId() && null != registro.getVariedad().getId().getCodvariedad()) {
			sql += registro.getVariedad().getId().getCodvariedad()  +";";
		}else {
			sql +="999;";
		}
		
		
		sql += StringUtils.nullToString(registro.getCodgarantizado()) + ";";
		sql += StringUtils.nullToString(registro.getCodnivelriesgo()) + ";";
		sql += StringUtils.nullToString(registro.getCoefsobrerdtos()) + ";";
		sql += StringUtils.nullToString(registro.getRdtopermitido()) + ";";
		sql += StringUtils.nullToString(registro.getProvincia().getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCpmodffg()) + ";";
		if (registro.getRiesgoCubierto() != null && registro.getRiesgoCubierto().getId() != null)
			sql += registro.getRiesgoCubierto().getId().getCodriesgocubierto() + ";";
		else
			sql += ";";
		if (registro.getFecfgarant() != null)
			sql += sdf.format(registro.getFecfgarant()) + ";";
		else
			sql += ";";
		sql += StringUtils.nullToString(registro.getCpmodcg()) + ";";
		sql += StringUtils.nullToString(registro.getCodrcubcg()) + ";";
		//VALORCG
		sql += ";";
		//DESCCG
		sql += ";";
		//ID
		sql += registro.getId().getId() + ";";
		
		if (registro.getRcubeleg() != null)
			sql += registro.getRcubeleg() + ";";
		else
			sql += ";";
		
		if (registro.getCpmodrcub() != null)
			sql += registro.getCpmodrcub() + ";";
		else
			sql += ";";
		
		if (registro.getCodrcubrcub() != null)
			sql += registro.getCodrcubrcub() + ";";
		else
			sql += ";";
		
		sql += StringUtils.nullToString(registro.getValfacrceleg()) + ";";
		sql += StringUtils.nullToString(registro.getAplicrend()) + ";";
		sql += StringUtils.nullToString(registro.getGrupovarietal()) + ";";
		sql += StringUtils.nullToString(registro.getSistcult()) + ";";
		sql += StringUtils.nullToString(registro.getTipmcoplan()) + ";";
		if(null!=registro.getCaractexplot()){
			sql += registro.getCaractexplot().toString() + ";";
		}else{
			sql += ";";
		}
		if(null!=registro.getRevisionrdto()){
			sql += registro.getRevisionrdto() + ";";
		}else{
			sql += ";";
		}
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		
		AseguradoAutorizadoSC registro;
		if (actual == null){
			registro = new AseguradoAutorizadoSC();
		}
		else{
			registro = (AseguradoAutorizadoSC) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			AseguradoAutorizadoSCId idAseg = new AseguradoAutorizadoSCId();
			idAseg.setId(new Long(id));
			idAseg.setLineaseguroid(lineaseguroid);
			
			registro.setId(idAseg);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));
				registro.setModulo(modulo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nif")).equals("")){
				registro.setNifasegurado(parser.getAttributeValue(null, "nif"));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				Variedad var = new Variedad();
				VariedadId varId = new VariedadId();
				varId.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
				varId.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
				varId.setLineaseguroid(lineaseguroid);
				var.setId(varId);
				registro.setVariedad(var);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "nivRies")).equals("")){
				registro.setCodnivelriesgo(new BigDecimal(parser.getAttributeValue(null, "nivRies")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "coefRdtos")).equals("")){
				registro.setCoefsobrerdtos(new BigDecimal(parser.getAttributeValue(null, "coefRdtos")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "rdtoPerm")).equals("")){
				registro.setRdtopermitido(new BigDecimal(parser.getAttributeValue(null, "rdtoPerm")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				Provincia p = new Provincia();
				p.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
				
				registro.setProvincia(p);
			}
			else{
				Provincia p = new Provincia();
				p.setCodprovincia(new BigDecimal(99));
				
				registro.setProvincia(p);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "rdtoPerm")).equals("")){
				registro.setRdtopermitido(new BigDecimal(parser.getAttributeValue(null, "rdtoPerm")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aplRdto")).equals("")){
				registro.setAplicrend(new Character(parser.getAttributeValue(null, "aplRdto").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "gruVar")).equals("")){
				registro.setGrupovarietal(new BigDecimal(parser.getAttributeValue(null, "gruVar")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCult")).equals("")){
				registro.setSistcult(new BigDecimal(parser.getAttributeValue(null, "sisCult")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMcoPlant")).equals("")){
				registro.setTipmcoplan(new BigDecimal(parser.getAttributeValue(null, "tipMcoPlant")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "carExpl")).equals("")){
				registro.setCaractexplot(new Long(parser.getAttributeValue(null, "carExpl")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "revRdto")).equals("")){
				registro.setRevisionrdto(new String(parser.getAttributeValue(null, "revRdto")));
			}
			registro.setValorcg(null);
			registro.setDesccg(null);
		}
		else if (GenericXMLParser.TAG_GARANTIZADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCpmodcg(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				registro.setCodrcubcg(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_FECHA_FIN_GARANTIAS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCpmodffg(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				RiesgoCubierto rc = new RiesgoCubierto();
				RiesgoCubiertoId rcId = new RiesgoCubiertoId();
				rcId.setCodmodulo(registro.getModulo().getId().getCodmodulo());
				rcId.setCodriesgocubierto(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
				rcId.setLineaseguroid(lineaseguroid);
				rc.setId(rcId);
				registro.setRiesgoCubierto(rc);
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				try {
					registro.setFecfgarant(sdf2.parse(parser.getAttributeValue(null, "valor")));
				} catch (ParseException e) {
					registro.setFecfgarant(null);
				}
			}
		}
		else if (GenericXMLParser.TAG_RIESGO_CUBIERTO_ELEGIDO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")){
				registro.setCpmodrcub(new BigDecimal(parser.getAttributeValue(null, "cPMod")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")){
				registro.setCodrcubrcub(new BigDecimal(parser.getAttributeValue(null, "codRCub")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setRcubeleg(parser.getAttributeValue(null, "valor").charAt(0));
			}
		}
		return registro;
	}
}
