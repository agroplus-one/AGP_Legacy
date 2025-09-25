package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;

public interface IExplotacionAnexoDao extends IGenericoDao { 
	public List getExplotaciones(Long idPoliza, String tipoModificacion, Long idAnexo) throws BusinessException ;
	public List getDatosVariables(BigDecimal idExplotacion, BigDecimal idGrupoRaza) throws BusinessException;
	public List getDatosVariables(BigDecimal idExplotacion) throws BusinessException;
	public List<Long> obtenerIdsExplotacionesAnexoConVariosGruposRaza(final Long idAnexo) throws DAOException;
}