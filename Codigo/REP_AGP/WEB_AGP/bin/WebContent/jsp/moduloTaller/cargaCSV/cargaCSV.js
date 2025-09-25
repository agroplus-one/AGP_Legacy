$(document).ready(function(){
	
	// Marca los checks 
	check_checks($('#idsRowsChecked').val());
	
	// Para evitar el cacheo de peticiones al servidor
    var URL = UTIL.antiCacheRand(document.getElementById("formCarga").action);
    document.getElementById("formCarga").action = URL;    

    $('#formCarga').validate({
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
			"plan": {required: true, digits: true, minlength: 4},
			"linea": {required: true, digits: true, minlength: 3},
			"entMed": {required: true, digits: true},
			"subentMed": {required: true, digits: true},
			"file": {required: true, accept: "csv"}		
		},
		messages: {
			"plan": {required: "El campo 'Plan' es obligatorio", minlength: "El campo 'Plan' debe contener 4 dígitos", digits: "El campo 'Plan' solo puede contener dígitos"},
			"linea": {required: "El campo 'Linea' es obligatorio", minlength: "El campo 'Linea' debe contener 3 dígitos", digits: "El campo 'Linea' solo puede contener dígitos"},
			"entMed": {required: "El campo 'Entidad Mediadora' es obligatorio", digits: "El campo 'Entidad Mediadora' solo puede contener dígitos"},
			"subentMed": {required: "El campo 'Subentidad Mediadora' es obligatorio", digits: "El campo 'Subentidad Mediadora' solo puede contener dígitos"},
			"file" : {required: "El campo 'Fichero CSV' es obligatorio", accept: "El campo 'Fichero CSV' solo puede contener ficheros con extensión .csv"}
		}
	});
	
	$("input[type=file]").filestyle({ 
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});
	
	
	// Obtiene todos los enlaces de la paginacion del listado
	$(".pagelinks [href^='cargaCSV.html']").each(function(){ 		
			$(this).bind("click", function(){	
				// Modifica el valor del 'method' para que llame al metodo 'doPaginar'
				$(this).attr('href', $(this).attr('href').replace ('doConsulta', 'doPaginar').replace ('doCargar', 'doPaginar').replace ('doEliminar', 'doPaginar'));
				// Se añaden los ids de los check del listado marcados a la url para mantenerlos marcados al paginar
				arrUrl = $(this).attr('href').split ('idsRowsChecked=');
				$(this).attr('href', (arrUrl[0] + "idsRowsChecked=" + $("#idsRowsChecked").val() + arrUrl[1].substr (arrUrl[1].indexOf ("&"))));
			});
	});
	
	
});



//Funcion para eliminar el filtro y realizar la consulta de cargas
function limpiar(){
	$("#formLimpiar").submit();
}

//Funcion para cargar un fichero
function cargar(){
	
	if($("#formCarga").valid()) {
		$.blockUI.defaults.message = '<h4> Cargando fichero CSV.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#idsRowsChecked').val('');
		$("#formCarga").submit();
	}
}

//Funcion para mostrar el contenido de un archivo CSV
function verContenido (idCarga, nomFichero){

	// Limpio los posibles mensajes que haya en pantalla
	limpiaAlertas();
	
	// submit con los datos necesarios
	$("#idVerContenido").val(idCarga);	
	$("#nomFicheroVerContenido").val(nomFichero);
	$("#formVerContenido").submit();
}

//Funcion para eliminar un archivo de CSV
function eliminar (idCarga){
	if (confirm ("¿Desea eliminar definitivamente el CSV seleccionado?")){
		$("#main").validate().cancelSubmit = true;
		$.blockUI.defaults.message = '<h4> Eliminando fichero de CSV.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#idCargaCSV").val(idCarga);
		$("#method").val("doEliminar");
		$("#main").submit();
	}
}

// Limpia los paneles de alertas
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');
}

// Devuelve un booleano si se ha informado al menos un campo de filtro
function filtroBusquedaInformado () {
	
	var filtroOK = false;
   	
   	if ($('#entidad').val() != '' || $('#entmediadora').val() != '' || $('#subentmediadora').val() != '' ||
   		$('#plan').val() != '' || $('#linea').val() != '' || $('#nombreFichero').val() != ''){   		
   		return true;
   	}
   	
   	return false;
}

//Funcion para realizar la consulta de cargas
function consultar(){
	// Si se ha informado al menos un campo de filtro
	if (filtroBusquedaInformado()) {
		// Si los valores introducidos en el filtro son correctos
		if (validarConsulta()) {
			$('#idsRowsChecked').val('');
			$("#formConsulta").submit();
		}
	}
	else {
		avisoUnCampo ();
	}
}

// Comprueba que los filtros de busqueda informados son correctos
function validarConsulta () {
	if (validaNumerico ('entidad','Entidad') && validaNumerico ('entmediadora','Entidad Mediadora') &&
		validaNumerico ('subentmediadora','Subentidad Mediadora') && validaNumerico ('plan','Plan') &&
		validaNumerico ('linea','L&iacute;nea') && validaEntidadGrupo ()) {
		return true;
	}
	
	return false;
}

