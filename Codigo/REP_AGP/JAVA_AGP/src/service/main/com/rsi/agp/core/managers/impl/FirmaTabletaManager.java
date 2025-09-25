package com.rsi.agp.core.managers.impl;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper;
import com.rsi.agp.core.webapp.util.AseguradoIrisHelper.AseguradoIrisBean;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.dao.models.param.IParametrizacionDao;
import com.rsi.agp.dao.tables.config.ConfigAgp;

public class FirmaTabletaManager {

	private IParametrizacionDao parametrizacionDao;
	
	public List<AseguradoIrisBean> getAseguradoIris(final String codigoEntidad, final String idExternoPersona,
			final String tipoPersona, final String codUsuario, final String codTerminal) throws BusinessException {
		AseguradoIrisHelper aih = new AseguradoIrisHelper();
		try {
			ConfigAgp configAgp = (ConfigAgp) this.parametrizacionDao.getObject(ConfigAgp.class, "agpNemo", "IRIS_API_KEY");
			String apiKey = configAgp.getAgpValor();
			if (StringUtils.isNullOrEmpty(apiKey)) {
				throw new BusinessException("Sin valor de configuración API-KEY para el SW");
			}
			return aih.getAseguradoIris(codigoEntidad, idExternoPersona, tipoPersona, codUsuario, codTerminal, apiKey);
		} catch (RestWSException e) {
			throw new BusinessException(e.getMessage());
		} catch (JSONException e) {
			throw new BusinessException(e.getMessage());
		}
	}	
	
	public void setParametrizacionDao(IParametrizacionDao parametrizacionDao) {
		this.parametrizacionDao = parametrizacionDao;
	}
}