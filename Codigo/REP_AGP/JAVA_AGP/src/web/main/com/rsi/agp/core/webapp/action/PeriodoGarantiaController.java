package com.rsi.agp.core.webapp.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.managers.impl.PeriodoGarantiaManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cesp.PeriodoGarantiaCe;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;
import com.rsi.agp.dao.tables.cpl.Cultivo;
import com.rsi.agp.dao.tables.cpl.EstadoFenologico;
import com.rsi.agp.dao.tables.cpl.Modulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PeriodoGarantiaController extends BaseSimpleController implements Controller {

	private static final Log LOG = LogFactory.getLog(PeriodoGarantiaController.class);
	private PeriodoGarantiaManager periodoGarantiaManager;
	final ResourceBundle bundle = ResourceBundle.getBundle("agp");
	PeriodoGarantiaCe periodoGarantiaBean = new PeriodoGarantiaCe();
	private SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd-MM-yyyy");

	public PeriodoGarantiaController() {
		super();
		setCommandClass(PeriodoGarantiaCe.class);
		setCommandName("periodoGarantiaBean");
	}

	@Override
	protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response, final Object object, final BindException exception) throws BusinessException {
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		PeriodoGarantiaCe periodoGarantiaCeBean = (PeriodoGarantiaCe) object;		
		final String operacion = request.getParameter("operacion");
		
		if ("alta".equalsIgnoreCase(operacion)) {
		
			periodoGarantiaManager.guardaPeriodoGarantia(periodoGarantiaCeBean);
			parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
			periodoGarantiaCeBean = new PeriodoGarantiaCe();
			
		}else if("modificar".equalsIgnoreCase(operacion)){
			
			periodoGarantiaManager.guardaPeriodoGarantia(periodoGarantiaCeBean);
			parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
			periodoGarantiaCeBean = new PeriodoGarantiaCe();
			
		} else if ("eliminar".equalsIgnoreCase(operacion)) {
			
			periodoGarantiaManager.bajaPeriodoGarantia(periodoGarantiaCeBean);
			periodoGarantiaCeBean = new PeriodoGarantiaCe();
			parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
		
		} else  if("editar_ajax".equalsIgnoreCase(operacion)){

			try{	
				String idPeriodoGarantiaCe = request.getParameter("idPeriodoGarantiaCe");
				periodoGarantiaCeBean = periodoGarantiaManager.getPeriodoGarantiaCe(Long.parseLong(idPeriodoGarantiaCe)); 
				
				JSONObject periodoGarantiaCeBeanJSON = new JSONObject();
				periodoGarantiaCeBeanJSON.put("idPeriodoGarantiaCe", periodoGarantiaCeBean.getId());
				periodoGarantiaCeBeanJSON.put("idLinea", periodoGarantiaCeBean.getLinea().getCodlinea());
				periodoGarantiaCeBeanJSON.put("idCultivo", periodoGarantiaCeBean.getCultivo().getId().getCodcultivo());
				periodoGarantiaCeBeanJSON.put("nummesesini", periodoGarantiaCeBean.getNummesesini());
				periodoGarantiaCeBeanJSON.put("numdiasini", periodoGarantiaCeBean.getNumdiasini());
				periodoGarantiaCeBeanJSON.put("fechaini", periodoGarantiaCeBean.getFechaini() != null ? simpleDateFormat.format(periodoGarantiaCeBean.getFechaini()) : "");
				periodoGarantiaCeBeanJSON.put("nummesesfin", periodoGarantiaCeBean.getNummesesfin());
				periodoGarantiaCeBeanJSON.put("numdiasfin", periodoGarantiaCeBean.getNumdiasfin());
				periodoGarantiaCeBeanJSON.put("fechafin", periodoGarantiaCeBean.getFechafin() != null ? simpleDateFormat.format(periodoGarantiaCeBean.getFechafin()) : "");
				periodoGarantiaCeBeanJSON.put("numdiasfin", periodoGarantiaCeBean.getNumdiasfin());
				periodoGarantiaCeBeanJSON.put("estadofenologicoini", periodoGarantiaCeBean.getEstadofenologicoini());
				periodoGarantiaCeBeanJSON.put("estadofenologicofin", periodoGarantiaCeBean.getEstadofenologicofin());
				
				
				getWriterJSON(response, periodoGarantiaCeBeanJSON);
				
				
				return null;
		
			}catch(Exception e){			
				
				logger.error(e);
				throw new RuntimeException("Se ha producido un error durante la generación y envío del objeto JSON", e);
			} 
			
		} else if ("getCultivos_ajax".equalsIgnoreCase(operacion)) {
			getCultivos_ajax(request, response);
			return null;
		} else if ("getEstadosFenologicos_ajax".equalsIgnoreCase(operacion)) {
			getEstadosFenologicos_ajax(request, response);
			return null;
		} 


		final List<PeriodoGarantiaCe> listaPeriodoGarantiaCe = periodoGarantiaManager.consultaPeriodoGarantia(periodoGarantiaCeBean);
		final List listaLineas = periodoGarantiaManager.getAllLineas();
		
		parameters.put("listaPeriodoGarantiaCe", listaPeriodoGarantiaCe);
		parameters.put("listaLineas", listaLineas);

		final ModelAndView resultado = new ModelAndView("moduloTaller/condicionesEspeciales/periodoGarantia", "periodoGarantiaBean", periodoGarantiaCeBean);

		resultado.addAllObjects(parameters);
		return resultado;

	
	}

	private final void getCultivos_ajax(final HttpServletRequest request, final HttpServletResponse response) {
		JSONObject object;
		final JSONArray array = new JSONArray();
		final String codLinea = request.getParameter("codLinea");

		final List<Object[]> listaCultivos = periodoGarantiaManager.getCultivosByCodLinea(codLinea);
		for (Object[] cultivo : listaCultivos) {
			object = new JSONObject();
			try {
				object.put("value",cultivo[0]);
				object.put("nodeText",cultivo[0]+" - "+cultivo[1]);
				array.put(object);
			} catch (final JSONException e) {
				LOG.warn("Imposible transformar el cultivo en un objeto JSON", e);
			}
		}
		try {
			getWriterJSON(response, array);
		} catch (final Exception e) {
			LOG.warn("Imposible escribir la lista de cultivos en la página", e);
		}
	}

	private final void getEstadosFenologicos_ajax(final HttpServletRequest request, final HttpServletResponse response) {
		JSONObject object;
		final JSONArray array = new JSONArray();
		final BigDecimal codcultivo = new BigDecimal(request.getParameter("codCultivo"));

		final List<Object[]> listaEstadosFenologicos = periodoGarantiaManager.getEstadosFenologicosByCodCultivo(codcultivo);
		for (Object[] estadoFenologico : listaEstadosFenologicos) {
			object = new JSONObject();
			try {
				object.put("value", estadoFenologico[0]);
				object.put("nodeText", estadoFenologico[1]);
				array.put(object);
			} catch (final JSONException e) {
				LOG.warn("Imposible transformar el cultivo en un objeto JSON", e);
			}
		}
		try {
			getWriterJSON(response, array);
		} catch (final Exception e) {
			LOG.warn("Imposible escribir la lista de cultivos en la página", e);
		}
	}
	
	public void setPeriodoGarantiaManager(final PeriodoGarantiaManager periodoGarantiaManager) {
		this.periodoGarantiaManager = periodoGarantiaManager;
	}

}
