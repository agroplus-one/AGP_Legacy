package com.rsi.agp.core.decorators;

import java.math.BigDecimal;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.dao.tables.config.PantallaConfigurable;
import com.rsi.agp.dao.tables.config.RelacionCampo;



public class ModelTableDecoratorRelacionCampos extends TableDecorator{
    
    public String getAdmActions() {
    	RelacionCampo  rc = (RelacionCampo)getCurrentRowObject();
    	return "<a href=\"javascript:editar('"+ rc.getIdrelacion().toString() +"')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a> " +
    	       "<a href=\"javascript:eliminar('"+ rc.getIdrelacion().toString() +"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Eliminar\" title=\"Eliminar\"/></a>" +
    	       	"<input type='hidden' name='idRow' value='"+ rc.getIdrelacion().toString() +"' />";
    }

    // Campo obligatorio
    public String getAdmTipocampo() {
    	RelacionCampo  rc = (RelacionCampo)getCurrentRowObject();
    	return rc.getTipocampo();
    }

    // Obligatorio si 
    public String getAdmProcesocalculo() {
    	RelacionCampo  rc = (RelacionCampo)getCurrentRowObject();
    	return rc.getProcesocalculo();
    }
    
    public String getAdmCampoSC() {
       RelacionCampo  rc = (RelacionCampo)getCurrentRowObject();
       return rc.getDiccionarioDatos().getNomconcepto();
    }
    
    public String getAdmFactor(){
    	try{
	       RelacionCampo  rc = (RelacionCampo)getCurrentRowObject();
	       return  rc.getGrupoFactores().getDescgrupofactores();
    	}
    	catch (Exception e) {
			return "";
		}
    }
}
