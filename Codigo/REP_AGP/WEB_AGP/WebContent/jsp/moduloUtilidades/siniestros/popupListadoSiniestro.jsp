<!--                                                     -->
<!-- popupInformacionSiniestro.jsp (show in declaracionesSiniestro.jsp) -->
<!--                                                     -->

<script type="text/javascript">
<!--
$(document).ready(function(){

	$('#panelInformacionSiniestro').draggable();	
});
function cerrarPopUpInformacionSiniestros(){

	$('#panelInformacionSiniestro').hide();
    $('#overlay').hide();
}
//-->
</script>
<html>
	<head>
		<form id="frmInformacionSiniestro" name="frmInformacionSiniestro" action="" method="post" >
			
			<input type="hidden" id="listaSiniestros" name="listaSiniestros" value ="${listaS}"/>
			
			<div id="panelInformacionSiniestro" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 40%; display: none; width: 40%; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Información del siniestro</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpInformacionSiniestros()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content" style="padding:15px;">
					<div id="panelInformacion" class="panelInformacion">
						<fieldset style="width:95%;">	
							
								<div id="datosPop"  class="dato"></div>
								
						</fieldset>
					</div>
				</div>
				<!-- Botones popup --> 
				<div style="margin-top:15px" align="center"> 
					<a class="bot" href="javascript:cerrarPopUpInformacionSiniestros()" title="Cancelar">Cerrar</a>
				</div>
			</div>    
		</form>
	</head>
</html>