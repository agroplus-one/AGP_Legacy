$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    if ($('#btnModificar').is(":visible") ){
    	$('#entidad').attr('readonly', true);
		$('#oficina').attr('readonly', true);
		$('#desc_entidad').attr('readonly', true);
	}
    
    var zonaSelAlta = $("#zonaSelAlta").val();
    if (zonaSelAlta != ""){
       seleccionaZonasAlta();
    }
   
	$('#main3').validate({					
	
		onfocusout: function(element) {
			var frm = document.getElementById('main3');
			if ( (frm.method.value == "doEditar") || (frm.method.value == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"id.codentidad":{required: true,digits: true},
			"id.codoficina":{required: true,digits: true},
			"pagoManual": {required: true},
			// GDLD-63701 ** MODIF TAM (24/08/2021) * Defecto 8 
			//"listaZonas":{validaZonasJq:true},
			"nomoficina":{required: true}
		},
		 messages: {
		 	"id.codentidad":{required: "El campo Entidad es obligatorio", digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"id.codoficina":{required: "El campo Oficina es obligatorio", digits: "El campo Oficina s\u00F3lo puede contener d\u00EDgitos"},
		 	"pagoManual": {required: "El campo Pago manual es obligatorio"},
			// GDLD-63701 ** MODIF TAM (24/08/2021) * Defecto 8 
		 	//"listaZonas":{validaZonasJq:"El campo Zonas es obligatorio"},
		 	"nomoficina": {required: "El campo Nombre oficina es obligatorio"}
		 	
		 }
	});
	
	jQuery.validator.addMethod("validaZonasJq", function(value, element, params) {		
		return validaZonas();
	});
	
	showOrHideExportIcon();
		
});


function consultar () {
	limpiaAlertas(); 
	
	var frm = document.getElementById('main3');
	
	if (validarConsulta()){
		if (comprobarCampos(false)){
			asignaZonasSeleccionadas();
			frm.method.value = 'doConsulta';
			frm.origenLlamada.value= 'primeraBusqueda';
			$("#main3").validate().cancelSubmit = true;
			$('#main3').submit();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
				 			
	}

function comprobarCampos(incluirJmesa){
	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaOficinasPagoManual');
	}
   	var resultado = false;
   	
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual','id.codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual','id.codoficina', $('#oficina').val());
   		}
   		resultado = true;
   	}
   	if ($('#desc_oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual', 'nomoficina', $('#desc_oficina').val());
   		}
   		resultado = true;
   	}
   	if ($('#pagoManual').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual', 'pagoManual', $('#pagoManual').val());
   		}
   		resultado = true;
   	}
   	if ($('select[name="listaZonas2"] option:selected').length != 0){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual', 'codZona', zonasSeleccionadas(document.getElementById('listaZonas2')));
   		}
   		resultado = true;
   	}
   	if ($('#zonaSel').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaOficinasPagoManual','Zonas', $('#zonaSel').val());
   		}
   		resultado = true;
   	}
   	
	return resultado;
}
 
//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	
	/* P0063701 ** MODIF TAM (25.08/2021) ** Defecto 6 ** Inicio */
	jQuery.jmesa.removeAllFiltersFromLimit('consultaOficinasPagoManual');
	onInvokeAction('consultaOficinasPagoManual','clear');
	
	$('#limpiar').submit();
}

function editar(entidad,oficina,pagoManual,nomoficina,nomentidad,idgrupo,zonSel){

	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.entidad.value = entidad;
	frm.oficina.value = oficina;
	frm.pagoManual.value = pagoManual;
	frm.desc_oficina.value = nomoficina;
	frm.desc_entidad.value = nomentidad;
	frm.idgrupo.value = idgrupo;
	/* Pet. 63701 ** MODIF TAM (25.06.2021) ** Inicio */
	frm.zonaSel = zonSel;
	seleccionaZonas(zonSel);
	
	$('#entidad').attr('readonly', true);
	$('#oficina').attr('readonly', true);
	$('#desc_entidad').attr('readonly', true);
	
	$("#btnModificar").show();
}


function modificar(){
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if (frm.pagoManual.value== ""){
		$('#panelAlertasValidacion').html("Debe seleccionar un valor para el campo pago manual");
		$('#panelAlertasValidacion').show();
	}else{
		asignaZonasSeleccionadasModifAlta("Modif");
		frm.method.value= "doEditar";
		$('#main3').submit();
	}
}

function onInvokeAction(id) {
	
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('pagoManual.run?ajax=true&zonaSel='+$("#zonaSel").val() + '&' + decodeURIComponent(parameterString), function(data) {
    
        $("#grid").html(data);
        showOrHideExportIcon();
        comprobarChecks();
		});
}


