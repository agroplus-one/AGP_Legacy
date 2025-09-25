package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.util.CollectionUtils;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IDatosParcelaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ConstantsParcelaDVs;
import com.rsi.agp.core.util.ParcelaCoberturaComparator;
import com.rsi.agp.core.util.ParcelaUtil;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.cpl.ConceptoCubiertoModuloFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloRiesgoSelecParcFiltro;
import com.rsi.agp.dao.filters.poliza.FechaFinGarantiasFiltro;
import com.rsi.agp.dao.filters.poliza.FechaRecoleccionFiltro;
import com.rsi.agp.dao.models.commons.ITerminoDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.ComarcaId;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SWModulosCoberturasParcela;
import com.rsi.agp.vo.CampoPantallaConfigurableVO;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.CaracteristicaRiesgoVO;
import com.rsi.agp.vo.ConceptoCubiertoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.DatosModulosRiesgosVO;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ModuloVO;
import com.rsi.agp.vo.ModulosVO;
import com.rsi.agp.vo.PantallaConfigurableVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.RiesgoVO;

import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturasDocument;

public class DatosParcelaManager implements IDatosParcelaManager {

	private final Log logger = LogFactory.getLog(DatosParcelaManager.class);

	private AjaxManager ajaxManager;
	private IDatosParcelaDao datosParcelaDao;
	private ITerminoDao terminoDao;
	private IPolizaDao polizaDao;

	private static final String ELEGIBLE = "ELEGIBLE";
	private static final String CON_CITRICOS = "conCitricos";

	public ParcelaVO getParcela(final Long idParcela) {
		return ParcelaUtil.getParcela(idParcela, this.datosParcelaDao);
	}

	public ParcelaVO getParcela(final Parcela parcela) throws BusinessException {
		return ParcelaUtil.getParcelaVO(parcela, this.datosParcelaDao);
	}

	@SuppressWarnings("unchecked")
	public void generateParcela(final ParcelaVO parcelaVO, final Parcela parcela, final Long idLineaSeguroId) {

		try {
			Poliza poliza = new Poliza();
			poliza.setIdpoliza(new Long(parcelaVO.getCodPoliza()));

			parcelaVO.getCodParcela();
			parcela.setPoliza(poliza);

			// -------------------------------------------------------------------------------------
			// --- Datos generales --
			// -------------------------------------------------------------------------------------
			parcela.setTipoparcela(parcelaVO.getTipoParcelaChar());
			if (Constants.TIPO_PARCELA_INSTALACION.equals(parcelaVO.getTipoParcelaChar())) {
				if ("sin valor".equals(parcelaVO.getRefIdParcela()) || !StringUtils.isNullOrEmpty(parcelaVO.getRefIdParcela())) {
					parcela.setIdparcelaestructura(new Long(parcelaVO.getRefIdParcela()));
				}
			}
			// Se fuerza a 1... los datos de hoja/numero reales se calculan mas adelante
			parcela.setHoja(1);
			parcela.setNumero(1);

			// Ubicacion
			Termino termino = new Termino();
			// provincia
			Provincia provincia = new Provincia();

			if (parcelaVO.getCodProvincia() != null && !parcelaVO.getCodProvincia().equals("")) {
				provincia.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
			}

			termino.setProvincia(provincia);
			// comarca
			Comarca comarca = new Comarca();
			ComarcaId comarcaId = new ComarcaId();

			if (parcelaVO.getCodComarca() != null && !parcelaVO.getCodComarca().equals("")) {
				comarcaId.setCodcomarca(new BigDecimal(parcelaVO.getCodComarca()));
			}

			if (parcelaVO.getCodProvincia() != null && !parcelaVO.getCodProvincia().equals("")) {
				comarcaId.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
			}

			comarca.setId(comarcaId);
			// termino
			termino.setComarca(comarca);
			TerminoId terminoId = new TerminoId();

			if (parcelaVO.getCodTermino() != null && !parcelaVO.getCodTermino().equals("")) {
				terminoId.setCodtermino(new BigDecimal(parcelaVO.getCodTermino()));
			}

			if (parcelaVO.getCodTermino() != null && !parcelaVO.getCodComarca().equals("")) {
				terminoId.setCodcomarca(new BigDecimal(parcelaVO.getCodComarca()));
			}

			if (parcelaVO.getCodSubTermino() != null || parcelaVO.getCodSubTermino().length() == 1) {
				if (parcelaVO.getCodSubTermino().equals("")) {
					terminoId.setSubtermino(" ".charAt(0));
				} else {
					terminoId.setSubtermino(parcelaVO.getCodSubTermino().charAt(0));
				}
			}

			if (parcelaVO.getCodProvincia() != null && !parcelaVO.getCodProvincia().equals("")) {
				terminoId.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
			}

			termino.setId(terminoId);
			parcela.setTermino(termino);

			if (parcelaVO.getProvinciaSigpac() != null && !parcelaVO.getProvinciaSigpac().equals(""))
				parcela.setCodprovsigpac(new BigDecimal(parcelaVO.getProvinciaSigpac()));
			if (parcelaVO.getTerminoSigpac() != null && !parcelaVO.getTerminoSigpac().equals(""))
				parcela.setCodtermsigpac(new BigDecimal(parcelaVO.getTerminoSigpac()));
			if (parcelaVO.getAgregadoSigpac() != null && !parcelaVO.getAgregadoSigpac().equals(""))
				parcela.setAgrsigpac(new BigDecimal(parcelaVO.getAgregadoSigpac()));
			if (parcelaVO.getZonaSigpac() != null && !parcelaVO.getZonaSigpac().equals(""))
				parcela.setZonasigpac(new BigDecimal(parcelaVO.getZonaSigpac()));
			if (parcelaVO.getPoligonoSigpac() != null && !parcelaVO.getPoligonoSigpac().equals(""))
				parcela.setPoligonosigpac(new BigDecimal(parcelaVO.getPoligonoSigpac()));
			if (parcelaVO.getParcelaSigpac() != null && !parcelaVO.getParcelaSigpac().equals(""))
				parcela.setParcelasigpac(new BigDecimal(parcelaVO.getParcelaSigpac()));
			if (parcelaVO.getRecintoSigpac() != null && !parcelaVO.getRecintoSigpac().equals(""))
				parcela.setRecintosigpac(new BigDecimal(parcelaVO.getRecintoSigpac()));
			// DAA 13/12/2012 Permite guardar el nombre de la parcela como "";
			if (parcelaVO.getNombreParcela() != null)
				parcela.setNomparcela(parcelaVO.getNombreParcela());
			if (parcelaVO.getCultivo() != null && !parcelaVO.getCultivo().equals(""))
				parcela.setCodcultivo(new BigDecimal(parcelaVO.getCultivo()));
			if (parcelaVO.getVariedad() != null && !parcelaVO.getVariedad().equals(""))
				parcela.setCodvariedad(new BigDecimal(parcelaVO.getVariedad()));

			// -------------------------------------------------------------------------------------
			// --- COBERTURAS ----
			// -------------------------------------------------------------------------------------
			// Obtengo TODOS los riesgos elegibles y los pongo como
			// no elegidos si la parcela no lo tiene ya
			List<ParcelaCobertura> riesgosElegiblesParcela = null;

			// Control para cuando se llama al WS estando en alta de parcela, ya que en ese
			// momento no tiene id
			if (parcela.getIdparcela() != null) {
				riesgosElegiblesParcela = datosParcelaDao.getObjects(ParcelaCobertura.class, "parcela.idparcela",
						parcela.getIdparcela());
			} else {
				riesgosElegiblesParcela = new ArrayList<ParcelaCobertura>();
			}

			if (riesgosElegiblesParcela.isEmpty()) {
				riesgosElegiblesParcela = getRiesgosElegibles(parcelaVO, idLineaSeguroId, parcela, "-2");

			} else {
				// Reseteamos los valores que tenia elegidos a "-2" (no elegidos) para que el
				// siguiente bulce asigne los elegidos.
				for (ParcelaCobertura pc : riesgosElegiblesParcela) {
					pc.setCodvalor(new BigDecimal(-2));
				}
			}

			// ----- VIENE DE LA UI (cargo la pestanha) -----Parche por si
			// riesgosSeleccionados viene a null
			if (parcelaVO.getRiesgosSeleccionados() != null && parcelaVO.getRiesgosSeleccionados().size() > 0) {
				// Pongo a -1 los riesgos elegibles que vienen de la UI a -1
				for (RiesgoVO riesgoVO : parcelaVO.getRiesgosSeleccionados()) {
					if (riesgoVO.getCodValor().equals("-1")) {
						// Lo busco y lo pongo a -1
						for (ParcelaCobertura parcelaCobertura : riesgosElegiblesParcela) {
							if (riesgoVO.getCodRiesgoCubierto().equals(
									parcelaCobertura.getRiesgoCubierto().getId().getCodriesgocubierto().toString())) {
								parcelaCobertura.setCodvalor(new BigDecimal("-1"));
							}
						}
					}
				}
			}
			// ----- NO VIENE DE LA UI (cargo la pestanha) ----- dejo el set como esta

			// Anhado la lista a la parcela
			parcela.setCoberturasParcela(new HashSet<ParcelaCobertura>(riesgosElegiblesParcela));

		} catch (Exception ex) {
			logger.error("[DatosParcelaFLManager -- generateParcela] Se ha producido un error al generar la parcela",
					ex);
		}
	}

