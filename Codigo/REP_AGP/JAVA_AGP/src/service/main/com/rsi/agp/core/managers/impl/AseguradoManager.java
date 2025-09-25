package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.hibernate.Hibernate;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.model.user.User;
import com.rsi.agp.core.util.AseguradoManagerGeneradorHtml;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.filters.admin.impl.Asegurado2Filtro;
import com.rsi.agp.dao.filters.admin.impl.AseguradoFiltro;
import com.rsi.agp.dao.filters.poliza.SeleccionPolizaFiltro;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.AseguradoSWDatPerMed;
import com.rsi.agp.dao.tables.admin.BloqueosAsegurado;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.cgen.SubvencionesAseguradosView;
import com.rsi.agp.dao.tables.commons.Localidad;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.commons.Via;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

import es.agroseguro.iTipos.Direccion;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidas;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoDatosYMedidasDocument;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoNoSubvencionable;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.DatosPersonales;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Organismo;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.PorcentajeModulacionENESA;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ProximoOrganismo;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ProximoSubvencion;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Subvencion;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.AseguradoInfoSaldoENESA;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.AseguradoInfoSaldoENESADocument;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error;


public class AseguradoManager implements IManager {
	
	private static final Log LOGGER = LogFactory.getLog(AseguradoManager.class);
	
	private IAseguradoDao aseguradoDao;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private AseguradoSubvencionManager aseguradoSubvencionManager;
	private IHistoricoEstadosManager historicoEstadosManager;

	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");
	final ResourceBundle bundleAgp = ResourceBundle.getBundle("agp");

	final String tituloListadoPolizas = "Listado de pólizas del asegurado para actualizar las subvenciones";

	@SuppressWarnings("unchecked")
	public List<Asegurado> getAsegurados(final Asegurado aseguradoBean,
			String perfil) {
		final AseguradoFiltro filter = new AseguradoFiltro(aseguradoBean);
		filter.setPerfil(perfil);
		List<Asegurado> lista = null;

		try {
			lista = aseguradoDao.getObjects(filter);

		} catch (Exception e) {
			LOGGER.error(
					"Se ha producido un error buscando el listado de asegurados",
					e);
		}
		return lista;
	}

	/* -------------------------------------------------------- */
	/* DISPLAYTAG PAGINATION */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Asegurado> getPaginatedListAsegurados(
			Asegurado aseguradoBean, List<BigDecimal> entidadesGrupo,
			int numPageRequest, String sort, String dir) {

		PaginatedListImpl<Asegurado> paginatedListImpl = null;
		int pageSize = (int) Long.parseLong(bundle.getString("numElementsPag"));

		try {

			Asegurado2Filtro aseguradoFiltro = generateAseguradoFiltro(
					aseguradoBean, entidadesGrupo);

			int fullListSize = aseguradoDao.getCountAsegurados(aseguradoFiltro);

			PageProperties pageProperties = new PageProperties();
			pageProperties.setFullListSize(fullListSize);
			pageProperties.setIndexRowMax((numPageRequest - 1) * pageSize
					+ pageSize - 1);
			pageProperties.setIndexRowMin((numPageRequest - 1) * pageSize);
			pageProperties.setPageNumber(numPageRequest);
			pageProperties.setPageSize(pageSize);
			pageProperties.setSort(sort);
			pageProperties.setDir(dir);

			paginatedListImpl = aseguradoDao.getPaginatedListAsegurados(
					pageProperties, aseguradoFiltro);

		} catch (DAOException dao) {
			LOGGER.error(
					"Se ha producido un error al generar el listado de asegurados",
					dao);
		}

		return paginatedListImpl;
	}

	public PaginatedListImpl<Asegurado> getPaginatedListAseguradoCargado(
			Asegurado asegurado, int numPageRequest, String sort, String dir) {

		PaginatedListImpl<Asegurado> paginatedListImpl = new PaginatedListImpl<Asegurado>();

		List<Asegurado> lstAsegurados = new ArrayList<Asegurado>();
		lstAsegurados.add(asegurado);

		int pageSize = (int) Long.parseLong(bundle.getString("numElementsPag"));

		PageProperties pageProperties = new PageProperties();
		pageProperties.setFullListSize(1);
		pageProperties.setIndexRowMax((numPageRequest - 1) * pageSize
				+ pageSize - 1);
		pageProperties.setIndexRowMin((numPageRequest - 1) * pageSize);
		pageProperties.setPageNumber(numPageRequest);
		pageProperties.setPageSize(pageSize);
		pageProperties.setSort(sort);
		pageProperties.setDir(dir);

		paginatedListImpl.setFullListSize(pageProperties.getFullListSize());
		paginatedListImpl.setObjectsPerPage(pageProperties.getPageSize());
		paginatedListImpl.setPageNumber(pageProperties.getPageNumber());
		paginatedListImpl.setList(lstAsegurados);
		paginatedListImpl.setSortCriterion(pageProperties.getSort());
		if (pageProperties.getDir().equals("asc")) {
			paginatedListImpl.setSortDirection(SortOrderEnum.ASCENDING);
		} else if (pageProperties.getDir().equals("desc")) {
			paginatedListImpl.setSortDirection(SortOrderEnum.DESCENDING);
		}

		return paginatedListImpl;
	}

	/**
	 * Genera el filtro de asegurados.
	 * 
	 * @param aseguradoBean
	 *            Bean recogido del formulario.
	 * @param entidadesGrupo
	 *            entidades del grupo al que pertenece el usuario (en caso de
	 *            que sea de perfil 5).
	 * @return
	 */
	private Asegurado2Filtro generateAseguradoFiltro(Asegurado aseguradoBean,
			List<BigDecimal> entidadesGrupo) {

		Asegurado2Filtro aseguradoFiltro = null;

		try {

			Map<String, Object> mapaAsegurado = aseguradoDao
					.getMapaAsegurado(aseguradoBean);

			aseguradoFiltro = new Asegurado2Filtro();
			aseguradoFiltro.setAseguradoBean(aseguradoBean);
			if (entidadesGrupo != null && entidadesGrupo.size() > 0) {
				// Si 'entidadesGrupo' tiene valores -> lo aÃ±ado al filtro
				aseguradoFiltro.setListaEnt(entidadesGrupo);
			} else {
				aseguradoFiltro.setListaEnt(new ArrayList<BigDecimal>());
			}
			aseguradoFiltro.setMapa(mapaAsegurado);
		} catch (DAOException dao) {
			LOGGER.error(
					"Se ha producido un error al generar el filtro de asegurado",
					dao);
		}

		return aseguradoFiltro;
	}

	public Integer getNumRows() {
		return aseguradoDao.getNumObjects(Asegurado.class);
	}

	/* -------------- fin displaytag pagination ----------------- */

	public List<Asegurado> getAseguradosGrupoEntidad(
			Asegurado aseguradoBusqueda, List<BigDecimal> entidades) {
		try {
			return aseguradoDao.getAseguradosGrupoEntidad(aseguradoBusqueda,
					entidades);
		} catch (DAOException dao) {
			LOGGER.error(
					"Se ha producido un error al generar el listado de aseguros por grupo de entidades",
					dao);
		}
		return null;
	}

	/**
	 * TMR 30-05-2012.Facturacion. Metodo que al carga un asegurados factura
	 */

	public Asegurado getAsegurado(final Long idAsegurado) {
		return (Asegurado) aseguradoDao.getObject(Asegurado.class, idAsegurado);
	}

