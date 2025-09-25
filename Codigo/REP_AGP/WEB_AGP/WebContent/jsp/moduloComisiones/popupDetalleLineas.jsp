<!--                                                     -->
<!-- popupDetalleLineas.jsp (show in comisionesCultivos.jsp) -->
<!--                                                     -->


<script type="text/javascript">
<!--
$(document).ready(function(){
	
	$("#panelDetalleLineas").draggable();
	// limpiamos alertas
	$('#panelAlertasValidacion_fp').html("");
	$('#panelAlertasValidacion_fp').hide();
	
});
function cerrarPopUpDetalleLineas(){
	     
	// limpiamos alertas
	$('#panelAlertasValidacion_fp').html("");
	$('#panelAlertasValidacion_fp').hide();
	
	$('#panelDetalleLineas').hide();
    $('#overlay').hide();
}



//-->
</script>
<html>
	<head>
		<form id="frmDetalleLineas" name="frmDetalleLineas" action="" method="post" >
			
			<input type="hidden" id="listDetallePct" name="listDetallePct" value ="${listDetallePct}"/>
			
			<div id="panelDetalleLineas" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;
					width: 40%; display: none; width: 40%; top: 270px; left: 33%; 
                    position: absolute; z-index: 1003;border:1px solid #A4A4A4;background-color:#F2F2F2">
			
				<!--  header popup -->
				<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; 
						font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px" >
					<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Detalle de porcentajes</div>
					
					<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; 
						top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
						<span onclick="cerrarPopUpDetalleLineas()">x</span>
					</a>
				</div>
			
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
						<div id="panelAlertasValidacion_fp" name="panelAlertasValidacion_fp" class="errorForm_fp" align="center"></div>
							<fieldset style="width:95%;">
							
								<table style="width:90%">
									<tr>
										<td align="center" class="literal">E-S Mediadora 
											
											<label id="esMedP"  class="dato"></label>
										</td>
									</tr>
									<tr></tr>
									<tr></tr>
								</table>	
								
								<!-- 
								<table style="width:90%;border-collapse:collapse;" class="LISTA"> 
										<tr> 
											<th class="cblistaImg"> Plan </th> 
											<th class="cblistaImg"> Línea </th> 
											<th class="cblistaImg"> G.N. </th> 
											<th class="cblistaImg"> % Entidades </th> 
											<th class="cblistaImg"> % E-S Mediadora </th> 
										</tr> 
								 -->
										<label id="datosPop"  class="dato"></label>
									
										
										
								<!-- 		
								</table>	
								 -->
								
							</fieldset>
						</div>
					
					</div>
					<!-- Botones popup --> 
			    <div style="margin-top:15px;margin-bottom:5px;" align="center">
			      
					   <a class="bot" href="javascript:cerrarPopUpDetalleLineas()" title="Cancelar">Cerrar</a>
					
				</div>
				</div>
			    
			
		</form>
		
	</head>
</html>