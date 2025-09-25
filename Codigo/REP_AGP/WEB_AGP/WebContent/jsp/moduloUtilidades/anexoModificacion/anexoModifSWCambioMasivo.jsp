<!--                                                     -->		
<!-- popupCambioMasivo.jsp (show in parcelasAnexoModificacionSW.jsp) -->
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
			        inputField        : "fechaSiembra",
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
                	var input = $('#frmCambioMasivo [name=hayCambiosDatosAsegurado]');
                	input.val($('#main3 [name=hayCambiosDatosAsegurado]').val());
	            	if(coincidenDatosFiltro()){
		  	     		$('#overlayCambioMasivo').show();
				 		$('#panelCambioMasivo').show();
				 		setFiltroPopUpCambioMasivo();
			  		}else{
			      		showPopUpAviso("La ubicación de la(s) parcela(s) debe coincider con la introducida en el filtro.");
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
		  		 if(validateFormCambioMasivo()){
		  			validaCamposLupas()// validamos que los campos de las lupas sean correctos (ajax) y se hace el submit
				 }
		  	 }
		  	 
		  	 function submitMain3FromCambioMasivo(){
		  		if (hayAlgunCampoRelleno()){ 
			  		jConfirm('¿Está seguro de que desea modificar las parcelas seleccionadas y sus instalaciones asociadas?', 
			  				'Diálogo de Confirmación', function(r) {
						if (r){
			  	     	//Si no hay indicado ningún campo de producción y los campos a cambiar son cultivo, variedad, superficie 
			  	     	//se muestra un mensaje de confirmación para realizar el recálculo de producción y precio
				  	    	if (!mostrarPopupRecalcular()){
								continuar("no");
							}else{
								$.unblockUI();
				     	    	$('#overlay').show();
				     	    	$("#popupRecalcular").show();
							}
			  	     	}
			  		});
		  		}
		  		
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
					+'&listCodModulos_cm=' + $('#listCodModulos_cm').val()
					+'&sistProd=' + $('#sistemaProduccion_cm').val(),
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
		  	// Si se modifican datos  que afectan al precio o a la produccion
			// (todos los datos variables menos produccion,precio y tipoPlantacion)
			// se le pregunta al usuario si desea recalcular. 
			function mostrarPopupRecalcular(){
		  		if ( $('#destino_cm').val()!= "" || $('#sistemaCultivo_cm').val() != ""
					  || $('#codtipomarcoplantac_cm').val()!= "" ||$('#codpracticacultural_cm').val()!= ""
					  || $('#fechaSiembra').val() != "" || $('#fechaFinGarantia_cm').val() != ""
					  || $('#edad_cm').val() != "" || $('#incEdad_cm').val()!= "" || $('#unidades_cm').val() != ""
					  || $('#cultivo_cm').val() != '' ||$('#variedad_cm').val() != '' || $('#provincia_cm').val() != ''
					  || $('#comarca_cm').val() != ''|| $('#termino_cm').val() != '' || $('#subtermino_cm').val() != ''
					  || $('#provSig_cm').val() != ''|| $('#termSig_cm').val() != '' || $('#agrSig_cm').val() != ''
					  || $('#zonaSig_cm').val() != ''|| $('#polSig_cm').val() != ''|| $('#parcSig_cm').val() != ''
				 	  || $('#recSig_cm').val() != '' || $('#superficie').val() != '' ||  $('#sistemaProduccion_cm').val() !='')
					  {
		  		
		  			return true && mostrarRecalcular();
		  		 }
		  		return false;
		  	}
		
		  	 function mostrarRecalcular(){
		  		var tienePrecio=$('#precio_cm').val()!='';
		  		var tieneProduccion=$('#increHa').val()!='' || $('#increParcela').val()!='' || $('#inc_unidades_cm').val() != '';
		  		var mostrar=true;
		  		if(tienePrecio && tieneProduccion){
		  			mostrar=false;
			  	}
		  		
		  		return mostrar;
		  		 
		  	 }
		  	
			function continuar(recalcular)
	     	{
				$("#popupRecalcular").hide();
	     	    $('#overlay').hide();
	     	    var frm = document.getElementById("frmCambioMasivo");
	     	    
	     	    frm.recalcularPrecioProd.value = recalcular;
	     	   
	     	    frm.method.value  = "doCambioMasivoSW";
				frm.idPoliza.value = $('#idPoliza').val();
				frm.idsRowsCheckedCM.value  = $('#idsRowsChecked').val();
				frm.tipoListadoGridCM.value = $('#tipoListadoGrid').val();
				frm.idAnexoMod.value =  $('#idAnexoModificacion').val();
				frm.idCupon.value =  $('#idCupon').val();
				frm.idCuponStr.value =  $('#idCuponStr').val();
				frm.vieneDeListadoAnexosMod.value = $('#vieneDeListadoAnexosMod').val();
				$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			    $.blockUI({ 
			        overlayCSS: { backgroundColor: '#525583'},
			        baseZ: 2000
			    });
			    frm.submit();  
	     	}
			 function hayAlgunCampoRelleno(){
				if ( $('#cultivo_cm').val() != '' ||$('#variedad_cm').val() != '' || $('#provincia_cm').val() != ''
						 || $('#comarca_cm').val() != ''|| $('#termino_cm').val() != '' || $('#subtermino_cm').val() != ''
						 || $('#provSig_cm').val() != ''|| $('#termSig_cm').val() != '' || $('#agrSig_cm').val() != ''
						 || $('#zonaSig_cm').val() != ''|| $('#polSig_cm').val() != ''|| $('#parcSig_cm').val() != ''
						 || $('#recSig_cm').val() != '' || $('#fechaFinGarantia_cm').val() != ''|| $('#codtipomarcoplantac_cm').val() != ''
						 || $('#codpracticacultural_cm').val() != '' || $('#unidades_cm').val() != ''|| $('#precio_cm').val() != ''
						 || $('#edad_cm').val() != ''|| $('#incEdad_cm').val() != ''|| $('#inc_unidades_cm').val() != ''
						 || $('#increHa').val()!= ''|| $('#increParcela').val() != ''|| $('#superficie').val() != ''|| $('#fechaSiembra').val() != ''
						 || $('#destino_cm').val() != '' || $('#tplantacion_cm').val() != '' 
						 || $('#sistemaCultivo_cm').val() != ''  || $('#sistemaProduccion_cm').val() != ''){
					
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
		  	     $('#increHa').val("");
		  	     $('#increParcela').val('');
		  	     $('#superficie').val('');
		  	     $('#fechaSiembra').val('');
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
		  	     setHojaNumero();
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
		  	     if($('#txt_provsigpac').val() == "")
		  	         $('#lbl_valor_provSig').html("__");
		  	     else
		  	         $('#lbl_valor_provSig').html("<span class='data'>" + $('#txt_provsigpac').val() + "</span>");
		  	         
		  	     // TermSig
		  	     if($('#txt_termsigpac').val() == "")
		  	         $('#lbl_valor_TermSig').html("__");
		  	     else
		  	         $('#lbl_valor_TermSig').html("<span class='data'>" + $('#txt_termsigpac').val() + "</span>");
		  	         
		  	     // agrSig
		  	     if($('#txt_agrsigpac').val() == "")
		  	         $('#lbl_valor_agrSig').html("__");
		  	     else
		  	         $('#lbl_valor_agrSig').html("<span class='data'>" + $('#txt_agrsigpac').val() + "</span>");
		  	         
		  	     // zonaSig
		  	     if($('#txt_zonasigpac').val() == "")
		  	         $('#lbl_valor_zonaSig').html("__");
		  	     else
		  	         $('#lbl_valor_zonaSig').html("<span class='data'>" + $('#txt_zonasigpac').val() + "</span>");
		  	         
		  	     // polSig
		  	     if($('#txt_polsigpac').val() == "")
		  	         $('#lbl_valor_polSig').html("__");
		  	     else
		  	         $('#lbl_valor_polSig').html("<span class='data'>" + $('#txt_polsigpac').val() + "</span>");
		  	         
		  	     // parcSig
		  	     if($('#txt_parcsigpac').val() == "")
		  	         $('#lbl_valor_parcSig').html("__");
		  	     else
		  	         $('#lbl_valor_parcSig').html("<span class='data'>" + $('#txt_parcsigpac').val() + "</span>");
		  	         
		  	     // recSig
		  	     if($('#txt_recsigpac').val() == "")
		  	         $('#lbl_valor_recSig').html("__");
		  	     else
		  	         $('#lbl_valor_recSig').html("<span class='data'>" + $('#txt_recsigpac').val() + "</span>"); 
		  	 }
		  	 function setHojaNumero(){
		  	     // hoja
		  	     if($('#txt_hoja').val() == "")
		  	         $('#lbl_hoja').html("__");
		  	     else
		  	         $('#lbl_hoja').html("<span class='data'>" + $('#txt_hoja').val() + "</span>"); 
		  	     // numero
		  	     if($('#txt_numero').val() == "")
		  	         $('#lbl_numero').html("__");
		  	     else
		  	         $('#lbl_numero').html("<span class='data'>" + $('#txt_numero').val() + "</span>");  
		  	 }
		  	 function setNombre(){
		  	     if($('#txt_nombreParcela').val() == "")
		  	         $('#lbl_nombre').html("______________________________________"); 
		  	     else
		  	         $('#lbl_nombre').html("<span class='data'>" + $('#txt_nombreParcela').val() + "</span>"); 
		  	     
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
		  	         
		  	 }

		  	 // validate popup cambio masivo, la validación se ejecuta al hacer el submit
		  	 function setValidate(){
		  		
				 	$('#frmCambioMasivo').validate({
				 		 errorLabelContainer: "#panelAlertasValidacion_cm",
		   				 wrapper: "li",
		   				 rules: {	
		   					 "variedad.id.codvariedad" : {siHayCultivoVariedadObligatorio: true,number: true},
		   				     "cultivo.id.codcultivo" : {sihayVariedadCultivoObligatorio: true, number: true},
		   				     "termino_cm.id.codprovincia" :{digits: true},
		   				     "termino_cm.id.codcomarca" :{siHayComarcaProvinciaObligatorio: true,number: true},
		   				     "termino_cm.id.codtermino" :{siHayTerminoComYProvObligatorio: true,number: true},
		   				     "subtermino_cm" :{siHaySubterminoTermYComYProvObligatorio: true},
							 "increHa" : {unaSolaProduccion: true, number: true},
		   				     "increParcela" : {number: true},
		   				  	 "inc_unidades_cm" :{number: true},
		   				  	 "provSig_cm": {number: true},
		   				  	 "termSig_cm": {number: true},
		   				     "agrSig_cm": {number: true},
		   				     "zonaSig_cm": {number: true},
		   				     "polSig_cm": {number: true},
		   				     "parcSig_cm": {number: true},
		   				     "recSig_cm": {number: true},
							 "superficie" : {number: true},
							 "tipoPlantacion.codtipoplantacion":{number:true},
		   				     "fechaSiembra" :{dateITA: true},
		   				     "fechaFinGarantia_cm" :{dateITA: true},
		   				     "codtipomarcoplantac_cm" : {number: true},
		   				     "codpracticacultural_cm" : {number: true},
		   				     "sistemaCultivo.codsistemacultivo" : {number: true},
		   				     "destino.coddestino" : {number: true},
		   				  	 "unidades_cm" : {number: true},
		   				  	 "precio_cm" : {number: true,valorDecimal4: true},
		   				     "edad_cm": {number: true,unaSolaEdad: true},
						     "incEdad_cm" : {number: true},
						     "sistemaProduccion.codsistemaproduccion": {digits: true}
		   					 
		   				},
		   				 messages: {
							 "variedad.id.codvariedad" : {siHayCultivoVariedadObligatorio: "Si introduce cultivo la variedad es obligatoria.",
			     				  number: "El campo Variedad sólo puede contener dígitos."},
			     			 "cultivo.id.codcultivo" : { sihayVariedadCultivoObligatorio: "Si introduce variedad debe introducir cultivo.", 
			                      number: "El campo Cultivo sólo puede contener dígitos."},
			                 "termino_cm.id.codprovincia" :{digits: "El campo Provincia sólo puede contener dígitos."},
			  				 "termino_cm.id.codcomarca" :{siHayComarcaProvinciaObligatorio: "Si introduce comarca, la provincia es obligatoria.",
			  					 				number: "El campo Comarca sólo puede contener dígitos."},
			  				 "termino_cm.id.codtermino" :{siHayTerminoComYProvObligatorio: "Si introduce termino, la provincia y la comarca son obligatorias.",
			  					 				number: "El campo Termino sólo puede contener dígitos."},
			  				 "subtermino_cm" :{siHaySubterminoTermYComYProvObligatorio: "Si introduce subtérmino, la provincia, la comarca y el término son obligatorios."},                 
						     "increHa" : {  unaSolaProduccion: "No se puede introducir más de una producción.", 
						                      number: "El campo Incremento Kg./Ha  sólo puede contener dígitos."},
						     "increParcela" : {number: "El campo Incremento Kg./Parcela sólo puede contener dígitos."},
						     "inc_unidades_cm" :{number: "El campo Incremento Kg./Unidades  sólo puede contener dígitos."},
						     "provSig_cm":  {number: "El campo prov (SIGPAC) sólo puede contener dígitos."},
							 "termSig_cm": {number: "El campo term (SIGPAC) sólo puede contener dígitos."},
							 "agrSig_cm":  {number: "El campo agr (SIGPAC) sólo puede contener dígitos."},
							 "zonaSig_cm":  {number: "El campo zona (SIGPAC) sólo puede contener dígitos."},
							 "polSig_cm":  {number: "El campo pol (SIGPAC) sólo puede contener dígitos."},
							 "parcSig_cm":  {number: "El campo par (SIGPAC) sólo puede contener dígitos."},
							 "recSig_cm": {number: "El campo rec (SIGPAC) sólo puede contener dígitos."},
						     "superficie" : {number: "El campo superficie sólo puede contener dígitos."},
						     "codtipomarcoplantac_cm" : {number: "El campo Tipo Marco plantación sólo puede contener dígitos."},
						     "fechaSiembra" :{dateITA:"El formato del campo Fecha siembra es dd/mm/YYYY"},
						     "fechaFinGarantia_cm" :{dateITA:"El formato del campo fecha fin garantías es dd/mm/YYYY"},
						     "tipoPlantacion.codtipoplantacion" : {number: "El campo tipo plantación sólo puede contener dígitos."},
						     "codpracticacultural_cm" : {number: "El campo práctica cultural sólo puede contener dígitos."},
						     "sistemaCultivo.codsistemacultivo" : {number: "El campo sitema cultivo sólo puede contener dígitos."},
						     "destino.coddestino" : {number: "El campo destino sólo puede contener dígitos."},
						     "unidades_cm" : {number: "El campo unidades sólo puede contener dígitos."},
						     "precio_cm" :{number: "El campo precio sólo puede contener dígitos.",
						    	 valorDecimal4:"El campo precio debe ser un valor numérico de hasta cuatro decimales."},
						     "edad_cm": {number: "El campo edad sólo puede contener dígitos.",unaSolaEdad:"No se puede introducir más de una edad."},
						     "incEdad_cm" : {number: "El campo incremento edad sólo puede contener dígitos."},
						     "sistemaProduccion.codsistemaproduccion": {digits: "El campo sistema de producción sólo puede contener dígitos."}
							 
						}
				 	});
				 	
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
				    
                    // función que valida los incrementos
				 	jQuery.validator.addMethod("unaSolaProduccion", function(value, element, params) { 
				 	    var result = true;
				 	    var count  = 0;

						// si más de uno relleno
						if($('#increParcela').val() != ""){ count++; }
						if($('#increHa').val()      != ""){ count++; } 
						if($('#inc_unidades_cm').val()  != ""){ count++; }
						
						if(count > 1){ 
							result = false;
						}

						return result;
				    });	
		     }
--></script>

<form:form name="frmCambioMasivo" id="frmCambioMasivo" action="anexoModCambioMasivoSW.html" method="post" commandName="anexoModSWCambioMasivo" >
	<input type="hidden" name="fechaSiembra.day" value="">
	<input type="hidden" name="fechaSiembra.month" value="">
	<input type="hidden" name="fechaSiembra.year" value="">
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
    <input type="hidden" name="vieneDeListadoAnexosMod"    id="vieneDeListadoAnexosMod" />
	<!-- bean -->
	<form:hidden path="idPoliza" id="idPoliza" />
	<form:hidden path="idAnexoMod" id="idAnexoMod" />
	<form:hidden path="idCupon" id ="idCupon" />
	<input type="hidden" id="idCuponStr" name ="idCuponStr" />
	
	<!-- cambio pasivo  -->
    <input type="hidden" name="checked_form_parcela_cm"    id="checked_form_parcela_cm"    value=""/>
    
    <input type="hidden" name="recalcularPrecioProd" id ="recalcularPrecioProd"/>
    <input type="hidden" name="recalcularPrecio" id ="recalcularPrecio"/>
	<input type="hidden" name="recalcularSistemaCultivo" 	id ="recalcularSistemaCultivo"/>  
	
	<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
		
			 	
	
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
					<legend class="literal">Hoja - Nº</legend>
					    <table align="center">												
							<tr>
								<td class="literal">Hoja</td>
								<td class="literal">
								    <label name="lbl_hoja" id="lbl_hoja" size="4" maxlength="4" class="dato">____</label>	
								</td>									
								<td class="literal">Número</td>
								<td class="literal">
								    <label name="lbl_numero" id="lbl_numero" size="4" maxlength="4" class="dato">____</label>	
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
												<td >
													<form:input path="termino_cm.id.codprovincia" id= "provincia_cm"  cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia_cm','comarca_cm','desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_provincia_cm" name="desc_provincia_cm" size="15" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ProvinciaCM','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia" />	
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Comarca</td>
												<td >
													<form:input path="termino_cm.id.codcomarca" id="comarca_cm" cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_comarca_cm','termino_cm','desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_comarca_cm" name="desc_comarca"_cm size="15" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ComarcaCM','principio', '', '');"	alt="Buscar Comarca" title="Buscar Comarca" />
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Término</td>
												<td >
													<form:input path="termino_cm.id.codtermino" id ="termino_cm" cssClass="dato" size="2" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_termino_cm','subtermino_cm');"/>
													<input class="dato"	id="desc_termino_cm" name="desc_termino_cm" size="22" readonly="readonly"/>
												</td>
												<td class=labelAseg style="COLOR: #4682b4">Subtérmino</td>
												<td >
													<form:input path="subtermino_cm" id ="subtermino_cm" cssClass="dato" size="1" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoCM','principio', '', '');"	alt="Buscar Término" title="Buscar Término" />
													
												</td>
											</tr>
										</table>
								</fieldset>
								
								<fieldset style="margin-top:3px;margin-left:3px;margin-right:3px">
				    				<legend  class="literal" style="color: #4682B4;">SIGPAC</legend>
				    				 	<table align="center">
					    				 	<tr>
											<td class=labelAseg style="COLOR: #4682b4">Prov</td>
											<td >
												<form:input path="provSig_cm" id="provSig_cm" cssClass="dato" size="2" maxlength="2"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Term</td>
											<td>
												<form:input path="termSig_cm" id="termSig_cm" cssClass="dato" size="3" maxlength="3"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Agr</td>
											<td >
												<form:input path="agrSig_cm" id="agrSig_cm" cssClass="dato" size="3" maxlength="3"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Zona</td>
											<td>
												<form:input path="zonaSig_cm" id="zonaSig_cm" cssClass="dato" size="2" maxlength="2"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Pol</td>
											<td >
												<form:input path="polSig_cm" id="polSig_cm" cssClass="dato" size="3" maxlength="3"/>
											</td>									
											<td class=labelAseg style="COLOR: #4682b4">Parc</td>
											<td >
												<form:input path="parcSig_cm" id="parcSig_cm" cssClass="dato" size="5" maxlength="5"/>
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Rec</td>
											<td >
												<form:input path="recSig_cm" id="recSig_cm" cssClass="dato" size="5" maxlength="5"/>
											</td>
										</tr>
										</table>
								</fieldset>
								
								<table style="width: 70%;" align="center">
									<tr>
									    <td> 
											<td class=labelAseg style="COLOR: #4682b4">Cultivo</td>
											<td>
												<form:input path="cultivo.id.codcultivo" id="cultivo_cm" cssClass="dato width40" size="3" maxlength="3"  onchange="javascript:lupas.limpiarCampos('desc_cultivo_cm','variedad_cm','desc_variedad_cm');"/>
												<form:input path="cultivo.descultivo" cssClass="dato" id="desc_cultivo_cm"  size="20" readonly="true" />	
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoCM','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
												
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Variedad</td>
											<td>
											    <form:input path="variedad.id.codvariedad" id="variedad_cm" cssClass="dato width40" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_variedad_cm');"/>
											    <form:input path="variedad.desvariedad" cssClass="dato" id="desc_variedad_cm"  size="20" readonly="true" />	
									            <img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VariedadCM','principio', '', '');"	alt="Buscar Variedad" title="Buscar Variedad" />
														
											</td>
										</td>
									</tr>
								</table> 
						</fieldset>
						<fieldset style="border: #4682b4 1px solid;margin-top:3px;margin-left:3px;margin-right:3px;margin-bottom:3px;">
				    		<legend class=literal style="COLOR: #4682b4">Capitales asegurados</legend>
				    			<table align="center" style="width: 100%">
									<tr>
									    <td class=labelAseg style="COLOR: #4682b4" >Producción: </td>
									    <td class=labelAseg style="COLOR: #4682b4" > Kg./Ha</td>
										<td>
											<form:input path="increHa" id="increHa" cssClass="dato"  size="9" maxlength="9" />
										</td>
										<td class=labelAseg style="COLOR: #4682b4" >Kg./Parcela</td>
										<td>
											<form:input path="increParcela" id="increParcela" cssClass="dato"  size="9" maxlength="9" />
										</td>
										<td class=labelAseg style="COLOR: #4682b4" >Kg./Unidades</td>
										<td>
											<form:input path="inc_unidades_cm" id="inc_unidades_cm" cssClass="dato" size="9" maxlength="9"/>
										</td>
										<td width="10">&nbsp;</td>
										<td class=labelAseg style="COLOR: #4682b4" >Superficie(hectáreas): </td>
										<td>
											<form:input path="superficie" id="superficie" cssClass="dato"  size="8" maxlength="8" />
										</td>
										<td width="10">&nbsp;</td>
										<td class=labelAseg style="COLOR: #4682b4" >Precio:</td>
										<td>
											<form:input path="precio_cm" id="precio_cm" cssClass="dato" size="8" maxlength="15"/>
										</td>
									<tr>
								</table>
							
							<fieldset style="margin-top:3px;margin-left:3px;margin-right:3px;margin-bottom:3px;">
							<LEGEND class=literal style="COLOR: #4682b4">Datos variables</LEGEND>
								<legend style="WIDTH: 100%" align=center>
									<table>
										<TR>
											<td class=labelAseg style="COLOR: #4682b4"> Destino: </td>
											<td noWrap>
												<form:input path="destino.coddestino" id="destino_cm" cssClass="dato"  size="2" maxlength="3"  onchange="javascript:lupas.limpiarCampos('desc_destino_cm');"/>
												<input class="dato"	id="desc_destino_cm" name="desc_destino_cm" size="20" readonly="readonly"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('DestinoCM','principio', '', '');"	alt="Buscar Destino" title="Buscar Destino" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4"> Tipo plantación:</td>
											<td noWrap>
												<form:input path="tipoPlantacion.codtipoplantacion" id="tplantacion_cm" cssClass="dato"  size="2" maxlength="3"  onchange="javascript:lupas.limpiarCampos('desc_tplantacion_cm');"/>
												<input class="dato"	id="desc_tplantacion_cm" name="desc_tplantacion_cm" size="20" readonly="readonly"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TipoPlantacionCM','principio', '', '');"	alt="Buscar Tipo Plantación" title="Buscar Tipo Plantación" />
											</td>
											
											<td class=labelAseg style="COLOR: #4682b4"> Sistema Cultivo:</td>
											<td noWrap>
												<form:input path="sistemaCultivo.codsistemacultivo" id="sistemaCultivo_cm" cssClass="dato"  size="2" maxlength="3"  onchange="javascript:lupas.limpiarCampos('desc_sistemaCultivo_cm');"/>
												<input class="dato"	id="desc_sistemaCultivo_cm" name="desc_sistemaCultivo_cm" size="20" readonly="readonly"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('SistemaCultivoCM','principio', '', '');"	alt="Buscar Sistema Cultivo" title="Buscar Sistema Cultivo" />
											</td>
										</tr>
									</table>
									<table style="WIDTH: 100%" align="center">
										<tr>
											<td class=labelAseg style="COLOR: #4682b4">Tipo Marco Plantación:</td>
											<td noWrap>
													<form:input path="codtipomarcoplantac_cm" id="codtipomarcoplantac_cm" cssClass="dato"  size="2" maxlength="3"  onchange="javascript:lupas.limpiarCampos('destipomarcoplantac_cm');"/>
													<input class="dato"	id="destipomarcoplantac_cm" name="destipomarcoplantac_cm" size="20" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('MarcoPlantacionCM','principio', '', '');"	alt="Buscar Tipo Marco Plantación" title="Buscar Tipo Marco Plantación" />
														
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Práctica Cultural:</td>
											<td noWrap>
													<form:input path="codpracticacultural_cm" id= "codpracticacultural_cm"  cssClass="dato" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('despracticacultural_cm');"/>
													<input class="dato"	id="despracticacultural_cm" name="despracticacultural_cm" size="20" readonly="readonly"/>
													<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('PracticaCulturalCM','principio', '', '');"	alt="Buscar Práctica Cultural" title="Buscar Práctica Cultural" />	
											</td>
											
											<td class=labelAseg style="COLOR: #4682b4">Sist. Producción:</SPAN> 
											<td noWrap>
												<form:input path="sistemaProduccion.codsistemaproduccion" id="sistemaProduccion_cm"  size="2" maxlength="3"  cssClass="dato" onchange="javascript:lupas.limpiarCampos('desc_sistemaProduccion_cm');"/>
												<input class="dato"	id="desc_sistemaProduccion_cm" name="desc_sistemaProduccion_cm" size="20" readonly="readonly" /> 
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VistaSistemaProduccionCM_IN','principio', '', '');"	alt="Buscar Sistema de Producción" title="Buscar Sistema de Producción" />
											 </td>
											
										</tr>
									 </table>
									 <table style="WIDTH: 100%" align=center>
											<td class=labelAseg style="COLOR: #4682b4" style="margin-left:10px">Fecha siembra:</td>
											<td noWrap>					
												<form:input path="fechaSiembra" id="fechaSiembra" cssClass="dato"  size="11" maxlength="10" />
												<input type="button" id="btn_fechaSiembra_cm" name="btn_fechaSiembra_cm" class="miniCalendario" style="cursor: pointer;" />
											</td>	
											<td class=labelAseg style="COLOR: #4682b4" style="margin-left:10px">Fecha Fin Garantias:</td>
											<td noWrap>					
												<input type="text" name="fechaFinGarantia_cm" id="fechaFinGarantia_cm" size="11" maxlength="10" class="dato"/>
												<input type="button" id="fechaFinGarantia_cm" name="btn_fechaFinGarantia_cm" class="miniCalendario" style="cursor: pointer;" />
											</td>
											<td class=labelAseg style="COLOR: #4682b4">Edad:</td>
											<td>
												<form:input path="edad_cm" id="edad_cm" cssClass="dato" size="3" />
											</td>	
											<td class=labelAseg style="COLOR: #4682b4">Incremento edad:</td>
											<td>
												<form:input path="incEdad_cm" id="incEdad_cm" cssClass="dato" size="3" />
											</td>	
											<td class=labelAseg style="COLOR: #4682b4">Unidades:</td>
											<td>
												<form:input path="unidades_cm" id="unidades_cm" cssClass="dato" size="3"/>
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
</form:form>


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

<!--  POPUPS -->
<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>




