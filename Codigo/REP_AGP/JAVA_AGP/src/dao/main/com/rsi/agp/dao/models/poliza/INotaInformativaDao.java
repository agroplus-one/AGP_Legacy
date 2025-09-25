package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;

public interface INotaInformativaDao {

	public Map<String, Object> getNotaInformativa(BigDecimal codEntidad,
			BigDecimal producto);

	public List<Object> getDatosParaNotaInfoGenerica(Long idPoliza)
			throws DAOException;

}
