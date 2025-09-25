<!--                                     -->		
<!--         popup recalcular            -->
<!--                                     -->
<div id="popupRecalcular" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	<!--  header popup -->
	<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
		            Aviso
		        </div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="$('#popupRecalcular').hide();$('#overlay').hide();">x</span>
		        </a>
	</div>
	<!--  body popup -->
	<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="sms">¿Desea recalcular precio y producción para las parcelas? </div>
				</div>
				<div style="margin-top:15px">
				    <a class="bot" href="#" onclick="$('#popupRecalcular').hide();$('#overlay').hide();return false;" title="Cancelar">Cancelar</a>
				    <a class="bot" href="javascript:continuar('si')" title="Si">Si</a>
				    <a class="bot" href="javascript:continuar('no')" title="No">No</a>
				</div>
	</div>
</div>