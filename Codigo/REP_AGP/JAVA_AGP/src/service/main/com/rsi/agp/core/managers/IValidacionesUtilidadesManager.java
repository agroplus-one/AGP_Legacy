package com.rsi.agp.core.managers;

import java.util.List;

public interface IValidacionesUtilidadesManager {
	
	/**
	 * Comprueba que las polizas correspondientes a los ids pasados como parametro pueden ser borradas
	 * @param ids Lista de ids de poliza separados por ';'
	 * @return Boolean que indica si se puede hacer el borrado
	 */
	public boolean validarPolizasBorradoMasivo (String ids);

	/**DAA 11/06/2012
	 * Comprueba que las polizas correspondientes a los ids pasados como parametro pueden ser cambiadas de oficina
	 * @param ids Lista de ids de poliza separados por ';'
	 * @return String que indica la entidad o bien "false" para no permitir el cambio
	 */
	public String validarPolizasCambioOficinaMultiple(String ids, boolean perfil0);
	
	public List<String> limpiarVacios (List<String> listaIni) ;	
}
