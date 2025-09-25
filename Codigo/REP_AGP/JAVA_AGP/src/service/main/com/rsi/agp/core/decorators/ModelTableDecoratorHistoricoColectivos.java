package com.rsi.agp.core.decorators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;
import com.rsi.agp.dao.tables.admin.HistoricoColectivos;
import com.rsi.agp.dao.tables.commons.Usuario;


public class ModelTableDecoratorHistoricoColectivos extends TableDecorator
{
		
	public String getColEntidad ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getTomador().getId().getCodentidad().toString();
	}
	
	public String getColPlan ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getLinea().getCodplan().toString();
	}
	
	public String getColLinea ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getLinea().getCodlinea().toString();
	}
	
	public String getColId ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		//TMR 18/07/2012
		//return co.getIdcolectivo() + "-" + co.getDc();
		return co.getReferencia() + "-" + co.getDc();
	}
	
	public String getColNombre ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getNomcolectivo();
	}
	
	public String getColEntMed ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getSubentidadMediadora().getId().getCodentidad().toString()+" - "+
			   co.getSubentidadMediadora().getId().getCodsubentidad().toString();
	}
	
	public String getColCifTom ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getTomador().getId().getCiftomador();
	}
	
	public String getColActivo ()
	{
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		String dev = "";
		if (co.getActivo() == '1')
		{
			dev = "Si";
		}
		else
		{
			dev = "No";
		}
		return dev;			
	}
	public String getColFechaCambio(){
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		String res = "";
		if (co.getFechacambio() != null){
			// MODIF TAM (16.01.2020) ** Inicio //
			// Formateamos fecha en formato 24 horas //
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// MODIF TAM (16.01.2020) ** Fin //
			Date fechaCambio = co.getFechacambio();
			res = sdf.format(fechaCambio);
		}
		return res;
	}
	
	public String getColUsuario(){
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getCodusuario();
	}
	
	
	public String getColFechaEfecto(){
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		String res = "";
		if (co.getFechaefecto() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaEfecto = co.getFechaefecto();
			res = sdf.format(fechaEfecto);
		}
		return res;
	}
	public String getColTipo(){
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		return co.getTipooperacion().toString();
	}
	
	public String getColenvIban(){
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		String descrEnvioIban = "";
		
		if (co.getEnvioIbanAgro() != null) {
			if (co.getEnvioIbanAgro().equals('O')){
				descrEnvioIban = "Dom. Agroseguro Obligatorio";
			}else if (co.getEnvioIbanAgro().equals('S')){
				descrEnvioIban = "Dom. Agroseguro Opcional";
			}else if (co.getEnvioIbanAgro() == 'N'){
				descrEnvioIban = "No domiciliar";
			}
		}else{
			descrEnvioIban = " ";
		}
		return descrEnvioIban;
	}
	
	public String getHistColectivoSelec()
	{			
		HistoricoColectivos co = (HistoricoColectivos) getCurrentRowObject();
		PageContext pageContext = (PageContext) getPageContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil();
		String baja = "";
		
		/* Si el tipo de operacion no es Alta y el perfil del usuario es 0, mostramos el boton de borrado */
		if (!(co.getTipooperacion().equals('A')) && (perfil.equals(Constants.PERFIL_USUARIO_ADMINISTRADOR))) {
			String id = "";

			if (co.getId() != null)
				id = StringUtils.forHTML(co.getId().toString());
			/* MODIF TAM (02.01.2020) */
			/* Se ha modificado y se lanza llamada a una nueva función para la baja del historico del colectivo */
			baja = "<a href=\"javascript:bajaColectivo('"+id+"')\">" +
					"<img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";
		}
	
		return baja;
	}
}
