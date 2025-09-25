$(document).ready(function(){
	$('#panelduplicarInf').hide();
	
	// comunes perfil 1 y 5
	if ($('#perfil').val() == 5 || $('#perfil').val() == 1){
		// "Todos" deshabilitado
		$('#checkTodos').attr('disabled', true);
		// "usuarios" deshabilitado
		$('#checkUsuarios').attr('disabled', true);
		deshabilitarIconosUsuarios();
		// entidades habilitado
		$('#checkEntidades').attr('checked', true);
		$('#checkEntidades').attr('disabled', true);
		$('#multEnts').attr('disabled', false);
		
		// anadimos la entidad por defecto siempre que no estemos volviendo de una modificacion 
		if ($('#entidadAux').val() != "" && $('#idInforme').val() ==""){
		
			addValue ($('#entidadAux').val(), 'multEnts');
		}
	}
	
	
	
	var arregloDeEntidades = $('#entidadesBox').val().split('#');
	for (var i=0; i < arregloDeEntidades.length; i++) {
		if (arregloDeEntidades[i]!=""){
			addValue (arregloDeEntidades[i], 'multEnts');
		}
	}
	
	var arregloDeUsuarios = $('#usuariosBox').val().split('#');
	for (var i=0; i < arregloDeUsuarios.length; i++) {
		if (arregloDeUsuarios[i]!=""){
			addValue (arregloDeUsuarios[i], 'multUsers');
		}
	}
	if (arregloDeUsuarios!=""){
		habilitarIconosUsuarios (); // Mostrar los iconos de usuario
		$('#checkUsuarios').attr('checked', true);
		$('#checkEntidades').attr('checked', false);
		$('#checkEntidades').attr('disabled', true);
	}
	if (arregloDeEntidades!=""){
		habilitarIconosEntidades (); // Mostrar los iconos de usuario
		$('#checkEntidades').attr('checked', true);
		$('#checkUsuarios').attr('checked', false);
		$('#checkUsuarios').attr('disabled', true);
	}
	
	// perfil 5
	if ($('#perfil').val() == 5){
		habilitarIconosEntidades();
	}
	if ($('#perfil').val() == 1){
		$('#iconoBorrarEnt').hide();
		$('#iconoLupaEnt').hide();
	}
	
	// Captura el evento del click para todos los checks de la página
	$(':checkbox').bind ('click', function() {
		limpiaAlertas();
  		gestionarChecks ($(this).attr('id'));
	});
	
	// Captura el evento change del input donde se guarda la entidad seleccionada en la lupa
	$('#entidad').bind ('change', function() {
		addValue ($(this).val(), 'multEnts');
		$(this).val('');
	});
	//TMR 15/02/2013 este codigo ahora se hace en la funcion cambiarMultipleUser() de lupaUsuarioMulti.jsp
	// Captura el evento change del input donde se guarda el usuario seleccionado en la lupa
	//$('#codusuario').bind ('change', function() {
	//	addValue ($(this).val(), 'multUsers');
	//	$(this).val('');
	//});
	
	// Si el campo hay algún informe seleccionado se muestran los botones 'Generar' y 'Modificar' 
	if ($('#idInforme').val().length > 0) {
		$('#btnModificar').show();
		$('#btnGenerar').attr('disabled', false);
	} else {
		$('#btnGenerar').attr('disabled', true);
	} 
	
	// Para cada check marcado realiza las acciones que se lanzarían se se hubiese marcado a mano
	actualizaVisibilidad();
    
    var URL = UTIL.antiCacheRand(document.getElementById("main").action);
    document.getElementById("main").action = URL;
    
    $('#main').validate({	
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEdita") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
								
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"nombre" : {required: true},
	 		"titulo1" : {required: true},
	 		"visibilidad" : {required: function () { return checkVisibilidad ();}},
	 		"cuenta" : {required: true},
	 		"cadenaCodigosLupas" : {required : function () { return lupasInformadas ();}}
		},
		messages: {
			"nombre":{required: "El campo Nombre es obligatorio"},
			"titulo1":{required: "El campo Titulo1 es obligatorio"},
			"visibilidad":{required: "El campo Visibilidad es obligatorio"},
			"cuenta":{required: "El campo Cuenta es obligatorio"},
			"cadenaCodigosLupas":{required: function () { return msgLupasInformadas ();}}
	 	}
	});
}); 