	private List<ParcelaCobertura> getRiesgosElegibles(final ParcelaVO parcelaVO, final Long idLineaSeguroId,
			final Parcela parcela, final String isElegido) {

		List<ParcelaCobertura> coberturasParcela = new ArrayList<ParcelaCobertura>(0);

		try {
			// codConcepto; Para riesgos 363 --> elegido: -1, no elegido: -2, otros: valor

			DatosModulosRiesgosVO datosModulosRiesgosVO = new DatosModulosRiesgosVO();
			datosModulosRiesgosVO.setLineaSeguroId(idLineaSeguroId.toString());
			datosModulosRiesgosVO.setParcelaVO(parcelaVO);

			// ModulosVO modulosVO = getModulosRiesgosParcela(datosModulosRiesgosVO,
			// SIN_CITRICOS); // Todos los riesgos a -2
			ModulosVO modulosVO = getModulosRiesgosParcela(datosModulosRiesgosVO, CON_CITRICOS); // Todos los riesgos a
																									// // -2

			// Introduzco en la BD todos los RIESGOS/COBERTURAS como no seleccionados
			// Solo se introduccen los ELEGIBLES (como no elegidos)
			for (ModuloVO moduloVO : modulosVO.getModulos()) {
				for (RiesgoVO riesgoVO : moduloVO.getRiesgos()) {

					// Parcela cobertura
					ParcelaCobertura parcelaCobertura = new ParcelaCobertura();
					parcelaCobertura.setCodvalor(new BigDecimal(isElegido));
					// Concepto principal modulo
					ConceptoPpalModulo conceptoPpalModulo = new ConceptoPpalModulo();
					conceptoPpalModulo.setCodconceptoppalmod(new BigDecimal(riesgoVO.getCodConceptoPpalMod()));
					parcelaCobertura.setConceptoPpalModulo(conceptoPpalModulo);
					parcelaCobertura.setParcela(parcela);

					// riesgo cubierto
					RiesgoCubierto riesgoCubierto = new RiesgoCubierto();

					RiesgoCubiertoId riesgoCubiertoId = new RiesgoCubiertoId();
					riesgoCubiertoId.setCodriesgocubierto(new BigDecimal(riesgoVO.getCodRiesgoCubierto()));
					riesgoCubiertoId.setLineaseguroid(idLineaSeguroId);
					riesgoCubiertoId.setCodmodulo(riesgoVO.getCodModulo());

					riesgoCubierto.setId(riesgoCubiertoId);
					parcelaCobertura.setRiesgoCubierto(riesgoCubierto);
					// Diccionarion de datos
					DiccionarioDatos diccionarioDatos = new DiccionarioDatos();
					diccionarioDatos.setCodconcepto(new BigDecimal(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
					parcelaCobertura.setDiccionarioDatos(diccionarioDatos);

					coberturasParcela.add(parcelaCobertura); // ADD COBERTURA
				} // for coberturas
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error al recuperar las coberturas de la parcela", ex);
		}

		return coberturasParcela;
	}

	@SuppressWarnings("unchecked")
	private ModulosVO getModulosRiesgosParcela(final DatosModulosRiesgosVO datosModulosRiesgosVO,
			final String condicionCitricos) throws Exception {

		ModulosVO modulosVO = new ModulosVO();
		FechaFinGarantiasFiltro ffgFiltro = null;

		try {
			// Listado de modulos filtrados
			ModuloRiesgoSelecParcFiltro filtro = new ModuloRiesgoSelecParcFiltro(
					Long.parseLong(datosModulosRiesgosVO.getLineaSeguroId()), datosModulosRiesgosVO.getModulos());
			List<Modulo> modulos = this.datosParcelaDao.getObjects(filtro);

			// Coberturas - concepto - caracteristica
			// Usado para renderizar las columnas en un orden concreto
			List<ConceptoCubiertoVO> listConceptosCubiertos = new ArrayList<ConceptoCubiertoVO>();

			if (modulos.size() > 0) {

				ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(
						Long.parseLong(datosModulosRiesgosVO.getLineaSeguroId()), datosModulosRiesgosVO.getModulos());
				List<ConceptoCubiertoModulo> lstConcCbrtoMod = this.datosParcelaDao.getObjects(filtroConcCbrto);

				for (ConceptoCubiertoModulo ccm : lstConcCbrtoMod) {
					if (!existInListConceptoCubiertoVO(listConceptosCubiertos, ccm)) {
						ConceptoCubiertoVO cc = new ConceptoCubiertoVO();
						cc.setDesConcepto(ccm.getDiccionarioDatos().getNomconcepto()); // descripcion
						cc.setId(ccm.getDiccionarioDatos().getCodconcepto().toString()); // codigo
						cc.setNumeroColumna(Integer.parseInt(ccm.getId().getColumnamodulo().toString())); // columna
						listConceptosCubiertos.add(cc);
					}
				} // for

				Collections.sort(listConceptosCubiertos); // reordenar list conceptos cubiertos
				modulosVO.setListConceptosCubiertos(listConceptosCubiertos);
			} // if

			// ------------------------------- MODULOS -------------------------------
			for (Modulo modulo : modulos) {
				ModuloVO moduloVO = new ModuloVO();
				moduloVO.setCodModulo(modulo.getId().getCodmodulo());
				moduloVO.setDesModulo(modulo.getDesmodulo());

				// ---------------------------- RIESGOS -------------------------------
				// Se buscan riesgos cubiertos con nivel de eleccion 'D' para esta lineaa y
				// modulo
				List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = this.datosParcelaDao.getRiesgosCubiertosModulo(
						modulo.getId().getLineaseguroid(), modulo.getId().getCodmodulo(), new Character('D'));

				for (RiesgoCubiertoModulo riesgo : lstRiesgoCbrtoMod) {

					RiesgoVO riesgoVO = new RiesgoVO();
					String riesgoElegible = "";

					// --- LINEA = 301 (CITRICOS) ---
					if (riesgo.getModulo().getLinea().getCodlinea().toString().equals("301")
							&& condicionCitricos.equals(CON_CITRICOS)) {

						// APLICO EL FILTRO UNA VEZ POR CADA CAPITAL ASEGURADO
						if (datosModulosRiesgosVO.getParcelaVO() != null
								&& datosModulosRiesgosVO.getParcelaVO().getCapitalesAsegurados() != null) {
							for (int i = 0; i < datosModulosRiesgosVO.getParcelaVO().getCapitalesAsegurados()
									.size(); i++) {

								String ffGarantias = "";
								CapitalAseguradoVO cpVO = datosModulosRiesgosVO.getParcelaVO().getCapitalesAsegurados()
										.get(i);

								// Recorro los datos variables buscando: practica cultural (133) y
								// fecha fin de garantias (134)
								for (int e = 0; e < cpVO.getDatosVariablesParcela().size(); e++) {
									DatoVariableParcelaVO avpVO = cpVO.getDatosVariablesParcela().get(e);
									if (avpVO.getCodconcepto() == 134)
										ffGarantias = avpVO.getValor();
								}

								ffgFiltro = new FechaFinGarantiasFiltro(
										datosModulosRiesgosVO.getParcelaVO().getCultivo(),
										datosModulosRiesgosVO.getParcelaVO().getVariedad(), cpVO.getCodtipoCapital(),
										datosModulosRiesgosVO.getParcelaVO().getCodProvincia(),
										datosModulosRiesgosVO.getParcelaVO().getCodComarca(),
										datosModulosRiesgosVO.getParcelaVO().getCodTermino(),
										datosModulosRiesgosVO.getParcelaVO().getCodSubTermino(), ffGarantias,
										riesgo.getConceptoPpalModulo().getCodconceptoppalmod().toString(),
										riesgo.getRiesgoCubierto().getId().getCodriesgocubierto().toString(),
										modulo.getId().getCodmodulo(), modulo.getId().getLineaseguroid());

								List<FechaFinGarantia> ffgList = datosParcelaDao.getObjects(ffgFiltro);

								if (ffgList.size() > 0)
									riesgoElegible = ELEGIBLE;

							} // for
						} // if
					} // if 301
					else
						riesgoElegible = ELEGIBLE;

					// --- GARANTIA && RIESGOS CUBIERTOS ---
					riesgoVO.getCaracteristicasRiesgo().add(new CaracteristicaRiesgoVO("", "",
							riesgo.getConceptoPpalModulo().getDesconceptoppalmod(), -2));

					riesgoVO.getCaracteristicasRiesgo().add(new CaracteristicaRiesgoVO(riesgoElegible, "",
							riesgo.getRiesgoCubierto().getDesriesgocubierto(), -1));

					// VALOR (7 posibles valores)
					String capAseg = "", calculo = "", minIndem = "", tipoFranq = "", pctFranq = "", garant = "",
							tipoRend = "";

					String elegible = ""; // ELEGIBLE
					String observacion = ""; // OBSERVACION
					Integer columna = 9999; // COLUMNA

					// --------------------------- CARACTERISTICAS -------------------------------
					List<CaracteristicaModulo> lstCaractMod = this.datosParcelaDao
							.getObjects(CaracteristicaModulo.class, "riesgoCubiertoModulo.id", riesgo.getId());
					for (CaracteristicaModulo cm : lstCaractMod) {
						elegible = "";
						// Elegible
						if (cm.getTipovalor() == 'E')
							elegible = ELEGIBLE;

						// Observaciones
						if (cm.getObservaciones() != null && !cm.getObservaciones().equals(""))
							observacion = cm.getObservaciones();

						// Fila - Columna
						columna = Integer.parseInt(cm.getId().getColumnamodulo().toString());

						// --------------------- VINCULACION VALORES -----------------------------
						List<VinculacionValoresModulo> lstVincValMod = this.datosParcelaDao.getObjects(
								VinculacionValoresModulo.class, "caracteristicaModuloByFkVincValModCaracMod1.id",
								cm.getId());
						for (VinculacionValoresModulo vvm : lstVincValMod) {
							if (vvm.getCalculoIndemnizacionByCalcindemneleg() != null
									&& vvm.getCalculoIndemnizacionByCalcindemneleg().getDescalculo() != null) {
								calculo = vvm.getCalculoIndemnizacionByCalcindemneleg().getDescalculo();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, calculo, columna));
							} else if (vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg() != null && vvm
									.getCapitalAseguradoElegibleByPctcapitalasegeleg().getDescapitalaseg() != null) {
								capAseg = vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg().getDescapitalaseg();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, capAseg, columna));
							} else if (vvm.getGarantizadoByGarantizadoeleg() != null
									&& vvm.getGarantizadoByGarantizadoeleg().getDesgarantizado() != null) {
								garant = vvm.getGarantizadoByGarantizadoeleg().getDesgarantizado();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, garant, columna));
							} else if (vvm.getMinimoIndemnizableElegibleByPctminindemneleg() != null
									&& vvm.getMinimoIndemnizableElegibleByPctminindemneleg().getDesminindem() != null) {
								minIndem = vvm.getMinimoIndemnizableElegibleByPctminindemneleg().getDesminindem();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, minIndem, columna));
							} else if (vvm.getPctFranquiciaElegibleByCodpctfranquiciaeleg() != null
									&& vvm.getPctFranquiciaElegibleByCodpctfranquiciaeleg()
											.getDespctfranquiciaeleg() != null) {
								pctFranq = vvm.getPctFranquiciaElegibleByCodpctfranquiciaeleg()
										.getDespctfranquiciaeleg();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, pctFranq, columna));
							} else if (vvm.getTipoFranquiciaByTipofranquiciaeleg() != null
									&& vvm.getTipoFranquiciaByTipofranquiciaeleg().getDestipofranquicia() != null) {
								tipoFranq = vvm.getTipoFranquiciaByTipofranquiciaeleg().getDestipofranquicia();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, tipoFranq, columna));
							} else if (vvm.getTipoRendimientoByCodtipordtoeleg() != null
									&& vvm.getTipoRendimientoByCodtipordtoeleg().getDestipordto() != null) {
								tipoRend = vvm.getTipoRendimientoByCodtipordtoeleg().getDestipordto();
								riesgoVO.getCaracteristicasRiesgo()
										.add(new CaracteristicaRiesgoVO(elegible, observacion, tipoRend, columna));
							}
						} // for
					} // for

					riesgoVO.setCodConceptoPpalMod(riesgo.getConceptoPpalModulo().getCodconceptoppalmod().toString());
					riesgoVO.setCodRiesgoCubierto(riesgo.getRiesgoCubierto().getId().getCodriesgocubierto().toString());
					riesgoVO.setCodValor("-1");
					riesgoVO.setCodModulo(riesgo.getRiesgoCubierto().getModulo().getId().getCodmodulo().toString());
					riesgoVO.setLineaSeguroId(
							riesgo.getRiesgoCubierto().getModulo().getId().getLineaseguroid().toString());
					riesgoVO.setCodConcepto(String.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));

					if (riesgoElegible.equals(ELEGIBLE))
						moduloVO.getRiesgos().add(riesgoVO); // Anhado el nuevo riesgo al modulo

					// Reordenar, antes de anhadir el modulo, todos CaracteristicaRiesgoVO de los
					// riesgos
					// del nuevo modulo. Se hace esto porque en la vista deben renderizarse en
					// orden.
					for (int i = 0; i < moduloVO.getRiesgos().size(); i++)
						moduloVO.getRiesgos().get(i).setCaracteristicasRiesgo(reordenarCaracteristicaRiesgoVO(
								moduloVO.getRiesgos().get(i).getCaracteristicasRiesgo()));

				} // for riesgos

				// Solo se anhade el nuevo modulo si tiene algun riesgo
				if (moduloVO.getRiesgos().size() > 0)
					modulosVO.getModulos().add(moduloVO);

			} // for modulos
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar los modulos y sus riesgos seleccionables", excepcion);
			throw excepcion;
		}

		return modulosVO;
	}

	public boolean existInListConceptoCubiertoVO(final List<ConceptoCubiertoVO> listConceptosCubiertos,
			final ConceptoCubiertoModulo ccm) {
		boolean result = false;
		if (listConceptosCubiertos.size() > 0 && ccm != null) {
			for (ConceptoCubiertoVO conceptoCubiertoVO : listConceptosCubiertos) {
				if (conceptoCubiertoVO.getId().equals(ccm.getDiccionarioDatos().getCodconcepto().toString()))
					result = true;
			}
		}
		return result;
	}

	private ArrayList<CaracteristicaRiesgoVO> reordenarCaracteristicaRiesgoVO(
			final ArrayList<CaracteristicaRiesgoVO> listaOriginal) {
		Boolean enco = false;
		ArrayList<CaracteristicaRiesgoVO> listAux = new ArrayList<CaracteristicaRiesgoVO>();
		// reordeno
		Collections.sort(listaOriginal);
		// quitar repetidos
		for (int i = 0; i < listaOriginal.size(); i++) {
			enco = false;
			CaracteristicaRiesgoVO aa = listaOriginal.get(i);
			for (CaracteristicaRiesgoVO bb : listAux) { // lo busco en la nueva lista
				if (bb.getNumColumna().equals(aa.getNumColumna())) {
					enco = true;
					break;
				}
			}
			if (!enco) {
				listAux.add(aa);
			}
		}
		return listAux;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getListaCodigosLupasParcelas(final Long idClase) {
		final Integer TODOS_99 = new Integer(99);
		final Integer TODOS_999 = new Integer(999);
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Integer> codProvincias = new ArrayList<Integer>();
		List<Integer> codComarcas = new ArrayList<Integer>();
		List<Integer> codTerminos = new ArrayList<Integer>();
		List<Character> codSubterminos = new ArrayList<Character>();
		List<Integer> codCultivos = new ArrayList<Integer>();
		List<Integer> codVariedades = new ArrayList<Integer>();
		List<Integer> codTiposCapital = new ArrayList<Integer>();
		// FILTRAMOS POR CLASE
		List<ClaseDetalle> detalles = this.datosParcelaDao.getObjects(ClaseDetalle.class, "clase.id", idClase);
		if (!CollectionUtils.isEmpty(detalles)) {
			for (ClaseDetalle detalle : detalles) {
				// PROVINCIA
				Integer codProvincia = detalle.getCodprovincia().intValue();
				if (!codProvincias.contains(codProvincia))
					codProvincias.add(codProvincia);
				// COMARCA
				Integer codComarca = detalle.getCodcomarca().intValue();
				if (!codComarcas.contains(codComarca))
					codComarcas.add(codComarca);
				// TERMINO
				Integer codTermino = detalle.getCodtermino().intValue();
				if (!codTerminos.contains(codTermino))
					codTerminos.add(codTermino);
				// SUBTERMINO
				Character codSubtermino = detalle.getSubtermino();
				if (!codSubterminos.contains(codSubtermino))
					codSubterminos.add(codSubtermino);
				// CULTIVO
				Integer codCultivo = detalle.getCultivo().getId().getCodcultivo().intValue();
				if (!codCultivos.contains(codCultivo))
					codCultivos.add(codCultivo);
				// VARIEDAD
				Integer codVariedad = detalle.getVariedad().getId().getCodvariedad().intValue();
				if (!codVariedades.contains(codVariedad))
					codVariedades.add(codVariedad);
				// TIPO CAPITAL
				Integer codTipoCapital = detalle.getTipoCapital() == null ? null
						: detalle.getTipoCapital().getCodtipocapital().intValue();
				if (codTipoCapital != null && !codTiposCapital.contains(codTipoCapital))
					codTiposCapital.add(codTipoCapital);
			}
			// Borramos las listas que contengan una clave 9, 99 , 999 "TODOS"
			limpiaListaConGenericos(codProvincias, TODOS_99);
			limpiaListaConGenericos(codComarcas, TODOS_99);
			limpiaListaConGenericos(codTerminos, TODOS_999);
			limpiaListaConGenericos(codCultivos, TODOS_999);
			limpiaListaConGenericos(codVariedades, TODOS_999);
			limpiaListaConGenericos(codTiposCapital, TODOS_999);
		}

		// Anhadimos las listas al mapa de parametros para la jsp
		if (codProvincias.size() > 0)
			parametros.put("listaCodProvincias", StringUtils.toValoresSeparadosXComas(codProvincias, false));
		if (codComarcas.size() > 0)
			parametros.put("listaCodComarcas", StringUtils.toValoresSeparadosXComas(codComarcas, false));
		if (codTerminos.size() > 0)
			parametros.put("listaCodTerminos", StringUtils.toValoresSeparadosXComas(codTerminos, false));
		if (codCultivos.size() > 0)
			parametros.put("listaCodCultivos", StringUtils.toValoresSeparadosXComas(codCultivos, false));
		if (codVariedades.size() > 0)
			parametros.put("listaCodVariedades", StringUtils.toValoresSeparadosXComas(codVariedades, false));
		if (codTiposCapital.size() > 0)
			parametros.put("listaTiposCapital", StringUtils.toValoresSeparadosXComas(codTiposCapital, false));
		return parametros;
	}

	private void limpiaListaConGenericos(final List<Integer> list, final Integer valorTodos) {
		if (list.contains(valorTodos))
			list.clear();
	}

	@Override
	public String[] validaDatosIdent(final Long lineaseguroId, final Long idClase, final Long idPoliza,
			final String nifcif, final BigDecimal codcultivo, final BigDecimal codvariedad,
			final BigDecimal codprovincia, final BigDecimal codcomarca, final BigDecimal codtermino,
			final Character subtermino) throws BusinessException {
		List<String> errorMsgs = new ArrayList<String>();
		List<String> modulos = this.datosParcelaDao.getCodsModulosPoliza(idPoliza);
		try {
			// VALIDACION CULTIVO/VARIEDAD
			VariedadId variedadId = new VariedadId(lineaseguroId, codcultivo, codvariedad);
			Variedad variedad = (Variedad) this.datosParcelaDao.get(Variedad.class, variedadId);
			if (variedad == null) {
				errorMsgs.add("El cultivo/variedad de la parcela no es v&aacute;lido.");
			}
			// VALIDACION UBICACION
			TerminoId terminoId = new TerminoId(codprovincia, codtermino, subtermino, codcomarca);
			Termino termino = (Termino) this.datosParcelaDao.get(Termino.class, terminoId);
			if (termino == null) {
				errorMsgs.add("La ubicaci&oacute;n de la parcela no es v&aacute;lida.");
			}
			if (errorMsgs.isEmpty()) {
				// VALIDACION POR AMBITO ASEGURABLE
				if (!this.datosParcelaDao.dentroDeAmbitoAsegurable(lineaseguroId, codprovincia, codcomarca, codtermino,
						subtermino)) {
					errorMsgs.add(
							"Los datos introducidos en la ubicacion de la parcela no est&aacute;n dentro de un &aacute;mbito asegurable.");
				}
				// VALIDACION POR CLASE
				else if (!this.datosParcelaDao.dentroDeClaseDetalle(idClase, modulos, codcultivo, codvariedad,
						codprovincia, codcomarca, codtermino, subtermino)) {
					errorMsgs.add(
							"Los datos introducidos en la parcela no est&aacute;n permitidos para la clase seleccionada en la p&oacute;liza.");
				}
			}
		} catch (Exception e) {
			logger.error("Error en la validacion de datos identificativos de parcela: " + e.getMessage());
			throw new BusinessException(e);
		}
		return errorMsgs.toArray(new String[] {});
	}

	@Override
	public String[] validaDatosVariables(final Long lineaseguroid, final ParcelaVO parcelaVO) throws BusinessException {
		List<String> errorMsgs = new ArrayList<String>();
		List<DatoVariableParcelaVO> datosVariables = parcelaVO.getCapitalAsegurado().getDatosVariablesParcela();
		try {
			for (DatoVariableParcelaVO dv : datosVariables) {
				List<?> result = null;
				String field = "";
				switch (dv.getCodconcepto().intValue()) {
				case ConstantsConceptos.CODCPTO_FEC_FIN_GARANT:
					field = "Fecha Final de Garant&iacute;a";
					FechaFinGarantiasFiltro ffgf = new FechaFinGarantiasFiltro(parcelaVO.getCultivo(),
							parcelaVO.getVariedad(), parcelaVO.getCapitalAsegurado().getCodtipoCapital(),
							parcelaVO.getCodProvincia(), parcelaVO.getCodComarca(), parcelaVO.getCodTermino(),
							parcelaVO.getCodSubTermino(), dv.getValor(), null, null, null, lineaseguroid);
					result = this.datosParcelaDao.getObjects(ffgf);
					break;
				case ConstantsConceptos.CODCPTO_FECHA_RECOLEC:
					field = "Fecha de Recolecci&oacute;n";
					FechaRecoleccionFiltro frf = new FechaRecoleccionFiltro(parcelaVO.getCultivo(),
							parcelaVO.getVariedad(), parcelaVO.getCapitalAsegurado().getCodtipoCapital(),
							parcelaVO.getCodProvincia(), parcelaVO.getCodComarca(), parcelaVO.getCodTermino(),
							parcelaVO.getCodSubTermino(), dv.getValor(), null, lineaseguroid);
					result = this.datosParcelaDao.getObjects(frf);
					break;
				case ConstantsConceptos.CODCPTO_FECSIEMBRA:
					field = "Fecha de Siembra/Transplante";
					break;
				default:
					break;
				}
				if (result != null && result.isEmpty()) {
					errorMsgs.add("Valor incorrecto en el campo '" + field + "'.");
				}
			}
		} catch (Exception e) {
			logger.error("Error en la validacion de datos variables de parcela: " + e.getMessage());
			throw new BusinessException(e);
		}
		return errorMsgs.toArray(new String[] {});
	}

	public PantallaConfigurableVO getPantallaConfigurableVO(final String modulos,
			final PantallaConfigurable pantallaConfigurable, final ParcelaVO parcela, final boolean calcularMascaras)
			throws BusinessException {
		PantallaConfigurableVO pantallaConfigurableVO = new PantallaConfigurableVO();
		try {
			if (pantallaConfigurable != null) {
				pantallaConfigurableVO.setIdPantalla(pantallaConfigurable.getPantalla().getIdpantalla().intValue());
				pantallaConfigurableVO
						.setIdPantallaConfigurable(pantallaConfigurable.getIdpantallaconfigurable().intValue());
				// Modulos de la poliza
				String[] modulosArr = modulos.split(",");
				List<String> modulosLst = new ArrayList<String>(modulosArr.length + 1);
				modulosLst.addAll(Arrays.asList(modulosArr));
				modulosLst.add("99999");
				// lista de todos los codconceptos que se mostraran
				List<BigDecimal> mascara = new ArrayList<BigDecimal>();
				if (calcularMascaras) {
					// Codconceptos filtrados por las distintas mascaras.
					Long lineaseguroid = pantallaConfigurable.getLinea().getLineaseguroid();
					BigDecimal codCultivo = StringUtils.isNullOrEmpty(parcela.getCultivo()) ? null
							: new BigDecimal(parcela.getCultivo());
					BigDecimal codVariedad = StringUtils.isNullOrEmpty(parcela.getVariedad()) ? null
							: new BigDecimal(parcela.getVariedad());
					BigDecimal codProvincia = StringUtils.isNullOrEmpty(parcela.getCodProvincia()) ? null
							: new BigDecimal(parcela.getCodProvincia());
					BigDecimal codComarca = StringUtils.isNullOrEmpty(parcela.getCodComarca()) ? null
							: new BigDecimal(parcela.getCodComarca());
					BigDecimal codTermino = StringUtils.isNullOrEmpty(parcela.getCodTermino()) ? null
							: new BigDecimal(parcela.getCodTermino());
					Character subTermino = StringUtils.isNullOrEmpty(parcela.getCodSubTermino()) ? null
							: parcela.getCodTermino().charAt(0);
					List<BigDecimal> listaMFCA = this.datosParcelaDao.getMascaraFCA(lineaseguroid, codCultivo,
							codVariedad, codProvincia, codComarca, codTermino, subTermino, modulosLst);
					List<BigDecimal> listaMGT = this.datosParcelaDao.getMascaraGT(lineaseguroid, codCultivo,
							codVariedad, codProvincia, codComarca, codTermino, subTermino, modulosLst);
					List<BigDecimal> listaMLR;
					if (Constants.TIPO_PARCELA_PARCELA.equals(parcela.getTipoParcelaChar())) {
						listaMLR = this.datosParcelaDao.getMascaraLRDTO(lineaseguroid, codCultivo, codVariedad,
								codProvincia, codComarca, codTermino, subTermino, modulosLst);
					} else {
						listaMLR = new ArrayList<BigDecimal>(0);
					}
					List<BigDecimal> listaMP = this.datosParcelaDao.getMascaraP(lineaseguroid, codCultivo, codVariedad,
							codProvincia, codComarca, codTermino, subTermino, modulosLst);
					mascara.addAll(listaMFCA);
					mascara.addAll(listaMGT);
					mascara.addAll(listaMLR);
					mascara.addAll(listaMP);
					// Codconceptos campos relacionados (Campos Mascara)
					List<BigDecimal> listaCampos = this.datosParcelaDao.getConceptosRelacionados(mascara);
					mascara.addAll(listaCampos);
					// Codconceptos que son obligatorios aunque los filtre la mascara
					List<BigDecimal> listaOblig = this.datosParcelaDao
							.getConceptosObligatorios(pantallaConfigurable.getIdpantallaconfigurable());
					mascara.addAll(listaOblig);
				}
				// Eliminamos posibles duplicados
				List<BigDecimal> newList = new ArrayList<BigDecimal>(mascara.size());
				for (BigDecimal value : mascara) {
					if (!newList.contains(value)) {
						newList.add(value);
					}
				}
				pantallaConfigurableVO.setListCodConceptosMascaras(newList);
				List<ConfiguracionCampo> listCampos = this.datosParcelaDao
						.getListConfigCampos(BigDecimal.valueOf(pantallaConfigurable.getIdpantallaconfigurable()));
				// Forzamos a deshabilitado los campos que no esten en mascaras
				for (ConfiguracionCampo campo : listCampos) {
					if (!pantallaConfigurableVO.getListCodConceptosMascaras().contains(campo.getId().getCodconcepto())
							&& !campo.getMostrarsiempre().equals(Constants.CHARACTER_S)) {
						campo.setDisabled(Constants.CHARACTER_S);
					}
				}
				pantallaConfigurableVO.setListCampos(getListPantallaConfigurableVO(listCampos, modulos));
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error al recuperar los datos de la pantalla configurable "
					+ pantallaConfigurable.getLinea().getCodlinea() + "/" + pantallaConfigurable.getLinea().getCodplan()
					+ " - idpantalla = " + pantallaConfigurable.getPantalla().getIdpantalla());
			throw new BusinessException(e);
		}
		return pantallaConfigurableVO;
	}

	private List<CampoPantallaConfigurableVO> getListPantallaConfigurableVO(
			final List<ConfiguracionCampo> listPantallas, final String modulos) throws BusinessException {
		List<CampoPantallaConfigurableVO> listConfiguracionCampo = new ArrayList<CampoPantallaConfigurableVO>();
		for (ConfiguracionCampo configuracionCampo : listPantallas) {
			CampoPantallaConfigurableVO campoPantallaConfigurableVO = new CampoPantallaConfigurableVO();
			campoPantallaConfigurableVO.setAlto(configuracionCampo.getAlto().intValue());
			campoPantallaConfigurableVO.setAncho(configuracionCampo.getAncho().intValue());
			campoPantallaConfigurableVO.setX(configuracionCampo.getX().intValue());
			campoPantallaConfigurableVO.setY(configuracionCampo.getY().intValue());
			campoPantallaConfigurableVO.setEtiqueta(WordUtils.capitalizeFully(configuracionCampo.getEtiqueta()));
			campoPantallaConfigurableVO.setIdtipo(configuracionCampo.getTipoCampo().getIdtipo().intValue());
			campoPantallaConfigurableVO.setDescripcion_tipo(configuracionCampo.getTipoCampo().getDesctipo());
			if (Constants.CHARACTER_S.equals(configuracionCampo.getMostrarsiempre())) {
				campoPantallaConfigurableVO.setMostrar(Constants.CHARACTER_S.toString());
			} else {
				campoPantallaConfigurableVO.setMostrar(Constants.CHARACTER_N.toString());
			}
			if (Constants.CHARACTER_S.equals(configuracionCampo.getDisabled())) {
				campoPantallaConfigurableVO.setDeshabilitado(Constants.CHARACTER_S.toString());
			} else {
				campoPantallaConfigurableVO.setDeshabilitado(Constants.CHARACTER_N.toString());
			}
			campoPantallaConfigurableVO.setTamanio(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getLongitud().intValue());
			campoPantallaConfigurableVO.setCodConcepto(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getCodconcepto().intValue());
			campoPantallaConfigurableVO
					.setDescripcion(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());
			if (configuracionCampo.getOrigenDatos() != null) {
				campoPantallaConfigurableVO
						.setIdorigendedatos(configuracionCampo.getOrigenDatos().getIdorigendatos().intValue());
				campoPantallaConfigurableVO.setTabla_asociada(configuracionCampo.getOrigenDatos().getSql());
			}
			campoPantallaConfigurableVO
					.setIdpantallaconfigurable(configuracionCampo.getId().getIdpantallaconfigurable().intValue());
			campoPantallaConfigurableVO.setIdseccion(configuracionCampo.getSeccion().getIdseccion().intValue());
			campoPantallaConfigurableVO
					.setNombre(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());
			campoPantallaConfigurableVO.setUbicacion_codigo(
					configuracionCampo.getOrganizadorInformacion().getUbicacion().getCodubicacion().intValue());
			campoPantallaConfigurableVO.setUbicacion_descripcion(
					configuracionCampo.getOrganizadorInformacion().getUbicacion().getDesubicacion());
			campoPantallaConfigurableVO.setCodTipoNaturaleza(configuracionCampo.getOrganizadorInformacion()
					.getDiccionarioDatos().getTipoNaturaleza().getCodtiponaturaleza().intValue());
			campoPantallaConfigurableVO.setDecimales(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getDecimales().intValue());
			// PARA CAMPOS DE TIPO 5-SELECT O 7-MULTICHECK
			// OBTENEMOS LOS POSIBLES VALORES
			if (ArrayUtils.contains(new int[] { 5, 7 }, campoPantallaConfigurableVO.getIdtipo())) {
				Map<String, Object> fieldMap = ConstantsParcelaDVs.MAP_DV_TIPO_5_7
						.get(campoPantallaConfigurableVO.getCodConcepto());
				// SIEMPRE SE FILTRARA POR LINEA SEGURO Y MODULOS SELECCIONADOS
				// EL SIGUIENTE CODIGO ES ESPECIFICO PARA EL ORIGEN DE DATOS DE MEDIDAS
				// PREVENTIVAS
				// QUE ES EL UNICO CAMPO DE TIPO 5 O 7 EXISTENTE
				JSONObject jsonObj = this.ajaxManager.getData((String) fieldMap.get(ConstantsParcelaDVs.FIELD_CLASS), null,
						new String[] {}, new String[] {}, new String[] {},
						(String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_SHOW),
						new String[] { configuracionCampo.getId().getLineaseguroid().toString() },
						(String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_FILTER),
						(String[]) fieldMap.get(ConstantsParcelaDVs.FILTER_TYPES), null,
						(String) fieldMap.get(ConstantsParcelaDVs.FIELD_TO_ORDER), "ASC", null,
						(String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_DISTINCT), new String[] {},
						new String[] {}, (String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_FILTER_IN),
						new String[] { modulos }, new String[] { "in" },
						(String[]) fieldMap.get(ConstantsParcelaDVs.FILTER_TYPES_IN), new String[] {}, new String[] {},
						Constants.STRING_N);
				try {
					JSONArray jsonArr = (JSONArray) jsonObj.get("lista");
					ItemVO[] valores = new ItemVO[jsonArr.length()];
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject item = jsonArr.getJSONObject(i);
						ItemVO valor = new ItemVO();
						valor.setCodigo(
								item.getString(((String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_DISTINCT))[0]));
						valor.setDescripcion(
								item.getString(((String[]) fieldMap.get(ConstantsParcelaDVs.FIELDS_TO_DISTINCT))[1]));
						valores[i] = valor;
					}
					campoPantallaConfigurableVO.setValores(valores);
				} catch (JSONException e) {
					throw new BusinessException(e);
				}
			}
			listConfiguracionCampo.add(campoPantallaConfigurableVO);
		}
		return listConfiguracionCampo;
	}

	@Override
	// METODO QUE SIRVE PARA GUARDAR UNA PARCELA COMPLETA O UNICAMENTE UN CAPITAL
	// ASEGURADO TANTO EN ALTA COMO EN EDICION
	public String[] guardarParcela(final ParcelaVO parcelaVO) throws BusinessException {

		logger.debug("DatosParcelaManager-guardarParcela[INIT]");
		CapitalAsegurado capAseg;
		List<String> errorMsgs = new ArrayList<String>();
		CapitalAseguradoVO capAsegVO = parcelaVO.getCapitalAsegurado();
		try {
			// SI VIENE EL IDENTIFICADOR DEL CAPITAL ASEGURADO
			// HAY QUE TRAER EL CAPITAL ASEGURADO DE BBDD
			// SI NO LO CONSTRUIMOS DESDE CERO
			if (StringUtils.isNullOrEmpty(capAsegVO.getId())) {
				capAseg = new CapitalAsegurado();
				// EN ESTE CASO HAY QUE VALIDAR LA POSIBLE DUPLICIDAD
				// DE TC O DATOS IDENTIFICATIVOS DE PARCELA
				errorMsgs = verificarDuplicidad(parcelaVO);
				// SI VIENE EL IDENTIFICADOR DE LA PARCELA
				// HAY QUE TRAER LA PARCELA DE BBDD
				// SI NO LA CONSTRUIMOS DESDE CERO
				if (errorMsgs.isEmpty() && !StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())) {
					capAseg.setParcela(
							(Parcela) this.datosParcelaDao.get(Parcela.class, Long.valueOf(parcelaVO.getCodParcela())));
				}
			} else {
				capAseg = (CapitalAsegurado) this.datosParcelaDao.get(CapitalAsegurado.class,
						Long.valueOf(parcelaVO.getCapitalAsegurado().getId()));
			}
			if (errorMsgs.isEmpty()) {
				Poliza poliza = (Poliza) this.datosParcelaDao.get(Poliza.class, Long.valueOf(parcelaVO.getCodPoliza()));
				// ACTUALIZAMOS CON LOS DATOS DEL VO
				// DATOS DE PARCELA
				Parcela parcela = capAseg.getParcela();
				parcela.setPoliza(poliza);
				parcela.setIndRecalculoHojaNumero(Integer.valueOf(1));
				parcela.setTipoparcela(parcelaVO.getTipoParcelaChar());
				parcela.setNomparcela(parcelaVO.getNombreParcela());
				parcela.setCodcultivo(new BigDecimal(parcelaVO.getCultivo()));
				parcela.setCodvariedad(new BigDecimal(parcelaVO.getVariedad()));
				parcela.setVariedad((Variedad) this.datosParcelaDao.get(Variedad.class, new VariedadId(
						poliza.getLinea().getLineaseguroid(), parcela.getCodcultivo(), parcela.getCodvariedad())));
				parcela.setCodprovsigpac(new BigDecimal(parcelaVO.getProvinciaSigpac()));
				parcela.setCodtermsigpac(new BigDecimal(parcelaVO.getTerminoSigpac()));
				parcela.setAgrsigpac(new BigDecimal(parcelaVO.getAgregadoSigpac()));
				parcela.setZonasigpac(new BigDecimal(parcelaVO.getZonaSigpac()));
				parcela.setPoligonosigpac(new BigDecimal(parcelaVO.getPoligonoSigpac()));
				parcela.setParcelasigpac(new BigDecimal(parcelaVO.getParcelaSigpac()));
				parcela.setRecintosigpac(new BigDecimal(parcelaVO.getRecintoSigpac()));
				parcela.setTermino(this.terminoDao.getTermino(new BigDecimal(parcelaVO.getCodProvincia()),
						new BigDecimal(parcelaVO.getCodComarca()), new BigDecimal(parcelaVO.getCodTermino()),
						parcelaVO.getCodSubTermino().charAt(0)));

				// GUARDAMOS LA PARCELA
				parcela = (Parcela) this.datosParcelaDao.saveOrUpdate(parcela);
				capAseg.setParcela(parcela);
				parcelaVO.setCodParcela(parcela.getIdparcela().toString());
				// DATOS DE CAPITAL ASEGURADO
				capAseg.setTipoCapital((TipoCapital) this.datosParcelaDao.get(TipoCapital.class,
						new BigDecimal(capAsegVO.getCodtipoCapital())));
				capAseg.setSuperficie(StringUtils.isNullOrEmpty(capAsegVO.getSuperficie()) ? BigDecimal.ZERO
						: new BigDecimal(capAsegVO.getSuperficie()));
				capAseg.setProduccion(StringUtils.isNullOrEmpty(capAsegVO.getProduccion()) ? BigDecimal.ZERO
						: new BigDecimal(capAsegVO.getProduccion()));
				capAseg.setPrecio(new BigDecimal(capAsegVO.getPrecio()));
				if (!StringUtils.isNullOrEmpty(capAsegVO.getId())) {
					@SuppressWarnings("unchecked")
					List<DatoVariableParcela> objs1 = this.datosParcelaDao.getObjects(DatoVariableParcela.class,
							"capitalAsegurado.idcapitalasegurado", Long.valueOf(capAsegVO.getId()));
					for (DatoVariableParcela obj : objs1) {
						this.datosParcelaDao.delete(obj);
					}
					@SuppressWarnings("unchecked")
					List<CapAsegRelModulo> objs2 = this.datosParcelaDao.getObjects(CapAsegRelModulo.class,
							"capitalAsegurado.idcapitalasegurado", Long.valueOf(capAsegVO.getId()));
					for (CapAsegRelModulo obj : objs2) {
						this.datosParcelaDao.delete(obj);
					}
				}
				// DATOS VARIABLES DE PARCELA
				List<DatoVariableParcelaVO> dvVOs = capAsegVO.getDatosVariablesParcela();
				Set<DatoVariableParcela> datosVarPar = new HashSet<DatoVariableParcela>(dvVOs.size());
				for (DatoVariableParcelaVO dvVO : dvVOs) {
					if (!StringUtils.isNullOrEmpty(dvVO.getValor())) {
						DatoVariableParcela dv = new DatoVariableParcela();
						dv.setCapitalAsegurado(capAseg);
						dv.setDiccionarioDatos((DiccionarioDatos) this.datosParcelaDao.get(DiccionarioDatos.class,
								BigDecimal.valueOf(dvVO.getCodconcepto())));
						dv.setValor(dvVO.getValor());
						datosVarPar.add(dv);
					}
				}
				capAseg.setDatoVariableParcelas(datosVarPar);
				// DATOS DE PARCELA POR MODULO
				Set<ModuloPoliza> modulos = poliza.getModuloPolizas();
				Set<CapAsegRelModulo> caRelMods = new HashSet<CapAsegRelModulo>(modulos.size());
				for (ModuloPoliza modulo : modulos) {
					
					/* ESC-13247 ** MODIF TAM (30.03.2021) ** Inicio*/
					/* Por si se ha seleccionado alguna comparativa del mismo modulo
					 * para que no se graben 2 registros del mismo modulo
					 */
					boolean encontrado = false;
					if (caRelMods.size() > 0) {
						for (CapAsegRelModulo capAsegMod: caRelMods) {
							if (modulo.getId().getCodmodulo().equals(capAsegMod.getCodmodulo())){
								encontrado = true;
								break;
							}
						}
					}
					
					if (encontrado == false) {
						CapAsegRelModulo capAsegRelModulo = new CapAsegRelModulo();
						capAsegRelModulo.setCapitalAsegurado(capAseg);
						capAsegRelModulo.setCodmodulo(modulo.getId().getCodmodulo());
						capAsegRelModulo.setPrecio(new BigDecimal(capAsegVO.getPrecio()));
						capAsegRelModulo.setProduccion(new BigDecimal(capAsegVO.getProduccion()));
						capAsegRelModulo.setTipoRdto(capAsegRelModulo.getProduccion().equals(BigDecimal.ZERO)
								? Constants.TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO
										: Constants.TIPO_RDTO_MAXIMO);
						caRelMods.add(capAsegRelModulo);
						
					}
						
				}
				capAseg.setCapAsegRelModulos(caRelMods);
				// GUARDAMOS EL CAPITAL ASEGURADO
				capAseg = (CapitalAsegurado) this.datosParcelaDao.saveOrUpdate(capAseg);
				capAsegVO.setId(capAseg.getIdcapitalasegurado().toString());
				// PASAMOS AL VO LAS DESCRIPCIONES DE LOS CAMPOS QUE SE HAN PODIDO
				// INTRODUCIR DE FORMA MANUAL (MENOS LOS CAMPOS VARIABLES)
				parcelaVO.setDesCultivo(parcela.getVariedad().getCultivo().getDescultivo());
				parcelaVO.setDesVariedad(parcela.getVariedad().getDesvariedad());
				parcelaVO.setDesProvincia(parcela.getTermino().getComarca().getProvincia().getNomprovincia());
				parcelaVO.setDesComarca(parcela.getTermino().getComarca().getNomcomarca());
				
				// Obtenemos la fecha de fin de contrataci�n.
				Date fechaInicioContratacion = poliza.getLinea().getFechaInicioContratacion();
				// Utiliza el m�todo getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
				// Esta versi�n ahora tiene en cuenta la fecha de inicio de contrataci�n y si la l�nea es de ganado para determinar el nombre correcto del termino
				parcelaVO.setDesTermino(parcela.getTermino().getNomTerminoByFecha(fechaInicioContratacion, false));
				capAsegVO.setDesTipoCapital(capAseg.getTipoCapital().getDestipocapital());

				/* Pet.50776_63485-Fase II ** MODIF TAM (27.10.2020) ** Inicio */
				// DATOS COBERTURAS DE LA PARCELA
				logger.debug("DatosParcelaManager-antes de guardar RiesgoElegibles de parcela");
				Long idLineaSeguroId = parcela.getPoliza().getLinea().getLineaseguroid();
				saveRiesgosElegibles(parcelaVO, idLineaSeguroId, parcela, "-2");
				logger.debug("DatosParcelaManager-despues de guardar RiesgoElegibles de parcela");
				/* Pet.50776_63485-Fase II ** MODIF TAM (27.10.2020) ** Fin */
			}
		} catch (DAOException e) {
			logger.error("Error al guardar la parcela: " + e.getMessage());
			throw new BusinessException(e);
		}
		return errorMsgs.toArray(new String[] {});
	}

	private List<String> verificarDuplicidad(ParcelaVO parcelaVO) throws BusinessException {
		List<String> errorMsgs = new ArrayList<String>();
		// CUANDO ES EL ALTA/MODIFICACION DE UN CAPITAL ASEGURADO EN UNA PARCELA
		// QUE YA EXISTE VERIFICAMOS QUE NO EXISTA OTRO CAPITAL ASEGURADO CON
		// EL MISMO TIPO DE CAPITAL
		if (!StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())) {
			List<CapitalAsegurado> capsAsegs = this.datosParcelaDao
					.getCapitalesAseguradoParcela(Long.valueOf(parcelaVO.getCodParcela()));
			for (CapitalAsegurado capAseg : capsAsegs) {
				if (!parcelaVO.getCapitalAsegurado().getId().equals(capAseg.getIdcapitalasegurado().toString())
						&& capAseg.getTipoCapital().getCodtipocapital()
								.equals(new BigDecimal(parcelaVO.getCapitalAsegurado().getCodtipoCapital()))) {
					errorMsgs.add("No puede haber dos 'Capitales Asegurados' con el mismo tipo capital.");
					break;
				}
			}
		}
		return errorMsgs;
	}

	@Override
	public void borrarTC(final Long idcapitalasegurado) throws BusinessException {
		try {
			this.datosParcelaDao.delete(CapitalAsegurado.class, idcapitalasegurado);
		} catch (DAOException e) {
			logger.error("Error al borrar el tipo de capital: " + e.getMessage());
			throw new BusinessException(e);
		}
	}

	@Override
	public CapitalAseguradoVO getCapitalAsegurado(final Long idcapitalasegurado) throws BusinessException {
		CapitalAseguradoVO capAsegVO;
		try {
			CapitalAsegurado capAseg = (CapitalAsegurado) this.datosParcelaDao.get(CapitalAsegurado.class,
					idcapitalasegurado);
			capAsegVO = ParcelaUtil.getCapitalAseguradoVO(capAseg);
		} catch (DAOException e) {
			logger.error("Error al obtener el tipo de capital: " + e.getMessage());
			throw new BusinessException(e);
		}
		return capAsegVO;
	}

	@Override
	public String getIdSiguienteParcela(final Long idParcelaOrigen, final Long idPoliza, final String listaIdsStr) throws BusinessException {
		String idSigParcela = "";
		try {
			if (StringUtils.isNullOrEmpty(listaIdsStr)) {
				Poliza poliza = (Poliza) this.datosParcelaDao.get(Poliza.class, idPoliza);
				List<Parcela> parcelas = new ArrayList<Parcela>(poliza.getParcelas());
				Collections.sort(parcelas, new Comparator<Parcela>() {
					public int compare(Parcela p0, Parcela p1) {
						return p0.compareTo(p1);
					}
				});
				if (!parcelas.isEmpty() && parcelas.size() > 1) {
					Iterator<Parcela> it = parcelas.iterator();
					while (it.hasNext()) {
						Parcela parcela = it.next();
						if (parcela.getIdparcela().equals(idParcelaOrigen) && it.hasNext()) {
							idSigParcela = it.next().getIdparcela().toString();
							break;
						}
					}
				}
			} else {
				List<String> idLst = Arrays.asList(listaIdsStr.split(","));
				if (!idLst.isEmpty() && idLst.size() > 1) {
					Iterator<String> it = idLst.iterator();
					while (it.hasNext()) {
						String idParcela = it.next();
						if (idParcela.equals(idParcelaOrigen.toString()) && it.hasNext()) {
							idSigParcela = it.next();
							break;
						}
					}
				}
			}
		} catch (DAOException e) {
			logger.error("Error al obtener el identificador de siguiente parcela: " + e.getMessage());
			throw new BusinessException(e);
		}
		return idSigParcela;
	}

	@Override
	public String getDescDatoVariable(final Long lineaseguroid, final String listCodModulos, final Integer codCpto,
			final String valor) {
		return this.datosParcelaDao.getDescDatoVariable(lineaseguroid, listCodModulos, codCpto, valor);
	}

	@Override
	public BigDecimal getCptoAsociadoTC(final BigDecimal codtipocapital) throws BusinessException {
		try {
			TipoCapital tipoCapital = (TipoCapital) this.datosParcelaDao.get(TipoCapital.class, codtipocapital);
			return tipoCapital == null ? null : tipoCapital.getCodconcepto();
		} catch (DAOException e) {
			logger.error("Error al obtener el concepto asociado al tipo de capital: " + e.getMessage());
			throw new BusinessException(e);
		}
	}

	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Inicio */
	@SuppressWarnings("unchecked")
	public List<ParcelasCoberturasNew> getCoberturasParcela(Parcela parcela, ParcelaVO parcelaVO, String codUsuario,
			String realPath, Set<ParcelaCobertura> cobParcExistentes, Long idCapAseg, Poliza plz) throws BusinessException {
		
		List<ParcelasCoberturasNew> lstCobParcelas = new ArrayList<ParcelasCoberturasNew>();
		List<ParcelaCobertura> riesgosElegiblesParcela = new ArrayList<ParcelaCobertura>();

		logger.info("Init - DatosParcelaManager - getCoberturasParcela");
		String xmlRespuesta = null;

		try {
			
			// Control para cuando se llama al WS estando en alta de parcela, ya que en ese
			// momento no tiene id
			if (parcela.getIdparcela() != null) {
				riesgosElegiblesParcela = datosParcelaDao.getObjects(ParcelaCobertura.class, "parcela.idparcela",
						parcela.getIdparcela());
			} else {
				riesgosElegiblesParcela = new ArrayList<ParcelaCobertura>();
			}
			
			Parcela parcelaAux = null;
			
			if (parcelaVO.getCapitalAsegurado().getCodtipoCapital().equals("1") && parcela.getIdparcela()!= null){
				parcelaAux = datosParcelaDao.getDatosParcela( parcela.getIdparcela());
			}
			
			CapitalAsegurado capiAseg = generateCapitalAsegurado(parcelaVO.getCapitalAsegurado(), parcela);
			Set<CapitalAsegurado> capitalesAsegs = new HashSet<CapitalAsegurado>(1);
			capitalesAsegs.add(capiAseg);
			parcela.setCapitalAsegurados(capitalesAsegs);
			
			

			// Obtengo TODOS los riesgos elegibles, sin condicion citricos, y los pongo como
			// no elegidos si la parcela no lo tiene ya
			
			List<ConceptoCubiertoVO> listConceptosCubiertos = new ArrayList<ConceptoCubiertoVO>();

			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
			List<String> modulosP = new ArrayList<String>();

			for (ModuloPoliza modP : modsPoliza) {
				if (!modulosP.contains(modP.getId().getCodmodulo()))
					modulosP.add(modP.getId().getCodmodulo());
			}

			// Por cada modulo llamamos al SW
			for (String modP : modulosP) {

				List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = this.datosParcelaDao
						.getRiesgosCubiertosModulo(plz.getLinea().getLineaseguroid(), modP, null);

				ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(
						plz.getLinea().getLineaseguroid(), modP);
				List<ConceptoCubiertoModulo> lstConcCbrtoMod = datosParcelaDao.getObjects(filtroConcCbrto);

				for (ConceptoCubiertoModulo ccm : lstConcCbrtoMod) {
					if (!existInListConceptoCubiertoVO(listConceptosCubiertos, ccm)) {
						ConceptoCubiertoVO cc = new ConceptoCubiertoVO();
						cc.setDesConcepto(ccm.getDiccionarioDatos().getNomconcepto()); // descripcion
						cc.setId(ccm.getDiccionarioDatos().getCodconcepto().toString()); // codigo
						cc.setNumeroColumna(Integer.parseInt(ccm.getId().getColumnamodulo().toString())); // columna
						listConceptosCubiertos.add(cc);
					}
				} // for

				// Si no hay Riesgos Cubiertos para el modulo no se lanza consulta al SW de
				// Modulos y Coberturas
				if (lstRiesgoCbrtoMod.size() > 0) {
					
					List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
					codsConceptos = polizaDao.getCodsConceptoOrganizador(plz.getLinea().getLineaseguroid());

					logger.debug("** Hay Riesgos Cubiertos para el modulo: -" + modP + '-');

					Collections.sort(listConceptosCubiertos); // reordenar list conceptos cubiertos
					String xmlPoliza = "";
					if (parcelaAux != null) {
						if (parcelaAux != null) {
							
							Set<CapitalAsegurado> capAsegurado = new HashSet<CapitalAsegurado>();
							for (CapitalAsegurado cap: parcelaAux.getCapitalAsegurados()) {
								capAsegurado.add(cap);
							}
							capAsegurado.add(capiAseg);
							parcelaAux.setCapitalAsegurados(capAsegurado);
						}
						
						xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgri(plz, parcelaAux, modP, polizaDao, codsConceptos);
					}else {
						xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgri(plz, parcela, modP, polizaDao, codsConceptos);
					}


					// guardar llamada al WS en BBDD
					String idParc = (parcela.getIdparcela() != null ? parcela.getIdparcela().toString() : "");
					/*** Resol. incidencia 02.01.2020 - Pet. 63485-Fase II ***/
					/* Si el capital es de tipoCapital 1(PLANTONES) no se guarda el xml */
					boolean guardar = false;
					Long idEnvio = new Long(0);
					
					for (CapitalAsegurado cap: parcela.getCapitalAsegurados()) {
						if (cap.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(0))==0){
							guardar = true;
						}
					}
					if (guardar) {
						idEnvio = guardarXmlEnvioParc(plz.getIdpoliza(), idParc, modP, xmlPoliza, codUsuario);
						logger.debug("end - generateAndSaveXMLPolizaCpl");
					}
						
					es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils
							.getModulosCoberturas(xmlPoliza);
					xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();

					if (null != ModulosCoberturasXmlRespuesta) {
						xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();
					}

					// guardar respuesta al WS en BBDD
					
					if (xmlRespuesta != null && guardar) {
						datosParcelaDao.actualizaXmlCoberturasParc(idEnvio, "", xmlRespuesta);
					}

					logger.debug("Respuesta WS: " + xmlRespuesta);
					// Transformarmos las coberturas que nos vienen del WS en una lista de
					// explotacionesCoberturas					
					es.agroseguro.modulosYCoberturas.ModulosYCoberturas modYCob = this.getMyCFromXml(xmlRespuesta);

					if (modYCob.getParcelas() != null) {
						es.agroseguro.modulosYCoberturas.Parcelas parcelas = modYCob.getParcelas();
						es.agroseguro.modulosYCoberturas.Parcela[] parcelaArray = parcelas.getParcelaArray();
						Long contador = new Long(2000000);
						Long nuevas = new Long(1);

						for (es.agroseguro.modulosYCoberturas.Parcela parc : parcelaArray) {
							es.agroseguro.modulosYCoberturas.TipoCapitalAgricola[] tipoCapitalAgri = parc
									.getTipoCapitalArray();

							for (es.agroseguro.modulosYCoberturas.TipoCapitalAgricola tipCapAgri : tipoCapitalAgri) {

								es.agroseguro.modulosYCoberturas.Cobertura[] cobb = tipCapAgri.getCoberturaArray();

								if (cobb.length > 0) {
									List<es.agroseguro.modulosYCoberturas.Cobertura> lstCobReg = Arrays.asList(cobb);

									for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : lstCobReg) {

										/*
										 * Comprobamos si el ConceptoPpal del Modulo y el Riesgo Cubierto estan en la
										 * lista de los permitidos y la cobertura es elegible
										 */
										if (Constants.CHARACTER_S.equals(cobertura.getElegible().toString().charAt(0)) && 
												existInListRiesgos(lstRiesgoCbrtoMod, cobertura.getConceptoPrincipalModulo(), cobertura.getRiesgoCubierto())) {

											ParcelasCoberturasNew parcCob = new ParcelasCoberturasNew();

											parcCob.setId(contador);
											parcCob.setCodmodulo(modP);
											parcCob.setDescModulo(modP);
											parcCob.setFila(cobertura.getFila());
											parcCob.setCpm(cobertura.getConceptoPrincipalModulo());
											parcCob.setCpmDescripcion(cobertura.getDescripcionCPM());
											parcCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
											parcCob.setRcDescripcion(cobertura.getDescripcionRC());
											parcCob.setElegible(cobertura.getElegible().toString().charAt(0));
											if (cobertura.getVinculacionFilaArray() != null) {
												String vinculadas = "";
												es.agroseguro.modulosYCoberturas.VinculacionFila[] vinFila = cobertura
														.getVinculacionFilaArray();
												for (es.agroseguro.modulosYCoberturas.VinculacionFila vFila : vinFila) {
													vinculadas += vFila.getElegida().toString().charAt(0);
													es.agroseguro.modulosYCoberturas.Fila[] fila = vFila.getFilaArray();
													for (es.agroseguro.modulosYCoberturas.Fila fi : fila) {
														vinculadas += "." + fi.getFila() + "."
																+ fi.getElegida().toString().charAt(0) + "." + modP
																+ ".." + "|";
													}
												}
												if (!vinculadas.equals("")) {
													vinculadas = vinculadas.substring(0, vinculadas.length() - 1);
												}
												parcCob.setVinculada(vinculadas);
											}
											logger.debug("Anhadimos primer bucle: " + parcCob.toString());
											lstCobParcelas.add(parcCob);
											contador++;
											nuevas++;
										} /* Fin del if de ConcptoPpalMod y CodRiesgoCub */
										// fin coberturas datos variables

										// Comprobamos si la cobertura tiene datos variables. Si son de tipo E se
										// guardan como tipo cobertura
										es.agroseguro.modulosYCoberturas.DatoVariable[] datArr = cobertura
												.getDatoVariableArray();
										if (datArr.length > 0) {
											List<es.agroseguro.modulosYCoberturas.DatoVariable> lstDatVar = Arrays
													.asList(datArr);

											for (es.agroseguro.modulosYCoberturas.DatoVariable datVar : lstDatVar) {

												String codConceptoStr = String.valueOf(datVar.getCodigoConcepto());

												// recogemos los datosvariables con tipovalor a 'E' y que estan en
												// la lista de Conceptos cubiertos.
												if (existInListConcepto(listConceptosCubiertos, codConceptoStr)
														&& (datVar.getTipoValor() != null
																&& "E".equals(datVar.getTipoValor()))) {
													// E
													// CARGA DATO VAR COMO COBERTURAS

													es.agroseguro.modulosYCoberturas.Valor[] valArr = datVar
															.getValorArray();
													List<es.agroseguro.modulosYCoberturas.Valor> lstValores = Arrays
															.asList(valArr);

													for (es.agroseguro.modulosYCoberturas.Valor valor : lstValores) {

														ParcelasCoberturasNew parcCob = new ParcelasCoberturasNew();
														
														parcCob.setId(contador);
														parcCob.setCodmodulo(modP);
														parcCob.setDescModulo(modP);
														parcCob.setFila(cobertura.getFila());
														parcCob.setCpm(cobertura.getConceptoPrincipalModulo());
														parcCob.setCpmDescripcion(cobertura.getDescripcionCPM());
														parcCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
														parcCob.setRcDescripcion(cobertura.getDescripcionRC());
														parcCob.setElegible(
																cobertura.getElegible().toString().charAt(0));
														parcCob.setDvCodConcepto(
																new Long(datVar.getCodigoConcepto()));
														parcCob.setDvDescripcion(datVar.getNombre());
														parcCob.setDvValor(valor.getValor());
														parcCob.setDvValorDescripcion(valor.getDescripcion());
														parcCob.setDvColumna(new Long(datVar.getColumna()));

														// coberturas datos variables
														String vinculadas = "";

														if (valor.getVinculacionCelda() != null) {
															es.agroseguro.modulosYCoberturas.VinculacionCelda vinCelda = valor
																	.getVinculacionCelda();

															String filaMadre = String
																	.valueOf(vinCelda.getFilaMadre());
															String columnaMadre = String
																	.valueOf(vinCelda.getColumnaMadre());
															String valorMadre = String
																	.valueOf(vinCelda.getValorMadre());

															vinculadas += "X" + "." + filaMadre + "." + "X" + "."
																	+ modP + "." + columnaMadre + "." + valorMadre
																	+ "." + codConceptoStr;
															
															parcCob.setVinculada(vinculadas);
														}
														// fin coberturas datos variables

														logger.debug("Anhadimos segundo bucle: " + parcCob.toString());
														lstCobParcelas.add(parcCob);
														contador++;
														nuevas++;
													} /* Fin del for de valores */
												} /* Fin del if de TipoValor */
											} /* Fin del for de DatosVariables */
										} /* Fin del if datoArray */
									} /* Fin del For de Coberturas */
								}
							}
						}
					} else {
						logger.debug("el SW no devuelve coberturas de Parcelas.");
					}
				} else {
					logger.debug("** NO HAY Riesgos Cubiertos para el modulo: -" + modP + '-');

				} /* Fin del if de lstRiesgoCbrtoMod */

			} /* Fin del For de Modulos */
		} catch (Exception e) {
			logger.error("Ha habido un error al obtener las coberturas de las parcelas", e);
		}
		
		
		/* Tatiana (02.12.2020) */
		// comprobamos si alguna de las que hay ya estaban seleccionadas
		if (riesgosElegiblesParcela.size() > 0) {

			BigDecimal cptoRiesgo = new BigDecimal(363);
			
			/* Tratamos Datos Variables */
			for (ParcelaCobertura cobExist : riesgosElegiblesParcela) {
				String codModulo = cobExist.getRiesgoCubierto().getId().getCodmodulo();
				BigDecimal cpm = cobExist.getConceptoPpalModulo().getCodconceptoppalmod();
				BigDecimal rc = cobExist.getRiesgoCubierto().getId().getCodriesgocubierto();
				BigDecimal codCpto = cobExist.getDiccionarioDatos().getCodconcepto();
				if (codCpto.compareTo(cptoRiesgo) != 0) {
					for (ParcelasCoberturasNew cob : lstCobParcelas) {
						BigDecimal valCpm = BigDecimal.valueOf(cob.getCpm());
						BigDecimal valRc = BigDecimal.valueOf(cob.getRiesgoCubierto());
						if (cob.getDvCodConcepto() != null) {
							if (codModulo.equals(cob.getCodmodulo()) && cpm.compareTo(valCpm) == 0
									&& rc.compareTo(valRc) == 0) {
								if (cob.getDvCodConcepto().compareTo(codCpto.longValue()) == 0) {
									cob.setDvElegido('S');
								}
							}
						}
					}
				}
			} /* Fin codconcepto */
			
			
			/* Tratamos coberturas */
			for (ParcelasCoberturasNew cob : lstCobParcelas) {
				
				logger.debug("bucle riesgosElegiblesParcela - 2");

				BigDecimal valCpm = BigDecimal.valueOf(cob.getCpm());
				BigDecimal valRc = BigDecimal.valueOf(cob.getRiesgoCubierto());
				
				for (ParcelaCobertura cobExist : riesgosElegiblesParcela) {
					
					String codModulo = cobExist.getRiesgoCubierto().getId().getCodmodulo();
					BigDecimal cpm = cobExist.getConceptoPpalModulo().getCodconceptoppalmod();
					BigDecimal rc = cobExist.getRiesgoCubierto().getId().getCodriesgocubierto();
					BigDecimal valor = cobExist.getCodvalor();
					BigDecimal codCpto = cobExist.getDiccionarioDatos().getCodconcepto();
					
					logger.debug("Valor de codModulo: " + codModulo + " y valor de cob.getCodmodulo:" + cob.getCodmodulo());
					logger.debug("Valor de cpm: " + cpm + " y valor de valCpm:" + valCpm);
					logger.debug("Valor de rc: " + rc + " y valor de valRc:" + valRc);
					logger.debug("Valor de codCpto: " + codCpto + " y valor de cob.getDvCodConcepto:" + cob.getDvCodConcepto());
				
					if (codModulo.equals(cob.getCodmodulo()) && cpm.equals(valCpm) && rc.equals(valRc)) {
						if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(codCpto)) {
							cobExist.getCodvalor();
							if (Constants.RIESGO_ELEGIDO_SI.equals(valor.toString())) {
								cob.setElegida(Constants.CHARACTER_S);
							} else {
								cob.setElegida(Constants.CHARACTER_N);
							}
						} else if (cob.getDvCodConcepto() != null && codCpto.toString().equals(cob.getDvCodConcepto().toString())) {
							logger.debug("Entramos en el if");
							logger.debug("Valor: " + valor + " y valor de cob.getDvValor():" + cob.getDvValor());
							if (cob.getDvValor().equals(valor.toString())) {
								cob.setDvElegido(Constants.CHARACTER_S);
							} else {
								cob.setDvElegido(Constants.CHARACTER_N);
							}
							break;
						}
					}
				}
			}
		}
		
		Collections.sort(lstCobParcelas, new ParcelaCoberturaComparator());
		logger.info("End - DatosParcelaManager - getCoberturasParcela");
		return lstCobParcelas;

	}

	/**
	 * Obtiene el objeto Poliza asociado a la Parcela
	 * 
	 * @param e
	 * @return
	 */
	public Poliza getPoliza(Long idPoliza) {
		return (Poliza) datosParcelaDao.getObject(Poliza.class, "idpoliza", idPoliza);
	}

	private Long guardarXmlEnvioParc(Long idPoliza, String idParc, String codmodulo, String envio, String codUsuario)
			throws DAOException {

		logger.debug("DatosParcelaManager - guardarXmlEnvio [INIT]");

		SWModulosCoberturasParcela doc = new SWModulosCoberturasParcela();

		doc.setIdpoliza(idPoliza);
		doc.setFecha((new GregorianCalendar()).getTime());

		doc.setEnvio(Hibernate.createClob(envio));
		doc.setRespuesta(Hibernate.createClob(" "));
		if (!idParc.equals(""))
			doc.setIdparcela(Long.parseLong(idParc));
		doc.setCodmodulo(codmodulo);
		doc.setUsuario(codUsuario);

		SWModulosCoberturasParcela newEnvio = (SWModulosCoberturasParcela) this.datosParcelaDao
				.saveEnvioCobParcela(doc);

		logger.debug("end - guardarXmlEnvio");
		return newEnvio.getId();
	}

	/**
	 * Convierte el xml recibido en un objeto ModulosYCoberturas
	 * 
	 * @param xml
	 * @return
	 */
	public ModulosYCoberturas getMyCFromXml(String xml) {

		ModulosYCoberturas myc = null;

		// Convierte el xml recibido en un objeto ModulosYCoberturas
		try {
			myc = ModulosYCoberturasDocument.Factory.parse(xml).getModulosYCoberturas();
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al parsar el xml a un objeto ModulosYCoberturas", e);
		}

		return myc;
	}

	public boolean existInListConcepto(final List<ConceptoCubiertoVO> listConceptosCubiertos, String codConcepto) {
		boolean result = false;
		if (listConceptosCubiertos.size() > 0 && codConcepto != null) {
			for (ConceptoCubiertoVO conceptoCubiertoVO : listConceptosCubiertos) {
				if (conceptoCubiertoVO.getId().equals(codConcepto))
					result = true;
			}
		}
		return result;
	}

	public boolean existInListRiesgos(final List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod, int codConceptoPpalMod,
			int codRiesgoCub) {

		boolean result = false;

		if (lstRiesgoCbrtoMod.size() > 0) {
			for (RiesgoCubiertoModulo riesgoCubMod : lstRiesgoCbrtoMod) {

				if (BigDecimal.valueOf(codConceptoPpalMod)
						.compareTo(riesgoCubMod.getConceptoPpalModulo().getCodconceptoppalmod()) == 0
						&& BigDecimal.valueOf(codRiesgoCub)
								.compareTo(riesgoCubMod.getRiesgoCubierto().getId().getCodriesgocubierto()) == 0) {
					return true;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void saveRiesgosElegibles(final ParcelaVO parcelaVO, final Long idLineaSeguroId, final Parcela parcela,
			final String isElegido) {

		logger.debug("DatosParcelaManager-saveRiesgosElegibles[INIT]");

		List<ParcelaCobertura> riesgosElegiblesParcela = new ArrayList<ParcelaCobertura>();

		if (parcela.getIdparcela() != null) {
			riesgosElegiblesParcela = datosParcelaDao.getObjects(ParcelaCobertura.class, "parcela.idparcela",
					parcela.getIdparcela());
		} else {
			riesgosElegiblesParcela = new ArrayList<ParcelaCobertura>();
		}

		/* Antes de volver a insertar datos, primero los borramos de la tabla */
		if (!riesgosElegiblesParcela.isEmpty()) {
			logger.debug("Antes de borrarRiesgosElegParcela");
			datosParcelaDao.borrarRiesgosElegParcela(riesgosElegiblesParcela);
		}

		try {
			// codConcepto; Para riesgos 363 --> elegido: -1, no elegido: -2, otros: valor

			DatosModulosRiesgosVO datosModulosRiesgosVO = new DatosModulosRiesgosVO();
			datosModulosRiesgosVO.setLineaSeguroId(idLineaSeguroId.toString());
			datosModulosRiesgosVO.setParcelaVO(parcelaVO);

			// Introduzco en la BD todos los RIESGOS/COBERTURAS como no seleccionados
			// Solo se introduccen los ELEGIBLES (como no elegidos)
			for (RiesgoVO riesgoVO : parcelaVO.getRiesgosSeleccionados()) {

				// Parcela cobertura
				ParcelaCobertura parcelaCobertura = new ParcelaCobertura();
				parcelaCobertura.setCodvalor(new BigDecimal(riesgoVO.getCodValor()));
				// Concepto principal modulo
				ConceptoPpalModulo conceptoPpalModulo = new ConceptoPpalModulo();
				conceptoPpalModulo.setCodconceptoppalmod(new BigDecimal(riesgoVO.getCodConceptoPpalMod()));
				parcelaCobertura.setConceptoPpalModulo(conceptoPpalModulo);
				parcelaCobertura.setParcela(parcela);

				// riesgo cubierto
				RiesgoCubierto riesgoCubierto = new RiesgoCubierto();

				RiesgoCubiertoId riesgoCubiertoId = new RiesgoCubiertoId();
				riesgoCubiertoId.setCodriesgocubierto(new BigDecimal(riesgoVO.getCodRiesgoCubierto()));
				riesgoCubiertoId.setLineaseguroid(idLineaSeguroId);
				riesgoCubiertoId.setCodmodulo(riesgoVO.getCodModulo());

				riesgoCubierto.setId(riesgoCubiertoId);
				parcelaCobertura.setRiesgoCubierto(riesgoCubierto);
				// Diccionarion de datos
				DiccionarioDatos diccionarioDatos = new DiccionarioDatos();
				diccionarioDatos.setCodconcepto(new BigDecimal(riesgoVO.getCodConcepto()));
				parcelaCobertura.setDiccionarioDatos(diccionarioDatos);

				logger.debug("Guardamos riesgo con codriesgo:"
						+ parcelaCobertura.getRiesgoCubierto().getId().getCodriesgocubierto() + " y codConcepto"
						+ parcelaCobertura.getDiccionarioDatos().getCodconcepto());
				parcelaCobertura = (ParcelaCobertura) this.datosParcelaDao.saveOrUpdate(parcelaCobertura);

			} // for coberturas
		
		} catch (Exception ex) {
			logger.error("Se ha producido un error al recuperar las coberturas de la parcela", ex);
		}

		logger.debug("DatosParcelaManager-saveRiesgosElegibles[END]");

	}
	
	public boolean isCoberturasElegiblesNivelParcela(Long lineaseguroId, String codModulo) {
		return datosParcelaDao.isCoberturasElegiblesNivelParcela(lineaseguroId, codModulo);
	}

	public boolean isCoberturasElegiblesNivelParcela(Long lineaseguroId, Set<ModuloPoliza> modsPoliza) {
		String codModulos = "";
		boolean primera = true;
		for (ModuloPoliza modP : modsPoliza) {
			if (!primera)
				codModulos = codModulos + ",";
			codModulos = codModulos + "'" + modP.getId().getCodmodulo() + "'";
			primera = false;
		}
		return datosParcelaDao.isCoberturasElegiblesNivelParcela(lineaseguroId, codModulos);
	}

	public Parcela getDatParcela(Long idParcela) {
		return datosParcelaDao.getDatosParcela(idParcela);
	}
	
	public String[] actualizarParcelasCoberturas(ParcelaVO parcelaVO, String codUsuario, Long idPoliza)
			throws BusinessException {

		List<String> errorMsgs = new ArrayList<String>();

		logger.info("Init - DatosParcelaManager - getCoberturasParcelaNew");
		String xmlRespuesta = null;

		try {

			Poliza plz = getPoliza(idPoliza);

			Parcela parcela = new Parcela();

			generateParcela(parcelaVO, parcela, plz.getLinea().getLineaseguroid());
			parcela.setIdparcela(Long.valueOf(parcelaVO.getCodParcela()));
			CapitalAsegurado capiAseg = generateCapitalAsegurado(parcelaVO.getCapitalAsegurado(), parcela);
			Set<CapitalAsegurado> capitalesAsegs = new HashSet<CapitalAsegurado>(1);
			capitalesAsegs.add(capiAseg);
			parcela.setCapitalAsegurados(capitalesAsegs);

			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();
			List<String> modulosP = new ArrayList<String>();
			
			Long lineaSeguroId = Long.valueOf(plz.getLinea().getLineaseguroid());

			for (ModuloPoliza modP : modsPoliza) {
				if (!modulosP.contains(modP.getId().getCodmodulo()))
					modulosP.add(modP.getId().getCodmodulo());
			}

			// Por cada modulo llamamos al SW
			for (String modP : modulosP) {

				List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = this.datosParcelaDao
						.getRiesgosCubiertosModulo(plz.getLinea().getLineaseguroid(), modP, null);

				// Si no hay Riesgos Cubiertos para el modulo no se lanza consulta al SW de
				// Modulos y Coberturas
				if (lstRiesgoCbrtoMod.size() > 0) {
					
					List<BigDecimal> codsConceptos = new ArrayList<BigDecimal>();
					codsConceptos = polizaDao.getCodsConceptoOrganizador(lineaSeguroId);

					logger.debug("** Hay Riesgos Cubiertos para el modulo: -" + modP + '-');
					String xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgri(plz, parcela, modP, polizaDao, codsConceptos);

					// guardar llamada al WS en BBDD
					String idParc = (parcela.getIdparcela() != null ? parcela.getIdparcela().toString() : "");
					
					/*** Resol. incidencia 02.01.2020 - Pet. 63485-Fase II ***/
					/* Si el capital es de tipoCapital 1(PLANTONES) no se guarda el xml */
					boolean guardar = false;
					Long idEnvio = new Long(0);
					for (CapitalAsegurado cap: parcela.getCapitalAsegurados()) {
						if (cap.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(0))==0){
							guardar = true;
						}
					}
					if (guardar) {
						idEnvio = guardarXmlEnvioParc(plz.getIdpoliza(), idParc, modP, xmlPoliza, codUsuario);
						logger.debug("end - generateAndSaveXMLPolizaCpl");
					}		

					es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils
							.getModulosCoberturas(xmlPoliza);
					xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();

					if (null != ModulosCoberturasXmlRespuesta) {
						xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();
					}

					// guardar respuesta al WS en BBDD
					if (xmlRespuesta != null && guardar) {
						datosParcelaDao.actualizaXmlCoberturasParc(idEnvio, "", xmlRespuesta);
					}

				} else {
					logger.debug("** NO HAY Riesgos Cubiertos para el modulo: -" + modP + '-');

				} /* Fin del if de lstRiesgoCbrtoMod */

			} /* Fin del For de Modulos */
		} catch (Exception e) {
			logger.error("Se ha producio un error al actualizar SW Parcelas Coberturas", e);
			errorMsgs.add("Se ha producido un error al actualizar Parcelas Coberturas.");
		}
		return errorMsgs.toArray(new String[] {});

	}

	private CapitalAsegurado generateCapitalAsegurado(CapitalAseguradoVO capitalAseguradoVO, Parcela parcela)
			throws NumberFormatException, DAOException {
		CapitalAsegurado capitalAsegurado = new CapitalAsegurado();
		capitalAsegurado.setParcela(parcela);
		if (StringUtils.isNullOrEmpty(capitalAseguradoVO.getSuperficie())) {
			capitalAsegurado.setSuperficie(BigDecimal.ZERO);
		} else {
			capitalAsegurado.setSuperficie(new BigDecimal(capitalAseguradoVO.getSuperficie()));
		}
		TipoCapital tipoCapital = new TipoCapital();
		if (!StringUtils.isNullOrEmpty(capitalAseguradoVO.getCodtipoCapital())) {
			tipoCapital.setCodtipocapital(new BigDecimal(capitalAseguradoVO.getCodtipoCapital()));
		}
		capitalAsegurado.setTipoCapital(tipoCapital);
		if (StringUtils.isNullOrEmpty(capitalAseguradoVO.getPrecio())) {
			capitalAsegurado.setPrecio(BigDecimal.ZERO);
		} else {
			capitalAsegurado.setPrecio(new BigDecimal(capitalAseguradoVO.getPrecio()));
		}
		if (StringUtils.isNullOrEmpty(capitalAseguradoVO.getProduccion())) {
			capitalAsegurado.setProduccion(BigDecimal.ZERO);
		} else {
			capitalAsegurado.setProduccion(new BigDecimal(capitalAseguradoVO.getProduccion()));
		}
		// ----------------------------------- DATOS VARIABLES
		// -----------------------------------
		capitalAsegurado.setDatoVariableParcelas(new HashSet<DatoVariableParcela>());
		for (DatoVariableParcelaVO datoVariableParcelaVO : capitalAseguradoVO.getDatosVariablesParcela()) {
			if (datoVariableParcelaVO.getValor() != null && !datoVariableParcelaVO.getValor().equals("")
					&& datoVariableParcelaVO.getCodconcepto() != null) {
				StringTokenizer tokens = new StringTokenizer(datoVariableParcelaVO.getValor(), ";");
				DatoVariableParcela datoVariableParcela1 = getNewDatoVariableParcela(capitalAsegurado,
						datoVariableParcelaVO, tokens);
				if (datoVariableParcela1 != null) {
					capitalAsegurado.getDatoVariableParcelas().add(datoVariableParcela1);
				}
			}
		}
		// ----------------------------------- FIN datos
		// variables-----------------------------------
		return capitalAsegurado;
	}

	private DatoVariableParcela getNewDatoVariableParcela(CapitalAsegurado capitalAsegurado,
			DatoVariableParcelaVO datoVariableParcelaVO, StringTokenizer tokens) {

		DatoVariableParcela datoVariableParcela1 = null;

		// Se tiene en cuenta el concepto "riesgo cubierto elegido" porque se guarda de
		// manera distinta.
		if (datoVariableParcelaVO.getCodconcepto().intValue() != ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO) {

			String valor = "";
			if (tokens.countTokens() > 1) {
				// Lista multiple: el valor se guardara de la siguiente manera: valor1 + " " +
				// valor2 + " " + valor3...
				while (tokens.hasMoreTokens()) {
					String valueItem = tokens.nextToken();
					valor += valueItem + " ";
				}
			} else {
				// Dato simple: se guarda el valor "tal cual".
				valor = datoVariableParcelaVO.getValor();
			}

			datoVariableParcela1 = generateNewDatoVariable(valor, datoVariableParcelaVO.getCodconcepto(),
					capitalAsegurado);
		}

		return datoVariableParcela1;
	}

	private DatoVariableParcela generateNewDatoVariable(String valor, Integer codconcepto,
			CapitalAsegurado capitalAsegurado) {
		DatoVariableParcela datoVariableParcela = new DatoVariableParcela();
		DiccionarioDatos diccionarioDatos = new DiccionarioDatos();

		datoVariableParcela.setValor(valor);
		diccionarioDatos.setCodconcepto(new BigDecimal(codconcepto));
		datoVariableParcela.setDiccionarioDatos(diccionarioDatos);
		datoVariableParcela.setCapitalAsegurado(capitalAsegurado);

		return datoVariableParcela;
	}

	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */

	public void setDatosParcelaDao(final IDatosParcelaDao datosParcelaDao) {
		this.datosParcelaDao = datosParcelaDao;
	}

	public void setAjaxManager(AjaxManager ajaxManager) {
		this.ajaxManager = ajaxManager;
	}

	public void setTerminoDao(ITerminoDao terminoDao) {
		this.terminoDao = terminoDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}



	public void copyParcelaCobertura(Long id, Long idparcela) throws BusinessException {
		logger.debug("DatosParcelaManager - copyParcelaCobertura");

		try {
		datosParcelaDao.copyParcelaCobertura(id, idparcela);
		
	} catch (DAOException e) {
		logger.error("Error al copiar la cobertura: " + e.getMessage());
		throw new BusinessException(e);
	}
		
		logger.debug("DatosParcelaManager - copyParcelaCobertura FIN");

	}
	
	public void actualizaParcelaCobertura(Long idcoberturaorigen, Long idparceladestino, Long lineaseguroid) throws BusinessException {
		// TODO Auto-generated method stub
		logger.debug("DatosParcelaManager - actualizaParcelaCobertura");

		try {
		datosParcelaDao.actualizaParcelaCobertura(idcoberturaorigen, idparceladestino,lineaseguroid);
		
	} catch (DAOException e) {
		logger.error("Error al actualizar la cobertura: " + e.getMessage());
		throw new BusinessException(e);
	}
		
		logger.debug("DatosParcelaManager - actualizaParcelaCobertura FIN");

	}
	
	public void copyElegibleCoberturas(Long idparcela) throws BusinessException {

		logger.debug("DatosParcelaManager - copyElegibleCoberturas");

		try {
			datosParcelaDao.copyElegibleCoberturas(idparcela);

		} catch (DAOException e) {
			logger.error("Error al copiar la cobertura: " + e.getMessage());
			throw new BusinessException(e);
		}

		logger.debug("DatosParcelaManager - copyElegibleCoberturas FIN");

	}

	public boolean existInTbScSRiesgoCcbrtoMod(ParcelaCobertura cobertura) throws BusinessException {
		logger.debug("DatosParcelaManager - existInTbScSRiesgoCcbrtoMod");

		try {
			return datosParcelaDao.existInTbScSRiesgoCcbrtoMod(cobertura);
		
		} catch (DAOException e) {
			logger.error("Error al copiar la cobertura: " + e.getMessage());
			throw new BusinessException(e);
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<ParcelaCobertura> getCoberturasElegiblesParcela(Long idparcela) {
		return datosParcelaDao.getObjects(ParcelaCobertura.class, "parcela.idparcela", idparcela);
	}
}