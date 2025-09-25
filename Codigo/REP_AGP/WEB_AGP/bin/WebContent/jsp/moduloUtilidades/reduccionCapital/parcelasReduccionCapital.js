jQuery.validator.addMethod(
	"validarProdSinInformar",
	function() {
		var esCorrecto = true;
							
		$("input[type=checkbox]").each(
			function() { 				        
				if($(this).attr('id').indexOf('alta_')!= -1 && $(this).attr('checked') == true){
																						
					var altaId = $(this).attr('id').substr(5);													
				
					$("input[type=text]").each(function(){
									
						if ($(this).attr('id').substr(0,9) == "prodPost_" && altaId == $(this).attr('id').substr(9) && 
							(parseFloat($(this).attr('value')) < parseFloat(0) || $(this).attr('value') =="")) {
							//DAA 26/07/2021 parseFloat($(this).attr('value')) <= parseFloat(0))
							quitaCapaEspera ();
							esCorrecto = false;
						}						
						
					});
				}
			}		    
		);
		
		return esCorrecto; 
	}, 
	"Compruebe los datos del formulario"
);

jQuery.validator.addMethod(
	"validarValorProdPost",
	function() {

		var esCorrecto = true;
	
		$("input[type=text]").each(
			function(){												
				if ($(this).attr('id').indexOf('prodPost_')!= -1  && $(this).attr('id').substr(0,9) == "prodPost_" && 
					$(this).attr('disabled') == false) {
	
					var prodPostId = $(this).attr('id').substr(9);
					var prodPostValor = $(this).attr('value'); 
					
					$("label").each(function() {
						if ($(this).attr('id').substr(0,5) == "prod_" && prodPostId == $(this).attr('id').substr(5) && 
							parseFloat($(this).attr('value')) <= parseFloat(prodPostValor)) {							
							quitaCapaEspera ();
							esCorrecto = false;
						}												
					});										
				}						
			}			
		);																						

		return esCorrecto;
	},		 
	"Compruebe los datos del formulario"
);

$(function(){
	$("#grid").displayTagAjax();
}).ajaxComplete(function(){					
	pintarChecksAlta();
	pintarTextProd();
	eventos_checks();
	numero_check_seleccionados(0);					
});

$(document).ready(function(){
	
	$("#main3").validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			"method": {
				validarProdSinInformar: true,
				validarValorProdPost: true
			}
		},
		messages: {
			"method": {
				validarProdSinInformar: "Se debe informar la Producci�n Posterior para todas las Altas",
				validarValorProdPost: "El valor de Producci�n Posterior debe ser menor que la Producci�n Declarada"
			}
		}
	});
	
	var n_checks = 0;
	function numero_check_seleccionados(n_checks){				
	    var str = $('#altaSel').val();
		if(str != ""){
			var str = str.split("|");
			for(var i=0;i<str.length -1;i++){
				if (str[i].indexOf("alta") != -1){
					n_checks = n_checks +1;
				}
			}
		}
		$('#sel').text(n_checks);		  
	}
	numero_check_seleccionados(n_checks);
	
	/*Desactivamos los campos de la columna "Producci�n Posterior" nada m�s empezar*/
	$("input[type=checkbox]").each(function(){
		
		if($(this).attr('id').indexOf('alta_')!= -1){					
        	if(!$(this).attr('checked')){

        		id=$(this).attr('id').substr(5);
        		var prodPost = "prodPost_"+id;		        		
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',true);
					}							
				});		
			}
		}
	});

	/*Captura del evento click en los checkboxs*/
	$("input[type=checkbox]").click(function(){		
		if($(this).attr('id').indexOf('alta_')!= -1){					
        	if($(this).attr('checked')){	
        		        		
        		id=$(this).attr('id').substr(5);
        		var prodPost = "prodPost_"+id;		        		
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',false);
					}							
				});												
        	}else{
        		id=$(this).attr('id').substr(5);		        		
        		var prodPost = "prodPost_"+id;
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',true);
						$('#'+ prodPost).attr('checked',false);
						$('#'+ prodPost).attr('value',0);
					}							
				});				
				
				/*Si uno s�lo de los checks se deschequean, el check "Marcar Todos" tambi�n*/
				$("#selTodos").attr('checked',false); 
						
        	}
		}else{		      	
          	if($(this).attr('id').indexOf('selTodos')!= -1){		          			
          		var prodPost = "prodPost_";	        		
      			$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$(this).attr('disabled',false);
						
					}							
				});						
      		}
	      }        
	});	
		
});

