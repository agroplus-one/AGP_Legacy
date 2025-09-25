/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  16/07/2010  Ernesto Laura     Interfaz del Dao para consultas de Modulos 
* 											
*
**************************************************************************************************/
package com.rsi.agp.dao.models.cpl;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.Modulo;

public interface IModulosDao extends GenericDao{
	
	/**
	 * Método para obtener los módulos seleccionados en una póliza
	 * @param idPoliza Identificador de la póliza
	 * @param lineaseguroid Identificador de plan/línea
	 * @return Lista de módulos
	 * @throws BusinessException
	 */
	public List<Modulo> getModulosPoliza(Long idPoliza, Long lineaseguroid) throws BusinessException;
	public List<Modulo> getModuloAnexo(Long idAnexo, Long idPoliza, Long lineaseguroid) throws BusinessException;
	public Modulo getModulo(Long lineaseguroid, String codmodulo);
}
