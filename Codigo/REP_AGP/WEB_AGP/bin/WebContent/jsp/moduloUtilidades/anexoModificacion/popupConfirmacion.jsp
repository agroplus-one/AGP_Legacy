
<!-- Utilizado en la pantalla de errores de validación de AM por SW -->

<script>
			
$(document).ready(function(){  
    $(".panelConfirmacion").draggable();	
});
            
</script>
<div id="panelConfirmacion" class="panelConfirmacion" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">


	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">		 					
			
			<!-- Aviso -->
			<p align="left" class="detalI">
			El cup&oacute;n contiene errores de tr&aacute;mite de un intento anterior, se puede:
			</p>
			<p align="left" class="detalI">
			- Intentar confirmar el cup&oacute;n de nuevo, verificando si contiene errores, o
			</p>
			<p align="left" class="detalI">
			- Forzar el env&iacute;o a Agroseguro quedando la modificaci&oacute;n en estado de Revisi&oacute;n Administrativa.
			<span id="msgPerfil34">Esta opci&oacute;n s&oacute;lo pueden realizar los servicios centrales.</span>
			</p>
		</div>		    
			    
	
		<div style="margin-top:15px">
				    <a class="bot" href="javascript:intentarConfirmar()" title="Intentar Confirmar">Intentar Confirmar</a>
				    <a class="bot" href="javascript:forzarConfirmacion();" title="Forzar Confirmaci&oacute;n" id="forzar">Forzar Confirmaci&oacute;n</a>
		</div>
	
	</div>
</div>
