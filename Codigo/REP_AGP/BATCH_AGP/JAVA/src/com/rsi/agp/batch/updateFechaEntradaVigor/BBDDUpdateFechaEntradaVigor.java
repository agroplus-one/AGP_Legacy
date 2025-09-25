package com.rsi.agp.batch.updateFechaEntradaVigor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import com.rsi.agp.batch.bbdd.Conexion;
import org.apache.log4j.Logger;
import com.rsi.agp.batch.updateFechaEntradaVigor.UpdateFechaEntradaVigor.PolizaBean;
import com.rsi.agp.core.exception.DAOException;


public final class BBDDUpdateFechaEntradaVigor {
	
	private BBDDUpdateFechaEntradaVigor() {
	}
	private static final Logger logger = Logger.getLogger(BBDDUpdateFechaEntradaVigor.class);
	
		// Método que devuelve las polizas con grupo seguro A01 en estado "enviada correcta"y fecha en vigor nula
		public static List<PolizaBean>  getPolizasAgricolas(String lstPlanes,String lstLineas, BigDecimal dias) throws DAOException {
			List<PolizaBean> lstPolizasAgricolas = new ArrayList<PolizaBean>();
			StringBuilder stringQuery = new StringBuilder();
			Conexion c = new Conexion();
			//dias = new BigDecimal(150);
			//planActual = 2016;
			try {
				stringQuery.append("select lin.codplan,lin.codlinea,pol.referencia,pol.tiporef"+
						" from o02agpe0.tb_polizas pol,o02agpe0.tb_lineas lin, o02agpe0.tb_sc_c_lineas sclin"+ 
						" where pol.lineaseguroid = lin.lineaseguroid and pol.fecha_vigor is null"+
						" and lin.codlinea = sclin.codlinea"+						
						//" and pol.referencia in('H476901','H476908')"+
						" and sclin.codgruposeguro='A01'"+
						" and pol.idestado = "+UpdateFEVConstants.ESTADO_POLIZA_DEFINITIVA+
						" and sysdate-"+dias+" >= pol.fechaenvio");
				
				if (!lstPlanes.equals("0")) {
					stringQuery.append(" and lin.codplan in("+lstPlanes+")");
				}else{
					Calendar c2 = new GregorianCalendar();
					int planActual = c2.get(Calendar.YEAR);
					stringQuery.append(" and lin.codplan in("+planActual+","+(planActual-1)+")");
				}
				
				if (lstLineas != null && !lstLineas.equals(""))
					stringQuery.append(" and lin.codlinea in("+lstLineas+")");
				
				logger.info("Consulta Agr: ********* "	+ stringQuery.toString());
				
				List lista = c.ejecutaQuery(stringQuery.toString(), 4);					
				if (lista != null && lista.size()>0) {
					logger.info(" ## Polizas agrícolas a procesar: "	+ lista.size()+" ##");			
					lstPolizasAgricolas =cargaLista(lista);
				}else {
					logger.info(" ## sin polizas agricolas con fecha vigor a null.");
				}					
			} catch (Exception e) {
				throw new DAOException("Se ha producido un error al recoger las pólizas agrícolas de bbdd", e);
			}
			return lstPolizasAgricolas;
			
		}
		
		// Método que devuelve las polizas con grupo seguro G01 no renovables en estado "enviada correcta" y fecha en vigor nula
				public static List<PolizaBean>  getPolizasGanado(String lstPlanes,String lstLineas,BigDecimal dias) throws DAOException {
					List<PolizaBean> lstPolizasGanado = new ArrayList<PolizaBean>();
					StringBuilder stringQuery = new StringBuilder();
					Conexion c = new Conexion();
					//dias = new BigDecimal(350);
					//planActual = 2015;
					try {
						stringQuery.append("select lin.codplan,lin.codlinea,pol.referencia,pol.tiporef"+
								" from o02agpe0.tb_polizas pol,o02agpe0.tb_lineas lin, o02agpe0.tb_sc_c_lineas sclin"+ 
								" where pol.lineaseguroid = lin.lineaseguroid and pol.fecha_vigor is null"+
								" and lin.codlinea = sclin.codlinea"+
								//" and pol.referencia in('126190W','124201Y')"+
								" and sclin.codgruposeguro='G01'"+
								" and pol.referencia not in (select reno.referencia from o02agpe0.tb_polizas_renovables reno)"+
								" and pol.idestado = "+UpdateFEVConstants.ESTADO_POLIZA_DEFINITIVA+
								" and sysdate-"+dias+" >= pol.fechaenvio");
						
						if (!lstPlanes.equals("0")) {
							stringQuery.append(" and lin.codplan in("+lstPlanes+")");
						}else{
							Calendar c2 = new GregorianCalendar();
							int planActual = c2.get(Calendar.YEAR);
							stringQuery.append(" and lin.codplan in("+planActual+","+(planActual-1)+")");
						}
						if (lstLineas != null && !lstLineas.equals(""))
							stringQuery.append(" and lin.codlinea in("+lstLineas+")");
						
						logger.info("Consulta Gan: ********* "	+ stringQuery.toString());
						List lista = c.ejecutaQuery(stringQuery.toString(), 4);				
						if (lista != null && lista.size()>0) {
							logger.info(" ## Polizas de Ganado a procesar: "	+ lista.size()+" ##");
							lstPolizasGanado =cargaLista(lista);
						}else {
							logger.info(" ## sin polizas de Ganado con fecha vigor a null.");
						}
							
					} catch (Exception e) {
						throw new DAOException("Se ha producido un error al recoger las pólizas de Ganado de bbdd", e);
					}
					return lstPolizasGanado;
					
				}
		