// Devuelve un booleano indicando si el valor del campo correspondiente al id 'valId' es numerico y en caso negativo
// muestra un mensaje incluyendo la descripcion del campo indicado por 'desc'
function validaNumerico (valId, desc) {

	if ($('#' + valId).val() != ''){
		var valorOk = false;
		try {
			if(!isNaN($('#' + valId).val())) valorOk = true;
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!valorOk) {
			$('#panelAlertasValidacion').html("Valor para el campo '" + desc + "' no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	return true;
}

// Comprueba que el codigo de entidad indicado en el formulario de busqueda esta incluido en el grupo de entidades
// asociado al usuario (solo perfil 5)
function validaEntidadGrupo () {
	var codentidad = $('#entidad').val();
	if($('#grupoEntidades').val() == ""){
		return true;
	}else if (codentidad != ""){
		var grupoEntidades = $('#grupoEntidades').val().split(',');
		var encontrado = false;
		for(var i=0;i<grupoEntidades.length;i++){
			if(grupoEntidades[i] == codentidad){
				encontrado = true;
				break;
			}
		}
		
		if (!encontrado) {
			$('#panelAlertasValidacion').html("La entidad indicada no pertenece al grupo de entidades del usuario");
			$('#panelAlertasValidacion').show();
		}
		
	}else
		return true;
	return 	encontrado;	
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

// Comprueba si se ha marcado algun check asociada a registro de carga y muestra un aviso en caso negativo o llama a la funcion
// para eliminarlo en caso afirmativo
function borradoMasivo () {
	if ($.trim($('#idsRowsChecked').val()) == '') {
		$('#panelAlertasValidacion').html("Es necesario seleccionar al menos una carga");
		$('#panelAlertasValidacion').show();
	}
	else {
		eliminar (null);
	}
}

// Funcion para eliminar el archivo de PAC correspondiente al id de carga pasado como parametro. Si este parametro es nulo, 
// se borraran todos las cargas asociadas a los ids almacenados en el hidden 'idsRowsChecked' (borrado masivo)
function eliminar (idCarga){
	
	var pregunta;
	if (idCarga == null) pregunta = "¿Desea eliminar todos los registros de carga de CSV seleccionados?";
	else pregunta = "¿Desea eliminar el registro de carga de CSV seleccionado?";
	
	if (confirm (pregunta)){
		// Si es un borrado individual, se vacia la lista de ids de borrado masivo y se añade el id de la carga seleccionada		
		if (idCarga != null) { 
			$('#idsRowsChecked').val('');
			addCheck2 (idCarga);
		}
		// Muestra la capa de bloqueo y llama al metodo que borra los registros
		$.blockUI.defaults.message = '<h4> Eliminando registros de carga de CSV.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#methodConsultar").val('doEliminar');
		$("#formConsulta").submit();
	}
}


//Funcion para descargar el fichero de plantilla
function plantilla(){
	
	if($("#formPlantilla").valid()) {
		$("#formPlantilla").submit();
	}
}

//Funcion para descargar el fichero de manual
function manual(){
	
	if($("#formManual").valid()) {
		$("#formManual").submit();
	}
}

// -------------------
// Gestion de checks para el borrado masivo 
// -------------------

// Si el check pasado como parametro esta marcado añade su id a la lista de ids marcados y si esta desmarcado lo elimina
function onClickInCheck (check, idCarga){
		if (check.checked) addCheck2(idCarga);
		else subtractCheck2(idCarga);
}

// AÃ±ade el id de carga pasado como parametro de la lista de ids marcados
function addCheck2(check){
	if($('#idsRowsChecked').val() != null) $('#idsRowsChecked').val($('#idsRowsChecked').val() + ";" + check);
}

// Elimina el id de carga pasado como parametro de la lista de ids marcados
function subtractCheck2(check){
	var newList = "";
	var ids = $('#idsRowsChecked').val();
	
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			if(array_ids[i] != check && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
				newList = newList + array_ids[i] + ";";
			}
		}
	}
	
	$('#idsRowsChecked').val(newList);
	
}

// Marca los checks de registro de carga de csv que esten marcados
function marcarTodos () {
	// Marca los checks que estan desmarcados
	$("input[id^='checkCarga_']").each(function(){ 				        		    
		if ($(this).attr('checked') == false) {
			$(this).attr('checked',true);			
		}
	});
	// Copia la lista de todos los ids del listado al input que gestiona los checks marcados
	$('#idsRowsChecked').val($('#idsRowsTodos').val());
}

// Desmarca los checks de registro de carga de csv que estan marcados
function desmarcarTodos () {
	$("input[id^='checkCarga_']").each(function(){
		if ($(this).attr('checked')) {
			$(this).attr('checked',false);			
		}				
	});
	// Vacia el input que gestiona los checks marcados
	$('#idsRowsChecked').val('');
}

// Marca todos los checks cuyo id esta incluido en la cadena recibida como parametro
function check_checks(ids){
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			var idCheck = "checkCarga_" + array_ids[i];
			$('#' + idCheck).attr('checked',true);
		}
	}
}