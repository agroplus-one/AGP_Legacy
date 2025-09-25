package com.rsi.agp.core.webapp.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.rsi.agp.core.managers.impl.HistoricoComCultivosManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;

public class HistoricoComCultivosController extends BaseMultiActionController{

	private static final Log logger = LogFactory.getLog(HistoricoComCultivosController.class);
	private HistoricoComCultivosManager historicoComCultivosManager;
	
	public ModelAndView doConsulta(HttpServletRequest request,HttpServletResponse response, CultivosEntidades cultivosEntidades) throws Exception {
		logger.debug("init: HistoricoComCultivos - doConsulta");
		ModelAndView mv = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<CultivosEntidadesHistorico> listCultivosEntidadesHistorico = new ArrayList<CultivosEntidadesHistorico>();
		CultivosEntidades CultEnt = new CultivosEntidades();
		String id = "";
		String grupoNegDescripcion="";
		String esMed="";
		if (!StringUtils.nullToString(request.getParameter("id")).equals("")){
			id = request.getParameter("id");			
			CultEnt = (CultivosEntidades) historicoComCultivosManager.getCultEnt(new Long(id));
		}

		try{
			listCultivosEntidadesHistorico = historicoComCultivosManager.getListHistoricoComCultivos(new Long(id));
			
			
			parameters.put("totalListSize", listCultivosEntidadesHistorico.size());
			parameters.put("listHisCe", listCultivosEntidadesHistorico);		
			parameters.put("plan", CultEnt.getLinea().getCodplan());
			parameters.put("linea", CultEnt.getLinea().getCodlinea());
			parameters.put("desc_linea", CultEnt.getLinea().getNomlinea());
			parameters.put("comMaximo", CultEnt.getPctgeneralentidad().toString());
			if (CultEnt.getPctadquisicion() != null){
				parameters.put("pctadquisicion", CultEnt.getPctadquisicion().toString());
			}
			if (CultEnt.getPctadministracion() != null){
				parameters.put("pctAdministracion", CultEnt.getPctadministracion().toString());
			}
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			String fecEfecto = "";
			if (CultEnt.getFechaEfecto() != null)
			{
				fecEfecto = StringUtils.forHTML(formato.format(CultEnt.getFechaEfecto()));
			}
			parameters.put("fecEfecto", fecEfecto);
			
			if(null!=CultEnt.getGrupoNegocio() && null!=CultEnt.getGrupoNegocio().getDescripcion() )
				grupoNegDescripcion=CultEnt.getGrupoNegocio().getDescripcion();
			if (null != CultEnt.getSubentidadMediadora()){
				if(null!=CultEnt.getSubentidadMediadora().getId() && null!= CultEnt.getSubentidadMediadora().getId().getCodentidad())
					esMed=CultEnt.getSubentidadMediadora().getId().getCodentidad().toString() + " - " 
							+ CultEnt.getSubentidadMediadora().getId().getCodsubentidad().toString();
			}
			
			parameters.put("grupoNegDesc", grupoNegDescripcion);
			parameters.put("esMed", esMed);
			
			mv = new ModelAndView("moduloComisiones/historicoComCultivos", "cultEntHistoricoBean", cultivosEntidades);
			
		} catch (Exception be) {
			logger.error("Se ha producido un error: " + be.getMessage());
			throw new Exception("Se ha producido un error: " + be.getMessage());
		}
		
		logger.debug("end: HistoricoComCultivos - doConsulta");
		return mv.addAllObjects(parameters);
	
	}


	public void setHistoricoComCultivosManager(
			HistoricoComCultivosManager historicoComCultivosManager) {
		this.historicoComCultivosManager = historicoComCultivosManager;
	}

	
	
}