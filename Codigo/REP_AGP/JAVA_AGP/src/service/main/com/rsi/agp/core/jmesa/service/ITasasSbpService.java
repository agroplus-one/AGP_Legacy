package com.rsi.agp.core.jmesa.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.TasasSbpFilter;
import com.rsi.agp.core.jmesa.sort.TasasSbpSort;
import com.rsi.agp.dao.tables.sbp.TasasSbp;

public interface ITasasSbpService {
		
	/**
	 * Devuelve el listado de tasas ordenados que se ajustan al filtro indicado 
	 * @param filter Filtro para la búsqueda de las tasas
	 * @param sort Ordenación para la búsqueda de los tasas
	 * @param rowStart Primer registro que se mostrará
	 * @param rowEnd Último registro que se mostrará
	 * @return
	 * @throws BusinessException
	 */
	public Collection<TasasSbp> getTasasSbpWithFilterAndSort(TasasSbpFilter filter, TasasSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	/**
	 * Devuelve el número de tasas que se ajustan al filtro pasado por parámetro
	 * @param filter
	 * @return
	 */
	public int getTasasSbpCountWithFilter(TasasSbpFilter filter);

	/**
	 * Genera la tabla para mostrar el listado de tasas que se ajustan al filtro de búsqueda
	 * @param request
	 * @param response
	 * @param tasasSbp
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaTasasSbp (HttpServletRequest request, HttpServletResponse response, TasasSbp tasasSbp, String origenLlamada);
	
	/**
	 * Borra el objeto tasa pasado como parámetro
	 * @param tasaSbp
	 * @return Boolean que indica si el borrado ha sido correcto
	 */
	public boolean bajaTasaSbp (TasasSbp tasaSbp);
	
	/**
	 * Obtiene el objeto tasa correspondiente al id indicado en parámetro
	 * @param id
	 * @return
	 */
	public TasasSbp getTasaSbp (Long id);
	
	/**
	 * Realiza el alta de la tasa pasada como parámetro
	 * @param tasaSbp
	 * @return Devuelve un mapa con los errores producidos en el proceso de alta
	 */
	public Map<String, String> altaTasaSbp (TasasSbp tasaSbp);
	
	/**
	 * Realiza la modificación de la tasa pasada como parámetro
	 * @param tasaSbp
	 * @return Devuelve un mapa con los errores producidos en el proceso de modificación
	 */
	public Map<String, String> updateTasaSbp (TasasSbp tasaSbp);

	Map<String, Object> replicar(BigDecimal planOrig, BigDecimal lineaOrig,	BigDecimal planDest, BigDecimal lineaDest) throws BusinessException;

	public void subeFicheroTasas(MultipartFile multipartFile, HttpServletRequest request) throws BusinessException, IOException ;
}
