package com.rsi.agp.core.managers.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCalendar;
import org.springframework.transaction.TransactionSystemException;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.IPolizasPctComisionesManager;
import com.rsi.agp.core.managers.ged.IDocumentacionGedManager;
import com.rsi.agp.core.managers.impl.HistoricoEstadosManager.Tabla;
import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ConstantsConceptos;
import com.rsi.agp.core.util.NumberUtils;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.config.IDiccionarioDatosDao;
import com.rsi.agp.dao.models.impl.DatabaseManager;
import com.rsi.agp.dao.models.poliza.IPolizaComplementariaDao;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.EstadoPagoAgp;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;

public class PolizaComplementariaManager implements IManager {
	
	private Log logger = LogFactory.getLog(PolizaComplementariaManager.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private IPolizaComplementariaDao polizaComplementariaDao;
	private DatabaseManager databaseManager;
	private IHistoricoEstadosManager historicoEstadosManager;
	private IDiccionarioDatosDao diccionarioDatosDao;
	private IPolizasPctComisionesManager polizasPctComisionesManager;
	private IDocumentacionGedManager documentacionGedManager;
	
	public List<Poliza> getPolizaByTipoRef(Poliza poliza, Character tipoRef) {
		
		List<Poliza> polizas = new ArrayList<Poliza>();
		try {
			polizas = polizaComplementariaDao.getPolizaByTipoRef(poliza, tipoRef);
		} catch (DAOException e) {
			logger.error("No se pudo obtener la poliza por referencia", e);
		}
		return polizas;
	}
	
	
	/**
	 * Metodo que comprueba si para la poliza actual se puede dar de alta una poliza complementaria.
	 * @param usuario
	 * @param lineaseguroid
	 * @param realPath
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 */
	public Map<String,Object> altaModuloCpl(Usuario usuario, String realPath,Poliza polizPpal) throws BusinessException, DAOException{
		logger.debug("init - tieneModuloCplPoliza");
		
		logger.debug("**@@** altaModuloCpl, valor de idPolPpal: "+polizPpal.getIdpoliza());
		
		Map<String,Object> parametrosPl  = new HashMap<String, Object>();
		Map<String,Object> params  = new HashMap<String, Object>();
		Poliza polizaCpl = new Poliza();
		boolean cargarParcelasBBDD = false;
		PolizaActualizadaResponse respuesta = new PolizaActualizadaResponse();
		String modPpl = "";
		try {
			Poliza polizas = polizaComplementariaDao.existePolizaCpl(polizPpal.getIdpoliza());
			
			if(polizas!= null){
				logger.debug("existe poliza complementaria contratada");
				polizaCpl.setIdpoliza(new Long(-3));
				params.put("polizaCpl", polizaCpl);
			}else{
				
				ModuloId idModulo = new ModuloId();
				// AMG 04/11/2014 modificaciones polizas complementarias
				if (polizPpal.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
					// llamada a sit. actualizada si la poliza esto en estado enviada correcta
				
					respuesta = new PolizaActualizadaResponse();
					try {
						SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
						respuesta = helper.getPolizaActualizada(polizPpal.getReferencia(), polizPpal.getLinea().getCodplan(), realPath);
					}catch (AgrException e){
						logger.error("El servicio ha devuelto una excepcion",e);
						//throw e;
						
					}catch (Exception ex) {
						logger.error("Error inesperado",ex);
						//throw ex;
					}	
				}
				
				// si la llamada al ws se ha realizado correctamente sacamos el modulo de la sit actualizada, si no es volida se saca de la pol. principal
				if (respuesta !=  null && respuesta.getPolizaPrincipalUnif() != null){ // por ws
					modPpl = respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getModulo().trim();					
				}else{
					modPpl = polizPpal.getCodmodulo();
				}
				idModulo.setCodmodulo(modPpl);
				idModulo.setLineaseguroid(polizPpal.getLinea().getLineaseguroid());
				
				Modulo moduloPpal =  polizaComplementariaDao.getModuloPPalPoliza(idModulo);
				logger.debug("modulo ppal: " + idModulo);
				
				/* ESC-14043 ** MODIF TAM (01.06.2021) ** Inicio */
				/* Pasamos el destinatario de la situación actualizada de la póliza recuperada */
				if (respuesta.getPolizaPrincipalUnif() != null) {
					for (PagoPoliza pp : polizPpal.getPagoPolizas()) {
						if (respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta()!= null) {
							if (respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getDestinatario()!= null) {
								String Destinatario = respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getDestinatario();
								pp.setDestinatarioDomiciliacion(Destinatario.charAt(0));
								logger.debug("**@@** Valor del destinatario:"+pp.getDestinatarioDomiciliacion());
							}
							
							if (respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getTitular()!= null) {
								String Titular = respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getTitular();
								pp.setTitularCuenta(Titular);
								logger.debug("**@@** Valor del titular:"+pp.getTitularCuenta());
							}
							
							if (respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getIban()!= null) {
								String IBAN = respuesta.getPolizaPrincipalUnif().getPoliza().getPago().getCuenta().getIban();
								pp.setIban(IBAN.substring(0,4));
								pp.setCccbanco(IBAN.substring(4));
								logger.debug("**@@** Valor cuenta IBAN:"+pp.getIban() + pp.getCccbanco());
							}
						}
					}
				}
				/* ESC-14043 ** MODIF TAM (01.06.2021) ** Fin */
				
				
				if(moduloPpal.getPpalcomplementario().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
					Modulo moduloCpl = polizaComplementariaDao.getModuloCplPoliza(polizPpal.getLinea().getLineaseguroid(),modPpl);
					
					if(moduloCpl != null){
						logger.debug("creamos nuestra poliza complementaria");
						polizaCpl  = crearPolizaCpl(polizPpal,moduloCpl,usuario);// crea la poliza no la guarda						
						
						PolizaPctComisiones ppc=null;
						// AMG 04/11/2014 plan >=2015 -> guardamos los gastos de la situacion actualizada o bien de los datos de la ppal
						if (polizPpal.isPlanMayorIgual2015()) {
							ppc = this.creaComisionesComplementaria(usuario, polizaCpl,polizPpal, params);//llenamos alertas en params si las comisiones son erroneas
							if (null!= ppc) {
								//Guardamos poliza complementaria de planes posteriores o iguales a 2015
								EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
								savePolizaCpl(polizaCpl, estado, usuario.getCodusuario());							
								logger.debug("Guardamos poliza complementaria >= 2015");
								//Guardamos comisiones de la poliza complementaria
								ppc.setPoliza(polizaCpl);
								polizaComplementariaDao.saveOrUpdate(ppc);
								logger.debug("Guardamos comisiones de poliza complementaria de plan >= 2015");
								polizaCpl.setPolizaPctComisiones(ppc);
								params.put("polizaCpl", polizaCpl);
							}else {
								// los parometros de alkerta se han creado en el motodo creaComisionesComplementaria
								logger.debug("Comisiones nulas o faltan datos obligatorios.");
								return params;
							}
						}else {
							//Guardamos poliza complementaria de plan anterior a 2015
							logger.debug("Guardamos poliza complementaria de plan anterior a2015");
							EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
							savePolizaCpl(polizaCpl, estado, usuario.getCodusuario());
							params.put("polizaCpl", polizaCpl);
						}
						
						
						
						if (polizPpal.getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_DEFINITIVA)) {
							
							logger.debug("cargamos las parcelas de la situacion actualizada");
							logger.debug("**@@** altaModuloCpl, valor de idPolPpal(2): "+polizPpal.getIdpoliza());
							try {
								/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
								/* Por los desarrollos de esta peticion tanto las polizas agricolas como las de ganado
								 * iron por el mismo end-point y con formato Unificado
								 */
								if (respuesta !=  null && respuesta.getPolizaPrincipalUnif() != null){
									//es.agroseguro.contratacion.Cobertura cob = (es.agroseguro.contratacion.Cobertura)respuesta.getPolizaPrincipal().getPoliza().getCobertura();
									es.agroseguro.contratacion.Cobertura cob = respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura();
									//Con los datos variables generamos los registros de coberturas
									this.populateCoberturas(polizaCpl, cob);
								}
								
								cargaParcelasFromWSMod (polizPpal.getReferencia(),polizPpal.getLinea().getCodplan(),
										realPath,polizPpal.getLinea().getLineaseguroid(),polizaCpl.getIdpoliza(),respuesta);
								
								/** Pet. 63497 (REQ.02) ** MODIF TAM (30/03/2020) ** Inicio */
								logger.debug("**@@** altaModuloCpl, Valor de Codmodulo: "+moduloCpl.getId().getCodmodulo());
								if (moduloCpl.getId().getCodmodulo().equals("CP")) {
									logger.debug("**@@** Cargamos Coberturas Parcelas Cpl");
									polizaComplementariaDao.cargarcoberturasParcelasCpl(polizPpal, polizaCpl);
									
									logger.debug("**@@** Cargamos CapAsegRelModulo Cpl");
									polizaComplementariaDao.cargarCapAsegRelModuloCpl(polizPpal, polizaCpl);
								}
								/** Pet. 63497 (REQ.02) ** MODIF TAM (30/03/2020) ** Inicio */
								
								params.put("mensaje", bundle.getObject("mensaje.cargaParcelas.ws"));
							} catch (Exception e) {
								logger.error("Error al recuperar las parcelas de la situacion actualizada",e);
								cargarParcelasBBDD = true;
							}
							
						}else {
							cargarParcelasBBDD = true;
						}		
						
						if (cargarParcelasBBDD) {
							logger.debug("modulo cpl: " + moduloCpl.getId().getCodmodulo());
							// PL que copia parcelas/capitales asegurados
							String procedure = "PQ_COPIA_PARCELAS.copiarParcelasEnPolCpl(P_IDPOLIZA1 IN NUMBER,P_IDPOLIZA2   IN NUMBER)";
							parametrosPl.put("P_IDPOLIZA1", polizPpal.getIdpoliza());
							parametrosPl.put("P_IDPOLIZA2", polizaCpl.getIdpoliza());
							logger.debug("PL que clona las parcelas: PQ_COPIA_PARCELAS.copiarParcelasEnPolCpl");
							logger.debug("parametros del PL. P_IDPOLIZA1" +  polizPpal.getIdpoliza());
							logger.debug("parametros del PL. P_IDPOLIZA2" +  polizaCpl.getIdpoliza());
							databaseManager.executeStoreProc(procedure, parametrosPl);
							logger.debug("parcelas clonadas correctamente");
							params.put("alerta", bundle.getObject("mensaje.cargaParcelas.bbdd"));
							
							// guardamos las coberturas
							List<ComparativaPoliza> comp = new ArrayList<ComparativaPoliza> (polizPpal.getComparativaPolizas());
							BigDecimal secuencia = null;
							List<String> lstModulos = new ArrayList<String>();
							for (ComparativaPoliza c : comp){	
								if (!lstModulos.contains(moduloCpl.getId().getCodmodulo())){
									ModuloPoliza moduloPolHbm;
									Set<ModuloPoliza> modulosPolHbm = new HashSet<ModuloPoliza>();
									moduloPolHbm = new ModuloPoliza();
									
									try {
										secuencia = polizaComplementariaDao.getSecuencia("SQ_MODULOS_POLIZA");
										lstModulos.add(moduloCpl.getId().getCodmodulo());
									} catch (DAOException e) {
										logger.error("Error al recoger la secuencia del modulo para la Cpl",e);								
									}
								
									moduloPolHbm.setId(new ModuloPolizaId(polizaCpl.getIdpoliza(),polizaCpl.getLinea().getLineaseguroid(), polizaCpl
													.getCodmodulo(),secuencia.longValue()));
									moduloPolHbm.setPoliza(polizaCpl);
									try{
										polizaComplementariaDao.saveOrUpdate(moduloPolHbm);
										modulosPolHbm.add(moduloPolHbm);
										polizaCpl.setModuloPolizas(modulosPolHbm);
									}catch (TransactionSystemException e) {
										logger.debug("Error controlado al guardar el modulo para la cpl");
									}
								}
								c.setPoliza(polizaCpl);
								c.getId().setIdpoliza(polizaCpl.getIdpoliza());
								c.getId().setCodmodulo(moduloCpl.getId().getCodmodulo());
								if (secuencia != null)
									c.getId().setIdComparativa(secuencia.longValue());
								polizaComplementariaDao.evict(c);
								try{
									polizaComplementariaDao.saveOrUpdate(c);
								}catch (TransactionSystemException e) {
									logger.debug("Error controlado al copiar las comparativas de la pol ppal a la cpl");
									//si da error, es que esa comparativa no se tiene que copiar
									// ya que para las cpl no apliza.Se continua con la ejecucion
								}
							}
						}

					}else{
						//NO EXISTE MODULO CPL
						logger.debug("NO EXISTE MODULO CPL");
						polizaCpl.setIdpoliza(new Long(-2));
						params.put("polizaCpl", polizaCpl);
					}
				}
			}
			logger.debug("end - tieneModuloCplPoliza");
			
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al crear la poliza complementaria: " + dao.getMessage());
			//Si falla borramos la poliza cpl creada
			if(polizaCpl.getIdpoliza() != null){
				polizaComplementariaDao.delete(Poliza.class, polizaCpl.getIdpoliza());
			}
			throw new BusinessException ("Se ha producido un error al crear la poliza complementaria",dao);
		}catch (BusinessException be) {
			logger.error("Se ha producido un error con al copiar las parcelas de la poliza ppal a la poliza complementaria: ", be);
			//Si falla borramos la poliza cpl creada
			if(polizaCpl.getIdpoliza() != null){
				polizaComplementariaDao.delete(Poliza.class, polizaCpl.getIdpoliza());
			}
			throw new BusinessException ("Se ha producido un error con al copiar las parcelas de la poliza ppal a la poliza complementaria",be);
		}
		catch (Exception be) {
			logger.error("Se ha producido un error con al copiar las parcelas de la poliza ppal a la poliza complementaria: ", be);
			//Si falla borramos la poliza cpl creada
			if(polizaCpl.getIdpoliza() != null){
				polizaComplementariaDao.delete(Poliza.class, polizaCpl.getIdpoliza());
			}
			throw new BusinessException ("Se ha producido un error con al copiar las parcelas de la poliza ppal a la poliza complementaria",be);
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	private  PolizaPctComisiones creaComisionesComplementaria(Usuario usuario, Poliza polCpl,
			Poliza polPpl,  Map<String,Object> params) throws DAOException,Exception{
		
		PolizaPctComisiones ppc=null;
		try {
			Map<String, Object> paramsComs = new HashMap<String, Object>();
			paramsComs = polizasPctComisionesManager.validaComisiones (polCpl,usuario);
			
			if (paramsComs.get("alerta") != null) {
				params.put("alerta", paramsComs.get("alerta"));
			}else {//paramsComs.get("polizaPctComisiones")!= null				
				List<PolizaPctComisiones> listappc = (List<PolizaPctComisiones>) paramsComs.get("polizaPctComisiones");
				ppc = (PolizaPctComisiones) listappc.get(0);

				logger.debug("pctComisiones guardados: Pctadministracion: " + 
				ppc.getPctadministracion().toString() + " Pctadquisicion: " +	
				ppc.getPctadquisicion().toString() + " pctcomMed: " + ppc.getPctesmediadora().toString());
			}
			return ppc;
			
		}catch(DAOException d) {
			logger.error("Error al validar las comisiones de la poliza complementaria",d);
			throw d;
		
		}catch (Exception e ) {
			logger.error("Error al validar las comisiones de la poliza complementaria",e);
			throw e;	
		}
		
	}
	
	// Metodo que transforma las coberturas de la situacion actual
	// en el formato esperado por el modelo de datos de Agroplus y puebla el
	// objeto Hibernate encargado de la importacion
	protected void populateCoberturas(final Poliza polizaHbm,
				final Object cobertura) {
			Set<ModuloPoliza> modulosPolHbm;
			
			Set<ComparativaPoliza> comparativasPolHbm;
			ModuloPoliza moduloPolHbm;
			ComparativaPoliza comparativaPolHbm;

			modulosPolHbm = new HashSet<ModuloPoliza>();
			moduloPolHbm = new ModuloPoliza();
			BigDecimal secuencia = null;
			try {
				secuencia = polizaComplementariaDao.getSecuencia("SQ_MODULOS_POLIZA");
			} catch (DAOException e) {
				logger.error("Error al recoger la secuencia del modulo",e);
			}
			
			moduloPolHbm.setId(new ModuloPolizaId(polizaHbm.getIdpoliza(),
					polizaHbm.getLinea().getLineaseguroid(), polizaHbm
							.getCodmodulo(),secuencia.longValue()));
			moduloPolHbm.setPoliza(polizaHbm);
			try{
				polizaComplementariaDao.saveOrUpdate(moduloPolHbm);
				modulosPolHbm.add(moduloPolHbm);
				polizaHbm.setModuloPolizas(modulosPolHbm);
				
				// Es principal
				/* Pet. 57626 ** MODIF TAM (22.07.2020) */  
				if (cobertura instanceof es.agroseguro.contratacion.Cobertura) {
					String modSitAct = polizaHbm.getCodmodulo();
					Long linSegId = polizaHbm.getLinea().getLineaseguroid();
					BigDecimal codLin = polizaHbm.getLinea().getCodlinea();
					es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables = ((es.agroseguro.contratacion.Cobertura) cobertura)
							.getDatosVariables();
					comparativasPolHbm = new HashSet<ComparativaPoliza>();
					if (null != datosVariables){
						// GARANTIZADO
						if (datosVariables.getGarantArray() != null
								&& datosVariables.getGarantArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.Garantizado g : datosVariables
									.getGarantArray()) {								
								
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId,modSitAct,
										BigDecimal.valueOf(g.getCPMod()), BigDecimal.valueOf(g.getCodRCub()));
								
									if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescGarantizado(new BigDecimal(g.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(g.getCPMod()),
											BigDecimal.valueOf(g.getCodRCub()),
											ConstantsConceptos.CODCPTO_GARANTIZADO,
											filaModulo,
											BigDecimal.valueOf(g.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
		
						// CALCULO INDEMNIZACION
						if (datosVariables.getCalcIndemArray() != null
								&& datosVariables.getCalcIndemArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.CalculoIndemnizacion c : datosVariables
									.getCalcIndemArray()) {
								
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId,modSitAct, BigDecimal.valueOf(c.getCPMod()), BigDecimal.valueOf(c.getCodRCub()));
								if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescCalcIndemnizacion(new BigDecimal(c.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(c.getCPMod()),
											BigDecimal.valueOf(c.getCodRCub()),
											ConstantsConceptos.CODCPTO_CALCULO_INDEMNIZACION,
											filaModulo,
											BigDecimal.valueOf(c.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
		
						// % FRANQUICIA
						if (datosVariables.getFranqArray() != null
								&& datosVariables.getFranqArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia pf : datosVariables
									.getFranqArray()) {
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId,modSitAct, BigDecimal.valueOf(pf.getCPMod()), BigDecimal.valueOf(pf.getCodRCub()));
								if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescPctFranquicia(new BigDecimal(pf.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(pf.getCPMod()),
											BigDecimal.valueOf(pf.getCodRCub()),
											ConstantsConceptos.CODCPTO_PCT_FRANQUICIA,
											filaModulo,
											BigDecimal.valueOf(pf.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
		
						// MINIMO INDEMNIZABLE
						if (datosVariables.getMinIndemArray() != null
								&& datosVariables.getMinIndemArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable pmi : datosVariables
									.getMinIndemArray()) {
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId, modSitAct, BigDecimal.valueOf(pmi.getCPMod()), BigDecimal.valueOf(pmi.getCodRCub()));
								if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescMinimoIndemnizable(new BigDecimal(pmi.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(pmi.getCPMod()),
											BigDecimal.valueOf(pmi.getCodRCub()),
											ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE,
											filaModulo,
											BigDecimal.valueOf(pmi.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
		
						// TIPO FRANQUICIA
						if (datosVariables.getTipFranqArray() != null
								&& datosVariables.getTipFranqArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.TipoFranquicia tf : datosVariables
									.getTipFranqArray()) {
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId,modSitAct, BigDecimal.valueOf(tf.getCPMod()), BigDecimal.valueOf(tf.getCodRCub()));
								if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescTipoFranquicia(new BigDecimal(tf.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(tf.getCPMod()),
											BigDecimal.valueOf(tf.getCodRCub()),
											ConstantsConceptos.CODCPTO_TIPO_FRANQUICIA,
											filaModulo,
											new BigDecimal(tf.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
						// % CAPITAL ASEGURADO
						if (datosVariables.getCapAsegArray() != null
								&& datosVariables.getCapAsegArray().length > 0) {
							for (es.agroseguro.contratacion.datosVariables.PorcentajeCapitalAsegurado pca : datosVariables
									.getCapAsegArray()) {
								BigDecimal filaModulo = polizaComplementariaDao.getFilaModulo(linSegId,modSitAct, BigDecimal.valueOf(pca.getCPMod()), BigDecimal.valueOf(pca.getCodRCub()));
								if (filaModulo != null){
									String desVal = polizaComplementariaDao.getDescCapitalAseguradoEleg(new BigDecimal(pca.getValor()));
									comparativaPolHbm = generarComparativaPoliza(
											polizaHbm,
											BigDecimal.valueOf(pca.getCPMod()),
											BigDecimal.valueOf(pca.getCodRCub()),
											ConstantsConceptos.CODCPTO_CAPITAL_ASEGURADO,
											filaModulo,
											BigDecimal.valueOf(pca.getValor()),desVal,
											BigDecimal.valueOf(1),modSitAct,linSegId, secuencia);
			
									polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
									comparativasPolHbm.add(comparativaPolHbm);
								}
							}
						}
						
						comparativasPolHbm.addAll(getComparativasRiesgCubEleg(polizaHbm,
								datosVariables.getRiesgCbtoElegArray(),modSitAct,linSegId,codLin, secuencia));
					}	
					polizaHbm.setComparativaPolizas(comparativasPolHbm);
				} else {
	
					// Las polizas complementarias no traen datos de coberturas. Son los
					// mismos que la poliza principal.
				}
			} catch (Exception ex) {
				logger.error("Se ha producido un error en el acceso a la BBDD",ex);
				//throw new DAOException("Se ha producido un error en el acceso a la BBDD",ex);
			}
		}
	
	private static ComparativaPoliza generarComparativaPoliza(Poliza polizaHbm, BigDecimal cpm, BigDecimal rCub,
			int codConcepto, BigDecimal filaModulo, BigDecimal valor, String descValor, BigDecimal filaComparativa,
			String modSitAct, Long lineaseguroid, BigDecimal secuencia) {

		ComparativaPoliza comparativaPolHbm = new ComparativaPoliza();
		ComparativaPolizaId id = new ComparativaPolizaId();

		id.setFilamodulo(filaModulo);
		id.setCodconcepto(BigDecimal.valueOf(codConcepto));
		id.setCodconceptoppalmod(cpm);
		id.setCodriesgocubierto(rCub);
		id.setCodmodulo(modSitAct);
		id.setCodvalor(valor);
		id.setIdpoliza(polizaHbm.getIdpoliza());
		id.setLineaseguroid(lineaseguroid);
		id.setFilacomparativa(filaComparativa);
		id.setIdComparativa(secuencia.longValue());

		comparativaPolHbm.setDescvalor(descValor);
		comparativaPolHbm.setId(id);

		comparativaPolHbm.setPoliza(polizaHbm);

		return comparativaPolHbm;
	}

	private List<ComparativaPoliza> getComparativasRiesgCubEleg(
			final Poliza polizaHbm,
			final es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido[] riesgCbtoElegArray,
			String modSitAct, Long lineaseguroid,BigDecimal codLin, BigDecimal secuencia){

		List<ComparativaPoliza> comparativasRiesgCubEleg;
		List<RiesgoCubiertoModulo> rCubMobList = new ArrayList<RiesgoCubiertoModulo>();
		ComparativaPoliza comparativaPolHbm;
		BigDecimal codConcepto;
		BigDecimal valor;
		String descValor;
		BigDecimal filaComparativa;

		final BigDecimal[] lineasEspeciales = new BigDecimal[] { BigDecimal
				.valueOf(301) };

		comparativasRiesgCubEleg = new ArrayList<ComparativaPoliza>();

		try {
			rCubMobList = polizaComplementariaDao.getListRiesgoCubiertoMod(lineaseguroid,
					modSitAct,Constants.CHARACTER_S);
		} catch (DAOException e) {
			logger.error("Excepcion : PolizaComplementariaManager - getComparativasRiesgCubEleg", e);
		}
		for (RiesgoCubiertoModulo rcmodHbm : rCubMobList) {

			if (riesgCbtoElegArray != null && riesgCbtoElegArray.length > 0) {

				codConcepto = BigDecimal.valueOf(ConstantsConceptos.CODCPTO_RIESGO_CUBIERTO_ELEGIDO);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "N";
				filaComparativa = BigDecimal.valueOf(0);

				for (es.agroseguro.contratacion.datosVariables.RiesgoCubiertoElegido rce : riesgCbtoElegArray) {

					if (rcmodHbm.getRiesgoCubierto().getId()
							.getCodriesgocubierto()
							.equals(BigDecimal.valueOf(rce.getCodRCub()))
							&& rcmodHbm.getConceptoPpalModulo()
									.getCodconceptoppalmod()
									.equals(BigDecimal.valueOf(rce.getCPMod()))) {
						descValor = rce.getValor();
						valor = new BigDecimal(
								"S".equals(rce.getValor()) ? Constants.RIESGO_ELEGIDO_SI
										: Constants.RIESGO_ELEGIDO_NO);
						if (!Arrays.asList(lineasEspeciales).contains(
								codLin)) {
							filaComparativa = "S".equals(rce.getValor()) ? BigDecimal
									.valueOf(1) : BigDecimal.valueOf(2);
						}
						break;
					}
				}
			} else {

				codConcepto = BigDecimal.valueOf(0);
				valor = new BigDecimal(Constants.RIESGO_ELEGIDO_NO);
				descValor = "";
				filaComparativa = Arrays.asList(lineasEspeciales).contains(
						codLin) ? BigDecimal
						.valueOf(0) : BigDecimal.valueOf(2);
			}

			comparativaPolHbm = generarComparativaPoliza(polizaHbm, rcmodHbm
					.getConceptoPpalModulo().getCodconceptoppalmod(), rcmodHbm
					.getRiesgoCubierto().getId().getCodriesgocubierto(),
					codConcepto.intValue(), rcmodHbm.getId().getFilamodulo(), valor,
					descValor, filaComparativa,modSitAct,lineaseguroid, secuencia);

			try {
				polizaComplementariaDao.saveOrUpdate(comparativaPolHbm);
			} catch (DAOException e) {
				logger.error("Excepcion : PolizaComplementariaManager - getComparativasRiesgCubEleg", e);
			}
			comparativasRiesgCubEleg.add(comparativaPolHbm);
		}

		// Ajuste de fila comparativa para lineas especiales
		if (!comparativasRiesgCubEleg.isEmpty()
				&& Arrays.asList(lineasEspeciales).contains(
						codLin)) {

			// Ordenamos por fila del modulo
			Collections.sort(comparativasRiesgCubEleg,
					new Comparator<ComparativaPoliza>() {
						@Override
						public int compare(final ComparativaPoliza arg0,
								final ComparativaPoliza arg1) {
							return arg0.getId().getFilamodulo()
									.compareTo(arg1.getId().getFilamodulo());
						}
					});

			if (BigDecimal.valueOf(301).equals(
					codLin)) {
				// LS301 tiene fila 7 y fila 12
				// Filacomp 1: ambos elegidos
				// Filacomp 2: elegida fila 7
				// Filacomp 3: elegida fila 12
				// Filacomp 4: ninguno elegido
				final ComparativaPoliza compFila7 = comparativasRiesgCubEleg
						.get(0);
				final ComparativaPoliza compFila12 = comparativasRiesgCubEleg
						.get(1);
				if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(4);
					compFila7.getId().setCodconcepto(BigDecimal.valueOf(0));
					compFila12.getId().setCodconcepto(BigDecimal.valueOf(0));
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_NO))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(3);
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_NO))) {
					filaComparativa = BigDecimal.valueOf(2);
				} else if (compFila7.getId().getCodvalor()
						.equals(new BigDecimal(Constants.RIESGO_ELEGIDO_SI))
						&& compFila12
								.getId()
								.getCodvalor()
								.equals(new BigDecimal(
										Constants.RIESGO_ELEGIDO_SI))) {
					filaComparativa = BigDecimal.valueOf(1);
				} else {
					filaComparativa = BigDecimal.valueOf(0);
				}
				compFila7.getId().setFilacomparativa(filaComparativa);
				compFila12.getId().setFilacomparativa(filaComparativa);
			}
		}
		return comparativasRiesgCubEleg;
	}
	
	
	private boolean  cargaParcelasFromWSMod(String referencia,
			BigDecimal codplan, String realPath, Long lineaseguroid,Long idPoliza, PolizaActualizadaResponse respuesta ) throws Exception {		
		List<CapitalAsegurado> listaCapAseg = null;
		CapitalAsegurado capAsegCpl = null;
		Parcela parCpl = null;
		
		logger.debug("PolizaComplementariaManager - cargaParcelasFromWSMod [INIT]");
		
		try {
			if (respuesta == null || null == respuesta.getPolizaPrincipalUnif()){
				SWAnexoModificacionHelper helper = new SWAnexoModificacionHelper();
				respuesta = helper.getPolizaActualizada(referencia, codplan, realPath);
			}
			
			//Recorremos las explotaciones de la situacion actualizada y vamos creando las explotaciones del anexo
			Node currNode = respuesta.getPolizaPrincipalUnif().getPoliza().getObjetosAsegurados().getDomNode().getFirstChild();
			
			while (currNode != null) {
				if (currNode.getNodeType() == Node.ELEMENT_NODE) {
					
					es.agroseguro.contratacion.parcela.ParcelaDocument par = null;
					par = es.agroseguro.contratacion.parcela.ParcelaDocument.Factory.parse(currNode);

					if (par != null){
				
						//for (es.agroseguro.seguroAgrario.contratacion.Parcela par : respuesta.getPolizaPrincipal().getPoliza().getObjetosAsegurados().getParcelaArray()){

						listaCapAseg = new ArrayList<CapitalAsegurado>();
				
						for (es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg : par.getParcela().getCosecha().getCapitalesAsegurados().getCapitalAseguradoArray()){
					
							parCpl = new Parcela();
							
							logger.debug("Tratando parcela Hoja:"+par.getParcela().getHoja());
							logger.debug("Tratando parcela Numero:"+par.getParcela().getNumero());
							
							// Parcela
							parCpl.getPoliza().setIdpoliza(idPoliza);
							parCpl.setHoja(par.getParcela().getHoja());
							parCpl.setNumero(par.getParcela().getNumero());
							parCpl.setNomparcela(par.getParcela().getNombre());
							parCpl.setAltaencomplementario(Constants.CHARACTER_N);
							parCpl.setTipoparcela(Character.valueOf('P'));
							// Ubicacion
							parCpl.getTermino().getProvincia().setCodprovincia(new BigDecimal(par.getParcela().getUbicacion().getProvincia()));
							parCpl.getTermino().getId().setCodprovincia(new BigDecimal(par.getParcela().getUbicacion().getProvincia()));
							parCpl.getTermino().getId().setCodtermino(new BigDecimal(par.getParcela().getUbicacion().getTermino()));
							if (!StringUtils.nullToString(par.getParcela().getUbicacion().getSubtermino()).equals("")){
								parCpl.getTermino().getId().setSubtermino(par.getParcela().getUbicacion().getSubtermino().charAt(0));
							}
							else{
								parCpl.getTermino().getId().setSubtermino(' ');
							}
							parCpl.getTermino().getId().setCodcomarca(new BigDecimal(par.getParcela().getUbicacion().getComarca()));
							parCpl.getTermino().getComarca().getId().setCodcomarca(new  BigDecimal(par.getParcela().getUbicacion().getComarca()));
							// Variedad
							parCpl.setCodvariedad(new BigDecimal(par.getParcela().getCosecha().getVariedad()));
							parCpl.setCodcultivo(new BigDecimal(par.getParcela().getCosecha().getCultivo()));
			
							
							//SIGPAC
							if (par.getParcela().getSIGPAC() != null){
								parCpl.setAgrsigpac(new BigDecimal(par.getParcela().getSIGPAC().getAgregado()));
								parCpl.setCodprovsigpac(new BigDecimal(par.getParcela().getSIGPAC().getProvincia()));
								parCpl.setCodtermsigpac(new BigDecimal(par.getParcela().getSIGPAC().getTermino()));
								parCpl.setParcelasigpac(new BigDecimal(par.getParcela().getSIGPAC().getParcela()));
								parCpl.setRecintosigpac(new BigDecimal(par.getParcela().getSIGPAC().getRecinto()));
								parCpl.setZonasigpac(new BigDecimal(par.getParcela().getSIGPAC().getZona()));
								parCpl.setPoligonosigpac(new BigDecimal(par.getParcela().getSIGPAC().getPoligono()));
							}
						
					
							// capitales asegurados
							capAsegCpl = new CapitalAsegurado();
							capAsegCpl.setParcela(parCpl);
							capAsegCpl.setPrecio(capAseg.getPrecio());
							capAsegCpl.setProduccion(new BigDecimal (capAseg.getProduccion()));
							capAsegCpl.setSuperficie(capAseg.getSuperficie());
							capAsegCpl.getTipoCapital().setCodtipocapital(new BigDecimal(capAseg.getTipo()));
							capAsegCpl.setAltaencomplementario(Constants.CHARACTER_N);
							listaCapAseg.add(capAsegCpl);
							
							// datos variables
							List<DatoVariableParcela> datosVariables = getDatosVariables(
									capAseg, respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables(),
									lineaseguroid,capAsegCpl,respuesta.getPolizaPrincipalUnif().getPoliza().getCobertura().getDatosVariables());
							
							capAsegCpl.setDatoVariableParcelas(new HashSet<DatoVariableParcela>(datosVariables));
							
							parCpl.setCapitalAsegurados(new HashSet<CapitalAsegurado>(listaCapAseg));
							
							polizaComplementariaDao.saveOrUpdate(parCpl);
							
						} /* Fin del for */
					} /* Fin del if */
				} /* Fin del if */
				currNode = currNode.getNextSibling();
			} /* Fin del while */
		}catch (AgrException e){
			logger.error("El servicio ha devuelto una excepcion",e);
			throw e;
			
		}catch (Exception ex) {
			logger.error("Error inesperado",ex);
			throw ex;
		}
		
		
		return true;
	}


	private List<DatoVariableParcela> getDatosVariables(
			es.agroseguro.contratacion.parcela.CapitalAsegurado capAseg,
			es.agroseguro.contratacion.datosVariables.DatosVariables datosVariables, Long lineaseguroid,
			CapitalAsegurado capAsegCpl, es.agroseguro.contratacion.datosVariables.DatosVariables datosVarCob) {
		
		DatoVariableParcela dv = new DatoVariableParcela();
        List<DatoVariableParcela> dvList = new ArrayList<DatoVariableParcela>();
        
        logger.debug("PolizaComplementariaManager - getDatosVariables [INIT]");
		
		// Mapa auxiliar con los codigos de concepto de los datos variables y sus etiquetas y tablas asociadas.
		Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla = diccionarioDatosDao.getCodConceptoEtiquetaTablaParcelas(lineaseguroid);
		
		for (BigDecimal codconcepto : auxEtiquetaTabla.keySet()){
			
				// Pet. 63497 (REQ.01) ** MODIF TAM (02.04.2020) ** Inicio //
			if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(codconcepto)
					|| BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE).equals(codconcepto)
					|| BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO).equals(codconcepto)) {
				
					logger.debug("Entramos en if, valor de codconcepto:"+codconcepto);
					
					if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA).equals(codconcepto)) {
						for (es.agroseguro.contratacion.datosVariables.PorcentajeFranquicia franq : capAseg.getDatosVariables().getFranqArray()) {
							if (franq.getValor() > 0) {
								String valor = String.valueOf(franq.getValor());
								dv = new DatoVariableParcela();
								dv.setCapitalAsegurado(capAsegCpl);
								dv.setValor(StringUtils.nullToString(valor));
								dv.setCodconceptoppalmod(Integer.valueOf(franq.getCPMod()));
								dv.setCodriesgocubierto(Integer.valueOf(franq.getCodRCub()));								
								dv.getDiccionarioDatos().setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_PCT_FRANQUICIA));
								dvList.add(dv);
							}
						}	
					} else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE).equals(codconcepto)) {
						for (es.agroseguro.contratacion.datosVariables.PorcentajeMinimoIndemnizable minIndem : capAseg.getDatosVariables().getMinIndemArray()) {
							if (minIndem.getValor() > 0) {
								String valor = String.valueOf(minIndem.getValor());
								dv = new DatoVariableParcela();
								dv.setCapitalAsegurado(capAsegCpl);
								dv.setValor(StringUtils.nullToString(valor));
								dv.setCodconceptoppalmod(Integer.valueOf(minIndem.getCPMod()));
								dv.setCodriesgocubierto(Integer.valueOf(minIndem.getCodRCub()));
								dv.getDiccionarioDatos().setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_MINIMO_INDEMNIZABLE));
								dvList.add(dv);
							}
						}							
					} else if (BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO).equals(codconcepto)) {
						for (es.agroseguro.contratacion.datosVariables.Garantizado garant : capAseg.getDatosVariables().getGarantArray()) {
							if (garant.getValor() > 0) {
								String valor = String.valueOf(garant.getValor());
								dv = new DatoVariableParcela();
								dv.setCapitalAsegurado(capAsegCpl);
								dv.setValor(StringUtils.nullToString(valor));
								dv.setCodconceptoppalmod(Integer.valueOf(garant.getCPMod()));
								dv.setCodriesgocubierto(Integer.valueOf(garant.getCodRCub()));
								dv.getDiccionarioDatos().setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_GARANTIZADO));
								dvList.add(dv);
							}
						}
					}
				}else {
					
			        logger.debug("Entramos en else, valor de codconcepto:"+codconcepto);
	
					try {
							
						//primero obtengo el objeto que representa al dato variable
						Method method = es.agroseguro.contratacion.datosVariables.DatosVariables.class.getMethod("get" + auxEtiquetaTabla.get(codconcepto).getEtiqueta());
						
						Object objeto = null; 
						/*ESC-13894 ** MODIF TAM (18.05.2021) ** Inicio */ 
						/* Incluimos validacion */ 
						if (capAseg.getDatosVariables()!= null) { 
							objeto = method.invoke(capAseg.getDatosVariables()); 
						} 
						/*ESC-13894 ** MODIF TAM (18.05.2021) ** Fin */	 
						
						if (objeto != null){
							dv =  setDatoVariableToPArcela (objeto,auxEtiquetaTabla,lineaseguroid,codconcepto);
							dv.setCapitalAsegurado(capAsegCpl);
							dvList.add(dv);
						}
						if (null != datosVarCob){
							Object objeto2 = method.invoke(datosVarCob);
								if (objeto2 != null){
									dv =  setDatoVariableToPArcela (objeto2,auxEtiquetaTabla,lineaseguroid,codconcepto);
									dv.setCapitalAsegurado(capAsegCpl);
									dvList.add(dv);
								}
						}
					
					} catch (SecurityException e) {
						logger.debug("Error de seguridad " + e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.debug("El metodo no existe para esta clase " + e.getMessage());
					} catch (IllegalArgumentException e) {
						logger.debug("El metodo acepta los argumentos " + e.getMessage());
					} catch (IllegalAccessException e) {
						logger.debug("Error " + e.getMessage());
					} catch (InvocationTargetException e) {
						logger.debug("Error " + e.getMessage());
					}
			}		
		}
		// Pet. 63497 (REQ.01) ** MODIF TAM (02.04.2020) ** Fin //	
		
