/**
 * 
 */
package com.rsi.agp.core.managers;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.vo.SigpacVO;

import es.agroseguro.seguroAgrario.infoReduccionParcelaFauna.InfoReduccionParcelaFauna;

/**
 * @author U029769
 */
public interface IDanhosFaunaManager {

	InfoReduccionParcelaFauna obtenerDanhosFauna (SigpacVO sigpac,String realPath) throws BusinessException, Exception;
	
}
