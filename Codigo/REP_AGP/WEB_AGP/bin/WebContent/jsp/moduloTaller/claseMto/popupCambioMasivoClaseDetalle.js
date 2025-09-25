
// DAA 08/02/2013 .js para las funciones que realiza el popup del cambio masivo de clases
   
// popup aviso
function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);
	$('#popUpAvisos').show();
	$('#overlayCambioMasivo').show();
}
 function hidePopUpAviso(){
	$('#popUpAvisos').hide();
	$('#overlayCambioMasivo').hide();
}

//popupCambioMasivo
function cerrarCambioMasivoClaseDetalle(){
	limpiarCambioMasivoClaseDetalle();
	$('#panelCambioMasivoClaseDetalle').hide();
	$('#overlayCambioMasivo').hide();
}

function limpiarCambioMasivoClaseDetalle(){
	$('#modulo_cm').val('');
	$('#cicloCultivo_cm').val('');	 
	$('#desciclocultivo_cm').val('');
	$('#sistemaCultivo_cm').val('');
	$('#desc_sistemaCultivo_cm').val('');
	$('#cultivo_cm').val("");
	$('#desc_cultivo_cm').val('');
	$('#variedad_cm').val('');
	$('#desc_variedad_cm').val('');
	$('#capital_cm').val('');
	$('#desc_capital_cm').val('');
	$('#provincia_cm').val('');
	$('#desc_provincia_cm').val('');
	$('#comarca_cm').val('');
	$('#desc_comarca_cm').val('');
	$('#termino_cm').val('');
	$('#desc_termino_cm').val('');
	$('#subtermino_cm').val('');
	$('#tplantacion_cm').val('');
	$('#desc_tplantacion_cm').val('');
	
}


function aplicarCambioMasivoClaseDetalle(){
	$('#main').submit();
	cerrarCambioMasivoClaseDetalle();	
}

function deshabilitaCampo(check){
	
	if (check.checked == true){
		if (check.id == "cicloCultivoCheck"){
			$('#cicloCultivo_cm').val('');
			$('#desciclocultivo_cm').val('');
			$('#cicloCultivo_cm').attr("disabled","disabled");
			$('#desciclocultivo_cm').attr("disabled","disabled");
			$('#lupaCicloCultivo').removeAttr("onclick");
			$('#lupaCicloCultivo').unbind("click");
			$('#lupaCicloCultivo').css('cursor','text');
			check.value="cicloCultivoCheck";
		}else if (check.id == "sistemaCultivoCheck"){
			$('#sistemaCultivo_cm').val('');
			$('#desc_sistemaCultivo_cm').val('');
			$('#sistemaCultivo_cm').attr("disabled","disabled");
			$('#desc_sistemaCultivo_cm').attr("disabled","disabled");
			$('#lupaSistemaCultivo').removeAttr("onclick");
			$('#lupaSistemaCultivo').unbind("click");
			$('#lupaSistemaCultivo').css('cursor','text');
			check.value="sistemaCultivoCheck";
		}else if (check.id == "tipoCapitalCheck"){
			$('#capital_cm').val('');
			$('#desc_capital_cm').val('');
			$('#capital_cm').attr("disabled","disabled");
			$('#desc_capital_cm').attr("disabled","disabled");
			$('#lupaTipoCapital').removeAttr("onclick");
			$('#lupaTipoCapital').unbind("click");
			$('#lupaTipoCapital').css('cursor','text');
			check.value="tipoCapitalCheck";
		}else if (check.id == "tipoPlantacionCheck"){
			$('#tplantacion_cm').val('');
			$('#desc_tplantacion_cm').val('');
			$('#tplantacion_cm').attr("disabled","disabled");
			$('#desc_tplantacion_cm').attr("disabled","disabled");
			$('#lupaTipoPlantacion').removeAttr("onclick");
			$('#lupaTipoPlantacion').unbind("click");
			$('#lupaTipoPlantacion').css('cursor','text');
			check.value="tipoPlantacionCheck"
			
		}
	}else{
		
		if (check.id == "cicloCultivoCheck"){
			$('#cicloCultivo_cm').attr("disabled","");
			$('#desciclocultivo_cm').attr("disabled","");
			$('#lupaCicloCultivo').click(function () { lupas.muestraTabla('CicloCultivoCM','principio', '', ''); });
			$('#lupaCicloCultivo').css('cursor','hand');
			check.value="";
		}else if (check.id == "sistemaCultivoCheck"){
			$('#sistemaCultivo_cm').attr("disabled","");
			$('#desc_sistemaCultivo_cm').attr("disabled","");
			$('#lupaSistemaCultivo').click(function () { lupas.muestraTabla('SistemaCultivoCM','principio', '', ''); });
			$('#lupaSistemaCultivo').css('cursor','hand');
			check.value="";
		}else if (check.id == "tipoCapitalCheck"){
			$('#capital_cm').attr("disabled","");
			$('#desc_capital_cm').attr("disabled","");
			$('#lupaTipoCapital').click(function () { lupas.muestraTabla('TipoCapitalCM','principio', '', ''); });
			$('#lupaTipoCapital').css('cursor','hand');
			check.value="";
		}else if (check.id == "tipoPlantacionCheck"){
			$('#tplantacion_cm').attr("disabled","");
			$('#desc_tplantacion_cm').attr("disabled","");
			$('#lupaTipoPlantacion').click(function () { lupas.muestraTabla('TipoPlantacionCM','principio', '', ''); });
			$('#lupaTipoPlantacion').css('cursor','hand');
			check.value="";
		}
	}
	
}
		
