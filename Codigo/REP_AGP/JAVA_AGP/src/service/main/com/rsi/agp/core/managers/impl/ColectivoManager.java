package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.admin.impl.Colectivo2Filtro;
import com.rsi.agp.dao.filters.admin.impl.ColectivoFiltro;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.poliza.SeleccionPolizaFiltro;
import com.rsi.agp.dao.models.admin.IColectivoDao;
import com.rsi.agp.dao.models.admin.ISubentidadMediadoraDao;
import com.rsi.agp.dao.models.admin.IUsuarioDao;
import com.rsi.agp.dao.models.ref.IReferenciaColectivoDao;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SwEnvioColectivo;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.admin.TomadorId;
import com.rsi.agp.dao.tables.commons.Oficina;
import com.rsi.agp.dao.tables.commons.OficinaId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Descuento;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.pagination.PageProperties;
import com.rsi.agp.pagination.PaginatedListImpl;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public class ColectivoManager implements IManager {

	protected IColectivoDao colectivoDao;
	protected IUsuarioDao usuarioAdminDao;
	private HistoricoColectivosManager historicoColectivosManager;
	private IReferenciaColectivoDao referenciaColectivoDao;
	private CargaAseguradoManager cargaAseguradoManager;
	private ISubentidadMediadoraDao subentidadMediadoraDao;
	private static final Log LOGGER = LogFactory.getLog(ColectivoManager.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");
	final ResourceBundle bundle_agp = ResourceBundle.getBundle("agp");

	private static final String BAJA_HISTORICO_COLECTIVO = "B";

	@SuppressWarnings("unchecked")
	public final List<Colectivo> getColectivos(final Colectivo colectivoBean) {
		final ColectivoFiltro filter = new ColectivoFiltro(colectivoBean);
		return colectivoDao.getObjects(filter);
	}

	public final List<Colectivo> getColectivosGrupoEntidad(final Colectivo colectivoBean, List<BigDecimal> entidades,
			boolean addFiltroBaja, List<BigDecimal> planesFiltroInicial) {
		try {
			return colectivoDao.getColectivosGrupoEntidad(colectivoBean, entidades, addFiltroBaja, planesFiltroInicial);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al recuperar los colectivos de un grupo de entidad", dao);
		}
		return null;
	}

	/**
	 * Realiza la baja del colectivo indicado en parametro 'colectivoBean'. Sera una
	 * baja logica si el colectivo tiene polizas asociadas y fisica en caso
	 * contrario
	 * 
	 * @param parameters
	 * @param colectivoBean
	 * @param colectivoBusqueda
	 * @param usuario
	 * @return
	 */
	public Map<String, Colectivo> baja(Map<String, Object> parameters, Colectivo colectivoBean,
			Colectivo colectivoBusqueda, Usuario usuario) {

		/*
		 * DAA 18/04/12 Baja logica de un colectivo
		 */
		final List<Poliza> listaPolizasAsociadas = this.getPolizasByIdColectivo(colectivoBean.getId());

		// MPM - 10-05-12
		// Si tiene polizas asociadas se realiza la baja logica
		if (listaPolizasAsociadas != null && !listaPolizasAsociadas.isEmpty()) {
			LOGGER.debug("ColectivoManager.baja - El colectivo tiene polizas asociadas. Se hara una baja logica");
			try {

				colectivoBean = this.getColectivo(colectivoBean.getId());
				this.bajaLogicaColectivo(colectivoBean, usuario);
				historicoColectivosManager.saveHistoricoColectivo(colectivoBean, usuario, BAJA_HISTORICO_COLECTIVO,
						null, false);
				parameters.put("mensaje", bundle_agp.getString("mensaje.baja.OK"));
				LOGGER.debug("ColectivoManager.baja - Baja logica realizada correctamente.");
			} catch (Exception e) {
				parameters.put("alerta", bundle_agp.getString("mensaje.baja.KO"));
			}
		}
		// No tiene polizas asociadas, se realiza la baja fisica
		else {
			LOGGER.debug("ColectivoManager.baja - El colectivo no tiene polizas asociadas. Se hara una baja fisica");
			try {
				// Actualiza los usuarios asociados a este colectivo
				desasociarColectivo(colectivoBean);
				// Borra el colectivo
				bajaFisicaColectivo(colectivoBean, usuario);
				// Se pasa a la jsp el mensaje de borrado correcto
				parameters.put("mensaje", bundle_agp.getString("mensaje.baja.OK"));
				LOGGER.debug("ColectivoManager.baja - Baja fisica realizada correctamente.");
			} catch (Exception e) {
				// Se pasa a la jsp el mensaje de borrado correcto
				parameters.put("alerta", bundle_agp.getString("mensaje.baja.KO"));
			}
		}

		colectivoBean = new Colectivo();
		colectivoBusqueda = new Colectivo();

		// se declara un mapa para poder devolver los dos colectivos
		Map<String, Colectivo> mapaColectivosBaja = new HashMap<String, Colectivo>();
		mapaColectivosBaja.put("colectivoBean", colectivoBean);
		mapaColectivosBaja.put("colectivoBusqueda", colectivoBusqueda);

		return mapaColectivosBaja;
	}

	/* -------------------------------------------------------- */
	/* DISPLAYTAG PAGINATION */
	/* -------------------------------------------------------- */
	public PaginatedListImpl<Colectivo> getPaginatedListColectivos(Colectivo colectivoBean,
			List<BigDecimal> listaCodEntidadesGrupo, int numPageRequest, String sort, String dir, String perfil,
			boolean addFiltroBaja, List<BigDecimal> planesFiltroInicial) {

		PaginatedListImpl<Colectivo> paginatedListImpl = null;
		int pageSize = (int) Long.parseLong(bundle.getString("numElementsPag"));

		try {

			Colectivo2Filtro colectivo2Filtro = generateColectivo2Filtro(colectivoBean, listaCodEntidadesGrupo, perfil,
					addFiltroBaja, planesFiltroInicial);

			int fullListSize = colectivoDao.getNumObjects(colectivo2Filtro);

			PageProperties pageProperties = new PageProperties();
			pageProperties.setFullListSize(fullListSize);
			pageProperties.setIndexRowMax((numPageRequest - 1) * pageSize + pageSize - 1);
			pageProperties.setIndexRowMin((numPageRequest - 1) * pageSize);
			pageProperties.setPageNumber(numPageRequest);
			pageProperties.setPageSize(pageSize);
			pageProperties.setSort(sort);
			pageProperties.setDir(dir);

			paginatedListImpl = colectivoDao.getPaginatedListColectivosGrupoEntidad(colectivoBean, pageProperties,
					colectivo2Filtro);

		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al recuperar la lista de colectivos ", dao);
		}

		return paginatedListImpl;
	}

	private Colectivo2Filtro generateColectivo2Filtro(Colectivo colectivoBean, List<BigDecimal> listaCodEntidadesGrupo,
			String perfil, boolean addFiltroBaja, List<BigDecimal> planesFiltroInicial) {

		Colectivo2Filtro colectivo2Filter = null;

		try {
			Map<String, Object> mapaColectivo = colectivoDao.getMapaColectivo(colectivoBean);

			colectivo2Filter = new Colectivo2Filtro();
			colectivo2Filter.setColectivoBean(colectivoBean);
			colectivo2Filter.setListaEnt(listaCodEntidadesGrupo);
			colectivo2Filter.setMapa(mapaColectivo);
			colectivo2Filter.setAddFiltroFechaBaja(addFiltroBaja);
			colectivo2Filter.setPlanesFiltroInicial(planesFiltroInicial);
			// DAA 22/05/2013
			colectivo2Filter.setPerfilUsuario(perfil);

		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error durante generacion del filtro de colectivo ", dao);
		}

		return colectivo2Filter;
	}

	/* -------------- fin displaytag pagination ----------------- */
	public final Colectivo getColectivo(final Long id) {
		return (Colectivo) colectivoDao.getObject(Colectivo.class, id);
	}

	/**
	 * TMR 30-05-2012.Facturacion. Metodo que al carga un colectivo factura
	 */
	public final Colectivo getColectivoFacturacion(final Long id, Usuario usuario, String tipo) {
		Colectivo col = (Colectivo) colectivoDao.getObject(Colectivo.class, id);
		try {
			colectivoDao.callFacturacion(usuario, tipo);
		} catch (Exception e) {
			LOGGER.error("Ha ocurrido un error al facturar la carga de colectivos");

		}
		return col;
	}

	@SuppressWarnings("unchecked")
	public final Colectivo getColectivo(final BigDecimal codentidad, final String ciftomador,
			final Long lineaseguroid) {
		Colectivo resultado = null;
		final ColectivoFiltro filter = new ColectivoFiltro(codentidad, ciftomador, lineaseguroid);
		final List<Colectivo> lista = colectivoDao.getObjects(filter);
		if (null != lista && !lista.isEmpty()) {
			resultado = lista.get(0);
		}
		return resultado;
	}

	public final boolean existeColectivo(final BigDecimal codentidad, final String ciftomador, final Long lineaseguroid,
			String idcolectivo) {
		Colectivo colectivo = new Colectivo();
		colectivo.setIdcolectivo(idcolectivo);

		final ColectivoFiltro filter = new ColectivoFiltro(codentidad, ciftomador, lineaseguroid, colectivo);

		return !colectivoDao.getObjects(filter).isEmpty();

	}

	/**
	 * 
	 * @param colectivoBean
	 * @param usuario
	 * @param altaModif:
	 *            0 -> Alta, 1-> Modificacion
	 * @return
	 */
	public final ArrayList<Integer> saveColectivo(final Colectivo colectivoBean, Usuario usuario, int altaModif) {
		ArrayList<Integer> error = null;
		try {
			error = comprobarDatosGrabacion(colectivoBean, usuario, altaModif);
			if (error.size() == 0) {
				LineasFiltro filtroLinea = new LineasFiltro(colectivoBean.getLinea().getCodplan(),
						colectivoBean.getLinea().getCodlinea());
				@SuppressWarnings("unchecked")
				List<Linea> lineas = colectivoDao.getObjects(filtroLinea);
				colectivoBean.setLinea((Linea) lineas.get(0));
				// TMR 29-05-2012 facturacion
				colectivoDao.saveOrUpdateFacturacion(colectivoBean, usuario);
				error.add(new Integer(0));
			}
		} catch (Exception e) {
			if (error != null)
				error.add(new Integer(9));
			LOGGER.error("Se ha producido un error durante el guardado del colectivo ", e);
		}
		return error;
	}

	/**
	 * Realiza la baja logica del colectivo pasado por parametro
	 * 
	 * @param colectivoBean
	 * @throws BusinessException
	 * @throws Exception
	 */
	public final void bajaLogicaColectivo(final Colectivo colectivoBean, Usuario usuario)
			throws BusinessException, Exception {
		/*
		 * DAA 18/04/12 Actualizamos el registro de fecha de baja y en caso contrario
		 * lanzamos excepciones
		 */
		try {
			colectivoBean.setFechabaja(new Date());
			colectivoBean.setFechacambio(new Date());
			colectivoBean.setFechaefecto(new Date());
			// TMR 30-05-2012 Facturacion.Cuando damos de baja un colectivo facturamos
			colectivoDao.saveOrUpdateFacturacion(colectivoBean, usuario);
		} catch (DAOException e) {
			LOGGER.error("Error de insercion en base de datos", e);
			throw new BusinessException("Error de insercion en base de datos", e);

		} catch (Exception e) {
			LOGGER.error("Error inesperado", e);
			throw new Exception("Error inesperado", e);
		}
	}

	/**
	 * MPM - 10/05-12 Realiza la baja fisica del colectivo pasado por parametro
	 * 
	 * @param colectivoBean
	 * @throws BusinessException
	 * @throws Exception
	 */
	public final void bajaFisicaColectivo(final Colectivo colectivoBean, Usuario usuario) throws Exception {
		try {
			// TMR 30-05-2012.Facturacion.cuando damos de baja un colectivo facturamos
			colectivoDao.removeObjectFacturacion(Colectivo.class, colectivoBean.getId(), usuario);
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error al borrar el colectivo", e);
			throw new Exception("Ocurrio un error al borrar el colectivo", e);
		}
	}

	/**
	 * Borra el colectivo indicado de todos los usuarios que lo tengan asociado
	 * 
	 * @param colectivoBean
	 * @throws Exception
	 */
	public final void desasociarColectivo(final Colectivo colectivoBean) throws Exception {
		try {
			usuarioAdminDao.desasociarColectivo(colectivoBean.getId());
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error al desasociar el colectivo", e);
			throw new Exception("Ocurrio un error al desasociar el colectivo", e);
		}
	}

	private ArrayList<Integer> comprobarDatosGrabacion(final Colectivo colectivoBean, Usuario usuario, int altaModif) {
		ArrayList<Integer> error = new ArrayList<Integer>();

		try {
			// Comprobamos si existe la referencia
			/*
			 * if ((colectivoBean.getIdcolectivo() != null) && (colectivoBean.getDc() !=
			 * null)){ List<Colectivo> listColectivos =
			 * this.colectivoDao.getColectivos(colectivoBean.getId(),
			 * colectivoBean.getIdcolectivo(), colectivoBean.getDc()); if
			 * (listColectivos.size() > 0){ error.add(new Integer(1)); } }
			 */

			// Comprobamos la entidad
			if (!usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR)
					&& !usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)) {
				if (usuario.getOficina().getEntidad().getCodentidad().intValue() != colectivoBean.getTomador().getId()
						.getCodentidad().intValue()) {
					error.add(new Integer(2));
				}
			}
			// comprobamos que la subentidad exista y que no este dada de baja
			Integer count;
			count = subentidadMediadoraDao.existeRegistro(colectivoBean.getSubentidadMediadora(), true,
					colectivoBean.getTomador().getId().getCodentidad());

			if (count == 0) {
				error.add(new Integer(13));
			}

			// Validamos el plan

			Linea lin = colectivoDao.getLineaColectivo(colectivoBean.getLinea().getCodlinea(),
					colectivoBean.getLinea().getCodplan());
			// El plan-linea no existiria
			if (null == lin) {
				error.add(new Integer(4));
			} else {
				// Comprobamos si el plan linea esta activo
				// Solo se comprueba si estamos dando de alta
				if (altaModif == 0) {
					if (lin.getActivo().equalsIgnoreCase("NO")) {
						error.add(new Integer(5));
					}
				}
			}

			// Comprobamos que exista el tomador
			Tomador tomadorAux = this.getTomadorByCif(colectivoBean.getTomador().getId().getCodentidad(),
					colectivoBean.getTomador().getId().getCiftomador());
			if (null == tomadorAux) {
				error.add(new Integer(6));
			}

			// comprobamos datos CCC
			error = comprobarDatosCCC(colectivoBean, error);

			if (error.size() == 0) {
				if (altaModif == 0) // Asignar referencia y dc al colectivo, solo en el alta
					asignarReferenciaColectivo(colectivoBean, error);
			}

			// Comprobamos el digito de control
			if (!StringUtils.nullToString(colectivoBean.getIdcolectivo()).equals("") && !colectivoBean.getDc()
					.equals(StringUtils.getDigitoControl(Integer.parseInt(colectivoBean.getIdcolectivo())))) {
				error.add(new Integer(7));
			}
		} catch (DAOException e) {
			LOGGER.error("Error al acceder a bbdd en  comprobarDatosGrabacion () - ColectivoManager", e);
			error.add(new Integer(20));
		} catch (Exception ex) {
			LOGGER.error("Error genérico al comprobarDatosGrabacion () - ColectivoManager", ex);
			error.add(new Integer(20));
		}

		return error;
	}

	private ArrayList<Integer> comprobarDatosCCC(Colectivo col, ArrayList<Integer> error) {

		if (null != col.getCccEntidad() && !"".equals(col.getCccEntidad()) && null != col.getCccOficina()
				&& !"".equals(col.getCccOficina())) {
			// Comprobamos que exista el banco
			Entidad compruebaBanco = (Entidad) colectivoDao.getObject(Entidad.class,
					new BigDecimal(col.getCccEntidad()));
			if (null == compruebaBanco) {
				error.add(new Integer(10));
			} else {
				// Comprobamos que exista la relacion oficina-banco
				OficinaId idOficina = new OficinaId();
				idOficina.setCodentidad(new BigDecimal(col.getCccEntidad()));
				idOficina.setCodoficina(new BigDecimal(col.getCccOficina()));
				Oficina compruebaOficina = (Oficina) colectivoDao.getObject(Oficina.class, idOficina);
				if (null == compruebaOficina) {
					error.add(new Integer(11));
				}
				// MPM - 30-10-2012
				// Se comprueba que el DC es correcto
				else {
					if (!validarDC(col.getCccEntidad() + col.getCccOficina() + col.getCccDc() + col.getCccCuenta())) {
						error.add(new Integer(12));
					}
				}
			}
		}
		return error;
	}

	/**
	 * Llama al metodo de utilidades que valida el DC de la cuenta, si devuelve
	 * false o una excepcion se toma como no valido
	 * 
	 * @param cuenta
	 * @return
	 */
	private boolean validarDC(String cuenta) {
		boolean isValido = false;

		try {
			isValido = StringUtils.cuentaValida(cuenta);
		} catch (Exception exc) {
			LOGGER.debug("ColectivoManager.validarDC - Ocurrio un error al validar el DC. Se toma como no valido", exc);
		}

		return isValido;
	}

	/**
	 * Asigna una referencia al colectivo que se pasa por parametro
	 * 
	 * @param p
	 * @param errores
	 */
	private void asignarReferenciaColectivo(Colectivo col, ArrayList<Integer> error) {

		LOGGER.info("asignarReferenciaColectivo - Asignar referencia al Colectivo");
		ColectivoReferencia ra = null;

		try {
			// Obtiene el siguiente referencia Colectivo disponible
			ra = referenciaColectivoDao.getSiguienteReferenciaColectivo();
			LOGGER.info("asignarReferenciaPoliza - Referencia obtenida: " + ra.getReferencia() + " - " + ra.getDc());

			// Se asigna la referencia al colectivo
			col.setIdcolectivo(ra.getReferencia());
			col.setDc(ra.getDc());

		} catch (DAOException e) {
			// Indica que ha habido un error que para el proceso obtener la seiguiente
			// referencia de Colectivo
			LOGGER.info("asignarReferenciaPoliza - error al obtener una nueva referencia");
			error.add(new Integer(9));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public final List<Poliza> getPolizasByIdColectivo(final Long id) {
		final Poliza poliza = new Poliza();
		poliza.getColectivo().setId(id);
		SeleccionPolizaFiltro filter = new SeleccionPolizaFiltro(poliza);
		return colectivoDao.getObjects(filter);
	}

	@SuppressWarnings("unchecked")
	public final BigDecimal getPctDtoCol(final Long lineaSeguroId) {
		BigDecimal resultado = null;
		final List<BigDecimal> lista = colectivoDao.getObjects(new Filter() {
			@Override
			public Criteria getCriteria(final Session sesion) {
				Criteria criteria = sesion.createCriteria(Descuento.class);
				criteria.add(Restrictions.eq("id.lineaseguroid", lineaSeguroId));
				criteria.setProjection(Projections.property("pctdtocontrcolect"));
				criteria.setMaxResults(Integer.parseInt("1"));
				return criteria;
			}
		});
		if (null != lista && !lista.isEmpty()) {
			resultado = lista.get(0);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public final Long getLineaseguroIdByCodLinea(final BigDecimal codlinea) {
		Long resultado = null;
		final List<Long> lista = colectivoDao.getObjects(new Filter() {
			@Override
			public Criteria getCriteria(final Session sesion) {
				Criteria criteria = sesion.createCriteria(Linea.class);
				criteria.add(Restrictions.eq("codlinea", codlinea));
				criteria.setProjection(Projections.property("lineaseguroid"));
				criteria.setMaxResults(Integer.parseInt("1"));
				return criteria;
			}
		});
		if (null != lista && !lista.isEmpty()) {
			resultado = lista.get(0);
		}
		return resultado;
	}

	public final Long getLineaseguroId(final BigDecimal codplan, final BigDecimal codlinea) {
		LineasFiltro filtroLinea = new LineasFiltro(codplan, codlinea);

		@SuppressWarnings("unchecked")
		List<Linea> lineas = colectivoDao.getObjects(filtroLinea);

		if (!lineas.isEmpty())
			return lineas.get(0).getLineaseguroid();

		return null;
	}

	public void guardarUsuario(Usuario usuario) {
		try {
			colectivoDao.saveOrUpdate(usuario);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar el usuario", e);
		}
	}

	public final Tomador getTomadorByCif(final BigDecimal codentidad, final String ciftomador) {
		TomadorId tomadorId = new TomadorId(codentidad, ciftomador);
		return (Tomador) colectivoDao.getObject(Tomador.class, tomadorId);
	}

	public boolean existeOficina(Oficina oficina) {
		try {
			return colectivoDao.existeOficina(oficina);

		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al comprobar la existencia de la oficina", e);
		}
		return false;
	}

	public void newOficina(Oficina oficina) {
		try {
			oficina.setNomoficina(
					"Oficina " + oficina.getId().getCodentidad().toString() + "-" + oficina.getId().getCodoficina());
			colectivoDao.saveOrUpdate(oficina);

		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al dar de alta una oficina", e);
		}

	}

	public Colectivo cargar(HttpServletRequest request, Map<String, Object> parameters, ResourceBundle bundle,
			Long id) {
		// obtenemos el usuario
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		// DESBLOQUEAMOS LAS POLIZAS DEL USUARIO AL CAMBIAR DE COLECTIVO
		cargaAseguradoManager.DesbloquearPolizasUsuario(usuario.getCodusuario());

		// TMR 30-05-2012 Facturacion. Le pasamos el ususario para facturar la carga de
		// colectivo
		Colectivo colectivo = getColectivoFacturacion(id, usuario, Constants.FACTURA_CONSULTA);

		if (null == colectivo) {
			parameters.put("alerta", bundle.getString("mensaje.colectivo.cargado.KO"));
		} else {

			// asociamos el coletivo al usuario
			usuario.setColectivo(colectivo);
			// quitamos el asegurado y la clase del usuario
			usuario.setAsegurado(null);
			usuario.setClase(null);
			Oficina oficina = usuario.getOficina();
			// Si no existe la oficina del usuario, la damos de alta
			if (!existeOficina(oficina)) {
				newOficina(oficina);
			}
			// guardamos el usuario con el colectivo cargado
			guardarUsuario(usuario);
			// Actualizo el usuario de sesion
			request.getSession().setAttribute("usuario", usuario);

			parameters.put("mensaje", bundle.getString("mensaje.colectivo.cargado.OK"));
		}

		return colectivo;
	}

	public Colectivo activarColectivo(Long id) throws BusinessException {
		try {
			return colectivoDao.activarColectivo(id);

		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al activar el colectivo", e);
			throw new BusinessException();
		}

	}

	public List<BigDecimal> getPlanesFiltroInicial() throws BusinessException {
		List<BigDecimal> planes = new ArrayList<BigDecimal>();
		try {
			ArrayList<BigDecimal> listPlanesAux = colectivoDao.getPlanesFiltroInicial();
			// solo nos interesan para el filtro los dos ultimos planes
			for (int i = 0; i < listPlanesAux.size(); i++) {
				planes.add(listPlanesAux.get(i));
				if (i == 1)
					i = listPlanesAux.size();
			}

			return planes;

		} catch (Exception e) {
			LOGGER.error("Se ha producido un error al recuperar los planes del filtro inicial", e);
			throw new BusinessException();
		}
	}

	public void copiabean(Colectivo colectivoBusqueda, Colectivo colectivoBean) {

		colectivoBusqueda.setActivo(colectivoBean.getActivo());
		colectivoBusqueda.setCccCuenta(colectivoBean.getCccCuenta());
		colectivoBusqueda.setCccDc(colectivoBean.getCccDc());
		colectivoBusqueda.setCccEntidad(colectivoBean.getCccEntidad());
		colectivoBusqueda.setCccOficina(colectivoBean.getCccOficina());
		colectivoBusqueda.setDc(colectivoBean.getDc());
		colectivoBusqueda.setFechabaja(colectivoBean.getFechabaja());
		colectivoBusqueda.setFechacambio(colectivoBean.getFechacambio());
		colectivoBusqueda.setFechaefecto(colectivoBean.getFechaefecto());
		colectivoBusqueda.setFechaprimerpago(colectivoBean.getFechaprimerpago());
		colectivoBusqueda.setFechasegundopago(colectivoBean.getFechasegundopago());
		colectivoBusqueda.setIban(colectivoBean.getIban());
		colectivoBusqueda.setIdcolectivo(colectivoBean.getIdcolectivo());
		colectivoBusqueda.setIsCRM(colectivoBean.getIsCRM());
		colectivoBusqueda.setLinea(colectivoBean.getLinea());
		colectivoBusqueda.setNomcolectivo(colectivoBean.getNomcolectivo());
		colectivoBusqueda.setObservaciones(colectivoBean.getObservaciones());
		colectivoBusqueda.setpctDescRecarg(colectivoBean.getpctDescRecarg());
		colectivoBusqueda.setPctdescuentocol(colectivoBean.getPctdescuentocol());
		colectivoBusqueda.setPctprimerpago(colectivoBean.getPctprimerpago());
		colectivoBusqueda.setPctsegundopago(colectivoBean.getPctsegundopago());
		colectivoBusqueda.setSubentidadMediadora(colectivoBean.getSubentidadMediadora());
		colectivoBusqueda.settipoDescRecarg(colectivoBean.gettipoDescRecarg());
		colectivoBusqueda.setTomador(colectivoBean.getTomador());
		/* Pet. 5385 ** MODIF TAM (13.11.2018) ** Inicio */
		colectivoBusqueda.setEnvioIbanAgro(colectivoBean.getEnvioIbanAgro());

	}

	public void setColectivoDao(IColectivoDao colectivoDao) {
		this.colectivoDao = colectivoDao;
	}

	public void setUsuarioAdminDao(IUsuarioDao usuarioAdminDao) {
		this.usuarioAdminDao = usuarioAdminDao;
	}

	public HistoricoColectivosManager getHistoricoColectivosManager() {
		return historicoColectivosManager;
	}

	public void setHistoricoColectivosManager(HistoricoColectivosManager historicoColectivosManager) {
		this.historicoColectivosManager = historicoColectivosManager;
	}

	public void setReferenciaColectivoDao(IReferenciaColectivoDao referenciaColectivoDao) {
		this.referenciaColectivoDao = referenciaColectivoDao;
	}

	public void setCargaAseguradoManager(CargaAseguradoManager cargaAseguradoManager) {
		this.cargaAseguradoManager = cargaAseguradoManager;
	}

	public void setSubentidadMediadoraDao(ISubentidadMediadoraDao subentidadMediadoraDao) {
		this.subentidadMediadoraDao = subentidadMediadoraDao;
	}

	/**
	 * 
	 * @param realPath
	 * @param colectivo
	 * @return
	 * @throws BusinessException 
	 */
	public AcuseRecibo registrarColectivo(final String realPath, final Colectivo colectivo, final Usuario usuario)
			throws BusinessException {
		LOGGER.debug("ColectivoManager - doRegistrarColectivo - init");
		AcuseRecibo resultado = null;
		try {
			if (colectivo != null) {
				String xmlEnvio = XmlTransformerUtil.generateXMLColectivo(colectivo);
				resultado = RegistrarColectivoHelper.registrarColectivo(realPath, xmlEnvio);
				colectivo.setFechaEnvio(new Date());
				colectivo.setEstadoAgroseguro(resultado == null ? Constants.COLECTIVO_AGRO_KO : resultado.getDocumentoArray(0).getEstado());
				this.colectivoDao.saveOrUpdate(colectivo);
				this.historicoColectivosManager.saveHistoricoColectivo(colectivo, usuario, "M", new Date(), false);
				SwEnvioColectivo swEnvioColectivo = new SwEnvioColectivo();
				swEnvioColectivo.setIdColectivo(colectivo.getId());
				swEnvioColectivo.setFecha(new Date());
				swEnvioColectivo.setCodUsuario(usuario.getCodusuario());
				swEnvioColectivo.setXmlEnvio(Hibernate.createClob(xmlEnvio));
				swEnvioColectivo.setXmlRespuesta(Hibernate.createClob(StringUtils.nullToString(resultado == null ? "Error en SW" :resultado.toString())));
				this.colectivoDao.saveOrUpdate(swEnvioColectivo);
			}
			LOGGER.debug("ColectivoManager - doRegistrarColectivo - end");
		} catch (Exception e) {
			LOGGER.error(e);
			throw new BusinessException(e);
		}
		return resultado;
	}
}