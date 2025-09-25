
<!-- DAA 28/05/2013                                             -->
<!-- popupDetalleRecibos.jsp 								 	-->
<!-- incluir "jsp/moduloComisiones/informesRecibosHTML.js" 		-->


	<!--  popup Detalle de recibos -->
	
	<div id="divPopupDetalleRecibos" class="parcelasRepWindow" style="top: 2%; left: 2%; color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;z-index:1005">
		<!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px;">
				Detalle Informe de Recibos
			</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopupDetalleRecibos()">x</span>
			</a>
		</div>
		
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion" >
			
				<!-- ********************************** TABLA DETALLE *************************************** -->
				<table border="1" id="tablaDetalle"></table>
				
				<div style="margin-top:30px; clear: both">
				    <a class="bot" href="javascript:cerrarPopupDetalleRecibos()">Cerrar</a>
				</div>
			</div>
		</div>
	</div>

