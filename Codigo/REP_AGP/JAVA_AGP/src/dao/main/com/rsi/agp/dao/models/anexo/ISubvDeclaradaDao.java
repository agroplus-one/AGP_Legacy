package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;

public interface ISubvDeclaradaDao extends GenericDao{
	
	public static final BigDecimal JOVEN_AGRICULTOR_HOMBRE = new BigDecimal(10);
	public static final BigDecimal JOVEN_AGRICULTOR_MUJER = new BigDecimal(11);
	public static final BigDecimal AGRICULTOR_PROFESIONAL = new BigDecimal(20);
	
	public List<SubvDeclarada> getAll(Long idAnexo) throws DAOException;

	public void bajaSubvIncompatible(AnexoModificacion anexo, BigDecimal codSubv) throws DAOException;

}
