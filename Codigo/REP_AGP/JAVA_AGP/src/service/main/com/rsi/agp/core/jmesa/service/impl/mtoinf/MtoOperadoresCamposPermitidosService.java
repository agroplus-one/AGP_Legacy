package com.rsi.agp.core.jmesa.service.impl.mtoinf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.service.mtoinf.IMtoOperadoresCamposPermitidosService;
import com.rsi.agp.core.util.ConstantsInf;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.mtoinf.IMtoOperadorCamposPermitidosDao;
import com.rsi.agp.dao.tables.mtoinf.CamposPermitidos;
import com.rsi.agp.dao.tables.mtoinf.OperadorCamposPermitido;
import com.rsi.agp.dao.tables.mtoinf.VistaCampo;

public class MtoOperadoresCamposPermitidosService implements IMtoOperadoresCamposPermitidosService {
	
	private IMtoOperadorCamposPermitidosDao mtoOperadorCamposPermitidosDao;
	private Log logger = LogFactory.getLog(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("agp_inf");
	
	@Override
	public Map<String, Object> altaOperadorCamposPermitidos(OperadorCamposPermitido operadorCamposPermitidos, String idVistaCampo) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		VistaCampo visC = new VistaCampo();
		String idOpPerEncontrado = "";
		try {
			
			idOpPerEncontrado = mtoOperadorCamposPermitidosDao.checkCampo_OperadorExists(idVistaCampo, operadorCamposPermitidos.getIdoperador(),null);
			if (!StringUtils.nullToString(idOpPerEncontrado).equals("")) {
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_ALTA_EXISTE_KO));
			}else{
				visC = this.getVistaCampo(new BigDecimal(idVistaCampo));
				operadorCamposPermitidos.setVistaCampo(visC);
				mtoOperadorCamposPermitidosDao.saveOrUpdate(operadorCamposPermitidos);
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_ALTA_OK));
			}					
		} catch (Exception ex) {
			throw new BusinessException("MtoCamposPermitidosService - altaCampoPermitido - Error al dar de alta el Operador campo permitido", ex);
		}
		
		return parameters;
	}
	
	/**
	 * realiza el alta de operadores por defecto para el idVistaCampo paado como parámetro
	 * @param idVistaCampo
	 * @param tipo
	 */
	public Map<String, Object> altaOperadoresPorDefectoByCamPer(BigDecimal idVistaCampo,BigDecimal tipo) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (tipo != null && tipo.compareTo(new BigDecimal(Integer.toString(ConstantsInf.CAMPO_TIPO_TEXTO))) == 0){
				altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_CAD_CONTIENEN));
				altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_CAD_EMPIEZAN_POR));
				altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_CAD_TERMINAN_POR));
			}
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_CONTENIDO_EN));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_ENTRE));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_IGUAL));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MAYOR_IGUAL_QUE));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MAYOR_QUE));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MENOR_IGUAL_QUE));
			altaOpPorDefecto(idVistaCampo,new BigDecimal(ConstantsInf.COD_OPERADOR_BD_MENOR_QUE));
								
		} catch (Exception ex) {
			throw new BusinessException("MtoCamposPermitidosService - altaOperadoresPorDefectoByCamPer - Error al dar de alta el Operador campo permitido", ex);
		}
		return parameters;
	}
	
	/**
	 * alta de un operador
	 * @param idVistaCampo
	 * @param idOperador
	 */
	public void altaOpPorDefecto(BigDecimal idVistaCampo,BigDecimal idOperador){
		OperadorCamposPermitido opCamPer = new OperadorCamposPermitido();
		VistaCampo visC = new VistaCampo();
		try {
			String idOpPerEncontrado = mtoOperadorCamposPermitidosDao.checkCampo_OperadorExists(idVistaCampo.toString(),idOperador,null);
			if (StringUtils.nullToString(idOpPerEncontrado).equals("")) {
				visC = this.getVistaCampo(idVistaCampo);
				opCamPer.setVistaCampo(visC);
				opCamPer.setIdoperador(idOperador);
				mtoOperadorCamposPermitidosDao.saveOrUpdate(opCamPer);
			}				
		} catch (Exception ex) {
			logger.error("MtoOperadoresCamposPermitidosService - método altaOpPorDefecto. Error al dar de alta un operador por defecto: ", ex);
		}
	}
	
	@Override
	public Map<String, Object> bajaOperadorCamposPermitidos(OperadorCamposPermitido operadorCamposPermitidos) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			boolean existeCondicionCamPerm = false;
			
			// reviso si hay condicion para ese campo permitido
			existeCondicionCamPerm = mtoOperadorCamposPermitidosDao.existeCondicionCamPerm(operadorCamposPermitidos.getId().toString());
			if (!existeCondicionCamPerm){
				mtoOperadorCamposPermitidosDao.delete(operadorCamposPermitidos);
				logger.debug("operadorCamposPermitido borrado = " + operadorCamposPermitidos.getId());
				parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_BAJA_OK));
			}else{ // aviso de que ya hay una condición para ese campo permitido
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_CONDICION_EXISTE_KO));
			}
		} catch (Exception ex) {
			logger.error("Error al eliminar el operadorCamposPermitido", ex);
			throw new BusinessException("Error al eliminar el operadorCamposPermitido", ex);
		}
		return parameters;
	}

	@Override
	public OperadorCamposPermitido getOperadorCamposPermitidos(Long id) throws BusinessException {
		try {
			return (OperadorCamposPermitido) mtoOperadorCamposPermitidosDao.getObject(OperadorCamposPermitido.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el OperadorCamposPermitido", dao);
			throw new BusinessException("Se ha producido al obtener el OperadorCamposPermitido:", dao);
		}
	}
	
	public CamposPermitidos getCampoPermitido(Long id) throws BusinessException {
		try {
			return (CamposPermitidos) mtoOperadorCamposPermitidosDao.getObject(CamposPermitidos.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener el CamposPermitido", dao);
			throw new BusinessException("Se ha producido al obtener el CamposPermitido:", dao);
		}
	}
	
	public VistaCampo getVistaCampo(BigDecimal id) throws BusinessException {
		try {
			return (VistaCampo) mtoOperadorCamposPermitidosDao.getObject(VistaCampo.class, id);
		} catch (Exception dao) {
			logger.error("Se ha producido al obtener la VistaCampo", dao);
			throw new BusinessException("Se ha producido al obtener la VistaCampo:", dao);
		}
	}

	@Override
	public Map<String, Object> updateOperadorCamposPermitidos(String idOpCampoPermitido, String idVistaCampo, String idOperador) throws BusinessException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		OperadorCamposPermitido opCamPerm = new OperadorCamposPermitido();
		VistaCampo visC = new VistaCampo();
		boolean existeCondicionCamPerm = false;
		try {
			// cargo el objeto desde la bbdd
			opCamPerm = this.getOperadorCamposPermitidos(Long.valueOf(idOpCampoPermitido));
			visC = this.getVistaCampo(new BigDecimal(idVistaCampo));
			
			// reviso si hay condicion para ese campo permitido
			String idOpPerEncontrado = "";
			existeCondicionCamPerm = mtoOperadorCamposPermitidosDao.existeCondicionCamPerm(idOpCampoPermitido);
			if (!existeCondicionCamPerm){
				idOpPerEncontrado = mtoOperadorCamposPermitidosDao.checkCampo_OperadorExists(idVistaCampo, new BigDecimal(idOperador),idOpCampoPermitido);
				if (!StringUtils.nullToString(idOpPerEncontrado).equals("")) {
					parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_ALTA_EXISTE_KO));
				}else{
					opCamPerm.setVistaCampo(visC);
					opCamPerm.setIdoperador(new BigDecimal(idOperador));
					
					mtoOperadorCamposPermitidosDao.saveOrUpdate(opCamPerm);
					logger.debug("OperadorCampoPermitido editado con id= "+opCamPerm.getId());
					parameters.put("mensaje", bundle.getObject(ConstantsInf.MSG_OPERADOR_MODIF_OK));
				}
			}else{ // aviso de que ya hay una condición para ese campo permitido
				parameters.put("alerta", bundle.getObject(ConstantsInf.ALERTA_OPERADOR_CONDICION_EXISTE_KO));
			}
			
		} catch (DataIntegrityViolationException ex) {
			logger.error("Error al editar el CampoPermitido", ex);
			throw new BusinessException("Error al editar el OperadorCampoPermitido", ex);
		} catch (Exception ex) {
			logger.error("Error al editar el CampoPermitido", ex);
			throw new BusinessException("Error al editar el OperadorCampoPermitido", ex);
		}
		return parameters;
	}

	/**
	 * Setter de Dao para Spring
	 * @param mtoOperadorCamposPermitidosDao
	 */
	public void setMtoOperadorCamposPermitidosDao(IMtoOperadorCamposPermitidosDao mtoOperadorCamposPermitidosDao) {
		this.mtoOperadorCamposPermitidosDao = mtoOperadorCamposPermitidosDao;
	}


	
	
}
