function listaCheckId(id){
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena=[];
	
	if($('#check_' + id).attr('checked')==true){
		listaIdsMarcados = $('#listaIdsMarcados').val() + id +",";
		$('#listaIdsMarcados').val(listaIdsMarcados);
	}else{
		listaIdsMarcados = $('#listaIdsMarcados').val();
		cadena= listaIdsMarcados.split(",");
		
		for (var i=0;i<cadena.length -1;i++){
			if(cadena[i]!=id){
				listaFinalIds = listaFinalIds + cadena[i] + ",";
			}		
		}
		$('#listaIdsMarcados').val(listaFinalIds);
		$('#marcaTodos').val('false');
		comprobarChecks();	
	}
}

function comprobarChecks(){

	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena=[];
	cadena= listaIdsMarcados.split(",");

	if (listaIdsMarcados.length>0){
		$("input[type=checkbox]").each(function(){
			if( $("#marcaTodos").val() == "true" ){
				if($(this).attr('id') != "checkTodos"){
					$(this).attr('checked',true);
				}
			}
			else{
				for (var i=0;i<cadena.length -1;i++){
					var idcheck = "check_" + cadena[i];
					if($(this).attr('id') == idcheck){
						$(this).attr('checked',true);
					}
				}
			}
		});
	}
	if($('#marcaTodos').val()=="true"){
		$('#checkTodos').attr('checked',true);
	}else{
		$('#checkTodos').attr('checked',false);
	}
}

function cambioMasivo(){
	limpiarCambioMasivoUsuarios();
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	//alert(listaIdsMarcados.length);
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivoUsuarios').show();
	}
	else{	
		showPopUpAviso("Debe seleccionar como m\u00EDnimo un Usuario.");
		//$('#panelInformacion').hide();
	}	
}

function limpiarCambioMasivoUsuarios() {
	$('#tipousuario_cm').val('');
	$('#entidad_cm').val('');
	$('#desc_entidad_cm').val('');
	$('#oficina_cm').val('');
	$('#desc_oficina_cm').val('');
	$('#entmediadora_cm').val('');
	$('#subentmediadora_cm').val('');
	$('#desc_subentmediadora_cm').val('');
	$('#delegacion_cm').val('');
	$('#txt_mensaje_cm').hide();
	$('#cargaPac_cm').val('');
	$('#email_cm').val('');
	
	$('#financiar_cm').val('');
	$('#impMinFinanciacion_cm').val('');
	$('#impMaxFinanciacion_cm').val('');
	$('#fechaLimite_cm').val('');
}

//popupCambioMasivo
function cerrarCambioMasivoUsuarios(){
	limpiarCambioMasivoUsuarios();
	$('#panelCambioMasivoUsuarios').hide();
	$('#overlayCambioMasivo').hide();
}

//popup aviso
function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);
	
	$('#panelInformacion2').show();
	$('#popUpAvisos').show();
	$('#overlayCambioMasivo').show();
}
 function hidePopUpAviso(){
	$('#popUpAvisos').hide();
	$('#overlayCambioMasivo').hide();
}
 
 function comprobarEntidad(valor){
	 $('#txt_mensaje_cm').hide();
		if ($('#entidad_cm').val() != ''){
			if (valor == 0){
				lupas.muestraTabla('OficinaCM','principio', '', '');
			}else{
				UTIL.subStrEntidadCM();
				lupas.muestraTabla('EntidadMediadoraCM','principio', '', '');
			}
		}else{
			
			$('#txt_mensaje_cm').html("* Debe seleccionar previamente la Entidad");
			$('#txt_mensaje_cm').show();
		}
 }
 
 function comprobarEntidadMed(){
	 	
		if ($('#entmediadora_cm').val() != ''){
			UTIL.subStrEntidadCM();
			lupas.muestraTabla('SubentidadMediadoraFiltroFechaCM','principio', '', '');
		}else{
			
			$('#txt_mensaje_cm').html("* Debe seleccionar previamente la Entidad Mediadora");
			$('#txt_mensaje_cm').show();
		}
}
 
 function aplicarCambioMasivoUsuarios() {
	 if ($("#main").valid()) {
			if($('#tipousuario_cm').val() == '' && $('#entidad_cm').val() == '' && $('#oficina_cm').val() == '' &&
			   $('#entmediadora_cm').val() == '' && $('#subentmediadora_cm').val() == '' && $('#delegacion_cm').val() == '' &&
			   $('#cargaPac_cm').val() == '' && $('#email_cm').val() == '' && $('#financiar_cm').val() == '' &&
			   $('#impMinFinanciacion_cm').val() == '' && $('#impMaxFinanciacion_cm').val() == '' && $('#fechaLimite_cm').val() == '') {
				
				$('#txt_mensaje_cm').html("* Debe seleccionar al menos un cambio");
				$('#txt_mensaje_cm').show();
			}else {
						$('#tipousuario_cm_sel').val($('#tipousuario_cm').val());
						$('#entidad_cm_sel').val($('#entidad_cm').val());
						$('#oficina_cm_sel').val($('#oficina_cm').val());
						$('#entmed_cm_sel').val($('#entmediadora_cm').val());
						$('#submed_cm_sel').val($('#subentmediadora_cm').val());
						$('#delegacion_cm_sel').val($('#delegacion_cm').val());
						$('#cargaPac_cm_sel').val($('#cargaPac_cm').val());
						$('#email_cm_sel').val($('#email_cm').val());
							
						$('#financiar_cm_sel').val($('#financiar_cm').val());
						$('#impMinFinanciacion_cm_sel').val($('#impMinFinanciacion_cm').val());
						$('#impMaxFinanciacion_cm_sel').val($('#impMaxFinanciacion_cm').val());
						$('#fechaLimite_cm_sel').val($('#fechaLimite_cm').val());
				
				$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				      $.blockUI({
				          overlayCSS: { backgroundColor: '#525583'},
				          baseZ: 2000
				      });
				    	  $('#main').submit();
				cerrarCambioMasivoUsuarios();
			}
	}

}

 function marcarTodos(){
		if($('#checkTodos').attr('checked')==true){
			var listaIdsTodos = $("#listaIdsTodos").val();
			$('#listaIdsMarcados').val(listaIdsTodos);
			$('#marcaTodos').val('true');
			comprobarChecks();
		}
		else{
			$('#listaIdsMarcados').val('');
			$('#marcaTodos').val('false');
			$("input[type=checkbox]").each(function(){
				$(this).attr('checked',false);
			});
		}

	}
 
