	function onLoad(){ 
	
	var strValue = $('#permitidocalculado').val() +'-'+$('#idcampo').val()+'-formato:'+ $('#inputFormat').val();
	
	$("#listaCampos").val(strValue);
	$("#listaCampos").change();
	
	if($("#id").val() != ""){
		$("#btnModificar").show();
		
	}else{
		$("#btnModificar").hide();
	}
}

	$(document).ready(function(){
			
			    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
		        document.getElementById("main3").action = URL;
		        
 				
 
 		$('#main3').validate({					
				
					onfocusout: function(element) {
						
						if ( ($('#method').val() == "modificar") || ($('#method').val() == "doAlta") ) {
							
							this.element(element);
						}
					},
					errorLabelContainer: "#panelAlertasValidacion",
   					wrapper: "li",
					
					rules: {
						listaCampos:{required: true},
						// Formato - Se valida si el campo es numérico o fecha
						"formato":{required: function(element){if ($("#tipoSeleccionado").val() == $("#tipoNumerico").val() || $("#tipoSeleccionado").val() == $("#tipoFecha").val()) return true; else return false;}},
						// Decimales - Se valida si el campo es numérico
						"decimales":{required: function(element){if ($("#tipoSeleccionado").val() == $("#tipoNumerico").val()) return true; else return false;}
									, digits: function(element){if ($("#tipoSeleccionado").val() == $("#tipoNumerico").val()) return true; else return false;}},
						// Totaliza - Se valida si el campo es numérico
						"totaliza":{required: function(element){if ($("#tipoSeleccionado").val() == $("#tipoNumerico").val()) return true; else return false;}},
						// Total por grupo - Se valida si se ha seleccionado 'Suma' en el campo 'Totaliza'
						"total_por_grupo":{required: function(element){if ($("#totaliza").val() == 1) return true; else return false;}}
						
					},
					messages: {
						listaCampos:{required: "El campo Columna es obligatorio"},
						"totaliza":{required: "El campo Totaliza es obligatorio"},
						"total_por_grupo":{required: "El campo Total por grupo es obligatorio"},
						"formato":{required: "El campo Formato es obligatorio"},
						"decimales":{required: "El campo Decimales es obligatorio", digits: "El campo Decimales sólo puede contener números"}
				 	}
				});
	
	/**
	 * Cuando el campo 'Decimales' pierda el foco se comprobará que tiene un valor numérico, si no es así se borra
	 */
	$("#decimales").focusout(function() {
		if (isNaN ($("#decimales").val())) {
			$("#decimales").val('');
		} 
	})
	
	// Carga todos los posibles formatos en el combo
	cargarTodosFormatos (document.getElementById('formato'));
					
	});
		
			
	function condicionInformes(){
	
		$("#redireccion").val('condiciones');
		$("#origenLlamada").val('datoInformes');
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
		
	}
	
	function clasificacionRuptura(){ 
	
		$("#redireccion").val('clasificacionYRuptura');
		$("#origenLlamada").val('datoInformes');
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
		
		
	}


	function volver(){
		limpiar();
		var frm = document.getElementById('main3');
	    frm.method.value = 'doConsulta';	
		$("#redireccion").val('informes');
		$("#origenLlamada").val('');
		$('#recogerInformeSesion').val("true");
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit(); 
		
		
	}
	
	function onInvokeAction(id) {
		
		var to=document.getElementById("adviceFilter");
		to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
		$.jmesa.setExportToLimit(id, '');
		var parameterString = $.jmesa.createParameterStringForLimit(id);
		var frm = document.getElementById('main3');
		$.get('mtoDatosInforme.run?ajax=true&idInforme='+$("#informeid").val() +'&'+ parameterString, function(data) {
		$("#grid").html(data)
		  });
		}
		
	/**
	 * Comprueba los campos que se han informado en el formulario y los añade al filtro
	 */
	function comprobarCampos(){
		 	
		 	jQuery.jmesa.removeAllFiltersFromLimit('consultaDatosInforme');
		 	
		 	// Columna
	      	if ($('#idcampo').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','idcampo', $('#idcampo').val());
			// Abreviado
			if ($('#abreviado').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','abreviado', $('#abreviado').val());	    
			// Formato
			if ($('#formato').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','formato', $('#formato').val());
			// Decimales
			if ($('#decimales').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','decimales', $('#decimales').val());
			// Totaliza
			if ($('#totaliza').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','totaliza', $('#totaliza').val());
			// Total por grupo
			if ($('#total_por_grupo').val() != '') jQuery.jmesa.addFilterToLimit('consultaDatosInforme','total_por_grupo', $('#total_por_grupo').val());
	}
	
	
	/**
	 * Trata el valor del campo 'Columna' seleccionado para obtener los valores correspondientes
	 */
	function partirCampo (campo) {
		
		// Indicador de campo permitido o calculado 
		var aux = campo.split ("-id:");
		$("#permitidocalculado").val(aux[0]);
		aux = aux[1];
		
		// Id del campo
		aux = aux.split ("-abv:"); 
		$("#idcampo").val(aux[0]);
		aux = aux[1];
		
		// Abreviado por defecto - Se utiliza en el alta si no se indica abreviado
		aux = aux.split ("-tipo:");
		$("#abreviadoSeleccionado").val(aux[0]);
		
		// Tipo de dato
		$("#tipoSeleccionado").val(aux[1]);
	}
	
	/**
	 * Borra los datos de los hidden
	 */
	function limpiarHidden () {
		$("#permitidocalculado").val('');
		$("#idcampo").val('');
		$("#abreviadoSeleccionado").val('');
		$("#tipoSeleccionado").val('');
	}
		
		
	/**
	 * Esta función se ejectua al seleccionar un campo en el combo 'Columna'.
	 * Rellena el campo 'Abreviado' con el nombre del campo y configura el combo de 'Formato' dependiendo del tipo de campo seleccionado
	 */
	function seleccionarCampo(value){
		
		// Trata el valor del campo 'Columna' seleccionado para obtener los valores correspondientes
		if (value != "") partirCampo (value); else limpiarHidden();
			
		// Llama a la función que configura el combo de 'Formato' dependiendo del tipo de campo seleccionado
		habilitaFormato();
		
		// Llama a la función que configura el input de 'Decimales' dependiendo del tipo de campo seleccionado
		habilitaDecimales();
		
		// Llama a la función que configura el combo de 'Totaliza' dependiendo del tipo de campo seleccionado
		updateTotaliza();
	}
	
	/**
	 * Configura el combo de 'Formato' dependiendo del tipo de campo pasado como parámetro
	 */
	function habilitaFormato(){
		
	var frm = document.getElementById('main3');
	var combo = frm.formato.options;
	
	var tipo = $("#tipoSeleccionado").val();
	
	// Se rellena el combo si el tipo es numérico o fecha
	if (tipo == '' || tipo == $('#tipoNumerico').val() || tipo == $('#tipoFecha').val()){
		
		combo.length = 0;
		// Campo tipo fecha
		if (tipo == $('#tipoFecha').val()){
			combo[0] = new Option("Todos","");
			cargarFormatosFecha (combo);
			$('#formato').attr('disabled', false);
		}
		// Campo tipo numérico
		else if (tipo == $('#tipoNumerico').val()){
			combo[0] = new Option("Todos","");
			cargarFormatosNumerico (combo);
			$('#formato').attr('disabled', false);
		}
		// Se ha seleccionado el 'Mostrar todos'
		else {
			$('#formato').attr('disabled', false);
			// Carga todos los posibles formatos en el combo
			cargarTodosFormatos (combo);
		}
	}
	// Campo tipo alfanumérico
	else{
		$('#formato').attr('disabled', true);
		$('#formato').val('');
		combo.length = null;
		combo[0] = new Option("Todos","");
	}
}

/**
 * Carga el combo pasado como parámetro todos los formatos de tipo fecha
 */
function cargarFormatosFecha (combo) {
	
	var lstCodFormatosFec = $('#codFormatosFec').val().split('#');
	var lstFormatosFec = $('#formatosFec').val().split('#');
	
	for(var i=1;i<lstCodFormatosFec.length+1;i++){
		combo[combo.length] = new Option(lstFormatosFec[i-1],lstCodFormatosFec[i-1]);
	}
}

/**
 * Carga el combo pasado como parámetro todos los formatos de tipo numérico
 */
function cargarFormatosNumerico (combo) {
	
	var lstCodFormatosNum = $('#codFormatosNum').val().split('#');
	var lstFormatosNum = $('#formatosNum').val().split('#');
	
	for(var i=1;i<lstCodFormatosNum.length+1;i++){
		combo[combo.length] = new Option(lstFormatosNum[i-1],lstCodFormatosNum[i-1]);
	}
}

/**
 * Carga el combo pasado como parámetro todos los formatos posibles
 */
function cargarTodosFormatos (combo) {
	$('#formato').empty();
	combo[0] = new Option("Todos","");
	// Carga los formatos de fecha
	cargarFormatosFecha (combo);
	// Carga los formatos numéricos
	cargarFormatosNumerico (combo);
}
	
	/**
	 * Configura el campo 'Decimales' dependiendo de los datos incluidos en 'Tipo' y 'Formato'
	 */
	function habilitaDecimales(){
		
		if ($('#tipoSeleccionado').val() == ''){
			$('#decimales').attr("disabled", false);
			$('#decimales').val('');
		}
		else if ($('#tipoSeleccionado').val() == $('#tipoNumerico').val()){ // numerico
			if ($('#formato').val() == ''){
				$('#decimales').attr("disabled", false);
				$('#decimales').val('');
			}else if($('#formato').val() == 2 || $('#formato').val() == 3){ //numerico sin decimales
				$('#decimales').attr("disabled", true);
				$('#decimales').val('0');
			}else{ //numerico con decimales
				$('#decimales').attr("disabled", false);
				$('#decimales').val('2');
			}
		}
		else if($('#formato').val() == 0 || $('#formato').val() == 1){  // fechas
				$('#decimales').attr("disabled", true);
				$('#decimales').val('');
		}
		else { // Alfanumérico
			$('#decimales').val('');
			$('#decimales').attr("disabled", false);
		}
	}
	
	/**
	 * Configura el campo 'Totaliza' dependiendo de los datos incluidos en 'Tipo'
	 */
	function updateTotaliza(){
		// Se ha seleccionado un campo alfanumérico o fecha
		if ($('#tipoSeleccionado').val() == $('#tipoTexto').val() || $('#tipoSeleccionado').val() == $('#tipoFecha').val()){
			$('#totaliza').val($('#totalizaNo').val());
			$('#total_por_grupo').val('0');
			$('#totaliza').attr("disabled", true);
			$('#total_por_grupo').attr("disabled", true);
		}
		// Se ha seleccionado un campo numérico o ninguno
		else{
			$('#totaliza').val('');
			$('#total_por_grupo').val('');
			$('#totaliza').attr("disabled", false);
			$('#total_por_grupo').attr("disabled", false);
		}
	}
	
	/**
	 * Configura el campo 'Total por grupo' dependiendo de los datos incluidos en 'Totaliza'
	 */
	function habilitaTotalPorGrupo(){
		if ($('#totaliza').val() == $('#totalizaNo').val()){
			$('#total_por_grupo').attr("disabled", true);
			$('#total_por_grupo').val('0');
		}else if ($('#totaliza').val() != $('#totalizaNo').val()){
			$('#total_por_grupo').attr("disabled", false);
		}else{
			$('#total_por_grupo').attr("disabled", true);
			$('#total_por_grupo').val('');
		}
	}
	
	/**
	 * Muestra los datos del registro seleccionado en el formulario
	 */
	function visualizarDatos (id,informeid,campoid,formato,calculadopermitido,tipo , abreviado, nombre , decimales , totaliza , total_por_grupo){
		// Limpia las alertas previas
		limpiaAlertas();
		
		// Compone la cadena para seleccionar el campo en el combo de 'Columna'
		var campo = calculadopermitido + '-id:' + campoid + '-abv:' + nombre + '-tipo:' + tipo;
		$("#listaCampos").val(campo);
		
		// Ejecuta la función que se lanza cuando se selecciona un campo en el combo de 'Columna'
		seleccionarCampo(campo);
		
		// Muestra en el formulario los datos del registro seleccionado
		// Campo permitido o calculado
		$("#camposcalculadosid").val(campoid);
		if(calculadopermitido == "1") $("#permitidocalculado").val(1);	else $("#permitidocalculado").val(2);
		// Abreviado
		$("#abreviado").val(abreviado);
		$("#abreviadoSeleccionado").val(abreviado);
		// Formato
		if(formato=="null")	$("#formato").val("");	else $("#formato").val(formato);
		// Habilita/Deshabilita el campo decimales dependiendo del formato seleccionado
		habilitaDecimales();
		// Decimales
		if(decimales=="null")	$("#decimales").val("");	else $("#decimales").val(decimales);
		// Totaliza
		$("#totaliza").val(totaliza);
		habilitaTotalPorGrupo();
		// Total por grupo
		$("#total_por_grupo").val(total_por_grupo);
		// Id
		$("#id").val(id);	
	}
		
	
  	function visualizar (id,informeid,campoid,formato,calculadopermitido,tipo , abreviado, nombre , decimales , totaliza , total_por_grupo){
  		// Muestra los datos del registro seleccionado en el formulario
  		visualizarDatos (id,informeid,campoid,formato,calculadopermitido,tipo , abreviado, nombre , decimales , totaliza , total_por_grupo)
  		// Oculta el botón 'Modificar'
		$('#btnModificar').hide();
	}
		
		
	function editar(id,informeid,campoid,formato,calculadopermitido,tipo , abreviado, nombre , decimales , totaliza , total_por_grupo){
		// Muestra los datos del registro seleccionado en el formulario
  		visualizarDatos (id,informeid,campoid,formato,calculadopermitido,tipo , abreviado, nombre , decimales , totaliza , total_por_grupo)
		// Muestra el botón 'Modificar'
		$('#btnModificar').show();
	}
	
	/*
	 * Da de alta el dato del informe con los datos informados en el formulario
	 */
	function alta(){
		
		limpiaAlertas();
	    
	    var frm = document.getElementById('main3');
		    	
		$("#id").val('');
		$("#origenLlamada").val("alta");
		frm.method.value = 'doAlta';
		// Se valida el formulario
		$('#main3').validate();
		// Si el formulario es correcto
		if ($('#main3').valid()) {
			// Si no se ha informado el campo Abreviado, se establece el nombre del campo como tal
			setAbreviado ();
			// Se habilitan los campos que pudieran estar deshabilitados antes del alta
			habilitarCampos ();
			$('#main3').validate().cancelSubmit = true;
			$('#main3').submit();
		}
	}
	
	/**
	 * Si no se ha informado el campo Abreviado, se establece el nombre del campo como tal
	 */
	function setAbreviado () {
		if ($("#abreviado").val() == '') $("#abreviado").val($("#abreviadoSeleccionado").val()); 
	}
	
	/*
	 * Habilita los posibles campos que estén deshabilitados antes de hacer el submit del formulario
	 */
	function habilitarCampos () {
		$("#formato").attr ("disabled", false);
		$("#decimales").attr ("disabled", false);
		$("#totaliza").attr ("disabled", false);
		$("#total_por_grupo").attr ("disabled", false);
	}
		
	function borrar(id,informeId){
	
		limpiaAlertas();
		$('#main3').validate().cancelSubmit = true;
		if(confirm('¿Está seguro de que desea eliminar este Dato de Informe?')){
			var frm = document.getElementById('main3');
			frm.method.value = 'doBaja';			
			$.blockUI.defaults.message = '<h4> Eliminando dato informe seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#campospermitidosid").val("");
			$("#camposcalculadosid").val("");
			$("#decimales").val("");
			$("#informeid").val(informeId);	
			$("#origenLlamada").val("borrar");	
			$("#id").val(id);	
			$("#main3").submit();	
			}	
	}
	
	
	function subir(id,informeId){
			
			var frm = document.getElementById('main3');
			frm.method.value = 'subirNivelDatoInformesyActualizar';
			$('#main3').validate().cancelSubmit = true;
			$("#id").val(id);
			$("#informeid").val(informeId);	
			$("#main3").submit();
					
			}
			
	function bajar(id,informeId){
			
			var frm = document.getElementById('main3');
			frm.method.value = 'bajarNivelDatoInformesyActualizar';
			$('#main3').validate().cancelSubmit = true;
			$("#id").val(id);	
			$("#informeid").val(informeId);	
			$("#main3").submit();
					
			}			
		
	function modificar(){
			limpiaAlertas();
			var frm = document.getElementById('main3');
			frm.method.value = 'modificarCampo';
			$("#origenLlamada").val("modificar");
			// Se habilitan los campos que pudieran estar deshabilitados antes de la modificación
			habilitarCampos ();
			$("#main3").submit();
	}
		
	function consultar(){
			
		limpiaAlertas();
		$("#btnModificar").hide();
		comprobarCampos();
	 	onInvokeAction('consultaDatosInforme','filter');
		    
	}
		
	/**
	 * Habilita y limpia todos los campos del formulario
	 */
	function limpiar(){
		
		  habilitarCampos ();
			
		  $("#listaCampos").val('');
		  $("#abreviado").val('');
		  // Carga todos los posibles formatos en el combo
		  cargarTodosFormatos (document.getElementById('formato'));
		  $("#decimales").val('');
		  $("#totaliza").val('');
		  $("#total_por_grupo").val('');
		  $("#id").val('');
		  $("#idcampo").val('');
		  $("#permitidocalculado").val('');
		  $("#orden").val('');
		  
		  limpiaAlertas();
		  limpiarHidden ();
		  $("#btnModificar").hide();
		  
		  jQuery.jmesa.removeAllFiltersFromLimit('consultaDatosInforme');
		  onInvokeAction('consultaDatosInforme','clear');		
		}
		
		
	function limpiaAlertas(){
			
		 $("#panelInformacion").val('');
		 $("#panelInformacion").hide();
		 $("#panelAlertasValidacion").val('');
		 $("#panelAlertasValidacion").hide();	
		 $("#panelAlertas").val('');
		 $("#panelAlertas").hide();
	 	
	 }
		 
	function generar(){
	
		limpiaAlertas();
		var frm = document.getElementById('main3');
		var frmGen = document.getElementById('generarInformeForm');
		frmGen.idInforme.value = frm.idInforme.value;
		frmGen.method.value = 'doGenerar';
		$('#generarInformeForm').attr('target', '_blank');
		$('#generarInformeForm').submit();
}
		
		