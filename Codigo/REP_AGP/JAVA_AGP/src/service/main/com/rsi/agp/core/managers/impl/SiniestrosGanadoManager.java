package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IPolizasRenovablesDao;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.exception.RestWSException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.dao.models.poliza.ISiniestroDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.siniestro.EstadoSiniestro;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanado;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoActas;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoRyD;
import com.rsi.agp.dao.tables.siniestro.SiniestroGanadoVida;

public class SiniestrosGanadoManager implements IManager {

	private static final Log logger = LogFactory.getLog(SiniestrosGanadoManager.class);
	private ISiniestroDao siniestroDao;
	private IPolizasRenovablesDao polizasRenovablesDao;

	/**
	 * se prepara la llamada al S.W de Consulta de Lista de Siniestros de Ganado por
	 * REST, del grupo de Negocio de Vida.
	 * 
	 * @param mp
	 *            Objeto 'Poliza' utilizado para obtener los datos de entrada para
	 *            lanzar la llamada al S.W
	 * @param realPath
	 *            Ruta a los wsdl para las llamadas al SW
	 * @param usuario
	 *            Usuario que inicia la accion
	 * @return Objeto 'SiniestroVida' que encapsula la respuesta del SW (Lista de
	 *         Siniestros)
	 * @throws Exception
	 */
	public List<SiniestroGanadoVida> getListSiniestrosGanadoVida(Poliza p, String realPath, Usuario usuario)
			throws Exception {

		logger.debug("SiniestrosGanadoController - getListSiniestrosGanadoRetiradao " + p.getReferencia());

		List<SiniestroGanadoVida> listaSiniestrosGanVida = new ArrayList<SiniestroGanadoVida>();

		// Llama al SW de información de Siniestros para obtener la lista de Siniestos
		// de Vida

		try {
			String refPoliza = p.getReferencia();
			BigDecimal codplan = p.getLinea().getCodplan();
			listaSiniestrosGanVida = SiniestrosGanadoManager.getSiniestrosGanadoVida(refPoliza, codplan);

		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		}

		// Convierte el xml recibido en un objeto ModulosYCoberturas
		return listaSiniestrosGanVida;
	}

	/**
	 * se prepara la llamada al S.W de Consulta de Lista de Siniestros de Ganado por
	 * REST, del grupo de Negocio de Retirada.
	 * 
	 * @param mp
	 *            Objeto 'Poliza' utilizado para obtener los datos de entrada para
	 *            lanzar la llamada al S.W
	 * @param realPath
	 *            Ruta a los wsdl para las llamadas al SW
	 * @param usuario
	 *            Usuario que inicia la accion
	 * @return Objeto 'SiniestroRyd' que encapsula la respuesta del SW (Lista de
	 *         Siniestros)
	 * @throws Exception
	 */
	public List<SiniestroGanadoRyD> getListSiniestrosGanadoRetirada(Poliza p, String realPath, Usuario usuario)
			throws Exception {

		logger.debug("SiniestrosGanadoController - getListSiniestrosGanadoRetirada " + p.getReferencia());

		List<SiniestroGanadoRyD> listaSiniestrosGanRyD = new ArrayList<SiniestroGanadoRyD>();

		try {
			String refPoliza = p.getReferencia();
			BigDecimal codplan = p.getLinea().getCodplan();
			listaSiniestrosGanRyD = getSiniestrosGanadoRetirada(refPoliza, codplan);

		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		}

		// Convierte el xml recibido en un objeto InformacionSiniestroGanado
		return listaSiniestrosGanRyD;
	}

	/**
	 * se prepara la llamada al S.W de Consulta de Actas del Siniestro de Ganado por
	 * REST.
	 * 
	 * @param mp
	 *            Objeto 'Poliza' utilizado para obtener los datos de entrada para
	 *            lanzar la llamada al S.W
	 * @param realPath
	 *            Ruta a los wsdl para las llamadas al SW
	 * @param usuario
	 *            Usuario que inicia la accion
	 * @return Objeto 'ActaGanado' que encapsula la respuesta del SW (Lista de
	 *         Siniestros)
	 * @throws Exception
	 */
	public List<SiniestroGanadoActas> getActasGanado(Poliza p, String realPath, Usuario usuario) throws Exception {

		logger.debug("SiniestrosGanadoController - getListSiniestrosGanadoRetirada " + p.getReferencia());

		List<SiniestroGanadoActas> lstSinGanadoActas = new ArrayList<SiniestroGanadoActas>();

		// Llama al SW de Ayudas a la contratacion para obtener el xml de
		// ModulosYCoberturas
		try {
			String refPoliza = p.getReferencia();
			BigDecimal codplan = p.getLinea().getCodplan();
			lstSinGanadoActas = SiniestrosGanadoManager.getActasGanado(refPoliza, codplan);

		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		} finally {
			// Registra la comunicacion con el SW en BBDD
			logger.debug("Llamada Correcta");
		}

		// Convierte el xml recibido en un objeto ModulosYCoberturas
		return lstSinGanadoActas;
	}

