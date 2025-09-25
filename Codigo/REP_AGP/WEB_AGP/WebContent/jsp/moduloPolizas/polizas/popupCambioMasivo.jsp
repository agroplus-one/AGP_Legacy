<!--                                                     -->		
<!-- popupCambioMasivo.jsp (show in listadoparcelas.jsp) -->
<!--                                                     -->

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
<script><!--
			
            $(document).ready(function(){  
            	setValidate();
                $(".panelCambioMasivo").draggable();	
            });
            
            
            $(document).ready(function() {
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaSiembra_cm",
			        button            : "btn_fechaSiembra_cm",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"
			        
			    });
		    });
            
            $(document).ready(function() {
		      	Zapatec.Calendar.setup({
			        firstDay          : 1,
			        weekNumbers       : false,
			        showOthers        : true,
			        showsTime         : false,
			        timeFormat        : "24",
			        step              : 2,
			        range             : [1900.01, 2999.12],
			        electric          : false,
			        singleClick       : true,
			        inputField        : "fechaFinGarantia_cm",
			        button            : "btn_fechaFinGarantia_cm",
			        ifFormat          : "%d/%m/%Y",
			        daFormat          : "%d/%m/%Y",
			        align             : "Br"
			        
			    });
		    });
		      	
		      	
            function cambioMasivo(){
                if(haveParcelaSele()){
	               //if(isEqualsLocalizacion()){
	                      if(coincidenDatosFiltro()){
					  	     	$('#overlayCambioMasivo').show();
							 	$('#panelCambioMasivo').show();
							 	setFiltroPopUpCambioMasivo();
						  }else{
						      showPopUpAviso("La ubicación de la(s) parcela(s) debe coincider con la introducida en el filtro.");
						  }
					//}else{
					//        showPopUpAviso("La ubicación no es la misma en todas las parcelas selecionadas.");
					//}
				 }else{
				     showPopUpAviso("Debe seleccionar como mínimo una parcela.");
				 }
		  	 }
		  	 
		  	 function borradoMasivo(){
		  	     if(haveParcelaSele()){
	               if(confirm('¿Está seguro de que desea eliminar las parcelas seleccionadas ?')){
						  var frm = document.getElementById("frmCambioMasivo");
						  frm.checked_form_parcela_cm.value    = getRowsChecks();
						  frm.method.value                     = "doBorrarMasivo";
						  frm.idpolizaCM.value   			   = $('#idpoliza').val();
						  frm.idsRowsCheckedCM.value		   = $('#idsRowsChecked').val();
						  frm.tipoListadoGridCM.value		   = $('#tipoListadoGrid').val();
						  $.blockUI.defaults.message = '<h4> Borrando las parcelas seleccionadas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       			      $.blockUI({ 
	       			          overlayCSS: { backgroundColor: '#525583'},
	       			          baseZ: 2000
	       			      });
						  frm.submit();  
					 }
				 }else{
				     showPopUpAviso("Debe seleccionar como mínimo una parcela.");
				 }
		  	 }
		  	 
		  	 
		  	 // popup aviso
		  	 function showPopUpAviso(mensaje){
		  	     $('#txt_mensaje_aviso').html(mensaje);
		  	     $('#popUpAvisos').show();
		  	     $('#overlayCambioMasivo').show();
		  	 }
		  	 function hidePopUpPanelAvisos(){
		  	     $('#popUpAvisos').hide();
		  	     $('#overlayCambioMasivo').hide();
		  	 }
		  	 
		  	 
		  	 function validateFormCambioMasivo(){
		  	     return $('#frmCambioMasivo').valid();
		  	 }  
		  	 function haveParcelaSele(){
		  	 
		  	     var itemsChecked = $('#idsRowsChecked').val();
		  	     if(itemsChecked.length > 0)
		  	        return true;
		  	     else
		  	        return false;
		  	 }   
		  	 function coincidenDatosFiltro(){
		  	     var ubicacion_parcela = getUbicacionPrimeraParcelaChecked();
		  	     var result = true;
		  	     var ubicacion_filtro1;
		  	     var ubicacion_filtro2;

		  	     var ubicacion_filtro1 = "@@" + $('#provincia').val() + ";;" + $('#comarca').val() + ";;" + 
		  	                           $('#termino').val() + ";;" + $('#subtermino').val() + "@@";
		  	                           
		  	     if($('#subtermino').val() == ""){
		  	         var ubicacion_filtro2 = "@@" + $('#provincia').val() + ";;" + $('#comarca').val() + ";;" + 
		  	                           $('#termino').val() + ";;" + " " + "@@";
		  	     }
		  	                     
                 if(hayAlgunCampoUbicacionRelleno()){  
			  	     if(ubicacion_parcela == ubicacion_filtro1 || ubicacion_parcela == ubicacion_filtro2){
			  	        result = true;
			  	     }else{
			  	        result = false;
			  	     }
		  	     }else{
		  	         result = true;
		  	     }
		  	     
		  	     return result;
		  	 }
		  	 function hayAlgunCampoUbicacionRelleno(){
		  	     var count = 0;
		  	     
		  	     if($('#provincia').val()  != "") count++;
		  	     if($('#comarca').val()    != "") count++;
		  	     if($('#termino').val()    != "") count++;
		  	     if($('#subtermino').val() != "") count++;

		  	     if(count > 0){
		  	         return true
		  	     }else{
		  	         return false;
		  	     }
		  	 }   
		  	 function getUbicacionPrimeraParcelaChecked(){
		  	     var rows             = $('#listaParcelas_cm').find('tr').get();
			  	 var localizacion     = "";
			  	 var aux_localizacion = "";
			  	 var count_cm         = 0;
			  	 var result           = true;
			  	 var checked_parce_cm = "";
			  	 
			  	 for (i = 0, j = rows.length; i < j; ++i)
			  	 {     
				 	      cells = rows[i].getElementsByTagName('td');
					      if(cells.length > 0){ 
						       if (cells[0].innerHTML.indexOf("P@P") != -1) {            
                                   var aaa2   = $(cells[0]).children("#localizacion_cm") 
                                   var check2 = $(cells[15]).children("input:first");
	                               if($(check2).attr('checked')) {                                                    
	                                       return $(aaa2).val();
                                   }
						       }
					       }
				 }//for
		  	 }
		  	 /**
		  	  * recorrer las filas seleccionadas comprobando que tienen la misma localizacion
		  	  */
		  	 function isEqualsLocalizacion()
		  	 {
		  	     var result = true;
		  	     var items = $('#parcelasString').val(); // todos los items
		  	     var itemsChecked = $('#idsRowsChecked').val(); // solo los items checked
		  	     var localizaciones = [];
		  	     var arrayItems = items.split(";"); // array de items
		  	     var arrayItemsChecked = itemsChecked.split(";"); // array de items checkes
		  	      
		  	      
		  	      // debug: alert(" all: " + items + "   checked: " + itemsChecked);
		  	      
		  	     // recorro arrayItemsChecked
		  	     for(var i = 0; i < arrayItemsChecked.length; i++)
		  	     {
		  	         var item = arrayItemsChecked[i];
		  	         // recorro el arrayItems buscando la localizacion
		  	         for(var e = 0; e < arrayItems.length; e++)
		  	         {
		  	             //alert("1:" + arrayItems[e]);
		  	             var arrayElem = (arrayItems[e]).split("_");
		  	             if(arrayElem[0] != undefined && arrayElem[2] != undefined)
		  	             {
			  	             var idElem =  arrayElem[0];
			  	             var locali = arrayElem[2];
			  	             // debug: alert("2 comparacion:" + idElem + "   " + locali);
	
	                         if(idElem != undefined && item != undefined)
	                         {
				  	             if(idElem == item)
				  	             {
				  	                  localizaciones.push(locali);
				  	             }  
			  	             }
			  	         }
		  	         }
		  	     }
		  	     
		  	     // miramos que todas las localizaciones encontradas son iguales (me salto el primero)
		  	     var local1 = localizaciones[1];
		  	     for(var m = 0;m < localizaciones.length; m++){
		  	         // debug: alert(local1 + "  " + localizaciones[m]);
		  	         if(localizaciones[m] != undefined && local1 != undefined){
			  	         if(local1 != localizaciones[m]){
			  	             
			  	             result = false;
			  	         }
			  	     }
		  	     }

			  	 return result;
		  	 }
		  	 
		  	 
		  	 function getRowsChecks(){
		  	 
		  	     var result; 
		  	     var rows             = $('#listaParcelas_cm').find('tr').get();
			  	 var localizacion     = "";
			  	 var aux_localizacion = "";
			  	 var count_cm         = 0;
			  	 var result           = true;
			  	 var checked_parce_cm = ""; // ejemplo: codigo_parcela1@@codigo_parcela2 ...
			  	 
			  	 for (i = 0, j = rows.length; i < j; ++i){     
					      cells = rows[i].getElementsByTagName('td');
					      if(cells.length > 0){ 
						       if (cells[0].innerHTML.indexOf("P@P") != -1){            
                                   var check2 = $(cells[15]).children("input:first");
	                               if($(check2).attr('checked')){                        
	                                   if(count_cm == 0){  
	                                       checked_parce_cm = ($(cells[0]).children("#idRow_cm")).attr('value');                               
	                                   }else{
	                                       checked_parce_cm = checked_parce_cm + "@@" + ($(cells[0]).children("#idRow_cm")).attr('value');
	                                   }
	                                   count_cm ++
                                   }//if
						       }//if
					       }//if
				 }//for

				 return checked_parce_cm;
		  	 }
		  	 
		  	 function aplicarCambioMasivo(){
		  		
		  		if (hayAlgunCampoRelleno()){ // validamos que haya algun campo relleno 
		  		 	if(validateFormCambioMasivo()){ // validamos que los campos tengan datos validos (validate jquery)
		  		 		validaCamposLupas()// validamos que los campos de las lupas sean correctos (ajax) y se hace el submit
		  		 	}
				}
		  	 }
		  	 
		  	 function submitMain3FromCambioMasivo(){
		  		
		  			jConfirm('¿Está seguro de que desea modificar las parcelas seleccionadas y sus instalaciones asociadas?', 
			  				'Diálogo de Confirmación', function(r) {
						if (r){
							
							  var frm = document.getElementById("frmCambioMasivo");
						var recalcular=false;
							  frm.idpolizaCM.value   			   = $('#idpoliza').val();
							  frm.idsRowsCheckedCM.value		   = $('#idsRowsChecked').val();
							  frm.tipoListadoGridCM.value		   = $('#tipoListadoGrid').val();
							  
							  // Ubicacion
							  frm.provincia_form_cm.value 	= $('#provincia_cm').val();
							  frm.comarca_form_cm.value 	= $('#comarca_cm').val();
							  frm.termino_form_cm.value 	= $('#termino_cm').val();
							  frm.subtermino_form_cm.value 	= $('#subtermino_cm').val();
							  
							  // SIGPAC
							  frm.provSig_form_cm.value            = $('#provSig_cm').val();
							  frm.termSig_form_cm.value            = $('#termSig_cm').val();
							  frm.agrSig_form_cm.value             = $('#agrSig_cm').val();
							  frm.zonaSig_form_cm.value            = $('#zonaSig_cm').val();
							  frm.polSig_form_cm.value             = $('#polSig_cm').val();
							  frm.parcSig_form_cm.value            = $('#parcSig_cm').val();
							  frm.recSig_form_cm.value             = $('#recSig_cm').val();
							  // cultivo y variedad
							  frm.cultivo_form_cm.value            = $('#cultivo_cm').val();
							  frm.variedad_form_cm.value           = $('#variedad_cm').val();
							  // produccion,superficie y precio 
							  frm.incremento_form_ha_cm.value      = $('#inc_ha_cm').val();
							  frm.incremento_form_parcela_cm.value = $('#inc_parcela_cm').val();
							  frm.incremento_form_unidades_cm.value = $('#inc_unidades_cm').val();
							  frm.superficie_form_cm.value         = $('#superficie_cm').val();
							  frm.precio_form_cm.value             = $('#precio_cm').val();
							    
							  //datos variables 
							  frm.destino_form_cm.value				= $('#destino_cm').val();
							  frm.tipoPlant_form_cm.value			= $('#tplantacion_cm').val();
							  frm.sistemaCultivo_form_cm.value		= $('#sistemaCultivo_cm').val();							  
							  frm.sistemaProduccion_form_cm.value	= $('#sistemaProduccion_cm').val();
							  frm.marcoPlant_form_cm.value			= $('#codtipomarcoplantac_cm').val();
							  frm.practicaCul_form_cm.value			= $('#codpracticacultural_cm').val();
							  frm.fechaSiembra_form_cm.value        = $('#fechaSiembra_cm').val();
							  frm.fechaFinGarantia_form_cm.value	= $('#fechaFinGarantia_cm').val();
							  frm.edad_form_cm.value				= $('#edad_cm').val();
							  frm.incremento_edad_form_cm.value		= $('#incEdad_cm').val();
							  frm.unidades_form_cm.value			= $('#unidades_cm').val();
							  // Si se modifican datos  que afectan al precio o a la produccion
							  // (todos los datos variables menos produccion,precio y tipoPlantacion)
							  // se le pregunta al usuario si desea recalcular. 
							  if (( $('#destino_cm').val()!= "" || $('#sistemaCultivo_cm').val() != "" || $('#sistemaProduccion_cm').val()!= ""
									  || $('#codtipomarcoplantac_cm').val()!= "" ||$('#codpracticacultural_cm').val()!= ""
									  || $('#fechaSiembra_cm').val() != "" || $('#fechaFinGarantia_cm').val() != ""
									  || $('#edad_cm').val() != "" || $('#incEdad_cm').val()!= "" || $('#unidades_cm').val() != ""
									  || $('#cultivo_cm').val() != "" || $('#variedad_cm').val() != '' || $('#provincia_cm').val() != ''
									  || $('#comarca_cm').val() != ''|| $('#termino_cm').val() != '' || $('#subtermino_cm').val() != ''
									  || $('#provSig_cm').val() != ''|| $('#termSig_cm').val() != '' || $('#agrSig_cm').val() != ''
									  || $('#zonaSig_cm').val() != ''|| $('#polSig_cm').val() != ''|| $('#parcSig_cm').val() != ''
									  || $('#recSig_cm').val() != '' || $('#superficie_cm').val() != '' ||  $('#sistemaProduccion_cm').val() !='')
									  && mostrarRecalcular()){
								
									 $.unblockUI();
					     	     	 $('#overlay').show();
					     	    	 $("#popupRecalcular").show();
								 
							  }else{
							  	frm.recalcular.value ="false";
							  	addcapaEspera();
					     	  	frm.submit();
							  }
						 }
			  		});
		  		
		  	 }
		  	 
		  	 function mostrarRecalcular(){
		  		var tienePrecio=$('#precio_cm').val()!='';
		  		var tieneProduccion=$('#inc_ha_cm').val()!='' || $('#inc_parcela_cm').val()!='' || $('#inc_unidades_cm').val() != '';
		  		var mostrar=true;
		  	
		  		if(tienePrecio && tieneProduccion){
			  		 mostrar= false;
			  	}
		  	
		  		return mostrar
		  		 
		  	 }
		  	function continuar(recalcular)
	     	{
	     	    $("#popupRecalcular").hide();
	            $('#overlay').hide();
	            
	            var frm = document.getElementById("frmCambioMasivo");

				if (recalcular == "si"){
					//Cuando recalcular es si, ponemos de nuevo la capa (se quitó para poder responder a la pregunta)
	            	$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		       		frm.recalcular.value ="true";
		       	}else{
		       		frm.recalcular.value ="false";
		       	}
				
				addcapaEspera();
				frm.submit();  
		     	
			  }
			  
		  	
		  	function validaCamposLupas(){
		  		 
				$.ajax({
					url: 'validaLupasCambioMasivoController.html',
					data: 'method=doValidar&cultivo=' + $('#cultivo_cm').val() + '&variedad=' +$('#variedad_cm').val()
					+'&lineaseguroId=' + $('#lineaseguroid').val()+'&destino=' + $('#destino_cm').val()
					+'&tipoPlantacion=' + $('#tplantacion_cm').val()+'&sisCultivo=' + $('#sistemaCultivo_cm').val()
					+'&tipoMarcoPlan=' + $('#codtipomarcoplantac_cm').val()+'&practicaCultural=' + $('#codpracticacultural_cm').val()
					+'&provincia=' + $('#provincia_cm').val()+'&comarca=' + $('#comarca_cm').val()
					+'&termino=' + $('#termino_cm').val()+'&subtermino=' + $('#subtermino_cm').val()
					+'&sistProd=' + $('#sistemaProduccion_cm').val()
					+'&listCodModulos_cm=' + $('#listCodModulos_cm').val(),
					dataType: 'json',
					success: function(datos){
						if(datos.errores.length > 0){
							var str = "";
							for(var i=0;i<datos.errores.length;i++){
								str +=datos.errores[i];
								$('#panelAlertasValidacion_cm').html(str);
								$('#panelAlertasValidacion_cm').show();
							}
						}else {
							submitMain3FromCambioMasivo();
						}
					},
					beforeSend: function(){
					},
            		complete: function(){
            		},
					type: 'post'
				});
			}
		  	
		  	function addcapaEspera(){			  
			  $.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		      $.blockUI({ 
		          overlayCSS: { backgroundColor: '#525583'},
		          baseZ: 2000
		      });
		  	}
		  	 function hayAlgunCampoRelleno(){
				if ( $('#cultivo_cm').val() != '' ||$('#variedad_cm').val() != '' || $('#provincia_cm').val() != ''
						 || $('#comarca_cm').val() != ''|| $('#termino_cm').val() != '' || $('#subtermino_cm').val() != ''
						 || $('#provSig_cm').val() != ''|| $('#termSig_cm').val() != '' || $('#agrSig_cm').val() != ''
						 || $('#zonaSig_cm').val() != ''|| $('#polSig_cm').val() != ''|| $('#parcSig_cm').val() != ''
						 || $('#recSig_cm').val() != '' || $('#fechaFinGarantia_cm').val() != ''|| $('#codtipomarcoplantac_cm').val() != ''
						 || $('#codpracticacultural_cm').val() != '' || $('#unidades_cm').val() != ''|| $('#precio_cm').val() != ''
						 || $('#edad_cm').val() != ''|| $('#incEdad_cm').val() != ''|| $('#inc_unidades_cm').val() != ''
						 || $('#inc_ha_cm').val()!= ''|| $('#inc_parcela_cm').val() != ''|| $('#superficie_cm').val() != ''|| $('#fechaSiembra_cm').val() != ''
						 || $('#destino_cm').val() != '' || $('#tplantacion_cm').val() != '' || $('#sistemaCultivo_cm').val() != ''
						 || $('#sistemaProduccion_cm').val() != ''){
					
					return true;	 
				 }else{
					 $('#panelAlertasValidacion_cm').html("Debe rellenar al menos un campo");
					 $('#panelAlertasValidacion_cm').show();
					 return false;		 
				 }
		  	 }
		  	 
		  	 function cerrarPopUpCambioMasivo(){
		  	     resetPopUpCambioMasivo();
		  	     $('#panelCambioMasivo').hide();
		  	     $('#overlayCambioMasivo').hide();
		  	 }
		  	 function resetPopUpCambioMasivo(){
		  	 		
		  		 $('#cultivo_cm').val('');
		  	     $('#desc_cultivo_cm').val('');		  	     
		  	     $('#variedad_cm').val('');
		  	     $('#desc_variedad_cm').val('');
		  	  	 $('#provincia_cm').val('');
		  	 	 $('#desc_provincia_cm').val('');
		  		 $('#comarca_cm').val('');
		  		 $('#desc_comarca_cm').val('');
		  	 	 $('#termino_cm').val('');
		  	 	 $('#desc_termino_cm').val('');
		  	     $('#subtermino_cm').val('');
		  		
		  	     $('#provSig_cm').val('');
		  	     $('#termSig_cm').val('');
		  	     $('#agrSig_cm').val('');
		  	     $('#zonaSig_cm').val('');
		  	     $('#polSig_cm').val('');
		  	     $('#parcSig_cm').val('');
		  	     $('#recSig_cm').val('');
		  	     
		  	     $('#fechaFinGarantia_cm').val('');
		  	     $('#codtipomarcoplantac_cm').val('');
		  	     $('#destipomarcoplantac_cm').val('');
		  	     $('#codpracticacultural_cm').val('');
		  	     $('#despracticacultural_cm').val('');
		  	     
		  	     $('#unidades_cm').val('');
		  	     $('#precio_cm').val('');
		  	     $('#edad_cm').val('');
		  	     $('#incEdad_cm').val('');
			     
		  	     $('#inc_unidades_cm').val('');
		  	     $('#inc_ha_cm').val("");
		  	     $('#inc_parcela_cm').val('');
		  	     $('#superficie_cm').val('');
		  	     $('#fechaSiembra_cm').val('');
		  	     $('#destino_cm').val('');
		  	     $('#desc_destino_cm').val('');
		  	     $('#tplantacion_cm').val('');
		  	     $('#desc_tplantacion_cm').val('');
		  	     $('#sistemaCultivo_cm').val('');
		  	     $('#desc_sistemaCultivo_cm').val('');
		  	     
		  	   	 $('#sistemaProduccion_cm').val('');
		  	     $('#desc_sistemaProduccion_cm').val('');
		  	   
		  		 
		  		 cleanPanelErroresCambioMasivo();
		  	     //setDefaultLabelValues();
		  	     
		  	 }
		  	 function cleanPanelErroresCambioMasivo(){
		  	     $('#panelAlertasValidacion_cm').html("");
		  	     $('#panelAlertasValidacion_cm').hide();
		  	 }
		  	 function setDefaultLabelValues(){
		  	     $('#lbl_valor_provincia').html("__"); 
		  	     $('#lbl_valor_desc_provincia').html("____________________");
		  	     $('#lbl_valor_comarca').html("__"); 
		  	     $('#lbl_valor_desc_comarca').html("____________________");
		  	     $('#lbl_valor_termino').html("___"); 
		  	     $('#lbl_valor_desc_termino').html("______________________________");
		  	     $('#lbl_valor_subtermino').html("__"); 
		  	     $('#lbl_valor_poligono').html("____"); 
		  	     $('#lbl_valor_parcela').html("____");
		  	     $('#lbl_valor_provSig').html("__");
		  	     $('#lbl_valor_TermSig').html("___"); 
		  	     $('#lbl_valor_agrSig').html("__"); 
		  	     $('#lbl_valor_zonaSig').html("__"); 
		  	     $('#lbl_valor_polSig').html("___"); 
		  	     $('#lbl_valor_parcSig').html("_____"); 
		  	     $('#lbl_valor_recSig').html("_____"); 
		  	     $('#lbl_nombre').html("______________________________________"); 
		  	     $('#lbl_valor_cultivo').html("__"); 
		  	     $('#lbl_valor_desc_cultivo').html("________________________"); 
		  	     $('#lbl_valor_variedad').html("__"); 
		  	     $('#lbl_valor_desc_variedad').html("________________________");
		  	 }
		  	 function limpiarPopUpCambioMasivo(){
		  	     resetPopUpCambioMasivo();
		  	 }
	
		  	 function setFiltroPopUpCambioMasivo(){
		  	     setDatosPoliza();
		  	     setProvincia(); 
		  	     setComarca();
		  	     setTermino();
		  	     setSubtermino();
		  	     setSIGPAC();
		  	     setPoligonoParcela();
		  	     setNombre();
		  	     setCultivo();
		  	     setVariedad();    
		  	 }
		  	 function setProvincia(){
		  	     // código
		  	     if($('#provincia').val() == "")
		  	         $('#lbl_valor_provincia').html("__");
		  	     else
		  	         $('#lbl_valor_provincia').html("<span class='data'>" + $('#provincia').val() + "</span>");
		  	     // descripción
		  	     if($('#desc_provincia').val() == "")
		  	         $('#lbl_valor_desc_provincia').html("____________________");
		  	     else
		  	         $('#lbl_valor_desc_provincia').html("<span class='data'>" + $('#desc_provincia').val() + "</span>");
		  	 }
		  	 
		  	 function setComarca(){
		  	     // código
		  	     if($('#comarca').val() == "")
		  	         $('#lbl_valor_comarca').html("__");
		  	     else
		  	         $('#lbl_valor_comarca').html("<span class='data'>" + $('#comarca').val() + "</span>");
		  	     // descripción
		  	     if($('#desc_comarca').val() == "")
		  	         $('#lbl_valor_desc_comarca').html("____________________");
		  	     else
		  	         $('#lbl_valor_desc_comarca').html("<span class='data'>" + $('#desc_comarca').val() + "</span>");
		  	 }
		  	 function setTermino(){
		  	     // código
		  	     if($('#termino').val() == "")
		  	         $('#lbl_valor_termino').html("__");
		  	     else
		  	         $('#lbl_valor_termino').html("<span class='data'>" + $('#termino').val() + "</span>");
		  	     // descripción
		  	     if($('#desc_termino').val() == "")
		  	         $('#lbl_valor_desc_termino').html("____________________");
		  	     else
		  	         $('#lbl_valor_desc_termino').html("<span class='data'>" + $('#desc_termino').val() + "</span>");  
		  	 }
		  	 function setSubtermino(){
                 // código
		  	     if($('#subtermino').val() == "")
		  	         $('#lbl_valor_subtermino').html("__");
		  	     else if($('#subtermino').val() == " ")
		  	         $('#lbl_valor_subtermino').html(" ");
		  	     else
		  	         $('#lbl_valor_subtermino').html("<span class='data'>" + $('#subtermino').val() + "</span>");
                 
		  	 }
		  	 function setSIGPAC(){
		  	     // provSig
		  	     if($('#provSig').val() == "")
		  	         $('#lbl_valor_provSig').html("__");
		  	     else
		  	         $('#lbl_valor_provSig').html("<span class='data'>" + $('#provSig').val() + "</span>");
		  	         
		  	     // TermSig
		  	     if($('#TermSig').val() == "")
		  	         $('#lbl_valor_TermSig').html("__");
		  	     else
		  	         $('#lbl_valor_TermSig').html("<span class='data'>" + $('#TermSig').val() + "</span>");
		  	         
		  	     // agrSig
		  	     if($('#agrSig').val() == "")
		  	         $('#lbl_valor_agrSig').html("__");
		  	     else
		  	         $('#lbl_valor_agrSig').html("<span class='data'>" + $('#agrSig').val() + "</span>");
		  	         
		  	     // zonaSig
		  	     if($('#zonaSig').val() == "")
		  	         $('#lbl_valor_zonaSig').html("__");
		  	     else
		  	         $('#lbl_valor_zonaSig').html("<span class='data'>" + $('#zonaSig').val() + "</span>");
		  	         
		  	     // polSig
		  	     if($('#polSig').val() == "")
		  	         $('#lbl_valor_polSig').html("__");
		  	     else
		  	         $('#lbl_valor_polSig').html("<span class='data'>" + $('#polSig').val() + "</span>");
		  	         
		  	     // parcSig
		  	     if($('#parcSig').val() == "")
		  	         $('#lbl_valor_parcSig').html("__");
		  	     else
		  	         $('#lbl_valor_parcSig').html("<span class='data'>" + $('#parcSig').val() + "</span>");
		  	         
		  	     // recSig
		  	     if($('#recSig').val() == "")
		  	         $('#lbl_valor_recSig').html("__");
		  	     else
		  	         $('#lbl_valor_recSig').html("<span class='data'>" + $('#recSig').val() + "</span>"); 
		  	 }
		  	 function setPoligonoParcela(){
		  	     // poligono
		  	     if($('#poligono').val() == "")
		  	         $('#lbl_valor_poligono').html("__");
		  	     else
		  	         $('#lbl_valor_poligono').html("<span class='data'>" + $('#poligono').val() + "</span>"); 
		  	     // parcela
		  	     if($('#parcela').val() == "")
		  	         $('#lbl_valor_parcela').html("__");
		  	     else
		  	         $('#lbl_valor_parcela').html("<span class='data'>" + $('#parcela').val() + "</span>");  
		  	 }
		  	 function setNombre(){
		  	     if($('#nombre').val() == "")
		  	         $('#lbl_nombre').html("______________________________________"); 
		  	     else
		  	         $('#lbl_nombre').html("<span class='data'>" + $('#nombre').val() + "</span>"); 
		  	     
		  	 }
		  	 function setCultivo(){
		  	     // código
		  	     if($('#cultivo').val() == "")
		  	         $('#lbl_valor_cultivo').html("__");
		  	     else
		  	         $('#lbl_valor_cultivo').html("<span class='data'>" + $('#cultivo').val() + "</span>");
		  	     // descripción
		  	     if($('#desc_cultivo').val() == "")
		  	         $('#lbl_valor_desc_cultivo').html("____________________");
		  	     else
		  	         $('#lbl_valor_desc_cultivo').html("<span class='data'>" + $('#desc_cultivo').val() + "</span>"); 
		  	     
		  	 }
		  	 function setVariedad(){
		  	     // código
		  	     if($('#variedad').val() == "")
		  	         $('#lbl_valor_variedad').html("__");
		  	     else
		  	         $('#lbl_valor_variedad').html("<span class='data'>" + $('#variedad').val() + "</span>");
		  	     // descripción
		  	     if($('#desc_variedad').val() == "")
		  	         $('#lbl_valor_desc_variedad').html("____________________");
		  	     else
		  	         $('#lbl_valor_desc_variedad').html("<span class='data'>" + $('#desc_variedad').val() + "</span>"); 
		  	 }
		  	 function setDatosPoliza(){
				 // plan
				 if($('#codplan').val() == "")
		  	         $('#lbl_valor_plan').html("__");
		  	     else
		  	         $('#lbl_valor_plan').html("<span class='data'>" + $('#codplan').val() + "</span>");
		  	         
		  	         
				 // linea
				 if($('#codlinea').val() == "")
		  	         $('#lbl_valor_linea').html("__");
		  	     else
		  	         $('#lbl_valor_linea').html("<span class='data'>" + $('#codlinea').val() + "</span>");
		  	         
		  	       
				 // nif
				 if($('#nifCif_cm').val() == "")
		  	         $('#lbl_valor_nif').html("__");
		  	     else
		  	         $('#lbl_valor_nif').html("<span class='data'>" + $('#nifCif_cm').val() + "</span>");
		  	         
		  	     /*    
				 // modulo
				 if($('#variedad').val() == "")
		  	         $('#lbl_valor_modulo').html("__");
		  	     else
		  	         $('#lbl_valor_modulo').html("<span class='data'>" + $('#variedad').val() + "</span>");
		  	     */
		  	 }

		  	 // validate popup cambio masivo, la validación se ejecuta al hacer el submit
		  	 function setValidate(){
				 	$('#frmCambioMasivo').validate({
				 		 errorLabelContainer: "#panelAlertasValidacion_cm",
		   				 wrapper: "li",
		   				 rules: {
		   				     "variedad_cm" : {siHayCultivoVariedadObligatorio: true,digits: true},
		   				     "cultivo_cm" : {sihayVariedadCultivoObligatorio: true, digits: true},
		   				     "provincia_cm" :{digits: true},
		   				     "comarca_cm" :{siHayComarcaProvinciaObligatorio: true,digits: true},
		   				     "termino_cm" :{siHayTerminoComYProvObligatorio: true,digits: true},
		   				     "subtermino_cm" :{siHaySubterminoTermYComYProvObligatorio: true},
							 "inc_ha_cm" : {unaSolaProduccion: true, digits: true},
		   				     "inc_parcela_cm" : {digits: true},
		   				  	 "inc_unidades_cm" :{digits: true},
		   				  	 "provSig_cm": {number: true},
		   				  	 "termSig_cm": {number: true},
		   				     "agrSig_cm": {number: true},
		   				     "zonaSig_cm": {number: true},
		   				     "polSig_cm": {number: true},
		   				     "parcSig_cm": {number: true},
		   				     "recSig_cm": {number: true},
							 "superficie_cm" : {number: true},
							 "tplantacion_cm":{number:true},
		   				     "fechaSiembra_cm" :{dateITA: true},
		   				     "fechaFinGarantia_cm" :{dateITA: true},
		   				     "codtipomarcoplantac_cm" : {number: true},
		   				     "codpracticacultural_cm" : {digits: true},
		   				     "sistemaCultivo_cm" : {digits: true},
		   				     "destino_cm" : {digits: true},
		   				  	 "unidades_cm" : {digits: true},
		   				  	 "precio_cm" : {number: true,valorDecimal4:true},
		   				     "edad_cm": {digits: true,unaSolaEdad: true},
						     "incEdad_cm" : {digits: true},
						     "sistemaProduccion_cm": {digits: true}
		   				 },
						 messages: {
						     "variedad_cm" : {siHayCultivoVariedadObligatorio: "Si introduce cultivo la variedad es obligatoria.",
						     				  digits: "El campo Variedad sólo puede contener dígitos."},
						     "cultivo_cm" : { sihayVariedadCultivoObligatorio: "Si introduce variedad debe introducir cultivo.", 
						                      digits: "El campo Cultivo sólo puede contener dígitos."},
		                     "provincia_cm" :{digits: "El campo Provincia sólo puede contener dígitos."},
			   				 "comarca_cm" :{siHayComarcaProvinciaObligatorio: "Si introduce comarca, la provincia es obligatoria.",
			   					 				digits: "El campo Comarca sólo puede contener dígitos."},
			   				 "termino_cm" :{siHayTerminoComYProvObligatorio: "Si introduce termino, la provincia y la comarca son obligatorias.",
			   					 				digits: "El campo Termino sólo puede contener dígitos."},
			   				 "subtermino_cm" :{siHaySubterminoTermYComYProvObligatorio: "Si introduce subtérmino, la provincia, la comarca y el término son obligatorios."},                 
						     "inc_ha_cm" : {  unaSolaProduccion: "No se puede introducir más de una producción.", 
						                      digits: "El campo Incremento Kg./Ha  sólo puede contener dígitos."},
						     "inc_parcela_cm" : {digits: "El campo Incremento Kg./Parcela  sólo puede contener dígitos."},
						     "inc_unidades_cm" :{digits: "El campo Incremento Kg./Unidades  sólo puede contener dígitos."},
						     "provSig_cm":  {number: "El campo prov (SIGPAC) sólo puede contener dígitos."},
		   				  	 "termSig_cm": {number: "El campo term (SIGPAC) sólo puede contener dígitos."},
		   				     "agrSig_cm":  {number: "El campo agr (SIGPAC) sólo puede contener dígitos."},
		   				     "zonaSig_cm":  {number: "El campo zona (SIGPAC) sólo puede contener dígitos."},
		   				     "polSig_cm":  {number: "El campo pol (SIGPAC) sólo puede contener dígitos."},
		   				     "parcSig_cm":  {number: "El campo par (SIGPAC) sólo puede contener dígitos."},
		   				     "recSig_cm": {number: "El campo rec (SIGPAC) sólo puede contener dígitos."},
						     "superficie_cm" : {number: "El campo superficie sólo puede contener dígitos."},
						     "codtipomarcoplantac_cm" : {number: "El campo Tipo Marco plantación sólo puede contener dígitos."},
						     "fechaSiembra_cm" :{dateITA:"El formato del campo Fecha siembra es dd/mm/YYYY"},
						     "fechaFinGarantia_cm" :{dateITA:"El formato del campo fecha fin garantías es dd/mm/YYYY"},
						     "tplantacion_cm" : {number: "El campo tipo plantación sólo puede contener dígitos."},
						     "codpracticacultural_cm" : {digits: "El campo práctica cultural sólo puede contener dígitos."},
						     "sistemaCultivo_cm" : {digits: "El campo sitema cultivo sólo puede contener dígitos."},
						     "destino_cm" : {digits: "El campo destino sólo puede contener dígitos."},
						     "unidades_cm" : {digits: "El campo unidades sólo puede contener dígitos."},
						     "precio_cm" :{number: "El campo precio sólo puede contener dígitos.",
						    	 valorDecimal4:"El campo precio debe ser un valor numérico de hasta cuatro decimales."},
						     "edad_cm": {digits: "El campo edad sólo puede contener dígitos.",unaSolaEdad:"No se puede introducir más de una edad."},
						     "incEdad_cm" : {digits: "El campo incremento edad sólo puede contener dígitos."},
						     "sistemaProduccion_cm": {digits: "El campo sistema de producción sólo puede contener dígitos."}
						 }
				 	});
				 	//"produccion_cm" : {digits: "El campo Producción sólo puede contener dígitos.", 
                    //  maxlength: "El campo Producción debe contener 9 dígitos."},
                    
				 	// función que valida cultivo - variedad
				 	jQuery.validator.addMethod("siHayCultivoVariedadObligatorio", function(value, element, params) { 
				 	    if($('#cultivo_cm').val() != "" && $('#variedad_cm').val() == "")
						    return false;
						else
						    return true;
				    });
				    
				    jQuery.validator.addMethod("sihayVariedadCultivoObligatorio", function(value, element, params) { 
						if($('#cultivo_cm').val() == "" && $('#variedad_cm').val() != "")
						    return false;
						else
						   return true;
						
				    });
				    jQuery.validator.addMethod("siHayComarcaProvinciaObligatorio", function(value, element, params) { 
						if($('#provincia_cm').val() == "" && $('#comarca_cm').val() != "")
						    return false;
						else
						   return true;
						
				    });
				    jQuery.validator.addMethod("siHayTerminoComYProvObligatorio", function(value, element, params) { 
						if( ($('#comarca_cm').val() == ""||$('#provincia_cm').val()=="") && $('#termino_cm').val() != "")
						    return false;
						else
						   return true;
						
				    });
				    jQuery.validator.addMethod("siHaySubterminoTermYComYProvObligatorio", function(value, element, params) { 
						if(($('#comarca_cm').val() == ""||$('#provincia_cm').val()=="" || $('#termino_cm').val() == "")
								&& $('#subtermino_cm').val() != "")
						    return false;
						else
						   return true;
						
				    });
				    jQuery.validator.addMethod("valorDecimal4", function(value, element, params) { 
				    	
				    	if ($('#precio_cm').val() != ''){
				    		return /^\d+([.]\d{1,4})?$/.test(value);
				    	}else{
				    		return true;
				    	}
				    	
				    	
				    });
				    
				    
					 // función que valida los incrementos
				 	jQuery.validator.addMethod("unaSolaEdad", function(value, element, params) { 
				 	    var result = true;
				 	    var count  = 0;

						// si más de uno relleno
						if($('#edad_cm').val() != ""){ count++; }
						if($('#incEdad_cm').val()      != ""){ count++; } 
						if(count > 1){ 
							result = false;
						}
						return result;
				    });	
                    
				 // función que valida que solo se haya metido o edad o Incremento edad 
				    jQuery.validator.addMethod("unaSolaProduccion", function(value, element, params) { 
				 	    var result = true;
				 	    var count  = 0;

						// si más de uno relleno
						if($('#inc_parcela_cm').val() != ""){ count++; }
						if($('#inc_ha_cm').val()      != ""){ count++; } 
						if($('#inc_unidades_cm').val()  != ""){ count++; }
						
						if(count > 1){ 
							result = false;
						}

						return result;
				    });	
		     }
		     
		    
		     
		     // -- CHECKS FUNCTIONS --
		  	 function numero_check_seleccionados(){	
		  	    var n_checks = 0; 			
				$("input[type=checkbox]").each(function() { 				        
			        if($(this).attr('id').indexOf('checkParcela_')!= -1){
			        	if($(this).attr('checked')){	        		        		
			        		n_checks = n_checks +1;
			        	}	        	
			        }      
			    });
			    return n_checks;
		     }
		     function seleccionar_checks(value){
				 $("input[type=checkbox]").each(function() { 				        
				        if($(this).attr('id').indexOf('checkParcela_')!= -1){
				        
				            // checking solo los que no estan deshabilitados
				            if(!($(this).is(':disabled'))){		        	
				        		$(this).attr('checked',value);
				        	}	
				        }      
				  });  
		      }
		      function selCheckTodos(){
			    if($("#selTodos").is(':checked'))    
                    seleccionar_checks(true);  
                else    
                    seleccionar_checks(false);     
			 }
			 // si hay alguno chekeado
	         function isCheck(){
		          var result = false;
		          $("input[type='checkbox'][checked]").each(  function() {   
		              result = true; 
		           });
	
		          return result;
	         }
			 function disabledChecks(value){
			     $("input[type=checkbox]").each(function() { 				        
				        if($(this).attr('id').indexOf('checkParcela_')!= -1){		        	
				        	$(this).attr('disabled',value);
				        }      
				  });
				  
				  
				  $("#selTodos").attr('disabled',value);
			 }
		



