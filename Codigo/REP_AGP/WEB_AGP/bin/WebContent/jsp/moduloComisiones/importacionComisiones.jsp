<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Importacion ficheros</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
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
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		
		<script>	
		var mensajes = new Array();
			
			$(function(){
				$("#grid").displayTagAjax();
			});
		
		 	function cerrarPopUp(){

		 		var id 		= $('#idFichero').val();

		 		if(id==""){
		 			//Es porque ha habido un error
		 			consultar();
		 			
		 		}else{
		 			//Si ha devuelto id, es que se ha procesado correctamente
			 		var tipo 	= $("#tipo").val();
			 		var estadoFichero 	= $("#estadoFichero").val();
			 		//En realidad, tipo y estadoFichero no son relevantes
			 		revisar(id, tipo, estadoFichero);
		 		}
			 }
			 
			function cerrarPopUpError(){
			 	$('#progressbar_popup').fadeOut('normal');
				$('#overlay').hide();
			}
			
			function cerrarPopUpContenido(){
			    $('#divContenidoFichero').fadeOut('normal');
				$('#overlay').hide();
			}
		
			function ajaxFileUpload(){
				if ($('#main8').valid()){
					$('#progressbar_popup').fadeIn('normal');
					$('#overlay').show();
					showUploadProgress();
					$.ajaxFileUpload({
							url:'importacionComisiones.html?method=doCargar&tipo='+	$('#tipo').val() , 
							secureuri:false,
							fileElementId:'file',
							dataType: 'json',
							success: function (data, status){
								$('#idFichero').val(data.id);
							},
							error: function (data, status, e){
								
							}
					});
				}
			}  
			
			function showUploadProgress() {
				var uploadInfo = function getUploadInfo() {
					$.ajax({
						url: 'subidaInfo.html',
						type: "POST",
						cache: false,
						dataType: "json",
						async: false,
						success: function(msg){
							if(msg.result == 'UPLOADING') {
								var progress = msg.progress;
								$("#upload_bar").progressBar(progress);
							} else if(msg.result == 'DONE') {
								$("#upload_bar").progressBar(msg.progress);
								clearInterval(uploadInfoHandle);
								$("#uploadOK").show();
								$('#panelInformacionImportacion').show();
								$('#mensaje').hide();
								$('#divbot').show();
							} else if(msg.result == 'WARN') {
								$("#upload_bar").progressBar(msg.progress);
								clearInterval(uploadInfoHandle);
								$("#uploadOK").show();
								$('#panelInformacionImportacion').css("color","orange");
								$('#panelInformacionImportacion').html('Importación completada con errores');
								$('#panelInformacionImportacion').show();
								$('#mensaje').hide();
								$('#divbot').show();
							} else if(msg.result == 'FAILED'){
								$("#uploadKO").show();
								clearInterval(uploadInfoHandle);
								$('#panelAlertasImportacion').show();
								$('#mensaje').hide();
								$('#divbot').show();
							}else if(msg.result == 'DUPLICADO'){
								$("#uploadKO").show();
								clearInterval(uploadInfoHandle);
								$('#panelAlertasImportacion').html('Fichero importado duplicado');
								$('#panelAlertasImportacion').show();
								$('#mensaje').hide();
								$('#divbot').show();
							}
						}
					});
				};
				var uploadInfoHandle = setInterval(uploadInfo, 2000);
			}
		
			$(document).ready(function() {	
				$('#estado').val('${estado}');					
			
				$('#main8').validate({
					errorLabelContainer: "#panelAlertasValidacion",
	   				wrapper: "li",
					 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_" + element.id).show();
	  			     },
	  				 unhighlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).hide();
	  				 },  	
					 rules: {
					 	"file": {validarFile: true},
					 	"fechaCarga":{dateITA: true},
					 	"fechaEmision":{dateITA: true},
					 	"fechaAceptacion":{dateITA: true},
					 	"fechaCierre":{dateITA: true}
					 },
					 messages: {
					 	"file": {validarFile: "El nombre del fichero no puede tener más de 12 caracteres"},
					 	"fechaCarga":{ dateITA: "El formato del campo Fecha de Carga debe ser dd/mm/YYYY"},
					 	"fechaEmision":{dateITA: "El formato del campo Fecha de Emisión debe ser dd/mm/YYYY"},
					 	"fechaAceptacion":{dateITA: "El formato del campo Fecha de Aceptación debe ser dd/mm/YYYY"},
					 	"fechaCierre":{dateITA: "El formato del campo Fecha de Cierre debe ser dd/mm/YYYY"}
					 }
				
				});
				
				jQuery.validator.addMethod("validarFile", function(value, element, params) { 
					return $('.file').val().split('\\').pop().length <= 12;	
				});
			
				getTitulo();
				
				
				$("#upload_bar").show();
				$("#upload_bar").progressBar({barImage: 'jsp/img/progressbg_black.gif',boxImage: 'jsp/img/progressbar.gif', value: 0});
			
				var URL = UTIL.antiCacheRand(document.getElementById("main8").action);
				document.getElementById("main8").action = URL;  
				
				$("input[type=file]").filestyle({ 
     				image: "jsp/img/boton_examinar.png",
     				imageheight : 22,
     				imagewidth : 82,
     				width : 250
 				});
 				
				Zapatec.Calendar.setup({
				        firstDay          : 1,
				        weekNumbers       : false,
				        showOthers        : true,
				        showsTime         : false,
				        timeFormat        : "24",
				        step              : 2,
				        range             : [1900.01, 2999.12],
				        electric          : false,
				        singleClick       : true,
				        inputField        : "tx_fecha_carga",
				        button            : "btn_fecha_carga",
				        ifFormat          : "%d/%m/%Y",
				        daFormat          : "%d/%m/%Y",
				        align             : "Br"			        	        
			    });	
			    Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "tx_fecha_emision",
			        button            : "btn_fecha_emision",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		      	});
		   	 	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "tx_fecha_aceptacion",
			        button            : "btn_fecha_aceptacion",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		     	});
		    	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "tx_fecha_cierre",
			        button            : "btn_fecha_cierre",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"			        	        
		      	});
			});	
			
			 
			
			
			function getTitulo(){
				var tipo = $("#tipo").val();
				var titulo = "";
				if (tipo == 'I'){
					titulo = "Importacion ficheros de impagados";
				}else if(tipo == 'R'){
					titulo = "Importacion ficheros de reglamentos";
				}else if(tipo == 'C'){
					titulo = "Importacion ficheros de comisiones";
				}else if(tipo == 'G'){
					titulo = "Importacion ficheros de recibos emitidos";	
				}else if(tipo == 'D'){
					titulo = "Importacion ficheros de deuda aplazada";
				}
				else{
					titulo = "Importacion ficheros";
				}
				
				$("#titulo").html(titulo);
			}
			
			function limpiar(){
				$('#nomFichero').val('');
				$('#tx_fecha_carga').val('');
				$('#tx_fecha_emision').val('');
				$('#tx_fecha_aceptacion').val('');
				$('#tx_fecha_cierre').val('');
				$('#estado').selectOptions('');
				$('#idFichero').val('');
				$('#sl_codigoSituacion').selectOptions('');
				$('#plan').val('');
				$('#fase').val('');
				$('#limpiar').val('limpiar');
				consultar();
			}
			
			function consultar(){
				
				var frm = document.getElementById('main8');
				frm.target="";
				$("#main8").validate().cancelSubmit = false;
				$('#method').val('doConsulta');
				frm.limpiar.value = "limpiar";
				$('#main8').submit();
				
				
			}
			
			function borrar(id){
				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					$('#method').val('doBorrarFichero');
					$('#idFichero').val(id);
					$.blockUI.defaults.message = '<h4> Eliminando el fichero seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
					$('#main8').submit();
				}
			}
			
			function revisar(id,tipo,estadoFichero){
				$('#revisar_method').val('doConsulta');
				$('#revisar_idFichero').val(id);
				$('#revisar_tipoFichero').val(tipo);
				$('#estadoFichero').val(estadoFichero);
				$('#revisarForm').submit();				
			}
			function revisarDeuda(id,tipo,estadoFichero){
				$('#revisar_method').val('doConsultaDeuda');
				$('#revisar_idFichero').val(id);
				$('#revisar_tipoFichero').val(tipo);
				$('#estadoFichero').val(estadoFichero);
				$('#revisarForm').submit();				
			}
			
			function descargar(id){
				$('#verFich_method').val('doDescargarFichero');
				$('#verFich_idFichero').val(id);
				$('#verFich').submit();				
			}
			
			function descargarDeuda(id){
				$('#verFich_method').val('doDescargarFicheroDeuda');
				$('#verFich_idFichero').val(id);
				$('#verFich').submit();				
			}
			
			function ver(id, tipo, numFase, estado,nombre){
			
				var frm = document.getElementById('main8');
				frm.method.value="doVerFichero"	
				frm.idFichero.value = id;
				frm.tipo.value = tipo;
				frm.numFase.value = numFase;
				frm.estadoF.value = estado;
				frm.nombreF.value = nombre;
				$('#main8').submit();	
			}
			
		</script>
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub7', 'sub5');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnCargar" href="javascript:ajaxFileUpload();">Cargar fichero</a>
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left" id="titulo"></p>
			
			<form:form name="main8" id="main8" action="importacionComisiones.html" method="post" enctype="multipart/form-data" commandName="ffcb">			
				<input type="hidden" id="method" name="method"/>
				<input type="hidden" id="idFichero" name="idFichero"/>
				<input type="hidden" id="tipo" name="tipo" value="${tipo }"/>
				<input type="hidden" id="estadoF" name="estadoF"/>
				<input type="hidden" id="nombreF" name="nombreF"/>
				<input type="hidden" id="limpiar" name="limpiar"/>
				<input type="hidden" id="numFase" name="numFase"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
				
				<div style="panel2 isrt">
					<fieldset style="width:95%;margin:0 auto;">
						<legend class="literal">Importación</legend>
						<table align="center">
							<tr>
								<td  class="literal">Fichero a importar&nbsp;&nbsp;</td>
								<td width="350px">
									<input type="file" class="dato" id="file" name="file" style="width: 400px"/>
									<label class="campoObligatorio" id="campoObligatorio_file" title="Campo obligatorio"> *</label>
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset style="width:95%;margin:0 auto;">
						<legend class="literal">Consulta</legend>
						<table align="center">
							<tr>
								<td class="literal">Fichero</td>
								<td colspan="3">
									<form:input cssClass="dato"	id="nomFichero" size="40" path="nombreFichero"/>
								</td>
								<td class="literal">Estado</td>
								<td>
									<select  class="dato" id="estado" name="estado">
										<option value="">Todos</option>
										<option value="Correcto">Correcto</option>
										<option value="Aviso">Aviso</option>
										<option value="Erroneo">Erroneo</option>
										<option value="Cargado">Cargado</option>
									</select>
								</td>
								<c:if test="${tipo!='D'}" >
									<td class="literal">Ejercicio</td>
									<td>
										<form:input cssClass="dato" id="plan" size="4" maxlength="4" path="plan"/>
									</td>
									<td class="literal">Fase </td>
									<td>
										<form:input cssClass="dato" id="fase" size="4" maxlength="4" path="fase"/>
									</td>
								</c:if>
							</tr>
							<tr>
								<td class="literal">Fec. Carga</td>
								<td>
									<spring:bind path="fechaCarga">
										<input type="text" name="fechaCarga" id="tx_fecha_carga" size="8" maxlength="10" class="dato" 
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${ffcb.fechaCarga}"/>"/>
									</spring:bind>
									<input type="button" id="btn_fecha_carga" name="btn_fecha_carga" class="miniCalendario" style="cursor: pointer;"/>
								</td>
								<td class="literal">Fec. Emisión</td>
								<td>
									<spring:bind path="fechaEmision">
										<input type="text" name="fechaEmision"  id="tx_fecha_emision" size="8" maxlength="10" class="dato"
											value="<fmt:formatDate pattern="dd/MM/yyyy" value="${ffcb.fechaEmision}"/>"/>
									</spring:bind>
									<input type="button" id="btn_fecha_emision" name="btn_fecha_emision" class="miniCalendario" style="cursor: pointer;"/>
								</td>
								<c:if test="${tipo!='G'}" >
									<td class="literal">Fec. Aceptación</td>
									<td>
										<spring:bind path="fechaAceptacion">
											<input type="text" name="fechaAceptacion"  id="tx_fecha_aceptacion" size="8" maxlength="10" class="dato"
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${ffcb.fechaAceptacion}"/>"/>
										</spring:bind>
										<input type="button" id="btn_fecha_aceptacion" name="btn_fecha_aceptacion" class="miniCalendario" style="cursor: pointer;"/>
									</td>
									<td class="literal">Fec. Cierre</td>
									<td>
										<spring:bind path="fechaCierre">
											<input type="text" name="fechaCierre"  id="tx_fecha_cierre" size="8" maxlength="10" class="dato"
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${ffcb.fechaCierre}"/>"/>
										</spring:bind>
										<input type="button" id="btn_fecha_cierre" name="btn_fecha_cierre" class="miniCalendario" style="cursor: pointer;"/>
									</td>
								</c:if>
								<c:if test="${tipo=='G'}" >
									<td colspan="4" >&nbsp;
										<input type="hidden" id="btn_fecha_aceptacion" name="btn_fecha_aceptacion" class="miniCalendario" style="cursor: pointer;"/>
										<input type="hidden" id="btn_fecha_cierre" name="btn_fecha_cierre" class="miniCalendario" style="cursor: pointer;"/>
									</td>
								</c:if>
								<c:if test="${tipo=='R'}" >
									<td class="literal">Situación</td>
									<td>
										<form:select  id="sl_codigoSituacion"  path="reglamentoProduccionEmitidaSituacion.codigo" cssClass="dato">
											<form:option value="">--Seleccione una opción--</form:option>
											<form:option value="0">Regularizada</form:option>
											<form:option value="1">En Fase</form:option>
										</form:select>
									</td>
								</c:if>
							</tr>
						</table>
					</fieldset>	
				</div>
			</form:form>
			
			<div id="grid">
				<c:if test="${tipo=='C'||tipo=='R'||tipo=='G'||tipo=='I'}" >
					<display:table requestURI="" class="LISTA" summary="importacionComisiones"
						pagesize="${numReg}" name="${listImportaciones }" id="importacionComisiones"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorImportacionComisiones" excludedParams="method limpiar"
						style="width:80%;border-collapse:collapse;" sort="list" defaultsort="4" defaultorder="descending">
						
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="admActions" style="width:90px;text-align:center" sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fichero" property="fichero" style="text-align:center" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estado" style="text-align:center" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fec. Carga" property="feccarga" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
						<c:if test="${tipo=='C'||tipo=='R'}" >
							<display:column class="literal" headerClass="cblistaImg" title="Fec. Emi. Agros." property="fecemit" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
						</c:if>
						<c:if test="${tipo!='G'}" >
							<display:column class="literal" headerClass="cblistaImg" title="Fec. Acept" property="fecacep" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
							<display:column class="literal" headerClass="cblistaImg" title="Fec. Cierre" property="feccierre" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
						</c:if>
						<c:if test="${tipo=='R'}" >
							<display:column class="literal" headerClass="cblistaImg" title="Tipo Situación" property="codigosituacion" style="text-align:center" sortable="true"/>
						</c:if>
						<display:column class="literal" headerClass="cblistaImg" title="Fase" property="fase.fase" style="text-align:center" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Ejercicio" property="fase.plan" style="text-align:center" sortable="true"/>
					</display:table>
				</c:if>
				<c:if test="${tipo=='D'}" >
					<display:table requestURI="" class="LISTA" summary="importacionComisiones"
						pagesize="${numReg}" name="${listImportacionesDeuda }" id="importacionComisionesDeuda"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorImportacionComisionesDeuda" excludedParams="method limpiar"
						style="width:80%;border-collapse:collapse;" sort="list" defaultsort="4" defaultorder="descending">
						
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="admActions" style="width:90px;text-align:center" sortable="false"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fichero" property="fichero" style="text-align:center" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estado" style="text-align:center" sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fec. Carga" property="feccarga" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fec. Aceptación" property="fechaAceptacion" style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
					</display:table>
				</c:if>
			</div>
			
			
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
	    <!-- Popup de la barra de progreso -->
	    <div id="progressbar_popup" class="wrapper_popup">
		    <div class="header-popup">
		        <div class="title_popup">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer;display:none" id="divbot">
		            <span onclick="cerrarPopUp()">x</span>
		        </a>
		         <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer;display:none" id="boterr">
		            <span onclick="cerrarPopUpError()">x</span>
		        </a>
		    </div>
		    <div id="panelInformacionImportacion" class="literal" style="color:green;display:none;text-align: center;">
					Importación completada con exito
			</div>
			<div id="panelAlertasImportacion" class="literal" style="color:red;display:none;text-align: center;">
					Se ha producido un error durante la importación
			</div>
			<div class="content_popup">
			    <table style="margin: 0 auto;">
			        <tr>
			            <td class="literal" id="mensaje">Espere un momento, por favor...</td>
			            <td>
			            	<table>
			            		<tr>
			            			<td>
									<div id="upload_bar"></div>
									</td>
									<td>
										<img src="jsp/img/displaytag/accept.png" style="display: none" id="uploadOK" />
										<img src="jsp/img/displaytag/cancel.png" style="display: none" id="uploadKO" />
									</td>
			            		</tr>
			            	</table>
			            </td>
			        </tr>
			    </table>
			</div>
		</div>
		
		
		<form name="revisarForm" id="revisarForm" action="incidencias.html">
			<input type="hidden" id="revisar_idFichero" name="idFichero" />
			<input type="hidden" id="revisar_tipoFichero" name="tipo" />
			<input type="hidden" id="revisar_method" name="method"/>
			<input type="hidden" id="estadoFichero" name="estadoFichero"/>
		</form>
		<form name="verFich" id="verFich" action="importacionComisiones.html" target="_blank" >
			<input type="hidden" id="verFich_idFichero" name="idFichero" />
			<input type="hidden" id="verFich_method" name="method"/>
	    </form>	
	    

	<div id="divContenidoFichero" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
							              background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">	
							Contenido del Fichero
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
							<span onclick="cerrarPopUpContenido()">x</span>
				</a>
			</div>
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="contenidoFichero" />			
				</div>
			</div>
	</div>	
	
		
	</body>
</html>