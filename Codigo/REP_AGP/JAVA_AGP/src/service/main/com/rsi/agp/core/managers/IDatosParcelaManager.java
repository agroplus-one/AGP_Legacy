package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.ConceptoCubiertoVO;
import com.rsi.agp.vo.PantallaConfigurableVO;
import com.rsi.agp.vo.ParcelaVO;

import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;

public interface IDatosParcelaManager {

	public ParcelaVO getParcela(final Long idParcela);
	
	public ParcelaVO getParcela(final Parcela parcela) throws BusinessException;
	
	public void generateParcela(final ParcelaVO parcelaVO, final Parcela parcela, final Long idLineaSeguroId);

	public Map<String, Object> getListaCodigosLupasParcelas(final Long idClase);

	public String[] validaDatosIdent(final Long lineaseguroId, final Long idClase, final Long idPoliza,
			final String nifcif, final BigDecimal codcultivo, final BigDecimal codvariedad,
			final BigDecimal codprovincia, final BigDecimal codcomarca, final BigDecimal codtermino,
			final Character subtermino) throws BusinessException;
	
	public String[] validaDatosVariables(final Long lineaseguroid, final ParcelaVO parcelaVO) throws BusinessException;

	public PantallaConfigurableVO getPantallaConfigurableVO(final String modulos,
			final PantallaConfigurable pantallaConfigurable, final ParcelaVO parcela, final boolean calcularMascaras)
			throws BusinessException;

	public String[] guardarParcela(final ParcelaVO parcelaVO) throws BusinessException;
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Inicio */
	public List<ParcelasCoberturasNew> getCoberturasParcela(Parcela parcela, ParcelaVO parcelaVO, String codUsuario,
			String realPath, Set<ParcelaCobertura> cobParcExistentes, Long idCapAseg, Poliza plz)
			throws BusinessException;
	public boolean isCoberturasElegiblesNivelParcela(Long lineaseguroId, String codModulo);
	public boolean isCoberturasElegiblesNivelParcela(Long lineaseguroId, Set<ModuloPoliza> modsPoliza);
	public Parcela getDatParcela(Long idParcela);
	public Poliza getPoliza(Long idPoliza);
	public String[] actualizarParcelasCoberturas(ParcelaVO parcelaVO, String codUsuario, Long idPoliza) throws BusinessException;
	public boolean existInListConceptoCubiertoVO(final List<ConceptoCubiertoVO> listConceptosCubiertos,final ConceptoCubiertoModulo ccm);
	public ModulosYCoberturas getMyCFromXml(String xml);
	public boolean existInListRiesgos(final List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod, int codConceptoPpalMod, int codRiesgoCub);
	public boolean existInListConcepto(final List<ConceptoCubiertoVO> listConceptosCubiertos, String codConcepto);
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */
	
	public void borrarTC(final Long idcapitalasegurado) throws BusinessException;

	public CapitalAseguradoVO getCapitalAsegurado(final Long valueOf) throws BusinessException;
	
	public String getIdSiguienteParcela(final Long idParcelaOrigen, final Long idPoliza, final String listaIdsStr) throws BusinessException;
	
	public String getDescDatoVariable(final Long lineaseguroid, final String listCodModulos, final Integer codCpto,
			final String valor);
	
	public BigDecimal getCptoAsociadoTC(final BigDecimal codtipocapital) throws BusinessException;
}