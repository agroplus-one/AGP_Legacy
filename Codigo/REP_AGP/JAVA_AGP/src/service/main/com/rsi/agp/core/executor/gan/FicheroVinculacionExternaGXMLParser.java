package com.rsi.agp.core.executor.gan;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.executor.GenericXMLParser;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cpl.gan.FicheroVinculacionExternaG;
import com.rsi.agp.dao.tables.cpl.gan.FicheroVinculacionExternaGId;

public class FicheroVinculacionExternaGXMLParser extends GenericXMLParser {

	public static void main(String[] args) {

		// TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\TablaExternaGruposMedidaGruposNegocioEspecies.xml";
		args[1] = "D:\\borrar\\TablaExternaGruposMedidaGruposNegocioEspecies.csv";
		args[2] = "1045";
		args[3] = "d/MM/yyyy";*/
		// FIN TEMPORAL

		if (args.length != 4) {
			System.out.println("Usage: java "
					+ FicheroVinculacionExternaGXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>"
					+ " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			FicheroVinculacionExternaGXMLParser parser = new FicheroVinculacionExternaGXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out
					.println("Error al buscar el fichero el fichero de FicheroVinculacionExternaG"
							+ e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out
					.println("Error de entrada/salida al parsear el fichero de FicheroVinculacionExternaG"
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

		FicheroVinculacionExternaG registro = (FicheroVinculacionExternaG) reg;
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getCodmodulo() + ";";
		sql += registro.getId().getGrupomedida() + ";";
		sql += registro.getId().getGruponegocio() + ";";
		sql += registro.getId().getCodespecie() + ";";
		
		if (registro.getId().getCpmriesgocubeleg() != null) {
			sql += registro.getId().getCpmriesgocubeleg() + ";";
		}
		if (registro.getId().getRcriesgocubeleg() != null) {
			sql += registro.getId().getRcriesgocubeleg() + ";";
		}
		if (registro.getId().getValriesgocubeleg() != null) {
			sql += registro.getId().getValriesgocubeleg() + ";";
		}
		if (registro.getId().getCpmpctfranquicia() != null) {
			sql += registro.getId().getCpmpctfranquicia() + ";";
		}
		if (registro.getId().getRcpctfranquicia() != null) {
			sql += registro.getId().getRcpctfranquicia() + ";";
		}
		if (registro.getId().getValpctfranquicia() != null) {
			sql += registro.getId().getValpctfranquicia() + ";";
		}

		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag,
			XMLStreamReader parser, int id, Long lineaseguroid) {

		FicheroVinculacionExternaG registro;
		FicheroVinculacionExternaGId idActual;
		
		if (actual == null) {
			registro = new FicheroVinculacionExternaG();
			idActual = new FicheroVinculacionExternaGId();
			registro.setId(idActual);
		} else {
			registro = (FicheroVinculacionExternaG) actual;
			idActual = registro.getId();
		}
		
		
		if (this.getTagPrincipal().equals(tag)) {

			idActual.setLineaseguroid(lineaseguroid);
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod").trim()).equals("")) {
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupMed")).equals("")) {
				idActual.setGrupomedida(StringUtils.nullToString(parser
						.getAttributeValue(null, "grupMed").trim()));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupNeg")).equals("")) {
				idActual.setGruponegocio(new Character(parser
						.getAttributeValue(null, "grupNeg").charAt(0)));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "esp")).equals("")) {
				idActual.setCodespecie(new Long(parser.getAttributeValue(null,"esp")));

			}

		} else if (GenericXMLParser.TAG_RIESGO_CUBIERTO_ELEGIDO.equals(tag)) {
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cPMod")).equals("")) {
				idActual.setCpmriesgocubeleg(new Long(parser.getAttributeValue(
						null, "cPMod")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")) {
				idActual.setRcriesgocubeleg(new Long(parser.getAttributeValue(
						null, "codRCub")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")) {
				idActual.setValriesgocubeleg(new Character(parser
						.getAttributeValue(null, "valor").charAt(0)));
			}
		} else if (GenericXMLParser.TAG_FRANQUICIA.equals(tag)) {
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "CpMod")).equals("")) {
				idActual.setCpmpctfranquicia(new Long(parser.getAttributeValue(
						null, "CpMod")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codRCub")).equals("")) {
				idActual.setRcpctfranquicia(new Long(parser.getAttributeValue(
						null, "codRCub")));
			}
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")) {
				idActual.setValpctfranquicia(new Long(parser.getAttributeValue(
						null, "valor")));
			}
		}
		registro.setId(idActual);
		return registro;
	}
}
