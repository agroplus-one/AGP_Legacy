/**
 * 
 */
package com.rsi.agp.core.managers;

import java.util.Map;

import com.rsi.agp.core.exception.DAOException;

/**
 * @author U029769
 */
public interface ICambioClaseMasivoManager {

	Map<String, String> cambiaClase(String clase, String listaIds) throws DAOException;
}