function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'pagoManual.run?ajax=false&zonaSel='+$("#zonaSel").val() + '&'  + parameterString;
}


function borrar(entidad,oficina,nomoficina,pagoManual,nomentidad) {
	
	limpiaAlertas();
	
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')){
		$("#methodBorrar").val("doBorrar");
		$("#entidadBorrar").val(entidad);
		$("#oficinaBorrar").val(oficina);
		$("#nomoficinaBorrar").val(nomoficina);
		$("#pagoManualBorrar").val(pagoManual);
		$("#nomentidadBorrar").val(nomentidad);
		
		$("#frmBorrar").submit();
	}	
}

 
function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	/* Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */
	asignaZonasSeleccionadasModifAlta("Alta");
	frm.method.value= "doAlta";
	$("#main3").submit();	
	/* Pet. 63701 ** MODIF TAM (27.08.2021) * Defecto 9 * Inicio */
	
	var zonaSel = $("#zonaSelAlta").val();
	seleccionaZonasAlta();
}   

function validarConsulta() {
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != ''){
		var entidadOk = false;
		try {
			var auxentidad =  parseFloat($('#entidad').val());
			if(!isNaN(auxentidad)){
				$('#entidad').val(auxentidad);
				entidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion').html("Valor para la entidad no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'codoficina' si esta informado		 	
 	if ($('#oficina').val() != ''){ 
	 	var oficinaOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#oficina').val());
	 		if(!isNaN(valor)){
				$('#oficina').val(valor);
				oficinaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!oficinaOk) {
			$('#panelAlertasValidacion').html("Valor para el campo oficina no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}

/**** Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */
function asignaZonasSeleccionadas(){
	var select= document.getElementById('listaZonas2');
	
	var zonas= zonasSeleccionadas(select);

	if(zonas!=''){
		$("#zonaSel").val(zonas);		
	}else{
		$("#zonaSel").val(null);		
	}
}

/**** Pet. 63701 ** MODIF TAM (24.06.2021) ** Inicio */
function asignaZonasSeleccionadasModifAlta(accion){
	var select= document.getElementById('listaZonas2');
	
	var zonas= zonasSeleccionadas(select);

	if(zonas!=''){
		if (accion == "Modif"){
			$("#zonaSelModif").val(zonas);
		}else{
			$("#zonaSelAlta").val(zonas);
		}
		
		$("#zonaSel").val(null);
	}else{
		$("#zonaSel").val(null);		
	}
}

function obtenerZonasEntidad(codEntidad, codZona){
	var origen ="N";
	obtenerlistaZonas(codEntidad, origen, codZona);
}

function obtenerEntidadZona(codZona){
	var codEntidad = codZona.substring(0, codZona.indexOf('-'));
	$('#entidad').val(codEntidad);
	obtenerZonasEntidad(codEntidad, codZona);
}

function validaZonas(){
	//Validamos que haya algun elemento seleccionado
	var select= document.getElementById('listaZonas2');
	
	var zonas= zonasSeleccionadas(select);
	
	return (zonas!='');
}

function zonasSeleccionadas(select){
	var valores='';
	for ( var i = 0, l = select.options.length, o; i < l; i++ ){
		o = select.options[i];
		if(o.selected){
			valores=valores + o.value + ',';
		}
	}
	return valores;
}

function seleccionaZonas(zonas){
	if (zonas != ""){
		if(zonas!=null && zonas!=""){	
			var zonasArray=zonas.split(";");
		}

		for ( var i = 0, l = zonasArray.length, o; i < l; i++ ){
			var option= document.getElementById("esZona_"+ zonasArray[i]);
			
			if(option!=null){
				option.selected="selected";
			}
		}
	}
	
}

function seleccionaZonasAlta(){
	
	var zonas = $("#zonaSelAlta").val();

	if (zonas != ""){
		if(zonas!=null && zonas!=""){	
			var zonasArray=zonas.split(",");
		}

		for ( var i = 0, l = zonasArray.length, o; i < l; i++ ){
			var option= document.getElementById("esZona_"+ zonasArray[i]);
			
			if(option!=null){
				option.selected="selected";
			}
		}
	}
	
}
/**** Pet. 63701 ** MODIF TAM (24.06.2021) ** Fin */


function showOrHideExportIcon() {
	
	 var table = document.getElementById("consultaOficinasPagoManual");
	    var matchingRow = table.querySelector("tr[id^='consultaOficinasPagoManual']");

	    if (matchingRow) {
	        document.getElementById("divImprimir").style.display = "block";
	    } else {
	        document.getElementById("divImprimir").style.display = "none";
	    }
}

function exportToExcel(size) {
    var frm = document.getElementById('exportToExcel');
    frm.target = "_blank";
    frm.submit();
}


