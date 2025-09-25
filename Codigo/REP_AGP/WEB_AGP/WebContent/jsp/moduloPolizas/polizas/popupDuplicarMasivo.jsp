<!--                                                       -->
<!-- popupDuplicarMasivo.jsp (show in listadoparcelas.jsp) -->
<!--                                                       -->

<script>
function duplicar(){
     if($('#sel').text() >1){ // hay más de una parcela marcada
     	showPopUpAviso("Debe seleccionar como máximo una parcela.");
	 }else if($('#sel').text() >0){
	 	limpiarAlertasDuplicar();
  	    $('#duplicar_popup').fadeIn('normal');
  	    $('#overlay').show();
	 }else{ // no hay ninguna parcela marcada
	 	showPopUpAviso("Debe seleccionar como mínimo una parcela.");
	 }
		  	 
}

function limpiarAlertasDuplicar(){
	$('#campoObligatorio_numDuplicar').hide();
	$('#duplicar_popup_error').hide();
	$('#duplicarMax_popup_error').hide();
}

function cerrarPopUpDuplicar(){
     $('#duplicar_popup').fadeOut('normal');
     $('#overlay').hide();
}
 
function validarCampo(){
	if ($('#numDuplicar').val() != ''){
	 	var numDuplicarOk = "false";
	 	try {
	 		isValid = /^[0-9|.]+$/i.test($('#numDuplicar').val());
			if (isValid){
		 		var auxNumDuplicar =  parseFloat($('#numDuplicar').val());
		 		if(!isNaN(auxNumDuplicar)){
		 			if(auxNumDuplicar >50){
						numDuplicarOk = "maxError";
					}else{
						numDuplicarOk = "true";
					}
				}
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!numDuplicarOk) {
			return numDuplicarOk;
		}
	}
	return numDuplicarOk;
}

function continuarDuplicar(){
	limpiarAlertasDuplicar();
	var res = validarCampo();
	if(res == "true"){ // todo correcto
		  var frm = document.getElementById("frmDuplicarMasivo");
		  //var checked_parce_cm = 
		  //frm.checked_form_parcela_cm.value = getRowsChecks();
		  frm.method.value                  = "doDuplicarMasivo";
		  frm.idpolizaDM.value   			= $('#idpoliza').val();
		  frm.idsRowsCheckedDM.value		= $('#idsRowsChecked').val();
		  frm.tipoListadoGridDM.value		= $('#tipoListadoGrid').val();
		  frm.cantDuplicar.value			= $('#numDuplicar').val();
		  frm.submit(); 
		
	}else if(res == "false"){
		$('#campoObligatorio_numDuplicar').show();
	    $('#duplicar_popup_error').show();
	    $('#numDuplicar').val('');
	}else if(res == "maxError"){
		$('#campoObligatorio_numDuplicar').show();
	    $('#duplicarMax_popup_error').show();
	    $('#numDuplicar').val('');
	} 
}

</script> 

<!--  popup Duplicar Parcela -->

<form id="frmDuplicarMasivo" name="frmDuplicarMasivo" action="cambioMasivo.html" method="post" >
		<input type="hidden" name="idsRowsCheckedDM"           id="idsRowsCheckedDM"             />
        <input type="hidden" name="tipoListadoGridDM"          id="tipoListadoGridDM" />
        <input type="hidden" name="method"           		   id="method" />
        <input type="hidden" name="cantDuplicar"           	   id="cantDuplicar" />
		<input type="hidden" name="idpolizaDM"                 id="idpolizaDM" />
</form>
<div id="duplicar_popup" class="wrapper_popup">
    <div class="header-popup">
        <div class="title_popup">Duplicar parcela</div>
        <a class="close_botton_popup"><span onclick="cerrarPopUpDuplicar()">x</span></a>
    </div>
    <div id="duplicar_popup_error" class="literal" style="color:red;display:none;text-align:center">
       El campo solo puede contener números
    </div>
    <div id="duplicarMax_popup_error" class="literal" style="color:red;display:none;text-align:center">
       El número máximo permitido es 50
    </div>
	<div class="content_popup">
	    <table>
	        <tr>
	            <td class="literal">¿Cuántas veces desea duplicar la parcela?</td>
	            <td>
	            	<input id="numDuplicar" name="numDuplicar" class="dato" onchange="" type="text" value="" size="4" maxlength="4" />
	            	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numDuplicar"> *</label>
	            </td>
	        </tr>
	    </table>
		<div style="margin-top:15px">
		    <a class="bot" href="javascript:cerrarPopUpDuplicar()">Cancelar</a>
		    <a class="bot" href="javascript:continuarDuplicar()">Duplicar </a>
		</div>
	</div>
</div>