		// Método que devuelve las polizas renovables en estado "enviada correcta" cuya poliza tiene fecha en vigor nula
		public static List<PolizaBean>  getPolizasRenovables(String lstPlanes,String lstLineas, BigDecimal dias) throws DAOException {
			List<PolizaBean> lstPolizasRenovables = new ArrayList<PolizaBean>();
			StringBuilder stringQuery = new StringBuilder();
			Conexion c = new Conexion();
			//dias = new BigDecimal(350);
			//planActual = 2015;
			try {
				stringQuery.append("select lin.codplan,lin.codlinea,pol.referencia,pol.tiporef"+
						" from o02agpe0.tb_polizas pol,o02agpe0.tb_lineas lin, o02agpe0.tb_sc_c_lineas sclin,o02agpe0.tb_polizas_renovables renn "+ 
						" where pol.lineaseguroid = lin.lineaseguroid and pol.fecha_vigor is null"+
						" and lin.codlinea = sclin.codlinea"+
						//" and pol.referencia in('E229922','E229937')"+
						" and sclin.codgruposeguro='G01'"+
						" and pol.referencia=renn.referencia"+
						" and lin.codlinea = renn.linea"+
						" and lin.codplan = renn.plan"+						
						" and ((pol.idestado = "+UpdateFEVConstants.ESTADO_POLIZA_DEFINITIVA+
						" and sysdate-"+dias+" >= pol.fechaenvio) or"+
						" (pol.idestado = "+UpdateFEVConstants.ESTADO_POLIZA_EMITIDA+ " and sysdate - "+dias+" >= renn.fecha_renovacion))");

				if (!lstPlanes.equals("0")) {
					stringQuery.append(" and lin.codplan in("+lstPlanes+")");
				}else{
					Calendar c2 = new GregorianCalendar();
					int planActual = c2.get(Calendar.YEAR);
					stringQuery.append(" and lin.codplan in("+planActual+","+(planActual-1)+")");
				}
				if (lstLineas != null && !lstLineas.equals(""))
					stringQuery.append(" and lin.codlinea in("+lstLineas+")");
				
				logger.info("Consulta Ren: ********* "	+ stringQuery.toString());
				List lista = c.ejecutaQuery(stringQuery.toString(), 4);				
				if (lista != null && lista.size()>0) {
					logger.info(" ## Polizas Renovables a procesar: "	+ lista.size()+" ##");
					lstPolizasRenovables =cargaLista(lista);
				}else {
					logger.info(" ## sin polizas renovables con fecha vigor a null.");
				}
					
			} catch (Exception e) {
				throw new DAOException("Se ha producido un error al recoger las pólizas renovables de bbdd", e);
			}
			return lstPolizasRenovables;
			
		}
		
		
		// Método que devuelve lo días que tiene que pasar desde que una póliza es enviada a Agroseguro y queda "enviada correcta"
				public static BigDecimal  getParametroFechaVigor() throws DAOException {
					
					Conexion c = new Conexion();
					BigDecimal res = null;
					try {
						String sql =("select num_dias_fecha_vigor from o02agpe0.tb_parametros par");
						logger.info(" ## sql:"+sql);
						List lista = c.ejecutaQuery(sql, 1);				
						if (lista != null && lista.size()>0) {
							Object[] registro = (Object[]) lista.get(0);
							res = (BigDecimal)registro[0]; 
						}else {
							logger.info(" ## sin parametro de BBDD de fecha_vigor.");
						}
						logger.info(" ## sql fin");	
					} catch (Exception e) {
						throw new DAOException("Se ha producido un error al recoger el parametro de BBDD de fecha_vigor", e);
					}
					return res;
					
				}
				
		public static List<PolizaBean>  cargaLista(final List lista) {
			List<PolizaBean> lstPol = new ArrayList<PolizaBean>();		
			for (int j = 0; j < lista.size(); j++){
				Object[] registro = (Object[]) lista.get(j);
				BigDecimal plan   = (BigDecimal)registro[0];
				BigDecimal linea  = (BigDecimal)registro[1];
				String referencia = (String)registro[2];
				String tipoRef    = (String)registro[3];
				//logger.debug(" #"+j+"# plan: "+plan+ " linea: "+linea+" referencia: "+referencia+" tipoRef: "+tipoRef);
				PolizaBean pol = new PolizaBean(plan.toString(),linea.toString(),referencia,tipoRef);
				lstPol.add(pol);
			}
			return lstPol;
		}
		
		// Método que actualiza la fecha de entrada en vigor de la póliza
		public static void actualizaFechaVigor(final String fechaVigor, final String referencia, final String plan, 
				final String linea, final String tipoRef) throws Exception {			
			try{
				Conexion c = new Conexion();
				
				Date fec = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
				String fecha = sdf.format(fec);				
				String qr = " update o02agpe0.tb_polizas a set fecha_vigor = to_date('"+fechaVigor+"','dd/MM/yyyy'),FECHA_MODIFICACION = to_date('"+fecha+ "','DD/MM/YYYY HH24:mi:ss')"+
						" where referencia ='"+referencia+"' and tiporef='"+tipoRef+"'"+
						" and a.lineaseguroid = (select b.lineaseguroid from o02agpe0.tb_lineas b where b.codplan="+plan+" and b.codLinea ="+linea+")";					
				logger.debug(qr);
				c.ejecutaUpdate(qr);
				logger.debug("ACT: " + referencia + " plan: "+plan+" linea: "+linea+ " tipoRef: "+tipoRef+" fechaVigor: "+fechaVigor);
			}catch(Exception e ){
				logger.error("## ERROR en actualizaFechaVigor ##  ",e);
				throw e;
			}
		}
		
}
