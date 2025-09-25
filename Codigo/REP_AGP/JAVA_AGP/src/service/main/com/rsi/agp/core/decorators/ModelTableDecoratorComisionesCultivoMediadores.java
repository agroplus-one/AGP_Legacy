package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;

public class ModelTableDecoratorComisionesCultivoMediadores extends TableDecorator{


	
	public String getAdmActions() {
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaEfec ="";
		String acciones = "";
		if (cs.getFecEfecto()!= null)
			fechaEfec= sdf.format(cs.getFecEfecto());
		
		
		 if (cs.getFecBaja()==null) {
			 acciones = "<a href=\"javascript:modificar('"+cs.getId()+"','"+cs.getLinea().getCodplan()+"','"+ cs.getLinea().getCodlinea()+"'," +
						"'"+cs.getSubentidadMediadora().getId().getCodentidad()+"','"+cs.getSubentidadMediadora().getId().getCodsubentidad()+"'," +
						"'"+StringUtils.nullToString(cs.getPctmediador()) + "','"+cs.getUsuario().getCodusuario()+"','"+cs.getEntidad().getNomentidad() +
						"','" + cs.getSubentidadMediadora().getNomSubentidadCompleto()+"','"+ fechaEfec +"','" + cs.getLinea().getNomlinea()+"')\">" +
								"<img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
		 
			 acciones +=  "<a href=\"javascript:borrar('"+cs.getId()+"')\" /><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";
			 
			
			 acciones += "<a href=\"javascript:verDetalleLinea('"+cs.getSubentidadMediadora().getId().getCodentidad()+
				 					"','"+ cs.getSubentidadMediadora().getId().getCodsubentidad()+ 
				 				 	"'," + cs.getLinea().getLineaseguroid()+","+cs.getLinea().getCodplan()+","+cs.getLinea().getCodlinea()+")\"><img src=\"jsp/img/displaytag/detalle.gif\" alt=\"Detalle\" title=\"Detalle\"/></a>&nbsp;";
			
		 }else {
			 acciones += "<img src=\"jsp/img/displaytag/transparente.gif\" width='16' height='16'/>&nbsp;";;
		 }		 
		 acciones += "<a href=\"javascript:consultarHistorico("+cs.getId()+",'"+cs.getSubentidadMediadora().getId().getCodentidad()+
				 			"','"+ cs.getSubentidadMediadora().getId().getCodsubentidad()+"',"+cs.getLinea().getLineaseguroid()+
				 			"," + cs.getLinea().getCodlinea()+","+cs.getLinea().getCodplan()+
				 			"," + cs.getPctmediador()+",'"+ cs.getSubentidadMediadora().getNomsubentidad() +"')\"><img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/></a>&nbsp;";
		 
		 
		 return acciones;
	}
	
	public String getPlan(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return StringUtils.nullToString(cs.getLinea().getCodplan());
	}
	public String getLinea(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return StringUtils.nullToString(cs.getLinea().getCodlinea());
	}
	public String getPctent(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();

		
		if(cs.getPctentidad() != null){
			return StringUtils.nullToString(cs.getPctentidad().setScale(2, BigDecimal.ROUND_DOWN)) + "%";
		}	
		return "";
	}
	public String getPctmed(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		
		if(cs.getPctmediador() != null){
			return StringUtils.nullToString(cs.getPctmediador().setScale(2, BigDecimal.ROUND_DOWN)) + "%";
		}	
		return "";
	}
	public Date getFechaEfecto(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return cs.getFecEfecto();
	}
	public Date getFecBaja(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return cs.getFecBaja();
	}
	public String getEntidad(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return StringUtils.nullToString(cs.getSubentidadMediadora().getId().getCodentidad());
	}
	public String getSubentidad(){
		CultivosSubentidades cs = (CultivosSubentidades) getCurrentRowObject();
		return StringUtils.nullToString(cs.getSubentidadMediadora().getId().getCodsubentidad());
	}
}
