//cambiarTitular.js INICIO
function limpiarPaneles(){
	$("#panelAlertasValidacion").hide();
	$('#txt_validacionesRes').html("");
	$('#txt_validacionesRes').hide();
	$('#txt_mensaje_res').html("");
	$('#txt_mensaje_res').hide();
	$("#panelInformacion").hide();
	$("#panelInformacion").html('');
}

//Función a la que se llama desde el botón de cambiar titular
function operacionCambiarTitular(){
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
			//El elemento en 4 es el código de entidad
			$('#codEntidadLupa').val(arrayValores[4]);
			$('#codSubentidadLupa').val(arrayValores[5]);
			$('#isganado').val(arrayValores[6]);
			$('#codEntMedLupa').val(arrayValores[18]);
			validaPolCorrectaCambioTitular_ajax();
		}
	}else{// no hay polizas seleccionadas		
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function cambiarTitular(){
	var frm = document.getElementById('main3');
	if(frm.operacion.value != null && frm.operacion.value == "cambioTitular"){
    	if (document.getElementById('idAseguradoCambioTitular').value !=""){
			if (confirm('¿Seguro que desea cambiar el titular de la póliza?')){
				$('#main3').submit();
			}
		}
	 }
}

function borrarAlerts(){
	
		$('#txt_mensaje_res').html("");
		$('#txt_mensaje_res').hide();
	
}

function validaPolCorrectaCambioTitular_ajax(){

	$.ajax({
            url: "validacionesPolizaAjax.html",
            data: "method=validaPolCorrectaCambioTitular&idsRowsChecked="+$("#idsRowsChecked").val(),
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
            	if(datos.cambioTitularValido.valueOf() == "true"){
        			var frm = document.getElementById('main3');
        			frm.operacion.value = 'cambioTitular';
        			lupas.muestraTabla('CambiarTitular','principio', 'fullName', 'ASC');
            	}else{
            		$('#divAviso').show();
					$('#txt_info_ec').show();
					$('#overlay').show();
            	}
            	
            },
            type: "POST"
        });
}

//cambiarTitular.js FIN