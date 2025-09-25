package com.rsi.agp.core.executor;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ZonificacionFamilia;
import com.rsi.agp.dao.tables.cgen.ZonificacionGrupoCultivo;
import com.rsi.agp.dao.tables.cgen.ZonificacionSIGPAC;

public class ZonificacionXMLParser {
	
	private static final Log logger = LogFactory.getLog(ZonificacionXMLParser.class);
	
	/**
	 * Metodo para procesar el fichero de zonificaciones enviado por Agroseguro.
	 * @param ficheroOrigen Fichero a tratar.
	 * @param ficheroDestino Fichero canonico a generar.
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws FactoryConfigurationError 
	 * @throws XMLStreamException 
	 */
	public static void procesarFichero(String ficheroOrigen, String ficheroDestino, int maxInsertedId, String dateFormat) throws SAXException, FileNotFoundException, IOException, XMLStreamException, FactoryConfigurationError{
		//Formato para logs
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		//Formato para el fichero canonico
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		int eventCode;
		String tag;
		
		System.out.println("INICIO DEL PARSEO " + sdf1.format(new Date()));
		
		int id = 1 + maxInsertedId;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroDestino))) {

			XMLInputFactory factory = XMLInputFactory.newInstance();			
			try {
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			} catch (IllegalArgumentException e1) {
				logger.error(e1.getMessage());
			}
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(ficheroOrigen));
			while (parser.hasNext()) {
				eventCode = parser.next();
				if (eventCode == XMLStreamConstants.START_ELEMENT) {
					tag = parser.getLocalName();
					if ("RG".equals(tag)) {
						// Cargamos los datos del registro en un objeto ZonificacionSIGPAC
						ZonificacionSIGPAC actual = new ZonificacionSIGPAC();

						// y le asignamos la "valoracion" contenida como atributo
						if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecEst")).equals("")) {

							try {
								actual.setFecestudiozonif(sdf2.parse(parser.getAttributeValue(null, "fecEst")));
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha " + parser.getAttributeValue(null, "fecEst")
										+ ": " + e.getMessage());
							}
						}

						ZonificacionFamilia zonificacionFamilia = new ZonificacionFamilia();
						zonificacionFamilia.setCodfamilia(parser.getAttributeValue(null, "familia").trim());
						actual.setZonificacionFamilia(zonificacionFamilia);

						if (parser.getAttributeValue(null, "grupCul") != null) {
							ZonificacionGrupoCultivo zonificacionGrupoCultivo = new ZonificacionGrupoCultivo();
							zonificacionGrupoCultivo.setCodgrpcultivo(parser.getAttributeValue(null, "grupCul").trim());
							actual.setZonificacionGrupoCultivo(zonificacionGrupoCultivo);
						}

						actual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
						actual.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
						if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" "))
							actual.setSubtermino('-');
						else
							actual.setSubtermino(parser.getAttributeValue(null, "sterm").charAt(0));
						actual.setZona(new BigDecimal(parser.getAttributeValue(null, "zona")));
						actual.setCodprovsigpac(new BigDecimal(parser.getAttributeValue(null, "provSIG")));
						actual.setCodtermsigpac(new BigDecimal(parser.getAttributeValue(null, "termSIG")));
						actual.setAgrsigpac(new BigDecimal(parser.getAttributeValue(null, "agrSIG")));
						actual.setZonasigpac(new BigDecimal(parser.getAttributeValue(null, "zonaSIG")));
						actual.setPoligonosigpac(new BigDecimal(parser.getAttributeValue(null, "polSIG")));
						actual.setParcelasigpac(new BigDecimal(parser.getAttributeValue(null, "parSIG")));

						// Insertamos la ZonificacionSIGPAC en el fichero canonico
						String consulta = "";
						consulta += id + ";";
						if (actual.getFecestudiozonif() != null) {
							consulta += sdf.format(actual.getFecestudiozonif()) + ";";
						} else {
							consulta += ";";
						}
						if (!StringUtils.nullToString(actual.getZonificacionFamilia().getCodfamilia()).equals(""))
							consulta += StringUtils.nullToString(actual.getZonificacionFamilia().getCodfamilia()) + ";";
						else
							consulta += ";";
						if (actual.getZonificacionGrupoCultivo() != null && !StringUtils
								.nullToString(actual.getZonificacionGrupoCultivo().getCodgrpcultivo()).equals(""))
							consulta += StringUtils
									.nullToString(actual.getZonificacionGrupoCultivo().getCodgrpcultivo()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getCodprovincia()).equals(""))
							consulta += StringUtils.nullToString(actual.getCodprovincia()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getCodtermino()).equals(""))
							consulta += StringUtils.nullToString(actual.getCodtermino()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getSubtermino()).equals(""))
							consulta += StringUtils.nullToString(actual.getSubtermino()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getZona()).equals(""))
							consulta += StringUtils.nullToString(actual.getZona()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getCodprovsigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getCodprovsigpac()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getCodtermsigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getCodtermsigpac()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getAgrsigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getAgrsigpac()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getZonasigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getZonasigpac()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getPoligonosigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getPoligonosigpac()) + ";";
						else
							consulta += ";";
						if (!StringUtils.nullToString(actual.getParcelasigpac()).equals(""))
							consulta += StringUtils.nullToString(actual.getParcelasigpac()) + "";
						else
							consulta += ";";

						bw.write(consulta + "\n");

						id++;

					}
				}
			}
		}		
		System.out.println("Número de elementos generados: " + id);
		System.out.println("FIN DEL PARSEO " + sdf1.format(new Date()));
	}
	
	public static void main(String[] args){
		if (args.length != 4) {
			System.out.println("Usage: java "
					+ ZonificacionXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <maxId>" + " <dateFormat>");
			System.out.println("Optional parameters: "
					+ " <XSLT parameters>" + " <XSLT parameter values>");
			System.exit(1);
		}
		try {
			procesarFichero(args[0], args[1], Integer.parseInt(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de zonificaciones " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de zonificaciones " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			e.printStackTrace();
			System.exit(7);
		}
	}
}
