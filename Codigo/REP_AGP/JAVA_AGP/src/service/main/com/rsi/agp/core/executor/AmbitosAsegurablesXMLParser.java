package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.AmbitoAsegurable;
import com.rsi.agp.dao.tables.cpl.AmbitoAsegurableId;

public class AmbitosAsegurablesXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\AmbitosAsegurables.xml";
		args[1] = "D:\\borrar\\AmbitosAsegurables.csv";
		args[2] = "181";
		args[3] = "yyyy-MM-dd";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + AmbitosAsegurablesXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			AmbitosAsegurablesXMLParser parser = new AmbitosAsegurablesXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de ambitos asegurables " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de ambitos asegurables " + e.getMessage());
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
		AmbitoAsegurable registro = (AmbitoAsegurable) reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";" + registro.getId().getCodprovincia() + ";";
		sql += registro.getId().getCodtermino() + ";" + registro.getId().getSubtermino() + ";";
		sql += StringUtils.nullToString(registro.getId().getCodcomarca());
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		AmbitoAsegurable registro;
		if (actual == null){
			registro = new AmbitoAsegurable();
		}
		else{
			registro = (AmbitoAsegurable) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			AmbitoAsegurableId idActual = new AmbitoAsegurableId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				idActual.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				idActual.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				idActual.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				idActual.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				idActual.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				idActual.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				idActual.setSubtermino(new Character('-'));
			}
			else{
				idActual.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			registro.setId(idActual);
		}
		
		return registro;
	}
}
