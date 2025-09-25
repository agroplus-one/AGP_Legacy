/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  21/06/2010  Ernesto Laura     Decorator para listados de historico
* 											      
*
 **************************************************************************************************
*/
package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.log.HistImportaciones;

public class ModelTableDecoratorHistorico extends TableDecorator{
	
    public String getHistSelec() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	return "<a href=\"javascript:historico.consultaDetalleHistorico("+hi.getIdhistorico().toString()+")\"><img src=\"jsp/img/magnifier.png\" alt=\"Detalle\" title=\"Detalle\"/></a>";
    }
    
    public Date getHistFecImport() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();    	   
    	return hi.getFechaimport();
    }
    
    public String getHistTipoImport() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	String dev = "";
    	if (hi.getTipoImportacion()!=null)
    	{
	    	if (null == hi.getTipoImportacion().getDescripcion())
	    	{
	    		dev = "";
	    	}
	    	else{
	    		dev = hi.getTipoImportacion().getDescripcion();
	    	}
    	}
    	return dev;
    }
    
    public String getHistEstado() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	return hi.getEstado();
    }
    
    public Date getHistFechaAct() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	if(hi.getLinea() != null && StringUtils.nullToString(hi.getLinea().getActivo()).equals("SI")){
    		return hi.getLinea().getFechaactivacion();
    	}else{
    		return null;
    	}    	
    }
    
    public String getHistLineaId() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	if (hi.getLinea() != null)
    		return hi.getLinea().getCodlinea()+"/"+hi.getLinea().getCodplan();
    	else
    		return null;
    }
    
    public String getHistPlan() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	if (hi.getLinea() != null)
    		return hi.getLinea().getCodplan().toString();
    	else
    		return null;
    }
    
    public String getHistLinea() {
    	HistImportaciones hi = (HistImportaciones)getCurrentRowObject();
    	if (hi.getLinea() != null)
    		return hi.getLinea().getCodlinea().toString();
    	else
    		return null;
    }
}
