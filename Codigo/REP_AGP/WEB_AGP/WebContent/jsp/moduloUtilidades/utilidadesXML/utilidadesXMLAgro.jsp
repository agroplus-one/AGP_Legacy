<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
<head>
    
	<title>Generaci&oacute;n de XML</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	
	<script type="text/javascript" src="jsp/moduloUtilidades/utilidadesXML/utilidadesXMLAgro.js"></script>
	
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">

	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la pagina -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="66%">&nbsp;</td>
				<td width="33%" align="right"> 
					<a class="bot" id="btnConsultar" href="javascript:if($('#formXML').valid()){$('#formXML').submit();}" tabindex="3">Generar XML</a> 
				</td>
			</tr>
		</table>
	</div>
	
	<div class="conten" style="padding: 3px; width: 97%">
		
		<p class="titulopag" align="left">UTILIDADES XML</p>
	
		<!-- Form principal  -->
		<form id="formXML" name="formXML" action="utilidadesXML.run" method="post">
		
			<input type="hidden" name="method" id="method" value="doConsulta"/>
			<input type="hidden" name="codServicio" id="codServicio" value=""/>
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<fieldset class="panel2 isrt" style="width: auto">
				<legend class="literal">Generar XML</legend>
				<div style="width: 100%; float: left;">
					<div style="width: 100%;">
						<span> 
							<label for="idPoliza" class="literal">Solicitud</label>
							<input type=text" size="12" maxlength="15"  class="dato" id="idPoliza" name="idPoliza" tabindex="1" />
							<label class="campoObligatorio" id="campoObligatorio_idPoliza" title="Campo obligatorio">*</label>
						</span>
						<span>
							<label for="servicio" class="literal">Servicio</label>
							<select style="width:130px" class="dato" tabindex="2" onchange="$('#codServicio').val(this.value);">
								<option value="">--- Seleccione ---</option>
								<option value="CL">C&aacute;lculo</option>
								<option value="PD">Confirmaci&oacute;n</option>
								<option value="MC">M&oacute;dulos y Coberturas</option>
								<option value="VL">Validaci&oacute;n</option>
							</select>
							<label class="campoObligatorio" id="campoObligatorio_codServicio" title="Campo obligatorio">*</label>
						</span>
					</div>
			</fieldset>
			
			<fieldset class="panel2 isrt" style="width: auto">
				<legend class="literal">Resultado</legend>
				<textarea style="width:100%; height:350px; border:0px;">${xmlPoliza}</textarea>
			</fieldset>
		</form>
		
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
</body>
</html>