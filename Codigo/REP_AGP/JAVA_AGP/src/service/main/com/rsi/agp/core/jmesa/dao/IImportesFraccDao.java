package com.rsi.agp.core.jmesa.dao;

import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;

public interface IImportesFraccDao extends IGenericoDao {
//	public ImporteFraccionamiento getImporteFraccionamientoSAECA (Long lineaseguroid); 
	
	/**
	 * Devuelve un objeto de ImporteFraccionamiento dado un lineaSeguroId u una subentidadMediadora
	 * @param lineaSeguroId
	 * @param SubentidadMediadora sm
	 * @return
	 */
	public ImporteFraccionamiento obtenerImporteFraccionamiento(Long lineaSeguroId,SubentidadMediadora sm);
	
}