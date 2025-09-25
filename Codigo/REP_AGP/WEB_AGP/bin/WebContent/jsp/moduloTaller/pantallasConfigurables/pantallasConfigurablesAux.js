// 23/03/2015 pantallasConfigurablesAux.js
$(document).ready(function(){
	
	$("#btnModificar").hide();
	$('#btnReplicar').hide();

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
		  "linea.codplan": {required: true, digits: true, minlength: 4},			 
		  "linea.codlinea": {required: true, digits: true},
		  "pantalla.idpantalla": {required: true}
		},
		messages: {
			"linea.codplan": {required: "El campo Plan es obligatorio.", digits: "El campo Plan solo admite dígitos.", minlength: "El campo Plan debe contener 4 dígitos"},
			"linea.codlinea": {required: "El campo Línea es obligatorio.", digits: "El campo Línea solo admite dígitos."},
			"pantalla.idpantalla": {required: "El campo Pantalla es obligatorio."}
		}
	});
	
	if($("#id").val() != ""){
			
		$('#operacion').val("modificar");
		$('#btnAlta').hide();
		$('#btnModificar').show();
		 				
	} 
});    	 

function limpiar(){
	
	$('#panelAlertasValidacion').html('');
	$("#panelAlertasValidacion").hide();
	$("#sl_planes").val('');
	$("#sl_lineas").val('');
	$("#desc_linea").val('');
	$("#sl_pantallas").selectOptions("");
	$('#operacion').val("inicializar");
	$("#main").validate().cancelSubmit = true;
	$('#main').submit();
			
}
function consultar(){
		
		$('#id').val("");
		$('#operacion').val("consulta");
		
		var settings = $('#main').validate().settings;
		
		settings.rules['linea.codplan'] = {required: false, digits: true, minlength: 4};
		settings.rules['linea.codlinea'] = {required: false, digits: true};
		settings.rules['pantalla.idpantalla'] = {required: false};
		
		if( ($("#sl_planes").val() != "") || ($("#sl_lineas").val() != "") || ($("#sl_pantallas").val() != "")){
			$('#main').submit();
		}else{
			$('#panelAlertasValidacion').html('<li><label class="error">Debe completar al menos un campo.</label></li>');
			$('#panelAlertasValidacion').show();
		}
}
function editar(idPantallaConfigurable){

		$('#operacion').val("editar");
		$('#id').val(idPantallaConfigurable);
		$('#btnAlta').hide();
		$('#btnModificar').show();
		$('#btnReplicar').show();
		
		$.ajax({
			 	url: "pantallasConfigurables.html",
	            data: "operacion=editar&idPantallaConfigurable="+idPantallaConfigurable,
	            dataType: "json",
	            success: function(datos){
	            	$('#id').val(datos.idpantallaconfigurable);
	            	$("#sl_planes").val(""+datos.plan);
	            	$("#sl_lineas").val(""+datos.codlinea);
	            	$("#desc_linea").val(""+datos.nomlinea);
	            	$("#sl_pantallas").val(""+datos.pantalla);
	            },
	            type: "POST"
		});
}
function eliminar(idPantallaConfigurable){

		if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
			
			$("#main").validate().cancelSubmit = true;
			$('#id').val(idPantallaConfigurable);
			$('#operacion').val("baja");
			$('#main').submit();

		}
	
}
function alta(){
  $("#panelInformacion").hide();
  $('#id').val('');
  $('#operacion').val("alta");
  
	var settings = $('form').validate().settings;
	
	settings.rules['linea.codplan'] = {required: true, digits: true, minlength: 4};
	settings.rules['linea.codlinea'] = {required: true, digits: true};
	settings.rules['pantalla.idpantalla'] = {required: true};
  
  $('#main').submit();
}
function modificar(){		 
    $("#panelInformacion").hide();		        
    $('#operacion').val("modificacion");
    
    var settings = $('form').validate().settings;
    
	settings.rules['linea.codplan'] = {required: true, digits: true, minlength: 4};
	settings.rules['linea.codlinea'] = {required: true, digits: true};
	settings.rules['pantalla.idpantalla'] = {required: true};
	
    $('#main').submit();		
   }