$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand($("#main3").attr("action"));
	$("#main3").attr("action", URL);
	seleccionaEstadosPago();
	seleccionaEstadosCarga();
	
	$('#main3').validate({					
			
			onfocusout: function(element) {
				if ( ($('#operacion').val() == "actualizar") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
					this.element(element);
				}
			},
			errorLabelContainer: "#panelAlertasValidacion",
			wrapper: "li",
			highlight: function(element, errorClass) {
				 	$("#campoObligatorio_"+element.id).show();
				 },
				 unhighlight: function(element, errorClass) {
				 	$("#campoObligatorio_"+element.id).hide();
				 },			 
			rules: {
				"numDiasDesdePagoRenov":{required: true},
				"numDiasHastaPagoRenov": {required: true},
				"numDiasFechaVigor": {required: true},
				"estadosRenovacionPagos":{validaEstadosPagoJq:true},
				"estadosRenovacionPolizas":{validaEstadosPolizasJq:true},
				"pctRetencion":{required: true, number: true, range: [0, 100], valorEntero3Decimal2: true}
			},
			messages: {
				"numDiasDesdePagoRenov":{required: "El campo Desde para el env\u00EDo de p\u00F3lizas de pago es obligatorio"},
				"numDiasHastaPagoRenov":{required: "El campo Hasta para el env\u00EDo de p\u00F3lizas de pago es obligatorio"},			
			 	"numDiasFechaVigor": {required: "El campo D\u00EDas para la asignaci\u00F3n de la fecha de vencimiento es obligatorio"},
			 	"estadosRenovacionPagos":{validaEstadosPagoJq: "El campo Estados para env\u00EDo de P\u00F3lizas Renovables a Pagos es obligatorio"},
			 	"estadosRenovacionPolizas":{validaEstadosPolizasJq: "El campo Estados para carga de Renovables como P\u00F3lizas es obligatorio"},
			 	"pctRetencion":{required: "El campo % Retenci\u00F3n es obligatorio", range: 'El campo % Retenci\u00F3n solo admite valores entro 0 y 100', number: 'El campo % Retenci\u00F3n no contiene un n\u00FAmero v\u00E1lido', valorEntero3Decimal2: 'El campo % Retención debe de ser un valor num\u00E9rico de hasta 3 d\u00EDgitos en su parte entera y dos decimales'}
		 	}
	});
	
	
	jQuery.validator.addMethod("validaEstadosPagoJq", function(value, element, params) {		
		return validaEstadosPago();
	});	
	
	jQuery.validator.addMethod("validaEstadosPolizasJq", function(value, element, params) {		
		return validaEstadosPolizas();
	});	
	
	jQuery.validator.addMethod("valorEntero3Decimal2", function(value, element, params) {		
		return /^\d{0,3}([.]\d{1,2})?$/.test(value);
	});	
	
});

function enviarForm(){
	$("#panelAlertasValidacion").hide();
	$("#panelInformacion").hide();
	var frm = document.getElementById('main3');	
	if($("#main3").valid()){
		asignaEstadosPago();
		asignaEstadosPolizas();
		frm.submit();
	}		
}

function asignaEstadosPago(){
	var select= document.getElementById('estadosRenovacionPagos');
	var estados= estadosSeleccionados(select);
	if(estados!=''){
		$("#estadoPlzRenovPago").val(estados);		
	}else{
		$("#estadoPlzRenovPago").val(null);		
	}
}

function asignaEstadosPolizas(){
	var select= document.getElementById('estadosRenovacionPolizas');
	var estados= estadosSeleccionados(select);
	if(estados!=''){
		$("#estadoPlzRenovCarga").val(estados);
	}else{
		$("#estadoPlzRenovCarga").val(null);
	}
}

function validaEstadosPago(){
	//Validamos que haya algun elemento seleccionado
	var select= document.getElementById('estadosRenovacionPagos');
	var estados= estadosSeleccionados(select);
	return (estados!='');
}

function validaEstadosPolizas(){
	//Validamos que haya algun elemento seleccionado
	var select= document.getElementById('estadosRenovacionPolizas');
	var estados= estadosSeleccionados(select);
	return (estados!='');
}

function estadosSeleccionados(select){
	var valores='';
	for ( var i = 0, l = select.options.length, o; i < l; i++ ){
		o = select.options[i];
		if(o.selected){
			valores=valores + o.value + ',';
		}
	}
	return valores;
}

function seleccionaEstadosPago(){
		var estados =$("#estadoPlzRenovPago").val();
		var estadosArray = [];
		if(estados!=null && estados!=""){	
			estadosArray=estados.split(",");
		}
	
		for ( var i = 0, l = estadosArray.length, o; i < l; i++ ){
			var option= document.getElementById("esPa_"+ estadosArray[i]);
			if(option!=null) option.selected="selected";
		}
	
}

