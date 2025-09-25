package com.rsi.agp.dao.models.poliza;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

public interface IValidaLupasCambioMasivoDao extends GenericDao {

	boolean validaCultivoVariedadCM(String cultivo, String variedad,
			String lineaSeguroId) throws DAOException;

	boolean validaDestinoCM(String destino) throws DAOException;

	boolean validaSisCultivoCM(String sisCultivo) throws DAOException;

	boolean validaMarcoPlanCM(String tipoMarcoPlan) throws DAOException;

	boolean validaPracticaCulturalCM(String practicaCultural)
			throws DAOException;

	boolean validaTipoPlantacionCM(String tipoPlantacion) throws DAOException;

	boolean validaUbicacionCM(String provincia, String comarca, String termino,
			String subtermino) throws DAOException;

	boolean validaSistemaProduccion(String sistProd) throws DAOException;
	
}
