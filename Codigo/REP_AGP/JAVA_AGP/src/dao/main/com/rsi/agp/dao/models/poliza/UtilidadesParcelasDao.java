package com.rsi.agp.dao.models.poliza;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projections;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.filters.poliza.ParcelaFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.poliza.Parcela;

@SuppressWarnings("rawtypes")
public class UtilidadesParcelasDao extends BaseDaoHibernate implements IUtilidadesParcelasDao  {
	private static final Log logger = LogFactory.getLog(UtilidadesParcelasDao.class);

	
	/**
	 * Obtiene todos los ids de parcelas de una poliza ordenados y filtrados tal y como en el listado.
	 * @param 
	 */	
	public List getIdsParcelas(Parcela parcelaFiltro, String columna, String orden) throws BusinessException {
		logger.debug("init - [UtilidadesParcelaDao] getIdsParcelas");
		
		final ParcelaFiltro filter= new ParcelaFiltro(parcelaFiltro, columna, orden);
		// le digo a Hibernate que solo quiero los idsParcela para rellenar mi lista de parcelas
		List listIdParcelas = filter.getCriteria(this.obtenerSession()).setProjection(Projections.property("idparcela")).list(); 
				
		logger.debug("end - [UtilidadesParcelaDao] getIdsParcelas");
		
		return listIdParcelas;		
	}

}