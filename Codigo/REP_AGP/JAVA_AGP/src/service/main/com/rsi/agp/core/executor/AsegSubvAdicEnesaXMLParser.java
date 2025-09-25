package com.rsi.agp.core.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Garantizado;
import com.rsi.agp.dao.tables.cgen.TipoRendimiento;
import com.rsi.agp.dao.tables.cpl.AsegSubvAdicionalEnesa;
import com.rsi.agp.dao.tables.cpl.AsegSubvAdicionalEnesaId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;

public class AsegSubvAdicEnesaXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\AseguradosSubvencionAdicionalRenovacionContratacionEnesa.xml";
		args[1] = "D:\\borrar\\AseguradosSubvencionAdicionalRenovacionContratacionEnesa.csv";
		args[2] = "241";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + AsegSubvAdicEnesaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			AsegSubvAdicEnesaXMLParser parser = new AsegSubvAdicEnesaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de AseguradosSubvencionAdicionalRenovacionContratacionEnesa " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de AseguradosSubvencionAdicionalRenovacionContratacionEnesa " + e.getMessage());
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
		AsegSubvAdicionalEnesa registro = (AsegSubvAdicionalEnesa)reg;
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getNifasegurado() + ";";
		
		if (registro.getNumanioscontrata() != null && !StringUtils.nullToString(registro.getNumanioscontrata()).equals("")){
			sql += StringUtils.nullToString(registro.getNumanioscontrata()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getModulo() != null && !StringUtils.nullToString(registro.getModulo().getId().getCodmodulo()).equals("")){
			sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoRendimiento() != null && !StringUtils.nullToString(registro.getTipoRendimiento().getCodtipordto()).equals("")){
			sql += StringUtils.nullToString(registro.getTipoRendimiento().getCodtipordto()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getGarantizado() != null && !StringUtils.nullToString(registro.getGarantizado().getCodgarantizado()).equals("")){
			sql += registro.getGarantizado().getCodgarantizado() + ";";
		}
		else{
			sql += ";";
		}
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		AsegSubvAdicionalEnesa registro;
		if (actual == null){
			registro = new AsegSubvAdicionalEnesa();
		}
		else{
			registro = (AsegSubvAdicionalEnesa) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			AsegSubvAdicionalEnesaId idAseg = new AsegSubvAdicionalEnesaId();
			idAseg.setNifasegurado(parser.getAttributeValue(null, "nif"));
			idAseg.setLineaseguroid(lineaseguroid);
			
			registro.setId(idAseg);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				ModuloId idMod = new ModuloId();
				idMod.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
				idMod.setLineaseguroid(lineaseguroid);
				modulo.setId(idMod);
				registro.setModulo(modulo);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "aCont")).equals("")){
				registro.setNumanioscontrata(new BigDecimal(parser.getAttributeValue(null, "aCont")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipRdto")).equals("")){
				TipoRendimiento tr = new TipoRendimiento();
				tr.setCodtipordto(new BigDecimal(parser.getAttributeValue(null, "tipRdto")));
				registro.setTipoRendimiento(tr);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "garant")).equals("")){
				Garantizado garantizado = new Garantizado();
				garantizado.setCodgarantizado(new BigDecimal(parser.getAttributeValue(null, "garant")));
				registro.setGarantizado(garantizado);
			}					
			
		}
		
		return registro;
	}
}
