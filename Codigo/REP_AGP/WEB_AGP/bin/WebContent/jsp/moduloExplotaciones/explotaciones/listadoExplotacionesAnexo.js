function consultar(){
	$('#main3').submit();
}

function limpiar() {
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_termino').val('');
	$('#desc_comarca').val('');
	$('#latitud').val('');
	$('#longitud').val('');
	$('#rega').val('');
	$('#sigla').val('');
	$('#subexplotacion').val('');
	$('#especie').val('');
	$('#desc_especie').val('');
	$('#regimen').val('');
	$('#desc_regimen').val('');
	$('#limpiar').val("true");
	$('#tipoModificacion').val('');
	consultar();
}

function volver(){
	var formMain = document.forms['main3'];
	var vieneDeListadoAnexosMod = formMain['vieneDeListadoAnexosMod'].value;
	
	if(vieneDeListadoAnexosMod=='true'){
		var formVolverAnexos = document.forms['volverConsultaAnexos'];
		formVolverAnexos.submit();
	}else{
		var formVolverDeclaracionesModificacionPoliza = document.forms['volverDeclaracionesModificacionPoliza'];
		formVolverDeclaracionesModificacionPoliza['idPoliza'].value = formMain['idpoliza'].value;
		formVolverDeclaracionesModificacionPoliza.submit();
	}
}

function altaExplotacionAnexo(){
	/* Comentado por no funcionar en IE8
	$('#datosExplotacionesAnexo #method').val('doAltaExplotacionAnexo');
	$('#datosExplotacionesAnexo #anexoModificacionId').val($('#main3 #anexoModificacionId').val());
	$('#datosExplotacionesAnexo').submit();
	*/
	var formDatos = document.forms['datosExplotacionesAnexo'];
	var formMain = document.forms['main3'];
	
	formDatos['method'].value = 'doAltaExplotacionAnexo';
	formDatos['anexoModificacionId'].value = formMain['anexoModificacionId'].value;
	formDatos.submit();
}

function editarExplotacionAnexo(id){
	/* Comentado por no funcionar en IE8
	$('#datosExplotacionesAnexo #method').val('doEditarExplotacionAnexo');
	$('#datosExplotacionesAnexo #explotacionAnexoId').val(id);
	$('#datosExplotacionesAnexo').submit();
	 */
	var form = document.forms['datosExplotacionesAnexo'];
	form['method'].value = 'doEditarExplotacionAnexo';
	form['explotacionAnexoId'].value = id;
	form.submit();
}

function deshacerExplotacionAnexo(id){

	if (confirm('�Est� seguro de que desea deshacer los cambios de esta explotaci�n?')) {
		/* Comentado por no funcionar en IE8
		$('#main3 #operacion').val('deshacerExplotacionAnexo');
		$('#main3 #explotacionAnexoId').val(id);
		$('#main3').submit();
		*/
		var form = document.forms['main3'];
		form['operacion'].value = 'deshacerExplotacionAnexo';
		form['explotacionAnexoId'].value = id;
		form.submit();
	}
}

function borrarExplotacionAnexo(id){
	
	if (confirm('�Est� seguro de que desea borrar esta explotaci�n?')) {
		/* Comentado por no funcionar en IE8
		$('#main3 #operacion').val('borrarExplotacionAnexo');
		$('#main3 #explotacionAnexoId').val(id);
		$('#main3').submit();
		*/
		var form = document.forms['main3'];
		form['operacion'].value = 'borrarExplotacionAnexo';
		form['explotacionAnexoId'].value = id;
		form.submit();
	}
}

function visualizarDatosRegistro(id){
	/* Comentado por no funcionar en IE8
	$('#datosExplotacionesAnexo #method').val('doEditarExplotacionAnexo');
	$('#datosExplotacionesAnexo #operacion').val('visualizarDatosRegistro');
	$('#datosExplotacionesAnexo #explotacionAnexoId').val(id);
	$('#datosExplotacionesAnexo').submit();
	*/
	var form = document.forms['datosExplotacionesAnexo'];
	form['method'].value = 'doEditarExplotacionAnexo';
	form['operacion'].value = 'visualizarDatosRegistro';
	form['explotacionAnexoId'].value = id;
	form.submit();
}

function recalcular(){
	
	if (confirm('�Est� seguro de que desea recalcular el precio de todas las explotaciones?')) {
		/* Comentado por no funcionar en IE8
		$('#main3 #operacion').val('recalcularPrecios');
		$('#main3 #explotacionAnexoId').val('-1');
		$('#main3').submit();
		*/
		var form = document.forms['main3'];
		form['operacion'].value = 'recalcularPrecios';
		form['explotacionAnexoId'].value = -1;
		form.submit();
	}
}

