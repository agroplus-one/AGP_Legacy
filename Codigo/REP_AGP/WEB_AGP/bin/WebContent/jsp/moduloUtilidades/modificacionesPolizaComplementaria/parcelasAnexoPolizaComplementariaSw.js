function deshacerCambiosSw(id){
	var table = document.getElementById('parcelasCpl');
    if(table){
     	var rowCount = table.rows.length-1;
     	for(var i=1; i<rowCount; i++) {
			var cell = table.rows[i].cells[0];	
			var idFila = cell.lastChild.value;
     		idFila = idFila.split("#")[0];
			if(idFila == id){
				 var check = table.rows[i].cells[16].innerHTML;
				 if(check.indexOf("CHECKED") != -1){
				 	check = check.replace('CHECKED','');
					table.rows[i].cells[16].innerHTML = check;
				 }
				 table.rows[i].cells[16].innerHTML = '';
				 table.rows[i].cells[14].innerHTML = '';
				 eliminarListadosSw(id);
			}
		}			        
	}
}

function eliminarListadosSw(id){
	var aux = "";
	var str = $('#checksSel').val();
	if(str != ""){
		str = str.split("|");
		if(str.length > 0){
			for(var i=0;i<str.length-1;i++){
				if(id != str[i].split("#")[0]){	
					aux += str[i] + "|";
				}
			}
		}
	}
	$('#checksSel').val(aux);
}

function resetSw(){
	$("input[type=checkbox]").each(function() { 				        
		  if($(this).attr('id').indexOf('check_')!= -1){
		        if($(this).attr('checked')){
		        	 var id = $(this).attr('id').split('_')[1];
		        	 deshacerCambiosSw(id);
		        }
		  }
	});
}

function grabarAndEnviar(){
	$('#method').val('doGuardarAndEnviar');
	$('#main3').submit();
}

//Funci�n para Imprimir un anexo de modificaci�n
function imprimir(){
	
	$("#idCuponImprimir").val($("#idCuponStr").val());
	$('#imprimirAnexoCpl').attr('target', '_blank');
	$("#imprimirAnexoCpl").submit();
}



/*function bajaSw(){
	
	var estado = "B"
	$("input[type=checkbox]").each(function() { 				        
		  if($(this).attr('id').indexOf('check_')!= -1){
		        if($(this).attr('checked')){
		        	 var incrAnt = $(this).attr('value').split('#')[2];
		        	 if(!isNaN(incrAnt)){
		        	 	 var id = $(this).attr('id').split('_')[1];
		        	 	bajaCapitalSw(id);
		        	 }
		        }
		  }
	});
}

function bajaCapitalSw(id){
	var table = document.getElementById('parcelasCpl');
    if(table){
     	var rowCount = table.rows.length-1;
     	for(var i=1; i<rowCount; i++) {
			var cell = table.rows[i].cells[0];	
			var idFila = cell.lastChild.value;
     		idFila = idFila.split("#")[0];
			if(idFila == id){
				 var check = table.rows[i].cells[16].innerHTML;
				 if(check.indexOf("CHECKED") == -1){
				 	 check = check.substring(0,check.length-1);
					 check += " CHECKED>";									
					 table.rows[i].cells[16].innerHTML = check;
				 }
				 var estado = "B"
				 modificarListaSw(id,estado);
				 table.rows[i].cells[17].innerHTML = '';
				 pintarEstadoSw(estado,table.rows[i].cells[15]);
			}
		}			        
	}
}*/
