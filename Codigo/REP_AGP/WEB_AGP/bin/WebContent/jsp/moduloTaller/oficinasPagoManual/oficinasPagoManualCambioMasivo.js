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

function comprobarChecks() {

	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena = [];
	cadena = listaIdsMarcados.split(",");

	if (listaIdsMarcados.length > 0) {
		$("input[type=checkbox]").each(function() {
			if ($("#marcaTodos").val() == "true") {
				if ($(this).attr('id') != "checkTodos") {
					$(this).attr('checked', true);
				}
			} else {
				for ( var i = 0; i < cadena.length - 1; i++) {
					var idcheck = "check_" + cadena[i];
					if ($(this).attr('id') == idcheck) {
						$(this).attr('checked', true);
					}
				}
			}
		});
	}
	if ($('#marcaTodos').val() == "true") {
		$('#checkTodos').attr('checked', true);
	} else {
		$('#checkTodos').attr('checked', false);
	}
}

function marcarTodos() {
	if ($('#checkTodos').attr('checked') == true) {
		var listaIdsTodos = $("#listaIdsTodos").val();
		$('#listaIdsMarcados').val(listaIdsTodos);
		$('#marcaTodos').val('true');
		comprobarChecks();
	} else {
		$('#listaIdsMarcados').val('');
		$('#marcaTodos').val('false');
		$("input[type=checkbox]").each(function() {
			$(this).attr('checked', false);
		});
	}
}

function cambioMasivo(){
	
	/* En el cambio Masivo, no se debe mostrar la opcion de PagoManual */
	document.getElementById("txtpagoManual").style.display = '';
	$("#txtpagoManual").css("display",'');
	$('#txtpagoManual').show();
	
	document.getElementById("valPagoManual").style.display = '';
	$("#valPagoManual").css("display",'');
	$('#valPagoManual').show();
	
	/* P0063701 ** MODIF TAM(26.08.2021) * Defecto 11 ** Inicio */
	var listaIdsMarcados = "";
	if($('#checkTodos').attr('checked')==true){
		listaIdsMarcados = $("#listaIdsTodos").val();
	}else{
		listaIdsMarcados = $("#listaIdsMarcados").val();
	}
	
	
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#adiccionMasiva').val('N');
		
		/* Pet. 63701 ** MODIF TAM (30.06.2021) ** Inicio */
		/* Comprobamos que la Entidad de todos los checks marcados es la misma, sino no mostramos la ventana */
		if (comprobarEntMarcadas(listaIdsMarcados)){
			var entidad = ObtenerEntidad(listaIdsMarcados);
			var origen = "S";
			obtenerlistaZonas(entidad, origen);
		}else{
			showPopUpAviso("Debe seleccionar Oficinas de la misma Entidad.");
		}
			
	}else{
		 showPopUpAviso("Debe seleccionar como m\u00EDnimo una oficina.");
	}	
}

function adiccionMasiva(){
	
	/* En la Adicion Masiva, no se debe mostrar la opcion de PagoManual */
	document.getElementById("txtpagoManual").style.display = 'none';
	$("#txtpagoManual").css("display",'none');
	$('#txtpagoManual').hide();
	
	document.getElementById("valPagoManual").style.display = 'none';
	$("#valPagoManual").css("display",'none');
	$('#valPagoManual').hide();
	
	
	
	$('#adiccionMasiva').val('S');
	
	/* P0063701 ** MODIF TAM(26.08.2021) * Defecto 11 ** Inicio */
	var listaIdsMarcados = "";
	if($('#checkTodos').attr('checked')==true){
		listaIdsMarcados = $("#listaIdsTodos").val();
	}else{
		listaIdsMarcados = $("#listaIdsMarcados").val();
	}
	
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#adiccionMasiva').val('S');
		
		/* Pet. 63701 ** MODIF TAM (30.06.2021) ** Inicio */
		/* Comprobamos que la Entidad de todos los checks marcados es la misma, sino no mostramos la ventana */
		if (comprobarEntMarcadas(listaIdsMarcados)){
			var entidad = ObtenerEntidad(listaIdsMarcados);
			var origen = 'S';
			obtenerlistaZonas(entidad, origen);
		}else{
			showPopUpAviso("Debe seleccionar Oficinas de la misma Entidad.");
		}
			
	}else{
		 showPopUpAviso("Debe seleccionar como m\u00EDnimo una oficina.");
	}	
}


function comprobarChecksMarcados(){
	
	var checksMarcados="";
	
	var checks = document.getElementsByTagName('input');
	for (var i = 0; i < checks.length; i++)	{
 		var node = checks[i];
 		if (node.getAttribute('type') == 'checkbox'){
 			if (node.checked){
 				var valor = node.name;
 				var valCheck = valor.substr(6, valor.length-1);
 				checksMarcados+= valCheck+",";
 			}
 		}
	}
	return checksMarcados;
	
}

