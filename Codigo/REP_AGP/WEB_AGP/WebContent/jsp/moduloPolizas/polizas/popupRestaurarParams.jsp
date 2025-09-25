<!-- AMG 29/10/2014 -->
<!-- popupRestaurarParams.jsp (show in cambiopolizasdefinitivas.jsp) -->
<!-- incluir "jsp/moduloPolizas/polizas/restaurarParams.js" 		 -->


<form name="frmRestaurarParams" action="revisionComisiones.html" method="post" id=frmRestaurarParams>
	<input type="hidden" name="method" id="method" value="doRestaurarParams"> 
	<input type="hidden" name="idPlz" id="idPlz" value="">
	
	<!--  popup Restaurar Params -->
	<div id="divRestaurarParams" class="parcelasRepWindow" style="color: #333333; left: 3%; width: 95%; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; z-index: 1005">
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Datos de Comisiones</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpRestaurarParams()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div>
			<div id="panelInformacion">
				<div id="txt_mensaje_res" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>
				<div id="txt_validacionesRes" name="txt_validacionesRes" style="color:red" class="errorFormRes"></div>
			
				<div style="panel2 isrt">
					<fieldset style="width:100%;" align="center">
						<legend class="literal">Parametrización</legend>
						<table align="center" style="width:100%;" border="0">
							<tr>
								<label id="datosPop" class="dato"></label>
							</tr>
						</table>
					</fieldset>
				</div>	

					<fieldset style="width:100%;" align="center">
						<legend class="literal">Datos de la póliza</legend>
						<table align="center" style="width:100%;" border="0">
							<tr>
								<label id="datosPctPol" class="dato"></label>
							</tr>
						</table>
					</fieldset>
					
				<input type="hidden" id="pctDescRecColectivo" name="pctDescRecColectivo" size="4" maxlength="6" class="dato"/>
				<input type="hidden" id="numeroPoliza" class="numeroPoliza" name="numeroPoliza" size="4" maxlength="6" />
				
			</div>
		</div>
		<!-- Botones popup --> 
	    <div style="margin-top:15px" align="center">
			<a class="bot" href="javascript:actualizarParams()">Actualizar</a>
		</div>
		
		
	</div>

</form>