function eventos_checks(){
	/*Desactivamos los campos de la columna "Producci�n Posterior" nada m�s empezar*/
	$("input[type=checkbox]").each(function(){
		
		if($(this).attr('id').indexOf('alta_')!= -1){					
        	if(!$(this).attr('checked')){

        		id=$(this).attr('id').substr(5);
        		var prodPost = "prodPost_"+id;		        		
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',true);
					}							
				});		
			}
		}
	});

	/*Captura del evento click en los checkboxs*/
	$("input[type=checkbox]").click(function(){		
		if($(this).attr('id').indexOf('alta_')!= -1){					
        	if($(this).attr('checked')){	
        		        		
        		id=$(this).attr('id').substr(5);
        		var prodPost = "prodPost_"+id;		        		
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',false);
					}							
				});												
        	}else{
        		id=$(this).attr('id').substr(5);		        		
        		var prodPost = "prodPost_"+id;
        		
        		$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$('#'+ prodPost).attr('disabled',true);
						$('#'+ prodPost).attr('checked',false);
						$('#'+ prodPost).attr('value',0);
					}							
				});				
				
				/*Si uno s�lo de los checks se deschequean, el check "Marcar Todos" tambi�n*/
				$("#selTodos").attr('checked',false); 
						
        	}
		}else{		      	
          	if($(this).attr('id').indexOf('selTodos')!= -1){		          			
          		var prodPost = "prodPost_";	        		
      			$("input[type=text]").each(function(){		        			
					if($(this).attr('id').indexOf(prodPost)!= -1){								
						$(this).attr('disabled',false);
						
					}							
				});						
      		}
	      }        
	});
}

function consultar(){			
	$('#method').val('doConsulta');
	$("#main3").validate().cancelSubmit = true;
	$('#main3').submit();
}

function limpiar(){
	$("#panelErrores").hide();
	$("#panelAlertasValidacion").hide();

	$('#tx_hoja').val('');
	$('#tx_n').val('');
	$('#tx_pol').val('');
	$('#tx_par').val('');	
	$('#txt_provsigpac').val('');
	$('#txt_termsigpac').val('');
	$('#txt_agrsigpac').val('');
	$('#txt_zonasigpac').val('');
	$('#txt_polsigpac').val('');
	$('#txt_parcsigpac').val('');
	$('#txt_recsigpac').val('');
	$('#nomparcela').val('');
	$('#provincia').val('');
	$('#desc_provincia').val('');
	$('#comarca').val('');
	$('#desc_comarca').val('');
	$('#termino').val('');
	$('#desc_termino').val('');
	$('#subtermino').val('');
	$('#cultivo').val('');
	$('#desc_cultivo').val('');
	$('#variedad').val('');
	$('#desc_variedad').val('');
	$('#capital').val('');
	$('#desc_tipocapital').val('');
	$('#superficie').val('');
	$('#prod').val('');
	$('#desc_provincia').val('');
	$('#desc_comarca').val('');
	$('#desc_termino').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#desc_capital').val('');
	consultar();
}
		
function alta(){
	$('#method').val('doAlta');
	if($('#main3').valid()){
		$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').submit();
	}
}
	
function volver(){	
	$('#origenVolver').val('parcelas');
	$('#idReduccionCapitalVolver').val($('#idReduccionCapital').val());
	$("#id").val($('#idReduccionCapital').val());
	$('#idPolizaVolver').val($('#idPoliza').val());
	$('#formVolver').submit();
}

function numero_check_seleccionados(n_checks){				
	var str = $('#altaSel').val();
	if(str != ""){
		var str = str.split("|");
		for(var i=0;i<str.length -1;i++){
			if (str[i].indexOf("alta") != -1){
				n_checks = n_checks +1;
			}
		}
	}
	$('#sel').text(n_checks);			    
}
	
