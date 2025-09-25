package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.filters.config.ConfiguracionCamposFiltro;
import com.rsi.agp.dao.filters.poliza.CapitalAseguradoParcelaFiltro;
import com.rsi.agp.dao.filters.poliza.GetIdPantallaConfigurableFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraFechaContrataAgricolaFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraGrupoTasasFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraLimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.poliza.MascaraPrecioFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;
import com.rsi.agp.dao.tables.admin.ClaseDetalle;
import com.rsi.agp.dao.tables.cgen.DatosBuzonGeneral;
import com.rsi.agp.dao.tables.commons.VistaTerminosAsegurable;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.FechaContratacionAgricola;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.orgDat.VistaPorFactores;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaCobertura;
import com.rsi.agp.dao.tables.poliza.SWModulosCoberturasParcela;

@SuppressWarnings({ "unchecked" })
public class DatosParcelaDao extends BaseDaoHibernate implements IDatosParcelaDao {

	private static final String SUBTERMINO = "subtermino";

	private static final String CODTERMINO = "codtermino";

	private static final String CODCOMARCA = "codcomarca";

	private static final String CODPROVINCIA = "codprovincia";

	private static final String VARIEDAD_ID_CODVARIEDAD = "variedad.id.codvariedad";

	private static final String VARIEDAD_ID_CODCULTIVO = "variedad.id.codcultivo";

	private static final String ID_LINEASEGUROID = "id.lineaseguroid";

	private static final Log logger = LogFactory.getLog(DatosParcelaDao.class);

	private final String TODOS_99999 = "99999";
	private final BigDecimal TODOS_999 = BigDecimal.valueOf(999);
	private final BigDecimal TODOS_99 = BigDecimal.valueOf(99);
	private final Character TODOS_9 = '9';
	
	@Override
	public List<CapitalAsegurado> getCapitalesAseguradoParcela(final Long codParcela) {
		logger.debug("init - [DatosParcelaDao] getCapitalesAseguradoParcela");
		CapitalAseguradoParcelaFiltro filtro = new CapitalAseguradoParcelaFiltro(codParcela);
		logger.debug("end - [DatosParcelaDao] getCapitalesAseguradoParcela");
		return this.getObjects(filtro);
	}

	@Override
	public Long getIdPantallaConfigurable(final Long lineaseguroid, final Long idPantalla) {
		logger.debug("init - [DatosParcelaDao] getIdPantallaConfigurable");
		GetIdPantallaConfigurableFiltro filtro = new GetIdPantallaConfigurableFiltro(lineaseguroid,
				new BigDecimal(idPantalla));
		List<PantallaConfigurable> pantallasConfiguradas = this.getObjects(filtro);
		Long idPantallaConfigurable = new Long(0);
		for (PantallaConfigurable pantallaConfigurable : pantallasConfiguradas) {
			idPantallaConfigurable = pantallaConfigurable.getIdpantallaconfigurable();
		}
		logger.debug("end - [DatosParcelaDao] getIdPantallaConfigurable");
		return idPantallaConfigurable;
	}

	@Override
	public List<String> getCodsModulosPoliza(final Long idPoliza) throws BusinessException {
		logger.debug("init - [DatosParcelaDao] getModulosPoliza");
		List<String> lista = null;
		try {
			lista = this.getObjectsBySQLQuery("select codmodulo from tb_modulos_poliza t where idpoliza=" + idPoliza);
		} catch (Exception e) {
			throw new BusinessException(
					"[DatosParcelaDao][getCodsModulosPoliza]error al obtener los codigos de modulo de la poliza", e);
		}
		logger.debug("end - [DatosParcelaDao] getCodsModulosPoliza");
		return lista;
	}

