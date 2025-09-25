<!-- Popup datos aval [Inicio] -->
<form id="frmDatosAval" name="frmDatosAval" action="" method="post" >
	<input type="hidden" id="methodToDo" name="method"/>
	<input type="hidden" id="idpoliza" name="idpoliza" value="${idpoliza}"/>	
	<input type="hidden" name="validComps" 	id="validComps" value="${validComps}" /> 
	
	<div id="panelDatosAval_Gral" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 35%; display: none; top: 270px; left: 33%; 
       position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">	

		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Datos del Aval</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cancelar_da()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<fieldset style="border:0px">
					<div id="panelAlertasValidacion_da" class="errorForm_fp"></div>
				</fieldset>
				<div id="panelDatosAval">
					<table style="width:90%">
						<tr>
							<td class="literal" align="right">Número:</td>
							<td>
								<input type="text" name="numero_da" size="6" class="dato" id="numero_da" maxlength="6"/>
							</td>
							<td class="literal" align="right">Importe:</td>
							<td>
								<input type="text" name="importe_da" size="14" class="dato" id="importe_da" maxlength="14"/>
							</td>
						</tr>
					</table>
				</div>
			</div>
			
			
			<!-- variables necesarias para volver a la página de importes desde la pantalla de forma de pago -->		
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" />
		<input type="hidden" name="grProvisional" id="grProvisional" value="true" />
		<input type="hidden" name="origenllamada" id="origenllamada" value="pago" />
		<input type="hidden" name="vieneDeImportes" id="vieneDeImportesComparativas" value="true"/>
		<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}" />
		<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}" />
		<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}" />
		<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}" />
		<input type="hidden" name="grProvisionalOK_da" id="grProvisionalOK_da" value="${grProvisionalOK}" />
		<input type="hidden" name="muestraBotonFinanciar_da" id="muestraBotonFinanciar_da" value=""/>
		<input type="hidden"   name="isFechaEnvioPosteriorSep2020" id="isFechaEnvioPosteriorSep2020" value="${isFechaEnvioPosteriorSep2020}" />
		</div>
	    <!-- Botones popup --> 
	    <div style="margin-top:15px;margin-bottom:2px;" align="center">
			<a class="bot" id="btnAceptar_da" href="javascript:validar_da()">Aceptar</a> 
			<a class="bot" id="btnCancelar_da" href="javascript:cancelar_da()">Cancelar</a>	
		</div>
	</div>

</form>
<!-- Popup datos aval [Fin] -->