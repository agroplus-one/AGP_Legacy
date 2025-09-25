<!-- Popup importacion poliza [Inicio] -->

<form id="frmImportacionPoliza" name="frmImportacionPoliza" action="" method="post" >
	
	
	<div id="panelImportacionPoliza" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
		width: 35%; display: none; top: 270px; left: 33%; 
       position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">	

		<!--  Se añaden cambios por resolución de Incidencias -->
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Importación póliza</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cancelar_ip()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion_ip" class="panelInformacion">
				<fieldset style="border:0px">
					<div id="panelAlertasValidacion_ip" class="errorForm_fp" style="width:98%; height:25%"></div>
				</fieldset>
				
					<table style="width:100%">
						<tr>
							<td class="literal" align="right">Plan:</td>
							<td>
								<input type="text" name="plan_ip" size="6" class="dato" id="plan_ip" maxlength="4"/>
							</td>
							<td class="literal" align="right">Póliza:</td>
							<td>
								<input type="text" name="poliza_ip" size="10" class="dato" id="poliza_ip" maxlength="8" onchange="this.value=this.value.toUpperCase();"/>
							</td>
						</tr>
						<tr>
							<td class="literal" align="right">Usuario:</td>
							<td>
							<!--
								<input type="text" name="usuario_ip" size="10" class="dato" id="usuario_ip" maxlength="8"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Usuario','principio', '', '');" alt="Buscar Usuarios" title="Buscar Usuarios" />
							-->
								<input id="usuario_ip" name="usuario_ip" size="10" maxlength="8" class="dato" onchange="this.value=this.value.toUpperCase();"/>
								<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('UsuarioFiltrosRenovables','principio', '', '');" alt="Buscar Usuario"/>
							</td>
							
							<td class="literal" align="right">Tipo referencia:</td>
							<td>
								<input type="text" name="tipoRefPoliza_ip" size="4" class="dato" id="tipoRefPoliza_ip" maxlength="1" onchange="this.value=this.value.toUpperCase();"/>
							</td>
							
						</tr>
							
					</table>
				
			</div>
			
			
		</div>
	    
	    <!-- Botones popup --> 
	    <div style="margin-top:15px;margin-bottom:2px;" align="center">
			<a class="bot" id="btnAplicar_ip" href="javascript:aplicar_ip()">Aplicar</a> 
			<a class="bot" id="btnCancelar_ip" href="javascript:cancelar_ip()">Cancelar</a>	
		</div>
	</div>

</form>
<!-- Popup importacion poliza [Fin] -->