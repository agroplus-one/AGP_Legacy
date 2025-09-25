package com.rsi.agp.core.managers.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import es.agroseguro.seguimientoContratacion.EstadoContratacion;
import es.agroseguro.seguimientoContratacion.Incidencia;
import es.agroseguro.seguimientoContratacion.InfoAdicional;
import es.agroseguro.seguimientoContratacion.Recibo;

public class SeguimientoPolizaBean implements Serializable {

	private static final long serialVersionUID = -7866204990672655306L;
	
	private String referenciaPoliza;
	private int digitoPoliza;
	private int plan;
	private int linea;
	private Character tipoPoliza;
	private String nifTomador;
	private String nombreTomador;
	private String colectivo;
	private String mediador;
	private String nifAsegurado;
	private Date fechaVigor;
	private Date fechaVencimiento;
	private Date fechaComunicacion;
	private BigDecimal costeTomador;
	private String digitoColectivo;
	
	private BigDecimal estado;
	private String desEstado;
	private Date fechaEstado;
	private BigDecimal motivoAnulRes;
	private String desMotivoAnulResc;
	
	private InfoAdicional[] infoAdicional;
	private Incidencia[] incidencia;
	private EstadoContratacion[] estados;
	private Recibo[] recibo;
	
	public SeguimientoPolizaBean(){}
	public SeguimientoPolizaBean(String referencia, int digitoControl, int plan, int linea, Character tipoPoliza, 
								String nifTomador, String nombreTomador, String colectivo, String mediador, String nifAsegurado, 
								Date fechaVigor, String digitoColectivo){
		
		this.referenciaPoliza = referencia;
		this.digitoPoliza = digitoControl;
		this.plan = plan;
		this.linea = linea;
		this.tipoPoliza = tipoPoliza;
		this.nifTomador = nifTomador;
		this.nombreTomador = nombreTomador;
		this.colectivo = colectivo;
		this.mediador = mediador;
		this.nifAsegurado = nifAsegurado;
		this.fechaVigor = fechaVigor;
		this.digitoColectivo = digitoColectivo;
		
		this.infoAdicional = new InfoAdicional[0];
		this.incidencia    = new Incidencia[0];
		this.estados       = new EstadoContratacion[0];
		this.recibo        = new Recibo[0];	
		
	}
	
	
	public static SeguimientoPolizaBean populate(es.agroseguro.seguimientoContratacion.Contratacion con){
		
		SeguimientoPolizaBean seguimientoPolizaBean = new SeguimientoPolizaBean();
		
		seguimientoPolizaBean.setReferenciaPoliza(con.getReferencia());
		seguimientoPolizaBean.setDigitoPoliza(con.getDigitoControl());
		seguimientoPolizaBean.setPlan(con.getPlan());
		seguimientoPolizaBean.setLinea(con.getLinea());
		if (con.getTipoPoliza() != null){
			seguimientoPolizaBean.setTipoPoliza(con.getTipoPoliza().toString().charAt(0));
		}
		seguimientoPolizaBean.setNifTomador(con.getNifTomador());
		seguimientoPolizaBean.setNombreTomador(con.getNombreTomador());
		seguimientoPolizaBean.setColectivo(con.getColectivo());
		seguimientoPolizaBean.setMediador(con.getMediador());
		seguimientoPolizaBean.setNifAsegurado(con.getNifAsegurado());
		if(con.getFechaVigor()!= null){
			seguimientoPolizaBean.setFechaVigor(con.getFechaVigor().getTime());
		}	
		if(con.getFechaVencimiento()!= null){
			seguimientoPolizaBean.setFechaVencimiento(con.getFechaVencimiento().getTime());
		}
		if(con.getFechaComunicacion()!= null){
			seguimientoPolizaBean.setFechaComunicacion(con.getFechaComunicacion().getTime());
		}	
		seguimientoPolizaBean.setCosteTomador(con.getCosteTomador());

		EstadoContratacion estadoContratacion = null;
		for (EstadoContratacion aux : con.getEstadoContratacionArray()) {
			if (estadoContratacion == null) {
				// Primera iteracion... nos quedamos provisionalmente con este
				// elemento
				estadoContratacion = aux;
			} else {
				int compare = aux.getFechaHora().getTime()
						.compareTo(estadoContratacion.getFechaHora().getTime());
				switch (compare) {
				case 0:
					// Estado con misma fecha de cambio... nos quedamos
					// provisionalmente con el estado con mayor codigo de estado
					if (aux.getEstado() > estadoContratacion.getEstado()) {
						estadoContratacion = aux;
					}
					break;
				case 1:
					// Estado con fecha de cambio superior... se establece
					// provisionalmente como ultimo estado de la poliza
					estadoContratacion = aux;
					break;
				case -1:
				default:
					// Estado con fecha de cambio anterior... se ignora
					break;
				}
			}
		}
		seguimientoPolizaBean.setEstado(new BigDecimal(estadoContratacion.getEstado()));
		seguimientoPolizaBean.setDesEstado(estadoContratacion.getDescEstado());
		seguimientoPolizaBean.setFechaEstado(estadoContratacion.getFechaHora().getTime());
		seguimientoPolizaBean.setMotivoAnulRes(estadoContratacion.isSetMotivoAnulResc() ? new BigDecimal(estadoContratacion.getMotivoAnulResc()) : null);
		seguimientoPolizaBean.setDesMotivoAnulResc(estadoContratacion.isSetDesMotivoAnulResc() ? estadoContratacion.getDesMotivoAnulResc() : "");
		
		seguimientoPolizaBean.setInfoAdicional(con.getInfoAdicionalArray());
		seguimientoPolizaBean.setIncidencia(con.getIncidenciaArray());
		
		seguimientoPolizaBean.setEstados(con.getEstadoContratacionArray());
		
		Recibo recibo = null;
		for (Recibo aux : con.getReciboArray()) {
			if (recibo == null) {
				recibo = aux;
			}			
		}
		
		seguimientoPolizaBean.setRecibo(con.getReciboArray());
		
		return seguimientoPolizaBean;
	}
	
