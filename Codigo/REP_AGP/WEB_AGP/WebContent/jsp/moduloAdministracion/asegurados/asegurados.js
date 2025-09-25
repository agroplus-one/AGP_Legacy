$(function(){
	$("#grid").displayTagAjax();
	check_checks($('#idsRowsChecked').val());
});

$(document).ready(function(){
	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
	
	if($("#error").val() == "true"){ 
		$("#btnAlta").hide();
		$("#btnAlta").attr("href", "javascript:");
		$("#btnModificar").hide();
		$("#btnModificar").attr("href", "javascript:");
		$("#btnConsultar").hide();
		$("#btnConsultar").attr("href", "javascript:");
		$("#btnLimpiar").hide();
		$("#btnLimpiar").attr("href", "javascript:");
		
	} else {
		Zapatec.Calendar.setup({
			firstDay 	: 1,
			weekNumbers : false,
			showOthers 	: true,
			showsTime 	: false,
			timeFormat 	: "24",
			step 		: 2,
			range 		: [ 1900.01, 2999.12 ],
			electric 	: false,
			singleClick : true,
			inputField 	: "fecha-fecha-estudio",
			button 		: "btn-fecha-estudio",
			ifFormat 	: "%d/%m/%Y",
			daFormat 	: "%d/%m/%Y",
			align 		: "Br"
		});
	}
	

	$('#main').validate({
		 errorLabelContainer: "#panelAlertasValidacion",
		 wrapper: "div",
		 
		 onfocusout: function(element) {
			if ( ($('#operacion').val() == "alta" || $('#operacion').val() == "modificar") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
				this.element(element);
			}else{				
											
				if($('#grupoEntidades').val() != ""){
					if(element.name == "entidad.codentidad"){
						if($("#main").validate().element(element)){
							$('#operacion').val("consultar");									
							$('#btnConsultar').attr('disabled','');								
						}else{
							$('#operacion').val("consultar");
							$('#btnConsultar').attr('disabled','true');										
						}
					}
				}							
			}
		 },
		 highlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).show();
		 },
		 unhighlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).hide();
		 },
	 	rules: {	
	 		"usuario.subentidadMediadora.id.codentidad": {required:true, digits: true, rangelength:[1,4]},
	 		"usuario.subentidadMediadora.id.codsubentidad": {required:true, digits: true, rangelength:[1,4]},
	 		//DAA 11/05/2012 Mejora 145		 	
	 		"entidad.codentidad": {required: true, digits: true, rangelength:[3,4],grupoEnt:true},
	 		"tipoidentificacion": {required: true},
	 		"nifcif": {required: function(element){return $('#tipoIdentificacion').val() !='';}, CIFNIF: true},
	 		"nombre": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
	 		"apellido1": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
	 		"apellido2": {letterswithwhitespace: true},
	 		"razonsocial": {required: function(element){return $('#tipoIdentificacion').val() == 'CIF';},  lettersnumberswhitespacecolonpoint: true},
	 		"usuario.codusuario":{required: true},
	 		"via.clave": {required: true, lettersonly: true, minlength: 2},
	 		"direccion": {required: true, number: false,letterswithcommas: true, maxlength: 22},
	 		"numvia": {required: true, letterswithcommas: true},
	 		"piso" : {invalidChars: true},
	 		"bloque" : {invalidChars: true},
	 		"escalera" : {invalidChars: true},
		 	"localidad.id.codprovincia": {required: true, digits: true, number: false},
		 	"localidad.id.codlocalidad": {required: true, digits: true, number: false},
		 	"localidad.id.sublocalidad": {required: true, digits: true, number: false},
		 	"codpostalstr": {required: true, minlength: 5, digits: true, notregex: "000$"},
		 	"telefono": {required: true, digits: true, rangelength: [9,10],maxlength: 9},
		 	"movil": {required: false, digits: true, rangelength: [9,10],maxlength: 9},
		 	"email": {email: true},
		 	"numsegsocial": {required:false,minlength: 12,digits:true,obligatorioCondNSS:true,numSS:true},
		 	"regimensegsocial": {regimensegsocialOK: true,obligatorioCondReg:true},
		 	"atp":{required:true},
		 	"jovenagricultor":{required:true}
	 	},
	 	messages: {
	 		"usuario.subentidadMediadora.id.codentidad": {required:"El campo Entidad Mediadora es obligatorio", digits: "El campo Entidad Mediadora solo puede contener d\u00EDgitos", rangelength: "El campo Entidad Mediadora debe contener entre 1 y 4 d\u00EDgitos"},
	 		"usuario.subentidadMediadora.id.codsubentidad": {required:"El campo Subentidad Mediadora es obligatorio", digits: "El campo Subentidad Mediadora solo puede contener d\u00EDgitos", rangelength: "El campo Subentidad Mediadora debe contener entre 1 y 4 d\u00EDgitos"},
	 	 	"entidad.codentidad": {required: "El campo Entidad es obligatorio", digits: "El campo Entidad solo puede contener d\u00EDgitos", rangelength: "El campo Entidad debe contener entre 3 y 4 d\u00EDgitos",grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"},
	 	 	"tipoidentificacion": {required: "El campo Tipo ident. es obligatorio"},
	 	 	"nifcif": {required: "El campo NIF/CIF/NIE Asegurado es obligatorio", CIFNIF: "El campo NIF/CIF/NIE Asegurado tiene un formato incorrecto"},
	 	 	"nombre": {required: "El campo Nombre es obligatorio", letterswithwhitespace: "El campo Nombre no puede contener d\u00EDgitos ni caracteres especiales"},
	 	 	"apellido1": {required: "El campo Primer Apellido es obligatorio", letterswithwhitespace: "El campo 1er Apellido no puede contener d\u00EDgitos ni caracteres especiales"},
	 	 	"apellido2": {letterswithwhitespace: "El campo 2\u00B0 Apellido no puede contener d\u00EDgitos ni caracteres especiales"},
	 	 	"razonsocial": {required: "El campo Raz\u00F3n Social es obligatorio", lettersnumberswhitespacecolonpoint: "El campo Raz\u00F3n Social no puede contener caracteres especiales"},
	 	 	"usuario.codusuario":{required: "El campo usuario es obligatorio"},
	 	 	"via.clave": {required: "El campo V\u00EDa es obligatorio", lettersonly: "El campo V\u00EDa no puede contener d\u00EDgitos", minlength: "El campo V\u00EDa debe contener 2 letras"},
		 	"direccion": {required: "El campo Domicilio es obligatorio",letterswithcommas: "El campo Domicilio no puede caracteres especiales excepto comas", number: "El campo Domicilio no puede contener d\u00EDgitos", maxlength: "El campo Domicilio debe tener 22 caracteres como m\u00E1ximo"},
		 	"numvia": {required: "El campo n\u00FAmero es obligatorio",letterswithcommas: "El campo n\u00FAmero solo puede contener letras, d\u00EDgitos y comas"},
		 	"piso" : {invalidChars: "El campo Piso contiene caracteres no v\u00E1lidos .,:;<>[]%!?()+*_="},
		 	"bloque" : {invalidChars: "El campo Bloque contiene caracteres no v\u00E1lidos .,:;<>[]%!?()+*_="},
		 	"escalera" : {invalidChars: "El campo Escalera contiene caracteres no v\u00E1lidos .,:;<>[]%!?()+*_="},
		 	"localidad.id.codprovincia": {required: "El campo Provincia es obligatorio", digits: "El campo Provincia solo puede contener d\u00EDgitos"},
		 	"localidad.id.codlocalidad": {required: "El campo Localidad es obligatorio", digits: "El campo Localidad solo puede contener d\u00EDgitos"},
		 	"localidad.id.sublocalidad": {required: "El campo Sublocalidad es obligatorio", digits: "El campo Sublocalidad solo puede contener d\u00EDgitos"},
		 	"codpostalstr": {required: "El campo c\u00F3digo Postal es obligatorio", minlength: "El campo c\u00F3digo Postal debe contener 5 d\u00EDgitos", digits: "El campo c\u00F3digo Postal solo puede contener d\u00EDgitos", notregex:"El campo c\u00F3digo Postal no tiene el formato correcto"},
		 	"telefono": {required: "El campo Tel\u00E9fono es obligatorio", rangelength: "El campo Tel\u00E9fono debe contener entre 9 y 10 d\u00EDgitos", digits: "El campo Tel\u00E9fono solo puede contener d\u00EDgitos",maxlength: "El campo Tel\u00E9fono debe tener 9 digitos como m\u00E1ximo"},
		 	"movil": {rangelength: "El campo M\u00F3vil debe contener entre 9 y 10 d\u00EDgitos", digits: "El campo M\u00F3vil solo puede contener d\u00EDgitos",maxlength: "El campo M\u00F3vil debe tener 9 digitos como m\u00E1ximo"},
		 	"email": {email: "El formato del campo E-mail no es correcto"},
		 	"numsegsocial": {minlength: "El campo N\u00B0 S.S. debe contener 12 d\u00EDgitos",digits:"El n\u00FAmero de S.S solo admite d\u00EDgitos",obligatorioCondNSS:"El campo N\u00B0 S.S. es obligatorio",numSS:"El campo N\u00B0 S.S. tiene formato incorrecto"},
		 	"regimensegsocial": {regimensegsocialOK: "El campo Ind. R\u00E9gimen no puede estar vac\u00EDo", obligatorioCondReg: "El campo Ind.r\u00E9gimen es obligatorio"},
		 	"atp":{required:"El campo ATP es obligatorio"},
		 	"jovenagricultor":{required:"El campo J. Agricultor/a es obligatorio"}
	 	}
	 	
	 	
	});	
	
	$('#frmImportarCsv').validate({			
		onfocusout: function(element) {
			if ( ($('#operacion').val() == "alta" || $('#operacion').val() == "modificar") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
				this.element(element);
			}
		 },
		 
		errorLabelContainer: "#panelAlertasValidacionImportarCsv",
		wrapper: "div",	  				 
		rules: {
			"file":{required: true, accept: "csv"}
		},
		messages: {
			"file":{required: "Debe seleccionar un archivo", accept:"El archivo no tiene una extensi\u00F3n v\u00E1lida"}
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
	
	jQuery.validator.addMethod("obligatorioCondReg", function(value, element, params) {
		
		if($('#atp').val() == "S"){
			if($('#regimensegsocial').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	});	
		
	jQuery.validator.addMethod("regimensegsocialOK", function(value, element, params) {
		if($('#numsegsocial').val() == "")
			return true;
		else if ($('#regimensegsocial').val() == "")
			return false;
		else
			return true;
	});	
	
	jQuery.validator.addMethod("grupoEnt", function(value, element, params) { 
		var codentidad = $('#entidad').val();
		if (codentidad != ""){
			if($('#grupoEntidades').val() == "")
					return true;
			var grupoEntidades = $('#grupoEntidades').val().split(',');
			var encontrado = false;
			
			for(var i=0;i<grupoEntidades.length;i++){
				if(grupoEntidades[i] == codentidad){
					encontrado = true;
					break;
				}
			}
			return 	encontrado;	
		}
		return true;
	});
	
	jQuery.validator.addMethod("numSS", function(value, element, params) { 
		return value.substr(0,2)!='00'; //&& value.substr(value.length-2)!='00';
	});
	
	jQuery.validator.addMethod("invalidChars", function(value, element, params) { 
		var validado = true;
		var invalidChars = ".,:;<>[]%!?()+*_=";
		for(var i = 0; i< value.length;i++){
			if(invalidChars.indexOf(value.charAt(i))!= -1){							
				validado = false;
				break;
			}
		}
		return (this.optional(element) || validado);	
	});
	
	$("#tipoIdentificacion").change(function (event){
		if($('#operacion').val()=="alta" || $('#operacion').val()=="modificacion"){
			$("label[class|='campoObligatorio']").hide();
			$("#main").valid();						
		}
	});
	

	if($("#idAsegurado").val() != ""){ 
		$('#operacion').val("modificar");
		$("#btnAlta").hide();
		$("#btnModificar").show();
		$("#btnDatosAdic").show();
		if ($("#tipoIdentificacion").val() != 'CIF'){
			$("#btnSocios").hide();
			$("#jovenagricultor").attr('disabled', false);
		}else{
			$("#btnSocios").show();
			$("#jovenagricultor").attr('disabled', true);
		}
		//$('#nifcif').attr('readonly', true);
		if($('#perfil').val() != "0"){
			$('#nifcif').attr('readonly', true);
			$("#entidad").attr('readonly', true);
			$("#desc_entidad").attr('readonly', true);
			$("#tipoIdentificacion").attr('disabled', true);
		}
	};
	
	$('.enviar-carga-usuario').click(function(){
		var nifCif = $(this).attr('data-nifcif');
		llamadaAjaxDatosAsegurado('', nifCif, 'carga');
	});
	
	if($('#numElem').val() == "1"){
		document.getElementById('selTodos').disabled = true;
	}
	
});

jQuery.validator.addMethod("CIFNIF", function(value, element) {
	return (this.optional(element) || generales.validaCifNif($("#tipoIdentificacion").val(), value));
});

function modificar (idAsegurado, entidad, tipoIndent, cifNif, nombre, apellido1, apellido2,
		razSocial, via, domicilio, numero, piso, bloque, escalera, provincia, localidad, sublocalidad,
		codPostal, telefono, movil, email, numSS, regimen, ATP, jovenAgr, desc_entidad, desc_via,
		desc_provincia, desc_localidad, codUsuario, codEntMed, codSubentMed,fechaRevision,revisado){
	
	$("#idAsegurado").val(idAsegurado);
	$("#entidad").val(entidad);
	$("#desc_entidad").val(desc_entidad);
	$("#tipoIdentificacion").val(tipoIndent);
	$("#nifcif").val(cifNif);
	$("#nombre").val(nombre);
	$("#apellido1").val(apellido1);
	$("#apellido2").val(apellido2);
	$("#razonsocial").val(razSocial);
	$("#codusuario").val(codUsuario);
	$("#via").val(via);
	$("#desc_via").val(desc_via);
	$("#direccion").val(domicilio);
	$("#numvia").val(numero);
	$("#piso").val(piso);
	$("#bloque").val(bloque);
	$("#esc").val(escalera);
	$("#provincia").val(provincia);
	$("#desc_provincia").val(desc_provincia);
	$("#localidad").val(localidad);
	$("#desc_localidad").val(desc_localidad);
	$("#sublocalidad").val(sublocalidad);
	$("#cp").val(codPostal);
	$("#telefono").val(telefono);
	$("#movil").val(movil);
	$("#mail").val(email);
	$("#numsegsocial").val(numSS);
	$("#regimensegsocial").val(regimen);
	$("#atp").val(ATP);
	$("#atp").attr('readonly', true);
	$("#jovenagricultor").val(jovenAgr);
	$("#entmediadora").val(codEntMed);
	$("#subentmediadora").val(codSubentMed);
	$("#fechaRevision").val(fechaRevision);
	$("#revisado").val(revisado);
	
	$("#btnDatosAdic").show();
	if (tipoIndent != 'CIF'){
		$("#btnSocios").hide();
		$("#jovenagricultor").attr('disabled', false);
	}else{
		$("#btnSocios").show();
		$("#jovenagricultor").attr('disabled', true);
	}
	$("#numsegsocialOLD").val(numSS);
	$("#atpOLD").val(ATP);
	$("#jovenagricultorOLD").val(jovenAgr);
	
	generales.cifnifSeleccionado();
	//Botones
	$("#main").valid();
	$("#avisoErrores").hide();	
	$("#btnAlta").hide();
	$("#btnModificar").show();
	if($('#perfil').val() != "0"){
		$('#nifcif').attr('readonly', true);
		$("#entidad").attr('readonly', true);
		$("#desc_entidad").attr('readonly', true);
		$("#tipoIdentificacion").attr('disabled', true);
	}
	// si estamos en carga Asegurados no se muestran los botones
	// Datos adic ni socios
	
	if($('#cargaAseg').val() == "cargaAseg"){
		$("#btnSocios").hide();
		$("#btnDatosAdic").hide();
	}
}

function cargaDatos(formulario) {
	document.getElementById(formulario).idAsegurado.value = document.getElementById('main').idAsegurado.value;
}



function botonesModificacion()
{				
	var request=location.search.indexOf('editar');
	if(request != -1){
		var alta = document.getElementById('btnAlta');
		alta.style.display = "none";
		var modif = document.getElementById('btnModificar');
		modif.style.display = "";
		
	}
	var identificacionFiscal=document.getElementById('tipoIdentificacion').value;
	if(identificacionFiscal == 'NIF' || identificacionFiscal == 'NIE'){
		var socios = document.getElementById('btnSocios');
		socios.style.display = "none";
	}
}

function alta(){
	var frm = document.getElementById('main');
	frm.target="";
    $("#avisoErrores").hide();		        
	$('#id').val('');
	$('#method').val("doAlta");
	$('#origenLlamada').val("alta");
	$('#main').submit();
	if ($('#main').valid()) {
		$.blockUI.defaults.message = '<h4> <BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
 		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	}
}

//function actualizarAseguradosWS(){
//	var frm = document.getElementById('main');
//	frm.idsRowsChecked.value="";
//	frm.target="";
//	$("#main").validate().cancelSubmit = true;
//	$('#method').val("doActualizaAseguradosSW");
//	$('#main').submit();
//}

function mostrarFormularioAsegurados() {
    // Verifica si el formulario ya existe y alterna visibilidad
    var existingForm = document.getElementById('formAsegurados');
    if (existingForm) {
        existingForm.style.display = existingForm.style.display === 'none' ? 'block' : 'none';
        return;
    }

    // Crea el formulario
    var formHtml = `
        <form id="formAsegurados" onsubmit="return actualizarAseguradosWS(event)">
            <label for="inicio">Inicio:</label>
            <input type="text" id="inicio" name="inicio" pattern="\\d*" required><br>
            <label for="fin">Fin:</label>
            <input type="text" id="fin" name="fin" pattern="\\d*" required><br>
            <button type="submit">Enviar</button>
        </form>
    `;

    // Inserta el formulario justo debajo del botón
    var btn = document.getElementById('btnActualizaAseguradoWS');
    var div = document.createElement("div");
    div.innerHTML = formHtml;
    div.style.marginTop = "10px";
    btn.parentNode.insertBefore(div, btn.nextSibling);
}

function actualizarAseguradosWS(event) {
	// Previene que el formulario recargue la página
	event.preventDefault();

    var inicio = document.getElementById('inicio').value;
    var fin = document.getElementById('fin').value;

    // Valida que sean números
    if (isNaN(inicio) || isNaN(fin)) {
        alert("Por favor, ingrese números válidos en los campos de inicio y fin.");
        return false;
    }

    // Añadimos los valores como campos ocultos al formulario principal
    var frm = document.getElementById('main');
    frm.idsRowsChecked.value = "";
    frm.target = "";

    var inputInicio = document.createElement('input');
    inputInicio.type = 'hidden';
    inputInicio.name = 'inicio';
    inputInicio.value = inicio;
    frm.appendChild(inputInicio);

    var inputFin = document.createElement('input');
    inputFin.type = 'hidden';
    inputFin.name = 'fin';
    inputFin.value = fin;
    frm.appendChild(inputFin);

    $("#main").validate().cancelSubmit = true;
    $('#method').val("doActualizaAseguradosSW");

    // Envia el formulario principal
    frm.submit();
}

function seleccionarAsegurado(){
	$('#method').val("doModificar");
	$('#main').submit();
}

function guardarModificaciones(){
	var frm = document.getElementById('main');
	var mostrarPopup = "false";
	frm.target="";
	var subv20= "false";
	var subv10= "false";
	if ($("#atpOLD").val() != $("#atp").val()){
		if ($("#atp").val() == "S"){
			mostrarPopup = "true";
			subv20 = "true";
		}else if ($("#atp").val() == "N" && $("#atpOLD").val() !="" ){
			mostrarPopup = "true";
			subv20 = "false";
		}
	} else {
		if ($("#atp").val() == "S"){
			subv20 = "true";
		} else {
			subv20 = "false";
		}
	}
	if ($("#jovenagricultorOLD").val() != $("#jovenagricultor").val()){
		if ($("#jovenagricultor").val() == "S"){
			mostrarPopup = "true";
			subv10 = "true";
		}else if ($("#jovenagricultor").val() == "N" && $("#jovenagricultorOLD").val() !="" ){
			mostrarPopup = "true";
			subv10 = "false";
		}
	} else {
		if ($("#jovenagricultor").val() == "S"){
			subv10 = "true";
		} else {
			subv10 = "false";
		}
	}
	
	$("#showPopupPolAsegurados").val(mostrarPopup);
	$("#subv10").val(subv10);
	$("#subv20").val(subv20);
	// le quitamos el disabled para que envie el valor del combo
	$("#tipoIdentificacion").attr('disabled', false);
	$('#origenLlamada').val("modificar");
    $("#avisoErrores").hide();
	$('#method').val("doModificar");
	$('#main').submit();
}

    
function consultar(){
	
	
	var frm = document.getElementById('main');
	frm.idsRowsChecked.value="";
	frm.target="";
	//DAA 11/05/2012 Mejora 145
	if (!comprobarCampos()){
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
		$('#panelAlertasValidacion').show();
	}
	else{
		$("#main").validate().cancelSubmit = true;
		$('#method').val("doConsulta");
		$('#main').submit();
	}
}

function cargarAsegurado(idAsegurado) {
	$("#main").validate().cancelSubmit = true;
	$('#method').val('doCarga');
	$('#idAsegurado').val(idAsegurado);
	$('#main').submit();
}

function limpiar(){				
	
	$('#entidad').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	$('#idAsegurado').val('');
	$('#codusuario').val('');
	$('#desc_entidad').val('');
	$('#desc_via').val('');
	$('#desc_provincia').val('');
	$('#desc_localidad').val('');
	$('#nifcif').val('');
	$('#nombre').val('');
	$('#apellido1').val('');
	$('#apellido2').val('');
	$('#razonsocial').val('');
	$('#via').val('');
	$('#direccion').val('');
	$('#numvia').val('');
	$('#piso').val('');
	$('#bloque').val('');
	$('#esc').val('');
	$('#provincia').val('');
	$('#localidad').val('');
	$('#sublocalidad').val('');
	$('#cp').val('');
	$('#telefono').val('');
	$('#movil').val('');
	$('#mail').val('');
	$('#numsegsocial').val('');
	$('#atp').selectOptions("");
	$('#jovenagricultor').selectOptions("");
	$('#regimensegsocial').selectOptions("");
	$('#tipoIdentificacion').selectOptions("");	
	$('#idsRowsChecked').val('');
	$('#checkTodo').val('');
	var frm = document.getElementById('main');
	frm.target="";
	$("#main").validate().cancelSubmit = true;
	$('#method').val("doConsulta");
	$('#origenLlamada').val("limpiar");
	$('#main').submit();				
}

function imprimir(size,formato)	{
	var frm = document.getElementById('main');
	
	if (size > frm.impresionnumRegAseg.value){
		$('#panelAlertasValidacion').html(frm.listMsgError.value);
		$('#panelAlertasValidacion').show();
	}else{
		frm.target="_blank";
		frm.formato.value = formato;
		frm.method.value = 'imprimir';
		frm.submit();
	}
}	

function eliminar(idAsegurado){
	jConfirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		if (r){
			var frm = document.getElementById('main');
			frm.target="";
			$("#main").validate().cancelSubmit = true;
			$("#method").val("doBaja");
			$("#idAsegurado").val(idAsegurado);
			$('#origenLlamada').val("baja");
			$("#main").submit();
		}
	});
} 
function desbloquearUsuario(usuario){
	jConfirm('\u00BFEst\u00E1 seguro de que desea desbloquear el asegurado seleccionado?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		if (r){
			var frm = document.getElementById('main');
			frm.target="";
			$("#main").validate().cancelSubmit = true;
			$("#method").val("doDesbloquearUsuario");
			$("#usuarioAsegurado").val(usuario);
			$("#main").submit();
		}
	});
}

