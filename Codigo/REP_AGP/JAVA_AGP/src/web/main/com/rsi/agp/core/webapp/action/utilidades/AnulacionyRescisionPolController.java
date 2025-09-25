package com.rsi.agp.core.webapp.action.utilidades;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.PolizaManager;
import com.rsi.agp.core.managers.impl.utilidades.AnulacionyRescisionPolManager;
import com.rsi.agp.core.managers.impl.utilidades.AportarDocIncidenciaManager;
import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.models.inc.IAportarDocIncidenciaDao;
import com.rsi.agp.dao.models.poliza.PolizaDao;
import com.rsi.agp.dao.tables.anexo.AnexoModificacion;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.Incidencias;
import com.rsi.agp.dao.tables.inc.Motivos;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.poliza.Poliza;

import es.agroseguro.serviciosweb.contratacionscimpresionmodificacion.AgrException;

public class AnulacionyRescisionPolController extends MultiActionController{

	private AnulacionyRescisionPolManager anulacionyRescisionPolManager;
	
	private AportarDocIncidenciaManager aportarDocIncidenciaManager;
	private IAportarDocIncidenciaDao aportarDocIncidenciaDao;
	private PolizaManager polizaManager;
	private PolizaDao polizaDao;
	
	private static final BigDecimal COD_ESTADO_CORRECTO = new BigDecimal("1");
	private static final BigDecimal COD_ESTADO_ERRONEO = new BigDecimal ("0");
	private static final String ESTADO_CORRECTO = "C";
	private static final String ESTADO_ERRONEO = "E";
	
	private static final String USUARIO = "usuario";
	private static final String ALERTA = "alerta";
	private static final String MENSAJE = "mensaje";
	private static final String COD_USUARIO = "codUsuario";
	private static final String ID_POL_INI_AY_R = "idPolIniAyR";
	private static final String ORIGEN_LLAMADA = "origenLlamada";
	private static final String VENTANA_VOLVER = "ventanaVolver";
	private static final String LINEA = "linea";
	private static final String MOTIVO_AY_R = "motivoAyR";
	private static final String TIPO_ANU_RESC = "tipoAnuResc";
	private static final String ID_INC_AY_R = "idIncAyR";
	private static final String MOTIVOS = "motivos";
	private static final String TIPOREF_SEL = "tiporefSel";
	private static final String ID_POLIZA_ANULY_RESC = "idPolizaAnulyResc";
	private static final String CODLINEA_ANULY_RESC = "codlineaAnulyResc";
	private static final String ID_POLIZA = "idPoliza";
	private static final String POLIZA = "poliza";
	private static final String DOCONSULTA_ANUL_RESC = "doconsultaAnulResc";
	private static final String METHOD = "method";
	private static final String ID_INC_VUELTA = "idIncVuelta";
	private static final String ENT_INC_VUELTA = "entIncVuelta";
	private static final String REF_INC_VUELTA = "refIncVuelta";
	private static final String OFI_INC_VUELTA = "ofiIncVuelta";
	private static final String ENT_MED_INC_VUELTA = "entMedIncVuelta";
	private static final String S_ENT_MED_INC_VUELTA = "sEntMedIncVuelta";
	private static final String DELEG_INC_VUELTA = "delegIncVuelta";
	private static final String LINEA_INC_VUELTA = "lineaIncVuelta";
	private static final String PLAN_INC_VUELTA = "planIncVuelta";
	private static final String CODEST_INC_VUELTA = "codestIncVuelta";
	private static final String COD_AGRO_INCO_VUELTA = "codAgroIncVuelta";
	private static final String NIF_INC_VUELTA = "nifIncVuelta";
	private static final String TIPO_REF_INC_VUELTA = "tipoRefIncVuelta";	
	private static final String ID_CUP_INC_VUELTA = "idCupIncVuelta";	
	private static final String ASUNT_INC_VUELTA = "asuntIncVuelta";
	private static final String FECHA_DES_INC_VUELTA = "fechaDesIncVuelta";
	private static final String FECHA_HAS_INC_VUELTA = "fechaHasIncVuelta";
	private static final String NUM_INC_VUELTA = "numIncVuelta";
	private static final String USU_INC_VUELTA = "usuIncVuelta";
	private static final String TIPO_INC_VUELTA = "tipoIncVuelta";
	
	private ResourceBundle bundle = ResourceBundle.getBundle("agp");
	@SuppressWarnings("unused")
	private String aportarDocIncidenciaVista;
	
