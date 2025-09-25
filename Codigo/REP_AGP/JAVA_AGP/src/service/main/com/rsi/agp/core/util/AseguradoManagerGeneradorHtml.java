package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.ArrayUtils;

import com.rsi.agp.core.webapp.util.StringUtils;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.AseguradoNoSubvencionable;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Organismo;

import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.PorcentajeModulacionENESA;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ProximoOrganismo;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.ProximoSubvencion;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.SentenciaJudicial;
import es.agroseguro.seguroAgrario.aseguradoDatosYMedidas.Subvencion;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.AseguradoInfoSaldoENESA;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.SaldoAplicadoPoliza;
import es.agroseguro.seguroAgrario.aseguradoInfoSaldoENESA.SaldoTotal;

public class AseguradoManagerGeneradorHtml {
	
	
	private static String subtablaSentencias(final SentenciaJudicial[] sentencias) {
		StringBuilder subtabla = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		subtabla.append("<table class=\"subtabla-popup\"><thead>");
		subtabla.append("<tr>");
		subtabla.append("<th>Organismo</th>");
		subtabla.append("<th>Fecha Efecto</th>");
		subtabla.append("<th>Fecha Fin</th>");
		subtabla.append("</tr>");
		subtabla.append("</thead><tbody>");
		for(SentenciaJudicial sen : sentencias){
			subtabla.append("<tr>");
			subtabla.append("<td>").append(sen.getOrganismo()).append("</td>");
			subtabla.append("<td>").append(formatter.format(sen.getFechaEfectoSentencia().getTime())).append("</td>");	
			subtabla.append("<td>").append(formatter.format(sen.getFechaFinSentencia().getTime())).append("</td>");	

			subtabla.append("</tr>");
		}
		subtabla.append("</tbody>");
		subtabla.append("</tr></table>");
		return subtabla.toString();
	}
	
	
	public static String tablaSubvencionesAsegurado(final AseguradoNoSubvencionable aseguradoNoSubvencionable, final Organismo[] organismos, final ProximoOrganismo[] proximoOrganismos, final PorcentajeModulacionENESA porcentajeModulacionENESA, final boolean origenCarga){
		StringBuilder html = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		
		
		if (aseguradoNoSubvencionable != null) {
			html.append("<fieldset width=\"95%\">");
			html.append("<legend class=\"literal\">Asegurado no subvencionable </legend>");
			html.append("<table class=\"tabla-popup\" style=\"width:100%\"><thead>");
			html.append("<tr>");
			html.append("<th>Ente publico: " + aseguradoNoSubvencionable.getEntePublico() + " </th>");
			html.append("</tr>");
			html.append("</thead><tbody>");
			if(!ArrayUtils.isEmpty(proximoOrganismos)){
				html.append("<tr>");
				SentenciaJudicial[] sentencias = aseguradoNoSubvencionable.getSentenciaJudicialArray();
				if(!ArrayUtils.isEmpty(sentencias)){
					html.append("<tr><td class=\"detalle-subvencion\" align=\"center\" style=\"text-align:center;\">");
					html.append(subtablaSentencias(sentencias));
					html.append("</td></tr>");
				}
				html.append("</tr>");
				html.append("</tbody></table>");
				html.append("</fieldset>");
			}
			
		
		}
		
		
		html.append("<fieldset width=\"95%\">");
		html.append("<legend class=\"literal\">Control de acceso a subvenciones</legend>");
		html.append("<table class=\"tabla-popup\" style=\"width:100%\"><thead>");
		html.append("<tr>");
		html.append("<th>Organismo</th>");
		html.append("<th>Info. Adicional</th>"); 
		html.append("</tr>");
		html.append("</thead><tbody>");
		if(!ArrayUtils.isEmpty(proximoOrganismos)){
			for(Organismo org : organismos){				
				html.append("<tr>");
				String organismo = StringUtils.nullToString(org.getDescriptivoOrganismo());
				html.append("<td>").append(organismo).append("</td>");
				String infoAdicional = StringUtils.nullToString(org.getInfoAdicional());
				html.append("<td>").append(infoAdicional).append("</td>");

				html.append("</tr>");
				Subvencion[] subvenciones = org.getSubvencionArray();
				if(!ArrayUtils.isEmpty(subvenciones)){
					html.append("<tr><td colspan=3 class=\"detalle-subvencion\" align=\"center\" style=\"text-align:center;\">");
					html.append(subtablaSubvenciones(subvenciones, origenCarga));
					html.append("</td></tr>");
				}
			}
			html.append("</tbody></table>");
			html.append("</fieldset>");
		}
		
		

		
		html.append("<fieldset width=\"95%\">");
		html.append("<legend class=\"literal\">Próximo Control de acceso a subvenciones</legend>");
		html.append("<table class=\"tabla-popup\" style=\"width:100%\"><thead>");
		html.append("<tr>");
		html.append("<th>Organismo</th>");
		html.append("<th>Info. Adicional</th>"); 
		html.append("<th>Fecha efecto</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		if(!ArrayUtils.isEmpty(proximoOrganismos)){
			for(ProximoOrganismo org : proximoOrganismos){				
				html.append("<tr>");
				String organismo = StringUtils.nullToString(org.getDescriptivoOrganismo());
				html.append("<td>").append(organismo).append("</td>");
				String infoAdicional = StringUtils.nullToString(org.getInfoAdicional());
				html.append("<td>").append(infoAdicional).append("</td>");
				html.append("<td>").append(formatter.format(org.getFechaEfecto().getTime())).append("</td>");
				html.append("</tr>");
				ProximoSubvencion[] subvenciones = org.getSubvencionArray();
				if(!ArrayUtils.isEmpty(subvenciones)){
					html.append("<tr><td colspan=3 class=\"detalle-subvencion\" align=\"center\" style=\"text-align:center;\">");
					html.append(subtablaSubvenciones(subvenciones, origenCarga));
					html.append("</td></tr>");
				}
			}
			html.append("</tbody></table>");
			html.append("</fieldset>");
		}
		
		if (origenCarga && porcentajeModulacionENESA != null) {
			html.append(fragmentoModulacionEnesa(porcentajeModulacionENESA));
		}
		return html.toString();
	}
	
