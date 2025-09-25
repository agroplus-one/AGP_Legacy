package com.rsi.agp.core.managers.impl.ganado;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.core.managers.IDatosExplotacionesManager;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega.AmbitoAgroseguro;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega.AmbitoAgroseguro.Comarca;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega.AmbitoAgroseguro.Provincia;
import com.rsi.agp.core.managers.impl.ganado.InformacionRega.AmbitoAgroseguro.Subtermino;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ExplotacionCoberturaComparator;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.models.commons.ITerminoDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ganado.IDatosExplotacionDao;
import com.rsi.agp.dao.models.poliza.ganado.IPrecioGanadoDao;
import com.rsi.agp.dao.tables.admin.ClaseDetalleGanado;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Termino;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.cpl.gan.Especie;
import com.rsi.agp.dao.tables.cpl.gan.GruposRazas;
import com.rsi.agp.dao.tables.cpl.gan.MascaraPrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.PrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.RegimenManejo;
import com.rsi.agp.dao.tables.cpl.gan.TiposAnimalGanado;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.explotaciones.DatosVariable;
import com.rsi.agp.dao.tables.poliza.explotaciones.Explotacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCobertura;
import com.rsi.agp.dao.tables.poliza.explotaciones.ExplotacionCoberturaVinculacion;
import com.rsi.agp.dao.tables.poliza.explotaciones.GrupoRaza;
import com.rsi.agp.dao.tables.poliza.explotaciones.PrecioAnimalesModulo;
import com.rsi.agp.dao.tables.poliza.explotaciones.SWModulosCoberturasExplotacion;

