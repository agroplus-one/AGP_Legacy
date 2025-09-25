package com.rsi.agp.core.managers;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface ICargaComisionesManager {
	
	public static final int FICHERO_NOT_FOUND = -1;
	public static final int FICHERO_CARGADO = 0;
	public static final int ERROR = -999;
	
	public int cargaFichero(final HttpServletRequest request, final Usuario usuario) throws BusinessException;
	public String obtenerNombreFichero() throws BusinessException;
	public File obtenerFichero(final String nombreFichero, final String directorio);
}