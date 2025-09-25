package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.IDatosParcelaManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.solicitud.ISolicitudModificacionManager;
import com.rsi.agp.core.managers.impl.anexoMod.util.AnexoModificacionUtils;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ParcelaCoberturaComparator;
import com.rsi.agp.core.util.WSRUtils;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.util.XmlTransformerUtil;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.commons.LineasFiltro;
import com.rsi.agp.dao.filters.cpl.ConceptoCubiertoModuloFiltro;
import com.rsi.agp.dao.models.anexo.ICuponDao;
import com.rsi.agp.dao.models.anexo.IDeclaracionModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IParcelaModificacionPolizaDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IPagoPolizaDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ParcelasCoberturasNew;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SWModulosCobParcelaAnexo;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.ConceptoCubiertoVO;
import com.rsi.agp.vo.ParcelaVO;

public class ParcelasModificacionPolizaManager implements IManager {

	private static final Log logger = LogFactory.getLog(ParcelasModificacionPolizaManager.class);
	
	public static final String MODIFICAR_PARCELA = "modificarParcela";
	public static final String ALTA_PARCELA = "altaParcela";
	public static final String MODIFICAR_CAPITAL_ASEGURADO = "modificarCapitalAsegurado";
	public static final String ALTA_CAPITAL_ASEGURADO = "altaCapitalAsegurado";
	private static final String REPLICAR_PARCELA = "replicarParcela";

	private IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao = null;
	private IParcelaModificacionPolizaDao parcelaModificacionPolizaDao = null;
	private IPolizaCopyDao polizaCopyDao = null;
	private IXmlAnexoModificacionDao xmlAnexoModDao = null;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private ISolicitudModificacionManager solicitudModificacionManager;
	private ICuponDao cuponDao;
	private IPolizaDao polizaDao;
	private IDatosParcelaManager datosParcelaManager;
	private DatosParcelaAnexoManager datosParcelaAnexoManager;
	private IPagoPolizaDao pagoPolizaDao;

	public List<CapitalAsegurado> buscarParcelas(CapitalAsegurado capitalAsegurado) throws BusinessException {
		List<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> listaCapitalesAseguradosAnexo = null;
		
		try{
			listaCapitalesAseguradosAnexo = parcelaModificacionPolizaDao.list(capitalAsegurado); 
			
		}
		catch(DAOException dao){
			logger.error("Excepcion : ParcelasModificacionPolizaManager - buscarParcelas", dao);
		}
		catch(Exception ex){
			logger.error("Excepcion : ParcelasModificacionPolizaManager - buscarParcelas", ex);
			throw new BusinessException ("Se ha producido un error buscando las parcelas a mostrar", ex);
		}

		return listaCapitalesAseguradosAnexo;

	}

	public void copiarParcelasFromPolizaOrCopy(Long idAnexoModificacion) throws BusinessException {
		try {

			if (!existenParcelasEnAnexo(idAnexoModificacion)) {
				// DAA 24/07/2012 Optimizacion
				parcelaModificacionPolizaDao.copiarParcelasPolizaCopy(idAnexoModificacion);
			}
		} catch (DAOException dao) {
			logger.error("Error al copiar las parcelas al anexo", dao);
			throw new BusinessException("Se ha producido un error al copiarParcelasPolizaCopy", dao);
		}
	}

