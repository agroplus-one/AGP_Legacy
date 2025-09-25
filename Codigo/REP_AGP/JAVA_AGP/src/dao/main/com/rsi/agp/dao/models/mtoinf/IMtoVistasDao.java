package com.rsi.agp.dao.models.mtoinf;

import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.RelVistaCampos;
import com.rsi.agp.dao.tables.mtoinf.Vista;

public interface IMtoVistasDao  extends GenericDao{
	
	public List<Vista> getListadoVistas ();
	
	public List<RelVistaCampos> getRelVistaCampos ();

}
