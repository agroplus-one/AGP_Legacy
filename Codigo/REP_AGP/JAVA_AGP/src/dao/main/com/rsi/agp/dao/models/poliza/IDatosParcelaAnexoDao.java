package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.AmbitoAsegurable;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraPrecio;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.SubvencionCCAA;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.vo.DatosPantallaConfigurableVO;
import com.rsi.agp.vo.ItemSubvencionVO;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.ParamsSubvencionesVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.SigpacVO;

@SuppressWarnings("rawtypes")
public interface IDatosParcelaAnexoDao extends GenericDao {
	
	public List<Comarca> getComarcas(Long codProvincia) throws BusinessException;

	public List<VistaTerminosAsegurable> getSubterminos(Long codProvincia, Long codTermino, String subtermino,
			Long linea) throws DAOException;

	public List<VistaTerminosAsegurable> getSubterminosBySigpacList(List<LocalCultVarVO> listLocalCultVarVO, Long linea,
			Long claseId) throws DAOException;

	public List<Cultivo> getCultivos(Long codLinea, Long codPlan) throws BusinessException;

	public List<Variedad> getVariedades(Long codCultivo, Long lineaseguroid) throws BusinessException;

	public List<TipoCapital> getTiposCapital(Long lineaSeguroId, Long codProvincia, Long codComarca, Long codTermino,
			String subtermino, Long codPlan, Long codLinea, Long cultivo, Long variedad, String perfilUsuario,
			Long idPantalla, List<BigDecimal> lstTipoCapitales);

	public PantallaConfigurable getPantallaConfigurada(Long idPantallaConfigurable) throws BusinessException;

	public Long getIdPantallaConfigurable(Long codLinea, Long codPlan, Long idPantalla) throws BusinessException;

	public List<com.rsi.agp.dao.tables.poliza.CapitalAsegurado> getCapitalesAseguradoParcela(Long codParcela)
			throws BusinessException;

	public List getModulosPoliza(Long idPoliza) throws BusinessException;

	public List getConceptosRelacionados(List listaMascaras);

	public List getConceptosObligatorios(Long idPantallaConfigurable);

	public List getMascaraFCA(DatosPantallaConfigurableVO datosPantalla, List modulos);

	public List getMascaraGT(DatosPantallaConfigurableVO datosPantalla, List modulos);

	public List getMascaraLRDTO(DatosPantallaConfigurableVO datosPantalla, List modulos);

	public List getMascaraP(DatosPantallaConfigurableVO datosPantalla, List modulos);

	public ArrayList<ItemSubvencionVO> getSubvencionesParcelaEnesa(ParamsSubvencionesVO paramsSubvencionesVO);

	public ArrayList<ItemSubvencionVO> getSubvencionesParcelaCCAA(ParamsSubvencionesVO paramsSubvencionesVO);

	public List<SubvencionCCAA> getSubvencionesCCAA(Long codigo);

	public List<SubvencionEnesa> getSubvencionesEnesa(Long codigo);

	public List<MascaraLimiteRendimiento> getMascaraLimiteRendimiento(Long lineaseguroid, String codmodulo,
			ParcelaVO parcela);

	public List<LimiteRendimiento> getRendimientos(LimiteRendimientoFiltro limRendFiltro);

	public List<MascaraPrecio> getMascaraPrecio(Long lineaseguroid, String codmodulo, ParcelaVO parcela);

	public List<Precio> getPrecio(PrecioFiltro precioFiltro) throws DAOException;

	public List<ModuloPoliza> getModulosPoliza(Long idPoliza, Long lineaseguroid) throws DAOException;

	// public List<Parcela> getParcelas(Long idAnexo) throws DAOException;
	public List<ComparativaPoliza> getRiesgosCubiertos(String codPoliza, BigDecimal codRiesgo, String valor)
			throws DAOException;

	public Comarca getComarca(BigDecimal codProvincia, BigDecimal codComarca) throws DAOException;

	public Termino getTermino(BigDecimal codProvincia, BigDecimal codComarca, BigDecimal codTermino)
			throws DAOException;

	public Cultivo getCultivo(BigDecimal codCultivo) throws DAOException;

	public Variedad getVariedad(BigDecimal codCultivo, BigDecimal codVariedad) throws DAOException;

	public boolean existeLimiteRendimientoByLineaseguroid(Long lineaseguroid);

	public Long getNumMayorParcelaToAnexo(Long idAnexo) throws DAOException;

	public void deleteCapitalAsegurado(CapitalAsegurado capitalAsegurado);

	public Long saveCapitalAsegurado(CapitalAsegurado capitalAsegurado) throws DAOException;

	public void updateCapitalAsegurado(CapitalAsegurado capitalAsegurado) throws DAOException;

	public void deleteParcela(Parcela parcela) throws BusinessException;

	public Long saveObjectParcela(Parcela parcela) throws Exception;

	public void saveOrUpdateParcela(Parcela parcela) throws DAOException;

	public List<ConfiguracionCampo> getListConfigCampos(BigDecimal idpantalla) throws DAOException;

	public List<AmbitoAsegurable> getAmbitosAsegurablesProvincias(Long lineaseguroid) throws DAOException;

	public Linea getLineaseguroId(Long codlinea, Long codplan);

	public LocalCultVarVO getLocalCultVar(SigpacVO sigpacVO) throws DAOException;

	public List<BigDecimal> dameListaValoresConceptoFactor(BigDecimal lineaseguroid, String lstModulos,
			BigDecimal codConcepto);

	public String getClaseQuery(String campo);

	public List getFieldFromClase(Long lineaseguroid, Long clase, String query);

	public List<BigDecimal> dameListaTotalValoresConceptoFactor(BigDecimal lineaseguroid, BigDecimal codConcepto);

	public CapitalDTSVariable getDatoVariable(BigDecimal codConcepto, Long idCapitalAsegurado);

	public CapitalDTSVariable getDatoVariable(CapitalDTSVariable dv);

	public void borrarDatosVariables(String idCapitalAsegurado, List<Integer> listaConceptos);

	public Parcela clonarParcelaAnexo(Long idParcela, Usuario usuario, Parcela clonParcela);
}