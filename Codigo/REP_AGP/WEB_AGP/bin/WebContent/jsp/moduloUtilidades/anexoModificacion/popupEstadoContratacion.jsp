
<!-- Utilizado en la pantalla de declaraciones de modificación de una póliza, en el alta de AM por WS -->

<script>
			
$(document).ready(function(){  
    $(".panelEstadoContratacion").draggable();	
    
    // Oculta las partes del popup con visibilidad condicionada a los datos
    $("#tr_estadoCpl").hide();
    $("#siModPrev").hide();
    $("#tablaModPrev").hide();
    $("#trModPrevPpal").hide();
    $("#trModPrevCpl").hide();
});
            
</script>




<form id="frmEstadoContratacion" name="frmEstadoContratacion" action="cambioMasivo.html" method="post" >
	
	
<div id="panelEstadoContratacion" class="panelEstadoContratacion" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">



     <!--  header popup -->
	<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Estado de la contratación</div>
	</div>
	
	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">		 
			<!-- Estado de la póliza en Agroseguro -->
			<fieldset>
				<legend class="literal">Estado de la <span id="idMsgPoliza">Póliza</span> en Agroseguro</legend>	
				<table>
					<!-- Principal -->
					<tr>
						<td class="literal">Principal:</td><td class="detalI" id="estadoPpal"></td>
					</tr>
					<!-- Complementaria -->
					<tr id="tr_estadoCpl">
						<td class="literal">Complementaria:</td><td class="detalI" id="estadoCpl"></td>
					</tr>
				</table>
			</fieldset>
			&nbsp;
			<!-- Modificaciones previas -->
			<fieldset>
				<legend class="literal">Modificaciones previas</legend>	
				<span class="detalI" id="noModPrev">No constan modificaciones previas</span>
				<span class="detalI" id="siModPrev">Existen modificaciones previas</span>
				<table id="tablaModPrev">
					<tr>
						<td class="literal">&nbsp;</td>
						<td class="detalI"></td>
						<td class="detalI"></td>
					</tr>
					<!-- Principal -->
					<tr id="trModPrevPpal">
						<td class="literal">Principal:</td>
						<td class="detalI" id="tdModPrevPpalCupon"></td>
						<td class="detalI" id="tdModPrevPpalEstado"></td>
					</tr>
					<!-- Complementaria -->
					<tr id="trModPrevCpl">
						<td class="literal">Complementaria:</td>
						<td class="detalI" id="tdModPrevCplCupon"></td>
						<td class="detalI" id="tdModPrevCplEstado"></td>
					</tr>
				</table>
			</fieldset>
			
			<!-- Aviso -->
			<p align="left" class="detalI">Cualquier modificaci&oacute;n que se env&iacute;e sobre una p&oacute;liza y sea aceptada total o parcialmente,
			anula cualquier modificaci&oacute;n anterior sobre la misma p&oacute;liza que se encuentre en revisi&oacute;n administrativa.</p>
			<p align="left" class="detalI">Si existe una solicitud de modificaci&oacute;n por otra v&iacute;a (fax, tel&eacute;fono, etc.) que no sea
			por cup&oacute;n, no quedar&aacute; reflejado en este cuadro, teniendo la misma afectaci&oacute;n en caso de estar en revisi&oacute;n
			administrativa.</p>
			<p align="left" class="detalI">¿Desea continuar con el alta del Anexo?</p>
		</div>		    
			    
	
		<div style="margin-top:15px">
				    <a class="bot" href="javascript:continuarAltaAM()" title="Aceptar">Aceptar</a>
				    <a class="bot" href="javascript:cancelarCupon();" title="Cancelar">Cancelar</a>
		</div>
	
	</div>
</div>

</form>









