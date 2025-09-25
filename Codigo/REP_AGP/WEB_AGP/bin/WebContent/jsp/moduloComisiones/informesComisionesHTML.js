$(document).ready(function(){

});

function ordenacion(campo,sentido){
	
	if(campo != $('#campoOrdenar').val()){
		sentido= "";
	}
	
	$('#campoOrdenar').val(campo);
	if(sentido == ""){
		$('#sentido').val('ASCENDENTE');
	}else{
		if(sentido == "ASCENDENTE"){
			$('#sentido').val('DESCENDENTE');
		}else{
			$('#sentido').val('');
		}
	}
	$("#method").val('doOrdenar');
	$("#main3").submit();	
}

function rellenaStringCabeceras(campo){
	var stringCabeceras = $('#stringCabeceras').val();
	stringCabeceras += campo + "#";
	$('#stringCabeceras').val(stringCabeceras);
}

function obtenerRegistro(numLinea){
	
	//obtengo las cabeceras
	var stringCabeceras = $('#stringCabeceras').val();
	var cabeceras = stringCabeceras.split('#');
	var id = "";
	var celda;
	var value = "";
	var stringRegistro = "";
	
	for (var i=0; i<cabeceras.length-1; i++){
		//para cada cabecera obtengo su valor y lo monto en el string del numero de linea seleccionado
		id = cabeceras[i]+"_"+numLinea;
		celda = document.getElementById(id);
		if (celda != null)
			value = document.getElementById(id).innerText;
		else
			value = ' ';
		
		stringRegistro += cabeceras[i] + "=" + value;
		if(i != cabeceras.length-2){
			stringRegistro += ",";
		}
	}
	$('#stringRegistro').val(stringRegistro);
	
	$("#method").val('doDetalle');
	common.mostrarDetalleInformeComisiones_ajax();
}

function showPopupDetalleComisiones(){
	$('#divPopupDetalleComisiones').fadeIn('normal');
}

function cerrarPopupDetalleComisiones(){
	$('#divPopupDetalleComisiones').fadeOut('normal');
	//eliminamos la tabla
	var tablaDetalle=document.getElementById("tablaDetalle"); 
	while(tablaDetalle.rows.length>0){ 
		tablaDetalle.deleteRow(tablaDetalle.rows.length-1);
	}  
}