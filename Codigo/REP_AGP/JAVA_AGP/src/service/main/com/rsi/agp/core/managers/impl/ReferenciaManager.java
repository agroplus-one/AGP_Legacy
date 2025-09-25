package com.rsi.agp.core.managers.impl;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ReferenciaDuplicadaException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.ref.ReferenciaFiltro;
import com.rsi.agp.dao.models.ref.IReferenciaDao;
import com.rsi.agp.dao.tables.ref.ReferenciaAgricola;

public class ReferenciaManager implements IManager {

	private IReferenciaDao referenciaDao;
	private static final Log LOG = LogFactory.getLog(ReferenciaManager.class);
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	// Indica cuantas referencias se han insertado hasta el momento
	private Integer insertados = 0;
	// Indica si se ha finalizado el proceso de carga de referencias
	private Boolean finalizado = false;
	
	public Integer getNumInsertados () {
		return insertados;
	}
	
	public Boolean isFinalizado () {
		return finalizado;
	}
	
	@SuppressWarnings("unchecked")
	public final Integer getNumRefLibres() {
		return referenciaDao.getNumObjects(new ReferenciaFiltro(true));
	}
	
	@SuppressWarnings("unchecked")
	public final String getUltimaRef() {
		return referenciaDao.getUltimaRef();
	}

	/**
	 * Inserta las referencias indicadas por el intervalo si no previamente no existe ninguna
	 * @param refInicial Referencia inicial del intervalo
	 * @param refFinal Referencia final del intervalo
	 * @throws ReferenciaDuplicadaException Se lanza si ya existe alguna referencia del intervalo indicado
	 * @throws BusinessException 
	 */
	public void insertarReferencias (String refInicial, String refFinal) throws ReferenciaDuplicadaException {
		
		final Integer longitudRef = Integer.parseInt(bundle.getString("referencia.longitud"));
		Integer numRefIni = Integer.parseInt(refInicial.substring(1));
		Integer numRefFin = Integer.parseInt(refFinal.substring(1));
		String letraRef = refInicial.substring(0,1);
		final String numLetraRef = bundle.getString("referencia.valor" + letraRef.toUpperCase());
		
		// Comprueba si ya hay referencias dadas de alta pertenecientes al intervalo indicado
		try {
			if (hayRefRepetidasEnRango(letraRef.toUpperCase() + String.format("%1$0" + (longitudRef-1) + "d", numRefIni), 
									   letraRef.toUpperCase() + String.format("%1$0" + (longitudRef-1) + "d", numRefFin))) throw new ReferenciaDuplicadaException();
		} catch (BusinessException e1) {
			throw new ReferenciaDuplicadaException();
		}
		
		// Si en el intervalo no hay repetidos se generan las referencias y se insertan
		insertados = 0;
		finalizado = false;
		for (Integer i = numRefIni; i <= numRefFin; i++) {
			// Se formatea el número actual con 0 por la izquierda hasta alcanzar la longitud de la referencia - 1
			String numRef = String.format("%1$0" + (longitudRef-1) + "d", i);
			
			ReferenciaAgricola ra = new ReferenciaAgricola(null, letraRef.concat(numRef));
			ra.setDc(StringUtils.getDigitoControl(Integer.parseInt(numLetraRef.concat(numRef))));
			try {
				referenciaDao.saveOrUpdate(ra);
				insertados++;
			} catch (DAOException e) {
				LOG.error("Ocurrió un error al insertar la referencia", e);
			}
		}
		finalizado = true;
	}
	
	/**
	 * Devuelve un boolean indicando si existe alguna referencia entre el rango indicado por refInicial y refFinal
	 * @param refInicial
	 * @param refFinal
	 * @return
	 * @throws BusinessException
	 */
	public boolean hayRefRepetidasEnRango (String refInicial, String refFinal) throws BusinessException { 
		try {
			return (this.referenciaDao.hayRefRepetidasEnRango(refInicial, refFinal));
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage());
		} catch (Exception e1) {
			LOG.error("Ocurrió un error iniesperado en la llamada a hayRefRepetidasEnRango", e1);
			throw new BusinessException("Ocurrió un error iniesperado en la llamada a hayRefRepetidasEnRango");
		}
		
	}

	public final void setReferenciaDao(final IReferenciaDao referenciaDao) {
		this.referenciaDao = referenciaDao;
	}

}