function getDatosAseguradoWS(idAsegurado,nifcif){
	
	var frm = document.getElementById('main');
	frm.target="";
	$("#main").validate().cancelSubmit = true;
	frm.method.value = "getDatosAseguradoWS";
	
	frm.idAseguradoP.value = idAsegurado;
	frm.nifcifBusqueda.value = nifcif;
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main").submit();
}


function getDatosAseguradoWService(){
	var frm = document.getElementById('main');
	var nifcif = main.nifcif.value;
	if(validaNif(nifcif)){
		$('#panelAlertasValidacion').html("");
		$('#panelAlertasValidacion').hide();
		getDatosAseguradoWS(null,nifcif)
	}else{
		$('#panelAlertasValidacion').html("El formato del NIF/CIF/NIE es incorrecto.");
		$('#panelAlertasValidacion').show();
	}	
}

//DAA 11/05/2012 comprueba si los campos estan vacios
function comprobarCampos(){
 	var resultado = false;
 	if (!resultado && $('#entidad').val() != ''){
 		resultado = true;
 	}     
 	if (!resultado && ($('#entmediadora').val() != '' && $('#subentmediadora').val() != '')){
 		resultado = true;
 	}   
 	if (!resultado && $('#tipoIdentificacion').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#nifcif').val() != ''){
 		resultado = true;
 	} 
 	if (!resultado && $('#nombre').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#apellido1').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#apellido2').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#razonsocial').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#codusuario').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#via').val() != ''){
 		resultado = true;
 	}
	if (!resultado && $('#direccion').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#numvia').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#piso').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#bloque').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#esc').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#provincia').val() != ''){
 		resultado = true;
 	}	         	
 	if (!resultado && $('#localidad').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#sublocalidad').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#cp').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#telefono').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#movil').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#mail').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#numsegsocial').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#regimensegsocial').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#atp').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#jovenagricultor').val() != ''){
 		resultado = true;
 	}
 	return resultado;
 }   
   

