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
import com.rsi.agp.dao.tables.cpl.RendimientosMediosParcela;
import com.rsi.agp.dao.tables.cpl.RendimientosMediosParcelaId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;



public class RendimientosMediosParcelaXMLParser extends GenericXMLParser{
	
	private static final Log logger = LogFactory.getLog(RendimientosMediosParcelaXMLParser.class);
	
		private final String CARACTER_SEPARADOR = ";";
		
		public static void main (String[] args) {
			
			//TEMPORAL
			/*args = new String[3];
			args[0] = "D:\\temp\\adaptacion\\RendimientosMediosParcela.xml";
			args[1] = "D:\\temp\\adaptacion\\RendimientosMediosParcela.csv";
			args[2] = "765";
			args[3] = "d/MMM/yyyy";
			*/
			//FIN TEMPORAL
			
			if (args.length != 4) {
				System.out.println("Usage: java " + RendimientosMediosParcelaXMLParser.class.getName()
						+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>");
				System.exit(1);
			}
			
			try {
				RendimientosMediosParcelaXMLParser parser = new RendimientosMediosParcelaXMLParser();
				parser.setTagPrincipal(GenericXMLParser.TAG_RG);
				parser.procesarFichero(args[0], args[1], new Long(args[2]), null);
			}
			catch (FileNotFoundException e) {
				System.out.println("No se ha encontrado el fichero de rendimientos medios parcela " + e.getMessage());
				System.exit(2);
			} catch (SAXException e) {
				logger.error("Error al crear el XMLReader " + e.getMessage());
				System.exit(3);
			} catch (IOException e) {
				logger.error("Error de entrada/salida al parsear el fichero de rendimientos medios parcela " + e.getMessage());
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
			
			
			RendimientosMediosParcela registro;
			if (actual == null){
				registro = new RendimientosMediosParcela();
			}
			else{
				registro = (RendimientosMediosParcela) actual;
			}
			
			if (this.getTagPrincipal().equals(tag)){
				RendimientosMediosParcelaId rrpid = new RendimientosMediosParcelaId();
				rrpid.setId(new BigDecimal (id));
				rrpid.setLineaseguroid(new BigDecimal (lineaseguroid));
				
				registro.setId(rrpid);
				
				// CODMODULO
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
					Modulo modulo = new Modulo();
					modulo.setId(new ModuloId(lineaseguroid, parser.getAttributeValue(null, "mod").trim()));				
					registro.setCodmodulo(modulo);
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
					registro.setCodvariedad(var);
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
					registro.setCodconceptoppalmod(cpm);
				}
				
				// CODRIESGOCUBIERTO
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")) {
					RiesgoCubiertoId riesgoCubiertoId = new RiesgoCubiertoId();
					riesgoCubiertoId.setCodriesgocubierto(new BigDecimal (parser.getAttributeValue(null, "codRCub")));
					RiesgoCubierto riesgoCubierto = new RiesgoCubierto();
					riesgoCubierto.setId(riesgoCubiertoId);
					registro.setCodriesgocubierto(riesgoCubierto);
				}
				
				// RIESGO_CUBIERTO_ELEGIDO
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")) {
					registro.setRiesgoCubiertoElegido(parser.getAttributeValue(null, "riesgCbtoEleg").charAt(0));
				}	
				
				// TIPO PARCELA
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipPar")).equals("")) {
					registro.setTipoParcela(new Long(parser.getAttributeValue(null, "tipPar")));
				}
				
