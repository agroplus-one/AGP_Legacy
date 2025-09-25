<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numRegImpresion">
		<fmt:message key="impresionnumReg" />
	</c:set>
</fmt:bundle>

<html>
	<head>
		<title>Revision de Incidencias</title>

		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		
		<script>
			$(function(){
				$("#grid").displayTagAjax();
			});
			
			function consultar(){		
				var frm = document.getElementById('main3');
				frm.target="";		
				$('#method').val('doConsulta');
				$('#main3').submit();
			}

			function cargar(){
				var frm = document.getElementById('main3');
				frm.target="";
				$('#method').val('doCargar');
				$('#main3').submit();
			}
			
			function verificar(){
				var frm = document.getElementById('main3');
				frm.target="";
				$('#method').val('doVerificar');
				$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#main3').submit();
			}
			
			function limpiar(){
				$('#colectivo').val('');
				$('#linea').val('');
				$('#esMed_colectivo').val('');
				$('#oficina').val('');
				$('#poliza').val('');
				$('#estado').selectOptions('');
				$('#mensaje').val('');
				$('#refpoliza').val('');
				consultar();
			}
			
			function redirigir(numpagina){
				var frm = document.getElementById('main3');
				frm.target="";
				$('#method').val('doRedirigir');
				$('#pagina').val(numpagina);
				if (numpagina==5){ //colectivos
					if ((frm.colectivo.value != "")&& (frm.linea.value != "")){
						$('#main3').submit();
					}		 
					
				}else{
					$('#main3').submit();
				}
			}
			
			function imprimir(size)	{
				var frm = document.getElementById('main3');
				if(size < ${numRegImpresion }){
					frm.target="_blank";
				}				
				frm.method.value = 'doImprimir';
				frm.submit();		
			}
			
			function showMensajeError(mensaje){
				var arrayMensajes = mensaje.split("|");
		  	    var msj ="";
		  	    for(var i = 0; i < arrayMensajes.length; i++){
		  	     	msj += arrayMensajes[i] + "<br>";
		  	    }
			 	$('#mensajeError').html(msj);
		  	    $('#divMensajeError').fadeIn('normal');
		  	    $('#overlay').show();
		  	 }
			
			function cerrarPopUp(){
			     $('#divMensajeError').fadeOut('normal');
			     $('#overlay').hide();
			 }
			 
			function getTitulo(){
				var tipo = $("#tipofichero").val();				
				var titulo = "";
				if (tipo == 'I'){
					titulo = "Revisión de incidencias de impagados";
				}else if(tipo == 'R'){
					titulo = "Revisión de incidencias de reglamentos";
				}else if(tipo == 'C'){
					titulo = "Revisión de incidencias de comisiones";
				}else if(tipo == 'G'){
					titulo = "Revisión de incidencias de recibos emitidos";	
				}else{
					titulo = "Revisión de incidencias";
				}
				
				$("#titulo").html(titulo);
			}	
			
			function volver(){
				var tipo = $("#tipoFichero").val();
				$(window.location).attr('href', 'importacionComisiones.html?rand=' + UTIL.getRand() +'&tipo=' + tipo); 		
			}
			
			$(document).ready(function() {
				getTitulo();
				
				if ($('#estadoFichero').val() == 'Correcto' || $('#estadoFichero').val() == 'Aviso'){
					$('#btnCargar').attr('disabled',false);
				}else{
					$('#btnCargar').attr('disabled',true);
				}
				$('#main3').validate({
					errorLabelContainer: "#panelAlertasValidacion",
					onfocusout: function(element) {
			   			if(($('#method').val() == "doConsulta") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
							this.element(element);
						}
	   				},
	   				wrapper: "li",
					highlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).show();
	  			    },
	  				unhighlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).hide();
	  				},  	
					rules: {
						"linea.codlinea":{digits:true}					 	
					},
					messages: {
						"linea.codlinea":{digits: "El campo línea sólo puede contener dígitos"}					 	
					}				
				});
			});
			
			function modificar (refColectivo, linea, plan, esMed_fichero, oficina, refPoliza, estado, esMed_col)
			{
				if (refColectivo == 'null'){
					refColectivo = '';
				}
				$('#colectivo').val(refColectivo);
				$('#linea').val(linea);
				$('#plan').val(plan);
				$('#subentidad').val(esMed_fichero);
				$('#oficina').val(oficina);
				$('#refpoliza').val(refPoliza);
				$('#estado').val(estado);
				$('#esMed_colectivo').val(esMed_col);
				
			}
			
			function marcaCombo (combo, valor){
				for (var i=0; i<combo.length; i++){
					if (combo[i].value == valor){
						combo[i].selected = 'true';
					}					
				}
			}
			
		</script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub7', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	
	<form:form name="main3" id="main3" action="incidenciasMult.html" method="post" commandName="ficheroMultIncidenciaBean">
		<c:set var="ficheroTipo" value="${ficheroMultIncidenciaBean.ficheroMult.tipoFichero}"/>
		<c:set var="ficheroMult" value="${ficheroMultIncidenciaBean.ficheroMult}"/>
		<form:hidden path="ficheroMult.id" id="idFichero" />
		<form:hidden path="ficheroMult.tipoFichero" id="tipoFichero" />
		<input type="hidden" id="pagina" name="pagina" />
		<input type="hidden" id="method" name="method" />	
		<input type="hidden" id="estadoFichero" name ="estadoFichero" value="${estadoFichero}" />
		
			<div id="buttons"  style="padding-left:20px;">
				<table width="95%" cellspacing="0" cellpadding="0" border="0">
					<tr align="left">
						<td align="left">
							<a class="bot" id="btnVerificar" href="javascript:verificar();">Verificar Todos</a>
							
							<a class="bot" id="btnImprimir" href="javascript:imprimir(${totalListSize})">Imprimir</a> 
							<c:if test="${ficheroTipo!='G'}" >
								<a class="bot" id="btnCargar" href="javascript:openPopupAceptarDatos();">Aceptar Datos</a>
							</c:if>
						</td>
						<td align="center">		
							<c:if test="${plan<2015}" >
								<a class="bot" id="btnColectivo" href="javascript:redirigir(5);">Colectivos</a>
								<a class="bot" id="btnGGE" href="javascript:redirigir(1);">GGE</a> 
								<a class="bot" id="btnCC"  href="javascript:redirigir(2);">CC</a> 
								<a class="bot" id="btnReg" href="javascript:redirigir(3);">Reglam.</a> 
								<a class="bot" id="btnESMed" href="javascript:redirigir(4);">E-S Med.</a>
							</c:if>
							<c:if test="${plan>2014}" >
								<a class="bot" id="btnESMed" href="javascript:redirigir(4);">E-S Med.</a>
								<a class="bot" id="btnColectivo" href="javascript:redirigir(5);">Colectivos</a>
								<a class="bot" id="btnGGE" href="javascript:redirigir(6);">Param Grales.</a> 
								<a class="bot" id="btnCC"  href="javascript:redirigir(7);">Com E-S Med</a> 
								<a class="bot" id="btnReg" href="javascript:redirigir(8);">Descuentos</a>
							</c:if> 			
						</td>
						<td align="right">
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
							<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
							<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
						</td>
					</tr>
				</table>
			</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 97%">
			<p class="titulopag" align="left" id="titulo"></p>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt" style="width: 95%;">
				<fieldset><legend class="literal">Fichero</legend>
				<table align="center">
					<tr>
						<td class="literal">Fichero</td>
						<td width="100px">
							<form:input path="ficheroMult.nombreFichero" id="fichero" size="20" maxlength="12" cssClass="dato" readonly="true" />
						</td>
						<%-- <td class="literal">Fase</td>
						<td width="80px">
							<form:input path="ficheroMult.faseMults.fase" id="fase" size="5" maxlength="4" cssClass="dato" readonly="true" />
						</td> --%>
						<td class="literal">Fec. Carga</td>
						<td width="100px">
							<spring:bind path="ficheroMult.fechaCarga">
								<input type="text" name="ficheroMult.fechaCarga" id="fecha1" size="11" class="dato" readonly="true"
									value="<fmt:formatDate pattern="dd/MM/yyyy"	value="${ficheroMultIncidenciaBean.ficheroMult.fechaCarga}"/>" />
							</spring:bind>
						</td>
						<td class="literal">Fec. Aceptación</td>
						<td width="100px">
							<spring:bind path="ficheroMult.fechaAceptacion">
								<input type="text" name="ficheroMult.fechaAceptacion" id="fecha2" size="11" class="dato" readonly="true"
									value="<fmt:formatDate pattern="dd/MM/yyyy"	value="${ficheroMultIncidenciaBean.ficheroMult.fechaAceptacion}"/>" />
							</spring:bind>
						</td>
						<td class="literal">Estado</td>
						<td width="80px">
							<input type="text" id="estadoFichero" name="estadoFichero" size="10"  class="dato" readonly="true" value="${estadoFichero}"/>
						</td>
					</tr>
				</table>
				</fieldset>
			</div>
			
			<div class="panel2 isrt" style="width: 95%;">
			<fieldset><legend class="literal">Filtro</legend>
			<table align="center">
				<tr>
					<td class="literal">Colectivo</td>
					<td width="100px">
						<form:input path="idcolectivo" id="colectivo" size="10" maxlength="7" cssClass="dato" />
					</td>
					<td class="literal">Línea</td>
					<td width="100px">
						<form:input path="linea.codlinea" id="linea" size="5" maxlength="3" cssClass="dato" />
					</td>
					<td class="literal">E-S Med Col.</td>
					<td width="100px">
						<%-- <form:input path="subentidad" id="subentidad" size="8" maxlength="8" cssClass="dato" /> --%>
						<form:input path="esMedColectivo" id="esMed_colectivo" cssClass="dato"/>
					</td>
					<td class="literal">Oficina</td>
					<td width="100px">
						<form:input path="oficina" id="oficina"	size="5" maxlength="4" cssClass="dato" />
					</td>
				</tr>
				<tr>
				<c:if test="${ficheroTipo!='I'}" >
					<td class="literal">Póliza</td>
					<td width="100px">
						<form:input path="refpoliza" id="refpoliza" size="8" maxlength="7" cssClass="dato" onchange="this.value=this.value.toUpperCase();"/>
					</td>
				</c:if>
				<c:if test="${ficheroTipo=='I'}" >
					<form:hidden path="refpoliza" id="refpoliza" />
				</c:if>
					<td class="literal">Estado</td>
					<td width="100px">
						<form:select path="estado" id="estado" cssClass="dato">
							<form:option value="">-- Seleccione una opción --</form:option>				
							<form:option value="Erroneo">Erroneo</form:option>
							<form:option value="Aviso">Aviso</form:option>
							<form:option value="Correcto">Correcto</form:option>
						</form:select>
					</td>
					<td class="literal">Mensaje</td>
					<td width="250px" colspan="3">
						<form:input path="mensaje" id="mensaje" size="50" maxlength="40" cssClass="dato" />
					</td>
				</tr>
			</table>
			</fieldset>
		</div>
		
		<%@ include file="/jsp/moduloComisiones/popupAceptarDatos.jsp"%>
		
	</form:form>
	<div id="grid">
		<display:table requestURI="/incidenciasMult.html" class="LISTA" summary="incidenciasMult" pagesize="${numReg}" sort="list" defaultsort="1" defaultorder="descending"
			name="${listIncidencias}" id="incidenciasMult" excludedParams="method"
			decorator="com.rsi.agp.core.decorators.ModelTableDecoratorIncidenciasMult"
			style="width:95%;border-collapse:collapse;">
		
			<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="admActions" sortable="false" style="width:20px;text-align:left" />
			<display:column class="literal" headerClass="cblistaImg" title="Plan" property="plan" sortable="true" style="width:30px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Línea" property="linea" sortable="true" style="width:30px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Colectivo" property="colectivo" sortable="true"	style="width:70px;text-align:center" />
			<c:if test="${ficheroTipo!='I'}" >
				<display:column class="literal" headerClass="cblistaImg" title="Oficina" property="oficina" sortable="true" style="width:60px;text-align:center" comparator="com.rsi.agp.core.comparators.TableComparatorOficina"/>
				<display:column class="literal" headerClass="cblistaImg" title="Póliza" property="refPoliza" sortable="true" style="width:55px;text-align:center" />
			</c:if>
			<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estado" sortable="true" style="width:60px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="E-S Med Col" property="esCol" sortable="true" style="width:50px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Mensaje" property="mensaje" sortable="true" style="width:200px;text-align:center" />
		
		</display:table>

	</div>



<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	
	<div id="header-popup" style="padding:0.4em 1em;position:relative;
	   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
					              background:#525583;height:15px">
		<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		 	Errores
		</div>
		<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		          font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			<span onclick="cerrarPopUp()">x</span>
		</a>
	</div>
	
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
			<div id="mensajeError"></div>			
			<div style="margin-top:15px;clear: both">
				<a class="bot" href="javascript:cerrarPopUp()">Aceptar</a>				
			</div>
		</div>
	</div>
</div>

</body>
</html>