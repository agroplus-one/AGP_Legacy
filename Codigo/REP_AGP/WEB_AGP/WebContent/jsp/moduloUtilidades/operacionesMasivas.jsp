
	<div id="popUpMasivosUtilidades" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 515px; top: 165px;">
	     <!--  header popup -->
		 <div id="header-popupMU" style="padding:0.4em 0.4em; position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:1px 1px 1px 1px;background:#525583;height:15px">
			        <div  style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Elige acción a realizar
			        </div>
			        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.1em;top:50%;width:25px;
			                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			            <span onclick="hidePopMasivosUtilidades()">x</span>
			        </a>
		 </div>
	 <!--  body popup -->
	 <div class="panelInformacion_content" style="width: 100%; padding: 0;">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_aviso_3" style="width:100%;height:20px;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8"></div>
			</div>
				
					<table width="100%">
						<tr>
						<td >
							<fieldset style="height:100%">
								<legend class="literal">Masivo</legend>
								<table>
									<tr>
										<td class="detalI">
											<input type="radio" name="selCambio" id="selMasivoBorrado" value="1" class="dato"> Borrado 
										</td>
									</tr>
									<c:if test="${perfil == 0}">
										<tr>
											<td class="detalI">
												<input type="radio" name="selCambio" id="selMasivoPago" value="2" class="dato"> Pago  
											</td>
										</tr>
									</c:if>
								</table>
							</fieldset>
						</td>
						<td>
							<c:if test="${perfil == 0 || perfil == 1 || perfil == 5}">
								<fieldset style="height:100%">
									<legend class="literal">Cambiar</legend>
									<table >
										<tr>
											<td class="detalI">
												<c:if test="${externo == 0}">
													<input type="radio" name="selCambio" id="selMasivoOficina" value="4" class="dato" > Oficina
												</c:if>
											</td>
											<td class="detalI" >
												<c:if test="${perfil == 0}">
													<input type="radio" name="selCambio" id="selMasivoClase" value="5" class="dato" > Clase
												</c:if> 
											</td>
											<td class="detalI" >												
												<c:if test="${perfil == 0}">
													<input type="radio" name="selCambio" id="selMasivoModulo" value="8" class="dato" > M&oacute;dulo
												</c:if> 
											</td>	
										</tr>
										<tr>
											<td class="detalI" >
												<input type="radio" name="selCambio" id="selMasivoUsuario" value="6" class="dato" > Usuario
											</td>										
											<td class="detalI">
												<c:if test="${perfil == 0}">
													<input type="radio" name="selCambio" id="selMasivoTitular" value="7" class="dato" > Titular
												</c:if> 
											</td>											
										</tr>
										<tr>
																					
										</tr>										
									</table>
								</fieldset>
							</c:if>
						</td>
						
						<td >
							<fieldset style="height:100%">
								<legend class="literal">Informes</legend>
								<table>
									<tr>
										<td class="detalI">
											<input type="radio" name="selCambio" id="selInformeDetalle" value="9" class="dato"> Detalle póliza 
										</td>
									</tr>
								
										<tr>
											<td class="detalI">
												<input type="radio" name="selCambio" id="selInformeDetalleSitAct" value="10" class="dato"> Detalle sit. act.  
											</td>
										</tr>
								
								</table>
							</fieldset>
						</td>
						
						</tr>
					</table>
					
				
					<div id="botones_abajo" width="100%" style="margin-top:10px">
					 	<table width="100%">
					 		<tr style="background-color:#e5e5e5">
							 	<td class="literal" align="center" style="margin:0 auto; display:table; text-align:center;">
								  <a class="bot" id="btn_AceptarCM" href="javascript:aceptarPopMasivosUtilidades()" title="Aceptar">Aceptar</a>
								  <a class="bot" id="btn_CancelarCM" href="javascript:hidePopMasivosUtilidades()" title="Cancelar">Cancelar</a>
						   		</td>
							</tr>	
						</table>			 
				   </div>
 			</div> 
		</div>
