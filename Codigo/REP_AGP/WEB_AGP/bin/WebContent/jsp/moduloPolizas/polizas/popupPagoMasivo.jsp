
<!-- DAA 28/05/2013                                             -->
<!-- popupPagoMasivo.jsp (show in cambiopolizasdefinitivas.jsp) -->
<!-- incluir "jsp/moduloPolizas/polizas/pagoMasivo.js" 			-->

<c:if test="${perfil == 0}">
	<form action="cambioMasivoPolizas.html" method="post" name="frmPagoMasivo" id="frmPagoMasivo">
		
		<input type="hidden" name="fechapago.day" id="fechapago.day" value=""/>
		<input type="hidden" name="fechapago.month" id="fechapago.month" value=""/> 
		<input type="hidden" name="fechapago.year" id="fechapago.year" value=""/>
		<input type="hidden" name="method" id="method" value="doPagoMasivo"/>
		<input type="hidden" name="listaIds" id="listaIds" value=""/>
		
		<!--  popup Pago Masivo de Polizas -->
		<div id="divPagoMasivo" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;z-index:1005">
			<!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					Pago Masivo de Pólizas
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					<span onclick="cerrarPopUpPagoMasivo()">x</span>
				</a>
			</div>
			
			<!--  body popup -->
			<div class="panelInformacion_content">
				<!-- DAA 01/07/2013 Txt Validacion fecha -->
				<div style="height:30px">
					<div id="txt_val_fecha" class="errorForm_cm"></div>
				</div>
				<div id="panelInformacion" class="panelInformacion">
					<table>
						<tr>
							<td class="literal">
								<input type="radio" name="pagoMasivo" id="pagoMasivoM" value="S" checked="checked"> Marcar como pagada
							</td>
							<td class="literal">
								<input type="radio" name="pagoMasivo" id="pagoMasivoD" value="N"> Desmarcar como pagada
							</td>
						</tr>
						
						<tr>
							<td class="literal">Fecha de Pago:
								<input type="text" name="fechapago" id="fechapago" size="11" maxlength="10" class="dato"
									onchange="if (!ComprobarFecha(this, document.frmPagoMasivo, 'Fecha pago')) this.value='';"
									value="<fmt:formatDate pattern='dd/MM/yyyy' value='${fechapago}' />" />
								<input type="button" id="btn_fechapago" name="btn_fechapago" class="miniCalendario" style="cursor: pointer;" />
							</td>
							<td>&nbsp;</td>
						</tr>
					</table>
					
					<div style="margin-top:30px; clear: both">
					    <a class="bot" href="javascript:cerrarPopUpPagoMasivo()">Cancelar</a>
						<a class="bot" href="javascript:doPagoMasivo()">Pago Masivo</a>
					</div>
				</div>
			</div>
		</div>
	
	</form>
</c:if>