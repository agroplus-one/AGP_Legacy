package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CalculoIndemnizacion;
import com.rsi.agp.dao.tables.cgen.CapitalAseguradoElegible;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.MinimoIndemnizableElegible;
import com.rsi.agp.dao.tables.cgen.PctFranquiciaElegible;
import com.rsi.agp.dao.tables.cgen.TipoFranquicia;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModuloId;

public class VinculacionesValoresModuloXMLParser extends GenericXMLParser {
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\VinculacionesValoresModulo.xml";
		args[1] = "D:\\borrar\\VinculacionesValoresModulo.csv";
		args[2] = "202";
		args[3] = "null";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + VinculacionesValoresModuloXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			VinculacionesValoresModuloXMLParser parser = new VinculacionesValoresModuloXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Vinculaciones Valores Modulo " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Vinculaciones Valores Modulo " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		VinculacionValoresModulo registro = (VinculacionValoresModulo)reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		
		if (registro.getCaracteristicaModuloByFkVincValModCaracMod1() != null){
			sql += registro.getCaracteristicaModuloByFkVincValModCaracMod1().getId().getCodmodulo() + ";";
			sql += registro.getCaracteristicaModuloByFkVincValModCaracMod1().getId().getFilamodulo() + ";";
			sql += registro.getCaracteristicaModuloByFkVincValModCaracMod1().getId().getColumnamodulo() + ";";
		}
		else{
			sql += ";;;";
		}
		
		if (registro.getCaracteristicaModuloByFkVincValModCaracMod2() != null){
			sql += registro.getCaracteristicaModuloByFkVincValModCaracMod2().getId().getFilamodulo() + ";";
			sql += registro.getCaracteristicaModuloByFkVincValModCaracMod2().getId().getColumnamodulo() + ";";
		}
		else{
			sql += ";;";
		}
		
		//VINCULADOS
		if (registro.getCapitalAseguradoElegibleByPctcapitalasegvinc() != null){
			sql += registro.getCapitalAseguradoElegibleByPctcapitalasegvinc().getPctcapitalaseg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCalculoIndemnizacionByCalcindemnvinc() != null){
			sql += registro.getCalculoIndemnizacionByCalcindemnvinc().getCodcalculo() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getMinimoIndemnizableElegibleByPctminindemnvinc() != null){
			sql += registro.getMinimoIndemnizableElegibleByPctminindemnvinc().getPctminindem() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoFranquiciaByTipofranquiciavinc() != null){
			sql += registro.getTipoFranquiciaByTipofranquiciavinc().getCodtipofranquicia() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getPctFranquiciaElegibleByPctfranquiciavinc() != null){
			sql += registro.getPctFranquiciaElegibleByPctfranquiciavinc().getCodpctfranquiciaeleg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoRendimientoByCodtipordtovinc() != null){
			sql += registro.getTipoRendimientoByCodtipordtovinc().getCodtipordto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getGarantizadoByGarantizadovinc() != null){
			sql += registro.getGarantizadoByGarantizadovinc().getCodgarantizado() + ";";
		}
		else{
			sql += ";";
		}
		
