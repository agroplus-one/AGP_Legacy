package com.rsi.agp.core.jmesa.service;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.CargasFicherosFilter;
import com.rsi.agp.core.jmesa.sort.CargasFicherosSort;
import com.rsi.agp.dao.tables.cargas.CargasFicheros;

public interface ICargasFicherosService {
	/**
	 * Busca las cargas de Ficheros y genera la tabla para presentarlas
	 * @param request
	 * @param response
	 * @param cargasFicheros
	 * @param origenLlamada
	 * @return
	 */
	public String getTablaCargasFicheros(HttpServletRequest request,
			HttpServletResponse response, CargasFicheros cargasFicheros,
			String origenLlamada);
	/**
	 * 
	 * @param filter
	 * @param cargasFicheros
	 * @return
	 */
	public int getFicherosCountWithFilter(CargasFicherosFilter filter,
			CargasFicheros cargasFicheros);
	/**
	 * 
	 * @param filter
	 * @param sort
	 * @param rowStart
	 * @param rowEnd
	 * @param cargasFicheros
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CargasFicheros> getFicherosWithFilterAndSort(
			CargasFicherosFilter filter, CargasFicherosSort sort, int rowStart,
			int rowEnd, CargasFicheros cargasFicheros) throws BusinessException;
	/**
	 * 
	 * @param file
	 * @param request
	 * @throws BusinessException
	 * @throws Exception
	 */
	public void subeFicherosFTP(MultipartFile file, HttpServletRequest request)
			throws BusinessException, Exception;
	/**
	 * 
	 * @param cargasFicherosBean
	 * @param long1
	 * @return
	 * @throws BusinessException
	 */
	public Long saveFichero(CargasFicheros cargasFicherosBean, Long long1)
			throws BusinessException;
	/**
	 * 
	 * @param cargasFicherosBean
	 * @return
	 */
	public List<String> validaCampos(CargasFicheros cargasFicherosBean);
	/**
	 * 
	 * @param idFichero
	 * @param nombreFichero
	 * @throws BusinessException
	 * @throws Exception
	 */
	public void borrarFichero(Long idFichero, String nombreFichero)
			throws BusinessException, Exception;

}