	/**
	 * se prepara la llamada al S.W (REST) Descarga del pdf de una acta de tasación
	 * de ganado
	 * 
	 * @param mp
	 *            Objeto 'Poliza' utilizado para obtener los datos de entrada para
	 *            lanzar la llamada al S.W
	 * @param realPath
	 *            Ruta a los wsdl para las llamadas al SW
	 * @param usuario
	 *            Usuario que inicia la accion
	 * @param serie
	 *            Serie del acta
	 * @param numero
	 *            Numero del acta
	 * @param letra
	 *            letra del acta
	 * 
	 * @return Descargar un fichero Adobe Acrobat (PDF) con la impresión del acta de
	 *         tasación
	 * @throws Exception
	 */
	public byte[] getPDFActaGanado(Integer serie, Integer numero, String letra) throws Exception {

		logger.debug("SiniestrosGanadoManager - getPDFActaGanado [INIT]");

		if (letra.equals("") || letra.equals(null) || letra.equals(" ")) {
			letra = "%20";
		}

		logger.debug("getPDFActaGanado con valores: serie: " + serie + " y numero: " + numero + " y letra: " + letra);

		try {
			return WSRestSiniestrosInformacion.getPDFActaGanado(serie, numero, letra, WSRUtils.getSecurityToken());
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		}

	}

	/**
	 * se prepara la llamada al S.W (REST) Descarga del pdf de una carta de pago de
	 * ganado
	 * 
	 * @param mp
	 *            Objeto 'Poliza' utilizado para obtener los datos de entrada para
	 *            lanzar la llamada al S.W
	 * @param realPath
	 *            Ruta a los wsdl para las llamadas al SW
	 * @param usuario
	 *            Usuario que inicia la accion
	 * @param serie
	 *            Serie del acta
	 * @param numero
	 *            Numero del acta
	 * @param letra
	 *            letra del acta
	 * 
	 * @return descargar un fichero Adobe Acrobat (PDF) con la impresión de la carta
	 *         de pago del acta de tasación indicada
	 * @throws Exception
	 */
	public byte[] getPDFCartaPagoGanado(Integer serie, Integer numero, String letra) throws Exception {

		logger.debug("SiniestrosGanadoManager - getPDFActaGanado [INIT]");
		logger.debug("getPDFActaGanado con valores: serie: " + serie + "y numero: " + numero + "y letra: " + letra);

		if (letra.equals("") || letra.equals(null) || letra.equals(" ")) {
			letra = "%20";
		}

		try {
			return WSRestSiniestrosInformacion.getPDFCartaPagoGanado(serie, numero, letra, WSRUtils.getSecurityToken());
		} catch (Exception e) {
			logger.error("Ha ocurrido un error en la llamada al SW de Ayudas a la contratacion", e);
			throw e;
		} finally {
			// Registra la comunicacion con el SW en BBDD
			logger.debug("Llamada Correcta");
		}

	}

	public List<EstadoSiniestro> getEstadosSiniestro() throws BusinessException {
		try {
			return siniestroDao.getEstadosSiniestro();
		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error buscando los siniestros de una poliza", dao);
		}
	}

	public List<GruposNegocio> getGruposNegocio() {
		return this.polizasRenovablesDao.getGruposNegocio(false);
	}

	public Poliza getPoliza(Long idPoliza) throws BusinessException {
		Poliza poliza = null;
		try {

			poliza = siniestroDao.getPoliza(idPoliza);
			return poliza;

		} catch (DAOException dao) {

			throw new BusinessException("Se ha producido un error buscando los riesgos de un siniestro", dao);

		}
	}

	public static List<SiniestroGanadoVida> getSiniestrosGanadoVida(String referencia, BigDecimal codplan)
			throws RestWSException, JSONException {
		List<SiniestroGanadoVida> lstSiniestrosGanVida = new ArrayList<SiniestroGanadoVida>();

		String output = WSRestSiniestrosInformacion.getSiniestrosGanadoVida(referencia, codplan,
				WSRUtils.getSecurityToken());

		/* Formateamos la respuesta en lista de Siniestros */
		if (!output.equals("")) {
			return lstSiniestrosGanVida = cargarListaSinVida(output);
		} else {
			return lstSiniestrosGanVida;
		}
	}