	private static String subtablaSubvenciones(final ProximoSubvencion[] subvenciones, final boolean origenCarga) {
		StringBuilder subtabla = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		subtabla.append("<table class=\"subtabla-popup\"><thead>");
		subtabla.append("<tr>");
	//	if (!origenCarga) {
			subtabla.append("<th>Gr.Seg / Linea</th>");
	//	}
		subtabla.append("<th>Subvenciones</th>");
		subtabla.append("<th>Fecha Efecto</th>");
	//	if (!origenCarga) {
			subtabla.append("<th>Permitida</th>");
		//}
		subtabla.append("</tr>");
		subtabla.append("</thead><tbody>");
		for(ProximoSubvencion subv : subvenciones){
			subtabla.append("<tr>");
			//if (!origenCarga) {
				String grupoSeguro = StringUtils.nullToString(subv.getCodigoGrupoSeguro());
				if (Constants.GRUPO_SEGURO_AGRICOLA.equals(grupoSeguro)) {
					grupoSeguro = "Agricola";
				} else if (Constants.GRUPO_SEGURO_GANADO.equals(grupoSeguro)) {
					grupoSeguro = "Ganado";
				}  else if (Constants.TODOS_CULTIVOS.equals(grupoSeguro)) {
					grupoSeguro = "Todos";
				} else if ("".equals(grupoSeguro)) {
					grupoSeguro = Constants.STRING_NA;
				}
				
				subtabla.append("<td>").append(grupoSeguro).append("</td>");	
		//	}
			String descripcionSubvencion = StringUtils.nullToString(subv.getDescriptivoSubvencion());
			subtabla.append("<td>").append(descripcionSubvencion).append("</td>");
			subtabla.append("<td>").append(formatter.format(subv.getFechaEfecto().getTime())).append("</td>");
		//	if (!origenCarga) {
				String permitida = StringUtils.nullToString(subv.getPermitida());
				if (permitida.equals("")) {
					permitida = "Si";
				}
				subtabla.append("<td>").append(permitida).append("</td>");
		//	}
			subtabla.append("</tr>");
		}
		subtabla.append("</tbody>");
		subtabla.append("</tr></table>");
		return subtabla.toString();
	}
	
