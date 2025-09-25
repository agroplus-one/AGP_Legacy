package com.rsi.agp.core.managers.impl.ComisionesUnificadas;

import java.io.File;
import java.sql.Blob;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroContenidoUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IImportacionComisionesUnificadoManager {
	public boolean esFicheroYaImportado(String nombreFichero)throws BusinessException;
	public Map<String,Long> procesaFichero(MultipartFile file, Character tipoFichero,Usuario usuario, HttpServletRequest request) throws BusinessException, Exception;
	public Map<String,Long> procesaFichero(File file, String nombreFichero, Long idFichero, Character tipoFichero,Usuario usuario, HttpServletRequest request) throws BusinessException, Exception;

	public FicheroUnificado getFichero(Long idfichero) throws Exception;
	public void borrarFichero(FicheroUnificado fichero,Usuario usuario) throws BusinessException;
	public FicheroContenidoUnificado getFicheroContenido(Long idfichero) throws Exception;
	public void actualizaEstadoFichero(Long idFichero,Character estado)throws BusinessException;
	public Map<String, Long> procesaFichero(Blob blob, String nombreFichero, Long idFichero, Character tipoFichero, Usuario usuario, HttpServletRequest request) throws Exception;
}
