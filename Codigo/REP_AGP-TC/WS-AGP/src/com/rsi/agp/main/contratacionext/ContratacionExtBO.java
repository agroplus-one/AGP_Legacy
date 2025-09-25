package com.rsi.agp.main.contratacionext;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

import com.rsi.agp.core.managers.IContratacionExtConfirmacionManager;
import com.rsi.agp.core.managers.confirmarext.AnularCuponExtBean;
import com.rsi.agp.core.managers.confirmarext.CalcularAnexoExtBean.AgrError;
import com.rsi.agp.core.managers.confirmarext.CalcularExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarCuponExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarExtBean;
import com.rsi.agp.core.managers.confirmarext.ConfirmarSiniestroExtBean;
import com.rsi.agp.core.managers.confirmarext.SolicitarCuponExtBean;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularAnexoRequest;
import com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularAnexoResponse;
import com.rsi.agp.serviciosweb.contratacionextconfirmacion.ValidarRequest;
import com.rsi.agp.serviciosweb.contratacionextconfirmacion.ValidarResponse;

public class ContratacionExtBO implements ServletContextAware {

	private ServletContext servletContext;
	private IContratacionExtConfirmacionManager manager;

	private static final Log logger = LogFactory.getLog(ContratacionExtBO.class);

	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarResponse confirmar(
			com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarRequest parameters) {
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarResponse response;
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarResponse();
		logger.debug("############ ContratacionExtBO -- confirmar - BEGIN ############");
		if (parameters.getPoliza() == null || parameters.getPoliza().getValue() == null) {
			response.setCodigo("-1");
			response.setMensaje("No se ha recibido la poliza a confirmar.");
			logger.error("No se ha recibido la poliza a confirmar.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
                /*esc-11877*/
				logger.error("Antes de ejecutar el doConfirmar.");
				ConfirmarExtBean result = this.manager.doConfirmar(
						parameters.getPoliza(), real_path);

				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Poliza rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Poliza validada por RGA pero rechazada por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (com.rsi.agp.core.managers.confirmarext.ConfirmarExtBean.AgrError agrError : result
							.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Poliza aceptada y confirmada con Agroseguro. [" + result.getMensaje() + "]");
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- confirmar - END ############");
		return response;
	}

	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.SolicitudCuponResponse solicitarCupon(
			com.rsi.agp.serviciosweb.contratacionextconfirmacion.SolicitudCuponRequest parameters) {
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.SolicitudCuponResponse response;
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.SolicitudCuponResponse();
		logger.debug("############ ContratacionExtBO -- solicitarCupon - BEGIN ############");
		if (parameters.getPlan() == null || StringUtils.isNullOrEmpty(parameters.getReferencia())) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
				SolicitarCuponExtBean result = this.manager.doSolicitarCupon(parameters.getPlan(),
						parameters.getReferencia(), real_path);
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Solicitud rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Solicitud validada por RGA pero rechazada por Agroseguro.");
					response.setCuponModificacion(result.getCuponModificacion());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Solicitud aceptada por Agroseguro. [" + result.getMensaje() + "]");
					response.setPoliza(result.getPoliza());
					response.setPolizaComp(result.getPolizaComp());
					response.setEstadoContratacion(result.getEstadoContratacion());
					response.setCuponModificacion(result.getCuponModificacion());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- response - END ############");
		return response;
	}

	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.AnularCuponResponse anularCupon(
			com.rsi.agp.serviciosweb.contratacionextconfirmacion.AnularCuponRequest parameters) {
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.AnularCuponResponse response;
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.AnularCuponResponse();
		logger.debug("############ ContratacionExtBO -- anularCupon - BEGIN ############");
		if (StringUtils.isNullOrEmpty(parameters.getIdCupon())) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
				AnularCuponExtBean result = this.manager.doAnularCupon(parameters.getIdCupon(), real_path);
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Solicitud rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Solicitud validada por RGA pero rechazada por Agroseguro.");

					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Solicitud aceptada por Agroseguro. [" + result.getMensaje() + "]");

					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- response - END ############");
		return response;
	}

	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarCuponResponse confirmarCupon(
			com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarCuponRequest parameters) {
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarCuponResponse response;
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarCuponResponse();
		logger.debug("############ ContratacionExtBO -- confirmarCupon - BEGIN ############");
		if (StringUtils.isNullOrEmpty(parameters.getIdCupon()) || parameters.getRevisionAdmin() == null
				|| ((parameters.getPolizaPpal() == null || parameters.getPolizaPpal().getValue() == null)
						&& (parameters.getPolizaComp() == null || parameters.getPolizaComp().getValue() == null))) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
				ConfirmarCuponExtBean result = this.manager.doConfirmarCupon(parameters.getIdCupon(),
						parameters.getRevisionAdmin(), parameters.getPolizaPpal(), parameters.getPolizaComp(),
						real_path);
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Anexo rechazado por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Anexo validado por RGA pero rechazado por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (com.rsi.agp.core.managers.confirmarext.ConfirmarCuponExtBean.AgrError agrError : result
							.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Anexo enviado a Agroseguro. [" + result.getMensaje() + "]");
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- response - END ############");
		return response;
	}
	
	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarSiniestroResponse confirmarSiniestro(
			com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarSiniestroRequest parameters) {
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarSiniestroResponse response;
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.ConfirmarSiniestroResponse();
		logger.debug("############ ContratacionExtBO -- confirmarSiniestro - BEGIN ############");
		if (parameters.getSiniestro() == null || parameters.getSiniestro().getValue() == null) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
				ConfirmarSiniestroExtBean result = this.manager.doConfirmarSiniestro(parameters.getSiniestro(), real_path);
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Poliza rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Siniestro validado por RGA pero rechazada por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (com.rsi.agp.core.managers.confirmarext.ConfirmarSiniestroExtBean.AgrError agrError : result
							.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Siniestro aceptado y confirmado con Agroseguro. [" + result.getMensaje() + "]");
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- response - END ############");
		return response;
	}
	
	/* Pet. 73328 ** MODIF TAM (18.03.2021) ** Inicio */
	public com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularResponse calcular(com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularRequest parameters) {
		
		logger.debug("############ ContratacionExtBO -- calcular - [INIT] ############");
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularResponse response;
		
		response = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.CalcularResponse();
		
		if (parameters.getPoliza() == null || parameters.getPoliza().getValue() == null) {
			response.setCodigo("-1");
			response.setMensaje("No se ha recibido la poliza para Calcular.");
			logger.error("No se ha recibido la poliza para Calcular.");
		} else {
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
                
				logger.error("Antes de ejecutar el doCalcular.");
				CalcularExtBean result = this.manager.doCalcular(parameters.getPoliza(), real_path);
	
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Poliza rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Poliza validada por RGA pero rechazada por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (com.rsi.agp.core.managers.confirmarext.CalcularExtBean.AgrError agrError : result.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Poliza Aceptada y calculada con Agroseguro. [" + result.getMensaje() + "]");
					response.setAcuseRecibo(result.getAcuseRecibo());
					response.setCalculo(result.getCalculo());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		logger.debug("############ ContratacionExtBO -- Calcular - [END] ############");
		
		return response;
	}

	/* Pet. 73328 ** MODIF TAM (18.03.2021) ** Fin */

	public void setManager(final IContratacionExtConfirmacionManager manager) {
		this.manager = manager;
	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public CalcularAnexoResponse calcularAnexo(CalcularAnexoRequest parameters) {
	
		logger.debug("############ ContratacionExtBO -- calcularAnexo - [INIT] ############");
		
		CalcularAnexoResponse response = new CalcularAnexoResponse();
				
		if (StringUtils.isNullOrEmpty(parameters.getIdCupon()) || StringUtils.isNullOrEmpty(parameters.getTipoPoliza()) || parameters.getModificacionPoliza() == null) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} 
	    else {
			
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
                
				logger.error("Antes de ejecutar el doCalcularAnexo.");
				
				com.rsi.agp.core.managers.confirmarext.CalcularAnexoExtBean result = this.manager.doCalcularAnexo(parameters.getIdCupon(), parameters.getTipoPoliza(), true, parameters.getModificacionPoliza(), real_path);
	
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Poliza rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Poliza validada por RGA pero rechazada por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (AgrError agrError : result.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Anexo Aceptado y calculado con Agroseguro. [" + result.getMensaje() + "]");
					response.setCalculoModificacion(result.getCalculoModificacion());
					response.setCalculoOriginal(result.getCalculoOriginal());
					response.setDiferenciasCoste(result.getDiferenciasCoste());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		
		logger.debug("############ ContratacionExtBO -- calcularAnexo - [END] ############");
		
		return response;
	}
	
	public ValidarResponse validar(ValidarRequest parameters) {
		logger.debug("############ ContratacionExtBO -- validar - [INIT] ############");
		
		ValidarResponse response = new ValidarResponse();

		if (parameters.getPoliza() == null) {
			response.setCodigo("-1");
			response.setMensaje("No se han recibido todos los datos de entrada.");
			logger.error("No se han recibido todos los datos de entrada.");
		} else {			
			try {
				String real_path = this.servletContext.getRealPath("/WEB-INF");
                
				logger.error("Antes de ejecutar el doValidar.");
				
				com.rsi.agp.core.managers.confirmarext.ValidarExtBean result = this.manager.doValidar(parameters.getPoliza(), real_path);
				
				switch (result.getCodigo()) {
				case -1:
					response.setCodigo("-1");
					response.setMensaje("Error inesperado. [" + result.getMensaje() + "]");
					break;
				case 0:
					response.setCodigo("0");
					response.setMensaje("Poliza rechazada por validaciones RGA. [" + result.getMensaje() + "]");
					break;
				case 1:
					response.setCodigo("1");
					response.setMensaje("Poliza validada por RGA pero rechazada por Agroseguro.");
					List<com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error> errors = response.getErrors();
					for (com.rsi.agp.core.managers.confirmarext.ValidarExtBean.AgrError agrError : result.getAgrErrors()) {
						com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error error = new com.rsi.agp.serviciosweb.contratacionextconfirmacion.Error();
						error.setCodigo(agrError.getCodigo());
						error.setMensaje(agrError.getMensaje());
						errors.add(error);
					}
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				case 2:
					response.setCodigo("2");
					response.setMensaje("Resultado de la Validacion. [" + result.getMensaje() + "]");
					response.setAcuseRecibo(result.getAcuseRecibo());
					break;
				default:
					response.setCodigo("-1");
					response.setMensaje("Codigo de respuesta inesperado.");
					break;
				}
			} catch (Throwable e) {
				response.setCodigo("-1");
				response.setMensaje("Error: " + e.getMessage());
				logger.error(e);
			}
		}
		
		logger.debug("############ ContratacionExtBO -- validar - [END] ############");
		
		return response;
	
	}
	
	public ValidarResponse validarAnexo(ValidarRequest parameters) {
		logger.debug("############ ContratacionExtBO -- validarAnexo - [INIT] ############");
		
		ValidarResponse response = new ValidarResponse();
		
		logger.debug("############ ContratacionExtBO -- validarAnexo - [END] ############");
		
		return response;
	}	
}
