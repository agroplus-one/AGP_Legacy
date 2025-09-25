package com.rsi.agp.core.managers.impl.sbp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SWConsultaContratacionException;
import com.rsi.agp.core.exception.SbpSinParcelasException;
import com.rsi.agp.core.managers.IConsultaSbpManager;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.ISimulacionSbpManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsSbp;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IPolizaComplementariaDao;
import com.rsi.agp.dao.models.sbp.ISimulacionSbpDao;
import com.rsi.agp.dao.models.sbp.ISobrePrecioDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Provincia;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;
import com.rsi.agp.dao.tables.sbp.ParcelaSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.PrimaMinimaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;
import com.rsi.agp.dao.tables.sbp.TasasSbp;
import com.rsi.agp.dao.tables.sbp.TipoEnvio;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class SimulacionSbpManager implements ISimulacionSbpManager {

	private static final String ANEXO = "Anexo ";
	private static final String ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO = "Error generico al calcular el sumplemento ";
	private static final String ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS = "Error durante el acceso a la base de datos";
	private static final String CULT_COMPATIBLES = "cultCompatibles";
	private static final String ERROR_EN_LA_SITUACION_ACTUALIZADA_CON_COMPLEMENTARIA = "Error en la situacion actualizada con complementaria";
	private static final String CALL_SW = "callSW";
	private static final String INCLUIR_CPL_EN_SBP = "incluirCplEnSbp";
	private static final String TIPO_POLIZA = "tipoPoliza";
	private static final Log logger = LogFactory.getLog(SimulacionSbpManager.class);
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp_sbp");
	private ISimulacionSbpDao simulacionSbpDao;
	private SeleccionPolizaManager seleccionPolizaManager;
	private ISobrePrecioDao sobrePrecioDao;
	private IHistoricoEstadosManager historicoEstadosManager;
	private IConsultaSbpManager consultaSbpManager;
	private IPolizaComplementariaDao polizaComplementariaDao;
	private IDocumentacionGedManager documentacionGedManager;
	
	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> altaPolizaSbp(PolizaSbp polizaSbp,
			String realPath,HttpServletRequest request) throws Exception {
		
		logger.debug("SimulacionSbpManager - altaPolizaSbp - init");

		Map<String, Object> parameters = new HashMap<String, Object>();
		List<ParcelaSbp> parcelaSbpsGuardar = new ArrayList<ParcelaSbp>();
		Map<String, Object> param = new HashMap<String, Object>();
		
		String codUsuario = null;
		BigDecimal idEstado = new BigDecimal(0);
		try {
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
			param = getTipoPoliza (polizaSbp,realPath);
			polizaSbp.getTipoEnvio().setId(ConstantsSbp.TIPO_ENVIO_PRINCIPAL);
			String tipoPoliza = (String) param.get(TIPO_POLIZA);
			boolean incluirCplEnSbp = (Boolean) param.get(INCLUIR_CPL_EN_SBP);
			Long lineaseguroId = polizaSbp.getPolizaPpal().getLinea().getLineaseguroid();
			BigDecimal codPlan = polizaSbp.getPolizaPpal().getLinea().getCodplan();
			List<ParcelaSbp> listParXcomarca = new ArrayList<ParcelaSbp>();
			
			// obtengo las parcelas de bbdd agrupadas por comarca para rellenar sus tasas
			if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL) || tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)){
				// de la situacion actualizada
				//Llamada a la situacion actualizada de la poliza
				SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
				PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
				try {
					respuesta = helper.getPolizaActualizada(polizaSbp.getPolizaPpal().getReferencia(), polizaSbp.getPolizaPpal().getLinea().getCodplan(), realPath);				
					parameters.put(CALL_SW, "true");
					listParXcomarca = getParcelasSbpBySituacionActualizada(respuesta ,
							polizaSbp.getPolizaPpal().getLinea().getCodplan(), polizaSbp.getPolizaPpal().getReferencia(),
							realPath,polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(),true,true);				
				} catch (Exception e) {
					// aÒadimos la alerta de BBDD
					parameters.put(CALL_SW, "false");
					logger.error(" # Error al llamar a la situacion actualizada de la poliza " , e);
					logger.debug(" # Recogemos de BBDD los datos para el alta de la poliza de sobreprecio #");
					if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL)){
						tipoPoliza = ConstantsSbp.TIPO_PPAL;
					}else{
						tipoPoliza = ConstantsSbp.TIPO_CPL;
					}
					List listCultivos = simulacionSbpDao.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
					listParXcomarca = simulacionSbpDao.getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp, true,listCultivos);
					//throw e;
				}
				if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)){
					try {
						listParXcomarca = getParcelasSbpBySituacionActualizadaYCpl(polizaSbp,tipoPoliza,incluirCplEnSbp,true,listParXcomarca);
					} catch (Exception e) {
						throw new BusinessException(
								ERROR_EN_LA_SITUACION_ACTUALIZADA_CON_COMPLEMENTARIA, e);
					}
				}
			}else{
				List listCultivos = simulacionSbpDao.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
				listParXcomarca = simulacionSbpDao.getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp, true,listCultivos);
			}
			// rellenamos en las parcelas el sbp que ha elegido el usuario,
			// las tasas de incendio y pedrisco para calcular las primas
			parcelaSbpsGuardar = simulacionSbpDao.rellenaPrimas (listParXcomarca,request,lineaseguroId);
			polizaSbp.setParcelaSbps(new HashSet<ParcelaSbp>(parcelaSbpsGuardar));
			
			/* TotalSumaAsegurada = sobreprecio * produccion de todas las parcelas
			 * PrimaNeta = sumatorio Primas incendio + sumatorio primas pedrisco de todas las parcelas
			 * Estos campos son calculados, se usan para aplicar los impuestos y no se guadan en bbdd
			 */
			List list = new ArrayList(parcelaSbpsGuardar);
			
			// calculo de impuestos
			BigDecimal importe = this.calculoImporte(list, polizaSbp.getPolizaPpal().getIdpoliza(), 
					codPlan, lineaseguroId);
			polizaSbp.setImporte(importe.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			polizaSbp.setEstadoPlzSbp(new EstadoPlzSbp());
			
			if (!StringUtils.nullToString(
					polizaSbp.getPolizaPpal().getReferencia()).equals("")) {
				polizaSbp.setReferencia(polizaSbp.getPolizaPpal()
						.getReferencia());
			}
			if (polizaSbp.getPolizaCpl() != null
					&& polizaSbp.getPolizaCpl().getIdpoliza() == null)
				polizaSbp.setPolizaCpl(null);
						
			logger.debug("POLIZASBP id -->" + polizaSbp.getId());
			// Si ya existe la poliza la borramos y la volvemos a insertar
			PolizaSbp p = (PolizaSbp) simulacionSbpDao.getObject(PolizaSbp.class, polizaSbp.getId());
			
			//Por defecto el estado lo ponemos en simulacion
			polizaSbp.getEstadoPlzSbp().setIdestado(ConstantsSbp.ESTADO_SIMULACION);
			//pero si al cargar la poliza de bbdd esta en otro estado diferente a simulacion
			//el sobreprecio lo guardamos en ese estado
			boolean estadoProvisional = false;
			if (p != null){
				if (p.getEstadoPlzSbp()!= null){
					if (p.getEstadoPlzSbp().getIdestado()!= null){
						polizaSbp.getEstadoPlzSbp().setIdestado(p.getEstadoPlzSbp().getIdestado());
						codUsuario = p.getUsuarioProvisional();
						estadoProvisional = true;
					}
				}
				simulacionSbpDao.deleteParcelas(p);
				simulacionSbpDao.delete(p);
				polizaSbp.setId(null);
			}
			//guardamos la poliza en estado simulacion
			simulacionSbpDao.saveOrUpdate(polizaSbp);
			simulacionSbpDao.evict(polizaSbp);
			
			logger.debug("POLIZASBP guardada -->" + polizaSbp.getId());
			
			if (estadoProvisional){
				idEstado = ConstantsSbp.ESTADO_GRAB_PROV;
			}else{
				idEstado = ConstantsSbp.ESTADO_SIMULACION;
				codUsuario = usuario.getCodusuario();
			}
			
			this.documentacionGedManager.saveNewGedDocPolizaSBP(polizaSbp.getId(), usuario.getCodusuario());
			
			
			try {
				//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
				historicoEstadosManager.insertaEstado(Tabla.SBP,polizaSbp.getId(),codUsuario,idEstado);
			}
			catch (Exception e) {
				logger.error("Se ha producido un error al grabar en el historico");
			}
			
			parameters.put("polizaSbp", polizaSbp);
			
			for (ParcelaSbp parcelaSbp : polizaSbp.getParcelaSbps()) {
				parcelaSbp.setPolizaSbp(polizaSbp);
				logger.debug("GrabacionSimulacion - Parcela - Prov: " + parcelaSbp.getComarca().getId().getCodprovincia() + 
					", Com: " + parcelaSbp.getComarca().getId().getCodcomarca());
				simulacionSbpDao.saveOrUpdate(parcelaSbp);
				simulacionSbpDao.evict(parcelaSbp);
			}
			parameters.put("polizaSbp", polizaSbp);
		
		} catch (DAOException e) {
			logger.error("Error durante el alta de la poliza sobreprecio", e);
			throw new BusinessException("Error durante el alta de la poliza sobreprecio", e);
		} catch (Exception e) {
			logger.error("Error generico al dar de alta una poliza de sobreprecio", e);
			throw e;
		}
		return parameters;
	}

	/**
	 * Devuelve las parcelas a mostrar en seleccionPrecios.jsp
	 * agrupadas por provincia y cultivo
	 */
	@Override
	public Map<String, Object> getSeleccionPreciosSbp(PolizaSbp polizaSbp,String realPath,List<ParcelaSbp> lstParSbp) throws BusinessException {
		
		logger.debug("SimulacionSbpManager - getSeleccionPreciosSbp - init");

		Map<String, Object> parameters= new HashMap<String,Object>();
		Map<String, Object> param= new HashMap<String,Object>();
		try {
			
			param = getTipoPoliza (polizaSbp,realPath);
			String tipoPoliza = (String) param.get(TIPO_POLIZA);
			boolean incluirCplEnSbp = (Boolean) param.get(INCLUIR_CPL_EN_SBP);
			
			logger.debug("getSeleccionPreciosSbp() -- tipoPoliza: " + tipoPoliza);
			logger.debug("getSeleccionPreciosSbp() -- incluirCplEnSbp: " + incluirCplEnSbp);
			logger.debug("getSeleccionPreciosSbp() -- lstParSbp: " + lstParSbp);

			if ((tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL)
					|| tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)) && lstParSbp == null) {
				SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
				PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
				try {

					logger.debug("poliza de sobreprecio: " + polizaSbp);
					logger.debug("poliza principal: " + polizaSbp.getPolizaPpal().getReferencia());
					logger.debug("codplan: " + polizaSbp.getPolizaPpal().getLinea().getCodplan());
					logger.debug("realpath: " + realPath);

					respuesta = helper.getPolizaActualizada(polizaSbp.getPolizaPpal().getReferencia(),
							polizaSbp.getPolizaPpal().getLinea().getCodplan(), realPath);
					logger.debug("respuesta: " + respuesta);
					lstParSbp = getParcelasSbpBySituacionActualizada(respuesta,
							polizaSbp.getPolizaPpal().getLinea().getCodplan(),
							polizaSbp.getPolizaPpal().getReferencia(), realPath,
							polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(), true, true);
					logger.debug("lstParSbp tras llamada: " + lstParSbp);
				} catch (Exception e) {

					/* DNF 24/03/2020 ESC-8985 */
					if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL)) {
						tipoPoliza = ConstantsSbp.TIPO_PPAL;
					} else {
						tipoPoliza = ConstantsSbp.TIPO_CPL;
					}
					// validamos si existen cultivos spb contratables de la poliza de BBDD al no
					// recogerlos de la situacion actualizada
					logger.debug(" ... Validamos si existen cultivos spb contratables ...");
					if (!existeCultivosSbpContratables(polizaSbp)) {
						logger.debug(" ... no existen cultivos contratables ...");
						// mapErrores.put("cultCompatibles",false);
					} else {
						logger.debug(" ... si existen cultivos contratables ...");
						@SuppressWarnings("rawtypes")
						List listCultivos = simulacionSbpDao
								.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
						// recuperamos todas las parcelas agrupando por provincia y comarca
						lstParSbp = simulacionSbpDao.getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp, true,
								listCultivos);
						// throw new BusinessException("Error durante la llamada a la situacion
						// actualizada de la poliza", e);
						logger.debug(" ..... parcelaSbpsValidar size: " + lstParSbp.size());
					}
					/* fin DNF 24/03/2020 ESC-8985 */
				}
			}
			
			//me devuelve en parameters listaSobreprecios y listaParcelas
			parameters =  simulacionSbpDao.getSobreprecios(polizaSbp,tipoPoliza,incluirCplEnSbp,false,lstParSbp);
			
			logger.debug("SimulacionSbpManager - getSeleccionPreciosSbp - end");

			return parameters;
			
		} catch (DAOException e) {
			throw new BusinessException(
					"Error durante la validacion de la poliza sobreprecio", e);
		}
	}
	
	public void bajaPolizaSbp(PolizaSbp polizaSbp) throws BusinessException {

		try {
			// primero borramos las parcelas de la poliza y luego la poliza sbp
			for (ParcelaSbp parcelaSbp : polizaSbp.getParcelaSbps()) {
				simulacionSbpDao.delete(parcelaSbp);
			}
			simulacionSbpDao.delete(polizaSbp);
			logger.debug("idPolizaSbp borrada = " + polizaSbp.getId());
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al editar la poliza sobreprecio", ex);
		}

	}

	public List<String> editarPolizaSbp(PolizaSbp polizaSbp)
			throws BusinessException {

		List<String> errores = new ArrayList<String>();
		try {

			EstadoPlzSbp estadoSbp = new EstadoPlzSbp();
			estadoSbp.setIdestado(ConstantsSbp.ESTADO_GRAB_PROV);
			polizaSbp.setEstadoPlzSbp(estadoSbp);
			simulacionSbpDao.saveOrUpdate(polizaSbp);

		} catch (Exception ex) {
			throw new BusinessException(
					"Error al editar la poliza sobreprecio", ex);
		}
		return errores;
	}
	
	public void saveOrUpdate(PolizaSbp polizaSbp){
		try {
			simulacionSbpDao.saveOrUpdate(polizaSbp);
		} catch (Exception ex) {
					
		}
	}
	
	public void grabacionProvisionalSbp(PolizaSbp polizaSbp2,Usuario usuario)
		throws BusinessException {

		try {
			EstadoPlzSbp estadoSbp = new EstadoPlzSbp();
			estadoSbp.setIdestado(ConstantsSbp.ESTADO_GRAB_PROV);
			polizaSbp2.setEstadoPlzSbp(estadoSbp);
			
			if (!StringUtils.nullToString(
					polizaSbp2.getPolizaPpal().getReferencia()).equals("")) {
				polizaSbp2.setReferencia(polizaSbp2.getPolizaPpal()
						.getReferencia());
			}
			if (polizaSbp2.getPolizaCpl() != null
					&& polizaSbp2.getPolizaCpl().getIdpoliza() == null)
				polizaSbp2.setPolizaCpl(null);
			
			simulacionSbpDao.saveOrUpdateFacturacion(polizaSbp2, usuario);
			//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
			historicoEstadosManager.insertaEstado(Tabla.SBP, 
					polizaSbp2.getId(),usuario.getCodusuario(),ConstantsSbp.ESTADO_GRAB_PROV);
			
		} catch (Exception ex) {
			throw new BusinessException(
					"Error al pasar a provisional la poliza sobreprecio", ex);
		}
		}

	public void grabacionDefinitivaSbp(PolizaSbp polizaSbp,Usuario usuario)
			throws Exception {
		
		logger.debug("SimulacionSbpManager - grabacionDefinitivaSbp - init");

		try {
			EstadoPlzSbp estadoPlzSbp = new EstadoPlzSbp();
			estadoPlzSbp.setIdestado(ConstantsSbp.ESTADO_GRAB_DEF);
			polizaSbp.setEstadoPlzSbp(estadoPlzSbp);
			
			simulacionSbpDao.saveOrUpdate(polizaSbp);
			simulacionSbpDao.evict(polizaSbp);
			//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
			historicoEstadosManager.insertaEstado(Tabla.SBP, 
					polizaSbp.getId(),usuario.getCodusuario(),ConstantsSbp.ESTADO_GRAB_DEF);
		} catch (Exception ex) {
			throw ex;
					
		}
		
		logger.debug("SimulacionSbpManager - grabacionDefinitivaSbp - end");

	}

	public Map<String, Object> validaPoliza(PolizaSbp polizaSbp,
			boolean isValidacionDef,String realPath) throws BusinessException {
		Map<String, Object> mapErrores = new HashMap<String, Object>();
		List<String> errores = new ArrayList<String>();
		Map<String, Object> param = new HashMap<String, Object>();
		
		logger.debug("**@@** SimulacionSbpManager - validaPoliza [INIT]");
		
		try {
			param = getTipoPoliza (polizaSbp,realPath);
			polizaSbp.getTipoEnvio().setId(ConstantsSbp.TIPO_ENVIO_PRINCIPAL);
			String tipoPoliza = (String) param.get(TIPO_POLIZA);			
			if (isValidacionDef) {
				// la poliza Ppal ha de estar en Enviada correcta, o enviada pendiente de confirmar o grabacion definitiva
				if (polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado()
						.compareTo(Constants.ESTADO_POLIZA_DEFINITIVA) == 0 ||
						polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado()
						.compareTo(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA) == 0 ||
						polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado()
						.compareTo(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR) == 0) {
				}else{
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_ESTADO_POL));
				}
				if (isPolizaConSiniestro(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_PLZ_SINI));
				}
				if (!isLineaEnPeriodoContratacion(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_FUERA_PER_CONT));
				}
				if (polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado().compareTo(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA) == 0){
					if (!isCultivosEnPeriodoContratacion(polizaSbp)) {
						errores.add(bundle
								.getString(ConstantsSbp.ERROR_CULTIVOS_FUERA_PER_CONT));
					}
				}
			} else {
				
				if (!isCargadasTasas(polizaSbp)) {
					errores.add(bundle.getString(ConstantsSbp.ERROR_VAL_TASAS));
				}
				if (!isCargadosImpuestos(polizaSbp)) {
					errores.add(bundle.getString(ConstantsSbp.ERROR_VAL_IMPUESTOS));
				}
				if (!isCargadoSbpCultivo(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_SBP_CULT));
				}
				if (!isCargadaPrimaMinima(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_PRI_MINM));
				}
				if (!isCargadoPeriodoContratacion(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_PER_CONT));
				}
				if (isPolizaConSiniestro(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_PLZ_SINI));
				}
				if (!isLineaEnPeriodoContratacion(polizaSbp)) {
					errores.add(bundle
							.getString(ConstantsSbp.ERROR_VAL_FUERA_PER_CONT));
				}
				
				if (!tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL) && !tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)){
					if (!existeCultivosSbpContratables(polizaSbp)) {
						errores.add(bundle
								.getString(ConstantsSbp.ERROR_CULTIVOS_INCOMPATIBLES));
					}
				}
				// si no hay errores validamos si las parcelas de sbp de la poliza tienen tasas asignadas
				// y que al menos hay algun cultivo es compatible para sobreprecio y en fecha
				if 	(errores.size() < 1){
				    mapErrores = validaTasa(polizaSbp, realPath);
					boolean tasaValida = false;
					boolean cultCompatibles = false;
					cultCompatibles = (Boolean) mapErrores.get(CULT_COMPATIBLES);
					if (!cultCompatibles){
						errores.add(bundle
								.getString(ConstantsSbp.ERROR_CULTIVOS_INCOMPATIBLES));
					}else{
						tasaValida = (Boolean) mapErrores.get("tasa");
						if (!tasaValida){
							errores.add(bundle
									.getString(ConstantsSbp.ALERT_NO_TASAS_SBP));
						}
					}
				}
			}
		} catch (BusinessException e) {
			throw new BusinessException(
					"Error durante la validacion de la poliza sobreprecio", e);
		}
		
		/*DNF ESC-8985 19/03/2020*/
		logger.debug("TamaÒo errores: " + errores.size());
		for(String valor : errores) {
			logger.debug("valor de la lista errores :" + valor);
		}
		logger.debug("mapErrores: " + mapErrores);
		/*fin DNF ESC-8985 19/03/2020*/
		
		mapErrores.put("errores", errores);
		
		logger.debug("**@@** SimulacionSbpManager - validaPoliza [END]");
		
		return mapErrores;
	}
	/**
	 * valida si las parcelas de sbp de la poliza tienen tasas asignadas
	 * @param polizaSbp
	 * @param realPath
	 * @return boolean true-> si tienen tasas, false -> no tienen
	 * @throws BusinessException
	 */
	
	@SuppressWarnings("rawtypes")
	private Map<String, Object> validaTasa(PolizaSbp polizaSbp, String realPath) throws BusinessException {
		Map<String, Object> param = new HashMap<String, Object>();
		List<ParcelaSbp> parcelaSbpsValidar = new ArrayList<ParcelaSbp>();
		Map<String, Object> mapErrores = new HashMap<String, Object>();
		try {
			param = getTipoPoliza(polizaSbp, realPath);
			String tipoPoliza = (String) param.get(TIPO_POLIZA);
			polizaSbp.getTipoEnvio().setId(ConstantsSbp.TIPO_ENVIO_PRINCIPAL);
			boolean incluirCplEnSbp = (Boolean) param.get(INCLUIR_CPL_EN_SBP);
			mapErrores.put(CULT_COMPATIBLES, true);
			if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL)
					|| tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)) {
				SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
				PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
				try {

					logger.debug("polizaSbp: " + polizaSbp);
					logger.debug("polizaSbp.getPolizaPpal(): " + polizaSbp.getPolizaPpal());
					logger.debug(
							"polizaSbp.getPolizaPpal().getReferencia(): " + polizaSbp.getPolizaPpal().getReferencia());
					logger.debug("polizaSbp.getPolizaPpal().getLinea(): " + polizaSbp.getPolizaPpal().getLinea());
					logger.debug("polizaSbp.getPolizaPpal().getLinea().getCodplan(): "
							+ polizaSbp.getPolizaPpal().getLinea().getCodplan());
					logger.debug("realPath: " + realPath);

					respuesta = helper.getPolizaActualizada(polizaSbp.getPolizaPpal().getReferencia(),
							polizaSbp.getPolizaPpal().getLinea().getCodplan(), realPath);
					parcelaSbpsValidar = getParcelasSbpBySituacionActualizada(respuesta,
							polizaSbp.getPolizaPpal().getLinea().getCodplan(),
							polizaSbp.getPolizaPpal().getReferencia(), realPath,
							polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(), true, true);
				} catch (Exception e) {
					logger.error(" # Error al llamar a la situacion actualizada de la poliza ", e);
					logger.debug(" # Recogemos de BBDD los datos para el calculo de la poliza de sobreprecio #");
					if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL)) {
						tipoPoliza = ConstantsSbp.TIPO_PPAL;
					} else {
						tipoPoliza = ConstantsSbp.TIPO_CPL;
					}
					// validamos si existen cultivos spb contratables de la poliza de BBDD al no
					// recogerlos de la situacion actualizada
					logger.debug(" # Validamos si existen cultivos spb contratables #");
					if (!existeCultivosSbpContratables(polizaSbp)) {
						logger.debug(" . no existen cultivos contratables");
						mapErrores.put(CULT_COMPATIBLES, false);
					} else {
						logger.debug(" . si existen cultivos contratables");
						List listCultivos = simulacionSbpDao
								.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
						// recuperamos todas las parcelas agrupando por provincia y comarca
						parcelaSbpsValidar = simulacionSbpDao.getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp,
								true, listCultivos);
						// throw new BusinessException("Error durante la llamada a la situacion
						// actualizada de la poliza", e);
						logger.debug(" . parcelaSbpsValidar size: " + parcelaSbpsValidar.size());
					}
				}

				if (parcelaSbpsValidar.size() < 1) {
					mapErrores.put(CULT_COMPATIBLES, false);
				} else {
					if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)) {
						try {
							parcelaSbpsValidar = getParcelasSbpBySituacionActualizadaYCpl(polizaSbp, tipoPoliza,
									incluirCplEnSbp, true, parcelaSbpsValidar);
						} catch (Exception e) {
							throw new BusinessException(ERROR_EN_LA_SITUACION_ACTUALIZADA_CON_COMPLEMENTARIA, e);
						}
					}
				}
			} else {
				List listCultivos = simulacionSbpDao
						.getCultivosSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid());
				// recuperamos todas las parcelas agrupando por provincia y comarca
				parcelaSbpsValidar = simulacionSbpDao.getParcelasParaSbp(polizaSbp, tipoPoliza, incluirCplEnSbp, true,
						listCultivos);
			}

			for (int i = 0; i < parcelaSbpsValidar.size(); i++) {
				ParcelaSbp par = (ParcelaSbp) parcelaSbpsValidar.get(i);
				TasasSbp tasa = simulacionSbpDao.getTasa(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(), par);
				if (tasa == null) {
					mapErrores.put("tasa", false);
					mapErrores.put("parSbp", parcelaSbpsValidar);
					return mapErrores;
				}
			}
			if (parcelaSbpsValidar.isEmpty() || parcelaSbpsValidar.size() < 1) {
				mapErrores.put(CULT_COMPATIBLES, false);
				logger.debug(" # sin cultivos compatibles #");
			} else {
				mapErrores.put("tasa", true);
				mapErrores.put("lstParSbp", parcelaSbpsValidar);
			}

			/* DNF ESC-8985 19/03/2020 */
			logger.debug("mapErrores cultCompatibles: " + mapErrores.get(CULT_COMPATIBLES));
			logger.debug("mapErrores tasa: " + mapErrores.get("tasa"));
			logger.debug("mapErrores parSbp: " + mapErrores.get("parSbp"));
			logger.debug("mapErrores lstParSbp: " + mapErrores.get("lstParSbp"));
			logger.debug("mapErrores: " + mapErrores);
			/* fin DNF ESC-8985 19/03/2020 */

			return mapErrores;

		} catch (DAOException e) {
			throw new BusinessException("Error durante la validacion de tasas la poliza sobreprecio", e);
		}
	}
	
	
	/**
	 * Calcula impuestos de sobreprecio
	 * @param totSumaAsegurada
	 * @param primaNeta
	 * @param codPlan
	 * @return
	 * @throws DAOException
	 */
	private BigDecimal calculoImpuestos(BigDecimal totSumaAsegurada,BigDecimal primaNeta,BigDecimal codPlan) throws DAOException {
		
		
		logger.debug("SimulacionSbpManager - calculoImpuestos - init");
		
		logger.debug("Valor prima neta: " + primaNeta);
		
		BigDecimal arbitrio = new BigDecimal(0);
		BigDecimal consorcio = new BigDecimal(0);
		BigDecimal liqEntidades = new BigDecimal(0);
		BigDecimal ips = new BigDecimal(0);
		BigDecimal primaTotalSeguro = new BigDecimal(0);
		try{
			logger.debug("Calculo de impuestos - Plan " + codPlan);
			
			// Obtenemos los impuestos de bbdd
			List <MtoImpuestoSbp> listImpuestos = simulacionSbpDao.getImpuestos(codPlan);
			
			logger.debug("Calculo de impuestos - Impuestos encontrados " + listImpuestos.size());
			
			for (int i=0; i<listImpuestos.size();i++){
				logger.debug("Calculo de impuestos - Iteracion " + i+1);
				
				MtoImpuestoSbp impuesto = (MtoImpuestoSbp)listImpuestos.get(i);
				
				logger.debug("Impuesto " + impuesto != null ? impuesto.getImpuestoSbp().getClass() + " - " + impuesto.getValor() : "Nulo");
				
				if(impuesto.getImpuestoSbp().getCodigo().equals(ConstantsSbp.ARBITRIO)){
					arbitrio = (primaNeta.multiply(impuesto.getValor())).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					logger.debug("Calculo de impuestos - arbitrio " + arbitrio);
					
				}else if(impuesto.getImpuestoSbp().getCodigo().equals(ConstantsSbp.CONSORCIO)){
					consorcio = totSumaAsegurada.multiply(impuesto.getValor()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					logger.debug("Calculo de impuestos - consorcio " + consorcio);
					
				}else if(impuesto.getImpuestoSbp().getCodigo().equals(ConstantsSbp.LIQ_ENTIDADES)){
					liqEntidades = primaNeta.multiply(impuesto.getValor()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					logger.debug("Calculo de impuestos - liqEntidades " + liqEntidades);
					
				}else if(impuesto.getImpuestoSbp().getCodigo().equals(ConstantsSbp.IPS)){
					ips = (primaNeta.add(arbitrio)).multiply(impuesto.getValor()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					logger.debug("Calculo de impuestos - ips " + ips);
				}
			}
			
			logger.debug("Valor prima neta: " + primaNeta);
			
			primaTotalSeguro = primaNeta.add(arbitrio).add(consorcio).add(liqEntidades).add(ips);
			
			logger.debug("Calculo de impuestos - primaTotalSeguro " + primaTotalSeguro);
			
		
		}catch (DAOException e) {
			logger.error("error al obtener los datos de impuestos de sobreprecio.");
			throw e; 
		}
		
		logger.debug("SimulacionSbpManager - calculoImpuestos - end");

		return primaTotalSeguro;
	}
	
	/**
	 * En funcion del estado,de si tiene copy y de si es ppal o cpl
	 * devuelve el tipo de poliza para posteriormente obtener sus parcelas y produccion
	 * @param polizaSbp
	 * @param realPath
	 * @return
	 * @throws BusinessException
	 */	
	private Map<String, Object> getTipoPoliza(PolizaSbp polizaSbp,String realPath) throws BusinessException {
		
		boolean incluirCplEnSbp = false;
		Map<String, Object> param = new HashMap<String,Object>();
		String tipoPoliza = "";
		BigDecimal estadoPpal = polizaSbp.getPolizaPpal().getEstadoPoliza().getIdestado();
		// CPl
		if (polizaSbp.getIncSbpComp() != null && polizaSbp.getIncSbpComp().equals('S') && polizaSbp.getPolizaCpl() != null
				&& polizaSbp.getPolizaCpl().getIdpoliza() != null) {
			
			BigDecimal estadoCPl = polizaSbp.getPolizaCpl().getEstadoPoliza().getIdestado();
			incluirCplEnSbp = true;
			// correcta-correcta Copy
			if (estadoPpal.compareTo(ConstantsSbp.ENVIADA_CORRECTA) == 0 && estadoCPl.compareTo(ConstantsSbp.ENVIADA_CORRECTA) == 0) {
				tipoPoliza = ConstantsSbp.TIPO_SIT_ACTUAL;		
			} else {
				if (estadoPpal.compareTo(ConstantsSbp.ENVIADA_CORRECTA) == 0 && (estadoCPl.compareTo(ConstantsSbp.GRABACION_DEFINITIVA) == 0 ||
						estadoCPl.compareTo(ConstantsSbp.GRABACION_PROVISIONAL) == 0 || estadoCPl.compareTo(ConstantsSbp.PENDIENTE_VALIDACION) == 0)) {
					tipoPoliza = ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL;
				}else{// demas estados bbdd
					tipoPoliza = ConstantsSbp.TIPO_CPL;
				}
				
			}
		// PPl
		} else {
			polizaSbp.setIncSbpComp('N');
			// correcta Sit Actualizada
			if (estadoPpal.compareTo(ConstantsSbp.ENVIADA_CORRECTA) == 0) {
				tipoPoliza = ConstantsSbp.TIPO_SIT_ACTUAL;			
			// demas estados bbdd
			} else {
				tipoPoliza = ConstantsSbp.TIPO_PPAL;
			}
		}
		
		param.put(TIPO_POLIZA, tipoPoliza);
		param.put(INCLUIR_CPL_EN_SBP, incluirCplEnSbp);
		logger.debug("tipoPoliza: " + tipoPoliza);
		return param;
	}



	/**
	 * metodo que valida si la poliza cumple con la linea y cultivos para SBP 0 =
	 * valida, 1 = linea incompatible, 2 = cultivo incompatible
	 * @return devuelve una lista de errores con el mensaje correspondiente
	 * @throws BusinessException
	 */
	public List<String> validarPolizaPpalParaSbp(Poliza poliza)
			throws BusinessException {

		List<String> errores = new ArrayList<String>();
		Integer validacion = 0;
		try {
			validacion = validarLineaYCultivoSBP(poliza);
			if (validacion == 1) // linea incompatible
				errores.add(bundle
						.getString(ConstantsSbp.ERROR_LINEA_INCOMPATIBLE));
			else if (validacion == 2) // cultivo incompatible
				errores.add(bundle
						.getString(ConstantsSbp.ERROR_CULTIVOS_INCOMPATIBLES));
		} catch (BusinessException e) {
			throw new BusinessException(
					"Error durante la validacion de la poliza", e);
		}
		return errores;
	}
	
	public PolizaSbp existePolizaSbp(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.existePolizaSbp(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}

	}
	
	/**
	 * Comprueba la compatibilidad con linea para Sbp
	 * @return true = valida
	 * @throws BusinessException
	 */
	public boolean validarLineaParaSbp(String linea, String plan)
			throws BusinessException {
		try {
			boolean cumplelinea = false;
			List<Sobreprecio> lineas = new ArrayList<Sobreprecio>();
			// comprobamos si la linea de la poliza es viable para el
			// sobreprecio
			lineas = sobrePrecioDao.getLineaSbpFromLineaPlan(linea,plan);
			if (lineas.size() > 0) { // cumple Linea para Sbp
				cumplelinea = true;
			}
			return cumplelinea;
		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}
	
	/**
	 * Comprueba la compatibilidad con linea y cultivos para Sbp
	 * @return 0 = valida, 1 = linea incompatible, 2 = cultivo incompatible
	 * @throws BusinessException
	 */
	private Integer validarLineaYCultivoSBP(Poliza poliza)
			throws BusinessException {
		try {
			Integer resultado = 0;
			boolean cumpleLinea = false;
			// comprobamos si la linea de la poliza es viable para el sobreprecio
			
			BigDecimal codPlan = this.getPlanSbp();
			logger.debug("maxPlan: " + codPlan);
			Map<Long, List<BigDecimal>> cultivosPorLinea = consultaSbpManager.getCultivosPorLineaseguroid(codPlan);

			if (cultivosPorLinea == null){
				logger.debug("cultivosPorLinea es null");
			}else{
				logger.debug("cultivosPorLinea size:"+cultivosPorLinea.size());
			}
			if (poliza != null){
				if (poliza.getLinea() != null){
					if (null !=poliza.getLinea().getLineaseguroid()){
						logger.debug("poliza.getLinea().getLineaseguroid():"+poliza.getLinea().getLineaseguroid());
					}else{
						logger.debug("poliza.getLinea().getLineaseguroid() es null");
					}
				}else{
					logger.debug("poliza.getLinea() es null");
				}
			}
			logger.debug("lineaseguroid:"+poliza.getLinea().getLineaseguroid());
			if (cultivosPorLinea.containsKey(poliza.getLinea().getLineaseguroid())){
        		cumpleLinea = true;
        	}
			
			if (cumpleLinea) {
				resultado = 0;
			} else { // no cumple linea
				resultado = 1;
			}
			return resultado;
		} catch (Exception daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}
	
	/**
	 * Comprueba si estan cargadas las tasas para el plan y linea de la poliza
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si estan cargadas las tasas, false si no
	 * @throws BusinessException
	 */
	private boolean isCargadasTasas(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isCargadasTasas(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}
	
	/**
	 * Comprueba si estan cargadas las tasas para el plan y linea de la poliza
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si estan cargadas las tasas, false si no
	 * @throws BusinessException
	 */
	private boolean isCargadosImpuestos(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isCargadosImpuestos(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}

	/**
	 * Comprueba si estan cargado el sobreprecio del cultivo
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si esta cargado el sobreprecio del cultivo, false si no
	 */
	private boolean isCargadoSbpCultivo(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isCargadoSbpCultivo(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}

	/**
	 * Comprueba si estan cargado el periodo de contratacion para ese plan/linea
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si esta cargado el periodo de contratacion para ese
	 *         plan/linea, false si no
	 */
	private boolean isCargadoPeriodoContratacion(PolizaSbp polizaSbp)
			throws BusinessException {
	
		try {
	
			return simulacionSbpDao.isCargadoPeriodoContratacion(polizaSbp);
	
		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	
	}

	/**
	 * Comprueba si estan cargado la prima minima para ese plan/linea
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si esta cargada la prima minima, false si no
	 */
	private boolean isCargadaPrimaMinima(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isCargadaPrimaMinima(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}

	/**
	 * Comprueba si la poliza en la que se basa la de sobreprecio tiene algun
	 * siniestro enviado para 'Incendio' o 'Pedrisco'
	 * 
	 * @param polizaSbp
	 *            Objeto que encapusula los datos de la poliza de sobreprecio
	 * @return True si tiene algun sinestro enviado, false si no
	 */
	private boolean isPolizaConSiniestro(PolizaSbp polizaSbp)
			throws BusinessException {
		
		logger.debug("**@@** SimulacionSbpManager - isPolizaConSiniestro [INIT]");
		try {
			// si la poliza tiene siniestros comprobamos el tipo
			if (polizaSbp.getPolizaPpal().getTienesiniestros().equals(
					new Character('S'))) {
				return simulacionSbpDao.isPolizaConSiniestro(polizaSbp);
			} else {
				return false;
			}
		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}

	/**
	 * Comprueba si la linea esta dentro del periodo de contratacion para
	 * sobreprecio
	 * 
	 * @param polizaSbp
	 * @return True si la fecha actual esta dentro del periodo de contratacion,
	 *         false si no
	 */
	public boolean isLineaEnPeriodoContratacion(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isLineaEnPeriodoContratacion(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}
	
	/**
	 * Comprueba si los cultivos de la poliza estan en periodo de contratacion
	 * para sobreprecio
	 * @param polizaSbp
	 * @return
	 * @throws BusinessException
	 */
	public boolean isCultivosEnPeriodoContratacion(PolizaSbp polizaSbp)
			throws BusinessException {
		try {

			return simulacionSbpDao.isCultivosEnPeriodoContratacion(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}

	/**
	 * Chequea si el cultivo de sobreprecio esta en fecha de contratacion
	 * para sobreprecio
	 * @param polizaSbp
	 * @return
	 * @throws BusinessException
	 */
	public boolean existeCultivosSbpContratables(PolizaSbp polizaSbp)
			throws BusinessException {
		try {
			return simulacionSbpDao.existeCultivosSbpContratables(polizaSbp);

		} catch (DAOException daoe) {
			throw new BusinessException(
					ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	}
	
	

	public PolizaSbp getPolizaSbp(Long idPolizaSbp) throws BusinessException {
		try {

			return (PolizaSbp) simulacionSbpDao.getObject(PolizaSbp.class,
					idPolizaSbp);
		} catch (Exception dao) {
			logger
					.error("Se ha producido al obtener la poliza de sobreprecio: "
							+ dao.getMessage());
			throw new BusinessException(
					"Se ha producido al obtener la poliza de sobreprecio:", dao);
		}
	}

	/**
	 * Borra o la poliza de sobreprecio asociada a la poliza actual en el caso de que exista
	 * @param parametros
	 * @param p
	 */
	public boolean actualizarPolizaSbp(Poliza p) {
		
		logger.info("actualizarSbp - Actualizacion de la poliza de sobreprecio");
		
		boolean SbpBorrada = false;
		Set<PolizaSbp> lstPolSbp = new HashSet<PolizaSbp>();
		lstPolSbp = p.getPolizaPrincipal();
		if (lstPolSbp != null){
			for (PolizaSbp polSbp:lstPolSbp){
				try {
					// borramos la Sbp
						this.bajaPolizaSbp(polSbp);
						logger.info("actualizarSbp - idPolizaSbp borrada = " + polSbp.getId());
						SbpBorrada = true;
					
				} catch (Exception e) {
					logger.info("actualizarSbp - Error al actualizar la poliza de sobreprecio asociada");
					logger.error(e);
				}
			}
		}
		return SbpBorrada;
		
	}

	/**
	 * Metodo que actualiza la poliza de sobreprecio con la Complementaria
	 * @throws Exception 
	 * 
	 */
	public Map<String, Object> recalculaPolizaSbpConCpl(Poliza polPpal, Poliza polCpl,
			PolizaSbp polSbp, String realPath,Usuario usuario,HttpServletRequest request) throws Exception {
		
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			
			PolizaSbp polizaSbp = new PolizaSbp();
			// anhadimos los datos de la Ppal
			polizaSbp.setPolizaPpal(polPpal);
			
			// anhadimos los datos de la Cpl
			if (polCpl != null){
				polizaSbp.setPolizaCpl(polCpl);
			}
			polizaSbp.setIncSbpComp('S');
			param = this.recalculaSbp(realPath, polSbp,usuario,false);
			
			return param;
		} catch (BusinessException e) {
			logger.error("Ocurrio un error en la actualizacion de la poliza de Sbp con la Cpl,"	+ e);
			return param;
		} catch (Exception e) {
			logger.error("Error generico al recalcular la poliza de sobreprecio con cpl");
			throw e;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> recalculaSbp (String realPath,PolizaSbp polizaSbp,Usuario usuario, boolean definitiva)throws Exception {
		logger.debug("Recalculamos la poliza de sobreprecio... definitiva: "+definitiva);
		List<ParcelaSbp> oldParcSbp = new ArrayList<ParcelaSbp>();	
		Map<String,Object> params = new HashMap<String, Object>();
		Map<String,Object> parameters = new HashMap<String, Object>();
		List<Sobreprecio> sbpList = new ArrayList<Sobreprecio>();
		
		try {
			Map<String,Object> param = this.getTipoPoliza(polizaSbp, realPath);
			polizaSbp.getTipoEnvio().setId(ConstantsSbp.TIPO_ENVIO_PRINCIPAL);
			String tipoPoliza = (String) param.get(TIPO_POLIZA);
			logger.debug("# tipoPoliza: "+tipoPoliza);
			boolean incluirCplEnSbp = (Boolean) param.get(INCLUIR_CPL_EN_SBP);
			int count =0;
			List<ParcelaSbp> listaParcelas = new ArrayList<ParcelaSbp>();
			//me devuelve en parameters listaSobreprecios y listaParcelas
			if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL) || tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)){
				SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
				PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
				
				try {
					respuesta = helper.getPolizaActualizada(polizaSbp.getPolizaPpal().getReferencia(), polizaSbp.getPolizaPpal().getLinea().getCodplan(), realPath);
					
					listaParcelas = getParcelasSbpBySituacionActualizada(respuesta ,
							polizaSbp.getPolizaPpal().getLinea().getCodplan(), polizaSbp.getPolizaPpal().getReferencia(),
							realPath,polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(),true,true);			
				} catch (DAOException e) {
					throw new BusinessException("Error al recuperar la situacion actualizada de la poliza ", e);
				} catch (Exception e) {
					logger.error(" # Recalcular - Error al llamar a la situacion actualizada de la poliza " , e);
					if (definitiva){
						params.put(CALL_SW, "false");
						return params;
					}else{
						throw new BusinessException(ERROR_EN_LA_SITUACION_ACTUALIZADA_CON_COMPLEMENTARIA, e);
					}
				}
			}
			if (tipoPoliza.equals(ConstantsSbp.TIPO_SIT_ACTUAL_CON_CPL)){
				try {
					listaParcelas = getParcelasSbpBySituacionActualizadaYCpl(polizaSbp,tipoPoliza,incluirCplEnSbp,true,listaParcelas);	
				} catch (Exception e) {
					throw new BusinessException(
							ERROR_EN_LA_SITUACION_ACTUALIZADA_CON_COMPLEMENTARIA, e);
				}
			}
			
			// Si en este punto el listado de parcelas est√° vac√≠o significa que en la situaci√≥n actualizada no hay parcelas para
			// sobreprecio en periodo de contrataci√≥n
			if (listaParcelas == null || listaParcelas.isEmpty()) {
				throw new SbpSinParcelasException();
			}
			
			
			parameters =  simulacionSbpDao.getSobreprecios(polizaSbp,tipoPoliza,incluirCplEnSbp,false,listaParcelas);
			List<Sobreprecio> listSobreprecios = (List<Sobreprecio>)parameters.get("listaSobreprecios");
			
			// busco las parcelas en parcelasSbp y comparo
			//oldParcSbp = simulacionSbpDao.getParcelasSbpbyIdPolizaPpal(false);
			try {
				oldParcSbp = new ArrayList<ParcelaSbp>(polizaSbp.getParcelaSbps());
			}
			catch (Exception e) {
				logger.error("Error al obtener las parcelas de sobreprecio", e);
			}
			
			// recorro las parcelas antiguas y actualizo los sbp
			List<BigDecimal> lstCultivos= new ArrayList<BigDecimal>();
			for (int i=0;i<listSobreprecios.size();i++){
				Sobreprecio s = listSobreprecios.get(i);
				boolean encontrada= false;
				for (int j=0;j<oldParcSbp.size();j++){
					ParcelaSbp p = oldParcSbp.get(j);
					if (!lstCultivos.contains(p.getCultivo().getId().getCodcultivo())){
						if (s.getCultivo().getId().getCodcultivo().equals(p.getCultivo().getId().getCodcultivo()) &&
								(p.getComarca().getProvincia().getCodprovincia().equals(s.getProvincia().getCodprovincia()))){
							s.setSbpAsegurado(p.getSobreprecio());
							sbpList.add(s);
							lstCultivos.add(p.getCultivo().getId().getCodcultivo());
							encontrada = true;
							j=oldParcSbp.size();
						}else if (s.getCultivo().getId().getCodcultivo().equals(p.getCultivo().getId().getCodcultivo())&&
								(s.getProvincia().getCodprovincia().equals(new BigDecimal(99)))){
							s.setSbpAsegurado(p.getSobreprecio());
							sbpList.add(s);
							lstCultivos.add(p.getCultivo().getId().getCodcultivo());
							encontrada = true;
							j=oldParcSbp.size();
						}else if (s.getCultivo().getId().getCodcultivo().equals(new BigDecimal(999))&&
								(s.getProvincia().getCodprovincia().equals(new BigDecimal(99)))){
							s.setSbpAsegurado(p.getSobreprecio());
							sbpList.add(s);
							lstCultivos.add(p.getCultivo().getId().getCodcultivo());
							encontrada = true;
							j=oldParcSbp.size();
						}
					}
				}
				if (!encontrada){
					count++;
					sbpList.add(s); // si no lo encuentra en la parcela lo anhadimos porque es nuevo y mostramos msj
				}
			}
			// Hay parcelas nuevas y hay que mostrar sobreprecios para que el usu meta el valor del nuevo
			if (count>0){
				logger.debug(" # hay parcelas nuevas y hay que mostrar sobreprecios #");
				params.put("mensaje",  bundle.getString(ConstantsSbp.MSJ_RECALCULAR_SBP));
				params.put("listaSobreprecios", sbpList);
			}else{
				//recalculamos los valores que no hemos guardado en bbdd
				Long lineaseguroId = polizaSbp.getPolizaPpal().getLinea().getLineaseguroid();
				BigDecimal codPlan = polizaSbp.getPolizaPpal().getLinea().getCodplan();
				
				// me quedo con las parcelas nuevas y las aÒado al set de parcelas de la polizaSbp				
				for (ParcelaSbp parsbp: listaParcelas){
					// actualizamos las primas 
					parsbp.setPrimaNetaIncendio(((parsbp.getTotalProduccion().multiply(parsbp.getSobreprecio())).multiply(parsbp.getTasaIncendio())).divide(
							new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP));
					parsbp.setPrimaNetaPedrisco(((parsbp.getTotalProduccion().multiply(parsbp.getSobreprecio())).multiply(parsbp.getTasaPedrisco())).divide(
							new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP));
				}

				
				List list = new ArrayList(listaParcelas);
				//Set<ParcelaSbp> setParcelasSbp = new HashSet<ParcelaSbp>(); //(listaParcelas);
				
				
				//polizaSbp.setParcelaSbps(setParcelasSbp);
				//parcelas = new ArrayList<ParcelaSbp>(polizaSbp.getParcelaSbps());
				
				// calculo de impuestos
								
				BigDecimal importe = this.calculoImporte(list, polizaSbp.getPolizaPpal().getIdpoliza(), 
						codPlan, lineaseguroId);
				polizaSbp.setImporte(importe.setScale(2, BigDecimal.ROUND_HALF_UP));
				if (!StringUtils.nullToString(
						polizaSbp.getPolizaPpal().getReferencia()).equals("")) {
					polizaSbp.setReferencia(polizaSbp.getPolizaPpal()
							.getReferencia());
				}
				
				if (polizaSbp.getPolizaCpl() != null && polizaSbp.getPolizaCpl().getIdpoliza() == null){
					polizaSbp.setPolizaCpl(null);
					logger.debug(" # recalculaSbp - polizaCpl = null #");
				}
				polizaSbp.setEstadoPlzSbp(new EstadoPlzSbp());
				polizaSbp.getEstadoPlzSbp().setIdestado(ConstantsSbp.ESTADO_GRAB_PROV);
				
				logger.debug(" # listParcelas.size: "+ listaParcelas.size());
				logger.debug(" # parcelasBBDD.size: "+ polizaSbp.getParcelaSbps().size());
				int parcelasActualizadas = 0;
				try{
					for (ParcelaSbp parBBDD: polizaSbp.getParcelaSbps()){
						for (ParcelaSbp parSit: listaParcelas){
							if (parBBDD.getCultivo().getId().getCodcultivo().equals(parSit.getCultivo().getId().getCodcultivo()) &&
									(parBBDD.getComarca().getId().getCodcomarca().equals(parSit.getComarca().getId().getCodcomarca())) &&
									(parBBDD.getComarca().getProvincia().getCodprovincia().equals(parSit.getComarca().getProvincia().getCodprovincia()))){
								parBBDD.setTotalProduccion(parSit.getTotalProduccion());
								parBBDD.setTasaIncendio(parSit.getTasaIncendio());
								parBBDD.setPrimaNetaIncendio(parSit.getPrimaNetaIncendio());
								parBBDD.setTasaPedrisco(parSit.getTasaPedrisco());
								parBBDD.setPrimaNetaPedrisco(parSit.getPrimaNetaPedrisco());
								logger.debug(" # actualizando parcela con comarca: "+parBBDD.getComarca().getId().getCodcomarca() +" provincia: "+ parBBDD.getComarca().getId().getCodprovincia() +" cultivo: "+parBBDD.getCultivo().getId().getCodcultivo());
								simulacionSbpDao.saveOrUpdate(parBBDD);
								logger.debug(" # actualizada  parcela OK");
								parcelasActualizadas++;
							}							
						}
					}
					logger.debug(" # parcelasActualizadas:  "+parcelasActualizadas);

					saveOrUpdate(polizaSbp);
				} catch (Exception e) {
					logger.error("Error al guardar la poliza recalculada",e);
					throw e;
				}
				
				simulacionSbpDao.saveOrUpdateFacturacion(polizaSbp, usuario);
				
				try {
					//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
					historicoEstadosManager.insertaEstado(Tabla.SBP,polizaSbp.getId(),usuario.getCodusuario(),ConstantsSbp.ESTADO_GRAB_PROV);
					logger.debug(" # recalculaSbp - inserccion correcta en historico #");
				}
				catch (Exception ex){
					logger.error("Se ha producido un error al grabar en el historico", ex);
				}
			}
			
				params.put("polizaSbp", polizaSbp);
			
		} catch (DAOException e) {
			throw new BusinessException("Error al recuperar las parcelas de sbp ", e);
		} catch (Exception e) {
			logger.error("Error generico al recalcular el sobreprecio",e);
			throw e;
		}
		return params;
	}
	
	public void deleteParcelasSbp(PolizaSbp polizaSbp){
		try{
			for (ParcelaSbp parsbp: polizaSbp.getParcelaSbps()){
				simulacionSbpDao.deleteParcela(parsbp);
				
			}
			polizaSbp.setParcelaSbps(null);
			this.saveOrUpdate(polizaSbp);	
		} catch (Exception e) {
			logger.error("Error aen el borrado de las parcelas en la poliza de sobreprecio.",e);
		}
	}
	
	public boolean validaPolizaParaSuplemento(final Long idPoliza, final Date fechaVigorPoliza, final Usuario usuario,
			final String realPath, final AnexoModificacion anexo, final boolean actualizaAnexo, final Session session, final boolean esBatch)
			throws Exception {
		
		Long idSuplemento = null;
		
		try {
			logger.debug("validaPolizaParaSuplemento INIT");
			
			Poliza poliza = seleccionPolizaManager.getPolizaById(Long.valueOf(idPoliza));
			
			PolizaSbp polSbp = new PolizaSbp();			
			polSbp.getPolizaPpal().setIdpoliza(poliza.getIdpoliza());
			polSbp.getPolizaPpal().getEstadoPoliza().setIdestado(poliza.getEstadoPoliza().getIdestado());
			PolizaSbp polizaSbp = null;
			polizaSbp = this.existePolizaSbp(polSbp);
			
			// validamos que la poliza ppal este en enviada correcta
			if (polizaSbp != null && polizaSbp.getEstadoPlzSbp().getIdestado().compareTo(ConstantsSbp.ESTADO_ENVIADA_CORRECTA)==0) {
				
				idSuplemento = this.calculaSuplemento(poliza, fechaVigorPoliza, usuario, realPath, session, esBatch, null);
				
				if (actualizaAnexo) {
					if (null!=idSuplemento) {
						anexo.setRevisarSbp(Constants.CHARACTER_N);
						session.saveOrUpdate(anexo);
					}
				}
			}
			logger.debug("validaPolizaParaSuplemento FIN");
			return null!=idSuplemento;
		} catch (Exception e) {
			logger.error("Error al calcular el suplemento", e);
			throw e;
		}		
	}
	
	/**
	 * 
	 * @param poliza
	 * @param realPath
	 * @return
	 * @throws Exception 
	 */
	public List<ParcelaSbp> obtenerParcelasSituacionActualizadaParaSuplemento(Poliza poliza, String realPath) throws Exception {
		
		logger.debug("SimulacionSbpManager - obtenerParcelasSituacionActualizadaParaSuplemento - init");
		
		SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
		
		logger.debug("Recuperamos parcelas de la situacion actualizada");
		try {
			respuesta = helper.getPolizaActualizada(poliza.getReferencia(), poliza.getLinea().getCodplan(), realPath);
			
			//Me devuelve las parcelas de la poliza actualizada ppla y cpl que tienen sbp
			List<ParcelaSbp> parcelasSitAct = this.getParcelasSbpBySituacionActualizada(respuesta,
			poliza.getLinea().getCodplan(), poliza.getReferencia(), realPath,
			poliza.getLinea().getLineaseguroid(), false, true);
			
			logger.debug("Se han recuperado " + parcelasSitAct.size() + " parcelas de la situacion actualizada.");
			
			if (null!=parcelasSitAct && parcelasSitAct.size()!=0) {
				return parcelasSitAct;
			}

		}catch (DAOException e) {
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,e);
			throw e;
		}catch (Exception ex){
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,ex);
			throw ex;
		}catch (Throwable a ) {
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,a);
		}
		
		
		return null;
		
	}
	
	public Long calculaSuplemento(final Poliza poliza, final Date fechaVigorPoliza, final Usuario usuario, final String realPath,
			final Session session, boolean esBatch, HttpServletRequest request) throws Exception {
		
		logger.debug("calculaSuplemento INIT");
		PolizaSbp polizaSbp = new PolizaSbp();
		List <ParcelaSbp> parcelasSuplemento = new ArrayList<ParcelaSbp>();
		SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
		
		try{
			polizaSbp.getPolizaPpal().setIdpoliza(poliza.getIdpoliza());
			polizaSbp.setReferencia(poliza.getReferencia());
			polizaSbp.getPolizaPpal().getLinea().setLineaseguroid(poliza.getLinea().getLineaseguroid());
		
			// Me devuelve las parcelas de sbp de la poliza
			logger.debug("Recuperamos parcelas de sobreprecio");
			List <ParcelaSbp> parcelasSbp = simulacionSbpDao.getParcelasSbpbyIdPolizaPpal(polizaSbp.getPolizaPpal().getIdpoliza(), false);
			
			logger.debug("Recuperamos parcelas de la situacion actualizada");
			respuesta = helper.getPolizaActualizada(poliza.getReferencia(), poliza.getLinea().getCodplan(), realPath);
			//Me devuelve las parcelas de la poliza actualizada ppla y cpl que tienen sbp
			List<ParcelaSbp> parcelasSitAct = this.getParcelasSbpBySituacionActualizada(respuesta,
					poliza.getLinea().getCodplan(), poliza.getReferencia(), realPath,
					poliza.getLinea().getLineaseguroid(), false, true);
		    
		    //List<ParcelaSbp> parcelasSbpCpl = new ArrayList<ParcelaSbp>(parcelasSbpCplAux);
			boolean hayCambiosParcela= false;
			boolean existeEnSbp= false;
			logger.debug("Comparamos parcelas");
			logger.debug("# size parcelasSbp: "+parcelasSbp.size());
			logger.debug("# size parcelasSitAct: "+parcelasSitAct.size());
			BigDecimal[] codCultivos = new BigDecimal[] {};
			for (int j=0; j<parcelasSitAct.size();j++){
				ParcelaSbp parAct = (ParcelaSbp)  parcelasSitAct.get(j);
				existeEnSbp = false;
				for (int i=0;i<parcelasSbp.size();i++){
					ParcelaSbp parSbp = (ParcelaSbp) parcelasSbp.get(i);
					logger.debug("# parAct prov: "+parAct.getComarca().getProvincia().getCodprovincia() +" parSbp prov: "+parSbp.getComarca().getProvincia().getCodprovincia());
					logger.debug("# parAct cult: "+parAct.getCultivo().getId().getCodcultivo() +" parSbp cult: "+parSbp.getCultivo().getId().getCodcultivo());
					logger.debug("# parAct com: "+parAct.getComarca().getId().getCodcomarca()+" parSbp com: "+parSbp.getComarca().getId().getCodcomarca());
					if (parAct.getComarca().getProvincia().getCodprovincia().equals(parSbp.getComarca().getProvincia().getCodprovincia())&&
							parAct.getCultivo().getId().getCodcultivo().equals(parSbp.getCultivo().getId().getCodcultivo())
							&& parAct.getComarca().getId().getCodcomarca().equals(parSbp.getComarca().getId().getCodcomarca())){
						logger.debug("# parAct totProd: "+parAct.getTotalProduccion());
						logger.debug("# parSbp totProd: "+parSbp.getTotalProduccion());
						// si son iguales hay que comparar las producciones para ver si han cambiado
						if (esBatch && parAct.getTotalProduccion().equals(parSbp.getTotalProduccion())){
							logger.debug("Parcelas Iguales");
							/* guardamos la parcela porque puede ser que haya parcelas nuevas y si las hay, hay 
							 enviar todas las parcelas
							 como son iguales no habria que generar de nuevo la parcela, se coje la que habia en sobreprecio*/
							
							//creamos una nueva parcela a partir de la de sobreprecio
							try{													
								ParcelaSbp parSbpClon = new ParcelaSbp();
								parSbpClon.setComarca(parSbp.getComarca());
								parSbpClon.setCultivo(parSbp.getCultivo());	
								parSbpClon.setPrimaNetaIncendio(parSbp.getPrimaNetaIncendio());
								parSbpClon.setPrimaNetaPedrisco(parSbp.getPrimaNetaPedrisco());
								parSbpClon.setSobreprecio(parSbp.getSobreprecio());
								parSbpClon.setTasaIncendio(parSbp.getTasaIncendio());
								parSbpClon.setTasaPedrisco(parSbp.getTasaPedrisco());
								parSbpClon.setTotalProduccion(parSbp.getTotalProduccion());								
								if (!ArrayUtils.contains(codCultivos, parSbpClon.getCultivo().getId().getCodcultivo())) {
									codCultivos = (BigDecimal[]) ArrayUtils.add(codCultivos,
											parSbpClon.getCultivo().getId().getCodcultivo());
								}
								parcelasSuplemento.add(parSbpClon);
								
							}catch (Exception ex){
								logger.error("Error al generar la nueva parcela a partir de la de sbp original",ex);
								throw ex;
							}
						/* Si es un suplemento manual o ha cambiado la produccion generamos una nueva parcela de sbp*/ 	
						}else {
							logger.debug(esBatch ? "Parcelas Distintas: ha cambiado la produccion" : "No se compara produccion dado que es suplemento manual");

							parAct = generaParcelaSbpParaSuplemento(parAct);
							if (!ArrayUtils.contains(codCultivos, parAct.getCultivo().getId().getCodcultivo())) {
								codCultivos = (BigDecimal[]) ArrayUtils.add(codCultivos,
										parAct.getCultivo().getId().getCodcultivo());
							}
							parcelasSuplemento.add(parAct);
							hayCambiosParcela= true;
						}						
						existeEnSbp = true;
						logger.debug("# existeEnSbp: "+existeEnSbp);
					}
				}
				/* si la parcela no de la situacion actualizada no coincide con ninguna de las de sobreprecio
				 * quiere decir que es nueva y  generamos una nueva parcela de sbp para enviarla en el sbp*/
				if (!existeEnSbp) { // 
					logger.debug("Parcelas Nueva");
					parAct = generaParcelaSbpParaSuplemento(parAct);
					if (!ArrayUtils.contains(codCultivos, parAct.getCultivo().getId().getCodcultivo())) {
						codCultivos = (BigDecimal[]) ArrayUtils.add(codCultivos,
								parAct.getCultivo().getId().getCodcultivo());
					}
					parcelasSuplemento.add(parAct);
					hayCambiosParcela= true;
				}
			}
			/* si hay nuevas parcelas bien porque haya cambiado la produccion o pq venga nueva
			   de la sit act generamos el suplemento, si no paramos */
			logger.debug("hayCambiosParcelas " + hayCambiosParcela);
			if (hayCambiosParcela) {
				
				logger.debug("Hay nuevas parcelas porque ha cambiado la produccion o porque vienen en la situacion actualizada");
				
				List <ParcelaSbp> parcelasSbpGuardar = null;
				
				if (esBatch) {
					polizaSbp.setParcelaSbps(new HashSet<ParcelaSbp> (parcelasSuplemento));
				} else {
					// Si viene del online puede que el usuario haya seleccionado un sobreprecio diferente y haya que sobreescribirlo en la lista de parcelas
					parcelasSbpGuardar =  simulacionSbpDao.rellenaPrimas(parcelasSuplemento, request, poliza.getLinea().getLineaseguroid());
					polizaSbp.setParcelaSbps(new HashSet<ParcelaSbp> (parcelasSbpGuardar));
				}
				polizaSbp.setTipoEnvio(new TipoEnvio());
				polizaSbp.getTipoEnvio().setId(ConstantsSbp.TIPO_ENVIO_SUPLEMENTO);
				logger.debug ("Antes de comprobar si es complementaria");
				if (respuesta.getPolizaComplementaria() != null  || respuesta.getPolizaComplementariaUnif() != null) {
					logger.debug ("Entramos por Complementaria ");
					polizaSbp.setIncSbpComp('S');
					/* ESC-13939 ** MODIF TAM (20.05.2021) ** Inicio **/
					/* Para obtener el Id de la poliza complementaria, es necesario buscar aquellas 
					 * polizas que estÈn en estado "Enviada Correcta" */
					/*Long polCpl = polizaComplementariaDao.getIdPolizaByRef(poliza.getReferencia(),
							Constants.MODULO_POLIZA_COMPLEMENTARIO, poliza.getLinea().getLineaseguroid());*/
					logger.debug ("Buscamos referencia de pol. complementaria Enviada Correcta");
					Long polCpl = polizaComplementariaDao.getIdPolizaByRefSupSbp(poliza.getReferencia(),
							Constants.MODULO_POLIZA_COMPLEMENTARIO, poliza.getLinea().getLineaseguroid());
					/* ESC-13939 ** MODIF TAM (20.05.2021) ** Fin **/
					polizaSbp.setPolizaCpl(new Poliza());
				    polizaSbp.getPolizaCpl().setIdpoliza(polCpl);
				}else {
					logger.debug ("Entramos por PÛliza Principal");
					polizaSbp.setIncSbpComp('N');
					polizaSbp.setPolizaCpl(null);
				}
				
				polizaSbp.setReferencia(poliza.getReferencia());
				
				Date fechaAltaPoliza = fechaVigorPoliza;
				Date fechaAltaSup = new Date();
				Date fechaFinPoliza = this.sobrePrecioDao.getFechaFinGarantiasSbp(poliza.getLinea().getLineaseguroid(),
						codCultivos);
				
				/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Inicio */
				/* Pasamos al mÈtodo calculoImporte en el 2∫ parametro el idpoliza de la poliza principal
				 * para poder calcular la prima Neta total de la poliza principal
				 */
				BigDecimal importe;
				
				if (esBatch) {
					importe = this.calculoImporte(parcelasSuplemento, polizaSbp.getPolizaPpal().getIdpoliza(), poliza.getLinea().getCodplan(),
							poliza.getLinea().getLineaseguroid(), fechaAltaPoliza, fechaAltaSup, fechaFinPoliza);
					
				} else {
					importe = this.calculoImporte(parcelasSbpGuardar, polizaSbp.getPolizaPpal().getIdpoliza(), poliza.getLinea().getCodplan(),
							poliza.getLinea().getLineaseguroid(), fechaAltaPoliza, fechaAltaSup, fechaFinPoliza);
					
				}
				
				logger.debug("IMPORTE CALCULADO --> " + importe);
				
				polizaSbp.setImporte(importe.setScale(2, BigDecimal.ROUND_HALF_UP));
				
				if (esBatch) {
					//guardamos la poliza sbp en definitiva
					logger.debug("Guardamos el suplemento,parcelas,actualizamos gen_spl_cpl y actualizamos el historico estados");
					simulacionSbpDao.guardaSuplemento(polizaSbp, usuario, poliza.getIdpoliza(), Constants.CHARACTER_N,
							session, ConstantsSbp.ESTADO_GRAB_DEF);
				} else {
					
					//guardamos la poliza sbp en provisional
					polizaSbp.setFechaProvisional(new Date());
					
					logger.debug("Guardamos el suplemento,parcelas,actualizamos gen_spl_cpl y actualizamos el historico estados");
					simulacionSbpDao.guardaSuplemento(polizaSbp, usuario, poliza.getIdpoliza(), Constants.CHARACTER_N,
							session, ConstantsSbp.ESTADO_GRAB_PROV);
				}
				
				return polizaSbp.getId();
				
			}else {
				logger.debug("No hay cambios en las parcelas. No se genera suplemento");

				return null;
			}
			
			
		}catch (DAOException e) {
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,e);
			throw e;
		}catch (Exception ex){
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,ex);
			throw ex;
		}catch (Throwable a ) {
			logger.error(ERROR_GENERICO_AL_CALCULAR_EL_SUMPLEMENTO,a);
		}
		logger.debug("calculaSuplemento FIN");
		return null;
		
	}
	
	/**
	 * 
	 */
	public void recalcularImporteSuplemento(Poliza poliza, PolizaSbp polizaSbp, HttpServletRequest request) {
		
		logger.debug("SimulacionSbpManager - recalcularImporteSuplemento - init");
		
		logger.debug("ID SUPLEMENTO --> " + polizaSbp.getId());
		
		BigDecimal[] codCultivos = new BigDecimal[] {};
		
		Date fechaProvisionalPoliza = polizaSbp.getFechaProvisional();
		//Date fechaAltaPoliza = polizaSbp.getFechaEnvioSbp();
		Date fechaAltaSup = new Date();
		Date fechaFinPoliza;
		try {
			
			List <ParcelaSbp> parcelasSuplemento = new ArrayList<ParcelaSbp>(polizaSbp.getParcelaSbps());
			List <ParcelaSbp> parcelasSbpGuardar =  simulacionSbpDao.rellenaPrimas(parcelasSuplemento, request, poliza.getLinea().getLineaseguroid());
			polizaSbp.setParcelaSbps(new HashSet<ParcelaSbp> (parcelasSbpGuardar));
			
			for (ParcelaSbp parcelaSbp : polizaSbp.getParcelaSbps()) {
				if (!ArrayUtils.contains(codCultivos, parcelaSbp.getCultivo().getId().getCodcultivo())) {
					codCultivos = (BigDecimal[]) ArrayUtils.add(codCultivos,
							parcelaSbp.getCultivo().getId().getCodcultivo());
				}
				
				logger.debug("PRECIO PARCELA RECALCULAR -->" + parcelaSbp.getSobreprecio());
			}
			
			fechaFinPoliza = this.sobrePrecioDao.getFechaFinGarantiasSbp(polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(),
					codCultivos);
			
			List<ParcelaSbp> parcelas = new ArrayList<ParcelaSbp>();
			parcelas.addAll(polizaSbp.getParcelaSbps());
			
			BigDecimal importe = this.calculoImporte(parcelas, polizaSbp.getPolizaPpal().getIdpoliza(), polizaSbp.getPolizaPpal().getLinea().getCodplan(),
					polizaSbp.getPolizaPpal().getLinea().getLineaseguroid(), fechaProvisionalPoliza, fechaAltaSup, fechaFinPoliza);
			
			logger.debug("IMPORTE RECALCULADO --> " + importe);
			
			polizaSbp.setImporte(importe.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			sobrePrecioDao.saveOrUpdate(polizaSbp);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.debug("SimulacionSbpManager - recalcularImporteSuplemento - end");
	}

	private ParcelaSbp generaParcelaSbpParaSuplemento(ParcelaSbp parAct) {
			
			parAct.setPrimaNetaIncendio(((parAct.getTotalProduccion().multiply(parAct.getSobreprecio())).multiply(parAct.getTasaIncendio())).divide(
					new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP));
			parAct.setPrimaNetaPedrisco(((parAct.getTotalProduccion().multiply(parAct.getSobreprecio())).multiply(parAct.getTasaPedrisco())).divide(
					new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP));
			return 	parAct;
		}

	private BigDecimal calculoImporte(List <ParcelaSbp> parcelas, Long idPolizaPpal,
			BigDecimal codPlan,Long lineaseguroId) throws BusinessException,Exception{
		return calculoImporte(parcelas, idPolizaPpal, codPlan, lineaseguroId, null, null, null);
	}
	
	private BigDecimal calculoImporte(List<ParcelaSbp> parcelas, Long idPolizaPpal, BigDecimal codPlan, Long lineaseguroId,
			Date fechaAltaPoliza, Date fechaAltaSup, Date fechaFinPoliza) throws BusinessException, Exception {
		
		BigDecimal primaTotalSeguro;
		BigDecimal totSumaAsegurada = BigDecimal.ZERO;
		BigDecimal primaNeta= BigDecimal.ZERO;
		
		 
		/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Inicio */ 
		// Me devuelve las parcelas de sbp de la poliza
		BigDecimal primaNetaPpal= BigDecimal.ZERO;
		BigDecimal primaNetaAux = BigDecimal.ZERO;
		
		logger.debug("Recuperamos parcelas de sobreprecio de la Pol Principal:"+idPolizaPpal);
		List <ParcelaSbp> parcelasSbpPpal = simulacionSbpDao.getParcelasSbpbyIdPolizaPpal(idPolizaPpal, false);
		
		/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Fin */	
		boolean ppalConPrimaMin = false;
		boolean splConPrimaMin = false;
		
		try {
			
			for (int i= 0;i<parcelas.size();i++) {
				ParcelaSbp par = parcelas.get(i);
				totSumaAsegurada = totSumaAsegurada.add(par.getSobreprecio().multiply(par.getTotalProduccion()))
						.setScale(2, BigDecimal.ROUND_HALF_UP);
				primaNeta = primaNeta.add(par.getPrimaNetaIncendio().add(par.getPrimaNetaPedrisco())).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				
				if (par.getComarca().getProvincia() != null) {
					par.getComarca().getId().setCodprovincia(par.getComarca().getProvincia().getCodprovincia());
				}
				logger.debug("parcela: "+ i + " idParcela --> " + par.getId() + " primaNeta: " + primaNeta);
				logger.debug("parcela: "+ i +" totSumaAsegurada: " + totSumaAsegurada );
			}
			
			logger.debug("Total totSumaAsegurada: " + totSumaAsegurada);
			logger.debug("Total primaNeta (pre-prorrateo): " + primaNeta);
			
			/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Inicio */ 
			for (int i= 0;i<parcelasSbpPpal.size();i++) {
				ParcelaSbp parPpal = parcelasSbpPpal.get(i);
				
				primaNetaPpal = primaNetaPpal.add(parPpal.getPrimaNetaIncendio().add(parPpal.getPrimaNetaPedrisco())).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				
				logger.debug("parPpal: " + parPpal);
				logger.debug("parPpal.getComarca(): " + parPpal.getComarca());
				logger.debug("parPpal.getComarca().getProvincia(): " + parPpal.getComarca().getProvincia());
				
				if (parPpal.getComarca().getProvincia() != null) {
					parPpal.getComarca().getId().setCodprovincia(parPpal.getComarca().getProvincia().getCodprovincia());
				}
				logger.debug("parcelaPpal: "+ i +" primaNetaPpal: " + primaNetaPpal);
			}
			/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Fin */ 
			
			// Obtenemos la prima minima
			PrimaMinimaSbp primaMinSbp = simulacionSbpDao.getPrimaMinima(lineaseguroId);
			
			// Si la prima total es menor que la prima minima, mostramos la prima minima
			if (primaNetaPpal.compareTo(primaMinSbp.getPrimaMinima()) == -1) {

				primaNetaPpal = primaMinSbp.getPrimaMinima();
				ppalConPrimaMin = true;
			}
			splConPrimaMin = (primaNeta.compareTo(primaMinSbp.getPrimaMinima()) == -1);
			logger.debug("Total primaNetaPpal (tras verificar prima mÌnima): " + primaNetaPpal);
			logger.debug("ppalConPrimaMin: " + ppalConPrimaMin);
			logger.debug("splConPrimaMin: " + splConPrimaMin);
			
			
			logger.debug("FECHA ALTA POLIZA --> " + fechaAltaPoliza);
			logger.debug("FECHA ALTA SUPLEMENTO --> " + fechaAltaSup);
			logger.debug("FECHA FIN POLIZA --> " + fechaFinPoliza);

			
			if (fechaAltaPoliza != null && fechaAltaSup != null && fechaFinPoliza != null) {
				
				logger.debug("Fecha Alta Poliza: " + fechaAltaPoliza);
				logger.debug("Fecha Alta Suplemento: " + fechaAltaSup);
				logger.debug("Fecha Fin Poliza: " + fechaFinPoliza);
				
				/** MODIF PET. 61429 (24.07.2019) **/
				SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
				
		        String fechaAltaPoliza_aux = dt1.format(fechaAltaPoliza);
		        logger.debug(" Valor de fechaAltaPoliza(string):"+fechaAltaPoliza_aux);
		        Date fechaAltaPol = dt1.parse(fechaAltaPoliza_aux);
		        
		        
		        String fechaAltaSup_aux = dt1.format(fechaAltaSup);
		        logger.debug(" Valor de fechaAltaSup(string):"+fechaAltaSup_aux);
		        Date fechaAltaSupPol = dt1.parse(fechaAltaSup_aux);
		        
		        String fechaFinPol_aux = dt1.format(fechaFinPoliza);
		        logger.debug(" Valor de fechaFinPol_aux(string):"+fechaFinPol_aux);
		        Date fechaFinPol= dt1.parse(fechaFinPol_aux);
		        
		        
		        logger.debug(" Valor de fechaAltaSup(Date):"+fechaAltaSupPol);		        
		        logger.debug(" Valor de fechaAltaPoliza(Date):"+fechaAltaPol);
		        logger.debug(" Valor de fechafinPoliza(Date):"+fechaFinPol);
		        /** MODIF PET. 61429 (24.07.2019) FIN **/
				
				if (fechaAltaSupPol.compareTo(fechaFinPol) > 0) {
					throw new BusinessException("Fecha de alta de suplemento superior a fecha fin de garantias.");
				}
				if (fechaAltaPol.compareTo(fechaFinPol) > 0) {
					throw new BusinessException("Fecha de vigor de poliza superior a fecha fin de garantias.");
				}
				
				
				int diasVigorPol = daysBetween(fechaAltaPol, fechaFinPol);
				logger.debug("Valor de diasVigorPol calculados: "+diasVigorPol);
				int diasVigorSup = daysBetween(fechaAltaSupPol, fechaFinPol);
				logger.debug("Valor de diasVigorSup calculados: "+diasVigorSup);
				
				/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Inicio */
				/* Recalculamos el valor de la prima Neta para restarle el valor de la prima Neta total de la principal */
				logger.debug("** Nuevo Calculo de prima y Prorrateo (29.07.2019) **");
				logger.debug("** Valor prima Neta: "+primaNeta);
				logger.debug("** Valor de primaNetaPpal: "+primaNetaPpal);
				
				primaNetaAux = (splConPrimaMin ? primaMinSbp.getPrimaMinima() : primaNeta).subtract(primaNetaPpal);//realiza la resta
				logger.debug("** Despues de hacer la resta, valor de primaNetaAux: "+primaNetaAux);
				
				BigDecimal primaPorDiaAux = primaNetaAux.divide(new BigDecimal(diasVigorPol), 10, BigDecimal.ROUND_HALF_UP);
				logger.debug("** Valor de primaPorDiaAux: "+primaPorDiaAux);

				primaNetaAux = primaPorDiaAux.multiply(new BigDecimal(diasVigorSup)).setScale(2, BigDecimal.ROUND_HALF_UP);
				logger.debug("** Valor de primaNetaAux(2): "+primaNetaAux);
				
				if (ppalConPrimaMin && splConPrimaMin) {
					primaNeta = BigDecimal.ZERO;
				} else {
					primaNeta = primaNetaAux;
				}
				/* Pet. 61429 ** MODIF TAM (29.07.2019) ** Inicio */				
			} else {
			
				// Si la prima total es menor que la prima minima, mostramos la prima minima
				if (primaNeta.compareTo(primaMinSbp.getPrimaMinima()) == -1) {
	
					primaNeta = primaMinSbp.getPrimaMinima();
				}
				logger.debug("Total primaNeta (tras verificar prima mÌnima): " + primaNeta);
			}
			
			//calculo de impuestos
			 primaTotalSeguro = this.calculoImpuestos (totSumaAsegurada, primaNeta, codPlan);
			 logger.debug("primaTotalSeguro: " + primaTotalSeguro );
			
		}catch (DAOException e) {
			throw new BusinessException("Error al calcular el importe sbp ", e);
		}catch (Exception ex){
			logger.error("Error generico al calcular el importe sbp", ex);
			throw ex;
		}
		return primaTotalSeguro;
	}
	
	private int daysBetween(final Date d1, final Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
	
		
	@Override
	public void anulaPoliza(String referencia) throws BusinessException {
		try{	
			simulacionSbpDao.cambiaEstado (referencia);
		}catch (DAOException e) {
			throw new BusinessException("Error al cambiar el estado a anulada ", e);
		}
		
	}
	
	@Override
	public List<Sobreprecio> generaSobreprecios(List<Sobreprecio> listSobreprecios,
			List<ParcelaSbp> parcelaSbpsMostrar, Map<String, Object> parameters) throws BusinessException {
		
		logger.debug("SimulacionSbpManager - generaSobreprecios - init");
		
		List<BigDecimal> lstCultivos= new ArrayList<BigDecimal>();
		List<Sobreprecio> sbpList = new ArrayList<Sobreprecio>();
		List<BigDecimal> lstCodComarca= new ArrayList<BigDecimal>();
		
		for (int i=0;i<listSobreprecios.size();i++){
			Sobreprecio s = listSobreprecios.get(i);
			
			for (int j=0;j<parcelaSbpsMostrar.size();j++){
				ParcelaSbp p = parcelaSbpsMostrar.get(j);

				if (!lstCultivos.contains(p.getCultivo().getId().getCodcultivo())){
					if (s.getCultivo().getId().getCodcultivo().equals(p.getCultivo().getId().getCodcultivo()) &&
							(p.getComarca().getProvincia().getCodprovincia().equals(s.getProvincia().getCodprovincia()))){						
							s.setSbpAsegurado(p.getSobreprecio());
							s.setProvincia(p.getComarca().getProvincia());
							sbpList.add(s);
							lstCultivos.add(p.getCultivo().getId().getCodcultivo());
							lstCodComarca.add(p.getComarca().getId().getCodcomarca());
							break;		
							
					}else if (s.getCultivo().getId().getCodcultivo().equals(p.getCultivo().getId().getCodcultivo())&&
							(s.getProvincia().getCodprovincia().equals(new BigDecimal(99)))){				
							s.setSbpAsegurado(p.getSobreprecio());
							s.setProvincia(p.getComarca().getProvincia());
							sbpList.add(s);
							lstCultivos.add(p.getCultivo().getId().getCodcultivo());
							lstCodComarca.add(p.getComarca().getId().getCodcomarca());
							break;	
							
					}else if (s.getCultivo().getId().getCodcultivo().equals(new BigDecimal(999))&&
							(s.getProvincia().getCodprovincia().equals(new BigDecimal(99)))){
						s.setSbpAsegurado(p.getSobreprecio());
						s.setProvincia(p.getComarca().getProvincia());
						sbpList.add(s);
						lstCultivos.add(p.getCultivo().getId().getCodcultivo());
						lstCodComarca.add(p.getComarca().getId().getCodcomarca());
						break;
					}
				}
			}
		}
		
		StringBuilder lstCodComarcaStrBuilder = new StringBuilder();

		for (int i = 0; i < lstCodComarca.size(); i++) {
		    lstCodComarcaStrBuilder.append(lstCodComarca.get(i).toString());
		    if (i < lstCodComarca.size() - 1) {
		        lstCodComarcaStrBuilder.append(",");
		    }
		}

		String lstCodComarcaStr = lstCodComarcaStrBuilder.toString();
		parameters.put("lstCodComarcaStr", lstCodComarcaStr);
		
		logger.debug("SimulacionSbpManager - generaSobreprecios - end");

		return sbpList;
	}
	
	@Override
	public List<ParcelaSbp> getParcelasSimulacion(PolizaSbp polizaSbp) throws BusinessException {
		try{
			return simulacionSbpDao.getParcelasSimulacion(polizaSbp);
		}catch (DAOException e) {
			throw new BusinessException("Error al obtener las parcelas de bbdd ", e);
		}
	}
	
	@Override
	public void actualizaSobreprecio(BigDecimal sobreprecio, BigDecimal codCultivo, BigDecimal codProvincia, BigDecimal codComarca) throws BusinessException {
		try{
			simulacionSbpDao.actualizarSobreprecio(sobreprecio, codCultivo, codProvincia, codComarca);
		}catch (DAOException e) {
			throw new BusinessException("Error al actualizar el sobreprecio en bbdd ", e);
		}
	}
	
	/**
	 * Devuelve el max plan de sbp
	 * @author U029769 26/06/2013
	 * @return BigDecimal
	 * @throws BusinessException
	 */
	public BigDecimal getPlanSbp()throws BusinessException {
		
		try{
			return simulacionSbpDao.getPlanSbp();
			
		}catch (DAOException e) {
			throw new BusinessException("Error al obtener lel max plan de sbp ", e);
		}
		
	}
	
	private List<ParcelaSbp> getParcelasSbpBySituacionActualizada(PolizaActualizadaResponse respuesta,
			BigDecimal codPlan, String referencia, String realPath, Long lineaseguroId, boolean comprobarFecha,
			boolean filtroComarca) throws SWConsultaContratacionException, AgrException, Exception {
		BigDecimal cultivo = null;
		BigDecimal provincia = null;
		BigDecimal comarca = null;
		BigDecimal produccion = null;
		HashMap<String, ParcelaSbp> auxMapPpl = new HashMap<String, ParcelaSbp>();
		List<ParcelaSbp> parcelasSitAct = new ArrayList<ParcelaSbp>();
		ParcelaSbp parSbp = null;
		List<BigDecimal> lstTiposCapital = new ArrayList<BigDecimal>();
		try {
			if (respuesta.getPolizaPrincipal() != null) {
				logger.debug("Entramos por poliza principal");
				es.agroseguro.seguroAgrario.contratacion.Parcela[] parcelaArr = respuesta.getPolizaPrincipal()
						.getPoliza().getObjetosAsegurados().getParcelaArray();
				logger.debug("Numero de parcelas: " + parcelaArr.length);
				for (es.agroseguro.seguroAgrario.contratacion.Parcela par : parcelaArr) {
					lstTiposCapital.clear();
					// 1.comprobamos si el cultivo-provincia-linea es de sbp
					cultivo = new BigDecimal(par.getCosecha().getCultivo());
					provincia = new BigDecimal(par.getUbicacion().getProvincia());
					comarca = new BigDecimal(par.getUbicacion().getComarca());
					logger.debug("cultivo: " + cultivo.intValue());
					logger.debug("provincia: " + provincia.intValue());
					logger.debug("comarca: " + comarca.intValue());
					// Recogemos la lista de capitales asegurados de la parcela
					for (es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado cap : par.getCosecha()
							.getCapitalesAsegurados().getCapitalAseguradoArray()) {
						if (!lstTiposCapital.contains(new BigDecimal(cap.getTipo())))
							lstTiposCapital.add(new BigDecimal(cap.getTipo()));
					}
					if (lstTiposCapital.isEmpty() || lstTiposCapital.size() < 1) {
						logger.debug("capital asegurado sin tipo.");
					}
					BigDecimal codTipCap = simulacionSbpDao.esParcelaConSbp(cultivo, provincia, lineaseguroId,
							comprobarFecha, lstTiposCapital);
					if (codTipCap != null) {
						// guardamos en un map "provincia-cultivo-comarca" || ParcelaSbp
						// si el mapa no contiene la clave la anhadimos
						String clave = "";
						if (filtroComarca)
							clave = provincia + "-" + cultivo + "-" + comarca;
						else
							clave = provincia + "-" + cultivo;
						if (!auxMapPpl.containsKey(clave)) {
							parSbp = new ParcelaSbp();
							this.setDatosParcela(cultivo, lineaseguroId, provincia, comarca, parSbp);
							parSbp.setTotalProduccion(this.setProduccionPpal(
									par.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap));
							this.setImportes(cultivo, provincia, comarca, referencia, parSbp, lineaseguroId,
									filtroComarca, lstTiposCapital);

							auxMapPpl.put(clave, parSbp);
							// si ya contine la clave
						} else {
							ParcelaSbp paraux = auxMapPpl.get(clave);
							produccion = this.setProduccionPpal(
									par.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap);
							paraux.setTotalProduccion(paraux.getTotalProduccion().add(produccion));
							auxMapPpl.remove(clave);
							auxMapPpl.put(clave, paraux);
						}
					}
				}
			}
			if (respuesta.getPolizaPrincipalUnif() != null) {
				logger.debug("Entramos por poliza principal unificada");
				Node currNode = respuesta.getPolizaPrincipalUnif().getPoliza().getObjetosAsegurados().getDomNode()
						.getFirstChild();
				while (currNode != null) {
					if (currNode.getNodeType() == Node.ELEMENT_NODE) {
						es.agroseguro.contratacion.parcela.ParcelaDocument par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory
								.parse(currNode);
						lstTiposCapital.clear();
						// 1.comprobamos si el cultivo-provincia-linea es de sbp
						cultivo = new BigDecimal(par.getParcela().getCosecha().getCultivo());
						provincia = new BigDecimal(par.getParcela().getUbicacion().getProvincia());
						comarca = new BigDecimal(par.getParcela().getUbicacion().getComarca());
						logger.debug("cultivo: " + cultivo.intValue());
						logger.debug("provincia: " + provincia.intValue());
						logger.debug("comarca: " + comarca.intValue());
						// Recogemos la lista de capitales asegurados de la parcela
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado cap : par.getParcela().getCosecha()
								.getCapitalesAsegurados().getCapitalAseguradoArray()) {
							if (!lstTiposCapital.contains(new BigDecimal(cap.getTipo())))
								lstTiposCapital.add(new BigDecimal(cap.getTipo()));
						}
						if (lstTiposCapital.isEmpty() || lstTiposCapital.size() < 1) {
							logger.debug("capital asegurado sin tipo.");
						}
						BigDecimal codTipCap = simulacionSbpDao.esParcelaConSbp(cultivo, provincia, lineaseguroId,
								comprobarFecha, lstTiposCapital);
						if (codTipCap != null) {
							// guardamos en un map "provincia-cultivo-comarca" || ParcelaSbp
							// si el mapa no contiene la clave la anhadimos
							String clave = "";
							if (filtroComarca)
								clave = provincia + "-" + cultivo + "-" + comarca;
							else
								clave = provincia + "-" + cultivo;
							if (!auxMapPpl.containsKey(clave)) {
								parSbp = new ParcelaSbp();
								this.setDatosParcela(cultivo, lineaseguroId, provincia, comarca, parSbp);
								parSbp.setTotalProduccion(this.setProduccionPpal(par.getParcela().getCosecha()
										.getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap));
								this.setImportes(cultivo, provincia, comarca, referencia, parSbp, lineaseguroId,
										filtroComarca, lstTiposCapital);

								auxMapPpl.put(clave, parSbp);
								// si ya contine la clave
							} else {
								ParcelaSbp paraux = auxMapPpl.get(clave);
								produccion = this.setProduccionPpal(par.getParcela().getCosecha()
										.getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap);
								paraux.setTotalProduccion(paraux.getTotalProduccion().add(produccion));
								auxMapPpl.remove(clave);
								auxMapPpl.put(clave, paraux);
							}
						}
					}
					currNode = currNode.getNextSibling();
				} /* Fin del While */
			}
			if (respuesta.getPolizaComplementaria() != null) {
				logger.debug("Entramos por poliza complementaria");
				es.agroseguro.seguroAgrario.contratacion.complementario.Parcela[] parcelaArr = respuesta
						.getPolizaComplementaria().getPoliza().getObjetosAsegurados().getParcelaArray();
				logger.debug("Numero de parcelas: " + parcelaArr.length);
				for (es.agroseguro.seguroAgrario.contratacion.complementario.Parcela parCpl : parcelaArr) {
					lstTiposCapital.clear();
					// 1.comprobamos si el cultivo-provincia-linea es de sbp
					cultivo = new BigDecimal(parCpl.getCosecha().getCultivo());
					provincia = new BigDecimal(parCpl.getUbicacion().getProvincia());
					comarca = new BigDecimal(parCpl.getUbicacion().getComarca());
					logger.debug("cultivo: " + cultivo.intValue());
					logger.debug("provincia: " + provincia.intValue());
					logger.debug("comarca: " + comarca.intValue());
					// Recogemos la lista de capitales asegurados de la parcela
					for (es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado cap : parCpl
							.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()) {

						if (!lstTiposCapital.contains(new BigDecimal(cap.getTipo())))
							lstTiposCapital.add(new BigDecimal(cap.getTipo()));
					}
					if (lstTiposCapital.isEmpty() || lstTiposCapital.size() < 1) {
						logger.debug("capital asegurado sin tipo.");
					}
					BigDecimal codTipCap = simulacionSbpDao.esParcelaConSbp(cultivo, provincia, lineaseguroId,
							comprobarFecha, lstTiposCapital);
					if (codTipCap != null) {
						// guardamos en un map "provincia-cultivo" || ParcelaSbp
						// si el mapa no contiene la clave la anhadimos
						String clave = "";
						if (filtroComarca)
							clave = provincia + "-" + cultivo + "-" + comarca;
						else
							clave = provincia + "-" + cultivo;
						if (!auxMapPpl.containsKey(clave)) {
							parSbp = new ParcelaSbp();
							this.setDatosParcela(cultivo, lineaseguroId, provincia, comarca, parSbp);
							parSbp.setTotalProduccion(this.setProduccionCpl(
									parCpl.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(),
									codTipCap));
							this.setImportes(cultivo, provincia, comarca, referencia, parSbp, lineaseguroId,
									filtroComarca, lstTiposCapital);
							auxMapPpl.put(clave, parSbp);
							// si ya contine la clave
						} else {
							ParcelaSbp paraux = auxMapPpl.get(clave);
							produccion = this.setProduccionCpl(
									parCpl.getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap);
							paraux.setTotalProduccion(paraux.getTotalProduccion().add(produccion)); // produccion +
																									// incremento
							auxMapPpl.remove(clave);
							auxMapPpl.put(clave, paraux);
						}
					}
				}
			}
			if (respuesta.getPolizaComplementariaUnif() != null) {
				logger.debug("Entramos por poliza complementaria unificada");
				Node currNode = respuesta.getPolizaComplementariaUnif().getPoliza().getObjetosAsegurados().getDomNode()
						.getFirstChild();
				while (currNode != null) {
					if (currNode.getNodeType() == Node.ELEMENT_NODE) {
						es.agroseguro.contratacion.parcela.ParcelaDocument parCpl = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory
								.parse(currNode);
						lstTiposCapital.clear();
						// 1.comprobamos si el cultivo-provincia-linea es de sbp
						cultivo = new BigDecimal(parCpl.getParcela().getCosecha().getCultivo());
						provincia = new BigDecimal(parCpl.getParcela().getUbicacion().getProvincia());
						comarca = new BigDecimal(parCpl.getParcela().getUbicacion().getComarca());
						logger.debug("cultivo: " + cultivo.intValue());
						logger.debug("provincia: " + provincia.intValue());
						logger.debug("comarca: " + comarca.intValue());
						// Recogemos la lista de capitales asegurados de la parcela
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado cap : parCpl.getParcela().getCosecha()
								.getCapitalesAsegurados().getCapitalAseguradoArray()) {
							if (!lstTiposCapital.contains(new BigDecimal(cap.getTipo())))
								lstTiposCapital.add(new BigDecimal(cap.getTipo()));
						}
						if (lstTiposCapital.isEmpty() || lstTiposCapital.size() < 1) {
							logger.debug("capital asegurado sin tipo.");
						}
						BigDecimal codTipCap = simulacionSbpDao.esParcelaConSbp(cultivo, provincia, lineaseguroId,
								comprobarFecha, lstTiposCapital);
						if (codTipCap != null) {
							// guardamos en un map "provincia-cultivo" || ParcelaSbp
							// si el mapa no contiene la clave la anhadimos
							String clave = "";
							if (filtroComarca)
								clave = provincia + "-" + cultivo + "-" + comarca;
							else
								clave = provincia + "-" + cultivo;
							if (!auxMapPpl.containsKey(clave)) {

								parSbp = new ParcelaSbp();
								this.setDatosParcela(cultivo, lineaseguroId, provincia, comarca, parSbp);
								parSbp.setTotalProduccion(this.setProduccionCpl(parCpl.getParcela().getCosecha()
										.getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap));
								this.setImportes(cultivo, provincia, comarca, referencia, parSbp, lineaseguroId,
										filtroComarca, lstTiposCapital);
								auxMapPpl.put(clave, parSbp);
								// si ya contine la clave
							} else {
								ParcelaSbp paraux = auxMapPpl.get(clave);
								produccion = this.setProduccionCpl(parCpl.getParcela().getCosecha()
										.getCapitalesAsegurados().getCapitalAseguradoArray(), codTipCap);
								paraux.setTotalProduccion(paraux.getTotalProduccion().add(produccion)); // produccion +
																										// incremento
								auxMapPpl.remove(clave);
								auxMapPpl.put(clave, paraux);
							}
						}
					}
					currNode = currNode.getNextSibling();
				} /* Fin del While */
			}
			logger.debug("Tenemos parcelas: " + !auxMapPpl.isEmpty());
			// guardamos todas las parcelas en una lista
			Iterator<Entry<String, ParcelaSbp>> it2 = auxMapPpl.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry<String, ParcelaSbp> e2 = it2.next();
				parcelasSitAct.add((ParcelaSbp) e2.getValue());
			}
		} catch (Exception e) {
			throw e;
		}
		return parcelasSitAct;
	}
	
	@SuppressWarnings("rawtypes")
	private List<ParcelaSbp> getParcelasSbpBySituacionActualizadaYCpl(PolizaSbp pSbp,
			String tipoPoliza, boolean incluirCplEnSbp, boolean filtroxComarca,List<ParcelaSbp> listaParcelas) throws SWConsultaContratacionException, AgrException, Exception {
		List<ParcelaSbp> parcelasSbpsCpl = new ArrayList<ParcelaSbp>();
		// recogemos las parcelas de la complementaria de bbdd y las juntamos con las de la situacion actualizada.
		List listCultivos = simulacionSbpDao.getCultivosSbp(pSbp.getPolizaPpal().getLinea().getLineaseguroid());
		//recuperamos todas las parcelas agrupando por provincia y comarca
		parcelasSbpsCpl =  simulacionSbpDao.getParcelasParaSbp(pSbp,tipoPoliza,incluirCplEnSbp,true,listCultivos);
		// sumamos la produccion de la ppal con el incremento de la cpl
		for (ParcelaSbp p : listaParcelas){
			for (ParcelaSbp pCpl : parcelasSbpsCpl){
				if (// Cultivo
					p.getCultivo().getId().getCodcultivo().compareTo(pCpl.getCultivo().getId().getCodcultivo()) == 0 &&
					// Provincia
					p.getComarca().getProvincia().getCodprovincia().compareTo(pCpl.getComarca().getProvincia().getCodprovincia()) == 0 &&
					// Comarca
					p.getComarca().getId().getCodcomarca().compareTo(pCpl.getComarca().getId().getCodcomarca()) == 0)		{
					
						// sumamos prod + incremento
						p.setTotalProduccion(p.getTotalProduccion().add(pCpl.getTotalProduccion()));
				}	
			}
		}
		
		return listaParcelas;
	}
	
	private void setImportes(BigDecimal cultivo, BigDecimal provincia,BigDecimal comarca,
			String referencia, ParcelaSbp parSbp,Long lineaseguroId,boolean filtroComarca, List<BigDecimal> lstTiposCapital) throws DAOException {
		try {
			List<Object> importes = simulacionSbpDao.getSobreprecioYtasasFromParcelas(cultivo,provincia,referencia,comarca,filtroComarca);
			if (importes!= null && importes.size()>0) {
				Object[] imp = (Object[]) importes.get(0);
				parSbp.setSobreprecio((BigDecimal) imp[0]);
				parSbp.setTasaIncendio((BigDecimal) imp[1]);
				parSbp.setTasaPedrisco((BigDecimal) imp[2]);
				
			}else { //La parcela no existe en parcelas SBP - nos vamos a buscar los datos a tb_sbp_sobreprecio
				Sobreprecio sbp = simulacionSbpDao.getMaxSobrepreciofromTabla(cultivo,provincia,lineaseguroId,lstTiposCapital);
				parSbp.setSobreprecio(sbp.getPrecioMaximo());
				TasasSbp tasa = simulacionSbpDao.getMaxTasasfromTabla(cultivo, provincia, lineaseguroId, comarca);
				if (tasa!=null){
					parSbp.setTasaIncendio(tasa.getTasaIncendio());
					parSbp.setTasaPedrisco(tasa.getTasaPedrisco());
				}else {
					logger.debug("No se han encontrado tasas");
				}
			}
			
		}catch(DAOException e ) {
			throw e;
		}
	}
	
	private BigDecimal setProduccionPpal(Object[] capitalAseguradoArray, BigDecimal codTipoCapital) {
		int tipoCapital = -1;
		int prod = 0;
		BigDecimal produccion = new BigDecimal(0);
		for (Object capAseg : capitalAseguradoArray) {
			if (capAseg instanceof es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado) {
				tipoCapital = ((es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado) capAseg).getTipo();
				prod = ((es.agroseguro.seguroAgrario.contratacion.CapitalAsegurado) capAseg).getProduccion();
			} else if (capAseg instanceof es.agroseguro.contratacion.parcela.CapitalAsegurado) {
				tipoCapital = ((es.agroseguro.contratacion.parcela.CapitalAsegurado) capAseg).getTipo();
				prod = ((es.agroseguro.contratacion.parcela.CapitalAsegurado) capAseg).getProduccion();
			}
			if (tipoCapital == codTipoCapital.intValue())
				produccion = produccion.add(new BigDecimal(prod));
		}
		return produccion;
	}
	
	private BigDecimal setProduccionCpl(Object[]capitalAseguradoArray, BigDecimal codTipoCapital) {		
		int tipoCapital = -1;
		int prod = 0;
		BigDecimal produccion = new BigDecimal(0);
		for (Object capAseg : capitalAseguradoArray){
			if (capAseg instanceof es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado) {
				tipoCapital = ((es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado) capAseg).getTipo();
				prod = ((es.agroseguro.seguroAgrario.contratacion.complementario.CapitalAsegurado) capAseg).getProduccion();
			} else if (capAseg instanceof es.agroseguro.contratacion.parcela.CapitalAsegurado) {
				tipoCapital = ((es.agroseguro.contratacion.parcela.CapitalAsegurado) capAseg).getTipo();
				prod = ((es.agroseguro.contratacion.parcela.CapitalAsegurado) capAseg).getProduccion();
			}
			if (tipoCapital == codTipoCapital.intValue())
				produccion = produccion.add(new BigDecimal(prod));
		}
		return produccion;
	}
	
	
	private void setDatosParcela(BigDecimal cultivo, Long lineaseguroId,
			BigDecimal provincia, BigDecimal comarca, ParcelaSbp parSbp) {


		parSbp.getCultivo().getId().setCodcultivo(cultivo);
		parSbp.getCultivo().getId().setLineaseguroid(lineaseguroId);
		parSbp.getCultivo().getLinea().setLineaseguroid(lineaseguroId);
		Provincia prov = new Provincia();
		prov = (Provincia) simulacionSbpDao.getObject(Provincia.class, provincia);
		parSbp.getComarca().setProvincia(prov);
		parSbp.getComarca().getId().setCodcomarca(comarca);
		
	}
	
	public ArrayList<Long> checkAnexosCuponParaSbp(Session sesion) throws BusinessException {
		ArrayList<AnexoModificacion> listaAnexosCupon = null;
		ArrayList<Long> listaAnexosActualizar = new ArrayList<Long>();
		BigDecimal idSbp = null;
		try {
			listaAnexosCupon = simulacionSbpDao.getAnexosCuponParaSbp(new Long[] {
					Constants.AM_CUPON_ESTADO_CONFIRMADO_TRAMITE, Constants.AM_CUPON_ESTADO_CONFIRMADO_APLICADO },
					Constants.CHARACTER_S);
			List<Long> comprobados = new ArrayList<Long>();
			for (AnexoModificacion am : listaAnexosCupon) {
				if (!comprobados.contains(am.getPoliza().getIdpoliza())) { // es la primera vez
					// verificamos si tiene sobreprecio enviado correcto y en periodo de contratacion
					idSbp = simulacionSbpDao.esPolizaConSbp(am.getPoliza().getIdpoliza(),
							am.getPoliza().getLinea().getCodlinea(), am.getPoliza().getLinea().getCodplan());
					comprobados.add(am.getPoliza().getIdpoliza());
					// Tiene sobreprecio
					if (idSbp != null) {
						logger.debug("Actualizo flag sobpreprecio: " + idSbp.longValue());
						simulacionSbpDao.actualizaFlagSbp(idSbp.longValue(), sesion);
					}
				}
				if (!listaAnexosActualizar.contains(am.getId())) {
					listaAnexosActualizar.add(am.getId());
					logger.debug(ANEXO + am.getId() + " estado cupon " + am.getCupon().getEstadoCupon().getId() + " - insertado en la lista");
				}
			}
			if (esUltimoDiaEnvSupl()) {
				// OBTIENE LOS ANEXOS EN CONFIRMADO-TRAMITE QUE NO SE HAN MARCADO PARA SBP
				listaAnexosCupon = simulacionSbpDao.getAnexosCuponParaSbp();
				for (AnexoModificacion am : listaAnexosCupon) {
					// verificamos si tiene sobreprecio enviado correcto y en periodo de contratacion
					idSbp = simulacionSbpDao.esPolizaConSbp(am.getPoliza().getIdpoliza(),
							am.getPoliza().getLinea().getCodlinea(), am.getPoliza().getLinea().getCodplan());
					// Tiene sobreprecio
					if (idSbp != null) {
						logger.debug("Actualizo flag sobpreprecio: " + idSbp.longValue());
						simulacionSbpDao.actualizaFlagSbp(idSbp.longValue(), sesion);
					}
				}
			}
			return listaAnexosActualizar;
		} catch (DAOException e) {
			logger.error("Error al obtener los anexos para sbp " + e);
			throw new BusinessException("Error al obtener los anexos para sbp ", e);
		}
	}

	/**
	 * Devuelve una lista de polizas sbp para generar los suplementos
	 */
	public List<PolizaSbp> getPolizasSbpParaSuplementos() throws BusinessException {
		try {
			return simulacionSbpDao.getPolizasSbpParaSuplementos();
		
		}catch(DAOException e) {
			logger.error("Error al las polizas de sobreprecio para calcular los suplementos " + e);
			throw new BusinessException("Error al las polizas de sobreprecio para calcular los suplementos ", e);
		}
	}
	/**
	 * Mira si es el ultimo dia de contratacion +1
	 */
	private boolean esUltimoDiaEnvSupl() throws BusinessException {		
		try {
			return simulacionSbpDao.esUltimoDiaEnvSupl();
		} catch (DAOException e) {
			logger.error("Error al comprobar si es el utimo dia de contratacion " + e);
			throw new BusinessException("Error al comprobar si es el utimo dia de contratacion  ", e);
		}
	}
	
	public void actualizaRevisar(ArrayList<Long> listaAnexosActualizar) throws BusinessException {
		
		try {
			simulacionSbpDao.updateFlagAnexo(listaAnexosActualizar);
		
		} catch (Exception e) {
			logger.error("Error al actualizar el campo revisar de los anexos " + e);
			throw new BusinessException("Error al actualizar el campo revisar de los anexos ", e);
		}
	}
	
	public void updateFlagbyIdPolSbp(List<Long> lstPolSbp) throws BusinessException {
		try {
			simulacionSbpDao.updateFlagbyIdsPolSbp(lstPolSbp,Constants.CHARACTER_N);
		} catch (Exception e) {
			logger.error("Error al actualizar el flag en polizaSbp ", e);
			throw new BusinessException("Error al actualizar el flag en polizaSbp ", e);
		}
	}
	
	/**
	 * P79222_3
	 */
	public boolean validarSuplemento(PolizaSbp pSbp) throws BusinessException {
	    
	    List<PolizaSbp> suplementos = new ArrayList<>();
	    
	    try {
			suplementos = this.simulacionSbpDao.existeSuplemento(pSbp.getId());
			
			for (PolizaSbp suplemento : suplementos) {
				if (suplemento.getEstadoPlzSbp().getIdestado().equals(ConstantsSbp.ESTADO_PENDIENTE_ACEPTACION)) {
					return false;
				}
			}
			
		} catch (DAOException daoe) {
			throw new BusinessException(ERROR_DURANTE_EL_ACCESO_A_LA_BASE_DE_DATOS, daoe);
		}
	    
	    return true;
	}
	
	public void setSimulacionSbpDao(ISimulacionSbpDao simulacionSbpDao) {
		this.simulacionSbpDao = simulacionSbpDao;
	}
	
	public void setSobrePrecioDao(ISobrePrecioDao sobrePrecioDao) {
		this.sobrePrecioDao = sobrePrecioDao;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}

	public void setPolizaComplementariaDao(
			IPolizaComplementariaDao polizaComplementariaDao) {
		this.polizaComplementariaDao = polizaComplementariaDao;
	}
	
	public void setConsultaSbpManager(IConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}	
	
	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
	
	public PolizaSbp getPolizaSbp(final Long idpoliza, final Character tipoRef) throws BusinessException {
		try {
			return simulacionSbpDao.getPolizaSbp(idpoliza, tipoRef);
		} catch (DAOException dao) {			
			throw new BusinessException("Se ha producido al obtener la poliza de sobreprecio:", dao);
		}
	}
}