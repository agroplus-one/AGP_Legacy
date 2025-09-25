package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.CargaPolizaActualizadaDelCuponException;
import com.rsi.agp.core.exception.CargaPolizaFromCopyOrPolizaException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.models.admin.AseguradoDao;
import com.rsi.agp.dao.models.admin.IAseguradoDao;
import com.rsi.agp.dao.models.anexo.ICuponDao;
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IEstadoCuponDao;
import com.rsi.agp.dao.models.anexo.ISubvDeclaradaDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.ILineaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.Cobertura;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.anexo.SubvDeclarada;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.iTipos.Gastos;

public class DeclaracionesModificacionPolizaManager implements IManager {

	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao;
	private ISubvDeclaradaDao subvDeclaradaDao;
	private IPolizaCopyDao polizaCopyDao;
	private IPolizaDao polizaDao;
	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private PolizaCopyManager polizaCopyManager;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private ILineaDao lineaDao;
	private IEstadoCuponDao estadoCuponDao;
	private ICuponDao cuponDao;
	IDiccionarioDatosDao diccionarioDatosDao;
	private IAseguradoDao aseguradoDao;

	private ISolicitudModificacionManager solicitudModificacionManager;

	private static final Log logger = LogFactory.getLog(DeclaracionesModificacionPolizaManager.class);

