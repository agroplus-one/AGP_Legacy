<form name="frmVerErroresEnvioIBAN" action="polizasRenovables.run" method="post" id="frmVerErroresEnvioIBAN">
	<input type="hidden" name="method" id="method" value="doVerErroresValidacionEnvioIBAN"> 
	<input type="hidden" name="idErroresIBAN" id="idErroresIBAN" value="${requestScope.idErroresIBAN}">
	<input type="hidden" name="lstErroresEnvioIBAN" id="lstErroresEnvioIBAN" value="${lstErroresEnvioIBAN}" />	
</form>	

<form name="frmRenEnvioIBAN" action="polizasRenovables.run" method="post" id="frmRenEnvioIBAN">
	<input type="hidden" name="method" id="method" value="doValidarEnvioIBAN"> 
	<input type="hidden" name="idPlzEnvioIBAN" id="idPlzEnvioIBAN" value="">
	<input type="hidden" name="fecCargaIni_e" value="">
	<input type="hidden" name="fecCargaFin_e" value="">
	<input type="hidden" name="fecRenoIni_e" value="">
	<input type="hidden" name="fecRenoFin_e" value="">
	<input type="hidden" name="fecEnvioIBANIni_e" value="">
	<input type="hidden" name="fecEnvioIBANFin_e" value="">
	<input type="hidden" name="estadoRenovacionAgroplus_e" value="">
	<input type="hidden" name="polRenGrupoNegocio_e" value="">
	
	<input type="hidden" name="mostrarResultadoEnvioIBAN" value="${requestScope.mostrarResultadoEnvioIBAN}">	
	
	<!--  popup Renovables Envio IBAN -->
	<div id="divRenEnvioIBAN" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 30%; display: none; top: 270px; left: 35%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Elegir operación a realizar</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpEnvioIBAN()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacionIBAN" class="panelInformacion">
					<div id="panelAlertasValidacion_envioIBAN" name="panelAlertasValidacion_envioIBAN" class="errorForm_fp" align="center"></div>
						<table style="width:90%">
							<tr>
							   <table width="90%" height="80%">
										<tr align="left" style="vertical-align: middle;">
											<td class="literal" style="padding-left: 70px;vertical-align: middle;" align="left" >
												  <input type="radio" name="selEnvioIBAN" id="selEnvioIBAN1" checked="true" value="1" style="background-color:#e5e5e5"> Marcar para enviar IBAN
											</td>
										</tr>
								</table>
								
							</tr>
							
							<tr>
								<table width="90%" height="80%">								
									<tr align="left">
										<td class="literal" style="padding-left: 70px;vertical-align: middle;">
											  <input type="radio" name="selEnvioIBAN" id="selEnvioIBAN2" value="2" style="background-color:#e5e5e5"> Desmarcar para no enviar IBAN 
									    </td>
									</tr>
							 	</table>
							</tr>
						</table>
					</div>
				</div>
				
				<!-- Botones popup --> 
			    <div style="margin-top:5px; margin-bottom:5px;" align="center">
					<a class="bot" href="javascript:continuarEnvioIBAN()">Aceptar</a>
					<a class="bot" href="javascript:cerrarPopUpEnvioIBAN()">Cancelar</a>
				</div>
			   
			</div>

</form>
<form name="frmRenResEnvioIBAN" action="polizasRenovables.run" method="post" id="frmRenResEnvioIBAN">
	<input type="hidden" name="method" id="method" value="doModificarEstadoEnvioIBAN">
	<input type="hidden" name="idsResPlzEnvioIBAN" id="idPlzEnvioIBAN" value="${listaIds}">
	<input type="hidden" name="marcadasIBAN" value="${marcadasIBAN}">
	<input type="hidden" name="correctasIBAN" value="${correctasIBAN}">   
	<input type="hidden" name="erroneasIBAN" value="${erroneasIBAN}">
	<input type="hidden" name="idErroresIBAN" value="${idErroresIBAN}">
	<input type="hidden" name="seleccionEnvioIBAN" value="${requestScope.seleccionEnvioIBAN}">
    <input type="hidden" name="fecCargaIni_res" value="">
	<input type="hidden" name="fecCargaFin_res" value="">
	<input type="hidden" name="fecRenoIni_res" value="">
	<input type="hidden" name="fecRenoFin_res" value="">
	<input type="hidden" name="fecEnvioIBANIni_res" value="">
	<input type="hidden" name="fecEnvioIBANFin_res" value="">
	<input type="hidden" name="estadoRenovacionAgroplus_res" value="">
	<input type="hidden" name="polRenGrupoNegocio_res" value="">

	<!--  popup Renovables Resultado Envio IBAN -->
	<div id="divRenResEnvioIBAN" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
			width: 30%; display: none; top: 270px; left: 35%;  
                  position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
	
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
				font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Resultado de la validación</div>
			
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
				top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUpEnvioIBAN()">x</span>
			</a>
		</div>
	
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
			<div id="panelAlertasValidacion_res_envioIBAN" name="panelAlertasValidacion_res_envioIBAN" class="errorForm_fp" align="center"></div>
				<c:if test="${requestScope.erroneasIBAN != null && requestScope.erroneasIBAN != '0'}">
					<table style="width:80%" border=0 align="center">
				</c:if>
				<c:if test="${requestScope.erroneasIBAN == null || requestScope.erroneasIBAN == '0'}">
					<table style="width:35%" border=0 align="center">
				</c:if>	
					<tr style="vertical-align: middle;">
						<td  align="right" class="literal" >Pólizas marcadas:</td>
						<td class="detalI" align ="left"> ${requestScope.marcadasIBAN}</td>
					
					</tr>
					<tr  style="vertical-align: middle;">
						<td align="right" class="literal" >Pólizas correctas:</td>
						<td class="detalI"  align ="left"> ${requestScope.correctasIBAN}</td>
						
					</tr>
					<tr style="vertical-align: middle; ">
						<td   align="right" class="literal" >Pólizas erróneas:</td>
						<td class="detalI"  align ="left"> ${requestScope.erroneasIBAN}
						<c:if test="${requestScope.erroneasIBAN != null && requestScope.erroneasIBAN != '0'}">
							<a class="detalI" href="javascript:verErroresEnvioIBAN()">(<span style='color:red'><nobr>Ver Errores</nobr></span>)</a>	
						</c:if>	
						</td>
					</tr>
				</table>
			</div>
		</div>
		
		<!-- Botones popup --> 
	    <div style="margin-top:5px" align="center">
	    	<c:if test="${requestScope.erroneasIBAN != null && requestScope.erroneasIBAN == '0'}"> 
				<a class="bot" href="javascript:continuarResEnvioIBAN()">Enviar</a>
			</c:if>	
			<a class="bot" href="javascript:cerrarPopUpEnvioIBAN()">Cancelar</a>
		</div>
	   
	</div>

</form>
	
	
	