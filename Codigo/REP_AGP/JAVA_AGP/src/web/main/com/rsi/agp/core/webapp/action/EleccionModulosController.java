package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.managers.impl.EleccionModulosManager;
import com.rsi.agp.core.webapp.util.StringUtils;

public class EleccionModulosController extends BaseSimpleController implements Controller {
	
	private static final Log LOGGER = LogFactory.getLog(EleccionModulosController.class);
	private EleccionModulosManager elecModulosManager;

	public EleccionModulosController ()
	{
		setCommandClass(String.class);
		setCommandName("string");
	}
	
	@Override
	protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,
			final BindException exception) {
		ModelAndView mv = null;	
		String accion = StringUtils.nullToString(request.getParameter("action"));
		
		if (accion.equalsIgnoreCase("comparativa"))
		{
			String[] modSelec = request.getParameterValues("");
			mv = handleComparativa (modSelec);
		} else if (accion.equalsIgnoreCase("seleccionComp")) {
			String seleccionados = StringUtils.nullToString(request.getParameter("seleccionados"));
			mv = handleSeleccionComparativa (seleccionados);
		}
		else
		{
			mv = inicio (request);
		}
		
		return mv;
	}
	
	private ModelAndView inicio (HttpServletRequest request)//eleccion modulos
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		return new ModelAndView("moduloPolizas/modulos/elecmodulos", "detCoberturas", parameters);		
	}
	
	private ModelAndView handleComparativa (String[] modSelec)//seleccionComparativas
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters = elecModulosManager.consultaComparativas(modSelec);
		return new ModelAndView("moduloPolizas/modulos/seleccionComparativas", "listaModulos", parameters);
	}
	
	private ModelAndView handleSeleccionComparativa (String seleccionComp)//resultadoComparativas
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters = elecModulosManager.trataSeleccion(seleccionComp);
		return new ModelAndView("moduloPolizas/modulos/resultadoComparativas", "listaSeleccion", parameters);
	}
	
	public void setElecModulosManager(EleccionModulosManager elecModulosManager) {
		this.elecModulosManager = elecModulosManager;
	}
	

}
