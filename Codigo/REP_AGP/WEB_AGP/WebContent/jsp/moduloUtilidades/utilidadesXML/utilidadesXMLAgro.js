$(document).ready(function() {
	$('#formXML').validate({		
		errorLabelContainer: "#panelAlertasValidacion",		
		onfocusout: function(element) {
			if ($('#formIncidencias:input[name="method"]').val() == "doConsulta") {
				this.element(element);
			}else{
				this.element(element);
			}
		},
		wrapper: "li",
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			'idPoliza': {required: true, digits: true},
			'codServicio': {required: true}
		},
		messages: {
			'idPoliza': {required : 'El campo Solicitud es obligatorio.', 
			   	digits: 'El campo Solicitud s&oacute;lo puede contener d&iacute;gitos.'},	
			'codServicio': {required : 'El campo Servicio es obligatorio.'},	
		}
	});	
});