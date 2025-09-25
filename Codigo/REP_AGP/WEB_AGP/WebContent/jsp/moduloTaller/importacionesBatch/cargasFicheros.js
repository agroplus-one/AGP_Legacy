$(document).ready(function(){
	
	$("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});

	$('#main1').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).show();
		},
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			"file":{required: true,valExtensionTxt:true},
			"file2":{required: true,validaNames: true,valExtensionZip:true},
			"linea":{validaLinea:true,digits:true},
			"plan":{validaPlan:true,digits:true}
		},
		messages: {
			"file":{required: "El Fichero Etiqueta  es obligatorio",valExtensionTxt:"El Fichero Etiqueta no tiene una extensión válida"},
			"file2":{required: "El Fichero ZIP  es obligatorio",validaNames:"Los nombres de los ficheros deben ser iguales",valExtensionZip:"El Fichero ZIP no tiene una extensión válida"},
			"linea":{validaLinea:"El campo Línea es obligatorio",digits:"El campo línea solo puede contener numeros"},
			"plan":{validaPlan:"El campo Plan es obligatorio",digits:"El campo Plan solo puede contener numeros"}
		}
	});
	
	jQuery.validator.addMethod("validaLinea", function(value, element, params) {
		if($('#tipo').val() != 2 && $('#linea').val() == ''){
			return false;
		}
		return true;
	});
	
	jQuery.validator.addMethod("validaPlan", function(value, element, params) {
		if($('#tipo').val() != 2 && $('#plan').val() == ''){
			return false;
		}
		return true;
	});
	
	jQuery.validator.addMethod("validaNames", function(value, element, params) {
		var name = $('#file').val();
		var name2 = $('#file2').val();
		name = name.split('\\');
		name2 = name2.split('\\');
		var aux1 =(name[name.length-1]).substring(0,(name[name.length-1].length - 4));
		var aux2 =(name2[name2.length-1]).substring(0,(name[name.length-1].length - 4));
		if (aux1 == aux2){
			return true;
		}else{
			return false;
		}
	});
	
	jQuery.validator.addMethod("valExtensionTxt", function(value, element, params) {
		var name = $('#file').val();
		name = name.split('\\');
		var aux1 =(name[name.length-1]).substring((name[name.length-1].length - 3),(name[name.length-1].length) );
		if ((aux1 == 'TXT')||(aux1 == 'txt')){
			return true;
		}else{
			return false;
		}
	});
	
	jQuery.validator.addMethod("valExtensionZip", function(value, element, params) {
		var name2 = $('#file2').val();
		name2 = name2.split('\\');
		var aux2 =(name2[name2.length-1]).substring((name2[name2.length-1].length - 3),(name2[name2.length-1].length) );
		if ((aux2 == 'ZIP')||(aux2 == 'zip')){
			return true;
		}else{
			return false;
		}
	});	

});

function cargar(){
	if ($('#main1').valid()){
		$.blockUI.defaults.message = '<h4> Cargando ficheros.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });

		$('#method1').val('doCargar');
		$('#main1').submit();
	}
}

function salir(){
	$("#main1").validate().cancelSubmit = true;
	var frm = document.getElementById('main1');
	frm.method.value="doSalir";
	$('#main1').submit();
}

function editarFichero(id){
	var frm = document.getElementById('main2');
	frm.idFichero.value=id;
	frm.method.value='doEditarTablas';
	$('#main2').submit();
}

function borrarFichero(id,name){
	$("#main1").validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar el fichero seleccionado?')){
		var frm = document.getElementById('main1');
		frm.idFichero.value=id;
		frm.nombreFichero.value = name;
		frm.method.value='doBorrarFichero';
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main1').submit();
	}
}

function consultarFichero(id){
	var frm = document.getElementById('main2');
	frm.idFichero.value=id;
	frm.modoConsulta.value = "true";
	//llamamos al metodo doEditarTablas (que carga las tablas)y le pasamos el atributo modoConsulta = true
	frm.method.value='doEditarTablas';
	$('#main2').submit();
}
