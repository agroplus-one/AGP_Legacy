package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.sort.PolizaActualizadaSort;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.MedidaFranquicia;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.TablaExternaCultivo;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaFija;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;

@SuppressWarnings("rawtypes")
public interface IPolizaDao extends GenericDao {
	
	public List<VistaComparativas> getComparativa(final Filter filter);
	public void borrarComparativasSelec(Long idpoliza, String codmodulo, String comparativa) throws DAOException;
	public String getRefPolizaById(Long idPoliza) throws DAOException;
	public Poliza getPolizaById(Long idPoliza) throws DAOException;
	public Poliza getPolizaByReferencia(String refPoliza,Character tipoRefPoliza) throws DAOException;
	public Poliza getPolizaByReferenciaPlan(String refPoliza, Character tipoRefPoliza, BigDecimal plan) throws DAOException;
	public Poliza getPolizaByRefPlanLin(String refPoliza, Character tipoRefPoliza, BigDecimal plan, BigDecimal linea) throws DAOException;
	public void updateComparativas(ComparativaPolizaId compPoriginal, ComparativaPolizaId compPupdate) throws DAOException;
	public int getNumeroMaxComparativas() throws DAOException;
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgo(Poliza poliza, ComparativaPoliza cp) throws BusinessException;
	/* Pet. 63497 (REQ.01) ** MODIF TAM (31/03/2020) ** Inicio */
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoJavaImpl(Poliza poliza, ComparativaPoliza cp, String webServiceToCall) throws BusinessException;
	/* Pet. 63497 (REQ.01) ** MODIF TAM (31/03/2020) ** Fin */
	public Map<BigDecimal, List<String>> getDatosVariablesParcelaRiesgoCPL(Poliza poliza,String codModulo) throws BusinessException;
	public EnvioAgroseguro saveEnvioAgroseguro(EnvioAgroseguro envio) throws DAOException;
	public void evictEnvio(EnvioAgroseguro envio);
	public void evictPoliza(Poliza poliza);
	public void actualizaXmlEnvio (Long idEnvio, final String xml, final String calculo);
	public void actualizaXmlPoliza (Long idPoliza, final String xml);
	public boolean aplicaCaractExplotacion(final Long lineaseguroid) throws Exception;
	public void deleteCaractExplotacion(final Long idpoliza) throws DAOException;
	public boolean tieneAccesoAPolizaByIdPoliza(Usuario usuario, String idPoliza);
	public boolean tieneAccesoAPolizaByRefPoliza(Usuario usuario, String refPoliza);
	public boolean tieneAccesoAAnexo(Usuario usuario, String idAnexo);
	public boolean tieneAccesoAAsegurado(Usuario usuario, String idAsegurado);
	public boolean tieneAccesoASiniestro(Usuario usuario, String idSiniestro);
	public boolean tieneAccesoARedCapital(Usuario usuario, String idRedCapital);
	public BigDecimal getHistAsegBonus(Long lineaseguroid, BigDecimal codpctfranquiciaeleg);
	public List<BigDecimal> getMedAsocHistAseg(Long lineaseguroid, String codmodulo, BigDecimal codHistAseg);
	public BigDecimal getPctBonifRecargo(Long lineaseguroid, String nifcif);
	
	public List<VinculacionValoresModulo> getVinculacionesValoresModulo(
			Long lineaseguroid, String codmodulo, BigDecimal codconcepto, BigDecimal valor, BigDecimal filamodulo,
			BigDecimal columnamodulo) throws DAOException;
	
	public String getMSGAclaracionModulo(String codmodulo, Long lineaseguroid)throws DAOException;
	public Comunicaciones getComunicaciones(BigDecimal idEnvio) throws DAOException;
	public List<TablaExternaCultivo> getTablaExtCultivo(Long lineaseguroid, BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto, String codmodulo );
	public List<BigDecimal> getCultivosClase(BigDecimal clase, Long lineaseguroid);
	public int validarFecha(Poliza poliza) throws DAOException;
	public BigDecimal getPctFranquicia(long lineaseguroid, String codmodulo,String nifCif, BigDecimal codconceptoppalmod, BigDecimal codriesgocubierto,Character elegido);
	
