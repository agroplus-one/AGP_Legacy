package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IUtilidadesParcelasAnexoDao;

//import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.anexo.Parcela;

public class UtilidadesParcelasAnexoManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(UtilidadesParcelasAnexoManager.class); 

	private IUtilidadesParcelasAnexoDao utilidadesParcelasAnexoDao;
  	
	public void setUtilidadesParcelasAnexoDao(IUtilidadesParcelasAnexoDao utilidadesParcelasAnexoDao) {
		this.utilidadesParcelasAnexoDao = utilidadesParcelasAnexoDao;
	}
	
	/**AMG 19/02/2014
	 * Obtiene todos los ids de parcelas de Anexo de una poliza ordenados y filtrados tal y como en el listado.
	 * @param orden 
	 * @param columna 
	 * @param parcelaBusqueda 
	 * @return listado de objetos idsParcelasVO
	 */
	public List getIdsParcelas(Parcela parcelaFiltro, String columna, String orden){
		List listaIdsParcelas = null;
		//DAA 23/09/2013 Nos aseguramos que los ids de parcela que traiga sean unicamente de parcelas y no instalaciones
		parcelaFiltro.setTipoparcela(Constants.TIPO_PARCELA_PARCELA);

		try{
			listaIdsParcelas = utilidadesParcelasAnexoDao.getIdsParcelas(parcelaFiltro, columna, orden);
		}
		catch(Exception excepcion){
			logger.error("Se ha producido un error al recuperar el listado de ids de Parcelas de Anexo",excepcion);
		}
	return listaIdsParcelas;
	}

	
}
