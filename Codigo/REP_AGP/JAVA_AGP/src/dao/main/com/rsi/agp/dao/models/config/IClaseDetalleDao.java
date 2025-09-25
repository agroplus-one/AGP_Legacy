package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClaseDetalleFilter;
import com.rsi.agp.core.jmesa.sort.ClaseDetalleSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.CicloCultivo;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.cgen.TipoPlantacion;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.Variedad;

@SuppressWarnings("rawtypes")
public interface IClaseDetalleDao extends GenericDao {
	
	/**
	 * Devuelve el listado de clases ordenadas que se ajustan al filtro indicado 
	 * @param filter Filtro para la busqueda de errores
	 * @param sort Ordenacion para la busqueda errores
	 * @param rowStart Primer registro que se mostrara
	 * @param rowEnd ultimo registro que se mostrara
	 * @return
	 * @throws BusinessException
	 */
	public Collection<ClaseDetalle> getClaseDetalleWithFilterAndSort(ClaseDetalleFilter filter, ClaseDetalleSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el numero de clases que se ajustan al filtro pasado como parametro
	 * @param filter
	 * @return
	 */
	public int getConsultaClaseDetalleCountWithFilter(final ClaseDetalleFilter filter);
	
	/**
	 * Devuelve el Cultivo que se ajusta al filtro
	 * @param lineaseguroid
	 * @param codcultivo
	 * @return
	 * @throws BusinessException
	 */
	public Cultivo getCultivo(Long lineaseguroid, BigDecimal codcultivo) throws BusinessException;
	
	/**
	 * Devuelve el Modulo que se ajusta al filtro
	 * @param lineaseguroid
	 * @param codmodulo
	 * @return
	 */
	public Modulo getModulo(Long lineaseguroid, String codmodulo);
	
	/**
	 * Devuelve la Variedad que se ajusta al filtro
	 * @param lineaseguroid
	 * @param codcultivo
	 * @param codvariedad
	 * @return
	 */
	public Variedad getVariedad(Long lineaseguroid, BigDecimal codcultivo,	BigDecimal codvariedad);
	
	/**
	 * Devuelve el objeto CicloCultivo que se ajusta al filtro
	 * @param cicloCultivo
	 * @return
	 */
	public CicloCultivo getCicloCultivo (BigDecimal cicloCultivo);
	
	/**
	 * Devuelve el objeto SistemaCultivo que se ajusta al filtro
	 * @param sistemaCultivo
	 * @return
	 */
	public SistemaCultivo getSistemaCultivo (BigDecimal sistemaCultivo);
	
	/**
	 * Devuelve el objeto tipoCapital que se ajusta al filtro
	 * @param tCapital
	 * @return
	 */
	public TipoCapital getTipoCapital(final BigDecimal tCapital);
	
	/**
	 * Devuelve el objeto tipoPlantacion que se ajusta al filtro
	 * @param tPlantacion
	 * @return
	 */
	public TipoPlantacion getTipoPlantacion(final BigDecimal tPlantacion);
	
	/**
	 * Devuelve el listado de objetos ClaseDetalle que tienen la clase y el modulo indicados en los parametros
	 * @param idClase
	 * @param codmodulo
	 * @return
	 */
	public List<BigDecimal> getClaseDetallePorClaseModulo (final Long lineaseguroid, final BigDecimal idClase, final String codmodulo);
	/**
	 * comprueba si esa el detalle de la clase ya existe en bbdd
	 * @param claseDetalle
	 * @return true ->existe false->no existe
	 */
	public boolean existeClaseDetalle(ClaseDetalle claseDetalle);
	
	/** DAA 06/02/2013  Metodo para recuperar un String con todos los Ids de detalleClase segun el filtro
	 * 
	 * @param consultaFilter
	 * @return
	 */
	public String getlistaIdsTodos(ClaseDetalleFilter consultaFilter);
	
	public BigDecimal getClaseSitAct(final es.agroseguro.contratacion.Poliza sitAct, final Long lineaseguroid) throws DAOException;
}