	public static List<SiniestroGanadoRyD> getSiniestrosGanadoRetirada(String referencia, BigDecimal codplan)
			throws RestWSException, JSONException {

		List<SiniestroGanadoRyD> lstSiniestrosGanRyD = new ArrayList<SiniestroGanadoRyD>();

		String output = WSRestSiniestrosInformacion.getSiniestrosGanadoRetirada(referencia, codplan,
				WSRUtils.getSecurityToken());

		/* Formateamos la respuesta en lista de Siniestros */
		if (!output.equals("")) {
			return lstSiniestrosGanRyD = cargarListaSinRyD(output);
		} else {
			return lstSiniestrosGanRyD;
		}

	}

	public static List<SiniestroGanadoActas> getActasGanado(String referencia, BigDecimal codplan)
			throws RestWSException, JSONException {

		String output = WSRestSiniestrosInformacion.getActasGanado(referencia, codplan, WSRUtils.getSecurityToken());

		/* Formateamos la respuesta en lista de Siniestros */
		List<SiniestroGanadoActas> lstSiniestrosGanActas = cargarListaSinActas(output);
		return lstSiniestrosGanActas;

	}

	public List<SiniestroGanado> cargarDatosSiniestrosGanado(List<SiniestroGanadoVida> lstSinGanadoVida,
			List<SiniestroGanadoRyD> lstSinGanadoRyD) {

		List<SiniestroGanado> listaSiniestroGanado = new ArrayList<SiniestroGanado>();

		/*
		 * Guardamos en la lista Común de Siniestro Ganado lo recueprado en la lista de
		 * GanadoVida
		 */
		for (SiniestroGanadoVida sinGanVida : lstSinGanadoVida) {
			SiniestroGanado sinGanado = new SiniestroGanado();

			sinGanado.setGrupoNegocio("Resto");
			sinGanado.setProvincia(sinGanVida.getProvincia());
			sinGanado.setDesProvincia(sinGanVida.getDesProvincia());
			sinGanado.setTermino(sinGanVida.getTermino());
			sinGanado.setDesTermino(sinGanVida.getDesTermino());
			sinGanado.setSerieSiniestro(sinGanVida.getSerieSiniestro());
			sinGanado.setNumeroSiniestro(sinGanVida.getNumeroSiniestro());
			sinGanado.setFechaComunicacion(sinGanVida.getFechaComunicacion());
			sinGanado.setLibro(sinGanVida.getLibro());
			sinGanado.setTasado(sinGanVida.getTasado());
			sinGanado.setIdAnimal(sinGanVida.getIdAnimal());
			sinGanado.setPerito(sinGanVida.getPerito());
			sinGanado.setTlfPerito(sinGanVida.getTlfPerito());

			listaSiniestroGanado.add(sinGanado);
		}

		/*
		 * Guardamos en la lsita Común de Siniestro Ganado lo recueprado en la lista de
		 * GanadoVida
		 */
		for (SiniestroGanadoRyD sinGanRyD : lstSinGanadoRyD) {
			String vacio = null;
			SiniestroGanado sinGanado = new SiniestroGanado();

			sinGanado.setGrupoNegocio("RyD");
			sinGanado.setProvincia(vacio);
			sinGanado.setDesProvincia(vacio);
			sinGanado.setTermino(vacio);
			sinGanado.setDesTermino(vacio);
			sinGanado.setSerieSiniestro(sinGanRyD.getSerieSiniestro());
			sinGanado.setNumeroSiniestro(sinGanRyD.getNumSiniestro());
			sinGanado.setFechaComunicacion(sinGanRyD.getFechaComunicacion());
			sinGanado.setLibro(sinGanRyD.getLibro());
			sinGanado.setIdAnimal(sinGanRyD.getIdAnimal());
			sinGanado.setPerito(vacio);
			sinGanado.setTlfPerito(vacio);
			sinGanado.setFechaRetirada(sinGanRyD.getFechaRetirada());
			sinGanado.setKilos(sinGanRyD.getKilos());
			sinGanado.setPagoGestora(sinGanRyD.getPagoGestora());
			sinGanado.setCosteRetirada(sinGanRyD.getCosteRetirada());

			listaSiniestroGanado.add(sinGanado);
		}

		return listaSiniestroGanado;
	}