/**
 * funcion que se ejecuta una vez se hayan cargado todos los elementos del formulario
 */

function checksOnload(){
	if( $('#checkPerfil').is(':checked') ) {
		$('#perfilRadio').attr('disabled', false); // Habilitar el combo de 'Perfil'
	}
}
/**
 * Devuelve true si no se ha seleccionadon ningún check en el apartado Visibilidad
 */
function checkVisibilidad () {
	return ($('input[type=checkbox]:checked').length == 0);
}

/**
 * Devuelve true si se ha marcado el check de usuarios o de entidades y no se ha seleccionado ningún código
 */
function lupasInformadas () {
	if ($('#checkUsuarios').attr ('checked') || $('#checkEntidades').attr ('checked')) {
		if ($('#cadenaCodigosLupas').val().length==0) {
			return true;
		}
	}	
	return false;
}

/**
 * Devuelve el mensaje a mostrar si se ha marcado el check de usuarios o de entidades y no se ha seleccionado ningún código
 */
function msgLupasInformadas () {
	if ($('#checkUsuarios').attr ('checked')) {
			return "Debe seleccionar al menos un usuario en Visibilidad";
	}
	else if ($('#checkEntidades').attr ('checked')) {
			return "Debe seleccionar al menos una entidad en Visibilidad";
	}	
}

/**
 * Se ejecuta cuando se marca o desmarca algún check de la pantalla, cuyo id llega como parámetro
 * Dependiendo del check que se haya modificado llamará a una función para gestionar dicho cambio
 */
function gestionarChecks (idObj) {
	
	if (idObj == 'checkTodos') {
		gestionarCheckTodos();
	}
	else if (idObj == 'checkPerfil') {
		gestionarCheckPerfiles();
	}
	else if (idObj == 'checkUsuarios') {
		gestionarCheckUsuarios();
	}
	else if (idObj == 'checkEntidades') {
		
		gestionarCheckEntidades();
	}
}

/**
 * Gestión del cambio del check de visibilidad 'Todos'
 */
function gestionarCheckTodos () {
	// Se ha marcado
	if ($('#checkTodos').attr('checked')) {
		deshabilitarPerfil(); // Deshabilitar y borrar 'Perfil' 
		deshabilitarUsuarios(); // Deshabilitar y borrar 'Usuarios'
		deshabilitarIconosUsuarios (); // Ocultar iconos de usuario
	}
	// Se ha desmarcado
	else {
		$('#checkPerfil').attr ('disabled', false); // Habilitar el check de 'Perfil'
		habilitarUsuarios (); // Habilitar el check de 'Usuarios' si no está marcado el de 'Entidades'
	}
}

/**
 * Gestión del cambio del check de visibilidad 'Perfil'
 */
function gestionarCheckPerfiles () {
	// Se ha marcado
	if ($('#checkPerfil').attr('checked')) {
		deshabilitarTodos(); // Deshabilitar y borrar 'Todos'
		$('#perfilRadio').attr('disabled', false); // Habilitar el combo de 'Perfil'
		deshabilitarUsuarios(); // Deshabilitar y borrar 'Usuarios'
		deshabilitarIconosUsuarios(); // Ocultar iconos de usuario
	}
	// Se ha desmarcado
	else {
		$('#perfilRadio').val(0);
		$('#perfilRadio').attr('disabled', true); // Deshabilitar y borrar el combo de 'Perfil' 
		if ($('#perfil').val() == 0)
			$('#checkTodos').attr ('disabled', false); // Habilitar el check de 'Todos'
	
		if ($('#perfil').val() == 0) // si es perfil 1 o 5 no lo habilita porque ya esta habilitado el de entidades
			habilitarUsuarios (); // Habilitar el check de 'Usuarios' si no está marcado el de 'Entidades'
	}
}

/**
 * Gestión del cambio del check de visibilidad 'Usuarios'
 */
