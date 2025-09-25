package com.rsi.agp.core.decorators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import es.agroseguro.seguimientoContratacion.EstadoContratacion;
import es.agroseguro.seguimientoContratacion.InfoAdicional;
import es.agroseguro.seguimientoContratacion.Recibo;
import es.agroseguro.seguimientoContratacion.Incidencia;

public class ModelTableDecoratorSeguimientoPoliza extends TableDecorator{

	/*********************************/
	/*****ESTADO CONTRATACION*********/
	/*********************************/
	public String getEstadoDescriContratacion() {
		EstadoContratacion estadoContratacion = (EstadoContratacion) getCurrentRowObject();
		
		if (estadoContratacion == null)
			return "";
		else
			
			return StringUtils.nullToString(estadoContratacion.getEstado() +" - "+estadoContratacion.getDescEstado());
	}
	
	public String getEstContFecha() throws ParseException {
		EstadoContratacion estadoContratacion = (EstadoContratacion) getCurrentRowObject();
		//return estadoContratacion.getFechaHora().getTime();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		if (estadoContratacion == null || estadoContratacion.getFechaHora() == null)
			return "";
		else
			return f.format(estadoContratacion.getFechaHora().getTime());
	}
	
	public String getMotivoAnulResc() {
		EstadoContratacion estadoContratacion = (EstadoContratacion) getCurrentRowObject();

		if (estadoContratacion == null)
			return "";
		else
			if(estadoContratacion.getMotivoAnulResc() != 0){
				return StringUtils.nullToString(estadoContratacion.getMotivoAnulResc() +" - "+ estadoContratacion.getDesMotivoAnulResc());
			}else
				return "";
	}
	
	/***********************************/
	/*****INFORMACION ADICIONAL*********/
	/***********************************/
	
	public String getInfoTexto() {
		InfoAdicional infoAdicional = (InfoAdicional) getCurrentRowObject();

		if (infoAdicional == null)
			return "";
		else
			return StringUtils.nullToString(infoAdicional.getCodigo() +" - "+ infoAdicional.getTexto());
	}
	
	public String getInfoFecha() {
		InfoAdicional infoAdicional = (InfoAdicional) getCurrentRowObject();

		//return infoAdicional.getFechaHora();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if(infoAdicional == null || infoAdicional.getFechaHora() == null)
			return "";
		else
			
			return f.format(infoAdicional.getFechaHora().getTime());
		
		
	}
	public String getInfoCausa(){
		InfoAdicional infoAdicional = (InfoAdicional) getCurrentRowObject();

		if (infoAdicional == null)
			return "";
		else
			if(infoAdicional.getCausa() == 0){
				return "";
			}else
				return StringUtils.nullToString(infoAdicional.getCausa() +" - "+ infoAdicional.getDescCausa());
			
	}
	
	/*********************/
	/*****RECIBOS*********/
	/*********************/
	
	public String getReciboNumero(){
		Recibo recibo = (Recibo) getCurrentRowObject();

		if (recibo == null)
			return "";
		else
			return StringUtils.nullToString(recibo.getNumero());
	}
	
	public String getReciboFecEmision(){
		Recibo recibo = (Recibo) getCurrentRowObject();
		//return recibo.getFechaEmision();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		if(recibo == null || recibo.getFechaEmision() == null)
			return "";
		else
			return f.format(recibo.getFechaEmision().getTime());
	}
	public String getReciboFecImpago(){
		Recibo recibo = (Recibo) getCurrentRowObject();
		//return recibo.getFechaImpago();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		if(recibo == null  || recibo.getFechaImpago() == null)
			return "";
		else
			return f.format(recibo.getFechaImpago().getTime());
			
	}
	public String getReciboFase(){
		Recibo recibo = (Recibo) getCurrentRowObject();

		if (recibo == null)
			return "";
		else
			return StringUtils.nullToString(recibo.getFase());
	}
	public String getReciboImporte(){
		Recibo recibo = (Recibo) getCurrentRowObject();

		if (recibo == null)
			return "";
		else
			return StringUtils.nullToString(recibo.getImporte());
	}
	
	/******************************************/
	/*****MODIFICACIONES E INCIDENCIAS*********/
	/******************************************/
	
	public String getModIncAnio(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getAnio());
	}
	public String getModIncNumero(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getNumero());
	}
	public String getModIncTipo(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getTipo() +" - "+ incidencia.getDesTipo());
	}
	public String getModIncAsunto(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getAsunto() +" - "+ incidencia.getDesAsunto());
	}
	public String getModIncEstado(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getEstado() +" - "+ incidencia.getDesEstado());
	}
	public String getModIncFecha(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();
		//return incidencia.getFechaHoraEstado();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (incidencia == null || incidencia.getFechaHoraEstado() == null)
			return "";
		else
			return f.format(incidencia.getFechaHoraEstado().getTime());
	}
	public String getModIncDocAfectado(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getCodDocumentoAfectado() +" - "+ incidencia.getDesDocumentoAfectado());
	}
	public String getModIncIdEnvio(){
		Incidencia incidencia = (Incidencia) getCurrentRowObject();

		if (incidencia == null)
			return "";
		else
			return StringUtils.nullToString(incidencia.getIdEnvio());
	}
	
}
