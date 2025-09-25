package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cpl.gan.MedidaG;
import com.rsi.agp.dao.tables.poliza.Poliza;


@SuppressWarnings("rawtypes")
public interface IClaseDao extends GenericDao {
	
	public List<Clase> getListaClases(Clase claseBean) throws DAOException;
	
	public List<ClaseDetalle> getListaDetalleClases(ClaseDetalle claseDetalleBean);
	
	public List<String> dameListaModulosClase(long lineaseguroid, BigDecimal clase);
	
	public Set getClaseDetalle (long lineaseguroid, BigDecimal clase);
	
	public Clase getClaseById( long idClase);
	
	public Clase getClase (Poliza p) throws DAOException;
	
	public Clase getClase (long lineaseguroid, BigDecimal clase);
	
	// ASF - 17/10/2012 - Adaptaciones 314
	public String getComprobarAac (Long lineaseguroid, BigDecimal clase);

	public List<Character> obtenerGruposNegocio(Map<String,List<Long>> mapListas,List<String> listMod,List<Character> listSubterminos);

	public List<MedidaG>getMedidasGanado(Long lineaseguroid,List<Character> lstGrNeg,List<String> mod,
			List<Long> especie, String nifCif);
}
