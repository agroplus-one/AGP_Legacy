<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
	<title>Agroplus - Importaci&oacute;n</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/moduloTaller/importacionesBatch/cargasFicheros.js" ></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript">
		$(document).ready(function(){
			<c:if test="${modoConsulta eq 'true'}">
				$("#btnCargar").css("display", "none");
				$("#divImportacion").css("display", "none");
			</c:if>
		});

		function onInvokeAction(id) {
			var to=document.getElementById("adviceFilter");
			to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
			$.jmesa.setExportToLimit(id, '');
			var parameterString = $.jmesa.createParameterStringForLimit(id);
			$.get('${pageContext.request.contextPath}/cargasFicheros.run?origenLlamada='+$('#origenLlamada').val()+'&ajax=true&' + parameterString, function(data) {
				$("#grid").html(data)
			});
		}
		
		</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<a class="bot" id="btnCargar" href="javascript:cargar();">Cargar Ficheros</a>
							<a class="bot" id="btnSalir" href="javascript:salir();">Salir</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
			<p class="titulopag" align="left">Cargas de Condicionado</p>			
			<form:form name="main2" id="main2" action="cargasTablas.html" method="post">
				<input type="hidden" name="method" id="method2"/>
				<input type="hidden" name="idFichero" id ="idFichero"/>
				<input type="hidden" name="modoConsulta" id ="modoConsulta"/>
				
			</form:form>
			<form:form name="main1" id="main1" action="cargasFicheros.run" method="post" enctype="multipart/form-data" commandName="cargasFicherosBean">
				<input type="hidden" name="method" id="method1" />	
				<input type="hidden" name="idCondicionado" id ="idCondicionado" value="${idCondicionado}"/>
				<input type="hidden" name="origenLlamada" id ="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="idFichero" id ="idFichero"/>
				<input type="hidden" name="nombreFichero" id ="nombreFichero"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div class="panel2 isrt" style="width:90%;margin: 0 auto;" id="divImportacion">
					 <fieldset>
						<legend class="literal">Importación Ficheros</legend>
							<table align="center">
								<tr >
									<td  class="literal">Fichero Etiqueta </td>
									<td width="350px">
										<input type="file" class="dato" id="file" name="file" style="width: 400px"/>
										<label class="campoObligatorio" id="campoObligatorio_file" title="Campo obligatorio"> *</label>
									</td>
								</tr>
								
								<tr>
									<td  class="literal">Fichero ZIP</td>
									<td width="350px">
										<input type="file" class="dato" id="file2" name="file2" style="width: 400px"/>
										<label class="campoObligatorio" id="campoObligatorio_file2" title="Campo obligatorio"> *</label>
									</td>
								</tr>
								<td class="literal">Tipo</td>
								<td colspan="4">
									<select  class="dato" id="tipo" name="tipo">
										<option value="1">Organizador</option>
										<option value="2">Cond. General</option>	
										<option value="3">Cond. Específico</option>
									</select>
								</td>
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="plan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="1"/>
									<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">Línea</td>
								<td class="literal">
									<form:input path="linea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2"/>
									<label class="campoObligatorio" id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
								</td>
							</table>		
					   </fieldset>	
				</div>
			</form:form>
			
			<div id="grid" style="width: 90%;margin:0 auto;">
			  	${listadoCargasFicheros}		  							               
			</div> 	
		
		
		</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		

</body>
</html>