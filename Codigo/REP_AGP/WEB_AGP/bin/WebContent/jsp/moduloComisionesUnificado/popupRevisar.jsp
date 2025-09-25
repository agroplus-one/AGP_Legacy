<script type="text/javascript">
	
function openPopupRevisar() {
	
	$('#panelAlertasValidacion_r').html('');
	$('#panelAlertasValidacion_r').hide();
	
	$('#panelRevision').css('width','35%');
	$('#overlay').show();
	$('#panelRevision').show();
	
	$("#panelRevision").draggable();
	
}
function closePopupRevisar() {
	// limpiamos alertas
	$('#panelAlertasValidacion_r').html('');
	$('#panelAlertasValidacion_r').hide();
	$('#panelRevision').hide();
	$('#overlay').hide();
}

function limpiarAlertas () {
	$('#panelAlertasValidacion_r').html('');
	$('#panelAlertasValidacion_r').hide();
	$('#panelRevision').hide();
	$('#overlay').hide();
}

function aplicarRevisar() {

	if(validarCampos()) {
		$('#idEstadoRevision').val($('input[name=idEstadoRevisionRadio]:checked').val());
		$('#method').val('doRevisarMultiple');
		closePopupRevisar();
		$.blockUI.defaults.message = '<h4> Realizando acciones.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').submit();
	}	
}

function validarCampos() {
	
	var valido = true;
	
	if(!$('input[name=idEstadoRevisionRadio]').is(':checked')) {
		$('#panelAlertasValidacion_r').html("Debe seleccionar la acción a realizar");
		$('#panelAlertasValidacion_r').show();
		valido = false;
	} else if ($('#listaIdsMarcados').val() == '') {
		$('#panelAlertasValidacion_r').html("Debe seleccionar una o varias incidencias");
		$('#panelAlertasValidacion_r').show();
		valido = false;
	}
	return valido;
}
</script>

<div id="panelRevision" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 60%; display: none; top: 270px; left: 33%; 
                 position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">

	<!--  header popup -->
	<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
			font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
		<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Revisar incidencias</div>
		
		<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
			top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
			<span onclick="closePopupRevisar()">x</span>
		</a>
	</div>
	
	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
		<div id="panelAlertasValidacion_r" name="panelAlertasValidacion_r" class="errorForm_fp" align="center"></div>
			<table style="width:90%">
				<tr>
				    <td class="literal" align="center">
				    	<input type="radio" name="idEstadoRevisionRadio" value="R" /> Marcar revisada
				    	&nbsp;&nbsp;&nbsp;
				    	<input type="radio" name="idEstadoRevisionRadio" value="A"/> Desmarcar como revisada
				    </td>
				</tr>
			</table>
		</div>
	</div>
    <!-- Botones popup --> 
    <div style="margin-top:15px" align="center">
        <a class="bot" href="javascript:closePopupRevisar()" title="Cancelar">Cancelar</a>
		<a class="bot" href="javascript:aplicarRevisar()" title="Aceptar">Aceptar</a>
	</div>
</div>