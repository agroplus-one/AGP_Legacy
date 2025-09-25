package com.rsi.agp.serviciosweb.contratacionextconfirmacion;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.soap.MTOM;

@MTOM(enabled = true)
@WebService(name = "ContratacionExtConfirmacion", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({ org.w3._2005._05.xmlmime.ObjectFactory.class,
		com.rsi.agp.serviciosweb.contratacionextconfirmacion.ObjectFactory.class })
public class ContratacionExtConfirmacionImpl extends ContratacionExtConfirmacion {

	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/confirmar")
	@WebResult(name = "confirmarResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public ConfirmarResponse confirmar(
			@WebParam(name = "confirmarRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") ConfirmarRequest parameters) {
		return getBo().confirmar(parameters);
	}

	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/solicitarCupon")
	@WebResult(name = "solicitudCuponResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public SolicitudCuponResponse solicitarCupon(
			@WebParam(name = "solicitudCuponRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") SolicitudCuponRequest parameters) {
		return getBo().solicitarCupon(parameters);
	}

	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/anularCupon")
	@WebResult(name = "anularCuponResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public AnularCuponResponse anularCupon(
			@WebParam(name = "anularCuponRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") AnularCuponRequest parameters) {
		return getBo().anularCupon(parameters);
	}
	
	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/confirmarCupon")
	@WebResult(name = "confirmarCuponResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public ConfirmarCuponResponse confirmarCupon(
			@WebParam(name = "confirmarCuponRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") ConfirmarCuponRequest parameters) {
		return getBo().confirmarCupon(parameters);
	}
	
	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/confirmarSiniestro")
	@WebResult(name = "confirmarSiniestroResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public ConfirmarSiniestroResponse confirmarSiniestro(
			@WebParam(name = "confirmarSiniestroRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") ConfirmarSiniestroRequest parameters) {
		return getBo().confirmarSiniestro(parameters);
	}
	
	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/calcular")
	@WebResult(name = "calcularResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public CalcularResponse calcular(
			@WebParam(name = "calcularRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") CalcularRequest parameters) {
		return  getBo().calcular(parameters);
	}
	
	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/calcularAnexo")
	@WebResult(name = "calcularAnexoResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public CalcularAnexoResponse calcularAnexo(
			@WebParam(name = "calcularAnexoRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") CalcularAnexoRequest parameters) {
		return  getBo().calcularAnexo(parameters);
	}

	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/validar")
	@WebResult(name = "validarResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public ValidarResponse validar(
			@WebParam(name = "validarRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") ValidarRequest parameters) {
		return  getBo().validar(parameters);
	}
	
	@WebMethod(action = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/validarAnexo")
	@WebResult(name = "validarResponse", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters")
	public ValidarResponse validarAnexo(
			@WebParam(name = "validarAnexoRequest", targetNamespace = "http://agp.rsi.com/serviciosweb/ContratacionExtConfirmacion/", partName = "parameters") ValidarRequest parameters) {
		return  getBo().validarAnexo(parameters);
	}
}
