package com.rsi.agp.dao.models.poliza.ganado;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface ICargaExplotacionesDao extends GenericDao {
	
	List<BigDecimal> getIdsPolizas(int tipoFiltro, Long idAsegurado, List<BigDecimal> listCodplan, BigDecimal codlinea,
			Long idpoliza) throws DAOException;

	public void actualizarIdCargaExplotaciones(Long idpoliza, Integer idCargaExplotaciones) throws Exception;

	public List<com.rsi.agp.dao.tables.poliza.Poliza> getPolizas(int tipoFiltro, Long idAsegurado,
			List<BigDecimal> listCodplan, BigDecimal codlinea, Long idPoliza) throws DAOException;

	public short getFilaExplotacionCobertura(Long lineaSeguroId, String modulo, int conceptoPrincioalModulo,
			int riesgoCubierto) throws DAOException;

	public String getDescripcionConceptoPpalMod(int conceptoPpalMod) throws DAOException;

	public String getDescripcionRiesgoCubierto(Long lineaSeguroId, String modulo, int riesgoCubierto)
			throws DAOException;

	public Object[] getMediadoraColectivoGanadoweb(Integer refColectivo) throws DAOException;
}
