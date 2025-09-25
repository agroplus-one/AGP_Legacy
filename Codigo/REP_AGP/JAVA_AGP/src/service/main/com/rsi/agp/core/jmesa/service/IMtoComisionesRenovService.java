package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.MtoComisionesRenovFilter;
import com.rsi.agp.core.jmesa.sort.MtoComisionesRenovSort;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.ComisionesRenov;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;

import es.agroseguro.contratacion.costePoliza.CosteGrupoNegocio;

/**
 * @author U028975 (Tatiana, T-Systems) Petición: 57624 (Mantenimiento de
 *         Comisioens en Renovables por E-S Mediadora) Fecha:
 *         (Enero/Febrero.2019)
 **/
public interface IMtoComisionesRenovService {

	String getTablaComisionesRenov(HttpServletRequest request, HttpServletResponse response,
			ComisionesRenov ComisionesRenovBean, String origenLlamada, List<GruposNegocio> gruposNegocio);

	Collection<ComisionesRenov> getComisRenovWithFilterAndSort(MtoComisionesRenovFilter filter,
			MtoComisionesRenovSort sort, int rowStart, int rowEnd) throws BusinessException;

	int getComisionesRenovCountWithFilter(MtoComisionesRenovFilter filter) throws BusinessException, Exception;

	Map<String, Object> borraComisionRenov(ComisionesRenov comisionesRenovBean) throws BusinessException;

	List<GruposNegocio> getGruposNegocio();

	Map<String, Object> validaAltaModificacion(ComisionesRenov comisionesRenovBean) throws Exception;

	public ArrayList<Integer> guardaComisRenov(ComisionesRenov comisionesRenovBean, Usuario usuario, int altaModif)
			throws Exception;

	public ComisionesRenov cargarFiltroBusqueda(HttpServletRequest request, ComisionesRenov comisionesRenovBean);

	public Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest,
			BigDecimal lineaDest, String CodUsuario) throws BusinessException;

	public String getDescLinea(BigDecimal codplan, BigDecimal codlinea);

	public String getNombEntidad(BigDecimal codEntidad);

	public Map<Character, ComsPctCalculado> getComisRenovParaCalculo(final BigDecimal codplan,
			final BigDecimal codlinea, final String codModulo, final Long idComparativa, final BigDecimal codEntidad,
			final BigDecimal codEntMed, final BigDecimal codSubEntMed, final CosteGrupoNegocio[] cgnArr)
			throws BusinessException;

	public List<ComisionesRenov> getComisionesRenovList(Limit limit);
}