function seleccionaEstadosCarga(){
	var estados =$("#estadoPlzRenovCarga").val();
	var estadosArray = [];
	if(estados!=null && estados!=""){	
		estadosArray=estados.split(",");
	}

	for ( var i = 0, l = estadosArray.length, o; i < l; i++ ){
		var option= document.getElementById("esCa_"+ estadosArray[i]);
		if(option!=null) option.selected="selected";
	}
}

function loadAgpValor() {
	var agpNemo = $("#configAgpNemo").val();
	if (agpNemo == '') {
		$("#configAgpValor").val('');
	} else {
		$.ajax({
			type : 'POST',
			url : 'parametrizacion.html',
			data : {
					'operacion' : 'cargaAgpNemo',
					'agpNemo'   : agpNemo
				   },
			async : true,
			dataType : 'json',
			success : function(datos) {
				if (datos.errorMsgs.length > 0) {
					var errorMsg = '';
					for (i = 0; i < datos.errorMsgs.length; i++) {
						errorMsg += datos.errorMsgs[i] + '<br/>';
					}
					$('#panelAlertasValidacion').html(errorMsg);
					$('#panelAlertasValidacion').show();
				} else {
					$("#configAgpValor").val(datos.agpValor);
				}				
			},
			beforeSend : function() {
				$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			},
			complete : function() {
				$.unblockUI();
			},
			error : function(jqXHR, exception) {
				if (jqXHR.status === 0) {
		            msg = 'Verifique la conexi\u00F3n.';
		        } else if (jqXHR.status == 404) {
		            msg = 'P\u00E1gina no encontrada [404].';
		        } else if (jqXHR.status == 500) {
		            msg = 'Error interno del servidor [500].';
		        } else if (exception === 'parsererror') {
		            msg = 'Fallo en el tratamiento del JSON esperado.';
		        } else if (exception === 'timeout') {
		            msg = 'Tiempo de espera agotado.';
		        } else if (exception === 'abort') {
		            msg = 'Petici\u00F3n Ajax cancelada.';
		        } else {
		            msg = 'Error no esperado: ' + jqXHR.responseText;
		        }
				$('#panelAlertasValidacion').html(msg);
				$('#panelAlertasValidacion').show();
			}
		});
	}
}

function updateAgpValor() {
	var agpNemo = $("#configAgpNemo").val();
	var agpValor = $("#configAgpValor").val();
	if (agpNemo == '') {
		$("#configAgpValor").val('');
	} else {
		jConfirm('ATENCI\u00D3N: Esta modificaci\u00F3n puede provocar un funcionamiento incorrecto de la aplicaci\u00F3n, \u00BFdesea continuar?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
			$.ajax({
				type : 'POST',
				url : 'parametrizacion.html',
				data : {
						'operacion' : 'updateAgpValor',
						'agpNemo'   : agpNemo,
						'agpValor'  : agpValor
					   },
				async : true,
				dataType : 'json',
				success : function(datos) {
					if (datos.errorMsgs.length > 0) {
						var errorMsg = '';
						for (i = 0; i < datos.errorMsgs.length; i++) {
							errorMsg += datos.errorMsgs[i] + '<br/>';
						}
						$('#panelAlertasValidacion').html(errorMsg);
						$('#panelAlertasValidacion').show();
					} else {
						$('#panelMensajeValidacion').html('Par\u00E1metro de configuraci\u00F3n actualizado correctamente.');
						$('#panelMensajeValidacion').show();					
					}				
				},
				beforeSend : function() {
					$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				},
				complete : function() {
					$.unblockUI();
				},
				error : function(jqXHR, exception) {
					if (jqXHR.status === 0) {
			            msg = 'Verifique la conexi\u00F3n.';
			        } else if (jqXHR.status == 404) {
			            msg = 'P\u00E1gina no encontrada [404].';
			        } else if (jqXHR.status == 500) {
			            msg = 'Error interno del servidor [500].';
			        } else if (exception === 'parsererror') {
			            msg = 'Fallo en el tratamiento del JSON esperado.';
			        } else if (exception === 'timeout') {
			            msg = 'Tiempo de espera agotado.';
			        } else if (exception === 'abort') {
			            msg = 'Petici\u00F3n Ajax cancelada.';
			        } else {
			            msg = 'Error no esperado: ' + jqXHR.responseText;
			        }
					$('#panelAlertasValidacion').html(msg);
					$('#panelAlertasValidacion').show();
				}
			});
		});
	}
}






function numberFrom0To100(event){

	  
	var numero=String.fromCharCode(event.keyCode);

  
  
	if (!/^([0-9.])*$/.test(numero)){
		return event.preventDefault();
		
	}
}


