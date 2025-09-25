package com.rsi.agp.core.decorators;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.managers.impl.anexoMod.impresion.ImpresionIncidenciasModBean;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;

public class ModelTableDecoratorImpresionIncidencias extends TableDecorator {
	
	
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getAcciones() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		
		//* MODIF TAM (20.08.2018) ** Inicio //
		// Obtenemos el perfil del usuario //
		PageContext pageContext = (PageContext) getPageContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil().substring(4);
		//* MODIF TAM (20.08.2018) ** Fin //		

		String funcion = this.generarFuncionJS(i);
		
		StringBuilder imprimir = new StringBuilder("<a href=\"#\"  onclick=\"javascript:imprimir('").append(i.getIdEnvio()).append("',")
				.append(i.getAnio()).append(",").append(i.getNumeroInc()).append(")\">")
				.append("<img src='jsp/img/displaytag/imprimir.png' alt='Imprimir' title='Imprimir'/></a>&nbsp;");
		StringBuilder verDC = new StringBuilder("<a href=\"#\"  onclick=\"javascript:verDC('").append(i.getIdEnvio()).append("')\">")
				.append("<img src='jsp/img/displaytag/reduccionCapital.png' alt='Ver distribuc&oacute;n de costes' title='Ver distribuc&oacute;n de costes'/></a>&nbsp;");
		StringBuilder aportarDocu = new StringBuilder("<a href=\"#\" onclick=\"").append(funcion).append("\">")
				.append("<img src='jsp/img/displaytag/imprimir_condiciones.png' alt='Aportar documentación' title='Aportar documentacion'/></a>&nbsp;");;
		StringBuilder resultado = new StringBuilder();
		//* MODIF TAM (20.08.2018) ** Inicio //
		//if("0".equals(perfil)){
			return resultado.append(imprimir).append(verDC).append(aportarDocu).toString();
		//}else{
		//	return resultado.append(imprimir).append(verDC).toString();
		//}
	}
		
	private String generarFuncionJS(ImpresionIncidenciasModBean bean){
		return new StringBuilder("aportarDocumentacion(")
				.append(bean.getAnio())
				.append(", '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getCodAsunto()))
				.append("', '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getAsunto()))
				.append("', '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getCodigoDocAfectado()))
				.append("', ")
				.append(bean.getNumeroInc())
				.append(", '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getEstado()))
				.append("', '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getIdEnvio()))
				.append("', '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getReferencia()))
				.append("', '")
				.append(org.apache.commons.lang.StringUtils.stripToEmpty(bean.getTipoPoliza()))
				.append("', ")
				.append(bean.getFechaEstado().getTime())
				.append(", ")
				.append(bean.getNumDocumentos())
				.append(", ")
				.append(bean.getNumeroInc())
				.append(")").toString();
	}
	
	public String getAnio() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		return StringUtils.nullToString(i.getAnio());
	}
	public String getNumero() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		return StringUtils.nullToString(i.getNumeroInc());
	}
	public String getAsunto() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		return StringUtils.nullToString(i.getAsunto());
	}
	public String getEstado (){
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		
		return StringUtils.nullToString(i.getDescEstado());
	}
	public String getFecha() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		if (i.getFechaEstado()!= null)
			return df.format(i.getFechaEstado());
		return "";
	}
	public String getDocumento() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		return StringUtils.nullToString(i.getDescDocAfectado());
	}
	public String getTipoPoliza() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		if (i.getTipoPoliza()!= null){
			if (i.getTipoPoliza().equalsIgnoreCase("P")) {
				return "Principal";
			}else if (i.getTipoPoliza().equalsIgnoreCase("C")){
				return "Complementaria";
			}else {
				return i.getTipoPoliza();
			}
		}                                                                                                                                             
		return "";
		
	}
	public String getidEnvio() {
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)getCurrentRowObject();
		return StringUtils.nullToString(i.getIdEnvio());
	}
	
	public String getReferencia(){
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)this.getCurrentRowObject();
		return StringUtils.nullToString(i.getReferencia());
	}
	
	public String getNumDocumentos(){
		ImpresionIncidenciasModBean i = (ImpresionIncidenciasModBean)this.getCurrentRowObject();
		return Integer.toString(i.getNumDocumentos());
	}
}
