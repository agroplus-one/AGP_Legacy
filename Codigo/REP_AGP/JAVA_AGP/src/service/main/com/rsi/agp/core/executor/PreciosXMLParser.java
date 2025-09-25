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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.Destino;
import com.rsi.agp.dao.tables.cgen.MaterialCubierta;
import com.rsi.agp.dao.tables.cgen.MaterialEstructura;
import com.rsi.agp.dao.tables.cgen.Pendiente;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.SistemaProteccion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoInstalacion;
import com.rsi.agp.dao.tables.cgen.TipoMasa;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cgen.TipoTerreno;
import com.rsi.agp.dao.tables.cpl.CodigoDenominacionOrigen;
import com.rsi.agp.dao.tables.cpl.CodigoDenominacionOrigenId;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.PrecioId;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;

public class PreciosXMLParser extends GenericXMLParser{
	
	private static final Log logger = LogFactory.getLog(PreciosXMLParser.class);
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\Precios.xml";
		args[1] = "D:\\borrar\\Precios.csv";
		args[2] = "481";
		args[3] = "d/MMM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + PreciosXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			PreciosXMLParser parser = new PreciosXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_RG);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			logger.error("Error al buscar el fichero el fichero de precios " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			logger.error("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			logger.error("Error de entrada/salida al parsear el fichero de precios " + e.getMessage());
			System.exit(4);
		} catch (XMLStreamException e) {
			logger.error("Error al parsear el XML: " + e.getMessage());
			System.exit(5);
		} catch (FactoryConfigurationError e) {
			logger.error("Error al crear el parseador XML: " + e.getMessage());
			System.exit(6);
		} catch (Exception e) {
			logger.error("Error indefinido al parsear el XML: " + e.getMessage());
			System.exit(7);
		}
	}
	
	@Override
	protected String generaInsert(Object reg, String dateFormat) {
		Precio registro = (Precio)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		
		if (registro.getModulo() != null){
			sql += StringUtils.nullToString(registro.getModulo().getId().getCodmodulo().trim()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getVariedad() != null){
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodcultivo()) + ";";
			sql += StringUtils.nullToString(registro.getVariedad().getId().getCodvariedad()) + ";";
		}
		else{
			sql += ";";
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getCodprovincia()) + ";";
		sql += StringUtils.nullToString(registro.getCodtermino()) + ";";
		sql += StringUtils.nullToString(registro.getSubtermino()) + ";";
		
		if (registro.getPracticaCultural() != null){
			sql += StringUtils.nullToString(registro.getPracticaCultural().getCodpracticacultural()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoCapital() != null){
			sql += StringUtils.nullToString(registro.getTipoCapital().getCodtipocapital()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaProduccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaProduccion().getCodsistemaproduccion()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getPreciodesde()) + ";";
		sql += StringUtils.nullToString(registro.getPreciohasta()) + ";";
		sql += StringUtils.nullToString(registro.getPreciofijo()) + ";";
		sql += registro.getId().getId() + ";";
		sql += StringUtils.nullToString(registro.getCodcomarca()) + ";";
		
		if (registro.getCodigoDenominacionOrigen() != null){
			sql += StringUtils.nullToString(registro.getCodigoDenominacionOrigen().getId().getCoddenomorigen()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getDestino() != null){
			sql += StringUtils.nullToString(registro.getDestino().getCoddestino()) + ";";
		}
		else{
			sql += ";";
		}
		
		sql += StringUtils.nullToString(registro.getDensidaddesde()) + ";";
		sql += StringUtils.nullToString(registro.getDensidadhasta()) + ";";
		sql += StringUtils.nullToString(registro.getEdaddesde()) + ";";
		sql += StringUtils.nullToString(registro.getEdadhasta())+ ";";
		
		if (registro.getFrecoldesde() != null){
			sql += sdf.format(registro.getFrecoldesde()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getFrecolhasta() != null){
			sql += sdf.format(registro.getFrecolhasta()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaCultivo() != null){
			sql += StringUtils.nullToString(registro.getSistemaCultivo().getCodsistemacultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getSistemaProteccion() != null){
			sql += StringUtils.nullToString(registro.getSistemaProteccion().getCodsistemaproteccion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoPlantacion() != null){
			sql += StringUtils.nullToString(registro.getTipoPlantacion().getCodtipoplantacion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getCicloCultivo() != null){
			sql += StringUtils.nullToString(registro.getCicloCultivo().getCodciclocultivo()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoInstalacion() != null){
			sql += StringUtils.nullToString(registro.getTipoInstalacion().getCodtipoinstalacion()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getMaterialEstructura() != null){
			sql += StringUtils.nullToString(registro.getMaterialEstructura().getCodmaterialestructura()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getMaterialCubierta() != null){
			sql += StringUtils.nullToString(registro.getMaterialCubierta().getCodmaterialcubierta()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoTerreno() != null){
			sql += StringUtils.nullToString(registro.getTipoTerreno().getCodtipoterreno()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getTipoMasa() != null){
			sql += StringUtils.nullToString(registro.getTipoMasa().getCodtipomasa()) + ";";
		}
		else{
			sql += ";";
		}
		
		if (registro.getPendiente() != null){
			sql += StringUtils.nullToString(registro.getPendiente().getCodpendiente()) + ";";
		}
		else{
			sql += ";";
		}
		
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canÃ³nico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		Precio registro;
		if (actual == null){
			registro = new Precio();
		}
		else{
			registro = (Precio) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			PrecioId idPrecio = new PrecioId();
			idPrecio.setId(new BigDecimal(id));
			idPrecio.setLineaseguroid(lineaseguroid);
			
			registro.setId(idPrecio);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				Modulo modulo = new Modulo();
				ModuloId idMod = new ModuloId();
				idMod.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
				modulo.setId(idMod);
				registro.setModulo(modulo);
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
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "prov")).equals("0")){
				registro.setCodprovincia(new BigDecimal(parser.getAttributeValue(null, "prov")));
			}
			else{
				registro.setCodprovincia(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "com")).equals("0")){
				registro.setCodcomarca(new BigDecimal(parser.getAttributeValue(null, "com")));
			}
			else{
				registro.setCodcomarca(new BigDecimal(99));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("") &&
					!StringUtils.nullToString(parser.getAttributeValue(null, "term")).equals("0")){
				registro.setCodtermino(new BigDecimal(parser.getAttributeValue(null, "term")));
			}
			else{
				registro.setCodtermino(new BigDecimal(999));
			}
			
			if (StringUtils.nullToString(parser.getAttributeValue(null, "sterm")).equals(" ")){
				registro.setSubtermino(new Character('-'));
			}
			else{
				registro.setSubtermino(new Character(parser.getAttributeValue(null, "sterm").charAt(0)));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "praCult")).equals("")){
				PracticaCultural pc = new PracticaCultural();
				pc.setCodpracticacultural(new BigDecimal(parser.getAttributeValue(null, "praCult")));
				registro.setPracticaCultural(pc);
			}
			else{
				PracticaCultural pc = new PracticaCultural();
				pc.setCodpracticacultural(new BigDecimal(0));
				registro.setPracticaCultural(pc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipCptal")).equals("")){
				TipoCapital tc = new TipoCapital();
				tc.setCodtipocapital(new BigDecimal(parser.getAttributeValue(null, "tipCptal")));
				registro.setTipoCapital(tc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisProd")).equals("")){
				SistemaProduccion sProd = new SistemaProduccion();
				sProd.setCodsistemaproduccion(new BigDecimal(parser.getAttributeValue(null, "sisProd")));
				registro.setSistemaProduccion(sProd);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "precD")).equals("")){
				registro.setPreciodesde(new BigDecimal(parser.getAttributeValue(null, "precD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "precH")).equals("")){
				registro.setPreciohasta(new BigDecimal(parser.getAttributeValue(null, "precH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "precF")).equals("")){
				registro.setPreciofijo(new BigDecimal(parser.getAttributeValue(null, "precF")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "codDO")).equals("")){
				CodigoDenominacionOrigen codDO = new CodigoDenominacionOrigen();
				CodigoDenominacionOrigenId idDo = new CodigoDenominacionOrigenId();
				idDo.setCoddenomorigen(new BigDecimal(parser.getAttributeValue(null, "codDO")));
				codDO.setId(idDo);
				registro.setCodigoDenominacionOrigen(codDO);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "dest")).equals("")){
				Destino destino = new Destino();
				destino.setCoddestino(new BigDecimal(parser.getAttributeValue(null, "dest")));
				registro.setDestino(destino);
			}
			else{
				Destino destino = new Destino();
				destino.setCoddestino(new BigDecimal(0));
				registro.setDestino(destino);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "densD")).equals("")){
				registro.setDensidaddesde(new BigDecimal(parser.getAttributeValue(null, "densD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "densH")).equals("")){
				registro.setDensidadhasta(new BigDecimal(parser.getAttributeValue(null, "densH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "edadD")).equals("")){
				registro.setEdaddesde(new BigDecimal(parser.getAttributeValue(null, "edadD")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "edadH")).equals("")){
				registro.setEdadhasta(new BigDecimal(parser.getAttributeValue(null, "edadH")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecRecolD")).equals("")){
				try {
					registro.setFrecoldesde(sdf2.parse(parser.getAttributeValue(null, "fecRecolD")));
				} catch (ParseException e) {
					registro.setFrecoldesde(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "fecRecolH")).equals("")){
				try {
					registro.setFrecolhasta(sdf2.parse(parser.getAttributeValue(null, "fecRecolH")));
				} catch (ParseException e) {
					registro.setFrecolhasta(null);
				}
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisCult")).equals("")){
				SistemaCultivo sCul = new SistemaCultivo();
				sCul.setCodsistemacultivo(new BigDecimal(parser.getAttributeValue(null, "sisCult")));
				registro.setSistemaCultivo(sCul);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "sisProt")).equals("")){
				SistemaProteccion sProt = new SistemaProteccion();
				sProt.setCodsistemaproteccion(new BigDecimal(parser.getAttributeValue(null, "sisProt")));
				registro.setSistemaProteccion(sProt);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipPlant")).equals("")){
				TipoPlantacion tp = new TipoPlantacion();
				tp.setCodtipoplantacion(new BigDecimal(parser.getAttributeValue(null, "tipPlant")));
				registro.setTipoPlantacion(tp);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "ciCul")).equals("")){
				CicloCultivo cc = new CicloCultivo();
				cc.setCodciclocultivo(new BigDecimal(parser.getAttributeValue(null, "ciCul")));
				registro.setCicloCultivo(cc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipInst")).equals("")){
				TipoInstalacion ti = new TipoInstalacion();
				ti.setCodtipoinstalacion(new BigDecimal(parser.getAttributeValue(null, "tipInst")));
				registro.setTipoInstalacion(ti);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "matEstr")).equals("")){
				MaterialEstructura me = new MaterialEstructura();
				me.setCodmaterialestructura(new BigDecimal(parser.getAttributeValue(null, "matEstr")));
				registro.setMaterialEstructura(me);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "matCubi")).equals("")){
				MaterialCubierta mc = new MaterialCubierta();
				mc.setCodmaterialcubierta(new BigDecimal(parser.getAttributeValue(null, "matCubi")));
				registro.setMaterialCubierta(mc);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipTer")).equals("")){
				TipoTerreno tt = new TipoTerreno();
				tt.setCodtipoterreno(new BigDecimal(parser.getAttributeValue(null, "tipTer")));
				registro.setTipoTerreno(tt);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "tipMas")).equals("")){
				TipoMasa tm = new TipoMasa();
				tm.setCodtipomasa(new BigDecimal(parser.getAttributeValue(null, "tipMas")));
				registro.setTipoMasa(tm);
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "pend")).equals("")){
				Pendiente pend = new Pendiente();
				pend.setCodpendiente(new BigDecimal(parser.getAttributeValue(null, "pend")));
				registro.setPendiente(pend);
			}
		}
		return registro;
	}
}
