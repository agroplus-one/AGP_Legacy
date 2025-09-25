package com.rsi.agp.core.managers;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.dao.tables.poliza.Poliza;

public interface IBaseManager {
	public void cargaCabecera(Poliza polizaBean, HttpServletRequest request) throws Exception;
	public void cargaCabecera(Long idPoliza, HttpServletRequest request) throws Exception;
}
