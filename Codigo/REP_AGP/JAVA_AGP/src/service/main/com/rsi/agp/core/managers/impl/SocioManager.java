package com.rsi.agp.core.managers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.admin.impl.SocioFiltro;
import com.rsi.agp.dao.models.admin.ISocioDao;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.admin.SocioId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;

public class SocioManager implements IManager {
	private static final Log LOGGER = LogFactory.getLog(SocioManager.class);
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	protected ISocioDao socioDao;

	@SuppressWarnings("unchecked")
	public final List<Socio> getSocios(final Socio socioBean) {
		final SocioFiltro filter = new SocioFiltro(socioBean);
		return socioDao.getObjects(filter);
	}

	public final Socio getSocio(final String nif, final Long idAsegurado) {
		final SocioId socioId = new SocioId(nif, idAsegurado);
		return (Socio)socioDao.getObject(Socio.class, socioId);
	}

	public final void saveSocio(final Socio socioBean) {
		try {
			socioDao.saveOrUpdate(socioBean);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar el socio",e);
		}
	}
	
	public final void savePolizaSocio(final PolizaSocio polizaSocioBean) {
		
		// Carga en el objeto el siguiente orden del socio en la poliza
		try {
			polizaSocioBean.setOrden(socioDao.getOrdenPolizaSocio(polizaSocioBean.getPoliza().getIdpoliza()));
		}
		catch (Exception ex) {
			LOGGER.error("Se ha producido un error al obtener el orden del socio en la poliza", ex);
		}
		
		try {
			socioDao.saveOrUpdate(polizaSocioBean);
		} catch (DAOException e) {
			LOGGER.error("Se ha producido un error al guardar la poliza del socio",e);
		}
	}
	
