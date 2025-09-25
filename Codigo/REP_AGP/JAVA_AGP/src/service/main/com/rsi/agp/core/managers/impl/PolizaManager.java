package com.rsi.agp.core.managers.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.ValidacionPolizaException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.ModuloComparator;
import com.rsi.agp.core.util.ModuloPolizaComparator;
import com.rsi.agp.core.util.OrganizadorInfoConstants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.cesp.RiesgoCubiertoModuloFiltro;
import com.rsi.agp.dao.filters.commons.ErrorWsFiltro;
import com.rsi.agp.dao.filters.cpl.ModuloFiltro;
import com.rsi.agp.dao.filters.poliza.MedidaFiltro;
import com.rsi.agp.dao.models.admin.IClaseDao;
import com.rsi.agp.dao.models.config.IClaseDetalleDao;
import com.rsi.agp.dao.models.cpm.ICPMTipoCapitalDAO;
import com.rsi.agp.dao.models.poliza.IDatosAvalDao;
import com.rsi.agp.dao.models.poliza.IFechaContratacionDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeleccionPolizaDao;
import com.rsi.agp.dao.models.poliza.ganado.ISeleccionComparativaSWDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.MedidaFranquicia;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;
import com.rsi.agp.dao.tables.param.Parametro;
import com.rsi.agp.dao.tables.poliza.CapAsegRelModulo;
import com.rsi.agp.dao.tables.poliza.CapitalAsegurado;
import com.rsi.agp.dao.tables.poliza.ComparativaPoliza;
import com.rsi.agp.dao.tables.poliza.ComparativaPolizaId;
import com.rsi.agp.dao.tables.poliza.ComsPctCalculado;
import com.rsi.agp.dao.tables.poliza.Comunicaciones;
import com.rsi.agp.dao.tables.poliza.DatoVariableParcela;
import com.rsi.agp.dao.tables.poliza.DatosAval;
import com.rsi.agp.dao.tables.poliza.DistribucionCoste;
import com.rsi.agp.dao.tables.poliza.EnvioAgroseguro;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.ModuloPoliza;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaCoberturaSW;
import com.rsi.agp.dao.tables.poliza.ModuloPolizaId;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubAseguradoENESA;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;
import com.rsi.agp.dao.tables.poliza.dc2015.DistribucionCoste2015;
import com.rsi.agp.vo.ProduccionVO;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.Documento;

@SuppressWarnings({"unchecked", "rawtypes"}) 
public class PolizaManager implements IManager {
	private static final Log logger = LogFactory.getLog(PolizaManager.class);
	
	private IPolizaDao polizaDao;
	private ISeleccionPolizaDao seleccionPolizaDao;
	private IClaseDao claseDao;
	private IClaseDetalleDao claseDetalleDao;
	private IFechaContratacionDao fechaContratacionDao;
	private ICPMTipoCapitalDAO cpmTipoCapitalDao;
	
	private CalculoPrecioProduccionManager calculoPrecioProduccionManager;
	private SeleccionPolizaManager seleccionPolizaManager;
	private IDatosAvalDao datosAvalDao;
	private WebServicesManager webServicesManager;
	private ISeleccionComparativaSWDao seleccionComparativaSWDao;


	public Poliza getPoliza(Long id) {
		Poliza poliza = (Poliza) this.polizaDao.getObject(Poliza.class, id);
		return poliza;
	}
	
	public void guardarDCFraccAgroseguro (DistribucionCoste2015 dc) {
		try {
			polizaDao.saveOrUpdate(dc);
		} catch (Exception e) {
			logger.error("Error al actualizar la dc", e);
		}
	}
	
	public Poliza getPolizaByReferencia(String refPoliza,Character tipoRefPoliza) throws BusinessException{
		try{
			
			Poliza poliza = (Poliza)polizaDao.getPolizaByReferencia(refPoliza,tipoRefPoliza);
			return poliza;
			
		}catch(DAOException daoe){
			throw new BusinessException("Error durante el acceso a la base de datos",daoe); 
		}
	}
			
	/**
	 * Metodo para generar el fichero XML de envio a Agroseguro con los datos de la poliza y guardarlo
	 * en el campo 'XMLACUSECONTRATACION' de la tabla de TB_POLIZAS
	 * @param poliza Poliza a generar
	 * @throws DAOException 
	 * @throws BusinessException 
	 * @throws ValidacionPolizaException 
	 * @throws Exception 
	 */
	public void grabarXmlDefinitivo(Poliza poliza) throws DAOException, ValidacionPolizaException, BusinessException{
		
		ComparativaPoliza cp = poliza.getComparativaPolizas().iterator().next();
		DatosAval dv=null;
		if (poliza.getLinea().getCodplan().compareTo(new BigDecimal("2015"))!=-1) {
			for (DistribucionCoste2015 dc : poliza.getDistribucionCoste2015s()) {
				if(null!=dc.getPeriodoFracc()) {
					dv=this.GetDatosAval(poliza.getIdpoliza());
					if(null!=dv) {
						poliza.setDatosAval(dv);
					}
				}
			}
		}
		
		// Calculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + cp.getId().getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null, poliza.getIdpoliza(), cp.getId().getCodmodulo());			
		
		Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager.getComsPctCalculadoComp(cp.getId().getIdComparativa());
		
		String envio = WSUtils.generateXMLPoliza(poliza, cp, Constants.WS_VALIDACION, polizaDao, listaCPM, poliza.getUsuario(), true, comsPctCalculado);				
		
		this.polizaDao.actualizaXmlPoliza(poliza.getIdpoliza(), envio);
		this.polizaDao.evictPoliza(poliza);				
	}
	
	/**
	  *Metodo para generar el fichero XML de envio a Agroseguro con los datos de la poliza y guardarlo
	 * en el campo 'XMLACUSECONTRATACION' de la tabla de TB_POLIZAS
	 * @param poliza
	 */
	public void grabarXmlDefinitivoCpl(Poliza poliza, String realPath) throws DAOException, ValidacionPolizaException, BusinessException {
		
		// Calculo de CPM permitidos
		logger.debug("Se cargan los CPM permitidos para la poliza - idPoliza: " + poliza.getIdpoliza() + ", codModulo: " + poliza.getCodmodulo());
		List<BigDecimal> listaCPM = cpmTipoCapitalDao.getCPMDePoliza(null,poliza.getIdpoliza(), poliza.getCodmodulo());
		
		Boolean saeca = this.esFinanciadaSaeca(poliza.getLinea().getLineaseguroid(), poliza.getColectivo().getSubentidadMediadora());
		
		/* Pet.57626 ** MODIF TAM (28.05.2020) ** Inicio */ 
		// comprobamos que la poliza principal tiene comparativas y si tiene cojo una 
		//para enviarla por parámetro
		List<ComparativaPoliza> listComparativasPoliza = poliza
				.getComparativaPolizas() != null ? Arrays.asList(poliza
				.getPolizaPpal().getComparativaPolizas()
				.toArray(new ComparativaPoliza[] {}))
				: new ArrayList<ComparativaPoliza>();
		ComparativaPoliza cp = new ComparativaPoliza();
		
		if(listComparativasPoliza.size()>0)
			cp = listComparativasPoliza.get(0);
		/* Pet.57626 ** MODIF TAM (28.05.2020) ** Fin */
		
		ModuloPoliza mp = null;
		Set<ModuloPoliza> mpLst = poliza.getModuloPolizas();
		for (ModuloPoliza aux : mpLst) {
			mp = aux;
			break;
		}
		
		Map<Character, ComsPctCalculado> comsPctCalculado = this.webServicesManager.getComsPctCalculadoComp(mp.getId().getNumComparativa());
		
		String envio = WSUtils.generateXMLPolizaCpl(poliza, poliza.getPolizaPpal(), cp, Constants.WS_VALIDACION, polizaDao, listaCPM, poliza.getUsuario(), saeca, true, comsPctCalculado, realPath);
		
		this.polizaDao.actualizaXmlPoliza(poliza.getIdpoliza(), envio);
		this.polizaDao.evictPoliza(poliza);
	}