--></script>



<form id="frmCambioMasivo" name="frmCambioMasivo" action="cambioMasivo.html" method="post" >
	<input type="hidden" name="fechaSiembra_cm.day" value="">
	<input type="hidden" name="fechaSiembra_cm.month" value="">
	<input type="hidden" name="fechaSiembra_cm.year" value="">
	<input type="hidden" name="fechaFinGarantia_cm.day" value="">
	<input type="hidden" name="fechaFinGarantia_cm.month" value="">
	<input type="hidden" name="fechaFinGarantia_cm.year" value="">
	
	<!-- mantener checks en paginacion -->
	<input type="hidden" name="idsRowsCheckedCM"           id="idsRowsChecked"             />
    <input type="hidden" name="parcelasString"             id="parcelasString"             value="${parcelasString}"/>
    <input type="hidden" name="marcarTodosChecks"          id="marcarTodosChecks"          value="${marcarTodosChecks}"/>
    <input type="hidden" name="isClickInListado"           id="isClickInListado" />
    <input type="hidden" name="tipoListadoGridCM"          id="tipoListadoGridCM" />
    <input type="hidden" name="method"           		   id="method" />
	<!-- cambio masivo -->
	<input type="hidden" name="idpolizaCM"                 id="idpolizaCM" />
	<input type="hidden" name="produccion_form_cm"         id="produccion_form_cm"         value=""/>
  	<input type="hidden" name="checked_form_parcela_cm"    id="checked_form_parcela_cm"    value=""/>
  	<input type="hidden" name="recalcular"    			   id="recalcular"    value=""/>
  	
	  	<!-- ubicacion -->
	  	<input type="hidden" name="provincia_form_cm"       id="provincia_form_cm"         value=""/>
	  	<input type="hidden" name="comarca_form_cm"         id="comarca_form_cm"           value=""/>
	  	<input type="hidden" name="termino_form_cm"         id="termino_form_cm"           value=""/>
	  	<input type="hidden" name="subtermino_form_cm"      id="subtermino_form_cm"        value=""/>
	  	<!-- sigpac  -->
		<input type="hidden" name="provSig_form_cm"           id="provSig_form_cm"           value=""/>
		<input type="hidden" name="termSig_form_cm"           id="termSig_form_cm"           value=""/>
		<input type="hidden" name="agrSig_form_cm"            id="agrSig_form_cm"            value=""/>
		<input type="hidden" name="zonaSig_form_cm"           id="zonaSig_form_cm"           value=""/>
		<input type="hidden" name="polSig_form_cm"            id="polSig_form_cm"            value=""/>
		<input type="hidden" name="parcSig_form_cm"           id="parcSig_form_cm"           value=""/>
		<input type="hidden" name="recSig_form_cm"            id="recSig_form_cm"            value=""/>
	  	<!-- cultivo y variedad -->
	  	<input type="hidden" name="variedad_form_cm"           id="variedad_form_cm"           value=""/>
	  	<input type="hidden" name="cultivo_form_cm"            id="cultivo_form_cm"            value=""/>
	  	<!-- produccion,superficie y precio -->
	  	<input type="hidden" name="incremento_form_ha_cm"      id="incremento_form_ha_cm"        value=""/>
	  	<input type="hidden" name="incremento_form_parcela_cm" id="incremento_form_parcela_cm"   value=""/>
	  	<input type="hidden" name="incremento_form_unidades_cm" id="incremento_form_unidades_cm" value=""/>
	    <input type="hidden" name="superficie_form_cm" 		   id="superficie_form_cm" 	         value=""/>
	    <input type="hidden" name="precio_form_cm" 		       id="precio_form_cm" 	             value=""/>
	    <!-- datos variables -->
	    <input type="hidden" name="destino_form_cm"            id="destino_form_cm"        value=""/>
	    <input type="hidden" name="tipoPlant_form_cm"          id="tipoPlant_form_cm"      value=""/>
	    <input type="hidden" name="sistemaCultivo_form_cm"     id="sistemaCultivo_form_cm" value=""/>
	    <input type="hidden" name="sistemaProduccion_form_cm"  id="sistemaProduccion_form_cm" value=""/>
	    <input type="hidden" name="marcoPlant_form_cm"         id="marcoPlant_form_cm"     value=""/>
	    <input type="hidden" name="practicaCul_form_cm"        id="practicaCul_form_cm"    value=""/>
	    <input type="hidden" name="fechaSiembra_form_cm"       id="fechaSiembra_form_cm"   value=""/>
	    <input type="hidden" name="fechaFinGarantia_form_cm"   id="fechaFinGarantia_form_cm"  value=""/>
	    <input type="hidden" name="edad_form_cm"   			   id="edad_form_cm"       	   value=""/>
	    <input type="hidden" name="incremento_edad_form_cm"    id="incremento_edad_form_cm" value=""/>
	    <input type="hidden" name="unidades_form_cm"   		   id="unidades_form_cm"       value=""/>

