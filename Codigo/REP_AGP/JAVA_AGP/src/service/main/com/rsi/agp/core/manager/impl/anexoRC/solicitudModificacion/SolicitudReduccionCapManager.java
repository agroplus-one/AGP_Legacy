package com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion;

import java.math.BigDecimal;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.dsig.XMLObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.manager.impl.anexoRC.SWAnexoRCHelper;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalAsegurado;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.CapitalesAsegurados;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ObjetosAsegurados;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.PolizaReduccionCapital;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.rc.ICuponDao;
import com.rsi.agp.dao.models.rc.IEnviosSWAnulacionDao;
import com.rsi.agp.dao.models.rc.IEnviosSWSolicitudDao;
import com.rsi.agp.dao.tables.reduccionCap.CuponRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWAnulacionRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWSolicitudRC;
import com.rsi.agp.dao.tables.reduccionCap.EnviosSWXMLRC;
import com.rsi.agp.dao.tables.reduccionCap.EstadoCuponRC;
import com.rsi.agp.dao.tables.reduccionCap.HistoricoCuponRC;

import es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;
import es.agroseguro.tipos.MotivoReduccionCapital;
import com.rsi.agp.core.manager.impl.anexoRC.reduccionCapital.ParcelaReducida;

public class SolicitudReduccionCapManager implements ISolicitudReduccionCapManager {

	private static final Log logger = LogFactory.getLog(SolicitudReduccionCapManager.class);
	
	private IEnviosSWAnulacionDao enviosSWRCAnulacionDao;
	private IEnviosSWSolicitudDao enviosSWRCSolicitudDao;
	private ICuponDao cuponRCDao;
	
	private SWAnexoRCHelper swAnexoRCHelper;

	public SolicitudReduccionCapBean solicitarModificacion(
			final String referencia, final BigDecimal plan,
			final String realPath, final String codUsuario) {

		SolicitudReduccionCapResponse respuesta = null;

		SolicitudReduccionCapBean bean = new SolicitudReduccionCapBean();
		try {
			respuesta = new SWAnexoRCHelper()
						.solicitudModificacionRC(referencia, plan, realPath);

			//falta descomentar y modificar para insertar en tabla correcta para RC
			insertarEnviosSWSolicitud(referencia, plan, codUsuario, respuesta);
		} catch (AgrException e) {
			logger.error(
					"Ocurrió un error en la llamada al SW de SolicitudModificacion",
					e);
			return solicitudModificacionToBean(e);
		} catch (Exception e) {
			logger.error(
					"Ocurrió un error inesperado en la llamada al SW de SolicitudModificacion",
					e);
		}
		Long id = saveCupon(null, respuesta.getCuponModificacion()
				.getCuponModificacion().getIdCupon(), referencia, codUsuario,
				Constants.AM_CUPON_ESTADO_ABIERTO);
		
		try {
			bean.setId(id);
			bean.setIdCupon(respuesta.getCuponModificacion().getCuponModificacion().getIdCupon());
			
			EstadoContratacion estadoContratacion =  respuesta.getEstadoContratacion().getEstadoContratacion();
			boolean isRenovacion = estadoContratacion.getSeguroPrincipal() == null && estadoContratacion.getRenovacionContratacion() != null;
			
			bean.setIdEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getEstado()
					  : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getEstado());
			bean.setEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getDescriptivo()
				    : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getDescriptivo());
			
