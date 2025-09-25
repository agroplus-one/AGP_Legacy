package com.rsi.agp.core.managers.impl.utilidades;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.hibernate.Hibernate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.ImpresionIncidenciasModManager;
import com.rsi.agp.core.managers.impl.anexoMod.impresion.SWImpresionModificacionHelper;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.inc.IAportarDocIncidenciaDao;
import com.rsi.agp.dao.models.inc.IIncidenciasAgroDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.inc.AsuntosInc;
import com.rsi.agp.dao.tables.inc.AsuntosIncId;
import com.rsi.agp.dao.tables.inc.DocsAfectadosInc;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.IncidenciasHist;
import com.rsi.agp.dao.tables.inc.LlamadaWSInc;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.acuseRecibo.AcuseRecibo;
import es.agroseguro.acuseRecibo.AcuseReciboDocument;
import es.agroseguro.acuseRecibo.DatosAsociados;
import es.agroseguro.acuseRecibo.Documento;
import es.agroseguro.acuseRecibo.Error;
import es.agroseguro.relacionincidencias.Incidencia;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.ParametrosAnulacionRescision;
import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.PlanReferenciaTipo;
import es.agroseguro.tipos.PolizaReferenciaTipo;

public class AnulacionyRescisionPolManager implements IManager {
	
	private static final Character CATALOGO_POLIZA = 'P';
	private IPolizaDao polizaDao;
	private IAportarDocIncidenciaDao aportarDocIncidenciaDao;
	private IIncidenciasAgroDao incidenciasAgroDao; 

	private static final BigDecimal COD_ESTADO_ACTIVO = new BigDecimal("1");
	private static final BigDecimal COD_ESTADO_ERRONEO = new BigDecimal ("0");
	
	private static final String SW_ANUL_POL = "anulacionPoliza";
	private static final String SW_RESC_POL = "rescisionPropuesta";
	
	private static final Log logger = LogFactory.getLog(ImpresionIncidenciasModManager.class);
	
