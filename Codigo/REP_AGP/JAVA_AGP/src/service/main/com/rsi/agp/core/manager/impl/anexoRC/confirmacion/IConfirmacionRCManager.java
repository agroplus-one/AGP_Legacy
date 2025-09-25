package com.rsi.agp.core.manager.impl.anexoRC.confirmacion;

import java.util.Map;

import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.ReduccionCapital;

import es.agroseguro.acuseRecibo.AcuseRecibo;


public interface IConfirmacionRCManager {

	public CuponRC getCuponRCByIdCupon(Long idCuponRC);
	public AcuseRecibo generarPolizaActualizada (ReduccionCapital reduccionCapital, String realPath,String codUsuario, boolean hayCambiosDatosAsegurado);
	public Map<String, String> confirmarReduccionCapital (ReduccionCapital reduccionCapital,boolean isRevisionAdmin,String codUsuario, String realPath);
	public ReduccionCapital getRCByIdRC(Long id);
	public Map<String, Object> getAcuseConfirmacion(long longValue);
		
	//	public AcuseRecibo generarPolizaActualizadaCpl (AnexoModificacion am, String realPath,String codUsuario, boolean hayCambiosDatosAsegurado);
//	public AnexoModificacion getAnexoByIdCupon(Long id);
//	public Map<String, String> confirmarAnexo (AnexoModificacion am, boolean indRevAdm, String codUsuario, String realPath);
//	public Map<String, Object> getAcuseConfirmacion (Long idAnexo);
//	public AcuseRecibo limpiaErroresWsAnexo(AcuseRecibo ar, String tipoLlamada, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntida);
//	
//	/**
//	 * Obtiene el cupón dado un id de cupón
//	 * @param idCupon
//	 * @return
//	 */
//	public Cupon obtenerCuponByIdCupon(Long idCupon);
//	
//	public boolean checkPerfil34(final String perfil, final BigDecimal forzarRevisionAM);
//	public boolean validarCaractExplotacion(Poliza poliza, boolean isGanado) throws DAOException;
//	/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Inicio */
//	public BigDecimal calcularCaractExplotacionAnx(final AnexoModificacion am, final String realPath, final String codUsuario, final Poliza poliza, boolean isGanado) throws BusinessException, DAOException;
}
