package com.rsi.agp.core.managers;

import com.rsi.agp.core.exception.DAOException;

public interface ICambioMasivoPolizasManager {	
	
	void pagoMasivo(String fechapago, String listaIds, String marcar_desmarcar,String codUsuario) throws DAOException;
}
