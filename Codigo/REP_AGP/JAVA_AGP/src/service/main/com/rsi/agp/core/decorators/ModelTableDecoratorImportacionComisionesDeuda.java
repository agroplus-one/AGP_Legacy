package com.rsi.agp.core.decorators;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.managers.impl.ImportacionComisionesManager;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMultIncidencias;

public class ModelTableDecoratorImportacionComisionesDeuda extends TableDecorator{
	
	public String getAdmActions() {
		FicheroMult ffc = (FicheroMult) getCurrentRowObject();
		String acciones = "";
		
		acciones += "<a href=\"javascript:descargarDeuda('"+ffc.getId()+"')\"><img src=\"jsp/img/displaytag/download.png\" alt=\"Descargar\" title=\"Descargar\"/></a>&nbsp;" +
					"<a href=\"javascript:revisarDeuda('"+ffc.getId()+"','"+ ffc.getTipoFichero() +"','"+getEstado()+"')\"><img src=\"jsp/img/folderopen.gif\" alt=\"Revisar\" title=\"Revisar\"/></a>&nbsp;";
								
		return  acciones;
	}
	
	public String getFichero(){
		FicheroMult ffc = (FicheroMult) getCurrentRowObject();
		return StringUtils.nullToString(ffc.getNombreFichero());
	}
	
	public String getEstado(){
		String estado = ImportacionComisionesManager.ESTADO_CARGADO;
		FicheroMult ffc = (FicheroMult) getCurrentRowObject();
		int estadoAviso = 0;
		Set<FicheroMultIncidencias> fIncidencia = ffc.getFicheroMultIncidenciases();
		Iterator<FicheroMultIncidencias> iter= fIncidencia.iterator();
		 while (iter.hasNext()) {
			FicheroMultIncidencias incidencia =iter.next();
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
	
	public Date getFeccarga(){
		FicheroMult ffc = (FicheroMult) getCurrentRowObject();
		return ffc.getFechaCarga();
	}

	public Date getFecacep(){
		FicheroMult ffc = (FicheroMult) getCurrentRowObject();
		return ffc.getFechaAceptacion();
	}


}
