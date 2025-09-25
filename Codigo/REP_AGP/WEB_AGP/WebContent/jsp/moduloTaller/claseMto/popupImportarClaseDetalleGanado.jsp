<form:form name="frmImportar" id="frmImportar" action="claseDetalleGanado.run" method="post" commandName="claseDetalleGanadoBean" enctype="multipart/form-data">
<input type="hidden" name="method" id="methodImportar" value ="doImportar"/>	
  <form:hidden path="clase.id" id="detalleid_Importar" />
  <form:hidden path="clase.linea.lineaseguroid" id="lineaseguroid__Importar"/>
  <form:hidden path="clase.clase" id="clase__Importar"/>
  <form:hidden path="clase.linea.codplan" id="lineacodplan__Importar"/>
  <form:hidden path="clase.linea.codlinea" id="lineacodlinea__Importar"/>
  <form:hidden path="clase.descripcion" id="descripcion__Importar"/>
<div id="divImportar" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
		<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					Importar clases de detalle de ganado
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					<span onclick="cerrarPopUpImportar()">x</span>
				</a>
		</div>
		<div class="panelInformacion_content">
				<div style="height:30px">
					<div id="panelAlertasValidacionFile" name="panelAlertasValidacionFile" class="errorForm_cm" ></div>
				</div>
				<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
					<input type="file" class="dato" id="file" name="file" onchange="javascript:limpiaPanelAlertasValidacionFile();"/>
				</div>	
				<div style="margin-top:15px;clear: both">
						    <a class="bot" href="javascript:cerrarPopUpImportar()">Cancelar</a>
						    <a class="bot" href="javascript:doImportar()">Importar</a>
					</div>
			</div>
	</div>
</form:form>