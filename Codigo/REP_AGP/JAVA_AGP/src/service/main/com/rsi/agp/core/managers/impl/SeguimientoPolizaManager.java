package com.rsi.agp.core.managers.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.managers.ISeguimientoPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.models.admin.IColectivoDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.models.poliza.ISeguimientoPolizaDao;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.seguimientoContratacion.Contratacion;
import es.agroseguro.seguimientoContratacion.Incidencia;
import es.agroseguro.seguimientoContratacion.SeguimientoContratacion;

public class SeguimientoPolizaManager implements ISeguimientoPolizaManager {

	private static final Log logger = LogFactory.getLog(SeguimientoPolizaManager.class);
	private WebServicesManager webServicesManager;
	
	private ISeguimientoPolizaDao seguimientoPolizaDao;
	private IPolizaDao polizaDao;
	private IColectivoDao colectivoDao;
	
	private Date fechaCambioEstDesde;  //00 horas del dia
	private Date fechaCambioEstHasta; //24 horas del dia de hoy
	
	private final String DESDE_STR = "desde";
	private final String HASTA_STR = "hasta";

	/**
	 * Método que obtiene una lista del objeto SeguimientoPolizaBean resultante de la llamada al SW de seguimiento
	 * del estado de la póliza
	 * @author DANUNEZ
	 * @since 17/01/2019
	 * @return List<SeguimientoPolizaBean>
	 **/
	public List<SeguimientoPolizaBean> getPolizasSeguimiento(String codUsuario, String realPath) throws DAOException {
		List<SeguimientoPolizaBean> listaSeguimiento = null;
		
		fechaCambioEstDesde = configurarFecha(DESDE_STR);
		fechaCambioEstHasta = configurarFecha(HASTA_STR);

		//Llamada al sw de seguimiento del estado de la poliza de agroseguro.
		//pasar por parametros fecha de cambio de estado desde y fecha de cambio de estado hasta
		//la referencia se la paso "" y el plan inicializado a 0, no necesito ninguno de los dos
		
		logger.info("Llamamos al servicio de consulta de Seguimiento de la Poliza desde getPolizasSeguimiento");
		Map<String, Object> faseDocument = webServicesManager.getSeguimientoPoliza("", new BigDecimal(0), fechaCambioEstDesde, fechaCambioEstHasta, realPath, "getPolizasSeguimiento");
		
		SeguimientoContratacion sc = (SeguimientoContratacion) faseDocument.get("acuse");
		if (sc == null){
			listaSeguimiento = new ArrayList<SeguimientoPolizaBean>();
		} else {
			listaSeguimiento = new ArrayList<SeguimientoPolizaBean>(sc.getContratacionArray().length);
			for (es.agroseguro.seguimientoContratacion.Contratacion con : sc.getContratacionArray()) {			
				listaSeguimiento.add(SeguimientoPolizaBean.populate(con));
			}
		}
		seguimientoPolizaDao.auditarLlamadaSW(null, null, fechaCambioEstDesde, fechaCambioEstHasta, codUsuario, sc == null ? (String) faseDocument.get("errorAgro") : sc.xmlText());
		return listaSeguimiento;
	}