			if (estadoContratacion.getSeguroPrincipal() != null && estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon() != null) {
				bean.setModifPpalCupon(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getIdCupon());
				bean.setModifCplIdEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
				bean.setModifPpalEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
			}
			
		} catch (Exception e) {
			logger.error("Error al rellenar el bean con la respuesta del SW SolicitudModificacion para RC", e);
		}

		return bean;
	}
	
	/**
	 * Realiza la llamada al SW de Anulacion de Cupón y devuelve un string con el resultado de la operación
	 * @param idCupon
	 * @param realPath
	 * @param codUsuario
	 * @return
	 */
	public String anularCupon(final Long id, final String idCupon, final String realPath, final String codUsuario) {
		String respuesta = null;
		try {
			/* Por los desarrollos de la pet.57626 ** Tanto Ganado como Agricolas van por Formato Unificado */ 
			respuesta = new SWAnexoRCHelper().anulacionCuponRC(idCupon, realPath);
		} 
		catch (AgrException e) {
			logger.error("Ocurrió un error en la llamada al SW de AnulacionCupon", e);
			respuesta = getMsgAgrException(e);
		} catch (Exception e) {
			logger.error("Ocurrió un error inesperado en la llamada al SW de AnulacionCupon", e);
		}		
		// Inserta el registro en el histórico del cupón
		saveCupon(id, idCupon, null, codUsuario, Constants.AM_CUPON_ESTADO_CADUCADO);		
		// Inserta la comunicación con el SW de Anulación en la tabla correspondiente
		insertarEnvioSWAnulacion(idCupon, codUsuario, respuesta);		
		return respuesta;
	}

	/**
	 * Devuelve el objeto Poliza de contratacion de Agroseguro a partir del id
	 * de cupón indicado
	 * 
	 * @param idCupon
	 * @return
	 */
	public XmlObject getPolizaActualizadaFromCupon(final String idCupon) {
		// Obtiene el registro de envios al sw de Solicitud correspondiente al
		// cupón indicado
		EnviosSWSolicitudRC e = getEnvioSWSolicitudFromCupon(idCupon);
		logger.debug(" SolicitudModificacionManager -Dentro de getPolizaActualizadaFromCupon");
		// Si hay registro asociado
		if (e != null && e.getEnviosSWXMLRCByIdxmlPpal() != null) {
			// Se va a devolver el objeto póliza asociado a la situación
			// actualizada de la principal
			//return cargarPolizaRCFromXml(e.getEnviosSWXMLRCByIdxmlPpal().getXml());
			return cargarPolizaRCFromXml(e.getEnviosSWXMLRCByIdxmlPpal().getXml());
		}
		return null;
	}
//	
//	/**
//	 * Devuelve el objeto Poliza Complementaria de contratacion de Agroseguro a partir del id de cupón indicado
//	 * @param idCupon
//	 * @return
//	 */
//	public XmlObject getPolizaActualizadaCplFromCupon (String idCupon) {
//		// Obtiene el registro de envios al sw de Solicitud correspondiente al cupón indicado
//		EnviosSWSolicitud e = getEnvioSWSolicitudFromCupon(idCupon);
//		
//		// Si hay registro asociado
//		if (e != null && e.getEnviosSWXMLByIdxmlCpl() != null && e.getEnviosSWXMLByIdxmlCpl().getXml() != null) {
//			// Se va a devolver el objeto póliza asociado a la situación actualizada de la principal
//			return cargarPolizaCplFromXml(e.getEnviosSWXMLByIdxmlCpl().getXml());
//		}		
//		return null;
//	}
//	
//	
	/**
	 * Obtiene el registro de EnvioSWSolicitud correspondiente al id de cupón pasado como parámetro
	 * @param idCupon
	 * @return
	 */
	private EnviosSWSolicitudRC getEnvioSWSolicitudFromCupon (String idCupon) {
		
		try {
			return enviosSWRCSolicitudDao.getEnviosSWSolicitud(idCupon);
		} catch (DAOException e) {
			return null;
		} catch (Exception e1) {
			logger.error("Error inesperado al obtener el registro de envios al SW de Solicitud", e1);
		}
		
		return null;
	}
