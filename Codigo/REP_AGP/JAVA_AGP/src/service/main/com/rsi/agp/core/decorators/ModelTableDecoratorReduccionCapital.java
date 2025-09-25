package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.action.DeclaracionesReduccionCapitalController;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

public class ModelTableDecoratorReduccionCapital extends TableDecorator {

	private static final Log logger = LogFactory.getLog(ModelTableDecoratorReduccionCapital.class);
	
	public String getAcciones() {
		String acciones = "";
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		Long idRedCap = reduccionCapital.getId();
		short estadoRC = reduccionCapital.getEstado().getIdestado();
		boolean isRedCapSW = false;
		CuponRC cupon = null;
		Long estadoCupon = new Long("-1");
		
		/*/MOCKEO
		//dato al iniciar getAcciones no hay valor del cupon
		CuponRC cuponRC = new CuponRC();
		reduccionCapital.setCupon(cuponRC);
		reduccionCapital.getCupon().getEstadoCupon().setId(new Long(4));
		// dato al iniciar getAcciones no hay valor del cupon
		//MOCKEO*/
		
		// Comprobamos si es un RC por SW o FTP
		if (reduccionCapital.getCupon() != null) {
			isRedCapSW = true;
			cupon = reduccionCapital.getCupon();
			estadoCupon = cupon.getEstadoCupon().getId();
		}
		
		// Estados que permiten la edicion
		if (Constants.REDUCCION_CAPITAL_ESTADO_BORRADOR.equals(estadoRC)
				|| Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO.equals(estadoRC)
				|| Constants.REDUCCION_CAPITAL_ESTADO_DEFINITIVO.equals(estadoRC)) {
			// Si el cupon esta caducado, se vuelve a solicitar
			if (Constants.AM_CUPON_ESTADO_CADUCADO.equals(estadoCupon) || reduccionCapital.getCupon() == null) {
				// Editar RC solicitando cupon nuevo
				acciones += "<a href=\"#\" onclick=\"javascript:editarRCCuponCaducado(" + idRedCap + ","
						+ reduccionCapital.getPoliza().getIdpoliza() + ",'"
						+ reduccionCapital.getPoliza().getReferencia() + "',"
						+ reduccionCapital.getPoliza().getLinea().getCodplan() + ")\">"
						+ "<img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;";
			} else {
				// Editar RC
				acciones += "<a href=\"#\" onclick=\"javascript:editar(" + idRedCap + "," + 
						+ reduccionCapital.getPoliza().getIdpoliza() + "," + 
						estadoRC + 
						")\"><img src='jsp/img/displaytag/edit.png' alt='Editar' title='Editar'/></a>&nbsp;";
			}
			
			// Eliminar RC
			acciones += "<a href=\"#\" onclick=\"javascript:eliminar("+ idRedCap+ ")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>&nbsp;";
		} else {
			// No permite la edicion, no se muestra eliminar
			// Informacion
			acciones += "<a href=\"#\" onclick=\"javascript:informacion("+ idRedCap + ")\"><img src='jsp/img/displaytag/information.png' alt='Visualizar' title='Visualizar'/></a>&nbsp;";
		}
		
		// Imprimir RC
		if (isRedCapSW && cupon.getId() != null && estadoCupon.equals(Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO)) {
			acciones += "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimirSwPDFIncidencia('"
					+ cupon.getIdcupon() + "')\">"
					+ "<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Reduccion de Capital' title='Imprimir Reduccion de Capital'/></a>&nbsp;";
		} else {
			acciones += "<a href=\"#\" rel=\"external\" onclick=\"javascript:imprimir(" + idRedCap
					+ ")\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir Reduccion de Capital' title='Imprimir Reduccion de Capital'/></a>&nbsp;";
		}
		
		// Consultar Acuse de Recibo
		if (isRedCapSW && (Constants.AM_CUPON_ESTADO_ERROR_RECHAZADO.equals(estadoCupon)
				|| Constants.AM_CUPON_ESTADO_ERROR_TRAMITE.equals(estadoCupon)
				|| Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE.equals(estadoCupon)
				|| Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO.equals(estadoCupon))) {
			acciones += "<a href=\"#\" onclick=\"javascript:verAcuseRecibo(" + idRedCap
					+ ")\"><img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo' /></a>";
		}
		
		/*/segun el DT no hay condicion, se debe mostrar simepre?
		if (idEstado.equals(Constants.REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO)){
			acciones += "<a href=\"#\" onclick=\"javascript:verAcuseRecibo(" + idRedCap
				+ ")\"><img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo' /></a>&nbsp;";
		}*/

		return acciones;
	}

	public Long getColumnaId() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		
		if (reduccionCapital.getId() != null) {
			return reduccionCapital.getId();
		} else {
			return 0L;
		}
	}

	public BigDecimal getColumnaNumAnexo() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		
		if (reduccionCapital.getNumAnexo() != null) {
			return reduccionCapital.getNumAnexo();
		} else {
			return BigDecimal.ZERO;
		}
	}

	public Date getColumnaOcurrencia() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		Date fecha = null;
		if (reduccionCapital.getFechadanios() != null) {
			fecha = reduccionCapital.getFechadanios();
		}

		return fecha;
	}

	public String getColumnaRiesgo() {		
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		String codMotivoRiesgo = reduccionCapital.getCodmotivoriesgo();
		
		if (codMotivoRiesgo == null) {
			return "";
		} else {
			return String.format("%02d", Integer.parseInt(reduccionCapital.getCodmotivoriesgo()));
		}		
	}

	public String getColumnaDescripcion() {
		return "";
	}

	public String getColumnaEstado() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		logger.debug("ModelTableDecoratorReduccionCapital "+reduccionCapital.toDebugString());
		if (reduccionCapital.getEstado() == null) {
			return " - ";
		} else {
			return reduccionCapital.getEstado().getDescestado().toString();
		}
	}

	public Date getColumnaEnvio() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		Date fecha = null;
		if (reduccionCapital.getFechafirma() != null) {
				fecha = reduccionCapital.getFechafirma();
		}
		return fecha;
	}
	
	//P0079361
	public String getColumnaNumero() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		String numeroCupon = " - ";
		if (reduccionCapital.getCupon() != null) {
			if (reduccionCapital.getCupon().getIdcupon() != null) {
				return reduccionCapital.getCupon().getIdcupon();
			}
		}
		return numeroCupon;
	}
	
	public String getColumnaEstadoCupon() {
		ReduccionCapital reduccionCapital = (ReduccionCapital) getCurrentRowObject();
		String estadoCupon = " - ";
		if (reduccionCapital.getCupon() != null) {
			if(reduccionCapital.getCupon().getEstadoCupon() != null) {
				if(reduccionCapital.getCupon().getEstadoCupon().getId() != null) {
					estadoCupon = reduccionCapital.getCupon().getEstadoCupon().getEstado();
				}
			}
		}
		return estadoCupon;
	}
	//P0079361
}