function gestionarCheckUsuarios () {
	// Se ha marcado
	
	if ($('#checkUsuarios').attr('checked')) {
		
		deshabilitarTodos(); // Deshabilitar y borrar 'Todos'
		deshabilitarPerfil(); // Deshabilitar y borrar 'Perfil'
		habilitarIconosUsuarios (); // Mostrar los iconos de usuario
		deshabilitarEntidades(); // Deshabilitar y borrar 'Entidad'
		deshabilitarIconosEntidades(); // Ocultar iconos de entidades
	}
	// Se ha desmarcado
	else {
		
		$('#checkTodos').attr ('disabled', false); // Habilitar el check de 'Todos'
		$('#checkPerfil').attr ('disabled', false); // Habilitar el check de 'Perfil'
		$('#multUsers').empty();
		$('#multUsers').attr('disabled', true);
		$('#cadenaCodigosLupas').val(''); // Borra el hidden que almacena todos los usuarios seleccionados
		$('#checkEntidades').attr ('disabled', false); // Habilitar el check de 'Perfil'
		deshabilitarIconosUsuarios(); // Ocultar los iconos de usuario
	}
}

/**
 * Gestión del cambio del check de visibilidad 'Entidades'
 */
function gestionarCheckEntidades () {
	// Se ha marcado
	
	if ($('#checkEntidades').attr('checked')) {
		deshabilitarUsuarios(); // Deshabilitar y borrar 'Usuarios'
		deshabilitarIconosUsuarios (); // Ocultar iconos de usuario
		if ($('#perfil').val() == 0){
			habilitarIconosEntidades (); // Mostrar los iconos de entidades
		}
		if ($('#perfil').val() == 1){
			$('#multEnts').attr ('disabled', false);
			$('#iconoBorrarEnt').hide();
			$('#iconoLupaEnt').hide();
   	    }
		if ($('#perfil').val() == 5){
			habilitarIconosEntidades();
			$('#checkEntidades').attr('checked', true);
			$('#checkEntidades').attr('disabled', true);
		}
			
	}
	// Se ha desmarcado
	else {
		if ($('#checkTodos').attr ('checked') == false && $('#checkPerfil').attr ('checked') == false) {
			$('#checkUsuarios').attr ('disabled', false); // Habilitar el check de 'Usuarios' si el de 'Todos' y el de 'Perfil' no están marcados			
		}
		$('#multEnts').empty();
		$('#multEnts').attr('disabled', true);
		$('#cadenaCodigosLupas').val(''); // Borra el hidden que almacena todos los usuarios seleccionados
		deshabilitarIconosEntidades (); // Ocultar los iconos de entidades
	}
}

/**
 * Deshabilita la visibilidad 'Todos'
 */
function deshabilitarTodos () {
	$('#checkTodos').attr('checked', false);
	$('#checkTodos').attr('disabled', true);
}

/**
 * Deshabilita la visibilidad por perfil
 */
function deshabilitarPerfil () {
	$('#checkPerfil').attr('checked', false);
	$('#checkPerfil').attr('disabled', true);
	$('#perfilRadio').val(0);
	$('#perfilRadio').attr('disabled', true);
}

/**
 * Deshabilita la visibilidad por perfil
 */
function deshabilitarUsuarios () {
	$('#checkUsuarios').attr('checked', false);
	$('#checkUsuarios').attr('disabled', true);
	$('#multUsers').empty();
	$('#multUsers').attr('disabled', true);
	$('#cadenaCodigosLupas').val(''); // Borra el hidden que almacena todos los usuarios seleccionados
}

/**
 * Deshabilita la visibilidad por perfil
 */
function deshabilitarEntidades () {
	$('#checkEntidades').attr('checked', false);
	$('#checkEntidades').attr('disabled', true);
	$('#multEnts').empty();
	$('#multEnts').attr('disabled', true);
	$('#cadenaCodigosLupas').val(''); // Borra el hidden que almacena todos los usuarios seleccionados
}

/**
 * Habilita la visibilidad por perfil
 */
function habilitarPerfil () {
	$('#checkPerfil').attr('disabled', false);
	$('#perfilRadio').attr('disabled', false);
}

