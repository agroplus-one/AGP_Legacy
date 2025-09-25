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
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RendimientosRegistrosParcela;
import com.rsi.agp.dao.tables.cpl.RendimientosRegistrosParcelaId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class RendimientosRegistrosParcelaXMLParser extends GenericXMLParser {
	
	private static final Log logger = LogFactory.getLog(RendimientosRegistrosParcelaXMLParser.class);
	
	private final String CARACTER_SEPARADOR = ";";
	
	public static void main (String[] args) {
		
		//TEMPORAL
		/*args = new String[3];
		args[0] = "D:\\temp\\adaptacion\\RendimientosRegistroParcela.xml";
		args[1] = "D:\\temp\\adaptacion\\RendimientosRegistroParcela.csv";
		args[2] = "765";
		args[3] = "d/MMM/yyyy";
		*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + RendimientosRegistrosParcelaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>");
			System.exit(1);
		}
		
		try {
			RendimientosRegistrosParcelaXMLParser parser = new RendimientosRegistrosParcelaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), null);
		}
		catch (FileNotFoundException e) {
			System.out.println("No se ha encontrado el fichero de rendimientos registro parcela " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de rendimientos registro parcela " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		}
		catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			System.exit(7);
		}
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		
		
		RendimientosRegistrosParcela registro;
		if (actual == null){
			registro = new RendimientosRegistrosParcela();
		}
		else{
			registro = (RendimientosRegistrosParcela) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			RendimientosRegistrosParcelaId rrpid = new RendimientosRegistrosParcelaId();
			rrpid.setId(new BigDecimal (id));
			rrpid.setLineaseguroid(new BigDecimal (lineaseguroid));
			
			registro.setId(rrpid);
			
			// CODMODULO
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));				
				registro.setModulo(modulo);
			}
			
			// CULTIVO / VARIEDAD
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
			
			// PROVSIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "provSIG")).equals("")) {
				registro.setCodprovsigpac(new BigDecimal(parser.getAttributeValue(null, "provSIG")));
			}
			
			// CODTERMSIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "termSIG")).equals("")) {
				registro.setCodtermsigpac(new BigDecimal(parser.getAttributeValue(null, "termSIG")));
			}
			
			// AGRSIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "agrSIG")).equals("")) {
				registro.setAgrsigpac(new BigDecimal(parser.getAttributeValue(null, "agrSIG")));
			}
			
			// ZONASIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "zonaSIG")).equals("")) {
				registro.setZonasigpac(new BigDecimal(parser.getAttributeValue(null, "zonaSIG")));
			}
			
			// POLIGONOSIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "polSIG")).equals("")) {
				registro.setPoligonosigpac(new BigDecimal(parser.getAttributeValue(null, "polSIG")));
			}
			
			// PARCELASIGPAC
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "parSIG")).equals("")) {
				registro.setParcelasigpac(new BigDecimal(parser.getAttributeValue(null, "parSIG")));
			}
			
			// CODCONCEPTOPPALMOD
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")) {
				ConceptoPpalModulo cpm = new ConceptoPpalModulo();
				cpm.setCodconceptoppalmod(new BigDecimal (parser.getAttributeValue(null, "cPMod")));
				registro.setConceptoPpalModulo(cpm);
			}
			
			// CODRIESGOCUBIERTO
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")) {
				RiesgoCubiertoId riesgoCubiertoId = new RiesgoCubiertoId();
				riesgoCubiertoId.setCodriesgocubierto(new BigDecimal (parser.getAttributeValue(null, "codRCub")));
				RiesgoCubierto riesgoCubierto = new RiesgoCubierto();
				riesgoCubierto.setId(riesgoCubiertoId);
				registro.setRiesgoCubierto(riesgoCubierto);
			}
			
			// RIESGO_CUBIERTO_ELEGIDO
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")) {
				registro.setRiesgoCubiertoElegido(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0));
			}	
			
			// APLIC_RENDIMIENTO
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aplRdto")).equals("")) {
				registro.setAplicRendimiento(parser.getAttributeValue(null, "aplRdto").charAt(0));
			}
			
			// RENDIMIENTO_PERM
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "rdtoPerm")).equals("")) {
				registro.setRendimientoPerm(new BigDecimal (parser.getAttributeValue(null, "rdtoPerm")));
			}
		}
		
		return registro;
	}

	@Override
	protected String generaInsert(Object reg, String dateFormat) {
					
		RendimientosRegistrosParcela rendRegParcela = (RendimientosRegistrosParcela) reg;
		
		StringBuilder sql = new StringBuilder("");
		
		// ID
		sql.append(rendRegParcela.getId().getId()).append(CARACTER_SEPARADOR);
		
		// LINEASEGUROID
		sql.append(rendRegParcela.getId().getLineaseguroid()).append(CARACTER_SEPARADOR);
		
		// CODMODULO
		if (rendRegParcela.getModulo() != null && !StringUtils.nullToString(rendRegParcela.getModulo().getId().getCodmodulo()).equals("")) {
			sql.append(StringUtils.nullToString(rendRegParcela.getModulo().getId().getCodmodulo()).trim()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
				
		if (rendRegParcela.getVariedad() != null) {
			// CODCULTIVO
			if (!StringUtils.nullToString(rendRegParcela.getVariedad().getId().getCodcultivo()).equals("")) {
				sql.append(StringUtils.nullToString(rendRegParcela.getVariedad().getId().getCodcultivo()).trim()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// CODVARIEDAD
			if (!StringUtils.nullToString(rendRegParcela.getVariedad().getId().getCodvariedad()).equals("")) {
				sql.append(StringUtils.nullToString(rendRegParcela.getVariedad().getId().getCodvariedad()).trim()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
		}
		else {
			sql.append(CARACTER_SEPARADOR).append(CARACTER_SEPARADOR);
		}
		
		// CODPROVSIGPAC
		if (rendRegParcela.getCodprovsigpac() != null) {
			sql.append(rendRegParcela.getCodprovsigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// CODTERMSIGPAC
		if (rendRegParcela.getCodtermsigpac() != null) {
			sql.append(rendRegParcela.getCodtermsigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// AGRSIGPAC
		if (rendRegParcela.getAgrsigpac() != null) {
			sql.append(rendRegParcela.getAgrsigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// ZONASIGPAC
		if (rendRegParcela.getZonasigpac() != null) {
			sql.append(rendRegParcela.getZonasigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// POLIGONOSIGPAC
		if (rendRegParcela.getPoligonosigpac() != null) {
			sql.append(rendRegParcela.getPoligonosigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// PARCELASIGPAC
		if (rendRegParcela.getParcelasigpac() != null) {
			sql.append(rendRegParcela.getParcelasigpac()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// CODCONCEPTOPPALMOD
		if (rendRegParcela.getConceptoPpalModulo() != null && !StringUtils.nullToString(rendRegParcela.getConceptoPpalModulo().getCodconceptoppalmod()).equals("")) {
			sql.append(rendRegParcela.getConceptoPpalModulo().getCodconceptoppalmod()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// CODRIESGOCUBIERTO
		if (rendRegParcela.getRiesgoCubierto() != null && !StringUtils.nullToString(rendRegParcela.getRiesgoCubierto().getId().getCodriesgocubierto()).equals("")) {
			sql.append(rendRegParcela.getRiesgoCubierto().getId().getCodriesgocubierto()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// RIESGO_CUBIERTO_ELEGIDO
		if (rendRegParcela.getRiesgoCubiertoElegido() != null) {
			sql.append(rendRegParcela.getRiesgoCubiertoElegido()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// APLIC_RENDIMIENTO
		if (rendRegParcela.getAplicRendimiento() != null) {
			sql.append(rendRegParcela.getAplicRendimiento()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		// RENDIMIENTO_PERM
		if (rendRegParcela.getRendimientoPerm() != null) {
			sql.append(rendRegParcela.getRendimientoPerm()).append(CARACTER_SEPARADOR);
		}
		else {
			sql.append(CARACTER_SEPARADOR);
		}
		
		return sql.toString();
	}

}
