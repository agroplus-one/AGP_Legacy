// AMG 30/10/2014 js para operaciones del popupRestaurarParams


$(document).ready(function(){
		
	//Validaciones del formulario
	$('#frmRestaurarParams').validate({
		errorLabelContainer: "#txt_validacionesRes",
		wrapper: "li",
			 highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
		     },
			 unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
			 },		  				  				 
		rules: {
			"comMaxP":{range: [0,100]},
			"pctadministracionP":{range: [0,100]},
			"pctadquisicionP":{range: [0,100]},
			"pctEntidadP":{range: [0,100]},
			"pctESMedP":{range: [0,1000]}
		},
		messages: {
			"comMaxP":{range: "% Comisión máximo debe contener un número entre 0 y 100"},
			"pctadministracionP":{range: "% Administración debe contener un número entre 0 y 100"},
			"pctadquisicionP":{range: "% Adquisición debe contener un número entre 0 y 100"},
			"pctEntidadP":{range: "% Entidad debe contener un número entre 0 y 100"},
			"pctESMedP":{range: "% E-S Mediadora debe contener un número entre 0 y 1000"}
		}
	});
	
	
});

function limpiarPaneles(){
	$("#panelAlertasValidacion").hide();
	$('#txt_validacionesRes').html("");
	$('#txt_validacionesRes').hide();
	$('#txt_mensaje_res').html("");
	$('#txt_mensaje_res').hide();
}

function limpiarDatos(){
	$('#comMaxP').val('');
	$('#pctadministracionP').val('');
	$('#pctadquisicionP').val('');
	$("#campoObligatorio_comMaxP").hide();
	$("#campoObligatorio_pctadministracionP").hide();
	$("#campoObligatorio_pctadquisicionP").hide();
	
}

function showPopUpRestaurarParams(){
 	$('#divRestaurarParams').fadeIn('normal');
	$('#overlay').show();
}

function cerrarPopUpRestaurarParams(){
	$('#divRestaurarParams').fadeOut('normal');
	$('#overlay').hide();
}

function actualizarParams(){
	if ($('#frmRestaurarParams').valid()){
	
		var esValido = true;	
		var vacios=0;
		
		
		var numeroRegistros=$(".numeroPoliza").val();		
		numeroRegistros++;		
		for(var x=1;x<numeroRegistros;x++){
			$("."+x+"aa").each(function(i, obj) {
				if(obj.value==''){				
					vacios++;
				}
				if(vacios==3){
					$('#txt_mensaje_res').html("Debe introducir al menos un porcentaje");
					$('#txt_mensaje_res').show();
					esValido = false;
				}
			});
		}

		
		
		
		$(':input.bb').each(function(i, obj) {
			if(obj.value==''){
				$('#txt_mensaje_res').html("Los porcentajes de Entidad y E-S Mediadora no pueden ser vacíos");
				$('#txt_mensaje_res').show();
				esValido = false;
			}		    
		});
		
	/*
		if($('#comMaxP').val() == '' && $('#pctadministracionP').val() =='' && $('#pctadquisicionP').val() ==''){
			$('#txt_mensaje_res').html("Debe introducir al menos un porcentaje");
			$('#txt_mensaje_res').show();
			esValido = false;
		}
		
		if(esValido){
			if( $('#pctEntidadP').val() == '' || $('#pctESMedP').val() ==''){
				$('#txt_mensaje_res').html("Los porcentajes de Entidad y E-S Mediadora no pueden ser vacíos");
				$('#txt_mensaje_res').show();
				esValido = false;
			}
			else{
				
				// MPM - 04/05/2015
				// Valida que los % de entidad y emisora sumen 100%
				var porc1 = Number($('#pctEntidadP').val());
				var porc2 = Number($('#pctESMedP').val());
				
				if((porc1 + porc2) != 100){
					$('#txt_mensaje_res').html("Los porcentajes de Entidad y E-S Mediadora deben sumar 100%");
					$('#txt_mensaje_res').show();
					esValido = false;
				}
			}
		}
*/
		if(esValido){
			$('#txt_mensaje_res').html("");
			$('#txt_mensaje_res').hide();
			confirmarActualizacionParams();	
		}
	}
}


