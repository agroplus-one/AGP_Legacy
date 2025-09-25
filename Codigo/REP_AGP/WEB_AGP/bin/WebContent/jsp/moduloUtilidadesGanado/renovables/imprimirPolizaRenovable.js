
/**
* show popupImprimir
*/
function imprimir(idpoliza,estado,plan,referencia){
	document.getElementById("selImpresion").checked = true;

	// parametros impresión por Ws
	$("#planWs").val(plan);
	$("#referenciaWs").val(referencia);

	
	// parametros para impresión situación actualizada (Copy)
	$("#referenciaPol").val(referencia);
	$("#planPol").val(plan);
	
	
	// todo oculto
	$("#impresionComDefinitiva").css("display",'none');
	$("#impresionPolCopy").css("display",'none');
	if (estado != 2){	// Primera Comunicación
		if (estado == 4){	// Emitida
			$("#impresionComDefinitiva").css("display",'');
			$("#impresionPolCopy").css("display",'');	
		}
		if (estado == 3){ // Comunicación definitiva	
			$("#impresionComDefinitiva").css("display",'');
		}
	}
	
	$('#overlay').show();			
	$('#popUpImprimir').show();
	$('#txt_mensaje_aviso_3').show();
}

function imprimirSeleccion(){
	  var seleccion="ninguno";
      
      var valoresRadio=document.getElementsByName("selImpresion");
      for(var i=0;i<valoresRadio.length;i++){
          if(valoresRadio[i].checked)
              seleccion=valoresRadio[i].value;
      }    
      switch(seleccion) {
      case '1': // 1ª Comunicación
    	  imprimirPorWs("P");
          break;
      case '2': // Comunicación definitiva
    	  imprimirPorWs("D");
          break;
      case '3': // Copy
    	  polizaActualizada();
          break;
      default:
          null;
      }
}

function imprimirPorWs(valorWs){
	hidePopUpImprimir();
	var frm = document.getElementById('printWs');
	frm.valorWs.value = valorWs;
	$('#printWs').attr('target', '_blank');
	$("#printWs").submit();
}

function polizaActualizada(){
	hidePopUpImprimir();
	$('#polizaActualizada').attr('target', '_blank');
	$("#polizaActualizada").submit();	
}

 /**
 * hide popupImprimir
 */
 function hidePopUpImprimir(){
     $('#popUpImprimir').hide();
     $('#overlay').hide();
 }
 
		  	 
		  	 
		  	