function validaNif(input){
	var validado    = true;
	var cadena = input.split(',');
	for(var i=0; i<cadena.length; i++){
		var validadoNIF = true;
		var validadoCIF = true;
		var validadoNIE = true;
		validadoNIF = generales.validaCifNif("NIF", $.trim(cadena[i]));
		if (validadoNIF == false)
			validadoCIF = generales.validaCifNif("CIF", $.trim(cadena[i]));
		if (validadoCIF == false)
			validadoNIE = generales.validaCifNif("NIE", $.trim(cadena[i]));			
		if (validadoNIF == false && validadoCIF == false && validadoNIE == false){
			validado = false;
			break;
		}
	}
	if(validado == false){
		$('#method').val('');
	}
	return validado;
}

//FUNCIONES PARA LA IMPORTACION DEL FICHERO CSV
function cleanPopUpImportarCsv(){
	cerrarPopUpImportarCsv();
	$("#tablaInformacion").html("<input type='file' class='dato'  id='file' name='file' size='40' onchange='javascript:limpiaPanelAlertasImportarCsv();'/>");	
	$("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});
	
	limpiaPanelAlertasImportarCsv();
}

function cerrarPopUpImportarCsv(){
	$('#divImportarCsv').fadeOut('normal');
	$('#overlay').hide();
}

function doImportarCsv(){
  limpiaPanelAlertasImportarCsv();
	if ($('#frmImportarCsv').valid()){
		cerrarPopUpImportarCsv();
		var frm = document.getElementById('frmImportarCsv');
		$.blockUI.defaults.message = '<h4> Importando archivo de asegurados.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		frm.submit();			
	}
}

function limpiaPanelAlertasImportarCsv(){
	$("#fileImportarCsv").val('');
	$("#panelAlertasValidacionImportarCsv").hide();
}

function showPopUpImportarCsv(){
	limpiaPanelAlertasImportarCsv();
  	var frm = document.getElementById('frmImportarCsv');
	$('#divImportarCsv').fadeIn('normal');
	$('#overlay').show();
}

function ajaxFileUpload(){
	if ($('#frmImportarCsv').valid()){
			$('#overlay').show();
			$.ajaxFileUpload({
					url:'asegurado.html?method=doImportarCsv', 
					secureuri:false,
					fileElementId:'file',
					dataType: 'json',
					success: function (data){
					
						cerrarPopUpImportarCsv();
						
						if (data.alerta != null){
							var str = data.alerta;
							$('#resultadoImportacionCSV').html(str);
							$('#resultadoImportacionCSV_popup').fadeIn('normal');
							$('#overlay').show();
						}else{
							var str = "";
							
							str+="<Table width='75%' border='0'>";
								str+="<tr align='left'><td class='literal' width='60%'>Asegurados incluidos en el fichero:</td><td class='literal2' width='30%'>" + (data.registrosImportacion)   + "</td></tr>";
								str+="<tr align='left'><td class='literal' width='60%'>Asegurados cargados correctamente:</td><td class='literal2'  width='30%'>" + (data.registrosImportacionOK) + "</td></tr>";
								str+="<tr align='left'><td class='literal' width='60%'>Asegurados no cargados:</td><td class='literal2'  width='30%'>" + (data.registrosImportacionKO) + "</td></tr>";
							str+="</table>";
							
							str+="<table width='65%' border='0'>";
							
							for(var i=0;i<data.registrosImportacionKOLista.length;i++){
								
								if ( data.registrosImportacionKOLista[i].indexOf("NIF")  !== -1){
									//str  += "<ul>";
									str +="<tr align='left' ><td class='literal' width='15%'><li>" 
										+ data.registrosImportacionKOLista[i].substring(3,data.registrosImportacionKOLista[i].length)
										+ "</li></td>";
										pintoNif=true;
									//str  += "</ul>";
								}else{
									if (pintoNif){
										str +="<td class='literal2'>" 
											+ data.registrosImportacionKOLista[i]
											+ "</td></tr>";
									}else{
										str +="</tr><tr align='left'><td></td><td class='literal2'>" 
											+ data.registrosImportacionKOLista[i]
											+ "</td></tr>";
									}
									pintoNif=false;
								}
							}
							$('#resultadoImportacionCSV').html(str);
							$('#resultadoImportacionCSV_popup').fadeIn('normal');
							$('#overlay').show();
						}
			         
					},				
					error: function (data, status, e){
						cerrarPopUpImportarCsv();
						
						$('#mensajeError').html(data);
						$('#resultadoImportacionCSV_popup').fadeIn('normal');
						$('#overlay').show();
					}
			});
		
	}
}

function cerrarPopUpResultadoImportarCsv(){
	$('#resultadoImportacionCSV_popup').fadeOut('normal');
	$('#overlay').hide();
}

function abrirPopUpFechaEstudio(nifCif){
	$('#nif-fecha-estudio').val(nifCif);
	$('#fecha-estudio-popup').fadeIn('normal');
	$('#overlay').show();		
}


function cerrarPopUpFechaEstudio(){
	$('#nif-fecha-estudio').val('');
	$('#fecha-fecha-estudio').val('');
	$('#fecha-estudio-popup').fadeOut('normal');
	$('#overlay').hide();
}

/* Marcar todos los checks */

function check_checks(ids){
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			var idCheck = "checkAsegurado_" + array_ids[i];
			$('#' + idCheck).attr('checked',true);
		}
	}
}

