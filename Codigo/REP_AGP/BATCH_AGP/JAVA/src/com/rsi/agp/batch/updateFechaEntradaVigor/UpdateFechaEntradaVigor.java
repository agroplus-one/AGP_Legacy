package com.rsi.agp.batch.updateFechaEntradaVigor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class UpdateFechaEntradaVigor {

	private static final Logger logger = Logger.getLogger(UpdateFechaEntradaVigor.class);
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			logger.debug(" ");
			logger.debug("##--------------------------------------##");
			logger.debug("INICIO Batch UPDATE FECHA ENTRADA EN VIGOR");
			logger.debug("##--------------------------------------##");

			doWork();				 

			logger.debug("##-----------------------------------##");
			logger.debug("FIN Batch UPDATE FECHA ENTRADA EN VIGOR");
			logger.debug("##------------------------------------##");
			System.exit(0);
		} catch (Throwable e) {
			logger.error("# Error en el Update de fecha de entrada en vigor",e);
			System.exit(1);
		}
	}
		
	private static void doWork() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("agp_update_fecha_entrada_vigor");
		List<PolizaBean> lstPolizasAgr = new ArrayList<PolizaBean>();
		List<PolizaBean> lstPolizasRen = new ArrayList<PolizaBean>();
		List<PolizaBean> lstPolizasGan = new ArrayList<PolizaBean>();
		int contAgr =0;
		int contGan =0;
		int contRen =0;
		try {
			
			// 1.0 - dias que tiene que pasar desde que una poliza es enviada a Agroseguro y queda "enviada correcta"			
			BigDecimal dias = BBDDUpdateFechaEntradaVigor.getParametroFechaVigor();
			//BigDecimal dias = new BigDecimal(7);
			// 1.1 - maximo reintentos de llamada al WS
			int	reintentos = Integer.parseInt(bundle.getString("reintentos"));
			// 1.2 - plan a tratar en el batch
			String lstPlanes = bundle.getString("planes");			
			// 1.3 - lineas a tratar en el batch
			String lstLineas = bundle.getString("lineas");
			// 1.4 - max Polizas a tratar
			Integer maxPolizas = Integer.parseInt(bundle.getString("maxPolizas"));
						
			logger.debug("# Reintentos por timeOut: "+reintentos+ " parametroDias: "+dias+" planes: "+lstPlanes+" lineas: "+lstLineas+" maxPolizas: "+maxPolizas);
			
			// 2 - Polizas de Agricola
			lstPolizasAgr = BBDDUpdateFechaEntradaVigor.getPolizasAgricolas(lstPlanes,lstLineas,dias);
			
			// 3 - Update Polizas Agricolas
			if (lstPolizasAgr.size()>0)
				contAgr =UpdateFechaEntradaVigorWS.updatePolizasAgricolas(lstPolizasAgr,reintentos,contAgr,maxPolizas);
			maxPolizas = maxPolizas - contAgr;				
			// 4 - Polizas de Ganado
			lstPolizasGan = BBDDUpdateFechaEntradaVigor.getPolizasGanado(lstPlanes,lstLineas,dias);
			
			// 5 - Update Polizas de Ganado
			if (lstPolizasGan.size()>0)
				contGan = UpdateFechaEntradaVigorWS.updatePolizasGanado(lstPolizasGan,reintentos,contGan,maxPolizas);
			maxPolizas = maxPolizas - contGan;
			// 6 - Polizas Renovables
			lstPolizasRen = BBDDUpdateFechaEntradaVigor.getPolizasRenovables(lstPlanes,lstLineas,dias);
			
			// 7 - Update Polizas Renovables
			if (lstPolizasRen.size()>0)
				contRen =UpdateFechaEntradaVigorWS.updatePolizasRenovables(lstPolizasRen,reintentos,contRen,maxPolizas);
				
		} catch (Exception ex) {	
			logger.error("# Error inesperado en la ejecucion de Update de fecha de entrada en vigor",ex);
			throw new Exception();
		} 
		logger.debug("### Total polizas Agricolas  actualizadas: "+contAgr+" ###");
		logger.debug("### Total polizas de Ganado  actualizadas: "+contGan+" ###");
		logger.debug("### Total polizas Renovables actualizadas: "+contRen+" ###");		
	}			
	
	protected static class PolizaBean {

		private String plan;
		private String linea;
		private String referencia;
		private String tipoRef;
		
		public PolizaBean() {			
		}
		public PolizaBean(final String plan, final String linea,final String referencia,final String tipoRef) {
			this.plan = plan;
			this.linea = linea;
			this.referencia = referencia;
			this.tipoRef = tipoRef;
		}
		
		public void setPlan(String plan) {
			this.plan = plan;
		}


		public void setLinea(String linea) {
			this.linea = linea;
		}


		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}

		public String getPlan() {
			return plan;
		}

		public String getLinea() {
			return linea;
		}

		public String getReferencia() {
			return referencia;
		}
			
		public String getTipoRef() {
			return tipoRef;
		}
		public void setTipoRef(String tipoRef) {
			this.tipoRef = tipoRef;
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.plan);
			sb.append(" ");
			sb.append(this.linea);
			sb.append(" ");			
			sb.append(this.referencia);
			sb.append(" ");
			sb.append(this.tipoRef);
			sb.append(" ");
			return sb.toString();
		}
	}

}