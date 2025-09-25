package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.ParcelaUtil;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.config.CampoMascaraFiltro;
import com.rsi.agp.dao.filters.cpl.LimiteRendimientoFiltro;
import com.rsi.agp.dao.filters.cpl.ReduccionRendimientosAmbitosFiltro;
import com.rsi.agp.dao.filters.poliza.MedidaFiltro;
import com.rsi.agp.dao.filters.poliza.PrecioFiltro;
import com.rsi.agp.dao.models.anexo.IParcelaAMSWRendimientosDao;
import com.rsi.agp.dao.models.anexo.IXmlAnexoModificacionDao;
import com.rsi.agp.dao.models.copy.IPolizaCopyDao;
import com.rsi.agp.dao.models.cpl.IAseguradoAutorizadoDao;
import com.rsi.agp.dao.models.cpl.IModulosDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.ICalculoPrecioDao;
import com.rsi.agp.dao.models.poliza.ICalculoProduccionDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaAnexoDao;
import com.rsi.agp.dao.models.poliza.IDatosParcelaDao;
import com.rsi.agp.dao.models.poliza.IParcelaSWRendimientosDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalDTSVariable;
import com.rsi.agp.dao.tables.anexo.ParcelaAMSWRendimiento;
import com.rsi.agp.dao.tables.cgen.TipoCapital;
import com.rsi.agp.dao.tables.commons.Limites;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;
import com.rsi.agp.dao.tables.cpl.LimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraLimiteRendimiento;
import com.rsi.agp.dao.tables.cpl.MascaraPrecio;
import com.rsi.agp.dao.tables.cpl.MascaraRendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.Precio;
import com.rsi.agp.dao.tables.cpl.ReduccionRdtoAmbito;
import com.rsi.agp.dao.tables.cpl.RendimientoCaracteristicaEspecifica;
import com.rsi.agp.dao.tables.cpl.Variedad;
import com.rsi.agp.dao.tables.cpl.VariedadId;
import com.rsi.agp.dao.tables.masc.CampoMascara;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.ParcelaSWRendimiento;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoCCAAGanado;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESAGanado;
import com.rsi.agp.dao.tables.poliza.dc2015.BonificacionRecargo2015;
import com.rsi.agp.dao.tables.poliza.dc2015.DistCosteSubvencion2015;
import com.rsi.agp.vo.CapitalAseguradoVO;
import com.rsi.agp.vo.DatoVariableParcelaVO;
import com.rsi.agp.vo.ItemVO;
import com.rsi.agp.vo.ParcelaVO;
import com.rsi.agp.vo.PrecioVO;
import com.rsi.agp.vo.PreciosProduccionesVO;
import com.rsi.agp.vo.ProduccionVO;

import es.agroseguro.seguroAgrario.rendimientosCalculo.ControlParcelaAjustado;
import es.agroseguro.seguroAgrario.rendimientosCalculo.Cosecha;
import es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException;
import es.agroseguro.serviciosweb.contratacionscrendimientos.CalcularRendimientosRequest;
import es.agroseguro.serviciosweb.contratacionscrendimientos.Error;
import es.agroseguro.tipos.AjustarProducciones;

public class CalculoPrecioProduccionManager implements IManager {

	/*** SONAR Q ** MODIF TAM (23.11.2021) ***/
	/** - Se ha eliminado todo el código comentado 
	 * - Se crean metodos nuevos para descargar de ifs/fors 
	 * - Se crean constantes locales nuevas
	 **/

	/** CONSTANTES SONAR Q ** MODIF TAM (23.11.2021) ** Inicio **/
	private final static String UTF8 = "UTF-8";
	private final static String MODULO = "Módulo: ";
	private final static String ERROR = "Error : ";
	private final static String PROD = "getProduccionPrecioWS: ";
	/** CONSTANTES SONAR Q ** MODIF TAM (23.11.2021) ** Fin **/

	private static final Log logger = LogFactory.getLog(CalculoPrecioProduccionManager.class);
	private static final int WS_CODIGO_ERROR_RENDIMIENTO_LIBRE = 1;

	private IParcelaSWRendimientosDao rendParcelaPolizaWSDao;
	private IParcelaAMSWRendimientosDao rendParcelaAnexoWSDao;

	private ICalculoProduccionDao calculoProduccionDao;
	private ICalculoPrecioDao calculoPrecioDao;
	private IAseguradoAutorizadoDao aseguradoAutorizadoDao;
	private IModulosDao modulosDao;

	private IDatosParcelaDao datosParcelaDao;
	private IDatosParcelaAnexoDao datosParcelaAnexoDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;

	private IPolizaDao polizaDao;

	private DatosParcelaManager datosParcelaManager;
	private DatosParcelaAnexoManager datosParcelaAnexoManager;

	private IXmlAnexoModificacionDao xmlAnexoModDao;
	private IPolizaCopyDao polizaCopyDao;

	// SETs
	public void setDatosParcelaManager(DatosParcelaManager datosParcelaManager) {
		this.datosParcelaManager = datosParcelaManager;
	}

	public void setDatosParcelaAnexoManager(DatosParcelaAnexoManager datosParcelaAnexoManager) {
		this.datosParcelaAnexoManager = datosParcelaAnexoManager;
	}

	public void setDatosParcelaAnexoDao(IDatosParcelaAnexoDao datosParcelaAnexoDao) {
		this.datosParcelaAnexoDao = datosParcelaAnexoDao;
	}

	public void setDatosParcelaDao(IDatosParcelaDao datosParcelaDao) {
		this.datosParcelaDao = datosParcelaDao;
	}

	public void setRendParcelaPolizaWSDao(IParcelaSWRendimientosDao rendParcelaPolizaWSDao) {
		this.rendParcelaPolizaWSDao = rendParcelaPolizaWSDao;
	}

	public void setRendParcelaAnexoWSDao(IParcelaAMSWRendimientosDao rendParcelaAnexoWSDao) {
		this.rendParcelaAnexoWSDao = rendParcelaAnexoWSDao;
	}

	public void setCalculoProduccionDao(ICalculoProduccionDao calculoProduccionDao) {
		this.calculoProduccionDao = calculoProduccionDao;
	}

	public void setCalculoPrecioDao(ICalculoPrecioDao calculoPrecioDao) {
		this.calculoPrecioDao = calculoPrecioDao;
	}

	public void setAseguradoAutorizadoDao(IAseguradoAutorizadoDao aseguradoAutorizadoDao) {
		this.aseguradoAutorizadoDao = aseguradoAutorizadoDao;
	}

