<!--                                                     -->		
<!-- popupDuplicarInforme.jsp (show in mtoInformes.jsp) -->
<!--                                                     -->

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
<script><!--

$(document).ready(function(){  
	$(".panelduplicarInf").draggable();	
});


function openPopupDuplicar(idInforme){
	$('#idInforme').val(idInforme);
	$('#overlay').show();
	$('#panelduplicarInf').show();
	$('#tituloPopup').val('');
}

function cerrarPopupDuplicarInf(){
	$('#overlay').hide();
	$('#panelduplicarInf').hide();
	$('#tituloPopup').val('');
}

function duplicarInf(){
	
	if ($('#tituloPopup').val()!= ''){
		$('#panelAlertasValidacion_pp').html("");
		$('#panelAlertasValidacion_pp').hide();
		$('#tituloInfoDuplicado').val($('#tituloPopup').val());
		var frm = document.getElementById('main');
		frm.method.value = 'doDuplicar';
		$('#overlay').hide();
		$('#panelduplicarInf').hide();
		$('#main').validate().cancelSubmit = true;
		$('#main').submit();
		
	}else{
		$('#panelAlertasValidacion_pp').html("El Título es obligatorio");
		$('#panelAlertasValidacion_pp').show();
		
	}
}

--></script>



<!-- Popup duplicar informe -->
	<div id="panelduplicarInf" class="wrapper_popup">
		<!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Nuevo título para el informe</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				  <span onclick="cerrarPopupDuplicarInf()">x</span>
			</a>
		</div>
		
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacionPopUp" class="panelInformacion">
				 <div id="panelAlertasValidacion_pp" class="errorForm_cm" style="margin-top:3px;margin-bottom: 5px;"></div>
				
			 	Nombre <input type="text"  id="tituloPopup"  class="dato" size="30" maxlength="100" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
			</div>
		<div style="margin-top:15px">
		    <a class="bot" href="javascript:cerrarPopupDuplicarInf()" title="Cancelar">Cancelar</a>
		    <a class="bot" href="javascript:duplicarInf()" title="Aplicar">Aplicar</a>
		</div>
	</div>
</div>
	