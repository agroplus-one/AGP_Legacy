package com.rsi.agp.core.managers.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.managers.IBaseManager;
import com.rsi.agp.dao.models.commons.IUserDao;
import com.rsi.agp.dao.tables.admin.Clase;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.config.DatosCabecera;
import com.rsi.agp.dao.tables.cpl.Medida;
import com.rsi.agp.dao.tables.cpl.gan.MedidaG;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class BaseManager implements IBaseManager {
	private SeleccionPolizaManager seleccionPolizaManager;	
	private static final Log LOGGER = LogFactory.getLog(ConsultaDetallePolizaManager.class);	
	private IUserDao usuarioDao;
	private ClaseManager claseManager;
	/**
	 * Método que carga los datos de la cabecera correspondientes al usuario de la póliza, no al usuario de sesión
	 */
	public void cargaCabecera(Poliza polizaBean, HttpServletRequest request) throws Exception {

		DatosCabecera datos = new DatosCabecera();
		String nombre="";
		
		try{
			
			if (polizaBean.getAsegurado().getTipoidentificacion().equals("CIF"))
				nombre = polizaBean.getAsegurado().getRazonsocial();
			else
				nombre = polizaBean.getAsegurado().getNombre()+" "+polizaBean.getAsegurado().getApellido1()+" "+polizaBean.getAsegurado().getApellido2();
			
			datos.setAsegurado(nombre);
			datos.setNifCif(polizaBean.getAsegurado().getNifcif());
			datos.setColectivo(polizaBean.getColectivo().getNomcolectivo());
			datos.setPlanLinea(polizaBean.getLinea().getCodplan()+"/"+polizaBean.getLinea().getCodlinea());
			if(null!=polizaBean.getClase())
				datos.setClase(polizaBean.getClase().toString());
			
			Usuario usuario = polizaBean.getUsuario();
			
			datos.setUsuario(usuario.getNombreusu());
			datos.setEntidad(polizaBean.getColectivo().getTomador().getId().getCodentidad().toString()+" "+
					polizaBean.getAsegurado().getEntidad().getNomentidad());			
			
			Clase clase = claseManager.getClase(polizaBean);
			//calculamos el intervalo de CoefReduccRdto
			if (clase != null){
				datos.setClase(clase.getClase().toString());
				Usuario usu = new Usuario();
				usu.setAsegurado(polizaBean.getAsegurado());
				usu.setColectivo(polizaBean.getColectivo());
				String intervaloCoefReduccionRdtoStr = seleccionPolizaManager.calcularIntervaloCoefReduccionRdtoPoliza(usu,clase.getId());
				request.getSession().setAttribute("intervaloCoefReduccionRdto", intervaloCoefReduccionRdtoStr);
			}
			
			String nifcif = polizaBean.getAsegurado().getNifcif();
			Long lineaseguroid = polizaBean.getLinea().getLineaseguroid();
			Object medida;
			
			if (polizaBean.getLinea().isLineaGanado()) {
				medida = claseManager.calcularMedidaGanadoAsegurado(clase, nifcif);
			} else {
				medida = usuarioDao.getMedida(lineaseguroid, nifcif);
			}
			
			if (medida == null) {
				medida = polizaBean.getLinea().isLineaGanado() ?  new MedidaG() : new Medida();
			}
			
			request.getSession().setAttribute("datosCabecera", datos);
			request.getSession().setAttribute("medida", medida);
		}catch(Exception e){
			LOGGER.error("Error al cargar los datos de la cabecera" + e.getMessage());
			throw e;
		}
	}
	
	public void cargaCabecera(Long idPoliza, HttpServletRequest request) throws Exception {
		Poliza polizaBean=null;
		try{
			polizaBean=seleccionPolizaManager.getPolizaById(idPoliza);
			this.cargaCabecera(polizaBean, request);
		}catch(Exception e){
			LOGGER.error("Error al cargar los datos de la cabecera" + e.getMessage());
			throw e;
		}
	}	
	
	public void setSeleccionPolizaManager(
			SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}
	
	public void setUsuarioDao(IUserDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}
	
	public void setClaseManager(ClaseManager claseManager) {
		this.claseManager = claseManager;
	}	
}