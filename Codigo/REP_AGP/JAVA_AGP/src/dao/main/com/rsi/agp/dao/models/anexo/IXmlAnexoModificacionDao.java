package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;

public interface IXmlAnexoModificacionDao extends GenericDao{
	
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgo(AnexoModificacion am) throws BusinessException;
	public Map<BigDecimal, List<String>> getDatosVariablesParcela(AnexoModificacion am) throws BusinessException;
	public void saveXmlAnexoModificacion(Long idAnexo, final String xml) throws DAOException;
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoAnexoCPL(Long idAnexo, String moduloPPal) throws BusinessException;
	String getDatVarParcelaModifRiesgoJavaImpl(Long idAnx) throws BusinessException;

}
