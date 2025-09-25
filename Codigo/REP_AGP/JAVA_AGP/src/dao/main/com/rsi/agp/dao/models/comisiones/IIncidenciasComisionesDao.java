package com.rsi.agp.dao.models.comisiones;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;


public interface IIncidenciasComisionesDao extends GenericDao{

	public List<FicheroIncidencia> getListFicherosIncidencias(FicheroIncidencia ficheroIncidenciaBean) throws DAOException;
	
	public List<FicheroMultIncidencias> getListFicherosMultIncidencias(FicheroMultIncidencias ficheroMultIncidenciaBean) throws DAOException;

	public SubentidadMediadora getSubEntByColectivo(String idcolectivo,String entmediadora,String subentmediadora) throws DAOException;

	public void deleteFichero(Long idFichero) throws DAOException;
	
	public void deleteFicheroMult(Long idFichero) throws DAOException;
}	

