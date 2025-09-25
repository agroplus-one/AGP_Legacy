<html>
	<head>
		<form id="frmConfirmacion" name="frmConfirmacion" action="pagoPoliza.html" method="post">

			
			<div id="panelConfirmacion" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 40%; display: none; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" class="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpConfirmacion()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				   <div class="panelInformacion_content">
      					<div id="panelInformacion" class="panelInformacion" >

							<p>El usuario <i>${codUsuario} - ${nomUsuario}</i> confirma que ha entregado la siguiente documentación 
							al cliente <i>${nifcifAsegurado} - ${nomAsegurado}</i> y ha recibido firmado conforme:</p>

						 <span style="display: inline-block; width:120px; text-align: left;">
				            <input type="checkbox" id="notaPreviaId" value="notaPrevia" onclick="compruebaChecks();"> Nota Previa <br/>
				            <input type="checkbox" id="IPIDId" value="IPID" onclick="compruebaChecks();"> IPID <br/>
				            <input type="checkbox" id="RGPDId" value="RGPD" onclick="compruebaChecks();"> RGPD
				        </span>
					
					</div>
				</div>
			    <!-- Botones popup --> 
			   
			    <div style="margin-top:15px; padding-bottom: 13px" align="center" class="cerrarPopUpX" id="cerrarPopUp">
			        <a class="bot" href="javascript:cerrarPopUpConfirmacion()" title="Cancelar">Cancelar</a>
			        <a id="botonEnviarId" style="display: none" class="bot" href="javascript:cerrarPopUpConfirmacion();confirmaSW();" title="Enviar">Enviar</a>
				</div>

			</div>
		</form>
	</head>
</html>
