<%@ include file="/jsp/common/static/taglibs.jsp"%>

<%@ include file="/jsp/js/generales.jsp"%>
<fmt:setBundle basename="displaytag" />
<c:set var="numReg">
	<fmt:message key="numElementsPag" />
</c:set>


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
<head>
<title>Modulos Compatibles</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />


<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>


<script type="text/javascript">
			
			$(document).ready(function(){

 				SwitchMenu('sub1');
				$("#botonModificar").hide();
				
				$('#selectPlan').change(function() {
				
					$("#selectLinea").empty();
					$("#selectModuloPpal").empty();
					$("#selectModuloCompl").empty();
					$("#selectRiesgo").empty();
					
					cargarSelectLinea();
					
				});
				
				$('#selectLinea').change(function() {
				
					$("#selectModuloPpal").empty();
					$("#selectModuloCompl").empty();
					$("#selectRiesgo").empty();
					
					cargarSelectsModulos();
					
				});
				
				$('#selectModuloPpal').change(function() {
				
					$("#selectRiesgo").empty();
					
					cargarSelectRiesgo();
					
				});
				
				$('#main').validate({
					errorLabelContainer: "#panelAlertasValidacion",
					wrapper: "li",
					onfocusout: false,
   					highlight: function(element, errorClass) {
					 	$("#campoObligatorio_"+element.id).show();
  					},
  					unhighlight: function(element, errorClass) {
						$("#campoObligatorio_"+element.id).hide();
  					},
					rules: { 
						"linea.codplan": "required",
						"linea.lineaseguroid": "required",
						"moduloPrincipal.id.codmodulo": "required",
						"moduloComplementario.id.codmodulo": "required",
						"riesgoCubierto.id.codriesgocubierto": "required"
					},
					messages: {
						"linea.codplan": "El campo Plan es obligatorio",
						"linea.lineaseguroid": "El campo Línea es obligatorio",
						"moduloPrincipal.id.codmodulo": "El campo Módulo Principal es obligatorio",
						"moduloComplementario.id.codmodulo": "El campo Riesgo es obligatorio",
						"riesgoCubierto.id.codriesgocubierto": "El campo Módulo Complementario es obligatorio"
					}
				});
				
				<c:if test="${not empty moduloCompatibleCeBean.linea.codplan}">
					cargarSelectLinea('${moduloCompatibleCeBean.linea.lineaseguroid}','${moduloCompatibleCeBean.moduloPrincipal.id.codmodulo}','${moduloCompatibleCeBean.moduloComplementario.id.codmodulo}','${riesgoCubierto.id.codriesgocubierto}');
				</c:if>
				
				if($("#id").val() != ""){
					
					$('#accion').val("modificar");
					$('#botonAlta').hide();
					$('#botonModificar').show(); 				
				} 
			
			});
			
			function cargarSelectLinea(idLinea,idModuloPpal, idModuloComp, idRiesgo){
			
				var idPlan = $("#selectPlan").val();
				
				if(idPlan != ''){
			
					$.ajax({
				            url: "moduloCompatible.html",
				            data: "accion=getLineas_ajax&idPlan="+idPlan,
				            dataType: "json",
				            success: function(datos){
								$("#selectLinea").removeOption(/./);
								$("#selectLinea").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#selectLinea").addOption(value.value, value.nodeText);
								});
								
								$("#selectLinea").selectOptions(idLinea == undefined ? "" : String(idLinea));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_selectLinea").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_selectLinea").hide();
            					cargarSelectsModulos(idModuloPpal, idModuloComp, idRiesgo);
            				},
				            type: "POST"
						});
			
				}
			}
			
			function cargarSelectsModulos(idModuloPpal, idModuloComp, idRiesgo){
			
				var idLinea = $("#selectLinea").val();
				
				if(idLinea != ''){
					
					$.ajax({
				            url: "moduloCompatible.html",
				            data: "accion=getModulos_ajax&idLinea="+idLinea,
				            dataType: "json",
				            success: function(datos){
								$("#selectModuloPpal").removeOption(/./);
								$("#selectModuloCompl").removeOption(/./);
								$("#selectModuloPpal").addOption("","-- Seleccione una opción --");
								$("#selectModuloCompl").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
									if (value.ppalComp == 'P'){
				 						$("#selectModuloPpal").addOption(value.value, value.nodeText);
				 					}
				 					else{
				 						$("#selectModuloCompl").addOption(value.value, value.nodeText);
				 					}
								});
								
								$("#selectModuloPpal").selectOptions(idModuloPpal == undefined ? "" : String(idModuloPpal));
								$("#selectModuloCompl").selectOptions(idModuloComp == undefined ? "" : String(idModuloComp));
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_selectModuloPpal").show();
			            		$("#ajaxLoading_selectModuloCompl").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_selectModuloCompl").hide();
            					$("#ajaxLoading_selectModuloPpal").hide();
            					cargarSelectRiesgo(idRiesgo);
            				},
				            type: "POST"
						});
				}
			}
			
			function cargarSelectRiesgo(idRiesgo){
			
				var codModuloPpal = $("#selectModuloPpal").val();
				var idLinea = $("#selectLinea").val();
				
				if(idLinea != '' && codModuloPpal != '') {
					
					$.ajax({
				            url: "moduloCompatible.html",
				            data: "accion=getRiesgos_ajax&idLinea="+idLinea+"&codModulo="+codModuloPpal,
				            dataType: "json",
				            success: function(datos){
								$("#selectRiesgo").removeOption(/./);
								$("#selectRiesgo").addOption("","-- Seleccione una opción --");
								$.each(datos, function(index, value) { 
				 					$("#selectRiesgo").addOption(value.value, value.nodeText);
								});
								
								$("#selectRiesgo").selectOptions(idRiesgo == undefined ? "" : String(idRiesgo));
								
				            },
				            beforeSend: function(){
			            		$("#ajaxLoading_selectRiesgo").show();
            				},
            				complete: function(){
            					$("#ajaxLoading_selectRiesgo").hide();
            				},
				            type: "POST"
					});
				
				}
				
			}
			
			
			function limpiar(){
			
				$("#selectPlan").selectOptions("");
				$("#selectLinea").empty();
				$("#selectModuloPpal").empty();
				$("#selectModuloCompl").empty();
				$("#selectRiesgo").empty();
				consultar();
				
			}
			
			function consultar(){
				
				$('#id').val('');
				$('#accion').val("consultar");
				$("#main").validate().cancelSubmit = true;
				$('#main').submit();
			
			}
			
			function editar(idModuloCompatible){

				$('#id').val(idModuloCompatible);
				$("#botonAlta").hide();
				$("#panelAlertasValidacion").hide();
				$("#botonModificar").show();
				$('#accion').val("editarModuloCompatible_ajax");
				$("#panelInformacion").hide();
				$("#panelErrores").hide();
				$("label[class|='campoObligatorio']").hide();
				
				$.ajax({
			            url: "moduloCompatible.html",
			            data: "accion=editarModuloCompatible_ajax&idModuloCompatible="+idModuloCompatible,
			            dataType: "json",
			            success: function(datos){
			            	$('#id').val(datos.id);
			            	$("#selectPlan").selectOptions(""+datos.plan);
			            	cargarSelectLinea(datos.lineaseguroid,datos.codmoduloppal,datos.codmodulocompl,datos.codriesgocubierto);
			            	//cargarSelectsModulos();
			            	//cargarSelectRiesgo();
			            },
			            type: "POST"
					});
				
			
			}
			
			function modificar(){

		        $("#panelInformacion").hide();
		        
				$('#accion').val("modificar");
				$('#main').submit();			    

			}
			
			function eliminar(idModuloCompatible){

				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					
					$("#main").validate().cancelSubmit = true;
					$('#id').val(idModuloCompatible);
					$('#accion').val("eliminar");
					$('#main').submit();

				}
			
			}
			
			function alta(){

		        $("#panelInformacion").hide();
				$('#id').val('');
				$('#accion').val("alta");
				$('#main').submit();			    
				
			}			
			
