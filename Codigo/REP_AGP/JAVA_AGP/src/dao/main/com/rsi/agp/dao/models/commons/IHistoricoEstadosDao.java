/**
 * 
 */
package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.Date;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

/**
 * @author U029769 27/06/2013
 */
@SuppressWarnings("rawtypes")
public interface IHistoricoEstadosDao extends GenericDao {

	/**
	 * @author U029769 27/06/2013
	 * @param nomTabla
	 * @param secuencia
	 * @param idObjeto
	 * @param codUsuario
	 * @param idEstado
	 */
	void insertaEstado(String nomTabla, String secuencia, Long idObjeto,
			String codUsuario, BigDecimal idEstado);
	/**
	 * @author U029769 23/07/2013
	 * @param nomTabla
	 * @param secuencia
	 * @param idObjeto
	 * @param codUsuario
	 * @param idEstado
	 */	
	public void insertaEstadoPoliza(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final BigDecimal idEstado, final BigDecimal tipoPago, final Date fPrimerPago,
			final BigDecimal pctPrimerPago, final Date fSegundoPago, final BigDecimal pctSegundoPago);
	
	void insertaEstadoAnexo(String nomTabla, String secuencia, Long idObjeto,
			String codUsuario, BigDecimal idEstado, Character estadoAgro);
	
    void insertaEstadoDatosAseg(final String nomTabla, final String secuencia, final Long idObjeto,
			final String codUsuario, final String idEstado);
    
	boolean esNuevaContratacion(Long idPoliza) throws DAOException;

}
