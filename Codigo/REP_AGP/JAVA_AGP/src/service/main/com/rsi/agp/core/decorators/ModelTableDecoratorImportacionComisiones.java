package com.rsi.agp.core.decorators;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.managers.impl.ImportacionComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.Fichero;
import com.rsi.agp.dao.tables.comisiones.FicheroIncidencia;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitida;
import com.rsi.agp.dao.tables.comisiones.reglamento.ReglamentoProduccionEmitidaSituacion;

public class ModelTableDecoratorImportacionComisiones extends TableDecorator{
	
	public String getAdmActions() {
		Fichero ffc = (Fichero) getCurrentRowObject();
		String acciones = "";
		
		acciones += "<a href=\"javascript:descargar('"+ffc.getId()+"')\"><img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar\" title=\"Descargar\"/></a>&nbsp;" +
					"<a href=\"javascript:revisar('"+ffc.getId()+"','"+ffc.getTipofichero()+"','"+getEstado()+"')\"><img src=\"jsp/img/folderopen.gif\" alt=\"Revisar\" title=\"Revisar\"/></a>&nbsp;"+
					"<a href=\"javascript:ver('"+ffc.getId()+"','"+ffc.getTipofichero()+"','"+ffc.getFase().getFase()+"','"+getEstado()+"','"+ffc.getNombrefichero()+"')\"><img src=\"jsp/img/displaytag/information.png\" alt=\"Ver\" title=\"Ver\"/></a>&nbsp;";
		
		if(ffc.getFase().getCierre() == null)
			acciones +="<a href=\"javascript:borrar('"+ffc.getId()+"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";
		else
			acciones +="<img src=\"jsp/img/displaytag/transparente.gif\" width='16' height='16'/>";			
		return  acciones;
	}
	
	public String getFichero(){
		Fichero ffc = (Fichero) getCurrentRowObject();
		return StringUtils.nullToString(ffc.getNombrefichero());
	}
	
	public String getEstado(){
		String estado = ImportacionComisionesManager.ESTADO_CARGADO;
		Fichero ffc = (Fichero) getCurrentRowObject();
		int estadoAviso = 0;
		Set<FicheroIncidencia> fIncidencia = ffc.getFicheroIncidencias();
		Iterator<FicheroIncidencia> iter= fIncidencia.iterator();
		 while (iter.hasNext()) {
	        FicheroIncidencia incidencia =iter.next();
	        estado = ImportacionComisionesManager.ESTADO_CORRECTO;
			if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_ERRONEO)){
				return ImportacionComisionesManager.ESTADO_ERRONEO;
			} else if (incidencia.getEstado().equals(ImportacionComisionesManager.ESTADO_AVISO)){
				estadoAviso ++;
			}
		}
		if (estadoAviso>0)
			return  ImportacionComisionesManager.ESTADO_AVISO;
		else
			return estado;
	}
	
	public String getCodigosituacion()
	{
		String cod = "";
		Fichero ffc = (Fichero) getCurrentRowObject();
		Set<ReglamentoProduccionEmitida> fReglamento= ffc.getFicheroReglamentos();
		if (fReglamento != null && fReglamento.size() > 0)
		{
			ReglamentoProduccionEmitida fichReg = fReglamento.iterator().next();
			Set<ReglamentoProduccionEmitidaSituacion> fichRegAplList = fichReg.getReglamentoProduccionEmitidaSituacions();
			if (fichRegAplList != null && fichRegAplList.size() > 0)
			{
				ReglamentoProduccionEmitidaSituacion ficApli = fichRegAplList.iterator().next();
				
				if (ficApli.getCodigo()  != null)
					cod= ficApli.getCodigo().toString();
				
			}
		}
		if (!cod.equals(""))
		{
			if (cod.equals("0"))
			{
				cod = "Regularizada";
			}
			if (cod.equals("1"))
			{
				cod = "En Fase";
			}
		}
		return cod;
	}
	public Date getFeccarga(){
		Fichero ffc = (Fichero) getCurrentRowObject();
		return ffc.getFechacarga();
	}
	public Date getFecemit(){
		Fichero ffc = (Fichero) getCurrentRowObject();
		return ffc.getFase().getFechaemision();
	}
	public Date getFecacep(){
		Fichero ffc = (Fichero) getCurrentRowObject();
		return ffc.getFechaaceptacion();
	}
	public Date getFeccierre(){
		Fichero ffc = (Fichero) getCurrentRowObject();
		if(ffc.getFase().getCierre() != null)
			return ffc.getFase().getCierre().getFechacierre();
		else
			return null;
	}

}