	private static final Log LOGGER = LogFactory.getLog(AnulacionyRescisionPolController.class);
	
	
	public ModelAndView doconsultaAnulResc(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("Dentro de anulacionyRescisionPolController - doConsultaAnulResc");
		
		ModelAndView mv = null;
		Map<String,Object> parameters = new HashMap<String,Object> ();
		AnexoModificacion anexoModificacion = new AnexoModificacion();
		Poliza poliza = null;
		Poliza polIniAyR = null;
		Motivos motivos = new Motivos();
		
		Linea linea = new Linea();
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		
		String alerta = request.getParameter(ALERTA);
		String mensaje = request.getParameter(MENSAJE);
		
		parameters.put("perfil", usuario.getTipousuario());
		parameters.put(COD_USUARIO, usuario.getCodusuario());
		parameters.put(ID_POL_INI_AY_R, request.getParameter(ID_POL_INI_AY_R));
		
		if (alerta != null) {
			parameters.put(ALERTA, alerta);
		}
		if (mensaje != null) {
			parameters.put(MENSAJE, mensaje);
		}
			
		logger.debug("doConsultaAnulResc, valor de perfil:"+usuario.getTipousuario());

		String ventanaOrigen = request.getParameter(ORIGEN_LLAMADA);
		logger.debug("Valor de ventanaOrigen: "+ventanaOrigen);
		
		parameters.put(ORIGEN_LLAMADA, ventanaOrigen);
		parameters.put("ventanaIniAyR", ventanaOrigen);
		parameters.put(VENTANA_VOLVER, ventanaOrigen);
		parameters.put(LINEA, request.getParameter(LINEA));
		parameters.put(MOTIVO_AY_R, request.getParameter(MOTIVO_AY_R));
		parameters.put(TIPO_ANU_RESC, request.getParameter(TIPO_ANU_RESC));
		parameters.put(ID_POL_INI_AY_R, request.getParameter(ID_POL_INI_AY_R));
		parameters.put(ID_INC_AY_R, request.getParameter(ID_INC_AY_R));
		
		parameters.put(MOTIVOS, request.getParameter(MOTIVOS));
		
		String tipoPolizaReferencia = request.getParameter(TIPOREF_SEL);
		
		if (tipoPolizaReferencia != null) {
			Character tiporef = tipoPolizaReferencia.equals("P") ? 'P' : 'C';
			parameters.put("tipoRef", tiporef);
			parameters.put(TIPOREF_SEL, tiporef);
		}
			
		// MODIF TAM (20.07.2018) ** Inicio //
		String codmotivo = request.getParameter(MOTIVO_AY_R);
		if (codmotivo != null) {
			motivos = (Motivos) this.aportarDocIncidenciaDao.getObject(Motivos.class, codmotivo);
			parameters.put(MOTIVOS, motivos);
		}
			
		try {
			
			// MODIF TAM (20.07.2018) ** Inicio //
			String codmotivoStr = request.getParameter(MOTIVO_AY_R);
			
			if (codmotivoStr != null) { 
				Integer codmotivoAux = Integer.parseInt(request.getParameter(MOTIVO_AY_R));
			
				if (codmotivoAux != 0){
					Motivos motivoVista = new Motivos();
					motivoVista.setcodmotivo(codmotivoAux);
					Motivos motivosAux = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
					parameters.put(MOTIVOS, motivosAux);
				}
			}
			
			parameters.put("listaMotivos", this.aportarDocIncidenciaManager.obtenerMotivos());
			
			if (request.getParameter(ID_POLIZA_ANULY_RESC) != null) {
				                      
				Long idPoliza = new Long (request.getParameter(ID_POLIZA_ANULY_RESC));
			
				logger.debug("id poliza: " + idPoliza);
				poliza = polizaManager.getPoliza(new Long(idPoliza));
			}
			
			
			String lin = "";
			String plan = "";
			
			if (poliza == null) {
				logger.debug("No tenemos idPoliza, por lo que retornamos el error y volvemos a la ventana");
				
				String codLinea = StringUtils.nullToString(request.getParameter(CODLINEA_ANULY_RESC));
				logger.debug("doConsultaAnulResc, valor de codLineas:"+codLinea);
				
				lin = request.getParameter(CODLINEA_ANULY_RESC);
				plan = request.getParameter("codplanAnulyResc");
				
				if (lin !=null) 	
					linea.setCodlinea(new BigDecimal(lin));				
				if (plan !=null) 
					linea.setCodplan(new BigDecimal(plan));		
				
				parameters.put(LINEA, linea);
				
				parameters.put(ALERTA, request.getParameter(ALERTA));
				parameters.put("codlinea", request.getParameter(CODLINEA_ANULY_RESC));
				parameters.put("nombLinea", request.getParameter("nomblineaAnulyResc") );
				parameters.put("codPlan", request.getParameter("codplanAnulyResc"));
				parameters.put(ID_POLIZA, request.getParameter(ID_POLIZA_ANULY_RESC));
				parameters.put("referencia", request.getParameter("referenciaAnulyResc"));
				parameters.put("idPolizaIni", request.getParameter(ID_POL_INI_AY_R));
			
				parameters.put("nifcif", request.getParameter("nifAsegAnulyResc"));
				parameters.put("ventanaOrigen", request.getParameter("ventanaOrigen"));
				parameters.put(ID_POL_INI_AY_R, request.getParameter(ID_POL_INI_AY_R));
				
				// Devolvemos siempre en el objeto poliza los datos de la poliza inicial
				if (!StringUtils.isNullOrEmpty(request.getParameter(ID_POL_INI_AY_R))){
					Long idPolIniAyR = new Long (request.getParameter(ID_POL_INI_AY_R));
					polIniAyR = polizaManager.getPoliza(new Long(idPolIniAyR));
					parameters.put(POLIZA, polIniAyR);
				
					anexoModificacion.setPoliza(polIniAyR); //Para Mejora Acuse Recibo
				}
				
			}else {
				
				parameters.put("codlinea", poliza.getLinea().getCodlinea());
				parameters.put("nombLinea", poliza.getLinea().getNomlinea() );
				parameters.put("codPlan", poliza.getLinea().getCodplan());
				
				parameters.put(ID_POL_INI_AY_R, request.getParameter(ID_POL_INI_AY_R));
				
				logger.debug("doConsultaAnulResc, valor de codLineas:"+poliza.getLinea().getCodlinea());
			
				parameters.put(ID_POLIZA, poliza.getIdpoliza());
				parameters.put("referencia", poliza.getReferencia());
			
				parameters.put("nifcif", poliza.getAsegurado().getNifcif());
				parameters.put("tipoRef", poliza.getTipoReferencia());
				parameters.put(TIPOREF_SEL, poliza.getTipoReferencia());
				
				lin = poliza.getLinea().getCodlinea().toString();
				plan = poliza.getLinea().getCodplan().toString();
				
				if (lin !=null) 	
					linea.setCodlinea(new BigDecimal(lin));				
				if (plan !=null) 
					linea.setCodplan(new BigDecimal(plan));		
				
				parameters.put(LINEA, linea);
				
				// Devolvemos siempre en el objeto poliza los datos de la poliza inicial
				Long idPolIniAyR = new Long (request.getParameter(ID_POL_INI_AY_R));
				polIniAyR = polizaManager.getPoliza(new Long(idPolIniAyR));
				parameters.put(POLIZA, polIniAyR);

				anexoModificacion.setPoliza(polIniAyR); //Para Mejora Acuse Recibo 

				if (ventanaOrigen.equals("UtilidadesPol")) {
					logger.debug("Accedemos a Anulación y Rescisión desde Utilidades Pólizas");
					
					parameters.put(ID_POLIZA, request.getParameter(ID_POLIZA_ANULY_RESC));
				}else {
					logger.debug("Accedemos a Anulación y Rescisión desde Relación De Modificación de Incidencias");
					
				}		
			}

		}catch(Exception e) {
			LOGGER.debug("Ha ocurrido un error generico - verAnulacionyRescision",e);
			parameters.put(ALERTA, bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
		}
		
		VistaIncidenciasAgro vistaVuelta = CargarParametrosVueltaenVista(request);
		
		parameters.put("vuelta", vistaVuelta);
		
		mv =  new ModelAndView("/moduloUtilidades/incidenciasAgro/anulacionyRescision", "anexoModificacion", anexoModificacion).addAllObjects(parameters);
		return mv;
	}

	public ModelAndView doEnviarAnulyResc(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		LOGGER.debug("AnulacionyRescisionPolController - Dentro de doEnviarAnulyResc");
		
		ModelAndView mv = null;	
		
		String realPath = this.getServletContext().getRealPath("/WEB-INF/");
		Map<String,Object> parametros = new HashMap<String,Object> ();
		Incidencias AnulyResc = new Incidencias();
		
		final Usuario usuario = (Usuario) req.getSession().getAttribute(USUARIO);
		String codUsuario = usuario.getCodusuario();
		
		Character tipoInc = req.getParameter(TIPO_ANU_RESC).charAt(0);
		String ventanaOrigen = req.getParameter(ORIGEN_LLAMADA);
		String tipoPolizaReferencia = req.getParameter(TIPOREF_SEL);
		
		String idPolIniAyR = req.getParameter(ID_POL_INI_AY_R);
		
		/* Validamos si el idPoliza viene informado para saber si tenemos que obtener el 
		 * id por referencia, plan y línea insertados.
		 */
		String idPolizaStr = req.getParameter("idPolizaAyR");
		
		if (idPolizaStr.equals("0")){
			idPolizaStr = "";
		}
		
		String idIncidencia = req.getParameter(ID_INC_AY_R);
		
		if (!StringUtils.isNullOrEmpty(idIncidencia)){
			AnulyResc.setIdincidencia(Long.parseLong(idIncidencia));
		}
			
		
		Long idPoliza = new Long(0);
						
		try {
			
			if (StringUtils.isNullOrEmpty(idPolizaStr)){
				String refPoliza =  req.getParameter("referenciaAyR");
				Character tipoRefPoliza = tipoPolizaReferencia.charAt(0);
				BigDecimal plan = new BigDecimal (req.getParameter("codPlanAyR")); 
				BigDecimal linea = new BigDecimal (req.getParameter("codlineaAyR"));
				Poliza poliza = this.anulacionyRescisionPolManager.obtenerPolizaByRefPlanLin(refPoliza, tipoRefPoliza, plan, linea);
				
				/* Si no se recupera ninguna póliza para esa ferencia, tipoRef, plan y línea Se continua con la ejecución del envío */
				if (poliza == null) {
					
					LOGGER.error("La poliza no existe pero lanzamos el envío");
					
					Motivos motivo = AnulacionyRescisionPolManager.obtenerMotivosVista(req);
					
					AnulyResc.setCodestado(Constants.ESTADO_INC_LIMBO);
					
					String referencia =  refPoliza;
					AnulyResc.setReferencia(referencia);
					AnulyResc.setCodlinea(linea);
					AnulyResc.setCodplan(plan);
					AnulyResc.setTipoinc(tipoInc);
					
					AnulyResc.setTiporef(tipoRefPoliza);
					
					AnulyResc.setFechaestadoagro(new Date());
					AnulyResc.setFechaestado(new Date());
					AnulyResc.setfecanulresc(new Date());
					AnulyResc.setCodusuario(req.getParameter("usuarioSession"));
					AnulyResc.setNifaseg(req.getParameter("nifCifAyR"));
					
		 			parametros = this.anulacionyRescisionPolManager.guardarAnulacionyRescision(AnulyResc, motivo);
		 			LOGGER.debug("anulacionyRescisionPolController - despues de guardarIncidencia");
					
					Long incidenciaId = (Long)parametros.get("incidenciaId");
					
					AnulyResc.setIdincidencia(incidenciaId);
					
					Integer codmotivo = motivo.getCodmotivo();
					if (codmotivo != 0){
						parametros.put(MOTIVO_AY_R, codmotivo);
						Motivos motivoVista = new Motivos();
						motivoVista.setcodmotivo(codmotivo);
						Motivos motivos = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
						parametros.put(MOTIVOS, motivos);
					}
					
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(ID_POL_INI_AY_R, idPolIniAyR);
					parametros.put(ID_INC_AY_R, incidenciaId);
					
					parametros.put(MOTIVO_AY_R, req.getParameter(MOTIVO_AY_R));
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(CODLINEA_ANULY_RESC, linea);
					parametros.put("codplanAnulyResc", plan);
					parametros.put("referenciaAnulyResc", refPoliza);
					parametros.put(TIPOREF_SEL, tipoRefPoliza);
					parametros.put("nifAsegAnulyResc", req.getParameter("nifCifAyR"));
					parametros.putAll(this.anulacionyRescisionPolManager.cargarNombreLinea(linea));
								
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(METHOD, DOCONSULTA_ANUL_RESC);
					parametros.put(ORIGEN_LLAMADA, ventanaOrigen);
					parametros.put(VENTANA_VOLVER, ventanaOrigen);
					parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
					parametros.put(COD_USUARIO, req.getSession().getAttribute(USUARIO));
					
					parametros = this.anulacionyRescisionPolManager.enviarAnulyRescAgroseguro(realPath, AnulyResc, tipoInc, codUsuario);
					
					Character rescision ='R';
					
					String codEstado = (String) parametros.get("codestadoInc");
					
					if (parametros.get(ALERTA) == null) {
						if (tipoInc == rescision) {
							if (codEstado.endsWith("E")){
								parametros.put(MENSAJE, "Rescisión en Revisión Administración");
							}else{
								parametros.put(MENSAJE, "Rescisión enviada correctamente");
							}		
						}else {
							if (codEstado.endsWith("E")){
								parametros.put(MENSAJE, "Anulación en Revisión Administración");
							}else{
								parametros.put(MENSAJE, "Anulación enviada correctamente");
							}	
							
						}
					}
					
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(ID_POL_INI_AY_R, idPolIniAyR);
					parametros.put(ID_INC_AY_R, incidenciaId);
					
					parametros.put(MOTIVO_AY_R, req.getParameter(MOTIVO_AY_R));
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(CODLINEA_ANULY_RESC, linea);
					parametros.put("codplanAnulyResc", plan);
					parametros.put("referenciaAnulyResc", refPoliza);
					parametros.put(TIPOREF_SEL, tipoRefPoliza);
					parametros.put("nifAsegAnulyResc", req.getParameter("nifCifAyR"));
					parametros.putAll(this.anulacionyRescisionPolManager.cargarNombreLinea(linea));
								
					parametros.put(TIPO_ANU_RESC, tipoInc);
					parametros.put(METHOD, DOCONSULTA_ANUL_RESC);
					parametros.put(ORIGEN_LLAMADA, ventanaOrigen);
					parametros.put(VENTANA_VOLVER, ventanaOrigen);
					parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
					parametros.put(COD_USUARIO, req.getSession().getAttribute(USUARIO));

					/* Anhadimos los parametros para la vuelta */
					Integer codmotivo2 = motivo.getCodmotivo();
					if (codmotivo2 != 0){
						parametros.put(MOTIVO_AY_R, codmotivo2);
						Motivos motivoVista = new Motivos();
						motivoVista.setcodmotivo(codmotivo2);
						Motivos motivos = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
						parametros.put(MOTIVOS, motivos);
					}

					
					/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
					parametros.put(ID_INC_VUELTA, req.getParameter("idincConsVuelta"));
					parametros.put(ENT_INC_VUELTA, req.getParameter("entidadConsVuelta"));
					parametros.put(REF_INC_VUELTA, req.getParameter("refConsVuelta"));
					parametros.put(OFI_INC_VUELTA, req.getParameter("oficinaConsVuelta"));
					parametros.put(ENT_MED_INC_VUELTA, req.getParameter("entmediadoraConsVuelta"));
					parametros.put(S_ENT_MED_INC_VUELTA, req.getParameter("subentmediadoraConsVuelta"));
					parametros.put(DELEG_INC_VUELTA, req.getParameter("delegacionConsVuelta"));
					parametros.put(LINEA_INC_VUELTA, req.getParameter("codlineaConsVuelta"));
					parametros.put(PLAN_INC_VUELTA, req.getParameter("codplanConsVuelta"));
					parametros.put(CODEST_INC_VUELTA, req.getParameter("codestadoConsVuelta"));
					parametros.put(COD_AGRO_INCO_VUELTA, req.getParameter("codestadoagroConsVuelta"));
					parametros.put(NIF_INC_VUELTA, req.getParameter("nifcifConsVuelta"));
					parametros.put(TIPO_REF_INC_VUELTA, req.getParameter("tiporefConsVuelta"));
					parametros.put(ID_CUP_INC_VUELTA, req.getParameter("idcuponConsVuelta"));			
					parametros.put(ASUNT_INC_VUELTA, req.getParameter("asuntoConsVuelta"));
					parametros.put(FECHA_DES_INC_VUELTA, req.getParameter("fechaEnvioDesdeIdConsVuelta"));
					parametros.put(FECHA_HAS_INC_VUELTA, req.getParameter("fechaEnvioHastaIdConsVuelta"));
					parametros.put(NUM_INC_VUELTA, req.getParameter("numIncidenciaConsVuelta"));
					parametros.put(USU_INC_VUELTA, req.getParameter("codusuarioConsVuelta"));
					parametros.put(TIPO_INC_VUELTA, req.getParameter("tipoincConsVuelta"));
					/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
					
					LOGGER.debug("Volvemos a la pantalla de Anulación y Rescisión");
					
					if (idPolIniAyR != null && idPolIniAyR != "") {
						logger.debug("Obtenemos el objeto poliza Inicial de idPolizaInicial: "+idPolIniAyR);
						Poliza PolIniAyR = polizaManager.getPoliza(new Long(idPolIniAyR));
						parametros.put(POLIZA, PolIniAyR);
					}
					
					// MODIF TAM (19.11.2019)
					LOGGER.debug("Redirigiendo a doconsultaAnulyResc de la pantalla de Anulación y Rescisión (1)");
					mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, DOCONSULTA_ANUL_RESC).addAllObjects(parametros);
					return mv;
					
				}else {
					idPoliza = poliza.getIdpoliza();
					
					parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
				}
				
				 
			}else {
				if (!StringUtils.isNullOrEmpty(req.getParameter("idPolizaAyR"))) {
					logger.debug ("Valor de idPolizaAyR:" +req.getParameter("idPolizaAyR"));
					idPoliza = new Long (req.getParameter("idPolizaAyR"));
					parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
				}
			}

			LOGGER.debug("AnulacionyRescisionPolController - dentro de doEnviarAnulResc");
			LOGGER.debug("Valor de ventanaOrigen: "+ventanaOrigen);
			
			Motivos motivo = AnulacionyRescisionPolManager.obtenerMotivosVista(req);
			
			Poliza poliza = polizaManager.getPoliza(new Long(idPoliza));
			
			AnulyResc.setCodestado(Constants.ESTADO_INC_LIMBO);
			
			String referencia =  poliza.getReferencia();
			AnulyResc.setReferencia(referencia);
			AnulyResc.setCodlinea(poliza.getLinea().getCodlinea());
			AnulyResc.setCodplan(poliza.getLinea().getCodplan());
			AnulyResc.setTipoinc(tipoInc);
			
			AnulyResc.setTiporef(poliza.getTipoReferencia());
			
			AnulyResc.setFechaestadoagro(new Date());
			AnulyResc.setFechaestado(new Date());
			AnulyResc.setfecanulresc(new Date());
			AnulyResc.setCodusuario(req.getParameter("usuarioSession"));
			AnulyResc.setNifaseg(poliza.getAsegurado().getNifcif());
			
 			parametros = this.anulacionyRescisionPolManager.guardarAnulacionyRescision(AnulyResc, motivo);
 			LOGGER.debug("anulacionyRescisionPolController - despues de guardarIncidencia");
			
			Long incidenciaId = (Long)parametros.get("incidenciaId");
			
			AnulyResc.setIdincidencia(incidenciaId);
			
			Integer codmotivo = motivo.getCodmotivo();
			if (codmotivo != 0){
				parametros.put(MOTIVO_AY_R, codmotivo);
				Motivos motivoVista = new Motivos();
				motivoVista.setcodmotivo(codmotivo);
				Motivos motivos = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
				parametros.put(MOTIVOS, motivos);
			}
			parametros.put(TIPO_ANU_RESC, tipoInc);
			parametros = this.anulacionyRescisionPolManager.enviarAnulyRescAgroseguro(realPath, AnulyResc, tipoInc, codUsuario);
			
			Character rescision ='R';
			
			String codEstado = (String) parametros.get("codestadoInc");
			
			if (parametros.get(ALERTA) == null) {
				if (tipoInc == rescision) {
					if (codEstado.endsWith("E")){
						parametros.put(MENSAJE, "Rescisión en Revisión Administración");
					}else{
						parametros.put(MENSAJE, "Rescisión enviada correctamente");
					}		
				}else {
					if (codEstado.endsWith("E")){
						parametros.put(MENSAJE, "Anulación en Revisión Administración");
					}else{
						parametros.put(MENSAJE, "Anulación enviada correctamente");
					}	
					
				}
			}
			
			parametros.put(ID_INC_AY_R, incidenciaId);
			
			String codEstadoInc ="";
			
			if (codEstado.endsWith("A") || codEstado.endsWith("E")){
				codEstadoInc =ESTADO_CORRECTO; 
			}else {
				codEstadoInc =ESTADO_ERRONEO;
			}

			
			this.actualizarAnulyRescBD(incidenciaId, AnulyResc, codEstadoInc);

			/* Añadimos los parametros para la vuelta */
			Integer codmotivo2 = motivo.getCodmotivo();
			if (codmotivo2 != 0){
				parametros.put(MOTIVO_AY_R, codmotivo2);
				Motivos motivoVista = new Motivos();
				motivoVista.setcodmotivo(codmotivo2);
				Motivos motivos = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
				parametros.put(MOTIVOS, motivos);
			}
			
			parametros.put(ID_POL_INI_AY_R, idPolIniAyR);
			parametros.put(TIPO_ANU_RESC, tipoInc);
			parametros.put(METHOD, DOCONSULTA_ANUL_RESC);
			parametros.put(ORIGEN_LLAMADA, ventanaOrigen);
			parametros.put(VENTANA_VOLVER, ventanaOrigen);
			parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
			parametros.put(COD_USUARIO, req.getSession().getAttribute(USUARIO));
			
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
			parametros.put(ID_INC_VUELTA, req.getParameter("idincConsVuelta"));
			parametros.put(ENT_INC_VUELTA, req.getParameter("entidadConsVuelta"));
			parametros.put(REF_INC_VUELTA, req.getParameter("refConsVuelta"));
			parametros.put(OFI_INC_VUELTA, req.getParameter("oficinaConsVuelta"));
			parametros.put(ENT_MED_INC_VUELTA, req.getParameter("entmediadoraConsVuelta"));
			parametros.put(S_ENT_MED_INC_VUELTA, req.getParameter("subentmediadoraConsVuelta"));
			parametros.put(DELEG_INC_VUELTA, req.getParameter("delegacionConsVuelta"));
			parametros.put(LINEA_INC_VUELTA, req.getParameter("codlineaConsVuelta"));
			parametros.put(PLAN_INC_VUELTA, req.getParameter("codplanConsVuelta"));
			parametros.put(CODEST_INC_VUELTA, req.getParameter("codestadoConsVuelta"));
			parametros.put(COD_AGRO_INCO_VUELTA, req.getParameter("codestadoagroConsVuelta"));
			parametros.put(NIF_INC_VUELTA, req.getParameter("nifcifConsVuelta"));
			parametros.put(TIPO_REF_INC_VUELTA, req.getParameter("tiporefConsVuelta"));
			parametros.put(ID_CUP_INC_VUELTA, req.getParameter("idcuponConsVuelta"));			
			parametros.put(ASUNT_INC_VUELTA, req.getParameter("asuntoConsVuelta"));
			parametros.put(FECHA_DES_INC_VUELTA, req.getParameter("fechaEnvioDesdeIdConsVuelta"));
			parametros.put(FECHA_HAS_INC_VUELTA, req.getParameter("fechaEnvioHastaIdConsVuelta"));
			parametros.put(NUM_INC_VUELTA, req.getParameter("numIncidenciaConsVuelta"));
			parametros.put(USU_INC_VUELTA, req.getParameter("codusuarioConsVuelta"));
			parametros.put(TIPO_INC_VUELTA, req.getParameter("tipoincConsVuelta"));
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/

			//parametros.put(ID_POLIZA_ANULY_RESC, idPoliza);
			LOGGER.debug("Redirigiendo a doconsultaAnulyResc de la pantalla de Anulación y Rescisión (2)");
			// MODIF TAM (19.11.2019)
			mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, DOCONSULTA_ANUL_RESC).addAllObjects(parametros);
			
		} catch(BusinessException e) {
			LOGGER.error("Avisamos al usuario del error, BussinessException: ",e);
			
			Long incidenciaId = (Long)parametros.get("incidenciaId");
			
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
			parametros.put(ID_INC_VUELTA, req.getParameter("idincConsVuelta"));
			parametros.put(ENT_INC_VUELTA, req.getParameter("entidadConsVuelta"));
			parametros.put(REF_INC_VUELTA, req.getParameter("refConsVuelta"));
			parametros.put(OFI_INC_VUELTA, req.getParameter("oficinaConsVuelta"));
			parametros.put(ENT_MED_INC_VUELTA, req.getParameter("entmediadoraConsVuelta"));
			parametros.put(S_ENT_MED_INC_VUELTA, req.getParameter("subentmediadoraConsVuelta"));
			parametros.put(DELEG_INC_VUELTA, req.getParameter("delegacionConsVuelta"));
			parametros.put(LINEA_INC_VUELTA, req.getParameter("codlineaConsVuelta"));
			parametros.put(PLAN_INC_VUELTA, req.getParameter("codplanConsVuelta"));
			parametros.put(CODEST_INC_VUELTA, req.getParameter("codestadoConsVuelta"));
			parametros.put(COD_AGRO_INCO_VUELTA, req.getParameter("codestadoagroConsVuelta"));
			parametros.put(NIF_INC_VUELTA, req.getParameter("nifcifConsVuelta"));
			parametros.put(TIPO_REF_INC_VUELTA, req.getParameter("tiporefConsVuelta"));
			parametros.put(ID_CUP_INC_VUELTA, req.getParameter("idcuponConsVuelta"));			
			parametros.put(ASUNT_INC_VUELTA, req.getParameter("asuntoConsVuelta"));
			parametros.put(FECHA_DES_INC_VUELTA, req.getParameter("fechaEnvioDesdeIdConsVuelta"));
			parametros.put(FECHA_HAS_INC_VUELTA, req.getParameter("fechaEnvioHastaIdConsVuelta"));
			parametros.put(NUM_INC_VUELTA, req.getParameter("numIncidenciaConsVuelta"));
			parametros.put(USU_INC_VUELTA, req.getParameter("codusuarioConsVuelta"));
			parametros.put(TIPO_INC_VUELTA, req.getParameter("tipoincConsVuelta"));
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
			
			this.actualizarAnulyRescBD(incidenciaId, AnulyResc, ESTADO_ERRONEO);
			
			parametros.put(ALERTA, bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
			
			LOGGER.debug("Redirigiendo a doconsultaAnulyResc de la pantalla de Anulación y Rescisión (3)");
			
			parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
			mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, DOCONSULTA_ANUL_RESC).addAllObjects(parametros);

			
		} catch (Exception exception) {
			
			LOGGER.error("Traza de error en doEnviarAnulyResc ... Exception: ",exception);
			
			String error = AnulacionyRescisionPolManager.procesarAgrException((AgrException) exception);
			parametros.put(ALERTA, error);
			
			parametros.put(ID_POL_INI_AY_R, idPolIniAyR);
			parametros.put(METHOD, DOCONSULTA_ANUL_RESC);
			parametros.put(ORIGEN_LLAMADA, ventanaOrigen);
			parametros.put(VENTANA_VOLVER, ventanaOrigen);
			parametros.put(ID_POLIZA_ANULY_RESC, Long.toString(idPoliza));
			
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/
			parametros.put(ID_INC_VUELTA, req.getParameter("idincConsVuelta"));
			parametros.put(ENT_INC_VUELTA, req.getParameter("entidadConsVuelta"));
			parametros.put(REF_INC_VUELTA, req.getParameter("refConsVuelta"));
			parametros.put(OFI_INC_VUELTA, req.getParameter("oficinaConsVuelta"));
			parametros.put(ENT_MED_INC_VUELTA, req.getParameter("entmediadoraConsVuelta"));
			parametros.put(S_ENT_MED_INC_VUELTA, req.getParameter("subentmediadoraConsVuelta"));
			parametros.put(DELEG_INC_VUELTA, req.getParameter("delegacionConsVuelta"));
			parametros.put(LINEA_INC_VUELTA, req.getParameter("codlineaConsVuelta"));
			parametros.put(PLAN_INC_VUELTA, req.getParameter("codplanConsVuelta"));
			parametros.put(CODEST_INC_VUELTA, req.getParameter("codestadoConsVuelta"));
			parametros.put(COD_AGRO_INCO_VUELTA, req.getParameter("codestadoagroConsVuelta"));
			parametros.put(NIF_INC_VUELTA, req.getParameter("nifcifConsVuelta"));
			parametros.put(TIPO_REF_INC_VUELTA, req.getParameter("tiporefConsVuelta"));
			parametros.put(ID_CUP_INC_VUELTA, req.getParameter("idcuponConsVuelta"));			
			parametros.put(ASUNT_INC_VUELTA, req.getParameter("asuntoConsVuelta"));
			parametros.put(FECHA_DES_INC_VUELTA, req.getParameter("fechaEnvioDesdeIdConsVuelta"));
			parametros.put(FECHA_HAS_INC_VUELTA, req.getParameter("fechaEnvioHastaIdConsVuelta"));
			parametros.put(NUM_INC_VUELTA, req.getParameter("numIncidenciaConsVuelta"));
			parametros.put(USU_INC_VUELTA, req.getParameter("codusuarioConsVuelta"));
			parametros.put(TIPO_INC_VUELTA, req.getParameter("tipoincConsVuelta"));
			/*** Recuperamos los valores de los campos de vuelta a la ventana de lista de Incidencias ***/

			parametros.put(COD_USUARIO, req.getSession().getAttribute(USUARIO));
			parametros.put(TIPO_ANU_RESC, tipoInc);
			
			// MODIF TAM (19.11.2019)
			//mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, DOCONSULTA_ANUL_RESC).addAllObjects(parametros);
			
			LOGGER.debug("Redirigiendo a doconsultaAnulyResc de la pantalla de Anulación y Rescisión (4)");
			
			mv= new ModelAndView("redirect:/anulacionyRescisionPol.run").addObject(METHOD, DOCONSULTA_ANUL_RESC).addAllObjects(parametros);

		}
		return mv;
	}
	
	private Incidencias actualizarAnulyRescBD(Long idIncidencia, Incidencias incidenciaAgroseguro, String estado) throws DAOException {
		
		BigDecimal anhoincidencia = BigDecimal.ZERO;
		
		if (estado.equals(ESTADO_CORRECTO)){
			Incidencias incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
			Character codEstado = incidenciaAgroseguro.getEstadosInc().getCodestado();
		
			EstadosInc estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
			incidenciaBD.setEstadosInc(estadoInc);
		
			
			estadoInc.getIncidenciases().add(incidenciaBD);
		
			incidenciaBD.setAnhoincidencia(incidenciaAgroseguro.getAnhoincidencia());
			incidenciaBD.setNumincidencia(incidenciaAgroseguro.getNumincidencia());
			
			if (incidenciaBD.getAnhoincidencia() == null){
				incidenciaBD.setAnhoincidencia(anhoincidencia);
			}
			if (incidenciaBD.getNumincidencia() == null){
				incidenciaBD.setNumincidencia(anhoincidencia);
			}
			
			incidenciaBD.setCodestado(COD_ESTADO_CORRECTO);
		
			incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
			return incidenciaBD;
		}else {
			
			Incidencias incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.getObject(Incidencias.class, idIncidencia);
			
			incidenciaBD.setCodestado(COD_ESTADO_ERRONEO);
			
			
			if (incidenciaBD.getAnhoincidencia() == null){
				incidenciaBD.setAnhoincidencia(anhoincidencia);
			}
			if (incidenciaBD.getNumincidencia() == null){
				incidenciaBD.setNumincidencia(anhoincidencia);
			}
				
			/* Actualizamos el estadoAgroseguro con el valor "Rechazada" */
			Character codEstado;
			EstadosInc estadoInc;
			
			if (incidenciaBD.getEstadosInc().getCodestado() == null) {
				codEstado = 'R';
				estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
			}else {
				codEstado = incidenciaBD.getEstadosInc().getCodestado();
				estadoInc = (EstadosInc)this.aportarDocIncidenciaDao.getObject(EstadosInc.class, codEstado);
			}
			
			incidenciaBD.setEstadosInc(estadoInc);			
			estadoInc.getIncidenciases().add(incidenciaBD);
			
			incidenciaBD = (Incidencias)this.aportarDocIncidenciaDao.saveOrUpdate(incidenciaBD);
			return incidenciaBD; 
			
		}
	}
	
	/* Esta función la utilizamos cuando venimos de Lista de Incidencias y únicamente se quiere 
	 * CONSULTAR la indicencia, todos los campos en modo consulta
	 */
