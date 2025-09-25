package com.rsi.agp.dao.models.poliza;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.MascaraFechaContrataAgricola;


/*
 * Interface for <strong>FechaContratacionDao</strong> class
 */
@SuppressWarnings("rawtypes")
public interface IFechaContratacionDao extends GenericDao { 
	
	public boolean validarPorModulo(List<BigDecimal> listCultivos, String codmodulo , 
			       Long lineaseguroid) throws DAOException;
	public boolean validarPorParcela() throws DAOException;
	public boolean validarPorParcelas() throws DAOException;
	public boolean validarPorPolizas() throws DAOException;
	
	public Date getFechaContratacion(List<BigDecimal> listCultivos,
			List<BigDecimal> listVariedad,List<BigDecimal> listProvs,List<BigDecimal> listCmc
			,List<BigDecimal> listTerminos,List<Character> listSubTerminos,String codmodulo ,
			Long lineaseguroid,Object[] cp, List<BigDecimal> listCicloCultivo,
			List<BigDecimal> listSisCultivo, 
			List<BigDecimal> listtipoPlan, List<BigDecimal> listsistProt,
			List<BigDecimal> listTipoCapital,String campo)throws Exception;
	
	public Object[] getDatosComparativas(Long idpoliza) throws Exception;
	
	public List<MascaraFechaContrataAgricola> getConceptosMascaras(List<BigDecimal> listCultivos,
			List<BigDecimal> listVariedad, List<BigDecimal> listProvs,
			List<BigDecimal> listCmc, List<BigDecimal> listTerminos,
			List<Character> listSubTerminos, Long lineaseguroid, String codmodulo) throws Exception;
	
	public Object getFechaContratacionGan(List<Long> listEspecies,
			List<Long> listRegimenes, List<Long> listTipoCapital,
			List<Long> listGrupoRaza, List<Long> listTipoAnimal,
			List<Long> listProvs, List<Long> listCmc,
			List<Long> listTerminos, List<Character> listSubTerminos,
			String codmodulo, Long lineaseguroid,String campo) throws Exception;
}

