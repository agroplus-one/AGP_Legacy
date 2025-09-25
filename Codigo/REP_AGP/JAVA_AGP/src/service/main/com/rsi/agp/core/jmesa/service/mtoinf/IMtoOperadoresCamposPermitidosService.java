package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

public interface IMtoOperadoresCamposPermitidosService {
	
	/**
	 * Borra el objeto operador de campos permitidos pasado como parámetro
	 * @param operadorCamposPermitidos
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public Map<String, Object> bajaOperadorCamposPermitidos(OperadorCamposPermitido operadorCamposPermitidos) throws BusinessException;
	
	/**
	 * Obtiene el objeto operador de campos permitidos correspondiente al id indicado en parámetro
	 * @param id
	 * @return
	 */
	public OperadorCamposPermitido getOperadorCamposPermitidos(Long id) throws BusinessException;
	
	public CamposPermitidos getCampoPermitido(Long id) throws BusinessException;
	
	public VistaCampo getVistaCampo(BigDecimal id) throws BusinessException;
	
	/**
	 * Realiza el alta del operador de campos permitidos pasado como parámetro
	 * @param operadorCamposPermitidos
	 * @return Devuelve un mapa con los errores producidos en el proceso de alta
	 */
	public Map<String, Object> altaOperadorCamposPermitidos(OperadorCamposPermitido operadorCamposPermitidos, String idCamPer) throws BusinessException;
	
	/**
	 * Realiza la modificación del operador de campos permitidos pasado como parámetro
	 * @param operadorCamposPermitidos
	 * @return Devuelve un mapa con los errores producidos en el proceso de modificación
	 */
	public Map<String, Object> updateOperadorCamposPermitidos(String idOpCampoPermitido, String idcampoPerm, String idOperador) throws BusinessException;
	
	public Map<String, Object> altaOperadoresPorDefectoByCamPer(BigDecimal idVistaCampo,BigDecimal tipo) throws BusinessException;

}