	/**
	 * Metodo para cargar las parcelas de un anexo a partir de la situacien
	 * actualizada de agroseguro obtenida junto con la peticien del cupen
	 * 
	 * @param idAnexo
	 *            Identificador interno del anexo de modificacien
	 * @param idCupon
	 *            Identificador interno del cupen asociado al anexo
	 * @throws BusinessException
	 * @throws XmlException
	 */
	public void copiarParcelasFromPolizaActualizada(Long idAnexo, String idCupon, Long lineaseguroid)
			throws BusinessException, XmlException {
		try {
			if (!existenParcelasEnAnexo(idAnexo)) {
				// Obtengo el codigo de cupen de la base de datos
				Cupon cupon = (Cupon) this.cuponDao.get(Cupon.class, Long.parseLong(idCupon));

				/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
				/*
				 * Por los desarrollos de esta peticien tanto las polizas agricolas como las
				 * de ganado iren por el mismo end-point y con formato Unificado
				 */
				// Obtengo la poliza actualizada con el formato unificado
				es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) solicitudModificacionManager
						.getPolizaActualizadaFromCupon(cupon.getIdcupon())).getPoliza();

				// Mapa auxiliar con los codigos de concepto de los datos variables y sus
				// etiquetas y tablas asociadas.
				Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
						.getCodConceptoEtiquetaTablaParcelas(lineaseguroid);

				List<Parcela> parcelas = AnexoModificacionUtils.getParcelasAnexoFromPolizaActualizada(poliza, idAnexo,
						auxEtiquetaTabla);

				this.parcelaModificacionPolizaDao.saveOrUpdateList(parcelas);

				// elimino las nuevas parcelas de la sesien de hibernate
				for (Parcela p : parcelas) {
					this.parcelaModificacionPolizaDao.evict(p);
				}

				// Instalaciones: identificar y asignar la parcela asociada
				this.parcelaModificacionPolizaDao.asignarInstalacionesFromPolizaActualizada(idAnexo);

			}
		} catch (DAOException dao) {
			logger.error("Error al copiar las parcelas al anexo", dao);
			throw new BusinessException("Se ha producido un error al copiarParcelasPolizaCopy", dao);
		}
	}

	public Parcela buscarParcela(Long idParcela) throws BusinessException {
		try {
			return (Parcela) parcelaModificacionPolizaDao.get(Parcela.class, idParcela);
		}
		catch (DAOException dao) {
			logger.error("Excepcion : ParcelasModificacionPolizaManager - buscarParcela", dao);
			throw new BusinessException ("Se ha producido un error buscando una parcela", dao);
		}
	} 
	
    public AnexoModificacion getAnexo(Long idAnexoModificacion) throws BusinessException {
		try{
		    return (AnexoModificacion)parcelaModificacionPolizaDao.get(AnexoModificacion.class,idAnexoModificacion);
		}
		catch (DAOException dao) {
			logger.error("Excepcion : ParcelasModificacionPolizaManager - getAnexo", dao);
			throw new BusinessException ("Se ha producido un error buscar el anexo", dao);
		}
	}

	@SuppressWarnings("unchecked")
	public String getListCodModulos(Long idpoliza) {
		StringBuilder listCodigosModulos = new StringBuilder("");

		try {
			int control = 1;
			Set<String> modulos = new HashSet<String>(parcelaModificacionPolizaDao.getModulosPoliza(idpoliza));

			for (String item : modulos) {
				if (control < modulos.size()) {
					listCodigosModulos.append(item).append(";");
				} else { // el eltimo
					listCodigosModulos.append(item);
				}
				control++;
			}
		} catch (Exception exception) {

		}

		return listCodigosModulos.toString();
	}

	/**
	 * Metodo para obtener la pantalla configuracion de la seccion de datos
	 * variables de poliza para montar la pantalla de parcela
	 * 
	 * @param polizaBean
	 *            Poliza a gestionar.
	 * @return PantallaConfigurable: la pantalla configurable.
	 */
	@SuppressWarnings("rawtypes")
	public PantallaConfigurable getPantallaVarAnexo(Long lineaSeguroId, Long idPantalla) {
		List pantallas = parcelaModificacionPolizaDao.getPantallaVarAnexo(lineaSeguroId, idPantalla);

		if (pantallas.size() > 0)
			return (PantallaConfigurable) pantallas.get(0);
		else
			return null;
	}

	/**
	 * DAA DAA 19/12/2012 Metodo para dar de baja parcelas de los anexos de
	 * modificacien
	 * 
	 * @param idsParcelas
	 *            Identificadores de las parcelas
	 * @param idAnexo
	 *            Identificador del anexo de modificacien
	 * @throws BusinessException
	 */
	public void establecerBajaParcelas(List<Long> idsParcelas, Long idAnexo) throws BusinessException {
		try {
			for (int i = 0; i < idsParcelas.size(); i++) {
				Long id = idsParcelas.get(i);
				bajaParcelaAnexo(idAnexo, id);
			}
		} catch (DAOException dao) {
			logger.error("Error al dar de baja las parcelas del anexo " + idAnexo, dao);
			throw new BusinessException("Se ha producido un error buscando una parcela", dao);
		}
	}

	/**
	 * DAA 19/12/2012 bajaParcelaAnexo Metodo para marcar como baja una parcela de
	 * un anexo. Revisa las instalaciones asociadas a la misma y las reasigna si es
	 * posible o las marca como bajas en caso contrario.
	 * 
	 * @param idAnexo
	 *            Identificador del anexo de modificacien
	 * @param id
	 *            Identificador de la parcela a dar de baja
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void bajaParcelaAnexo(Long idAnexo, Long id) throws DAOException {

		// 1. Obtener la parcela a poner de baja.
		Parcela parcelaBaja = parcelaModificacionPolizaDao.getParcelaAnexo(id);

		// 2. Si es parcela => obtengo la lista de instalaciones de la parcela
		// y la lista de parcelas que coinciden con el SIGPAC y no son instalaciones y
		// no es la que voy a dar de baja.

		if (Constants.TIPO_PARCELA_PARCELA.equals(parcelaBaja.getTipoparcela())) {
			List<Parcela> listInstalaciones = parcelaModificacionPolizaDao.getObjects(Parcela.class,
					"idparcelaanxestructura", id);
			List<Parcela> listaParcelas = parcelaModificacionPolizaDao.getParcelasAnexoMismoSigpac(parcelaBaja,
					idAnexo);

			// 2.1 si hay parcelas q cumplen las condiciones muevo las instalaciones,
			// si no hay => doy de baja o elimino todas las instalaciones
			if (listaParcelas.size() > 0) {
				// 2.2 Cojo la primera y le asigno el idparcelaanxestructura a la lista de
				// instalaciones
				Long idNuevaParcela = listaParcelas.get(0).getId();
				parcelaModificacionPolizaDao.actualizaInstalaciones(listInstalaciones, idNuevaParcela);
			} else {
				try {

					for (int e = 0; e < listInstalaciones.size(); e++) {
						Parcela instalacion = (Parcela) listInstalaciones.get(e);

						if (instalacion.getTipomodificacion() != null
								&& instalacion.getTipomodificacion().equals(Constants.ALTA)) {
							// Si es una instalacion y es un alta nueva --> la borro de la BD
							parcelaModificacionPolizaDao.delete(Parcela.class, instalacion.getId());
						} else {
							// Si es instalacion y no es nuevo alta --> set estado a B
							List<Parcela> listAux = new ArrayList<Parcela>();
							listAux.add(instalacion);
							parcelaModificacionPolizaDao.setEstadoParcelas(listAux, Constants.BAJA);
						}
					}
				} catch (Exception e) {
					throw new DAOException("Se ha producido un error al borrar  las Instalaciones", e);
				}
			}
		}

		// 3. doy de baja la parcela/instalacion
		List<Parcela> listAux = new ArrayList<Parcela>();
		listAux.add(parcelaBaja);
		parcelaModificacionPolizaDao.setEstadoParcelas(listAux, Constants.BAJA);
	}

	public void eliminarParcela(Long idParcela) throws BusinessException {
		try {
			parcelaModificacionPolizaDao.delete(Parcela.class, idParcela);
		} 
		catch (DAOException dao){
			logger.error("Excepcion : ParcelasModificacionPolizaManager - eliminarParcela", dao);
			throw new BusinessException ("Se ha producido un error eliminando una parcela", dao);
		}
	}

	public Parcela guardarParcela(Parcela parcela) throws BusinessException {
		try {
			if (parcela.getId() != null)
				parcela.setTipomodificacion(Constants.MODIFICACION);
			else
				parcela.setTipomodificacion(Constants.ALTA);

			return (Parcela) parcelaModificacionPolizaDao.saveOrUpdate(parcela);
			
		} 
		catch (DAOException dao){
			logger.error("Excepcion : ParcelasModificacionPolizaManager - guardarParcela", dao);
			throw new BusinessException ("Se ha producido un error guardando una parcela", dao);
		}
	}

	/**
	 * Metodo para deshacer los cambios de las parcelas de los anexos de
	 * modificacien
	 * 
	 * @param idAnexo
	 *            Identificador del anexo
	 * @param idsParcelas
	 *            Identificadores de las parcelas a eliminar
	 * @param codUsuario
	 *            Usuario que realiza la operacien
	 * @throws BusinessException
	 * @throws DAOException
	 * @throws ValidacionAnexoModificacionException
	 * @throws XmlException
	 */
	public void deshacerCambiosParcelas(Long idAnexo, List<Long> idsParcelas, String codUsuario)
			throws BusinessException, DAOException, ValidacionAnexoModificacionException, XmlException {

		AnexoModificacion anexo = this.declaracionModificacionPolizaDao.getAnexoModifById(idAnexo);

		if (anexo.getCupon() != null && anexo.getCupon().getId() != null) {
			deshacerCambiosCupon(idsParcelas, anexo);
		} else {
			parcelaModificacionPolizaDao.deshacerParcelas(idsParcelas);
		}

		// Cálculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado");
		logger.debug("Se cargan los CPM permitidos para la poliza y el anexo relacionado - idPoliza: "
				+ anexo.getPoliza().getIdpoliza() + ", idAnexo: " + anexo.getId() + ", codModulo: "
				+ anexo.getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePolizaAnexoMod(anexo.getPoliza().getIdpoliza(),
				anexo.getId(), anexo.getCodmodulo());

		// Actualizamos el xml
		boolean modAseg = false;
		try {
			XmlTransformerUtil.updateXMLAnexoMod(xmlAnexoModDao, polizaCopyDao, anexo, modAseg, listaCPM, false);

		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Error validando el xml de Anexos de Modificacion" + e.getMessage());
			throw new ValidacionAnexoModificacionException(e.getMessage());
		} catch (Exception ee) {
			logger.error("Error generico al deshacerCambiosParcelas" + ee.getMessage());
			throw new BusinessException("Error generico al deshacerCambiosParcelas");
		}

		// TMR mejora 230 actualizamos el asunto del xml
		anexo.setAsunto(XmlTransformerUtil.generarAsuntoAnexo(anexo, false));
		this.declaracionModificacionPolizaDao.saveAnexoModificacion(anexo, codUsuario, null, false);

	}

	/**
	 * Metodo para deshacer los cambios de una parcela en un anexo de tipo "SW"
	 * 
	 * @param idsParcelas
	 *            Identificadores de las parcelas a deshacer
	 * @param anexo
	 *            Anexo de modificacien
	 * @throws DAOException
	 * @throws XmlException
	 */
	@SuppressWarnings("unchecked")
	private void deshacerCambiosCupon(final List<Long> idsParcelas, final AnexoModificacion anexo)
			throws DAOException, XmlException {
		// Si va por cupon => tengo que obtener los datos de la parcela de la
		// situacien actualizada
		// Obtengo la poliza actualizada

		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/*
		 * Por los desarrollos de esta peticien tanto las polizas agricolas como las
		 * de ganado iren por el mismo end-point y con formato Unificado
		 */
		es.agroseguro.contratacion.Poliza poliza = ((es.agroseguro.contratacion.PolizaDocument) this.solicitudModificacionManager
				.getPolizaActualizadaFromCupon(anexo.getCupon().getIdcupon())).getPoliza();

		// Mapa auxiliar con los codigos de concepto de los datos variables y
		// sus etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = this.diccionarioDatosDao
				.getCodConceptoEtiquetaTablaParcelas(anexo.getPoliza().getLinea().getLineaseguroid());
		// Recupero la parcela buscando en la situacien actualizada por hoja y
		// nemero
		for (Long idParcela : idsParcelas) {
			Parcela parcelaOld = this.parcelaModificacionPolizaDao.getParcelaAnexo(idParcela);
			// Obtengo las instalaciones asociadas a la parcela
			List<Parcela> listInstalaciones = this.parcelaModificacionPolizaDao.getObjects(Parcela.class,
					"idparcelaanxestructura", parcelaOld.getId());
			if (parcelaOld.getTipomodificacion().equals(Constants.ALTA)) {
				// Si es una parcela nueva
				// eliminar sus instalaciones si las tiene
				for (Parcela instalacion : listInstalaciones) {
					this.parcelaModificacionPolizaDao.evict(instalacion);
					this.parcelaModificacionPolizaDao.delete(instalacion);
				}
				this.parcelaModificacionPolizaDao.evict(parcelaOld);
				this.parcelaModificacionPolizaDao.delete(parcelaOld); // eliminar
																		// parcela
																		// del
																		// anexo
				// Renumeramos por si hay parcelas de alta que tengan la
				// numeracion mayor que la que estamos eliminando
				this.parcelaModificacionPolizaDao.renumerarHoja(parcelaOld);
			} else {
				// Recupero los datos de la parcela original y los de sus
				// instalaciones
				// Instalaciones
				for (Parcela instalacion : listInstalaciones) {
					recuperarParcelaFromSituacionActualizada(anexo, poliza, auxEtiquetaTabla, instalacion);
				}
				recuperarParcelaFromSituacionActualizada(anexo, poliza, auxEtiquetaTabla, parcelaOld);
			}
		}
	}

	/**
	 * Metodo para recuperar los datos de una parcela de la situacien
	 * actualizada y eliminar los datos de la parcela del anexo
	 * 
	 * @param anexo
	 * @param poliza
	 * @param auxEtiquetaTabla
	 * @param parcelaOld
	 * @throws DAOException
	 * @throws XmlException
	 */
	private void recuperarParcelaFromSituacionActualizada(AnexoModificacion anexo,
			es.agroseguro.contratacion.Poliza poliza, Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			Parcela parcelaOld) throws DAOException, XmlException {

		// Recupero los datos de la parcela original
		Parcela parcelaNew = AnexoModificacionUtils.getParcelaAnexoFromPolizaActualizada(poliza, anexo.getId(),
				auxEtiquetaTabla, parcelaOld.getHoja(), parcelaOld.getNumero());

		// Elimino la parcela modificada
		this.parcelaModificacionPolizaDao.delete(parcelaOld);

		// Inserto la parcela de la situacien actualizada
		/* ESC-15973 ** MODIF TAM (18.11.2021) ** Inicio */
		if (parcelaNew != null) {
			this.parcelaModificacionPolizaDao.saveOrUpdate(parcelaNew);
			this.parcelaModificacionPolizaDao.evict(parcelaNew);
		}
		/* ESC-15973 ** MODIF TAM (18.11.2021) ** Fin */
	}

	/**
	 * Metodo para comprobar si un anexo tiene parcelas.
	 * 
	 * @param idAnexoModificacion
	 *            Identificador del anexo
	 * @return True si tiene parcelas, false en caso contrario.
	 * @throws BusinessException
	 */
	public boolean existenParcelasEnAnexo(Long idAnexoModificacion) throws BusinessException {
		boolean exist = false;

		try {
			exist = parcelaModificacionPolizaDao.existenParcelasEnAnexo(idAnexoModificacion);
		} 
		catch (DAOException dao){
			logger.error("Excepcion : ParcelasModificacionPolizaManager - existenParcelasEnAnexo", dao);
			throw new BusinessException ("Se ha producido un error al consultar si existen parcelas en un anexo", dao);
			
		}
		return exist;
	}

	/**
	 * Obtiene la lista de parcelas del anexo.
	 * 
	 * @param Parcela
	 *            por la que filtrar
	 * @return
	 * @throws BusinessException
	 */
	public List<Parcela> getParcelasAnexo(Parcela parcela) throws BusinessException {
		List<Parcela> listParcelasAnexo = null;
		try {
			listParcelasAnexo = parcelaModificacionPolizaDao.getParcelasAnexo(parcela);

		} catch (DAOException dao) {
			throw new BusinessException("Se ha producido un error al obtener el listado de parcelas del anexo", dao);
		}
		return listParcelasAnexo;
	}

	/**
	 * Obtiene la lista de parcelas del anexo.
	 * 
	 * @param Parcela
	 *            por la que filtrar
	 * @return
	 * @throws BusinessException
	 */
	public List<CapitalAsegurado> getParcelasAnexo(CapitalAsegurado capitalAseguradoModificadaBean, String columna,
			String orden) throws BusinessException {
		List<Parcela> listParcelasAnexo = null;
		List<CapitalAsegurado> listCapitalesAnexo = new ArrayList<CapitalAsegurado>();
		try {
			listParcelasAnexo = parcelaModificacionPolizaDao.getParcelasAnexo(capitalAseguradoModificadaBean, columna,
					orden);

			// Lista de ids de parcela cuyos capitales asegurados ya se han incluido en la
			// lista a devolver
			List<Long> listaIds = new ArrayList<Long>();

			// Recorro la lista de parcelas y monto una lista de capitales asegurados
			for (Parcela par : listParcelasAnexo) {

				// Si esta parcela ya se ha tratado previamente, va a la siguiente iteracien
				if (listaIds.contains(par.getId()))
					continue;
				else
					listaIds.add(par.getId());

				CapitalAsegurado ca = new CapitalAsegurado();
				if (par.getCapitalAsegurados() != null && !par.getCapitalAsegurados().isEmpty()) {
					for (CapitalAsegurado ca_aux : par.getCapitalAsegurados()) {
						ca = new CapitalAsegurado(ca_aux.getId(), ca_aux.getParcela(), ca_aux.getTipoCapital(),
								ca_aux.getSuperficie(), ca_aux.getPrecio(), ca_aux.getProduccion(),
								ca_aux.getAltaencomplementario(), ca_aux.getIncrementoproduccion(),
								ca_aux.getTipomodificacion(), ca_aux.getIncrementoproduccionanterior(),
								ca_aux.getValorincremento(), ca_aux.getTipoincremento(),
								ca_aux.getCapitalDTSVariables(), ca_aux.getTipoRdto());
						listCapitalesAnexo.add(ca);
					}
				} else {
					// Falta el capital asegurado en la parcela => le pongo uno nulo
					ca.setParcela(par);
					listCapitalesAnexo.add(ca);
				}
			}
		} catch (DAOException dao) {
			logger.error("Error al obtener las parcelas del anexo", dao);
			throw new BusinessException("Se ha producido un error al obtener el listado de parcelas del anexo", dao);
		}
		return listCapitalesAnexo;
	}

	public List<CapitalAsegurado> ordenarCapitalesAnexo(List<CapitalAsegurado> listCapitalesAnexo) {
		List<CapitalAsegurado> listParcelasAux1 = new ArrayList<CapitalAsegurado>();
		List<CapitalAsegurado> listParcelasAux2 = new ArrayList<CapitalAsegurado>();

		// Creo array con solo capitalAsegurados que no sean instalaciones
		if (listCapitalesAnexo != null) {
			for (int i = 0; i < listCapitalesAnexo.size(); i++) {
				if (listCapitalesAnexo.get(i).getParcela().getTipoparcela().toString().equals("P")) {
					listParcelasAux1.add(listCapitalesAnexo.get(i));
				}
			}

			for (int i = 0; i < listParcelasAux1.size(); i++) {
				listParcelasAux2.add(listParcelasAux1.get(i));

				for (int e = 0; e < listCapitalesAnexo.size(); e++) {
					if (listCapitalesAnexo.get(e).getParcela().getTipoparcela().toString().equals("E")) {
						if (listCapitalesAnexo.get(e).getParcela().getIdparcelaanxestructura() != null
								&& listCapitalesAnexo.get(e).getParcela().getIdparcelaanxestructura().toString()
										.equals(listParcelasAux1.get(i).getParcela().getId().toString())) {
							listParcelasAux2.add(listCapitalesAnexo.get(e));
						}
					}
				}
			}
		}

		return listParcelasAux2;
	}

	@SuppressWarnings("unchecked")
	public final Long getLineaseguroId(final BigDecimal codplan, final BigDecimal codlinea) {
		LineasFiltro filtroLinea = new LineasFiltro(codplan, codlinea);

		List<Linea> lineas = declaracionModificacionPolizaDao.getObjects(filtroLinea);

		if (!lineas.isEmpty())
			return lineas.get(0).getLineaseguroid();

		return null;
	}

	public boolean isInstalacion(String idParcela) {
		Parcela parcela;
		boolean result = false;
		try {
			if (idParcela != null && !idParcela.equals("")) {
				parcela = (Parcela) declaracionModificacionPolizaDao.get(Parcela.class, new Long(idParcela));

				if (parcela.getTipoparcela().toString().equals("E"))
					result = true;
				else
					result = false;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
	@SuppressWarnings("unchecked")
	public final List<ParcelasCoberturasNew> getCoberturasParcela(com.rsi.agp.dao.tables.anexo.Parcela parcAnx,
			ParcelaVO parcelaVO, String codUsuario, String realPath, Set<CapitalDTSVariable> cobParcExistentes,
			Long idAnexo) throws BusinessException {
		
		logger.info("getCoberturasParcela [INIT]");
		
		List<ParcelasCoberturasNew> lstCobParcelas = new ArrayList<ParcelasCoberturasNew>();
		
		if (!StringUtils.isNullOrEmpty(parcelaVO.getCodParcela())){
			List<CapitalAsegurado> capitalAsegurado = parcelaModificacionPolizaDao.getObjects(CapitalAsegurado.class, "parcela.id", Long.parseLong(parcelaVO.getCodParcela()));
			
			Set<CapitalDTSVariable> cobParcExist = new HashSet<CapitalDTSVariable>(0); 
					 
			for (CapitalAsegurado cap: capitalAsegurado) {
				for (CapitalDTSVariable dtsVar: cap.getCapitalDTSVariables()) {
					cobParcExist.add(dtsVar);
				}
			}
			
			cobParcExistentes = cobParcExist;
						
		}
		
		for (CapitalDTSVariable dtsVar: cobParcExistentes) {
			logger.debug("[ESC-26788] Cob Existente, concepto: " + dtsVar.getCodconcepto() + ", valor: " + dtsVar.getValor());
		}
		
		String xmlRespuesta = null;
		
		try {
			
			AnexoModificacion anexo = declaracionModificacionPolizaDao.getAnexoModifById(idAnexo);
			
			if (parcelaVO.getCapitalAsegurado().getCodtipoCapital().equals("1") && parcAnx.getId()!= null){
				@SuppressWarnings("unused")
				Parcela parcelaAux = (Parcela) parcelaModificacionPolizaDao.getObjects(Parcela.class, "parcela.idparcela", parcAnx.getId());
			}
			
			// Obtengo TODOS los riesgos elegibles, sin condicion citricos, y los pongo como no elegidos si la parcela no lo tiene ya
			List<ConceptoCubiertoVO> listConceptosCubiertos = new ArrayList<ConceptoCubiertoVO>();
			
			List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = this.parcelaModificacionPolizaDao
					.getRiesgosCubiertosModuloAnx(anexo.getPoliza().getLinea().getLineaseguroid(), anexo.getCodmodulo(),
							null);
			
			ConceptoCubiertoModuloFiltro filtroConcCbrto = new ConceptoCubiertoModuloFiltro(anexo.getPoliza().getLinea().getLineaseguroid(), anexo.getCodmodulo());
			List<ConceptoCubiertoModulo> lstConcCbrtoMod = parcelaModificacionPolizaDao.getObjects(filtroConcCbrto);
			
			
			for(ConceptoCubiertoModulo ccm : lstConcCbrtoMod){
				if(!this.datosParcelaManager.existInListConceptoCubiertoVO(listConceptosCubiertos,ccm)){
					ConceptoCubiertoVO cc = new ConceptoCubiertoVO();
					cc.setDesConcepto(ccm.getDiccionarioDatos().getNomconcepto());                    // descripcion
					cc.setId(ccm.getDiccionarioDatos().getCodconcepto().toString());                  // codigo
					cc.setNumeroColumna(Integer.parseInt(ccm.getId().getColumnamodulo().toString())); // columna
					listConceptosCubiertos.add(cc);
				}
			}//for
			
			// Si no hay Riesgos Cubiertos para el modulo no se lanza consulta al SW de Modulos y Coberturas
			if (lstRiesgoCbrtoMod.size() > 0) {
				
				logger.debug("** Hay Riesgos Cubiertos para el modulo: -" + anexo.getCodmodulo() + '-');
				
				Collections.sort(listConceptosCubiertos); // reordenar list conceptos cubiertos
				
				Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
				try {
					listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(anexo);
				} catch (BusinessException e1) {
					logger.error(
							"Error al obtener la lista de datos variables dependientes del riesgo y cpm",
							e1);
				}
				
				String xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgriAnx(anexo, anexo.getPoliza(), null, parcAnx, anexo.getParcelas(), anexo.getCodmodulo(), getPolizaDao(), listaDatosVariables);
				
				// guardar llamada al WS en BBDD
				String idParc = (parcAnx.getId() != null ? parcAnx.getId().toString() : "");
				
				if (StringUtils.isNullOrEmpty(idParc)) {
					idParc = parcelaVO.getCodParcela();
				}
				
				Long idEnvio = guardarXmlEnvioParcAnx(idAnexo, idParc, anexo.getCodmodulo(), xmlPoliza, codUsuario);
				
				es.agroseguro.modulosYCoberturas.Modulo ModulosCoberturasXmlRespuesta = WSRUtils.getModulosCoberturas(xmlPoliza);
				xmlRespuesta = ModulosCoberturasXmlRespuesta.toString();

				if (null != ModulosCoberturasXmlRespuesta) {
					xmlRespuesta =  ModulosCoberturasXmlRespuesta.toString();
				}

				// guardar respuesta al WS en BBDD
				if (xmlRespuesta != null) {
					parcelaModificacionPolizaDao.actualizaXmlCoberturasParcAnx(idEnvio, "", xmlRespuesta);
				}
				
				logger.debug("Respuesta WS: " + xmlRespuesta);
				// Transformarmos las coberturas que nos vienen del WS en una lista de
				// explotacionesCoberturas
				
				es.agroseguro.modulosYCoberturas.ModulosYCoberturas modYCob = this.datosParcelaManager.getMyCFromXml(xmlRespuesta);
					
					
				if (modYCob.getParcelas() != null) {
					es.agroseguro.modulosYCoberturas.Parcelas parcelas = modYCob.getParcelas();
					es.agroseguro.modulosYCoberturas.Parcela[] parcelaArray = parcelas.getParcelaArray();
					Long contador = new Long(2000000);
					Long nuevas = new Long(1);
					
					for (es.agroseguro.modulosYCoberturas.Parcela parc : parcelaArray) {
						es.agroseguro.modulosYCoberturas.TipoCapitalAgricola[] tipoCapitalAgri = parc.getTipoCapitalArray();
						
						for (es.agroseguro.modulosYCoberturas.TipoCapitalAgricola tipCapAgri : tipoCapitalAgri) {
							
							es.agroseguro.modulosYCoberturas.Cobertura[] cobb = tipCapAgri.getCoberturaArray();
						
							if (cobb.length > 0) {
								List<es.agroseguro.modulosYCoberturas.Cobertura> lstCobReg = Arrays.asList(cobb);
								
								for (es.agroseguro.modulosYCoberturas.Cobertura cobertura : lstCobReg) {
									
									/* Comprobamos si el ConceptoPpal del Modulo y el Riesgo Cubierto estan en la lista de los permitidos */
									if (this.datosParcelaManager.existInListRiesgos(lstRiesgoCbrtoMod, cobertura.getConceptoPrincipalModulo(), cobertura.getRiesgoCubierto())) {
									
										ParcelasCoberturasNew parcCob = new ParcelasCoberturasNew();
										
										parcCob.setId(contador);
										parcCob.setCodmodulo(anexo.getCodmodulo());
										parcCob.setDescModulo(anexo.getCodmodulo());
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
															+ fi.getElegida().toString().charAt(0) + "." + anexo.getCodmodulo() + ".." + "|";
												}
											}
											if (!vinculadas.equals("")) {
												vinculadas = vinculadas.substring(0, vinculadas.length() - 1);
											}
											parcCob.setVinculada(vinculadas);
										}
										
										lstCobParcelas.add(parcCob);
										contador++;
										nuevas++;
										
										// fin coberturas datos variables
										 
										// Comprobamos si la cobertura tiene datos variables. Si son de tipo E se
										// guardan como tipo cobertura
										es.agroseguro.modulosYCoberturas.DatoVariable[] datArr = cobertura.getDatoVariableArray();
										if (datArr.length > 0) {
											List<es.agroseguro.modulosYCoberturas.DatoVariable> lstDatVar = Arrays.asList(datArr);
											
											for (es.agroseguro.modulosYCoberturas.DatoVariable datVar : lstDatVar) {
												
												String codConceptoStr = String.valueOf(datVar.getCodigoConcepto());
												
												// recogemos los datosvariables con tipovalor a 'E' y que estan en la lista de Conceptos cubiertos.
												if (this.datosParcelaManager.existInListConcepto(listConceptosCubiertos, codConceptoStr) && (datVar.getTipoValor() != null && datVar.getTipoValor().equals("E"))) { 
																															// E
													// CARGA DATO VAR COMO COBERTURAS
	
													es.agroseguro.modulosYCoberturas.Valor[] valArr = datVar.getValorArray();
													List<es.agroseguro.modulosYCoberturas.Valor> lstValores = Arrays.asList(valArr);
													
													for (es.agroseguro.modulosYCoberturas.Valor valor : lstValores) {
														
														parcCob = new ParcelasCoberturasNew();
														parcCob.setId(contador);
														parcCob.setCodmodulo(anexo.getCodmodulo());
														parcCob.setFila(cobertura.getFila());
														parcCob.setCpm(cobertura.getConceptoPrincipalModulo());
														parcCob.setCpmDescripcion(cobertura.getDescripcionCPM());
														parcCob.setRiesgoCubierto(cobertura.getRiesgoCubierto());
														parcCob.setRcDescripcion(cobertura.getDescripcionRC());
														parcCob.setElegible(cobertura.getElegible().toString().charAt(0));
														
														parcCob.setDvCodConcepto(new Long(datVar.getCodigoConcepto()));
														parcCob.setDvDescripcion(datVar.getNombre());
														parcCob.setDvValor(valor.getValor());
														parcCob.setDvValorDescripcion(valor.getDescripcion());
														parcCob.setDvColumna(new Long(datVar.getColumna()));
	
														// coberturas datos variables
														String vinculadas = "";
														
														if (valor.getVinculacionCelda() != null) {
															es.agroseguro.modulosYCoberturas.VinculacionCelda vinCelda = valor.getVinculacionCelda();
															
															String filaMadre = String.valueOf(vinCelda.getFilaMadre());
															String columnaMadre = String.valueOf(vinCelda.getColumnaMadre());
															String valorMadre =  String.valueOf(vinCelda.getValorMadre());
														
															vinculadas += "X" + "." + filaMadre + "." + "X" + "."
																	+ anexo.getCodmodulo() + "." + columnaMadre + "." + valorMadre + "." +codConceptoStr ;

															parcCob.setVinculada(vinculadas);
														}
														// fin coberturas datos variables
	
														lstCobParcelas.add(parcCob);
														contador++;
														nuevas++;
													} /* Fin del for de valores */
												} /* Fin del if de TipoValor */
											} /* Fin del for de DatosVariables*/
										} /* Fin del if datoArray*/
									} /* Fin del if de ConcptoPpalMod y CodRiesgoCub*/
	
								} /* Fin del For de Coberturas */
							}
						}
					}
				} else {
					logger.debug("el SW no devuelve coberturas de Parcelas.");
				}
			} else {
				logger.debug("** NO HAY Riesgos Cubiertos para el modulo: -" + anexo.getCodmodulo() + '-');
				
			}/* Fin del if de lstRiesgoCbrtoMod */
		} catch (Exception e){
			logger.error("Ha habido un error al obtener las coberturas de Parcelas en Anexos", e);
		}		
		
		// comprobamos si alguna de las que hay ya estaban seleccionadas
		if (cobParcExistentes.size() > 0) {
			
			/* Tratamos coberturas */
			for (CapitalDTSVariable cobExist: cobParcExistentes){
				
				if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO).equals(cobExist.getCodconcepto())){
					
					BigDecimal cpm = BigDecimal.valueOf(cobExist.getCodconceptoppalmod());
					BigDecimal rc = BigDecimal.valueOf(cobExist.getCodriesgocubierto());
					
					for (ParcelasCoberturasNew cob : lstCobParcelas) {
						
						BigDecimal valCpm = BigDecimal.valueOf(cob.getCpm());
						BigDecimal valRc = BigDecimal.valueOf(cob.getRiesgoCubierto());
						logger.debug ("Tratando riesgos contratados");
						logger.debug ("Valor de cpm: "+cpm + " y valor de valCpm:" +valCpm);
						logger.debug ("Valor de rc: "+rc + " y valor de valRc:" +valRc);
						if (cob.getDvCodConcepto() == null) {
							if ( cpm.equals(valCpm) && rc.equals(valRc)){
								if (Constants.RIESGO_ELEGIDO_SI.equals(cobExist.getValor())) {
									cob.setElegida('S');
									break;
								} else {
									cob.setElegida('N');
									break;
								}
							}
						}	
					}		
				}
			} 
		
			/* Tratamos Datos Variables de las coberturas*/
			for (CapitalDTSVariable cobExist : cobParcExistentes) {

				if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO)
						.equals(cobExist.getCodconcepto())) {

					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(cobExist.getCodconcepto())
							|| BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE)
									.equals(cobExist.getCodconcepto())) {

						for (ParcelasCoberturasNew cob : lstCobParcelas) {

							if (cob.getDvCodConcepto() != null && cob.getDvCodConcepto().compareTo(cobExist.getCodconcepto().longValue()) == 0) {
									cob.setDvElegido('S');
							}
						}
					}
				} else {
					logger.debug("[ESC-26788] Tratando DV elegible");
					for (ParcelasCoberturasNew cob : lstCobParcelas) {
						logger.debug("[ESC-26788] cob.getDvCodConcepto(): " + cob.getDvCodConcepto());
						if (cob.getDvCodConcepto() != null && cob.getDvCodConcepto().compareTo(cobExist.getCodconcepto().longValue()) == 0) {
							logger.debug("[ESC-26788] cob.getDvValor(): " + cob.getDvValor() + ", cobExist.getValor(): " + cobExist.getValor());
							if (cobExist.getValor().equals(cob.getDvValor())) {
								cob.setDvElegido('S');
							}							
						}
					}
				}
			} /* Fin del For */
		} 
	
		Collections.sort(lstCobParcelas, new ParcelaCoberturaComparator());
		logger.info("getCoberturasParcela [END]");
		return lstCobParcelas;
		
	}
	
	private Long guardarXmlEnvioParcAnx(Long idanexo, String idParc, String codmodulo, String envio, String codUsuario ) throws DAOException {
		
		logger.debug("ParcelasModificacionManager - guardarXmlEnvio [INIT]");
		
		SWModulosCobParcelaAnexo doc = new SWModulosCobParcelaAnexo();
		
		doc.setIdanexo(idanexo);
		doc.setFecha((new GregorianCalendar()).getTime());

		doc.setEnvio(Hibernate.createClob(envio));
		doc.setRespuesta(Hibernate.createClob(" "));
		if (!idParc.equals(""))
			doc.setIdParcelaAnexo(Long.parseLong(idParc));
		doc.setCodmodulo(codmodulo);
		doc.setUsuario(codUsuario);

		SWModulosCobParcelaAnexo newEnvio = (SWModulosCobParcelaAnexo) parcelaModificacionPolizaDao.saveEnvioCobParcelaAnx(doc);
				
		logger.debug("ParcelasModificacionManager - guardarXmlEnvio [END]");
		return newEnvio.getId();
	}
	
