package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.facade.TableFacade;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.DatosInformeFilter;
import com.rsi.agp.core.jmesa.sort.DatosInformeSort;
import com.rsi.agp.core.util.OperadorInforme;
import com.rsi.agp.dao.tables.mtoinf.CampoInforme;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfDatosInformes;

public interface IMtoDatosInformeService {

	/**
	 * Devuelve el listado de datos del informe ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de datos del informe
	 * @param sort Ordenación para la búsqueda datos del informe
	 * @param rowStart Primer registro que se datos del informe
	 * @param rowEnd Último registro que se datos del informe
	 * @return
	 * @throws BusinessException
	 */
	
	
	public Collection<VistaMtoinfDatosInformes> getDatosInformeWithFilterAndSort(
			DatosInformeFilter filter,DatosInformeSort sort,BigDecimal informeId,  int rowStart,
			int rowEnd) throws BusinessException; 

	public String getTablaDatosInforme (HttpServletRequest request, HttpServletResponse response,VistaMtoinfDatosInformes vistaMtoinfDatosInformes, String origenLlamada) ;
			
	public TableFacade crearTableFacade (HttpServletRequest request, HttpServletResponse response,VistaMtoinfDatosInformes vistaMtoinfDatosInformes, String origenLlamada) ;

	public int getDatosInformeCountWithFilter(DatosInformeFilter filter, final BigDecimal informeId);
	
	public List<CampoInforme> getListCamposInforme() throws BusinessException; 
	
	public  Map<String, Object>  bajarNivelDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException;
	
	public  Map<String, Object>  bajaDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException;
		
	public Map<String, Object>  subirNivelDatoInformesyActualizar(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException;

	public Map<String, Object> modificarDatoInformes(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException;		
	
	public Map<String, Object> altaCampoInforme(VistaMtoinfDatosInformes vistaMtoinfDatosInformes) throws BusinessException ;

	public List<CampoInforme> getListaCampos(BigDecimal informeId) throws BusinessException;
	
	public Map<String, String> getMapFormatos();
}
