package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;

public interface IDatosExplotacionesManager {

	public Map<String, Object> alta(Explotacion explotacion);

	public List<PrecioAnimalesModulo> calcularPrecio(Explotacion e) throws PrecioGanadoException;

	public Explotacion getExplotacion(Long id);

	public Explotacion getExplotacion(Long idPoliza, Integer numero);

	public List<ConfiguracionCampo> cargarDatosVariables(Long lineaseguroid);

	public Poliza getPoliza(Long idPoliza);

	public void borrarListaDatosVariables(Explotacion idExplotacion);

	void borrarGrupoRaza(Explotacion ex, String idgruporaza);

	void borrarListaDatosVariables(Explotacion ex, String idGrupoRaza);

	public void getListaCodigosLupasExplotaciones(Long idClase, Map<String, Object> parametros);

	public Termino obtenerTermino(BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino,
			Character subtermino);

	public Map<String, Object> validacionesPreviasDatosIdentificativos(Explotacion explotacion);

	public boolean isCoberturasElegiblesNivelExplotacion(Long lineaseguroid, Set<ModuloPoliza> modsPoliza);

	public List<ExplotacionCobertura> getCoberturasElegiblesExplotacion(Explotacion exp, String realPath,
			String codUsuario, String cobExistente);

	public Set<ExplotacionCobertura> procesarCoberturas(Explotacion exp, String coberturas);

	public Set<ExplotacionCobertura> procesarCoberturas(Explotacion exp, String coberturas, String realPath,
			String codUsuario);

	public String getTipoCapitalConGrupoNegocio(Boolean dependenNumAnimales);

	public Linea getLinea(Long lineaSeguroId);
	
	public InformacionRega getInfoRega(String codigoRega, String plan, String linea) throws RestWSException;
}