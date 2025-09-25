package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IDatoAseguradoDao extends GenericDao {
	
	public boolean existeDatoAsegurado (Long idAsegurado, BigDecimal codLinea);
	public boolean duplicaLinea (Long idasegurado,Long iddatoasegurado, BigDecimal codLinea);
	public BigDecimal getCodLinea(Long idDatoAsegurado);
}