		//ELEGIBLES
		if (registro.getCapitalAseguradoElegibleByPctcapitalasegeleg() != null){
			sql += registro.getCapitalAseguradoElegibleByPctcapitalasegeleg().getPctcapitalaseg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCalculoIndemnizacionByCalcindemneleg() != null){
			sql += registro.getCalculoIndemnizacionByCalcindemneleg().getCodcalculo() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getMinimoIndemnizableElegibleByPctminindemneleg() != null){
			sql += registro.getMinimoIndemnizableElegibleByPctminindemneleg().getPctminindem() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoFranquiciaByTipofranquiciaeleg() != null){
			sql += registro.getTipoFranquiciaByTipofranquiciaeleg().getCodtipofranquicia() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getPctFranquiciaElegibleByCodpctfranquiciaeleg() != null){
			sql += registro.getPctFranquiciaElegibleByCodpctfranquiciaeleg().getCodpctfranquiciaeleg() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoRendimientoByCodtipordtoeleg() != null){
			sql += registro.getTipoRendimientoByCodtipordtoeleg().getCodtipordto() + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getGarantizadoByGarantizadoeleg() != null){
			sql += registro.getGarantizadoByGarantizadoeleg().getCodgarantizado() + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getFichvinculacionexterna()) + ";";
		sql += registro.getId().getId() + ";";
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		VinculacionValoresModulo registro;
		if (actual == null){
			registro = new VinculacionValoresModulo();
		}
		else{
			registro = (VinculacionValoresModulo) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			VinculacionValoresModuloId idActual = new VinculacionValoresModuloId();
			idActual.setId(new BigDecimal(id));
			idActual.setLineaseguroid(lineaseguroid);
			registro.setId(idActual);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "fMod")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "cMod")).equals("")){
				//Característica elegible
				CaracteristicaModulo cm_e = new CaracteristicaModulo();
				cm_e.getId().setCodmodulo(StringUtils.nullToString(parser.getAttributeValue(null, "mod")).trim());
				cm_e.getId().setFilamodulo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "fMod"))));
				cm_e.getId().setColumnamodulo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "cMod"))));
				registro.setCaracteristicaModuloByFkVincValModCaracMod1(cm_e);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fModVinc")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "cModVinc")).equals("")){
				//Característica elegible
				CaracteristicaModulo cm_v = new CaracteristicaModulo();
				cm_v.getId().setFilamodulo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "fModVinc"))));
				cm_v.getId().setColumnamodulo(new BigDecimal(StringUtils.nullToString(parser.getAttributeValue(null, "cModVinc"))));
				registro.setCaracteristicaModuloByFkVincValModCaracMod2(cm_v);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "numTab")).trim().equals("")){
				registro.setFichvinculacionexterna(new BigDecimal(parser.getAttributeValue(null, "numTab")));
			}
		}
		else if (GenericXMLParser.TAG_DATOS_VINCULADOS.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "capAseg")).trim().equals("")){
				CapitalAseguradoElegible capAseg_vinc = new CapitalAseguradoElegible();
				capAseg_vinc.setPctcapitalaseg(new BigDecimal(parser.getAttributeValue(null, "capAseg")));
				registro.setCapitalAseguradoElegibleByPctcapitalasegvinc(capAseg_vinc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "calcIndem")).trim().equals("")){
				CalculoIndemnizacion calcIndem_vinc = new CalculoIndemnizacion();
				calcIndem_vinc.setCodcalculo(new BigDecimal(parser.getAttributeValue(null, "calcIndem")));
				registro.setCalculoIndemnizacionByCalcindemnvinc(calcIndem_vinc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "minIndem")).trim().equals("")){
				MinimoIndemnizableElegible minIndem_vinc = new MinimoIndemnizableElegible();
				minIndem_vinc.setPctminindem(new BigDecimal(parser.getAttributeValue(null, "minIndem")));
				registro.setMinimoIndemnizableElegibleByPctminindemnvinc(minIndem_vinc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipFranq")).trim().equals("")){
				TipoFranquicia tipFranq_vinc = new TipoFranquicia();
				tipFranq_vinc.setCodtipofranquicia(new Character(parser.getAttributeValue(null, "tipFranq").charAt(0)));
				registro.setTipoFranquiciaByTipofranquiciavinc(tipFranq_vinc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq")).trim().equals("")){
				PctFranquiciaElegible franq_vinc = new PctFranquiciaElegible();
				franq_vinc.setCodpctfranquiciaeleg(new BigDecimal(parser.getAttributeValue(null, "franq")));
				registro.setPctFranquiciaElegibleByPctfranquiciavinc(franq_vinc);
			}
			
			//Tipo Rendimiento de momento no se usa
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "garant")).trim().equals("")){
				Garantizado garant_vinc = new Garantizado();
				garant_vinc.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "garant")));
				registro.setGarantizadoByGarantizadovinc(garant_vinc);
			}
		}
		else if (GenericXMLParser.TAG_DATOS_ELEGIBLES.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "capAseg")).trim().equals("")){
				CapitalAseguradoElegible capAseg_eleg = new CapitalAseguradoElegible();
				capAseg_eleg.setPctcapitalaseg(new BigDecimal(parser.getAttributeValue(null, "capAseg")));
				registro.setCapitalAseguradoElegibleByPctcapitalasegeleg(capAseg_eleg);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "calcIndem")).trim().equals("")){
				CalculoIndemnizacion calcIndem_eleg = new CalculoIndemnizacion();
				calcIndem_eleg.setCodcalculo(new BigDecimal(parser.getAttributeValue(null, "calcIndem")));
				registro.setCalculoIndemnizacionByCalcindemneleg(calcIndem_eleg);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "minIndem")).trim().equals("")){
				MinimoIndemnizableElegible minIndem_eleg = new MinimoIndemnizableElegible();
				minIndem_eleg.setPctminindem(new BigDecimal(parser.getAttributeValue(null, "minIndem")));
				registro.setMinimoIndemnizableElegibleByPctminindemneleg(minIndem_eleg);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipFranq")).trim().equals("")){
				TipoFranquicia tipFranq_eleg = new TipoFranquicia();
				tipFranq_eleg.setCodtipofranquicia(new Character(parser.getAttributeValue(null, "tipFranq").charAt(0)));
				registro.setTipoFranquiciaByTipofranquiciaeleg(tipFranq_eleg);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "franq")).trim().equals("")){
				PctFranquiciaElegible franq_eleg = new PctFranquiciaElegible();
				franq_eleg.setCodpctfranquiciaeleg(new BigDecimal(parser.getAttributeValue(null, "franq")));
				registro.setPctFranquiciaElegibleByCodpctfranquiciaeleg(franq_eleg);
			}
			
			//Tipo Rendimiento de momento no se usa
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "garant")).trim().equals("")){
				Garantizado garant_eleg = new Garantizado();
				garant_eleg.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "garant")));
				registro.setGarantizadoByGarantizadoeleg(garant_eleg);
			}
		}
		
		return registro;
	}
}
