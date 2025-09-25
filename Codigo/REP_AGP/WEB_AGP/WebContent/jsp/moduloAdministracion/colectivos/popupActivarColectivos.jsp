<!--                                                     -->		
<!-- popupActivarColectivos.jsp (show in colectivos.jsp) -->
<!--                                                     -->

<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
<script><!--

$(document).ready(function(){  
	$(".panelActivarColectivo").draggable();	
});


function openPopupActivarColectivo(id,idcolectivo,cccCompleta){
	
	$('#refColectivoPopup').html(idcolectivo);
	// campo hidden del form de colectivos.jsp
	var frm = document.getElementById('main');
	frm.idColPopUp.value = id;
	
	
	if (frm.cuenta1.value!='' || frm.cuenta2.value!=''
			|| frm.cuenta3.value!=''
			|| frm.cuenta4.value!=''
			|| frm.cuenta5.value!=''){
		frm.ccc.value = cccCompleta;
	}else{
		frm.ccc.value = "";
	}
	
	$('#overlay').show();
	$('#panelActivarColectivo').show();
	
}

function cerrarPopupActivarColectivo(){
	$('#overlay').hide();
	$('#refColectivoPopup').html('');
	$('#panelActivarColectivo').hide();
}

function activarColectivo(){
	
	$('#overlay').hide();
	$('#panelActivarColectivo').hide();
	
	var frm = document.getElementById('main');
	frm.operacion.value = 'doActivarColectivo';
	
	$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#main').validate().cancelSubmit = true;
	$('#main').submit();
}

--></script>



<!-- Popup duplicar informe -->
	<div id="panelActivarColectivo" class="wrapper_popup">
		<!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Activar Colectivo</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				  <span onclick="cerrarPopupActivarColectivo()">x</span>
			</a>
		</div>
		
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacionPopUp" class="panelInformacion">
				 <div id="panelAlertasValidacion_pp" class="errorForm_cm" style="margin-top:3px;margin-bottom: 5px;"></div>
				
			 	Se va a activar el colectivo con referencia:&nbsp;<label id="refColectivoPopup" name="refColectivoPopup"> </label><br>
			 	 ¿Desea Continuar?
			 	
			</div>
		<div style="margin-top:15px">
		    <a class="bot" href="javascript:cerrarPopupActivarColectivo()" title="Cancelar">Cancelar</a>
		    <a class="bot" href="javascript:activarColectivo()" title="Aplicar">Aceptar</a>
		</div>
	</div>
</div>
	