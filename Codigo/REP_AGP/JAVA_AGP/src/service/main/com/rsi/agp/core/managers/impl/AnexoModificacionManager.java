package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.AnexoModificacionUtils;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IAnexoModificacionDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.commons.Via;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class AnexoModificacionManager implements IManager {
	
	private static final Log log = LogFactory.getLog(AnexoModificacionManager.class);
	
	private IAnexoModificacionDao anexoModificacionDao;
	
	public AnexoModificacion obtenerAnexoModificacionById(Long idAnexoModificacion) throws DAOException{

		AnexoModificacion anexoModificacion = null;
		anexoModificacion = (AnexoModificacion)anexoModificacionDao.get(AnexoModificacion.class, idAnexoModificacion);
		return anexoModificacion;
	}

	
	public void guardarAnexoModificacion(AnexoModificacion anexoModificacion) throws DAOException{
		anexoModificacionDao.saveOrUpdate(anexoModificacion);
	}
	
	
	public boolean isAnexoExplotacionesConModificaciones(Long idAnexoModificacion) throws BusinessException{
		boolean isModificado = false;
		
		try {
			isModificado = anexoModificacionDao.isAnexoExplotacionesConModificaciones(idAnexoModificacion);
		} catch (DAOException e) {
			log.error("Error al comprobar si el anexo [" + idAnexoModificacion + "] tiene explotaciones modificadas");
			throw new BusinessException(e);
		}
		
		return isModificado;
	}
	
	public boolean isAnexoCoberturasConModificaciones(Long idAnexoModificacion) throws BusinessException{
		boolean isModificado = false;
		
		try {
			isModificado = anexoModificacionDao.isAnexoCoberturasConModificaciones(idAnexoModificacion);
		} catch (DAOException e) {
			log.error("Error al comprobar si el anexo [" + idAnexoModificacion + "] tiene coberturas modificadas");
			throw new BusinessException(e);
		}
		
		return isModificado;
	}
	
	public boolean isAnexoSubvencionesConModificaciones(Long idAnexoModificacion) throws BusinessException{
		boolean isModificado = false;
		
		try {
			isModificado = anexoModificacionDao.isAnexoSubvencionesConModificaciones(idAnexoModificacion);
		} catch (DAOException e) {
			log.error("Error al comprobar si el anexo [" + idAnexoModificacion + "] tiene coberturas modificadas");
			throw new BusinessException(e);
		}
		
		return isModificado;
	}
	
	public boolean tieneModificacionesAnexo(final AnexoModificacion anexo,
			final es.agroseguro.contratacion.Poliza sitActual, final Character ppalComp) {
		boolean tieneModificaciones 		   = false;
		boolean tieneModificacionesCoberturas  = false;
		boolean tieneModificacinoesSubenciones = false;
		boolean tieneModificacionesParcelas    = false;
		if (anexo != null){
			try {
				log.debug("anexo: "+anexo.getId());
				tieneModificacionesParcelas = isAnexoParcelasConModificaciones(anexo.getId());
				log.debug("tieneModificacionesParcelas: " + tieneModificacionesParcelas);
			} catch (BusinessException e) {
				log.error("Error al comprobar si el anexo tiene parcelas modificadas");
			}
			
			if (Constants.MODULO_POLIZA_PRINCIPAL.equals(ppalComp) && !tieneModificacionesParcelas) {
				tieneModificacionesCoberturas  = AnexoModificacionUtils.tieneCambiosCoberturas(anexo, sitActual);
				log.debug("tieneModificacionesCoberturas: " + tieneModificacionesCoberturas);
			}
			if (Constants.MODULO_POLIZA_PRINCIPAL.equals(ppalComp) && !tieneModificacionesParcelas
					&& !tieneModificacionesCoberturas) {
				tieneModificacinoesSubenciones = checkCambiosSubvenciones(anexo.getSubvDeclaradas());
				log.debug("tieneModificacinoesSubenciones: " + tieneModificacinoesSubenciones);
			}	
			if (tieneModificacionesParcelas || tieneModificacionesCoberturas || tieneModificacinoesSubenciones) {
				tieneModificaciones = true;
			}
		}
		return tieneModificaciones;
	}
	
	public boolean isAnexoParcelasConModificaciones(Long idAnexoModificacion) throws BusinessException{
		boolean isModificado = false;
		
		try {
			isModificado = anexoModificacionDao.isAnexoParcelasConModificaciones(idAnexoModificacion);
		} catch (DAOException e) {
			log.error("Error al comprobar si el anexo [" + idAnexoModificacion + "] tiene parcelas modificadas");
			throw new BusinessException(e);
		}
		
		return isModificado;
	}

	public boolean checkCambiosSubvenciones(Set<SubvDeclarada> subvDeclaras) {
		boolean hayCambios = false;
		if(subvDeclaras!= null && !subvDeclaras.isEmpty()) {
			for (SubvDeclarada s: subvDeclaras){
				if (s.getTipomodificacion()!= null){
					hayCambios = true;
					break;
				}
			}
		}
		return hayCambios;
	}
	
	public boolean isAnexoAseguradoConModificaciones(final Poliza polizaAnexo,
			final es.agroseguro.contratacion.Poliza sitActual) {
		
		es.agroseguro.contratacion.Asegurado aseguradoSitActual = sitActual.getAsegurado();
		Asegurado aseguradoAnexo = polizaAnexo.getAsegurado();
		
		try {
		    return compararAsegurado(aseguradoAnexo, aseguradoSitActual);
		    
		} catch (Exception e) {
			 e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean compararCampos(String campoAnexo, String campoSitAct) {
		
	    // Tratamos las cadenas de solo espacios en blanco como null
		campoAnexo = (campoAnexo != null && !campoAnexo.trim().isEmpty()) ? campoAnexo.trim() : null;
	    campoSitAct = (campoSitAct != null && !campoSitAct.trim().isEmpty()) ? campoSitAct.trim() : null;

	    log.info(String.format("Comparando: anexo='%s', sitAct='%s'", campoAnexo, campoSitAct));

	    return (campoAnexo == null && campoSitAct != null) || 
	            (campoAnexo != null && campoSitAct == null) || 
	            (campoAnexo != null && campoSitAct != null && !campoAnexo.equals(campoSitAct));
	}

	
	public boolean compararAsegurado(Asegurado aseguradoAnexo, es.agroseguro.contratacion.Asegurado aseguradoSitAct) {

		if (compararCampos(aseguradoAnexo.getNifcif(), aseguradoSitAct.getNif())) {
		    return true;
		}
		
		if (aseguradoSitAct.getNombreApellidos() != null) {
			if (compararCampos(aseguradoAnexo.getNombre(), aseguradoSitAct.getNombreApellidos().getNombre())) {
			    return true;
			}
			if (compararCampos(aseguradoAnexo.getApellido1(), aseguradoSitAct.getNombreApellidos().getApellido1())) {
			    return true;
			}
			if (compararCampos(aseguradoAnexo.getApellido2(), aseguradoSitAct.getNombreApellidos().getApellido2())) {
			    return true;
			}
		} else {
			
			if (aseguradoSitAct.getRazonSocial() != null) {
				if (compararCampos(aseguradoAnexo.getRazonsocial(), aseguradoSitAct.getRazonSocial().getRazonSocial())) {
					return true;
				}
			}
		}
		
		Via viaAnexo = aseguradoAnexo.getVia();
		String viaAnexoCompleta = (viaAnexo != null && viaAnexo.getClave() != null && aseguradoAnexo.getDireccion() != null) ? viaAnexo.getClave() + " " + aseguradoAnexo.getDireccion() : null;
		if (compararCampos(viaAnexoCompleta, aseguradoSitAct.getDireccion().getVia())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getNumvia(), aseguradoSitAct.getDireccion().getNumero())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getPiso(), aseguradoSitAct.getDireccion().getPiso())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getBloque(), aseguradoSitAct.getDireccion().getBloque())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getEscalera(), aseguradoSitAct.getDireccion().getEscalera())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getLocalidad().getProvincia().getCodprovincia().toString(), new BigDecimal(aseguradoSitAct.getDireccion().getProvincia()).toString())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getLocalidad().getNomlocalidad(), aseguradoSitAct.getDireccion().getLocalidad())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getCodpostal().toString(), new BigDecimal(aseguradoSitAct.getDireccion().getCp()).toString())) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getTelefono(), String.valueOf(aseguradoSitAct.getDatosContacto().getTelefonoFijo()))) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getMovil(), String.valueOf(aseguradoSitAct.getDatosContacto().getTelefonoMovil()))) {
		    return true;
		}
		if (compararCampos(aseguradoAnexo.getEmail(), aseguradoSitAct.getDatosContacto().getEmail())) {
			return true;
		}

		log.info("SIN CAMBIOS EN EL ASEGURADO");
		return false;

	}
	
	//SETs
	public void setAnexoModificacionDao(IAnexoModificacionDao anexoModificacionDao) {
		this.anexoModificacionDao = anexoModificacionDao;
	}
}