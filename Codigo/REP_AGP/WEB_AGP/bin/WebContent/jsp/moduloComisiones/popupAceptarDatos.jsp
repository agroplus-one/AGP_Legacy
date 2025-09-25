<script type="text/javascript">

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
        inputField        : "fechaAceptacionFichero",
        button            : "btn_fechaAceptacionFichero",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"
        
    });
});
	
function openPopupAceptarDatos(){
	
	$('#panelAlertasValidacion_d').html("");
	$('#panelAlertasValidacion_d').hide();
	
	$('#panelFechaAceptacion').css('width','35%');
	$('#overlay').show();
	$('#panelFechaAceptacion').show();
	
	$("#panelFechaAceptacion").draggable();
	
}
function closePopupAceptarDatos(){
	// limpiamos alertas
	$('#panelAlertasValidacion_d').html("");
	$('#panelAlertasValidacion_d').hide();
	$('#panelFechaAceptacion').hide();
	$('#overlay').hide();
}

function aplicarFechaAceptacion(){

	if(validaCampo()){
		cargar();// Hace el submit
	}	
}

function limpiarAlertas (){
	$('#panelAlertasValidacion_d').html("");
	$('#panelAlertasValidacion_d').hide();
	$('#panelFechaAceptacion').hide();
	$('#overlay').hide();
}
function validaCampo(){
	
	var valido = true;
	if($('#fechaAceptacionFichero').val()==""){
		$('#panelAlertasValidacion_d').html("La fecha de aceptación es obligatoria");
		$('#panelAlertasValidacion_d').show();
		valido = false;
	}
	return valido;
}
</script>

<div id="panelFechaAceptacion" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 60%; display: none; top: 270px; left: 33%; 
                 position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">

	<!--  header popup -->
	<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
			font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
		<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Fecha de aceptación</div>
		
		<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
			top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
			<span onclick="closePopupAceptarDatos()">x</span>
		</a>
	</div>
	
	<input type="hidden" name="fechaAceptacionFichero.day" value="">
	<input type="hidden" name="fechaAceptacionFichero.month" value="">
	<input type="hidden" name="fechaAceptacionFichero.year" value="">
	
	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
		<div id="panelAlertasValidacion_d" name="panelAlertasValidacion_d" class="errorForm_fp" align="center"></div>
			<table style="width:90%">
				<tr>
				    <td class="literal" align="center">Fecha de aceptación del fichero:
		           		<input type="text" name="fechaAceptacionFichero" id="fechaAceptacionFichero" size="11" maxlength="10" tabindex="8" class="dato"
		           			onchange="if (!ComprobarFecha(this, document.main3, 'Fecha de aceptación del fichero')) this.value='';"
		           		 	value="" />
						<input type="button" id="btn_fechaAceptacionFichero" name="btn_fechaAceptacionFichero" class="miniCalendario" style="cursor: pointer;" /> 
						<label class="campoObligatorio" id="campoObligatorio_fechaAceptacionFichero" title="Campo obligatorio"> *</label>
					</td>
				</tr>
			</table>
		</div>
	</div>
    <!-- Botones popup --> 
    <div style="margin-top:15px" align="center">
        <a class="bot" href="javascript:closePopupAceptarDatos()" title="Cancelar">Cancelar</a>
		<a class="bot" href="javascript:aplicarFechaAceptacion();" title="Aplicar">Aplicar</a>
	</div>
</div>