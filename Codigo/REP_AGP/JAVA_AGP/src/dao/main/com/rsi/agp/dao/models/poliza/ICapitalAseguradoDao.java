package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatosTotalesCapital;
import com.rsi.agp.dao.tables.poliza.TipoCapitalComparativa;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings("rawtypes")
public interface ICapitalAseguradoDao extends GenericDao {
	
	public List<CapAsegRelModulo> listCapAsegRelModuloByIdCapAseg(Long idCapitalAsegurado) throws DAOException;
	public void deleteCapAsegRelModById(Long id) throws DAOException;
	public PaginatedListImpl<CapitalAsegurado> getPaginatedListCapitalesAsegurados(PageProperties pageProperties,CapitalAseguradoFiltro capitalAseguradoFiltro) throws DAOException;
	public List getTiposCapitalbyPoliza(Long idPoliza,BigDecimal hoja)throws DAOException ;
	public List<Object[]> getTiposCapitalParcelasComparativa(Long idPoliza)throws DAOException ;
	public List<Object[]> getTiposCapitalExplotacionesComparativa(Long idPoliza)throws DAOException ;
	public DatosTotalesCapital getTotalPlantones(Long idPoliza, BigDecimal tipoCapital,String descripcion,BigDecimal hoja,BigDecimal codConcepto) throws DAOException;
	public DatosTotalesCapital getTotalProduccion(Long idPoliza, BigDecimal tipoCapital, String descripcion, BigDecimal hoja)throws DAOException;
	public List<Object[]> getTiposCapitalModulosComparativa(Long idPoliza, BigDecimal tipoCapital)throws DAOException;
	public List<Object[]> getTiposCapitalGrupoNegocioComparativa(Long idPoliza, String tipoCapital)throws DAOException;
	public List<CapitalAsegurado> getListCapitalesAsegurados(Long idPoliza) throws DAOException;
	public void actualizarTipoRdto(Long idCapitalAsegurado, Long valor, Long prod) throws DAOException;
	public void actualizarTipoRdtoAnexo(Long idCapitalAsegurado, Long valor,  Long prod, Long idparcelaAnexo) throws DAOException;
}
