package com.rsi.agp.core.webapp.action;

import java.math.BigDecimal;
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

import com.rsi.agp.core.managers.impl.CampoMascaraManager;
import com.rsi.agp.core.managers.impl.DiccionarioDatosManager;
import com.rsi.agp.core.managers.impl.TablaCondicionadoManager;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONArray;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONException;
import com.rsi.agp.core.webapp.util.jsonJavaUtil.JSONObject;
import com.rsi.agp.dao.tables.cgen.TablaCondicionado;
import com.rsi.agp.dao.tables.masc.CampoMascara;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;


public class CamposMascaraController extends BaseSimpleController implements Controller {
	private static final Log logger = LogFactory.getLog(CamposMascaraController.class);
	private TablaCondicionadoManager tablaCondicionadoManager;
	private DiccionarioDatosManager diccionarioDatosManager;
	private CampoMascaraManager campoMascaraManager;
	
	public CamposMascaraController() {
		super();
		setCommandClass(CampoMascara.class);
		setCommandName("campoMascaraBean");
	}
	
	@Override
	protected final ModelAndView handle(final HttpServletRequest request, HttpServletResponse response, final Object object, final BindException exception) {

		final Map<String, Object> parameters = new HashMap<String, Object>();
		final ResourceBundle bundle = ResourceBundle.getBundle("agp");
		ModelAndView mv;
		CampoMascara campoMascaraBean = (CampoMascara)object;
		CampoMascara campoMascaraBusqueda = new CampoMascara();
		String accion = request.getParameter("accion");
		
		if("listCamposMascara_ajax".equals(accion)){
			String idTablaCondicionado = request.getParameter("idTablaCondicionado");
			List<DiccionarioDatos> listDiccionarioDatosCampoLimite = diccionarioDatosManager.listByTipoCampoLimite(new BigDecimal(idTablaCondicionado));
			
			JSONArray listDiccionarioDatosCampoLimiteJSON;
			try {
				listDiccionarioDatosCampoLimiteJSON = generarJSONListCamposMascara(listDiccionarioDatosCampoLimite);
				getWriterJSON(response,listDiccionarioDatosCampoLimiteJSON); 
				return null;
			} catch (JSONException e) {
				logger.error("Error al montar la lista de campos máscara", e);
			}
			
		}else if("eliminar".equals(accion)){
			
			campoMascaraManager.deleteCampoMascara(campoMascaraBean); 
			campoMascaraBean = new CampoMascara();
			campoMascaraBusqueda = new CampoMascara();
			parameters.put("mensaje", bundle.getString("mensaje.baja.OK"));
			
			
		}else if("alta".equals(accion)){
			
			if (campoMascaraManager.existeCampoMascara(campoMascaraBean)){
				parameters.put("alerta", bundle.getString("mensaje.alta.duplicado.KO"));
			} else{
				campoMascaraManager.saveCampoMascara(campoMascaraBean);
				parameters.put("mensaje", bundle.getString("mensaje.alta.OK"));
			}
			
			campoMascaraBusqueda = new CampoMascara(campoMascaraBean.getId(), campoMascaraBean.getDiccionarioDatosByCodconceptoasoc(),
					campoMascaraBean.getTablaCondicionado(), campoMascaraBean.getDiccionarioDatosByCodconceptomasc());
			
		}else if("editarCampoMascara_ajax".equals(accion)){				
			String idCampoMascara = request.getParameter("idCampoMascara");
			campoMascaraBean = campoMascaraManager.getCampoMascara(Long.parseLong(idCampoMascara)); 
			JSONObject campoMascaraBeanJSON = new JSONObject();
			try {
				campoMascaraBeanJSON.put("id", campoMascaraBean.getId());
				campoMascaraBeanJSON.put("codtablacondicionado", campoMascaraBean.getTablaCondicionado().getCodtablacondicionado());
				campoMascaraBeanJSON.put("codconceptomasc", campoMascaraBean.getDiccionarioDatosByCodconceptomasc().getCodconcepto());
				campoMascaraBeanJSON.put("codconceptoasoc", campoMascaraBean.getDiccionarioDatosByCodconceptoasoc().getCodconcepto());
				
				getWriterJSON(response,campoMascaraBeanJSON);
				
				return null;			
			} catch (JSONException e) {					
				logger.error("Error al montar la lista de campos máscara en modo edición", e);
			}	
			
		}else if("modificar".equals(accion)){
			
			if (campoMascaraManager.existeCampoMascara(campoMascaraBean)){
				parameters.put("alerta", bundle.getString("mensaje.modificacion.duplicado.KO"));
			} else{
				campoMascaraManager.saveCampoMascara(campoMascaraBean);
				parameters.put("mensaje", bundle.getString("mensaje.modificacion.OK"));
			}
			
			campoMascaraBusqueda = new CampoMascara(campoMascaraBean.getId(), campoMascaraBean.getDiccionarioDatosByCodconceptoasoc(),
					campoMascaraBean.getTablaCondicionado(), campoMascaraBean.getDiccionarioDatosByCodconceptomasc());
		}
		
		//Consultar y Limpiar
		else{
			campoMascaraBusqueda = campoMascaraBean;
		}

		List<TablaCondicionado> listTiposCamposLimites = tablaCondicionadoManager.listTiposCamposLimites();
		List<DiccionarioDatos> listDiccionarioDatos = diccionarioDatosManager.listAll();

		List<CampoMascara> listCamposMascara = campoMascaraManager.listCamposMascara(campoMascaraBusqueda);
		
		parameters.put("listTiposCamposLimites", listTiposCamposLimites);
		parameters.put("listDiccionarioDatos", listDiccionarioDatos);
		parameters.put("listCamposMascara", listCamposMascara);
		
		mv = new ModelAndView("moduloTaller/camposMascara/camposMascara","campoMascaraBean", campoMascaraBean);
		mv.addAllObjects(parameters);
		
		return mv;
		
	}

	private JSONArray generarJSONListCamposMascara(List<DiccionarioDatos> listDiccionarioDatosCampoLimite) throws JSONException{
		JSONArray listDiccionarioDatosCampoLimiteJSON = new JSONArray();
		JSONObject  diccionarioDatoCampoLimiteJSON= null;
		
		for(DiccionarioDatos diccionarioDato:listDiccionarioDatosCampoLimite){
			
			diccionarioDatoCampoLimiteJSON = new JSONObject();
			diccionarioDatoCampoLimiteJSON.put("codconcepto", diccionarioDato.getCodconcepto());
			diccionarioDatoCampoLimiteJSON.put("nomconcepto", diccionarioDato.getNomconcepto());
			diccionarioDatoCampoLimiteJSON.put("desconcepto", diccionarioDato.getDesconcepto());
			diccionarioDatoCampoLimiteJSON.put("deducible", diccionarioDato.getDeducible());
			listDiccionarioDatosCampoLimiteJSON.put(diccionarioDatoCampoLimiteJSON);
			
		}
		
		return listDiccionarioDatosCampoLimiteJSON;
		
	}
	public void setTablaCondicionadoManager(TablaCondicionadoManager tablaCondicionadoManager) {
		this.tablaCondicionadoManager = tablaCondicionadoManager;
	}

	public void setDiccionarioDatosManager(DiccionarioDatosManager diccionarioDatosManager) {
		this.diccionarioDatosManager = diccionarioDatosManager;
	}

	public void setCampoMascaraManager(CampoMascaraManager campoMascaraManager) {
		this.campoMascaraManager = campoMascaraManager;
	}	
}