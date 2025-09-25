package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.math.BigDecimal;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.tables.comisiones.unificado.AplicacionUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.ColectivoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FaseUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroContenidoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.GrupoNegocioUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.IndividualUnificado;

public class FicheroUnificadoGastosManager {
	
	private final Log logger = LogFactory.getLog(FicheroUnificadoGastosManager.class);
	
	protected void llenaDatosGeneralesFichero(FicheroUnificado file, String usuario, Character tipoFichero, 
			String nombreFichero, Date fechaCarga) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String fechaCargaFormat = df.format(fechaCarga);	
		file.setUsuario(usuario);
		file.setNombreFichero(nombreFichero);
		file.setTipoFichero(tipoFichero);
		try {
			file.setFechaCarga(df.parse(fechaCargaFormat));
		} catch (ParseException e) {
			logger.error("Excepcion : FicheroUnificadoGastosManager - llenaDatosGeneralesFichero", e);
		}
		file.setEstado(Constants.FICHERO_UNIFICADO_ESTADO_CARGADO);
	}
	
	protected FicheroContenidoUnificado getFicheroContenidoUnificado(FicheroUnificado file, Blob contenido, Long idFichero) {
		FicheroContenidoUnificado fc=new FicheroContenidoUnificado();
		fc.setFichero(file);
		fc.setContenido(contenido);
		fc.setIdfichero(idFichero);
		return fc;
	}
	
	protected FaseUnificado getFaseUnificado(int numFase, Date fechaEmisionRecibo, int codPlan, FicheroUnificado file) {
		FaseUnificado fase=new FaseUnificado();
		fase.setFase(numFase);
		fase.setFechaEmisionRecibo(fechaEmisionRecibo);
		fase.setFichero(file);
		fase.setPlan(codPlan);
	
		return fase;
	}
		
	
	protected ColectivoUnificado getColectivoUnificado(es.agroseguro.iTipos.NombreApellidos na, es.agroseguro.iTipos.RazonSocial rz,
			String referencia, String codigoInterno, Integer dc) {
		ColectivoUnificado colUnif = new ColectivoUnificado();
		if(null!= na) {
			colUnif.setApellido1(na.getApellido1());
			colUnif.setApellido2(na.getApellido2());
			colUnif.setNombre(na.getNombre());
		}
		if (null!=rz) {
			colUnif.setRazonSocial(rz.getRazonSocial());
		}
		if(null!=referencia) {
			colUnif.setReferencia(referencia);
		}
		if (null!=codigoInterno) {
			colUnif.setCodigoInterno(codigoInterno);
		}
		if (null!=dc) {
			colUnif.setDc(dc);
		}
		
		return colUnif;
	}
	
	
	
	protected IndividualUnificado getIndividualUnificado(es.agroseguro.iTipos.NombreApellidos na, es.agroseguro.iTipos.RazonSocial rz) {
		IndividualUnificado iu=new IndividualUnificado();
		if(null!=na) {
			iu.setApellido1(na.getApellido1());
			iu.setApellido2(na.getApellido2());
			iu.setNombre(na.getNombre());
		}
		if (null!=rz)		
			iu.setRazonSocial(rz.getRazonSocial());
		
		return iu;
	}
	
	
	protected AplicacionUnificado getAplicacionUnificado(es.agroseguro.iTipos.NombreApellidos na, 
			es.agroseguro.iTipos.RazonSocial rz, Character anuladaRefundida,String codigoInterno, 
			Integer dc, String referencia, Character tipoReferencia, BigDecimal importeSaldoPendiente, 
			BigDecimal importeCobroRecibido) {
		AplicacionUnificado apl=getAplicacionUnificado(na,rz, anuladaRefundida,codigoInterno, 
				dc, referencia, tipoReferencia);
		apl.setImporteCobroRecibido(importeCobroRecibido);
		apl.setImporteSaldoPdte(importeSaldoPendiente);
		
		return apl;
	}
	
	protected AplicacionUnificado getAplicacionUnificado(es.agroseguro.iTipos.NombreApellidos na, 
			es.agroseguro.iTipos.RazonSocial rz, Character anuladaRefundida,String codigoInterno, 
			Integer dc, String referencia, Character tipoReferencia) {
		
		AplicacionUnificado apl = new AplicacionUnificado();
		
		if (null!=na) {
			apl.setApellido1(na.getApellido1());
			apl.setApellido2(na.getApellido2());
			apl.setNombre(na.getNombre());
		}
		
		if(null!=rz) {
			apl.setRazonSocial(rz.getRazonSocial());
		}
		apl.setAnuladaRefundida(anuladaRefundida);
		apl.setCodigoInterno(codigoInterno); 
		apl.setDc(dc);
		apl.setReferencia(referencia);
		apl.setTipoReferencia(tipoReferencia);
		
		
		
		return apl;
	}
	
	protected void llenaGastosAbonar(GrupoNegocioUnificado gnu,BigDecimal gaAdmin, BigDecimal gaAdq, BigDecimal gaComisionMediador, 
			BigDecimal gaCommedEntidad, BigDecimal gaCommedEsmed, Long gaPlazoDomiciliacion,  Integer gaReciboCompensa) {
		gnu.setGaAdmin(gaAdmin);
		gnu.setGaAdq(gaAdq);
		gnu.setGaComisionMediador(gaComisionMediador);
		gnu.setGaCommedEntidad(gaCommedEntidad);
		gnu.setGaCommedEsmed(gaCommedEsmed);
		gnu.setGaPlazoDomiciliacion(gaPlazoDomiciliacion);
		gnu.setGaReciboCompensa(gaReciboCompensa);
		
	}
	
	protected void llenaGastosPendientesAbonar(GrupoNegocioUnificado gnu, BigDecimal gpAdmin, BigDecimal gpAdq, BigDecimal gpComisionMediador, 
			BigDecimal gpCommedEntidad, BigDecimal gpCommedEsmed) {
		gnu.setGpAdmin(gpAdmin);
		gnu.setGpAdq(gpAdq);
		gnu.setGpComisionMediador(gpComisionMediador);
		gnu.setGpCommedEntidad(gpCommedEntidad);
		gnu.setGpCommedEsmed(gpCommedEsmed);
		
	}
	
	protected void llenaGastosPendientesAbonarRecibosImpagados(GrupoNegocioUnificado gnu, BigDecimal gpAdmin, BigDecimal gpAdq, BigDecimal gpComisionMediador, 
			BigDecimal gpCommedEntidad, BigDecimal gpCommedEsmed) {
		gnu.setGpiAdmin(gpAdmin);
		gnu.setGpiAdq(gpAdq);
		gnu.setGpiComisionMediador(gpComisionMediador);
		gnu.setGpiCommedEntidad(gpCommedEntidad);
		gnu.setGpiCommedEsmed(gpCommedEsmed);
	}
	
	protected void llenaGastosDevengados(GrupoNegocioUnificado gnu, BigDecimal gpAdmin, BigDecimal gpAdq, BigDecimal gpComisionMediador, 
			BigDecimal gpCommedEntidad, BigDecimal gpCommedEsmed) {
		gnu.setGdAdmin(gpAdmin);
		gnu.setGdAdq(gpAdq);
		gnu.setGdComisionMediador(gpComisionMediador);
		gnu.setGdCommedEntidad(gpCommedEntidad);
		gnu.setGdCommedEsmed(gpCommedEsmed);
	}
	
}
