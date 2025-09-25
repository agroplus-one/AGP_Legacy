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
<title>Grupo de Asegurados</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/moduloTaller/condicionesEspeciales/grupoAsegurado.js"></script>

	<script type="text/javascript">
	
			$(document).ready(function(){
				
				$('#main').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
					 wrapper: "li",
					 invalidHandler: function(form) {
						//$("label[class|='campoObligatorio']").show();
					 },
					 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_"+element.id).show();
  					 },
  					 unhighlight: function(element, errorClass) {
					 	$("#campoObligatorio_"+element.id).hide();
  					 },
					 onfocusout: false,
					 rules: {
					 	codgrupoaseg: {
					 		required: true 
					 	},
					 	bonifrecprimas: {
					 		required: true, 
   						 	digits: true
					 	},
					 	bonifrecrdtomax: {
					 		required: true, 
   						 	digits: true
					 	}
					 },
					 messages: {
					 	codgrupoaseg: {
					 		required: "El campo Código Grupo es obligatorio"
					 	},
					 	bonifrecprimas: {
							required: "El campo Bonificación/ Recargo Primas es obligatorio",
   					 	  	digits: "El campo Bonificación/ Recargo Primas sólo puede contener dígitos"					 	
   					 	}, 	
					 	bonifrecrdtomax: {
  					 	  	required: "El campo Bonificación/ Recargo Rendimiento Máximo es obligatorio",
   					 	  	digits: "El campo Bonificación/ Recargo Rendimiento Máximo sólo puede contener dígitos"   					 	   
 					 	}
					 }
					 
				});
								
				<c:if test="${activarModoModificar == 'true'}">					
					$('#operacion').val("modificar");
					$('#btnAlta').hide();
					$('#btnModificar').show(); 				
				</c:if> 	
			
			});
			
			
			
			function editar(codGrupoaseg, bonifRecprimas, bonifRecrdtomax)
			{
				var frm = document.getElementById('main');
				frm.codgrupoaseg.value = codGrupoaseg;
				frm.bonifrecprimas.value = bonifRecprimas;
				frm.bonifrecrdtomax.value = bonifRecrdtomax;
				$("#btnAlta").hide();
				$("#btnModificar").show();
				$("label[class|='campoObligatorio']").hide();
				$("#panelAlertasValidacion").hide();
				$("#codgrupoaseg").attr("readonly", true);				
			}

			function enviar(operacion) 
			{
				generales.enviar(operacion,'codgrupoaseg','bonifrecprimas','bonifrecrdtomax');
			}
			
			function mayusculas(objeto){
				objeto.value = objeto.value.toUpperCase();
			}
			
			function eliminar(idGrupoAsegurado){

				if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
					$("#main").validate().cancelSubmit = true;
					$('#codgrupoaseg').val(idGrupoAsegurado);
					$('#operacion').val("baja");
					$('#main').submit();
				}
			
			}
			
			function consultar(){
			
				
				$("#main").validate().cancelSubmit = true;
				$('#operacion').val("consultar");
				$('#main').submit();			
			}
			
			function modificar(){

		        $("#panelInformacion").hide();		        
				$('#operacion').val("modificar");
				$('#main').submit();	
			}
			
			function limpiar(){
			
				$("#codgrupoaseg").val("");
				$("#bonifrecprimas").val("");
				$("#bonifrecrdtomax").val("");
				consultar();
			}
			
			function alta(){
			
		        $("#panelInformacion").hide();		        
				$('#operacion').val("alta");
				$('#main').submit();			
			}
			
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub1');generales.fijarFila()">

<%@ include file="../../common/static/cabecera.jsp"%>
<%@ include file="../../common/static/menuLateralTaller.jsp"%>
<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>


	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
					<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>							
	
		
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">grupo Asegurado</p>
		<form:form name="main" id="main" action="grupoAsegurado.html" method="post" commandName="grupoAseguradoBean">
			<input type="hidden" name="operacion" id="operacion"/>
	
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<form:errors path="*" cssClass="error" />
			
			<div class="panel2 isrt" >
				<fieldset>
					<table width="60%">
						<tr align="left">
							<td class="literal">Código Grupo</td>
							<td class="literal">
								<form:input path="codgrupoaseg" id="codgrupoaseg" maxlength="2" cssClass="dato" onchange="javascript:mayusculas(this);" />
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_codgrupoaseg"> *</label>
							</td>
						</tr>
						<tr align="left">
							<td class="literal">Bonificación / Recargo Primas</td>
							<td class="literal">
								<form:input path="bonifrecprimas" id="bonifrecprimas" maxlength="3" cssClass="dato"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_bonifrecprimas"> *</label>
							</td>
						</tr>
						<tr align="left">
							<td class="literal">Bonificación / Recargo Rendimiento Máximo</td>
							<td class="literal">
								<form:input path="bonifrecrdtomax" id="bonifrecrdtomax" maxlength="3" cssClass="dato"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_bonifrecrdtomax"> *</label>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		 <br/>

		<div class="grid" style="">
			<display:table requestURI="grupoAsegurado.html" class="LISTA" summary="grupoAsegurado" defaultsort="0" defaultorder="ascending" pagesize="${numReg}" sort="list" excludedParams="operacion" name="${listaGrupoAsegurado}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorGrupoAsegurado" style="width:50%" id="grupoAsegurado" export="true">
				<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" property="columnaAcciones" title="Acciones" sortable="false" media="html" />
				<display:column class="literal" headerClass="cblistaImg" title="Grupo" property="columnaGrupo" sortable="true" style="width:30%;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Bonif / Rec. Primas" property="columnaPrimas" sortable="true"  style="width:30%;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Bonif / Rec. Máx" property="columnaRendimiento" sortable="true"/>
			</display:table>
		</div>
	</div>
	<br />
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>