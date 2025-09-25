package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.rsi.agp.core.managers.impl.TomadorManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.admin.TomadorId;
import com.rsi.agp.dao.tables.commons.Usuario;

public class TomadorController extends BaseSimpleController implements Controller {
	private TomadorManager tomadorManager;
	
	public TomadorController() {
		super();
		setCommandClass(Tomador.class);
		setCommandName("tomadorBean");
	}

	@Override
	protected final ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object,
			final BindException exception) {

		final Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		
		final Usuario user = (Usuario) request.getSession().getAttribute("usuario");
		Tomador tomadorBean = (Tomador) object;
		Tomador tomadorBusqueda = new Tomador();

		tomadorBean.setRazonsocial(StringUtils.normalizaRS(tomadorBean.getRazonsocial()));

		String operacion = request.getParameter("operacion");
		if ("alta".equalsIgnoreCase(operacion)) {
			tomadorBusqueda = tomadorManager.getTomador(tomadorBean.getId().getCodentidad(), tomadorBean.getId().getCiftomador());
			if (null == tomadorBusqueda) {
				//TMR 29-05-2012 facturacion
				ArrayList<Integer> errorTomador = tomadorManager.saveTomador(tomadorBean,user);
				ArrayList<String> erroresWeb = new ArrayList<String>();
				for (Integer valor: errorTomador)
				{
					switch (valor.intValue())
					{ 
	    				case 0: parameters.put("mensaje", bundle.getString("mensaje.alta.OK")); 
							    break;
						case 1: erroresWeb.add(bundle.getString("mensaje.tomador.entidad.inexistente.KO"));
								//parameters.put("alerta", bundle.getString("mensaje.tomador.entidad.inexistente.KO"));
							    break;
						case 2: parameters.put("alerta", bundle.getString("mensaje.tomador.via.KO"));//mensajes en properties
						        break;
						case 3: parameters.put("alerta", bundle.getString("mensaje.tomador.provincia.KO"));
								break;
						case 4: parameters.put("alerta", bundle.getString("mensaje.tomador.localidad.KO")); 
								break;
						case 5: parameters.put("alerta", bundle.getString("mensaje.tomador.sublocalidad.KO"));
								break;
						case 6: parameters.put("alerta", bundle.getString("mensaje.tomador.relacion.KO"));
								break;								
						default: parameters.put("alerta",bundle.getString("mensaje.error.grave"));	
					}
				}
				if (erroresWeb.size()>0)
				{
					parameters.put("alerta2", erroresWeb);
				}
			} else {
				parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
			}
			tomadorBusqueda = tomadorBean;
			parameters.put("activarModoModificar", true);
			
		} else if("baja".equalsIgnoreCase(operacion)) {
			final BigDecimal codEntidad = new BigDecimal(request.getParameter("codentidadAccion"));
			final String cifTomador = request.getParameter("ciftomadorAccion");			
			TomadorId idTomador = new TomadorId(codEntidad, cifTomador.trim());
			//TMR 30-05-2012 Facturacion. Añadimos el usuario
			int valorBorrado = tomadorManager.dropTomador(idTomador,user);
			if (valorBorrado == -1 || valorBorrado == -2) // si esa funcion devuelve -1 es porque el tomador tiene un colectivo que le hace referencia
			{
				if (valorBorrado == -1)
				{
					parameters.put("alerta",bundle.getString("mensaje.tomador.imposible.borrar.KO"));
				}
				else
				{
					parameters.put("alerta", bundle.getString("mensaje.baja.KO"));
				}
			}
			else
			{
				parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
			}
			
			tomadorBean = new Tomador();
			tomadorBusqueda = new Tomador();
		} else if ("modificar".equalsIgnoreCase(operacion)) {
				//TMR 29-05-2012 facturacion
				ArrayList<Integer> errorTomador = tomadorManager.saveTomador(tomadorBean,user);
				ArrayList<String> erroresWeb = new ArrayList<String>();				
				for (Integer valor: errorTomador)
				{
					switch (valor.intValue())
					{
	    				case 0: parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK")); 
							    break;
						case 1: erroresWeb.add(bundle.getString("mensaje.tomador.entidad.inexistente.KO"));
							    break;
						case 2: parameters.put("alerta", bundle.getString("mensaje.tomador.via.KO"));
						        break;
						case 3: parameters.put("alerta", bundle.getString("mensaje.tomador.provincia.KO"));
								break;
						case 4: parameters.put("alerta", bundle.getString("mensaje.tomador.localidad.KO")); 
								break;
						case 5: parameters.put("alerta", bundle.getString("mensaje.tomador.sublocalidad.KO"));
								break;
						case 6: parameters.put("alerta", bundle.getString("mensaje.tomador.relacion.KO"));
								break;								
						default: parameters.put("alerta",bundle.getString("mensaje.error.grave"));	
					}
				}
				if (erroresWeb.size()>0)
				{
					parameters.put("alerta2", erroresWeb);
				}				
			
			//Cuando modificamos el tomador, utilizamos ese mismo registro como filtro
			tomadorBusqueda = tomadorBean;
			parameters.put("activarModoModificar", true);
			
		} else if ("editar".equalsIgnoreCase(operacion)) {
			BigDecimal codEntidad = new BigDecimal(request.getParameter("codentidad"));
			String cifTomador = request.getParameter("ciftomador");
			tomadorBean = tomadorManager.getTomador(codEntidad, cifTomador);
		} else if ("limpiar".equalsIgnoreCase(operacion)) {
			//Esta operación es igual que cuando no viene el parámetro 'operacion'
			tomadorBusqueda = tomadorBean;
		}else if("imprimir".equalsIgnoreCase(operacion)){
				tomadorBusqueda = tomadorBean;
				List<Tomador> listaTomadores = tomadorManager.getTomadoresGrupoEntidad(tomadorBusqueda,user.getListaCodEntidadesGrupo());
				request.setAttribute("listaTom", listaTomadores);
				
				return new ModelAndView("forward:/informes.html?method=doInformeTomadores");
		} else {
			tomadorBusqueda = tomadorBean;
		}	
		
		// Si el usuario tiene perfil diferente de 0 y 5 se incluye su entidad en el filtro 
		if (!Constants.PERFIL_USUARIO_ADMINISTRADOR.equals(user.getPerfil()) &&
			!Constants.PERFIL_USUARIO_SEMIADMINISTRADOR.equals(user.getPerfil())) {
			tomadorBean.getId().setCodentidad(user.getOficina().getEntidad().getCodentidad());
			tomadorBusqueda = tomadorBean;
		}

		String perfil = user.getPerfil().substring(4);
		List<Tomador> listaTomadores = tomadorManager.getTomadoresGrupoEntidad(tomadorBusqueda,user.getListaCodEntidadesGrupo());
		parameters.put("listaTomadores", listaTomadores);
		parameters.put("grupoEntidades", StringUtils.toValoresSeparadosXComas(user.getListaCodEntidadesGrupo(), false, false));
		parameters.put("perfil", perfil);

		ModelAndView resultado = new ModelAndView("moduloAdministracion/tomadores/tomadores", "tomadorBean", tomadorBean);
		resultado.addAllObjects(parameters);

		return resultado;
	}

	public final void setTomadorManager(final TomadorManager tomadorManager) {
		this.tomadorManager = tomadorManager;
	}
}