function seleccionar_checks(){
	var str = $('#altaSel').val();
	$("input[type=checkbox]").each(function() { 				        
	        if($(this).attr('id').indexOf('alta_')!= -1){		        	   			        	
	        	$(this).attr('checked',true);
	        	var id = $(this).attr('id').split("_")[1];
	        	var aux_baja = "baja_" + id;
	        	if (str.indexOf(id) == -1){
	        		str += $(this).attr('id') + "|";
	        	} else if (str.indexOf(aux_baja) != -1){		        		
	        		var aux_alta = "alta_" + id;		        		
	        		str = str.replace(aux_baja,aux_alta);
	        	}	        			        			        	
	        }      
	  });
	  $('#altaSel').val(str);		 
	  numero_check_seleccionados(0);
}
	
function capitalAlta(id){
		var str = $('#altaSel').val();			
		if($('#'+id).attr('id').indexOf('alta_')!= -1){
			if($('#'+id).attr('checked')){	
		      	var aux = id.substring(5,id.length);	
		      	var indx = str.indexOf(aux)	      		
		      	if( indx == -1){
		      		str += $('#'+id).attr('id') + "|";				      			
		      	}else{
		      		indx = indx - 5;
		      		var cadena = str.substring(indx,indx + id.length);
		      		cadena = cadena.replace('alta_','baja_');
		      		str = str.replace(cadena,id);
			    }				    			         		        		
		    }else{
		   		var aux = id.substring(5,id.length);
		   		var pos = str.indexOf(aux);
		   		if(pos != -1){
		   			var cadena = id.replace('alta_','baja_');
		   			str = str.replace(id,cadena);
		   		}			   		
		   	}	        	
		}   
		$('#altaSel').val(str);
		numero_check_seleccionados(0);			
}

function capitalProdPost(id,value){
	id = id.split("_")[1];
	var resultado = "";
	var encontrado = false;
	var str = $('#prodSel').val();
	if(str != ""){
		var str = str.split("|");
		for(var i=0;i<str.length -1;i++){
			var aux = str[i].split("#");			
			if(aux[0] != id){
				resultado += aux[0] + "#" + aux[1] + "|";
			}else{
				encontrado = true;
				if(value != ""){
					resultado += aux[0] + "#" + value + "|";
				}		
			}
		}
	}
	
	if(!encontrado){
		var aux = id.substring(9,id.length);
		resultado += id + "#" + value + "|";
	}	
		
	$('#prodSel').val(resultado);				
}
	
function pintarChecksAlta(){
	//ESC-33192
	//var checkeado = " CHECKED>";
	var table = document.getElementById('listCapitalesAsegurados');
    if(table){
     	var rowCount = table.rows.length -1;
	   	var string = "";	         	
	   	if($('#altaSel').val() != ""){
	   		string = $('#altaSel').val().substring(0,$('#altaSel').val().length-1);
	   		string = string.split("|");
	   		for(var i=1; i<rowCount; i++){
	    		for(var j=0; j<string.length; j++){
	       			var datos = string[j];
	        		//var check = table.rows[i].cells[13]; //elemento check alta correcto es 12 y no 13
	        		var check = table.rows[i].cells[12];
	        		if(datos.indexOf("baja_") != -1){
	        			if(check.lastChild.id.split("_")[1] == datos.split("_")[1]){
	        				//var str = check.innerHTML.replace('CHECKED','');
	        				//check.innerHTML = str;
	        				document.getElementById(check.lastChild.id).removeAttribute('checked');
	        			}
	        		}else{
		       			if(check.lastChild.id.split("_")[1] == datos.split("_")[1]){
		       				if(check.innerHTML.indexOf("checked") == -1){
		       					//var str = check.innerHTML.substring(0,check.innerHTML.length-1);
			       				//str += checkeado;			         				
			       				//check.innerHTML = str;
		       					document.getElementById(check.lastChild.id).setAttribute('checked', 'checked');
		       				}
		       			}
	        		}
	       		}
	       	}
	    }
    }
  //ESC-33192
}