//	
	/**
	 * Genera el objeto Poliza correspondiente al clob pasado como parámetro
	 * @param xml
	 * @return
	 */
	private XmlObject cargarPolizaRCFromXml(final Clob xml) {
		XmlObject polizaRC = null;
		XmlObject xmlObject  = null;
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petición tanto las polizas agricolas como las de ganado
		 * irán por el mismo end-point y con formato Unificado
		 */
		
		logger.debug("SolicitudModificacionManager - cargarPolizaFromXml");
		
		try {
			//En algunos casos viene con xml-fragment, por lo que hay que reemplazarlo
			String xmlString = WSUtils.convertClob2String(xml).replace("xml-fragment", "ns2:Poliza");
			//polizaRC = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlString));		
			PolizaReduccionCapital polizaxmlRC = new PolizaReduccionCapital();
			polizaxmlRC.setModulo("2    ");
			polizaxmlRC.setPlan(2024);
			polizaxmlRC.setReferencia("K347451");
			
			MotivoReduccionCapital polizaMotivo = new MotivoReduccionCapital();
			polizaMotivo.setCausaDanio("00");
			//polizaMotivo.setFechaOcurrencia("2024");
			ObjetosAsegurados polizaObj = new ObjetosAsegurados();
			List<ParcelaReducida> parcelas = new ArrayList<ParcelaReducida>();
			for(ParcelaReducida parcela : parcelas) {
				ParcelaReducida parcelaAux = new ParcelaReducida();
				parcelaAux.setHoja(1);
				parcelaAux.setNumero(1);
				List<CapitalAsegurado> capitalesAseguradosList = new ArrayList<CapitalAsegurado>();
				for(CapitalAsegurado capAsegurado: capitalesAseguradosList) {
					CapitalAsegurado capAseguradoAux = new CapitalAsegurado();
					capAseguradoAux.setProduccionTrasReduccion(2000);
					capAseguradoAux.setTipo(0);
					capitalesAseguradosList.add(capAseguradoAux);
					
				}
				
				parcelas.add(parcelaAux);
			}
			polizaxmlRC.setMotivo(polizaMotivo);
			polizaxmlRC.setObjetosAsegurados(polizaObj);
			xmlObject = XmlObject.Factory.parse(xmlString);
		} catch (Exception e) {
			logger.error("Error inesperado al cargar la poliza desde el xml", e);
		} 
		return xmlObject ;
	}
	
	/**
	 * Genera el objeto Poliza Complementaria correspondiente al clob pasado como par�metro
	 * @param xml
	 * @return
	 */
//	private XmlObject cargarPolizaCplFromXml(final Clob xml) {
//		XmlObject poliza = null;
//		try {
//			String xmlString = WSUtils.convertClob2String(xml).replace("xml-fragment", "ns2:Poliza");
//			try {				
//				poliza = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlString));				
//			} catch (XmlException ex) {
//				poliza = es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument.Factory.parse(new StringReader(xmlString));
//			}
//		} catch (XmlException e) {
//			logger.error("Error al convertir el xml", e);
//		} catch (IOException e) {
//			logger.error("Error de entrada/salida", e);
//		} catch (Exception e) {
//			logger.error("Error inesperado al cargar la póliza desde el xml", e);
//		}
//		return poliza;
//	}
//	
	/**
	 * Inserta en bd el registro correspondiente al cupón asociado al anexo 
	 * @param idCupon Identificador del cupón solicitao
	 * @param referencia Referencia de la póliza asociada al anexo que solicita el cupón
	 * @param codUsuario Usuario que provoca el cambio de estado del cupón
	 * @return Id numérico del cupón creado en bd
	 */
	public Long saveCupon (Long id, String idCupon, String referencia, String codUsuario, Long estadoCupon) {
		
		// Estado del cupón
		EstadoCuponRC ec = new EstadoCuponRC();
		ec.setId(estadoCupon);

		// Si id es nulo hay que dar de alta el cupón
		CuponRC cupon = null;
		if (id == null) {
			cupon = new CuponRC(null, ec, idCupon, new Date(), referencia);
		}
		// Si id viene informado
		else {
			// Carga el cupón de BD
			try {
				cupon = (CuponRC) cuponRCDao.get(CuponRC.class, id);
			} catch (DAOException e) {
				logger.error("Error al cargar el cupon para actualizar", e);
				return null;
			}
			
			// Actualiza el cupón
			cupon.setEstadoCupon(ec);
		}
		
		
		try {
			cupon = cuponRCDao.saveCupon(cupon);
		} catch (DAOException e) {
			return null;
		}
		
		// Guarda el registro en el histórico del cupón
		saveHistoricoCupon(cupon, codUsuario);
		
		return cupon.getId();
	}