	/**
	 * Obtiene los Anexos de Modificacion de la poliza.
	 * 
	 * @param idPoliza
	 * @return
	 * @throws BusinessException
	 */
	public List<AnexoModificacion> listByIdPoliza(Long idPoliza) throws BusinessException {

		try {
			// Recuperamos los anexos de modificacion de la poliza
			return declaracionModificacionPolizaDao.listAnexModifByIdPoliza(idPoliza);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error listando las declaraciones de modificacion de poliza",
					dao);
		}
	}

	/**
	 * Metodo para rellenar los datos del anexo en funcion de la copy o de la poliza
	 * 
	 * @param anexo
	 * @param realPath
	 * @throws BusinessException
	 */
	private void setDatosAnexoFromCopyOrPoliza(AnexoModificacion anexo, String realPath)
			throws BusinessException, CargaPolizaFromCopyOrPolizaException {
		// String refPoliza = null;
		Long idCopy = null;
		try {
			if (!StringUtils.nullToString(anexo.getPoliza().getReferencia()).equals("")) {
				logger.debug("Descargamos la copy para los siguientes datos: " + anexo.getPoliza().getTipoReferencia()
						+ ", " + anexo.getPoliza().getLinea().getCodplan() + ", " + anexo.getPoliza().getReferencia()
						+ ", " + realPath);

				idCopy = polizaCopyManager.descargarPolizaCopyWS(Constants.MODULO_POLIZA_PRINCIPAL + "",
						anexo.getPoliza().getLinea().getCodplan(), anexo.getPoliza().getReferencia(), realPath);

				if (idCopy != null) {
					logger.debug("Se descargo la copy de Agroseguro para los datos introducidos");
					anexo.setIdcopy(idCopy);
				} else {
					// si idcopy es null buscamos en la bbdd la copy mas reciente
					logger.debug("Buscamos la copy en BD");
					com.rsi.agp.dao.tables.copy.Poliza copy = polizaCopyDao.getPolizaCopyMasRecienteByReferencia(
							anexo.getPoliza().getTipoReferencia(), anexo.getPoliza().getReferencia());

					if (copy != null) {
						logger.debug("Id de copy de BD: " + copy.getId());
						anexo.setIdcopy(copy.getId());
					} else {
						// Si no hay copy en la BBDD creamos el siniestro con el
						// idcopy a null
						logger.debug("No hay copy para los datos introducidos en BD");
						anexo.setIdcopy(null);
					}
				}

			}

			this.setDatosAseguradoAnexo(anexo);

			// Si el anexo pertenece a una poliza de plan 2015 o superior se copian los
			// datos de comisiones
			if (anexo.getPoliza() != null && anexo.getPoliza().isPlanMayorIgual2015()) {
				anexo.setPctadministracion(anexo.getPoliza().getPolizaPctComisiones().getPctadministracion());
				anexo.setPctadquisicion(anexo.getPoliza().getPolizaPctComisiones().getPctadquisicion());
				anexo.setPctcomisionmediador(anexo.getPoliza().getPolizaPctComisiones().getPctcommax());
			}

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error listando declaraciones de modificacion de poliza",
					dao);
		} catch (Exception e) {
			throw new BusinessException(
					"Se ha producido un error indefinido listando declaraciones de modificacion de poliza", e);
		}
	}

	/**
	 * Metodo para cargar los datos basicos del anexo de modificacion a partir de la
	 * situacion actualizada obtenida durante la peticion del cupon para el anexo.
	 * 
	 * @param anexo
	 *            Anexo de modificacion
	 * @throws Exception
	 */
	private void setDatosAnexoFromPolizaActualizada(final AnexoModificacion anexo) throws Exception {
		
		es.agroseguro.contratacion.Poliza poliza  = null;
		es.agroseguro.contratacion.Poliza polizaComp = null;
		
		if (Constants.MODULO_POLIZA_PRINCIPAL.equals(anexo.getPoliza().getTipoReferencia())) {
			poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
					.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();
		} else {
			poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
					.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();
			
			polizaComp = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
					.getPolizaActualizadaCplFromCupon(anexo.getCupon().getIdcupon())).getPoliza();
			
		}		
		
		// Datos del asegurado
		if (poliza.getAsegurado() != null) {
			if (poliza.getAsegurado().getRazonSocial() != null
					&& !StringUtils.nullToString(poliza.getAsegurado().getRazonSocial().getRazonSocial()).equals("")) {
				anexo.setRazsocaseg(StringUtils.nullToString(poliza.getAsegurado().getRazonSocial().getRazonSocial()));
			} else if (poliza.getAsegurado().getNombreApellidos() != null) {
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido1()).equals(""))
						anexo.setApel1aseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido1()));
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido2()).equals(""))
						anexo.setApel2aseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getApellido2()));
					if (!StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getNombre()).equals(""))
						anexo.setNomaseg(
								StringUtils.nullToString(poliza.getAsegurado().getNombreApellidos().getNombre()));
			}
			
			if (poliza.getAsegurado().getDireccion() != null) {
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getVia()).equals(""))
					anexo.setCalleaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getVia()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getNumero()).equals(""))
					anexo.setNumaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getNumero()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getBloque()).equals(""))
					anexo.setBloqueaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getBloque()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getEscalera()).equals(""))
					anexo.setEscaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getEscalera()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getPiso()).equals(""))
					anexo.setPisoaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getPiso()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getProvincia()).equals(""))
					anexo.setCodprovincia(new BigDecimal(
							StringUtils.nullToString(poliza.getAsegurado().getDireccion().getProvincia())));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getLocalidad()).equals(""))
					anexo.setNomlocalidad(
							StringUtils.nullToString(poliza.getAsegurado().getDireccion().getLocalidad()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDireccion().getCp()).equals(""))
					anexo.setCodposaseg(StringUtils.nullToString(poliza.getAsegurado().getDireccion().getCp()));
			}
			
			if (poliza.getAsegurado().getDatosContacto() != null) {
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getEmail()).equals(""))
					anexo.setEmail(StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getEmail()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoFijo()).equals(""))
					anexo.setTelffijoaseg(
							StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoFijo()));
				if (!StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoMovil()).equals(""))
					anexo.setTelfmovilaseg(
							StringUtils.nullToString(poliza.getAsegurado().getDatosContacto().getTelefonoMovil()));
			}
		}
		// Datos de la seguridad social
		if (poliza.getSubvencionesDeclaradas() != null
				&& poliza.getSubvencionesDeclaradas().getSeguridadSocial() != null) {
			anexo.setNumsegsocial(StringUtils
					.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getProvincia())
					+ StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getNumero())
					+ StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getCodigo()));
			anexo.setRegimensegsocial(
					StringUtils.nullToString(poliza.getSubvencionesDeclaradas().getSeguridadSocial().getRegimen())
							+ "");
		}

		// Gastos de la principal
		if (Constants.MODULO_POLIZA_PRINCIPAL.equals(anexo.getPoliza().getTipoReferencia())) {
			// Gastos de la principal
			if (poliza.getEntidad().getGastosArray() != null && poliza.getEntidad().getGastosArray().length > 0) {
	
				for (Gastos gastos : poliza.getEntidad().getGastosArray()) {
					// Si pertenece al G.N de Resto (1)
					if (Constants.GRUPO_NEGOCIO_VIDA.equals(gastos.getGrupoNegocio().charAt(0))) {
						anexo.setPctadministracionResto(gastos.getAdministracion());
						anexo.setPctadquisicionResto(gastos.getAdquisicion());
						anexo.setPctcomisionmediadorResto(gastos.getComisionMediador());
					}
					// Si pertenece al G.N de RyD (2)
					else if (Constants.GRUPO_NEGOCIO_RYD.equals(gastos.getGrupoNegocio().charAt(0))) {
						anexo.setPctadministracion(gastos.getAdministracion());
						anexo.setPctadquisicion(gastos.getAdquisicion());
						anexo.setPctcomisionmediador(gastos.getComisionMediador());
					}
				}
			}
	
			String codModulo = poliza.getCobertura().getModulo().trim();
			anexo.setCodmodulo(codModulo);
		} else {
			if (polizaComp.getEntidad().getGastosArray() != null && polizaComp.getEntidad().getGastosArray().length > 0) {
				
				for (Gastos gastos : polizaComp.getEntidad().getGastosArray()) {
					// Si pertenece al G.N de Resto (1)
					if (Constants.GRUPO_NEGOCIO_VIDA.equals(gastos.getGrupoNegocio().charAt(0))) {
						anexo.setPctadministracionResto(gastos.getAdministracion());
						anexo.setPctadquisicionResto(gastos.getAdquisicion());
						anexo.setPctcomisionmediadorResto(gastos.getComisionMediador());
					}
					// Si pertenece al G.N de RyD (2)
					else if (Constants.GRUPO_NEGOCIO_RYD.equals(gastos.getGrupoNegocio().charAt(0))) {
						anexo.setPctadministracion(gastos.getAdministracion());
						anexo.setPctadquisicion(gastos.getAdquisicion());
						anexo.setPctcomisionmediador(gastos.getComisionMediador());
					}
				}
			}
	
			String codModulo = polizaComp.getCobertura().getModulo().trim();
			anexo.setCodmodulo(codModulo);
		}
		/* Pet. 70105 - Fase III (REQ.05) - MODIF TAM (02/03/2021) * Inicio */
		if (poliza.getPago() != null && poliza.getPago().getCuenta() != null) {
			if (anexo.getPoliza().getLinea().isLineaGanado()) {
				// Asegurado
				if ("A".equals(poliza.getPago().getCuenta().getDestinatario())) {
					anexo.setIbanAsegOriginal(poliza.getPago().getCuenta().getIban());
					anexo.setEsIbanAsegModificado(0);
				}
			} else {
				anexo.setIbanAsegOriginal(poliza.getPago().getCuenta().getIban());
				anexo.setEsIbanAsegModificado(0);
			}
		}

		if (poliza.getCuentaCobroSiniestros() != null) {
			anexo.setIban2AsegOriginal(poliza.getCuentaCobroSiniestros().getIban());
			anexo.setEsIban2AsegModificado(0);
		}

	}

	/**
	 * Elimina el Anexo de Modificacion.
	 * 
	 * @param idModificacionPoliza
	 * @throws BusinessException
	 */
	public void eliminarDeclaracionModificacionPoliza(Long idModificacionPoliza) throws BusinessException {

		try {

			declaracionModificacionPolizaDao.eliminarDeclaracionModificacionPoliza(idModificacionPoliza);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error borrando una declaracion de modificacion de poliza",
					dao);
		}
	}

	/**
	 * Obtiene el Anexo de Modificacion.
	 * 
	 * @param idAnexo
	 * @return
	 * @throws BusinessException
	 */
	public AnexoModificacion getAnexoModifById(Long idAnexo) throws BusinessException {
		try {
			return declaracionModificacionPolizaDao.getAnexoModifById(idAnexo);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al recuperar el anexo de modificacion de poliza",
					dao);
		}
	}

	public AnexoModificacion getAnexoModifById(Long idAnexo, boolean evict) throws BusinessException {
		try {
			return declaracionModificacionPolizaDao.getAnexoModifById(idAnexo, evict);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al recuperar el anexo de modificacion de poliza",
					dao);
		}
	}

	/**
	 * Alta de un Anexo de Modificacion.
	 * 
	 * @param anexo
	 * @return
	 * @throws BusinessException
	 */
	public AnexoModificacion altaAnexoModificacion(AnexoModificacion anexo, String realPath, String codUsuario,
			Estado estado, boolean esAlta) throws BusinessException, DAOException, ValidacionAnexoModificacionException,
			CargaPolizaActualizadaDelCuponException, CargaPolizaFromCopyOrPolizaException {

		logger.debug("*** DeclaracionesModificacionPolizaManager - altaAnexoModificacion");
		Long idPoliza = anexo.getPoliza().getIdpoliza();
		Poliza poliza = this.polizaDao.getPolizaById(idPoliza);
		anexo.setPoliza(poliza);
		boolean actualizaXml = false;

		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/*
		 * Por los desarrollos de esta peticion tanto las polizas agricolas como las de
		 * ganado iran por el mismo end-point y con formato Unificado
		 */

		if (anexo.getCupon() != null && anexo.getCupon().getIdcupon() != null) {
			try {
				// Si el anexo va por cupon => cargo los datos de la situacion actualizada
				// asociada al cupon
				this.setDatosAnexoFromPolizaActualizada(anexo);
			} catch (Exception e) {
				logger.error("Se ha producido un error al procesar los datos de la situacion actualizada del cupon. ",
						e);
				throw new CargaPolizaActualizadaDelCuponException(
						"Se ha producido un error al cargar los datos de la situacion actualizada del cupon", e);
			}
		} else {
			// Si el anexo es de tipo FTP => obtengo los datos de la copy o de la poliza
			if (!hayAnexoBorradorODefinitivoByIdPoliza(idPoliza)) {
				try {
					this.setDatosAnexoFromCopyOrPoliza(anexo, realPath);
					actualizaXml = true;
				} catch (Exception e) {
					logger.error("Se ha producido un error al procesar los datos de la copy de la poliza. ", e);
					throw new CargaPolizaFromCopyOrPolizaException(
							"Se ha producido un error al cargar los datos de la copy de la poliza", e);
				}
			} else {
				return anexo;
			}
		}

		// Creamos el Anexo de Modificacion
		anexo = this.saveAnexoModificacion(anexo, codUsuario, estado, esAlta);
		logger.debug("*** Despuï¿½s de guardar el anexo ");

		/* ESC-14312 ** MODIF TAM (21.06.2021) ** Inicio */
		/*
		 * Cargamos las comparativas en el anexo obtenidas en la situacion actualizada
		 * de la poliza
		 */
		/*
		 * Obtener las coberturas de la Situacion Actualizada de la poliza, solo en el
		 * caso de que el Anexo no tenga coberturas ya
		 */
		/* ESC-14700 ** MODIF TAM (23.07.2021) **/
		if (anexo.getCoberturas().size() <= 0) {
			if (!anexo.getPoliza().getLinea().isLineaGanado()
					&& anexo.getPoliza().getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)) {
				es.agroseguro.contratacion.Poliza polizaAct = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
						.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();

				if (polizaAct.getCobertura().getDatosVariables() != null) {
					es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = polizaAct.getCobertura()
							.getDatosVariables();

					Set<Cobertura> coberturasAnx = getCoberturasFromPolizaActualizada(datosVariables, anexo);

					if (coberturasAnx.size() > 0) {
						for (Cobertura cob : coberturasAnx) {
							declaracionModificacionPolizaDao.saveCoberturasAnexo(cob);
						}
					}
				}

			} else {
				/* ESC-14671 ** MODIF TAM (22/07/2021) ** Inicio */
				/*
				 * Cargar las coberturas para los anexos de Ganado tambien por defecto, los que
				 * se recperen de la situacion actualizada
				 */
				if (anexo.getPoliza().getLinea().isLineaGanado()) {

					logger.debug("*** DeclaracionesModificacionPolizaManager - Entramos por Anexo de Ganado");
					logger.debug("*** Guardamos las coberturas de Ganado obtenidas de la situaciï¿½n Actualizada");

					es.agroseguro.contratacion.Poliza polizaAct = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
							.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();

					logger.debug("Despuï¿½s de obtener la pï¿½liza Actualizada");
					List<Cobertura> lstCob = new ArrayList<Cobertura>();

					final Long lineaseguroid = poliza.getLinea().getLineaseguroid();

					if (((es.agroseguro.contratacion.Poliza) polizaAct).getCobertura() != null && !StringUtils
							.nullToString(((es.agroseguro.contratacion.Poliza) polizaAct).getCobertura().getModulo())
							.equals("")) {
						// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA
						// (31) Y A
						// LA UBICACION DE COBERTURAS (18)
						Filter oiFilter = new Filter() {
							@Override
							public Criteria getCriteria(final Session sesion) {
								Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
								criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
								criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA));
								criteria.add(Restrictions.in("id.codubicacion",
										new Object[] { OrganizadorInfoConstants.UBICACION_COBERTURA_DV }));
								return criteria;
							}
						};
						@SuppressWarnings("unchecked")
						List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) lineaDao
								.getObjects(oiFilter);
						es.agroseguro.contratacion.datosVariables.DatosVariables dvs = ((es.agroseguro.contratacion.Poliza) polizaAct)
								.getCobertura().getDatosVariables();

						if (dvs != null) {
							
							try {
								if (dvs.getRiesgCbtoElegArray() != null && dvs.getRiesgCbtoElegArray().length > 0) {
									for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : dvs
											.getRiesgCbtoElegArray()) {
										
										BigDecimal sFila = declaracionModificacionPolizaDao.getfilaRiesgoCubModulo(lineaseguroid,
												((es.agroseguro.contratacion.Poliza) polizaAct).getCobertura().getModulo(), 
												new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub()));
										
										lstCob.add(generarCoberturaGan(new BigDecimal(rce.getCPMod()), new BigDecimal(rce.getCodRCub()), 
														    new BigDecimal(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO),
														    sFila, 
														    "S".equals(rce.getValor()) ? new BigDecimal(Constants.RIESGO_ELEGIDO_SI)
																	: new BigDecimal(Constants.RIESGO_ELEGIDO_NO), anexo));
									}
								}

							} catch (Exception e) {
								logger.debug("Error al obtener los riesgos cubiertos elegibles de la cobertura.", e);
							}
							
							try {
								for (OrganizadorInformacion oi : oiList) {
									Method method = dvs.getClass()
											.getMethod("get" + oi.getDiccionarioDatos().getEtiquetaxml() + "Array");
									Class<?> dvClass = dvs.getClass()
											.getMethod("addNew" + oi.getDiccionarioDatos().getEtiquetaxml())
											.getReturnType();
									Object[] result = (Object[]) method.invoke(dvs);
									for (Object obj : result) {

										BigDecimal valor = new BigDecimal(
												"" + dvClass.getMethod("getValor").invoke(obj));
										BigDecimal cpMod = new BigDecimal(
												"" + dvClass.getMethod("getCPMod").invoke(obj));
										BigDecimal rCub = new BigDecimal(
												"" + dvClass.getMethod("getCodRCub").invoke(obj));
										logger.debug("Dentro del for obj, valor de cpMod:" + cpMod + " valor de rCub:"
												+ rCub);
										
										BigDecimal sFila = declaracionModificacionPolizaDao.getfilaRiesgoCubModulo(lineaseguroid,
												((es.agroseguro.contratacion.Poliza) polizaAct).getCobertura().getModulo(), 
												cpMod, rCub);
										
										lstCob.add(
												generarCoberturaGan(cpMod, rCub, oi.getId().getCodconcepto(), sFila, valor, anexo));
									}
								}
							} catch (Exception e) {
								logger.error("Error al obtener los datos variables de la cobertura.", e);
							}
						}
						
						//Comprobar lo de la tipología
						Integer tipologia = poliza.getModuloPolizas().iterator().next().getTipoAsegGanado();
						
						if (tipologia != null) {
							lstCob.add(
								generarCoberturaGan(null, null, BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_ASEG_GAN), BigDecimal.valueOf(0), BigDecimal.valueOf(tipologia), anexo));
						}
						if (lstCob.size() > 0) {
							for (Cobertura cob : lstCob) {
								logger.debug("*** Guardamos la cobertura:" + cob.getCodconceptoppalmod() + " y :"
										+ cob.getCodriesgocubierto());
								declaracionModificacionPolizaDao.saveCoberturasAnexo(cob);
							}
						}
					}
				}
			}
		} /* Fin del if de coberturas.size */

		/* ESC-14312 ** MODIF TAM (21.06.2021) ** Inicio */
		if (actualizaXml) {
			try {
				// Actualizamos el xml que se enviara a Agroseguro
				actualizaXml(anexo, false);

			} catch (ValidacionAnexoModificacionException e) {
				logger.error("Se ha producido un error al validar el xml del anexo de modificacion", e);
				throw new ValidacionAnexoModificacionException(e);
			} catch (Exception dao) {
				throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza",
						dao);
			}
		}

		// Cuando damos de alta un anexo rellenamos el campo tieneanexomp a 'S'
		anexo.getPoliza().setTieneanexomp('S');
		this.updateAnexoModificacionPoliza(anexo.getPoliza());

		return anexo;
	}

	/* ESC-14671 ** MODIF TAM (22.07.2021) ** Inicio */
	/**
	 * Crear un objeto CoberturaSeleccionada con los datos indicados por parametro
	 * 
	 * @param cpm
	 * @param rCub
	 * @param codConcepto
	 * @param valor
	 * @return
	 */
	private Cobertura generarCoberturaGan(final BigDecimal cpm, final BigDecimal rCub, final BigDecimal codConcepto,
			final BigDecimal filaModulo, final BigDecimal valor, final AnexoModificacion anexo) {
		
		Cobertura cobertura = new Cobertura();
		
		cobertura.setCodconcepto(codConcepto);
		cobertura.setCodconceptoppalmod(cpm);
		cobertura.setCodriesgocubierto(rCub);
		cobertura.setCodvalor(valor.toString());
		
		cobertura.setFilacomparativa(1);
		cobertura.setFilamodulo(filaModulo.intValue());
		
		cobertura.setAnexoModificacion(anexo);
		
		return cobertura;
	}

	/* ESC-14671 ** MODIF TAM (22.07.2021) ** Fin */

	/**
	 * Pasa a definitiva un A.M de una poliza PPAL TMR 28-06-2013 llamada al PL para
	 * insertar el estado y usuario en el historico
	 * (historicoEstadosManager.insertaEstado )
	 * 
	 * @author U029769 28/06/2013
	 * @param idAnexo
	 * @param codUsuario
	 * @throws BusinessException
	 * @throws DAOException
	 * @throws ValidacionAnexoModificacionException
	 */
	public void pasarDefinitiva(Long idAnexo, String codUsuario)
			throws BusinessException, DAOException, ValidacionAnexoModificacionException {

		AnexoModificacion anexoModificacion = (AnexoModificacion) declaracionModificacionPolizaDao
				.get(AnexoModificacion.class, idAnexo);

		try {

			actualizaXml(anexoModificacion, true);

		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificacion");
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception dao) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza", dao);
		}

		Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
		anexoModificacion.setFechafirmadoc(new Date());
		this.saveAnexoModificacion(anexoModificacion, codUsuario, estado, false);
	}

	/**
	 * Guarda un Anexo de Modificacion y inserta en el historico 14/08/2013 U029769
	 * 
	 * @param anexo
	 * @throws BusinessException
	 */
	public AnexoModificacion saveAnexoModificacion(AnexoModificacion anexo, String codUsuario, Estado estado,
			boolean esAlta) throws BusinessException {

		try {

			boolean insertarHistorico = false;
			if (estado != null && !estado.getIdestado().equals(anexo.getEstado().getIdestado()))
				insertarHistorico = true;

			if (estado != null) {
				anexo.setEstado(estado);
			}
			/*
			 * Si es alta:Comprobamos si el objeto "comunicaciones" tiene id, porque si lo
			 * tiene hay que quitarlo para que no de error al guardar
			 */
			if (esAlta) {
				if (anexo.getComunicaciones() != null && anexo.getComunicaciones().getIdenvio() == null) {
					anexo.setComunicaciones(null);
				}

				// Comprobamos si el AM se da de alta por cupon o por FTP
				if (anexo.getCupon() == null || StringUtils.nullToString(anexo.getCupon().getIdcupon()).equals("")) {
					anexo.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_FTP);
					anexo.setCupon(null);
				} else {
					anexo.setTipoEnvio(Constants.ANEXO_MODIF_TIPO_ENVIO_SW);
				}

				if (anexo.getEstadoAgroseguro() != null && anexo.getEstadoAgroseguro().getCodestado() == null) {
					anexo.setEstadoAgroseguro(null);
				}
			}

			declaracionModificacionPolizaDao.saveOrUpdate(anexo);

			// Si hay un cambio de estado se inserta en el historico
			if (insertarHistorico)
				historicoEstadosManager.insertaEstado(Tabla.ANEXO_MOD, anexo.getId(), codUsuario, estado.getIdestado());

			declaracionModificacionPolizaDao.evict(anexo);

			return anexo;

		} catch (Exception ex) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion", ex);
		}
	}

	/**
	 * Llama al metodo que borra todos los cupones cuyo idcupon coincida con el
	 * parÃ¡metro
	 * 
	 * @param idCupon
	 */
	public void borrarCupon(String idCupon) {
		try {
			cuponDao.borrarCupon(idCupon);
		} catch (Exception e) {
			logger.error("Error al borrar el cupon", e);
		}
	}

	/**
	 * Metodo que asigna el asegurado a nuestro anexo, desde copy o desde poliza
	 * 
	 * @param anexo
	 * @return
	 * @throws DAOException
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void setDatosAseguradoAnexo(AnexoModificacion anexo) throws DAOException {

		Asegurado aseg = anexo.getPoliza().getAsegurado();
		if (anexo.getIdcopy() != null) {
			// Si tengo copy => cogemos los datos de la misma

			com.rsi.agp.dao.tables.copy.Asegurado aseguradoCopy = declaracionModificacionPolizaDao
					.getAseguradoCopy(anexo.getIdcopy());

			if (aseguradoCopy.getRazonsocialaseg() != null && !aseguradoCopy.getRazonsocialaseg().equals("")) {
				anexo.setRazsocaseg(StringUtils.nullToString(aseguradoCopy.getRazonsocialaseg()));
			} else {
				anexo.setApel1aseg(StringUtils.nullToString(aseguradoCopy.getApell1aseg()));
				anexo.setApel2aseg(StringUtils.nullToString(aseguradoCopy.getApell2aseg()));
				anexo.setNomaseg(StringUtils.nullToString(aseguradoCopy.getNombreaseg()));
			}
			anexo.setBloqueaseg(StringUtils.nullToString(aseguradoCopy.getBloqueaseg()));
			anexo.setCalleaseg(StringUtils.nullToString(aseguradoCopy.getViaaseg()));

			// INTRODUCIMOS EL CODLOCALIDAD DEL ASEGURADO DE POLIZA
			if (anexo.getPoliza() != null && anexo.getPoliza().getAsegurado() != null
					&& anexo.getPoliza().getAsegurado().getLocalidad() != null
					&& anexo.getPoliza().getAsegurado().getLocalidad().getId() != null
					&& anexo.getPoliza().getAsegurado().getLocalidad().getId().getCodlocalidad() != null)
				anexo.setCodlocalidad(anexo.getPoliza().getAsegurado().getLocalidad().getId().getCodlocalidad());

			if (aseguradoCopy.getProvinciaaseg() != null)
				anexo.setCodprovincia(aseguradoCopy.getProvinciaaseg());

			anexo.setCodposaseg(StringUtils.nullToString(aseguradoCopy.getCpaseg()));
			anexo.setEmail(StringUtils.nullToString(aseguradoCopy.getEmail()));
			anexo.setEscaseg(StringUtils.nullToString(aseguradoCopy.getEscaleraaseg()));
			anexo.setNumaseg(StringUtils.nullToString(aseguradoCopy.getNumeroviaaseg()));

			// DAA 19/03/2013 Puesto que aseguradoCopy no tiene Numsegsocial la cogemos del
			// asegurado de la Poliza (si la tiene)
			// y si no, de A.M. Anteriores de la misma poliza

			if (!("").equals(StringUtils.nullToString(aseg.getNumsegsocial()))) {
				anexo.setNumsegsocial(StringUtils.nullToString(aseg.getNumsegsocial()));
				anexo.setRegimensegsocial(StringUtils.nullToString(aseg.getRegimensegsocial()));
			} else {
				List segsocialAnexoAnterior = declaracionModificacionPolizaDao
						.getSegSocialAnexoAnterior(anexo.getPoliza().getIdpoliza());
				if (segsocialAnexoAnterior != null && segsocialAnexoAnterior.size() > 0) {
					anexo.setNumsegsocial((String) segsocialAnexoAnterior.get(0));
					anexo.setRegimensegsocial((String) segsocialAnexoAnterior.get(1));
				} else {
					anexo.setNumsegsocial("");
					anexo.setRegimensegsocial("");
				}
			}
			anexo.setPisoaseg(StringUtils.nullToString(aseguradoCopy.getPisoaseg()));
			anexo.setTelffijoaseg(StringUtils.nullToString(aseguradoCopy.getTelefonofijo()));
			anexo.setTelfmovilaseg(StringUtils.nullToString(aseguradoCopy.getTelefonomovil()));

		} else {
			// Cogemos los datos de la poliza
			if (aseg.getTipoidentificacion().equals("NIF") || aseg.getTipoidentificacion().equals("NIE")) {
				anexo.setApel1aseg(StringUtils.nullToString(aseg.getApellido1()));
				anexo.setApel2aseg(StringUtils.nullToString(aseg.getApellido2()));
				anexo.setNomaseg(StringUtils.nullToString(aseg.getNombre()));
			} else {
				anexo.setRazsocaseg(StringUtils.nullToString(aseg.getRazonsocial()));
			}
			anexo.setBloqueaseg(StringUtils.nullToString(aseg.getBloque()));
			anexo.setCalleaseg(StringUtils.nullToString(aseg.getDireccion()));
			if (aseg.getLocalidad() != null && aseg.getLocalidad().getId() != null) {
				if (aseg.getLocalidad().getId().getCodlocalidad() != null)
					anexo.setCodlocalidad(aseg.getLocalidad().getId().getCodlocalidad());
				if (aseg.getLocalidad().getId().getCodprovincia() != null)
					anexo.setCodprovincia(aseg.getLocalidad().getId().getCodprovincia());
				anexo.setNomlocalidad(StringUtils.nullToString(aseg.getLocalidad().getNomlocalidad()));
			}
			anexo.setCodposaseg(StringUtils.nullToString(aseg.getCodpostal()));
			anexo.setEmail(StringUtils.nullToString(aseg.getEmail()));
			anexo.setEscaseg(StringUtils.nullToString(aseg.getEscalera()));
			anexo.setNumaseg(StringUtils.nullToString(aseg.getNumvia()));
			anexo.setNumsegsocial(StringUtils.nullToString(aseg.getNumsegsocial()));
			anexo.setPisoaseg(StringUtils.nullToString(aseg.getPiso()));

			anexo.setRegimensegsocial(StringUtils.nullToString(aseg.getRegimensegsocial()));
			anexo.setTelffijoaseg(StringUtils.nullToString(aseg.getTelefono()));
			anexo.setTelfmovilaseg(StringUtils.nullToString(aseg.getMovil()));

		}

	}

	public List<SubvDeclarada> getAllSubvAnexo(Long idAnexo) throws BusinessException {
		try {

			return subvDeclaradaDao.getAll(idAnexo);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al recuperar las subvenciones asociadas al anexo",
					dao);
		}
	}

	public List<AnexoModificacion> getAnexosPolizaEstadoBorradorODefinitivo(Long idPoliza) throws BusinessException {
		try {

			return declaracionModificacionPolizaDao.listarByIdPolizaBorradorYDefinitivo(idPoliza);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error recuperar los anexos de la poliza", dao);
		}
	}

	/**
	 * Alta/Update de un Anexo de Modificacion.
	 * 
	 * @param anexo
	 * @return
	 * @throws BusinessException
	 * @throws ValidacionAnexoModificacionException
	 */
	public AnexoModificacion guardarAnexoModificacion(AnexoModificacion anexo, String realPath, boolean modAseg,
			boolean comprobarCopy, String codUsuario, Estado estado, boolean esAlta)
			throws BusinessException, ValidacionAnexoModificacionException {

		try {
			// Asociamos al Anexo el idCopy (si tiene)
			if (comprobarCopy) {
				setDatosAnexoFromCopyOrPoliza(anexo, realPath);
			}

			// Creamos el Anexo de Modificacion
			saveAnexoModificacion(anexo, codUsuario, estado, esAlta);

			// Actualizamos el xml que se enviara a Agroseguro
			actualizaXml(anexo, false);

			return anexo;

		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificacion", e);
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception dao) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza", dao);

		}
	}

	/**
	 * Devuelve true si la Poliza tiene un Anexo en estado Borrador o definitivo
	 * 
	 * @param idPoliza
	 * @return
	 * @throws BusinessException
	 */
	private boolean hayAnexoBorradorODefinitivoByIdPoliza(Long idPoliza) throws BusinessException {
		try {
			return declaracionModificacionPolizaDao.listByIdPolizaBorradorYDefinitivo(idPoliza);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza", dao);
		}
	}

	public void guardarAnexoSubv(SubvDeclarada subvencion, AnexoModificacion anexo, String codUsuario)
			throws BusinessException {
		try {
			subvDeclaradaDao.saveOrUpdate(subvencion);

			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
			this.saveAnexoModificacion(anexo, codUsuario, estado, false);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza", dao);
		}

	}

	public void bajaSubvIncompatible(AnexoModificacion anexo, BigDecimal codSubv, String codUsuario)
			throws BusinessException, ValidacionAnexoModificacionException {

		try {
			subvDeclaradaDao.bajaSubvIncompatible(anexo, codSubv);

			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_BORRADOR);
			this.saveAnexoModificacion(anexo, codUsuario, estado, false);

			// Actualizamos el xml que se enviara a Agroseguro
			actualizaXml(anexo, false);
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificacion");
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception dao) {
			throw new BusinessException("Se ha producido un error al guardar el anexo de modificacion de poliza", dao);
		}

	}

	public Poliza getPoliza(Long idPoliza) {
		Poliza poliza = null;
		poliza = (Poliza) this.declaracionModificacionPolizaDao.getObject(Poliza.class, idPoliza);
		return poliza;
	}

	public Comunicaciones getComunicaciones(BigDecimal idEnvio) {
		Comunicaciones comunicaciones = null;

		comunicaciones = (Comunicaciones) this.declaracionModificacionPolizaDao.getObject(Comunicaciones.class,
				idEnvio);
		return comunicaciones;
	}

	public List<Parcela> getParcelas(Long idAnexo) {
		List<Parcela> parcelas = null;

		try {
			parcelas = this.declaracionModificacionPolizaDao.getParcelas(idAnexo);
		} catch (DAOException e) {
			logger.error("Excepcion : DeclaracionesModificacionPolizaManager - getParcelas", e);
		}
		return parcelas;
	}

	/**
	 * Metodo para actualizar el xml de un anexo
	 * 
	 * @param anexo
	 *            Anexo a actualizar
	 * @throws BusinessException
	 * @throws ValidacionAnexoModificacionException,Exception
	 */
	private void actualizaXml(AnexoModificacion anexo, boolean validarEstructuraXml)
			throws ValidacionAnexoModificacionException, BusinessException {

		boolean modAseg = false;
		try {
			// Calculo de CPM permitidos
			logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
					+ anexo.getPoliza().getIdpoliza() + ", idAnexo: " + anexo.getId() + ", codModulo: "
					+ anexo.getCodmodulo());
			List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexo.getPoliza().getIdpoliza(),
					anexo.getId(), anexo.getCodmodulo());

			XmlTransformerUtil.updateXMLAnexoMod(xmlAnexoModDao, polizaCopyDao, anexo, modAseg, listaCPM,
					validarEstructuraXml);

		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Error validando el xml de de Anexos de Modificacion" + e.getMessage());
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception ee) {
			logger.error("Error generico al actualizar el xml de Anexos de Modificacion" + ee.getMessage());
			throw new BusinessException("Error generico al pasar a definitiva");
		}
	}

	public es.agroseguro.acuseRecibo.Error[] getFicheroContenido(BigDecimal idEnvio, String refPoliza, BigDecimal linea,
			BigDecimal plan) throws BusinessException {
		AcuseReciboDocument acuseRecibo = null;
		es.agroseguro.acuseRecibo.Error[] errores = null;
		Comunicaciones comunicaciones = null;

		// Se monta la referencia que luego se comparara
		String referencia = refPoliza.toString() + "" + plan.toString() + "" + linea.toString();

		try {

			comunicaciones = declaracionModificacionPolizaDao.getComunicaciones(idEnvio);

			if (comunicaciones == null) {
				errores = new es.agroseguro.acuseRecibo.Error[0];

			} else {
				Clob fichero = comunicaciones.getFicheroContenido();
				if (fichero == null) {
					errores = new es.agroseguro.acuseRecibo.Error[0];
				} else {
					String xml = WSUtils.convertClob2String(fichero); // Recuperamos el Clob y lo convertimos en String
					// Se comprueba si existe cabecera, sino se inserta al principio
					if (xml.indexOf("<?xml version=\"1.0") == -1) {
						String cabecera = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
						xml = cabecera + xml;
					}

					// Se Reempla
					String namespace = "http://www.agroseguro.es/AcuseRecibo";
					String reemplazar1 = "<AcuseRecibo xmlns=\"" + namespace + "\"";
					String reemplazar2 = "</AcuseRecibo>";
					if (xml.indexOf("<xml-fragment") == -1) {
						if (xml.indexOf("http://www.agroseguro.es/AcuseRecibo") == -1) {
							// Buscamos Acuse Recibo
							xml = xml.replace("<AcuseRecibo", reemplazar1);
						}
					} else {

						xml = xml.replace("<xml-fragment", reemplazar1).replace("</xml-fragment>", reemplazar2)
								.replace("xmlns:acus=\"http://www.agroseguro.es/AcuseRecibo\"", "")
								.replace("acus:", "");
					}

					try {
						acuseRecibo = AcuseReciboDocument.Factory.parse(new StringReader(xml)); // String parseado a
																								// AcuseReciboDocument
					} catch (Exception e) {
						logger.error("Se ha producido un error al recuperar el XML de Acuse de Recibo", e);
						throw new BusinessException("Error al convertir el XML a XML Bean", e);
					}

					if (acuseRecibo != null) {
						AcuseRecibo ac = acuseRecibo.getAcuseRecibo();
						ArrayList<es.agroseguro.acuseRecibo.Error> ArrayE = new ArrayList<es.agroseguro.acuseRecibo.Error>();

						// Recorremos Acuse de Recibo para montar Array con errores
						for (int i = 0; i < ac.getDocumentoArray().length; i++) {
							Documento documentoRecibido = ac.getDocumentoArray(i);
							int j = 0;

							// Si el documento del acuse de recibo tiene estado 2 (rechazado) y coincide
							// "idPoliza + linea + plan"
							if (documentoRecibido.getEstado() == Constants.ACUSE_RECIBO_ESTADO_RECHAZADO
									&& documentoRecibido.getId().equals(referencia)) {
								// Formamos la lista de Errores
								while (j < documentoRecibido.getErrorArray().length) {
									try {
										ArrayE.add(
												(es.agroseguro.acuseRecibo.Error) documentoRecibido.getErrorArray(j));
										j = j + 1;

									} catch (Exception ex) {
										throw new BusinessException(
												"Se ha producido un error al visualizar Acuse de Recibo ", ex);
									}
								}
							}
						}
						errores = new es.agroseguro.acuseRecibo.Error[ArrayE.size()];
						for (int i = 0; i < ArrayE.size(); i++) {
							errores[i] = ArrayE.get(i);
						}

					} else {
						errores = new es.agroseguro.acuseRecibo.Error[0];
					}
				}
			}

		} catch (DAOException dao) {
			throw new BusinessException(
					"Se ha producido un error al recuperar el fichero_contenido de un Anexo de Modificacion", dao);
		}

		return errores;
	}

	public void updateAnexoModificacionPoliza(Poliza poliza) throws BusinessException {

		try {
			polizaDao.saveOrUpdate(poliza);

		} catch (Exception e) {
			throw new BusinessException("Se ha producido un error actualizando el campo tieneanexomp de la poliza", e);
		}
	}

	public List<AnexoModificacion> buscarAnexosPoliza(Long idPoliza) throws BusinessException {
		try {

			return declaracionModificacionPolizaDao.listAnexModifByIdPoliza(idPoliza);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error buscando los siniestros de una poliza", dao);
		}
	}

	public List<Estado> getEstadosAnexosModificacion() throws BusinessException {
		try {

			return declaracionModificacionPolizaDao.getEstadosAnexoModificacion();

		} catch (DAOException dao) {

			throw new BusinessException("Se ha producido un error al recuperar datos de un anexo de modificacion", dao);

		}
	}

	/**
	 * Devuelve el listado de estados posibles para el cupon
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<EstadoCupon> getEstadosCupon() {
		try {
			return estadoCuponDao.getListaEstadoCupon();
		} catch (Exception exc) {
			logger.error("Ocurrio un error al obtener la listado de estados del cupon", exc);
		}

		return new ArrayList<EstadoCupon>();
	}

	/**
	 * Realiza las validaciones previas al alta de AM
	 * 
	 * @param codLinea
	 *            Linea asociada a la poliza sobre la cual se quiere dar de alta el
	 *            AM
	 * @param codPlan
	 *            Plan asociado a la poliza sobre la cual se quiere dar de alta el
	 *            AM
	 * @param idPoliza
	 *            Identificador de la poliza sobre la cual se quiere dar de alta el
	 *            AM
	 * @param idEstadoPlz
	 *            Estado de la poliza sobre la cual se quiere dar de alta el AM
	 * @return String con el resultado de las validaciones; posibles valores:
	 *         "sinGastos": La poliza renovable asociada a la poliza sobre la que se
	 *         quiere dar de alta el AM no tiene todos los datos de comisiones
	 *         informados "true": Se puede dar de alta el AM "false": El plan/linea
	 *         sobre el cual se va a dar de alta el AM no es el ultimo en
	 *         contratacion, hay que solicitar confirmacion al usuario "error": Se
	 *         ha producido un error inesperado durante las validaciones
	 */
	public String comprobarAltaAnexo(BigDecimal codLinea, BigDecimal codPlan, BigDecimal idPoliza,
			BigDecimal idEstadoPlz) {

		try {
			// Si el estado de la poliza es 'Precartera generada', 'Precartera
			// precalculada' o 'Primera comunicacion' hay que validar que la poliza
			// renovable asociada tiene correctamente informados los datos de comisiones
			// para todos sus grupos de negocio
			if (Constants.ESTADO_POLIZA_PRECARTERA_GENERADA.equals(idEstadoPlz)
					|| Constants.ESTADO_POLIZA_PRECARTERA_PRECALCULADA.equals(idEstadoPlz)
					|| Constants.ESTADO_POLIZA_PRIMERA_COMUNICACION.equals(idEstadoPlz)) {

				// En caso de que los datos de comisiones no sean correctos, se devuelve la
				// cadena que identifica el error de validacion correspondiente
				if (!declaracionModificacionPolizaDao.isValidoAnexoRenovable(idPoliza))
					return "sinGastos";
			}

			// Comprueba que el plan/linea indicados es el ultimo en contratacion
			return Boolean.toString(this.lineaDao.noExisteLineaMayorPlanActivo(codLinea, codPlan));

		} catch (Exception e) {
			logger.error("Error en las validaciones previas al alta del AM", e);
		}

		return "error";
	}

	public boolean isAnexoCaducado(AnexoModificacion am) throws ParseException {
		boolean caducado = false;
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date fechaCupon = am.getCupon().getFecha();
		sdf2.format(fechaCupon);

		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.HOUR_OF_DAY, 23);
		cal1.set(Calendar.MINUTE, 59);
		cal1.set(Calendar.SECOND, 59);
		cal1.set(Calendar.MILLISECOND, 59);
		cal1.add(Calendar.DATE, -1);

		String aux = sdf2.format(cal1.getTime());

		Date fecha2 = sdf2.parse(aux);
		if (fechaCupon.compareTo(fecha2) > 0) {
			caducado = false;
		} else if (fechaCupon.compareTo(fecha2) < 0) {
			caducado = true;
		} else if (fechaCupon.compareTo(fecha2) == 0) {
			caducado = true;
		}
		return caducado;
	}

	/**
	 * Metodo para obtener las coberturas de la poliza actualizada
	 * 
	 * @param datosVariables
	 * @return
	 */
	public static Set<Cobertura> getCoberturasFromPolizaActualizada(
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, AnexoModificacion anexo) {

		// List<CoberturaSeleccionada> lstCoberturas = new
		// ArrayList<CoberturaSeleccionada>();
		Set<Cobertura> lstCoberturas = new HashSet<Cobertura>();
		if (datosVariables != null) {
			// CALCULO INDEMNIZACION
			if (null != datosVariables.getCalcIndemArray() && datosVariables.getCalcIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion calcIndem : datosVariables
						.getCalcIndemArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION));
					cobertura.setCodconceptoppalmod(new BigDecimal(calcIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(calcIndem.getCodRCub()));
					cobertura.setCodvalor(calcIndem.getValor() + "");
					cobertura.setAnexoModificacion(anexo);
					
					lstCoberturas.add(cobertura);
				}
			}

			// GARANTIZADO
			if (null != datosVariables.getGarantArray() && datosVariables.getGarantArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.Garantizado garant : datosVariables.getGarantArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(garant.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(garant.getCodRCub()));
					cobertura.setCodvalor(garant.getValor() + "");
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// % FRANQUICIA
			if (null != datosVariables.getFranqArray() && datosVariables.getFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia franq : datosVariables
						.getFranqArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(franq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(franq.getCodRCub()));
					cobertura.setCodvalor(franq.getValor() + "");
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// % MINIMO INDEMNIZABLE
			if (null != datosVariables.getMinIndemArray() && datosVariables.getMinIndemArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : datosVariables
						.getMinIndemArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
					cobertura.setCodconceptoppalmod(new BigDecimal(minIndem.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(minIndem.getCodRCub()));
					cobertura.setCodvalor(minIndem.getValor() + "");
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// RIESGO CUBIERTO ELEGIDO
			if (null != datosVariables.getRiesgCbtoElegArray() && datosVariables.getRiesgCbtoElegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rCub : datosVariables
						.getRiesgCbtoElegArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO));
					cobertura.setCodconceptoppalmod(new BigDecimal(rCub.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(rCub.getCodRCub()));
					cobertura.setCodvalor(
							"S".equals(rCub.getValor()) || "-1".equals(rCub.getValor()) ? Constants.RIESGO_ELEGIDO_SI
									: Constants.RIESGO_ELEGIDO_NO);
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// TIPO FRANQUICIA
			if (null != datosVariables.getTipFranqArray() && datosVariables.getTipFranqArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tFranq : datosVariables
						.getTipFranqArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA));
					cobertura.setCodconceptoppalmod(new BigDecimal(tFranq.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(tFranq.getCodRCub()));
					cobertura.setCodvalor(tFranq.getValor() + "");
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// % CAPITAL ASEGURADO
			if (null != datosVariables.getCapAsegArray() && datosVariables.getCapAsegArray().length > 0) {
				for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado capAseg : datosVariables
						.getCapAsegArray()) {
					Cobertura cobertura = new Cobertura();

					cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO));
					cobertura.setCodconceptoppalmod(new BigDecimal(capAseg.getCPMod()));
					cobertura.setCodriesgocubierto(new BigDecimal(capAseg.getCodRCub()));
					cobertura.setCodvalor(capAseg.getValor() + "");
					cobertura.setAnexoModificacion(anexo);

					lstCoberturas.add(cobertura);
				}
			}

			// CARACT. EXPLOTACION
			if (null != datosVariables.getCarExpl()) {
				Cobertura cobertura = new Cobertura();

				cobertura.setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_CARACT_EXPLOTACION));
				cobertura.setCodvalor(datosVariables.getCarExpl().getValor() + "");
				cobertura.setAnexoModificacion(anexo);

				lstCoberturas.add(cobertura);
			}
		}
		return lstCoberturas;
	}

	/**
	 * Llama al metodo que comprueba si el AM por cupon caducado correspondiente al
	 * id indicado es editable solicitando un nuevo cupon o no
	 * 
	 * @param idPoliza
	 * @param idAnexo
	 * @return
	 */
	public BigDecimal isEditableAMCuponCaducado(Long idPoliza, Long idAnexo) {
		try {
			return declaracionModificacionPolizaDao.isEditableAMCuponCaducado(idAnexo, idPoliza);
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al comprobar si es editable el anexo con id " + idAnexo, e);
			return new BigDecimal(-1);
		}
	}

	public EstadoCupon getEstadoCupon() throws DAOException {

		return estadoCuponDao.getEstadoCupon(Constants.AM_CUPON_ESTADO_CADUCADO);

	}

	public void setDeclaracionModificacionPolizaDao(
			IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) {
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setSubvDeclaradaDao(ISubvDeclaradaDao subvDeclaradaDao) {
		this.subvDeclaradaDao = subvDeclaradaDao;
	}

	public void setPolizaCopyManager(PolizaCopyManager polizaCopyManager) {
		this.polizaCopyManager = polizaCopyManager;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setHistoricoEstadosManager(IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setLineaDao(ILineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public void setEstadoCuponDao(IEstadoCuponDao estadoCuponDao) {
		this.estadoCuponDao = estadoCuponDao;
	}

	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setCuponDao(ICuponDao cuponDao) {
		this.cuponDao = cuponDao;
	}

	public IDiccionarioDatosDao getDiccionarioDatosDao() {
		return diccionarioDatosDao;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public IAseguradoDao getAseguradoDao() {
		return aseguradoDao;
	}

	public void setAseguradoDao(IAseguradoDao aseguradoDao) {
		this.aseguradoDao = aseguradoDao;
	}

}
