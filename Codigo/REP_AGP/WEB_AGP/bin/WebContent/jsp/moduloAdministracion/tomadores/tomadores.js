$(document).ready(function(){
	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
		
	$('#main').validate({
		 errorLabelContainer: "#panelAlertasValidacion",
			 wrapper: "li",
		 onfocusout: function(element) {
			if ( ($('#operacion').val() == "alta" || $('#operacion').val() == "modificar") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
				this.element(element);
			}else{
				if($('#grupoEntidades').val() != ""){
					if(element.name == "id.codentidad"){
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
		 	"id.codentidad": {required: true, digits: true, rangelength:[3,4], grupoEnt: true},
		 	"id.ciftomador": {required: true, cif: true, minlength: 9},
		 	"razonsocial": {required: true},
		 	"via.clave": {required: true, lettersonly: true, minlength: 2},
		 	"domicilio": {required: true,letterswithcommas: true},
		 	"numvia": {required: true, letterswithcommas: true},
		 	"piso" : {invalidChars: true},
	 		"bloque" : {invalidChars: true},
	 		"escalera" : {invalidChars: true},
		 	"localidad.id.codprovincia": {required: true, digits: true},
		 	"localidad.id.codlocalidad": {required: true, digits: true},
		 	"localidad.id.sublocalidad": {required: true, digits: true},
		 	"codpostalstr": {required: true, minlength: 5, digits: true, notregex:"000$", cp:true},
		 	"telefono": {required: true, digits: true, rangelength: [9,10]},
		 	"movil": {required: false, digits: true, rangelength: [9,10]},
		 	"email": {required: true, email: true},
		 	"email2": {required: false, email: true},
		 	"email3": {required: false, email: true},
		 	"repreNombre": {required: true,letterswithwhitespace: true},
		 	"repreAp1": {required: true,letterswithwhitespace: true},
		 	"repreAp2": {required: true,letterswithwhitespace: true},
		 	"repreNif": {required: true, nif: true},
		 	"envioAPagos": {required: true}
		 },
		 messages: {
		 	"id.codentidad": {required: "El campo Entidad es obligatorio", digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos", rangelength: "El campo Entidad debe contener entre 3 y 4 d\u00EDgitos", grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"},
		 	"id.ciftomador": {required: "El campo CIF Tomador es obligatorio", minlength: "El campo CIF debe contener 9 caracteres", cif: "El CIF introducido no es correcto"},
		 	"razonsocial": {required: "El campo Raz\u00F3n Social es obligatorio"},
		 	"via.clave": {required: "El campo V\u00EDa es obligatorio", lettersonly: "El campo V\u00EDa no puede contener d\u00EDgitos", minlength: "El campo V\u00EDa debe contener 2 letras"},
		 	"domicilio": {required: "El campo Domicilio es obligatorio",letterswithcommas:  "El campo Domicilio no puede caracteres especiales excepto comas"},
		 	"numvia": {required: "El campo N\u00FAmero es obligatorio", letterswithcommas: "El campo N\u00FAmero solo puede contener caracteres, d\u00EDgitos y comas"},
		 	"piso" : {invalidChars: "El campo Piso contiene caracteres no v\u00E1lidos"},
		 	"bloque" : {invalidChars: "El campo Bloque contiene caracteres no v\u00E1lidos"},
		 	"escalera" : {invalidChars: "El campo Escalera contiene caracteres no v\u00E1lidos"},
		 	"localidad.id.codprovincia": {required: "El campo Provincia es obligatorio", digits: "El campo Provincia s\u00F3lo puede contener d\u00EDgitos"},
		 	"localidad.id.codlocalidad": {required: "El campo Localidad es obligatorio", digits: "El campo Localidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"localidad.id.sublocalidad": {required: "El campo Sublocalidad es obligatorio", digits: "El campo Sublocalidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"codpostalstr": {required: "El campo C\u00F3digo Postal es obligatorio", minlength: "El campo C\u00F3digo Postal debe contener 5 d\u00EDgitos", digits: "El campo C\u00F3digo Postal s\u00F3lo puede contener d\u00EDgitos", notregex:"El campo C\u00F3digo Postal no tiene el formato correcto", cp: "El campo C\u00F3digo Postal no tiene el formato correcto"},
		 	"telefono": {required: "El campo Tel\u00E9fono es obligatorio", rangelength: "El campo Tel\u00E9fono debe contener entre 9 y 10 d\u00EDgitos", digits: "El campo Tel\u00E9fono s\u00F3lo puede contener d\u00EDgitos"},
		 	"movil": {rangelength: "El campo M\u00F3vil debe contener entre 9 y 10 d\u00EDgitos", digits: "El campo M\u00F3vil s\u00F3lo puede contener d\u00EDgitos"},
		 	"email": {required: "El campo email es obligatorio", email: "El formato del campo E-mail no es correcto"},
		 	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Inicio */
		 	"email2": {email: "El formato del campo Segundo E-mail no es correcto"},
		 	"email3": {email: "El formato del campo Tercer E-mail no es correcto"},
		 	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Fin */
		 	"repreNombre": {required: "El campo Nombre es obligatorio", letterswithwhitespace: "El campo Nombre no puede contener d\u00EDgitos ni caracteres especiales"},
		 	"repreAp1": {required: "El campo Primer Apellido es obligatorio", letterswithwhitespace: "El campo Primer Apellido no puede contener d\u00EDgitos ni caracteres especiales"},
		 	"repreAp2": {required: "El campo Segundo Apellido es obligatorio", letterswithwhitespace: "El campo Segundo Apellido no puede contener d\u00EDgitos ni caracteres especiales"},
		 	"repreNif": {required: "El campo NIF Representante es obligatorio", nif: "El campo NIF Representante tiene un formato incorrecto"},
		 	"envioAPagos": {required: "El campo env\u00EDo a Pagos es obligatorio"}
		 }
	});
	
	jQuery.validator.addMethod("grupoEnt", function(value, element, params) { 
		var codentidad = $('#entidad').val();
		var encontrado = false;
		if($('#grupoEntidades').val() == ""){
			return true;
		}else if (codentidad != ""){
			var grupoEntidades = $('#grupoEntidades').val().split(',');
			
			for(var i=0;i<grupoEntidades.length;i++){
				if(grupoEntidades[i] == codentidad){
					encontrado = true;
					break;
				}
			}
		}else
			return true;
		return 	encontrado;	
	});
	
	jQuery.validator.addMethod("cp", function(value, element, params) { 
		var codprov = $('#provincia').val();
		var codpostal = value;		
		if(codprov.length == 1){
			codprov = "0" + codprov;
		}	
		
		return 	codprov == codpostal.substring(0,2);	
	});
	
	
	jQuery.validator.addMethod("cif", function(value, element, params) { 
	
			return generales.validaCifNif("CIF", value);
	});
	
	jQuery.validator.addMethod("nif", function(value, element, params) { 
	
			return generales.validaCifNif("NIF", value);
	});	
	
	jQuery.validator.addMethod("invalidChars", function(value, element, params) { 
		var validado = true;
		var invalidChars = ".,:;\u00BF\uDDC7<>[]%!?()-+*_=";
		for(var i = 0; i< value.length;i++){
			if(invalidChars.indexOf(value.charAt(i))!= -1){							
				validado = false;
				break;
			}
		}
		return (this.optional(element) || validado);	
	});
});
	
function modificar(codEntidad, desc_entidad, desc_via, desc_provincia, desc_localidad, cifTomador, razonSoc, via, domicilio, num, piso, bloque, esc, codPostal, provincia, localidad, sublocalidad, telef, movil, email, email2, email3, repreNombre, repreAp1, repreAp2, repreNif, envioAPagos)
{
	/*Limpiamos las desc*/
	$("#desc_entidad").val(desc_entidad);
	$("#desc_via").val(desc_via);
	$("#desc_provincia").val(desc_provincia);
	$("#desc_localidad").val(desc_localidad);
	var frm = document.getElementById('main');
	frm.target="";
	frm.entidad.value = codEntidad;
	frm.ciftomador.value = cifTomador;
	frm.razonsocial.value = razonSoc;
	frm.via.value = via;
	frm.domicilio.value= domicilio;
	frm.numero.value = num;
	frm.piso.value = piso;
	frm.bloque.value = bloque;
	frm.esc.value = esc;
	frm.provincia.value = provincia;
	frm.localidad.value = localidad;
	frm.sublocalidad.value = sublocalidad;
	frm.cp.value = codPostal;
	frm.telefono.value = telef;
	frm.movil.value = movil;
	frm.email.value = email;
	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Inicio */
	frm.email2.value = email2;
	frm.email3.value = email3;
	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Fin */
	//Bloqueamos el campo CIF
	frm.ciftomador.disabled = true;
	frm.entidad.readOnly = 'true';
	frm.repreNombre.value = repreNombre;
	frm.repreAp1.value = repreAp1;
	frm.repreAp2.value = repreAp2;
	frm.repreNif.value = repreNif;
	frm.envioAPagos.value = envioAPagos;
	//Botones
	$('#operacion').val("modificar");
	$("#btnAlta").hide();
	$("#btnModificar").show();
	$("#main").valid();
}

function enviar(operacion) {
	//Desbloqueamos el campo CIF
	document.getElementById('main').ciftomador.disabled = false;
	document.getElementById('main').entidad.disabled = false;
	generales.enviar(operacion,'entidad','ciftomador','razonSocial','via','domicilio','numero','cp','provincia','localidad','sublocalidad','telefono');
}
function enviarForm(operacion,ciftomador,codentidad) {
	if (operacion != 'baja' || confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')) {
		var frm = document.getElementById('main');
		frm.target="";
		//Desbloqueamos el campo CIF
		frm.ciftomador.disabled = false;
		frm.entidad.disabled = false; 
		frm.operacion.value = operacion;
		frm.ciftomadorAccion.value = ciftomador;
		frm.codentidadAccion.value = codentidad;
		frm.submit();
	}
}

function mayusculas(objeto){
	objeto.value = objeto.value.toUpperCase();
}

function alta(){
	var frm = document.getElementById('main');
	frm.target="";
    $("#panelInformacion").hide();
	$('#id').val('');
	$('#operacion').val("alta");
	$('#main').submit();			
}

function consultar(){
	var frm = document.getElementById('main');
	frm.target="";
	$("#main").validate().cancelSubmit = true;
	$("#ciftomador").attr("disabled", false);
	$("#entidad").attr("disabled", false);
	$('#operacion').val("consultar");
	$('#main').submit();			
}
function limpiar(){
	if($('#perfil').val() == "0" || $('#perfil').val() == "5"){
		$('#entidad').val('');
	}
	$('#desc_entidad').val('');
	$('#desc_via').val('');
	$('#desc_provincia').val('');
	$('#desc_localidad').val('');
	$('#ciftomador').val('');				
	$('#razonSocial').val('');
	$('#via').val('');
	$('#domicilio').val('');
	$('#numero').val('');
	$('#piso').val('');
	$('#bloque').val('');
	$('#esc').val('');
	$('#provincia').val('');
	$('#localidad').val('');
	$('#sublocalidad').val('');
	$('#cp').val('');
	$('#telefono').val('');
	$('#movil').val('');
	$('#email').val('');
	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Inicio */
	$('#email2').val('');
	$('#email3').val('');
	/* PTC. 78845 ** CAMPOS NUEVOS (02.03.2022) ** Fin */
	$('#repreNombre').val('');
	$('#repreAp1').val('');
	$('#repreAp2').val('');
	$('#repreNif').val('');
	$('#envioAPagos').val('');
	consultar();
}


function guardarModificaciones(){
	var frm = document.getElementById('main');
	frm.target="";
    $("#panelInformacion").hide();
	$('#operacion').val("modificar");
	$("#ciftomador").attr("disabled", false);
	$("#entidad").attr("disabled", false);
	$('#main').submit();
	if(!$('#main').valid()){
		$("#ciftomador").attr("disabled", true);
		$("#entidad").attr("disabled", true);
	}
}

function imprimir()	{
	var frm = document.getElementById('main');
	frm.operacion.value = 'imprimir';
	frm.target="_blank";
	frm.submit();				
}