//popupCambioMasivo
function cerrarCambioMasivoOficinas(){
	
	$('#panelCambioMasivoOficinas').hide();
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

function aplicarCambioMasivoOficinas(){
	
	$.blockUI.defaults.message = '<h4> <BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	asignaZonasSeleccionadas_cm();
	$('#main').submit();
	cerrarCambioMasivoOficinas();
}

/*  Pet. 63701 ** MODIF TAM (30.06.2021) ** Inicio */
function comprobarEntMarcadas(listaIdsMarcados){
	
	var retorno = true;
	var arEntidades = [];
	
	if (listaIdsMarcados != ""){
		var EntidadesSelArray=listaIdsMarcados.split(",");
		for ( var i = 0, l = EntidadesSelArray.length, o; i < l; i++ ){
			var EntArray = EntidadesSelArray[i].split("_");
			var Entidad = EntArray[0];
			if (Entidad != ""){
				arEntidades[i] = Entidad;	
			}
		}
		
		EntidadAnt = arEntidades[0];
		var tamArray = arEntidades.length;
		
		for(var j=1; j<arEntidades.length; j++){
			if (EntidadAnt != arEntidades[j]){
				return false;
			}
		}
	}
	return retorno;
}

function obtenerlistaZonas(codent, cambioMasivo, codZona){
	
	$('#listaIdsMarcados').val("");
	
	$.ajax({
        url: "pagoManual.run",
        data: "method=doObtenerListaZonasEntidad&esCambioMasivo="+cambioMasivo+"&codent="+codent,            
        async:true,
        beforeSend: function(objeto){
        },
        complete: function(objeto, exito){
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        error: function(objeto, quepaso, otroobj){
            alert("Error al comprobar si las p\u00F3lizas seleccionadas estan en estado Definitiva: " + quepaso);
        },
        global: true,
        ifModified: false,
        processData:true,
        success: function(datos){
        	if (cambioMasivo == "S"){
            	rellenarSelectZonasNew(datos.listaZonasCM);

            	$('#overlayCambioMasivo').show();
    			$('#panelCambioMasivoOficinas').show();
        	}else{
        		rellenarSelectZonasPpal(datos.listaZonasCM, codZona);
        	}
        	
        },
        type: "POST"
    });
	
}

function rellenarSelectZonasNew(listaZonascm){
	//Primero lo vacio
	$('#listaZonascm').find('option').remove();
	
	if(listaZonascm!=null){
		for(var i in listaZonascm){
			
			var zona = listaZonascm[i];
			var nombre = zona.nomzona;
			var value = zona.id.codentidad +"-" + zona.id.codzona;

			var options = $('#listaZonascm').attr('options'); 
			options[options.length] = new Option(nombre, value);//, true, true);
			
			
		}
	}
}

function rellenarSelectZonasPpal(listaZonasEntidad, codZona){
	
	//Primero lo vacio
	$('#listaZonas2').find('option').remove();
	
	if(listaZonasEntidad!=null){
		for(var i in listaZonasEntidad){
			
			var zona = listaZonasEntidad[i];
			var nombre = zona.nomzona;
			var value = zona.id.codentidad +"-" + zona.id.codzona;
			var id = "esZona_"+value
			
			var options = $('#listaZonas2').attr('options'); 
			options[options.length] = new Option(nombre, value);//, true, true);
			options[id] = id;			
		}
		$('#listaZonas2').find('option').each(function() {
			if ($(this).val() == codZona) {
				$(this).attr('selected', true);
			}
		});
	}
}

function ObtenerEntidad(listaIdsMarcados){
	var retorno = null;
	var arEntidades = [];
	
	
	if (listaIdsMarcados != ""){
		var EntidadesSelArray=listaIdsMarcados.split(",");
		for ( var i = 0, l = EntidadesSelArray.length, o; i < l; i++ ){
			var EntArray = EntidadesSelArray[i].split("_");
			var Entidad = EntArray[0];
			if (Entidad != ""){
				arEntidades[i] = Entidad;	
			}
		}
		
		Entidad = arEntidades[0];
		return Entidad;
	}
	
	return retorno;
}


function asignaZonasSeleccionadas_cm(){
	var select= document.getElementById('listaZonascm');
	
	var zonas= zonasSeleccionadas(select);
	if(zonas!=''){
		$("#zonaSelcm").val(zonas);		
	}else{
		$("#zonaSelcm").val(null);		
	}
}

/*  Pet. 63701 ** MODIF TAM (30.06.2021) ** Fin */		
