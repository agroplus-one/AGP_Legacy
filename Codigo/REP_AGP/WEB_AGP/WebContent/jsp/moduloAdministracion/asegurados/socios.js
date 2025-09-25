$(document).ready(function(){
		
	if($('#displayPopUpPolizas').val() == "true"){
	    $('#overlay').show();
	    $('#popUpPolizasSinGrabar').show();
	}
		
	$('#main').validate({
		 errorLabelContainer: "#panelAlertasValidacion",
		 wrapper: "li",
		 
		 onfocusout: function(element) {
		 },
		 highlight: function(element, errorClass) {
			 	$("#campoObligatorio_"+element.id).show();
		 },
		 unhighlight: function(element, errorClass) {
			 	$("#campoObligatorio_"+element.id).hide();
		 },
		 rules: {	
			 "tipoidentificacion": {required: true},
			 "id.nif": {required: function(element){return $('#tipoIdentificacion').val() !='';}, CIFNIF: true},
	 		 "nombre": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
	 		 "apellido1": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
	 		 "apellido2": {letterswithwhitespace: true},
	 		 "razonsocial": {required: function(element){return $('#tipoIdentificacion').val() == 'CIF';},  lettersnumberswhitespacecolonpoint: true},
	 		 "numsegsocial": {required:false,minlength: 12,digits:true,obligatorioCondNSS:true},
		 	 "regimensegsocial": {obligatorioCondReg:true},
		 	 "atp":{validaATP:true}
		 	// "jovenagricultor":{validaJovenagricultor:true}
		},
 		messages: {
	 		"tipoidentificacion": {required: "El campo Tipo ident. es obligatorio"},
	 	 	"id.nif": {required: "El campo NIF/CIF/NIE Asegurado es obligatorio", CIFNIF: "El campo NIF/CIF/NIE Asegurado tiene un formato incorrecto"},
	 	 	"nombre": {required: "El campo Nombre es obligatorio", letterswithwhitespace: "El campo Nombre no puede contener dígitos ni caracteres especiales"},
	 	 	"apellido1": {required: "El campo Primer Apellido es obligatorio", letterswithwhitespace: "El campo 1er Apellido no puede contener dígitos ni caracteres especiales"},
	 	 	"apellido2": {letterswithwhitespace: "El campo 2º Apellido no puede contener dígitos ni caracteres especiales"},
	 	 	"razonsocial": {required: "El campo Razón Social es obligatorio", lettersnumberswhitespacecolonpoint: "El campo Razón Social no puede contener caracteres especiales"},
	 	 	"numsegsocial": {minlength: "El campo Nº S.S. debe contener 12 dígitos",digits:"El número de S.S solo admite dígitos",obligatorioCondNSS:"El campo Nº S.S. es obligatorio"},
		 	"regimensegsocial": {obligatorioCondReg: "El campo Ind.régimen es obligatorio"},
		 	"atp":{validaATP:"El campo ATP es obligatorio"}
		 	//"jovenagricultor":{validaJovenagricultor:"El campo J. Agricultor/a es obligatorio"}
 		}
 	});
	
	jQuery.validator.addMethod("obligatorioCondNSS", function(value, element, params) {
		if($('#atp').val() == "S"){
			if($('#numsegsocial').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	});	
	jQuery.validator.addMethod("validaATP", function(value, element, params) {
		if($('#tipoIdentificacion').val() == "NIF" || $('#tipoIdentificacion').val() == "NIE"){
			if($('#ATP').val() == "" ){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	});
	jQuery.validator.addMethod("obligatorioCondReg", function(value, element, params) {
		
		if($('#numsegsocial').val() != ""){
			if($('#regimensegsocial').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	});
	if($("#idSocio").val() != ""){ 
		$('#operacion').val("modificar");
		$("#btnAlta").hide();
		$("#btnModificar").show();
	}
});
jQuery.validator.addMethod("CIFNIF", function(value, element) {
	return (this.optional(element) || generales.validaCifNif($("#tipoIdentificacion").val(), value));
});
				
			
function modificar(idAsegurado, cifnif, tipoIdent, nombre, apellido1, apellido2, razonSocial, ss, indRegimen,
					atp, jovenAgricultor)
{
	var frm = document.getElementById('main');
	frm.idAseguradoBaja.value = idAsegurado;
	frm.nifcif.value = cifnif;
	marcaCombo (frm.tipoIdentificacion, tipoIdent);
	frm.nombre.value = nombre;
	frm.apellido1.value = apellido1;
	frm.apellido2.value = apellido2;
	frm.razonsocial.value = razonSocial;
	frm.numsegsocial.value = ss;
	marcaCombo (frm.regimensegsocial, indRegimen);
	frm.atp.value = atp;
	marcaCombo (frm.jovenagricultor, jovenAgricultor);
	generales.botonesModificacion();
	$("#btnConsultar").show();
	
	generales.cifnifSeleccionado();
}

function marcaCombo (combo, valor)
{
	for (var i=0; i<combo.length; i++)
	{
		if (combo[i].value == valor)
		{
			combo[i].selected = 'true';
		}					
	}
}

function baja (idAsegurado, nif)
{
	var frm = document.getElementById('main');
	jConfirm('¿Está seguro de que desea eliminar el registro seleccionado?', 'Diálogo de Confirmación', function(r) {
		if (r){
			frm.target="";
			$('#idAseguradoBaja').val(idAsegurado);
			$('#nifcifBaja').val(nif);
			$('#method').val("doBaja");
			frm.submit();
		}
	});
}

function deshacerSocio (idAsegurado, nif)
{
	var frm = document.getElementById('main');
	jConfirm('¿Seguro que desea recuperar este registro?', 'Diálogo de Confirmación', function(r) {
		if (r){
			frm.target="";
			$('#idAseguradoBaja').val(idAsegurado);
			$('#nifcifBaja').val(nif);
			$('#method').val("deshacerSocio");
			frm.submit();
		}
	});
}

function doAlta() {
	var frm = document.getElementById('main');
	frm.target="";
	$('#method').val("doAlta");
	$('#origenLlamada').val("doAlta");
	$('#main').submit();	
}

function doModificar() {
	var frm = document.getElementById('main');
	frm.target="";
	$('#method').val("doModificar");
	$('#origenLlamada').val("doModificar");
	$('#main').submit();	
}


function doConsulta() {
	var frm = document.getElementById('main');
	frm.target="";
	$('#method').val("doConsulta");
	$('#origenLlamada').val("doConsulta");
	$("#main").validate().cancelSubmit = true;
	$('#main').submit();	
}

function doLimpiar() {
	var frm = document.getElementById('main');
	frm.target="";
	$('#idSocio').val('');
	$('#nifcif').val('');
	$('#nombre').val('');
	$('#apellido1').val('');
	$('#apellido2').val('');
	$('#razonsocial').val('');
	$('#numsegsocial').val('');
	$('#atp').selectOptions("");
	$('#jovenagricultor').selectOptions("");
	$('#regimensegsocial').selectOptions("");
	$('#tipoIdentificacion').selectOptions("");		
					
	$('#method').val("doConsulta");
	$('#origenLlamada').val("doLimpiar");
	$("#main").validate().cancelSubmit = true;
	$('#main').submit();	
}



function closePopUpPolizasSinGrabar(){
	var frm = document.getElementById('main');
	frm.target="";
	$('#method').val('');
    frm.displayPopUpPolizas.value = 'false';       
    $('#popUpPolizasSinGrabar').hide();
    $('#overlay').hide();
	
}

function bajaSociosConPolizas(idAsegurado, nifcifAsegurado)
{
	var frm = document.getElementById('main');
	frm.target="";
	$('#idAseguradoBaja').val(idAsegurado);
	$('#nifcifBaja').val(nifcifAsegurado);
	$('#method').val("doBaja");
	frm.submit();
}
function imprimir(size)	{
	var frm = document.getElementById('main');
	frm.target="_blank";
	$('#method').val("doImprimir");
	frm.submit();				
}				