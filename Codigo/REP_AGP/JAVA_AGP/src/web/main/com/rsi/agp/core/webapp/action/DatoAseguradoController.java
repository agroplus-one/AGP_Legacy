package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.managers.IHistoricoEstadosManager;
import com.rsi.agp.core.managers.impl.DatoAseguradoManager;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.managers.impl.SocioManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.DatoAsegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class DatoAseguradoController extends MultiActionController{

	private static final Log LOGGER = LogFactory.getLog(DatoAseguradoController.class); 
	
	private DatoAseguradoManager datoAseguradoManager;
	private EntidadManager entidadManager;
	private SocioManager socioManager;
	private IHistoricoEstadosManager historicoEstadosManager;
	
	
	private final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	public ModelAndView doConsulta(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
				
		Map<String, Object> params = new HashMap<String, Object>();
		ModelAndView mv = null;
		String listaEnt="";
		BigDecimal codEntidad = null;
		boolean isCRM = false;
		List<DatoAsegurado> listaDatosAsegurado = new ArrayList<DatoAsegurado>();
		List<DatoAsegurado> listaDatosAsegurado999 = new ArrayList<DatoAsegurado>();
		
		try{		
			LOGGER.debug("Inicio consulta de datos asegurados");
			String lineaGen =  StringUtils.nullToString(request.getParameter("lineaGen"));
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			datoAseguradoBean.getAsegurado().setId(idAsegurado);
			String origenLlamada = StringUtils.nullToString(request.getParameter("method"));
			String cargaAseg =  StringUtils.nullToString(request.getParameter("cargaAseg"));
			params.put("cargaAseg", cargaAseg);
			if (!origenLlamada.equals("")){
				datoAseguradoBean.getLineaCondicionado().
					setCodlinea(this.getCodLinea(datoAseguradoBean, request));
			}else{
				datoAseguradoBean = new DatoAsegurado();
			}
			
			if (null == datoAseguradoBean.getAsegurado().getNifcif() || 
					null == datoAseguradoBean.getAsegurado().getEntidad().getCodentidad()) {
				datoAseguradoBean.setAsegurado(datoAseguradoManager.getAseguradoById(idAsegurado));
				codEntidad = datoAseguradoBean.getAsegurado().getEntidad().getCodentidad();
			}
			isCRM = entidadManager.isCRM(codEntidad);
			if(isCRM)	
				listaEnt = entidadManager.getEntidadesGrupoCRM();
			
			DatoAsegurado dabusqueda = new DatoAsegurado();
			
			if (origenLlamada.equals("actualizaIbanPolizasAseg")){
				
				dabusqueda.getAsegurado().setId(idAsegurado);
			}else{
				dabusqueda.getAsegurado().setId(idAsegurado);
				dabusqueda.getLineaCondicionado().setCodlinea(datoAseguradoBean.getLineaCondicionado().getCodlinea());
			}

			listaDatosAsegurado = datoAseguradoManager.getDatosAsegurado(dabusqueda);
			
			/**/
			dabusqueda.getLineaCondicionado().setCodlinea(new BigDecimal(999));
			listaDatosAsegurado999 = datoAseguradoManager.getDatosAsegurado(dabusqueda);
			
			params.put("listaEnt", listaEnt);
			params.put("listaDatosAsegurado", listaDatosAsegurado);
			params.put("asegurado", socioManager.getDatosAsegurado(idAsegurado));
			params.put("isCRM", isCRM);
			params.put("idAsegurado", idAsegurado);
			params.put("lineaGen", lineaGen);
			if (listaDatosAsegurado999.size() == 0){
				datoAseguradoBean.getLineaCondicionado().setCodlinea(new BigDecimal(999));
				params.put("linea999", "linea999");
			}else {
				
				params.put("linea999", "");
			}
			params.put("titCuenta",datoAseguradoBean.getTitularCuenta());
			params.put("destDomic",datoAseguradoBean.getDestinatarioDomiciliacion());
			mv = new ModelAndView("moduloAdministracion/asegurados/datosAsegurado", 
					"datoAseguradoBean", datoAseguradoBean).addAllObjects(params);
			
			LOGGER.debug("Fin consulta de datos asegurados");
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error general: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.error.grave"));
			mv = new ModelAndView("moduloAdministracion/asegurados/datosAsegurado", 
					"datoAseguradoBean", new DatoAsegurado()).addAllObjects(params);
		}
		return mv;
	}


	public ModelAndView doAlta(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
		LOGGER.debug("Inicio doAlta de asegurados");
		ModelAndView mv = null;
		Map<String, Object> params = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		//List<DatoAsegurado> listaDatosAsegurado = new ArrayList<DatoAsegurado>();
		try{
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			datoAseguradoBean.getAsegurado().setId(idAsegurado);
			if (!this.datoAseguradoManager.existeDatoAsegurado(datoAseguradoBean.getAsegurado().getId(), 
					datoAseguradoBean.getLineaCondicionado().getCodlinea())) {
				
					datoAseguradoBean.setId(null);
		    		if (datoAseguradoManager.existeLineaCondicionado (datoAseguradoBean.getLineaCondicionado().getCodlinea())){
		    			
			    		try {
			    			
			    			datoAseguradoManager.saveDatoAsegurado(datoAseguradoBean);
			    			
	    					params.put("mensaje", bundle.getString("mensaje.alta.OK"));
	    					if (datoAseguradoBean.getLineaCondicionado().getCodlinea().compareTo(new BigDecimal(999))==0) 
	    						params.put("lineaGen", "999");
	    				
			    		}catch(Exception e) {
							params.put("alerta", bundle.getString("mensaje.error.grave"));
							params.remove("showPopupDatosAsegurados");
							return doConsulta(request,response,new DatoAsegurado()).addAllObjects(params);
						}
	    				
			    		//TMR 27-06-2013 llamada al PL para insertar el estado en el historico
	    				historicoEstadosManager.insertaEstadoDatosAseg(datoAseguradoBean.getId(),
									usuario.getCodusuario(), Constants.ALTA);
							
						if (usuario.getAsegurado() != null){
							//Si todo correcto y  hay un asegurado en sesion qtiene el mismo identificador, le asigno la cuenta	
							Asegurado aseguradoSesion = datoAseguradoManager.getAseguradoById(usuario.getAsegurado().getId());
							LOGGER.debug("Se ha recogido el asegurado en sesion con id");
							if (aseguradoSesion != null && aseguradoSesion.getId().equals(idAsegurado)){
									aseguradoSesion.getDatoAsegurados().add(datoAseguradoBean);
									usuario.setAsegurado(aseguradoSesion);
									request.getSession().setAttribute("usuario", usuario);
							}
						}
						// Pet P19699 : Mostrar polizas en popup del asegurado
						//datoAseguradoManager.showPopupDatosAsegurados(this.getCodLinea(datoAseguradoBean, request), idAsegurado,params);
					
						if (null != request.getParameter("volver")&& "volver".equals(request.getParameter("volver"))){		
							request.getSession().setAttribute("usuario", usuario);
							return new ModelAndView("redirect:/cargaAsegurado.html").addObject("idAsegurado", idAsegurado);
						}
		    		}else{
		    			params.put("alerta", bundle.getString("mensaje.datoAsegurado.linea.KO"));
		    		}
					
			}else{
				params.put("alerta", bundle.getString("mensaje.asegurado.ccc.existe.KO"));
			}
			BigDecimal codLinea = this.getCodLinea(datoAseguradoBean, request);
			this.datoAseguradoManager.showPopupDatosAsegurados(codLinea, idAsegurado, params);
			params.put("origenLlamada", "doAlta");
			mv = doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error en el alta: " + e.getMessage());
			params.put("alerta",  bundle.getString("mensaje.alta.generico.KO"));
			return doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}
		return mv;
	
	}
	
	public ModelAndView doModificar(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
		
		ModelAndView mv = null;
		Map<String, Object> params = new HashMap<String, Object>();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		try{
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			datoAseguradoBean.getAsegurado().setId(idAsegurado);			
			boolean actualizandoYduplicando=datoAseguradoManager.duplicaLinea(idAsegurado,datoAseguradoBean.getId(), datoAseguradoBean.getLineaCondicionado().getCodlinea());

			//JANV  19-04-2016
			//para modificar no hace falta la comprobación. 
			//Se añade otra que evitar excepción por violación de fk
			if(!actualizandoYduplicando){
			//if (datoAseguradoManager.existeLineaCondicionado (datoAseguradoBean.getLineaCondicionado().getCodlinea())){
					datoAseguradoBean.setFechaModificacion(new Date());					
					datoAseguradoManager.saveDatoAsegurado(datoAseguradoBean);
					historicoEstadosManager.insertaEstadoDatosAseg(datoAseguradoBean.getId(),
								usuario.getCodusuario(), Constants.MODIFICACION);
					params.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
					if (datoAseguradoBean.getLineaCondicionado().getCodlinea().compareTo(new BigDecimal(999))==0) 
						params.put("lineaGen", "999");
				}else{
					params.put("alerta", bundle.getString("mensaje.asegurado.ccc.existe.KO"));
				}
			//}else{
    		//	params.put("alerta", bundle.getString("mensaje.datoAsegurado.linea.KO"));
    		//}
			// Pet P19699 : Mostrar polizas en popup del asegurado
			datoAseguradoManager.showPopupDatosAsegurados(this.getCodLinea(datoAseguradoBean, request), idAsegurado,params);
			
			params.put("origenLlamada", "doModificar");
			mv = doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}catch (Exception e) {
			LOGGER.error("Se ha producido un error en la modificacion: " + e.getMessage());
			params.put("alerta", bundle.getString("mensaje.error.grave"));
			return doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doBaja(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
		
		ModelAndView mv = null;
		Map<String, Object> params = new HashMap<String, Object>();
		
		try{
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			String lineaCondicionadoBaja = StringUtils.nullToString(request.getParameter("lineaCondicionadoBaja"));
			datoAseguradoBean.getAsegurado().setId(idAsegurado);
			if (lineaCondicionadoBaja.equals("999")) 
			{
				final List<Poliza> listaPolizasAsociadas = datoAseguradoManager.getPolizasByIdAsegurado(idAsegurado,null,null);
				if (null == listaPolizasAsociadas || listaPolizasAsociadas.isEmpty()){
					datoAseguradoManager.dropDatoAsegurado(datoAseguradoBean.getId());
					params.put("mensaje", bundle.getString("mensaje.baja.OK"));
				} 
				else{
					params.put("alerta", bundle.getString("mensaje.baja.con.polizas.KO"));
				}
			} 
			else 
			{
				datoAseguradoManager.dropDatoAsegurado(datoAseguradoBean.getId());
				params.put("mensaje", bundle.getString("mensaje.baja.OK"));
			}
		params.put("origenLlamada", "doBaja");	
		datoAseguradoBean = new DatoAsegurado();
		mv = doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}catch (Exception e) {
			logger.error("Error al eliminar el dato asegurado", e);
			params.put("alerta", bundle.getString("mensaje.baja.KO"));
			return doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	public ModelAndView doLimpiar(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
		
		ModelAndView mv = null;
		Map<String, Object> params = new HashMap<String, Object>();
		
		try{
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			datoAseguradoBean = new DatoAsegurado();
			datoAseguradoBean.getAsegurado().setId(idAsegurado);
			mv = doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		
		}catch (Exception e) {
			logger.error("Error al limìar los datos asegurado", e);
			params.put("alerta", bundle.getString("mensaje.error.grave"));
			return doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	
	public ModelAndView actualizaIbanPolizasAseg(HttpServletRequest request, 
			HttpServletResponse response, DatoAsegurado datoAseguradoBean){
		
		ModelAndView mv = null;
		Map<String, Object> params = new HashMap<String, Object>();
		
		try{
			Long idAsegurado = Long.parseLong(request.getParameter("idAsegurado"));
			datoAseguradoBean.getAsegurado().setId(idAsegurado);
			
			datoAseguradoManager.actualizaIbanPolizasAseg(idAsegurado,
						this.getCodLinea(datoAseguradoBean, request),request,datoAseguradoBean);
			params.put("mensaje", bundle.getString("mensaje.actualiza.iban.polizas.aseg.OK"));
			params.put("origenLlamada", "actualizaIbanPolizasAseg");
			mv = doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		
		}catch (Exception e) {
			LOGGER.error("Error al actualizar el iban en las polizas del asegurado", e);
			params.put("alerta",  bundle.getString("mensaje.actualiza.iban.polizas.aseg.KO"));
			return doConsulta(request, response, datoAseguradoBean).addAllObjects(params);
		}
		return mv;
	}
	
	private BigDecimal getCodLinea(DatoAsegurado datoAseguradoBean,HttpServletRequest request) {
		BigDecimal codLinea = datoAseguradoBean.getLineaCondicionado().getCodlinea();
		if (null == codLinea) {
			final String cadenaCodLinea = request.getParameter("codLinea");
			if (null != cadenaCodLinea) {
				codLinea = new BigDecimal(cadenaCodLinea);
			}
		}
		datoAseguradoBean.getLineaCondicionado().setCodlinea(codLinea);
		return codLinea;
	}
	
	public final void setDatoAseguradoManager(final DatoAseguradoManager datoAseguradoManager) {
		this.datoAseguradoManager = datoAseguradoManager;
	}

	public void setSocioManager(SocioManager socioManager) {
		this.socioManager = socioManager;
	}

	public void setEntidadManager(EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}

	public void setHistoricoEstadosManager(
			IHistoricoEstadosManager historicoEstadosManager) {
		this.historicoEstadosManager = historicoEstadosManager;
	}
}