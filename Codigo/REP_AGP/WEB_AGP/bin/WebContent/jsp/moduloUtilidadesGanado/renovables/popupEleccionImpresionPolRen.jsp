
<!-- Formulario para imprimir por WS la póliza renovable -->
<form name="printWs" id="printWs" action="polizasRenovables.run" method="post">
	<input type="hidden" name="method" id="method" value="getImpresionProrroga"/>
	<input type="hidden" name="planWs" id="planWs"/>
	<input type="hidden" name="referenciaWs" id="referenciaWs"/>
	<input type="hidden" name="valorWs" id="valorWs" />
</form>

<!-- Formulario para ver el estado actual de la poliza en Agroseguro -->
<form:form name="polizaActualizada" id="polizaActualizada" action="polizasRenovables.run" method="post">
	<input type="hidden" id="method" name="method" value="doImprimirCopy" />
	<input type="hidden" name="referenciaPol" id="referenciaPol"/>
	<input type="hidden" name="planPol" id="planPol"/>
</form:form>

<!--                                                     -->
<!-- 		*** popUp Elección de impresión Póliza renovable*** 		 -->
<!--                                                     -->

<div id="popUpImprimir" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; width: 350px; top: 165px;">
     <!--  header popup -->
	 <div id="header-popup" style="padding:0.4em 1em;width: 350px; position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div  style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Imprimir Comunicación
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpImprimir()">x</span>
		        </a>
	 </div>
	 <!--  body popup -->
	 <div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_aviso_3"></div>
			</div>
			<div style="margin-top:15px">
			
				<table width="100%" height="80%">
						<tr align="left" style="vertical-align: middle;">
							<td class="literal" style="padding-left: 95px;vertical-align: middle;" align="left" >
								  <input type="radio" name="selImpresion" id="selImpresion" checked="true" value="1" style="background-color:#e5e5e5">1ª Comunicación
							</td>
						</tr>
				</table>			
				
				<div id="impresionComDefinitiva" style="display:none">
				    <table width="100%" height="80%">								
						<tr align="left">
							<td class="literal" style="padding-left: 95px;vertical-align: middle;">
								  <input type="radio" name="selImpresion" id="selImpresion" value="2" style="background-color:#e5e5e5"> Comunicación definitiva 
						    </td>
						</tr>
				 	</table>
				 </div>
				 <div id="impresionPolCopy" style="display:none">
				 	<table width="100%" height="80%">
						<tr align="left">
							<td class="literal" style="padding-left: 95px;vertical-align: middle;">
								  <input type="radio" name="selImpresion" id="selImpresion" value="3" style="background-color:#e5e5e5"> Copy
						    </td>
						</tr>
					</table>
				
 				</div>
			  	<br>
				<div id="botones_abajo" >
				 	<table width="100%" height="80%">
				 		<tr align="left" style="background-color:#e5e5e5">
						 	<td class="literal" style="padding-left: 95px;vertical-align: middle;">
							  <a class="bot" id="btn_imprimirNormal" href="javascript:imprimirSeleccion()" title="Imprimir">Imprimir</a>
							  <a class="bot" id="btn_imprimirReducida" href="javascript:hidePopUpImprimir()" title="Cancelar">Cancelar</a>
					   		</td>
						</tr>	
					</table>			 
				</div>
 			</div>
	</div>
</div>





