package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;

import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;

public class ModelTableDecoratorHisComisionesCultivosEntidades extends TableDecorator{
	
	public String getPlan(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(ce.getLinea().getCodplan());
	}
	public String getLinea(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(ce.getLinea().getCodlinea());
	}
	public String getPctent(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		return StringUtils.nullToString(ce.getPctgeneralentidad()) + "%";
	}
	public String getPctrga(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctrga() != null)		
			resultado = StringUtils.nullToString(ce.getPctrga().setScale(2,BigDecimal.ROUND_DOWN)) + "%";
		return resultado;
	}
	public String getPctadquisicion(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctadquisicion() != null)
			resultado = StringUtils.nullToString(ce.getPctadquisicion().setScale(2,BigDecimal.ROUND_DOWN)) + "%";
		return resultado;
	}
	public String getPctadministracion(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctadministracion() != null)
			resultado = StringUtils.nullToString(ce.getPctadministracion().setScale(2,BigDecimal.ROUND_DOWN)) + "%";		
		return resultado;
	}
	
	public String getPctgeneralentidad(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctgeneralentidad() != null)
			resultado = StringUtils.nullToString(ce.getPctgeneralentidad().setScale(2,BigDecimal.ROUND_DOWN)) + "%";
		return resultado;
	}
	
	
	public Date getFecha(){
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		return ce.getFechamodificacion();
	}
	
	
	public String getFechaEfecto(){
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		String fechaEfecto = "";
		if (ce.getFechaEfecto() != null)
		{
			fechaEfecto = StringUtils.forHTML(formato.format(ce.getFechaEfecto()));
		}
		return fechaEfecto;
	}
	
	public String getGrupoNegocio(){
		String resultado = "";
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		if(null!=ce.getGrupoNegocio())
			resultado=StringUtils.nullToString(ce.getGrupoNegocio().getDescripcion());
		return resultado;
	}
	
	public String getEntSubEntMed(){
		String resultado = "";
		CultivosEntidadesHistorico ce = (CultivosEntidadesHistorico) getCurrentRowObject();
		
		if(ce.getSubentidadMediadora()!=null && ce.getSubentidadMediadora().getId()!=null){
			if(ce.getSubentidadMediadora().getId().getCodentidad()!=null){
				resultado= StringUtils.forHTML(ce.getSubentidadMediadora().getId().getCodentidad().toString());
			}
			if(ce.getSubentidadMediadora().getId().getCodsubentidad()!=null){
				resultado=resultado + " - " + 
					StringUtils.forHTML(ce.getSubentidadMediadora().getId().getCodsubentidad().toString());
			}
		}
		return resultado;
	}
	
}
