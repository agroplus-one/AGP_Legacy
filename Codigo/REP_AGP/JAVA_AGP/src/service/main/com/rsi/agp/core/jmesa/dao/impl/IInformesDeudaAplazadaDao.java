package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;

import com.rsi.agp.core.jmesa.dao.IGenericoDao;

public interface IInformesDeudaAplazadaDao extends IGenericoDao {

	String getNombreLinea(BigDecimal bigDecimal);

	String getNombreEntidad(BigDecimal bigDecimal);
}
