package com.rsi.agp.core.decorators;

import java.util.Date;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.managers.impl.ActivacionLineasManager;
import static com.rsi.agp.core.util.Constants.*;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ModelTableDecoratorActvLineas extends TableDecorator 
{	
	//DAA 29/01/13
	public String getLineaSelec () 
	{	
		String cadena = "";
		Linea linea = (Linea) getCurrentRowObject();
		String detalle = "<a href=\"javascript:activacion.detalleTablas('"+linea.getLineaseguroid()+"')\"><img width=\"16\" alt=\"Detalle Importaci&oacute;n\" src=\"jsp/img/magnifier.png\"/></a>&nbsp;"; 
		String coberturas = "<a href=\"javascript:activacion.coberturas('"+linea.getLineaseguroid()+ "','" + linea.getEsLineaGanadoCount() +"')\"><img width=\"16\" alt=\"Coberturas\" src=\"jsp/img/displaytag/replicar.png\"/></a>&nbsp;"; 
		String bloquear = "<a href=\"javascript:activacion.bloquear('"+linea.getLineaseguroid()+"')\"><img width=\"16\" alt=\"Bloquear\" src=\"jsp/img/displaytag/cancel.png\"/></a>";
		String forzarActivar = "<a href=\"javascript:activacion.showPopUpForzarActivar('"+linea.getLineaseguroid()+"')\"><img width=\"16\" alt=\"Forzar activar\" src=\"jsp/img/displaytag/accept.png\"/></a>&nbsp;";
		String activar = "<a href=\"javascript:activacion.activar('"+linea.getLineaseguroid()+"')\"><img width=\"16\" alt=\"Activar\" src=\"jsp/img/displaytag/accept.png\"/></a>&nbsp;";
		
		//siempre muestro dettale
		cadena = detalle; 
			
		if(LINEA_ACTIVA_SI.equalsIgnoreCase(linea.getActivo())){
			cadena += bloquear;
		}else{
			if(LINEA_IMPORTADA_INCOMPLETA.equalsIgnoreCase(linea.getEstado())){
				cadena += forzarActivar;
			}else{
				if(!LINEA_IMPORTADA_ERROR.equalsIgnoreCase(linea.getEstado())){
					cadena += activar; 
				}
			}	
		}
		
		//IGT 14/10/2019: se muestran siempre (P0061436)
		cadena += coberturas;
		
		return cadena;
	}
	
	public String getLineaPlan () 
	{
		Linea linea = (Linea) getCurrentRowObject();
		return linea.getCodplan()+"";
	}
	
	public String getLineaLinea () 
	{
		Linea linea = (Linea) getCurrentRowObject();
		return linea.getCodlinea()+"";
	}
	
	public String getLineaImport () 
	{
		Linea linea = (Linea) getCurrentRowObject();
		if (linea.getEstado().equalsIgnoreCase(ActivacionLineasManager.ESTADO_IMPORTADO))
			return ActivacionLineasManager.ESTADO_IMPORTADO;
		else if (linea.getEstado().equalsIgnoreCase(ActivacionLineasManager.ESTADO_INCOMPLETO))
			return ActivacionLineasManager.ESTADO_INCOMPLETO;
		else if (linea.getEstado().equalsIgnoreCase(ActivacionLineasManager.ESTADO_ERROR))
			return ActivacionLineasManager.ESTADO_ERROR;
		else 
			return "";
	}
	
	public String getLineaActivado () 
	{
		Linea linea = (Linea) getCurrentRowObject();
		
		String dev = "";
		
		if (StringUtils.nullToString(linea.getActivo()).equals("BL")){
			dev = "SI";
		}
		else{
			dev = linea.getActivo();
		}
		
		return dev;
	}
	
	public Date getLineaFechaAct () 
	{
		Linea linea = (Linea) getCurrentRowObject();
		return linea.getFechaactivacion();
	}
	
	public String getLineaBloqueado () {
		Linea linea = (Linea) getCurrentRowObject();
		
		String dev = "";
		
		if (StringUtils.nullToString(linea.getActivo()).equals("BL")){
			dev = "SI";
		}
		else{
			dev = "NO";
		}
		return dev;
	}
	
}