<div id="panelCambioMasivo" class="panelCambioMasivo" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">



     <!--  header popup -->
	<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
		<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			  <span onclick="cerrarPopUpCambioMasivo()">x</span>
		</a>
	</div>
	
	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
			<fieldset style="border:0px">
			    <div id="panelAlertasValidacion_cm" name="panelAlertasValidacion_cm" class="errorForm_cm"></div>
			</fieldset>
		 
			<!-- filtro -->
			<fieldset>
			<legend class="literal">Filtro parcelas aplicado</legend>	
				<fieldset style="border:0px">
					<fieldset style="border:0px">
				       <table align="center" style="width: 20%;">
							<tr>
								<td class="literal">Plan</td>
								<td class="literal">
									<label id="lbl_valor_plan" name="lbl_valor_plan" size="2"  class="dato">___</label>	
								</td>
								<td class="literal">Linea</td>
								<td class="literal">
									<label type="text"  id="lbl_valor_linea" name="lbl_valor_linea" size="2" maxlength="2" class="dato">___</label>
								</td>
								<td class="literal">NIF/NIE</td>
								<td class="literal">
									<label id="lbl_valor_nif" name="lbl_valor_nif" size="3" maxlength="3" class="dato">_________</label>												
								</td>
								<!--  
								<td class="literal">Módulo</td>
								<td class="literal">
								    <label type="text"  id="lbl_valor_modulo"  name="lbl_valor_modulo" size="1"  class="dato">__</label>	
								</td>
								-->
							</tr>
						</table>   
				    </fieldset>
					
					<fieldset>
					<legend class="literal">Ubicación</legend>				
						<table align="center">
							<tr>
								<td class="literal">Provincia</td>
								<td class="literal">
									<label id="lbl_valor_provincia" name="provincia" size="2"  class="dato">__</label>	
									<label class="dato"	id="lbl_valor_desc_provincia" name="lbl_valor_desc_provincia" size="20">____________________</label>
								</td>
								<td class="literal">Comarca</td>
								<td class="literal">
									<label type="text"  id="lbl_valor_comarca" name="lbl_valor_comarca" size="2" maxlength="2" class="dato">__</label>
									<label class="dato"	id="lbl_valor_desc_comarca" name="lbl_valor_desc_comarca" size="20">____________________</label> 
								</td>
								<td class="literal">Término</td>
								<td class="literal">
									<label id="lbl_valor_termino" name="lbl_valor_termino" size="3" maxlength="3" class="dato">___</label>	
									<label id="lbl_valor_desc_termino" name="lbl_valor_desc_termino" size="30" class="dato">______________________________</label>												
								</td>
								<td class="literal">Subtérmino</td>
								<td class="literal">
								    <label type="text"  id="lbl_valor_subtermino"  name="lbl_valor_subtermino" size="1"  class="dato">__</label>	
								</td>
							</tr>
						</table>
					</fieldset>
					            
					<fieldset style="width: 20%;float:left">
					<legend class="literal">Id. Catastral</legend>
					    <table align="center">												
							<tr>
								<td class="literal">Polígono</td>
								<td class="literal">
								    <label name="lbl_valor_poligono" id="lbl_valor_poligono" size="4" maxlength="4" class="dato">____</label>	
								</td>									
								<td class="literal">Parcela</td>
								<td class="literal">
								    <label name="lbl_valor_parcela" id="lbl_valor_parcela" size="4" maxlength="4" class="dato">____</label>	
								</td>
							</tr>
						</table>
					</fieldset>
							    
					<fieldset>
					<legend class="literal">SIGPAC</legend>
						<table align="center">
							<tr>
								<td class="literal">Prov</td>
								<td class="literal"><label name="lbl_valor_provSig" id="lbl_valor_provSig" size="2" maxlength="2" class="dato">__</label></td>
								<td class="literal">Term</td>
								<td class="literal"><label name="lbl_valor_TermSig"  id="lbl_valor_TermSig" size="3" maxlength="3" class="dato">___</label></td>
								<td class="literal">Agr</td>
								<td class="literal"><label name="lbl_valor_agrSig"  id="lbl_valor_agrSig" size="3" maxlength="3" class="dato">___</label></td>
								<td class="literal">Zona</td>
								<td class="literal"><label name="lbl_valor_zonaSig"  id="lbl_valor_zonaSig" size="2" maxlength="2" class="dato">__</label></td>
								<td class="literal">Pol</td>
								<td class="literal"><label name="lbl_valor_polSig"  id="lbl_valor_polSig" size="3" maxlength="3" class="dato">___</label></td>									
								<td class="literal">Parc</td>
								<td class="literal"><label name="lbl_valor_parcSig"  id="lbl_valor_parcSig" size="5" maxlength="5" class="dato">_____</label></td>
								<td class="literal">Rec</td>
								<td class="literal"><label name="lbl_valor_recSig"  id="lbl_valor_recSig" size="5" maxlength="5" class="dato">_____</label></td>
							</tr>
						</table>
					</fieldset>
						<table align="center">
							<tr>
								<td class="literal">Nombre Parcela</td>
								<td class="literal">
									<label id="lbl_nombre" name="lbl_nombre"  size="40"  class="dato">______________________________________</label>
								</td>
								<td class="literal">Cultivo</td>
								<td class="literal">
									<label id="lbl_valor_cultivo" name="lbl_valor_cultivo" size="3" maxlength="3" class="dato">__</label>
									<label id="lbl_valor_desc_cultivo" name="lbl_valor_desc_cultivo" size="25" class="dato">________________________</label>
								</td>
								<td class="literal">Variedad</td>
								<td class="literal">
								    <label id="lbl_valor_variedad" name="lbl_valor_variedad" size="3" maxlength="3" class="dato">__</label>	
									<label id="lbl_valor_desc_variedad" name="lbl_valor_desc_variedad" size="25" class="dato">________________________</label> 		
								</td>
							</tr>
					    </table>    
					</fieldset>
				</fieldset>
					    
			    <!-- campos -->
				<fieldset style="margin-top:5px; border:1px solid #4682B4; padding: 3px;">
					<legend class=literalCM >Campos modificables</legend>		
				    	<fieldset style="border:1px solid #4682B4; padding: 3px">
				       		<legend  class="literal" style="color: #4682B4;">Datos identificativos</legend>
				    			<fieldset>
				    				<legend class="literalCM">Ubicación</legend>
				    				 	<table>
					    				 	<tr>
												<td class=labelAseg style="COLOR: #4682b4">Provincia</td>
												<td>
													<input  id="provincia_cm" name= "provincia_cm"  class="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia_cm','comarca_cm','desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_provincia_cm" name="desc_provincia_cm" size="20" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ProvinciaCM','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Comarca</td>
												<td>
													<input  id="comarca_cm" name="comarca_cm" class="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_comarca_cm" name="desc_comarca"_cm size="19" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ComarcaCM','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Término</td>
												<td>
													<input  id="termino_cm" name ="termino_cm" class="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_termino_cm" name="desc_termino_cm" size="29" readonly="readonly"/>
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Subtérmino</td>
												<td>
													<input  id="subtermino_cm" name ="subtermino_cm" class="dato" size="1" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoCM','principio', '', '');"	alt="Buscar Término" title="Buscar Término" />
													
												</td>
											</tr>
										</table>
								</fieldset>
								
								<fieldset style="margin-top:3px;margin-left:3px;margin-right:3px">
				    				<legend  class="literal" style="color: #4682B4;">SIGPAC</legend>
				    				 	<table  align="center">
					    				 	<tr>
											<td class=labelAseg style="COLOR: #4682b4">Prov</td>
											<td>
												<input  id="provSig_cm" name="provSig_cm" class="dato" size="2" maxlength="2"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Term</td>
											<td >
												<input  id="termSig_cm" name="termSig_cm" class="dato" size="3" maxlength="3"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Agr</td>
											<td >
												<input  id="agrSig_cm" name="agrSig_cm" class="dato" size="3" maxlength="3"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Zona</td>
											<td >
												<input  id="zonaSig_cm" name="zonaSig_cm" class="dato" size="2" maxlength="2"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Pol</td>
											<td >
												<input  id="polSig_cm" name="polSig_cm" class="dato" size="3" maxlength="3"/>
											</td>									
											<td class=labelAseg style="COLOR: #4682b4">Parc</td>
											<td >
												<input  id="parcSig_cm" name="parcSig_cm" class="dato" size="5" maxlength="5"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Rec</td>
											<td >
												<input  id="recSig_cm" name="recSig_cm" class="dato" size="5" maxlength="5"/>
											</td>
										</tr>
										</table>
								</fieldset>
								
								<table style="width: 70%;" align="center">
									<tr>
											<td class=labelAseg style="COLOR: #4682b4" >Cultivo</td>
											<td >
												<input type="text" id="cultivo_cm" name="cultivo_cm" size="3" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_cultivo_cm','variedad_cm','desc_variedad_cm');"/>
												<input class="dato"	id="desc_cultivo_cm" name="desc_cultivo_cm" size="20" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoCM','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
												
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Variedad</td>
											<td >
											    <input type="text"  id="variedad_cm" name="variedad_cm" size="3" maxlength="3" class="dato" onchange="javascript:lupas.limpiarCampos('desc_variedad_cm');"/>	
												<input class="dato"	id="desc_variedad_cm" name="desc_variedad_cm" size="20" readonly="readonly"/> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VariedadCM','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />
											</td>
									</tr>
								</table> 
						</fieldset>
						<fieldset style="border: #4682b4 1px solid;margin-top:3px;margin-left:3px;margin-right:3px;margin-bottom:3px;">
				    		<legend class=labelAseg style="COLOR: #4682b4">Capitales asegurados</legend>
				    			<table align="center" style="width: 100%">
				    				<tr>
									    <td class=labelAseg style="COLOR: #4682b4" >Producción: </td>
									    <td class=labelAseg style="COLOR: #4682b4" > Kg./Ha</td>
										<td>
											<input type="text" id="inc_ha_cm" name="inc_ha_cm"  size="9" maxlength="9"  class="dato"/>
										</td>
										<td class=labelAseg style="COLOR: #4682b4" >Kg./Parcela</td>
										<td>
											<input type="text" id="inc_parcela_cm" name="inc_parcela_cm"  size="9" maxlength="9" class="dato"/>
										</td>
										<td class=labelAseg style="COLOR: #4682b4" >Kg./Unidades</td>
										<td>
											<input type="text" id="inc_unidades_cm" name="inc_unidades_cm"  size="9" maxlength="9" class="dato"/>
										</td>
										
										<td class=labelAseg style="COLOR: #4682b4">Superficie(hectáreas): </td>
										<td>
											<input type="text" id="superficie_cm" name="superficie_cm"  size="8" maxlength="8"  class="dato"/>
										</td>
										<td width="50">&nbsp;</td>
										<td class=labelAseg style="COLOR: #4682b4" >Precio:</td>
										<td>
											<input  id="precio_cm" name="precio_cm" class="dato" size="8" maxlength="15"/>
										</td>
									<tr>
							</table>
						
							<fieldset style="margin-top:3px;margin-left:3px;margin-right:3px;margin-bottom:3px;">
							<LEGEND class=literal style="COLOR: #4682b4">Datos variables</LEGEND>
								<legend style="WIDTH: 100%" align=center>
									<table>
										<TR>
											<td class=labelAseg style="COLOR: #4682b4">Destino: </td>
											<td noWrap>
												<input type="text" id="destino_cm" name="destino_cm""  size="3" maxlength="3"  class="dato" onchange="javascript:lupas.limpiarCampos('desc_destino_cm');"/>
												<input class="dato"	id="desc_destino_cm" name="desc_destino_cm" size="20" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('DestinoCM','principio', '', '');"	alt="Buscar Destino" title="Buscar Destino" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4" noWrap>T. plantación:</td>
											<td noWrap>
												<input type="text" id="tplantacion_cm" name="tplantacion_cm""  size="3" maxlength="3"  class="dato" onchange="javascript:lupas.limpiarCampos('desc_tplantacion_cm');"/>
												<input class="dato"	id="desc_tplantacion_cm" name="desc_tplantacion_cm" size="25" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoPlantacionCM','principio', '', '');"	alt="Buscar Tipo Plantación" title="Buscar Tipo Plantación" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4" noWrap>Sist. Cultivo:</td>
											<td noWrap>
												<input type="text" id="sistemaCultivo_cm" name="sistemaCultivo_cm"  size="3" maxlength="3"  class="dato" onchange="javascript:lupas.limpiarCampos('desc_sistemaCultivo_cm');"/>
												<input class="dato"	id="desc_sistemaCultivo_cm" name="desc_sistemaCultivo_cm" size="20" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivoCM','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
											 </td>
										 </TR>
									</table>
									<table style="WIDTH: 100%" align="center">
										<tr>
											<td class=labelAseg style="COLOR: #4682b4">T. Marco Plantación:</SPAN> 
											<td noWrap>
												<input  id="codtipomarcoplantac_cm" name= "codtipomarcoplantac_cm"  class="dato" size="1" maxlength="2" onchange="javascript:lupas.limpiarCampos('destipomarcoplantac_cm');"/>
												<input class="dato"	id="destipomarcoplantac_cm" name="destipomarcoplantac_cm" size="19" readonly="readonly"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('MarcoPlantacionCM','principio', '', '');"	alt="Buscar Tipo Marco Plantación" title="Buscar Tipo Marco Plantación" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Práct. Cultural:</SPAN> 
											<td noWrap>
												<input  id="codpracticacultural_cm" name= "codpracticacultural_cm"  class="dato" size="1" maxlength="2" onchange="javascript:lupas.limpiarCampos('despracticacultural_cm');"/>
												<input class="dato"	id="despracticacultural_cm" name="despracticacultural_cm" size="23" readonly="readonly"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('PracticaCulturalCM','principio', '', '');"	alt="Buscar Práctica Cultural" title="Buscar Práctica Cultural" />
											</td>
											
											
											<td class=labelAseg style="COLOR: #4682b4">Sist. Producción:</SPAN> 
											<td noWrap>
												<input type="text" id="sistemaProduccion_cm" name="sistemaProduccion_cm"  size="2" maxlength="3"  class="dato" onchange="javascript:lupas.limpiarCampos('desc_sistemaProduccion_cm');"/>
												<input class="dato"	id="desc_sistemaProduccion_cm" name="desc_sistemaProduccion_cm" size="20" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaSistemaProduccionCM_IN','principio', '', '');"	alt="Buscar Sistema de Producción" title="Buscar Sistema de Producción" />
											 </td>
											
										</tr>
									</table>	
									<table style="WIDTH: 85%" align=center>
										<tr class=labelAseg >
										<td class=labelAseg style="COLOR: #4682b4" noWrap>Fecha siembra:</td>
											<td noWrap>
												<input id="fechaSiembra_cm" class=dato maxLength=10 size=11 name="fechaSiembra_cm"> 
												<input id="btn_fechaSiembra_cm" class="miniCalendario" style="CURSOR: pointer" type=button name="btn_fechaSiembra_cm"> 
											</td><!-- </TR></TBODY></TABLE> -->
											<td class=labelAseg style="COLOR: #4682b4" noWrap>Fecha Fin Garantías:</td>
											<td noWrap>
												<input type="text" name="fechaFinGarantia_cm" id="fechaFinGarantia_cm" size="11" maxlength="10" class="dato"/>
												<input type="button" id="btn_fechaFinGarantia_cm" name="btn_fechaFinGarantia_cm" class="miniCalendario" style="cursor: pointer;" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Edad:</td>
											<td>
												<input  id="edad_cm" name="edad_cm" class="dato" size="3" maxLength="3"/> </td>
											<td class=labelAseg style="COLOR: #4682b4">Incremento edad:</td>
											<td>
												<input  id="incEdad_cm" name="incEdad_cm" class="dato" size="3"  maxLength="3"/> </td>
											<td class=labelAseg style="COLOR: #4682b4">Unidades:</td>
											<td>
												<input  id="unidades_cm" name="unidades_cm" class="dato" size="3" maxLength="8" /> 
											</td>
										</tr>
									</table>
							</fieldset>
					</fieldset>
				</fieldset>
	
	</div>
		<div style="margin-top:15px">
				    <a class="bot" href="javascript:limpiarPopUpCambioMasivo()" title="Limpiar">Limpiar</a>
				    <a class="bot" href="javascript:cerrarPopUpCambioMasivo()" title="Cancelar">Cancelar</a>
				    <a class="bot" href="javascript:aplicarCambioMasivo()" title="Aplicar">Aplicar</a>
		</div>
	</div>
</div>
<%@ include file="/jsp/common/lupas/lupaCultivoCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVariedadCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaDestinoCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTipoPlantacionCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaSistemaCultivoCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvinciaCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTerminoCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaComarcaCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaMarcoPlantacionCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaPracticaCulturalCM.jsp"%>
<%@ include file="/jsp/common/lupas/lupaSistemaProduccionCM.jsp"%>									
<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
</form>


<!--                             -->		
<!-- PANEL AVISOS (REUTILIZABLE) -->
<!--                             -->
<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
     <!--  header popup -->
	 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
	            Aviso
	        </div>
	        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
	                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
	            <span onclick="hidePopUpPanelAvisos()">x</span>
	        </a>
	 </div>
	 <!--  body popup -->
	 <div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_aviso">sin mensaje.</div>
			</div>
			<div style="margin-top:15px">
			    <a class="bot" href="javascript:hidePopUpPanelAvisos()" title="Cancelar">Aceptar</a>
			</div>
	 </div>
</div>