/**
 * Habilitar el check de 'Usuarios' si no está marcado el de 'Entidades'
 */
function habilitarUsuarios () {
	
	if ($('#checkEntidades').attr('checked') == false) {
		$('#checkUsuarios').attr ('disabled', false); 
	}
}

/**
 * Habilita el combo de Usuarios y muestra sus iconos de borrar y seleccionar usuarios
 */
function habilitarIconosUsuarios () {
	$('#multUsers').attr ('disabled', false);
	$('#iconoBorrar').show();
	$('#iconoLupa').show();;
}

/**
 * Deshabilita el combo de Usuarios y oculta sus iconos de borrar y seleccionar usuarios
 */
function deshabilitarIconosUsuarios(){
	$('#multUsers').attr ('disabled', true);
	$('#iconoBorrar').hide();;
	$('#iconoLupa').hide();;
}

/**
 * Habilita el combo de Entidades y muestra sus iconos de borrar y seleccionar entidades
 */
function habilitarIconosEntidades () {
	
	$('#multEnts').attr ('disabled', false);
	$('#iconoBorrarEnt').show();
	$('#iconoLupaEnt').show();;
	
}

/**
 * Deshabilita el combo de Entidades y oculta sus iconos de borrar y seleccionar entidades
 */
function deshabilitarIconosEntidades(){
	$('#multEnts').attr ('disabled', true);
	$('#iconoBorrarEnt').hide();
	$('#iconoLupaEnt').hide();
}


function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main');
    						
    $.get('mtoInformes.run?ajax=true&origenLlamada='+frm.origenLlamada.value+'&cadenaCodigosLupas='+frm.cadenaCodigosLupas.value+'&' + parameterString, function(data) {
    $("#grid").html(data)
		});
} 

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = '${pageContext.request.contextPath}/mtoInformes.run?ajax=false&' + parameterString;
}


/**
 * Limpia las posibles alertas y mensajes que se estuviesen mostrando
 */ 
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}	

/**
 * Limpia el formulario
 */
function limpiarFormulario (subirRegistro,limpiar) {
	$("#nombre").val('');	
	$('#titulo1').val('');
	$('#titulo2').val('');
	$('#titulo3').val('');
	$('#visibilidad').val('');
	$('#cuenta').val('');
	$('#codusuario').val('');
	$('#perfilRadio').val(0);
	$('#origenLlamada').val('menuGeneral');
	$('#fechaAlta').val('');
	
	// Visibilidad
	limpiarVisibilidad (subirRegistro,limpiar);
}

/**
 * Limpia el formulario y lanza la búsqueda
 */
function limpiar(){
	limpiaAlertas();
	habilitarBotones();
	var subirRegistro = false;
	var limpiar = true;
	limpiarFormulario (subirRegistro,limpiar);
	$('#btnGenerar').attr('disabled','disabled');
	$("#btnModificar").hide();	
	
	var frm = document.getElementById('main');
	frm.perfilRadio.value = 0;
	frm.origenLlamada.value = "menuGeneral";
	var limpiar = true;
	consultar(limpiar);								
}

/**
 * Limpia los controles asociados al campo 'Visibilidad'
 */
