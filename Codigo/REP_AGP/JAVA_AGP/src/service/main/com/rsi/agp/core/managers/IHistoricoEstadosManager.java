/**
 * 
 */
package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.Date;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;

/**
 * @author U029769 27/06/2013
 */
public interface IHistoricoEstadosManager {

	void insertaEstado(final Tabla tabla, final Long idObjeto, final String codUsuario, final BigDecimal idEstado);

	void insertaEstado(final Tabla tabla, final Long idObjeto, final String codUsuario, final BigDecimal idEstado,
			final Character estadoAgro);

	void insertaEstadoPoliza(final Long idObjeto, final String codUsuario, final BigDecimal idEstado,
			final BigDecimal tipoPago, final Date fPrimerPago, final BigDecimal pctPrimerPago, final Date fSegundoPago,
			final BigDecimal pctSegundoPago);

	void insertaEstadoDatosAseg(final Long idObjeto, final String codUsuario, final Character idEstado);

	boolean esNuevaContratacion(final Long idPoliza) throws BusinessException;

}
