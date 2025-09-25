package com.rsi.agp.core.jmesa.service.mtoinf;

import java.util.List;

import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public interface IMtoVistasService {

	public List<Vista> getListadoVistas ();
	
	public List<RelVistaCampos> getRelVistaCampos();
}
