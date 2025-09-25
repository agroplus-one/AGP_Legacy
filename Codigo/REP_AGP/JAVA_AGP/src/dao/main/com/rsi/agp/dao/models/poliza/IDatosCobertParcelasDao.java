/**********************************************/
/** CREATE: 29/09/2020, T-SYSTEMS            **/
/** Fuente nuevo para Coberturas de Parcelas **/
/** PETICIÓN: 63485 - FASE II                **/
/**********************************************/
package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IDatosCobertParcelasDao extends GenericDao {
	
	public String getCobParcelas(String codModulo, Long idPoliza, Integer numParc, Integer hojaParc)
			throws DAOException;

	public Boolean obtenerParcelaElegida(BigDecimal codConcepto, Long idParcela,
			String codModulo, BigDecimal codRiesgoCub) throws DAOException;

	public String getCobParcelasAnexo(String codmodulo, Long id, Long idParcela, Integer numeroParc, Integer hojaParc)
			throws DAOException;

	public Boolean obtenerParcelaElegidaAnexo(BigDecimal codConcepto, Long idParcelaAnx,
			String codModulo, BigDecimal codRiesgoCub) throws DAOException;
	
	public String obtenerParcValorElegido(BigDecimal codConcepto, Long idParcela, String codModulo) throws DAOException;
	
	public String obtenerParcValorElegidoAnexo(BigDecimal codConcepto, Long idParcela) throws DAOException;
}