	public HashMap<String, Object> dameListaModulos(Poliza poliza, boolean isFiltradoPorFecha) {
		logger.debug("init - [metodo] dameListaModulos");
		
		HashMap<String, Object> listaModulos = new HashMap<String, Object>();
		List<Modulo> modulos = null;
		HashMap<String, String> isInPerioContratMods = new HashMap<String, String>();
       // estara en el mismo orden que la lista modulos

		//filtrado de modulos por lineaseguroid en la tabla clase
		List<String> lstModulos = new ArrayList<String>();
		lstModulos= claseDao.dameListaModulosClase(poliza.getLinea().getLineaseguroid(), poliza.getClase());
		//fin filtrado de modulos por lineaseguroid en la tabla clase

		ModuloFiltro filtro = new ModuloFiltro(poliza.getLinea().getLineaseguroid(), lstModulos);
		filtro.setPpalComplementario(Constants.MODULO_POLIZA_PRINCIPAL);
		
		modulos = polizaDao.getObjects(filtro);
		
		Collections.sort(modulos, new ModuloComparator());
		
		listaModulos.put("listaModulos", modulos);
		
		for(int i=0; i < modulos.size(); i++){ 
			// obtengo los cultivos por clase seleccionada 
			List<BigDecimal> listCultivo = polizaDao.getCultivosClase(poliza.getClase(), poliza.getLinea().getLineaseguroid()); 
			if(listCultivo.size() > 0 && poliza.getClase()!= null) { 
				try { 
					Boolean result = fechaContratacionDao.validarPorModulo(listCultivo, modulos.get(i).getId().getCodmodulo() ,poliza.getLinea().getLineaseguroid()); 
					if(result) { 
						isInPerioContratMods.put(modulos.get(i).getId().getCodmodulo(), ""); 
					} else { 
						isInPerioContratMods.put(modulos.get(i).getId().getCodmodulo(), "&nbsp;(fuera del periodo de contrataci&oacute;n)"); 
					} 
				} catch(Exception ex) { 
					logger.error("fechaContratacionDao.validarPorModulo - " + ex.getMessage()); 
				} 
			} 
		}
		
		listaModulos.put("isInPerioContratMods", isInPerioContratMods);
		
		logger.debug("end - [metodo] dameListaModulos");
		return listaModulos;
	}

	/**
	 * Dados los datos de una poliza, recuperamos los riesgos cubiertos.
	 * @param lineaseguroid
	 * @param codmodulo
	 * @param codriesgocubierto
	 * @return
	 */
	public List<RiesgoCubiertoModulo> getRiesgoCubiertoModulo(final Long lineaseguroid, final String codmodulo, final BigDecimal codriesgocubierto){
		
		RiesgoCubiertoModuloFiltro riesgoCubiertoFiltro = new RiesgoCubiertoModuloFiltro(lineaseguroid, codmodulo, codriesgocubierto);
		List<RiesgoCubiertoModulo> lista = polizaDao.getObjects(riesgoCubiertoFiltro);
		return lista;
	}
	
	public void crearModulo(String codmodulo, Poliza poliza, int renovable) {
		
		ModuloPoliza moduloP = new ModuloPoliza();
		try {
			ModuloPolizaId moduloID = new ModuloPolizaId();
			moduloID.setCodmodulo(codmodulo); 							
			moduloID.setLineaseguroid(poliza.getLinea().getLineaseguroid());
			moduloID.setIdpoliza(poliza.getIdpoliza());
			moduloID.setNumComparativa(seleccionComparativaSWDao.getSecuenciaComparativa());
			moduloP.setId(moduloID);
			moduloP.setPoliza(poliza);
			moduloP.setRenovable(renovable);
	
			poliza.getModuloPolizas().add(moduloP);
		
		
			polizaDao.saveOrUpdate(moduloP);
		} catch (Exception e) {
			logger.error("Se ha producido un error al guardar el modulo de la poliza",e);
		}
		
	}
	
	public boolean existeComparativa(ComparativaPolizaId id){
		boolean existe = false;
		Object comparativa = polizaDao.getObject(ComparativaPoliza.class, id);
		if(comparativa != null){
			existe = true;
		}
		return existe;
	}
	
	
	public void guardaComparativasSeleccionadas(ComparativaPoliza compP) {
		try {
			polizaDao.saveOrUpdate(compP);
		} catch (Exception e) {
			logger.error("Se ha producido un error al guardar las comparativas seleccionadas para la poliza",e);
		}
	}

	public void borraComparativa(Poliza poliza) throws DAOException {
		
		if (poliza.getComparativaPolizas() != null) {
			try {
				this.polizaDao.deleteAll(poliza.getComparativaPolizas());
				polizaDao.evict(poliza);
			} catch (DAOException dao) {
				logger.error("Se ha producido un error al borrar la comparativa de la poliza",dao);
				throw dao;
			} 
		}
	}
	
	public final void savePoliza(final Poliza polizaBean) {
		
		try {
			polizaDao.saveOrUpdate(polizaBean);
		} 
		catch (DAOException e) {
			logger.debug("error al guardar la poliza",e);
		}
	}
	
	/**
	 * Funcion para configurar el numero maximo de comparativas seleccionables.
	 * @return
	 * @throws BusinessException
	 */
	public int numeroMaxComparativas () throws BusinessException{
		
		int numeroMaxComparativas = 0;		
		try {
			numeroMaxComparativas = polizaDao.getNumeroMaxComparativas();
		} catch (DAOException dao) {			
			throw new BusinessException ("Se ha producido un error buscando los siniestros de una poliza", dao);
		}		
		
		return numeroMaxComparativas;
	}
	
	/**
	 * Comprueba que el asegurado no pertenece a la tabla de asegurados no
	 * subvencionables.
	 * 
	 * @param nifasegurado.
	 * @return boolean.
	 */
	public boolean isUsuarioAutorizado(Long lineaseguroid, String nifasegurado) {
		logger.debug("init - [metodo] isUsuarioAutorizado");
		
		MedidaFiltro filtro = new MedidaFiltro(lineaseguroid,nifasegurado);
		//if(true) return true;
		List<Medida> medidaAseg = polizaDao.getObjects(filtro);
		
		if (medidaAseg != null && medidaAseg.size() > 0) {
			for(Medida medida : medidaAseg){
				Double pctbonifrecargo = medida.getPctbonifrecargo().doubleValue();
				if(medida.getTipomedidaclub().toString().equals("1") && pctbonifrecargo >= 15){
					return true;
				} else {
					return false;
				}
			}
		} 
		
		return false;
	}
		
	public void deleteComparativaPoliza(List lstCmpEliminar) throws DAOException {
		polizaDao.deleteAll(lstCmpEliminar);
	}

