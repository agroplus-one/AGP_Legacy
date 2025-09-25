
<!-- 
<form:form name="frmImportar" id="frmImportar" action="tasasSbp.run" method="post" commandName="tasaSbpBean" enctype="multipart/form-data">

	<input type="hidden" name="method" id="methodImportar" value ="doImportar"/>	

	<div id="divImportarTasas" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
		<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					Importar tasas de Sobreprecio
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					<span onclick="cerrarPopUpImportarTasas()">x</span>
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
					    <a class="bot" href="javascript:cerrarPopUpImportarTasas()">Cancelar</a>
					    <a class="bot" href="javascript:doImportar()">Importar</a>
				</div>
		</div>
	</div>
</form:form>
 -->
<form:form name="frmImportarCsv" id="frmImportarCsv" method="post" enctype="multipart/form-data" action="" commandName="">
	<input type="hidden" name="mensajeEsperaImportarCsv" id="mensajeEsperaImportarCsv"  value="" />
	<input type="hidden" name="method" id="methodImportarCsv" value =""/>
	<input type="hidden" name="actionImportarCsv" id="actionImportarCsv" value =""/>
	
	<div id="divImportarCsv" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
		<div id="headerPopupImportarCsv" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
				<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" id="divTituloImportraCsv" name="divTituloImportraCsv">
					Importar Fichero CSV <!-- Le asignamos el valor desde donde cargamos el popUp -->
				</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					<span onclick="cerrarPopUpImportarCsv()">x</span>
				</a>
		</div>
		<div class="panelInformacion_content">
				<div style="height:30px">
					<div id="panelAlertasValidacionImportarCsv" name="panelAlertasValidacionImportarCsv" class="errorForm_cm" ></div>
				</div>
				<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
					<input type="file" class="dato" id="fileImportarCsv" name="fileImportarCsv" onchange="javascript:limpiaPanelAlertasImportarCsv();"/>
				</div>	
				<div style="margin-top:15px;clear: both">
					    <a class="bot" href="javascript:cerrarPopUpImportarCsv()">Cancelar</a>
					    <a class="bot" href="javascript:doImportarCsv()">Importar</a>
				</div>
		</div>
	</div>
</form:form>

<script type="text/javascript">
  
  function cerrarPopUpImportarCsv(){
		$('#divImportarCsv').fadeOut('normal');
		$('#overlay').hide();
  }
  
  function doImportarCsv(){
	  limpiaPanelAlertasImportarCsv();
		if ($('#frmImportarCsv').valid()){
			cerrarPopUpImportarCsv();
			var frm = document.getElementById('frmImportarCsv');
			frm.method.value = frm.methodImportarCsv.value;
			frm.action = frm.actionImportarCsv.value;
			var tipofichero=frm.mensajeEsperaImportarCsv.value;			
			$.blockUI.defaults.message = '<h4> Importando archivo&nbsp' + tipofichero + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			frm.submit();			
		}
  }
  
  function limpiaPanelAlertasImportarCsv(){
		$("#fileImportarCsv").val('');
		$("#panelAlertasValidacionImportarCsv").hide();
		/* $("#panelInformacion").hide();
		$("#panelAlertasValidacion").hide();
		$("#panelAlertas").hide(); */
	}
  
  function showPopUpImportarCsv(actionForm, methodForm, mensajeEspera, titulo, commandName){
	  	$('#divTituloImportraCsv').html(titulo);
	  	var frm = document.getElementById('frmImportarCsv');
	  	frm.mensajeEsperaImportarCsv.value=mensajeEspera;
	  	frm.methodImportarCsv.value =methodForm;
	  	frm.actionImportarCsv.value =actionForm;
	  	frm.command=commandName;
		$('#divImportarCsv').fadeIn('normal');
		$('#overlay').show();
  }
  
</script>
