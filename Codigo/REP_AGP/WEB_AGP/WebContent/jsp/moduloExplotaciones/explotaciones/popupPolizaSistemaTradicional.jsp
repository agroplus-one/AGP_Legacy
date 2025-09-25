<!-- Popup Sistema Tradicional [Inicio] -->
<form id="frmSistemaTradicional" name="frmSistemaTradicional" action="cargaExplotaciones.html" method="doCargaPolizaSistemaTradicional" >
	<input type="hidden" id="methodToDo" name="method" value="doCargaPolizaSistemaTradicional" /> 
	<!-- <input type="hidden" name="method" id="methodVolver"/> -->
	<input type="hidden" id="idpoliza_SistTrad" name="idpoliza_SistTrad" value="${idpoliza}"/>	
	
	<div id="panelSistemaTradicional" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 35%; display: none; top: 270px; left: 33%; 
       position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">	

		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Datos de la Póliza del Sistema Tradicional</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cancelar_SistTrad()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<fieldset style="border:0px">
					<div id="panelAlertasValidacion_SistTrad" class="errorForm_fp"></div>
				</fieldset>
				<div id="panelSistemaTradicional">
					<table style="width:50%">
						<tr align="left">
							<td class="literal">Plan:</td>
							<td>
								<input type="text" name="plan_SistTrad" size="6" class="dato" id="plan_SistTrad" maxlength="4" value="${planAnterior}" />	
							</td>
						</tr>
						<tr>
							<td class="literal">Referencia:</td>
							<td>
								<input type="text" name="referencia_SistTrad" size="14" class="dato" id="referencia_SistTrad" maxlength="7"/>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	    <!-- Botones popup --> 
	    <div style="margin-top:15px;margin-bottom:2px;" align="center">
			<a class="bot" id="btnContinuar_SistTrad" href="javascript:validar_SistTrad()">Continuar</a> 
			<a class="bot" id="btnCancelar_SistTrad" href="javascript:cancelar_SistTrad()">Cancelar</a>	
		</div>
	</div>
</form>
<!-- Popup Sistema Tradicional l [Fin] -->