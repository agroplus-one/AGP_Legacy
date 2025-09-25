<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<!-- P0073325 - RQ.04, RQ.05, RQ.06, RQ.10, RQ.11 y RQ.12 -->
<script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaDocFirmada.js" ></script>

<!-- POPUP CARGA DOC FIRMADA  -->
		<form:form name="frmCargaDocFirmada" id="frmCargaDocFirmada" method="post" enctype="multipart/form-data" action="cargaDocFirmada.html" >
			<input type="hidden" name="idPoliza" id="idPoliza" value="" /> 
			<input type="hidden" name="origenPoliza" id="origenPoliza" value="" /> 
			
			<div id="divCargaDocFirmada" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top:170px;width:600px;">
				<div id="headerPopupImportarCsv" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
						<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" id="divTituloCargaDocFirmada" name="divTituloCargaDocFirmada">
							Cargar Documentaci&oacute;n
						</div>
						<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
							<span onclick="cerrarPopUpCargaDocFirmada()">x</span>
						</a>
				</div>
				<div class="panelInformacion_content">
						
						<div id="panelAlertasValidacionCargaDocFirmada" style="width:560px;color: black;border: 1px solid #DD3C10;
							display: block;font-size: 12px;font-style: italic;font-weight: bold;line-height: 20px;background-color: #FFEBE8;margin-left: auto;margin-right: auto;">
						</div>
						
						<div id="panelMensajeValidacionCargaDocFirmada" style="width:560px;color: black;border: 1px solid #FFCD00;
							display: none;font-size: 12px;font-style: italic;font-weight: bold;line-height: 20px;background-color: #FCF6CF;margin-left: auto;margin-right: auto;">
								Documentaci&oacute;n cargada correctamente
						</div>
						
						<!-- Detalles poliza -->
						<div id="infoPopUpDocFirmada"></div>
						
						<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
							<div style="display: inline;padding-right: 1em;"><span class="literal">Fichero a importar:</span></div>
							<input type="file" class="dato" id="file" name="file" onchange="javascript:limpiaPanelAlertasCargaDocFirmada();"/>
						</div>
						
						<div style="margin:10px auto;clear: both">
							    <a class="bot" id="btnImportarFirma" href="javascript:ajaxFileUpload()">Importar</a>
							    <a class="bot" id="btnCancelarFirma" href="javascript:cerrarPopUpCargaDocFirmada()">Cancelar</a>					    
						</div>
				</div>
			</div>
			
		</form:form>
