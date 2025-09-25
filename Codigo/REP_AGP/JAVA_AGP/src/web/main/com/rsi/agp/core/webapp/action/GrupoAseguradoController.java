package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.GrupoAseguradoManager;
import com.rsi.agp.core.webapp.util.HTMLUtils;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.cesp.GrupoAseguradoCe;

public class GrupoAseguradoController extends BaseSimpleController implements Controller {

	private static final Log LOGGER = LogFactory.getLog(GrupoAseguradoController.class);
	private GrupoAseguradoManager grupoAseguradoManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");

	public GrupoAseguradoController() {
		super();
		setCommandClass(GrupoAseguradoCe.class);
		setCommandName("grupoAseguradoBean"); 
	}
	
	@Override
	protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object, final BindException exception) {
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		List<GrupoAseguradoCe> listaGrupoAsegurado = null; 
		
		GrupoAseguradoCe grupoAseguradoBean = (GrupoAseguradoCe) object;
		GrupoAseguradoCe comprobarGrupo = null;
		GrupoAseguradoCe grupoAseguradoBusqueda = new GrupoAseguradoCe();
		
		final String operacion = request.getParameter("operacion"); 

		
		if("alta".equalsIgnoreCase(operacion)){
		
			comprobarGrupo=	grupoAseguradoManager.getObjet(grupoAseguradoBean);

			if(comprobarGrupo!=null){
				
				parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
			
			}else{
			
				grupoAseguradoManager.saveCodGrupo(grupoAseguradoBean);
				parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
				parameters.put("activarModoModificar", true);
			
			}
			
			grupoAseguradoBusqueda = grupoAseguradoBean;
		}else if("baja".equalsIgnoreCase(operacion)){
			grupoAseguradoManager.dropObjet(grupoAseguradoBean.getCodgrupoaseg());
			parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
			grupoAseguradoBean = new GrupoAseguradoCe();
			grupoAseguradoBusqueda = new GrupoAseguradoCe();			
		}else if("modificar".equalsIgnoreCase(operacion)){
			grupoAseguradoManager.saveCodGrupo(grupoAseguradoBean);		
			
			parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
			parameters.put("activarModoModificar", true);
			grupoAseguradoBusqueda = grupoAseguradoBean;
		}
		//Consultar y limpiar
		else{
			listaGrupoAsegurado = grupoAseguradoManager.getListAsegurados(grupoAseguradoBean);
			grupoAseguradoBusqueda = grupoAseguradoBean; 
		}
		
		final ModelAndView resultado = new ModelAndView("moduloTaller/condicionesEspeciales/grupoAsegurado", "grupoAseguradoBean",grupoAseguradoBean);
		listaGrupoAsegurado = grupoAseguradoManager.getListAsegurados(grupoAseguradoBusqueda); 
		parameters.put("listaGrupoAsegurado", listaGrupoAsegurado);
		resultado.addAllObjects(parameters);		

		return resultado;
	}

	
	
	public void setGrupoAseguradoManager(GrupoAseguradoManager grupoAseguradoManager) {
		this.grupoAseguradoManager = grupoAseguradoManager;
	}

	

}