public String[] actualizarParcelasCoberturas (ParcelaVO parcelaVO, String codUsuario, Long idPoliza) throws BusinessException{
		
		List<String> errorMsgs = new ArrayList<String>();
		
		logger.info("Init - DatosParcelaManager - getCoberturasParcelaNew");
		String xmlRespuesta = null;
		
		try {
			
			Long idAnexo = Long.parseLong(parcelaVO.getIdAnexoModificacion());
			AnexoModificacion anexo = declaracionModificacionPolizaDao.getAnexoModifById(idAnexo);
			
			com.rsi.agp.dao.tables.anexo.Parcela parcAnx = new com.rsi.agp.dao.tables.anexo.Parcela(); 
			this.setParcelaAnxVO(parcelaVO, parcAnx, "altaParcela", anexo);

			Map<BigDecimal, List<String>> listaDatosVariables = new HashMap<BigDecimal, List<String>>();
			try {
				listaDatosVariables = xmlAnexoModDao.getDatosVariablesParcelaRiesgo(anexo);
			} catch (BusinessException e1) {
				logger.error(
						"Error al obtener la lista de datos variables dependientes del riesgo y cpm",
						e1);
			}
				
			List<RiesgoCubiertoModulo> lstRiesgoCbrtoMod = this.parcelaModificacionPolizaDao
					.getRiesgosCubiertosModuloAnx(anexo.getPoliza().getLinea().getLineaseguroid(), anexo.getCodmodulo(),
							new Character('D'));
			
			// Si no hay Riesgos Cubiertos para el modulo no se lanza consulta al SW de Modulos y Coberturas
			if (lstRiesgoCbrtoMod.size() > 0) {
				
				logger.debug("** Hay Riesgos Cubiertos para el modulo: -" + anexo.getCodmodulo() + '-');
				/****/
				String xmlPoliza = WSUtils.generateXMLPolizaModulosCoberturasAgriAnx(anexo, anexo.getPoliza(), null,
						parcAnx, anexo.getParcelas(), anexo.getCodmodulo(), getPolizaDao(), listaDatosVariables);
				
				// guardar llamada al WS en BBDD
				String idParc = (parcAnx.getId() != null ? parcAnx.getId().toString() : "");
				
				if (StringUtils.isNullOrEmpty(idParc)) {
					idParc = parcelaVO.getCodParcela();
				}
				
				boolean guardar = false;
				if ("0".equals(parcelaVO.getCapitalAsegurado().getCodtipoCapital())){
					guardar = true;
				}
				
				Long idEnvio = new Long(0);
				if (guardar) {
					idEnvio = guardarXmlEnvioParcAnx(idAnexo, idParc, anexo.getCodmodulo(), xmlPoliza, codUsuario);
					logger.debug("end - generateAndSaveXMLPolizaCpl");
				}
				
				es.agroseguro.modulosYCoberturas.Modulo modulosCoberturasXmlRespuesta = WSRUtils.getModulosCoberturas(xmlPoliza);
				xmlRespuesta = modulosCoberturasXmlRespuesta.toString();

				if (null != modulosCoberturasXmlRespuesta) {
					xmlRespuesta =  modulosCoberturasXmlRespuesta.toString();
				}

				// guardar respuesta al WS en BBDD
				if (xmlRespuesta != null && guardar) {
					parcelaModificacionPolizaDao.actualizaXmlCoberturasParcAnx(idEnvio, "", xmlRespuesta);
				}
				
			}else{
				logger.debug("** NO HAY Riesgos Cubiertos para el modulo: -" + anexo.getCodmodulo() + '-');
				
			}/* Fin del if de lstRiesgoCbrtoMod */
		} catch (Exception e){
			logger.error("Se ha producio un error al actualizar SW Parcelas Coberturas", e);
			errorMsgs.add("Se ha producido un error al actualizar Parcelas Coberturas.");
		}
		return errorMsgs.toArray(new String[] {});
		
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
	public void setParcelaAnxVO(ParcelaVO parcelaVO, com.rsi.agp.dao.tables.anexo.Parcela parcela, String operacion, AnexoModificacion anexoModificacion) {
	
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
			AnexoModificacion anexoModificacionAux = (AnexoModificacion) parcelaModificacionPolizaDao.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
			this.datosParcelaAnexoManager.getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
		} else if (operacion.equals(DatosParcelaAnexoManager.ALTA_ESTRUCTURA_PARCELA)) {
			AnexoModificacion anexoModificacionAux = (AnexoModificacion) parcelaModificacionPolizaDao
					.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
			this.datosParcelaAnexoManager.getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
		} else if (operacion.equals(REPLICAR_PARCELA)) {
			// JANV 06/04/2016
			// se anhade calculo de hoja-numero a replicar.
			// si se coge por defecto la hoja-num anterior, aparece errores si se ha
			// modificado la parcela
			// ya que no se recalcula segun prov,term y subterm.
			Parcela par = (Parcela) parcelaModificacionPolizaDao.getObject(Parcela.class, parcela.getId());
			if (par.getCodprovincia().compareTo(parcela.getCodprovincia()) != 0
					|| par.getCodtermino().compareTo(parcela.getCodtermino()) != 0
					|| !par.getSubtermino().equals(parcela.getSubtermino())) {
				AnexoModificacion anexoModificacionAux = (AnexoModificacion) parcelaModificacionPolizaDao
						.getObject(AnexoModificacion.class, parcela.getAnexoModificacion().getId());
				this.datosParcelaAnexoManager.getHojaNumero(anexoModificacionAux, parcela, parcelaVO, operacion);
			}
		} else {
			parcela.setHoja(new BigDecimal(-1));
			parcela.setNumero(new BigDecimal(-1));
		}
		
		/*** Capital Asegurado ***/
		CapitalAsegurado capitalAsegurado = null;
		
		CapitalAseguradoVO capAsegVO = parcelaVO.getCapitalAsegurado();
		capitalAsegurado = this.datosParcelaAnexoManager.generateCapitalAseguradoDeAnexo(capAsegVO, parcela, operacion,	parcelaVO);

		Set<CapitalAsegurado> capAsegAnx = new HashSet<CapitalAsegurado>();
		capAsegAnx.add(capitalAsegurado);
		
		parcela.setCapitalAsegurados(capAsegAnx);		
		
	}
	
	/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Inicio */
	public boolean obtenerLineaSup2021(BigDecimal codPlan, BigDecimal codLinea, boolean isLineaGanado) {
		boolean lineaSup2021 = false;
		try {
			lineaSup2021 = pagoPolizaDao.lineaContratacion2021(codPlan, codLinea, isLineaGanado);
		} catch (Exception e) {
			logger.error(
					"PolizaUnificadTransformer.java - Se ha producido un erroral recuperar la lineacontratacion" + e);
		}
		return lineaSup2021;
	}			
	/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (23/02/2021) * Fin */
	
	/* Pet. 78691 ** MODIF TAM (21.12.2021) ** Inicio */
	/**
	 * Recorre las lista de parcelas de Anexos filtradas y devuelve una nueva
	 * lista filtrada ademas por sistema de cultivo
	 * 
	 * @param ListCapitalAsegurado
	 * @param sistcultivo
	 * @return ListCapitalAsegurado
	 */
	public List<CapitalAsegurado> getParcelasAnxFiltradas(List<CapitalAsegurado> listaparcelas, String sistcultivo) {
		
		List<CapitalAsegurado> listaParcelasFinal = new ArrayList<CapitalAsegurado>();

		logger.debug("**@@** ParcelasModificacionPolizaManager - getParcelasAnxFiltradas [INIT]");

		// Si se ha filtrado el sistema de cultivo por algun valor
		if (!"".equals(StringUtils.nullToString(sistcultivo))) {

			/** SONAR Q ** MODIF TAM (28.10.2021) ** Inicio **/
			/* Sacamos codigo fuera para descargar la funcion de ifs/fors */
			listaParcelasFinal = obtenerListParcAnxFinal(listaparcelas, sistcultivo);
			/** SONAR Q ** MODIF TAM (28.10.2021) ** Final **/

			return listaParcelasFinal;
		}
		// Si no filtro por sistcultivo devuelvo el listado de parcelas original
		else {
			return listaparcelas;
		}
	}
	
	private List<CapitalAsegurado> obtenerListParcAnxFinal(List<CapitalAsegurado> listparcAnx, String sistcultivo) {

		List<CapitalAsegurado> listParcFinal = new ArrayList<CapitalAsegurado>();
		
		for (CapitalAsegurado cap : listparcAnx) {
			for (CapitalDTSVariable datvar : cap.getCapitalDTSVariables()) {
				// Si la parcela tiene "Sistema de cultivo"
				if (datvar.getCodconcepto()
						.equals(new BigDecimal(ConstantsConceptos.CODCPTO_SISTCULTIVO))) {
					logger.debug("La parcela de Anexotiene sistema de cultivo");

					// Si el sistema de cultivo es igual que el
					// introducido en el filtro de busqueda se inserta
					// la parcela en la lista
					if (datvar.getValor().equals(sistcultivo)) {
						// Si la lista final no tiene la parcela
						// encontrada la meto
						if (!listParcFinal.contains(cap)) {
							listParcFinal.add(cap);
						}
					}
				}
			}
		}
		
		return listParcFinal;
	}
	
	public String obtenerDescSistCultivo(String sistCultivo) throws DAOException {
		return parcelaModificacionPolizaDao.getdescSistCultivo(sistCultivo);
	}
	/* Pet. 78691 ** MODIF TAM (21.12.2021) ** Fin */

	
	public Parcela getParcelaAnx(Long idAnexo) {
		return parcelaModificacionPolizaDao.getParcelaAnexo(idAnexo);
	}
	
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Fin */

	public void setDeclaracionModificacionPolizaDao(
			IDeclaracionModificacionPolizaDao declaracionModificacionPolizaDao) {
		this.declaracionModificacionPolizaDao = declaracionModificacionPolizaDao;
	}

	public void setParcelaModificacionPolizaDao(IParcelaModificacionPolizaDao parcelaModificacionPolizaDao) {
		this.parcelaModificacionPolizaDao = parcelaModificacionPolizaDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setSolicitudModificacionManager(ISolicitudModificacionManager solicitudModificacionManager) {
		this.solicitudModificacionManager = solicitudModificacionManager;
	}

	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}

	public void setCuponDao(ICuponDao cuponDao) {
		this.cuponDao = cuponDao;
	}
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Inicio */
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
	
	public void setDatosParcelaManager(IDatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}
	
	public IPolizaDao getPolizaDao() {
		return polizaDao;
	}
	
	public void setDatosParcelaAnexoManager(DatosParcelaAnexoManager datosParcelaAnexoManager) {
		this.datosParcelaAnexoManager = datosParcelaAnexoManager;
	}
	
	public void setPagoPolizaDao(IPagoPolizaDao pagoPolizaDao) {
		this.pagoPolizaDao = pagoPolizaDao;
	}
	/* Pet.50776_63485-Fase II ** MODIF TAM (17.11.2020) ** Fin */
}
