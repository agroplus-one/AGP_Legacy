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
	<title>Mantenimiento de comisiones por E-S Mediadora</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
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
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	
	<script language="javascript">

		$(function(){
			$("#grid").displayTagAjax();
		});
		
		$(document).ready(function(){		
			
		 	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
			document.getElementById("main3").action = URL;  
			
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
		        inputField        : "fecEfecto",
		        button            : "btn_fechaEfecto",
		        ifFormat          : "%d/%m/%Y",
		        daFormat          : "%d/%m/%Y",
		        align             : "Br"			        	        
		  	});
			
		 	$('#main3').validate({
		 		 errorLabelContainer: "#panelAlertasValidacion",
		 		 onfocusout: function(element) {
		   			if(($('#method').val() == "doGuardarDistribucionMediadores") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
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
   				 	"subentidadMediadora.id.codentidad" : 	{required: true, digits: true,minlength: 4,range: [3000,9999]},		 	
   				 	"subentidadMediadora.id.codsubentidad" : 	{required: true, digits: true,range: [0,99]},		 	
   				 	"linea.codplan" : 	{required: true, digits: true,minlength: 4},		 	
   				 	"linea.codlinea" : 	{required: true, digits: true},		 	
   				 	"pctmediador" : {required: true,range: [0,100]},
   				    "fecEfecto":{ required: true, dateITA: true, comprobarFechaE: true}
				 },
				 messages: {
				 	"subentidadMediadora.id.codentidad":{required: "El campo Entidad mediadora es obligatorio", digits: "El campo Entidad mediadora sólo puede contener dígitos", minlength: "El campo Entidad mediadora debe contener 4 dígitos",range: "Entidad mediadora no válida"},
				 	"subentidadMediadora.id.codsubentidad":{required: "El campo Subentidad mediadora es obligatorio", digits: "El campo Subentidad mediadora sólo puede contener dígitos",range: "Subentidad mediadora no válida"},
				 	"linea.codplan" : 	{required: "El campo Plan es obligatorio.",digits: "El campo Plan solo admite dígitos.",minlength: "El campo Plan debe contener 4 dígitos"},		 	
   				 	"linea.codlinea" : 	{required: "El campo Línea es obligatorio.",digits: "El campo Línea solo admite dígitos."},		 	
   				 	"pctmediador" : {required: "El campo % E-S Mediadora es obligatorio.",range: "El campo % E-S Mediadora debe contener un número entre 0 y 100"},
   				    "fecEfecto":{required: "El campo Fecha Efecto es obligatorio.",dateITA : "El formato del campo Fecha Efecto es dd/mm/YYYY",comprobarFechaE:"La Fecha Efecto debe ser superior o igual a la fecha actual"}
				 }
		 	});
		 	
		 	
		 	
		 	//comprueba la fecha inicial con la del sistema
		 	//element--> la fechaIni
		 	jQuery.validator.addMethod("comprobarFechaE", function(value, element) {					
		 		return (this.optional(element) || UTIL.fechaMayorOIgualQueFechaActual(element.value));
		 	});
		 	
		 	
		 	
		 	<c:if test="${activarModoModificar == 'true'}">	
				$('#method').val("doEdita");
				$("#btnAlta").hide();
				$("#btnModif").show();
				$('#txt_porcentajeMediador').attr("readonly", true);
			</c:if>		
		 	
		});
		
		function validarCamposConsulta () {

			// entmediadora
			if ($('#entmediadora').val() != ''){ 
			 	var entidadOk = false;
			 	try {		 	
			 		var auxEntidad =  parseFloat($('#entmediadora').val());
			 		if(!isNaN(auxEntidad)){
						$('#entmediadora').val(auxEntidad);
						entidadOk = true;
					}
				}
				catch (ex) {}
				
				// Si ha habido error en la validación muestra el mensaje
				if (!entidadOk) {
					$('#panelAlertasValidacion').html("Valor para la Entidad Mediadora no válido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// subentidad
			if ($('#subentmediadora').val() != ''){ 
			 	var subentmediadoraOk = false;
			 	try {		 	
			 		var auxsubentmediadora =  parseFloat($('#subentmediadora').val());
			 		if(!isNaN(auxsubentmediadora)){
						$('#subentmediadora').val(auxsubentmediadora);
						subentmediadoraOk = true;
					}
				}
				catch (ex) {}
				
				// Si ha habido error en la validación muestra el mensaje
				if (!subentmediadoraOk) {
					$('#panelAlertasValidacion').html("Valor para la SubEntidad Mediadora no válido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// PLAN
		 	if ($('#plan').val() != ''){
			 	var planOk = false;
			 	try {		 	
			 		var auxPlan =  parseFloat($('#plan').val());
			 		if(!isNaN(auxPlan) && $('#plan').val().length == 4){
						$('#plan').val(auxPlan);
						planOk = true;
					}
				}
				catch (ex) {}
				// Si ha habido error en la validación muestra el mensaje
				if (!planOk) {
					$('#panelAlertasValidacion').html("Valor para el plan no válido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// LINEA
			if ($('#linea').val() != ''){ 
			 	var lineaOk = false;
			 	try {		 	
			 		var auxLinea =  parseFloat($('#linea').val());
			 		if(!isNaN(auxLinea)){
						$('#linea').val(auxLinea);
						lineaOk = true;
					}
				}
				catch (ex) {}
				// Si ha habido error en la validación muestra el mensaje
				if (!lineaOk) {
					$('#panelAlertasValidacion').html("Valor para la línea no válido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			// % E-S Mediadora 
			if ($('#txt_porcentajeMediador').val() != '' ){ 
			 	var pctmediadorOk = false;
			 	try {		 	
			 		var auxpctmediador =  parseFloat($('#txt_porcentajeMediador').val());
			 		if(!isNaN(auxpctmediador)){
						$('#txt_porcentajeMediador').val(auxpctmediador);
						pctmediadorOk = true;
					}
				}
				catch (ex) {}
				// Si ha habido error en la validación muestra el mensaje
				if (!pctmediadorOk) {
					$('#panelAlertasValidacion').html("Valor para la % E-S Mediadora no válido");
					$('#panelAlertasValidacion').show();
					return false;
				}
			}
			return true;
		}

		function cerrarPopUp(){
			$('#divAviso').fadeOut('normal');
			$('#divReplicar').fadeOut('normal');
			$('#overlay').hide();
		}
		
		/** si la entidad mediadora es la 8000 se pone el % de la E-S Mediadora a 100 **/
		function entidad8XXX(){
			var value = $('#entmediadora').val();
			if( value.substr(0,2) == "80"){
				$('#pctmediador').val('100');
			}
		}
		/** pone el valor % E-S Mediadora en el filtro, pero si la entidad es la 8000 pone
			siempre 100 y no deja editarlo */
		function inicializarMediador(pctmed){
			var entMediadora = $('#entmediadora').val();
			$('#txt_porcentajeMediador').val(pctmed);
			$('#txt_porcentajeMediador').attr("readonly", false);
			if (entMediadora.length == 4){
				if (entMediadora.charAt(0) == '8'){
					$('#txt_porcentajeMediador').val('100');
					$('#txt_porcentajeMediador').attr("readonly", true);
				} 
			}
		}
		/** modifica un registo */
		function modificar(id,plan,linea,entidad,subentidad,pctmed,usuario,desc_entidad,
				desc_subentidad,fechaEfecto,nomLinea){
			$('#id').val(id);
			$('#plan').val(plan);
			$('#linea').val(linea);
			$('#entmediadora').val(entidad);
			$('#desc_entmediadora').val(desc_entidad);
			$('#desc_subentmediadora').val(desc_subentidad);
			$('#subentmediadora').val(subentidad);
			$('#txt_porcentajeMediador').val(pctmed);
			$('#usuario').val(usuario);
			$('#fecEfecto').val(fechaEfecto);
			$('#desc_linea').val(nomLinea);
			inicializarMediador(pctmed);
			
			//BOTONES
			$('#btnAlta').hide();
			$('#btnModif').show();			
		}
		
		function consultar(){
			$("#main3").validate().cancelSubmit = true;
			if (validarCamposConsulta()){
				$('#consultando').val("consultando");
				$('#id').val('');
				$('#method').val('doConsulta');			
				$('#main3').submit();
			}
		}
		
		/** limpia los campos del formulario **/
		function limpiar(){
			var fecha = new Date();
			var year = fecha.getFullYear();

			$('#id').val('');
			$('#plan').val(year);
			$('#linea').val('');
			$('#desc_linea').val('');
			$('#entmediadora').val('');
			$('#desc_subentmediadora').val('');
			$('#subentmediadora').val('');
			$('#txt_porcentajeMediador').val('');
			$('#usuario').val('');
			$('#subentmediadora').val('');
			$('#fecEfecto').val('');
			$("#main3").validate().cancelSubmit = true;
			$('#method').val('doConsulta');			
			$('#main3').submit();
		}
		
		/** Realiza el alta de comision por e-s med**/
		function alta(){
			
			if ($('#main3').valid()){
				$('#id').val('');
				existeComisionMaxima ($('#plan').val(),$('#linea').val())
			}
		}
		
		/** Modifica la comision de la e-s med **/
		function editar(){
			if ($('#main3').valid()){
				existeComisionMaxima ($('#plan').val(),$('#linea').val())
			}	
		}
		
		/** comprueba si en la tabla de parametros generales existe plan/linea antes de
		 dar de alta o de modificar **/
		function existeComisionMaxima(plan, linea){
			
			if (linea != 999){ // Para la linea 999 no se comprueba.Se permite
				$.ajax({
					    url: "comisionesCultivos.html?method=doExisteComisionMaxima&idplan="+plan+"&idlinea="+linea,
						data: "",
						async:true,
					    dataType: "json",
					    success: function(datos){
					    	if(datos.resultado == "OK"){
					    		$('#method').val('doGuardarDistribucionMediadores');
								$('#main3').submit();
					    			          				            
					        }else if (datos.resultado == "KO"){
					        	jAlert("No se encuentra el parámetro general",'Error');
					        	
					        }else if (datos.resultado == "ERROR"){
					        	jAlert("Error al comprobar si el plan/linea existe en la tabla Parámetros Generales",'Error');
					        }	 		   		
					    },
					    error: function (){
					    	jAlert("Error al comprobar si el plan/linea existe en la tabla Parámetros Generales",'Error');
					    },
					    beforeSend: function(){
				           		
	            		},
	            		complete: function(){
	            				            					
	            		},				           
					    type: "POST"
					});
			}else{
				$('#method').val('doGuardarDistribucionMediadores');
				$('#main3').submit();
			}
		}
		
		function borrar(id){
			$("#main3").validate().cancelSubmit = true;
			
			jConfirm('¿Está seguro de que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
				if (r){
					$('#method').val('doBorrarDistribucionMediadores');
					$('#id').val(id);
					$('#main3').submit();
				}
			});
			
		}
		
		function abrirReplicar(){
			
			$('#panelAlertasValidacion2').hide();
			$('#lineaorigen').val($('#lineaorigenH').val());
			$('#lineanuevo').val($('#lineanuevoH').val());
			$('#divReplicar').fadeIn('normal');
			$('#overlay').show();
		}
		
		function limpiarReplicar() {
			$('#plan_re').val('');
			$('#linea_re').val('');
			$('#desc_linea_re').val('');
			$('#planreplica').val('');
			$('#lineareplica').val('');
			$('#desc_lineareplica').val('');
			
			$('#txt_mensaje_re').html("");
		}
		
		function replicar(){
			// Se comprueba que el plan y linea a replicar no son vacios antes de llamar al servidor
			// Si son vacios, se ha hecho click en un elemento de la lupa que no es un registro (ordenacion, etc.)
			if ($('#planreplica').val() != '' && $('#lineareplica').val() != '' && $('#linea_re').val() != '' && $('#plan_re').val() != '') {

				jConfirm('\u00BFDesea replicar todas las Comisiones para este Plan y L\u00EDnea?', 'Diálogo de Confirmación', function(r){
					if(r){
						// Valida que el plan/linea origen y destino no son iguales
						if (replicaPlanLineaDiferentes($('#planreplica').val(),$('#plan_re').val(), $('#lineareplica').val(),$('#linea_re').val() )) {
							
							var frm = document.getElementById('replicar');
							
				 			if (document.getElementById('tipoFichero')!= null)
				 			{
				 				var tipo = document.getElementById('tipoFichero').value;
				 				$('#tipoFichero').val(tipo);
				 			}
							
				 			if (document.getElementById('idFichero') != null){
				 				var idFichero = document.getElementById('idFichero').value;
				 				$('#idFichero').val('idFichero');	
				 			}
							
							$('#plan_orig').val($('#plan_re').val());	
							$('#linea_orig').val($('#linea_re').val());
							$('#plan_dest').val($('#planreplica').val());
							$('#linea_dest').val($('#lineareplica').val());
							
							/* ESC-17100 ** MODIF TAM (08/02/2022) ** Inicio */
							/* Pasamos los datos del filtro actual para restaurarlos despues de la replica */
							$('#plan_filtro').val(document.getElementById('plan').value);
							$('#linea_filtro').val(document.getElementById('linea').value);
							$('#entidad_filtro').val(document.getElementById('entmediadora').value);
							$('#subentidad_filtro').val(document.getElementById('subentmediadora').value);
							/* ESC-17100 ** MODIF TAM (08/02/2022) ** Fin */
							
							frm.method.value= "doReplicar";
							
							$("#replicar").submit();
						}else {
							$('#panelAlertasValidacion').html("El plan/linea origen no puede ser igual que el destino para la Replica");
							$('#panelAlertasValidacion').show();
							// cerramos la ventana de Replica
							cerrarPopUp();
						}
					}
				});
			}else{
				$('#panelAlertasValidacion').html("Debe Insertar valores en Plan/Linea Destino y Plan/Linea Origen para la Replica.");
				$('#panelAlertasValidacion').show();
				// cerramos la ventana de Replica
				cerrarPopUp();
			}

		}
		
		function replicaPlanLineaDiferentes (planReplica, plan, lineaReplica, linea) {	
			if (planReplica == plan && lineaReplica == linea){ 
				return false
			} else { 
				return true; 
			}
		}
		
		function validaCamposLinea(){
			if ($('#lineaorigen').val() != ''){
			 	var lineaorigenOk = false;
			 	try {		 	
			 		var auxlineaorigen =  parseFloat($('#lineaorigen').val());
			 		if(!isNaN(auxlineaorigen) && $('#lineaorigen').val().length == 4){
						$('#lineaorigen').val(auxlineaorigen);
						lineaorigenOk = true;
					}
				}
				catch (ex) {}
				// Si ha habido error en la validación muestra el mensaje
				if (!kOk) {
					$('#panelAlertasValidacion2').html("Valor para la l&iacute;nea Origen no válido");
					$('#panelAlertasValidacion2').show();
					return false;
				}
			}else{
				$('#panelAlertasValidacion2').html("La l&iacute;nea Origen es obligatorio");
				$('#panelAlertasValidacion2').show();
				return false;
			}
			if ($('#lineanuevo').val() != ''){
			 	var lineanuevoOk = false;
			 	try {		 	
			 		var auxlineanuevo =  parseFloat($('#lineanuevo').val());
			 		if(!isNaN(auxlineanuevo) && $('#lineanuevo').val().length == 4){
						$('#lineanuev1').val(auxlineanuevo);
						lineanuevoOk = true;
					}
				}
				catch (ex) {}
				// Si ha habido error en la validación muestra el mensaje
				if (!lineanuevoOk) {
					$('#panelAlertasValidacion2').html("Valor para la l&iacute;nea Nueva no válido");
					$('#panelAlertasValidacion2').show();
					return false;
				}
			}else{
				if (!lineanuevoOk) {
					$('#panelAlertasValidacion2').html("La l&iacute;nea Nueva es obligatoria");
					$('#panelAlertasValidacion2').show();
					return false;
				}
			}
			return true;
		}
		
		
		function setOperacion(){
			$('#operacion').val('cambioPctMediador');
		}
		
		function consultarHistorico (id,codEnt,codSubEnt,lineaSeguroId,linea,plan,pctmediador,desc_subentmediadora){
			$("#main3").validate().cancelSubmit = true;
			$('#id').val(id);
			$('#entmediadoraH').val(codEnt);
			$('#subentmediadoraH').val(codSubEnt);
			$('#desc_subentmediadoraH').val(desc_subentmediadora);
			$('#lineaSeguroId').val(lineaSeguroId);
			$('#planH').val(plan);
			$('#lineaH').val(linea);
			$('#txt_porcentajeMediadorH').val(pctmediador);
			$('#method').val ("doConsultarHistorico");
			$('#main3').submit();
		}
		function verDetalleLinea (codEnt,codSubEnt,lineaseguoId,plan,codLin){
			$('#esMedP').html(codEnt+"-"+codSubEnt);
			$.ajax({
				    url: "comisionesCultivos.html?method=doVerDetalle&idplan="+plan +"&ent="+codEnt+"&subEnt="+codSubEnt+"&lineaseguoId="+lineaseguoId+"&codLin="+codLin,
					data: "",
					async:true,
				    dataType: "json",
				    error: function(objeto, quepaso, otroobj){
		                alert("Error al cargar el detalle por líneas: " + quepaso);
		            },
		            success: function(datos){
		 				pintarTabla(datos.listDetallePct);
		            	$('#listDetallePct').val(datos.listDetallePct);
				    	$('#panelDetalleLineas').show();
						$('#overlay').show(); 		
		            },
		            type: "GET"
				    
				});
		}
		/* recorremos los datos y generamos la tabla dinamicamente para 
		pasarsela al popup*/ 
		function pintarTabla(listDetallePct){
			var reg ="";
			
			
			var tablaIni = "<table style='width:90%;border-collapse:collapse;' class='LISTA'> "+
			"<tr> "+
				"<th class='cblistaImg'> Plan </th> "+
				"<th class='cblistaImg'> Línea </th> "+
				"<th class='cblistaImg'> G.N. </th> "+
				"<th class='cblistaImg'> % Entidades </th> "+
				"<th class='cblistaImg'> % E-S Mediadora </th> "+
			"</tr> ";
			
			var tablaFin = "</table>"
			
			for (var i = 0; i < listDetallePct.length; i++){
				var item = listDetallePct[i];
				
				reg = reg +"<tr> " +
							"<td style='text-align:center' class='literal'>" + item.codplan +"</td>" + 
							"<td style='text-align:center' class='literal'>" + item.codlinea +"</td>" +
							"<td style='text-align:center' class='literal'>" + item.descripcionGN +"</td>" +
							"<td style='text-align:center' class='literal'>" + item.pctEntidad +"% <span style='color:green'>&nbsp;&nbsp;("+ item.pctEntAux+ "%)</span></td>"+
							"<td style='text-align:center' class='literal'>" + item.pctMediador +"% <span style='color:green'>&nbsp;&nbsp;("+ item.pctEsMedAux+ "%)</span></td></tr> ";
			}
			
			var tablaCon = tablaIni + reg + tablaFin;
			$('#datosPop').html(tablaCon);
			
		}
		
		function returnBack()
		
		{ 
			if ($('#procedencia').val() == ''){
				
				limpiar();
			}else{
				<c:if test="${procedencia eq 'incidenciasComisionesUnificadas'}" >
					 $(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
								'&origenLlamada=comisiones'+
								'&method=doConsulta');	
				</c:if>	
				<c:if test="${procedencia ne 'incidenciasComisionesUnificadas'}" >
							$(window.location).attr('href', 'incidencias.html?rand=' + 
									UTIL.getRand() + '&idFichero='+$('#idFicheroComisiones').val()+
									'&tipo='+$('#tipoFicheroComisiones').val()+ 
									'&codplan='+$('#plan').val()+
									'&method=doConsulta');	
				</c:if>	
			}		
		}
		
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="center">
				<td align="left">
					&nbsp;<a class="bot" id="btnReplicar"  href="javascript:abrirReplicar();">Replicar Plan/L&iacute;nea</a>
				</td>		
				<td align="right">			
					<a class="bot" id="btnAlta"  href="javascript:alta();">Alta</a>				
					<a class="bot" id="btnModif" style="display:none" href="javascript:editar();">Modificar</a>										
					<a class="bot" id="btnConsultar"  href="javascript:consultar();">Consultar</a>				
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>	
					
					<c:if test="${tipoFichero!= '' && idFichero!= ''}" >
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if>	
						
					 <%--<c:if test="${(tipoFichero!= null && idFichero!= null) || (tipoFichero!= '' && idFichero!= '') }" >
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if> --%>		
					
					<%--<c:if test="${procedencia eq 'incidenciasComisiones'}" >
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if>--%>
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Mantenimiento de comisiones por E-S Mediadora</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<form:form id="main3" name="main3" method="post" action="comisionesCultivos.html" commandName="cultivosSubentidadesBean">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="operacion" id="operacion"/>
			<input type="hidden" name="idCultivosEntidades" id="idCultivosEntidades" value="${cultivosEntidades.id }"/>
			<form:hidden path="id" id="id"/>
			<form:hidden path="usuario.codusuario" id="usuario"/>
			<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
			<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
			<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
			<input type="hidden" id="limpiar" name="limpiar"/>		
			<input type="hidden" name="entidad2" id="entidad" value=""/>	
			<input type="hidden" id="fecEfecto.day" name="fechae.day" value="">
			<input type="hidden" id="fecEfecto.month" name="fechae.month" value="">
			<input type="hidden" id="fecEfecto.year" name="fechae.year" value="">
			<input type="hidden" id="entmediadoraH" name="entmediadoraH"/>
			<input type="hidden" id="subentmediadoraH" name="subentmediadoraH"/>
			<input type="hidden" id="desc_subentmediadoraH" name="desc_subentmediadoraH"/>
			<input type="hidden" id="planH" name="planH"/>
			<input type="hidden" id="lineaH" name="lineaH"/>
			<input type="hidden" id="txt_porcentajeMediadorH" name="txt_porcentajeMediadorH"/>
			<input type="hidden" id="filtraPlan" name="filtraPlan"/>
			<input type="hidden" name="procedencia"  id="procedencia" value="${procedencia}"/>
			<input type="hidden" name="consultando"  id="consultando" value="${consultando}"/>
			
			
			<div style="panel2 isrt">
				<fieldset style="width:95%; margin:0 auto;">
					<legend class="literal">Distribución Mediadores</legend>
					<table style="margin:0 auto;">
						<tr align="center">
							<td class="literal" colspan="2">Entidad mediadora
							
								<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');setOperacion();" />
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:setOperacion();lupas.muestraTabla('EntidadMediadora','principio', '', '');"	alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
								<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" colspan="4"> Subentidad mediadora
							
								<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');"/>
								<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
							</td>
						</tr>
						<tr>
							<td class="literal" align="center">Plan
							
							
								<form:input path="linea.codplan" id="plan" size="5" maxlength="4" cssClass="dato" />
								<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio">*</label>
							</td>
							<td class="literal">Línea
							
								<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea"  onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio" id="campoObligatorio_linea" title="Campo obligatorio">*</label>
							</td>
							<form:hidden path="linea.lineaseguroid" id ="lineaSeguroId" />
							<td class="literal">% E-S Mediadora
							
								<form:input	path="pctmediador" size="4" maxlength="5" cssClass="dato" id="txt_porcentajeMediador" onfocus="entidad8XXX();" onchange="this.value = this.value.replace(',', '.')"/>
								<label class="campoObligatorio" id="campoObligatorio_txt_porcentajeMediador" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" align="right" >Fecha Efecto
	                        
			                    <spring:bind path="fecEfecto">
			                    	 <input type="text" name="fecEfecto" id="fecEfecto" size="11" maxlength="10" class="dato" 
			                    	  onchange="if (!ComprobarFecha(this, document.main3, 'Fecha efecto')) this.value='';"
			                    	 		value="<fmt:formatDate pattern="dd/MM/yyyy" value="${cultivosSubentidadesBean.fecEfecto}" />" />
			                    </spring:bind>
		                     <input type="button" id="btn_fechaEfecto" name="btn_fechaEfecto" class="miniCalendario" style="cursor: pointer;" /> 
		                     <label class="campoObligatorio" id="campoObligatorio_fechae" title="Campo obligatorio">*</label>
	                         </td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="distMediadores" 
					pagesize="${numReg}" name="${listCultivosSubentidades}" id="distMediadores" excludedParams="method"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorComisionesCultivoMediadores" 	sort="list"
					style="width:90%;border-collapse:collapse;">
					
					
					<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" 	title="Acciones"	property="admActions"    style="width:80px;text-align:center" sortable="false"/>
					<display:column class="literal" headerClass="cblistaImg" title="Entidad Med."   				property="entidad"       style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Subentidad Med."   				property="subentidad"    style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorSubEntidadMediadora"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan"   						property="plan"          style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea"            				property="linea"  		 style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Entidades"  					property="pctent"    	 style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="% E-S Mediadora"            	property="pctmed"    	 style="text-align:center" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Efecto"      		        property="fechaEfecto" 		 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Baja"      		        property="fecBaja" 		 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
			</display:table>
		</div>
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresDestinoWS.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresOrigenWS.jsp"%>
	<!-- panel avisos -->
	<div id="divAviso" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
       <!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
		                                  background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Aviso
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="cerrarPopUp()">x</span>
		        </a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_info" style="width: 70%" >Por cada plan,debe introducir una distribución para la línea genérica 999.</div>
				</div>
		</div>
	</div>
	<!-- panel replicacion -->
	<div id="divReplicar" class="wrapper_popup" style="left: 25%; width: 55%; color:#333333; z-index: 1005; -moz-border-radius: 4px 4px 4px 4px; padding: 0.9em;">
		 <!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" >Replicar Comisiones</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				  <span onclick="javascript:cerrarPopUp()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion" >
				<div id="txt_mensaje_re" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:40px;"></div>
				<form action="comisionesCultivos.html" id="replicar" name="replicar" method="post">
					<input type="hidden" name="method" id="methodReplicar"/>		
					<input type="hidden" name="idFichero" id="idFichero" value="${idFichero}"/>
					<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero}"/>
					<input type="hidden" name="lineaorigenH" id="lineaorigenH" value="${lineaorigen}"/>
					<input type="hidden" name="lineanuevoH" id="lineanuevoH" value="${lineanuevo}"/>
					
					<input type="hidden" name="plan_orig" id="plan_orig" value=""/>
					<input type="hidden" name="linea_orig" id="linea_orig" value=""/>
					<input type="hidden" name="plan_dest" id="plan_dest" value=""/>
					<input type="hidden" name="linea_dest" id="linea_dest" value=""/>
					
					<!--  ESC-17100 ** MODIF TAM (08.02.2022) ** Inicio  -->
					<input type="hidden" name="plan_filtro" id="plan_filtro" value="" />
					<input type="hidden" name="linea_filtro" id="linea_filtro" value="" />
					<input type="hidden" name="entidad_filtro" id="entidad_filtro" value="" />
					<input type="hidden" name="subentidad_filtro" id="subentidad_filtro" value="" />
					<!--  ESC-17100 ** MODIF TAM (08.02.2022) ** Fin  -->
												   
					<div>
						<fieldset class="panel2 isrt">
							<legend class="literal">Origen</legend>
							<table>
								<tr>
									<td class="literal">Plan</td>
									<td class="literal" style="padding:0.5em 1em" >
										<input class="dato" name="plan_re" size="5" maxlength="4" id="plan_re" tabindex="1" />
									</td>
									<td class="literal">Línea</td>
									<td class="literal" nowrap style="padding:0.5em 1em">
										<input class="dato" name="linea_re" size="3" maxlength="3" id="linea_re" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_linea_re');"/>
										<input class="dato" id="desc_linea_re" size="40" disable="disable"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaReplicaOrigen','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
					<div>
						<fieldset class="panel2 isrt">
							<legend class="literal">Destino</legend>
							<table>
								<tr>
									<td class="literal">Plan</td>
									<td class="literal" style="padding:0.5em 1em">
										<input class="dato" size="5" maxlength="4" id="planreplica" tabindex="1" />
									</td>
									<td class="literal">Línea</td>
									<td class="literal" nowrap style="padding:0.5em 1em">
										<input class="dato" size="3" maxlength="3" id="lineareplica" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_lineareplica');"/>
										<input class="dato" id="desc_lineareplica" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaReplica','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />	
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
				</form>	
			</div>
			<div style="margin-top:15px">
			    <a class="bot" href="javascript:limpiarReplicar()" title="Limpiar">Limpiar</a>
			    <a class="bot" href="javascript:cerrarPopUp()" title="Cancelar">Cancelar</a>
			    <a class="bot" href="javascript:replicar()" title="Aplicar">Aplicar</a>
			</div>
		</div>
	</div>
 	<form name="revisarForm" id="revisarForm" action="incidencias.html">
		<input type="hidden" name="method" id="methodVolver"/>
		<input type="hidden" id="revisar_idFichero" name="idFichero" /> 
		<input	type="hidden" id="revisar_tipoFichero" name="tipo" /> 
	</form>
</body>
<%@ include file="/jsp/moduloComisiones/popupDetalleLineas.jsp"%>
</html>