	public String getDescPctFranquiciaEleg(BigDecimal pctFranquicia);
	public List<ComparativaPoliza> getLstCompPolizas(long idpoliza, String modulo);
	public List<ComparativaPoliza> getLstCompPolizasByIdPol(long idpoliza);
	public List<ModuloPoliza> getLstModulosPoliza(long idpoliza);
	public List<MedidaFranquicia> getLstMedidasFranquicia(String nifcif, long lineaseguroid, String modulo, Character elegido);
	public List<Poliza> getListPolizasAseg(long idAseg,Poliza poliza, String OpTipoPol);
	public String getTotalProdComparativa(String modulo,Poliza poliza);
	public BigDecimal getSistCultivoClase(BigDecimal clase, Long lineaseguroid);
	public List<ConceptoCubiertoModulo> getMapConceptoCubMod(Long lineaseguroid, String codmodulo);
	public BigDecimal getCodigoConceptoCubMod(Long lineaseguroid, String codmodulo, BigDecimal columnamodulovinc);
	public BigDecimal getValorVincValMod(Long lineaseguroid,String codmodulo, BigDecimal filaModulo,BigDecimal colModulo, BigDecimal codigoVinc, BigDecimal codigo, BigDecimal valorcodigo);
	public boolean checkModulosPoliza(Poliza poliza);
	public boolean existeCultivoVariedad(Long lineaSeguroId, Short cultivo, Short variedad);
	public List<ComparativaFija> getListComparativasFijas(Long lineaseguroid, String modulo, BigDecimal clase);
	public List<String> getLstModulosElegidos(Long idPoliza);
	public List<Parcela> getlistParcelas(Long idPoliza);
	public List<BigDecimal> getHojasPoliza(Long idPoliza);
	public List<BigDecimal> getCodConceptoMod(Long linea, String codmodulo);
	public List<Poliza> dameListaPolizasCplByPpl(Long idpoliza);
	public List<EnvioAgroseguro> getEnvioAgroseguro(Long idPoliza,String codmodulo)throws DAOException;
	public EnvioAgroseguro getEnvioAgroseguro(Long idEnvio) throws DAOException;
	public List<com.rsi.agp.dao.tables.poliza.Poliza> existePolizaPlanLinea(String nifCif, BigDecimal[] codPlan, BigDecimal codLinea, BigDecimal clase, BigDecimal entidad, boolean situacionAct) throws DAOException;
	
	//ASF - 17/9/2012 - AmpliaciÃ³n de la Mejora 79: preguntar si desea recalcular producciÃ³n al cargar la copy
	public int getNumPolizasContratadas(BigDecimal codplan, final BigDecimal codlinea, final String nifasegurado,final boolean polAnterior);
	public Poliza getPolizaContratada(BigDecimal codplan, final BigDecimal codlinea, final String nifasegurado, final boolean polAnterior);
	
	public void arrastreParcelas(Long lineaseguroid, Long idPolizaDestino, Long idcopy, Long idPolizaOrigen, BigDecimal clase)throws Exception;
	
	public Poliza savePoliza (Poliza poliza) throws DAOException;
	Collection<Poliza> getPolizasParaActualizar(Poliza poliza, int rowStart,int rowEnd, final PolizaActualizadaSort sort) throws BusinessException;
	public int getPolizasParaActualizarCount(final Poliza poliza);
	public List<BigDecimal> getCodsConceptoOrganizador(Long lineaseguroid) throws DAOException;
	//DAA 15/01/2013 borra las subvenciones de los modulos no elegidos de la poliza actual
	public void deleteSubvsEnesaModsNoElec(String codModulo, Long idPoliza, Long lineaseguroid) throws DAOException;
	public void deleteSubvsCCAAModsNoElec(String codModulo, Long idPoliza, Long lineaseguroid) throws DAOException;
	public Character isPacCargada (Long idPoliza);
	//DAA 11/07/2013 Actualiza el estado de la poliza tras la carga de la pac
	public void actualizaPacCargadaPoliza(Long idPoliza, Character estado);
	public List<BigDecimal> getCiclosCultivoClase(BigDecimal clase,Long lineaseguroid);
	
