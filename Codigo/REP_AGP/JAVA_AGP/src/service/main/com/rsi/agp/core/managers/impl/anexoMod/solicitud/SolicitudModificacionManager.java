package com.rsi.agp.core.managers.impl.anexoMod.solicitud;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Hibernate;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.anexoMod.SWAnexoModificacionHelper;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.util.WSUtils;
import com.rsi.agp.dao.models.anexo.ICuponDao;
import com.rsi.agp.dao.models.anexo.IEnviosSWAnulacionDao;
import com.rsi.agp.dao.models.anexo.IEnviosSWSolicitudDao;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.anexo.Cupon;
import com.rsi.agp.dao.tables.anexo.EnviosSWAnulacion;
import com.rsi.agp.dao.tables.anexo.EnviosSWSolicitud;
import com.rsi.agp.dao.tables.anexo.EnviosSWXML;
import com.rsi.agp.dao.tables.anexo.EstadoCupon;
import com.rsi.agp.dao.tables.anexo.HistoricoCupon;

import es.agroseguro.seguroAgrario.estadoContratacion.EstadoContratacion;
import es.agroseguro.serviciosweb.contratacionscmodificacion.AgrException;
import es.agroseguro.serviciosweb.contratacionscmodificacion.Error;

public class SolicitudModificacionManager implements ISolicitudModificacionManager {
	
	private IEnviosSWAnulacionDao enviosSWAnulacionDao;
	private IEnviosSWSolicitudDao enviosSWSolicitudDao;
	private ICuponDao cuponDao;
	private IPolizaDao polizaDao;

	private static final Log logger = LogFactory.getLog(SolicitudModificacionManager.class);
	