//	
	/**
	 * Inserta en bd el registro correspondiente a cambio de estado del cupón
	 * @param cupon Cupón que cambia de estado
	 * @param codUsuario Usuario que provoca el cambio de estado del cupón
	 */
	private void saveHistoricoCupon (CuponRC cupon, String codUsuario) {
		
		HistoricoCuponRC hc = new HistoricoCuponRC();
		hc.setCodusuario(codUsuario);
		hc.setCuponRC(cupon);
		hc.setFecha(new Date());
		hc.setEstadoCupon(cupon.getEstadoCupon());
		
		
		
		// Se inserta el registro de histórico en BD
		try {
			cuponRCDao.saveHistoricoCupon(hc);
		} catch (DAOException e) {
			// Devolver excepción para mostrar error en pantalla?
			logger.error("Ha ocurrido un error al guardar el historico del cupon", e);
		}
	}

	/**
	 * Inserta la comunicación con el SW de Anulación en la tabla correspondiente
	 * @param idCupon
	 * @param codUsuario
	 * @param respuesta
	 * @throws DAOException
	 */
	private void insertarEnvioSWAnulacion(String idCupon, String codUsuario, String respuesta) {

		EnviosSWXMLRC enviosSWXML = new EnviosSWXMLRC();
		enviosSWXML.setXml(Hibernate.createClob(respuesta));
		
		EnviosSWAnulacionRC envio = new EnviosSWAnulacionRC();
		envio.setEnviosSWXMLRC(enviosSWXML);
		envio.setCodusuario(codUsuario);
		envio.setFecha(new Date());
		envio.setIdcupon(idCupon);
		
		enviosSWRCAnulacionDao.saveEnviosSWAnulacion(envio);
	}
	
	/**
	 * Inserta la comunicación con el SW de Solicitud de Modificacion en la tabla correspondiente
	 * @param referencia
	 * @param plan
	 * @param codUsuario
	 * @param response
	 */
	public void insertarEnviosSWSolicitud(final String referencia,
			final BigDecimal plan, final String codUsuario,
			final SolicitudReduccionCapResponse response) {
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petición tanto las polizas agricolas como las de ganado
		 * irán por el mismo end-point y con formato Unificado
		 */
		
		// XML de la pï¿½liza principal
		EnviosSWXMLRC enviosSWXMLPpal = new EnviosSWXMLRC();
		
		enviosSWXMLPpal.setXml(Hibernate.createClob(response.getPolizaPrincipalUnif().toString()));
			
		// XML del estado de la contrataciï¿½n
		EnviosSWXMLRC enviosSWXMLEstadoCont = new EnviosSWXMLRC();
		enviosSWXMLEstadoCont.setXml(Hibernate.createClob(response
				.getEstadoContratacion().toString()));
		// cupï¿½n
		String idCupon = response.getCuponModificacion() != null ? response
				.getCuponModificacion().getCuponModificacion().getIdCupon()
				: null;
				
		EnviosSWSolicitudRC enviosSWSolicitud = new EnviosSWSolicitudRC();
		enviosSWSolicitud.setCodusuario(codUsuario);
		enviosSWSolicitud.setFecha(new Date());
		enviosSWSolicitud.setPlan(plan);
		enviosSWSolicitud.setReferencia(referencia);
		enviosSWSolicitud.setEnviosSWXMLRCByIdxmlPpal(enviosSWXMLPpal);
		enviosSWSolicitud.setEnviosSWXMLRCByIdxmlEstadoContratacion(enviosSWXMLEstadoCont);
		enviosSWSolicitud.setIdcupon(idCupon);
		try {
			enviosSWRCSolicitudDao.saveOrUpdate(enviosSWSolicitud);
		} catch (DAOException e) {
			logger.error(
					"Error al insertar el registro de la comunicacion con el SW de Solicitud de Modificacion",
					e);
		}
	}