	boolean isOficinaConPolizas(String codOficina,BigDecimal codEntidad) throws DAOException;
	PagoPoliza existePagoPoliza(Poliza polizaBean) throws DAOException;
	public void actualizaImporte(Poliza poliza)throws Exception;
	
	//11/03/2014
	public List<Poliza> getPolizasDefinitivas(final BigDecimal codPlan, final BigDecimal codLinea, 
			final String nifAsegurado, final BigDecimal codEntidad, final PolizaActualizadaSort sort, final int rowStart,
			final int rowEnd) throws BusinessException;
	public int getPolizasDefinitivasCount(final BigDecimal codPlan, final BigDecimal codLinea, final String nifAsegurado, final BigDecimal codEntidad);
	
	public boolean esPolizaGanado(final String referencia, final BigDecimal plan) throws DAOException;
	public boolean esPolizaGanadoByIdPoliza(final Long idPoliza) throws DAOException;
	public List<GruposNegocio>getGruposNegocio(Long idPoliza)throws DAOException;
	public List<GruposNegocio>getGruposNegocio(Long lineaseguroid, Long codGrupoRaza, Long codtipocapital)throws DAOException;
	public void actualizaXmlCoberturas(Long id, String envio, String string);
	public void actualizaXmlCoberturasAnexo(Long idEnvio, final String xml, final String respuesta);
	public List getDatosVariablesEspecialesExplotacion(Long idExplotacion, long codConcepto)throws DAOException;
	public void actualizarEstadoComplementaria(Poliza poliza) throws Exception;
	/**
	 * Actualiza el flag de tener siniestros o no de la póliza
	 * @author U029114 21/06/2017
	 * @param idPoliza
	 * @param caracter
	 * @throws DAOException
	 */
	public void actualizaFlagTieneSiniestrosPoliza(Long idpoliza, Character caracter) throws DAOException;
	/**
	 * Actualiza el estado de la póliza
	 * @author U029114 14/09/2017
	 * @param idPoliza
	 * @param idestado
	 * @throws DAOException
	 */
	public void actualizaEstadoPoliza(Long idpoliza, BigDecimal idestado) throws DAOException;
	/**
	 * Obtener el estado de la póliza
	 * @author U029114 14/09/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal obtenerEstadoPoliza(Long idpoliza) throws DAOException;
	
	BigDecimal obtenerPlanPoliza(String referencia, Character tipoRef) throws DAOException;
	
	BigDecimal obtenerLineaPoliza(String referencia, Character tipoRef, BigDecimal codplan) throws DAOException;
	
	String obtenerNifCifDesdeReferenciaPoliza(String referencia, Character tipoRef, BigDecimal codplan) throws DAOException;
	
	String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) throws DAOException;
	public void actualizarPolizaPagada(Long idpoliza, Date fechaEnvioAgro) throws DAOException;
	
	public void actualizaCsvCargadoPoliza(Long idpoliza, Character pacCargadaSi); 
	public Character isCsvCargado(Long idPoliza); 
	
	public List<RiesgoCubiertoModuloGanado> getRiesgoCubiertosModuloGanado(Long lineaSeguroId, String codModulo);
	public List<RiesgoCubiertoModulo> getRiesgoCubiertosModulo(Long lineaSeguroId, String codModulo);
	public Poliza getPolizaByRefPlanTipoRef(String refPoliza, BigDecimal plan, Character tipoRefPoliza) throws DAOException;
	public void saveCobertura(Long idparcela, Long lineaseguroid, BigDecimal elegible, Integer conceptoppalmod, Integer codconcepto,
			Integer codriesgo, String codmodulo)  throws DAOException;
	
	public List<Explotacion> getExplotacionesPoliza(final Long idPoliza) throws DAOException;
	public List<BonificacionRecargo2015> getDcBonifRecargos(final Long idDc2015) throws DAOException;
	public List<DistCosteSubvencion2015> getDcSubvs(final Long idDc2015) throws DAOException;
	public List<SubAseguradoENESAGanado> getSubAseguradoENESAGanados(final Long idPoliza) throws DAOException;
	public List<SubAseguradoCCAAGanado> getSubAseguradoCCAAGanados(final Long idPoliza) throws DAOException;
	
	public List<CapitalAsegurado> getListCapitalesAsegurados(Long idPoliza) throws DAOException;
}