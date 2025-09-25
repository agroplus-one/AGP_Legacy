<form name="frmAltaPolizaRen" action="polizasRenovables.run" method="post" id="frmAltaPolizaRen">
	<input type="hidden" name="method" id="method" value="doAltaPolizaRenovable">
	<!--  popup Restaurar Params -->
	
	<input type="hidden" name="idPlzEnvioIBAN" id="idPlzEnvioIBAN" value="">
	<input type="hidden" name="fecCargaIni_e" value="">
	<input type="hidden" name="fecCargaFin_e" value="">
	<input type="hidden" name="fecRenoIni_e" value="">
	<input type="hidden" name="fecRenoFin_e" value="">
	<input type="hidden" name="fecEnvioIBANIni_e" value="">
	<input type="hidden" name="fecEnvioIBANFin_e" value="">
	<input type="hidden" name="estadoRenovacionAgroplus_e" value="">
	<input type="hidden" name="polRenGrupoNegocio_e" value="">
	
	
	<div id="divAltaPolizaRenovable" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 36%; display: none; top: 270px; left: 35%; 
                 position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
				font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div id="AltaPolizaRenTitulo" style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Alta Póliza Renovable</div>
			
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpAltaRenovable()">x</span>
			</a>
		</div>
		
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacionPolRenov" class="panelInformacion">
			<div id="panelAlertasValidacion_altaPolizaRen" name="panelAlertasValidacion_altaPolizaRen" class="errorForm_fp" align="center" style="width:98%; height:25%" text-align="center"></div>
				<table style="width:90%">
					<tr>
					   <table width="90%" height="80%">
						   	<!--  -->
						   	<tr>
								<td class="literal" width="10%" align="left">Plan</td>
								<td class="literal" style="padding:0.5em 1em" width="25%" align="left">
									<input cssClass="dato" name="plan_renov" size="5" maxlength="4" id="plan_renov" tabindex="1" onchange="javascript:validarcampos() ;javascript:lupas.limpiarCampos('linea_renov', 'desc_linea_renov');" />
								</td>
								<td class="literal" width="10%" align="left">Línea</td>
								<td class="literal" nowrap style="padding:0.5em 1em" width="40%">
									<input cssClass="dato" name="linea_renov" size="3" maxlength="3" id="linea_renov" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_linea_renov'); javascript:validarcampos()"/>
									<input cssClass="dato" name="desc_linea_renov" id="desc_linea_renov" style="width:90%"  readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaAltaRen','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								</td>
							</tr>
							<tr>
								<td class="literal" width="10%">P&oacute;liza </td>
								<td class="literal" style="padding:0.5em 1em" width="25%" align="left">
									<input name="referencia_renov" id="referencia_renov" size="9" maxlength="9" cssClass="dato" onchange="this.value = this.value.toUpperCase(); javascript:validarcampos()"/>
									<label class="campoObligatorio" id="campoObligatorio_referencia" title="Campo obligatorio"> *</label>
								</td>
							</tr>		
						</table>
					</tr>
				</table>
			</div>
		</div>
	
		<!-- Botones popup --> 
	    <div style="margin-top:15px; margin-bottom:5px" align="center">
			<a class="bot" id="btnAplicar_ren" href="javascript:doAtaPolizaRenovable()">Aplicar</a>
			<a class="bot" id="btnCancelar_ip" href="javascript:cerrarPopUpAltaRenovable()">Cancelar</a>
		</div>
	   
	</div>
</form>

<script>
	
	function validarcampos(){
		if ($('#plan_renov').val() != '' &  $('#linea_renov').val() != '' && $('#referencia_renov').val() != '') {
			$('#panelAlertasValidacion_altaPolizaRen').hide();
		}
		
	}

</script>		