function marcar_todos() {
	var frm = document.getElementById('main');
	$("input[type=checkbox]").each(function() {
		$(this).attr('checked', true);
	});
	frm.checkTodo.value = "true";
	frm.idsRowsChecked.value = frm.aseguradosString.value;
}

function desmarcar_todos() {
	var frm = document.getElementById('main');
	if (frm.checkTodo.value == "true") {
		$("input[type=checkbox]").each(function() {
			$(this).attr('checked', false);
		});
		frm.checkTodo.value = "false";
		frm.idsRowsChecked.value = "";
	}
}


// * Mantener checks *//
function onClickInCheck2(idCheck) {
	if (idCheck) {
		var __ids = $('#idsRowsChecked').val();

		if (document.getElementById("checkAsegurado_"+idCheck).checked == true) {
			addCheck2(__ids, idCheck);
		} else {
			subtractCheck2(__ids, idCheck);
		}
	}
}

function addCheck2(ids, check) {
	if (ids != null) {
		ids = ids + check + ";";
	}

	var frm = document.getElementById('main');
	frm.idsRowsChecked.value = ids;
}

function subtractCheck2(ids, check) {
	var newList = "";
	var frm = document.getElementById('main');

	if (frm.checkTodo.value == "true") {
		frm.checkTodo.value = "";
		document.getElementById('selTodos').checked = false;
		ids = frm.idsRowsChecked.value;
	}

	if (ids != null) {
		var array_ids = ids.split(';');
		for (var i = 0; i < array_ids.length; i++) {
			if (array_ids[i] != check && array_ids[i] != ""
					&& array_ids[i] != null && array_ids[i] != undefined) {
				newList = newList + array_ids[i] + ";";
			}
		}
	}

	var frm = document.getElementById('main');
	frm.idsRowsChecked.value = newList;
}


