package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.models.config.IConfiguracionCamposDao;
import com.rsi.agp.dao.models.config.IPantallasConfigurablesDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ConfiguracionCamposManager implements IManager {
	
	private static final Log             logger = LogFactory.getLog(ConfiguracionCamposManager.class);
	
	private IConfiguracionCamposDao configuracionCamposDao;
	private IPantallasConfigurablesDao pantallasConfigurablesDao;
	

	/** DAA 04/09/2013
	 * 
	 * @param lineaseguroid
	 * @param lstCodConceptos 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConfiguracionCampo> getListaDatosVariablesCargaParcelasMarcados(Long lineaseguroid, List<BigDecimal> lstCodConceptos) throws BusinessException{ 
		logger.debug("init - getListaDatosVariablesCargaParcelasMarcados");
		List<ConfiguracionCampo> lstDatosVarMarcados= new ArrayList<ConfiguracionCampo>();
		
		try {
			LineasFiltro filtro=new LineasFiltro();
			filtro.setLineaSeguroId(lineaseguroid);
			List<Linea> lista=null;
			lista=(List<Linea>)configuracionCamposDao.getObjects(filtro);
			Linea linea=lista.get(0);
			String grupoSeguro=pantallasConfigurablesDao.obtieneGrupoSeguro(linea.getCodlinea());
			lstDatosVarMarcados = (List<ConfiguracionCampo>) configuracionCamposDao.getDatosVariablesCargaParcelasMarcados(lineaseguroid, lstCodConceptos,grupoSeguro);
			
		}catch (DAOException ex){
			logger.error("Se ha producido un error durante el acceso a la base de datos", ex);
			throw new BusinessException ("[ERROR] al obtener los datos variables de la tabla DatoVariableDefault - en CargaPACManager, metodo mostrarDatosVariablesPac]",ex);
		}
		logger.debug("Fin - getListaDatosVariablesCargaParcelasMarcados");
		return lstDatosVarMarcados;
	}

	public void setConfiguracionCamposDao(IConfiguracionCamposDao configuracionCamposDao) {
		this.configuracionCamposDao = configuracionCamposDao;
	}
	public void setPantallasConfigurablesDao(
			IPantallasConfigurablesDao pantallasConfigurablesDao) {
		this.pantallasConfigurablesDao = pantallasConfigurablesDao;
	}
}
