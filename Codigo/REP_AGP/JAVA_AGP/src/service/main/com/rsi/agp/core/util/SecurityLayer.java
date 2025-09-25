package com.rsi.agp.core.util;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SecurityLayerException;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.poliza.IPolizaDao;
import com.rsi.agp.dao.tables.commons.Usuario;

public class SecurityLayer {
	
	private static IPolizaDao polizaDao;
	
	public static void _assert(HttpServletRequest request) throws Exception {
		
		String path = request.getServletPath();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		
		if(usuario != null && !Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(usuario.getPerfil()) && !Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(usuario.getPerfil())){
			
			String idPoliza = null;
			String refPoliza = null;
			String idAnexo = null;
			String idAsegurado = null;
			String idRedCapital = null;
			String idSiniestro = null;
			
			if(path.equals("/siniestros.html")){
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("poliza.idpoliza"));
			}else if (path.equals("/parcelasSiniestradas.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("capAsegSiniestro.parcelaSiniestro.parcela.poliza.idpoliza"));
			}else if (path.equals("/declaracionesReduccionCapital.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("poliza.idpoliza"));
			}else if (path.equals("/parcelasReduccionCapital.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("parcela.reduccionCapital.poliza.idpoliza"));
			}else if (path.equals("/recibosPoliza.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					refPoliza = StringUtils.nullToString(request.getParameter("refPoliza"));
			}else if (path.equals("/declaracionesModificacionPoliza.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("poliza.idpoliza"));
			}else if (path.equals("/coberturasModificacionPoliza.html")) {
				idAnexo = StringUtils.nullToString(request.getParameter("idAnexo"));
				if("".equals(idAnexo))
					idAnexo = StringUtils.nullToString(request.getParameter("id"));
			}else if (path.equals("/parcelasAnexoModificacion.html")) {
				idAnexo = StringUtils.nullToString(request.getParameter("idAnexo"));
				if("".equals(idAnexo))
					idAnexo = StringUtils.nullToString(request.getParameter("parcela.anexoModificacion.id"));
			}else if (path.equals("/subvencionAseguradoAnexoMod.html")) {
				idAnexo = StringUtils.nullToString(request.getParameter("idAnexoModificacion"));
				if("".equals(idAnexo))
					idAnexo = StringUtils.nullToString(request.getParameter("poliza.idpoliza"));
			}else if (path.equals("/utilidadesPoliza.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("polizaOperacion"));
				if("".equals(idPoliza))
					idPoliza = null;
			}else if (path.equals("/datosParcelaAnexo.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if (path.equals("/datosParcela.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if(path.equals("/pagoPoliza.html")){
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("poliza.idpoliza"));
			}else if(path.equals("/revProduccionPrecio.html")){
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));				
			}else if (path.equals("/socioSubvencion.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if (path.equals("/grabacionPoliza.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if (path.equals("/polizaController.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if (path.equals("/webservices.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}else if (path.equals("/seleccionPoliza.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));				
			}else if (path.equals("/socio.html")) {
				idAsegurado = StringUtils.nullToString(request.getParameter("idAsegurado"));				
			}else if (path.equals("/informes.html")) {
				idPoliza = StringUtils.nullToString(request.getParameter("idPoliza"));
				idAnexo = StringUtils.nullToString(request.getParameter("idAnexo"));
				idRedCapital = StringUtils.nullToString(request.getParameter("idReduccionCapital"));
				idSiniestro = StringUtils.nullToString(request.getParameter("idSiniestro"));
			}else if(path.equals("/polizaComplementaria.html")){
				idPoliza = StringUtils.nullToString(request.getParameter("idpolizaCpl"));
				if("".equals(idPoliza))
					idPoliza = StringUtils.nullToString(request.getParameter("parcela.poliza.idpoliza"));
				if("".equals(idPoliza))
					refPoliza = StringUtils.nullToString(request.getParameter("refPol"));
			}else if(path.equals("/webservicesCpl.html")){
				idPoliza = StringUtils.nullToString(request.getParameter("idpoliza"));
			}
			
			if(StringUtils.nullToString(idPoliza).equals("") && !StringUtils.nullToString(refPoliza).equals("")){
				if(!tieneAccesoAPolizaByRefPoliza(usuario, refPoliza))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}else if(!StringUtils.nullToString(idPoliza).equals("")){
				if (!tieneAccesoAPolizaByIdPoliza(usuario, idPoliza))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}else if (!StringUtils.nullToString(idAnexo).equals("")) {
				if(!tieneAccesoAAnexo(usuario, idAnexo))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}else if(!StringUtils.nullToString(idAsegurado).equals("")){
				if(!tieneAccesoAAsegurado(usuario, idAsegurado))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}else if (!StringUtils.nullToString(idSiniestro).equals("")) {
				if(!tieneAccesoASiniestro(usuario, idSiniestro))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}else if (!StringUtils.nullToString(idRedCapital).equals("")) {
				if(!tieneAccesoARedCapital(usuario, idRedCapital))
					throw new SecurityLayerException("No tiene acceso a la operacion solicitada");
			}
			
			return;
		}
	}
	
	private static boolean tieneAccesoAPolizaByIdPoliza(Usuario usuario, String idPoliza) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoAPolizaByIdPoliza(usuario, idPoliza);
		return resultado;
	}
	
	private static boolean tieneAccesoAPolizaByRefPoliza(Usuario usuario, String refPoliza) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoAPolizaByRefPoliza(usuario, refPoliza);
		return resultado;
	}
	
	private static boolean tieneAccesoAAnexo(Usuario usuario, String idAnexo) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoAAnexo(usuario, idAnexo);
		return resultado;
	}
	
	private static boolean tieneAccesoAAsegurado(Usuario usuario, String idAsegurado) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoAAsegurado(usuario, idAsegurado);
		return resultado;
	}
	
	private static boolean tieneAccesoASiniestro(Usuario usuario, String idSiniestro) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoASiniestro(usuario, idSiniestro);
		return resultado;
	}
	
	private static boolean tieneAccesoARedCapital(Usuario usuario, String idRedCapital) throws DAOException{
		boolean resultado = false;
		resultado = SecurityLayer.polizaDao.tieneAccesoARedCapital(usuario, idRedCapital);
		return resultado;
	}

	public synchronized void setPolizaDao(IPolizaDao polizaDao) {
		SecurityLayer.polizaDao = polizaDao;
	}
}