function limpiarVisibilidad (subirRegistro,limpiar) {
	
	
		$('#checkTodos').attr ('disabled', false);
		$('#checkTodos').attr ('checked', false);
		$('#checkPerfil').attr ('disabled', false);
		$('#checkPerfil').attr ('checked', false);
		$('#perfilRadio').val(0);
		$('#perfilRadio').attr ('disabled', true);
		$('#checkUsuarios').attr ('disabled', false);
		$('#checkUsuarios').attr ('checked', false);
		$('#multUsers').empty();
		
		
		$('#cadenaCodigosLupas').val('');
		
		
		
		if ($('#perfil').val() == 0){
			$('#checkTodos').attr ('disabled', false);
			$('#checkEntidades').attr ('disabled', false);
			$('#checkEntidades').attr ('checked', false);
			$('#multEnts').empty(); 
			deshabilitarIconosEntidades (); // Ocultar los iconos de entidades
			$('#checkUsuarios').attr ('disabled', false);
			$('#checkUsuarios').attr ('checked', false);
			deshabilitarIconosUsuarios(); // Ocultar los iconos de usuario

		}else if ($('#perfil').val() == 1){
			
			$('#checkTodos').attr ('disabled', true);
			$('#checkEntidades').attr ('disabled', true);
			$('#checkEntidades').attr ('checked', true);
			deshabilitarIconosEntidades (); // Ocultar los iconos de entidades
			$('#multEnts').attr ('disabled', false);
			$('#checkUsuarios').attr ('disabled', true);
			$('#checkUsuarios').attr ('checked', false);
			deshabilitarIconosUsuarios(); // Ocultar los iconos de usuario
		
		
		}else if ($('#perfil').val() == 5){
			$('#checkTodos').attr ('disabled', true);
			$('#checkEntidades').attr ('checked', true);
			$('#checkEntidades').attr ('disabled', true);
			habilitarIconosEntidades ();
			$('#checkUsuarios').attr ('disabled', '');
			$('#checkUsuarios').attr ('checked', false);
			deshabilitarIconosUsuarios(); // Ocultar los iconos de usuario
			
			
			$('#multEnts').empty();
			if (!subirRegistro){
				// si estamos subiendo el registro (ej: consultar o editar) no añadimos la entidad por defecto
				if ($('#entidadAux').val() != ""){
					addValue ($('#entidadAux').val(), 'multEnts');
				
				}
			}
			if (limpiar){
				$('#multEnts').empty();
				addValue ($('#entidadAux').val(), 'multEnts');
			}
		}
}

/**
 * Añade al filtro de búsqueda los campos introducidos en el formulario
 */ 
function comprobarCampos(limpiar){
	jQuery.jmesa.removeAllFiltersFromLimit('mtoInforme');
	
 	if ($('#nombre').val() != '') 	jQuery.jmesa.addFilterToLimit('mtoInforme','nombre', $('#nombre').val());
 	if ($('#titulo1').val() != '')	jQuery.jmesa.addFilterToLimit('mtoInforme','titulo1', $('#titulo1').val());
 	if ($('#titulo2').val() != '')	jQuery.jmesa.addFilterToLimit('mtoInforme','titulo2', $('#titulo2').val());
 	if ($('#titulo3').val() != '')	jQuery.jmesa.addFilterToLimit('mtoInforme','titulo3', $('#titulo3').val());
	
	// Visibilidad
 	if ($('#checkTodos').attr ('checked')) jQuery.jmesa.addFilterToLimit('mtoInforme','visibilidad', $('#vsbTodos').val());
 	else if ($('#checkPerfil').attr ('checked')) jQuery.jmesa.addFilterToLimit('mtoInforme','visibilidad', $('#vsbPerfil').val());
 	else if ($('#checkUsuarios').attr ('checked')) jQuery.jmesa.addFilterToLimit('mtoInforme','visibilidad', $('#vsbUsuarios').val());
 	
 	// Visibilidad Entidades
 	if (limpiar != true){
 		if ($('#checkEntidades').attr ('checked')) jQuery.jmesa.addFilterToLimit('mtoInforme','visibilidadEnt', $('#vsbEntidades').val());
 	}
 	
 	// Perfil
 	if (!$('#perfilRadio').attr ('disabled')) jQuery.jmesa.addFilterToLimit('mtoInforme','perfil', $('#perfilRadio').val());
 	
    // Cuenta
    if ($('#cuenta').val() != '')	jQuery.jmesa.addFilterToLimit('mtoInforme','cuenta', $('#cuenta').val());
    
    //codUsuario
    if ($('#codusuario').val() != '') jQuery.jmesa.addFilterToLimit('mtoInforme','usuario.codusuario', $('#codusuario').val());
		
 	 	
}