	public void deleteCapAsegRelModulo(List lstCapAsegEliminar) throws DAOException {
		polizaDao.deleteAll(lstCapAsegEliminar);
	}
	
	public void deleteModuloPoliza(List lstModEliminar) throws DAOException {
		polizaDao.deleteAll(lstModEliminar);
	}
	
	public void deleteDistribucionCostes(List lstDistCostes)throws DAOException {
		polizaDao.deleteAll(lstDistCostes);			
	}
	
	public void actualizaImporte(Poliza poliza) throws Exception {
		try {
			polizaDao.actualizaImporte(poliza);
		}catch (Exception e) {
			logger.error("Se ha producido un error en actualizaImporte", e);
			throw e;
		}
	}
	
	
	/**
	 * Metodo que devuelve true, si la poliza es valida para enviarla a agroseguro.
	 * una poliza es valida para contratar, si cumple los parametrizacion de su lÃ­nea, numMaxPolizas y conceptoMaxPoliza
	 * @param poliza
	 * @return
	 */
	public boolean validarParametrizacionPoliza(Poliza poliza, Clase clase) {
		
		int contador = 0;
		String valorConcepto = "";
		boolean valorConceptoEncontrado = false;
		BigDecimal maxPolizas = null;
		BigDecimal conceptoMaxPoliza = null;
		
		try {
			
			maxPolizas = clase.getMaxpolizas();
			if(poliza.getLinea().getDiccionarioDatos() != null){
				conceptoMaxPoliza =  poliza.getLinea().getDiccionarioDatos().getCodconcepto();
			}
			
			if(conceptoMaxPoliza == null){
				//el numero maximo de polizas viene dado por maxpolizas
				for(Poliza pol:poliza.getAsegurado().getPolizas()){
					if (!pol.getIdpoliza().equals(poliza.getIdpoliza())){
						if(pol.getLinea().equals(poliza.getLinea()) && pol.getClase().equals(poliza.getClase()) && pol.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
							if(Constants.ESTADO_POLIZA_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
							   Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR.equals(pol.getEstadoPoliza().getIdestado()) || 								   
							   Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
							   Constants.ESTADO_POLIZA_ENVIADA_ERRONEA.equals(pol.getEstadoPoliza().getIdestado()) ){
								contador++;
							}
						}
					}						
				}
			}else{
				//Recogemos el valor del conceptoMaxPoliza de la poliza 
				for(Parcela parcela: poliza.getParcelas()){
					for(CapitalAsegurado cap: parcela.getCapitalAsegurados()){
						for(DatoVariableParcela datvar:cap.getDatoVariableParcelas()){
							if(datvar.getDiccionarioDatos().getCodconcepto().equals(conceptoMaxPoliza)){
								valorConcepto = datvar.getValor();
								break;
							}
						}
					}
				}
				for(Poliza pol:poliza.getAsegurado().getPolizas()){
					if (!pol.getIdpoliza().equals(poliza.getIdpoliza())){
						if(pol.getLinea().equals(poliza.getLinea()) && pol.getClase().equals(poliza.getClase()) && pol.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
							if(Constants.ESTADO_POLIZA_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
							   Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR.equals(pol.getEstadoPoliza().getIdestado()) || 								   
							   Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
							   Constants.ESTADO_POLIZA_ENVIADA_ERRONEA.equals(pol.getEstadoPoliza().getIdestado()) ){
								for(Parcela par: pol.getParcelas()){
									for(CapitalAsegurado cap: par.getCapitalAsegurados()){
										for(DatoVariableParcela datvar:cap.getDatoVariableParcelas()){
											if(datvar.getDiccionarioDatos().getCodconcepto().equals(conceptoMaxPoliza) && 
											   valorConcepto.equals(datvar.getValor())){
												contador++;
												valorConceptoEncontrado = true;
												break;
											}
										}
										if(valorConceptoEncontrado)
											break;
									}
									if(valorConceptoEncontrado)
										break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Se ha producido un error al validar el nÃºmero maximo de polizas contratables",e);
		}
		
		if(maxPolizas != null && contador < maxPolizas.intValue()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Metodo que calcula las comparativas seleccionadas no elegibles
	 * @param comparativaModulo
	 * @return
	 * @throws Exception
	 */
	public String seleccionaComparativas(LinkedHashMap<String, Object> comparativaModulo) /*throws Exception*/ {
		String seleccionados = "";
		List<List<VistaComparativas>> combinacionesElementosComparativa = null;
		int numFila = 1;
		//try {
			
			if(comparativaModulo.size() > 0){				
				for(String clave:comparativaModulo.keySet()){
					combinacionesElementosComparativa = (List<List<VistaComparativas>>) comparativaModulo.get(clave);
					for(List<VistaComparativas> listaVistas:combinacionesElementosComparativa){
						//numFila = 1;
						for(VistaComparativas vista : listaVistas){
							seleccionados+= vista.getId().getCodmodulo() + "|" + vista.getId().getFilamodulo() + "|" + 
											vista.getId().getCodconceptoppalmod() + "|" + vista.getId().getCodriesgocubierto() + "|" + 
											vista.getId().getCodconcepto() + "|"+ vista.getId().getCodvalor() + "|" + numFila + "|"+ vista.getId().getDesvalor()+  ";";
							for(VistaComparativas vinc:vista.getFilasVinculadas()){
								seleccionados+= vinc.getId().getCodmodulo() + "|" + vinc.getId().getFilamodulo() + "|" + 
												vinc.getId().getCodconceptoppalmod() + "|" + vinc.getId().getCodriesgocubierto() + "|" + 
												vinc.getId().getCodconcepto() + "|"+ vinc.getId().getCodvalor() + "|" + numFila + "|"+ vinc.getId().getDesvalor()+  ";";
								
							}
						}
						//numFila++;
					}
				}				
			}
			
		/*} catch (Exception e) {
			throw new Exception ("Se ha producido un error al calcular las comparativas seleccionadas",e);
		}*/
		return seleccionados;
	}
	
	/**
	 * Metodo que comprueba si alemnos existe almenos un modulo con mas de una comparativa seleccionable
	 * @param comparativaModulo
	 * @return
	 * @throws Exception
	 */
	public boolean hayComparativasElegiblesModulo(LinkedHashMap<String, Object> comparativaModulo) /*throws Exception*/{
		boolean hayComprativasElegibles = false;
		List<List<VistaComparativas>> combinacionesElementosComparativa = null;
		//try {
			if(comparativaModulo.size() > 0){
				for(String clave : comparativaModulo.keySet()){
	 				combinacionesElementosComparativa = (List<List<VistaComparativas>>) comparativaModulo.get(clave);
	 				if(combinacionesElementosComparativa.size() > 1){
	 					hayComprativasElegibles = true;
	 				}
				}
			}
		/*} catch (Exception e) {
			throw new Exception ("Se ha producido un error al calcular las comparativas seleccionadas",e);
		}*/
		return hayComprativasElegibles;
	}
	
	/**
	 * Metodo que comprueba si el numero de comparativas no supera el maximo de las permitidas, cuando no pasamos por las jsp de seleccion de comparativas
	 * @param comparativaModulo
	 * @param numMaxComp
	 * @return
	 * @throws Exception
	 */
	public boolean isNumComparativasNoElegidasOK(LinkedHashMap<String, Object> comparativaModulo,int numMaxComp) throws Exception {
		int contador = 0;
		boolean resultado = false;
		try {
			if(comparativaModulo.size() > 0){				
				contador = comparativaModulo.size();
			}
			
			if(contador <= numMaxComp)
				resultado = true;
			else
				resultado = false;
			
		} catch (Exception e) {
			throw new Exception ("Se ha producido un error al comprobar si el numero de comparativas es menor que el maximo permitido",e);
		}
		return resultado;
	}
	
	/**
	 * Metodo que actualiza los Modulos de la poliza en la BBDD y devuelve un List con los modulos seleccionados
	 * @param poliza
	 * @param modSelec
	 * @return
	 * @throws BusinessException 
	 */
	public List<String> updateModulosPoliza(Poliza poliza, String[] modSelec, String[] modRenovablesSelec)
			throws BusinessException {
		try {
			List<ComparativaPoliza> comparativas = this.polizaDao.getLstCompPolizasByIdPol(poliza.getIdpoliza());
			// BORRAMOS LOS MODULOS PREVIAMENTE GUARDADOS Y NO SELECCIONADOS
			// ACTUALIZAMOS EL INDICADOR DE RENOVABLE EN LOS SELECCIONADOS Y PREVIAMENTE
			// GUARDADOS
			Set<ModuloPoliza> modulosPoliza = poliza.getModuloPolizas();
			List<String> modulosGuardados = new ArrayList<String>(modulosPoliza.size());
			for (ModuloPoliza mod : modulosPoliza) {
				String codModulo = mod.getId().getCodmodulo();
				if (ArrayUtils.contains(modSelec, codModulo)) {
					mod.setRenovable(isModRenovable(codModulo, modRenovablesSelec));
					this.polizaDao.saveOrUpdate(mod);
					modulosGuardados.add(codModulo);
				} else {
					for (ComparativaPoliza comp : comparativas) {
						if (comp.getId().getIdComparativa().equals(mod.getId().getNumComparativa())) {
							this.polizaDao.removeObject(ComparativaPoliza.class, comp.getId());
						}
					}
					ModuloPolizaCoberturaSW mpc = mod.getModuloPolizaCoberturaSW();
					if (mpc != null) {
							this.polizaDao.removeObject(ModuloPolizaCoberturaSW.class, mpc.getId());
					}
					this.polizaDao.removeObject(ModuloPoliza.class, mod.getId());
				}
			}
			// INTRODUCIMOS LOS NUEVOS MODULOS
			for (int i = 0; i < modSelec.length; i++) {
				String codmodulo = modSelec[i];
				if (!modulosGuardados.contains(codmodulo)) {
					this.crearModulo(codmodulo, poliza, isModRenovable(codmodulo, modRenovablesSelec));
				}
			}
		} catch (DAOException e) {
			logger.error(e);
			throw new BusinessException(e);
		}
		return Arrays.asList(modSelec);
	}
	
	/**
	 * Devuelve un entero indicando si el modulo correspondiente a 'codModulo' se ha seleccionado como renovable en la pantalla
	 * @param codModulo codigo del modulo a tratar
	 * @param modRenovablesSelec Array de String que contiene valores del tipo "codModulo#renovable"
	 * @return 0 - No renovable, 1 - Renovable
	 */
	private int isModRenovable (String codModulo, String[] modRenovablesSelec) {
		
		if (modRenovablesSelec != null) {
			try {
				for (String valor : modRenovablesSelec) {
					String[] modRenov = valor.split("#");
					if (modRenov[0].equals(codModulo)) return Integer.parseInt(modRenov[1]);
				}
			} catch (Exception e) {
				logger.error("Error al comprobar si se ha seleccionado el modulo como renovable", e);
			}
		}
		
		return 0;
	}
	
	public es.agroseguro.acuseRecibo.Error[] getFicheroContenido(BigDecimal idEnvio, String refPoliza, BigDecimal linea, BigDecimal plan) throws BusinessException{
		AcuseReciboDocument acuseRecibo = null;
		es.agroseguro.acuseRecibo.Error[] errores = null;
		Comunicaciones comunicaciones= null;
		
		// Se monta la referencia que luego se comparara
		String referencia =  refPoliza.toString() + "" +  plan.toString()+ "" +linea.toString(); 
		
		try {
			comunicaciones = polizaDao.getComunicaciones(idEnvio);
			if (comunicaciones == null){
				errores = new es.agroseguro.acuseRecibo.Error[0];
				
			} else {
				Clob fichero = comunicaciones.getFicheroContenido();
				if (fichero == null) {
					errores = new es.agroseguro.acuseRecibo.Error[0];
				} else {
					String xml = WSUtils.convertClob2String(fichero); //Recuperamos el Clob y lo convertimos en String
					//Se comprueba si existe cabecera, sino se inserta al principio
					if (xml.indexOf("<?xml version=\"1.0") == -1)
					{ String cabecera="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
					  xml = cabecera + xml;	
					}
					
					// Se Reempla 
					String namespace ="http://www.agroseguro.es/AcuseRecibo";
					String reemplazar1="<AcuseRecibo xmlns=\""+namespace+"\"";
					String reemplazar2="</AcuseRecibo>";
					xml = xml.replace("<xml-fragment", reemplazar1)
							.replace("</xml-fragment>", reemplazar2)
							.replace("xmlns:acus=\"http://www.agroseguro.es/AcuseRecibo\"", "")
							.replace("acus:", "");
					try {
						acuseRecibo = AcuseReciboDocument.Factory.parse(new StringReader(xml)); // String parseado a AcuseReciboDocument 
					} catch (Exception e) {
						logger.error("Se ha producido un error al recuperar el XML de Acuse de Recibo", e);
						throw new BusinessException("Error al convertir el XML a XML Bean", e);
					}
					
					if (acuseRecibo != null) { 
						AcuseRecibo ac = acuseRecibo.getAcuseRecibo();
						ArrayList<es.agroseguro.acuseRecibo.Error> ArrayE  =  new ArrayList<es.agroseguro.acuseRecibo.Error>();
											
						// Recorremos Acuse de Recibo para hacer Array con errores
						for (int i = 0; i < ac.getDocumentoArray().length; i++) 
						{
								Documento documentoRecibido = ac.getDocumentoArray(i);
								int j = 0;
								
								// Si el documento del acuse de recibo tiene estado 2 (rechazado) y coincide  "idPoliza + linea + plan"
								if (documentoRecibido.getEstado()== Constants.ACUSE_RECIBO_ESTADO_RECHAZADO && documentoRecibido.getId().equals(referencia)) {
									// Formamos la lista de Errores
									while (j < documentoRecibido.getErrorArray().length){
										try {
											ArrayE.add((es.agroseguro.acuseRecibo.Error) documentoRecibido.getErrorArray(j));  
											j = j+1;
											
										} catch (Exception ex) {
											throw new BusinessException("Se ha producido un error al visualizar Acuse de Recibo ",ex);
										}
									}
								}
						}
						errores = new es.agroseguro.acuseRecibo.Error[ArrayE.size()];
						for (int i = 0; i < ArrayE.size(); i++) {
							errores[i] = ArrayE.get(i);
						}
					}else {
						errores = new es.agroseguro.acuseRecibo.Error[0];
					}
				}
			}
			
		} catch (DAOException dao) {
			throw new BusinessException ("Se ha producido un error al recuperar el fichero_contenido de una Reduccion de Capital", dao);
		}
		
		return errores;
	}

	/**
	 * Metodo para comprobar que hay precio y produccion para todas las parcelas y, en caso de no haberlo, 
	 * lo calculamos: llamaremos a las funciones de calculo de precio y produccion. En caso de que nos de
	 * produccion 0 => cogemos la produccion de otro modulo para el mismo capital asegurado y si tampoco 
	 * lo tenemos => ponemos un 0
	 * @param poliza Poliza a comprobar
	 * @throws Throwable 
	 */
	public void checkPrecioProduccion(Long idpoliza, String realPath, Usuario usuario) throws Throwable {
		boolean hayCambios = false;
		
		Poliza poliza = this.getPoliza(idpoliza);
		if(poliza.getParcelas() != null && poliza.getParcelas().size() > 0) {
			
			List<String> codsModuloPoliza = new ArrayList<String>();
			
			// Recargo los modulos seleccionados (en base a las comparativas)
			Set<ModuloPoliza> modPolizas = poliza.getModuloPolizas();
			for (ModuloPoliza comp: modPolizas){
				if (!codsModuloPoliza.contains(comp.getId().getCodmodulo()))
					codsModuloPoliza.add(comp.getId().getCodmodulo());
			}
			
			outer:for (Parcela parcela : poliza.getParcelas()) {
				
				for (CapitalAsegurado capAseg : parcela.getCapitalAsegurados()) {					
					
					if (capAseg.getCapAsegRelModulos().size() > 0) {
						// Si hay datos relacionados entre capitales asegurados y modulos, comprobamos que
						// para cada modulo, el capital asegurado tiene precio y produccion
						
						// Compruebo que para cada capital asegurado hay precio y produccion para todos los modulos
						for (CapAsegRelModulo carm : capAseg.getCapAsegRelModulos()){
							if (codsModuloPoliza.size() > 0){
								for (String codMod : codsModuloPoliza){
									if (!codMod.equals(carm.getCodmodulo()) 
											|| carm.getPrecio() == null || carm.getProduccion() != null){
										hayCambios = true;
										break;
									}
								}
								if (hayCambios){
									break;
								}
							}
						}
					} else {
						// Si NO hay datos relacionados entre capitales asegurados y modulos hay que calcular precio 
						// y produccion para cada uno de ellos.
						hayCambios = true;
						break outer;
					}
				}
			}
			if (hayCambios) {
				
				logger.debug("Se debe recalcular predio/produccion.");
				boolean recalcularRendimientoConSW = calculoPrecioProduccionManager.calcularRendimientoProdConSW();
				
				if(recalcularRendimientoConSW) {
					Map<String, ProduccionVO> mapaRendimientosProd = calculoPrecioProduccionManager.calcularRendimientosPolizaWS(idpoliza, null, realPath, usuario.getCodusuario(), 0);				
			    	seleccionPolizaManager.recalculoPrecioProduccion(poliza.getParcelas(), codsModuloPoliza, recalcularRendimientoConSW, mapaRendimientosProd);			    	
				} else {
					//calcular precio y produccion				
					this.seleccionPolizaDao.reCalculoPrecioProduccion(poliza.getParcelas(), codsModuloPoliza);
				}
				
				//guardo los cambios
				for (Parcela par : poliza.getParcelas()){
					this.seleccionPolizaDao.saveOrUpdate(par);
				}
			}
		}
	}	
		
	public HashMap<String, Object> getMapaCompNoElegidas(Poliza poliza){
		HashMap<String, Object> mapa = new HashMap<String, Object>();
		List<ComparativaPoliza> lstCompPolizas = polizaDao.getLstCompPolizas(poliza.getIdpoliza(), poliza.getCodmodulo());
		List<MedidaFranquicia> lstMedidaFranquicia = polizaDao.getLstMedidasFranquicia(poliza.getAsegurado().getNifcif(),poliza.getLinea().getLineaseguroid(),poliza.getCodmodulo(), 'N');
		for (ComparativaPoliza c: lstCompPolizas){
			for (MedidaFranquicia m: lstMedidaFranquicia){
				String codconcepto= c.getId().getCodconceptoppalmod().toString();
				String cod2= m.getConceptoPpalModulo().getCodconceptoppalmod().toString();
				String riesgo= c.getRiesgoCubierto().getId().getCodriesgocubierto().toString();
				String riesgo2= m.getRiesgoCubierto().getId().getCodriesgocubierto().toString();
				String desRiesgo= c.getRiesgoCubierto().getDesriesgocubierto();
				if (codconcepto.equals(cod2) && (riesgo.equals(riesgo2))){
					String descPctFranquiciaEleg= polizaDao.getDescPctFranquiciaEleg(m.getPctfranquicia());
					mapa.put("descPct", descPctFranquiciaEleg);
					mapa.put("desRiesgo",desRiesgo);
				}
			}
		}
		logger.debug("end - [metodo] getMapaCompNoElegidas");
		return mapa;
	}
		
	public List<Poliza> getListPolizasAseg(long idAseg,Poliza poliza, String OpTipoPol){
		List<Poliza> listPolizasAseg = null;
		listPolizasAseg=polizaDao.getListPolizasAseg(idAseg,poliza, OpTipoPol);
		return listPolizasAseg;
	}
	
	public Asegurado getAsegurado(String idAseg){
		Asegurado aseg = null;
		aseg = (Asegurado)polizaDao.getObject(Asegurado.class, Long.parseLong(idAseg));
		return aseg;
	}
	
	public boolean checkModulosPoliza(Poliza poliza){
		boolean hayModulos=false;
		hayModulos=polizaDao.checkModulosPoliza(poliza);
		return hayModulos;
	}
	
	public String getTotalProdComparativa(String modulo,Poliza poliza){
		return polizaDao.getTotalProdComparativa(modulo,poliza);
	}
		
	/**
	 * Metodo que recibe una poliza principal o Complementaria para checkear la distribucionCoste
	 * @param poliza
	 * @return
	 */
	public boolean checkDistribucionCosteSubv(Poliza poliza){
		boolean resultado= false;
		if (poliza.isPlanMayorIgual2015()) {
			if (poliza.getDistribucionCoste2015s() != null && !poliza.getDistribucionCoste2015s().isEmpty()) {
				return true;			
			}
		}else{
			if (poliza.getDistribucionCostes() != null && !poliza.getDistribucionCostes().isEmpty()) {
				return true;			
			}
		}
		return resultado;	
	}
	
	/**
	 * Metodo que recibe una poliza principal o Complementaria y checkea que existen datos de pago y que el importe no es 0.
	 * @param poliza
	 * @return resultado
	 */
	public int checkDatosDePago(Poliza p){
		int resultado = 0;
		if (p.getPagoPolizas() != null && p.getPagoPolizas().size() >0){
			Set<PagoPoliza> pagoPolizas = p.getPagoPolizas();
			Iterator<PagoPoliza> pp = pagoPolizas.iterator();
			while(pp.hasNext()) {
				PagoPoliza pagoPol = pp.next();
				if (Constants.CARGO_EN_CUENTA.equals(pagoPol.getTipoPago())) {
					if (pagoPol.getImporte() == null || pagoPol.getImporte().compareTo(BigDecimal.ZERO) == 0){ // importe errï¿½neo
						if (pagoPol.getImporte() != null)
							logger.debug("[metodo] checkDatosDePago: Error, el importe de los datos de pago de la pï¿½liza es: " + pagoPol.getImporte());
						else
							logger.debug("[metodo] checkDatosDePago: Error, el importe de los datos de pago es nulo"); 
	
						return 2;
					}
					if (pagoPol.getPctprimerpago() == null || pagoPol.getPctprimerpago().compareTo(BigDecimal.ZERO) == 0
							|| pagoPol.getPctprimerpago().compareTo(new BigDecimal(100)) == 1){ // pctprimerpago errï¿½neo
						if (pagoPol.getPctprimerpago() != null)
							logger.debug("[metodo] checkDatosDePago: Error, el pctprimerpago de los datos de pago es: " + pagoPol.getPctprimerpago());
						else
							logger.debug("[metodo] checkDatosDePago: Error, el pctprimerpago de los datos de pago es nulo"); 						
						return 3;
					}
					if (pagoPol.getCccbanco() == null || pagoPol.getCccbanco().length() != 20){ // ccc errï¿½neo
						if (pagoPol.getCccbanco() != null)
							logger.debug("[metodo] checkDatosDePago: Error, cccbanco de los datos de pago no tiene 20 dï¿½gitos: " + pagoPol.getCccbanco());						
						else
							logger.debug("[metodo] checkDatosDePago: Error, cccbanco de los datos de pago es nulo"); 						
						return 4;
					}
				} else if (Constants.PAGO_MANUAL.equals(pagoPol.getTipoPago())) {
					if (pagoPol.getBanco() == null || pagoPol.getFecha() == null
							|| pagoPol.getImporte() == null
							|| pagoPol.getIban() == null
							|| "".equals(pagoPol.getIban())
							//|| pagoPol.getCccbanco() == null
							//|| "".equals(pagoPol.getCccbanco())
							) {
						logger.debug("[metodo] checkDatosDePago: Error, pï¿½liza con pago manual sin datos de pago");
						return 1;
					}
				}else{
					if (pagoPol.getCccbanco() == null || "".equals(pagoPol.getCccbanco()) ||
							pagoPol.getIban() == null || "".equals(pagoPol.getIban())){
						logger.debug("[metodo] checkDatosDePago: Error, pï¿½liza con domiciliaciï¿½n agroseguro sin datos de pago");
						return 1;
					}
				}
			}				
			
		}else{ // no tiene datos de pago
			logger.debug("[metodo] checkDatosDePago: Error, pï¿½liza sin datos de pago");		
			return 1;
		}
		return resultado;	
	}
	
	/**
	 * Metodo para el caso de pasar a definitiva una poliza complementaria
	 * @param polizaCpl = para checkear la distribucionCosteSubvencion
	 * @param poliza = para comprobar si tipo de subvencion con codigo 50 esta checkeado
	 * @return
	 */
	public boolean checkDistribucionCosteSubvCpl(Poliza polizaCpl,Poliza polizaPpl){
		boolean resultado= false;
		if (polizaCpl.isPlanMayorIgual2015()) {
			if (polizaCpl.getDistribucionCoste2015s() != null) {
				Set<DistribucionCoste2015> distribucionCostes2015s = polizaCpl.getDistribucionCoste2015s();
				Iterator<DistribucionCoste2015> it = distribucionCostes2015s.iterator();
				while(it.hasNext()) {
					DistribucionCoste2015 dc = it.next();
					if (dc.getDistCosteSubvencion2015s() !=null){
						if (!dc.getDistCosteSubvencion2015s().isEmpty()){
							resultado= true;
							break;
						}
					}
				}
			}
		}else{
			if (polizaCpl.getDistribucionCostes() != null) {
				Set<DistribucionCoste> distribucionCostes = polizaCpl.getDistribucionCostes();
				Iterator<DistribucionCoste> it = distribucionCostes.iterator();
				while(it.hasNext()) {
					DistribucionCoste dc = it.next();
					if (dc.getDistCosteSubvencions() !=null){
						if (!dc.getDistCosteSubvencions().isEmpty()){
							resultado= true;
							break;
						}
					}
				}
			}
		}
		
		
		
		
		if (!resultado){
			for (SubAseguradoENESA subAseguradoENESA : polizaPpl.getSubAseguradoENESAs()){
				SubvencionEnesa subvencionEnesa = subAseguradoENESA.getSubvencionEnesa();
				if (subvencionEnesa.getTipoSubvencionEnesa().getCodtiposubvenesa().equals(new BigDecimal(50))){
					resultado = true;
					break;
				}
			}
		}
		return resultado;	
	}
		
	public List<List<String>> getTotalProdParcelas(Poliza poliza){
		//aqui se guardan las parcelas ordenadas por prov,comarca,termino y cultivo
		List<Parcela> lstParcelas = polizaDao.getlistParcelas(poliza.getIdpoliza());
		//aqui se guarda un mapa con el nombre del modulo y el total de produccion agrupado por prov,com,term y cult de la parcela
		Map<String , BigDecimal> mapTotalesMods= new HashMap<String, BigDecimal>();
		//almacena los datos de cada parcela y el total de produccion por cada modulo
		List<List<String>> lstParcelasPopUp = new ArrayList<List<String>>();
		BigDecimal prodTemp= new BigDecimal(0);
		BigDecimal supTemp= new BigDecimal(0);
		//pintar cabecera
		List<String> datosPar = new ArrayList<String>();
		datosPar.add("Provincia");
		datosPar.add("Comarca");
		datosPar.add("Termino");
		datosPar.add("Cultivo");
		String prov = "";
		String com = "";
		String term = "";
		String cult = "";
		String provNext = "";
		String comNext = "";
		String termNext = "";
		String cultNext = "";
		boolean parcela1 = true;
		
		List<ModuloPoliza> lstModulos = new ArrayList<ModuloPoliza>();
		Set<ModuloPoliza> setModulos= poliza.getModuloPolizas();
		lstModulos.addAll(setModulos);
		Collections.sort(lstModulos, new ModuloPolizaComparator());
		
		for (ModuloPoliza mod: lstModulos){
			datosPar.add("Total Prod. Mod. " + mod.getId().getCodmodulo().toString());
		}
		datosPar.add("Total Superficie");
		lstParcelasPopUp.add(datosPar);
		if (lstParcelas != null){ 
			for (Parcela par: lstParcelas){
				if (par.getTermino() !=null){
					provNext = par.getTermino().getId().getCodprovincia().toString();
					comNext = par.getTermino().getId().getCodcomarca().toString();
					termNext = par.getTermino().getId().getCodtermino().toString();
				}else{
					provNext = "";
					comNext = "";
					termNext = "";
				}
				if(par.getCodcultivo() !=null) 
					cultNext = par.getCodcultivo().toString();
				else
					cultNext = "";
				if (prov.equals(provNext) && com.equals(comNext) && term.equals(termNext) && cult.equals(cultNext)){
					prov = provNext;
					com = comNext;
					term = termNext;
					cult = cultNext;
					
				}else{
					if (!parcela1){
						for (ModuloPoliza mod: lstModulos){
							datosPar.add(mapTotalesMods.get(mod.getId().getCodmodulo()).toString());
						}
						datosPar.add(supTemp.toString());
						lstParcelasPopUp.add(datosPar);
					}
					prodTemp = new BigDecimal(0);
					supTemp = new BigDecimal(0);
					datosPar = new ArrayList<String>();
					datosPar.add(provNext);
					datosPar.add(comNext);
					datosPar.add(termNext);
					datosPar.add(cultNext);
					mapTotalesMods= new HashMap<String, BigDecimal>();
					for (ModuloPoliza mod: lstModulos){
						mapTotalesMods.put(mod.getId().getCodmodulo(), prodTemp);
					}
				}
				
				for (CapitalAsegurado cap: par.getCapitalAsegurados()){
					for (CapAsegRelModulo cRelModulo: cap.getCapAsegRelModulos()){
						prodTemp = mapTotalesMods.get(cRelModulo.getCodmodulo());
						if (cRelModulo.getProduccion() != null){
							prodTemp = prodTemp.add(cRelModulo.getProduccion());
							mapTotalesMods.put(cRelModulo.getCodmodulo(), prodTemp);
						}
					}
					if (cap.getSuperficie()!=null && cap.getTipoCapital().getCodtipocapital().compareTo(new BigDecimal(100)) < 0){
						supTemp = supTemp.add(cap.getSuperficie());
						
					}
				}
				prov = provNext;
				com = comNext;
				term = termNext;
				cult = cultNext;
				parcela1 = false;
			}
		}
		for (ModuloPoliza mod: lstModulos){
			if (lstParcelas != null && lstParcelas.size() >0)
				datosPar.add(mapTotalesMods.get(mod.getId().getCodmodulo()).toString());
		}
		datosPar.add(supTemp.toString());
		lstParcelasPopUp.add(datosPar);
		return lstParcelasPopUp;
	}

	public List<BigDecimal> getCodConceptoMod(Long linea, String codmodulo) {
		List<BigDecimal>  listCod = null;
		listCod = polizaDao.getCodConceptoMod(linea,codmodulo);
		return listCod;
	}
		
	//ASF - Borramos las polizas complementarias a partir del id de la poliza principal.
	public void borrarPolizasComplementariasByPpal (Long idpoliza){
		List<Poliza> lstPolizasCpl= null;
		lstPolizasCpl = polizaDao.dameListaPolizasCplByPpl(idpoliza);
		if (lstPolizasCpl!=null){
			
			try {
				polizaDao.deleteAll(lstPolizasCpl);
			} catch (DAOException e) {
				logger.error("Error al obtener las polizas complementarias de la poliza " + idpoliza, e);
			}
			logger.debug("end - [metodo] borrarPolizasComplementariasByPpal");
		}
	}
	
	/* Mejora 96: Angel 26/01/2012 - Funcion que calcula el total de superfice en una poliza
	 * @param poliza
	 * @return supTemp
	 */
	public BigDecimal getTotalSuperficie(Poliza poliza){
		List<Parcela> lstParcelas = polizaDao.getlistParcelas(poliza.getIdpoliza());
		BigDecimal supTemp= new BigDecimal(0);
		for (Parcela par: lstParcelas){
			for (CapitalAsegurado cap: par.getCapitalAsegurados()){
				if (cap.getSuperficie()!=null){
					supTemp = supTemp.add(cap.getSuperficie());	
				}
			}
		}
		return supTemp;
	}
	
	public List<Poliza> existePolizaPlanLinea(String nifCif, BigDecimal codPlan, BigDecimal codLinea, 
			BigDecimal clase, BigDecimal entidad,boolean situacionAct) throws BusinessException {
		
		List<Poliza> polizas = new ArrayList<Poliza>();
		BigDecimal[] planAnterior;
		
		if (situacionAct) {
			planAnterior = new BigDecimal[1];
			planAnterior[0]= codPlan.subtract(new BigDecimal (1));
		}else {
			planAnterior = new BigDecimal[3];
			planAnterior[0]= codPlan.subtract(new BigDecimal (1));
			planAnterior[1]= codPlan.subtract(new BigDecimal (2));
			planAnterior[2]= codPlan.subtract(new BigDecimal (3));
		}
		
		try {
			//Buscamos para la clase en concreto
			polizas = polizaDao.existePolizaPlanLinea(nifCif, planAnterior, codLinea, clase, entidad,situacionAct);
			if (polizas.size() == 0){
				//Si no hay polizas de esa clase, se buscan de cualquier otra clase.
				polizas = polizaDao.existePolizaPlanLinea(nifCif, planAnterior, codLinea, null, entidad,situacionAct);
			}
			return polizas;
			
		} catch (DAOException daoe) {
			throw new BusinessException("Se ha producido un error durante el acceso a la base de datos", daoe);
		}
	}
	
	public void limpiaErroresWs(AcuseRecibo acuseRecibo,String webServiceToCall, Parametro parametro,  BigDecimal codPlan, BigDecimal codLinea, BigDecimal codEntidad) {
		WSUtils.limpiaErroresWs(acuseRecibo, webServiceToCall, parametro, polizaDao, codPlan, codLinea, codEntidad, Constants.PASAR_DEFINITIVA);
	}

	public Usuario getUsuario(String codUsuario) {
		Usuario usuario = null;
		
		usuario = (Usuario) polizaDao.getObject(Usuario.class, codUsuario);			
		
		return usuario;
	}

	public List<ErrorWsAccion> getObjects(ErrorWsFiltro filtro) {
		return polizaDao.getObjects(filtro);
		
	}

	public List<EnvioAgroseguro> getEnvioAgroseguro(Long idPoliza,String codmodulo) throws DAOException {
		return polizaDao.getEnvioAgroseguro(idPoliza,codmodulo);
		
	}

	/** DAA 15/01/2013 borra las subvenciones ENESA y CCAA de los modulos no elegidos de la poliza actual 
	 * 
	 * @param codModulo
	 * @param poliza 
	 * @throws DAOException 
	 */
	public void deleteSubvsModsNoElec(String codModulo, Poliza poliza) {
		try {
			Long idPoliza = poliza.getIdpoliza();
			Long lineaseguroid = poliza.getLinea().getLineaseguroid();
			polizaDao.deleteSubvsEnesaModsNoElec(codModulo, idPoliza, lineaseguroid);
			polizaDao.deleteSubvsCCAAModsNoElec(codModulo, idPoliza, lineaseguroid);
		} catch (DAOException e) {
			logger.error("deleteSubvsModsNoElec. Error al borrar las subvenciones de los modulos no seleccionados", e);
		}
		
	}	
	
	public void AddDatosAval(DatosAval datosAval) throws DAOException {
		try {
			datosAvalDao.Add(datosAval);
		}catch(Exception e) {
			logger.debug("PolizaManager.AddDatosAval - Se ha producido un error aï¿½adiendo los datos de la aval en la base de datos. "+e);
			throw new DAOException("Se ha producido un error aï¿½adiendo los datos de la aval en la base de datos.",e);
		}
		
	}
	
	public void DeleteDatosAval(DatosAval datosAval) throws DAOException {
		try {
			datosAvalDao.delete(datosAval);
		}catch(Exception e) {
			logger.debug("PolizaManager.AddDatosAval - Se ha producido un error eliminando los datos del aval en la base de datos. "+e);
			throw new DAOException("Se ha producido un error eliminando los datos del aval en la base de datos.",e);
		}
		
	}
	
	public DatosAval GetDatosAval(Long idPoliza) throws DAOException {
		DatosAval datosAval=null;
		try {
			datosAval = datosAvalDao.GetDatosAval(idPoliza);
		}catch(Exception e) {
			logger.debug("PolizaManager.AddDatosAval - Se ha producido un error seleccionando los datos del aval en la base de datos. "+e);
			throw new DAOException("Se ha producido un error seleccionando los datos del aval en la base de datos.",e);
		}
		
		return datosAval;
	}
	
	
	public void obtenerMapaCodConceptoVsUbicacion(final Long lineaseguroId,
			Map<Integer, BigDecimal> mapaCodConceptoVsUbicacion,
			Map<Integer, String> mapaConceptoNombre)
			throws BusinessException {

		Integer codConcepto;
		BigDecimal codUbicacion;
		String nomConcepto;
		
		try {
			// SE BUSCAN AQUELLOS CONCEPTOS QUE APLIQUEN AL USO POLIZA (31) Y A
			// LAS UBICACIONES DE EXPLOTACION
			Filter oiFilter = new Filter() {
				@Override
				public Criteria getCriteria(final Session sesion) {
					Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
					criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroId));
					criteria.add(Restrictions.eq("id.coduso", OrganizadorInfoConstants.USO_POLIZA));
					criteria.add(Restrictions.in("id.codubicacion",
									new Object[] {
											OrganizadorInfoConstants.UBICACION_ANIMALES,
											OrganizadorInfoConstants.UBICACION_CAP_ASEG,
											OrganizadorInfoConstants.UBICACION_GRUPO_RAZA,
											OrganizadorInfoConstants.UBICACION_EXPLOTACION }));
					return criteria;
				}
			};

			List<OrganizadorInformacion> oiList = (List<OrganizadorInformacion>) polizaDao.getObjects(oiFilter);
			
			for (OrganizadorInformacion oi : oiList) {
				codConcepto = oi.getId().getCodconcepto().intValue();
				codUbicacion = oi.getId().getCodubicacion();
				mapaCodConceptoVsUbicacion.put(codConcepto, codUbicacion);

				nomConcepto = oi.getDiccionarioDatos().getNomconcepto();
				mapaConceptoNombre.put(codConcepto, nomConcepto);
			}
			
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	public Boolean esFinanciadaSaeca(Long lineaSeguroId, SubentidadMediadora sm){
		ImporteFraccionamiento ifr = webServicesManager.getImporteFraccionamiento(lineaSeguroId, sm);
		Boolean result = false;
		if(ifr != null){
			if (ifr.getTipo() == 0){
				result = true;
			}
		}
		
		return result;	
	};
	
	public DistribucionCoste2015 getDistCosteSaeca(Poliza poliza) {
		logger.debug("INIT getDistCosteSaeca");
		Set<DistribucionCoste2015> dcs = poliza.getDistribucionCoste2015s();
		Iterator<DistribucionCoste2015> dcIt = dcs.iterator();
		DistribucionCoste2015 dc = null;
		while(dcIt.hasNext()){
			logger.debug("dentro del while ...");
			dc = dcIt.next();
			logger.debug("dc : " + dc);
			logger.debug("dc.getCodmodulo : " + dc.getCodmodulo());
			logger.debug("poliza.getCodmodulo()" + poliza.getCodmodulo());
			if(dc.getCodmodulo().equals(poliza.getCodmodulo())){
				logger.debug("dentro del if ...");
				break;
			}
		}
		
		logger.debug("FIN getDistCosteSaeca");
		logger.debug("return dc: " + dc);
		return dc;
	}
	
	public void asignacionModulosGanado(final Poliza poliza) {
		Set<ModuloPoliza> modulos = new HashSet<ModuloPoliza>(0);
		try {
			List<Modulo> listMod = polizaDao.getObjects(Modulo.class, "id.lineaseguroid",
					poliza.getLinea().getLineaseguroid());
			if (null != listMod && listMod.size() > 0) {
				for (Modulo modulo : listMod) {
					if (!modulo.getId().getCodmodulo().equals(Constants.TODOS_MODULOS)) {

						ModuloPolizaId mpId = new ModuloPolizaId(poliza.getIdpoliza(),
								poliza.getLinea().getLineaseguroid(), modulo.getId().getCodmodulo(),
								seleccionComparativaSWDao.getSecuenciaComparativa());
						ModuloPoliza mp = new ModuloPoliza(mpId, poliza, null, null);
						polizaDao.saveOrUpdate(mp);
						modulos.add(mp);
					}
				}
			}
			poliza.setModuloPolizas(modulos);
		} catch (DAOException e) {
			logger.error("PolizaManager - asignacionModulosGanado. Error en la asignaciï¿½n de los mï¿½dulos de ganado",
					e);
		}
	}
	
	/**
	 * Obtener el estado de la pï¿½liza
	 * @author U029114 14/09/2017
	 * @param idPoliza
	 * @return BigDecimal
	 * @throws DAOException
	 */
	public BigDecimal obtenerEstadoPoliza(Long idpoliza) throws DAOException {

		try {
			BigDecimal estadoPoliza = polizaDao.obtenerEstadoPoliza(idpoliza);
			return estadoPoliza;

		}catch (Exception ex) {
			logger.info("Se ha producido un error al obtener el campo idestado de la poliza: " + ex.getMessage());
			throw new DAOException("Se ha producido un error al obtener el campo idestado de la poliza", ex);
		}

	}
	

	public String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) throws DAOException {
		try {
			return this.polizaDao.getNombreOficina(codOficina, codEntidad);
		} catch (Exception e) {
			throw new DAOException("Error al obtener el nombre de Oficina", e);
		}
	}
	
	/* P0063482 ** MODIF TAM (27.08.2021) ** Defecto 31 y 32 * Inicio */
	/* ESC- BOTÓN VOLVER DESDE FORMA DE PAGO EN PóLIZAS CONTRATADAS DESDE EL BOTÓN DE ALTA DE PÓLIZA*/
	public Modulo obtenerModulo(ModuloId moduloId) {
		Modulo modulo = new Modulo();
		
		try{
			modulo = (Modulo)polizaDao.get(Modulo.class, moduloId);
			
		} catch (Exception ex) {
			logger.error(" Se ha producido un error al clonar la parcela.", ex);
		}	
		return modulo;
	}
	/* P0063482 ** MODIF TAM (27.08.2021) ** Defecto 31 y 32 * Fin */
	
	
	public boolean esPolizaGanadoByIdPoliza(Long idPoliza) throws DAOException {
		return polizaDao.esPolizaGanadoByIdPoliza(idPoliza);
	}
	
	public void setDatosAvalDao(IDatosAvalDao datosAvalDao) {
		this.datosAvalDao = datosAvalDao;
	}
	
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public void setClaseDao(IClaseDao claseDao) {
		this.claseDao = claseDao;
	}
	
	public void setFechaContratacionDao(IFechaContratacionDao fechaContratacionDao) {
		this.fechaContratacionDao = fechaContratacionDao;
	}		
	
	public void setCpmTipoCapitalDao(ICPMTipoCapitalDAO cpmTipoCapitalDao) {
		this.cpmTipoCapitalDao = cpmTipoCapitalDao;
	}

	public IClaseDetalleDao getClaseDetalleDao() {
		return claseDetalleDao;
	}

	public void setClaseDetalleDao(IClaseDetalleDao claseDetalleDao) {
		this.claseDetalleDao = claseDetalleDao;
	}

	public void setSeleccionPolizaDao(ISeleccionPolizaDao seleccionPolizaDao) {
		this.seleccionPolizaDao = seleccionPolizaDao;
	}
	
	public void setCalculoPrecioProduccionManager(
			CalculoPrecioProduccionManager calculoPrecioProduccionManager) {
		this.calculoPrecioProduccionManager = calculoPrecioProduccionManager;
	}
	
	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}

	public void setSeleccionComparativaSWDao(
			ISeleccionComparativaSWDao seleccionComparativaSWDao) {
		this.seleccionComparativaSWDao = seleccionComparativaSWDao;
	}
	
	
}