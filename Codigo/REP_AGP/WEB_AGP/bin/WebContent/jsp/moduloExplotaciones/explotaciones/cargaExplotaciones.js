/*Funciones y validaciones del popupSistemaTradicional*/
function validar_SistTrad(){
	
	var frm= document.getElementById("frmSistemaTradicional");
	var plan = frm.plan_SistTrad.value;
	var ref	= frm.referencia_SistTrad.value;	
	var idpoliza = frm.idpoliza_SistTrad.value;
	var resval = validafrmSistemaTradicional(plan, ref);
	
	if(resval==""){
		$('#overlay').hide();
		$('#panelSistemaTradicional').hide();
		mostrarCapaBloqueo('Cargando explotaciones de la p�liza.');
		frm.submit();

	}else{
		$('#panelAlertasValidacion_SistTrad').html(resval);
		$('#panelAlertasValidacion_SistTrad').show();
	}
} 

function validafrmSistemaTradicional(plan, ref){
	
	var mensaje="";
	
	if(!validaCamposObligatoriosSistTrad(plan, ref)){
		mensaje="Los campos Plan y Referencia son obligatorios";
		return mensaje;
	}
	
	if(!validaPlanSistTrad(plan)){
		mensaje="El plan no es v�lido.";
		return mensaje;
	}
	
	if(!validaReferenciaSistTrad(ref)){
		mensaje="La referencia no es v�lida.";
		return mensaje;
	}
	return mensaje;
}

function validaPlanSistTrad(plan){
	//El campo ‘Plan’ debe ser un valor numérico entre 1980-9999
	var res=false;
	var valor = parseInt(plan); 
    if (isNaN(valor)) { 
           //no es número  
    	res = false; 
    }else{ 
          //Si era un número 
    	if(valor>=1980 && valor<=9999){
    		res = true;
    	}else{
    		res = false;
    	}            
     } 
    return res;
}

function validaReferenciaSistTrad(ref){
	//El campo ‘Referencia’ debe ser un valor de tamaño 7
	var largo = ref.length;
	var res=false;
     if (largo == 7){
    	res=true; 
     }
     return res;
}

function validaCamposObligatoriosSistTrad(plan, ref){
	//Ambos campos son obligatorios
	var res=true;
	  if (
			  (plan == null || plan.length == 0 || /^\s*$/.test(plan)) ||
			  (ref == null || ref.length == 0 || /^\s*$/.test(ref))
		 ){
          res=false;
      }

	
	return res;
}

function cancelar_SistTrad(){
	// limpiamos alertas
	$('#panelAlertasValidacion_SistTrad').html("");
	$('#panelAlertasValidacion_SistTrad').hide();
	// vaciamos campos
	
//	$('#plan_SistTrad').val("");
	$('#referencia_SistTrad').val("");
		
	// cerramos div "inferiores"
	$('#panelSistemaTradicional').hide();
	//$('#panelDatosAval').hide();
    $('#overlay').hide();
}


/* ************************************************************************** */
function continuar(){
	var radioSelect = $('input:radio[name=seleccionOrigen]:checked').val();
	switch (radioSelect) {
    case "doSistemaTradicional":
//       Cargamos popup
    	$('#panelAlertasValidacion_SistTrad').hide();
    	$('#overlay').show();
    	$('#panelSistemaTradicional').show();
        break;
    case "doSituacionAct":
    	mostrarCapaBloqueo('Cargando Datos.');
    	mandarFormularioExplotaciones("doCargaSituacionActualizada");
        break;
    case "doPolizaAnterior":
    	mostrarCapaBloqueo('Cargando Datos.');
    	mandarFormularioExplotaciones("doCargaPolizaOriginalUltimosPlanes");
        break;
    case "doPlanActual":
    	mostrarCapaBloqueo('Cargando Datos.');
    	mandarFormularioExplotaciones("doCargaPolizaPlanActual");
        break;
    case "doNoCargar":
    	mandarFormularioExplotaciones("doNoCargarExplotaciones");
        break;
    default:        
        break;    
	} 	
}

function mostrarCapaBloqueo(mensaje){
	$.blockUI.defaults.message = '<h4>' + mensaje + '<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
}

function mandarFormularioExplotaciones(metodo){
	var frm= document.getElementById("main");
	frm.method.value = metodo;
	frm.submit();
}