	public Map<String, Object> guardarAnulacionyRescision(Incidencias incidenciaVista, Motivos motivoVista) 
			throws BusinessException{
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		logger.debug("**@@**AnulacionyRescisionPolManager - guardarAnulacionyRescision");
		Incidencias incidenciaModif = new Incidencias();

		try {
			
			String referenciaPoliza = incidenciaVista.getReferencia();
			Character tipoReferencia = incidenciaVista.getTiporef();
			BigDecimal plan = incidenciaVista.getCodplan();
			
			BigDecimal linea = this.polizaDao.obtenerLineaPoliza(referenciaPoliza, tipoReferencia, plan);
			
			if(referenciaPoliza != null){
				if (!StringUtils.isNullOrEmpty(referenciaPoliza)) {
					BigDecimal dc = this.incidenciasAgroDao.getDCPoliza(referenciaPoliza, tipoReferencia, plan, linea);
					incidenciaVista.setDc(dc);
				}
			}
			
			Motivos motivoBD = this.obtenerMotivoBD(motivoVista);
			incidenciaVista.setmotivos(motivoBD);
			
			motivoBD.getIncidenciases().add(incidenciaVista);
			
			EstadosInc estadoBorrador = (EstadosInc)this.incidenciasAgroDao.getObject(EstadosInc.class, 'B');
			estadoBorrador.getIncidenciases().add(incidenciaVista);
			incidenciaVista.setEstadosInc(estadoBorrador);
			
			DocsAfectadosInc docsAfectadosInc = new DocsAfectadosInc();
			docsAfectadosInc.setCoddocafectado('P');
			docsAfectadosInc.setDescripcion("Anulación/Rescisión");
			
			incidenciaVista.setDocsAfectadosInc(docsAfectadosInc);
			
			if (!incidenciaVista.getTipoinc().equals('I')) {
				AsuntosInc asuntosVista = new AsuntosInc();
				asuntosVista.getId().setCodasunto("SINASU");
				AsuntosInc asuntoBD = this.obtenerAsuntoBD(asuntosVista);
				incidenciaVista.setAsuntosInc(asuntoBD);
			}
			
			if (incidenciaVista.getIdincidencia() != null) {
				
				Long idIncidencia_aux = incidenciaVista.getIdincidencia();
	
				incidenciaModif = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia_aux);
				incidenciaModif.setCodestado(incidenciaVista.getCodestado());
				incidenciaModif.setCodestado(incidenciaVista.getCodestado());
				incidenciaModif.setAnhoincidencia(incidenciaVista.getAnhoincidencia());
				incidenciaModif.setAnhoincidencia(incidenciaVista.getAnhoincidencia());
				incidenciaModif.setmotivos(incidenciaVista.getmotivos());
				incidenciaModif.setIdincidencia(incidenciaVista.getIdincidencia());
				incidenciaModif.setTipoinc(incidenciaVista.getTipoinc());
				incidenciaModif.setCodplan(incidenciaVista.getCodplan());
				incidenciaModif.setCodlinea(incidenciaVista.getCodlinea());
				incidenciaModif.setReferencia(incidenciaVista.getReferencia());
				incidenciaModif.setTiporef(incidenciaVista.getTiporef());
				incidenciaModif = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaModif);
				parametros.put("incidenciaId", incidenciaModif.getIdincidencia());
				
				return parametros;
			}
				
			
			Incidencias incidencia = (Incidencias)this.incidenciasAgroDao.saveOrUpdate(incidenciaVista);
			parametros.put("incidenciaId", incidencia.getIdincidencia());
		} catch (DAOException e) {
			logger.error("No se ha guardado la incidencia", e);
			parametros.put("alerta", "No se ha guardado la incidencia");
			throw new BusinessException();
		}
 		
		return parametros;
	}
	
	public static Motivos obtenerMotivosVista(HttpServletRequest req){
		Integer codMotivoDoc;
		String motivoDoc = "";
		BigDecimal incActivo = new BigDecimal(0);
		
		
		if(StringUtils.isNullOrEmpty(req.getParameter("motivoAyR"))){
			codMotivoDoc = 0;
			motivoDoc = "Sin Motivo";
			incActivo = new BigDecimal("1");
			
		}else {
			codMotivoDoc = Integer.parseInt(req.getParameter("motivoAyR"));
			motivoDoc = req.getParameter("motivos");
			incActivo = new BigDecimal("1");
		}	
		
		Motivos motivo = new Motivos(codMotivoDoc, motivoDoc, incActivo);
		return motivo;
	}
	
	
	public Poliza obtenerPolizaByRefPlanLin(String refPoliza, Character tipoRefPoliza, BigDecimal plan, BigDecimal linea) throws DAOException {
		Poliza pol = new Poliza();
		pol = polizaDao.getPolizaByRefPlanLin(refPoliza, tipoRefPoliza, plan, linea);
		
		return pol;
	}
	
	public Map<String, Object> cargarNombreLinea(BigDecimal codLinea) throws DAOException{
		Map<String, Object> params = new HashMap<String, Object>();
		String nomLinea = this.incidenciasAgroDao.getNombreLinea(codLinea);
		params.put("nomLinea", nomLinea);
		return params;
	}
	

	
	public Motivos obtenerMotivoBD(Motivos motivoVista) throws DAOException{
		Motivos motivoBD = (Motivos)this.incidenciasAgroDao.getObject(Motivos.class, motivoVista.getCodmotivo());
		if(motivoBD == null){
			this.incidenciasAgroDao.saveOrUpdate(motivoVista);
			motivoBD = motivoVista;
		}
		return motivoBD;
	}
	
	private AsuntosInc obtenerAsuntoBD(AsuntosInc asuntosVista) throws DAOException{
		AsuntosInc asuntosBD = (AsuntosInc)this.incidenciasAgroDao.getObject(AsuntosInc.class, 
				new AsuntosIncId(asuntosVista.getId().getCodasunto(), CATALOGO_POLIZA));
		if(asuntosBD == null){
			this.incidenciasAgroDao.saveOrUpdate(asuntosVista);
			asuntosBD = asuntosVista;
		}
		return asuntosBD;
	}
	
	public static Map<String, Object> agregarParametrosDeVuelta(Map<String, Object> params, Incidencias incidencia){
		
		params.put("codPlan", incidencia.getCodplan());
		params.put("codlinea", incidencia.getCodlinea());
		params.put("nifcif", incidencia.getNifaseg());
		
		params.put("tipoRef", incidencia.getTiporef());
		params.put("tiporefSel", incidencia.getTiporef());
		params.put("referencia", incidencia.getReferencia());
		
		params.put("asunto", incidencia.getAsuntosInc().getId().getCodasunto());
		params.put("tipoAnuResc", incidencia.getTipoinc());
		
		return params;
	};


	public Map<String, Object> enviarAnulyRescAgroseguro(String realPath, Incidencias AnulyResc, Character tipoBusqueda, String codUsuario) 
			throws Exception{
		
		Long idIncidencia = AnulyResc.getIdincidencia();
		Map<String, Object> parametros = new HashMap<String, Object>();
		AcuseReciboDocument acuseReciboDocument = null;
		ParametrosAnulacionRescision wsReq = new ParametrosAnulacionRescision();
		Incidencias incidencia = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
		Character tipoInc = AnulyResc.getTipoinc();
		
		try{
			wsReq = crearWSRequestAnulyResc(AnulyResc, tipoBusqueda);
		
			/* ANULACIÓN */
			if (tipoInc.equals('A')){
				acuseReciboDocument = new SWImpresionModificacionHelper().envioAnulacionPol(realPath, wsReq);
			/* RESCISIÓN PROPUESTA DE PÓLIZAS */	
			}else if (tipoInc.equals('R')) {
				acuseReciboDocument = new SWImpresionModificacionHelper().envioRescisionPol(realPath, wsReq);
			}
			
			String Error = " "; 
			this.guardarXmlAnulyResc(wsReq, acuseReciboDocument, codUsuario, incidencia, Error, tipoInc);

			parametros = this.erroresAcuseRecibo(acuseReciboDocument.getAcuseRecibo());
			String estado ="";
			Incidencia incidenciaAgroseguro = this.obtenerNuevosDatosAnuyResc(acuseReciboDocument.getAcuseRecibo(), estado);
			
			if(incidenciaAgroseguro != null){
				parametros.put("codestadoInc", incidenciaAgroseguro.getEstado());
				actualizarAnulyRescBD(idIncidencia, incidenciaAgroseguro);
				
				this.guardarHistorico(incidencia, codUsuario);
			}else{
			
				if (incidencia != null){
					
					Long idIncidencia_aux = incidencia.getIdincidencia();
					estado = "nok";
					incidenciaAgroseguro = this.obtenerNuevosDatosAnuyResc(acuseReciboDocument.getAcuseRecibo(), estado);		
	
					Incidencias incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia_aux);
					incidenciaBD.setCodestado(COD_ESTADO_ACTIVO);
					
					if (incidenciaAgroseguro.getEstado().equals("2")){
						Character codEstado = 'R';
						EstadosInc estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
						incidenciaBD.setEstadosInc(estadoInc);
						estadoInc.getIncidenciases().add(incidenciaBD);
						
					}else if (incidenciaAgroseguro.getEstado().equals("3")) {
						Character codEstado = 'E';
						EstadosInc estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
						incidenciaBD.setEstadosInc(estadoInc);
						estadoInc.getIncidenciases().add(incidenciaBD);
					}
					
					parametros.put("codestadoInc", incidenciaAgroseguro.getEstado());
					
					incidenciaBD.setIdenvio(incidenciaAgroseguro.getIdEnvio());						
					incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
					
					this.guardarHistorico(incidencia, codUsuario);
					
				}
			}
			
		} catch (AgrException e) {
			String Error = procesarAgrException(e);
			this.guardarXmlAnulyResc(wsReq, acuseReciboDocument, codUsuario, incidencia, Error, tipoInc);
				
			this.actualizarAnulyRescBDError(idIncidencia);
			this.guardarHistorico(incidencia, codUsuario);
			throw e;
		}
		return parametros;
		
	}	
	
	private void guardarXmlAnulyResc(Object request, AcuseReciboDocument acuseReciboDocument, String codUsuario, Incidencias anulyResc, String Error, Character tipoInc) 
			throws DAOException{
		String xmlEnvio = "";
		String swUtilizado = "";
		
		if(tipoInc.equals('A')){
			logger.debug("Guardamos xml de Anulación de Pólizas");
			xmlEnvio = xmlEnvioAnulacionPol((ParametrosAnulacionRescision)request);
			swUtilizado = SW_ANUL_POL;
		} else {
			logger.debug("Guardamos xml de Rescisión Propuesta de Pólizas");
			xmlEnvio = xmlEnvioRescisionPol((ParametrosAnulacionRescision)request);
			swUtilizado = SW_RESC_POL;
		}
		LlamadaWSInc llamadaWS = new LlamadaWSInc();
		llamadaWS.setTimestamp(new Date());
		llamadaWS.setSwutilizado(swUtilizado);
		llamadaWS.setXmlenviado(Hibernate.createClob(xmlEnvio));
		/* MODIF TAM (04.10.2018) ** Inicio */
		if(acuseReciboDocument != null){
			llamadaWS.setXmlrecibido(Hibernate.createClob(acuseReciboDocument.toString()));
		}else{
			
			llamadaWS.setXmlrecibido(Hibernate.createClob(Error));
		}
	
		llamadaWS.setUsuario(codUsuario);
		llamadaWS.setIncidencias(anulyResc);
		this.aportarDocIncidenciaDao.saveOrUpdate(llamadaWS);
	}
	
	
	private static String xmlEnvioAnulacionPol(ParametrosAnulacionRescision req){		
		StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosweb/ContratacionSCImpresionModificacion/\" xmlns:xm=\"http://www.w3.org/2005/05/xmlmime\"><soapenv:Body>");
		sb.append("<con:anulacionPolizaRequest>");
		if(req.getPlanReferenciaTipo() != null){
			sb.append(nodoPlanReferenciaTipo(req.getPlanReferenciaTipo()));
		}
		if(req.getNif() != null){
			sb.append("<con:nif>").append(req.getNif()).append("</con:nif>");
		}
		if(String.valueOf(req.getLinea()) != null){
			sb.append("<con:linea>").append(req.getLinea()).append("</con:linea>");
		}
		if(String.valueOf(req.getCodigoMotivo()) != null){
			sb.append("<con:codigoMotivo>").append(req.getCodigoMotivo()).append("</con:codigoMotivo>");
		}
		
		sb.append("</con:anulacionPolizaRequest>");
		sb.append("</soapenv:Body></soapenv:Envelope>");;
		
		return sb.toString();
	}
	
	private static String xmlEnvioRescisionPol(ParametrosAnulacionRescision req){		
		StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oas=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:con=\"http://www.agroseguro.es/serviciosweb/ContratacionSCImpresionModificacion/\" xmlns:xm=\"http://www.w3.org/2005/05/xmlmime\"><soapenv:Body>");
		sb.append("<con:rescisionPropuestaRequest>");
		if(req.getPlanReferenciaTipo() != null){
			sb.append(nodoPlanReferenciaTipo(req.getPlanReferenciaTipo()));
		}
		if(req.getNif() != null){
			sb.append("<con:nif>").append(req.getNif()).append("</con:nif>");
		}
		if(String.valueOf(req.getLinea()) != null){
			sb.append("<con:linea>").append(req.getLinea()).append("</con:linea>");
		}
		if(String.valueOf(req.getCodigoMotivo()) != null && String.valueOf(req.getCodigoMotivo()) != "0"){
			sb.append("<con:codigoMotivo>").append(req.getCodigoMotivo()).append("</con:codigoMotivo>");
		}
		
		sb.append("</con:rescisionPropuestaRequest>");
		sb.append("</soapenv:Body></soapenv:Envelope>");;
		
		return sb.toString();
	}
		
		
	private static StringBuilder nodoPlanReferenciaTipo(PlanReferenciaTipo planReferenciaTipo){
		StringBuilder sb = new StringBuilder();
		sb.append("<con:planReferenciaTipo>");
		sb.append("<con:plan>").append(planReferenciaTipo.getPlan()).append("</con:plan>");
		sb.append("<con:referencia>").append(planReferenciaTipo.getReferencia()).append("</con:referencia>");
		sb.append("<con:tipoReferencia>").append(planReferenciaTipo.getTipoReferencia()).append("</con:tipoReferencia>");
		sb.append("</con:planReferenciaTipo>");
		return sb;
	}
	
	public static String procesarAgrException(AgrException e){
		StringBuilder msg = new StringBuilder();
		if(e.getFaultInfo() != null && e.getFaultInfo().getError() != null){
			List<es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error> errores = e.getFaultInfo().getError();
			for(es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.Error error : errores){
				msg.append(error.getMensaje()).append("; ");
			}
		}
		return msg.toString();
	}
	
	private Map<String,Object> erroresAcuseRecibo(AcuseRecibo acuseRecibo) throws XmlException {
		Map<String,Object> parametros = new HashMap<String, Object>();
		Documento[] documentos = acuseRecibo.getDocumentoArray();
		StringBuilder alerta = new StringBuilder();
		for(Documento doc : documentos){
			Error[] errores = doc.getErrorArray();
			for(Error error : errores) {
				alerta.append(error.getDescripcion()).append("; ");
			}			
		}
		if(alerta.length() > 0){
			parametros.put("alerta", alerta.toString());
		}		
		return parametros;
	}

	/* Creamos la Request de Envio de la Anulación de Pólizas */
	private ParametrosAnulacionRescision crearWSRequestAnulyResc(Incidencias AnulyResc, Character tipoInc){
		
		ParametrosAnulacionRescision paramAnulyResc = new ParametrosAnulacionRescision();
		PlanReferenciaTipo paramPlanRefTipo = new PlanReferenciaTipo();
		
		paramAnulyResc.setLinea(AnulyResc.getCodlinea().intValue());
		Integer codmotivo = (Integer) AnulyResc.getmotivos().getCodmotivo() ;
		if( tipoInc.equals('R')) {
			if (codmotivo > 0){
				paramAnulyResc.setCodigoMotivo(AnulyResc.getmotivos().getCodmotivo());
			}
		}
		paramAnulyResc.setCodigoMotivo(AnulyResc.getmotivos().getCodmotivo());
		paramAnulyResc.setNif(AnulyResc.getNifaseg());
		
		paramPlanRefTipo.setPlan(AnulyResc.getCodplan().intValue());
		paramPlanRefTipo.setReferencia(AnulyResc.getReferencia());
		if(AnulyResc.getTiporef().equals('P')){
			paramPlanRefTipo.setTipoReferencia(PolizaReferenciaTipo.P);
		} else {	
			paramPlanRefTipo.setTipoReferencia(PolizaReferenciaTipo.C);
		}
		paramAnulyResc.setPlanReferenciaTipo(paramPlanRefTipo);

		return paramAnulyResc;
	    
	}
	
	private Incidencia obtenerNuevosDatosAnuyResc(AcuseRecibo acuseRecibo, String estado) throws XmlException{
		Incidencia incidencia = new Incidencia();
		Documento[] documentos = acuseRecibo.getDocumentoArray();
		/* Estado correcto */
		if (estado.equals("")){
			for(Documento doc : documentos){
				String estadoInc = String.valueOf(doc.getEstado());
				DatosAsociados datosAsociados = doc.getDatosAsociados();
				if(datosAsociados != null){
					Node currNode = datosAsociados.getDomNode().getFirstChild();
					if(currNode.getNodeType() == Node.ELEMENT_NODE){
						Element element = (Element)currNode;	
						String estadoAgro = element.getAttribute("estado");
						String idEnvio = element.getAttribute("id");
						/* Anulación Aceptada */
						if (estadoInc.equals("1")) {
							incidencia.setEstado(estadoAgro);
						    incidencia.setIdEnvio(idEnvio);
						    int anioAyR = Integer.parseInt(element.getAttribute("anio"));
						    incidencia.setAnio(anioAyR);
						    BigInteger numeroAyR = new BigInteger(element.getAttribute("numero"));
						    incidencia.setNumero(numeroAyR);
						/* Anulación Rechazada */
						} else if (estadoInc.equals("2")) {
							incidencia.setEstado(estadoAgro);
						    incidencia.setIdEnvio(idEnvio);	
						/* Anulación Pendiente de Revisión de Adiministración */	
						} else if (estadoInc.equals("3")) {
							incidencia = new Incidencia();
							/* En este caso se queda como enviada correcta*/
							incidencia.setEstado(estadoAgro);
						    incidencia.setIdEnvio(idEnvio);						    
						    int anioAyR = Integer.parseInt(element.getAttribute("anio"));
						    incidencia.setAnio(anioAyR);
						    BigInteger numeroAyR = new BigInteger(element.getAttribute("numero"));
						    incidencia.setNumero(numeroAyR);
						}
					}
				}else{
					incidencia = null;
				}
			}
		}else{
			for(Documento doc : documentos){
				String estadoInc = Integer.toString(doc.getEstado());
				String idEnvio = doc.getId();
				if (estadoInc != null) {
					incidencia.setEstado(estadoInc);
				}
				if (idEnvio != null) {
					incidencia.setIdEnvio(idEnvio);
				}
			}
			
		}
		return incidencia;
	}
	
	private Incidencias actualizarAnulyRescBD(Long idIncidencia, Incidencia incidenciaAgroseguro) throws DAOException {
		Incidencias incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
		
		Character codEstado = incidenciaAgroseguro.getEstado().charAt(0);
		BigDecimal anhoincidencia = BigDecimal.ZERO;
		
		EstadosInc estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
		incidenciaBD.setEstadosInc(estadoInc);
		estadoInc.getIncidenciases().add(incidenciaBD);
		
		incidenciaBD.setAnhoincidencia(new BigDecimal(incidenciaAgroseguro.getAnio()));
		incidenciaBD.setNumincidencia(new BigDecimal(incidenciaAgroseguro.getNumero()));
		
		if (incidenciaBD.getAnhoincidencia() == null){
			incidenciaBD.setAnhoincidencia(anhoincidencia);
		}
		if (incidenciaBD.getNumincidencia() == null){
			incidenciaBD.setNumincidencia(anhoincidencia);
		}
		incidenciaBD.setCodestado(COD_ESTADO_ACTIVO);
		
		incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
		return incidenciaBD; 
	}
	
	
	private Incidencias actualizarAnulyRescBDError(Long idIncidencia) throws DAOException {
		Incidencias incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
		
		incidenciaBD.setCodestado(COD_ESTADO_ERRONEO);
		BigDecimal anhoincidencia = BigDecimal.ZERO;
		
		if (incidenciaBD.getAnhoincidencia() == null){
			incidenciaBD.setAnhoincidencia(anhoincidencia);
		}
		if (incidenciaBD.getNumincidencia() == null){
			incidenciaBD.setNumincidencia(anhoincidencia);
		}
			
		/* Actualizamos el estadoAgroseguro con el valor "Rechazada" */
		Character codEstado = 'R';
		EstadosInc estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
		incidenciaBD.setEstadosInc(estadoInc);
		estadoInc.getIncidenciases().add(incidenciaBD);
		
		incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
		return incidenciaBD; 
	}
	
	private void guardarHistorico(Incidencias inc, String codUsuario) throws DAOException{
		IncidenciasHist hist = new IncidenciasHist();
		hist.setAnhoincidencia(inc.getAnhoincidencia());
		hist.setAsuntosInc(inc.getAsuntosInc());
		hist.setmotivos(inc.getmotivos());
		hist.setCodestado(inc.getCodestado());
		hist.setCodlinea(inc.getCodlinea());
		hist.setCodplan(inc.getCodplan());
		hist.setDc(inc.getDc());
		hist.setDocsAfectadosInc(inc.getDocsAfectadosInc());
		hist.setEstadosInc(inc.getEstadosInc());
		hist.setFechaestado(inc.getFechaestado());
		hist.setFechaestadoagro(inc.getFechaestadoagro());
		hist.setIdenvio(inc.getIdenvio());
		hist.setNifaseg(inc.getNifaseg());
		hist.setNumdocumentos(inc.getNumdocumentos());
		hist.setNumincidencia(inc.getNumincidencia());
		hist.setObservaciones(inc.getObservaciones());
		hist.setReferencia(inc.getReferencia());
		hist.setTimestamp(new Date());
		hist.setTiporef(inc.getTiporef());
		hist.setUsuario(codUsuario);
		hist.setTipoInc(inc.getTipoinc());
		
		inc.getIncidenciasHists().add(hist);
		hist.setIncidencias(inc);
		
		this.aportarDocIncidenciaDao.saveOrUpdate(hist);
	}
	
	/**
	 * Setter para Spring 
	 * @param polizaDao
	 */
	public void setPolizaDao(IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	public IPolizaDao getPolizaDao() {
		return polizaDao;
	}
	
	public void setAportarDocIncidenciaDao(IAportarDocIncidenciaDao aportarDocIncidenciaDao) {
		this.aportarDocIncidenciaDao = aportarDocIncidenciaDao;
	}
	
	public void setIncidenciasAgroDao(IIncidenciasAgroDao incidenciasAgroDao) {
		this.incidenciasAgroDao = incidenciasAgroDao;
	}
	
	public IIncidenciasAgroDao getIncidenciasAgroDao() {
		return incidenciasAgroDao;
	}


	
}
	
	
	


