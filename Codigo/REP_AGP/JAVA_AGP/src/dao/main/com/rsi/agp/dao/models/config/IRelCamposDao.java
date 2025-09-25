/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Interface para RelCamposDao
*
 **************************************************************************************************
*/
package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.RelacionCampo;

public interface IRelCamposDao extends GenericDao {
	public List getPlanes() throws BusinessException;
	public List getRelacionesCampos() throws BusinessException;
	public List getLineas(BigDecimal codPlan) throws BusinessException;
	public Object getGruposFactores(BigDecimal camposc) throws BusinessException;
	public Object getFactoresPorGrupo(BigDecimal factor)throws BusinessException;
	public List getRelacionCamposConsulta(RelacionCampo rc) throws BusinessException;
	public List getCampoSC(Long linea, BigDecimal uso, BigDecimal ubicacion) throws BusinessException;	
}
