package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.IPagoEstadosPolizaManager;
/* P73325 - RQ.04, RQ.05 y RQ.06  Incicio */
import com.rsi.agp.core.managers.ged.impl.DocumentacionGedManager;
/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
import com.rsi.agp.core.managers.impl.AseguradoManager;
import com.rsi.agp.core.managers.impl.FechaContratacionManager;
import com.rsi.agp.core.managers.impl.HistoricoManager;
import com.rsi.agp.core.managers.impl.PagoPolizaManager;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.PolizaRCManager;
import com.rsi.agp.core.managers.impl.SeleccionPolizaManager;
import com.rsi.agp.core.managers.impl.SiniestrosManager;
import com.rsi.agp.core.managers.impl.sbp.ConsultaSbpManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.commons.Usuario;
/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
import com.rsi.agp.dao.tables.ged.GedDocPoliza;
/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
import com.rsi.agp.dao.tables.pagos.EstadosPoliza;
import com.rsi.agp.dao.tables.poliza.CanalFirma;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.rc.PolizasRC;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.pagination.PaginatedListImpl;

public class UtilidadesPolizaController extends BaseSimpleController implements Controller {
	
	private static final String LISTA_POLIZAS = "listaPolizas";
	private static final String PERFIL2 = "perfil";
	private static final String JOVEN2 = "joven";
	private static final String OP_TIPO_POL = "opTipoPol";
	private static final String ID_ASEG = "idAseg";
	private static final String MENSAJE = "mensaje";
	private static final String ID_POLIZA = "idPoliza";
	private static final String ALERTA = "alerta";
	private static final String METHOD = "method";
	private static final String POLIZA_BUSQUEDA = "polizaBusqueda";
	private static final String IDS_ROWS_CHECKED = "idsRowsChecked";
	private static final String OPERACION = "operacion";
	private static final String GRUPO_ENTIDADES = "grupoEntidades";
	private static final String POLIZA_BEAN = "polizaBean";
	
