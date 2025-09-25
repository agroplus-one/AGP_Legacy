package com.rsi.agp.dao.models.comisiones;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.Fase;

public interface IRgaComisionesDao extends GenericDao{

	public void generarDatosRgaComisiones(List<Fase> listficherosCerrados)throws DAOException;
}
