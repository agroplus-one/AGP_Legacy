package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.ColectivoManager;
import com.rsi.agp.core.managers.impl.EntidadManager;
import com.rsi.agp.core.managers.impl.HistoricoColectivosManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.admin.Tomador;
import com.rsi.agp.dao.tables.admin.TomadorId;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.pagination.PaginatedListImpl;

import es.agroseguro.acuseRecibo.AcuseRecibo;

public class ColectivoController extends BaseSimpleController implements Controller {

	private ColectivoManager colectivoManager;
	private EntidadManager entidadManager;
	private HistoricoColectivosManager historicoColectivosManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	
	private static final String ALTA_HISTORICO_COLECTIVO = "A";
	private static final String MODIFICACION_HISTORICO_COLECTIVO = "M";
	
	public static final String COLECTIVO_CORRECTO = "Colectivo Correcto";
	public static final String COLECTIVO_ERRONEO = "Colectivo Err&#243;neo";
	public static final String COLECTIVO_RECHAZADO = "Colectivo Rechazado";
	public static final List<String> ESTADOSCOLECTIVOS = Arrays.asList(COLECTIVO_CORRECTO, COLECTIVO_ERRONEO, COLECTIVO_RECHAZADO);
	
	
	public ColectivoController() {
		super();
		setCommandClass(Colectivo.class);
		setCommandName("colectivoBean");
	}

