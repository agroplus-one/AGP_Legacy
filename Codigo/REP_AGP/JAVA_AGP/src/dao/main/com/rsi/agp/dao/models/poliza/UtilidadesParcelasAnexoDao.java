package com.rsi.agp.dao.models.poliza;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projections;

import com.rsi.agp.core.exception.BusinessException;

import com.rsi.agp.dao.filters.poliza.ParcelaAnexoFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.anexo.Parcela;

public class UtilidadesParcelasAnexoDao extends BaseDaoHibernate implements IUtilidadesParcelasAnexoDao  {
	private static final Log logger = LogFactory.getLog(UtilidadesParcelasAnexoDao.class);

	
	/**
	 * Obtiene todos los ids de parcelas de una poliza ordenados y filtrados tal y como en el listado.
	 * @param 
	 */
	public List getIdsParcelas(Parcela parcelaFiltro, String columna, String orden) throws BusinessException {
		logger.debug("init - [UtilidadesParcelaAnexoDao] getIdsParcelas");
		
		final ParcelaAnexoFiltro filter= new ParcelaAnexoFiltro(parcelaFiltro, columna, orden);
		// le digo a Hibernate que solo quiero los idsParcela para rellenar mi lista de parcelas
		List listIdParcelas = filter.getCriteria(this.obtenerSession()).setProjection(Projections.property("id")).list(); 
				
		logger.debug("end - [UtilidadesParcelaAnexoDao] getIdsParcelas");
		
		return listIdParcelas;		
	}

}