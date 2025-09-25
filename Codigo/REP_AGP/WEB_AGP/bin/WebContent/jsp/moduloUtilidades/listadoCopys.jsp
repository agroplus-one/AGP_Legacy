<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numRegImpresion">
		<fmt:message key="impresionnumReg"/>
	</c:set>
</fmt:bundle>

<html>
	<head>
		<title>Duplicados informáticos (Copys)</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		
		<script type="text/javascript" charset="ISO-8859-1">
		
		    $(function(){
				$("#grid").displayTagAjax();
			});
			
			$(document).ready(function(){
			    
			     var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		        document.getElementById("main3").action = URL;
		        if ($('#btnImprimirPolizaActivado').val()!=""){
			        $('#a').css({'display':'none'});
				 	var frm = document.getElementById('recibosPoliza');	
				 	frm.method.value = 'doVerPDFPoliza2';
					frm.tipoRefPoliza.value = $('#datoTipoRefPoliza').val();
					frm.idPoliza.value = $('#datoIdPol').val();;
					frm.poliza.value = $('#datoRefPoliza').val();;
					frm.plan2.value = $('#datoCodPlan').val();;
					frm.target="_blank";
					$('#recibosPoliza').submit();
		        }
		        if ($('#poliza').val()!=''){
			 		$('#dcpoliza').attr('readonly',false);
			 	}else{
			 		$('#dcpoliza').val('');
			 		$('#dcpoliza').attr('readonly',true);
			 	} 
			    if ($('#colectivo').val()!=''){
			 		$('#dccolectivo').attr('readonly',false);
			 	}else{
			 		$('#dccolectivo').val('');
			 		$('#dccolectivo').attr('readonly',true);
			 	}
			 	
				$('#main3').validate({
					 errorLabelContainer: "#panelAlertasValidacion",
   					 wrapper: "li",
   					 highlight: function(element, errorClass) {
				 	 $("#campoObligatorio_" + element.id).show();
  			     	 },
  				 	 unhighlight: function(element, errorClass) {
					 $("#campoObligatorio_" + element.id).hide();
  					 },
					 rules: {
					 	"colectivo.tomador.id.codentidad": {required: true},
					 	"referencia": {required: true},
					 	"dc": {required: true}
					 },
					 messages: {
					 "colectivo.tomador.id.codentidad" : {required: "el campo Entidad es obligatorio"},
					 "referencia": {required: "el campo póliza es obligatorio"},
					 "dc": {required: "el campo DC de la póliza es obligatorio"}
					 }
				});
				
				<c:if test="${empty listaPolizas}">					
					$('#btnImprimir').hide(); 									
				</c:if> 				
					 
			});
		    			
			function bloqueaInputs(){
				var frm = document.getElementById('main3');
			}
			
			 function setEntidad(entidad){
				document.getElementById('entidad').value = entidad;
			 }
	        
	        function limpiarAvisos(){
	        	$("#panelAlertasValidacion").hide();
	        	$("#campoObligatorio_poliza").hide();
	        	$("#campoObligatorio_dcpoliza").hide();
			 	$("#campoObligatorio_DCcolectivo").hide();
			 	$("#campoObligatorio_entidad").hide();
			 	$("#campoObligatorio_nifcif").hide();
			 	$('#data1').css({'display':'none'});
			 	$('#data2').css({'display':'none'});
			 	$('#data3').css({'display':'none'});
			 	$('#data4').css({'display':'none'});
			 	$('#a').css({'display':'none'});
	        }
	        
			 function consultar(){
			 $("#main3").validate().cancelSubmit = true;
				limpiarAvisos();
			 
				 if ($('#nifcif').val()=='' || $('#entidad').val()==''){
				    $('#a').css({'display':'block'});
				 	if ($('#entidad').val()==''){
				    	$('#data1').css({'display':'block'});
				    	$("#campoObligatorio_entidad").show();
				    }
				    if ($('#nifcif').val()==''){
				    	$('#data2').css({'display':'block'});
				    	$("#campoObligatorio_nifcif").show();
				    }
				 }else{
				 	if (($('#poliza').val()!='' && $('#dcpoliza').val()=='') || ($('#colectivo').val()!='' && $('#dccolectivo').val()=='')){
				 		if ($('#poliza').val()!='' && $('#dcpoliza').val()==''){
				 			$('#a').css({'display':'block'});
				 			$('#data3').css({'display':'block'});
				 			$("#campoObligatorio_dcpoliza").show();
				 		}
				 		if ($('#colectivo').val()!='' && $('#dccolectivo').val()==''){
				 			$('#a').css({'display':'block'});
				 			$('#data4').css({'display':'block'});
				 			$("#campoObligatorio_DCcolectivo").show();
				 		}
					}else{
						var frm = document.getElementById('main3');			
						$('#operacion').val("copy");
						frm.imprimirPoliza.value = "";
						frm.target="";
						$('#main3').submit();
						}
				 }
			 }
			
			 function limpiar(){
			 	$("#main3").validate().cancelSubmit = true;
				$('#entidad').val('');
				$('#desc_entidad').val('');
				$('#codusuario').val('');
				$('#plan').val('');
				$('#linea').val('');
				$('#desc_linea').val('');
				$('#poliza').val('');
				$('#colectivo').val('');
				$('#dc').val('');
				$('#dcpoliza').val('');
				$('#nifcif').val('');
				$('#ref').val('');
				$('#nombre').val('');
				
				var frm = document.getElementById('main3');
				frm.imprimirPoliza.value = "false";
				$('#operacion').val("copy");
				$('#listadoVacioCopy').val("true");
				frm.target="";
				$('#main3').submit();
			 }
			
			 function imprimirInforme(idpoliza,tiporef,codPlan,refPoliza){
			 	$('#a').css({'display':'none'});
			 	var frm = document.getElementById('recibosPoliza');	
			 	frm.method.value = 'doVerPDFPoliza2';
				frm.tipoRefPoliza.value = tiporef;
				frm.idPoliza.value = idpoliza;
				frm.plan2.value = codPlan;
				frm.poliza.value = refPoliza;
				frm.target="_blank";
				$('#recibosPoliza').submit();
			 }
			 
			 function imprimirPoliza(){
			 	limpiarAvisos();
				if (($('#poliza').val()!='' && $('#dcpoliza').val()=='') || ($('#colectivo').val()!='' && $('#dccolectivo').val()=='')){
				 		if ($('#poliza').val()!='' && $('#dcpoliza').val()==''){
				 			$('#a').css({'display':'block'});
				 			$('#data3').css({'display':'block'});
				 			$("#campoObligatorio_dcpoliza").show();
				 		}
				 		if ($('#colectivo').val()!='' && $('#dccolectivo').val()==''){
				 			$('#a').css({'display':'block'});
				 			$('#data4').css({'display':'block'});
				 			$("#campoObligatorio_DCcolectivo").show();
				 		}
				}else{
					$("#main3").validate().cancelSubmit = false;
					var frm = document.getElementById('main3');
					$('#operacion').val("copy");
					frm.imprimirPoliza.value = "true";
					$('#main3').submit();
				}
			 }
			 
			 function changeDCpoliza(){
			 	if ($('#poliza').val()!=''){
			 	$('#dcpoliza').attr('readonly',false);
			 	}else{
			 		$('#dcpoliza').val('');
			 		$('#dcpoliza').attr('readonly',true);
			 	}
			 }
			 
			 function changeDCcolectivo(){
			 	if ($('#colectivo').val()!=''){
			 	$('#dccolectivo').attr('readonly',false);
			 	}else{
			 		$('#dccolectivo').val('');
			 		$('#dccolectivo').attr('readonly',true);
			 	}
			 }
		     
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');bloqueaInputs();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
								<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
								<a class="bot" href="javascript:limpiar();">Limpiar</a>
								<a class="bot" href="javascript:imprimirPoliza();">Imprimir Póliza</a>
								<a class="bot" href="menu.html?OP=ppal">Salir</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Duplicados inform&aacute;ticos (Copys)</p>
			<form name="" id="recibosPoliza" action="recibosPoliza.html" method="post">
				<input type="hidden" name="method" id=""/>
				<input type="hidden" id="operacion" name="operacion" value="copy"/>
				<input type="hidden" name="idPoliza" id="idPoliza"/>
				<input type="hidden" name="tipoRefPoliza" value="">
				<input type="hidden" name="plan2" value="">
				<input type="hidden" name="opTipoPol" value="">
				<input type="hidden" name="poliza" value="">
				<input type="hidden" name="dcpoliza" value="">
			</form>
			<form:form name="main3" id="main3" action="utilidadesPoliza.html" method="post" commandName="polizaBean">
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
				<form:hidden path="idenvio" id="idenvio"/>
				<input type="hidden" name="accion" value=""/>
				<input type="hidden" id="operacion" name="operacion" value="copy"/>
				<input type="hidden" name="polizaOperacion" value=""/>				
				<input type="hidden" name="perfil" value="${perfil}"/>
				<input type="hidden" name="oficina2" id="sucursal"/>
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="listCheck" id="listCheck" value=""/>
				<input type="hidden" name="fecEnvioId.day" value=""> 
	            <input type="hidden" name="fecEnvioId.month" value=""> 
	            <input type="hidden" name="fecEnvioId.year" value=""> 
	            <input type="hidden" name="tipoRefPoliza" value=""> 
				<input type="hidden" name="imprimirPoliza" value="">
				<input type="hidden" name="listadoVacioCopy" id="listadoVacioCopy" value="">
				<input type="hidden" name="btnImprimirPolizaActivado" id="btnImprimirPolizaActivado" value="${btnImprimirPolizaActivado}"/>
				<input type="hidden" name="datoTipoRefPoliza" id="datoTipoRefPoliza" value="${datoTipoRefPoliza}"/>
				<input type="hidden" name="datoIdPol" id="datoIdPol" value="${datoIdPol}"/>
				<input type="hidden" name="datoCodPlan" id="datoCodPlan" value="${datoCodPlan}"/>
				<input type="hidden" name="datoRefPoliza" id="datoRefPoliza" value="${datoRefPoliza}"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="displaytag" id="a" style="display:none">
					<table width="40%"><tr>
						<td class="centrado" style="width:400px;height:20px;color:red;border:1px solid #DD3C10;display:block;
								font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8">
						<ul>
							<li id="data1" name="data1" style="display:none">el campo Entidad es obligatorio</li>
							<li id="data2" name="data2" style="display:none">el campo NIF/CIF es obligatorio</li>
							<li id="data3" name="data3" style="display:none">el campo DC de la p&oacute;liza es obligatorio</li>
							<li id="data4" name="data4" style="display:none">el campo DC del colectivo es obligatorio</li>
						</ul>	
						</td>
					</tr>
					</table>
				</div>
				<div class="panel2 isrt">
					<fieldset>
					<legend class="literal">Filtro</legend>
							<table>
								<tr align="left">
									<td class="literal">Entidad</td>
									<td class="literal" colspan="3">
										<c:if test="${perfil == 0 || perfil == 5}">
											<form:input path="colectivo.tomador.id.codentidad" size="5" maxlength="4" cssClass="dato" id="entidad" tabindex="1" />
											<label class="campoObligatorio" title="Campo obligatorio"
											id="campoObligatorio_entidad"> *</label>
											<form:input path="colectivo.tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupaEntidad('','','');" alt="Buscar Entidad" title="Buscar Entidad" />
										</c:if>
										<c:if test="${perfil > 0 && perfil < 5}">
											<form:input path="colectivo.tomador.id.codentidad" size="5" maxlength="4" cssClass="dato" readonly="true" id="entidad" tabindex="1"/>
										</c:if>
									</td>
									<td class="literal">CIF/NIF <br>Asegurado</td>
								<td class="literal">
									<form:input path="asegurado.nifcif" id="nifcif" size="20" maxlength="11" cssClass="dato" tabindex="2" onchange="this.value=this.value.toUpperCase();"/>
									<label class="campoObligatorio" title="Campo obligatorio"
											id="campoObligatorio_nifcif"> *</label>
								</td>
								<td class="literal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TIPO POL</td>
									<td class="literal" width="205px">	
										<select name="opTipoPol"  class="dato"	style="width:70" id="opTipoPol">
											<option value="1"<c:if test="${opTipoPol == 1}">selected</c:if>>P</option>
											<option value="0"<c:if test="${opTipoPol == 0}">selected</c:if>>C</option>
											<option value="2"<c:if test="${opTipoPol == 2}">selected</c:if>>T</option>
										</select>
									</td>
								</tr>
								<tr align="left">
									<td class="literal">Plan</td>
									<td class="literal">
										<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="3" onchange=""/>
									</td>
									<td></td>
									<td></td>
									<td class="literal">L&iacute;nea</td>
									<td class="literal" colspan="3">
										<form:input path="linea.codlinea" size="5" maxlength="4" cssClass="dato" id="linea" tabindex="4" onchange=""/>
										<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" alt="Buscar Línea" title="Buscar Línea" />					
									</td>
								</tr>
								<tr>
									<td class="literal">P&oacute;liza</td>
									<td class="literal">
										<form:input path="referencia" id="poliza" size="20" maxlength="15" cssClass="dato" tabindex="5" onchange="this.value=this.value.toUpperCase();changeDCpoliza();"/>
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_poliza"> *</label>
										<form:input path="dc" cssClass="dato" id="dcpoliza" size="2" tabindex="6" maxlength="2" readonly="true"/>   
										<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_dcpoliza"> *</label>
									</td>
									<td></td>
									<td></td>
									<td class="literal">Colectivo</td>
									<td class="literal">
										<form:input path="colectivo.idcolectivo" id="colectivo" size="11" maxlength="9" cssClass="dato" tabindex="7" onchange="changeDCcolectivo();"/>
										<form:input path="colectivo.dc" size="3" id="dccolectivo" maxlength="1" cssClass="dato" tabindex="8" readonly="true"/>
										<label class="campoObligatorio" title="Campo obligatorio"
											id="campoObligatorio_DCcolectivo"> *</label>
									</td>
								</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
		<div id="grid">
		  <form name="list" id="list">
				<display:table requestURI="utilidadesPoliza.html" id="listaResultados" class="LISTA" summary="Poliza" 
		                       pagesize="${numReg}" size="${totalListSize}"  name="${listaPolizas}" 
		                       decorator="com.rsi.agp.core.decorators.ModelTableDecoratorCopys" style="width:100%;border-collapse:collapse;" 
		                       partialList="true"> 
			        <display:setProperty name="pagination.sort.param" value="sort"/>
					<display:setProperty name="pagination.sortdirection.param" value="dir"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="polSelec" style="width:80px;text-align:center" />
					<display:column class="literal" headerClass="cblistaImg" title="CIF/NIF Aseg" property="polCifNif" sortProperty="ase.nifcif" sortable="true" style="width:100px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Asegurado" property="polNombreAseg" sortable="false" style="width:250px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Poliza" property="polPoliza" sortProperty="referencia" sortable="true" style="width:80px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="P/C" property="polTipoRef"  style="width:70px;"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan" property="polPlan" sortProperty="lin.codplan" sortable="true" style="width:50px"/>
					<display:column class="literal" headerClass="cblistaImg" title="Linea" property="polLinea" sortProperty="lin.codlinea" sortable="true" style="width:50px"/>	
					<display:column class="literal" headerClass="cblistaImg" title="Colectivo" property="polColectivo" />
					<display:column class="literal" headerClass="cblistaImg" title="Usuario" property="polUsuario" sortProperty="usuario" sortable="true" style="width:80px"/>
		        </display:table>
		        </form>
		</div>   
</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
	</body>
</html>