	public Asegurado getAseguradoFacturacion(final Long idAsegurado,
			Usuario usuario, String tipo) {
		Asegurado aseg = (Asegurado) aseguradoDao.getObject(Asegurado.class,
				idAsegurado);
		// ASF 12/09/2012 - Se hacen los dos evict para evitar que de problemas
		// la carga de asegurados cuando el
		// usuario que lo esta cargando es el mismo que ya lo tenia cargado o
		// el mismo que lo creo.
		aseguradoDao.evict(aseg.getUsuario());
		aseguradoDao.evict(aseg);

		// TMR 30-05-2012 facturacion. LLamamos a facturacion. este metodo se
		// llama al cargar el asegurados
		try {
			aseguradoDao.callFacturacion(usuario, tipo);
		} catch (Exception e) {
			LOGGER.error("Ha ocurrido un error al facturar la carga de asegurados");

		}
		return aseg;
	}

	@SuppressWarnings("unchecked")
	public final Asegurado getAseguradoUnico(final BigDecimal codentidad,
			final BigDecimal codEntMed, final BigDecimal codSubentMed,
			final String nifcif, final String discriminante,
			final Long idAsegurado) {
		Asegurado resultado = null;

		final AseguradoFiltro filter = new AseguradoFiltro(codentidad,
				codEntMed, codSubentMed, nifcif, discriminante, idAsegurado);
		final List<Asegurado> lista = aseguradoDao.getObjects(filter);
		
		/* Pet. 62719 ** MODIF TAM */
		/* Si se ha encontrado asegurado se comprueba que no esté bloqueado, en caso de estarlo se permite el alta*/
		try {
			if (null != lista && !lista.isEmpty()) {
				Asegurado aseg = null;
				aseg = lista.get(0);
				BloqueosAsegurado blq = aseguradoDao.consultarAsegBloqueado(aseg.getNifcif());
				
				if (blq != null) {
					/* retornamos null, por que el asegurado encontrado está bloqueado y se permite el alta del nuevo asegurado como alta nueva */
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al consultar Asegurado bloqueado",	e);
		}
				

		if (null != lista && !lista.isEmpty()) {
			resultado = lista.get(0);
		}
		return resultado;
	}

	@SuppressWarnings("finally")
	public ArrayList<Integer> saveAsegurado(
			final Asegurado aseguradoBean, Usuario usuario,
			boolean importarCsv, boolean saveIban) throws BatchUpdateException {
		// Comprobaciones previas a la grabacion
		ArrayList<Integer> error = null;
		try {
			error = comprobarDatosGrabacion(aseguradoBean);
			// SI NO HAY ERRORES O SOLO HAY EL ERROR 69
			if (error.size() == 0 || (error.size() == 1 && error.get(0) == 69)) {
				// TMR 29-05-2012 Facturacion
				aseguradoDao.saveOrUpdateFacturacion(aseguradoBean, usuario);

				if (saveIban) {
					DatoAsegurado da = aseguradoBean.getDatoAsegurados()
							.iterator().next();
					da.setAsegurado(aseguradoBean);
					try {
						aseguradoDao.saveOrUpdate(da);
						// llamada al PL para insertar el estado en el historico
						historicoEstadosManager.insertaEstadoDatosAseg(
								da.getId(), usuario.getCodusuario(),
								Constants.ALTA);
					} catch (Exception e) {
						if (error != null)
							error.add(new Integer(16));// error Datos asegurado
						LOGGER.error(
								"Se ha producido un error al guardar el iban del asegurado",
								e);
					} finally {
						return error;
					}
				}
				if (importarCsv) {
					return error;
				} else {
					error.add(new Integer(0));
				}
			}
		} catch (Exception e) {
			if (e instanceof UncategorizedSQLException) {
				if (error != null)
					error.add(new Integer(15));// error triger

			} else {
				if (error != null)
					error.add(new Integer(999));// error grave
				LOGGER.error(
						"Se ha producido un error al guardar el Asegurado", e);
			}
		}

		return error;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Integer> comprobarDatosGrabacion(
			final Asegurado aseguradoBean) throws DAOException {
		ArrayList<Integer> error = new ArrayList<Integer>();
		List<Entidad> comprobaciones = new ArrayList<Entidad>();
		// Comprobamos la entidad
		comprobaciones = (List<Entidad>) aseguradoDao.getObjects(Entidad.class,
				"codentidad", aseguradoBean.getEntidad().getCodentidad());
		if (comprobaciones.size() == 0) {
			// AÃ±adimos error de entidad inexistente
			error.add(new Integer(1));
		}
		// Comprobamos la validez del CIF/NIF
		if (!StringUtils.validaNifCif(aseguradoBean.getTipoidentificacion(),
				aseguradoBean.getNifcif())) {
			error.add(new Integer(2));
		}
		// Comprobamos la via
		List<Via> existeVia = (List<Via>) aseguradoDao.getObjects(Via.class,
				"clave", aseguradoBean.getVia().getClave());
		if (existeVia.size() == 0) {
			error.add(new Integer(3));
		}
		// Existe la provincia, localidad y la sublocalidad, comprobamos que la
		// relacion de esos 3 campos sea correcta
		Localidad existeRelacion = (Localidad) aseguradoDao.getObject(
				Localidad.class, aseguradoBean.getLocalidad().getId());
		if (existeRelacion == null) {
			error.add(new Integer(7));
		} else {		
			// SI LOS DOS PRIMEROS DIGITOS DEL CP NO COINCIDEN CON LA PROVINCIA SE MUESTRA AVISO
			
			// Si el codigo postal empieza por 0
			if (aseguradoBean.getCodpostal().toString().length() < 5) {
				
				// Y es valido
				if (aseguradoBean.getCodpostal().toString().length()==4) {
					
					// Comparamos con el codigo de provincia
					if (!new BigDecimal(aseguradoBean.getCodpostal().toString().substring(0,1)).equals(existeRelacion.getProvincia().getCodprovincia())) {
						error.add(new Integer(69));				
					}
				}
				else {
					error.add(new Integer(69));	
				}
				
			} else {
			
				if (!new BigDecimal(aseguradoBean.getCodpostal().toString().substring(0, 2))
					.equals(existeRelacion.getProvincia().getCodprovincia())) {
					error.add(new Integer(69));				
				}
			}	
		}
		
		// Comprobar existe codusuario

		// P000019224@048 Agregamos condición por si es un alta
		if (aseguradoBean.getId() != null) {
			String codUsuarioForm = aseguradoBean.getUsuario().getCodusuario();
			if (!StringUtils.isNullOrEmpty(codUsuarioForm)) {
				Usuario usuarioForm = (Usuario) aseguradoDao.getObject(
						Usuario.class, codUsuarioForm);
				BigDecimal entMedUsuForm = usuarioForm.getSubentidadMediadora()
						.getId().getCodentidad();
				BigDecimal subEntMedUsuForm = usuarioForm
						.getSubentidadMediadora().getId().getCodsubentidad();

				if (entMedUsuForm.compareTo(aseguradoBean.getUsuario()
						.getSubentidadMediadora().getId().getCodentidad()) != 0
						|| subEntMedUsuForm.compareTo(aseguradoBean
								.getUsuario().getSubentidadMediadora().getId()
								.getCodsubentidad()) != 0) {
					error.add(new Integer(9));
				}
			}
		}
		// comprobamos que la E-S Med este asociada a la Entidad en
		// tb_subentidades_mediadoras

		SubentidadMediadora subentidadMediadoraBean = new SubentidadMediadora();
		subentidadMediadoraBean.getId().setCodentidad(
				aseguradoBean.getUsuario().getSubentidadMediadora().getId()
						.getCodentidad());
		subentidadMediadoraBean.getId().setCodsubentidad(
				aseguradoBean.getUsuario().getSubentidadMediadora().getId()
						.getCodsubentidad());
		try {
			Integer count = subentidadMediadoraDao.existeRegistro(
					subentidadMediadoraBean, false, aseguradoBean.getEntidad()
							.getCodentidad());
			// si existe miramos si esta dada de baja
			if (count > 0) {
				boolean esBaja = !subentidadMediadoraDao.isSubentidadMedBaja(
						aseguradoBean.getUsuario().getSubentidadMediadora()
								.getId().getCodentidad(), aseguradoBean
								.getUsuario().getSubentidadMediadora().getId()
								.getCodsubentidad());
				if (esBaja) {
					error.add(new Integer(10));
				}
				// si no existe error
			} else {
				error.add(new Integer(11));
			}
		} catch (DAOException e) {
			LOGGER.error("Error al comprobar la subentidad Mediadora" + e);
			throw e;
		}

		return error;
	}

	public final void dropAsegurado(Asegurado asegurado, Usuario usuario) {
		// TMR 30-05-2012 Facturacion. Al borrar facturamos. Tipo A
		aseguradoDao.removeObjectFacturacion(Asegurado.class,
				asegurado.getId(), usuario);
	}

	@SuppressWarnings("unchecked")
	public List<Poliza> getPolizasByIdAsegurado(final Long id) {
		final Poliza poliza = new Poliza();
		poliza.getAsegurado().setId(id);
		final SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		return aseguradoDao.getObjects(filter);
	}

	public List<Poliza> tienePolizasVigentes(Asegurado aseguradoBean,
			Map<String, Object> parameters) throws DAOException {
		List<Poliza> polizas = new LinkedList<Poliza>();

		String listIdPolizas = "";
		BigDecimal[] estados = new BigDecimal[2];
		try {
			LOGGER.debug("INIT - tienePolizasVigentes - comprobamos si el asegurado tiene alguna poliza en estado "
					+ "pendiente de validacion o grabacion provisional con plan actual y anterior");

			// estados poliza grabacion provisional y pendiente de validacion
			estados[0] = Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION;
			estados[1] = Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL;

			polizas = aseguradoDao.tienePolizasVigentes(aseguradoBean, estados);

			if (polizas.size() > 0) {
				parameters.put("polizasAsegurado", polizas);
				parameters
						.put("tituloListadoPolizasAseg", tituloListadoPolizas);
				parameters.put("showPopupPolAsegurados", "true");
				// guardamos los ids de las polizas para luego no tener que
				// volver
				// a consultarlas en caso de que le de actualizar del popup

				for (int i = 0; i < polizas.size(); i++) {
					Poliza p = polizas.get(i);
					if (i == 0)
						listIdPolizas += p.getIdpoliza();
					else
						listIdPolizas += "," + p.getIdpoliza();
				}
				parameters.put("listIdPolizas", listIdPolizas);
			}

		} catch (DAOException dao) {
			LOGGER.error(
					"Se ha producido un error al obtener las polizas vigentes del Asegurado",
					dao);
			throw dao;
		}
		return polizas;
	}

	public void desbloqueaAsegurado(String usuarioAsegurado)
			throws DAOException {
		try {
			aseguradoDao.desbloqueaAsegurado(usuarioAsegurado);
		} catch (DAOException e) {
			LOGGER.error("Error al desbloquear un asegurado");
			throw e;
		}
	}

	public HashMap<String, Object> subvencionabilidadAsegurado(Long idAsegurado,
			String nifcif, String realPath, String usuario, String id, String codLinea, String codPlan) {
		WSResponse<AseguradoDatosYMedidasDocument> respuesta = null;
		boolean alertaSubvencionable = false;
		HashMap<String, Object> params = new HashMap<String, Object>();
		try {
			respuesta = new SWAsegDatosYMedidasHelper()
					.getSolicitudAseguradoActualizado(nifcif, codPlan, codLinea, realPath);
			AseguradoDatosYMedidasDocument data = respuesta.getData();
			if (null != idAsegurado) {
				// Inserta la comunicacion con el SW de impresion de incidencias
				// de Modificacion en la tabla correspondiente
				insertarEnviosSWSolicitud(idAsegurado, nifcif, usuario, data);
			}
			AseguradoDatosYMedidas aseguradoDatosYMedidas = data.getAseguradoDatosYMedidas();
			DatosPersonales datosPersonales = aseguradoDatosYMedidas.getDatosPersonales();
			if(datosPersonales.getControlAccesoSubvenciones() != null && datosPersonales.getControlAccesoSubvenciones().getOrganismoArray() != null){
				Organismo[] organismos = datosPersonales.getControlAccesoSubvenciones().getOrganismoArray();
				alertaSubvencionable = !aseguradoSubvencionable(organismos);
			}
			params.put("alertaSubvencionable", alertaSubvencionable);
			return params;
		} catch (AgrException e) {
			LOGGER.error(
					"Ocurrio un error en la llamada al SW de ContratacionSCAsegurado - SolicitudAseguradoActualizado", e);
			params.put("error", getMsgAgrException(e));
			return params;

		} catch (Exception e) {
			LOGGER.error("Ocurrio un error inesperado en la llamada al SW de ContratacionSCAsegurado -SolicitudAseguradoActualizado", e);
			params.put("error", bundleAgp.getString("mensaje.asegurado.obtenerDatos.KO"));
			return params;
		}
	}
	
	public Map<String, Object> getDatosAseguradoWS(Long idAsegurado,
			String nifcif, String realPath, String usuario, String id, String codLinea, String codPlan) {

		Map<String, Object> parameters = new HashMap<String, Object>();

		WSResponse<AseguradoDatosYMedidasDocument> respuesta = null;
		try {
			respuesta = new SWAsegDatosYMedidasHelper()
					.getSolicitudAseguradoActualizado(nifcif, codPlan, codLinea, realPath);
			AseguradoDatosYMedidasDocument data = respuesta.getData();
			if (null != idAsegurado) {
				// Inserta la comunicacion con el SW de impresion de incidencias
				// de Modificacion en la tabla correspondiente
				insertarEnviosSWSolicitud(idAsegurado, nifcif, usuario, data);
			}

			// Convierte la respuesta en un bean con los campos necesarios para
			// enviar a la pantalla
			AseguradoDatosYMedidas aseguradoDatosYMedidas = data.getAseguradoDatosYMedidas();
			Asegurado asegurado = solicitudAseguradoToBean(aseguradoDatosYMedidas);
			if (asegurado != null) {

				if (null != idAsegurado) {
					parameters.put("aseguradoPopup", asegurado);
				} else {
					parameters.put("aseguradoBean", asegurado);
				}
			} else {
				/*
				 * si el asegurado viene a null, es que el servicio web nos ha
				 * devuelto el xml pero no ha devuelto datos para ese asegurado
				 * (solo devuelve el nif). En tal caso tambien actualizamos la
				 * fecha de revision del asegurado pero no el campo revisado
				 */
				aseguradoDao.actualizaFechaRevision(id, "S");
				parameters.put("error",
						bundleAgp.getString("mensaje.asegurado.sinDatos.KO"));
			}
		} catch (AgrException e) {			
			LOGGER.error(
					"Ocurrio un error en la llamada al SW de "
							+ "ContratacionSCAsegurado - SolicitudAseguradoActualizado",
					e);
			parameters.put("error", getMsgAgrException(e));
			return parameters;
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error inesperado en la llamada al SW de "
					+ "ContratacionSCAsegurado -SolicitudAseguradoActualizado",
					e);
			parameters.put("error",
					bundleAgp.getString("mensaje.asegurado.obtenerDatos.KO"));
			return parameters;
		}
		return parameters;
	}

	private boolean aseguradoSubvencionable(Organismo[] organismos) {
		boolean esSubvencionable = false;
		for(Organismo org : organismos){
			if (org.getOrganismo().equals(Constants.ORGANISMO_ENESA)) {
				Subvencion[] subvenciones = org.getSubvencionArray();
				for(Subvencion subv : subvenciones){
					if(subv.getTipo() == Constants.ASEGURADO_SUBVENCIONABLE){
						esSubvencionable = true;
						break;
					}
				}
			}
		}
		return esSubvencionable;
	}

	/**
	 * Inserta la comunicacion con el SW de solicitud asegurado actualizado en
	 * la tabla correspondiente
	 * 
	 * @param usuario
	 * @param response
	 * @throws DAOException
	 */
	private void insertarEnviosSWSolicitud(Long idAsegurado, String nifcif,
			String usuario, AseguradoDatosYMedidasDocument data)
			throws Exception {

		AseguradoSWDatPerMed bean = new AseguradoSWDatPerMed();

		Asegurado a = new Asegurado();
		a.setId(idAsegurado);
		bean.setAsegurado(a);
		bean.setUsuario(usuario);
		bean.setNif(nifcif);
		bean.setFecha(new Date());
		bean.setRespuesta(Hibernate.createClob(data.toString()));

		try {
			aseguradoDao.saveOrUpdate(bean);
		} catch (Exception e) {
			LOGGER.error(
					"Error al insertar el registro de la comunicacion con el SW de Solicitud de Asegurado actualizado",
					e);
			throw e;
		}
	}

	/**
	 * Devuelve el bean con los datos devueltos por el SW para mostrar en el
	 * popup
	 * 
	 * @param respuesta
	 *            Objeto que encapsula la respuesta del SW
	 * @return
	 * @throws DAOException
	 */
	public Asegurado solicitudAseguradoToBean(
			AseguradoDatosYMedidas aseguradoDatosYMedidas) throws DAOException {
		
		Asegurado a = new Asegurado();

		// Datos personales
		if (aseguradoDatosYMedidas.getDatosPersonales() != null) {
			// Asegurado
			if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado() != null) {

				a.setNifcif(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getNif());

				// Nombre Apellidos / razon social
				if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getRazonSocial() != null) {
					a.setRazonsocial(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
							.getRazonSocial().getRazonSocial());
				} else {
					a.setApellido1(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
							.getNombreApellidos().getApellido1());
					a.setApellido2(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
							.getNombreApellidos().getApellido2());
					a.setNombre(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
							.getNombreApellidos().getNombre());
				}
				// Direccion
				if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion() != null) {

					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getBloque() != null)
						a.setBloque(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDireccion().getBloque());
					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getCp() != null)
						a.setCodpostal(new BigDecimal(aseguradoDatosYMedidas.getDatosPersonales()
								.getAsegurado().getDireccion().getCp()));
					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getEscalera() != null)
						a.setEscalera(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDireccion().getEscalera());
					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getNumero() != null)
						a.setNumvia(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDireccion().getNumero());
					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getPiso() != null)
						a.setPiso(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDireccion().getPiso());

					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getVia() != null) {
						// cortamos la direccion por espacios
						String[] cad = aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDireccion().getVia().split(" ");
						// cojemos la primera palabra, que en teoria corresponde
						// con el nombre de la via
						String viaAux = cad[0];
						// si la longitud es igual a 2 es que nos viene la clave
						// de la via
						if (viaAux.length() == 2) {
							// asignamos la direcion. Cogemos a partir del 3
							// caracter
							a.setDireccion(aseguradoDatosYMedidas.getDatosPersonales()
									.getAsegurado().getDireccion().getVia()
									.substring(3));
							// sacamos el nombre de la via a partir de la clave
							Via viaaux = (Via) aseguradoDao.get(Via.class, aseguradoDatosYMedidas
									.getDatosPersonales().getAsegurado()
									.getDireccion().getVia().substring(0, 2));
							// si lo encontramos
							if (viaaux != null) {
								// asignamos la via
								a.getVia().setClave(
										aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
												.getDireccion().getVia()
												.substring(0, 2));
								// si no lo encontramos, la direccion se rellena
								// con todo lo que viene en el campo via del ws
							} else {
								a.setDireccion(aseguradoDatosYMedidas.getDatosPersonales()
										.getAsegurado().getDireccion().getVia());
							}
							// si no la direccion se rellena con todo lo que
							// viene en el campo via del ws
						} else {
							a.getVia().setClave("CL"); // Sigpe7182
							a.setDireccion(aseguradoDatosYMedidas.getDatosPersonales()
									.getAsegurado().getDireccion().getVia());

						}
					}

					if (!StringUtils.nullToString(
							aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
									.getDireccion().getProvincia()).equals(""))
						a.getLocalidad()
								.getId()
								.setCodprovincia(
										new BigDecimal(aseguradoDatosYMedidas.getDatosPersonales()
												.getAsegurado().getDireccion()
												.getProvincia()));
					if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion()
							.getLocalidad() != null)
						a.getLocalidad().setNomlocalidad(
								aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
										.getDireccion().getLocalidad());
					// obtenemos la descripcion de la provincia, el codigo de
					// localidad y sublocalidad
					Direccion direccion = aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDireccion();
					Object[] datos = aseguradoDao.getDatosProvincia(
							new BigDecimal(direccion.getProvincia()), direccion.getLocalidad());
					if (datos != null) {
						a.getLocalidad().getId()
								.setCodlocalidad((BigDecimal) datos[0]);
						a.getLocalidad().getId()
								.setSublocalidad((String) datos[1]);
						a.getLocalidad().getProvincia()
								.setNomprovincia((String) datos[2]);
					}

				}
				// Datos de contacto
				if (aseguradoDatosYMedidas.getDatosPersonales().getAsegurado().getDatosContacto() != null) {

					if (!StringUtils.nullToString(
							aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
									.getDatosContacto().getEmail()).equals(""))
						a.setEmail(aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
								.getDatosContacto().getEmail());

					if (!StringUtils.nullToString(
							aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
									.getDatosContacto().getTelefonoFijo())
							.equals(""))
						a.setTelefono(""
								+ aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
										.getDatosContacto().getTelefonoFijo());

					if (!StringUtils.nullToString(
							aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
									.getDatosContacto().getTelefonoMovil())
							.equals("")
							&& aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
									.getDatosContacto().getTelefonoMovil() != 0)
						a.setMovil(""
								+ aseguradoDatosYMedidas.getDatosPersonales().getAsegurado()
										.getDatosContacto().getTelefonoMovil());
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
		return a;
	}

	/**
	 * Actualiza el asegurado de bbd con los datos del WS
	 * 
	 * @param idAseg
	 * @param asegurado
	 * @throws Exception
	 * 
	 */

	public void actualizaDatosAseguradoWS(Asegurado aseguradoBean,
			String idAseg, String revisado) throws Exception {
		try {
			// actualizamos los datos del asegurado
			aseguradoDao.actualizaDatosAseguradoWS(aseguradoBean, idAseg);
			// actualizamos la fecha de revision
			aseguradoDao.actualizaFechaRevision(idAseg, revisado);
			aseguradoDao.evict(aseguradoBean);
		} catch (Exception e) {
			LOGGER.error("Error al actualizar el asegurado de bbdd con el ws",
					e);
			throw e;
		}
	}

	public void actualizaFechaRevision(String idAseg, Asegurado aseguradoBean,
			String revisado) throws Exception {

		try {
			aseguradoDao.actualizaFechaRevision(idAseg, revisado);
			aseguradoDao.evict(aseguradoBean);
		} catch (Exception e) {
			LOGGER.error("Error al actualizar el asegurado de bbdd con el ws",
					e);
			throw e;
		}
	}

	/**
	 * Devuelve una cadena con los errores devueltos en una AgrException
	 * 
	 * @param exc
	 * @return
	 */
	private String getMsgAgrException(AgrException exc) {
		StringBuilder sb = new StringBuilder();
		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				sb.append(error.getMensaje()).append(". ");
			}
		}
		return sb.toString();

	}

	public Usuario getUsuario(final User user) {
		return (Usuario) aseguradoDao.getObject(Usuario.class,
				user.getIdUsuario());
	}

	public Asegurado getAseguradobyId(Asegurado aseguradoBean, String idAseg)
			throws Exception {
		try {

			Asegurado asegAux = aseguradoDao.getAseguradoById(idAseg);
			aseguradoBean
					.setTipoidentificacion(asegAux.getTipoidentificacion());
			aseguradoBean.setNumsegsocial(asegAux.getNumsegsocial());
			aseguradoBean.setRegimensegsocial(asegAux.getRegimensegsocial());
			aseguradoBean.setAtp(asegAux.getAtp());
			aseguradoBean.setJovenagricultor(asegAux.getJovenagricultor());
			aseguradoBean.setUsuario(asegAux.getUsuario());
			aseguradoBean.getEntidad().setCodentidad(
					asegAux.getEntidad().getCodentidad());
			aseguradoBean.getEntidad().setNomentidad(
					asegAux.getEntidad().getNomentidad());
			return aseguradoBean;

		} catch (Exception e) {
			LOGGER.error("Error al recuperar los datos del asegurado", e);
			throw e;
		}
	}

	// FUNCIONES Y MÉTODOS DE TRATAMIENTYO DEL FICHERO CSV
	// -------------------------------------------------------
	public JSONObject importarCSV(MultipartFile file, HttpServletRequest request)
			throws Exception {

		Map<String, ArrayList<String>> resFichero = new HashMap<String, ArrayList<String>>();
		ArrayList<Integer> resGrabacion = new ArrayList<Integer>();
		int contadorRegistros = 0;
		int contadorRegistrosKO = 0;
		int contadorRegistrosOK = 0;
		try {
			Charset charset = Charset.forName("ISO-8859-1");
			CsvReader csv = new CsvReader(file.getInputStream(), ';', charset);

			while (csv.readRecord()) {
				Map<String, ArrayList<String>> resRegistro = new HashMap<String, ArrayList<String>>();
				contadorRegistros += 1;
				AseguradoCsvRegistro regAdeg = new AseguradoCsvRegistro(csv);
				resRegistro = validaCampos(regAdeg);
				this.asignaDatosAseg(regAdeg, resRegistro);

				if (regAdeg.getEsRegistroValido()) {// Sin errores

					// Convertimos en objeto Asegurado e intentamos grabar
					Asegurado aseg = new Asegurado(regAdeg);
					// Si el asegurado se ha guardado OK y tenemos iban,
					// guardamos el iban en tb_datos_asegurados
					// con la linea 999, le pasamos true o false al metodo
					// saveAsegurado
					boolean saveIban = false;
					if (aseg.getDatoAsegurados().size() > 0) {
						saveIban = true;
					}
					ArrayList<Integer> resGrab = new ArrayList<Integer>();
					if (!this.existeAsegurado(aseg, resGrab)) {
						// no existe el asegurado-> damos de alta
						aseg.setFechaModificacion(new Date());
						final Usuario user = (Usuario) request.getSession()
								.getAttribute("usuario");
						aseg.setUsuarioModificacion(user.getCodusuario());
						resGrab = saveAsegurado(aseg, aseg.getUsuario(), true,
								saveIban);
					}
					if (resGrab.size() > 0) {
						if (resGrab.size() == 1 && resGrab.get(0) != 69) {
							contadorRegistrosKO += 1;
							
						}
						else {
							contadorRegistrosOK +=1;
						}
						resGrabacion.addAll(resGrab);
						mensajeErroresImportacionCsv(resGrab, resRegistro,
								aseg.getNifcif(), aseg.getTipoidentificacion()); 
					} else {
						contadorRegistrosOK += 1;
					}
				} else {// Con errores
					contadorRegistrosKO += 1;

				}
				resFichero.putAll(resRegistro);
			}

			JSONObject json = formatMensajesImportacionCSV(contadorRegistros,
					contadorRegistrosOK, contadorRegistrosKO, resFichero);
			return json;

		} catch (Exception e) {
			LOGGER.error("Error al importar el fichero de asegurados" + e);
			throw e;
		}

	}

	private boolean existeAsegurado(Asegurado aseg, ArrayList<Integer> resGrab) {
		// antes de guardar el asegurado comprobamos si existe
		// Nos aseguramos que NO exista un asegurado con esos datos
		Asegurado aseguradoBusqueda = this.getAseguradoUnico(aseg.getEntidad()
				.getCodentidad(), aseg.getUsuario().getSubentidadMediadora()
				.getId().getCodentidad(), aseg.getUsuario()
				.getSubentidadMediadora().getId().getCodsubentidad(), aseg
				.getNifcif(), aseg.getDiscriminante(), null);
		if (null == aseguradoBusqueda) {
			return false;

		}
		resGrab.add(17);
		return true;

	}

	private void asignaDatosAseg(AseguradoCsvRegistro regAdeg,
			Map<String, ArrayList<String>> resRegistro) throws DAOException {
		// Cargamos el usuario para obtener la E-S Mediadora
		Usuario usu;
		try {
			usu = (Usuario) aseguradoDao.get(Usuario.class,
					regAdeg.getUsuario());
			if (usu != null) {
				regAdeg.setEntidadMediadora(usu.getSubentidadMediadora()
						.getId().getCodentidad());
				regAdeg.setSubEntidadMediadora(usu.getSubentidadMediadora()
						.getId().getCodsubentidad());
				regAdeg.setCodEntidad(usu.getOficina().getId().getCodentidad());
				regAdeg.setCodOficina(usu.getOficina().getId().getCodoficina());
			} else {
				ArrayList<String> mensajes = new ArrayList<String>();
				mensajes.add("El usuario no existe");
				resRegistro.put(regAdeg.getIdentificacion(), mensajes);
				regAdeg.setEsRegistroValido(false);
			}

		} catch (DAOException e) {
			LOGGER.error("Error al cargar el usuario");
			throw e;
		}
		regAdeg.setDiscriminante("0");

		regAdeg.setRevisado(Constants.CHARACTER_N);

	}

	private Map<String, ArrayList<String>> validaCampos(
			AseguradoCsvRegistro regAseg) {
		Map<String, ArrayList<String>> resRegistro = new HashMap<String, ArrayList<String>>();
		ArrayList<String> mensajes = new ArrayList<String>();

		// NIF/CIF/NIE: cadena de caracteres de 9 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getIdentificacion(),
				regAseg.getTipoIdentificacion(), true, false, 9, 0, null));

		// Entidad: numérico de 4 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getEntidad(), "Entidad",
				true, true, 4, 0, null));

		// Tipo identificación: se podrán indicar los valores NIF, CIF o NIE.
		// Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getTipoIdentificacion(),
				"Tipo identificacion", true, false, 0, 0, "NIF;CIF;NIE"));

		// Nombre: cadena de caracteres de 20 posiciones. Obligatorio si tipo
		// identificación es NIF o NIE.
		if (regAseg.getTipoIdentificacion().compareTo("NIF") == 0
				|| regAseg.getTipoIdentificacion().compareTo("NIE") == 0) {
			mensajes.addAll(regAseg.isValidCampo(regAseg.getNombre(), "Nombre",
					true, false, 0, 20, null));
			mensajes.addAll(regAseg.isValidCampo(regAseg.getApellido1(),
					"Primer apellido", true, false, 0, 40, null));
		} else {
			mensajes.addAll(regAseg.isValidCampo(regAseg.getNombre(), "Nombre",
					false, false, 0, 20, null));
			mensajes.addAll(regAseg.isValidCampo(regAseg.getApellido1(),
					"Primer apellido", false, false, 0, 40, null));
		}

		// Segundo apellido: cadena de caracteres de 40 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getApellido2(),
				"Segundo apellido", false, false, 0, 40, null));

		// Razón social: cadena de caracteres de 50 posiciones. Obligatorio si
		// tipo identificación es CIF.
		if (regAseg.getTipoIdentificacion().compareTo("CIF") == 0) {
			mensajes.addAll(regAseg.isValidCampo(regAseg.getRazonSocial(),
					"Raz&oacuten social:", true, false, 0, 50, null));
		} else {
			mensajes.addAll(regAseg.isValidCampo(regAseg.getRazonSocial(),
					"Raz&oacuten social:", false, false, 0, 50, null));
		}

		// Usuario: cadena de caracteres de 8 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getUsuario(), "Usuario",
				true, false, 0, 8, null));

		// Tipo de vía: cadena de caracteres de 2 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getTipoVia(),
				"Tipo de v&iacutea", true, false, 2, 0, null));

		// Domicilio: cadena de caracteres de 200 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getDomicilio(),
				"Domicilio", true, false, 0, 200, null));

		// Número: cadena de caracteres de 5 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getNumero(),
				"N&uacutemero", true, false, 0, 5, null));

		// Piso: cadena de caracteres de 12 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getPiso(), "Piso", false,
				false, 0, 12, null));

		// Bloque: cadena de caracteres de 10 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getBloque(), "Bloque",
				false, false, 0, 10, null));

		// Escalera: cadena de caracteres de 10 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getEscalera(), "Escalera",
				false, false, 0, 10, null));

		// Código de provincia: numérico de 2 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getCodProvincia(),
				"Provincia", true, true, 0, 2, null));

		// Código de localidad: numérico de 3 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getCodLocalidad(),
				"Localidad", true, true, 0, 3, null));

		// Sublocalidad: cadena de caracteres de 1 posición. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getCosSublocalidad(),
				"Sublocalidad", true, true, 0, 3, null));

		// Código postal: cadena de caracteres de 5 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getCodPostal(),
				"C&oacutedigo postal", true, true, 0, 0, null));

		// Teléfono fijo: cadena de caracteres de 9 posiciones. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getTelefonoFijo(),
				"Tel&eacutefono fijo", true, true, 9, 0, null));

		// Teléfono móvil: cadena de caracteres de 9 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getTelefonoMovil(),
				"Teléfono móvil", false, true, 9, 0, null));

		// e-mail: cadena de caracteres de 50 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getEmail(), "e-mail",
				false, false, 0, 50, null));

		// Número de la seguridad social: cadena de caracteres de 12 posiciones.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getNumSeguridadSoc(),
				"N&uacute;mero de la seguridad social", false, true, 12, 0,
				null));

		// Indicador de régimen: se permitirá elegir entre los valores 0, 1 ó 2:
		// 'Autónomo', 'Rea cuenta Ajena' o 'Rea cuenta propia' respectivamente.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getRegSeguridadSoc(),
				"Indicador de r&eacutegimen", false, true, 1, 0, "0;1;2"));

		// ATP: se permitirá elegir entre los valores 'SI' y 'NO'. Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getAtp(), "ATP", true,
				false, 2, 0, "SI;NO"));

		// Joven Agricultor: se permitirá elegir entre los valores 'SI' y 'NO'.
		// Obligatorio.
		mensajes.addAll(regAseg.isValidCampo(regAseg.getJovenAgricultor(),
				"Joven Agricultor", true, false, 2, 0, "SI;NO"));

		// IBAN: cadena de caracteres de 24 posiciones.
		StringBuilder sb1 = new StringBuilder();
		sb1.append(regAseg.getIbanPrima1());
		sb1.append(regAseg.getIbanPrima2());
		sb1.append(regAseg.getIbanPrima3());
		sb1.append(regAseg.getIbanPrima4());
		sb1.append(regAseg.getIbanPrima5());
		sb1.append(regAseg.getIbanPrima6());
		mensajes.addAll(regAseg.isValidCampo(sb1.toString(), "IBAN PRIMA", false, false, 24, 0, null));
		
		// IBAN: cadena de caracteres de 24 posiciones.
		StringBuilder sb2 = new StringBuilder();
		sb2.append(regAseg.getIbanSiniestro1());
		sb2.append(regAseg.getIbanSiniestro2());
		sb2.append(regAseg.getIbanSiniestro3());
		sb2.append(regAseg.getIbanSiniestro4());
		sb2.append(regAseg.getIbanSiniestro5());
		sb2.append(regAseg.getIbanSiniestro6());
		mensajes.addAll(regAseg.isValidCampo(sb2.toString(), "IBAN SINIESTROS", false, false, 24, 0, null));

		if (mensajes.size() > 0) {
			resRegistro.put(regAseg.getIdentificacion(), mensajes);
			regAseg.setEsRegistroValido(false);
		} else
			regAseg.setEsRegistroValido(true);

		return resRegistro;
	}

	private void mensajeErroresImportacionCsv(
			ArrayList<Integer> errorAsegurado,
			Map<String, ArrayList<String>> resRegistro, String identificacion,
			String tipoIdent) {

		ArrayList<String> mensajes = new ArrayList<String>();

		for (Integer error : errorAsegurado) {
			switch (error.intValue()) {
			case 0:
				break;
			case 1:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.entidad.inexistente"));
				break;
			case 2:
				mensajes.add("El " + tipoIdent + " introducido no es correcto.");
				break;
			case 3:
				mensajes.add(bundleAgp.getString("mensaje.asegurado.via.KO"));
				break;
			case 4:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.provincia.KO"));
				break;
			case 5:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.localidad.KO"));
				break;
			case 6:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.sublocalidad.KO"));
				break;
			case 7:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.relacion.KO"));
				break;
			case 8:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.codusuario.KO"));
				break;
			case 9:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.subentmed.KO"));
				break;
			case 10:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.ESMedBaja.KO"));
				break;
			case 11:
				mensajes.add(bundleAgp.getString("mensaje.asegurado.ESMed.KO"));
				break;
			case 15:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.nifDuplicadoEnt.KO"));
				break;
			case 16:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.ErrorIban.KO"));
				break;
			case 17:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.duplicado.importacion.KO"));
				break;
			case 69:
				mensajes.add(bundleAgp
						.getString("mensaje.asegurado.codPostal.KO"));
				break;
			default:
				mensajes.add(bundleAgp.getString("mensaje.error.grave"));
			}
		}

		if (mensajes.size() > 0) {
			resRegistro.put(identificacion, mensajes);
		}

	}

	private JSONObject formatMensajesImportacionCSV(int contadorRegistros,
			int contadorRegistrosOK, int contadorRegistrosKO,
			Map<String, ArrayList<String>> resFichero) throws JSONException {
		JSONObject json = new JSONObject();

		ArrayList<String> listaFinal = new ArrayList<String>();
		ArrayList<String> lstaMensajes = new ArrayList<String>();

		json.put("registrosImportacion", +contadorRegistros);
		json.put("registrosImportacionOK", +contadorRegistrosOK);
		json.put("registrosImportacionKO", +contadorRegistrosKO);

		if (!resFichero.isEmpty()) {
			Iterator<Entry<String, ArrayList<String>>> it = resFichero
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, ArrayList<String>> ent = it.next();

				listaFinal.add("NIF" + (String) ent.getKey() + " : ");

				lstaMensajes = (ArrayList<String>) ent.getValue();
				for (String mensaje : lstaMensajes) {
					listaFinal.add(mensaje);
				}
			}
		}
		json.put("registrosImportacionKOLista", listaFinal);

		return json;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void setSubentidadMediadoraDao(
			ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}

	public void asociarSubvsPolizaAseg(Long id, String listIdPolizas,
			String subv10, String subv20, Usuario user) throws Exception {
		
		LOGGER.debug("AseguradoManager - asociarSubvsPolizaAseg - init");

		
		try {
			Asegurado asegurado = this.getAsegurado(id);
			List<Object> subvSelecEnesa = new ArrayList<Object>();
			SubvencionEnesa subvencionEnesa = null;
			Set<?> subvAsegEnesa;
			//Set<?> subvAsegCcaa;
			BigDecimal codSubv;
			
			String[] idsPol = listIdPolizas.split(",");
			for (int i = 0; i < idsPol.length; i++) {
				Long idPol = new Long(idsPol[i]);
				Poliza poliza = (Poliza) aseguradoDao.getObject(Poliza.class,
						idPol);
				List<SubvencionesAseguradosView> subvencionList = aseguradoSubvencionManager
						.getSubvencionesAsegurado(asegurado, poliza, user,
								false);
				
				// ESC-31458: recuperamos las subvenciones de la poliza, para no perdelas
				if (poliza.getLinea().isLineaGanado()) {
					subvAsegEnesa = poliza.getSubAseguradoENESAGanados();
					//subvAsegCcaa = poliza.getSubAseguradoCCAAGanados();
					
					for (Object subEnAseg : subvAsegEnesa) {
						codSubv = ((SubAseguradoENESAGanado) subEnAseg).getSubvencionEnesaGanado()
								.getTipoSubvencionEnesa().getCodtiposubvenesa();
						if (!codSubv.equals(Constants.SUBVENCION_JOVEN_HOMBRE)
								&& !codSubv.equals(Constants.SUBVENCION_AG_GR_PROFESIONAL)) {
							subvSelecEnesa.add(((SubAseguradoENESAGanado) subEnAseg).getSubvencionEnesaGanado());
						}
					}
				} else {
					subvAsegEnesa = poliza.getSubAseguradoENESAs();
					//subvAsegCcaa = poliza.getSubAseguradoCCAAs();
					
					for (Object subEnAseg : subvAsegEnesa) {
						codSubv = ((SubAseguradoENESA) subEnAseg).getSubvencionEnesa()
								.getTipoSubvencionEnesa().getCodtiposubvenesa();
						if (!codSubv.equals(Constants.SUBVENCION_JOVEN_HOMBRE)
								&& !codSubv.equals(Constants.SUBVENCION_AG_GR_PROFESIONAL)) {
							subvSelecEnesa.add(((SubAseguradoENESA) subEnAseg).getSubvencionEnesa());
						}
					}
				}
				// FIN ESC-31458
				
				// comprobamos que sea compatible
				for (SubvencionesAseguradosView subv : subvencionList) {
					if (subv.getCodtiposubvencion().equals(
							Constants.SUBVENCION_JOVEN_HOMBRE)) {

						if (subv10.equals("true")) {
							subvencionEnesa = (SubvencionEnesa) subv
									.getSubvEnesa();
							subvSelecEnesa.add(subvencionEnesa);
						}
					} else if (subv.getCodtiposubvencion().equals(
							Constants.SUBVENCION_AG_GR_PROFESIONAL)) {
						if (subv20.equals("true")) {
							subvencionEnesa = (SubvencionEnesa) subv
									.getSubvEnesa();
							subvSelecEnesa.add(subvencionEnesa);
						}
					}
				}

				
				aseguradoSubvencionManager.altaSubvenciones(asegurado, poliza,
						subvSelecEnesa, null, null, user.getCodusuario(), null);
				
				subvSelecEnesa = new ArrayList<Object>();
			}
		} catch (Exception e) {
			LOGGER.debug("Error en el metodo asociarSubvsPolizaAseg(): " + e);
			throw e;
		}
		
		LOGGER.debug("AseguradoManager - asociarSubvsPolizaAseg - end");

	}

	public void showPopupSubv(HttpServletRequest request,
			Asegurado aseguradoBean, Map<String, Object> params)
			throws Exception {

		try {
			// si no hay errores miramos si se han modificado los valores
			// ATP/Joven Agricultor
			String showPopupPolAsegurados = StringUtils.nullToString(request
					.getParameter("showPopupPolAsegurados"));
			params.put("subv10",
					StringUtils.nullToString(request.getParameter("subv10")));
			params.put("subv20",
					StringUtils.nullToString(request.getParameter("subv20")));
			params.put("idAsegurado", aseguradoBean.getId());
			if (showPopupPolAsegurados.equals("true")) {
				this.tienePolizasVigentes(aseguradoBean, params);
			}
		} catch (Exception e) {
			LOGGER.debug("Error en el metodo showPopupSubv(): " + e);
			throw e;
		}
	}
	

	public String getControlSubvsAsegurado(String nifCif, String fechaEstudio, String realPath) throws Exception {
		LOGGER.info("VOY A LLAMAR AL SERVICIO WEB DE AGROSEGURO");
		String tablaSubvenciones = "";
		try {
			WSResponse<AseguradoDatosYMedidasDocument> documento = new SWAsegDatosYMedidasHelper()
					.mostrarProximasSubvenciones(nifCif, fechaEstudio, realPath);
			ProximoOrganismo[] proximoOrganismo = extraerProximosOrganismos(documento);
			Organismo[] organismos = this.extraerOrganismos(documento);
			AseguradoNoSubvencionable aseguradoNoSubvencionable = extraerAseguradoNoSubvencionables(documento);
			if(proximoOrganismo != null){
				tablaSubvenciones = AseguradoManagerGeneradorHtml.tablaSubvencionesAsegurado(aseguradoNoSubvencionable, organismos, proximoOrganismo, null, false);
			}
		} catch (es.agroseguro.serviciosweb.contratacionscasegurado.AgrException e) {			
			throw new Exception(pintarErroresAgrException(e));
		}
		LOGGER.info("HE HECHO LA LLAMADA");
		return tablaSubvenciones;
	}
	
	public String getControlSubvsAsegurado(String nifCif, String codPlan, String codLinea, String realPath) throws Exception {
		LOGGER.info("VOY A LLAMAR AL SERVICIO WEB DE AGROSEGURO");
		String tablaSubvenciones = "";
		try {
			WSResponse<AseguradoDatosYMedidasDocument> documento = new SWAsegDatosYMedidasHelper()
					.mostrarProximasSubvenciones(nifCif, codPlan, codLinea, realPath);
			ProximoOrganismo[] proximoOrganismo = extraerProximosOrganismos(documento);
			Organismo[] organismos = this.extraerOrganismos(documento);
			AseguradoNoSubvencionable aseguradoNoSubvencionable = extraerAseguradoNoSubvencionables(documento);
			
			// Incluir el código de grupo ya que la respuesta del WS no la devuelve
			for (Organismo o:organismos) {
				for (Subvencion s:o.getSubvencionArray()) {
					s.setCodigoGrupoSeguro(codLinea);
				}
			}
			
			// Incluir el código de linea ya que la respuesta del WS no la devuelve
			for (ProximoOrganismo o: proximoOrganismo) {
				for (ProximoSubvencion s:o.getSubvencionArray()) {
					s.setCodigoGrupoSeguro(codLinea);
				}
			}

			if(proximoOrganismo != null){
				tablaSubvenciones = AseguradoManagerGeneradorHtml
						.tablaSubvencionesAsegurado(aseguradoNoSubvencionable, organismos, proximoOrganismo, documento.getData().getAseguradoDatosYMedidas().getMedidas().getPorcentajeModulacionENESA(),true);
			}
		} catch (es.agroseguro.serviciosweb.contratacionscasegurado.AgrException e) {
			throw new Exception(pintarErroresAgrException(e));
		}
		LOGGER.info("HE HECHO LA LLAMADA");
		return tablaSubvenciones;
	}
	
	

	private Organismo[] extraerOrganismos(WSResponse<AseguradoDatosYMedidasDocument> documento) {
		Organismo[] organismos = null;
		DatosPersonales datosPersonales = documento.getData().getAseguradoDatosYMedidas().getDatosPersonales();
		if(datosPersonales.getControlAccesoSubvenciones() != null){
			organismos = datosPersonales.getControlAccesoSubvenciones().getOrganismoArray();
		}
		return organismos;
	}
	private AseguradoNoSubvencionable extraerAseguradoNoSubvencionables(WSResponse<AseguradoDatosYMedidasDocument> documento) {
		
		return documento.getData().getAseguradoDatosYMedidas().getDatosPersonales().getAseguradoNoSubvencionable();
	}
	
	
	private ProximoOrganismo[] extraerProximosOrganismos(
			WSResponse<AseguradoDatosYMedidasDocument> documento) {
		ProximoOrganismo[] organismos = null;
		DatosPersonales datosPersonales = documento.getData().getAseguradoDatosYMedidas().getDatosPersonales();
		if(datosPersonales.getProximoControlAccesoSubvenciones() != null 
				&& datosPersonales.getProximoControlAccesoSubvenciones().getOrganismoArray() != null){
			organismos = datosPersonales.getProximoControlAccesoSubvenciones().getOrganismoArray();
		}
		return organismos;
	}
	
	public String getControlSubvsAseguradoImportes(String nifCif, String codLinea, String codPlan, String realPath) throws Exception {
		LOGGER.info("VOY A LLAMAR AL SERVICIO WEB DE AGROSEGURO");
		StringBuilder tablaSubvenciones = new StringBuilder();
		String fragmentoModulacionEnesa = "";
		String fragmentoControAccesoSubvencionesImportes = "";
		String fragmentoSaldoReduccionEnesa = "";
		try {
			LOGGER.debug("Obteniendo datos Asegurado Info Saldo ENESA del SW de agroseguro");
			WSResponse<AseguradoInfoSaldoENESADocument> saldoENESA = 
					new SWAsegDatosYMedidasHelper().mostrarSaldoENESA(nifCif, codPlan, realPath);
			AseguradoInfoSaldoENESA aseguradoInfoSaldoENESA = saldoENESA.getData().getAseguradoInfoSaldoENESA();
			LOGGER.debug("Datos obtenidos. Generando tabla Asegurado Info Saldo ENESA");
			fragmentoSaldoReduccionEnesa = AseguradoManagerGeneradorHtml.fragmentoSaldoReduccionEnesa(aseguradoInfoSaldoENESA);
			LOGGER.debug("Tabla Asegurado Info Saldo ENESA");
		} catch (es.agroseguro.serviciosweb.contratacionscasegurado.AgrException e) {
			
			// si solamente recibimos un error y este se corresponde al no hay datos para el nif y plan
			if (e.getFaultInfo().getError().size()==1 && e.getFaultInfo().getError().get(0).getCodigo() == -1) {
				fragmentoSaldoReduccionEnesa = AseguradoManagerGeneradorHtml.fragmentoSaldoReduccionEnesa(null);
			} else {
				
				// si hay mas errores, lanzamos excepcion
				throw new Exception(pintarErroresAgrException(e));
			}
		}
		try {
			LOGGER.debug("Obteniendo los datos de Proximas Subvenciones Importes del SW de agroseguro");
			WSResponse<AseguradoDatosYMedidasDocument> datosYMedidas = 
					new SWAsegDatosYMedidasHelper().mostrarProximasSubvenciones(nifCif, codPlan, codLinea, realPath);
			LOGGER.debug("Datos obtenidos. Generando tabla html con la información del Porcentaje Modulacion ENESA");
			PorcentajeModulacionENESA porcentajeModulacionENESA = datosYMedidas.getData()
					.getAseguradoDatosYMedidas().getMedidas().getPorcentajeModulacionENESA();
			fragmentoModulacionEnesa = AseguradoManagerGeneradorHtml.fragmentoModulacionEnesa(porcentajeModulacionENESA);
			LOGGER.debug("Tabla Porcentaje Modulacion ENESA generada");
			LOGGER.debug("Datos obtenidos. Generando tabla Proximo Control Acceso Subvenciones");
			ProximoOrganismo[] proximoOrganismos = extraerProximosOrganismos(datosYMedidas);
			Organismo[] organismos = this.extraerOrganismos(datosYMedidas);
			fragmentoControAccesoSubvencionesImportes = AseguradoManagerGeneradorHtml.fragmentoControAccesoSubvencionesImportes(organismos, proximoOrganismos);
			LOGGER.debug("Tabla Proximo Control Acceso Subvenciones generada");
		} catch (es.agroseguro.serviciosweb.contratacionscasegurado.AgrException e) {
			throw new Exception(pintarErroresAgrException(e));
		}
		LOGGER.debug("Componiendo tabla final con todos los datos");
		tablaSubvenciones.append(fragmentoControAccesoSubvencionesImportes).append(fragmentoModulacionEnesa).append(fragmentoSaldoReduccionEnesa);
		LOGGER.info("Tabla creada");
		return tablaSubvenciones.toString();
	}

	private String pintarErroresAgrException(
			es.agroseguro.serviciosweb.contratacionscasegurado.AgrException e) {
		LOGGER.debug("El SW de agroseguro ha devuelto errores");
		StringBuilder sb = new StringBuilder();
		List<es.agroseguro.serviciosweb.contratacionscasegurado.Error> errores = e.getFaultInfo().getError();
		for(es.agroseguro.serviciosweb.contratacionscasegurado.Error error : errores){
			// descartamos el error de no hay datos
			if (error.getCodigo()!=-1) {
				sb.append(error.getMensaje()).append(". ");
			}
		}
		LOGGER.debug(sb.toString());
		return sb.toString();
	}
	
	public void setAseguradoDao(final IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}
	
	public void setAseguradoSubvencionManager(
			AseguradoSubvencionManager aseguradoSubvencionManager) {
		this.aseguradoSubvencionManager = aseguradoSubvencionManager;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
	

	/*
	 * 
	 */
	public String getListAseguradosString(List<Asegurado> listAseguradosEntidades) {
		
		String listaIds="";
		for(int i=0;i<listAseguradosEntidades.size();i++){
			listaIds += listAseguradosEntidades.get(i).getId().toString()+":"+listAseguradosEntidades.get(i).getNifcif()+";";
		}
		return listaIds;
	}

}