function enviar() {
	muestraCapaEspera ("Validando el A.M");
	$("#validarAnexo #idCuponValidar").val($("#main3 #idCupon").val());
	$("#validarAnexo").submit();
}

function muestraCapaEspera(msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

function onInvokeAction(id) {	
	var to = document.getElementById("adviceFilter");
	if (to) {
		to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	}
	var panelInformacion = document.getElementById("panelInformacion");
	if (panelInformacion) {
		panelInformacion.style.display = 'none';
	}
	var panelAlertas = document.getElementById("panelAlertas");
	if (panelAlertas) {
		panelAlertas.style.display = 'none';
	}
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	var modoLectura = $('#datosExplotacionesAnexo #modoLectura').val();
	$.get('listadoExplotacionesAnexo.html?ajax=true&modoLectura='+modoLectura+'&'
			+ decodeURIComponent(parameterString), function(data) {
		$("#grid").html(data);
	});
}

function validacionesPreviasEnvioAjax(){

	var form = document.forms['main3'];
	var idAnexo = form['anexoModificacionId'].value;
	
	$.ajax({
            url: "validacionesAnexoAjax.html",
            data: "method=doValidacionesPreviasEnvio&idAnexo="+idAnexo+"&hayCambiosDatosAsegurado="+$("#hayCambiosDatosAsegurado").val(),
            async:true,
            cache: false,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al comprobar la coherencia de los datos variables de las explotaciones: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	if(datos.validacionesPreviasEnvio.valueOf() == "true"){
            		enviar();
            	}else{
            		$('#panelInformacion').hide();
            		$('#panelAlertasValidacion').html(datos.mensaje);
            		$('#panelAlertasValidacion').show();
            	}
            	
            },
            type: "POST"
        });
}

function showPopUpCambioIBAN(){
	console.log ("Dentro de showPopUpCambioIBAN");
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('#panelInformacion').html('');
	$('#panelInformacion').hide();
	
	var ibanAsegOriginal = $('#ibanAsegOriginal').val();
	var ibanAsegFormateado = ibanAsegOriginal.substr(0, 4)+" "+ibanAsegOriginal.substr(4, 4)+" "+ibanAsegOriginal.substr(8, 4)+" "+ibanAsegOriginal.substr(12, 4)+" "+ibanAsegOriginal.substr(16, 4)+" "+ibanAsegOriginal.substr(20, 4);
	$('#ibanAntiguo').text(ibanAsegFormateado);
 	$('#divCambioIBAN').fadeIn('normal');
 	$('#cambioIBANPopUpError').hide();
 	
 	/* Pet. 70105 ** MODIF TAM (11/03/2021) */
 	/* RGA solicita que se visualice siempre el contenido de las cuentas modificadas, no solo en consulta */
	var ibanAsegModificado = $('#ibanAsegModificado').val();
	$('#iban').val(ibanAsegModificado.substr(0, 4));
	$('#cuenta1').val(ibanAsegModificado.substr(4, 4));
	$('#cuenta2').val(ibanAsegModificado.substr(8, 4));
	$('#cuenta3').val(ibanAsegModificado.substr(12, 4));
	$('#cuenta4').val(ibanAsegModificado.substr(16, 4));
	$('#cuenta5').val(ibanAsegModificado.substr(20, 4));
	/* Pet. 70105 ** MODIF TAM (11/03/2021) * Fin */
 	
 	var iban2AsegOriginal = $('#iban2AsegOriginal').val();
 	console.log ("Valor de iban2AsegOriginal:" + iban2AsegOriginal);
 	
	var iban2AsegFormateado = iban2AsegOriginal.substr(0, 4)+" "+iban2AsegOriginal.substr(4, 4)+" "+iban2AsegOriginal.substr(8, 4)+" "+iban2AsegOriginal.substr(12, 4)+" "+iban2AsegOriginal.substr(16, 4)+" "+iban2AsegOriginal.substr(20, 4);
 	console.log ("Valor de iban2AsegFormateado:" + iban2AsegFormateado);
 	
	$('#iban2Antiguo').text(iban2AsegFormateado);
	
 	$('#divCambioIBAN').fadeIn('normal');
 	$('#cambioIBANPopUpError').hide();
 	
 	/* Pet. 70105 ** MODIF TAM (11/03/2021) */
 	/* RGA solicita que se visualice siempre el contenido de las cuentas modificadas, no solo en consulta */

	var iban2AsegModificado = $('#iban2AsegModificado').val();
	console.log ("Valor de iban2AsegModificado:"+iban2AsegModificado);
 		
	$('#iban2').val(iban2AsegModificado.substr(0, 4));
	$('#iban2_cuenta1').val(iban2AsegModificado.substr(4, 4));
	$('#iban2_cuenta2').val(iban2AsegModificado.substr(8, 4));
	$('#iban2_cuenta3').val(iban2AsegModificado.substr(12, 4));
	$('#iban2_cuenta4').val(iban2AsegModificado.substr(16, 4));
	$('#iban2_cuenta5').val(iban2AsegModificado.substr(20, 4));
	/* Pet. 70105 ** MODIF TAM (11/03/2021) * Fin */
 	
	$('#overlay').show(); 
 	
}

