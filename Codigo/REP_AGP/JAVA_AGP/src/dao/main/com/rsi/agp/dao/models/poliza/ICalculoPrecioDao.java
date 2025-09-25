package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ParcelaVO;

@SuppressWarnings("rawtypes")
public interface ICalculoPrecioDao extends GenericDao {
	/**
	 * Método para consultar en la base de datos si el cálculo de precio se hace mediante pl/sql o mediante
	 * código java.
	 * @return
	 */
	public boolean calcularConPlSql();
	
	/**
	 * Método para realizar la llamada al pl/sql de cálculo de precio.
	 * @param lineaseguroid Identificador de plan/línea
	 * @param datosVariablesCapAseg Cadena de texto con los datos variables y sus valores
	 * @param codmodulo Módulo de la póliza
	 * @param codcultivo Cultivo de la parcela
	 * @param codvariedad Variedad de la parcela
	 * @param codprovincia Provincia
	 * @param codcomarca Comarca
	 * @param codtermino Término
	 * @param subtermino Subtérmino
	 * @return Array de dos posiciones con el máximo y el mínimo de precio para los datos de la parcela introducidos
	 * @throws BusinessException
	 */
	public String[] getPrecioPlSql(Long lineaseguroid, List<ItemVO> datosVariablesCapAseg,  
			String codmodulo, String codcultivo, String codvariedad, 
			String codprovincia, String codcomarca, String codtermino, String subtermino) throws BusinessException;
	
	/**
	 * Método para obtener los registros de máscara de precios que afectan al cálculo de precios.
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @return
	 */
	public List getMascaraPrecio(Long lineaseguroid, String codmodulo,ParcelaVO parcela);
	
	/**
	 * Método para obtener la lista de precios para una determinada parcela
	 * @param precioFiltro Filtro necesario para obtener el precio
	 * @return
	 */
	public List<Precio> getPrecio(PrecioFiltro precioFiltro);
}
