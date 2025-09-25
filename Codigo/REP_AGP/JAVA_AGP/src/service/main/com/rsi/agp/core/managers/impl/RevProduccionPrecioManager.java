package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.filters.poliza.RendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.VariedadFiltro;
import com.rsi.agp.dao.models.poliza.ICapitalAseguradoDao;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

public class RevProduccionPrecioManager implements IManager {

	private ICapitalAseguradoDao capitalAseguradoDao;
	private static final Log LOGGER = LogFactory.getLog(RevProduccionPrecioManager.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");

	public final CapitalAsegurado getCapitalAseguradoById(final long idCapitalAsegurado) {
		return (CapitalAsegurado) capitalAseguradoDao.getObject(CapitalAsegurado.class, idCapitalAsegurado);
	}
	
	public final void setCapitalAseguradoDao(final ICapitalAseguradoDao capitalAseguradoDao) {
		this.capitalAseguradoDao = capitalAseguradoDao;
	}
	
	@SuppressWarnings("unchecked")
	public Variedad getVariedad(BigDecimal codCultivo){
		VariedadFiltro variedadFiltro = new VariedadFiltro(codCultivo);
		List<Variedad> variedades= capitalAseguradoDao.getObjects(variedadFiltro);
		if (null != variedades && variedades.size() > 0)
			return variedades.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public LimiteRendimiento getLimitesProduccion (final Modulo modulo, final Variedad variedad, final Termino termino){
		RendimientoFiltro rendimientosFiltro = new RendimientoFiltro(modulo, variedad, termino);
		List<LimiteRendimiento> limitesRendimiento = capitalAseguradoDao.getObjects(rendimientosFiltro);
		if (null != limitesRendimiento && limitesRendimiento.size() > 0 )
			return limitesRendimiento.get(0);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Precio getLimitesPrecio (final Modulo modulo, final Variedad variedad, final Termino termino){
		PrecioFiltro preciosFiltro = new PrecioFiltro(modulo, variedad, termino);
		List<Precio> limitesPrecios = capitalAseguradoDao.getObjects(preciosFiltro);
		if (null != limitesPrecios	&& limitesPrecios.size() > 0 )
			return limitesPrecios.get(0);
		
		return null;
	}
	
	public final void guardarCapitalAsegurado(final CapitalAsegurado capitalAsegurado) {
		try {
			capitalAseguradoDao.saveOrUpdate(capitalAsegurado);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar el capital asegurado",e);
		}
	}

	public PaginatedListImpl<CapitalAsegurado> getPaginatedListCapitalesAsegurados(String idpoliza, int numPageRequest, String sort,String dir) {
		PaginatedListImpl<CapitalAsegurado> paginatedListImpl = null;
		int pageSize = (int)Long.parseLong(bundle.getString("numElementsPag"));
		
		
		try {
			CapitalAseguradoFiltro capitalAseguradoFiltro = new CapitalAseguradoFiltro(new Long(idpoliza));
			int fullListSize = capitalAseguradoDao.getNumObjects(capitalAseguradoFiltro);

	        PageProperties pageProperties = new PageProperties();
			pageProperties.setFullListSize(fullListSize);
			pageProperties.setIndexRowMax((numPageRequest - 1) * pageSize + pageSize - 1);
			pageProperties.setIndexRowMin((numPageRequest - 1) * pageSize);
			pageProperties.setPageNumber(numPageRequest);
			pageProperties.setPageSize(pageSize);
			//DAA 04/12/2012 Ordenacion 
			pageProperties.setSort(sort);
			pageProperties.setDir(dir);
			paginatedListImpl = capitalAseguradoDao.getPaginatedListCapitalesAsegurados(pageProperties, capitalAseguradoFiltro);
			
		} catch(DAOException dao) {
			LOGGER.error("Se ha producido un error al generar el listado de asegurados",dao);
		}
		
		return paginatedListImpl;
	}
 
}
