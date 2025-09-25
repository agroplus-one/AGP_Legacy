//Nota: Debe importarse utilesIBAN.js
function limpiarPaneles(){
	$("#panelInformacion").hide();
	$("#panelInformacion").html('');
}
function showPopUpCambioIBAN(){
	limpiarPaneles();
	var list = "";
	var contador = 0;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function(){
		if($(this).attr('checked')){
			list = list + $(this).val();
			contador++;
		}
	});
	
	if(contador > 0){
		if (contador > 1){// hay más de una póliza marcada	
			$('#divAviso').show();
			$('#txt_info_check_multiple').show();
			$('#overlay').show();
		}else{// solo una póliza marcada
			var arrayValores = list.split("#");
			$('#isganado').val(arrayValores[6]);
			validaPolCorrectaCambioIBAN_ajax();
		}
	}else{// no hay polizas seleccionadas	
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function cambiarIBAN(){
	var ibanCompleto = $('#iban').val() + $('#cuenta1').val()+ $('#cuenta2').val() + $('#cuenta3').val() + $('#cuenta4').val()+ $('#cuenta5').val();
	if(validarCampoIBAN(ibanCompleto)){
		$('#cambioIBANPopUpError').hide();
		if (confirm('¿Seguro que desea cambiar el IBAN de la póliza?')){
			var frm = document.getElementById('main3');
			frm.operacion.value = 'cambioIBAN';
			$('#nuevoIbanCompleto').val(ibanCompleto);
			$('#main3').submit();
		}

	}else{
		$('#cambioIBANPopUpError').show();
		//setTimeout("limpiarErrorIban()", 5000);
	}
}

function cerrarPopUpCambioIBAN(){
	$('#divCambioIBAN').fadeOut('normal');
	$('#overlay').hide();
}

function limpiarErrorIban(){
	$('#cambioIBANPopUpError').hide();
}

function validaPolCorrectaCambioIBAN_ajax(){

	$.ajax({
            url: "validacionesPolizaAjax.html",
            data: "method=validaPolCorrectaCambioIBAN&idsRowsChecked="+$("#idsRowsChecked").val(),
            async:true,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al comprobar si las pólizas seleccionadas estan en estado Enviada Correcta: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	if(datos.cambioIBANValido.valueOf() == "true"){
        			$('#ccc').val('');
        			$('#iban').val('');
        			$('#cuenta1').val('');
        			$('#cuenta2').val('');
        			$('#cuenta3').val('');
        			$('#cuenta4').val('');
        			$('#cuenta5').val('');
        		 	$('#divCambioIBAN').fadeIn('normal');
        		 	$('#cambioIBANPopUpError').hide();
        			$('#overlay').show();
            	}else{
            		$('#divAviso').show();
					$('#txt_info_ec').show();
					$('#overlay').show();
            	}
            	
            },
            type: "POST"
        });
}