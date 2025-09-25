function cambiarOficina(){}
		
$(document).ready(function(){

	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#mainForm").attr("action"));
	$("#mainForm").attr("action", URL);
	
	$('#mainForm').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		onfocusout: function(element) {
			console.log('OnFocus!!!', $('#mainForm #method').val())
   				if(
   						($('#mainForm #method').val() == "doAlta" || $('#mainForm #method').val() == "doEdita") &&
   						!this.checkable(element) && 
   						(element.name in this.submitted || !this.optional(element)) 
   						) 
   				{ console.log(element); this.element(element); }
		},  					 
   		highlight: function(element, errorClass) {
			$("#campoObligatorio_"+element.id).show();
  		},
  		unhighlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).hide();
  		},
		rules: {				 	
			"id.codFamilia": {required: true,digits: true,range: [0,999]},
			"familia.nomFamilia": {required: true, alphanumeric: true, maxlength: 255},
			"id.codGrupoFamilia": {required: true},
			"id.grupoNegocio": {required: true},
			"id.codLinea": {required: true}
		},
		messages: {
			"id.codFamilia": {required: "El campo codigo de familia es obligatorio.", digits: "El campo Codigo de familia sólo puede contener dígitos.", range: "Codigo de familia no válida."},
			"familia.nomFamilia": {required: "El campo nombre de familia es obligatorio.", alphanumeric: "El campo Nombre familia solo puede contener valores alfanuméricos.", maxlength: 'El campo Nombre familia supera la longitud máxima de 255 carácteres.'},
			"id.codGrupoFamilia": {required: "El campo grupo es obligatorio"},
			"id.grupoNegocio": {required: "El campo grupoNegocio es obligatorio"},
			"id.codLinea": {required: "El campo linea es obligatorio"}
		}			
	});		
	

	jQuery.validator.addMethod("alphanumeric", function(value, element) {
	    return this.optional(element) || /^[a-zA-Z0-9\s]+$/.test(value);
	});
	
});		


function modificar (codFamilia,nomFamilia,grupo,grupoNegocio,linea,nomLinea){
	
	    $("#panelAlertasValidacion").hide();
					
		$("#codFamilia").val(codFamilia);
		$("#nomFamilia").val(nomFamilia);
		$("#grupo").val(grupo);
		$("#grupoNegocio").val(grupoNegocio);
		$("#lineaCondicionado").val(linea);
		$("#nomLineaCondicionado").val(nomLinea);

		$("#codFamiliaInicial").val(codFamilia);
		$("#grupoInicial").val(grupo);
		$("#grupoNegocioInicial").val(grupoNegocio);
		$("#lineaInicial").val(linea);

		//BOTONES
		$('#btnModificar').show();
}

function consultar(){
	$("#mainForm").validate().cancelSubmit = true;
	$('#mainForm #method').val("doConsulta");
	$('#mainForm').submit();			
}   

function limpiarFiltro(){
	
	$("#codFamilia").val('');
	$("#nomFamilia").val('');
	$("#grupo").val('');
	$("#grupoNegocio").val('');
	$("#lineaCondicionado").val('');
	$("#nomLineaCondicionado").val('');

	$("#codFamiliaInicial").val('');
	$("#grupoInicial").val('');
	$("#grupoNegocioInicial").val('');
	$("#lineaInicial").val('');

	consultar();
}




function baja(codFamilia, codGrupoFamilia, grupoNegocio, codLinea){
	$("#frmBorrar").validate().cancelSubmit = true;
	$('#codFamiliaBorrar').val(codFamilia);
	$('#codLineaBorrar').val(codLinea);
	$('#codGrupoFamiliaBorrar').val(codGrupoFamilia);
	$('#grupoNegocioBorrar').val(grupoNegocio);
	

	jConfirm('¿Está seguro de que desea eliminar el registro seleccionado?','Diálogo de Confirmación', function(r) {
		if (r==true){
			$('#frmBorrar').submit();
		}
	});

}



function alta(){
	limpiaAlertas();
	$("#panelAlertasValidacion").hide();
	// TODO validaciones?
	$('#mainForm #method').val("doAlta");
	$('#mainForm').submit();
}


function editar(){
	limpiaAlertas();
	$("#panelAlertasValidacion").hide();
	$('#panelInfo_adicional').hide();
	$('#mainForm #method').val("doEditar");
	$('#mainForm').submit();
	
}



function limpiaAlertas() {
		$('#alerta').val("");
		$("#panelInformacion").hide();
		$("#panelAlertasValidacion").hide();
		$("#panelAlertas").hide();
		$("#panelInformacion").html('');
		$("#panelAlertasValidacion").html('');
		$("#panelAlertas").html('');
		$('#mensaje').val("");
		$("#panelMensajeValidacion").hide();
	    $("#panelMensajeValidacion").html('');
}	 
	 

