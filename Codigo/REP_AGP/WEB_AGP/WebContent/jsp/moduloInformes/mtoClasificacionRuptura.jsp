<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Informes</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloInformes/clasificacionruptura.js"></script>
		<script type="text/javascript" src="jsp/moduloInformes/mtoInformesAjax.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript">
			$(document).ready(function(){
	       		$('#main3').validate({					
				
					errorLabelContainer: "#panelAlertasValidacion",
	   				wrapper: "li",
	   				
					rules: {
						
				 		"sentido":{required: true},
				 		"ruptura":{required: true},
				 		"listaCampo":{required: true}
				 		
				 		
				 	},
					messages: {
						
				 		"sentido":{required: "El campo Sentido es obligatorio"},
				 		"ruptura":{required: "El campo Ruptura es obligatorio"},
				 		"listaCampo":{required: "El campo Columna es obligatorio"}
				 		
				 	}
				});
				
			});
			function cargarFiltro(){
	
				<c:forEach items="${sessionScope.mtoConsultaClasificacionRuptura_LIMIT.filterSet.filters}" var="filtro">
				
					if ('${filtro.property}' == 'listaCampo'){
						var inputText = document.getElementById('listaCampo');
						inputText.value = '${filtro.value}';
					}
					else if ('${filtro.property}' == 'sentido'){
						var inputText = document.getElementById('sentido');
						inputText.value = '${filtro.value}';
					}
					else if ('${filtro.property}' == 'ruptura'){
						var inputText = document.getElementById('ruptura');
						inputText.value = '${filtro.value}';
					}
				
				</c:forEach>
			}
		</script>			
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub12','sub11');onLoad();cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la página -->
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
						<tr>
					<td width="5">&nbsp;</td>
					<td align="left">
						<a class="bot" id="datoinforme" href="javascript:datosInformes();">Datos informe</a>						
						<a class="bot" id="condiciones" href="javascript:condiciones();">Condiciones</a>
						<a class="bot" id="btnGenerar" href="javascript:ajaxCheckInforme();">Generar</a>
					</td>
					<td align="right">
						<a class="bot" id="btnModificar" href="javascript:modificar()">Modificar</a>
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
						<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
						<a class="bot" id="btnVolver"href="javascript:volver()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la pagina -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de clasificación y ruptura del informe </p>
	
		<form:form name="main3" id="main3" action="mtoClasificacionRuptura.run" method="post" commandName="vistaMtoinfClasificacionRuptura">								
			<input type="hidden" name="method" id="method" />
			<input type="hidden" name="redireccion" id="redireccion"/>
			<input type="hidden" name="nombre" id="nombre" value="${nombre}"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="idInforme" id="idInforme" value="${idInforme}"/>
			<input type="hidden" name="modificarValidCalculado" id="modificarValidCalculado" value="${modificarValidCalculado}"/>
			<input type="hidden" name="condicionesOk" id="condicionesOk" />
			<input type="hidden" name="recogerInformeSesion" id="recogerInformeSesion" value="${recogerInformeSesion}"/>
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt" style="width:90%;" id="divImportacion">
				 <fieldset>
					<legend class="literal">Clasificación y ruptura del informe : ${nombre} </legend>
						<table align="left" width="100%">		
							<tr>
								<td class="literal"  style="width:60px" valign="top">Columna</td>
								<td valign="top">
									<select id="listaCampo" name="listaCampo"" tabindex="1" class="dato" style="width:350px" onchange="javascript:listaCampoChange();" >
										<option value="" selected="selected">Todos</option>
										<c:forEach items="${listaCampo}" var="cp">
											<option value="${cp.permitidoOCalculado}-${cp.datoInformeId}">${cp.nombreVista} - ${cp.nombre}</option>
										</c:forEach>
									</select>	
								</td>
								<td class="literal" valign="top" style="width:60px">Sentido </td>
								<td valign="top">
									<form:select id="sentido" path="sentido" tabindex="2" cssClass="dato" cssStyle="width:110px" onchange="">
										<form:option value="">Todos</form:option>
										<form:option value="0">ASCENDENTE</form:option>
										<form:option value="1">DESCENDENTE</form:option>
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_sentido" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal" valign="top">Ruptura</td>
								<td valign="top">
									<form:select id="ruptura" path="ruptura" tabindex="3" cssClass="dato" cssStyle="width:110px" onchange="">
										<form:option value="">Todos</form:option>
										<form:option value="0">NO</form:option>
										<form:option value="1">SI</form:option>
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_ruptura" title="Campo obligatorio"> *</label>
								</td>
							</tr>
						</table>
				</fieldset>	
				
				
				<form:hidden path="idClasifRupt" id="id" />
				<form:hidden path="idinforme" id="informeid" />
				<form:hidden path="id.permitidocalculado" id="permitidOCalculado" />
				<form:hidden path="id.iddatoInforme" id="idDatoInforme" />
				
				
				
			</div>
			
		</form:form>
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${mtoConsultaClasificacionRuptura}		  							               
		</div> 	
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloInformes/popupFormatoInforme.jsp"%>
	
	</body>
</html>