$(document).ready(function(){
			
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    	
    /*var frm = document.getElementById('main3');
    alert(frm.id.value);	
    if(frm.id.value != "" && frm.id.value != undefined){ 
		$("#btnModificar").show();
    }*/
    Zapatec.Calendar.setup({
        firstDay          : 1,
        weekNumbers       : false,
        showOthers        : true,
        showsTime         : false,
        timeFormat        : "24",
        step              : 2,
        range             : [1900.01, 2999.12],
        electric          : false,
        singleClick       : true,
        inputField        : "fechainicio",
        button            : "btn_fechaInicio",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
    
  	Zapatec.Calendar.setup({
        firstDay          : 1,
        weekNumbers       : false,
        showOthers        : true,
        showsTime         : false,
        timeFormat        : "24",
        step              : 2,
        range             : [1900.01, 2999.12],
        electric          : false,
        singleClick       : true,
        inputField        : "fechafin",
        button            : "btn_fechaFin",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
  	
  	Zapatec.Calendar.setup({
        firstDay          : 1,
        weekNumbers       : false,
        showOthers        : true,
        showsTime         : false,
        timeFormat        : "24",
        step              : 2,
        range             : [1900.01, 2999.12],
        electric          : false,
        singleClick       : true,
        inputField        : "fechaFinGarantia",
        button            : "btn_fechaFinGarantia",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
  	
  	Zapatec.Calendar.setup({
        firstDay          : 1,
        weekNumbers       : false,
        showOthers        : true,
        showsTime         : false,
        timeFormat        : "24",
        step              : 2,
        range             : [1900.01, 2999.12],
        electric          : false,
        singleClick       : true,
        inputField        : "fechaFinSuplementos",
        button            : "btn_fechaFinSuplementos",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
  	
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
			"linea.codplan":{required: true, digits: true, minlength: 4},
	 		"linea.codlinea":{required: true, digits: true},
	 		"cultivo.id.codcultivo":{required:true,digits: true},
	 		"fechainicio":{required:true, dateITA: true},
	 		"fechafin":{required: true, dateITA: true, comprobarFechaFin: ['fechainicio', 'fechafin']},
	 		"fechaFinGarantia":{required: true, dateITA: true, comprobarFechaFin: ['fechafin', 'fechaFinGarantia']},
	 		"fechaFinSuplementos":{required: true, dateITA: true, comprobarFechaFin: ['fechainicio', 'fechaFinSuplementos']}
		},
		messages: {
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan s�lo puede contener d�gitos", minlength: "El campo Plan debe contener 4 digitos"},
	 		"linea.codlinea":{required: "El campo Linea es obligatorio", digits: "El campo Linea debe contener digitos"},
	 		"cultivo.id.codcultivo":{required: "El campo Cultivo es obligatorio",digits: "El campo Cultivo debe contener digitos"},
	 		"fechainicio":{required: "El campo Fecha Inicio de Contratacion es obligatorio", dateITA: "El formato del campo Fecha inicio es dd/mm/YYYY"},
	 		"fechafin":{required: "El campo Fecha Fin de Contratacion es obligatorio", dateITA: "El formato del campo Fecha fin es dd/mm/YYYY", comprobarFechaFin: "La Fecha Fin de Contratacion debe ser superior a la Fecha de Inicio"},
	 		"fechaFinGarantia":{required: "El campo Fecha Fin de Garantia es obligatorio", dateITA: "El formato del campo Fecha Fin de Garantia es dd/mm/YYYY", comprobarFechaFin: "La Fecha Fin de Garant&iacute;a debe ser superior a la Fecha Fin de Contratacion"},
	 		"fechaFinSuplementos":{required: "El campo Fecha Fin de env&iacute;o de Suplementos es obligatorio", dateITA: "El formato del campo Fecha Fin env&iacute;o de Suplementos es dd/mm/YYYY", comprobarFechaFin: "La Fecha Fin de env&iacute;o de Suplementos debe ser superior a la Fecha Fin de Inicio"}
		}
	});
	
			//comprueba la fecha inicial y la final entre ellas		
			//params--> array que contiene las fechas
			jQuery.validator.addMethod("comprobarFechaFin", function(value, element, params) {

				return (this.optional(element) || !UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));	
	});
	
});


function consultarInicial () {
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	if (validarConsulta()){
		if (comprobarCampos(false)){
		
			frm.origenLlamada.value= 'primeraBusqueda';
			$("#main3").validate().cancelSubmit = true;
			$('#main3').submit();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}
function consultar(){
	limpiaAlertas(); 
	if (validarConsulta()){
		if (comprobarCampos(true)){
			$("#btnModificar").hide();
			lanzarConsulta ();
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

//Lanza la consulta de fechas de contratacion
function lanzarConsulta () {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaFechasContratacionSbp','filter');
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
		jQuery.jmesa.removeAllFiltersFromLimit('consultaFechasContratacionSbp');
	}
	var resultado = false;
	
	if ($('#plan').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','linea.codplan', $('#plan').val());
		}
		resultado = true;
	}
	if ($('#linea').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','linea.codlinea', $('#linea').val());
		}
		resultado = true;
	}
	if ($('#fechainicio').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','fechainicio', $('#fechainicio').val());
		}
		resultado = true;
	}
	if ($('#fechafin').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','fechafin', $('#fechafin').val());
		}
		resultado = true;
	}
	if ($('#fechaFinGarantia').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','fechaFinGarantia', $('#fechaFinGarantia').val());
		}
		resultado = true;
	}
	if ($('#cultivo').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','cultivo.id.codcultivo', $('#cultivo').val());
		}
		resultado = true;
	}
	if ($('#fechaFinSuplementos').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaFechasContratacionSbp','fechaFinSuplementos', $('#fechaFinSuplementos').val());
		}
		resultado = true;
	}
	return resultado;
}


//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	$('#limpiar').submit();
}
			
