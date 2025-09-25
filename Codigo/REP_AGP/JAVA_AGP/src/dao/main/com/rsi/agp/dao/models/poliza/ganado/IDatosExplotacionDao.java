package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCoberturasExplotacion;

@SuppressWarnings("rawtypes")
public interface IDatosExplotacionDao extends GenericDao {
	
	public List findFiltered(Class clase, String[] parametros, Object[] valores, String orden) throws DAOException;

	public boolean isCoberturasElegiblesNivelExplotacion(Long lineaseguroId, String codModulos);

	public void evictEnvio(SWModulosCoberturasExplotacion newEnvio);

	public SWModulosCoberturasExplotacion saveEnvioCobExplotacion(SWModulosCoberturasExplotacion doc)
			throws DAOException;

	public void deleteCoberturasById(final Long idExplotacion) throws DAOException;

	public List<Object> getTipoCapitalConGrupoNegocio(Boolean dependenNumAnimales) throws DAOException;

	public void deleteCoberturasByIdsCob(Long id, List<String> lstidsBBDD) throws DAOException;

	/* Pet. 57622-REQ.01 * INICIO */
	public String getCobExplotacion(final String codModulo, final Long idExpl) throws DAOException;

	public String obtenerExploDescvalorElegido(BigDecimal codConcepto, BigDecimal fila, Long idPoliza,
			Integer numeroExpl) throws DAOException;

	public Boolean obtenerExploElegida(BigDecimal codConcepto, BigDecimal fila, Long idPoliza, Integer numeroExpl)
			throws DAOException;
}