		// Recorremos el array de fechas fin garantia, y si tiene anhadimos el valor
		// a la lista como otro dato variable mas
		/* ESC-13894 */
		if (capAseg.getDatosVariables() != null) { 
			for (es.agroseguro.contratacion.datosVariables.FechaLimiteGarantias ffg : capAseg.getDatosVariables().getFecFGarantArray()) {
				
				if (ffg.getValor()!= null) {
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
					Date d = new Date();
					String fecha="";
					try {
						d = sdf.parse(ffg.getValor().toString());
						fecha = sdf2.format(d);
					} catch (ParseException e) {
						logger.error("Error al parsear la fecha en los datos variables de los capitales asegurados de siniestros", e);
					}
					dv = new DatoVariableParcela();
					dv.setCapitalAsegurado(capAsegCpl);
					dv.setValor(fecha);
					dv.getDiccionarioDatos().setCodconcepto(BigDecimal.valueOf(ConstantsConceptos.CODCPTO_FEC_FIN_GARANT));
					dvList.add(dv);
					break;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (DatoVariableParcela dvaux : dvList) {
			sb.append(dvaux.getDiccionarioDatos().getCodconcepto() + ", ");
		}		
		logger.debug("dvList: " + sb.toString());
		logger.debug("PolizaComplementariaManager - getDatosVariables [END]");
		return dvList;
	}


	private DatoVariableParcela setDatoVariableToPArcela(Object objeto,
			Map<BigDecimal, RelacionEtiquetaTabla> auxEtiquetaTabla,
			Long lineaseguroid, BigDecimal codconcepto) {
		
		DatoVariableParcela dv = null;
		
		//primero obtengo el objeto que representa al dato variable
		try {
			Method methodValor = objeto.getClass().getMethod("getValor");
			Object valor = methodValor.invoke(objeto);
			
			if (!StringUtils.nullToString(valor).equals("")){
				//Creo un objeto "DatoVariable" y lo anado a la lista con sus datos
				dv = new DatoVariableParcela();
				dv.getDiccionarioDatos().setCodconcepto(codconcepto);
				if (valor instanceof XmlCalendar) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
					Date d = new Date();
					String fecha="";
					try {
						d = sdf.parse(valor.toString());
						fecha = sdf2.format(d);
					} catch (ParseException e) {
						logger.error("Error al parsear la fecha en los datos variables de los capitales asegurados de las parcelas CPL", e);
					}
					
					dv.setValor(fecha);
				}else {
					dv.setValor(StringUtils.nullToString(valor));
				}
				
			}
		} catch (SecurityException e) {
			logger.debug("Error de seguridad " + e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("El metodo no existe para esta clase " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("El metodo acepta los argumentos " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug("Error " + e.getMessage());
		} catch (InvocationTargetException e) {
			logger.debug("Error " + e.getMessage());
		}
		return dv;
	}


	/**
	 * Metodo que clona los datos de una poliza a su correspondiente complementaria. Y la guarda en la BBDD
	 * TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico (historicoEstadosManager.insertaEstado) 
	 * @param poliza
	 * @param moduloCpl
	 * @param usuario
	 * @throws DAOException
	 */
	private Poliza crearPolizaCpl(Poliza poliza,Modulo moduloCpl,Usuario usuario) throws DAOException {
		logger.debug("init - crearPolizaCpl");
		
		Poliza polizaCpl = new Poliza();
		polizaCpl.setUsuario(usuario);
		polizaCpl.setLinea(poliza.getLinea());
		polizaCpl.setAsegurado(poliza.getAsegurado());
		polizaCpl.setColectivo(poliza.getColectivo());
		polizaCpl.setDiscriminante(poliza.getDiscriminante());
		polizaCpl.setCodmodulo(moduloCpl.getId().getCodmodulo());
		if (poliza.getReferencia()!=null){
			polizaCpl.setReferencia(poliza.getReferencia());
		}
		if (poliza.getDc()!=null){
			polizaCpl.setDc(poliza.getDc());
		}
		
		polizaCpl.setEstadoPagoAgp(new EstadoPagoAgp(Constants.POLIZA_NO_PAGADA, null, null));
		
		polizaCpl.setTipoReferencia(Constants.MODULO_POLIZA_COMPLEMENTARIO);
		polizaCpl.setBloqueadopor(usuario.getCodusuario());
		polizaCpl.setFechabloqueo((new GregorianCalendar()).getTime());
		
		//antes de grabar,relleno la oficina con ceros a la izq. hasta completar el maximo permitido
		Integer ofi = new Integer(StringUtils.nullToString(poliza.getOficina()));
		polizaCpl.setOficina(String.format("%04d", ofi.intValue()));
		polizaCpl.setClase(usuario.getClase().getClase());
		polizaCpl.setTienesiniestros('N');
		polizaCpl.setTieneanexomp('N');
		polizaCpl.setTieneanexorc('N');
		//TMR 01/04/2013 le anadimos a la cpl la ppl.
		polizaCpl.setPolizaPpal(poliza);
		
		/*EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION);
		savePolizaCpl(polizaCpl, estado, usuario.getCodusuario());*/
		
		logger.debug("end - crearPolizaCpl");
		
		return polizaCpl;
	}

	/**
	 * Metodo para guardar los datos de una poliza complementaria y actualizar el historico de estado si es necesario
	 * @param polizaCpl Poliza complementaria a guardar
	 * @param newEstado Estado al que pasa la poliza
	 * @param codusuario Usuario que realiza la operacion
	 * @throws DAOException
	 * @throws BusinessException 
	 */
	private void savePolizaCpl(Poliza polizaCpl, EstadoPoliza newEstado, String codusuario) throws DAOException, BusinessException {
		
		EstadoPoliza estadoOld = polizaCpl.getEstadoPoliza();
		if (newEstado != null){
			polizaCpl.setEstadoPoliza(newEstado);
		}
		
		polizaComplementariaDao.saveOrUpdate(polizaCpl);
		
		
		if (polizaCpl.getGedDocPoliza() == null) {
			//generamos la info de ged incluyendo el codigo de barras	
			this.documentacionGedManager.saveNewGedDocPoliza(polizaCpl.getIdpoliza(), codusuario);
		}
		
		if (newEstado != null && (estadoOld.getIdestado() == null || !estadoOld.getIdestado().equals(newEstado.getIdestado()))){
			historicoEstadosManager.insertaEstado(Tabla.POLIZAS, 
					polizaCpl.getIdpoliza(), codusuario, newEstado.getIdestado());
		}
		
		polizaComplementariaDao.evict(polizaCpl);
	}
	
	/**
	 * Metodo para obtener el id de una poliza complementaria
	 * @param referencia
	 * @param lineaseguroid
	 * @return
	 * @throws BusinessException
	 */
	public Long getIdPolizaCplByRef(String referencia,Long lineaseguroid) throws BusinessException {
		logger.debug("init - getIdPolizaCplByRef");
		Long id = null;
		try {
			logger.debug("parametros. ref: " + referencia + "linea: " + lineaseguroid);
			id = polizaComplementariaDao.getIdPolizaByRef(referencia, Constants.MODULO_POLIZA_COMPLEMENTARIO,lineaseguroid);
			logger.debug("end - getIdPolizaCplByRef. ID: " +id);
			return id;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el id de la poliza", dao);
			throw new BusinessException ("Se ha producido un error al recuperar el id de la poliza",dao);
		}
	}
	
	/**
	 * Metodo para obtener el id de una poliza principal
	 * @param referencia
	 * @param lineaseguroid
	 * @return
	 * @throws BusinessException
	 */
	public Long getIdPolizaPpalByRef(String referencia,Long lineaseguroid) throws BusinessException {
		logger.debug("init - getIdPolizaPpalByRef");
		Long id = null;
		try {
			if(lineaseguroid != null){
				logger.debug("parametros. ref: " + referencia + "linea: " + lineaseguroid);
				id = polizaComplementariaDao.getIdPolizaByRef(referencia, Constants.MODULO_POLIZA_PRINCIPAL,lineaseguroid);
				logger.debug("end - getIdPolizaPpalByRef. ID: " +id);
			}else{
				id = polizaComplementariaDao.getIdPolizaByRef(referencia, Constants.MODULO_POLIZA_COMPLEMENTARIO);
			}
			return id;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el id de la poliza", dao);
			throw new BusinessException ("Se ha producido un error al recuperar el id de la poliza",dao);
		}
	}
	
	
	/**
	 * Metodo para listar capitales asegurados
	 * @param capAseg
	 * @return
	 * @throws BusinessException
	 */
	public List<CapitalAsegurado> getCapitalesAsegPolCpl(CapitalAsegurado capAseg) throws BusinessException {
		logger.debug("init - getCapitalesAsegPolCpl");
		List<CapitalAsegurado> capitales= null;
		try {
			
			capitales = polizaComplementariaDao.getCapitalesAsegPolCpl(capAseg);
			logger.debug("end - getCapitalesAsegPolCpl");
			return capitales;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al recuperar el listado de parcelas", dao);
			throw new BusinessException ("Se ha producido un error al recuperar el listado de parcelas",dao);
		}
	}
	
	public void capitalesAsegModificadosLista (String listaIdCapAseg, String tipoInc, String incremento) {
		
		// Obtiene el array de ids de capitales asegurados
		String[] listaId = null;
		try {
			listaId = listaIdCapAseg.split(",");
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al obtener el array de ids de capitales asegurados", e);
			return;
		}
		
		// Recorre el array e incrementa cada capital asegurado
		for (int i = 0; i < listaId.length; i++) {
			if (!listaId[i].equals("")){
				// Obtiene el capital asegurado de bd
				CapitalAsegurado capAseguradoAux = null;		
				try {
					capAseguradoAux = (CapitalAsegurado) polizaComplementariaDao.get(CapitalAsegurado.class, new Long(listaId[i]));
				} catch (Exception e) {
					logger.error("Ocurrio un error al obtener el capitale asegurado de bd", e);
					return;
				}
				
				// Calcula el incremento para ese CA
				String resultado = "";
				//logger.debug(capAseguradoAux.getSuperficie() + " * " + incremento + " = " + capAseguradoAux.getSuperficie().multiply(new BigDecimal (incremento)));
				if("ha".equals(tipoInc)){
		        	resultado = Double.toString(NumberUtils.redondear ((capAseguradoAux.getSuperficie().multiply(new BigDecimal (incremento))) ,2));
		        	//logger.debug(capAseguradoAux.getSuperficie() + " -> " + resultado);
		        }
				else if ("kha".equals(tipoInc)){
					resultado = Double.toString(NumberUtils.redondear ((capAseguradoAux.getSuperficie().multiply(new BigDecimal (incremento)).subtract(capAseguradoAux.getProduccion())) ,2));
				}
				else{
		        	resultado = Double.toString(NumberUtils.redondear (new BigDecimal (incremento) ,2));
		        }
				
				BigDecimal incrementoProduccion = new BigDecimal(resultado);
				logger.debug("Incremento de produccion: " + incrementoProduccion);
				if (incrementoProduccion.compareTo(new BigDecimal(0)) > 0){
					logger.debug(capAseguradoAux.getSuperficie() + " -> " + incrementoProduccion);
					
					// Actualiza los datos del CA
					capAseguradoAux.setAltaencomplementario('S');
					capAseguradoAux.setIncrementoproduccion(incrementoProduccion);
					capAseguradoAux.getParcela().setAltaencomplementario('S');
					
				}else{
					// Actualiza los datos del CA: Si el valor es negativo, no se pone incremento
					capAseguradoAux.setAltaencomplementario('N');
					capAseguradoAux.setIncrementoproduccion(null);
					capAseguradoAux.getParcela().setAltaencomplementario('N');
				}
				try {
					polizaComplementariaDao.saveOrUpdate(capAseguradoAux);
				} catch (DAOException e) {
					logger.error("Ocurrio un error al actualizar el capitale asegurado de bd", e);
				}
			}
		}
		
	}
	/**
	 * incrementa las parcelas del anexo de la polizaComplementaria
	 * @param listaIds
	 * @param tipoInc
	 * @param incremento
	 */
	public void capitalesAsegAnexoModificadosLista (String listaIds, String tipoInc, String incremento) {
		
		// Obtiene el array de ids de capitales asegurados
		String[] listaId = null;
		try {
			listaId = listaIds.split(",");
		}
		catch (Exception e) {
			logger.error("Ocurrio un error al obtener el array de ids de capitales asegurados", e);
			return;
		}
		// Recorre el array e incrementa cada capital asegurado
		for (int i = 0; i < listaId.length; i++) {
			if (!listaId[i].equals("")){
				// Obtiene el capital asegurado de bd
				com.rsi.agp.dao.tables.anexo.CapitalAsegurado capAseguradoAux = null;		
				try {
					capAseguradoAux = (com.rsi.agp.dao.tables.anexo.CapitalAsegurado) polizaComplementariaDao.get(com.rsi.agp.dao.tables.anexo.CapitalAsegurado.class, new Long(listaId[i]));
				} catch (Exception e) {
					logger.error("Ocurrio un error al obtener el capitale asegurado de bd", e);
					return;
				}
				// Calcula el incremento para ese CA
				String resultado = "";
				if("ha".equals(tipoInc)){
		        	resultado = Double.toString(NumberUtils.redondear ((capAseguradoAux.getSuperficie().multiply(new BigDecimal (incremento))) ,2));

		        }
				else if ("kha".equals(tipoInc)){
					if (capAseguradoAux.getProduccion()==null){
						capAseguradoAux.setProduccion(new BigDecimal(0));
					}
					resultado = Double.toString(NumberUtils.redondear ((capAseguradoAux.getSuperficie().multiply(new BigDecimal (incremento)).subtract(capAseguradoAux.getProduccion())) ,2));
				}
				else{
		        	resultado = Double.toString(NumberUtils.redondear (new BigDecimal (incremento) ,2));
		        }
				
				BigDecimal incrementoProduccion = new BigDecimal(resultado);
				logger.debug("Incremento de produccion: " + incrementoProduccion);
			    logger.debug(capAseguradoAux.getSuperficie() + " -> " + incrementoProduccion);
			    if (incrementoProduccion.compareTo(new BigDecimal(0)) > 0){
				    // Actualiza los datos del CA
					capAseguradoAux.setAltaencomplementario('S');
					capAseguradoAux.setIncrementoproduccion(incrementoProduccion);
					capAseguradoAux.getParcela().setAltaencomplementario('S');
					capAseguradoAux.setValorincremento(new BigDecimal(incremento));
					capAseguradoAux.setTipoincremento(tipoInc);
					capAseguradoAux.setTipomodificacion('M');
					capAseguradoAux.getParcela().setTipomodificacion('M');
			    }else{
					// Actualiza los datos del CA: Si el valor es negativo, no se pone incremento
					capAseguradoAux.setAltaencomplementario('N');
					capAseguradoAux.setIncrementoproduccion(null);
					capAseguradoAux.getParcela().setAltaencomplementario('N');
					capAseguradoAux.setValorincremento(null);
					capAseguradoAux.setTipoincremento(null);
				}
			    
				
				try {
					polizaComplementariaDao.saveOrUpdate(capAseguradoAux);
				} catch (DAOException e) {
					logger.error("Ocurrio un error al actualizar el capitale asegurado de bd", e);
				}
			}
		}
		
	}
	/**
	 * Metodo que devuelve  string por referencia, con los listados de checks de alta y los incrementos
	 * @param listCapAseg
	 * @param capString
	 * @param incrementosString
	 */
	public void getlistaAltas(List<CapitalAsegurado> listCapAseg,StringBuilder capString, StringBuilder incrementosString) {
		
		String cadena = "alta_";
		for(CapitalAsegurado cap: listCapAseg){
			if(cap.getAltaencomplementario().toString().equals("S")){
//				capString.append(cadena+cap.getIdcapitalasegurado().toString() + "#" + cap.getPrecio().toString() + "#" + cap.getProduccion().toString() + "|");
				capString.append(cadena+cap.getIdcapitalasegurado().toString() + "#" + cap.getSuperficie().toString() + "|");
				if(cap.getIncrementoproduccion() != null)
					incrementosString.append(cap.getIdcapitalasegurado().toString() + "#" +  cap.getIncrementoproduccion().toString() + "|"); 
			}
		}
	}
	
	/**
	 * Metodo que actualiza el estado de la poliza complementaria
	 * @param poliza
	 * @return
	 * @throws BusinessException
	 */
	public Poliza polizaComplementariaPendienteValidacion(Poliza poliza, BigDecimal estado, String codusuario) throws BusinessException {
		try {
			
			EstadoPoliza estadoP = new EstadoPoliza();
			estadoP.setIdestado(estado);

			this.savePolizaCpl(poliza, estadoP, codusuario);
			
			return poliza;
			
		} catch (DAOException dao) {
			logger.error("Se ha producido un error al actualizar el estado de la poliza complementaria: " + dao.getMessage());
			throw new BusinessException ("Se ha producido un error al actualizar el estado de la poliza complementaria",dao);
		}
	}
	
	/**
	 * Metodo que comprueba que hay capitales asegurados con incremento y devuelve una lista con ellos
	 * @param listCapAseg
	 * @return
	 */
	public boolean getCapitalesConIncremento(Poliza poliza) {
		boolean existe = false;
		for (Parcela p: poliza.getParcelas()){
			for (CapitalAsegurado ca: p.getCapitalAsegurados()){
				if (ca.getIncrementoproduccion() != null && ca.getIncrementoproduccion().compareTo(new BigDecimal(0)) > 0){
					existe = true;
					break;
				}
			}
			if (existe){
				break;
			}
		}
		
		return existe;
	}
	
	public void setPolizaComplementariaDao(IPolizaComplementariaDao polizaComplementariaDao) {
		this.polizaComplementariaDao = polizaComplementariaDao;
	}

	public void setDatabaseManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}


	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}


	public void setDiccionarioDatosDao(IDiccionarioDatosDao diccionarioDatosDao) {
		this.diccionarioDatosDao = diccionarioDatosDao;
	}
	
	public void setPolizasPctComisionesManager(
			IPolizasPctComisionesManager polizasPctComisionesManager) {
		this.polizasPctComisionesManager = polizasPctComisionesManager;
	}
	
	public void setDocumentacionGedManager(IDocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
}