package com.rsi.agp.dao.models.poliza.ganado;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCobExplotacionAnexo;

@SuppressWarnings("rawtypes")
public interface IDatosExplotacionAnexoDao extends GenericDao {
	
	public List findFiltered(Class clase, String[] parametros, Object[] valores, String orden) throws DAOException;

	public Integer calcularNuevoNumeroExplotacion(Long idAnexoExplotacion) throws DAOException;

	public void deleteCoberturasById(final Long idExplotacionAnexo) throws DAOException;

	public SWModulosCobExplotacionAnexo saveEnvioCobExplotacion(SWModulosCobExplotacionAnexo envio) throws DAOException;

	public void deleteCoberturasByIdsCob(Long id, List<String> lstidsBBDD) throws DAOException;
}