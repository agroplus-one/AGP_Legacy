/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  Interface para PantallasConfigurablesDao
*
 **************************************************************************************************
*/
package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.Pantalla;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public interface IPantallasConfigurablesDao  extends GenericDao {
	public List getPantallas() throws BusinessException;
	public List getPantallasConfigurables() throws BusinessException;
	public List getPantallasConfigurablesConsulta(PantallaConfigurable pantallaConfigurable) throws BusinessException;
	public List getPantallasConfigurables(Long idLinea) throws BusinessException;
	public Pantalla getPantalla(Long idPantalla) throws BusinessException;
	public boolean existePantalla(Long lineaseguroid, Long idpantalla, Long idpantallaconfigurable);
	public String obtieneGrupoSeguro(BigDecimal codLinea);
	
}


