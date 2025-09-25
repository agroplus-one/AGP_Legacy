package com.rsi.agp.core.managers.impl.anexoMod.confirmacion;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;


public interface IConfirmacionModificacionManager {
	public AcuseRecibo generarPolizaActualizada (AnexoModificacion am, String realPath,String codUsuario, boolean hayCambiosDatosAsegurado);
	public AcuseRecibo generarPolizaActualizadaCpl (AnexoModificacion am, String realPath,String codUsuario, boolean hayCambiosDatosAsegurado);
	public AnexoModificacion getAnexoByIdCupon(Long id);
	public Map<String, String> confirmarAnexo (AnexoModificacion am, boolean indRevAdm, String codUsuario, String realPath);
	public Map<String, Object> getAcuseConfirmacion (Long idAnexo);
	public AcuseRecibo limpiaErroresWsAnexo(AcuseRecibo ar, String tipoLlamada, BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntida);
	
	/**
	 * Obtiene el cupón dado un id de cupón
	 * @param idCupon
	 * @return
	 */
	public Cupon obtenerCuponByIdCupon(Long idCupon);
	
	public boolean checkPerfil34(final String perfil, final BigDecimal forzarRevisionAM);
	public boolean validarCaractExplotacion(Poliza poliza, boolean isGanado) throws DAOException;
	/* Pet. 78691 ** MODIF TAM (15.12.2021) ** Inicio */
	public BigDecimal calcularCaractExplotacionAnx(final AnexoModificacion am, final String realPath, final String codUsuario, final Poliza poliza, boolean isGanado) throws BusinessException, DAOException;
}