public ModelAndView doConsultar(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("Dentro de anulacionyRescisionPolController - doConsulta(modoConsulta)");
		
		ModelAndView mv = null;
		Map<String,Object> parameters = new HashMap<String,Object> ();
		AnexoModificacion anexoModificacion = new AnexoModificacion();
		
		Linea linea = new Linea();
		
		final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
		
		Long idIncidencia = Long.parseLong(request.getParameter("idincidenciaConsulta"));
		
		VistaIncidenciasAgro vistaIncidencia = new VistaIncidenciasAgro();
		
		try {
			vistaIncidencia = this.aportarDocIncidenciaDao.getIncidenciasById(idIncidencia);
			
			
			logger.debug("doConsultar, valor de perfil:"+usuario.getTipousuario());
			
			parameters.put("listaMotivos", this.aportarDocIncidenciaManager.obtenerMotivos());
			
			parameters.put(ORIGEN_LLAMADA, "Consulta");
			parameters.put("ventanaIniAyR", "Consulta");
			parameters.put(VENTANA_VOLVER, "Consulta");
			parameters.put("perfil", usuario.getTipousuario());
			parameters.put(COD_USUARIO, usuario.getCodusuario());
			
			parameters.put(LINEA, vistaIncidencia.getCodlinea());
			parameters.put("tipoRef", vistaIncidencia.getTiporef());
			parameters.put(TIPOREF_SEL, vistaIncidencia.getTiporef());
			parameters.put(MOTIVO_AY_R, vistaIncidencia.getCodmotivo());
			
			VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
			this.parametrosVueltaConsultaIncidencia(request, vistaIncAgro);
			
			parameters.put("vuelta", vistaIncAgro);
			
			parameters.put("codlinea", vistaIncidencia.getCodlinea());
			String nomblinea = this.aportarDocIncidenciaDao.getNombLinea(vistaIncidencia.getCodlinea());
			parameters.put("nombLinea", nomblinea );
			parameters.put("codPlan", vistaIncidencia.getCodplan());
			parameters.put("nifcif", vistaIncidencia.getNifcif());
			parameters.put("referencia", vistaIncidencia.getReferencia());
			parameters.put(TIPO_ANU_RESC,vistaIncidencia.getTipoinc());
			
			String lin = (vistaIncidencia.getCodlinea()).toString();
			String plan =(vistaIncidencia.getCodplan()).toString();
			
			if (lin !=null) 	
				linea.setCodlinea(new BigDecimal(lin));				
			if (plan !=null) 
				linea.setCodplan(new BigDecimal(plan));		
			
			parameters.put(LINEA, linea);
			
			/* obtenemos el idpoliza */
			String referencia = "";
			Character tipoRef;
			BigDecimal codPlan = new BigDecimal(0); 
			BigDecimal codLinea = new BigDecimal(0);
			
			referencia = vistaIncidencia.getReferencia();
			tipoRef = vistaIncidencia.getTiporef();
			codPlan = vistaIncidencia.getCodplan();
			codLinea = vistaIncidencia.getCodlinea();		
			
			BigDecimal idPoliza = this.aportarDocIncidenciaDao.getIdPoliza(referencia, tipoRef, codPlan, codLinea);
			
			if (idPoliza != null && idPoliza.compareTo(BigDecimal.ZERO) > 0) {
				parameters.put(ID_POLIZA, idPoliza);
				parameters.put("idPolizaIni", idPoliza);
				parameters.put(ID_POL_INI_AY_R, idPoliza);
				
				Long idPolAux = idPoliza.longValue();
				Poliza polIniAyR = polizaManager.getPoliza(idPolAux);
				parameters.put(POLIZA, polIniAyR);
				
				anexoModificacion.setPoliza(polIniAyR); //Para Mejora Acuse Recibo 
			}
		
			String codmotivo = String.valueOf(vistaIncidencia.getCodmotivo());
			
			if (codmotivo != null) { 
				Integer codmotivoAux = vistaIncidencia.getCodmotivo();
			
				if (codmotivoAux != 0){
					Motivos motivoVista = new Motivos();
					motivoVista.setcodmotivo(codmotivoAux);
					Motivos motivosAux = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
					parameters.put(MOTIVOS, motivosAux);
				}
			}
			
		}catch(Exception e) {
			LOGGER.debug("Ha ocurrido un error generico - doConsultar(AnulacionyRescisionPolController)",e);
			parameters.put(ALERTA, bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
		}
		mv =  new ModelAndView("/moduloUtilidades/incidenciasAgro/anulacionyRescision", "anexoModificacion", anexoModificacion).addAllObjects(parameters);
		return mv;
	}

/* Esta función la utilizamos cuando venimos de Lista de Incidencias y únicamente se quiere 
 * CONSULTAR la indicencia, todos los campos en modo consulta
 */
public ModelAndView doEditarAnulyResc(HttpServletRequest request, HttpServletResponse response) {
	
	logger.debug("Dentro de anulacionyRescisionPolController - doEditarAnulyResc(Editar Anulación desde Incidencias)");
	
	ModelAndView mv = null;
	Map<String,Object> parameters = new HashMap<String,Object> ();
	AnexoModificacion anexoModificacion = new AnexoModificacion();
	
	Linea linea = new Linea();
	
	final Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);
	
	Long idIncidencia = Long.parseLong(	request.getParameter("idincidenciaConsulta"));
	
	VistaIncidenciasAgro vistaIncidencia = new VistaIncidenciasAgro();
	
	try {
		vistaIncidencia = this.aportarDocIncidenciaDao.getIncidenciasById(idIncidencia);
		
		logger.debug("doEditarAnulyResc, valor de perfil:"+usuario.getTipousuario());
		
		parameters.put("listaMotivos", this.aportarDocIncidenciaManager.obtenerMotivos());
		
		parameters.put(ORIGEN_LLAMADA, "EditarAnuyResc");
		parameters.put("ventanaIniAyR", "EditarAnuyResc");
		parameters.put(VENTANA_VOLVER, "EditarAnuyResc");
		parameters.put("perfil", usuario.getTipousuario());
		parameters.put(COD_USUARIO, usuario.getCodusuario());
		
		parameters.put(LINEA, vistaIncidencia.getCodlinea());
		parameters.put("tipoRef", vistaIncidencia.getTiporef());
		parameters.put(TIPOREF_SEL, vistaIncidencia.getTiporef());
		parameters.put(MOTIVO_AY_R, vistaIncidencia.getCodmotivo());
		
		VistaIncidenciasAgro vistaIncAgro = new VistaIncidenciasAgro();
		this.parametrosVueltaConsultaIncidencia(request, vistaIncAgro);
		
		parameters.put("vuelta", vistaIncAgro);
		
		parameters.put("codlinea", vistaIncidencia.getCodlinea());
		String nomblinea = this.aportarDocIncidenciaDao.getNombLinea(vistaIncidencia.getCodlinea());
		parameters.put("nombLinea", nomblinea );
		parameters.put("codPlan", vistaIncidencia.getCodplan());
		parameters.put("nifcif", vistaIncidencia.getNifcif());
		parameters.put("referencia", vistaIncidencia.getReferencia());
		parameters.put(TIPO_ANU_RESC,vistaIncidencia.getTipoinc());
		
		String lin = (vistaIncidencia.getCodlinea()).toString();
		String plan =(vistaIncidencia.getCodplan()).toString();
		
		if (lin !=null) 	
			linea.setCodlinea(new BigDecimal(lin));				
		if (plan !=null) 
			linea.setCodplan(new BigDecimal(plan));		
		
		parameters.put(LINEA, linea);
		
		/* obtenemos el idpoliza */
		String referencia = "";
		Character tipoRef;
		BigDecimal codPlan = new BigDecimal(0); 
		BigDecimal codLinea = new BigDecimal(0);
		
		referencia = vistaIncidencia.getReferencia();
		tipoRef = vistaIncidencia.getTiporef();
		codPlan = vistaIncidencia.getCodplan();
		codLinea = vistaIncidencia.getCodlinea();		
		
		BigDecimal idPoliza = this.aportarDocIncidenciaDao.getIdPoliza(referencia, tipoRef, codPlan, codLinea);
		logger.debug("Valor de idPoliza:"+idPoliza);
		
		if (idPoliza != null && idPoliza.compareTo(BigDecimal.ZERO) > 0) {
			parameters.put(ID_POLIZA, idPoliza);
			parameters.put("idPolizaIni", idPoliza);
			parameters.put(ID_POL_INI_AY_R, idPoliza);
			
			Long idPolAux = idPoliza.longValue();
			Poliza polIniAyR = polizaManager.getPoliza(idPolAux);
			parameters.put(POLIZA, polIniAyR);
			
			anexoModificacion.setPoliza(polIniAyR); //Para Mejora Acuse Recibo 		
		}
				
		String codmotivo = String.valueOf(vistaIncidencia.getCodmotivo());
		
		if (codmotivo != null) { 
			Integer codmotivoAux = vistaIncidencia.getCodmotivo();
		
			if (codmotivoAux != 0){
				Motivos motivoVista = new Motivos();
				motivoVista.setcodmotivo(codmotivoAux);
				Motivos motivosAux = this.anulacionyRescisionPolManager.obtenerMotivoBD(motivoVista);
				parameters.put(MOTIVOS, motivosAux);
			}
		}
		
	}catch(Exception e) {
		LOGGER.debug("Ha ocurrido un error generico - doConsultar(AnulacionyRescisionPolController)",e);
		parameters.put(ALERTA, bundle.getString("mensaje.swImpresion.llamadaWs.KO"));
	}
	
	mv =  new ModelAndView("/moduloUtilidades/incidenciasAgro/anulacionyRescision", "anexoModificacion", anexoModificacion).addAllObjects(parameters);
	return mv;
}	
	