</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="generales.fijarFila()">

		<%@ include file="../../common/static/cabecera.jsp"%>
		<%@ include file="../../common/static/menuLateralTaller.jsp"%>
		<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>


		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="botonAlta" href="javascript:alta();">Alta</a>
						<a class="bot" id="botonModificar" href="javascript:modificar();">Modificar</a>
						<a class="bot" href="javascript:consultar();">Consultar</a>
						<a class="bot" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Modulos Compatibles</p>
		<form:form name="main" id="main" action="moduloCompatible.html" method="post" commandName="moduloCompatibleCeBean">

			<input type="hidden" name="accion" id="accion" />
			<form:hidden path="id" id="id"/>
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div class="panel2 isrt">
				<table width="60%" align="center">
					<tr>
						<td class="literal" width="80px">Plan</td>
						<td align="left">
						    <form:select id="selectPlan" path="linea.codplan" cssClass="dato">
								<form:option value="">-- Seleccione una opción --</form:option>
	                            <c:forEach items="${listPlanes}" var="plan">
								    <form:option value="${plan}">${plan}</form:option> 
								</c:forEach>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectPlan" title="Campo obligatorio"> *</label>
						</td>
				    </tr>
					<tr> 
					    <td class="literal">L&iacute;nea</td>
				    	<td align="left">
							<form:select id="selectLinea" path="linea.lineaseguroid" cssClass="dato">
								<form:option value=""></form:option>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectLinea" title="Campo obligatorio"> *</label>
							<img id="ajaxLoading_selectLinea" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />	
						</td>				
					</tr>				
					<tr>
					    <td class="literal">Módulo Principal</td>
					    <td align="left">
							<form:select id="selectModuloPpal" path="moduloPrincipal.id.codmodulo" cssClass="dato">
								<form:option value=""></form:option>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectModuloPpal" title="Campo obligatorio"> *</label>	
							<img id="ajaxLoading_selectModuloPpal" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
						</td>				
					</tr>
					<tr>
					    <td class="literal">Riesgo</td>
					    <td align="left">
							<form:select id="selectRiesgo" path="riesgoCubierto.id.codriesgocubierto" cssClass="dato">
								<form:option value=""></form:option>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectRiesgo" title="Campo obligatorio"> *</label>
							<img id="ajaxLoading_selectRiesgo" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
						</td>					
					</tr>
					<tr>
					    <td class="literal">Módulo Complementario</td>
					    <td align="left">
							<form:select id="selectModuloCompl" path="moduloComplementario.id.codmodulo" cssClass="dato">
								<form:option value=""></form:option>
							</form:select><label class="campoObligatorio" id="campoObligatorio_selectModuloCompl" title="Campo obligatorio"> *</label>	
							<img id="ajaxLoading_selectModuloCompl" src="jsp/img/ajax-loading.gif" width="16px" style="cursor:hand;cursor:pointer;display:none" height="11px" />
						</td>				
					</tr>
				</table>	
				</div>
	</form:form>
		 <br/>
	<div class="grid" style="">
        <display:table requestURI="moduloCompatible.html" id="listModulosCompatibles" class="LISTA" summary="ModuloCompatible" name="${listModulosCompatibles}" sort="list" pagesize="10" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorModulosCompatibles" style="width:90%" excludedParams="accion" defaultsort="1">
            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="columnasAcciones" style="width:50px;text-align:center"/>
            <display:column class="literal" headerClass="cblistaImg" title="Plan" property="columnaPlan" sortable="true" style="width:50px;"/>
            <display:column class="literal" headerClass="cblistaImg" title="Línea" property="columnaLinea"  sortable="true" />
            <display:column class="literal" headerClass="cblistaImg" title="Mod. Principal" property="columnaModP" sortable="true"/>            
            <display:column class="literal" headerClass="cblistaImg" title="Riesgo" property="columnaRiesgo" sortable="true"/>
            <display:column class="literal" headerClass="cblistaImg" title="Mod. Complementario" property="columnaModC" sortable="true"/>
        </display:table>				
	</div>
<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>