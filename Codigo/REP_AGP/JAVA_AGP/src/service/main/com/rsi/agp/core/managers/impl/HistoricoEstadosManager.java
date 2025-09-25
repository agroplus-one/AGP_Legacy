/**
 * 
 */
package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.dao.models.commons.IHistoricoEstadosDao;

/**
 * @author U029769 27/06/2013
 */
public class HistoricoEstadosManager implements IHistoricoEstadosManager{

	private  IHistoricoEstadosDao historicoEstadosDao;
	private static final Log LOG = LogFactory.getLog(HistoricoEstadosManager.class);
	
	public enum Tabla {
		ANEXO_MOD, ANEXO_RED, SINIESTRO, POLIZAS, SBP, DATOS_ASEG
		}
	
	private static final String TAANEXOMOD = "TB_ANEXO_MOD_HISTORICO_ESTADOS";
	private static final String TANEXORED = "TB_ANEXO_RED_HISTORICO_ESTADOS";
	private static final String TSINIESTRO = "TB_SINIESTRO_HISTORICO_ESTADOS";
	private static final String TPOLIZAS = "TB_POLIZAS_HISTORICO_ESTADOS";
	private static final String TSBP = "TB_SBP_HISTORICO_ESTADOS";
	private static final String TDATOSASEG = "TB_DATOS_ASEGURADO_HIST_ESTADO";
	
	private static final String SQANEXOMOD = "SQ_ANEXO_MOD_HISTORICO_ESTADOS.nextval";
	private static final String SQANEXORED = "SQ_ANEXO_RED_HISTORICO_ESTADOS.nextval";
	private static final String SQINIESTRO = "SQ_SINIESTRO_HISTORICO_ESTADOS.nextval";
	private static final String SQPOLIZAS = "SQ_POLIZAS_HISTORICO_ESTADOS.nextval";
	private static final String SQSBP = "SQ_SBP_HISTORICO_ESTADOS.nextval";
	private static final String SQDATOSASEG = "SQ_DATOS_ASEGURADO_HIST_ESTADO.nextval";
	
	
	public void insertaEstado(final Tabla tabla,final Long idObjeto,final String codUsuario,final BigDecimal idEstado) {
		insertaEstado(tabla, idObjeto, codUsuario, idEstado, null);
	}
	
	/**
	 * Llama al pl que inserta en el historico de estados de polizas,A.M,R.D,Siniestros y sbp
	 * @author U029769 23/07/2013
	 * @param tabla
	 * @param idObjeto
	 * @param codUsuario
	 * @param idEstado
	 */
	public void insertaEstado(final Tabla tabla,final Long idObjeto,final String codUsuario,final BigDecimal idEstado, final Character estadoAgro) {
		
		LOG.debug("INIT insertarEstado");
		String nomTabla=null;
		String secuencia=null;
		boolean tablaPolizas = false;
		boolean tablaAnexos = false;
		switch (tabla) {
			case ANEXO_MOD:
				nomTabla = TAANEXOMOD;
				secuencia = SQANEXOMOD;
				tablaAnexos = true;
				historicoEstadosDao.insertaEstadoAnexo(nomTabla, secuencia, idObjeto, codUsuario, idEstado, estadoAgro);
				break;
			case ANEXO_RED:
				nomTabla = TANEXORED;
				secuencia = SQANEXORED;
				break;
			case SINIESTRO:
				nomTabla = TSINIESTRO;
				secuencia = SQINIESTRO;
				break;
			case POLIZAS:
				nomTabla = TPOLIZAS;
				secuencia = SQPOLIZAS;
				tablaPolizas = true;
				historicoEstadosDao.insertaEstadoPoliza(nomTabla, secuencia, idObjeto, codUsuario, idEstado, null, null,
					null, null, null);
				break;
			case SBP:
				nomTabla = TSBP;
				secuencia = SQSBP;
				break;
			case DATOS_ASEG:
				nomTabla = TDATOSASEG;
				secuencia = SQDATOSASEG;
				break;
		}
		if (!tablaPolizas && !tablaAnexos)
			historicoEstadosDao.insertaEstado(nomTabla, secuencia, idObjeto, codUsuario, idEstado);
		
		LOG.debug("FIN insertarEstado");
	}
	
public void insertaEstadoPoliza(final Long idObjeto,final String codUsuario,final BigDecimal idEstado,
		final BigDecimal tipoPago, final Date fPrimerPago,
		final BigDecimal pctPrimerPago, final Date fSegundoPago, final BigDecimal pctSegundoPago) {
		
		LOG.debug("INIT insertarEstadoPoliza");
		String nomTabla=TPOLIZAS;
		String secuencia=SQPOLIZAS;
		
		historicoEstadosDao.insertaEstadoPoliza(nomTabla, secuencia, idObjeto, codUsuario, idEstado, tipoPago,
				fPrimerPago, pctPrimerPago, fSegundoPago, pctSegundoPago);
		
		LOG.debug("FIN insertarEstadoPoliza");
	}
	
	/**
	 * Inserta en el historico de estados de datos asegurados
	 * @author U029769 23/07/2013
	 * @param tabla
	 * @param idObjeto
	 * @param codUsuario
	 * @param idEstado
	 */
	public void insertaEstadoDatosAseg(final Long idObjeto, final String codUsuario, final Character idEstado) {
		
		LOG.debug("INIT insertarEstado");
		
		historicoEstadosDao.insertaEstadoDatosAseg(TDATOSASEG, SQDATOSASEG, idObjeto, codUsuario, idEstado.toString());
		
		LOG.debug("FIN insertarEstado");
	}
	
	public void setHistoricoEstadosDao(IHistoricoEstadosDao historicoEstadosDao) {
		this.historicoEstadosDao = historicoEstadosDao;
	}

	@Override
	public boolean esNuevaContratacion(Long idPoliza) throws BusinessException {
		
		boolean resultado = false;
		
		try {
			resultado = this.historicoEstadosDao.esNuevaContratacion(idPoliza);
		} catch (Exception ex) {
			LOG.error(ex);
		    throw new BusinessException("[FechasContratacionManager] validarPorParcela() - error ",ex);
		}
		
		return resultado;
	}

}