function previoConfirmarActualizacionParams(){
	
	
	$.ajax({
	    url: "comisionesCultivos.html?method=doParamsGeneralesByIdPolizaYLinea&idPoliza="+id+"&lineaSeguroId="+lineaSeguroId,
		data: "",
		async:true,
	    dataType: "json",
	    error: function(objeto, quepaso, otroobj){
            alert("Error al cargar los parametros generales para la póliza: " + quepaso);
        },
        success: function(datos){                 	
        	pintarDatosParamsGenerales(eval(datos), porcentajeRecargo);
        },
        type: "GET"
	    
	});
}

function confirmarActualizacionParams(){

		
	
	if (confirm('¡ATENCIÓN! Se va a proceder a actualizar los datos de comisiones de la póliza ¿Desea continuar?')) {
		
		// MPM - 04/05/2015
		// Antes de enviar el formulario hay que aplicar el % de descuento o recargo del colectivo a la comisión del mediador
		var nuevoValorESMed = ((Number($('#pctDescRecColectivo').val()) * Number($('#pctESMedP').val())) + Number($('#pctESMedP').val()));
		$('#pctESMedP').val(Number(nuevoValorESMed).toFixed(2));
		
		$("#frmRestaurarParams").submit();
		$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	      $.blockUI({ 
	          overlayCSS: { backgroundColor: '#525583'},
	          baseZ: 2000
	      });
	    cerrarPopUpRestaurarParams();
	}
}

