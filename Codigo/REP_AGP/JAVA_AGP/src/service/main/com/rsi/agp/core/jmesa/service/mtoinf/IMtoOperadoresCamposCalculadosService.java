package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

public interface IMtoOperadoresCamposCalculadosService {
	
	/**
	 * Borra el objeto operador de campos calculados pasado como parámetro
	 * @param operadorCamposCalculados
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public Map<String, Object> bajaOperadorCampoCalculado(OperadorCamposCalculados operadorCamposCalculados) throws BusinessException;
	
	/**
	 * Obtiene el objeto operador de campos calculados correspondiente al id indicado en parámetro
	 * @param id
	 * @return
	 */
	public OperadorCamposCalculados getOperadorCampoCalculado(Long id) throws BusinessException;
	
	/**
	 * Realiza el alta del operador de campos calculados pasado como parámetro
	 * @param operadorCamposCalculados
	 * @return Devuelve un mapa con los errores producidos en el proceso de alta
	 */
	public Map<String, Object> altaOperadorCamposCalculados(OperadorCamposCalculados operadorCamposCalculados,
			String idCampoCalc) throws BusinessException;
	
	/**
	 * Realiza la modificación del operador de campos calculados pasado como parámetro
	 * @param operadorCamposCalculados
	 * @return Devuelve un mapa con los errores producidos en el proceso de modificación
	 */
	public Map<String, Object> updateOperadorCampoCalculado(String idOpCalculado, String idCampoCalc, String idOperador) throws BusinessException;
	
	public CamposCalculados getCampoCalculado(Long id) throws BusinessException;
	
	public Map<String, Object> altaOperadoresPorDefectoByCamCalc(Long idCampCalc) throws BusinessException;
	
}