	@Override
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(final Long lineaseguroid, final String codmodulo,
			final Character nivelEleccion) {
		logger.debug("init - [DatosParcelaDao] getRiesgosCubiertosModulo");
		Session session = obtenerSession();
		List<RiesgoCubiertoModulo> lista = new ArrayList<RiesgoCubiertoModulo>();
		try {
			Criteria criteria = session.createCriteria(RiesgoCubiertoModulo.class);
			criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroid));
			criteria.add(Restrictions.eq("id.codmodulo", codmodulo));
			if (nivelEleccion != null) {
				criteria.add(Restrictions.eq("niveleccion", nivelEleccion));
			}
			lista = criteria.list();
		} catch (Exception e) {
			logger.fatal("[DAOException - sin throw][DatosParcelaDao][getRiesgosCubiertosModulo]Error lectura BD", e);
		}
		logger.debug("end - [DatosParcelaDao] getRiesgosCubiertosModulo");
		return lista;
	}

	@Override
	public List<ConfiguracionCampo> getListConfigCampos(final BigDecimal idpantalla) throws DAOException {
		logger.debug("init - [DatosParcelaDao] getListConfigCampos");
		Session session = obtenerSession();
		List<ConfiguracionCampo> lista = new ArrayList<ConfiguracionCampo>();
		try {
			Criteria criteria = session.createCriteria(ConfiguracionCampo.class);
			criteria.add(Restrictions.eq("id.idpantallaconfigurable", idpantalla));
			criteria.addOrder(Order.asc("y"));
			criteria.addOrder(Order.asc("x"));
			lista = criteria.list();
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][getListConfigCampos]error lectura BD", e);
		}
		logger.debug("end - [DatosParcelaDao] getListConfigCampos");
		return lista;
	}

	/**
	 * Comprueba si el capital asegurado pasado como parametro esta dentro de fechas
	 * de contratacion
	 */
	@Override
	public boolean dentroDeFechasContratacion(final Long lineaseguroId, final List<String> modulos,
			final BigDecimal codcultivo, final BigDecimal codvariedad, final BigDecimal codprovincia,
			final BigDecimal codcomarca, final BigDecimal codtermino, final Character subtermino,
			final CapitalAsegurado capitalAsegurado) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(FechaContratacionAgricola.class);
			Conjunction c = Restrictions.conjunction();
			if (FiltroUtils.noEstaVacio(lineaseguroId)) {
				c.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroId));
			}
			if (FiltroUtils.noEstaVacio(modulos)) {
				Disjunction dd = Restrictions.disjunction();
				for (String modulo : modulos) {
					dd.add(Restrictions.eq("modulo.id.codmodulo", modulo));
				}
				c.add(dd);
			}
			if (FiltroUtils.noEstaVacio(codcultivo)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, codcultivo))
						.add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(codvariedad)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, codvariedad))
						.add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(codprovincia)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODPROVINCIA, codprovincia))
						.add(Restrictions.eq(CODPROVINCIA, TODOS_99)));
			}
			if (FiltroUtils.noEstaVacio(codcomarca)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODCOMARCA, codcomarca))
						.add(Restrictions.eq(CODCOMARCA, TODOS_99)));
			}
			if (FiltroUtils.noEstaVacio(codtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODTERMINO, codtermino))
						.add(Restrictions.eq(CODTERMINO, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(subtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(SUBTERMINO, subtermino))
						.add(Restrictions.eq(SUBTERMINO, TODOS_9)));
			}
			if (capitalAsegurado != null
					&& capitalAsegurado.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(100)) < 0) {
				for (DatoVariableParcela datvar : capitalAsegurado.getDatoVariableParcelas()) {
					// CICLO CULTIVO
					if (datvar.getDiccionarioDatos().getCodconcepto().intValue() == ConstantsConceptos.CODCPTO_CICLOCULTIVO
							&& FiltroUtils.noEstaVacio(new BigDecimal(datvar.getValor()))) {
						c.add(Restrictions.disjunction()
								.add(Restrictions.eq("cicloCultivo.codciclocultivo", new BigDecimal(datvar.getValor())))
								.add(Restrictions.isNull("cicloCultivo.codciclocultivo")));
					}
					// TIPO PLANTACION
					if (datvar.getDiccionarioDatos().getCodconcepto()
							.intValue() == ConstantsConceptos.CODCPTO_TIPO_PLANTACION
							&& FiltroUtils.noEstaVacio(new BigDecimal(datvar.getValor()))) {
						c.add(Restrictions.disjunction()
								.add(Restrictions.eq("tipoPlantacion.codtipoplantacion",
										new BigDecimal(datvar.getValor())))
								.add(Restrictions.isNull("tipoPlantacion.codtipoplantacion")));
					}
					// MPM - 22/05/12
					// SISTEMA DE CULTIVO
					// Si el concepto es 'Sistema de cultivo' y no esta vacio
					if ((datvar.getDiccionarioDatos().getCodconcepto().intValue() == ConstantsConceptos.CODCPTO_SISTCULTIVO)
							&& !"".equals(StringUtils.nullToString(new BigDecimal(datvar.getValor())))) {
						c.add(Restrictions.disjunction()
								.add(Restrictions.eq("sistemaCultivo.codsistemacultivo",
										new BigDecimal(datvar.getValor())))
								.add(Restrictions.isNull("sistemaCultivo.codsistemacultivo")));
					}
				}
			}
			criteria.add(c);
			// MPM - 19/06/12
			// Obtiene la fecha actual ignorando la hora.
			GregorianCalendar gc = new GregorianCalendar();
			Date fechaActual = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
					gc.get(Calendar.DAY_OF_MONTH)).getTime();
			// Anhade la condicion para comprobar si la fecha actual esta dentro de las
			// fechas que obtenidas en la consulta
			Conjunction disjFechas = Restrictions.conjunction();
			// Que la fecha de inicio de contratacion sea menor o igual que la fecha actual
			disjFechas.add(Restrictions.le("feciniciocontrata", fechaActual));
			// Que la fecha de ultimo dia de pago sear mayor o igual que la fecha actual
			disjFechas.add(Restrictions.ge("ultimodiapago", fechaActual));
			criteria.add(disjFechas);
			// Si la consulta devuelve algun registro, el capital asegurado esta dentro de
			// fechas de contratacion
			return criteria.list().size() > 0;
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][dentroDeFechasContratacion] error lectura BD", e);
		}
	}

	/*** MODIF TAM (10.12.2018) ESC-4627 ** Inicio ***/
	@Override
	public DiccionarioDatos getDiccionarioDatosVarPar(final BigDecimal codConcepto) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(DiccionarioDatos.class);
			criteria.add(Restrictions.eq("codconcepto", codConcepto));
			DiccionarioDatos diccDatos = (DiccionarioDatos) criteria.uniqueResult();
			return diccDatos;
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][getDiccionarioDAtosVarPar] error lectura BD", e);
		}
	}
	/*** MODIF TAM (10.12.2018) ** ESC-4627 ** Fin **/

	@Override
	public boolean dentroDeClaseDetalle(final Long idClase, final List<String> modulos, final BigDecimal codcultivo,
			final BigDecimal codvariedad, final BigDecimal codprovincia, final BigDecimal codcomarca,
			final BigDecimal codtermino, final Character subtermino) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(ClaseDetalle.class);
			Conjunction c = Restrictions.conjunction();
			if (FiltroUtils.noEstaVacio(idClase)) {
				c.add(Restrictions.eq("clase.id", idClase));
			}
			if (FiltroUtils.noEstaVacio(modulos)) {
				Disjunction dd = Restrictions.disjunction();
				for (String modulo : modulos) {
					dd.add(Restrictions.eq("codmodulo", modulo));
				}
				dd.add(Restrictions.eq("codmodulo", TODOS_99999));
				c.add(dd);
			}
			if (FiltroUtils.noEstaVacio(codcultivo)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, codcultivo))
						.add(Restrictions.eq(VARIEDAD_ID_CODCULTIVO, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(codvariedad)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, codvariedad))
						.add(Restrictions.eq(VARIEDAD_ID_CODVARIEDAD, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(codprovincia)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODPROVINCIA, codprovincia))
						.add(Restrictions.eq(CODPROVINCIA, TODOS_99)));
			}
			if (FiltroUtils.noEstaVacio(codcomarca)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODCOMARCA, codcomarca))
						.add(Restrictions.eq(CODCOMARCA, TODOS_99)));
			}
			if (FiltroUtils.noEstaVacio(codtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(CODTERMINO, codtermino))
						.add(Restrictions.eq(CODTERMINO, TODOS_999)));
			}
			if (FiltroUtils.noEstaVacio(subtermino)) {
				c.add(Restrictions.disjunction().add(Restrictions.eq(SUBTERMINO, subtermino))
						.add(Restrictions.eq(SUBTERMINO, TODOS_9)));
			}
			criteria.add(c);
			return criteria.list().size() > 0;
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][dentroDeAmbitoAsegurable] error lectura BD", e);
		}
	}

	@Override
	public boolean dentroDeAmbitoAsegurable(final Long lineaseguroId, final BigDecimal codprovincia,
			final BigDecimal codcomarca, final BigDecimal codtermino, final Character subtermino) throws DAOException {
		Session session = obtenerSession();
		try {
			Criteria criteria = session.createCriteria(VistaTerminosAsegurable.class);			
			if (FiltroUtils.noEstaVacio(lineaseguroId)) {
				criteria.add(Restrictions.eq(ID_LINEASEGUROID, lineaseguroId));
			}
			// COMPROBAMOS SI HAY DATOS PARA LA LINEA... SI NO HAY SE DA POR BUENO
			if (criteria.list().size() > 0) {
				Conjunction c = Restrictions.conjunction();
				if (FiltroUtils.noEstaVacio(codprovincia)) {
					c.add(Restrictions.disjunction().add(Restrictions.eq("id.codprovincia", codprovincia))
							.add(Restrictions.eq("id.codprovincia", TODOS_99)));
				}
				if (FiltroUtils.noEstaVacio(codcomarca)) {
					c.add(Restrictions.disjunction().add(Restrictions.eq("id.codcomarca", codcomarca))
							.add(Restrictions.eq("id.codcomarca", TODOS_99)));
				}
				if (FiltroUtils.noEstaVacio(codtermino)) {
					c.add(Restrictions.disjunction().add(Restrictions.eq("id.codtermino", codtermino))
							.add(Restrictions.eq("id.codtermino", TODOS_999)));
				}
				if (FiltroUtils.noEstaVacio(subtermino)) {
					c.add(Restrictions.disjunction().add(Restrictions.eq("id.subtermino", subtermino))
							.add(Restrictions.eq("id.subtermino", TODOS_9)));
				}
				criteria.add(c);
				return criteria.list().size() > 0;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new DAOException("[DatosParcelaDao][dentroDeAmbitoAsegurable] error lectura BD", e);
		}
	}

//	@Override
//	public boolean isAseguradoAutorizado(final Long lineaseguroId, final List<String> modulos, final String nifcif,
//			final BigDecimal codcultivo, final BigDecimal codvariedad, final Object object) throws DAOException {
//		Session session = obtenerSession();
//		try {
//			Criteria criteria = session.createCriteria(AseguradoAutorizado.class);
//			Conjunction c = Restrictions.conjunction();
//			if (FiltroUtils.noEstaVacio(lineaseguroId)) {
//				c.add(Restrictions.eq("id.lineaseguroid", lineaseguroId));
//			}
//			if (FiltroUtils.noEstaVacio(modulos)) {
//				Disjunction dd = Restrictions.disjunction();
//				for (String modulo : modulos) {
//					dd.add(Restrictions.eq("modulo.id.codmodulo", modulo));
//				}
//				dd.add(Restrictions.eq("modulo.id.codmodulo", TODOS_99999));
//				c.add(dd);
//			}
//			if (FiltroUtils.noEstaVacio(nifcif)) {
//				c.add(Restrictions.eq("nifasegurado", nifcif));
//			}
//			if (FiltroUtils.noEstaVacio(codcultivo)) {
//				c.add(Restrictions.disjunction().add(Restrictions.eq("variedad.id.codcultivo", codcultivo))
//						.add(Restrictions.eq("variedad.id.codcultivo", TODOS_999)));
//			}
//			if (FiltroUtils.noEstaVacio(codvariedad)) {
//				c.add(Restrictions.disjunction().add(Restrictions.eq("variedad.id.codvariedad", codvariedad))
//						.add(Restrictions.eq("variedad.id.codvariedad", TODOS_999)));
//			}
//			criteria.add(c);
//			return criteria.list().size() > 0;
//		} catch (Exception e) {
//			throw new DAOException("[DatosParcelaDao][isAseguradoAutorizado] error lectura BD", e);
//		}
//	}
	
	@Override
	public List<BigDecimal> getConceptosRelacionados(final List<BigDecimal> listaMascaras) {
		logger.debug("init - [DatosParcelaDao] getConceptosRelacionados");
		List<BigDecimal> lista = new ArrayList<BigDecimal>();
		if (listaMascaras != null && !listaMascaras.isEmpty()) {
			CampoMascaraFiltro filter = new CampoMascaraFiltro(listaMascaras);
			lista = this.getObjects(filter);
		}
		logger.debug("end - [DatosParcelaDao] getConceptosRelacionados");
		return lista;
	}
	
	@Override
	public List<BigDecimal> getConceptosObligatorios(final Long idPantallaConfigurable) {
		logger.debug("init - [DatosParcelaDao] getConceptosObligatorios");
		List<BigDecimal> listaObligatorios = null;
		ConfiguracionCamposFiltro filter = new ConfiguracionCamposFiltro(true, idPantallaConfigurable);
		listaObligatorios = this.getObjects(filter);
		logger.debug("end - [DatosParcelaDao] getConceptosObligatorios");
		return listaObligatorios;
	}
	
	@Override
	public List<BigDecimal> getMascaraFCA(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos) {
		logger.debug("init - [DatosParcelaDao] getMascaraFCA");
		List<BigDecimal> listaMFCAF = null;
		MascaraFechaContrataAgricolaFiltro filtroMFCAF = new MascaraFechaContrataAgricolaFiltro(lineaseguroId,
				codCultivo, codVariedad, codProvincia, codComarca, codTermino, subTermino, modulos);
		listaMFCAF = this.getObjects(filtroMFCAF);
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente
		// orden:
		// 1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		// 4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMFCAF.isEmpty()) {
			filtroMFCAF.setAllsubterminos(true);
			listaMFCAF = this.getObjects(filtroMFCAF);
			if (listaMFCAF.isEmpty()) {
				filtroMFCAF.setAllterminos(true);
				listaMFCAF = this.getObjects(filtroMFCAF);
				if (listaMFCAF.isEmpty()) {
					filtroMFCAF.setAllcomarcas(true);
					listaMFCAF = this.getObjects(filtroMFCAF);
					if (listaMFCAF.isEmpty()) {
						filtroMFCAF.setAllprovincias(true);
						listaMFCAF = this.getObjects(filtroMFCAF);
						if (listaMFCAF.isEmpty()) {
							filtroMFCAF.setAllvariedades(true);
							listaMFCAF = this.getObjects(filtroMFCAF);
							if (listaMFCAF.isEmpty()) {
								filtroMFCAF.setAllcultivos(true);
								listaMFCAF = this.getObjects(filtroMFCAF);
							}
						}
					}
				}
			}
		}
		logger.debug("end - [DatosParcelaDao] getMascaraFCA");
		return listaMFCAF;
	}
	
	@Override
	/**
	 * Obtiene la mascar de grupo tasas
	 */
	public List<BigDecimal> getMascaraGT(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos) {
		logger.debug("init - [DatosParcelaDao] getMascaraGT");
		List<BigDecimal> listaMGT = null;
		MascaraGrupoTasasFiltro filtroMGT = new MascaraGrupoTasasFiltro(lineaseguroId, codCultivo, codVariedad,
				codProvincia, codComarca, codTermino, subTermino, modulos);
		listaMGT = this.getObjects(filtroMGT);
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente
		// orden:
		// 1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		// 4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMGT.isEmpty()) {
			filtroMGT.setAllsubterminos(true);
			listaMGT = this.getObjects(filtroMGT);
			if (listaMGT.isEmpty()) {
				filtroMGT.setAllterminos(true);
				listaMGT = this.getObjects(filtroMGT);
				if (listaMGT.isEmpty()) {
					filtroMGT.setAllcomarcas(true);
					listaMGT = this.getObjects(filtroMGT);
					if (listaMGT.isEmpty()) {
						filtroMGT.setAllprovincias(true);
						listaMGT = this.getObjects(filtroMGT);
						if (listaMGT.isEmpty()) {
							filtroMGT.setAllvariedades(true);
							listaMGT = this.getObjects(filtroMGT);
							if (listaMGT.isEmpty()) {
								filtroMGT.setAllcultivos(true);
								listaMGT = this.getObjects(filtroMGT);
							}
						}
					}
				}
			}
		}
		logger.debug("end - [DatosParcelaDao] getMascaraGT");
		return listaMGT;
	}
	
	@Override
	/**
	 * Obtiene la mascara de limites de rendimientos
	 */
	public List<BigDecimal> getMascaraLRDTO(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos) {
		logger.debug("init - [DatosParcelaDao] getMascaraLRDTO");
		List<BigDecimal> listaMLRDTO = null;
		MascaraLimiteRendimientoFiltro filtroMLRDTO = new MascaraLimiteRendimientoFiltro(lineaseguroId, codCultivo,
				codVariedad, codProvincia, codComarca, codTermino, subTermino, modulos);
		listaMLRDTO = this.getObjects(filtroMLRDTO);
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente
		// orden:
		// 1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		// 4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMLRDTO.isEmpty()) {
			filtroMLRDTO.setAllsubterminos(true);
			listaMLRDTO = this.getObjects(filtroMLRDTO);
			if (listaMLRDTO.isEmpty()) {
				filtroMLRDTO.setAllterminos(true);
				listaMLRDTO = this.getObjects(filtroMLRDTO);
				if (listaMLRDTO.isEmpty()) {
					filtroMLRDTO.setAllcomarcas(true);
					listaMLRDTO = this.getObjects(filtroMLRDTO);
					if (listaMLRDTO.isEmpty()) {
						filtroMLRDTO.setAllvariedades(true);
						listaMLRDTO = this.getObjects(filtroMLRDTO);
						if (listaMLRDTO.isEmpty()) {
							filtroMLRDTO.setAllprovincias(true);
							listaMLRDTO = this.getObjects(filtroMLRDTO);
							if (listaMLRDTO.isEmpty()) {
								filtroMLRDTO.setAllcultivos(true);
								listaMLRDTO = this.getObjects(filtroMLRDTO);
							}
						}
					}
				}
			}
		}
		logger.debug("end - [DatosParcelaDao] getMascaraLRDTO");
		return listaMLRDTO;
	}
	
	@Override
	/**
	 * Obtiene la mascara de precios
	 */
	public List<BigDecimal> getMascaraP(final Long lineaseguroId, final BigDecimal codCultivo,
			final BigDecimal codVariedad, final BigDecimal codProvincia, final BigDecimal codComarca,
			final BigDecimal codTermino, final Character subTermino, final List<String> modulos) {
		logger.debug("init - [DatosParcelaDao] getMascaraP");
		List<BigDecimal> listaMPR = null;
		MascaraPrecioFiltro filtroMPR = new MascaraPrecioFiltro(lineaseguroId, codCultivo, codVariedad, codProvincia,
				codComarca, codTermino, subTermino, modulos);
		listaMPR = this.getObjects(filtroMPR);
		// Si no recupera datos filtramos por los datos genericos. Segun el siguiente
		// orden:
		// 1. Subtermino = 9; 2. Termino = 999; 3. Comarca = 99
		// 4. Provincia = 99; 5. Variedad = 999; 6. Cultivo = 999
		if (listaMPR.isEmpty()) {
			filtroMPR.setAllsubterminos(true);
			listaMPR = this.getObjects(filtroMPR);
			if (listaMPR.isEmpty()) {
				filtroMPR.setAllterminos(true);
				listaMPR = this.getObjects(filtroMPR);
				if (listaMPR.isEmpty()) {
					filtroMPR.setAllcomarcas(true);
					listaMPR = this.getObjects(filtroMPR);
					if (listaMPR.isEmpty()) {
						filtroMPR.setAllprovincias(true);
						listaMPR = this.getObjects(filtroMPR);
						if (listaMPR.isEmpty()) {
							filtroMPR.setAllvariedades(true);
							listaMPR = this.getObjects(filtroMPR);
							if (listaMPR.isEmpty()) {
								filtroMPR.setAllcultivos(true);
								listaMPR = this.getObjects(filtroMPR);
							}
						}
					}
				}
			}
		}
		logger.debug("end - [DatosParcelaDao] getMascaraP");
		return listaMPR;
	}
	
	/**
	 * Obtiene la descripcion del dato variable asociado al codigo de concepto y
	 * valor indicados como parametros
	 */
	public String getDescDatoVariable(final Long lineaseguroid, final String listCodModulos, final Integer codCpto, final String valor) {
		String res = "";
		if (codCpto != null) {
			try {
				List<DatosBuzonGeneral> listaDBG = this.findFiltered(DatosBuzonGeneral.class,
						new String[] { "id.codcpto", "id.valorCpto" },
						new Object[] { new BigDecimal(codCpto), new BigDecimal(valor) }, 
						null);
				if (listaDBG != null && !listaDBG.isEmpty()) {
					res = listaDBG.get(0).getDescripcion();
				} else if (lineaseguroid != null && !StringUtils.isNullOrEmpty(listCodModulos)) {
					List<VistaPorFactores> listaVPF = this.getObjects(new Filter() {
						@Override
						public Criteria getCriteria(final Session sesion) {
							Criteria criteria = sesion.createCriteria(VistaPorFactores.class);							
							criteria.add(Restrictions.eq(ID_LINEASEGUROID, BigDecimal.valueOf(lineaseguroid)));
							criteria.add(Restrictions.in("id.codmodulo", listCodModulos.split(",")));
							criteria.add(Restrictions.eq("id.codconcepto",  BigDecimal.valueOf(codCpto)));
							criteria.add(Restrictions.eq("id.codvalor", new BigDecimal(valor)));
							return criteria;
						}
					});
					if (listaVPF != null && !listaVPF.isEmpty()) {
						res = listaVPF.get(0).getId().getDescripcion();
					}
				}
			} catch (Exception e) {
				logger.error("Error al obtener la descripcion del dato variable asociado al concepto " + codCpto
						+ " con valor " + valor, e);
			}
		}
		return res;
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Inicio */
	public SWModulosCoberturasParcela saveEnvioCobParcela(SWModulosCoberturasParcela envio)	throws DAOException {
		Session session = obtenerSession();
		try {

			session.saveOrUpdate(envio);
		} catch (Exception ex) {
			logger.error(ex);
			throw new DAOException("Se ha producido un error durante el guardado de la entidad",ex);
		} finally {
		}

		return envio;
	}
	
	public void actualizaXmlCoberturasParc(Long idEnvio, final String xml, final String respuesta) throws DAOException {
		Session session = obtenerSession();
		try {
			if (!StringUtils.nullToString(xml).equals("")) {
			
				Query update = session
						.createSQLQuery("UPDATE TB_SW_MODULOS_COBERTURAS_PARC SET ENVIO=:envio WHERE ID=" + idEnvio)
						.setString("envio", xml);
				update.executeUpdate();	
			}
		
			if (!StringUtils.nullToString(respuesta).equals("")) {
				Query update = session
						.createSQLQuery("UPDATE TB_SW_MODULOS_COBERTURAS_PARC SET RESPUESTA=:respuesta WHERE ID=" + idEnvio)
						.setString("respuesta", respuesta);
				update.executeUpdate();	
			}
		} catch (Exception e) {
			throw new DAOException(
					"Se ha producido un error durante el acceso a la base de datos",
					e);
		} finally {
		}
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isCoberturasElegiblesNivelParcela(Long lineaSeguroId, String codModulos){
		boolean isCoberturas = false;
		try {
			String sql = "select rc.FILAMODULO from o02agpe0.Tb_Sc_c_Riesgo_Cbrto_Mod rc WHERE rc.LINEASEGUROID = "
					+ lineaSeguroId + " AND rc.CODMODULO in (" + codModulos + ")  AND rc.NIVELECCION='D'"
					+ " UNION select cm.filamodulo from o02agpe0.tb_sc_c_caract_modulo cm"
					+ " WHERE cm.lineaseguroid = " + lineaSeguroId + " AND cm.codmodulo in (" + codModulos
					+ ")  AND cm.niveleleccion='D'";
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			List resultado = session.createSQLQuery(sql).list();
			if(null!=resultado && resultado.size()>0)
				return true;
		
			return isCoberturas;
		}catch (Exception ex) {
			logger.error("DatosParcelaDao.isCoberturasElegiblesNivelParcela. - ", ex);
			return false;
		}	
	}
	
	/**
	 * Obtiene una parcela de la BD
	 * 
	 * @param idParcela:
	 *            PK en la BD
	 */
	public Parcela getDatosParcela(Long idParcela) {
		Session session = obtenerSession();
		Parcela parcela = null;

		try {
			parcela = (Parcela) session.get(Parcela.class, idParcela);
		} catch (Exception excepcion) {
			logger.error(excepcion);
			excepcion.printStackTrace();
		} finally {
		}
		return parcela;
	}
	
	
	@Override
	public void borrarRiesgosElegParcela(List<ParcelaCobertura> ListRiesgElegParcela) {
		logger.debug("DatosParcelaDao - borrarRiesgosElegParcela");
		try {
			if (!ListRiesgElegParcela.isEmpty()) {
				deleteAll(ListRiesgElegParcela);
			}
		} catch (Exception e) {
			logger.error("Error al borrar la lista de datos variables asociada a la explotacion", e);
		}

	}
	

	/* Pet.50776_63485-Fase II ** MODIF TAM (07.10.2020) ** Fin */
	
	@Override
	public void copyParcelaCobertura(Long idcobertura, Long idparcela) throws DAOException {
		
		logger.debug("DatosParcelaDao - copyParcelaCobertura");
		
		try {
			String sql =  String.format("insert into o02agpe0.tb_parcelas_coberturas (id,idparcela,lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor)"
					+ " select o02agpe0.sq_parcela_cobertura.nextVal as id,%d as idparcela,lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor FROM o02agpe0.tb_parcelas_coberturas where id = %d" + 
					"", idparcela, idcobertura);
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			session.createSQLQuery(sql).executeUpdate();

		} catch (Exception e) {
				logger.error("DatosParcelaDao.copyParcelaCobertura. - ", e);

		 
				throw new DAOException(
						"Se ha producido un error durante el acceso a la base de datos",
						e);
		 
		
		}
		logger.debug("DatosParcelaDao - copyParcelaCobertura FIN");

	}

	
	
	
	@Override
	public void copyElegibleCoberturas(Long idparcela) throws DAOException {
		
		logger.debug("DatosParcelaDao - copyElegibleCoberturas");
		
		try {
			String sql =  String.format("insert into o02agpe0.tb_parcelas_coberturas (id,idparcela,lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor) " + 
					"select o02agpe0.sq_parcela_cobertura.nextVal as id, p.idparcela,p.lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,'%s' as codconcepto,'%s' as codvalor from tb_sc_c_riesgo_cbrto_mod co inner join tb_parcelas p on p.idparcela = %d where co.lineaseguroid = p.lineaseguroid and elegible = 'S' and codmodulo = 'P'",
					String.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO),
					String.valueOf(Constants.RIESGO_ELEGIDO_NO),
					idparcela);
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			session.createSQLQuery(sql).executeUpdate();

		}
		 catch (Exception e) {
				logger.error("DatosParcelaDao.copyElegibleCoberturas. - ", e);

		 
				throw new DAOException(
						"Se ha producido un error durante el acceso a la base de datos",
						e);
		 
		
		}
		logger.debug("DatosParcelaDao - copyElegibleCoberturas FIN");

	}

	
	
	@Override
	public boolean existInTbScSRiesgoCcbrtoMod(ParcelaCobertura cobertura) throws DAOException {


		logger.debug("DatosParcelaDao - existInTbScSRiesgoCcbrtoMod");

		
		String sql = String.format("select count(*) from tb_parcelas_coberturas pa,tb_sc_c_riesgo_cbrto_mod sc where pa.id = %d AND  sc.lineaseguroid = pa.lineaseguroid AND"
				+ " sc.codmodulo = pa.codmodulo AND sc.codconceptoppalmod = pa.codconceptoppalmod AND sc.codriesgocubierto = pa.codriesgocubierto AND sc.niveleccion = 'D'",
				cobertura.getId());
		
		logger.debug(sql);
		Session session = obtenerSession();
	
		BigDecimal count = (BigDecimal) session.createSQLQuery(sql).uniqueResult();
		
		if (count.longValue() > 0) {
			logger.debug("Encontrada la cobertura en la base de datos");
		}
		
		return count.longValue() > 0;
	}

	@Override
	public void actualizaParcelaCobertura(Long idcoberturaorigen, Long idparceladestino, Long lineaseguroid) throws DAOException {
		// TODO Auto-generated method stub
		logger.debug("DatosParcelaDao - actualizaParcelaCobertura");

		
		// TODO crea o actualiza en caso de existir
		
		try {
			String sql =  String.format("update o02agpe0.tb_parcelas_coberturas set (codvalor) = (select codvalor from o02agpe0.tb_parcelas_coberturas where id = %d) where idparcela = %d AND (codriesgocubierto,codconcepto) IN (select codriesgocubierto,codconcepto from o02agpe0.tb_parcelas_coberturas where id = %d)",
					idcoberturaorigen, idparceladestino,idcoberturaorigen);
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			int affectedRows = session.createSQLQuery(sql).executeUpdate();
			
			if (affectedRows == 0) {
				this.copyParcelaCobertura(idcoberturaorigen, idparceladestino, lineaseguroid);
			}
			// TODO crear las que falten

		}catch (Exception e) {
			
				logger.error("DatosParcelaDao.actualizaParcelaCobertura. - ", e);
				throw new DAOException("Se ha producido un error durante el acceso a la base de datos", e);
		}
		
		logger.debug("DatosParcelaDao - actualizaParcelaCobertura FIN");
	}
	
	@Override
	public void copyParcelaCobertura(Long idcobertura, Long idparcela, Long lineaseguroid) throws DAOException {

		
		logger.debug("DatosParcelaDao - copyParcelaCobertura");

		
		// TODO crea o actualiza en caso de existir
		
		try {
			String sql =  String.format("insert into o02agpe0.tb_parcelas_coberturas (id,idparcela,lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor)"
					+ " select o02agpe0.sq_parcela_cobertura.nextVal as id,%d as idparcela,%d as lineaseguroid,codmodulo,codconceptoppalmod,codriesgocubierto,codconcepto,codvalor FROM o02agpe0.tb_parcelas_coberturas where id = %d" + 
					"", idparcela,lineaseguroid, idcobertura);
		
			logger.debug(sql);
			Session session = obtenerSession();
		
			session.createSQLQuery(sql).executeUpdate();

		}
		 catch (Exception e) {
				logger.error("DatosParcelaDao.copyParcelaCobertura. - ", e);

		 
				throw new DAOException(
						"Se ha producido un error durante el acceso a la base de datos",
						e);
		 
		
		}
		logger.debug("DatosParcelaDao - copyParcelaCobertura FIN");

	}

	
	
}