	public String getReferenciaPoliza() {
		return referenciaPoliza;
	}
	public void setReferenciaPoliza(String referenciaPoliza) {
		this.referenciaPoliza = referenciaPoliza;
	}
	public int getDigitoPoliza() {
		return digitoPoliza;
	}
	public void setDigitoPoliza(int digitoPoliza) {
		this.digitoPoliza = digitoPoliza;
	}
	public int getPlan() {
		return plan;
	}
	public void setPlan(int plan) {
		this.plan = plan;
	}
	public int getLinea() {
		return linea;
	}
	public void setLinea(int linea) {
		this.linea = linea;
	}
	public Character getTipoPoliza() {
		return tipoPoliza;
	}
	public void setTipoPoliza(Character tipoPoliza) {
		this.tipoPoliza = tipoPoliza;
	}
	public String getNifTomador() {
		return nifTomador;
	}
	public void setNifTomador(String nifTomador) {
		this.nifTomador = nifTomador;
	}
	public String getNombreTomador() {
		return nombreTomador;
	}
	public void setNombreTomador(String nombreTomador) {
		this.nombreTomador = nombreTomador;
	}
	public String getColectivo() {
		return colectivo;
	}
	public void setColectivo(String colectivo) {
		this.colectivo = colectivo;
	}
	public String getMediador() {
		return mediador;
	}
	public void setMediador(String mediador) {
		this.mediador = mediador;
	}
	public String getNifAsegurado() {
		return nifAsegurado;
	}
	public void setNifAsegurado(String nifAsegurado) {
		this.nifAsegurado = nifAsegurado;
	}
	public Date getFechaVigor() {
		return fechaVigor;
	}
	public void setFechaVigor(Date fechaVigor) {
		this.fechaVigor = fechaVigor;
	}
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Date getFechaComunicacion() {
		return fechaComunicacion;
	}
	public void setFechaComunicacion(Date fechaComunicacion) {
		this.fechaComunicacion = fechaComunicacion;
	}
	public BigDecimal getCosteTomador() {
		return costeTomador;
	}
	public void setCosteTomador(BigDecimal costeTomador) {
		this.costeTomador = costeTomador;
	}
	
	public BigDecimal getEstado() {
		return estado;
	}

	public void setEstado(BigDecimal estado) {
		this.estado = estado;
	}

	public String getDesEstado() {
		return desEstado;
	}

	public void setDesEstado(String desEstado) {
		this.desEstado = desEstado;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public BigDecimal getMotivoAnulRes() {
		return motivoAnulRes;
	}

	public void setMotivoAnulRes(BigDecimal motivoAnulRes) {
		this.motivoAnulRes = motivoAnulRes;
	}

	public String getDesMotivoAnulResc() {
		return desMotivoAnulResc;
	}

	public void setDesMotivoAnulResc(String desMotivoAnulResc) {
		this.desMotivoAnulResc = desMotivoAnulResc;
	}

	public Incidencia[] getIncidencia() {
		return incidencia;
	}
	public void setIncidencia(Incidencia[] incidencias) {
		this.incidencia = incidencias;
	}
	
	public InfoAdicional[] getInfoAdicional() {
		return infoAdicional;
	}
	public void setInfoAdicional(InfoAdicional[] infoAdicional) {
		this.infoAdicional = infoAdicional;
	}

	public EstadoContratacion[] getEstados() {
		return estados;
	}
	public void setEstados(EstadoContratacion[] estadosCon) {
		this.estados = estadosCon;
	}
	public Recibo[] getRecibo() {
		return recibo;
	}
	public void setRecibo(Recibo[] recibo) {
		this.recibo = recibo;
	}
	public String getDigitoColectivo() {
		return digitoColectivo;
	}
	public void setDigitoColectivo(String digitoColectivo) {
		this.digitoColectivo = digitoColectivo;
	}
	
}
