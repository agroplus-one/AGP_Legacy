package com.rsi.agp.core.managers.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.poliza.IUtilidadesParcelasDao;
import com.rsi.agp.dao.tables.poliza.Parcela;


public class UtilidadesParcelasManager implements IManager{
	
	private static final Log logger = LogFactory.getLog(UtilidadesParcelasManager.class); 

	private IUtilidadesParcelasDao utilidadesParcelasDao;
    
	public void setUtilidadesParcelasDao(IUtilidadesParcelasDao utilidadesParcelasDao) {
		this.utilidadesParcelasDao = utilidadesParcelasDao;
	}
	
	/**DAA 14/11/2012
	 * Obtiene todos los ids de parcelas de una poliza ordenados y filtrados tal y como en el listado.
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
			listaIdsParcelas = utilidadesParcelasDao.getIdsParcelas(parcelaFiltro, columna, orden);
		}
		catch(Exception excepcion){
			logger.error("Se ha producido un error al recuperar el listado de ids de Parcelas",excepcion);
		}
	return listaIdsParcelas;
	}

	
}