	public void setModulosDao(IModulosDao modulosDao) {
		this.modulosDao = modulosDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public void setXmlAnexoModDao(IXmlAnexoModificacionDao xmlAnexoModDao) {
		this.xmlAnexoModDao = xmlAnexoModDao;
	}

	public void setPolizaCopyDao(IPolizaCopyDao polizaCopyDao) {
		this.polizaCopyDao = polizaCopyDao;
	}

	// MÉTODOS
	public PreciosProduccionesVO getProduccionPrecio(ParcelaVO parcelaVO, int indiceCapital,
			BigDecimal codConceptoProduccion) throws Exception {
		return this.getProduccionPrecio(parcelaVO, indiceCapital, codConceptoProduccion, false);
	}

	/**
	 * Metodo para obtener la produccion y el precio para un capital asegurado.
	 * 
	 * @param parcelaVO
	 *            Datos del capital asegurado
	 * @param indiceCapital
	 *            Indice del capital asegurado para el que estamos realizando el
	 *            calculo
	 * @param codConceptoProduccion
	 * @param esProduccionConWS
	 *            ¿Usar servicio web para el cálculo de la producción?
	 * @return Objeto con precio y produccion para los datos del capital asegurado.
	 * @throws Exception
	 */
	public PreciosProduccionesVO getProduccionPrecio(ParcelaVO parcelaVO, int indiceCapital,
			BigDecimal codConceptoProduccion, boolean esProduccionConWS) throws Exception {
		GregorianCalendar fechaInicio = new GregorianCalendar();
		Poliza p = new Poliza();
		// Obtenemos todos los datos comunes necesarios para el calculo de produccion y
		// precio.
		List<Modulo> modulos = new ArrayList<Modulo>();
		p = (Poliza) this.calculoProduccionDao.getObject(Poliza.class, new Long(parcelaVO.getCodPoliza()));
		logger.debug("Poliza obtenida de base de datos");
		// si idAnexoModificacion no esta vacio, se coge el modulo del anexo en vez de
		// los de la poliza
		if (!StringUtils.nullToString(parcelaVO.getIdAnexoModificacion()).equals(""))
			modulos = this.getModuloAnexo(Long.parseLong(parcelaVO.getIdAnexoModificacion()), p.getIdpoliza(),
					p.getLinea().getLineaseguroid());
		else
			modulos = this.getModulosPoliza(p.getIdpoliza(), p.getLinea().getLineaseguroid());

		logger.debug("Modulos obtenidos de base de datos: " + modulos.size());
		// Obtenemos las producciones y los precios
		PreciosProduccionesVO precioProduccionVO = new PreciosProduccionesVO();

		precioProduccionVO.setListPrecios(
				getPrecio(p.getLinea().getLineaseguroid(), p.getIdpoliza(), parcelaVO, modulos, indiceCapital));

		if (!esProduccionConWS) {
			precioProduccionVO
					.setListProducciones(getProduccion(p.getLinea().getLineaseguroid(), p.getAsegurado().getNifcif(),
							p.getIdpoliza(), parcelaVO, modulos, indiceCapital, codConceptoProduccion));
		}

		GregorianCalendar fechaFin = new GregorianCalendar();
		Long tiempo = fechaFin.getTimeInMillis() - fechaInicio.getTimeInMillis();
		logger.debug("Tiempo desde que se inicia el calculo de precio y produccion hasta que termina: " + tiempo
				+ " milisegundos.");

		return precioProduccionVO;
	}

	/***
	 * PET.63485.FIII DNF 09/02/2021 Me creo un nuevo metodo para recuperar el
	 * precio sin llamar al WS
	 */
	public PreciosProduccionesVO getPrecioPolizaCalculoPrecio(ParcelaVO parcelaVO) {

		PreciosProduccionesVO preciosProduccionesVO = new PreciosProduccionesVO();
		List<PrecioVO> listaPreciosVO = new ArrayList<PrecioVO>();
		Poliza poliza = null;
		String mensajeError = "";

		try {
			poliza = (Poliza) polizaDao.getPolizaById(new Long(parcelaVO.getCodPoliza()));
			listaPreciosVO = crearListaPrecios(poliza.getLinea().getLineaseguroid(), parcelaVO);
		} catch (Exception e) {
			logger.error(e);
		}

		preciosProduccionesVO.setListPrecios(listaPreciosVO);
		for (PrecioVO precioVO : listaPreciosVO) {
			if (BigDecimal.ZERO.compareTo(new BigDecimal(precioVO.getLimMax())) == 0) {
				mensajeError = "Con los datos actuales de la parcela, el precio del modulo " + precioVO.getCodModulo()
						+ " es igual a 0. Por favor, revise los datos introducidos.";
				break;
			}
		}
		preciosProduccionesVO.setMensajeError(mensajeError);
		return preciosProduccionesVO;
	}

	/***
	 * fin PET.63485.FIII DNF 09/02/2021 Me creo un nuevo metodo para recuperar el
	 * precio sin llamar al WS
	 */

	/**
	 * El que se llama desde la pantalla de parcela de póliza
	 */
	public PreciosProduccionesVO getProduccionPrecioPolizaWS(ParcelaVO parcelaVO, String realPath, String codUsuario) {
		// Para el control de errores
		int numLlamadas = 0;
		PreciosProduccionesVO preciosProduccionesVO = new PreciosProduccionesVO();
		List<ProduccionVO> listaProduccionesVO = new ArrayList<ProduccionVO>();
		List<PrecioVO> listaPreciosVO = new ArrayList<PrecioVO>();
		Parcela parcela = null;
		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		Poliza poliza = null;
		Poliza polizaXML = null;
		String xmlPoliza = "";
		String mensajeError = "";
		String mensajeErrorAux = "";
		try {
			String rutaWebInfDecod = URLDecoder.decode(realPath, UTF8);
			poliza = (Poliza) polizaDao.getPolizaById(new Long(parcelaVO.getCodPoliza()));
			// Sólo entrar en WS si es de producción
			boolean esTipoProduccion = esTipoProduccion(parcelaVO.getCapitalAsegurado().getCodtipoCapital());
			if (esTipoProduccion) {
				polizaXML = new Poliza();
				BeanUtils.copyProperties(poliza, polizaXML);
				parcela = getParcelaRdtos(poliza.getLinea().getLineaseguroid(), parcelaVO);
				Set<Parcela> parcelas = new HashSet<Parcela>(1);
				parcelas.add(parcela);
				polizaXML.setParcelas(parcelas);
			}

			Set<ModuloPoliza> colComparativas = poliza.getModuloPolizas();
			Set<String> colModulos = new HashSet<String>(colComparativas.size());
			logger.debug("Tamaño colección comparativas: " + colComparativas.size());

			for (ModuloPoliza mp : colComparativas) {
				String codModulo = mp.getId().getCodmodulo();
				logger.debug(MODULO + codModulo + " | Num llamadas: " + numLlamadas);
				if (!colModulos.contains(codModulo)) {

					if (esTipoProduccion) {
						mensajeErrorAux = null;
						numLlamadas++;
						xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosToXml(
								Long.valueOf(parcelaVO.getCapitalAsegurado().getCodtipoCapital()), polizaXML, mp,
								cpmTipoCapitalDao, polizaDao, null);
						request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
								AjustarProducciones.MAX, true);
						try {
							respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request,
									rutaWebInfDecod, codUsuario);
							convertirRespuesta(respuesta, codModulo, poliza.getLinea().getLineaseguroid(),
									listaProduccionesVO);
						} catch (AgrException e) {
							mensajeErrorAux = getMsgAgrException(e);
							if ("1".equals(mensajeErrorAux)) {
								mensajeErrorAux = "Rendimiento libre";
								preciosProduccionesVO.setRdtosLibres(true);
							} else {
								mensajeError += MODULO + codModulo + ": " + mensajeErrorAux + "<br/>";
							}

							logger.debug(ERROR + mensajeErrorAux);
						} catch (Exception e) {
							mensajeErrorAux = e.getMessage();
							logger.error(ERROR + mensajeErrorAux);
						} finally {
							registrarEnHistoricoDesdePoliza(codUsuario, request, respuesta, poliza, xmlPoliza,
									mensajeErrorAux, parcela.getHoja(), parcela.getNumero());
						}
					}
					colModulos.add(codModulo);
				}
			}
			
			preciosProduccionesVO.setListProducciones(listaProduccionesVO);
			listaPreciosVO = crearListaPrecios(poliza.getLinea().getLineaseguroid(), parcelaVO);
			preciosProduccionesVO.setListPrecios(listaPreciosVO);
			for (PrecioVO precioVO : listaPreciosVO) {
				if (BigDecimal.ZERO.compareTo(new BigDecimal(precioVO.getLimMax())) == 0) {
					mensajeError = "Con los datos actuales de la parcela, el precio del modulo "
							+ precioVO.getCodModulo() + " es igual a 0. Por favor, revise los datos introducidos.";
					break;
				}
			}
			preciosProduccionesVO.setMensajeError(mensajeError);
		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			logger.error(PROD + e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);
		} catch (Exception e) {
			logger.error(PROD, e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);
		}
		return preciosProduccionesVO;
	}

	private boolean esTipoProduccion(String codTipoCapital) {
		boolean esTipoProduccion = false;
		BigDecimal codConceptoProduccion = new BigDecimal(
				ResourceBundle.getBundle("agp").getString("codConceptoRendimiento"));
		try {
			TipoCapital tipoCapital = (TipoCapital) calculoPrecioDao.get(TipoCapital.class,
					new BigDecimal(codTipoCapital));
			if (tipoCapital != null) {
				if (tipoCapital.getCodconcepto().intValue() == codConceptoProduccion.intValue()) {
					esTipoProduccion = true;
				}
			}
		} catch (DAOException e) {
			// Se establece como si fuera de tipo producción para que se realice la llamada
			// al WS
			esTipoProduccion = true;
			logger.error("Error al calcular si es tipo producción para el codTipoCapital = " + codTipoCapital);
			logger.error("esTipoProduccion: ", e);
		}
		return esTipoProduccion;
	}
	
	public PreciosProduccionesVO getProduccionPrecioAnexoWS2(ParcelaVO parcelaVO, String realPath, String codUsuario) throws Exception {

		PreciosProduccionesVO preciosProduccionesVO = new PreciosProduccionesVO();
		List<ProduccionVO> listaProduccionesVO = new ArrayList<ProduccionVO>();
		String mensajeError = "";
		String mensajeErrorAux = "";
		
		ContratacionRendimientosResponse respuesta = null;

		AnexoModificacion anexoMod = null;

		try {
			String idAnexoModificacion = parcelaVO.getIdAnexoModificacion();
			
			Poliza poliza = (Poliza) polizaDao.getPolizaById(new Long(parcelaVO.getCodPoliza()));
			Usuario usuario = poliza.getUsuario();
			Long lineaSeguroId = poliza.getLinea().getLineaseguroid();
			
			// Obtener el Anexo iterando los anexos de la poliza
			for (AnexoModificacion auxAnexo: poliza.getAnexoModificacions()) {
				if (auxAnexo.getId().toString().equals(idAnexoModificacion)) {
					anexoMod = auxAnexo;
					break;
				}
			}
			
			
			logger.debug(MODULO + anexoMod.getCodmodulo());

			
			boolean esTipoProduccion = esTipoProduccion(parcelaVO.getCapitalAsegurado().getCodtipoCapital());
			

			if (esTipoProduccion) {

				
				com.rsi.agp.dao.tables.anexo.Parcela parcela = new com.rsi.agp.dao.tables.anexo.Parcela();
				parcela.setHoja(new BigDecimal(1));
				parcela.setNumero(new BigDecimal(1));
				parcela.setNomparcela(parcelaVO.getNombreParcela());
				parcela.setCodcomarca(new BigDecimal(parcelaVO.getCodComarca()));
				parcela.setCodprovincia(new BigDecimal(parcelaVO.getCodProvincia()));
				parcela.setCodtermino(new BigDecimal(parcelaVO.getCodTermino()));
				parcela.setCodcultivo(new BigDecimal(parcelaVO.getCultivo()));
				parcela.setCodvariedad(new BigDecimal(parcelaVO.getVariedad()));
				
				parcela.setSubtermino(" ".charAt(0));
				if (!parcelaVO.getCodSubTermino().isEmpty()) {
					parcela.setSubtermino(parcelaVO.getCodSubTermino().charAt(0));
				}
				
				com.rsi.agp.dao.tables.anexo.CapitalAsegurado cap = new com.rsi.agp.dao.tables.anexo.CapitalAsegurado();
				
				if (parcelaVO.getCapitalAsegurado().getPrecio().isEmpty()) {
					cap.setPrecio(new BigDecimal(0));
				}else {
					cap.setPrecio(new BigDecimal(parcelaVO.getCapitalAsegurado().getPrecio()));
				}
				
				cap.setProduccion(new BigDecimal(0));
				cap.setSuperficie(new BigDecimal(parcelaVO.getCapitalAsegurado().getSuperficie()));
				TipoCapital tipoCapital = new TipoCapital();
				tipoCapital.setCodtipocapital(new BigDecimal(parcelaVO.getCapitalAsegurado().getCodtipoCapital()));
				cap.setTipoCapital(tipoCapital);
				
				
				for (DatoVariableParcelaVO dv : parcelaVO.getCapitalAsegurado().getDatosVariablesParcela()) {
					
					if (dv.getValor().isEmpty()) {
						continue;
					}
					
					CapitalDTSVariable dts = new CapitalDTSVariable();
					dts.setCodconcepto(new BigDecimal(dv.getCodconcepto()));
					dts.setValor(dv.getValor());
					dts.setId(dv.getIdDatoVariable());
									
					cap.getCapitalDTSVariables().add(dts);
				}
				
				
				parcela.setCapitalAsegurados(Collections.singleton(cap));
				anexoMod.setParcelas(Collections.singleton(parcela));
				
				
				String xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexoMod, null, usuario, xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, true);
				
				
				CalcularRendimientosRequest request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true, AjustarProducciones.MAX, true);

				try {

					respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request, URLDecoder.decode(realPath, UTF8), codUsuario);
					convertirRespuestaAnexo(respuesta, anexoMod.getCodmodulo(), lineaSeguroId, listaProduccionesVO);

				} catch (AgrException e) {

					mensajeErrorAux = getMsgAgrException(e);

					if ("1".equals(mensajeErrorAux)) {
						mensajeErrorAux = "Rendimiento libre";
						preciosProduccionesVO.setRdtosLibres(true);

						logger.debug(ERROR + mensajeErrorAux);
					} else {
						mensajeError = mensajeError + MODULO + anexoMod.getCodmodulo() + "\n" + mensajeErrorAux + "\n";
						logger.debug(ERROR + mensajeErrorAux);
						preciosProduccionesVO.setMensajeError(mensajeError);
					}
				} finally {
					Map<String, BigDecimal> mapaHojaNumero = new HashMap<String, BigDecimal>();
					// Obtener la parcela y rellenar el mapa
					for (Parcela auxP: poliza.getParcelas()) {
						if (auxP.getIdparcela().toString().equals(parcelaVO.getCodParcela())) {
							mapaHojaNumero.put("HOJA", new BigDecimal(auxP.getHoja()));
							mapaHojaNumero.put("NUMERO", new BigDecimal(auxP.getNumero()));
							break;
						}
					}
					
					registrarEnHistoricoDesdeAnexo(codUsuario, request, respuesta, anexoMod, xmlPoliza, mensajeErrorAux, mapaHojaNumero);
				}
				
				
			}

			List<PrecioVO> listaPreciosVO = crearListaPrecios(lineaSeguroId, parcelaVO);
			preciosProduccionesVO.setListProducciones(listaProduccionesVO);
			preciosProduccionesVO.setListPrecios(listaPreciosVO);

		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			logger.error(PROD + e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);

		} catch (Exception e) {
			logger.error(PROD, e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);

		}

		return preciosProduccionesVO;
	}

	public PreciosProduccionesVO getProduccionPrecioAnexoWS(ParcelaVO parcelaVO, CapitalAseguradoVO capitalAseguradoVO,
			String realPath, String codUsuario, int indiceCapital) throws Exception {

		PreciosProduccionesVO preciosProduccionesVO = new PreciosProduccionesVO();
		List<ProduccionVO> listaProduccionesVO = new ArrayList<ProduccionVO>();
		List<PrecioVO> listaPreciosVO = new ArrayList<PrecioVO>();
		Map<String, BigDecimal> mapaHojaNumero = new HashMap<String, BigDecimal>();

		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		Poliza poliza = null;
		String xmlPoliza = "";
		String mensajeError = "";
		String mensajeErrorAux = "";
		Long lineaSeguroId = null;
		Usuario usuario = null;
		AnexoModificacion anexoMod = null;

		try {
			String idAnexoModificacion = parcelaVO.getIdAnexoModificacion();
			
			poliza = (Poliza) polizaDao.getPolizaById(new Long(parcelaVO.getCodPoliza()));

			usuario = poliza.getUsuario();
			lineaSeguroId = poliza.getLinea().getLineaseguroid();

			Set<AnexoModificacion> colAnexos = poliza.getAnexoModificacions();
			Iterator<AnexoModificacion> it = colAnexos.iterator();

			boolean encontrado = false;
			while (it.hasNext() && !encontrado) {
				AnexoModificacion auxAnexo = it.next();
				if (auxAnexo.getId().toString().equals(idAnexoModificacion)) {
					anexoMod = auxAnexo;
					encontrado = true;
				}
			}

			mapaHojaNumero = dejarPolizaConSoloUnaParcelaAnexo(anexoMod, parcelaVO, capitalAseguradoVO);
			String rutaWebInfDecod = URLDecoder.decode(realPath, UTF8);

			String codModulo = anexoMod.getCodmodulo();

			if (codModulo == null) {
				codModulo = poliza.getCodmodulo();
			}
			logger.debug(MODULO + codModulo);

			boolean esTipoProduccion = esTipoProduccion(capitalAseguradoVO.getCodtipoCapital());

			if (esTipoProduccion) {
				/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
				/*
				 * Pasamos parametro tipo boolean para saber que venimos de Calculo de
				 * Rendimiento Orientativo
				 */
				boolean calcRendOriHist = false;
				xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexoMod, null, usuario,
						xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, calcRendOriHist);
				/* ESC-16025 ** MODIF TAM (23.11.2021) ** Fin */
				request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
						AjustarProducciones.MAX, true);

				try {

					respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request,
							rutaWebInfDecod, codUsuario);
					convertirRespuestaAnexo(respuesta, codModulo, lineaSeguroId, listaProduccionesVO);

				} catch (AgrException e) {

					mensajeErrorAux = getMsgAgrException(e);

					if ("1".equals(mensajeErrorAux)) {
						mensajeErrorAux = "Rendimiento libre";
						logger.debug(ERROR + mensajeErrorAux);
					} else {
						mensajeError = mensajeError + MODULO + codModulo + "\n" + mensajeErrorAux + "\n";
						logger.debug(ERROR + mensajeErrorAux);
						preciosProduccionesVO.setMensajeError(mensajeError);
					}
				}

				registrarEnHistoricoDesdeAnexo(codUsuario, request, respuesta, anexoMod, xmlPoliza, mensajeErrorAux,
						mapaHojaNumero);
			}

			listaPreciosVO = crearListaPrecios(lineaSeguroId, parcelaVO);
			preciosProduccionesVO.setListProducciones(listaProduccionesVO);
			preciosProduccionesVO.setListPrecios(listaPreciosVO);

		} catch (javax.xml.ws.soap.SOAPFaultException e) {
			logger.error(PROD + e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);

		} catch (Exception e) {
			logger.error(PROD, e);
			mensajeError = e.getMessage();
			preciosProduccionesVO.setMensajeError(mensajeError);

		}

		return preciosProduccionesVO;
	}

	/**
	 * Devuelve un mapa cuyas claves se forman con "codModulo-hoja-numero" de la
	 * parcela de póliza, y cuyo contenido son los ProduccionVO
	 */
	public Map<String, ProduccionVO> calcularRendimientosPolizaWS(Long idPoliza, Set<Long> colIdParcelasParaRecalculo,
			String realPath, String codUsuario, int indiceCapital) throws Throwable {
		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		Poliza poliza = null;
		String xmlPoliza = "";
		String error = null;
		Long lineaSeguroId = null;
		Map<String, ProduccionVO> mapaPreciosProducciones = new HashMap<String, ProduccionVO>();
		poliza = (Poliza) polizaDao.getPolizaById(idPoliza);
		lineaSeguroId = poliza.getLinea().getLineaseguroid();
		String rutaWebInfDecod = URLDecoder.decode(realPath, UTF8);
		List<ModuloPoliza> colComparativas = polizaDao.getLstModulosPoliza(poliza.getIdpoliza());
		Set<String> colModulos = new HashSet<String>();
		for (ModuloPoliza mp : colComparativas) {
			String codModulo = mp.getId().getCodmodulo();
			logger.debug(MODULO + codModulo);
			if (!colModulos.contains(codModulo)) {
				logger.debug("Previo a generar el XML de rendimientos");
				xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosToXml(null, poliza, mp,
						cpmTipoCapitalDao, polizaDao, colIdParcelasParaRecalculo);
				request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true,
						AjustarProducciones.MAX, true);
				try {
					respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request,
							rutaWebInfDecod, codUsuario);
					convertirRespuestaMasivo(respuesta, codModulo, lineaSeguroId, mapaPreciosProducciones);
				} catch (AgrException e) {
					error = getMsgAgrExceptionMasivo(e);
					if (error != null && !"".equals(error)) {
						logger.debug(ERROR + error);
						throw e;
					}
				} catch (Exception e) {
					error = e.getMessage();
					logger.error(ERROR + error);
				} finally {
					registrarEnHistoricoDesdePoliza(codUsuario, request, respuesta, poliza, xmlPoliza, error, null,
							null);
				}
				colModulos.add(codModulo);
			}
		}
		return mapaPreciosProducciones;
	}

	public Map<String, ProduccionVO> calcularRendimientosAnexoWS(AnexoModificacion anexoMod,
			Set<Long> colIdParcelasParaRecalculo, String realPath, Usuario usuario, int indiceCapital)
			throws Exception {

		CalcularRendimientosRequest request = null;
		ContratacionRendimientosResponse respuesta = null;
		String xmlPoliza = "";
		String error = null;
		Long lineaSeguroId = null;
		Map<String, ProduccionVO> mapaPreciosProducciones = new HashMap<String, ProduccionVO>();
		String codUsuario = usuario.getCodusuario();

		String rutaWebInfDecod = URLDecoder.decode(realPath, UTF8);

		String codModulo = anexoMod.getCodmodulo();

		if (codModulo == null) {
			codModulo = anexoMod.getPoliza().getCodmodulo();
		}
		logger.debug(MODULO + codModulo);

		lineaSeguroId = anexoMod.getPoliza().getLinea().getLineaseguroid();

		/* ESC-16025 ** MODIF TAM (23.11.2021) ** Inicio */
		/*
		 * Pasamos parametro tipo boolean para saber que venimos de Calculo de
		 * Rendimiento Orientativo
		 */
		boolean calcRendOriHist = false;

		xmlPoliza = SWContratacionRendimientosHelper.polizaRendimientosAnexoToXml(anexoMod, colIdParcelasParaRecalculo,
				usuario, xmlAnexoModDao, cpmTipoCapitalDao, polizaCopyDao, calcRendOriHist);
		/* ESC-16025 ** MODIF TAM (23.11.2021) ** Fin */
		request = SWContratacionRendimientosHelper.obtenerRendimientosRequest(xmlPoliza, true, AjustarProducciones.MAX,
				true);

		try {
			logger.debug("TAMARA -llamamos al servicio web getProduccionRendimientos");
			respuesta = new SWContratacionRendimientosHelper().getProduccionRendimientos(request, rutaWebInfDecod,
					codUsuario);
			logger.debug("TAMARA -despues de llamar al servicio web");
			logger.debug("**@@** Valor de la respuesta:"+respuesta.toString());
			logger.debug("TAMARA -convertimos la respuesta del servicion web");
			convertirRespuestaMasivo(respuesta, codModulo, lineaSeguroId, mapaPreciosProducciones);
			logger.debug("TAMARA -despues de convertir la respuesta");
		} catch (AgrException e) {

			logger.debug("TAMARA -ERRORRRRRRRRRRRRRRRRRR");
			error = getMsgAgrExceptionMasivo(e);
			if (error != null && !"".equals(error)) {
				logger.debug(ERROR + error);
				throw e;
			}
		} finally {
			registrarEnHistoricoDesdeAnexo(codUsuario, request, respuesta, anexoMod, xmlPoliza, error, null);
		}

		return mapaPreciosProducciones;
	}

	// Simple poliza
	private void convertirRespuesta(ContratacionRendimientosResponse respuesta, String codModulo, Long lineaSeguroId,
			List<ProduccionVO> listaProduccionesVO) throws DAOException, BusinessException, ParseException {

		es.agroseguro.seguroAgrario.rendimientosCalculo.Poliza poliza = respuesta.getRendimientoPolizaDocument()
				.getPoliza();

		Calendar fechaRecepcion = poliza.getFechaRecepcion();
		Calendar fechaRespuesta = poliza.getFechaRespuesta();
		logger.info(fechaRespuesta.getTimeInMillis() - fechaRecepcion.getTimeInMillis());

		Cosecha[] arrayCosecha = poliza.getCosechaArray();
		Cosecha cosecha = arrayCosecha[0];
		es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela[] arrayParcelas = cosecha.getParcelaArray();
		es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela = arrayParcelas[0];

		ControlParcelaAjustado controlAjustado = parcela.getControlParcelaAjustado();
		ProduccionVO produccionVO = new ProduccionVO();

		// Nota: Para algunos casos de rendimiento libre no viene relleno
		if (controlAjustado != null && (Long.valueOf(0).equals(Long.valueOf(controlAjustado.getCodigo()))
				|| Constants.TIPO_RDTO_MAXIMO.equals(Long.valueOf(controlAjustado.getCodigo())))) {
			produccionVO.setLimMax(String.valueOf(controlAjustado.getProduccionMaxima()));
			produccionVO.setLimMin(String.valueOf(controlAjustado.getProduccionMinima()));
			produccionVO.setProduccion(String.valueOf(controlAjustado.getProduccion()));
		} else {
			produccionVO.setLimMin("0");
			produccionVO.setLimMax("");
			produccionVO.setProduccion("");
		}

		// Para que no cause error luego
		produccionVO.setRdtoMin("0");
		produccionVO.setCodModulo(codModulo);
		produccionVO.setDesModulo(
				((Modulo) this.modulosDao.get(Modulo.class, new ModuloId(lineaSeguroId, codModulo))).getDesmodulo());

		listaProduccionesVO.add(produccionVO);
	}

	// Simple anexo
	private void convertirRespuestaAnexo(ContratacionRendimientosResponse respuesta, String codModulo,
			Long lineaSeguroId, List<ProduccionVO> listaProduccionesVO)
			throws DAOException, BusinessException, ParseException {

		es.agroseguro.seguroAgrario.rendimientosCalculo.Poliza poliza = respuesta.getRendimientoPolizaDocument()
				.getPoliza();

		Calendar fechaRecepcion = poliza.getFechaRecepcion();
		Calendar fechaRespuesta = poliza.getFechaRespuesta();
		logger.info(fechaRespuesta.getTimeInMillis() - fechaRecepcion.getTimeInMillis());

		Cosecha[] arrayCosecha = poliza.getCosechaArray();
		Cosecha cosecha = arrayCosecha[0];
		es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela[] arrayParcelas = cosecha.getParcelaArray();
		es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela = arrayParcelas[0];

		ControlParcelaAjustado controlAjustado = parcela.getControlParcelaAjustado();

		ProduccionVO produccionVO = new ProduccionVO();

		// Nota: Para algunos casos de rendimiento libre no viene relleno
		if (controlAjustado != null && (Long.valueOf(0).equals(Long.valueOf(controlAjustado.getCodigo()))
				|| Constants.TIPO_RDTO_MAXIMO.equals(Long.valueOf(controlAjustado.getCodigo())))) {
			logger.debug("controlAjustado.codigo: " + controlAjustado.getCodigo());
			produccionVO.setLimMax(String.valueOf(controlAjustado.getProduccionMaxima()));
			produccionVO.setLimMin(String.valueOf(controlAjustado.getProduccionMinima()));
			produccionVO.setProduccion(String.valueOf(controlAjustado.getProduccion()));
		} else {
			produccionVO.setLimMin("0");
			produccionVO.setLimMax("");
			produccionVO.setProduccion("");
		}

		// Para que no cause error luego
		produccionVO.setRdtoMin("0");

		produccionVO.setCodModulo(codModulo);

		listaProduccionesVO.add(produccionVO);
	}

	private List<PrecioVO> crearListaPrecios(final Long lineaSeguroId, final ParcelaVO parcelaVO)
			throws NumberFormatException, BusinessException, ParseException {
		List<PrecioVO> listaPreciosVO = new ArrayList<PrecioVO>();
		List<Modulo> modulos = new ArrayList<Modulo>();
		Long idPoliza = Long.valueOf(parcelaVO.getCodPoliza());
		// si idAnexoModificacion no esta vacio, se coge el modulo del anexo en vez de
		// los de la poliza
		if (StringUtils.isNullOrEmpty(parcelaVO.getIdAnexoModificacion())) {
			modulos = this.getModulosPoliza(idPoliza, lineaSeguroId);
		} else {
			modulos = this.getModuloAnexo(Long.valueOf(parcelaVO.getIdAnexoModificacion()), idPoliza, lineaSeguroId);
		}
		listaPreciosVO = getPrecio(lineaSeguroId, idPoliza, parcelaVO, modulos, 0);
		return listaPreciosVO;
	}

	/**
	 * Convertir respuesta para masivo
	 */
	private void convertirRespuestaMasivo(ContratacionRendimientosResponse respuesta, String codModulo,
			Long lineaSeguroId, Map<String, ProduccionVO> mapaProduccionVO)
			throws DAOException, BusinessException, ParseException {

		logger.debug("[ESC-28227] convertirRespuestaMasivo [BEGIN]");
		es.agroseguro.seguroAgrario.rendimientosCalculo.Poliza poliza = respuesta.getRendimientoPolizaDocument()
				.getPoliza();

		Cosecha[] arrayCosecha = poliza.getCosechaArray();

		for (int i = 0; i < arrayCosecha.length; i++) {

			Cosecha cosecha = arrayCosecha[i];
			logger.debug("[ESC-28227] cosecha: " + cosecha.xmlText());
			es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela[] arrayParcelas = cosecha.getParcelaArray();

			for (int j = 0; j < arrayParcelas.length; j++) {
				es.agroseguro.seguroAgrario.rendimientosCalculo.Parcela parcela = arrayParcelas[j];
				logger.debug("[ESC-28227] parcela: " + parcela.xmlText());
				int hoja = parcela.getHoja();
				int numero = parcela.getNumero();

				String claveMapa = codModulo + "-" + hoja + "-" + numero + "-" + parcela.getTipo();
				logger.debug("CLAVE DEL PUT:" + claveMapa + " LINEA: " + lineaSeguroId);
				ControlParcelaAjustado controlAjustado = parcela.getControlParcelaAjustado();

				ProduccionVO produccionVO = new ProduccionVO();

				// Nota: Para algunos casos de rendimiento libre no viene relleno
				if (controlAjustado != null && (Long.valueOf(0).equals(Long.valueOf(controlAjustado.getCodigo()))
						|| Constants.TIPO_RDTO_MAXIMO.equals(Long.valueOf(controlAjustado.getCodigo())))) {

					logger.debug("controlAjustado.codigo: " + controlAjustado.getCodigo());
					produccionVO.setLimMax(String.valueOf(controlAjustado.getProduccionMaxima()));
					produccionVO.setLimMin(String.valueOf(controlAjustado.getProduccionMinima()));

					// Se ha pedido que en el masivo coja la producción máxima
					produccionVO.setProduccion(String.valueOf(controlAjustado.getProduccionMaxima()));

					// Para que no cause error luego
					produccionVO.setRdtoMin("0");

					produccionVO.setCodModulo(codModulo);

					mapaProduccionVO.put(claveMapa, produccionVO);

				} else {
					logger.debug("Rendimiento libre. ClaveMapa: " + claveMapa);
				}
			}
		}
		logger.debug("[ESC-28227] convertirRespuestaMasivo [END]");
	}

	/**
	 * Graba los datos de la llamada al servicio web de rendimiento en la tabla
	 * TB_PARCELA_SW_RENDIMIENTOS
	 */
	public void registrarEnHistoricoDesdePoliza(String codUsuario, CalcularRendimientosRequest rendimientosRequest,
			com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse rendimientosRespuesta, Poliza poliza,
			String xmlPoliza, String error, Integer hoja, Integer numero) {
		ParcelaSWRendimiento parcelaSWRendimiento = new ParcelaSWRendimiento();
		if (poliza != null) {
			parcelaSWRendimiento.setPoliza(poliza);
		}
		parcelaSWRendimiento.setUsuario(codUsuario);
		parcelaSWRendimiento.setFecha(new Date());
		if (hoja != null)
			parcelaSWRendimiento.setHoja(BigDecimal.valueOf(hoja));
		if (numero != null)
			parcelaSWRendimiento.setNumero(BigDecimal.valueOf(numero));
		String xmlLlamadaEntero = WSUtils.generateXMLLlamadaCalculoRendimientos(rendimientosRequest, xmlPoliza);
		parcelaSWRendimiento.setXmlEnvio(Hibernate.createClob(xmlLlamadaEntero));
		if (rendimientosRespuesta != null) {
			parcelaSWRendimiento.setXmlRespuesta(
					Hibernate.createClob(rendimientosRespuesta.getRendimientoPolizaDocument().toString()));
		} else {
			parcelaSWRendimiento.setXmlRespuesta(Hibernate.createClob(error));
		}
		try {
			rendParcelaPolizaWSDao.saveOrUpdate(parcelaSWRendimiento);
		} catch (DAOException e) {
			logger.error(
					"Error al insertar el registro de la comunicacion con el SW de cálculo de rendimientos de póliza",
					e);
		}
	}

	/**
	 * Graba los datos de la llamada al servicio web de rendimiento en la tabla
	 * TB_ANEXO_MOD_PAR_SW_RENDIMIENT
	 * 
	 * @param codUsuario
	 * @param rendimientosRequest
	 * @param rendimientosRespuesta
	 * @param anexoModificacion
	 * @param xmlPoliza
	 * @param error
	 * @param mapaHojaNumero
	 */
	public void registrarEnHistoricoDesdeAnexo(String codUsuario, CalcularRendimientosRequest rendimientosRequest,
			com.rsi.agp.core.managers.impl.ContratacionRendimientosResponse rendimientosRespuesta,
			AnexoModificacion anexoModificacion, String xmlPoliza, String error,
			Map<String, BigDecimal> mapaHojaNumero) {

		ParcelaAMSWRendimiento parcelaAMSWRendimiento = new ParcelaAMSWRendimiento();

		if (anexoModificacion != null) {
			parcelaAMSWRendimiento.setAnexoModificacion(anexoModificacion);
		}

		parcelaAMSWRendimiento.setUsuario(codUsuario);
		parcelaAMSWRendimiento.setFecha(new Date());

		if (mapaHojaNumero != null) {
			parcelaAMSWRendimiento.setHoja(mapaHojaNumero.get("HOJA"));
			parcelaAMSWRendimiento.setNumero(mapaHojaNumero.get("NUMERO"));
		}

		String xmlLlamadaEntero = WSUtils.generateXMLLlamadaCalculoRendimientos(rendimientosRequest, xmlPoliza);
		parcelaAMSWRendimiento.setXmlEnvio(Hibernate.createClob(xmlLlamadaEntero));

		if (rendimientosRespuesta != null) {
			parcelaAMSWRendimiento.setXmlRespuesta(
					Hibernate.createClob(rendimientosRespuesta.getRendimientoPolizaDocument().toString()));
		} else {
			parcelaAMSWRendimiento.setXmlRespuesta(Hibernate.createClob(error));
		}

		try {
			rendParcelaAnexoWSDao.saveOrUpdate(parcelaAMSWRendimiento);

		} catch (DAOException e) {
			logger.error(
					"Error al insertar el registro de la comunicacion con el SW de cálculo de rendimientos de anexo",
					e);
		}
	}

	/**
	 * Para saber si se debe calcular el rendimiento vía SW o no
	 * 
	 * @return
	 */
	public boolean calcularRendimientoProdConSW() {

		boolean usarSW = calculoProduccionDao.calcularRendimientoProdConSW();
		return usarSW;
	}

	/**
	 * Para saber si se debe calcular el rendimiento vía SW o no
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean getRiesgoCubElegCalculoRendi(Long lineaseguroid, String codModulo, List<BigDecimal> lstCodCultivos)
			throws DAOException {

		boolean usarSW = calculoProduccionDao.getRiesgoCubElegCalculoRendi(lineaseguroid, codModulo, lstCodCultivos);
		return usarSW;
	}

	/**
	 * Obtiene la Produccion.
	 */
	public List<ProduccionVO> getProduccion(Long lineaseguroid, String nifasegurado, Long idPoliza, ParcelaVO parcela,
			List<Modulo> modulos, int indiceCapital, BigDecimal codConceptoProduccion)
			throws ParseException, BusinessException {

		String[] produccion = new String[4];

		ProduccionVO produccionVO = new ProduccionVO();
		List<ProduccionVO> producciones = new ArrayList<ProduccionVO>();

		for (Modulo moduloPlz : modulos) {
			// Inicializamos la produccion a 0
			produccion[0] = "0";
			produccion[1] = "0";
			produccion[2] = "0";
			produccion[3] = "0";

			logger.debug("tc parcelavo = " + parcela.getCapitalesAsegurados().get(indiceCapital).getCodtipoCapital()
					+ " idparcela=" + parcela.getCodParcela());
			TipoCapital tc = (TipoCapital) this.calculoProduccionDao.getObject(TipoCapital.class,
					new BigDecimal(parcela.getCapitalesAsegurados().get(indiceCapital).getCodtipoCapital()));

			if (tc != null) {
				logger.debug("tc = " + tc.getCodconcepto());
				// Obtenemos de base de datos el parametro para saber si calculamos la
				// produccion mediante pl o mediante java
				boolean conPl = this.calculoProduccionDao.calcularConPlSql();
				/* SONAR Q */
				produccion = informarProduccion(produccion, tc, codConceptoProduccion, lineaseguroid, nifasegurado,
						idPoliza, parcela, indiceCapital, conPl, moduloPlz);
				/* SONAR Q FIN */
			} else {
				logger.error("No se pudo obtener el tipoCapital!!!!");
			}

			produccionVO = new ProduccionVO();

			produccionVO.setCodModulo(moduloPlz.getId().getCodmodulo());

			// Asignamos los valores redondeando los decimales
			produccionVO.setLimMin(
					new BigDecimal(produccion[0].replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR).toString());
			if (!StringUtils.nullToString(produccion[1]).equals(""))
				produccionVO.setLimMax(
						new BigDecimal(produccion[1].replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR).toString());
			else
				produccionVO.setLimMax("");

			produccionVO.setRdtoMin(
					new BigDecimal(produccion[2].replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR).toString());
			if (produccion.length == 4 && !StringUtils.nullToString(produccion[3].trim()).equals(""))
				produccionVO.setRdtoMax(
						new BigDecimal(produccion[3].replace(",", ".")).setScale(0, BigDecimal.ROUND_FLOOR).toString());
			else
				produccionVO.setRdtoMax("");

			producciones.add(produccionVO);
		}

		return producciones;
	}

	/**
	 * Metodo para obtener la lista de producciones posibles de la base de datos
	 * mediante JAVA. Se filtrara atendiendo a los campos de mascara.
	 * 
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @param nifasegurado
	 * @return
	 * @throws ParseException
	 * @throws BusinessException
	 */
	public String[] getProduccionesBBDD(Long lineaseguroid, String codmodulo, ParcelaVO parcela, String nifasegurado,
			int indiceCapital) throws ParseException, BusinessException {

		List<ItemVO> datVarPar = getDatosParcela(parcela, indiceCapital);
		String[] produccion = new String[2];
		List<AseguradoAutorizado> asegAutorizados = null;

		String fechaFinGarantias = null;
		String garantizado = null;
		Medida medidaAseg = null;
		BigDecimal tablaRdto = new BigDecimal(0);
		BigDecimal coefRdtoAseg = new BigDecimal(1);
		BigDecimal porcentajeReduccionMinimo = new BigDecimal(1);

		String codLinea;

		try {

			codLinea = ((Linea) this.calculoProduccionDao.getObject(Linea.class, lineaseguroid)).getCodlinea()
					.toString();

			// 1.- Comprobamos si hay que filtrar asegurados Autorizados por F.F.Garantias o
			// Garantizado
			Integer countOrganizador = this.calculoProduccionDao.getOrganizadorInformacion(lineaseguroid);
			if (countOrganizador.equals(new Integer(0))) {
				garantizado = getGarantizado(parcela);
			} else {
				fechaFinGarantias = getFechaFinGarantias(parcela);
			}

			logger.debug("garantidado: " + garantizado + " ffgarantias: " + fechaFinGarantias);

			/** SONAR Q ** Inicio */
			asegAutorizados = informarAsegAutorizados(lineaseguroid, codmodulo, parcela, nifasegurado, indiceCapital,
					fechaFinGarantias, garantizado, codLinea);
			/** SONAR Q ** Fin */

			medidaAseg = getCoefRdtoMaxAseg(lineaseguroid, nifasegurado);

			/** SONAR Q ** Inicio */
			tablaRdto = getablaRdto(medidaAseg, tablaRdto);
			/** SONAR Q ** Fin */

			Integer countMedidasAplicablesModulo = this.calculoProduccionDao.getMedidasAplicableModulo(lineaseguroid,
					codmodulo);

			boolean existeLimiteRdto = this.calculoProduccionDao.existeLimiteRendimientoByLineaseguroid(lineaseguroid);

			if (asegAutorizados.size() == 0) {

				/** SONAR Q ** Inicio */
				produccion = Informarprod(lineaseguroid, codmodulo, parcela, nifasegurado, indiceCapital, produccion,
						datVarPar, existeLimiteRdto, tablaRdto);
				/** SONAR Q ** fIN */

				if (countMedidasAplicablesModulo != null && !countMedidasAplicablesModulo.equals(new Integer(0))
						&& medidaAseg != null) {
					coefRdtoAseg = medidaAseg.getCoefrdtomaxaseg();
				}

			} else {
				AseguradoAutorizado aseg = asegAutorizados.get(0);

				if (aseg.getRdtopermitido() != null && aseg.getRdtopermitido().longValue() != 0) {
					// el asegurado tiene rendimiento fijo, se mide en Kg/Ha.

					// calculo prod x superficie/unidades
					Float prodXSuperficie = new Float(0);
					Float prodXUnidades = new Float(0);
					Float prodXSuperficieMax = new Float(0);
					Float prodXUnidadesMax = new Float(0);

					String sProd = "";
					String sProdMax = "";

					for (ItemVO item : datVarPar) {
						if ("314".equals(codLinea) && "117".equals(item.getCodigo())
								&& !StringUtils.nullToString(item.getValor()).equals("")) {
							Float unidades = new Float(item.getValor());
							prodXUnidades = unidades * aseg.getRdtopermitido().floatValue();
							prodXUnidadesMax = prodXUnidades;
							prodXUnidades = prodXUnidades * 0.5F;
							sProd = prodXUnidades.toString();
							sProdMax = prodXUnidadesMax.toString();
							break;
						} else if ("258".equals(item.getCodigo()) && !"314".equals(codLinea)
								&& !StringUtils.nullToString(item.getValor()).equals("")) {
							Float superficie = new Float(item.getValor());
							prodXSuperficie = superficie * aseg.getRdtopermitido().floatValue();
							prodXSuperficieMax = prodXSuperficie;
							prodXSuperficie = prodXSuperficie * 0.5F;
							sProd = prodXSuperficie.toString();
							sProdMax = prodXSuperficieMax.toString();
							break;
						} else {
							sProd = "0";
							sProdMax = "0";
						}
					}
					produccion[0] = sProd;
					produccion[1] = sProdMax;
				} else if (aseg.getCoefsobrerdtos() != null) {
					// calculamos los limites de rendimiento
					produccion = getLimitesRendimiento(lineaseguroid, codmodulo, parcela, datVarPar, tablaRdto);

					/* SONAR Q */
					coefRdtoAseg = getcoefRdtoAseg(coefRdtoAseg, countMedidasAplicablesModulo, medidaAseg, aseg);
					/* SONAR Q FIN */
				} else if (existeLimiteRdto) {
					produccion = getLimitesRendimiento(lineaseguroid, codmodulo, parcela, datVarPar, tablaRdto);
					if (countMedidasAplicablesModulo != null && !countMedidasAplicablesModulo.equals(new Integer(0))
							&& medidaAseg != null) {
						coefRdtoAseg = medidaAseg.getCoefrdtomaxaseg();
					}
				} else {
					produccion[0] = "1";
					produccion[1] = "";
				}
			}

		} catch (Exception ex) {
			logger.error("Se ha producido un error al cacular los limites de produccion: " + ex.getMessage());
			throw new BusinessException("Se ha producido un error al cacular los limites de produccion", ex);
		}

		BigDecimal porcentajeReduccionRendimientoDatosVariables = calcularPorcentajeReduccionRendimientoDatosVariables(
				lineaseguroid, codmodulo, parcela);

		/* SONAR Q */
		porcentajeReduccionMinimo = getporcentajeReduccionMinimo(porcentajeReduccionRendimientoDatosVariables,
				coefRdtoAseg, porcentajeReduccionMinimo);
		/* SONAR Q FIN */

		produccion[0] = new BigDecimal(produccion[0]).multiply(porcentajeReduccionMinimo).toString().replace("\\.\\d*",
				"");

		/* SONAR Q */
		produccion[1] = obtenerprod1(produccion, porcentajeReduccionMinimo);
		/* SONAR Q FIN */

		return produccion;
	}

	/**
	 * Metodo para obtener el valor del dato variable "GARANTIZADO" de la poliza en
	 * caso de que lo tenga.
	 * 
	 * @param parcela
	 * @return
	 */
	private String getGarantizado(ParcelaVO parcela) {

		// un bucle por cada capital asegurado
		for (int i = 0; i < parcela.getCapitalesAsegurados().size(); i++) {
			CapitalAseguradoVO cavo = parcela.getCapitalesAsegurados().get(i);
			for (int e = 0; e < cavo.getDatosVariablesParcela().size(); e++) {
				DatoVariableParcelaVO dvpvo = cavo.getDatosVariablesParcela().get(e);
				if ("175".equals(dvpvo.getCodconcepto().toString())) {
					return dvpvo.getValor();
				}
			}
		}

		return "";
	}

	/**
	 * Metodo para obtener el valor del dato variable "FECHA FIN GARANTIAS" de la
	 * poliza en caso de que lo tenga.
	 * 
	 * @param parcela
	 * @return
	 */
	private String getFechaFinGarantias(ParcelaVO parcela) {

		// un bucle por cada capital asegurado
		for (int i = 0; i < parcela.getCapitalesAsegurados().size(); i++) {
			CapitalAseguradoVO cavo = parcela.getCapitalesAsegurados().get(i);
			for (int e = 0; e < cavo.getDatosVariablesParcela().size(); e++) {
				DatoVariableParcelaVO dvpvo = cavo.getDatosVariablesParcela().get(e);
				if ("134".equals(dvpvo.getCodconcepto().toString())) {
					return dvpvo.getValor();
				}
			}
		}

		return "";
	}

	/**
	 * PRECIO/PRODUCCION
	 */
	public Float getProduccion(Long lineaseguroid, Long idPoliza, CapitalAsegurado capAseg, String modulo)
			throws BusinessException {
		Float produccionMax = new Float(0);
		String[] produccion = new String[2];
		ParcelaVO parcelaVO = null;
		String nifasegurado = "";
		Float aux = new Float(0);
		try {

			nifasegurado = capAseg.getParcela().getPoliza().getAsegurado().getNifcif();
			parcelaVO = ParcelaUtil.getParcelaVO(capAseg.getParcela(), datosParcelaDao);

			boolean conPl = this.calculoProduccionDao.calcularConPlSql();

			if (capAseg.getTipoCapital().getCodconcepto().equals(new BigDecimal(68))) {
				if (!conPl) {
					produccion = this.getProduccionesBBDD(lineaseguroid, modulo, parcelaVO, nifasegurado, 0);
				} else {
					produccion = this.calculoProduccionDao.getProduccionPlSql(lineaseguroid, idPoliza,
							this.getDatosParcela(parcelaVO, 0), nifasegurado, modulo, parcelaVO.getCultivo(),
							parcelaVO.getVariedad(), parcelaVO.getCodProvincia(), parcelaVO.getCodComarca(),
							parcelaVO.getCodTermino(), parcelaVO.getCodSubTermino(), parcelaVO.getSIGPAC());
				}
			} else {
				produccion[0] = "0";
				produccion[1] = "0";
			}
			if (produccion[1].equals("")) {
				Iterator<CapAsegRelModulo> iter = capAseg.getCapAsegRelModulos().iterator();
				while (iter.hasNext()) {
					CapAsegRelModulo carm = iter.next();
					if (Float.compare(aux, carm.getProduccion().floatValue()) < 0) {
						aux = carm.getProduccion().floatValue();
					}
				}
				produccionMax = aux;
			} else {
				produccionMax = Float.parseFloat(produccion[1]);
			}

		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al calcular la produccion", excepcion);
			throw new BusinessException("Se ha producido un error al calcular la produccion", excepcion);
		}
		return produccionMax;
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
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private String[] getLimitesRendimiento(Long lineaseguroid, String codmodulo, ParcelaVO parcela,
			List<ItemVO> datVarPar, BigDecimal tablaRdto) throws ParseException, DAOException {
		logger.debug("init - getLimitesRendimiento");
		String[] produccion = new String[2];
		Long limRendMin = new Long(1);
		Long limRendMax = new Long(0);

		// comprobacion inicial de la tabla a usar para calcular los rendimientos
		// (LimiteRendimiento o RDTOS_CARACT_ESP)
		List<Limites> rendimientosAct = new ArrayList<Limites>();
		String tablaCalcRdtos = "limiteRendimiento";

		boolean checkRendCaracEsp = this.calculoProduccionDao.checkRendCaracEsp(lineaseguroid);
		if (checkRendCaracEsp) {
			// calculamos primero los rendimientos con la tabla RDTOS_CARACT_ESP

			HashMap<String, String> filtroMascara = new HashMap<String, String>();

			// 1. Obtenemos los campos de mascara segÃºn plan, linea, cultivo
			// variedad, provincia, comarca, termino, subtermino y modulo
			List<MascaraRendimientoCaracteristicaEspecifica> mascaras = this.calculoProduccionDao
					.getMascaraRdtosCaracEsp(lineaseguroid, codmodulo, parcela);
			for (MascaraRendimientoCaracteristicaEspecifica mascara : mascaras) {
				BigDecimal codConcepto = mascara.getId().getCodconcepto();
				if (codConcepto != null) {
					CampoMascaraFiltro campMascaraFiltro = new CampoMascaraFiltro();
					campMascaraFiltro.getCampoMascara().getDiccionarioDatosByCodconceptomasc()
							.setCodconcepto(codConcepto);

					List<CampoMascara> camposMascara = this.calculoProduccionDao.getObjects(campMascaraFiltro);

					String valor = new String();
					/* SONAR Q */
					valor = obtenerval(camposMascara, codConcepto, datVarPar);
					filtroMascara.put(codConcepto.toString(), valor);
					/* SONAR Q FIN */
				}
			}
			// 2. Ponemos a cero los campos del filtro que vayan vacios y tengan un valor
			// por defecto

			// 3. Obtenemos los Limites de Rendimiento
			List<RendimientoCaracteristicaEspecifica> rendimientos = this.calculoProduccionDao
					.dameListaRenCaracEsp(tablaRdto, lineaseguroid, codmodulo, parcela, filtroMascara);

			/* SONAR Q */
			rendimientosAct = getRendimientosAct(rendimientos, rendimientosAct, tablaCalcRdtos);
			/* SONAR Q */
		}
		if (tablaCalcRdtos.equals("limiteRendimiento")) {
			HashMap<String, String> filtroMascaraLimRdto = new HashMap<String, String>();

			List<LimiteRendimiento> rendimientos = new ArrayList<LimiteRendimiento>();
			// 1. Obtenemos los campos de mascara segÃºn plan, linea, cultivo
			// variedad, provincia, comarca, termino, subtermino y modulo
			List<MascaraLimiteRendimiento> mascaras = this.calculoProduccionDao
					.getMascaraLimiteRendimiento(lineaseguroid, codmodulo, parcela);

			for (MascaraLimiteRendimiento mascara : mascaras) {
				BigDecimal codConcepto = mascara.getId().getCodconcepto();
				if (codConcepto != null) {
					CampoMascaraFiltro campMascaraFiltro = new CampoMascaraFiltro();
					campMascaraFiltro.getCampoMascara().getDiccionarioDatosByCodconceptomasc()
							.setCodconcepto(codConcepto);

					List<CampoMascara> camposMascara = this.calculoProduccionDao.getObjects(campMascaraFiltro);

					String valor = new String();
					/* SONAR Q */
					valor = obtenerval(camposMascara, codConcepto, datVarPar);
					filtroMascaraLimRdto.put(codConcepto.toString(), valor);
					/* SONAR Q FIN */

				}
			}

			// 2. Ponemos a cero los campos del filtro que vayan vacios y tengan
			// un valor por defecto
			valorPorDefectoFitros(filtroMascaraLimRdto);

			// 3. Obtenemos los Limites de Rendimiento
			LimiteRendimientoFiltro limRendFiltro = rellenarFiltroRendimientos(tablaRdto, lineaseguroid, codmodulo,
					parcela, filtroMascaraLimRdto);

			rendimientos = this.calculoProduccionDao.getRendimientos(limRendFiltro);
			/* SONAR Q */
			rendimientosAct = obtRendimientosAct(rendimientos, rendimientosAct, tablaCalcRdtos);
			/* FIN SONAR Q */

		}

		// 4. Se aplica el coeficiente de Rendimiento Maximo Asegurable a los
		// limites obtenidos

		/* Sonar Q */
		limRendMin = obtenerlimRendMin(rendimientosAct, limRendMin);
		limRendMax = ibtenerlimRendMax(rendimientosAct, limRendMax);
		/* FIN SONAR Q */

		// 5. Obtenemos la produccion.
		if (limRendMax == 0) {
			// 5.1. Si no tiene rendimientos muestra 0 en los dos rendimientos
			produccion[0] = "0";
			produccion[1] = "0";
		} else if (limRendMax == -1) {
			// El limite superior es de libre eleccion
			produccion[0] = limRendMin.toString();
			produccion[1] = "";
		} else if (rendimientosAct.size() > 0) {
			// 5.2. SegÃºn el campo apprdto (aplicar rendimiento) se obtiene la produccion
			String apprdto = rendimientosAct.get(0).getApprdto() != null
					? rendimientosAct.get(0).getApprdto().toString()
					: "";

			produccion = calcularProduccion(limRendMin, limRendMax, apprdto, datVarPar);
		}

		logger.debug("end - getLimitesRendimiento");
		return produccion;
	}

	/**
	 * Obtiene el Coeficiente de Rendimiento Maximo Asegurable del asegurado.
	 * 
	 * @param lineaseguroid
	 * @param nifasegurado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Medida getCoefRdtoMaxAseg(Long lineaseguroid, String nifasegurado) {
		Medida medida = null;

		// obtenemos de la tabla de medidas el coeficiente de rendimiento maximo
		// asegurable.
		MedidaFiltro filtroMedida = new MedidaFiltro(lineaseguroid, nifasegurado);
		List<Medida> medidaAseg = this.calculoProduccionDao.getObjects(filtroMedida);
		if (medidaAseg != null && medidaAseg.size() > 0) {
			medida = medidaAseg.get(0);
		} else {
			filtroMedida = new MedidaFiltro(lineaseguroid, "-");
			medidaAseg = this.calculoProduccionDao.getObjects(filtroMedida);
			if (medidaAseg != null && medidaAseg.size() > 0)
				medida = medidaAseg.get(0);
		}

		return medida;
	}

	/**
	 * Calcula la produccion en Kg/Ha (rendimiento * superficie) o en Kg/arbol
	 * (rendimiento * nÃºmero de unidades).
	 * 
	 * @param limRendMin
	 * @param limRendMax
	 * @param tipMarcPlan
	 * @param datVarPar
	 * @return
	 */
	private String[] calcularProduccion(Long limRendMin, Long limRendMax, String apprdto, List<ItemVO> datVarPar) {
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

	public List<Modulo> getModulosPoliza(final Long idpoliza, final Long lineaseguroid) throws BusinessException {
		// Obtenemos los objetos modulo que tiene seleccionados en la poliza
		return this.modulosDao.getModulosPoliza(idpoliza, lineaseguroid);
	}

	public List<Modulo> getModuloAnexo(final Long idAnexo, final Long idPoliza, final Long lineaseguroid)
			throws BusinessException {
		// Obtenemos el objeto modulo que tiene seleccionado el anexo
		return this.modulosDao.getModuloAnexo(idAnexo, idPoliza, lineaseguroid);
	}

	/**
	 * Metodo que devuelve el precio maximo para un capital aseguroado concreto.
	 */
	public Float getPrecio(Long lineaseguroid, Long idPoliza, Parcela parcela, String modulo, int indiceCapital)
			throws BusinessException {
		Float precioMax = new Float(0);
		ParcelaVO parcelaVO = null;
		try {
			parcelaVO = ParcelaUtil.getParcelaVO(parcela, datosParcelaDao);
			// Obtenemos la lista de precios de la base de datos
			boolean conPl = this.calculoPrecioDao.calcularConPlSql();

			List<Precio> precios;
			if (!conPl) {
				Modulo m = new Modulo();
				ModuloId idmod = new ModuloId();
				idmod.setCodmodulo(modulo);
				idmod.setLineaseguroid(lineaseguroid);
				m.setId(idmod);

				precios = this.getPreciosBBDD(lineaseguroid, m, parcelaVO, indiceCapital);
			} else {
				String[] precios_arr = this.calculoPrecioDao.getPrecioPlSql(lineaseguroid,
						this.getDatosParcela(parcelaVO, indiceCapital), modulo, parcela.getCodcultivo() + "",
						parcela.getCodvariedad() + "", parcela.getTermino().getId().getCodprovincia() + "",
						parcela.getTermino().getId().getCodcomarca() + "",
						parcela.getTermino().getId().getCodtermino() + "",
						parcela.getTermino().getId().getSubtermino() + "");

				Precio p = new Precio();
				p.setPreciodesde(new BigDecimal(precios_arr[0]));
				p.setPreciohasta(new BigDecimal(precios_arr[1]));
				p.setPreciofijo(new BigDecimal(precios_arr[2]));

				precios = new ArrayList<Precio>();
				precios.add(p);
			}

			// Si se recupera mas de un precio hay que mostrar para el precio
			// desde el mas bajo de todos y para el precio hasta el mas alto de todos.

			/* SONAR Q */
			precioMax = obtenerPrecioMax(precios, precioMax);
			/* FIN SONAR Q */

		} catch (Exception excepcion) {
			logger.error("Se ha producido un error al calcular el precio", excepcion);
			throw new BusinessException("Se ha producido un error al calcular el precio", excepcion);
		}
		return precioMax;
	}

	/**
	 * Obtiene el precio minimo y maximo de la parcela.
	 */
	private List<PrecioVO> getPrecio(Long lineaseguroid, Long idPoliza, ParcelaVO parcela, List<Modulo> modulos,
			int indiceCapital) throws BusinessException, ParseException {

		PrecioVO precioVO = new PrecioVO();
		List<PrecioVO> preciosVO = new ArrayList<PrecioVO>(modulos.size());

		logger.debug("Inicio del calculo de precios");

		for (Modulo mod : modulos) {

			// Obtenemos la lista de precios de la base de datos
			boolean conPl = this.calculoPrecioDao.calcularConPlSql();
			logger.debug("getPrecio: " + conPl);
			List<Precio> precios;
			if (!conPl) {
				precios = this.getPreciosBBDD(lineaseguroid, mod, parcela, indiceCapital);
			} else {
				String[] precios_arr = this.calculoPrecioDao.getPrecioPlSql(lineaseguroid,
						this.getDatosParcela(parcela, indiceCapital), mod.getId().getCodmodulo(), parcela.getCultivo(),
						parcela.getVariedad(), parcela.getCodProvincia(), parcela.getCodComarca(),
						parcela.getCodTermino(), parcela.getCodSubTermino());

				// Si el pl devuelve valores menores que 0, se come el primer caracter, asi que
				// lo tratamos
				if (precios_arr[0].charAt(0) == ',')
					precios_arr[0] = "0" + precios_arr[0];

				if (precios_arr[1].charAt(0) == ',')
					precios_arr[1] = "0" + precios_arr[1];

				if (precios_arr[2].charAt(0) == ',')
					precios_arr[2] = "0" + precios_arr[2];

				Precio p = new Precio();
				p.setPreciodesde(new BigDecimal(precios_arr[0]));
				p.setPreciohasta(new BigDecimal(precios_arr[1]));
				p.setPreciofijo(new BigDecimal(precios_arr[2]));

				precios = new ArrayList<Precio>();
				precios.add(p);
			}

			// 4. Si se recupera mas de un precio hay que mostrar para el precio
			// desde el mas bajo de todos y para el precio hasta el mas alto de
			// todos.
			Float precioMin = new Float(0);
			Float precioMax = new Float(0);

			/* SONAR Q */
			precioMin = obtenerPrecioMin(precios);
			precioMax = obtenerPrecioMax(precios);
			/* SONAR Q FIN */

			precioVO = new PrecioVO();

			precioVO.setCodModulo(mod.getId().getCodmodulo());
			precioVO.setDesModulo(mod.getDesmodulo());
			precioVO.setLimMin(precioMin.toString());
			precioVO.setLimMax(precioMax.toString());

			preciosVO.add(precioVO);
		}

		return preciosVO;
	}

	/**
	 * Metodo para obtener el porcentaje de reduccion de rendimientos seleccionado
	 * por el usuario en el apartado de datos variables.
	 */
	@SuppressWarnings("rawtypes")
	private BigDecimal calcularPorcentajeReduccionRendimientoDatosVariables(Long lineaseguroid, String codmodulo,
			ParcelaVO parcela) {

		BigDecimal porcentajeReduccionRendimiento = null;

		List<DatoVariableParcelaVO> listaDatosVariablesParcela = parcela.getCapitalesAsegurados().get(0)
				.getDatosVariablesParcela();

		for (DatoVariableParcelaVO datoVariableParcela : listaDatosVariablesParcela) {

			if (datoVariableParcela.getCodconcepto() == 620 && !"".equals(datoVariableParcela.getValor())) {

				String[] listaCodigosReduccionRdtoAmb = datoVariableParcela.getValor().split(";");

				for (String codigoReduccionRdtoAmb : listaCodigosReduccionRdtoAmb) {

					ReduccionRendimientosAmbitosFiltro filtro = new ReduccionRendimientosAmbitosFiltro(lineaseguroid,
							codmodulo, new BigDecimal(codigoReduccionRdtoAmb),
							new BigDecimal(parcela.getCodProvincia()), new BigDecimal(parcela.getCodComarca()),
							new BigDecimal(parcela.getCodTermino()), parcela.getCodSubTermino().charAt(0));
					List listaReduccionRdtoAmb = this.calculoProduccionDao.getObjects(filtro);
					/* SONAR Q */
					porcentajeReduccionRendimiento = informarporcentajeReduccionRendimiento(
							porcentajeReduccionRendimiento, listaReduccionRdtoAmb);
					/* FIN SONAR Q */

				}

				break;
			}

		}

		return porcentajeReduccionRendimiento;
	}

	/**
	 * Metodo para obtener la lista de precios posibles de la base de datos. Se
	 * filtrara atendiendo a los campos de mascara.
	 * 
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param parcela
	 * @return
	 * @throws ParseException
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public List<Precio> getPreciosBBDD(Long lineaseguroid, Modulo modulo, ParcelaVO parcela, int indiceCapital)
			throws ParseException, BusinessException {

		List<ItemVO> datVarPar = getDatosParcela(parcela, indiceCapital);
		HashMap<String, String> filtroMascara = new HashMap<String, String>();

		// 1. Obtenemos las mascaras de precio segÃºn plan, linea, modulo,
		// cultivo, variedad, provincia, comarca, termino y subtermino
		List<MascaraPrecio> mascaras = this.calculoPrecioDao.getMascaraPrecio(lineaseguroid,
				modulo.getId().getCodmodulo(), parcela);

		for (MascaraPrecio mascara : mascaras) {
			BigDecimal codConcepto = mascara.getId().getCodconcepto();
			if (codConcepto != null) {
				CampoMascaraFiltro campMascaraFiltro = new CampoMascaraFiltro();
				campMascaraFiltro.getCampoMascara().getDiccionarioDatosByCodconceptomasc().setCodconcepto(codConcepto);

				List<CampoMascara> camposMascara = this.calculoPrecioDao.getObjects(campMascaraFiltro);

				String valor = new String();
				if (camposMascara.size() == 0) {
					valor = getValueConcepto(codConcepto.toString(), datVarPar);
					filtroMascara.put(codConcepto.toString(), valor);
				} else if (camposMascara.size() > 0) {
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

		// 3. Obtenemos los Precios
		PrecioFiltro precioFiltro = rellenarFiltroPrecio(lineaseguroid, modulo, parcela, filtroMascara);
		return this.calculoPrecioDao.getPrecio(precioFiltro);
	}

	/**
	 * Metodo para rellenar el filtro de limites de rendimiento
	 */
	private LimiteRendimientoFiltro rellenarFiltroRendimientos(BigDecimal tablaRdto, Long lineaseguroid,
			String codmodulo, ParcelaVO parcela, HashMap<String, String> filtroMascara) throws ParseException {
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
		limRendFiltro.getLimiteRendimiento().setTablardtos(tablaRdto);

		if (filtroMascara != null && filtroMascara.size() > 0) {
			// Rellenamos los datos variables de la parcela, obtenidos de la
			// mascara de limites de rendimiento
			datVarMascRendimiento(filtroMascara, limRendFiltro);
		}

		return limRendFiltro;
	}

	private PrecioFiltro rellenarFiltroPrecio(Long lineaseguroid, Modulo modulo, ParcelaVO parcela,
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

		precioFiltro.getPrecio().setModulo(modulo);

		VariedadId idVar = new VariedadId();
		idVar.setCodcultivo(codcultivo);
		idVar.setCodvariedad(codvariedad);
		idVar.setLineaseguroid(lineaseguroid);
		Variedad var = new Variedad();
		var.setId(idVar);
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

	/**
	 * Metodo para obtener una lista de ItemVO a partir de los datos variables de
	 * una parcela.
	 * 
	 * @param parcela
	 * @return
	 */
	public List<ItemVO> getDatosParcela(ParcelaVO parcela, int indiceCapital) {
		List<ItemVO> datosParcela = new ArrayList<ItemVO>();
		ItemVO item;

		CapitalAseguradoVO capAsegVo;
		try {
			capAsegVo = ((List<CapitalAseguradoVO>) parcela.getCapitalesAsegurados()).get(indiceCapital);
		} catch (Exception e) {
			capAsegVo = ((List<CapitalAseguradoVO>) parcela.getCapitalesAsegurados()).get(0);
		}
		// Tipo de Capital
		item = new ItemVO();
		item.setCodigo(String.valueOf(ConstantsConceptos.CODCPTO_TIPOCAPITAL));
		item.setValor(capAsegVo.getCodtipoCapital());
		datosParcela.add(item);
		// Superficie
		item = new ItemVO();
		item.setCodigo(String.valueOf(ConstantsConceptos.CODCPTO_SUPERFICIE));
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
	 */
	private void datVarMascRendimiento(HashMap<String, String> filtroMascara, LimiteRendimientoFiltro limRendFiltro)
			throws ParseException {

		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if (valor != null && !"".equals(valor)) {
				/* SONAR Q */
				limRendFiltro = informarlimRendFiltro(valor, key, limRendFiltro);
				/* FIN SONAR Q */
			}
		}
	}

	/**
	 * Rellena el filtro de Precio con los datos variables de la parcela obtenidos
	 * de la mascara de precios.
	 */
	private void datVarMascPrecio(HashMap<String, String> filtroMascara, PrecioFiltro precioFiltro)
			throws ParseException {
		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if (valor != null && !"".equals(valor)) {

				/* SONAR Q */
				precioFiltro = informarprecioFiltro(valor, key, precioFiltro);
				/* FIN SONAR Q */
			}
		}
	}

	/**
	 * Metodo para asignar valores por defecto para el calculo de produccion a
	 * aquellos datos variables que lo requieren: Tipo Marco Plantacion, Sistema
	 * Produccion, Tipo Plantacion, Practica Cultural, Tipo Capital, y Destino.
	 */
	private void valorPorDefectoFitros(HashMap<String, String> filtroMascara) {
		// Hay datos variables que si van vacios se le asigna un 0. Para poder
		// calcular el precio y produccion.

		for (String key : filtroMascara.keySet()) {
			String valor = filtroMascara.get(key);

			if ("116".equals(key) || "616".equals(key) || "173".equals(key) || "133".equals(key) || "126".equals(key)
					|| "110".equals(key)) {

				valor = (valor == null || "".equals(valor) || "-1".equals(valor)) ? "0" : valor;

				filtroMascara.put(key, valor);
			}
		}
	}

	/**
	 * Obtiene el mensaje de error para un AgrException
	 */
	private String getMsgAgrException(es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException exc) {
		String msg = "";
		boolean esRendimientoLibre = false;

		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				if (error.getCodigo() == WS_CODIGO_ERROR_RENDIMIENTO_LIBRE) {
					esRendimientoLibre = true;
				} else {
					msg += error.getMensaje() + "\n";
				}
			}
		}

		// Si es de rendimiento libre, ignoramos el resto de mensajes y le plantamos un
		// "1"
		if (esRendimientoLibre) {
			msg = String.valueOf(WS_CODIGO_ERROR_RENDIMIENTO_LIBRE);
		}

		return msg;
	}

	/**
	 * AgrException necesita filtrarse para cuando se realizan cambios masivos, ya
	 * que sólo debe "pararse" cuando sean errores de código menor que cero.
	 */
	public static String getMsgAgrExceptionMasivo(
			es.agroseguro.serviciosweb.contratacionscrendimientos.AgrException exc) {

		String msg = "";

		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				if (error.getCodigo() < 0) {
					msg += error.getMensaje() + "\n";
				} else {
					// Ignorar el resto de mensajes
				}
			}
		}
		return msg;
	}

	private Parcela getParcelaRdtos(Long lineaseguroid, ParcelaVO parcelaVO) throws DAOException {
		Parcela parcela = new Parcela();
		this.datosParcelaManager.generateParcela(parcelaVO, parcela, lineaseguroid);
		CapitalAsegurado capiAseg = generateCapitalAsegurado(parcelaVO.getCapitalAsegurado(), parcela);
		Set<CapitalAsegurado> capitalesAsegs = new HashSet<CapitalAsegurado>(1);
		capitalesAsegs.add(capiAseg);
		parcela.setCapitalAsegurados(capitalesAsegs);
		return parcela;
	}

	private Map<String, BigDecimal> dejarPolizaConSoloUnaParcelaAnexo(AnexoModificacion anexoOriginal,
			ParcelaVO parcelaVO, CapitalAseguradoVO capitalAseguradoVO) throws Exception {

		com.rsi.agp.dao.tables.anexo.Parcela parcela = null;
		Map<String, BigDecimal> mapaHojaNumero = new HashMap<String, BigDecimal>();
		String operacion = "";

		// Parcela de anexo ya creada
		if (parcelaVO.getCodParcela() != null && !parcelaVO.getCodParcela().equals("")
				&& !parcelaVO.getCodParcela().equals("-1")) {
			parcela = (com.rsi.agp.dao.tables.anexo.Parcela) datosParcelaAnexoDao
					.getObject(com.rsi.agp.dao.tables.anexo.Parcela.class, new Long(parcelaVO.getCodParcela())); // get
																													// original
			operacion = DatosParcelaAnexoManager.MODIFICAR_PARCELA;

		} else {// Nueva parcela de anexo
			parcela = new com.rsi.agp.dao.tables.anexo.Parcela();
			operacion = DatosParcelaAnexoManager.ALTA_PARCELA;
		}

		datosParcelaAnexoManager.generateParcelaAnexo(parcelaVO, parcela, operacion,
				anexoOriginal.getPoliza().getLinea().getLineaseguroid());

		// El envío ha de hacerse con hoja y número rellenos
		if (parcela.getHoja() == null || parcela.getNumero() == null) {
			parcela.setHoja(new BigDecimal(1));
			parcela.setNumero(new BigDecimal(1));
		}

		mapaHojaNumero.put("HOJA", parcela.getHoja());
		mapaHojaNumero.put("NUMERO", parcela.getNumero());

		com.rsi.agp.dao.tables.anexo.CapitalAsegurado capiAseg = generateCapitalAseguradoDeAnexo(capitalAseguradoVO,
				parcela, parcelaVO);

		Set<com.rsi.agp.dao.tables.anexo.CapitalAsegurado> colCapiAseg = new HashSet<com.rsi.agp.dao.tables.anexo.CapitalAsegurado>();
		colCapiAseg.add(capiAseg);
		parcela.setCapitalAsegurados(colCapiAseg);

		Set<com.rsi.agp.dao.tables.anexo.Parcela> colParcelas = new HashSet<com.rsi.agp.dao.tables.anexo.Parcela>();
		colParcelas.add(parcela);
		anexoOriginal.setParcelas(colParcelas);

		return mapaHojaNumero;
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

	private com.rsi.agp.dao.tables.anexo.CapitalAsegurado generateCapitalAseguradoDeAnexo(
			CapitalAseguradoVO capitalAseguradoVO, com.rsi.agp.dao.tables.anexo.Parcela parcela, ParcelaVO parcelaVO) {
		// El que pertenecera al objeto parcela
		com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado = new com.rsi.agp.dao.tables.anexo.CapitalAsegurado();
		// auxiliar

		/* SONAR Q */
		capitalAsegurado = informarCapitalAseg(capitalAseguradoVO, capitalAsegurado, parcela);
		/* FIN SONAR Q */

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

						/* SONAR Q */
						CapitalDTSVariable datoVariableParcela1 = null;
						datoVariableParcela1 = obtenerdatoVarParc1(parcelaVO, valor, datoVariableParcelaVO,
								capitalAsegurado, datoVariableParcela1);
						/* FIN SONAR Q */

						capitalAsegurado.getCapitalDTSVariables().add(datoVariableParcela1);
					} else {
						// add n datos Variables
						while (tokens.hasMoreTokens()) {
							String valueItem = tokens.nextToken();

							CapitalDTSVariable datoVariableParcela1 = null;

							datoVariableParcela1 = generateNewCapitalDTSVariable(valueItem,
									new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A',
									null);
							capitalAsegurado.getCapitalDTSVariables().add(datoVariableParcela1);
						}
					}
				} else {
					// si NO es lista multiple
					if (datoVariableParcelaVO.getCodconcepto() != 363) {

						CapitalDTSVariable dtVariable1 = null;

						dtVariable1 = generateNewCapitalDTSVariable(datoVariableParcelaVO.getValor(),
								new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A', null);

						capitalAsegurado.getCapitalDTSVariables().add(dtVariable1);
					}
				}
			} // if
		} // for datos variables

		capitalAsegurado.setTipomodificacion('A');

		return capitalAsegurado;
	}

	private CapitalDTSVariable generateNewCapitalDTSVariable(String valor, BigDecimal codconcepto,
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado, Character tipomodificacion,
			CapitalDTSVariable capitalDTSVariable) {
		if (capitalDTSVariable == null) {
			capitalDTSVariable = new CapitalDTSVariable();
		}
		capitalDTSVariable.setCapitalAsegurado(capitalAsegurado);
		capitalDTSVariable.setCodconcepto(codconcepto);
		capitalDTSVariable.setValor(valor);
		capitalDTSVariable.setTipomodificacion(tipomodificacion);
		return capitalDTSVariable;
	}

	private DatoVariableParcela getNewDatoVariableParcela(CapitalAsegurado capitalAsegurado,
			DatoVariableParcelaVO datoVariableParcelaVO, StringTokenizer tokens) {

		DatoVariableParcela datoVariableParcela1 = null;

		// Se tiene en cuenta el concepto "riesgo cubierto elegido" porque se guarda de
		// manera distinta.
		if (datoVariableParcelaVO.getCodconcepto() != ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO) {
			datoVariableParcela1 = generateNewDatoVariable(
					this.getValorDatoVariable(datoVariableParcelaVO.getValor(), tokens),
					datoVariableParcelaVO.getCodconcepto(), capitalAsegurado);
		}

		return datoVariableParcela1;
	}

	private String getValorDatoVariable(String valor, StringTokenizer tokens) {
		String resultado = "";
		if (tokens.countTokens() > 1) {
			// Lista multiple: el valor se guardara de la siguiente manera: valor1 + " " +
			// valor2 + " " + valor3...
			while (tokens.hasMoreTokens()) {
				String valueItem = tokens.nextToken();
				resultado += valueItem + " ";
			}
		} else {
			// Dato simple: se guarda el valor "tal cual".
			resultado = valor;
		}
		return resultado;
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

	/* MODIF TAM (23.11.2021) ** SONAR Q ** Inicio */
	/*
	 * Creamos nuevas funciones, para descargar las funciones principales de
	 * ifs/fors
	 */

	private String[] informarProduccion(String[] produccion, TipoCapital tc, BigDecimal codConceptoProduccion,
			Long lineaseguroid, String nifasegurado, Long idPoliza, ParcelaVO parcela, int indiceCapital, boolean conPl,
			Modulo moduloPlz) throws ParseException, BusinessException {

		if (tc.getCodconcepto().equals(codConceptoProduccion)) {
			if (!conPl) {
				produccion = this.getProduccionesBBDD(lineaseguroid, moduloPlz.getId().getCodmodulo(), parcela,
						nifasegurado, indiceCapital);
			} else {
				produccion = this.calculoProduccionDao.getProduccionPlSql(lineaseguroid, idPoliza,
						this.getDatosParcela(parcela, indiceCapital), nifasegurado, moduloPlz.getId().getCodmodulo(),
						parcela.getCultivo(), parcela.getVariedad(), parcela.getCodProvincia(), parcela.getCodComarca(),
						parcela.getCodTermino(), parcela.getCodSubTermino(), parcela.getSIGPAC());
			}
		}
		return produccion;
	}

	private List<AseguradoAutorizado> informarAsegAutorizados(Long lineaseguroid, String codmodulo, ParcelaVO parcela,
			String nifasegurado, int indiceCapital, String fechaFinGarantias, String garantizado, String codLinea)
			throws DAOException {

		List<AseguradoAutorizado> asegAutorizados = null;

		if (ArrayUtils.contains(new String[] { "310", "312", "313" }, codLinea)) {
			asegAutorizados = this.aseguradoAutorizadoDao.getAseguradosAutorizados(lineaseguroid, nifasegurado,
					codmodulo, fechaFinGarantias, garantizado, parcela.getCultivo());
		}

		if (asegAutorizados == null || asegAutorizados.isEmpty()) {
			asegAutorizados = this.aseguradoAutorizadoDao.getAseguradosAutorizados(lineaseguroid, nifasegurado,
					codmodulo, fechaFinGarantias, garantizado, "999");
		}

		if (asegAutorizados.isEmpty() && (ArrayUtils.contains(new String[] { "310", "312", "313" }, codLinea))) {
			asegAutorizados = this.aseguradoAutorizadoDao.getAseguradosAutorizados(lineaseguroid, null, codmodulo,
					fechaFinGarantias, garantizado, parcela.getCultivo());
		}

		if (asegAutorizados.isEmpty()) {
			asegAutorizados = this.aseguradoAutorizadoDao.getAseguradosAutorizados(lineaseguroid, null, codmodulo,
					fechaFinGarantias, garantizado, "999");
		}
		return asegAutorizados;

	}

	/**
	 * @throws DAOException
	 * @throws ParseException
	 **/
	private String[] Informarprod(Long lineaseguroid, String codmodulo, ParcelaVO parcela, String nifasegurado,
			int indiceCapital, String[] produccion, List<ItemVO> datVarPar, boolean existeLimiteRdto,
			BigDecimal tablaRdto) throws ParseException, DAOException {

		if (existeLimiteRdto) {
			produccion = getLimitesRendimiento(lineaseguroid, codmodulo, parcela, datVarPar, tablaRdto);
		} else {
			produccion[0] = "1";
			produccion[1] = "";
		}
		return produccion;
	}

	private BigDecimal getcoefRdtoAseg(BigDecimal coefRdtoAseg, Integer countMedidasAplicablesModulo, Medida medidaAseg,
			AseguradoAutorizado aseg) {

		if (countMedidasAplicablesModulo != null && !countMedidasAplicablesModulo.equals(new Integer(0))
				&& medidaAseg != null) {
			coefRdtoAseg = medidaAseg.getCoefrdtomaxaseg();
		} else if (aseg.getCoefsobrerdtos() != null) {
			coefRdtoAseg = aseg.getCoefsobrerdtos();
		}
		return coefRdtoAseg;
	}

	private BigDecimal getporcentajeReduccionMinimo(BigDecimal porcentajeReduccionRendimientoDatosVariables,
			BigDecimal coefRdtoAseg, BigDecimal porcentajeReduccionMinimo) {
		if (porcentajeReduccionRendimientoDatosVariables != null) {
			porcentajeReduccionMinimo = porcentajeReduccionRendimientoDatosVariables.compareTo(coefRdtoAseg) == -1
					? porcentajeReduccionRendimientoDatosVariables
					: coefRdtoAseg;
		} else {
			porcentajeReduccionMinimo = coefRdtoAseg;
		}
		return porcentajeReduccionMinimo;

	}

	private BigDecimal getablaRdto(Medida medidaAseg, BigDecimal tablaRdto) {
		if (medidaAseg != null) {
			tablaRdto = medidaAseg.getTablardto();
		}

		return tablaRdto;
	}

	/* SONAR Q */
	private List<Limites> getRendimientosAct(List<RendimientoCaracteristicaEspecifica> rendimientos,
			List<Limites> rendimientosAct, String tablaCalcRdtos) {
		if (rendimientos.size() > 0) {
			tablaCalcRdtos = "";
			for (RendimientoCaracteristicaEspecifica rend : rendimientos) {
				Limites limites = new Limites();
				if (rend.getLimiteinfrdto() != null)
					limites.setLimiteinfrdto(rend.getLimiteinfrdto());
				if (rend.getLimitesuprdto() != null)
					limites.setLimitesuprdto(rend.getLimitesuprdto());
				limites.setApprdto('S');
				rendimientosAct.add(limites);

			}
		}

		return rendimientosAct;
	}

	private List<Limites> obtRendimientosAct(List<LimiteRendimiento> rendimientos, List<Limites> rendimientosAct,
			String tablaCalcRdtos) {
		for (LimiteRendimiento rend : rendimientos) {
			Limites limites = new Limites();
			if (rend.getLimiteinfrdto() != null)
				limites.setLimiteinfrdto(rend.getLimiteinfrdto());
			if (rend.getLimitesuprdto() != null)
				limites.setLimitesuprdto(rend.getLimitesuprdto());
			if (rend.getApprdto() != null)
				limites.setApprdto(rend.getApprdto());
			rendimientosAct.add(limites);
		}

		return rendimientosAct;
	}

	private String obtenerprod1(String[] produccion, BigDecimal porcentajeReduccionMinimo) {

		if (!"".equals(produccion[1])) {
			produccion[1] = new BigDecimal(produccion[1]).multiply(porcentajeReduccionMinimo).toString()
					.replace("\\.\\d*", "");
		}
		return produccion[1];
	}

	private String obtenerval(List<CampoMascara> camposMascara, BigDecimal codConcepto, List<ItemVO> datVarPar) {

		String valor = new String();
		if (camposMascara.size() == 0) {
			valor = getValueConcepto(codConcepto.toString(), datVarPar);
		} else if (camposMascara.size() == 1) {
			CampoMascara campoMascara = camposMascara.get(0);

			BigDecimal codConceptoAsoc = campoMascara.getDiccionarioDatosByCodconceptoasoc().getCodconcepto();
			valor = getValueConcepto(codConceptoAsoc.toString(), datVarPar);
		}
		return valor;
	}

	private Long obtenerlimRendMin(List<Limites> rendimientosAct, Long limRendMin) {

		if (rendimientosAct.size() == 0) {
			limRendMin = new Long(0);
		} else if (rendimientosAct.size() == 1) {
			// Obtengo el rendimiento
			Limites rendimineto = rendimientosAct.get(0);
			if (rendimineto.getLimiteinfrdto() != null && rendimineto.getLimitesuprdto() != null) {

				Float flimRendMin = rendimineto.getLimiteinfrdto().floatValue();
				limRendMin = flimRendMin.longValue();
			} else if (rendimineto.getLimitesuprdto() == null) {
				// si esta vacio el limite maximo, se deja a libre eleccion
				limRendMin = rendimineto.getLimiteinfrdto().longValue();
			}
		} else if (rendimientosAct.size() > 1) {
			// Si recupera mas de un rendimiento obtengo el maximo y el minimo de todos
			for (Limites rendimineto : rendimientosAct) {
				if (rendimineto.getLimiteinfrdto().longValue() < limRendMin) {
					limRendMin = rendimineto.getLimiteinfrdto().longValue();
				}
			}
			Float flimRendMin = limRendMin.floatValue();
			limRendMin = flimRendMin.longValue();
		}

		return limRendMin;
	}

	private Long ibtenerlimRendMax(List<Limites> rendimientosAct, Long limRendMax) {
		if (rendimientosAct.size() == 0) {
			limRendMax = new Long(0);
		} else if (rendimientosAct.size() == 1) {
			// Obtengo el rendimiento
			Limites rendimineto = rendimientosAct.get(0);
			if (rendimineto.getLimiteinfrdto() != null && rendimineto.getLimitesuprdto() != null) {

				Float flimRendMax = rendimineto.getLimitesuprdto().floatValue();
				limRendMax = flimRendMax.longValue();
			} else if (rendimineto.getLimitesuprdto() == null) {
				// si esta vacio el limite maximo, se deja a libre eleccion
				limRendMax = new Long(-1);
			}
		} else if (rendimientosAct.size() > 1) {
			// Si recupera mas de un rendimiento obtengo el maximo y el minimo de todos
			for (Limites rendimineto : rendimientosAct) {
				if (rendimineto.getLimitesuprdto().longValue() > limRendMax) {
					limRendMax = rendimineto.getLimitesuprdto().longValue();
				}
			}
			Float flimRendMax = limRendMax.floatValue();
			limRendMax = flimRendMax.longValue();
		}

		return limRendMax;
	}

	private Float obtenerPrecioMin(List<Precio> precios) {
		Float precioMin = new Float(0);

		if (precios.size() == 1) {
			Precio precio = precios.get(0);
			if (precio.getPreciofijo().floatValue() > 0) {
				precioMin = precio.getPreciofijo().floatValue();
			} else {
				precioMin = precio.getPreciodesde().floatValue();
			}
		}
		return precioMin;
	}

	private Float obtenerPrecioMax(List<Precio> precios) {
		Float precioMax = new Float(0);

		if (precios.size() == 1) {
			Precio precio = precios.get(0);
			if (precio.getPreciofijo().floatValue() > 0) {
				precioMax = precio.getPreciofijo().floatValue();
			} else {
				precioMax = precio.getPreciohasta().floatValue();
			}
		}
		return precioMax;
	}
	
	private Float obtenerPrecioMax(List<Precio> precios, Float precioMax) {
		if (precios.size() == 1) {
			Precio precio = precios.get(0);
			if (precio.getPreciofijo().floatValue() > 0) {
				precioMax = precio.getPreciofijo().floatValue();
			} else {
				precioMax = precio.getPreciohasta().floatValue();
			}
		}
		return precioMax;
	}

	@SuppressWarnings("rawtypes")
	private BigDecimal informarporcentajeReduccionRendimiento(BigDecimal porcentajeReduccionRendimiento,
			List listaReduccionRdtoAmb) {
		if (!listaReduccionRdtoAmb.isEmpty()) {
			ReduccionRdtoAmbito reduccionRdtoAmbito = (ReduccionRdtoAmbito) listaReduccionRdtoAmb.get(0);
			BigDecimal pctReduccion = reduccionRdtoAmbito.getPctreduccion().divide(new BigDecimal(100.0));
			if (porcentajeReduccionRendimiento == null || porcentajeReduccionRendimiento.compareTo(pctReduccion) == 1)
				porcentajeReduccionRendimiento = pctReduccion;
		}
		return porcentajeReduccionRendimiento;
	}

	private LimiteRendimientoFiltro informarlimRendFiltro(String key, String valor,
			LimiteRendimientoFiltro limRendFiltro) throws ParseException {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

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
			// Num aÃ±os de Poda
			limRendFiltro.getLimiteRendimiento().setNumaniospoda(new BigDecimal(valor));
		} else if ("123".equals(key)) {
			// Sistema Cultivo
			limRendFiltro.getLimiteRendimiento().getSistemaCultivo().setCodsistemacultivo(new BigDecimal(valor));
		} else if ("616".equals(key)) {
			// Sistema Produccion
			limRendFiltro.getLimiteRendimiento().getSistemaProduccion().setCodsistemaproduccion(new BigDecimal(valor));
		} else if ("131".equals(key)) {
			// Sistema Conduccion
			limRendFiltro.getLimiteRendimiento().getSistemaConduccion().setCodsistemaconduccion(new BigDecimal(valor));
		} else if ("173".equals(key)) {
			// Tipo Plantacion
			limRendFiltro.getLimiteRendimiento().getTipoPlantacion().setCodtipoplantacion(new BigDecimal(valor));
		} else if ("133".equals(key)) {
			// Practica Cultural
			limRendFiltro.getLimiteRendimiento().getPracticaCultural().setCodpracticacultural(new BigDecimal(valor));
		} else {
			logger.fatal("El condigo concepto " + key
					+ " no esta en el filtro para el calculo de los limites de rencimiento");
		}

		return limRendFiltro;
	}

	private PrecioFiltro informarprecioFiltro(String key, String valor, PrecioFiltro precioFiltro)
			throws ParseException {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

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
			precioFiltro.getPrecio().getCodigoDenominacionOrigen().getId().setCoddenomorigen(new BigDecimal(valor));
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
		return precioFiltro;
	}

	/* SONAR Q */
	private com.rsi.agp.dao.tables.anexo.CapitalAsegurado informarCapitalAseg(CapitalAseguradoVO capitalAseguradoVO,
			com.rsi.agp.dao.tables.anexo.CapitalAsegurado capAseg, com.rsi.agp.dao.tables.anexo.Parcela parcela) {

		if (capitalAseguradoVO.getId() != null && !"".equals(capitalAseguradoVO.getId())) {
			capAseg.setId(new Long(capitalAseguradoVO.getId()));
		}

		capAseg.setParcela(parcela);
		TipoCapital tipoCapital = new TipoCapital();

		if (capitalAseguradoVO.getCodtipoCapital() != null && !capitalAseguradoVO.getCodtipoCapital().equals(""))
			tipoCapital.setCodtipocapital(new BigDecimal(capitalAseguradoVO.getCodtipoCapital()));

		capAseg.setTipoCapital(tipoCapital);

		if (capitalAseguradoVO.getSuperficie() != null && !capitalAseguradoVO.getSuperficie().equals(""))
			capAseg.setSuperficie(new BigDecimal(capitalAseguradoVO.getSuperficie()));

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

			capAseg.setPrecio(precioMax);
			// Produccion (selecciono la produccion maxima)
			BigDecimal produccionMax = new BigDecimal(0);

			for (ProduccionVO produccionVO : capitalAseguradoVO.getListProducciones())
				if (new BigDecimal(produccionVO.getLimMax()).compareTo(produccionMax) > 0)
					produccionMax = new BigDecimal(produccionVO.getLimMax());

			capAseg.setProduccion(produccionMax);
		}

		if (capitalAseguradoVO.getPrecio() != null && !capitalAseguradoVO.getPrecio().equals(""))
			capAseg.setPrecio(new BigDecimal(capitalAseguradoVO.getPrecio()));

		if (capitalAseguradoVO.getProduccion() != null && !capitalAseguradoVO.getProduccion().equals(""))
			capAseg.setProduccion(new BigDecimal(capitalAseguradoVO.getProduccion()));

		return capAseg;

	}

	private CapitalDTSVariable obtenerdatoVarParc1(ParcelaVO parcelaVO, String valor,
			DatoVariableParcelaVO datoVariableParcelaVO, com.rsi.agp.dao.tables.anexo.CapitalAsegurado capitalAsegurado,
			CapitalDTSVariable datoVariableParcela1) {

		if ("A".equals(parcelaVO.getTipoModificacion())) {
			datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
					new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'A', null);
		} else {
			datoVariableParcela1 = generateNewCapitalDTSVariable(valor,
					new BigDecimal(datoVariableParcelaVO.getCodconcepto()), capitalAsegurado, 'M',
					datoVariableParcela1);
		}
		return datoVariableParcela1;
	}

	/* MODIF TAM (23.11.2021) ** SONAR Q ** Fin */
	
	public List<BonificacionRecargo2015> getDcBonifRecargos(final Long idDc2015) throws DAOException {
		
		return this.polizaDao.getDcBonifRecargos(idDc2015);
	}
	
	public List<DistCosteSubvencion2015> getDcSubvs(final Long idDc2015) throws DAOException {
		
		return this.polizaDao.getDcSubvs(idDc2015);
	}
	
	public List<SubAseguradoENESAGanado> getSubAseguradoENESAGanados(final Long idPoliza) throws DAOException {
			
		return this.polizaDao.getSubAseguradoENESAGanados(idPoliza);
	}
	
	public List<SubAseguradoCCAAGanado> getSubAseguradoCCAAGanados(final Long idPoliza) throws DAOException {
		
		return this.polizaDao.getSubAseguradoCCAAGanados(idPoliza);
	}
}