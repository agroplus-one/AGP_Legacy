
/**
 * Funciones específicas para explotaciones Coberturas
 */


// function montarCoberturasExplotacion(){
//    // coberturas existentes
//	var cobExistentes = "";    
//    for(i=0;i<document.datosExplotaciones.elements.length; i++){
//	    if(document.datosExplotaciones[i].type == 'checkbox') {
//	    	if (document.datosExplotaciones[i].checked == true){
//	    		document.datosExplotaciones[i].value = document.datosExplotaciones[i].value+"|S";    		
//	    	}else{
//	    		document.datosExplotaciones[i].value = document.datosExplotaciones[i].value+"|N";
//	    	}
//	    	cobExistentes+= document.datosExplotaciones[i].value +";";
//	    }
//    }
//    //alert(cobExistentes);
//    return cobExistentes;
//}
 
// function dameCoberturaFila(fila,modulo){
//	 for(h=0;h<document.datosExplotaciones.elements.length; h++){
//		 if(document.datosExplotaciones[h].type == 'checkbox') {
//			 var cob = document.datosExplotaciones[h].value.split('|');
//			 if (cob[2] == fila && cob[1] == modulo){
//				 //alert("encontrada");
//				 var encontrada = document.datosExplotaciones[h];
//				 h=1000;
//				 return encontrada;
//			 }
//		 }
//	 }
// }
 
// function grabaChekCob2(obj){
//	 
// }
 
 //# Valores posibles del array de coberturas#
 // obj[0]=id, obj[1]=modulo, obj[2]=fila, obj[3]=CPM, obj[4]=desc CPM, obj[5]=RC, obj[6]=desc RC, obj[7]=vinculada(S/N), obj[8]=elegible(S/N), obj[9]=tipoCobertura
// function grabaChekCob(obj){ 
//	  // CONTROL COBERTURAS
//	  for(i=0;i<document.datosExplotaciones.elements.length; i++){
//		    var mensajeError = "";
//		    if(document.datosExplotaciones[i].type == 'checkbox') {
//		    	var cob = document.datosExplotaciones[i].value.split('|');
//		    	
//		    	//Cobertura elegible obligatoriamente
//		    	// obj[8]=elegible(S), obj[9]=tipoCobertura(B)
//		    	if(document.datosExplotaciones[i].checked==false){
//		    		var cobElegible=cob[8];	    	var cobTipo=cob[9];
//		    		if(cobElegible!=null && cobTipo!=null){
//		    			if(cobElegible=='S' && cobTipo=='B'){
//		    				mensajeError = mensajeError + " La cobertura  "+cob[6]+" es obligatoria ";
//		    			}
//		    		}
//		    	}
//		    	//alert(" revisando cobertura: "+cob);
//		    	if (cob[7] != '' && cob[7] !='null'){
//		    		//cob[7] ="S.4.S.1-S.4.N.2";
//		    		//alert(cob[7]);
//		    		var vinc = cob[7].split('-');
//		    		for(j=0;j<vinc.length; j++){
//		    			var vinci = vinc[j].split('.');
//		    			
//		    			var vinculacion = vinci[0];
//		    			var fila = vinci[1];
//		    			var vinc_elegida = vinci[2];
//		    			var modulo = vinci[3];
//		    			//alert("desglose vinc: "+vinci[0] +" " + vinci[1] +" "+vinci[2]+" "+vinci[3]);
//		    			//alert("buscanco fila "+ fila);
//	    				var cobFila = dameCoberturaFila(fila,modulo, document.datosExplotaciones);
//	    				//alert (cobFila);			
//	    				if (typeof cobFila != 'undefined'){
//	    					//alert("cob encontrada: "+cobFila.value);
//			    			if (vinculacion == 'S' && document.datosExplotaciones[i].checked == true){			
//			    				if (vinc_elegida == 'S'){
//			    					//alert("-S S- la vinculada ha de estar marcada");
//			    					if(cobFila.checked == false){ // S S
//			    						var cobEncontrada = cobFila.value.split('|');
//				    					mensajeError = mensajeError +(" Para poder contratar  "+cob[6]+" debe elegir la garantía "+ cobEncontrada[6]+". ");
//			    					}
//			    				}
//			    				if (vinc_elegida == 'N'){
//			    					//alert("-S N- la vinculada ha de estar desmarcada");
//			    					if(cobFila.checked == true){ // S N
//			    						var cobEncontrada = cobFila.value.split('|');
//				    					mensajeError = mensajeError +(" Para poder contratar "+cob[6]+" no puede elegir la garantía "+ cobEncontrada[6]+". ");
//			    					}
//			    				}
//			    			}  				
//			    			
//			    			if (vinculacion == 'N' && document.datosExplotaciones[i].checked == false){
//			    				if (vinc_elegida == 'S'){
//			    					//alert("-N S- la vinculada ha de estar marcada");
//			    					if(cobFila.checked == false){ // N S
//			    						var cobEncontrada = cobFila.value.split('|');
//				    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" debe elegir la garantía "+ cobEncontrada[6]+". ");
//			    					}
//			    				}
//			    				if (vinc_elegida == 'N'){
//			    					//alert("-N N-la vinculada ha de estar desmarcada");
//			    					if(cobFila.checked == true){ // N N
//			    						var cobEncontrada = cobFila.value.split('|');
//				    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" no puede elegir la garantía "+ cobEncontrada[6]+". ");
//			    					}
//			    				}
//			    			}
//		    			}
//		    			
//		    		}
//		    		//alert("fin vinc .length");
//		    	}
//		    		
//		    }
//		    if (mensajeError != ""){
//		    	break;
//		    }
//	  }	    
//	 	
//	  
//	  if (mensajeError != ""){
//		  $('#panelAlertasValidacion').html(mensajeError);
//		  $('#panelAlertasValidacion').show();
//		  
//	  }else{
//		  return true;
//	  }
//}
 
// function limpiarAlertas(){
//	 $('#panelAlertasValidacion').html("");
//	 $('#panelAlertasValidacion').hide();
// }