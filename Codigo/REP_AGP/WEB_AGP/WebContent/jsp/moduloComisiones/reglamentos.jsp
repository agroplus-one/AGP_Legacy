<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<html>
<head>
	<title>Mantenimiento de Reglamento de Produccion Emitida</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
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
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	
	<script language="javascript">
	
		$(function(){
			$("#grid").displayTagAjax();
		});
	
		 $(document).ready(function(){
			 
			      var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		          document.getElementById("main3").action = URL;
			 
				  $('#main3').validate({
			 		 errorLabelContainer: "#panelAlertasValidacion",
	   				 wrapper: "li",
	   				 onfocusout: function(element) {
		   				 if(($('#method').val() == "doAlta" || $('#method').val() == "doEdita") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
								this.element(element);
						 }
	   				 },
	   				 highlight: function(element, errorClass) {
					 	$("#campoObligatorio_" + element.id).show();
	  			     },
	  				 unhighlight: function(element, errorClass) {
						$("#campoObligatorio_" + element.id).hide();
	  				 },
	   				 rules: {	
	   				 	"entidad.codentidad" : 	{required: true, digits: true,minlength: 4},		 	
	   				 	"plan" : 	{required: true, digits: true,minlength: 4},		 	
	   				 	"pctentidad" : {required: true,pctrango: true}	 	
					 },
					 messages: {
					 	"entidad.codentidad":{required: "El campo Entidad mediadora es obligatorio", digits: "El campo Entidad mediadora sólo puede contener dígitos", minlength: "El campo Entidad mediadora debe contener 4 dígitos"},
					 	"plan" : 	{required: "El campo Plan es obligatorio.",digits: "El campo Plan solo admite dígitos.",minlength: "El campo Plan debe contener 4 dígitos"},		 	
	   				 	"pctentidad" : {required: "El campo % Mediador es obligatorio.",pctrango: "El campo % Mediador debe contener un número entre 0 y 100"} 			 	
					 }
			 	});
		  });
		  
		  jQuery.validator.addMethod("pctrango", function(value, element, params) {
				var isvalid = false;
				value = value.replace(",",".");
				if(!isNaN(value)){
					if(value >=0 && value <= 100)
						isvalid = true;
					else
						isvalid = false;
				}else
					isvalid = false;
				return (this.optional(element) || isvalid);	
			});
	
		function cerrarPopUp(){
			$('#divReplicar').fadeOut('normal');
			$('#overlay').hide();
		}
		
		function trunc(num, ndec) { 
		   var fact = Math.pow(10, ndec); 	
				
		   return (parseInt(num * fact) / fact).toFixed(2); 
		} 
		
		function calcularPctRGA(){
			 var pctEntidad = parseFloat($('#txt_porcentajeMediador').val().replace(",","."));
			 if(!isNaN(pctEntidad)){
			 	$('#txt_porcentajeMediador').val(pctEntidad);
			 	if(pctEntidad <= 100){
			 		pctEntidad = parseFloat(pctEntidad);
				 	 var pctRGA = parseFloat(100) - parseFloat(pctEntidad);
				 	 var spctRGA = new String(pctRGA);
				 	 if (spctRGA.length > 4  ) 
				 	 {
				 	 	var quintoDecimal = "";
				 	 	if (pctEntidad > 10)
				 	 		var quintoDecimal = spctRGA.substring(5,6);
				 	 	if (quintoDecimal != '0' && quintoDecimal != '')
				 	 	{
				 	 		pctRGA = parseFloat(pctRGA);
				 	 		pctRGA = pctRGA + 0.01;
				 	 		spctRGA =  new String(pctRGA);
				 	 		pctRGA = spctRGA.substring(0,4);
				 	 	}
				 		else{
				 			pctRGA = spctRGA.substring(0,5);
				 			pctRGA = parseFloat(pctRGA);
				 		}
				 	 }
					 $('#pctrga').val(pctRGA); 			 
			 	}
			 }else{
			 	$('#pctrga').val('');
			 }
		}
	
		function limpiar(){
			$('#id').val('');
			$('#entidad').val('');
			$('#plan').val('');
			$('#txt_porcentajeMediador').val('');
			$('#pctrga').val('');			
			$('#usuario').val('');
			
			consultar();
		}
		
		function consultar(){
			$("#main3").validate().cancelSubmit = true;
			$('#method').val('doConsulta');
			$('#main3').submit();
		}
		
		function alta(){
			$('#method').val('doAlta');
			$('#main3').submit();
		}
		
		function modificar(id,plan,entidad,pctentidad,pctrga,usuario,nomentidad){
			$('#id').val(id);
			$('#entmediadora').val(entidad);
			$('#plan').val(plan);
			$('#txt_porcentajeMediador').val(pctentidad);
			$('#pctrga').val(pctrga);			
			$('#usuario').val(usuario);
			
			//BOTONES
			$('#btnAlta').hide();
			$('#btnModif').show();
		}
		
		function editar(){
			$('#method').val('doAlta');
			$('#main3').submit();
		}
		
		function borrar(id){
			$("#main3").validate().cancelSubmit = true;
			if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
				$('#method').val('doBaja');
				$('#id').val(id);
				$('#main3').submit();
			}
		}
		
		function abrirReplicar(){
			$('#divReplicar').fadeIn('normal');
			$('#overlay').show();
		}
		
		function replicar(){
			$("#replicar").validate().cancelSubmit = true;
			$('#methodReplicar').val('doReplicar');
			$('#replicar').submit();
		}
		function returnBack()
		{
			$('#methodVolver').val('doConsulta');
			if (document.getElementById('tipoFichero')!= null)
			{
				var tipo = document.getElementById('tipoFichero').value;
				$('#revisar_tipoFichero').val(tipo);
			}
			if (document.getElementById('idFichero') != null){
				var idFichero = document.getElementById('idFichero').value;
				$('#revisar_idFichero').val(idFichero);
				
			}
			$('#revisarForm').submit();				
			
		}
		function setOperacion(){
			$('#operacion').val('cambioPctMediador');
		}
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">			
					<a class="bot" id="btnAlta"  href="javascript:alta();">Alta</a>				
					<a class="bot" id="btnModif" style="display:none" href="javascript:editar();">Modificar</a>				
					<a class="bot" id="btnReplicar"  href="javascript:abrirReplicar();">Replicar Plan</a>				
					<a class="bot" id="btnConsultar"  href="javascript:consultar();">Consultar</a>				
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>
					<c:if test="${idFichero!=''}" >				
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if>				
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de Reglamento de Producción Emitida</p>
		<form:form id="main3" name="main3" method="post" action="reglamento.html" commandName="reglamentoBean">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="operacion" id="operacion"/>
			<form:hidden path="id" id="id"/>
			<form:hidden path="usuario.codusuario" id="usuario"/>
			<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
			<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
			<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero}"/>
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value=""/>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div style="panel2 isrt">
				<fieldset style="width:95%; margin:0 auto">
					<legend class="literal">Distribución Mediadores</legend>
					<table style="margin:0 auto">
						<tr>
							<td class="literal">Entidad</td>
							<td class="literal">
								<form:input	path="entidad.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" onchange="javascript:calcularPctRGA();" />
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"	alt="Buscar Entidad" title="Buscar Entidad" />
								<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio">*</label>
							</td>
							<td class="literal">Plan</td>
							<td class="literal">
								<form:input path="plan" id="plan" size="5" maxlength="4" cssClass="dato"/>
								<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio">*</label>
							</td>
						</tr>
						<tr>
							<td class="literal">% Entidad</td>
							<td class="literal">
								<form:input	path="pctentidad" size="5" maxlength="5" cssClass="dato" id="txt_porcentajeMediador" onchange="calcularPctRGA();"/>
								<label class="campoObligatorio" id="campoObligatorio_pctentidad" title="Campo obligatorio">*</label>
							</td>
							<td class="literal">% RGA</td>
							<td class="literal">
								<form:input	path="pctrga" size="5" maxlength="5" cssClass="dato" id="pctrga" readonly="true"/>
								<label class="campoObligatorio" id="campoObligatorio_pctrga" title="Campo obligatorio">*</label>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="reglamentos" 
					pagesize="${numReg}" name="${listReglamentos}" id="reglamentos" excludedParams="method"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorReglamentos" 
					sort="list"
					style="width:70%;border-collapse:collapse;">
					
					<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" 	title="Acciones"	property="admActions"    style="width:50px;text-align:center" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Entidad"   						property="entidad"       style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan"   						property="plan"          style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Entidad"  					property="pctent"    	 style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="% RGA"            				property="pctrga"    	 style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Modificación"      		property="fecha" 		 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
			</display:table>
		</div>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<!-- panel replicacion -->
	<form action="reglamento.html" id="replicar" name="replicar" method="post">
		<input type="hidden" name="method" id="methodReplicar"/>
		<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
		<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
		<div id="divReplicar" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
			       <!--  header popup -->
					<div id="header-popup" style="padding:0.4em 1em;position:relative;
					                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
					                                  background:#525583;height:15px">
					        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					            Replicar Plan
					        </div>
					        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
					                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					            <span onclick="cerrarPopUp()">x</span>
					        </a>
					</div>
					<!--  body popup -->
					<div class="panelInformacion_content">
							<div id="panelInformacion" class="panelInformacion">
								<div id="planO" style="width:  45%;float:left;">
									Plan Origen: <input type="text" id="planorigen" name="planorigen" size="4" maxlength="4" value="${planorigen }">
												 
								</div>
								<div id="planD" style="width: 45%;float:right;">
									Plan Nuevo: <input type="text" id="plannuevo" name="plannuevo" size="4" maxlength="4" value="${plannuevo }">
								</div>
								<div style="margin-top:15px;clear: both">
								    <a class="bot" href="javascript:cerrarPopUp()" title="Cancelar">Cancelar</a>
								    <a class="bot" href="javascript:replicar()" title="Continuar">Replicar</a>
								</div>
							</div>
					</div>
		</div>
	</form>
	
	<form name="revisarForm" id="revisarForm" action="incidencias.html">
		<input type="hidden" name="method" id="methodVolver"/>
		<input type="hidden" id="revisar_idFichero" name="idFichero" /> 
		<input	type="hidden" id="revisar_tipoFichero" name="tipo" /> 
	</form>

</body>
</html>