	private static String subtablaSubvenciones(final Subvencion[] subvenciones, final boolean origenCarga) {
		StringBuilder subtabla = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		subtabla.append("<table class=\"subtabla-popup\"><thead>");
		subtabla.append("<tr>");
		//if (!origenCarga) {
			subtabla.append("<th>Gr.Seg / Linea</th>");
		//}
		subtabla.append("<th>Subvenciones</th>");
		//if (!origenCarga) {
			subtabla.append("<th>Permitida</th>");
		//}
		subtabla.append("</tr>");
		subtabla.append("</thead><tbody>");
		for(Subvencion subv : subvenciones){
			subtabla.append("<tr>");
		//	if (!origenCarga) {
				String grupoSeguro = StringUtils.nullToString(subv.getCodigoGrupoSeguro());
				if (Constants.GRUPO_SEGURO_AGRICOLA.equals(grupoSeguro)) {
					grupoSeguro = "Agricola";
				} else if (Constants.GRUPO_SEGURO_GANADO.equals(grupoSeguro)) {
					grupoSeguro = "Ganado";
				}  else if (Constants.TODOS_CULTIVOS.equals(grupoSeguro)) {
					grupoSeguro = "Todos";
				} else if ("".equals(grupoSeguro)) {
					grupoSeguro = Constants.STRING_NA;
				}

				
				subtabla.append("<td>").append(grupoSeguro).append("</td>");	
				
				
				
		//	}
			String descripcionSubvencion = StringUtils.nullToString(subv.getDescriptivoSubvencion());
			subtabla.append("<td>").append(descripcionSubvencion).append("</td>");
		//	if (!origenCarga) {
				String permitida = StringUtils.nullToString(subv.getPermitida());
				if (permitida.equals("")) {
					permitida = "Si";
				}
				subtabla.append("<td>").append(permitida).append("</td>");
		//	}
			subtabla.append("</tr>");
		}
		subtabla.append("</tbody>");
		subtabla.append("</tr></table>");
		return subtabla.toString();
	}
	
	
	public static String fragmentoControAccesoSubvencionesImportes(Organismo[] organismos, ProximoOrganismo[] proximoOrganismos){
		StringBuilder html = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		

		html.append("<fieldset width=\"95%\">");
		html.append("<legend class=\"literal\">Control de acceso a subvenciones</legend>");
		html.append("<table class=\"subtabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Organimos</th>");
		html.append("<th>Info. Adicional</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		for(Organismo org : organismos){
			html.append("<tr>");
			String descripcion = StringUtils.nullToString(org.getDescriptivoOrganismo());
			html.append("<td>").append(descripcion).append("</td>");
			String infoAdicional = StringUtils.nullToString(org.getInfoAdicional());
			html.append("<td>").append(infoAdicional).append("</td>");
			html.append("</tr>");
			Subvencion[] subvenciones = org.getSubvencionArray();
			if(!ArrayUtils.isEmpty(subvenciones)){
				html.append("<tr><td colspan=3 class=\"detalle-subvencion\" align=\"center\" style=\"text-align:center;\">");
				html.append(subtablaControAccesoSubvencionesImportes(subvenciones));
				html.append("</td></tr>");
			}
		}
		html.append("</tbody></table></fieldset>");
		
		
		html.append("<fieldset width=\"95%\">");
		html.append("<legend class=\"literal\">Próximo Control de acceso a subvenciones</legend>");
		html.append("<table class=\"subtabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Organimos</th>");
		html.append("<th>Info. Adicional</th>");
		html.append("<th>Fecha efecto</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		for(ProximoOrganismo org : proximoOrganismos){
			html.append("<tr>");
			String descripcion = StringUtils.nullToString(org.getDescriptivoOrganismo());
			html.append("<td>").append(descripcion).append("</td>");
			String infoAdicional = StringUtils.nullToString(org.getInfoAdicional());
			html.append("<td>").append(infoAdicional).append("</td>");
			html.append("<td>").append(formatter.format(org.getFechaEfecto().getTime())).append("</td>");
			html.append("</tr>");
			ProximoSubvencion[] subvenciones = org.getSubvencionArray();
			if(!ArrayUtils.isEmpty(subvenciones)){
				html.append("<tr><td colspan=3 class=\"detalle-subvencion\" align=\"center\" style=\"text-align:center;\">");
				html.append(subtablaControAccesoSubvencionesImportes(subvenciones));
				html.append("</td></tr>");
			}
		}
		html.append("</tbody></table></fieldset>");
		
		return html.toString();
	}
	
