package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposCalculadosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoOperadorCamposCalculadosDao;
import com.rsi.agp.dao.tables.mtoinf.CamposCalculados;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposCalculados;

public class MtoOperadoresCamposCalculadosService implements IMtoOperadoresCamposCalculadosService {
	
	private IMtoOperadorCamposCalculadosDao mtoOperadorCamposCalculadosDao;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");

	@Override
	public Map<String, Object> altaOperadorCamposCalculados(OperadorCamposCalculados operadorCamposCalculados,
			String idCampoCalc) throws BusinessException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		CamposCalculados camCalc = new CamposCalculados();
		String idOpCalcEncontrado = "";
		try {
			idOpCalcEncontrado = mtoOperadorCamposCalculadosDao.checkCampo_OperadorExists(idCampoCalc, operadorCamposCalculados.getIdoperador(),null);
			if (!StringUtils.nullToString(idOpCalcEncontrado).equals("")) {
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_ALTA_EXISTE_KO));
			}else{
				camCalc = this.getCampoCalculado(Long.parseLong(idCampoCalc));
				operadorCamposCalculados.setCamposCalculados(camCalc);
				mtoOperadorCamposCalculadosDao.saveOrUpdate(operadorCamposCalculados);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_ALTA_OK));
			}					
		} catch (Exception ex) {
			throw new BusinessException("MtoOperadoresCamposCalculadosService - altaOperadorCamposCalculados - Error al dar de alta el Operador campo Calculado", ex);
		}
		
		return parameters;
	}
	
	/**
	 * Realiza el alta de los operadores por defecto para ese campo calculado
	 * @param idCampCalc
	 */
	public Map<String, Object> altaOperadoresPorDefectoByCamCalc(Long idCampCalc) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_ENTRE));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_IGUAL));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE));
			altaOp(idCampCalc,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MENOR_QUE));
								
		} catch (Exception ex) {
			throw new BusinessException("MtoCamposPermitidosService - altaOperadoresPorDefectoByCamCalc - Error al dar de alta el Operador campo calculado", ex);
		}
		return parameters;
	}
	
	/**
	 * inserta en BBDD un operador dado un idcampoCalculado y el idOperador
	 * @param idCampoCalc
	 * @param idOperador
	 */
	public void altaOp(Long idCampoCalc,BigDecimal idOperador){
		OperadorCamposCalculados opCamCalc = new OperadorCamposCalculados();
		CamposCalculados camCalc = new CamposCalculados();
		try {
			// Comprueba si ya existe la relación entre el operador y el campo calculado
			String idOpCalcEncontrado = mtoOperadorCamposCalculadosDao.checkCampo_OperadorExists(idCampoCalc.toString(),idOperador,null);
			// Si no existe lo da de alta
			if (StringUtils.nullToString(idOpCalcEncontrado).equals("")) {
				camCalc = this.getCampoCalculado(idCampoCalc);
				opCamCalc.setCamposCalculados(camCalc);
				opCamCalc.setIdoperador(idOperador);
				mtoOperadorCamposCalculadosDao.saveOrUpdate(opCamCalc);
			}
			
		} catch (Exception ex) {
			logger.error("Ocurrió un error al comprueba si ya existe la relación entre el operador y el campo calculado" , ex);
		}
	}
	
	@Override
	public Map<String, Object> bajaOperadorCampoCalculado(OperadorCamposCalculados operadorCamposCalculados) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			boolean existeCondicionCamCalc = false;
			
			// reviso si hay condicion para ese campo permitido
			existeCondicionCamCalc = mtoOperadorCamposCalculadosDao.existeCondicionCamCalc(operadorCamposCalculados.getId().toString());
			if (!existeCondicionCamCalc){
				mtoOperadorCamposCalculadosDao.delete(operadorCamposCalculados);
				logger.debug("operadorCamposCalculado borrado = " + operadorCamposCalculados.getId());
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_BAJA_OK));
			}else{ // aviso de que ya hay una condiciÃ³n para ese campo Calculado
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_CONDICION_EXISTE_KO));
			}
		} catch (Exception ex) {
			logger.error("Error al eliminar el operadorCampoCalculado", ex);
			throw new BusinessException("Error al eliminar el operadorCampoCalculado", ex);
		}
		return parameters;
	}

	@Override
	public OperadorCamposCalculados getOperadorCampoCalculado(Long id) throws BusinessException{
		try {
			return (OperadorCamposCalculados) mtoOperadorCamposCalculadosDao.getObject(OperadorCamposCalculados.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el OperadorCamposCalculado", dao);
			throw new BusinessException("Se ha producido al obtener el OperadorCamposCalculado:", dao);
		}
	}

	@Override
	public Map<String, Object> updateOperadorCampoCalculado(String idOpCalculado, String idCampoCalc, String idOperador) throws BusinessException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		OperadorCamposCalculados opCamCalc = new OperadorCamposCalculados();
		CamposCalculados camCalc = new CamposCalculados();
		boolean existeCondicionCamCalc = false;
		try {
			// cargo el objeto desde la bbdd
			opCamCalc = this.getOperadorCampoCalculado(Long.valueOf(idOpCalculado));
			camCalc = this.getCampoCalculado(Long.parseLong(idCampoCalc));
			
			// reviso si hay condicion para ese campo permitido
			String idOpCalcEncontrado = "";
			existeCondicionCamCalc = mtoOperadorCamposCalculadosDao.existeCondicionCamCalc(idOpCalculado);
			if (!existeCondicionCamCalc){
				idOpCalcEncontrado = mtoOperadorCamposCalculadosDao.checkCampo_OperadorExists(idCampoCalc, new BigDecimal(idOperador),idOpCalculado);
				if (!StringUtils.nullToString(idOpCalcEncontrado).equals("")) {
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_ALTA_EXISTE_KO));
				}else{
					opCamCalc.setCamposCalculados(camCalc);
					opCamCalc.setIdoperador(new BigDecimal(idOperador));
					
					mtoOperadorCamposCalculadosDao.saveOrUpdate(opCamCalc);
					logger.debug("OperadorCampoCalculado editado con id= "+opCamCalc.getId());
					parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_MODIF_OK));
				}
			}else{ // aviso de que ya hay una condiciÃ³n para ese campo calculado
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_CONDICION_EXISTE_KO));
			}
			
		} catch (DataIntegrityViolationException ex) {
			logger.error("Error al editar el CampoCalculado", ex);
			throw new BusinessException("Error al editar el OperadorCampoCalculado", ex);
		} catch (Exception ex) {
			logger.error("Error al editar el CampoCalculado", ex);
			throw new BusinessException("Error al editar el OperadorCampoCalculado", ex);
		}
		return parameters;
	}

	/**
	 * Setter del Dao para Spring
	 * @param mtoOperadorCamposCalculadosDao
	 */
	public void setMtoOperadorCamposCalculadosDao(IMtoOperadorCamposCalculadosDao mtoOperadorCamposCalculadosDao) {
		this.mtoOperadorCamposCalculadosDao = mtoOperadorCamposCalculadosDao;
	}
	
	public CamposCalculados getCampoCalculado(Long id) throws BusinessException {
		try {
			return (CamposCalculados) mtoOperadorCamposCalculadosDao.getObject(CamposCalculados.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el campo Calculado", dao);
			throw new BusinessException("Se ha producido al obtener campo Calculado:", dao);
		}
	}
}