function cerrarPopUpCambioIBAN(){
	$('#divCambioIBAN').fadeOut('normal');
	$('#overlay').hide();
}

function cambiarIBAN(){
	//alert ("Dentro de cambiarIBAN [INIT]");
	var ibanAntiguo = $('#ibanAsegOriginal').val().replace(/ /g,'');
	var ibanNuevo = $('#iban').val() + $('#cuenta1').val()+ $('#cuenta2').val() + $('#cuenta3').val() + $('#cuenta4').val()+ $('#cuenta5').val();
	
	var iban2Antiguo = $('#iban2AsegOriginal').val().replace(/ /g,'');
	var iban2Nuevo = $('#iban2').val() + $('#iban2_cuenta1').val()+ $('#iban2_cuenta2').val() + $('#iban2_cuenta3').val()+ $('#iban2_cuenta4').val()+ $('#iban2_cuenta5').val();
	
	if (ibanAntiguo == ibanNuevo && iban2Antiguo == iban2Nuevo){
		//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer if");
		$('#cambioIBANPopUpError').html('IBAN Pago Prima e IBAN cuenta Cobro Siniestro coincidentes');
		$('#cambioIBANPopUpError').show();
		vaciarNuevoIBAN();
		vaciarNuevoIBAN2();
	}else if (ibanAntiguo!=ibanNuevo && iban2Antiguo!=iban2Nuevo){
		//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer else if");
	    //alert("Valor de IbanNuevo:-"+ibanNuevo+"-");
	    //alert("Valor de Iban2Nuevo:-"+iban2Nuevo+"-");
		
	    if (ibanNuevo != "" && iban2Nuevo !=""){
		   //alert ("if");	
		   //alert ("Validar IbanNuevo:"+validarCampoIBAN(ibanNuevo));
		   //alert ("Validar Iban2Nuevo:"+validarCampoIBAN(iban2Nuevo));
			if (!validarCampoIBAN(ibanNuevo) || !validarCampoIBAN(iban2Nuevo)){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
		}else if (ibanNuevo != "" && iban2Nuevo ==""){
			//alert ("else if(1)");
			//alert("Valor de validar:"+validarCampoIBAN(ibanNuevo));
			if (!validarCampoIBAN(ibanNuevo) ){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
			
		}else if (ibanNuevo == "" && iban2Nuevo !=""){
			//alert ("else if(2)");
			if (!validarCampoIBAN(iban2Nuevo) ){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
			
		}
	    //alert ("Continuamos");
		if (validarCampoIBAN(ibanNuevo) && validarCampoIBAN(iban2Nuevo)){
			//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer if (1)");
			$('#cambioIBANPopUpError').hide();
			if (confirm('�Seguro que desea cambiar los IBAN de la p�liza?')){
				$('#ibanCompleto').val(ibanNuevo);
				$('#iban2Completo').val(iban2Nuevo);
				/* Le pasamos el parametro P para que identificar que es el Iban de la p�liza */
				cambiarIbanAjax();
			}
		}else if(!validarCampoIBAN(ibanNuevo) && validarCampoIBAN(iban2Nuevo)){
			  if (ibanNuevo != ""){
				  if (!validarCampoIBAN(ibanNuevo)){
					   $('#cambioIBANPopUpError').html('IBAN incorrecto');
					   $('#cambioIBANPopUpError').show();   
				   }
			  }else{
				  if (iban2Nuevo != "" && validarCampoIBAN(iban2Nuevo)){
					if (confirm('�Seguro que desea cambiar los IBAN de la p�liza?')){
						$('#ibanCompleto').val(ibanNuevo);
						$('#iban2Completo').val(iban2Nuevo);
						/* Le pasamos el parametro P para que identificar que es el Iban de la p�liza */
						cambiarIbanAjax();
					}
				  }
			  } 
				
		}else{
			if(validarCampoIBAN(ibanNuevo) && !validarCampoIBAN(iban2Nuevo)){
				if (iban2Nuevo != ""){
					$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro incorrecto');
					$('#cambioIBANPopUpError').show();
				}else{
					if (confirm('�Seguro que desea cambiar los IBAN de la p�liza?')){
						$('#ibanCompleto').val(ibanNuevo);
						$('#iban2Completo').val(iban2Nuevo);
						/* Le pasamos el parametro P para que identificar que es el Iban de la p�liza */
						cambiarIbanAjax();
					}
				}
			}else{
				if (confirm('�Seguro que desea cambiar los IBAN de la p�liza?')){
					$('#ibanCompleto').val(ibanNuevo);
					$('#iban2Completo').val(iban2Nuevo);
					/* Le pasamos el parametro P para que identificar que es el Iban de la p�liza */
					cambiarIbanAjax();
				}
			}
		}
	}else{
	    //alert ("Entra en el else");
		if(ibanAntiguo!=ibanNuevo){
			if(validarCampoIBAN(ibanNuevo)){
				$('#cambioIBANPopUpError').hide();
				if (confirm('�Seguro que desea cambiar el IBAN de la p�liza?')){
					$('#ibanCompleto').val(ibanNuevo);
					/* Le pasamos el parametro P para que identificar que es el Iban de la p�liza */
					cambiarIbanAjax();
				}
			}else{
				if (ibanNuevo != ""){
					//alert("Error iban incorrecto 1");
					$('#cambioIBANPopUpError').html('IBAN incorrecto');
					$('#cambioIBANPopUpError').show();
				}
			}
		}else{
			if (ibanNuevo != ""){
				vaciarNuevoIBAN();
				$('#cambioIBANPopUpError').html('IBAN coincidente');
				$('#cambioIBANPopUpError').show();
			}
		}
				
		/* Validamos el Iban Cuenta Cobro Siniestro */
		//alert ("Valor de iban2Antiguo:-"+iban2Antiguo +"- comparamos con iban2Nuevo:-"+iban2Nuevo+"-");
		
		if(iban2Antiguo!=iban2Nuevo){
			if(validarCampoIBAN(iban2Nuevo)){
				$('#cambioIBANPopUpError').hide();
				if (confirm('�Seguro que desea cambiar el IBAN cuenta Cobro Siniestro de la p�liza?')){
					$('#iban2Completo').val(iban2Nuevo);
					//alert ("Valor de iban2Completo:"+$('#iban2Completo').val());
					/* Le pasamos el parametro 'S' para que identificar que es el Iban del Siniestro */
					cambiarIbanAjax();
				}
		
			}else{
				if(iban2Nuevo != '' ){
					$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro incorrecto');
					$('#cambioIBANPopUpError').show();
				}
			}
		}else{
			if (iban2Nuevo != ""){
				vaciarNuevoIBAN2();
				$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro coincidente');
				$('#cambioIBANPopUpError').show();
			}
		}
	}
	
	//alert ("Dentro de cambiarIBAN [END]");
}

function vaciarNuevoIBAN(){
	$('#iban').val('');
	$('#cuenta1').val('');
	$('#cuenta2').val('');
	$('#cuenta3').val('');
	$('#cuenta4').val('');
	$('#cuenta5').val('');
	$('#ibanCompleto').val('');
}

function vaciarNuevoIBAN2(){
	$('#iban2').val('');
	$('#iban2_cuenta1').val('');
	$('#iban2_cuenta2').val('');
	$('#iban2_cuenta3').val('');
	$('#iban2_cuenta4').val('');
	$('#iban2_cuenta5').val('');
	$('#iban2Completo').val('');
}

function cambiarIbanAjax(){

	var form = document.forms['main3'];
	var idAnexo = form['anexoModificacionId'].value;
	
	var nuevoIBAN = $('#ibanCompleto').val();
	var nuevoIBAN2 = $('#iban2Completo').val();
		
	$.ajax({
            url: "anexoModificacionUtilidades.run",
            data: "method=doCambiarIBAN&idAnexo="+idAnexo + "&nuevoIBAN="+nuevoIBAN + "&nuevoIBAN2="+nuevoIBAN2,
            async:true,
            cache: false,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al intentar cambiar el IBAN del anexo: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	if(datos.cambioIBANValido.valueOf() == "true"){
	            	$('#ibanAsegModificado').val(nuevoIBAN);
	            	$('#iban2AsegModificado').val(nuevoIBAN2);
	            	$('#panelMensajeValidacion').html('Cambio de IBAN realizado');
	            	$('#panelMensajeValidacion').show();
	             	$('#divCambioIBAN').fadeOut('normal');
	            	$('#overlay').hide();
            	}else{
            		$('#cambioIBANPopUpError').html('Se produjo un error al intentar cambiar el IBAN');
            		$('#cambioIBANPopUpError').show();
            	}
            },
            type: "POST"
        });
}

function subvenciones(){
	$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#frmSubvenciones').submit();
}