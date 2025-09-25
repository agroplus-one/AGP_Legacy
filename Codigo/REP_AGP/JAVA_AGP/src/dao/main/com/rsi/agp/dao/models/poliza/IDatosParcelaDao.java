package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.SWModulosCoberturasParcela;

@SuppressWarnings("rawtypes")
public interface IDatosParcelaDao extends GenericDao {

	public List<CapitalAsegurado> getCapitalesAseguradoParcela(final Long codParcela) throws BusinessException;

	public List<String> getCodsModulosPoliza(final Long idPoliza) throws BusinessException;

	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(final Long lineaseguroid, final String codmodulo,
			final Character nivelEleccion);

	public List<ConfiguracionCampo> getListConfigCampos(final BigDecimal idpantalla) throws DAOException;

	public boolean dentroDeClaseDetalle(final Long idClase, final List<String> modulos, final BigDecimal codcultivo,
			final BigDecimal codvariedad, final BigDecimal codprovincia, final BigDecimal codcomarca,
			final BigDecimal codtermino, final Character subtermino) throws DAOException;

	public boolean dentroDeAmbitoAsegurable(final Long lineaseguroId, final BigDecimal codprovincia,
			final BigDecimal codcomarca, final BigDecimal codtermino, final Character subtermino) throws DAOException;

	public boolean dentroDeFechasContratacion(final Long lineaseguroId, final List<String> modulos,
			final BigDecimal codcultivo, final BigDecimal codvariedad, final BigDecimal codprovincia,
			final BigDecimal codcomarca, final BigDecimal codtermino, final Character subtermino,
			final CapitalAsegurado capitalAsegurado) throws DAOException;

	public Long getIdPantallaConfigurable(final Long lineaseguroid, final Long idPantalla);

	/*** MODIF TAM (10.12.2018) ESC-4627 ** Inicio ***/
	public DiccionarioDatos getDiccionarioDatosVarPar(final BigDecimal codConcepto) throws DAOException;
	/*** MODIF TAM (10.12.2018) ESC-4627 ** Fin ***/

	public List<BigDecimal> getConceptosRelacionados(final List<BigDecimal> listaMascaras);

	public List<BigDecimal> getConceptosObligatorios(final Long idPantallaConfigurable);

	public List<BigDecimal> getMascaraFCA(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos);

	public List<BigDecimal> getMascaraGT(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos);

	public List<BigDecimal> getMascaraLRDTO(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos);

	public List<BigDecimal> getMascaraP(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos);
	
	public String getDescDatoVariable(final Long lineaseguroid, final String listCodModulos, final Integer codCpto,
			final String valor);
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Inicio */
	public SWModulosCoberturasParcela saveEnvioCobParcela(SWModulosCoberturasParcela envio)	throws DAOException ;
	public void actualizaXmlCoberturasParc(Long idEnvio, final String xml, final String respuesta) throws DAOException;
	public boolean isCoberturasElegiblesNivelParcela(Long lineaSeguroId, String codModulos);
	public Parcela getDatosParcela(Long idParcela);
	void borrarRiesgosElegParcela(List<ParcelaCobertura> ListRiesgElegParcela);
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */

	public void copyParcelaCobertura(Long id, Long idparcela) throws DAOException;

	public boolean existInTbScSRiesgoCcbrtoMod(ParcelaCobertura cobertura) throws DAOException;
	public void copyElegibleCoberturas(Long idparcela) throws DAOException;

	public void actualizaParcelaCobertura(Long idcoberturaorigen, Long idparceladestino, Long lineaseguroid) throws DAOException;
	public void copyParcelaCobertura(Long idcobertura, Long idparcela, Long lineaseguroid) throws DAOException;
}