	public final void dropSubvencionSocio(final SubvencionSocio subvencionSocio) throws BusinessException {
		try {
			socioDao.delete(subvencionSocio);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
	}
	
	public final void dropPolizaSocio(final PolizaSocio polizaSocio) throws BusinessException {
		try {
			socioDao.delete(polizaSocio);
			// Actualiza el orden de los socios correspondientes al idpoliza del registro que se acaba de borrar
			socioDao.actualizaOrdenPolizaSocio(polizaSocio.getPoliza().getIdpoliza());
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
	}
	
	public final void saveSubvencionesSocio(final List<SubvencionSocio> listSubvencionSocios) throws BusinessException {
		try {
			socioDao.saveOrUpdateList(listSubvencionSocios);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
	}

	public final void dropDatoSocio(final SocioId id) {
		socioDao.removeObject(Socio.class, id);
	}

	public final List<Poliza> getPolizasSinGrabarByIdAsegurado(final Long id) throws BusinessException {
		try {
			return socioDao.getPolizasSinGrabarByIdAsegurado(id);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
	}
	
	public final List<PolizaSocio> getPolizasByIdSocio(SocioId socioId) throws BusinessException {
		try {
			return socioDao.getPolizasByIdSocio(socioId);
		} catch (DAOException dao) {
			LOGGER.error("Se ha producido un error al acceder a base de datos ", dao);
			throw new BusinessException("Se ha producido un error al acceder a base de datos ", dao);
		}
				
	}
	
	public final List<Poliza> filtrarPolizasSinGrabarByIdAsegurado(List<PolizaSocio> listPolizasAsegurado) {
		List<Poliza> listRes = new ArrayList<Poliza>();

		for (PolizaSocio pol:listPolizasAsegurado){
			if ((pol.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)) || 
				(pol.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))){
				listRes.add(pol.getPoliza());
			}
		}
		
		return listRes;		
	}
	
	public String getTablePolizasSinGrabar(List<Poliza> listaPolizasSinGrabar, String mensaje) {
		StringBuilder table = new StringBuilder();
			
    	table.append("<div style='color:black;border:1px solid #DD3C10;font-size:12px;text-align:center;");
    	table.append("font-size:12px;line-height:20px;background-color:#FFEBE8;'>");
    	table.append(mensaje);
    	table.append("</div>");
    	table.append("<br/>");
    	
    	table.append("<table class='LISTA'>");
    	table.append("<thead>");
    	table.append("<th class='cblistaImg'>Nif Aseg.</th>");
    	table.append("<th class='cblistaImg'>Id Colect.</th>");
    	table.append("<th class='cblistaImg'>Plan</th>");
    	table.append("<th class='cblistaImg'>Linea</th>");
    	table.append("</thead>");

    	for(int i=0; i < listaPolizasSinGrabar.size(); i++ )
    	{
    		Poliza poliza = listaPolizasSinGrabar.get(i);
    		table.append("<tr>");
    		table.append("<td class='literal'>" + 
    				poliza.getAsegurado().getNifcif() + "</td><td class='literal'>" + 
    				poliza.getColectivo().getIdcolectivo() + "</td><td class='literal'>" + 
    				poliza.getLinea().getCodplan()+"</td><td class='literal'>" + 
    				poliza.getLinea().getCodlinea() + "</td>");
    		table.append("</tr>");
    	}
    	
    	table.append("</table>");  	
    	    	
    	return table.toString();
	}

	public final void setSocioDao(final ISocioDao socioDao) {
		this.socioDao = socioDao;
	}
	
	public Asegurado getDatosAsegurado (Long idAsegurado)
	{
		Asegurado as = (Asegurado)socioDao.getObject(Asegurado.class, idAsegurado);
		return as;
	}

	public void esSocioConSubvs(String displayPopUpPolizas, Map<String, Object> params,
			List<PolizaSocio> listaSociosAsociados, String idAseguradoBorrar, String cifSocioBorrar) throws Exception {
		
		List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
		try{
			//Si el socio tiene polizas con estado "Pendiente de validacion" y "grabacion provisional"
			//se muestra un pop-up con un listado de dichas polizas
			if (!displayPopUpPolizas.equals("true")){
				listaPolizasSinGrabar = this.filtrarPolizasSinGrabarByIdAsegurado(listaSociosAsociados);
			}
				
			if ((!displayPopUpPolizas.equals("true")) && (listaPolizasSinGrabar.size() > 0)){
				String tableSociosSinGrabar = this.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
						bundle.getString("mensaje.poliza.socio.baja.polizasSinGrabar"));
				params.put("popUpPolizas", "true");
				params.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
				params.put("idAseguradoBaja", idAseguradoBorrar);				
				params.put("nifcifBaja", cifSocioBorrar);
				//params.put("operacion", operacion);
			} else {
				//Si el socio tiene polizas asociadas, no se borran físicamente los datos
				Socio socioBaja = this.getSocio(cifSocioBorrar, new Long(idAseguradoBorrar));
				socioBaja.setBaja('S');
				
				//Si el socio tiene alguna subvencion asociada en un polizas con estado "Pendiente de validacion" y
				//"grabacion provisional", se borran dichas subvenciones y se muestra un aviso
				List<SubvencionSocio> listSubvencionSocios = new ArrayList<SubvencionSocio>();
				Set<SubvencionSocio> subvencionesAux = new HashSet<SubvencionSocio>(socioBaja.getSubvencionSocios());
				
				//Se recorre un set auxiliar y se copian a una lista las subvenciones que hay que borrar para evitar concurrencia
				Iterator<SubvencionSocio> it = subvencionesAux.iterator();
				while(it.hasNext()){
					SubvencionSocio subvSocio = it.next();
					if ((subvSocio.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)) ||
						(subvSocio.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))){
						listSubvencionSocios.add(subvSocio);
						socioBaja.getSubvencionSocios().remove(subvSocio);
					}
				}
				
				for (SubvencionSocio subvAux:listSubvencionSocios){
					this.dropSubvencionSocio(subvAux);
				}
				
				socioBaja.setSubvencionSocios(subvencionesAux);				
				this.saveSocio(socioBaja);
				
				//Si el socio tiene alguna poliza asociada en estado "Pendiente de validacion" y
				//"grabacion provisional", se borra el socio de dichas polizas
				for (PolizaSocio polizaSocioAux: listaSociosAsociados){
					if ((polizaSocioAux.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION)) ||
						(polizaSocioAux.getPoliza().getEstadoPoliza().getIdestado().equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL))){
						this.dropPolizaSocio(polizaSocioAux);
					}
				}					
				params.put("mensaje", bundle.getString("mensaje.baja.OK"));	
			}	
		}catch (Exception e) {
			LOGGER.error("Error al comprobar las subvenciones del socio - esSocioConSubvs()" + e);
			throw e;
		}
		
	}

	public void actualizaAsegSesion(Usuario user, Socio socioBean) throws Exception {
		try{
			//si el socio que estamos dando de baja es del asegurado de sesion, actualizamos el asegurado de sesion
			if ((user.getAsegurado() != null) && (socioBean.getId().getIdasegurado().equals(user.getAsegurado().getId()))){
				user.setAsegurado(this.getDatosAsegurado(socioBean.getId().getIdasegurado()));
			}
		}catch (Exception e) {
			LOGGER.error("Error al actualizar el asegurado de sesion  - actualizaAsegSesion()" + e);
			throw e;
		}
	}

	public void altaSocio(String displayPopUpPolizas,Map<String, Object> params,
			Usuario user,Socio socioBean) throws Exception {

		boolean socioGrabado = false;
		List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
		try{
			listaPolizasSinGrabar = this.getPolizasSinGrabarByIdAsegurado(socioBean.getId().getIdasegurado());	
			
			if (listaPolizasSinGrabar != null && !listaPolizasSinGrabar.isEmpty()){
				if (displayPopUpPolizas.equals("true")){
					this.saveSocio(socioBean);
					
					for (Poliza poliza : listaPolizasSinGrabar){
						PolizaSocio polizaSocio = new PolizaSocio();
						polizaSocio.setPoliza(poliza);
						polizaSocio.setSocio(socioBean);
						this.savePolizaSocio(polizaSocio);
					}
					
					socioGrabado = true;												
				} else {
					String tableSociosSinGrabar = this.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
							bundle.getString("mensaje.poliza.socio.alta.polizasSinGrabar"));
					params.put("popUpPolizas", "true");
					params.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
					//params.put("operacion", operacion);
				}
			} else {
				this.saveSocio(socioBean);							
				socioGrabado = true;
			}
			
			if (socioGrabado){
				params.put("mensaje", bundle.getString("mensaje.alta.OK"));
				this.actualizaAsegSesion(user,socioBean);
			}					
		}catch (Exception e) {
			LOGGER.error("Error al dar de alta un socio - altaSocio()" + e);
			throw e;
		}
	}

	public void deshazSocio(String displayPopUpPolizas, Map<String, Object> params, Socio socioBean,
			Usuario user, String idAseguradoDeshacer, String cifSocioDeshacer, Socio socioBaja) throws Exception {

		List<Poliza> listaPolizasSinGrabar = new ArrayList<Poliza>();
		boolean socioRecuperado = false;
		try{
			listaPolizasSinGrabar = this.getPolizasSinGrabarByIdAsegurado(new Long(idAseguradoDeshacer));	
			
			if (listaPolizasSinGrabar != null && !listaPolizasSinGrabar.isEmpty()){
				
				if (displayPopUpPolizas.equals("true")){
					for (Poliza poliza : listaPolizasSinGrabar){
						PolizaSocio polizaSocio = new PolizaSocio();
						polizaSocio.setPoliza(poliza);
						polizaSocio.setSocio(socioBaja);
						this.savePolizaSocio(polizaSocio);
					}
					
					socioRecuperado = true;							
				} else {
					String tableSociosSinGrabar = this.getTablePolizasSinGrabar(listaPolizasSinGrabar, 
							bundle.getString("mensaje.poliza.socio.alta.polizasSinGrabar"));
					params.put("popUpPolizas", "true");
					params.put("tableInfoPolizasSinGrabar", tableSociosSinGrabar);
					params.put("idAseguradoBaja", idAseguradoDeshacer);				
					params.put("nifcifBaja", cifSocioDeshacer);
				}
			} else {
				socioRecuperado = true;		
			}			
			if (socioRecuperado){
				socioBaja.setBaja('N');
				this.saveSocio(socioBaja);
				params.put("mensaje", bundle.getString("mensaje.deshacer.OK"));
				
				this.actualizaAsegSesion(user, socioBean);
			}
		}catch (Exception e) {
			LOGGER.error("Error al deshacer un socio - deshazSocio()" + e);
			throw e;
		}
		
	}	

}
