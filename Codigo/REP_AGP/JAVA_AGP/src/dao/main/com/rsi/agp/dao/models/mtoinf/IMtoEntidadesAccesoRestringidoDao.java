package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.jmesa.filter.EntidadAccesoRestringidoFilter;
import com.rsi.agp.core.jmesa.sort.EntidadAccesoRestringidoSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.EntidadAccesoRestringido;

public interface IMtoEntidadesAccesoRestringidoDao extends GenericDao{
	
	/**
	 * Devuelve el número de entidades con acceso restringido que se ajustan al filtro de búsqueda
	 * @param filter
	 * @return
	 */
	public int getEntidadAccesoRestringidoCountWithFilter(final EntidadAccesoRestringidoFilter filter);

	/**
	 * Devuelve el listado de entidades con acceso restringido que se ajustan al filtro de búsqueda
	 * @param filter
	 * @param sort
	 * @param rowStart
	 * @param rowEnd
	 * @return
	 */
	public Collection<EntidadAccesoRestringido> getEntidadAccesoRestringidoWithFilterAndSort
		(final EntidadAccesoRestringidoFilter filter, final EntidadAccesoRestringidoSort sort, final int rowStart, final int rowEnd);
	
	/**
	 * Devuelve un boolean indicando si existe una entidad con acceso restringido con los datos indicados como parámetro
	 * @param codentidad
	 * @param id
	 * @return
	 */
	public boolean checkEntidadAccesoRestringido (BigDecimal codentidad, Long id);
}