private VistaIncidenciasAgro CargarParametrosVueltaenVista (final HttpServletRequest req){
	VistaIncidenciasAgro vista = new VistaIncidenciasAgro();
	
	String idIncidencia = req.getParameter(ID_INC_VUELTA);
	String codEntidad = req.getParameter(ENT_INC_VUELTA);
	String referencia =req.getParameter(REF_INC_VUELTA);
	String oficina = req.getParameter(OFI_INC_VUELTA);
	String entMediadora = req.getParameter(ENT_MED_INC_VUELTA);
	String subentMediadora = req.getParameter(S_ENT_MED_INC_VUELTA);
	String delegacion = req.getParameter(DELEG_INC_VUELTA);
	String codPlan = req.getParameter(PLAN_INC_VUELTA);
	String codLinea = req.getParameter(LINEA_INC_VUELTA);
	String codEstado = req.getParameter(CODEST_INC_VUELTA);
	String codEstadoAgro = req.getParameter(COD_AGRO_INCO_VUELTA);
	String nifCif = req.getParameter(NIF_INC_VUELTA);
	String tipoReferencia = req.getParameter(TIPO_REF_INC_VUELTA);
	String idCupon = req.getParameter(ID_CUP_INC_VUELTA);
	String asunto = req.getParameter(ASUNT_INC_VUELTA);
	String fechaEnvioDesde = req.getParameter(FECHA_DES_INC_VUELTA);
	String fechaEnvioHasta = req.getParameter(FECHA_HAS_INC_VUELTA);
	String numIncidencia = req.getParameter(NUM_INC_VUELTA);
	String codUsuario = req.getParameter(USU_INC_VUELTA);
	String tipoinc = req.getParameter(TIPO_INC_VUELTA);
	
	if(!StringUtils.isNullOrEmpty(idIncidencia)) {
		vista.setIdincidencia(Long.parseLong(idIncidencia));
	}
	if(!StringUtils.isNullOrEmpty(codEntidad)) {
		vista.setCodentidad(new BigDecimal(codEntidad));
	}
	if(!StringUtils.isNullOrEmpty(oficina)) {
		vista.setOficina(oficina);
	}
	if(!StringUtils.isNullOrEmpty(entMediadora)) {
		vista.setEntmediadora(new BigDecimal(entMediadora));
	}
	if(!StringUtils.isNullOrEmpty(subentMediadora)) {
		vista.setSubentmediadora(new BigDecimal(subentMediadora));
	}
	if(!StringUtils.isNullOrEmpty(delegacion)) {
		vista.setDelegacion(new BigDecimal(delegacion));
	}
	if(!StringUtils.isNullOrEmpty(codPlan)) {
		vista.setCodplan(new BigDecimal(codPlan));
	}
	if(!StringUtils.isNullOrEmpty(codLinea)) {
		vista.setCodlinea(new BigDecimal(codLinea));
	}
	if(!StringUtils.isNullOrEmpty(codEstado)) {
		vista.setCodestado(new BigDecimal(codEstado));
	}
	if(!StringUtils.isNullOrEmpty(codEstadoAgro)) {
		vista.setCodestadoagro(codEstadoAgro.charAt(0));
	}
	if(!StringUtils.isNullOrEmpty(nifCif)) {
		vista.setNifcif(nifCif);
	}
	if(!StringUtils.isNullOrEmpty(tipoReferencia)) {
		vista.setTiporef(tipoReferencia.charAt(0));
	}
	if(!StringUtils.isNullOrEmpty(idCupon)) {
		vista.setIdcupon(idCupon);
	}
	if(!StringUtils.isNullOrEmpty(asunto)) {
		vista.setAsunto(asunto);
		vista.setCodasunto(asunto);
	}
	
	if(!StringUtils.isNullOrEmpty(referencia)) {
		vista.setReferencia(referencia);
	}
	try {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(!StringUtils.isNullOrEmpty(fechaEnvioDesde)) {
			vista.setFechaEnvioDesde(sdf.parse(fechaEnvioDesde));
		}
		if(!StringUtils.isNullOrEmpty(fechaEnvioHasta)) {
			vista.setFechaEnvioHasta(sdf.parse(fechaEnvioHasta));
		}	
	} catch (ParseException e) {
		logger.error(e.getMessage());
	}
	if(!StringUtils.isNullOrEmpty(numIncidencia)) {
		vista.setNumero(new BigDecimal(numIncidencia));
	}
	if(!StringUtils.isNullOrEmpty(codUsuario)) {
		vista.setCodusuario(codUsuario);
	}
	
	if(!StringUtils.isNullOrEmpty(tipoinc)) {
		vista.setTipoinc(tipoinc.charAt(0));
	}
	
	return vista;
}
	