function abrirPopupSubvenciones() {
	var fecha = $('#fecha-fecha-estudio').val();
	var valido = false;
	if (fecha == '') {
		valido = true;
	} else {
		if (ComprobarFecha(document.getElementById('fecha-fecha-estudio'),
				document.forms['fecha-estudio-form'], 'Fecha de estudio')) {
			valido = true;
		} else {
			$('#fecha-fecha-estudio').val('');
			valido = false;
		}
	}
	if (valido) {
		var nifCif = $('#nif-fecha-estudio').val();
		$('#nif-fecha-estudio').val('');
		$('#fecha-fecha-estudio').val('');
		$('#fecha-estudio-popup').fadeOut('normal');
		llamadaAjaxDatosAsegurado(fecha, nifCif, 'admin');
	}
}

function cerrarPopupSubvenciones() {
	$('#subveciones-asegurado-popup').fadeOut('normal');
	$('#overlay').hide();
}

function abrirPopupSeleccionLinea() {
	
	var frm = document.getElementById('main');
	
	if (frm.idsRowsChecked.value == '') {
		jAlert('Debe seleccionar al menos un Asegurado', 'Error');
	} else {
		lupas.limpiarCampos('plan', 'linea', 'desc_linea');
		$('#seleccion-linea-popup').fadeIn('normal');
		$('#overlay').show();
	}
}

