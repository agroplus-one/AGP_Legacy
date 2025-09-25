package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModSWCambioMasivo;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Parcela;

@SuppressWarnings("rawtypes")
public interface IAnexoModCambioMasivoSWDao extends GenericDao {

	
	void updateParcelaAnexo(Parcela parcelaAnexo, BigDecimal codcultivo,BigDecimal codVariedad)throws  DAOException;

	void updateSuperficie(BigDecimal superficie, Parcela parcelaAnexo)throws DAOException;
	
	void updateProduccion(BigDecimal increHa,BigDecimal increParcela,Parcela parcelaAnexo) 
			throws DAOException;

	void updateDatosVariables(String valor, Parcela parcelaAnexo, int codConcepto) throws DAOException;
	
	String getAplRdto (CapitalAsegurado ca, String codModulo);

	void cambiaEstadoParcela(List<Long> listParcelas) throws DAOException;

	void setEdad(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo, boolean b)throws DAOException;

	HashMap<String, String> updateUbicacion(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo,HashMap<String, String> mensajesError) throws DAOException;

	void updateSigpac(AnexoModSWCambioMasivo amCm, Parcela parcelaAnexo)throws DAOException;

	void updatePrecio(BigDecimal precio_cm, Parcela parcelaAnexo)throws DAOException;

	void updateIncUnidades(String unidades_cm, BigDecimal inc_unidades_cm,
			Parcela parcelaAnexo) throws DAOException;



	
	
}
