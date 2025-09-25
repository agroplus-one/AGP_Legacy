<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<!-- POPUP REPLICAR PLAN  -->
<div id="divReplicarPlan" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
	<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
		<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			Introduzca el plan de destino
		</div>
		<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			<span onclick="cerrarPopUpReplicarPlan()">x</span>
		</a>
		</div>
		<div class="panelInformacion_content">
		<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
			Plan Destino: <input type="text" size="5" maxlength="4" class="dato" id="plandestino" tabindex="2"/>
		</div>	
		<div style="margin-top:15px;clear: both">
		    <a class="bot" href="javascript:cerrarPopUpReplicarPlan()">Cancelar</a>
		    <a class="bot" href="javascript:doReplicarPlan()">Replicar Plan</a>
		</div>
	</div>
</div>

<script>
	function doReplicarPlan(){
		// Se comprueba que el plan a replicar no es vacíos antes de llamar al servidor
		var plandestino = $('#plandestino').val();
		$('#planreplica').val(plandestino);
		
		if ($('#planreplica').val() != '') {
			if(confirm('¿Desea replicar todos los registros para este Plan ?')){
				// Valida que el plan/linea origen y destino no son iguales
				if (planDiferentes()) {
					cerrarPopUpReplicarPlan();
					var frm = document.getElementById('main3');
					frm.method.value= "doReplicar";
					$('#main3').validate().cancelSubmit = true;
					$("#main3").submit();			
				} else {
					cerrarPopUpReplicarPlan();
					$('#panelAlertasValidacion').html("El plan origen no puede ser igual que el destino");
					$('#panelAlertasValidacion').show();
				}
			}
		}
	}
	
	function planDiferentes () {	
		if ($('#planreplica').val() == $('#codplan').val()) return false
		else return true;
	}
	
	function showPopUpReplicarPlan(){
		if($('#codplan').val() == ''){
			$('#panelAlertasValidacion').html("El plan origen no puede ser nulo");
			$('#panelAlertasValidacion').show();
		} else {
			$('#divReplicarPlan').fadeIn('normal');
			$('#overlay').show();
		}

	}
	
	function cerrarPopUpReplicarPlan(){
		$('#divReplicarPlan').fadeOut('normal');
		$('#overlay').hide();
	}
</script>