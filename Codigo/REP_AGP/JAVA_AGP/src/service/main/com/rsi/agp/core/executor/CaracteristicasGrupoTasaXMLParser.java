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

import org.xml.sax.SAXException;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.cgen.Alternativa;
import com.rsi.agp.dao.tables.cgen.CaracteristicaExplotacion;
import com.rsi.agp.dao.tables.cgen.CertificadoInstalacion;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.DenominacionOrigen;
import com.rsi.agp.dao.tables.cgen.Destino;
import com.rsi.agp.dao.tables.cgen.GastoSalvamento;
import com.rsi.agp.dao.tables.cgen.HistorialAsegurado;
import com.rsi.agp.dao.tables.cgen.MaterialCubierta;
import com.rsi.agp.dao.tables.cgen.NivelRiesgo;
import com.rsi.agp.dao.tables.cgen.NumAniosDesdeDescorche;
import com.rsi.agp.dao.tables.cgen.PracticaCultural;
import com.rsi.agp.dao.tables.cgen.ProteccionCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaConduccion;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.SistemaProteccion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoInstalacion;
import com.rsi.agp.dao.tables.cgen.TipoMasa;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cgen.TipoTerreno;
import com.rsi.agp.dao.tables.cpl.GrupoTasas;
import com.rsi.agp.dao.tables.cpl.GrupoTasasId;

public class CaracteristicasGrupoTasaXMLParser extends GenericXMLParser{
	
