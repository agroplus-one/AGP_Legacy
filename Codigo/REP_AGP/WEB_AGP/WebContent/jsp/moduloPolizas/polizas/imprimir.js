
/**
* show popupImprimir
*/
function imprimirInforme(idpoliza,tiporef,estado,refPoliza,plan,tipoLinea,esRenovable){

	document.getElementById("selImpresion").checked = true;
	
	if(tipoLinea=="AGR"){
		$("#impresionBorrReducido").css("display",'');
	}else{
		$("#impresionBorrReducido").css("display",'none');
	}
	
	// parametros impresionborrador
	$("#idPolizaPrint").val(idpoliza);
	$("#estadoPol").val(estado);
	if ($("#estadoPol").val() >8){// polizas renovables cargadas por batch
		$("#impresionBorrNormal").css("display",'none');	
		
		var valoresRadio = document.getElementsByName("selImpresion");
	    valoresRadio[0].checked = false;		
	}
	
	// parametros para impresion ultimo copy
	$('[id=tipoRefPoliza]').each(function () {
		$(this).val(tiporef);
	});
	var frm = document.getElementById('impresionCopy');
	frm.poliza.value = refPoliza;
	$("#plan2").val(plan);
	
	// parametros para impresionsituacionactualizada
	$("#referenciaPol").val(refPoliza);
	$("#planPol").val(plan);
	
	// parametros impresionpor Ws
	$("#planWs").val(plan);
	$("#referenciaWs").val(refPoliza);
	
	// si lapoliza no esta en anulada o enviada correcta o emitida(4 y 16), no aparecen las opciones de sit. actualizada ni copy
	if ($("#estadoPol").val() != 4 && $("#estadoPol").val() != 8 && $("#estadoPol").val() != 14 && $("#estadoPol").val() != 16){	
		$("#impresionSitActualizada").css("display",'none');
		$("#impresionCopyPol").css("display",'none');
	}else{
		$("#impresionSitActualizada").css("display",'');
		$("#impresionCopyPol").css("display",'');
	}
	
	$("#primeraComunicacion").css("display",'none');
	$("#impresionComDefinitiva").css("display",'none');

	if (esRenovable == 'S' || $("#estadoPol").val()>8){
		
		
		// primera comunicacion  : renovables en estado  12-Primera comunicacion, 13-Comunicaciondefinitiva, 14-Emitida  o 16-Anulada.	
		if ($("#estadoPol").val() == 12 || $("#estadoPol").val() == 13 || $("#estadoPol").val() == 14 || $("#estadoPol").val() == 16){
			$("#primeraComunicacion").css("display",'');
		}
		// impresionComDefinitiva: renovables en estado  13-Comunicaciondefinitiva, 14-Emitida o 16-Anulada
		if ($("#estadoPol").val() == 13 || $("#estadoPol").val() == 14 || $("#estadoPol").val() == 16){		
			$("#impresionComDefinitiva").css("display",'');
		}
	}
	
	
	
	var frm = document.getElementById('print');
	if (tiporef == 'C'){
		frm.method.value = 'doInformePolizaComplementaria';	
		if ($("#estadoPol").val() != 8 && $("#estadoPol").val() !=4){	
			$('#print').attr('target', '_blank');
			$("#print").submit();
		}else{
			$("#impresionBorrReducido").css("display",'none');
			$('#overlay').show();			
			$('#popUpImprimir').show();
			$('#txt_mensaje_aviso_3').show();
		}
	} else {
		frm.method.value = 'doInformePoliza';
		
		if(tipoLinea=="AGR"){
			$('#overlay').show();			
			$('#popUpImprimir').show();
			$('#txt_mensaje_aviso_3').show();
		}else{
			//En caso de que sea de ganado nos saltamos el popup si no tiene el estado Enviada Correcta o Emitida(ganado) porque solo existira una opcion a marcar
			if ($("#estadoPol").val() == 4 || $("#estadoPol").val() == 8 || $("#estadoPol").val() == 12|| $("#estadoPol").val() == 13|| $("#estadoPol").val() == 14 ||$("#estadoPol").val() == 16 ){
				$('#overlay').show();			
				$('#popUpImprimir').show();
			}else{
				$("#selImpresion").val(1);//Borrador
				$('#print').attr('target', '_blank');
				$("#print").submit();
			}
		}
	}	
}

function imprimirSeleccion(){
	  var seleccion="ninguno";
      
      var valoresRadio=document.getElementsByName("selImpresion");
      for(var i=0;i<valoresRadio.length;i++){
          if(valoresRadio[i].checked)
              seleccion=valoresRadio[i].value;
      }
    	  
      switch(seleccion) {
      case '1': // borrador normal
    	  imprimirBorrador(false);
          break;
      case '2': // borrador reducido
    	  imprimirBorrador(true);
          break;
      case '3': // situacionactualizada
    	  polizaActualizada();
          break;
      case '4': // 1a Comunicacion
    	  imprimirPorWs("P");
          break;
      case '5': // Comunicaciondefinitiva
    	  imprimirPorWs("D");
          break;
      case '6': // copy
    	  imprimirCopy();
          break;
      default:
    	  var mensajeError = "Es necesario elegir un tipo de impresi\u00F3n"
          $('#mensaje_error').html(mensajeError);
          $("#mensaje_error").css("display",'');
      }
}


function imprimirPorWs(valorWs){
	hidePopUpImprimir();
	var frm = document.getElementById('printWs');
	frm.valorWs.value = valorWs;
	$('#printWs').attr('target', '_blank');
	$("#printWs").submit();
}

function imprimirBorrador(imprimirReducida){
	hidePopUpImprimir();
	var frm = document.getElementById('print');
	frm.StrImprimirReducida.value = imprimirReducida;
	$('#print').attr('target', '_blank');
	$("#print").submit();
}

function imprimirCopy(){
	hidePopUpImprimir();
	$('#impresionCopy').attr('target', '_blank');
	$("#impresionCopy").submit();	
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
	 $('#mensaje_error').css("display",'none');
     $('#popUpImprimir').hide();
     $('#overlay').hide();
 }  	