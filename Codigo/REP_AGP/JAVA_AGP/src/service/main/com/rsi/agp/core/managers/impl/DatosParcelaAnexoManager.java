package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ParcelaAnexoUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.filters.cpl.ConceptoCubiertoModuloFiltro;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloRiesgoSelecParcFiltro;
import com.rsi.agp.dao.filters.poliza.AseguradoAutorizadoFiltro;
import com.rsi.agp.dao.filters.poliza.FechaFinGarantiasFiltro;
import com.rsi.agp.dao.filters.poliza.MedidaFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.models.anexo.IParcelaModificacionPolizaDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaAnexoDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Comarca;
import com.rsi.agp.dao.tables.commons.ComarcaId;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.TerminoId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.AmbitoAsegurable;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.CultivoId;
import com.rsi.agp.dao.tables.cpl.FechaFinGarantia;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraPrecio;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.cpl.VinculacionValoresModulo;
import com.rsi.agp.dao.tables.masc.CampoMascara;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.vo.CampoPantallaConfigurableVO;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.CaracteristicaRiesgoVO;
import com.rsi.agp.vo.ComarcaVO;
import com.rsi.agp.vo.ConceptoCubiertoVO;
import com.rsi.agp.vo.CultivoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.DatosModulosRiesgosVO;
import com.rsi.agp.vo.DatosPantallaConfigurableVO;
import com.rsi.agp.vo.ItemSubvencionVO;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.LocalCultVarVO;
import com.rsi.agp.vo.ModuloVO;
import com.rsi.agp.vo.ModulosVO;
import com.rsi.agp.vo.OperationResultVO;
import com.rsi.agp.vo.PantallaConfigurableVO;
import com.rsi.agp.vo.ParamsSubvencionesVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.ProduccionVO;
import com.rsi.agp.vo.ProvinciaVO;
import com.rsi.agp.vo.RiesgoVO;
import com.rsi.agp.vo.SigpacVO;
import com.rsi.agp.vo.SubterminoVO;
import com.rsi.agp.vo.SubvencionesVO;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DatosParcelaAnexoManager implements IManager {

	private static final String SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_LAS_FECHAS_DE_RECOLECCION = "Se ha producido un error al recuperar las Fechas de Recoleccion.";
	private static final String SE_HA_PRODUCIDO_UN_ERROR_EN_EL_ACCESO_A_LA_BASE_DE_DATOS = "Se ha producido un error en el acceso a la base de datos.";
	private IDatosParcelaAnexoDao datosParcelaAnexoDao; 
	private IParcelaModificacionPolizaDao parcelaModificacionPolizaDao;
	private static final Log logger = LogFactory.getLog(DatosParcelaAnexoManager.class);

	public static final String MODIFICAR_PARCELA = "modificarParcela";
	public static final String ALTA_PARCELA = "altaParcela";
	public static final String MODIFICAR_CAPITAL_ASEGURADO = "modificarCapitalAsegurado";
	public static final String ALTA_CAPITAL_ASEGURADO = "altaCapitalAsegurado";
	private static final String REPLICAR_PARCELA = "replicarParcela";
	private static final Character MODIFICAR = 'M';
	private static final Character ALTA = 'A';
	private static final String ALTA_ACTUALIZAR_REPLICAR_KO = "Error al dar de alta, actualizar o replicar la parcela.";
	private static final String ELEGIBLE = "ELEGIBLE";
	private static final String CON_CITRICOS = "conCitricos";

	// Operaciones estructura/instalacion
	public static final String ALTA_ESTRUCTURA_PARCELA = "altaEstructuraParcela";
	public static final String MODIFICAR_INSTALACION_PARCELA = "modificarInstalacionParcela";
	
	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;

	/**
	 * Obtiene el listado de provincias
	 * 
	 * @return listado de objetos ProvinciaVO
	 */
	public List<ProvinciaVO> getProvincias(Long codlinea, Long codplan) {

		List<AmbitoAsegurable> listProvincias = null;
		List<ProvinciaVO> listProvinciasVO = null;

		try {
			Linea linea = datosParcelaAnexoDao.getLineaseguroId(codlinea, codplan);
			listProvincias = datosParcelaAnexoDao.getAmbitosAsegurablesProvincias(linea.getLineaseguroid());
			listProvinciasVO = getListProvinciasVO(listProvincias);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar el listado de Provincias", excepcion);
		}
		return listProvinciasVO;
	}

	/**
	 * Obtiene un listado de comarcas
	 * 
	 * @param codProvincia:
	 *            codigo de provincia
	 * @return listado de objetos ComarcaVO
	 */
	public List<ComarcaVO> getComarcas(Long codProvincia) {
		List<Comarca> listComarcas = null;
		List<ComarcaVO> listComarcasVO = new ArrayList<ComarcaVO>();

		try {
			listComarcas = datosParcelaAnexoDao.getComarcas(codProvincia);
			listComarcasVO = getListComarcasVO(listComarcas);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar el listado de Comarcas", excepcion);
		}
		return listComarcasVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public List<SubterminoVO> getSubterminos(Long codProvincia, Long codTermino, String subtermino, Long codlinea,
			Long codplan) {
		List<VistaTerminosAsegurable> listSubterminos = null;
		List<SubterminoVO> listSubterminosVO = new ArrayList<SubterminoVO>();
		try {
			Linea linea = datosParcelaAnexoDao.getLineaseguroId(codlinea, codplan);
			listSubterminos = datosParcelaAnexoDao.getSubterminos(codProvincia, codTermino, subtermino,
					linea.getLineaseguroid());

			listSubterminosVO = getListSubterminosVO(listSubterminos);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar el listado de Terminos/Subtrm", excepcion);
		}

		return listSubterminosVO;
	}

	/**
	 * 
	 * @param listLocalCultVarVO
	 * @param codlinea
	 * @param codplan
	 * @param claseId
	 * @return
	 */
	public List<SubterminoVO> getSubterminosBySigpacList(List<LocalCultVarVO> listLocalCultVarVO, Long codlinea,
			Long codplan, Long claseId) {

		List<VistaTerminosAsegurable> listSubterminos = null;
		List<SubterminoVO> listSubterminosVO = new ArrayList<SubterminoVO>();
		try {
			Linea linea = datosParcelaAnexoDao.getLineaseguroId(codlinea, codplan);
			listSubterminos = datosParcelaAnexoDao.getSubterminosBySigpacList(listLocalCultVarVO,
					linea.getLineaseguroid(), claseId);
			datosParcelaAnexoDao.getSubterminosBySigpacList(listLocalCultVarVO, linea.getLineaseguroid(), claseId);
			listSubterminosVO = getListSubterminosVO(listSubterminos);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar el listado de Terminos/Subtrm", excepcion);
		}

		return listSubterminosVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public List<CultivoVO> getCultivos(Long codLinea, Long codPlan) {
		List<Cultivo> listCultivos = null;
		List<CultivoVO> listCultivosVO = new ArrayList<CultivoVO>();

		try {
			listCultivos = datosParcelaAnexoDao.getCultivos(codLinea, codPlan);
			listCultivosVO = getListCultivosVO(listCultivos);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar al listado de Cultivos", excepcion);
		}

		return listCultivosVO;
	}

	public SubvencionesVO getSubvenciones(ParamsSubvencionesVO paramsSubvencionesVO) throws Exception {
		SubvencionesVO subvencionesVO = new SubvencionesVO();
		try {
			ArrayList<ItemSubvencionVO> listSuvbencionesEnesa = new ArrayList<ItemSubvencionVO>();
			ArrayList<ItemSubvencionVO> listSuvbencionesCCAA = new ArrayList<ItemSubvencionVO>();

			listSuvbencionesEnesa = datosParcelaAnexoDao.getSubvencionesParcelaEnesa(paramsSubvencionesVO);
			listSuvbencionesCCAA = datosParcelaAnexoDao.getSubvencionesParcelaCCAA(paramsSubvencionesVO);

			for (ItemSubvencionVO itemSubvencionVO : listSuvbencionesCCAA) {
				if (!isItemInEnesaArray(itemSubvencionVO.getCodigo(), listSuvbencionesEnesa))
					listSuvbencionesEnesa.add(itemSubvencionVO);
			}

			subvencionesVO.setSubvencionesEnesa(listSuvbencionesEnesa);
		} catch (Exception excepcion) {
			logger.error(excepcion.getMessage());
			throw excepcion;
		}

		return subvencionesVO;
	}

	/**
	 * devuelve true si codSubvencion esta en listSuvbencionesEnesa
	 */
	@SuppressWarnings("unused")
	private boolean isItemInEnesaArray(Long codSubvencion, ArrayList<ItemSubvencionVO> listSuvbencionesEnesa) {
		boolean result = false;
		for (ItemSubvencionVO itemSubvencionVO : listSuvbencionesEnesa) {
			if (itemSubvencionVO.getCodigo() != null && itemSubvencionVO.getCodigo().equals(codSubvencion))
				result = true;
			break;
		}
		return result;
	}

	/**
	 * 
	 * @param
	 * @throws Exception
	 */
	public PantallaConfigurableVO getPantallaConfigurada(Long lineaSeguroId, DatosPantallaConfigurableVO datosPantalla)
			throws Exception {
		PantallaConfigurableVO pantallaConfigurableVO = new PantallaConfigurableVO();

		try {
			// Validamos que la ubicacion sea real
			if (datosPantalla.getSubtermino() == null || datosPantalla.getSubtermino().equals(""))
				datosPantalla.setSubtermino(" ");

			Character subterm = datosPantalla.getSubtermino().charAt(0);

			ArrayList<String> errores = isValidUbicacion(new BigDecimal(datosPantalla.getCodProvincia()),
					new BigDecimal(datosPantalla.getCodComarca()), new BigDecimal(datosPantalla.getCodTermino()),
					subterm);

			if (errores.size() != 0)
				throw new Exception("Se ha producido un error al validar la Ubicacion.");
			else
				pantallaConfigurableVO = getPantallaConfigurableVO(lineaSeguroId, datosPantalla);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar la PantallaConfigurada", excepcion);
			throw excepcion;
		}

		return pantallaConfigurableVO;
	}

	/**
	 * Obtiene una parcela y genera su parcelaVO.
	 * 
	 * @param codigo
	 *            de parcela
	 * @return objeto tipo parcelaVO
	 */
	public ParcelaVO getParcela(Long codParcela) {
		ParcelaVO parcelaVO = new ParcelaVO();
		Parcela parcela = new Parcela();

		try {
			parcela = (Parcela) this.datosParcelaAnexoDao.getObject(Parcela.class, codParcela);
			if (parcela != null)
				parcelaVO = ParcelaAnexoUtil.getParcelaVO(parcela, this.datosParcelaAnexoDao);
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar la Parcela", excepcion);
			parcelaVO = null;
		}

		return parcelaVO;
	}

	/**
	 * Obtiene los modulos y sus riesgos seleccionables a nivel de parcela
	 * 
	 * @param datosModulosRiesgosVO
	 *            (objeto con los datos necesatios)
	 * @return objeto tipo ModulosVO (es un contenedor de modulos) Aviso: los
	 *         modulos no elegidos no se daran de alta
	 */
	public ModulosVO getModulosRiesgosParcela(DatosModulosRiesgosVO datosModulosRiesgosVO, String condicionCitricos)
			throws BusinessException {

		ModulosVO modulosVO = new ModulosVO();
		FechaFinGarantiasFiltro ffgFiltro = null;

		try {
			// Listado de modulos filtrados
			ModuloRiesgoSelecParcFiltro filtro = new ModuloRiesgoSelecParcFiltro(
					Long.parseLong(datosModulosRiesgosVO.getLineaSeguroId()), datosModulosRiesgosVO.getModulos());
			List<Modulo> modulos = datosParcelaAnexoDao.getObjects(filtro);

			// Coberturas - concepto - caracteristica
			// Usado para renderizar las columnas en un orden concreto
			List<ConceptoCubiertoVO> listConceptosCubiertos = new ArrayList<ConceptoCubiertoVO>();

			if (modulos.size() > 0) {

				ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(
						Long.parseLong(datosModulosRiesgosVO.getLineaSeguroId()), datosModulosRiesgosVO.getModulos());
				List<ConceptoCubiertoModulo> lstConcCbrtoMod = datosParcelaAnexoDao.getObjects(filtroConcCbrto);

				for (ConceptoCubiertoModulo ccm : lstConcCbrtoMod) {
					ConceptoCubiertoVO cc = new ConceptoCubiertoVO();
					cc.setDesConcepto(ccm.getDiccionarioDatos().getNomconcepto()); // descripcion
					cc.setId(ccm.getDiccionarioDatos().getCodconcepto().toString()); // codigo
					cc.setNumeroColumna(Integer.parseInt(ccm.getId().getColumnamodulo().toString())); // columna
					listConceptosCubiertos.add(cc);
				}

				Collections.sort(listConceptosCubiertos); // reordenar list conceptos cubiertos
				modulosVO.setListConceptosCubiertos(listConceptosCubiertos);
			}

			// ------------------------------- MODULOS -------------------------------
			for (Modulo modulo : modulos) {
				ModuloVO moduloVO = new ModuloVO();
				moduloVO.setCodModulo(modulo.getId().getCodmodulo());
				moduloVO.setDesModulo(modulo.getDesmodulo());

				// ---------------------------- RIESGOS -------------------------------
				ModuloId moduloid = new ModuloId(modulo.getId().getLineaseguroid(), modulo.getId().getCodmodulo());
				List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = datosParcelaAnexoDao
						.getObjects(RiesgoCubiertoModulo.class, "modulo.id", moduloid);

				for (RiesgoCubiertoModulo riesgo : lstRiesgoCbrtoMod) {

					// --- NIVEL SELECCION = D ---
					if (riesgo.getNiveleccion() != null && riesgo.getNiveleccion() == 'D'
							&& riesgo.getElegible() == 'S') {

						RiesgoVO riesgoVO = new RiesgoVO();
						String riesgoElegible = "";

						// --- LINEA = 301 (CITRICOS) ---
						if (riesgo.getModulo().getLinea().getCodlinea().toString().equals("301")
								&& condicionCitricos.equals(CON_CITRICOS)) {

							// APLICO EL FILTRO UNA VEZ POR CADA CAPITAL ASEGURADO
							if (datosModulosRiesgosVO.getParcelaVO().getCapitalesAsegurados() != null) {
								for (int i = 0; i < datosModulosRiesgosVO.getParcelaVO().getCapitalesAsegurados()
										.size(); i++) {

									String ffGarantias = "";
									CapitalAseguradoVO cpVO = datosModulosRiesgosVO.getParcelaVO()
											.getCapitalesAsegurados().get(i);

									// Recorro los datos variables buscando: practica cultural (133) y
									// fecha fin de garantias (134)
									for (int e = 0; e < cpVO.getDatosVariablesParcela().size(); e++) {
										DatoVariableParcelaVO avpVO = cpVO.getDatosVariablesParcela().get(e);
										if (avpVO.getCodconcepto() == 134)
											ffGarantias = avpVO.getValor();
									}

									ffgFiltro = new FechaFinGarantiasFiltro(
											datosModulosRiesgosVO.getParcelaVO().getCultivo(),
											datosModulosRiesgosVO.getParcelaVO().getVariedad(),
											cpVO.getCodtipoCapital(),
											datosModulosRiesgosVO.getParcelaVO().getCodProvincia(),
											datosModulosRiesgosVO.getParcelaVO().getCodComarca(),
											datosModulosRiesgosVO.getParcelaVO().getCodTermino(),
											datosModulosRiesgosVO.getParcelaVO().getCodSubTermino(), ffGarantias,
											riesgo.getConceptoPpalModulo().getCodconceptoppalmod().toString(),
											riesgo.getRiesgoCubierto().getId().getCodriesgocubierto().toString(),
											modulo.getId().getCodmodulo(), modulo.getId().getLineaseguroid());

									List<FechaFinGarantia> ffgList = datosParcelaAnexoDao.getObjects(ffgFiltro);

									logger.debug(" ffgList.size: " + ffgList.size());

									if (ffgList.size() > 0)
										riesgoElegible = ELEGIBLE;

								} // for
							} // if
						} // if 301
						else
							riesgoElegible = ELEGIBLE;

						// GARANTIA && RIESGOS CUBIERTOS
						riesgoVO.getCaracteristicasRiesgo().add(new CaracteristicaRiesgoVO("", "",
								riesgo.getConceptoPpalModulo().getDesconceptoppalmod(), -2));

						/*
						 * ASF: AUNQUE EL RIESGO SEA ELEGIBLE, SOLO PERMITIMOS ELEGIRLO SI POR FECHA DE
						 * FIN DE GARANTIAS. if(riesgo.getElegible()=='S') riesgoElegible = ELEGIBLE;
						 */

						riesgoVO.getCaracteristicasRiesgo().add(new CaracteristicaRiesgoVO(riesgoElegible, "",
								riesgo.getRiesgoCubierto().getDesriesgocubierto(), -1));

						// VALOR (7 posibles valores)
						String capAseg = "", calculo = "", minIndem = "", tipoFranq = "", pctFranq = "", garant = "",
								tipoRend = "";

						String elegible = ""; // ELEGIBLE
						String observacion = ""; // OBSERVACION
						Integer columna = 9999; // COLUMNA

						// --------------------------- CARACTERISTICAS -------------------------------

						List<CaracteristicaModulo> lstCaractMod = datosParcelaAnexoDao
								.getObjects(CaracteristicaModulo.class, "riesgoCubiertoModulo.id", riesgo.getId());
						for (CaracteristicaModulo cm : lstCaractMod) {
							elegible = "";
							// Elegible
							if (cm.getTipovalor() == 'E')
								elegible = ELEGIBLE;

							// Observaciones
							if (cm.getObservaciones() != null && !"".equals(cm.getObservaciones()))
								observacion = cm.getObservaciones();

							// Fila - Columna
							columna = Integer.parseInt(cm.getId().getColumnamodulo().toString());

							// --------------------- VINCULACION VALORES -----------------------------

							List<VinculacionValoresModulo> lstVincValMod = datosParcelaAnexoDao.getObjects(
									VinculacionValoresModulo.class, "caracteristicaModuloByFkVincValModCaracMod1.id",
									cm.getId());
							for (VinculacionValoresModulo vvm : lstVincValMod) {
								if (vvm.getCalculoIndemnizacionByCalcindemneleg() != null
										&& vvm.getCalculoIndemnizacionByCalcindemneleg().getDescalculo() != null) {
									calculo = vvm.getCalculoIndemnizacionByCalcindemneleg().getDescalculo();
									riesgoVO.getCaracteristicasRiesgo()
											.add(new CaracteristicaRiesgoVO(elegible, observacion, calculo, columna));
								} else if (vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg() != null
										&& vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg()
												.getDescapitalaseg() != null) {
									capAseg = vvm.getCapitalAseguradoElegibleByPctcapitalasegeleg().getDescapitalaseg();
									riesgoVO.getCaracteristicasRiesgo()
											.add(new CaracteristicaRiesgoVO(elegible, observacion, capAseg, columna));
								} else if (vvm.getGarantizadoByGarantizadoeleg() != null
										&& vvm.getGarantizadoByGarantizadoeleg().getDesgarantizado() != null) {
									garant = vvm.getGarantizadoByGarantizadoeleg().getDesgarantizado();
									riesgoVO.getCaracteristicasRiesgo()
											.add(new CaracteristicaRiesgoVO(elegible, observacion, garant, columna));
								} else if (vvm.getMinimoIndemnizableElegibleByPctminindemneleg() != null && vvm
										.getMinimoIndemnizableElegibleByPctminindemneleg().getDesminindem() != null) {
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

						riesgoVO.setCodConceptoPpalMod(
								riesgo.getConceptoPpalModulo().getCodconceptoppalmod().toString());
						riesgoVO.setCodRiesgoCubierto(
								riesgo.getRiesgoCubierto().getId().getCodriesgocubierto().toString());
						riesgoVO.setCodValor("-1");
						riesgoVO.setCodModulo(riesgo.getRiesgoCubierto().getModulo().getId().getCodmodulo().toString());
						riesgoVO.setLineaSeguroId(
								riesgo.getRiesgoCubierto().getModulo().getId().getLineaseguroid().toString());
						riesgoVO.setCodConcepto("363");

						if (riesgoElegible.equals(ELEGIBLE))
							moduloVO.getRiesgos().add(riesgoVO); // Anhade el nuevo riesgo al modulo

						// Reordenar, antes de anhadir el modulo, todos CaracteristicaRiesgoVO de los
						// riesgos
						// del nuevo modulo. Se hace esto porque en la vista deben renderizarse en
						// orden.
						for (int i = 0; i < moduloVO.getRiesgos().size(); i++)
							moduloVO.getRiesgos().get(i).setCaracteristicasRiesgo(reordenarCaracteristicaRiesgoVO(
									moduloVO.getRiesgos().get(i).getCaracteristicasRiesgo()));

					} // if riesgo.getNiveleccion().equals('D') and riesgo.getElegible = 'S'
				} // for riesgos

				// Solo se anade el nuevo modulo si tiene algun riesgo
				if (moduloVO.getRiesgos().size() > 0)
					modulosVO.getModulos().add(moduloVO);

			} // for modulos
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar los modulos y sus riesgos seleccionables", excepcion);
			throw new BusinessException("Se ha producido un error durante la gestion de los datos ", excepcion);
		}

		return modulosVO;
	}

	private ArrayList<CaracteristicaRiesgoVO> reordenarCaracteristicaRiesgoVO(
			ArrayList<CaracteristicaRiesgoVO> listaOriginal) {
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
			if (!enco)
				listAux.add(aa);
		}

		return listAux;
	}

	public OperationResultVO saveOrUpdateParcela(Long lineaSeguroId, ParcelaVO parcelaVO, String operacion) {
		OperationResultVO operationResultVO = new OperationResultVO();
		Parcela parcela = null;

		try {

			if (operacion.equals(MODIFICAR_PARCELA)) {

				parcela = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class, new Long(parcelaVO.getCodParcela()));

			} else {
				parcela = new Parcela();
			}

			generateParcelaAnexo(parcelaVO, parcela, operacion, lineaSeguroId);

			operationResultVO = isParcelaOk(parcela, lineaSeguroId);

			if (operationResultVO.getMessageErrors().size() == 0) {
				if (operacion.equals(MODIFICAR_PARCELA)) {
					if (parcela.getTipomodificacion() == null) {
						parcela.setTipomodificacion(' ');
					}
					if (parcela.getTipomodificacion().equals(ALTA)) {
						parcela.setTipomodificacion(ALTA);
					} else {
						parcela.setTipomodificacion(MODIFICAR);
					}
				}
			}
			if (operacion.equals(ALTA_PARCELA)) {
				parcela.setTipomodificacion(ALTA);
				parcela.setParcela(null);
			}
			if (!operacion.equals(ALTA_ESTRUCTURA_PARCELA)
					&& !operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
				parcela = (Parcela) datosParcelaAnexoDao.saveOrUpdate(parcela);
				operationResultVO.setCodNuevaParcela(parcela.getId().toString());
			}
			// Si se esta modificando una instalacion y el tipo de modificacion viene
			// informado desde la pantalla de parcela
			if (operacion.equals(MODIFICAR_INSTALACION_PARCELA)
					&& !StringUtils.nullToString(parcelaVO.getTipoModificacion()).equals("")) {

				Parcela instaMod = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class,
						new Long(parcelaVO.getCodParcela()));
				datosParcelaAnexoDao.evict(instaMod);

				if ("A".equals(parcelaVO.getTipoModificacion()))
					instaMod.setTipomodificacion('A');
				else
					instaMod.setTipomodificacion('M');

				instaMod = (Parcela) datosParcelaAnexoDao.saveOrUpdate(instaMod);
				operationResultVO.setCodNuevaParcela(instaMod.getId().toString());
			}

			// Guarda las coberturas elegibles de la parcela
			try {
				Set<CapitalAsegurado> ca = parcela.getCapitalAsegurados();
				for (CapitalAsegurado capitalAsegurado : ca) {
					setCoberturas(lineaSeguroId, parcelaVO, ALTA_PARCELA, parcela, capitalAsegurado);
				}
			} catch (Exception e) {
				logger.error("Error al guardar las coberturas elegibles de la parcela", e);
			}

			// Mensajes de informacion de operacion
			if (operacion.equals(MODIFICAR_PARCELA))
				operationResultVO.getMessageOk().add("Modificacion parcela realizada.");
			else if (operacion.equals(REPLICAR_PARCELA))
				operationResultVO.getMessageOk().add("Replicacion parcela realizada.");
			else if (operacion.equals(ALTA_ESTRUCTURA_PARCELA))
				operationResultVO.getMessageOk().add("Alta estructura realizada.");
			else if (operacion.equals(MODIFICAR_INSTALACION_PARCELA))
				operationResultVO.getMessageOk().add("Modificacion estructura realizada.");
			else
				operationResultVO.getMessageOk().add("Operacion realizada.");

		} catch (Exception excepcion) {
			logger.error(ALTA_ACTUALIZAR_REPLICAR_KO, excepcion);
			operationResultVO.getMessageErrors().add(ALTA_ACTUALIZAR_REPLICAR_KO);
		}
		return operationResultVO;

	}

	private CapitalDTSVariable generateNewCapitalDTSVariable(String valor, BigDecimal codconcepto,
			CapitalAsegurado capitalAsegurado, Character tipomodificacion, CapitalDTSVariable capitalDTSVariable) {
		if (capitalDTSVariable == null) {
			capitalDTSVariable = new CapitalDTSVariable();
		}
		capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
		capitalDTSVariable.setCodconcepto(codconcepto);
		capitalDTSVariable.setValor(valor);
		capitalDTSVariable.setTipomodificacion(tipomodificacion);
		return capitalDTSVariable;
	}

	/**
	 * 
	 * @param parcelaVO
	 * @param idLineaSeguroId
	 * @param parcela
	 * @param isElegido
	 *            --> -1(elegido), -2(no elegido)
	 * @return
	 */
	private Set<ParcelaCobertura> getRiesgosElegibles(ParcelaVO parcelaVO, Long idLineaSeguroId, Parcela parcela,
			String isElegido) {

		Set<ParcelaCobertura> coberturasParcela = new HashSet<ParcelaCobertura>(0);
		
		try {
			AnexoModificacion anexoModificacion = (AnexoModificacion) datosParcelaAnexoDao.get(AnexoModificacion.class,
					Long.parseLong(parcelaVO.getIdAnexoModificacion()));
			
			// codConcepto: Para riesgos 363 --> elegido: -1, no elegido: -2, otros: valor
			
			DatosModulosRiesgosVO datosModulosRiesgosVO = new DatosModulosRiesgosVO();
			datosModulosRiesgosVO.setLineaSeguroId(idLineaSeguroId.toString());
			datosModulosRiesgosVO.setParcelaVO(parcelaVO);
			datosModulosRiesgosVO.setModulos(anexoModificacion.getCodmodulo());	

			ModulosVO modulosVO = getModulosRiesgosParcela(datosModulosRiesgosVO, CON_CITRICOS); 

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
					diccionarioDatos.setCodconcepto(new BigDecimal("363"));
					parcelaCobertura.setDiccionarioDatos(diccionarioDatos);

					coberturasParcela.add(parcelaCobertura); // ADD COBERTURA
				} // for coberturas
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error al recuperar las coberturas de la parcela", ex);
		}

		return coberturasParcela;
	}

	/**
	 * @author U029769 21/06/2013
	 * @param parcelaVO
	 * @param parcela
	 * @param operacion
	 * @param lineaseguroId
	 * @throws Exception
	 */
	public void generateParcelaAnexo(ParcelaVO parcelaVO, Parcela parcela, String operacion, Long lineaseguroId)
			throws Exception {

		AnexoModificacion anexoModificacion = null;
		try {
			// cargamos el A.M
			anexoModificacion = (AnexoModificacion) datosParcelaAnexoDao.get(AnexoModificacion.class,
					Long.parseLong(parcelaVO.getIdAnexoModificacion()));
			// set del objeto parcela
			setParcela(parcelaVO, parcela, operacion, anexoModificacion);
		} catch (Exception excepcion) {
			logger.error("Error al generar las parcelas del anexo: " + excepcion);
			throw new Exception();
		}

	}

	/**
	 * @author U029769 20/06/2013
	 * @param parcelaVO
	 * @param parcela
	 * @param operacion
	 * @param anexoModificacion
	 *            Crea un objeto parcela (de A.M)con los datos que ha introducido el
	 *            usuario
	 */
	private void setParcela(ParcelaVO parcelaVO, Parcela parcela, String operacion,
			AnexoModificacion anexoModificacion) {

		// Set tipo parcela y refIdParcela
		parcela.setTipoparcela(parcelaVO.getTipoParcelaChar());
		if (Constants.TIPO_PARCELA_INSTALACION.equals(parcelaVO.getTipoParcelaChar())) {
			if ("sin valor".equals(parcelaVO.getRefIdParcela()) || !StringUtils.isNullOrEmpty(parcelaVO.getRefIdParcela()))
				parcela.setIdparcelaanxestructura(Long.valueOf((parcela.getId())));
		} 
		parcela.setAnexoModificacion(anexoModificacion);
		// Set poliza
		Poliza poliza = new Poliza();
		poliza.setIdpoliza(Long.valueOf((parcelaVO.getCodPoliza())));

		if (parcela.getIdcopyparcela() != null) {
			parcela.setIdcopyparcela(parcela.getIdcopyparcela());
		}
		if (parcela.getIdparcelaanxestructura() != null) {
			parcela.setIdparcelaanxestructura(parcela.getIdparcelaanxestructura());
		}
		// Set localizacion
		if (!StringUtils.nullToString(parcelaVO.getCodProvincia()).equals(""))
			parcela.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
		if (!StringUtils.nullToString(parcelaVO.getCodComarca()).equals(""))
			parcela.setCodcomarca(new BigDecimal(parcelaVO.getCodComarca()));
		if (!StringUtils.nullToString(parcelaVO.getCodTermino()).equals(""))
			parcela.setCodtermino(new BigDecimal(parcelaVO.getCodTermino()));
		if (!StringUtils.nullToString(parcelaVO.getCodSubTermino()).equals("")
				|| parcelaVO.getCodSubTermino().length() == 1)
			parcela.setSubtermino(parcelaVO.getCodSubTermino().charAt(0));
		// set SIGPAC
		if (!StringUtils.nullToString(parcelaVO.getProvinciaSigpac()).equals(""))
			parcela.setCodprovsigpac(new BigDecimal(parcelaVO.getProvinciaSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getTerminoSigpac()).equals(""))
			parcela.setCodtermsigpac(new BigDecimal(parcelaVO.getTerminoSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getAgregadoSigpac()).equals(""))
			parcela.setAgrsigpac(new BigDecimal(parcelaVO.getAgregadoSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getZonaSigpac()).equals(""))
			parcela.setZonasigpac(new BigDecimal(parcelaVO.getZonaSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getPoligonoSigpac()).equals(""))
			parcela.setPoligonosigpac(new BigDecimal(parcelaVO.getPoligonoSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getParcelaSigpac()).equals(""))
			parcela.setParcelasigpac(new BigDecimal(parcelaVO.getParcelaSigpac()));
		if (!StringUtils.nullToString(parcelaVO.getRecintoSigpac()).equals(""))
			parcela.setRecintosigpac(new BigDecimal(parcelaVO.getRecintoSigpac()));
		// set cultivo - variedad
		if (!StringUtils.nullToString(parcelaVO.getCultivo()).equals(""))
			parcela.setCodcultivo(new BigDecimal(parcelaVO.getCultivo()));
		if (!StringUtils.nullToString(parcelaVO.getVariedad()).equals(""))
			parcela.setCodvariedad(new BigDecimal(parcelaVO.getVariedad()));
		// set POLIGONO,PARCELA_1 & NOM.PARCELA
		if (!StringUtils.nullToString(parcelaVO.getNombreParcela()).equals(""))
			parcela.setNomparcela(parcelaVO.getNombreParcela());
		else
			parcela.setNomparcela(" ");

		// HOJA & NUMERO
		if (operacion.equals(MODIFICAR_PARCELA)) {
			// IGT 11/04/2018 --> NO SE REALIZA REASIGNACION DE NUMERO DE HOJA/PARCELA EN
			// MODIFICACIONES
		} else if (operacion.equals(ALTA_PARCELA)) {
			AnexoModificacion anexoModificacionAux = (AnexoModificacion) datosParcelaAnexoDao
					.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
			getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
		} else if (operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
			AnexoModificacion anexoModificacionAux = (AnexoModificacion) datosParcelaAnexoDao
					.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
			getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
		} else if (operacion.equals(REPLICAR_PARCELA)) {
			// JANV 06/04/2016
			// calculo de hoja-numero a replicar.
			// si se coge por defecto la hoja-num anterior, aparece errores si se ha
			// modificado la parcela
			// ya que no se recalcula segun prov,term y subterm.
			Parcela par = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class, parcela.getId());
			if (par.getCodprovincia().compareTo(parcela.getCodprovincia()) != 0
					|| par.getCodtermino().compareTo(parcela.getCodtermino()) != 0
					|| !par.getSubtermino().equals(parcela.getSubtermino())) {
				AnexoModificacion anexoModificacionAux = (AnexoModificacion) datosParcelaAnexoDao
						.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
				getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
			}
		} else {
			parcela.setHoja(new BigDecimal(-1));
			parcela.setNumero(new BigDecimal(-1));
		}
	}

	/**
	 * @author U029769 20/06/2013
	 * @param operacion
	 * @param anexoModificacion
	 *            Crea un objeto CapitalAsegurado (de A.M)con los datos que ha
	 *            introducido el usuario
	 * @param capitalAseguradoVO
	 */
	private CapitalAsegurado setCapitalesAsegurados(CapitalAseguradoVO capitalAseguradoVO) {

		BigDecimal precioMax = new BigDecimal(0);
		BigDecimal produccionMax = new BigDecimal(0);
		CapitalAsegurado capitalAsegurado = null;
		TipoCapital tipoCapital = new TipoCapital();

		capitalAsegurado = (CapitalAsegurado) datosParcelaAnexoDao.getObject(CapitalAsegurado.class,
				Long.valueOf((capitalAseguradoVO.getId())));
		// Indica el tipo de modificacion en el capital asegurado y en la parcela
		if (capitalAsegurado.getTipomodificacion() != null)
			capitalAsegurado.setTipomodificacion(MODIFICAR);
		if (!StringUtils.nullToString(capitalAseguradoVO.getCodtipoCapital()).equals(""))
			tipoCapital.setCodtipocapital(new BigDecimal(capitalAseguradoVO.getCodtipoCapital()));

		capitalAsegurado.setTipoCapital(tipoCapital);

		if (!StringUtils.nullToString(capitalAseguradoVO.getSuperficie()).equals(""))
			capitalAsegurado.setSuperficie(new BigDecimal(capitalAseguradoVO.getSuperficie()));

		if ((!StringUtils.nullToString(capitalAseguradoVO.getListPrecios()).equals(""))
				&& (capitalAseguradoVO.getListPrecios().size() > 0)) {
			// Precio (selecciono el precio maximo)
			for (PrecioVO precioVO : capitalAseguradoVO.getListPrecios()) {
				if (precioVO.getLimMax() != null) {
					if (new BigDecimal(precioVO.getLimMax()).compareTo(precioMax) > 0)
						precioMax = new BigDecimal(precioVO.getLimMax());
				} else if (precioVO.getLimMin() != null) {
					if (new BigDecimal(precioVO.getLimMin()).compareTo(precioMax) > 0)
						precioMax = new BigDecimal(precioVO.getLimMin());
				}
			}
			capitalAsegurado.setPrecio(precioMax);
			// Produccion (selecciono la produccion maxima)
			for (ProduccionVO produccionVO : capitalAseguradoVO.getListProducciones())
				if (new BigDecimal(produccionVO.getLimMax()).compareTo(produccionMax) > 0)
					produccionMax = new BigDecimal(produccionVO.getLimMax());

			capitalAsegurado.setProduccion(produccionMax);
		}
		if (!StringUtils.nullToString(capitalAseguradoVO.getPrecio()).equals(""))
			capitalAsegurado.setPrecio(new BigDecimal(capitalAseguradoVO.getPrecio()));

		if (!StringUtils.nullToString(capitalAseguradoVO.getProduccion()).equals(""))
			capitalAsegurado.setProduccion(new BigDecimal(capitalAseguradoVO.getProduccion()));

		capitalAsegurado.setTipoRdto(Constants.TIPO_RDTO_MAXIMO);

		return capitalAsegurado;
	}

	/**
	 * @author U029769 21/06/2013
	 * @param parcelaVO
	 * @param operacion
	 * @param parcela
	 * @param capitalAsegurado
	 * @param idLineaSeguroId
	 *            Set de las coberturas del capital asegurado del anexo
	 * @throws Exception
	 */
	private void setCoberturas(Long lineaseguroId, ParcelaVO parcelaVO, String operacion, Parcela parcela,
			CapitalAsegurado capitalAsegurado) throws Exception {
		
		try {

			// Obtengo TODOS los riesgos elegibles, sin condicion citricos y los pongo como
			// no elegidos
			Set<ParcelaCobertura> riesgosElegiblesParcela = getRiesgosElegibles(parcelaVO, lineaseguroId, parcela, 	"-2");

			// ----- VIENE DE LA UI (cargo la pestanha 4) -----
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
			
			
			// ----- NO VIENE DE LA UI (cargo la pestana 4) ----- dejo el set como esta
			// Los guardo como datos variables. Transformo los objetos y los anado
			for (ParcelaCobertura parcelaCobertura : riesgosElegiblesParcela) {

				CapitalDTSVariable dtVariableCobertura = new CapitalDTSVariable();
				dtVariableCobertura.setCodconcepto(new BigDecimal(363));

				if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
					dtVariableCobertura.setTipomodificacion('A');
				} else if (operacion.equals(MODIFICAR_PARCELA) || operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
					if ("A".equals(parcelaVO.getTipoModificacion()))
						dtVariableCobertura.setTipomodificacion('A');
					else
						dtVariableCobertura.setTipomodificacion('M');
				} else {
					dtVariableCobertura.setTipomodificacion('?');
				}
				dtVariableCobertura.setCodconceptoppalmod(
						Long.parseLong(parcelaCobertura.getConceptoPpalModulo().getCodconceptoppalmod().toString()));
				dtVariableCobertura.setCodriesgocubierto(
						Long.parseLong(parcelaCobertura.getRiesgoCubierto().getId().getCodriesgocubierto().toString()));
				dtVariableCobertura.setCapitalAsegurado(capitalAsegurado);

				// Comprueba si el dato variable ya existia para actualizarlo
				CapitalDTSVariable dvAux = datosParcelaAnexoDao.getDatoVariable(dtVariableCobertura);
				if (dvAux == null) {
					// No existe, inserto el que se habia rellenado
					dtVariableCobertura.setValor(parcelaCobertura.getCodvalor().toString());
					datosParcelaAnexoDao.saveOrUpdate(dtVariableCobertura);
				}
				// Existe, actualizo el que hay en bbdd
				else {
					dvAux.setValor(parcelaCobertura.getCodvalor().toString());
					dvAux.setTipomodificacion(dtVariableCobertura.getTipomodificacion());
					datosParcelaAnexoDao.saveOrUpdate(dvAux);
				}

			}
			
			/* Cargamos los datos Variables de las coberturas de las parcelas elegibles */
			for (RiesgoVO riesgo: parcelaVO.getRiesgosSeleccionados()) {
				
				if (!riesgo.getCodConcepto().equals("363")){
					CapitalDTSVariable dtVariableCobertura = new CapitalDTSVariable();
					dtVariableCobertura.setCodconcepto(new BigDecimal(riesgo.getCodConcepto()));
	
					if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
						dtVariableCobertura.setTipomodificacion('A');
					} else if (operacion.equals(MODIFICAR_PARCELA) || operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
						if ("A".equals(parcelaVO.getTipoModificacion()))
							dtVariableCobertura.setTipomodificacion('A');
						else
							dtVariableCobertura.setTipomodificacion('M');
					} else {
						dtVariableCobertura.setTipomodificacion('?');
					}
					dtVariableCobertura.setCodconceptoppalmod(Long.parseLong(riesgo.getCodConceptoPpalMod()));
					dtVariableCobertura.setCodriesgocubierto(Long.parseLong(riesgo.getCodRiesgoCubierto()));
					dtVariableCobertura.setCapitalAsegurado(capitalAsegurado);
	
					// Comprueba si el dato variable ya existia para actualizarlo
					CapitalDTSVariable dvAux = datosParcelaAnexoDao.getDatoVariable(dtVariableCobertura);
					if (dvAux == null) {
						// No existe, inserto el que se habia rellenado
						dtVariableCobertura.setValor(riesgo.getCodValor());
						datosParcelaAnexoDao.saveOrUpdate(dtVariableCobertura);
					}else {
					// Existe, actualizo el que hay en bbdd
						if (parcelaVO.getCapitalAsegurado().getId().equals(dvAux.getCapitalAsegurado().getId().toString())) {
							dvAux.setValor(riesgo.getCodValor());
							dvAux.setTipomodificacion(dtVariableCobertura.getTipomodificacion());
							datosParcelaAnexoDao.saveOrUpdate(dvAux);
						}		
					}
				}	
				
			}
			
		} catch (Exception excepcion) {
			logger.error("Error en setCoberturas del  anexo: " + excepcion);
			throw new Exception();
		}
	}

	/**
	 * @author U029769 21/06/2013
	 * @throws Exception
	 */
	private void setDatosVariables(CapitalAseguradoVO capitalAseguradoVO, String operacion, ParcelaVO parcelaVO,
			CapitalAsegurado capitalAsegurado) throws Exception {

		CapitalDTSVariable datoVariableParcela1 = null;
		try {

			List<Integer> listaConceptosNoBorrar = new ArrayList<Integer>();

			for (DatoVariableParcelaVO datoVariableParcelaVO : capitalAseguradoVO.getDatosVariablesParcela()) {				

				if (datoVariableParcelaVO.getValor() != null && !datoVariableParcelaVO.getValor().equals("")
						&& datoVariableParcelaVO.getCodconcepto() != null) {
					
					listaConceptosNoBorrar.add(datoVariableParcelaVO.getCodconcepto());

					StringTokenizer tokens = new StringTokenizer(datoVariableParcelaVO.getValor(), ";");

					// si es una modificacion
					if (operacion.equals(MODIFICAR_PARCELA)) {
						// Si el id de dato variable viene informado
						if (datoVariableParcelaVO.getIdDatoVariable() != null
								&& datoVariableParcelaVO.getIdDatoVariable() > 0) {
							datoVariableParcela1 = (CapitalDTSVariable) datosParcelaAnexoDao
									.getObject(CapitalDTSVariable.class, datoVariableParcelaVO.getIdDatoVariable());
						}
						// Si no, se obtiene el DV con el id de capital asegurado y el codigo de
						// concepto
						else {
							datoVariableParcela1 = datosParcelaAnexoDao.getDatoVariable(
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()),
									new Long(capitalAseguradoVO.getId()));
						}
					}

					// Si es un alta de DV
					if (!operacion.equals(MODIFICAR_PARCELA) || datoVariableParcela1 == null) {
						datoVariableParcela1 = new CapitalDTSVariable();
						datoVariableParcela1.setCapitalAsegurado(capitalAsegurado);
					}

					// si es lista multiple
					if (tokens.countTokens() > 1) {
						// lista multiple de medidas preventivas, caso especial
						if (datoVariableParcelaVO.getCodconcepto().equals(new Integer(124))) {
							String valor = "";
							while (tokens.hasMoreTokens()) {
								String valueItem = tokens.nextToken();
								valor = valor + valueItem + " ";
							}
							if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
								datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
										null);
							} else if (operacion.equals(MODIFICAR_PARCELA)
									|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
								if ("A".equals(parcelaVO.getTipoModificacion()))
									datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'A', null);
								else
									datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'M', datoVariableParcela1);
							} else {
								datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, '?',
										null);
							}

							datosParcelaAnexoDao.saveOrUpdate(datoVariableParcela1);
						} else {
							// add n datos Variables
							while (tokens.hasMoreTokens()) {
								String valueItem = tokens.nextToken();

								if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
									datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'A', null);
								} else if (operacion.equals(MODIFICAR_PARCELA)
										|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
									if ("A".equals(parcelaVO.getTipoModificacion()))
										datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
												new BigDecimal(datoVariableParcelaVO.getCodconcepto()),
												capitalAsegurado, 'A', null);
									else
										datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
												new BigDecimal(datoVariableParcelaVO.getCodconcepto()),
												capitalAsegurado, 'M', datoVariableParcela1);
								} else {
									datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'?', null);
								}
								datoVariableParcela1.setCapitalAsegurado(capitalAsegurado);
								datosParcelaAnexoDao.saveOrUpdate(datoVariableParcela1);
								// capitalAsegurado.getCapitalDTSVariables().add(datoVariableParcela1);
							}
						}
					} else {
						// si NO es lista multiple
						if (datoVariableParcelaVO.getCodconcepto() != 363) {

							if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
								datoVariableParcela1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
										null);
							} else if (operacion.equals(MODIFICAR_PARCELA)
									|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
								if ("A".equals(parcelaVO.getTipoModificacion()))
									datoVariableParcela1 = generateNewCapitalDTSVariable(
											datoVariableParcelaVO.getValor(),
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'A', null);
								else
									datoVariableParcela1 = generateNewCapitalDTSVariable(
											datoVariableParcelaVO.getValor(),
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'M', datoVariableParcela1);
							} else {
								datoVariableParcela1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, '?',
										null);
							}
							datoVariableParcela1.setCapitalAsegurado(capitalAsegurado);
							datosParcelaAnexoDao.saveOrUpdate(datoVariableParcela1);
						}
					}
				} // if
			} // for datos variables

			// Borra los datos variables asociados al capital asegurado cuyo codigo de
			// concepto no venga en el listado
			// Anhadimos a los conceptos que no hay que borrar el 363, que corresponde a
			// las coberturas elegibles
			listaConceptosNoBorrar.add(new Integer(363));
			datosParcelaAnexoDao.borrarDatosVariables(capitalAseguradoVO.getId(), listaConceptosNoBorrar);

		} catch (Exception excepcion) {
			logger.error("Error al generar los datos variables del anexo: " + excepcion);
			throw new Exception();
		}
	}

	public CapitalAsegurado generateCapitalAseguradoDeAnexo(CapitalAseguradoVO capitalAseguradoVO, Parcela parcela,
			String operacion, ParcelaVO parcelaVO) {
		// El que pertenecera al objeto parcela
		CapitalAsegurado capitalAsegurado = new CapitalAsegurado();
		// auxiliar

		if (capitalAseguradoVO.getId() != null && !"".equals(capitalAseguradoVO.getId())) {
			capitalAsegurado.setId(new Long(capitalAseguradoVO.getId()));
		}

		capitalAsegurado.setParcela(parcela);
		TipoCapital tipoCapital = new TipoCapital();

		if (capitalAseguradoVO.getCodtipoCapital() != null && !capitalAseguradoVO.getCodtipoCapital().equals(""))
			tipoCapital.setCodtipocapital(new BigDecimal(capitalAseguradoVO.getCodtipoCapital()));

		capitalAsegurado.setTipoCapital(tipoCapital);

		if (capitalAseguradoVO.getSuperficie() != null && !capitalAseguradoVO.getSuperficie().equals(""))
			capitalAsegurado.setSuperficie(new BigDecimal(capitalAseguradoVO.getSuperficie()));

		if (capitalAseguradoVO.getListPrecios() != null && capitalAseguradoVO.getListPrecios().size() > 0) {
			// Precio (selecciono el precio maximo)
			BigDecimal precioMax = new BigDecimal(0);

			for (PrecioVO precioVO : capitalAseguradoVO.getListPrecios()) {
				if (precioVO.getLimMax() != null) {
					if (new BigDecimal(precioVO.getLimMax()).compareTo(precioMax) > 0)
						precioMax = new BigDecimal(precioVO.getLimMax());
				} else if (precioVO.getLimMin() != null) {
					if (new BigDecimal(precioVO.getLimMin()).compareTo(precioMax) > 0)
						precioMax = new BigDecimal(precioVO.getLimMin());
				}
			}

			capitalAsegurado.setPrecio(precioMax);
			// Produccion (selecciono la produccion maxima)
			BigDecimal produccionMax = new BigDecimal(0);

			for (ProduccionVO produccionVO : capitalAseguradoVO.getListProducciones())
				if (new BigDecimal(produccionVO.getLimMax()).compareTo(produccionMax) > 0)
					produccionMax = new BigDecimal(produccionVO.getLimMax());

			capitalAsegurado.setProduccion(produccionMax);
		}
		
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

		// -------------------------- DATOS VARIABLES --------------------------

		for (DatoVariableParcelaVO datoVariableParcelaVO : capitalAseguradoVO.getDatosVariablesParcela()) {
			if (datoVariableParcelaVO.getValor() != null && !datoVariableParcelaVO.getValor().equals("")
					&& datoVariableParcelaVO.getCodconcepto() != null) {

				StringTokenizer tokens = new StringTokenizer(datoVariableParcelaVO.getValor(), ";");

				// si ES lista multiple
				if (tokens.countTokens() > 1) {
					// lista multiple de medidas preventivas, caso especial
					if (datoVariableParcelaVO.getCodconcepto().equals(new Integer(124))) {
						String valor = "";
						while (tokens.hasMoreTokens()) {
							String valueItem = tokens.nextToken();
							valor = valor + valueItem + " ";
						}

						CapitalDTSVariable datoVariableParcela1 = null;

						if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
							datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
									null);
						} else if (operacion.equals(MODIFICAR_PARCELA)
								|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
							if ("A".equals(parcelaVO.getTipoModificacion()))
								datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
										null);
							else
								datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'M',
										datoVariableParcela1);
						} else {
							datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, '?',
									null);
						}

						capitalAsegurado.getCapitalDTSVariables().add(datoVariableParcela1);
					} else {
						// add n datos Variables
						while (tokens.hasMoreTokens()) {
							String valueItem = tokens.nextToken();

							CapitalDTSVariable datoVariableParcela1 = null;
							if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA)) {
								datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
										null);
							} else if (operacion.equals(MODIFICAR_PARCELA)
									|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
								if ("A".equals(parcelaVO.getTipoModificacion()))
									datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'A', null);
								else
									datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
											new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado,
											'M', datoVariableParcela1);
							} else {
								datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, '?',
										null);
							}

							capitalAsegurado.getCapitalDTSVariables().add(datoVariableParcela1);
						}
					}
				}

				// si NO es lista multiple
				else {
					if (datoVariableParcelaVO.getCodconcepto() != 363) {

						CapitalDTSVariable dtVariable1 = null;

						if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA))
							dtVariable1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
									null);
						else if (operacion.equals(MODIFICAR_PARCELA)
								|| operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
							if ("A".equals(parcelaVO.getTipoModificacion()))
								dtVariable1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
										null);
							else
								dtVariable1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
										new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'M',
										dtVariable1);
						} else
							dtVariable1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, '?',
									null);

						capitalAsegurado.getCapitalDTSVariables().add(dtVariable1);
					}
				}
			} // if
		} // for datos variables

		if (operacion.equals(ALTA_PARCELA) || operacion.equals(ALTA_ESTRUCTURA_PARCELA))
			capitalAsegurado.setTipomodificacion('A');
		else if (operacion.equals(MODIFICAR_PARCELA) || operacion.equals(MODIFICAR_INSTALACION_PARCELA)) {
			if ("A".equals(parcelaVO.getTipoModificacion()))
				capitalAsegurado.setTipomodificacion('A');
			else
				capitalAsegurado.setTipomodificacion('M');
		} else {
			capitalAsegurado.setTipomodificacion('?');
		}
		capitalAsegurado.setTipoRdto(Constants.TIPO_RDTO_MAXIMO);
		return capitalAsegurado;
	}

	public LocalCultVarVO getLocalCultVar(SigpacVO sigpacVO) {
		LocalCultVarVO localCultVarVO = new LocalCultVarVO();

		try {
			localCultVarVO = datosParcelaAnexoDao.getLocalCultVar(sigpacVO);
		} catch (Exception excepcion) {
			logger.error(excepcion.getMessage());
			localCultVarVO = null;
		}

		return localCultVarVO;
	}

	/**
	 * DAA 14/03/2013 Asignamos las Hojas y Numeros segun la clave provincia,
	 * termino y subtermino para cada una tenemos una hoja
	 */
	public void getHojaNumero(AnexoModificacion anexoModificacionAux, Parcela newParcela, ParcelaVO parcelaVO,
			String operacion) {

		Map<Object, Object> maxNumPorHoja = new HashMap<Object, Object>();
		// hoja,maxNumero
		Map<Object, Object> claveTerminoPorHoja = new HashMap<Object, Object>();
		// claveTermino, hoja
		String claveTermNewParcela = "";
		String codParcelaVO = "";
		int mayorHoja = 0;
		boolean nuevaHoja = false;

		try {

			if (parcelaVO != null) {
				codParcelaVO = StringUtils.nullToString(parcelaVO.getCodParcela());
			}
			if (operacion.equals(REPLICAR_PARCELA)) {
				maxNumPorHoja = parcelaModificacionPolizaDao.getMaxNumPorHoja(anexoModificacionAux.getId(), null);
			} else {
				maxNumPorHoja = parcelaModificacionPolizaDao.getMaxNumPorHoja(anexoModificacionAux.getId(),
						codParcelaVO);
			}

			claveTerminoPorHoja = parcelaModificacionPolizaDao.getClaveTerminoPorHoja(anexoModificacionAux.getId(),
					codParcelaVO);

			// ***** PARCELA NUEVA *****
			claveTermNewParcela = newParcela.getCodprovincia().toString().trim() + "|"
					+ newParcela.getCodtermino().toString().trim() + "|" + newParcela.getSubtermino();

			// una vez recuperado que hoja le corresponde a claveTermNewParcela
			if (claveTerminoPorHoja.containsKey(claveTermNewParcela)) {
				BigDecimal hoja = (BigDecimal) claveTerminoPorHoja.get(claveTermNewParcela);

				// buscamos para esa hoja el maximo de numero que existe y anhadimos uno mas
				if (maxNumPorHoja.containsKey(hoja)) {
					int maxNumero = Integer.parseInt(maxNumPorHoja.get(hoja).toString());
					if (maxNumero != Constants.MAX_NUM_HOJA) {
						newParcela.setHoja(hoja);
						newParcela.setNumero(new BigDecimal(maxNumero + 1));
					} else {
						// si el maxNumero es el maximo permitido crearemos otra hoja
						nuevaHoja = true;
					}
				}
			} else {
				// nueva hoja
				nuevaHoja = true;
			}

			if (nuevaHoja) {
				// buscamos cual es la ultima hoja que se ha creado entre las claves de
				// maxNumPorHoja
				Set<Object> todasHojas = maxNumPorHoja.keySet();
				Iterator<Object> iter = todasHojas.iterator();

				while (iter.hasNext()) {
					String hojaComprobar = iter.next().toString();
					if (Integer.parseInt(hojaComprobar) > mayorHoja) {
						mayorHoja = Integer.parseInt(hojaComprobar);
					}
				}
				newParcela.setHoja(new BigDecimal(mayorHoja + 1));
				newParcela.setNumero(new BigDecimal(1));
			}

		} catch (DAOException e) {
			logger.error("error al establecer hoja y numero de la nueva parcela " + e);
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------
	// GENERATE PANTALLA CONFIGURABLE VO
	// --------------------------------------------------------------------
	private PantallaConfigurableVO getPantallaConfigurableVO(Long lineaseguroId,
			DatosPantallaConfigurableVO datosPantalla) {

		PantallaConfigurableVO pantallaConfigurableVO = new PantallaConfigurableVO();

		try {
			Long idPantallaConfigurable = datosParcelaAnexoDao.getIdPantallaConfigurable(
					new Long(datosPantalla.getCodLinea()), new Long(datosPantalla.getCodPlan()),
					new Long(datosPantalla.getIdPantalla()));
			PantallaConfigurable pantallaConfigurable = datosParcelaAnexoDao
					.getPantallaConfigurada(idPantallaConfigurable);

			if (pantallaConfigurable != null) {
				pantallaConfigurableVO.setIdPantalla(pantallaConfigurable.getPantalla().getIdpantalla().intValue());
				pantallaConfigurableVO
						.setIdPantallaConfigurable(pantallaConfigurable.getIdpantallaconfigurable().intValue());

				List<ConfiguracionCampo> listPantallas = datosParcelaAnexoDao
						.getListConfigCampos(new BigDecimal(idPantallaConfigurable));
				pantallaConfigurableVO.setListCampos(getListPantallaConfigurableVO(listPantallas));

				datosPantalla.setLineaSeguroId(lineaseguroId.toString());
				// Modulos de la poliza
				Set<String> modulos = new HashSet<String>(
						datosParcelaAnexoDao.getModulosPoliza(new Long(datosPantalla.getIdPoliza())));
				modulos.add("99999");

				// Codconceptos filtrados por las distintas mascaras.
				Set<BigDecimal> listaMFCA = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getMascaraFCA(datosPantalla, new ArrayList(modulos)));
				Set<BigDecimal> listaMGT = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getMascaraGT(datosPantalla, new ArrayList(modulos)));
				Set<BigDecimal> listaMLR = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getMascaraLRDTO(datosPantalla, new ArrayList(modulos)));
				Set<BigDecimal> listaMP = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getMascaraP(datosPantalla, new ArrayList(modulos)));
				// Codconceptos que son obligatorios aunque los filtre la mascara
				Set<BigDecimal> listaOblig = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getConceptosObligatorios(idPantallaConfigurable));

				// lista de todos los codconceptos que se mostraran
				Set<BigDecimal> mascara = new HashSet<BigDecimal>();
				mascara.addAll(listaMFCA);
				mascara.addAll(listaMGT);
				mascara.addAll(listaMLR);
				mascara.addAll(listaMP);
				// Codconcetos campos relacionados (Campos Mascara)
				Set<BigDecimal> listaCampos = new HashSet<BigDecimal>(
						datosParcelaAnexoDao.getConceptosRelacionados(new ArrayList(mascara)));
				mascara.addAll(listaCampos);
				mascara.addAll(listaOblig);
				pantallaConfigurableVO.setListCodConceptosMascaras(new ArrayList<BigDecimal>(mascara));
			}
		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al recuperar los datos de la pantalla configurable", excepcion);
		}
		return pantallaConfigurableVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private List<CampoPantallaConfigurableVO> getListPantallaConfigurableVO(List<ConfiguracionCampo> listPantallas) {

		List<CampoPantallaConfigurableVO> listConfiguracionCampo = new ArrayList<CampoPantallaConfigurableVO>();

		for (ConfiguracionCampo configuracionCampo : listPantallas) {

			CampoPantallaConfigurableVO campoPantallaConfigurableVO = new CampoPantallaConfigurableVO();

			campoPantallaConfigurableVO.setAlto(configuracionCampo.getAlto().intValue());
			campoPantallaConfigurableVO.setAncho(configuracionCampo.getAncho().intValue());
			campoPantallaConfigurableVO.setX(configuracionCampo.getX().intValue());
			campoPantallaConfigurableVO.setY(configuracionCampo.getY().intValue());
			campoPantallaConfigurableVO.setEtiqueta(configuracionCampo.getEtiqueta());
			campoPantallaConfigurableVO.setIdtipo(configuracionCampo.getTipoCampo().getIdtipo().intValue());
			campoPantallaConfigurableVO.setDescripcion_tipo(configuracionCampo.getTipoCampo().getDesctipo());

			if (configuracionCampo.getMostrarsiempre() != null && configuracionCampo.getMostrarsiempre().equals('S'))
				campoPantallaConfigurableVO.setMostrar("Si");
			else
				campoPantallaConfigurableVO.setMostrar("No");

			if (configuracionCampo.getDisabled() != null && configuracionCampo.getDisabled().equals('S'))
				campoPantallaConfigurableVO.setDeshabilitado("Si");
			else
				campoPantallaConfigurableVO.setDeshabilitado("No");

			campoPantallaConfigurableVO.setTamanio(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getLongitud().intValue());
			campoPantallaConfigurableVO.setCodConcepto(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getCodconcepto().intValue());
			campoPantallaConfigurableVO
					.setDescripcion(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());

			if (configuracionCampo.getOrigenDatos() != null)
				campoPantallaConfigurableVO
						.setIdorigendedatos(configuracionCampo.getOrigenDatos().getIdorigendatos().intValue());

			campoPantallaConfigurableVO
					.setIdpantallaconfigurable(configuracionCampo.getId().getIdpantallaconfigurable().intValue());
			campoPantallaConfigurableVO.setIdseccion(configuracionCampo.getSeccion().getIdseccion().intValue());
			campoPantallaConfigurableVO
					.setNombre(configuracionCampo.getPantallaConfigurable().getPantalla().getDescpantalla());

			// campoPantallaConfigurableVO.setTabla_asociada("sin hacer");

			campoPantallaConfigurableVO.setUbicacion_codigo(
					configuracionCampo.getOrganizadorInformacion().getUbicacion().getCodubicacion().intValue());
			campoPantallaConfigurableVO.setUbicacion_descripcion(
					configuracionCampo.getOrganizadorInformacion().getUbicacion().getDesubicacion());

			campoPantallaConfigurableVO.setCodTipoNaturaleza(configuracionCampo.getOrganizadorInformacion()
					.getDiccionarioDatos().getTipoNaturaleza().getCodtiponaturaleza().intValue());
			campoPantallaConfigurableVO.setDecimales(
					configuracionCampo.getOrganizadorInformacion().getDiccionarioDatos().getDecimales().intValue());
			listConfiguracionCampo.add(campoPantallaConfigurableVO);
		}
		return listConfiguracionCampo;
	}

	// ---------------------------- CREATE VO's --------------------------------
	/**
	 * 
	 * @param
	 * @return
	 */
	private List<ProvinciaVO> getListProvinciasVO(List<AmbitoAsegurable> listProvincias) {
		List<ProvinciaVO> listProvinciasVO = new ArrayList<ProvinciaVO>();
		ProvinciaVO provinciaVO = null;
		for (AmbitoAsegurable ambitoAseg : listProvincias) {
			provinciaVO = new ProvinciaVO(ambitoAseg.getProvincia().getCodprovincia().intValue(),
					ambitoAseg.getProvincia().getNomprovincia());
			listProvinciasVO.add(provinciaVO);
		}
		return listProvinciasVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private List<ComarcaVO> getListComarcasVO(List<Comarca> listComarcas) {
		List<ComarcaVO> listComarcasVO = new ArrayList<ComarcaVO>();
		ComarcaVO comarcaVO = null;

		for (Comarca comarca : listComarcas) {
			comarcaVO = new ComarcaVO();
			comarcaVO.setCodComarca(comarca.getId().getCodcomarca().intValue());
			comarcaVO.setNomComarca(comarca.getNomcomarca());

			listComarcasVO.add(comarcaVO);
		}
		return listComarcasVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private List<SubterminoVO> getListSubterminosVO(List<VistaTerminosAsegurable> listSubterminos) {
		List<SubterminoVO> listSubterminosVO = new ArrayList<SubterminoVO>();
		SubterminoVO subterminoVO = null;

		for (VistaTerminosAsegurable vistaAmbito : listSubterminos) {
			subterminoVO = new SubterminoVO();

			// set provincia
			subterminoVO.setCodProvincia(vistaAmbito.getId().getCodprovincia().intValue());
			subterminoVO.setNomProvincia(vistaAmbito.getId().getNomprovincia());
			// set comarca
			if (vistaAmbito.getId().getCodcomarca() != null) {

				subterminoVO.setCodComarca(vistaAmbito.getId().getCodcomarca().intValue());
				subterminoVO.setNomComarca(vistaAmbito.getId().getNomcomarca());
			} else {
				subterminoVO.setCodComarca(-1);
				subterminoVO.setNomComarca("");
			}

			// set termino
			subterminoVO.setCodTermino(vistaAmbito.getId().getCodtermino().intValue());
			subterminoVO.setNomTermino((vistaAmbito.getId().getNomtermino()));
			// set subtermino
			subterminoVO.setSubTermino(vistaAmbito.getId().getSubtermino().toString());

			listSubterminosVO.add(subterminoVO);
		}
		return listSubterminosVO;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private List<CultivoVO> getListCultivosVO(List<Cultivo> listCultivos) {
		List<CultivoVO> listCultivoVO = new ArrayList<CultivoVO>();
		CultivoVO cultivoVO = null;

		for (Cultivo cultivo : listCultivos) {
			cultivoVO = new CultivoVO();
			// set cultivo
			cultivoVO.setCodCultivo(cultivo.getId().getCodcultivo().intValue());
			cultivoVO.setDesCultivo(cultivo.getDescultivo());
			// add object
			listCultivoVO.add(cultivoVO);
		}
		return listCultivoVO;
	}

	/**
	 * SAVE_CAPITAL_ASEGURADO : ALTA --> si es el primer capital asegurado crear una
	 * parcela y darlo de alta, si es el segundo darlo de alta. EDIT --> anhadir el
	 * nuevo o modificar el modificado.
	 * 
	 * @throws Exception
	 */
	public void saveCapitalAsegurado(Long lineaseguroId, ParcelaVO parcelaVO,
			CapitalAseguradoVO capitalAseguradoVO, String operacionCapital, String operacionParcela) {
		CapitalAsegurado capitalAsegurado = null;
		Long codParcela = null;
		Parcela newParcela = new Parcela();
		Parcela parcela = new Parcela();
		Long idNewCapitalAsegurado = null;
		try {
			// -------- ALTA NUEVO CAPITAL --------
			if (operacionCapital.equals(ALTA_CAPITAL_ASEGURADO)) {
				if (parcelaVO.getCodParcela() == null || parcelaVO.getCodParcela().equals("")
						|| parcelaVO.getCodParcela().equals("0") || parcelaVO.getCodParcela().equals("-1")) {
					// NO EXISTE PARCELA
					newParcela = generateParcelaBase(parcelaVO); // creo nueva parcela
					newParcela.setTipomodificacion(ALTA);
					newParcela.setParcela(null);
					codParcela = datosParcelaAnexoDao.saveObjectParcela(newParcela); // add la nueva parcela
					
					/* modif tam - necesito el codigo de Parcela para actualizar la llamada al SW de Modulos y Coberturas*/
					parcelaVO.setCodParcela(Long.toString(codParcela));
				} else {
					// YA EXISTE PARCELA
					codParcela = new Long(parcelaVO.getCodParcela());
				}
				// -------- ALTA --------
				parcela = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class, codParcela);
				capitalAsegurado = generateCapitalAseguradoDeAnexo(capitalAseguradoVO, parcela, operacionParcela,
						parcelaVO);
				idNewCapitalAsegurado = datosParcelaAnexoDao.saveCapitalAsegurado(capitalAsegurado);
				if (parcelaVO.getCapitalesAsegurados().size() == 0)
					parcelaVO.getCapitalesAsegurados().add(capitalAseguradoVO);
				setCoberturas(lineaseguroId, parcelaVO, ALTA_PARCELA, parcela, capitalAsegurado);
			}
			// -------- MODIFICACION CAPITAL --------
			else if (operacionCapital.equals(MODIFICAR_CAPITAL_ASEGURADO)) {
				// Crea el objeto correspondiente al capital asegurado y lo actualiza en bbdd
				capitalAsegurado = setCapitalesAsegurados(capitalAseguradoVO);
				datosParcelaAnexoDao.updateCapitalAsegurado(capitalAsegurado);
				// Modifica los datos variables
				setDatosVariables(capitalAseguradoVO, MODIFICAR_PARCELA, parcelaVO, capitalAsegurado);
				// Modifica las coberturas elegibles
				setCoberturas(lineaseguroId, parcelaVO, MODIFICAR_PARCELA, parcela, capitalAsegurado);				
				idNewCapitalAsegurado = capitalAsegurado.getId();
				codParcela = new Long(parcelaVO.getCodParcela());
				// Marca la parcela como modificada si no lo esta ya
				parcela = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class, codParcela);
				if (parcela.getTipomodificacion() == null) {
					parcela.setTipomodificacion(MODIFICAR);
					datosParcelaAnexoDao.saveOrUpdateParcela(parcela);
				}
			}
			// PASAMOS AL VO LAS DESCRIPCIONES DE LOS CAMPOS QUE SE HAN PODIDO 
			// INTRODUCIR DE FORMA MANUAL (MENOS LOS CAMPOS VARIABLES) 
			try {
				TerminoId terminoId = new TerminoId();
				terminoId.setCodprovincia(parcela.getCodprovincia());
				terminoId.setCodcomarca(parcela.getCodcomarca());
				terminoId.setCodtermino(parcela.getCodtermino());
				terminoId.setSubtermino(parcela.getSubtermino());
				Termino termino = (Termino) this.datosParcelaAnexoDao.getObject(Termino.class, terminoId);
				if (termino != null) {
					parcelaVO.setDesProvincia(termino.getProvincia().getNomprovincia());
					parcelaVO.setDesComarca(termino.getComarca().getNomcomarca());
					
					// Se recupera una instancia específica de la entidad "Linea" a través del DAO a partir del lineaseguroid
					com.rsi.agp.dao.tables.poliza.Linea linea = lineaDao.getLinea(lineaseguroId.toString());
					// Obtenemos la fecha de fin de contratación.
					Date fechaInicioContratacion = linea.getFechaInicioContratacion();
					// Utiliza el método getNomTermino(fechaInicioContratacion, esGanado) en lugar del antiguo getNomtermino() para adaptarse a los nuevos requisitos de la P0079469
					// Esta versión ahora tiene en cuenta la fecha de inicio de contratación y si la línea es de ganado para determinar el nombre correcto del termino
					parcelaVO.setDesTermino(termino.getNomTerminoByFecha(fechaInicioContratacion, false));
				}
			} catch (Exception e) {
				// La ubicacion no existe
				logger.error(e);
			}
			try {
				TipoCapital tipoCapital = (TipoCapital) this.datosParcelaAnexoDao.getObject(TipoCapital.class,
						capitalAsegurado.getTipoCapital().getCodtipocapital());
				capitalAseguradoVO.setDesTipoCapital(tipoCapital.getDestipocapital());
			} catch (Exception e) {
				// El tipo de capital
				logger.error(e);
			}			
		} catch (Exception ex) {
			logger.error("Error al guardar el capital asegurado", ex);
		}

		capitalAseguradoVO.setId(idNewCapitalAsegurado != null ? idNewCapitalAsegurado.toString() : "");
	}

	public Parcela generateParcelaBase(ParcelaVO parcelaVO) {
		Parcela parcela = new Parcela();
		AnexoModificacion anexoModificacion = new AnexoModificacion();

		try {
			anexoModificacion = (AnexoModificacion) datosParcelaAnexoDao.get(AnexoModificacion.class,
					Long.parseLong(parcelaVO.getIdAnexoModificacion()));
		} catch (Exception excepcion) {
			logger.error("Error al generar la parcela base", excepcion);
		}
		parcela.setAnexoModificacion(anexoModificacion);

		// Set tipo parcela
		parcela.setTipoparcela(parcelaVO.getTipoParcelaChar());		
		
		// Set poliza
		Poliza poliza = new Poliza();
		poliza.setIdpoliza(new Long(parcelaVO.getCodPoliza()));
		// Set parcela

		// Set localizacion
		if (parcelaVO.getCodProvincia() != null && !parcelaVO.getCodProvincia().equals(""))
			parcela.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
		if (parcelaVO.getCodComarca() != null && !parcelaVO.getCodComarca().equals(""))
			parcela.setCodcomarca(new BigDecimal(parcelaVO.getCodComarca()));
		if (parcelaVO.getCodTermino() != null && !parcelaVO.getCodComarca().equals(""))
			parcela.setCodtermino(new BigDecimal(parcelaVO.getCodTermino()));
		if (parcelaVO.getCodSubTermino() != null && !parcelaVO.getCodSubTermino().equals("")
				|| parcelaVO.getCodSubTermino().length() == 1)
			parcela.setSubtermino(parcelaVO.getCodSubTermino().charAt(0));
		// set SIGPAC
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
		// set cultivo - variedad
		if (parcelaVO.getCultivo() != null && !parcelaVO.getCultivo().equals(""))
			parcela.setCodcultivo(new BigDecimal(parcelaVO.getCultivo()));
		if (parcelaVO.getVariedad() != null && !parcelaVO.getVariedad().equals(""))
			parcela.setCodvariedad(new BigDecimal(parcelaVO.getVariedad()));
		// set POLIGONO,PARCELA_1 & NOM.PARCELA
		if (parcelaVO.getNombreParcela() != null && !parcelaVO.getNombreParcela().equals(""))
			parcela.setNomparcela(parcelaVO.getNombreParcela());
		else
			parcela.setNomparcela(" "); // temporal

		// HOJA & NUMERO
		AnexoModificacion anexoModificacionAux = (AnexoModificacion) datosParcelaAnexoDao
				.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
		getHojaNumero(anexoModificacionAux, parcela, null, ALTA_ESTRUCTURA_PARCELA);

		if (parcelaVO.getIdparcelaanxestructura() != null && !"".equals(parcelaVO.getIdparcelaanxestructura()))
			parcela.setIdparcelaanxestructura(new Long(parcelaVO.getIdparcelaanxestructura()));

		return parcela;
	}

	/**
	 * CANCELAR_PARCELA
	 */
	public void cancelarParcela(String codParcela, String operacion) {
		/* SIN USAR */
	}

	/**
	 * DELETE_CAPITAL_ASEGURADO : ALTA --> borrar el capital asegurado EDIT -->
	 * borrar el capital asegurado
	 */
	public void deleteCapitalAsegurado(String codCapitalAsegurado) {
		CapitalAsegurado capitalAsegurado = (CapitalAsegurado) datosParcelaAnexoDao.getObject(CapitalAsegurado.class,
				new Long(codCapitalAsegurado));
		datosParcelaAnexoDao.deleteCapitalAsegurado(capitalAsegurado);
	}

	// --------------------------------------------------------------------
	// PARCELA VALIDATION
	// --------------------------------------------------------------------
	/**
	 * 
	 * @param
	 * @return
	 */
	private OperationResultVO isParcelaOk(Parcela parcela, Long idLinea) {
		OperationResultVO operationResultVO = new OperationResultVO();

		ArrayList<String> errorsList = new ArrayList<String>();
		ArrayList<String> errorsListPanel1 = new ArrayList<String>();
		ArrayList<String> errorsListPanel2 = new ArrayList<String>();
		ArrayList<String> errorsListPanel3 = new ArrayList<String>();

		errorsListPanel1 = isValidPanel1(parcela, idLinea);
		errorsListPanel2 = isValidPanel2(parcela);

		for (String error : errorsListPanel1)
			errorsList.add(error);
		for (String error : errorsListPanel2)
			errorsList.add(error);
		for (String error : errorsListPanel3)
			errorsList.add(error);

		operationResultVO.setMessageErrors(errorsList);

		return operationResultVO;
	}

	// ---------------------------- VALID PANEL1 ---------------------------
	/**
	 * 
	 * @param
	 * @return
	 */
	private ArrayList<String> isValidPanel1(Parcela parcela, Long idLinea) {

		ArrayList<String> errorsList = new ArrayList<String>();
		ArrayList<String> errorsListPanel1 = new ArrayList<String>();
		ArrayList<String> errorsListPanel2 = new ArrayList<String>();

		errorsListPanel1 = isValidUbicacion(parcela.getCodprovincia(), parcela.getCodcomarca(), parcela.getCodtermino(),
				parcela.getSubtermino());
		errorsListPanel2 = isValidCultivoVariedad(parcela.getCodcultivo(), parcela.getCodvariedad(), idLinea);

		for (String error : errorsListPanel1)
			errorsList.add(error);
		for (String error : errorsListPanel2)
			errorsList.add(error);

		return errorsList;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private ArrayList<String> isValidUbicacion(BigDecimal codProvincia, BigDecimal codComarca, BigDecimal codTermino,
			Character codSubtermino) {
		ArrayList<String> errorsList = new ArrayList<String>();

		if (!exitProvincia(codProvincia))
			errorsList.add("El codigo de provincia no existe.");
		else {
			if (codComarca != null)
				if (!exitComarcaInProvincia(codProvincia, codComarca))
					errorsList.add("El codigo de comarca no pertenece a la provincia.");

			if (!exitTerminoInProvincia(codProvincia, codComarca, codTermino, codSubtermino))
				errorsList.add("El codigo de termino y subtermino no pertenecen a la provincia .");
		}

		// Si todos son correctos miro si es una ubicacion real
		if (errorsList.size() == 0)
			if (!exitUbicacion(codProvincia, codComarca, codTermino, codSubtermino))
				errorsList.add("La ubicacion no existe.");

		return errorsList;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean exitProvincia(BigDecimal codProvincia) {
		boolean result = true;

		if ((Provincia) datosParcelaAnexoDao.getObject(Provincia.class, codProvincia) == null)
			result = false;

		return result;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean exitComarcaInProvincia(BigDecimal codProvincia, BigDecimal codComarca) {
		boolean result = true;

		ComarcaId comarcaId = new ComarcaId(codComarca, codProvincia);

		if ((Comarca) datosParcelaAnexoDao.getObject(Comarca.class, comarcaId) == null)
			result = false;

		return result;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean exitTerminoInProvincia(BigDecimal codProvincia, BigDecimal codComarca, BigDecimal codTermino,
			Character subtermino) {
		boolean result = true;

		TerminoId terminoId = new TerminoId(codProvincia, codTermino, subtermino, codComarca);
		if ((Termino) datosParcelaAnexoDao.getObject(Termino.class, terminoId) == null)
			result = false;

		return result;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean exitUbicacion(BigDecimal codProvincia, BigDecimal codComarca, BigDecimal codTermino,
			Character subtermino) {

		boolean result = true;
		TerminoId terminoId = null;
		Termino termino = null;

		if (codTermino == null || codProvincia == null) {
			result = false;
		} else {
			if (codComarca != null && subtermino != null) {
				terminoId = new TerminoId(codProvincia, codTermino, subtermino, codComarca);
				termino = (Termino) datosParcelaAnexoDao.getObject(Termino.class, terminoId);
			} else if (codComarca != null) {
				terminoId = new TerminoId();
				terminoId.setCodprovincia(codProvincia);
				terminoId.setCodtermino(codTermino);

				termino = (Termino) datosParcelaAnexoDao.getObject(Termino.class, terminoId);
			}

			if (termino == null) {
				result = false;
			} else {
				if (termino.getProvincia().getCodprovincia().compareTo(codProvincia) == -1) {
					result = false;
				}
				if (termino.getComarca().getId().getCodcomarca().compareTo(codComarca) == -1) {
					result = false;
				}
				if (termino.getId().getSubtermino().compareTo(subtermino) == -1) {
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private ArrayList<String> isValidCultivoVariedad(BigDecimal codCultivo, BigDecimal codVariedad,
			Long lineaseguroid) {

		ArrayList<String> errorsList = new ArrayList<String>();

		if (!exitCultivo(codCultivo, lineaseguroid))
			errorsList.add("El codigo de cultivo no existe.");

		// Si todos son correctos miro si es una ubicacion real
		if (errorsList.size() == 0)
			if (!isVariedadDeEsteCultivo(codCultivo, codVariedad, lineaseguroid))
				errorsList.add("La variedad no pertenece al cultivo.");

		return errorsList;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean exitCultivo(BigDecimal codCultivo, Long lineaseguroid) {
		boolean result = true;

		CultivoId cultivoid = new CultivoId(lineaseguroid, codCultivo);
		if ((Cultivo) datosParcelaAnexoDao.getObject(Cultivo.class, cultivoid) == null)
			result = false;

		return result;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean isVariedadDeEsteCultivo(BigDecimal codCultivo, BigDecimal codVariedad, Long lineaseguroid) {
		boolean result = true;

		VariedadId variedadId = new VariedadId(lineaseguroid, codCultivo, codVariedad);

		if ((Variedad) datosParcelaAnexoDao.getObject(Variedad.class, variedadId) == null)
			result = false;
		return result;
	}

	// ---------------------------- VALID PANEL2 ---------------------------

	/**
	 * 
	 * @param
	 * @return
	 */
	private ArrayList<String> isValidPanel2(Parcela parcela) {
		ArrayList<String> errorsList = new ArrayList<String>();

		// No se ve necesario hacer validaciones de este panel

		return errorsList;
	}
	// ---------------------------- VALID PANEL3 ---------------------------

	/**
	 * 
	 * PRECIO/PRODUCCION
	 *
	 */

	public List<ProduccionVO> getProduccion(ParcelaVO parcela) throws BusinessException {
		List<ProduccionVO> producciones = new ArrayList<ProduccionVO>();
		List<ModuloPoliza> modulos = null;

		try {

			// Recuperamos los datos de la poliza
			Long idPoliza = new Long(parcela.getCodPoliza());
			Poliza poliza = new Poliza();

			poliza = (Poliza) datosParcelaAnexoDao.getObject(Poliza.class, idPoliza);

			Long lineaseguroid = poliza.getLinea().getLineaseguroid();
			String nifasegurado = poliza.getAsegurado().getNifcif();

			modulos = datosParcelaAnexoDao.getModulosPoliza(idPoliza, lineaseguroid);

			// Set<ModuloPoliza> modulos = poliza.getModuloPolizas();
			producciones = getProduccion(lineaseguroid, nifasegurado, modulos, parcela);

		} catch (DAOException ex) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR_EN_EL_ACCESO_A_LA_BASE_DE_DATOS, ex);
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_EN_EL_ACCESO_A_LA_BASE_DE_DATOS, ex);

		} catch (ParseException ex) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_LAS_FECHAS_DE_RECOLECCION, ex);
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_LAS_FECHAS_DE_RECOLECCION, ex);
		}

		return producciones;
	}

	/**
	 * Obtiene la Produccion.
	 * 
	 * @param lineaseguroid
	 * @param nifasegurado
	 * @param modulos
	 * @param dataGridPopUpData
	 * @throws ParseException
	 */
	public List<ProduccionVO> getProduccion(Long lineaseguroid, String nifasegurado, List<ModuloPoliza> modulos,
			ParcelaVO parcela) throws ParseException {

		List<ItemVO> datVarPar = getDatosParcela(parcela);

		String codmodulo = new String();
		String[] produccion = new String[2];
		ProduccionVO produccionVO = new ProduccionVO();
		List<ProduccionVO> producciones = new ArrayList<ProduccionVO>();

		for (ModuloPoliza modulo : modulos) {
			codmodulo = modulo.getId().getCodmodulo();

			// 1Âº Obtenemos los asegurados autorizados para el plan y la linea
			AseguradoAutorizadoFiltro asegAutorizadoFiltro = new AseguradoAutorizadoFiltro(lineaseguroid, nifasegurado);
			List<AseguradoAutorizado> asegAutorizados = datosParcelaAnexoDao.getObjects(asegAutorizadoFiltro);

			if (asegAutorizados.size() == 0) {
				// no hay asegurados para la linea

				if (datosParcelaAnexoDao.existeLimiteRendimientoByLineaseguroid(lineaseguroid))
					produccion = getLimitesRendimiento(lineaseguroid, nifasegurado, codmodulo, parcela, datVarPar);
				else {
					produccion[0] = "1";
					produccion[1] = "";
				}

			} else if (asegAutorizados.size() == 1) {
				AseguradoAutorizado aseg = asegAutorizados.get(0);
				if (aseg.getRdtopermitido() != null && aseg.getRdtopermitido().longValue() != 0) {
					// el asegurado tiene rendimiento fijo, se mide en
					// Kg/Ha.
					String sProd = calcularProduccionFija(aseg.getRdtopermitido().longValue(), datVarPar);
					produccion[0] = sProd;
					produccion[1] = sProd;
				} else if (aseg.getCoefsobrerdtos() != null && aseg.getCoefsobrerdtos().longValue() != 0) {
					// calculamos los limites de rendimiento
					produccion = getLimitesRendimiento(lineaseguroid, nifasegurado, codmodulo, parcela, datVarPar);
					// los limites se multiplican por el coeficiente
					// sobre rendimientos del asegurado
					for (String sLimite : produccion) {
						if (!sLimite.equals("")) {
							Long lLimite = new Long(sLimite);
							lLimite = lLimite * aseg.getCoefsobrerdtos().longValue();
							sLimite = lLimite.toString();
						}
					}
				} else {
					produccion[0] = "0";
					produccion[1] = "0";
				}
			}

			produccionVO = new ProduccionVO();

			produccionVO.setCodModulo(codmodulo);
			produccionVO.setLimMin(produccion[0]);
			produccionVO.setLimMax(produccion[1]);

			producciones.add(produccionVO);
		}

		return producciones;
	}

	/**
	 * Obtengo la produccion del limite fijo del asegurado.
	 * 
	 * @param limRend
	 * @param datVarPar
	 * @return
	 */
	public String calcularProduccionFija(Long limRend, List<ItemVO> datVarPar) {
		// El rendimiento se mide en Kg/Ha
		for (ItemVO item : datVarPar) {
			if (item.getCodigo().equals("258")) {
				Long superficie = Long.parseLong(item.getValor());
				limRend = (limRend * superficie);
			}
		}

		return limRend.toString();
	}

	/**
	 * Obtiene limites de rendimientos y calcula la produccion
	 * 
	 * @param lineaseguroid
	 * @param nifasegurado
	 * @param codmodulo
	 * @param dataGridPopUpData
	 * @return
	 * @throws ParseException
	 */
	private String[] getLimitesRendimiento(Long lineaseguroid, String nifasegurado, String codmodulo, ParcelaVO parcela,
			List<ItemVO> datVarPar) throws ParseException {

		String[] produccion = new String[2];
		Long limRendMin = new Long(1);
		Long limRendMax = new Long(0);
		HashMap<String, String> filtroMascara = new HashMap<String, String>();

		// Obtenemos el coeficiente de Rendimiento Maximo Asegurable
		Float coefRdtoMaxAseg = new Float(1);
		coefRdtoMaxAseg = getCoefRdtoMaxAseg(lineaseguroid, nifasegurado);

		// 1. Obtenemos los campos de mascara segun plan, linea, cultivo
		// variedad, provincia, comarca, termino, subtermino y modulo
		List<MascaraLimiteRendimiento> mascaras = datosParcelaAnexoDao.getMascaraLimiteRendimiento(lineaseguroid,
				codmodulo, parcela);

		for (MascaraLimiteRendimiento mascara : mascaras) {
			BigDecimal codConcepto = mascara.getId().getCodconcepto();
			if (codConcepto != null) {
				CampoMascaraFiltro campMascaraFiltro = new CampoMascaraFiltro();
				campMascaraFiltro.getCampoMascara().getDiccionarioDatosByCodconceptomasc().setCodconcepto(codConcepto);

				List<CampoMascara> camposMascara = datosParcelaAnexoDao.getObjects(campMascaraFiltro);

				String valor = new String();
				if (camposMascara.size() == 0) {
					valor = getValueConcepto(codConcepto.toString(), datVarPar);
					filtroMascara.put(codConcepto.toString(), valor);
				} else if (camposMascara.size() == 1) {
					CampoMascara campoMascara = camposMascara.get(0);

					BigDecimal codConceptoAsoc = campoMascara.getDiccionarioDatosByCodconceptoasoc().getCodconcepto();
					valor = getValueConcepto(codConceptoAsoc.toString(), datVarPar);
					filtroMascara.put(codConcepto.toString(), valor);
				}
			}
		}

		// 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
		// un valor por defecto
		valorPorDefectoFitros(filtroMascara);

		// 3. Obtenemos los Limites de Rendimiento
		LimiteRendimientoFiltro limRendFiltro = rellenarFiltroRendimientos(lineaseguroid, codmodulo, parcela,
				filtroMascara);

		List<LimiteRendimiento> rendimientos = datosParcelaAnexoDao.getRendimientos(limRendFiltro);

		// 4. Se aplica el coeficiente de Rendimiento Maximo Asegurable a los
		// limites obtenidos
		if (rendimientos.size() == 0) {
			limRendMin = new Long(0);
			limRendMax = new Long(0);
		} else if (rendimientos.size() == 1) {
			// Obtengo el rendimiento
			LimiteRendimiento rendimineto = rendimientos.get(0);
			if (coefRdtoMaxAseg != null && coefRdtoMaxAseg > 0 && rendimineto.getLimiteinfrdto() != null
					&& rendimineto.getLimitesuprdto() != null) {

				Float flimRendMin = rendimineto.getLimiteinfrdto().floatValue() * coefRdtoMaxAseg;
				Float flimRendMax = rendimineto.getLimitesuprdto().floatValue() * coefRdtoMaxAseg;
				limRendMin = flimRendMin.longValue();
				limRendMax = flimRendMax.longValue();
			} else if (rendimineto.getLimitesuprdto() == null) {
				// si esta vacio el limite maximo, se deja a libre eleccion
				limRendMin = rendimineto.getLimiteinfrdto().longValue();
				limRendMax = new Long(-1);
			}
		} else if (rendimientos.size() > 1) {
			// Si recupera mas de un rendimiento obtengo el maximo y el minimo de todos
			for (LimiteRendimiento rendimineto : rendimientos) {
				if (rendimineto.getLimiteinfrdto().longValue() < limRendMin) {
					limRendMin = rendimineto.getLimiteinfrdto().longValue();
				}
				if (rendimineto.getLimitesuprdto().longValue() > limRendMax) {
					limRendMax = rendimineto.getLimitesuprdto().longValue();
				}
			}
			Float flimRendMin = limRendMin.floatValue() * coefRdtoMaxAseg;
			Float flimRendMax = limRendMax.floatValue() * coefRdtoMaxAseg;
			limRendMin = flimRendMin.longValue();
			limRendMax = flimRendMax.longValue();
		}

		// 5. Obtenemos la produccion.
		if (limRendMax == 0) {
			// 5.1. Si no tiene rendimientos muestra 0 en los dos rendimientos
			produccion[0] = "0";
			produccion[1] = "0";
		} else if (limRendMax == -1) {
			// El limite superior es de libre eleccion
			produccion[0] = limRendMin.toString();
			produccion[1] = "";
		} else if (rendimientos.size() > 0) {
			// 5.2. Segun el campo apprdto (aplicar rendimiento) se obtiene la produccion
			String apprdto = rendimientos.get(0).getApprdto() != null ? rendimientos.get(0).getApprdto().toString()
					: "";

			produccion = calcularProduccion(limRendMin, limRendMax, apprdto, datVarPar);
		}

		return produccion;
	}

	/**
	 * Obtiene el Coeficiente de Rendimiento Maximo Asegurable del asegurado.
	 * 
	 * @param lineaseguroid
	 * @param nifasegurado
	 * @return
	 */
	public Float getCoefRdtoMaxAseg(Long lineaseguroid, String nifasegurado) {
		Float coefRdtoMaxAseg = new Float(1);

		// obtenemos de la tabla de medida el coeficiente de rendimiento maximo
		// asegurable.
		MedidaFiltro filtroMedida = new MedidaFiltro(lineaseguroid, nifasegurado);
		List<Medida> medidaAseg = datosParcelaAnexoDao.getObjects(filtroMedida);
		if (medidaAseg != null && medidaAseg.size() > 0) {
			Medida medida = medidaAseg.get(0);
			coefRdtoMaxAseg = medida.getCoefrdtomaxaseg().floatValue();
		}

		return coefRdtoMaxAseg;
	}

	/**
	 * Calcula la produccion en Kg/Ha (rendimiento * superficie) o en Kg/arbol
	 * (rendimiento * numero de unidades).
	 * 
	 * @param limRendMin
	 * @param limRendMax
	 * @param tipMarcPlan
	 * @param datVarPar
	 * @return
	 */
	public String[] calcularProduccion(Long limRendMin, Long limRendMax, String apprdto, List<ItemVO> datVarPar) {
		String[] prod = new String[2];
		Long unidades = new Long(0);
		// La superficie puede tener decimales, pero a la hora de mostrar la produccion,
		// solo se muestra la parte entera
		Float superficie = new Float(0);
		Float fLimRendMin = new Float(limRendMin);
		Float fLimRendMax = new Float(limRendMax);

		for (ItemVO item : datVarPar) {
			if (item.getCodigo().equals("258")) {
				superficie = new Float(item.getValor());
			} else if (item.getCodigo().equals("117")) {
				unidades = new Long(item.getValor());
			}
		}
		if ("S".equals(apprdto)) {
			// Si el apprdto es "S" el rendimiento se mide en Kg/Ha
			// los rendimineto se multiplican por la superficie
			fLimRendMin = fLimRendMin * superficie;
			fLimRendMax = fLimRendMax * superficie;
			// nos quedamos solo con la parte entera
			limRendMin = fLimRendMin.longValue();
			limRendMax = fLimRendMax.longValue();
			// si la parte entera del limite minimo es 0 se muestra 1
			// limRendMin = (limRendMin==0)? 1 : limRendMin;

			prod[0] = limRendMin.toString();
			prod[1] = limRendMax.toString();
		} else if ("U".equals(apprdto)) {
			// Si el apprdto es "U" el rendimiento se mide en Kg/arbol
			// los rendimineto se multiplican por las unidades
			limRendMin = limRendMin * unidades;
			limRendMax = limRendMax * unidades;
			prod[0] = limRendMin.toString();
			prod[1] = limRendMax.toString();
		}

		return prod;
	}

	/**
	 * Obtiene de los datos de la parcela el valor del concepto que recibe.
	 * 
	 * @param codconcepto
	 * @param datosvariables
	 * @return
	 */
	public String getValueConcepto(String codconcepto, List<ItemVO> datosvariables) {
		String value = new String();

		for (ItemVO item : datosvariables) {
			if (codconcepto.equals(item.getCodigo())) {
				value = item.getValor();
				break;
			}
		}
		return value;
	}

	/**
	 * Obtiene el precio minimo y maximo de la parcela.
	 * 
	 * @param parcela
	 * @return
	 * @throws BusinessException
	 */
	public List<PrecioVO> getPrecio(ParcelaVO parcela) throws BusinessException {
		List<PrecioVO> preciosModulo = new ArrayList<PrecioVO>();
		List<ModuloPoliza> modulos = null;

		try {

			Long idPoliza = new Long(parcela.getCodPoliza());
			Poliza poliza = new Poliza();
			poliza = (Poliza) datosParcelaAnexoDao.getObject(Poliza.class, idPoliza);
			Long lineaseguroid = poliza.getLinea().getLineaseguroid();
			modulos = datosParcelaAnexoDao.getModulosPoliza(idPoliza, lineaseguroid);
			// Set<ModuloPoliza> modulos = poliza.getModuloPolizas();

			preciosModulo = getPrecio(lineaseguroid, modulos, parcela);

		} catch (DAOException ex) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR_EN_EL_ACCESO_A_LA_BASE_DE_DATOS, ex);
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_EN_EL_ACCESO_A_LA_BASE_DE_DATOS, ex);
		} catch (ParseException ex) {
			logger.error(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_LAS_FECHAS_DE_RECOLECCION, ex);
			throw new BusinessException(SE_HA_PRODUCIDO_UN_ERROR_AL_RECUPERAR_LAS_FECHAS_DE_RECOLECCION, ex);
		}

		return preciosModulo;
	}

	/**
	 * Obtiene el precio minimo y maximo de la parcela.
	 * 
	 * @param lineaseguroid
	 * @param modulos
	 * @param parcela
	 * @return
	 * @throws BusinessException
	 * @throws ParseException
	 */
	public List<PrecioVO> getPrecio(Long lineaseguroid, List<ModuloPoliza> modulos, ParcelaVO parcela)
			throws BusinessException, ParseException {

		List<PrecioVO> preciosVO = new ArrayList<PrecioVO>(modulos.size());

		try {
			List<ItemVO> datVarPar = getDatosParcela(parcela);
			PrecioVO precioVO = new PrecioVO();
			String codmodulo = new String();
			HashMap<String, String> filtroMascara = new HashMap<String, String>();

			for (ModuloPoliza modulo : modulos) {
				codmodulo = modulo.getId().getCodmodulo();

				// 1. Obtenemos las mascaras de precio segun plan, linea, modulo,
				// cultivo, variedad, provincia, comarca, termino y subtermino
				List<MascaraPrecio> mascaras = datosParcelaAnexoDao.getMascaraPrecio(lineaseguroid, codmodulo, parcela);

				for (MascaraPrecio mascara : mascaras) {
					BigDecimal codConcepto = mascara.getId().getCodconcepto();
					if (codConcepto != null) {
						CampoMascaraFiltro campMascaraFiltro = new CampoMascaraFiltro();
						campMascaraFiltro.getCampoMascara().getDiccionarioDatosByCodconceptomasc()
								.setCodconcepto(codConcepto);

						List<CampoMascara> camposMascara = datosParcelaAnexoDao.getObjects(campMascaraFiltro);

						String valor = new String();
						if (camposMascara.size() == 0) {
							valor = getValueConcepto(codConcepto.toString(), datVarPar);
							filtroMascara.put(codConcepto.toString(), valor);
						} else if (camposMascara.size() > 0) {
							CampoMascara campoMascara = camposMascara.get(0);

							BigDecimal codConceptoAsoc = campoMascara.getDiccionarioDatosByCodconceptoasoc()
									.getCodconcepto();
							valor = getValueConcepto(codConceptoAsoc.toString(), datVarPar);
							filtroMascara.put(codConcepto.toString(), valor);
						}
					}
				}

				// 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
				// un valor por defecto
				valorPorDefectoFitros(filtroMascara);

				// 3. Obtenemos los Precios
				PrecioFiltro precioFiltro = rellenarFiltroPrecio(lineaseguroid, codmodulo, parcela, filtroMascara);
				List<Precio> precios = datosParcelaAnexoDao.getPrecio(precioFiltro);

				// 4. Si se recupera mas de un precio hay que mostrar para el precio
				// desde el mas bajo de todos y para el precio hasta el mas alto de
				// todos.
				Float precioMin = new Float(0);
				Float precioMax = new Float(0);

				if (precios.size() == 1) {
					Precio precio = precios.get(0);
					if (precio.getPreciofijo().floatValue() > 0) {
						precioMin = precio.getPreciofijo().floatValue();
						precioMax = precio.getPreciofijo().floatValue();
					} else {
						precioMin = precio.getPreciodesde().floatValue();
						precioMax = precio.getPreciohasta().floatValue();
					}
				} else if (precios.size() > 1) {
					for (Precio precio : precios) {
						if (precioMin == 0 || precio.getPreciodesde().floatValue() < precioMin)
							precioMin = precio.getPreciodesde().floatValue();

						if (precioMax == 0 || precio.getPreciohasta().floatValue() > precioMax)
							precioMax = precio.getPreciohasta().floatValue();
					}
				}
				
				precioVO = new PrecioVO();
				precioVO.setCodModulo(codmodulo);
				precioVO.setLimMin(precioMin.toString());
				precioVO.setLimMax(precioMax.toString());
				preciosVO.add(precioVO);
			}
		} catch (Exception ex) {
			logger.error("Se ha producido un error durante el calculo del precio", ex);
			throw new BusinessException("Se ha producido un error durante el calculo del precio", ex);
		}

		return preciosVO;
	}

	private LimiteRendimientoFiltro rellenarFiltroRendimientos(Long lineaseguroid, String codmodulo, ParcelaVO parcela,
			HashMap<String, String> filtroMascara) throws ParseException {
		LimiteRendimientoFiltro limRendFiltro = new LimiteRendimientoFiltro();

		BigDecimal codcultivo = (parcela.getCultivo() != null && !parcela.getCultivo().equals(""))
				? new BigDecimal(parcela.getCultivo())
				: null;
		BigDecimal codvariedad = (parcela.getVariedad() != null && !parcela.getVariedad().equals(""))
				? new BigDecimal(parcela.getVariedad())
				: null;
		BigDecimal codprovincia = (parcela.getCodProvincia() != null && !parcela.getCodProvincia().equals(""))
				? new BigDecimal(parcela.getCodProvincia())
				: null;
		BigDecimal codtermino = (parcela.getCodTermino() != null && !parcela.getCodTermino().equals(""))
				? new BigDecimal(parcela.getCodTermino())
				: null;
		Character subtermino = (parcela.getCodSubTermino() != null && !parcela.getCodSubTermino().equals(""))
				? new Character(parcela.getCodSubTermino().charAt(0))
				: null;
		BigDecimal codcomarca = (parcela.getCodComarca() != null && !parcela.getCodComarca().equals(""))
				? new BigDecimal(parcela.getCodComarca())
				: null;

		limRendFiltro.getLimiteRendimiento().getId().setLineaseguroid(lineaseguroid);
		limRendFiltro.getLimiteRendimiento().getModulo().getId().setCodmodulo(codmodulo);
		limRendFiltro.getLimiteRendimiento().getVariedad().getId().setCodcultivo(codcultivo);
		limRendFiltro.getLimiteRendimiento().getVariedad().getId().setCodvariedad(codvariedad);
		limRendFiltro.getLimiteRendimiento().setCodprovincia(codprovincia);
		limRendFiltro.getLimiteRendimiento().setCodtermino(codtermino);
		limRendFiltro.getLimiteRendimiento().setSubtermino(subtermino);
		limRendFiltro.getLimiteRendimiento().setCodcomarca(codcomarca);

		if (filtroMascara != null && filtroMascara.size() > 0) {
			// Rellenamos los datos variables de la parcela, obtenidos de la
			// mascara de limites de rendimiento
			datVarMascRendimiento(filtroMascara, limRendFiltro);
		}

		return limRendFiltro;
	}

	private PrecioFiltro rellenarFiltroPrecio(Long lineaseguroid, String codmodulo, ParcelaVO parcela,
			HashMap<String, String> filtroMascara) throws ParseException {
		PrecioFiltro precioFiltro = new PrecioFiltro();

		BigDecimal codcultivo = (parcela.getCultivo() != null && !parcela.getCultivo().equals(""))
				? new BigDecimal(parcela.getCultivo())
				: null;
		BigDecimal codvariedad = (parcela.getVariedad() != null && !parcela.getVariedad().equals(""))
				? new BigDecimal(parcela.getVariedad())
				: null;
		BigDecimal codprovincia = (parcela.getCodProvincia() != null && !parcela.getCodProvincia().equals(""))
				? new BigDecimal(parcela.getCodProvincia())
				: null;
		BigDecimal codtermino = (parcela.getCodTermino() != null && !parcela.getCodTermino().equals(""))
				? new BigDecimal(parcela.getCodTermino())
				: null;
		Character subtermino = (parcela.getCodSubTermino() != null && !parcela.getCodSubTermino().equals(""))
				? new Character(parcela.getCodSubTermino().charAt(0))
				: null;
		BigDecimal codcomarca = (parcela.getCodComarca() != null && !parcela.getCodComarca().equals(""))
				? new BigDecimal(parcela.getCodComarca())
				: null;

		precioFiltro.getPrecio().getId().setLineaseguroid(lineaseguroid);
		ModuloId idModulo = new ModuloId();
		idModulo.setLineaseguroid(lineaseguroid);
		idModulo.setCodmodulo(codmodulo);
		Modulo modulo = (Modulo) datosParcelaAnexoDao.getObject(Modulo.class, idModulo);
		precioFiltro.getPrecio().setModulo(modulo);
		// precioFiltro.getPrecio().getModulo().getId().setCodmodulo(codmodulo);
		VariedadId idVar = new VariedadId();
		idVar.setCodcultivo(codcultivo);
		idVar.setCodvariedad(codvariedad);
		idVar.setLineaseguroid(lineaseguroid);
		Variedad var = (Variedad) datosParcelaAnexoDao.getObject(Variedad.class, idVar);
		precioFiltro.getPrecio().setVariedad(var);
		precioFiltro.getPrecio().setCodprovincia(codprovincia);
		precioFiltro.getPrecio().setCodtermino(codtermino);
		precioFiltro.getPrecio().setSubtermino(subtermino);
		precioFiltro.getPrecio().setCodcomarca(codcomarca);

		if (filtroMascara != null && filtroMascara.size() > 0) {
			// Rellenamos los datos variables de la parcela, obtenidos de la
			// mascara de precio
			datVarMascPrecio(filtroMascara, precioFiltro);
		}

		return precioFiltro;
	}

	public List<ItemVO> getDatosParcela(ParcelaVO parcela) {
		List<ItemVO> datosParcela = new ArrayList<ItemVO>();
		ItemVO item;

		CapitalAseguradoVO capAsegVo = ((List<CapitalAseguradoVO>) parcela.getCapitalesAsegurados()).get(0);
		// Tipo de Capital
		item = new ItemVO();
		item.setCodigo("126");
		item.setValor(capAsegVo.getCodtipoCapital());
		datosParcela.add(item);
		// Superficie
		item = new ItemVO();
		item.setCodigo("258");
		item.setValor(capAsegVo.getSuperficie());
		datosParcela.add(item);

		List<DatoVariableParcelaVO> datosVariablesParcela = capAsegVo.getDatosVariablesParcela();

		for (DatoVariableParcelaVO datVarPar : datosVariablesParcela) {
			item = new ItemVO();
			item.setCodigo(datVarPar.getCodconcepto().toString());
			item.setValor(datVarPar.getValor());
			datosParcela.add(item);
		}
		return datosParcela;
	}

	/**
	 * Rellena el filtro de Limite de Rendimiento con los datos variables de la
	 * parcela obtenidos de la mascara de limites de rendimiento.
	 * 
	 * @param filtroMascara
	 * @param limRendFiltro
	 * @throws ParseException
	 */
	private void datVarMascRendimiento(HashMap<String, String> filtroMascara, LimiteRendimientoFiltro limRendFiltro)
			throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if (valor != null && !"".equals(valor)) {
				if ("116".equals(key)) {
					// Tipo Marco Plantacion
					limRendFiltro.getLimiteRendimiento().setCodtipomarcoplantac(new BigDecimal(valor));
				} else if ("231".equals(key)) {
					// Edad Desde
					limRendFiltro.getLimiteRendimiento().setEdaddesde(new BigDecimal(valor));
				} else if ("232".equals(key)) {
					// Edad Hasta
					limRendFiltro.getLimiteRendimiento().setEdadhasta(new BigDecimal(valor));
				} else if ("106".equals(key)) {
					// Caract. Explotacion
					limRendFiltro.getLimiteRendimiento().getCaracteristicaExplotacion()
							.setCodcaractexplotacion(new BigDecimal(valor));
				} else if ("226".equals(key)) {
					// Densidad Desde
					limRendFiltro.getLimiteRendimiento().setDensidaddesde(new BigDecimal(valor));
				} else if ("227".equals(key)) {
					// Densidad Hasta
					limRendFiltro.getLimiteRendimiento().setDensidadhasta(new BigDecimal(valor));
				} else if ("235".equals(key)) {
					// Fecha Recoleccion Desde
					limRendFiltro.getLimiteRendimiento().setFrecoldesde(df.parse(valor));
				} else if ("236".equals(key)) {
					// Fecha Recoleccion Hasta
					limRendFiltro.getLimiteRendimiento().setFrecolhasta(df.parse(valor));
				} else if ("244".equals(key)) {
					// Num Unidades Desde
					limRendFiltro.getLimiteRendimiento().setNumudsdesde(new BigDecimal(valor));
				} else if ("245".equals(key)) {
					// Num Unidades Hasta
					limRendFiltro.getLimiteRendimiento().setNumudshasta(new BigDecimal(valor));
				} else if ("617".equals(key)) {
					// Num anhos de Poda
					limRendFiltro.getLimiteRendimiento().setNumaniospoda(new BigDecimal(valor));
				} else if ("123".equals(key)) {
					// Sistema Cultivo
					limRendFiltro.getLimiteRendimiento().getSistemaCultivo()
							.setCodsistemacultivo(new BigDecimal(valor));
				} else if ("616".equals(key)) {
					// Sistema Produccion
					limRendFiltro.getLimiteRendimiento().getSistemaProduccion()
							.setCodsistemaproduccion(new BigDecimal(valor));
				} else if ("131".equals(key)) {
					// Sistema Conduccion
					limRendFiltro.getLimiteRendimiento().getSistemaConduccion()
							.setCodsistemaconduccion(new BigDecimal(valor));
				} else if ("173".equals(key)) {
					// Tipo Plantacion
					limRendFiltro.getLimiteRendimiento().getTipoPlantacion()
							.setCodtipoplantacion(new BigDecimal(valor));
				} else if ("133".equals(key)) {
					// Practica Cultural
					limRendFiltro.getLimiteRendimiento().getPracticaCultural()
							.setCodpracticacultural(new BigDecimal(valor));
				}
			}
		}
	}

	/**
	 * Rellena el filtro de Precio con los datos variables de la parcela obtenidos
	 * de la mascara de precios.
	 * 
	 * @param filtroMascara
	 * @param precioFiltro
	 * @throws ParseException
	 */
	private void datVarMascPrecio(HashMap<String, String> filtroMascara, PrecioFiltro precioFiltro)
			throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if (valor != null && !"".equals(valor)) {
				if ("133".equals(key)) {
					// Practica Cultural
					precioFiltro.getPrecio().getPracticaCultural().setCodpracticacultural(new BigDecimal(valor));
				} else if ("126".equals(key)) {
					// Tipo Capital
					precioFiltro.getPrecio().getTipoCapital().setCodtipocapital(new BigDecimal(valor));
				} else if ("616".equals(key)) {
					// Sistema Produccion
					precioFiltro.getPrecio().getSistemaProduccion().setCodsistemaproduccion(new BigDecimal(valor));
				} else if ("107".equals(key)) {
					// Denominacion de Origen
					precioFiltro.getPrecio().getCodigoDenominacionOrigen().getId()
							.setCoddenomorigen(new BigDecimal(valor));
				} else if ("110".equals(key)) {
					// Destino
					precioFiltro.getPrecio().getDestino().setCoddestino(new BigDecimal(valor));
				} else if ("231".equals(key)) {
					// Edad Desde
					precioFiltro.getPrecio().setEdaddesde(new BigDecimal(valor));
				} else if ("232".equals(key)) {
					// Edad Hasta
					precioFiltro.getPrecio().setEdadhasta(new BigDecimal(valor));
				} else if ("235".equals(key)) {
					// Fecha Recoleccion Desde
					precioFiltro.getPrecio().setFrecoldesde(df.parse(valor));
				} else if ("236".equals(key)) {
					// Fecha Recoleccion Hasta
					precioFiltro.getPrecio().setFrecolhasta(df.parse(valor));
				} else if ("123".equals(key)) {
					// Sistema Cultivo
					precioFiltro.getPrecio().getSistemaCultivo().setCodsistemacultivo(new BigDecimal(valor));
				} else if ("621".equals(key)) {
					// Sistema Proteccion
					precioFiltro.getPrecio().getSistemaProteccion().setCodsistemaproteccion(new BigDecimal(valor));
				} else if ("173".equals(key)) {
					// Tipo Plantacion
					precioFiltro.getPrecio().getTipoPlantacion().setCodtipoplantacion(new BigDecimal(valor));
				} else if ("618".equals(key)) {
					// Ciclo Cultivo
					precioFiltro.getPrecio().getCicloCultivo().setCodciclocultivo(new BigDecimal(valor));
				} else if ("873".equals(key)) {
					// Material cubierta
					precioFiltro.getPrecio().getMaterialCubierta().setCodmaterialcubierta((new BigDecimal(valor)));
				} else if ("875".equals(key)) {
					// Material estructura
					precioFiltro.getPrecio().getMaterialEstructura().setCodmaterialestructura(new BigDecimal(valor));
				} else if ("778".equals(key)) {
					// Tipo instalacion
					precioFiltro.getPrecio().getTipoInstalacion().setCodtipoinstalacion((new BigDecimal(valor)));
				}
			}
		}
	}

	private void valorPorDefectoFitros(HashMap<String, String> filtroMascara) {
		// Hay datos variables que si van vacios se le asigna un 0. Para poder
		// calcular el precio y produccion. Los campos son los siguientes:
		// Tipo Marco Plantacion, Sistema Produccion, Tipo Plantacion, Practica
		// Cultural, Tipo Capital, y Destino.

		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if ("116".equals(key) || "616".equals(key) || "173".equals(key) || "133".equals(key) || "126".equals(key)
					|| "110".equals(key)) {
				valor = (valor == null || "".equals(valor) || "-1".equals(valor)) ? "0" : valor;
				filtroMascara.put(key, valor);
			}
		}
	}

	// AMG 18/02/2014 clonacion parcela de Anexo
	public Parcela clonarParcelaAnexo(Long codParcela) {
		// Facturacion. por cada poliza clonada,facturamos.
		// cargamos la parcela para obtener el idPoliza
		Parcela parcela2 = (Parcela) datosParcelaAnexoDao.getObject(Parcela.class, codParcela); // get original
		// con el idPoliza cargamos la poliza para obtener el codUsuario
		Poliza pol = (Poliza) datosParcelaAnexoDao.getObject(Poliza.class,
				parcela2.getAnexoModificacion().getPoliza().getIdpoliza());
		// Con el codUsuario cargamos el objeto Usuario para hacer la facturaciion
		Usuario usuario = (Usuario) datosParcelaAnexoDao.getObject(Usuario.class, pol.getUsuario().getCodusuario());

		Parcela clonParcela = new Parcela();

		// HOJA & NÃšMERO
		AnexoModificacion anexoModificacionAux = (AnexoModificacion) datosParcelaAnexoDao
				.getObject(AnexoModificacion.class, parcela2.getAnexoModificacion().getId());
		// getHojaNumero(anexoModificacionAux,parcela, null,ALTA_ESTRUCTURA_PARCELA);

		ParcelaVO parVO = new ParcelaVO();
		parVO.setCodParcela(codParcela.toString());
		clonParcela.setCodprovincia(parcela2.getCodprovincia());
		clonParcela.setCodtermino(parcela2.getCodtermino());
		clonParcela.setSubtermino(parcela2.getSubtermino());
		getHojaNumero(anexoModificacionAux, clonParcela, parVO, REPLICAR_PARCELA);
		Parcela parcela = (Parcela) datosParcelaAnexoDao.clonarParcelaAnexo(codParcela, usuario, clonParcela);
		return parcela;
	}

	/**
	 * FIN PRECIO/PRODUCCION
	 */
	
	public CapitalAseguradoVO getCapitalAsegurado(final Long idcapitalasegurado) throws BusinessException {
		CapitalAseguradoVO capAsegVO;
		try {
			CapitalAsegurado capAseg = (CapitalAsegurado) this.parcelaModificacionPolizaDao.get(CapitalAsegurado.class,
					idcapitalasegurado);
			capAsegVO = ParcelaAnexoUtil.getCapitalAseguradoVO(capAseg);
		} catch (DAOException e) {
			logger.error("Error al obtener el tipo de capital: " + e.getMessage());
			throw new BusinessException(e);
		}
		return capAsegVO;
	}

	public void setParcelaModificacionPolizaDao(IParcelaModificacionPolizaDao parcelaModificacionPolizaDao) {
		this.parcelaModificacionPolizaDao = parcelaModificacionPolizaDao;
	}

	public void setDatosParcelaAnexoDao(IDatosParcelaAnexoDao datosParcelaAnexoDao) {
		this.datosParcelaAnexoDao = datosParcelaAnexoDao;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}
}
