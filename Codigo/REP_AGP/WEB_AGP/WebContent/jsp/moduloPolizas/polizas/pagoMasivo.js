//DAA 28/05/2013 js para operaciones del pagoMasivo

$(document).ready(function(){
	
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
        inputField        : "fechapago",
        button            : "btn_fechapago",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
	
	//Validaciones del formulario
	$('#frmPagoMasivo').validate({
		errorLabelContainer: "#txt_val_fecha",
		wrapper: "li",		  				  				 
		rules: {
			"fechapago":{required: true, comprobarFechaPago: true }	  	
		},
		messages: {
			"fechapago":{required: "El campo fecha de pago es obligatorio", comprobarFechaPago: "La fecha de pago no debe ser mayor a la actual"}
		}
	});
	
	
});

jQuery.validator.addMethod("comprobarFechaPago", function(value, element) {
	return (UTIL.fechaMenorOIgualQueFechaActual(element.value));
});

function compruebaPolizasDefinitivasPagoMasivo_ajax(){

	$.ajax({
            url: "cambioMasivoPolizas.html",
            data: "method=compruebaPolizasDefinitivasPagoMasivo&idsRowsChecked="+$("#idsRowsChecked").val(),
            async:true,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al comprobar si las p�lizas seleccionadas estan en estado Definitiva: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	if(datos.pagoMasivoValido.valueOf() == "true"){
            		$("#listaIds").val(datos.listaIds.valueOf());
            		showPopUpPagoMasivo();
            	}else{
            		$('#divAviso').show();
					$('#txt_info_gf').show();
					$('#overlay').show();
            	}
            	
            },
            type: "POST"
        });
}

function showPopUpPagoMasivo(){
 	$('#divPagoMasivo').fadeIn('normal');
	$('#overlay').show();
	CargarFechaActual(true,document.getElementById("fechapago"));
}

function cerrarPopUpPagoMasivo(){
	$('#divPagoMasivo').fadeOut('normal');
	$('#overlay').hide();
	$('#fechapago').val('');
	$("#txt_val_fecha").hide();
}

function doPagoMasivo(){
	
	if ($('#pagoMasivoD').is(":checked")){
		$('#frmPagoMasivo').validate().cancelSubmit = true;
	}
	$('#frmPagoMasivo').submit();
}		 	  	