	/**
	 * Método que obtiene la información del objeto Poliza a través de su idPoliza
	 * @author DANUNEZ
	 * @since 17/01/2019
	 * @param idPoliza Identificador único de una póliza
	 * @return SeguimientoPolizaBean
	 **/
	public SeguimientoPolizaBean getInfoPoliza(final Poliza poliza, final String codUsuario, final String realPath) throws DAOException {
		
		SeguimientoPolizaBean seguimientoPolizaBean;
		
		String referencia = poliza.getReferencia();
		BigDecimal plan   = poliza.getLinea().getCodplan();
		
		fechaCambioEstDesde = configurarFecha(DESDE_STR);
		fechaCambioEstHasta = configurarFecha(HASTA_STR);
		
		//Llamada al sw de seguimiento del estado de la poliza de agroseguro. SeguimientoSCPoliza_Service
		//pasar por parametros la referencia y el plan obtenidos anteriormente.
		logger.info("Llamamos al servicio de consulta de Seguimiento de la Poliza desde getInfoPoliza");
		Map<String, Object> faseDocument = webServicesManager.getSeguimientoPoliza(referencia, plan, fechaCambioEstDesde, fechaCambioEstHasta, realPath, "getInfoPoliza");
		
		SeguimientoContratacion sc = (SeguimientoContratacion) faseDocument.get("acuse");
		if (sc == null){
			
			seguimientoPolizaBean = new SeguimientoPolizaBean(referencia, poliza.getDc().intValue(), poliza.getLinea().getCodplan().intValue(), poliza.getLinea().getCodlinea().intValue(), 
					poliza.getTipoReferencia(), poliza.getColectivo().getTomador().getId().getCiftomador(), poliza.getColectivo().getTomador().getRazonsocial(), 
					poliza.getColectivo().getIdcolectivo(),"", poliza.getAsegurado().getNifcif(), poliza.getFechavigor(), poliza.getColectivo().getDc());
		} else {
			Contratacion con;
			if (Constants.MODULO_POLIZA_PRINCIPAL.equals(poliza.getTipoReferencia())) {
				con = sc.getContratacionArray() == null || sc.getContratacionArray().length == 0 ? null : sc.getContratacionArray(0);
			} else {
				con = sc.getContratacionArray() == null || sc.getContratacionArray().length < 2 ? null : sc.getContratacionArray(1);
			}
			seguimientoPolizaBean = con == null ? new SeguimientoPolizaBean() : SeguimientoPolizaBean.populate(con);	
			/* ESC-6475 
				Sacamos el digito de control de la BBDD puesto que no lo tenemos en el servicio */	
			String dcColectivo = colectivoDao.getDcColectivo(con.getColectivo());			
			seguimientoPolizaBean.setDigitoColectivo(dcColectivo);
			/* ESC-6475 FIN */
		}
		
		seguimientoPolizaDao.auditarLlamadaSW(plan, referencia, fechaCambioEstDesde, fechaCambioEstHasta, codUsuario, sc == null ? (String) faseDocument.get("errorAgro") : sc.xmlText());
		logger.info("Llamamos al servicio de consulta de Seguimiento de la Poliza desde getInfoPoliza [END]");
		return seguimientoPolizaBean;
	}

	public void actualizarPoliza(Long idPoliza, SeguimientoPolizaBean seguimientoPolizaBean, String codUsuario)
			throws DAOException {
		
		logger.debug("SeguimientoPolizaManager - actualizarPoliza [INIT]");
		
		BigDecimal idEstadoAgro = seguimientoPolizaBean.getEstado();
		Date fechaCamEstado     = seguimientoPolizaBean.getFechaEstado();
		BigDecimal costeTomador = seguimientoPolizaBean.getCosteTomador();
		Integer plan            = seguimientoPolizaBean.getPlan();
		
		logger.debug("Actualizamos la poliza del plan: "+plan + "con el idpoliza:" + idPoliza);
		
		
		seguimientoPolizaDao.actualizarPoliza(idPoliza, costeTomador, idEstadoAgro, fechaCamEstado, codUsuario, plan);
		logger.debug("Despues de actualizar la poliza");
	
		if (seguimientoPolizaBean.getIncidencia() != null && seguimientoPolizaBean.getIncidencia().length > 0 ){
			logger.debug("Entramos en el if");
			
			// Separo la lista global en una lista de incidencias y una lista de anexos
			List<Incidencia> listaGlobal = Arrays.asList(seguimientoPolizaBean.getIncidencia());
			//Si es 4 es incidencia y 1 es un anexo de modificacion
			List<Incidencia> listaInc = new ArrayList<Incidencia>();
			List<Incidencia> listaAnexMod = new ArrayList<Incidencia>();
			
			for(int x = 0; x<listaGlobal.size(); x++){
				int variableTipo = listaGlobal.get(x).getTipo();
				if(variableTipo == 1){//ANEXO MODIFICACION
					listaAnexMod.add(listaGlobal.get(x));
				}else if (variableTipo == 4){//INCIDENCIA
					listaInc.add(listaGlobal.get(x));
				}
			}
			
			logger.debug("Entramos en el if (2)");
			// Si existen anexos que actualizar, realice una llamada al método ‘actualizarAnexos’ 
			//	de ‘SeguimientoPolizaDAO’.
			if(listaAnexMod.size() > 0){
				seguimientoPolizaDao.actualizarAnexos(listaAnexMod, codUsuario, seguimientoPolizaBean);
			}
			logger.debug("Entramos en el if (3)");
			// Si existen incidencias que actualizar, realice una llamada al método ‘actualizarIncidencias’ 
			//	de ‘SeguimientoPolizaDAO’.
			if(listaInc.size() > 0){
				seguimientoPolizaDao.actualizarIncidencias(listaInc, codUsuario, seguimientoPolizaBean);
			}
		}
		logger.debug("SeguimientoPolizaManager - actualizarPoliza [END]");
	}
	
