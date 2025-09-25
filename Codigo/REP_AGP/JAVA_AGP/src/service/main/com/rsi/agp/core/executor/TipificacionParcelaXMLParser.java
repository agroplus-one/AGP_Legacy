package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.TipificacionRecibos;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RendimientosMediosParcela;
import com.rsi.agp.dao.tables.cpl.RendimientosMediosParcelaId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.TipificacionParcela;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;



public class TipificacionParcelaXMLParser extends GenericXMLParser{
	
	
		private final String CARACTER_SEPARADOR = ";";
		
		public static void main (String[] args) {
			
			//TEMPORAL
			/*args = new String[3];
			args[0] = "D:\\temp\\adaptacion\\TipificacionParcela.xml";
			args[1] = "D:\\temp\\adaptacion\\TipificacionParcela.csv";
			args[2] = "765";
			args[3] = "d/MMM/yyyy";
			*/
			//FIN TEMPORAL
			
			if (args.length != 4) {
				System.out.println("Usage: java " + TipificacionParcelaXMLParser.class.getName()
						+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>");
				System.exit(1);
			}
			
			try {
				TipificacionParcelaXMLParser parser = new TipificacionParcelaXMLParser();
				parser.setTagPrincipal(GenericXMLParser.TAG_RG);
				parser.procesarFichero(args[0], args[1], new Long(args[2]), null);
			}
			catch (FileNotFoundException e) {
				System.out.println("No se ha encontrado el fichero de tipificacion de parcelas " + e.getMessage());
				System.exit(2);
			} catch (SAXException e) {
				System.out.println("Error al crear el XMLReader " + e.getMessage());
				System.exit(3);
			} catch (IOException e) {
				System.out.println("Error de entrada/salida al parsear el fichero de tipificacion de parcelas " + e.getMessage());
				System.exit(4);
			} catch (XMLStreamException e) {
				System.out.println("Error al parsear el XML: " + e.getMessage());
				System.exit(5);
			} catch (FactoryConfigurationError e) {
				System.out.println("Error al crear el parseador XML: " + e.getMessage());
				System.exit(6);
			}
			catch (Exception e) {
				System.out.println("Error indefinido al parsear el XML: " + e.getMessage());
				System.exit(7);
			}
		}

		@Override
		protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
			
			
			TipificacionParcela registro;
			if (actual == null){
				registro = new TipificacionParcela();
			}
			else{
				registro = (TipificacionParcela) actual;
			}
			
			if (this.getTagPrincipal().equals(tag)){

				if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipPar")).trim().equals("")){
					registro.setIdtipoparcela(new Long(parser.getAttributeValue(null, "tipPar")));
				}
				
				if (!StringUtils.nullToString(parser.getAttributeValue(null, "desc")).trim().equals("")){
					registro.setDescripcion(parser.getAttributeValue(null, "desc"));
				}
			}
			
			return registro;
		}

		@Override
		protected String generaInsert(Object reg, String dateFormat) {
		
			
			TipificacionParcela registro = (TipificacionParcela)reg;
			String sql = "";
			sql += registro.getIdtipoparcela()+ ";" + StringUtils.nullToString(registro.getDescripcion()) + ";";
			return sql;
		}

	}