function visualizarRegistro (id,nombre,titulo1,titulo2, titulo3,visib,visibEnt,perfilInforme,usuariosStr,cuenta,
		codusuario,subirRegistro) {
	// Limpia todo el formulario y las alerta
 	limpiaAlertas();
 	
 	limpiarFormulario(subirRegistro);
	
	// Carga los datos en el formulario
	$('#idInforme').val(id);
	$('#nombre').val(nombre);
	$('#titulo1').val(titulo1);
	$('#titulo2').val(titulo2);
	$('#titulo3').val(titulo3);
	$('#cuenta').val(cuenta);
	$('#codusuario').val(codusuario);
	
	// Visibilidad
	if (visib == $('#vsbTodos').val()) { // Todos
		$('#checkTodos').attr("checked", true);
		gestionarChecks ('checkTodos');
	}
	else if (visib == $('#vsbPerfil').val()) { // Perfil
		$('#checkPerfil').attr("checked", true);
		gestionarChecks ('checkPerfil');
		$('#perfilRadio').val(perfilInforme);
		$('#perfilRadio').change();
	}
	else if (visib == $('#vsbUsuarios').val()) { // Usuarios
		$('#checkUsuarios').attr("checked", true);
		gestionarChecks ('checkUsuarios');
		$('#cadenaCodigosLupas').val(usuariosStr);
		cargarLupasCodigos ('multUsers');
	}
	// Visibilidad entidad
	if (visibEnt == $('#vsbEntidades').val()) { // Entidades
		
		$('#checkEntidades').attr("checked", true);
		gestionarChecks ('checkEntidades');
		$('#cadenaCodigosLupas').val(usuariosStr);
		cargarLupasCodigos ('multEnts');
	}
	
	habilitarBotones();
}
 
/**
 * 
 */
function editar(id,nombre,titulo1,titulo2, titulo3,visib,visibEnt,perfilInforme,usuariosStr,cuenta, propietario, fechaAlta){
	var subirRegistro = true;
	visualizarRegistro (id,nombre,titulo1,titulo2, titulo3,visib,visibEnt,perfilInforme,usuariosStr,cuenta, propietario,subirRegistro);
 	$('#fechaAlta').val(fechaAlta);
	$('#btnModificar').show();
}	

function visualizar(id,nombre,titulo1,titulo2, titulo3,visib,visibEnt,perfilInforme,usuariosStr,cuenta, propietario){
	
	var subirRegistro = true;
	visualizarRegistro (id,nombre,titulo1,titulo2, titulo3,visib,visibEnt,perfilInforme,usuariosStr,cuenta, propietario,subirRegistro);
	$('#btnModificar').hide();
}

function habilitarBotones(){
	$('#btnDatosInforme').attr('disabled','');
	$('#btnCondiciones').attr('disabled','');
	$('#btnClasif_Ruptura').attr('disabled','');
	$('#btnGenerar').attr('disabled','');
	$('#btnAlta').attr('disabled','');
}

function ocultarBotones(){
	$('#btnDatosInforme').attr('disabled','disabled');
	$('#btnCondiciones').attr('disabled','disabled');
	$('#btnClasif_Ruptura').attr('disabled','disabled');
	$('#btnAlta').attr('disabled','disabled');
}


function borrar(id) {
	$('#origenLlamada').val('');
	$('#main').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar este Informe?')){
		var frm = document.getElementById('main');
		frm.method.value = 'doBaja';
		frm.idInforme.value = id;				
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main").submit();	
		}	
}

function consultar(limpiar){
	$("#btnModificar").hide();
	$('#btnGenerar').attr('disabled','disabled');
	$('#origenLlamada').val('');
	limpiaAlertas();
	comprobarCampos(limpiar);
	
	cargarInputCodigos (limpiar);
	
	onInvokeAction('mtoInforme','filter');
	
}	

/**
 * Se llama al controlador para el alta si las validaciones previas son correctas 
 */
function alta() {
	limpiaAlertas();
	$('#origenLlamada').val('');
	$('#redireccion').val('');
	// Inserta los datos del select cuyo check esté seleccionado en un hidden
	var limpiar = false;
    cargarInputCodigos (limpiar);
   // para usuarios de perfil 1 y 5
    if ($('#checkEntidades').attr ('checked')){
    	$('#checkEntidades').attr('disabled', false);
    }
    $('#method').val('');
    var frm = document.getElementById('main');
    frm.method.value = 'doAlta';
 	$('#idInforme').val('');
 	jQuery.jmesa.removeAllFiltersFromLimit('mtoInforme');
 	$('#main').submit();
}

