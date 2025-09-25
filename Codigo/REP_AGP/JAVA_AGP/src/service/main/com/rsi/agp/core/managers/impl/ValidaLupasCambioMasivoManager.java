package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IValidaLupasCambioMasivoManager;
import com.rsi.agp.dao.models.poliza.IValidaLupasCambioMasivoDao;

public class ValidaLupasCambioMasivoManager implements IValidaLupasCambioMasivoManager{
	
	private static final Log logger = LogFactory.getLog(ValidaLupasCambioMasivoManager.class);
	private IValidaLupasCambioMasivoDao validaLupasCambioMasivoDao;
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	@Override
	public void validaCultivoVariedad(String cultivo, String variedad,
			String lineaSeguroId,ArrayList<String> erroresLupas) throws Exception{
		
		try{
			if (!validaLupasCambioMasivoDao.validaCultivoVariedadCM (cultivo,variedad,lineaSeguroId)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaCultivoVariedad.ko")+ "<br>");
			}
			
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaCultivoVariedad",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaCultivoVariedad",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}
		
	}
	
	@Override
	public void validaDestino(String destino, ArrayList<String> erroresLupas)
			throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaDestinoCM (destino)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaDestino.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaDestino",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaDestino",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}
	}

	@Override
	public void validaTipoPlantacion(String tipoPlantacion,
			ArrayList<String> erroresLupas) throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaTipoPlantacionCM (tipoPlantacion)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaTipoPlantacion.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaTipoPlantacion",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaTipoPlantacion",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}	
	}

	@Override
	public void validaSisCultivo(String sisCultivo,
			ArrayList<String> erroresLupas) throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaSisCultivoCM (sisCultivo)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaSisCult.ko")+ "<br>");
			}
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaSisCultivo",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaSisCultivo",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}		
	}
	
	@Override
	public void validaMarcoPlan(String tipoMarcoPlan,
			ArrayList<String> erroresLupas) throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaMarcoPlanCM (tipoMarcoPlan)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaTipoMarcoPlan.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaMarcoPlan",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaMarcoPlan",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}		
	}



	@Override
	public void validaPracticaCultural(String practicaCultural,
			ArrayList<String> erroresLupas) throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaPracticaCulturalCM (practicaCultural)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupaPracticaCultural.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaPracticaCultural",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaPracticaCultural",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}		
	}

	@Override
	public void validaUbicacion(String provincia, String comarca,
			String termino, String subtermino, ArrayList<String> erroresLupas) throws Exception {
		try{
			if (!validaLupasCambioMasivoDao.validaUbicacionCM (provincia,comarca,termino,subtermino)){
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupasUbicacion.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaUbicacion",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaUbicacion",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}		
		
	}
	
	
	@Override
	public void validaSistemaProduccion(String sistProd, ArrayList<String> erroresLupas)
			throws Exception {
		
		try{
			if (!validaLupasCambioMasivoDao.validaSistemaProduccion(sistProd)){				
				erroresLupas.add(bundle.getString("mensaje.cambioMasivo.lupasSistProd.ko")+ "<br>");
			}
		
		} catch (DAOException ex) {
    		logger.error("Se ha producido un error durante el acceso a la base de datos - validaSistemaProduccion",ex);
    		throw new BusinessException("[ERROR] al acceder a la BBDD.",ex);
		} catch (Exception e){
			logger.error("Se ha producido un error generico - validaSistemaProduccion",e);
    		throw new Exception("[ERROR] al acceder a la BBDD.",e);
		}		
		
	}

	public void setValidaLupasCambioMasivoDao(
			IValidaLupasCambioMasivoDao validaLupasCambioMasivoDao) {
		this.validaLupasCambioMasivoDao = validaLupasCambioMasivoDao;
	}
	
}
