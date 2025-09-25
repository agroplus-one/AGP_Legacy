$(function(){
	$("#grid").displayTagAjax();
}).ajaxSend(function(){

}).ajaxComplete(function(){
	check_checks($('#listaIds').val());
	numero_check_seleccionados();
	pintarEstadoModif();
	pintarIncreModif();
});

$(document).ready(function(){

	numero_check_seleccionados();
	
	/* Pet. 78691 ** MODIF TAM (22/12/2021) */
	$('#sistemaCultivo').val($('#sist_cultivo').val());
	$('#dessistemaCultivo').val($('#des_sist_cultivo').val());
	/* Pet. 78691 ** MODIF TAM (22/12/2021) */
	
	//TMR 13-11-2012
	if(document.getElementById("parcelasCpl") != null){
		     	
    	var frm = document.getElementById('main3');
    	
     	frm.selTodos.checked=false;
     	if(frm.marcarTodos.value=="true"){
     		marcar_todos();
    	}
    	else {
    		if ($('#listaIds').val() != ''){
    			check_checks($('#listaIds').val());
    		}
    		else{
    			desmarcar_todos();
    		}
    	}
    	if (frm.incrementoOK.value=="true"){
     		desmarcar_todos();
     	}
    }	
	
});

function pintarIncreModif(){
	var table = document.getElementById('parcelasCpl');
     if(table){
     	var rowCount = table.rows.length -1;
     	var string = "";
     	if($('#incrSel').val() != ""){
     		string = $('#incrSel').val();
     		string = string.split("|");
     		for(var i=1; i<= rowCount; i++){
     			for(var j=0; j<string.length-1; j++){
     				var datos = string[j].split("#")[0];
     				var cell = table.rows[i].cells[0];
     				var id = cell.lastChild.value;
     				id = id.split("#")[0];
     				if(id == datos){
     					table.rows[i].cells[16].innerHTML =  string[j].split("#")[1];
     				}
     			}
     		}
     	}
     }
}

function pintarEstadoModif(){
	var table = document.getElementById('parcelasCpl');
     if(table){
     	var rowCount = table.rows.length -1;
     	var string = "";
     	if($('#checksSel').val() != ""){
     		string = $('#checksSel').val();
     		string = string.split("|");
     		for(var i=1;i <= rowCount;i++){
     			for(var j=0; j<string.length-1; j++){
     				var datos = string[j].split("#")[0];
     				var cell = table.rows[i].cells[0];
     				var id = cell.lastChild.value;
     				id = id.split("#")[0];
     				if(id == datos){
     					pintarEstado(string[j].split("#")[1],table.rows[i].cells[14])
     				}
     			}
     		}
     	}
     }
}
function pintarEstado(estado,celda){
	if(estado == "M"){
		celda.innerHTML = "Modif";
	}else if(estado == "A"){
		celda.innerHTML = "Alta";
	}else{
		celda.innerHTML = "Baja";
	}
}

function calcularEstado(celda){
	if(celda != "" && !isNaN(celda)){
		return "M";
	}else
		return "A";
}


function bajaCapital(id){
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
						 check += "";
						 table.rows[i].cells[16].innerHTML = check;
					 }
					 var estado = "B"
					 modificarLista(id,estado);
					 table.rows[i].cells[17].innerHTML = '';
					 pintarEstado(estado,table.rows[i].cells[14]);
				}
			}			        
		}
}

function modificarLista(id,estado){
	var aux = "";
	var str = $('#checksSel').val();
	var encontrado = false;
	if(str != ""){
		str = str.split("|");
		if(str.length > 0){
			for(var i=0;i<str.length-1;i++){
				if(id != str[i].split("#")[0]){	
					aux += str[i] + "|";
				}else{
					aux += id + "#" + estado + "|";
					encontrado = true;
				}
				
			}
		}
	}
	if(!encontrado)
		aux += id + "#" + estado + "|";
		
	$('#checksSel').val(aux);
}
function deshacerCambios(id){
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
				 table.rows[i].cells[17].innerHTML = '';
				 table.rows[i].cells[15].innerHTML = '';
				 eliminarListados(id);
			}
		}			        
	}
}

function eliminarListados(id){
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

function baja(){
	
	var estado = "B"
	$("input[type=checkbox]").each(function() { 				        
		  if($(this).attr('id').indexOf('check_')!= -1){
		        if($(this).attr('checked')){
		        	 var incrAnt = $(this).attr('value').split('#')[2];
		        	 if(!isNaN(incrAnt)){
		        	 	 var id = $(this).attr('id').split('_')[1];
		        	 	bajaCapital(id);
		        	 }
		        }
		  }
	});
}

function reset(){
	$("input[type=checkbox]").each(function() { 				        
		  if($(this).attr('id').indexOf('check_')!= -1){
		        if($(this).attr('checked')){
		        	 var id = $(this).attr('id').split('_')[1];
		        	 deshacerCambios(id);
		        }
		  }
	});
}

function salir(){
	$(window.location).attr('href', 'declaracionesModificacionPoliza.html?idPoliza='+ $('#idPoliza').val() + '&rand='+encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime())); 		
}

function limpiar(){
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_termino').val('');
	$('#desc_comarca').val('');
	$('#desc_capital').val('');
	$('#poligono').val('');
	$('#parcela').val('');
	$('#provSig').val('');
	$('#TermSig').val('');
	$('#agrSig').val('');
	$('#zonaSig').val('');
	$('#polSig').val('');
	$('#parcSig').val('');
	$('#recSig').val('');
	$('#nombre').val('');
	$('#cultivo').val('');
	$('#variedad').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#capital').val('');
	$('#superficie').val('');
	$('#prodAnt').val('');
	$('#estado').selectOptions('');
	/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto Nº 2 */
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');
	
	$('#sist_cultivo').val('');
	$('#des_sist_cultivo').val('');
	/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto Nº 2 */
	
	consultar();
}

function coberturas(){
	$('#method').val('doCoberturas');
	$('#main3').submit();
}

function consultar(){
	$('#method').val('doConsulta');
	$('#main3').submit();
}

function grabar(){
	$('#method').val('doGuardar');
	$('#main3').submit();
}

function imprimir(){
	$('#idPolizaPrint').val($('#idPoliza').val());					
	$('#printCpl').attr('target', '_blank');
	$("#printCpl").submit();	
}

function volverAnexoListado(){
	$('#volverUtilidadesAnexos').submit();
}