	@Override
	/** 
	 * Handle: redirige segun la operacion indicada
	 */
	protected final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object object,BindException exception) throws Exception {
		//DAA  09/04/12
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		boolean meterFiltroInicial= false;
		Map<String, Object> parameters = new HashMap<String, Object>();
		boolean activarCol = false;
		//Guardo parametros de busqueda en ColectivoBean
		Colectivo colectivoBean = (Colectivo) object;
		//Guardo en recogerColectivoSesion el hidden de bvolver del jsp
		String  recogerColectivoSesion =  StringUtils.nullToString(request.getParameter("recogerColectivoSesion"));
		
		String origenLlamada = StringUtils.nullToString(request.getParameter("origenLlamada"));
		
		// Operacion a realizar
		String operacion = StringUtils.nullToString(request.getParameter("operacion"));
		
		boolean addFiltroBaja =false;
		List<BigDecimal> planesFiltroInicial = new ArrayList<BigDecimal>();
				
		// Si se ha pulsado 'Volver', se obtiene el objeto de busqueda de la sesion
		if ("true".equals(recogerColectivoSesion)){
			colectivoBean = (Colectivo) request.getSession().getAttribute("colectivoBean");
			request.getSession().removeAttribute("colectivoBean");
					
		}
		
		if (operacion.equals("consultar")){
			if (!usuario.getPerfil().equals(bundle.getString("usuario.perfil") + "-0")) {
				colectivoBean.setActivo(null);
			}
		}
		// se inserta el objeto de busqueda en sesion, excepto cuando la operacion es 'Baja'
		if (!"baja".equals(operacion)){			
			request.getSession().setAttribute("colectivoBean", colectivoBean);
		}
				
		Colectivo colectivoBusqueda = new Colectivo();
		
		// Se comprueban las fechas primer pago y segundo pago
		if (colectivoBean != null && colectivoBean.getFechaprimerpago() == null){
			String fechaPrimerPago = request.getParameter("fechaprimerpago");
			if (!StringUtils.nullToString(fechaPrimerPago).equals("")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				colectivoBean.setFechaprimerpago(df.parse(fechaPrimerPago));
			}
		}
		if (colectivoBean != null && colectivoBean.getFechasegundopago() == null){
			String fechaSegundoPago = request.getParameter("fechasegundopago");
			if (!StringUtils.nullToString(fechaSegundoPago).equals("")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				colectivoBean.setFechasegundopago(df.parse(fechaSegundoPago));
			}
		}
		
		// Se comprueban las fechas de cambio y efecto
		if (colectivoBean != null && colectivoBean.getFechacambio() == null){
			String fechaCambio = request.getParameter("fechacambio");
			if (!StringUtils.nullToString(fechaCambio).equals("")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				colectivoBean.setFechacambio(df.parse(fechaCambio));
			}
		}
		
		if (colectivoBean != null && colectivoBean.getFechaefecto() == null){
			String fechaEfecto = request.getParameter("fechaefecto");
			if (!StringUtils.nullToString(fechaEfecto).equals("")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				colectivoBean.setFechaefecto(df.parse(fechaEfecto));
			}
		}
			
		
		
		if ("cargar".equalsIgnoreCase(operacion)){
			final Long id = Long.parseLong(request.getParameter("id"));
			colectivoBusqueda=colectivoManager.cargar(request, parameters, bundle,id);
			//colectivoManager.copiabean (colectivoBusqueda,colectivoBean);
			colectivoBusqueda = colectivoBean;
		
		}else if ("alta".equalsIgnoreCase(operacion))
			colectivoBusqueda = alta(usuario, parameters, colectivoBean,request);
		
		else if ("baja".equalsIgnoreCase(operacion)){
			//mapa que almacena los colectivos que devuelve el metodo baja
			Map<String, Colectivo> mapaColectivosBaja = colectivoManager.baja(parameters, colectivoBean, colectivoBusqueda, usuario);
			colectivoBean = mapaColectivosBaja.get("colectivoBean");
			colectivoBusqueda = mapaColectivosBaja.get("colectivoBusqueda");
		
		}else if ("modificar".equalsIgnoreCase(operacion)){
			colectivoBusqueda = modificar(usuario, parameters, colectivoBean, request);
		
		}else if("imprimir".equalsIgnoreCase(operacion)){
			//colectivoManager.copiabean (colectivoBusqueda,colectivoBean);
			colectivoBusqueda = colectivoBean;
			
			Colectivo colectivocopy = new Colectivo();
			colectivoManager.copiabean(colectivocopy,colectivoBusqueda);
			if (!esBeanFiltroEmpty(colectivocopy) && 
					StringUtils.nullToString(colectivocopy.getActivo()).equals(Constants.COLECTIVO_NO_ACTIVO.toString())){
				colectivocopy.setActivo(null);
			}
			
			if (esBeanFiltroEmpty(colectivocopy)){
				// anadimos filtro inicial siempre que vengamos del menu lateral o de limpiar
				// y para perfil 0
				if (usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
					colectivoBusqueda.setActivo(Constants.COLECTIVO_NO_ACTIVO);
					colectivoBean.setActivo(Constants.COLECTIVO_NO_ACTIVO);
					try {
						planesFiltroInicial = colectivoManager.getPlanesFiltroInicial();
					} catch (BusinessException e) {
						throw e;
					}
					addFiltroBaja = true;
				}
			}
			
			// ---- Para la paginacion del displaytag evitando que almacene todos los registros de la BD en memoria. ----
	        Long numPageRequest = new Long("0");
			
			if(request.getParameter("page") == null)
				numPageRequest = Long.parseLong("1");
			else
			    numPageRequest = Long.parseLong(request.getParameter("page"));
			//------------------Parametros de ordenacion
			String sort = StringUtils.nullToString(request.getParameter("sort"));		
			String dir = StringUtils.nullToString(request.getParameter("dir"));
			
			PaginatedListImpl<Colectivo> listaColectivos = colectivoManager.getPaginatedListColectivos(
					colectivoBusqueda, usuario.getListaCodEntidadesGrupo(), numPageRequest.intValue(),
					sort,dir, usuario.getPerfil(),addFiltroBaja,planesFiltroInicial);
			
			if(listaColectivos.getFullListSize() < Integer.parseInt(bundle.getString("impresionnumReg"))){
				List<Colectivo> listaCol = colectivoManager.getColectivosGrupoEntidad(colectivoBusqueda, 
						usuario.getListaCodEntidadesGrupo(),addFiltroBaja,planesFiltroInicial);	
				request.setAttribute("listaCol", listaCol);
				return new ModelAndView("forward:/informes.html?method=doInformeColectivos");
			}else{
				parameters.put("alerta", bundle.getString("listados.msgError"));	
			}
		}
		else if ("limpiar".equalsIgnoreCase(operacion)){
			// Si venimos de carga asegurado, y limpiamos mostramos los colectivos de la entidad del usuario
			if(request.getRequestURL().toString().indexOf("cargaColectivo")!=-1){
				if (!usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR) && !usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)){
					BigDecimal codentidad = usuario.getOficina().getEntidad().getCodentidad();
					TomadorId tomId = new TomadorId();
					tomId.setCodentidad(codentidad);
					Tomador tom = new Tomador();
					tom.setId(tomId);
					colectivoBean = new Colectivo();
					colectivoBean.setTomador(tom);
					colectivoManager.copiabean (colectivoBusqueda,colectivoBean);
				}					
			}
			
			else{
				colectivoManager.copiabean (colectivoBusqueda,colectivoBean);
			}
			meterFiltroInicial = true;
		}else if ("doActivarColectivo".equalsIgnoreCase(operacion)){
			String idCol = request.getParameter("idColPopUp");
			
			String ccc = StringUtils.nullToString(request.getParameter("ccc"));
			if (!ccc.equals("")){
				colectivoBean.setCccEntidad(ccc.substring(4,8));
				colectivoBean.setCccOficina(ccc.substring(8,12));
				colectivoBean.setCccDc(ccc.substring(12,14));
				colectivoBean.setCccCuenta(ccc.substring(14,24));
			}
		
			activarCol = true;
			
			if (null != idCol){
				colectivoBean = colectivoManager.activarColectivo (Long.valueOf(idCol));
			//	colectivoManager.copiabean (colectivoBusqueda,colectivoBean);
				HistoricoColectivos hc = historicoColectivosManager.getUltColectivoHistorico (Long.valueOf(idCol));
				if (hc == null){
					hc = new HistoricoColectivos();
					hc.setFechaefecto(new Date());
				}
					historicoColectivosManager.saveHistoricoColectivo(colectivoBean,  usuario,MODIFICACION_HISTORICO_COLECTIVO,
							hc.getFechaefecto(),activarCol);
				
				if (request.getSession().getAttribute("colectivoBean") != null){
					colectivoBean = (Colectivo) request.getSession().getAttribute("colectivoBean");
					colectivoBusqueda = colectivoBean;
					//request.getSession().removeAttribute("colectivoBean");
				}
				if (request.getSession().getAttribute("filtroInicial") != null){
				
					// anadimos filtro inicial siempre que vengamos del menu lateral o de limpiar
					// y para perfil 0
					if (usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
						colectivoBusqueda.setActivo(Constants.COLECTIVO_NO_ACTIVO);
						try {
							planesFiltroInicial = colectivoManager.getPlanesFiltroInicial();
						} catch (BusinessException e) {
							throw e;
						}
						addFiltroBaja = true;
					}
					request.getSession().removeAttribute("filtroInicial");
				}
				
				parameters.put("mensaje", bundle.getString("mensaje.colectivo.activar.OK"));
			}else{
				parameters.put("alerta", bundle.getString("mensaje.colectivo.activar.KO"));
			}
		} else if ("registrarColectivo".equalsIgnoreCase(operacion)) {
			
			if (colectivoBean.getId() != null) {
					
				String realPath = this.getServletContext().getRealPath("/WEB-INF/");
				Colectivo colRegis = this.colectivoManager.getColectivo(colectivoBean.getId());
				if (colRegis != null) {
					try {						
						AcuseRecibo acuseReciboConfirm = this.colectivoManager.registrarColectivo(realPath, colRegis, usuario);
						Integer estadoCol = acuseReciboConfirm == null ? Constants.COLECTIVO_AGRO_KO
								: acuseReciboConfirm.getDocumentoArray(0).getEstado();
						if (Constants.COLECTIVO_AGRO_OK.equals(estadoCol)) {
							parameters.put("mensaje", bundle.getString("mensaje.colectivo.registrar.OK"));
						} else {							
							if (acuseReciboConfirm == null) {
								parameters.put("alerta", "Error en llamada a Servicio Web de Agroseguro");
							} else {
								es.agroseguro.acuseRecibo.Error[] errors = acuseReciboConfirm.getDocumentoArray(0).getErrorArray();					
								String[] errorsArr = new String[] {};
								for (es.agroseguro.acuseRecibo.Error error : errors) {
									errorsArr = (String[]) ArrayUtils.add(errorsArr, StringUtils.isNullOrEmpty(error.getDescripcionAmpliada()) ? error.getDescripcion() : error.getDescripcionAmpliada());
									parameters.put(Constants.COLECTIVO_AGRO_RECH.equals(estadoCol) ? "alertaCol" : "mensajeCol", errorsArr);
								}
							}							
						}
					} catch (BusinessException be) {
						parameters.put("alerta", be.getMessage());
					}
					colectivoManager.copiabean(colectivoBusqueda, colRegis);
					colectivoBusqueda.setpctDescRecarg(null);
					colectivoBusqueda.settipoDescRecarg(null);
					colectivoManager.copiabean(colectivoBean, colRegis);
				}				
				colectivoBean.setId(null);
				meterFiltroInicial = false;
			}
		} else {
			//Consultaoperacion
			colectivoManager.copiabean(colectivoBusqueda,colectivoBean);
			
			//eliminamos los valores de estas propiedades puesto que no las queremos en el filtro
			colectivoBusqueda.setpctDescRecarg(null);
			colectivoBusqueda.settipoDescRecarg(null);
			
			Colectivo colectivocopy = new Colectivo();
			colectivoManager.copiabean(colectivocopy,colectivoBusqueda);
			if (!esBeanFiltroEmpty(colectivocopy) && 
					StringUtils.nullToString(colectivocopy.getActivo()).equals(Constants.COLECTIVO_NO_ACTIVO.toString())){
				colectivocopy.setActivo(null);
			}
			// Si el bean esta vacio y no venimos de cargaColectivo
			// anadimos filtro inicial (siempre que vengamos del menu lateral o de limpiar
			// y para perfil 0)
			if ((esBeanFiltroEmpty(colectivocopy) && !origenLlamada.equals("cargaColectivos"))){
				meterFiltroInicial = true;
			}
			if (operacion.equals("consultar")){
				meterFiltroInicial = false;
			}
			// al consultar guardamos el colectivo en session para que al activar un colectivo
			// lo podamos recuperar y mostrar
			request.getSession().setAttribute("colectivoBean", colectivoBean);
		}
		request.getSession().removeAttribute("filtroInicial");
		if (meterFiltroInicial){
			if (usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR)){
				colectivoBusqueda.setActivo(Constants.COLECTIVO_NO_ACTIVO);
				colectivoBean.setActivo(Constants.COLECTIVO_NO_ACTIVO);
				try {
					planesFiltroInicial = colectivoManager.getPlanesFiltroInicial();
				} catch (BusinessException e) {
					throw e;
				}
				addFiltroBaja = true;
				request.getSession().setAttribute("colectivoBean", colectivoBean);
				request.getSession().setAttribute("filtroInicial", true);
			}
		}
		
		
		if (!usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_ADMINISTRADOR) && 
				!usuario.getPerfil().equalsIgnoreCase(Constants.PERFIL_USUARIO_SEMIADMINISTRADOR)){
			colectivoBean.getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
			colectivoBean.getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
	    }
		if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
			colectivoBean.getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
			colectivoBean.getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
			colectivoBean.getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
			colectivoBean.getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
		}
		
		//Rellenamos los valores de E-S para el perfil 4
		 if((Constants.PERFIL_USUARIO_OTROS.equals(usuario.getPerfil()))
				 && usuario.getSubentidadMediadora() != null
				 && usuario.getSubentidadMediadora().getId() != null)
		 {
				colectivoBean.getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
				colectivoBean.getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
				colectivoBean.getSubentidadMediadora().setNomsubentidad(usuario.getSubentidadMediadora().getNomSubentidadCompleto());
		 }
				
		// ---------------------------------------------
		//           PARA TODAS LAS OPERACIONES
		// ---------------------------------------------
		
		
		// ---- Para la paginacion del displaytag evitando que almacene todos los registros de la BD en memoria. ----
        Long numPageRequest = new Long("0");
		
		if(request.getParameter("page") == null)
			numPageRequest = Long.parseLong("1");
		else
		    numPageRequest = Long.parseLong(request.getParameter("page"));
		//------------------Parametros de ordenacion
		String sort = StringUtils.nullToString(request.getParameter("sort"));		
		String dir = StringUtils.nullToString(request.getParameter("dir"));

		//DAA 08/05/2012 Si la operacion es baja tiene que devolver el filtro de busqueda original.
		if ("baja".equalsIgnoreCase(operacion)){
			colectivoBusqueda = (Colectivo) request.getSession().getAttribute("colectivoBean");
			colectivoBean = new Colectivo();
			this.colectivoManager.copiabean(colectivoBean, colectivoBusqueda);
		}
		//TMR 23/05/2012 
		PaginatedListImpl<Colectivo> listaColectivos;
		//Si vengo de comisiones filtramos los colectivos en funcion del idcolectivo que recibo en la request
		String idColectivoComisiones = StringUtils.nullToString(request.getParameter("idColectivoComisiones"));
		if (StringUtils.nullToString(request.getParameter("vengoDComisiones")).equals("true")){
			parameters.put("addBotonVolver", "true");
		}
		
		if (!idColectivoComisiones.equals("")){
			Colectivo col = new Colectivo();
			col.setIdcolectivo(idColectivoComisiones);
			
			Linea linea = new Linea();
			String codlinea= StringUtils.nullToString(request.getParameter("codLineaCom"));
			if (!codlinea.equals(""))
				linea.setCodlinea(new BigDecimal(codlinea));
			String plan = StringUtils.nullToString(request.getParameter("planLineaCom"));
			if (!plan.equals(""))
				linea.setCodplan(new BigDecimal(plan));
			col.setLinea(linea);
			
			listaColectivos = colectivoManager.getPaginatedListColectivos(col, 
					usuario.getListaCodEntidadesGrupo(),
					numPageRequest.intValue(),sort,dir, usuario.getPerfil(),addFiltroBaja,planesFiltroInicial);
			
			colectivoBean.setIdcolectivo(idColectivoComisiones);
			colectivoBean.setLinea(linea);
			parameters.put("idFicheroComisiones", request.getParameter("idFicheroComisiones"));
			parameters.put("tipoFicheroComisiones", request.getParameter("tipoFicheroComisiones"));
			parameters.put("vengoDComisiones","true");
			
		}else{
			listaColectivos = colectivoManager.getPaginatedListColectivos(colectivoBusqueda, 
					usuario.getListaCodEntidadesGrupo(),numPageRequest.intValue(),sort,dir, 
					usuario.getPerfil(),addFiltroBaja,planesFiltroInicial);
		}
		
        // ---- fin paginacion ----
		
		if(null!= request.getParameter("procedencia")&& "incidenciasComisionesUnificadas".equals(request.getParameter("procedencia")) ) {
			parameters.put("procedencia","incidenciasComisionesUnificadas");
		}
		
		
		
		//Obtenemos la lista de colectivos
		String grupoEntidades = "";
		if (!StringUtils.nullToString(usuario.getListaCodEntidadesGrupo()).equals("")){
			grupoEntidades = StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(), false, false);
		}
		//DAA 09/05/13 Para validar el grupo de entidades CRM		
		parameters.put("listaEntCRM", entidadManager.getEntidadesGrupoCRM());
		parameters.put("totalListSize", listaColectivos.getFullListSize());
		parameters.put("listaColectivos", listaColectivos);
		parameters.put("perfil", usuario.getPerfil().substring(4));
		parameters.put("grupoEntidades", grupoEntidades);
		parameters.put("externo",usuario.getExterno());
		parameters.put("ccc", StringUtils.nullToString(colectivoBean.getIban())+StringUtils.nullToString(colectivoBean.getCccEntidad())
				+StringUtils.nullToString(colectivoBean.getCccOficina())+StringUtils.nullToString(colectivoBean.getCccDc())
				+StringUtils.nullToString(colectivoBean.getCccCuenta()));
		
		//Dependiendo de la url (modulo) desde la que hemos venido, se redirige a una u otra pagina.
		ModelAndView resultado = ((request.getRequestURL().toString().indexOf("cargaColectivo")!=-1)?
		new ModelAndView("moduloPolizas/colectivos/cargaColectivos", "colectivoBean", colectivoBean):
		new ModelAndView("moduloAdministracion/colectivos/colectivos", "colectivoBean", colectivoBean));
		
		resultado.addAllObjects(parameters);

		return resultado;
	}
	
	

	private boolean esBeanFiltroEmpty(Colectivo bean){
		
		Colectivo c = new Colectivo();
		if (bean.equals(c))
			return true;
		return false;
	}

	private Colectivo modificar(final Usuario usuario, final Map<String, Object> parameters, Colectivo colectivoBean,HttpServletRequest request) {
		Colectivo colectivoBusqueda;	
		boolean hayErroresColectivo = false;
		String ccc = "";
		
		// PTC-5729 (03/05/2019) ** MODIF TAM //
		// Independientemente del perfil, al realizar una modificación sobre el colectivo, este siempre se marca como activo = 'NO'
		colectivoBean.setActivo("0".charAt(0));
		// PEC-5729 (17/05/2019) ** FIN ** MODIF TAM //
		
		colectivoBean.setFechacambio(new Date());	
		if (colectivoBean.getFechaefecto() == null){
			colectivoBean.setFechaefecto(new Date());	
		}
		if (colectivoBean.getFechaprimerpago() == null)
		{
			Date fecha = new Date();
			colectivoBean.setFechaprimerpago(fecha);
		}
		ccc = StringUtils.nullToString(request.getParameter("ccc"));
		colectivoBean.setCccEntidad(ccc.substring(4,8));
		colectivoBean.setCccOficina(ccc.substring(8,12));
		colectivoBean.setCccDc(ccc.substring(12,14));
		colectivoBean.setCccCuenta(ccc.substring(14,24));
		ArrayList<Integer> errorColectivo = colectivoManager.saveColectivo(colectivoBean, usuario, 1);
		
		//Si no hay errores se actualiza el historico de colectivos
		for (Integer valor: errorColectivo){
			if (valor.intValue() != 0){
				hayErroresColectivo = true;
				break;
			}
		}
		if (!hayErroresColectivo){
			errorColectivo = historicoColectivosManager.saveHistoricoColectivo(colectivoBean, usuario, 
					MODIFICACION_HISTORICO_COLECTIVO,null,false);
		}
		
		// Carga los mensajes de error producidos en el proceso de modificacion
		ArrayList<String> erroresWeb = cargarErrores(usuario, parameters, errorColectivo, false);
		
		if (erroresWeb.size() > 0){
			parameters.put("alerta2", erroresWeb);
		}else if (erroresWeb.size() == 0)
			parameters.put("idCol", colectivoBean.getId());
		
		Tomador tom = colectivoManager.getTomadorByCif(colectivoBean.getTomador().getId().getCodentidad(),colectivoBean.getTomador().getId().getCiftomador());
		parameters.put("repreNombre", tom.getRepreNombre());
		parameters.put("repreAp1", tom.getRepreAp1());
		parameters.put("repreAp2", tom.getRepreAp2());
		parameters.put("repreNif", tom.getRepreNif());
		parameters.put("estado", "modificar");
		parameters.put("ccc", ccc);
		if(null!= request.getParameter("procedencia")&& "incidenciasComisionesUnificadas".equals(request.getParameter("procedencia")) ) {
			parameters.put("procedencia","incidenciasComisionesUnificadas");
		}
		colectivoBusqueda = colectivoBean;
		return colectivoBusqueda;
	}

	/**
	 * Devuelve la lista de mensajes correspondiente a los errores producidos en el proceso de alta o modificacion del colectivo
	 * @param usuario
	 * @param parameters
	 * @param errorColectivo
	 * @return
	 */
	private ArrayList<String> cargarErrores(final Usuario usuario,final Map<String, Object> parameters, ArrayList<Integer> errorColectivo, boolean alta) {
		
		ArrayList<String> erroresWeb = new ArrayList<String>();
		for (Integer valor: errorColectivo){
			switch (valor.intValue()){
				case 0: 
						parameters.put("mensaje", bundle.getString("mensaje." + (alta ? "alta" : "modificacion") + ".OK"));
						break;
				case 1: 
						erroresWeb.add(bundle.getString("mensaje.alta.duplicado.KO"));
						break;
				case 2: 
						if (!usuario.getPerfil().equalsIgnoreCase(bundle.getString("usuario.perfil") + "-0")) {
							erroresWeb.add(bundle.getString("mensaje.colectivo.entidad.KO"));
						}
						break;
				case 3: 
						erroresWeb.add(bundle.getString("mensaje.colectivo.plan.inexistente.KO"));
						break;
				case 4: 
						erroresWeb.add(bundle.getString("mensaje.colectivo.planLinea.inexistente.KO"));
						break;
				case 5: 
						erroresWeb.add(bundle.getString("mensaje.colectivo.planLinea.inactivo.KO"));
						break;
				case 6: 
						erroresWeb.add(bundle.getString("mensaje.colectivo.tomador.inexistente.KO"));
						break;
				case 7: 
						erroresWeb.add(bundle.getString("mensaje.colectivo.dc.incorrecto.KO"));
						break;
				case 8:	
						erroresWeb.add(bundle.getString("mensaje.modificacion.colectivo.dc.inexistente.OK"));
						break;
				case 10:
						erroresWeb.add(bundle.getString("mensaje.colectivo.ccc.entidad.inexistente.KO"));
						break;
				case 11:
						erroresWeb.add(bundle.getString("mensaje.colectivo.ccc.oficina.inexistente.KO"));
						break;
				case 12:
						erroresWeb.add(bundle.getString("mensaje.colectivo.ccc.dc.incorrecto.KO"));
						break;
				case 13:
						erroresWeb.add(bundle.getString("mensaje.colectivo.subentidad.inexistente.KO"));
						break;
				case 20:
						erroresWeb.add(bundle.getString("mensaje.colectivo.generico.KO"));
						break;
				default:
						break;
			}
		}
		return erroresWeb;
	}

	private Colectivo alta(final Usuario usuario,Map<String, Object> parameters, Colectivo colectivoBean,HttpServletRequest request) {
		
		Colectivo colectivoBusqueda = colectivoManager.getColectivo(colectivoBean.getId());
		boolean hayErroresColectivo = false;
		String ccc = "";
		if(colectivoBusqueda == null){
				colectivoBean.setActivo("0".charAt(0));
				
				if (colectivoBean.getFechaprimerpago() == null)
				{
					Date fecha = new Date();
					colectivoBean.setFechaprimerpago(fecha);
				}
				colectivoBean.setFechacambio(new Date());
				colectivoBean.setFechaefecto(new Date());
			
				colectivoBean.setPctdescuentocol(colectivoManager.getPctDtoCol(colectivoManager.getLineaseguroId(colectivoBean.getLinea().getCodplan(), colectivoBean.getLinea().getCodlinea())));

				ccc = request.getParameter("ccc");
				colectivoBean.setCccEntidad(ccc.substring(4,8));
				colectivoBean.setCccOficina(ccc.substring(8,12));
				colectivoBean.setCccDc(ccc.substring(12,14));
				colectivoBean.setCccCuenta(ccc.substring(14,24));
				//DAA 29/11/2012
				// Validamos el tomador y si no lo es no debemos guardar el colectivo y no imprimira el informe de alta
				ArrayList<Integer> errorColectivo = new ArrayList<Integer>();
				Tomador tom = colectivoManager.getTomadorByCif(colectivoBean.getTomador().getId().getCodentidad(),colectivoBean.getTomador().getId().getCiftomador());
				if (tom != null && tom.getRepreNombre()!=null && tom.getRepreAp1()!=null && tom.getRepreAp2()!=null && tom.getRepreNif()!=null && tom.getRepreNombre()!=null){
					errorColectivo = colectivoManager.saveColectivo(colectivoBean, usuario, 0);
					//Si no hay errores se actualiza el historico de colectivos
					for (Integer valor: errorColectivo){
						if (valor.intValue() != 0){
							hayErroresColectivo = true;
							break;
						}
					}
					if (!hayErroresColectivo){
						
						
						errorColectivo = historicoColectivosManager.saveHistoricoColectivo(colectivoBean, usuario, 
								ALTA_HISTORICO_COLECTIVO,null,false);
					}
					
					// Carga los mensajes de error producidos en el proceso de alta
					ArrayList<String> erroresWeb = cargarErrores(usuario, parameters, errorColectivo, true);
					
					if (erroresWeb.size() > 0){
						parameters.put("alerta2", erroresWeb);
					}else if (erroresWeb.size() == 0){
						
						colectivoBean.setTomador(tom);
						parameters.put("repreNombre", tom.getRepreNombre());
						parameters.put("repreAp1", tom.getRepreAp1());
						parameters.put("repreAp2", tom.getRepreAp2());
						parameters.put("repreNif", tom.getRepreNif());
						
						parameters.put("estadoInforme", "informeOK");
						parameters.put("idCol", colectivoBean.getId());
						
					}
				}
				else{
					parameters.put("alerta", bundle.getString("mensaje.alta.colectivo.tomador.KO"));
				}
				
				
		}else{
			parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
		}
		parameters.put("estado", "alta");
		parameters.put("ccc", ccc);
		if(null!= request.getParameter("procedencia")&& "incidenciasComisionesUnificadas".equals(request.getParameter("procedencia")) ) {
			parameters.put("procedencia","incidenciasComisionesUnificadas");
		}
		colectivoBusqueda = colectivoBean;
		return colectivoBusqueda;
	}

	public final void setColectivoManager(final ColectivoManager colectivoManager) {
		this.colectivoManager = colectivoManager;
	}
	
	public final void setEntidadManager(final EntidadManager entidadManager) {
		this.entidadManager = entidadManager;
	}

	public void setHistoricoColectivosManager(HistoricoColectivosManager historicoColectivosManager) {
		this.historicoColectivosManager = historicoColectivosManager;
	}
	
}