private boolean parametrosVueltaConsultaIncidencia(final HttpServletRequest req, final VistaIncidenciasAgro vista) {
	boolean hasFilters = false;
	String idIncidencia = req.getParameter("idincidenciaAyR");
	String codEntidad = req.getParameter("codentidadAyR");
	String oficina = req.getParameter("oficinaAyR");
	String referencia =req.getParameter("referenciaAyR");
	String entMediadora = req.getParameter("entmediadoraAyR");
	String subentMediadora = req.getParameter("subentmediadoraAyR");
	String delegacion = req.getParameter("delegacionAyR");
	String codPlan = req.getParameter("codplanAyR");
	String codLinea = req.getParameter("codlineaAyR");
	String codEstado = req.getParameter("codestadoAyR");
	String codEstadoAgro = req.getParameter("codestadoagroAyR");
	String nifCif = req.getParameter("nifcifAyR");
	String tipoReferencia = req.getParameter("tiporefAyR");
	String idCupon = req.getParameter("idcuponAyR");
	String asunto = req.getParameter("asuntoAyR");
	String fechaEnvioDesde = req.getParameter("fechaEnvioDesdeIdAyR");
	String fechaEnvioHasta = req.getParameter("fechaEnvioHastaIdAyR");
	String numIncidencia = req.getParameter("numIncidenciaAyR");
	String codUsuario = req.getParameter("codusuarioAyR");
	String tipoinc = req.getParameter("tipoincAyR");
	
	String origen_aux = req.getParameter("origen");
	if (origen_aux == null || (origen_aux != null && "null".equals(origen_aux))){
		origen_aux = "";
	}
	
	if(!StringUtils.isNullOrEmpty(idIncidencia)) {
		hasFilters = true;
		vista.setIdincidencia(Long.parseLong(idIncidencia));
	}
	if(!StringUtils.isNullOrEmpty(codEntidad)) {
		hasFilters = true;
		vista.setCodentidad(new BigDecimal(codEntidad));
	}
	if(!StringUtils.isNullOrEmpty(oficina)) {
		hasFilters = true;
		vista.setOficina(oficina);
	}
	if(!StringUtils.isNullOrEmpty(entMediadora)) {
		hasFilters = true;
		vista.setEntmediadora(new BigDecimal(entMediadora));
	}
	if(!StringUtils.isNullOrEmpty(subentMediadora)) {
		hasFilters = true;
		vista.setSubentmediadora(new BigDecimal(subentMediadora));
	}
	if(!StringUtils.isNullOrEmpty(delegacion)) {
		hasFilters = true;
		vista.setDelegacion(new BigDecimal(delegacion));
	}
	if(!StringUtils.isNullOrEmpty(codPlan)) {
		hasFilters = true;
		vista.setCodplan(new BigDecimal(codPlan));
	}
	if(!StringUtils.isNullOrEmpty(codLinea)) {
		hasFilters = true;
		vista.setCodlinea(new BigDecimal(codLinea));
	}
	if(!StringUtils.isNullOrEmpty(codEstado)) {
		hasFilters = true;
		vista.setCodestado(new BigDecimal(codEstado));
	}
	if(!StringUtils.isNullOrEmpty(codEstadoAgro)) {
		hasFilters = true;
		vista.setCodestadoagro(codEstadoAgro.charAt(0));
	}
	if(!StringUtils.isNullOrEmpty(nifCif)) {
		hasFilters = true;
		vista.setNifcif(nifCif);
	}
	if(!StringUtils.isNullOrEmpty(tipoReferencia)) {
		hasFilters = true;
		vista.setTiporef(tipoReferencia.charAt(0));
	}
	if(!StringUtils.isNullOrEmpty(idCupon)) {
		hasFilters = true;
		vista.setIdcupon(idCupon);
	}
	if(!StringUtils.isNullOrEmpty(asunto)) {
		hasFilters = true;
		vista.setAsunto(asunto);
		vista.setCodasunto(asunto);
	}
	
	if(!StringUtils.isNullOrEmpty(referencia)) {
		hasFilters = true;
		vista.setReferencia(referencia);
	}
	try {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(!StringUtils.isNullOrEmpty(fechaEnvioDesde)) {
			hasFilters = true;
			vista.setFechaEnvioDesde(sdf.parse(fechaEnvioDesde));
		}
		if(!StringUtils.isNullOrEmpty(fechaEnvioHasta)) {
			hasFilters = true;
			vista.setFechaEnvioHasta(sdf.parse(fechaEnvioHasta));
		}	
	} catch (ParseException e) {
		logger.error(e.getMessage());
	}
	if(!StringUtils.isNullOrEmpty(numIncidencia)) {
		hasFilters = true;
		vista.setNumero(new BigDecimal(numIncidencia));
	}
	if(!StringUtils.isNullOrEmpty(codUsuario)) {
		hasFilters = true;
		vista.setCodusuario(codUsuario);
	}
	
	if(!StringUtils.isNullOrEmpty(tipoinc)) {
		hasFilters = true;
		vista.setTipoinc(tipoinc.charAt(0));		
	}
	
	return hasFilters;
}

	/**
     * Se registra un editor para hacer el bind de las propiedades tipo Date que vengan de la jsp. En MultiActionController no se hace este bind
     * automaticamente
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    		// True indica que se aceptan fechas vacias
    		CustomDateEditor editor = new CustomDateEditor(df, true);    		    		   		
    		binder.registerCustomEditor(Date.class, editor);
    }
    
    public AnulacionyRescisionPolManager getAnulacionyRescisionPolManager() {
		return anulacionyRescisionPolManager;
	}

	public void setAnulacionyRescisionPolManager(AnulacionyRescisionPolManager anulacionyRescisionPolManager) {
		this.anulacionyRescisionPolManager = anulacionyRescisionPolManager;
	}
	
	public void setAportarDocIncidenciaVista(String aportarDocIncidenciaVista){
		this.aportarDocIncidenciaVista = aportarDocIncidenciaVista;
	}
	
	public void setAportarDocIncidenciaManager(AportarDocIncidenciaManager aportarDocIncidenciaManager) {
		this.aportarDocIncidenciaManager = aportarDocIncidenciaManager;
	}
	public void getAportarDocIncidenciaManager(AportarDocIncidenciaManager aportarDocIncidenciaManager) {
		this.aportarDocIncidenciaManager = aportarDocIncidenciaManager;
	}
	
	public void setPolizaManager(PolizaManager polizaManager) {
		this.polizaManager = polizaManager;
	}
	
	public void setAportarDocIncidenciaDao(IAportarDocIncidenciaDao aportarDocIncidenciaDao) {
		this.aportarDocIncidenciaDao = aportarDocIncidenciaDao;
	}
	
	public PolizaDao getPolizaDao() {
		return polizaDao;
	}

	public void setPolizaDao(PolizaDao polizaDao) {
		this.polizaDao = polizaDao;
	}

	

}