function editar(id,linea,plan,codcultivo,nombreCultivo,nomLinea,fechainicio,fechafin,fechaFinGarantia,fechaFinSuplementos){
	
	var frm = document.getElementById('main3');
	frm.target="";
	frm.id.value=id;
	frm.plan.value = plan;
	frm.linea.value = linea;
	frm.cultivo.value = codcultivo;
	frm.desc_cultivo.value = nombreCultivo;
	frm.desc_linea.value =nomLinea;
	frm.fechainicio.value = fechainicio;
	frm.fechafin.value = fechafin;
	frm.fechaFinGarantia.value = fechaFinGarantia;
	frm.fechaFinSuplementos.value = fechaFinSuplementos;
	$("#btnModificar").show();
}

function modificar(){
	var frm = document.getElementById('main3');
	frm.target="";
	$("#panelInformacion").hide();
	frm.method.value= "doEditar";
	$('#main3').submit();
}

function borrar(id,codcultivo) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('�Est� seguro de que desea eliminar la linea de Sobreprecio seleccionada?')){
		var frm = document.getElementById('main3');
		frm.method.value = 'doBorrar';
		frm.id.value = id;
		//frm.cultivo.value = codcultivo;
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main3").submit();
	}
}

function alta() {
	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	frm.method.value = 'doAlta';
	$("#main3").submit();
}

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('periodoContSbp.run?ajax=true&' + parameterString, function(data) {
		$("#grid").html(data)
	});
}
//Metodo que realiza la llamada al servidor para generar el informe de anexos
/*function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'periodoContSbp.run?ajax=false&' + parameterString;
}*/

// Comprueba que los valores del formulario son correctos antes de consultar
function validarConsulta() {
	// Valida el campo 'Plan' si esta informado
	if ($('#plan').val() != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#plan').val());
			if(!isNaN(auxPlan) && $('#plan').val().length == 4){
				$('#plan').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'Linea' si esta informado
	if ($('#linea').val() != ''){
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat($('#linea').val());
			if(!isNaN(auxLinea)){
				$('#linea').val(auxLinea);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la l�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}
			