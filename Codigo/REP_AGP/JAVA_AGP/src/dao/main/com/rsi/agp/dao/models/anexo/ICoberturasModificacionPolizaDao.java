package com.rsi.agp.dao.models.anexo;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.copy.CoberturaPoliza;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;

@SuppressWarnings("rawtypes")
public interface ICoberturasModificacionPolizaDao extends GenericDao {

	public List<Modulo> getModulosPoliza(Long lineaseguroid) throws DAOException;
	public List<ComparativaPoliza> getCoberturasPoliza(Long idPoliza) throws DAOException;
	public List<CoberturaPoliza> getCoberturasCopy(Long idCopy) throws DAOException;
	public boolean isUsuarioAutorizado(Long lineaseguroid, String nifcif) throws DAOException;
	public Modulo getModulo(Long lineaseguroid, String codmodulo) throws DAOException;
	public boolean saveCoberturasAnexo(Long idAnexo, List<Cobertura> listCoberturas) throws DAOException;
	public List<VinculacionValoresModulo> getLstVincValMod(Long lineaseguroid, String codModulo) throws DAOException;
	public List<VinculacionValoresModulo> getLstVincValMod2(Long lineaseguroid, String codModulo) throws DAOException;
	public String getDesValorByCodConcepto(String sql) throws DAOException;
	public BigDecimal getFilaModuloByCodConcepto(String sqlFila) throws DAOException;
}
