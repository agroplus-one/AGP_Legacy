
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
	$('#overlay').hide();
}

//popupCambioMasivo
function cerrarCambioMasivo(){
	limpiarCambioMasivo();
	$('#panelCambioMasivoClaseDetalleGanado').hide();
	$('#overlayCambioMasivo').hide();
	$('#overlay').hide();
}

function limpiarCambioMasivo(){
	$('#modulo_cm').val('');	
	$('#provincia_cm').val('');
	$('#desc_provincia_cm').val('');
	$('#comarca_cm').val('');
	$('#desc_comarca_cm').val('');
	$('#termino_cm').val('');
	$('#desc_termino_cm').val('');
	$('#subtermino_cm').val('');
	$('#especie_cm').val('');
	$('#desc_especie_cm').val('');	
	$('#regimen_cm').val('');
	$('#desc_regimen_cm').val('');	
	$('#codgrupoRaza_cm').val('');
	$('#desGrupoRaza_cm').val('');	
	$('#codtipoanimal_cm').val('');
	$('#desTipoAnimal_cm').val('');	
	$('#codtipocapital_cm').val('');
	$('#desTipoCapital_cm').val('');
	
	$('#tipoCapitalCheck').attr('checked', false);
	var chktipocapital=document.getElementById('tipoCapitalCheck');
	deshabilitaCampo(chktipocapital);
}


function aplicarCambioMasivo(){
	
	var formMasivo = document.getElementById('main');
	var codprovincia = formMasivo['provincia_cm'].value;
	var codcomarca = formMasivo['comarca_cm'].value;
	var codtermino = formMasivo['termino_cm'].value;
	var subtermino = formMasivo['subtermino_cm'].value;

	var settings = $('#main').validate().settings;
	
	if(codprovincia!='' || codcomarca!='' || codtermino!='' || subtermino!=''){
		settings.rules['codprovincia'] = {required: true, digits: true};
		settings.rules['codcomarca'] = {required: true, digits: true};
		settings.rules['codtermino'] = {required: true, digits: true};
		settings.rules['subtermino'] = {subtermino_cmOK: true};

	}else{
		settings.rules['codprovincia'] = {required: false, digits: true};
		settings.rules['codcomarca'] = {required: false, digits: true};
		settings.rules['codtermino'] = {required: false, digits: true};
		settings.rules['subtermino'] = {required: false};
	}

	if ($('#main').valid()){
		if (confirm ("Se va a modificar la información relativa a la clase. ¿Desea continuar?")) {
			hidePopUpAviso();
			comprobarCampos();//funcion de claseDetalleGanado.js para establecer el Limit
			$('#main').submit();
			cerrarCambioMasivo(); 
		}	
	}	
}

function deshabilitaCampo(check){
	if (check.checked == true){
		if (check.id == "especieCheck"){
			$('#especie_cm').val('');
			$('#desc_especie_cm').val('');
			$('#especie_cm').attr("disabled","disabled");
			$('#desc_especie_cm').attr("disabled","disabled");
			$('#lupaEspecie').removeAttr("onclick");
			$('#lupaEspecie').unbind("click");
			$('#lupaEspecie').css('cursor','text');
			check.value="especieCheck";
		}else if (check.id == "regimenManejoCheck"){
			$('#regimen_cm').val('');
			$('#desc_regimen_cm').val('');
			$('#regimen_cm').attr("disabled","disabled");
			$('#desc_regimen_cm').attr("disabled","disabled");
			$('#lupaRegimenManejo').removeAttr("onclick");
			$('#lupaRegimenManejo').unbind("click");
			$('#lupaRegimenManejo').css('cursor','text');
			check.value="regimenManejoCheck";
		}else if (check.id == "gruposRazasCheck"){
			$('#codgrupoRaza_cm').val('');
			$('#desGrupoRaza_cm').val('');
			$('#codgrupoRaza_cm').attr("disabled","disabled");
			$('#desGrupoRaza_cm').attr("disabled","disabled");
			$('#lupaGruposRazas').removeAttr("onclick");
			$('#lupaGruposRazas').unbind("click");
			$('#lupaGruposRazas').css('cursor','text');
			check.value="gruposRazasCheck";
		}else if (check.id == "tiposAnimalCheck"){
			$('#codtipoanimal_cm').val('');
			$('#desTipoAnimal_cm').val('');
			$('#codtipoanimal_cm').attr("disabled","disabled");
			$('#desTipoAnimal_cm').attr("disabled","disabled");
			$('#lupaTipoAnimal').removeAttr("onclick");
			$('#lupaTipoAnimal').unbind("click");
			$('#lupaTipoAnimal').css('cursor','text');
			check.value="tiposAnimalCheck"
		}else if (check.id == "tipoCapitalCheck"){
			$('#codtipocapital_cm').val('');
			$('#desTipoCapital_cm').val('');
			$('#codtipocapital_cm').attr("disabled","disabled");
			$('#desTipoCapital_cm').attr("disabled","disabled");
			$('#lupaTipoCapital').removeAttr("onclick");
			$('#lupaTipoCapital').unbind("click");
			$('#lupaTipoCapital').css('cursor','text');
			check.value="tipoCapitalCheck"	
		}
	}else{		
		if (check.id == "especieCheck"){
			$('#especie_cm').attr("disabled","");
			$('#desc_especie_cm').attr("disabled","");
			$('#lupaEspecie').click(function () { lupas.muestraTabla('EspecieCM','principio', '', ''); });
			$('#lupaEspecie').css('cursor','hand');
			check.value="";
		}else if (check.id == "regimenManejoCheck"){
			$('#regimen_cm').attr("disabled","");
			$('#desc_regimen_cm').attr("disabled","");
			$('#lupaRegimenManejo').click(function () { lupas.muestraTabla('RegimenCM','principio', '', ''); });
			$('#lupaSistemaCultivo').css('cursor','hand');
			check.value="";
		}else if (check.id == "gruposRazasCheck"){
			$('#codgrupoRaza_cm').attr("disabled","");
			$('#desGrupoRaza_cm').attr("disabled","");
			$('#lupaGruposRazas').click(function () { lupas.muestraTabla('GrupoRazaCM','principio', '', ''); });
			$('#lupaGruposRazas').css('cursor','hand');
			check.value="";
		}else if (check.id == "tiposAnimalCheck"){
			$('#codtipoanimal_cm').attr("disabled","");
			$('#desTipoAnimal_cm').attr("disabled","");
			$('#lupaTipoAnimal').click(function () { lupas.muestraTabla('TiposAnimalGanadoCM','principio', '', ''); });
			$('#lupaTipoAnimal').css('cursor','hand');
			check.value="";
		}else if (check.id == "tipoCapitalCheck"){
			$('#codtipocapital_cm').attr("disabled","");
			$('#desTipoCapital_cm').attr("disabled","");
			$('#lupaTipoCapital').click(function () { lupas.muestraTabla('TipoCapitalGrupoNegocioCM','principio', 'codtipocapital_cm', 'ASC'); });
			$('#lupaTipoCapital').css('cursor','hand');
			check.value="";
		}
	}
	
}