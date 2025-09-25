package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;

public interface IUtilidadesXMLDAO {
	
	/**
	 * Obtiene el fichero xml de calculo de una poliza enviado a Agroseguro para una fila dada
	 * @param idPoliza Identificador de la poliza
	 * @param filaComparativa Identifica la fila a comparar
	 * @throws DAOException
	 * @return Cadena de texto con el xml del envio a Agroseguro
	 */
	public String getXMLCalculo(String idPoliza, String filaComparativa) throws DAOException;
	
	/**
	 * Obtiene el fichero xml de validacion de una poliza enviado a Agroseguro para una fila dada
	 * @param idPoliza Identificador de la poliza
	 * @param filaComparativa Identifica la fila a comparar
	 * @throws DAOException
	 * @return Cadena de texto con el xml del envio a Agroseguro
	 */
	public String getXMLValidacion(String idPoliza, String filaComparativa) throws DAOException;

}