	public List<SiniestroGanado> aplicarFiltroenLista(final List<SiniestroGanado> listaSiniestros,
			final Map<String, String> filtroSiniestros) throws Exception {

		/**
		 * Se recorre la lista y se van eliminado los registros que no coinciden con el
		 * filtro insertado
		 */
		logger.debug("**@@** SiniestrosGanadoManager - aplicarFiltroenLista [INIT]");
		List<SiniestroGanado> lstSiniestroGanadoFinal = new ArrayList<SiniestroGanado>();

		String grNeg = "GrupoNegSin";
		String serieSin = "SerieSin";
		String numSin = "NumSin";
		String libroSin = "LibroSin";
		String fechaSin = "FechaSin";
		String fechaRet = "FechaRet";

		String valor = "";
		boolean filtro = false;

		try {

			/* Aplicamos 1º Filtro si lo tiene informado - Grupo de Negocio */
			if (filtroSiniestros.containsKey(grNeg)) {
				logger.debug("Filtramos por Grupo de Negocio");

				if (filtroSiniestros.get(grNeg).equals("1")) {
					valor = "Resto";
				} else {
					valor = "RyD";
				}
				lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, grNeg, valor);
				filtro = true;
			}

			/* Aplicamos 2º Filtro si lo tiene informado - Serie Siniestro */
			if (filtroSiniestros.containsKey(serieSin)) {
				logger.debug("Filtramos por Serie");

				valor = filtroSiniestros.get(serieSin);
				if (filtro) {
					lstSiniestroGanadoFinal = filtrarLista(lstSiniestroGanadoFinal, serieSin, valor);
				} else {
					lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, serieSin, valor);
				}

				filtro = true;

			}

			/* Aplicamos 3º Filtro si lo tiene informado - Numero Serie Siniestro */
			if (filtroSiniestros.containsKey(numSin)) {

				logger.debug("Filtramos por Numero de Serie");

				valor = filtroSiniestros.get(numSin);
				if (filtro) {
					lstSiniestroGanadoFinal = filtrarLista(lstSiniestroGanadoFinal, numSin, valor);
				} else {
					lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, numSin, valor);
				}

