//TMR 12/06/2013 js para operaciones de Cambiar clase

function validaPolCorrectasCambioClaseMasivo_ajax(){

	$.ajax({
            url: "cambioClaseMasivo.html",
            data: "method=validaPolCorrectasCambioClaseMasivo&idsRowsChecked="+$("#idsRowsChecked").val(),
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
            	if(datos.claseMasivoValido.valueOf() == "true"){
            		$("#listaIdsPlz").val(datos.listaIdsPlz.valueOf());
            		showPopUpCambioClase();
            	}else{
            		$('#divAviso').show();
					$('#txt_info_ec').show();
					$('#overlay').show();
            	}
            	
            },
            type: "POST"
        });
}

function showPopUpCambioClase(){
 	$('#divCambioClaseMasivo').fadeIn('normal');
	$('#overlay').show();
}

function cerrarPopUpCambioClaseMasivo(){
	$('#divCambioClaseMasivo').fadeOut('normal');
	$('#overlay').hide();
	$('#claseCM').val('');	
}

function doCambioClaseMasivo(){
	$("#frmCambioClaseMasivo").submit();

}		 	  	