function pintarTextProd(){
	var table = document.getElementById('listCapitalesAsegurados');
   	if(table){
   		var rowCount = table.rows.length -1;
	    var string = "";	
	   	if($('#prodSel').val() != ""){
	   		string = $('#prodSel').val().substring(0,$('#prodSel').val().length-1);
	        string = string.split("|");
	        for(var i=1; i<rowCount; i++){
	         	for(var j=0; j<string.length; j++){
	         		var datos = string[j];
	         		var id = datos.split("#")[0];
	         		//ESC-33192
	         		//var cell = table.rows[i].cells[14]; //longitud es 14 pero el ultimo elemento (input) es 13
	         		var cell = table.rows[i].cells[13];
	         		if(cell.lastChild.id.split("_")[1] == id){
	         			//table.rows[i].cells[14].lastChild.value = datos.split("#")[1];
	         			table.rows[i].cells[13].lastChild.value = datos.split("#")[1];//longitud es 14 pero el ultimo elemento (input) es 13
	         		}
	         		//ESC-33192
	         	}
	        }
	    }
    }
}

/**
* Realiza la llamal al SW de validacion previo a la confirmacion del cupon --> P0079361
* @param idCupon
*/
function enviar () {
   muestraCapaEspera ("Validando");
   //alert("ciclo vida anexo parcelas - idCupon: "+$("#idCupon").val()+ " idAnexo: "+ $("#idAnexoModificacion").val());
   altaParcelasRC($("#idCupon").val(),$("#idReduccionCapital").val(),$("#hayCambiosDatosAsegurado").val());
}

function validacionesPreviasEnvioAjax(idCupon,idReduccionCapital, hayCambiosDatosAsegurado){
   
   $.ajax({
		   url: "validacionesRCAjax.html",
		   data: "method=doValidacionesPreviasEnvio&idReduccionCapital="+idReduccionCapital+"&hayCambiosDatosAsegurado="+hayCambiosDatosAsegurado,
		   async:true,
		   cache: false,
		   beforeSend: function(objeto){
			   console.log("Se llama a validacionesPreviasEnvioAjax para mostrar la ventana de validacion si procede");
		   },
		   complete: function(objeto, exito){
		   },
		   contentType: "application/x-www-form-urlencoded",
		   dataType: "json",
		   error: function(objeto, quepaso, otroobj){
			   alert("Error al comprobar la coherencia de los datos variables de las parcelas: " + quepaso);
		   },
		   global: true,
		   ifModified: false,
		   processData:true,
		   success: function(datos){

			   
				if(datos.validacionesPreviasEnvio.valueOf() == "true"){
            		$("#idReduccionCapitalConfirmarRC").val(idReduccionCapital);
            		$("#validarAnexo").submit();
            	}else{
            		quitaCapaEspera ();
            		//$('#panelInformacion').hide();
            		$('#panelAlertasValidacion').html(datos.mensaje);
            		$('#panelAlertasValidacion').show();
            	}
			   
		   },
		   type: "POST"
	   });
}
function altaParcelasRC(idCupon,idReduccionCapital, hayCambiosDatosAsegurado){
   if ($('#altaSel').val()==""){
		quitaCapaEspera ();
		$('#panelAlertasValidacion').html("No se ha seleccionado ninguna parcela");
		$('#panelAlertasValidacion').show();
   }else{
	alta();
   }
}

function calcular(idRC) {
	$.ajax({
		url:          "calculoRC.html",
		data:         "method=doCalculoRC&idRC=" + idRC,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error: " + quepaso);
			$.unblockUI();
		},
		success: function(resultado){
				console.log("GOLA")
		},
		type: "GET"
	});	
}

/*function confirmarRedCap(idRC) {
    $.ajax({
        url:          "confirmarRC.html",
        data:         "method=doConfirmarRC&idRC=" + idRC,
        async:        true,
        contentType:  "application/x-www-form-urlencoded",
        dataType:     "json",
        global:       false,
        ifModified:   false,
        processData:  true,
        error: function(objeto, quepaso, otroobj){
            alert("Error en la la confirmacion de Reduccion de Capital: " + quepaso);
            $.unblockUI();
        },
        success: function(resultado){
                console.log("Llamada AJAX correcta para la confirmacion de Reduccion de Capital")
        },
        type: "GET"
    });    
}*/
	   
/**
* Muestra la capa de espera con el mensaje recibido como parametro
* @param msg
* @returns
*/
function muestraCapaEspera (msg) {
   $.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}
/**
 * Elimina la capa de espera
 */
function quitaCapaEspera () {
	$.unblockUI();
}