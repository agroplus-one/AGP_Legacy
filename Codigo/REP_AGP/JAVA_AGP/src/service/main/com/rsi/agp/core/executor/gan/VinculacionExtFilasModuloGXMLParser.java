package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.VinculacionExtFilasModuloG;
import com.rsi.agp.dao.tables.cpl.gan.VinculacionExtFilasModuloGId;

public class VinculacionExtFilasModuloGXMLParser extends GenericXMLParser {

	public static void main(String[] args) {

		/*args = new String[4];
		args[0] = "D:\\borrar\\VinculacionesExternasFilasModulo.xml";
		args[1] = "D:\\borrar\\VinculacionesExternasFilasModulo.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";*/
		

		if (args.length != 4) {
			System.out.println("Usage: java "
					+ VinculacionExtFilasModuloGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>"
					+ " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			VinculacionExtFilasModuloGXMLParser parser = new VinculacionExtFilasModuloGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out
					.println("Error al buscar el fichero el fichero de VinculacionExtFilasModuloG"
							+ e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out
					.println("Error de entrada/salida al parsear el fichero de VinculacionExtFilasModuloG"
							+ e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			System.out.println("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			System.out.println("Error al crear el parseador XML: "
					+ e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			System.out.println("Error indefinido al parsear el XML: "
					+ e.getMessage());
			System.exit(7);
		}
	}

	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		
		VinculacionExtFilasModuloG registro = (VinculacionExtFilasModuloG) reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getFilamodulo() + ";";
		sql += registro.getId().getValriesgocubeleg() + ";";
		if (registro.getFichvinculacionexterna() != null) {
			sql += registro.getFichvinculacionexterna() + ";";
		} else {
			sql += ";";
		}

		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag,
			XMLStreamReader parser, int id, Long lineaseguroid) {

		VinculacionExtFilasModuloG registro;
		if (actual == null) {
			registro = new VinculacionExtFilasModuloG();
		} else {
			registro = (VinculacionExtFilasModuloG) actual;
		}
		VinculacionExtFilasModuloGId idActual = new VinculacionExtFilasModuloGId();
		if (this.getTagPrincipal().equals(tag)) {
			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils
					.nullToString(parser.getAttributeValue(null, "mod"))
					.equals("")) {
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod")
						.trim());
			}
			if (!StringUtils.nullToString(
					parser.getAttributeValue(null, "fMod")).equals("")) {
				idActual.setFilamodulo(new Long(StringUtils.nullToString(parser
						.getAttributeValue(null, "fMod").trim())));
			}
			if (!StringUtils.nullToString(
					parser.getAttributeValue(null, "riesgCbtoEleg")).equals("")) {
				idActual.setValriesgocubeleg(new Character(parser
						.getAttributeValue(null, "riesgCbtoEleg").charAt(0)));
			}
			if (!StringUtils.nullToString(
					parser.getAttributeValue(null, "numTab")).equals("")) {
				registro.setFichvinculacionexterna(new Long(StringUtils
						.nullToString(parser.getAttributeValue(null, "numTab")
								.trim())));
			}
		}
		registro.setId(idActual);
		return registro;
	}
}
