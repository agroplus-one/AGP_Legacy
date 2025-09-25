package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionSystemException;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.poliza.PolizaFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

@SuppressWarnings("rawtypes")
public interface ISeleccionPolizaDao extends GenericDao {

	public int getCountPolizas(PolizaFiltro polizaFiltro);

	public void deleteParcela(Long idParcela) throws DAOException;

	public void deleteCapAsegRelModulo(CapAsegRelModulo carm);

	public void cloneParcela(Long idParcela);

	public Parcela getParcela(Long idParcela);

	public List<Parcela> getParcelas(Parcela parcela, String columna, String orden);

	public Long savePoliza(Poliza polizaBean);

	public Long saveParcela(Parcela parcela);

	public Long saveCapAseg(CapitalAsegurado capAseg);

	public List<ModuloPoliza> getModulosPoliza(Long idPoliza) throws BusinessException;

	public List<ComparativaPoliza> getModulosPolizaWithComparativa(Long idPoliza) throws BusinessException;

	public List<TipoCapital> getTiposCapitales(Long idlinea) throws DAOException;

	public TipoCapital getTipoCapitalById(Long codtipocapital) throws DAOException;

	public void borrarPoliza(Poliza poliza, Usuario usuario) throws DAOException;

	public List<Poliza> getPolizasButEstadosGrupoEnt(Poliza polizaBusqueda, PolizaFiltro polizaFiltro)
			throws DAOException;

	public PaginatedListImpl<Poliza> getPaginatedListPolizasButEstadosGrupoEnt(Poliza polizaBean,
			PageProperties pageProperties, PolizaFiltro polizaFiltro, Usuario usuario) throws DAOException;

	public Map<String, Object> getMapaPoliza(Poliza poliza) throws DAOException;

	public Usuario aseguradoCargadoUsuario(Asegurado aseg, Long lineaseguroid, String usuario) throws DAOException;

	public List<BigDecimal> getIdsPolizaSiniestros() throws DAOException;

	public List<BigDecimal> getIdsPolizaRedCapital() throws DAOException;

	public List<BigDecimal> getIdsAnexoMod() throws DAOException;

	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException;

	public List<Poliza> getVerificarPolizas(Poliza polizaBean) throws DAOException;

	public void saveParcela2(Parcela nPar) throws DAOException;

	public void saveCapAsegurado(CapitalAsegurado nCap) throws DAOException;

	public void saveDatoVarParcela(DatoVariableParcela nDat) throws DAOException;

	public void cambioMasivo(CambioMasivoVO cambioMasivoVO, Poliza poliza, Usuario usuario, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd, String recalcular,
			Map<Long, List<Long>> mapaParcelasInstalaciones, boolean guardaSoloPrecioYProd)
			throws DAOException, SQLIntegrityConstraintViolationException, TransactionSystemException;

	public String borradoMasivo(String strCodsParcelaEliminar) throws DAOException;

	public void duplicadoMasivo(String idParcela, Poliza poliza, Long cantDuplicar) throws DAOException;

	public List<ClaseDetalle> getClaseDetalle(long lineaseguroid, BigDecimal clase);

	public void reCalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza) throws Exception;

	public void reCalculoPrecioProduccion(Collection<Parcela> parcelas, List<String> codsModuloPoliza, boolean esConWS,
			Map<String, ProduccionVO> mapaRendimientosProd) throws Exception;

	public List<SubvencionCCAA> getSubvencionesCCAANivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException;

	public List<SubvencionEnesa> getSubencionesEnesaNivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException;

	public List<Modulo> getCoberturasNivelParcela(List<String> listCodigosModulos, Long lineaseguroid)
			throws DAOException;

	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo);

	public List<String> getListCodModulosClase(Long idClase);

	public String getIdsPolizas(PolizaFiltro polizaFiltro) throws DAOException;

	public void actualizaTotSuperficie(String polizasString) throws DAOException;

	public List<BigDecimal> getListCodCultivosClase(Long idClase);

	public List<BigDecimal> getListCodVariedadesClase(Long idClase);

	public List<Long> getParcelasDeInstalaciones(Long idPoliza, List<Long> listaIdsParcelas);

	public void actualizaInstalacion(Long idInstalacion, Long idNuevaParcela) throws DAOException;

	public List<Parcela> getParcelasMismoSigpac(Parcela parcelaBorrar, Long idPoliza, List<Long> listaIdsParcelas);

	boolean aplicaSistemaCultivoARendimiento(Long lineaseguroid);

	HashMap<BigDecimal, BigDecimal> getLineasRecalculo();

	public boolean isOficinaPagoManual(BigDecimal oficina, BigDecimal codEntidad) throws DAOException;

	public void modificarParcelaCambioMasivo(CambioMasivoVO cambioMasivoVO, int i, Parcela parcela,
			BigDecimal maxProduccion, BigDecimal minProduccion, Usuario usuario, boolean guardaSoloPrecioYProd)
			throws DAOException, SQLIntegrityConstraintViolationException;

	public void modificarInstalacionesCambioMasivo(CambioMasivoVO cambioMasivoVO, List<Parcela> listInstalaciones,
			Usuario usuario);

	public void actualizaHojaNumero(int hoja, int num, Long idparcela) throws DAOException;

	public boolean isParcelasCorrectas(Long idPoliza);

	public Map<Long, List<Long>> getMapaParcelasInstalaciones(List<String> lstCadenasIds) throws DAOException;

	public List<Parcela> getInstalaciones(List<Long> lstIdsInstall);

	public void marcarRecalculoHojaYNum(Long idPoliza);

	public boolean checkRecalculoHojaYNumPoliza(Long idPoliza);

	public void inicializarRecalculoHojaYNumPoliza(Long idPoliza);

	public void deleteParcelas(List<Long> listaParcelas) throws DAOException;

	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulos(final Long lineaseguroid, final String listCodModulos);
	
	public Map<String, List<String>> getCoberturasCapAseg301(Long idPoliza, String codmodulo) throws BusinessException;
	
	public List<Explotacion> getExplotaciones(final Long idPoliza) throws DAOException;
	
	public List<PolizaSocio> getSociosPoliza(final Long idPoliza) throws DAOException;
}