function cerrarPopupSeleccionLinea() {
	$('#seleccion-linea-popup').fadeOut('normal');
	$('#overlay').hide();
}

function abrirPopupControlSubvs() {
	$('#control-subvs-popup').fadeIn('normal');
	$('#overlay').show();
	$('#content_popup_table').scrollTop(0);
}

function cerrarPopupControlSubvs() {
	$('#control-subvs-popup').fadeOut('normal');
	$('#overlay').hide();
}

function doImprimirControlSubvenciones() {
	var tabularData = [];
	$('#control-subvs-table tr').each(function() {
		var row = {};
		var i = 0;
		$.each(this.cells, function(){
			row[i++] = $(this).html();
		});
		tabularData.push(row);
	});
	var fileName = 'Control_Subvs_' + $('#plan').val() + '_' + $('#linea').val() + '.xlsx';
	var ws = XLSX.utils.json_to_sheet(tabularData, {skipHeader:true});
    var wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Control_Subvs");
    XLSX.writeFile(wb, fileName);
}

function doControlSubvenciones(plan, linea) {
	
	var frm = document.getElementById('main');
	
	
	$.ajax({
		url: 'asegurado.html',
		data:{
			method: 'doControlSubvenciones', 
			aseguradoSeleccionado: frm.idsRowsChecked.value, 
			plan: plan, 
			linea: linea
		},
		dataType: 'json',
		cache: false,
		success: function(data){
			$('#control-subvs-table').empty();
			$('#control-subvs-table').html('<tr><th class="literal">Asegurado</th><th class="literal">Subvencionable</th></tr>');			
			if (data.errorMsg) {
				jAlert(data.errorMsg, 'Error');
				$.unblockUI();
				$('#overlay').hide();
			} else {
				if (data.datosAsegs) {
					$.each(data.datosAsegs, function(index, value) {
						$('#control-subvs-table tr:last').after('<tr><td>' + value.nifcif + '</td><td>' + (value.subvencionable ? 'S\u00ED tiene subvenci\u00F3n por renovaci\u00F3n' : 'No existe el NIF/CIF/NIE en la BBDD para esta l\u00EDnea') + '</td></tr>');	
					});
					abrirPopupControlSubvs();
					$('#overlay').show();
				}
				$.unblockUI();
			}
		},
		beforeSend : function() {
			cerrarPopupSeleccionLinea();
			$.blockUI.defaults.message = '<h4> Realizando la consulta.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		},
		error: function(objeto, quepaso, otroobj){
			jAlert("Error al realizar la consulta de Subvenciones.", 'Error');
			$.unblockUI();
			$('#overlay').hide();
		},
		timeout: 120000,
		type: 'POST'
	});
}

function llamadaAjaxDatosAsegurado(fecha, nifCif, origen){
	$.ajax({
		url: 'asegurado.html',
		data:{method: 'getDetalleAsegurado', fechaEstudio: fecha, nifCif: nifCif, origen: origen},
		dataType: 'json',
		cache: false,
		success: function(data){
			if (data.agroMsg) {
				jAlert(data.agroMsg, 'Error');
				$.unblockUI();
				$('#overlay').hide();
			} else {
				$('#resultado-subvecionesAsegurado').html(data.tablaHtml);
				$('#subveciones-asegurado-popup').show('normal');
				$.unblockUI();
				$('#overlay').show();
			}
		},
		beforeSend : function() {
			$.blockUI.defaults.message = '<h4> Realizando la consulta.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		},
		error: function(objeto, quepaso, otroobj){
			jAlert("Error al realizar la llamada al WS de Agroseguro.", 'Error');
			$.unblockUI();
			$('#overlay').hide();
		},
		timeout: 120000,
		type: 'GET'
	});
}