	/**
	 * Realiza la llamada al SW de SolicitudModificacion y devuelve un bean con la informaciï¿½n necesaria tras procesar la respuesta
	 * @param referencia
	 * @param plan
	 * @param realPath
	 * @return
	 */
	public SolicitudModificacionBean solicitarModificacion(
			final String referencia, final BigDecimal plan,
			final String realPath, final String codUsuario) {
		// Se realiza la llamada al servicio para obtener la situación
		// actualizada de la póliza y el cupón de modificación
		SolicitudModificacionResponse respuesta = null;
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petición tanto las polizas agricolas como las de ganado
		 * irán por el mismo end-point y con formato Unificado
		 */
		
		try {
			boolean esPolizaGanado = polizaDao.esPolizaGanado(referencia, plan);
			if (esPolizaGanado) {
				respuesta = new SWAnexoModificacionHelper()
						.getSolicitudModificacionUnificado(referencia, plan,
								realPath);
			} else {
				respuesta = new SWAnexoModificacionHelper()
						.getSolicitudModificacion(referencia, plan, realPath);
			}

			// Inserta la comunicación con el SW de Solicitud de Modificación en
			// la tabla correspondiente
			insertarEnviosSWSolicitud(referencia, plan, codUsuario, respuesta,
					esPolizaGanado);
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
		// Guarda el cupón de modificación obtenido
		Long id = saveCupon(null, respuesta.getCuponModificacion()
				.getCuponModificacion().getIdCupon(), referencia, codUsuario,
				Constants.AM_CUPON_ESTADO_ABIERTO);
		// Convierte la respuesta en un bean con los campos necesarios para
		// enviar a la pantalla
		return solicitudModificacionToBean(respuesta, id);
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
			respuesta = new SWAnexoModificacionHelper().getAnulacionCuponUnificado(idCupon, realPath);
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
		EnviosSWSolicitud e = getEnvioSWSolicitudFromCupon(idCupon);
		logger.debug(" SolicitudModificacionManager -Dentro de getPolizaActualizadaFromCupon");
		// Si hay registro asociado
		if (e != null && e.getEnviosSWXMLByIdxmlPpal() != null) {
			// Se va a devolver el objeto póliza asociado a la situación
			// actualizada de la principal
			return cargarPolizaFromXml(e.getEnviosSWXMLByIdxmlPpal().getXml());
		}
		return null;
	}
	
	/**
	 * Devuelve el objeto Poliza Complementaria de contratacion de Agroseguro a partir del id de cupón indicado
	 * @param idCupon
	 * @return
	 */
	public XmlObject getPolizaActualizadaCplFromCupon (String idCupon) {
		// Obtiene el registro de envios al sw de Solicitud correspondiente al cupón indicado
		EnviosSWSolicitud e = getEnvioSWSolicitudFromCupon(idCupon);
		
		// Si hay registro asociado
		if (e != null && e.getEnviosSWXMLByIdxmlCpl() != null && e.getEnviosSWXMLByIdxmlCpl().getXml() != null) {
			// Se va a devolver el objeto póliza asociado a la situación actualizada de la principal
			return cargarPolizaCplFromXml(e.getEnviosSWXMLByIdxmlCpl().getXml());
		}		
		return null;
	}
	
	
	/**
	 * Obtiene el registro de EnvioSWSolicitud correspondiente al id de cupón pasado como parámetro
	 * @param idCupon
	 * @return
	 */
	private EnviosSWSolicitud getEnvioSWSolicitudFromCupon (String idCupon) {
		
		try {
			return enviosSWSolicitudDao.getEnviosSWSolicitud(idCupon);
		} catch (DAOException e) {
			return null;
		} catch (Exception e1) {
			logger.error("Error inesperado al obtener el registro de envios al SW de Solicitud", e1);
		}
		
		return null;
	}
	
	/**
	 * Genera el objeto Poliza correspondiente al clob pasado como parámetro
	 * @param xml
	 * @return
	 */
	private XmlObject cargarPolizaFromXml(final Clob xml) {
		XmlObject poliza = null;
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petición tanto las polizas agricolas como las de ganado
		 * irán por el mismo end-point y con formato Unificado
		 */
		
		logger.debug("SolicitudModificacionManager - cargarPolizaFromXml");
		
		try {
			//En algunos casos viene con xml-fragment, por lo que hay que reemplazarlo
			String xmlString = WSUtils.convertClob2String(xml).replace("xml-fragment", "ns2:Poliza");
			try {
				poliza = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlString));				
			} catch (XmlException ex) {
				poliza = es.agroseguro.seguroAgrario.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlString));
			}
		} catch (XmlException e) {
			logger.error("Error al convertir el xml", e);
		} catch (IOException e) {
			logger.error("Error de entrada/salida", e);
		} catch (Exception e) {
			logger.error("Error inesperado al cargar la póliza desde el xml", e);
		}
		return poliza;
	}
	
	/**
	 * Genera el objeto Poliza Complementaria correspondiente al clob pasado como parámetro
	 * @param xml
	 * @return
	 */
	private XmlObject cargarPolizaCplFromXml(final Clob xml) {
		XmlObject poliza = null;
		try {
			String xmlString = WSUtils.convertClob2String(xml).replace("xml-fragment", "ns2:Poliza");
			try {				
				poliza = es.agroseguro.contratacion.PolizaDocument.Factory.parse(new StringReader(xmlString));				
			} catch (XmlException ex) {
				poliza = es.agroseguro.seguroAgrario.contratacion.complementario.PolizaDocument.Factory.parse(new StringReader(xmlString));
			}
		} catch (XmlException e) {
			logger.error("Error al convertir el xml", e);
		} catch (IOException e) {
			logger.error("Error de entrada/salida", e);
		} catch (Exception e) {
			logger.error("Error inesperado al cargar la póliza desde el xml", e);
		}
		return poliza;
	}
	
	/**
	 * Inserta en bd el registro correspondiente al cupón asociado al anexo 
	 * @param idCupon Identificador del cupón solicitao
	 * @param referencia Referencia de la póliza asociada al anexo que solicita el cupón
	 * @param codUsuario Usuario que provoca el cambio de estado del cupón
	 * @return Id numérico del cupón creado en bd
	 */
	public Long saveCupon(Long id, String idCupon, String referencia, String codUsuario, Long estadoCupon) {
		
		// Estado del cupón
		EstadoCupon ec = new EstadoCupon();
		ec.setId(estadoCupon);

		// Si id es nulo hay que dar de alta el cupón
		Cupon cupon = null;
		if (id == null) {
			cupon = new Cupon(null, ec, idCupon, new Date(), referencia);
		}
		// Si id viene informado
		else {
			// Carga el cupón de BD
			try {
				cupon = (Cupon) cuponDao.get(Cupon.class, id);
			} catch (DAOException e) {
				logger.error("Error al cargar el cupon para actualizar", e);
				return null;
			}
			
			// Actualiza el cupón
			cupon.setEstadoCupon(ec);
		}
		
		
		try {
			cupon = cuponDao.saveCupon(cupon);
		} catch (DAOException e) {
			return null;
		}
		
		// Guarda el registro en el histórico del cupón
		saveHistoricoCupon(cupon, codUsuario);
		
		return cupon.getId();
	}
	
	/**
	 * Inserta en bd el registro correspondiente a cambio de estado del cupón
	 * @param cupon Cupón que cambia de estado
	 * @param codUsuario Usuario que provoca el cambio de estado del cupón
	 */
	private void saveHistoricoCupon (Cupon cupon, String codUsuario) {
		
		HistoricoCupon hc = new HistoricoCupon();
		hc.setCodusuario(codUsuario);
		hc.setCupon(cupon);
		hc.setFecha(new Date());
		hc.setEstadoCupon(cupon.getEstadoCupon());
		
		// Si en el cupón viene el estado de la póliza principal asociada
		if (cupon.getEstadoPlzPpalAgroseguro() != null && cupon.getEstadoPlzPpalAgroseguro().getId() != null) {
			hc.setEstadoPlzAgroseguroByIdestadoPpal(cupon.getEstadoPlzPpalAgroseguro());
			
			// Si en el cupón viene el estado de la póliza compl ementaria asociada
			if (cupon.getEstadoPlzCplAgroseguro() != null  && cupon.getEstadoPlzCplAgroseguro().getId() != null) {
				hc.setEstadoPlzAgroseguroByIdestadoCpl(cupon.getEstadoPlzCplAgroseguro());
			}
			else {
				hc.setEstadoPlzAgroseguroByIdestadoCpl(null);
			}
		}
		
		
		// Se inserta el registro de histórico en BD
		try {
			cuponDao.saveHistoricoCupon(hc);
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

		EnviosSWXML enviosSWXML = new EnviosSWXML();
		enviosSWXML.setXml(Hibernate.createClob(respuesta));
		
		EnviosSWAnulacion envio = new EnviosSWAnulacion();
		envio.setEnviosSWXML(enviosSWXML);
		envio.setCodusuario(codUsuario);
		envio.setFecha(new Date());
		envio.setIdcupon(idCupon);
		
		enviosSWAnulacionDao.saveEnviosSWAnulacion(envio);
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
			final SolicitudModificacionResponse response,
			final boolean isPolizaGanado) {
		
		/* Pet. 57626 ** MODIF TAM (11.06.2020) ** Inicio */
		/* Por los desarrollos de esta petición tanto las polizas agricolas como las de ganado
		 * irán por el mismo end-point y con formato Unificado
		 */
		
		// XML de la pï¿½liza principal
		EnviosSWXML enviosSWXMLPpal = new EnviosSWXML();
		if (isPolizaGanado) {
			enviosSWXMLPpal.setXml(Hibernate.createClob(response
					.getPolizaGanado().toString()));
		} else {
			enviosSWXMLPpal.setXml(Hibernate.createClob(response.getPolizaPrincipalUnif().toString()));
		}
			
		// XML del estado de la contrataciï¿½n
		EnviosSWXML enviosSWXMLEstadoCont = new EnviosSWXML();
		enviosSWXMLEstadoCont.setXml(Hibernate.createClob(response
				.getEstadoContratacion().toString()));
		// XML de la pï¿½liza complementaria
		EnviosSWXML enviosSWXMLCpl = null;
		if (response.getPolizaComplementariaUnif() != null) {
			enviosSWXMLCpl = new EnviosSWXML();
			enviosSWXMLCpl.setXml(Hibernate.createClob(response
					.getPolizaComplementariaUnif().toString()));
		}
		// cupï¿½n
		String idCupon = response.getCuponModificacion() != null ? response
				.getCuponModificacion().getCuponModificacion().getIdCupon()
				: null;
		EnviosSWSolicitud enviosSWSolicitud = new EnviosSWSolicitud();
		enviosSWSolicitud.setCodusuario(codUsuario);
		enviosSWSolicitud.setFecha(new Date());
		enviosSWSolicitud.setPlan(plan);
		enviosSWSolicitud.setReferencia(referencia);
		enviosSWSolicitud.setEnviosSWXMLByIdxmlPpal(enviosSWXMLPpal);
		enviosSWSolicitud
				.setEnviosSWXMLByIdxmlEstadoContratacion(enviosSWXMLEstadoCont);
		enviosSWSolicitud.setEnviosSWXMLByIdxmlCpl(enviosSWXMLCpl);
		enviosSWSolicitud.setIdcupon(idCupon);
		try {
			enviosSWSolicitudDao.saveOrUpdate(enviosSWSolicitud);
		} catch (DAOException e) {
			logger.error(
					"Error al insertar el registro de la comunicacion con el SW de Solicitud de Modificacion",
					e);
		}
	}
	
	/**
	 * Procesa la respuesta del servicio de SolicitudModificacion y pasa los datos necesarios al bean
	 * @param respuesta
	 * @return
	 */
	private SolicitudModificacionBean solicitudModificacionToBean (SolicitudModificacionResponse respuesta, Long id) {
		SolicitudModificacionBean bean = new SolicitudModificacionBean();
		
		try {
			// Obtiene el id del cupï¿½n solicitado
			bean.setId(id);
			bean.setIdCupon(respuesta.getCuponModificacion().getCuponModificacion().getIdCupon());
			
			// Obtiene el objecto que contiene el estado de la contrataciï¿½n de la respuesta
			EstadoContratacion estadoContratacion =  respuesta.getEstadoContratacion().getEstadoContratacion();
			
			boolean isRenovacion = estadoContratacion.getSeguroPrincipal() == null && estadoContratacion.getRenovacionContratacion() != null;
			
			// -- Estado de la pï¿½liza principal o de la renovaciï¿½n -- 
			// Cï¿½digo
			bean.setIdEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getEstado()
					 						  : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getEstado());
			// Descripciï¿½n del estado
			bean.setEstadoPpal(isRenovacion ? estadoContratacion.getRenovacionContratacion().getEstadoRenovacion().getDescriptivo()
										    : estadoContratacion.getSeguroPrincipal().getEstadoPoliza().getDescriptivo());
			
			// Estado de la pï¿½liza complementaria
			if (estadoContratacion.getSeguroComplementario() != null) {
				bean.setIdEstadoCpl(estadoContratacion.getSeguroComplementario().getEstadoPoliza().getEstado());
				bean.setEstadoCpl(estadoContratacion.getSeguroComplementario().getEstadoPoliza().getDescriptivo());
			}
			// Id de cupï¿½n de modificaciï¿½n sobre la pï¿½liza principal
			if (estadoContratacion.getSeguroPrincipal() != null && estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon() != null) {
				bean.setModifPpalCupon(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getIdCupon());
				bean.setModifCplIdEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
				bean.setModifPpalEstado(estadoContratacion.getSeguroPrincipal().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
			}
			// Id de cupï¿½n de modificaciï¿½n sobre la renovaciï¿½n
			if (estadoContratacion.getRenovacionContratacion() != null && estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon() != null) {
				bean.setModifPpalCupon(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getIdCupon());
				bean.setModifCplIdEstado(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
				bean.setModifPpalEstado(estadoContratacion.getRenovacionContratacion().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
			}
			// Id de cupï¿½n de modificaciï¿½n sobre la pï¿½liza complementaria
			if (estadoContratacion.getSeguroComplementario() != null &&	estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon() != null) {
				bean.setModifCplCupon(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getIdCupon());
				bean.setModifCplIdEstado(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getCodigoEstadoIncidencia());
				bean.setModifCplEstado(estadoContratacion.getSeguroComplementario().getEstadoUltimaModificacionCupon().getDescriptivoEstadoIncidencia());
			}
		} catch (Exception e) {
			logger.error("Error al rellenar el bean con la respuesta del SW SolicitudModificacion", e);
		}
		
		return bean;
	}

	/**
	 * Incluye en el bean el detalle del error de la excepciï¿½n generada por la llamada al SW
	 * @param exc
	 * @return
	 */
	private SolicitudModificacionBean solicitudModificacionToBean (AgrException exc) {

		SolicitudModificacionBean bean = new SolicitudModificacionBean();
		bean.setError(getMsgAgrException(exc));
		return bean;
		
	}
	
	/**
	 * Devuelve una cadena con los errores devueltos en una AgrException
	 * @param exc
	 * @return
	 */
	private String getMsgAgrException (AgrException exc) {
		
		String msg = "";
		
		if (exc.getFaultInfo() != null && exc.getFaultInfo().getError() != null) {
			for (Error error : exc.getFaultInfo().getError()) {
				msg += error.getMensaje() + ". ";
			}
		}
		
		return msg;
	}

	public IEnviosSWAnulacionDao getEnviosSWAnulacionDao() {
		return enviosSWAnulacionDao;
	}

	public void setEnviosSWAnulacionDao(IEnviosSWAnulacionDao enviosSWAnulacionDao) {
		this.enviosSWAnulacionDao = enviosSWAnulacionDao;
	}

	public ICuponDao getCuponDao() {
		return cuponDao;
	}

	public void setCuponDao(ICuponDao cuponDao) {
		this.cuponDao = cuponDao;
	}

	public IEnviosSWSolicitudDao getEnviosSWSolicitudDao() {
		return enviosSWSolicitudDao;
	}

	public void setEnviosSWSolicitudDao(IEnviosSWSolicitudDao enviosSWSolicitudDao) {
		this.enviosSWSolicitudDao = enviosSWSolicitudDao;
	}

	public void setPolizaDao(final IPolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}
}