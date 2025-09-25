package com.rsi.agp.core.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.exception.ValidacionAnexoModificacionException;
import com.rsi.agp.core.managers.impl.AseguradoSubvencionManager;
import com.rsi.agp.core.managers.impl.DeclaracionesModificacionPolizaManager;
import com.rsi.agp.core.managers.impl.SubvencionesDeclarablesAseguradoModificacionPolizaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.anexo.CapitalAsegurado;
import com.rsi.agp.dao.tables.anexo.Estado;
import com.rsi.agp.dao.tables.anexo.Parcela;
import com.rsi.agp.dao.tables.cgen.SubvencionesAseguradosView;
import com.rsi.agp.dao.tables.commons.Usuario;

public class SubvencionesDeclarablesAseguradoModificacionPolizaController extends BaseMultiActionController {

	private Log logger = LogFactory.getLog(SubvencionesDeclarablesAseguradoModificacionPolizaController.class);
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	private DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager;
	private SubvencionesDeclarablesAseguradoModificacionPolizaManager subvencionesDeclarablesAseguradoModificacionPolizaManager;
	private AseguradoSubvencionManager aseguradoSubvencionManager;
	
	private DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController;
	private ParcelasModificacionPolizaController parcelasModificacionPolizaController;
	
	/**
	 * Método para obtener la lista de subvenciones del asegurado y mostrarlas en pantalla
	 * @param request
	 * @param response
	 * @param asegurado
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doConsulta(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		logger.debug("init - doConsulta");
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
		Long idAnexo = new Long(StringUtils.nullToString(request.getParameter("idAnexoModificacion")));
		String modoLectura = request.getParameter("modoLectura");
		Boolean esModoLectura =(modoLectura!=null && modoLectura.compareTo("modoLectura")==0);
		
		logger.debug("idAnexoModificacion = " + idAnexo);
		AnexoModificacion anexo = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
		
		/*
		 * Las subvenciones que se mostrarán marcadas serán aquellas cuyo tipomodificacion sea null o 'ALTA'
		 */		
		logger.debug("Recuperamos el listado de subvenciones del asegurado");
		List<SubvencionesAseguradosView> listaSubvencionesAsegurado = 
				subvencionesDeclarablesAseguradoModificacionPolizaManager.getSubvencionesAsegurado(anexo, usuario.getTipousuario());
		logger.debug("listado recuperado. Tamaño=" + listaSubvencionesAsegurado.size());
		
		logger.debug("Generamos la tabla html de las subvenciones");
		String tabla = aseguradoSubvencionManager.pintarTablaSubv(listaSubvencionesAsegurado, esModoLectura); 		
		logger.debug("Tabla generada. Tiene datos? "+ !tabla.equals(""));
		
		if(!tabla.equals("")){
			parameters.put("tabla", tabla);
			parameters.put("NoData", false);
		}else
			parameters.put("NoData", true);
		
		parameters.put("idAnexoModificacion", idAnexo);
		parameters.put("modoLectura", modoLectura);
		parameters.put("vieneDeListadoAnexosMod", StringUtils.nullToString(request.getParameter("vieneDeListadoAnexosMod")));
		
