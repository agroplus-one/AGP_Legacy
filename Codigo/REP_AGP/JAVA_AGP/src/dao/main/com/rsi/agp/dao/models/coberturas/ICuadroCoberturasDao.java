package com.rsi.agp.dao.models.coberturas;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.coberturas.CuadroCobertura;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;

@SuppressWarnings("rawtypes")
public interface ICuadroCoberturasDao extends GenericDao {
	
	public List<CuadroCobertura> getCoberturas(CaracteristicaModulo caracteristicaModulo);
	
	public List<CuadroCobertura> getCoberturas(Long lineaseguroid, String codmodulo, 
			BigDecimal filamodulo, BigDecimal columnamodulo, BigDecimal codconcepto, BigDecimal valor);

	public AnexoModificacion getAnexo(String idAnexo);
}