//	
//	/**
//	 * Procesa la respuesta del servicio de SolicitudModificacion y pasa los datos necesarios al bean
//	 * @param respuesta
//	 * @return
//	 */
//	private SolicitudModificacionBean solicitudModificacionToBean (SolicitudModificacionResponse respuesta, Long id) {
//		SolicitudModificacionBean bean = new SolicitudModificacionBean();
//		
//		try {
//			// Obtiene el id del cupï¿½n solicitado
//			bean.setId(id);
//			bean.setIdCupon(respuesta.getCuponModificacion().getCuponModificacion().getIdCupon());
//			
//			// Obtiene el objecto que contiene el estado de la contrataciï¿½n de la respuesta
//			EstadoContratacion estadoContratacion =  respuesta.getEstadoContratacion().getEstadoContratacion();
//			
//			boolean isRenovacion = estadoContratacion.getSeguroPrincipal() == null && estadoContratacion.getRenovacionContratacion() != null;
//			
//			// -- Estado de la pï¿½liza principal o de la renovaciï¿½n -- 
//			// Cï¿½digo
//			bean.setIdEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getEstado()
//					 						  : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getEstado());
//			// Descripciï¿½n del estado
//			bean.setEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getDescriptivo()
//										    : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getDescriptivo());
//			
//			// Estado de la pï¿½liza complementaria
//			if (estadoContratacion.getSeguroComplementario() != null) {
//				bean.setIdEstadoCpl(estadoContratacion.getSeguroComplementario().getEstadoPoliza().getEstado());
//				bean.setEstadoCpl(estadoContratacion.getSeguroComplementario().getEstadoPoliza().getDescriptivo());
//			}
//			// Id de cupï¿½n de modificaciï¿½n sobre la pï¿½liza principal
//			if (estadoContratacion.getSeguroPrincipal() != null && estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon() != null) {
//				bean.setModifPpalCupon(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getIdCupon());
//				bean.setModifCplIdEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
//				bean.setModifPpalEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
//			}
//			// Id de cupï¿½n de modificaciï¿½n sobre la renovaciï¿½n
//			if (estadoContratacion.getRenovacionContratacion() != null && estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon() != null) {
//				bean.setModifPpalCupon(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getIdCupon());
//				bean.setModifCplIdEstado(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
//				bean.setModifPpalEstado(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
//			}
//			// Id de cupï¿½n de modificaciï¿½n sobre la pï¿½liza complementaria
//			if (estadoContratacion.getSeguroComplementario() != null &&	estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon() != null) {
//				bean.setModifCplCupon(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getIdCupon());
//				bean.setModifCplIdEstado(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
//				bean.setModifCplEstado(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
//			}
//		} catch (Exception e) {
//			logger.error("Error al rellenar el bean con la respuesta del SW SolicitudModificacion", e);
//		}
//		
//		return bean;
//	}

	private SolicitudReduccionCapBean solicitudModificacionToBean (AgrException exc) {

		SolicitudReduccionCapBean bean = new SolicitudReduccionCapBean();
		bean.setError(getMsgAgrException(exc));
		return bean;
		
	}

	private String getMsgAgrException (AgrException exc) {
		
		String msg = "";
		
		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				msg += error.getMensaje() + ". ";
			}
		}
		
		return msg;
	}
	
	public SWAnexoRCHelper getSwAnexoRCHelper() {
		return swAnexoRCHelper;
	}

	public void setSwAnexoRCHelper(SWAnexoRCHelper servAnexoRCHelper) {
		this.swAnexoRCHelper = servAnexoRCHelper;
	}

	public IEnviosSWAnulacionDao getEnviosSWRCAnulacionDao() {
		return enviosSWRCAnulacionDao;
	}
	
	public void setEnviosSWRCAnulacionDao(IEnviosSWAnulacionDao enviosSWRCAnulacionDao) {
		this.enviosSWRCAnulacionDao = enviosSWRCAnulacionDao;
	}
	
	public IEnviosSWSolicitudDao getEnviosSWRCSolicitudDao() {
		return enviosSWRCSolicitudDao;
	}
	
	public void setEnviosSWRCSolicitudDao(IEnviosSWSolicitudDao enviosSWRCSolicitudDao) {
		this.enviosSWRCSolicitudDao = enviosSWRCSolicitudDao;
	}

	public ICuponDao getCuponRCDao() {
		return cuponRCDao;
	}
	
	public void setCuponRCDao(ICuponDao cuponRCDao) {
		this.cuponRCDao = cuponRCDao;
	}	
}