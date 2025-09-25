package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidadesHistorico;


public class ModelTableDecoratorComisionesCultivoMediadoresHistorico extends TableDecorator{

	public String getPlan(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getLinea().getCodplan());
	}
	public String getLinea(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getLinea().getCodlinea());
	}
	public String getPctent(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();

		
		if(cs.getPctentidad() != null){
			return StringUtils.nullToString(cs.getPctentidad().setScale(2, BigDecimal.ROUND_DOWN)) + "%";
		}	
		return "";
	}
	public String getPctmed(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		
		if(cs.getPctmediador() != null){
			return StringUtils.nullToString(cs.getPctmediador().setScale(2, BigDecimal.ROUND_DOWN)) + "%";
		}	
		return "";
	}
	public Date getFechaEfecto(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return cs.getFecEfecto();
	}
	
	public String getEntidad(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getCodentidadSM());
	}
	public String getSubentidad(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getCodsubentidadSM());
	}
	public String getTipoMov(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getAccion());
	}
	public Date getFechaMov(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return cs.getFechamodificacion();
	}
	public String getUsuario(){
		CultivosSubentidadesHistorico cs = (CultivosSubentidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(cs.getUsuario().getCodusuario());
	}
	

}