	private static String subtablaControAccesoSubvencionesImportes(ProximoSubvencion[] subvenciones){
		StringBuilder html = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		html.append("<table class=\"subtabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Subvención</th>");
		html.append("<th>Fecha efecto</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		for(ProximoSubvencion subv : subvenciones){
			html.append("<tr>");
			String descripcion = StringUtils.nullToString(subv.getDescriptivoSubvencion());
			html.append("<td>").append(descripcion).append("</td>");
			html.append("<td>").append(formatter.format(subv.getFechaEfecto().getTime())).append("</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html.toString();
	}
	
	private static String subtablaControAccesoSubvencionesImportes(Subvencion[] subvenciones){
		StringBuilder html = new StringBuilder();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		html.append("<table class=\"subtabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Subvención</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		for(Subvencion subv : subvenciones){
			html.append("<tr>");
			String descripcion = StringUtils.nullToString(subv.getDescriptivoSubvencion());
			html.append("<td>").append(descripcion).append("</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html.toString();
	}
	
	public static String fragmentoModulacionEnesa(PorcentajeModulacionENESA porcentajeModulacionENESA){
		StringBuilder html = new StringBuilder();
		DecimalFormat df = new DecimalFormat("#,###,##0.00#"); 
		html.append("<fieldset width=\"95%\" style=\"margin-top:10px;\">");
		html.append("<legend class=\"literal\">Modulación de ENESA</legend>");
		html.append("<table class=\"tabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Limite de subvención</th>");
		html.append("<th>% Modulación</th>");
		html.append("<th>Coeficiente Modulación</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		html.append("<tr>");
		BigDecimal limiteSubvenciona = StringUtils.nullToZero(porcentajeModulacionENESA.getLimiteSubvencion());
		html.append("<td>").append(df.format(limiteSubvenciona)).append("</td>");
		BigDecimal porcentaje = StringUtils.nullToZero(porcentajeModulacionENESA.getPorcentajeModulacion());
		html.append("<td>").append(df.format(porcentaje)).append("</td>");
		BigDecimal coeficiente = StringUtils.nullToZero(porcentajeModulacionENESA.getCoeficienteModulacion());
		html.append("<td>").append(df.format(coeficiente)).append("</td>");
		html.append("</tr>");
		html.append("</tbody></table></fieldset>");
		return html.toString();
	}
	
	public static String fragmentoSaldoReduccionEnesa(AseguradoInfoSaldoENESA aseguradoInfoSaldoENESA){
		StringBuilder html = new StringBuilder();
		
		if (null!=aseguradoInfoSaldoENESA) {
		
			DecimalFormat df = new DecimalFormat("#,###,##0.00#");
			SaldoTotal saldoTotal = aseguradoInfoSaldoENESA.getSaldoTotal();
			html.append("<fieldset width=\"95%\" style=\"margin-top:10px;\">");
			html.append("<legend class=\"literal\">Saldo de la reducción de ENESA</legend>");
			html.append("<div>");
			BigDecimal aplicado = StringUtils.nullToZero(saldoTotal.getAplicado());
			BigDecimal restante = StringUtils.nullToZero(saldoTotal.getRestante());
			html.append("<p class=\"detaldatoC\">");
			html.append("<span class=\"literal\">Aplicado: ").append("</span>").append(df.format(aplicado));
			html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			html.append("<span class=\"literal\">Restante: ").append("</span>").append(df.format(restante));
			html.append("</p>");
			html.append("</div>");
			SaldoAplicadoPoliza[] saldoAplicadoPolizas = aseguradoInfoSaldoENESA.getSaldoAplicadoPolizaArray();
			if(!ArrayUtils.isEmpty(saldoAplicadoPolizas)){
				html.append(subtablaSaldoReduccionENESA(saldoAplicadoPolizas));
			}
			html.append("</fieldset>"); 
		} 
		else {
			
			html.append("<fieldset width=\"95%\" style=\"margin-top:10px;\">");
			html.append("<legend class=\"literal\">Saldo de la reduccion de ENESA</legend>");
			html.append("<div>");
			html.append("<p class=\"detaldatoC\">");
			html.append("<span>No disponible").append("</span>");
			html.append("</p>");
			html.append("</div>");
			html.append("</fieldset>"); 
			
		}
		
		return html.toString();
	}
	
	private static String subtablaSaldoReduccionENESA(SaldoAplicadoPoliza[] saldoAplicadoPolizas){
		StringBuilder html = new StringBuilder();
		DecimalFormat df = new DecimalFormat("#,###,##0.00#");
		html.append("<table class=\"tabla-popup\"><thead>");
		html.append("<tr>");
		html.append("<th>Póliza</th>");
		html.append("<th>Linea</th>");
		html.append("<th>Importe</th>");
		html.append("</tr>");
		html.append("</thead><tbody>");
		for(SaldoAplicadoPoliza saldo : saldoAplicadoPolizas){
			html.append("<tr>");
			String referencia = StringUtils.nullToString(saldo.getReferencia());
			html.append("<td>").append(referencia).append("</td>");
			html.append("<td>").append(saldo.getLinea()).append("</td>");
			BigDecimal importe = StringUtils.nullToZero(saldo.getImporte());
			html.append("<td>").append(df.format(importe)).append("</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html.toString();
	}
}