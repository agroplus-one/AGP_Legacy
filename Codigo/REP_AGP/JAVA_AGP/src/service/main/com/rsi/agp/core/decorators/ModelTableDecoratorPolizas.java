package com.rsi.agp.core.decorators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.ArrayUtils;
import org.displaytag.decorator.TableDecorator;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.PagoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;

public class ModelTableDecoratorPolizas extends TableDecorator {	
	
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER = "<a style=\"cursor: hand;\" onclick=\"javascript:ver('";
	private static final String IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION_PNG_ALT_VER = "')\"/><img src=\"jsp/img/displaytag/information.png\" alt=\"Ver\" title=\"Ver\"/>";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_EDITAR_POL_CPL = "<a style=\"cursor: hand;\" onclick=\"javascript:editarPolCpl('";
	private static final String IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR = "')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>&nbsp;";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER_DETALLE_POLIZA = "<a style=\"cursor: hand;\" onclick=\"javascript:verDetallePolizaCpl('";
	private static final String IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION = "')\"><img src='jsp/img/displaytag/information.png' alt='Detalle P&oacute;liza' title='Detalle P&oacute;liza'/></a>";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_SEGUIMIENTO_POLIZA = "<a style=\"cursor: hand;\" onclick=\"javascript:seguimientoPolizaJs('";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK = "<a style=\"cursor: hand;\" onclick=\"javascript:accion('anexoModificacion','";
	private static final String IMG_SRC_JSP_IMG_DISPLAYTAG_ANEXO_MODIFICACION = "')\"><img src='jsp/img/displaytag/anexoModificacion.png' alt='Anexo de Modificaci&oacute;n' title='Anexo de Modificaci&oacute;n'/></a>&nbsp;";
	private static final String IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR_TITLE_EDITAR_A = "')\"><img src=\"jsp/img/displaytag/edit.png\" alt=\"Editar\" title=\"Editar\"/></a>";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_EDITAR = "<a style=\"cursor: hand;\" onclick=\"javascript:editar('";
	private static final String A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_ELIMINAR = "<a style=\"cursor: hand;\" onclick=\"javascript:eliminar(";
	private static final String IMG_ELIMINAR = ")\"><img src='jsp/img/displaytag/delete.png' alt='Eliminar' title='Eliminar'/></a>&nbsp;";
	
	final ResourceBundle utl_bundle = ResourceBundle.getBundle("agp_list_polizas");