				filtro = true;
			}

			/* Aplicamos 4º Filtro si lo tiene informado - Libro Siniestro */
			if (filtroSiniestros.containsKey(libroSin)) {

				logger.debug("Filtramos por Libro");

				valor = filtroSiniestros.get(libroSin);
				if (filtro) {
					lstSiniestroGanadoFinal = filtrarLista(lstSiniestroGanadoFinal, libroSin, valor);
				} else {
					lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, libroSin, valor);
				}

				filtro = true;
			}

			/* Aplicamos 5º Filtro si lo tiene informado - Fecha Comunicación */
			if (filtroSiniestros.containsKey(fechaSin)) {

				logger.debug("Filtramos por Fecha Comunicación");

				valor = filtroSiniestros.get(fechaSin);
				if (filtro) {
					lstSiniestroGanadoFinal = filtrarLista(lstSiniestroGanadoFinal, fechaSin, valor);
				} else {
					lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, fechaSin, valor);
				}

				filtro = true;
			}

			/* Aplicamos 4º Filtro si lo tiene informado - Libro Siniestro */
			if (filtroSiniestros.containsKey(fechaRet)) {

				logger.debug("Filtramos por Fecha Retirada");

				valor = filtroSiniestros.get(fechaRet);

				if (filtro) {
					lstSiniestroGanadoFinal = filtrarLista(lstSiniestroGanadoFinal, fechaRet, valor);
				} else {
					lstSiniestroGanadoFinal = filtrarLista(listaSiniestros, fechaRet, valor);
				}

				filtro = true;
			}

		} catch (Exception e) {
			logger.error("Error al aplicar el filtro", e);
			throw e;
		}

		logger.debug("**@@** SiniestrosGanadoManager - aplicarFiltroenLista [END]");

		return lstSiniestroGanadoFinal;

	}

	public List<SiniestroGanadoActas> aplicarFiltroenListaActa(final List<SiniestroGanadoActas> listaSinGanadoActas,
			final Map<String, String> filtroSiniestros) throws Exception {

		/**
		 * Se recorre la lista y se van eliminado los registros que no coinciden con el
		 * filtro insertado
		 */
		logger.debug("**@@** SiniestrosGanadoManager - aplicarFiltroenListaActa [INIT]");
		List<SiniestroGanadoActas> lstSinGanadoActasFinal = new ArrayList<SiniestroGanadoActas>();

		String serieActa = "serieActa";
		String numActa = "numActa";
		String libroActa = "libroActa";
		String fechaActa = "fechaActa";
		String fechaPagoActa = "fechaPagoActa";

		String valor = "";
		boolean filtro = false;

		try {

			/* Aplicamos 1º Filtro si lo tiene informado - Serie Acta */
			if (filtroSiniestros.containsKey(serieActa)) {
				valor = filtroSiniestros.get(serieActa);
				lstSinGanadoActasFinal = filtrarListaActas(listaSinGanadoActas, serieActa, valor);
				filtro = true;
			}

			/* Aplicamos 2º Filtro si lo tiene informado - Numero Acta */
			if (filtroSiniestros.containsKey(numActa)) {
				valor = filtroSiniestros.get(numActa);
				if (filtro) {
					lstSinGanadoActasFinal = filtrarListaActas(lstSinGanadoActasFinal, numActa, valor);
				} else {
					lstSinGanadoActasFinal = filtrarListaActas(listaSinGanadoActas, numActa, valor);
				}
				filtro = true;
			}

			/* Aplicamos 3º Filtro si lo tiene informado - Libro Acta */
			if (filtroSiniestros.containsKey(libroActa)) {
				valor = filtroSiniestros.get(libroActa);
				if (filtro) {
					lstSinGanadoActasFinal = filtrarListaActas(lstSinGanadoActasFinal, libroActa, valor);
				} else {
					lstSinGanadoActasFinal = filtrarListaActas(listaSinGanadoActas, libroActa, valor);
				}
				filtro = true;
			}

			/* Aplicamos 4º Filtro si lo tiene informado - Fecha de Acta */
			if (filtroSiniestros.containsKey(fechaActa)) {

				valor = filtroSiniestros.get(fechaActa);
				if (filtro) {
					lstSinGanadoActasFinal = filtrarListaActas(lstSinGanadoActasFinal, fechaActa, valor);
				} else {
					lstSinGanadoActasFinal = filtrarListaActas(listaSinGanadoActas, fechaActa, valor);
				}

				filtro = true;
			}

			/* Aplicamos 4º Filtro si lo tiene informado - Fecha de Pago */
			if (filtroSiniestros.containsKey(fechaPagoActa)) {

				valor = filtroSiniestros.get(fechaPagoActa);
				if (filtro) {
					lstSinGanadoActasFinal = filtrarListaActas(lstSinGanadoActasFinal, fechaPagoActa, valor);
				} else {
					lstSinGanadoActasFinal = filtrarListaActas(listaSinGanadoActas, fechaPagoActa, valor);
				}

				filtro = true;
			}

		} catch (Exception e) {
			logger.error("Error al aplicar el filtro", e);
			throw e;
		}

		logger.debug("**@@** SiniestrosGanadoManager - aplicarFiltroenListaActa [INIT]");

		return lstSinGanadoActasFinal;

	}

	public List<SiniestroGanado> filtrarLista(final List<SiniestroGanado> listaSiniestros, final String filtro,
			final String valor) throws Exception {

		List<SiniestroGanado> lstFinalSinGanado = new ArrayList<SiniestroGanado>();

		String grNeg = "GrupoNegSin";
		String serieSin = "SerieSin";
		String numSin = "NumSin";
		String libroSin = "LibroSin";
		String fechaSin = "FechaSin";
		String fechaRet = "FechaRet";

		/* FILTRO POR GRUPO NEGOCIO */
		if (filtro.equals(grNeg)) {
			for (SiniestroGanado sinGan : listaSiniestros) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGan.getGrupoNegocio().equals(valor)) {
					lstFinalSinGanado.add(sinGan);
				}
			}
		}

		/* FILTRO POR SERIE SINIESTRO */
		if (filtro.equals(serieSin)) {
			int valorInt = Integer.parseInt(valor);
			for (SiniestroGanado sinGan : listaSiniestros) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGan.getSerieSiniestro() == valorInt) {
					lstFinalSinGanado.add(sinGan);
				}
			}
		}

		/* FILTRO POR NUMERO SINIESTRO */
		if (filtro.equals(numSin)) {
			int valorInt = Integer.parseInt(valor);
			for (SiniestroGanado sinGan : listaSiniestros) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGan.getNumeroSiniestro() == valorInt) {
					lstFinalSinGanado.add(sinGan);
				}
			}
		}

		/* FILTRO POR LIBRO SINIESTRO */
		if (filtro.equals(libroSin)) {
			for (SiniestroGanado sinGan : listaSiniestros) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGan.getLibro().equals(valor)) {
					lstFinalSinGanado.add(sinGan);
				}
			}
		}

		/* FILTRO POR FECHA COMUNICACION SINIESTRO */
		if (filtro.equals(fechaSin)) {

			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaComunicacion = null;
			try {
				fechaComunicacion = formato.parse(valor);
			} catch (ParseException e) {
				logger.error("Error al parsear la fecha Comunicación del Filtro", e);
			}

			for (SiniestroGanado sinGan : listaSiniestros) {

				if (sinGan.getFechaComunicacion().equals(fechaComunicacion)) {
					lstFinalSinGanado.add(sinGan);
				}
			}

		}

		/* FILTRO POR FECHA RETIRADA SINIESTRO */
		if (filtro.equals(fechaRet)) {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaRetirada = null;
			try {
				fechaRetirada = formato.parse(valor);
			} catch (ParseException e) {
				logger.error("Error al parsear la fecha Retirada del filtro", e);
			}

			for (SiniestroGanado sinGan : listaSiniestros) {

				if (sinGan.getFechaRetirada().equals(fechaRetirada)) {
					lstFinalSinGanado.add(sinGan);
				}
			}
		}

		return lstFinalSinGanado;
	}

	public List<SiniestroGanadoActas> filtrarListaActas(final List<SiniestroGanadoActas> listaSiniestrosActas,
			final String filtro, final String valor) throws Exception {

		List<SiniestroGanadoActas> lstFinalSinGanadoActa = new ArrayList<SiniestroGanadoActas>();

		String serieActa = "serieActa";
		String numActa = "numActa";
		String libroActa = "libroActa";
		String fechaActa = "fechaActa";
		String fechaPagoActa = "fechaPagoActa";

		/* FILTRO POR SERIE ACTA */
		if (filtro.equals(serieActa)) {
			int valorInt = Integer.parseInt(valor);
			for (SiniestroGanadoActas sinGanActa : listaSiniestrosActas) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGanActa.getSerieActa() == valorInt) {
					lstFinalSinGanadoActa.add(sinGanActa);
				}
			}
		}

		/* FILTRO POR NUMERO ACTA */
		if (filtro.equals(numActa)) {
			int valorInt = Integer.parseInt(valor);
			for (SiniestroGanadoActas sinGanActa : listaSiniestrosActas) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGanActa.getNumActa() == valorInt) {
					lstFinalSinGanadoActa.add(sinGanActa);
				}
			}
		}

		/* FILTRO POR LIBRO ACTAS */
		if (filtro.equals(libroActa)) {
			for (SiniestroGanadoActas sinGanActa : listaSiniestrosActas) {
				/* Comprobamos el filtro de Serie Grupo Negocio */
				if (sinGanActa.getLibro().equals(valor)) {
					lstFinalSinGanadoActa.add(sinGanActa);
				}
			}
		}

		/* FILTRO POR FECHA ACTA */
		if (filtro.equals(fechaActa)) {

			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha_Acta = null;
			try {
				fecha_Acta = formato.parse(valor);
			} catch (ParseException e) {
				logger.error("Error al parsear la fecha Comunicación del Filtro", e);
			}

			for (SiniestroGanadoActas sinGanActa : listaSiniestrosActas) {
				if (sinGanActa.getFechaActa().equals(fecha_Acta)) {
					lstFinalSinGanadoActa.add(sinGanActa);
				}
			}

		}

		/* FILTRO POR FECHA PAGO */
		if (filtro.equals(fechaPagoActa)) {

			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha_PagoActa = null;
			try {
				fecha_PagoActa = formato.parse(valor);
			} catch (ParseException e) {
				logger.error("Error al parsear la fecha Comunicación del Filtro", e);
			}

			for (SiniestroGanadoActas sinGanActa : listaSiniestrosActas) {
				if (sinGanActa.getFechaPago().equals(fecha_PagoActa)) {
					lstFinalSinGanadoActa.add(sinGanActa);
				}
			}

		}

		return lstFinalSinGanadoActa;
	}

	/****************************************************************************************/
	/** Cargamos la lista de Siniestros Ganado RyD con la información recuperada del       **/
	/** S.Web.																			   **/	
	/****************************************************************************************/
	public static List<SiniestroGanadoRyD> cargarListaSinRyD(final String output) throws JSONException {
		List<SiniestroGanadoRyD> lstSinGanadoRyD = new ArrayList<SiniestroGanadoRyD>();

		JSONArray jsonArray = new JSONArray(output);

		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {

				SiniestroGanadoRyD siniestroRyD = new SiniestroGanadoRyD();

				String lista = jsonArray.get(i).toString();
				lista = lista.replace("{", "");
				lista = lista.replace("}", "");
				String[] siniestro = lista.split(",");
				int numDatSin = siniestro.length;
				for (int j = 0; j < numDatSin; j++) {
					String datoSin = siniestro[j];
					datoSin = datoSin.replace("\"", "");
					String[] datsiniestro = datoSin.split(":");

					String dato = datsiniestro[0];
					String valor = "";
					if (datsiniestro.length > 1) {
						valor = datsiniestro[1];
					}

					// * Parseamos todos los datos recibidos en el SW por cada siniestro recibido */
					if (dato.equals("serieSiniestro")) {
						Integer serieSin = Integer.parseInt(valor);
						siniestroRyD.setSerieSiniestro(serieSin);
					}
					if (dato.equals("numSiniestro")) {
						Integer numSin = Integer.parseInt(valor);
						siniestroRyD.setNumSiniestro(numSin);
					}
					if (dato.equals("fechaComunicacion")) {
						if (!valor.equals("")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
							Date fechaComunicacion = null;

							try {

								String fecha_valor = convertDateFormat(valor);
								fechaComunicacion = formato.parse(fecha_valor);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables", e);
							}

							siniestroRyD.setFechaComunicacion(fechaComunicacion);

						}
					}
					if (dato.equals("libro")) {
						siniestroRyD.setLibro(valor);
					}
					if (dato.equals("fechaRetirada")) {
						if (!valor.equals("")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
							Date fechaRetirada = null;
							try {
								String fecha_valor = convertDateFormat(valor);
								fechaRetirada = formato.parse(fecha_valor);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables", e);
							}

							siniestroRyD.setFechaRetirada(fechaRetirada);
						}
					}
					if (dato.equals("idAnimal")) {
						siniestroRyD.setIdAnimal(valor);
					}
					if (dato.equals("kilos")) {
						if (!valor.equals("")) {
							siniestroRyD.setKilos(new BigDecimal(valor));
						}
					}
					if (dato.equals("costeRetirada")) {
						if (!valor.equals("")) {
							siniestroRyD.setCosteRetirada(new BigDecimal(valor));
						}
					}

					if (dato.equals("pagoGestora")) {
						if (!valor.equals("")) {
							siniestroRyD.setPagoGestora(new BigDecimal(valor));
						}
					}
				}
				lstSinGanadoRyD.add(siniestroRyD);
			}
		}

		return lstSinGanadoRyD;
	}

	/****************************************************************************************/
	/** Cargamos la lista de Siniestros Ganado Vida con la información recuperada del      **/
	/** S.Web.																			   **/
	/****************************************************************************************/
	public static List<SiniestroGanadoVida> cargarListaSinVida(final String output) throws JSONException {
		List<SiniestroGanadoVida> lstSinGanadoVida = new ArrayList<SiniestroGanadoVida>();

		JSONArray jsonArray = new JSONArray(output);

		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {

				SiniestroGanadoVida siniestroVida = new SiniestroGanadoVida();

				String lista = jsonArray.get(i).toString();
				lista = lista.replace("{", "");
				lista = lista.replace("}", "");
				String[] siniestro = lista.split(",");
				int numDatSin = siniestro.length;
				for (int j = 0; j < numDatSin; j++) {
					String datoSin = siniestro[j];
					datoSin = datoSin.replace("\"", "");
					String[] datsiniestro = datoSin.split(":");

					String dato = datsiniestro[0];
					String valor = "";
					if (datsiniestro.length > 1) {
						valor = datsiniestro[1];
					}

					// * Parseamos todos los datos recibidos en el SW por cada siniestro recibido */
					if (dato.equals("provincia")) {
						siniestroVida.setProvincia(valor);
					}
					if (dato.equals("desProvincia")) {
						siniestroVida.setDesProvincia(valor);
					}
					if (dato.equals("termino")) {
						siniestroVida.setTermino(valor);
					}
					if (dato.equals("desTermino")) {
						siniestroVida.setDesProvincia(valor);
					}
					if (dato.equals("serieSiniestro")) {
						Integer serieSin = Integer.parseInt(valor);
						siniestroVida.setSerieSiniestro(serieSin);
					}
					if (dato.equals("numSiniestro")) {
						Integer numSin = Integer.parseInt(valor);
						siniestroVida.setNumeroSiniestro(numSin);
					}
					if (dato.equals("fechaComunicacion")) {
						if (!valor.equals("")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
							Date fechaComunicacion = null;

							try {
								String fecha_valor = convertDateFormat(valor);
								fechaComunicacion = formato.parse(fecha_valor);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables", e);
							}

							siniestroVida.setFechaComunicacion(fechaComunicacion);
						}
					}
					if (dato.equals("libro")) {
						siniestroVida.setLibro(valor);
					}

					if (dato.equals("idAnimal")) {
						siniestroVida.setIdAnimal(valor);
					}
					if (dato.equals("perito")) {
						siniestroVida.setPerito(valor);
					}
					if (dato.equals("tlfPerito")) {
						siniestroVida.setTlfPerito(valor);
					}

					if (dato.equals("tasado")) {
						if (valor.equals("Si")) {
							siniestroVida.setTasado(true);
						} else {
							siniestroVida.setTasado(false);
						}
					}
				}
				lstSinGanadoVida.add(siniestroVida);
			}
		}

		return lstSinGanadoVida;
	}

	
	/****************************************************************************************/
	/** Cargamos la lista de Siniestros Ganado RyD con la información recuperada del       **/  
	/** S.Web.																			   **/
	/****************************************************************************************/
	public static List<SiniestroGanadoActas> cargarListaSinActas(final String output) throws JSONException {
		List<SiniestroGanadoActas> lstSinGanadoActas = new ArrayList<SiniestroGanadoActas>();

		JSONArray jsonArray = new JSONArray(output);

		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {

				SiniestroGanadoActas siniestroActas = new SiniestroGanadoActas();

				String lista = jsonArray.get(i).toString();
				lista = lista.replace("{", "");
				lista = lista.replace("}", "");
				String[] siniestro = lista.split(",");
				int numDatSin = siniestro.length;
				for (int j = 0; j < numDatSin; j++) {
					String datoSin = siniestro[j];
					datoSin = datoSin.replace("\"", "");
					String[] datsiniestro = datoSin.split(":");

					String dato = datsiniestro[0];
					String valor = "";
					if (datsiniestro.length > 1) {
						valor = datsiniestro[1];
					}

					// * Parseamos todos los datos recibidos en el SW por cada siniestro recibido */
					if (dato.equals("provincia")) {
						siniestroActas.setProvincia(valor);
					}
					if (dato.equals("desProvincia")) {
						siniestroActas.setDesProvincia(valor);
					}
					if (dato.equals("termino")) {
						siniestroActas.setTermino(valor);
					}
					if (dato.equals("desTermino")) {
						siniestroActas.setDesProvincia(valor);
					}
					if (dato.equals("serieActa")) {
						Integer serieActa = Integer.parseInt(valor);
						siniestroActas.setSerieActa(serieActa);
					}
					if (dato.equals("numActa")) {
						Integer numActa = Integer.parseInt(valor);
						siniestroActas.setNumActa(numActa);
					}
					if (dato.equals("estado")) {
						siniestroActas.setEstado(valor);
					}
					if (dato.equals("libro")) {
						siniestroActas.setLibro(valor);
					}

					if (dato.equals("idAnimal")) {
						siniestroActas.setIdAnimal(valor);
					}
					if (dato.equals("fechaActa")) {
						if (!valor.equals("") && !valor.equals("null")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
							Date fechaActa = null;

							try {
								String fecha_valor = convertDateFormat(valor);
								fechaActa = formato.parse(fecha_valor);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables", e);
							}

							siniestroActas.setFechaActa(fechaActa);
						}
					}
					if (dato.equals("importeActa")) {
						if (!valor.equals("")) {
							siniestroActas.setImporteActa(new BigDecimal(valor));
						}
					}
					if (dato.equals("importeDevolver")) {
						if (!valor.equals("")) {
							siniestroActas.setImporteDevolver(new BigDecimal(valor));
						}
					}
					if (dato.equals("fechaPago")) {
						if (!valor.equals("") && !valor.equals("null")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
							Date fechaPago = null;

							try {
								String fecha_valor = convertDateFormat(valor);
								fechaPago = formato.parse(fecha_valor);
							} catch (ParseException e) {
								logger.error("Error al parsear la fecha en los datos variables", e);
							}

							siniestroActas.setFechaPago(fechaPago);
						}
					}
				}

				lstSinGanadoActas.add(siniestroActas);
			}
		}

		return lstSinGanadoActas;
	}

	public static String convertDateFormat(String fechaEnt) {
		String[] fecha = fechaEnt.split("-");
		String FechaSalida = fecha[2] + "/" + fecha[1] + '/' + fecha[0];
		return FechaSalida;
	}

	/* Setter */
	public void setSiniestroDao(ISiniestroDao siniestroDao) {
		this.siniestroDao = siniestroDao;
	}

	public void setPolizasRenovablesDao(IPolizasRenovablesDao polizasRenovablesDao) {
		this.polizasRenovablesDao = polizasRenovablesDao;
	}

}