	public void actualizarRenovable(SeguimientoPolizaBean seguimientoPolizaBean, Long idRenov, String codUsuario)
			throws DAOException {

		BigDecimal idEstadoAgro = seguimientoPolizaBean.getEstado();
		BigDecimal costeTomador = seguimientoPolizaBean.getCosteTomador();
		Integer plan = seguimientoPolizaBean.getPlan();
		
		seguimientoPolizaDao.actualizaRenovable(null, idRenov, idEstadoAgro, costeTomador, plan, codUsuario);
	}
	
	public Poliza getPoliza(Long idPoliza){
		
		Poliza poliza = new Poliza();
		try {
			poliza = polizaDao.getPolizaById(idPoliza);
		} catch (DAOException e) {
			logger.error("Excepcion : SeguimientoPolizaManager - getPoliza", e);
		}
		
		return poliza;
	}
	
	public Date configurarFecha(String valor){
		
		Calendar calendar = Calendar.getInstance();
		
		/* MODIF TAM (25.03.2019) */
		/* Le restamos un día al calendar */
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		/* MODIF TAM (25.03.2019) - Fin */
        
        Date dateRetorno;
        if (valor.equals(DESDE_STR)){
        	dateRetorno = seguimientoPolizaDao.getFechaParamDesde();
        }else{ //hasta
        	calendar.set(Calendar.SECOND, 59);
	        calendar.set(Calendar.MINUTE, 59);
	        calendar.set(Calendar.HOUR_OF_DAY, 23);
	        dateRetorno = calendar.getTime(); 
        }
        
        return dateRetorno;
	}
	
	@Override
	public void createTmpBatchSeguimiento(final String nifAsegurado,
			final String nifTomador, final BigDecimal plan,
			final BigDecimal linea, final String referencia,
			final Character tipoRef, final BigDecimal entidad,
			final String oficina, final BigDecimal tipo, final String detalle,
			final String estado, final String colectivo) throws DAOException {
		this.seguimientoPolizaDao.createTmpBatchSeguimiento(nifAsegurado,
				nifTomador, plan, linea, referencia, tipoRef, entidad, oficina,
				tipo, detalle, estado, colectivo);
	}
	
	@Override
	public String getDistribucionCostes(final Long idPoliza, final String realPath) throws DAOException, SeguimientoServiceException {
		Poliza poliza = (Poliza) seguimientoPolizaDao.get(Poliza.class, idPoliza);
		es.agroseguro.seguroAgrario.distribucionCostesSeguroAgrario.Poliza xmlPoliza = this.webServicesManager
				.consultaCostesRecibo(poliza.getTipoReferencia().toString(), poliza.getLinea().getCodplan(),
						poliza.getReferencia(), null, null, realPath);
		return xmlPoliza.toString();
	}
	
	public void setWebServicesManager(WebServicesManager webServicesManager) {
		this.webServicesManager = webServicesManager;
	}
	
	//Setters para Spring
	public ISeguimientoPolizaDao getSeguimientoPolizaDao() {
		return seguimientoPolizaDao;
	}

	public void setSeguimientoPolizaDao(ISeguimientoPolizaDao seguimientoPolizaDao) {
		this.seguimientoPolizaDao = seguimientoPolizaDao;
	}

	public IPolizaDao getPolizaDao() {
		return polizaDao;
	}

	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public IColectivoDao getColectivoDao() {
		return colectivoDao;
	}

	public void setColectivoDao(IColectivoDao colectivoDao) {
		this.colectivoDao = colectivoDao;
	}
}