				// APLIC_RENDIMIENTO
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "aplRdto")).equals("")) {
					registro.setAplicRendimiento(parser.getAttributeValue(null, "aplRdto").charAt(0));
				}
				
				// RENDIMIENTO_PERM
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "rdtoMed")).equals("")) {
					registro.setRendimientoMedio(new BigDecimal (parser.getAttributeValue(null, "rdtoMed")));
				}
				
				// PORCENTAJE REDUCCION
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "reduc")).equals("")) {
					registro.setPorcentajeReduccion(new BigDecimal (parser.getAttributeValue(null, "reduc")));
				}
								
				// REDUCCION
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "codReduc")).equals("")) {
				   registro.setReduccion(new BigDecimal (parser.getAttributeValue(null, "codReduc")));
				}

			}
			
			return registro;
		}

		@Override
		protected String generaInsert(Object reg, String dateFormat) {
						
			RendimientosMediosParcela rendMedParcela = (RendimientosMediosParcela) reg;
			
			StringBuilder sql = new StringBuilder("");
			
			// ID
			sql.append(rendMedParcela.getId().getId()).append(CARACTER_SEPARADOR);
			
			// LINEASEGUROID
			sql.append(rendMedParcela.getId().getLineaseguroid()).append(CARACTER_SEPARADOR);
			
			// CODMODULO
			if (rendMedParcela.getCodmodulo() != null && !StringUtils.nullToString(rendMedParcela.getCodmodulo().getId().getCodmodulo()).equals("")) {
				sql.append(StringUtils.nullToString(rendMedParcela.getCodmodulo().getId().getCodmodulo()).trim()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
					
			if (rendMedParcela.getCodvariedad() != null) {
				// CODCULTIVO
				if (!StringUtils.nullToString(rendMedParcela.getCodvariedad().getId().getCodcultivo()).equals("")) {
					sql.append(StringUtils.nullToString(rendMedParcela.getCodvariedad().getId().getCodcultivo()).trim()).append(CARACTER_SEPARADOR);
				}
				else {
					sql.append(CARACTER_SEPARADOR);
				}
				
				// CODVARIEDAD
				if (!StringUtils.nullToString(rendMedParcela.getCodvariedad().getId().getCodvariedad()).equals("")) {
					sql.append(StringUtils.nullToString(rendMedParcela.getCodvariedad().getId().getCodvariedad()).trim()).append(CARACTER_SEPARADOR);
				}
				else {
					sql.append(CARACTER_SEPARADOR);
				}
			}
			else {
				sql.append(CARACTER_SEPARADOR).append(CARACTER_SEPARADOR);
			}
			
			// CODPROVSIGPAC
			if (rendMedParcela.getCodprovsigpac() != null) {
				sql.append(rendMedParcela.getCodprovsigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// CODTERMSIGPAC
			if (rendMedParcela.getCodtermsigpac() != null) {
				sql.append(rendMedParcela.getCodtermsigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// AGRSIGPAC
			if (rendMedParcela.getAgrsigpac() != null) {
				sql.append(rendMedParcela.getAgrsigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// ZONASIGPAC
			if (rendMedParcela.getZonasigpac() != null) {
				sql.append(rendMedParcela.getZonasigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// POLIGONOSIGPAC
			if (rendMedParcela.getPoligonosigpac() != null) {
				sql.append(rendMedParcela.getPoligonosigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// PARCELASIGPAC
			if (rendMedParcela.getParcelasigpac() != null) {
				sql.append(rendMedParcela.getParcelasigpac()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// CODCONCEPTOPPALMOD
			if (rendMedParcela.getCodconceptoppalmod() != null && !StringUtils.nullToString(rendMedParcela.getCodconceptoppalmod().getCodconceptoppalmod()).equals("")) {
				sql.append(rendMedParcela.getCodconceptoppalmod().getCodconceptoppalmod()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// CODRIESGOCUBIERTO
			if (rendMedParcela.getCodriesgocubierto() != null && !StringUtils.nullToString(rendMedParcela.getCodriesgocubierto().getId().getCodriesgocubierto()).equals("")) {
				sql.append(rendMedParcela.getCodriesgocubierto().getId().getCodriesgocubierto()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// RIESGO_CUBIERTO_ELEGIDO
			if (rendMedParcela.getRiesgoCubiertoElegido() != null) {
				sql.append(rendMedParcela.getRiesgoCubiertoElegido()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			//TIPO PARCELA
			if (rendMedParcela.getTipoParcela() != null) {
				sql.append(rendMedParcela.getTipoParcela()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// APLIC_RENDIMIENTO
			if (rendMedParcela.getAplicRendimiento() != null) {
				sql.append(rendMedParcela.getAplicRendimiento()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// RENDIMIENTO_MEDIO
			if (rendMedParcela.getRendimientoMedio() != null) {
				sql.append(rendMedParcela.getRendimientoMedio()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// PORCENTAJE_REDUCCION
			if (rendMedParcela.getPorcentajeReduccion() != null) {
				sql.append(rendMedParcela.getPorcentajeReduccion()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
			
			// REDUCCION
			if (rendMedParcela.getReduccion() != null) {
				sql.append(rendMedParcela.getReduccion()).append(CARACTER_SEPARADOR);
			}
			else {
				sql.append(CARACTER_SEPARADOR);
			}
						
						
			
			return sql.toString();
		}

	}
