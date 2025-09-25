package com.rsi.agp.dao.models.cpl;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;

@SuppressWarnings("rawtypes")
public interface IAseguradoAutorizadoDao extends GenericDao {
	
	public List<AseguradoAutorizado> getAseguradosAutorizados(Long lineaseguroid, String nifasegurado, String modulo, 
			String fechaFinGarantias,String garantizado, String codcultivo) throws DAOException;
	
	//METODOS PARA LA GENERACION DE LOS CUADROS DE COBERTURAS
	public boolean checkAseguradoAutorizadoGarantizado(long lineaseguroid);
	public boolean checkAseguradoAutorizadoNif(long lineaseguroid,String nifAsegurado);
	public List<AseguradoAutorizado> lstAsegGarantizadosAplicables(long lineaseguroid,String nifAsegurado);

}