	public String getPolSelec() { // utilidades de poliza
		StringBuffer sb = new StringBuffer("");
		PageContext pageContext = (PageContext) getPageContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		String perfil = usuario.getPerfil().substring(4);
		Poliza po = (Poliza)getCurrentRowObject();
		Boolean esLineaGanado = po.getLinea().isLineaGanado();
		if(!Constants.PAC_PROCESO_CARGA.equals(po.getPacCargada())) {
			String estadoPol = po.getEstadoPoliza() == null ? "" : po.getEstadoPoliza().getIdestado().toString();
			// ELIMINAR
			if (mustRenderItem("eliminar", perfil, estadoPol)) {
				sb.append(A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_ELIMINAR + po.getIdpoliza() + IMG_ELIMINAR);
			}
			// EDITAR
			if (mustRenderItem("editar", perfil, estadoPol)) {
				if (Constants.MODULO_POLIZA_PRINCIPAL.equals(po.getTipoReferencia())) {
					sb.append(A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_EDITAR + po.getIdpoliza() + "','" + estadoPol + "','" + po.getTipoReferencia() + IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR_TITLE_EDITAR_A);
				} else {
					sb.append(A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_EDITAR_POL_CPL + po.getIdpoliza() + "','" + estadoPol + IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR);
				}
			}
			
			// SINIESTRO: Solamente se renderiza esta opcion si se trata de una poliza principal
			if (!esLineaGanado && Constants.MODULO_POLIZA_PRINCIPAL.equals(po.getTipoReferencia()) && mustRenderItem("siniestro", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('siniestros','" + po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/siniestro.png' alt='Siniestros' title='Siniestros'/></a>&nbsp;");
			}
			
			/* SINIESTRO
			if (!esLineaGanado && mustRenderItem("siniestro", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('siniestros','" + po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/siniestro.png' alt='Siniestros' title='Siniestros'/></a>&nbsp;");
			}*/
			
			
			/* P0043417 ** MODIF TAM (06.09.2021) ** Inicio */
			// SINIESTRO GANADO
			if (esLineaGanado && mustRenderItem("siniestroGan", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('siniestrosGan','"+po.getIdpoliza().toString()+"')\"><img src='jsp/img/displaytag/siniestro.png' alt='Siniestros Ganado' title='Siniestros Ganado'/></a>&nbsp;");
			}
			/* P0043417 ** MODIF TAM (06.09.2021) ** Fin */
			
			// ANEXO MODIFICACION
			if (mustRenderItem("anexo", perfil, estadoPol)) {
				sb.append(A_STYLE_CURSOR_HAND_ONCLICK + po.getIdpoliza() + IMG_SRC_JSP_IMG_DISPLAYTAG_ANEXO_MODIFICACION);
			}
			// REDUCCION CAPITAL
			if (!esLineaGanado && Constants.MODULO_POLIZA_PRINCIPAL.equals(po.getTipoReferencia()) && mustRenderItem("reduccion", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('reduccionCapital','" +po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/reduccionCapital.png' alt='Reducci&oacute;n de Capital' title='Reducci&oacute;n de Capital'/></a>&nbsp;");
			}
			// SEGUIMIENTO
			if (mustRenderItem("seguimiento", perfil, estadoPol)) {
				sb.append(A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_SEGUIMIENTO_POLIZA + po.getIdpoliza() + "')\"><img src='jsp/img/magnifier.png' alt='Estado poliza' title='Estado poliza'/></a>&nbsp;");
			}
			// IMPRIMIR
			if (mustRenderItem("imprimir", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:imprimirInforme('" + po.getIdpoliza() + "','" + po.getTipoReferencia() + "','" + estadoPol + "','" + po.getReferencia() + "','" + po.getLinea().getCodplan() + "','" + (esLineaGanado ? Constants.TipoLinea.GAN : Constants.TipoLinea.AGR) + "','" + po.getRenovableSn() + "')\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir p&oacute;liza' title='Imprimir p&oacute;liza'/></a>&nbsp;");
			}
			// ACUSE RECIBO
			if (mustRenderItem("acuse", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('verAcuseRecibo','" + po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo'/></a>&nbsp;");		
			}
			// ANULAR
			if (mustRenderItem("anular", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:accion('anular','" + po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/cancel.png' alt='Anular p&oacute;liza' title='Anular p&oacute;liza'/></a>&nbsp;");
			}
			// CONSULTAR
			if (mustRenderItem("consultar", perfil, estadoPol)) {
				if (Constants.MODULO_POLIZA_PRINCIPAL.equals(po.getTipoReferencia())) {
					sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:verDetallePoliza('" + po.getIdpoliza() + "','" + esLineaGanado + IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION);
				} else {
					sb.append(A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER_DETALLE_POLIZA + po.getIdpoliza() + IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION);
				}
			}
			// PORTAL MEDIADORES
			if (mustRenderItem("portal", perfil, estadoPol)) {
				sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:irPortalMediadorPoliza('" +po.getReferencia() + "')\"><img src='jsp/img/displaytag/portalMediador.png' alt='Portal Mediadores' title='Portal Mediadores'/></a>&nbsp;");
			}
			if ((po.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 0
					|| po.getLinea().getCodplan().compareTo(Constants.PLAN_2015) == 1)
					&& po.getPolizaPctComisiones() != null) {				
				String descuentoRecargoTipo = (po.getColectivo().gettipoDescRecarg() != null)
						? po.getColectivo().gettipoDescRecarg().toString()
						: "-1";
				String descuentoRecargoPct = (po.getColectivo().getpctDescRecarg() != null)
						? po.getColectivo().getpctDescRecarg().toString()
						: "0";
				String pctEntidadCom = (po.getPolizaPctComisiones().getPctentidad() != null)
						? po.getPolizaPctComisiones().getPctentidad().toString()
						: "-1";
				String pctESMedCom = (po.getPolizaPctComisiones().getPctesmediadora() != null)
						? po.getPolizaPctComisiones().getPctesmediadora().toString()
						: "-1";
				BigDecimal pctDescuentoElegido = po.getPolizaPctComisiones().getPctdescelegido();
				BigDecimal pctRecargoElegido = po.getPolizaPctComisiones().getPctrecarelegido();
				BigDecimal pctElegido = new BigDecimal("0.00");
				BigDecimal ceroBigDecimal = new BigDecimal("0.00");
				// Suponemos que no pueden ser los dos diferentes de null o cero
				if (pctDescuentoElegido != null && pctDescuentoElegido.compareTo(ceroBigDecimal) != 0) {
					pctElegido = pctDescuentoElegido.negate();
				} else {
					if (pctRecargoElegido != null && pctRecargoElegido.compareTo(ceroBigDecimal) != 0) {
						pctElegido = pctRecargoElegido;
					}
				}		
				sb.insert(0, "<input type=\"checkbox\" id=\"checkParcela_" + po.getIdpoliza() + "\"  name=\"checkParcela_" + po.getIdpoliza() + "\" onClick=\"onClickInCheck2('checkParcela_" + po.getIdpoliza() + "');\" value=\""+ po.getIdpoliza() + "#" + po.getEstadoPoliza().getIdestado() + "#" + po.getAsegurado().getNombreCompleto()+ "#" + po.getAsegurado().getNifcif() + "#" + po.getColectivo().getTomador().getId().getCodentidad() + "#" + po.getColectivo().getSubentidadMediadora().getId().getCodsubentidad() + "#" + esLineaGanado + "#" + po.getTipoReferencia().toString()+ "#" + po.getLinea().getCodplan() + "#" + po.getPolizaPctComisiones().getPctcommax() + "#" + po.getPolizaPctComisiones().getPctadministracion() + "#" + po.getPolizaPctComisiones().getPctadquisicion()+ "#" +po.getLinea().getLineaseguroid() + "#" + pctEntidadCom+ "#" + pctESMedCom + "#" + descuentoRecargoTipo + "#" + descuentoRecargoPct + "#" + pctElegido + "#" + po.getColectivo().getSubentidadMediadora().getId().getCodentidad() + "#|\"/>");					
			} else {
				sb.insert(0, "<input type=\"checkbox\" id=\"checkParcela_" + po.getIdpoliza() + "\"  name=\"checkParcela_" + po.getIdpoliza() + "\" onClick=\"onClickInCheck2('checkParcela_" + po.getIdpoliza() + "');\" value=\""+ po.getIdpoliza() + "#" + po.getEstadoPoliza().getIdestado() + "#" + po.getAsegurado().getNombreCompleto()+ "#" + po.getAsegurado().getNifcif() + "#" + po.getColectivo().getTomador().getId().getCodentidad() + "#" + po.getColectivo().getSubentidadMediadora().getId().getCodsubentidad() + "#" + esLineaGanado + "#" + po.getTipoReferencia().toString()+ "#" + po.getLinea().getCodplan() + "#0#0#0#0#0#-1#0#0#0#" + po.getColectivo().getSubentidadMediadora().getId().getCodentidad() + "#|\"/>");
			}
			/*  P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
			// Cargar documentación
			if (mustRenderItem("cargadoc", perfil, estadoPol)) {
				Long canal = po.getGedDocPoliza().getCanalFirma().getIdCanal();
				if(Constants.ESTADO_POLIZA_DEFINITIVA.equals(new BigDecimal(estadoPol)) && ("N".equals(po.getGedDocPoliza().getDocFirmada().toString()) || 
						( "S".equals(po.getGedDocPoliza().getDocFirmada().toString()) && (Constants.CANAL_FIRMA_PAPEL.equals(canal) || Constants.CANAL_FIRMA_PDTE.equals(canal)))))  {
					sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:cargaDocFirmada('"+ po.getIdpoliza() + "','" + po.getReferencia() + "','" + po.getLinea().getCodplan() + "','" + po.getTipoReferencia() + "','2')\"><img src='jsp/img/jmesa/pdf.gif' alt='Cargar documentaci&oacute;n' title='Cargar documentaci&oacute;n'/></a>&nbsp;");
				}					
			}
			/*   P73325 - RQ.04, RQ.05 y RQ.06  Inicio */
		} else {
			sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:alertaCargaPac()\"/><img src=\"jsp/img/error_red.png\" alt=\"Cargando Parcelas PAC\" title=\"Cargando Parcelas PAC\"/>");			
		}
		if (this.muestraBotonRecibo(po, po.getEstadoPoliza().getIdestado()) == true) {
			sb.append("<a style=\"cursor: hand;\" onclick=\"javascript:cambia_cursor(); accion('recibos','" + po.getIdpoliza() + "')\"><img src='jsp/img/displaytag/report.png' alt='Consultar recibos' title='Consultar recibos'/></a>&nbsp;");
		}

		return sb.toString();
	}
	
	private Boolean mustRenderItem(final String accion, final String perfil, final String estadoPol) {
		Hashtable<String, String[]> cfg = getActionCfg(accion);
		return cfg != null
				&& (ArrayUtils.contains(cfg.get("perfil"), "999") || ArrayUtils.contains(cfg.get("perfil"), perfil))
				&& (ArrayUtils.contains(cfg.get("estado"), "99") || ArrayUtils.contains(cfg.get("estado"), estadoPol));
	}
	
	private Hashtable<String, String[]> getActionCfg(final String accion) {
		Hashtable<String, String[]> result = null;
		String cfg = utl_bundle.getString(accion + ".cfg");
		if (!StringUtils.isNullOrEmpty(cfg)) {
			result = new Hashtable<String, String[]>(3);
			String[] aux = cfg.split("\\|");
			result.put("estado", aux[0].split(";"));
			result.put("perfil", aux[1].split(";"));
			result.put("externo", aux[2].split(";"));
		}
		return result;
	}
	
	private boolean muestraBotonRecibo(Poliza pol, BigDecimal estado) {
		boolean res = false;
		if (new Character('S').equals(pol.getRenovableSn())) {
			res = true;
		} else {
			if (Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR.equals(estado)
					|| Constants.ESTADO_POLIZA_DEFINITIVA.equals(estado)
					|| Constants.ESTADO_POLIZA_ANULADA.equals(estado)) {
				res = true;
			}
			if (!Constants.PAC_PROCESO_CARGA.equals(pol.getPacCargada())) {
				if (Constants.ESTADO_POLIZA_EMITIDA.equals(estado)) {
					res = true;
				}
			}
		}
		return res;
	}	
	
	public String getCargaPolSelec() { // seleccion de poliza
		Poliza po = (Poliza)getCurrentRowObject();
		String codentidad = "";
		String codusuario = "";
		String codplan = "";
		String idcolectivo = "";
		String codlinea = "";
		String oficina = "";
	    String codmodulo = "";
	    String nifcif = "";
	    BigDecimal estado = new BigDecimal(0);
	    
		if(null != po.getColectivo().getTomador().getId().getCodentidad())
			codentidad = po.getColectivo().getTomador().getId().getCodentidad().toString();
		if(null != po.getUsuario() 
				&& null != po.getUsuario())
			codusuario	= po.getUsuario().getCodusuario();
					 
		if(null != po.getColectivo() 
				&& null != po.getColectivo().getIdcolectivo())
			idcolectivo	= po.getColectivo().getIdcolectivo().toString();
		if(null != po.getColectivo() 
				&& null != po.getColectivo().getLinea() 
				&& null != po.getColectivo().getLinea().getCodplan())
			codplan = po.getColectivo().getLinea().getCodplan().toString();
		if(null != po.getColectivo()
				&& null != po.getColectivo().getLinea()
				&& null != po.getColectivo().getLinea().getCodlinea())
			codlinea = po.getColectivo().getLinea().getCodlinea().toString();
		if(null != po.getOficina())	
			oficina	= po.getOficina();
		if(null != po.getCodmodulo())	
			codmodulo = po.getCodmodulo().toString();
		if(null != po.getAsegurado() 
				&& null != po.getAsegurado().getNifcif())	
			nifcif = po.getAsegurado().getNifcif().toString();
		if(null != po.getEstadoPoliza())			
			estado = po.getEstadoPoliza().getIdestado();
		
		String edit = "";	
		String borrar = "";
		String modif = "";
		String ver = "";
		String imprimir = "";
		String verAcuseRecibo = "";
		String cargaPac = "";
		
		boolean esLineaGanado = po.getLinea().isLineaGanado();

		Constants.TipoLinea tipoLinea;
		if(esLineaGanado){
			tipoLinea = Constants.TipoLinea.GAN;			
		}else{
			tipoLinea = Constants.TipoLinea.AGR;
		}
		
		//DAA 11/07/2013 Si la carga de la pag esta en proceso no se puede hacer ninguna accion
		 if(!Constants.PAC_PROCESO_CARGA.equals(po.getPacCargada())){
			
			if(estado.equals(Constants.ESTADO_POLIZA_DEFINITIVA)){
				
				if(po.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL) ){
					ver = A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER+po.getIdpoliza()+IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION_PNG_ALT_VER;					
				}else{
					ver =  "<a style=\"cursor: hand;\" onclick=\"javascript:verCpl('"+po.getIdpoliza()+"')\"/><img src=\"jsp/img/displaytag/information.png\" alt=\"Ver\" title=\"Ver\" />";
				}
				imprimir = getImprimirSel(po,tipoLinea);
				
			}
		    if(estado.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)){
				if(!esLineaGanado){
					if(po.getTipoReferencia().equals(Constants.MODULO_POLIZA_PRINCIPAL)){
						ver = A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER+po.getIdpoliza()+IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION_PNG_ALT_VER;						
					}else{
						ver =  "<a style=\"cursor: hand;\" onclick=\"javascript:verCpl('"+po.getIdpoliza()+"')\"/><img src=\"jsp/img/displaytag/information.png\" alt=\"Ver\" title=\"Ver\" />";	
					}
				}
			}
			
			if(estado.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)  || estado.equals(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA)){
				imprimir = getImprimirSel(po,tipoLinea);		
			}
			else if(po.getTipoReferencia().equals(Constants.MODULO_POLIZA_COMPLEMENTARIO) && estado.equals(Constants.ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR)){
				imprimir = getImprimirSel(po,tipoLinea);		
			}
			
			modif = "<a style=\"cursor: hand;\" onclick=\"javascript:modificar('"+po.getIdpoliza()+"', '"+codentidad+"','"+oficina+"',"+
					"'"+codusuario+"','"+codplan+"','"+codlinea+"','"+idcolectivo+"','"+
					codmodulo+"','"+nifcif+"','"+StringUtils.nullToString(po.getReferencia())+"','"+this.getPolNombreAseg()+"','"+estado+"','"+po.getTipoReferencia()+"')\">"
					+"<img src=\"jsp/img/magnifier.png\" alt=\"Consultar\" title=\"Consultar\"/></a>&nbsp;";
			
			if((estado.equals(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION) || estado.equals(Constants.ESTADO_POLIZA_GRABACION_PROVISIONAL)) && !Constants.POLIZA_PAGADA.equals(po.getEstadoPagoAgp().getId())){
				borrar = "<a style=\"cursor: hand;\" onclick=\"javascript:borrar('"+po.getIdpoliza()+"')\"><img src=\"jsp/img/displaytag/delete.png\" alt=\"Borrar\" title=\"Borrar\"/></a>&nbsp;";				
			}
			
			// estados de ganado donde no aparece el boton'11-Borrador precartera', '15-Rescindida', '17-Con gastos sin renovacion', '18-Precartera precalculada', '19-Precartera generada' 
			if(estado.compareTo(Constants.ESTADO_POLIZA_PENDIENTE_VALIDACION) == 1) {
				if (!estado.toString().equals(Constants.ESTADO_POLIZA_BORRADOR_PRECARTERA.toString()) && !estado.toString().equals(Constants.ESTADO_POLIZA_RESCINDIDA.toString()) 
					&& !estado.toString().equals(Constants.ESTADO_POLIZA_CON_GASTOS_SIN_RENOVACION.toString()) && !estado.toString().equals(Constants.ESTADO_POLIZA_PRECARTERA_PRECALCULADA.toString()) 
					&& !estado.toString().equals(Constants.ESTADO_POLIZA_PRECARTERA_GENERADA.toString()))
					imprimir = getImprimirSel(po,tipoLinea);
			}			
			
			// MPM - 09-05-12
			// Si la poliza esta en defintiva y pagada, no se muestra el boton de editar
			if(Constants.ESTADO_POLIZA_GRABACION_DEFINITIVA.equals(estado) && Constants.POLIZA_PAGADA.equals(po.getEstadoPagoAgp().getId())) {
				edit = "";
			}
			else {		
				if(Constants.MODULO_POLIZA_PRINCIPAL.equals(po.getTipoReferencia())){
					if( (estado.intValue() > 3 && estado.intValue() != 7) || (po.getColectivo().getLinea().getActivo().equals(Constants.LINEA_ACTIVA_NO) || (po.getColectivo().getLinea().getActivo().equals(Constants.LINEA_ACTIVA_BLOQUEADA)) || po.getColectivo().getActivo().toString().equals("0"))){
						edit = "";
					}else{
						edit = "<a  style=\"cursor: hand;\"onclick=\"javascript:editar('"+po.getIdpoliza()+"','"+estado+"','"+po.getTipoReferencia()+IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR;
					}
				}else if(Constants.MODULO_POLIZA_COMPLEMENTARIO.equals(po.getTipoReferencia())){
					
					if( (estado.intValue() > 3 && estado.intValue() != 7) || (po.getColectivo().getLinea().getActivo().equals(Constants.LINEA_ACTIVA_NO) || (po.getColectivo().getLinea().getActivo().equals(Constants.LINEA_ACTIVA_BLOQUEADA)) || po.getColectivo().getActivo().toString().equals("0"))){
						edit = "";
					}else{
						edit = A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_EDITAR_POL_CPL+po.getIdpoliza()+"','"+estado+IMG_SRC_JSP_IMG_DISPLAYTAG_EDIT_PNG_ALT_EDITAR;
					}
					
				}
			}		
			
			// si el estado es 7 Enviada Erronea se muestra el boton de Ver Acuse de Recibo
			if(Constants.ESTADO_POLIZA_ENVIADA_ERRONEA.equals(estado)) {
				verAcuseRecibo = "<a style=\"cursor: hand;\" onclick=\"javascript:verAcuseRecibo("
						+ po.getIdpoliza()
						+ ")\"><img src='jsp/img/displaytag/acuserecibo.png' alt='Consultar Acuse Recibo' title='Consultar Acuse Recibo'/></a>&nbsp;";	
			}
		}else{
			cargaPac = "<a  onclick=\"javascript:alertaCargaPac()\"/><img src=\"jsp/img/error_red.png\" alt=\"Cargando Parcelas PAC\" title=\"Cargando Parcelas PAC\"/>";
			
		}
		
		if(esLineaGanado) {//mismo codigo que el metodo muestraSoloLecturaUtilidades cuando es ganado
			if (new Character('S').equals(po.getRenovableSn())){
				if (Constants.ESTADO_POLIZA_PRIMERA_COMUNICACION.equals(estado) || 
						Constants.ESTADO_POLIZA_COMUNICACION_DEFINITIVA.equals(estado) ||
						Constants.ESTADO_POLIZA_EMITIDA.equals(estado))
					ver = A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER+po.getIdpoliza()+IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION_PNG_ALT_VER;
				else{
					ver="";
				}
			}else{
				ver = A_STYLE_CURSOR_HAND_ONCLICK_JAVASCRIPT_VER+po.getIdpoliza()+IMG_SRC_JSP_IMG_DISPLAYTAG_INFORMATION_PNG_ALT_VER;
			}
		}
		
		return  ver + modif + edit + borrar + imprimir + verAcuseRecibo + cargaPac;		
    }
	
	public String getPolEntidad() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getTomador().getEntidad().getCodentidad().toString();
	}
	
	public String getColectivo_tomador_id_codentidad() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getTomador().getEntidad().getCodentidad().toString();
	}
	
	public String getPolOficina() {
		String oficina = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getOficina() != null)
			oficina = po.getOficina();
		return oficina;
	}
	
	public String getOficina() {
		String oficina = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getOficina() != null)
			oficina = po.getOficina();
		return oficina;
	}
	
	public String getPolUsuario () {
		String usuario = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getUsuario() != null)
		{
			usuario = po.getUsuario().getCodusuario();
		}
		return usuario;
	}
	
