/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  15/10/2010  Ernesto Laura		Controller para la realizacion del alta de subvenciones de     
*											socios. 
*											Version 2: realizada desde cero
*
 **************************************************************************************************
*/
package com.rsi.agp.core.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.SocioSubvencionManager;
import com.rsi.agp.core.managers.impl.ganado.SocioSubvManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesaView;
import com.rsi.agp.dao.tables.cgen.ganado.TipoSubvencionEnesaGanadoView;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.cpl.gan.SubvencionEnesaGanado;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;
import com.rsi.agp.dao.tables.poliza.SubvencionSocioGanado;

public class SocioSubvencionController extends BaseSimpleController implements Controller {

	private SocioSubvencionManager socioSubvencionManager;
	private SocioSubvManager socioSubvManager;
	final Set<String> listaSocioSubvencionesIdAux = new HashSet<String>();

	private static final Log logger = LogFactory.getLog(SocioSubvencionController.class);

	public SocioSubvencionController() {
		super();
		setCommandClass(Socio.class);
		setCommandName("socioBean");
	}

	@Override
	protected final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object object, BindException exception) 
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		ModelAndView resultado = null;
		List<TipoSubvencionEnesaView> listaSubvenciones;
		List<TipoSubvencionEnesaGanadoView> listaSubvencionesGanado;
		List<SubvencionSocio> listaSubvencionesSocio;
		List<SubvencionSocioGanado> listaSubvencionesGanadoSocio;
		ResourceBundle bundle = ResourceBundle.getBundle("agp");
		String tabla = "";
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		final Long idPoliza = Long.parseLong(request.getParameter("idpoliza"));
		Poliza poliza = socioSubvencionManager.getPolizaById(idPoliza);
		//TMR
		String modoLectura = StringUtils.nullToString(request.getParameter("modoLectura"));
		String vieneDeUtilidades = StringUtils.nullToString(request.getParameter("vieneDeUtilidades"));
		Asegurado aseguradoSesion = usuario.getAsegurado();
		
		Long idAseg;
		if (usuario.getAsegurado()!= null && modoLectura.equals("")){
			idAseg = aseguradoSesion.getId();
		}else{
			idAseg = poliza.getAsegurado().getId();
		}
		aseguradoSesion = (Asegurado)socioSubvencionManager.getDatosAsegurado(idAseg);
		
		Socio socioBean = (Socio) object;

		final String operacion = StringUtils.nullToString(request.getParameter("operacion"));
		
		
		
		try{
			
			
			parameters.put("modoLectura",modoLectura);
			parameters.put("vieneDeUtilidades", vieneDeUtilidades);
			
			// Si el asegurado es persona fisico, no tiene socios o lo ha solicitado, lo enviamos a otra pantalla.
//			if ("NIF".equalsIgnoreCase(aseguradoSesion.getTipoidentificacion()) || null == aseguradoSesion.getSocios()
//					|| aseguradoSesion.getSocios().isEmpty() || "continuar".equalsIgnoreCase(operacion)) {
//				Map<String, Object> params = new HashMap<String, Object>();
//				if (null == aseguradoSesion.getSocios() || aseguradoSesion.getSocios().isEmpty())
//					params.put("tieneSocios", "false");
//				params.put("idpoliza", idPoliza);
//				params.put("operacion", "");
//				params.put("modoLectura",modoLectura);
//				params.put("vieneDeUtilidades", vieneDeUtilidades);
//				return new ModelAndView("redirect:/aseguradoSubvencion.html").addAllObjects(params);
//			}
			if (operacion.equalsIgnoreCase("importes")){
				parameters.put("operacion", "importes");
				parameters.put("idpoliza", idPoliza);
				return new ModelAndView("redirect:/seleccionPoliza.html", parameters);
			
			}
			if (operacion.equalsIgnoreCase("continuar")){
				//si la operacion es continuar => redirigimos a la pantalla de subvenciones del asegurado
				return new ModelAndView("redirect:/aseguradoSubvencion.html");
			}
			else{
				//Ejecutamos el codigo comun para todas las operaciones segun el tipo de linea (ganado o agraria)
				if (poliza.getLinea().isLineaGanado() == true){
				
					if (!operacion.equals("")){
						//Cargamos los datos del socio seleccionado
						socioBean = socioSubvManager.getSocioById(socioBean);
						//Obtenemos la lista completa de subvenciones posibles
						listaSubvencionesGanado = socioSubvManager.getSubvenciones(socioBean, poliza);
						
						//Evaluamos la operacion y ejecutamos las acciones pertinentes
						if (operacion.equalsIgnoreCase("cargaSocio")){
							String subvencionesJS = "";
							logger.debug("Cargando las subvenciones del socio. Num subvenciones: " + listaSubvencionesGanado.size());
							for (TipoSubvencionEnesaGanadoView tip : listaSubvencionesGanado)
							{
								subvencionesJS += tip.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()+",";
							}
							logger.debug("Subvenciones del socio: " + subvencionesJS);
							if (!subvencionesJS.equals("")){
								subvencionesJS = subvencionesJS.substring(0, subvencionesJS.lastIndexOf(','));
							}
							parameters.put("subsJS", subvencionesJS);
						}
						else if (operacion.equalsIgnoreCase("alta") || operacion.equalsIgnoreCase("modificar")){
							//recogemos los codigos de subvencion seleccionados por el usuario
							List<SubvencionEnesaGanado> subvSeleccionadas = null;
							String codSubvsSeleccionadas = StringUtils.nullToString(request.getParameter("subsSeleccionadas"));
							if (!codSubvsSeleccionadas.equalsIgnoreCase("")){
								codSubvsSeleccionadas = codSubvsSeleccionadas.substring(0, codSubvsSeleccionadas.lastIndexOf(','));
								//Obtenemos las SubvencionesEnesa para los codigos recogidos seleccionados por el usuario
								subvSeleccionadas = socioSubvManager.getSubvencionesInsertar(socioBean, poliza, codSubvsSeleccionadas);
							}
							
							ArrayList<String> errorSubvenciones = socioSubvManager.altaSubvenciones(socioBean, poliza, subvSeleccionadas);
							ArrayList<String> erroresWeb = new ArrayList<String>();
							
							if (errorSubvenciones.size() == 0){
								parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
								//modifico el asegurado de la sesion para añadirle las subvenciones
								for (Socio soc : aseguradoSesion.getSocios()){
									if (soc.getId().equals(socioBean.getId())){
										soc.setSubvencionSocios(socioBean.getSubvencionSocios());
										break;
									}
								}
							}
							else{
								for (String mens : errorSubvenciones)
									erroresWeb.add(mens);
								
								if (erroresWeb.size() > 0){
									parameters.put("alerta2", erroresWeb);
								}
							}
							
							
						}
						
						//Cargamos la lista de subvenciones del socio
						listaSubvencionesGanadoSocio = new ArrayList<SubvencionSocioGanado>();
						for (SubvencionSocioGanado ss : socioBean.getSubvencionSocioGanados()){
							if (ss.getPoliza().getIdpoliza().equals(poliza.getIdpoliza()))
								listaSubvencionesGanadoSocio.add(ss);
						}
						
						//marcar en 'listaSubvenciones' aquellas que ya estan en 'listaSubvencionesGanadoSocio'
						for (TipoSubvencionEnesaGanadoView tsev : listaSubvencionesGanado){
							for (SubvencionSocioGanado ss1 : listaSubvencionesGanadoSocio){
								if (tsev.getSubvEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa().compareTo(ss1.getSubvencionEnesaGanado().getTipoSubvencionEnesa().getCodtiposubvenesa()) == 0){
									tsev.setMarcada(true);
									break;
								}
							}
						}
						
		//				STRING CON LA TABLA DE LAS SUBVENCIONES DE LOS SOCIOS
						tabla = socioSubvManager.pintarTablaSubv(listaSubvencionesGanado); 		
						if(!tabla.equals(""))
							parameters.put("tabla", tabla);
						
						parameters.put("subvenciones", listaSubvencionesGanado); //Todas las subvenciones disponibles
						parameters.put("subvencionesSocio", listaSubvencionesGanadoSocio); //Las subvenciones YA asignadas al socio
						parameters.put("numSubvencionesSocio", listaSubvencionesGanadoSocio.size());
						parameters.put("esATP", socioBean.getAtp());
						parameters.put("socio_subv", socioBean);
					}
				}
				else{
					if (!operacion.equals("")){
						//Cargamos los datos del socio seleccionado
						socioBean = socioSubvencionManager.getSocioById(socioBean);
						//Obtenemos la lista completa de subvenciones posibles
						listaSubvenciones = socioSubvencionManager.getSubvenciones(socioBean, poliza);
						
						//Evaluamos la operacion y ejecutamos las acciones pertinentes
						if (operacion.equalsIgnoreCase("cargaSocio")){
							String subvencionesJS = "";
							logger.debug("Cargando las subvenciones del socio. Num subvenciones: " + listaSubvenciones.size());
							for (TipoSubvencionEnesaView tip : listaSubvenciones) {
								subvencionesJS += tip.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()+",";
							}
							logger.debug("Subvenciones del socio: " + subvencionesJS);
							subvencionesJS = StringUtils.isNullOrEmpty(subvencionesJS) ? "" : subvencionesJS.substring(0, subvencionesJS.lastIndexOf(','));
							parameters.put("subsJS", subvencionesJS);
						}
						else if (operacion.equalsIgnoreCase("alta") || operacion.equalsIgnoreCase("modificar")){
							//recogemos los codigos de subvencion seleccionados por el usuario
							List<SubvencionEnesa> subvSeleccionadas = null;
							String codSubvsSeleccionadas = StringUtils.nullToString(request.getParameter("subsSeleccionadas"));
							if (!codSubvsSeleccionadas.equalsIgnoreCase("")){
								codSubvsSeleccionadas = codSubvsSeleccionadas.substring(0, codSubvsSeleccionadas.lastIndexOf(','));
								//Obtenemos las SubvencionesEnesa para los codigos recogidos seleccionados por el usuario
								subvSeleccionadas = socioSubvencionManager.getSubvencionesInsertar(socioBean, poliza, codSubvsSeleccionadas);
							}
							
							ArrayList<String> errorSubvenciones = socioSubvencionManager.altaSubvenciones(socioBean, poliza, subvSeleccionadas);
							ArrayList<String> erroresWeb = new ArrayList<String>();
							
							if (errorSubvenciones.size() == 0){
								parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
								//modifico el asegurado de la sesion para anhadirle las subvenciones
								for (Socio soc : aseguradoSesion.getSocios()){
									if (soc.getId().equals(socioBean.getId())){
										soc.setSubvencionSocios(socioBean.getSubvencionSocios());
										break;
									}
								}
							}
							else{
								for (String mens : errorSubvenciones)
									erroresWeb.add(mens);
								
								if (erroresWeb.size() > 0){
									parameters.put("alerta2", erroresWeb);
								}
							}
							
							
						}
						
						//Cargamos la lista de subvenciones del socio
						listaSubvencionesSocio = new ArrayList<SubvencionSocio>();
						for (SubvencionSocio ss : socioBean.getSubvencionSocios()){
							if (ss.getPoliza().getIdpoliza().equals(poliza.getIdpoliza()))
								listaSubvencionesSocio.add(ss);
						}
						
						//marcar en 'listaSubvenciones' aquellas que ya estan en 'listaSubvencionesSocio'
						for (TipoSubvencionEnesaView tsev : listaSubvenciones){
							for (SubvencionSocio ss1 : listaSubvencionesSocio){
								if (tsev.getSubvEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa().compareTo(ss1.getSubvencionEnesa().getTipoSubvencionEnesa().getCodtiposubvenesa()) == 0){
									tsev.setMarcada(true);
									break;
								}
							}
						}
						
		//				STRING CON LA TABLA DE LAS SUBVENCIONES DE LOS SOCIOS
						tabla = socioSubvencionManager.pintarTablaSubv(listaSubvenciones); 		
						if(!tabla.equals(""))
							parameters.put("tabla", tabla);
						
						parameters.put("subvenciones", listaSubvenciones); //Todas las subvenciones disponibles
						parameters.put("subvencionesSocio", listaSubvencionesSocio); //Las subvenciones YA asignadas al socio
						parameters.put("numSubvencionesSocio", listaSubvencionesSocio.size());
						parameters.put("esATP", socioBean.getAtp());
						parameters.put("socio_subv", socioBean);
					}
				}
				//Cargamos los parametros comunes
				parameters.put("poliza", poliza);
			}
			
			//SIGP0000009281. Se mostraran todos los socios asociados a la póliza.
			socioBean.setListaSocios(socioSubvManager.getSociosByAsegPoliza(aseguradoSesion, poliza));
			
		
		} catch (BusinessException e) {
			parameters.put("alerta", bundle.getString("mensaje.poliza.SubvencionesSocio.KO"));
		}
		
		if (poliza.getLinea().isLineaGanado() == true){
			resultado = new ModelAndView("moduloPolizas/polizas/subvenciones/socioSubvencionesGanado", "socioBean", socioBean);
		}
		else{
			resultado = new ModelAndView("moduloPolizas/polizas/subvenciones/socioSubvenciones", "socioBean", socioBean);
		}
		
		resultado.addAllObjects(parameters);

		return resultado;
	}
	
	public final void setSocioSubvencionManager(final SocioSubvencionManager socioSubvencionManager) {
		this.socioSubvencionManager = socioSubvencionManager;
	}

	public void setSocioSubvManager(SocioSubvManager socioSubvManager) {
		this.socioSubvManager = socioSubvManager;
	}
}