		// Setea el hidden de cambios de asegurados para los volver
		parameters.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));
		
		boolean isLineaGanado = anexo.getPoliza().getLinea().isLineaGanado();
		
		parameters.put("isGanado", isLineaGanado);
		
		return new ModelAndView("/moduloUtilidades/modificacionesPoliza/subvencionesAsegAnexoModificacion", "subvencionesAsegurado", parameters);

	}

	/**
	 * Método para guardar las modificaciones de subvenciones y pasar a la siguiente pantalla.
	 * @param request
	 * @param response
	 * @param anexo
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doContinua(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexo) throws Exception{
		/*
		 * A la hora de guardar las subvenciones, se debe tener en cuenta lo siguiente:
		 * Subvenciones seleccionadas: 
		 * 		- Si tipomodificacion = null o 'ALTA' => lo dejamos como está.
		 * 		- Si tipomodificacion = 'BAJA' => lo dejamos a null.
		 * Subvenciones no seleccionadas:
		 * 		- Si tipomodificacion = null o 'BAJA' => lo dejamos en 'BAJA'
		 * 		- Si tipomodificacion = 'ALTA' => la eliminamos de las subvenciones 
		 */
		
		//Obtenemos el anexo de modificacion para ver que subvenciones tenía previamente.
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario"); 
		Long idAnexo = new Long(StringUtils.nullToString(request.getParameter("idAnexoModificacion")));
		anexo = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
		if(request.getParameter("operacion").equals("pasarDedinitivo")){
			Estado estado = new Estado(Constants.ANEXO_MODIF_ESTADO_DEFINITIVO);
		   
		   declaracionesModificacionPolizaManager.saveAnexoModificacion(anexo,usuario.getCodusuario(),estado,false);
		}
		//Obtengo los codigos de subvencion seleccionados en la pantalla
		String subvSeleccionadas = StringUtils.nullToString(request.getParameter("subvsSeleccionadas"));
		
		//Llamamos al método del manager que se encarga de tratar las subvenciones
		try{
			subvencionesDeclarablesAseguradoModificacionPolizaManager.actualizaSubvencionesAnexoModificacion(anexo, subvSeleccionadas, usuario.getTipousuario());
			//Una vez guardadas las subvenciones, se redirige a la pantalla del listado de anexos.
			request.setAttribute("mensaje",bundle.getString("mensaje.anexo.OK"));
			
			//AMG . Utilidades anexos de modificacion 18-09-2012
			String vieneDeListadoAnexosMod = request.getParameter("vieneDeListadoAnexosMod");
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));

			if ("true".equals(vieneDeListadoAnexosMod)) {
				parametros.put("volver", true);
				parametros.put("mensaje", bundle.getString("mensaje.anexo.OK"));
				return new ModelAndView("redirect:/anexoModificacionUtilidades.run").addAllObjects(parametros);
			}else{
				//return new ModelAndView("forward:/declaracionesModificacionPoliza.html?method=doConsulta&idPoliza="+anexo.getPoliza().getIdpoliza());
				parametros.put("mensaje", bundle.getString("mensaje.anexo.OK"));
				return this.declaracionesModificacionPolizaController.doConsulta(request, response, anexo).addAllObjects(parametros);
			}
		
		} catch (ValidacionAnexoModificacionException e) {
			logger.error("Se ha producido un error al validar el xml del anexo de modificacion. " + e.getMessage());
			return doConsulta(request, response).addObject("alerta", bundle.getString("mensaje.anexo.KO"));
		}catch (Exception e){
			//En caso de error, se redirige a la misma pantalla informando del error.
			logger.error("Se ha producido un error al validar el xml del anexo de modificacion. " + e.getMessage());
			return doConsulta(request, response).addObject("alerta", bundle.getString("mensaje.anexo.KO"));
		}
	}
	
	/**
	 * Método para volver a la pantalla del listado de parcelas
	 * @param request
	 * @param response
	 * @param anexoModificacion
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doVolver(HttpServletRequest request, HttpServletResponse response, AnexoModificacion anexo) throws Exception {
		Map<String, Object> parametros = new HashMap<String, Object>();
		Long idAnexo = new Long(0);
		String modoLectura = request.getParameter("modoLectura");
		
		if (!StringUtils.nullToString(request.getParameter("idAnexoModificacion")).equals(""))
			idAnexo = new Long(StringUtils.nullToString(request.getParameter("idAnexoModificacion")));
		
		// Obtengo los datos del anexo
		anexo = declaracionesModificacionPolizaManager.getAnexoModifById(idAnexo);
		
		parametros.put("idAnexo", anexo.getId());
		parametros.put("idPoliza", anexo.getPoliza().getIdpoliza());
		parametros.put("modoLectura", modoLectura);
		
		String vieneDeListadoAnexosMod = StringUtils.nullToString(request.getParameter("vieneDeListadoAnexosMod"));
		parametros.put("vieneDeListadoAnexosMod",vieneDeListadoAnexosMod);
		
		parametros.put("hayCambiosDatosAsegurado", request.getParameter("hayCambiosDatosAsegurado"));

		CapitalAsegurado capitalAseguradoModificadaBean = new CapitalAsegurado();
		Parcela parcelaAnexo = new Parcela();
		parcelaAnexo.setAnexoModificacion(anexo);
		capitalAseguradoModificadaBean.setParcela(parcelaAnexo);
		
		return parcelasModificacionPolizaController.doConsulta(request, response, capitalAseguradoModificadaBean).addAllObjects(parametros);
	}
	
	public void setDeclaracionesModificacionPolizaManager(DeclaracionesModificacionPolizaManager declaracionesModificacionPolizaManager) {
		this.declaracionesModificacionPolizaManager = declaracionesModificacionPolizaManager;
	}

	public void setSubvencionesDeclarablesAseguradoModificacionPolizaManager(SubvencionesDeclarablesAseguradoModificacionPolizaManager subvencionesDeclarablesAseguradoModificacionPolizaManager) {
		this.subvencionesDeclarablesAseguradoModificacionPolizaManager = subvencionesDeclarablesAseguradoModificacionPolizaManager;
	}

	public void setAseguradoSubvencionManager(AseguradoSubvencionManager aseguradoSubvencionManager) {
		this.aseguradoSubvencionManager = aseguradoSubvencionManager;
	}

	public void setParcelasModificacionPolizaController(
			ParcelasModificacionPolizaController parcelasModificacionPolizaController) {
		this.parcelasModificacionPolizaController = parcelasModificacionPolizaController;
	}

	public void setDeclaracionesModificacionPolizaController(
			DeclaracionesModificacionPolizaController declaracionesModificacionPolizaController) {
		this.declaracionesModificacionPolizaController = declaracionesModificacionPolizaController;
	}

}