	public String getPolPlan() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getLinea().getCodplan().toString();
	}
	
	public String getPolLinea() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getLinea().getCodlinea().toString();
	}
	
	public String getPolColectivo() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getColectivo().getIdcolectivo() + "-" + po.getColectivo().getDc();
	}
	
	public String getPolPoliza() {
		Poliza po = (Poliza)getCurrentRowObject();
		//return StringUtils.nullToString(po.getReferencia());
		if((!"".equals(StringUtils.nullToString(po.getReferencia()))) && (!"".equals(StringUtils.nullToString(po.getDc())))){
			return StringUtils.nullToString(po.getReferencia()) + "-" + po.getDc();
		} else {
			return StringUtils.nullToString(po.getReferencia());
		}
	}
	
	public String getImporte() {
		String importe = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getImporte() != null){
			importe = StringUtils.nullToString(po.getImporte());
		}else{
			try {
				for (PagoPoliza pago : po.getPagoPolizas()) {
					importe = StringUtils.nullToString(pago.getImporte());
				}
			} catch (Exception e) {
				importe = "";
			}
		}	
		return importe;
	}
	
	public String getPolModulo() {
		String modulo = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getCodmodulo() != null)
			modulo = po.getCodmodulo();
		return modulo;
	}
	
	public String getPolCifNif() {
		Poliza po = (Poliza)getCurrentRowObject();
		return po.getAsegurado().getNifcif();
	}
	
	public String getPolNombreAseg() {
		Poliza po = (Poliza)getCurrentRowObject();
		String nombre = "";
		if(po.getAsegurado().getTipoidentificacion().equals("CIF"))
			nombre = po.getAsegurado().getRazonsocial();
		else
			nombre = po.getAsegurado().getNombre()+" "+po.getAsegurado().getApellido1()+" "+po.getAsegurado().getApellido2();
		return nombre;
	}
	
	public String getPolEstado() {
		Poliza po = (Poliza)getCurrentRowObject();
		
		String estado = "";		
		if(po.getEstadoPoliza() != null)
		{
			//estado = estados.get(po.getEstado());
			estado = po.getEstadoPoliza().getDescEstado();
		}
		
		return estado;
	}
	public String getPolStr(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		if(po.getTienesiniestros().equals('S'))
			res = "S";
		
		return res;
	}
	public String getPolRc(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		if(po.getTieneanexorc().equals('S'))
			res = "S";
				
		return res;
	}
	public String getPolMOD(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		if(po.getTieneanexomp().equals('S'))
			res = "S";
				
		return res;
	}
	public String getPolTipoRef(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if(po.getTipoReferencia() != null) {
			if(po.getTipoReferencia().equals('P')){
				res = "PRINCIPAL";
			}else{
				res = "COMPLEMENTARIA";
			}
		}
		return res;
	}

	public String getPolFechaEnvio() {
		String res = "";
		Poliza po = (Poliza) getCurrentRowObject();
		Date fechaEnvio = po.getFechaenvio();
		if (fechaEnvio != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			res = sdf.format(fechaEnvio);
		}
		return res;
	}
	
	public String getPolFechaPago(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "";
		if(po.getEstadoPagoAgp().getId().equals(Constants.POLIZA_PAGADA) && po.getFechaPago()!= null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaPago = po.getFechaPago();
			res = sdf.format(fechaPago);
		}
		return res;
	}
	
	public String getPolRnv(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		
		if(new Character('S').equals(po.getRenovableSn())) {
			res = "S";}
		return res;
	}	
	
	public String getPolClase() {
		String res = "";
		Poliza po = (Poliza)getCurrentRowObject();
		if (po.getClase() != null) {
			res=po.getClase().toString();
		}		
		return res;
	}
	
	public String getPg() {
		Poliza po = (Poliza)getCurrentRowObject();
		if(po.getEstadoPagoAgp()!= null) {
			return "<span title=\"" + StringUtils.nullToString(po.getDescEstadoPago()) + 
					"\">" + po.getEstadoPagoAgp().getAbreviado() + "</span>";
		}
		else
			return Constants.POLIZA_NO_PAGADA_TXT;
	}
	
	public String getPolEsFinanciada(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		if(po.getEsFinanciada()!=null)res=po.getEsFinanciada().toString();
		return res;
		
	}
	public String getPolEsRyD(){
		Poliza po = (Poliza)getCurrentRowObject();
		String res = "N";
		if(po.getEsRyD()!=null)res=po.getEsRyD().toString();
		return res;
	}
	
	public Long getPolNSolicitud() {
		Poliza po = (Poliza)getCurrentRowObject();
		
		Long poliza = 0L;		
		if(po.getIdpoliza() != null)
		{
			poliza = po.getIdpoliza();
		}
		return poliza;
	}
	
	
	public String getImprimirSel(Poliza po,Constants.TipoLinea tipoLinea){ // para seleccion poliza
		String imprimir = "<a href=\"javascript:imprimirInforme('"+po.getIdpoliza()+"','"+po.getTipoReferencia()+"','"+po.getEstadoPoliza().getIdestado().toString()+"','"+po.getReferencia()+"','"+po.getLinea().getCodplan().toString()+"','"+tipoLinea+"','"+po.getRenovableSn()+"')\"><img src='jsp/img/displaytag/imprimir.png' alt='Imprimir p&oacute;liza' title='Imprimir p&oacute;liza'/></a>";
		return imprimir;
	}
}