import es.agroseguro.modulosYCoberturas.ModulosYCoberturas;
import es.agroseguro.modulosYCoberturas.ModulosYCoberturasDocument;
import es.agroseguro.serviciosweb.contratacionayudas.ModulosCoberturasResponse;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DatosExplotacionesManager implements IDatosExplotacionesManager {

	private IDatosExplotacionDao datosExplotacionDao;
	private IPrecioGanadoDao precioGanadoDao;
	private IDatosParcelaDao datosParcelaDao;
	private ITerminoDao terminoDao;
	private IPolizaDao polizaDao;

	// ILineaDao requerido para obtener el objeto 'Linea' y acceder a su atributo 'fechaInicioContratacion'
	private ILineaDao lineaDao;
		
	private static final Log logger = LogFactory.getLog(DatosExplotacionesManager.class);
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private ContratacionAyudasHelper contratacionAyudasHelper = new ContratacionAyudasHelper();

	@Override
	public List<ConfiguracionCampo> cargarDatosVariables(Long lineaseguroid) {

		try {
			return datosParcelaDao.getListConfigCampos(new BigDecimal(
					datosParcelaDao.getIdPantallaConfigurable(lineaseguroid, Constants.PANTALLA_EXPLOTACIONES)));
		} catch (Exception e) {
			logger.error(
					"Error al obtener la lista de campos configurados para la pantalla de explotaciones de la linea "
							+ lineaseguroid,
					e);
		}

		return new ArrayList<ConfiguracionCampo>();
	}

	@Override
	public Map<String, Object> alta(Explotacion explotacion) {

		// Validaciones previas al alta de explotacion
		Map<String, Object> paramReturn = validacionesPrevias(explotacion);
		// Si se han encontrado errores de validacion no se prosigue con el alta
		if (!paramReturn.isEmpty()) {
			paramReturn.put("explotacionBean", explotacion);
			return paramReturn;
		}

		// Alta de registro de explotacion
		try {
			Explotacion e = (Explotacion) this.datosExplotacionDao.saveOrUpdate(explotacion);
			paramReturn.put(Constants.KEY_MENSAJE, bundle.getString("mensaje.datosExplotacion.altaOK"));
			paramReturn.put("explotacionBean", e);
		} catch (Exception e) {
			logger.error("Error al guardar la explotacion", e);
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.altaKO"));
			paramReturn.put("explotacionBean", explotacion);
		}

		return paramReturn;
	}

	/**
	 * Devuelve el precio correspondiente a la explotacion indicada
	 * 
	 * @throws FechaPrecioGanadoException
	 * @throws PrecioGanadoException
	 */
	public List<PrecioAnimalesModulo> calcularPrecio(Explotacion explotacion) throws PrecioGanadoException {

		List<PrecioAnimalesModulo> lista = new ArrayList<PrecioAnimalesModulo>();

		// Obtiene la poliza asociada a la explotacion
		Poliza poliza = getPoliza(explotacion.getPoliza().getIdpoliza());
		if (poliza == null) {
			return lista;
		}
		explotacion.setPoliza(poliza);

		// Para cada uno de los modulos de la poliza - Iterar sobre los modulos de la
		// poliza - PENDIENTE!!
		Set<ModuloPoliza> mp = poliza.getModuloPolizas();

		for (ModuloPoliza moduloPoliza : mp) {

			// Obtener mascaras de precios asociadas a los datos de la explotacion -
			// PENDIENTE!!
			String codModulo = moduloPoliza.getId().getCodmodulo();
			List<MascaraPrecioGanado> lstMascPrecio = precioGanadoDao.getMascarasPrecioExplotacion(explotacion,
					codModulo);

			// Si hay mascaras de precio para la explotacion hay que comprobar que se haya
			// informado de todos los datos variables asociados
			boolean buscarPrecio = true;
			PrecioGanado pgDv = new PrecioGanado();
			if (!lstMascPrecio.isEmpty()) {

				for (GrupoRaza grupoRaza : explotacion.getGrupoRazas()) {

					Set<DatosVariable> lstDatVariables = grupoRaza.getDatosVariables();

					for (MascaraPrecioGanado mascaraPrecioGanado : lstMascPrecio) {

						// DNF ESC-6756-VS2 22/08/2019**********************************
						// int codconcepto =
						// mascaraPrecioGanado.getDiccionarioDatos().getCodconcepto().intValue();
						int codconcepto = 0;
						if (null != mascaraPrecioGanado.getDiccionarioDatos()
								&& null != mascaraPrecioGanado.getDiccionarioDatos().getCodconcepto()) {
							codconcepto = mascaraPrecioGanado.getDiccionarioDatos().getCodconcepto().intValue();
							logger.debug("El valor de codconcepto es: "
									+ mascaraPrecioGanado.getDiccionarioDatos().getCodconcepto().intValue());
						}
						// FIN DNF ESC-6756-VS2 22/08/2019******************************

						if (ArrayUtils.contains(new int[] { ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_DESDE,
								ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_HASTA }, codconcepto)) {
							Long numAnimales = grupoRaza.getNumanimales();
							if (numAnimales != null) {
								switch (codconcepto) {
								case ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_DESDE:
									pgDv.setNumAnimalesAcumDesde(numAnimales);
									break;
								case ConstantsConceptos.CODCPTO_NUM_ANIM_ACUM_HASTA:
									pgDv.setNumAnimalesAcumHasta(numAnimales);
									break;
								default:
									break;
								}
							} else {
								buscarPrecio = false;
								break;
							}
						} else {
							if (!isDVInformado(lstDatVariables, mascaraPrecioGanado, pgDv)) {
								buscarPrecio = false;
								break;
							}
						}
					}
				}

			}

			// Si todos los DV necesarios para el calculo de precio estan informados
			if (buscarPrecio) {

				// Obtener el registro de la tabla de precios de Ganado para los datos de la
				// explotacion
				PrecioGanado precioGanado = precioGanadoDao.getPrecioExplotacion(explotacion, codModulo, pgDv);

				// Crear el objeto PrecioAnimalesModulo correspondiente al precio obtenido y
				// anhadirlo a la lista
				// Si se ha encontrado precio
				if (precioGanado != null) {
					lista.add(new PrecioAnimalesModulo(precioGanado.getModulo().getId().getCodmodulo(),
							precioGanado.getPrecioGanadoDesde(), precioGanado.getPrecioGanadoHasta()));
				} else {
					// Si no se ha encontrado precio
					lista.add(new PrecioAnimalesModulo(codModulo, null, null));
				}
			} else {
				// Si no se ha buscado precio
				lista.add(new PrecioAnimalesModulo(codModulo, null, null));
			}
		}

		return lista;
	}

	/**
	 * Obtiene el objeto Poliza asociado a la explotacion
	 * 
	 * @param e
	 * @return
	 */
	public Explotacion getExplotacion(Long id) {
		try {
			Explotacion exp = (Explotacion) precioGanadoDao.get(Explotacion.class, id);

			// Si tiene datos variables, se cargan las descripciones de los configurados por
			// lupa
			for (GrupoRaza gr : exp.getGrupoRazas()) {
				for (DatosVariable dv : gr.getDatosVariables()) {
					if (dv.getValor() != null)
						dv.setDesValor(getDescDatoVariable(dv.getCodconcepto(), dv.getValor()));
				}
			}

			return exp;

		} catch (Exception e1) {
			logger.error("Error al obtener la explotacion con id " + id, e1);
			return null;
		}
	}

	@Override
	public void borrarListaDatosVariables(Explotacion ex) {
		try {
			for (final GrupoRaza grupoRaza : ex.getGrupoRazas()) {

				if (grupoRaza.getId() != null) {
					List<DatosVariable> lista = this.datosExplotacionDao.findFiltered(DatosVariable.class,
							new String[] { "grupoRaza.id" }, new Object[] { grupoRaza.getId() }, null);

					if (!lista.isEmpty()) {
						grupoRaza.getDatosVariables().removeAll(lista);
						this.datosExplotacionDao.deleteAll(lista);
					}
				}

			}
		} catch (Exception e) {
			logger.error("Error al borrar la lista de datos variables asociada a la explotacion", e);
		}

	}

	@Override
	public void borrarListaDatosVariables(Explotacion ex, String gruporazaid) {
		if (!StringUtils.nullToString(gruporazaid).equals("")) {
			try {
				for (final GrupoRaza grupoRaza : ex.getGrupoRazas()) {

					if (grupoRaza.getId().equals(new Long(gruporazaid))) {
						List<DatosVariable> lista = this.datosExplotacionDao.findFiltered(DatosVariable.class,
								new String[] { "grupoRaza.id" }, new Object[] { grupoRaza.getId() }, null);

						if (!lista.isEmpty()) {
							grupoRaza.getDatosVariables().removeAll(lista);
							this.datosExplotacionDao.deleteAll(lista);
						}
					}

				}
			} catch (Exception e) {
				logger.error("Error al borrar la lista de datos variables asociada a la explotacion", e);
			}
		}
	}

	@Override
	public void borrarGrupoRaza(Explotacion ex, String idgruporaza) {
		try {
			List<GrupoRaza> listagr = this.datosExplotacionDao.findFiltered(GrupoRaza.class, new String[] { "id" },
					new Object[] { new Long(idgruporaza) }, null);

			if (!listagr.isEmpty()) {
				ex.getGrupoRazas().removeAll(listagr);
				this.datosExplotacionDao.deleteAll(listagr);
			}
		} catch (Exception e) {
			logger.error("Error al borrar la lista de datos variables asociada a la explotacion", e);
		}
	}

	public String getDescDatoVariable(final Integer codCpto, final String valor) {
		return this.datosParcelaDao.getDescDatoVariable(null, null, codCpto, valor);
	}

	/**
	 * Devuelve un boolean indicando si el concepto de la mascara esta incluido en
	 * los datos variables
	 * 
	 * @param lstDatVariables
	 * @param mpg
	 * @return
	 */
	private boolean isDVInformado(Set<DatosVariable> lstDatVariables, MascaraPrecioGanado mpg, PrecioGanado pgDv) {

		// DNF ESC-6756-VS2 22/08/2019**********************************
		if (mpg.getDiccionarioDatos() != null && mpg.getDiccionarioDatos().getCodconcepto() != null) {
			// FIN DNF ESC-6756-VS2 22/08/2019******************************
			Iterator<DatosVariable> iter = lstDatVariables.iterator();
			while (iter.hasNext()) {

				DatosVariable dv = (DatosVariable) iter.next();

				if (dv.getCodconcepto() != null
						&& dv.getCodconcepto().equals(mpg.getDiccionarioDatos().getCodconcepto().intValue())) {

					switch (dv.getCodconcepto().intValue()) {
					// Control Oficial Lechero - 1045
					case ConstantsConceptos.CODCPTO_CONTROL_OFICIAL_LECHERO:
						pgDv.setCodControlOficialLechero(new Long(dv.getValor()).longValue());
						break;
					// Pureza - 1046
					case ConstantsConceptos.CODCPTO_PUREZA:
						pgDv.setCodPureza(new Long(dv.getValor()).longValue());
						break;
					// IGP/DO Ganado - 1051
					case ConstantsConceptos.CODCPTO_IGPDO:
						pgDv.setCodIgpdoGanado(new Long(dv.getValor()).longValue());
						break;
					// Empresa gestora - 1049
					case ConstantsConceptos.CODCPTO_EMPRESA_GESTORA:
						pgDv.setCodGestora(new Long(dv.getValor()).longValue());
						break;
					// Sistema de almacenamiento - 1048
					case ConstantsConceptos.CODCPTO_SISTEMA_ALMACENAMIENTO:
						pgDv.setCodSistAlmacena(new Long(dv.getValor()).longValue());
						break;
					// Excepcion contratacion - Explotacion - 1063
					case ConstantsConceptos.CODCPTO_EXCP_CONTRATACION_EXPL:
						pgDv.setCodExcepContrExc(new Long(dv.getValor()).longValue());
						break;
					// Excepcion contratacion - Poliza - 1111
					case ConstantsConceptos.CODCPTO_EXCP_CONTRATACION_PLZ:
						pgDv.setCodExcepContrPol(new Long(dv.getValor()).longValue());
						break;
					// Tipo ganaderia - 1052
					case ConstantsConceptos.CODCPTO_TIPO_GANADERIA:
						pgDv.setCodTipoGanaderia(new Long(dv.getValor()).longValue());
						break;
					// Alojamiento - 1053
					case ConstantsConceptos.CODCPTO_ALOJAMIENTO:
						pgDv.setCodAlojamiento(new Long(dv.getValor()).longValue());
						break;
					// Destino - 110
					case ConstantsConceptos.CODCPTO_DESTINO:
						pgDv.setCodDestino(new Long(dv.getValor()).longValue());
						break;
					// Sistema de produccion - 616
					case ConstantsConceptos.CODCPTO_SISTEMA_PRODUCCION:
						pgDv.setSistemaProduccion(new SistemaProduccion(new BigDecimal(dv.getValor()), null));
						break;
					// prodAnualMedia (Calidad Produccion) - 1047
					case ConstantsConceptos.CODCPTO_PROD_ANUAL_MEDIA:
						pgDv.setProdAnualMedia(new BigDecimal(dv.getValor()));
						break;
					default:
						break;
					}

					return true;
				}
			}
			// DNF ESC-6756-VS2 22/08/2019**********************************
		} else {
			return true;
		}
		// FIN DNF ESC-6756-VS2 22/08/2019******************************
		return false;
	}

	/**
	 * Obtiene el objeto Poliza asociado a la explotacion
	 * 
	 * @param e
	 * @return
	 */
	public Poliza getPoliza(Long idPoliza) {
		// return (Poliza) precioGanadoDao.get(Poliza.class, idPoliza);
		// return (Poliza) precioGanadoDao.getObject(Poliza.class, idPoliza);
		return (Poliza) precioGanadoDao.getObject(Poliza.class, "idpoliza", idPoliza);
	}

	public void getListaCodigosLupasExplotaciones(Long idClase, Map<String, Object> parametros) {
		List<ClaseDetalleGanado> detalles = datosExplotacionDao.getObjects(ClaseDetalleGanado.class, "clase.id",
				idClase);
		if (null != detalles && detalles.size() > 0) {
			List<Integer> codEspecies = new ArrayList<Integer>();
			List<Integer> codRegimenes = new ArrayList<Integer>();
			List<Integer> codGruposRazas = new ArrayList<Integer>();
			List<Integer> codTiposCapital = new ArrayList<Integer>();
			List<Integer> codTiposAnimal = new ArrayList<Integer>();
			List<Integer> codProvincias = new ArrayList<Integer>();
			List<Integer> codComarcas = new ArrayList<Integer>();
			List<Integer> codTerminos = new ArrayList<Integer>();
			List<Character> codSubterminos = new ArrayList<Character>();
			for (ClaseDetalleGanado detalle : detalles) {
				// ESPECIES
				Integer codEspecie = detalle.getCodespecie();
				if (!codEspecies.contains(codEspecie))
					codEspecies.add(codEspecie);

				// REGIMEN MANEJO
				Integer codRegimen = detalle.getCodregimen();
				if (!codRegimenes.contains(codRegimen))
					codRegimenes.add(codRegimen);

				// GRUPOS RAZAS
				Integer codGrupoRaza = detalle.getCodgruporaza();
				if (!codGruposRazas.contains(codGrupoRaza))
					codGruposRazas.add(codGrupoRaza);

				// TIPOS CAPITAL
				Integer codTipoCapital = detalle.getCodtipocapital();
				if (!codTiposCapital.contains(codTipoCapital))
					codTiposCapital.add(codTipoCapital);

				// TIPOS ANIMAL
				Integer codTipoAnimal = detalle.getCodtipoanimal();
				if (!codTiposAnimal.contains(codTipoAnimal))
					codTiposAnimal.add(codTipoAnimal);

				// PROVINCIA
				Integer codProvincia = detalle.getCodprovincia();
				if (!codProvincias.contains(codProvincia))
					codProvincias.add(codProvincia);
				// COMARCA
				Integer codComarca = detalle.getCodcomarca();
				if (!codComarcas.contains(codComarca))
					codComarcas.add(codComarca);
				// TERMINO
				Integer codTermino = detalle.getCodtermino();
				if (!codTerminos.contains(codTermino))
					codTerminos.add(codTermino);
				// SUBTERMINO
				Character codSubtermino = detalle.getSubtermino();
				if (!codSubterminos.contains(codSubtermino))
					codSubterminos.add(codSubtermino);
			}

			// Borramos las listas que contengan una clave 9, 99, 999 "TODOS"
			Integer todos99 = new Integer(99);
			Integer todos999 = new Integer(999);
			if (codProvincias.contains(todos99))
				codProvincias.clear();
			if (codComarcas.contains(todos99))
				codComarcas.clear();
			if (codTerminos.contains(todos999))
				codTerminos.clear();
			if (codEspecies.contains(todos999))
				codEspecies.clear();
			if (codRegimenes.contains(todos999))
				codRegimenes.clear();
			if (codGruposRazas.contains(todos999))
				codGruposRazas.clear();
			if (codTiposCapital.contains(todos999))
				codTiposCapital.clear();
			if (codTiposAnimal.contains(todos999))
				codTiposAnimal.clear();

			// Anhadimos las listas al mapa de parametros para la jsp
			if (codProvincias.size() > 0)
				parametros.put("listaCodProvincias", StringUtils.toValoresSeparadosXComas(codProvincias, false));
			if (codComarcas.size() > 0)
				parametros.put("listaCodComarcas", StringUtils.toValoresSeparadosXComas(codComarcas, false));
			if (codTerminos.size() > 0)
				parametros.put("listaCodTerminos", StringUtils.toValoresSeparadosXComas(codTerminos, false));
			if (codEspecies.size() > 0)
				parametros.put("listaCodEspecies", StringUtils.toValoresSeparadosXComas(codEspecies, false));
			if (codRegimenes.size() > 0)
				parametros.put("listaCodRegimenes", StringUtils.toValoresSeparadosXComas(codRegimenes, false));
			if (codGruposRazas.size() > 0)
				parametros.put("listaCodGruposRazas", StringUtils.toValoresSeparadosXComas(codGruposRazas, false));
			if (codTiposCapital.size() > 0)
				parametros.put("listaCodTiposCapital", StringUtils.toValoresSeparadosXComas(codTiposCapital, false));
			if (codTiposAnimal.size() > 0)
				parametros.put("listaCodTiposAnimal", StringUtils.toValoresSeparadosXComas(codTiposAnimal, false));
		}
	}

	/**
	 * Validaciones previas al alta/modificacion de explotaciones
	 * 
	 * @param e
	 *            Explotacion que se da de alta/modificac
	 * @return Mensajes generados en la validacion
	 */
	private Map<String, Object> validacionesPrevias(Explotacion e) {

		Map<String, Object> paramReturn = new HashMap<String, Object>();

		// validacion del termino asociado a la explotacion
		if (!esTerminoAsegurable(e)) {
			logger.debug("El termino asociado a la explotacion no es asegurable");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.terminoKO"));
			return paramReturn;
		}

		// validacion de la especie
		if (!esValidaEspecie(e)) {
			logger.debug("La especie asociada a la explotacion no es valida");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.especieKO"));
			return paramReturn;
		}

		// validacion del regimen
		if (!esValidoRegimen(e)) {
			logger.debug("El regimen asociado a la explotacion no es valido");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.regimenKO"));
			return paramReturn;
		}

		for (GrupoRaza grupoRaza : e.getGrupoRazas()) {

			// validacion del grupo de raza
			if (!esValidoGrupoRaza(e, grupoRaza)) {
				logger.debug("El grupo de raza asociado a la explotacion no es valido");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.grupoRazaKO"));
				return paramReturn;
			}

			// validacion del tipo de capital
			if (!esValidoTipoCapital(grupoRaza)) {
				logger.debug("El tipo de capital asociado a la explotacion no es valido");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.tipoCapitalKO"));
				return paramReturn;
			}

			// validacion del tipo de animal
			if (!esValidoTipoAnimal(e, grupoRaza)) {
				logger.debug("El tipo de animal asociado a la explotacion no es valido");
				paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.tipoAnimalKO"));
				return paramReturn;
			}
		}

		return paramReturn;
	}

	/**
	 * Comprueba si el termino asociado a la explotacion es valido
	 * 
	 * @param e
	 *            Explotacion
	 * @return Booleano
	 */
	private boolean esTerminoAsegurable(Explotacion e) {

		return esValidoGenerico(VistaTerminosAsegurable.class,
				new String[] { "id.lineaseguroid", "id.codprovincia", "id.codcomarca", "id.codtermino",
						"id.subtermino" },
				new Object[] { e.getPoliza().getLinea().getLineaseguroid(), e.getTermino().getId().getCodprovincia(),
						e.getTermino().getId().getCodcomarca(), e.getTermino().getId().getCodtermino(),
						e.getTermino().getId().getSubtermino(), });

	}

	/**
	 * Comprueba si la especie asociada a la explotacion es valida
	 * 
	 * @param e
	 * @return
	 */
	private boolean esValidaEspecie(Explotacion e) {
		return esValidoGenerico(Especie.class, new String[] { "id.lineaseguroid", "id.codespecie" },
				new Object[] { e.getPoliza().getLinea().getLineaseguroid(), e.getEspecie() });
	}

	/**
	 * Comprueba si el regimen asociado a la explotacion es valido
	 * 
	 * @param e
	 * @return
	 */
	private boolean esValidoRegimen(Explotacion e) {
		return esValidoGenerico(RegimenManejo.class, new String[] { "id.lineaseguroid", "id.codRegimen" },
				new Object[] { e.getPoliza().getLinea().getLineaseguroid(), e.getRegimen() });
	}

	/**
	 * Comprueba si el grupo de raza asociado a la explotacion es valido
	 * 
	 * @param e
	 * @return
	 */
	private boolean esValidoGrupoRaza(Explotacion e, GrupoRaza gr) {
		return esValidoGenerico(GruposRazas.class, new String[] { "id.lineaseguroid", "id.CodGrupoRaza" },
				new Object[] { e.getPoliza().getLinea().getLineaseguroid(), new Long(gr.getCodgruporaza()) });
	}

	/**
	 * Comprueba si el tipo de capital asociado a la explotacion es valido
	 * 
	 * @param e
	 * @return
	 */
	private boolean esValidoTipoCapital(GrupoRaza gr) {
		return esValidoGenerico(TipoCapital.class, new String[] { "codtipocapital" },
				new Object[] { gr.getCodtipocapital() });
	}

	/**
	 * Comprueba si el tipo de animal asociado a la explotacion es valido
	 * 
	 * @param e
	 * @return
	 */
	private boolean esValidoTipoAnimal(Explotacion e, GrupoRaza gr) {
		return esValidoGenerico(TiposAnimalGanado.class, new String[] { "id.lineaseguroid", "id.codTipoAnimal" },
				new Object[] { e.getPoliza().getLinea().getLineaseguroid(), new Long(gr.getCodtipoanimal()) });
	}

	/**
	 * Comprueba si existen registros de la entidad indicada por 'clase' filtrando
	 * por los campos indicados por 'campos' por los valores indicados por 'valores'
	 * 
	 * @param clase
	 *            Clase asociada a la entidad donde se va a buscar
	 * @param campos
	 *            Lista de nombres de campos por los que se filtra
	 * @param valores
	 *            Lista de valores de campos por los que se filtra
	 * @return
	 */
	private boolean esValidoGenerico(Class clase, String[] campos, Object[] valores) {

		try {
			List<Object> lista = this.datosExplotacionDao.findFiltered(clase, campos, valores, null);
			return (lista != null && !lista.isEmpty());
		} catch (Exception e1) {
			logger.error("Error al obtener el registro de " + clase + " filtrando por los campos " + campos
					+ " por los valores " + valores);
		}

		return false;
	}

	@Override
	public Termino obtenerTermino(BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino,
			Character subtermino) {
		Termino term = null;
		try {
			term = terminoDao.getTermino(codprovincia, codcomarca, codtermino, subtermino);
		} catch (DAOException e) {
			logger.error("Excepcion : DatosExplotacionesManager - obtenerTermino", e);
		}
		return term;
	}

	/**
	 * Valida los datos identificativos del formulario de explotacion
	 * 
	 * @param explotacion
	 * @return
	 */
	public Map<String, Object> validacionesPreviasDatosIdentificativos(Explotacion explotacion) {

		Map<String, Object> paramReturn = new HashMap<String, Object>();

		// Validacion del termino asociado a la explotacion
		if (!esTerminoAsegurable(explotacion)) {
			logger.debug("El termino asociado a la explotacion no es asegurable");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.terminoKO"));
			return paramReturn;
		}

		// Validacion de la especie
		if (!esValidaEspecie(explotacion)) {
			logger.debug("La especie asociada a la explotacion no es valida");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.especieKO"));
			return paramReturn;
		}

		// Validacion del regimen
		if (!esValidoRegimen(explotacion)) {
			logger.debug("El regimen asociado a la explotacion no es valido");
			paramReturn.put(Constants.KEY_ALERTA, bundle.getString("mensaje.datosExplotacion.regimenKO"));
			return paramReturn;
		}
		return paramReturn;
	}

	// Setter para Spring
	public void setDatosExplotacionDao(IDatosExplotacionDao datosExplotacionDao) {
		this.datosExplotacionDao = datosExplotacionDao;
	}

	public void setPrecioGanadoDao(IPrecioGanadoDao precioGanadoDao) {
		this.precioGanadoDao = precioGanadoDao;
	}

	public void setDatosParcelaDao(IDatosParcelaDao datosParcelaFLDao) {
		this.datosParcelaDao = datosParcelaFLDao;
	}

	public void setTerminoDao(ITerminoDao terminoDao) {
		this.terminoDao = terminoDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	@Override
	public Explotacion getExplotacion(Long idPoliza, Integer numero) {
		try {
			Explotacion exp = null;
			String campos[] = { "poliza.idpoliza", "numero" };
			Object valores[] = { idPoliza, numero };

			List<Explotacion> lista = datosExplotacionDao.findFiltered(Explotacion.class, campos, valores, "numero");
			if (lista != null && lista.size() > 0) {
				exp = lista.get(0);
				// Si tiene datos variables, se cargan las descripciones de los configurados por
				// lupa
				for (GrupoRaza gr : exp.getGrupoRazas()) {
					for (DatosVariable dv : gr.getDatosVariables()) {
						if (dv.getValor() != null)
							dv.setDesValor(getDescDatoVariable(dv.getCodconcepto(), dv.getValor()));
					}
				}
			}

			return exp;

		} catch (Exception e1) {
			logger.error("Error al obtener la explotacion de la poliza " + idPoliza + " y numero " + numero, e1);
			return null;
		}
	}

	public boolean isCoberturasElegiblesNivelExplotacion(Long lineaseguroId, Set<ModuloPoliza> modsPoliza) {
		String codModulos = "";
		boolean primera = true;
		for (ModuloPoliza modP : modsPoliza) {
			if (!primera)
				codModulos = codModulos + ",";
			codModulos = codModulos + "'" + modP.getId().getCodmodulo() + "'";
			primera = false;
		}
		boolean isCoberturas = datosExplotacionDao.isCoberturasElegiblesNivelExplotacion(lineaseguroId, codModulos);
		return isCoberturas;
	}

	/**
	 * Unifica en un set todos los grupos de raza de la explotacion
	 * 
	 * @param exp
	 *            Exlotacion
	 * @param setExpBBDD
	 *            Set de la explotacion de BBDD
	 */
	private void cargarRestoGruposRazas(Explotacion exp, Set<Explotacion> setExpBBDD) {
		logger.debug("Inicio - DatosExplotacionesManager - cargarRestoGruposRazas");
		GrupoRaza grInicial = exp.getGrupoRazas().iterator().next();
		Set<GrupoRaza> setExp = exp.getGrupoRazas();
		if (setExpBBDD != null && setExpBBDD.size() > 0 && exp.getId() != null) {
			for (Explotacion exBBDD : setExpBBDD) {
				if (exBBDD.getId().toString().equals(exp.getId().toString())) {
					Set<GrupoRaza> setGrBBDD = exBBDD.getGrupoRazas();
					for (GrupoRaza grBBDD : setGrBBDD) {
						if (grBBDD.getCodgruporaza().toString().equals(grInicial.getCodgruporaza().toString())
								&& grBBDD.getCodtipocapital().toString()
										.equals(grInicial.getCodtipocapital().toString())
								&& grBBDD.getCodtipoanimal().toString()
										.equals(grInicial.getCodtipoanimal().toString())) {
							logger.debug("grupoRaza: " + grBBDD.getCodgruporaza() + "-" + grInicial.getCodtipocapital()
									+ "-" + grInicial.getCodtipoanimal() + " ya introducido");
						} else {
							logger.debug("grupoRaza: " + grBBDD.getCodgruporaza() + "-" + grBBDD.getCodtipocapital()
									+ "-" + grBBDD.getCodtipoanimal() + " nuevo, inserto");
							GrupoRaza gr = new GrupoRaza();
							gr.setCodgruporaza(grBBDD.getCodgruporaza());
							gr.setCodtipoanimal(grBBDD.getCodtipoanimal());
							gr.setCodtipocapital(grBBDD.getCodtipocapital());
							gr.setNumanimales(grBBDD.getNumanimales());
							// FALTA METER LOS DATOS VARIABLES
							Set<DatosVariable> setDVOrigen = new HashSet<DatosVariable>();
							Set<DatosVariable> setDVBBDD = grBBDD.getDatosVariables();
							for (DatosVariable dtBBDD : setDVBBDD) {
								DatosVariable dv = new DatosVariable(null, gr, null, null, dtBBDD.getCodconcepto(),
										dtBBDD.getValor());
								setDVOrigen.add(dv);
								logger.debug("DV de explotacion " + grBBDD.getCodgruporaza() + "-"
										+ grBBDD.getCodtipocapital() + "-" + grBBDD.getCodtipoanimal() + " cargado: "
										+ dv.getCodconcepto() + " = " + dv.getValor());
							}
							gr.setDatosVariables(setDVOrigen);
							setExp.add(gr);
						}
					}
					exp.setGrupoRazas(setExp);
				}
			}

		}

		// Recoge los parametros de la explotacion necesarios para el calculo y
		// crea el objeto que los encapsula
		logger.debug("End - DatosExplotacionesManager - cargarRestoGruposRazas");
	}

	/**
	 * Obtiene las coberturas elegibles de una explotacion
	 */
	public List<ExplotacionCobertura> getCoberturasElegiblesExplotacion(Explotacion exp, String realPath,
			String codUsuario, String cobExistentes) {
		logger.info("Init - DatosExplotacionesManager - getCoberturasElegiblesExplotacion");
		String xmlData = null;
		List<ExplotacionCobertura> lstCob = new ArrayList<ExplotacionCobertura>();
		try {

			Poliza plz = getPoliza(exp.getPoliza().getIdpoliza());
			Set<ModuloPoliza> modsPoliza = plz.getModuloPolizas();

			// anhadimos los grupos de raza de BBDD a la explotacion
			try {
				cargarRestoGruposRazas(exp, plz.getExplotacions());
			} catch (Exception e) {
				logger.error(" Error al anhadir los grupos de raza de BBDD a la explotacion de la poliza: "
						+ exp.getPoliza().getIdpoliza(), e);
			}
			List<String> modulosP = new ArrayList<String>();
			for (ModuloPoliza modP : modsPoliza) {
				if (!modulosP.contains(modP.getId().getCodmodulo()))
					modulosP.add(modP.getId().getCodmodulo());
			}
			// Por cada modulo llamamos al SW
			for (String modP : modulosP) {
				String xml = WSUtils.generateXMLPolizaModulosCoberturas(plz, exp, null, null, modP, polizaDao);

				// guardar llamada al WS en BBDD
				String idExp = (exp.getId() != null ? exp.getId().toString() : "");
				Long idEnvio = guardarXmlEnvio(exp.getPoliza().getIdpoliza(), idExp, modP, xml, codUsuario);
				
				ModulosCoberturasResponse response = contratacionAyudasHelper.doModulosCoberturas(xml, realPath);

				if (null != response) {
					xmlData = com.rsi.agp.core.util.WSUtils.getStringResponse(response.getModulosCoberturas());
				}

				// guardar respuesta al WS en BBDD
				if (xmlData != null) {
					polizaDao.actualizaXmlCoberturas(idEnvio, "", xmlData);
				}
				logger.debug("Respuesta WS: " + xmlData);
				// Transformarmos las coberturas que nos vienen del WS en una lista de
				// explotacionesCoberturas
				es.agroseguro.modulosYCoberturas.ModulosYCoberturas modYCob = this.getMyCFromXml(xmlData);
				if (modYCob.getExplotaciones() != null) {
					es.agroseguro.modulosYCoberturas.Explotaciones explotaciones = modYCob.getExplotaciones();
					es.agroseguro.modulosYCoberturas.Explotacion[] explotacion = explotaciones.getExplotacionArray();
					Long contador = new Long(2000000);
					Long nuevas = new Long(1);
					for (es.agroseguro.modulosYCoberturas.Explotacion expo : explotacion) {
						es.agroseguro.modulosYCoberturas.Cobertura[] cobb = expo.getCoberturaArray();
						if (cobb.length > 0) {
							List<es.agroseguro.modulosYCoberturas.Cobertura> lstCobReg = Arrays.asList(cobb);
							for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : lstCobReg) {
								ExplotacionCobertura expCob = new ExplotacionCobertura();
								// CARGA COBERTURAS
								expCob.setId(contador);
								expCob.setCodmodulo(modP);
								expCob.setFila(cobertura.getFila());
								expCob.setCpm(cobertura.getConceptoPrincipalModulo());
								expCob.setCpmDescripcion(cobertura.getDescripcionCPM());
								expCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
								expCob.setRcDescripcion(cobertura.getDescripcionRC());
								expCob.setElegible(cobertura.getElegible().toString().charAt(0));
								expCob.setTipoCobertura(cobertura.getTipoCobertura().charAt(0));
								if (cobertura.getVinculacionFilaArray() != null) {
									String vinculadas = "";
									es.agroseguro.modulosYCoberturas.VinculacionFila[] vinFila = cobertura
											.getVinculacionFilaArray();
									for (es.agroseguro.modulosYCoberturas.VinculacionFila vFila : vinFila) {
										vinculadas += vFila.getElegida().toString().charAt(0);
										es.agroseguro.modulosYCoberturas.Fila[] fila = vFila.getFilaArray();
										for (es.agroseguro.modulosYCoberturas.Fila fi : fila) {
											vinculadas += "." + fi.getFila() + "."
													+ fi.getElegida().toString().charAt(0) + "." + modP + ".." + "|";
										}
									}
									if (!vinculadas.equals("")) {
										vinculadas = vinculadas.substring(0, vinculadas.length() - 1);
									}
									expCob.setVinculada(vinculadas);
								}
								lstCob.add(expCob);
								contador++;
								nuevas++;

								// Comprobamos si la cobertura tiene datos variables. Si son de tipo E se
								// guardan como tipo cobertura
								es.agroseguro.modulosYCoberturas.DatoVariable[] datArr = cobertura
										.getDatoVariableArray();
								if (datArr.length > 0) {
									List<es.agroseguro.modulosYCoberturas.DatoVariable> lstDatVar = Arrays
											.asList(datArr);
									for (es.agroseguro.modulosYCoberturas.DatoVariable datVar : lstDatVar) {
										if (datVar.getTipoValor() != null && datVar.getTipoValor().equals("E")) { // recogemos
																													// los
																													// datosvariables
																													// con
																													// tipovalor
																													// a
																													// E
											// CARGA DATO VAR COMO COBERTURAS

											es.agroseguro.modulosYCoberturas.Valor[] valArr = datVar.getValorArray();
											List<es.agroseguro.modulosYCoberturas.Valor> lstValores = Arrays
													.asList(valArr);
											for (es.agroseguro.modulosYCoberturas.Valor valor : lstValores) {
												expCob = new ExplotacionCobertura();
												expCob.setId(contador);
												expCob.setCodmodulo(modP);
												expCob.setFila(cobertura.getFila());
												expCob.setCpm(cobertura.getConceptoPrincipalModulo());
												expCob.setCpmDescripcion(cobertura.getDescripcionCPM());
												expCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
												expCob.setRcDescripcion(cobertura.getDescripcionRC());
												expCob.setElegible(cobertura.getElegible().toString().charAt(0));
												expCob.setTipoCobertura(cobertura.getTipoCobertura().charAt(0));
												expCob.setDvCodConcepto(new Long(datVar.getCodigoConcepto()));
												expCob.setDvDescripcion(datVar.getNombre());
												expCob.setDvValor(new Long(valor.getValor()));
												expCob.setDvValorDescripcion(valor.getDescripcion());
												expCob.setDvColumna(new Long(datVar.getColumna()));

												// coberturas datos variables
												String vinculadas = "";
												if (valor.getVinculacionCelda() != null) {
													es.agroseguro.modulosYCoberturas.VinculacionCelda vinCelda = valor
															.getVinculacionCelda();
													vinculadas += "X" + "." + vinCelda.getFilaMadre() + "." + "X" + "."
															+ modP + "." + vinCelda.getColumnaMadre() + "."
															+ vinCelda.getValorMadre();

													// if (!vinculadas.equals("")) {
													// vinculadas=vinculadas.substring(0, vinculadas.length()-1);
													// }
													expCob.setVinculada(vinculadas);
												}
												// fin coberturas datos variables

												lstCob.add(expCob);
												contador++;
												nuevas++;
											}
										}
									}
								}
								// FIN dato array
							}
						} else {
							logger.debug("el SW no devuelve coberturas de explotacion.");
						}
					}
				}

				// comprobamos si alguna de las que hay ya estaban seleccionadas
				if (!cobExistentes.equals("")) {
					boolean esDatoVar = false;
					String[] arrCobExistentes = cobExistentes.split(";");
					for (int i = 0; i < arrCobExistentes.length; i++) {
						esDatoVar = false;
						String[] cobExist = arrCobExistentes[i].split("\\|");
						String[] cb_dv = arrCobExistentes[i].split("#");
						String[] dv = cb_dv[1].split("\\|");
						logger.debug("Cob. ya existente: id: " + cobExist[0] + " mod: " + cobExist[1] + " fila: "
								+ cobExist[2] + " CPM: " + cobExist[3] + " descCPM: " + cobExist[4] + " RC: "
								+ cobExist[5] + " desc RC: " + cobExist[6] + " vinculada: " + cobExist[7]
								+ " elegible: " + cobExist[8] + " tipoCobertura: " + cobExist[9] + " checked: "
								+ cobExist[10].charAt(0));
						if (dv != null && dv.length > 4 && dv[0] != null && !dv[0].equals("null")
								&& !dv[0].equals("")) {
							logger.debug(" DV: codCptoDV: " + dv[0] + " desc DV: " + dv[1] + " valorDV: " + dv[2]
									+ " descValorDv: " + dv[3] + " columna: " + dv[4] + " elegido: " + dv[5]);
							esDatoVar = true;
						}
						for (ExplotacionCobertura cob : lstCob) {
							Integer fila = new Integer(cob.getFila());
							Integer cpm = new Integer(cob.getCpm());
							Integer rc = new Integer(cob.getRiesgoCubierto());
							logger.debug("id: " + cob.getId() + " fila: " + fila + " cpm: " + cpm + " rc: " + rc);
							// si modulo-fila-cpm-rg = igual q la q tengo, reviso si esta checked
							// compruebo que no sea cob. de tipo dato variable
							if (cobExist[1].equals(cob.getCodmodulo()) && cobExist[2].equals(fila.toString())
									&& cobExist[3].equals(cpm.toString()) && cobExist[5].equals(rc.toString())) {
								if (cob.getDvCodConcepto() == null && !esDatoVar) { // es cobertura normal
									cob.setId(Long.parseLong(cobExist[0]));
									if (cobExist[10].charAt(0) == 'S') {
										cob.setElegida('S');
										break;
									} else {
										cob.setElegida('N');
										break;
									}
								} else {
									if (cob.getDvCodConcepto() != null && esDatoVar) {
										if (dv[0].equals(cob.getDvCodConcepto().toString())
												&& dv[2].equals(cob.getDvValor().toString())) {
											cob.setId(Long.parseLong(cobExist[0]));
											if (cobExist[10].charAt(0) == 'S') {
												cob.setElegida('S');
											} else {
												cob.setElegida('N');
											}
											if (dv[5].charAt(0) == 'S') {
												cob.setDvElegido('S');
												break;
											} else {
												cob.setDvElegido('N');
												break;
											}
										}
									}
								}
							}
						} // fin lstCob
					}
				}
			}

			Collections.sort(lstCob, new ExplotacionCoberturaComparator());
			logger.info("End - DatosExplotacionesManager - getCoberturasElegiblesExplotacion");
			return lstCob;
		} catch (Exception e1) {
			logger.error(" Error al obtener las coberturas elegibles de la explotacion de la poliza: "
					+ exp.getPoliza().getIdpoliza(), e1);
			return null;
		}

	}

	private Long guardarXmlEnvio(Long idPoliza, String idExp, String codmodulo, String envio, String codUsuario)
			throws DAOException {
		logger.debug("init - guardarXmlEnvio");
		SWModulosCoberturasExplotacion doc = new SWModulosCoberturasExplotacion();
		doc.setIdpoliza(idPoliza);
		doc.setFecha((new GregorianCalendar()).getTime());

		doc.setEnvio(Hibernate.createClob(envio));
		doc.setRespuesta(Hibernate.createClob(" "));
		if (!idExp.equals(""))
			doc.setIdexplotacion(Long.parseLong(idExp));
		doc.setCodmodulo(codmodulo);
		doc.setUsuario(codUsuario);

		SWModulosCoberturasExplotacion newEnvio = (SWModulosCoberturasExplotacion) this.datosExplotacionDao
				.saveEnvioCobExplotacion(doc);
		this.polizaDao.actualizaXmlCoberturas(newEnvio.getId(), envio, "");
		logger.debug("end - guardarXmlEnvio");
		return newEnvio.getId();
	}

	public Set<ExplotacionCobertura> procesarCoberturas(Explotacion exp, String coberturas) {
		return procesarCoberturas(exp, coberturas, null, null);
	}

	public Set<ExplotacionCobertura> procesarCoberturas(Explotacion exp, String coberturas, String realPath,
			String codUsuario) {

		logger.info("Init - DatosExplotacionesManager - procesarCoberturas");

		if (realPath != null && codUsuario != null) {
			getCoberturasElegiblesExplotacion(exp, realPath, codUsuario, coberturas);
		}

		Set<ExplotacionCobertura> expCoberturas = exp.getExplotacionCoberturas();
		Set<ExplotacionCobertura> setExpCoberturasFinal = new HashSet<ExplotacionCobertura>();
		List<String> lstidsBBDD = new ArrayList<String>();
		List<String> lstidsPantalla = new ArrayList<String>();
		if (!coberturas.equals("")) {
			// list ids Existentes en BBDD
			Map<String, ExplotacionCobertura> keysBBDD = new HashMap<String, ExplotacionCobertura>();
			for (ExplotacionCobertura cob : expCoberturas) {
				lstidsBBDD.add(cob.getId().toString());
				keysBBDD.put(cob.getId().toString(), cob);
			}

			// comprobamos si alguna de las que hay ya estaban seleccionadas
			String[] arrCobExistentes = coberturas.split(";");
			Map<String, ExplotacionCobertura> keysPantalla = new HashMap<String, ExplotacionCobertura>();
			for (int i = 0; i < arrCobExistentes.length; i++) {
				String[] cobExist = arrCobExistentes[i].split("\\|");
				keysPantalla.put(cobExist[0], null);
				lstidsPantalla.add(cobExist[0]);
			}
			boolean esDatoVar = false;
			for (int i = 0; i < arrCobExistentes.length; i++) {
				esDatoVar = false;
				String[] cobExist = arrCobExistentes[i].split("\\|");
				String[] cb_dv = arrCobExistentes[i].split("#");
				String[] dv = cb_dv[1].split("\\|");
				logger.debug(" Cob. ya existente: id: " + cobExist[0] + " mod: " + cobExist[1] + " fila: " + cobExist[2]
						+ " CPM: " + cobExist[3] + " descCPM: " + cobExist[4] + " RC: " + cobExist[5] + " desc RC: "
						+ cobExist[6] + " vinculada: " + cobExist[7] + " elegible: " + cobExist[8] + " tipoCobertura: "
						+ cobExist[9] + " checked: " + cobExist[10].charAt(0));
				if (dv != null && dv.length > 4 && dv[0] != null && !dv[0].equals("null") && !dv[0].equals("")) {
					logger.debug(" DV: codCptoDV: " + dv[0] + " desc DV: " + dv[1] + " valorDV: " + dv[2]
							+ " descValorDv: " + dv[3] + " columna: " + dv[4] + " elegido: " + dv[5]);
					esDatoVar = true;
				}
				boolean yaExiste = false;
				for (ExplotacionCobertura cob : expCoberturas) {
					Integer fila = new Integer(cob.getFila());
					Integer cpm = new Integer(cob.getCpm());
					Integer rc = new Integer(cob.getRiesgoCubierto());
					logger.debug("id: " + cob.getId() + " fila: " + fila + " cpm: " + cpm + " rc: " + rc);
					// si modulo, fila, cpm, rg = igual q la q tengo, reviso si esta checked
					if (cobExist[1].equals(cob.getCodmodulo()) && cobExist[2].equals(fila.toString())
							&& cobExist[3].equals(cpm.toString()) && cobExist[5].equals(rc.toString())) {

						if (cob.getDvCodConcepto() == null && !esDatoVar) { // es cobertura normal
							cob.setId(Long.parseLong(cobExist[0]));
							cob.setVinculada(cobExist[7]);
							if (cobExist[10].charAt(0) == 'S') {
								cob.setElegida('S');
								// break;
							} else {
								cob.setElegida('N');
								// break;
							}
						} else {
							if (cob.getDvCodConcepto() != null && esDatoVar) {
								if (dv[0].equals(cob.getDvCodConcepto().toString())
										&& dv[2].equals(cob.getDvValor().toString())) {
									cob.setId(Long.parseLong(cobExist[0]));
									if (cobExist[10].charAt(0) == 'S') {
										cob.setElegida('S');
										// break;
									} else {
										cob.setElegida('N');
										// break;
									}
									if (dv[5].charAt(0) == 'S') {
										cob.setDvElegido('S');
										// break;
									} else {
										cob.setDvElegido('N');
									}
								}
							}
						}

					}
					if (cobExist[0].equals(cob.getId().toString())) { // cob ya la teniamos
						yaExiste = true;
						try {
							yaExiste = true;
							datosExplotacionDao.saveOrUpdate(cob);

							setExpCoberturasFinal.add(cob);

							keysBBDD.remove(cob.getId().toString());
							keysPantalla.remove(cob.getId().toString());
							lstidsBBDD.remove(cob.getId().toString());
						} catch (Exception e) {
							logger.error("Error al grabar la cobertura existente en BBDD ", e);
						}
						// lstidsExistentes.add(cob.getId().toString());
					}
				}

				if (!yaExiste) {// es nueva
					ExplotacionCobertura expCob = new ExplotacionCobertura();
					expCob.setExplotacion(exp);
					expCob.setCodmodulo(cobExist[1]);
					expCob.setFila(Integer.parseInt(cobExist[2]));
					expCob.setCpm(Integer.parseInt(cobExist[3]));
					expCob.setCpmDescripcion(cobExist[4]);
					expCob.setRiesgoCubierto(Integer.parseInt(cobExist[5]));
					expCob.setRcDescripcion(cobExist[6]);
					expCob.setElegible(cobExist[8].charAt(0));
					expCob.setTipoCobertura(cobExist[9].charAt(0));
					expCob.setElegida(cobExist[10].charAt(0));
					// dato variable
					if (esDatoVar) {
						// logger.debug(" DV: codCptoDV: "+dv[0]+ " desc DV: "+dv[1]+ " valorDV:
						// "+dv[2]+" descValorDv: "+dv[3]+" columna: "+dv[4]]+" elegido: "+dv[5]);
						if (dv[0] != null)
							expCob.setDvCodConcepto(new Long(dv[0]));
						if (dv[1] != null)
							expCob.setDvDescripcion(dv[1]);
						if (dv[2] != null)
							expCob.setDvValor(new Long(dv[2]));
						if (dv[3] != null)
							expCob.setDvValorDescripcion(dv[3]);
						if (dv[4] != null)
							expCob.setDvColumna(new Long(dv[4]));
						if (dv[5] != null)
							expCob.setDvElegido(dv[5].charAt(0));
					}

					// primero grabamos
					try {
						datosExplotacionDao.saveOrUpdate(expCob);
						setExpCoberturasFinal.add(expCob);

					} catch (Exception e) {
						logger.error("Error al grabar la nueva cobertura");
					}

					// Si tiene vinculada la creamos
					Set<ExplotacionCoberturaVinculacion> setVinc = new HashSet<ExplotacionCoberturaVinculacion>();
					if (!cobExist[7].equals("null") && !cobExist[7].equals("")) {

						String[] cobVincs = cobExist[7].split("#");
						for (String cobVinc : cobVincs) {
							String[] cobV = cobVinc.split("\\.");
							ExplotacionCoberturaVinculacion vi = new ExplotacionCoberturaVinculacion();
							vi.setExplotacionCobertura(expCob);
							vi.setFila(Integer.parseInt(cobV[1]));
							vi.setVinculacion(cobV[0].charAt(0));
							vi.setVinculacionElegida(cobV[2].charAt(0));
							if (cobV.length > 5) {
								vi.setDvColumna(new Long(cobV[4]));
								vi.setDvValor(new Long(cobV[5]));
							}
							try {
								datosExplotacionDao.saveOrUpdate(vi);
								setVinc.add(vi);

							} catch (Exception e) {
								logger.error("Error al grabar la nueva cobertura Vinculacion");
							}
						}

					}
					expCob.setExplotacionCoberturaVinculacions(setVinc);
				}
			}
			logger.debug("ids a borrar: " + lstidsBBDD.toString());
			if (lstidsBBDD.size() > 0) {
				try {
					datosExplotacionDao.deleteCoberturasByIdsCob(exp.getId(), lstidsBBDD);
				} catch (DAOException e) {
					logger.error("Error al borrar las coberturas.", e);
				}
			}
		} else {
			logger.debug("No se han recogido coberturas de la pantalla, las borramos.");
			exp.getExplotacionCoberturas().clear();
			try {
				datosExplotacionDao.deleteCoberturasById(exp.getId());
			} catch (DAOException e) {
				logger.error("Error al grabar la nueva explotacion sin coberturas.", e);
			}

		}
		logger.info("Fin - DatosExplotacionesManager - procesarCoberturas");
		return setExpCoberturasFinal;
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

	public String getTipoCapitalConGrupoNegocio(Boolean dependenNumAnimales) {
		List<Object> tc = null;
		String res = null;
		try {
			tc = datosExplotacionDao.getTipoCapitalConGrupoNegocio(dependenNumAnimales);
			if (null != tc && tc.size() > 0) {
				res = new String();
				for (int i = 0; i < tc.size(); i++) {
					Object[] tcgn = (Object[]) tc.get(i);
					res = res + tcgn[0].toString() + "-" + tcgn[1] + ";";
				}

			}
		} catch (DAOException e) {
			logger.error(
					"DatosExplotacionesManager.getTipoCapitalConGrupoNegocio - Error recogiendo los tipos de capital con grupo de negocio que no dependen del numero de animales. ",
					e);
		}
		return res;
	}

	public Linea getLinea(Long lineaSeguroId) {
		Linea linea = (Linea) datosExplotacionDao.getObject(Linea.class, lineaSeguroId);
		return linea;
	}

	@Override
	public InformacionRega getInfoRega(String codigoRega, String plan, String linea) throws RestWSException {
		
		InformacionRega infoRega = new InformacionRega();
		
		
		String json = WSRUtils.getInfoRega(codigoRega, plan, linea);
		try {	
			
			JSONObject jsonObject = new JSONObject(json);
			
			// ControlActual
			JSONObject controlActual = jsonObject.getJSONObject("rega").getJSONObject("controlActual");
			
			String explotacionRegistrada = ((controlActual.getString("explotacionRegistrada")=="true") ? "S&#237;" : "No");
			String fechaEfecto = controlActual.getString("fechaEfecto");
			String fechaVersionCenso = controlActual.getString("fechaVersionCenso");
			
			if (!"No".equals(explotacionRegistrada)) 
				infoRega.setExplotacionRegistrada(explotacionRegistrada);
			
			if (fechaEfecto != null && !fechaEfecto.equals("") && !fechaEfecto.equals("null")) {
			    final Calendar parsedFe = javax.xml.bind.DatatypeConverter.parseDateTime(fechaEfecto);
				infoRega.setFechaEfecto(new SimpleDateFormat("dd/MM/yyyy").format(parsedFe.getTime()));
			}
			
			if (fechaVersionCenso != null && !fechaVersionCenso.equals("") && !fechaVersionCenso.equals("null")) {
			    final Calendar parsedFvc = javax.xml.bind.DatatypeConverter.parseDateTime(fechaVersionCenso);
				infoRega.setFechaVersionCenso(new SimpleDateFormat("dd/MM/yyyy").format(parsedFvc.getTime()));
			}
			
			JSONArray lineas = controlActual.getJSONArray("linea");
			
			if (lineas!=null) {
				
				logger.debug("Lineas encontradas: " + lineas.length());
				
				for (int i=0;i<lineas.length();i++) {
					
					
				      JSONObject lineaJSON = lineas.getJSONObject(i);
				      InformacionRega.Linea lineaToAdd = infoRega.new Linea();
				      
				      String idLinea = lineaJSON.getString("linea"); 
				      
				      if (idLinea.equals(linea) || idLinea.equals("415")) {
				    	
					      String descLinea = lineaJSON.getString("descriptivo");
					      
					      if (!StringUtils.isNullOrEmpty(idLinea))
					    	  lineaToAdd.setId(idLinea);
					      if (!StringUtils.isNullOrEmpty(descLinea))
					    	  lineaToAdd.setDescriptivo(descLinea);
					      
					      JSONArray especiesArray = lineaJSON.getJSONArray("especie");
					      
					      for (int j=0;j<especiesArray.length();j++) {
					    	  
						      JSONObject especie = especiesArray.getJSONObject(j);

					    	  InformacionRega.Linea.Especie especieToAdd = lineaToAdd.new Especie();
					    	  
					    	  String descEspecie = especie.getString("descriptivo");
					    	  String idEspecie = especie.getString("especie");
					    	  
					    	  if (!StringUtils.isNullOrEmpty(descEspecie))
					    		  especieToAdd.setDescriptivo(descEspecie);
					    	  if (!StringUtils.isNullOrEmpty(idEspecie))
					    		  especieToAdd.setEspecie(idEspecie);
					    	  
						      JSONArray regimenesArray = especie.getJSONArray("regimen");
						      
						      for (int k=0;k<regimenesArray.length();k++) {
						    	  
							      JSONObject regimen = regimenesArray.getJSONObject(k);

						    	  InformacionRega.Linea.Especie.Regimen regimenToAdd = especieToAdd.new Regimen();
						    	  
						    	  String idRegimen = regimen.getString("regimen");
						    	  String descRegimen = regimen.getString("descriptivo");
						    	  String censo = regimen.getString("censo");
						    	  
						    	  if (!StringUtils.isNullOrEmpty(idRegimen))
						    		  regimenToAdd.setRegimen(idRegimen);
						    	  if (!StringUtils.isNullOrEmpty(descRegimen))
						    		  regimenToAdd.setDescriptivo(descRegimen);
						    	  if (!StringUtils.isNullOrEmpty(censo)){
						    		  regimenToAdd.setCenso(censo);
						    		  especieToAdd.getRegimenes().add(regimenToAdd);  // Si el censo esta informado se anyade a la lista de regimenes
						    	  }
						    	  
						      }
						      
						      if (especieToAdd.getRegimenes().size()!=0) {
							      lineaToAdd.getEspecies().add(especieToAdd); 
						      }
					      }
				      }
				      
				      if (lineaToAdd.getEspecies().size()!=0)
				    	  infoRega.getLineas().add(lineaToAdd); // Solo si hay lineas con regimenes y censo informados se anyade a la lista de lineas
				      
				    }
			}
			
			
			/////////////////////////////////////////
			/// Rellenar AmbitoEquivalenteAgroseguro
			
			JSONArray ambitoEquivalenteAgroseguros = jsonObject.getJSONArray("ambitoEquivalenteAgroseguro");
			
			
			if (ambitoEquivalenteAgroseguros != null && ambitoEquivalenteAgroseguros.length() > 0) {
				
				for (int i = 0; i < ambitoEquivalenteAgroseguros.length(); i++) {
					
					
				      JSONObject ambitoEquivalenteAgroseguro = ambitoEquivalenteAgroseguros.getJSONObject(i);

				      JSONObject ambitoAgroseguro = ambitoEquivalenteAgroseguro.getJSONObject("ambitoAgroseguro");
				      
					
				      AmbitoAgroseguro ambitoAgroseguroObj =  infoRega.new AmbitoAgroseguro();
				      
				      
	
				      ambitoAgroseguroObj.setZonificacionestimada(ambitoAgroseguro.getString("zonificacionestimada"));

				  
				      
				      Provincia provincia = ambitoAgroseguroObj.new Provincia();
				      provincia.setCodigo(ambitoAgroseguro.getJSONObject("provincia").getString("codigo"));
				      provincia.setDescriptivo(ambitoAgroseguro.getJSONObject("provincia").getString("descriptivo"));
				      ambitoAgroseguroObj.setProvincia(provincia);

				      
				      
				      
				      Comarca comarca = ambitoAgroseguroObj.new Comarca();
				      comarca.setCodigo(ambitoAgroseguro.getJSONObject("comarca").getString("codigo"));
				      comarca.setDescriptivo(ambitoAgroseguro.getJSONObject("comarca").getString("descriptivo"));
				      
				      ambitoAgroseguroObj.setComarca(comarca);
				      
				      
				      InformacionRega.AmbitoAgroseguro.Termino termino = ambitoAgroseguroObj.new Termino();
				      termino.setCodigo(ambitoAgroseguro.getJSONObject("termino").getString("codigo"));
				      
				      // Se recupera una instancia especifica de la entidad "Linea" a traves del DAO a partir de la linea y plan
					  com.rsi.agp.dao.tables.poliza.Linea lineaRega;
					  try {
						lineaRega = lineaDao.getLinea(new BigDecimal(linea), new BigDecimal(plan));
						BigDecimal codProvincia = new BigDecimal(provincia.getCodigo());
						BigDecimal codComarca = new BigDecimal(comarca.getCodigo());
						BigDecimal codTermino = new BigDecimal(termino.getCodigo());
						Character codSubTermino =ambitoAgroseguro.getJSONObject("subTermino").getString("codigo").charAt(0);
						  
						Termino terminoRega = terminoDao.getTermino(codProvincia,codComarca, codTermino, codSubTermino);
					      
					    termino.setDescriptivo(terminoRega.getNomTerminoByFecha(lineaRega.getFechaInicioContratacion(), true));
					    ambitoAgroseguroObj.setTermino(termino);
					  } catch (DAOException e1) {
						logger.debug("Se ha producido un error al recuperar la linea o termino" );
						e1.printStackTrace();
					  }
					 

				      
		

				      
				      try {
					      Subtermino subtermino = ambitoAgroseguroObj.new Subtermino();
					      subtermino.setCodigo(ambitoAgroseguro.getJSONObject("subTermino").getString("codigo"));
					      subtermino.setDescriptivo(ambitoAgroseguro.getJSONObject("subTermino").getString("descriptivo"));
					      ambitoAgroseguroObj.setSubtermino(subtermino);
				      } catch(Exception e) {
				    	  // Subtermino es nulo
				    	  e.printStackTrace();
				      }
				      
				      


				      infoRega.getAmbitoAgroseguro().add(ambitoAgroseguroObj);
				      


					
				}
				
			}
		
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return infoRega;	
	}
	
}