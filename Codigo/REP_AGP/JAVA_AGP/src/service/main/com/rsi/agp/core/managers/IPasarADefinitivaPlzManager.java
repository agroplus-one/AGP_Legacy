package com.rsi.agp.core.managers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public interface IPasarADefinitivaPlzManager {
	
	public Map<String, Object> doPasarADefinitiva (Long idPoliza, Map<String, Object> parametros, HttpServletRequest request);
	public Map<String, Object> doPasarADefinitivaMultiple (Map<String, Object> parametros, HttpServletRequest request);
	public boolean muestraBotonPasoDef(Long idPoliza, AcuseRecibo acuseRecibo,	Usuario usuario);	
	public void generarXMLDefinitivo(Poliza p, Map<String, Object> errores);

	
}
