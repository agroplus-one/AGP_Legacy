package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;

public class ModelTableDecoratorComisionesCultivosEntidades extends TableDecorator{

	public String getAdmActions() {
		
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String cadena = "";
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		String fecEfecto = "";
		if (ce.getFechaEfecto() != null)
		{
			fecEfecto = StringUtils.forHTML(formato.format(ce.getFechaEfecto()));
		}
		String grupoNegocio="";
		String grupoNegocioDescripcion="";
		String entMed="";
		String subEntMed="";
		if(null!=ce.getGrupoNegocio()&& null!=ce.getGrupoNegocio().getGrupoNegocio()){
			grupoNegocio=ce.getGrupoNegocio().getGrupoNegocio().toString();
			grupoNegocioDescripcion=ce.getGrupoNegocio().getDescripcion();
		}	
		if(ce.getSubentidadMediadora()!=null && ce.getSubentidadMediadora().getId()!=null){
			if(ce.getSubentidadMediadora().getId().getCodentidad()!=null){
				entMed= ce.getSubentidadMediadora().getId().getCodentidad().toString();
			}
			if(ce.getSubentidadMediadora().getId().getCodsubentidad()!=null){
				subEntMed=ce.getSubentidadMediadora().getId().getCodsubentidad().toString();
			}
		}
		if (ce.getFechaBaja() == null){
			cadena += "<a href=\"javascript:modificar('"+ce.getId()+"','"+ce.getLinea().getCodplan()+"','"
						+ce.getLinea().getCodlinea()+"','"+ce.getLinea().getLineaseguroid() + "','"
						+ce.getPctgeneralentidad()+"','"+StringUtils.nullToString(ce.getPctrga())+"','"
						+ce.getUsuario().getCodusuario()+"','"+StringUtils.nullToString(ce.getPctadquisicion())+"','"
						+StringUtils.nullToString(ce.getPctadministracion())+"','"+fecEfecto+"','"
						+StringUtils.nullToString(ce.getLinea().getNomlinea())+"','"+grupoNegocio+"','"
						+entMed+"','"+subEntMed
							+"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";		
			cadena += "<a href=\"javascript:borrar('"+ce.getId()+"','"+ce.getLinea().getCodplan()+"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";
		}
		cadena += "<a href=\"javascript:verHistorico('"+ce.getId()+"','"+ce.getLinea().getCodplan()+"','"
				+ce.getLinea().getCodlinea()+"','"+ce.getLinea().getLineaseguroid() + "','"
				+ce.getPctgeneralentidad()+"','"+StringUtils.nullToString(ce.getPctrga())+"','"
				+ce.getUsuario().getCodusuario()+"','"+StringUtils.nullToString(ce.getPctadquisicion())+"','"
				+StringUtils.nullToString(ce.getPctadministracion())+"','"+fecEfecto+"','"
				+grupoNegocioDescripcion+"','"+entMed+"','"+subEntMed				
				+"')\"><img src=\"jsp/img/magnifier.png\" alt=\"Consultar Histórico\" title=\"Consultar Histórico\"/></a>&nbsp;";
		
		
		if (ce.getFechaBaja() == null){
			cadena =  "<input type=\"checkbox\" id=\"checkParcela_" + ce.getId() + "\"  name=\"checkParcela_" + ce.getId() + "\" onClick=\"onClickInCheck2('checkParcela_" + ce.getId()+ "');\" value=\""+ce.getId().toString()+ "#" +ce.getLinea().toString()+"|"+ "\"/>" + cadena;
		}
		return cadena;
	}
	
	public String getCodPlan(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		return StringUtils.nullToString(ce.getLinea().getCodplan());
	}
	public String getCodLinea(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		return StringUtils.nullToString(ce.getLinea().getCodlinea());
	}
	public String getPctent(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		return StringUtils.nullToString(ce.getPctgeneralentidad()) + "%";
	}
	public String getPctrga(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctrga() != null)
			resultado = StringUtils.nullToString(ce.getPctrga()) + "%";
		
		return resultado;
	}
	public String getPctadquisicion(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String  resultado = "";
		if(ce.getPctadquisicion() != null)
			resultado = StringUtils.nullToString(ce.getPctadquisicion().setScale(2,BigDecimal.ROUND_DOWN)) + "%";
		
		return resultado;
	}
	public String getPctadministracion(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctadministracion() != null)
			resultado = StringUtils.nullToString(ce.getPctadministracion().setScale(2,BigDecimal.ROUND_DOWN)) + "%";
		return resultado;
	}
	public String getPctgeneralentidad(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String resultado = "";
		if(ce.getPctgeneralentidad() != null)
			resultado = StringUtils.nullToString(ce.getPctgeneralentidad().setScale(2,BigDecimal.ROUND_DOWN)) + "%";	
		return resultado;
	}
	
	public Date getFecha(){
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		return ce.getFechamodificacion();
	}
	
	public String getGrupoNegocio(){
		String resultado = "";
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		if(null!=ce.getGrupoNegocio())
			resultado=StringUtils.nullToString(ce.getGrupoNegocio().getDescripcion());
		return resultado;
	}
	
	public String getEntSubEntMed(){
		String resultado = "";
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		
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
	
	/*
	public String getFechaEfecto(){
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		CultivosEntidades ce = (CultivosEntidades) getCurrentRowObject();
		String fechaEfecto = "";
		if (ce.getFechaEfecto() != null)
		{
			fechaEfecto = StringUtils.forHTML(formato.format(ce.getFechaEfecto()));
		}
		return fechaEfecto;
	}
	*/
}
