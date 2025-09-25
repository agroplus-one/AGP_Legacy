package com.rsi.agp.core.jmesa.service;

import java.text.ParseException;

import org.jmesa.limit.Limit;

import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;

public interface IFicheroUnificadoService extends IGetTablaService {
	public FicheroUnificado getBeanFromLimit(Limit consulta_LIMIT) throws ParseException;
}
