package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.jmesa.filter.EntidadAccesoRestringidoFilter;
import com.rsi.agp.core.jmesa.sort.EntidadAccesoRestringidoSort;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public interface IMtoEntidadesAccesoRestringidoService {
	
	/**
	 * Devuelve el listado de entidadades con acceso restringido que se ajustan al filtro ordenadas
	 * @param filter
	 * @param sort
	 * @param rowStart
	 * @param rowEnd
	 * @return
	 */
	public Collection<EntidadAccesoRestringido> getEntidadAccesoRestringidoWithFilterAndSort
		(EntidadAccesoRestringidoFilter filter, EntidadAccesoRestringidoSort sort, int rowStart, int rowEnd);
	

	/**
	 * Devuelve el número de entidadades con acceso restringido que se ajustan al filtro
	 * @param filter
	 * @return
	 */
	public int getEntidadAccesoRestringidoCountWithFilter(EntidadAccesoRestringidoFilter filter);

	/**
	 * Devuelve el código html de la tabla que muestra el listado de entidadades con acceso restringido
	 * @param request
	 * @param response
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public String getTablaEntidadesAccesoRestringido(HttpServletRequest request, HttpServletResponse response, EntidadAccesoRestringido entidadAccesoRestringido);

	/**
	 * Devuelve un boolean indicando si la baja de la entidad con acceso restringido ha sido correcta o no
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public boolean bajaEntidadAccesoRestringido (EntidadAccesoRestringido entidadAccesoRestringido);
	
	/**
	 * Devuelve el objeto de entidad con acceso restringido correspondiente al id pasado como parámetro
	 * @param idEntidad
	 * @return
	 */
	public EntidadAccesoRestringido getEntidadAccesoRestringido (Long idEntidad);
	
	/**
	 * Devuelve un boolean indicando si el alta de la entidad con acceso restringido ha sido correcta o no
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public Map<String, Object> altaEntidadAccesoRestringido (EntidadAccesoRestringido entidadAccesoRestringido);
	
	/**
	 * Devuelve un boolean indicando si la modificación de la entidad con acceso restringido ha sido correcta o no
	 * @param entidadAccesoRestringido
	 * @return
	 */
	public Map<String, Object> editaEntidadAccesoRestringido (EntidadAccesoRestringido entidadAccesoRestringido);
	
}
