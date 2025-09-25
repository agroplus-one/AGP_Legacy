package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.managers.impl.SubentidadMediadoraManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.admin.Entidad;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.admin.SubentidadMediadoraId;

public class SubentidadMediadoraController extends BaseMultiActionController {
	private static final Log logger = LogFactory.getLog(SubentidadMediadoraController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");

	private SubentidadMediadoraManager subentidadMediadoraManager;
	private EntidadManager entidadManager;

	private final static String VACIO = "";

	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response,
			SubentidadMediadora subentidadMediadoraBean) throws Exception {
		logger.debug("init - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<SubentidadMediadora> listSubentidadMediadora = null;

		try {
			logger.debug("se comprueban si existen los parametros que se envian desde la pantalla de incidencias");
			if (request.getParameter("entidadmediadora") != null) {
				subentidadMediadoraBean.getId().setCodentidad(new BigDecimal(request.getParameter("entidadmediadora")));
				if (request.getParameter("nomentidadmediadora") != null) {
					subentidadMediadoraBean.getEntidadMediadora()
							.setNomentidad(request.getParameter("nomentidadmediadora"));
				}
			}

			if (request.getParameter("subentidad") != null) {
				subentidadMediadoraBean.getId().setCodsubentidad(new BigDecimal(request.getParameter("subentidad")));
			}

			// Se incluye el filtro de busqueda en la sesion
			request.getSession().setAttribute("subentidadMediadoraBean", subentidadMediadoraBean);

			listSubentidadMediadora = subentidadMediadoraManager.listSubentidadesGrupoEntidad(subentidadMediadoraBean);
			logger.debug("listado Subentidades Mediadoras. Size: " + listSubentidadMediadora.size());

			if (request.getParameter("tipoFichero") != null && request.getParameter("idFichero") != null) {
				parametros.put("idFichero", request.getParameter("idFichero"));
				parametros.put("tipoFichero", request.getParameter("tipoFichero"));
			}
			parametros.put("listSubentidadMediadora", listSubentidadMediadora);
			if (request.getParameter("procedencia") != null) {
				parametros.put("procedencia", request.getParameter("procedencia"));
			}
			if (subentidadMediadoraBean.getIban() != null && !subentidadMediadoraBean.getIban().equals(""))
				parametros.put("iban", subentidadMediadoraBean.getIban());
			mv = new ModelAndView("moduloAdministracion/subentidadesMediadoras/subentidadesMediadoras",
					"subentidadMediadoraBean", subentidadMediadoraBean);

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error general: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.error.general"));
			mv = doConsulta(request, response, new SubentidadMediadora());
		}

		logger.debug("end - doConsulta");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doAlta(HttpServletRequest request, HttpServletResponse response,
			SubentidadMediadora subentidadMediadoraBean) throws Exception {
		logger.debug("init - doAlta");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Integer> errList = null;

		try {

			logger.debug("alta idsubentidadMediadora: " + subentidadMediadoraBean.getId().getCodentidad() + ","
					+ subentidadMediadoraBean.getId().getCodsubentidad());
			logger.debug("comprobamos que no existen registros duplicados");
			if (!subentidadMediadoraManager.existeRegistro(subentidadMediadoraBean)) {

				// Para grabar por defecto la propia entidad en caso de permitir cargo en cuenta
				if (new BigDecimal(1).compareTo(subentidadMediadoraBean.getCargoCuenta()) == 0) {
					Set<Entidad> colEntidadesCargoCuenta = new HashSet<Entidad>();
					Entidad entidad = entidadManager.getEntidad(subentidadMediadoraBean.getEntidad().getCodentidad());
					colEntidadesCargoCuenta.add(entidad);
					subentidadMediadoraBean.setEntidadesCargoCuenta(colEntidadesCargoCuenta);
				}

				errList = subentidadMediadoraManager.guardarSubentidadMediadora(subentidadMediadoraBean);
				logger.debug("errores de validacion: " + errList.size());

				parametros = this.gestionMensajes(errList, bundle.getString("mensaje.subentidadesMed.alta.OK"));

				parametros.put("activarModoModificar", true);

				mv = doConsulta(request, response, subentidadMediadoraBean);

			} else {
				parametros.put("alerta", bundle.getString("mensaje.subentidadesMed.alta.duplicados.KO"));
				mv = doConsulta(request, response, subentidadMediadoraBean);
			}

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error al guardar una subentidad mediadora: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.subentidadesMed.alta.KO"));
			mv = doConsulta(request, response, subentidadMediadoraBean);
		}

		logger.debug("end - doAlta");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doEdita(HttpServletRequest request, HttpServletResponse response,
			SubentidadMediadora subentidadMediadoraBean) throws Exception {
		logger.debug("init - doEdita");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		List<Integer> errList = null;
		try {
			logger.debug("modificacion idsubentidadMediadora: " + subentidadMediadoraBean.getId().getCodentidad() + ","
					+ subentidadMediadoraBean.getId().getCodsubentidad());

			SubentidadMediadora subEntMedOriginal = subentidadMediadoraManager
					.getSubentidadMediadora(subentidadMediadoraBean.getId());
			copiarBeanSubEntidadMediadoraEnObjeto(subEntMedOriginal, subentidadMediadoraBean);
			errList = subentidadMediadoraManager.guardarSubentidadMediadora(subEntMedOriginal);
			logger.debug("errores de validacion: " + errList.size());

			parametros = this.gestionMensajes(errList, bundle.getString("mensaje.subentidadesMed.modificacion.OK"));
			parametros.put("activarModoModificar", true);

			if (errList != null && errList.size() == 0) {
				// Cargar el nuevo
				mv = doConsulta(request, response, subEntMedOriginal);
			} else {
				// Devolverlo como estaba
				mv = doConsulta(request, response, subentidadMediadoraBean);
			}

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error al modificar una subentidad mediadora: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.subentidadesMed.modificacion.KO"));
			mv = doConsulta(request, response, subentidadMediadoraBean);
		}

		logger.debug("end - doEdita");
		return mv.addAllObjects(parametros);
	}

	/**
	 * Realiza varias verificaciones antes de la baja de la subentidad Comprueba si
	 * la entidad-Subentidad tiene usuarios asociados
	 * 
	 * @param request
	 * @param response
	 * @return 0 - NO tiene usuarios asociados; 1 - SI tiene usuarios asociados
	 */
	public void verificarSubentidad(HttpServletRequest request, HttpServletResponse response) {
		JSONObject objeto = new JSONObject();
		try {

			BigDecimal Entmed = new BigDecimal(StringUtils.nullToString(request.getParameter("entidad")));
			BigDecimal subEnt = new BigDecimal(StringUtils.nullToString(request.getParameter("subentidad")));
			// Datos recibidos
			logger.debug("entidad: " + Entmed + " Subentidad: " + subEnt);
			SubentidadMediadoraId id = new SubentidadMediadoraId();
			id.setCodentidad(Entmed);
			id.setCodsubentidad(subEnt);
			SubentidadMediadora subEntBean = subentidadMediadoraManager.getSubentidadMediadora(id);
			boolean tieneUsAsociados = subentidadMediadoraManager.checkUsuariosSubentidad(subEntBean);
			if (tieneUsAsociados)
				objeto.put("datos", "1");
			else
				objeto.put("datos", "0");
			getWriterJSON(response, objeto);
		} catch (JSONException e) {
			logger.error("Excepcion : SubentidadMediadoraController - verificarSubentidad", e);
		} catch (Exception e) {
			logger.debug(
					"SubentidadMediadoraController.verificarSubentidad - Ocurrió un error al verificar la baja de la Subentidad");
			logger.error("Ocurrió un error al verificar la baja de la Subentidad.", e);
			try {
				objeto.put("alert",
						"Ha ocurrido un error al verificar los usuarios asociados a la subEntidad Mediadora");
				getWriterJSON(response, objeto);
			} catch (JSONException e1) {
				logger.error("Excepcion : SubentidadMediadoraController - verificarSubentidad", e1);
			}
		}
	}

	public ModelAndView doBaja(HttpServletRequest request, HttpServletResponse response,
			SubentidadMediadora subentidadMediadoraBean) throws Exception {
		logger.debug("init - doBaja");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {

			logger.debug("Borramos idsubentidadMediadora: " + subentidadMediadoraBean.getId().getCodentidad() + ","
					+ subentidadMediadoraBean.getId().getCodsubentidad());
			subentidadMediadoraBean = subentidadMediadoraManager
					.getSubentidadMediadora(subentidadMediadoraBean.getId());

			// if(subentidadMediadoraManager.isBajaOk(subentidadMediadoraBean)){

			subentidadMediadoraManager.borrarSubentidadMediadora(subentidadMediadoraBean);
			// Se modifica los registros de la table de informes mediadores correspondientes
			// a la subentidad
			subentidadMediadoraManager.modificarInformeMediadores(subentidadMediadoraBean);

			parametros.put("mensaje", bundle.getString("mensaje.subentidadesMed.baja.OK"));

			// }else{

			// parametros.put("alerta",
			// bundle.getString("mensaje.subentidadesMed.baja.validacion.KO"));

			// }

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error al borrar una subentidad mediadora: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.subentidadesMed.baja.KO"));
		}

		// Se obtiene el bean que se enviarÃ¡ en la redirecciÃ³n
		SubentidadMediadora sm = request.getSession().getAttribute("subentidadMediadoraBean") != null
				? (SubentidadMediadora) request.getSession().getAttribute("subentidadMediadoraBean")
				: new SubentidadMediadora();

		mv = doConsulta(request, response, sm);

		logger.debug("end - doBaja");
		return mv.addAllObjects(parametros);
	}

	public ModelAndView doDeshacerBaja(HttpServletRequest request, HttpServletResponse response,
			SubentidadMediadora subentidadMediadoraBean) throws Exception {
		logger.debug("init - doDeshacerBaja");
		ModelAndView mv = null;
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {

			logger.debug("Deshacemos la subentidadMediadora: " + subentidadMediadoraBean.getId().getCodentidad() + ","
					+ subentidadMediadoraBean.getId().getCodsubentidad());
			subentidadMediadoraBean = subentidadMediadoraManager
					.getSubentidadMediadora(subentidadMediadoraBean.getId());

			subentidadMediadoraBean.setFechabaja(null);
			parametros.put("mensaje", bundle.getString("mensaje.deshacer.OK"));

		} catch (BusinessException be) {
			logger.debug("Se ha producido un error al deshacer la baja de la subentidad mediadora: " + be.getMessage());
			parametros.put("alerta", bundle.getString("mensaje.deshacer.KO"));
		}

		// Se obtiene el bean que se enviará en la redirección
		SubentidadMediadora sm = request.getSession().getAttribute("subentidadMediadoraBean") != null
				? (SubentidadMediadora) request.getSession().getAttribute("subentidadMediadoraBean")
				: new SubentidadMediadora();

		mv = doConsulta(request, response, sm);

		logger.debug("end - doDeshacerBaja");
		return mv.addAllObjects(parametros);
	}

	/**
	 * Devuelve la lista de entidades de cargo a cuenta de una subentidad mediadora
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doCargarListaEntidadesCargoCuenta(HttpServletRequest request, HttpServletResponse response) {

		logger.debug("init - doCargarListaEntidadesCargoCuenta");
		List<String> listaEntidadesCargoCuenta = new ArrayList<String>();
		String codEntidad = StringUtils.nullToString(request.getParameter("codEntidad"));
		String codEntidadMed = StringUtils.nullToString(request.getParameter("codEntidadMed"));
		String codSubentidad = StringUtils.nullToString(request.getParameter("codSubentidad"));

		JSONObject resultado = new JSONObject();

		try {
			if (!codEntidad.equals("") && !codEntidadMed.equals("") && !codSubentidad.equals("")) {
				SubentidadMediadora subentidadMediadora = subentidadMediadoraManager
						.obtenerSubentidadMediadora(codEntidadMed, codSubentidad);
				Set<Entidad> colEntidadesCargoCuenta = subentidadMediadora.getEntidadesCargoCuenta();
				Iterator<Entidad> it = colEntidadesCargoCuenta.iterator();

				while (it.hasNext()) {
					Entidad entidadAux = it.next();
					// Si coincide con la entidad de la subentidad, la ponemos al inicio de la lista
					String auxCodEntYDescripcion = entidadAux.getCodentidad() + "|" + entidadAux.getNomentidad();

					if (subentidadMediadora.getEntidad().getCodentidad().compareTo(entidadAux.getCodentidad()) == 0) {
						listaEntidadesCargoCuenta.add(0, auxCodEntYDescripcion);
					} else {
						listaEntidadesCargoCuenta.add(auxCodEntYDescripcion);
					}
				}

				resultado.put("listaEntidadesCargoCuenta", listaEntidadesCargoCuenta);
			}

		} catch (Exception be) {
			logger.error("Se ha producido un error al doCargarListaEntidadesCargoCuenta: " + be.getMessage());
		}
		getWriterJSON(response, resultado);

		return null;
	}

	public ModelAndView doGuardarListaEntidadesCargoCuenta(HttpServletRequest request, HttpServletResponse response) {

		logger.debug("init - doGuardarListaEntidadesCargoCuenta");
		String codEntidad = StringUtils.nullToString(request.getParameter("codEntidad"));
		String codEntidadMed = StringUtils.nullToString(request.getParameter("codEntidadMed"));
		String codSubentidad = StringUtils.nullToString(request.getParameter("codSubentidad"));
		String entidadesCargoCuenta = StringUtils.nullToString(request.getParameter("entidadesCargoCuenta"));

		JSONObject resultado = new JSONObject();

		try {
			if (!VACIO.equals(codEntidad) && !VACIO.equals(codEntidadMed) && !VACIO.equals(codSubentidad)
					&& !VACIO.equals(entidadesCargoCuenta)) {
				SubentidadMediadora subentidadMediadora = subentidadMediadoraManager
						.obtenerSubentidadMediadora(codEntidadMed, codSubentidad);

				Set<Entidad> colEntidadesCargoCuenta = new HashSet<Entidad>();
				String[] arrayEntidadesCargoCuenta = entidadesCargoCuenta.split("\\|");
				logger.debug("arrayEntidadesCargoCuenta.length = " + arrayEntidadesCargoCuenta.length);

				List<BigDecimal> listaCodEntidadCargoCuenta = new ArrayList<BigDecimal>();

				for (int i = 0; i < arrayEntidadesCargoCuenta.length; i++) {
					String codEntidadAux = arrayEntidadesCargoCuenta[i];
					listaCodEntidadCargoCuenta.add(new BigDecimal(codEntidadAux));
				}

				List<Entidad> listaEntidades = entidadManager
						.obtenerListaEntidadesByArrayCodEntidad(listaCodEntidadCargoCuenta);
				logger.debug("listaEntidades.size() = " + listaEntidades.size());
				colEntidadesCargoCuenta.addAll(listaEntidades);
				subentidadMediadora.setEntidadesCargoCuenta(colEntidadesCargoCuenta);
				subentidadMediadoraManager.guardarSubentidadMediadora(subentidadMediadora);
			}

		} catch (Exception be) {
			logger.error("Se ha producido un error al doGuardarListaEntidadesCargoCuenta: " + be.getMessage());
		}
		getWriterJSON(response, resultado);

		return null;
	}

	/**
	 * Metodo que genera los mensajes de errores de validacion que se mostrarÃ¡n en
	 * la jsp o el mensaje de todo correcto
	 * 
	 * @param errList
	 * @param msjOK
	 * @return
	 */
	private Map<String, Object> gestionMensajes(List<Integer> errList, String msjOK) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<String> erroresWeb = new ArrayList<String>();

		// MENSAJES DE ERRORES DE VALIDACION
		if (errList.size() > 0) {
			for (Integer error : errList) {
				switch (error) {
				case 1:
					erroresWeb.add(bundle.getString("mensaje.subentidadesMed.validacion.obligatorio"));
					break;
				case 2:
					erroresWeb.add(bundle.getString("mensaje.subentidadesMed.validacion.ent"));
					break;
				case 3:
					erroresWeb.add(bundle.getString("mensaje.subentidadesMed.validacion.entMed"));
					break;
				case 4:
					erroresWeb.add(bundle.getString("mensaje.subentidadesMed.validacion.ultimosDigitos"));
					break;
				default:
					break;
				}
			}
			parametros.put("alerta2", erroresWeb);
		} else {
			parametros.put("mensaje", msjOK);
		}

		return parametros;
	}

	/**
	 * Copia
	 * 
	 * @param subEntMed
	 * @param subEntMedBean
	 */
	private void copiarBeanSubEntidadMediadoraEnObjeto(SubentidadMediadora subEntMed,
			SubentidadMediadora subEntMedBean) {

		subEntMed.setTipoidentificacion(subEntMedBean.getTipoidentificacion());
		subEntMed.setNifcif(subEntMedBean.getNifcif());

		if ("CIF".equalsIgnoreCase(subEntMedBean.getTipoidentificacion())) {
			subEntMed.setNombre(null);
			subEntMed.setApellido1(null);
			subEntMed.setApellido2(null);
			subEntMed.setNomsubentidad(StringUtils.normalizaEspacios(subEntMedBean.getNomsubentidad()));
			subEntMed.setNomSubentidadCompleto(subEntMed.getNomsubentidad());
		} else {
			subEntMed.setNombre(StringUtils.normalizaEspacios(subEntMedBean.getNombre()));
			subEntMed.setApellido1(StringUtils.normalizaEspacios(subEntMedBean.getApellido1()));
			subEntMed.setApellido2(StringUtils.normalizaEspacios(subEntMedBean.getApellido2()));
			subEntMed.setNomsubentidad(null);

			String nombreCompleto = StringUtils.normalizaEspacios(subEntMedBean.getNombre() + " "
					+ subEntMedBean.getApellido1() + " " + subEntMedBean.getApellido2());
			subEntMed.setNomSubentidadCompleto(nombreCompleto);
		}

		subEntMed.setCodpostal(subEntMedBean.getCodpostal());

		subEntMed.setPagodirecto(subEntMedBean.getPagodirecto());

		BigDecimal cargoEnCuentaNuevo = subEntMedBean.getCargoCuenta();
		BigDecimal uno = new BigDecimal(1);
		Set<Entidad> colEntidadesCargoCuenta = new HashSet<Entidad>();

		// Si pasa de no tener cargo en cuenta a tenerlo
		if (uno.compareTo(subEntMed.getCargoCuenta()) != 0 && uno.compareTo(cargoEnCuentaNuevo) == 0) {
			// Añadirle el de por defecto
			colEntidadesCargoCuenta.add(subEntMed.getEntidad());
			subEntMed.setEntidadesCargoCuenta(colEntidadesCargoCuenta);
			logger.debug("Añadida entidad por defecto para cargo cuenta");

			// Si pasa de tener cargo en cuenta a no tenerlo
		} else if (uno.compareTo(subEntMed.getCargoCuenta()) == 0 && uno.compareTo(cargoEnCuentaNuevo) != 0) {
			// Vaciar el Set
			subEntMed.setEntidadesCargoCuenta(colEntidadesCargoCuenta);
			logger.debug("Borradas entidades por defecto para cargo cuenta");

		} else {
			logger.debug("Sin variación en entidades cargo cuenta");
		}
		subEntMed.setCargoCuenta(cargoEnCuentaNuevo);
		subEntMed.setFechabaja(subEntMedBean.getFechabaja());
		// if (subEntMedBean.getIban() != null && !subEntMedBean.getIban().equals(""))
		subEntMed.setIban(subEntMedBean.getIban());
		subEntMed.setForzarRevisionAM(subEntMedBean.getForzarRevisionAM());
		subEntMed.setCalcularRcGanado(subEntMedBean.getCalcularRcGanado());
		subEntMed.setSwConfirmacion(subEntMedBean.getSwConfirmacion());
		subEntMed.setIndGastosAdq(subEntMedBean.getIndGastosAdq());
		subEntMed.setEmail(subEntMedBean.getEmail());
		subEntMed.setEmail2(subEntMedBean.getEmail2());
		subEntMed.setFirmaTableta(subEntMedBean.getFirmaTableta());
	}

	/**
	 * Se registra un editor para hacer el bind de las propiedades tipo Date que
	 * vengan de la jsp. En MultiActionController no se hace este bind
	 * automaticamente
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		// True indica que se aceptan fechas vacias
		CustomDateEditor editor = new CustomDateEditor(df, true);
		binder.registerCustomEditor(Date.class, editor);
	}

	public void setSubentidadMediadoraManager(SubentidadMediadoraManager subentidadMediadoraManager) {
		this.subentidadMediadoraManager = subentidadMediadoraManager;
	}

	public void setEntidadManager(EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}

}