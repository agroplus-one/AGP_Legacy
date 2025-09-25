package com.rsi.agp.core.manager.impl.anexoRC;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.List;
import java.util.Map;

import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion.SolicitudReduccionCapResponse;
import com.rsi.agp.dao.tables.cgen.Riesgo;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public interface IAnexoReduccionCapitalManager {

	public List<Riesgo> obtenerAyudaCausaRC(String realPath) throws Exception;

	public List<Object[]> obtenerAyudaCausaDeclaracionRC(String realPath) throws Exception;

	public PolizaActualizadaRCResponse consultarContratacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws Exception;

	public SolicitudReduccionCapResponse solicitudModificacionRC(final String referencia, final BigDecimal plan,
			final String realPath) throws Exception;

	public AcuseRecibo envioModificacionRC(final String idCupon, final boolean revAdministrativa, final Clob xmlPpal,
			final Clob xmlCpl, final String realPath) throws Exception;

	public String anulacionCuponRC(final String idCupon, final String realPath) throws Exception;

	public AcuseRecibo validacionModificacionRC(String xmlpoliza, final String idCupon, final String realPath) throws Exception;

	public Map<String, Object> calculoModificacionCuponActivoRC(final String realPath, final String cupon,
			final Base64Binary xml) throws Exception;
}