	public static void main(String[] args){
		
		//TEMPORAL
		/*args = new String[4];
		args[0] = "D:\\borrar\\CaracteristicasGrupoTasa.xml";
		args[1] = "D:\\borrar\\CaracteristicasGrupoTasa.csv";
		args[2] = "181";
		args[3] = "d/MM/yyyy";*/
		//FIN TEMPORAL
		
		if (args.length != 4) {
			System.out.println("Usage: java " + CaracteristicasGrupoTasaXMLParser.class.getName()
					+ " <XML that needs to be transformed>" + " <Output file>" + " <LineaseguroId>" + " <dateFormat>");
			System.exit(1);
		}
		try {
			CaracteristicasGrupoTasaXMLParser parser = new CaracteristicasGrupoTasaXMLParser();
			parser.setTagPrincipal(GenericXMLParser.TAG_CARACTERISTICA_GRUPO_TASA);
			parser.procesarFichero(args[0], args[1], new Long(args[2]), args[3]);
		} catch (FileNotFoundException e) {
			System.out.println("Error al buscar el fichero el fichero de Caracteristicas Grupo Tasa " + e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.out.println("Error al crear el XMLReader " + e.getMessage());
			System.exit(3);
		} catch (IOException e) {
			System.out.println("Error de entrada/salida al parsear el fichero de Caracteristicas Grupo Tasa " + e.getMessage());
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
		GrupoTasas registro = (GrupoTasas)reg;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		String sql = "";
		sql += registro.getId().getLineaseguroid() + ";";
		sql += registro.getId().getCodmodulo().trim() + ";";
		sql += registro.getId().getCodcultivo() + ";";
		sql += registro.getId().getCodvariedad() + ";";
		sql += registro.getId().getCodgrupotasa() + ";";
		if (registro.getTipoCapital() != null){
			sql += registro.getTipoCapital().getCodtipocapital() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getTipoPlantacion() != null){
			sql += registro.getTipoPlantacion().getCodtipoplantacion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getSistemaCultivo() != null){
			sql += registro.getSistemaCultivo().getCodsistemacultivo() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getSistemaProduccion() != null){
			sql += registro.getSistemaProduccion().getCodsistemaproduccion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getSistemaProteccion() != null){
			sql += registro.getSistemaProteccion().getCodsistemaproteccion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getSistemaConduccion() != null){
			sql += registro.getSistemaConduccion().getCodsistemaconduccion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getGastoSalvamento() != null){
			sql += registro.getGastoSalvamento().getIndgastosalvamento() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getProteccionCultivo() != null){
			sql += registro.getProteccionCultivo().getIndproteccioncultivo() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getAlternativa() != null){
			sql += registro.getAlternativa().getCodalternativa() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getPracticaCultural() != null){
			sql += registro.getPracticaCultural().getCodpracticacultural() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCaracteristicaExplotacion() != null){
			sql += registro.getCaracteristicaExplotacion().getCodcaractexplotacion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getHistorialAsegurado() != null){
			sql += registro.getHistorialAsegurado().getCodhistorialasegurado() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getDestino() != null){
			sql += registro.getDestino().getCoddestino() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getDenominacionOrigen() != null){
			sql += registro.getDenominacionOrigen().getInddenomorigen() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getNivelRiesgo() != null){
			sql += registro.getNivelRiesgo().getCodnivelriesgo() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCicloCultivo() != null){
			sql += registro.getCicloCultivo().getCodciclocultivo() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getTipoInstalacion() != null){
			sql += registro.getTipoInstalacion().getCodtipoinstalacion() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getCertificadoInstalacion() != null){
			sql += registro.getCertificadoInstalacion().getCodcertificadoinstal() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getMaterialCubierta() != null){
			sql += registro.getMaterialCubierta().getCodmaterialcubierta() + ";";
		}
		else{
			sql += ";";
		}
		sql += StringUtils.nullToString(registro.getEdaddesdeestructura()) + ";";
		sql += StringUtils.nullToString(registro.getEdadhastaestructura()) + ";";
		if (registro.getFechaRecoleccionDesde() != null){
			sql += sdf.format(registro.getFechaRecoleccionDesde()) + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getFechaRecoleccionHasta() != null){
			sql += sdf.format(registro.getFechaRecoleccionHasta()) + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getTipoTerreno() != null){
			sql += registro.getTipoTerreno().getCodtipoterreno() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getTipoMasa() != null){
			sql += registro.getTipoMasa().getCodtipomasa() + ";";
		}
		else{
			sql += ";";
		}
		if (registro.getNumAniosDesdeDescorche() != null){
			sql += registro.getNumAniosDesdeDescorche().getCodnumaniosdescorche() + ";";
		}
		else{
			sql += ";";
		}
		return sql;
	}

	@Override
	protected Object generaRegistro(Object actual, String tag, XMLStreamReader parser, int id, Long lineaseguroid) {
		//Formato para el fichero canónico
		SimpleDateFormat sdf2 = new SimpleDateFormat(GenericXMLParser.FORMATO_ORIGEN);
		
		GrupoTasas registro;
		if (actual == null){
			registro = new GrupoTasas();
		}
		else{
			registro = (GrupoTasas) actual;
		}
		
		if (this.getTagPrincipal().equals(tag)){
			GrupoTasasId idActual = new GrupoTasasId();
			idActual.setLineaseguroid(lineaseguroid);
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "mod")).equals("")){
				idActual.setCodmodulo(parser.getAttributeValue(null, "mod").trim());
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "cul")).equals("")){
				idActual.setCodcultivo(new BigDecimal(parser.getAttributeValue(null, "cul")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "var")).equals("")){
				idActual.setCodvariedad(new BigDecimal(parser.getAttributeValue(null, "var")));
			}
			
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "grupoTasa")).equals("")){
				idActual.setCodgrupotasa(new BigDecimal(parser.getAttributeValue(null, "grupoTasa")));
			}
			
			registro.setId(idActual);
		}
		else if (GenericXMLParser.TAG_TIPO_CAPITAL.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoCapital tc = new TipoCapital();
				tc.setCodtipocapital(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setTipoCapital(tc);
			}
		}
		else if (GenericXMLParser.TAG_TIPO_PLANTACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoPlantacion tp = new TipoPlantacion();
				tp.setCodtipoplantacion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setTipoPlantacion(tp);
			}
		}
		else if (GenericXMLParser.TAG_SISTEMA_CULTIVO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				SistemaCultivo sc = new SistemaCultivo();
				sc.setCodsistemacultivo(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setSistemaCultivo(sc);
			}
		}
		else if (GenericXMLParser.TAG_SISTEMA_PRODUCCION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				SistemaProduccion sp = new SistemaProduccion();
				sp.setCodsistemaproduccion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setSistemaProduccion(sp);
			}
		}
		else if (GenericXMLParser.TAG_SISTEMA_PROTECCION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				SistemaProteccion spt = new SistemaProteccion();
				spt.setCodsistemaproteccion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setSistemaProteccion(spt);
			}
		}
		else if (GenericXMLParser.TAG_SISTEMA_CONDUCCION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				SistemaConduccion scc = new SistemaConduccion();
				scc.setCodsistemaconduccion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setSistemaConduccion(scc);
			}
		}
		else if (GenericXMLParser.TAG_GASTOS_SALVAMENTO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				GastoSalvamento gs = new GastoSalvamento();
				gs.setIndgastosalvamento(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				registro.setGastoSalvamento(gs);
			}
		}
		else if (GenericXMLParser.TAG_PROTECCION_CULTIVO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				ProteccionCultivo prot = new ProteccionCultivo();
				prot.setIndproteccioncultivo(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				registro.setProteccionCultivo(prot);
			}
		}
		else if (GenericXMLParser.TAG_ROTACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				Alternativa alt = new Alternativa();
				alt.setCodalternativa(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setAlternativa(alt);
			}
		}
		else if (GenericXMLParser.TAG_PRACTICA_CULTURAL.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				PracticaCultural pract = new PracticaCultural();
				pract.setCodpracticacultural(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setPracticaCultural(pract);
			}
		}
		else if (GenericXMLParser.TAG_CARACTERISTICA_EXPLOTACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				CaracteristicaExplotacion cesp = new CaracteristicaExplotacion();
				cesp.setCodcaractexplotacion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setCaracteristicaExplotacion(cesp);
			}
		}
		else if (GenericXMLParser.TAG_HISTORIAL_ASEGURADO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				HistorialAsegurado hist = new HistorialAsegurado();
				hist.setCodhistorialasegurado(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setHistorialAsegurado(hist);
			}
		}
		else if (GenericXMLParser.TAG_DESTINO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				Destino dest = new Destino(); 
				dest.setCoddestino(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setDestino(dest);
			}
		}
		else if (GenericXMLParser.TAG_DENOMINACION_ORIGEN.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				DenominacionOrigen dorig = new DenominacionOrigen(); 
				dorig.setInddenomorigen(new Character(parser.getAttributeValue(null, "valor").charAt(0)));
				registro.setDenominacionOrigen(dorig);
			}
		}
		else if (GenericXMLParser.TAG_NIVEL_RIESGO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				NivelRiesgo nr = new NivelRiesgo(); 
				nr.setCodnivelriesgo(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setNivelRiesgo(nr);
			}
		}
		else if (GenericXMLParser.TAG_CICLO_CULTIVO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				CicloCultivo cc = new CicloCultivo(); 
				cc.setCodciclocultivo(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setCicloCultivo(cc);
			}
		}
		else if (GenericXMLParser.TAG_TIPO_INSTALACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoInstalacion tInstal = new TipoInstalacion(); 
				tInstal.setCodtipoinstalacion(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setTipoInstalacion(tInstal);
			}
		}
		else if (GenericXMLParser.TAG_CERTIFICADO_INSTALACION.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				CertificadoInstalacion cInst = new CertificadoInstalacion(); 
				cInst.setCodcertificadoinstal(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setCertificadoInstalacion(cInst);
			}
		}
		else if (GenericXMLParser.TAG_MATERIAL_CUBIERTA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				MaterialCubierta mCub = new MaterialCubierta(); 
				mCub.setCodmaterialcubierta(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setMaterialCubierta(mCub);
			}
		}
		else if (GenericXMLParser.TAG_EDAD_ESTRUCTURA_DESDE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setEdaddesdeestructura(new BigDecimal(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_EDAD_ESTRUCTURA_HASTA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				registro.setEdadhastaestructura(new BigDecimal(parser.getAttributeValue(null, "valor")));
			}
		}
		else if (GenericXMLParser.TAG_FECHA_RECOLECCION_DESDE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				try {
					registro.setFechaRecoleccionDesde(sdf2.parse(parser.getAttributeValue(null, "valor")));
				} catch (ParseException e) {
					registro.setFechaRecoleccionDesde(null);
				}
			}
		}
		else if (GenericXMLParser.TAG_FECHA_RECOLECCION_HASTA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				try {
					registro.setFechaRecoleccionHasta(sdf2.parse(parser.getAttributeValue(null, "valor")));
				} catch (ParseException e) {
					registro.setFechaRecoleccionHasta(null);
				}
			}
		}
		else if (GenericXMLParser.TAG_TIPO_TERRENO.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoTerreno tTer = new TipoTerreno(); 
				tTer.setCodtipoterreno(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setTipoTerreno(tTer);
			}
		}
		else if (GenericXMLParser.TAG_TIPO_MASA.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				TipoMasa tmasa = new TipoMasa(); 
				tmasa.setCodtipomasa(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setTipoMasa(tmasa);
			}
		}
		else if (GenericXMLParser.TAG_NUM_ANIOS_DESDE_DESCORCHE.equals(tag)){
			if (!StringUtils.nullToString(parser.getAttributeValue(null, "valor")).equals("")){
				NumAniosDesdeDescorche nadd = new NumAniosDesdeDescorche(); 
				nadd.setCodnumaniosdescorche(new BigDecimal(parser.getAttributeValue(null, "valor")));
				registro.setNumAniosDesdeDescorche(nadd);
			}
		}
		return registro;
	}
}
