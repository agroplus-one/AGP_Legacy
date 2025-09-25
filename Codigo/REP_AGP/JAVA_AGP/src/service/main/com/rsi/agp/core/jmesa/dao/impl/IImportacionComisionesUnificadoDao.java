package com.rsi.agp.core.jmesa.dao.impl;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;

public interface IImportacionComisionesUnificadoDao extends IGenericoDao {
	public Boolean existeFicheroImportado(String nombreFichero) throws DAOException;
	public void saveFicheroUnificado(FicheroUnificado fichero)throws DAOException;
	public void validarFicheroComisiones(Long idFichero, Character tipoFichero) throws DAOException;
	public int getCountWithFilter(CriteriaCommand filter);
	public String getPasswordBuzonInfovia();
}