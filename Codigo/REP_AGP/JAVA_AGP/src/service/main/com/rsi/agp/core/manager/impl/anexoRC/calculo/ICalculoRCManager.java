package com.rsi.agp.core.manager.impl.anexoRC.calculo;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

public interface ICalculoRCManager {
	public Map<String, Object> calcularModificacion (final String realPath, final long idrc, final Usuario usuario,final boolean actualizaComMediadora);
//	public Map<String, Object> consultaDistribucionCoste (final AnexoModificacion am);
	public Poliza getPoliza(Long long1);
//	public Map<String, Object> muestraBotonDescuentoAnexo(Poliza poliza,AnexoModificacion anexo,Usuario usuario)throws Exception;
	public ReduccionCapital getRC(Long long1);
//	public void actualizaAnexoDescuento(final String [] varRequest, final Long idAnexo) throws Exception;
//	public void actualizaAnexoRecargo(final String [] varRequest, final Long idAnexo) throws Exception;
//	public BigDecimal comprobarTotalRecargo(Long idAnexo,BigDecimal recargo ) throws Exception;
//	AnexoModificacion getAnexoPorIdCupon(String idCupon) throws DAOException;
}
