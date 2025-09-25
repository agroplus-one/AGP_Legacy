<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Mantenimiento de Distribucion de Gastos de Gestion Externa</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script>
		
			$(function(){
				$("#grid").displayTagAjax();
					}).ajaxComplete(function(e, xhr, settings){	
						if((settings.url.indexOf('ajaxCommon.html')) < 0 && (settings.url.indexOf('doActualizaPlan')==-1)){
							 pctCalculado();
						}
			});
			
			 function cerrarPopUp(){
			     $('#divReplicar').fadeOut('normal');
			     $('#modificarPorcentajes_popup').fadeOut('normal');
			     $('#overlay').hide();
			 }
			 
			 $(document).ready(function(){
			 
			      var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		          document.getElementById("main3").action = URL;
			 
			  	  pctCalculado();
			  	  
			  	  if(${alertaCargarDatos}){
			  	  		$('#divAviso').fadeIn('normal');
			  	  		$('#overlay').show();
			  	  }
			  
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
	   				 	"subentidadMediadora.id.codentidad" : 	{required: true, digits: true,minlength: 4,range: [3000,8999|9999]},		 	
	   				 	"subentidadMediadora.id.codsubentidad" : 	{required: true, digits: true,range: [0,99],entsubentcorrecta:true},		 	
	   				 	"plan" : 	{required: true, digits: true,minlength: 4},		 	
	   				 	"pctmediador" : {required: true}	 	   				 		
					 },
					 messages: {
					 	"subentidadMediadora.id.codentidad":{required: "El campo Entidad mediadora es obligatorio", digits: "El campo Entidad mediadora sólo puede contener dígitos", minlength: "El campo Entidad mediadora debe contener 4 dígitos",range: "Entidad mediadora no válida"},
					 	"subentidadMediadora.id.codsubentidad":{required: "El campo Subentidad mediadora es obligatorio", digits: "El campo Subentidad mediadora sólo puede contener dígitos",range: "Subentidad mediadora no válida",entsubentcorrecta:"Subentidad mediadora no válida para ese mediador"},
					 	"plan" : 	{required: "El campo Plan es obligatorio.",digits: "El campo Plan solo admite dígitos.",minlength: "El campo Plan debe contener 4 dígitos"},		 	
	   				 	"pctmediador" : {required: "El campo % Mediador es obligatorio."} 			 	
					 }
			 	});
			 	
			 	jQuery.validator.addMethod("entsubentcorrecta", function(value, element, params) {
			 		//ASF - 21/01/2014 - Los registros de tipo 3xxx-0 no tienen que darse de alta en este mantenimiento
			 		var valorEntidad = $('#entmediadora').val();
			 				
			 		if ((valorEntidad.length == 4) && (valorEntidad >= 3000 && valorEntidad <= 8999) && 
			 			(valorEntidad.charAt(0) != 3 || ((valorEntidad.charAt(0) == 3) && (value != 0)))){
			 			return true;
			 		} else {
			 			return false;
			 	 	}
			 	});
			 	
		     });
		     
		     function trunc(num, ndec) { 
				  var fact = Math.pow(10, ndec); // 10 elevado a ndec 
				
				  /* Se desplaza el punto decimal ndec posiciones, 
				    se trunca el número y se vuelve a colocar 
				    el punto decimal en su sitio. */ 
				  return (parseInt(num * fact) / fact).toFixed(2); 
			} 
		     
		     function pctCalculado(){		
		     	var table = document.getElementById('tableDistribucionGGE');
		         	if(table){
		         		var rowCount = table.rows.length;
			         	for(var i=1; i<rowCount; i++){
				         	var cellMediador = table.rows[i].cells[5];
				         	var cellPlan = table.rows[i].cells[3];
				         	var pctgeneral = obtenerPorcentajePorPlan(cellPlan.innerHTML);
				         	var color='green';
				         	if(pctgeneral==0){
				         		color='red';		
				         		var imgs=table.rows[i].cells[0].getElementsByTagName('img');				         		
				         		imgs[0].src='jsp/img/displaytag/transparente.gif';
				         		imgs[0].witdh='16';
				         		imgs[0].height='16';
				         		imgs[0].alt='';
				         		var aes=table.rows[i].cells[0].getElementsByTagName('a');
				         		aes[0].href="#";
				         		
							}
				         	var pctmediador = parseFloat(cellMediador.innerHTML);
				         	var resultadoMediador = Math.round(trunc(pctmediador * pctgeneral / 100,3)*100)/100;
				         	var strMediador = cellMediador.innerHTML + "&nbsp;&nbsp;<span style='color:"+color+"'>("+resultadoMediador+"%)</span>";
				         	cellMediador.innerHTML = strMediador;
				         	
				         	var cellEntidad = table.rows[i].cells[4];
				         	var pctentidad = parseFloat(cellEntidad.innerHTML);
		
				         	if(pctentidad != 0){
					         	var resultadoEntidad  = Math.round(trunc(pctgeneral - resultadoMediador,3)*100)/100;
				         	}else{
				         		var resultadoEntidad  = "0.00";
				         	}
				         	var strEntidad = cellEntidad.innerHTML + "&nbsp;&nbsp;<span style='color:"+color+"'>("+resultadoEntidad+"%)</span>";
				         	cellEntidad.innerHTML = strEntidad;
			         	}
		         }
			}
			
			function obtenerPorcentajePorPlan(plan){
		
				var jsonArray = ${planesJSON};	
					for(var j=0; j<jsonArray.length; j=j+2) {
						if(plan==jsonArray[j]){
						return jsonArray[j+1];
						}
					}
				return 0;				
			}
			 
		     function limpiar() {
		     	$('#entmediadora').val('');
				$('#desc_entmediadora').val('');
				$('#desc_subentmediadora').val('');
				$('#subentmediadora').val('');
		    	$('#txt_plan').val('');
		    	$('#txt_porcentajeMediador').val('');
		    	$('#idGgeSubentidad').val('');
		    	
				consultar();
		     }
		     
		     function consultar(){
		     	$('#main3').validate().cancelSubmit = true;
		     	$('#method').val('doConsulta');
		     	$('#limpiar').val('limpiar');
		     	$('#main3').submit();
		     }
		     
		     function alta(){
		    	$('#method').val('doAlta');
		    	$('#idGgeSubentidad').val('');
		     	$('#main3').submit();
		     }
		     
		     function editar(){
		     	$('#method').val('doEdita');
		     	$('#main3').submit();
		     }
		     
		  	 function deleteGeeSubentidad(id){
		  	 	$('#main3').validate().cancelSubmit = true;
		  	    if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
			  	    $('#method').val('doBaja');
			  	    $('#idGgeSubentidad').val(id);
			     	$('#main3').submit();
		     	}
		  	 }
		  	 
		  	 function showReplicarPlanPopUp(){
		  	    $('#divReplicar').fadeIn('normal');
		  	    $('#overlay').show();
		  	 }
		  	 
		  	 function modificar(id,codentidad,plan,codsub,pctmediador,nomentidad,nombsub){
		  	 	$('#idGgeSubentidad').val(id);
		  	 	$('#entmediadora').val(codentidad);
				$('#desc_entmediadora').val(nomentidad);
				$('#desc_subentmediadora').val(nombsub);
				$('#subentmediadora').val(codsub);
		    	$('#txt_plan').val(plan);
		    	$('#txt_porcentajeMediador').val(pctmediador);
		  	    actualizaPlan(plan);
		  	    //Botones
				$("#btnAlta").hide();
				$("#btnModificar").show();				
		  	 }
		  	 
		  	 function actualizaPlanPorCampoPlan(){
		  	 
			  	 if(($('#txt_plan').val()!=null) && $('#txt_plan').val().length==4){
			  	 	actualizaPlan($('#txt_plan').val());
			  	 }else{
			  	 	alert("No hay datos para el plan seleccionado");
			  	 }
		  	 }
		  	 
		  	 function actualizaPlan(plan){
			$.ajax({
				    url: "gge.html?method=doActualizaPlan&idplan="+plan,
					data: "",
					async:true,
				    dataType: "json",
				    success: function(datos){
				    	if(datos.ge!=null && datos.rga!=null && datos.gsa!=null){
					            $('#pctentidades').val(datos.ge);
					            $('#pctrga').val(datos.rga);  
					            $('#celdaplan').text(plan);					           				           
					            $('#celdrsa').text(datos.gsa);					          				            
				            }else{
				            	alert("No hay datos para el plan seleccionado");
				            }	 		   		
				    },
				    beforeSend: function(){
			           		
            		},
            		complete: function(){
            				            					
            		},				           
				    type: "POST"
				});
		}
		  	 
		  
		  	 function showModificarPorcentajesPopUp(){
		  	 	 $('#campoObligatorio_pctentidadnuevo').hide();
		  	 	 $('#modificarPorcentajes_popup_error').hide();
		  	     $('#modificarPorcentajes_popup').fadeIn('normal');
		  	     $('#overlay').show();  
		  	 }
		  	 
		  	 function modificarPorcentajes(){
		  	 	if(validarpctEntidades()){
		  	 		calcularpctRGA();
					$('#methodModifPct').val('doModificarPorcentajes');
					if (document.getElementById('tipoFichero')!= null)
					{
						var tipo = document.getElementById('tipoFichero').value;
						$('#tipoFichero').val(tipo);
					}
					if (document.getElementById('idFichero') != null){
						var idFichero = document.getElementById('idFichero').value;
						$('#idFichero').val(idFichero);	
					}
					var plan = $('#celdaplan').text();
					$('#planPorcentajes').val(plan);
			     	$('#modifPct').submit();
		  	 	}else{
		  	 		$('#campoObligatorio_pctentidadnuevo').show();
		  	 		$('#modificarPorcentajes_popup_error').show();
		  	 	}
		  	 }
		  	 
		  	 function replicar(){
				$("#replicar").validate().cancelSubmit = true;
				$('#methodReplicar').val('doReplicar');
				if (document.getElementById('tipoFichero')!= null)
				{
					var tipo = document.getElementById('tipoFichero').value;
					$('#tipoFichero').val(tipo);
						
				}
				if (document.getElementById('idFichero') != null){
					var idFichero = document.getElementById('idFichero').value;
					$('#idFichero').val(idFichero);	
				}
			
				$('#replicar').submit();
			}
			
			function validarpctEntidades(){
				var isValid = false;
				var aux = $('#pctentidadnuevo').val();				
				aux=aux.replace(",",".");
				if(aux.indexOf(".")==0){
					aux='0'+aux;
				}				
				
				isValid = /^[0-9|.]+$/i.test(aux);
				if (isValid){
					var pctentidades = parseFloat(aux);
					if(!isNaN(pctentidades)){
						$('#pctentidadnuevo').val(pctentidades)
						var pctgeneral = parseFloat($('#porcentajeGeneral').val());
						if(pctentidades > pctgeneral){
							isValid = false;
						}else{
							isValid = true;
						}
					}else{
						$('#pctentidadnuevo').val('');
					}
				} else {
					$('#pctentidadnuevo').val('');
				}
				return isValid;
			}
			
			function calcularpctRGA(){
				var pctentidades = parseFloat($('#pctentidadnuevo').val());
				var pctgeneral = parseFloat($('#porcentajeGeneral').val());
				var pctrga = trunc(pctgeneral - pctentidades,2);
				$('#txt_porcentajeRga').val(pctrga);
			}
			
			function setOperacion(){
				$('#operacion').val('cambioPctMediador');
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
		</script>
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');">
	<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
	<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
						<a class="bot" id="btnModificar" style="display:none" href="javascript:editar();">Modificar</a>
						<a class="bot" href="javascript:showReplicarPlanPopUp();">Replicar Plan</a>
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
						<c:if test="${idFichero!=''}" >
							<a class="bot" href="javascript:returnBack();">Volver</a>
						</c:if>	
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Mantenimiento de Distribución de Gastos de Gestión Externa</p>
			<form:form name="main3" id="main3" action="gge.html" method="post" commandName="geeSubentidadesBean">
				<input type="hidden" name="operacion" id="operacion"/>
				<input type="hidden" name="method" id="method"/>
				<form:hidden path="id" id="idGgeSubentidad"/>
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
				<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
				<input type="hidden" name="limpiar" id="limpiar" />
				<input type="hidden" name="entidad2" id="entidad" value=""/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div style="panel2 isrt">
					    <fieldset style="width:95%; margin:0 auto;">
							    <table align="center">
									<tr>
									    <td width="200px" class="literal">% General Sector Agrario</td>
										<td width="60px" class="detalI" id="celdapsa" name="celdapsa">${ggeEntidad.pctsectoragricola}</td>
										<td width="60px" class="literal">PLAN</td>
									    <td width="100px" class="detalI" id="celdaplan" name="celdaplan">${ggeEntidad.plan}</td>
									    <td width="60px">
									       <a class="bot" id="btnModificarPorcentaje" href="javascript:showModificarPorcentajesPopUp();" title="Modificar porcentaje">Modificar</a>
									    </td>
									 </tr>
									 <tr>
									    <td width="200px" class="literal">% General Entidades</td>
										<td width="60px" class="literal">
											<input id="pctentidades" name="pctentidades" type="text" size="2"  maxlength="2" class="dato" disabled="disabled" value="${ggeEntidad.pctentidades}"/>
											<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_generalEntidades"> *</label>
										</td> 
										<td width="60px" class="literal">% RGA</td>
										<td width="60px" class="literal">
										    <input id="pctrga" name="pctrga" type="text" size="2" maxlength="2" class="dato"  disabled="disabled" value="${ggeEntidad.pctrga}" />
										</td>
									 </tr>
								 </table>
					    </fieldset>	
					     <fieldset style="width:95%; margin:0 auto;">
							    <legend class="literal">Distribución Entidades</legend>
							    <table align="center">
									<tr>
									    <td class="literal">Entidad Mediadora</td>
										<td class="literal">
											<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');setOperacion();UTIL.cambiarPctMediador();" />
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:setOperacion();lupas.muestraTabla('EntidadMediadora','principio', '', '');"	alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
											<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
										</td>
										<td class="literal">Subentidad Mediadora</td>
										<td class="literal">
											<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');"/>
											<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="40" readonly="true"/>
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
											<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
										</td>
									 </tr>
									 <tr>
									    <td class="literal">Plan</td>
										<td class="literal">
											<form:input id="txt_plan" path="plan" size="4" maxlength="4" cssClass="dato" onblur="actualizaPlanPorCampoPlan();"/>
											<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_plan"> *</label>
										</td>
										<td class="literal">% Mediador</td>
										<td class="literal">
											<form:input path="pctmediador" id="txt_porcentajeMediador" size="5" maxlength="5" cssClass="dato" />
											(Sobre el 100%)
											<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_txt_porcentajeMediador"> *</label>
										</td>
									 </tr>
								 </table>
					    </fieldset>	
				</div>
	      </form:form>
	      <div id="grid">
		      <display:table  requestURI="" class="LISTA" 
					          decorator="com.rsi.agp.core.decorators.modelTableDecoratorDistribucionGGE" 
					          defaultsort="0" defaultorder="ascending"
							  pagesize="${numReg}" sort="list" name="${listCultivosSubentidades}" 	
							  id="tableDistribucionGGE" style="width:80%;border-collapse:collapse;" excludedParams="method limpiar">
							
						<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="admActions" sortable="false"  style="width:50px;text-align:center" />
						<display:column class="literal" headerClass="cblistaImg" title="Entidad Med."       property="entidad"             style="text-align:center"  sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="Subentidad Med."    property="subentidad"          style="text-align:center"  sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSubEntidadMediadora"/>
						<display:column class="literal" headerClass="cblistaImg" title="Plan"               property="plan"                style="text-align:center"  sortable="true"/>
						<display:column class="literal" headerClass="cblistaImg" title="% Entidad"          property="porcentajeentidad"   style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
						<display:column class="literal" headerClass="cblistaImg" title="% Mediador"         property="porcentajemediador"  style="text-align:right"  sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fec.Modificacion"   property="fechamodificacion"   style="text-align:center"  sortable="true" format="{0,date,dd/MM/yyyy}"/>
						
			 </display:table>
		 </div>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<!-- panel replicacion -->
	<form action="gge.html" id="replicar" name="replicar" method="post">
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
								    <a class="bot" href="javascript:cerrarPopUp()">Cancelar</a>
								    <a class="bot" href="javascript:replicar()">Replicar</a>
								</div>
							</div>
					</div>
		</div>
	</form>
	<form action="gge.html" id="modifPct" name="modifPct" method="post">
		<input type="hidden" name="method" id="methodModifPct"/>
		<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
		<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
		<input type="hidden" name="porcentajeGeneral" id="porcentajeGeneral" value="${ggeEntidad.pctsectoragricola}"/>
		<input type="hidden" name="planPorcentajes" id="planPorcentajes" value="${ggeEntidad.plan}"/>
		<input type="hidden" name="txt_porcentajeRga" id="txt_porcentajeRga" value="${ggeEntidad.pctrga}"/>
		<input type="hidden" name="txt_generalEntidades" id="txt_generalEntidades" value="${ggeEntidad.pctentidades}"/>
	
		<!--  popup modificarPorcentajes -->
		<div id="modificarPorcentajes_popup" class="wrapper_popup">
		    <div class="header-popup">
		        <div class="title_popup">Modificar porcentaje general entidades</div>
		        <a class="close_botton_popup"><span onclick="cerrarPopUp()">x</span></a>
		    </div>
		     <div id="modificarPorcentajes_popup_error" class="literal" style="color:red;display:none;text-align:center">
		       % introducido no es válido
		    </div>
			<div class="content_popup">
			    <table>
			        <tr>
			            <td class="literal">% general entidades</td>
			            <td>
			            	<input id="pctentidadnuevo" name="pctentidadnuevo" class="dato" onchange="" type="text" value="${porcentajeEntidades}" size="4" maxlength="4" />
			            	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_pctentidadnuevo"> *</label>
			            </td>
			        </tr>
			    </table>
				<div style="margin-top:15px">
				    <a class="bot" href="javascript:cerrarPopUp()">Cancelar</a>
				    <a class="bot" href="javascript:modificarPorcentajes()">Modificar </a>
				</div>
			</div>
		</div>
	</form>
		<!-- panel avisos -->
		<div id="divAviso" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	       <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
			                                  background:#525583;height:15px">
			        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Aviso
			        </div>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
						<div id="txt_info" style="width: 70%" >ATENCIÓN! No han sido cargados los datos generales de entidades.</div>
					</div>
			</div>
			<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">					
				<div style="margin-top:15px;clear: both">
					<a class="bot" href="menu.html?OP=ppal">Aceptar</a>				
				</div>
			</div>
	</div>
		</div>
	    <%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	    
	 <form name="revisarForm" id="revisarForm" action="incidencias.html">
		<input type="hidden" name="method" id="methodVolver"/>
		<input type="hidden" id="revisar_idFichero" name="idFichero" /> 
		<input	type="hidden" id="revisar_tipoFichero" name="tipo" /> 
	</form>
	</body>
	
	
</html>