function restaurarParams(){
	
	limpiarPaneles();
	limpiarDatos();
	var list = "";
	var contador = 0;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function(){
		if($(this).attr('checked')){
			list = list + $(this).val();
			contador++;
		}
	});
	if(contador > 0){
		if (contador > 1){// hay más de una póliza marcada			
			$('#divAviso').show();
			$('#txt_info_check_multiple').show();
			$('#overlay').show();
		}else{// solo una póliza marcada			
			list = list.substring(0,list.length - 1);
			var valores   = list.split("|");			
			var id        = valores[0].split("#")[0];
			var estado    = valores[0].split("#")[1];
			var tipo      = valores[0].split("#")[7];
			var plan      = valores[0].split("#")[8];
			
			var pctComMax = valores[0].split("#")[9];		
			var pctAdm 	  = valores[0].split("#")[10];
			var pctAdq    = valores[0].split("#")[11];
			var linsegId  = valores[0].split("#")[12];
			
			var pctEnt	  = valores[0].split("#")[13];

			var pctESMed  = valores[0].split("#")[14];
			
			var descuentoRecargoTipo = valores[0].split("#")[15];
			var descuentoRecargoPct = valores[0].split("#")[16];
			
			var descuentoElegidoPct = valores[0].split("#")[17];
			
			var porcentajeRecargo = 0.00;
			/*
			$('#comMaxP').val(Number(pctComMax).toFixed(2));
			$('#pctadministracionP').val(Number(pctAdm).toFixed(2));
			$('#pctadquisicionP').val(Number(pctAdq).toFixed(2));
			
			if(pctEnt!=null && !pctEnt=="" && Number(pctEnt)>=0 && pctESMed!=null && !pctESMed=="" && Number(pctESMed)>=0){
				
				$('#pctEntidadP').val(Number(pctEnt).toFixed(2));
				$('#pctESMedP').val(Number(pctESMed).toFixed(2));
				
				var pctESMedNumerico = Number(pctESMed).toFixed(2);
				
				// % de entidad con el % de comisión máxima -> pctEnt*pctComMax/100.00
				var pctEntidadPAjustadoAux = (Number(pctEnt)*Number(pctComMax)/100.00).toFixed(2);
				$('#pctEntidadPAjustado').html("("+pctEntidadPAjustadoAux+"%)");

				//PCT_DESC_RECARG (0 - Descuento, 1 - Recargo)
				if(descuentoRecargoTipo == '0'){
					//Descuento
					$('#pctColectivoP').html(descuentoRecargoPct+ "% dto");
					porcentajeRecargo = -Number(descuentoRecargoPct);
					
				}else if(descuentoRecargoTipo == '1'){
					//Recargo
					$('#pctColectivoP').html(descuentoRecargoPct+"% rec");
					porcentajeRecargo = Number(descuentoRecargoPct);
					
				}else{
					//Vacío -> N/A
					$('#pctColectivoP').html("N/A");
					porcentajeRecargo = Number(0.00);
										
				}
				
				// % de recargo/descuento asociado al colectivo (para la validación al actualizar)
				$('#pctDescRecColectivo').val(porcentajeRecargo/100.00);
				
				//Sin descuento elegido en póliza
				var numDescuentoElegidoPct = Number(descuentoElegidoPct);

				if(numDescuentoElegidoPct==0){//Sin descuentos
					
					// % de ES con el % de comisión máxima -> pctEnt*pctComMax/100.00
					var pctESMedPAjustadoAux = (Number(pctESMed)*Number(pctComMax)/100.00).toFixed(2);
					$('#pctESMedPAjustado').html("("+pctESMedPAjustadoAux+"%)");
					
				}else{//Con descuento o recargo
					
					// % de ES con el % de comisión máxima -> pctEnt*pctComMax/100.00
					var pctESMedPAjustadoAux	= (Number(pctESMed)*Number(pctComMax)/100.00);
					pctESMedPAjustadoAux		= (pctESMedPAjustadoAux + (Number(numDescuentoElegidoPct)*pctESMedPAjustadoAux/100.00)).toFixed(2);
					$('#pctESMedPAjustado').html("("+pctESMedPAjustadoAux+"%)");
					
				}
			}
			*/
			if (tipo == 'P'){
				if(estado == 1 || estado == 2){
					if (plan >= 2015){
						$('#idPlz').val(id);
						if ($('#perfil').val()==0){// Recuperamos los datos de Parametros generales				
							recuperarParamsGenerales(id, linsegId, porcentajeRecargo);											
						}else{// se abre confirmación directamente para perfiles 1 interno y 5							
							if ($('#perfil').val()==5 || ($('#perfil').val()==1 && $('#externo').val()==0)){
								confirmarActualizacionParams();
							}else{//usuario no válido
								$('#panelAlertasValidacion').html("Usuario sin permisos para actualizar los datos de comisiones");
								$("#panelAlertasValidacion").show();
							}
						}
					}else{// plan incorrecto					
						$('#panelAlertasValidacion').html("El plan de la póliza no permite actualizar los datos de comisiones");
						$("#panelAlertasValidacion").show();
					}
				}else{// estado incorrecto de la póliza Ppal					
					$('#panelAlertasValidacion').html("El estado de la póliza principal no permite actualizar los datos de comisiones");
					$("#panelAlertasValidacion").show();
				}
			}else{// estado incorrecto , póliza complementaria				
				$('#panelAlertasValidacion').html("El tipo de la póliza no permite actualizar los datos de comisiones");
				$("#panelAlertasValidacion").show();
			}
		}
	}else{// no hay polizas seleccionadas		
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function recuperarParamsGenerales (id, lineaSeguroId, porcentajeRecargo){
	
	$.ajax({
		    url: "comisionesCultivos.html?method=doParamsGeneralesByIdPolizaYLinea&idPoliza="+id+"&lineaSeguroId="+lineaSeguroId,
			data: "",
			async:true,
		    dataType: "json",
		    error: function(objeto, quepaso, otroobj){
                alert("Error al cargar los parametros generales para la póliza: " + quepaso);
            },
            success: function(datos){                 	
            	pintarDatosParamsGenerales(eval(datos), porcentajeRecargo);
            	//$('#listDetallePct').val(datos.listParamSGen);
		    	//$('#panelDetalleLineas').show();
				//$('#overlay').show(); 		
            },
            type: "GET"
		    
		});
}


function borrarAlerts(){
	
		$('#txt_mensaje_res').html("");
		$('#txt_mensaje_res').hide();
	
}

function pintarDatosParamsGenerales(listParamSGen, porcentajeRecargo){		
	
		var porComisionMax 	= "";//Number(listParamSGen[0][2]).toFixed(2);
		var porAdmin 		= "";//Number(listParamSGen[0][0]).toFixed(2);
		var porAdq 			= "";//Number(listParamSGen[0][1]).toFixed(2);

		var porEnt = "";
		var porEntAjustado = "";
		var porESMed = "";
		var porESMedAjustado = "";	
		
		
		//validamos que traen información
		var error = false;
		var grupoFallidos="";
		
		//validacion de params generales
		var hayAlguno=false;
		for(var i=0;i<listParamSGen.length;i++){
			if(isNaN(listParamSGen[i].id)){
								hayAlguno=true;
								if (listParamSGen[i].porEnt==null || listParamSGen[i].porEnt=="" || listParamSGen[i].porESMed==null || listParamSGen[i].porESMed==""){
									error=true;
								}			
			}
		}			
				
		
		if(error || !hayAlguno){
				$('#panelAlertasValidacion').html("No existen registros de comisiones en el mantenimiento");
				$("#panelAlertasValidacion").show();
		}else{
		//si no hay errores pintamos los datos.
		//volvemos a recorrer y pintamos según sea parametro general o de poliza.
					var reg="";
					var comPol="";
					var numeroPoliza=0; //nos permite recoger los parámetros en el controller mas comodos.
					
					for(var i=0;i<listParamSGen.length;i++){
						if(isNaN(listParamSGen[i].id)){
								
								//general
								 porComisionMax=Number(listParamSGen[i].porComisionMax).toFixed(2);
								 porAdmin=Number(listParamSGen[i].porAdmin).toFixed(2);
								 porAdq=Number(listParamSGen[i].porAdq).toFixed(2);
								 porcentajeRecargo=Number(listParamSGen[i].porcentajeRecargo).toFixed(2);
								 porEntAjustado=Number(listParamSGen[i].pctEntidadAjustado).toFixed(2);
								 porESMedAjustado=Number(listParamSGen[i].pctEsMedAjustado).toFixed(2);
								 if(listParamSGen[i].porEnt!=null ){
									 porEnt = Number(listParamSGen[i].porEnt).toFixed(2);
								 }
								 if(listParamSGen[i].porESMed!=null){
									 porESMed = Number(listParamSGen[i].porESMed).toFixed(2);
								 }
								 
								reg = reg + "<tr>";
								reg = reg + "<td style='text; width:7%' class='literal' >G.N. "+listParamSGen[i].descGrupoNecio+"</td>";
								reg = reg + "<td style='text; width:11%' class='literal' >% Comisi&oacute;n m&aacute;ximo </td>" + 
								"<td style='text; width:5%' class='detalI'>" +porComisionMax+"%</td>" +
								"<td style='text; width:10%' class='literal'>% Administraci&oacute;n</td>" + 
								"<td style='text; width:5%' class='detalI'>" +porAdmin+"%</td>" +
								"<td style='text; width:9%' class='literal' >% Adquisici&oacute;n</td>" + 
								"<td style='text; width:5%' class='detalI' >" + porAdq+"%</td>" +
							
								"<td style='text; width:8%' class='literal' >% Entidad</td>" + 
								"<td style='text; width:9%' class='detalI' >" +porEnt+"% <label style='color:blue'>(" + porEntAjustado + " %)</label></td>" +
								"<td style='text; width:7%' class='literal' >% Colectivo</td>";
								
								var tipoDctoRecargo=listParamSGen[i].tipoDctoRecargo;
								if(tipoDctoRecargo=='1'){
									reg = reg + "<td style='text-align:center; width:5%' class='detalI'>" +porcentajeRecargo+"% rec</td>";
									
								}else if(tipoDctoRecargo=='0'){
									//Se le vuelve a cambiar el signo para mostrarlo
									reg = reg + "<td style='text-align:center; width:5%' class='detalI'>" +(porcentajeRecargo)+"% dto</td>";
									
								}else{
									reg = reg + "<td style='text-align:center; width:5%' class='detalI'>N/A</td>";
								}
							
								reg = reg +
								"<td style='text-align:left; width:9%' class='literal'>% E-S Mediadora </td>" + 
								"<td style='text-align:left; width:10%' class='detalI'>" + porESMed+"% <label style='color:blue'>(" + porESMedAjustado + " %)</label></td>";
								reg = reg + "</tr>";
							}else{
								//poliza
								
								numeroPoliza++;
											
								var id        = listParamSGen[i].id;
								var estado    = listParamSGen[i].estado;
								var tipo      = listParamSGen[i].tipo;
								var plan      = listParamSGen[i].plan;
								
								var pctComMax = listParamSGen[i].pctComMax;
								var pctAdm 	  = listParamSGen[i].pctAdm;
								var pctAdq    = listParamSGen[i].pctAdq;
								var linsegId  = listParamSGen[i].linsegId;
								
								var pctEnt	  = listParamSGen[i].pctEnt;

								var pctESMed  = listParamSGen[i].pctESMed;
								
								var descuentoRecargoTipo = listParamSGen[i].descuentoRecargoTipo;
								var descuentoRecargoPct = listParamSGen[i].descuentoRecargoPct;
								
								var descuentoElegidoPct = listParamSGen[i].descuentoElegidoPct;
								
								var porcentajeRecargo = 0.00;
								var pctEsMedAjustado = listParamSGen[i].pctEsMedAjustado;
								var pctEntidadAjustado = listParamSGen[i].pctEntidadAjustado;
								
								//$('#comMaxP').val(Number(pctComMax).toFixed(2));
								var valComMaxP=Number(pctComMax).toFixed(2);
								var valPctAdminP=Number(pctAdm).toFixed(2);
								var valpctadquisicionP=Number(pctAdq).toFixed(2);
								var valpctEsMedAjustado=Number(pctEsMedAjustado).toFixed(2);
								var valpctEntidadAjustado=Number(pctEntidadAjustado).toFixed(2);
								
								if(pctEnt!=null && !pctEnt=="" && Number(pctEnt)>=0 && pctESMed!=null && !pctESMed=="" && Number(pctESMed)>=0){
									//PCT_DESC_RECARG (0 - Descuento, 1 - Recargo)
									var valPctColectivo="";
									if(descuentoRecargoTipo == '0'){
										//Descuento
										valPctColectivo=Number(descuentoRecargoPct).toFixed(2)+ "% dto";
									}else if(descuentoRecargoTipo == '1'){
										//Recargo
										valPctColectivo=Number(descuentoRecargoPct).toFixed(2)+"% rec";
									}else{
										//Vacío -> N/A
										valPctColectivo="N/A";
									}
									
								}

								comPol = comPol + "<tr>";
								
								comPol = comPol + "<td style='text; width:7%' class='literal'>" +
								"<input type='hidden' value="+listParamSGen[i].grupoNegocio+" id='grupoNegocio"+numeroPoliza+"' name='grupoNegocio"+numeroPoliza+"' />"+		
								"G.N. "+listParamSGen[i].descGrupoNegocio+" </td>";
								
								comPol = comPol + "<td style='text; width:11%' class='literal'>% Comisi&oacute;n m&aacute;ximo </td>"+								
								"<td class='detalI' style='width:5%'>"+
								"<input type='text' value="+valComMaxP+" id='comMaxP"+numeroPoliza+"' name ='comMaxP"+numeroPoliza+"' size='4' maxlength='6' class='dato "+numeroPoliza+"aa' onchange='this.value = this.value.replace(',', '.');if(!isNaN(this.value) && value !=''){this.value = Number(this.value).toFixed(2)};borrarAlerts();'/>"+
								"<label class='campoObligatorio' id='campoObligatorio_comMaxP"+numeroPoliza+"' title='Campo obligatorio'>*</label>"+
								"</td>";					
								
								comPol = comPol + "<td class='literal' style='width:10%'>% Administración</td>"+
								"<td class='detalI' style='width:5%'>"+
								"<input type='text'  value="+valPctAdminP+" id='pctadministracionP"+numeroPoliza+"' name='pctadministracionP"+numeroPoliza+"' size='4' maxlength='6' class='dato "+numeroPoliza+"aa' onchange='this.value = this.value.replace(',', '.');if(!isNaN(this.value) && value !=''){this.value = Number(this.value).toFixed(2)};borrarAlerts();'/>"+
								"<label class='campoObligatorio' id='campoObligatorio_pctadministracionP"+numeroPoliza+"' title='Campo obligatorio'>*</label>"+
								"</td>";
								
								comPol = comPol + "<td class='literal' style='width:9%'>% Adquisición</td>"+
								"<td class='detalI' style='width:5%'>"+
								"<input type='text' value="+valpctadquisicionP+" id='pctadquisicionP"+numeroPoliza+"' name='pctadquisicionP"+numeroPoliza+"' size='4' maxlength='6' class='dato "+numeroPoliza+"aa' onchange='this.value = this.value.replace(',', '.');if(!isNaN(this.value) && value !=''){this.value = Number(this.value).toFixed(2)};borrarAlerts();'/>"+
								"<label class='campoObligatorio' id='campoObligatorio_pctadquisicionP"+numeroPoliza+"' title='Campo obligatorio'>*</label>"+
								"</td>";
								
								comPol = comPol + "<td class='literal' style='width:8%'>% Entidad</td>"+
								"<td class='detalI' style='width:9%'>"+
								"<input type='text' value='"+pctEnt+"' id='pctEntidadP"+numeroPoliza+"' name='pctEntidadP"+numeroPoliza+"' size='4' maxlength='6' class='dato bb' onchange='this.value = this.value.replace(',', '.');if(!isNaN(this.value) && value !=''){this.value = Number(this.value).toFixed(2)};borrarAlerts();'/>"+
								"<label class='campoObligatorio' id='campoObligatorio_pctEntidadP"+numeroPoliza+"' title='Campo obligatorio'>*</label>"+
								"<label id='pctEntidadPAjustado"+i+"' style='color:blue'>&nbsp;("+valpctEntidadAjustado+" %)</label>"+
								"</td>";								
								
								comPol = comPol + "<td class='literal' style='width:7%'>% Colectivo</td>"+
								"<td class='detalI' style='width:5%;text-align:center;'>"+
								valPctColectivo+
								"</td>";	
								
								comPol = comPol + "<td class='literal' style='width:9%'>% E-S Mediadora</td>"+
								"<td class='detalI' style='width:10%'>"+
								"<input type='text' value="+pctESMed+" id='pctESMedP"+numeroPoliza+"' name='pctESMedP"+numeroPoliza+"' size='4' class='dato bb' onchange='this.value = this.value.replace(',', '.');if(!isNaN(this.value) && value !=''){this.value = Number(this.value).toFixed(2)};borrarAlerts();'/>"+
								"<label class='campoObligatorio' id='campoObligatorio_pctESMedP"+numeroPoliza+"' title='Campo obligatorio'>*</label>"+
								"<label id='pctESMedPAjustado"+i+"' style='color:blue'>&nbsp;("+valpctEsMedAjustado+" %)</label>"+
								"</td>";
								
								comPol = comPol + "</tr>";
								
							}
						}
						//pintamos generales
						$('#datosPop').html(reg);
						$('#datosPctPol').html(comPol);	
						$('#numeroPoliza').val(numeroPoliza);
						showPopUpRestaurarParams();
						//pintamos polizas
		}

		
	}