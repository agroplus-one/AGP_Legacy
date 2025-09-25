<form name="frmRenovablesMasivo" action="polizasRenovables.run" method="post" id="frmRenovablesMasivo">
	<input type="hidden" name="method" id="method" value="doCambioMasivo"> 
	<input type="hidden" name="idPlz" id="idPlz" value="">
	<input type="hidden" name="fecCargaIni_cm" value="">
	<input type="hidden" name="fecCargaFin_cm" value="">
	<input type="hidden" name="fecRenoIni_cm" value="">
	<input type="hidden" name="fecRenoFin_cm" value="">
	<input type="hidden" name="fecEnvioIBANIni_cm" value="">
	<input type="hidden" name="fecEnvioIBANFin_cm" value="">
	<input type="hidden" name="estadoRenovacionAgroplus_cm" value="">
	<input type="hidden" name="polRenGrupoNegocio_cm" value="">			
	<input type="hidden" name="isPerfil0" id ="isPerfil0">	
	<!--  popup Restaurar Params -->
	<div id="divRenovablesMasivo" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 30%; display: none; top: 270px; left: 35%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div id="cambioMasivoTitulo" style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Asignar comisiones</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpGastosMasivo()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion_M" class="panelInformacion">
					<div id="panelAlertasValidacion_d" name="panelAlertasValidacion_d" class="errorForm_fp" align="center"></div>
						<table style="width:90%">
							<tr>
							    <td class="literal" align="center">% Comisi&oacute;n:
									<input type="text" name="comisionMasivo" id="comisionMasivo" size="6" maxlength="6" class="dato" value="" onchange="this.value = this.value.replace(',', '.')"/>
									
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<!-- Botones popup --> 
			    <div style="margin-top:15px; margin-bottom:5px" align="center">
					<a class="bot" href="javascript:actualizarPorcentajeComisiones()">Aplicar</a>
					<a class="bot" href="javascript:cerrarPopUpGastosMasivo()">Cancelar</a>
				</div>
			   
			</div>

</form>