/**
 * Se llama al controlador para la modificación si las validaciones previas son correctas 
 */
function modificar(){
	limpiaAlertas();
	$('#origenLlamada').val('');
	$('#redireccion').val('');
	 // para usuarios de perfil 1 y 5
    if ($('#checkEntidades').attr ('checked')){
    	$('#checkEntidades').attr('disabled', false);
    }
	// Inserta los datos del select cuyo check esté seleccionado en un hidden
    var limpiar = false;
    cargarInputCodigos (limpiar);
	var frm = document.getElementById('main');
	frm.method.value = 'doEdita';	
	$('#main').submit();		
}	


	
function redirigir(redireccion){
	var modif = document.getElementById('btnModificar');
	
	// Para diferenciar si es un alta de informe o éste ya existe se comprueba si ha rellenado el id del informe en el hidden
	var frm = document.getElementById('main');
	var idInforme = frm.idInforme.value;
	
	// Inserta los datos del select cuyo check esté seleccionado en un hidden
	var limpiar = false;
    cargarInputCodigos (limpiar);
	
	// Si es un número, se ha insertado el id del informe en el hidden
	if (idInforme != ''){ //modificar
		frm.method.value = 'doEdita';
		frm.redireccion.value = redireccion;
		$('#main').submit();
	}
	// Si no es un número no hay id, por lo que es un alta de informe.
	else{ // alta
		limpiaAlertas();
	    frm.method.value = 'doAlta';
	    frm.redireccion.value = redireccion;
     	$('#main').submit();
	}
		
}

/**
 * Devuelve un boolean dependiendo si 'value' existe como valor en algún option de 'combo'
 */
function existeEnCombo (value, combo) {
	return (($('#' + combo + ' option[value=' + value + ']').length) >= 1);
}

/**
 * Añade al combo múltiple el valor indicado sin permitir repetidos 
 */
function addValue (value, combo) {
	// Se añade el código de entidad si no existe ya en el combo
	if (!existeEnCombo (value, combo)) {
		var selObj = document.getElementById(combo);
		selObj.options[selObj.length] = new Option(value, value);
	}
}

/**
 * Elimina todos los elementos seleccionados del combo indicado en el parámetro
 */
function deleteValue (combo) {
	$('#' + combo + ' [selected = true]').remove();
}

/**
 * Inserta en la lista de códigos los datos del select cuyo check esté seleccionado en un hidden
 */
function cargarInputCodigos (limpiar) {
	
	$('#cadenaCodigosLupas').val('');
	if (limpiar != true){
		if ($('#checkUsuarios').attr ('checked')) {
			$('#multUsers option').each ( function () {
				actualizaInput ($(this).val());
			});
		}
		else if ($('#checkEntidades').attr ('checked')) {
			$('#multEnts option').each ( function () {
				actualizaInput ($(this).val());
			});
		}
	}
	
}

/**
 * Carga los select de 'Usuarios' o 'Entidades' con los datos del hidden
 */
function cargarLupasCodigos (combo) {
	// Si el hidden tiene datos
	deleteValue("multEnts");
	if ($('#cadenaCodigosLupas').val().length > 0) {
		// Parte la cadena por el caracter '#'
		var arrCadena = $('#cadenaCodigosLupas').val().split('#');
		// Recorre el array e inserta el valor en el combo correspondiente
		for (i=0; i<arrCadena.length; i++) {
			addValue (arrCadena[i], combo);
		}
	}
} 

/**
 * Inserta value en el hidden correspondiente 
 */
function actualizaInput (value) {
	// Comprueba si hay que añadir una coma antes de insertar el valor
	if ($('#cadenaCodigosLupas').val().length > 0) {
		$('#cadenaCodigosLupas').val($('#cadenaCodigosLupas').val() + '#');
	}
	
	$('#cadenaCodigosLupas').val($('#cadenaCodigosLupas').val() + value);
}

	
/**
 * Para cada check marcado realiza las acciones que se lanzarían se se hubiese marcado a mano
 */ 
function actualizaVisibilidad () {
	
	$(':checkbox [checked=true]').each (function() {
		$(this).trigger('click');
	});
}