	private static final Log LOGGER = LogFactory.getLog(UtilidadesPolizaController.class);
	final ResourceBundle bundle = ResourceBundle.getBundle("displaytag");
	final ResourceBundle bundleSbp = ResourceBundle.getBundle("agp_sbp");
	private SeleccionPolizaManager seleccionPolizaManager;
	private PolizaManager polizaManager;
	private SiniestrosManager siniestrosManager;
	private FechaContratacionManager fechaContratacionManager;
	private ConsultaSbpManager consultaSbpManager;
	private IPagoEstadosPolizaManager pagoEstadosPolizaManager;
	private AseguradoManager aseguradoManager;
	private HistoricoManager historicoManager;
	private PagoPolizaManager pagoPolizaManager;
	private PolizaRCManager polizaRCManager;
	/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
	private DocumentacionGedManager documentacionGedManager;
	/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
	
	
	public UtilidadesPolizaController () {
		super();
		setCommandClass(Poliza.class);
		setCommandName(POLIZA_BEAN);
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		LOGGER.debug("init - UtilidadesPolizaController");
		ModelAndView mv = null;	
		int resFecha=0;
		final Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> param = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		final Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
    	boolean vieneDeMenuLateral = request.getParameter("origenLlamada") != null;    	
    	String vieneDeLimpiar = request.getParameter("vieneDeLimpiar");
		parameters.put(GRUPO_ENTIDADES, StringUtils.toValoresSeparadosXComas (usuario.getListaCodEntidadesGrupo(), false, false));
		parameters.put("vieneDeUtilidades", true);
		
		//ESC-30568 / GD-18421
        request.getSession().setAttribute("rdtoOrientativoPulsado","");
        request.getSession().setAttribute("rdtoHistPulsado","");
        //ESC-30568 / GD-18421
		
		String accion = StringUtils.nullToString(request.getParameter(OPERACION));
		String idPoliza = StringUtils.nullToString(request.getParameter("polizaOperacion"));
		Poliza polizaBean = null;
		//guardo en polizaBusqueda el filtro
		Poliza polizaBusqueda = (Poliza) command;

		String valorSTR = StringUtils.nullToString(request.getParameter("seleccionSTR"));
		String valorRC = StringUtils.nullToString(request.getParameter("seleccionRC"));
		String valorMOD  = StringUtils.nullToString(request.getParameter("seleccionMOD"));
		String tipoPago  = StringUtils.nullToString(request.getParameter("seleccionPago"));
		String valorRnv  = StringUtils.nullToString(request.getParameter("seleccionRnv"));		
		String valorFinanciada = StringUtils.nullToString(request.getParameter("seleccionFinanciada"));
		String valorIBAN = StringUtils.nullToString(request.getParameter("seleccionIBAN"));
		String valorRyD = StringUtils.nullToString(request.getParameter("seleccionRyD"));
		/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
		String valorCanalFirma = StringUtils.nullToString(request.getParameter("canalFirma"));
		String valorDocFirmada= StringUtils.nullToString(request.getParameter("docFirmada"));
		/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
		
		
		if (!valorSTR.equals("")){
			polizaBusqueda.setTienesiniestros(new Character(request.getParameter("seleccionSTR").charAt(0)));
		}
		if (!valorRC.equals("")){
			polizaBusqueda.setTieneanexorc(new Character(request.getParameter("seleccionRC").charAt(0)));
		}
		if (!valorMOD.equals("")){
			polizaBusqueda.setTieneanexomp(new Character(request.getParameter("seleccionMOD").charAt(0)));
		}
		if (!valorRnv.equals("")){
			polizaBusqueda.setRenovableSn(new Character(request.getParameter("seleccionRnv").charAt(0)));
		}
		if (!valorFinanciada.equals("")){
			polizaBusqueda.setEsFinanciada(valorFinanciada.trim().charAt(0));
		}
		if (!valorIBAN.equals("")){
			polizaBusqueda.setTieneIBAN(valorIBAN.trim().charAt(0));
		}
		if (!valorRyD.equals("")){
			polizaBusqueda.setEsRyD(valorRyD.trim().charAt(0));
		}
		
		/* P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
		if (!valorCanalFirma.equals("") || !valorDocFirmada.equals("")){
			GedDocPoliza gedDocPoliza = polizaBusqueda.getGedDocPoliza();
			if (gedDocPoliza == null) {
				gedDocPoliza = new GedDocPoliza();
			}
			
			if (gedDocPoliza.getCanalFirma() == null) {
				CanalFirma canalFirma = new CanalFirma();	
				try {
					canalFirma.setIdCanal(new Long(valorCanalFirma));
				}catch(NumberFormatException e) {
					LOGGER.debug("handle - valorcanalfirma no num�rico");
				}
				gedDocPoliza.setCanalFirma(canalFirma);
				parameters.put("canalIdCanal", valorCanalFirma);
				
			}
			
			if (!valorDocFirmada.equals("")){
				gedDocPoliza.setDocFirmada(new Character(request.getParameter("docFirmada").charAt(0)));
				parameters.put("docFirmada",valorDocFirmada);
			}

			polizaBusqueda.setGedDocPoliza(gedDocPoliza);
		}
		/* P73325 - RQ.04, RQ.05 y RQ.06  Fin */
		
		String idsRowsChecked    = StringUtils.nullToString(request.getParameter(IDS_ROWS_CHECKED));
		parameters.put(IDS_ROWS_CHECKED,      idsRowsChecked);
		
		/* Si vuelvo recojo de sesion el filtro de busqueda
		 * DAA 16/04/2012
		 */		
		// Boolean que indica si la poliza de busqueda se ha cargado de la sesion
		boolean plzDeSesion = false;
		//JANV 08/04/2016
		//Se a�ade al filtro acci�n="" para que recupere la busqueda original al ordenar columnas
		//si se ha visto el detalle de una poliza, al ordenaba filtraba por su id y solo sal�a 1. 
		if (("volver").equals(accion) || ("").equals(accion) || "true".equals(request.getParameter("recogerPolizaSesion"))){
			LOGGER.debug("init - operacion volver");
			if (request.getSession().getAttribute(POLIZA_BUSQUEDA) != null){
				polizaBusqueda = (Poliza) request.getSession().getAttribute(POLIZA_BUSQUEDA);
				request.getSession().removeAttribute("datosCabecera");
				plzDeSesion = true;
			}
		}
		// MPM 30/04/2012 - Al consultar tambien se guarda la poliza de busqueda, para el paso a definitiva
		else{
			if (!accion.equals("cambioOficina") && !accion.equals("cambioOficinaValidacion")){
				//guardo filtro en sesion
				request.getSession().setAttribute(POLIZA_BUSQUEDA, polizaBusqueda);				
			}
		}
		//JANV 08/04/2016
		//consecuencia de la anterior y de que la acci�n limpiar no se usa, se usa ahora el parametro 'vienedelimpiar'
		if ("limpiar".equalsIgnoreCase(accion) || "true".equals(vieneDeLimpiar) || vieneDeMenuLateral) {
			LOGGER.debug("init - operacion limpiar");
			
			polizaBusqueda = new Poliza();
			// MPM - 14/04/2016 - Si viene de la acci�n limpiar si indica que no se ha cargado la p�liza de sesi�n para no perder
			// los datos de b�squeda obligatorios por perfil de usuario.
			plzDeSesion = false;
			
			LOGGER.debug("end -  operacion limpiar");
		
		}
		else if ("pasarDefinitiva".equalsIgnoreCase(accion)) {
			LOGGER.debug("init - operacion pasar a definitiva");
			
			//Antes de grabar, pasar validaciones de webservices de VALIDACIoN y CaLCULO
			
			parameters.put("idpoliza", idPoliza);			
			parameters.put("origenllamada", "pasarDefinitiva");			
			request.getSession().setAttribute("confFiltroPolizas", polizaBusqueda);
			
			LOGGER.debug("end - operacion pasar a definitiva");
			
			polizaBean = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
			
			//verificar si la poliza se encuentra dentro del plazo de contratacion
			resFecha=seleccionPolizaManager.verPlazosPoliza(polizaBean);
			if ((resFecha ==0) || (resFecha ==1)){
				if (polizaBean.getTipoReferencia() == 'C'){
					parameters.put(METHOD, "doValidar");
					return new ModelAndView("redirect:/webservicesCpl.html", parameters);				
				} else {
					parameters.put(OPERACION, "validar");
					return new ModelAndView("redirect:/webservices.html", parameters);
				}
			}else{
				
				parameters.put(ALERTA, bundle.getString("mensaje.poliza.fueraPlazo.contratacion"));
				
			}	
				
		}
		else if ("anular".equalsIgnoreCase(accion))
		{
			/** Pet. 57627 ** MODIF TAM (11.10.2019) **/
			LOGGER.debug("init - operacion anular, redirigida a Anulacion y Rescisi�n");
			
			parameters.put(METHOD, "doconsultaAnulResc");
			parameters.put("origenLlamada", "UtilidadesPol");
			parameters.put("origen", "UtilidadesPol");
			
			parameters.put("idPolizaAnulyResc", idPoliza);
			parameters.put("idPolIniAyR", idPoliza);
			parameters.put("codUsuario", request.getSession().getAttribute("usuario"));
			 
			LOGGER.debug("end - operacion anular");
			
			mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, "doconsultaAnulResc").addAllObjects(parameters);
			return mv;
					
			/** 
			polizaBean = seleccionPolizaManager.getPolizaById(new Long(idPoliza)); 
			EstadoPoliza estado = new EstadoPoliza(Constants.ESTADO_POLIZA_ANULADA);
			polizaBean.setEstadoPoliza(estado);
            polizaBean.setFechaModificacion(new Date());
			seleccionPolizaManager.savePoliza(polizaBean);
			//TMR 28-06-2013 llamada al PL para insertar el estado y usuario en el historico 
			historicoEstadosManager.insertaEstado(Tabla.POLIZAS, 
					polizaBean.getIdpoliza(),usuario.getCodusuario(),Constants.ESTADO_POLIZA_ANULADA);
			/** Pet. 57627 ** MODIF TAM (11.10.2019) Fin **/		
			
			
		}
		else if ("recibos".equalsIgnoreCase(accion))
		{
			LOGGER.debug("init - operacion recibos");
			
			mv = new ModelAndView("redirect:recibosPoliza.html");
			mv.addObject(ID_POLIZA, idPoliza);
			
			LOGGER.debug("end - operacion recibos");
			
			return mv;
		}
		else if("siniestros".equals(accion)){
			LOGGER.debug("init - operacion siniestros");
			
			mv = new ModelAndView("redirect:siniestros.html");
			mv.addObject(ID_POLIZA,idPoliza);
			
			LOGGER.debug("end - operacion siniestros");
			
			return mv;
		}
		else if("anexoModificacion".equals(accion)) {
			LOGGER.debug("init - operacion anexo de modificacion");
			
			mv = new ModelAndView("redirect:declaracionesModificacionPoliza.html");
			
			mv.addObject(ID_POLIZA,idPoliza);
			
			LOGGER.debug("end - operacion anexo de modificacion");
			return mv;
		}else if("reduccionCapital".equals(accion)){
			LOGGER.debug("init - operacion reduccion de capital");
			
			String mensaje = "";
			if (polizaConSiniestros(idPoliza)) { 	
				mensaje = bundle.getString("mensaje.reduccionCapital.polizaConSiniestros");				
			}
			
			mv = new ModelAndView("redirect:declaracionesReduccionCapital.html");
			mv.addObject(ID_POLIZA,idPoliza);
			mv.addObject(ALERTA, mensaje);
			
			LOGGER.debug("end - operacion reduccion de capital");
			return mv;
		}else if("imprimir".equals(accion)){			
			LOGGER.debug("init - operacion informe poliza");
			LOGGER.debug("end - operacion informe poliza");
			polizaBean = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
			if (polizaBean.getTipoReferencia() == 'C'){
				return new ModelAndView("redirect:/informes.html").addObject(ID_POLIZA, idPoliza).addObject(METHOD, "doInformePolizaComplementaria");
			} else {
				return new ModelAndView("redirect:/informes.html").addObject(ID_POLIZA, idPoliza).addObject(METHOD, "doInformePoliza");
			}					
		}else if ("imprimirInforme".equals(accion)){				
			try {
				// ---- Para la paginacion del displaytag evitando que almacene todos los registros de la BD en memoria. ----
				Long numPageRequest = new Long("0");
				if (request.getParameter("page") == null)
					numPageRequest = Long.parseLong("1");
				else
					numPageRequest = Long.parseLong(request.getParameter("page"));
				//------------------Parametros de ordenacion
				String sort = StringUtils.nullToString(request.getParameter("sort"));
				String dir = StringUtils.nullToString(request.getParameter("dir"));
				//DAA 25/07/2012
				seleccionPolizaManager.actualizaTotalSuperficie(polizaBusqueda, 
						new BigDecimal[] { Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION,Constants.ESTADO_POLIZA_BAJA },
						usuario,tipoPago);
				// MPM - 05/09/12
				// Se quita la validacion del numero máximo de registros a imprimir ya que se hace previamente en la jsp
				List<Poliza> listaPolizas = seleccionPolizaManager.getPolizasButEstadosGrupoEnt(
								polizaBusqueda, new BigDecimal[] { Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION, Constants.ESTADO_POLIZA_BAJA },
								usuario, numPageRequest.intValue(), sort, dir,tipoPago,usuario.getListaCodOficinasGrupo());
				
				request.setAttribute("listaPol", listaPolizas);
				return new ModelAndView("forward:/informes.html?method=doInformeUtilidades");
			} catch (Exception e) {
				logger.error("Error al gererar el informe de utilidades de pólizas", e);
			}
			
		}
		else if("cambioOficina".equals(accion)){
			LOGGER.debug("Init - Cambio Oficina");
			/* Recojo de sesion el filtro para despues hacer el cambio y la validacion 
			 * DAA 16/04/12
			 */
			polizaBusqueda = (Poliza) request.getSession().getAttribute(POLIZA_BUSQUEDA);
			String listCheck = request.getParameter(IDS_ROWS_CHECKED);
			String oficina = request.getParameter("codoficinaCO");
			String[] arrayCheck = listCheck.split(";");
			
			for (int i=0;i<arrayCheck.length;i++) {
				String idPol = arrayCheck[i];
				if (!idPol.equals("")) {
					Poliza polizaBuscada = new Poliza();
					polizaBuscada = seleccionPolizaManager.getPolizaById(new Long(idPol));
					// antes de grabar,relleno la oficina con ceros a la izq. hasta
					// completar el máximo permitido
					Integer ofi = new Integer(StringUtils.nullToString(oficina));
					polizaBuscada.setOficina(String.format("%04d", ofi.intValue()));
					polizaBuscada.setFechaModificacion(new Date());
					seleccionPolizaManager.savePoliza(polizaBuscada);
					parameters.put(MENSAJE, bundle.getString("mensaje.modificacion.OK"));
					LOGGER.debug("End - Cambio Oficina");
				}
			}
			
		}
		else if("cambioTitular".equals(accion)){
			LOGGER.debug("Init - Cambio Titular");
			polizaBusqueda = (Poliza) request.getSession().getAttribute(POLIZA_BUSQUEDA);
			String listCheck = request.getParameter(IDS_ROWS_CHECKED);
			String idAseguradoCambioTitularStr = request.getParameter("idAseguradoCambioTitular");
			String[] arrayCheck = listCheck.split(";");			
			String idPol = arrayCheck[1];//Viene siempre con un ";" al principio, por lo que hay que coger el elemento 1, no el 0
			if (!idPol.equals("")) {
				Poliza polizaBuscada = new Poliza();
				polizaBuscada = seleccionPolizaManager.getPolizaById(new Long(idPol));
				Long idAseguradoCambioTitular = new Long(idAseguradoCambioTitularStr);
				Asegurado nuevoAseguradoCambioTitular = aseguradoManager.getAsegurado(idAseguradoCambioTitular);
				historicoManager.grabarHistoricoCambioTitular(polizaBuscada.getIdpoliza(), usuario.getCodusuario(), polizaBuscada.getAsegurado().getFullName(), nuevoAseguradoCambioTitular.getFullName());
				polizaBuscada.setAsegurado(nuevoAseguradoCambioTitular);
				polizaBuscada.setFechaModificacion(new Date());
				seleccionPolizaManager.savePoliza(polizaBuscada);
				parameters.put(MENSAJE, bundle.getString("mensaje.modificacion.OK"));
			}			
			LOGGER.debug("End - Cambio Titular");
		}
		else if("cambioIBAN".equals(accion)){
			LOGGER.debug("Init - Cambio IBAN");
			polizaBusqueda = (Poliza) request.getSession().getAttribute(POLIZA_BUSQUEDA);
			String listCheck = request.getParameter(IDS_ROWS_CHECKED);
			String nuevoIbanCompleto = request.getParameter("nuevoIbanCompleto");
			String isganado = request.getParameter("isganado");
			String nuevoIban = null;
			String nuevaCcc = null;
			
			if (nuevoIbanCompleto.length() != 24) {
				LOGGER.debug("IBAN incompleto");
			} else {
				
				nuevoIban = nuevoIbanCompleto.substring(0, 4);
				nuevaCcc = nuevoIbanCompleto.substring(4, 24);
				String[] arrayCheck = listCheck.split(";");				
				
				if (isganado.equals("true")) {
					String idPol = arrayCheck[1];//Viene siempre con un ";" al principio, por lo que hay que coger el elemento 1, no el 0
					if (!idPol.equals("")) {
						Poliza polizaBuscada = new Poliza();
						polizaBuscada = seleccionPolizaManager.getPolizaById(new Long(idPol));
		
						String ibanCompletoAnterior = null;
						Set<PagoPoliza> colPagoPoliza = polizaBuscada.getPagoPolizas();
						Iterator<PagoPoliza> itPagoPoliza = colPagoPoliza.iterator();
						
						PagoPoliza pagoPoliza = null;
						if (colPagoPoliza.isEmpty()){
							parameters.put(ALERTA, bundle.getString("mensaje.modificacion.inexistente.KO"));
						}
						
						while(itPagoPoliza.hasNext()){
							pagoPoliza = itPagoPoliza.next();
							ibanCompletoAnterior = StringUtils.nullToString(pagoPoliza.getIban()) + StringUtils.nullToString(pagoPoliza.getCccbanco());
							historicoManager.grabarHistoricoCambioIBAN(new Long(idPol), usuario.getCodusuario(), ibanCompletoAnterior, nuevoIbanCompleto);
							pagoPoliza.setIban(nuevoIban);
							pagoPoliza.setCccbanco(nuevaCcc);
							pagoPoliza.setBanco(null);
							pagoPolizaManager.savePagoPoliza(pagoPoliza);
							parameters.put(MENSAJE, bundle.getString("mensaje.modificacion.OK"));
						}
					}
				}
				else{
					parameters.put(ALERTA, bundle.getString("alerta.poliza.modificacion.iban.KO"));
				}
			}
			LOGGER.debug("End - Cambio IBAN");
			
		}else if("cambiarUsuario".equals(accion)){
			LOGGER.debug("Init - Cambio Usuario");
			//String listCheck = request.getParameter("listCheck");
			String listCheck = request.getParameter(IDS_ROWS_CHECKED);
			String codUsuario = request.getParameter("usuarioNuevo");
			String[] arrayCheck = listCheck.split(";");
			
			if (codUsuario != null){
				if (!listCheck.equals("")){
					param = this.seleccionPolizaManager.cambiarUsuario(arrayCheck, codUsuario, usuario);
					if (param.get(ALERTA)!= null) 
						parameters.put(ALERTA, param.get(ALERTA));
					else if (param.get("alerta2")!= null) 
						parameters.put("alerta2", param.get("alerta2"));
					else
						parameters.put(MENSAJE, bundle.getString("mensaje.cambioUsuario.OK"));
					
				} else {
					parameters.put(ALERTA, bundle.getString("cambioUsuario.noCheck.msgError"));
				}
			}else{
				parameters.put(ALERTA, bundle.getString("cambioUsuario.noExisteUsuario"));
			}
			LOGGER.debug("End - Cambio Usuario");
		}else if("MultiGrabDef".equals(accion)){
			LOGGER.debug("Init - MultiGrabDef Poliza");
			//String listIdPolizas = request.getParameter("listGrabDefPolizas");
			String listIdPolizas = request.getParameter(IDS_ROWS_CHECKED);
			String grFueraContratacion = StringUtils.nullToString(request.getParameter("grabFueraContratacion"));
			String actualizarSbp = StringUtils.nullToString(request.getParameter("actualizarSbp"));
			String tableModsParcsNoDefin = "";			
			
			List<Poliza> listPolizas = new ArrayList<Poliza>();
			listPolizas = getListObjPolFromString(listIdPolizas);
			
			/*Al pasar a definitiva, añadir un control que verifique que no hay polizas con el mismo lineaseguroid, 
			 * nifAsegurado y clase. Mejora 120 . tamara 23/04/2012
			 */
			Poliza polizaOtroColectivo = null;
			List<Poliza> lstPolizasOtroColectivo = new ArrayList<Poliza>();
			boolean errorPasarDefinitiva= false;
			BigDecimal estadosPolizaNoIncluir [] = new BigDecimal [3];
			estadosPolizaNoIncluir[0]=new BigDecimal(1); //pte. validacion
			estadosPolizaNoIncluir[1]=new BigDecimal(2); //Grb. provisional
			estadosPolizaNoIncluir[2]=new BigDecimal(4); //anulada
			
			for (int i=0;i<listPolizas.size();i++){
				polizaOtroColectivo = new Poliza();
				Poliza pol = listPolizas.get(i);
				polizaOtroColectivo.setAsegurado(pol.getAsegurado());
				polizaOtroColectivo.setColectivo(null);
				polizaOtroColectivo.setClase(pol.getClase());
				polizaOtroColectivo.setLinea(pol.getLinea());
				lstPolizasOtroColectivo = seleccionPolizaManager.getPolizasButEstados(polizaOtroColectivo, estadosPolizaNoIncluir);
				//En esta lista están las polizas del mismo asegurado, lineaseguroid y clase para cualquier colectivo.
				if (lstPolizasOtroColectivo.size()>0) {
					parameters.put(ALERTA, bundle.getString("mensaje.pasarDefinitiva.otroColectivo.KO"));
					errorPasarDefinitiva = true;
					i= listPolizas.size();
				}	
			}
			if (!errorPasarDefinitiva){
				// validamos periodo contratacion de n polizas (modulos y parcelas).
				// no es necesario validar si el usuario es perfil 0 y el usuario decide grabar la poliza 
				// aunque este fuera del ámbito de contratacion
				if (grFueraContratacion.equals("")){
					tableModsParcsNoDefin = fechaContratacionManager.getTableModsParcsNoDefin(listPolizas);
				} else {
					tableModsParcsNoDefin = "-1";
				}
			
				if(tableModsParcsNoDefin.equals("-1"))
				{
						String listaux = "";
						if(!listIdPolizas.equals("")){
							Poliza polizaBuscada = new Poliza();
							Map<String, Object> mapaPolizas = new HashMap<String, Object>();
							String[] listaFin = listIdPolizas.split(";");				
							for (int j=0; j<listaFin.length; j++){
								String id = listaFin[j];
								if (!id.equals("")){
									listaux = listaux + id + "|";
									polizaBuscada = seleccionPolizaManager.getPolizaById(new Long(id));
									mapaPolizas.put(id, polizaBuscada);
								}
							}
							
							parameters.put("listIdPolizas", listaux);
							parameters.put("mapaPolizas", mapaPolizas);
							parameters.put("actualizarSbp", actualizarSbp);
							LOGGER.debug("End - MultiGrabDef Poliza");
							return new ModelAndView("/moduloUtilidades/resultadoMultiGrabDefPoliza", POLIZA_BEAN, polizaBean).addAllObjects(parameters);
						}else{
							parameters.put(ALERTA, bundle.getString("cambiooficinaNoCheck.msgError"));
						}
				}else{
					parameters.put("popUpAmbiCont", "true");
					parameters.put("tableInfoNoDefinitiva", tableModsParcsNoDefin);
					parameters.put("listGrabDefPolizas", listIdPolizas);
					parameters.put("botonPerfil0", "1");
				}
			}
			
		}else if("polizasAseg".equals(accion)){
			LOGGER.debug("Init - polizasAseg Poliza");
			String idAseg = request.getParameter(ID_ASEG);
			String opTipoPol = StringUtils.nullToString(request.getParameter(OP_TIPO_POL));
			String joven = StringUtils.nullToString(request.getParameter(JOVEN2));
			String prof = StringUtils.nullToString(request.getParameter("prof"));
			String perfil = "";
			perfil = usuario.getPerfil().substring(4);
			Asegurado aseguradoBean = polizaManager.getAsegurado(idAseg);
			
			List<Poliza> listPolizasAseg= polizaManager.getListPolizasAseg(Long.parseLong(idAseg),polizaBusqueda, opTipoPol);
			
			parameters.put(PERFIL2, perfil);
			parameters.put(ID_ASEG, idAseg);
			parameters.put(JOVEN2, joven);
			parameters.put("prof", prof);
			parameters.put(LISTA_POLIZAS, listPolizasAseg);
			parameters.put("nifcifAseg", StringUtils.nullToString(aseguradoBean.getNifcif()));
			parameters.put("nomAseg", StringUtils.nullToString(aseguradoBean.getNombre()) + " " + StringUtils.nullToString(aseguradoBean.getApellido1()) + " " + StringUtils.nullToString(aseguradoBean.getApellido2()));
			parameters.put("entidad", StringUtils.nullToString(aseguradoBean.getEntidad().getCodentidad()));
			parameters.put(OP_TIPO_POL, opTipoPol);
			
			LOGGER.debug("End - polizasAseg Poliza");
			return new ModelAndView("moduloAdministracion/asegurados/verPolizasModifSubAseg", POLIZA_BEAN, polizaBusqueda).addAllObjects(parameters);
		}else if("irSeleccionSubvenciones".equals(accion)){
			String jovenAgr= StringUtils.nullToString(request.getParameter(JOVEN2));
			String prof= StringUtils.nullToString(request.getParameter("prof"));
			String idAseg = StringUtils.nullToString(request.getParameter(ID_ASEG));
			Asegurado aseguradoBean = polizaManager.getAsegurado(idAseg);
			return new ModelAndView("moduloAdministracion/asegurados/modifSubAseg", "aseguradoBean", aseguradoBean)
			.addObject(JOVEN2,jovenAgr)
			.addObject("prof", prof);
		}else if("retornarAsegSinModificar".equals(accion)){
			String jovenAgr= StringUtils.nullToString(request.getParameter(JOVEN2));
			String prof= StringUtils.nullToString(request.getParameter("prof"));
			String idAseg = StringUtils.nullToString(request.getParameter(ID_ASEG));
			return new ModelAndView("redirect:/asegurado.html").addObject(ID_ASEG, idAseg)
			.addObject(JOVEN2,jovenAgr)
			.addObject(OPERACION,"noModifSubv")
			.addObject("prof", prof);
		}
		// ----------------------------------------------------------------
		// VER ACUSE RECIBO
		// ----------------------------------------------------------------
		else if ("verAcuseRecibo".equals(accion)){
			LOGGER.debug("init - operacion ver Acuse Recibo");
			Map<String, Object> parametros = new HashMap<String, Object>();
			es.agroseguro.acuseRecibo.Error[] errores = null;
			String refPoliza = null;
			BigDecimal linea = null;
			BigDecimal plan = null;
			BigDecimal idEnvio = null;
			
			LOGGER.debug("idpoliza: " + idPoliza);
			if(idPoliza != null){
				polizaBean = seleccionPolizaManager.getPolizaById(new Long(idPoliza));
				
				refPoliza = polizaBean.getReferencia();
				linea = polizaBean.getLinea().getCodlinea();
				plan = polizaBean.getLinea().getCodplan();
				idEnvio = polizaBean.getIdenvio();
				
				LOGGER.debug("refPoliza: " + refPoliza + " linea: " + linea + " plan: " + plan + " idEnvio: " + idEnvio);
				
				if (idEnvio == null || refPoliza == null) {
					parametros.put(ID_POLIZA, idPoliza);
					parametros.put("errLength", 0);
					parametros.put(OPERACION, "poliza");
					return new ModelAndView("/moduloUtilidades/erroresContratacion", POLIZA_BEAN, polizaBean).addAllObjects(parametros);
							
				} else {
					
					// Se obtiene un array con los errores
					errores = seleccionPolizaManager.getFicheroContenido(idEnvio, refPoliza, linea, plan);

					if (errores.length == 0) {
						parametros.put("errLength", 0);
						parametros.put(ID_POLIZA, idPoliza);
						parametros.put(OPERACION, "poliza");
					} else {
						parametros.put(ID_POLIZA, idPoliza);
						parametros.put("errores", errores);
						parametros.put("errLength", errores.length);
						parametros.put(OPERACION, "poliza");
					}
					
					return new ModelAndView("/moduloUtilidades/erroresContratacion",POLIZA_BEAN, polizaBean).addAllObjects(parametros).addObject("errores", errores);
				}
				
			}else{
				parameters.put(ALERTA, bundle.getString("mensaje.error.general"));
			}
		// ----------------------------------------------------------------
		// DUPLICADOS INFORMATICOS
		// ----------------------------------------------------------------			
		}else if ("copy".equalsIgnoreCase(accion)) {
			LOGGER.debug("init - operacion copy");
			String perfil = "";
			perfil = usuario.getPerfil().substring(4);
			
			if (null != perfil && !"".equalsIgnoreCase(perfil))
			{
				switch (new Integer(perfil).intValue())
				{
					case 1: polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							break;
					case 3:	polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
					        polizaBusqueda.setOficina(usuario.getOficina().getNomoficina());
							break;				
					case 4: 
							polizaBusqueda.setUsuario(usuario);
							break;
					default:
							break;
				}
			}

			polizaBusqueda.getLinea().setLineaseguroid(seleccionPolizaManager.getLineaseguroId(polizaBusqueda.getColectivo().getLinea().getCodplan(), polizaBusqueda.getColectivo().getLinea().getCodlinea()));
	        Long numPageRequest = new Long("0");
			if(request.getParameter("page") == null)
				numPageRequest = Long.parseLong("1");
			else
			    numPageRequest = Long.parseLong(request.getParameter("page"));
			
			String opTipoPol = StringUtils.nullToString(request.getParameter(OP_TIPO_POL));
			if (opTipoPol.equals("1"))
				polizaBusqueda.setTipoReferencia('P');
			if (opTipoPol.equals("0"))
				polizaBusqueda.setTipoReferencia('C');
			polizaBusqueda.getEstadoPoliza().setIdestado(Constants.ESTADO_POLIZA_DEFINITIVA);
			
			String sort = StringUtils.nullToString(request.getParameter("sort"));
			String dir = StringUtils.nullToString(request.getParameter("dir"));
			String listadoVacioCopy = StringUtils.nullToString(request.getParameter("listadoVacioCopy"));
			PaginatedListImpl<Poliza> listaPolizas = new PaginatedListImpl<Poliza>();
			if ("true".equals(listadoVacioCopy)){
				polizaBusqueda.setCodmodulo("ZZ");
			}
			listaPolizas = seleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt(polizaBusqueda,  
					new BigDecimal[]{Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION,Constants.ESTADO_POLIZA_BAJA}, usuario, numPageRequest.intValue(),sort,dir,tipoPago);
			String imprimirPoliza = StringUtils.nullToString(request.getParameter("imprimirPoliza"));
			if (imprimirPoliza.equals("true")){
				if (listaPolizas.getList().size()>0){
					idPoliza=listaPolizas.getList().get(0).getIdpoliza().toString();
					String tipoRefPoliza= listaPolizas.getList().get(0).getTipoReferencia().toString();
					String  idPol= listaPolizas.getList().get(0).getIdpoliza().toString();
					String codPlan= listaPolizas.getList().get(0).getLinea().getCodplan().toString();
					String refPoliza = listaPolizas.getList().get(0).getReferencia();
					parameters.put("datoTipoRefPoliza", tipoRefPoliza);
					parameters.put("datoIdPol", idPol);
					parameters.put("datoCodPlan", codPlan);
					parameters.put("datoRefPoliza", refPoliza);
					parameters.put("btnImprimirPolizaActivado", "true");
				}else{
					parameters.put(ALERTA, bundle.getString("mensaje.poliza.copy.NoEcontrado"));
				}
				parameters.put("totalListSize", listaPolizas.getFullListSize());
				parameters.put(LISTA_POLIZAS, listaPolizas);
				parameters.put(PERFIL2, perfil);
				parameters.put(GRUPO_ENTIDADES, StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false,false));
				parameters.put(OP_TIPO_POL, opTipoPol);
				mv = new ModelAndView("moduloUtilidades/listadoCopys", POLIZA_BEAN, polizaBusqueda);
				mv.addAllObjects(parameters);
				return mv;
			}else{
				// ---------------------------------------------------------------
				parameters.put("totalListSize", listaPolizas.getFullListSize());
				LOGGER.debug("Establecemos totalListSize");
				parameters.put(LISTA_POLIZAS, listaPolizas);
				LOGGER.debug("Establecemos totalListSize");
				parameters.put(PERFIL2, perfil);
				LOGGER.debug("Establecemos perfil");
				parameters.put(GRUPO_ENTIDADES, StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false,false));
				LOGGER.debug("Establecemos grupo de Entidades");
				parameters.put(OP_TIPO_POL, opTipoPol);
				mv = new ModelAndView("moduloUtilidades/listadoCopys", POLIZA_BEAN, polizaBusqueda);
				mv.addAllObjects(parameters);
				LOGGER.debug("end - operacion copy");
				LOGGER.debug("end - UtilidadesPolizaController");
				return mv;
			}
		}
		else if ("recibosCopy".equalsIgnoreCase(accion))
		{
			LOGGER.debug("init - operacion recibosCopy");
			String tipoRefPoliza= request.getParameter("tipoRefPoliza");
			mv = new ModelAndView("redirect:recibosPoliza.html");
			mv.addObject(ID_POLIZA, idPoliza);
			mv.addObject("tipoRefPoliza", tipoRefPoliza);
			LOGGER.debug("end - operacion recibosCopy");
			return mv;
		}
		else if ("eliminar".equals(accion)){
			LOGGER.debug("init - operacion eliminar");
			Long idPolizaDelete = null;
			try {
				
				idPolizaDelete = new Long(request.getParameter(ID_POLIZA));
				Poliza poliza = seleccionPolizaManager.getPolizaById(idPolizaDelete);
				
				// *** SBP: borramos la poliza de Sbp asociada ***
				String msjSbp = consultaSbpManager.borrarPolizaSbpByPoliza(poliza, usuario, realPath);
				if (msjSbp.equals("msjSbpBorrada")){
					parameters.put("mensaje2", bundleSbp.getString("mensaje.borrar.ok"));
				}else if (msjSbp.equals("msjSbpRecalculada")){
					parameters.put("mensaje2", bundleSbp.getString("mensaje.grabacion.definitiva.SinCpl"));
				}
				
				if(this.polizaManager.esPolizaGanadoByIdPoliza(idPolizaDelete)){
					this.borrarPolizaRC(idPolizaDelete);
				}
				
				//TMR.Facturacion. Le añadimos el usuario para facturar las bajas de las polizas
				seleccionPolizaManager.borrarPoliza(poliza,usuario);
				//SOLO PARA POLIZA PRINCIPAL: borramos todas las polizas complementarias de la principal al pasar a definitiva
				if(poliza.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
					polizaManager.borrarPolizasComplementariasByPpal (poliza.getIdpoliza());
				}
				parameters.put(MENSAJE, bundle.getString("mensaje.baja.OK"));
				
			} catch (BusinessException be) {
				logger.error("Se ha producido un error durante el borrado de la poliza", be);
				parameters.put(ALERTA, bundle.getString("mensaje.baja.KO"));
			} catch (Exception ex){
				logger.error("EXCEPTION: Se ha producido un error durante el borrado de una poliza.IDPOLIZA="+idPolizaDelete.toString(), ex);
			}
			LOGGER.debug("End - operacion eliminar");
		}
		else if ("borradoMasivo".equals(accion)){
			LOGGER.debug("init - Borrado Masivo de polizas");
			//String listIdPolizas = request.getParameter("listBorradoPolizas");
			String listIdPolizas = request.getParameter(IDS_ROWS_CHECKED);
			String id = "";
			try {
				if(!listIdPolizas.equals("")){
					Poliza polizaBorrar = new Poliza();
					
					String[] listaFin = listIdPolizas.split(";");				
					
					for (int j=0; j<listaFin.length; j++){
						if (!listaFin[j].equals("")){
							id = listaFin[j];
							
							Long idPolizaPrincipal = Long.parseLong(id);
							
							polizaBorrar = seleccionPolizaManager.getPolizaById(idPolizaPrincipal);
							
							// *** SBP: borramos la poliza de Sbp asociada ***
							consultaSbpManager.borrarPolizaSbpByPoliza(polizaBorrar, usuario, realPath);
							//************************************************

							if(this.polizaManager.esPolizaGanadoByIdPoliza(idPolizaPrincipal)){
								this.borrarPolizaRC(idPolizaPrincipal);
							}
							
							//TMR.Facturacion. Le añadimos el usuario para facturar las bajas de las polizas
							seleccionPolizaManager.borrarPoliza(polizaBorrar,usuario);
							//SOLO PARA POLIZA PRINCIPAL: borramos todas las polizas complementarias de la principal al pasar a definitiva
							if(polizaBorrar.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
								polizaManager.borrarPolizasComplementariasByPpal (polizaBorrar.getIdpoliza());
							}
						}
					}
				}
				
				parameters.put(MENSAJE, bundle.getString("mensaje.baja.OK"));
			} catch (BusinessException be) {
				logger.error("Se ha producido un error durante el borrado masivo de polizas", be);
				parameters.put(ALERTA, bundle.getString("mensaje.baja.KO"));
			} catch (Exception ex){
				logger.error("EXCEPTION: Se ha producido un error durante el borrado masivo de polizas. IDPOLIZA=" + id, ex);
			}
			LOGGER.debug("End - Borrado Masivo de polizas");
		}
		
		LOGGER.debug("init - operaciones generales");
		
		String perfil = "";
		perfil = usuario.getPerfil().substring(4);
		
		if (null != perfil && !"".equalsIgnoreCase(perfil))
		{
			// DAA 16/04/12 si la accion es volver se mantiene el objeto polizaBusqueda para mantener el filtro.
			// Si no se ha cargado la poliza de sesion se incluyen filtros por defecto en el objeto de busqueda
			if ((StringUtils.nullToString(request.getParameter("filtro")).equals("")) && !plzDeSesion) 
			{
				switch (new Integer(perfil).intValue())
				{
					// MPM - 26/10/2012 - El perfil 2 ve las mismas polizas que el 1
					case Constants.COD_PERFIL_1:
							polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							break;
					
					case Constants.COD_PERFIL_3: case Constants.COD_PERFIL_2:	
							polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
					        polizaBusqueda.setOficina(usuario.getOficina().getId().getCodoficina().toString());
					        polizaBusqueda.setNombreOfi(usuario.getOficina().getNomoficina());
							break;				
					case Constants.COD_PERFIL_4: 
							polizaBusqueda.setUsuario(usuario);
							polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
							polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
							break;
					case Constants.COD_PERFIL_5:
							polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
							polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
							break;
					default:
							break;
				}
			}
		}
		
		//campos por defecto en caso de usuario externo
        if (usuario.getExterno().equals(Constants.USUARIO_EXTERNO)) {
        	if (usuario.getTipousuario().compareTo(Constants.PERFIL_1)==0) {
        		polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
        		polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        	}else if (usuario.getTipousuario().compareTo(Constants.PERFIL_3)==0) {
        		polizaBusqueda.getColectivo().getTomador().getId().setCodentidad(usuario.getOficina().getEntidad().getCodentidad());
        		polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodentidad(usuario.getSubentidadMediadora().getId().getCodentidad());
        		polizaBusqueda.getColectivo().getSubentidadMediadora().getId().setCodsubentidad(usuario.getSubentidadMediadora().getId().getCodsubentidad());
        		polizaBusqueda.getColectivo().getTomador().getEntidad().setNomentidad(usuario.getSubentidadMediadora().getEntidad().getNomentidad());
        		polizaBusqueda.setOficina(usuario.getOficina().getId().getCodoficina().toString());
        		polizaBusqueda.setNombreOfi(usuario.getOficina().getNomoficina());
        		polizaBusqueda.getUsuario().setDelegacion(usuario.getDelegacion());
        	}
		}	
		// ---------------------------------------------
		//           PARA TODAS LAS OPERACIONES
		// ---------------------------------------------
		
        // MPM - 10/02/2016
        if (polizaBusqueda.getColectivo().getLinea().getCodplan() != null && polizaBusqueda.getColectivo().getLinea().getCodlinea() != null) { 
        	polizaBusqueda.getLinea().setLineaseguroid(seleccionPolizaManager.getLineaseguroId(polizaBusqueda.getColectivo().getLinea().getCodplan(), polizaBusqueda.getColectivo().getLinea().getCodlinea()));
        }
		
        //polizaBusqueda.setClase(claseFiltrar);
		 
        Long numPageRequest = new Long("0");
		
		if(request.getParameter("page") == null)
			numPageRequest = Long.parseLong("1");
		else
		    numPageRequest = Long.parseLong(request.getParameter("page"));
		
		//------------------Parametros de ordenacion
		
		String sort = StringUtils.nullToString(request.getParameter("sort"));
		String dir = StringUtils.nullToString(request.getParameter("dir"));
		
		PaginatedListImpl<Poliza> listaPolizas = null;

		final List <EstadoPoliza> estadosPoliza = seleccionPolizaManager.getEstadosPoliza(new BigDecimal[]{});	
		
		//TMR 02/08/2013 Proceso de pagos
		final List <EstadosPoliza> estadosPolizaPago = pagoEstadosPolizaManager.getEstadosPagoPoliza();
		
		final List <CanalFirma> canalesFirma = documentacionGedManager.getCanalesFirma();	
		parameters.put("canalFirma", canalesFirma);
		
		//DAA 18/12/2013 Al entrar por primera vez no realiza la busqueda de polizas 
		//solo si no vieneDeMenuLateral y no vieneDeLimpiar
		if (!vieneDeMenuLateral && !("true").equals(vieneDeLimpiar)){
			listaPolizas = seleccionPolizaManager.getPaginatedListPolizasButEstadosGrupoEnt(polizaBusqueda,  
					new BigDecimal[]{Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION,Constants.ESTADO_POLIZA_BAJA}, usuario, numPageRequest.intValue(),sort,dir,tipoPago);
		}  	

    	// DAA 17/05/2012 obtengo una lista con todos los idpoliza
    	// MPM 17/01/2013 - Solo se buscan los id de poliza si la busqueda ha devuelto resultados
    	String polizasString = "";
        if (listaPolizas != null && listaPolizas.getFullListSize() > 0) {
	    	polizasString = seleccionPolizaManager.getListPolizasString(polizaBusqueda,  
	    			new BigDecimal[]{Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION,Constants.ESTADO_POLIZA_BAJA}, usuario,tipoPago);
	        if (!StringUtils.nullToString(request.getParameter(ALERTA)).equals("")){
	        	parameters.put(ALERTA, request.getParameter(ALERTA));
	        }
	        if (!StringUtils.nullToString(request.getParameter(MENSAJE)).equals("")){
				parameters.put(MENSAJE, request.getParameter(MENSAJE));
	        }	
        }
        parameters.put("polizasString", polizasString);
    	
    	
    	
    	// si la accion esta rellena vaciamos los checks seleccionados
		if (!accion.equals("")){
			parameters.put(IDS_ROWS_CHECKED, "");
		}
		
		//Fase 4.habilitamos o no el boton en funcion de su lineaSeguroid
		PolizaSbp polizaSbp = new PolizaSbp();
		polizaSbp.setIncSbpComp('N');
		parameters.put("polizaSbp", polizaSbp);
		//Fin Fase 4
		
		// MPM - Paso a definitiva
		Poliza polizaDefinitiva = new Poliza ();
		polizaDefinitiva.setIdpoliza(new Long (0));
		parameters.put("polizaDefinitiva", polizaDefinitiva);
		
		String alerta = request.getParameter(ALERTA);
        if (alerta!=null){
        	parameters.put(ALERTA, alerta);
        }
        
        
        //DAA 10/01/2013
        if(listaPolizas != null){
	        parameters.put("totalListSize", listaPolizas.getFullListSize());
			parameters.put(LISTA_POLIZAS, listaPolizas);
			LOGGER.debug("Establecemos el listaPolizas");
        }
        else{
        	parameters.put("tablaPolizas", "vacio");
        	LOGGER.debug("La tabla de polizas va vacia");
        }
        parameters.put("estadosPolizaPago", estadosPolizaPago);
		parameters.put("estados", estadosPoliza);
		parameters.put(PERFIL2, perfil);
		parameters.put(GRUPO_ENTIDADES, StringUtils.toValoresSeparadosXComas(usuario.getListaCodEntidadesGrupo(),false,false));
		parameters.put("opcionSTR", polizaBusqueda.getTienesiniestros());
		parameters.put("opcionRC", polizaBusqueda.getTieneanexorc());
		parameters.put("opcionMOD", polizaBusqueda.getTieneanexomp());
		parameters.put("opcionPago", tipoPago);
		parameters.put("opcionRnv", polizaBusqueda.getRenovableSn());
		parameters.put("externo",usuario.getExterno());		
		parameters.put("opcionRyD", polizaBusqueda.getEsRyD());
		parameters.put("opcionFinanciada", polizaBusqueda.getEsFinanciada());
		parameters.put("opcionIBAN", polizaBusqueda.getTieneIBAN());
		parameters.put("grupoOficinas",StringUtils.toValoresSeparadosXComas(usuario.getListaCodOficinasGrupo(),false,false));
		mv = new ModelAndView("moduloUtilidades/cambiopolizasdefinitivas", POLIZA_BEAN, polizaBusqueda);
		mv.addAllObjects(parameters);
		LOGGER.debug("end - UtilidadesPolizaController operaciones generales");
		return mv;
	}

	private void borrarPolizaRC(Long idPolizaPrincipal) throws BusinessException {
		PolizasRC polizaRC = this.polizaRCManager.getPolizaRC(idPolizaPrincipal);
		if(polizaRC != null){
			LOGGER.debug(String.format("Poliza de RC para la Poliza Principal (%d) encontrada", idPolizaPrincipal));
			this.polizaRCManager.deletePolizaRC(polizaRC);
			LOGGER.debug("Poliza de RC borrada");
		}
	}
	
	/**
	 * 
	 * @param idPoliza
	 * @return
	 * @throws Exception
	 */
	public boolean polizaConSiniestros (String idPoliza) throws Exception {
		LOGGER.debug("init - [metodo] polizaConSiniestros");
		
		boolean resultado = false;
		
		if (!StringUtils.nullToString(idPoliza).equals("")) {	
			try {
				BigDecimal totalSiniestros = siniestrosManager.getNumTotalSiniestros(new Long(idPoliza));
				if (totalSiniestros != null && totalSiniestros.intValue() > 0) {
					resultado = true;
				} else {
					resultado = false;
				}
			} catch (DAOException de) {			
				LOGGER.error("Se ha producido un error durante la consulta de los siniestros de una poliza", de);			
				throw new Exception();
			}
		} else {
			resultado = false;
		}
		
		LOGGER.debug("end - [metodo] polizaConSiniestros");
		return resultado;
	}

	/* COMENTO EL METODO PORQUE NO SE USA 19-Julio-2013
	 * 
	 * @param idPoliza
	 * @return
	 * @throws Exception
	 * /
	private AcuseRecibo getAcuseRecibo(Long idPoliza,String codmodulo) throws Exception {
		List<EnvioAgroseguro> listEnvios = new ArrayList<EnvioAgroseguro>();
		listEnvios = polizaManager.getEnvioAgroseguro (idPoliza,codmodulo);
		es.agroseguro.acuseRecibo.AcuseRecibo acuseRecibo = null;
		if (listEnvios.size()>0){
			
			//cojo el primero que es el ultimo insertado.
			EnvioAgroseguro envio = (EnvioAgroseguro) listEnvios.get(0);
			if (envio.getCalculo() != null){
				String calculo = WSUtils.convertClob2String(envio.getCalculo());
				acuseRecibo = AcuseRecibo.Factory.parse(new StringReader(calculo));
			}
		}
		return acuseRecibo;
		
	}*/
	/*
	/**
	 * Metodo para obtener la lista de errores que se pueden omitir.
	 * @param servicio Servicio para el cual se desean filtrar los errores.
	 *
	public  List<ErrorWsAccion> getListaErroresOmitir(String servicio, ) 
	{
		ErrorWsFiltro filtro = new ErrorWsFiltro(servicio, lineaseguroid);
		return polizaManager.getObjects(filtro);
	}*/
	
	/**
	 * Devuelve un lista de objetos poliza.
	 * @param seleccionPolizaManager
	 */
	private List<Poliza> getListObjPolFromString(String listIdPolizas)
	{
		Poliza poliza;
		List<Poliza> listPolizas = new ArrayList<Poliza>();
		String[] listaFin = listIdPolizas.split(";");	
		
		for (int j=0; j<listaFin.length; j++){
			String id = listaFin[j];
			if (!id.equals("")){
				poliza = seleccionPolizaManager.getPolizaById(new Long(id));
				listPolizas.add(poliza);
			}
		}
		
		return listPolizas;
	}
			
	/* COMENTO EL METODO PORQUE NO SE USA 19-Julio-2013
	 * Indica si dentro de un listado de polizas, el numero de polizas con
	 * estado enviada pendiente, o enviada correcta, es menor que el campo
	 * maxpolizasppal ,en cuyo caso se modifica una variable que indica si se
	 * puede dar el alta o no.
	 * 
	 * @param listaPolizas
	 * /
	private boolean isPermiteAltaMaxPolizasPpal(Poliza poliza, List<Poliza> listaPolizas,
			BigDecimal maxpolizasppal) {
		BigDecimal maxpol = maxpolizasppal;
		Integer numpol = 0;
		Iterator<Poliza> itListaPolizas = listaPolizas.iterator();
		while (itListaPolizas.hasNext()) {
			Poliza pol = (Poliza) itListaPolizas.next();
			if(poliza.getLinea().equals(pol.getLinea()) && poliza.getClase().equals(pol.getClase())){
				if(Constants.ESTADO_POLIZA_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
						Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR.equals(pol.getEstadoPoliza().getIdestado()) || 								   
						Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.equals(pol.getEstadoPoliza().getIdestado()) || 
						Constants.ESTADO_POLIZA_ENVIADA_ERRONEA.equals(pol.getEstadoPoliza().getIdestado()) ){
					numpol++;
				}	
			}
		}
		if (numpol < maxpol.intValue()) {
			return true;
		} else {
			return false;
		}
	}*/
	
	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automáticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacías
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
    

	/* SETTERS SPRING IOC
	 ------------------------------------------------------------------------------------ */
	public void setSeleccionPolizaManager(SeleccionPolizaManager seleccionPolizaManager) {
		this.seleccionPolizaManager = seleccionPolizaManager;
	}

	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setSiniestrosManager(SiniestrosManager siniestrosManager) {
		this.siniestrosManager = siniestrosManager;
	}
	public void setFechaContratacionManager(
			FechaContratacionManager fechaContratacionManager) {
		this.fechaContratacionManager = fechaContratacionManager;
	}

	public void setConsultaSbpManager(ConsultaSbpManager consultaSbpManager) {
		this.consultaSbpManager = consultaSbpManager;
	}

	public void setPagoEstadosPolizaManager(
			IPagoEstadosPolizaManager pagoEstadosPolizaManager) {
		this.pagoEstadosPolizaManager = pagoEstadosPolizaManager;
	}

	public AseguradoManager getAseguradoManager() {
		return aseguradoManager;
	}

	public void setAseguradoManager(AseguradoManager aseguradoManager) {
		this.aseguradoManager = aseguradoManager;
	}
	
	public HistoricoManager getHistoricoManager() {
		return historicoManager;
	}

	public void setHistoricoManager(HistoricoManager historicoManager) {
		this.historicoManager = historicoManager;
	}

	public void setPagoPolizaManager(PagoPolizaManager pagoPolizaManager) {
		this.pagoPolizaManager = pagoPolizaManager;
	}
	
	public void setPolizaRCManager(PolizaRCManager polizaRCManager) {
		this.polizaRCManager = polizaRCManager;
	}
	
	public void setDocumentacionGedManager(DocumentacionGedManager documentacionGedManager) {
		this.documentacionGedManager = documentacionGedManager;
	}
	
	
}
